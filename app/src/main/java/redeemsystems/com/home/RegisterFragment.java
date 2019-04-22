package redeemsystems.com.home;


import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;

import static redeemsystems.com.home.Forgotpassword.phoneNumber;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    TextInputLayout username,email,password,confpassword,phone,otp;
    RadioGroup radioGroup;
    RadioButton admin,technician,user;
//    Spinner spinner;
//    CountryCodes countryCodes;
    TextView verify,disclamier;
//    EditText countryCode;
    CheckBox checkBox;
    String isAdmin;
    String user_name;
    String email_id;
    String pwd;
    boolean checkEmail, checkPw;
    Pattern pEmail, pPw;
    Matcher mEmail, mPw;
    String c_pwd;
    String phonenum;
    ProgressBar spinner1;
//    String code;
    int captcha1;
    MainActivity mainActivity;
    static boolean flag = false;
    String message;

    Button register;
    public RegisterFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_register, container, false);
        username = v.findViewById(R.id.user_name);
        email = v.findViewById(R.id.email_id);
        password = v.findViewById(R.id.password);
        confpassword = v.findViewById(R.id.conf_password);
        phone = v.findViewById(R.id.phone_num);
//        spinner = v.findViewById(R.id.spinner);
        otp= v.findViewById(R.id.otp);
        register= v.findViewById(R.id.register);
        verify= v.findViewById(R.id.verify);
        disclamier=v.findViewById(R.id.terms);
        mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportActionBar().hide();
        //checkBox = v.findViewById(R.id.agree);
        radioGroup = v.findViewById(R.id.radiogrp);
        admin = v.findViewById(R.id.admin);
        technician = v.findViewById(R.id.technician);
        user = v.findViewById(R.id.user);
        spinner1=v.findViewById(R.id.spinner1);

//        countryCode = v.findViewById(R.id.countryCode);
//        countryCodes = new CountryCodes(getActivity());
//        spinner.setAdapter( countryCodes );

//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                code = countryCodes.getCode(position);
//                countryCode.setText(code);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        disclamier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();

                mainActivity.loadDisclaimer();
            }
        });


        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String c_code = countryCode.getText().toString().trim();
                final String phoneNumber1 = phone.getEditText().getText().toString().trim();
