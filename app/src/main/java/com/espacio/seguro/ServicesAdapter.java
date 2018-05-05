package com.espacio.seguro;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
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
import com.espacio.seguro.Utils.SessionManager;
import com.espacio.seguro.Utils.User;
import com.espacio.seguro.R;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ViewHolder>{

    List<JSONObject> l =new ArrayList<>();
    private Context c;
    View.OnClickListener listener;
    private MaterialDialog md;
    private String name_alarm;
    private EditText serviceInput;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;

    SessionManager session;
    User u;

    public ServicesAdapter(List<JSONObject> l) {
        this.l = l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        c=parent.getContext();
        session=new SessionManager(c);
        HashMap<String,String> user=session.getUserDetails();
        Gson g=new Gson();
        u=g.fromJson(user.get(SessionManager.KEY_VALUES),User.class);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service,parent,false));
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final JSONObject o=l.get(position);
        holder.name.setText((String)o.get("name"));
        holder.code.setText((String)o.get("cod_servicio"));

        if(o.get("active").equals("0")){
            holder.itemView.setBackgroundColor(Color.parseColor("#fb1a01"));
        }else{
            holder.itemView.setBackgroundColor(Color.parseColor("#00a947"));
        }
        holder.itemView.setTag(o);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(c);
                View promptsView = li.inflate(R.layout.update_user_service, null);
                alertDialogBuilder = new AlertDialog.Builder(c);
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.setCancelable(false);
                TextView title=promptsView.findViewById(R.id.update_service_name);
                Button actualizar,cancel;
                actualizar=promptsView.findViewById(R.id.btn_update_user_service_save);
                cancel=promptsView.findViewById(R.id.btn_user_service_cancel);
                title.setText("¿Seguro que desea cambiar al servicio: "+holder.name.getText()+"?");
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                actualizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        md=new MaterialDialog.Builder(c)
                                .content("Actualizando")
                                .progress(true,0)
                                .cancelable(false)
                                .backgroundColor(Color.WHITE)
                                .contentColor(Color.BLACK)
                                .show();
                        changeService(u.getId(),holder.code.getText().toString());
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
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(c);
                View promptsView = li.inflate(R.layout.edit_service, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);

                alertDialogBuilder.setView(promptsView);
                serviceInput= promptsView.findViewById(R.id.edit_name_service);
                serviceInput.setText(holder.name.getText());
                Button save=promptsView.findViewById(R.id.btn_service_name_save);
                Button cancel=promptsView.findViewById(R.id.btn_service_name_cancel);

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
                        updateService((String)o.get("id"),serviceInput.getText().toString());
                        holder.name.setText(serviceInput.getText().toString());
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

        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", holder.code.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(c,"Código copiado",Toast.LENGTH_LONG).show();
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
        ImageView edit,copy;

        private ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.item_service_name);
            code=itemView.findViewById(R.id.item_service_code);
            edit=itemView.findViewById(R.id.item_service_edit);
            copy=itemView.findViewById(R.id.item_service_copy);
        }
    }

    public void updateService(String idAlarm,String name) {

        RequestQueue queue = Volley.newRequestQueue(c);
        String url = "https://www.espacioseguro.pe/php_connection/updateServiceInfo.php?idService="+idAlarm+"&nombre="+name;
        System.out.println(url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            md.dismiss();
                            if(response=="0"){
                                new CDialog(c).createAlert("Error al actualizar",
                                        CDConstants.ERROR,   // Type of dialog
                                        CDConstants.MEDIUM)    //  size of dialog
                                        .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                        .setDuration(2000)   // in milliseconds
                                        .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                                        .show();
                            }else{
                                new CDialog(c).createAlert("Listo",
                                        CDConstants.SUCCESS,   // Type of dialog
                                        CDConstants.MEDIUM)    //  size of dialog
                                        .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                        .setDuration(2000)   // in milliseconds
                                        .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                                        .show();
                                loginRequest(u.getcorreo(),u.getClave());
                            }
                        } catch (Exception e) {
                            md.dismiss();
                            new CDialog(c).createAlert("Error al actualizar",
                                    CDConstants.ERROR,   // Type of dialog
                                    CDConstants.MEDIUM)    //  size of dialog
                                    .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                    .setDuration(2000)   // in milliseconds
                                    .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                                    .show();
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
                        //pDialog.dismiss();
                        md.dismiss();
                    }
                }
        );
        queue.add(postRequest);
    }

    public void changeService(String user, final String service) {

        RequestQueue queue = Volley.newRequestQueue(c);
        String params="?code="+service+"&user="+user;
        String url = "https://www.espacioseguro.pe/php_connection/updateUserCode.php"+params;
        System.out.println(url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            md.dismiss();
//                            u.setCod_servicio(service);
                            loginRequest(u.getcorreo(),u.getClave());
                            new CDialog(c).createAlert("Listo",
                                    CDConstants.SUCCESS,   // Type of dialog
                                    CDConstants.MEDIUM)    //  size of dialog
                                    .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                    .setDuration(2000)   // in milliseconds
                                    .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                                    .show();
                        } catch (Exception e) {
                            md.dismiss();
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

    public void setUserValues(String datos){
        Gson gson=new Gson();
        User u;
        u=gson.fromJson(datos,User.class);
        ((HomeActivity)c).updateService(u.getName_servicio());
    }

    public void loginRequest(final String email,final String psw){
        RequestQueue queue = Volley.newRequestQueue(c);
        String params="?usuario="+email+"&psw="+psw;
        String url = "https://espacioseguro.pe/php_connection/login.php"+params;

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JSONParser p=new JSONParser();
                        try {
                            org.json.simple.JSONArray a=(org.json.simple.JSONArray)p.parse(response);
                            if(a.size()!=0){
                                org.json.simple.JSONObject o=(org.json.simple.JSONObject)a.get(0);
                                String result=o.toJSONString();
                                session.createLoginSession(email,result);
                                HashMap<String,String> user=session.getUserDetails();
                                setUserValues(user.get(SessionManager.KEY_VALUES));
                            }
                        } catch (Exception e) {
                            System.out.println(e);

                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        queue.add(postRequest);
    }
}
