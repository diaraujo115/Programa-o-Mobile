package com.example.appfoodtime.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.appfoodtime.R;
import com.example.appfoodtime.model.Product;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends ArrayAdapter<Product> {

    public ProductAdapter(@NonNull Context context, @NonNull List<Product> products) {
        super(context, 0, products);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Product product = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_product_management, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.textViewItemProductName);
        TextView tvStock = convertView.findViewById(R.id.textViewItemProductStock);
        TextView tvPrice = convertView.findViewById(R.id.textViewItemProductPrice);

        if (product != null) {
            tvName.setText(product.getName());
            tvStock.setText(String.format(Locale.getDefault(), "Estoque: %d", product.getStock()));
            tvPrice.setText(String.format(Locale.US, "R$ %.2f", product.getPrice()));
        }

        return convertView;
    }
}
