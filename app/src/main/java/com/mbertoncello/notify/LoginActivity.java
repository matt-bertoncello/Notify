package com.mbertoncello.notify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mbertoncello.notify.callbacks.LoginAPICallback;

import java.util.HashMap;
import java.util.Map;

import static com.mbertoncello.notify.MyApplication.FIREBASE_INSTANCE_ID_PREFERENCE_KEY;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private TextView errorField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Allocate errorField TextView.
        errorField = findViewById(R.id.errorText);

        // Get the login button and set onclick listener.
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewLoginDetails();
            }
        });
    }

    /*
    Read the EditText fields with id: 'email' and 'password'.
    Confirm these fields are populated.
    Attempt login with mbertoncello API.
     */
    private void reviewLoginDetails() {
        TextView errorField = findViewById(R.id.errorText);

        errorField.setText("");    // reset error text.
        EditText emailField = findViewById(R.id.email);
        EditText passwordField = findViewById(R.id.password);

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        Log.d(TAG, "email: "+email);
        Log.d(TAG, "password: "+password);

        // Validation check before calling API.
        if (email.isEmpty()) {
            errorField.setText("email cannot be empty");
        } else if (password.isEmpty()) {
            errorField.setText("password cannot be empty");
        } else {
            sendLoginToAPI(email, password);
        }
    }

    /*
    Handle configuration of GET API, and allocate success and error functions.
     */
    private void sendLoginToAPI(String email, String password) {
        // Get firebase_instance_id to send with login details in body of API.
        String firebase_instance_id = ((MyApplication) getApplicationContext()).preferences.getString(FIREBASE_INSTANCE_ID_PREFERENCE_KEY,"");

        // Call API to load current user details and display.
        Map<String,String> headers = new HashMap<String, String>();
        headers.put("Content-Type","application/x-www-form-urlencoded");
        headers.put("Email", email);
        headers.put("Password", password);
        headers.put("Firebase-Instance-Id", firebase_instance_id);

        new NotifyGetRequest(this, "/login", headers, new LoginAPICallback(this, errorField));
    }
}
