package com.example.gallery_noob;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import ly.img.android.pesdk.PhotoEditorSettingsList;
import ly.img.android.pesdk.assets.filter.basic.FilterPackBasic;
import ly.img.android.pesdk.assets.font.basic.FontPackBasic;
import ly.img.android.pesdk.assets.frame.basic.FramePackBasic;
import ly.img.android.pesdk.assets.overlay.basic.OverlayPackBasic;
import ly.img.android.pesdk.assets.sticker.emoticons.StickerPackEmoticons;
import ly.img.android.pesdk.assets.sticker.shapes.StickerPackShapes;
import ly.img.android.pesdk.backend.model.EditorSDKResult;
import ly.img.android.pesdk.backend.model.state.LoadSettings;
import ly.img.android.pesdk.backend.model.state.PhotoEditorSaveSettings;
import ly.img.android.pesdk.backend.model.state.manager.SettingsList;
import ly.img.android.pesdk.ui.activity.EditorBuilder;
import ly.img.android.pesdk.ui.model.state.UiConfigFilter;
import ly.img.android.pesdk.ui.model.state.UiConfigFrame;
import ly.img.android.pesdk.ui.model.state.UiConfigOverlay;
import ly.img.android.pesdk.ui.model.state.UiConfigSticker;
import ly.img.android.pesdk.ui.model.state.UiConfigText;
import ly.img.android.pesdk.ui.panels.item.PersonalStickerAddItem;
import ly.img.android.pesdk.ui.utils.PermissionRequest;
import ly.img.android.serializer._3.IMGLYFileWriter;

public class FullScreenImage extends AppCompatActivity implements PermissionRequest.Response {
    static LinearLayout function_bar;
    static LinearLayout title_bar;

    static String position;
    private static final int REQUEST_PERMISSIONS = 100;
    public static int PESDK_RESULT = 1;
    static boolean req = false;
    Button button;
    static boolean visibility;
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
    ArrayList<String> addList;
    static ArrayList<String> favList;
    private static ArrayList<Model_images> al_images = new ArrayList<>();
    //-----------------------------
    int cur_select;
    private int slideShowPosition;
    TextView send,imageMore;
    TextView del,edit;
    static TextView fav;
    static boolean favStatus;
    boolean slideShow_MODE;

    Handler mSlideshowHandler = new Handler();
    private Runnable runSlideshow = new Runnable() {
        public void run() {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            mSlideshowHandler.postDelayed(runSlideshow,
                    3000);
        }
    };

    @Override
    public void permissionGranted() {}

    @Override
    public void permissionDenied() {
        /* TODO: The Permission was rejected by the user. The Editor was not opened,
         * Show a hint to the user and try again. */
    }

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

    public void onClickSubMenu() {
        PopupMenu popupMenu=new PopupMenu(this, imageMore);
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
                        break;
                    case R.id.item3:
                        Intent goTo = new Intent(getApplicationContext(), faceDetection.class);
                        goTo.putExtra("current_path",listOfPathImages.get(cur_select));
                        startActivity(goTo);
                        break;
                    case R.id.item4:
                        showDialog();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        imageMore=findViewById(R.id.more);
        Drawable background = ContextCompat.getDrawable(this, R.drawable
                .popup_menu_background);

        fn_imagespath();

        imageMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSubMenu();
            }
        });

        viewPager= (ViewPager) findViewById(R.id.view_pager);
        function_bar= (LinearLayout)findViewById(R.id.func_bar);
        title_bar= (LinearLayout)findViewById(R.id.title_bar);
        function_bar.setVisibility(View.GONE);
        title_bar.setVisibility(View.GONE);
        visibility = false;

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
        //=======================================edit===============================================
        edit = (TextView)findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cur = viewPager.getCurrentItem();
                String filename = listOfPathImages.get(cur);
                Uri uri = FileProvider.getUriForFile(getApplicationContext(),"com.example.gallery_noob",new File(filename));
