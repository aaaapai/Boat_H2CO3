package org.koishi.launcher.h2co3.adapter;

import static org.koishi.launcher.h2co3.core.H2CO3Tools.MINECRAFT_DIR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.game.h2co3launcher.H2CO3GameHelper;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.resources.component.adapter.H2CO3RecycleAdapter;
import org.koishi.launcher.h2co3.ui.fragment.directory.DirectoryFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DirectoryListAdapter extends H2CO3RecycleAdapter<String> {
    private final DirectoryFragment directoryFragment;
    private final H2CO3GameHelper gameHelper;
    private final JSONObject directoriesJson;

    public final String h2co3DirectoryPath = MINECRAFT_DIR;

    public DirectoryListAdapter(List<String> directoryList, Context context, JSONObject directoriesJson, H2CO3GameHelper gameHelper, DirectoryFragment directoryFragment) {
        super(directoryList, context);
        this.directoriesJson = directoriesJson;
        this.gameHelper = gameHelper;
        this.directoryFragment = directoryFragment;
    }

    @SuppressLint({"ResourceAsColor", "UseCompatLoadingForDrawables"})
    @Override
    protected void bindData(BaseViewHolder holder, final int position) {
        TextView pathTextView = (TextView) holder.getView(R.id.tv_record);
        TextView nameTextView = (TextView) holder.getView(R.id.tv_name);
        MaterialCardView cardView = (MaterialCardView) holder.getView(R.id.ver_item);
        ImageView statusIcon = (ImageView) holder.getView(R.id.ver_check_icon);
        MaterialButton removeButton = (MaterialButton) holder.getView(R.id.tv_remove_dir);
        MaterialButton deleteButton = (MaterialButton) holder.getView(R.id.tv_del_dir);

        pathTextView.setSingleLine(true);
        pathTextView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        pathTextView.setHorizontallyScrolling(true);

        nameTextView.setSingleLine(true);
        nameTextView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        nameTextView.setHorizontallyScrolling(true);

        if (directoriesJson == null) return;

        try {
            JSONObject directoryObject = directoriesJson.getJSONArray("dirs").getJSONObject(position);
            String directoryPath = directoryObject.getString("path");
            String directoryName = directoryObject.getString("name");

            pathTextView.setText(directoryPath);
            nameTextView.setText(directoryName);

            File directoryFile = new File(directoryPath);
            boolean isValidDirectory = directoryFile.exists() && directoryFile.isDirectory();
            statusIcon.setImageDrawable(mContext.getResources().getDrawable(isValidDirectory ? org.koishi.launcher.h2co3.resources.R.drawable.ic_menu_about : org.koishi.launcher.h2co3.resources.R.drawable.xicon));
            deleteButton.setVisibility(isValidDirectory ? View.GONE : View.VISIBLE);

            if (directoryPath.equals(gameHelper.getGameDirectory())) {
                cardView.setStrokeWidth(11);
                cardView.setCheckable(false);
                cardView.setOnClickListener(null);
            } else {
                cardView.setStrokeWidth(3);
                cardView.setOnClickListener(isValidDirectory ? v -> {
                    setDirectory(directoryPath);
                    updateData(getDirList());
                } : v -> H2CO3Tools.showError(mContext, mContext.getString(org.koishi.launcher.h2co3.resources.R.string.ver_null_dir)));
            };


            boolean isH2CO3Directory = directoryPath.equals(h2co3DirectoryPath);
            removeButton.setVisibility(isH2CO3Directory ? View.GONE : View.VISIBLE);
            deleteButton.setVisibility(isH2CO3Directory ? View.GONE : View.VISIBLE);

            removeButton.setOnClickListener(view -> showRemoveConfirmationDialog(position));
            deleteButton.setOnClickListener(view -> {
                if (mRvItemOnclickListener != null) {
                    showDeleteConfirmationDialog(directoryPath, position);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showRemoveConfirmationDialog(int position) {
        new MaterialAlertDialogBuilder(mContext)
                .setTitle(org.koishi.launcher.h2co3.resources.R.string.title_action)
                .setMessage(org.koishi.launcher.h2co3.resources.R.string.ver_if_remove)
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    if (mRvItemOnclickListener != null) {
                        mRvItemOnclickListener.RvItemOnclick(position);
                    }
                })
                .setNegativeButton("No", null)
                .create()
                .show();
    }

    private void showDeleteConfirmationDialog(String directoryPath, int position) {
        new MaterialAlertDialogBuilder(mContext)
                .setTitle(org.koishi.launcher.h2co3.resources.R.string.title_action)
                .setMessage(org.koishi.launcher.h2co3.resources.R.string.ver_if_del)
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    File directoryFile = new File(directoryPath);
                    mRvItemOnclickListener.RvItemOnclick(position);
                    this.updateData(getDirList());
                    new Thread(() -> {
                        try {
                            FileTools.deleteDirectory(directoryFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                })
                .setNegativeButton("No", null)
                .create()
                .show();
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_dir;
    }

    public void setDirectory(String directory) {
        gameHelper.setGameDirectory(directory);
        gameHelper.setGameAssets(directory + "/assets/virtual/legacy");
        gameHelper.setGameAssetsRoot(directory + "/assets");
        gameHelper.setGameCurrentVersion(directory + "/versions");
        directoryFragment.updateVerList(directory + "/versions");
    }

    private List<String> getDirList() {
        List<String> dirList = new ArrayList<>();
        try {
            JSONArray dirs = directoriesJson.getJSONArray("dirs");
            for (int i = 0; i < dirs.length(); i++) {
                JSONObject dirObj = dirs.getJSONObject(i);
                dirList.add(dirObj.getString("name"));
            }
        } catch (JSONException e) {
            directoryFragment.logError(e);
        }
        return dirList;
    }

    public interface RemoveVerOnclickListener {

        void RVItemOnclick(int position);
    }

    public interface DeleteDirOnclickListener {

        void DVItemOnclick(int position);
    }
}