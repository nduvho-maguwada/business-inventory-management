package com.example.bim.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 3;

    // Products table
    private static final String TABLE_PRODUCTS = "Products";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_COST_PRICE = "costPrice";
    private static final String COLUMN_STOCK = "stock";
    private static final String COLUMN_CATEGORY = "category";

    // Sales table
    private static final String TABLE_SALES = "Sales";
    private static final String COLUMN_SALE_ID = "id";
    private static final String COLUMN_PRODUCT_ID = "product_id";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_TOTAL = "total";
    private static final String COLUMN_SALE_DATE = "saleDate";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProducts = "CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCTS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_PRICE + " REAL,"
                + COLUMN_COST_PRICE + " REAL,"
                + COLUMN_STOCK + " INTEGER,"
                + COLUMN_CATEGORY + " TEXT"
                + ")";
        db.execSQL(createProducts);

        String createSales = "CREATE TABLE IF NOT EXISTS " + TABLE_SALES + " ("
                + COLUMN_SALE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PRODUCT_ID + " INTEGER,"
                + COLUMN_QUANTITY + " INTEGER,"
                + COLUMN_TOTAL + " REAL,"
                + COLUMN_SALE_DATE + " INTEGER"
                + ")";
        db.execSQL(createSales);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_PRODUCTS + " ADD COLUMN " + COLUMN_COST_PRICE + " REAL DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_SALES + " ADD COLUMN " + COLUMN_SALE_DATE + " INTEGER DEFAULT 0");
        }
    }

    // -------------------- PRODUCT METHODS --------------------
    public long addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, product.getName());
        values.put(COLUMN_PRICE, product.getPrice());
        values.put(COLUMN_COST_PRICE, product.getCostPrice());
        values.put(COLUMN_STOCK, product.getStock());
        values.put(COLUMN_CATEGORY, product.getCategory());
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    public int updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, product.getName());
        values.put(COLUMN_PRICE, product.getPrice());
        values.put(COLUMN_COST_PRICE, product.getCostPrice());
        values.put(COLUMN_STOCK, product.getStock());
        values.put(COLUMN_CATEGORY, product.getCategory());
        return db.update(TABLE_PRODUCTS, values, COLUMN_ID + "=?",
                new String[]{String.valueOf(product.getId())});
    }

    public void deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);
        if (cursor.moveToFirst()) {
            do {
                Product p = new Product(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_COST_PRICE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOCK)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                );
                list.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Product p = new Product(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_COST_PRICE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOCK)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
            );
            cursor.close();
            return p;
        }
        return null;
    }

    // -------------------- SALES METHODS --------------------
    public long addSale(Sale sale) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_ID, sale.getProductId());
        values.put(COLUMN_QUANTITY, sale.getQuantity());
        values.put(COLUMN_TOTAL, sale.getTotal());
        values.put(COLUMN_SALE_DATE, sale.getSaleDate());
        return db.insert(TABLE_SALES, null, values);
    }

    public List<Sale> getAllSales() {
        List<Sale> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SALES, null);
        if (cursor.moveToFirst()) {
            do {
                Sale sale = new Sale(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SALE_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SALE_DATE))
                );
                list.add(sale);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // -------------------- ANALYTICS & REPORTS --------------------
    public double getTotalSales() {
        SQLiteDatabase db = this.getReadableDatabase();
        double total = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_TOTAL + ") FROM " + TABLE_SALES, null);
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    public double getTotalProfit() {
        double profit = 0;
        for (Sale sale : getAllSales()) {
            Product p = getProductById(sale.getProductId());
            if (p != null) {
                profit += (sale.getTotal() - p.getCostPrice() * sale.getQuantity());
            }
        }
        return profit;
    }

    public double getInventoryValuation() {
        double value = 0;
        for (Product p : getAllProducts()) {
            value += p.getStock() * p.getCostPrice();
        }
        return value;
    }

    public List<Product> getLowStockProducts(int threshold) {
        List<Product> lowStock = new ArrayList<>();
        for (Product p : getAllProducts()) {
            if (p.getStock() < threshold) lowStock.add(p);
        }
        return lowStock;
    }

    public List<Sale> getSalesBetween(long startDate, long endDate) {
        List<Sale> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SALES + " WHERE " + COLUMN_SALE_DATE + " BETWEEN ? AND ?",
                new String[]{String.valueOf(startDate), String.valueOf(endDate)});
        if (cursor.moveToFirst()) {
            do {
                Sale sale = new Sale(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SALE_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SALE_DATE))
                );
                list.add(sale);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
