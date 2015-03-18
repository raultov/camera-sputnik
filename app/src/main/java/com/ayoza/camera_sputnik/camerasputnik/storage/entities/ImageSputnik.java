package com.ayoza.camera_sputnik.camerasputnik.storage.entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Entity to manage images
 *  
 * Created by raultov on 16/03/15.
 */
public class ImageSputnik implements Serializable {

    private static final long serialVersionUID = 654682943294423424L;

    private Long id;
    private String filename;
    private Date createDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public ImageSputnik clone() {
        try {
            return (ImageSputnik) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        ImageSputnik imageSputnik = (ImageSputnik) obj;

        if (id == null || imageSputnik.id == null) {
            return false;
        }

        return imageSputnik.id == id;
    }


    @Override
    public String toString() {
        return filename + " - " + createDate;
    }
}
