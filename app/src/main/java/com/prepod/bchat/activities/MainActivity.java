package com.prepod.bchat.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prepod.bchat.DividerItemDecoration;
import com.prepod.bchat.containers.Consts;
import com.prepod.bchat.adapters.CustomFireBaseRecyclerAdapter;
import com.prepod.bchat.interfaces.OnAddRoom;
import com.prepod.bchat.interfaces.OnRoomItemClick;
import com.prepod.bchat.R;
import com.prepod.bchat.containers.Room;
import com.prepod.bchat.containers.User;
import com.prepod.bchat.fragments.AddRoomDialog;

import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnAddRoom {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private GoogleApiClient googleApiClient;
    private DatabaseReference firebaseDatabaseRefer;
    private CustomFireBaseRecyclerAdapter<Room, RoomsViewHolder> firebaseRecyclerAdapter;
    private String userName;
    private String userAvatar = "";
    private boolean isAdmin;
    private User user;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private CircleImageView navUserAvatar;
    private TextView navUserName;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton fab;

    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddRoomDialog dialog = new AddRoomDialog();
                dialog.show(getFragmentManager(), "");
            }
        });

          final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ImageView backBtn = (ImageView) findViewById(R.id.back_btn);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setImageResource(R.drawable.ic_menu_white_24dp);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               drawer.openDrawer(Gravity.LEFT);
            }
        });
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);

        navUserAvatar = (CircleImageView) headerLayout.findViewById(R.id.navUserAvatar);
        navUserName = (TextView) headerLayout.findViewById(R.id.navUserName);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.roomListCoordinator);

        recyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);

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
            navUserName.setText(userName);
            if (firebaseUser.getPhotoUrl() != null){
                userAvatar = firebaseUser.getPhotoUrl().toString();
                Glide.with(MainActivity.this)
                        .load(userAvatar)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                                //setNavColor();
//                                Log.v("", "");
//                                Drawable d =(Drawable) resource;
//                                BitmapDrawable b = (BitmapDrawable) d;
//                                Bitmap bitmap = b.getBitmap();
//                                setNavColor(bitmap);
                                return false;
                            }
                        })
                        .into(navUserAvatar);
            }
        }

        Log.i("My!", "User logined " + userName);
            firebaseDatabaseRefer.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    if (user != null && user.isAdmin())
                        fab.setVisibility(View.VISIBLE);
                    updateUser();
                    Log.i("", "" + user);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.v("", "");
                }
            });


        //mDatabase.child("users").child(userId).child("username").setValue(name);


        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //linearLayoutManager.setStackFromEnd(true);

        firebaseRecyclerAdapter = new CustomFireBaseRecyclerAdapter<Room, RoomsViewHolder>(
                Room.class,
                R.layout.item_room,
                RoomsViewHolder.class,
                firebaseDatabaseRefer.child(Consts.ROOMS_CHILD),
                new OnRoomItemClick() {
                    @Override
                    public void onClick(int position) {
                       // Log.v("My" , "");
                        Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                        intent.putExtra("room", firebaseRecyclerAdapter.getRef(position).getKey());
                        intent.putExtra("title", firebaseRecyclerAdapter.getItem(position).getTitle());
                        Log.i("My!", "Room selected " + firebaseRecyclerAdapter.getItem(position).getTitle());
                        startActivity(intent);
                    }
                }
        ) {
            @Override
            protected void populateViewHolder(RoomsViewHolder viewHolder, Room model, int position) {
                viewHolder.titleTxt.setText(model.getTitle());
                viewHolder.lastMessage.setText(model.getAbout());
               /* if (model.getUserAvavatar() != null){
                    Glide.with(MainActivity.this)
                            .load(model.getUserAvavatar())
                            .into(viewHolder.userAvatar);
                }*/
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
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
//        messageTxt.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence.toString().trim().length() > 0){
//                    sendBtn.setEnabled(true);
//                } else {
//                    sendBtn.setEnabled(false);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//
//        sendBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Message message = new Message();
//                message.setName(userName);
//                message.setText(messageTxt.getText().toString());
//                message.setUserAvavatar(userAvatar);
//                firebaseDatabaseRefer.child(Consts.MESSAGES_CHILD)
//                        .push().setValue(message);
//                messageTxt.setText("");
//            }
//        });

        firebaseDatabaseRefer.child(Consts.MESSAGES_CHILD).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("My", "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void updateUser(){
        User updateUser = new User(userName, firebaseUser.getEmail(), userAvatar);
        if (this.user != null) {
            updateUser.setAdmin(this.user.isAdmin());
        } else {
            updateUser.setAdmin(false);
        }
        updateUser.setRoom("");
        Log.i("My!", "User updated " + updateUser.getName());
        firebaseDatabaseRefer.child("users").child(firebaseUser.getUid()).setValue(updateUser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.log_out:
//                firebaseAuth.signOut();
//                Auth.GoogleSignInApi.signOut(googleApiClient);
//                firebaseAuth = null;
//                userName = null;
//                userAvatar = null;
//                startActivity(new Intent(this, AuthActivity.class));
//                finish();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut(){
        firebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient);
        firebaseAuth = null;
        userName = null;
        userAvatar = null;
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_logout:
                logOut();
                break;
        }
        return false;
    }

    @Override
    public void onAdd(String name, String desc) {
        Room room = new Room();
        room.setTitle(name);
        room.setAbout(desc);
        room.setTimeStamp(System.currentTimeMillis());
        firebaseDatabaseRefer.child(Consts.ROOMS_CHILD).push().setValue(room);
    }

    private void setNavColor(Bitmap bitmap) {
        Palette p = createPaletteSync(bitmap);
        Palette.Swatch vibrantSwatch = checkVibrantSwatch(p);

        LinearLayout navHeader = (LinearLayout) findViewById(R.id.nav_header);
        navHeader.setBackgroundColor(vibrantSwatch.getRgb());
        //toolbar.setTitleTextColor(vibrantSwatch.getTitleTextColor());
    }

    private Palette.Swatch checkVibrantSwatch(Palette p) {
        Palette.Swatch vibrant = p.getVibrantSwatch();
        if (vibrant != null) {
            return vibrant;
        }
        return null;
    }

    private Palette createPaletteSync(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        return p;
    }

    public static class RoomsViewHolder extends RecyclerView.ViewHolder{

        public ImageView userAvatar;
        public TextView titleTxt;
        public TextView lastMessage;
        public TextView lastMessageDate;
        public LinearLayout itemRoomLayout;
        private OnRoomItemClick lisener;

        public RoomsViewHolder(View itemView, final OnRoomItemClick listener) {
            super(itemView);
            this.lisener = listener;
            titleTxt = (TextView) itemView.findViewById(R.id.roomTitleTextView);
            lastMessage = (TextView) itemView.findViewById(R.id.lastMessageTextView);
            itemRoomLayout = (LinearLayout) itemView.findViewById(R.id.itemRoom);
            itemRoomLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(getAdapterPosition());
                }
            });
        }
    }
}
