package redeemsystems.com.home;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class Complaint_Heading extends Fragment {
    MainActivity mainActivity;


    public Complaint_Heading() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_complaint__heading, container, false);
        mainActivity = (MainActivity) getActivity();
        mainActivity.loadComplaint_Heading();
        return view;
    }

}
