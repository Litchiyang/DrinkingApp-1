package urmc.drinkingapp.pages;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;


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
import urmc.drinkingapp.DrunkModeDefaultActivity;
import urmc.drinkingapp.R;
import urmc.drinkingapp.database.PhoneNumberCollection;
import urmc.drinkingapp.model.PhoneNumbers;
import urmc.drinkingapp.model.User;

public class GoingOutSettingsActivity extends AppCompatActivity {

    private BuddyFragment mBuddyFragment;
    private PhoneNumbersFragment mPhoneNumberFragment;
    private CheckBox mBuddyCheckBox;
    private CheckBox mTextCheckBox;
    private CheckBox mCallCheckBox;
    private EditText mText;
    private EditText mCall;
    private EditText mPhoneNumber;
    private FancyButton mAddNumber;
    private FancyButton mAddCall;
    private FancyButton mSave;
    private DatabaseReference mDatabase;
    public DatabaseReference mUserReference;
    public DatabaseReference mPhoneNumberReference;
    private StorageReference mUserStorageRef;

    //private DrinkingAppCollection mCollection;
    private FirebaseRecyclerAdapter mAdapter;
    public RecyclerView mRecyclerView;



    //Get Current user's UID from the database
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_going_out_settings);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]


        mPhoneNumberReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(getUid()).child("phoneNumber");


        //Set the buddy checkbox
        mBuddyCheckBox = (CheckBox)findViewById(R.id.check_box_bubby_settings);

        mBuddyCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //sets the UserFragment
                    mBuddyFragment = new BuddyFragment();
                    FragmentManager fm = getSupportFragmentManager();
                    fm.beginTransaction()
                            .add(R.id.frame_layout_buddy, mBuddyFragment)
                            .commit();
                } else {

                }

            }
        });


        mTextCheckBox = (CheckBox)findViewById(R.id.check_box_text_settings);
        mCallCheckBox = (CheckBox)findViewById(R.id.check_box_call_settings);

        //Set the BuddyFragment
        mBuddyFragment = new BuddyFragment();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.frame_layout_buddy, mBuddyFragment)
                .commit();



        mPhoneNumber = (EditText) findViewById(R.id.edit_text_phone_number);

        //Set the phone numbers Fragment
        FragmentManager manager1 = getSupportFragmentManager();
        mPhoneNumberFragment = new PhoneNumbersFragment();
        manager1.beginTransaction()
                .add(R.id.frame_layout_out_settings_numbers, mPhoneNumberFragment)
                .commit();

        mAddNumber = (FancyButton) findViewById(R.id.button_out_settings_add_number);
        mAddNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneNumbers mNumber = new PhoneNumbers();
                //mNumber.setNumber(mPhoneNumber.getText().toString());
                //PhoneNumberCollection.get().addNumbers(mNumber);
                mDatabase.child("users").child(getUid()).child("phoneNumber").setValue(mPhoneNumber.getText().toString());
                //OnlineUpdateUI();
            }
        });


        mText = (EditText) findViewById(R.id.edit_text_text);
        mCall = (EditText) findViewById(R.id.edit_text_call);

        FragmentManager manager2 = getSupportFragmentManager();
        mPhoneNumberFragment = new PhoneNumbersFragment();
        manager2.beginTransaction()
                .add(R.id.frame_layout_out_settings_calls, mPhoneNumberFragment)
                .commit();

        mAddCall = (FancyButton) findViewById(R.id.button_out_settings_add_calls);
        mAddCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneNumbers mNumber = new PhoneNumbers();
//                mNumber.setNumber(mCall.getText().toString());
//                PhoneNumberCollection.get().addNumbers(mNumber);
                mPhoneNumberReference.setValue(mPhoneNumber.getText().toString());
            }
        });

        mSave = (FancyButton) findViewById(R.id.button_out_settings_save);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String presetPhoneNumber = PhoneNumberViewHolder.mPhoneNumber.getNumber();
                String presetText = mText.getText().toString();
                String presetCall = PhoneNumberViewHolder.mPhoneNumber.getNumber();

                Intent i = new Intent(GoingOutSettingsActivity.this, DrunkModeDefaultActivity.class);

                i.putExtra("PRESETPHONENUMBER", presetPhoneNumber);
                i.putExtra("PRESETTEXT", presetText);
                i.putExtra("PRESETCALL", presetCall);
                i.putExtra("BUDDYCHECKED", mBuddyCheckBox.isChecked());
                i.putExtra("TEXTCHECKED", mTextCheckBox.isChecked());
                i.putExtra("CALLCHECKED", mCallCheckBox.isChecked());

                startActivity(i);
                finish();
            }
        });

    }




}
