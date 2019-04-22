package redeemsystems.com.home;


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
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class OnOrOff_Fan extends Fragment
{
    public BluetoothGattCharacteristic characteristicTX;
    public BluetoothGattCharacteristic characteristicRX;
    public Bluetoothservice mBluetoothLeService;
    MainActivity mainActivity;
    MyTask myTask;
    public boolean mConnected = false;
    public final static UUID HM_RX_TX =
            UUID.fromString(SampleGattAttributes.HM_RX_TX);

    public final String LIST_NAME = "NAME";
    public final String LIST_UUID = "UUID";
    String writeMessage, roomName, Rid;
//    Switch switch1, switch2;
    SeekBar seekBar, seekBar2;
    Cursor cursor;
    Bundle bundle;
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
                    Toast.makeText(mainActivity, "Connected", Toast.LENGTH_SHORT).show();
                } else if (Bluetoothservice.ACTION_GATT_DISCONNECTED.equals(action)) {
                    mConnected = false;
                    Toast.makeText(mainActivity, "Device is disConnected", Toast.LENGTH_SHORT).show();
                }
                else if (Bluetoothservice.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                    Log.i("Swapnil", "reached");
                    // Show all the supported services and characteristics on the user interface.
                    displayGattServices(mBluetoothLeService.getSupportedGattServices());
                    if (characteristicTX == null) {
                        Log.i("Sw", "Its not nullllllll");
//                    makeChange();
                    }
                } else if (Bluetoothservice.ACTION_DATA_AVAILABLE.equals(action)) {
                }
            }catch (Throwable e){
                e.printStackTrace();
                Log.d("sus",e.toString());
            }
        }
    };

    public OnOrOff_Fan() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_or_off, container, false);
        Intent gattServiceIntent = new Intent(getActivity(), Bluetoothservice.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getActivity()).bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        }
        mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportActionBar().setTitle("Fans");

        bundle = getArguments();
        if(bundle != null)
        {
            roomName = bundle.getString("roomName");
            cursor = mainActivity.myDatabase.queryByName(roomName);
            cursor.moveToFirst();
            Rid = cursor.getString(2).trim();
            Log.d("joan", Rid);
        }
//        switch1 = view.findViewById(R.id.switch1);
//        switch2 = view.findViewById(R.id.switch2);
        seekBar = view.findViewById(R.id.seekBar);
        seekBar2 = view.findViewById(R.id.seekBar2);
//        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(switch1.isChecked())
//                {
//                    writeMessage = Rid+"/SW7/1";
//                    makeChange();
//                }
//                else {
//                    writeMessage = Rid+"/SW7/0";
//                    makeChange();
//                }
//            }
//        });
//        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(switch2.isChecked())
//                {
//                    writeMessage = Rid+"/SW8/1";
//                    makeChange();
//                }
//                else {
//                    writeMessage = Rid+"/SW8/0";
//                    makeChange();
//                }
//            }
//        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                try {

                    if (progress == 0) {
                        writeMessage = Rid + "/SW7/0";

                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
                    } else if (progress > 1 && progress <= 20) {
                        writeMessage = Rid + "/SW7/1";

                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
                    } else if (progress > 21 && progress <= 40) {
                        writeMessage = Rid + "/SW7/2";

                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
                    } else if (progress > 41 && progress <= 60) {
                        writeMessage = Rid + "/SW7/3";

                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
                    } else if (progress > 61 && progress <= 80) {
                        writeMessage = Rid + "/SW7/4";

                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
                    } else if (progress > 81 && progress <= 100) {
                        writeMessage = Rid + "/SW7/5";

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

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                try {

                    if (progress == 0) {
                        writeMessage = Rid + "/SW8/0";

                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
                    } else if (progress > 1 && progress <= 20) {
                        writeMessage = Rid + "/SW8/1";

                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
                    } else if (progress > 21 && progress <= 40) {
                        writeMessage = Rid + "/SW8/2";

                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
                    } else if (progress > 41 && progress <= 60) {
                        writeMessage = Rid + "/SW8/3";

                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
                    } else if (progress > 61 && progress <= 80) {
                        writeMessage = Rid + "/SW8/4";

                        if (ListDevices.isWiFi == true) {
                            myTask = new MyTask();
                            myTask.execute("http://61.12.38.210/Thingworx/Things/BIGBASKET1/Properties/BAG3");
                        } else {
                            makeChange();
                        }
                    } else if (progress > 81 && progress <= 100) {
                        writeMessage = Rid + "/SW8/5";

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

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
                Toast.makeText(mainActivity, writeMessage+" sent", Toast.LENGTH_SHORT).show();
                Log.d("sus", Arrays.toString(tx));
            }
            catch (Throwable e)
            {
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
                Log.d("Tanu", response+"");
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
