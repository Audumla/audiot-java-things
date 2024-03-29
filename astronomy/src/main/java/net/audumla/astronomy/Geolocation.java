package net.audumla.astronomy;

/*
* *********************************************************************
*  ORGANIZATION : audumla.net
*  More information about this project can be found at the following locations:
*  http://www.audumla.net/
*  http://audumla.googlecode.com/
* *********************************************************************
*  Copyright (C) 2012 - 2013 Audumla.net
*  Licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
*  You may not use this file except in compliance with the License located at http://creativecommons.org/licenses/by-nc-nd/3.0/
*
*  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
*  "AS IS BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and limitations under the License.
 */

import net.audumla.bean.SafeParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Representation of a location on the globe
 *
 * @author Marius Gleeson
 */
public interface Geolocation {

    static Geolocation newGeoLocation(double lat, double lng, double elevation) {
        return new Location(lat, lng, elevation);
    }

    static Geolocation newGeoLocation(String repr) {
        return new Location(repr);
    }

    Double getLatitude();

    void setLatitude(Double latitude);

    Double getLongitude();

    void setLongitude(Double longitude);

    Double getElevation();

    void setElevation(Double elevation);

    Double getLatitude(Direction direction);

    void setLatitude(Double latitude, Direction direction);

    Double getLongitude(Direction direction);

    void setLongitude(Double longitude, Direction direction);

    /**
     * Direction settings for Latitude and Longitude values
     * Default is
     * Latitude North ( +ve for North -ve for South)
     * Longitude East ( +ve for East -ve for West)
     */
    public enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    /**
     * Class description
     *
     * @author Marius Gleeson
     */
    class Location implements Geolocation {
        private static final Logger logger = LoggerFactory.getLogger(Location.class);
        private Double latitude;
        private Double longitude;
        private Double elevation;

        /**
         * Constructs ...
         */
        public Location(String repr) {
            try {
                String[] values = repr.split(",");
                String[] slat = values[0].split(" ");
                String[] slon = values[1].split(" ");

                double lat = SafeParse.parseDouble(slat[0]);
                double lon = SafeParse.parseDouble(slon[0]);

                Direction latDir = "S".equals(slat[1]) ? Direction.SOUTH : Direction.NORTH;
                Direction longDir = "W".equals(slon[1]) ? Direction.WEST : Direction.EAST;

                setLatitude(lat, latDir);
                setLongitude(lon, longDir);

                if (values.length == 3) {
                    String[] selevation = values[2].split(" ");
                    double elevation = SafeParse.parseDouble(selevation[0]);
                    // convert from feet to meters
                    elevation = "f".equals(selevation[1]) ? 0.3048 * elevation : elevation;
                    setElevation(elevation);
                }
            } catch (Exception ex) {
                logger.error("Cannot parse location [" + repr + "]");
            }
        }


        /**
         * Constructs an empty location
         */
        public Location() {
        }

        /**
         * Constructs a location based on the supplied coordinates using North for latitude and East for longitude
         *
         * @param latitude
         * @param longitude
         * @param elevation
         */
        public Location(double latitude, double longitude, double elevation) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.elevation = elevation;
        }

        @Override
        public String toString() {
            return getLatitude() + " N," + getLongitude() + " E," + getElevation() + " m";
        }

        /**
         * Method description
         *
         * @return
         */
        @Override
        public Double getLatitude() {
            return latitude;
        }

        /**
         * Method description
         *
         * @param latitude
         */
        @Override
        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        /**
         * Method description
         *
         * @return
         */
        @Override
        public Double getLongitude() {
            return longitude;
        }

        /**
         * Method description
         *
         * @param longitude
         */
        @Override
        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        /**
         * Method description
         *
         * @return
         */
        @Override
        public Double getElevation() {
            return elevation;
        }

        /**
         * Method description
         *
         * @param elevation
         */
        @Override
        public void setElevation(Double elevation) {
            this.elevation = elevation;
        }

        /**
         * Method description
         *
         * @param direction
         * @return
         */
        @Override
        public Double getLatitude(Direction direction) {
            return (direction == Direction.NORTH)
                    ? latitude
                    : -1 * latitude;
        }

        /**
         * Method description
         *
         * @param latitude
         * @param direction
         */
        @Override
        public void setLatitude(Double latitude, Direction direction) {
            this.latitude = (direction == Direction.NORTH)
                    ? latitude
                    : -1 * latitude;
        }

        /**
         * Method description
         *
         * @param direction
         * @return
         */
        @Override
        public Double getLongitude(Direction direction) {
            return (direction == Direction.EAST)
                    ? longitude
                    : -1 * longitude;
        }

        /**
         * Method description
         *
         * @param longitude
         * @param direction
         */
        @Override
        public void setLongitude(Double longitude, Direction direction) {
            this.longitude = (direction == Direction.EAST)
                    ? longitude
                    : -1 * longitude;
        }
    }
}



