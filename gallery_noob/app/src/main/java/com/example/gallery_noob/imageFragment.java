package com.example.gallery_noob;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

public class imageFragment extends Fragment implements FragmentLifecycle{
    private PhotoView imageFrag;
    private String url;
    static boolean lastFavStatus = false;

    public imageFragment(String url){
        this.url=url;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.image_full_screen, container,false);
        imageFrag=view.findViewById(R.id.imageFull);
        Glide.with(view)
                .load(new File(url))
                .into(imageFrag);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible())
        {
            if (!isVisibleToUser)
            {
//                if(FullScreenImage.favList != null){
//                    Log.e("ERROR","Chay ! null");
//                    if(lastFavStatus && !FullScreenImage.favList.contains(url)){
//                        FullScreenImage.favList.add(url);
//                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor edit = sharedPreferences.edit();
//                        Gson gson = new Gson();
//                        String json = gson.toJson(FullScreenImage.favList);
//                        edit.putString("favourite", json);
//                        edit.commit();
//                    }else if(!lastFavStatus && FullScreenImage.favList.contains(url)){
//                        FullScreenImage.favList.remove(url);
//                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor edit = sharedPreferences.edit();
//                        Gson gson = new Gson();
//                        String json = gson.toJson(FullScreenImage.favList);
//                        edit.putString("favourite", json);
//                        edit.commit();
//                    }
//                    for(String i: FullScreenImage.favList){
//                        Log.e("favList",i);
//                    }
//                }else{
//                    Log.e("ERROR","Chay null");
//                    if(lastFavStatus) {
//                        FullScreenImage.favList = new ArrayList<>();
//                        FullScreenImage.favList.add(url);
//                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor edit = sharedPreferences.edit();
//                        Gson gson = new Gson();
//                        String json = gson.toJson(FullScreenImage.favList);
//                        edit.putString("favourite", json);
//                        edit.commit();
//                        for(String i: FullScreenImage.favList){
//                            Log.e("favList",i);
//                        }
//                    }
//                }
            }
        }
    }

    @Override
    public void onPauseFragment() {

    }

    @Override
    public void onResumeFragment() {
//        lastFavStatus = FullScreenImage.favStatus;
        if(FullScreenImage.favList != null){
            if(FullScreenImage.favList.contains(url)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    FullScreenImage.fav.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_baseline_favorite_24,0,0);
                    FullScreenImage.favStatus = true;
                }
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    FullScreenImage.fav.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_baseline_favorite,0,0);
                    FullScreenImage.favStatus = false;
                }
            }
        }
    }
}
