package com.alphaomardiallo.go4lunch.domain;

import android.location.Location;
import android.location.LocationManager;

public class DistanceCalculatorUtils {

    public float getDistance(String startLocation, double endLocationLat, double endLocationLng) {
        String[] separatedStart = startLocation.split(",");
        double startLatitude = Double.parseDouble(separatedStart[0]);
        double startLongitude = Double.parseDouble(separatedStart[1]);
        Location tempStart = new Location(LocationManager.GPS_PROVIDER);
        tempStart.setLatitude(startLatitude);
        tempStart.setLongitude(startLongitude);
        Location tempEnd = new Location(LocationManager.GPS_PROVIDER);
        tempEnd.setLatitude(endLocationLat);
        tempEnd.setLongitude(endLocationLng);
        return tempStart.distanceTo(tempEnd);
    }

    public float getDistanceTwoStrings(String startLocation, String endLocation) {
        String[] separatedStart = startLocation.split(",");
        double startLatitude = Double.parseDouble(separatedStart[0]);
        double startLongitude = Double.parseDouble(separatedStart[1]);
        Location tempStart = new Location(LocationManager.GPS_PROVIDER);
        tempStart.setLatitude(startLatitude);
        tempStart.setLongitude(startLongitude);

        String[] separatedEnd = endLocation.split(",");
        double endLatitude = Double.parseDouble(separatedStart[0]);
        double endLongitude = Double.parseDouble(separatedStart[1]);
        Location tempEnd = new Location(LocationManager.GPS_PROVIDER);
        tempEnd.setLatitude(endLatitude);
        tempEnd.setLongitude(endLongitude);
        return tempStart.distanceTo(tempEnd);
    }
}
