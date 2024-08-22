package org.koishi.launcher.h2co3.adapter;

import android.content.Context;
import android.widget.ImageView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.game.h2co3launcher.H2CO3GameHelper;
import org.koishi.launcher.h2co3.resources.component.H2CO3Button;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;
import org.koishi.launcher.h2co3.resources.component.adapter.H2CO3RecycleAdapter;
import org.koishi.launcher.h2co3.ui.fragment.directory.DirectoryFragment;

import java.io.File;
import java.util.List;

public class MCVersionListAdapter extends H2CO3RecycleAdapter<String> {

    private H2CO3TextView versionName, versionLib;
    private H2CO3Button deleteVerButton;
    private ImageView versionIcon;
    private DirectoryFragment directoryFragment;
    private String path;

    public MCVersionListAdapter(Context context, List<String> list, DirectoryFragment directoryFragment, H2CO3GameHelper h2CO3GameHelper, String path) {
        super(list, context);
        this.directoryFragment = directoryFragment;
        this.path = path;
    }

    @Override
    protected void bindData(BaseViewHolder holder, int position) {
        versionName = (H2CO3TextView) holder.getView(R.id.version_name);
        versionLib = (H2CO3TextView) holder.getView(R.id.version_lib);
        versionIcon = (ImageView) holder.getView(R.id.version_icon);
        deleteVerButton = (H2CO3Button) holder.getView(R.id.version_delete);
       init(position);
    }

    private void init(int position) {
        versionName.setText(data.get(position));
        File f = new File(path + "/" + data.get(position));
        if (!f.isDirectory() && !f.exists()) {
            deleteVerButton.setEnabled(false);
            versionIcon.setImageDrawable(mContext.getResources().getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.xicon));
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_version_local;
    }

    public void updatePath(String path) {
        this.path = path;
    }

}