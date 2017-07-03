package com.cz.smartlocation.util;

import android.content.Context;
import android.location.Location;

import com.cz.smartlocation.OnLocationUpdatedListener;
import com.cz.smartlocation.location.LocationProvider;
import com.cz.smartlocation.location.config.LocationParams;
import com.cz.smartlocation.utils.Logger;

/**
 * Created by mrm on 8/1/15.
 */
public class MockLocationProvider implements LocationProvider {

    private OnLocationUpdatedListener listener;

    @Override
    public void init(Context context, Logger logger) {

    }

    @Override
    public void start(OnLocationUpdatedListener listener, LocationParams params, boolean singleUpdate) {
        this.listener = listener;
    }

    @Override
    public void stop() {

    }

    @Override
    public Location getLastLocation() {
        return null;
    }


    public void fakeEmitLocation(Location location) {
        listener.onLocationUpdated(location);
    }
}
