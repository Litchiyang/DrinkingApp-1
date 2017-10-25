package urmc.drinkingapp.testing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

import java.util.UUID;

import urmc.drinkingapp.R;
import urmc.drinkingapp.control.FirebaseDAO;
import urmc.drinkingapp.model.Friend;
import urmc.drinkingapp.model.User;

public class TestActivity extends AppCompatActivity {

    private final static String testEmail = "a@a.com";
    private final static String testPassword = "aaaaaa";
    private final static String TAG = "TestActivity";
    int status = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    @Override
    public View onCreateView(String name, final Context context, AttributeSet attrs) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(testEmail,testPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseDAO dao = new FirebaseDAO();
                        Friend f = new Friend();
                        User u = new User("X");
                        u.setFirstname("John");
                        u.setLastname("Doe");
                        u.setPhoneNumber("111111");
                        u.setEmail("1212");
                        f.setfriendID("11009785-7095-4e50-bcf0-75535cbd5325");
                        f.setFriendStatus(Friend.FRIEND);
                        if(status ==1) {
                          dao.getUserDatabase().addListenerForSingleValueEvent(new ValueEventListener() {
                              @Override
                              public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                    Log.d("testxxx",ds.getValue(User.class).getID());
                                }
                              }

                              @Override
                              public void onCancelled(DatabaseError databaseError) {

                              }
                          });
                            status = 0;
                        }
//                        User u = new User();
//                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
//                        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(u);
//                        mDatabase.setValue()
//                        Log.d(TAG,"User is"+dao.getSelfID());
                    }
                }
        );

//        FirebaseDAO dao = new FirebaseDAO();
        return super.onCreateView(name, context, attrs);
    }
}
