package com.ma.kissairaproject;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_email) EditText email_ET;
    @BindView(R.id.input_password) EditText password_ET;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;
    //LoginDatabaseAdapter loginDataBaseAdapter;

    SharedPreferences sharedPreferences;
    String token=null;
    String userId="";
    String email="";
    String password="";
    String userType ="seller_login";
    String result =null;
    String status = null;
    String responseCode = null;
    String firstName = "";
    String lastName = "";

    private static final String USER_INFO = "USER_INFO";
    private static final String EMAIL = "EMAIL";
    private static final String PASSWORD = "PASSWORD";
    private static final String USERID = "USERID";
    private static final String TYPE = "TYPE" ;
    private static final String FIRST_NAME = "FIRST_NAME" ;
    private static final String LAST_NAME = "LAST_NAME" ;

    JSONObject jsonObject;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        // create the instance of internal Databse
        //loginDataBaseAdapter=new LoginDatabaseAdapter(getApplicationContext());




        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }
    public void login() {
        Log.d(TAG, "Login"+"");
        if (!validate()) {//make sure only that the input mail and password are written well
            onLoginFailed();
            return;
        }
        _loginButton.setEnabled(false);
        email = email_ET.getText().toString();
        password = password_ET.getText().toString();

        // TODO: Implement your own authentication logic here.

        Intent intentToken= getIntent();
        Bundle bundleToken = intentToken.getExtras();

        if(bundleToken!=null ) {
            token = (String) bundleToken.get("TOKEN");
            seekAndGetUserType();

        }
    }

    private void seekAndGetUserType() {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        BackgroundWorker backgroundWorker=new BackgroundWorker(LoginActivity.this);

        try {
            result = backgroundWorker.execute(userType, email, password, token).get();
            result=(result==null)?"":result;
            Log.d("jasonobject", result+"");
            jsonObject = new JSONObject(result);
            status = jsonObject.getString("statusIV");
            responseCode = jsonObject.getString("code");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (status == null) {
            status = "No internet";
        }
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        switch (responseCode) {
                            case "1":
                                Toast.makeText(LoginActivity.this, "success dès le premier coup, code réponse: "+ responseCode, Toast.LENGTH_SHORT).show();
                                try {
                                    userId = jsonObject.getString("shid");
                                    firstName = jsonObject.getString("first_name");
                                    lastName = jsonObject.getString("last_name");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                onLoginSuccess();
                                break;
                            case "2":
                                Toast.makeText(LoginActivity.this, "Another user is already connected with the same email !, code réponse: "+responseCode, Toast.LENGTH_SHORT).show();
                                onLoginFailed();
                                break;
                            case "0":
                                Toast.makeText(LoginActivity.this, "error while attempting to connect as a seller, code réponse: "+ responseCode, Toast.LENGTH_SHORT).show();
                                userType ="customer_login";
                                try {
                                    result = new BackgroundWorker(LoginActivity.this).execute(userType, email, password, token).get();
                                    result=(result==null)?"":result;
                                    jsonObject = new JSONObject(result);
                                    status = jsonObject.getString("statusIV");
                                    responseCode=jsonObject.getString("code");
                                    Log.d("resultat 123",result+"" );
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (status == null) {
                                    status = "No internet";
                                }
                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                // On complete call either onLoginSuccess or onLoginFailed
                                                switch (responseCode) {
                                                    case "1":
                                                        Toast.makeText(LoginActivity.this, "customer, code réponse: "+ responseCode, Toast.LENGTH_SHORT).show();
                                                        try {
                                                            userId = jsonObject.getString("cuid");
                                                            firstName = jsonObject.getString("first_name");
                                                            lastName = jsonObject.getString("last_name");
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        onLoginSuccess();
                                                        break;
                                                    case "0":
                                                        onLoginFailed();
                                                        break;
                                                    case "2":
                                                        Toast.makeText(LoginActivity.this, "Another user is connected with the same email !, code réponse: "+responseCode, Toast.LENGTH_SHORT).show();
                                                        onLoginFailed();
                                                        break;
                                                }
                                                progressDialog.dismiss();
                                            }
                                        }, 500);
                                break;
                        }
                        // onLoginFailed();

                        progressDialog.dismiss();
                    }
                }, 500);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);


        /*
        String receieveOk;
        loginDataBaseAdapter=loginDataBaseAdapter.open();
        receieveOk=loginDataBaseAdapter.insertEntry(email_ET.getText().toString(), password_ET.getText().toString());

        loginDataBaseAdapter.close();*/


        sharedPreferences = getBaseContext().getSharedPreferences(USER_INFO, MODE_PRIVATE);
        sharedPreferences
                .edit()
                .putString(USERID, userId)
                .putString(EMAIL, email)
                .putString(PASSWORD, password)
                .putString(TYPE, userType)
                .putString(FIRST_NAME, firstName)
                .putString(LAST_NAME, lastName)
                .apply();

        Toast.makeText(this, "user userType is: " + userType, Toast.LENGTH_SHORT).show();



        Intent intentEMail=new Intent();
        intentEMail.putExtra("EMAIL", email_ET.getText().toString());
        setResult(2, intentEMail);
        finish();
    }
    public void onLoginFailed() {
        //Toast.makeText(LoginActivity.this, "Login failed", //Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);

        token=null;
        userId="";
        email="";
        password="";
        firstName="";
        lastName="";
        userType ="seller_login";
        result =null;
        status = null;
    }
    public boolean validate() {
        boolean valid = true;

        String username = email_ET.getText().toString();
        String password = password_ET.getText().toString();

        if (username.isEmpty() ) {
            email_ET.setError("enter a valid username address");
            valid = false;
        } else {
            email_ET.setError(null);
        }

        if (password.isEmpty() ) {//|| password.length() < 4 || password.length() > 10) {
            password_ET.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            password_ET.setError(null);
        }
        return valid;
    }
}
