/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.ui.fragment.download;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChooseVersionFragment extends H2CO3Fragment {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private RecyclerView recyclerView;
    private VersionAdapter versionAdapter;
    private RadioGroup typeRadioGroup;
    private List<Version> versionList;
    private List<Version> filteredList;
    private LinearProgressIndicator progressIndicator;
    private NavController navController;
    private LinearLayoutCompat eMessageLayout;
    private AppCompatImageButton eMessageImageButton;
    private H2CO3TextView eMessageText;
    private final Handler handler = new Handler();
    private boolean run = false;
    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (run) {
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download_mc_choose_version, container, false);
        navController = Navigation.findNavController(requireParentFragment().requireView());
        initView(view);
        initListeners();
        versionList = new ArrayList<>();
        filteredList = new ArrayList<>();
        versionAdapter = new VersionAdapter(filteredList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(versionAdapter);

        eMessageImageButton.setOnClickListener(v -> {
            eMessageLayout.setVisibility(View.GONE);
            eMessageImageButton.setVisibility(View.GONE);
            progressIndicator.setVisibility(View.VISIBLE);
            fetchVersionsFromApi();
        });

        fetchVersionsFromApi();

        return view;
    }

    private void initView(View view) {
        eMessageLayout = view.findViewById(R.id.emessage_layout);
        eMessageText = view.findViewById(R.id.emessage_text);
        eMessageImageButton = view.findViewById(R.id.emessage_refresh_button);
        recyclerView = view.findViewById(R.id.loadingversionFileListView1);
        typeRadioGroup = view.findViewById(R.id.typeRadioGroup);
        progressIndicator = view.findViewById(R.id.progressIndicator);
    }

    private void initListeners() {
        typeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> filterVersions(checkedId));
    }

    private void fetchVersionsFromApi() {
        recyclerView.setAdapter(null);
        String apiUrl = "https://bmclapi2.bangbang93.com/mc/game/version_manifest_v2.json";
        fetchVersions(apiUrl);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterVersions(int checkedId) {
        filteredList.clear();
        for (Version version : versionList) {
            if ((checkedId == R.id.rb_release && version.versionType().equals("release")) ||
                    (checkedId == R.id.rb_snapshot && version.versionType().equals("snapshot")) ||
                    (checkedId == R.id.rb_old_beta && (version.versionType().equals("old_alpha") || version.versionType().equals("old_beta")))) {
                filteredList.add(version);
            }
        }
        versionAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(versionAdapter);
    }


    private void fetchVersions(String apiUrl) {
        executor.execute(() -> {
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5000);
                try (InputStream in = con.getInputStream();
                     BufferedReader bfr = new BufferedReader(new InputStreamReader(in))) {
                    StringBuilder str = new StringBuilder();
                    String temp;
                    while ((temp = bfr.readLine()) != null) {
                        str.append(temp).append("\n");
                    }
                    List<Version> versionList = getVersionList(str);
                    uiHandler.post(() -> {
                        this.versionList.clear();
                        this.versionList.addAll(versionList);
                        filterVersions(typeRadioGroup.getCheckedRadioButtonId());
                        progressIndicator.hide();
                    });
                }
            } catch (Exception e) {
                uiHandler.post(() -> {
                    eMessageLayout.setVisibility(View.VISIBLE);
                    eMessageText.setText(e.getMessage());
                    progressIndicator.hide();
                });
            }
        });

        run = true;
        handler.postDelayed(task, 1000);
    }


    @NotNull
    private List<Version> getVersionList(StringBuilder str) throws JSONException {
        final String message = str.toString();
        JSONObject jsonObject = new JSONObject(message);
        JSONArray versionsArray = jsonObject.getJSONArray("versions");
        List<Version> versionList = new ArrayList<>();
        for (int i = 0; i < versionsArray.length(); i++) {
            JSONObject versionObject = versionsArray.getJSONObject(i);
            String versionName = versionObject.getString("id");
            String versionType = versionObject.getString("type");
            String versionUrl = versionObject.getString("url");
            String versionSha1 = versionObject.getString("sha1");
            Version version = new Version(versionName, versionType, versionUrl, versionSha1);
            versionList.add(version);
        }
        return versionList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(task);
    }

    class VersionAdapter extends RecyclerView.Adapter<VersionAdapter.ViewHolder> {

        private final List<Version> versionList;

        public VersionAdapter(List<Version> versionList) {
            this.versionList = versionList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_version, parent, false);
            return new VersionAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VersionAdapter.ViewHolder holder, int position) {
            Version version = versionList.get(position);
            holder.versionNameTextView.setText(version.versionName());
            holder.versionTypeTextView.setText(version.versionType());
        }

        @Override
        public int getItemCount() {
            return versionList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final AtomicBoolean isHandlingClick = new AtomicBoolean(false);
            public TextView versionNameTextView;
            public TextView versionTypeTextView;
            public H2CO3CardView versionCardView;

            public ViewHolder(View itemView) {
                super(itemView);
                versionNameTextView = itemView.findViewById(R.id.id);
                versionTypeTextView = itemView.findViewById(R.id.type);
                versionCardView = itemView.findViewById(R.id.download_ver_item);
                versionCardView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (isHandlingClick.getAndSet(true)) {
                    return;
                }

                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Version version = versionList.get(position);

                    Bundle bundle = new Bundle();
                    bundle.putString("versionName", version.versionName());

                    navController.navigate(R.id.action_chooseVersionFragment_to_editVersionFragment, bundle);
                }

                isHandlingClick.set(false);
            }
        }
    }

    public record Version(String versionName, String versionType, String versionUrl,
                          String versionSha1) {
    }
}