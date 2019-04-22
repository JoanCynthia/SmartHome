package redeemsystems.com.home;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class SucessDialog extends DialogFragment
{
    static Dialog d;


    public SucessDialog() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        d=null;
        AlertDialog.Builder ab=new AlertDialog.Builder(getActivity());
        View view= getActivity().getLayoutInflater().inflate(R.layout.fragment_sucess_dialog,null);
        ab.setView(view);
        d=ab.create();
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return d;
    }



//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view= inflater.inflate(R.layout.fragment_sucess_dialog, container, false);
//        return view;
//    }

}
