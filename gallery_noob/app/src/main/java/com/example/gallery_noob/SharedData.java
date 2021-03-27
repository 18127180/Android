package com.example.gallery_noob;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

public class SharedData extends Activity {
    public SharedData(Context context) {
        al_images = new ArrayList<>();
        this.context =  context;
    }
    public SharedData(){
        al_images = new ArrayList<>();
    }

    public static ArrayList<Model_images> getAl_images() {
        return al_images;
    }

    public static ArrayList<Model_images> al_images = new ArrayList<>();
    static boolean boolean_folder;
    static int count = 0;
    static Context context;

    public static void addModel(Model_images i){
        SharedData.al_images.add(i);
    }
    public static void addCamera(String path){
        for (int i = 0; i < al_images.size(); i++) {        //De in ra xem trong mang al_images co nhung thu muc gi, file gi ?.
            if(al_images.get(i).getStr_folder().equals("Camera")){
                al_images.get(i).al_imagepath.add(0,path);
                break;
            }
        }
    }

    //Getter & Setter
    public static void setAl_images(ArrayList<Model_images> al_images) {
        SharedData.al_images = new ArrayList<>();
        SharedData.al_images = al_images;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Initiate Gallery
    public static ArrayList<Model_images> fn_imagespath() {
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

        cursor = context.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

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
        return al_images;
    }
}
