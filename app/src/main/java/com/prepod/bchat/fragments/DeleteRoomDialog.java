package com.prepod.bchat.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.prepod.bchat.R;
import com.prepod.bchat.interfaces.OnRoomManipulation;

/**
 * Created by Антон on 12.05.2017.
 */

public class DeleteRoomDialog extends DialogFragment {

        private OnRoomManipulation listener;


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.fragment_add, null);
            final EditText name = (EditText) view.findViewById(R.id.add_room_name);
            final EditText desc = (EditText) view.findViewById(R.id.add_room_description);
            builder.setTitle("Delete this room?")
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            listener.onDelete();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            com.prepod.bchat.fragments.DeleteRoomDialog.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            try {
                listener = (OnRoomManipulation) activity;
            } catch (ClassCastException e){

            }
        }
    }


