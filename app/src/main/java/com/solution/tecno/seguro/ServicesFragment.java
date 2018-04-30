package com.solution.tecno.seguro;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.circulardialog.CDialog;
import com.example.circulardialog.extras.CDConstants;
import com.google.gson.Gson;
import com.solution.tecno.seguro.Utils.SessionManager;
import com.solution.tecno.seguro.Utils.User;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ServicesFragment extends Fragment {

    RecyclerView activity;
    ServicesAdapter adapter;
    List<JSONObject> l=new ArrayList<>();
    SessionManager session;
    static MaterialDialog md;
    Button new_service;
    EditText service_code;
    User u;

    SwipeRefreshLayout srefresh;

    public ServicesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ServicesFragment newInstance() {
        ServicesFragment fragment = new ServicesFragment();
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
        View view=inflater.inflate(R.layout.fragment_services, container, false);
        srefresh=view.findViewById(R.id.swiperefresh_services);
        activity=view.findViewById(R.id.recycler_view_services);
        activity.setLayoutManager(new LinearLayoutManager(getContext()));
        new_service=view.findViewById(R.id.btn_new_service);
        new_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                Fragment fr;
                fr=ServiceFragment.newInstance();
                fragmentTransaction.replace(R.id.flaContenido,fr);
                fragmentTransaction.commit();
            }
        });
        adapter=new ServicesAdapter(l);
        srefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srefresh.setRefreshing(true);
                getServices(u.getId());
            }
        });

        activity.setAdapter(adapter);

        new ServicesFragment.DoInBackGround().execute();
        return view;
    }

    public class DoInBackGround extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            session=new SessionManager(getContext());
            HashMap<String,String> user;
            user = session.getUserDetails();
            Gson g=new Gson();
            u=g.fromJson(user.get(SessionManager.KEY_VALUES),User.class);
            getServices(u.getId());
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

    public void getServices(final String idUsuario) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://www.espacioseguro.pe/php_connection/getServices.php?user="+idUsuario;
        System.out.println(url);

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        JSONParser jp = new JSONParser();
                        try {
                            srefresh.setRefreshing(false);
                            JSONArray ja=(JSONArray)jp.parse(response);
                            l.clear();
                            for(int i=0;i<ja.size();i++){
                                l.add((JSONObject)ja.get(i));
                            }
                            adapter.notifyDataSetChanged();
                            md.dismiss();
                        } catch (Exception e) {
                            md.dismiss();
                            new CDialog(getContext()).createAlert("Ocurrió un error",
                                    CDConstants.ERROR,   // Type of dialog
                                    CDConstants.MEDIUM)    //  size of dialog
                                    .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                    .setDuration(2000)   // in milliseconds
                                    .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                                    .show();
                            e.printStackTrace();
                        }
                        md.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        md.dismiss();
                    }
                }
        );
        queue.add(postRequest);
    }

}
