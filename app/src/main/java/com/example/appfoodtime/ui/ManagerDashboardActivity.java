package com.example.appfoodtime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.appfoodtime.R;

public class ManagerDashboardActivity extends AppCompatActivity {

    private Button buttonManageProducts, buttonViewOrders;
    private long currentAdminId; // Para guardar o ID do gerente

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Painel do Gerente");
        }

        currentAdminId = getIntent().getLongExtra("ADMIN_ID", -1);

        buttonManageProducts = findViewById(R.id.buttonManageProducts);
        buttonViewOrders = findViewById(R.id.buttonViewOrders);

        buttonManageProducts.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerDashboardActivity.this, ProductManagementActivity.class);
            startActivity(intent);
        });

        buttonViewOrders.setOnClickListener(v -> {
            Toast.makeText(ManagerDashboardActivity.this, "Tela de pedidos a ser implementada.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.manager_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_manager_profile) {
            Intent intent = new Intent(this, ManagerProfileActivity.class);
            intent.putExtra("ADMIN_ID", currentAdminId); // Passa o ID para a tela de perfil
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_manager_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        Intent intent = new Intent(ManagerDashboardActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
