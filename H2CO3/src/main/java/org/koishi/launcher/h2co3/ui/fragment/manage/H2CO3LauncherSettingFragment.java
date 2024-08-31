package org.koishi.launcher.h2co3.ui.fragment.manage;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.resources.component.preference.H2CO3SwitchPreference;
import org.koishi.launcher.h2co3.ui.fragment.H2CO3Fragment;

public class H2CO3LauncherSettingFragment extends H2CO3Fragment {
    private View view;

    private H2CO3SwitchPreference preferenceEnableMaterialMonet;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage_launcher_setting, container, false);

        preferenceEnableMaterialMonet = view.findViewById(R.id.preference_enable_material_monet);

        initEnableMaterialMonet();
        return view;
    }

    private void initEnableMaterialMonet() {
        if (Build.VERSION.SDK_INT < 31){
            preferenceEnableMaterialMonet.rootView.setEnabled(false);
            preferenceEnableMaterialMonet.rootView.setClickable(false);
            preferenceEnableMaterialMonet.rootView.setFocusable(false);
            preferenceEnableMaterialMonet.switchView.setEnabled(false);
            preferenceEnableMaterialMonet.switchView.setClickable(false);
            preferenceEnableMaterialMonet.switchView.setFocusable(false);
        }
        preferenceEnableMaterialMonet.setTitle(requireActivity().getString(org.koishi.launcher.h2co3.library.R.string.title_enable_monet));
        preferenceEnableMaterialMonet.setChecked(H2CO3Tools.getH2CO3Value("enable_monet", false,  Boolean.class));
        preferenceEnableMaterialMonet.setOnPreferenceChangeListener((preference, newValue) -> {
            H2CO3Tools.setH2CO3Value("enable_monet", newValue);
        });
    }
}
