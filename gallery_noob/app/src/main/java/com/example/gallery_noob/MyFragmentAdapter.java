package com.example.gallery_noob;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.net.URLConnection;
import java.util.ArrayList;

public class MyFragmentAdapter extends FragmentPagerAdapter {
    ArrayList <Fragment> path;
    public MyFragmentAdapter(@NonNull FragmentManager fm, ArrayList<Fragment> path) {
        super(fm);
        this.path=path;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return path.get(position);
    }

    public boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    @Override
    public int getCount() {
        return path.size();
    }
}
