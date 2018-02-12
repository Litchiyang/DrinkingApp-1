package urmc.drinkingapp.pages.Friends;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;
import urmc.drinkingapp.R;
import urmc.drinkingapp.control.FirebaseDAO;
import urmc.drinkingapp.model.User;
import urmc.drinkingapp.pages.FriendsViewHolder;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

/**
 * Fragment displaying a SearchView to search for users on the database
 */
public class SearchTabFragment extends Fragment {
    private static final String TAG = "SearchTabFragment";
    private FirebaseDAO dao;
    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter mAdapter;
    public SearchTabFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dao = new FirebaseDAO();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_full_screen_search_tab, container, false);
        Log.d(TAG,"onCreateView");
        mRecyclerView = view.findViewById(R.id.recycler_view_search_result);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)view.findViewById(R.id.search_view_friends);
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG,"onQueryTextSubmit:"+query);
                Query keyQuery = dao.getUserDatabase().orderByChild("firstname").equalTo(query);
                FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<User>()
                        .setIndexedQuery(keyQuery,dao.getUserDatabase(),User.class).build();
                mAdapter = new FirebaseRecyclerAdapter<User, FriendsViewHolder>(options) {
                    @Override
                    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.friends_view_holder_add_buddy, parent, false);
                        return new FriendsViewHolder(view);
                    }
                    @Override
                    protected void onBindViewHolder(FriendsViewHolder holder, int position, User model) {
                        Log.d(TAG,"binding"+ model.getFirstname());
                        holder.bindUser(model);
                    }
                };
                mRecyclerView.setAdapter(mAdapter);

                mAdapter.startListening();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return view;
    }


}
