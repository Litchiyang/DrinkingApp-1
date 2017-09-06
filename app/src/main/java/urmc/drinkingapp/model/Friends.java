package urmc.drinkingapp.model;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Berto on 2017/9/5.
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
