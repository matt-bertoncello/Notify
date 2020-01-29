package com.mbertoncello.notify.callbacks;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.mbertoncello.notify.MyApplication;
import com.mbertoncello.notify.UserActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static com.mbertoncello.notify.MyApplication.AUTH_TOKEN_PREFERENCE_KEY;
import static com.mbertoncello.notify.MyApplication.SECRET_PREFERENCE_KEY;

/*
    Define callback functions for '/login' endpoint response.
    Save auth_token to SharedPreferences storage and redirect to UserActivity.
     */
public class LoginAPICallback implements APICallback {
    private String TAG = "LoginAPICallback";
    private Context context;
    private TextView errorField;

    public LoginAPICallback(Context context, TextView errorField) {
        this.context = context;
        this.errorField = errorField;
    }

    @Override
    public void onSuccess(JSONObject jsonObject) {
        try {
            String auth_token = jsonObject.getString("auth-token");
            String secret = jsonObject.getString("secret");

            Log.d(TAG, "auth_token: "+auth_token);

            // Save the auth_token and secret to device storage.
            ((MyApplication) context.getApplicationContext()).preferences.edit().putString(AUTH_TOKEN_PREFERENCE_KEY, auth_token).commit();
            ((MyApplication) context.getApplicationContext()).preferences.edit().putString(SECRET_PREFERENCE_KEY, secret).commit();

            // Redirect to User Activity.
            Intent intent = new Intent(context, UserActivity.class);
            context.startActivity(intent);

        } catch (JSONException e) {
            Log.d(TAG, "could not find 'auth_token' in response");
        }
    }

    @Override
    public void onError(Integer statusCode, JSONObject jsonObject) {
        if (statusCode == 401 || statusCode == 500) {
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
