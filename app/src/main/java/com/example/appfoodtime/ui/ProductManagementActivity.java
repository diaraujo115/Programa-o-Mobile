package com.example.appfoodtime.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.appfoodtime.R;
import com.example.appfoodtime.database.DatabaseHelper;
import com.example.appfoodtime.model.Product;

public class ProductManagementActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView listViewAllProducts;
    private Button buttonAddNewProduct;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Gerenciar Produtos");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = DatabaseHelper.getInstance(this);

        listViewAllProducts = findViewById(R.id.listViewAllProducts);
        buttonAddNewProduct = findViewById(R.id.buttonAddNewProduct);

        buttonAddNewProduct.setOnClickListener(v -> showProductDialog(null));

        listViewAllProducts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showOptionsDialog(id);
                return true;
            }
        });

        loadAllProducts();
    }

    private void loadAllProducts() {
        Cursor cursor = dbHelper.getAllProducts();
        String[] from = {DatabaseHelper.COLUMN_PRODUCT_NAME, DatabaseHelper.COLUMN_PRODUCT_STOCK, DatabaseHelper.COLUMN_PRODUCT_PRICE};
        int[] to = {R.id.textViewItemProductName, R.id.textViewItemProductStock, R.id.textViewItemProductPrice};
        adapter = new SimpleCursorAdapter(this, R.layout.list_item_product_management, cursor, from, to, 0);
        listViewAllProducts.setAdapter(adapter);
    }

    private void showOptionsDialog(final long productId) {
        final CharSequence[] options = {"Editar", "Excluir"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha uma Ação");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Editar")) {
                Product productToEdit = dbHelper.getProductById(productId);
                if (productToEdit != null) {
                    showProductDialog(productToEdit);
                }
            } else if (options[item].equals("Excluir")) {
                showDeleteConfirmationDialog(productId);
            }
        });
        builder.show();
    }

    private void showDeleteConfirmationDialog(final long productId) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Exclusão")
                .setMessage("Você tem certeza que deseja excluir este produto?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    boolean success = dbHelper.deleteProduct(String.valueOf(productId));
                    if (success) {
                        Toast.makeText(this, "Produto excluído!", Toast.LENGTH_SHORT).show();
                        refreshProductList();
                    } else {
                        Toast.makeText(this, "Falha ao excluir produto.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void showProductDialog(final Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(product == null ? "Adicionar Novo Produto" : "Editar Produto");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null);
        builder.setView(view);

        final EditText inputName = view.findViewById(R.id.editTextProductName);
        final EditText inputDescription = view.findViewById(R.id.editTextProductDescription);
        final EditText inputPrice = view.findViewById(R.id.editTextProductPrice);
        final EditText inputStock = view.findViewById(R.id.editTextProductStock);

        if (product != null) {
            inputName.setText(product.getName());
            inputDescription.setText(product.getDescription());
            inputPrice.setText(String.valueOf(product.getPrice()));
            inputStock.setText(String.valueOf(product.getStock()));
        }

        builder.setPositiveButton(product == null ? "Adicionar" : "Salvar", (dialog, which) -> {
            String name = inputName.getText().toString();
            String description = inputDescription.getText().toString();
            String priceStr = inputPrice.getText().toString();
            String stockStr = inputStock.getText().toString();

            if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                Toast.makeText(this, "Nome, preço e estoque são obrigatórios.", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            boolean success;
            if (product == null) {
                success = dbHelper.addProduct(name, description, "", price, stock);
            } else {
                success = dbHelper.updateProduct(String.valueOf(product.getId()), name, description, price, stock);
            }

            if (success) {
                Toast.makeText(this, "Produto " + (product == null ? "adicionado" : "atualizado") + "!", Toast.LENGTH_SHORT).show();
                refreshProductList();
            } else {
                Toast.makeText(this, "Falha na operação.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void refreshProductList() {
        Cursor newCursor = dbHelper.getAllProducts();
        adapter.changeCursor(newCursor);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null && adapter.getCursor() != null) {
            adapter.getCursor().close();
        }
    }
}
