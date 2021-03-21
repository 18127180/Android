package com.example.gallery_noob;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by deepshikha on 20/3/17.
 */

public class PhotosActivity extends AppCompatActivity {
    int int_position;
    private GridView gridView;
    GridViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        gridView = (GridView)findViewById(R.id.grid_view);

        Intent i = getIntent();
        int_position = i.getIntExtra("value", 0);
        //ArrayList<Model_images>al_images = i.getParcelableArrayListExtra("al_images");
        getSupportActionBar().setTitle(MainActivity.al_images.get(int_position).str_folder);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new GridViewAdapter(getApplicationContext(),MainActivity.al_images,int_position);
        //Adapter_PhotosFolder adapter = new Adapter_PhotosFolder(this,MainActivity.al_images);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(PhotosActivity.this,FullScreenImage.class);
                intent.putExtra("path", SharedData.al_images.get(int_position).getAl_imagepath().get(position));
                startActivity(intent);
            }
        });
        gridView.setVerticalSpacing(5);
        gridView.setHorizontalSpacing(2);
    }

    @Override
    public void onBackPressed() {
        //Intent intent = new Intent(PhotosActivity.this,ThirdFragment.class);
        //startActivity(intent);
        setResult(RESULT_OK, new Intent().setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }
}