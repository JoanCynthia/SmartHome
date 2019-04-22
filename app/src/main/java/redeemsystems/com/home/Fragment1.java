package redeemsystems.com.home;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment1 extends Fragment {
   LinearLayout linearLayout;
   RecyclerView recyclerView;
   MyAdapter myAdapter;
   LinearLayoutManager layoutManager;
   ArrayList<String> al;
   MainActivity mainActivity;
   Cursor cursor;

    public Fragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_fragment1, container, false);
        try {
        mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportActionBar().setTitle("Controllers");

        linearLayout=v.findViewById(R.id.add);
        recyclerView=v.findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(mainActivity,LinearLayoutManager.VERTICAL,false);
        myAdapter =new MyAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);
        al = (ArrayList<String>) mainActivity.getArrayList();

//        if(mainActivity.myDatabase != null) {
            cursor = mainActivity.myDatabase.queryDevices();
            Log.d("joan", "cursor success");
//        }
            if(cursor != null) {
                myAdapter.notifyDataSetChanged();
            }
            Log.d("joan", "adapter notified");
        }
        catch (Throwable e)
        {
            Toast.makeText(mainActivity, e.toString(), Toast.LENGTH_SHORT).show();
            Log.d("joan", e.toString());
        }



//        if(cursor != null && cursor.getCount() > 0)
//        {
//
//        }
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mainActivity.loadaddcontrol();
            }
        });



        return v;
    }


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mainActivity.getLayoutInflater().inflate(R.layout.row,parent,false);
            ViewHolder vh = new ViewHolder(view);

            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

            //~Temporary
            try {
//                while (cursor.moveToNext())
//                {
                    Log.d("joan", "while entered");

                    cursor.moveToPosition(position);
                    holder.tv.setText(cursor.getString(1));
                    Log.d("joan", "textview set");

//                }
            }
            catch (Throwable e)
            {
                Log.d("joan", e.toString());
            }

            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   mainActivity.loadaddcontrol(position);
                }
            });
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
            LinearLayout linearLayout;
            public ViewHolder(View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.tv1);
                linearLayout = itemView.findViewById(R.id.linear_clickable);

            }
        }
    }


}
