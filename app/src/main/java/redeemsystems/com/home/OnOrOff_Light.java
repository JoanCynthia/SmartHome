package redeemsystems.com.home;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
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

import static android.content.Context.BIND_AUTO_CREATE;
import static redeemsystems.com.home.R.drawable.toggle;


/**
 * A simple {@link Fragment} subclass.
 */
public class OnOrOff_Light extends Fragment
{
    public BluetoothGattCharacteristic characteristicTX;
    public BluetoothGattCharacteristic characteristicRX;
    public Bluetoothservice mBluetoothLeService;
    MainActivity mainActivity;
    byte[] tx;
    MyTask myTask;
    public boolean mConnected = false;
    public BluetoothGatt mBluetoothGatt;
    static String stringBuilder;



    public final static UUID HM_RX_TX =
            UUID.fromString(SampleGattAttributes.HM_RX_TX);

    public final String LIST_NAME = "NAME";
    public final String LIST_UUID = "UUID";
    String writeMessage;
    String roomName, Rid;
    Switch switch1, switch2,switch3, switch4, switch5, switch6;

    Cursor cursor;
    Bundle bundle;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

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
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                final String action = intent.getAction();
                if (Bluetoothservice.ACTION_GATT_CONNECTED.equals(action)) {
                    mConnected = true;
                    writeMessage = "status";
                    makeChange();
                    Toast.makeText(mainActivity, "Connected", Toast.LENGTH_SHORT).show();
                } else if (Bluetoothservice.ACTION_GATT_DISCONNECTED.equals(action)) {
                    mConnected = false;
                    Toast.makeText(mainActivity, "Device is disConnected", Toast.LENGTH_SHORT).show();
                }
                else if (Bluetoothservice.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                    Log.i("joan", "service discovered");
                    // Show all the supported services and characteristics on the user interface.
                    displayGattServices(mBluetoothLeService.getSupportedGattServices());
                    Log.i("joan", "display gatt");

                    if (characteristicTX == null) {
                        Log.i("joan", "Its not nullllllll");
//                    makeChange();
                    }
                } else if (Bluetoothservice.EXTRA_DATA.equals(action)) {
//                    receivedata();
                    Log.d("joan", "extra data");

                    displayGattServices(mBluetoothLeService.getSupportedGattServices());
                    Log.d("joan", "extra data display gatt");

                    if(characteristicRX == null)
                    {
                        makeChange();
                        Log.d("joan", "extra data make change");

                    }
//                    makeChange();
                }
            }catch (Throwable e){
                e.printStackTrace();
                Log.d("sus",e.toString());
            }
        }
    };




    public OnOrOff_Light() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_or_off__light, container, false);
        mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportActionBar().setTitle("Lights");
        writeMessage = "status";
        makeChange();
