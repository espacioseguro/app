package com.solution.tecno.espacioseguro;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

public class SharedUsersAdapter extends RecyclerView.Adapter<SharedUsersAdapter.ViewHolder>{

    List<JSONObject> l =new ArrayList<>();
    Context c;
    View.OnClickListener listener;
    private MaterialDialog md;

    public SharedUsersAdapter(List<JSONObject> l) {
        this.l = l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        c=parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shared,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final JSONObject o=l.get(position);
        holder.name.setText((String)o.get("nombre"));
        holder.itemView.setTag(o);
        holder.itemView.setOnClickListener(listener);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
                alertDialogBuilder.setMessage("¿Está seguro de guardar esta ubicación?")
                        .setCancelable(false)
                        .setPositiveButton("Eliminar",
                                new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int id){
                                        deleteUser((String)o.get("id"));
                                    }
                                });
                alertDialogBuilder.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
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
        md=new MaterialDialog.Builder(c)
                .content("Eliminando")
                .progress(true,0)
                .cancelable(false)
                .backgroundColor(Color.WHITE)
                .contentColor(Color.BLACK)
                .show();
        RequestQueue queue = Volley.newRequestQueue(c);
        String url = "https://www.espacioseguro.pe/php_connection/deleteUser.php?user="+idUser;

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            md.dismiss();
                            SharedUsersFragment.newInstance().getSharedUsers();
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
