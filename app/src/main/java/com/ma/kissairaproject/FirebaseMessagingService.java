package com.ma.kissairaproject;


import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;


import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG ="TAG3" ;
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String NOTIFICATION_ID = "NOTIFICATION_ID" ;
    private static final String USER_INFO = "USER_INFO";
    private static final String IS_IN_FORGROUND = "IS_IN_FORGROUND";
    private static final String SHID_ID = "SHID_ID";
    private static final String ORDER_ID = "ORDER_ID";
    private static final String STRING_OBJECT = "STRING_OBJECT";

    private static int requestCode = 1;
    private static int notificationID = 777;
    private static String token;



    String title="";
    String body="";
    String type="";
    String shid="";
    String orid="";
    Uri uri;
    Bundle bundle;

    NotificationCompat.Builder builder;
    PendingIntent tapPendingIntent;
    JSONObject object;


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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("onreceivedcalled","called");


        SharedPreferences sharedPreferences;
        sharedPreferences = getBaseContext().getSharedPreferences(USER_INFO, MODE_PRIVATE);
        Log.e("dataChat",remoteMessage.getData().toString());
        Map<String, String> params = remoteMessage.getData();
        object = new JSONObject(params);
        Log.d("JSON_OBJECT", object.toString());

        try {
            title=  object.getString("title");
            body=   object.getString("body");
            type=   object.getString("type");
            shid=   object.getString("shid");
            orid=   object.getString("orid");
            Log.d("received_by_notif","orid = "+orid);
            Log.d("received_by_notif","shid = "+shid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // Create an Intent for the activity you want to start
        Intent tapIntent = new Intent(this, MainActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        tapIntent.setAction("TAP");

        Bundle bundle= new Bundle();
        bundle.putString(SHID_ID, shid);
        bundle.putString(ORDER_ID, orid);
        bundle.putString(STRING_OBJECT, object.toString());

        tapIntent.putExtras(bundle);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(tapIntent);
        // Get the PendingIntent containing the entire back stack
        tapPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        if (type.equals("validate_reception")){

            buildYesNoNotification();

            //push notification only if activity is NOT running
            if (sharedPreferences.contains(IS_IN_FORGROUND)){
                boolean isActivityInFG = sharedPreferences.getBoolean(IS_IN_FORGROUND, false);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(notificationID, builder.build());
                if (isActivityInFG){
                    sendBroadcastToRefresh();//if app is running so just refresh app instead
                }
            }
        }
        if (type.equals("refresh")){
            buildStandardNotification();
            if (sharedPreferences.contains(IS_IN_FORGROUND)){
                boolean isActivityInFG = sharedPreferences.getBoolean(IS_IN_FORGROUND, false);
                if (!isActivityInFG){// if activity is not running then show notification
//                    notificationID++;
//                    bundle.putInt(NOTIFICATION_ID, notificationID);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    notificationManager.notify(notificationID, builder.build());
                }
                else  sendBroadcastToRefresh();//if app is running so just refresh app instead
            }
        }
        Log.d("type_is:", type);
    }

    private void buildStandardNotification() {
        String notificationChannelId =
                NotificationUtil.createNotificationChannel(this);
        builder = new NotificationCompat.Builder(this, notificationChannelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(tapPendingIntent)
                .setSound(uri);
    }

    private void sendBroadcastToRefresh() {
        Intent intent = new Intent("unique_name");
        //put whatever data you want to send, if any
        intent.putExtra("message", object.toString());
        //send broadcast
        getApplicationContext().sendBroadcast(intent);
    }

    private void buildYesNoNotification() {
        title="Confirmation de la réception";
        body="Avez-vous bien reçu votre commande?";
        Intent yesIntent = new Intent(this, NotifReceiver.class);
        yesIntent.setAction("YES");
        Log.d("orid_inFirebase",orid);

        bundle= new Bundle();
        bundle.putString(SHID_ID, shid);
        bundle.putString(ORDER_ID, orid);
        bundle.putString(STRING_OBJECT, object.toString());
        notificationID++;
        bundle.putInt(NOTIFICATION_ID, notificationID);
        Log.d("sent_notification_id", notificationID+"");


        yesIntent.putExtras(bundle);
        requestCode++;//I heard that changing request code will ensure not using the sae intent
        PendingIntent yesPendingIntent =
                PendingIntent.getBroadcast(this, requestCode, yesIntent, 0);
        Intent noIntent = new Intent(this, NotifReceiver.class);
        noIntent.setAction("NO");

        bundle= new Bundle();
        bundle.putString(SHID_ID, shid);
        bundle.putString(ORDER_ID, orid);
        bundle.putString(STRING_OBJECT, object.toString());

        requestCode++;
        noIntent.putExtras(bundle);
        PendingIntent noPendingIntent =
                PendingIntent.getBroadcast(this, requestCode, noIntent, 0);
        String notificationChannelId =
                NotificationUtil.createNotificationChannel(this);
        builder = new NotificationCompat.Builder(this, notificationChannelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(tapPendingIntent)
                .setSound(uri)
                .addAction(0, getString(R.string.OUI), yesPendingIntent)
                .addAction(0, getString(R.string.NON),noPendingIntent)
                .setAutoCancel(true);
    }
}


