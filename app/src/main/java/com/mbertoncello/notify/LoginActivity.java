package com.mbertoncello.notify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.mbertoncello.notify.MyApplication.AUTH_TOKEN_PREFERENCE_KEY;
import static com.mbertoncello.notify.MyApplication.PREFERENCE_NAME;


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
        // Call API to load current user details and display.
        Map<String,String> params = new HashMap<String, String>();
        params.put("Content-Type","application/x-www-form-urlencoded");
        params.put("email", email);
        params.put("password", password);
        new NotifyGetRequest(this, "/login", params, new LoginAPICallback(this));
    }

    /*
Define callback functions for '/login' endpoint response.
Save auth_token to SharedPreferences storage and redirect to UserActivity.
 */
    class LoginAPICallback implements APICallback {
        private String TAG = "LoginAPICallback";
        private Context context;

        public LoginAPICallback(Context context) {
            this.context = context;
        }

        @Override
        public void onSuccess(JSONObject jsonObject) {
            try {
                String auth_token = jsonObject.getString("auth_token");

                Log.d(TAG, "auth_token: "+auth_token);

                // Save the auth_token to device storage.
                SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
                preferences.edit().putString(AUTH_TOKEN_PREFERENCE_KEY, auth_token).commit();

                // Redirect to User Activity.
                Intent intent = new Intent(context, UserActivity.class);
                context.startActivity(intent);

            } catch (JSONException e) {
                Log.d(TAG, "could not find 'auth_token' in response");
            }
        }

        @Override
        public void onError(Integer statusCode, JSONObject jsonObject) {
            if (statusCode == 401) {
                try {
                    String errorMessage = jsonObject.getString("message");
                    errorField.setText(errorMessage);

                } catch (JSONException e) {
                    Log.d(TAG, "error: "+jsonObject);
                }
            } else {
                Log.d(TAG, "error: "+jsonObject);
            }
        }
    }
}
