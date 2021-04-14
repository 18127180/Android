package com.example.gallery_noob;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private ArrayList <Fragment> frag_array;
    ViewPager viewPager;
    MyFragmentAdapter adapter;
    private float x1,x2,y1,y2;
    private float MIN_DISTANCE=150;
    static int req_from = 1;
    //---return elements
    ArrayList<String> delList;
    static ArrayList<String> favList;
    //-----------------------------
    int cur_select;

    TextView send,imageMore;
    TextView del;
    static TextView fav;
    static boolean favStatus;

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }
    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public String readExif(String path)
    {
        String exif="Exif: " + path;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            exif += "\nIMAGE_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            exif += "\nIMAGE_WIDTH: " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            exif += "\n DATETIME: " + exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            exif += "\n TAG_MAKE: " + exifInterface.getAttribute(ExifInterface.TAG_MAKE);
            exif += "\n TAG_MODEL: " + exifInterface.getAttribute(ExifInterface.TAG_MODEL);
            exif += "\n TAG_ORIENTATION: " + exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
            exif += "\n TAG_WHITE_BALANCE: " + exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
            exif += "\n TAG_FOCAL_LENGTH: " + exifInterface.getAttributeDouble(ExifInterface.TAG_FOCAL_LENGTH,0);
            exif += "\n TAG_FLASH: " + exifInterface.getAttribute(ExifInterface.TAG_FLASH);
            exif += "\n TAG_ISO: " + exifInterface.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS);//TAG_F_NUMBER
            exif += "\n F: " + exifInterface.getAttribute(ExifInterface.TAG_F_NUMBER);//TAG_SHUTTER_SPEED_VALUE
            exif += "\n Exposure: " + exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);//TAG_SHUTTER_SPEED_VALUE
            exif += "\nGPS related:";
            exif += "\n TAG_GPS_DATESTAMP: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
            exif += "\n TAG_GPS_TIMESTAMP: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
            exif += "\n TAG_GPS_LATITUDE: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            exif += "\n TAG_GPS_LATITUDE_REF: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            exif += "\n TAG_GPS_LONGITUDE: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            exif += "\n TAG_GPS_LONGITUDE_REF: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            exif += "\n TAG_GPS_PROCESSING_METHOD: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } return exif;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        imageMore=findViewById(R.id.more);
        Drawable background = ContextCompat.getDrawable(this, R.drawable
                .popup_menu_background);

        imageMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(getApplicationContext(), imageMore);
                popupMenu.getMenuInflater().inflate(R.menu.more_image_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item1:
                                Intent goToNextActivity = new Intent(getApplicationContext(), detail_media_activity.class);
                                goToNextActivity.putExtra("current_path",listOfPathImages.get(cur_select));
                                startActivity(goToNextActivity);
                                break;
                            case R.id.item2:
                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                int height = displayMetrics.heightPixels;
                                int width = displayMetrics.widthPixels << 1; // best wallpaper width is twice screen width

                                // First decode with inJustDecodeBounds=true to check dimensions
                                final BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(listOfPathImages.get(cur_select), options);

                                // Calculate inSampleSize
                                options.inSampleSize = calculateInSampleSize(options, width, height);

                                // Decode bitmap with inSampleSize set
                                options.inJustDecodeBounds = false;
                                Bitmap decodedSampleBitmap = BitmapFactory.decodeFile(listOfPathImages.get(cur_select), options);

                                WallpaperManager wm = WallpaperManager.getInstance(getApplicationContext());
                                try {
                                    wm.setBitmap(decodedSampleBitmap);
                                } catch (IOException e) {
                                    Log.e("TAG", "Cannot set image as wallpaper", e);
                                }

//                                WallpaperManager myWallpaperManager
//                                        = WallpaperManager.getInstance(getApplicationContext());
//                                try {
//                                    myWallpaperManager.setBitmap(BitmapFactory.decodeFile(listOfPathImages.get(cur_select)));
//                                } catch (IOException e) {
//                                    // TODO Auto-generated catch block
//                                    e.printStackTrace();
//                                }
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });


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

        //-----------------------------------------Favourite processing---------------------------------------------------------

        favList = new ArrayList<>();
        favList = loadFavouriteList();
        favStatus = false;

        fav = (TextView)findViewById(R.id.favourite);
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFav();
            }
        });

        //----------------------------------------------------------------------------------------------------------------------

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
            frag_array=new ArrayList<>();
            for (int j=0;j<listOfPathImages.size();j++)
            {
                if (isImageFile(listOfPathImages.get(j)))
                {
                    Log.e("Information",readExif(listOfPathImages.get(j)));
                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(listOfPathImages.get(j));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    double[] latLong = exif.getLatLong();
                    if (latLong!=null) {
                        System.out.println(listOfPathImages.get(j));
                        System.out.println("Latitude: " + latLong[0]);
                        System.out.println("Longitude: " + latLong[1]);
                    }
                    Fragment item=new imageFragment(listOfPathImages.get(j));
                    frag_array.add(item);
                }
                if (isVideoFile(listOfPathImages.get(j)))
                {
                    Fragment item=new videoFragment(listOfPathImages.get(j));
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
                    fragmentToShow.onResumeFragment();
                    cur_select=position;
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

    Uri uri;
    public void onSend() throws FileNotFoundException {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        String path = MediaStore.Images.Media.insertImage(getContentResolver(),position,"Temp",null);
        uri = Uri.parse(path);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Image"));
    }

    public void onDel() throws FileNotFoundException {
        File f = new File(position);
        if(f.exists()){
            if (favList != null && favList.contains(position)) {       //Neu xoa co trong danh sach favourite thi xoa luon
                favList.remove(position);
                saveFavouriteList();
            }
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
            int cur = viewPager.getCurrentItem();
            frag_array.remove(cur);
            listOfPathImages.remove(cur);
            adapter.notifyDataSetChanged();

            Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
        }
    }

    public void onFav(){
        favStatus = !favStatus;
        int cur = viewPager.getCurrentItem();
        String path = listOfPathImages.get(cur);
        if(favStatus){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                fav.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_baseline_favorite_24,0,0);
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                fav.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_baseline_favorite,0,0);
            }
        }

        if(FullScreenImage.favList != null){
            Log.e("ERROR","Chay ! null");
            if(favStatus && !favList.contains(path)){
                favList.add(path);
                saveFavouriteList();
            }else if(!favStatus && favList.contains(path)){
                favList.remove(path);
                saveFavouriteList();
            }
        }else{
            Log.e("ERROR","Chay null");
            if(favStatus) {
                favList = new ArrayList<>();
                favList.add(path);
                saveFavouriteList();
            }
        }
        Toast.makeText(getApplicationContext(),String.valueOf(cur),Toast.LENGTH_LONG).show();

        for(String i: favList){
            Log.e("E",i);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(uri != null){
            getApplicationContext().getContentResolver().delete(uri, null, null);
        }
        Intent intent = new Intent();
        if (req_from == 1) {  //Neu tu first fragment sang day, ve First fragment
            intent.putStringArrayListExtra("delList",delList);
            intent.putStringArrayListExtra("al_images", listOfPathImages);
            setResult(RESULT_OK, intent);
            finish();
        }else if(req_from == 2){    //Neu tu trang yeu thich qua thi quay lai trang yeu thich
//            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putStringArrayListExtra("delList",delList);
            intent.putStringArrayListExtra("al_images", listOfPathImages);
            setResult(RESULT_OK, intent);
            finish();
        }else if(req_from == 3){    //Neu tu trang album qua thi quay lai trang album
            intent.putStringArrayListExtra("delList",delList);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private ArrayList<String> loadFavouriteList() {      //ham lay danh sach favourite
        ArrayList<String> temp = new ArrayList<>();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("favourite", "");
        temp = gson.fromJson(json, new TypeToken<List<String>>(){}.getType());
        return temp;
    }

    public void saveFavouriteList(){     //ham luu danh sach favourite
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(FullScreenImage.favList);
        edit.putString("favourite", json);
        edit.commit();
    }
}