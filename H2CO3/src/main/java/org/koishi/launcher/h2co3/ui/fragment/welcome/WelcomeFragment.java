package org.koishi.launcher.h2co3.ui.fragment.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Auth;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.h2co3launcher.utils.H2CO3GameHelper;
import org.koishi.launcher.h2co3.core.utils.RuntimeUtils;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;
import org.koishi.launcher.h2co3.ui.H2CO3MainActivity;

import java.io.IOException;

public class WelcomeFragment extends Fragment {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private H2CO3TextView title, description;
    private NavController navController;
    private ConstraintLayout bottomLayout;
    private boolean h2co3Launcher = false;
    private boolean java8 = false;
    private boolean java11 = false;
    private boolean java17 = false;
    private boolean java21 = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isAdded()) {
            // Only proceed if the fragment is added to the activity
            navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
            initViews(view);
            checkFirstLaunch();
        } else {
            // Handle the case where the fragment is not added to the activity
        }
    }

    private void initViews(@NonNull View view) {
        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.description);
        LinearProgressIndicator progressIndicator = view.findViewById(R.id.linearProgressIndicator);
        FloatingActionButton nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> navController.navigate(R.id.action_welcomeFragment_to_eulaFragment));
        bottomLayout = view.findViewById(R.id.bottom_layout);
    }

    private void checkFirstLaunch() {
        boolean isFirstLaunch = H2CO3Tools.getH2CO3Value("isFirstLaunch", true, Boolean.class);
        if (isFirstLaunch) {
            H2CO3Auth.resetUserState();
            H2CO3GameHelper.setDir(H2CO3Tools.MINECRAFT_DIR);
            showWelcomeUI();
        } else {
            checkPermission();
        }
    }

    private void showWelcomeUI() {
        title.setVisibility(View.VISIBLE);
        description.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);
    }

    private void checkPermission() {
        if (!XXPermissions.isGranted(requireContext(), Permission.MANAGE_EXTERNAL_STORAGE)) {
            navController.navigate(R.id.action_welcomeFragment_to_permissionRequestFragment);
        } else {
            initRuntimeState();
            if (checkRuntime()) {
                handler.postDelayed(this::proceedToMainActivity, 1000);
            } else {
                navController.navigate(R.id.action_welcomeFragment_to_installFragment);
            }
        }
    }

    private void proceedToMainActivity() {
        Intent intent = new Intent(requireActivity(), H2CO3MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private boolean checkRuntime() {
        return h2co3Launcher && java8 && java11 && java17 && java21;
    }

    private void initRuntimeState() {
        try {
            h2co3Launcher = RuntimeUtils.isLatest(H2CO3Tools.H2CO3LAUNCHER_LIBRARY_DIR, "/assets/app_runtime/h2co3Launcher");
            java8 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_8_PATH, "/assets/app_runtime/java/jre8");
            java11 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_11_PATH, "/assets/app_runtime/java/jre11");
            java17 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_17_PATH, "/assets/app_runtime/java/jre17");
            java21 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_21_PATH, "/assets/app_runtime/java/jre21");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}