package com.prepod.bchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.prepod.bchat.Consts.MESSAGES_CHILD;

public class RoomActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CustomFireBaseRecyclerAdapter adapter;
    private DatabaseReference roomReference;
    private String roomName;
    private EditText mMessageEditText;
    private Button mSendButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private String userAvatar;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        roomName = getIntent().getExtras().get("room").toString();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        userName = firebaseUser.getDisplayName();
        if (firebaseUser.getPhotoUrl() != null){
            userAvatar = firebaseUser.getPhotoUrl().toString();
        }

        recyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        roomReference = FirebaseDatabase.getInstance().getReference().child("messages").child(roomName);

        adapter = new CustomFireBaseRecyclerAdapter<Message, MessageViewHolder>(Message.class,
                R.layout.item_message,
                MessageViewHolder.class,
                roomReference,
                null)
        {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
                viewHolder.senderTxt.setText(model.getName());
                viewHolder.messageText.setText(model.getText());
                if (model.getUserAvavatar() != null){
                    Glide.with(RoomActivity.this)
                            .load(model.getUserAvavatar())
                            .into(viewHolder.userAvatar);
                }
            }
        };

        recyclerView.setAdapter(adapter);

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
//        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
//                .getInt(CodelabPreferences.FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message(userName, userAvatar, mMessageEditText.getText().toString());
                roomReference.push().setValue(message);
                mMessageEditText.setText("");

//                FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(), mUsername,
//                        mPhotoUrl, System.currentTimeMillis());
//                mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(friendlyMessage);
//                mMessageEditText.setText("");
//                mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
            }
        });

    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{

        public ImageView userAvatar;
        public TextView senderTxt;
        public TextView messageText;
        public TextView lastMessageDate;
        public LinearLayout itemRoomLayout;
        private OnRoomItemClick lisener;

        public MessageViewHolder(View itemView, final OnRoomItemClick listener) {
            super(itemView);
            this.lisener = listener;
            senderTxt = (TextView) itemView.findViewById(R.id.senderTextView);
            messageText = (TextView) itemView.findViewById(R.id.messageTextView);
            userAvatar = (ImageView) itemView.findViewById(R.id.userAvatar);
        }
    }
}
