package com.solution.tecno.seguro;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder>{

    List<JSONObject> l =new ArrayList<>();
    private Context c;
    View.OnClickListener listener;
    private MaterialDialog md;
    private String name_alarm;
    private EditText userInput;
    User u;
    SessionManager session;

    public AlarmAdapter(List<JSONObject> l) {
        this.l = l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        c=parent.getContext();
        session=new SessionManager(c);
        HashMap<String,String> user;
        user = session.getUserDetails();
        Gson g=new Gson();
        u=g.fromJson(user.get(SessionManager.KEY_VALUES),User.class);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm,parent,false));
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final JSONObject o=l.get(position);
        holder.name.setText((String)o.get("nombre"));
        holder.code.setText((String)o.get("codProd"));

        if(o.get("estado").equals("0")){
            holder.itemView.setBackgroundColor(Color.parseColor("#fb1a01"));
        }else{
            holder.itemView.setBackgroundColor(Color.parseColor("#00a947"));
        }
        holder.itemView.setTag(o);
        holder.itemView.setOnClickListener(listener);
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(c);
                View promptsView = li.inflate(R.layout.edit_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);

                alertDialogBuilder.setView(promptsView);
                userInput= promptsView.findViewById(R.id.edit_name_alarm);
                userInput.setText(holder.name.getText());
                Button save=promptsView.findViewById(R.id.btn_alarm_name_save);
                Button cancel=promptsView.findViewById(R.id.btn_alarm_name_cancel);

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        md=new MaterialDialog.Builder(c)
                                .content("Guardando")
                                .progress(true,0)
                                .cancelable(false)
                                .backgroundColor(Color.WHITE)
                                .contentColor(Color.BLACK)
                                .show();
                        updateAlarm((String)o.get("id"),userInput.getText().toString());
                        holder.name.setText(userInput.getText().toString());
                        alertDialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(c);
                View promptsView = li.inflate(R.layout.delete_alarm, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.setCancelable(false);
                TextView title=promptsView.findViewById(R.id.delete_name_alarm);
                Button delete,cancel;
                delete=promptsView.findViewById(R.id.btn_delete_alarm_save);
                cancel=promptsView.findViewById(R.id.btn_delete_alarm_cancel);
                title.setText("¿Seguro que desea eliminar la alarma: "+holder.name.getText()+"?");
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                        alertDialog.dismiss();
                        alertDialog.hide();
                        md=new MaterialDialog.Builder(c)
                                .content("Eliminando")
                                .progress(true,0)
                                .cancelable(false)
                                .backgroundColor(Color.WHITE)
                                .contentColor(Color.BLACK)
                                .show();
                        deleteAlarm((String)o.get("id"));
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        if(l==null){
            return 0;
        }else {
            return l.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView name,code;
        ImageView edit,delete;

        private ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.item_alarm_name);
            code=itemView.findViewById(R.id.item_alarm_code);
            edit=itemView.findViewById(R.id.item_alarm_edit);
            delete=itemView.findViewById(R.id.item_alarm_delete);
        }
    }

    public void updateAlarm(String idAlarm,String name) {

        RequestQueue queue = Volley.newRequestQueue(c);
        String url = "https://www.espacioseguro.pe/php_connection/updateAlarmInfo.php?idAlarma="+idAlarm+"&nombre="+ Uri.encode(name);
        System.out.println(url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            md.dismiss();
                            //pDialog.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(c,"Intente luego", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            //pDialog.dismiss();
                            md.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        new CDialog(c).createAlert("Ocurrió un error",
                                CDConstants.ERROR,   // Type of dialog
                                CDConstants.MEDIUM)    //  size of dialog
                                .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                .setDuration(2000)   // in milliseconds
                                .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                                .show();
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(c, error.toString(), Toast.LENGTH_SHORT).show();
                        //pDialog.dismiss();
                        md.dismiss();
                    }
                }
        );
        queue.add(postRequest);
    }

    public void deleteAlarm(String idAlarm) {

        RequestQueue queue = Volley.newRequestQueue(c);
        String url = "https://www.espacioseguro.pe/php_connection/deleteAlarm.php?alarm="+idAlarm;

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            md.dismiss();
                            getAlarms();
                            new CDialog(c).createAlert("Alarma eliminada",
                                    CDConstants.SUCCESS,   // Type of dialog
                                    CDConstants.MEDIUM)    //  size of dialog
                                    .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                    .setDuration(2000)   // in milliseconds
                                    .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                                    .show();
                            //pDialog.dismiss();
                        } catch (Exception e) {
                            new CDialog(c).createAlert("Ocurrió un error",
                                    CDConstants.ERROR,   // Type of dialog
                                    CDConstants.MEDIUM)    //  size of dialog
                                    .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                    .setDuration(2000)   // in milliseconds
                                    .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                                    .show();
                            Toast.makeText(c,"Intente luego", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            //pDialog.dismiss();
                            md.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(c, error.toString(), Toast.LENGTH_SHORT).show();
                        //pDialog.dismiss();
                        md.dismiss();
                    }
                }
        );
        queue.add(postRequest);
    }

    public void getAlarms() {
        RequestQueue queue = Volley.newRequestQueue(c);
        String url = "https://www.espacioseguro.pe/php_connection/getAlarms.php?cod_service="+u.getCod_servicio();
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
                            AlarmAdapter.this.notifyDataSetChanged();
                        } catch (Exception e) {
                            Toast.makeText(c,"Intente luego", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            md.dismiss();
                        }
                        md.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(c, error.toString(), Toast.LENGTH_SHORT).show();
                        md.dismiss();
                    }
                }
        );
        queue.add(postRequest);
    }
}
