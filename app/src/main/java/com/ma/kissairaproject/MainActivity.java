package com.ma.kissairaproject;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
//import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

//import static com.ma.kissairaproject.LoginDatabaseAdapter.email;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG2";


    String token="tokenWitoutInitialization";


    SharedPreferences sharedPreferences;
    String userId="";
    String email="";
    String password="";
    private static final String USER_INFO = "USER_INFO";
    private static final String EMAIL = "EMAIL";
    private static final String PASSWORD = "PASSWORD";
    private static final String USERID = "USERID";

    private RecyclerView recyclerView;



    public void onBackPressed() {
        moveTaskToBack(true);
    }
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        /**Cancel notifications**/
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();

        /**For pull and refresh**/
        // Lookup the swipe container view
        swipeContainer = findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCmdList(userId);
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources( android.R.color.holo_red_light,
                                                android.R.color.holo_green_light,
                                                android.R.color.holo_orange_light,
                                                android.R.color.holo_blue_bright);





        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        token = task.getResult().getToken();
                        // Log and //Toast
                        String msg = "the token number is: " + token;
                        Log.d(TAG, msg);



                        sharedPreferences = getBaseContext().getSharedPreferences(USER_INFO, MODE_PRIVATE);

                        if (sharedPreferences.contains(EMAIL) && sharedPreferences.contains(PASSWORD)) {
                            userId = sharedPreferences.getString(USERID, "");
                            email = sharedPreferences.getString(EMAIL, "");
                            password = sharedPreferences.getString(PASSWORD, "");
                            String message="email: " + email + " password: " + password;
                            //Toast.makeText(MainActivity.this,message, //Toast.LENGTH_SHORT).show();

                        }
                        //Toast.makeText(this, "stored mail: " + storedEmail, //Toast.LENGTH_SHORT).show();
                        if (          !email.equals("")   &&    ! password.equals("") &&    ! userId.equals("")  ) {
                            getCmdList(userId);
                            //Toast.makeText(MainActivity.this, email +"/ userId"+userId, //Toast.LENGTH_SHORT).show();
                        }else {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.putExtra("TOKEN", token);
                            startActivityForResult(intent, 2);
                        }


                    }

                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (data != null) {
                //Bundle bundleData=data.getExtras();
                //String userId= (String) bundleData.get("USERID");

                //we retreive userId that have been stored thanks to loginActivity
                userId = sharedPreferences.getString(USERID, "");
                getCmdList(userId);

            } else {
                //Toast.makeText(this, "Problem", //Toast.LENGTH_SHORT).show();

            }
        } else {
            //Toast.makeText(this, "Problem 2", //Toast.LENGTH_SHORT).show();
        }


    }


    public  void getCmdList(String userId) {
        String type="commandList";
        BackgroundWorker backgroundWorker=new BackgroundWorker(MainActivity.this);
        String tempResult=null;
        try {
            tempResult=backgroundWorker.execute(type,userId).get();

            //Toast.makeText(MainActivity.this, tempResult, Toast.LENGTH_SHORT).show();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        final String result = tempResult;
        ArrayList<SingleRow> list = new ArrayList<>();

        try {

            JSONObject json = new JSONObject(result);
            JSONArray jArray = json.getJSONArray("orders");

            boolean boolTemp;
            for(int i=0; i<jArray.length(); i++){
                JSONObject json_line = jArray.getJSONObject(i);
                boolTemp= (i % 2 == 0);
                int imageCode=0;
                if (json_line.getString("orderstatus").equals("pending")){
                    imageCode=R.drawable.ic_pending_ribbon;
                } else if(json_line.getString("orderstatus").equals("canceled")) {
                    imageCode=R.drawable.ic_canceled_ribbon;
                }
                else if(json_line.getString("orderstatus").equals("delivered")) {
                    imageCode=R.drawable.ic_delivered_ribbon;
                }
                else if(json_line.getString("orderstatus").equals("prepared")) {
                    imageCode=R.drawable.ic_prepared_ribbon;
                }


                ArrayList<SingleRowDetailCmd> detailCmdsList=new ArrayList<>();
                detailCmdsList.add(new SingleRowDetailCmd("Libellé", "Quantité", "Prix"));
                detailCmdsList.add(new SingleRowDetailCmd("tomato", "5 kg", "5 DH"));
                detailCmdsList.add(new SingleRowDetailCmd("tomato", "5 kg", "5 DH"));
                detailCmdsList.add(new SingleRowDetailCmd("tomato", "5 kg", "5 DH"));
                detailCmdsList.add(new SingleRowDetailCmd("tomato", "5 kg", "5 DH"));
                detailCmdsList.add(new SingleRowDetailCmd("tomato", "5 kg", "5 DH"));
                detailCmdsList.add(new SingleRowDetailCmd("tomato", "5 kg", "5 DH"));

                ArrayList<String> l= getPriceCouple(json_line.getString("ttc"));

                list.add(new SingleRow(
                        json_line.getString("orid"),//order Id
                        full_name,
                        imageCode, //for status ribbon
                        l.get(0), //price big part
                        l.get(1),//price decimal part
                        boolTemp,//even and add elements
                        detailCmdsList, //command list details for each ingle element
                        address,
                        phone_number,
                        customer_ship_date,
                        creation_date
                ));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setItemPrefetchEnabled(false);
        recyclerView.setLayoutManager(linearLayoutManager);


        recyclerView.setAdapter(new RecyclerViewAdapter( list, this));
    }
    /**the aim of this function is to devide the price into two slices,
     * they are needed then to be formatted correctly,
     * one will be smaller than the other*/
    private ArrayList<String> getPriceCouple(String s) {
        ArrayList<String> l=new ArrayList<>();
        int commaPosition=s.indexOf(".");
        if (commaPosition>0) {
            l.add(s.substring(0, commaPosition + 1));
            l.add(s.substring(commaPosition + 1));
        } else {
            l.add(s);
            l.add("");
        }
        return l;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.disconnect) {
            //supprimer les identifiants de la base de données
            /*
            loginDataBaseAdapter = loginDataBaseAdapter.open();

            loginDataBaseAdapter.deleteEntry(storedEmail);
            loginDataBaseAdapter.close();*/


            sharedPreferences.edit().clear().commit();

            //passer à l activité de login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("TOKEN", token);
            startActivityForResult(intent, 2);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


class SnappingLinearLayoutManager extends LinearLayoutManager {

    public SnappingLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                       int position) {
        RecyclerView.SmoothScroller smoothScroller = new TopSnappedSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    private class TopSnappedSmoothScroller extends LinearSmoothScroller {
        public TopSnappedSmoothScroller(Context context) {
            super(context);

        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return SnappingLinearLayoutManager.this
                    .computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;
        }
    }
}
