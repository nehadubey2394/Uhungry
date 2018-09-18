package com.uhungry.model;


import java.io.Serializable;

public class Discover implements Serializable{

    public String id,title,content,contentType,contentImage,disCName,disCId,newsDes,newsName;
    public boolean isChacked = false;
    public boolean isBtnChecked = false;

    public Discover(){

    }

    public Discover(Discover discover){
        id = discover.id;
        title = discover.title;
        content = discover.content;
        contentType = discover.contentType;
        contentImage = discover.contentImage;
        disCName = discover.disCName;
        disCId = discover.disCId;
        newsDes = discover.newsDes;
        newsName = discover.newsName;
        isChacked = discover.isChacked;
        isBtnChecked = discover.isBtnChecked;
    }
}
