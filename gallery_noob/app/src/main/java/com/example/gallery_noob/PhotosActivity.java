package com.example.gallery_noob;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
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
        if(folders != null && al_images.get(int_position).checkIfUserCreateThis(folders)) {
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
            case R.id.deleteFolder:     //xoa thu muc
                deleteFolder();
                break;
            case R.id.renameTo:
                setFolderName();
                break;
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    protected void onPause() {
        super.onPause();
        ThirdFragment.saveFolderList(getApplicationContext(),folders);
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

    private void deleteFolder(){
        if(folders != null && al_images.get(int_position).checkIfUserCreateThis(folders)) {
            for(Folder folder: folders){
                if(folder.getFolderName().equals(al_images.get(int_position).str_folder)){
                    File mydir = null;
                    mydir = getApplicationContext().getDir(folder.getFolderName(), Context.MODE_PRIVATE);//Creating an internal dir;
                    if (mydir.exists())
                    {
                        deleteRecursive(mydir);
                        folders.remove(folder);
                        al_images.remove(int_position);
                        onBackPressed();
                        break;
                    }
                }
            }
        }
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    private void setFolderName() {

        AlertDialog.Builder rename = new AlertDialog.Builder(PhotosActivity.this);
        rename.setTitle(R.string.doiTenThanh);

        // Set up the input
        final EditText input = new EditText(PhotosActivity.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        rename.setView(input);

        // Set up the buttons
        rename.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();  //ten thu muc
                for (Model_images temp : al_images) {
                    if (name.equals(temp.str_folder)) {
                        Toast.makeText(getApplicationContext(), R.string.warning_thumucdaco, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                try {
                    getSupportActionBar().setTitle(name);
                    File srcdir = getApplicationContext().getDir(al_images.get(int_position).str_folder, Context.MODE_PRIVATE);//Creating an internal dir;
                    File dstdir = getApplicationContext().getDir(name, Context.MODE_PRIVATE);
                    if (srcdir.exists() && srcdir.isDirectory()) {
                        Folder temp = null;
                        for (Folder folder : folders) {
                            if (al_images.get(int_position).str_folder.equals(folder.getFolderName())) {
                                temp = folder;
                                break;
                            }
                        }

                        srcdir.renameTo(dstdir);
                        folders.add(new Folder(name, temp.getFolderPass()));
                        folders.remove(temp);
                        al_images.get(int_position).setStr_folder(name);

                    }
                }catch (Exception e){
                    Log.e("Error",e.toString());
                }
            }
        });
        rename.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        rename.show();
//        Log.i("Directory is", directory.toString());
//        Log.i("Default path is", videoURI.toString());
//        Log.i("From path is", from.toString());
//        Log.i("To path is", to.toString());
    }
}