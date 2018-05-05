package com.espacio.seguro;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SharedUsersAdapter extends RecyclerView.Adapter<SharedUsersAdapter.ViewHolder>{

    List<JSONObject> l =new ArrayList<>();
    Context c;
    View.OnClickListener listener;
    private MaterialDialog md;
    SessionManager session;
    User u;

    public SharedUsersAdapter(List<JSONObject> l) {
        this.l = l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        c=parent.getContext();
        session=new SessionManager(c);
        HashMap<String,String> user=session.getUserDetails();
        Gson g=new Gson();
        u=g.fromJson(user.get(SessionManager.KEY_VALUES),User.class);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shared,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final JSONObject o=l.get(position);
        holder.name.setText((String)o.get("nombre"));
        holder.itemView.setTag(o);
        holder.itemView.setOnClickListener(listener);
        holder.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(c);
                View promptsView = li.inflate(R.layout.delete_shared_user, null);
                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(c);
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.setCancelable(false);
                TextView title=promptsView.findViewById(R.id.delete_name_shared_user);
                Button delete,cancel;
                delete=promptsView.findViewById(R.id.btn_delete_shared_user_save);
                cancel=promptsView.findViewById(R.id.btn_delete_shared_user_cancel);
                title.setText("¿Seguro que desea eliminar al usuario: "+holder.name.getText()+"?");
                final android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
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
                        deleteUser((String)o.get("id"));
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                        alertDialog.dismiss();
                        alertDialog.hide();
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name;
        ImageView trash;

        public ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.item_shared_name);
            trash=itemView.findViewById(R.id.item_shared_trash);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public void deleteUser(String idUser) {
        RequestQueue queue = Volley.newRequestQueue(c);
        String url = "https://www.espacioseguro.pe/php_connection/deleteUser.php?user="+idUser;

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            md.dismiss();
                            getSharedUsers();
                            new CDialog(c).createAlert("Usuario eliminado",
                                    CDConstants.SUCCESS,   // Type of dialog
                                    CDConstants.MEDIUM)    //  size of dialog
                                    .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                    .setDuration(2000)   // in milliseconds
                                    .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                                    .show();
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

    public void getSharedUsers() {

        RequestQueue queue = Volley.newRequestQueue(c);
        String params="?code="+u.getCod_servicio()+"&user="+u.getId();
        String url = "https://www.espacioseguro.pe/php_connection/getSharedUsers.php"+params;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONParser jp = new JSONParser();
                        try {
                            JSONArray ja=(JSONArray)jp.parse(response);
                            l.clear();
                            for(int i=0;i<ja.size();i++){
                                l.add((JSONObject)ja.get(i));
                            }
                            SharedUsersAdapter.this.notifyDataSetChanged();
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
                        md.dismiss();
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(c, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(postRequest);
    }

}
