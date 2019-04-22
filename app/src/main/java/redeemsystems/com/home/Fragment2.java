package redeemsystems.com.home;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment2 extends Fragment {
    int id;
    Button button,update;
    Cursor cursor;
    EditText editText1,editText2;
    TextView editId;
    ImageView editName;
    String etName, etId;
    int pos;
    //~Temporary
    MainActivity mainActivity;


    public Fragment2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //~Temporary
        mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportActionBar().setTitle("Configure");

        View v = inflater.inflate(R.layout.fragment_fragment2, container, false);

        button=v.findViewById(R.id.save);
        update=v.findViewById(R.id.update);

        editText1=v.findViewById(R.id.Rname);
        editText2=v.findViewById(R.id.Rid);
//        fragment1 = new Fragment1();

        editName = v.findViewById(R.id.editName);
        editId = v.findViewById(R.id.editId);

        Bundle bundle = getArguments();
        if (bundle != null) {
            pos = bundle.getInt("pos");
            cursor = mainActivity.myDatabase.queryDevices();
            cursor.moveToPosition(pos);
            etName = cursor.getString(1);
            etId = cursor.getString(2);

            editText1.setText(etName);
//            editText2.setText(etId);
            editText2.setVisibility(View.INVISIBLE);
            editName.setVisibility(View.VISIBLE);
            editId.setText(etId);
            editId.setVisibility(View.VISIBLE);
            button.setVisibility(View.INVISIBLE);
            update.setVisibility(View.VISIBLE);
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("joan", "clicked");
                    String Rname=editText1.getText().toString().trim();
                    String Rid=editText2.getText().toString().trim();
                    Log.d("joan", Rname+" "+Rid);

                    try {
                        mainActivity.myDatabase.updatedevices(pos, Rname, Rid);
                        mainActivity.getSupportFragmentManager().popBackStack("fragment1", 0);
                    }
                    catch (Throwable e){
                        e.printStackTrace();
                        Log.d("sus",e.toString());
                    }
                }
            });
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              String Rname=editText1.getText().toString();
              String Rid=editText2.getText().toString();
              try {
                  mainActivity.myDatabase.insertdevices(Rname, Rid);
                  mainActivity.getSupportFragmentManager().popBackStack("fragment1", 0);
              }catch (Throwable e){
                  e.printStackTrace();
                  Log.d("sus",e.toString());
              }
//                cursor = mainActivity.myDatabase.queryDevices();
//                if (cursor != null){
//                    cursor.moveToFirst();
//                    //Toast.makeText(mainActivity, cursor.getString(4), Toast.LENGTH_SHORT).show();
//                }
//
//                //~Temporary
//                mainActivity.arrayList.add(Rname);
            }

        });

        return v;
    }
}
