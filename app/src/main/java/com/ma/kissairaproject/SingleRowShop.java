package com.ma.kissairaproject;

import java.util.ArrayList;

class SingleRowShop {

    private String shopName;
    private String shId;
    private String shopStatus;
    private String phoneNumber;

    private ArrayList<SingleRowProduct> productsList;



    SingleRowShop(String shopName, String shId, String shopStatus, String phoneNumber, ArrayList<SingleRowProduct> productList){

        this.shopName=shopName;
        this.shId=shId;
        this.shopStatus=shopStatus;
        this.phoneNumber=phoneNumber;

        this.productsList =new ArrayList<>(productList);
    }
    SingleRowShop(){
        this.shopName="Shope name";
        this.productsList.add(new SingleRowProduct());
        this.productsList.add(new SingleRowProduct());
        this.productsList.add(new SingleRowProduct());
    }
    public String getShopName() {
        return shopName;
    }
    public ArrayList<SingleRowProduct> getProductsList() {
        return productsList;
    }
    public String getShopStatus() {
        return shopStatus;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getshId() {
        return shId;
    }
}
