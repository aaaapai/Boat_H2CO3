package org.koishi.launcher.h2co3.ui.fragment.download;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.ui.fragment.H2CO3Fragment;

public class ResourcesPackListFragment extends H2CO3Fragment {
    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_download_resources_list, container, false);
        return view;
    }
}
