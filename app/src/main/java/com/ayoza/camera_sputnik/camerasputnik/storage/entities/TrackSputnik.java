package com.ayoza.camera_sputnik.camerasputnik.storage.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by raultov on 9/05/15.
 * This class stores information about a set of points
 */
public class TrackSputnik implements Serializable {
    private static final long serialVersionUID = -668423424624244484L;

    private Long idTrackSputnik;
    private Date date;
    
    private List<PointSputnik> pointSputniks = null;

    public Long getIdTrackSputnik() {
        return idTrackSputnik;
    }

    public void setIdTrackSputnik(Long idTrackSputnik) {
        this.idTrackSputnik = idTrackSputnik;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<PointSputnik> getPointSputniks() {
        return pointSputniks;
    }

    public void setPointSputniks(List<PointSputnik> pointSputniks) {
        this.pointSputniks = pointSputniks;
    }
}
