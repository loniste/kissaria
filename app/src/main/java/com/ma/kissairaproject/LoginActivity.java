package com.ma.kissairaproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.ma.kissairaproject.models.LoginResponse;
import com.ma.kissairaproject.utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseAct {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_email)
    EditText email_ET;
    @BindView(R.id.input_password)
    EditText password_ET;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;
    @BindView(R.id.demo)
    View demo;
    //LoginDatabaseAdapter loginDataBaseAdapter;

    SharedPreferences sharedPreferences;
    String token = null;
    String userId = "";
    String email = "";
    String password = "";
    String userType = "seller_login";
    String result = null;
    String status = null;
    String responseCode = null;
    String firstName = "";
    String lastName = "";
    String profileImageLink = "";


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

        demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email_ET.getText().toString().equals("")) {
                    email_ET.setText("khalid.essalhi@gmail.com");
                    password_ET.setText("123456");
                } else if (email_ET.getText().toString().equals("khalid.essalhi@gmail.com")) {
                    email_ET.setText("admin@admin.com");
                    password_ET.setText("admin@admin.com");
                } else {
                    email_ET.setText("");
                    password_ET.setText("");
                }
            }
        });
        if (Constants.demo) {
            demo.setVisibility(View.VISIBLE);
        } else {
            demo.setVisibility(View.GONE);
        }


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

        FirebaseInstanceId.getInstance().getInstanceId().addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        token = ("not token");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            token = ("not token");
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        if (task.getResult() != null)
                            token = task.getResult().getToken();
                        else
                            token = ("not token");

                        // Log and //Toast


                    }
                });
    }

    public void login() {
        Log.d(TAG, "Login" + "");
        if (!validate()) {//make sure only that the input mail and password are written well
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        showLoading();
        RequestBody emailPart = RequestBody.create(MultipartBody.FORM, email_ET.getText().toString());
        RequestBody passwordPart = RequestBody.create(MultipartBody.FORM, password_ET.getText().toString());
        RequestBody tokenPart = RequestBody.create(MultipartBody.FORM, token == null ? "" : token);

        getAPIManager().loginAsSeller(
                emailPart, passwordPart, tokenPart
        ).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    if (response.body() == null) return;
                    _loginButton.setEnabled(true);
                    switch (response.body().code) {
                        case 1:
//                            Toast.makeText(LoginActivity.this, "success dès le premier coup, code réponse: "+ responseCode, Toast.LENGTH_SHORT).show();
                            userId = response.body().shid;
                            firstName = response.body().firstName;
                            lastName = response.body().lastName;
                            profileImageLink = response.body().image;
                            onLoginSuccess();
                            break;
                        case 2:
                            Toast.makeText(LoginActivity.this, "Another user is already connected with the same email !, code réponse: " + responseCode, Toast.LENGTH_SHORT).show();
                            onLoginFailed();
                            break;
                        case 0:
//                            Toast.makeText(LoginActivity.this, "error while attempting to connect as a seller, code réponse: "+ responseCode, Toast.LENGTH_SHORT).show();
                            userType = "customer_login";
                            getAPIManager().loginAsUser(emailPart, passwordPart, tokenPart).enqueue(new Callback<LoginResponse>() {
                                @Override
                                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                    hideLoading();
                                    if (!response.isSuccessful()) return;
                                    if (response.body() == null) return;
                                    switch (response.body().code) {
                                        case 1:
//                                            Toast.makeText(LoginActivity.this, "customer, code réponse: "+ responseCode, Toast.LENGTH_SHORT).show();
                                            userId = response.body().cuid;
                                            firstName = response.body().firstName;
                                            lastName = response.body().lastName;
                                            profileImageLink = response.body().image;
                                            onLoginSuccess();
                                            break;
                                        case 0:
                                            onLoginFailed();
                                            break;
                                        case 2:
//                                            Toast.makeText(LoginActivity.this, "Another user is connected with the same email !, code réponse: "+responseCode, Toast.LENGTH_SHORT).show();
                                            onLoginFailed();
                                            break;
                                    }
                                }

                                @Override
                                public void onFailure(Call<LoginResponse> call, Throwable throwable) {
                                    hideLoading();
                                    Log.d(TAG, "onFailure: e: " + throwable.getMessage().toString());

                                }
                            });

                    }

                } else if (response.code() == 401) {

                } else if (response.code() == 402) {

                } else {
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d(getClass().getSimpleName(), "onFailure: " + t.getMessage());
                hideLoading();
            }
        });


//        Intent intentToken= getIntent();
//        Bundle bundleToken = intentToken.getExtras();
//
//        if(bundleToken!=null ) {
//            token = (String) bundleToken.get("TOKEN");
//            seekAndGetUserType();

//        }
    }

    private void seekAndGetUserType() {

        BackgroundWorker backgroundWorker = new BackgroundWorker(LoginActivity.this);

        try {
            result = backgroundWorker.execute(userType, email, password, token).get();
            result = (result == null) ? "" : result;
            Log.d("jasonobject", result + "");
            jsonObject = new JSONObject(result);
            status = jsonObject.getString("status");
            responseCode = jsonObject.getString("code");
        } catch (ExecutionException | JSONException | InterruptedException e) {
            e.printStackTrace();
        }
        if (status == null) {
            status = "No internet";
        }
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        // onLoginFailed();

                        hideLoading();
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


        sharedPreferences = getBaseContext().getSharedPreferences(Constants.USER_INFO, MODE_PRIVATE);
        sharedPreferences
                .edit()
                .putString(Constants.USERID, userId)
                .putString(Constants.EMAIL, email_ET.getText().toString())
                .putString(Constants.PASSWORD, password_ET.getText().toString())
                .putString(Constants.TYPE, userType)
                .putString(Constants.FIRST_NAME, firstName)
                .putString(Constants.LAST_NAME, lastName)
                .putString(Constants.PROFILE_IMAGE_LINK, profileImageLink)
                .apply();

        Toast.makeText(this, "user userType is: " + userType, Toast.LENGTH_SHORT).show();


        Intent intentEMail = new Intent(LoginActivity.this, MainActivity.class);

        intentEMail.putExtra("EMAIL", email_ET.getText().toString());
//        setResult(2, intentEMail);
        startActivity(intentEMail);
    }

    public void onLoginFailed() {
        //Toast.makeText(LoginActivity.this, "Login failed", //Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);

        token = null;
        userId = "";
        email = "";
        password = "";
        firstName = "";
        lastName = "";
        profileImageLink = "";
        userType = "seller_login";
        result = null;
        status = null;
    }

    public boolean validate() {
        boolean valid = true;

        String username = email_ET.getText().toString();
        String password = password_ET.getText().toString();

        if (username.isEmpty()) {
            email_ET.setError("enter a valid username address");
            valid = false;
        } else {
            email_ET.setError(null);
        }

        if (password.isEmpty()) {//|| password.length() < 4 || password.length() > 10) {
            password_ET.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            password_ET.setError(null);
        }
        return valid;
    }
}
