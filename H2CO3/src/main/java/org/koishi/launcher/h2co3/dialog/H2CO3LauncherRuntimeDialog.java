package org.koishi.launcher.h2co3.dialog;

import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.game.h2co3launcher.H2CO3GameHelper;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3CustomViewDialog;

public class H2CO3LauncherRuntimeDialog extends H2CO3CustomViewDialog implements View.OnClickListener {

    private static final int BUTTON_COUNT = 4;
    private H2CO3CardView[] buttons;
    private String[] javaPaths;
    private H2CO3GameHelper gameHelper;

    public H2CO3LauncherRuntimeDialog(Context context) {
        super(context);
        setTitle(org.koishi.launcher.h2co3.resources.R.string.title_runtime);
        setCustomView(R.layout.custom_dialog_runtime);
        gameHelper = new H2CO3GameHelper();
        initViews();
    }

    public void initViews() {
        buttons = new H2CO3CardView[BUTTON_COUNT];
        javaPaths = new String[]{
                H2CO3Tools.JAVA_8_PATH,
                H2CO3Tools.JAVA_11_PATH,
                H2CO3Tools.JAVA_17_PATH,
                H2CO3Tools.JAVA_21_PATH
        };

        for (int i = 0; i < BUTTON_COUNT; i++) {
            buttons[i] = findViewById(getButtonId(i));
            buttons[i].setOnClickListener(this);
            updateButtonStrokeWidth(i);
        }
    }

    private int getButtonId(int index) {
        return switch (index) {
            case 0 -> R.id.button_jre_8;
            case 1 -> R.id.button_jre_11;
            case 2 -> R.id.button_jre_17;
            case 3 -> R.id.button_jre_21;
            default -> throw new IllegalArgumentException("Invalid button index");
        };
    }

    private void updateButtonStrokeWidth(int index) {
        if (gameHelper != null) {
            buttons[index].setStrokeWidth(gameHelper.getJavaPath().equals(javaPaths[index]) ? 11 : 0);
        } else {
            buttons[index].setStrokeWidth(0);
        }
    }

    @Override
    public AlertDialog show() {
        return super.show();
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < BUTTON_COUNT; i++) {
            if (v == buttons[i]) {
                gameHelper.setJavaPath(javaPaths[i]);
                updateButtonStrokeWidth(i);
            } else {
                buttons[i].setStrokeWidth(0);
            }
        }
    }
}