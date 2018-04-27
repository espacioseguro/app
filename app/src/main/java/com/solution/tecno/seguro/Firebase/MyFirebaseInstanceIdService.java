package com.solution.tecno.seguro.Firebase;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;
import com.solution.tecno.seguro.Utils.SessionManager;
import com.solution.tecno.seguro.Utils.User;

import java.util.HashMap;

/**
 * Created by Julian on 25/02/2018.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    SessionManager session;
    HashMap<String, String> user;
    User u;
    private String result="";
    //this method will be called
    //when the token is generated
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        //now we will have the token
        final String token = FirebaseInstanceId.getInstance().getToken();

        //for now we are displaying the token in the log
        //copy it as this method is called only when the new token is generated
        //and usually new token is only generated when the app is reinstalled or the data is cleared
        RequestQueue queue = Volley.newRequestQueue(this);
        session=new SessionManager(getApplicationContext());
        HashMap<String,String> user=session.getUserDetails();
        Gson g=new Gson();
        u=g.fromJson(user.get(SessionManager.KEY_VALUES),User.class);
        if(u!=null){
            String params="?idUser="+u.getId()+"&fcm="+token;
            String url = "https://espacioseguro.pe/php_connection/updateFCM.php"+params;

            StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            // response
                            if(response!="[]"){
                                u.setFcm(token);
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
}
