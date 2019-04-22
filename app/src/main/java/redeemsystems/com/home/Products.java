package redeemsystems.com.home;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Products extends Fragment
{
    LinearLayout moc;
    MainActivity mainActivity;
    SearchView searchView;
    TextView search;

    public Products() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        moc = view.findViewById(R.id.moc);

        searchView = view.findViewById(R.id.search);
        search = view.findViewById(R.id.textView);
        mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportActionBar().setTitle("Products");

        moc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.loadMOC();
            }
        });
//        USE THIS WHEN PRODUCTS ARE ADDED TO SERVER
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setVisibility(View.GONE);

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


//                myAdapter.getFilter().filter(newText);
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                search.setVisibility(View.VISIBLE);
                return false;
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity.getSupportFragmentManager().popBackStack("home", 2);
    }
}
