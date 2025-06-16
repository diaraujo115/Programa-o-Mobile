package com.example.appfoodtime.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.appfoodtime.R;
import com.example.appfoodtime.database.DatabaseHelper;

public class OrderHistoryActivity extends AppCompatActivity {

    private ListView listViewOrderHistory;
    private TextView textViewNoOrders;
    private DatabaseHelper dbHelper;
    private long currentCustomerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Meus Pedidos");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = DatabaseHelper.getInstance(this);
        listViewOrderHistory = findViewById(R.id.listViewOrderHistory);
        textViewNoOrders = findViewById(R.id.textViewNoOrders);

        currentCustomerId = getIntent().getLongExtra("CUSTOMER_ID", -1);
        if (currentCustomerId == -1) {
            Toast.makeText(this, "Erro de sessão. Faça login novamente.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadOrderHistory();
    }

    private void loadOrderHistory() {
        Cursor cursor = dbHelper.getOrdersByCustomerId(currentCustomerId);

        if (cursor == null || cursor.getCount() == 0) {
            textViewNoOrders.setVisibility(View.VISIBLE);
            listViewOrderHistory.setVisibility(View.GONE);
        } else {
            textViewNoOrders.setVisibility(View.GONE);
            listViewOrderHistory.setVisibility(View.VISIBLE);

            String[] from = {DatabaseHelper.COLUMN_ORDER_ID, DatabaseHelper.COLUMN_ORDER_TOTAL, DatabaseHelper.COLUMN_ORDER_DATE, DatabaseHelper.COLUMN_ORDER_STATUS};
            int[] to = {R.id.textViewOrderHistoryId, R.id.textViewOrderHistoryTotal, R.id.textViewOrderHistoryDate, R.id.textViewOrderHistoryStatus};

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_item_order_history, cursor, from, to, 0);
            listViewOrderHistory.setAdapter(adapter);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
