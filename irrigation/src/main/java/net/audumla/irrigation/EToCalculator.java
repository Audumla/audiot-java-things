/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.irrigation;
/**
 * User: audumla
 * Date: 27/07/13
 * Time: 3:42 PM
 */

import net.audumla.automate.Event;
import net.audumla.climate.ClimateData;
import net.audumla.climate.ClimateDataSource;
import net.audumla.util.Time;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EToCalculator {
    private static final Logger logger = Logger.getLogger(EToCalculator.class);
    private static final long calculationDurationInDays = 5;
    private static final long minuteTimeout = 15;
    private Zone zone;
    private Date lastCalculated = new Date(0);
    private double currentETo;

    public EToCalculator() {
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }


    public double calculateETo(Date now) {
        // refresh if we are in a different day or an elapse time has passed
        if (!DateUtils.isSameDay(lastCalculated, now) ||
                (DateUtils.getFragmentInMinutes(lastCalculated, Calendar.DAY_OF_YEAR) - DateUtils.getFragmentInMinutes(now, Calendar.DAY_OF_YEAR)) > minuteTimeout) {
            double debt = 0.0;
            for (int i = 0; i < calculationDurationInDays; ++i) {
                try {
                    // add up all the ETo values over the past days.
                    // the current day may use forecast information
                    ClimateData data = zone.getClimateObserver().getClimateData(now);
                    try {
                        double eto = data.getEvapotranspiration();
                        debt += eto;
                    } catch (UnsupportedOperationException ex) {
                        // no ETo information available
                    }

                    try {
                        double rain = data.getRainfall();
                        // if the rainfall value is a forecast then we revert to the latest observation and use the value
                        // that is the rainfall since 9am.
                        if (!data.getDataSource().getType().equals(ClimateDataSource.ClimateDataSourceType.DAILY_FORECAST)) {
                            debt -= rain;
                        } else {
                            debt -= data.getObservation(now, ClimateData.ObservationMatch.CLOSEST).getRainfall();
                        }
                    } catch (UnsupportedOperationException ex) {
                        // no rain information available
                    }
                    // add all the irrigation events for this day against the water debt
                    List<Event> events = zone.getIrrigationEventsForDay(now);
                    for (Event e : events) {
                        if (e.getEventStartTime().before(now)) {
                            debt -= IrrigationZone.calculateIrrigatedDepth(zone, e.getEventDuration());
                        }
                    }
                } catch (UnsupportedOperationException ex) {
                    logger.warn("Unable to obtain climate data for " + Time.dateFormatter.format(now), ex);
                } finally {
                    now = DateUtils.addDays(now, -1);
                }
            }
            currentETo = debt;
            lastCalculated = now;
        }
        return currentETo;

    }

}
