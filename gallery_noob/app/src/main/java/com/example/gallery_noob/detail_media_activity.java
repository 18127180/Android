package com.example.gallery_noob;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;

public class detail_media_activity extends AppCompatActivity {
    ImageView back_btn,icon_camera,set_location_btn,location_icon;
    TextView date_modified,name_media,path_media,
            size_media,window_media,location_media,camera_media,
            sub1_camera_media,sub2_camera_media,sub3_camera_media,
            sub4_camera_media,mode1_camera_media,mode2_camera_media;

    TextView fix_btn,exit_btn,save_btn;
    private static final int REQUEST_CODE_DETAIL = 6969;

    private boolean save_mode=false;
    private String name_adress;
    private double Lat,Long,newLat,newLong;

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
    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public double[] getLatLong(String path)
    {
        double[] latLong=null;
        if (isImageFile(path))
        {
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            latLong = exif.getLatLong();
        }
        return latLong;
    }

    public String getAddress(double lat, double lng) {
        String name_adress=null;
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            name_adress = obj.getAddressLine(0);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return name_adress;
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
            double[] latlong=getLatLong(path);
            if (latlong!=null)
            {
                Lat=latlong[0];
                Long=latlong[1];
                name_adress=getAddress(Lat,Long);
                if (name_adress!=null)
                {
                    location_media.setText(name_adress);
                }
                else
                {
                    location_icon.setVisibility(View.GONE);
                    location_media.setVisibility(View.GONE);
                    set_location_btn.setVisibility(View.GONE);
                }
            }
            else
            {
                location_icon.setVisibility(View.GONE);
                location_media.setVisibility(View.GONE);
                set_location_btn.setVisibility(View.GONE);
            }


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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE_DETAIL){
            if (resultCode==Activity.RESULT_OK){
                newLat=data.getDoubleExtra("req_lat",0);
                newLong=data.getDoubleExtra("req_long",0);
                if (newLat!=0 || newLong!=0)
                {
                    location_media.setText(getAddress(newLat,newLong));

                    exit_btn.setVisibility(View.VISIBLE);
                    save_btn.setVisibility(View.VISIBLE);
                    fix_btn.setVisibility(View.GONE);
                    set_location_btn.setVisibility(View.VISIBLE);

                    set_location_btn.setImageResource(R.drawable.ic_baseline_remove_24);
                    set_location_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            set_location_btn.setImageResource(R.drawable.ic_baseline_add_location);
                            location_media.setText(R.string.doiDiaDiem);
                            set_location_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent goToNextActivity = new Intent(getApplicationContext(), MapsActivity.class);
                                    goToNextActivity.putExtra("lat_position",Lat);
                                    goToNextActivity.putExtra("long_position",Long);
                                    startActivityForResult(goToNextActivity, REQUEST_CODE_DETAIL);
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    public static String dec2DMS(double coord) {
        coord = coord > 0 ? coord : -coord;  // -105.9876543 -> 105.9876543
        String sOut = Integer.toString((int)coord) + "/1,";   // 105/1,
        coord = (coord % 1) * 60;         // .987654321 * 60 = 59.259258
        sOut = sOut + Integer.toString((int)coord) + "/1,";   // 105/1,59/1,
        coord = (coord % 1) * 60000;             // .259258 * 60000 = 15555
        sOut = sOut + Integer.toString((int)coord) + "/1000";   // 105/1,59/1,15555/1000
        return sOut;
    }

    public static void writeFile (String path, double latitude, double longitude) throws IOException{
        try {
            ExifInterface ef = new ExifInterface(path);
            ef.setAttribute(ExifInterface.TAG_GPS_LATITUDE, dec2DMS(latitude));
            ef.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,dec2DMS(longitude));
            if (latitude > 0)
                ef.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
            else
                ef.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
            if (longitude>0)
                ef.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
            else
                ef.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
            ef.saveAttributes();
        } catch (IOException e) {}
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
        fix_btn=findViewById(R.id.fix_btn);
        exit_btn=findViewById(R.id.exit_btn);
        save_btn=findViewById(R.id.save_btn);
        icon_camera=findViewById(R.id.icon_camera);
        location_icon=findViewById(R.id.location_icon);
        set_location_btn=findViewById(R.id.set_location_btn);

        String path=getIntent().getStringExtra("current_path");
        if (path!=null)
        {
            readExif(path);
        }

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name_adress=location_media.getText().toString();
                set_location_btn.setVisibility(View.GONE);
                if(name_adress==getString(R.string.doiDiaDiem))
                {
                    location_icon.setVisibility(View.GONE);
                    location_media.setVisibility(View.GONE);
                }
                else
                {
                    location_media.setText(name_adress);
                    Lat=newLat;
                    Long=newLong;
                    try {
                        writeFile(path,Lat,Long);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        fix_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit_btn.setVisibility(View.VISIBLE);
                save_btn.setVisibility(View.VISIBLE);
                fix_btn.setVisibility(View.GONE);
                set_location_btn.setVisibility(View.VISIBLE);

                if (name_adress==null)
                {
                    location_icon.setVisibility(View.VISIBLE);
                    location_media.setVisibility(View.VISIBLE);
                    set_location_btn.setImageResource(R.drawable.ic_baseline_add_location);
                    location_media.setText(R.string.doiDiaDiem);
                    set_location_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent goToNextActivity = new Intent(getApplicationContext(), MapsActivity.class);
                            goToNextActivity.putExtra("lat_position",Lat);
                            goToNextActivity.putExtra("long_position",Long);
                            startActivityForResult(goToNextActivity, REQUEST_CODE_DETAIL);
                        }
                    });
                }
                else
                {
                    set_location_btn.setImageResource(R.drawable.ic_baseline_remove_24);
                    set_location_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            set_location_btn.setImageResource(R.drawable.ic_baseline_add_location);
                            location_media.setText(R.string.doiDiaDiem);
                            set_location_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent goToNextActivity = new Intent(getApplicationContext(), MapsActivity.class);
                                    goToNextActivity.putExtra("lat_position",Lat);
                                    goToNextActivity.putExtra("long_position",Long);
                                    startActivityForResult(goToNextActivity, REQUEST_CODE_DETAIL);
                                }
                            });
                        }
                    });
                }
            }
        });

        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fix_btn.setVisibility(View.VISIBLE);
                exit_btn.setVisibility(View.GONE);
                save_btn.setVisibility(View.GONE);
                set_location_btn.setVisibility(View.GONE);

                if (name_adress!=null)
                {
                    location_media.setText(name_adress);
                }
                else
                {
                    location_icon.setVisibility(View.GONE);
                    location_media.setVisibility(View.GONE);
                }
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}