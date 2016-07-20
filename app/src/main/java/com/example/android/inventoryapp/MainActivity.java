package com.example.android.inventoryapp;

import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, AddProduct_Dialog.NoticeDialogListener {

    // This is the Adapter being used to display the list's data.
    private CustomCursorAdapter mAdapter;
    private ListView listview;
    private Bundle args = new Bundle();
    private Cursor cursor;
    private Button addproduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

        listview = (ListView) findViewById(R.id.listview);
        listview.setEmptyView(findViewById(R.id.empty_layout));

        String[] fromColumns = new String[]{
                ProductContract.ProductEntry.COLUMN_NAME_PROD_NAME,
                ProductContract.ProductEntry.COLUMN_NAME_PROD_QUANTITY,
                ProductContract.ProductEntry.COLUMN_NAME_PROD_PRICE};

        int[] toFields = new int[]{R.id.productname, R.id.productquantity, R.id.productprice};

        mAdapter = new CustomCursorAdapter(this,        // current context
                R.layout.list_item,     // Layout for a single row
                cursor,                 // Cursor
                fromColumns,            // Cursor columns to use
                toFields,               // Layout fields to use
                0);                     // No flags

        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh the listview with any data changes
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri PRODUCTS_URI = ProductsProvider.CONTENT_URI;

        String[] projection =
                {
                        // Contract class constant for the _ID column name
                        ProductContract.ProductEntry._ID + " as _id", /* need this for the simplecuroradapter to work */
                        ProductContract.ProductEntry.COLUMN_NAME_PROD_NAME,
                        ProductContract.ProductEntry.COLUMN_NAME_PROD_QUANTITY,
                        ProductContract.ProductEntry.COLUMN_NAME_PROD_PRICE,
                        ProductContract.ProductEntry.COLUMN_NAME_PROD_IMAGEPATH
                };

        // Defines a string to contain the selection clause
        String selection = null;

        // An array to contain selection arguments
        String[] selectionArgs = null;

        String sortOrder = null;

        Log.d("MainActivity", "onCreateLoader: Calling cursorLoader");

        CursorLoader cursorLoader = new CursorLoader(
                this,   // context
                PRODUCTS_URI,   // URI
                projection,   // projection
                selection,   // selection
                selectionArgs,   // selectionArgs
                sortOrder);  // sortOrder

        Log.d("MainActivity", "onCreateLoader: Returning cursor");

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        try {

            if (cursor != null) {
                cursor.moveToFirst();
                listview.setAdapter(mAdapter);
                mAdapter.swapCursor(cursor);

                // Start the product details activity
                final Intent productDetailsIntent = new Intent(this, ProductDetailsActivity.class);

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View v, int position,
                                            long arg3) {
                        Cursor selecteditem = (Cursor) adapter.getItemAtPosition(position);
                        if (selecteditem != null) {
                            Log.d("MainActivity", selecteditem.toString());
                            String row_id = selecteditem.getString(selecteditem.getColumnIndex(ProductContract.ProductEntry._ID));
                            String name = selecteditem.getString((selecteditem.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME_PROD_NAME)));
                            String quantityStr = selecteditem.getString((selecteditem.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME_PROD_QUANTITY)));
                            String priceStr = selecteditem.getString((selecteditem.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME_PROD_PRICE)));
                            String imagefile = selecteditem.getString((selecteditem.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME_PROD_IMAGEPATH)));

                            Bundle b = new Bundle();
                            b.putString("row_id", row_id);
                            b.putString("productname", name);
                            b.putString("productquantity", quantityStr);
                            b.putString("productprice", priceStr);
                            b.putString("imagefile", imagefile);

                            productDetailsIntent.putExtras(b);

                            startActivity(productDetailsIntent);

                        }
                    }
                });
            } else {
                Log.d("MainActivity", "onFinishedLoader: Cursor is null");
            }
        } catch (Exception e) {
            Log.d("MainACtivity", e.toString());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);
    }


    public void onListItemClick(ListView l, View v, int position, long id) {

        Log.d("Main Activity", "Item ID = " + id);
    }

    public void onClickAddProduct(View view) {

        Log.d("MainActiity", "Add Products button clicked.");

        DialogFragment newFragment = new AddProduct_Dialog();
        args.putString("name", "");
        args.putInt("quantity", -1);
        args.putFloat("price", -1);
        args.putString("imagefile", "");
        newFragment.setArguments(args);

        newFragment.show(getFragmentManager(), "Add Product");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        try {
            // User touched the dialog's positive button
            String name = args.getString("name");
            int quantity = args.getInt("quantity");
            float price = args.getFloat("price");
            String imagefile = args.getString("imagefile");
            Log.d("Main Activity", "Add product now...");

            ContentValues valuesInsert = new ContentValues();

            // Add the product to the database
            valuesInsert.put(ProductContract.ProductEntry.COLUMN_NAME_PROD_NAME, name);
            valuesInsert.put(ProductContract.ProductEntry.COLUMN_NAME_PROD_QUANTITY, quantity);
            valuesInsert.put(ProductContract.ProductEntry.COLUMN_NAME_PROD_PRICE, price);
            valuesInsert.put(ProductContract.ProductEntry.COLUMN_NAME_PROD_IMAGEPATH, imagefile);

            Uri uriInsert = getContentResolver().insert(ProductsProvider.CONTENT_URI, valuesInsert);

            // Notify adapter to refresh the listview so the new data is shown
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d("MainActivity", e.toString());
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        Log.d("Main Activity", "Dialog dismissed");
    }
}
