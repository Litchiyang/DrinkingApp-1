package urmc.drinkingapp.pages;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;
import urmc.drinkingapp.MainActivity;
import urmc.drinkingapp.R;
import urmc.drinkingapp.control.FirebaseDAO;
import urmc.drinkingapp.control.IntentParam;
import urmc.drinkingapp.control.Utils;
import urmc.drinkingapp.model.Friend;
import urmc.drinkingapp.model.User;

import static urmc.drinkingapp.control.LoginAuthentication.isValidEmail;
import static urmc.drinkingapp.control.LoginAuthentication.isValidPassword;

/**
 * Maps activity displays the user's location and the buddy's location in real time if available
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location MyLocation;
    private static final String TAG = "MapsActivity";
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private Marker mMarker;
    private FirebaseDAO mFirebaseDAO;
    private HashMap<String,Marker> mFriendsList;


    //https://stackoverflow.com/questions/41500765/how-can-i-get-continuous-location-updates-in-android-like-in-google-maps

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(urmc.drinkingapp.R.layout.activity_maps);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            Toast.makeText(this, "Please give permission first!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.d(TAG, "Permission granted");
        }

        mFirebaseDAO = new FirebaseDAO();

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //get the latitude and longitude from the location
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                //update location to firebase
                mFirebaseDAO.getUserDatabase().child(Utils.getUid()).child("lat").setValue(latitude);
                mFirebaseDAO.getUserDatabase().child(Utils.getUid()).child("lon").setValue(longitude);


                //get the location name from latitude and longitude
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> addresses =
                            geocoder.getFromLocation(latitude, longitude, 1);
                    String result = addresses.get(0).getSubLocality() + ":";
                    result += addresses.get(0).getLocality() + ":";
            result += addresses.get(0).getCountryCode();
                    LatLng latLng = new LatLng(latitude, longitude);
                    mMarker.setPosition(latLng);
                    mMap.setMaxZoomPreference(20);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady()");

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this,"User permission required",Toast.LENGTH_LONG).show();
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            MyLocation = location;
                            //get the latitude and longitude from the location
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            mFirebaseDAO.getUserDatabase().child(Utils.getUid()).child("lat").setValue(latitude);
                            mFirebaseDAO.getUserDatabase().child(Utils.getUid()).child("lon").setValue(longitude);
                            // Logic to handle location object
                            LatLng latLng = new LatLng(MyLocation.getLatitude(), MyLocation.getLongitude());
                            mMarker = mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .title("You are here")
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng)      // Sets the center of the map to location user
                            .zoom(10)                   // Sets the zoom
                            .build();                   // Creates a CameraPosition from the builder
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    }
                });
        mMap.setMyLocationEnabled(true);
        //add marker for friends
        createMarker();
    }

    private void createMarker() {
        mFriendsList = new HashMap<>();
        // iterate through users
        mFirebaseDAO.getUserDatabase().addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Log.d(TAG, dataSnapshot.toString());
                        //list of users
                        for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                            //iterate through friends
                            final User user = userSnapshot.getValue(User.class);
                            mFirebaseDAO.getFriendsDatabase().child(Utils.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                                        Friend friend = ds.getValue(Friend.class);
                                        // match only when users are buddies
                                        if(user.getID().equals(friend.getfriendID()) &&
                                                friend.getFriendStatus() == Friend.BUDDY){
                                            if(mFriendsList.containsKey(user.getID())){
                                                LatLng location = new LatLng(user.getLat(), user.getLon());
                                                // get buddys current location
                                                Marker temp =   mFriendsList.get(user.getID());
                                                if(user.isDrunk()){
                                                    temp.setPosition(location);
                                                    temp.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                }
                                                else{
                                                    temp.setPosition(location);
                                                    temp.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                                }
                                            }
                                            // new entry, mark the buddys location on the map
                                            else{
                                                if(user.getLat() != null && user.getLon() != null) {
                                                    LatLng location = new LatLng(user.getLat(), user.getLon());
                                                    Marker temp = mMap.addMarker(new MarkerOptions()
                                                            .position(location)
                                                            .title(user.getFirstname() + " " + user.getLastname()));
                                                    if(user.isDrunk()){
                                                        temp.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                    }
                                                    else{
                                                        temp.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                                    }
                                                    mFriendsList.put(user.getID(), temp);
                                                }
                                            }
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

}
