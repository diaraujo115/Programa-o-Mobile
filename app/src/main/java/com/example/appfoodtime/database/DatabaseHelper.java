package com.example.appfoodtime.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import com.example.appfoodtime.model.CartItem;
import com.example.appfoodtime.model.Product;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;

    private static final String DATABASE_NAME = "FoodTime.db";
    private static final int DATABASE_VERSION = 7; // Versão final para forçar atualização

    // --- Constantes das Tabelas ---
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";

    public static final String TABLE_CUSTOMERS = "customers";
    public static final String COLUMN_CUSTOMER_ID = "_id";
    public static final String COLUMN_CUSTOMER_NAME = "name";
    public static final String COLUMN_CUSTOMER_EMAIL = "email";
    public static final String COLUMN_CUSTOMER_PHONE = "phone";
    public static final String COLUMN_CUSTOMER_PASSWORD = "password";

    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_PRODUCT_ID = "_id";
    public static final String COLUMN_PRODUCT_NAME = "name";
    public static final String COLUMN_PRODUCT_DESCRIPTION = "description";
    public static final String COLUMN_PRODUCT_IMAGE_URI = "image_uri";
    public static final String COLUMN_PRODUCT_PRICE = "price";
    public static final String COLUMN_PRODUCT_STOCK = "stock";

    public static final String TABLE_ORDERS = "orders";
    public static final String COLUMN_ORDER_ID = "_id";
    public static final String COLUMN_ORDER_CUSTOMER_ID = "customer_id";
    public static final String COLUMN_ORDER_DATE = "order_date";
    public static final String COLUMN_ORDER_TOTAL = "total";
    public static final String COLUMN_ORDER_STATUS = "status";

    public static final String TABLE_ORDER_ITEMS = "order_items";
    public static final String COLUMN_ORDER_ITEM_ID = "_id";
    public static final String COLUMN_ORDER_ITEM_ORDER_ID = "order_id";
    public static final String COLUMN_ORDER_ITEM_PRODUCT_ID = "product_id";
    public static final String COLUMN_ORDER_ITEM_QUANTITY = "quantity";
    public static final String COLUMN_ORDER_ITEM_PRICE = "price_at_purchase";


    private DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL, "
                + COLUMN_USER_PASSWORD + " TEXT NOT NULL)";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_CUSTOMERS_TABLE = "CREATE TABLE " + TABLE_CUSTOMERS + " ("
                + COLUMN_CUSTOMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CUSTOMER_NAME + " TEXT NOT NULL, "
                + COLUMN_CUSTOMER_EMAIL + " TEXT UNIQUE NOT NULL, "
                + COLUMN_CUSTOMER_PASSWORD + " TEXT NOT NULL, "
                + COLUMN_CUSTOMER_PHONE + " TEXT)";
        db.execSQL(CREATE_CUSTOMERS_TABLE);

        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + " ("
                + COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + COLUMN_PRODUCT_DESCRIPTION + " TEXT, "
                + COLUMN_PRODUCT_IMAGE_URI + " TEXT, "
                + COLUMN_PRODUCT_PRICE + " REAL NOT NULL, "
                + COLUMN_PRODUCT_STOCK + " INTEGER NOT NULL)";
        db.execSQL(CREATE_PRODUCTS_TABLE);

        String CREATE_ORDERS_TABLE = "CREATE TABLE " + TABLE_ORDERS + " ("
                + COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ORDER_CUSTOMER_ID + " INTEGER, "
                + COLUMN_ORDER_DATE + " TEXT NOT NULL, "
                + COLUMN_ORDER_TOTAL + " REAL NOT NULL, "
                + COLUMN_ORDER_STATUS + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + COLUMN_ORDER_CUSTOMER_ID + ") REFERENCES " + TABLE_CUSTOMERS + "(" + COLUMN_CUSTOMER_ID + "))";
        db.execSQL(CREATE_ORDERS_TABLE);

        String CREATE_ORDER_ITEMS_TABLE = "CREATE TABLE " + TABLE_ORDER_ITEMS + " ("
                + COLUMN_ORDER_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ORDER_ITEM_ORDER_ID + " INTEGER NOT NULL, "
                + COLUMN_ORDER_ITEM_PRODUCT_ID + " INTEGER NOT NULL, "
                + COLUMN_ORDER_ITEM_QUANTITY + " INTEGER NOT NULL, "
                + COLUMN_ORDER_ITEM_PRICE + " REAL NOT NULL, "
                + "FOREIGN KEY(" + COLUMN_ORDER_ITEM_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "), "
                + "FOREIGN KEY(" + COLUMN_ORDER_ITEM_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + "))";
        db.execSQL(CREATE_ORDER_ITEMS_TABLE);

        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_USER_EMAIL, "admin");
        adminValues.put(COLUMN_USER_PASSWORD, "123");
        db.insert(TABLE_USERS, null, adminValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // --- MÉTODOS PARA ADMIN (GERENTE) ---
    public Cursor checkAdmin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + " = ? AND " + COLUMN_USER_PASSWORD + " = ?", new String[]{email, password});
    }

    public boolean updateAdmin(long adminId, String newEmail, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, newEmail);
        values.put(COLUMN_USER_PASSWORD, newPassword);
        int result = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(adminId)});
        return result > 0;
    }

    // --- MÉTODOS PARA CLIENTES ---
    public boolean addCustomer(String name, String email, String password, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOMER_NAME, name);
        values.put(COLUMN_CUSTOMER_EMAIL, email);
        values.put(COLUMN_CUSTOMER_PASSWORD, password);
        values.put(COLUMN_CUSTOMER_PHONE, phone);
        return db.insert(TABLE_CUSTOMERS, null, values) != -1;
    }

    public Cursor checkCustomer(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CUSTOMERS + " WHERE " + COLUMN_CUSTOMER_EMAIL + " = ? AND " + COLUMN_CUSTOMER_PASSWORD + " = ?", new String[]{email, password});
    }

    public Cursor getCustomerById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CUSTOMERS, null, COLUMN_CUSTOMER_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
    }

    // --- MÉTODOS PARA PRODUTOS ---
    public boolean addProduct(String name, String description, String imageUri, double price, int stock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, name);
        values.put(COLUMN_PRODUCT_DESCRIPTION, description);
        values.put(COLUMN_PRODUCT_IMAGE_URI, imageUri);
        values.put(COLUMN_PRODUCT_PRICE, price);
        values.put(COLUMN_PRODUCT_STOCK, stock);
        return db.insert(TABLE_PRODUCTS, null, values) != -1;
    }

    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);
    }

    public boolean updateProduct(String id, String name, String description, double price, int stock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, name);
        values.put(COLUMN_PRODUCT_DESCRIPTION, description);
        values.put(COLUMN_PRODUCT_PRICE, price);
        values.put(COLUMN_PRODUCT_STOCK, stock);
        return db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCT_ID + " = ?", new String[]{id}) > 0;
    }

    public boolean deleteProduct(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PRODUCTS, COLUMN_PRODUCT_ID + " = ?", new String[]{id}) > 0;
    }

    public Product getProductById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, COLUMN_PRODUCT_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        Product product = null;
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE));
            int stock = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_STOCK));
            product = new Product(id, name, description, price, stock);
        }
        if (cursor != null) {
            cursor.close();
        }
        return product;
    }

    // --- MÉTODOS PARA PEDIDOS ---
    public long createOrder(long customerId, List<CartItem> cartItems) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long orderId = -1;
        try {
            double total = 0;
            for (CartItem item : cartItems) {
                total += item.getQuantity() * item.getProduct().getPrice();
            }
            ContentValues orderValues = new ContentValues();
            orderValues.put(COLUMN_ORDER_CUSTOMER_ID, customerId);
            orderValues.put(COLUMN_ORDER_DATE, new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()));
            orderValues.put(COLUMN_ORDER_TOTAL, total);
            orderValues.put(COLUMN_ORDER_STATUS, "Pendente");
            orderId = db.insert(TABLE_ORDERS, null, orderValues);

            if (orderId == -1) return -1;

            for (CartItem item : cartItems) {
                ContentValues itemValues = new ContentValues();
                itemValues.put(COLUMN_ORDER_ITEM_ORDER_ID, orderId);
                itemValues.put(COLUMN_ORDER_ITEM_PRODUCT_ID, item.getProduct().getId());
                itemValues.put(COLUMN_ORDER_ITEM_QUANTITY, item.getQuantity());
                itemValues.put(COLUMN_ORDER_ITEM_PRICE, item.getProduct().getPrice());
                db.insert(TABLE_ORDER_ITEMS, null, itemValues);
                db.execSQL("UPDATE " + TABLE_PRODUCTS + " SET " + COLUMN_PRODUCT_STOCK + " = " + COLUMN_PRODUCT_STOCK + " - " + item.getQuantity() + " WHERE " + COLUMN_PRODUCT_ID + " = " + item.getProduct().getId());
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return orderId;
    }

    public Cursor getOrdersByCustomerId(long customerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ORDERS + " WHERE " + COLUMN_ORDER_CUSTOMER_ID + " = ? ORDER BY " + COLUMN_ORDER_ID + " DESC", new String[]{String.valueOf(customerId)});
    }

    public Cursor getAllOrdersWithCustomerName() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT o." + COLUMN_ORDER_ID + ", o." + COLUMN_ORDER_DATE + ", o." + COLUMN_ORDER_TOTAL + ", o." + COLUMN_ORDER_STATUS + ", c." + COLUMN_CUSTOMER_NAME +
                " FROM " + TABLE_ORDERS + " o" +
                " LEFT JOIN " + TABLE_CUSTOMERS + " c ON o." + COLUMN_ORDER_CUSTOMER_ID + " = c." + COLUMN_CUSTOMER_ID +
                " ORDER BY o." + COLUMN_ORDER_ID + " DESC";
        return db.rawQuery(query, null);
    }
}
