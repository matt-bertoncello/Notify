package com.mbertoncello.notify;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface APICallback {
    void onSuccess(JSONObject jsonObject);

    void onError(Integer statusCode, JSONObject jsonObject);
}
