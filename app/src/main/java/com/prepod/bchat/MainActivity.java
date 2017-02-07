package com.prepod.bchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private GoogleApiClient googleApiClient;
    private DatabaseReference firebaseDatabaseRefer;
    private FirebaseRecyclerAdapter<Message, MessageViewHolder> firebaseRecyclerAdapter;
    private String userName;
    private String userAvatar;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private Button sendBtn;
    private EditText messageTxt;

    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        sendBtn = (Button) findViewById(R.id.sendButton);
        messageTxt = (EditText) findViewById(R.id.messageEditText);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabaseRefer = FirebaseDatabase.getInstance().getReference();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e("My", "Failed");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        if (firebaseUser == null){
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        } else {
            userName = firebaseUser.getDisplayName();
            if (firebaseUser.getPhotoUrl() != null){
                userAvatar = firebaseUser.getPhotoUrl().toString();
            }
        }

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.item_message,
                MessageViewHolder.class,
                firebaseDatabaseRefer.child(Consts.MESSAGES_CHILD)
        ) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
                viewHolder.messangerTxt.setText(model.getName());
                viewHolder.messageTxt.setText(model.getText());
                if (model.getUserAvavatar() != null){
                    Glide.with(MainActivity.this)
                            .load(model.getUserAvavatar())
                            .into(viewHolder.userAvatar);
                }
            }
        };

        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messageCount = firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePos = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (lastVisiblePos == -1 ||
                        (positionStart >= (messageCount - 1) &&
                        lastVisiblePos == (positionStart - 1))){

                }
            }
        });

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        messageTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0){
                    sendBtn.setEnabled(true);
                } else {
                    sendBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message();
                message.setName(userName);
                message.setText(messageTxt.getText().toString());
                message.setUserAvavatar(userAvatar);
                firebaseDatabaseRefer.child(Consts.MESSAGES_CHILD)
                        .push().setValue(message);
                messageTxt.setText("");
            }
        });

        firebaseDatabaseRefer.child(Consts.MESSAGES_CHILD).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("My", "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                firebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(googleApiClient);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{

        public ImageView userAvatar;
        public TextView messageTxt;
        public TextView messangerTxt;

        public MessageViewHolder(View itemView) {
            super(itemView);

            userAvatar = (ImageView) itemView.findViewById(R.id.userAvatar);
            messageTxt = (TextView) itemView.findViewById(R.id.messageTextView);
            messangerTxt = (TextView) itemView.findViewById(R.id.messengerTextView);
        }
    }
}
