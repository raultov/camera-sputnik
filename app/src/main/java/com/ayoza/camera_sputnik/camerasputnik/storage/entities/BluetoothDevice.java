package com.ayoza.camera_sputnik.camerasputnik.storage.entities;

import java.io.Serializable;

/**
 * Created by raul on 3/02/15.
 */
public class BluetoothDevice implements Serializable {
    
    public static final long serialVersionUID = -561615151681684L;
    
    private Long id;
    private String name;
    private String mac;

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
    
    @Override
    public String toString() {
        return name + " - " + mac;
    }
}
