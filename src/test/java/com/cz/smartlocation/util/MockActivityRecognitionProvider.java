package com.cz.smartlocation.util;

import android.content.Context;

import com.cz.smartlocation.OnActivityUpdatedListener;
import com.cz.smartlocation.activity.ActivityProvider;
import com.cz.smartlocation.activity.config.ActivityParams;
import com.cz.smartlocation.utils.Logger;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by nacho on 1/9/15.
 */
public class MockActivityRecognitionProvider implements ActivityProvider {

    private OnActivityUpdatedListener listener;

    @Override
    public void init(Context context, Logger logger) {

    }

    @Override
    public void start(OnActivityUpdatedListener listener, ActivityParams params) {
        this.listener = listener;
    }

    @Override
    public void stop() {

    }

    @Override
    public DetectedActivity getLastActivity() {
        return new DetectedActivity(DetectedActivity.UNKNOWN, 100);
    }

    public void fakeEmitActivity(DetectedActivity detectedActivity) {
        listener.onActivityUpdated(detectedActivity);
    }
}
