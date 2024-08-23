package org.koishi.launcher.h2co3.adapter;

import static org.koishi.launcher.h2co3.core.H2CO3Auth.setUserState;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.application.H2CO3Application;
import org.koishi.launcher.h2co3.core.H2CO3Auth;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.bean.UserBean;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.adapter.H2CO3RecycleAdapter;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MaterialDialog;
import org.koishi.launcher.h2co3.ui.fragment.home.HomeFragment;

import java.util.ArrayList;

public class HomeListUserAdapter extends H2CO3RecycleAdapter<UserBean> {

    private final Context context;
    private final HomeFragment fragment;
    private int selectedPosition;
    private boolean isRemoveUserDialogShowing = false;

    public HomeListUserAdapter(HomeFragment fragment, ArrayList<UserBean> list) {
        super(list, fragment.requireActivity());
        this.context = fragment.requireActivity();
        this.fragment = fragment;
        this.selectedPosition = -1;

        for (UserBean user : data) {
            user.setUserIcon(getUserIcon(user));
        }

        View footerView = LayoutInflater.from(context).inflate(R.layout.item_user_add, null);
        setFooterView(footerView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_user_list;
    }

    @Override
    protected void bindData(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == ITEM_TYPE_NORMAL) {
            bindUserViewHolder((ViewHolder) holder, position);
        } else if (viewType == ITEM_TYPE_FOOTER) {
            bindAddViewHolder((ViewHolder) holder);
        }
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView;
        if (viewType == ITEM_TYPE_NORMAL) {
            itemView = inflater.inflate(R.layout.item_user_list, parent, false);
        } else if (viewType == ITEM_TYPE_FOOTER) {
            itemView = inflater.inflate(R.layout.item_user_add, parent, false);
        } else {
            throw new IllegalStateException("Unexpected view type: " + viewType);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, final int position) {
        bindData(holder, position);
    }

    private void bindUserViewHolder(ViewHolder holder, int position) {
        UserBean user = data.get(position);
        if (user.isSelected()) {
            selectedPosition = position;
            updateUserState(user);
            holder.selectorCardView.setStrokeWidth(13);
            holder.selectorCardView.setClickable(false);
        } else {
            holder.selectorCardView.setStrokeWidth(3);
            holder.selectorCardView.setClickable(true);
            holder.selectorCardView.setOnClickListener(v -> {
                int newPosition = holder.getBindingAdapterPosition();
                if (newPosition != RecyclerView.NO_POSITION) {
                    selectedPosition = newPosition;
                    try {
                        updateSelectedUser(selectedPosition);
                        fragment.reLoadUser();
                    } catch (Exception e) {
                        Log.e("HomeListUserAdapter", "Error updating selected user", e);
                    }
                }
            });
        }
        holder.nameTextView.setText(user.getUserName());
        holder.stateTextView.setText(getUserStateText(user));

        if (user.getUserIcon() == null) {
            user.setUserIcon(getUserIcon(user));
        }
        holder.userIcon.setImageDrawable(user.getUserIcon());

        holder.removeImageButton.setOnClickListener(v -> {
            if (!isRemoveUserDialogShowing) {
                isRemoveUserDialogShowing = true;
                showRemoveUserDialog(holder.getBindingAdapterPosition());
            }
        });
    }

    private void bindAddViewHolder(ViewHolder holder) {
        holder.addCardView.setOnClickListener(v -> fragment.showLoginDialog());
    }

    private Drawable getUserIcon(UserBean user) {
        Drawable defaultIcon = ContextCompat.getDrawable(context, org.koishi.launcher.h2co3.resources.R.drawable.ic_home_user);
        return user.getIsOffline() || user.getUserIcon() == null ? defaultIcon : user.getUserIcon();
    }

    private void updateSelectedUser(int selectedPosition) {
        try {
            JSONObject usersJson = new JSONObject(H2CO3Auth.getUserJson());
            UserBean selectedUser = data.get(selectedPosition);
            for (UserBean user : data) {
                boolean isSelected = user.equals(selectedUser);
                user.setIsSelected(isSelected);
                usersJson.getJSONObject(user.getUserName()).put(H2CO3Tools.LOGIN_IS_SELECTED, isSelected);
            }
            H2CO3Auth.setUserJson(usersJson.toString());
        } catch (JSONException e) {
            Log.e("HomeListUserAdapter", "Error updating user JSON", e);
        }
    }

    private void removeUser(int position) {
        H2CO3Application.sExecutorService.execute(() -> {
            try {
                UserBean removedUser = data.remove(position);
                if (position == selectedPosition) {
                    selectedPosition = -1;
                    fragment.runOnUiThread(this::resetUserState);
                } else if (position < selectedPosition) {
                    selectedPosition--;
                }

                JSONObject usersJson = new JSONObject(H2CO3Auth.getUserJson());
                usersJson.remove(removedUser.getUserName());
                H2CO3Auth.setUserJson(usersJson.toString());

                fragment.reLoadUser();
            } catch (JSONException e) {
                Log.e("HomeListUserAdapter", "Error removing user", e);
            }
        });
    }

    private void updateUserState(UserBean user) {
        setUserState(user);
        fragment.homeUserName.setText(user.getUserName());
        fragment.homeUserState.setText(getUserStateText(user));
        fragment.homeUserIcon.setImageDrawable(getUserIcon(user));
    }

    private void resetUserState() {
        UserBean emptyUser = new UserBean();
        setUserState(emptyUser);
        fragment.homeUserName.setText(context.getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
        fragment.homeUserState.setText(context.getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
        fragment.homeUserIcon.setImageDrawable(ContextCompat.getDrawable(context, org.koishi.launcher.h2co3.resources.R.drawable.xicon));
    }

    private String getUserStateText(UserBean user) {
        String userType = user.getUserType();
        return switch (userType) {
            case "1" ->
                    context.getString(org.koishi.launcher.h2co3.resources.R.string.user_state_microsoft);
            case "2" ->
                    context.getString(org.koishi.launcher.h2co3.resources.R.string.user_state_other) + user.getApiUrl();
            default ->
                    context.getString(org.koishi.launcher.h2co3.resources.R.string.user_state_offline);
        };
    }

    private void showRemoveUserDialog(int position) {
        H2CO3MaterialDialog dialog = new H2CO3MaterialDialog(context);
        dialog.setTitle("确认删除用户");
        dialog.setMessage("确定要删除该用户吗？");
        dialog.setPositiveButton("确定", (dialogInterface, which) -> {
            removeUser(position);
        });
        dialog.setNegativeButton("取消", (dialogInterface, which) -> isRemoveUserDialogShowing = false);
        dialog.setOnDismissListener(dialogInterface -> isRemoveUserDialogShowing = false);
        dialog.show();
    }

    static class ViewHolder extends BaseViewHolder {
        TextView nameTextView;
        TextView stateTextView;
        H2CO3CardView selectorCardView, addCardView;
        ImageButton removeImageButton;
        ImageView userIcon;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.item_listview_user_name);
            stateTextView = itemView.findViewById(R.id.item_listview_user_state);
            selectorCardView = itemView.findViewById(R.id.login_user_item);
            userIcon = itemView.findViewById(R.id.item_listview_userImageView);
            removeImageButton = itemView.findViewById(R.id.item_listview_user_remove);
            addCardView = itemView.findViewById(R.id.login_user_add);
        }
    }
}