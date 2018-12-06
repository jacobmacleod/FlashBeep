package com.example.jacob.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 50;
    private boolean isLightOn; // status of light
    private CameraManager cameraManager;
    private String cameraId;
    private boolean emergency;
    public Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isLightOn = false;
        // check if user has flash light on their device
        boolean isFlashAvailable = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!isFlashAvailable) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("This device does not have a flash light.");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    System.exit(0);
                }
            });
            alertDialog.show();
            return;
        }
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        }
        catch (CameraAccessException e) {
            e.printStackTrace();
        }
        // start and stop flashing and beeping by button press
        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!emergency) {
                    emergency = true;
                    flash();
                }
                else {
                    emergency = false;
                    turnOffLight();
                }
            }
        });
    }
    public void turnOffLight() {
        try {
            cameraManager.setTorchMode(cameraId, false);
            isLightOn = false;
            Thread.sleep(250);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void turnOnLight() {
        try {
            cameraManager.setTorchMode(cameraId, true);
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            isLightOn = true;
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP,250);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // while in emergency mode, flash light at 2 Hz.
    public void flash() {
        while(emergency) {
            try {
                if (isLightOn) {
                    turnOffLight();
                } else {
                    turnOnLight();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

