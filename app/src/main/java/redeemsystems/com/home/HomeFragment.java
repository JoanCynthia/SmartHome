package redeemsystems.com.home;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.internal.Util;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment
{
    CardView control, configure, products, request;
    MainActivity mainActivity;
    ImageView on, off;
    CircleImageView imageView;
    TextView userName;
    MyTask myTask;

    public BluetoothGattCharacteristic characteristicTX;
    public BluetoothGattCharacteristic characteristicRX;
    public Bluetoothservice mBluetoothLeService;
    public boolean mConnected = false;

    public final static UUID HM_RX_TX =
            UUID.fromString(SampleGattAttributes.HM_RX_TX);

    public final String LIST_NAME = "NAME";
    public final String LIST_UUID = "UUID";
    String writeMessage;
    static String readMessage;

    public final ServiceConnection mServiceConnection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((Bluetoothservice.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.v("check", "Unable to initialize Bluetooth");
                return;
            }
            // Automatically connects to the device upon successful start-up initialization.
            try {
                mBluetoothLeService.connect(ListDevices.address);
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                Log.d("check1", e.toString());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    public final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                /**/

                if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(intent.getAction()))
                {
                    final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int type = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);

                    if (type == BluetoothDevice.PAIRING_VARIANT_PIN)
                    {
                        device.setPin(new byte[]{1,2,3,4,5,6});
                        abortBroadcast();
                    }
                    else
                    {
                        Toast.makeText(context, "afgaani: "+type, Toast.LENGTH_SHORT).show();
//                        L.w("Unexpected pairing type: " + type);
                    }
                }

                /**/

                final String action = intent.getAction();
                if (Bluetoothservice.ACTION_GATT_CONNECTED.equals(action)) {
                    mConnected = true;
                    Toast.makeText(mainActivity, "Connected", LENGTH_SHORT).show();
                } else if (Bluetoothservice.ACTION_GATT_DISCONNECTED.equals(action)) {
                    mConnected = false;
                    Toast.makeText(mainActivity, "Device is disConnected", LENGTH_SHORT).show();
                }
                else if (Bluetoothservice.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                    Log.i("Swapnil", "reached");
                    // Show all the supported services and characteristics on the user interface.
                    displayGattServices(mBluetoothLeService.getSupportedGattServices());
                    if (characteristicTX == null) {
                        Log.i("Sw", "Its not nullllllll");
//                    makeChange();
                    }
                } else if (Bluetoothservice.EXTRA_DATA.equals(action))
                {
                    Log.d("joan", "extra data");

                    displayGattServices(mBluetoothLeService.getSupportedGattServices());
                    Log.d("joan", "extra data display gatt");

                    if(characteristicRX == null)
                    {
                        makeChange();
                        Log.d("joan", "extra data make change");

                    }
                }
            }catch (Throwable e){
                e.printStackTrace();
                Log.d("sus",e.toString());
            }
        }
    };


    public HomeFragment() {
        // Required empty public constructor
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null) {
                if(info.isConnected()==true);
                return true;
            }
        }
        return false;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mainActivity = (MainActivity) getActivity();
