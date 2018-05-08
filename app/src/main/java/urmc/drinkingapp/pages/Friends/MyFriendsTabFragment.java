package urmc.drinkingapp.pages.Friends;

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
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import urmc.drinkingapp.R;
import urmc.drinkingapp.control.FirebaseDAO;
import urmc.drinkingapp.control.Utils;
import urmc.drinkingapp.model.User;
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
    private FirebaseDAO dao;

    public MyFriendsTabFragment() {
        // Required empty public constructor
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
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.d(TAG,"QUERY "+q.toString());
        return q;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
