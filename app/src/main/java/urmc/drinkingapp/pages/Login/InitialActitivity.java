package urmc.drinkingapp.pages.Login;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import urmc.drinkingapp.R;

/**
 * Initial activity hosting the SignInFragment and the OnlineSignUp
 * There are two callback interfaces in place to switch between these two fragments
 */
public class InitialActitivity extends AppCompatActivity implements SignInFragment.SignUpProcess, SignUpFragment.SignUpProcessCancel {

    private SignInFragment mLoginFragment;
    private SignUpFragment mSignUpFragment;
    int counter = 0;


    public void SignUpStarted(){
        FragmentManager fm = getSupportFragmentManager();
        counter = 1;
        mSignUpFragment = new SignUpFragment();
        fm.beginTransaction()
                .replace(R.id.frame_layout_login_activity,mSignUpFragment)
                .commit();
    }

    public void SignUpCancel(){
        FragmentManager fm = getSupportFragmentManager();
        counter = 0;
        mLoginFragment = new SignInFragment();
        fm.beginTransaction()
                .replace(R.id.frame_layout_login_activity,mLoginFragment)
                .commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_initial_actitivity);

        FragmentManager fm =  getSupportFragmentManager();
        mLoginFragment = new SignInFragment();
        //get persisted fragment if it exists
        if (savedInstanceState!=null){
            mLoginFragment = (SignInFragment) getSupportFragmentManager().getFragment(savedInstanceState,"FRAGMENT");
        }
        fm.beginTransaction()
                .replace(R.id.frame_layout_login_activity,mLoginFragment)
                .commit();

    }

    //persisting fragment
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (counter==0){
            getSupportFragmentManager().putFragment(outState,"FRAGMENT",mLoginFragment);
        }else{
            getSupportFragmentManager().putFragment(outState,"FRAGMENT",mSignUpFragment);
        }
    }
}
