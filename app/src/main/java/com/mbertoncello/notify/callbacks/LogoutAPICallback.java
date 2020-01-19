package com.mbertoncello.notify.callbacks;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.mbertoncello.notify.MainActivity;
import com.mbertoncello.notify.MyApplication;
import com.mbertoncello.notify.R;

import org.json.JSONException;
import org.json.JSONObject;

/*
Define callback functions for '/logout' endpoint response.
API server will remove the firebaseInstance from the user defined by auth_token.
 */
public class LogoutAPICallback implements APICallback {
    private String TAG = "LogoutAPICallback";
    private Context context;

    public LogoutAPICallback(Context context) { this.context = context; }

    @Override
    public void onSuccess(JSONObject jsonObject) {
        // Delete appropriate values from SharedPreference.
        ((MyApplication) context.getApplicationContext()).removeCache();

        // Go to MainActivity
        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onError(Integer statusCode, JSONObject jsonObject) {
        // Toast error.
        if (statusCode == 401) {
            try {
                String errorMessage = jsonObject.getString("message");
                String msg = context.getString(R.string.no_format, errorMessage);
                Log.d(TAG, msg);
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                Log.d(TAG, "error: "+jsonObject);
            }
        } else {
            Log.d(TAG, "error: "+jsonObject);
        }
    }
}
