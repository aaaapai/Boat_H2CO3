package org.koishi.launcher.h2co3.core.message;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koishi.launcher.h2co3.library.R;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;
import org.koishi.launcher.h2co3.resources.component.adapter.H2CO3RecycleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class H2CO3MessageManager {
    private final NotificationAdapter adapter;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static RecyclerView recyclerView = null;

    public H2CO3MessageManager(NotificationAdapter adapter, RecyclerView recyclerView) {
        this.adapter = adapter;
        H2CO3MessageManager.recyclerView = recyclerView;
    }

    public void addNotification(NotificationItem.Type type, String message) {
        NotificationItem item = new NotificationItem(type, message);
        handler.post(() -> adapter.addNotification(item));

        Runnable removalTask = () -> {
            adapter.removeNotification(item);
        };
        handler.postDelayed(removalTask, 3700);
    }

    public List<NotificationItem> getNotifications() {
        return adapter.getNotifications();
    }

    public record NotificationItem(Type type, String message) {
        public enum Type {
            DEBUG, ERROR, INFO, WARNING
        }
    }

    public static class NotificationAdapter extends H2CO3RecycleAdapter<NotificationItem> {
        private final List<NotificationItem> data = new ArrayList<>();
        private final Map<NotificationItem.Type, Integer> colorMap;

        public NotificationAdapter(Context context, List<NotificationItem> notifications) {
            super(new ArrayList<>(notifications), context);
            colorMap = new HashMap<>();
            colorMap.put(NotificationItem.Type.DEBUG, R.color.app_blue_normal);
            colorMap.put(NotificationItem.Type.ERROR, R.color.md_theme_errorContainer);
            colorMap.put(NotificationItem.Type.INFO, R.color.app_green_normal);
            colorMap.put(NotificationItem.Type.WARNING, R.color.md_theme_errorContainer);
        }

        @Override
        protected void bindData(BaseViewHolder holder, int position) {
            NotificationItem notification = data.get(position);
            H2CO3CardView itemCardView = holder.itemView.findViewById(R.id.message_item_view);
            H2CO3TextView messageTextView = holder.itemView.findViewById(R.id.tv_message);
            H2CO3TextView typeTextView = holder.itemView.findViewById(R.id.tv_type);

            messageTextView.setText(notification.message());
            typeTextView.setText(String.valueOf(notification.type()));
            itemCardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), colorMap.get(notification.type())));

            itemCardView.setOnClickListener(v -> {
                new MaterialAlertDialogBuilder(mContext)
                        .setTitle("Message Type" + ": " + notification.type())
                        .setMessage(notification.message())
                        .setPositiveButton("OK", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_message;
        }

        public void addNotification(NotificationItem item) {
            data.add(item);
            updateData(getNotifications());
            notifyItemInserted(data.size() - 1);
            recyclerView.scrollToPosition(data.size() - 1);
        }

        public List<NotificationItem> getNotifications() {
            return data;
        }

        public void removeNotification(NotificationItem item) {
            int position = data.indexOf(item);
            if (position >= 0) {
                data.remove(position);
                notifyItemRemoved(position);
            }
        }
    }
}