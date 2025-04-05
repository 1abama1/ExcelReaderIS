package com.example.demo1;

public class ProductSale {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private int finalPrice;
    private String date;

    public ProductSale(int id, String name, double price, int quantity, int finalPrice, String date) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.finalPrice = finalPrice;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(int finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    @Override
    public String toString() {
        return  id + " - " + name + " - " + quantity + " шт. - $" + price + " - " + finalPrice + " - " + date;
    }
}
