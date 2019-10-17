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
    String userId="";
    String email="";
    String password="";
    private static final String USER_INFO = "USER_INFO";
    private static final String EMAIL = "EMAIL";
    private static final String PASSWORD = "PASSWORD";
    private static final String USERID = "USERID";

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
        Log.d(TAG, "Login");
        String token=null;
        if (!validate()) {
            onLoginFailed();
            return;
        }
        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        email = email_ET.getText().toString();
        password = password_ET.getText().toString();

        // TODO: Implement your own authentication logic here.
        String type="login";
        BackgroundWorker backgroundWorker=new BackgroundWorker(LoginActivity.this);
        String tempResult=null;
        Intent intentToken= getIntent();
        Bundle bundleToken = intentToken.getExtras();

        if(bundleToken!=null )
        {
            token =(String) bundleToken.get("TOKEN");
            try {
                Toast.makeText(LoginActivity.this, "token: " + token, Toast.LENGTH_SHORT).show();

                tempResult=backgroundWorker.execute(type,email,password,token).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



        final String result = tempResult;
        String statusTmp=null;
        //Toast.makeText(LoginActivity.this, result, //Toast.LENGTH_SHORT).show();

        try {
            JSONObject jsonObject = new JSONObject(result);
            statusTmp=jsonObject.getString("status");
            userId=jsonObject.getString("soid");
            //Toast.makeText(LoginActivity.this, statusTmp, //Toast.LENGTH_SHORT).show();

            //for (int i = 0; i < jsonObject.length(); i++) {
            //   JSONObject jsonObject = jsonArray.getJSONObject(i);
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String statut = statusTmp;
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if (statut.equals("success")) {

                            onLoginSuccess();
                        } else if (statut.equals("error")){
                            onLoginFailed();
                        }
                        // onLoginFailed();

                        progressDialog.dismiss();
                        //Toast.maketext(LoginActivity.this, result, //Toast.LENGTH_SHORT).show();



                    }
                }, 500);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                .apply();

        //Toast.makeText(this, "Sauvegardé, relancez l'application pour voir le résultat", //Toast.LENGTH_SHORT).show();



        Intent intentEMail=new Intent();
        intentEMail.putExtra("EMAIL", email_ET.getText().toString());
        setResult(2, intentEMail);
        finish();
    }

    public void onLoginFailed() {
        //Toast.makeText(getBaseContext(), "Login failed", //Toast.LENGTH_LONG).show();
        //Toast.makeText(LoginActivity.this, "Login failed", //Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
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