//        mainActivity.loadListDevices();
        control = view.findViewById(R.id.control);
        configure = view.findViewById(R.id.configure);
        products = view.findViewById(R.id.products);
        request = view.findViewById(R.id.request);
        on = view.findViewById(R.id.on);
        off = view.findViewById(R.id.off);
        setHasOptionsMenu(true);


        mainActivity.getSupportActionBar().setTitle("Smart Home");
        control.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                writeMessage = "status";
                makeChange();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.loadControl();

                    }
                }, 1000);
            }
        });
        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.loadProducts();
            }
        });
        configure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.loadConfigure();
            }
        });
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.loadRequestFragment();
            }
        });
        if(ListDevices.isWiFi)
        {
            Boolean check = isNetworkAvailable();
            if(!check)
            {
                Toast.makeText(mainActivity, "Please connect to internet", LENGTH_SHORT).show();
            }
        }

        Intent gattServiceIntent = new Intent(getActivity(), Bluetoothservice.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getActivity()).bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        }

        off.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                off.setVisibility(View.INVISIBLE);
                on.setVisibility(View.VISIBLE);
                writeMessage = "TOTAL/ON";

                try {


                    if (ListDevices.isWiFi == true) {

                        myTask = new MyTask();
                        myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                    } else {
                        makeChange();
                    }
                } catch (Throwable e){
                    e.printStackTrace();
                    Log.d("sus",e.toString());
                }
                Toast.makeText(mainActivity, HomeFragment.readMessage, Toast.LENGTH_SHORT).show();  //Added by SUSMITA

            }
        });
        on.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                on.setVisibility(View.INVISIBLE);
                off.setVisibility(View.VISIBLE);
                writeMessage = "TOTAL/OFF";

                try {

                    if (ListDevices.isWiFi == true) {
                        myTask = new MyTask();
                        myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                    } else {
                        makeChange();
                    }
                }catch (Throwable e){
                    e.printStackTrace();
                    Log.d("sus",e.toString());
                }
                Toast.makeText(mainActivity, HomeFragment.readMessage, Toast.LENGTH_SHORT).show();  //Added by SUSMITA

            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity.finish();
    }

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Bluetoothservice.ACTION_GATT_CONNECTED);
        intentFilter.addAction(Bluetoothservice.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(Bluetoothservice.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(Bluetoothservice.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onResume() {
        super.onResume();
        try {
            mainActivity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

            mainActivity.registerReceiver(mPairingRequestRecevier,getIF()); // registering the pairing broadcast receiver

//            mainActivity.registerReceiver(receiver, intentFilter);
            if (mBluetoothLeService != null) {
                final boolean result = mBluetoothLeService.connect(ListDevices.address);
                Log.v("check", "Connect request result=" + result);
            }
        }catch (Throwable e){
            e.printStackTrace();
            Log.d("sus",e.toString());
        }
    }

    //*********//

    public IntentFilter getIF() {
        IntentFilter iFil = new IntentFilter();
        iFil.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        return iFil;
    }
    //****************//

    @Override
    public void onPause() {
        super.onPause();
        try {
            mainActivity.unregisterReceiver(mGattUpdateReceiver);
            mainActivity.unregisterReceiver(mPairingRequestRecevier); //Unregistering Pairing receiver (SUSMITA)
//            mainActivity.unregisterReceiver(receiver);
        }catch (Throwable e){
            e.printStackTrace();
            Log.d("sus",e.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onStop() {
        super.onStop();
        mBluetoothLeService.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mainActivity.unbindService(mServiceConnection);
            mBluetoothLeService = null;
        }catch (Throwable e){
            e.printStackTrace();
            Log.d("sus",e.toString());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void displayGattServices(List<BluetoothGattService> gattServices) {
        try {
            if (gattServices == null) {
                Log.i("Sw","Services not availabele");
                return;
            }
            String uuid = null;
            String unknownServiceString = "Unknown Service";
            ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

            // Loops through available GATT Services.
            for (BluetoothGattService gattService : gattServices) {
                try {
                    HashMap<String, String> currentServiceData = new HashMap<String, String>();
                    uuid = gattService.getUuid().toString();
                    Log.i("Sw ", "UUID: " + uuid);
                    currentServiceData.put(
                            LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));

                    // If the service exists for HM 10 Serial, say so.
                    if (SampleGattAttributes.lookup(uuid, unknownServiceString) == "HM 10 Serial") {
//                    Toast.makeText(mainActivity, "Serial Present", Toast.LENGTH_SHORT).show();
                    } else {
//                    Toast.makeText(mainActivity, "Serial Absent", Toast.LENGTH_SHORT).show();
                    }
                    currentServiceData.put(LIST_UUID, uuid);
                    gattServiceData.add(currentServiceData);

                    // get characteristic when UUID matches RX/TX UUID
                    characteristicTX = gattService.getCharacteristic(Bluetoothservice.UUID_HM_RX_TX);
                    characteristicRX = gattService.getCharacteristic(Bluetoothservice.UUID_HM_RX_TX);

                    Log.i("Sw", characteristicTX == null ? "Itsnull" : "wsbhd");

////            if(characteristicTX.getProperties()& BluetoothGattCharacteristic.PROPERTY_WRITE)
//                if (((characteristicTX.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE ) {
//                    // writing characteristic functions
//                    mWriteCharacteristic = characteristic;
//                }
                    Log.i("Sw", "About to call make change");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.d("check5", e.toString());
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                    Log.d("check", e.toString());
                }
//            makeChange();
            }
        }catch (Throwable e){
            e.printStackTrace();
            Log.d("sus",e.toString());
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void makeChange() {


        final byte[] tx = writeMessage.getBytes();
        if(mConnected) {
            try {
                Log.i("Sw","mConnected");
                characteristicTX.setValue(tx);
                mBluetoothLeService.writeCharacteristic(characteristicTX);
                mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
                characteristicRX.getValue();
                mBluetoothLeService.readCharacteristic(characteristicRX);
                Toast.makeText(mainActivity, "sent", LENGTH_SHORT).show();
                Log.d("sus", Arrays.toString(tx));
            }
            catch (Throwable e){
                e.printStackTrace();
                Log.d("check", e.toString());
            }
        }
    }
    public class MyTask extends AsyncTask<String, Void, String> {
        URL myurl;
        HttpURLConnection connection;
        OutputStream outputStream;
        OutputStreamWriter writer;

        @Override
        protected String doInBackground(String... strings) {
            try {
                myurl = new URL(strings[0]);
                connection = (HttpURLConnection) myurl.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("appKey", "d0007179-396c-4dba-a288-bd951b206471");
                connection.setRequestProperty("Content-Type", "application/json");
                outputStream = connection.getOutputStream();
                writer = new OutputStreamWriter(outputStream);
                JSONObject object = new JSONObject();
                object.accumulate("value", writeMessage);

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(GenerateOTP.this, ph_uuid, Toast.LENGTH_LONG).show();
//                    }
//                });

//                Log.d("IMEI : ", ""+ph_uuid);
                writer.write(object.toString());
                writer.flush();
                Log.d("check", "success");

                int response = connection.getResponseCode();

                return response+"";
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("check", e.toString());
                return e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("check", e.toString());
                return e.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            Toast.makeText(mainActivity, data, LENGTH_SHORT).show();
        }
    }

//    public void onPrepareOptionsMenu(Menu menu)
//    {
//        MenuItem home = menu.findItem(R.id.home);
//        MenuItem inbox = menu.findItem(R.id.inbox);
//        MenuItem products = menu.findItem(R.id.products);
//        MenuItem offer = menu.findItem(R.id.offer);
//        MenuItem order = menu.findItem(R.id.order);
//        MenuItem complaints = menu.findItem(R.id.complaints);
//        MenuItem tickets = menu.findItem(R.id.tickets);
//        MenuItem notification = menu.findItem(R.id.push_notification);
//        MenuItem request = menu.findItem(R.id.requests);
//        MenuItem appInfo = menu.findItem(R.id.app_info);
//        MenuItem logout = menu.findItem(R.id.logout);
//        MenuItem account = menu.findItem(R.id.account);
//
//        if(MainActivity.loginUser.equalsIgnoreCase("Admin"))
//        {
//            home.setVisible(true);
//            inbox.setVisible(true);
//            tickets.setVisible(true);
//            notification.setVisible(true);
//            request.setVisible(true);
//            logout.setVisible(true);
//            appInfo.setVisible(true);
//            products.setVisible(false);
//            offer.setVisible(false);
//            order.setVisible(false);
//            complaints.setVisible(false);
//            account.setVisible(true);
//
//
//        }
//        else if(MainActivity.loginUser.equalsIgnoreCase("Technician"))
//        {
//            home.setVisible(true);
//            inbox.setVisible(true);
//            tickets.setVisible(false);
//            notification.setVisible(false);
//            request.setVisible(false);
//            logout.setVisible(true);
//            appInfo.setVisible(true);
//            products.setVisible(true);
//            offer.setVisible(false);
//            order.setVisible(true);
//            complaints.setVisible(true);
//            account.setVisible(true);
//
//        }
//        else if(MainActivity.loginUser.equalsIgnoreCase("User"))
//        {
//            home.setVisible(true);
//            inbox.setVisible(true);
//            tickets.setVisible(false);
//            notification.setVisible(false);
//            request.setVisible(false);
//            logout.setVisible(true);
//            appInfo.setVisible(true);
//            products.setVisible(true);
//            offer.setVisible(true);
//            order.setVisible(false);
//            complaints.setVisible(false);
//            account.setVisible(true);
//
//        }
//
//    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        View headerview = MainActivity.navigationView.getHeaderView(0);
        imageView = headerview.findViewById(R.id.imageView);
        Account account = new Account();
        userName = headerview.findViewById(R.id.userName);
        if(MainActivity.loginName != null && MainActivity.loginName.length() > 0)
        {
            userName.setText(MainActivity.loginName);
        }
//        if(MainActivity.loginImage != null && MainActivity.loginImage.length > 5){
//            Bitmap photo = account.getImage(MainActivity.loginImage);
//            imageView.setImageBitmap(photo);
//        }
        if(mainActivity.myDatabase != null)
        {
            Cursor cursor = mainActivity.myDatabase.queryRegisterMobile(MainActivity.loginPhone);
            if (cursor != null && cursor.getCount() > 0){
                cursor.moveToNext();
                byte[] image = cursor.getBlob(6);
                if(image != null && image.length > 0) {
                    Bitmap photo = account.getImage(image);
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(photo, 200, 200, false));
                }
            }
        }

        Log.d("Does", "get called");
        if(MainActivity.loginUser.equalsIgnoreCase("admin")) {
            MainActivity.navigationView.getMenu().clear();
            MainActivity.navigationView.inflateMenu(R.menu.admin_menu);
            return;
        }
        else if(MainActivity.loginUser.equalsIgnoreCase("technician"))
        {
            MainActivity.navigationView.getMenu().clear();
            MainActivity.navigationView.inflateMenu(R.menu.tech_menu);
            return;
        }
        else if(MainActivity.loginUser.equalsIgnoreCase("User"))
        {
            MainActivity.navigationView.getMenu().clear();
            MainActivity.navigationView.inflateMenu(R.menu.user_menu);
            return;
        }
    }

    private final BroadcastReceiver mPairingRequestRecevier = new BroadcastReceiver()
    {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(intent.getAction()))
            {
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int type = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);

                if (type == BluetoothDevice.PAIRING_VARIANT_PIN)
                {
                    //abortBroadcast(); // for disabling system pairing dialog box
                    //device.createBond(); // SUSMITA
                    Log.w("Pairinggggg","HEHEHE: " + type);
                }
                else
                {
                    Log.w("Pairinggggg","Unexpected pairing type: " + type);
                }
            }
        }
    };

}
