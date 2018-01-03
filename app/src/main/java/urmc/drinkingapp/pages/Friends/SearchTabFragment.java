package urmc.drinkingapp.pages.Friends;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;
import urmc.drinkingapp.R;
import urmc.drinkingapp.control.FirebaseDAO;
import urmc.drinkingapp.model.User;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

/**
 * Fragment displaying a SearchView to search for users on the database
 */
public class SearchTabFragment extends Fragment {
    private static final String TAG = "SearchTabFragment";
    private FirebaseDAO dao;

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

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)view.findViewById(R.id.search_view_friends);
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG,"onQueryTextSubmit");
                Query keyQuery = dao.getUserDatabase().orderByChild("mFirstname").equalTo(query);
                FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<User>()
                        .setIndexedQuery(keyQuery,dao.getUserDatabase(),User.class).build();




                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG,"onQueryTextChange");

                return true;
            }
        });
        return view;
    }


}
