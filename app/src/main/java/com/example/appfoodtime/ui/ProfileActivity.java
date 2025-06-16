package com.example.appfoodtime.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.appfoodtime.R;
import com.example.appfoodtime.database.DatabaseHelper;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewProfileName, textViewProfileEmail, textViewProfilePhone;
    private Button buttonUpdateData, buttonLogout;
    private DatabaseHelper dbHelper;
    private long currentCustomerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Meu Perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = DatabaseHelper.getInstance(this);

        textViewProfileName = findViewById(R.id.textViewProfileName);
        textViewProfileEmail = findViewById(R.id.textViewProfileEmail);
        textViewProfilePhone = findViewById(R.id.textViewProfilePhone);
        buttonUpdateData = findViewById(R.id.buttonUpdateData);
        buttonLogout = findViewById(R.id.buttonLogout);

        currentCustomerId = getIntent().getLongExtra("CUSTOMER_ID", -1);

        loadCustomerData();

        buttonLogout.setOnClickListener(v -> logout());

        buttonUpdateData.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidade de atualizar dados a ser implementada.", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadCustomerData() {
        Cursor cursor = dbHelper.getCustomerById(currentCustomerId);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CUSTOMER_NAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CUSTOMER_EMAIL));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CUSTOMER_PHONE));

            textViewProfileName.setText(name);
            textViewProfileEmail.setText(email);
            textViewProfilePhone.setText(phone.isEmpty() ? "Telefone não informado" : phone);

            cursor.close();
        }
    }

    private void logout() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
