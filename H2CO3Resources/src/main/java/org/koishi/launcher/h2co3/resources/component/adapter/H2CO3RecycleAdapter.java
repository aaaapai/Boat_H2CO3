package org.koishi.launcher.h2co3.resources.component.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class H2CO3RecycleAdapter<T> extends RecyclerView.Adapter<H2CO3RecycleAdapter.BaseViewHolder> {

    public static final int ITEM_TYPE_NORMAL = 0X1111;
    public static final int ITEM_TYPE_HEADER = 0X1112;
    public static final int ITEM_TYPE_FOOTER = 0X1113;

    protected final Context mContext;
    protected List<T> data;
    protected RvItemOnclickListener mRvItemOnclickListener;

    private View mHeaderView;
    private View mFooterView;
    private boolean isHasHeader = false;
    private boolean isHasFooter = false;

    public H2CO3RecycleAdapter(List<T> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_FOOTER) {
            return new BaseViewHolder(mFooterView);
        }
        if (viewType == ITEM_TYPE_HEADER) {
            return new BaseViewHolder(mHeaderView);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(), parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, final int position) {
        int dataPosition = position - (isHasHeader ? 1 : 0);
        if (isHasFooter && position >= data.size() + (isHasHeader ? 1 : 0)) {
            return;
        }

        if (dataPosition >= 0 && dataPosition < data.size()) {
            bindData(holder, dataPosition);
            //holder.itemView.setAlpha(0f);
           // holder.itemView.animate().alpha(1f).setDuration(100).start();
        }
    }

    public void setHeaderView(View header) {
        if (header == null) return;
        this.mHeaderView = header;
        isHasHeader = true;
        notifyItemInserted(0);
    }

    public void setFooterView(View footer) {
        if (footer == null) return;
        this.mFooterView = footer;
        isHasFooter = true;
        notifyItemInserted(getItemCount() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (isHasHeader && position == 0) {
            return ITEM_TYPE_HEADER;
        }
        if (isHasFooter && position == data.size() + (isHasHeader ? 1 : 0)) {
            return ITEM_TYPE_FOOTER;
        }
        return ITEM_TYPE_NORMAL;
    }

    public void updateData(List<T> newData) {
        if (newData == null || newData.isEmpty()) return;
        this.data.clear();
        this.data.addAll(newData);
        notifyItemRangeChanged(0, newData.size());
    }

    public void addData(List<T> newData) {
        if (newData == null || newData.isEmpty()) return;
        int startPosition = this.data.size();
        this.data.addAll(newData);
        notifyItemRangeInserted(startPosition, newData.size());
    }

    public void remove(int position) {
        if (position < 0 || position >= data.size()) return;
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size() - position - 1);
    }

    protected abstract void bindData(BaseViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return data.size() + (isHasHeader ? 1 : 0) + (isHasFooter ? 1 : 0);
    }

    public abstract int getLayoutId();

    public void setItemText(TextView view, String text) {
        if (view != null) {
            view.setText(text);
        }
    }

    public RvItemOnclickListener getRvItemOnclickListener() {
        return mRvItemOnclickListener;
    }

    public void setRvItemOnclickListener(RvItemOnclickListener rvItemOnclickListener) {
        mRvItemOnclickListener = rvItemOnclickListener;
    }

    public interface RvItemOnclickListener {
        void RvItemOnclick(int position);
    }

    public static class BaseViewHolder extends RecyclerView.ViewHolder {
        private final Map<Integer, View> mViewMap;

        public BaseViewHolder(View itemView) {
            super(itemView);
            mViewMap = new HashMap<>();
        }

        public View getView(int id) {
            return mViewMap.computeIfAbsent(id, itemView::findViewById);
        }
    }
}