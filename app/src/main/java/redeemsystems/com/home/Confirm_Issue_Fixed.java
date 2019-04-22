package redeemsystems.com.home;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class Confirm_Issue_Fixed extends Fragment {
    MainActivity mainActivity;


    public Confirm_Issue_Fixed() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm__issue__fixed, container, false);
        mainActivity = (MainActivity) getActivity();
        mainActivity.loadConfirm_Issue_Fixed();
        return view;
    }

}
