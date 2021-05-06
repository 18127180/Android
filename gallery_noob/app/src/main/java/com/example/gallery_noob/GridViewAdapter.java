package com.example.gallery_noob;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter<Model_images> {

    Context context;
    ArrayList<Model_images> al_menu;
    int int_position;
    static boolean selected = false;
    public ArrayList<CheckBox>vArr = new ArrayList<>();
    protected AlbumListener albumListener;

    public void onCancelMultipleSelect(){
        if(vArr == null || vArr.isEmpty())  return;
        for(CheckBox v: vArr){
            v.setChecked(false);
            v.setVisibility(View.INVISIBLE);
        }
        vArr.clear();
    }

    public GridViewAdapter(Context context, ArrayList<Model_images> al_menu,int int_position, AlbumListener albumListener) {
        super(context, R.layout.none, al_menu);
        this.al_menu = al_menu;
        this.context = context;
        this.int_position = int_position;
        this.albumListener = albumListener;
    }

    @Override
    public int getCount() {
        Log.e("ADAPTER LIST SIZE", al_menu.get(int_position).getAl_imagepath().size() + "");
        return al_menu.get(int_position).getAl_imagepath().size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.gallery_item_video, parent, false);
            viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.duration = (TextView) convertView.findViewById(R.id.duration_video);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.check_video);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Glide.with(context).load("file://"+ al_menu.get(int_position).getAl_imagepath().get(position))
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
                .into(viewHolder.iv_image);
        viewHolder.checkBox.setVisibility(View.INVISIBLE);
        if(ImageAdapter.isImageFile(al_menu.get(int_position).getAl_imagepath().get(position))){
            viewHolder.duration.setVisibility(View.GONE);
        }else{
            viewHolder.duration.setText(ImageAdapter.getVideoDuration(context, Uri.parse(al_menu.get(int_position).getAl_imagepath().get(position))));
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected){
                    if(!viewHolder.checkBox.isChecked()){
                        viewHolder.checkBox.setVisibility(View.VISIBLE);
                        viewHolder.checkBox.setChecked(true);
                        vArr.add(viewHolder.checkBox);
                    }else{
                        viewHolder.checkBox.setChecked(false);
                        viewHolder.checkBox.setVisibility(View.INVISIBLE);
                        vArr.remove(viewHolder.checkBox);
                    }
                }
                albumListener.onClick(al_menu.get(int_position).getAl_imagepath().get(position));
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!selected) {
                    albumListener.onLongClick(al_menu.get(int_position).getAl_imagepath().get(position));
                    viewHolder.checkBox.setVisibility(View.VISIBLE);
                    viewHolder.checkBox.setChecked(true);
                    vArr.add(viewHolder.checkBox);
                }
                return true;
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView iv_image;
        TextView duration;
        CheckBox checkBox;
    }

    public interface AlbumListener{
        void onClick(String path);
        void onLongClick(String path);
    }
}
