package com.solution.tecno.espacioseguro;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder>{

    List<JSONObject> l =new ArrayList<>();
    private Context c;
    View.OnClickListener listener;
    private MaterialDialog md;
    private String name_alarm;
    private EditText userInput;

    public AlarmAdapter(List<JSONObject> l) {
        this.l = l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        c=parent.getContext();
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
                getNameAlarm((String)o.get("id"));

                LayoutInflater li = LayoutInflater.from(c);
                View promptsView = li.inflate(R.layout.edit_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);

                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.setTitle("Editando alarma");
                userInput= promptsView.findViewById(R.id.edit_name_alarm);


                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        md=new MaterialDialog.Builder(c)
                                                .content("Guardando")
                                                .progress(true,0)
                                                .cancelable(false)
                                                .backgroundColor(Color.WHITE)
                                                .contentColor(Color.BLACK)
                                                .show();
                                        updateAlarm((String)o.get("id"),userInput.getText().toString());
                                        holder.name.setText(userInput.getText().toString());
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
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
        ImageView edit;

        private ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.item_alarm_name);
            code=itemView.findViewById(R.id.item_alarm_code);
            edit=itemView.findViewById(R.id.item_alarm_edit);
        }
    }

    public void getNameAlarm(String idAlarm) {

        RequestQueue queue = Volley.newRequestQueue(c);
        String url = "https://www.espacioseguro.pe/php_connection/getAlarmInfo.php?idAlarma="+idAlarm;

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        JSONParser jp = new JSONParser();
                        try {
                            JSONArray ja=(JSONArray)jp.parse(response);
                            for(int i=0;i<ja.size();i++){
                                JSONObject o=(JSONObject)ja.get(i);
                                name_alarm=(String)o.get("nombre");
                                userInput.setText(name_alarm);
                            }
                        } catch (Exception e) {
                            Toast.makeText(c,"Intente luego", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(c, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(postRequest);
    }

    public void updateAlarm(String idAlarm,String name) {

        RequestQueue queue = Volley.newRequestQueue(c);
        String url = "https://www.espacioseguro.pe/php_connection/updateAlarmInfo.php?idAlarma="+idAlarm+"&nombre="+name;

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
}
