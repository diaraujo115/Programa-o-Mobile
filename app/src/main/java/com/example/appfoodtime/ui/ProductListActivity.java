package com.example.appfoodtime.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.appfoodtime.R;
import com.example.appfoodtime.database.DatabaseHelper;

public class ProductListActivity extends AppCompatActivity {

    private ListView listViewProducts;
    private DatabaseHelper dbHelper;
    private long currentCustomerId;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Nossos Produtos");
        }

        dbHelper = DatabaseHelper.getInstance(this);

        listViewProducts = findViewById(R.id.listViewProducts);

        currentCustomerId = getIntent().getLongExtra("CUSTOMER_ID", -1);
        if (currentCustomerId == -1) {
            Toast.makeText(this, "Erro de autenticação.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        listViewProducts.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", id);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("CUSTOMER_ID", currentCustomerId);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_cart) {
            Intent intent = new Intent(this, CartActivity.class);
            intent.putExtra("CUSTOMER_ID", currentCustomerId);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_order_history) {
            Intent intent = new Intent(this, OrderHistoryActivity.class);
            intent.putExtra("CUSTOMER_ID", currentCustomerId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadProducts() {
        Cursor cursor = dbHelper.getAllProducts();
        if (cursor == null) {
            Toast.makeText(this, "Não foi possível carregar os produtos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Nenhum produto cadastrado.", Toast.LENGTH_SHORT).show();
        }

        String[] fromColumns = {DatabaseHelper.COLUMN_PRODUCT_NAME, DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION, DatabaseHelper.COLUMN_PRODUCT_PRICE};
        int[] toViews = {R.id.textViewProductName, R.id.textViewProductDescription, R.id.textViewProductPrice};

        // Se o adapter ainda não foi criado, cria-o. Se já existe, apenas troca o cursor.
        if (adapter == null) {
            adapter = new SimpleCursorAdapter(this, R.layout.list_item_product, cursor, fromColumns, toViews, 0);
            listViewProducts.setAdapter(adapter);
        } else {
            adapter.changeCursor(cursor);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Garante que o cursor é fechado para evitar memory leaks
        if (adapter != null && adapter.getCursor() != null) {
            adapter.getCursor().close();
        }
    }
}
