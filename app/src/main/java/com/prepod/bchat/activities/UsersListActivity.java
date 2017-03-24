package com.prepod.bchat.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prepod.bchat.R;
import com.prepod.bchat.adapters.UsersListAdapter;
import com.prepod.bchat.containers.Consts;
import com.prepod.bchat.containers.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UsersListActivity extends AppCompatActivity {

   private RecyclerView recyclerView;
    private UsersListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private List<User> usersList = new ArrayList<>();

    private String roomName;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        if (getIntent().getExtras()!=null){
            roomName = getIntent().getExtras().getString(Consts.ROOM_NAME);
        }

        recyclerView = (RecyclerView) findViewById(R.id.users_list_recycler);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new UsersListAdapter(this, usersList);
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
