package com.example.appfoodtime.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.appfoodtime.R;
import com.example.appfoodtime.database.DatabaseHelper;
import com.example.appfoodtime.model.CartItem;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private ListView listViewCartItems;
    private Button buttonCheckout;
    private CartManager cartManager;
    private TextView textViewTotal, textViewEmptyCart;
    private LinearLayout bottomLayout;
    private DatabaseHelper dbHelper;
    private long currentCustomerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Meu Carrinho");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = DatabaseHelper.getInstance(this);
        cartManager = CartManager.getInstance();

        currentCustomerId = getIntent().getLongExtra("CUSTOMER_ID", -1);

        listViewCartItems = findViewById(R.id.listViewCartItems);
        buttonCheckout = findViewById(R.id.buttonCheckout);
        textViewTotal = findViewById(R.id.textViewTotal);
        textViewEmptyCart = findViewById(R.id.textViewEmptyCart);
        bottomLayout = findViewById(R.id.bottom_layout);

        buttonCheckout.setOnClickListener(v -> finalizePurchase());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }

    private void finalizePurchase() {
        if (cartManager.getCartItems().isEmpty()) {
            Toast.makeText(this, "O seu carrinho está vazio.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentCustomerId == -1) {
            Toast.makeText(this, "Erro de sessão. Por favor, faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        long orderId = dbHelper.createOrder(currentCustomerId, cartManager.getCartItems());

        if (orderId != -1) {
            Toast.makeText(this, "Pedido #" + orderId + " realizado com sucesso!", Toast.LENGTH_LONG).show();
            cartManager.clearCart();
            finish();
        } else {
            Toast.makeText(this, "Erro ao criar o pedido. Tente novamente.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCartItems() {
        List<CartItem> items = cartManager.getCartItems();

        if (items.isEmpty()) {
            textViewEmptyCart.setVisibility(View.VISIBLE);
            listViewCartItems.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
        } else {
            textViewEmptyCart.setVisibility(View.GONE);
            listViewCartItems.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.VISIBLE);

            CartAdapter adapter = new CartAdapter(this, items);
            listViewCartItems.setAdapter(adapter);
            calculateTotal();
        }
    }

    private void calculateTotal() {
        double total = 0;
        for (CartItem item : cartManager.getCartItems()) {
            total += item.getQuantity() * item.getProduct().getPrice();
        }
        textViewTotal.setText(String.format(Locale.US, "Total: R$ %.2f", total));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
