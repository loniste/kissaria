package com.ma.kissairaproject;

import java.util.ArrayList;

class CustomerSingleRow {
    String cmd;
    String full_name;
    int statusCode;
    String price;
    String afterCommaPrice;
    boolean recent;
    ArrayList<SingleRowShop> detailShopList;
    String address;
    String phone_number;
    String creation_time;
    String creation_date;



    String ship_time;
    String ship_date;
    String image;


    public CustomerSingleRow(){
        this.cmd="123";
        this.full_name="name surname";
        this.statusCode=R.drawable.ic_pending_icon;
        this.price="99.";
        this.afterCommaPrice="99";
        this.recent=true;
        this.detailShopList=new ArrayList<>() ;
        detailShopList.add(new SingleRowShop());
        this.address="Adrresse";
        this.phone_number="0606060606";
        this.creation_time="50:50";
        this.creation_date="2019-20-20";
    }

    public CustomerSingleRow(String cmd,
                             String full_name,
                             int statusCode,
                             String price,
                             String afterCommaPrice,
                             boolean recent,
                             ArrayList<SingleRowShop> detailShopList,
                             String address,
                             String phone_number,
                             String creation_time,
                             String creation_date,
                             String ship_time,
                             String ship_date

    ) {

        this.cmd=cmd;
        this.full_name=full_name;
        this.statusCode=statusCode;
        this.price=price;
        this.afterCommaPrice=afterCommaPrice;
        this.recent=recent;
        this.detailShopList=new ArrayList<>(detailShopList) ;
        this.address=address;
        this.phone_number=phone_number;
        this.creation_time=creation_time;
        this.creation_date=creation_date;
        this.ship_date=ship_date;
        this.ship_time=ship_time;
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

    public ArrayList<SingleRowShop> getDetailShopList() {
        return detailShopList;
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
}
