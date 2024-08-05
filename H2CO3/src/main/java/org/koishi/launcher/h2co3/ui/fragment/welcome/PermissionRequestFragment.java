package org.koishi.launcher.h2co3.ui.fragment.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.adapter.PermissionRequestCardAdapter;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.utils.RuntimeUtils;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;
import org.koishi.launcher.h2co3.ui.H2CO3MainActivity;
import org.koishi.launcher.h2co3.utils.bean.ui.PermissionRequestCard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PermissionRequestFragment extends H2CO3Fragment {

    NavController navController;
    private boolean h2co3Launcher = false;
    private boolean java8 = false;
    private boolean java11 = false;
    private boolean java17 = false;
    private boolean java21 = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome_permission_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        setupRecyclerView();

        view.findViewById(R.id.nextButton).setOnClickListener(v -> {
            view.findViewById(R.id.nextButton).setEnabled(false);
            XXPermissions.with(requireActivity())
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            initState();
                            check();
                        }

                        @Override
                        public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                            view.findViewById(R.id.nextButton).setEnabled(true);
                        }
                    });

        });
    }

    private void check() {
        if (h2co3Launcher && java8 && java11 && java17 && java21) {
            startActivity(new Intent(requireActivity(), H2CO3MainActivity.class));
            requireActivity().finish();
        } else {
            navController.navigate(R.id.action_permissionRequestFragment_to_installFragment);
        }
    }

    private void initState() {
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

    private void setupRecyclerView() {
        RecyclerView recyclerView = requireView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        PermissionRequestCardAdapter permissionRequestCardAdapter = new PermissionRequestCardAdapter(requireContext(), getPermissionRequestCards());
        recyclerView.setAdapter(permissionRequestCardAdapter);
    }

    private List<PermissionRequestCard> getPermissionRequestCards() {
        List<PermissionRequestCard> cards = new ArrayList<>();
        cards.add(new PermissionRequestCard(org.koishi.launcher.h2co3.resources.R.drawable.ic_btm_manager_normal, org.koishi.launcher.h2co3.resources.R.string.storage_permission, org.koishi.launcher.h2co3.resources.R.string.storage_permission_description));
        return cards;
    }
}