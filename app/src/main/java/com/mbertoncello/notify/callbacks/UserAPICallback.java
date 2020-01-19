package com.mbertoncello.notify.callbacks;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.mbertoncello.notify.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import static com.mbertoncello.notify.MyApplication.EMAIL_PREFERENCE_KEY;

/*
    Define callback functions for '/user' endpoint response.
     */
public class UserAPICallback implements APICallback {
    private String TAG = "UserAPICallback";
    private Context context;
    private TextView emailText;

    public UserAPICallback(Context context, TextView emailText) {
        this.context = context;
        this.emailText = emailText;
    }

    @Override
    public void onSuccess(JSONObject jsonObject) {
        try {
            String emailAPI = jsonObject.getString("email");
            emailText.setText(emailAPI);

            // save email value to SharedPreference
            ((MyApplication) context.getApplicationContext()).preferences.edit().putString(EMAIL_PREFERENCE_KEY, emailAPI).commit();

        } catch (JSONException e) {
            Log.d(TAG, "could not find 'email' in response.");
        }
    }

    @Override
    public void onError(Integer statusCode, JSONObject jsonObject) {
        Log.d(TAG, "error: "+jsonObject);
    }
}
