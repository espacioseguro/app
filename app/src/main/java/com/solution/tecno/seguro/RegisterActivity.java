package com.solution.tecno.seguro;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.solution.tecno.seguro.Utils.SessionManager;
import com.solution.tecno.seguro.Utils.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends AppCompatActivity{

    private String mName,mPassword,mPhone,mUsername;
    private EditText password,name,phone,username;
    private Button registerButton;
    private ImageView show_password;
    private CheckBox terms,information;
    private int terms_val,information_val=0;
    private Context context=RegisterActivity.this;
    SessionManager session;

    private String result="";

    private int visiblePassword=0;

    private MaterialDialog md;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private LoginButton loginButton;
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.v("LoginActivity", response.toString());

                            // Application code
                            try {
                                Log.d("tttttt",object.getString("id"));
                                String birthday="";
                                if(object.has("birthday")){
                                    birthday = object.getString("birthday"); // 01/31/1980 format
                                }

                                String fnm = object.getString("first_name");
                                String lnm = object.getString("last_name");
                                String mail = object.getString("email");
                                String gender = object.getString("gender");
                                String fid = object.getString("id");
                                //tvdetails.setText(fnm+" "+lnm+" \n"+mail+" \n"+gender+" \n"+fid+" \n"+birthday);
                                name.setText(fnm+" "+lnm);
//                                email.setText(mail);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
            request.setParameters(parameters);
            request.executeAsync();

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_register);

        username= findViewById(R.id.register_username);
        name= findViewById(R.id.register_name);
        password=findViewById(R.id.register_password);
        phone=findViewById(R.id.register_phone);
        registerButton=findViewById(R.id.register_button);
        terms=findViewById(R.id.terms);
        information=findViewById(R.id.information);

        terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    terms_val=1;
                    Toast.makeText(context,"terms_checked "+terms_val,Toast.LENGTH_SHORT).show();
                }else{
                    terms_val=0;
                    Toast.makeText(context,"terms_unchecked "+terms_val,Toast.LENGTH_SHORT).show();
                }
            }
        });

        information.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    information_val=1;
                }else{
                    information_val=0;
                }
            }
        });
        show_password=findViewById(R.id.register_show_password);
        show_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(visiblePassword==0){
                    password.setTransformationMethod(null);
                    show_password.setImageResource(R.drawable.invisible_password);
                    visiblePassword=1;
                }else{
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    show_password.setImageResource(R.drawable.visible_password);
                    visiblePassword=0;
                }
            }
        });

        session=new SessionManager(getApplicationContext());

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(terms_val==0){
                    md=new MaterialDialog.Builder(context)
                            .limitIconToDefaultSize()
                            .title("Términos y condiciones")
                            .content("Debe aceptar los términos y condiciones para continuar")
                            .backgroundColor(Color.WHITE)
                            .titleColor(Color.RED)
                            .contentColor(Color.BLACK)
                            .positiveText("Aceptar")
                            .onAny(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    md.dismiss();
                                }
                            })
                            .show();
                }else{
                    mName=name.getText().toString();
                    mPhone=phone.getText().toString();
                    mUsername=username.getText().toString();
                    mPassword=password.getText().toString();
                    int delay = 5000;// in ms

                    Timer timer = new Timer();
                    md=new MaterialDialog.Builder(RegisterActivity.this)
                            .content("Guardando")
                            .progress(true,0)
                            .cancelable(false)
                            .backgroundColor(Color.WHITE)
                            .contentColor(Color.BLACK)
                            .show();

                    timer.schedule( new TimerTask(){
                        public void run() {
                            registerRequest(mUsername,mPassword,mName,mPhone);
                        }
                    }, delay);
                }
            }
        });

        loginButton = findViewById(R.id.btnfb);

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {

            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.registerCallback(callbackManager, callback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();

    }

    public String getDatos(String email){
        return result;
    }

    public void setUserValues(String datos){
        Gson gson=new Gson();
        gson.fromJson(datos,User.class);
    }

    public void registerRequest(final String username,final String psw,final String name,final String phone){
        RequestQueue queue = Volley.newRequestQueue(this);
        String params="?username="+username+"&psw="+psw+"&name="+name+"&phone="+phone+"&information="+information_val;
        String url = "https://www.espacioseguro.pe/php_connection/register.php"+params;
        System.out.println(url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        JSONParser p=new JSONParser();
                        try {
                            System.out.println(response);
                            if(response!="0"){
                                md.dismiss();
                                loginRequest(mUsername,mPassword);
                            }else{
                                md.dismiss();
                            }

//                            org.json.simple.JSONArray a=(org.json.simple.JSONArray)p.parse(response);
//                            org.json.simple.JSONObject o=(org.json.simple.JSONObject)a.get(0);
//                            result=o.toJSONString();
//                            session.createLoginSession(username,result);
//                            HashMap<String,String> user=session.getUserDetails();
//                            setUserValues(user.get(SessionManager.KEY_VALUES));
                        } catch (Exception e) {
                            System.out.println(e);
                            md.dismiss();
                            md.setContent("Error al registrar usuario");
                            md.setCancelable(true);
                            md.show();
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

    public void loginRequest(final String email,final String psw){
        final MaterialDialog md=new MaterialDialog.Builder(this)
                .content("Validando")
                .progress(true,0)
                .cancelable(false)
                .backgroundColor(Color.WHITE)
                .contentColor(Color.BLACK)
                .show();
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
                                if(md.isShowing()){
                                    md.cancel();
                                }
                                Intent i=new Intent(RegisterActivity.this,HomeActivity.class);
                                startActivity(i);
                                finish();
                            }else{
                                md.hide();
                            }
                        } catch (Exception e) {

                            md.dismiss();
                            md.setContent(response);
                            md.setCancelable(true);
                            md.show();
                            System.out.println(e);
                            System.out.println(response);
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
    public void onBackPressed() {
        Intent i=new Intent(this,LoginActivity.class);
        startActivity(i);
        this.finish();
    }
}
