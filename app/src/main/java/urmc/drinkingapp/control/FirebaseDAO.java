package urmc.drinkingapp.control;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import urmc.drinkingapp.model.Friend;
import urmc.drinkingapp.model.User;

/**
 * For easier access to firebase
 * supports basic CRUD operations except Read
 * get methods can not be implemented due to firebase does not supporting synchronous request,
 * but since get methods are rarely used(hopefully), this class should be good enough.
 * Use this class for firebase interaction
 */

public class FirebaseDAO {
    private final static String TAG = "FirebaseDAO";
    private static DatabaseReference mDatabase;
    private static DatabaseReference mFriendDB;
    private static DatabaseReference mUserDB;
    private String stringCursor;
    private Friend friendCursor;
    private User userCursor;
    private ArrayList<Friend> friendList;
    private ArrayList<User> userList;

    public FirebaseDAO(){
        Log.d(TAG,"Begin FirebaseDAO init");
        initlizeDAO();
        Log.d(TAG,"FirebaseDAO init finished");
    }

    //a seperate method might be useful in the future
    /*
     * initialize the things needed for DAO:
     * getting the reference to User,Friends table
     */
    private void initlizeDAO(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserDB = mDatabase.child("users");
        mFriendDB = mDatabase.child("friends");
//        mUserDB.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
//                    Log.d(TAG,userSnapshot.getKey());
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e(TAG,"Connection Failed");
//            }
//        });
//
//        mFriendDB.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e(TAG,"Connection Failed");
//            }
//        });
    }

    public void updateUser(User user){
        String id = user.getID();
        Map<String,Object> taskMap = new HashMap<>();
        taskMap.put(id,user);
        mUserDB.updateChildren(taskMap);
    }

    public void setUser(User user){
        String id = user.getID();
        mUserDB.child(id).setValue(user);
    }

    public void deleteUser(User user){
        this.userCursor = user;
        mUserDB.child(userCursor.getID()).removeValue();
    }


    public void setFriend(String userID, Friend friend){
        mFriendDB.child(userID).child(friend.getfriendID()).setValue(friend.getFriendStatus());}

    //update the relation of a friend
    public void updateFriend(String userID, Friend friend){
        this.stringCursor = userID;
        this.friendCursor = friend;
        mFriendDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(stringCursor).exists()){
                    Map<String,Object> taskMap = new HashMap<>();
                    taskMap.put(friendCursor.getfriendID(),friendCursor.getFriendStatus());
                    mFriendDB.child(stringCursor).updateChildren(taskMap);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void deleteFriend(String userID, String friendId){
        this.stringCursor = userID;
        this.friendCursor = new Friend();
        friendCursor.setfriendID(friendId);
        mFriendDB.child(userID).child(friendId).removeValue();
    }

    //this method is useful for making queries
    public DatabaseReference getFriendsDatabase(){
        return  mFriendDB;
    }

    public DatabaseReference getUserDatabase(){
        return mUserDB;
    }

}
