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
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static android.content.Context.BIND_AUTO_CREATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Control extends Fragment
{
    Button fanButton, lightButton, CCTVButton, sensorButton;
    RelativeLayout fan, light, cctv, sensor;
    MainActivity mainActivity;
    Cursor cursor;
    static boolean isFan;

    String deviceName, deviceAddress;

    public Control() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        mainActivity = (MainActivity) getActivity();
        fanButton = view.findViewById(R.id.fanButton);
        lightButton = view.findViewById(R.id.lightButton);
        CCTVButton = view.findViewById(R.id.CCTVButton);
        sensorButton = view.findViewById(R.id.sensorButton);
        fan = view.findViewById(R.id.relative1);
        light = view.findViewById(R.id.relative2);
        cctv = view.findViewById(R.id.relative3);
        sensor = view.findViewById(R.id.relative4);
        mainActivity.getSupportActionBar().setTitle("Appliances");

        Toast.makeText(mainActivity, HomeFragment.readMessage, Toast.LENGTH_SHORT).show();  //Added by SUSMITA

        cursor = mainActivity.myDatabase.queryDevices();
        int fanCount = 0, lightCount = 0, cctvCount, sensorCount;
        deviceName = ListDevices.Name;
        deviceAddress = ListDevices.address;
        for(int i = 0; i < cursor.getCount(); i++)
        {
            fanCount = fanCount + 2;
            lightCount = lightCount + 6;
        }
        fanButton.setText(fanCount+"");
        lightButton.setText(lightCount+"");

        fan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFan = true;
                mainActivity.loadDevices();
            }
        });
        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFan = false;
                mainActivity.loadDevices();
            }
        });

        return view;
    }
}
