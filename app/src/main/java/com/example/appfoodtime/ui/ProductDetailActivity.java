package com.example.appfoodtime.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.appfoodtime.R;
import com.example.appfoodtime.database.DatabaseHelper;
import com.example.appfoodtime.model.Product;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView textViewDetailName, textViewDetailDescription, textViewDetailPrice, textViewDetailStock;
    private Button buttonAddToCart;
    private DatabaseHelper dbHelper;
    private Product currentProduct;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = DatabaseHelper.getInstance(this);
        cartManager = CartManager.getInstance();

        textViewDetailName = findViewById(R.id.textViewDetailName);
        textViewDetailDescription = findViewById(R.id.textViewDetailDescription);
        textViewDetailPrice = findViewById(R.id.textViewDetailPrice);
        textViewDetailStock = findViewById(R.id.textViewDetailStock);
        buttonAddToCart = findViewById(R.id.buttonAddToCart);

        long productId = getIntent().getLongExtra("PRODUCT_ID", -1);
        if (productId == -1) {
            Toast.makeText(this, "Erro: Produto não encontrado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadProductDetails(productId);

        buttonAddToCart.setOnClickListener(v -> {
            if (currentProduct != null) {
                cartManager.addToCart(currentProduct);
                Toast.makeText(this, currentProduct.getName() + " adicionado ao carrinho!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductDetails(long productId) {
        currentProduct = dbHelper.getProductById(productId);
        if (currentProduct != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(currentProduct.getName());
            }
            textViewDetailName.setText(currentProduct.getName());
            textViewDetailDescription.setText(currentProduct.getDescription());
            textViewDetailPrice.setText(String.format(Locale.US, "R$ %.2f", currentProduct.getPrice()));
            textViewDetailStock.setText(String.format(Locale.getDefault(), "Disponível: %d", currentProduct.getStock()));
        } else {
            Toast.makeText(this, "Não foi possível carregar os detalhes do produto.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
