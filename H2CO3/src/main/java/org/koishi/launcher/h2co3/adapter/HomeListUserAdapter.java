package org.koishi.launcher.h2co3.adapter;

import static org.koishi.launcher.h2co3.core.H2CO3Auth.setUserState;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Auth;
import org.koishi.launcher.h2co3.core.H2CO3Loader;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.bean.UserBean;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.adapter.H2CO3RecycleAdapter;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MaterialDialog;
import org.koishi.launcher.h2co3.ui.fragment.home.HomeFragment;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeListUserAdapter extends H2CO3RecycleAdapter<UserBean> {

    private final Map<String, Drawable> userIconCache = new HashMap<>();
    private final HomeFragment fragment;
    private int selectedPosition;
    private boolean isRemoveUserDialogShowing = false;

    public HomeListUserAdapter(HomeFragment fragment, List<UserBean> list) {
        super(list, fragment.requireActivity());
        this.fragment = fragment;
        this.selectedPosition = -1;

        for (UserBean user : list) {
            Drawable userIcon = getUserIcon(user);
            user.setUserIcon(userIcon);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_user_list;
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position < data.size() ? ITEM_TYPE_NORMAL : ITEM_TYPE_FOOTER;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_FOOTER) {
            View footerView = LayoutInflater.from(mContext).inflate(R.layout.item_user_add, parent, false);
            return new BaseViewHolder(footerView);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    protected void bindData(BaseViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_TYPE_FOOTER) {
            bindAddViewHolder(holder);
        } else {
            bindUserViewHolder(holder, position);
        }
    }

    private Drawable getUserIcon(UserBean user) {
        if (user.getIsOffline()) {
            return ContextCompat.getDrawable(mContext, org.koishi.launcher.h2co3.resources.R.drawable.ic_home_user);
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
        for (int i = 0; i < data.size(); i++) {
            UserBean user = data.get(i);
            boolean isSelected = (i == selectedPosition);
            user.setIsSelected(isSelected);
            usersJson.getJSONObject(user.getUserName()).put(H2CO3Tools.LOGIN_IS_SELECTED, isSelected);
        }
        H2CO3Auth.setUserJson(usersJson.toString());
    }

    private void removeUser(int position) throws JSONException, IOException {
        UserBean removedUser = data.remove(position);
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
        fragment.homeUserName.setText(mContext.getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
        fragment.homeUserState.setText(mContext.getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
        fragment.homeUserIcon.setImageDrawable(ContextCompat.getDrawable(mContext, org.koishi.launcher.h2co3.resources.R.drawable.xicon));
    }

    private String getUserStateText(UserBean user) {
        String userType = user.getUserType();
        return switch (userType) {
            case "1" -> mContext.getString(org.koishi.launcher.h2co3.resources.R.string.user_state_microsoft);
            case "2" -> mContext.getString(org.koishi.launcher.h2co3.resources.R.string.user_state_other) + user.getApiUrl();
            default -> mContext.getString(org.koishi.launcher.h2co3.resources.R.string.user_state_offline);
        };
    }

    private void showRemoveUserDialog(int position) {
        H2CO3MaterialDialog dialog = new H2CO3MaterialDialog(mContext);
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

    private void bindUserViewHolder(BaseViewHolder holder, int position) {
        UserBean user = data.get(position);
        View itemView = holder.itemView;
        TextView nameTextView = itemView.findViewById(R.id.item_listview_user_name);
        TextView stateTextView = itemView.findViewById(R.id.item_listview_user_state);
        H2CO3CardView selectorCardView = itemView.findViewById(R.id.login_user_item);
        ImageView userIcon = itemView.findViewById(R.id.item_listview_userImageView);
        ImageButton removeImageButton = itemView.findViewById(R.id.item_listview_user_remove);

        if (user.isSelected()) {
            selectedPosition = position;
            updateUserState(user);
            selectorCardView.setStrokeWidth(13);
            selectorCardView.setClickable(false);
            selectorCardView.setOnClickListener(null);
        } else {
            selectorCardView.setStrokeWidth(3);
            selectorCardView.setClickable(true);
            selectorCardView.setOnClickListener(v -> {
                selectedPosition = holder.getBindingAdapterPosition();
                try {
                    updateSelectedUser(selectedPosition);
                    fragment.reLoadUser();
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        nameTextView.setText(user.getUserName());
        stateTextView.setText(getUserStateText(user));
        userIcon.setImageDrawable(user.getUserIcon());

        removeImageButton.setOnClickListener(v -> {
            if (!isRemoveUserDialogShowing) {
                isRemoveUserDialogShowing = true;
                showRemoveUserDialog(holder.getBindingAdapterPosition());
            }
        });
    }

    private void bindAddViewHolder(BaseViewHolder holder) {
        View itemView = holder.itemView;
        H2CO3CardView addCardView = itemView.findViewById(R.id.login_user_add);
        addCardView.setOnClickListener(v -> fragment.showLoginDialog());
    }
}