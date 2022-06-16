package sg.com.fuzzie.android.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import timber.log.Timber;

import sg.com.fuzzie.android.utils.FuzzieData_;

import static sg.com.fuzzie.android.utils.Constants.BROADCAST_LOCATION_UPDATED_FIRST;

/**
 * Created by mac on 7/11/17.
 */

public class LocationService extends Service implements LocationListener{

    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long  MIN_TIME_BW_UPDATES             = 1000 * 60;        // 1 minute

    private static LocationService locationService = null;
    private LocationManager locationManager;

    public static LocationService getInstance() {
        if (locationService == null) {
            locationService = new LocationService();
        }

        return locationService;
    }

    @SuppressWarnings("MissingPermission")
    public void startLocationService(Context context) {

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean isGPSEnabled     = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGPSEnabled || isNetworkEnabled) {
            Location location = null;

            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, LocationService.this);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    Timber.e( "GPS_PROVIDER");
                    FuzzieData_.getInstance_(context).myLocation = location;
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_LOCATION_UPDATED_FIRST));
                }
            }

            if (isNetworkEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, LocationService.this);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        Timber.e("NETWORK_PROVIDER");
                        FuzzieData_.getInstance_(context).myLocation = location;
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_LOCATION_UPDATED_FIRST));
                    }
                }
            }
        }

    }

    public void stopLocationService() {
        if (locationManager != null) {
            locationManager.removeUpdates(LocationService.this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Timber.e(location.getLatitude() + " : " + location.getLongitude());
        if (FuzzieData_.getInstance_(this).myLocation == null){
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(BROADCAST_LOCATION_UPDATED_FIRST));
        }
        FuzzieData_.getInstance_(this).myLocation = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
        Timber.e("Location Provider Enabled - " + s);
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
