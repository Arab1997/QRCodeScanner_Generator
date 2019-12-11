package com.example.multi_qr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;

public class QRCodeGenerator extends AppCompatActivity {
    ImageView imgQRCODE;
    TextView txtData;
    FloatingActionButton fab;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //Toast.makeText(QRCodeGenerator.this, "Share Feature", Toast.LENGTH_SHORT).show();

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Check for the WRITE_EXTERNAL_STORAGE permission before accessing the camera.  If the
                                // permission is not granted yet, request permission.
                                int rc = android.support.v4.app.ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                if (rc == PackageManager.PERMISSION_GRANTED) {
                                    if (myBitmap != null) {
                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType("image/jpeg");
                                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                                        String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                                                myBitmap, "temp", null);
                                        Uri imageUri = Uri.parse(path);
                                        share.putExtra(Intent.EXTRA_STREAM, imageUri);
                                        startActivity(Intent.createChooser(share, "Select"));
                                        Snackbar.make(view, "Share QR Code", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null)
                                                .show();
                                    } else {
                                        Toast.makeText(context, "Please generate qr code first", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    requestCameraPermission();
                                }
                            }
                        }).show();

            }
        });
        imgQRCODE = findViewById(R.id.imageView);
        txtData = findViewById(R.id.txtData);
    }

    String data = "";

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w("SA", "storage permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            android.support.v4.app.ActivityCompat.requestPermissions(this, permissions, 5);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.ActivityCompat.requestPermissions(thisActivity, permissions,
                        5);
            }
        };


        Snackbar.make(fab, "Provide Permission",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", listener)
                .show();
    }

    public void generateQrcode(View v) {
        data = txtData.getText().toString();
        if (data.length() > 0) {

            createQRImage(data);
        } else {
            Toast.makeText(getBaseContext(),
                    "Please enter data",
                    Toast.LENGTH_SHORT).show();
        }
    }

    Bitmap myBitmap;

    private void createQRImage(String data) {
        try {

            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(data,
                        BarcodeFormat.QR_CODE, 200, 200);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                myBitmap = barcodeEncoder.createBitmap(bitMatrix);
                imgQRCODE.setImageBitmap(myBitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
