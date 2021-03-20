package com.example.gallery_noob;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

public class FullScreenImage extends AppCompatActivity {
    ImageView imageView;
    private static final int REQUEST_PERMISSIONS = 100;
    static boolean req = false;
    Button button;
    boolean gone = false;
    ImageButton back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        RelativeLayout ln2=(RelativeLayout) findViewById(R.id.full_scr_layout);
        LinearLayout ln= (LinearLayout)findViewById(R.id.full_scr);
        LinearLayout ln1= (LinearLayout)findViewById(R.id.text_func);
        LinearLayout ln3= (LinearLayout)findViewById(R.id.header_detail);
        ln.setVisibility(View.GONE);
        ln1.setVisibility(View.GONE);
        ln3.setVisibility(View.GONE);
        ln2.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    if(!gone){
                        ln.setVisibility(View.GONE);
                        ln1.setVisibility(View.GONE);
                        ln3.setVisibility(View.GONE);
                        gone=true;
                    }else{
                        ln.setVisibility(View.VISIBLE);
                        ln1.setVisibility(View.VISIBLE);
                        ln3.setVisibility(View.VISIBLE);
                        gone=false;
                    }
                }
                return false;
            }
        });

        back_btn=(ImageButton) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(FullScreenImage.this,MainActivity.class);
                startActivity(intent);
            }
        });

        imageView=(ImageView) findViewById(R.id.image_view);
        button = (Button)findViewById(R.id.button);
        getSupportActionBar().hide();
        getSupportActionBar().setTitle("Tuấn ngu");
        Intent i=getIntent();
        String position=null;
        if(!req){
            position=null;
        }else{
            position=i.getExtras().getString("path");
        }
        //ImageAdapter imageAdapter= new ImageAdapter(this);
        if(position!=null)
        {
            Picasso.get().load(new File(position)).into(imageView);
            /*Bitmap bitmap = BitmapFactory.decodeFile(position);
            try {
                File imgFile=new File(position);
                ExifInterface exif = new ExifInterface(imgFile.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                }
                else if (orientation == 3) {
                    matrix.postRotate(180);
                }
                else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
            }
            catch (Exception e) {

            }
            imageView.setImageBitmap(bitmap);*/
        }

        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            req=false;
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                        if ((ActivityCompat.shouldShowRequestPermissionRationale(FullScreenImage.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(FullScreenImage.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE))) {
                        } else {
                            ActivityCompat.requestPermissions(FullScreenImage.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_PERMISSIONS);
                        }
                    }
                }
            });
        }
        else{
            if(!req) startActivity(new Intent(FullScreenImage.this, MainActivity.class));
            req=true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(this,MainActivity.class);
                        startActivity(intent);
                        req=true;
                        break;
                    } else {
                        Toast.makeText(this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}