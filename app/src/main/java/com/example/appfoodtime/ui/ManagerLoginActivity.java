package com.example.appfoodtime.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appfoodtime.R;
import com.example.appfoodtime.database.DatabaseHelper;

public class ManagerLoginActivity extends AppCompatActivity {

    private EditText editTextManagerEmail, editTextManagerPassword;
    private Button buttonManagerLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_login);

        dbHelper = DatabaseHelper.getInstance(this);

        editTextManagerEmail = findViewById(R.id.editTextManagerEmail);
        editTextManagerPassword = findViewById(R.id.editTextManagerPassword);
        buttonManagerLogin = findViewById(R.id.buttonManagerLogin);

        buttonManagerLogin.setOnClickListener(v -> loginManager());
    }

    private void loginManager() {
        String email = editTextManagerEmail.getText().toString().trim();
        String password = editTextManagerPassword.getText().toString().trim();

        Cursor cursor = dbHelper.checkAdmin(email, password);
        if (cursor != null && cursor.moveToFirst()) {
            Toast.makeText(this, "Login de gerente bem-sucedido!", Toast.LENGTH_SHORT).show();

            long adminId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID));
            cursor.close();

            Intent intent = new Intent(ManagerLoginActivity.this, ManagerDashboardActivity.class);
            intent.putExtra("ADMIN_ID", adminId);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else {
            Toast.makeText(this, "Credenciais de gerente inválidas.", Toast.LENGTH_SHORT).show();
            if(cursor != null) {
                cursor.close();
            }
        }
    }
}
