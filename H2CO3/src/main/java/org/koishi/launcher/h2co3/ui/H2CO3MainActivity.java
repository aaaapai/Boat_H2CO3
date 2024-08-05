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

import java.util.Objects;

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
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(org.koishi.launcher.h2co3.resources.R.string.app_name));
        initFragment(homeFragment);
        setNavigationItemChecked(0);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setNavigationItemChecked(0);
                switchFragment(homeFragment, org.koishi.launcher.h2co3.resources.R.string.title_home);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav);
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        if (directoryFragment == null) {
            directoryFragment = new DirectoryFragment();
        }
        if (manageFragment == null) {
            manageFragment = new ManageFragment();
        }
        if (downloadFragment == null) {
            downloadFragment = new DownloadListFragment();
        }
    }

    private void initFragment(H2CO3Fragment fragment) {
        if (currentFragment != fragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(org.koishi.launcher.h2co3.resources.R.anim.fragment_enter_pop, org.koishi.launcher.h2co3.resources.R.anim.fragment_exit_pop);
            if (fragment.isAdded()) {
                transaction.show(fragment);
            } else {
                transaction.add(R.id.nav_host_fragment, fragment);
            }

            if (currentFragment != null && currentFragment != fragment) {
                transaction.hide(currentFragment);
            }

            currentFragment = fragment;
            transaction.commit();
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
            setNavigationItemChecked(0);
            switchFragment(homeFragment, org.koishi.launcher.h2co3.resources.R.string.app_name);
        } else if (item.getItemId() == R.id.action_item_setting) {
            setNavigationItemChecked(3);
            switchFragment(manageFragment, org.koishi.launcher.h2co3.resources.R.string.title_manage);
        }
        return super.onOptionsItemSelected(item);
    }


    private void switchFragment(H2CO3Fragment fragment, int resID) {
        initFragment(fragment);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(resID));
    }

    private void setNavigationItemChecked(int index) {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
        menu.getItem(index).setChecked(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (menuItem.isChecked()) {
            return true;
        }

        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
        menuItem.setChecked(true);

        if (itemId == R.id.navigation_home) {
            switchFragment(homeFragment, org.koishi.launcher.h2co3.resources.R.string.app_name);
        } else if (itemId == R.id.navigation_directory) {
            switchFragment(directoryFragment, org.koishi.launcher.h2co3.resources.R.string.title_directory);
        } else if (itemId == R.id.navigation_manage) {
            switchFragment(manageFragment, org.koishi.launcher.h2co3.resources.R.string.title_manage);
        } else if (itemId == R.id.navigation_download) {
            switchFragment(downloadFragment, org.koishi.launcher.h2co3.resources.R.string.title_download);
        }
        return true;
    }
}
