/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.adapter;

import static org.koishi.launcher.h2co3.core.H2CO3Auth.setUserState;

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
import org.koishi.launcher.h2co3.core.H2CO3Auth;
import org.koishi.launcher.h2co3.core.H2CO3Loader;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.bean.UserBean;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MaterialDialog;
import org.koishi.launcher.h2co3.ui.fragment.home.HomeFragment;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeListUserAdapter extends RecyclerView.Adapter<HomeListUserAdapter.ViewHolder> {

    private final Context context;
    private final List<UserBean> list;
    private final boolean hasFooter;
    private final Map<String, Drawable> userIconCache = new HashMap<>();
    private final HomeFragment fragment;
    private int selectedPosition;
    private boolean isRemoveUserDialogShowing = false;

    public HomeListUserAdapter(HomeFragment fragment, List<UserBean> list) {
        this.context = fragment.requireActivity();
        this.fragment = fragment;
        this.list = list;
        this.selectedPosition = -1;
        this.hasFooter = true;

        for (UserBean user : list) {
            Drawable userIcon = getUserIcon(user);
            user.setUserIcon(userIcon);
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + (hasFooter ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return position < list.size() ? 0 : 1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(viewType == 0 ? R.layout.item_user_list : R.layout.item_user_add, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        if (viewType == 0) {
            bindUserViewHolder(holder, position);
        } else {
            bindAddViewHolder(holder);
        }
    }


    private Drawable getUserIcon(UserBean user) {
        if (user.getIsOffline()) {
            return ContextCompat.getDrawable(context, org.koishi.launcher.h2co3.resources.R.drawable.ic_home_user);
        } else {
            Drawable cachedIcon = userIconCache.get(user.getUserName());
            if (cachedIcon != null) {
                return cachedIcon;
            } else {
                Drawable userIcon = H2CO3Loader.getHeadDrawable(fragment.requireActivity(), user.getSkinTexture());
                userIconCache.put(user.getUserName(), userIcon);
                return userIcon;
            }
        }
    }


    private void updateSelectedUser(int selectedPosition) throws JSONException {
        JSONObject usersJson = new JSONObject(H2CO3Auth.getUserJson());
        for (int i = 0; i < list.size(); i++) {
            UserBean user = list.get(i);
            boolean isSelected = (i == selectedPosition);
            user.setIsSelected(isSelected);
            usersJson.getJSONObject(user.getUserName()).put(H2CO3Tools.LOGIN_IS_SELECTED, isSelected);
        }
        H2CO3Auth.setUserJson(usersJson.toString());
    }

    private void removeUser(int position) throws JSONException, IOException {
        UserBean removedUser = list.remove(position);
        if (position == selectedPosition) {
            selectedPosition = -1;
            resetUserState();
        } else if (position < selectedPosition) {
            selectedPosition--;
        }

        JSONObject usersJson = new JSONObject(H2CO3Auth.getUserJson());
        usersJson.remove(removedUser.getUserName());
        H2CO3Auth.setUserJson(usersJson.toString());

        fragment.reLoadUser();
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
            try {
                removeUser(position);
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
            isRemoveUserDialogShowing = false;
        });
        dialog.setNegativeButton("取消", (dialogInterface, which) -> isRemoveUserDialogShowing = false);
        dialog.setOnDismissListener(dialogInterface -> isRemoveUserDialogShowing = false);
        dialog.show();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
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

    private void bindUserViewHolder(ViewHolder holder, int position) {
        UserBean user = list.get(position);
        if (user.isSelected()) {
            selectedPosition = position;
            updateUserState(user);
            holder.selectorCardView.setStrokeWidth(13);
            holder.selectorCardView.setClickable(false);
            holder.selectorCardView.setOnClickListener(null);
        } else {
            holder.selectorCardView.setStrokeWidth(3);
            holder.selectorCardView.setClickable(true);
            holder.selectorCardView.setOnClickListener(null);
            holder.selectorCardView.setOnClickListener(v -> {
                selectedPosition = holder.getBindingAdapterPosition();
                try {
                    updateSelectedUser(selectedPosition);
                    fragment.reLoadUser();
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        holder.nameTextView.setText(user.getUserName());
        holder.stateTextView.setText(getUserStateText(user));

        if (user.getUserIcon() == null) {
            Drawable userIcon = getUserIcon(user);
            user.setUserIcon(userIcon);
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
}