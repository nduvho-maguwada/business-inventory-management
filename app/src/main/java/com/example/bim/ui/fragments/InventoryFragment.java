package com.example.bim.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bim.R;
import com.example.bim.adapter.ProductAdapter;
import com.example.bim.data.DatabaseHelper;
import com.example.bim.data.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class InventoryFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FloatingActionButton fabAddProduct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(getContext());
        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadProducts();

        fabAddProduct.setOnClickListener(v -> showAddProductDialog());
    }

    private void loadProducts() {
        productList = dbHelper.getAllProducts();
        productAdapter = new ProductAdapter(productList, new ProductAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                Product product = productList.get(position);
                showProductOptionsDialog(product);
            }
        });
        recyclerView.setAdapter(productAdapter);
    }

    private void showAddProductDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_product, null);

        EditText etName = dialogView.findViewById(R.id.etProductName);
        EditText etPrice = dialogView.findViewById(R.id.etProductPrice);
        EditText etStock = dialogView.findViewById(R.id.etProductStock);
        EditText etCategory = dialogView.findViewById(R.id.etProductCategory);

        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Add Product")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();
                    String stockStr = etStock.getText().toString().trim();
                    String category = etCategory.getText().toString().trim();

                    if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || category.isEmpty()) {
                        Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double price;
                    int stock;
                    try {
                        price = Double.parseDouble(priceStr);
                        stock = Integer.parseInt(stockStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Invalid number format", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Use selling price as default costPrice for now
                    Product product = new Product(0, name, price, price, stock, category);
                    dbHelper.addProduct(product);
                    loadProducts();

                    if (stock < 5) {
                        Toast.makeText(getContext(), "Low stock alert!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Product added", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showProductOptionsDialog(Product product) {
        String[] options = {"Edit", "Delete"};
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(product.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showEditProductDialog(product);
                    } else if (which == 1) {
                        deleteProduct(product);
                    }
                }).show();
    }

    private void showEditProductDialog(Product product) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_product, null);

        EditText etName = dialogView.findViewById(R.id.etProductName);
        EditText etPrice = dialogView.findViewById(R.id.etProductPrice);
        EditText etStock = dialogView.findViewById(R.id.etProductStock);
        EditText etCategory = dialogView.findViewById(R.id.etProductCategory);

        etName.setText(product.getName());
        etPrice.setText(String.valueOf(product.getPrice()));
        etStock.setText(String.valueOf(product.getStock()));
        etCategory.setText(product.getCategory());

        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Edit Product")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();
                    String stockStr = etStock.getText().toString().trim();
                    String category = etCategory.getText().toString().trim();

                    if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || category.isEmpty()) {
                        Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double price;
                    int stock;
                    try {
                        price = Double.parseDouble(priceStr);
                        stock = Integer.parseInt(stockStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Invalid number format", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    product.setName(name);
                    product.setPrice(price);
                    product.setStock(stock);
                    product.setCategory(category);
                    // Keep costPrice same as before
                    dbHelper.updateProduct(product);
                    loadProducts();

                    if (stock < 5) {
                        Toast.makeText(getContext(), "Low stock alert!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Product updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteProduct(Product product) {
        dbHelper.deleteProduct(product.getId());
        loadProducts();
        Toast.makeText(getContext(), "Product deleted", Toast.LENGTH_SHORT).show();
    }
}
