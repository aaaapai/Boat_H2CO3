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
import org.koishi.launcher.h2co3.ui.fragment.H2CO3Fragment;

public class DownloadFragment extends H2CO3Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private View view;
    private NavigationView navigationView;
    private H2CO3Fragment currentFragment;

    private MinecraftVersionListFragment minecraftVersionListFragment;
    private ModListFragment modListFragment;
    private ModPackListFragment modPackListFragment;
    private ResourcesPackListFragment resourcesPackListFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_download, container, false);
        initUI();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.navigation_minecraftVersion);
        preLoadFragments();

        switchFragment(getMinecraftVersionListFragment());

        return view;
    }

    private void initUI() {
        navigationView = view.findViewById(R.id.nav);
    }

    private void preLoadFragments() {
        minecraftVersionListFragment = new MinecraftVersionListFragment();
        modListFragment = new ModListFragment();
        modPackListFragment = new ModPackListFragment();
        resourcesPackListFragment = new ResourcesPackListFragment();
    }

    private MinecraftVersionListFragment getMinecraftVersionListFragment() {
        return minecraftVersionListFragment;
    }

    private ModListFragment getModListFragment() {
        return modListFragment;
    }

    private ModPackListFragment getModPackListFragment() {
        return modPackListFragment;
    }

    private ResourcesPackListFragment getResourcesPackListFragment() {
        return resourcesPackListFragment;
    }

    private void initFragment(H2CO3Fragment fragment) {
        if (currentFragment != fragment) {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.setCustomAnimations(org.koishi.launcher.h2co3.library.R.anim.fragment_in, org.koishi.launcher.h2co3.library.R.anim.fragment_out, org.koishi.launcher.h2co3.library.R.anim.fragment_in_pop, org.koishi.launcher.h2co3.library.R.anim.fragment_out_pop);
            if (fragment != null) {
                if (fragment.isAdded()) {
                    transaction.show(fragment);
                } else {
                    transaction.add(R.id.downloadFragmentContainerView, fragment);
                }
                if (currentFragment != null) {
                    transaction.hide(currentFragment);
                }
                currentFragment = fragment;
                transaction.commit();
            }
        }
    }

    private void switchFragment(H2CO3Fragment fragment) {
        initFragment(fragment);
    }

    private void setNavigationItemChecked(int itemId) {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(menu.getItem(i).getItemId() == itemId);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.isChecked()) {
            return true;
        }

        setNavigationItemChecked(menuItem.getItemId());

        if (menuItem.getItemId() == R.id.navigation_minecraftVersion) {
            switchFragment(getMinecraftVersionListFragment());
        } else if (menuItem.getItemId() == R.id.navigation_modList) {
            switchFragment(getModListFragment());
        } else if (menuItem.getItemId() == R.id.navigation_modPackList) {
            switchFragment(getModPackListFragment());
        } else if (menuItem.getItemId() == R.id.navigation_resourcesPack) {
            switchFragment(getResourcesPackListFragment());
        }
        return true;
    }

}