package redeemsystems.com.home;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static redeemsystems.com.home.Forgotpassword.phoneNumber;


/**
 * A simple {@link Fragment} subclass.
 */
public class Changepassword extends Fragment {

    TextInputLayout textinputoldpwd,textinputnewpwd,textinputconnewpwd;
    TextView useremail,userphone;
    Button save;
    String  newPw, confirmPw,oldpw;


    public Changepassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_changepassword, container, false);
        textinputoldpwd = view.findViewById(R.id.oldpwd);
        textinputnewpwd = view.findViewById(R.id.newpwd);
        textinputconnewpwd = view.findViewById(R.id.confpwd);
        useremail = view.findViewById(R.id.useremail);
        userphone = view.findViewById(R.id.userphone);
        save = view.findViewById(R.id.save);

        userphone.setText(MainActivity.loginPhone);
        useremail.setText(MainActivity.loginEmail);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!validateoldpwd() || !validatenewpwd()|| !validateconfnewpwd()){
                    return;
                }
                oldpw=textinputoldpwd.getEditText().getText().toString().trim();
                newPw = textinputnewpwd.getEditText().getText().toString().trim();
                confirmPw = textinputconnewpwd.getEditText().getText().toString().trim();


                if(!oldpw.equals(MainActivity.loginPassword)){
                    Toast.makeText(getActivity(), "This is not old password", Toast.LENGTH_SHORT).show();
                }

                if(!newPw.equals(confirmPw)){
                    Toast.makeText(getActivity(), "Password mismatch", Toast.LENGTH_SHORT).show();
                }

                if(oldpw.equals(MainActivity.loginPassword) && newPw.equals(confirmPw)){
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.myDatabase.updatepassword(MainActivity.loginPhone,newPw);
                    mainActivity.getSupportFragmentManager().popBackStack("account", 0);
                    Toast.makeText(getActivity(), "Change Password sucessfully", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(getActivity(), "can't change password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;

    }

    private  boolean validateoldpwd(){
        String oldpwdinput=textinputoldpwd.getEditText().getText().toString().trim();

        if(oldpwdinput.isEmpty()){
            textinputoldpwd.setError("Field Can't be Empty");
            return false;
        }
        else {
            textinputoldpwd.setError(null);
            return true;
        }
    }
    private  boolean validatenewpwd(){
        String newpwdinput=textinputnewpwd.getEditText().getText().toString().trim();

        if(newpwdinput.isEmpty()){
            textinputnewpwd.setError("Field Can't be Empty");
            return false;
        }
        else {
            textinputnewpwd.setError(null);
            return true;
        }
    }
    private  boolean validateconfnewpwd(){
        String confnewpwdinput=textinputconnewpwd.getEditText().getText().toString().trim();

        if(confnewpwdinput.isEmpty()){
            textinputconnewpwd.setError("Field Can't be Empty");
            return false;
        }
        else {
            textinputconnewpwd.setError(null);
            return true;
        }
    }

}
