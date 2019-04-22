package redeemsystems.com.home;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment
{
    EditText subject, description;
    Button submit;
    RecyclerView recycler;
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.row_request, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }





        public class ViewHolder extends RecyclerView.ViewHolder
        {
            ImageView complete, pending;
            TextView subject, description, date;
            public ViewHolder(View itemView)
            {
                super(itemView);
                complete = itemView.findViewById(R.id.complete);
                pending = itemView.findViewById(R.id.pending);
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
            String status = raiseRequest.getStatus();
            if(status.equalsIgnoreCase("Completed"))
            {
                holder.complete.setVisibility(View.VISIBLE);
                holder.pending.setVisibility(View.INVISIBLE);
            }
            else if(status.equalsIgnoreCase("Pending"))
            {
                holder.complete.setVisibility(View.INVISIBLE);
                holder.pending.setVisibility(View.VISIBLE);
            }

        }
    }


    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, container, false);
        subject = view.findViewById(R.id.subject);
        description = view.findViewById(R.id.description);
        submit = view.findViewById(R.id.submit);
        recycler = view.findViewById(R.id.recycler);
        mainActivity = (MainActivity) getActivity();
        arrayList = new ArrayList<>();
        customAdapter = new CustomAdapter();
        recycler.setAdapter(customAdapter);
        manager = new LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_FETCH,
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


                ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("phone_number",MainActivity.loginPhone);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subject.getText().toString().equals("") || description.getText().toString().equals(""))
                {
                    Toast.makeText(mainActivity, "Fields can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_REQUEST, new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("nike", response);

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            if(message.equals("ok")) {
                                Toast.makeText(mainActivity, "Your request has been raised..Please wait for our Tech Person to contact you.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else if(message.equals("error"))
                            {
                                Toast.makeText(mainActivity, "Something went wrong", Toast.LENGTH_SHORT).show();
                                return;
                            }
//                            else if(message.equals("exist"))
//                            {
//                                String subject = jsonObject.getString("subject");
//                                String description = jsonObject.getString("description");
//                                String status = jsonObject.getString("status");
//                                String time = jsonObject.getString("time");
//                                CharSequence relativeDate =
//                                            DateUtils.getRelativeTimeSpanString(Long.parseLong(time),
//                                                    System.currentTimeMillis(),
//                                                    DateUtils.HOUR_IN_MILLIS);
//                                RaiseRequest raiseRequest = new RaiseRequest(subject, description, status, relativeDate);
//                                arrayList.add(raiseRequest);
//                                customAdapter.notifyDataSetChanged();
//
//                            }

                        }

                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.d("nike", "exception "+e.toString());
                        }
                    }
                },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                Toast.makeText(mainActivity,error.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();

                        params.put("user_name",MainActivity.loginName);
                        params.put("phone_number",MainActivity.loginPhone);
                        params.put("subject", subject.getText().toString());
                        params.put("description", description.getText().toString());
                        params.put("status", "Pending");
                        params.put("time", String.valueOf(System.currentTimeMillis()));

                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                requestQueue.add(stringRequest);


            }
        });

        return view;
    }

}
