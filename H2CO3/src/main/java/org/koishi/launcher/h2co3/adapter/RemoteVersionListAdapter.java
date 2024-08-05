package org.koishi.launcher.h2co3.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.download.RemoteVersion;
import org.koishi.launcher.h2co3.core.download.fabric.FabricAPIRemoteVersion;
import org.koishi.launcher.h2co3.core.download.fabric.FabricRemoteVersion;
import org.koishi.launcher.h2co3.core.download.forge.ForgeRemoteVersion;
import org.koishi.launcher.h2co3.core.download.game.GameRemoteVersion;
import org.koishi.launcher.h2co3.core.download.liteloader.LiteLoaderRemoteVersion;
import org.koishi.launcher.h2co3.core.download.neoforge.NeoForgeRemoteVersion;
import org.koishi.launcher.h2co3.core.download.optifine.OptiFineRemoteVersion;
import org.koishi.launcher.h2co3.core.download.quilt.QuiltAPIRemoteVersion;
import org.koishi.launcher.h2co3.core.download.quilt.QuiltRemoteVersion;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RemoteVersionListAdapter extends RecyclerView.Adapter<RemoteVersionListAdapter.ViewHolder> {

    private final ArrayList<RemoteVersion> list;
    private final OnRemoteVersionSelectListener listener;
    private final Context context;
    private static final Map<Class<? extends RemoteVersion>, Integer> iconMap = new HashMap<>();
    private static final Map<Class<? extends RemoteVersion>, Integer> tagMap = new HashMap<>();


    static {
        iconMap.put(LiteLoaderRemoteVersion.class, org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_liteloader);
        iconMap.put(OptiFineRemoteVersion.class, org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_optifine);
        iconMap.put(ForgeRemoteVersion.class, org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_forge);
        iconMap.put(NeoForgeRemoteVersion.class, org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_neoforge);
        iconMap.put(FabricRemoteVersion.class, org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_fabric);
        iconMap.put(FabricAPIRemoteVersion.class, org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_fabric);
        iconMap.put(QuiltRemoteVersion.class, org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_quilt);
        iconMap.put(QuiltAPIRemoteVersion.class, org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_quilt);
        iconMap.put(GameRemoteVersion.class, org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_mods);

        tagMap.put(GameRemoteVersion.class, org.koishi.launcher.h2co3.resources.R.string.download_release);
        tagMap.put(GameRemoteVersion.class, org.koishi.launcher.h2co3.resources.R.string.download_snapshot);
        tagMap.put(GameRemoteVersion.class, org.koishi.launcher.h2co3.resources.R.string.download_old_beta);
    }

    public RemoteVersionListAdapter(Context context, ArrayList<RemoteVersion> list, OnRemoteVersionSelectListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_remote_version, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RemoteVersion remoteVersion = list.get(position);
        holder.bind(remoteVersion);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnRemoteVersionSelectListener {
        void onSelect(RemoteVersion remoteVersion);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable getIcon(RemoteVersion remoteVersion) {
        Integer tagResId = iconMap.get(remoteVersion.getClass());
        return context.getDrawable(Objects.requireNonNullElse(tagResId, org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_mods));
    }

    private String getTag(RemoteVersion remoteVersion) {
        Integer tagResId = tagMap.get(remoteVersion.getClass());
        return tagResId != null ? context.getString(tagResId) : remoteVersion.getGameVersion();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        H2CO3CardView parent;
        AppCompatImageView icon;
        H2CO3TextView version;
        H2CO3TextView tag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            icon = itemView.findViewById(R.id.icon);
            version = itemView.findViewById(R.id.version);
            tag = itemView.findViewById(R.id.tag);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void bind(RemoteVersion remoteVersion) {
            parent.setOnClickListener(view -> listener.onSelect(remoteVersion));
            icon.setBackground(getIcon(remoteVersion));
            version.setText(remoteVersion.getSelfVersion());
            tag.setText(getTag(remoteVersion));
        }
    }
}