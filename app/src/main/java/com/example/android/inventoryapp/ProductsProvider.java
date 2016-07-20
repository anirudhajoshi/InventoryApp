package com.example.android.inventoryapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by anirudha.joshi on 7/13/2016.
 */
public class ProductsProvider extends ContentProvider {

    private static final String PROVIDER_NAME = "com.example.android.inventoryapp.Product";
    private static final String URL = "content://" + PROVIDER_NAME + "/product";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    private static final int PRODUCTS = 1;
    private static final int PRODUCTID = 2;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "products", PRODUCTS);
        uriMatcher.addURI(PROVIDER_NAME, "products/#", PRODUCTID);
    }

    private SQLiteDatabase db;

    public class InventoryDBHelper extends SQLiteOpenHelper {

        private static final String SQL_CREATE_PRODUCT_ENTRIES =
                "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " (" +
                        ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ProductContract.ProductEntry.COLUMN_NAME_PROD_NAME + " TEXT," +
                        ProductContract.ProductEntry.COLUMN_NAME_PROD_QUANTITY + " INTEGER," +
                        ProductContract.ProductEntry.COLUMN_NAME_PROD_PRICE + " FLOAT," +
                        ProductContract.ProductEntry.COLUMN_NAME_PROD_IMAGEPATH + " TEXT" + ")";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + ProductContract.ProductEntry.TABLE_NAME;

        public InventoryDBHelper(Context context) {
            super(context, ProductContract.ProductEntry.DATABASE_NAME,
                    null,
                    ProductContract.ProductEntry.DATABASE_VERSION);
        }

        // Creating Tables
        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(SQL_CREATE_PRODUCT_ENTRIES);
            } catch (Exception e) {
                Log.d("InventoryDBHelper", e.toString());
            }
            Log.d("InventoryDBHelper", SQL_CREATE_PRODUCT_ENTRIES);
        }

        // Upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            try {
                db.execSQL(SQL_DELETE_ENTRIES);
                onCreate(db);
            } catch (Exception e) {
                Log.d("InventoryDBHelper", e.toString());
            }
            Log.d("InventoryDBHelper", SQL_CREATE_PRODUCT_ENTRIES);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    @Override
    public boolean onCreate() {
        try {
            Context context = getContext();
            InventoryDBHelper dbHelper = new InventoryDBHelper(context);

            // Create a write able database which will trigger its
            // creation if it doesn't already exist.
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.d("ProductsProvider", e.toString());
        }

        return (db == null) ? false : true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long rowID = -1;
        Uri _uri = null;

        try {
            // Add a new product record
            rowID = db.insertOrThrow(ProductContract.ProductEntry.TABLE_NAME, null, values);

            // If record is added
            if (rowID > 0) {
                _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(_uri, null);
                return _uri;
            }
        } catch (Exception e) {
            Log.d("ProductsProvider", e.toString());
        }

        Log.d("ProductsProvider", "Inserted count = " + rowID);

        return _uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor c = null;

        try {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(ProductContract.ProductEntry.TABLE_NAME);

            if (sortOrder == null || sortOrder == "") {
                // Sort on product name by default
                sortOrder = ProductContract.ProductEntry.COLUMN_NAME_PROD_NAME;
            }

            Log.d("ProductsProvider", "Executing query on cursor");

            c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

            Log.d("ProductsProvider", "Query executed on cursor");

            // Register to watch content URI for changes
            c.setNotificationUri(getContext().getContentResolver(), uri);

        } catch (Exception e) {
            Log.d("ProductsProvider", e.toString());
        }

        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        try {
            count = db.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
        } catch (Exception e) {
            Log.d("ProductsProvider", e.toString());
        }

        // Notify content provider of data change
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;

        try {
            count = db.update(ProductContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        } catch (Exception e) {
            Log.d("ProductsProvider", e.toString());
        }

        // Notify content provider of data change
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public String getType(Uri uri) {
        try {

            switch (uriMatcher.match(uri)) {

                // Get all product recrods
                case PRODUCTS:
                    String ret_mulitple = getContext().getContentResolver().getType(CONTENT_URI);
                    return ret_mulitple;

                    /**
                     * Get a particular product
                     */
                case PRODUCTID:
                    String ret_single = getContext().getContentResolver().getType(CONTENT_URI);
                    return ret_single;

                default:
                    String ret_unsupported = "Unsupported URI";
                    return ret_unsupported;
            }
        } catch (Exception e) {
            Log.d("ProductsProvider", e.toString());
        }

        // Keep the compiler happy
        return "";
    }
}
