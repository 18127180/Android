package com.example.gallery_noob;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ly.img.android.pesdk.ui.utils.PermissionRequest;

public class Welcome extends AppCompatActivity {
    ImageView welcome_image;
    Button welcome_button;
    private final int REQUEST_PERMISSIONS = 101;

    private void setColor(int lang)
    {
        if (lang!=-1)
        {
            SharedPreferences.Editor editor = getSharedPreferences("SetColor", Context.MODE_PRIVATE).edit();
            editor.putInt("My_color_sl",lang);
            editor.apply();
            if (lang==0)
            {
                setTheme(R.style.ThemeChoice);
            }
            if (lang==1)
            {
                setTheme(R.style.ThemeChoice1);
            }
            if (lang==2)
            {
                setTheme(R.style.ThemeChoice2);
            }
        }
    }

    public void loadColor(){
        SharedPreferences preferences=getSharedPreferences("SetColor", MODE_PRIVATE);
        int language=preferences.getInt("My_color_sl",-1);
        setColor(language);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadColor();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        welcome_image = (ImageView)findViewById(R.id.welcome_image);
        welcome_button = (Button)findViewById(R.id.welcome_button);

        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            welcome_button.setVisibility(View.VISIBLE);
            welcome_image.setVisibility(View.VISIBLE);
            welcome_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(Welcome.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(Welcome.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE))) {
                    } else {
                        ActivityCompat.requestPermissions(Welcome.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                    }
                }
            });
        }else{
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(this,MainActivity.class);
                        startActivity(intent);
                        break;
                    } else {
                        Toast.makeText(this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
        }
    }
}