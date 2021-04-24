package com.example.gallery_noob;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<image_Item> images;
    protected PhotoListiner photoListiner;
    private boolean multiCheckMode=false;

    public ImageAdapter(Context context, List<String> images, PhotoListiner photoListiner) {
        this.context=context;
//        this.images=images;
        this.images=new ArrayList<>();
        for (String image:images)
        {
            this.images.add(new image_Item(image));
        }
        this.photoListiner=photoListiner;
    }

    @Override
    public int getItemViewType(int position) {
        Log.e("Current path",images.get(position).getPath());
        if (isImageFile(images.get(position).getPath()))
            return 0;
        return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case 0:return new ViewHolder_image(
                    LayoutInflater.from(context).inflate(R.layout.gallery_item,parent,false));
        }
        return new ViewHolder_video(
                LayoutInflater.from(context).inflate(R.layout.gallery_item_video,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType())
        {
            case 0:
                ViewHolder_image viewHolder1 = (ViewHolder_image) holder;
                String image=images.get(position).getPath();
                Glide.with(context).load(image).into(viewHolder1.image);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoListiner.onPhotoClick(images.get(position));
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        photoListiner.onLongClick(images.get(position));
                        return false;
                    }
                });
                if (multiCheckMode)
                {
                    viewHolder1.checkBox.setVisibility(View.VISIBLE);
                    viewHolder1.checkBox.setChecked(images.get(position).isChecked());
                }else {
                    viewHolder1.checkBox.setVisibility(View.GONE);
                }
                break;
            case 1:
                ViewHolder_video viewHolder2 = (ViewHolder_video) holder;
                String thumnail=images.get(position).getPath();
                Glide.with(context).load(thumnail).into(viewHolder2.thumbmail);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoListiner.onPhotoClick(images.get(position));
                    }
                });
                viewHolder2.duration.setText(getVideoDuration(context,Uri.parse(images.get(position).getPath())));
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        photoListiner.onLongClick(images.get(position));
                        return false;
                    }
                });
                if (multiCheckMode)
                {
                    viewHolder2.checkBox.setVisibility(View.VISIBLE);
                    viewHolder2.checkBox.setChecked(images.get(position).isChecked());
                }else {
                    viewHolder2.checkBox.setVisibility(View.GONE);
                }
                break;
        }
    }

//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        switch (holder.getItemViewType())
//        {
//            case 0:
//                String image=images.get(position);
//                Glide.with(context).load(image).into(holder.image);
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        photoListiner.onPhotoClick(image);
//                    }
//                });
//            case 1:
//                ViewHolder_video viewHolder2 = (ViewHolder_video) holder;
//        }
//    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder_image extends RecyclerView.ViewHolder{
        ImageView image;
        CheckBox checkBox;
        public ViewHolder_image(@NonNull View itemView)
        {
            super(itemView);
            image=itemView.findViewById(R.id.image);
            checkBox=itemView.findViewById(R.id.check_image);
        }
    }
    public class ViewHolder_video extends RecyclerView.ViewHolder{
        ImageView thumbmail;
        TextView duration;
        CheckBox checkBox;


        public ViewHolder_video(@NonNull View itemView)
        {
            super(itemView);
            thumbmail=itemView.findViewById(R.id.image);
            duration=itemView.findViewById(R.id.duration_video);
            checkBox=itemView.findViewById(R.id.check_video);
        }
    }

    public interface PhotoListiner{
        void onPhotoClick(image_Item item);
        void onLongClick(image_Item item);
    }

    public void setPhotoListener(PhotoListiner Listener){
        this.photoListiner=Listener;
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public static String getVideoDuration(Context context, Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        Log.e("link",uri.toString());
        Log.e("duration",time);
        long duration = Long.parseLong(time)/1000;
        retriever.release();
        return formatDuration(duration);
    }

    public static String formatDuration(long duration) {
        long seconds = duration;
        long absSeconds = Math.abs(seconds);
        @SuppressLint("DefaultLocale") String positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }

    public void setMultiCheckMode(boolean multiCheckMode)
    {
        this.multiCheckMode=multiCheckMode;
        notifyDataSetChanged();
    }

    public void resetCheckMode()
    {
        for (image_Item item:images)
        {
            item.setChecked(false);
        }
        notifyDataSetChanged();
    }

    public ArrayList<image_Item> getCheckedNotes(){
        ArrayList<image_Item> checkedNotes = new ArrayList<>();
        for (image_Item n: this.images)
        {
            if (n.isChecked())
            {
                checkedNotes.add(n);
            }
        }
        return checkedNotes;
    }

    public void del_item(String path)
    {
        for (image_Item item: images)
        {
            if (item.getPath()==path)
            {
                images.remove(item);
                break;
            }
        }
    }

    public void removeAll(List<String> items_to_delete){
        ArrayList<image_Item>temp = new ArrayList<>();
        for(String tempString: items_to_delete){
            temp.add(new image_Item(tempString));
        }
        images.removeAll(temp);
        notifyDataSetChanged();
    }

    public void addAll(List<String> items_to_add){
        ArrayList<image_Item>temp = new ArrayList<>();
        for(String tempString: items_to_add){
            temp.add(new image_Item(tempString));
        }
        images.addAll(temp);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

