package com.espacio.seguro;

import android.content.Intent;
import android.graphics.Typeface;
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
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.espacio.seguro.Utils.SessionManager;
import com.espacio.seguro.Utils.User;
import com.espacio.seguro.R;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    SessionManager session;
    User u;
    TextView service_name,name_profile;
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

        NavigationView navigationView = findViewById(R.id.nav_view);

        initializeUserData();
        updateFcm();
        initializeFragment();

        navigationView.setNavigationItemSelectedListener(this);
    }
    public void updateService(String service) {
        service_name.setText(service); //str OR whatvever you need to set.
    }

    private void initializeUserData(){
        session=new SessionManager(getApplicationContext());
        if(session.isLoggedIn()){
            HashMap<String,String> user=session.getUserDetails();
            Gson g=new Gson();
            u=g.fromJson(user.get(SessionManager.KEY_VALUES),User.class);
            setUserValues(user.get(SessionManager.KEY_VALUES));

            name_profile=findViewById(R.id.name_profile);
            service_name=findViewById(R.id.service_profile);
            name_profile.setText(u.getnombre());
            service_name.setText(u.getName_servicio());
            name_profile.setTypeface(null, Typeface.BOLD);
            service_name.setTypeface(null, Typeface.BOLD);
        }else{
            session.checkLogin();
        }
    }

    private void initializeFragment(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Fragment fr;
        fr= AlarmFragment.newInstance();
        fragmentTransaction.replace(R.id.flaContenido,fr);
        fragmentTransaction.commit();
    }

    public void updateFcm(){
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
    }

    public void setUserValues(String datos){
        Gson gson=new Gson();
        gson.fromJson(datos,User.class);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                this.finishAffinity();
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
