
/*
 * //
 * // Created by cainiaohh on 2024-04-04.
 * //
 */

package org.koishi.launcher.h2co3.ui.fragment.directory;

import static org.koishi.launcher.h2co3.core.H2CO3Tools.MINECRAFT_DIR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.adapter.BaseRecycleAdapter;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.h2co3launcher.utils.H2CO3GameHelper;
import org.koishi.launcher.h2co3.core.utils.file.AssetsUtils;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class DirectoryFragment extends H2CO3Fragment {

    private final String h2co3Directory = MINECRAFT_DIR;
    private VersionRecyclerAdapter verAdapter;
    private MaterialAlertDialogBuilder dialogBuilder;
    private DirectoryAdapter dirAdapter;
    private String H2CO3Dir;
    private JSONObject dirsJsonObj;
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0 -> dialogBuilder.create().dismiss();
                case 1 -> {
                    dialogBuilder.create().dismiss();
                    try {
                        dirsJsonObj.getJSONArray("dirs").put(H2CO3Dir);
                        saveJsonObj(dirsJsonObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dirAdapter.updata(getDirList());
                    H2CO3Tools.showError(requireActivity(), getString(org.koishi.launcher.h2co3.resources.R.string.ver_add_done));
                }
                case 2 ->
                        H2CO3Tools.showError(requireActivity(), getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_add_done));
            }
        }
    };
    private RecyclerView dirRecyclerView, verRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_directory, container, false);

        H2CO3Tools.loadPaths(requireContext());

        FloatingActionButton newDirButton = root.findViewById(R.id.ver_new_dir);
        newDirButton.setOnClickListener(v -> showDirDialog());
        FloatingActionButton newVerButton = root.findViewById(R.id.ver_new_ver);
        dirRecyclerView = root.findViewById(R.id.mRecyclerView);
        verRecyclerView = root.findViewById(R.id.mVerRecyclerView);
        initViews();
        initVer();
        newDirButton.show();
        newDirButton.show();
        return root;
    }

    public void initViews() {
        dirsJsonObj = getJsonObj();
        dirRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        dirAdapter = new DirectoryAdapter(getDirList(), requireActivity());
        dirAdapter.setRvItemOnclickListener(position -> {
            try {
                JSONArray dirs = dirsJsonObj.getJSONArray("dirs");
                dirs.remove(position);
                saveJsonObj(dirsJsonObj);
                dirAdapter.updata(getDirList());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        if (!hasData(h2co3Directory)) {
            try {
                dirsJsonObj.getJSONArray("dirs").put(h2co3Directory);
                saveJsonObj(dirsJsonObj);
                dirAdapter.updata(getDirList());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        dirRecyclerView.setAdapter(dirAdapter);
    }

    public void initVer() {
        File versionlist = new File(H2CO3GameHelper.getGameDirectory() + "/versions");

        if (versionlist.isDirectory() && versionlist.exists()) {

            try (Stream<Path> paths = Files.list(versionlist.toPath())) {

                List<String> verList = paths
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .collect(Collectors.toList());

                verRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

                verAdapter = new VersionRecyclerAdapter(requireContext(), verList);

                verRecyclerView.setAdapter(verAdapter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            verRecyclerView.setAdapter(null);
        }
    }

    public void showDirDialog() {
        dialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.custom_dialog_directory, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(org.koishi.launcher.h2co3.resources.R.string.add_directory);

        MaterialButton cancel = dialogView.findViewById(R.id.custom_dir_cancel);
        MaterialButton add = dialogView.findViewById(R.id.custom_dir_ok);
        TextInputLayout nameLay = dialogView.findViewById(R.id.dialog_dir_name_lay);
        TextInputEditText nameEditText = dialogView.findViewById(R.id.dialog_dir_name);
        TextInputLayout pathLay = dialogView.findViewById(R.id.dialog_dir_path_lay);
        pathLay.setError(getString(org.koishi.launcher.h2co3.resources.R.string.ver_input_hint));
        add.setEnabled(false);
        TextInputEditText pathEditText = dialogView.findViewById(R.id.dialog_dir_path);
        pathEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
            }

            @Override
            public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
            }

            @Override
            public void afterTextChanged(Editable p1) {
                String value = Objects.requireNonNull(pathEditText.getText()).toString();
                if (value.matches("(/storage/emulated/0|/sdcard|/mnt/sdcard).*")) {
                    pathLay.setErrorEnabled(false);
                    add.setEnabled(true);
                } else {
                    pathLay.setError(getString(org.koishi.launcher.h2co3.resources.R.string.ver_input_hint));
                    add.setEnabled(false);
                }
            }
        });

        AlertDialog dialog = dialogBuilder.create();

        cancel.setOnClickListener(v -> dialog.dismiss());
        add.setOnClickListener(v -> {
            if (!Objects.requireNonNull(pathEditText.getText()).toString().trim().isEmpty()) {
                boolean hasData = hasData(pathEditText.getText().toString().trim());
                if (!hasData) {
                    File f = new File(pathEditText.getText().toString().trim());
                    if (f.exists()) {
                        if (f.isDirectory()) {
                            H2CO3Dir = pathEditText.getText().toString();
                            newDir();
                        } else {
                            H2CO3Tools.showError(requireActivity(), getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_not_dir));
                        }
                    } else {
                        H2CO3Dir = pathEditText.getText().toString();
                        newDir();
                    }
                } else {
                    H2CO3Tools.showError(requireActivity(), getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_already_exists));
                }
                dirAdapter.updata(getDirList());
            } else {
                H2CO3Tools.showError(requireActivity(), "Please input");
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    public void newDir() {
        new Thread(() -> {
            try {
                AssetsUtils.extractZipFromAssets(requireActivity(), "pack.zip", H2CO3Dir);
                handler.sendEmptyMessage(1);
            } catch (IOException e) {
                H2CO3Tools.showError(requireActivity(), getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_not_right_dir) + e);
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        String currentDir = H2CO3GameHelper.getGameDirectory();
        File f = new File(currentDir);
        if (f.exists() && f.isDirectory()) {
            initVer();
        } else {
            setNewDirButton(h2co3Directory);
            H2CO3Tools.showError(requireActivity(), getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_null_dir));
            removeDir(currentDir);
            dirAdapter.updata(getDirList());
            initVer();
        }
    }

    public void setNewDirButton(String newDirButton) {
        H2CO3GameHelper.setGameDirectory(newDirButton);
        H2CO3GameHelper.setGameAssets(newDirButton + "/assets/virtual/legacy");
        H2CO3GameHelper.setGameAssetsRoot(newDirButton + "/assets");
        H2CO3GameHelper.setGameCurrentVersion(newDirButton + "/versions");
    }

    private JSONObject getJsonObj() {
        JSONObject jsonObj = null;
        try {
            File jsonFile = H2CO3Tools.DIRS_CONFIG_FILE;
            if (jsonFile.exists()) {
                String jsonStr = FileTools.readFileToString(H2CO3Tools.DIRS_CONFIG_FILE);
                jsonObj = new JSONObject(jsonStr);
            } else {
                jsonObj = createNewJsonObj();
                saveJsonObj(jsonObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jsonObj;
    }

    private JSONObject createNewJsonObj() {
        JSONObject jsonObj = new JSONObject();
        JSONArray dirs = new JSONArray();
        dirs.put(h2co3Directory);
        try {
            jsonObj.put("dirs", dirs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    private void saveJsonObj(JSONObject jsonObj) {
        File jsonFile = H2CO3Tools.DIRS_CONFIG_FILE;
        FileTools.writeFile(jsonFile, jsonObj.toString());
    }

    private List<String> getDirList() {
        List<String> dirList = new ArrayList<>();
        if (dirsJsonObj != null) {
            try {
                JSONArray dirs = dirsJsonObj.getJSONArray("dirs");
                for (int i = 0; i < dirs.length(); i++) {
                    dirList.add(dirs.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dirList;
    }

    public void removeDir(String dir) {
        if (dirsJsonObj != null) {
            try {
                JSONArray dirs = dirsJsonObj.getJSONArray("dirs");
                for (int i = 0; i < dirs.length(); i++) {
                    if (dirs.getString(i).equals(dir)) {
                        dirs.remove(i);
                        saveJsonObj(dirsJsonObj);
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean hasData(String dir) {
        if (dirsJsonObj != null) {
            try {
                JSONArray dirs = dirsJsonObj.getJSONArray("dirs");
                for (int i = 0; i < dirs.length(); i++) {
                    if (dirs.getString(i).equals(dir)) {
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    class VersionRecyclerAdapter extends RecyclerView.Adapter<VersionRecyclerAdapter.MyViewHolder> {
        private final List<String> datas;
        private final LayoutInflater inflater;

        public VersionRecyclerAdapter(Context context, List<String> datas) {
            inflater = LayoutInflater.from(context);
            this.datas = datas;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams") View itemView = inflater.inflate(R.layout.item_version_local, null);
            return new MyViewHolder(itemView);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.textview.setText(datas.get(position));
            File f = new File(H2CO3GameHelper.getGameDirectory() + "/versions/" + datas.get(position));
            String verF = H2CO3GameHelper.getGameDirectory() + "/versions/" + datas.get(position);
            if (verF.equals(H2CO3GameHelper.getGameCurrentVersion())) {
                holder.rl.setStrokeWidth(13);
            } else {
                holder.rl.setStrokeWidth(3);
            }
            if (f.isDirectory() && f.exists()) {
            } else {
                holder.rl.setEnabled(false);
                holder.ic.setImageDrawable(getResources().getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.xicon));
            }
            holder.rl.setOnClickListener(v -> {
                holder.dirs = datas.get(position);
                showExecDialog(holder.dirs);
            });

            if (holder.rl.getTag() == null) {
                holder.rl.setTag(true);
                holder.rl.setOnClickListener(v -> {
                    if (f.exists() && f.isDirectory()) {
                        verAdapter.notifyItemChanged(position);
                        H2CO3GameHelper.setGameCurrentVersion(verF);
                        verRecyclerView.setAdapter(verAdapter);
                        if (verF.equals(H2CO3GameHelper.getGameCurrentVersion())) {
                            holder.rl.setStrokeWidth(13);
                        } else {
                            holder.rl.setStrokeWidth(3);
                        }
                    }
                });
            }

            holder.btn.setOnClickListener(v -> {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
                alertDialogBuilder.setTitle(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.title_action));
                alertDialogBuilder.setMessage(org.koishi.launcher.h2co3.resources.R.string.ver_if_del);
                alertDialogBuilder.setPositiveButton("Yes Yes Yes", (dialogInterface, i) -> {
                    holder.btn.setVisibility(View.INVISIBLE);
                    holder.textview.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                    holder.rl.setEnabled(false);
                    File f1 = new File(H2CO3GameHelper.getGameDirectory() + "/versions/" + datas.get(position));
                    new Thread(() -> {
                        if (f1.isDirectory()) {
                            try {
                                FileTools.deleteDirectory(f1);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            deleteFile(H2CO3GameHelper.getGameDirectory() + "/versions/" + datas.get(position));
                        }
                        handler.sendEmptyMessage(2);
                    }).start();
                });
                alertDialogBuilder.setNegativeButton("No No No", (dialogInterface, i) -> {
                });
                AlertDialog alertDialog1 = alertDialogBuilder.create();
                alertDialog1.show();
            });
        }

        public void showExecDialog(String dir) {
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        public void deleteFile(String filePath) {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                file.delete();
            }
        }

        static class MyViewHolder extends RecyclerView.ViewHolder {
            private final TextView textview;
            private final MaterialButton btn;
            private final ImageView ic;
            private final MaterialCardView rl;
            private String dirs;

            public MyViewHolder(View itemView) {
                super(itemView);
                textview = itemView.findViewById(R.id.ver_name);
                btn = itemView.findViewById(R.id.ver_remove);
                rl = itemView.findViewById(R.id.ver_item);
                ic = itemView.findViewById(R.id.ver_icon);
            }
        }
    }

    class DirectoryAdapter extends BaseRecycleAdapter<String> {
        public DirectoryAdapter(List<String> datas, Context mContext) {
            super(datas, mContext);
        }

        @SuppressLint({"ResourceAsColor", "UseCompatLoadingForDrawables"})
        @Override
        protected void bindData(BaseViewHolder holder, final int position) {

            TextView textView = (TextView) holder.getView(R.id.tv_record);
            TextView textView1 = (TextView) holder.getView(R.id.tv_name);
            MaterialCardView lay = (MaterialCardView) holder.getView(R.id.ver_item);
            ImageView check = (ImageView) holder.getView(R.id.ver_check_icon);
            MaterialButton del = (MaterialButton) holder.getView(R.id.tv_remove_dir);
            MaterialButton delDir = (MaterialButton) holder.getView(R.id.tv_del_dir);
            textView.setText(datas.get(position));
            File f = new File(textView.getText().toString());
            if (f.isDirectory() && f.exists()) {
            } else {
                check.setImageDrawable(getResources().getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.xicon));
                delDir.setVisibility(View.VISIBLE);
            }
            if (datas.get(position).equals(H2CO3GameHelper.getGameDirectory())) {
                lay.setStrokeWidth(11);
                lay.setOnClickListener(null);
            } else {
                lay.setStrokeWidth(3);
                lay.setOnClickListener(new View.OnClickListener() {

                    /**
                     * @param v The view that was clicked.
                     */
                    @Override
                    public void onClick(View v) {
                        if (f.exists() && f.isDirectory()) {
                            setDir(textView.getText().toString());
                            dirAdapter.updata(getDirList());
                            verRecyclerView.setAdapter(null);
                            initVer();
                        } else {
                            if (null != mRvItemOnclickListener) {
                                mRvItemOnclickListener.RvItemOnclick(position);
                                dirAdapter.updata(getDirList());
                                H2CO3Tools.showError(requireActivity(), getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_null_dir));
                                verRecyclerView.setAdapter(null);
                            }
                        }
                    }
                });
            }
            if (datas.get(position).equals(h2co3Directory)) {
                del.setVisibility(View.GONE);
                delDir.setVisibility(View.GONE);
            } else {
                del.setVisibility(View.VISIBLE);
                delDir.setVisibility(View.VISIBLE);
            }

            String str1 = textView.getText().toString();
            str1 = str1.substring(0, str1.lastIndexOf("/"));
            int idx = str1.lastIndexOf("/");
            str1 = str1.substring(idx + 1).toUpperCase();
            textView1.setText(str1);
            del.setOnClickListener(view -> {
                if (null != mRvItemOnclickListener) {
                    mRvItemOnclickListener.RvItemOnclick(position);
                }
            });

            delDir.setOnClickListener(view -> {
                if (null != mRvItemOnclickListener) {
                    if (datas.get(position).equals(H2CO3GameHelper.getGameDirectory())) {
                        setDir(h2co3Directory);
                    }
                    AlertDialog alertDialog1 = new MaterialAlertDialogBuilder(requireActivity())
                            .setTitle(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.title_action))
                            .setMessage(org.koishi.launcher.h2co3.resources.R.string.ver_if_del)
                            .setPositiveButton("Yes Yes Yes", (dialogInterface, i) -> {
                                File f1 = new File(datas.get(position));
                                mRvItemOnclickListener.RvItemOnclick(position);
                                dirAdapter.updata(getDirList());
                                new Thread(() -> {
                                    try {
                                        FileTools.deleteDirectory(f1);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }).start();

                            })
                            .setNegativeButton("No No No", (dialogInterface, i) -> {
                            })
                            .create();

                    alertDialog1.show();
                }
            });

        }

        @Override
        public int getLayoutId() {
            return R.layout.item_dir;
        }

        public void setDir(String dir) {
            H2CO3GameHelper.setGameDirectory(dir);
            H2CO3GameHelper.setGameAssets(dir + "/assets/virtual/legacy");
            H2CO3GameHelper.setGameAssetsRoot(dir + "/assets");
            H2CO3GameHelper.setGameCurrentVersion(dir + "/versions");
        }
    }
}