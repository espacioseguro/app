package com.espacio.seguro;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
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
import com.espacio.seguro.Utils.SessionManager;
import com.espacio.seguro.Utils.User;
import com.espacio.seguro.R;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AlarmFragment extends Fragment {

    RecyclerView activity;
    AlarmAdapter adapter;
    List<JSONObject> l=new ArrayList<>();
    SessionManager session;
    static MaterialDialog md;
    Button save_alarm,save_code,cancel_alarm,cancel_code;
    ImageView new_alarm,new_code,turn_off_all_alarms,turn_on_all_alarms;
    EditText service_code,alarm_code,alarm_name,alarm_service_code;
    User u;
    SwipeRefreshLayout srefresh;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;

    CDialog cDialog;

    public AlarmFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AlarmFragment newInstance() {
        AlarmFragment fragment = new AlarmFragment();
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
        View view=inflater.inflate(R.layout.fragment_alarm, container, false);
        srefresh=view.findViewById(R.id.swiperefresh_alarm);
        activity=view.findViewById(R.id.recycler_view_alarm);
        activity.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new AlarmAdapter(l);

        srefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srefresh.setRefreshing(true);
                getAlarms(u.getCod_servicio());
            }
        });

        activity.setAdapter(adapter);
        turn_off_all_alarms=view.findViewById(R.id.turn_off_alarms);
        turn_off_all_alarms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md=new MaterialDialog.Builder(getContext())
                        .content("Apagando alarmas")
                        .progress(true,0)
                        .cancelable(false)
                        .backgroundColor(Color.WHITE)
                        .contentColor(Color.BLACK)
                        .titleColor(Color.RED)
                        .show();
                disableAlarms();
            }
        });

        turn_on_all_alarms=view.findViewById(R.id.turn_on_alarms);
        turn_on_all_alarms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md=new MaterialDialog.Builder(getContext())
                        .content("Encendiendo alarmas")
                        .progress(true,0)
                        .cancelable(false)
                        .backgroundColor(Color.WHITE)
                        .contentColor(Color.BLACK)
                        .titleColor(Color.RED)
                        .show();
                enableAlarms();
            }
        });
        new_code=view.findViewById(R.id.btn_service_code);
        new_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.add_code, null);
                alertDialogBuilder = new AlertDialog.Builder(getActivity());

                alertDialogBuilder.setView(promptsView);
                alertDialog = alertDialogBuilder.create();

                service_code= promptsView.findViewById(R.id.add_service_code);
                save_code=promptsView.findViewById(R.id.btn_new_code_save);

                cancel_code=promptsView.findViewById(R.id.btn_new_code_cancel);

                alertDialog.show();
                save_code.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (service_code.getText().toString().equals("")) {
                            cDialog=new CDialog(getContext()).createAlert("Ingrese un Código de Servicio",
                                    CDConstants.WARNING,   // Type of dialog
                                    CDConstants.MEDIUM)    //  size of dialog
                                    .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                    .setDuration(2000)   // in milliseconds
                                    .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                                    .setPosition(CDConstants.CENTER);
                            cDialog.show();
                        } else {
                            md=new MaterialDialog.Builder(getContext())
                                    .content("Enviando Solicitud")
                                    .progress(true,0)
                                    .cancelable(false)
                                    .backgroundColor(Color.WHITE)
                                    .contentColor(Color.BLACK)
                                    .titleColor(Color.RED)
                                    .show();
                            addServiceCode(u.getId(),service_code.getText().toString());
                            alertDialog.dismiss();
                        }
                    }
                });

                cancel_code.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        new_alarm=view.findViewById(R.id.btn_new_alarm);
        new_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(getContext());
                View promptsView = li.inflate(R.layout.add_alarm, null);
                alertDialogBuilder = new AlertDialog.Builder(getContext());

                alertDialogBuilder.setView(promptsView);
                alertDialog = alertDialogBuilder.create();
                alarm_code= promptsView.findViewById(R.id.add_new_alarm);
                alarm_name=promptsView.findViewById(R.id.add_new_alarm_name);
                alarm_service_code=promptsView.findViewById(R.id.add_new_service);
                save_alarm=promptsView.findViewById(R.id.btn_new_alarm_save);
                cancel_alarm=promptsView.findViewById(R.id.btn_new_alarm_cancel);

                alarm_service_code.setText(u.getCod_servicio());
                alertDialog.show();
                cancel_alarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                save_alarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(alarm_code.getText().toString().equals("") || alarm_name.getText().toString().equals("")){
                            cDialog=new CDialog(getContext()).createAlert("Completa todos los campos",
                                    CDConstants.WARNING,   // Type of dialog
                                    CDConstants.MEDIUM)    //  size of dialog
                                    .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                    .setDuration(2000)   // in milliseconds
                                    .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                                    .setPosition(CDConstants.CENTER);
                            cDialog.show();
                        }else{
                            md=new MaterialDialog.Builder(getContext())
                                    .content("Guardando")
                                    .progress(true,0)
                                    .cancelable(true)
                                    .backgroundColor(Color.WHITE)
                                    .contentColor(Color.BLACK)
                                    .titleColor(Color.RED)
                                    .show();
                            addAlarm(alarm_code.getText().toString(),alarm_name.getText().toString());
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });


        new AlarmFragment.DoInBackGround().execute();
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
            getAlarms(u.getCod_servicio());
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

    public void getAlarms(final String service_code) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://www.espacioseguro.pe/php_connection/getAlarms.php?cod_service="+service_code;
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
                            srefresh.setRefreshing(false);
                            adapter.notifyDataSetChanged();
                            md.dismiss();
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

    public void enableAlarm(String id,String status){
        md.show();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://www.espacioseguro.pe/php_connection/enableAlarm.php?idAlarm="+id+"&status="+status;

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            getAlarms(u.getCod_servicio());
                        } catch (Exception e) {
                            md.dismiss();
                            Toast.makeText(getContext(),"Intente luego", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
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

    public void disableAlarms(){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://www.espacioseguro.pe/php_connection/disableAlarms.php?idService="+u.getCod_servicio();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            getAlarms(u.getCod_servicio());
                        } catch (Exception e) {
                            md.dismiss();
                            Toast.makeText(getContext(),"Intente luego", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
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

    public void enableAlarms(){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://www.espacioseguro.pe/php_connection/enableAlarms.php?idService="+u.getCod_servicio();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            getAlarms(u.getCod_servicio());
                        } catch (Exception e) {
                            md.dismiss();
                            Toast.makeText(getContext(),"Intente luego", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
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

    public void addServiceCode(String idUser,final String service_code){
        md.show();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://www.espacioseguro.pe/php_connection/sendRequest.php?code="+service_code+"&idUser="+idUser;
        System.out.println("url: "+url);

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            md.dismiss();
                            cDialog=new CDialog(getContext()).createAlert("Listo",
                                    CDConstants.SUCCESS,   // Type of dialog
                                    CDConstants.MEDIUM)    //  size of dialog
                                    .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                    .setDuration(2000)   // in milliseconds
                                    .setTextSize(CDConstants.NORMAL_TEXT_SIZE);
                            cDialog.show();
                            NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                            View header=navigationView.getHeaderView(0);
                            ((HomeActivity)getContext()).updateService(u.getName_servicio());
                            loginRequest(u.getcorreo(),u.getClave());
                        } catch (Exception e) {
                            md.dismiss();
                            cDialog=new CDialog(getContext()).createAlert("Error al enviar solicitud",
                                    CDConstants.ERROR,   // Type of dialog
                                    CDConstants.MEDIUM)    //  size of dialog
                                    .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                    .setDuration(2000)   // in milliseconds
                                    .setTextSize(CDConstants.NORMAL_TEXT_SIZE);
                            cDialog.show();
                            Toast.makeText(getContext(),"Intente luego", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
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

    public void loginRequest(final String email,final String psw){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String params="?usuario="+email+"&psw="+psw;
        String url = "https://espacioseguro.pe/php_connection/login.php"+params;

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        if(response!="[]"){
                            JSONParser p=new JSONParser();
                            try {
                                org.json.simple.JSONArray a=(org.json.simple.JSONArray)p.parse(response);
                                if(a.size()!=0){
                                    org.json.simple.JSONObject o=(org.json.simple.JSONObject)a.get(0);
                                    System.out.println(o.toJSONString());
                                    session.createLoginSession(email,o.toJSONString());
                                    HashMap<String,String> user=session.getUserDetails();
                                    setUserValues(user.get(SessionManager.KEY_VALUES));
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        // Access the RequestQueue through your singleton class.
        queue.add(postRequest);
    }

    public void setUserValues(String datos){
        session=new SessionManager(getContext());
        HashMap<String,String> user;
        user = session.getUserDetails();
        Gson g=new Gson();
        u=g.fromJson(datos,User.class);
        getAlarms(u.getCod_servicio());
    }

    public void addAlarm(String alarm,String name){
        md.show();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String params="?code="+alarm+"&name="+name+"&service="+u.getCod_servicio();
        String url = "https://www.espacioseguro.pe/php_connection/addAlarm.php"+params;
        System.out.println("url: "+url);

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            md.dismiss();
                            cDialog=new CDialog(getContext()).createAlert("Listo",
                                    CDConstants.SUCCESS,   // Type of dialog
                                    CDConstants.MEDIUM)    //  size of dialog
                                    .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                    .setDuration(2000)   // in milliseconds
                                    .setTextSize(CDConstants.NORMAL_TEXT_SIZE);
                            cDialog.show();
                            getAlarms(u.getCod_servicio());
                        } catch (Exception e) {
                            md.dismiss();
                            cDialog=new CDialog(getContext()).createAlert("Error al agregar alarma",
                                    CDConstants.ERROR,   // Type of dialog
                                    CDConstants.MEDIUM)    //  size of dialog
                                    .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                    .setDuration(2000)   // in milliseconds
                                    .setTextSize(CDConstants.NORMAL_TEXT_SIZE);
                            cDialog.show();
                            Toast.makeText(getContext(),"Intente luego", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
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
