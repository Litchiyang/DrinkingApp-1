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
    private String mEmail;
    private String mPhoneNumber;
    private String mProfilePic = "none";

    private Double Lat;
    private Double Lon;

    private String mID;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id){
        mID = id;
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

    public String getID() {
        return mID;
    }
    public void setID(String mID) {
        this.mID = mID;
    }

    public String getFirstname() {
        return mFirstname;
    }
    public void setFirstname(String mFirstname) {
        this.mFirstname = mFirstname;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }
    public void setPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public Double getLat() {
        return Lat;
    }
    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLon() {
        return Lon;
    }
    public void setLon(Double lon) {
        Lon = lon;
    }

    public String getLastname(){
        return mLastname;
    }

    public void setLastname(String mLastname) {
        this.mLastname = mLastname;
    }


}


