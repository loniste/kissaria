package com.ma.kissairaproject;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;

import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG ="TAG3" ;
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static String token;

    private static final String USER_INFO = "USER_INFO";
    private static final String IS_IN_FORGROUND = "IS_IN_FORGROUND";


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
        Log.d("onreceivedcalled","called");


        SharedPreferences sharedPreferences;
        sharedPreferences = getBaseContext().getSharedPreferences(USER_INFO, MODE_PRIVATE);



        createNotificationChannel();




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
        Log.d("JSON_OBJECT", object.toString());

        String title="";
        String body="";
        String type="";
        String shid="";
        String orid="";
        try {
            title=  object.getString("title");
            body=   object.getString("body");
            type=   object.getString("type");
            shid=   object.getString("shid");
            orid=   object.getString("orid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Create an Intent for the activity you want to start
        Intent tapIntent = new Intent(this, MainActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        tapIntent.setAction("TAP");
        tapIntent.putExtra("shid", shid);
        tapIntent.putExtra("orid", orid);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(tapIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent tapPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);




        if (type.equals("validate_reception")){
            Intent yesIntent = new Intent(this, NotifReceiver.class);
            yesIntent.setAction("YES");
            yesIntent.putExtra("shid", shid);
            yesIntent.putExtra("orid", orid);
            PendingIntent yesPendingIntent =
                    PendingIntent.getBroadcast(this, 0, yesIntent, 0);


            Intent noIntent = new Intent(this, NotifReceiver.class);
            noIntent.setAction("YES");
            noIntent.putExtra("shid", shid);
            noIntent.putExtra("orid", orid);
            PendingIntent noPendingIntent =
                    PendingIntent.getBroadcast(this, 0, noIntent, 0);








            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(tapPendingIntent)
                    .setSound(uri)
                    .addAction(0, getString(R.string.OUI), yesPendingIntent)
                    .addAction(0, getString(R.string.NON),noPendingIntent)
                    .setAutoCancel(true);


            //push notification only if activity is NOT running
            if (sharedPreferences.contains(IS_IN_FORGROUND)){
                boolean isActivityInFG = sharedPreferences.getBoolean(IS_IN_FORGROUND, false);
                if (!isActivityInFG){
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    notificationManager.notify(1, builder.build());
                }
            }
        }

        if (type.equals("refresh")){


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(tapPendingIntent)
                    .setSound(uri);

            if (sharedPreferences.contains(IS_IN_FORGROUND)){
                boolean isActivityInFG = sharedPreferences.getBoolean(IS_IN_FORGROUND, false);
                if (!isActivityInFG){
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    notificationManager.notify(2, builder.build());
                }
            }




            Intent intent = new Intent("unique_name");
            //put whatever data you want to send, if any
            intent.putExtra("message", object.toString());
            //send broadcast
            getApplicationContext().sendBroadcast(intent);
        }

        Log.d("type_is:", type);

    }
    private void createNotificationChannel(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}


