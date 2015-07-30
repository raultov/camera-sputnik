package com.ayoza.camera_sputnik.camerasputnik.storage.entities;

/**
 * This class extends from PointSputnik and will be used in REST operations
 *
 * Created by raul on 30/07/15.
 */
public class PointSputnikRest extends PointSputnik {

    private Boolean uploaded = false;

    public Boolean getUploaded() {
        return uploaded;
    }

    public void setUploaded(Boolean uploaded) {
        this.uploaded = uploaded;
    }
}
