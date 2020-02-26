package com.mbertoncello.notify;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    public SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        this.preferences = getSharedPreferences(getString(R.string.shared_preference_name), MODE_PRIVATE);
    }

    /*
    Return true if auth_token is saved in SharedPreference. This indicates if the user has been authenticated.
     */
    public boolean isAuthenticated() {
        return preferences.contains(getString(R.string.auth_token_preference_key));
    }

    /*
    Remove appropriate shared preferences on logout.
     */
    public void removeCache() {
        this.preferences.edit().remove(getString(R.string.auth_token_preference_key)).apply();
        this.preferences.edit().remove(getString(R.string.email_preference_key)).apply();
        this.preferences.edit().remove(getString(R.string.firebase_instance_preference_key)).apply();
        this.preferences.edit().remove(getString(R.string.secret_preference_key)).apply();
    }
}
