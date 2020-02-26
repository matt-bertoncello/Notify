package com.mbertoncello.notify;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    // Root Url for API calls.
    //public static String ROOT_URL = "https://not1fy-staging.herokuapp.com/api/v1";
    //public static String ROOT_URL = "http://192.168.0.240:5001/api/client-v1";
    public static String ROOT_URL = "https://not1fy.herokuapp.com/api/client-v1";

    // Name of Private SharedPreference saved to device.
    public static String PREFERENCE_NAME = "com.mbertoncello.notify";

    // Keys for values in SharedPreference.
    public static String DEVICE_NAME_PREFERENCE_KEY = "device_name";
    public static String AUTH_TOKEN_PREFERENCE_KEY = "auth_token";
    public static String EMAIL_PREFERENCE_KEY = "email";
    public static String FIREBASE_INSTANCE_ID_PREFERENCE_KEY = "firebase_instance_id";
    public static String SECRET_PREFERENCE_KEY = "secret";

    public SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        this.preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);

        this.createNotificationChannel();
    }

    /*
    Return true if auth_token is saved in SharedPreference. This indicates if the user has been authenticated.
     */
    public boolean isAuthenticated() {
        return preferences.contains(AUTH_TOKEN_PREFERENCE_KEY);
    }

    /*
    Remove appropriate shared preferences on logout.
     */
    public void removeCache() {
        this.preferences.edit().remove(AUTH_TOKEN_PREFERENCE_KEY).apply();
        this.preferences.edit().remove(EMAIL_PREFERENCE_KEY).apply();
        this.preferences.edit().remove(FIREBASE_INSTANCE_ID_PREFERENCE_KEY).apply();
        this.preferences.edit().remove(SECRET_PREFERENCE_KEY).apply();
    }

    // Create notification channel (category).If the channel is aready created, this does nothing.
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.default_channel_name);
            String description = getString(R.string.default_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("DEFAULT", name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
