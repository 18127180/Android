package com.example.gallery_noob;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import java.net.URLConnection;
import java.util.ArrayList;

public class MyFragmentAdapter extends FragmentPagerAdapter {
    ArrayList <Fragment> path;
    private Fragment mCurrentPrimaryItem = null;
    private long baseId = 0;

    public MyFragmentAdapter(@NonNull FragmentManager fm, ArrayList<Fragment> path) {
        super(fm);
        this.path=path;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return path.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        if (path.contains(object)) return path.indexOf(object);
        else return POSITION_NONE;
    }

    public boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    @Override
    public int getCount() {
        return path.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (position < getCount()) {
            Fragment fragment =(Fragment)object;
            FragmentManager manager = fragment.getFragmentManager();
            FragmentTransaction trans = manager.beginTransaction();
            trans.remove(fragment);
            trans.commit();
        }
    }

    @Override
    public long getItemId(int position) {
        return baseId + position;
    }

    public void notifyChangeInPosition(int n) {
        // shift the ID returned by getItemId outside the range of all previous fragments
        baseId += getCount() + n;
    }

//    @NonNull
//    @Override
//    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        Fragment fragmentAdapter=(Fragment) super.instantiateItem(container, position);
//        path.set(position, fragmentAdapter);
//        return fragmentAdapter;
//    }
}
