package com.example.gallery_noob;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by deepshikha on 20/3/17.
 */

public class PhotosActivity extends AppCompatActivity {
    int int_position;
    private GridView gridView;
    GridViewAdapter adapter;
    ArrayList<Model_images> al_images = new ArrayList<>();
    private static int REQUEST_CODE_ALBUM = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        gridView = (GridView)findViewById(R.id.grid_view);

        Intent i = getIntent();
        int_position = i.getIntExtra("value", 0);
        al_images = i.getParcelableArrayListExtra("al_images");
        //ArrayList<Model_images>al_images = i.getParcelableArrayListExtra("al_images");
        getSupportActionBar().setTitle(al_images.get(int_position).str_folder);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new GridViewAdapter(getApplicationContext(),al_images,int_position);
        //Adapter_PhotosFolder adapter = new Adapter_PhotosFolder(this,MainActivity.al_images);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(PhotosActivity.this,FullScreenImage.class);
                intent.putExtra("path", al_images.get(int_position).getAl_imagepath().get(position));
                intent.putStringArrayListExtra("listOfImages",(ArrayList<String>)al_images.get(int_position).getAl_imagepath());
                intent.putExtra("req_from",3);
                startActivityForResult(intent,REQUEST_CODE_ALBUM);
            }
        });
        gridView.setVerticalSpacing(5);
        gridView.setHorizontalSpacing(2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photos_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.setPassword:
                //dat mat khau
                break;
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putParcelableArrayListExtra("al_images", al_images);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == REQUEST_CODE_ALBUM){
                ArrayList<String> del = data.getExtras().getStringArrayList("delList");
                if(!del.isEmpty()) {
                    al_images.get(int_position).al_imagepath.removeAll(del);
                    if(al_images.get(int_position).al_imagepath.isEmpty()){
                        al_images.remove(int_position);
                    }
                }

                ArrayList<String> add = data.getExtras().getStringArrayList("addList");
                if(add.isEmpty()) return;
                for(String str: add){
                    for(Model_images model: al_images){
                        if(str.contains(model.str_folder)){
                            model.al_imagepath.add(str);
                            break;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}