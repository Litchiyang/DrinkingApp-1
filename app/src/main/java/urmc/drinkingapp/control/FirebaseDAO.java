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

import urmc.drinkingapp.model.Friend;
import urmc.drinkingapp.model.Friends;
import urmc.drinkingapp.model.User;

/**
 * For easier access to database
 * basic CRUD operations
 */

public class FirebaseDAO {
    private final static String TAG = "FirebaseDAO";
    private String id;
    private DatabaseReference mDatabase;
    private DatabaseReference mFriendDB;
    private DatabaseReference mUserDB;

    private ArrayList<User> userList;
    private Friends friendList;

    public FirebaseDAO(){
        this.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        initlizeDAO();
    }

    public FirebaseDAO(String uuid){
        this.id = uuid;
        initlizeDAO();
    }

    //a seperate method might be useful in the future
    private void initlizeDAO(){
        if(this.id == null){
            throw new NullPointerException("ID not initalized");
        }
        userList = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserDB = mDatabase.child("users");
        mFriendDB = mDatabase.child("friends").child(this.id);

        mUserDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    User tempUser = userSnapshot.getValue(User.class);
                    userList.add(tempUser);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG,"Connection Failed");
            }
        });

        mFriendDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    friendList = userSnapshot.getValue(Friends.class);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG,"Connection Failed");
            }
        });
    }

    public ArrayList<Friend> getFriends(){
        return friendList.getFriends();
    }

    public User getUser(){
        for (User user : userList){
            if(user.getID().toString() == this.id){
                return user;
            }
        }
        return  null;
    }

    public User getUser(String id){
        return new FirebaseDAO(id).getUser();
    }

    public void updateFriend(String userID, Friend friend){
        String friendName = friend.getmID().toString();
        ArrayList<Friend> tempFriends = friendList.getFriends();
        for (int i = 0; i < tempFriends.size(); i++) {
            Friend f = tempFriends.get(i);
            if(f.getmID().toString().equals(friendName)){
                tempFriends.set(i,friend);
                break;
            }
        }
        friendList.setFriends(tempFriends);
        mFriendDB.child(this.id).setValue(friendList);
    }

    public void deleteFriend(String friendId){
        String friendName = friendId;
        ArrayList<Friend> tempFriends = friendList.getFriends();
        for (int i = 0; i < tempFriends.size(); i++) {
            Friend f = tempFriends.get(i);
            if(f.getmID().toString().equals(friendName)){
                tempFriends.remove(i);
                break;
            }
        }
        friendList.setFriends(tempFriends);
        mFriendDB.child(this.id).setValue(friendList);
    }

    public void addFriend(String id){
        //todo create mapping for friends DB
        mFriendDB.push().setValue(id);
    }

    public User getFriend(String id){
        return getUser(id);
    }

}
