package com.example.bim.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bim.R;
import com.example.bim.data.DatabaseHelper;
import com.example.bim.data.Product;
import com.example.bim.data.Sale;

import java.util.List;

public class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.SaleViewHolder> {

    private List<Sale> saleList;
    private DatabaseHelper dbHelper;

    public SaleAdapter(List<Sale> saleList, DatabaseHelper dbHelper) {
        this.saleList = saleList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sale, parent, false);
        return new SaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleViewHolder holder, int position) {
        Sale sale = saleList.get(position);
        Product product = dbHelper.getProductById(sale.getProductId());

        if (product != null) {
            holder.tvProductName.setText(product.getName());
        } else {
            holder.tvProductName.setText("Unknown Product");
        }

        holder.tvQuantity.setText("Qty: " + sale.getQuantity());
        holder.tvTotal.setText("Total: R" + String.format("%.2f", sale.getTotal()));
    }

    @Override
    public int getItemCount() {
        return saleList.size();
    }

    public static class SaleViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantity, tvTotal;

        public SaleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvSaleProductName);
            tvQuantity = itemView.findViewById(R.id.tvSaleQuantity);
            tvTotal = itemView.findViewById(R.id.tvSaleTotal);
        }
    }
}
