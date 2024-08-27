package org.koishi.launcher.h2co3.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.core.login.bean.UserBean;
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;

public class H2CO3Auth {

    private static final int HEAD_SIZE = 5000;
    private static final int HEAD_LEFT = 7;
    private static final int HEAD_TOP = 8;
    private static final int HEAD_RIGHT = 17;
    private static final int HEAD_BOTTOM = 16;

    private H2CO3Settings settings;

    public H2CO3Auth(H2CO3Settings settings) {
        this.settings = settings;
    }

    public void addUserToJson(String name, String email, String password, String userType, String apiUrl, String authSession, String uuid, String skinTexture, String token, String refreshToken, String clientToken, Boolean isOffline, boolean isSelected) {
        try {
            JSONObject jsonObj = settings.usersFile.exists() ? new JSONObject(readFileContent(settings.usersFile)) : new JSONObject();
            JSONObject userData = new JSONObject();
            userData.put(H2CO3Tools.LOGIN_USER_EMAIL, email);
            userData.put(H2CO3Tools.LOGIN_USER_PASSWORD, password);
            userData.put(H2CO3Tools.LOGIN_USER_TYPE, userType);
            userData.put(H2CO3Tools.LOGIN_API_URL, apiUrl);
            userData.put(H2CO3Tools.LOGIN_AUTH_SESSION, authSession);
            userData.put(H2CO3Tools.LOGIN_UUID, uuid);
            userData.put(H2CO3Tools.LOGIN_USER_SKINTEXTURE, skinTexture);
            userData.put(H2CO3Tools.LOGIN_TOKEN, token);
            userData.put(H2CO3Tools.LOGIN_REFRESH_TOKEN, refreshToken);
            userData.put(H2CO3Tools.LOGIN_CLIENT_TOKEN, clientToken);
            userData.put(H2CO3Tools.LOGIN_IS_OFFLINE, isOffline);
            userData.put(H2CO3Tools.LOGIN_IS_SELECTED, isSelected);
            userData.put(H2CO3Tools.LOGIN_INFO, new JSONArray().put(0, name).put(1, isOffline));
            jsonObj.put(name, userData);

            writeFileContent(settings.usersFile, jsonObj.toString());
            parseJsonToUser(jsonObj);
        } catch (JSONException | IOException e) {
            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
        }
    }

    public void parseJsonToUser(JSONObject usersObj) {
        if (usersObj == null || usersObj.length() == 0) {
            return;
        }

        try {
            Iterator<String> keys = usersObj.keys();
            while (keys.hasNext()) {
                String userName = keys.next();
                JSONObject userObj = usersObj.getJSONObject(userName);

                UserBean user = new UserBean();
                user.setUserName(userName);
                user.setUserEmail(userObj.optString(H2CO3Tools.LOGIN_USER_EMAIL, ""));
                user.setUserPassword(userObj.optString(H2CO3Tools.LOGIN_USER_PASSWORD, ""));
                user.setUserType(userObj.optString(H2CO3Tools.LOGIN_USER_TYPE, ""));
                user.setApiUrl(userObj.optString(H2CO3Tools.LOGIN_API_URL, ""));
                user.setAuthSession(userObj.optString(H2CO3Tools.LOGIN_AUTH_SESSION, ""));
                user.setUuid(userObj.optString(H2CO3Tools.LOGIN_UUID, ""));
                user.setSkinTexture(userObj.optString(H2CO3Tools.LOGIN_USER_SKINTEXTURE, ""));
                user.setToken(userObj.optString(H2CO3Tools.LOGIN_TOKEN, ""));
                user.setRefreshToken(userObj.optString(H2CO3Tools.LOGIN_REFRESH_TOKEN, ""));
                user.setClientToken(userObj.optString(H2CO3Tools.LOGIN_CLIENT_TOKEN, ""));
                user.setIsSelected(userObj.optBoolean(H2CO3Tools.LOGIN_IS_SELECTED, false));
                user.setIsOffline(userObj.optBoolean(H2CO3Tools.LOGIN_IS_OFFLINE, true));

                JSONArray loginInfoArray = userObj.optJSONArray(H2CO3Tools.LOGIN_INFO);
                if (loginInfoArray != null && loginInfoArray.length() >= 2) {
                    user.setUserInfo(loginInfoArray.optString(0, ""));
                    user.setUserPassword(loginInfoArray.optString(1, ""));
                }

                settings.userList.add(user);
            }
        } catch (JSONException e) {
            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
        }
    }

