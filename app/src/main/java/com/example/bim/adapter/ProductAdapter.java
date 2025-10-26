package com.example.bim.adapter;
//Nduvho Maguwada 402306070
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bim.R;
import com.example.bim.data.Product;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private OnItemLongClickListener listener;

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public ProductAdapter(List<Product> productList, OnItemLongClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText("Price: " + product.getPrice());
        holder.tvStock.setText("Stock: " + product.getStock());
        holder.tvCategory.setText(product.getCategory());

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onItemLongClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvStock, tvCategory;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvStock = itemView.findViewById(R.id.tvProductStock);
            tvCategory = itemView.findViewById(R.id.tvProductCategory);
        }
    }
}
