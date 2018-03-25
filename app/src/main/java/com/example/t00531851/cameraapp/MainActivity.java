package com.example.t00531851.cameraapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private ZXingScannerView zXingScannerView;
    private ZXingScannerView.ResultHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.VIBRATE
            }, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You cant use this application without permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void takePic(View view){
        Intent takePic = new Intent(MainActivity.this, CameraCapture.class);
        startActivity(takePic);
    }

    public void scanQR(View view){
        zXingScannerView = new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        handler = this;
        if(result != null){
            //show a dialog
            AlertDialog.Builder alertShow = new AlertDialog.Builder(MainActivity.this,
                    R.style.myDialog);
            alertShow.setTitle("Scanned Result");
            alertShow.setMessage(result.getText());
            alertShow.setCancelable(false);
            alertShow.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    zXingScannerView.resumeCameraPreview(handler);
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = alertShow.create();
            dialog.show();
        }else{
            zXingScannerView.resumeCameraPreview(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(zXingScannerView != null){
            zXingScannerView.startCamera();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        finish();
        overridePendingTransition(0,0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        super.onBackPressed();
    }
}
