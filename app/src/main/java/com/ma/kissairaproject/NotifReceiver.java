package com.ma.kissairaproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class NotifReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("action_received", "action received");
        Bundle bundle=intent.getExtras();
        String shid= bundle != null ? bundle.getString("shid") : "";
        String orid= bundle != null ? bundle.getString("orid") : "";
        String answer = Objects.equals(intent.getAction(), "YES") ? "true": (Objects.equals(intent.getAction(), "NO") ? "false": "error");
        String result="";
        BackgroundWorker backgroundWorker= new BackgroundWorker();

        if (!answer.equals("error")){
            try {
                result=backgroundWorker.execute("notif_yes_no", shid, orid, answer).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(context, "Result is"+ result, Toast.LENGTH_SHORT).show();
//        if (result==""){
//            Toast.makeText(arg0, result, Toast.LENGTH_SHORT).show();
//        }
    }

}