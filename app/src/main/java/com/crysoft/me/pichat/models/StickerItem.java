package com.crysoft.me.pichat.models;

import java.io.Serializable;

/**
 * Created by Maxx on 6/15/2016.
 */
public class StickerItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id, image, extension, category, time;

    private boolean isOnSd;

    public boolean isOnSd() {
        return isOnSd;
    }

    public void setIsOnSd(boolean isOnSd) {
        this.isOnSd = isOnSd;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
