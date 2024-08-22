package org.koishi.launcher.h2co3.resources.component.dialog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.koishi.launcher.h2co3.resources.R;
import org.koishi.launcher.h2co3.resources.component.adapter.H2CO3RecycleAdapter;
import org.koishi.launcher.h2co3.resources.component.dialog.model.ChooserFile;

import java.util.List;

public class FileAdapter extends H2CO3RecycleAdapter<ChooserFile> {

    private static final int TYPE_FILE = 1;
    private static final int TYPE_FOLDER = 2;
    private static final int TYPE_OPEN_FOLDER = 3;
    private final OnClickListener listener;

    public FileAdapter(List<ChooserFile> files, OnClickListener listener, Context context) {
        super(files, context);
        this.listener = listener;
    }

    @Override
    protected void bindData(BaseViewHolder holder, int position) {
        ChooserFile f = data.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.name.setText(f.name());
        viewHolder.size.setText(f.size());
        viewHolder.itemView.setOnClickListener(v -> listener.onClick(f));

        switch (getItemViewType(position)) {
            case TYPE_FILE:
                viewHolder.icon.setImageResource(R.drawable.ic_file);
                viewHolder.size.setVisibility(View.VISIBLE);
                break;
            case TYPE_FOLDER:
                viewHolder.icon.setImageResource(R.drawable.ic_folder);
                viewHolder.size.setVisibility(View.GONE);
                break;
            case TYPE_OPEN_FOLDER:
                viewHolder.icon.setImageResource(R.drawable.ic_folder_open);
                viewHolder.size.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.file_row;
    }

    @Override
    public int getItemViewType(int position) {
        ChooserFile f = data.get(position);

        if (f.file().isDirectory()) {
            if ("..".equals(f.name())) {
                return TYPE_OPEN_FOLDER;
            } else {
                return TYPE_FOLDER;
            }
        } else {
            return TYPE_FILE;
        }
    }

    public interface OnClickListener {
        void onClick(ChooserFile file);
    }

    static class ViewHolder extends BaseViewHolder {
        final TextView name;
        final ImageView icon;
        final TextView size;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            icon = itemView.findViewById(R.id.icon);
            size = itemView.findViewById(R.id.size);
        }
    }

    public void setFiles(List<ChooserFile> newFiles) {
        if (newFiles == null) return;

        int oldSize = data.size();
        data.clear();
        data.addAll(newFiles);
        int newSize = data.size();

        if (oldSize == 0) {
            notifyItemRangeInserted(0, newSize);
        } else {
            notifyItemRangeChanged(0, Math.min(oldSize, newSize));
            if (newSize > oldSize) {
                notifyItemRangeInserted(oldSize, newSize - oldSize);
            } else if (newSize < oldSize) {
                notifyItemRangeRemoved(newSize, oldSize - newSize);
            }
        }
    }
}