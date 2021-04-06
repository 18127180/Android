package com.example.gallery_noob;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

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

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
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
    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }
    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public int deletePath(String path){
        ArrayList<String> temp = new ArrayList<String>(Arrays.asList(images));
        int index = 0;
        if (temp.contains(path)) {
            index = temp.indexOf(path);
            if(index == temp.size()-1){
                index--;
            }
            temp.remove(path);
        }
        images = temp.toArray(new String[temp.size()]);
        notifyDataSetChanged();
        return index;
    }


}
