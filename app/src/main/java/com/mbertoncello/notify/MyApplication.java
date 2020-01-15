package com.mbertoncello.notify;

import android.app.Application;
import android.content.SharedPreferences;

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    // Root Url for API calls.
    public static String ROOT_URL = "https://mbertoncello-staging.herokuapp.com/freelance/notify/api/v1";

    // Name of Private SharedPreference saved to device.
    public static String PREFERENCE_NAME = "com.mbertoncello.notify";

    // Keys for values in SharedPreference.
    public static String AUTH_TOKEN_PREFERENCE_KEY = "auth_token";
    public static String EMAIL_PREFERENCE_KEY = "email";

    /*
    Return true if auth_token is saved in SharedPreference. This indicates if the user has been authenticated.
     */
    public boolean isAuthenticated() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return preferences.contains(AUTH_TOKEN_PREFERENCE_KEY);
    }
}
