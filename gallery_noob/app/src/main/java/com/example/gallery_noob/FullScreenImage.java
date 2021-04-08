package com.example.gallery_noob;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLConnection;
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
    private ArrayList<String> listOfPathImages;
    ViewPager viewPager;
    MyFragmentAdapter adapter;
    private float x1,x2,y1,y2;
    private float MIN_DISTANCE=150;
    static int req_from = 1;
    ArrayList<String> delList;

    TextView send;
    TextView del;

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }
    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        viewPager= (ViewPager) findViewById(R.id.view_pager);
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

        del = (TextView)findViewById(R.id.delete);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onDel();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Cannot delete this picture",Toast.LENGTH_LONG).show();
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
                onBackPressed();
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
            req_from = i.getExtras().getInt("req_from");
            delList = new ArrayList<>();

            //Log.e("Size cua mang ",""+listOfPathImages.size());
//            adapter = new ViewPagerAdapter(this,listOfPathImages.toArray(new String[listOfPathImages.size()]));
            ArrayList <Fragment> frag_array=new ArrayList<>();
            for (int j=0;j<listOfPathImages.size();j++)
            {
                if (isImageFile(listOfPathImages.get(j)))
                {
                    imageFragment item=new imageFragment(listOfPathImages.get(j));
                    frag_array.add(item);
                }
                if (isVideoFile(listOfPathImages.get(j)))
                {
                    videoFragment item=new videoFragment(listOfPathImages.get(j));
                    frag_array.add(item);
                }
            }
            adapter=new MyFragmentAdapter(getSupportFragmentManager(),frag_array);
//            viewPager.setOffscreenPageLimit(0);
            viewPager.setAdapter(adapter);
//            viewPager.setCurrentItem(listOfPathImages.indexOf(position));
            ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    FragmentLifecycle fragmentToShow = (FragmentLifecycle)adapter.getItem(position);
                    Log.e("pause","onPause");
                    fragmentToShow.onResumeFragment();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            };
            viewPager.setOnPageChangeListener(pageChangeListener);
//            pageChangeListener.onPageSelected(listOfPathImages.indexOf(position));
            viewPager.post(() -> viewPager.setCurrentItem(listOfPathImages.indexOf(position)));
            viewPager.post(new Runnable()
            {
                @Override
                public void run()
                {
                    pageChangeListener.onPageSelected(viewPager.getCurrentItem());
                }
            });
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
        File f = new File(path);
        f.delete();
    }

    public void onDel() throws FileNotFoundException {
        File f = new File(position);
        if(f.exists()){
            f.delete();
            ContentResolver contentResolver = getContentResolver();
            contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Images.ImageColumns.DATA + "=?", new String[]{position});
            delList.add(position);
        }
        if(position != null){
//                        int idx = listOfPathImages.indexOf(position);
//                        if(idx==listOfPathImages.size()-1){
//                            idx--;
//                        }
//                        adapter = new ViewPagerAdapter(getApplicationContext(),listOfPathImages.toArray(new String[listOfPathImages.size()]));
//                        viewPager.setAdapter(adapter);
//                        viewPager.setCurrentItem(idx);
            //adapter.deletePath(position);(Dang comment)
            adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (req_from == 1) {  //Neu tu first fragment sang day, ve First fragment
            Intent intent = new Intent(FullScreenImage.this, MainActivity.class);
            startActivity(intent);
        }else if(req_from == 3){    //Neu tu trang album qua thi quay lai trang album
            Intent intent = new Intent();
            intent.putStringArrayListExtra("delList",delList);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}