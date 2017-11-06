package urmc.drinkingapp.pages.Profile;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import mehdi.sakout.fancybuttons.FancyButton;
import urmc.drinkingapp.R;
import urmc.drinkingapp.control.FirebaseDAO;
import urmc.drinkingapp.control.Utils;
import urmc.drinkingapp.model.User;
import urmc.drinkingapp.pages.PhotoActivity;


/**
 * Fragment to edit the user's profile. The changes being made are saved into the online database.
 */
public class OnlineEditProfileFragment extends Fragment {


    //widgets
    private ImageView mProfilePicImageView;
    private EditText mFirstnameEditText;
    private EditText mLastnameEditText;
    private FancyButton mOkButton;
    private FancyButton mChangePicButton;
    private TextView mEmailTextView;
    private StorageReference mStorageRef;
    private String mPath;
    private StorageReference mUserStorageRef;

    private User mUser;
    final String TAG = "OnlineEditProfile";

    private EditProfileFinished mListener;

    public interface EditProfileFinished{
        void EditProfileOK();
    }

    public OnlineEditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_online_edit_profile, container, false);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mUserStorageRef = mStorageRef.child(Utils.getUid());

        //wiring up the widgets
        mProfilePicImageView = (ImageView)view.findViewById(R.id.image_view_profile_pic_edit_profile);
        mFirstnameEditText = (EditText)view.findViewById(R.id.edit_text_firstname_profile);
        mLastnameEditText =  (EditText)view.findViewById(R.id.edit_text_lastname_profile);
        mEmailTextView = (TextView) view.findViewById(R.id.edit_text_email_profile);
        FirebaseDAO dao = new FirebaseDAO();

        // [START single_value_read]
        final String userId = Utils.getUid();
        dao.getUserDatabase().child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        mUser = dataSnapshot.getValue(User.class);

                        if (mUser == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(getActivity(),
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                            mUser = new User(Utils.getUid());
                        } else {
                            Log.d(TAG, "Editing User " + userId +":"+dataSnapshot.toString());

                            //setting appropriate information in the widgets according to the user's attributes
                            mFirstnameEditText.setText(mUser.getFirstname());
                            mLastnameEditText.setText(mUser.getLastname());
                            mEmailTextView.setText(mUser.getEmail());
                            //setting profile picture
                            String mPath = mUser.getProfilePic();

                            if (!mPath.matches("none")){
                                // Load the image using Glide
                                loadPic();
                                /*
                                Bitmap photo = getScaledBitmap(mPath, 200, 200);
                                mProfilePicImageView.setImageBitmap(photo);
                                */
                                mProfilePicImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });

        //onSavedInstanceState standard procedure to persist the state
        if (savedInstanceState!=null){
            mFirstnameEditText.setText(savedInstanceState.getString("FULLNAME"));
        }

        //ok button to save changes
        //mOkButton = (Button)view.findViewById(R.id.button_ok_edit_profile);
        mOkButton = (FancyButton) view.findViewById(R.id.button_ok_edit_profile);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDAO dao = new FirebaseDAO();
                mUser.setFirstname(mFirstnameEditText.getText().toString());
                mUser.setLastname(mLastnameEditText.getText().toString());
                dao.updateUser(mUser);

                mListener.EditProfileOK();
                if(!mUser.getProfilePic().equals("none")){uploadPic();}
                /*
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();*/

            }
        });

        //onClickListener to change picture - triggers the photoActivity capable of taking pictures
        mChangePicButton = (FancyButton) view.findViewById(R.id.button_change_profile_pic);
        mChangePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Changing Pic");
                Intent intent = new Intent(getActivity(), PhotoActivity.class);
                startActivityForResult(intent,0);
            }
        });


        return view;
    }

    //overriding onActivityResult to get info back from the PhotoActivity - sets the new picture
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String path = data.getStringExtra("PATH");
            mPath = path;
            mUser.setProfilePic(path);
            Bitmap photo = getScaledBitmap(path, 200, 200);
            mProfilePicImageView.setImageBitmap(photo);
            mProfilePicImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnlineEditProfileFragment.EditProfileFinished)context;

    }

    private void uploadPic(){
        try {
            InputStream stream = new FileInputStream(new File(mPath));
            UploadTask uploadTask = mUserStorageRef.putStream(stream);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG,"uploadPic failed with storagereference:"+mUserStorageRef);
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    @SuppressWarnings("VisibleForTests")
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });
        }
        catch (Exception e){
            Log.d(TAG,"File not found");
        }
    }

    private void loadPic(){
        Glide.with(getActivity() /* context */)
                .using(new FirebaseImageLoader())
                .load(mUserStorageRef)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(mProfilePicImageView);
    }

}
