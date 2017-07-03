package com.cz.smartlocation.geofencing;

import android.content.Context;

import com.cz.smartlocation.OnGeofencingTransitionListener;
import com.cz.smartlocation.geofencing.model.GeofenceModel;
import com.cz.smartlocation.utils.Logger;

import java.util.List;

/**
 * Created by mrm on 20/12/14.
 */
public interface GeofencingProvider {
    void init(Context context, Logger logger);

    void start(OnGeofencingTransitionListener listener);

    void addGeofence(GeofenceModel geofence);

    void addGeofences(List<GeofenceModel> geofenceList);

    void removeGeofence(String geofenceId);

    void removeGeofences(List<String> geofenceIds);

    void stop();

}
