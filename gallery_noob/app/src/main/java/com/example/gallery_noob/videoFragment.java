package com.example.gallery_noob;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class videoFragment extends Fragment implements FragmentLifecycle {
    private VideoView videoView;
    private String url;
    private View view;
    MediaController mediaController;
    private ImageView imageView;
    private ImageView playVideo;

    public videoFragment()
    {

    }

    public static videoFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString("url_video_fragment", url);
        videoFragment f = new videoFragment();
        f.setArguments(args);
        return f;
    }

    public videoFragment(String url){
        this.url=url;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        Log.e("tao view video","true");
//        view=inflater.inflate(R.layout.full_screen_video, container,false);
//
//        this.url=getArguments().getString("url_video_fragment");

//        videoView = (VideoView) view.findViewById(R.id.videoView);
//        videoView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FullScreenImage.toggleBar();
//            }
//        });
//        videoView.setVideoURI(Uri.parse(url));
//        videoView.requestFocus();
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
//                    @Override
//                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//                        if (mediaController == null) {
//                            mediaController = new MediaController(view.getContext());
//                            videoView.setMediaController(mediaController);
//                            mediaController.setAnchorView(videoView);
//                        }
//                    }
//                });
//            }
//        });
        view = inflater.inflate(R.layout.video_thumbnail, container, false);
        this.url = getArguments().getString("url_video_fragment");
        imageView = (ImageView)view.findViewById(R.id.videoThumb);
        playVideo = (ImageView)view.findViewById(R.id.playVideo);

        Glide.with(getContext()).load(url).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullScreenImage.toggleBar();
            }
        });

        playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),Fullscreen_video.class);
                i.putExtra("url",url);
                startActivityForResult(i,12345);
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("pause", "ok");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible())
        {
            if (!isVisibleToUser)
            {
//                videoView.pause();
//                videoView.seekTo(0);
//                mediaController=null;
//                videoView.setMediaController(mediaController);
            }
        }
    }

    @Override
    public void onPauseFragment() {

    }

    @Override
    public void onResumeFragment() {
        if (videoView!=null)
        {
//            videoView.start();
//            if (mediaController == null) {
//                mediaController = new MediaController(view.getContext());
//                videoView.setMediaController(mediaController);
//                mediaController.setAnchorView(videoView);
//            }
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

    @Override
    public void addImageTransition() {

    }
}
