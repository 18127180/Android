package com.example.gallery_noob;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;
    private VideoView videoView;
    MediaController mediaController;
    View layout;
    // Array of images
    String[] images;
    public ViewPagerAdapter(Context context, String[] images)
    {
        this.context=context;
        this.images=images;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        if (isImageFile(images[position]))
        {
            layout = inflater.inflate(R.layout.activity_full_screen_image, container, false);
            PhotoView imageView=new PhotoView(layout.getContext());
            //ImageView imageView=new ImageView(context);
            Glide.with(layout)
                    .load(new File(images[position]))
                    .into(imageView);
            ((ViewPager)container).addView(imageView);
            return imageView;
        }
        else {
            layout = inflater.inflate(R.layout.full_screen_video, container, false);
            videoView = (VideoView) layout.findViewById(R.id.videoView);
            videoView.setVideoURI(Uri.parse(images[position]));
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoView.start();
                    mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                            if (mediaController == null) {
                                mediaController = new MediaController(layout.getContext());
                                videoView.setMediaController(mediaController);
                                mediaController.setAnchorView(videoView);
                            }
                        }
                    });
                }
            });
            container.addView(videoView);
            return layout;
        }
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
