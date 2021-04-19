package com.example.gallery_noob;

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
}

