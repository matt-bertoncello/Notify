package com.mbertoncello.notify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.mbertoncello.notify.MyApplication.AUTH_TOKEN_PREFERENCE;
import static com.mbertoncello.notify.MyApplication.ROOT_URL;
import static com.mbertoncello.notify.MyApplication.PREFERENCE_NAME;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ROOT_URL+"/login";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        onAPIResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: "+error);
                    }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    // Display the first 500 characters of the response string.
    private void onAPIResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String auth_token = jsonObject.getString("auth_token");

            Log.d(TAG, "auth_token: "+auth_token);

            // Save the auth_token to device storage.
            SharedPreferences preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
            preferences.edit().putString(AUTH_TOKEN_PREFERENCE, auth_token).commit();

            // Redirect to User Activity.
            Intent intent = new Intent(this, UserActivity.class);
            startActivity(intent);

        } catch (JSONException e) {
            Log.d(TAG, "error with response: "+response+" "+e.toString());
        }
    }
}
