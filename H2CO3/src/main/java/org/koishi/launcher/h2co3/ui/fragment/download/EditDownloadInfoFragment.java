package org.koishi.launcher.h2co3.ui.fragment.download;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.adapter.RemoteVersionListAdapter;
import org.koishi.launcher.h2co3.core.H2CO3Settings;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.game.H2CO3CacheRepository;
import org.koishi.launcher.h2co3.core.game.download.CacheRepository;
import org.koishi.launcher.h2co3.core.game.download.DefaultDependencyManager;
import org.koishi.launcher.h2co3.core.game.download.DownloadProviders;
import org.koishi.launcher.h2co3.core.game.download.GameBuilder;
import org.koishi.launcher.h2co3.core.game.download.H2CO3GameRepository;
import org.koishi.launcher.h2co3.core.game.download.LibraryAnalyzer;
import org.koishi.launcher.h2co3.core.game.download.RemoteVersion;
import org.koishi.launcher.h2co3.core.game.download.VersionList;
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3.core.utils.task.Schedulers;
import org.koishi.launcher.h2co3.core.utils.task.Task;
import org.koishi.launcher.h2co3.core.utils.task.TaskExecutor;
import org.koishi.launcher.h2co3.core.utils.task.TaskListener;
import org.koishi.launcher.h2co3.dialog.H2CO3DownloadTaskDialog;
import org.koishi.launcher.h2co3.resources.component.H2CO3LinearProgress;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3CustomViewDialog;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MessageDialog;
import org.koishi.launcher.h2co3.ui.fragment.H2CO3Fragment;
import org.koishi.launcher.h2co3.utils.download.InstallerItem;
import org.koishi.launcher.h2co3.utils.download.TaskCancellationAction;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EditDownloadInfoFragment extends H2CO3Fragment {

    private final Map<String, RemoteVersion> map = new HashMap<>();
    private final H2CO3Settings gameHelper;
    private View view;
    private TextInputEditText versionNameEditText;
    private AppCompatImageButton backButton, downloadButton;
    private NestedScrollView installerScrollView;
    private InstallerItem.InstallerItemGroup group;
    private String gameVersion;
    private boolean isChooseInstallerVersionDialogShowing;
    private RemoteVersionListAdapter.OnRemoteVersionSelectListener listener;
    private RecyclerView installerVersionListView;
    private VersionList<?> currentVersionList;
    private DownloadProviders downloadProviders;
    private AlertDialog chooseInstallerVersionDialogAlert;
    private H2CO3DownloadTaskDialog taskListPane;
    private final MinecraftVersionListFragment minecraftVersionListFragment;
    private AlertDialog taskListPaneAlert;
    private final Bundle args;
    private H2CO3LinearProgress progressBar;

    public EditDownloadInfoFragment(MinecraftVersionListFragment minecraftVersionListFragment, Bundle bundle) {
        super();
        this.minecraftVersionListFragment = minecraftVersionListFragment;
        this.args = bundle;
        this.gameHelper = new H2CO3Settings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_download_edit_version, container, false);
        initView();
        downloadProviders = new DownloadProviders(gameHelper);

        showLoadingIndicator();
        loadInitialData();

        backButton.setOnClickListener(v -> navigateBack());
        downloadButton.setOnClickListener(v -> startDownload());

        return view;
    }

    private void initView() {
        versionNameEditText = view.findViewById(R.id.version_name_edit);
        backButton = view.findViewById(R.id.minecraft_back_button);
        downloadButton = view.findViewById(R.id.minecraft_download_button);
        installerScrollView = view.findViewById(R.id.installer_list_layout);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void loadInitialData() {
        Schedulers.io().execute(() -> {
            Optional.ofNullable(args).ifPresent(bundle -> {
                gameVersion = bundle.getString("versionName");
                runOnUiThread(() -> versionNameEditText.setText(gameVersion));
            });

            group = new InstallerItem.InstallerItemGroup(getContext(), gameVersion);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                installerScrollView.addView(group.getView());
                hideLoadingIndicator();
                setupLibraryActions();
            }, 600);
        });
    }

    private void showLoadingIndicator() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoadingIndicator() {
        progressBar.setVisibility(View.GONE);
    }

    private void navigateBack() {
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(org.koishi.launcher.h2co3.library.R.anim.fragment_enter, org.koishi.launcher.h2co3.library.R.anim.fragment_exit, org.koishi.launcher.h2co3.library.R.anim.fragment_enter_pop, org.koishi.launcher.h2co3.library.R.anim.fragment_exit_pop)
                .remove(EditDownloadInfoFragment.this)
                .show(minecraftVersionListFragment)
                .commit();
    }

    private void setupLibraryActions() {
        for (InstallerItem library : group.getLibraries()) {
            String libraryId = library.getLibraryId();
            if ("game".equals(libraryId)) continue;

            library.action.set(() -> handleLibraryAction(libraryId, library));
            library.removeAction.set(() -> {
                map.remove(libraryId);
                reload();
            });
        }
    }

    private void handleLibraryAction(String libraryId, InstallerItem library) {
        if (LibraryAnalyzer.LibraryType.FABRIC_API.getPatchId().equals(libraryId)) {
            showFabricApiWarning();
        }

        if (library.incompatibleLibraryName.get() == null) {
            currentVersionList = downloadProviders.getDownloadProvider().getVersionListById(libraryId);
            if (!isChooseInstallerVersionDialogShowing) {
                showChooseInstallerVersionDialog(libraryId);
            }
            listener = remoteVersion -> {
                map.put(libraryId, remoteVersion);
                reload();
                chooseInstallerVersionDialogAlert.dismiss();
            };
        }
    }

    private void showFabricApiWarning() {
        new H2CO3MessageDialog(getContext())
                .setCancelable(false)
                .setMessage(requireContext().getString(org.koishi.launcher.h2co3.library.R.string.install_installer_fabric_api_warning))
                .setNegativeButton(requireContext().getString(org.koishi.launcher.h2co3.library.R.string.button_cancel), null)
                .create().show();
    }

    private void startDownload() {
        String versionName = Optional.ofNullable(versionNameEditText.getText())
                .map(CharSequence::toString)
                .orElse("");
        H2CO3CacheRepository cacheRepository = H2CO3CacheRepository.REPOSITORY;
        CacheRepository.setInstance(cacheRepository);
        cacheRepository.setDirectory(H2CO3Tools.CACHE_DIR);

        DefaultDependencyManager dependencyManager = new DefaultDependencyManager(new H2CO3GameRepository(new File(gameHelper.getGameDirectory())), downloadProviders.getDownloadProvider(), cacheRepository);
        GameBuilder builder = dependencyManager.gameBuilder();

        builder.name(versionName).gameVersion(gameVersion);
        String minecraftPatchId = LibraryAnalyzer.LibraryType.MINECRAFT.getPatchId();
        map.forEach((key, value) -> {
            if (!minecraftPatchId.equals(key)) {
                builder.version(value);
            }
        });

        Task<?> task = builder.buildAsync();
        taskListPane = new H2CO3DownloadTaskDialog(requireContext(), org.koishi.launcher.h2co3.library.R.style.ThemeOverlay_App_MaterialAlertDialog_FullScreen);
        taskListPaneAlert = taskListPane.create();
        taskListPane.setAlertDialog(taskListPaneAlert);
        taskListPane.setCancel(new TaskCancellationAction(taskListPaneAlert::dismiss));
        taskListPaneAlert.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        taskListPaneAlert.show();

        Schedulers.androidUIThread().execute(() -> {
            TaskExecutor executor = task.executor(new TaskListener() {
                @Override
                public void onStop(boolean success, TaskExecutor executor) {
                    Schedulers.androidUIThread().execute(() -> {
                        taskListPaneAlert.dismiss();
                        if (success) {
                            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.INFO, "Download success");
                        } else if (executor.getException() != null) {
                            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, String.valueOf(executor.getException()));
                        }
                    });
                }
            });
            taskListPane.setExecutor(executor, true);
            taskListPaneAlert.show();
            executor.start();
        });
    }

    private void showChooseInstallerVersionDialog(String libId) {
        isChooseInstallerVersionDialogShowing = true;

        H2CO3CustomViewDialog chooseInstallerVersionDialog = new H2CO3CustomViewDialog(requireActivity());
        chooseInstallerVersionDialog.setCustomView(R.layout.dialog_installer_version);
        chooseInstallerVersionDialog.setCancelable(false);
        chooseInstallerVersionDialog.setTitle(org.koishi.launcher.h2co3.library.R.string.dialog_title_choose_installer_version);
        chooseInstallerVersionDialog.setNegativeButton(org.koishi.launcher.h2co3.library.R.string.button_cancel, (dialog, which) -> chooseInstallerVersionDialogAlert.dismiss());
        installerVersionListView = chooseInstallerVersionDialog.findViewById(R.id.list_left);

        chooseInstallerVersionDialogAlert = chooseInstallerVersionDialog.create();
        chooseInstallerVersionDialogAlert.setCancelable(false);
        chooseInstallerVersionDialogAlert.show();
        chooseInstallerVersionDialog.setOnDismissListener(dialog -> isChooseInstallerVersionDialogShowing = false);
        chooseInstallerVersionDialogAlert.setOnDismissListener(dialog -> isChooseInstallerVersionDialogShowing = false);
        refreshList(libId);
    }

    private List<RemoteVersion> loadVersions(String libraryId) {
        return downloadProviders.getDownloadProvider().getVersionListById(libraryId)
                .getVersions(gameVersion)
                .stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public void refreshList(String libraryId) {
        installerVersionListView.setVisibility(View.GONE);
        currentVersionList.refreshAsync(gameVersion).whenComplete((result, exception) -> {
            if (exception == null) {
                List<RemoteVersion> items = loadVersions(libraryId);
                Schedulers.androidUIThread().execute(() -> {
                    if (items.isEmpty()) {
                        H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, "No version found for " + libraryId + "!");
                        chooseInstallerVersionDialogAlert.dismiss();
                        installerVersionListView.setVisibility(View.GONE);
                    } else {
                        RemoteVersionListAdapter adapter = new RemoteVersionListAdapter(getContext(), items, listener);
                        installerVersionListView.setLayoutManager(new LinearLayoutManager(getContext()));
                        installerVersionListView.setAdapter(adapter);
                        installerVersionListView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private String getVersion(String id) {
        return Optional.ofNullable(map.get(id)).map(RemoteVersion::getSelfVersion).orElse(null);
    }

    protected void reload() {
        for (InstallerItem library : group.getLibraries()) {
            String libraryId = library.getLibraryId();
            if (map.containsKey(libraryId)) {
                library.libraryVersion.set(getVersion(libraryId));
                library.removable.set(true);
            } else {
                library.libraryVersion.set(null);
                library.removable.set(false);
            }
        }
    }
}