package com.example.gallery_noob;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
    ArrayList<Folder> folders = new ArrayList<>();
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
        folders = ThirdFragment.loadFolderList(getApplicationContext());
        if(al_images.get(int_position).checkIfUserCreateThis(folders)) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.photos_activity_menu, menu);
        }
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
                setPassword();
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
                if(!add.isEmpty()) {
                    for (String str : add) {
                        for (Model_images model : al_images) {
                            if (str.contains(model.str_folder)) {
                                model.al_imagepath.add(str);
                                break;
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setPassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.password_type, viewGroup, false);

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        EditText password = dialogView.findViewById(R.id.password);
        Button show = dialogView.findViewById(R.id.show);
        Button remove = dialogView.findViewById(R.id.remove);
        Button save = dialogView.findViewById(R.id.save);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getInputType() == 129) {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD); // reveal password in plainText
                } else if (password.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    password.setInputType(129); // change back to password field
                }
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = al_images.get(int_position).str_folder;
                for(Folder folder: folders){
                    if(folder.getFolderName().equals(str)){
                        folder.setFolderPass(null);
                    }
                }
                ThirdFragment.saveFolderList(getApplicationContext(),folders);
                alertDialog.cancel();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = al_images.get(int_position).str_folder;
                String pass = password.getText().toString();
                for(Folder folder: folders){
                    if(folder.getFolderName().equals(str)){
                        folder.setFolderPass(pass);
                    }
                }
                ThirdFragment.saveFolderList(getApplicationContext(),folders);
                alertDialog.cancel();
            }
        });

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setView(dialogView);
//        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}