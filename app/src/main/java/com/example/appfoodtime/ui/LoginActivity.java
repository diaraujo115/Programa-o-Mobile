package com.example.appfoodtime.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appfoodtime.R;
import com.example.appfoodtime.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private TextView textViewManagerLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // --- ALTERAÇÃO PRINCIPAL AQUI ---
        // Em vez de criar uma nova instância, obtemos a instância única.
        dbHelper = DatabaseHelper.getInstance(this);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
        textViewManagerLogin = findViewById(R.id.textViewManagerLogin);

        buttonLogin.setOnClickListener(v -> loginUser());
        textViewRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        textViewManagerLogin.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ManagerLoginActivity.class)));
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.checkCustomer(email, password);

        if (cursor != null && cursor.moveToFirst()) {
            Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();

            long customerId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CUSTOMER_ID));

            Intent intent = new Intent(LoginActivity.this, ProductListActivity.class);
            intent.putExtra("CUSTOMER_ID", customerId);

            startActivity(intent);
            cursor.close();
            finish();
        } else {
            Toast.makeText(this, "Email ou senha inválidos.", Toast.LENGTH_SHORT).show();
            if(cursor != null) cursor.close();
        }
    }
}
