package com.example.gallery_noob;

import androidx.annotation.Nullable;

public class image_Item {
    String path;
    private boolean checked=false;

    public image_Item(String path)
    {
        this.path=path;
    }
    public boolean isChecked() {
        return checked;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == this){
            return true;
        }
        if(!(obj instanceof image_Item)){
            return false;
        }
        image_Item o = (image_Item)obj;
        return o.getPath().equals(path);
    }
}

