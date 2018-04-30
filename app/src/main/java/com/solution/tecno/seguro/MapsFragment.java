package com.solution.tecno.seguro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.solution.tecno.seguro.Firebase.Constants;
import com.solution.tecno.seguro.Firebase.MyNotificationManager;
import com.solution.tecno.seguro.Utils.SessionManager;
import com.solution.tecno.seguro.Utils.User;

import org.json.simple.parser.JSONParser;

import java.util.HashMap;


public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private String result="";
    private LocationManager locationManager;
    private GoogleMap mMap;
    SessionManager session;
    HashMap<String, String> user;
    User u;
    LatLng l;

    SwitchCompat sw;
    Button btn_add_home;

    double latitud=0.0;
    double longitud=0.0;

    int level;

    public MapsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MapsFragment newInstance() {
        MapsFragment fragment = new MapsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        getActivity().registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        getActivity().unregisterReceiver(mBatInfoReceiver);
        session = new SessionManager(getActivity().getApplicationContext());
        user = session.getUserDetails();
        Gson g = new Gson();
        u = g.fromJson(user.get(SessionManager.KEY_VALUES), User.class);

        sw = v.findViewById(R.id.smart_control);
        btn_add_home = v.findViewById(R.id.btn_add_home);

        sw.setChecked(u.getSmart_control() == 1);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int status = b ? 1 : 0;
                updateSmartControl(u.getId(), status);
            }
        });

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        showGPSDisabledAlertToUser();

        btn_add_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng latLng = new LatLng(latitud, longitud);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
                mMap.animateCamera(cameraUpdate);
                showLocationConfirmation(u.getId(), latitud, longitud);
            }
        });


        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //OBTENEMOS LA UBICACION DEL USUARIO Y MOVEMOS LA CAMARA HACIA SU POSICION

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //HOME LOCATION DEFINED BY USER
        l=new LatLng(Double.parseDouble(u.getLat()),Double.parseDouble(u.getLng()));
        int radiusM =50;
        int height = 80;
        int width = 50;

        // draw circle
        int d = 500; // diameter
        Bitmap bm = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.colorAccent));
        c.drawCircle(d/2, d/2, d/2, p);

        // generate BitmapDescriptor from circle Bitmap
        BitmapDescriptor bmD = BitmapDescriptorFactory.fromBitmap(bm);
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.house_icon);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        mMap.addMarker(new MarkerOptions()
                .position(l)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                .draggable(true)
        );

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                showLocationConfirmation(u.getId(),marker.getPosition().latitude,marker.getPosition().longitude);
            }
        });
        mMap.addGroundOverlay(new GroundOverlayOptions().
                image(bmD).
                position(l,radiusM*2,radiusM*2).
                transparency(0.7f));

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1000, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Toast.makeText(getContext(), "Ubicando...", Toast.LENGTH_LONG).show();
                latitud=location.getLatitude();
                longitud=location.getLongitude();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
                mMap.animateCamera(cameraUpdate);
                //mMap.addMarker(new MarkerOptions().position(latLng));
                Location loc1=new Location("");
                    loc1.setLatitude(l.latitude);
                    loc1.setLongitude(l.longitude);
                Location loc2=new Location("");
                    loc2.setLongitude(latLng.longitude);
                    loc2.setLatitude(latLng.latitude);

                float distance=loc1.distanceTo(loc2);
//                verifiedDistance(distance);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getContext(), "Active su GPS porfavor", Toast.LENGTH_SHORT).show();
            }
        });

        mMap.setMyLocationEnabled(true);
        UiSettings u=mMap.getUiSettings();
        u.setZoomControlsEnabled(true);
        u.setMyLocationButtonEnabled(true);
    }

