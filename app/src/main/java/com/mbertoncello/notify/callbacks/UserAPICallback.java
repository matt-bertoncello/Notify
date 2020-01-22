package com.mbertoncello.notify.callbacks;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.mbertoncello.notify.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import static com.mbertoncello.notify.MyApplication.DEVICE_NAME_PREFERENCE_KEY;
import static com.mbertoncello.notify.MyApplication.EMAIL_PREFERENCE_KEY;

/*
    Define callback functions for '/user' endpoint response.
     */
public class UserAPICallback implements APICallback {
    private String TAG = "UserAPICallback";
    private Context context;
    private TextView emailText;
    private EditText deviceName;

    public UserAPICallback(Context context, TextView emailText, EditText deviceName) {
        this.context = context;
        this.emailText = emailText;
        this.deviceName = deviceName;
    }

    @Override
    public void onSuccess(JSONObject jsonObject) {
        try {
            String emailAPI = jsonObject.getString("email");
            emailText.setText(emailAPI);

            String deviceNameApi = jsonObject.getString("device-name");
            deviceName.setHint(deviceNameApi);

            // save values to SharedPreference
            ((MyApplication) context.getApplicationContext()).preferences.edit().putString(EMAIL_PREFERENCE_KEY, emailAPI).commit();
            ((MyApplication) context.getApplicationContext()).preferences.edit().putString(DEVICE_NAME_PREFERENCE_KEY, deviceNameApi).commit();


        } catch (JSONException e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public void onError(Integer statusCode, JSONObject jsonObject) {
        Log.d(TAG, "error: "+jsonObject);
    }
}
