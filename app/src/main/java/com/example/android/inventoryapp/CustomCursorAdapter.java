package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Anirudha.Joshi on 7/15/2016.
 */


public class CustomCursorAdapter extends SimpleCursorAdapter {

    private Context context;

    public CustomCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context = context;
    }

    public View newView(Context _context, Cursor _cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(_context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context Context, Cursor cursor) {

        try {

            Button tracksales = (Button) view.findViewById(R.id.tracksales);
            Object prodid = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry._ID));
            tracksales.setTag(prodid);

            final String name = cursor.getString((cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME_PROD_NAME)));
            final String quantityStr = cursor.getString((cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME_PROD_QUANTITY)));
            final String priceStr = cursor.getString((cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME_PROD_PRICE)));
            final String imagefile = cursor.getString((cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME_PROD_IMAGEPATH)));

            TextView productname = (TextView) view.findViewById(R.id.productname);
            productname.setText(name);

            TextView productquantity = (TextView) view.findViewById(R.id.productquantity);
            productquantity.setText(quantityStr);

            TextView productprice = (TextView) view.findViewById(R.id.productprice);
            productprice.setText(priceStr);

            // Sales button handler
            tracksales.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (view != null) {
                        Object obj = view.getTag();
                        String row_id = obj.toString();

                        int quantity = Integer.parseInt(quantityStr);
                        int decremented_quantity = quantity - 1;
                        if (decremented_quantity < 0)
                            Toast.makeText(context,
                                    context.getResources().getString(R.string.negativequantity),
                                    Toast.LENGTH_LONG).show();
                        else {
                            // Update quantity
                            ContentValues valuesUpdate = new ContentValues();
                            valuesUpdate.put(ProductContract.ProductEntry.COLUMN_NAME_PROD_QUANTITY,
                                    String.valueOf(decremented_quantity));

                            // Defines selection criteria for the rows you want to update
                            String selection = ProductContract.ProductEntry._ID + " = ?";
                            String[] selectionArgs = {row_id};

                            int rowsUpdated = context.getContentResolver().update(ProductsProvider.CONTENT_URI,
                                    valuesUpdate,
                                    selection,
                                    selectionArgs);
                            Log.d("CustomAdapter", "rowUpdated: " + rowsUpdated);
                        }

                    } else
                        Log.d("CustomAdapter", "view is null");
                }
            });

        } catch (Exception e) {
            Log.d("CustomAdapter", e.toString());
        }
    }
}
