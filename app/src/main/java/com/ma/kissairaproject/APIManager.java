package com.ma.kissairaproject;


import com.google.gson.Gson;
import com.ma.kissairaproject.models.LoginResponse;
import com.ma.kissairaproject.models.SellerOrdersResponse;
import com.ma.kissairaproject.utilities.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIManager {

    public static String BASE_URL_TESTING = "http://192.168.43.23:5000/api/";
    public static String BASE_URL_SERVER = "http://rec.assouak.ma//";
    public static String ImageUrl_TESTING = "http://192.168.43.23:5000/api/attachments/";
    public static String ImageUrl_SERVER = "http://149.91.80.68:5000/api/attachments/";


    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constants.DEMO_ENV ? BASE_URL_TESTING : BASE_URL_SERVER)
            .addConverterFactory(GsonConverterFactory.create(new Gson()))
            .client(okHttpClient)
            .build();
//    Retrofit retrofitString = new Retrofit.Builder()
//            .baseUrl(Constants.DEMO_ENV ? BASE_URL_TESTING : BASE_URL_SERVER)
//            .addConverterFactory(ScalarsConverterFactory.create())
//            .client(okHttpClient)
//            .build();


//    @Multipart
//    @POST("users")
//    Call<User> updateProfile(
//            @Part("username") RequestBody title,
//            @Part("countryId") RequestBody description,
//            @Part("cityId") RequestBody start_at,
//            @Part("districtId") RequestBody end_at,
//            @Part("schoolId") RequestBody brand_id,
//            @Part("professionId") RequestBody sub_category_id,
//            @Part("firebaseAuthToken") RequestBody firebaseAuthToken,
//            @Part("phone") RequestBody phone,
//            @Part("firebaseMessagingTokenPart") RequestBody firebaseMessagingTokenPart,
//
//            @Part MultipartBody.Part data
//    );
//
//    @PUT
//    Call<ResponseBody> updateFBToken(@Url String url);
//
//    @Multipart
//    @POST
//    Call<ResponseBody> uploadNewPost(
//            @Url String url,
//            @Part("userId") RequestBody userId,
//            @Part("message") RequestBody message,
//            @Part("answerToPost") RequestBody answerToPost,
//            @Part MultipartBody.Part picture
//    );


//    @Headers({
//            "Content-Type: application/json",
//            "Accept: application/json"
//    })
//    @GET
//    Call<Config> getConfig(@Url String url);
//
//
//    @Headers({
//            "Content-Type: application/json",
//            "Accept: application/json"
//    })
//    @POST("signup")
//    Call<SignUpResponse> signUp(@Body SignUpRequest body);
//
//
//    @Headers("Accept: application/json")
//    @POST("login")
//    Call<LoginResponse> logIn(@Body LoginRequest body);


//    @Headers({
//            "Content-Type: application/json",
//            "Accept: */*"
//    })
    @Multipart
    @POST("login/apiCheckSellerlogin")
    Call<LoginResponse> loginAsSeller(
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("token") RequestBody token
    );


//    @Headers({
//            "Content-Type: application/json",
//            "Accept: application/json"
//    })
    @Multipart
    @POST("login/apiCheckUserlogin")
    Call<LoginResponse> loginAsUser(
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("token") RequestBody token
    );


    @GET
    Call<SellerOrdersResponse> getSellerOrders(@Url String url );
}
