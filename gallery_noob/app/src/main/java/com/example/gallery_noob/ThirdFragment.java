package com.example.gallery_noob;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends Fragment {

    private static ArrayList<Model_images> al_images = new ArrayList<>();
    Adapter_PhotosFolder obj_adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    GridView gv_folder;
    ImageButton add_btn;

    private final static int REQUEST_FROM_IMAGE_PICKER = 1234;
    private String name = "";
    File mydir = null;
    static ArrayList<Folder>folders = null;

    public ThirdFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThirdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_third, container, false);
        gv_folder=(GridView) rootView.findViewById(R.id.gv_folder);
        folders = new ArrayList<>();
        fn_imagespath();

        add_btn=(ImageButton) rootView.findViewById(R.id.buttonAdd);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Name your album:");

                // Set up the input
                final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        name = input.getText().toString();  //ten thu muc
                        for(Model_images temp:al_images){
                            if(name.equals(temp.str_folder)){
                                Toast.makeText(getContext(),R.string.warning_thumucdaco,Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        mydir = getContext().getDir(name, Context.MODE_PRIVATE);//Creating an internal dir;
                        if (!mydir.exists())
                        {
                            mydir.mkdirs();
                        }

                        ImagePicker.ImagePickerWithFragment launch = ImagePicker.create(ThirdFragment.this);
                        launch.includeVideo(true);
                        launch.saveImage(true);
                        launch.showCamera(true);
                        launch.includeAnimation(true);
                        launch.imageFullDirectory(mydir.getAbsolutePath());
                        launch.folderMode(true);
                        launch.toolbarFolderTitle("Album");
                        launch.start(REQUEST_FROM_IMAGE_PICKER);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        return rootView;
    }

    public static void saveFolderList(Context context,ArrayList<Folder> folders){     //ham luu danh sach thu muc nguoi dung tao
        SharedPreferences sharedPreferences = context.getSharedPreferences("app", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(folders);
        edit.putString("folderList", json);
        edit.commit();
    }

    public static ArrayList<Folder> loadFolderList(Context context) {      //ham lay danh sach favourite
        SharedPreferences sharedPreferences = context.getSharedPreferences("app", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("folderList", "");
        ArrayList<Folder> temp = gson.fromJson(json, new TypeToken<List<Folder>>(){}.getType());
        return temp;
    }

    @Override
    public void onResume() {
        super.onResume();
        folders = loadFolderList(getContext());
        obj_adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK) {
            switch(requestCode) {
                case 1:{
                    Log.e("CB","SUCCESS");
                    //just need the callback from PhotosActivity
                    ArrayList<Model_images>temp = data.getParcelableArrayListExtra("al_images");
                    al_images.clear();
//                    al_images.addAll(temp);
                    for(Model_images t:temp){
                        al_images.add(t);
                    }
                    obj_adapter.notifyDataSetChanged();
                    break;
                }
                case REQUEST_FROM_IMAGE_PICKER:{
                    List<Image> temp = ImagePicker.getImages(data);
                    Model_images tempModel = new Model_images(name);
                    try {
                        for(Image img: temp){
//                            Log.e("FILE",img.getPath());
                            tempModel.al_imagepath.add(img.getPath());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Files.copy(new File(img.getPath()).toPath(),
                                        new File(mydir,img.getName()).toPath(),
                                        StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    al_images.add(tempModel);
                    obj_adapter.notifyDataSetChanged();

                    Folder tempFolder = new Folder(name,null);
                    if(folders == null) folders = new ArrayList<>();
                    folders.add(tempFolder);
                    saveFolderList(getContext(),folders);

                    Intent intent = new Intent(getContext(), PhotosActivity.class);
                    intent.putExtra("value", al_images.size()-1);
                    intent.putParcelableArrayListExtra("al_images", al_images);
                    startActivityForResult(intent, 1);
                    break;
                }
            }
        }
    }

    boolean boolean_folder;
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
            cursor = getContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
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
        folders = loadFolderList(getContext());
        if(folders != null && folders.size() > 0){
            for(Folder folder: folders){
                Model_images tempModel = new Model_images(folder.getFolderName());
                File dir = getContext().getDir(folder.getFolderName(), Context.MODE_PRIVATE);//Creating an internal dir;
                File[] al_imagespath = dir.listFiles();
                for(File f: al_imagespath){
                    tempModel.al_imagepath.add(f.getAbsolutePath());
                }
                al_images.add(tempModel);
            }
        }

        obj_adapter = new Adapter_PhotosFolder(getContext(),al_images);

        //gv_folder.setAdapter(new ImageAdapter(getActivity()));
        gv_folder.setAdapter(obj_adapter);
        gv_folder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(folders != null && al_images.get(position).checkIfUserCreateThis(folders)){
                    checkPassword(position);
                }else {
                    Intent intent = new Intent(getContext(), PhotosActivity.class);
                    intent.putExtra("value", position);
                    intent.putParcelableArrayListExtra("al_images", al_images);
                    startActivityForResult(intent, 1);
                }
            }
        });
        return al_images;
    }

    boolean correct = false;
    private void checkPassword(int position) {
        String folderPass = "";
        String str = al_images.get(position).str_folder;
        for(Folder folder: folders){
            if(folder.getFolderName().equals(str)){
                folderPass = folder.getFolderPass();
                if(folderPass == null){     //pass ko co
                    Intent intent = new Intent(getContext(), PhotosActivity.class);
                    intent.putExtra("value", position);
                    intent.putParcelableArrayListExtra("al_images", al_images);
                    startActivityForResult(intent, 1);
                    return;
                }
            }
        }
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.password_type, null, false);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        TextView title = dialogView.findViewById(R.id.textView2);
        EditText password = dialogView.findViewById(R.id.password);
        Button show = dialogView.findViewById(R.id.show);
        Button remove = dialogView.findViewById(R.id.remove);
        Button save = dialogView.findViewById(R.id.save);

        title.setText(R.string.nhapMatKhau);

        show.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);

        String finalFolderPass = folderPass;
        save.setText("OK");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = password.getText().toString();
                if(!pass.equals(finalFolderPass)){
                    Toast.makeText(getContext(),R.string.saiMatKhau,Toast.LENGTH_SHORT).show();
                    password.setText("");
                }else {
                    alertDialog.cancel();
                    Intent intent = new Intent(getContext(), PhotosActivity.class);
                    intent.putExtra("value", position);
                    intent.putParcelableArrayListExtra("al_images", al_images);
                    startActivityForResult(intent, 1);
                }
            }
        });

//        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
//        builder.setView(dialogView);
//        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}