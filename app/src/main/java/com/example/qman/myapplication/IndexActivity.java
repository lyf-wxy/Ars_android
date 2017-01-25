package com.example.qman.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class IndexActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent= getIntent();
        String usernameLogin = intent.getStringExtra("usernameLogin");
        TextView tv = new TextView(this);
        tv.setText(usernameLogin);
        setContentView(tv);
        //setContentView(R.layout.activity_index);
    }
}
