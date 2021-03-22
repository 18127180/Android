package com.example.gallery_noob;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;

    // Array of images
    String[] images;
    public ViewPagerAdapter(Context context, String[] images)
    {
        this.context=context;
        this.images=images;
    }
    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PhotoView imageView=new PhotoView(context);
        //ImageView imageView=new ImageView(context);
        Glide.with(context)
                .load(new File(images[position]))
                .into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
