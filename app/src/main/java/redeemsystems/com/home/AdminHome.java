package redeemsystems.com.home;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminHome extends Fragment
{
    CardView control, configure, products;
    MainActivity mainActivity;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<String> arrayList;
    LinearLayoutManager manager;



    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
    {

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = getActivity().getLayoutInflater().inflate(R.layout.row_admin, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }
        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView count;
            LinearLayout linear;
            public ViewHolder(View itemView)
            {
                super(itemView);
                count = itemView.findViewById(R.id.count);
                linear = itemView.findViewById(R.id.linear);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position)
        {
            String count = arrayList.get(position);
            holder.count.setText(count);

            holder.linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity = (MainActivity) getActivity();
                    mainActivity.loadAdminRequest();
                }
            });

        }

    }

    public AdminHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);
        mainActivity = (MainActivity) getActivity();
        control = view.findViewById(R.id.control);
        configure = view.findViewById(R.id.configure);
        products = view.findViewById(R.id.products);
        recyclerView = view.findViewById(R.id.recyclerview);
        arrayList = new ArrayList<>();
        myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
        manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_ADMIN_REQUEST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("joan", response);
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);
                            String count = array.length()+"";
                            arrayList.add(count);
                            myAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }


        );

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
        control.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
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
        return view;
    }

}
