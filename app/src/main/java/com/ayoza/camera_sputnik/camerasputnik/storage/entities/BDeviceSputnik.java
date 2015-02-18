package com.ayoza.camera_sputnik.camerasputnik.storage.entities;

import java.io.Serializable;

/**
 * Created by raul on 3/02/15.
 */
public class BDeviceSputnik implements Serializable, Cloneable {
    
    public static final long serialVersionUID = -561615151681684L;
    
    private Long id;
    private String name;
    private String mac;
    private Boolean paired;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Boolean getPaired() {
        return paired;
    }

    public void setPaired(Boolean paired) {
        this.paired = paired;
    }
    
    @Override
    public BDeviceSputnik clone() {
        try {
            return (BDeviceSputnik) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        BDeviceSputnik objDeviceSputnik = (BDeviceSputnik) obj;
        
        if (id == null || objDeviceSputnik.id == null) {
            return false;
        }
        
        return objDeviceSputnik.getId() == id;
    }
        

    @Override
    public String toString() {
        return name + " - " + mac;
    }
}
