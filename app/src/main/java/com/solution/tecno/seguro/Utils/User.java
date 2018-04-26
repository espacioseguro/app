package com.solution.tecno.seguro.Utils;

import java.util.List;

/**
 * Created by Julian on 4/11/2017.
 */

public class User {

    public String id;
    public String correo;
    public String parent_id;
    public String nombre;
    public String lat;
    public String lng;
    public String clave;
    public int smart_control;
    public Configuration configuration;
    public List<User> parents;
    public String fcm;
    public String cod_servicio;


    public User() {
    }

    public User(String cod_servicio,String id, String correo, String parent_id, String nombre, String lat, String lng, String clave, int smart_control, Configuration configuration,String fcm) {
        this.id = id;
        this.correo = correo;
        this.parent_id = parent_id;
        this.nombre = nombre;
        this.lat = lat;
        this.lng = lng;
        this.clave = clave;
        this.smart_control = smart_control;
        this.configuration = configuration;
        this.fcm = fcm;
        this.cod_servicio = cod_servicio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getnombre() {
        return nombre;
    }

    public void setnombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public List<User> getParents() {
        return parents;
    }

    public void setParents(List<User> parents) {
        this.parents = parents;
    }

    public String getcorreo() {
        return correo;
    }

    public void setcorreo(String correo) {
        this.correo = correo;
    }

    public int getSmart_control() {
        return smart_control;
    }

    public void setSmart_control(int smart_control) {
        this.smart_control = smart_control;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getFcm() {
        return fcm;
    }

    public void setFcm(String fcm) {
        this.fcm = fcm;
    }

    public String getCod_servicio() {
        return cod_servicio;
    }

    public void setCod_servicio(String cod_servicio) {
        this.cod_servicio = cod_servicio;
    }
}
