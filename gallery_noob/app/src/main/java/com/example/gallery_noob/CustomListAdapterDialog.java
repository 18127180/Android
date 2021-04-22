package com.example.gallery_noob;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CustomListAdapterDialog extends BaseAdapter {
    Context context;

    private ArrayList<Model_images> listData;

    private LayoutInflater layoutInflater;

    public CustomListAdapterDialog(Context context, ArrayList<Model_images> listData) {
        this.context = context;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_dialog, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.folder_image);
            holder.unitView = (TextView) convertView.findViewById(R.id.folder_name);
            holder.quantityView = (TextView) convertView.findViewById(R.id.folder_quantity);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(context).load("file://" + listData.get(position).getAl_imagepath().get(0)).into(holder.imageView);
        holder.unitView.setText(listData.get(position).getStr_folder());
        holder.quantityView.setText(String.valueOf(listData.get(position).getAl_imagepath().size()));

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView unitView;
        TextView quantityView;
    }

}
