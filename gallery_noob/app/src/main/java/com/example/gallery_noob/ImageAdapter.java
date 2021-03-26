package com.example.gallery_noob;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.net.URLConnection;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<String> images;
    protected PhotoListiner photoListiner;

    public ImageAdapter(Context context, List<String> images, PhotoListiner photoListiner) {
        this.context=context;
        this.images=images;
        this.photoListiner=photoListiner;
    }

    @Override
    public int getItemViewType(int position) {
        if (isImageFile(images.get(position)))
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
                String image=images.get(position);
                Glide.with(context).load(image).into(viewHolder1.image);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoListiner.onPhotoClick(image);
                    }
                });
                break;
            case 1:
                ViewHolder_video viewHolder2 = (ViewHolder_video) holder;
                String thumnail=images.get(position);
                Glide.with(context).load(thumnail).into(viewHolder2.thumbmail);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoListiner.onPhotoClick(thumnail);
                    }
                });
                viewHolder2.duration.setText(getVideoDuration(context,Uri.parse(images.get(position))));
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

        public ViewHolder_image(@NonNull View itemView)
        {
            super(itemView);
            image=itemView.findViewById(R.id.image);
        }
    }
    public class ViewHolder_video extends RecyclerView.ViewHolder{
        ImageView thumbmail;
        TextView duration;


        public ViewHolder_video(@NonNull View itemView)
        {
            super(itemView);
            thumbmail=itemView.findViewById(R.id.image);
            duration=itemView.findViewById(R.id.duration_video);
        }
    }

    public interface PhotoListiner{
        void onPhotoClick(String path);
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
        String positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }
}

