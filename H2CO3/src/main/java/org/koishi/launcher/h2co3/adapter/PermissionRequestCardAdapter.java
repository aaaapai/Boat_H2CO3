package org.koishi.launcher.h2co3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.utils.bean.ui.PermissionRequestCard;

import java.util.List;

public class PermissionRequestCardAdapter extends RecyclerView.Adapter<PermissionRequestCardAdapter.ViewHolder> {

    private final Context context;
    private final List<PermissionRequestCard> permissionRequestCards;
    private final LayoutInflater inflater;

    public PermissionRequestCardAdapter(Context context, List<PermissionRequestCard> permissionRequestCards) {
        this.context = context;
        this.permissionRequestCards = permissionRequestCards;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_permission_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PermissionRequestCard permissionRequestCard = permissionRequestCards.get(position);
        holder.shapeableImageView.setImageResource(permissionRequestCard.getIconRes());
        holder.title.setText(permissionRequestCard.getTitleRes());
        holder.description.setText(permissionRequestCard.getDescriptionRes());
    }

    @Override
    public int getItemCount() {
        return permissionRequestCards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView shapeableImageView;
        public TextView title;
        public TextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            shapeableImageView = itemView.findViewById(R.id.shapeableImageView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }
    }
}