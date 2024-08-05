package org.koishi.launcher.h2co3.ui.fragment.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.utils.LocaleUtils;
import org.koishi.launcher.h2co3.core.utils.RuntimeUtils;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;
import org.koishi.launcher.h2co3.ui.H2CO3MainActivity;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class InstallFragment extends H2CO3Fragment implements View.OnClickListener {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private ProgressBar h2co3LauncherProgress, java8Progress, java11Progress, java17Progress, java21Progress;
    private AppCompatImageView h2co3LauncherIcon, java8Icon, java11Icon, java17Icon, java21Icon;
    private FloatingActionButton nextButton;
    private boolean h2co3Launcher = false;
    private boolean java8 = false;
    private boolean java11 = false;
    private boolean java17 = false;
    private boolean java21 = false;
    private boolean installing = false;
    private boolean hasEnteredLauncher = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_install, container, false);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        h2co3LauncherProgress = findViewById(view, R.id.h2co3Launcher_task_progress);
        h2co3LauncherIcon = findViewById(view, R.id.h2co3Launcher_task_icon);
        java8Progress = findViewById(view, R.id.java8_task_progress);
        java8Icon = findViewById(view, R.id.java8_task_icon);
        java11Progress = findViewById(view, R.id.java11_task_progress);
        java11Icon = findViewById(view, R.id.java11_task_icon);
        java17Progress = findViewById(view, R.id.java17_task_progress);
        java17Icon = findViewById(view, R.id.java17_task_icon);
        java21Progress = findViewById(view, R.id.java21_task_progress);
        java21Icon = findViewById(view, R.id.java21_task_icon);
        nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                install();
            }
        });
        start();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
    }

    public void start() {
        handler.postDelayed(this::startApp, 1000);
    }

    public void startApp() {
        initState();
        check();
    }

    private void enterLauncher() {

        if (!hasEnteredLauncher) {
            hasEnteredLauncher = true;
            Intent intent = new Intent(requireActivity(), H2CO3MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }
    }

    private boolean isLatest() {
        return h2co3Launcher && java8 && java11 && java17 && java21;
    }

    private void check() {
        if (isLatest()) {
            enterLauncher();
        }
    }

    private void initState() {
        try {
            h2co3Launcher = RuntimeUtils.isLatest(H2CO3Tools.H2CO3LAUNCHER_LIBRARY_DIR, "/assets/app_runtime/h2co3Launcher");
            if (h2co3Launcher) {
                requireActivity().runOnUiThread(() -> {
                    h2co3LauncherProgress.setVisibility(View.GONE);
                    h2co3LauncherIcon.setImageResource(org.koishi.launcher.h2co3.resources.R.drawable.ic_done);
                });
            } else {
                requireActivity().runOnUiThread(() -> h2co3LauncherIcon.setImageResource(org.koishi.launcher.h2co3.resources.R.drawable.xicon));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java8 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_8_PATH, "/assets/app_runtime/java/jre8");
            if (java8) {
                requireActivity().runOnUiThread(() -> {
                    java8Progress.setVisibility(View.GONE);
                    java8Icon.setImageResource(org.koishi.launcher.h2co3.resources.R.drawable.ic_done);
                });
            } else {
                requireActivity().runOnUiThread(() -> java8Icon.setImageResource(org.koishi.launcher.h2co3.resources.R.drawable.xicon));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java11 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_11_PATH, "/assets/app_runtime/java/jre11");
            if (java11) {
                requireActivity().runOnUiThread(() -> {
                    java11Progress.setVisibility(View.GONE);
                    java11Icon.setImageResource(org.koishi.launcher.h2co3.resources.R.drawable.ic_done);
                });
            } else {
                requireActivity().runOnUiThread(() -> java11Icon.setImageResource(org.koishi.launcher.h2co3.resources.R.drawable.xicon));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java17 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_17_PATH, "/assets/app_runtime/java/jre17");
            if (java17) {
                requireActivity().runOnUiThread(() -> {
                    java17Progress.setVisibility(View.GONE);
                    java17Icon.setImageResource(org.koishi.launcher.h2co3.resources.R.drawable.ic_done);
                });
            } else {
                requireActivity().runOnUiThread(() -> java17Icon.setImageResource(org.koishi.launcher.h2co3.resources.R.drawable.xicon));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java21 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_21_PATH, "/assets/app_runtime/java/jre21");
            if (java21) {
                requireActivity().runOnUiThread(() -> {
                    java21Progress.setVisibility(View.GONE);
                    java21Icon.setImageResource(org.koishi.launcher.h2co3.resources.R.drawable.ic_done);
                });
            } else {
                requireActivity().runOnUiThread(() -> java21Icon.setImageResource(org.koishi.launcher.h2co3.resources.R.drawable.xicon));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void install() {
        if (installing) {
            return;
        }
        nextButton.setEnabled(false);
        installing = true;
        if (!h2co3Launcher) {
            new Thread(() -> {
                try {
                    requireActivity().runOnUiThread(() -> {
                        h2co3LauncherIcon.setVisibility(View.GONE);
                        h2co3LauncherProgress.setVisibility(View.VISIBLE);
                    });
                    RuntimeUtils.install(requireActivity(), H2CO3Tools.H2CO3LAUNCHER_LIBRARY_DIR, "app_runtime/h2co3Launcher");
                    h2co3Launcher = true;
                    requireActivity().runOnUiThread(() -> {
                        h2co3LauncherProgress.setVisibility(View.GONE);
                        h2co3LauncherIcon.setVisibility(View.VISIBLE);
                        h2co3LauncherIcon.setImageResource(org.koishi.launcher.h2co3.resources.R.drawable.ic_done);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.post(this::check);
            }).start();
        }
        if (!java8) {
            new Thread(() -> {
                try {
                    requireActivity().runOnUiThread(() -> {
                        java8Icon.setVisibility(View.GONE);
                        java8Progress.setVisibility(View.VISIBLE);
                    });
                    RuntimeUtils.installJava(requireActivity(), H2CO3Tools.JAVA_8_PATH, "app_runtime/java/jre8");
                    java8 = true;
                    requireActivity().runOnUiThread(() -> {
                        java8Progress.setVisibility(View.GONE);
                        java8Icon.setVisibility(View.VISIBLE);
                        java8Icon.setImageResource(org.koishi.launcher.h2co3.resources.R.drawable.ic_done);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.post(this::check);
            }).start();
        }
        if (!java11) {
            new Thread(() -> {
                try {
                    requireActivity().runOnUiThread(() -> {
                        java11Icon.setVisibility(View.GONE);
                        java11Progress.setVisibility(View.VISIBLE);
                    });
                    RuntimeUtils.installJava(requireActivity(), H2CO3Tools.JAVA_11_PATH, "app_runtime/java/jre11");
                    if (!LocaleUtils.getSystemLocale().getDisplayName().equals(Locale.CHINA.getDisplayName())) {
                        FileTools.writeText(new File(H2CO3Tools.JAVA_11_PATH + "/resolv.conf"), "nameserver 1.1.1.1\n" + "nameserver 1.0.0.1");
                    } else {
                        FileTools.writeText(new File(H2CO3Tools.JAVA_11_PATH + "/resolv.conf"), "nameserver 8.8.8.8\n" + "nameserver 8.8.4.4");
                    }
                    java11 = true;
                    requireActivity().runOnUiThread(() -> {
                        java11Progress.setVisibility(View.GONE);
                        java11Icon.setVisibility(View.VISIBLE);
                        java11Icon.setImageResource(org.koishi.launcher.h2co3.resources.R.drawable.ic_done);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.post(this::check);
            }).start();
        }
        if (!java17) {
            new Thread(() -> {
                try {
                    requireActivity().runOnUiThread(() -> {
                        java17Icon.setVisibility(View.GONE);
                        java17Progress.setVisibility(View.VISIBLE);
                    });
                    RuntimeUtils.installJava(requireActivity(), H2CO3Tools.JAVA_17_PATH, "app_runtime/java/jre17");
                    if (!LocaleUtils.getSystemLocale().getDisplayName().equals(Locale.CHINA.getDisplayName())) {
                        FileTools.writeText(new File(H2CO3Tools.JAVA_17_PATH + "/resolv.conf"), "nameserver 1.1.1.1\n" + "nameserver 1.0.0.1");
                    } else {
                        FileTools.writeText(new File(H2CO3Tools.JAVA_17_PATH + "/resolv.conf"), "nameserver 8.8.8.8\n" + "nameserver 8.8.4.4");
                    }
                    java17 = true;
                    requireActivity().runOnUiThread(() -> {
                        java17Progress.setVisibility(View.GONE);
                        java17Icon.setVisibility(View.VISIBLE);
                        java17Icon.setImageResource(org.koishi.launcher.h2co3.resources.R.drawable.ic_done);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.post(this::check);
            }).start();
        }
        if (!java21) {
            new Thread(() -> {
                try {
                    requireActivity().runOnUiThread(() -> {
                        java21Icon.setVisibility(View.GONE);
                        java21Progress.setVisibility(View.VISIBLE);
                    });
                    RuntimeUtils.installJava(requireActivity(), H2CO3Tools.JAVA_21_PATH, "app_runtime/java/jre21");
                    if (!LocaleUtils.getSystemLocale().getDisplayName().equals(Locale.CHINA.getDisplayName())) {
                        FileTools.writeText(new File(H2CO3Tools.JAVA_21_PATH + "/resolv.conf"), "nameserver 1.1.1.1\n" + "nameserver 1.0.0.1");
                    } else {
                        FileTools.writeText(new File(H2CO3Tools.JAVA_21_PATH + "/resolv.conf"), "nameserver 8.8.8.8\n" + "nameserver 8.8.4.4");
                    }
                    java21 = true;
                    requireActivity().runOnUiThread(() -> {
                        java21Progress.setVisibility(View.GONE);
                        java21Icon.setVisibility(View.VISIBLE);
                        java21Icon.setImageResource(org.koishi.launcher.h2co3.resources.R.drawable.ic_done);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.post(this::check);
            }).start();
        }
    }
}