package com.example.gallery_noob;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by deepshikha on 3/3/17.
 */

public class Model_images implements Parcelable {
    String str_folder;
    ArrayList<String> al_imagepath;

    public Model_images(){
        str_folder="";
        al_imagepath=new ArrayList<>();
    }

    public Model_images(String name){
        str_folder = name;
        al_imagepath=new ArrayList<>();
    }

    protected Model_images(Parcel in) {
        str_folder = in.readString();
        al_imagepath = in.createStringArrayList();
    }

    public static final Creator<Model_images> CREATOR = new Creator<Model_images>() {
        @Override
        public Model_images createFromParcel(Parcel in) {
            return new Model_images(in);
        }

        @Override
        public Model_images[] newArray(int size) {
            return new Model_images[size];
        }
    };

    public String getStr_folder() {
        return str_folder;
    }

    public void setStr_folder(String str_folder) {
        this.str_folder = str_folder;
    }

    public ArrayList<String> getAl_imagepath() {
        return al_imagepath;
    }

    public void setAl_imagepath(ArrayList<String> al_imagepath) {
        this.al_imagepath = al_imagepath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(str_folder);
        dest.writeStringList(al_imagepath);
    }
}
