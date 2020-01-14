package com.mbertoncello.notify;

import android.app.Application;

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    public static String ROOT_URL = "http://192.168.0.83:5000/freelance/notify/api/v1";
    public static String PREFERENCE_NAME = "com.mbertoncello.notify";
    public static String AUTH_TOKEN_PREFERENCE = "auth_token";

}
