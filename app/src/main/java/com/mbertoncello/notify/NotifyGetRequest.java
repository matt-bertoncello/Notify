package com.mbertoncello.notify;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.util.Map;

import static com.mbertoncello.notify.MyApplication.ROOT_URL;

/*
Object to handle the API Get Request to an endpoint hosted at the ROOT_URL.
 */
public class NotifyGetRequest {

    private static final String TAG = "NotifyGetRequest";
    private JSONObject jsonObject;

    /*
    Call the endpoint from ROOT_URL.
    Can access the body of the response with @getJSONFromKey.
    params = list of headers in a Map.
     */
    public NotifyGetRequest(Context context, String endpoint, Map headers, Map body, APICallback callback) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ROOT_URL+endpoint;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d(TAG, "response: "+response);

                            // run callback function.
                            callback.onSuccess(jsonObject);
                        } catch (JSONException e) {
                            Log.d(TAG, "error with response: "+response+" - "+e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    // Catch error and convert data to JSON.
                    public void onErrorResponse(VolleyError error) {
                        // If connection error, toast error and return user to Main Activity
                        if (error instanceof NoConnectionError) {
                            String msg = context.getString(R.string.no_format, "No Connection Error: 102");
                            Log.d(TAG, msg);
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent);
                        }

                        // If it is another error, pass it through to the callback error function.
                        else {
                            try {
                                Log.d(TAG, error.toString());
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                Log.d(TAG, "error: "+responseBody);
                                JSONObject jsonObject = new JSONObject(responseBody);
                                callback.onError(error.networkResponse.statusCode, jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }) {
            @Override
            protected Map<String,String> getParams(){
                return body;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /*
    Called when API returns successful.
    Convert body response into JSON.
     */
    private void jsonify(String response, APICallback callback){

    }

}
