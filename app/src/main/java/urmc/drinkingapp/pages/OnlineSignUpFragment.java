package urmc.drinkingapp.pages;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mehdi.sakout.fancybuttons.FancyButton;
import urmc.drinkingapp.MainActivity;
import urmc.drinkingapp.R;
import urmc.drinkingapp.control.FirebaseDAO;
import urmc.drinkingapp.control.IntentParam;
import urmc.drinkingapp.model.User;

import static urmc.drinkingapp.control.LoginAuthentication.isValidEmail;
import static urmc.drinkingapp.control.LoginAuthentication.isValidPassword;


/**
 * Fragment to sign up into the app. Creates a new user for the online database and creates the firebase authentication credentials.
 */
public class OnlineSignUpFragment extends Fragment {

    //private Button mSignUpButton;
    //private Button mCancelButton;
    private FancyButton mSignUpButton;
    private FancyButton mCancelButton;
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    private String mLoginEmail;
    private String mLoginPassword;
    private String mLoginFirstName;
    private String mLoginLastName;
    private User mLoginUser;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String TAG = "OnlineSignUpFragment";

    private DatabaseReference mDatabase;


    public interface SignUpProcessCancel{
        void SignUpCancel();
    }

    private SignUpProcessCancel mListener;


    public OnlineSignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

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
        View view = inflater.inflate(R.layout.fragment_online_sign_up, container, false);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        mCancelButton = (FancyButton) view.findViewById(R.id.button_cancel_sign_up);
        mFirstNameEditText = (EditText)view.findViewById(R.id.edit_text_first_name_sign_up);
        mLastNameEditText = (EditText)view.findViewById(R.id.edit_text_last_name_sign_up);
        mEmailEditText = (EditText)view.findViewById(R.id.edit_text_enter_email_sign_up);
        mPasswordEditText = (EditText)view.findViewById(R.id.edit_text_enter_password_sign_up);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.SignUpCancel();
            }
        });

        //Check for valid information
        mSignUpButton = (FancyButton) view.findViewById(R.id.button_sign_up_sing_up);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValidEmail(mEmailEditText.getText())){
                    Toast.makeText(getActivity(), getString(R.string.email_hint_string),
                            Toast.LENGTH_SHORT).show();
                }
                else if(!isValidPassword(mPasswordEditText.getText())){
                    Toast.makeText(getActivity(), getString(R.string.enter_password_string),
                            Toast.LENGTH_SHORT).show();
                }
                else if(mFirstNameEditText.getText().length()<1){
                    Toast.makeText(getActivity(), getString(R.string.firstname_hint_string),
                            Toast.LENGTH_SHORT).show();
                }
                else if(mLastNameEditText.getText().length()<1){
                    Toast.makeText(getActivity(), getString(R.string.lastname_hint_string),
                            Toast.LENGTH_SHORT).show();
                }

                else{
                    mLoginEmail = mEmailEditText.getText().toString();
                    mLoginPassword = mPasswordEditText.getText().toString();
                    mLoginFirstName = mFirstNameEditText.getText().toString();
                    mLoginLastName = mLastNameEditText.getText().toString();
                    
                    showProgressDialog();
                    //try to create the user with the given information and start the main activity if successful
                    mAuth.createUserWithEmailAndPassword(mLoginEmail, mLoginPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                    hideProgressDialog();
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        Log.e(TAG,task.getException().getMessage());
                                    } else {
                                        onAuthSuccess(task.getResult().getUser(),mLoginFirstName, mLoginLastName);
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        intent.putExtra(IntentParam.ANALYZE,1);
                                        getActivity().startActivity(intent);
                                        getActivity().finish();
                                    }
                                    // ...
                                }
                            });
                }
            }
        });
        return view;
    }

    private void onAuthSuccess(FirebaseUser user,String firstname, String lastname) {
        // Write new user
        User u = new User();
        u.setLastname(lastname);
        u.setFirstname(firstname);
        u.setEmail(user.getEmail());
        u.setID(user.getUid());
        u.setLat(Math.random()*180-90);
        u.setLon(Math.random()*360-180);
        //creates a new user in database
        FirebaseDAO dao = new FirebaseDAO();
        dao.setUser(u);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnlineSignUpFragment.SignUpProcessCancel)context;
    }

}
