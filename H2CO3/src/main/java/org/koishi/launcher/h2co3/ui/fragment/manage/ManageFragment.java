package org.koishi.launcher.h2co3.ui.fragment.manage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.ui.fragment.H2CO3Fragment;

public class ManageFragment extends H2CO3Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private View view;
    private NavigationView navigationView;
    private H2CO3Fragment currentFragment;

    private GlobalGameSettingFragment globalGameSettingFragment;
    private H2CO3LauncherSettingFragment h2co3LauncherSettingFragment;
    private H2CO3HelpFragment h2co3HelpFragment;
    private H2CO3LauncherAboutFragment h2co3LauncherAboutFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage, container, false);
        initUI();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.navigation_global_game_setting);
        preLoadFragments();

        switchFragment(getGlobalGameSettingFragment());

        return view;
    }

    private void initUI() {
        navigationView = view.findViewById(R.id.nav);
    }

    private void preLoadFragments() {
        globalGameSettingFragment = new GlobalGameSettingFragment();
        h2co3LauncherSettingFragment = new H2CO3LauncherSettingFragment();
        h2co3HelpFragment = new H2CO3HelpFragment();
        h2co3LauncherAboutFragment = new H2CO3LauncherAboutFragment();
    }

    private GlobalGameSettingFragment getGlobalGameSettingFragment() {
        return globalGameSettingFragment;
    }

    private H2CO3LauncherSettingFragment getH2CO3LauncherSettingFragment() {
        return h2co3LauncherSettingFragment;
    }

    private H2CO3HelpFragment getH2CO3HelpFragment() {
        return h2co3HelpFragment;
    }

    private H2CO3LauncherAboutFragment getH2CO3LauncherAboutFragment() {
        return h2co3LauncherAboutFragment;
    }

    private void initFragment(H2CO3Fragment fragment) {
        if (currentFragment != fragment) {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.setCustomAnimations(org.koishi.launcher.h2co3.library.R.anim.fragment_in, org.koishi.launcher.h2co3.library.R.anim.fragment_out, org.koishi.launcher.h2co3.library.R.anim.fragment_in_pop, org.koishi.launcher.h2co3.library.R.anim.fragment_out_pop);
            if (fragment != null) {
                if (fragment.isAdded()) {
                    transaction.show(fragment);
                } else {
                    transaction.add(R.id.manageFragmentContainerView, fragment);
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
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {
        if (menuItem.isChecked()) {
            return true;
        }

        setNavigationItemChecked(menuItem.getItemId());

        if (menuItem.getItemId() == R.id.navigation_global_game_setting) {
            switchFragment(getGlobalGameSettingFragment());
        } else if (menuItem.getItemId() == R.id.navigation_h2co3_launcher_setting) {
            switchFragment(getH2CO3LauncherSettingFragment());
        } else if (menuItem.getItemId() == R.id.navigation_help) {
            switchFragment(getH2CO3HelpFragment());
        } else if (menuItem.getItemId() == R.id.navigation_about) {
            switchFragment(getH2CO3LauncherAboutFragment());
        }
        return true;
    }

}