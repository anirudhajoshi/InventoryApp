package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ProductDetailsActivity extends AppCompatActivity {

    private String row_id = "";
    private String name = "";
    private String quantity = "";
    private String updatedquantity = "";
    private String price = "";
    private String imagefile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        setTitle(R.string.productdetails_activity);

        Bundle args = getIntent().getExtras(); // Args passed in
        row_id = args.getString("row_id");
        name = args.getString("productname");
        quantity = args.getString("productquantity");
        price = args.getString("productprice");

        TextView productdetails_productname = (TextView) findViewById(R.id.textview_productname);
        productdetails_productname.setText(name);

        TextView productdetails_productquantity = (TextView) findViewById(R.id.textview_quantity);
        productdetails_productquantity.setText(quantity);

        TextView productdetails_productprice = (TextView) findViewById(R.id.textview_price);
        productdetails_productprice.setText(price);

        imagefile = args.getString("imagefile");
        SetProductImage();
    }

    public void UpdateQuantity(View v) {
        try {
            EditText edittext_quantity = (EditText) findViewById(R.id.edittext_quantity);
            updatedquantity = edittext_quantity.getText().toString();

            try {
                int quantitytoupdate = Integer.parseInt(updatedquantity);
                if (quantitytoupdate <= 0) {
                    edittext_quantity.setText("");
                    Toast.makeText(ProductDetailsActivity.this, R.string.updatedquantitynegative,
                            Toast.LENGTH_LONG).show();
                    return;     // Do not execute database update
                }
            } catch (Exception e) {
                edittext_quantity.setText("");
                Toast.makeText(ProductDetailsActivity.this, R.string.updatedquantityerror,
                        Toast.LENGTH_LONG).show();
                Log.d("ProductDetailsActivity", e.toString());
                return;     // Do not execute database update
            }

            ContentValues valuesUpdate = new ContentValues();
            valuesUpdate.put(ProductContract.ProductEntry.COLUMN_NAME_PROD_QUANTITY, updatedquantity);

            // Defines selection criteria for the rows to update
            String selection = ProductContract.ProductEntry._ID + " = ?";
            String[] selectionArgs = {row_id};

            AsyncQueryHandler queryHandler = new CustomQueryHandler(getContentResolver());

            queryHandler.startUpdate(
                    1,      // token - user defined request code used with cancelOperation to
                            // cancel unprocessed requests submitted with this token

                    null,   // data container object that can be passed from the request
                            // to the response (callback) so that it can be used for some
                            // purpose like identifying the request if necessary
                    ProductsProvider.CONTENT_URI,
                    valuesUpdate,
                    selection,
                    selectionArgs);
        } catch (Exception e) {
            Log.d("ProductDetailsActivity", e.toString());
        }
    }

    public void DeleteProduct(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.deletedialogtitle);
        alert.setMessage(R.string.deletedialogmessage);
        alert.setPositiveButton(R.string.deletedialogconfirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    AsyncQueryHandler queryHandler = new CustomQueryHandler(getContentResolver());

                    // Defines selection criteria for the rows to delete
                    String selection = ProductContract.ProductEntry._ID + " = ?";
                    String[] selectionArgs = {row_id};

                    queryHandler.startDelete(
                            1,      // token - user defined request code used with cancelOperation to
                                    // cancel unprocessed requests submitted with this token
                            null,   // data container object that can be passed from the request
                                    // to the response (callback) so that it can be used for some
                                    // purpose like identifying the request if necessary
                            ProductsProvider.CONTENT_URI,
                            selection,
                            selectionArgs);
                } catch (Exception e) {
                    Log.d("ProductDetailsActivity", e.toString());
                }

                dialog.dismiss();
            }
        });

        alert.setNegativeButton(R.string.deletedialogcancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alert.show();
    }

    public void SendEmailToSupplier(View view) {
        try {

            String emailbody = R.string.productname_label + name + "\n" +
                    R.string.quantityrequested_label + quantity;
            Intent emailintent = new Intent(Intent.ACTION_SENDTO);
            emailintent.setData(Uri.parse("mailto:")); // only email apps should handle this
            emailintent.putExtra(Intent.EXTRA_EMAIL, "some email");
            emailintent.putExtra(Intent.EXTRA_SUBJECT, "some subject");
            emailintent.putExtra(android.content.Intent.EXTRA_TEXT, emailbody);

            if (emailintent.resolveActivity(getPackageManager()) != null) {
                startActivity(emailintent);
            }
        } catch (Exception e) {
            Log.d("ProductDetailsActivity", e.toString());
        }
    }

    public void SetProductImage() {
        if (imagefile == null || imagefile.equals("")) {
            Log.d("ProductDetailsActivity", "imagefile null or empty");
            Toast.makeText(ProductDetailsActivity.this, R.string.noimageprovided, Toast.LENGTH_LONG).show();
            Bitmap bitmapNoImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.noimage);
            ImageView img = (ImageView) findViewById(R.id.product_image);
            img.setImageBitmap(bitmapNoImage);
            return;
        }

        try {
            File f = new File("/data/data/com.example.android.inventoryapp/app_imageDir/", imagefile);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img = (ImageView) findViewById(R.id.product_image);
            img.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            // Image could not be set - set it back to default image
            Bitmap bitmapNoImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.noimage);
            ImageView img = (ImageView) findViewById(R.id.product_image);
            img.setImageBitmap(bitmapNoImage);
        }
    }

    // Query handler to class to execute queries on seperate threads
    private class CustomQueryHandler extends AsyncQueryHandler {

        public CustomQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            try {
                // query() completed
                if (cursor == null) {
                    // Some providers return null if an error occurs whereas others throw an exception
                    Log.d("ProductDetailsActivity", "OnQueryCompleted: Cursor is null");
                } else if (cursor.getCount() < 1) {
                    // No matches found
                    Log.d("ProductDetailsActivity", "OnQueryCompleted: count is < 1");
                } else {
                    cursor.moveToFirst();
                    final String updatedquantity = cursor.getString(cursor.getColumnIndex(
                            ProductContract.ProductEntry.COLUMN_NAME_PROD_QUANTITY));

                    // Refresh the UI
                    TextView textview_quantity = (TextView) findViewById(R.id.textview_quantity);
                    textview_quantity.setText(updatedquantity);

                    EditText edittext_quantity = (EditText) findViewById(R.id.edittext_quantity);
                    edittext_quantity.setText("");
                }
            } catch (Exception e) {
                Log.d("ProductDetailsActivity", e.toString());
            }
        }

        // Not used here
        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            // insert() completed
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {

            try {
                // update() completed
                Log.d("ProductDetailsActivity", "AsyncQueryHandler Update completed");
                EditText edittext_quantity = (EditText) findViewById(R.id.edittext_quantity);
                updatedquantity = edittext_quantity.getText().toString();

                ContentValues valuesUpdate = new ContentValues();
                valuesUpdate.put(ProductContract.ProductEntry.COLUMN_NAME_PROD_QUANTITY, updatedquantity);

                String[] projection =
                        {
                                ProductContract.ProductEntry.COLUMN_NAME_PROD_QUANTITY,
                        };

                // Defines selection criteria for the rows you want to update
                String selection = ProductContract.ProductEntry._ID + " = ?";
                String[] selectionArgs = {row_id};

                AsyncQueryHandler queryHandler = new CustomQueryHandler(getContentResolver());

                queryHandler.startQuery(
                        1,      // token - user defined request code used with cancelOperation to
                                // cancel unprocessed requests submitted with this token
                        null,   // data container object that can be passed from the request
                                // to the response (callback) so that it can be used for some
                                // purpose like identifying the request if necessary
                        ProductsProvider.CONTENT_URI,
                        projection,     // select
                        selection,      // where
                        selectionArgs,  // where values
                        null            // no sort
                );
            } catch (Exception e) {
                Log.d("ProductDetailsActivity", e.toString());
            }
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {

            try {
                Toast.makeText(getApplicationContext(), R.string.productdeleted, Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception e) {
                Log.d("ProductDetailsActivity", e.toString());
            }
        }
    }
}
