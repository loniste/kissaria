package com.ma.kissairaproject.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("code")
    @Expose
    public Integer code;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("soid")
    @Expose
    public String soid;
    @SerializedName("shid")
    @Expose
    public String shid;
     @SerializedName("cuid")
    @Expose
    public String cuid;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("image")
    @Expose
    public String image;
}
