package net.audumla.scheduler.camel;

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

import net.audumla.astronomy.*;
import net.audumla.bean.SafeParse;
import net.audumla.scheduler.quartz.AstronomicScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CelestialScheduleEndpoint extends DefaultSchedulerEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(CelestialScheduleEndpoint.class);
    private String location;

    public CelestialScheduleEndpoint(String uri, SchedulerComponent component) {
        super(uri, component);
    }

    public static Map<String,String> getParameters() {
        Map<String,String> params = new HashMap<String,String>();
        // add the parameters that will identify that this scheduler is to be used. When the trigger.event parameter is 'celestial' then this scheduler will be used
        params.put("trigger.event","rise|set");
        return params;
    }


    @Override
    protected Trigger createTrigger(Date startTime) {
        Geolocation location = Geolocation.newGeoLocation(getLocation());
        if (location.getLatitude() != null && location.getLongitude() != null) {
            AstronomicEvent astroEvent = createAstronomicalObjectEvent(getObject(), getEvent(), getInclination(), location);

            if (astroEvent != null) {
                AstronomicScheduleBuilder builder = AstronomicScheduleBuilder.astronomicalSchedule().
                        startEvent(astroEvent).
                        startEventOffset(SafeParse.parseInteger(getEventOffset(), 0)).
                        endEvent(createAstronomicalObjectEvent(getObject(), getEndEvent(), getInclination(), location)).
                        endEventOffset(getEndEventOffset()).
                        withIntervalInSeconds(getInterval()).
                        withRepeatCount(getRepeatCount()).
                        withEventCount(getEventCount());


                return TriggerBuilder.newTrigger()
                        .withIdentity(getTriggerKey())
                        .startAt(startTime)
                        .withSchedule(builder)
                        .build();

            }
        } else {
            logger.error("A location must be specified for a celestial event scheduler - {}", getEndpointUri());
        }
        return null;
    }

    private AstronomicEvent createAstronomicalObjectEvent(AstronomicalObject astrobj, AstronomicalObjectEvent aevent, double inclination, Geolocation location) {
        if (aevent != null) {
            try {
                CelestialObject object = (CelestialObject) CelestialObject.class.getDeclaredField(astrobj.toString()).get(null);
                AstronomicEvent event = null;
                switch (aevent) {
                    case RISE: {
                        event = new CelestialObjectRiseEvent(object, location, inclination);
                        break;
                    }
                    case SET: {
                        event = new CelestialObjectSetEvent(object, location, inclination);
                        break;
                    }
                }
                return event;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.error("Cannot find Celestial Object [{}]", getObject());
            }
        }
        return null;
    }

    public AstronomicalObject getObject() {
        String sobject = getTriggerParameters().get("object") == null ? "SUN" : getTriggerParameters().get("object").toString().toUpperCase();
        try {
            return AstronomicalObject.valueOf(sobject);
        } catch (Exception ex) {
            logger.error("Cannot find Celestial Object [{}]", sobject);
        }
        return null;
    }

    public AstronomicalObjectEvent getEvent() {
        String sevent = getTriggerParameters().get("event") == null ? "RISE" : getTriggerParameters().get("event").toString().toUpperCase();
        try {
            return AstronomicalObjectEvent.valueOf(sevent);
        } catch (Exception ex) {
            logger.error("Cannot find Celestial Event [{}]", sevent);
        }
        return null;
    }

    public AstronomicalObjectEvent getEndEvent() {
        String sevent = (String) getTriggerParameters().get("event");
        if (sevent != null) {
            try {
                return AstronomicalObjectEvent.valueOf(sevent);
            } catch (Exception ex) {
                logger.error("Cannot find Celestial Event [{}]", sevent);
            }
        }
        return null;
    }

    public int getRepeatCount() {
        String ec = (String) getTriggerParameters().get("repeatCount");
        return "forever".equals(ec) ? 0 : SafeParse.parseInteger(ec, 0);
    }

    public int getInterval() {
        return SafeParse.parseInteger(getTriggerParameters().get("repeatInterval"), 0);
    }

    public double getInclination() {
        String inclination = (String) getTriggerParameters().get("inclination");
        if (inclination != null) {
            try {
                return CelestialObject.Inclination.valueOf(inclination).getAngle();
            } catch (IllegalArgumentException ex) {
                return SafeParse.parseDouble(inclination, CelestialObject.Inclination.CIVIL.getAngle());
            }
        } else {
            return CelestialObject.Inclination.CIVIL.getAngle();
        }
    }

    public long getEventOffset() {
        return SafeParse.parseLong((String) getTriggerParameters().get("eventOffset"), 0l);
    }

    public int getEventCount() {
        String ec = (String) getTriggerParameters().get("eventCount");
        return "forever".equals(ec) ? 0 : SafeParse.parseInteger(ec, 0);
    }

    public long getEndEventOffset() {
        return SafeParse.parseLong((String) getTriggerParameters().get("endEventOffset"), 0l);
    }

    @Override
    public String getParameterPrefix() {
        return "trigger";
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private static enum AstronomicalObject {SUN, MOON, MERCURY, VENUS, EARTH, MARS, JUPITER, SATURN, URANUS, NORTH, PLUTO}

    private static enum AstronomicalObjectEvent {RISE, SET}
}
