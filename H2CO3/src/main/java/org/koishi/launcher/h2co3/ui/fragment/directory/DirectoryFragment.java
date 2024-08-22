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
import org.koishi.launcher.h2co3.adapter.DirectoryListAdapter;
import org.koishi.launcher.h2co3.adapter.MCVersionListAdapter;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.game.h2co3launcher.H2CO3GameHelper;
import org.koishi.launcher.h2co3.core.utils.file.AssetsUtils;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.resources.component.H2CO3LinearProgress;
import org.koishi.launcher.h2co3.ui.fragment.H2CO3Fragment;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DirectoryFragment extends H2CO3Fragment {

    private final String h2co3Directory = MINECRAFT_DIR;
    private MaterialAlertDialogBuilder dialogBuilder;
    private DirectoryListAdapter dirAdapter;
    public MCVersionListAdapter verAdapter;
    private String H2CO3Dir;
    private JSONObject dirsJsonObj;
    private H2CO3GameHelper gameHelper;
    private TextInputEditText nameEditText;

    private static final int MSG_DIALOG_DISMISS = 0;
    private static final int MSG_ADD_NEW_DIRECTORY = 1;
    private static final int MSG_SHOW_ERROR = 2;


    private H2CO3LinearProgress dirProgressBar, verProgressBar;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0 -> dialogBuilder.create().dismiss();
                case 1 -> {
                    dialogBuilder.create().dismiss();
                    addNewDirectory();
                }
                case 2 ->
                        H2CO3Tools.showError(requireActivity(), getString(org.koishi.launcher.h2co3.resources.R.string.ver_add_done));
            }
        }
    };

    private RecyclerView dirRecyclerView;
    public RecyclerView verRecyclerView;

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
        dirRecyclerView = root.findViewById(R.id.dirsRecyclerView);
        verRecyclerView = root.findViewById(R.id.versRecyclerView);
        dirProgressBar = root.findViewById(R.id.dirProgressBar);
        verProgressBar = root.findViewById(R.id.verProgressBar);
        initViews();
        return root;
    }

    private void initViews() {
        verProgressBar.setVisibility(View.VISIBLE);
        dirProgressBar.setVisibility(View.VISIBLE);

        executorService.execute(() -> {
            dirsJsonObj = getJsonObj();
            List<String> dirList = getDirList();

            requireActivity().runOnUiThread(() -> {
                dirRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
                dirAdapter = new DirectoryListAdapter(dirList, requireActivity(), dirsJsonObj, gameHelper, this);
                dirAdapter.setRvItemOnclickListener(this::removeOrDeleteDirectory);
                ensureDefaultDirectory();
                dirRecyclerView.setAdapter(dirAdapter);
                updateVerList(gameHelper.getGameDirectory() + "/versions");
                verProgressBar.setVisibility(View.GONE);
                dirProgressBar.setVisibility(View.GONE);
            });
        });
    }

    private void ensureDefaultDirectory() {
        executorService.execute(() -> {
            if (!hasData(h2co3Directory)) {
                try {
                    JSONObject defaultDir = new JSONObject();
                    defaultDir.put("path", h2co3Directory);
                    defaultDir.put("name", "Default Directory");
                    dirsJsonObj.getJSONArray("dirs").put(defaultDir);
                    saveJsonObj(dirsJsonObj);
                    requireActivity().runOnUiThread(() -> dirAdapter.updateData(getDirList()));
                } catch (JSONException e) {
                    logError(e);
                }
            }
        });
    }

    private void addNewDirectory() {
        executorService.execute(() -> {
            try {
                if (nameEditText == null) return; // 防止空指针异常
                JSONObject newDir = new JSONObject();
                newDir.put("path", H2CO3Dir);
                newDir.put("name", nameEditText.getText().toString().trim());
                dirsJsonObj.getJSONArray("dirs").put(newDir);
                saveJsonObj(dirsJsonObj);
                requireActivity().runOnUiThread(() -> dirAdapter.updateData(getDirList()));
                H2CO3Tools.showError(requireActivity(), getString(org.koishi.launcher.h2co3.resources.R.string.ver_add_done));
            } catch (JSONException e) {
                logError(e);
            }
        });
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
        dirAdapter.updateData(getDirList());
        dialog.dismiss();
    }

    private void newDir() {
        executorService.execute(() -> {
            try {
                AssetsUtils.extractZipFromAssets(requireActivity(), "pack.zip", H2CO3Dir);
                handler.sendEmptyMessage(1);
            } catch (IOException e) {
                H2CO3Tools.showError(requireActivity(), getString(org.koishi.launcher.h2co3.resources.R.string.ver_not_right_dir) + e);
                handler.sendEmptyMessage(0);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        String currentDir = gameHelper.getGameDirectory();
        File f = new File(currentDir);
        if (dirAdapter != null) {
            dirAdapter.updateData(getDirList());
        }
        if (!f.exists() || !f.isDirectory()) {
            setNewDir(h2co3Directory);
            H2CO3Tools.showError(requireActivity(), getString(org.koishi.launcher.h2co3.resources.R.string.ver_null_dir));
            removeDir(currentDir);
            if (dirAdapter != null) {
                dirAdapter.updateData(getDirList());
            }
        }
    }

    private void setNewDir(String newDir) {
        gameHelper.setGameDirectory(newDir);
        gameHelper.setGameAssets(newDir + "/assets/virtual/legacy");
        gameHelper.setGameAssetsRoot(newDir + "/assets");
        gameHelper.setGameCurrentVersion(newDir + "/versions");
        updateVerList(newDir + "/versions");
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
        return new JSONObject();
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
        this.dirsJsonObj = jsonObj;
    }

    private void removeDir(String dir) {
        executorService.execute(() -> {
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
        });
    }

    private boolean hasData(String dir) {
        return checkDirExists(dir, "path");
    }

    private boolean isNameExists(String name) {
        return checkDirExists(name, "name");
    }

    private boolean checkDirExists(String value, String key) {
        try {
            JSONArray dirs = dirsJsonObj.getJSONArray("dirs");
            for (int i = 0; i < dirs.length(); i++) {
                JSONObject dirObj = dirs.getJSONObject(i);
                if (dirObj.getString(key).equals(value)) {
                    return true;
                }
            }
        } catch (JSONException e) {
            logError(e);
        }
        return false;
    }

    private void removeOrDeleteDirectory(int position) {
        executorService.execute(() -> {
            try {
                JSONArray dirs = dirsJsonObj.getJSONArray("dirs");
                if (position >= 0 && position < dirs.length()) {
                    dirs.remove(position);
                    saveJsonObj(dirsJsonObj);
                    requireActivity().runOnUiThread(() -> {
                        dirAdapter.remove(position);
                    });
                }
            } catch (JSONException e) {
                logError(e);
            }
        });
    }

    public void logError(Exception e) {
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
        public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
        }

        @Override
        public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
        }

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

    private List<String> getDirList() {
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


    public void updateVerList(String path) {
        executorService.execute(() -> {
            File directory = new File(path);
            if (directory.isDirectory()) {
                String[] filesArray = directory.list();
                if (filesArray != null) {
                    List<String> files = Arrays.asList(filesArray);
                    files.sort(Collator.getInstance(Locale.CHINA)::compare);
                    requireActivity().runOnUiThread(() -> {
                        verRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
                        verAdapter = new MCVersionListAdapter(requireActivity(), files, this, gameHelper, path);
                        verRecyclerView.setAdapter(verAdapter);
                    });
                } else {
                    requireActivity().runOnUiThread(() -> verRecyclerView.setAdapter(null));
                }
            } else {
                requireActivity().runOnUiThread(() -> verRecyclerView.setAdapter(null));
            }
        });
    }
}