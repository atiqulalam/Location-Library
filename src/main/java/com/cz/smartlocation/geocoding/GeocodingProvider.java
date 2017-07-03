package com.cz.smartlocation.geocoding;

import android.content.Context;
import android.location.Location;

import com.cz.smartlocation.OnGeocodingListener;
import com.cz.smartlocation.OnReverseGeocodingListener;
import com.cz.smartlocation.utils.Logger;

/**
 * Created by mrm on 20/12/14.
 */
public interface GeocodingProvider {
    void init(Context context, Logger logger);

    void addName(String name, int maxResults);

    void addLocation(Location location, int maxResults);

    void start(OnGeocodingListener geocodingListener, OnReverseGeocodingListener reverseGeocodingListener);

    void stop();

}
