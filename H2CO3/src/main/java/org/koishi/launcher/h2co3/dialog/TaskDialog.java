package org.koishi.launcher.h2co3.dialog;

import static org.koishi.launcher.h2co3.core.utils.AndroidUtils.getLocalizedText;
import static org.koishi.launcher.h2co3.core.utils.Lang.tryCast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.game.download.fabric.FabricAPIInstallTask;
import org.koishi.launcher.h2co3.core.game.download.fabric.FabricInstallTask;
import org.koishi.launcher.h2co3.core.game.download.forge.ForgeNewInstallTask;
import org.koishi.launcher.h2co3.core.game.download.forge.ForgeOldInstallTask;
import org.koishi.launcher.h2co3.core.game.download.vanilla.GameAssetDownloadTask;
import org.koishi.launcher.h2co3.core.game.download.vanilla.GameInstallTask;
import org.koishi.launcher.h2co3.core.game.download.liteloader.LiteLoaderInstallTask;
import org.koishi.launcher.h2co3.core.game.download.neoforge.NeoForgeInstallTask;
import org.koishi.launcher.h2co3.core.game.download.neoforge.NeoForgeOldInstallTask;
import org.koishi.launcher.h2co3.core.game.download.optifine.OptiFineInstallTask;
import org.koishi.launcher.h2co3.core.game.mod.MinecraftInstanceTask;
import org.koishi.launcher.h2co3.core.game.mod.ModpackInstallTask;
import org.koishi.launcher.h2co3.core.game.mod.ModpackUpdateTask;
import org.koishi.launcher.h2co3.core.game.mod.curse.CurseCompletionTask;
import org.koishi.launcher.h2co3.core.game.mod.curse.CurseInstallTask;
import org.koishi.launcher.h2co3.core.game.mod.mcbbs.McbbsModpackCompletionTask;
import org.koishi.launcher.h2co3.core.game.mod.mcbbs.McbbsModpackExportTask;
import org.koishi.launcher.h2co3.core.game.mod.modrinth.ModrinthCompletionTask;
import org.koishi.launcher.h2co3.core.game.mod.modrinth.ModrinthInstallTask;
import org.koishi.launcher.h2co3.core.game.mod.multimc.MultiMCModpackExportTask;
import org.koishi.launcher.h2co3.core.game.mod.multimc.MultiMCModpackInstallTask;
import org.koishi.launcher.h2co3.core.game.mod.server.ServerModpackCompletionTask;
import org.koishi.launcher.h2co3.core.game.mod.server.ServerModpackExportTask;
import org.koishi.launcher.h2co3.core.game.mod.server.ServerModpackLocalInstallTask;
import org.koishi.launcher.h2co3.core.utils.Lang;
import org.koishi.launcher.h2co3.core.utils.StringUtils;
import org.koishi.launcher.h2co3.core.utils.task.FileDownloadTask;
import org.koishi.launcher.h2co3.core.utils.task.Schedulers;
import org.koishi.launcher.h2co3.core.utils.task.Task;
import org.koishi.launcher.h2co3.core.utils.task.TaskExecutor;
import org.koishi.launcher.h2co3.core.utils.task.TaskListener;
import org.koishi.launcher.h2co3.resources.component.H2CO3Button;
import org.koishi.launcher.h2co3.resources.component.H2CO3LinearProgress;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;
import org.koishi.launcher.h2co3.resources.component.adapter.H2CO3RecycleAdapter;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3CustomViewDialog;
import org.koishi.launcher.h2co3.utils.download.TaskCancellationAction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TaskDialog extends H2CO3CustomViewDialog implements View.OnClickListener {
    private final H2CO3TextView speedView;
    private final Consumer<FileDownloadTask.SpeedEvent> speedEventHandler;

    private TaskExecutor executor;
    private TaskCancellationAction onCancel;
    private final RecyclerView LeftTaskListView, rightTaskListView;
    private RightTaskListPane rightTaskListPane;
    private LeftTaskListPane leftTaskListPane;
    private final H2CO3Button cancelButton;
    public AlertDialog alertDialog;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public TaskDialog(@NonNull Context context) {
        super(context);
        setCustomView(R.layout.dialog_task);
        setCancelable(false);

        rightTaskListView = findViewById(R.id.list_right);
        LeftTaskListView = findViewById(R.id.list_left);
        speedView = findViewById(R.id.speed);
        cancelButton = findViewById(R.id.cancel);

        cancelButton.setOnClickListener(this);

        DecimalFormat df = new DecimalFormat("#.##");
        speedEventHandler = speedEvent -> {
            double speed = speedEvent.getSpeed();
            String[] units = new String[]{"B/s", "KB/s", "MB/s"};
            int unitIndex = 0;
            while (speed > 1024 && unitIndex < units.length - 1) {
                speed /= 1024;
                unitIndex++;
            }
            String finalUnit = units[unitIndex];
            double finalSpeed = speed;
            Schedulers.androidUIThread().execute(() -> speedView.setText(df.format(finalSpeed) + " " + finalUnit));
        };
        FileDownloadTask.speedEvent.channel(FileDownloadTask.SpeedEvent.class).registerWeak(speedEventHandler);
    }

    public void setAlertDialog(AlertDialog dialog) {
        this.alertDialog = dialog;
    }

    public void setExecutor(TaskExecutor executor) {
        setExecutor(executor, true);
    }

    public void setExecutor(TaskExecutor executor, boolean autoClose) {
        this.executor = executor;

        if (executor != null) {
            if (autoClose) {
                executor.addTaskListener(new TaskListener() {
                    @Override
                    public void onStop(boolean success, TaskExecutor executor) {
                        Schedulers.androidUIThread().execute(() -> alertDialog.dismiss());
                    }
                });
            }

            leftTaskListPane = new LeftTaskListPane(getContext());
            LeftTaskListView.setLayoutManager(new LinearLayoutManager(getContext()));
            LeftTaskListView.setAdapter(leftTaskListPane);
            rightTaskListPane = new RightTaskListPane(getContext());
            rightTaskListView.setLayoutManager(new LinearLayoutManager(getContext()));
            rightTaskListView.setAdapter(rightTaskListPane);
        }
    }

    public void setCancel(TaskCancellationAction onCancel) {
        this.onCancel = onCancel;

        cancelButton.setEnabled(onCancel != null);
    }

    @Override
    public void onClick(View view) {
        if (view == cancelButton) {
            Optional.ofNullable(executor).ifPresent(TaskExecutor::cancel);
            if (onCancel != null) {
                onCancel.getCancellationAction().accept(this);
            }
            alertDialog.dismiss();
        }
    }

    public class LeftTaskListPane extends H2CO3RecycleAdapter<LeftTaskListPane.StageNode> {

        private final List<StageNode> stageNodes = new ArrayList<>();

        public LeftTaskListPane(Context context) {
            super(new ArrayList<>(), context);
            setExecutor(executor);
        }

        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_downloading_state, parent, false);
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(BaseViewHolder holder, int position) {
            StageNode stageNode = data.get(position);
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.bindView(stageNode.getView());
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_downloading_state;
        }

        private void setExecutor(TaskExecutor executor) {
            List<String> stages = Lang.removingDuplicates(executor.getStages());
            executor.addTaskListener(new TaskListener() {
                @Override
                public void onStart() {
                    Schedulers.androidUIThread().execute(() -> {
                        stageNodes.clear();
                        data.clear();
                        List<StageNode> newNodes = stages.stream().map(it -> new StageNode(getContext(), it)).collect(Collectors.toList());
                        stageNodes.addAll(newNodes);
                        data.addAll(newNodes);
                        notifyItemRangeInserted(0, newNodes.size());
                    });
                }

                @Override
                public void onReady(Task<?> task) {
                    if (task.getStage() != null) {
                        Schedulers.androidUIThread().execute(() -> {
                            for (int i = 0; i < stageNodes.size(); i++) {
                                if (stageNodes.get(i).stage.equals(task.getStage())) {
                                    stageNodes.get(i).begin();
                                    notifyItemChanged(i);
                                    break;
                                }
                            }
                        });
                    }
                }

                @Override
                public void onFinished(Task<?> task) {
                    if (task.getStage() != null) {
                        Schedulers.androidUIThread().execute(() -> {
                            for (int i = 0; i < stageNodes.size(); i++) {
                                if (stageNodes.get(i).stage.equals(task.getStage())) {
                                    stageNodes.get(i).succeed();
                                    notifyItemChanged(i);
                                    break;
                                }
                            }
                        });
                    }
                }

                @Override
                public void onFailed(Task<?> task, Throwable throwable) {
                    if (task.getStage() != null) {
                        Schedulers.androidUIThread().execute(() -> {
                            for (int i = 0; i < stageNodes.size(); i++) {
                                if (stageNodes.get(i).stage.equals(task.getStage())) {
                                    stageNodes.get(i).fail();
                                    notifyItemChanged(i);
                                    break;
                                }
                            }
                        });
                    }
                }

                @Override
                public void onPropertiesUpdate(Task<?> task) {
                    if (task.getStage() != null) {
                        Schedulers.androidUIThread().execute(() -> {
                            int total = tryCast(task.getProperties().get("total"), Integer.class).orElse(0);
                            stageNodes.stream()
                                    .filter(x -> x.stage.equals(task.getStage()))
                                    .findAny()
                                    .ifPresent(stageNode -> stageNode.setTotal(total));
                        });
                    }
                }
            });
        }

        public static class ViewHolder extends BaseViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            void bindView(View view) {
                LinearLayoutCompat container = (LinearLayoutCompat) itemView;
                if (view.getParent() != null) {
                    ((ViewGroup) view.getParent()).removeView(view);
                }
                container.removeAllViews();
                container.addView(view);
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public static class StageNode {
            private final Context context;
            private final String stage;
            private final View parent;
            private final H2CO3TextView title;
            private final AppCompatImageView icon;
            private int count = 0;
            private int total = 0;
            private boolean started = false;

            public StageNode(Context context, String stage) {
                this.context = context;
                this.stage = stage;

                parent = LayoutInflater.from(context).inflate(R.layout.item_downloading_state, null);
                title = parent.findViewById(R.id.title);
                icon = parent.findViewById(R.id.icon);

                // 根据 stage 设置消息
                String message = getLocalizedText(context, stage.replace(".", "_").replace("-", "_"));
                title.setText(message);
                icon.setImageDrawable(context.getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.ic_menu_custom));
            }

            public void begin() {
                if (started) return;
                started = true;
                icon.setImageDrawable(context.getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.ic_arrow_right_black));
            }

            public void fail() {
                icon.setImageDrawable(context.getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.xicon));
            }

            public void succeed() {
                icon.setImageDrawable(context.getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.ic_baseline_done_24));
            }

            public void count() {
                updateCounter(++count, total);
            }

            public void setTotal(int total) {
                this.total = total;
                updateCounter(count, total);
            }

            @SuppressLint("DefaultLocale")
            public void updateCounter(int count, int total) {
                if (total > 0)
                    title.setText(String.format("%s - %d/%d", title.getText(), count, total));
                else
                    title.setText(title.getText());
            }

            public View getView() {
                return parent;
            }
        }
    }

    public final class RightTaskListPane extends H2CO3RecycleAdapter<Task<?>> {

        private final Map<Task<?>, ProgressListNode> nodes = new HashMap<>();

        public RightTaskListPane(Context context) {
            super(new ArrayList<>(), context);
            setExecutor(executor);
        }

        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download, parent, false);
            return new TaskViewHolder(view);
        }

        @Override
        protected void bindData(BaseViewHolder holder, int position) {
            Task<?> task = data.get(position);
            TaskViewHolder taskViewHolder = (TaskViewHolder) holder;
            taskViewHolder.bindTask(task);
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_download;
        }

        private void setExecutor(TaskExecutor executor) {
            executor.addTaskListener(new TaskListener() {
                @Override
                public void onRunning(Task<?> task) {
                    if (!task.getSignificance().shouldShow() || task.getName() == null)
                        return;

                    Schedulers.androidUIThread().execute(() -> {
                        ProgressListNode node = new ProgressListNode(getContext(), task);
                        nodes.put(task, node);
                        data.add(task);
                        notifyItemInserted(data.size() - 1);
                    });
                }

                @Override
                public void onFinished(Task<?> task) {
                    Schedulers.androidUIThread().execute(() -> {
                        ProgressListNode node = nodes.remove(task);
                        if (node == null)
                            return;
                        node.unbind();
                        int index = data.indexOf(task);
                        if (index != -1) {
                            data.remove(index);
                            notifyItemRemoved(index);
                        }
                    });
                }

                @Override
                public void onFailed(Task<?> task, Throwable throwable) {
                    Schedulers.androidUIThread().execute(() -> {
                        ProgressListNode node = nodes.remove(task);
                        if (node == null)
                            return;
                        node.setThrowable(throwable);
                        int index = data.indexOf(task);
                        if (index != -1) {
                            data.remove(index);
                            notifyItemRemoved(index);
                        }
                    });
                }
            });
        }

        static class TaskViewHolder extends BaseViewHolder {
            private final H2CO3LinearProgress bar;
            private final H2CO3TextView state;
            private final H2CO3TextView title;

            TaskViewHolder(View itemView) {
                super(itemView);
                bar = itemView.findViewById(R.id.fileProgress);
                title = itemView.findViewById(R.id.fileNameText);
                state = itemView.findViewById(R.id.state);
            }

            void bindTask(Task<?> task) {
                bar.percentProgressProperty().bind(task.progressProperty());
                title.setText(task.getName());
                state.stringProperty().bind(task.messageProperty());
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private static class ProgressListNode {
            private final H2CO3LinearProgress bar;
            private final H2CO3TextView state;

            public ProgressListNode(Context context, Task<?> task) {
                View parent = LayoutInflater.from(context).inflate(R.layout.item_download, null);
                bar = parent.findViewById(R.id.fileProgress);
                H2CO3TextView title = parent.findViewById(R.id.fileNameText);
                state = parent.findViewById(R.id.state);

                bar.percentProgressProperty().bind(task.progressProperty());
                title.setText(task.getName());
                state.stringProperty().bind(task.messageProperty());
            }

            public void unbind() {
                bar.percentProgressProperty().unbind();
                state.stringProperty().unbind();
            }

            public void setThrowable(Throwable throwable) {
                unbind();
                state.setText(throwable.getLocalizedMessage());
                bar.setProgress(0);
            }
        }
    }
}