//        stringBuilder = new StringBuilder();
//        Toast.makeText(mainActivity, stringBuilder.toString(), Toast.LENGTH_LONG).show();
//        Log.d("joan", "check msg"+stringBuilder.toString());

        bundle = getArguments();
            if(bundle != null)
        {
            roomName = bundle.getString("roomName");
            cursor = mainActivity.myDatabase.queryByName(roomName);
            cursor.moveToFirst();
            Rid = cursor.getString(2).trim();
            Log.d("joan", Rid);
//            Toast.makeText(mBluetoothLeService, ""+Rid, Toast.LENGTH_SHORT).show();   //Added by SUSMITA
        }
        switch1 = view.findViewById(R.id.switch1);
        switch2 = view.findViewById(R.id.switch2);
        switch3 = view.findViewById(R.id.switch3);
        switch4 = view.findViewById(R.id.switch4);
        switch5 = view.findViewById(R.id.switch5);
        switch6 = view.findViewById(R.id.switch6);
        if(HomeFragment.readMessage != null) {
            if (HomeFragment.readMessage.equalsIgnoreCase("ST/4203/SW1/OFF")) {
                switch1.setEnabled(false);
            } else if (HomeFragment.readMessage.equalsIgnoreCase("ST/4203/SW1/ON")) {
                switch1.setEnabled(true);
            }
            if (HomeFragment.readMessage.equalsIgnoreCase("ST/4203/SW2/OFF")) {
                switch2.setEnabled(false);
            } else if (HomeFragment.readMessage.equalsIgnoreCase("ST/4203/SW2/ON")) {
                switch2.setEnabled(true);
            }
            if (HomeFragment.readMessage.equalsIgnoreCase("ST/4203/SW3/OFF")) {
                switch3.setEnabled(false);
            } else if (HomeFragment.readMessage.equalsIgnoreCase("ST/4203/SW3/ON")) {
                switch3.setEnabled(true);
            }
            if (HomeFragment.readMessage.equalsIgnoreCase("ST/4203/SW4/OFF")) {
                switch4.setEnabled(false);
            } else if (HomeFragment.readMessage.equalsIgnoreCase("ST/4203/SW4/ON")) {
                switch4.setEnabled(true);
            }
        }
        Intent gattServiceIntent = new Intent(getActivity(), Bluetoothservice.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getActivity()).bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        }
