package redeemsystems.com.home;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    MyDatabase myDatabase;
    ArrayList<String> arrayList;
    static String loginDetail;
    static final int REQUEST_CODE = 3;
    static String loginName, loginPhone, loginEmail, loginUser, loginPassword;
    static NavigationView navigationView;
    CircleImageView imageView;
    static byte[] loginImage;
    TextView userName;
    //public  static ApiInterface apiInterface;

//    LinearLayout account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAccount();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });
//        account = findViewById(R.id.account);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA},REQUEST_CODE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        myDatabase = new MyDatabase(this);
        myDatabase.open();
        //apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        //~Temporary
        arrayList = new ArrayList<String>();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = settings.getString("username", "");
        String email = settings.getString("email", "");
        String phone = settings.getString("phone", "");
        String password = settings.getString("password", "");
        String userType = settings.getString("usertype", "");

        if(username != null && username.length() > 1){
            loginName = username;
            loginEmail = email;
//            loginImage = image;
            loginPhone = phone;
            loginPassword = password;
            loginUser = userType;
            loadListDevices();

        }else {
            loadlogin();
        }
//        account.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadAccount();
//            }
//        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            getSupportFragmentManager().popBackStack("home", 2);
//            return true;
        } else if (id == R.id.inbox) {

        } else if (id == R.id.products) {
//            getSupportFragmentManager().popBackStack("products", 2);
              loadProducts();
//            return true;
        }
//          else if (id == R.id.offer) {
//
//        }
        else if (id == R.id.order) {
            loadOrders();
        }
        else if (id == R.id.complaints) {
            loadComplaints();
        }
        else if (id == R.id.tickets) {

        }
        else if (id == R.id.push_notification) {

        }
        else if (id == R.id.requests) {

        }
        else if (id == R.id.app_info) {

        }
        else if (id == R.id.logout) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("username");
            editor.commit();
            loadlogin();
        }
//        else if(id == R.id.account) {
//            loadAccount();
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadAccount()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Account account = new Account();
        transaction.replace(R.id.container, account);
        transaction.addToBackStack("account");
        transaction.commit();
    }

    public void loadHome()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        transaction.replace(R.id.container, homeFragment);
        transaction.addToBackStack("home");
        transaction.commit();
    }
    public void loadControl()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Control control = new Control();
        transaction.replace(R.id.container, control);
        transaction.addToBackStack("control");
        transaction.commit();
    }

    public void loadProducts()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Products products = new Products();
        transaction.replace(R.id.container, products);
        transaction.addToBackStack("products");
        transaction.commit();
    }

    public void loadMOC() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        MOC moc = new MOC();
        transaction.replace(R.id.container, moc);
        transaction.addToBackStack("moc");
        transaction.commit();
    }

    public void loadConfigure()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment1 fragment1 = new Fragment1();
        transaction.replace(R.id.container, fragment1);
        transaction.addToBackStack("fragment1");
        transaction.commit();
    }

    public void loadaddcontrol()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment2 fragment2 = new Fragment2();
        transaction.replace(R.id.container, fragment2);
        transaction.addToBackStack("fragment2");
        transaction.commit();
    }
    public void loadaddcontrol(int position)
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment2 fragment2 = new Fragment2();
        Bundle bundle = new Bundle();

        bundle.putInt("pos",position);
        fragment2.setArguments(bundle);
        transaction.replace(R.id.container, fragment2);
        transaction.addToBackStack("fragment2");
        transaction.commit();
    }

    public List getArrayList(){
        return arrayList;
    }

    public void loadListDevices()
    {
//        FragmentManager manager = getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        ListDevices listDevices = new ListDevices();
//        transaction.replace(R.id.container, listDevices);
//        transaction.addToBackStack("list");
//        transaction.commit();
        ListDevices listDevices = new ListDevices();
        listDevices.show(getFragmentManager(), null);
    }

    public void loadDevices()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Device device = new Device();
        transaction.replace(R.id.container, device);
        transaction.addToBackStack("list");
        transaction.commit();
    }

    public void loadOnOrOff_Fan(String roomName)
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        OnOrOff_Fan onOrOff_fan = new OnOrOff_Fan();
        Bundle bundle = new Bundle();
        bundle.putString("roomName", roomName);
        onOrOff_fan.setArguments(bundle);
        transaction.replace(R.id.container, onOrOff_fan);
        transaction.addToBackStack("fan_on_off");
        transaction.commit();
    }

    public void loadOnOrOff_Light(String roomName)
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        OnOrOff_Light onOrOff_light = new OnOrOff_Light();
        Bundle bundle = new Bundle();
        bundle.putString("roomName", roomName);
        onOrOff_light.setArguments(bundle);
        transaction.replace(R.id.container, onOrOff_light);
        transaction.addToBackStack("light_on_off");
        transaction.commit();
    }
    public void loadregister() {
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        RegisterFragment registerFragment=new RegisterFragment();
        transaction.replace(R.id.container,registerFragment);
        transaction.addToBackStack("Register");
        transaction.commit();
    }

    public void loadDisclaimer() {
        DisclaimerFragment disclaimer = new DisclaimerFragment();
        disclaimer.show(getFragmentManager(),null);
    }
    public void loadlogin()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        LoginFragment login = new LoginFragment();
        transaction.replace(R.id.container, login);
        transaction.addToBackStack("Login");
        transaction.commit();
    }
    public void loadforgotfragment() {
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        Forgotpassword forgotpassword=new Forgotpassword();
        transaction.replace(R.id.container,forgotpassword);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadresetpwd() {
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        Resetpassword resetpassword=new Resetpassword();
        transaction.replace(R.id.container,resetpassword);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadchangepassword() {
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        Changepassword changepassword=new Changepassword();
        transaction.replace(R.id.container,changepassword);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadComplaint_Heading() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        Complaint_Heading complaint_heading = new Complaint_Heading();
        transaction.replace(R.id.container,complaint_heading);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadComplaints() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Complaints complaints = new Complaints();
        transaction.replace(R.id.container,complaints);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadConfirm_Issue_Fixed() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Confirm_Issue_Fixed confirm_issue_fixed = new Confirm_Issue_Fixed();
        transaction.replace(R.id.container,confirm_issue_fixed);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadHeading() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Heading heading = new Heading();
        transaction.replace(R.id.container,heading);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadOrders() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Orders orders = new Orders();
        transaction.replace(R.id.container,orders);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void loaddialog() {

        SucessDialog dialogFragment = new SucessDialog();
        dialogFragment.show(getFragmentManager(), "dialog");
    }

    public void loadRequestFragment()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        RequestFragment requestFragment = new RequestFragment();
        transaction.replace(R.id.container,requestFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadAdminHome()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        AdminHome adminHome = new AdminHome();
        transaction.replace(R.id.container,adminHome);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadAdminRequest()
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        AdminRequest adminRequest = new AdminRequest();
        transaction.replace(R.id.container,adminRequest);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
