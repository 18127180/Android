package com.example.gallery_noob;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.loader.content.CursorLoader;

import java.util.ArrayList;

public class imagesGallery {
    public static ArrayList<String> listOfImages(Context context){
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> lisOfAllImages=new ArrayList<>();
        String absolutePath;
        uri= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        String[] projection={MediaStore.MediaColumns.DATA,MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };
//        String orderBy=MediaStore.Video.Media.DATE_TAKEN;
//        cursor=context.getContentResolver().query(uri,projection,null,null,orderBy+" DESC");
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Uri queryUri = MediaStore.Files.getContentUri("external");
        CursorLoader cursorLoader = new CursorLoader(
                context,
                queryUri,
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC" // Sort order.
        );
        cursor = cursorLoader.loadInBackground();
        column_index_data=cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while(cursor.moveToNext())
        {
            absolutePath=cursor.getString(column_index_data);
            //Log.e("Path",absolutePath);
            lisOfAllImages.add(absolutePath);
        }
        return  lisOfAllImages;
    }

    public static ArrayList<String> listOfVideos(Context context){
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> lisOfAllImages=new ArrayList<>();
        String absolutePath;
        uri= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        String[] projection={MediaStore.MediaColumns.DATA,MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };
//        String orderBy=MediaStore.Video.Media.DATE_TAKEN;
//        cursor=context.getContentResolver().query(uri,projection,null,null,orderBy+" DESC");
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Uri queryUri = MediaStore.Files.getContentUri("external");
        CursorLoader cursorLoader = new CursorLoader(
                context,
                queryUri,
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC" // Sort order.
        );
        cursor = cursorLoader.loadInBackground();
        column_index_data=cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while(cursor.moveToNext())
        {
            absolutePath=cursor.getString(column_index_data);
            //Log.e("Path",absolutePath);
            lisOfAllImages.add(absolutePath);
        }
        return  lisOfAllImages;
    }
}
