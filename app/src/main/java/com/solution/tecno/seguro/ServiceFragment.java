package com.solution.tecno.seguro;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class ServiceFragment extends Fragment {

    private int contador=0;
    private String tiempo="Meses";
    private ImageButton plus,minus;
    private TextView tv_contador,tv_time;
    private Spinner time;
    private Button btn_culqi;

    public ServiceFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ServiceFragment newInstance() {
        ServiceFragment fragment = new ServiceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_service, container, false);
        plus= v.findViewById(R.id.btn_plus);
        minus= v.findViewById(R.id.btn_minus);
        tv_contador= v.findViewById(R.id.tv_count);
        tv_time= v.findViewById(R.id.tv_time);
        time= v.findViewById(R.id.spinner);
        btn_culqi= v.findViewById(R.id.btn_culqi);

        btn_culqi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),CulqiActivity.class);
                startActivity(i);
            }
        });

        time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Snackbar.make(view, "Periodo: " + adapterView.getItemAtPosition(i), Snackbar.LENGTH_LONG).show();
                tiempo=(String)adapterView.getItemAtPosition(i);
                if(tiempo.equals("Meses")){
                    if(contador==1){
                        tv_time.setText(String.valueOf(contador)+" "+"mes");
                    }else{
                        tv_time.setText(String.valueOf(contador)+" "+"meses");
                    }
                }else{
                    if(contador==1){
                        tv_time.setText(String.valueOf(contador)+" "+"semana");
                    }else{
                        tv_time.setText(String.valueOf(contador)+" "+"semanas");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contador++;
                tv_contador.setText(String.valueOf(contador));
                if(tiempo.equals("Meses")){
                    if(contador==1){
                        tv_time.setText(String.valueOf(contador)+" "+"mes");
                    }else{
                        tv_time.setText(String.valueOf(contador)+" "+"meses");
                    }
                }else{
                    if(contador==1){
                        tv_time.setText(String.valueOf(contador)+" "+"semana");
                    }else{
                        tv_time.setText(String.valueOf(contador)+" "+"semanas");
                    }
                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contador>0){
                    contador--;
                    tv_contador.setText(String.valueOf(contador));
                    if(tiempo.equals("Meses")){
                        if(contador==1){
                            tv_time.setText(String.valueOf(contador)+" "+"mes");
                        }else{
                            tv_time.setText(String.valueOf(contador)+" "+"meses");
                        }
                    }else{
                        if(contador==1){
                            tv_time.setText(String.valueOf(contador)+" "+"semana");
                        }else{
                            tv_time.setText(String.valueOf(contador)+" "+"semanas");
                        }
                    }
                }
            }
        });
        return v;
    }

    }
