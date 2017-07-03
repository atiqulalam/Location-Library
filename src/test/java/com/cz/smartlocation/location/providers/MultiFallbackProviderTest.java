package com.cz.smartlocation.location.providers;

import android.content.Context;
import android.location.Location;

import com.cz.smartlocation.CustomTestRunner;
import com.cz.smartlocation.OnLocationUpdatedListener;
import com.cz.smartlocation.location.LocationProvider;
import com.cz.smartlocation.location.config.LocationParams;
import com.cz.smartlocation.utils.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.Iterator;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link MultiFallbackProvider}
 */
@RunWith(CustomTestRunner.class)
public class MultiFallbackProviderTest {

    @Test
    public void testDefaultBuilder() {
        MultiFallbackProvider subject = new MultiFallbackProvider.Builder().build();
        checkExpectedProviders(subject, LocationManagerProvider.class);
    }

    @Test
    public void testGoogleBuilder() {
        MultiFallbackProvider subject = new MultiFallbackProvider.Builder()
                .withGooglePlayServicesProvider().build();
        checkExpectedProviders(subject, LocationGooglePlayServicesProvider.class);
    }

    @Test
    public void testMultiProviderBuilder() {
        MultiFallbackProvider subject = new MultiFallbackProvider.Builder()
                .withGooglePlayServicesProvider().withDefaultProvider().build();
        checkExpectedProviders(subject, LocationGooglePlayServicesProvider.class,
                LocationManagerProvider.class);
    }

    @Test
    public void testMultiProviderRun() {
        LocationProvider providerMock = mock(LocationProvider.class);
        LocationProvider backupProvider = mock(LocationProvider.class);
        MultiFallbackProvider subject = new MultiFallbackProvider.Builder().withProvider
                (providerMock).withProvider(backupProvider).build();

        // Test initialization passes through to first provider
        subject.init(mock(Context.class), mock(Logger.class));
        verify(providerMock).init(any(Context.class), any(Logger.class));

        // Test starting location updates passes through to first provider
        OnLocationUpdatedListener listenerMock = mock(OnLocationUpdatedListener.class);
        LocationParams paramsMock = mock(LocationParams.class);
        subject.start(listenerMock, paramsMock, false);
        verify(providerMock).start(listenerMock, paramsMock, false);

        // Test that falling back initializes and starts the backup provider
        subject.fallbackProvider();
        verify(backupProvider).init(any(Context.class), any(Logger.class));
        verify(backupProvider).start(listenerMock, paramsMock, false);

        // Test that we're now using the fallback provider to stop.
        subject.stop();
        verify(backupProvider).stop();

        // Test that we're now using the fallback provider to get the last location
        Location mockLocation = mock(Location.class);
        when(backupProvider.getLastLocation()).thenReturn(mockLocation);
        assertEquals(mockLocation, subject.getLastLocation());
        verifyNoMoreInteractions(providerMock);
    }

    @SafeVarargs
    private final void checkExpectedProviders(MultiFallbackProvider subject, Class<? extends
            LocationProvider>... expectedProviders) {
        Collection<LocationProvider> providers = subject.getProviders();
        assertEquals(expectedProviders.length, providers.size());
        Iterator<LocationProvider> providerIt = providers.iterator();
        for (int i = 0; i < expectedProviders.length; i++) {
            Class<? extends LocationProvider> expected = expectedProviders[i];
            if (!providerIt.hasNext()) {
                fail("providers list did not have expected value " + expected.getName());
            }
            LocationProvider actual = providerIt.next();
            assertTrue("provider instance class " + actual.getClass().getName() + " does not " +
                    "match expected value " + expected.getName(), actual.getClass()
                    .isAssignableFrom(expected));

        }
    }

}
