package com.example.appfoodtime.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.appfoodtime.R;

public class ManagerProfileActivity extends AppCompatActivity {

    private EditText editTextNewEmail, editTextNewPassword;
    private Button buttonSaveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Atualizar Dados do Gerente");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextNewEmail = findViewById(R.id.editTextNewEmail);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);

        buttonSaveChanges.setOnClickListener(v -> updateManagerData());
    }

    private void updateManagerData() {
        String newEmail = editTextNewEmail.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();

        if (newEmail.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Dados atualizados com sucesso!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}