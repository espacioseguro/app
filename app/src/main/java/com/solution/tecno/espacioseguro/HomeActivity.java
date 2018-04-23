package com.solution.tecno.espacioseguro;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.solution.tecno.espacioseguro.Bluetooth.BluetoothChat;
import com.solution.tecno.espacioseguro.Firebase.Constants;
import com.solution.tecno.espacioseguro.Firebase.MyNotificationManager;
import com.solution.tecno.espacioseguro.Utils.SessionManager;
import com.solution.tecno.espacioseguro.Utils.User;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.file.CopyOption;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    SessionManager session;
    User u;
    TextView tv_name,tv_code_service;
    String result="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseApp.initializeApp(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        session=new SessionManager(getApplicationContext());
        HashMap<String,String> user=session.getUserDetails();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View header=navigationView.getHeaderView(0);
        tv_name= header.findViewById(R.id.header_name);
        tv_code_service=header.findViewById(R.id.header_service_code);

        setUserValues(user.get(SessionManager.KEY_VALUES));

        user = session.getUserDetails();
        Gson g=new Gson();
        u=g.fromJson(user.get(SessionManager.KEY_VALUES),User.class);
        RequestQueue queue = Volley.newRequestQueue(this);
        String params="?idUser="+u.getId()+"&fcm="+FirebaseInstanceId.getInstance().getToken();
        String url = "https://espacioseguro.pe/php_connection/updateFCM.php"+params;

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        if(response!="[]"){
                            loginRequest(u.getcorreo(),u.getClave());
                            u.setFcm(FirebaseInstanceId.getInstance().getToken());
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

        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Fragment fr;
        fr= AlarmFragment.newInstance();
        fragmentTransaction.replace(R.id.flaContenido,fr);
        fragmentTransaction.commit();
    }

    public void setUserValues(String datos){
        Gson gson=new Gson();
        User u=gson.fromJson(datos,User.class);
        tv_name.setText(u.getnombre());
        tv_code_service.setText(u.getCod_servicio());
        tv_code_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Mantén presionado para copiar",Toast.LENGTH_LONG).show();
            }
        });
        tv_code_service.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", tv_code_service.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(),"Código copiado",Toast.LENGTH_LONG).show();
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                finish();
            }
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Fragment fr;
        if (id == R.id.nav_maps) {
            fr=MapsFragment.newInstance();
            fragmentTransaction.replace(R.id.flaContenido,fr);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_service) {
            fr=ServiceFragment.newInstance();
            fragmentTransaction.replace(R.id.flaContenido,fr);
            fragmentTransaction.commit();
        }else if(id==R.id.nav_notification){
            fr=NotificationFragment.newInstance();
            fragmentTransaction.replace(R.id.flaContenido,fr);
            fragmentTransaction.commit();
        }else if(id==R.id.nav_alarm){
            fr=AlarmFragment.newInstance();
            fragmentTransaction.replace(R.id.flaContenido,fr);
            fragmentTransaction.commit();
        }else if(id==R.id.nav_shared_users){
            fr=SharedUsersFragment.newInstance();
            fragmentTransaction.replace(R.id.flaContenido,fr);
            fragmentTransaction.commit();
        }else if(id==R.id.nav_services){
            fr=ServicesFragment.newInstance();
            fragmentTransaction.replace(R.id.flaContenido,fr);
            fragmentTransaction.commit();
        }else if(id==R.id.nav_request){
            fr=RequestFragment.newInstance();
            fragmentTransaction.replace(R.id.flaContenido,fr);
            fragmentTransaction.commit();
        }else if(id==R.id.nav_logout) {
            session.logoutUser();
        }else if(id==R.id.nav_home){
            Intent i=new Intent(HomeActivity.this,Home2Activity.class);
            startActivity(i);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loginRequest(final String email,final String psw){
        RequestQueue queue = Volley.newRequestQueue(this);
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
                                    result=o.toJSONString();
                                    session.createLoginSession(email,result);
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
}
