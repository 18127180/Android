package com.example.gallery_noob;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.etsy.android.grid.util.DynamicHeightImageView;

import java.util.ArrayList;
import java.util.Random;
public class SampleAdapter extends ArrayAdapter<String> {

    private static final String TAG = "SampleAdapter";

    private final LayoutInflater mLayoutInflater;
    private final Random mRandom;
    private static ArrayList<String>al_images;
    private Context context;
    protected StaggeredListener staggeredListener;
    public static boolean selected = false;
    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();
    public ArrayList<CheckBox>vArr = new ArrayList<>();

    public void onCancelMultipleSelect(){
        if(vArr == null || vArr.isEmpty())  return;
        for(CheckBox v: vArr){
            v.setChecked(false);
            v.setVisibility(View.INVISIBLE);
        }
        vArr.clear();
    }

    public SampleAdapter(Context context, int textViewResourceId,
                         ArrayList<String> objects, StaggeredListener staggeredListener) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mRandom = new Random();
        this.staggeredListener = staggeredListener;
        al_images = objects;
    }

    @Override
    public void remove(@Nullable String object) {
        super.remove(object);
        al_images.remove(object);
    }

    @Override
    public View getView(final int position, View convertView,
                        final ViewGroup parent) {

        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.staggered_grid_view,
                    parent, false);
            vh = new ViewHolder();
            vh.imgView = (DynamicHeightImageView) convertView
                    .findViewById(R.id.imgView);
            vh.checkBox = (CheckBox) convertView.findViewById(R.id.check_video_1);
            vh.duration = (TextView) convertView.findViewById(R.id.duration_video_1);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        double positionHeight = getPositionRatio(position);

        vh.imgView.setHeightRatio(positionHeight);

        Glide.with(context).load(al_images.get(position)).into(vh.imgView);
        vh.checkBox.setVisibility(View.GONE);
        if(ImageAdapter.isImageFile(al_images.get(position))){
            vh.duration.setVisibility(View.GONE);
        }else{
            vh.duration.setText(ImageAdapter.getVideoDuration(context, Uri.parse(al_images.get(position))));
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected){
                    if(!vh.checkBox.isChecked()){
                        vh.checkBox.setVisibility(View.VISIBLE);
                        vh.checkBox.setChecked(true);
                        vArr.add(vh.checkBox);
                    }else{
                        vh.checkBox.setChecked(false);
                        vh.checkBox.setVisibility(View.INVISIBLE);
                        vArr.remove(vh.checkBox);
                    }
                }
                staggeredListener.onClick(al_images.get(position));
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!selected) {
                    staggeredListener.onLongClick(al_images.get(position));
                    vh.checkBox.setVisibility(View.VISIBLE);
                    vh.checkBox.setChecked(true);
                    vArr.add(vh.checkBox);
                }
                return true;
            }
        });

        return convertView;
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return al_images.size();
    }

    static class ViewHolder {
        DynamicHeightImageView imgView;
        TextView duration;
        CheckBox checkBox;
    }

    private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);
        // if not yet done generate and stash the columns height
        // in our real world scenario this will be determined by
        // some match based on the known height and width of the image
        // and maybe a helpful way to get the column height!
        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);
            Log.d(TAG, "getPositionRatio:" + position + " ratio:" + ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio() {
        return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5
        // the width
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    public interface StaggeredListener{
        public void onClick(String path);
        public void onLongClick(String path);
    }
}
