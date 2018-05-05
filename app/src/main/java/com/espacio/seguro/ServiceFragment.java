package com.espacio.seguro;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.espacio.seguro.Culqi.Card;
import com.espacio.seguro.Culqi.Token;
import com.espacio.seguro.Culqi.TokenCallback;
import com.example.circulardialog.CDialog;
import com.example.circulardialog.extras.CDConstants;
import com.google.gson.Gson;
import com.espacio.seguro.Utils.SessionManager;
import com.espacio.seguro.Utils.User;
import com.espacio.seguro.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ServiceFragment extends Fragment {

    private int contador=0;
    private String tiempo="Meses";
    private ImageButton plus,minus;
    private TextView tv_contador,tv_time;
    private Button btn_culqi,btn_services;
    private RadioButton p50,p75;
    private int service_type=1;

    User u;
    SessionManager session;

    static MaterialDialog md;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;

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
        session=new SessionManager(getActivity());
        HashMap<String,String> user;
        user = session.getUserDetails();
        Gson g=new Gson();
        u=g.fromJson(user.get(SessionManager.KEY_VALUES),User.class);
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
        btn_culqi= v.findViewById(R.id.btn_culqi);
        btn_services=v.findViewById(R.id.btn_list_services);
        btn_services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                Fragment fr;
                fr=ServicesFragment.newInstance();
                fragmentTransaction.replace(R.id.flaContenido,fr);
                fragmentTransaction.commit();
            }
        });
        p75=v.findViewById(R.id.radio_p75);
        p50=v.findViewById(R.id.radio_p50);
        p75.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
                p50.setChecked(false);
                service_type=1;
            }
        });
        p50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
                p75.setChecked(false);
                service_type=2;
            }
        });

        btn_culqi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contador==0){
                    new CDialog(getContext()).createAlert("El tiempo debe ser mayor a 0",
                            CDConstants.WARNING,   // Type of dialog
                            CDConstants.MEDIUM)    //  size of dialog
                            .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                            .setDuration(2000)   // in milliseconds
                            .setTextSize(CDConstants.SMALL)
                            .setPosition(CDConstants.CENTER)
                            .show();
                }else{
                    LayoutInflater li = LayoutInflater.from(getActivity());
                    View promptsView = li.inflate(R.layout.add_service, null);
                    alertDialogBuilder = new AlertDialog.Builder(getActivity());

                    alertDialogBuilder.setView(promptsView);
                    alertDialog = alertDialogBuilder.create();

                    final EditText service_address= promptsView.findViewById(R.id.add_service_address);
                    final EditText service_alias= promptsView.findViewById(R.id.add_service_name);
                    final Button save_service=promptsView.findViewById(R.id.btn_save_service);
                    final Button cancel_service=promptsView.findViewById(R.id.btn_cancel_service);

                    alertDialog.show();
                    save_service.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (service_address.getText().toString().equals("")) {
                                new CDialog(getContext()).createAlert("Ingrese una dirección",
                                        CDConstants.WARNING,   // Type of dialog
                                        CDConstants.MEDIUM)    //  size of dialog
                                        .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                        .setDuration(2000)   // in milliseconds
                                        .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                                        .setPosition(CDConstants.CENTER)
                                        .show();
                            } else {
                                alertDialog.dismiss();
                                md=new MaterialDialog.Builder(getContext())
                                        .content("Registrando Servicio")
                                        .progress(true,0)
                                        .cancelable(false)
                                        .backgroundColor(Color.WHITE)
                                        .contentColor(Color.BLACK)
                                        .titleColor(Color.RED)
                                        .show();
                                addService(u.getId(),service_address.getText().toString(),service_alias.getText().toString());
//                                culqiTest();
                            }
                        }
                    });

                    cancel_service.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                }
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            contador++;
            tv_contador.setText(String.valueOf(contador));
            if(contador==1){
                tv_time.setText(String.valueOf(contador)+" "+"mes");
            }else{
                tv_time.setText(String.valueOf(contador)+" "+"meses");
            }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contador>0){
                    contador--;
                    tv_contador.setText(String.valueOf(contador));
                    if(contador==1){
                        tv_time.setText(String.valueOf(contador)+" "+"mes");
                    }else{
                        tv_time.setText(String.valueOf(contador)+" "+"meses");
                    }
                }
            }
        });
        return v;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_p75:
                if (checked)
                    // Pirates are the best
                    Toast.makeText(getActivity(), ((RadioButton) view).getText(), Toast.LENGTH_SHORT).show();
                    break;
            case R.id.radio_p50:
                if (checked)
                    // Ninjas rule
                    Toast.makeText(getActivity(), ((RadioButton) view).getText(), Toast.LENGTH_SHORT).show();
                    break;
        }
    }

    public void addService(String idUser,String address,String alias){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String params="?user="+idUser+
                "&month="+contador+
                "&type="+service_type+
                "&address="+ Uri.encode(address)+
                "&alias="+Uri.encode(alias);
        String url = "https://www.espacioseguro.pe/php_connection/addService.php"+params;
        System.out.println("url: "+url);

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            md.dismiss();
                            if(response=="0"){
                                new CDialog(getContext()).createAlert("Error al registrar",
                                        CDConstants.ERROR,   // Type of dialog
                                        CDConstants.MEDIUM)    //  size of dialog
                                        .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                        .setDuration(2000)   // in milliseconds
                                        .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                                        .show();
                            }else{
                                new CDialog(getContext()).createAlert("Listo",
                                        CDConstants.SUCCESS,   // Type of dialog
                                        CDConstants.MEDIUM)    //  size of dialog
                                        .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                        .setDuration(2000)   // in milliseconds
                                        .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                                        .show();
                            }
                        } catch (Exception e) {
                            md.dismiss();
                            new CDialog(getContext()).createAlert("Error al registrar",
                                    CDConstants.ERROR,   // Type of dialog
                                    CDConstants.MEDIUM)    //  size of dialog
                                    .setAnimation(CDConstants.SCALE_FROM_TOP_TO_TOP)     //  Animation for enter/exit
                                    .setDuration(2000)   // in milliseconds
                                    .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                                    .show();
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

    public void culqiTest(){
        Card card = new Card("411111111111111", "123", 9, 2020, "wm@wm.com");

        Token token = new Token("{pk_live_7o60Ye8cTqfKsFiF}");

        token.createToken(getApplicationContext(), card, new TokenCallback() {
            @Override
            public void onSuccess(JSONObject token) {
                // token
                try {
                    md.dismiss();
                    String token_code=token.get("id").toString();
                    Toast.makeText(getActivity(),token_code , Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    md.dismiss();
                    e.printStackTrace();
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception error) {
                md.dismiss();
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
