package org.koishi.launcher.h2co3.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.adapter.H2CO3RecycleAdapter;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MaterialDialog;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MessageDialog;
import org.koishi.launcher.h2co3.ui.fragment.home.HomeFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeListUserAdapter extends H2CO3RecycleAdapter<UserBean> {

    private final Context context;
    private final HomeFragment fragment;
    private final H2CO3Auth h2co3Auth;
    private int selectedPosition;
    private boolean isRemoveUserDialogShowing = false;
    private final Map<String, Drawable> userIconCache = new HashMap<>();

    public HomeListUserAdapter(HomeFragment fragment, H2CO3Auth h2co3Auth, ArrayList<UserBean> list) {
        super(list, fragment.requireActivity());
        this.context = fragment.requireActivity();
        this.fragment = fragment;
        this.selectedPosition = -1;
        this.h2co3Auth = h2co3Auth;

        setUserIcons();
        View footerView = LayoutInflater.from(context).inflate(R.layout.item_user_add, null);
        setFooterView(footerView);
    }

    private void setUserIcons() {
        for (UserBean user : data) {
            user.setUserIcon(getUserIcon(user));
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_user_list;
    }

    @Override
    protected void bindData(BaseViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_TYPE_NORMAL) {
            bindUserViewHolder((ViewHolder) holder, position);
        } else {
            bindAddViewHolder((ViewHolder) holder);
        }
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(viewType == ITEM_TYPE_NORMAL ? R.layout.item_user_list : R.layout.item_user_add, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, final int position) {
        bindData(holder, position);
    }

    private void bindUserViewHolder(ViewHolder holder, int position) {
        UserBean user = data.get(position);
        holder.selectorCardView.setStrokeWidth(user.isSelected() ? 13 : 3);
        holder.selectorCardView.setClickable(!user.isSelected());

        if (user.isSelected()) {
            selectedPosition = position;
            updateUserState(user);
        } else {
            holder.selectorCardView.setOnClickListener(v -> handleUserSelection(holder, position));
        }

        holder.nameTextView.setText(user.getUserName());
        holder.stateTextView.setText(getUserStateText(user));
        holder.userIcon.setImageDrawable(user.getUserIcon() != null ? user.getUserIcon() : getUserIcon(user));

        holder.removeImageButton.setOnClickListener(v -> {
            if (!isRemoveUserDialogShowing) {
                showRemoveUserDialog(holder.getBindingAdapterPosition());
                isRemoveUserDialogShowing = true;
            }
        });

        holder.selectorCardView.setOnLongClickListener(v -> {
            showUserInfoDialog(user);
            return true;
        });
    }

    private void handleUserSelection(ViewHolder holder, int position) {
        int newPosition = holder.getBindingAdapterPosition();
        if (newPosition != RecyclerView.NO_POSITION) {
            selectedPosition = newPosition;
            try {
                updateSelectedUser(selectedPosition);
                fragment.reLoadUsers();
            } catch (Exception e) {
                H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
            }
        }
    }

    private void bindAddViewHolder(ViewHolder holder) {
        holder.addCardView.setOnClickListener(v -> fragment.showLoginDialog());
    }

    private Drawable getUserIcon(UserBean user) {
        if (user.getIsOffline()) {
            return ContextCompat.getDrawable(context, org.koishi.launcher.h2co3.library.R.drawable.ic_home_user);
        } else {
            return userIconCache.computeIfAbsent(user.getUserName(),
                    k -> h2co3Auth.getHeadDrawable(fragment.requireActivity(), user.getSkinTexture()));
        }
    }

    private void updateSelectedUser(int selectedPosition) {
        try {
            JSONObject usersJson = new JSONObject(h2co3Auth.getUserJson());
            UserBean selectedUser = data.get(selectedPosition);
            for (UserBean user : data) {
                boolean isSelected = user == selectedUser;
                user.setIsSelected(isSelected);
                usersJson.getJSONObject(user.getUserName()).put(H2CO3Tools.LOGIN_IS_SELECTED, isSelected);
            }
            h2co3Auth.setUserJson(usersJson.toString());
        } catch (JSONException e) {
            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
        }
    }

    private void removeUser(int position) {
        if (position < 0 || position >= data.size()) return;
        H2CO3Application.sExecutorService.execute(() -> {
            try {
                UserBean removedUser = data.remove(position);
                if (position == selectedPosition) {
                    selectedPosition = -1;
                    fragment.runOnUiThread(this::resetUserState);
                } else if (position < selectedPosition) {
                    selectedPosition--;
                }

                JSONObject usersJson = new JSONObject(h2co3Auth.getUserJson());
                usersJson.remove(removedUser.getUserName());
                h2co3Auth.setUserJson(usersJson.toString());

                fragment.runOnUiThread(() -> {
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount() - position);
                });

            } catch (JSONException e) {
                H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
            }
        });
    }

    private void updateUserState(UserBean user) {
        h2co3Auth.setUserState(user);
        fragment.userNameTextView.setText(user.getUserName());
        fragment.userStateTextView.setText(getUserStateText(user));
        fragment.userIconImageView.setImageDrawable(getUserIcon(user));
    }

    private void resetUserState() {
        UserBean emptyUser = new UserBean();
        h2co3Auth.setUserState(emptyUser);
        fragment.userNameTextView.setText(context.getString(org.koishi.launcher.h2co3.library.R.string.user_add));
        fragment.userStateTextView.setText(context.getString(org.koishi.launcher.h2co3.library.R.string.user_add));
        fragment.userIconImageView.setImageDrawable(ContextCompat.getDrawable(context, org.koishi.launcher.h2co3.library.R.drawable.xicon));
    }

    private String getUserStateText(UserBean user) {
        String userType = user.getUserType();
        return switch (userType) {
            case "1" ->
                    context.getString(org.koishi.launcher.h2co3.library.R.string.user_state_microsoft);
            case "2" ->
                    context.getString(org.koishi.launcher.h2co3.library.R.string.user_state_other) + user.getApiUrl();
            default ->
                    context.getString(org.koishi.launcher.h2co3.library.R.string.user_state_offline);
        };
    }

    private void showRemoveUserDialog(int position) {
        H2CO3MaterialDialog dialog = new H2CO3MaterialDialog(context);
        dialog.setTitle("确认删除用户");
        dialog.setMessage("确定要删除该用户吗？");
        dialog.setPositiveButton("确定", (dialogInterface, which) -> removeUser(position));
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

    private void showUserInfoDialog(UserBean userBean) {
        new H2CO3MessageDialog(context)
                .setTitle("INFO")
                .setMessage(userBean.getUserName() + "\n" + userBean.getApiUrl() + "\n" + userBean.getToken())
                .setPositiveButton("确定", null)
                .show();
    }
}