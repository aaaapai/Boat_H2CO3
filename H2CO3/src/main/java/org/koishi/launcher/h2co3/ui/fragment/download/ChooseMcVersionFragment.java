/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.ui.fragment.download;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.ui.fragment.H2CO3Fragment;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChooseMcVersionFragment extends H2CO3Fragment {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private RecyclerView recyclerView;
    private VersionAdapter versionAdapter;
    private RadioGroup typeRadioGroup;
    private final List<Version> versionList = new ArrayList<>();
    private final List<Version> filteredList = new ArrayList<>();
    private LinearProgressIndicator progressIndicator;
    private LinearLayoutCompat eMessageLayout;
    private AppCompatImageButton eMessageImageButton;
    private H2CO3TextView eMessageText;
    private volatile boolean isFetching = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download_mc_choose_version, container, false);
        if (view == null) return null; // Check for null
        initView(view);
        initListeners();
        versionAdapter = new VersionAdapter(filteredList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(versionAdapter);
        eMessageImageButton.setOnClickListener(v -> refreshVersions());
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

    private void refreshVersions() {
        eMessageLayout.setVisibility(View.GONE);
        eMessageImageButton.setVisibility(View.GONE);
        progressIndicator.setVisibility(View.VISIBLE);
        fetchVersionsFromApi();
    }

    private void fetchVersionsFromApi() {
        if (isFetching) return;
        isFetching = true;
        String apiUrl = "https://bmclapi2.bangbang93.com/mc/game/version_manifest_v2.json";
        fetchVersions(apiUrl);
    }

    private void fetchVersions(String apiUrl) {
        executor.execute(() -> {
            HttpURLConnection con = null;
            try {
                URL url = new URL(apiUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5000);
                if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + con.getResponseCode());
                }
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
                        isFetching = false;
                    });
                }
            } catch (Exception e) {
                uiHandler.post(() -> {
                    eMessageLayout.setVisibility(View.VISIBLE);
                    eMessageText.setText(e.getMessage());
                    progressIndicator.hide();
                    isFetching = false;
                });
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
        });
    }

    private void filterVersions(int checkedId) {
        filteredList.clear();
        for (Version version : versionList) {
            String versionType = version.versionType();
            if ((checkedId == R.id.rb_release && "release".equals(versionType)) ||
                    (checkedId == R.id.rb_snapshot && "snapshot".equals(versionType)) ||
                    (checkedId == R.id.rb_old_beta && ("old_alpha".equals(versionType) || "old_beta".equals(versionType)))) {
                filteredList.add(version);
            }
        }
        versionAdapter.notifyDataSetChanged();
    }

    @NotNull
    private List<Version> getVersionList(StringBuilder str) throws JSONException {
        JSONObject jsonObject = new JSONObject(str.toString());
        JSONArray versionsArray = jsonObject.getJSONArray("versions");
        List<Version> versionList = new ArrayList<>();
        for (int i = 0; i < versionsArray.length(); i++) {
            JSONObject versionObject = versionsArray.getJSONObject(i);
            versionList.add(new Version(versionObject.getString("id"), versionObject.getString("type"),
                    versionObject.getString("url"), versionObject.getString("sha1")));
        }
        return versionList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
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
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Version version = versionList.get(position);
            holder.versionNameTextView.setText(version.versionName());
            holder.versionTypeTextView.setText(version.versionType());
        }

        @Override
        public int getItemCount() {
            return versionList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Version version = versionList.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("versionName", version.versionName());
                    EditVersionFragment editVersionFragment = new EditVersionFragment(ChooseMcVersionFragment.this, bundle);
                    getParentFragmentManager().beginTransaction()
                            .setCustomAnimations(org.koishi.launcher.h2co3.resources.R.anim.fragment_enter, org.koishi.launcher.h2co3.resources.R.anim.fragment_exit, org.koishi.launcher.h2co3.resources.R.anim.fragment_enter_pop, org.koishi.launcher.h2co3.resources.R.anim.fragment_exit_pop)
                            .add(R.id.fragmentContainerView, editVersionFragment)
                            .hide(ChooseMcVersionFragment.this)
                            .commit();
                }
            }
        }
    }

    public record Version(String versionName, String versionType, String versionUrl, String versionSha1) {}
}