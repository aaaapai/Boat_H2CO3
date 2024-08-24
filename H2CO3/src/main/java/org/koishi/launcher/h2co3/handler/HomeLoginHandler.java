package org.koishi.launcher.h2co3.handler;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.core.H2CO3Auth;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.Texture.Texture;
import org.koishi.launcher.h2co3.core.login.Texture.TextureType;
import org.koishi.launcher.h2co3.core.login.microsoft.MicrosoftLoginUtils;
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3.core.utils.Avatar;
import org.koishi.launcher.h2co3.ui.fragment.home.HomeFragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeLoginHandler extends Handler {
    private final HomeFragment fragment;

    public HomeLoginHandler(HomeFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
    }

    public void login(Intent intent) {
        Uri data = intent != null ? intent.getData() : null;
        if (data != null && Objects.equals(data.getScheme(), "ms-xal-00000000402b5328") && Objects.equals(data.getHost(), "auth")) {
            String error = data.getQueryParameter("error");
            String errorDescription = data.getQueryParameter("error_description");
            if (error != null) {
                if (errorDescription != null && !errorDescription.startsWith("The user has denied access to the scope requested by the h2CO3ControlClient application")) {
                    H2CO3Tools.showError(H2CO3MessageManager.NotificationItem.Type.ERROR, error + ": " + errorDescription);
                }
            } else {
                String code = data.getQueryParameter("code");
                if (code == null) {
                    H2CO3Tools.showError(H2CO3MessageManager.NotificationItem.Type.ERROR, "Code is null");
                    return;
                }
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    try {
                        MicrosoftLoginUtils microsoftLoginUtils = new MicrosoftLoginUtils(false, code);
                        if (microsoftLoginUtils.doesOwnGame) {
                            MicrosoftLoginUtils.MinecraftProfileResponse minecraftProfile = MicrosoftLoginUtils.getMinecraftProfile(microsoftLoginUtils.tokenType, microsoftLoginUtils.mcToken);
                            Map<TextureType, Texture> textureMap = MicrosoftLoginUtils.getTextures(minecraftProfile).get();
                            Texture skinTexture = textureMap.get(TextureType.SKIN);
                            Bitmap skin = getSkinBitmap(skinTexture);
                            if (skin != null) {
                                fragment.requireActivity().runOnUiThread(() -> {
                                    String skinTextureString = Avatar.bitmapToString(skin);
                                    H2CO3Auth.addUserToJson(microsoftLoginUtils.mcName, "", "", "1", "https://www.microsoft.com", "0", microsoftLoginUtils.mcUuid, skinTextureString, microsoftLoginUtils.mcToken, microsoftLoginUtils.msRefreshToken, "00000000-0000-0000-0000-000000000000", false, false);
                                    fragment.reLoadUser();
                                    fragment.loginDialogAlert.dismiss();
                                    fragment.progressDialog.dismiss();
                                });
                            }
                        }
                    } catch (Exception e) {
                        H2CO3Tools.showError(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
                    }
                });
                executorService.shutdown();
            }
        }
    }

    private Bitmap getSkinBitmap(Texture texture) {
        if (texture == null) {
            return loadDefaultSkin();
        } else {
            String url = texture.getUrl();
            if (url != null && !url.startsWith("https")) {
                url = url.replaceFirst("http", "https");
            }
            return downloadBitmap(url);
        }
    }

    private Bitmap loadDefaultSkin() {
        try (InputStream inputStream = fragment.requireActivity().getAssets().open("drawable/alex.png")) {
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            H2CO3Tools.showError(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
            return null;
        }
    }

    private Bitmap downloadBitmap(String urlString) {
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            return BitmapFactory.decodeStream(httpURLConnection.getInputStream());
        } catch (IOException e) {
            H2CO3Tools.showError(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
            return null;
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }
}