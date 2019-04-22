package redeemsystems.com.home;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.LongDef;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Device extends Fragment
{
    LinearLayout linearLayout;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    LinearLayoutManager layoutManager;
    MainActivity mainActivity;
    Cursor cursor;

    public Device() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportActionBar().setTitle("Rooms");

        linearLayout=view.findViewById(R.id.add);
        recyclerView=view.findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(mainActivity,LinearLayoutManager.VERTICAL,false);
        myAdapter =new MyAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);

        cursor = mainActivity.myDatabase.queryDevices();
        myAdapter.notifyDataSetChanged();

        return view;
    }
    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


        @NonNull
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mainActivity.getLayoutInflater().inflate(R.layout.row,parent,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, final int position) {

                try {


                    cursor.moveToPosition(position);
                    final String roomName = cursor.getString(1);
                    holder.tv.setText(roomName);
                    Log.d("joan", "textview set");
                    if (roomName.contains("Living"))
                    {
                        holder.roomIcon.setImageResource(R.drawable.living);
                    }
                    else if(roomName.contains("Kitchen"))
                    {
                        holder.roomIcon.setImageResource(R.drawable.kitchen);
                    }
                    else if(roomName.contains("Bedroom"))
                    {
                        holder.roomIcon.setImageResource(R.drawable.bedroom);
                    }
                    holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Control.isFan) {
                                mainActivity.loadOnOrOff_Fan(roomName);
                            } else {
                                mainActivity.loadOnOrOff_Light(roomName);
                            }

                        }
                    });
                }catch (Throwable e){
                    e.printStackTrace();
                    Log.d("sus",e.toString());
                }
        }

        @Override
        public int getItemCount() {
//            Toast.makeText(mainActivity, al.size()+"  size", Toast.LENGTH_SHORT).show();
            try {
                if (cursor != null) {
                    Log.d("joan", ""+cursor.getCount());

                    return cursor.getCount();

                }
            }
            catch (Throwable e)
            {
                Log.d("joan", e.toString());
            }
            return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv;
            ImageView roomIcon;
            LinearLayout linearLayout;
            public ViewHolder(View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.tv1);
                linearLayout = itemView.findViewById(R.id.linear_clickable);
                roomIcon = itemView.findViewById(R.id.roomIcon);

            }
        }
    }


}
