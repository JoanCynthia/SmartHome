package redeemsystems.com.home;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    RadioButton admin, technician,user;
    RadioGroup group;
    TextInputLayout login_details1, password;
    TextView  register;
    TextView forgotPassword;
    String isAdmin;
    MainActivity mainActivity;
    Button login;
    String login_details,password1;
    Cursor cursor;
    CheckBox remember_me;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;



    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_login, container, false);
        group = view.findViewById(R.id.group);
        admin = (RadioButton) view.findViewById(R.id.radio1);
        technician = (RadioButton) view.findViewById(R.id.radio2);
        user = view.findViewById(R.id.radio3);
        login_details1 = view.findViewById(R.id.login_details);
        password = view.findViewById(R.id.password);
        forgotPassword=view.findViewById(R.id.forgotpwd);
        register = view.findViewById(R.id.register);
        login = view.findViewById(R.id.login);
        mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportActionBar().hide();
        remember_me = view.findViewById(R.id.remember);


        loginPreferences = getActivity().getSharedPreferences( "loginPrefs", MODE_PRIVATE );
        loginPrefsEditor = loginPreferences.edit();
        saveLogin = loginPreferences.getBoolean( "saveLogin", false );
        if (saveLogin == true) {
            login_details1.getEditText().setText( loginPreferences.getString( "username", "" ) );
            password.getEditText().setText( loginPreferences.getString( "password", "" ) );
            remember_me.setChecked( true );
        }



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity mainActivity= (MainActivity) getActivity();
                mainActivity.loadregister();
                mainActivity = (MainActivity) getActivity();
                mainActivity.getSupportActionBar().setTitle("Login");

            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity= (MainActivity) getActivity();
                mainActivity.loadforgotfragment();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("joan", "entered");
                if (!validatelogindetails() || !validatepwd()) {
                    return;
                }
                int type = group.getCheckedRadioButtonId();
                if (type == R.id.radio1) {
                    isAdmin = "Admin";
                } else if (type == R.id.radio2) {
                    isAdmin = "Technician";
                } else if (type == R.id.radio3) {
                    isAdmin = "User";
                } else {
                    Toast.makeText(getActivity(), "Select if you are admin or technician or user", Toast.LENGTH_SHORT).show();
                    return;
                }


                login_details = login_details1.getEditText().getText().toString().trim();
                password1 = password.getEditText().getText().toString().trim();
                if (login_details.equals("") || password1.equals("")) {
                    Toast.makeText(mainActivity, "Fields can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (RegisterFragment.flag) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_LOGIN, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                              Log.d("joan", response);
//                            String resp = response.substring(response.indexOf("{"), response.length());
//                            String resp1 = resp.substring(resp.indexOf("main"), resp.length());
//                            String resp2 = resp1.substring(resp1.indexOf("{"), resp1.length());
//                            Log.d("nike", resp2);

                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString("status");
                                String user_type = jsonObject.getString("user_type");
                                String user_name = jsonObject.getString("user_name");
                                String email = jsonObject.getString("email");
                                String password = jsonObject.getString("password");
                                String phone = jsonObject.getString("phone_number");
                                //byte[] image = jsonObject.get("image");

                                MainActivity.loginName = user_name;
                                MainActivity.loginEmail = email;
                                //MainActivity.loginImage = image;
                                MainActivity.loginPhone = phone;
                                MainActivity.loginPassword = password;
                                MainActivity.loginUser = user_type;

                                Log.d("joan", message);
                                if (message.equals("ok")) {
                                    mainActivity.loadListDevices();

                                } else if (message.equals("failed")) {
                                    Toast.makeText(mainActivity, "Login failed", Toast.LENGTH_SHORT).show();
                                    return;
                                } else if (message.equals("error")) {
                                    Toast.makeText(mainActivity, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("nike", "exception " + e.toString());
                            }
                        }
                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(mainActivity, error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("username", login_details);
                                params.put("password", password1);
                                return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                    requestQueue.add(stringRequest);
                } else {

                    try {

                        if (mainActivity.myDatabase != null) {

                            Log.d("joan", "entered");

                            if (login_details.contains(".com")) {
                                Log.d("joan", "entered");


                                cursor = mainActivity.myDatabase.queryRegisterEmail(login_details);
                            } else {
                                cursor = mainActivity.myDatabase.queryRegisterMobile(login_details);
                                Log.d("joan", "entered");

                            }
                            if (cursor != null && cursor.getCount() > 0) {
                                Log.d("joan", "entered");
                                while (cursor.moveToNext()) {
                                    Log.d("joan", "entered");

                                    //String user_name_regd = cursor.getString(3);
                                    String type1 = cursor.getString(1);
                                    String password_regd = cursor.getString(4);
                                    String phone = cursor.getString(5);
                                    String email = cursor.getString(3);
                                    String name = cursor.getString(2);
                                    byte[] image = cursor.getBlob(6);
                                    MainActivity.loginDetail = login_details;
                                    if ((isAdmin.equalsIgnoreCase(type1)) && (login_details.equals(email) || login_details.equals(phone)) && password1.equals(password_regd)) {
//                                Toast.makeText(getActivity(), "Login Success", Toast.LENGTH_SHORT).show();

                                        if (remember_me.isChecked()) {
                                            loginPrefsEditor.putBoolean("saveLogin", true);
                                            loginPrefsEditor.putString("username", login_details);
                                            loginPrefsEditor.putString("password", password1);
                                            loginPrefsEditor.commit();
                                        } else {
                                            loginPrefsEditor.clear();
                                            loginPrefsEditor.commit();
                                        }
                                        MainActivity.loginName = name;
                                        MainActivity.loginEmail = email;
                                        MainActivity.loginImage = image;
                                        MainActivity.loginPhone = phone;
                                        MainActivity.loginPassword = password_regd;
                                        MainActivity.loginUser = type1;

//                                mainActivity = (MainActivity) getActivity();
                                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putString("username", name);
                                        editor.putString("email", email);
//                                    editor.("image", image);
                                        editor.putString("phone", phone);
                                        editor.putString("password", password_regd);
                                        editor.putString("usertype", type1);
                                        editor.commit();
                                        mainActivity.loadListDevices();
//                                mainActivity.loadHome();
                                    } else {
                                        Toast.makeText(getActivity(), "Enter valid details and try again", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "Please register first", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    } catch (Throwable e) {
                        Log.d("joan", e.toString());
                    }
                }
            }
        });

        return view;
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
    private  boolean validatelogindetails(){
        String logininput=login_details1.getEditText().getText().toString().trim();

        if(logininput.isEmpty()){
            login_details1.setError("Field Can't be Empty");
            return false;
        }
        else {
            login_details1.setError(null);
            return true;
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity.finish();
    }
}
