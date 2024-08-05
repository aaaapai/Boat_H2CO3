package org.koishi.launcher.h2co3.ui.fragment.download;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.widget.NestedScrollView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.adapter.RemoteVersionListAdapter;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.download.CacheRepository;
import org.koishi.launcher.h2co3.core.download.DefaultDependencyManager;
import org.koishi.launcher.h2co3.core.download.DownloadProviders;
import org.koishi.launcher.h2co3.core.download.GameBuilder;
import org.koishi.launcher.h2co3.core.download.H2CO3GameRepository;
import org.koishi.launcher.h2co3.core.download.LibraryAnalyzer;
import org.koishi.launcher.h2co3.core.download.RemoteVersion;
import org.koishi.launcher.h2co3.core.download.VersionList;
import org.koishi.launcher.h2co3.core.game.H2CO3CacheRepository;
import org.koishi.launcher.h2co3.core.h2co3launcher.utils.H2CO3GameHelper;
import org.koishi.launcher.h2co3.core.utils.task.Schedulers;
import org.koishi.launcher.h2co3.core.utils.task.Task;
import org.koishi.launcher.h2co3.core.utils.task.TaskExecutor;
import org.koishi.launcher.h2co3.core.utils.task.TaskListener;
import org.koishi.launcher.h2co3.dialog.TaskDialog;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3CustomViewDialog;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MessageDialog;
import org.koishi.launcher.h2co3.utils.download.InstallerItem;
import org.koishi.launcher.h2co3.utils.download.TaskCancellationAction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class EditVersionFragment extends H2CO3Fragment {

    private final Map<String, RemoteVersion> map = new HashMap<>();
    public H2CO3CustomViewDialog chooseInstallerVersionDialog;
    public AlertDialog chooseInstallerVersionDialogAlert;
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
    private TaskDialog pane;
    private AlertDialog paneAlert;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_download_edit_version, container, false);
        initView();
        downloadProviders = new DownloadProviders();
        Bundle args = getArguments();
        System.out.println(args);
        if (args != null) {
            String versionName = args.getString("versionName");
            this.gameVersion = args.getString("versionName");
            versionNameEditText.setText(versionName);
        }
        group = new InstallerItem.InstallerItemGroup(getContext(), gameVersion);
        installerScrollView.addView(group.getView());
        NavController navController = Navigation.findNavController(requireParentFragment().requireView());
        backButton.setOnClickListener(v -> navController.navigate(R.id.action_editVersionFragment_to_chooseVersionFragment));
        for (InstallerItem library : group.getLibraries()) {
            String libraryId = library.getLibraryId();
            System.out.println(libraryId);

            if (libraryId.equals("game")) continue;
            library.action.set(() -> {
                if (LibraryAnalyzer.LibraryType.FABRIC_API.getPatchId().equals(libraryId)) {
                    H2CO3MessageDialog builder = new H2CO3MessageDialog(getContext());
                    builder.setCancelable(false);
                    builder.setMessage(requireContext().getString(org.koishi.launcher.h2co3.resources.R.string.install_installer_fabric_api_warning));
                    builder.setNegativeButton(requireContext().getString(org.koishi.launcher.h2co3.resources.R.string.button_cancel), null);
                    builder.create().show();
                }

                if (library.incompatibleLibraryName.get() == null) {

                    currentVersionList = downloadProviders.getDownloadProvider().getVersionListById(libraryId);
                    if (!isChooseInstallerVersionDialogShowing) {
                        showChooseInstallerVersionDialog(libraryId);
                    }
                    listener = remoteVersion -> {
                        map.put(libraryId, remoteVersion);
                        System.out.println(map);
                        reload();
                        chooseInstallerVersionDialogAlert.dismiss();
                    };
                }
            });
            library.removeAction.set(() -> {
                map.remove(libraryId);
                reload();
            });
        }


        downloadButton.setOnClickListener(v -> {
            String versionName = versionNameEditText.getText() != null ? versionNameEditText.getText().toString() : "";
            DownloadProviders downloadProviders = new DownloadProviders();
            H2CO3CacheRepository cacheRepository = H2CO3CacheRepository.REPOSITORY;
            CacheRepository.setInstance(cacheRepository);
            cacheRepository.setDirectory(H2CO3Tools.CACHE_DIR);
            System.out.println(cacheRepository.getDirectory());

            DefaultDependencyManager dependencyManager = new DefaultDependencyManager(new H2CO3GameRepository(new File(H2CO3GameHelper.getGameDirectory())), downloadProviders.getDownloadProvider(), cacheRepository);
            GameBuilder builder = dependencyManager.gameBuilder();

            builder.name(versionName);
            builder.gameVersion(gameVersion);

            String minecraftPatchId = LibraryAnalyzer.LibraryType.MINECRAFT.getPatchId();
            for (Map.Entry<String, RemoteVersion> entry : map.entrySet()) {
                if (!minecraftPatchId.equals(entry.getKey())) {
                    builder.version(entry.getValue());
                    System.out.println(entry.getValue());
                }
            }

            Task<?> task = builder.buildAsync();


            pane = new TaskDialog(requireContext());
            paneAlert = pane.create();
            pane.setAlertDialog(paneAlert);
            pane.setCancel(new TaskCancellationAction(() -> paneAlert.dismiss()));
            pane.setTitle("Installing...");

            Schedulers.androidUIThread().execute(() -> {
                TaskExecutor executor = task.executor(new TaskListener() {
                    @Override
                    public void onStop(boolean success, TaskExecutor executor) {
                        Schedulers.androidUIThread().execute(() -> {
                            if (success) {
                                showCompletionDialog(getContext());
                            } else {
                                if (executor.getException() == null) {
                                    return;
                                }
                                paneAlert.dismiss();
                                H2CO3Tools.showError(requireContext(), String.valueOf(executor.getException()));
                            }
                        });
                    }
                });
                pane.setExecutor(executor);
                paneAlert.show();
                executor.start();
            });
        });


        return view;
    }

    private void initView() {
        versionNameEditText = findViewById(view, R.id.version_name_edit);
        backButton = findViewById(view, R.id.minecraft_back_button);
        downloadButton = findViewById(view, R.id.minecraft_download_button);

        installerScrollView = findViewById(view, R.id.installer_list_layout);
    }

    private void showCompletionDialog(Context context) {
        new H2CO3CustomViewDialog(context)
                .setMessage("完成")
                .create()
                .show();
    }

    private void showChooseInstallerVersionDialog(String libId) {
        isChooseInstallerVersionDialogShowing = true;

        chooseInstallerVersionDialog = new H2CO3CustomViewDialog(requireActivity());
        chooseInstallerVersionDialog.setCustomView(R.layout.dialog_installer_version);
        chooseInstallerVersionDialog.setTitle(getString(org.koishi.launcher.h2co3.resources.R.string.title_activity_login));
        installerVersionListView = chooseInstallerVersionDialog.findViewById(R.id.list_left);

        chooseInstallerVersionDialogAlert = chooseInstallerVersionDialog.create();
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
                    if (currentVersionList.getVersions(gameVersion).isEmpty()) {
                        H2CO3Tools.showError(requireContext(), "Null");
                        chooseInstallerVersionDialogAlert.dismiss();
                        installerVersionListView.setVisibility(View.GONE);
                    } else {
                        if (!items.isEmpty()) {
                            RemoteVersionListAdapter adapter = new RemoteVersionListAdapter(getContext(), new ArrayList<>(items), listener);
                            installerVersionListView.setLayoutManager(new LinearLayoutManager(getContext()));
                            installerVersionListView.setAdapter(adapter);
                        }
                        installerVersionListView.setVisibility(View.VISIBLE);
                    }
                });
            }

            System.gc();
        });
    }

    private String getVersion(String id) {
        return Objects.requireNonNull(map.get(id)).getSelfVersion();
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