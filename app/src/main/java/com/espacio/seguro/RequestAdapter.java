package com.espacio.seguro;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder>{

    List<JSONObject> l =new ArrayList<>();
    private Context c;
    View.OnClickListener listener;
    private MaterialDialog md;
    SessionManager session;
    User u;

    public RequestAdapter(List<JSONObject> l) {
        this.l = l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        c=parent.getContext();
        session=new SessionManager(c);
        HashMap<String,String> user=session.getUserDetails();
        Gson g=new Gson();
        u=g.fromJson(user.get(SessionManager.KEY_VALUES),User.class);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request,parent,false));
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final JSONObject o=l.get(position);
        holder.name.setText((String)o.get("nombre"));
        holder.itemView.setTag(o);
        holder.itemView.setOnClickListener(listener);
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md=new MaterialDialog.Builder(c)
                        .content("Actualizando")
                        .progress(true,0)
                        .cancelable(false)
                        .backgroundColor(Color.WHITE)
                        .contentColor(Color.BLACK)
                        .show();

                Toast.makeText(c,(String)o.get("id"),Toast.LENGTH_SHORT).show();

                acceptRequest((String)o.get("sol_id"),(String)o.get("id"));
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md=new MaterialDialog.Builder(c)
                        .content("Eliminando")
                        .progress(true,0)
                        .cancelable(false)
                        .backgroundColor(Color.WHITE)
                        .contentColor(Color.BLACK)
                        .show();
                deleteRequest((String)o.get("sol_id"));
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
        ImageView accept,delete;

        private ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.item_request_name);
            accept=itemView.findViewById(R.id.item_acept_request);
            delete=itemView.findViewById(R.id.item_delete_request);
        }
    }

    public void deleteRequest(String idRequest) {

        RequestQueue queue = Volley.newRequestQueue(c);
        String url = "https://www.espacioseguro.pe/php_connection/deleteRequest.php?idRequest="+idRequest;
        System.out.println(url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            getRequest();
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

    public void acceptRequest(final String idRequest,String user) {

        RequestQueue queue = Volley.newRequestQueue(c);
        String params="?code="+u.getCod_servicio()+"&user="+user;
        String url = "https://www.espacioseguro.pe/php_connection/updateUserCode.php"+params;
        System.out.println(url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            deleteRequest(idRequest);
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

    private void getRequest() {
        RequestQueue queue = Volley.newRequestQueue(c);
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
                            RequestAdapter.this.notifyDataSetChanged();

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
