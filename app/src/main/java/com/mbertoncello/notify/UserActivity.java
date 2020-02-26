package com.mbertoncello.notify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mbertoncello.notify.apiRequests.NotifyGetRequest;
import com.mbertoncello.notify.apiRequests.NotifyPostRequest;
import com.mbertoncello.notify.callbacks.DeviceAPICallback;
import com.mbertoncello.notify.callbacks.LogoutAPICallback;
import com.mbertoncello.notify.callbacks.UserAPICallback;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";
    private TextView emailText;
    private EditText deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If there is no Auth Token saved, redirect to login screen.
        if (!((MyApplication) getApplicationContext()).preferences.contains(getString(R.string.auth_token_preference_key))) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        // If the Auth Token is saved, display details from cache and update user details from API.
        else {
            // display content.
            setContentView(R.layout.activity_user);
            this.emailText = findViewById(R.id.emailText);
            this.deviceName = findViewById(R.id.deviceName);

            // set Logout button
            setLogoutButton();

            setDeviceNameInput();

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
                // tell API server this user has logged out.
                String auth_token = ((MyApplication) getApplicationContext()).preferences.getString(getString(R.string.auth_token_preference_key),"");

                Map<String,String> params = new HashMap<String, String>();
                params.put("test", "test body");

                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type","application/x-www-form-urlencoded");
                headers.put("Auth-Token", auth_token);

                new NotifyPostRequest(getApplicationContext(), "/logout", headers, params, new LogoutAPICallback(getApplicationContext()));
            }
        });
    }

    /*
    Display the cached user details in the layout and update details from API.
     */
    private void displayUserDetails() {
        String auth_token = ((MyApplication) getApplicationContext()).preferences.getString(getString(R.string.auth_token_preference_key),"");
        Log.d(TAG, "auth_token: "+auth_token);

        // Load cached email and display.
        String emailCached = ((MyApplication) getApplicationContext()).preferences.getString(getString(R.string.email_preference_key),"Loading email...");
        this.emailText.setText(emailCached);

        // Load cached device name and display.
        String deviceNameCached = ((MyApplication) getApplicationContext()).preferences.getString(getString(R.string.device_name_preference_key),"no device name");
        this.deviceName.setHint(deviceNameCached);

        // Call API to load current user details and display.
        Map<String,String> headers = new HashMap<String, String>();
        headers.put("Content-Type","application/x-www-form-urlencoded");
        headers.put("Auth-Token", auth_token);
        new NotifyGetRequest(this, "/user", headers, new UserAPICallback(this, this.emailText, this.deviceName));
    }

    private void setDeviceNameInput() {
        // set Device Name value and listener
        this.deviceName.setOnEditorActionListener(
            new EditText.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                            actionId == EditorInfo.IME_ACTION_DONE ||
                            event != null &&
                                    event.getAction() == KeyEvent.ACTION_DOWN &&
                                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        if (event == null || !event.isShiftPressed()) {
                            // the user is done typing. Send post request to update device-name in server.
                            Log.d(TAG, "Updating device-name to: "+v.getText().toString());

                            Map<String,String> params = new HashMap<String, String>();;
                            params.put("Device-Name", v.getText().toString());

                            Map<String,String> headers = new HashMap<String, String>();
                            String auth_token = ((MyApplication) getApplicationContext()).preferences.getString(getString(R.string.auth_token_preference_key),"");
                            headers.put("Auth-Token", auth_token);

                            new NotifyPostRequest(getApplicationContext(), "/device", headers, params, new DeviceAPICallback(getApplicationContext(), deviceName, v.getText().toString()));

                            return false; // pass on to other listeners (as opposed to consuming).
                        }
                    }
                    return false; // pass on to other listeners (as opposed to consuming)..
                }
            }
        );
    }
}

