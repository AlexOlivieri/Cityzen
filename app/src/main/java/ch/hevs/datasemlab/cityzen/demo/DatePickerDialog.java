package ch.hevs.datasemlab.cityzen.demo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.DatePicker;

import java.util.ArrayList;

/**
 * Created by Alex on 10/28/2016.
 */

public class DatePickerDialog extends DialogFragment {

    private Context myContext;

    public void setMyContext(Context context){
        this.myContext = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//        // Use the Builder class for convenient dialog construction
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setMessage(R.string.button_go)
//                .setPositiveButton(R.string.button_go, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // FIRE ZE MISSILES!
//                    }
//                })
//                .setNegativeButton(R.string.button_go, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
//
//        // Create the AlertDialog object and return it
//        return builder.create();

        final ArrayList mSelectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        DatePicker picker = new DatePicker(this.myContext);

        builder.setTitle("Choose Year");
        builder.setView(picker);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Set", null);

        return builder.create();
    }
}