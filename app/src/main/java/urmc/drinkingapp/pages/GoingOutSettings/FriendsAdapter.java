package urmc.drinkingapp.pages.GoingOutSettings;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import urmc.drinkingapp.R;
import urmc.drinkingapp.model.User;
import urmc.drinkingapp.pages.FriendsViewHolder;

/**
 * Created by Alessandro on 3/10/17.
 */

/**
 * Helper adapter to display a list of friends in a recycler list
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsViewHolder> {

    //list of user
    private List<User> mItems;

    //constructor
    public FriendsAdapter(List<User> items){mItems = items;}

    //creating a view holder for the recycler view
    @Override
    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.friends_view_holder, parent, false);
        FriendsViewHolder holder = new FriendsViewHolder(view);
        return holder;
    }

    //overriding the function to bind to a specific Event
    @Override
    public void onBindViewHolder(FriendsViewHolder holder, int position) {
        holder.bindUser(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0: mItems.size();
    }

    public void setUsers(List<User> users){
        mItems = users;
    }
}
