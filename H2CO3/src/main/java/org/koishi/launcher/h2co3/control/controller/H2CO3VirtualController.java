
/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

/*
 * //
 * // Created by cainiaohh on 2024-03-16.
 * //
 */

package org.koishi.launcher.h2co3.control.controller;

import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.KEYBOARD_BUTTON;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.MARK_KEYNAME_SPLIT;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.MOUSE_BUTTON;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.MOUSE_POINTER;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.MOUSE_POINTER_INC;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.TYPE_WORDS;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.materialswitch.MaterialSwitch;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.ckb.support.CustomizeKeyboardMaker;
import org.koishi.launcher.h2co3.control.client.H2CO3ControlClient;
import org.koishi.launcher.h2co3.control.codes.Translation;
import org.koishi.launcher.h2co3.control.event.BaseKeyEvent;
import org.koishi.launcher.h2co3.control.input.Input;
import org.koishi.launcher.h2co3.control.input.OnscreenInput;
import org.koishi.launcher.h2co3.control.input.log.DebugInfo;
import org.koishi.launcher.h2co3.control.input.screen.CustomizeKeyboard;
import org.koishi.launcher.h2co3.control.input.screen.ItemBar;
import org.koishi.launcher.h2co3.control.input.screen.OnscreenTouchpad;
import org.koishi.launcher.h2co3.core.h2co3launcher.utils.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3.core.utils.DisplayUtils;
import org.koishi.launcher.h2co3.resources.component.MenuView;
import org.koishi.launcher.h2co3.resources.component.dialog.DialogUtils;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3CustomViewDialog;
import org.koishi.launcher.h2co3.resources.component.dialog.support.DialogSupports;

import java.util.HashMap;
import java.util.Objects;

import timber.log.Timber;


