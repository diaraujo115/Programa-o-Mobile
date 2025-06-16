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
import com.example.appfoodtime.model.CartItem;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends ArrayAdapter<CartItem> {
    public CartAdapter(@NonNull Context context, List<CartItem> cartItems) {
        super(context, 0, cartItems);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_cart, parent, false);
        }
        CartItem item = getItem(position);
        TextView tvName = convertView.findViewById(R.id.textViewCartItemName);
        TextView tvQuantity = convertView.findViewById(R.id.textViewCartItemQuantity);
        TextView tvPrice = convertView.findViewById(R.id.textViewCartItemPrice);
        if (item != null) {
            tvName.setText(item.getProduct().getName());
            tvQuantity.setText(String.format(Locale.getDefault(), "Qtd: %d", item.getQuantity()));
            double subtotal = item.getQuantity() * item.getProduct().getPrice();
            tvPrice.setText(String.format(Locale.US, "R$ %.2f", subtotal));
        }
        return convertView;
    }
}