package urmc.drinkingapp.pages;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import mehdi.sakout.fancybuttons.FancyButton;
import ng.max.slideview.Util;
import urmc.drinkingapp.R;
import urmc.drinkingapp.control.FirebaseDAO;
import urmc.drinkingapp.control.Utils;
import urmc.drinkingapp.model.Friend;
import urmc.drinkingapp.model.User;

/**
 * Created by Alessandro on 3/10/17.
 */

/**
 * View holder for the recycler view displaying all friends
 */
//the layout that maps to this class should be friends_view_holder_add_buddy.xml
public class FriendsViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG = "FriendsViewHolder";

    //widgets
    public ImageView mProfilePic;
    public TextView mUserName;
    public TextView mPhoneNumber;
    public FancyButton mUnfriendButton;
    public FancyButton mAddBuddyButton;

    private User mUser;

    //constructor - wires up all the widgets
    public FriendsViewHolder(View view){
        super(view);

        mProfilePic = (ImageView)view.findViewById(R.id.image_view_profile_pic_friends_view_holder);
        mUserName = (TextView)view.findViewById(R.id.text_view_friend_name_friends_view_holder);
        mUnfriendButton = (FancyButton) view.findViewById(R.id.button_unfriend_view_holder);
        mPhoneNumber = view.findViewById(R.id.text_view_friend_phone_friends_view_holder);
        mAddBuddyButton = (FancyButton) view.findViewById(R.id.button_add_buddy_view_holder);

        final AppCompatActivity c = (AppCompatActivity)view.getContext();

    }

    //bind a user to the viewHolder
    public void bindUser(final User user){
        mUser = user;

        //set up the picture
        String mPath = user.getProfilePic();
        if (!mPath.matches("none")){
            Bitmap photo = Utils.getScaledBitmap(mPath, 200, 200);
            mProfilePic.setImageBitmap(photo);
            mProfilePic.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        mUserName.setText(user.getFirstname()+" "+user.getLastname());
        mPhoneNumber.setText(user.getPhoneNumber());
        final FirebaseDAO dao = new FirebaseDAO();
        final Friend[] friend = new Friend[1]; // nasty hack to access inner class
        final DatabaseReference friendEntry = dao.getFriendsDatabase().child(Utils.getUid()).child(user.getID());
        friendEntry.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Friend f = dataSnapshot.getValue(Friend.class);
                if(f != null) {
                    //process based on friend status
                    String displayText = "";
                    //TODO: replace these with strings in string.xml
                    friend[0] = f;
                    switch (friend[0].getFriendStatus()) {
                        case Friend.PENDING:
                            displayText = "Accept request";
                            break;
                        case Friend.FRIEND:
                            displayText = "Buddy Up";
                            break;
                        case Friend.BUDDY:
                            displayText = "Cancel Buddy";
                            break;
                        default:
                            displayText = "Buddy up";
                    }
                    mAddBuddyButton.setText(displayText);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        mAddBuddyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                friendEntry.child("friendStatus").setValue(2);//TODO: finalize logic here
                Log.d(TAG,"onClick() with user "+user.getFirstname()+user.getLastname());
                Toast.makeText(view.getContext(), "test",Toast.LENGTH_LONG);

            }
        });


        mUnfriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"mUnfriendButton onClick()");
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage(R.string.unfriend_message)
                        .setTitle(R.string.unfriend)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dao.deleteFriend(Utils.getUid(),friend[0].getfriendID());
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
