package net.audumla.irrigation;

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
 *  "AS I BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

import net.audumla.automate.Event;
import net.audumla.automate.EventHandler;
import net.audumla.climate.ClimateObserver;

import java.util.Date;
import java.util.List;

public interface Zone extends EventHandler {

    /**
     * @return rating between 0 and 1 indicating amount of sunshine let through covering. 0 indicates no sunshine, 1 indicates all
     */
    double getShadeRating();

    /**
     * @return rating between 0 and 1 indicating how much the zone is surrounded. 0 indicates a complete surrounding (shed, greenhouse, etc). No wind penetrates the structure if a value of 0 is given. 1 indicates that there is no surrounding structure
     */
    double getEnclosureRating();

    /**
     * @return rating between 0 and 1 indicating the covering over the zone. Similar to enclosure however this directly affects the amount of rain that penetrates to the zone. A value of 0 indicates that no rain penetrates. 1 indicates that there is no covering
     */
    double getCoverRating();

    /**
     * @return m2 of the zone. Used to calculate water usage, ETo, and irrigation requirements
     */
    double getSurfaceArea();

    /**
     * @return amount of water in Litres per second that the zone receives when irrigation is turned on
     */
    double getFlowRate();

    /**
     * @return the mm depth per second that the zone receives when irrigation is turned on
     */
    double getDepthRate();

    /**
     * @return The observer that is used to calculate current weather effects and the impact to the zone
     */
    ClimateObserver getClimateObserver();

    /**
     * @return Crops that are growing in the zone. Used to determine watering needs and lifecycle information
     */
    List<Crop> getCrops();

    /**
     * TODO Make this a period to search over using Java 8 time functions
     *
     * @return The events that have been scheduled for the given day
     * @param day The day to return events for
     */
    List<Event> getIrrigationEventsForDay(Date day);

//    void addIrrigationEvent(Date when, int seconds);

}
