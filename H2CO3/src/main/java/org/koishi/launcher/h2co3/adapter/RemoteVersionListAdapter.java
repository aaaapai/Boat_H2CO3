package org.koishi.launcher.h2co3.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.game.download.RemoteVersion;
import org.koishi.launcher.h2co3.core.game.download.fabric.FabricAPIRemoteVersion;
import org.koishi.launcher.h2co3.core.game.download.fabric.FabricRemoteVersion;
import org.koishi.launcher.h2co3.core.game.download.forge.ForgeRemoteVersion;
import org.koishi.launcher.h2co3.core.game.download.liteloader.LiteLoaderRemoteVersion;
import org.koishi.launcher.h2co3.core.game.download.neoforge.NeoForgeRemoteVersion;
import org.koishi.launcher.h2co3.core.game.download.optifine.OptiFineRemoteVersion;
import org.koishi.launcher.h2co3.core.game.download.quilt.QuiltAPIRemoteVersion;
import org.koishi.launcher.h2co3.core.game.download.quilt.QuiltRemoteVersion;
import org.koishi.launcher.h2co3.core.game.download.vanilla.GameRemoteVersion;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;
import org.koishi.launcher.h2co3.resources.component.adapter.H2CO3RecycleAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteVersionListAdapter extends H2CO3RecycleAdapter<RemoteVersion> {

    private final OnRemoteVersionSelectListener listener;
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

    public RemoteVersionListAdapter(Context context, List<RemoteVersion> list, OnRemoteVersionSelectListener listener) {
        super(list, context);
        this.listener = listener;
    }

    @Override
    protected void bindData(BaseViewHolder holder, int position) {
        if (holder instanceof ViewHolder viewHolder) {
            RemoteVersion remoteVersion = data.get(position);
            viewHolder.bind(remoteVersion);
        } else {
            throw new ClassCastException("Invalid ViewHolder type");
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_remote_version;
    }

    private Drawable getIcon(RemoteVersion remoteVersion) {
        Integer tagResId = iconMap.get(remoteVersion.getClass());
        return ContextCompat.getDrawable(mContext, tagResId != null ? tagResId : org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_mods);
    }

    private String getTag(RemoteVersion remoteVersion) {
        Integer tagResId = tagMap.get(remoteVersion.getClass());
        return tagResId != null ? mContext.getString(tagResId) : remoteVersion.getGameVersion();
    }

    public interface OnRemoteVersionSelectListener {
        void onSelect(RemoteVersion remoteVersion);
    }

    public class ViewHolder extends BaseViewHolder {
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
            parent.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onSelect(data.get(position));
                }
            });
        }

        public void bind(RemoteVersion remoteVersion) {
            icon.setBackground(getIcon(remoteVersion));
            version.setText(remoteVersion.getSelfVersion());
            tag.setText(getTag(remoteVersion));
        }
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_remote_version, parent, false);
        return new ViewHolder(view);
    }
}