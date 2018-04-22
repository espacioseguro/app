package com.solution.tecno.espacioseguro.Utils;

/**
 * Created by Julian on 4/11/2017.
 */

public class Configuration {

    public String id;
    public String user_id;
    public String smart_control;
    public String min_battery;

    public Configuration() {
    }

    public Configuration(String id, String user_id, String smart_control, String min_battery) {
        this.id = id;
        this.user_id = user_id;
        this.smart_control = smart_control;
        this.min_battery = min_battery;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSmart_control() {
        return smart_control;
    }

    public void setSmart_control(String smart_control) {
        this.smart_control = smart_control;
    }

    public String getMin_battery() {
        return min_battery;
    }

    public void setMin_battery(String min_battery) {
        this.min_battery = min_battery;
    }
}