//    public void verifiedDistance(float distance){
//        if(distance>55){
//            //Snackbar.make(getView(),"Alarma apagada",Snackbar.LENGTH_LONG).show();
//            if(u.getSmart_control()==1){
//                turnAlarm("callao",0);
//                //addNotification("Servicio activado");
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    NotificationManager mNotificationManager =
//                            (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//                    int importance = NotificationManager.IMPORTANCE_HIGH;
//                    NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance);
//                    mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
//                    mChannel.enableLights(true);
//                    mChannel.setLightColor(Color.RED);
//                    mChannel.enableVibration(true);
//                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//                    mNotificationManager.createNotificationChannel(mChannel);
//                }
//                MyNotificationManager.getInstance(getActivity()).displayNotification("Servicio activado");
//            }
//        }else{
//            //Snackbar.make(getView(),"Alarma encendida",Snackbar.LENGTH_LONG).show();
//            if(u.getSmart_control()==1){
//                turnAlarm("callao",1);
//                //addNotification("Servicio desactivado");
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    NotificationManager mNotificationManager =
//                            (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//                    int importance = NotificationManager.IMPORTANCE_HIGH;
//                    NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance);
//                    mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
//                    mChannel.enableLights(true);
//                    mChannel.setLightColor(Color.RED);
//                    mChannel.enableVibration(true);
//                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//                    mNotificationManager.createNotificationChannel(mChannel);
//                }
//                MyNotificationManager.getInstance(getActivity()).displayNotification("Servicio desactivado");
//            }
//        }
//    }

    public void turnAlarm(final String alarm,final int status){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String params="?codProd="+alarm+"&estado="+status;
        String url = "https://espacioseguro.pe/php_connection/cambiarEstado.php"+params;

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        if(response!="[]"){
                            JSONParser p=new JSONParser();
                            String estado=status==0?"desactivada":"activado";
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

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        @Override

        public void onReceive(Context c, Intent intent) {

            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

            if(level<=15 && (level%5==0)){
                sw.setChecked(false);
                sw.setEnabled(false);
                sw.setSwitchMinWidth(110);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationManager mNotificationManager =
                            (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance);
                    mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
                    mChannel.enableLights(true);
                    mChannel.setLightColor(Color.RED);
                    mChannel.enableVibration(true);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    mNotificationManager.createNotificationChannel(mChannel);
                }
                MyNotificationManager.getInstance(getActivity()).displayNotification("Batería baja!\nServicio desactivado");
            }else{
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1000, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Toast.makeText(getActivity(), String.valueOf(status), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        sw.setEnabled(true);
                        sw.setSwitchMinWidth(110);
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        sw.setEnabled(false);
                        sw.setChecked(false);
                        updateSmartControl(u.getId(),0);
                        sw.setSwitchMinWidth(110);
                    }
                });
            }
        }

    };

    public void updateSmartControl(final String user_id,final int status){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String params="?idUser="+user_id+"&estado="+status;
        String url = "https://espacioseguro.pe/php_connection/updateSmartControl.php"+params;

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        String estado = "";
                        // response
                        if(response!="[]"){
                            loginRequest(u.getcorreo(),u.getClave());
                            JSONParser p=new JSONParser();
                            u.setSmart_control(status);
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

    private void showGPSDisabledAlertToUser(){
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setMessage("GPS desactivado.\n¿Desea activarlo?")
                    .setCancelable(false)
                    .setPositiveButton("Ir a configuración",
                            new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int id){
                                    Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                }
                            });
            alertDialogBuilder.setNegativeButton("Ahora no",
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }

    }

    private void showLocationConfirmation(final String user_id,final double lat,final double lng){
        u.setLat(String.valueOf(lat));
        u.setLng(String.valueOf(lng));
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("¿Está seguro de guardar esta ubicación?")
                .setCancelable(false)
                .setPositiveButton("Guardar",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                updateHome(u.getCod_servicio(),lat,lng);
                                mMap.clear();
                                int radiusM =50;
                                int height = 80;
                                int width = 50;
                                int d = 500; // diameter
                                Bitmap bm = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888);
                                Canvas c = new Canvas(bm);
                                Paint p = new Paint();
                                p.setColor(getResources().getColor(R.color.colorAccent));
                                c.drawCircle(d/2, d/2, d/2, p);
                                // generate BitmapDescriptor from circle Bitmap
                                BitmapDescriptor bmD = BitmapDescriptorFactory.fromBitmap(bm);
                                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.house_icon);
                                Bitmap b=bitmapdraw.getBitmap();
                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                                LatLng location=new LatLng(Double.parseDouble(u.getLat()),Double.parseDouble(u.getLng()));
                                mMap.addMarker(new MarkerOptions()
                                        .position(location)
                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                        .draggable(true)
                                );
                                mMap.addGroundOverlay(new GroundOverlayOptions().
                                        image(bmD).
                                        position(location,radiusM*2,radiusM*2).
                                        transparency(0.7f));

                            }
                        });
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void updateHome(final String service,final double lat,final double lng){
        final MaterialDialog md=new MaterialDialog.Builder(getActivity())
                .content("Guardando..")
                .progress(true,0)
                .cancelable(false)
                .backgroundColor(Color.WHITE)
                .contentColor(Color.BLACK)
                .show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String params="?service="+service+"&lat="+lat+"&lng="+lng;
        String url = "https://espacioseguro.pe/php_connection/updateHome.php"+params;

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        if(response!="[]"){
                            loginRequest(u.getcorreo(),u.getClave());
                            mMap.clear();
                            int radiusM =50;
                            int height = 80;
                            int width = 50;
                            int d = 500; // diameter
                            Bitmap bm = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888);
                            Canvas c = new Canvas(bm);
                            Paint p = new Paint();
                            p.setColor(getResources().getColor(R.color.colorAccent));
                            c.drawCircle(d/2, d/2, d/2, p);
                            // generate BitmapDescriptor from circle Bitmap
                            BitmapDescriptor bmD = BitmapDescriptorFactory.fromBitmap(bm);
                            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.house_icon);
                            Bitmap b=bitmapdraw.getBitmap();
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                            LatLng location=new LatLng(Double.parseDouble(u.getLat()),Double.parseDouble(u.getLng()));
                            mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                    .draggable(true)
                            );
                            mMap.addGroundOverlay(new GroundOverlayOptions().
                                    image(bmD).
                                    position(location,radiusM*2,radiusM*2).
                                    transparency(0.7f));
                            md.hide();
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

    public void loginRequest(final String email,final String psw){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
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
                            System.out.println(response);
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
