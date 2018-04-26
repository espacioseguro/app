package com.solution.tecno.seguro;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.solution.tecno.seguro.Utils.SessionManager;
import com.solution.tecno.seguro.Utils.User;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RequestFragment extends Fragment {

    RecyclerView activity;
    RequestAdapter adapter;
    List<JSONObject> l=new ArrayList<>();
    SessionManager session;
    static MaterialDialog md;
    User u;

    public RequestFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RequestFragment newInstance() {
        RequestFragment fragment = new RequestFragment();
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
        View view=inflater.inflate(R.layout.fragment_request, container, false);
        activity=view.findViewById(R.id.recycler_view_requests);
        activity.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new RequestAdapter(l);

        activity.setAdapter(adapter);

        new RequestFragment.DoInBackGround().execute();
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
            getRequest();
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

    public void getRequest() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://www.espacioseguro.pe/php_connection/getRequest.php?code="+u.getCod_servicio()+"&user="+u.getId();
        System.out.println(url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        JSONParser jp = new JSONParser();
                        try {
                            JSONArray ja=(JSONArray)jp.parse(response);
                            l.clear();
                            for(int i=0;i<ja.size();i++){
                                l.add((JSONObject)ja.get(i));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Toast.makeText(getContext(),"Intente luego", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            md.dismiss();
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
