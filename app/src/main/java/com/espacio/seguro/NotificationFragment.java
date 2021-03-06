package com.espacio.seguro;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.espacio.seguro.R;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment{

    RecyclerView activity;
    NotificationAdapter adapter;
    List<JSONObject> l=new ArrayList<>();
    SwipeRefreshLayout srefresh;
    static MaterialDialog md;

    public NotificationFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_notification, container, false);
        srefresh=view.findViewById(R.id.swiperefresh);
        activity=view.findViewById(R.id.recycler_view_notification);
        activity.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter=new NotificationAdapter(l);

        activity.setAdapter(adapter);
        srefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srefresh.setRefreshing(true);
                getActivities();
            }
        });

        new DoInBackGround().execute();

        return view;
    }

    public class DoInBackGround extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            getActivities();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            md=new MaterialDialog.Builder(getContext())
                    .content("Cargando")
                    .progress(true,0)
                    .cancelable(false)
                    .backgroundColor(Color.WHITE)
                    .contentColor(Color.BLACK)
                    .show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public void getActivities() {

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://www.espacioseguro.pe/php_connection/getNotifications.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        srefresh.setRefreshing(false);
                        JSONParser jp = new JSONParser();
                        try {
                            JSONArray ja=(JSONArray)jp.parse(response);
                            l.clear();
                            for(int i=0;i<ja.size();i++){
                                l.add((JSONObject)ja.get(i));
                            }
                            adapter.notifyDataSetChanged();
                            md.dismiss();
                        } catch (Exception e) {
                            md.dismiss();
                            Toast.makeText(getActivity(),"Intente luego", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                        md.dismiss();
                    }
                }
        );
        queue.add(postRequest);
    }
}
