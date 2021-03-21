package com.example.gallery_noob;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Model_images> al_images = new ArrayList<>();
    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_folder;
    private Adapter_PhotosFolder obj_adapter;

    public ArrayList<Model_images> getAl_images() {
        return al_images;
    }

    public Adapter_PhotosFolder getObj_adapter() {
        return obj_adapter;
    }

    //GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this,  R.id.fragment3);
        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.firstFragment);
        topLevelDestinations.add(R.id.secondFragment);
        topLevelDestinations.add(R.id.thirdFragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        /*Intent intent=getIntent();
        String s=intent.getStringExtra("key");
        if (s!=null)
        {
            Log.d("dc1", s.toString());
        }
        if (s=="allow_select") {
            Log.e("dc", "ok");
            bottomNavigationView.setVisibility(View.GONE);
        }*/

        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        }else {
            Log.e("Else","Loading Gallery");
            //fn_imagespath();
            SharedData i = new SharedData(this);
            i.fn_imagespath();
            al_images = SharedData.getAl_images();
            obj_adapter = new Adapter_PhotosFolder(getApplicationContext(),SharedData.al_images);
            /*
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("al_images", (ArrayList<? extends Parcelable>) al_images);
            bundle.putParcelable("obj_adapter", (Parcelable) obj_adapter);
            //Throw array to third fragment
            ThirdFragment trd = new ThirdFragment();
            trd.setArguments(bundle);
             */
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
                        break;
                    } else {
                        Toast.makeText(this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    public ArrayList<Model_images> fn_imagespath() {
        al_images.clear();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Log.e("DB","OK you can do it now");

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        try{
            cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                Log.e("Column", absolutePathOfImage);
                Log.e("Folder", cursor.getString(column_index_folder_name));

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





        /*
        for (int i = 0; i < al_images.size(); i++) {        //De in ra xem trong mang al_images co nhung thu muc gi, file gi ???
            Log.e("FOLDER", al_images.get(i).getStr_folder());
            for (int j = 0; j < al_images.get(i).getAl_imagepath().size(); j++) {
                Log.e("FILE", al_images.get(i).getAl_imagepath().get(j));
            }
        }
        */
        obj_adapter = new Adapter_PhotosFolder(getApplicationContext(),al_images);
        //gv_folder.setAdapter(obj_adapter);
        return al_images;
    }
}

