package com.cz.smartlocation.activity;

import android.content.Context;

import com.cz.smartlocation.OnActivityUpdatedListener;
import com.cz.smartlocation.activity.config.ActivityParams;
import com.cz.smartlocation.utils.Logger;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by mrm on 3/1/15.
 */
public interface ActivityProvider {
    void init(Context context, Logger logger);

    void start(OnActivityUpdatedListener listener, ActivityParams params);

    void stop();

    DetectedActivity getLastActivity();
}
