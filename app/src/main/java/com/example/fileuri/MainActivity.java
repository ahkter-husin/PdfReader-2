package com.example.fileuri;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fileuri.Utils.RealPathUtil;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    Button fab;
    private Intent intent;
    private static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.btn);
        tv = findViewById(R.id.tv1);
        tv.setMovementMethod(new ScrollingMovementMethod());

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        PermissionListener dialogPermissionListener =
                                DialogOnDeniedPermissionListener.Builder
                                        .withContext(getApplicationContext())
                                        .withTitle("Storage permission")
                                        .withMessage("Storage permission is needed to read pdf..")
                                        .withButtonText(android.R.string.ok)
                                        .withIcon(R.mipmap.ic_launcher)
                                        .build();


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                    }
                }).check();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
//                Toast.makeText(this, uri.getPath(), Toast.LENGTH_SHORT).show();
                Log.v("URI", uri.getPath());
                String text = read(uri);
                tv.setText(text);
            }
        }
    }

    public String read(Uri fname) {
        BufferedReader br = null;
        String response = null;
        try {
            StringBuffer output = new StringBuffer();
            // Get the dir of SD Card
            File sdCardDir = Environment.getExternalStorageDirectory();

            // Get The Text file

            RealPathUtil realPathUtil = new RealPathUtil();

            String fpath1 = realPathUtil.getRealPath(getApplicationContext(), fname);


            PdfReader reader = null;
            try {


                reader = new PdfReader(new FileInputStream(fpath1));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());

            }

            PdfReaderContentParser parser = new PdfReaderContentParser(reader);

            StringWriter strW = new StringWriter();

            TextExtractionStrategy strategy;
            //comment the below code in case you want to read whole pdf and follow step 2
            strategy = parser.processContent(1,
                    new SimpleTextExtractionStrategy());

            strW.write(strategy.getResultantText());

            // stepd2 uncomment below line of from 115-122 if you wants to read whole pdf

//            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
//                strategy = parser.processContent(i,
//                        new SimpleTextExtractionStrategy());
//
//                strW.write(strategy.getResultantText());
//
//            }

            response = strW.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }


}
