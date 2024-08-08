package org.koishi.launcher.h2co3.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DirectoryListAdapter extends BaseRecycleAdapter{

    public DirectoryListAdapter(List datas, Context mContext) {
        super(datas, mContext);
    }

    @Override
    protected void bindData(BaseViewHolder holder, int position) {

    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }
}
