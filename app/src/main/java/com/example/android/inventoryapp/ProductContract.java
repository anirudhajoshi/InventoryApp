package com.example.android.inventoryapp;

import android.provider.BaseColumns;

/**
 * Created by anirudha.joshi on 7/13/2016.
 */
public final class ProductContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ProductContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class ProductEntry implements BaseColumns {
        public static final int DATABASE_VERSION = 5;
        public static final String DATABASE_NAME = "inventory";
        public static final String TABLE_NAME = "product";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_PROD_NAME = "Name";
        public static final String COLUMN_NAME_PROD_QUANTITY = "Quantity";
        public static final String COLUMN_NAME_PROD_PRICE = "Price";
        public static final String COLUMN_NAME_PROD_IMAGEPATH = "ImagePath";
    }
}
