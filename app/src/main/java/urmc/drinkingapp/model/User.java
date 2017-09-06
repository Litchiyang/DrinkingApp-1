package urmc.drinkingapp.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Alessandro on 2/25/17.
 */

/**
 * Model to represent a User with all its attributes
 * Contains getters and setters for attributes
 */

//Object characterizing a user in the app
public class User {
    private String mFirstname;
    private String mLastname;
    private String mNickname;
    private String mEmail;
    private String mPhoneNumber;
    private String mProfilePic = "none";

    public Double Lat;
    public Double Lon;

    private UUID mID;
    public static boolean firstTime = true;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    /*
    //constructor
    public User(){
        mID = UUID.randomUUID();
        mProfilePic = "none";

    }*/
    /* Getters and Setters*/

    public User(UUID id){
        mID = id;
    }

    public String getFullname() {
        return mFirstname;
    }

    public void setFullname(String mFullname) {
        this.mFirstname = mFullname;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getProfilePic() {
        return mProfilePic;
    }

    public void setProfilePic(String mProfilePic) {
        this.mProfilePic = mProfilePic;
    }

    public UUID getID() {
        return mID;
    }

    public void setID(UUID mID) {
        this.mID = mID;
    }


}


