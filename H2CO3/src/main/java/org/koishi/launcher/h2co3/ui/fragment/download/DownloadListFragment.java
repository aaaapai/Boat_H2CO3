package org.koishi.launcher.h2co3.ui.fragment.download;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;

public class DownloadListFragment extends H2CO3Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private H2CO3Fragment currentFragment;
    private View view;

    private MinecraftVersionListFragment minecraftVersionListFragment;
    private ModListFragment modListFragment;
    private ModPackListFragment modPackListFragment;
    private ResourcesPackListFragment resourcesPackListFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_download_list, container, false);
        initUI();
        if (savedInstanceState != null) {
            currentFragment = (H2CO3Fragment) getChildFragmentManager().getFragment(savedInstanceState, "currentFragment");
        }
        if (currentFragment == null) {
            initFragment(new MinecraftVersionListFragment());
        }
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.navigation_download);
        setNavigationItemChecked();
        return view;
    }

    private void initUI() {
        minecraftVersionListFragment = new MinecraftVersionListFragment();
        modListFragment = new ModListFragment();
        modPackListFragment = new ModPackListFragment();
        resourcesPackListFragment = new ResourcesPackListFragment();
        navigationView = view.findViewById(R.id.nav);
    }

    private void initFragment(H2CO3Fragment fragment) {
        if (currentFragment != fragment) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(org.koishi.launcher.h2co3.resources.R.anim.fragment_enter_pop, org.koishi.launcher.h2co3.resources.R.anim.fragment_exit_pop);
            if (fragment.isAdded()) {
                transaction.show(fragment);
            } else {
                transaction.add(R.id.fragmentContainerView, fragment);
            }

            if (currentFragment != null && currentFragment != fragment) {
                transaction.hide(currentFragment);
            }

            currentFragment = fragment;
            transaction.commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (item.isChecked()) {
            return true;
        }

        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
        item.setChecked(true);

        if (itemId == R.id.navigation_minecraftVersion) {
            switchFragment(minecraftVersionListFragment);
        } else if (itemId == R.id.navigation_modList) {
            switchFragment(modListFragment);
        } else if (itemId == R.id.navigation_modPackList) {
            switchFragment(modPackListFragment);
        } else if (itemId == R.id.navigation_resourcesPack) {
            switchFragment(resourcesPackListFragment);
        }
        return true;
    }

    private void switchFragment(H2CO3Fragment fragment) {
        initFragment(fragment);
    }

    private void setNavigationItemChecked() {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
        menu.getItem(0).setChecked(true);
    }

}