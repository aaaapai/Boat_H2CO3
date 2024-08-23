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
import org.koishi.launcher.h2co3.application.H2CO3Application;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.game.h2co3launcher.H2CO3GameHelper;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.koishi.launcher.h2co3.resources.component.adapter.H2CO3RecycleAdapter;
import org.koishi.launcher.h2co3.ui.fragment.directory.DirectoryFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DirectoryListAdapter extends H2CO3RecycleAdapter<String> {
    private final DirectoryFragment directoryFragment;
    private final H2CO3GameHelper gameHelper;
    private final JSONObject directoriesJsonObj;
    private final String h2co3DirectoryPath = MINECRAFT_DIR;
    private boolean isRemoveButtonClickable = true, isProcessingClick = false;

    public DirectoryListAdapter(List<String> directoryList, Context context, JSONObject directoriesJsonObj, H2CO3GameHelper gameHelper, DirectoryFragment directoryFragment) {
        super(directoryList, context);
        this.directoriesJsonObj = directoriesJsonObj;
        this.gameHelper = gameHelper;
        this.directoryFragment = directoryFragment;
    }

    @SuppressLint({"ResourceAsColor", "UseCompatLoadingForDrawables"})
    @Override
    protected void bindData(BaseViewHolder holder, final int position) {
        TextView pathTextView = (TextView) holder.getView(R.id.tv_record);
        TextView nameTextView = (TextView) holder.getView(R.id.tv_name);
        MaterialCardView cardView = (MaterialCardView) holder.getView(R.id.ver_item);
        MaterialButton removeButton = (MaterialButton) holder.getView(R.id.tv_remove_dir);
        MaterialButton deleteButton = (MaterialButton) holder.getView(R.id.tv_del_dir);

        pathTextView.setSingleLine(true);
        nameTextView.setSingleLine(true);

        if (directoriesJsonObj == null) return;

        try {
            JSONObject directoryObject = directoriesJsonObj.getJSONArray("dirs").getJSONObject(position);
            String directoryPath = directoryObject.getString("path");
            String directoryName = directoryObject.getString("name");

            pathTextView.setText(directoryPath);
            nameTextView.setText(directoryName);
            configureCardView(cardView, directoryPath);
            configureButtons(position, removeButton, deleteButton, directoryPath);
        } catch (JSONException e) {
            H2CO3Tools.showError(mContext, "Error loading directory data.");
        }
    }

    private void configureCardView(MaterialCardView cardView, String directoryPath) {
        boolean isCurrentGameDir = directoryPath.equals(gameHelper.getGameDirectory());
        cardView.setStrokeWidth(isCurrentGameDir ? 11 : 3);
        cardView.setCheckable(!isCurrentGameDir);
        cardView.setOnClickListener(v -> {
            if (!isProcessingClick) {
                isProcessingClick = true;
                if (isCurrentGameDir) {
                    directoryFragment.verAdapter.updateData(directoryFragment.getVerList(directoryPath + "/versions"));
                } else {
                    setDirectory(directoryPath);
                    updateData(getDirList());
                }
                v.postDelayed(() -> isProcessingClick = false, 200);
            }
        });
    }

    private void configureButtons(int position, MaterialButton removeButton, MaterialButton deleteButton, String directoryPath) {
        boolean isH2CO3Directory = directoryPath.equals(h2co3DirectoryPath);
        removeButton.setVisibility(isH2CO3Directory ? View.GONE : View.VISIBLE);
        deleteButton.setVisibility(isH2CO3Directory ? View.GONE : View.VISIBLE);

        removeButton.setOnClickListener(view -> handleButtonClick(view, position, () -> showRemoveConfirmationDialog(position)));
        deleteButton.setOnClickListener(view -> handleButtonClick(view, position, () -> showDeleteConfirmationDialog(directoryPath, position)));
    }

    private void handleButtonClick(View view, int position, Runnable action) {
        if (isRemoveButtonClickable) {
            isRemoveButtonClickable = false;
            action.run();
            view.postDelayed(() -> isRemoveButtonClickable = true, 200);
        }
    }

    private void showRemoveConfirmationDialog(int position) {
        new MaterialAlertDialogBuilder(mContext)
                .setTitle(org.koishi.launcher.h2co3.library.R.string.title_action)
                .setMessage(org.koishi.launcher.h2co3.library.R.string.ver_if_remove)
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
                .setTitle(org.koishi.launcher.h2co3.library.R.string.title_action)
                .setMessage(org.koishi.launcher.h2co3.library.R.string.ver_if_del)
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    File directoryFile = new File(directoryPath);
                    if (directoryFile.exists()) {
                        H2CO3Application.sExecutorService.execute(() -> {
                            try {
                                FileTools.deleteDirectory(directoryFile);
                                ((H2CO3Activity) mContext).runOnUiThread(() -> mRvItemOnclickListener.RvItemOnclick(position));
                            } catch (IOException e) {
                                H2CO3Tools.showError(mContext, "Error deleting directory.");
                            }
                        });
                    }
                })
                .setNegativeButton("No", null)
                .create()
                .show();
    }

    private List<String> getDirList() {
        List<String> dirList = new ArrayList<>();
        try {
            JSONArray dirs = directoriesJsonObj.getJSONArray("dirs");
            for (int i = 0; i < dirs.length(); i++) {
                dirList.add(dirs.getJSONObject(i).getString("name"));
            }
        } catch (JSONException e) {
            directoryFragment.logError(e);
        }
        return dirList;
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
}