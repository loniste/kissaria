package com.ma.kissairaproject;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

    Context context;
    AlertDialog alertDialog;
    public BackgroundWorker(Context ctx){
        context=ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        String type=params[0];
/*
        String login_url="http://192.168.137.251//projet/index.php";
        String register_url="http://192.168.137.251//projet/register.php";
        String mission_url="http://192.168.137.251//projet/mission.php";
        String ordersList_url="http!://192.168.137.251//projet/orders_list.php";
*//*
        String login_url="http://192.168.101.1//projet/index.php";
        String register_url="http://192.168.101.1//projet/register.php";
        String mission_url="http://192.168.101.1//projet/mission.php";
        String ordersList_url="http://192.168.101.1//projet/orders_list.php";
/*
        String login_url="http://192.168.42.152//projet/index.php";
        String register_url="http://192.168.42.152//projet/register.php";
        String mission_url="http://192.168.42.152//projet/mission.php";
        String ordersList_url="http://192.168.42.152//projet/orders_list.php";


        String login_url="http://192.168.43.118//projet/index.php";
        String register_url="http://192.168.43.118//projet/register.php";
        String mission_url="http://192.168.43.118//projet/mission.php";
        String ordersList_url="http://192.168.43.118//projet/orders_list.php";


        String login_url="http://192.168.1.183//projet/index.php";
        String register_url="http://192.168.1.183//projet/register.php";
        String mission_url="http://192.168.1.183//projet/mission.php";
        String ordersList_url="http://192.168.1.183//projet/orders_list.php";
*/
        //String login_url="http://projet-31-07-19.000webhostapp.com/login.php";
        String register_url="http://192.168.1.183//projet/register.php";
        String login_url="https://dolimoni.com/kissaria/rec/login/apiCheckSellerlogin";
        String commandList_url2="https://dolimoni.com/kissaria/rec/seller/api/orders/getall";//?id=1&getproducts=true
        String commandList_url="https://dolimoni.com/kissaria/rec/seller/api/orders/apiGetAllOrders/1";//?id=1&getproducts=true

        if (type.equals("login")){
            try {
                String email=params[1];
                String password=params[2];
                String token=params[3];
                URL url =new URL(login_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream,"utf-8"));
                String post_data   =
                         URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"
                        +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8")+"&"
                        +URLEncoder.encode("token","UTF-8")+"="+URLEncoder.encode(token,"UTF-8")
                        ;
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream=httpURLConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line="";
                while((line=bufferedReader.readLine())!=null){
                    result+=line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("commandList")){
            try {
                String userId=params[1];
                URL url =new URL(commandList_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream,"utf-8"));

                String post_data
                        = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(userId,"UTF-8")+"&"
                        + URLEncoder.encode("getProducts","UTF-8")+"="+URLEncoder.encode("false","UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream=httpURLConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line="";
                while((line=bufferedReader.readLine())!=null){
                    result+=line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                //result= post_data;
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("register")) {
            try {
                String id_Order = params[1];
                String id_DeliveryMan = params[2];
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                String post_data = URLEncoder.encode("id_Order", "UTF-8") + "=" + URLEncoder.encode(id_Order, "UTF-8") + "&"
                        + URLEncoder.encode("id_DeliveryMan", "UTF-8") + "=" + URLEncoder.encode(id_DeliveryMan, "UTF-8");                        ;
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
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        alertDialog=new AlertDialog.Builder(context).create();
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
}
