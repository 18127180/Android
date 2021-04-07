package com.example.gallery_noob;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

public class imageFragment extends Fragment implements FragmentLifecycle{
    private PhotoView imageFrag;
    private String url;

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
    public void onPauseFragment() {

    }

    @Override
    public void onResumeFragment() {

    }
}
