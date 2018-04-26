package com.solution.tecno.seguro;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    List<JSONObject> l =new ArrayList<>();
    Context c;
    View.OnClickListener listener;

    public NotificationAdapter(List<JSONObject> l) {
        this.l = l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        c=parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final JSONObject o=l.get(position);
        holder.action.setText((String)o.get("info"));
        holder.fecha.setText((String)o.get("reg_date"));
        holder.itemView.setTag(o);
        holder.itemView.setOnClickListener(listener);
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

        TextView action,fecha;

        public ViewHolder(View itemView) {
            super(itemView);
            action=itemView.findViewById(R.id.item_notification);
            fecha=itemView.findViewById(R.id.item_fecha);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