public class H2CO3VirtualController extends BaseController implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    //sp
    private final static String spFileName = "gamecontroller_config";
    private final static int spMode = Context.MODE_PRIVATE;
    private final static String sp_enable_ckb = "enable_customize_keyboard";
    private final static String sp_enable_itembar = "enable_mcpe_itembar";
    private final static String sp_enable_onscreentouchpad = "enable_touchpad";
    private final static String sp_enable_debuginfo = "enable_debuginfo";
    private final static String sp_first_loadder = "first_loaded";

    //Dialog的控件
    private final Translation mTranslation;
    private final int screenWidth;
    private final int screenHeight;
    public OnscreenInput itemBar;
    public OnscreenInput custmoizeKeyboard;
    public OnscreenInput onscreenTouchpad;
    public Input debugInfo;
    public H2CO3LauncherBridge h2co3LauncherBridge;
    public AlertDialog settingDialogAlert;
    private VirtualControllerSetting settingDialog;
    private ImageButton buttonCustomizeKeyboard;
    private MaterialSwitch switchCustomizeKeyboard;
    private ImageButton buttonPEItembar;
    private MaterialSwitch switchPEItembar;
    private ImageButton buttonTouchpad;
    private MaterialSwitch switchTouchpad;
    private MaterialSwitch switchDebugInfo;
    private Button buttonOK;
    private CheckBox checkboxLock;
    private Button buttonResetPos;

    //绑定
    private HashMap<View, Input> bindingViews;

    public H2CO3VirtualController(H2CO3ControlClient h2CO3ControlClient, H2CO3LauncherBridge bridge, int transType) {
        super(h2CO3ControlClient, bridge, true);
        this.mTranslation = new Translation(transType);

        screenWidth = this.getConfig().getScreenWidth();
        screenHeight = this.getConfig().getScreenHeight();

        init();

    }

    @Override
    public void saveConfig() {
        super.saveConfig();
        this.saveConfigToFile();
    }

    public void init() {
        settingDialog = new VirtualControllerSetting(context);
        settingDialogAlert = settingDialog.create();

        onscreenTouchpad = new OnscreenTouchpad();
        itemBar = new ItemBar();
        custmoizeKeyboard = new CustomizeKeyboard();
        debugInfo = new DebugInfo();

        this.addInput(onscreenTouchpad);
        this.addInput(debugInfo);
        this.addInput(itemBar);
        this.addInput(custmoizeKeyboard);

        for (Input i : inputs) {
            i.setEnabled(false);
        }

        MenuView dButton = new MenuView(context);
        dButton.setLayoutParams(new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(context, 30), DisplayUtils.getPxFromDp(context, 30)));
        dButton.setTodo(() -> settingDialogAlert.show());
        dButton.setY((float) (screenHeight / 2));
        h2CO3ControlClient.addContentView(dButton, dButton.getLayoutParams());


        buttonCustomizeKeyboard = settingDialog.findViewById(R.id.virtual_controller_dialog_button_customize_keyboard);
        buttonPEItembar = settingDialog.findViewById(R.id.virtual_controller_dialog_button_pe_itembar);
        buttonTouchpad = settingDialog.findViewById(R.id.virtual_controller_dialog_button_pc_touchpad);

        switchCustomizeKeyboard = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_customize_keyboard);
        switchPEItembar = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_pe_itembar);
        switchTouchpad = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_pc_touchpad);
        switchDebugInfo = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_debug_info);

        buttonOK = settingDialog.findViewById(R.id.virtual_controller_dialog_button_ok);
        checkboxLock = settingDialog.findViewById(R.id.virtual_controller_dialog_checkbox_lock);
        buttonResetPos = settingDialog.findViewById(R.id.virtual_controller_dialog_button_reset_pos);

        for (View v : new View[]{buttonCustomizeKeyboard, buttonOK, buttonResetPos, buttonPEItembar, buttonTouchpad}) {
            v.setOnClickListener(this);
        }

        for (MaterialSwitch s : new MaterialSwitch[]{switchCustomizeKeyboard, switchPEItembar, switchTouchpad, switchDebugInfo}) {
            s.setOnCheckedChangeListener(this);
        }

        checkboxLock.setOnCheckedChangeListener(this);

        //绑定
        bindViewWithInput();

        //加载配置文件
        loadConfigFromFile();
    }

    public void bindViewWithInput() {
        //绑定Input对象与ImageButton和Switch
        bindingViews = new HashMap<>();
        bindingViews.put(buttonCustomizeKeyboard, custmoizeKeyboard);
        bindingViews.put(switchCustomizeKeyboard, custmoizeKeyboard);
        bindingViews.put(buttonPEItembar, itemBar);
        bindingViews.put(switchPEItembar, itemBar);
        bindingViews.put(buttonTouchpad, onscreenTouchpad);
        bindingViews.put(switchTouchpad, onscreenTouchpad);
        bindingViews.put(switchDebugInfo, debugInfo);
    }

    @Override
    public void sendKey(BaseKeyEvent e) {
        //日志输出
        toLog(e);
        //事件分配
        switch (e.getType()) {
            case KEYBOARD_BUTTON, MOUSE_BUTTON -> {
                String KeyName = e.getKeyName();
                String[] strs = KeyName.split(MARK_KEYNAME_SPLIT);
                for (String str : strs) {
                    //Log.e(e.getTag(),"切分: " + str + " 总大小: " + strs.length );
                    sendKeyEvent(new BaseKeyEvent(e.getTag(), str, e.isPressed(), e.getType(), e.getPointer()));
                }
            }
            case MOUSE_POINTER, MOUSE_POINTER_INC, TYPE_WORDS -> sendKeyEvent(e);
            default -> {
            }
        }

    }

    private void toLog(BaseKeyEvent event) {
        String info = switch (event.getType()) {
            case KEYBOARD_BUTTON ->
                    "Type: " + event.getType() + " KeyName: " + event.getKeyName() + " Pressed: " + event.isPressed();
            case MOUSE_BUTTON ->
                    "Type: " + event.getType() + " MouseName " + event.getKeyName() + " Pressed: " + event.isPressed();
            case MOUSE_POINTER ->
                    "Type: " + event.getType() + " PointerX: " + event.getPointer()[0] + " PointerY: " + event.getPointer()[1];
            case TYPE_WORDS -> "Type: " + event.getType() + " Char: " + event.getChars();
            case MOUSE_POINTER_INC ->
                    "Type: " + event.getType() + " IncX: " + event.getPointer()[0] + " IncY: " + event.getPointer()[1];
            default -> "Unknown: " + event;
        };
        Timber.tag(event.getTag()).e(info);
    }

    //事件发送
    private void sendKeyEvent(BaseKeyEvent e) {
        switch (e.getType()) {
            case KEYBOARD_BUTTON:
                h2CO3ControlClient.setKey(mTranslation.trans(e.getKeyName()), e.isPressed());
                break;
            case MOUSE_BUTTON:
                h2CO3ControlClient.setMouseButton(mTranslation.trans(e.getKeyName()), e.isPressed());
                break;
            case MOUSE_POINTER:
                if (e.getPointer() != null) {
                    h2CO3ControlClient.setPointer(e.getPointer()[0], e.getPointer()[1]);
                }
                break;
            case TYPE_WORDS:
                typeWords(e.getChars());
                break;
            case MOUSE_POINTER_INC:
                if (e.getPointer() != null) {
                    h2CO3ControlClient.setPointerInc(e.getPointer()[0], e.getPointer()[1]);
                }
            default:
        }
    }

    @Override
    public void onClick(View v) {

        if (v instanceof ImageButton && bindingViews.containsKey(v)) {
            Objects.requireNonNull(bindingViews.get(v)).runConfigure();
            return;
        }

        if (v == buttonOK) {
            saveConfigToFile();
            settingDialogAlert.dismiss();
            return;
        }

        if (v == buttonResetPos) {
            DialogUtils.createBothChoicesDialog(context, context.getString(org.koishi.launcher.h2co3.resources.R.string.title_note), context.getString(org.koishi.launcher.h2co3.resources.R.string.tips_are_you_sure_to_auto_config_layout), context.getString(org.koishi.launcher.h2co3.resources.R.string.title_ok), context.getString(org.koishi.launcher.h2co3.resources.R.string.title_cancel), new DialogSupports() {
                @Override
                public void runWhenPositive() {
                    resetAllPosOnScreen();
                }
            });
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView instanceof MaterialSwitch && bindingViews.containsKey(buttonView)) {
            (Objects.requireNonNull(bindingViews.get(buttonView))).setEnabled(isChecked);
        }
        if (buttonView == checkboxLock) {
            for (Input i : inputs) {
                if (i instanceof OnscreenInput) {
                    ((OnscreenInput) i).setUiMoveable(isChecked);
                }
            }
        }

    }

    private int[] calculateMarginsOnScreen(OnscreenInput i, float leftScale, float topScale) {
        int viewWidth;
        int viewHeight;
        int leftMargin;
        int topMargin;

        if (i.getSize() == null) {
            return null;
        } else {
            viewWidth = i.getSize()[0];
            viewHeight = i.getSize()[1];
        }

        leftMargin = (int) (screenWidth * leftScale - viewWidth / 2);
        topMargin = (int) (screenHeight * topScale - viewHeight / 2);

        //超出右边界
        if (leftMargin + viewWidth > screenWidth) {
            leftMargin = screenWidth - viewWidth;
        }
        //超出下边界
        if (topMargin + viewHeight > screenHeight) {
            topMargin = screenHeight - viewHeight;
        }
        //超出左边界
        if (leftMargin < 0) {
            leftMargin = 0;
        }
        //超出上边界
        if (topMargin < 0) {
            topMargin = 0;
        }

        //Log.e(TAG,"屏幕宽度 " + screenWidth + " 屏幕高度 " + screenHeight + '\n' + "左侧比例 " + leftScale + " 顶部比例 " + topScale + '\n' + "左侧边距大小 " + leftMargin + " 顶部边距大小 " +topMargin);

        return new int[]{leftMargin, topMargin};
    }

    private void resetAllPosOnScreen() {
        int[] i;

        i = calculateMarginsOnScreen(itemBar, 0.5f, 1);
        if (i != null) {
            itemBar.setMargins(i[0], i[1], 0, 0);
        }
    }

    private void saveConfigToFile() {
        SharedPreferences.Editor editor = context.getSharedPreferences(spFileName, spMode).edit();
        editor.putBoolean(sp_enable_ckb, switchCustomizeKeyboard.isChecked());
        editor.putBoolean(sp_enable_itembar, switchPEItembar.isChecked());
        editor.putBoolean(sp_enable_onscreentouchpad, switchTouchpad.isChecked());
        editor.putBoolean(sp_enable_debuginfo, switchDebugInfo.isChecked());
        if (!context.getSharedPreferences(spFileName, spMode).contains(sp_first_loadder)) {
            editor.putBoolean(sp_first_loadder, false);
        }
        editor.apply();

    }

    private void loadConfigFromFile() {
        SharedPreferences sp = context.getSharedPreferences(spFileName, spMode);
        switchCustomizeKeyboard.setChecked(sp.getBoolean(sp_enable_ckb, true));
        switchPEItembar.setChecked(sp.getBoolean(sp_enable_itembar, true));
        switchTouchpad.setChecked(sp.getBoolean(sp_enable_onscreentouchpad, true));
        switchDebugInfo.setChecked(sp.getBoolean(sp_enable_debuginfo, false));
        if (!sp.contains(sp_first_loadder)) {
            resetAllPosOnScreen();
            ((CustomizeKeyboard) custmoizeKeyboard).mManager.loadKeyboard(new CustomizeKeyboardMaker(context).createDefaultKeyboard());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        saveConfigToFile();
    }

    private static class VirtualControllerSetting extends H2CO3CustomViewDialog {
        public VirtualControllerSetting(@NonNull Context context) {
            super(context);
            setCustomView(R.layout.dialog_controller_functions);
        }
    }
}
