package redeemsystems.com.home;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListDevices extends DialogFragment
{
    MainActivity mainActivity;
    BluetoothAdapter myBluetooth;
    Intent btnEnabling;
    int requestCodeForEnable;
    ListView listView;
    ArrayList<String> stringArrayList;
    ArrayList<BluetoothDevice> mLeDevices;
    BroadcastReceiver myreceiver, broadcastReceiver;
    LeDeviceListAdapter mLeDeviceListAdapter;
    public boolean mScanning;
    private static final long SCAN_PERIOD = 10000;
    static String Name, address;
    static Boolean isBluetooth;
    ProgressBar pBar;
    Switch bt_wifi;
    static boolean isWiFi;
    AlertDialog.Builder ab;
    Dialog d;

    public class LeDeviceListAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            Log.d("joan", mLeDevices.size()+"");
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = getActivity().getLayoutInflater().inflate(R.layout.router, parent, false);
            TextView name, status;
            name = view.findViewById(R.id.name);
            status = view.findViewById(R.id.status);

            BluetoothDevice device = mLeDevices.get(position);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                name.setText(deviceName);
            else
                name.setText("Unknown Device");
            status.setText(device.getAddress());
            Log.v("check","2");
            //ADD EXCEPTION HANDLING
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    isBluetooth = true;
                    final BluetoothDevice device = mLeDevices.get(position);
                    Name = device.getName();
                    address = device.getAddress();
                    if (device == null) return;
                    if(MainActivity.loginUser.equalsIgnoreCase("admin")){
                        mainActivity.loadAdminHome();
                    }
                    else {
                        mainActivity.loadHome();
                    }
                    d.dismiss();
//                    mainActivity.loadControl();//remove after checking
//                    mainActivity.loadBluetoothDetails();
                }
            });
            return view;
        }


    }

    // Device scan callback.
    public BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        try {
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (!mLeDevices.contains(device) && mLeDevices != null) {
                                            mLeDevices.add(device);
                                            Log.v("check", "adapter notified");
                                        }
                                    }catch (Throwable e){
                                        e.printStackTrace();
                                        Log.d("sus",e.toString());
                                    }
                                }
                            });
                        }
                        catch (Throwable e)
                        {
                            Log.d("joan", e.toString());
                        }

                    }
                    if(mLeDevices.size() != 0) {
                        mLeDeviceListAdapter.notifyDataSetChanged();
                    }
                }

            };



    public ListDevices() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        d = null;
        ab = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_list_devices, null);
//        ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });

        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.getSupportActionBar().show();
//            mainActivity.getSupportActionBar().setTitle("Bluetooth Devices");
        }

        listView = view.findViewById(R.id.list);
        stringArrayList = new ArrayList<String>();
        mLeDevices = new ArrayList<BluetoothDevice>();
        pBar = view.findViewById(R.id.pBar);
        btnEnabling = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestCodeForEnable = 1;

        bt_wifi = view.findViewById(R.id.switch1);

        bt_wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isWiFi = true;
                    if(MainActivity.loginUser.equalsIgnoreCase("admin")){
                        mainActivity.loadAdminHome();
                    }
                    else {
                        mainActivity.loadHome();
                    }
                    d.dismiss();
                }
                else {
                    isWiFi = false;
                }
            }
        });


        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mainActivity, "BLE not supported", Toast.LENGTH_SHORT).show();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            myBluetooth = bluetoothManager.getAdapter();
        }

        // Checks if Bluetooth is supported on the device.
        if (myBluetooth == null) {
            Toast.makeText(mainActivity, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
        }
        ab.setView(view);
        d = ab.create();
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return d;

    }
//
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_list_devices, container, false);
//        mainActivity = (MainActivity) getActivity();
//        if (mainActivity != null) {
//            mainActivity.getSupportActionBar().show();
//            mainActivity.getSupportActionBar().setTitle("Bluetooth Devices");
//        }
//
//        listView = view.findViewById(R.id.list);
//        stringArrayList = new ArrayList<String>();
//        mLeDevices = new ArrayList<BluetoothDevice>();
//        pBar = view.findViewById(R.id.pBar);
//        btnEnabling = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        requestCodeForEnable = 1;
//
//        bt_wifi = view.findViewById(R.id.switch1);
//
//        bt_wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    isWiFi = true;
//                    mainActivity.loadHome();
//                }
//                else {
//                    isWiFi = false;
//                }
//            }
//        });
//
//
//        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(mainActivity, "BLE not supported", Toast.LENGTH_SHORT).show();
//        }
//
//        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
//        // BluetoothAdapter through BluetoothManager.
//        final BluetoothManager bluetoothManager =
//                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
//        if (bluetoothManager != null) {
//            myBluetooth = bluetoothManager.getAdapter();
//        }
//
//        // Checks if Bluetooth is supported on the device.
//        if (myBluetooth == null) {
//            Toast.makeText(mainActivity, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
//        }
//
//
//
////                    stringArrayList.clear();
////                    mLeDevices.clear();
////                    mLeDeviceListAdapter = new LeDeviceListAdapter();
////                    listView.setAdapter(mLeDeviceListAdapter);
////                    scanLeDevice(true);
////
//
//        return view;
//    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requestCodeForEnable){
            if (resultCode == RESULT_OK){
                Toast.makeText(getActivity(), "Bluetooth is Enabled", Toast.LENGTH_SHORT).show();
            }else if (resultCode == RESULT_CANCELED){
                Toast.makeText(getActivity(), "Bluetooth Enabling Cancelled", Toast.LENGTH_SHORT).show();

            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onPause() {
        super.onPause();
        try {
            scanLeDevice(false);

        }catch (Throwable e){
            e.printStackTrace();
            Log.d("sus",e.toString());
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        try {
            if (!myBluetooth.isEnabled()) {
                if (!myBluetooth.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                    Log.v("check","bLe adapter enabled");
                }
            }
            // Initializes list view adapter.
            mLeDeviceListAdapter = new LeDeviceListAdapter();
            listView.setAdapter(mLeDeviceListAdapter);
            scanLeDevice(true);
        }catch (Throwable e){
            e.printStackTrace();
            Log.d("sus",e.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
//            new Handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mScanning = false;
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    invalidateOptionsMenu();
//                }
//            }, SCAN_PERIOD);
            mScanning = true;
            try {
                myBluetooth.startLeScan(mLeScanCallback);
                Log.v("check", "Le scan started");
            }
            catch (Throwable e)
            {
                Log.d("check", e.toString());
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    try {
                        myBluetooth.stopLeScan(mLeScanCallback);
                        pBar.setVisibility(View.INVISIBLE);
                    }
                    catch (Throwable e)
                    {
                        Log.d("check", e.toString());
                    }
                }
            },SCAN_PERIOD);

        } else {
            mScanning = false;
            try {
                myBluetooth.stopLeScan(mLeScanCallback);
                pBar.setVisibility(View.INVISIBLE);
            }
            catch (Throwable e)
            {
                Log.d("check", e.toString());
            }        }
    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mainActivity.finish();
//    }

}