//        receivedata();
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                try {
                    if (switch1.isChecked()) {
                        writeMessage = Rid + "/SW1/ON";
                        switch1.setThumbResource(R.drawable.toggle);
                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
//                        Toast.makeText(mainActivity, HomeFragment.readMessage, Toast.LENGTH_LONG).show();
//                        Log.d("joan", "check msg "+HomeFragment.readMessage);
//                        if(HomeFragment.readMessage.equalsIgnoreCase("ST/4203/SW1/OFF"))
//                        {
//                            switch1.setEnabled(false);
////                            return;
//                        }
//                        else if(HomeFragment.readMessage.equalsIgnoreCase("ST/4203/SW1/ON"))
//                        {
//                            switch1.setEnabled(true);
//
//                        }
//                        else if(HomeFragment.readMessage != null && HomeFragment.readMessage.length() == 0 ){

//                        }
//                    }
                    }
//                        else if(stringBuilder.toString().equalsIgnoreCase("ST/4203/SW1/ON"))
//                        {
//                            switch1.setChecked(true);
//                            return;
//                        }
                    else {
                        writeMessage = Rid + "/SW1/OFF";
                        switch1.setThumbResource(R.drawable.toggle_off);
                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            writeMessage = Rid + "/SW1/OFF";
                            makeChange();
                        }
//                        if(HomeFragment.readMessage.equalsIgnoreCase("ST/4203/SW1/OFF"))
//                        {
//                            switch1.setEnabled(false);
////                            return;
//                        }
//                        else if(HomeFragment.readMessage.equalsIgnoreCase("ST/4203/SW1/ON"))
//                        {
//                            switch1.setEnabled(true);
//
//                        }

                    }

                }catch (Throwable e){
                    e.printStackTrace();
                    Log.d("sus",e.toString());
                }
            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (switch2.isChecked()) {
                        writeMessage = Rid + "/SW2/ON";
                        switch2.setThumbResource(R.drawable.toggle);
                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
                    } else {

                        writeMessage = Rid + "/SW2/OFF";
                        switch2.setThumbResource(R.drawable.toggle_off);
                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
                    }
                }catch (Throwable e){
                    e.printStackTrace();
                    Log.d("sus",e.toString());
                }
            }
        });
        switch3.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {

                    if (switch3.isChecked()) {
                        writeMessage = Rid + "/SW3/ON";
                        switch3.setThumbResource(R.drawable.toggle);
                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
                    } else {
                        writeMessage = Rid + "/SW3/OFF";
                        switch3.setThumbResource(R.drawable.toggle_off);
                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
                    }
                }catch (Throwable e){
                    e.printStackTrace();
                    Log.d("sus",e.toString());
                }
            }
        });
        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {

                if(switch4.isChecked())
                {
                    writeMessage = Rid+"/SW4/ON";
                    switch4.setThumbResource(R.drawable.toggle);
                    if (ListDevices.isWiFi == true){
                        myTask = new MyTask();
                        myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                    }else {
                        makeChange();
                    }                 }
                else {
                    writeMessage = Rid+"/SW4/OFF";
                    switch4.setThumbResource(R.drawable.toggle_off);
                    if (ListDevices.isWiFi == true){
                        myTask = new MyTask();
                        myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                    }else {
                        makeChange();
                    }
                }
                }catch (Throwable e){
                    e.printStackTrace();
                    Log.d("sus",e.toString());
                }
            }
        });
        switch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (switch5.isChecked()) {
                        writeMessage = Rid + "/SW5/ON";
                        switch5.setThumbResource(R.drawable.toggle);
                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
                    } else {
                        writeMessage = Rid + "/SW5/OFF";
                        switch5.setThumbResource(R.drawable.toggle_off);
                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
                    }
                }catch (Throwable e){
                    e.printStackTrace();
                    Log.d("sus",e.toString());
                }
            }
        });
        switch6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (switch6.isChecked()) {
                        writeMessage = Rid + "/SW6/ON";
                        switch6.setThumbResource(R.drawable.toggle);
                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
                    } else {
                        writeMessage = Rid + "/SW6/OFF";
                        switch6.setThumbResource(R.drawable.toggle_off);
                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
                    }
                }catch (Throwable e){
                    e.printStackTrace();
                    Log.d("sus",e.toString());
                }
            }
        });

        return view;
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
    @Override
    public void onPause() {
        super.onPause();
        try {
            mainActivity.unregisterReceiver(mGattUpdateReceiver);
//            mainActivity.unregisterReceiver(receiver);
        }catch (Throwable e){
            e.printStackTrace();
            Log.d("sus",e.toString());
        }
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

//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
//    public void receivedata() {
//        {
////            if (mBluetoothAdapter == null || mBluetoothGatt == null) {
////                Log.w(TAG, "BluetoothAdapter not initialized");
////                return;
////            }
//            /*check if the service is available on the device*/
////            if(mBluetoothGatt != null) {
////                BluetoothGattService mCustomService = mBluetoothGatt.getService(Bluetoothservice.UUID_HM_RX_TX);
////                if (mCustomService == null) {
////                    Log.w("joan", "Custom BLE Service not found");
////                    return;
////                }
//
////                BluetoothGattCharacteristic mReadCharacteristic = mCustomService.getCharacteristic(Bluetoothservice.UUID_HM_RX_TX);
//                if (mBluetoothGatt.readCharacteristic(characteristicRX) == false) {
//                    Log.w("joan", "Failed to read characteristic");
//                }
//                mBluetoothGatt.readCharacteristic(characteristicRX);
//                Log.w("joan", "read characteristic called");
////            }
////            Toast.makeText(mainActivity, "not entering", Toast.LENGTH_SHORT).show();
//
//        }
//    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void makeChange() {

        if(writeMessage != null) {
            tx = writeMessage.getBytes();
        }
        if(mConnected) {
            try {
                Log.i("Sw","mConnected");
                if(tx != null) {
                    characteristicTX.setValue(tx);
                    mBluetoothLeService.writeCharacteristic(characteristicTX);
                }
                mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
//                characteristicRX.getValue();
                mBluetoothLeService.readCharacteristic(characteristicRX);
                mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
                Toast.makeText(mainActivity, writeMessage+" sent", Toast.LENGTH_SHORT).show();
                Log.d("sus", Arrays.toString(tx));
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                Log.d("check", e.toString());
            }
        }
        else
        {
            Log.i("Sw","not Connected");

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
            Toast.makeText(mainActivity, data, Toast.LENGTH_SHORT).show();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onStop() {
        super.onStop();
        mBluetoothLeService.disconnect();

    }

}
