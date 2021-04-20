package com.example.gallery_noob;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.jcminarro.roundkornerlayout.RoundKornerLinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    static boolean visibility;
    static LinearLayout layout_select;
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;
    List<String> images;
    Button btn_select;
    RoundKornerLinearLayout layout_date;
    private ImageButton del_multi_btn;

    private static final int MY_READ_PERMISION_CODE=101;
    private static final int CAMERA_PERMISION_CODE=102;
    private static final int REQUEST_FROM_GALLERY=103;

    int REQUEST_CODE_CAMERA=123;
    static int gridSize = 3;

    public FirstFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstFragment newInstance(String param1, String param2) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    ImageAdapter.PhotoListiner default_mode_adapter=new ImageAdapter.PhotoListiner(){
        @Override
        public void onPhotoClick(image_Item item) {
            Intent intent= new Intent(getActivity().getApplicationContext(),FullScreenImage.class);
            intent.putExtra("path", item.getPath());
            intent.putStringArrayListExtra("listOfImages",(ArrayList<String>)images);
            intent.putExtra("req_from",1);
            startActivityForResult(intent,REQUEST_FROM_GALLERY);
        }

        @Override
        public void onLongClick(image_Item item) {
            item.setChecked(true);
            toggleBar();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.lib_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        if (id==R.id.action_setting_big)
        {
            //Toast.makeText(getActivity(),"Phóng to",Toast.LENGTH_SHORT).show();
            if(gridSize<5) {
                gridSize++;
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), gridSize));
            }
        }
        if (id==R.id.action_setting_small)
        {
            //Toast.makeText(getActivity(),"Thu nhỏ",Toast.LENGTH_SHORT).show();
            if(gridSize>3){
                gridSize--;
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),gridSize));
            }
        }
        if (id==R.id.action_setting_camera)
        {
//            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
//            {
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISION_CODE);
//            }
//            else
//            {
//                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, REQUEST_CODE_CAMERA);
//            }
            dispatchTakePictureIntent();
        }
        if (id==R.id.action_setting_slideshow)
        {
            Intent intent= new Intent(getActivity().getApplicationContext(),FullScreenImage.class);
            intent.putExtra("path", images.get(0));
            intent.putStringArrayListExtra("listOfImages",(ArrayList<String>)images);
            intent.putExtra("req_from",1);
            intent.putExtra("slideShow_MODE",true);
            startActivityForResult(intent,REQUEST_FROM_GALLERY);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //lay currentPhotoPath
        if(savedInstanceState != null & currentPhotoPath != null){
            currentPhotoPath = savedInstanceState.getString("current");
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_first, container, false);
        recyclerView=(RecyclerView)rootView.findViewById(R.id.recyclerview_gallery_images);
        //Permission
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_READ_PERMISION_CODE);
        }
        else
        {
            if(images==null){
                loadImages();
            }
        }

        del_multi_btn=rootView.findViewById(R.id.del_multi_button);
        btn_select=rootView.findViewById(R.id.button_select);
        Button btn_remove=rootView.findViewById(R.id.button_remove);
        layout_date=(RoundKornerLinearLayout) rootView.findViewById(R.id.ign_layout);
        layout_select=(LinearLayout) rootView.findViewById(R.id.layout_select);
        layout_select.setVisibility(View.GONE);
        visibility=false;


        del_multi_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_select.setVisibility(View.GONE);
                layout_date.setVisibility(View.GONE);
                toggleBar();
                //Intent intent=new Intent(getActivity(),MainActivity.class);
                //intent.putExtra("key","allow_select");
                //startActivity(intent);
                //bottomNavigationView.setVisibility(View.GONE);
            }
        });

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBar();
            }
        });

        /*gridView=(GridView) rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(new ImageAdapter(getActivity()));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(getActivity().getApplicationContext(),FullScreenImage.class);
                intent.putExtra("id", position);
                startActivity(intent);
            }
        });*/
        return rootView;
    }

    public void onDel(String position) throws FileNotFoundException {
        File f = new File(position);
        if(f.exists()){
//            if (favList != null && favList.contains(position)) {       //Neu xoa co trong danh sach favourite thi xoa luon
//                favList.remove(position);
//                saveFavouriteList();
//            }
            f.delete();
            ContentResolver contentResolver = getContext();
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

            Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
        }
    }

    public void toggleBar(){
        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(200);
        transition.addTarget(layout_select);
        TransitionManager.beginDelayedTransition((ViewGroup) layout_select.getParent(),transition);

        if(visibility){
            layout_select.setVisibility(View.GONE);
            imageAdapter.setMultiCheckMode(false);
            imageAdapter.setPhotoListener(default_mode_adapter);
            imageAdapter.resetCheckMode();
            btn_select.setVisibility(View.VISIBLE);
            layout_date.setVisibility(View.VISIBLE);
        }else{
            OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
                @Override
                public void handleOnBackPressed() {
                    imageAdapter.setMultiCheckMode(false);
                    imageAdapter.setPhotoListener(default_mode_adapter);
                    imageAdapter.resetCheckMode();

                    layout_select.setVisibility(View.GONE);
                    btn_select.setVisibility(View.VISIBLE);
                    layout_date.setVisibility(View.VISIBLE);
                    this.setEnabled(false);
                }
            };
            requireActivity().getOnBackPressedDispatcher().addCallback(callback);

            layout_select.setVisibility(View.VISIBLE);
            btn_select.setVisibility(View.GONE);
            layout_date.setVisibility(View.GONE);
            imageAdapter.setMultiCheckMode(true);
            imageAdapter.setPhotoListener(new ImageAdapter.PhotoListiner() {
                @Override
                public void onPhotoClick(image_Item item) {
                    item.setChecked(!item.isChecked());
                    imageAdapter.notifyDataSetChanged();
                }

                @Override
                public void onLongClick(image_Item item) {

                }
            });
        }
        visibility = !visibility;
    }

    public void loadImages(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), gridSize));
        images=imagesGallery.listOfImages(getContext());
        imageAdapter=new ImageAdapter(getContext(),images,default_mode_adapter);
        recyclerView.setAdapter(imageAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==MY_READ_PERMISION_CODE){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                loadImages();
            }
        }
        if (requestCode == CAMERA_PERMISION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                dispatchTakePictureIntent();
            }
            else
            {
                Toast.makeText(getActivity(), "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_CODE_CAMERA){
                galleryAddPic();
            }else if(requestCode == REQUEST_FROM_GALLERY){
                ArrayList<String> del = data.getExtras().getStringArrayList("delList");
                if(del.isEmpty()) return;
                images.removeAll(del);

                imageAdapter.notifyDataSetChanged();
            }
        }
    }

    //------------------------------------------------------------------------------
    //-------------------------Day code phan lay anh camera-------------------------
    //------------------------------------------------------------------------------

    private String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM+"/Camera/");
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        //        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("Error",ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.gallery_noob",
                        photoFile);
                //Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
        Uri contentUri = FileProvider.getUriForFile(getContext(),"com.example.gallery_noob",f);
        mediaScanIntent.setData(contentUri);
        getContext().sendBroadcast(mediaScanIntent);
        images.add(0,currentPhotoPath);
        imageAdapter.notifyDataSetChanged();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if(currentPhotoPath!=null) {
//            Toast.makeText(getContext(), currentPhotoPath, Toast.LENGTH_LONG).show();
//            //addImageToGallery(getActivity().getContentResolver(),new File(currentPhotoPath));
//            galleryAddPic();
//            currentPhotoPath = null;
//        }
//    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("current",currentPhotoPath);

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }
}