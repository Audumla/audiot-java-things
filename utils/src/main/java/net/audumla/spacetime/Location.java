/*
 * Copyright 2008-2009 Mike Reedell / LuckyCatLabs.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.audumla.spacetime;

import net.audumla.spacetime.Geolocation;

/**
 * Simple VO class to store latitude/longitude information.
 */
public class Location implements Geolocation {
    private double latitude;
    private double longitude;
    private double elevation;

    /**
     * Creates a new instance of <code>Location</code> with the given parameters.
     *
     * @param latitude  the latitude, in degrees, of this location. North latitude is positive, south negative.
     * @param longitude the longitude, in degrees, of this location. East longitude is positive, east negative.
     * @param elevation the elevaion above sea level in meters
     */
    public Location(Double latitude, Double longitude, Double elevation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    /**
     * @return the latitude
     */
    @Override
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the value of <code>Latitude</code> with the given parameter.
     *
     * @param latitude the latitude, in degrees, of this location. North latitude is positive, south negative.
     */
    @Override
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    @Override
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the value of <code>Longitude</code> with the given parameter.
     *
     * @param longitude the Longitude, in degrees, of this location. East longitude is positive, east negative.
     */
    @Override
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the elevation
     */
    @Override
    public double getElevation() {
        return elevation;
    }

    /**
     * Sets the value of <code>elevation</code> with the given parameter.
     *
     * @param elevation the elevation, in meters, above sea level
     */
    @Override
    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }
}
