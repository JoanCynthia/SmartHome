package redeemsystems.com.home;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import static redeemsystems.com.home.Forgotpassword.phoneNumber;


/**
 * A simple {@link Fragment} subclass.
 */
public class Resetpassword extends Fragment {
    TextInputLayout otp,password,confpassword;
    Button resetpwd;
    String OTP, newPw, confirmPw;
    MainActivity mainActivity;

    public Resetpassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_resetpassword, container, false);
        mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportActionBar().show();
        mainActivity.getSupportActionBar().setTitle("Reset Password");
        otp=view.findViewById(R.id.otp);
        password=view.findViewById(R.id.pwd);
        confpassword=view.findViewById(R.id.cpwd);
        resetpwd=view.findViewById(R.id.resetpassword);
        resetpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateotp() || !validatepwd() || !validatecpwd()) {
                    return;
                }
                OTP = otp.getEditText().getText().toString().trim();
                newPw = password.getEditText().getText().toString().trim();
                confirmPw = confpassword.getEditText().getText().toString().trim();
                if(!OTP.equals(Forgotpassword.captcha1+""))
                {
                    Toast.makeText(getActivity(), "You are not allowed to reset password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPw.equals(confirmPw)) {
                    mainActivity.myDatabase.updatepassword(phoneNumber,newPw);
                    mainActivity.getSupportFragmentManager().popBackStack("frogotPassword", 0);
                    Toast.makeText(getActivity(), "Reset Password sucessfully", Toast.LENGTH_SHORT).show();
                }
            }
        } );
        return view;
    }
    private  boolean validateotp(){
        String otpinput=otp.getEditText().getText().toString().trim();

        if(otpinput.isEmpty()){
            otp.setError("Field Can't be Empty");
            return false;
        }
        else {
            otp.setError(null);
            return true;
        }
    }
    private  boolean validatepwd(){
        String pwdinput=password.getEditText().getText().toString().trim();

        if(pwdinput.isEmpty()){
            password.setError("Field Can't be Empty");
            return false;
        }
        else if(pwdinput.length()<8){
            password.setError("Password not less than 8 letter");
            return false;
        }
        else if(pwdinput.length()>16){
            password.setError("Password too long");
            return false;
        }
        else {
            password.setError(null);
            return true;
        }
    }
    private  boolean validatecpwd(){
        String cpwdinput=confpassword.getEditText().getText().toString().trim();

        if(cpwdinput.isEmpty()){
            confpassword.setError("Field Can't be Empty");
            return false;
        }
        else if(cpwdinput.length()<8){
            confpassword.setError("Password not less than 8 letter");
            return false;
        }
        else if(cpwdinput.length()>16){
            confpassword.setError("Confirm Password too long");
            return false;
        }
        else {
            confpassword.setError(null);
            return true;
        }
    }

}
