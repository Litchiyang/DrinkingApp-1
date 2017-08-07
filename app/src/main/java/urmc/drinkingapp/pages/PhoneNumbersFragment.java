package urmc.drinkingapp.pages;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.*;

import com.bumptech.glide.Glide;
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


import java.util.List;

import urmc.drinkingapp.database.PhoneNumberCollection;
import urmc.drinkingapp.model.PhoneNumbers;
import urmc.drinkingapp.model.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneNumbersFragment extends Fragment {

    public RecyclerView mRecyclerView;

    //private DrinkingAppCollection mCollection;
    public FirebaseRecyclerAdapter mAdapter;

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]
  //  private DatabaseReference mFriendReference;
    public DatabaseReference mUserReference;
    public DatabaseReference mPhoneNumberReference;
    public DatabaseReference mPresetPhoneReference;
    private StorageReference mUserStorageRef;



    public PhoneNumbersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_phone_numbers, container, false);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        //sets up the recycler view
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_phone_numbers);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDatabase.child("users").child(getUid()).child("phoneNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    Log.d("PHONE NUMBER", dataSnapshot.toString());
                }else{
                    OnlineUpdateUI();
                    Log.d("PHONE NUMBER2", dataSnapshot.toString());
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

    public Query getQuery(DatabaseReference databaseReference) {
        // All my friends
        Query q = databaseReference.child("users").child(getUid()).child("phoneNumber");
        Log.d("QUERY",q.toString());
        return q;
    }

    //Get friends from the database and display them in the recyclerView
    public void OnlineUpdateUI(){

        Log.d("TEST2", "222");
        mAdapter = new FirebaseRecyclerAdapter<User, PhoneNumberViewHolder>(User.class, R.layout.view_phone_numbers, PhoneNumberViewHolder.class, getQuery(mDatabase)) {
            @Override
            public void populateViewHolder(PhoneNumberViewHolder PhoneNumberHolder, User user, int position) {
                //FriendProfileHolder.bindUser(user);
                Log.d("LOAD NUMBER","Going out settings");

                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                //Log.d("FrList",postKey);

                final PhoneNumberViewHolder myView = PhoneNumberHolder;
                mPhoneNumberReference = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(getUid()).child("phoneNumber").child(postKey);

                mPhoneNumberReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Log.d("List phonenumber BUTTON", dataSnapshot.toString());
                        if (dataSnapshot.getValue() == null) {

                        } else {

                            myView.itemView.setBackgroundColor(Color.parseColor("#ffffff"));

                        }
                    }
//                    @Override
//                    public void onClick(View v) {
//                        mPhoneNumberReference = FirebaseDatabase.getInstance().getReference()
//                                .child("users").child(getUid()).child("phoneNumber").child(postKey);
//
//                        mPhoneNumberReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                Log.d("List phone number", dataSnapshot.toString());
//
//                                if (dataSnapshot.getValue()==null){
//                                    mDatabase.child("users").child(getUid()).child("presetPhone").child(postKey)
//                                            .setValue(myView.mPhoneNumber.toString());
//                                    myView.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
//                                }else {
//                                    if (dataSnapshot.getValue(Boolean.class)){
//                                        mDatabase.child("users").child(getUid()).child("friends").child(postKey).removeValue();
//                                        mDatabase.child("users").child(postKey).child("friends").child(getUid()).removeValue();
//                                        myView.itemView.setBackgroundColor(Color.parseColor("#ff5a5f"));
//                                    }else{
//                                        mDatabase.child("users").child(getUid()).child("friends").child(postKey).setValue(true);
//                                        mDatabase.child("users").child(postKey).child("friends").child(getUid()).setValue(true);
//                                        myView.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//                                Log.w("IsFrList", "getUser:onCancelled", databaseError.toException());
//                            }
//                        });
//
//
//                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("IsFrList", "getUser:onCancelled", databaseError.toException());
                    }
                });



            }
        };
        mRecyclerView.setAdapter(mAdapter);
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
