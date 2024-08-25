package org.koishi.launcher.h2co3.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.game.h2co3launcher.H2CO3GameHelper;
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.resources.component.H2CO3Button;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.koishi.launcher.h2co3.resources.component.adapter.H2CO3RecycleAdapter;
import org.koishi.launcher.h2co3.ui.fragment.directory.DirectoryFragment;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MCVersionListAdapter extends H2CO3RecycleAdapter<String> {

    private final DirectoryFragment directoryFragment;
    private final H2CO3GameHelper gameHelper;
    private String path;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean isVersionItemClickable = true;
    private boolean isDeleteButtonClickable = true;

    public MCVersionListAdapter(Context context, List<String> list, DirectoryFragment directoryFragment, H2CO3GameHelper gameHelper, String path) {
        super(list, context);
        this.directoryFragment = directoryFragment;
        this.gameHelper = gameHelper;
        this.path = path;
    }

    @Override
    protected void bindData(BaseViewHolder holder, int position) {
        H2CO3TextView versionName = (H2CO3TextView) holder.getView(R.id.version_name);
        H2CO3Button deleteVerButton = (H2CO3Button) holder.getView(R.id.version_delete);
        H2CO3CardView versionItemView = (H2CO3CardView) holder.getView(R.id.version_item);
        ImageView versionIcon = (ImageView) holder.getView(R.id.version_icon);

        if (position < data.size()) {
            String version = data.get(position);
            File versionDir = new File(path, version);
            String verF = versionDir.getPath();

            versionName.setText(version);
            boolean isDirectory = versionDir.isDirectory();
            versionItemView.setVisibility(isDirectory ? View.VISIBLE : View.GONE);
            versionItemView.setStrokeWidth(verF.equals(gameHelper.getGameCurrentVersion()) ? 13 : 3);
            versionItemView.setClickable(verF.equals(gameHelper.getGameCurrentVersion()));

            versionItemView.setOnClickListener(v -> {
                if (isVersionItemClickable) {
                    isVersionItemClickable = false;
                    if (isDirectory) {
                        gameHelper.setGameCurrentVersion(verF);
                        for (int i = 0; i < getItemCount(); i++) {
                            notifyItemChanged(i);
                        }
                    }
                    v.postDelayed(() -> isVersionItemClickable = true, 200);
                }
            });

            deleteVerButton.setOnClickListener(v -> {
                if (isDeleteButtonClickable) {
                    isDeleteButtonClickable = false;
                    showDeleteDialog(position);
                    v.postDelayed(() -> isDeleteButtonClickable = true, 200);
                }
            });
        }
    }

    private void showDeleteDialog(int position) {
        new MaterialAlertDialogBuilder(mContext)
                .setTitle(org.koishi.launcher.h2co3.library.R.string.title_action)
                .setMessage(org.koishi.launcher.h2co3.library.R.string.ver_if_del)
                .setPositiveButton("Yes", (dialog, which) -> deleteVersion(position))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteVersion(int position) {
        executorService.execute(() -> {
            File versionDir = new File(gameHelper.getGameDirectory(), "versions/" + data.get(position));
            try {
                if (versionDir.isDirectory() && versionDir.getCanonicalPath().startsWith(new File(gameHelper.getGameDirectory(), "versions").getCanonicalPath())) {
                    FileTools.deleteDirectory(versionDir);
                } else {
                    deleteFile(versionDir);
                }
                ((H2CO3Activity) mContext).runOnUiThread(() -> {
                    data.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, data.size() - position);
                });
                directoryFragment.handler.sendEmptyMessage(2);
            } catch (IOException e) {
                H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
            }
        });
    }

    private void deleteFile(File file) throws IOException {
        if (file.exists() && !file.delete()) {
            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, "Failed to delete file: " + file.getPath());
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.item_version_local;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updatePath(String path) {
        this.path = path;
    }
}