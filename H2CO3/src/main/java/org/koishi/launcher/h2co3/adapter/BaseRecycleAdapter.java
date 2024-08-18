package org.koishi.launcher.h2co3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseRecycleAdapter<T> extends RecyclerView.Adapter<BaseRecycleAdapter.BaseViewHolder> {

    public static final int ITEM_TYPE_NORMAL = 0X1111;
    public static final int ITEM_TYPE_HEADER = 0X1112;
    public static final int ITEM_TYPE_FOOTER = 0X1113;
    protected final Context mContext;
    protected List<T> datas;
    protected RvItemOnclickListener mRvItemOnclickListener;
    private View mHeaderView;
    private View mFooterView;
    private boolean isHasHeader = false;
    private boolean isHasFooter = false;

    public BaseRecycleAdapter(List<T> datas, Context mContext) {
        this.datas = datas != null ? datas : new ArrayList<>();
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
        int dataPosition = position;
        if (isHasHeader) {
            dataPosition--;
        }
        if (dataPosition >= 0 && dataPosition < datas.size()) {
            bindData(holder, dataPosition);
        }
    }

    public void setHeaderView(View header) {
        if (header != null) {
            this.mHeaderView = header;
            isHasHeader = true;
            notifyItemInserted(0);
        }
    }

    public void setFooterView(View footer) {
        if (footer != null) {
            this.mFooterView = footer;
            isHasFooter = true;
            notifyItemInserted(getItemCount() - 1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isHasHeader && position == 0) {
            return ITEM_TYPE_HEADER;
        }
        if (isHasFooter && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        }
        return ITEM_TYPE_NORMAL;
    }

    public void refresh(List<T> newDatas) {
        if (newDatas != null) {
            int oldSize = datas.size();
            datas.clear();
            datas.addAll(newDatas);
            notifyItemRangeChanged(0, oldSize);
            notifyItemRangeInserted(0, newDatas.size());
        }
    }

    public void update(List<T> newDatas) {
        if (newDatas != null) {
            datas.clear();
            datas.addAll(newDatas);
            notifyDataSetChanged();
        }
    }

    public void addData(List<T> newDatas) {
        if (newDatas != null) {
            int startPosition = datas.size();
            datas.addAll(newDatas);
            notifyItemRangeInserted(startPosition, newDatas.size());
        }
    }

    public void remove(int position) {
        if (position >= 0 && position < datas.size()) {
            datas.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, datas.size() - position);
        }
    }

    protected abstract void bindData(BaseViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return datas.size() + (isHasHeader ? 1 : 0) + (isHasFooter ? 1 : 0);
    }

    public abstract int getLayoutId();

    public void setItemText(View view, String text) {
        if (view instanceof TextView) {
            ((TextView) view).setText(text != null ? text : "");
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