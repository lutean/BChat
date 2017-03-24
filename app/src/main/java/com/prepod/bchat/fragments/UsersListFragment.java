package com.prepod.bchat.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prepod.bchat.R;
import com.prepod.bchat.adapters.UsersListAdapter;
import com.prepod.bchat.containers.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class UsersListFragment extends Fragment {
    private static final String ROOM_NAME = "roomName";

    private RecyclerView recyclerView;
    private UsersListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private List<User> usersList = new ArrayList<>();

    private String roomName;

    private DatabaseReference databaseReference;

    public UsersListFragment() {
        // Required empty public constructor
    }

   public static UsersListFragment newInstance(String param1) {
        UsersListFragment fragment = new UsersListFragment();
        Bundle args = new Bundle();
        args.putString(ROOM_NAME, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomName = getArguments().getString(ROOM_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.users_list_recycler);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new UsersListAdapter(getActivity(), usersList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("My", "my");
                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                while (iter.hasNext()){
                    User user = iter.next().getValue(User.class);
                    if (user.getRoom().equals(roomName)) {
                        usersList.add(user);
                        Log.v("My", "my" + user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
