package redeemsystems.com.home;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import static android.os.Build.VERSION_CODES.O;


/**
 * A simple {@link Fragment} subclass.
 */
public class Forgotpassword extends Fragment {

    TextInputLayout textInputemail,textInputphonenum;
    Button submit;
    static  int captcha1;
    static String phoneNumber;
    MainActivity mainActivity;

    public Forgotpassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_forgotpassword, container, false);
        textInputemail=view.findViewById(R.id.email);
        textInputphonenum=view.findViewById(R.id.phone);
        mainActivity= (MainActivity) getActivity();
        submit=view.findViewById(R.id.resetpassword);
        mainActivity.getSupportActionBar().show();
        mainActivity.getSupportActionBar().setTitle("Reset Password");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateemail() || !validatephone()){
                    return;
                }
                phoneNumber = textInputphonenum.getEditText().getText().toString().trim();

                captcha1 = (int) (Math.random()*10000);
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(phoneNumber, null, ""+captcha1, null, null);
                    Toast.makeText(getActivity(), captcha1+" ", Toast.LENGTH_LONG).show();


                new CountDownTimer(5000, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish()
                    {
                        Toast.makeText(getActivity(), "If you didn't receive captcha" +
                                " please check phone number and try again", Toast.LENGTH_SHORT).show();
                    }
                }.start();

                mainActivity.loadresetpwd();
            }
        });
        return view;
    }

    private  boolean validateemail(){
        String emailinput=textInputemail.getEditText().getText().toString().trim();

        if(emailinput.isEmpty()){
            textInputemail.setError("Field Can't be Empty");
            return false;
        }
        else {
            textInputemail.setError(null);
            return true;
        }
    }
    private  boolean validatephone(){
        String phoneinput=textInputphonenum.getEditText().getText().toString().trim();

        if(phoneinput.isEmpty()){
            textInputphonenum.setError("Field Can't be Empty");
            return false;
        }
        else if(phoneinput.length()>10){
            textInputphonenum.setError("Phone number too long");
            return false;
        }
        else if(phoneinput.length()<10){
            textInputphonenum.setError("Phone number not less than 10 digit");
            return false;
        }
        else {
            textInputphonenum.setError(null);
            return true;
        }
    }


}
