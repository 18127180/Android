package com.example.gallery_noob;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public int[] imageArray={R.drawable.welcome,
            R.drawable._1gdgmgrrnl_sl1465_,R.drawable._41261279_2867390260165722_4511917266191176847_o,
            R.drawable.an07,R.drawable.cuongdh01,R.drawable._589863275695_367121_udemy_750x422,
            R.drawable.__xesjmqb59kkkdscargw_ra,R.drawable.download,R.drawable.download__1_,
            R.drawable.duonglt03,R.drawable.duonglt03,R.drawable.duonglt03,R.drawable.duonglt03,
            R.drawable.duonglt03,R.drawable.duonglt03,R.drawable.duonglt03,R.drawable.duonglt03
    };

    public ImageAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return imageArray.length;
    }

    @Override
    public Object getItem(int position) {
        return imageArray[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(imageArray[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(340,350));
        return imageView;
    }
}

