package com.cz.smartlocation.location.providers;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.cz.smartlocation.OnLocationUpdatedListener;
import com.cz.smartlocation.location.LocationProvider;
import com.cz.smartlocation.location.config.LocationParams;
import com.cz.smartlocation.utils.GooglePlayServicesListener;
import com.cz.smartlocation.utils.Logger;
import com.google.android.gms.common.ConnectionResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A {@link LocationProvider} that allows multiple location services to be used. <br/><br/> New
 * instances of <code>MultiFallbackProvider</code> must be initialized via the Builder class:
 * <pre>
 * LocationProvider provider = new MultiLocationProvider.Builder()
 *         .withGooglePlayServicesProvider()
 *         .withDefaultProvider()
 *         .build();
 * </pre>
 * <code>MultiFallbackProvider</code> will attempt to use the location services in the order they
 * were added to the builder.  If the provider fails to connect to the underlying service, the next
 * provider in the list is used. <br/><br/> If no providers are added to the builder, the {@link
 * LocationManagerProvider} is used by default.
 *
 * @author abkaplan07
 */
public class MultiFallbackProvider implements LocationProvider {

    private Queue<LocationProvider> providers;
    private LocationProvider currentProvider;
    private Context context;
    private Logger logger;
    private OnLocationUpdatedListener locationListener;
    private LocationParams locationParams;
    private boolean singleUpdate;
    private boolean shouldStart;


    MultiFallbackProvider() {
        this.providers = new LinkedList<>();
    }

    @Override
    public void init(Context context, Logger logger) {
        this.context = context;
        this.logger = logger;
        LocationProvider current = getCurrentProvider();
        if (current != null) {
            current.init(context, logger);
        }

    }

    @Override
    public void start(OnLocationUpdatedListener listener, LocationParams params, boolean
            singleUpdate) {
        this.shouldStart = true;
        this.locationListener = listener;
        this.locationParams = params;
        this.singleUpdate = singleUpdate;
        LocationProvider current = getCurrentProvider();
        if (current != null) {
            current.start(listener, params, singleUpdate);
        }
    }

    @Override
    public void stop() {
        LocationProvider current = getCurrentProvider();
        if (current != null) {
            current.stop();
        }

    }

    @Override
    public Location getLastLocation() {
        LocationProvider current = getCurrentProvider();
        if (current == null) {
            return null;
        }
        return current.getLastLocation();
    }

    boolean addProvider(LocationProvider provider) {
        return providers.add(provider);
    }

    Collection<LocationProvider> getProviders() {
        return providers;
    }

    /**
     * Gets the current <code>LocationProvider</code> instance in use.
     *
     * @return the underlying <code>LocationProvider</code> used for location services.
     */
    LocationProvider getCurrentProvider() {
        if (currentProvider == null && !providers.isEmpty()) {
            currentProvider = providers.poll();
        }
        return currentProvider;
    }

    /**
     * Fetches the next location provider in the fallback list, and initializes it. If location
     * updates have already been started, this restarts location updates.<br/><br/>If there are no
     * location providers left, no action occurs.
     */
    void fallbackProvider() {
        if (!providers.isEmpty()) {
            currentProvider = providers.poll();
            currentProvider.init(context, logger);
            if (shouldStart) {
                currentProvider.start(locationListener, locationParams, singleUpdate);
            }
        }
    }

    /**
     * Builder class for the {@link MultiFallbackProvider}.
     */
    public static class Builder {

        private MultiFallbackProvider builtProvider;

        public Builder() {
            this.builtProvider = new MultiFallbackProvider();
        }

        /**
         * Adds Google Location Services as a provider.
         */
        public Builder withGooglePlayServicesProvider() {
            GooglePlayServicesListener googleListener = new GooglePlayServicesListener() {
                @Override
                public void onConnected(Bundle bundle) {
                    // noop
                }

                @Override
                public void onConnectionSuspended(int i) {
                    runFallback();
                }

                @Override
                public void onConnectionFailed(ConnectionResult connectionResult) {
                    runFallback();
                }

                private void runFallback() {
                    LocationProvider current = builtProvider.getCurrentProvider();
                    if (current != null && current instanceof LocationGooglePlayServicesProvider) {
                        builtProvider.fallbackProvider();
                    }
                }
            };
            LocationGooglePlayServicesProvider googleProvider = new
                    LocationGooglePlayServicesProvider(googleListener);
            return withProvider(googleProvider);
        }

        /**
         * Adds the built-in Android Location Manager as a provider.
         */
        public Builder withDefaultProvider() {
            return withProvider(new LocationManagerProvider());
        }

        /**
         * Adds the given {@link LocationProvider} as a provider.
         *
         * @param provider a <code>LocationProvider</code> instance.
         */
        public Builder withProvider(LocationProvider provider) {
            builtProvider.addProvider(provider);
            return this;
        }

        /**
         * Builds a {@link MultiFallbackProvider} instance. If no providers were added to the
         * builder, the built-in Android Location Manager is used.
         */
        public MultiFallbackProvider build() {
            // Always ensure we have the default provider
            if (builtProvider.providers.isEmpty()) {
                withDefaultProvider();
            }
            return builtProvider;
        }
    }
}
