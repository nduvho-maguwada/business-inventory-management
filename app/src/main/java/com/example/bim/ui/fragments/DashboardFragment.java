package com.example.bim.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bim.R;
import com.example.bim.data.DatabaseHelper;
import com.example.bim.data.Product;
import com.example.bim.data.Sale;

import java.util.List;

public class DashboardFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private TextView tvTotalProducts, tvProductNames,
            tvTotalSales, tvSalesNames,
            tvLowStock, tvLowStockNames;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the modern dashboard layout
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(getContext());

        // Bind all the TextViews
        tvTotalProducts = view.findViewById(R.id.tvTotalProducts);
        tvProductNames = view.findViewById(R.id.tvProductNames);
        tvTotalSales = view.findViewById(R.id.tvTotalSales);
        tvSalesNames = view.findViewById(R.id.tvSalesNames);
        tvLowStock = view.findViewById(R.id.tvLowStock);
        tvLowStockNames = view.findViewById(R.id.tvLowStockNames);

        loadDashboardMetrics();
    }

    private void loadDashboardMetrics() {
        // Load all products
        List<Product> allProducts = dbHelper.getAllProducts();
        tvTotalProducts.setText(String.valueOf(allProducts.size()));

        // Show product names
        StringBuilder productNames = new StringBuilder();
        for (Product p : allProducts) {
            productNames.append("• ").append(p.getName()).append("\n");
        }
        tvProductNames.setText(productNames.toString());

        // Load all sales
        List<Sale> allSales = dbHelper.getAllSales();
        tvTotalSales.setText(String.valueOf(allSales.size()));

        // Show sales details
        StringBuilder salesDetails = new StringBuilder();
        for (Sale s : allSales) {
            Product product = dbHelper.getProductById(s.getProductId());
            if (product != null) {
                salesDetails.append("• ").append(product.getName())
                        .append(" x").append(s.getQuantity())
                        .append(" = R").append(String.format("%.2f", s.getTotal()))
                        .append("\n");
            }
        }
        tvSalesNames.setText(salesDetails.toString());

        // Low stock products
        List<Product> lowStockProducts = allProducts.stream()
                .filter(p -> p.getStock() < 5)
                .toList();
        tvLowStock.setText(String.valueOf(lowStockProducts.size()));

        StringBuilder lowStockNames = new StringBuilder();
        for (Product p : lowStockProducts) {
            lowStockNames.append("• ").append(p.getName())
                    .append(" (").append(p.getStock()).append(" left)")
                    .append("\n");
        }
        tvLowStockNames.setText(lowStockNames.toString());
    }
}
