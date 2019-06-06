package com.example.george.digitalmenu.restaurant;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

public class SignInFailureFragment extends DialogFragment {

    private final static String dialogMessage = "Log In Failed";
    private final static String positiveButtonText = "OK";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(dialogMessage)
                .setPositiveButton(positiveButtonText, (dialog, id) -> {
                    // FIRE ZE MISSILES!
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
