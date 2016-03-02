package net.techguild.base.data.manager;

import android.content.SharedPreferences;

import net.techguild.base.data.model.User;
import net.techguild.base.data.response.LoginResponse;
import net.techguild.base.util.CLog;

// Stores and retrieves user information from Shared Prefs and holds user data
public class UserStore {
    private static final String ACCESS_TOKEN_PREF_KEY = "token";
    private static final String ACCESS_TOKEN_EXPIRE_TIME_KEY = "expire_at";

    private final SharedPreferences sharedPreferences;
    private boolean loggedIn;
    private String accessToken;
    private User user;

    public UserStore(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void initToken() {
        String token = sharedPreferences.getString(ACCESS_TOKEN_PREF_KEY, null);
        long expireAt = sharedPreferences.getLong(ACCESS_TOKEN_EXPIRE_TIME_KEY, 0);

        if (token != null && expireAt != 0) {

            if ((expireAt * 1000) >= System.currentTimeMillis()) {
                // Not expired yet
                CLog.i("T", "Key updated from sharedPrefs");
                setAccessToken(token);
                loggedIn = true;
            } else {
                CLog.t("Key expired");

                // Keys expired, clear current keys
                clearSharedPrefKeys();
            }
        } else {
            CLog.t("Sharedpref tokens are empty");
        }
    }

    public void updateToken(String accessToken, long expireTime) {
        sharedPreferences.edit()
                .putString(ACCESS_TOKEN_PREF_KEY, accessToken)
                .putLong(ACCESS_TOKEN_EXPIRE_TIME_KEY, expireTime).apply();

        setAccessToken(accessToken);
        loggedIn = true;
    }

    private void clearSharedPrefKeys() {
        sharedPreferences.edit()
                .putString(ACCESS_TOKEN_PREF_KEY, null)
                .putLong(ACCESS_TOKEN_EXPIRE_TIME_KEY, 0).apply();
    }

    public void updateUserCredentials(LoginResponse loginResponse) {
        this.user = loginResponse.user;

        updateToken(loginResponse.token, loginResponse.expireTime);
    }

    public void onLogout() {
        user = null;
        setAccessToken(null);
        loggedIn = false;
        clearSharedPrefKeys();
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
