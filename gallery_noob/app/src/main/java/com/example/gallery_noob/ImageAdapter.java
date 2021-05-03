package com.example.gallery_noob;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.gallery_noob.ThirdFragment.loadFolderList;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<String> images;
    private ArrayList<List> grid;
    private ArrayList<DateImageAdapter> list_adapter;
    protected PhotoListiner photoListiner;
    private boolean multiCheckMode=false;
    private int num_grid=4;
    ArrayList<String> dateArr;

    public void setNum_grid(int gridSize)
    {
        this.num_grid=gridSize;
    }

    public String convertDate(Date date_image) throws ParseException {
//        Date date=new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").parse(date_image);
        SimpleDateFormat spf= new SimpleDateFormat("dd/MM/yyyy");
        String newDate = spf.format(date_image);
        return newDate;
    }

    public ArrayList getListDate()
    {
        ArrayList<String> dateArr=new ArrayList<>();
        for (String item:images)
        {
            File file=new File(item);
            if(file.exists()) //Extra check, Just to validate the given path
            {
                try
                {
                    Date dateString = new Date(file.lastModified());
                    String temp=convertDate(dateString);
                    Log.e("PHOTO DATE", "Dated : "+ temp);
                    if (!dateArr.contains(temp))
                    {
                        dateArr.add(temp);
                    }
                }
                catch (ParseException e)
                {

                }
            }
        }
        return dateArr;
    }

    public void classify_grid() throws ParseException {
        dateArr=getListDate();
        if (dateArr!=null)
        {
            grid=new ArrayList<>();
            list_adapter=new ArrayList<>();
            int number_grid=dateArr.size();
            int index=0;
            for (int i=0;i<number_grid;i++)
            {
                List<String> temp=new ArrayList<String>();
                while (index<images.size())
                {
                    File file=new File(images.get(index));
                    Date date = new Date(file.lastModified());
                    String dateString=convertDate(date);
                    if (dateArr.get(i).equals(dateString))
                    {
                        temp.add(images.get(index));
                        index++;
                    }
                    else
                    {
                        break;
                    }
                }
                DateImageAdapter dateImageAdapter=new DateImageAdapter(context,temp,photoListiner);
                grid.add(temp);
                list_adapter.add(dateImageAdapter);
            }
        }
    }

    public void classify_grid_v2() throws ParseException, IOException {
        dateArr = new ArrayList<>();
        grid = new ArrayList<>();
        list_adapter = new ArrayList<>();
        String lastDateTimestamp = convertDate(new Date(new File(images.get(0)).lastModified()));
        int imagesSize = images.size();
        int count;
        String str = null;
        for (int i = 0 ; i < imagesSize ; i += count)
        {
            count = 0;
            dateArr.add(lastDateTimestamp);
            List<String>tempStr = new ArrayList<>();
            tempStr.add(images.get(i+count));
            count++;
            while(i+count<imagesSize && (str = convertDate(new Date(new File(images.get(i+count)).lastModified()))).equals(lastDateTimestamp)){
                tempStr.add(images.get(i+count));
                count++;
            }
            DateImageAdapter dateImageAdapter=new DateImageAdapter(context,tempStr,photoListiner);
            grid.add(tempStr);
            list_adapter.add(dateImageAdapter);

            lastDateTimestamp = str;
        }

        //doc folder cua nguoi dung tao
        ArrayList<Folder>folders = loadFolderList(context);
        if(folders != null && folders.size() > 0){
            for(Folder folder: folders){
                if(folder.getFolderPass()==null){
                    File dir = context.getDir(folder.getFolderName(), Context.MODE_PRIVATE);//Creating an internal dir;
                    File[] al_imagespath = dir.listFiles();
                    for(File f: al_imagespath){
                        str = convertDate(new Date(f.lastModified()));
                        if(dateArr.contains(str)){
                            DateImageAdapter dateImageAdapter = list_adapter.get(dateArr.indexOf(str));
                            images.add(images.indexOf(dateImageAdapter.lastPath())+1,f.getAbsolutePath());
                            dateImageAdapter.addPath(f.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }

    public ImageAdapter(Context context, List<String> images, PhotoListiner photoListiner) throws ParseException {
        this.context=context;
        this.images=images;
        this.photoListiner=photoListiner;
        try {
            classify_grid_v2();
        }catch(IOException e){
            Log.e("classify_grid_v2",e.toString());
        }
//        classify_grid();
//        getListDate();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder_image_parent(
                LayoutInflater.from(context).inflate(R.layout.item_date,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder_image_parent viewHolderImage=(ViewHolder_image_parent) holder;
        RecyclerView recyclerView=((ViewHolder_image_parent) holder).small_grid;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, num_grid));
        recyclerView.setAdapter(list_adapter.get(position));
        ((ViewHolder_image_parent) holder).date_time.setText(dateArr.get(position));
    }

    @Override
    public int getItemCount() {
        return grid.size();
    }

    public class ViewHolder_image_parent extends RecyclerView.ViewHolder{
        TextView date_time;
        RecyclerView small_grid;
        public ViewHolder_image_parent(@NonNull View itemView)
        {
            super(itemView);
            date_time=itemView.findViewById(R.id.date_classify);
            small_grid=itemView.findViewById(R.id.recyclerview_gallery_images_date);
        }
    }

    public interface PhotoListiner{
        void onPhotoClick(image_Item item);
        void onLongClick(image_Item item);
    }

    public void setPhotoListener(PhotoListiner Listener){
        this.photoListiner=Listener;
        for (DateImageAdapter item : list_adapter)
        {
            item.setPhotoListener(Listener);
        }
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
        for (DateImageAdapter item : list_adapter)
        {
            item.setMultiCheckMode(multiCheckMode);
            item.notifyDataSetChanged();
        }
        notifyDataSetChanged();
    }

    public void resetCheckMode()
    {
        for (DateImageAdapter item : list_adapter)
        {
            item.resetCheckMode();
            item.notifyDataSetChanged();
        }
        notifyDataSetChanged();
    }

    public ArrayList<image_Item> getCheckedNotes(){
        ArrayList<image_Item> checkedNotes=new ArrayList<>();
        for (DateImageAdapter item : list_adapter)
        {
            ArrayList<image_Item> temp=new ArrayList<>();
            temp=item.getCheckedNotes();
            for (image_Item check:temp)
            {
                checkedNotes.add(check);
            }
        }
        return checkedNotes;
    }

    public void delEmptyDateHeader()
    {
        ArrayList<Integer> indexArrOnDel=new ArrayList<>();
        for (int i=0;i<grid.size();i++)
        {
            if (list_adapter.get(i).getItemCount()==0)
            {
                indexArrOnDel.add(i);
            }
        }
        Log.e("SizeDel",""+indexArrOnDel.size());
        for (Integer index:indexArrOnDel)
        {
            int position=index;
            dateArr.remove(position);
            list_adapter.remove(position);
        }
        notifyDataSetChanged();
    }
    public void del_item(String path)
    {
        for (DateImageAdapter item : list_adapter)
        {
            item.del_item(path);
            item.notifyDataSetChanged();
        }
        delEmptyDateHeader();
    }

    public void removeAll(List<String> items_to_delete){
//        ArrayList<image_Item>temp = new ArrayList<>();
//        for(String tempString: items_to_delete){
//            temp.add(new image_Item(tempString));
//        }
//        images.removeAll(temp);
//        notifyDataSetChanged();
    }

    public void addAll(List<String> items_to_add){
//        ArrayList<image_Item>temp = new ArrayList<>();
//        for(String tempString: items_to_add){
//            temp.add(new image_Item(tempString));
//        }
//        images.addAll(temp);
//        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

