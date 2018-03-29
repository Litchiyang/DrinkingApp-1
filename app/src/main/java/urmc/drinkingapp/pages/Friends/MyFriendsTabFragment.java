package urmc.drinkingapp.pages.Friends;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import urmc.drinkingapp.R;
import urmc.drinkingapp.control.FirebaseDAO;
import urmc.drinkingapp.control.Utils;
import urmc.drinkingapp.model.Friend;
import urmc.drinkingapp.model.User;
import urmc.drinkingapp.pages.Profile.ExpandedProfileActivity;
import urmc.drinkingapp.pages.FriendsViewHolder;


/**
 * Fragment displaying list of user's friends. Accessed when clicking the friends tab on the ViewPager
 */
public class MyFriendsTabFragment extends Fragment {

    private final static String TAG = "MyFriendsTabFragment";

    //instance of the recylcer view
    private RecyclerView mRecyclerView;

    private DatabaseReference mUserDatabase;

    //private DrinkingAppCollection mCollection;
    private FirebaseRecyclerAdapter mAdapter;

    private DatabaseReference mFriendReference;
    public DatabaseReference mUserReference;
    public DatabaseReference mBuddyReference;
    private StorageReference mUserStorageRef;
    private FirebaseDAO dao;


    public MyFriendsTabFragment() {
        // Required empty public constructor
    }

    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("Loading...");
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_friends_tab, container, false);

        // [START initialize_database_ref]
        dao = new FirebaseDAO();
        mUserDatabase = dao.getUserDatabase();
        mFriendReference = dao.getFriendsDatabase();
        // [END initialize_database_ref]

        //sets up the recycler view
        mRecyclerView = view.findViewById(R.id.recycler_view_my_friends);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        Log.d(TAG,"Creating FirebaseRecyclerOptions");
        //getting the list of friends
        DatabaseReference keyQuery = mFriendReference.child(getUid());
        Log.d(TAG,"check keyQuery is null:" + (keyQuery==null));
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<User>()
                .setIndexedQuery(keyQuery,mUserDatabase,User.class).build();

        mAdapter = new FirebaseRecyclerAdapter<User, FriendsViewHolder>(options) {
            @Override
            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friends_view_holder_add_buddy, parent, false);
                return new FriendsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final FriendsViewHolder holder, int position, final User model) {
                Log.d(TAG,"WTF");
                if(!model.getProfilePic().equals("none")){
                    Log.d(TAG,"profile pic location: "+model.getProfilePic());
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    Glide.with(getActivity()).load(model.getProfilePic()).into(holder.mProfilePic);
//                    storageRef.child(model.getProfilePic()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            Log.d(TAG,"load img for " + model.getLastname() + model.getFirstname()+" with location " + model.getProfilePic());
//                            Glide.with(getActivity()).load(uri).into(holder.mProfilePic);
//                            // TODO: handle uri
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            // Handle any errors
//                        }
//                    });


                }

                holder.bindUser(model);
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
        return view;
    }

    private String getUid(){
        return Utils.getUid();
    }

    public Query getFriendsQuery() {
        //https://stackoverflow.com/questions/41135658/how-to-perform-join-query-in-firebase
        //Query used to find list
        ArrayList<User> friends;
        final Query resultQuery;
        String selfId = getUid();
        Query q = dao.getUserDatabase();
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dao.getFriendsDatabase().child(getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){



                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.d(TAG,"QUERY "+q.toString());
        return q;
    }

    /*
    * For full documentation see:
    * https://firebaseui.com/docs/android/com/firebase/ui/FirebaseRecyclerViewAdapter.html
    * Get the users friends from the friends table,
    * then search all the friends by id in the user table
    * */
    public void OnlineUpdateUI(){
        showProgressDialog();
        Log.d(TAG,"OnlineUpdateUI()");
        // TODO: 2017/11/6 clean up this part
        hideProgressDialog();

/*
        mAdapter = new FirebaseRecyclerAdapter<User, FriendsViewHolder>(User.class, R.layout.friends_view_holder_add_buddy, FriendsViewHolder.class, getFriendsQuery()) {
            @Override
            public void populateViewHolder(FriendsViewHolder FriendProfileHolder, User user, int position) {
//                //FriendProfileHolder.bindUser(user);
                Log.d(TAG,"OnlineUpdateUI() -> populateViewHolder()");
                hideProgressDialog();


                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                //Log.d("FrList",postKey);

                final FriendsViewHolder myView = FriendProfileHolder;

                mBuddyReference = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(Utils.getUid()).child("buddy");

                mFriendReference = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(Utils.getUid()).child("friends").child(postKey);
                mUserStorageRef = FirebaseStorage.getInstance().getReference().child(postKey);
                showProgressDialog();
                mFriendReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Log.d("List Dis BUTTON",dataSnapshot.toString());
                        if (dataSnapshot.getValue()==null){
                            //mAreWeFriends = false;
                        }else {
                            //mAreWeFriends = dataSnapshot.getValue();
                            if (dataSnapshot.getValue(Boolean.class)){
                                myView.mAddFriendButton.setText("-");
                                myView.mAddFriendButton.setBackgroundColor(Color.parseColor("#ffffff"));
                                myView.mAddFriendButton.setTextColor(Color.parseColor("#000000"));
                            }

                            FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    if (user == null) {
                                        // User is null, error out
                                        Log.e(TAG,"FriendsTAB User is unexpectedly null");
                                        Toast.makeText(getActivity(),
                                                "Error: could not fetch user.",
                                                Toast.LENGTH_SHORT).show();
                                    }else{
                                        myView.bindUser(user);
                                        if(!user.getProfilePic().equals("none")) {
                                            Glide.with(getActivity() )
                                                    .using(new FirebaseImageLoader())
                                                    .load(mUserStorageRef)
                                                    .into(myView.mProfilePic);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w("FriendsTAB", "getUser:onCancelled", databaseError.toException());
                                }
                            });


                            mBuddyReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue()==null){
                                        //mAreWeFriends = false;
                                    }else {
                                        //mAreWeFriends = dataSnapshot.getValue(Boolean.class);
                                        if (dataSnapshot.getValue(String.class).equals(postKey)) {
                                            myView.mAddBuddyButton.setText("Buddy down");
                                            myView.mAddBuddyButton.setBackgroundColor(Color.parseColor("#ffffff"));
                                            myView.mAddBuddyButton.setTextColor(Color.parseColor("#000000"));
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        hideProgressDialog();
                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("IsFrList", "getUser:onCancelled", databaseError.toException());
                    }
                });

                FriendProfileHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(getActivity(), ExpandedProfileActivity.class);
                        intent.putExtra("KEY", postKey);
                        startActivity(intent);
                    }
                });

                FriendProfileHolder.mAddFriendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFriendReference = FirebaseDatabase.getInstance().getReference()
                                .child("users").child(Utils.getUid()).child("friends").child(postKey);
                        showProgressDialog();
                        mFriendReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Get user value
                                Log.d("List Dis BUTTON",dataSnapshot.toString());
                                if (dataSnapshot.getValue()==null){
                                    mDatabase.child("users").child(getUid()).child("friends").child(postKey).setValue(true);
                                    mDatabase.child("users").child(postKey).child("friends").child(getUid()).setValue(true);
                                    myView.mAddFriendButton.setText("-");
                                    myView.mAddFriendButton.setBackgroundColor(Color.parseColor("#ffffff"));
                                    myView.mAddFriendButton.setTextColor(Color.parseColor("#000000"));
                                }else {
                                    //mAreWeFriends = dataSnapshot.getValue(Boolean.class);
                                    if (dataSnapshot.getValue(Boolean.class)){
                                        //mDatabase.child("users").child(getUid()).child("friends").child(postKey).setValue(false);
                                        mDatabase.child("users").child(getUid()).child("friends").child(postKey).removeValue();
                                        mDatabase.child("users").child(postKey).child("friends").child(getUid()).removeValue();
                                        //TODO If friend being removed is also a buddy have to update this (or not?)
                                        myView.mAddFriendButton.setText("+");
                                        myView.mAddFriendButton.setBackgroundColor(Color.parseColor("#ff5a5f"));
                                        myView.mAddFriendButton.setTextColor(Color.parseColor("#ffffff"));
                                    }else{
                                        mDatabase.child("users").child(getUid()).child("friends").child(postKey).setValue(true);
                                        mDatabase.child("users").child(postKey).child("friends").child(getUid()).setValue(true);
                                        myView.mAddFriendButton.setText("-");
                                        myView.mAddFriendButton.setBackgroundColor(Color.parseColor("#ffffff"));
                                        myView.mAddFriendButton.setTextColor(Color.parseColor("#000000"));
                                    }
                                }
                                hideProgressDialog();
                                // [START_EXCLUDE]
                                // [END_EXCLUDE]
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("IsFrList", "getUser:onCancelled", databaseError.toException());
                            }
                        });
                    }
                });

                //this part will throw nullpointer error
                FriendProfileHolder.mAddBuddyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBuddyReference = FirebaseDatabase.getInstance().getReference()
                                .child("users").child(getUid()).child("buddy");
                        showProgressDialog();
                        mBuddyReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Get user value
                                Log.d("List Buddies BUTTON",dataSnapshot.toString());
                                if (dataSnapshot.getValue()==null){
                                    mDatabase.child("users").child(getUid()).child("buddy").setValue(postKey);
                                    mDatabase.child("users").child(postKey).child("buddy").setValue(getUid());
                                    myView.mAddBuddyButton.setText("Buddy down");
                                    myView.mAddBuddyButton.setBackgroundColor(Color.parseColor("#ffffff"));
                                    myView.mAddBuddyButton.setTextColor(Color.parseColor("#000000"));
                                }else {
                                    //mAreWeFriends = dataSnapshot.getValue(Boolean.class);
                                    if (dataSnapshot.getValue(String.class).equals(postKey)){
                                        //mDatabase.child("users").child(getUid()).child("friends").child(postKey).setValue(false);
                                        mDatabase.child("users").child(getUid()).child("buddy").removeValue();
                                        mDatabase.child("users").child(postKey).child("buddy").removeValue();
                                        myView.mAddBuddyButton.setText("Buddy up");
                                        myView.mAddBuddyButton.setBackgroundColor(Color.parseColor("#ff5a5f"));
                                        myView.mAddBuddyButton.setTextColor(Color.parseColor("#ffffff"));
                                    }else{
                                        mDatabase.child("users").child(dataSnapshot.getValue(String.class)).child("buddy").removeValue();
                                        mDatabase.child("users").child(getUid()).child("buddy").setValue(postKey);
                                        mDatabase.child("users").child(postKey).child("buddy").setValue(getUid());
                                        myView.mAddBuddyButton.setText("Buddy down");
                                        myView.mAddBuddyButton.setBackgroundColor(Color.parseColor("#ffffff"));
                                        myView.mAddBuddyButton.setTextColor(Color.parseColor("#000000"));
                                        OnlineUpdateUI();
                                    }
                                }
                                hideProgressDialog();
                                // [START_EXCLUDE]
                                // [END_EXCLUDE]
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("IsFrList", "getUser:onCancelled", databaseError.toException());
                            }
                        });
                    }
                });
            }
        };
        */

//        mRecyclerView.setAdapter(mAdapter);

    }


    @Override
    public void onResume() {
        super.onResume();
    }

}
