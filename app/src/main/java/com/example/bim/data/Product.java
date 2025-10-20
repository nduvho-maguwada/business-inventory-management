package com.example.bim.data;

public class Product {
    private int id;
    private String name;
    private double price;
    private double costPrice;
    private int stock;
    private String category;

    // Default constructor
    public Product() {}

    // Full constructor
    public Product(int id, String name, double price, double costPrice, int stock, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.costPrice = costPrice;
        this.stock = stock;
        this.category = category;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getCostPrice() { return costPrice; }
    public int getStock() { return stock; }
    public String getCategory() { return category; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }
    public void setStock(int stock) { this.stock = stock; }
    public void setCategory(String category) { this.category = category; }
}
