package com.example.gallery_noob;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class Adapter_PhotosFolder extends ArrayAdapter<Model_images> {

    Context context;
    ViewHolder viewHolder;
    ArrayList<Model_images> al_menu = new ArrayList<>();

    public Adapter_PhotosFolder(Context context, ArrayList<Model_images> al_menu) {
        super(context, R.layout.adapter_photosfolder, al_menu);
        this.al_menu = al_menu;
        this.context = context;
    }

    @Override
    public int getCount() {
        Log.e("ADAPTER LIST SIZE", al_menu.size() + "");
        return al_menu.size();
    }

    @Override
    public int getItemViewType(int position) {
//        if(position>=getViewTypeCount())    return getViewTypeCount()-1;
//        return position;
        return 0;
    }

    @Override
    public int getViewTypeCount() {
//        if (al_menu.size() > 0) {
//            return al_menu.size();
//        } else {
//            return 1;
//        }
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Nullable
    @Override
    public Model_images getItem(int position) {
        return al_menu.get(position);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_photosfolder, parent, false);
            viewHolder.tv_foldern = (TextView) convertView.findViewById(R.id.tv_folder);
            viewHolder.tv_foldersize = (TextView) convertView.findViewById(R.id.tv_folder2);
            viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_foldern.setText(al_menu.get(position).getStr_folder());
        viewHolder.tv_foldersize.setText(al_menu.get(position).getAl_imagepath().size()+"");


        if(al_menu.get(position).getAl_imagepath().size()>0) {
            Glide.with(context).load("file://" + al_menu.get(position).getAl_imagepath().get(0))
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
                    .into(viewHolder.iv_image);
        }else{

        }

        return convertView;
    }

    private static class ViewHolder {
        TextView tv_foldern, tv_foldersize;
        ImageView iv_image;
    }
}
