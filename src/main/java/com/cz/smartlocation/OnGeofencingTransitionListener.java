package com.cz.smartlocation;

import com.cz.smartlocation.geofencing.utils.TransitionGeofence;

/**
 * Created by mrm on 4/1/15.
 */
public interface OnGeofencingTransitionListener {
    void onGeofenceTransition(TransitionGeofence transitionGeofence);
}