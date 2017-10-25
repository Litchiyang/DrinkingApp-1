package urmc.drinkingapp.model;

import java.util.UUID;

/**
 * Single friend of a user in the friends tree
 * friend status indicates the relation of the friend with the user who owns the Friends object
 */

public class Friend {
    public static final int PENDING = 0;
    public static final int FRIEND = 1;
    public static final int BUDDY = 2;

    private String friendID;
    private int friendStatus;

    public Friend(){}

    public  Friend(String friendID){
        this.friendID = friendID;
    }

    public String getfriendID() {
        return friendID;
    }

    public void setfriendID(String friendID) {
        this.friendID = friendID;
    }

    public int getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(int friend) {
        friendStatus = friend;
    }
}
