package com.espacio.seguro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.espacio.seguro.Utils.SessionManager;
import com.espacio.seguro.Utils.User;
import com.espacio.seguro.R;

import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{

    SessionManager session;
    private String result="";
    public static int MY_PERMISSIONS_REQUEST_ACCESS= 1;
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
        if (
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED
            )
        {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.INTERNET,
                                Manifest.permission.CAMERA
                        },
                        MY_PERMISSIONS_REQUEST_ACCESS);
        }else{
            validateSession();
        }
    }

    public void validateSession(){
        View v=View.inflate(getApplicationContext(),R.layout.activity_main,null);
        Timer t=new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                session = new SessionManager(getApplicationContext());
                session.checkLogin();
                if(session.isLoggedIn()){
                    HashMap<String,String> user=session.getUserDetails();
                    User u;
                    Gson g=new Gson();
                    u=g.fromJson(user.get(SessionManager.KEY_VALUES),User.class);
                    loginRequest(u.getcorreo(),u.getClave());
                    Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                }else{
                    Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
            }
        },2000);

    }

    public void setUserValues(String datos){
        Gson gson=new Gson();
        gson.fromJson(datos,User.class);
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
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        queue.add(postRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case 1 : {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    validateSession();
                } else {
                    checkPermissions();
                    Toast.makeText(getApplicationContext(),
                            "Permisos necesarios.\nDebe aceptar para continuar",
                            Toast.LENGTH_SHORT
                    ).show();
                }
                return;
            }
        }
    }
}
