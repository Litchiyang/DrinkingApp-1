package urmc.drinkingapp.pages;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import urmc.drinkingapp.R;
import urmc.drinkingapp.model.User;
import urmc.drinkingapp.pages.Profile.ExpandedProfileActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class BuddyFragment extends Fragment {
    private static final String TAG = "BuddyFragment";
    //instance of the recylcer view
    private RecyclerView mRecyclerView;

    //private DrinkingAppCollection mCollection;
    private FirebaseRecyclerAdapter mAdapter;

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]
    private DatabaseReference mFriendReference;
    public DatabaseReference mUserReference;
    public DatabaseReference mBuddyReference;
    private StorageReference mUserStorageRef;


    public BuddyFragment() {

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

        View view = inflater.inflate(R.layout.fragment_buddy, container, false);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        //sets up the recycler view
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_buddy);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDatabase.child("users").child(getUid()).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    Log.d(TAG,dataSnapshot.toString());
                }else{
                    OnlineUpdateUI();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    //Get Current user's UID from the database
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Query getQuery() {
        // All my friends
        Query q = mDatabase.child("users").child(getUid()).child("friends");
        Log.d(TAG,"getQuery"+q.toString());
        return q;
    }

    //Get friends from the database and display them in the recyclerView
    public void OnlineUpdateUI(){

        showProgressDialog();
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(getQuery(), User.class)
                .build();
        mAdapter = new FirebaseRecyclerAdapter<User, BuddiesViewHolder>(options){
            @Override
            public BuddiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            protected void onBindViewHolder(BuddiesViewHolder holder, int position, User model) {

            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }


    public User mFriend;
    public User getUserFromSnapshot(DataSnapshot snapshot){
        String key = snapshot.getKey();
        // Initialize Database
        mUserReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(key);

        showProgressDialog();
        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get user value
                Log.d("PROFILE",dataSnapshot.toString());
                mFriend = dataSnapshot.getValue(User.class);
                hideProgressDialog();
                // [START_EXCLUDE]
                if (mFriend == null) {
                    // User is null, error out
                    Log.e("FriendsTAB", "User is unexpectedly null");
                    Toast.makeText(getActivity(),
                            "Error: could not fetch user.",
                            Toast.LENGTH_SHORT).show();
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("FriendsTAB", "getUser:onCancelled", databaseError.toException());
            }
        });
        return mFriend;
    }


    @Override
    public void onResume() {
        super.onResume();
        mDatabase.child("users").child(getUid()).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    Log.d("FRIENDS",dataSnapshot.toString());
                }else{
                    OnlineUpdateUI();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateView(){
        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]
        mDatabase.child("users").child(getUid()).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    Log.d("FRIENDS",dataSnapshot.toString());
                }else{
                    OnlineUpdateUI();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }





}
