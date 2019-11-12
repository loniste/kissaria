package com.ma.kissairaproject;


import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;

import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONObject;

import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG ="TAG3" ;
    private static String token;
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        this.token=token;
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);

    }
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
    public static String getToken(){
        return token;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        /*Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), "New notification !", Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                getApplicationContext().startActivity(intent);
            }
        });
*/
        // This function will create an intent. This intent must take as parameter the "unique_name" that you registered your activity with




        Log.e("dataChat",remoteMessage.getData().toString());

        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);
        Log.e("JSON_OBJECT", object.toString());


        Intent intent = new Intent("unique_name");

        //put whatever data you want to send, if any
        intent.putExtra("message", object.toString());

        //send broadcast
        getApplicationContext().sendBroadcast(intent);



    }

}