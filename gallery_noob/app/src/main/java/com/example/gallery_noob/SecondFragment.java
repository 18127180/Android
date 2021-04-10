package com.example.gallery_noob;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    public ArrayList<String> all_medias;

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
            mAdapter = new SampleAdapter(getContext(), android.R.layout.simple_list_item_1, mData);
            mGridView.setAdapter(mAdapter);
            //        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            //            @Override
            //            public void onScrollStateChanged(AbsListView view, int scrollState) {
            //                Log.d(TAG, "onScrollStateChanged:" + scrollState);
            //            }
            //
            //            @Override
            //            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            //                Log.d(TAG, "onScroll firstVisibleItem:" + firstVisibleItem +
            //                        " visibleItemCount:" + visibleItemCount +
            //                        " totalItemCount:" + totalItemCount);
            //                // our handling
            //                if (!mHasRequestedMore) {
            //                    int lastInScreen = firstVisibleItem + visibleItemCount;
            //                    if (lastInScreen >= totalItemCount) {
            //                        Log.d(TAG, "onScroll lastInScreen - so load more");
            //                        mHasRequestedMore = true;
            //                        onLoadMoreItems();
            //                    }
            //                }
            //            }
            //        });
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), FullScreenImage.class);
                    intent.putExtra("path", mData.get(position));
                    intent.putStringArrayListExtra("listOfImages", mData);
                    intent.putExtra("req_from", 2);
                    startActivityForResult(intent, REQUEST_FROM_FAVOURITE);
                }
            });
        }else{
            Toast.makeText(getActivity(),"No favourites found",Toast.LENGTH_LONG).show();
        }

        return rootView;
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.create_favor_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        if (id==R.id.action_setting_create_favorite)
        {
            Toast.makeText(getActivity(),"Thêm mục yêu thích",Toast.LENGTH_SHORT).show();
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
}