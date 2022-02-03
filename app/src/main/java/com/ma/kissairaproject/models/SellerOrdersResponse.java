package com.ma.kissairaproject.models;


import android.graphics.drawable.Drawable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ma.kissairaproject.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class SellerOrdersResponse {
    /**
     * the aim of this function is to devide the price into two slices,
     * they are needed then to be formatted correctly,
     * one will be smaller than the other
     */
    public static ArrayList<String> getNumberCouple(String s) {
        ArrayList<String> l = new ArrayList<>();
        int commaPosition = s.indexOf(".");
        if (commaPosition > 0) {
            l.add(s.substring(0, commaPosition + 1));
            l.add(s.substring(commaPosition + 1));
        } else {
            l.add(s);
            l.add("");
        }
        return l;
    }
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("orders")
    @Expose
    public List<Order> orders = null;

    public static class Order {

        private static final String PENDING = "pending";
        private static final String CANCELED = "canceled";
        private static final String READY = "ready";
        private static final String DELIVERED = "delivered";
        private static final String RECEIVED = "received";
        public String full_name;
        public int statusIcon;
        public int statusText;
        public String price;
        public String afterCommaPrice;
        public String phone_number;
        public String creation_time;
        public String creation_date;
        public String ship_time;
        public String ship_date;
        public Drawable drawableProfile;
        @SerializedName("orid")
        @Expose
        public String orid;
        @SerializedName("cuid")
        @Expose
        public String cuid;
        @SerializedName("dvid")
        @Expose
        public String dvid;
        @SerializedName("ttc")
        @Expose
        public String ttc;
        @SerializedName("reference")
        @Expose
        public String reference;
        @SerializedName("orderstatus")
        @Expose
        public String orderstatus;
        @SerializedName("paymentstatus")
        @Expose
        public String paymentstatus;
        @SerializedName("customer_ship_date")
        @Expose
        public String customerShipDate;
        @SerializedName("created_at")
        @Expose
        public String createdAt;
        @SerializedName("updated_at")
        @Expose
        public Object updatedAt;
        @SerializedName("type_livraison")
        @Expose
        public String typeLivraison;
        @SerializedName("mode_paiement")
        @Expose
        public String modePaiement;
        @SerializedName("shid")
        @Expose
        public String shid;
        @SerializedName("subTotal")
        @Expose
        public String subTotal;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("last_name")
        @Expose
        public String lastName;
        @SerializedName("first_name")
        @Expose
        public String firstName;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName("ciid")
        @Expose
        public String ciid;
        @SerializedName("code_postal")
        @Expose
        public String codePostal;
        @SerializedName("default_id_delivery")
        @Expose
        public String defaultIdDelivery;
        @SerializedName("orderstatusLabel")
        @Expose
        public String orderstatusLabel;
        @SerializedName("products")
        @Expose
        public List<Product> products = null;
        @SerializedName("audio")
        @Expose
        public List<Audio> audio = null;
        public Order() {
        }



        public void fillDisplayAttributes() {
            ArrayList<String> l;
            l = getNumberCouple(subTotal);
            ArrayList<String> dateCouple = getDateCouple(createdAt);
            ArrayList<String> dateCouple2 = getDateCouple(customerShipDate);


            full_name = firstName + " " + lastName;
            setStatusUI();

            price = l.get(0);
            afterCommaPrice = l.get(1);

            phone_number = phone;

            creation_time = dateCouple != null && dateCouple.size() == 2 ? dateCouple.get(1) : "";//time
            creation_date = dateCouple != null && dateCouple.size() == 2 ? dateCouple.get(0) : "";

            ship_time = dateCouple2 != null && dateCouple2.size() == 2 ? dateCouple2.get(1) : "";
            ship_date = dateCouple2 != null && dateCouple2.size() == 2 ? dateCouple2.get(0) : "";

            drawableProfile = null;
            for (int i = 0; i < (products==null?0:products.size()); i++) {
                products.get(i).fillProductDisplayAttributes();
            }


        }

        private void setStatusUI() {
            statusIcon = getImageCode(orderstatus);
        }

        private ArrayList<String> getDateCouple(String s) {
            try {
                ArrayList<String> l = new ArrayList<>();
                //2019-10-16 16:20:09

                /**time processing*/
                int spacePosition = s.indexOf(" ");
                String fullTime = s.substring(spacePosition + 1);//hh:mm:ss
                int secondePointsPosition = fullTime.indexOf(":");
                String tempo = fullTime.substring(secondePointsPosition + 1);//"mm:ss"
                secondePointsPosition += tempo.indexOf(":") + 1;
                fullTime = fullTime.substring(0, secondePointsPosition);//hh:mm
                /*date processing*/
                String date = s.substring(0, spacePosition);
                StringBuilder newDate = new StringBuilder();
                String year = date.substring(2, 4);
                String month = date.substring(5, 7);
                String day = date.substring(8, 10);
                newDate.append(day);
                newDate.append("-");
                newDate.append(month);
                newDate.append("-");
                newDate.append(year);

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);

                String currentDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(Calendar.getInstance().getTime());

                String yesterdaysDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(cal.getTime());

//        Log.d("date&time", currentDate);
//        Log.d("date&time", yesterdaysDate);
                if (String.valueOf(newDate).equals(currentDate)) {
                    newDate = new StringBuilder("Auj.");
                } else if (String.valueOf(newDate).equals(yesterdaysDate)) {
                    newDate = new StringBuilder("Hier");
                }
                if (spacePosition > 0) {
                    l.add(String.valueOf(newDate));
                    l.add(fullTime);
                } else {
                    l.add(s);
                    l.add("");
                }
                return l;
            } catch (Exception e) {
                return null;
            }

        }


        private int getImageCode(String orderstatus) {
            int imageCode = 0;
            if (orderstatus == null) {
                return imageCode;
            }
            switch (orderstatus) {
                case PENDING:
                    imageCode = R.drawable.ic_pending_icon;
                    break;
                case CANCELED:
                    imageCode = R.drawable.ic_canceled_icon;
                    break;
                case READY:
                    imageCode = R.drawable.ic_ready_icon;
                    break;
                case DELIVERED:
                    imageCode = R.drawable.ic_delivered_icon;
                    break;
                case RECEIVED:
                    //this is right though, in fact, we are asked to transform statusIV to
                    // delivered in the seller side when the actual statusIV is received
                    imageCode = R.drawable.ic_delivered_icon;
                    break;
            }
            return imageCode;
        }
    }

    public static class Product {

        @SerializedName("opid")
        @Expose
        public String opid;
        @SerializedName("prid")
        @Expose
        public String prid;
        @SerializedName("orid")
        @Expose
        public String orid;
        @SerializedName("shid")
        @Expose
        public String shid;
        @SerializedName("unit_price")
        @Expose
        public String unitPrice;
        @SerializedName("quantity")
        @Expose
        public String quantity;
        @SerializedName("created_at")
        @Expose
        public String createdAt;
        @SerializedName("updated_at")
        @Expose
        public Object updatedAt;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("main_image")
        @Expose
        public String mainImage;
        @SerializedName("subTotal")
        @Expose
        public String subTotal;


        public String article;
        public String quantity1;
        public String quantity2;
        public String price1;
        public String price2;

        public void fillProductDisplayAttributes(){
            ArrayList<String> unitPriceList = getNumberCouple(subTotal);
            ArrayList<String> unitQtyList = getNumberCouple(quantity);
            article=name;
            quantity1=unitQtyList.get(0);
            quantity2=unitQtyList.get(1);
            price1=unitPriceList.get(0);
            price2=unitPriceList.get(1);



        }



    }

    public static class Audio {

        @SerializedName("audio_id")
        @Expose
        public String audioId;
        @SerializedName("orid")
        @Expose
        public String orid;
        @SerializedName("audio")
        @Expose
        public String audio;
        @SerializedName("from")
        @Expose
        public String from;
        @SerializedName("shid")
        @Expose
        public String shid;
        @SerializedName("created_at")
        @Expose
        public String createdAt;
        @SerializedName("name")
        @Expose
        public String name;

    }

}





