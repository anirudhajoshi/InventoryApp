package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by anirudha.joshi on 7/14/2016.
 */

public class AddProduct_Dialog extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
    * implement this interface in order to receive event callbacks.
    * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);

        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    private NoticeDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setTitle(R.string.dialog_title);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_addproduct, null))
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    //@Override
                    public void onClick(DialogInterface dialog, int id) {

                        try {
                            //                        //Log.d("Dialog","Add Product");
                            Dialog d = (Dialog) dialog;

                            // Get user entered product quantity
                            String ErrorMsg = "";
                            boolean bError = false;
                            int quantity = -1;
                            float price = -1;

                            // Get user entered product name
                            EditText edittext_name = (EditText) d.findViewById(R.id.edittext_name);
                            String name = edittext_name.getText().toString();
                            if (name.equals("")) {     // Cannot have an empty product name
                                ErrorMsg += "\n" + getActivity().getString(R.string.emptyname);
                                bError = true;
                            }

                            EditText edittext_quantity = (EditText) d.findViewById(R.id.edittext_quantity);
                            try {
                                quantity = Integer.valueOf(edittext_quantity.getText().toString());
                            } catch (Exception e) {
                                ErrorMsg += "\n" + getActivity().getString(R.string.numbersonly);
                                bError = true;
                            }

                            // Get user entered product price
                            EditText edittext_price = (EditText) d.findViewById(R.id.edittext_price);
                            try {
                                price = Float.valueOf((edittext_price.getText().toString()));
                            } catch (Exception e) {
                                ErrorMsg += "\n" + getActivity().getString(R.string.floatsonly);
                                bError = true;
                            }

                            // Get user entered image path

                            EditText edittext_imagefile = (EditText) d.findViewById(R.id.edittext_imagefile);
                            String imagefile = edittext_imagefile.getText().toString();

                            if (bError)   // Only get valid values in the parent activity otherwise ignore
                                Toast.makeText(getActivity(), ErrorMsg, Toast.LENGTH_LONG).show();
                            else {
                                Bundle args = getArguments(); // Args passed in
                                args.putString("name", name);
                                args.putInt("quantity", quantity);
                                args.putFloat("price", price);
                                args.putString("imagefile", imagefile);

                                // Notify main activity so it can handle database inserts
                                mListener.onDialogPositiveClick(AddProduct_Dialog.this);
                            }
                        } catch (Exception e) {
                            Log.d("AddProduct_Dialog", e.toString());
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("Dialog", "Cancel");
                        mListener.onDialogNegativeClick(AddProduct_Dialog.this);
                        dismiss();
                    }
                });

        // 3. Get the AlertDialog from create()
        Bundle args = this.getArguments();

        return builder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the main activity
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
