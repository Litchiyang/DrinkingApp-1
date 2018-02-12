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
        //get all users friends
        final DatabaseReference friendEntry = dao.getFriendsDatabase().child(Utils.getUid()).child(user.getID());
        friendEntry.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Friend f = dataSnapshot.getValue(Friend.class);
                String displayText = "";
                friend[0] = f;
                if(f == null) {
                    displayText = "Add friend";
                    mAddBuddyButton.setText(displayText);
                    mUnfriendButton.setVisibility(View.INVISIBLE);
                }
                else{
                    //process based on friend status
                    //TODO: replace these with strings in string.xml
                    switch (friend[0].getFriendStatus()) {
                        case Friend.PENDING:
                            displayText = "Cancel Request";
                            break;
                        case Friend.RECEIVED:
                            displayText = "Accept Request";
                            break;
                        case Friend.FRIEND:
                            displayText = "Buddy Up";
                            break;
                        case Friend.BUDDY:
                            displayText = "Cancel Buddy";
                            break;
                        default:
                    }
                        int status = friend[0].getFriendStatus();
                        if (status == Friend.PENDING || status == Friend.RECEIVED) {
                            mUnfriendButton.setVisibility(View.INVISIBLE);
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
                if(friend[0]==null ){
                    //send Friend request
                    //set status to pending on user side
                    friendEntry.child("friendID").setValue(user.getID());
                    friendEntry.child("friendStatus").setValue(Friend.PENDING);
                    //register request on friends side
                    dao.getFriendsDatabase().child(user.getID()).child(Utils.getUid()).child("friendID").setValue(Utils.getUid());
                    dao.getFriendsDatabase().child(user.getID()).child(Utils.getUid()).child("friendStatus").setValue(Friend.RECEIVED);
                }
                else{
                    switch(friend[0].getFriendStatus()) {
                        case Friend.PENDING:        // cancle friend request
                            dao.deleteFriend(Utils.getUid(),user.getID());
                            dao.deleteFriend(user.getID(),Utils.getUid());
                            break;
                        case Friend.RECEIVED:       // accept friend request
                            mUnfriendButton.setVisibility(View.VISIBLE);
                            friendEntry.child("friendStatus").setValue(Friend.FRIEND);
                            dao.getFriendsDatabase().child(user.getID()).child("friendStatus").setValue(Friend.FRIEND);
                            break;
                        case Friend.FRIEND:         // promote to buddy
                            friendEntry.child("friendStatus").setValue(Friend.BUDDY);
                            break;
                        case Friend.BUDDY:          // demote to friend
                            friendEntry.child("friendStatus").setValue(Friend.FRIEND);
                            break;
                        default:
                }
                }
                Log.d(TAG,"mAddBuddyButton onClick() with user "+user.getID());
            }
        });

        mUnfriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"mUnfriendButton onClick() with user"+user.getID());
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage(R.string.unfriend_message)
                        .setTitle(R.string.unfriend)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dao.deleteFriend(Utils.getUid(),friend[0].getfriendID());
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
