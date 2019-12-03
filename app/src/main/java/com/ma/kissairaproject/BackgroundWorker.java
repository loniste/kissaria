package com.ma.kissairaproject;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

//import static android.support.v4.content.ContextCompat.startActivity;

public class BackgroundWorker extends AsyncTask<String, Void, String> {
    public static String resultat;

//    Context context;
    AlertDialog alertDialog;
    public BackgroundWorker(Context ctx){
//        context=ctx;
    }
    public BackgroundWorker(){
    }
    @Override
    protected String doInBackground(String... params) {
        String type=params[0];

        String register_url="http://192.168.1.183//projet/register.php";
        String SellerLoginUrl="https://dolimoni.com/kissaria/rec/login/apiCheckSellerlogin";
        String customerLoginUrl="https://dolimoni.com/kissaria/rec/login/apiCheckUserlogin";
        String sellerCommandList_url="https://dolimoni.com/kissaria/rec/seller/api/orders/apiGetAllOrders/";//the url is completed by then with the userId
        String customerCommandList_url="https://dolimoni.com/kissaria/rec/user/api/orders/apiGetAllOrders/";//the url is completed by then with the userId
        String postStatusSellerUrl="https://dolimoni.com/kissaria/rec/seller/api/orders/apiChangeStatus";
        String postStatusCustomerUrl="https://dolimoni.com/kissaria/rec/user/api/orders/apiChangeStatusForShop";
        String sellerLogOutUrl="https://dolimoni.com/kissaria/rec/login/apiSellerLogout";
        String customerLogOutUrl="https://dolimoni.com/kissaria/rec/login/apiUserLogout";
        String isOrderReceivedUrl="https://dolimoni.com/kissaria/rec/user/api/orders/isOrderReceived";
        String imageUrl="https://image.shutterstock.com/image-vector/shield-letter-s-logosafesecureprotection-logomodern-260nw-633031571.jpg";
        switch (type) {
            case "seller_login":
                try {
                    String email = params[1];
                    String password = params[2];
                    String token = params[3];
                    URL url = new URL(SellerLoginUrl);
                    Log.d("seller_login_bg_url",url+"");

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                    String post_data =
                            URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&"
                                    + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&"
                                    + URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");

                    Log.d("seller_login_bg",post_data);

                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result = "";
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }

                    Log.d("res_seller_login_bg",String.valueOf(result));


                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case "customer_login":
                try {
                    String email = params[1];
                    String password = params[2];
                    String token = params[3];
                    URL url = new URL(customerLoginUrl);
                    Log.d("customer_login_bg_url",url+"");

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                    String post_data =
                            URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&"
                                    + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&"
                                    + URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");
                    Log.d("customer_login_bg",post_data);

                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    StringBuilder result = new StringBuilder();
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("res_customer_login_bg",String.valueOf(result));

                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "customer_logout":
                try {
                    String cuid = params[1];
                    URL url = new URL(customerLogOutUrl);
                    Log.d("customer_logoutn_bg_url",url+"");

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                    String post_data =
                            URLEncoder.encode("cuid", "UTF-8") + "=" + URLEncoder.encode(cuid, "UTF-8");

                    Log.d("customer_logout_bg",post_data);

                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result = "";
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }

                    Log.d("res_customer_logout_bg",String.valueOf(result));


                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case "seller_logout":
                try {
                    String soid = params[1];
                    URL url = new URL(sellerLogOutUrl);
                    Log.d("seller_logout_bg_url",url+"");

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                    String post_data =
                            URLEncoder.encode("soid", "UTF-8") + "=" + URLEncoder.encode(soid, "UTF-8");

                    Log.d("seller_logout_bg",post_data);

                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result = "";
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }

                    Log.d("res_seller_logout_bg",String.valueOf(result));


                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case "sellerCommandList":
                try {
                    String userId = params[1];
                    URL url = new URL(sellerCommandList_url + userId);
                    Log.d("sellerCmdList_bg_url",url+"");

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));

                    String post_data= URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");

                    Log.d("sellerCommandList_bg",post_data);
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    StringBuilder result = new StringBuilder();
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("res_selCommandList_bg", String.valueOf(result));

                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    //result= post_data;
                    return result.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case "custommerCommandList":
                try {
                    String userId = params[1];
                    URL url = new URL( customerCommandList_url+ userId);
                    Log.d("custommerCmdLst_bg_url",url+"");

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));

                    String post_data
                            = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");

                    Log.d("custommerCommandList_bg",post_data);



                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    StringBuilder result = new StringBuilder();
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("res_custCommandList_bg",String.valueOf(result));
//                    appendLog(String.valueOf(result));



                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    //result= post_data;
                    return result.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case "register":
                try {
                    String id_Order = params[1];
                    String id_DeliveryMan = params[2];
                    URL url = new URL(register_url);
                    Log.d("register_bg_url",url+"");

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                    String post_data = URLEncoder.encode("id_Order", "UTF-8") + "=" + URLEncoder.encode(id_Order, "UTF-8") + "&"
                            + URLEncoder.encode("id_DeliveryMan", "UTF-8") + "=" + URLEncoder.encode(id_DeliveryMan, "UTF-8");
                    ;
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    StringBuilder result = new StringBuilder();
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("res_register_bg",String.valueOf(result));


                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    Log.d("result", result.toString());
                    return result.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "post_status_seller":
                try {
                    String shid = params[1];
                    String orid = params[2];
                    String status = params[3];
                    URL url = new URL(postStatusSellerUrl);
                    Log.d("poststatusSeller_bg_url",url+"");

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                    Log.d("poststatusSel_ready_bg", status);
                    String post_data
                            = URLEncoder.encode("shid", "UTF-8") + "=" + URLEncoder.encode(shid, "UTF-8") + "&"
                            + URLEncoder.encode("orid", "UTF-8") + "=" + URLEncoder.encode(orid, "UTF-8") +"&"
                            + URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(status, "UTF-8");

                    Log.d("poststatusSeller_bg_url",post_data);

                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    StringBuilder result = new StringBuilder();
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("res_poststaSel_bg_url",String.valueOf(result));


                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    //result= post_data;
                    return result.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "post_status_customer":
                try {
                    String shid = params[1];
                    String orid = params[2];
                    String status = params[3];
                    URL url = new URL(postStatusCustomerUrl);
                    Log.d("poststatusCus_bg_url",url+"");

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                    Log.d("poststatusCus_ready_bg", status);
                    String post_data
                            = URLEncoder.encode("shid", "UTF-8") + "=" + URLEncoder.encode(shid, "UTF-8") + "&"
                            + URLEncoder.encode("orid", "UTF-8") + "=" + URLEncoder.encode(orid, "UTF-8") +"&"
                            + URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(status, "UTF-8");

                    Log.d("poststatusCus_bg_post",post_data);

                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    StringBuilder result = new StringBuilder();
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("res_poststaCus_bg_url",String.valueOf(result));


                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    //result= post_data;
                    return result.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "notif_yes_no":
                try {
                    String shid = params[1];
                    String orid = params[2];
                    String answer = params[3];
                    URL url = new URL(isOrderReceivedUrl);
                    Log.d("notif_yes_no_bg_url",url+"");

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                    String post_data
                            = URLEncoder.encode("shid", "UTF-8") + "=" + URLEncoder.encode(shid, "UTF-8") + "&"
                            + URLEncoder.encode("orid", "UTF-8") + "=" + URLEncoder.encode(orid, "UTF-8") +"&"
                            + URLEncoder.encode("isOrderReceived", "UTF-8") + "=" + URLEncoder.encode(answer, "UTF-8");

                    Log.d("notif_yes_no_bg",post_data);

                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    StringBuilder result = new StringBuilder();
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("res_notif_yes_no_bg",String.valueOf(result));


                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    //result= post_data;
                    return result.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        return null;
    }


    @Override
    protected void onPreExecute() {
//        alertDialog=new AlertDialog.Builder(context).create();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


    public void appendLog(String text)
    {
        File logFile = new File("C:\\Z\\log.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

