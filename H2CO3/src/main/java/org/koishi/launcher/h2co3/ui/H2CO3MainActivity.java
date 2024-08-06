package org.koishi.launcher.h2co3.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;
import org.koishi.launcher.h2co3.resources.component.H2CO3ToolBar;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.koishi.launcher.h2co3.ui.fragment.directory.DirectoryFragment;
import org.koishi.launcher.h2co3.ui.fragment.download.DownloadListFragment;
import org.koishi.launcher.h2co3.ui.fragment.home.HomeFragment;
import org.koishi.launcher.h2co3.ui.fragment.manage.ManageFragment;

public class H2CO3MainActivity extends H2CO3Activity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private H2CO3ToolBar toolbar;
    private NavigationView navigationView;
    private H2CO3Fragment currentFragment;

    private HomeFragment homeFragment;
    private DirectoryFragment directoryFragment;
    private ManageFragment manageFragment;
    private DownloadListFragment downloadFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        toolbar.inflateMenu(R.menu.home_toolbar);
        setSupportActionBar(toolbar);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.navigation_home);
        getSupportActionBar().setTitle(getString(org.koishi.launcher.h2co3.resources.R.string.app_name));
        initFragment(getHomeFragment());
        setNavigationItemChecked(R.id.navigation_home);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setNavigationItemChecked(R.id.navigation_home);
                switchFragment(getHomeFragment(), org.koishi.launcher.h2co3.resources.R.string.title_home);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav);
    }

    private HomeFragment getHomeFragment() {
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        return homeFragment;
    }

    private DirectoryFragment getDirectoryFragment() {
        if (directoryFragment == null) {
            directoryFragment = new DirectoryFragment();
        }
        return directoryFragment;
    }

    private ManageFragment getManageFragment() {
        if (manageFragment == null) {
            manageFragment = new ManageFragment();
        }
        return manageFragment;
    }

    private DownloadListFragment getDownloadFragment() {
        if (downloadFragment == null) {
            downloadFragment = new DownloadListFragment();
        }
        return downloadFragment;
    }

    private void initFragment(H2CO3Fragment fragment) {
        if (currentFragment != fragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(org.koishi.launcher.h2co3.resources.R.anim.fragment_enter_pop, org.koishi.launcher.h2co3.resources.R.anim.fragment_exit_pop);
            if (fragment != null) {
                if (fragment.isAdded()) {
                    transaction.show(fragment);
                } else {
                    transaction.add(R.id.nav_host_fragment, fragment);
                }
                if (currentFragment != null) {
                    transaction.hide(currentFragment);
                }
                currentFragment = fragment;
                transaction.commit();
            }
        }
    }

    @Override
    public void onClick(View v) {
        // Handle click events
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_item_home) {
            setNavigationItemChecked(R.id.navigation_home);
            switchFragment(getHomeFragment(), org.koishi.launcher.h2co3.resources.R.string.app_name);
        } else if (item.getItemId() == R.id.action_item_setting) {
            setNavigationItemChecked(R.id.navigation_manage);
            switchFragment(getManageFragment(), org.koishi.launcher.h2co3.resources.R.string.title_manage);
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchFragment(H2CO3Fragment fragment, int resID) {
        initFragment(fragment);
        getSupportActionBar().setTitle(getString(resID));
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

        if (menuItem.getItemId() == R.id.navigation_home) {
            switchFragment(getHomeFragment(), org.koishi.launcher.h2co3.resources.R.string.app_name);
        } else if (menuItem.getItemId() == R.id.navigation_directory) {
            switchFragment(getDirectoryFragment(), org.koishi.launcher.h2co3.resources.R.string.title_directory);
        } else if (menuItem.getItemId() == R.id.navigation_manage) {
            switchFragment(getManageFragment(), org.koishi.launcher.h2co3.resources.R.string.title_manage);
        } else if (menuItem.getItemId() == R.id.navigation_download) {
            switchFragment(getDownloadFragment(), org.koishi.launcher.h2co3.resources.R.string.title_download);
        }
        return true;
    }
}