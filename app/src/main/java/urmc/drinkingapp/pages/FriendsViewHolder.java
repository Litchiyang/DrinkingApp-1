package urmc.drinkingapp.pages;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import mehdi.sakout.fancybuttons.FancyButton;
import urmc.drinkingapp.R;
import urmc.drinkingapp.model.User;

/**
 * Created by Alessandro on 3/10/17.
 */

/**
 * View holder for the recycler view displaying all friends
 */

public class FriendsViewHolder extends RecyclerView.ViewHolder{

    //widgets
    public ImageView mProfilePic;
    public TextView mUserName;
    public FancyButton mAddFriendButton;
    public FancyButton mAddBuddyButton;

    private User mUser;

    //constructor - wires up all the widgets
    public FriendsViewHolder(View view){
        super(view);
        //Wire Widgets
        mProfilePic = (ImageView)view.findViewById(R.id.image_view_profile_pic_friends_view_holder);
        mUserName = (TextView)view.findViewById(R.id.text_view_friend_name_friends_view_holder);
        mAddFriendButton = (FancyButton) view.findViewById(R.id.button_add_friend_view_holder);
        //Do this null check because from the search results the user cannot add buddies because the resulting users are not
        //necessarily friends. The user can only add buddies if they're already friends. So only in that case they AddBuddy button will
        //exists
        if (view.findViewById(R.id.button_add_buddy_view_holder)!= null){
            mAddBuddyButton = (FancyButton) view.findViewById(R.id.button_add_buddy_view_holder);
        }
        final AppCompatActivity c = (AppCompatActivity)view.getContext();
        //mCollection = DrinkingAppCollection.get(c);

        //when an item in the recyclerView is pressed the ExpandedProfileActivity is launched
        /*
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, ExpandedProfileActivity.class);
                intent.putExtra("EMAIL", mUser.getEmail());
                c.startActivity(intent);
            }
        });*/
    }

    //bind a user to the viewHolder
    public void bindUser(User user){
        mUser = user;

        /*
        //set up the picture
        String mPath = user.getProfilePic();
        if (!mPath.matches("none")){
            Bitmap photo = User.getScaledBitmap(mPath, 200, 200);
            mProfilePic.setImageBitmap(photo);
            mProfilePic.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }*/

        //set all the other attributes
        mUserName.setText(user.getFullname());

    }
}
