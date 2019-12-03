package com.ma.kissairaproject;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

class SellerSingleRow {
    private String ship_time;
    private String ship_date;



    private String cmd;
    private String full_name;
    private int statusCode;
    private String price;
    private String afterCommaPrice;
    private boolean recent;
    private ArrayList<SingleRowProduct> detailCmdsList;
    private String address;
    private String phone_number;
    private String creation_time;
    private String creation_date;
    private Drawable drawableProfile;


    public SellerSingleRow(){
        this.cmd="123";
        this.full_name="name surname";
        this.statusCode=R.drawable.ic_pending_icon;
        this.price="99.";
        this.afterCommaPrice="99";
        this.recent=true;
        this.detailCmdsList=new ArrayList<>() ;
        detailCmdsList.add(new SingleRowProduct("tomates","10.","10","80.","80"));
        this.address="Adrresse";
        this.phone_number="0606060606";
        this.creation_time="50:50";
        this.creation_date="2019-20-20";
    }

    public SellerSingleRow(String cmd,
                           String full_name,
                           int statusCode,
                           String price,
                           String afterCommaPrice,
                           boolean recent,
                           ArrayList<SingleRowProduct> detailCmdsList,
                           String address,
                           String phone_number,
                           String creation_time,
                           String creation_date,
                           String ship_time,
                           String ship_date,
                           Drawable drawableProfile

    ) {

        this.cmd=cmd;
        this.full_name=full_name;
        this.statusCode=statusCode;
        this.price=price;
        this.afterCommaPrice=afterCommaPrice;
        this.recent=recent;
        this.detailCmdsList=new ArrayList<>(detailCmdsList) ;
        this.address=address;
        this.phone_number=phone_number;
        this.creation_time=creation_time;
        this.creation_date=creation_date;

        this.ship_time=ship_time;
        this.ship_date=ship_date;

        this.drawableProfile =drawableProfile;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isRecent() {
        return recent;
    }

    public void setRecent(boolean recent) {
        this.recent = recent;
    }

    public ArrayList<SingleRowProduct> getDetailCmdsList() {
        return detailCmdsList;
    }

    public void setDetailCmdsList(ArrayList<SingleRowProduct> detailCmdsList) {
        this.detailCmdsList = detailCmdsList;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setAfterCommaPrice(String afterCommaPrice) {
        this.afterCommaPrice = afterCommaPrice;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }



    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public void setShip_time(String ship_time) {
        this.ship_time = ship_time;
    }

    public void setShip_date(String ship_date) {
        this.ship_date = ship_date;
    }

    public String getCmd(){
        return this.cmd;
    }
    public int  getStatusCode(){
        return this.statusCode;
    }
    public String  getPrice(){
        return this.price;
    }
    public boolean getRecent(){
        return this.recent;
    }
    public String getAfterCommaPrice() {
        return afterCommaPrice;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone_number() {
        return phone_number;
    }


    public String getCreation_date() {
        return creation_date;
    }
    public String getCreation_time() {
        return creation_time;
    }

    public String getShip_time() {
        return ship_time;
    }

    public String getShip_date() {
        return ship_date;
    }

    public Drawable getDrawableProfile() {
        return drawableProfile;
    }

    public void setDrawableProfile(Drawable drawableProfile) {
        this.drawableProfile = drawableProfile;
    }
}
