package urmc.drinkingapp.pages.Profile;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.MessageDigest;

import mehdi.sakout.fancybuttons.FancyButton;
import urmc.drinkingapp.R;
import urmc.drinkingapp.control.FirebaseDAO;
import urmc.drinkingapp.control.IntentParam;
import urmc.drinkingapp.control.Utils;
import urmc.drinkingapp.model.User;
import urmc.drinkingapp.pages.Login.InitialActitivity;
import urmc.drinkingapp.pages.Login.SignInFragment;


/**
 * Fragment to display the profile of the main user. Gets the info from the Database
 */
public class OnlineProfileFragment extends Fragment {


    public interface EditProfileProcess{
        void EditProfileStarted();
    }

    private EditProfileProcess mListener;

    private String mEmail;

    //widgets
    private ImageView mProfilePicture;
    private TextView mFullnameTextView;
    private TextView mEmailTextView;
    //private Button mEditProfileButton;
    private FancyButton mEditProfileButton;
    private FancyButton mLogOutButton;
    private static final String TAG = "OnlineProfileFragment";

    private User mUser;
    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]
    private StorageReference mUserStorageRef;


    public String getUid() {return FirebaseAuth.getInstance().getCurrentUser().getUid();}

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


    public OnlineProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_online_profile, container, false);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]
        mUserStorageRef = FirebaseStorage.getInstance().getReference().child(getUid());

        mProfilePicture = (ImageView)view.findViewById(R.id.image_view_profile_pic);
        mFullnameTextView = (TextView)view.findViewById(R.id.text_view_fullname_profile);
        mEmailTextView = (TextView)view.findViewById(R.id.text_view_email_profile);


        showProgressDialog();
        // [START single_value_read]
        final String userId = getUid();
        FirebaseDAO dao = new FirebaseDAO();
        dao.getUserDatabase().child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Log.d(TAG,dataSnapshot.toString());
                        mUser = dataSnapshot.getValue(User.class);
                        hideProgressDialog();
                        // [START_EXCLUDE]
                        if (mUser == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(getActivity(),
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG,"Viewing User "+mUser.getID());
                            //setting appropriate information in the widgets according to the user's attributes
                            mFullnameTextView.setText(mUser.getFirstname()+" "+mUser.getLastname());
                            mEmailTextView.setText(mUser.getEmail());
                            //setting profile picture
                            String mPath = mUser.getProfilePic();

                            if (!mPath.matches("none")){
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
        // [END single_value_read]

        //starting the EditProfileActivity when EditProfile Button is pressed
        //mEditProfileButton = (Button)view.findViewById(R.id.button_edit_profile);
        mEditProfileButton = (FancyButton) view.findViewById(R.id.button_edit_profile);
//        if(savedInstanceState.getString())
        mEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.EditProfileStarted();
            }
        });
        mLogOutButton = view.findViewById(R.id.button_log_out);
        mLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor Ed=sp.edit();
                Ed.remove(IntentParam.KEY_USERNAME);
                Ed.remove(IntentParam.KEY_PASSWORD);
                Ed.commit();
                Intent intent = new Intent(getActivity(), InitialActitivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }

    //method to update the UI after the EditProfile Activity Returns
    public void updateUI(){
        showProgressDialog();
        // [START single_value_read]
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        mUser = dataSnapshot.getValue(User.class);
                        hideProgressDialog();

                        // [START_EXCLUDE]
                        if (mUser == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(getActivity(),
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            //setting appropriate information in the widgets according to the user's attributes
                            mFullnameTextView.setText(mUser.getFirstname()+" "+mUser.getLastname());
                            mEmailTextView.setText(mUser.getEmail());
                            //setting profile picture
                            String mPath = mUser.getProfilePic();

                            if (!mPath.matches("none")){
                                loadPic();
                                /*
                                Bitmap photo = getScaledBitmap(mPath, 200, 200);
                                mProfilePicture.setImageBitmap(photo);
                                */
                                mProfilePicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnlineProfileFragment.EditProfileProcess)context;
    }

    private void loadPic(){
        Log.d(TAG,"loading image:"+mUserStorageRef);
        FirebaseDAO dao = new FirebaseDAO();
        // clear cache to load from Firebase
        dao.getUserDatabase().child(Utils.getUid()).child("profilePic").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.equals("none")){
                    RequestOptions ro = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true);
                    Log.d(TAG,"Loading image from firebase with location:"+dataSnapshot.getValue().toString());
                    Glide.with(getContext())
                            .load(mUserStorageRef)
                            .apply(ro)
//                            .load(dataSnapshot.getValue().toString())
                            .into(mProfilePicture);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
