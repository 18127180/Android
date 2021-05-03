package com.example.gallery_noob;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    ImageAdapter imageAdapter;
//    ArrayList<String> images;
//    RecyclerView recyclerView;
    private static final int REQUEST_FROM_FAVOURITE = 744;

    private static final String TAG = "StaggeredGridActivity";
    public static final String SAVED_DATA_KEY = "SAVED_DATA";

    private StaggeredGridView mGridView;
    private boolean mHasRequestedMore;
    private SampleAdapter mAdapter;

    private ArrayList<String> mData;
//    public ArrayList<String> all_medias;
    private ArrayList<String> multiSelected;

    SampleAdapter.StaggeredListener staggeredListener=new SampleAdapter.StaggeredListener(){
        @Override
        public void onClick(String path) {
            if(SampleAdapter.selected){
                if(multiSelected.contains(path)){
                    multiSelected.remove(path);
                }
                else    multiSelected.add(path);
                Toast.makeText(getContext(),multiSelected.size()+" items selected",Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent(getActivity(), FullScreenImage.class);
                intent.putExtra("path", path);
                intent.putStringArrayListExtra("listOfImages", mData);
                intent.putExtra("req_from", 2);
                startActivityForResult(intent, REQUEST_FROM_FAVOURITE);
            }
        }

        @Override
        public void onLongClick(String path) {
            Toast.makeText(getContext(),"Multi select mode",Toast.LENGTH_SHORT).show();
            SampleAdapter.selected = true;
            onPrepareOptionsMenu(menu);
            if(multiSelected == null)   multiSelected = new ArrayList<>();
            multiSelected.clear();
            multiSelected.add(path);
        }
    };

    public void cancelMultipleSelect(){
        SampleAdapter.selected = false;
        if(multiSelected != null)   multiSelected.clear();
        mAdapter.onCancelMultipleSelect();
        onPrepareOptionsMenu(menu);
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelMultipleSelect();
    }

    public SecondFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondFragment newInstance(String param1, String param2) {
        SecondFragment fragment = new SecondFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_second, container, false);

        mGridView = (StaggeredGridView) rootView.findViewById(R.id.grid_view);
//        all_medias = imagesGallery.listOfImages(getContext());
        // do we have saved data?
        if (savedInstanceState != null) {
            mData = savedInstanceState.getStringArrayList(SAVED_DATA_KEY);
        }

        if (mData == null) {
            mData = generateData();
        }

        if(mData != null) {
            mAdapter = new SampleAdapter(getContext(), android.R.layout.simple_list_item_1, mData, staggeredListener);
            mGridView.setAdapter(mAdapter);

//            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Intent intent = new Intent(getActivity(), FullScreenImage.class);
//                    intent.putExtra("path", mData.get(position));
//                    intent.putStringArrayListExtra("listOfImages", mData);
//                    intent.putExtra("req_from", 2);
//                    startActivityForResult(intent, REQUEST_FROM_FAVOURITE);
//                }
//            });

        }else{
            Toast.makeText(getActivity(),"No favourites found",Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    public void onClick(int position){
        Intent intent = new Intent(getActivity(), FullScreenImage.class);
        intent.putExtra("path", mData.get(position));
        intent.putStringArrayListExtra("listOfImages", mData);
        intent.putExtra("req_from", 2);
        startActivityForResult(intent, REQUEST_FROM_FAVOURITE);
    }

    private void onLoadMoreItems() {
        final ArrayList<String> sampleData = generateData();
        for (String data : sampleData) {
            mAdapter.add(data);
        }
        // stash all the data in our backing store
        mData.addAll(sampleData);
        // notify the adapter that we can update now
        mAdapter.notifyDataSetChanged();
        mHasRequestedMore = false;
    }

    private ArrayList<String> generateData() {
        ArrayList<String> temp = new ArrayList<>();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("favourite", "");
        temp = gson.fromJson(json, new TypeToken<List<String>>(){}.getType());
        return temp;
    }

    Menu menu;
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.create_favor_menu, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(SampleAdapter.selected){
            menu.findItem(R.id.deleteInFav).setVisible(true);
            menu.findItem(R.id.cancelInFav).setVisible(true);
            menu.findItem(R.id.shareInFav).setVisible(true);
        }else{
            menu.findItem(R.id.deleteInFav).setVisible(false);
            menu.findItem(R.id.cancelInFav).setVisible(false);
            menu.findItem(R.id.shareInFav).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.shareInFav:
                try {
                    onSend();
                    cancelMultipleSelect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.deleteInFav:
                try {
                    onDel();
                    cancelMultipleSelect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.cancelInFav:
                cancelMultipleSelect();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_FROM_FAVOURITE){
                ArrayList<String> del = data.getExtras().getStringArrayList("delList");
                if(del.isEmpty()) return;
                for(String i:del){
                    mAdapter.remove(i);
                }

                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onSend() throws Exception {
        Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
        share.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
        share.setType("image/*");

        ArrayList<Uri> files = new ArrayList<Uri>();
        for(String path: multiSelected) {
            Uri uri = FileProvider.getUriForFile(getContext(),"com.example.gallery_noob",new File(path));
            files.add(uri);
        }

//        String path = MediaStore.Images.Media.insertImage(getContentResolver(),position,"Temp",null);
//        uri = Uri.parse(path);
//        uri = FileProvider.getUriForFile(this,"com.example.gallery_noob",new File(position));
//        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        startActivity(share);
    }

    public void onDel() throws IOException {
        for(String position: multiSelected) {
            File f = new File(position);
            if (f.exists()) {
//            if (favList != null && favList.contains(position)) {       //Neu xoa co trong danh sach favourite thi xoa luon
//                favList.remove(position);
//                saveFavouriteList();
//            }
                f.delete();
                ContentResolver contentResolver = getContext().getContentResolver();
                contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Images.ImageColumns.DATA + "=?", new String[]{position});
            }
            if (position != null) {
                mData.remove(position);
                mAdapter.notifyDataSetChanged();

                FullScreenImage.saveFavouriteList(getContext(),mData);
            }
        }
    }
}