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
import com.google.gson.Gson;
import com.espacio.seguro.Utils.SessionManager;
import com.espacio.seguro.Utils.User;
import com.espacio.seguro.R;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SharedUsersFragment extends Fragment{

    RecyclerView activity;
    SharedUsersAdapter adapter;
    List<JSONObject> l=new ArrayList<>();
    SwipeRefreshLayout srefresh;
    static MaterialDialog md;
    SessionManager session;
    User u;

    public SharedUsersFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SharedUsersFragment newInstance() {
        SharedUsersFragment fragment = new SharedUsersFragment();
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
        View view=inflater.inflate(R.layout.fragment_users_shared, container, false);
        srefresh=view.findViewById(R.id.shared_users_swiperefresh);
        activity=view.findViewById(R.id.recycler_view_shared_users);
        activity.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter=new SharedUsersAdapter(l);

        activity.setAdapter(adapter);

        session=new SessionManager(getContext());
        HashMap<String,String> user;
        user = session.getUserDetails();
        Gson g=new Gson();
        u=g.fromJson(user.get(SessionManager.KEY_VALUES),User.class);

        srefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srefresh.setRefreshing(true);
                getSharedUsers();
            }
        });


        new DoInBackGround().execute();

        return view;
    }

    public class DoInBackGround extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            getSharedUsers();

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

    public void getSharedUsers() {

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String params="?code="+u.getCod_servicio()+"&user="+u.getId();
        String url = "https://www.espacioseguro.pe/php_connection/getSharedUsers.php"+params;

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
                        md.dismiss();
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(postRequest);
    }
}