//                String finalPhoneNumber = c_code+phoneNumber1;

                if(phoneNumber1.equals(""))
                {
                    Toast.makeText(mainActivity, "Enter phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
//                if(c_code.equals(""))
//                {
//                    Toast.makeText(mainActivity, "Select country code", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if(phoneNumber1.length() < 6 || phoneNumber1.length() > 13)
                {
                    Toast.makeText(getActivity(), "Enter valid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                captcha1 = (int) (Math.random()*10000);
                try {
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(phoneNumber1, null, "Thank you for registering with us.This is ur OTP "+captcha1+". Please don't share with anyone", null, null);
                    Toast.makeText(getActivity(), captcha1+" ", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Log.d("sms",e.toString());
                }
                catch (OutOfMemoryError e)
                {
                    e.printStackTrace();
                    Log.d("exception", e.toString());
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                    Log.d("exception", e.toString());
                }

                new CountDownTimer(5000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        verify.setText("" + millisUntilFinished / 1000);
                        spinner1.setVisibility(View.VISIBLE);
                    }

                    public void onFinish()
                    {
                        spinner1.setVisibility(View.GONE);
                        verify.setText("Sent");
                        Toast.makeText(getActivity(), "If you didn't receive captcha" +
                                " please check phone number and try again", Toast.LENGTH_SHORT).show();
                    }
                }.start();
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int type = radioGroup.getCheckedRadioButtonId();
                if(type == R.id.admin)
                {
                  isAdmin="Admin";
                }
                else if(type == R.id.technician)
                {
                    isAdmin="Technician";
                }
                else if(type == R.id.user){
                    isAdmin="User";
                }
                else
                {
                    Toast.makeText(getActivity(), "Select if you are admin or technician or user", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!validatefullname()||!validateemail() ||!validatepwd()||!validatecpwd()||!validatephone()||!validateotp()){
                    return;
                }

                user_name = username.getEditText().getText().toString().trim();
                email_id = email.getEditText().getText().toString().trim();
                pwd = password.getEditText().getText().toString().trim();
                c_pwd = confpassword.getEditText().getText().toString().trim();
                String cpw = confpassword.getEditText().getText().toString().trim();
//                String c_code = countryCode.getText().toString().trim();
                phonenum = phone.getEditText().getText().toString().trim();
                String cap = otp.getEditText().getText().toString().trim();
                if (pwd.equals(c_pwd)) {
//                    return;
//                    mainActivity.myDatabase.updatepassword(phoneNumber,newPw);
//                    mainActivity.getSupportFragmentManager().popBackStack("frogotPassword", 0);
//                    Toast.makeText(getActivity(), "Reset Password sucessfully", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "Password mismatch", Toast.LENGTH_SHORT).show();
                }
                if(user_name.equals("") || email.equals("") || password.equals("") || confpassword.equals("")){

                    Toast.makeText(getActivity(), "Fields cant be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

                pEmail = Pattern.compile(EMAIL_STRING);

                mEmail = pEmail.matcher(email_id);
                checkEmail = mEmail.matches();
                String PW_STRING = "^[ A-Za-z0-9_@./#&+-]*$";
                pPw = Pattern.compile(PW_STRING);
                mPw = pPw.matcher(pwd);
//                checkPw = mPw.matches();

                 checkPw=true;//Temp
                if(user_name.length() > 70)
                {
                    Toast.makeText(getActivity(), "User name should not exceed 70 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!checkEmail) {
                    email.setError("Not Valid Email");
                    return;
                }
                if(!checkPw)
                {
                    password.setError("Not Valid Password");
                    return;
                }
                if(pwd.length() < 8 || pwd.length() > 20)
                {
                    Toast.makeText(getActivity(), "Password should not be less than 8 or more than 20 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!pwd.equals(c_pwd))
                {
                    Toast.makeText(getActivity(), "Invalid Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                int cap1 = Integer.parseInt(cap);
                if(!(cap1==captcha1)){
                    Toast.makeText(getActivity(), "Invalid Captcha", Toast.LENGTH_SHORT).show();
                    return;
                }

//                if(!checkBox.isChecked()){
//                    Toast.makeText(getActivity(), "Select the Checkbox", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                Log.d("joan", "entered");
                mainActivity.myDatabase.insertregister(isAdmin,user_name,email_id,pwd, phonenum, null);
//
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_REGISTER, new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("nike", response);
                        //String resp = response.substring(response.indexOf("{"), response.length());


                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            message = jsonObject.getString("message");
                        }

                        catch (JSONException e)
                        {
                            String resp1 = response.substring(response.indexOf("main"), response.length());
                            String resp2 = resp1.substring(resp1.indexOf("{"), resp1.length());
                            Log.d("nike", resp2);
                            try {
                                JSONObject jsonObject = new JSONObject(resp2);
                                message = jsonObject.getString("message");
                            }
                            catch (JSONException exc)
                            {
                                Log.d("nike", exc.toString());
                            }
                        }
                        if(message.equals("ok")) {
                            mainActivity.loaddialog();
                            flag = true;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mainActivity.loadlogin();
                                    SucessDialog.d.dismiss();
                                }
                            }, 2000);

                        }
                        else if(message.equals("exist"))
                        {
                            Toast.makeText(mainActivity, "User already exist", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(message.equals("error"))
                        {
                            Toast.makeText(mainActivity, "Something went wrong", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                Toast.makeText(mainActivity,error.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("user_type",isAdmin);
                        params.put("user_name",user_name);
                        params.put("email",email_id);
                        params.put("password",pwd);
                        params.put("phone_number",phonenum);
                        //params.put("image",null);

                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                requestQueue.add(stringRequest);
//                Toast.makeText(mainActivity, "Insertd Sucessfully", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }
    private  boolean validatefullname(){
        String fullnameinput=username.getEditText().getText().toString().trim();

        if(fullnameinput.isEmpty()){
            username.setError("Field Can't be Empty");
            return false;
        }
        else if(fullnameinput.length()>70) {
            username.setError("Full Name too long");
            return false;
        }
        else {
            username.setError(null);
            return true;
        }
    }
    private  boolean validateemail(){
        String emailinput = email.getEditText().getText().toString().trim();

        if(emailinput.isEmpty()){
            email.setError("Field Can't be Empty");
            return false;
        }
        else if(emailinput.length()>70) {
            email.setError("Full Name too long");
            return false;
        }
        else {
            email.setError(null);
            return true;
        }
    }
    private  boolean validatephone(){
        String phoneinput=phone.getEditText().getText().toString().trim();

        if(phoneinput.isEmpty()){
            phone.setError("Field Can't be Empty");
            return false;
        }
        else if(phoneinput.length()>10){
            phone.setError("Phone number too long");
            return false;
        }
        else if(phoneinput.length()<10){
            phone.setError("Phone number not less than 10 digit");
            return false;
        }
        else {
            phone.setError(null);
            return true;
        }
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
