package urmc.drinkingapp.control.DAOs;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import urmc.drinkingapp.model.User;

/**
 * Created by Berto on 2017/8/25.
 * Data access object for Friends
 * supports basic CRUD operations
 */

public class FriendsDAO {

    String currentUser;

    public FriendsDAO(){
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public FriendsDAO(String currentUser){
        this.currentUser = currentUser;
    }

    public ArrayList<User> getFriends(){
        return null;
    }

    public void updateFriends(){

    }

    public User getFriend(){
        return null;
    }

    public boolean removeFriend(){

        return true;
    }

    public void addFriend(){

    }
}
