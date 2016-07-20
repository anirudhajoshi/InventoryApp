package com.example.android.inventoryapp;

/**
 * Created by anirudha.joshi on 7/13/2016.
 */
public class Product {

    private String mName;
    private int mQuantity;
    private float mPrice;
    private String mImagePath;

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int mQuantity) {
        this.mQuantity = mQuantity;
    }

    public float getPrice() {
        return mPrice;
    }

    public void setPrice(float mPrice) {
        this.mPrice = mPrice;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
    }

    @Override
    public String toString() {
        return "Product [Name=" + getName() + ", Quantity=" + getQuantity() + ", Price=" + getPrice() + ", Imagepath=" + getImagePath() + "]";
    }
}
