package com.ayoza.camera_sputnik.camerasputnik.storage.entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by raultov on 9/05/15.
 * This class stores information about a GPS trackpoint
 */
public class PointSputnik implements Serializable {

    private static final long serialVersionUID = -676868168433300000L;

    private Long idPointSputnik;
    private Double latitude;
    private Double longitude;
    private Date date;
    private ImageSputnik imageSputnik;

    public Long getIdPointSputnik() {
        return idPointSputnik;
    }

    public void setIdPointSputnik(Long idPointSputnik) {
        this.idPointSputnik = idPointSputnik;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ImageSputnik getImageSputnik() {
        return imageSputnik;
    }

    public void setImageSputnik(ImageSputnik imageSputnik) {
        this.imageSputnik = imageSputnik;
    }

}
