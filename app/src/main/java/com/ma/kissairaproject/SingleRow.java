package com.ma.kissairaproject;

import java.util.ArrayList;

class SingleRow {
    String cmd;
    String full_name;
    int statusCode;
    String price;
    String afterCommaPrice;
    boolean recent;
    ArrayList<SingleRowDetailCmd> detailCmdsList;
    String address;
    String phone_number;
    String customer_ship_date;
    String creation_date;





    public SingleRow(String cmd, String full_name, int statusCode, String price, String afterCommaPrice, boolean recent, ArrayList<SingleRowDetailCmd> detailCmdsList) {






        this.cmd=cmd;
        this.statusCode=statusCode;
        this.price=price;
        this.afterCommaPrice=afterCommaPrice;
        this.recent=recent;
        this.detailCmdsList=new ArrayList<>(detailCmdsList) ;
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

    public ArrayList<SingleRowDetailCmd> getDetailCmdsList() {
        return detailCmdsList;
    }

    public void setDetailCmdsList(ArrayList<SingleRowDetailCmd> detailCmdsList) {
        this.detailCmdsList = detailCmdsList;
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
}
