package com.example.gallery_noob;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class FullScreenImage extends AppCompatActivity {
    ImageView imageView;
    static String position;
    private static final int REQUEST_PERMISSIONS = 100;
    static boolean req = false;
    Button button;
    boolean gone = false;
    ImageButton back_btn;
    private List<String> listOfPathImages;
    private float x1,x2,y1,y2;
    private float MIN_DISTANCE=150;

    TextView send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ViewPager viewPager=findViewById(R.id.view_pager);
        LinearLayout ln= (LinearLayout)findViewById(R.id.full_scr);
        LinearLayout ln3= (LinearLayout)findViewById(R.id.header_detail);
        //ln.setVisibility(View.GONE);
        //ln1.setVisibility(View.GONE);
        //ln3.setVisibility(View.GONE);
        send = (TextView)findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onSend();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        viewPager.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        x1=event.getX();
                        y1=event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2=event.getX();
                        y2=event.getY();
                        if (Math.abs(x1-x2)>MIN_DISTANCE)
                        {
                            //Luot qua phai
//                            if (x2>x1)
//                            {
//                                ln.setVisibility(View.GONE);
//                                ln3.setVisibility(View.GONE);
//                            }
//                            else
//                            {
//                                ln.setVisibility(View.VISIBLE);
//                                ln3.setVisibility(View.VISIBLE);
//                            }
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

        //imageView=(ImageView) findViewById(R.id.image_view);
        button = (Button)findViewById(R.id.button);
        getSupportActionBar().hide();
        Intent i=getIntent();
        position=null;

        if(!req){
            position=null;
            listOfPathImages=null;
        }else{
            listOfPathImages=new ArrayList<String>();
            listOfPathImages = getIntent().getStringArrayListExtra("listOfImages");
            position=i.getExtras().getString("path");
            //Log.e("Size cua mang ",""+listOfPathImages.size());
            ViewPagerAdapter adapter=new ViewPagerAdapter(this,listOfPathImages.toArray(new String[listOfPathImages.size()]));
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(listOfPathImages.indexOf(position));
            viewPager.setOffscreenPageLimit(3);
        }
        //ImageAdapter imageAdapter= new ImageAdapter(this);
//        if(position!=null)
//        {
//            ViewPagerAdapter adapter=new ViewPagerAdapter(this,listOfPathImages.toArray(new String[listOfPathImages.size()]));
//            viewPager.setCurrentItem(listOfPathImages.indexOf(position));
//            viewPager.setAdapter(adapter);
//            Picasso.get().load(new File(position)).into(imageView);
//        }

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

    public void onSend() throws FileNotFoundException {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        String path = MediaStore.Images.Media.insertImage(getContentResolver(),position,"Temp",null);
        Uri uri = Uri.parse(path);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Image"));
    }
}