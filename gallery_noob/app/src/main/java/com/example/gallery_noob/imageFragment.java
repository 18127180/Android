package com.example.gallery_noob;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
    private static final int FADE_IN_TIME = 1000;

    public imageFragment(){

    }

    public imageFragment(String url){
        this.url=url;
    }

    public static imageFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString("url_image_fragment", url);
        imageFragment f = new imageFragment();
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.image_full_screen, container,false);

        this.url=getArguments().getString("url_image_fragment");

        imageFrag=view.findViewById(R.id.imageFull);
        imageFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullScreenImage.toggleBar();
            }
        });
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
                //execute your data loading logic
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

    @Override
    public void addImageTransition() {
        // Transition drawable with a transparent drawable and the final bitmap
        @SuppressLint("ResourceAsColor") TransitionDrawable td = new TransitionDrawable(new Drawable[] {
                new ColorDrawable(R.color.imgly_transparent_color),
                imageFrag.getDrawable() });
        imageFrag.setImageDrawable(td);
        td.startTransition(FADE_IN_TIME);
    }
}
