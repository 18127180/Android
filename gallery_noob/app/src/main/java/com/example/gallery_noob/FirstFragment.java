package com.example.gallery_noob;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jcminarro.roundkornerlayout.RoundKornerLinearLayout;

import java.util.ArrayList;
import java.util.List;

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
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;
    List<String> images;
    private static final int MY_READ_PERMISION_CODE=101;

    int REQUEST_CODE_CAMERA=123;

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
            Toast.makeText(getActivity(),"Phóng to",Toast.LENGTH_SHORT).show();
        }
        if (id==R.id.action_setting_small)
        {
            Toast.makeText(getActivity(),"Thu nhỏ",Toast.LENGTH_SHORT).show();
        }
        if (id==R.id.action_setting_camera)
        {
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
            loadImages();
        }

        Button btn_select=rootView.findViewById(R.id.button_select);
        Button btn_remove=rootView.findViewById(R.id.button_remove);
        RoundKornerLinearLayout layout_date=(RoundKornerLinearLayout) rootView.findViewById(R.id.ign_layout);
        LinearLayout layout_select=(LinearLayout) rootView.findViewById(R.id.layout_select);
        layout_select.setVisibility(View.GONE);
        //BottomNavigationView bottomNavigationView =( BottomNavigationView) mainView.findViewById(R.id.bottomNavigationView);

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_select.setVisibility(View.GONE);
                layout_date.setVisibility(View.GONE);
                layout_select.setVisibility(View.VISIBLE);
                //Intent intent=new Intent(getActivity(),MainActivity.class);
                //intent.putExtra("key","allow_select");
                //startActivity(intent);
                //bottomNavigationView.setVisibility(View.GONE);
            }
        });

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_select.setVisibility(View.VISIBLE);
                layout_date.setVisibility(View.VISIBLE);
                layout_select.setVisibility(View.GONE);
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
    public void loadImages(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        images=imagesGallery.listOfImages(getContext());
        imageAdapter=new ImageAdapter(getContext(),images,new ImageAdapter.PhotoListiner(){
            @Override
            public void onPhotoClick(String path) {
                Intent intent= new Intent(getActivity().getApplicationContext(),FullScreenImage.class);
                intent.putExtra("path", path);
                intent.putStringArrayListExtra("listOfImages",(ArrayList<String>)images);
                startActivity(intent);
            }
        });
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
    }
}