package urmc.drinkingapp.pages;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import mehdi.sakout.fancybuttons.FancyButton;
import urmc.drinkingapp.R;
import urmc.drinkingapp.model.User;

//no longer used
public class BuddiesViewHolder extends RecyclerView.ViewHolder {
    public ImageView mProfilePic;
    public TextView mUserName;
    public FancyButton mAddFriendButton;
    public FancyButton mAddBuddyButton;

    private User mUser;

    //constructor - wires up all the widgets
    public BuddiesViewHolder(View view){
        super(view);
        //Wire Widgets
        mProfilePic = (ImageView)view.findViewById(R.id.image_view_profile_pic_buddies_view_holder);
        mUserName = (TextView)view.findViewById(R.id.text_view_friend_name_buddies_view_holder);
        mAddFriendButton = (FancyButton) view.findViewById(R.id.button_add_friends_buddy_view_holder);
        mAddBuddyButton = (FancyButton) view.findViewById(R.id.button_add_buddies_buddy_view_holder);

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
        mUserName.setText(user.getFirstname()+" "+user.getLastname());
    }

}
