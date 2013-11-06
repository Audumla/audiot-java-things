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

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class ObjectSetEvent implements AstronomicEvent {
    private static final Logger logger = LoggerFactory.getLogger(ObjectSetEvent.class);
    private OrbitingObject object;
    private Geolocation location;
    private double inclination;
    private Date eventTime;


    public ObjectSetEvent(OrbitingObject object, Geolocation location, double inclination) {
        setObject(object);
        setInclination(inclination);
        setLocation(location);
    }

    public Geolocation getLocation() {
        return location;
    }

    protected void setLocation(Geolocation location) {
        this.location = location;
    }

    public double getInclination() {
        return inclination;
    }

    protected void setInclination(double inclination) {
        this.inclination = inclination;
    }

    public OrbitingObject getObject() {
        return object;
    }

    protected void setObject(OrbitingObject object) {
        this.object = object;
    }

    @Override
    public Date getCalculatedEventTime() {
        if (eventTime == null) {
            calculateEventFrom(new Date());
        }
        return eventTime;
    }

    @Override
    public Date calculateEventFrom(Date date) {
        eventTime = object.getTransitDetails(date,getLocation(),getInclination()).getSetTime();
        return eventTime;

    }

    @Override
    public AstronomicEvent getNextEvent() {
        AstronomicEvent event = new ObjectRiseEvent(object,location,inclination);
        event.calculateEventFrom(DateUtils.addDays(getCalculatedEventTime(), 1));
        return event;
    }

    @Override
    public AstronomicEvent getPreviousEvent() {
        AstronomicEvent event = new ObjectRiseEvent(object,location,inclination);
        event.calculateEventFrom(DateUtils.addDays(getCalculatedEventTime(),-1));
        return event;
    }
}
