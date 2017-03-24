package com.prepod.bchat.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.prepod.bchat.R;
import com.prepod.bchat.adapters.UsersListAdapter;
import com.prepod.bchat.containers.User;

import java.util.ArrayList;

/**
 * Created by Антон on 07.03.2017.
 */

public class UsersListDialog extends DialogFragment {

    private ArrayList<User> usersList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            usersList = (ArrayList<User>) getArguments().getSerializable("users");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_users_list, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dialog_users_list_recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        UsersListAdapter adapter = new UsersListAdapter(getActivity(), usersList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        return builder.setView(view).create();
    }
}
