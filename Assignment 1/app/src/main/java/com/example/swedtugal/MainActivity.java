package com.example.swedtugal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    public void calibrate(View view){
        Intent intent = new Intent(this,  CalibratingActivity.class);
        startActivity(intent);
    }

    public void locateMe(View view){
        Intent intent = new Intent(this, PositioningActivity.class);
        startActivity(intent);
    }

    public void credits(View view){
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }
}
