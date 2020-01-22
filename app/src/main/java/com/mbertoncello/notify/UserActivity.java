package com.mbertoncello.notify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mbertoncello.notify.callbacks.LogoutAPICallback;
import com.mbertoncello.notify.callbacks.UserAPICallback;

import java.util.HashMap;
import java.util.Map;

import static com.mbertoncello.notify.MyApplication.AUTH_TOKEN_PREFERENCE_KEY;
import static com.mbertoncello.notify.MyApplication.DEVICE_NAME_PREFERENCE_KEY;
import static com.mbertoncello.notify.MyApplication.EMAIL_PREFERENCE_KEY;
import static com.mbertoncello.notify.MyApplication.FIREBASE_INSTANCE_ID_PREFERENCE_KEY;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";
    private TextView emailText;
    private EditText deviceName;

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
                String auth_token = ((MyApplication) getApplicationContext()).preferences.getString(AUTH_TOKEN_PREFERENCE_KEY,"");
                String firebase_instance_id = ((MyApplication) getApplicationContext()).preferences.getString(FIREBASE_INSTANCE_ID_PREFERENCE_KEY,"");

                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type","application/x-www-form-urlencoded");
                headers.put("Auth-Token", auth_token);
                headers.put("Firebase-Instance-Id", firebase_instance_id);

                new NotifyGetRequest(getApplicationContext(), "/logout", headers, new LogoutAPICallback(getApplicationContext()));
            }
        });
    }

    /*
    Display the cached user details in the layout and update details from API.
     */
    private void displayUserDetails() {
        String auth_token = ((MyApplication) getApplicationContext()).preferences.getString(AUTH_TOKEN_PREFERENCE_KEY,"");
        Log.d(TAG, "auth_token: "+auth_token);

        // Load cached email and display.
        String emailCached = ((MyApplication) getApplicationContext()).preferences.getString(EMAIL_PREFERENCE_KEY,"Loading email...");
        this.emailText.setText(emailCached);

        // Load cached device name and display.
        String deviceNameCached = ((MyApplication) getApplicationContext()).preferences.getString(DEVICE_NAME_PREFERENCE_KEY,"no device name");
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
                            // the user is done typing.
                            Log.d(TAG, v.getText().toString());
                            return false; // pass on to other listeners (as opposed to consuming).
                        }
                    }
                    return false; // pass on to other listeners (as opposed to consuming)..
                }
            }
        );
    }
}

