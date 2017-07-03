package com.cz.smartlocation.location;

import android.content.Context;
import android.location.Location;

import com.cz.smartlocation.OnLocationUpdatedListener;
import com.cz.smartlocation.location.config.LocationParams;
import com.cz.smartlocation.utils.Logger;

/**
 * Created by mrm on 20/12/14.
 */
public interface LocationProvider {
    void init(Context context, Logger logger);

    void start(OnLocationUpdatedListener listener, LocationParams params, boolean singleUpdate);

    void stop();

    Location getLastLocation();

}
