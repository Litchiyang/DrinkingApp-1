package urmc.drinkingapp.pages.GoingOutSettings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mehdi.sakout.fancybuttons.FancyButton;
import urmc.drinkingapp.R;

public class GoingOutSettingsActivity extends AppCompatActivity {

    private FancyButton mAddNumber;
    private FancyButton mAddCall;
    public DatabaseReference mFriendsDatabase;

    //private DrinkingAppCollection mCollection;
    private RecyclerView.Adapter mAdapter;
    public RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_going_out_settings);
        //initlize recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.going_out_recycler_view);

    }



}
