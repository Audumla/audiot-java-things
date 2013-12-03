package net.audumla.camel.scheduler;

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

import net.audumla.astronomy.AstronomicEvent;
import net.audumla.astronomy.Geolocation;
import net.audumla.bean.SafeParse;
import net.audumla.scheduler.quartz.AstronomicScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class SeasonalScheduleEndpoint extends DefaultSchedulerEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(SeasonalScheduleEndpoint.class);
    private String location;


    public SeasonalScheduleEndpoint(String uri, SchedulerComponent component) {
        super(uri, component);
    }

    public static String[] getParameters() {
        return new String[]{"seasonal.event"};
    }

    @Override
    protected Trigger createTrigger(Date startTime) {
        Geolocation location = Geolocation.newGeoLocation(getLocation());
        if (location.getLatitude() != null && location.getLongitude() != null) {
            AstronomicEvent astroEvent = getSeasonalEvent(location);

            if (astroEvent != null) {
                return TriggerBuilder.newTrigger()
                        .withIdentity(getTriggerKey())
                        .startAt(startTime)
                        .withSchedule(AstronomicScheduleBuilder.astronomicalSchedule().
                                startEvent(astroEvent).
                                withEventCount(getEventCount()))
                        .build();

            }
        } else {
            logger.error("A location must be specified for a seasonal event scheduler - {}", getEndpointUri());
        }
        return null;
    }

    private AstronomicEvent getSeasonalEvent(Geolocation location) {
        String event = (String) getTriggerParameters().get("event");
        try {
            if (event != null) {
                net.audumla.astronomy.SeasonalEvent.SeasonalFunction function = (net.audumla.astronomy.SeasonalEvent.SeasonalFunction) net.audumla.astronomy.SeasonalEvent.class.getDeclaredField(event.toUpperCase()).get(null);
                return new net.audumla.astronomy.SeasonalEvent(function, location);
            }
        } catch (Exception e) {
            logger.error("Cannot find seasonal event [{}]", event);
        }
        return null;
    }

    @Override
    public String getParameterPrefix() {
        return "seasonal";
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getEventCount() {
        String ec = (String) getTriggerParameters().get("eventCount");
        return "forever".equals(ec) ? 0 : SafeParse.parseInteger(ec, 0);
    }


}
