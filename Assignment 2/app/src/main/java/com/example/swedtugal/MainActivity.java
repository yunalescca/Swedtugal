package com.example.swedtugal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int MULTIPLE_PERMISSIONS = 10;

    private String[] permissions = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        checkPermissions();
    }

    private boolean checkPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p: permissions) {
            int result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(
                    new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissionsList, int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0) {
                StringBuilder permissionsDenied = new StringBuilder();
                for (String per: permissionsList) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        permissionsDenied.append("\n").append(per);
                    }
                }
            }
        }
    }

    public void calibrate(View view) {
        Intent intent = new Intent(this,  CalibratingActivity.class);
        startActivity(intent);
    }

    public void locateMe(View view) {
        Intent intent = new Intent(this, PositioningActivity.class);
        startActivity(intent);
    }

    public void credits(View view) {
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }
}
