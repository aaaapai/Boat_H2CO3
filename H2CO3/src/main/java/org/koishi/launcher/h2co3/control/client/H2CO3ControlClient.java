/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.control.client;

import android.view.View;
import android.view.ViewGroup;

import org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;

public interface H2CO3ControlClient extends KeyEvent {
    void setKey(int keyCode, boolean pressed);

    void setMouseButton(int mouseCode, boolean pressed);

    void setPointer(int x, int y);

    void setPointerInc(int xInc, int yInc);

    H2CO3Activity getActivity();

    void addView(View v);

    void addContentView(View view, ViewGroup.LayoutParams params);

    void typeWords(String str);

    int[] getGrabbedPointer();

    int[] getLoosenPointer();

    ViewGroup getViewsParent();

    View getSurfaceLayerView();

    boolean isGrabbed();
}