package org.koishi.launcher.h2co3.ui.fragment.manage;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Settings;
import org.koishi.launcher.h2co3.resources.component.preference.CustomListPreference;
import org.koishi.launcher.h2co3.resources.component.preference.CustomSwitchPreference;
import org.koishi.launcher.h2co3.resources.component.preference.RangeSliderPreference;
import org.koishi.launcher.h2co3.ui.fragment.H2CO3Fragment;

public class GlobalGameSettingFragment extends H2CO3Fragment {
    private View view;

    private CustomListPreference preferenceChooseJava;
    private CustomSwitchPreference preferenceSetPriVerDir;
    private RangeSliderPreference preferenceSetGameMemory;
    private H2CO3Settings settings;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        settings = new H2CO3Settings();
        view = inflater.inflate(R.layout.fragment_manage_global_setting, container, false);
        preferenceChooseJava = view.findViewById(R.id.preference_choose_java);
        preferenceSetPriVerDir = view.findViewById(R.id.preference_set_pri_ver_dir);
        preferenceSetGameMemory = view.findViewById(R.id.preference_set_game_memory);

        initChooseJavaPreference();
        initSetPriVerDirPreference();
        initSetGameMemoryPreference();
        return view;
    }

    private void initSetGameMemoryPreference() {
        preferenceSetGameMemory.setUse(true);
        preferenceSetGameMemory.setTitle(getContext().getString(org.koishi.launcher.h2co3.library.R.string.title_game_memory));
        ActivityManager activityManager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        float maxMemoryMB = (float) (memoryInfo.totalMem / (1024 * 1024));
        preferenceSetGameMemory.setInitValues(256, maxMemoryMB);
        preferenceSetGameMemory.setStepSize(1);
        int[] initialValue = {settings.getGameMemoryMin(), settings.getGameMemoryMax()};
        preferenceSetGameMemory.setValues(initialValue[0], initialValue[1]);
        preferenceSetGameMemory.setOnRangeChangeListener((preference, valueFrom, valueTo) -> {
            settings.setGameMemoryMin((int) valueFrom);
            settings.setGameMemoryMax((int) valueTo);
        });
    }

    private void initSetPriVerDirPreference() {
        preferenceSetPriVerDir.setChecked(settings.getIsPriVerDir());
        preferenceSetPriVerDir.setOnPreferenceChangeListener((preference, newValue) -> {
            settings.setSetPriVerDir(newValue);
        });
        preferenceSetPriVerDir.setTitle(getContext().getString(org.koishi.launcher.h2co3.library.R.string.title_pri_ver_dir));
    }

    private void initChooseJavaPreference() {
        Context context = getContext();
        if (context == null) return;

        String titleJavaVersion = context.getString(org.koishi.launcher.h2co3.library.R.string.title_java_vesion);
        preferenceChooseJava.setTitle(titleJavaVersion);

        String[] entries = new String[]{
                context.getString(org.koishi.launcher.h2co3.library.R.string.title_menu_java_auto),
                context.getString(org.koishi.launcher.h2co3.library.R.string.runtime_java8),
                context.getString(org.koishi.launcher.h2co3.library.R.string.runtime_java11),
                context.getString(org.koishi.launcher.h2co3.library.R.string.runtime_java17),
                context.getString(org.koishi.launcher.h2co3.library.R.string.runtime_java21)
        };
        preferenceChooseJava.setEntries(entries, (preference, newValue) -> {
            if (newValue.equals(entries[1])) {
                settings.setJavaVer(1);
            } else if (newValue.equals(entries[2])) {
                settings.setJavaVer(2);
            } else if (newValue.equals(entries[3])) {
                settings.setJavaVer(3);
            } else if (newValue.equals(entries[4])) {
                settings.setJavaVer(4);
            } else {
                settings.setJavaVer(0);
            }
        });

        for (int i = 0; i < entries.length; i++) {
            if (settings.getJavaVer() == getJavaPathByIndex(i)) {
                preferenceChooseJava.setValue(entries[i]);
                break;
            }
        }
    }

    private int getJavaPathByIndex(int index) {
        return switch (index) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> 4;
            default -> 0;
        };
    }
}
