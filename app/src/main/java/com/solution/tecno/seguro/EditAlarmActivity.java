package com.solution.tecno.seguro;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditAlarmActivity extends AppCompatActivity {

    EditText code,name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
        View parentLayout = findViewById(android.R.id.content);

        Intent intent=getIntent();
        String id=intent.getStringExtra("id_alarm");
        Snackbar.make(parentLayout,id,Snackbar.LENGTH_LONG).show();
        code=findViewById(R.id.edit_code_alarm);
        name=findViewById(R.id.edit_name_alarm);

        code.setText(id);

    }
}
