package urmc.drinkingapp.pages;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import mehdi.sakout.fancybuttons.FancyButton;
import urmc.drinkingapp.R;
import urmc.drinkingapp.model.User;


/**
 * Expanded Profile Fragment displays the user that was clicked from a the list of Friends
 * Uses the Firebase Database to obtain user
 */
public class ExpandedProfileFragment extends Fragment {


    //private DrinkingAppCollection mCollection;

    private Context mContext;
    private String mEmail;
    private String mPassword;

    //widgets
    private ImageView mProfilePicture;
    private TextView mFullnameTextView;
    private FancyButton mAddFriendButton;

    private User mUser;
    private String mKey;
    private boolean mAreWeFriends;

    final String TAG = "EXPANDED PROFILE";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    //Different references for different parts of the database
    private DatabaseReference mUserReference;
    private DatabaseReference mFriendReference;
    //Reference to Firebase storage to obtain profile picture of user
    private StorageReference mUserStorageRef;


    public ExpandedProfileFragment() {
        // Required empty public constructor
    }

    //Function to obtain the Firebase ID of the current user
    public String getUid() {return FirebaseAuth.getInstance().getCurrentUser().getUid();}

    //Process dialog to be shown while the information is downloaded from the Online database
    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expanded_profile, container, false);

        mContext = getActivity();

        //instance of the database collection
        //mCollection = DrinkingAppCollection.get(mContext);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]


        //get arguments being passed - Key to get the user
        Bundle args = getArguments();
        mKey = args.getString("KEY");

        // Initialize Database - With reference to the user
        mUserReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(mKey);

        //Initializes reference to Fireabase storage
        mUserStorageRef = FirebaseStorage.getInstance().getReference().child(mKey);

        //getting the user
        //mUser = mCollection.getUser(mEmail);
        //wiring up widgets
        mProfilePicture = (ImageView)view.findViewById(R.id.image_view_profile_pic_expanded_profile_fragment);
        mFullnameTextView = (TextView)view.findViewById(R.id.text_view_fullname_profile_expanded_profile_fragment);
        showProgressDialog();

        //Obtain value from the database - Check online documentation of Firebase for detailed explanation
        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get user value
                Log.d("PROFILE",dataSnapshot.toString());
                mUser = dataSnapshot.getValue(User.class);
                hideProgressDialog();
                // [START_EXCLUDE]
                if (mUser == null) {
                    // User is null, error out
                    Log.e(TAG, "User is unexpectedly null");
                    Toast.makeText(getActivity(),
                            "Error: could not fetch user.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //setting appropriate information in the widgets according to the user's attributes
                    mFullnameTextView.setText(mUser.getFirstname()+" "+mUser.getLastname());
                    //setting profile picture
                    String mPath = mUser.getProfilePic();

                    //If no profile pic then default no pic will be loaded
                    if (!mPath.matches("none")){
                        /*
                        Bitmap photo = getScaledBitmap(mPath, 200, 200);
                        mProfilePicture.setImageBitmap(photo);
                        */
                        loadPic();
                        mProfilePicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }

                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });


        //Checks the friends of the User to see if the expanded user being looked at is a friend or not
        //The add friend button adjusts accordingly
        mAddFriendButton = (FancyButton) view.findViewById(R.id.button_add_friend_expanded_profile_fragment);
        mFriendReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(getUid()).child("friends").child(mKey);

        showProgressDialog();
        mFriendReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get user value
                Log.d("DISABLED BUTTON",dataSnapshot.toString());
                if (dataSnapshot.getValue()==null){
                    mAreWeFriends = false;
                }else {
                    mAreWeFriends = dataSnapshot.getValue(Boolean.class);
                }
                hideProgressDialog();
                // [START_EXCLUDE]
                if (mAreWeFriends) {
                    mAddFriendButton.setText("Delete Friend");
                    mAddFriendButton.setBackgroundColor(Color.parseColor("#ffffff"));
                    mAddFriendButton.setTextColor(Color.parseColor("#000000"));
                    //mAddFriendButton.setEnabled(false);
                    /*
                    // User is null, error out
                    Log.e(TAG, "User is unexpectedly null");
                    Toast.makeText(getActivity(),
                            "Error: could not fetch user.",
                            Toast.LENGTH_SHORT).show();*/

                } else {
                    Log.d(TAG, "We are not friends");
                }

                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });

        //Sets up add friend button
        mAddFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAreWeFriends){
                    //mDatabase.child("users").child(getUid()).child("friends").child(mKey).setValue(false);
                    mDatabase.child("users").child(getUid()).child("friends").child(mKey).removeValue();
                    mDatabase.child("users").child(mKey).child("friends").child(getUid()).removeValue();
                    mAddFriendButton.setText("Add Friend");
                    mAreWeFriends = false;
                    mAddFriendButton.setBackgroundColor(Color.parseColor("#ff5a5f"));
                    mAddFriendButton.setTextColor(Color.parseColor("#ffffff"));
                }else{
                    mDatabase.child("users").child(getUid()).child("friends").child(mKey).setValue(true);
                    mDatabase.child("users").child(mKey).child("friends").child(getUid()).setValue(true);
                    mAddFriendButton.setText("Delete Friend");
                    mAreWeFriends = true;
                    mAddFriendButton.setBackgroundColor(Color.parseColor("#ffffff"));
                    mAddFriendButton.setTextColor(Color.parseColor("#000000"));
                }

                //mAddFriendButton.setEnabled(false);
            }
        });

        return view;
    }

    //method to fix pictures to be displayed in the app
    public Bitmap getScaledBitmap(String path, int width, int height) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        int sampleSize = 1;
        if(srcHeight > height || srcWidth > width ) {
            if(srcWidth > srcHeight) {
                sampleSize = Math.round(srcHeight / height);
            } else {
                sampleSize = Math.round(srcWidth / width);
            }
        }
        BitmapFactory.Options scaledOptions = new BitmapFactory.Options(); scaledOptions.inSampleSize = sampleSize;
        return BitmapFactory.decodeFile(path, scaledOptions);
    }

    //Loads profile picture from the Firebase Storage
    private void loadPic(){
        Glide.with(getActivity() /* context */)
                .using(new FirebaseImageLoader())
                .load(mUserStorageRef)
                .into(mProfilePicture);
    }


}
