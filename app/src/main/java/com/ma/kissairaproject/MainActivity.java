package com.ma.kissairaproject;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
//import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

//import static com.ma.kissairaproject.LoginDatabaseAdapter.email;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG2";


    String token="tokenWitoutInitialization";

    ArrayList<SellerSingleRow> sellerList = new ArrayList<>();
    ArrayList<CustomerSingleRow> customer_list = new ArrayList<>();
    private RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    SellerRecyclerViewAdapter sellerRecyclerViewAdapter;
    CustomerRecyclerViewAdapter customerRecyclerViewAdapter;

    SharedPreferences sharedPreferences;
    String userId="";
    String email="";
    String password="";
    String userType="";
    private static final String USER_INFO = "USER_INFO";
    private static final String EMAIL = "EMAIL";
    private static final String PASSWORD = "PASSWORD";
    private static final String USERID = "USERID";
    private static final String TYPE = "TYPE";




    public void onBackPressed() {
        moveTaskToBack(true);
    }
    private SwipeRefreshLayout swipeContainer;


    /*for drawerLayout*/
    private DrawerLayout dl;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView nv;

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
                sellerList.clear();
                customer_list.clear();
                if (userType.equals("customer_login")){
                    getCustomerCmdList(userId);
                } else if (userType.equals("seller_login")) {
                    getSellerCmdList(userId);
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        swipeContainer.setRefreshing(false);

                    }
                }, 500);
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
                        String msg = "tokenCode: " + token;
                        Log.d(TAG, msg);



                        sharedPreferences = getBaseContext().getSharedPreferences(USER_INFO, MODE_PRIVATE);

                        if (sharedPreferences.contains(EMAIL) && sharedPreferences.contains(PASSWORD)) {
                            userId = sharedPreferences.getString(USERID, "");
                            email = sharedPreferences.getString(EMAIL, "");
                            password = sharedPreferences.getString(PASSWORD, "");
                            userType = sharedPreferences.getString(TYPE, "");
                            Log.d("userType", userType);
                            String message="email: " + email + " password: " + password;
                            //Toast.makeText(MainActivity.this,message, //Toast.LENGTH_SHORT).show();

                        }
                        //Toast.makeText(this, "stored mail: " + storedEmail, //Toast.LENGTH_SHORT).show();
                        if (          !email.equals("")   &&    ! password.equals("") &&    ! userId.equals("")  ) {
                            if (userType.equals("customer_login")){
                                getCustomerCmdList(userId);
                            } else if (userType.equals("seller_login")) {
                                getSellerCmdList(userId);
                            }                            //Toast.makeText(MainActivity.this, email +"/ userId"+userId, //Toast.LENGTH_SHORT).show();
                        }else {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.putExtra("TOKEN", token);
                            startActivityForResult(intent, 2);
                        }
                    }
                });

        dl = (DrawerLayout)findViewById(R.id.activity_main);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);

        dl.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.account:
                        Toast.makeText(MainActivity.this, "My Account",Toast.LENGTH_SHORT).show();break;
                    case R.id.settings:
                        Toast.makeText(MainActivity.this, "Settings",Toast.LENGTH_SHORT).show();break;
                    case R.id.mycart:
                        Toast.makeText(MainActivity.this, "My Cart",Toast.LENGTH_SHORT).show();break;
                    default:
                        return true;
                }
                return true;

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
                userType = sharedPreferences.getString(TYPE, "");
                Log.d("user_id", userId);
                if (userType.equals("customer_login")){
                    getCustomerCmdList(userId);
                } else if (userType.equals("seller_login")) {
                    getSellerCmdList(userId);
                }
            } else {
                //Toast.makeText(this, "Problem", //Toast.LENGTH_SHORT).show();

            }
        } else {
            //Toast.makeText(this, "Problem 2", //Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mMessageReceiver);
    }


    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("OnReceivedMessage: ", message);