    public void resetUserState() {
        UserBean emptyUser = new UserBean();
        setUserState(emptyUser);
    }

    public ArrayList<UserBean> getUserList(JSONObject obj) {
        settings.userList.clear();
        parseJsonToUser(obj);
        return settings.userList;
    }

    public void setUserState(UserBean user) {
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_AUTH_PLAYER_NAME, user.getUserName());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USER_EMAIL, user.getUserEmail());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USER_PASSWORD, user.getUserPassword());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USER_TYPE, user.getUserType());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_API_URL, user.getApiUrl());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_AUTH_SESSION, user.getAuthSession());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_UUID, user.getUuid());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USER_SKINTEXTURE, user.getSkinTexture());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_TOKEN, user.getToken());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_REFRESH_TOKEN, user.getRefreshToken());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_CLIENT_TOKEN, user.getClientToken());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_INFO, user.getUserInfo());
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_IS_OFFLINE, user.getIsOffline());
    }

    public String getUserJson() {
        try {
            Log.e("TEST", "getUserJson: " + settings.usersFile);
            return readFileContent(settings.usersFile);
        } catch (IOException e) {
            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
        }
        return "";
    }

    public void setUserJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            writeFileContent(settings.usersFile, json);
            parseJsonToUser(jsonObject);
        } catch (JSONException | IOException e) {
            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
        }
    }

    private void writeFileContent(File file, String content) throws IOException {
        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
    }

    public String readFileContent(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    private final RequestOptions requestOptions = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public Drawable getHeadDrawable(Context context, String texture) {
        if (context == null || texture == null) {
            throw new IllegalArgumentException("Context or texture is null");
        }

        try {
            Bitmap headBitmap = decodeAndCropHeadBitmap(texture);
            return new BitmapDrawable(context.getResources(), headBitmap);
        } catch (Exception | OutOfMemoryError e) {
            throw new RuntimeException("Failed to get head drawable", e);
        }
    }

    public void getHead(Context context, String texture, ImageView imageView) {
        if (context == null || texture == null || imageView == null) {
            throw new IllegalArgumentException("Context, texture or imageView is null");
        }

        try {
            Bitmap headBitmap = decodeAndCropHeadBitmap(texture);
            Glide.with(context)
                    .load(headBitmap)
                    .apply(requestOptions)
                    .into(imageView);
        } catch (Exception | OutOfMemoryError e) {
            throw new RuntimeException("Failed to load head image", e);
        }
    }

    private Bitmap decodeAndCropHeadBitmap(String texture) {
        byte[] decodedBytes = Base64.decode(texture, Base64.DEFAULT);
        Bitmap skinBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        if (skinBitmap == null) {
            throw new RuntimeException("Failed to decode skin bitmap");
        }
        return cropHeadFromSkin(skinBitmap);
    }

    private Bitmap cropHeadFromSkin(Bitmap skinBitmap) {
        Bitmap headBitmap = Bitmap.createBitmap(HEAD_SIZE, HEAD_SIZE, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(headBitmap);
        Rect srcRect = new Rect(HEAD_LEFT, HEAD_TOP, HEAD_RIGHT, HEAD_BOTTOM);
        Rect dstRect = new Rect(0, 0, HEAD_SIZE, HEAD_SIZE);
        canvas.drawBitmap(skinBitmap, srcRect, dstRect, null);
        return headBitmap;
    }
}