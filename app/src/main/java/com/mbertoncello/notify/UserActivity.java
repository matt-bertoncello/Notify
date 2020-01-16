package com.mbertoncello.notify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.mbertoncello.notify.MyApplication.AUTH_TOKEN_PREFERENCE_KEY;
import static com.mbertoncello.notify.MyApplication.EMAIL_PREFERENCE_KEY;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";
    private TextView emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If there is no Auth Token saved, redirect to login screen.
        if (!((MyApplication) getApplicationContext()).preferences.contains(AUTH_TOKEN_PREFERENCE_KEY)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        // If the Auth Token is saved, display details from cache and update user details from API.
        else {
            // display content.
            setContentView(R.layout.activity_user);
            this.emailText = findViewById(R.id.emailText);

            // set Logout button
            setLogoutButton();

            // display user details from cache and update user details from API
            displayUserDetails();

        }
    }

    // When logout button is pressed, delete SharedPreferences and redirect to main activity.
    private void setLogoutButton() {
        Button logoutButton = (Button) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete all values from SharedPreference.
                ((MyApplication) getApplicationContext()).preferences.edit().clear().commit();

                // Go to MainActivity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
    Display the cached user details in the layout and update details from API.
     */
    private void displayUserDetails() {
        String auth_token = ((MyApplication) getApplicationContext()).preferences.getString(AUTH_TOKEN_PREFERENCE_KEY,"");
        Log.d(TAG, "auth_token: "+auth_token);

        // Load cached user details and display.
        String emailCached = ((MyApplication) getApplicationContext()).preferences.getString(EMAIL_PREFERENCE_KEY,"Loading email...");
        this.emailText.setText(emailCached);

        // Call API to load current user details and display.
        Map<String,String> headers = new HashMap<String, String>();
        headers.put("Content-Type","application/x-www-form-urlencoded");
        headers.put("auth_token", auth_token);

        Map<String,String> body = new HashMap<String, String>();

        new NotifyGetRequest(this, "/user", headers, body, new UserAPICallback());
    }

    /*
    Define callback functions for '/user' endpoint response.
     */
    class UserAPICallback implements APICallback {
        private String TAG = "UserAPICallback";

        public UserAPICallback() {}

        @Override
        public void onSuccess(JSONObject jsonObject) {
            try {
                String emailAPI = jsonObject.getString("email");
                emailText.setText(emailAPI);

                // save email value to SharedPreference
                ((MyApplication) getApplicationContext()).preferences.edit().putString(EMAIL_PREFERENCE_KEY, emailAPI).commit();

            } catch (JSONException e) {
                Log.d(TAG, "could not find 'email' in response.");
            }
        }

        @Override
        public void onError(Integer statusCode, JSONObject jsonObject) {
            Log.d(TAG, "error: "+jsonObject);
        }
    }
}