//            sellerList.add(new SellerSingleRow());
////            sellerRecyclerViewAdapter.notifyItemInserted(sellerList.size() - 1);
            sellerList.clear();
            customer_list.clear();
            if (userType.equals("customer_login")){
                getCustomerCmdList(userId);
            } else if (userType.equals("seller_login")) {
                getSellerCmdList(userId);
            }            Toast.makeText(getApplicationContext(), "Liste actualisée suite à une nouvelle commande", Toast.LENGTH_SHORT).show();

            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };



    public  void getSellerCmdList(String userId) {
        String backGroundType="sellerCommandList";
        BackgroundWorker backgroundWorker=new BackgroundWorker(MainActivity.this);
        String tempResult=null;
        try {
            tempResult=backgroundWorker.execute(backGroundType,userId).get();
            tempResult=(tempResult==null)?"":tempResult;

            Log.d("result", tempResult+"");


            //Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        final String result = tempResult;
        try {

            JSONObject json = new JSONObject(result);
            JSONArray jArray = json.getJSONArray("orders");

            boolean boolTemp;
            for(int i=0; i<jArray.length(); i++){
                JSONObject json_line = jArray.getJSONObject(i);
                boolTemp= (i % 2 == 0);
                int imageCode=0;
                switch (json_line.getString("orderstatus")) {
                    case "pending":
                        imageCode = R.drawable.ic_pending_ribbon;
                        break;
                    case "canceled":
                        imageCode = R.drawable.ic_canceled_ribbon;
                        break;
                    case "ready":
                        imageCode = R.drawable.ic_ready_ribbon;
                        break;
                    case "delivered":
                        imageCode = R.drawable.ic_delivered_ribbon;
                        break;
                    case "received":
                        imageCode = R.drawable.ic_received_ribbon;
                        break;
                }
                ArrayList<SingleRowProduct> detailCmdsList=new ArrayList<>();
                JSONArray products_jArray = json_line.getJSONArray("products");
                for (int j = 0; j <products_jArray.length() ; j++) {
                    JSONObject json_product_row = products_jArray.getJSONObject(j);
                    ArrayList<String> unitPriceList= getNumberCouple(json_product_row.getString("subTotal"));
                    ArrayList<String> unitQtyList= getNumberCouple(json_product_row.getString("quantity"));
                    detailCmdsList.add(new SingleRowProduct(
                            json_product_row.getString("name"),
                            unitQtyList.get(0) ,
                            unitQtyList.get(1) ,
                            unitPriceList.get(0),
                            unitPriceList.get(1)
                            )
                    );
                }
                ArrayList<String> l=new ArrayList<>();
                l= getNumberCouple(json_line.getString("subTotal"));
                ArrayList<String> dateCouple= getDateCouple(json_line.getString("created_at"));
                sellerList.add(new SellerSingleRow(
                        json_line.getString("orid"),//order Id
                        json_line.getString("first_name")+" "+json_line.getString("last_name"),
                        imageCode, //for status ribbon
                        l.get(0), //price big part
                        l.get(1),//price decimal part
                        boolTemp,//even and add elements
                        detailCmdsList, //command sellerList details for each ingle element
                        json_line.getString("address"),
                        json_line.getString("phone"),
                        json_line.getString("customer_ship_date"),
                        dateCouple.get(1),//time
                        dateCouple.get(0)//date
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        linearLayoutManager.setItemPrefetchEnabled(false);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        sellerRecyclerViewAdapter =new SellerRecyclerViewAdapter(sellerList,this, userId);
        recyclerView.setAdapter(sellerRecyclerViewAdapter);
    }
    public  void getCustomerCmdList(String userId) {
        String backGroundType="custommerCommandList";
        BackgroundWorker backgroundWorker=new BackgroundWorker(MainActivity.this);
        String tempResult=null;
        try {
            tempResult=backgroundWorker.execute(backGroundType,userId).get();
            tempResult=(tempResult==null)?"":tempResult;

            Log.d("result", tempResult+"");


            //Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        final String result = tempResult;
        try {

            JSONObject json = new JSONObject(result);
            JSONArray jArray = json.getJSONArray("ordersBySellers");

            boolean boolTemp;
            Log.d("number_of_cmds", jArray.length()+"");
            for(int i=0; i<jArray.length(); i++){
                JSONObject json_line = jArray.getJSONObject(i);
                boolTemp= (i % 2 == 0);
                int imageCode=0;
                switch (json_line.getString("orderstatus")) {
                    case "pending":
                        imageCode = R.drawable.ic_pending_ribbon;
                        break;
                    case "canceled":
                        imageCode = R.drawable.ic_canceled_ribbon;
                        break;
                    case "ready":
                        imageCode = R.drawable.ic_ready_ribbon;
                        break;
                    case "prepared":
                        imageCode = R.drawable.ic_delivered_ribbon;
                        break;
                    case "received":
                        imageCode = R.drawable.ic_received_ribbon;
                        break;
                }

                ArrayList<SingleRowShop> shopList=new ArrayList<>();
                JSONArray shops_jArray = json_line.getJSONArray("shops");

                JSONObject shopElement;
                String shopName;

                // for each shop
                for (int k = 0; k <shops_jArray.length() ; k++) {
                    shopElement = shops_jArray.getJSONObject(k);
                    shopName=shopElement.getString("shopName");

                    JSONArray products_jArray = shopElement.getJSONArray("products");
                    ArrayList<SingleRowProduct> productsList=new ArrayList<>();
                    JSONObject productElement;

                    // for each product
                    for (int l = 0; l <products_jArray.length() ; l++) {
                        productElement=products_jArray.getJSONObject(l);

                        ArrayList<String> unitPriceList= getNumberCouple(productElement.getString("ttc"));
                        ArrayList<String> unitQtyList= getNumberCouple(productElement.getString("quantity"));
                        productsList.add(new SingleRowProduct(
                                        productElement.getString("name"),
                                        unitQtyList.get(0) ,
                                        unitQtyList.get(1) ,
                                        unitPriceList.get(0),
                                        unitPriceList.get(1)
                                )
                        );

                    }


                    shopList.add(new SingleRowShop(shopElement.getString("shopName"),shopElement.getString("shid"),shopElement.getString("osOrderstatus"),"0123456789", productsList));
                }

                ArrayList<SingleRowProduct> detailCmdsList=new ArrayList<>();


                ArrayList<String> l=new ArrayList<>();
                if (userType.equals("customer_login")){
                    l= getNumberCouple(json_line.getString("ttc"));
                } else if (userType.equals("seller_login")){
                    l= getNumberCouple(json_line.getString("subTotal"));
                }
                ArrayList<String> dateCouple= getDateCouple(json_line.getString("created_at"));
                customer_list.add(new CustomerSingleRow(
                        json_line.getString("orid"),//order Id
                        json_line.getString("first_name")+" "+json_line.getString("last_name"),
                        imageCode, //for status ribbon
                        l.get(0), //price big part
                        l.get(1),//price decimal part
                        boolTemp,//even and add elements
                        shopList, //command sellerList details for each ingle element
                        json_line.getString("address"),
                        json_line.getString("phone"),
                        json_line.getString("customer_ship_date"),
                        dateCouple.get(1),//time
                        dateCouple.get(0)//date
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        linearLayoutManager.setItemPrefetchEnabled(false);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        customerRecyclerViewAdapter =new CustomerRecyclerViewAdapter(customer_list,this, userId);
        recyclerView.setAdapter(customerRecyclerViewAdapter);
    }
    private ArrayList<String> getDateCouple(String s) {
        ArrayList<String> l=new ArrayList<>();
        //2019-10-16 16:20:09

        /**time processing*/
        int spacePosition=s.indexOf(" ");
        String fullTime=s.substring(spacePosition + 1);//hh:mm:ss
        int secondePointsPosition=fullTime.indexOf(":");
        String tempo =fullTime.substring(secondePointsPosition+1);//"mm:ss"
        secondePointsPosition+=tempo.indexOf(":")+1;
        fullTime=fullTime.substring(0,secondePointsPosition);//hh:mm
        /*date processing*/
        String date=s.substring(0, spacePosition);
        StringBuilder newDate= new StringBuilder();
        String year=date.substring(2,4);
        String month=date.substring(5,7);
        String day=date.substring(8,10);
        newDate.append(day);
        newDate.append("-");
        newDate.append(month);
        newDate.append("-");
        newDate.append(year);

        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,-1);

        String currentDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(Calendar.getInstance().getTime());

        String yesterdaysDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(cal.getTime());

//        Log.d("date&time", currentDate);
//        Log.d("date&time", yesterdaysDate);
        if (String.valueOf(newDate).equals(currentDate)){
            newDate= new StringBuilder("Auj.");
        }else if (String.valueOf(newDate).equals(yesterdaysDate)){
            newDate= new StringBuilder("Hier");
        }
        if (spacePosition>0) {
            l.add(String.valueOf(newDate));
            l.add(fullTime);
        } else {
            l.add(s);
            l.add("");
        }
        return l;
    }

    /**the aim of this function is to devide the price into two slices,
     * they are needed then to be formatted correctly,
     * one will be smaller than the other*/
    private ArrayList<String> getNumberCouple(String s) {
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
            sellerList.clear();

            //passer à l activité de login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("TOKEN", token);
            startActivityForResult(intent, 2);

            return true;
        }
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

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
