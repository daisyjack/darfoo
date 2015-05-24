package com.darfoo.mvplayer.utils;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by YuXiaofei on 2014/11/29.
 */
public class FileDetail implements Serializable {
    private int id;
    private String url;
    private String title;
    private String imageUrl;
    private int updateTimeStamp;
    private String author;
    private int type;
    private long duration;


    public FileDetail(int id, String url, String title, String imageUrl, int updateTimeStamp, String author, int type){
        this.id = id;
        this.url = url;
        this.title = title;
        this.imageUrl = imageUrl;
        this.updateTimeStamp = updateTimeStamp;
        this.author = author;
        this.type = type;

    }

    public FileDetail(int id, String url, String title, String imageUrl, int updateTimeStamp, String author, long duration, int type){
        this.id = id;
        this.url = url;
        this.title = title;
        this.imageUrl = imageUrl;
        this.updateTimeStamp = updateTimeStamp;
        this.author = author;
        this.duration = duration;
        this.type = type;

    }

    public int getId(){
        return id;
    }

    public String getUrl(){
        return url;
    }

    public String getTitle(){
        return title;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public int getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public String getAuthor() {
        return author;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUpdateTimeStamp(int updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
