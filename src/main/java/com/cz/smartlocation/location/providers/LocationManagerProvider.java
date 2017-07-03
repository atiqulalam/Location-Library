package com.cz.smartlocation.location.providers;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import com.cz.smartlocation.OnLocationUpdatedListener;
import com.cz.smartlocation.location.LocationProvider;
import com.cz.smartlocation.location.LocationStore;
import com.cz.smartlocation.location.config.LocationAccuracy;
import com.cz.smartlocation.location.config.LocationParams;
import com.cz.smartlocation.utils.Logger;

/**
 * Created by nacho on 12/23/14.
 */
public class LocationManagerProvider implements LocationProvider, LocationListener {
    private static final String LOCATIONMANAGERPROVIDER_ID = "LMP";

    private LocationManager locationManager;
    private OnLocationUpdatedListener listener;
    private LocationStore locationStore;
    private Logger logger;

    @Override
    public void init(Context context, Logger logger) {
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        this.logger = logger;

        locationStore = new LocationStore(context);
    }

    @Override
    public void start(OnLocationUpdatedListener listener, LocationParams params, boolean singleUpdate) {
        this.listener = listener;
        if (listener == null) {
            logger.d("Listener is null, you sure about this?");
        }

        Criteria criteria = getProvider(params);
        try {
            if (singleUpdate) {
                locationManager.requestSingleUpdate(criteria, this, Looper.getMainLooper());
            } else {
                locationManager.requestLocationUpdates(params.getInterval(), params.getDistance(), criteria, this, Looper.getMainLooper());
            }
        }catch (SecurityException ex){

        }
    }

    @Override
    public void stop() {
        try {
            locationManager.removeUpdates(this);
        }catch (SecurityException ex){

        }
    }

    @Override
    public Location getLastLocation() {
        try {
            if (locationManager != null) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    return location;
                }
            }
        }catch (SecurityException ex){

        }

        Location location = locationStore.get(LOCATIONMANAGERPROVIDER_ID);
        if (location != null) {
            return location;
        }

        return null;
    }

    private Criteria getProvider(LocationParams params) {
        final LocationAccuracy accuracy = params.getAccuracy();
        final Criteria criteria = new Criteria();
        switch (accuracy) {
            case HIGH:
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setPowerRequirement(Criteria.POWER_HIGH);
                break;
            case MEDIUM:
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_MEDIUM);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_MEDIUM);
                criteria.setBearingAccuracy(Criteria.ACCURACY_MEDIUM);
                criteria.setSpeedAccuracy(Criteria.ACCURACY_MEDIUM);
                criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
                break;
            case LOW:
            case LOWEST:
            default:
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_LOW);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_LOW);
                criteria.setBearingAccuracy(Criteria.ACCURACY_LOW);
                criteria.setSpeedAccuracy(Criteria.ACCURACY_LOW);
                criteria.setPowerRequirement(Criteria.POWER_LOW);
        }
        return criteria;
    }

    @Override
    public void onLocationChanged(Location location) {
        logger.d("onLocationChanged", location);
        if (listener != null) {
            listener.onLocationUpdated(location);
        }
        if (locationStore != null) {
            logger.d("Stored in SharedPreferences");
            locationStore.put(LOCATIONMANAGERPROVIDER_ID, location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
