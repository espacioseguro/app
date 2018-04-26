package com.solution.tecno.seguro.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.solution.tecno.seguro.HomeActivity;
import com.solution.tecno.seguro.LoginActivity;
import com.solution.tecno.seguro.MainActivity;

import java.util.HashMap;

/**
 * Created by Julian on 4/11/2017.
 */

public class SessionManager {

    SharedPreferences pref;

    SharedPreferences.Editor editor;

    Context _context;

    int PRIVATE_MODE=0;

    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_VALUES="user";
    public static final String KEY_FCM="fcm";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(KEY_EMAIL, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createToken(String token){
        // Storing email in pref
        editor.putString(KEY_FCM, token);

        // commit changes
        editor.commit();
    }

    public void createLoginSession(String email,String user_data){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_VALUES,user_data);


        // commit changes
        editor.commit();
    }

    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }else{
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, HomeActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        //user values
        user.put(KEY_VALUES,pref.getString(KEY_VALUES,null));

        user.put(KEY_FCM,pref.getString(KEY_FCM,null));

        // return user
        return user;
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
