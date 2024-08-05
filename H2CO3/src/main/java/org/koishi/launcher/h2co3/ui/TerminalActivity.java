/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.shell.ShellUtil;
import org.koishi.launcher.h2co3.resources.component.LineTextView;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;

import java.io.File;
import java.util.Objects;

public class TerminalActivity extends H2CO3Activity {

    private LineTextView logWindow;
    private TextInputEditText editText;
    private ShellUtil shellUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);
        logWindow = new LineTextView(this);
        logWindow = findViewById(R.id.shell_log_window);
        editText = findViewById(R.id.shell_input);
        logWindow.append("Welcome to use Fold Craft Launcher!\n");
        logWindow.append("Here is the shell command line!\n");
        shellUtil = new ShellUtil(new File(H2CO3Tools.FILES_DIR).getParent(), output -> logWindow.append("\t" + output + "\n"));
        shellUtil.start();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String cmd = Objects.requireNonNull(editText.getText()).toString();
                if (cmd.endsWith("\n")) {
                    logWindow.append("->" + cmd);
                    editText.setText("");
                    if (cmd.contains("clear")) {
                        return;
                    }
                    shellUtil.append(cmd);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shellUtil.interrupt();
    }
}
