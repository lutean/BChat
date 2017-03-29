package com.prepod.bchat.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.prepod.bchat.interfaces.OnAttachSourceSelectListener;

/**
 * Created by Антон on 28.11.2016.
 */

public class AttachDialog extends DialogFragment {

    private OnAttachSourceSelectListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String[] varik = {"pic from gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Attachment");

        builder.setItems(varik, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                mListener.onAttachSourceSelected(item);
            }
        });
        builder.setCancelable(true);
        builder.create();
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnAttachSourceSelectListener) activity;
        } catch (ClassCastException e) {

        }
    }


}
