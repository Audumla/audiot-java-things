package net.audumla.astronomical;

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

/**
 * Representation of a location on the globe
 *
 * @author         Marius Gleeson
 */
public interface Geolocation {

    /**
     * Direction settings for Latitude and Longitude values
     * Default is
     *  Latitude North ( +ve for North -ve for South)
     *  Longitude East ( +ve for East -ve for West)
     *
     */
    public enum Direction { NORTH, SOUTH, EAST, WEST }

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
}



