package com.example.appfoodtime.ui;

import com.example.appfoodtime.model.CartItem;
import com.example.appfoodtime.model.Product;
import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static CartManager instance;
    private final List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(Product product) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        cartItems.add(new CartItem(product));
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public double getTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getQuantity() * item.getProduct().getPrice();
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
    }
}
