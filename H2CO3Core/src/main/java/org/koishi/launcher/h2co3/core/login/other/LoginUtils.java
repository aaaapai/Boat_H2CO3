package org.koishi.launcher.h2co3.core.login.other;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginUtils {
    private static final LoginUtils INSTANCE = new LoginUtils();
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private String baseUrl;

    private LoginUtils() {
    }

    public static LoginUtils getINSTANCE() {
        return INSTANCE;
    }

    public void setBaseUrl(String baseUrl) {
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        this.baseUrl = baseUrl;
        System.out.println(this.baseUrl);
    }

    public void login(String userName, String password, Listener listener) throws IOException {
        Objects.requireNonNull(baseUrl, "no baseUrl");
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(userName);
        authRequest.setPassword(password);
        AuthRequest.Agent agent = new AuthRequest.Agent();
        agent.setName("Boat_H2CO3");
        agent.setVersion(1.0);
        authRequest.setAgent(agent);
        authRequest.setRequestUser(true);
        authRequest.setClientToken("Boat_H2CO3");
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), gson.toJson(authRequest));
        Request request = new Request.Builder()
                .url(baseUrl + "/authserver/authenticate")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            System.out.println(res);
            if (response.code() == 200) {
                AuthResult result = gson.fromJson(res, AuthResult.class);
                listener.onSuccess(result);
            } else {
                listener.onFailed(response.code() + "\n" + res);
            }
        }
    }

    public String getServeInfo(String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String res = response.body().string();
                System.out.println(res);
                if (response.code() == 200) {
                    return res;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface Listener {
        void onSuccess(AuthResult authResult);

        void onFailed(String error);
    }
}
