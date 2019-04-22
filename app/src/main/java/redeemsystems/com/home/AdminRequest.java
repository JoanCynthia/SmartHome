package redeemsystems.com.home;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
public class AdminRequest extends Fragment
{
    RecyclerView recyclerView;
    MainActivity mainActivity;
    ArrayList<RaiseRequest> arrayList;
    CustomAdapter customAdapter;
    LinearLayoutManager manager;

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>
    {
        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        @NonNull
        @Override
        public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.request_admin, parent, false);
            ViewHolder viewHolder = new CustomAdapter.ViewHolder(view);
            return viewHolder;
        }





        public class ViewHolder extends RecyclerView.ViewHolder
        {

            TextView subject, description, date;
            public ViewHolder(View itemView)
            {
                super(itemView);

                subject = itemView.findViewById(R.id.subject);
                description = itemView.findViewById(R.id.description);
                date = itemView.findViewById(R.id.date);

            }
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position)
        {
            RaiseRequest raiseRequest = arrayList.get(position);
            holder.subject.setText(raiseRequest.getSubject());
            holder.description.setText(raiseRequest.getDescription());
            holder.date.setText(raiseRequest.getDate().toString());
//            String status = raiseRequest.getStatus();
//            if(status.equalsIgnoreCase("Completed"))
//            {
//                holder.complete.setVisibility(View.VISIBLE);
//                holder.pending.setVisibility(View.INVISIBLE);
//            }
//            else if(status.equalsIgnoreCase("Pending"))
//            {
//                holder.complete.setVisibility(View.INVISIBLE);
//                holder.pending.setVisibility(View.VISIBLE);
//            }

        }
    }


    public AdminRequest() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_request, container, false);
        mainActivity = (MainActivity) getActivity();
        recyclerView = view.findViewById(R.id.recycler);
        mainActivity = (MainActivity) getActivity();
        arrayList = new ArrayList<>();
        customAdapter = new CustomAdapter();
        recyclerView.setAdapter(customAdapter);
        manager = new LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_ADMIN_REQUEST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject request = array.getJSONObject(i);
                                String subject = request.getString("subject");
                                String description = request.getString("description");
                                String status = request.getString("status");
                                String time = request.getString("time");
                                CharSequence relativeDate =
                                        DateUtils.getRelativeTimeSpanString(Long.parseLong(time),
                                                System.currentTimeMillis(),
                                                DateUtils.DAY_IN_MILLIS);
                                RaiseRequest raiseRequest = new RaiseRequest(subject, description, status, relativeDate);
                                arrayList.add(raiseRequest);

                            }

                            //creating adapter object and setting it to recyclerview
                            customAdapter.notifyDataSetChanged();
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
        return view;
    }

}
