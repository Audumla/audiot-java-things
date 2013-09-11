/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.irrigation;

import net.audumla.climate.ClimateObserver;

import java.util.Date;
import java.util.List;

public interface Zone {
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
     * @return The observer that is used to calculate current weather effects and the impact to the zone
     */
    ClimateObserver getClimateObserver();

    /**
     * @return Crops that are growing in the zone. Used to determine watering needs and lifecycle information
     */
    List<Crop> getCrops();

    List<IrrigationEvent> getIrrigationEventsForDay(Date day);

    void addIrrigationEvent(IrrigationEvent event);

    void addIrrigationEvent(Date when, int seconds);

    /**
     * @return the irrigated depth in mm over a given duration
     */
    default double calculateIrrigatedDepth(long seconds) {
        double litres = seconds * getFlowRate();
        double volume = litres / 1000;
        double depth = (volume / getSurfaceArea()) * 1000;
        return depth;
    }

    /**
     * @return the number of seconds required to irrigate to the given depth
     */
    default long calculateIrrigationDuration(double depth) {
        double area = getSurfaceArea(); // m2
        double volume = (depth / 1000) * area; //m3
        double litres = volume * 1000; // litres
        long duration = (long) (litres / getFlowRate()); //seconds
        return duration;
    }
}
