package urmc.drinkingapp.pages;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import mehdi.sakout.fancybuttons.FancyButton;
import urmc.drinkingapp.MainActivity;
import urmc.drinkingapp.R;
import urmc.drinkingapp.control.FirebaseDAO;
import urmc.drinkingapp.control.IntentParam;
import urmc.drinkingapp.model.Friend;
import urmc.drinkingapp.model.User;

import static urmc.drinkingapp.control.LoginAuthentication.isValidEmail;
import static urmc.drinkingapp.control.LoginAuthentication.isValidPassword;

/**
 * Maps activity displays the user's location and the buddy's location in real time if available
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int MY_LOCATION_REQUEST_CODE = 7;


    // private User mUser;
    private Friend mBuddy;
    private Marker marker;
    private Double mBuddyLat;
    private Double mBuddyLon;
    private Boolean haveBuddy = false;
    private String mKey;

    private static final String TAG = "MAPS";

    private User mUser;
    private User mFriend;
    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]
    private StorageReference mUserStorageRef;


    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(urmc.drinkingapp.R.layout.activity_maps);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]
        mUserStorageRef = FirebaseStorage.getInstance().getReference().child(getUid());


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        createMarker();

    }

    private void createMarker() {
        final String userId = getUid();
        final FirebaseDAO dao = new FirebaseDAO();
        dao.getFriendsDatabase().child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Log.d(TAG, dataSnapshot.toString());

                        mUser = dataSnapshot.getValue(User.class);

                        Iterable<DataSnapshot> mUserChildren = dataSnapshot.getChildren();

                        // [START_EXCLUDE]
                        if (mUser == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(getApplicationContext(),
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "UserID is " + userId );
                            for (DataSnapshot buddy : mUserChildren) {

                                Log.d(TAG, "BUDDY key: " + buddy.getKey() + " value: " + buddy.getValue());

                                if (Integer.valueOf(buddy.getValue().toString()) == 1) {

                                    dao.getUserDatabase().child(buddy.getKey().toString()).addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    // Get user value
                                                    Log.d(TAG, snapshot.toString());

                                                    mFriend = snapshot.getValue(User.class);

                                                    // [START_EXCLUDE]
                                                    if (mFriend == null) {
                                                        // User is null, error out
                                                        Log.e(TAG, "User " + mFriend + " is unexpectedly null");
                                                        Toast.makeText(getApplicationContext(),
                                                                "Error: could not fetch user.",
                                                                Toast.LENGTH_SHORT).show();

                                                    } else {
                                                        Log.d(TAG, "UserID is " + mFriend.getFirstname());
                                                        Log.d(TAG, "long " + mFriend.getLon() + " " + mFriend.getLat());
                                                        LatLng friendLocation = new LatLng(mFriend.getLat(), mFriend.getLon());
                                                        mMap.addMarker(new MarkerOptions().position(friendLocation));
                                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(friendLocation));


                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                                }
                                            });


                                }
                            }


                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });


    }


//    public void showProgressDialog() {
//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(getActivity());
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            mProgressDialog.setMessage("Loading...");
//        }
//
//        mProgressDialog.show();
//    }
//
//    public void hideProgressDialog() {
//        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            mProgressDialog.dismiss();
//        }
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        mAuth.removeAuthStateListener(mAuthListener);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_online_sign_in, container, false);
//
//        mAuth = FirebaseAuth.getInstance();
//
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//                // ...
//            }
//        };
//
//        // Configure Google Sign In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        //getting persisted state
//        if (savedInstanceState!=null){
//            mEmailEditText.setText(savedInstanceState.getString("EMAIL"));
//            mPasswordEditText.setText(savedInstanceState.getString("PASSWORD"));
//        }
//
//        //onClick listener for the signIn button - checks for valid login information
//        //mSignInButton = (Button)view.findViewById(R.id.button_sign_in);
//        mSignInButton = (FancyButton)view.findViewById(R.id.button_sign_in);
//        mSignInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isValidEmail(mEmailEditText.getText())){
//                    Toast.makeText(getActivity(), "Enter a valid email",Toast.LENGTH_SHORT).show();
//                }
//                else if(!isValidPassword(mPasswordEditText.getText())){
//                    Toast.makeText(getActivity(), "Enter a valid password - more than 6 characters",
//                            Toast.LENGTH_SHORT).show();
//                }
//
//                //check for valid user and start the profile activity
//                else{
//                    mLoginEmail = mEmailEditText.getText().toString();
//                    mLoginPassword = mPasswordEditText.getText().toString();
//                    showProgressDialog();
//
//                    //Try to authenticate the user's credentials
//                    mAuth.signInWithEmailAndPassword(mLoginEmail, mLoginPassword)
//                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
//                                    hideProgressDialog();
//                                    // If sign in fails, display a message to the user. If sign in succeeds
//                                    // the auth state listener will be notified and logic to handle the
//                                    // signed in user can be handled in the listener.
//                                    if (!task.isSuccessful()) {
//                                        Log.w(TAG, "signInWithEmail", task.getException());
//                                        Toast.makeText(getActivity().getBaseContext(), "Authentication failed.",
//                                                Toast.LENGTH_SHORT).show();
//                                    }
//
//                                    else{
//                                        Intent intent = new Intent(getActivity(), MainActivity.class);
//                                        //hardcode bs here
//                                        intent.putExtra(IntentParam.ANALYZE,1);
//                                        startActivity(intent);
//                                    }
//                                }
//                            });
//
//                }
//            }
//        });
//
//
//        return view;
//    }



}