//                Toast.makeText(getApplicationContext(),uri.toString(),Toast.LENGTH_SHORT).show();
                openEditor(uri);
            }
        });
        //=============================================del==========================================
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
                        break;
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
        // Get the intent that started this activity
        Intent i = getIntent();
        Uri data = i.getData();
        position=null;

        // Figure out what to do based on the intent type
        if (i.getType().indexOf("image/") != -1 || i.getType().equals("video/")) {
            if (i.getType().indexOf("image/") != -1 || i.getType().equals("video/")){
                File file = new File(getPath(data));
                File parent = file.getParentFile();
                listOfPathImages = new ArrayList<>();
                for (File f : parent.listFiles()) {
                    listOfPathImages.add(f.getAbsolutePath());
                }
                position = file.getAbsolutePath();
                req_from = 4;
                slideShow_MODE = false;
            }
        } else {

            listOfPathImages = new ArrayList<String>();
            listOfPathImages = getIntent().getStringArrayListExtra("listOfImages");
            position = i.getExtras().getString("path");
            req_from = i.getExtras().getInt("req_from");
            slideShow_MODE = i.getExtras().getBoolean("slideShow_MODE");
        }
        delList = new ArrayList<>();
        addList = new ArrayList<>();
        frag_array = new ArrayList<>();

        for (int j=0;j<listOfPathImages.size();j++)
        {
            if (isImageFile(listOfPathImages.get(j)))
            {
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
//           viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
//           viewPager.setCurrentItem(listOfPathImages.indexOf(position));
        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (slideShow_MODE==true) {
                    FragmentLifecycle fragmentToShow = (FragmentLifecycle) adapter.getItem(position);
                    fragmentToShow.onResumeFragment();
                    cur_select = position;
                }
                else {
                    FragmentLifecycle fragmentToShow = (FragmentLifecycle) adapter.getItem(position);
                    fragmentToShow.onResumeFragment();
                    cur_select = position;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
        viewPager.setOnPageChangeListener(pageChangeListener);
        viewPager.post(() -> viewPager.setCurrentItem(listOfPathImages.indexOf(position),false));
        viewPager.post(new Runnable()
        {
            @Override
            public void run()
            {
                pageChangeListener.onPageSelected(viewPager.getCurrentItem());
            }
        });
        if (slideShow_MODE)
        {
            try {
                Interpolator sInterpolator = new AccelerateInterpolator();
                Field mScroller;
                mScroller = ViewPager.class.getDeclaredField("mScroller");
                mScroller.setAccessible(true);
                FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(), sInterpolator);
                mScroller.set(viewPager, scroller);
            } catch (NoSuchFieldException e) {
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
            viewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
                private static final float MIN_SCALE = 0.85f;
                private static final float MIN_ALPHA = 0.5f;
                private static final float MIN_SCALE1 = 0.75f;
                @Override
                public void transformPage(View view, float position) {
                    switch (cur_select%3)
                    {
                        case 0:
                            Log.e("slide",""+cur_select);
                            if(position <= -1.0F || position >= 1.0F) {
                                view.setTranslationX(view.getWidth() * position);
                                view.setAlpha(0.0F);
                            } else if( position == 0.0F ) {
                                view.setTranslationX(view.getWidth() * position);
                                view.setAlpha(1.0F);
                            } else {
                                // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                                view.setTranslationX(view.getWidth() * -position);
                                view.setAlpha(1.0F - Math.abs(position));
                            }
                            break; case 1: Log.e("slide",""+cur_select);
                            int pageWidth = view.getWidth();
                            int pageHeight = view.getHeight();
                            if (position < -1) { // [-Infinity,-1)
                                // This page is way off-screen to the left.
                                view.setAlpha(0f);
                            } else if (position <= 1) { // [-1,1]
                                // Modify the default slide transition to shrink the page as well
                                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                                if (position < 0) {
                                    view.setTranslationX(horzMargin - vertMargin / 2);
                                } else {
                                    view.setTranslationX(-horzMargin + vertMargin / 2);
                                }
                                // Scale the page down (between MIN_SCALE and 1)
                                view.setScaleX(scaleFactor);
                                view.setScaleY(scaleFactor);
                                // Fade the page relative to its size.
                                view.setAlpha(MIN_ALPHA +
                                        (scaleFactor - MIN_SCALE) /
                                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));
                            } else { // (1,+Infinity]
                                // This page is way off-screen to the right.
                                view.setAlpha(0f);
                            }
                            break; case 2: Log.e("slide",""+cur_select);
                            int pageWidth1 = view.getWidth();
                            if (position < -1) { // [-Infinity,-1)
                                // This page is way off-screen to the left.
                                view.setAlpha(0f);
                            } else if (position <= 0) { // [-1,0]
                                // Use the default slide transition when moving to the left page
                                view.setAlpha(1f);
                                view.setTranslationX(0f);
                                view.setScaleX(1f);
                                view.setScaleY(1f);
                            } else if (position <= 1) { // (0,1]
                                // Fade the page out.
                                view.setAlpha(1 - position);
                                // Counteract the default slide transition
                                view.setTranslationX(pageWidth1 * -position);
                                // Scale the page down (between MIN_SCALE and 1)
                                float scaleFactor = MIN_SCALE1
                                        + (1 - MIN_SCALE1) * (1 - Math.abs(position));
                                view.setScaleX(scaleFactor);
                                view.setScaleY(scaleFactor);
                            } else { // (1,+Infinity]
                                // This page is way off-screen to the right.
                                view.setAlpha(0f);
                            }
                            break;
                    }
                }
            });
        }
    }

    Uri uri;
    public void onSend() throws FileNotFoundException {
        int cur = viewPager.getCurrentItem();
        position = listOfPathImages.get(cur);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
//        String path = MediaStore.Images.Media.insertImage(getContentResolver(),position,"Temp",null);
//        uri = Uri.parse(path);
        uri = FileProvider.getUriForFile(this,"com.example.gallery_noob",new File(position));
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Image"));
    }

    public void onDel() throws FileNotFoundException {
        int cur = viewPager.getCurrentItem();
        position = listOfPathImages.get(cur);
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

            frag_array.remove(cur);
            listOfPathImages.remove(cur);
            adapter.notifyChangeInPosition(1);
            adapter.notifyDataSetChanged();
            if(listOfPathImages.isEmpty()) {
                onBackPressed();
            }
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
//        super.onBackPressed();
//        if(uri != null){
//            getApplicationContext().getContentResolver().delete(uri, null, null);
//        }
        Intent intent = new Intent();
        if (req_from == 1) {  //Neu tu first fragment sang day, ve First fragment
            intent.putStringArrayListExtra("delList",delList);
            intent.putStringArrayListExtra("addList",addList);
            intent.putStringArrayListExtra("al_images", listOfPathImages);
        }else if(req_from == 2){    //Neu tu trang yeu thich qua thi quay lai trang yeu thich
//            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putStringArrayListExtra("delList",delList);
            intent.putStringArrayListExtra("al_images", listOfPathImages);
        }else if(req_from == 3){    //Neu tu trang album qua thi quay lai trang album
            intent.putStringArrayListExtra("delList",delList);
            intent.putStringArrayListExtra("addList",addList);
        }else if(req_from == 4){
//            outer application intent
//            intent = new Intent("com.example.RESULT_ACTION", Uri.parse("content://result_uri"));
        }
        setResult(RESULT_OK, intent);
        finish();
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

    public static void toggleBar(){
        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(200);
        transition.addTarget(function_bar);
        TransitionManager.beginDelayedTransition((ViewGroup) function_bar.getParent(),transition);

        if(visibility){
            function_bar.setVisibility(View.GONE);
            title_bar.setVisibility(View.GONE);
            Log.e("ERROR","turn invisible");
        }else{
            function_bar.setVisibility(View.VISIBLE);
            title_bar.setVisibility(View.VISIBLE);
            Log.e("ERROR","turn visible");
        }
        visibility = !visibility;
    }

    //-------------------------------------------edit-----------------------------------------------

    private SettingsList createPesdkSettingsList() {

        // Create a empty new SettingsList and apply the changes on this referance.
        PhotoEditorSettingsList settingsList = new PhotoEditorSettingsList();

        // If you include our asset Packs and you use our UI you also need to add them to the UI,
        // otherwise they are only available for the backend
        // See the specific feature sections of our guides if you want to know how to add our own Assets.

        settingsList.getSettingsModel(UiConfigFilter.class).setFilterList(
                FilterPackBasic.getFilterPack()
        );

        settingsList.getSettingsModel(UiConfigText.class).setFontList(
                FontPackBasic.getFontPack()
        );

        settingsList.getSettingsModel(UiConfigFrame.class).setFrameList(
                FramePackBasic.getFramePack()
        );

        settingsList.getSettingsModel(UiConfigOverlay.class).setOverlayList(
                OverlayPackBasic.getOverlayPack()
        );

        settingsList.getSettingsModel(UiConfigSticker.class).setStickerLists(
                new PersonalStickerAddItem(),
                StickerPackEmoticons.getStickerCategory(),
                StickerPackShapes.getStickerCategory()
        );

        return settingsList;
    }

    private void openEditor(Uri inputImage) {
        SettingsList settingsList = createPesdkSettingsList();

        // Set input image
        settingsList.getSettingsModel(LoadSettings.class).setSource(inputImage);

        settingsList.getSettingsModel(PhotoEditorSaveSettings.class).setOutputToGallery(Environment.DIRECTORY_DCIM);

        new EditorBuilder(this)
                .setSettingsList(settingsList)
                .startActivityForResult(this, PESDK_RESULT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Eroor","pause");
        if (mSlideshowHandler!= null) {
            mSlideshowHandler.removeCallbacks(runSlideshow);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Eroor","resume");
        if (slideShow_MODE)
        {
            mSlideshowHandler.postDelayed(runSlideshow, 3000);
        }
    }

    boolean boolean_folder;
    public void fn_imagespath() {
        al_images.clear();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        Log.e("DB","OK you can do it now");

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        try{
            cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
//                Log.e("Column", absolutePathOfImage);
//                Log.e("Folder", cursor.getString(column_index_folder_name));

                for (int i = 0; i < al_images.size(); i++) {
                    if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                        boolean_folder = true;
                        int_position = i;
                        break;
                    } else {
                        boolean_folder = false;
                    }
                }

                if (boolean_folder) {

                    ArrayList<String> al_path = new ArrayList<>();
                    al_path.addAll(al_images.get(int_position).getAl_imagepath());
                    al_path.add(absolutePathOfImage);
                    al_images.get(int_position).setAl_imagepath(al_path);

                } else {
                    ArrayList<String> al_path = new ArrayList<>();
                    al_path.add(absolutePathOfImage);
                    Model_images obj_model = new Model_images();
                    obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                    obj_model.setAl_imagepath(al_path);

                    al_images.add(obj_model);
                }
            }
        }catch(Exception exc){
            Log.e("Error",exc.toString());
        }

        //doc folder cua nguoi dung tao
        ArrayList<Folder> folders = ThirdFragment.loadFolderList(getApplicationContext());
        if(folders != null && folders.size() > 0){
            for(Folder folder: folders){
                Model_images tempModel = new Model_images(folder.getFolderName());
                File dir = getApplicationContext().getDir(folder.getFolderName(), Context.MODE_PRIVATE);//Creating an internal dir;
                File[] al_imagespath = dir.listFiles();
                for(File f: al_imagespath){
                    tempModel.al_imagepath.add(f.getAbsolutePath());
                }
                al_images.add(tempModel);
            }
        }
    }

    private void showDialog(){
        try {
            int cur = viewPager.getCurrentItem();
            Dialog dialog = new Dialog(this);
            View view = getLayoutInflater().inflate(R.layout.dialog_main, null);
            ListView lv = (ListView) view.findViewById(R.id.custom_list);
            CustomListAdapterDialog clad = new CustomListAdapterDialog(FullScreenImage.this, al_images);
            lv.setAdapter(clad);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getApplicationContext(), listOfPathImages.get(cur), Toast.LENGTH_SHORT).show();
                    File f = new File(al_images.get(position).getAl_imagepath().get(0));
                    try {
                        String s = copy(listOfPathImages.get(cur),f.getParent());
                        al_images.get(position).al_imagepath.add(s);
                        clad.notifyDataSetChanged();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.cancel();
                }
            });
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.cancel();
                }
            });
            dialog.setContentView(view);
            dialog.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String copy(String src, String dst) throws IOException {
        String dstFilePath = null;          //dst o day la duong dan thu muc
        if(Build.VERSION.SDK_INT >= 26){
            Path srcPath = Paths.get(src);
            Path dstPath = Paths.get(dst);
            Path dst_file_path = Files.copy(srcPath,dstPath.resolve(srcPath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            dstFilePath = dst_file_path.toString();
        }
        else{
            //de cho cac API >= 19
            try {
                File srcFile = new File(src);
                dst = dst+"/"+srcFile.getName();
                dstFilePath = dst;
                if(dst.equals(src))     return null;
                File dstFile = new File(dst);
                try (InputStream in = new FileInputStream(srcFile)) {
                    try (OutputStream out = new FileOutputStream(dstFile)) {
                        // Transfer bytes from in to out
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e("dst File path: ",dstFilePath);
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(dstFilePath);
//        if(f.exists()){
//            Uri contentUri = FileProvider.getUriForFile(FullScreenImage.this,"com.example.gallery_noob",f);
//            Log.e("dst Uri path",contentUri.toString());
//            mediaScanIntent.setData(contentUri);
//            getApplicationContext().sendBroadcast(mediaScanIntent);
//        }
        MediaScannerConnection.scanFile(this,
                new String[] { f.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("ExternalStorage", "Scanned " + path + ":");
                        Log.e("ExternalStorage", "-> uri=" + uri);
                    }
                });
        addList.add(dstFilePath);
        return dstFilePath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == PESDK_RESULT) {
            // Editor has saved an Image.
            EditorSDKResult data = new EditorSDKResult(intent);

            // This adds the result and source image to Android's gallery
            data.notifyGallery(EditorSDKResult.UPDATE_RESULT & EditorSDKResult.UPDATE_SOURCE);

            Log.i("PESDK", "Source image is located here " + data.getSourceUri());
            Log.i("PESDK", "Result image is located here " + data.getResultUri());

            // TODO: Do something with the result image

            // OPTIONAL: read the latest state to save it as a serialisation
            SettingsList lastState = data.getSettingsList();
            new IMGLYFileWriter(lastState).writeJson(new File(
                    Environment.getExternalStorageDirectory(),
                    "serialisationReadyToReadWithPESDKFileReader.json"
            ));

        } else if (resultCode == RESULT_CANCELED && requestCode == PESDK_RESULT) {
            // Editor was canceled
            EditorSDKResult data = new EditorSDKResult(intent);

            Uri sourceURI = data.getSourceUri();
            // TODO: Do something with the source...
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}