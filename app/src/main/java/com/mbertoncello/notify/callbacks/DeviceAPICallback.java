package com.mbertoncello.notify.callbacks;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import com.mbertoncello.notify.MyApplication;

import org.json.JSONObject;

import static com.mbertoncello.notify.MyApplication.DEVICE_NAME_PREFERENCE_KEY;

public class DeviceAPICallback implements APICallback {
    private String TAG = "DeviceAPICallback";
    private Context context;
    private EditText editText;
    private String deviceName;

    public DeviceAPICallback(Context context, EditText editText, String deviceName){
        this.context = context;
        this.editText = editText;
        this.deviceName = deviceName;
    }

    @Override
    public void onSuccess(JSONObject jsonObject) {
        Log.d(TAG, jsonObject.toString());
        ((MyApplication) context.getApplicationContext()).preferences.edit().putString(DEVICE_NAME_PREFERENCE_KEY, this.deviceName).commit();
        this.editText.setHint(this.deviceName);
        this.editText.setText("");
    }

    @Override
    public void onError(Integer statusCode, JSONObject jsonObject) {
        Log.d(TAG, "[ERROR] "+jsonObject.toString());
    }
}
