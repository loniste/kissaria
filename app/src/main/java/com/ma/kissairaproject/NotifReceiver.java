package com.ma.kissairaproject;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotifReceiver extends BroadcastReceiver
{
    private static final String SHID_ID = "SHID_ID";
    private static final String ORDER_ID = "ORDER_ID";
    private static final String STRING_OBJECT = "STRING_OBJECT";
    private static final String NOTIFICATION_ID = "NOTIFICATION_ID" ;


    String shid;
    String orid;
    String stringObject;
    int notification_id;

    Context c;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        c=context;
        Log.d("action_received", "action received");
        Bundle bundle=intent.getExtras();
//        String shid=intent.getStringExtra("shid");
//        String orid=intent.getStringExtra("orid");
        shid= bundle != null ? bundle.getString(SHID_ID) : "";//TODO code not optimized, in fact, stringbject contains already shid and orid
        orid= bundle != null ? bundle.getString(ORDER_ID) : "";
        notification_id= bundle != null ? bundle.getInt(NOTIFICATION_ID) : 0;
        Log.d("recd_notification_id", notification_id+"");

        stringObject= bundle != null ? bundle.getString(STRING_OBJECT) : "";

        Log.d("orid_in_notifReceiver",orid+"");
        Log.d("shid_in_notifReceiver",shid+"");
        String answer = Objects.equals(intent.getAction(), "YES") ? "true": (Objects.equals(intent.getAction(), "NO") ? "false": "error");
        String result="";
        String result2="";
        BackgroundWorker backgroundWorker= new BackgroundWorker();
        BackgroundWorker backgroundWorker2= new BackgroundWorker();
        /**Cancel notifications**/
        NotificationManager nm = (NotificationManager)  context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
//            nm.cancelAll();
            nm.cancel(notification_id);
        }else Log.d("notif_problem", "notif manager is null");
        if (!answer.equals("error")){
            try {
                result=backgroundWorker.execute("notif_yes_no", shid, orid, answer).get();
                result2=backgroundWorker2.execute("post_status_customer", shid , orid,"received").get();
                JSONObject jsonObject = new JSONObject(result);
                sendBroadcastToRefresh();

                if (!jsonObject.getString("status").equals("success")){
                    //TODO: setting back the previous buttons status
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("notifreceiver", result+";;;;;"+result2);
        Toast.makeText(context, "Result is"+ result, Toast.LENGTH_SHORT).show();
//        if (result==""){
//            Toast.makeText(arg0, result, Toast.LENGTH_SHORT).show();
//        }
    }
    private void sendBroadcastToRefresh() {
        Intent intent = new Intent("unique_name");
        //put whatever data you want to send, if any
        intent.putExtra("message", stringObject);
        //send broadcast
        c.sendBroadcast(intent);
    }
}