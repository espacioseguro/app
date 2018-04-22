package com.solution.tecno.espacioseguro.Utils;

/**
 * Created by Julian on 4/11/2017.
 */

public class Auth {

    String username;
    String passwor;

    public Auth() {
    }

    public Auth(String username, String passwor) {
        this.username = username;
        this.passwor = passwor;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswor() {
        return passwor;
    }

    public void setPasswor(String passwor) {
        this.passwor = passwor;
    }
}
