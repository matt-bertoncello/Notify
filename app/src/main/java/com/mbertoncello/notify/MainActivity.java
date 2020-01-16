package com.mbertoncello.notify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.content.Intent;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import static com.mbertoncello.notify.MyApplication.FIREBASE_INSTANCE_ID_PREFERENCE_KEY;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    /*
    Load activity_main.xml page.
    Find button with id='button'. When onclick, redirect to second activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);

        // If the user is authenticated, the button redirects to user page.
        if (((MyApplication) getApplicationContext()).isAuthenticated()){
            button.setText("User");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToUserActivity();
                }
            });
        }

        // If the user is not authenticated, the button redirects to login page.
        else {
            button.setText("Login");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToLoginActivity();
                }
            });
        }

        // Get token
        // [START retrieve_current_token]
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get Instance ID token
                        String token = task.getResult().getToken();

                        // save token to SharedPreferences
                        ((MyApplication) getApplicationContext()).preferences.edit().putString(FIREBASE_INSTANCE_ID_PREFERENCE_KEY, token).commit();
                    }
                });
        // [END retrieve_current_token]
    }

    /*
    Redirect to the login activity.
     */
    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /*
    Redirect to the user activity.
     */
    private void goToUserActivity() {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }
}
