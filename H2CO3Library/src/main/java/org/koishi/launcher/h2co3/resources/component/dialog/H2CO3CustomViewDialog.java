/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.resources.component.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class H2CO3CustomViewDialog extends H2CO3MaterialDialog {
    private View customView;

    public H2CO3CustomViewDialog(Context context) {
        super(context);
    }

    public View getCustomView() {
        return customView;
    }

    public void setCustomView(int layoutResId) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        customView = inflater.inflate(layoutResId, null);
        setView(customView);
    }

    /**
     * 根据ID查找视图
     *
     * @param viewId 视图ID
     * @param <T>    视图类型
     * @return 查找到的视图，如果未找到则返回null
     */
    public <T extends View> T findViewById(int viewId) {
        if (getCustomView() != null) {
            return getCustomView().findViewById(viewId);
        }
        return null;
    }
}