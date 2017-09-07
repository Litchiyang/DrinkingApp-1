package urmc.drinkingapp.model;

import java.util.UUID;

/**
 * Single friend of a user in the friends tree
 */

public class Friend {
    private UUID mID;
    private Boolean isFriend;

    public Friend(){}

    public UUID getmID() {
        return mID;
    }

    public void setmID(UUID mID) {
        this.mID = mID;
    }

    public Boolean getFriend() {
        return isFriend;
    }

    public void setFriend(Boolean friend) {
        isFriend = friend;
    }
}
