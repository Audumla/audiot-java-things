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
import net.audumla.astronomical.Geolocation;
import org.quartz.ScheduleBuilder;
import org.quartz.spi.MutableTrigger;

public class AstronomicScheduleBuilder extends ScheduleBuilder<AstronomicalTrigger> {

    private AstronomicSchedule schedule = new AstronomicSchedule();

    public static class AstronomicSchedule {
        public AstronomicEvent startTime;
        public AstronomicEvent endTime;
        public int interval = 1;
        public int repeat = 0;
        public int eventCount = Integer.MIN_VALUE;
        public long startOffset = 0;
    }


    public AstronomicScheduleBuilder startEvent(AstronomicEvent objectEvent) {
        schedule.startTime = objectEvent;
        return this;
    }

    public AstronomicScheduleBuilder endEvent(AstronomicEvent objectEvent) {
        schedule.endTime = objectEvent;
        return this;
    }

    public AstronomicScheduleBuilder withEventCount(int i) {
        schedule.eventCount = i;
        return this;
    }

    public AstronomicScheduleBuilder startEventOffset(long i) {
        schedule.startOffset = i;
        return this;
    }

    public AstronomicScheduleBuilder forEveryEvent() {
        schedule.eventCount = Integer.MIN_VALUE;
        return this;
    }

    public AstronomicScheduleBuilder withSecondInterval(int seconds) {
        schedule.interval = seconds;
        return this;
    }

    public AstronomicScheduleBuilder withCount(int count) {
        schedule.repeat = count;
        return this;
    }

    public static AstronomicScheduleBuilder astronomicalSchedule() {
        return new AstronomicScheduleBuilder();
    }

    @Override
    protected MutableTrigger build() {
        return new AstronomicTriggerImpl(schedule);
    }

}
