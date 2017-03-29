package com.prepod.bchat.activities;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prepod.bchat.DividerItemDecoration;
import com.prepod.bchat.adapters.CustomFireBaseRecyclerAdapter;
import com.prepod.bchat.adapters.RoomAdapter;
import com.prepod.bchat.containers.Consts;
import com.prepod.bchat.containers.Message;
import com.prepod.bchat.containers.User;
import com.prepod.bchat.fragments.UsersListDialog;
import com.prepod.bchat.fragments.UsersListFragment;
import com.prepod.bchat.interfaces.OnRoomItemClick;
import com.prepod.bchat.R;

import org.w3c.dom.ls.LSException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoomActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CustomFireBaseRecyclerAdapter adapter;
    //private RoomAdapter adapter;
    private DatabaseReference roomReference;
    private DatabaseReference databaseReference;
    private String roomName;
    private EditText mMessageEditText;
    private FrameLayout mSendButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private String userAvatar;
    private String userName;

    private ArrayList<User> usersList = new ArrayList<>();
    private List<Message> messageList = new ArrayList<>();

    private  String sharedUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView backBtn = (ImageView) findViewById(R.id.back_btn);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setImageResource(R.mipmap.arrow);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoomActivity.super.onBackPressed();
            }
        });

        roomName = getIntent().getExtras().get("room").toString();
        sharedUrl = getIntent().getExtras().getString("sharedUrl");
        Log.i("My!", "Room in " + roomName);

        TextView titleTxt = (TextView) findViewById(R.id.textTitle);
        String roomTitle = getIntent().getExtras().get("title").toString();
        titleTxt.setText(roomTitle);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        userName = firebaseUser.getDisplayName();

        Log.i("My!", "User " + userName);
        if (firebaseUser.getPhotoUrl() != null) {
            userAvatar = firebaseUser.getPhotoUrl().toString();
        }

        recyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        roomReference = FirebaseDatabase.getInstance().getReference().child("messages").child(roomName);
        databaseReference = FirebaseDatabase.getInstance().getReference();


        databaseReference.child("rooms_members").child(roomName).child(firebaseUser.getUid()).setValue(true);

        databaseReference.child("rooms_members").child(roomName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("MY_APP", "" + dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listenUsers();

        adapter = new CustomFireBaseRecyclerAdapter<Message, MessageViewHolder>(Message.class,
                R.layout.item_message,
                MessageViewHolder.class,
                roomReference,
                null) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
                viewHolder.senderTxt.setText(model.getName());
                viewHolder.messageText.setText(model.getText());
                Log.i("My!", "Text: " + model.getText());
                final ImageView messageImg = viewHolder.msgImage;
                if (model.getUserAvavatar() != null) {
                    Glide.with(RoomActivity.this)
                            .load(model.getUserAvavatar())
                            .into(viewHolder.userAvatar);
                }
                if (!model.getImgUrl().equals("")) {
                    viewHolder.msgImage.setVisibility(View.VISIBLE);
                    Glide.with(RoomActivity.this)
                            .load(model.getImgUrl())
                            .override(500, 500)
                            .crossFade()
                            .fitCenter()
                            .into(viewHolder.msgImage);
                } else {
                    Glide.clear(viewHolder.msgImage);
                    viewHolder.msgImage.setImageDrawable(null);
                    //viewHolder.msgImage.setVisibility(View.INVISIBLE);
                }


            }
        };

//        adapter = new RoomAdapter(this, messageList);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = adapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });


        recyclerView.setAdapter(adapter);

//        roomReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                messageList.clear();
//                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
//                while (iter.hasNext()){
//                    DataSnapshot snapshot = iter.next();
//                    Message message = snapshot.getValue(Message.class);
//                    messageList.add(message);
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        if (sharedUrl != null) mMessageEditText.setText(sharedUrl);
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

        mSendButton = (FrameLayout) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mMessageEditText.getText().toString();
                Message message = new Message();
                message.setName(userName);
                message.setUserAvavatar(userAvatar);
                String imgUrl = parseImage(text);
                if (!imgUrl.equals("")){
                    message.setImgUrl(imgUrl);
                    String newText = text.replace(imgUrl, "");
                    message.setText(newText);
                } else {
                    message.setImgUrl("");
                    message.setText(text);
                }
                roomReference.push().setValue(message);
                mMessageEditText.setText("");

//                FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(), mUsername,
//                        mPhotoUrl, System.currentTimeMillis());
//                mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(friendlyMessage);
//                mMessageEditText.setText("");
//                mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
            }
        });

        updateUser(roomName);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.users_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.users_list:
                showUsersListFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUser(String room) {
        databaseReference.child("users").child(firebaseUser.getUid()).child("room").setValue(room);
        Log.i("My!", "User updated " + room);
    }

    private String parseImage(String url) {

        //String url = "hi there this is a URL String https://cs7060.userapi.com/c836525/v836525410/30fc2/Rs2GD47uIcQ.jpg";

        //String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url);
        String urlStr = "";
        while (m.find()) {
            urlStr = m.group();
            if (urlStr.contains(".jpg")
                    || urlStr.contains(".jpeg")
                    || urlStr.contains(".png")
                    || urlStr.contains(".gif")) {

                return urlStr;
            }
            if (!urlStr.equals("")) {
                return "";
            }
        }
        return "";
    }

    private void showUsersListFragment() {
       /* UsersListFragment fragment = UsersListFragment.newInstance(roomName);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(fragment, "userslist")
                .commit();*/
        /*Log.i("My!", "Show user list  " + roomName);
        Intent intent = new Intent(this, UsersListActivity.class);
        intent.putExtra(Consts.ROOM_NAME, roomName);
        startActivity(intent);*/

        UsersListDialog dialog = new UsersListDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("users", usersList);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "");

    }

    private void listenUsers() {

        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("My", "my");
                usersList.clear();
                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                while (iter.hasNext()) {
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


    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        public ImageView userAvatar;
        public ImageView msgImage;
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
            msgImage = (ImageView) itemView.findViewById(R.id.message_image);
            Log.i("My!", " " + msgImage);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.child("rooms_members").child(roomName).child(firebaseUser.getUid()).setValue(false);
        updateUser("");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        roomName = intent.getExtras().get("room").toString();
        sharedUrl = intent.getExtras().getString("sharedUrl");
        if (sharedUrl != null) {
            if (mMessageEditText != null) mMessageEditText.setText(sharedUrl);
        }
    }
}
