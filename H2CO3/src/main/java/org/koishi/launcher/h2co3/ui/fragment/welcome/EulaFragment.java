package org.koishi.launcher.h2co3.ui.fragment.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.utils.NetworkUtils;
import org.koishi.launcher.h2co3.core.utils.RuntimeUtils;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;
import org.koishi.launcher.h2co3.ui.H2CO3MainActivity;

import java.io.IOException;

public class EulaFragment extends H2CO3Fragment implements View.OnClickListener {

    public static final String EULA_URL = "https://gitee.com/cainiaohanhanyai/cnhhfile/raw/master/Documents/eula.txt?inline=false";
    private final Handler handler = new Handler(Looper.getMainLooper());
    NavController navController;
    private ProgressBar progressBar;
    private H2CO3TextView eula;
    private boolean load = false;
    private boolean h2co3Launcher = false;
    private boolean java8 = false;
    private boolean java11 = false;
    private boolean java17 = false;
    private boolean java21 = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_eula, container, false);
        progressBar = findViewById(view, R.id.loading_progress);
        eula = findViewById(view, R.id.eulaText);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.nextButton).setEnabled(false);
        navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        view.findViewById(R.id.nextButton).setOnClickListener(v -> {
            H2CO3Tools.setH2CO3Value("isFirstLaunch", false);
            checkPermission();
        });

        loadEula(view);
    }

    private void loadEula(View v) {
        new Thread(() -> {
            String str;
            try {
                str = NetworkUtils.doGet(NetworkUtils.toURL(EULA_URL));
                load = true;
            } catch (IOException e) {
                str = getString(org.koishi.launcher.h2co3.resources.R.string.title_error);
                load = true;
            }
            final String s = str;
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (load) {
                        v.findViewById(R.id.nextButton).setEnabled(true);
                    }
                    progressBar.setVisibility(View.GONE);
                    eula.setText(s);
                });
            }
        }).start();
    }

    private void checkPermission() {
        if (!XXPermissions.isGranted(requireContext(), Permission.MANAGE_EXTERNAL_STORAGE)) {
            navController.navigate(R.id.action_eulaFragment_to_permissionRequestFragment);
        } else {
            initRuntimeState();
            if (checkRuntime()) {
                handler.postDelayed(() -> startActivity(new Intent(requireActivity(), H2CO3MainActivity.class)), 1000);
                requireActivity().finish();
            } else {
                navController.navigate(R.id.action_eulaFragment_to_installFragment);
            }
        }
    }

    private boolean checkRuntime() {
        return h2co3Launcher && java8 && java11 && java17 && java21;
    }

    private void initRuntimeState() {
        try {
            h2co3Launcher = RuntimeUtils.isLatest(H2CO3Tools.H2CO3LAUNCHER_LIBRARY_DIR, "/assets/app_runtime/h2co3Launcher");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java8 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_8_PATH, "/assets/app_runtime/java/jre8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java11 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_11_PATH, "/assets/app_runtime/java/jre11");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java17 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_17_PATH, "/assets/app_runtime/java/jre17");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java21 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_21_PATH, "/assets/app_runtime/java/jre21");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }
}