package com.cs65.homie;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("ALL")
public class Utilities
{

    /**
     * Approximate radius of the earth in kilometers
     */
    public static final int EARTH_RADIUS = 6371;
    public static final int GET_CURRENT_LOC_SDK = 30;
    /**
     *  Miles to kilometers conversion
     */
    public static double MILES_TO_KILOMETERS = 1.609344;

    public static boolean checkPermissionLocation(final Activity activity)
    {
        return checkPermissions(
            activity,
            new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            },
            1
        );
    }

    /**
     * Generic function to check for permission(s)
     *
     * If the permissions are not granted, request it
     *
     * @param activity      Calling activity
     * @param permissions   List of permissions to check
     * @param sdk_min       Minimum SDK for the permissions
     * @return              Whether or not the app has the the given
     *                      permission(s)
     */
    private static boolean checkPermissions(
        final Activity activity, final String[] permissions,
        final int sdk_min
    )
    {

        boolean permission = true;

        if (Build.VERSION.SDK_INT >= sdk_min)
        {

            List<String> need_permission = new ArrayList<String>();
            for (String permission_str : permissions)
            {
                if (
                    activity.checkSelfPermission(permission_str)
                        != PackageManager.PERMISSION_GRANTED
                )
                {
                    need_permission.add(permission_str);
                }
            }
            if (!need_permission.isEmpty())
            {
                activity.requestPermissions(
                    need_permission.toArray(new String[0]), 0
                );
            }
            for (String permission_str : need_permission)
            {
                if (
                    activity.checkSelfPermission(permission_str)
                        != PackageManager.PERMISSION_GRANTED
                )
                {
                    permission = false;
                    break;
                }
            }

        }

        return permission;

    }

    /**
     * Compute the distance between two latitude/longitude pairs using the
     * Haversine formula, including their altitudes
     *
     * If altitudes are not available, pass zero to each
     *
     * @param x     First pair
     * @param y     Second pair
     * @param altX  Altitude of first pair, in meters
     * @param altY  Altitude of the second pair, in meters
     *
     * @return      Distance between the two points, in miles
     */
    public static double distanceHaversine(
        LatLng x, LatLng y, double altX, double altY
    )
    {

        // Altitude is given in meters
        // But distance is in kilometers and results are given in miles
        double height = (altX - altY) / 1000;

        double latDistance = Math.toRadians(y.latitude - x.latitude);
        double lonDistance = Math.toRadians(y.longitude - x.longitude);
        double line =
            Math.sin(latDistance / 2.0)
                * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(x.latitude))
                * Math.cos(Math.toRadians(y.latitude))
                * Math.sin(lonDistance / 2.0)
                * Math.sin(lonDistance / 2.0);
        double distance =
            2
                * Math.atan2(Math.sqrt(line), Math.sqrt(1.0 - line))
                * EARTH_RADIUS;
        distance = Math.sqrt(
            Math.pow(distance, 2.0) + Math.pow(height, 2.0)
        );

        return Utilities.kilometersToMiles(distance);

    }

    public static Address latLngToAddress(Geocoder geocoder, LatLng loc)
    {

        if (geocoder == null || loc == null)
        {
            return null;
        }

        List<Address> addresses = null;
        Address address = null;
        try
        {
            addresses = geocoder.getFromLocation(
                loc.latitude, loc.longitude, 1
            );
        }
        catch (IOException exception)
        {
            for (StackTraceElement ele : exception.getStackTrace())
            {
                Log.d(MainActivity.TAG, ele.toString());
            }
            Log.d(MainActivity.TAG, exception.toString());
            Log.d(
                MainActivity.TAG,
                "Utilities.latLngToAddress(), IO exception on geocoder call"
            );
        }
        if (addresses != null && addresses.size() > 0)
        {
            address = addresses.get(0);
        }

        return address;

    }

    /**
     * Convert miles to kilometers
     * @param miles Miles
     * @return      Kilometers
     */
    public static double milesToKilometers(double miles)
    {
        return miles * MILES_TO_KILOMETERS;
    }
    /**
     * Covert kilometers to miles
     * @param km    Kilometers
     * @return      Miles
     */
    public static double kilometersToMiles(double km)
    {
        return km / MILES_TO_KILOMETERS;
    }

    public static void checkPermission(Activity activity){
        if (Build.VERSION.SDK_INT < 23)
            return;
        if ((ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA}, 0);

        }

    }

}