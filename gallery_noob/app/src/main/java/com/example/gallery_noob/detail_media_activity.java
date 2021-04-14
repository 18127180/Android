package com.example.gallery_noob;

import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class detail_media_activity extends AppCompatActivity {
    ImageView back_btn,icon_camera,set_location_btn;
    TextView date_modified,name_media,path_media,
            size_media,window_media,location_media,camera_media,
            sub1_camera_media,sub2_camera_media,sub3_camera_media,
            sub4_camera_media,mode1_camera_media,mode2_camera_media;

    public String calculateFileSize(String filepath) {
        //String filepathstr=filepath.toString();
        File file = new File(filepath);
        double fileSizeInBytes = file.length();
        double fileSizeInKB = fileSizeInBytes / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        double fileSizeInMB = fileSizeInKB / 1024;
        if (fileSizeInMB<1)
        {
            Double value=Math.round(fileSizeInKB*100.0)/100.0;
            return Double.toString(value) + " KB";
        }
        Double value=Math.round(fileSizeInMB*100.0)/100.0;
        String calString = Double.toString(value);
        return calString+" MB";
    }

    public String convert_fraction(double num)
    {
        String result=null;
        double negligibleRatio = 0.01;

        for(int i=1;;i++){
            double tem = num/(1D/i);
            if(Math.abs(tem-Math.round(tem))<negligibleRatio){
                result=Math.round(tem)+"/"+i;
                return result;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void readExif(String path)
    {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            //set Datetime
            date_modified.setText(exifInterface.getAttribute(ExifInterface.TAG_DATETIME));

            //set Name media
            File file = new File(path);
            String name;
            name=file.getName();
            name_media.setText(name);

            //set path media
            path_media.setText(path);

            //set size media
            String size_image;
            size_image=exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)+"x"+
                    exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            window_media.setText(size_image);

            //set size MB media
            size_media.setText(calculateFileSize(path));

            //set Location


            //set name device
            String f_number=exifInterface.getAttribute(ExifInterface.TAG_F_NUMBER);
            String exposure_time=exifInterface.getAttribute(ExifInterface.TAG_F_NUMBER);
            String focal_length=exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
            String iso=ExifInterface.TAG_ISO_SPEED_RATINGS;
            String white_mode=exifInterface.TAG_WHITE_BALANCE;
            String flash_mode=ExifInterface.TAG_FLASH;


            String formatString=exifInterface.getAttribute(ExifInterface.TAG_MAKE)+" "+exifInterface.getAttribute(ExifInterface.TAG_MODEL);
            if (formatString==null || f_number==null || exposure_time==null || focal_length==null ||
            iso==null || white_mode==null || flash_mode==null)
            {
                icon_camera.setVisibility(View.GONE);
                return;
            }
            String cap = formatString.substring(0, 1).toUpperCase() + formatString.substring(1);
            camera_media.setText(cap);

            //set F
            sub1_camera_media.setText("F"+f_number);

            //set exposure
            sub2_camera_media.setText(convert_fraction(exifInterface.getAttributeDouble(ExifInterface.TAG_EXPOSURE_TIME,0)));

            //set focal_length
            sub3_camera_media.setText(exifInterface.getAttributeDouble(ExifInterface.TAG_FOCAL_LENGTH,0)+"mm");

            //set ISO
            sub4_camera_media.setText("ISO "+exifInterface.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS));

            //set white balace
            int mode_balace=exifInterface.getAttributeInt(exifInterface.TAG_WHITE_BALANCE,0);
            if (mode_balace==0)
            {
                mode1_camera_media.setText("Cân bằng trắng Tự động");
            }
            else
            {
                mode1_camera_media.setText("Cân bằng trắng Thủ công");
            }

            //set Flash
            int mode_flash=exifInterface.getAttributeInt(ExifInterface.TAG_FLASH,0);

            if (mode_flash==0)
            {
                mode2_camera_media.setText("Không có flash");
            }
            else
            {
                mode2_camera_media.setText("Flash được dùng");
            }
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_detail_media_activity);
        back_btn=findViewById(R.id.back_btn_detail);
        date_modified=findViewById(R.id.date_modified);
        name_media=findViewById(R.id.name_media);
        path_media=findViewById(R.id.path_media);
        size_media=findViewById(R.id.size_media);
        window_media=findViewById(R.id.window_media);
        location_media=findViewById(R.id.location_media);
        camera_media=findViewById(R.id.camera_media);
        sub1_camera_media=findViewById(R.id.sub1_camera_media);
        sub2_camera_media=findViewById(R.id.sub2_camera_media);
        sub3_camera_media=findViewById(R.id.sub3_camera_media);
        sub4_camera_media=findViewById(R.id.sub4_camera_media);
        mode1_camera_media=findViewById(R.id.mode1_camera_media);
        mode2_camera_media=findViewById(R.id.mode2_camera_media);

        icon_camera=findViewById(R.id.icon_camera);
        set_location_btn=findViewById(R.id.set_location_btn);

        set_location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToNextActivity = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(goToNextActivity);
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String path=getIntent().getStringExtra("current_path");
        if (path!=null)
        {
            readExif(path);
        }
    }
}