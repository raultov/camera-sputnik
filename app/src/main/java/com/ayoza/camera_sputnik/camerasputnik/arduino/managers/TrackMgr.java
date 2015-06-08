package com.ayoza.camera_sputnik.camerasputnik.arduino.managers;

import android.app.Activity;

import com.ayoza.camera_sputnik.camerasputnik.exceptions.TrackException;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.ImageSputnik;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.PointSputnik;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.TrackSputnik;

import java.util.List;

/**
 * Created by raultov on 27/05/15.
 * This class handles tracks
 */
public class TrackMgr {
    
    private static volatile TrackMgr instance;
    private static final Object lock = new Object();
    private TrackSputnik currentTrack = null;
    private ConfigurationMgr configurationMgr = null;
    
    private TrackMgr(Activity activity) {
        configurationMgr = ConfigurationMgr.getInstance(activity);
    }
    
    public static TrackMgr getInstance(Activity activity) {
        TrackMgr r = instance;

        if(r == null) {
            synchronized(lock) {    // while we were waiting for the lock, another
                r = instance;       // thread may have instantiated the object
                if(r == null) {
                    r = new TrackMgr(activity);
                    instance = r;
                }
            }
        }
        
        return r;
        
/*        if (instance == null) {
            instance = new TrackMgr(activity);
        }
        
        return instance;
        */
    }
    
    public void startNewTrack() throws TrackException {
        if (currentTrack != null) {
            throw new TrackException(TrackException.ANOTHER_TRACK_IS_CURRENTLY_OPEN);
        }

        currentTrack = configurationMgr.createTrackSputnik();
    }
    
    public void addPointToCurrentTrack(Long imageId, Double latitude, Double longitude) throws TrackException {
        if (currentTrack == null) {
            throw new TrackException(TrackException.NO_CURRENT_TRACK_AVAILABLE);
        }

        PointSputnik pointSputnik = configurationMgr.createPointSputnik(currentTrack.getIdTrackSputnik(),
                                                                imageId, 
                                                                    latitude, longitude);
        
        currentTrack.addPointSputnik(pointSputnik);
    }

    public List<ImageSputnik> getImagesFromCurrentTrack() {

        currentTrack.getPointSputniks().get(0).get
        //configurationMgr.g

        return null;
    }
    
    public void closeCurrentTrack() {
        currentTrack = null;
    }
    
    public boolean isThereCurrentTrack() {
        return currentTrack != null;
    }
}
