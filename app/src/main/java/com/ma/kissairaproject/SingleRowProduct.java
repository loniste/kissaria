package com.ma.kissairaproject;

class SingleRowProduct {
    private String article;
    private String quantity;
    private String quantity2;
    private String price;
    private String price2;



    public SingleRowProduct(String article, String qty, String qty2, String price, String price2) {
        this.article=article;
        this.quantity=qty;
        this.quantity2=qty2;
        this.price=price;
        this.price2=price2;
    }
    public SingleRowProduct(){
        //**this is just for creating an instance for the very first row, the head row*/
    }
    public String getArticle() {
        return this.article;
    }
    public String getQuantity() {
        return quantity;
    }
    public String getPrice() {
        return price;
    }
    public String getQuantity2() {
        return quantity2;
    }
    public String getPrice2() {
        return price2;
    }
}
