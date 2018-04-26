package com.solution.tecno.seguro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class Home2Activity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            Fragment fr;
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    menuItem.setTitle("Aqui");
                    return false;
                }
            });
            switch (item.getItemId()) {
                case R.id.home2_nav_alarm:
                    mTextMessage.setText(R.string.title_home);
                    fr=AlarmFragment.newInstance();
                    fragmentTransaction.replace(R.id.flaContenido,fr);
                    fragmentTransaction.commit();
                    return true;
                case R.id.home2_nav_maps:
                    mTextMessage.setText(R.string.title_dashboard);
                    fr=MapsFragment.newInstance();
                    fragmentTransaction.replace(R.id.flaContenido,fr);
                    fragmentTransaction.commit();
                    return true;
                case R.id.home2_nav_notification:
                    mTextMessage.setText(R.string.title_notifications);
                    fr=NotificationFragment.newInstance();
                    fragmentTransaction.replace(R.id.flaContenido,fr);
                    fragmentTransaction.commit();
                    return true;
                case R.id.home2_home:
                    Intent intent=new Intent(Home2Activity.this,HomeActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Fragment fr;
        fr= MapsFragment.newInstance();
        fragmentTransaction.replace(R.id.flaContenido,fr);
        fragmentTransaction.commit();
        navigation.setSelectedItemId(R.id.home2_nav_maps);
    }


}
