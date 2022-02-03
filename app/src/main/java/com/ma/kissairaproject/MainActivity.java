package com.ma.kissairaproject;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.ma.kissairaproject.models.SellerOrdersResponse;
import com.ma.kissairaproject.utilities.Constants;
import com.ma.kissairaproject.utilities.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends BaseAct {


    public static final String CUSTOMER_LOGIN = "customer_login";
    public static final String SELLER_LOGIN = "seller_login";
    private static final String USER_INFO = "USER_INFO";
    private static final String EMAIL = "EMAIL";
    private static final String PASSWORD = "PASSWORD";
    private static final String USERID = "USERID";
    private static final String TYPE = "TYPE";
    private static final String FIRST_NAME = "FIRST_NAME";
    private static final String LAST_NAME = "LAST_NAME";
    private static final String PROFILE_IMAGE_LINK = "PROFILE_IMAGE_LINK";
    private static final String TAG = "MainActivity";
    private static final String IS_IN_FORGROUND = "IS_IN_FORGROUND";
    private static final String TOKEN = "TOKEN";
    private static final String SELLER_COMMAND_LIST = "sellerCommandList";
    private static final String CUSTOMER_COMMAND_LIST = "custommerCommandList";
    private static final String PENDING = "pending";
    private static final String CANCELED = "canceled";
    private static final String READY = "ready";
    private static final String DELIVERED = "delivered";
    private static final String RECEIVED = "received";
    private static final String ORDERS_BY_SELLERS = "ordersBySellers";
    private static final String ORDER_STATUS = "orderstatus";
    private static final String SHOPS = "shops";
    private static final String SHOP_NAME = "shopName";
    private static final String PRODUCTS = "products";
    private static final String UNIT_PRICE = "unit_price";
    private static final String QUANTITY = "quantity";
    private static final String SHOP_ID = "shid";
    private static final String SHOP_STATUS = "osOrderstatus";
    private static final String SELLER_FIRST_NAME = "first_name";
    private static final String SELLER_LAST_NAME = "last_name";
    private static final String SELLER_ADDRESS = "address";
    private static final String SELLER_PHONE = "phone";
    private static final String CUSTOMER_ORDERS_LIST = "orders";
    private static final String PRODUCT_NAME = "name";
    private static final String ORDER_ID = "orid";
    private static final String CUSTOMER_FIRST_NAME = "first_name";
    private static final String CUSTOMER_LAST_NAME = "last_name";
    private static final String CUSTOMER_ADDRESS = "address";
    private static final String CUSTOMER_PHONE = "phone";
    ArrayList<SellerSingleRow> sellerList = new ArrayList<>();
    ArrayList<CustomerSingleRow> customer_list = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    SellerRecyclerViewAdapter sellerRecyclerViewAdapter;
    CustomerRecyclerViewAdapter customerRecyclerViewAdapter;
    SharedPreferences sharedPreferences;
    SharedPreferences activityStateSharedPreferences;
    String userId = "";
    String email = "";
    String password = "";
    String userType = "";
    String firstName = "";
    String lastName = "";
    String profileImageLink = "";
    String token = "tokenWitoutInitialization";
    private RecyclerView recyclerView;
    private View dots;
    /*for drawerLayout*/
    private DrawerLayout dl;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView nv;


    private SwipeRefreshLayout swipeContainer;
    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            if (userType.equals(CUSTOMER_LOGIN)) {
                customer_list.clear();
                getCustomerCmdList(userId);
            } else if (userType.equals(SELLER_LOGIN)) {
                sellerList.clear();
                loadSellerOrders(userId);
            }
            Toast.makeText(getApplicationContext(), "Liste actualisée suite à une nouvelle commande", Toast.LENGTH_SHORT).show();

            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };

    public void onBackPressed() {
        if (sellerRecyclerViewAdapter.areAllCollapsed()) {
            moveTaskToBack(true);
        } else {
            sellerRecyclerViewAdapter.collapseItem();
        }
    }

    void processData() {


        sharedPreferences = getBaseContext().getSharedPreferences(USER_INFO, MODE_PRIVATE);

        if (sharedPreferences.contains(EMAIL) && sharedPreferences.contains(PASSWORD)) {
            userId = sharedPreferences.getString(USERID, "");
            email = sharedPreferences.getString(EMAIL, "");
            password = sharedPreferences.getString(PASSWORD, "");
            userType = sharedPreferences.getString(TYPE, "");
            firstName = sharedPreferences.getString(FIRST_NAME, "");
            lastName = sharedPreferences.getString(LAST_NAME, "");
            profileImageLink = sharedPreferences.getString(PROFILE_IMAGE_LINK, "");

            TextView userName = nv.findViewById(R.id.user_name);
            userName.setText(String.format("%s %s", firstName, lastName));


            ImageView userProfile = nv.findViewById(R.id.profileImage);
            RequestOptions options = new RequestOptions();
            options.centerCrop();
            String profileURL = APIManager.BASE_URL_SERVER + Preferences.getValue(this, Constants.PROFILE_IMAGE_LINK, "");
            Log.d(TAG, "processData: profileURL: " + profileURL);
            Glide.with(this)
                    .load(profileURL)
                    .apply(options)
                    .error(R.drawable.user_image)
                    .into(userProfile);
            BackgroundWorkerProfile backgroundWorkerProfile = new BackgroundWorkerProfile();
            Drawable drawableProfile = null;
            try {
                drawableProfile = backgroundWorkerProfile.execute(profileImageLink).get();
                userProfile.setImageDrawable(drawableProfile);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "onComplete: kjskdjfsdf email: " + email);
        Log.d(TAG, "onComplete: kjskdjfsdf password: " + password);
        Log.d(TAG, "onComplete: kjskdjfsdf userId: " + userId);
        if (!email.equals("") && !password.equals("") && !userId.equals("")) {
            if (userType.equals(CUSTOMER_LOGIN)) {
                getCustomerCmdList(userId);
            } else if (userType.equals(SELLER_LOGIN)) {
                loadSellerOrders(userId);
            }                            //Toast.makeText(MainActivity.this, email +"/ userId"+userId, //Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "onComplete: kjskdjfsdf called ");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra(TOKEN, token);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: kjskdjfsdf called");
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        dots = findViewById(R.id.dots);
        dots.setOnClickListener(view -> {
            showPopup(dots);
        });
        /**Cancel notifications**/
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancelAll();
        }

        /**For pull and refresh**/
        // Lookup the swipe container view
        swipeContainer = findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                CustomThread customThread=new CustomThread();
//                customThread.start();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeContainer.setRefreshing(false);
                    }
                }, 3000);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright);


        //navigation drawer processing
        dl = (DrawerLayout) findViewById(R.id.activity_main);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);

        dl.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        findViewById(R.id.toggle).setOnClickListener(view -> {
            dl.openDrawer(GravityCompat.START);
        });

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        nv = (NavigationView) findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.account:
                        Toast.makeText(MainActivity.this, "My Account", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.settings:
                        Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.propos:
                        Toast.makeText(MainActivity.this, "My Cart", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
        processData();
        initCallbacks();

    }

    private void initCallbacks() {
        findViewById(R.id.dashboard).setOnClickListener(view -> {
            Toast.makeText(this, "Tableau de bord", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.settings).setOnClickListener(view -> {
            Toast.makeText(this, "Paramètres", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.profile).setOnClickListener(view -> {
            Toast.makeText(this, "Mon compte", Toast.LENGTH_SHORT).show();
        });
    }

    void loadData() {
        //Bundle bundleData=data.getExtras();
        //String userId= (String) bundleData.get("USERID");

        //we retreive userId that have been stored thanks to loginActivity
        userId = sharedPreferences.getString(USERID, "");
        userType = sharedPreferences.getString(TYPE, "");
        firstName = sharedPreferences.getString(FIRST_NAME, "");
        lastName = sharedPreferences.getString(LAST_NAME, "");
        profileImageLink = sharedPreferences.getString(PROFILE_IMAGE_LINK, "");
        Log.d("first_name", firstName);

        TextView userName = nv.getHeaderView(0).findViewById(R.id.user_name);
        userName.setText(String.format("%s %s", firstName, lastName));


        ImageView userProfile = nv.getHeaderView(0).findViewById(R.id.user_profile);
        BackgroundWorkerProfile backgroundWorkerProfile = new BackgroundWorkerProfile();
        Drawable drawableProfile = null;
        try {
            drawableProfile = backgroundWorkerProfile.execute(profileImageLink).get();
            userProfile.setImageDrawable(drawableProfile);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (userType.equals(CUSTOMER_LOGIN)) {
            getCustomerCmdList(userId);
        } else if (userType.equals(SELLER_LOGIN)) {
            loadSellerOrders(userId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (data != null) {

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));

        activityStateSharedPreferences = getBaseContext().getSharedPreferences(USER_INFO, MODE_PRIVATE);
        activityStateSharedPreferences
                .edit()
                .putBoolean(IS_IN_FORGROUND, true)
                .apply();
    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mMessageReceiver);
        activityStateSharedPreferences = getBaseContext().getSharedPreferences(USER_INFO, MODE_PRIVATE);
        activityStateSharedPreferences
                .edit()
                .putBoolean(IS_IN_FORGROUND, false)
                .apply();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (userType.equals(CUSTOMER_LOGIN)) {
            customer_list.clear();
            getCustomerCmdList(userId);
        } else if (userType.equals(SELLER_LOGIN)) {
            sellerList.clear();
            loadSellerOrders(userId);
        }

    }

    void loadSellerOrders(String userId) {
        showLoading();
        getAPIManager().getSellerOrders("seller/api/orders/apiGetAllOrders/"+userId).enqueue(new Callback<SellerOrdersResponse>() {
            @Override
            public void onResponse(Call<SellerOrdersResponse> call, Response<SellerOrdersResponse> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {

                    setData(response.body());
                }
            }

            @Override
            public void onFailure(Call<SellerOrdersResponse> call, Throwable throwable) {
                hideLoading();
                throwable.printStackTrace();
            }
        });
    }

    private void setData(SellerOrdersResponse body) {
        for (int i = 0; i < (body.orders == null ? 0 : body.orders.size()); i++) {
            body.orders.get(i).fillDisplayAttributes();
        }
        linearLayoutManager = new CustomLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 20);
        sellerRecyclerViewAdapter = new SellerRecyclerViewAdapter(body.orders, this, userId);
        recyclerView.setAdapter(sellerRecyclerViewAdapter);
    }


    public void getCustomerCmdList(String userId) {
        BackgroundWorker backgroundWorker = new BackgroundWorker(MainActivity.this);
        String tempResult = null;
        try {
            tempResult = backgroundWorker.execute(CUSTOMER_COMMAND_LIST, userId).get();
            tempResult = (tempResult == null) ? "" : tempResult;
            Log.d("result", tempResult + "");

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        final String result = tempResult;
        try {

            JSONObject json = new JSONObject(result);
            JSONArray jArray = json.getJSONArray(ORDERS_BY_SELLERS);

            boolean boolTemp;
            Log.d("number_of_cmds", jArray.length() + "");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_line = jArray.getJSONObject(i);
                boolTemp = (i % 2 == 0);
                int imageCode = 0;
                switch (json_line.getString(ORDER_STATUS)) {
                    case PENDING:
                        imageCode = R.drawable.ic_pending_icon;
                        break;
                    case CANCELED:
                        imageCode = R.drawable.ic_canceled_icon;
                        break;
                    case READY:
                        imageCode = R.drawable.ic_ready_icon;
                        break;
                    case DELIVERED:
                        imageCode = R.drawable.ic_delivered_icon;
                        break;
                    case RECEIVED:
                        imageCode = R.drawable.ic_received_icon;
                        break;
                }
                ArrayList<SingleRowShop> shopList = new ArrayList<>();
                JSONArray shops_jArray = json_line.getJSONArray(SHOPS);

                JSONObject shopElement;
                String shopName;

                // for each shop
                for (int k = 0; k < shops_jArray.length(); k++) {
                    shopElement = shops_jArray.getJSONObject(k);

                    JSONArray products_jArray = shopElement.getJSONArray(PRODUCTS);
                    ArrayList<SingleRowProduct> productsList = new ArrayList<>();
                    JSONObject productElement;

                    // for each product
                    for (int l = 0; l < products_jArray.length(); l++) {
                        productElement = products_jArray.getJSONObject(l);

                        ArrayList<String> unitPriceList = getNumberCouple(productElement.getString(UNIT_PRICE));
                        ArrayList<String> unitQtyList = getNumberCouple(productElement.getString(QUANTITY));
                        productsList.add(new SingleRowProduct(
                                        productElement.getString("name"),
                                        unitQtyList.get(0),
                                        unitQtyList.get(1),
                                        unitPriceList.get(0),
                                        unitPriceList.get(1)
                                )
                        );

                    }


                    shopList.add(new SingleRowShop(shopElement.getString(SHOP_NAME), shopElement.getString(SHOP_ID), shopElement.getString(SHOP_STATUS), "0123456789", productsList));
                }

                ArrayList<SingleRowProduct> detailCmdsList = new ArrayList<>();


                ArrayList<String> l = new ArrayList<>();
                if (userType.equals(CUSTOMER_LOGIN)) {
                    l = getNumberCouple(json_line.getString("ttc"));
                } else if (userType.equals(SELLER_LOGIN)) {
                    l = getNumberCouple(json_line.getString("subTotal"));
                }
                ArrayList<String> dateCouple = getDateCouple(json_line.getString("created_at"));
                ArrayList<String> dateCouple2 = getDateCouple(json_line.getString("customer_ship_date"));
                customer_list.add(new CustomerSingleRow(
                        json_line.getString("orid"),//order Id
                        json_line.getString(SELLER_FIRST_NAME) + " " + json_line.getString(SELLER_LAST_NAME),
                        imageCode, //for statusIV ribbon
                        l.get(0), //price big part
                        l.get(1),//price decimal part
                        boolTemp,//even and add elements
                        shopList, //command sellerList details for each ingle element
                        json_line.getString(SELLER_ADDRESS),
                        json_line.getString(SELLER_PHONE),
                        dateCouple != null && dateCouple.size() == 2 ? dateCouple.get(1) : "",//time
                        dateCouple != null && dateCouple.size() == 2 ? dateCouple.get(0) : "",//date
                        dateCouple2 != null && dateCouple2.size() == 2 ? dateCouple2.get(1) : "",//time2
                        dateCouple2 != null && dateCouple2.size() == 2 ? dateCouple2.get(0) : ""//date2
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        linearLayoutManager.setItemPrefetchEnabled(false);
        linearLayoutManager = new CustomLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        customerRecyclerViewAdapter = new CustomerRecyclerViewAdapter(customer_list, this, userId);
        recyclerView.setAdapter(customerRecyclerViewAdapter);
    }


    void logout() {
        //supprimer les identifiants de la base de données
            /*
            loginDataBaseAdapter = loginDataBaseAdapter.open();

            loginDataBaseAdapter.deleteEntry(storedEmail);
            loginDataBaseAdapter.close();*/
        String result = "";
        String postType = (userType.equals(CUSTOMER_LOGIN)) ? "customer_logout" : "seller_logout";
        BackgroundWorker backgroundWorker = new BackgroundWorker(MainActivity.this);
        try {
            result = backgroundWorker.execute(postType, userId).get();
            Log.d("deconnexion_result", result + "");
            if (result != null) {
                JSONObject json = new JSONObject(result);
                if (json.getString("status").equals("success")) {
                    sharedPreferences.edit().clear().apply();
                    sellerList.clear();
                    customer_list.clear();

                    //passer à l activité de login
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("TOKEN", token);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(MainActivity.this, "Impossible de se déconnecter depuis le serveur, merci de vérifier votre connexion internet !", Toast.LENGTH_SHORT).show();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public static ArrayList<String> getNumberCouple(String s) {
        ArrayList<String> l = new ArrayList<>();
        int commaPosition = s.indexOf(".");
        if (commaPosition > 0) {
            l.add(s.substring(0, commaPosition + 1));
            l.add(s.substring(commaPosition + 1));
        } else {
            l.add(s);
            l.add("");
        }
        return l;
    }
    private ArrayList<String> getDateCouple(String s) {
        try {
            ArrayList<String> l = new ArrayList<>();
            //2019-10-16 16:20:09

            /**time processing*/
            int spacePosition = s.indexOf(" ");
            String fullTime = s.substring(spacePosition + 1);//hh:mm:ss
            int secondePointsPosition = fullTime.indexOf(":");
            String tempo = fullTime.substring(secondePointsPosition + 1);//"mm:ss"
            secondePointsPosition += tempo.indexOf(":") + 1;
            fullTime = fullTime.substring(0, secondePointsPosition);//hh:mm
            /*date processing*/
            String date = s.substring(0, spacePosition);
            StringBuilder newDate = new StringBuilder();
            String year = date.substring(2, 4);
            String month = date.substring(5, 7);
            String day = date.substring(8, 10);
            newDate.append(day);
            newDate.append("-");
            newDate.append(month);
            newDate.append("-");
            newDate.append(year);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);

            String currentDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(Calendar.getInstance().getTime());

            String yesterdaysDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(cal.getTime());

//        Log.d("date&time", currentDate);
//        Log.d("date&time", yesterdaysDate);
            if (String.valueOf(newDate).equals(currentDate)) {
                newDate = new StringBuilder("Auj.");
            } else if (String.valueOf(newDate).equals(yesterdaysDate)) {
                newDate = new StringBuilder("Hier");
            }
            if (spacePosition > 0) {
                l.add(String.valueOf(newDate));
                l.add(fullTime);
            } else {
                l.add(s);
                l.add("");
            }
            return l;
        } catch (Exception e) {
            return null;
        }

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

        popup.show();
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.logout:
                    logout();
                    return true;
            }
            return false;
        });
    }

//    class CustomThread extends Thread {
//        public CustomThread() {
//            if (userType.equals(CUSTOMER_LOGIN)) {
//                customer_list.clear();
//                getCustomerCmdList(userId);
//            } else if (userType.equals(SELLER_LOGIN)) {
//                sellerList.clear();
//                getSellerCmdList(userId);
//            }
//        }
//
//        @Override
//        public void run() {
//
//        }
//    }
}
