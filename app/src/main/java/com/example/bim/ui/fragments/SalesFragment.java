package com.example.bim.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bim.R;
import com.example.bim.adapter.SaleAdapter;
import com.example.bim.data.DatabaseHelper;
import com.example.bim.data.Product;
import com.example.bim.data.Sale;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class SalesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddSale;
    private DatabaseHelper dbHelper;
    private SaleAdapter saleAdapter;
    private List<Sale> saleList;
    private List<Product> productList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sale, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewSales);
        fabAddSale = view.findViewById(R.id.fabAddSale);

        dbHelper = new DatabaseHelper(getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadSales();

        fabAddSale.setOnClickListener(v -> showAddSaleDialog());
    }

    private void loadSales() {
        saleList = dbHelper.getAllSales();
        saleAdapter = new SaleAdapter(saleList, dbHelper);
        recyclerView.setAdapter(saleAdapter);
    }

    private void showAddSaleDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_sale, null);

        Spinner spinnerProducts = dialogView.findViewById(R.id.spinnerProducts);
        EditText etQuantity = dialogView.findViewById(R.id.etSaleQuantity);

        productList = dbHelper.getAllProducts();
        List<String> productNames = new ArrayList<>();
        for (Product p : productList) {
            productNames.add(p.getName() + " (Stock: " + p.getStock() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, productNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProducts.setAdapter(adapter);

        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Add Sale")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    int position = spinnerProducts.getSelectedItemPosition();
                    Product selectedProduct = productList.get(position);

                    String qtyStr = etQuantity.getText().toString().trim();
                    if (qtyStr.isEmpty()) {
                        Toast.makeText(getContext(), "Enter quantity", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int quantity;
                    try {
                        quantity = Integer.parseInt(qtyStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Invalid number", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (quantity <= 0) {
                        Toast.makeText(getContext(), "Quantity must be > 0", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (quantity > selectedProduct.getStock()) {
                        Toast.makeText(getContext(), "Not enough stock", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Create sale with timestamp
                    long timestamp = System.currentTimeMillis();
                    Sale sale = new Sale(
                            0, // id will auto-increment
                            selectedProduct.getId(),
                            quantity,
                            selectedProduct.getPrice() * quantity,
                            timestamp
                    );
                    dbHelper.addSale(sale);

                    // Update product stock
                    selectedProduct.setStock(selectedProduct.getStock() - quantity);
                    dbHelper.updateProduct(selectedProduct);

                    loadSales();

                    if (selectedProduct.getStock() < 5) {
                        Toast.makeText(getContext(), "Low stock alert!", Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(getContext(), "Sale recorded", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
