package com.oluwafemi.medmanager.util;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by phemi-mint on 4/10/18.
 */

public class Utility {

    public static boolean fieldValidation(TextInputEditText[] fields, TextInputLayout[] allInputLayout){

        if (fields.length != allInputLayout.length) {
            try {
                throw new Exception("Size of EditText does not equal size of InputLayout");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for ( int i=0; i < fields.length; i++) {
            TextInputEditText currentField = fields[i];
            TextInputLayout currentLayout = allInputLayout[i];

            if (currentField.getText().toString().trim().length() <= 0) {
                currentField.setError("This field is required");
                addListenerToInputField(currentField, currentLayout);
                currentField.requestFocus();
                currentLayout.setErrorEnabled(true);
                currentLayout.setError("This field is required");
                return false;
            } else {
                currentLayout.setErrorEnabled(false);
                addListenerToInputField(currentField, currentLayout);
                currentLayout.setError(null);
                currentField.setError(null);
            }
        }
        return true;
    }

    // TextChangeListener to remove TextInputLayoutError
    private static void addListenerToInputField(TextInputEditText editText, final TextInputLayout layout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    layout.setErrorEnabled(false);
                } else
                    layout.setErrorEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    // format Date
    public static String formatRecyclerViewDate(Date mDate) {
        String newDate;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        newDate = sdf.format(mDate);
        return newDate;
    }
}
