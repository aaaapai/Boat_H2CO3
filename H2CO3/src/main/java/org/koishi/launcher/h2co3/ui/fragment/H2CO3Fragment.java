/*
 * <!--This file is part of LSPosed.
 *
 * LSPosed is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LSPosed is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LSPosed.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2021 LSPosed Contributors-->
 */

package org.koishi.launcher.h2co3.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import org.koishi.launcher.h2co3.application.H2CO3Application;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public abstract class H2CO3Fragment extends Fragment {
    public final <T extends View> T findViewById(View view, int id) {
        return view.findViewById(id);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (requireActivity().getWindow() != null) {
            requireActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        return null;
    }

    public void navigateUp() {
        getNavController().navigateUp();
    }

    public NavController getNavController() {
        return NavHostFragment.findNavController(this);
    }

    public boolean safeNavigate(@IdRes int resId) {
        try {
            getNavController().navigate(resId);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    public boolean safeNavigate(NavDirections direction) {
        try {
            getNavController().navigate(direction);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    public void runAsync(Runnable runnable) {
        H2CO3Application.getExecutorService().submit(runnable);
    }

    public <T> Future<T> runAsync(Callable<T> callable) {
        return H2CO3Application.getExecutorService().submit(callable);
    }

    public void runOnUiThread(Runnable runnable) {
        H2CO3Application.getMainHandler().post(runnable);
    }

    public <T> Future<T> runOnUiThread(Callable<T> callable) {
        var task = new FutureTask<>(callable);
        runOnUiThread(task);
        return task;
    }
}
