package redeemsystems.com.home;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DisclaimerFragment extends DialogFragment {
    TextView disclaim;

    public DisclaimerFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = null;
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setTitle("Disclaimer");
        // Inflate the layout for this fragment
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_disclaimer, null);
        disclaim = v.findViewById(R.id.disclaimer);
        disclaim.setText("We, the Operators of this Website, provide it as a public service to our users." +
                "\n" +"Please carefully review the following basic rules that govern your use of the Website. " +
                "Please note that your use of the Website constitutes your unconditional agreement to follow and " +
                "be bound by these Terms and Conditions of Use. " +
                "If you (the \"User\") do not agree to them, do not use the Website, " +
                "provide any materials to the Website or download any materials from them." +
                "\n" +"The Operators reserve the right to update or modify these Terms and Conditions at " +
                "any time without prior notice to User. Your use of the Website following any such change constitutes your" +
                " unconditional agreement to follow and be bound by these Terms and Conditions as changed." +
                " For this reason, we encourage you to review these Terms and Conditions of Use whenever you use the Website." +
                "\n"+"These Terms and Conditions of Use apply to the use of the Website and do not extend " +
                "to any linked third party sites. These Terms and Conditions and our Privacy Policy, " +
                "which are hereby incorporated by reference, contain the entire agreement (the “Agreement”) " +
                "between you and the Operators with respect to the Website. Any rights not expressly granted herein are reserved.");
        ab.setView(v);
        d = ab.create();
        return d;
    }


}
