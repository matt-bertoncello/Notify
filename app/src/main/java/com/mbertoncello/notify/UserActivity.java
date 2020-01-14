package com.mbertoncello.notify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import static com.mbertoncello.notify.MyApplication.AUTH_TOKEN_PREFERENCE;
import static com.mbertoncello.notify.MyApplication.PREFERENCE_NAME;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load private preferences.
        SharedPreferences preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);

        // If there is no Auth Token saved, redirect to login screen.
        if (!preferences.contains(AUTH_TOKEN_PREFERENCE)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        // If the Auth Token is saved, display details from cache and update user details from API.
        else {
            String auth_token = preferences.getString(AUTH_TOKEN_PREFERENCE,"");
            Log.d(TAG, "auth_token: "+auth_token);
            setContentView(R.layout.activity_user);
        }
    }
}
