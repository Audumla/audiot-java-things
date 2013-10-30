package net.audumla.automate.scheduler.quartz;

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

import net.audumla.astronomical.AstronomicEvent;
import net.audumla.astronomical.ObjectRiseEvent;
import net.audumla.astronomical.algorithims.Sun;
import net.audumla.bean.BeanUtils;
import net.audumla.astronomical.Geolocation;
import net.audumla.Time;
import org.quartz.ScheduleBuilder;
import org.quartz.spi.MutableTrigger;

import java.util.Calendar;
import java.util.Date;

public class AstronomicalScheduleBuilder extends ScheduleBuilder<AstronomicalTrigger> {

    private AstronomicalSchedule schedule = new AstronomicalSchedule();

    public AstronomicalScheduleBuilder startEvent(ObjectRiseEvent objectRiseEvent) {
        return null;
    }

    public static class AstronomicalSchedule {
        public AstronomicEvent startTime;
        public AstronomicEvent endTime;
        public int interval = 60 * 30; // 30 minutes
        public int repeat = Integer.MIN_VALUE;
    }

    public static abstract class ReferenceTime {
        int offset;
        Geolocation location;

        protected abstract Date getReferenceTime(Date now);

        public Date getOffsetTime(Date now) {
            Calendar c = Time.newCalendar();
            c.setTime(getReferenceTime(now));
            c.add(Calendar.SECOND, offset);
            return c.getTime();
        }

        protected void set(ReferenceTime rt, Geolocation location) {
            if (rt != null) {
                this.offset = rt.offset;
            }
            this.location = location;
        }
    }

    protected static class SunsetReferenceTime extends ReferenceTime {

        public SunsetReferenceTime() {
        }

        public SunsetReferenceTime(ReferenceTime rt, Geolocation location) {
            set(rt, location);
        }

        @Override
        public Date getReferenceTime(Date now) {
            return new Sun().getTransitDetails(now,location,Sun.CIVIL).getSetTime();
        }

    }

    protected static class SunriseReferenceTime extends ReferenceTime {

        public SunriseReferenceTime() {
        }

        public SunriseReferenceTime(ReferenceTime rt, Geolocation location) {
            set(rt, location);
        }

        @Override
        public Date getReferenceTime(Date now) {
            return new Sun().getTransitDetails(now,location,Sun.CIVIL).getRiseTime();
        }

    }

    public AstronomicalScheduleBuilder offsetStartFromSet(int seconds) {
        schedule.startTime = new SunsetReferenceTime(schedule.startTime, schedule.location);
        schedule.startTime.offset = seconds;
        return this;
    }

    public AstronomicalScheduleBuilder startEventOffset(int seconds) {
        schedule.startTime = new SunriseReferenceTime(schedule.startTime, schedule.location);
        schedule.startTime.offset = seconds;
        return this;
    }

    public AstronomicalScheduleBuilder offsetEndFromSet(int seconds) {
        schedule.endTime = new SunsetReferenceTime(schedule.endTime, schedule.location);
        schedule.endTime.offset = seconds;
        return this;
    }

    public AstronomicalScheduleBuilder offsetEndFromRise(int seconds) {
        schedule.endTime = new SunriseReferenceTime(schedule.endTime, schedule.location);
        schedule.endTime.offset = seconds;
        return this;
    }

    public AstronomicalScheduleBuilder atLocation(double llat, double llong) {
        schedule.location.setLatitude(llat);
        schedule.location.setLongitude(llong);
        return this;
    }

    public AstronomicalScheduleBuilder atLocation(Geolocation location) {
        schedule.location = location;
        return this;
    }

    public AstronomicalScheduleBuilder withSecondInterval(int seconds) {
        schedule.interval = seconds;
        return this;
    }

    public AstronomicalScheduleBuilder withCount(int count) {
        schedule.repeat = count;
        return this;
    }

    public static AstronomicalScheduleBuilder astronomicalSchedule() {
        return new AstronomicalScheduleBuilder();
    }

    @Override
    protected MutableTrigger build() {
        return new AstronomicalTriggerImpl(schedule);
    }

}
