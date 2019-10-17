package com.ma.kissairaproject;

class SingleRowDetailCmd {
    private String article;
    private String quantity;
    private String price;

    public SingleRowDetailCmd(String article, String qty, String price) {
        this.article=article;
        this.quantity=qty;
        this.price=price;
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

}
