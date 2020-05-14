package com.example.googlelogin;

public class Upload {

    private String mName;
    private String mImageUrl;
    private String time;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String name, String imageUrl, String theTime) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mImageUrl = imageUrl;
        time = theTime;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;

    }

    public  String getTime() {return time;}
    public  String setTime() {return time;}


}
