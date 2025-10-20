package com.example.bim.data;

public class Sale {
    private int id;
    private int productId;
    private int quantity;
    private double total;
    private long saleDate;

    // Default constructor
    public Sale() {}

    // Full constructor
    public Sale(int id, int productId, int quantity, double total, long saleDate) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.total = total;
        this.saleDate = saleDate;
    }

    // Getters
    public int getId() { return id; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getTotal() { return total; }
    public long getSaleDate() { return saleDate; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setTotal(double total) { this.total = total; }
    public void setSaleDate(long saleDate) { this.saleDate = saleDate; }
}
