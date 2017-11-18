package com.example.tayhuaswee.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goPrototype(View view) {
        startActivity(new Intent(MainActivity.this, Prototype.class));
    }

    public void goDebug(View view) {
        startActivity(new Intent(MainActivity.this, BasicBeacon.class));
    }
}
