package com.mbertoncello.notify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.mbertoncello.notify.MyApplication.AUTH_TOKEN_PREFERENCE_KEY;
import static com.mbertoncello.notify.MyApplication.EMAIL_PREFERENCE_KEY;
import static com.mbertoncello.notify.MyApplication.PREFERENCE_NAME;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load private preferences.
        SharedPreferences preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);

        // If there is no Auth Token saved, redirect to login screen.
        if (!preferences.contains(AUTH_TOKEN_PREFERENCE_KEY)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        // If the Auth Token is saved, display details from cache and update user details from API.
        else {
            // display content.
            setContentView(R.layout.activity_user);

            // set Logout button
            setLogoutButton(preferences);

            // display user details from cache and update user details from API
            displayUserDetails(preferences);

        }
    }

    // When logout button is pressed, delete SharedPreferences and redirect to main activity.
    private void setLogoutButton(SharedPreferences preferences) {
        Button logoutButton = (Button) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete all values from SharedPreference.
                preferences.edit().clear().commit();

                // Go to MainActivity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
    Display the cached user details in the layout and update details from API.
     */
    private void displayUserDetails(SharedPreferences preferences) {
        String auth_token = preferences.getString(AUTH_TOKEN_PREFERENCE_KEY,"");
        Log.d(TAG, "auth_token: "+auth_token);

        // Load cached user details and display.
        String emailCached = preferences.getString(EMAIL_PREFERENCE_KEY,"Loading email...");
        TextView emailText = findViewById(R.id.emailText);
        emailText.setText(emailCached);

        // Call API to load current user details and display.
        Map<String,String> params = new HashMap<String, String>();
        params.put("Content-Type","application/x-www-form-urlencoded");
        params.put("auth_token", auth_token);
        new NotifyGetRequest(this, "/user", params, new UserAPICallback(emailText));
    }
}

/*
Define callback functions for '/user' endpoint response.
 */
class UserAPICallback implements APICallback {
    private static String TAG = "UserAPICallback";
    private TextView emailText;

    public UserAPICallback(TextView emailText) { this.emailText = emailText; }

    @Override
    public void onSuccess(JSONObject jsonObject) {
        try {
            String emailAPI = jsonObject.getString("email");
            this.emailText.setText(emailAPI);
        } catch (JSONException e) {
            Log.d(TAG, "could not find 'email' in response.");
        }
    }

    @Override
    public void onError(Integer statusCode, JSONObject jsonObject) {
        Log.d(TAG, "error: "+jsonObject);
    }
}
