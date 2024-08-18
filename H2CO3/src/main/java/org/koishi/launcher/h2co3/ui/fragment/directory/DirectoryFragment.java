package org.koishi.launcher.h2co3.ui.fragment.directory;

import static org.koishi.launcher.h2co3.core.H2CO3Tools.MINECRAFT_DIR;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.adapter.DirectoryAdapter;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.game.h2co3launcher.H2CO3GameHelper;
import org.koishi.launcher.h2co3.core.utils.file.AssetsUtils;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.ui.fragment.H2CO3Fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DirectoryFragment extends H2CO3Fragment {

    private final String h2co3Directory = MINECRAFT_DIR;
    private MaterialAlertDialogBuilder dialogBuilder;
    private DirectoryAdapter dirAdapter;
    private String H2CO3Dir;
    private JSONObject dirsJsonObj;
    private H2CO3GameHelper gameHelper;
    private TextInputEditText nameEditText;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0 -> dialogBuilder.create().dismiss();
                case 1 -> {
                    dialogBuilder.create().dismiss();
                    addNewDirectory();
                }
                case 2 -> H2CO3Tools.showError(requireActivity(), getString(org.koishi.launcher.h2co3.resources.R.string.ver_add_done));
            }
        }
    };

    private RecyclerView dirRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameHelper = new H2CO3GameHelper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_directory, container, false);
        H2CO3Tools.loadPaths(requireContext());

        FloatingActionButton newDirButton = root.findViewById(R.id.ver_new_dir);
        newDirButton.setOnClickListener(v -> showDirDialog());
        dirRecyclerView = root.findViewById(R.id.mRecyclerView);
        initViews();
        return root;
    }

    private void initViews() {
        dirsJsonObj = getJsonObj();
        dirRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        dirAdapter = new DirectoryAdapter(getDirList(), requireActivity(), dirsJsonObj, gameHelper);
        dirAdapter.setRvItemOnclickListener(this::removeDirectory);
        ensureDefaultDirectory();
        dirRecyclerView.setAdapter(dirAdapter);
    }

    private void ensureDefaultDirectory() {
        if (!hasData(h2co3Directory)) {
            try {
                JSONObject defaultDir = new JSONObject();
                defaultDir.put("path", h2co3Directory);
                defaultDir.put("name", "Default Directory");
                dirsJsonObj.getJSONArray("dirs").put(defaultDir);
                saveJsonObj(dirsJsonObj);
                dirAdapter.update(getDirList());
            } catch (JSONException e) {
                logError(e);
            }
        }
    }

    private void addNewDirectory() {
        try {
            JSONObject newDir = new JSONObject();
            newDir.put("path", H2CO3Dir);
            newDir.put("name", nameEditText.getText().toString().trim());
            dirsJsonObj.getJSONArray("dirs").put(newDir);
            saveJsonObj(dirsJsonObj);
            dirAdapter.update(getDirList());
            H2CO3Tools.showError(requireActivity(), getString(org.koishi.launcher.h2co3.resources.R.string.ver_add_done));
        } catch (JSONException e) {
            logError(e);
        }
    }

    private void showDirDialog() {
        dialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.custom_dialog_directory, null);
        dialogBuilder.setView(dialogView).setTitle(org.koishi.launcher.h2co3.resources.R.string.add_directory);

        MaterialButton cancel = dialogView.findViewById(R.id.custom_dir_cancel);
        MaterialButton add = dialogView.findViewById(R.id.custom_dir_ok);
        TextInputLayout nameLay = dialogView.findViewById(R.id.dialog_dir_name_lay);
        nameEditText = dialogView.findViewById(R.id.dialog_dir_name);
        TextInputLayout pathLay = dialogView.findViewById(R.id.dialog_dir_path_lay);
        pathLay.setError(getString(org.koishi.launcher.h2co3.resources.R.string.ver_input_hint));
        add.setEnabled(false);
        TextInputEditText pathEditText = dialogView.findViewById(R.id.dialog_dir_path);
        pathEditText.addTextChangedListener(new DirectoryTextWatcher(pathLay, add));

        AlertDialog dialog = dialogBuilder.create();
        cancel.setOnClickListener(v -> dialog.dismiss());
        add.setOnClickListener(v -> handleAddDirectory(pathEditText, dialog));
        dialog.show();
    }

    private void handleAddDirectory(TextInputEditText pathEditText, AlertDialog dialog) {
        String path = pathEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();

        if (path.isEmpty() || name.isEmpty()) {
            H2CO3Tools.showError(requireActivity(), "Please input both path and name");
            return;
        }

        if (hasData(path) || isNameExists(name)) {
            H2CO3Tools.showError(requireActivity(), getString(org.koishi.launcher.h2co3.resources.R.string.ver_already_exists));
            return;
        }

        H2CO3Dir = path;
        newDir();
        dialog.dismiss();
    }

    private void newDir() {
        new Thread(() -> {
            try {
                AssetsUtils.extractZipFromAssets(requireActivity(), "pack.zip", H2CO3Dir);
                handler.sendEmptyMessage(1);
            } catch (IOException e) {
                H2CO3Tools.showError(requireActivity(), getString(org.koishi.launcher.h2co3.resources.R.string.ver_not_right_dir) + e);
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        String currentDir = gameHelper.getGameDirectory();
        File f = new File(currentDir);
        if (!f.exists() || !f.isDirectory()) {
            setNewDirButton(h2co3Directory);
            H2CO3Tools.showError(requireActivity(), getString(org.koishi.launcher.h2co3.resources.R.string.ver_null_dir));
            removeDir(currentDir);
            dirAdapter.update(getDirList());
        }
    }

    private void setNewDirButton(String newDirButton) {
        gameHelper.setGameDirectory(newDirButton);
        gameHelper.setGameAssets(newDirButton + "/assets/virtual/legacy");
        gameHelper.setGameAssetsRoot(newDirButton + "/assets");
        gameHelper.setGameCurrentVersion(newDirButton + "/versions");
    }

    private JSONObject getJsonObj() {
        try {
            File jsonFile = H2CO3Tools.DIRS_CONFIG_FILE;
            if (jsonFile.exists()) {
                String jsonStr = FileTools.readFileToString(jsonFile);
                return new JSONObject(jsonStr);
            } else {
                JSONObject jsonObj = createNewJsonObj();
                saveJsonObj(jsonObj);
                return jsonObj;
            }
        } catch (JSONException e) {
            logError(e);
        }
        return new JSONObject(); // Return an empty JSONObject instead of null
    }

    private JSONObject createNewJsonObj() {
        JSONObject jsonObj = new JSONObject();
        JSONArray dirs = new JSONArray();
        try {
            JSONObject defaultDir = new JSONObject();
            defaultDir.put("path", h2co3Directory);
            defaultDir.put("name", "Default Directory");
            dirs.put(defaultDir);
            jsonObj.put("dirs", dirs);
        } catch (JSONException e) {
            logError(e);
        }
        return jsonObj;
    }

    private void saveJsonObj(JSONObject jsonObj) {
        File jsonFile = H2CO3Tools.DIRS_CONFIG_FILE;
        FileTools.writeFile(jsonFile, jsonObj.toString());
    }

    private void removeDir(String dir) {
        try {
            JSONArray dirs = dirsJsonObj.getJSONArray("dirs");
            for (int i = 0; i < dirs.length(); i++) {
                JSONObject dirObj = dirs.getJSONObject(i);
                if (dirObj.getString("path").equals(dir)) {
                    dirs.remove(i);
                    saveJsonObj(dirsJsonObj);
                    break;
                }
            }
        } catch (JSONException e) {
            logError(e);
        }
    }
    private boolean hasData(String dir) {
        try {
            JSONArray dirs = dirsJsonObj.getJSONArray("dirs");
            for (int i = 0; i < dirs.length(); i++) {
                JSONObject dirObj = dirs.getJSONObject(i);
                if (dirObj.getString("path").equals(dir)) {
                    return true;
                }
            }
        } catch (JSONException e) {
            logError(e);
        }
        return false;
    }

    private void removeDirectory(int position) {
        try {
            JSONArray dirs = dirsJsonObj.getJSONArray("dirs");
            if (position >= 0 && position < dirs.length()) {
                dirs.remove(position);
                saveJsonObj(dirsJsonObj);
                dirAdapter.update(getDirList());
            }
        } catch (JSONException e) {
            logError(e);
        }
    }

    private boolean isNameExists(String name) {
        try {
            JSONArray dirs = dirsJsonObj.getJSONArray("dirs");
            for (int i = 0; i < dirs.length(); i++) {
                JSONObject dirObj = dirs.getJSONObject(i);
                if (dirObj.getString("name").equals(name)) {
                    return true;
                }
            }
        } catch (JSONException e) {
            logError(e);
        }
        return false;
    }

    private void logError(Exception e) {
        Log.e("DirectoryFragment", "Error: ", e);
    }

    private class DirectoryTextWatcher implements TextWatcher {
        private final TextInputLayout pathLay;
        private final MaterialButton addButton;

        public DirectoryTextWatcher(TextInputLayout pathLay, MaterialButton addButton) {
            this.pathLay = pathLay;
            this.addButton = addButton;
        }

        @Override
        public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {}

        @Override
        public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {}

        @Override
        public void afterTextChanged(Editable p1) {
            String value = p1.toString();
            if (value.matches("(/storage/emulated/0|/sdcard|/mnt/sdcard).*")) {
                pathLay.setErrorEnabled(false);
                addButton.setEnabled(true);
            } else {
                pathLay.setError(getString(org.koishi.launcher.h2co3.resources.R.string.ver_input_hint));
                addButton.setEnabled(false);
            }
        }
    }

    public List<String> getDirList() {
        List<String> dirList = new ArrayList<>();
        try {
            JSONArray dirs = dirsJsonObj.getJSONArray("dirs");
            for (int i = 0; i < dirs.length(); i++) {
                JSONObject dirObj = dirs.getJSONObject(i);
                dirList.add(dirObj.getString("name"));
            }
        } catch (JSONException e) {
            logError(e);
        }
        return dirList;
    }
}