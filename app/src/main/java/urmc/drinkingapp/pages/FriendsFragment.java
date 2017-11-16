package urmc.drinkingapp.pages;


import android.app.ProgressDialog;
import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
 * Friends fragment displays a list of the user's friends. It is used to display the resulting users when a search is performed.
 * It is not currently being used to display the user's friends because the MyFriendsTabFragment is being used. However, this fragment
 * continues to be used to display the results of a search for a user
 */

public class FriendsFragment extends Fragment {

    private final static String TAG = "FriendsFragment";

    //instance of the recylcer view
    private RecyclerView mRecyclerView;
    //private DrinkingAppCollection mCollection;
    private FirebaseRecyclerAdapter mAdapter;
    private String mQuery;
    private Context mContext;
    private DatabaseReference mDatabase;

    //Reference to the user's friends
    private DatabaseReference mFriendReference;

    //reference to the database storage to load the profile pictures of the different users
    private StorageReference mUserStorageRef;
    private boolean mAreWeFriends;
    //private NoResultsProcess mListener;

    public FriendsFragment() {
        // Required empty public constructor
    }

    public interface NoResultsProcess{
        void NoResultStarted();
    }

    //Process dialog to be displayed while the app loads the user's friends
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
        View view = inflater.inflate(R.layout.fragment_friends, container, false);


        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]


        mContext = getActivity();

        //gets the database collection - OFFLINE DATABASE
        //mCollection = DrinkingAppCollection.get(mContext);

        //sets up the recycler view
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_friends);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //getting arguments being passed - Used when we have to display the results of a search
        Bundle args = getArguments();
        if(args!=null){
            mQuery = args.getString("QUERY");
            Log.d(TAG,mQuery);
        }else {
            OnlineUpdateUI();
        }

        return view;
    }

    public Query getQuery(DatabaseReference databaseReference) {
        // All my users

        Query q = databaseReference.child("users");
        Log.d("QUERY",q.toString());
        return q;
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void OnlineUpdateUI(){
//        mAdapter = new FirebaseRecyclerAdapter<User, FriendsViewHolder>(User.class, R.layout.friends_view_holder, FriendsViewHolder.class, getQuery(mDatabase)) {
//            @Override
//            public void populateViewHolder(FriendsViewHolder FriendProfileHolder, User user, int position) {
//                FriendProfileHolder.bindUser(user);
//            }
//        };
//        mRecyclerView.setAdapter(mAdapter);
    }

    //Perform search on the database using the queryText
    public Query doMySearch(String queryText){
        return mDatabase.child("users")
                .orderByChild("fullname")
                .startAt(queryText)
                .endAt(queryText+"\uf8ff");

    }

    /*
    //sets the adapter and updates the UI
    public void UpdateUI(){
        mCollection = DrinkingAppCollection.get(getActivity());
        List<User> users = mCollection.getAllUsersButMyself(mCollection.mMainUser.getEmail());
        if (mAdapter == null){
            mAdapter = new FriendsAdapter(users);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setUsers(users);
            mAdapter.notifyDataSetChanged();
        }

    }

    //sets the adapter and updates the UI
    public void UpdateUI(String Query){
        mCollection = DrinkingAppCollection.get(getActivity());
        List<User> users = mCollection.getAllUsersBasedOnQuery(Query);
        if (users.isEmpty()){
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            FragmentManager manager = getFragmentManager();
            NoResultsDialog dialog = NoResultsDialog.newInstance();
            dialog.show(manager, "NoResultsDialog");
            //mListener.NoResultStarted();
        }else{
        if (mAdapter == null){
            mAdapter = new FriendsAdapter(users);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setUsers(users);
            mAdapter.notifyDataSetChanged();
        }}

    }*/

    public void isFriend(String key){
        final String myKey = key;
        mFriendReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(getUid()).child("friends").child(key);
        showProgressDialog();
        mFriendReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get user value
                Log.d("List Dis BUTTON",dataSnapshot.toString());
                if (dataSnapshot.getValue()==null){
                    mAreWeFriends = false;
                }else {
                    mAreWeFriends = dataSnapshot.getValue(Boolean.class);
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //mListener = (NoResultsProcess) context;
    }




}
