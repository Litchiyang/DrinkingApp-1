package urmc.drinkingapp.model;

import java.util.ArrayList;
import java.util.UUID;

/**
 * List of friends for a user in the friends tree
 */

public class Friends {
    UUID mID;
    ArrayList<Friend> friends;

    public Friends(){}

    public UUID getmID() {
        return mID;
    }

    public void setmID(UUID mID) {
        this.mID = mID;
    }

    public ArrayList<Friend> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Friend> friends) {
        this.friends = friends;
    }

}
