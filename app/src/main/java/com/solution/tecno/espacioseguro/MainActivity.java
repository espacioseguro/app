package com.solution.tecno.espacioseguro;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.solution.tecno.espacioseguro.Utils.SessionManager;
import com.solution.tecno.espacioseguro.Utils.User;

import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{

    SessionManager session;
    private String result="";
    public static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    ImageView logo;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logo=findViewById(R.id.main_logo);
        name=findViewById(R.id.main_name);
        checkPermissions();

    }

    private void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.BLUETOOTH_PRIVILEGED,
                                Manifest.permission.INTERNET,
                                Manifest.permission.CAMERA
                        },
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            validateSession();
        }
    }

    private void checkRunTimePermission() {
        String[] permissionArrays = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET,
                Manifest.permission.BATTERY_STATS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission_group.LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE
        };


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, 11111);
        } else {
            // if already permition granted
            // PUT YOUR ACTION (Like Open cemara etc..)
            Toast.makeText(this, "Permisos dados", Toast.LENGTH_SHORT).show();
            validateSession();
        }
    }

    public void validateSession(){
        int delay = 5000;// in ms

        Timer timer = new Timer();
//        final MaterialDialog md=new MaterialDialog.Builder(MainActivity.this)
//                .content("Validando")
//                .progress(true,0)
//                .cancelable(false)
//                .backgroundColor(Color.WHITE)
//                .contentColor(Color.BLACK)
//                .show();
        View v=View.inflate(getApplicationContext(),R.layout.activity_main,null);
//        handleAnimation(v);
        timer.schedule( new TimerTask(){
            public void run() {
                session = new SessionManager(getApplicationContext());
                session.checkLogin();
                if(session.isLoggedIn()){
                    HashMap<String,String> user=session.getUserDetails();
                    user=session.getUserDetails();
                    User u;
                    Gson g=new Gson();
                    u=g.fromJson(user.get(SessionManager.KEY_VALUES),User.class);
                    loginRequest(u.getcorreo(),u.getClave());
//                    if(md.isShowing()){
//                        md.cancel();
//                    }
                    Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                }else{
//                    if(md.isShowing()){
//                        md.cancel();
//                    }
                    Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
            }
        }, delay);
    }

    public void setUserValues(String datos){
        Gson gson=new Gson();
        gson.fromJson(datos,User.class);
    }

    public void handleAnimation(View view){
        ObjectAnimator logo_animation=ObjectAnimator.ofFloat(logo,"rotation",0f,1080f);
        ObjectAnimator name_animation=ObjectAnimator.ofFloat(logo,"rotation",0f,1080f);
        logo_animation.setDuration(6000);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(logo_animation,name_animation);
        animatorSet.start();
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

                        } catch (Exception e) {
                            System.out.println(e);
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
        queue.add(postRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1 : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    validateSession();
                    //Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    checkPermissions();
                    Toast.makeText(getApplicationContext(),
                            "Permisos necesarios.\nDebe aceptar para continuar",
                            Toast.LENGTH_SHORT
                    ).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
