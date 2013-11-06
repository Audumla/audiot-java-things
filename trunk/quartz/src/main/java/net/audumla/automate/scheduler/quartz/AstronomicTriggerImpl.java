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

import org.quartz.Calendar;
import org.quartz.ScheduleBuilder;
import org.quartz.impl.triggers.AbstractTrigger;

import java.util.Date;

public class AstronomicTriggerImpl extends AbstractTrigger<AstronomicalTrigger> implements AstronomicalTrigger {

    private static final long serialVersionUID = 1L;
    protected AstronomicScheduleBuilder.AstronomicSchedule schedule;
    protected Date startTime;
    protected Date endTime;
    protected Date nextFireTime;
    protected Date previousFireTime;
    protected int count = 0;
    protected int eventCount = 0;
    protected Calendar calendar;

    public AstronomicTriggerImpl(AstronomicScheduleBuilder.AstronomicSchedule schedule) {
        this.schedule = schedule;
        setName(getClass().getName());
    }

    @Override
    public Date computeFirstFireTime(Calendar cal) {
        // seed the start event
        calendar = cal;
        setNextFireTime(getFireTimeAfter(getStartTime()));
        ++eventCount; // set the event count as the first trigger will not increment this
        return getNextFireTime();
    }

    @Override
    public Date getEndTime() {
        return endTime;
    }

    @Override
    public void setEndTime(Date time) {
        endTime = time;
    }

    @Override
    public Date getFinalFireTime() {
        return null;
    }

    @Override
    public Date getFireTimeAfter(Date now) {
        Date fireTime = null;
        //seed the start time
        schedule.startTime.calculateEventFrom(now);
        // honor the count for the number of events to repeat on
        if (schedule.eventCount == Integer.MIN_VALUE || eventCount < schedule.eventCount) {
            // Ensure that we only execute a number of times equal to the repeat count during start and end events
            boolean nextEvent = false;
            if (schedule.repeat == Integer.MIN_VALUE || count <= schedule.repeat) {
                fireTime = org.apache.commons.lang3.time.DateUtils.addSeconds(now, (int) schedule.interval);
                // if there is no end time then use the next start event instead so that we dont overlap with recurring events
                Date end = schedule.endTime == null ? schedule.startTime.getNextEvent().getCalculatedEventTime() : schedule.endTime.calculateEventFrom(now);
                // make sure that the next interval is not passed the end event time
                if (end.after(fireTime)) {
                    // get the event time that has been calculated using the initial seed time
                    Date start = org.apache.commons.lang3.time.DateUtils.addSeconds(schedule.startTime.getCalculatedEventTime(), (int) schedule.startOffset);
                    if (start.after(fireTime)) {
                        //return the calculated start time if it is after now plus the interval
                        fireTime = start;
                    }
                } else {
                    nextEvent = true;
                }
            } else {
                nextEvent = true;
            }

            if (nextEvent) {
                // if the next fire time is after the calculated end time then we need to reseed the start time to the next scheduled event
                schedule.startTime = schedule.startTime.getNextEvent();
                // increment the event count as we have passed an entire event cycle if we enter this section. We also need to reset the count to 0 as the count applies for each individual triggered event
                ++eventCount;
                count = 0;
                fireTime = org.apache.commons.lang3.time.DateUtils.addSeconds(schedule.startTime.getCalculatedEventTime(), (int) schedule.startOffset);
            }
        }
        if (fireTime != null && calendar != null && !calendar.isTimeIncluded(fireTime.getTime())) {
            Date nextValid = new Date(calendar.getNextIncludedTime(fireTime.getTime()));
            fireTime = getFireTimeAfter(nextValid);
        }
        return fireTime;

    }

    @Override
    public Date getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Date time) {
        nextFireTime = time;
    }

    @Override
    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    public void setPreviousFireTime(Date time) {
        previousFireTime = time;
    }

    @Override
    public ScheduleBuilder<AstronomicalTrigger> getScheduleBuilder() {
        return new AstronomicScheduleBuilder();
    }

    @Override
    public Date getStartTime() {
        if (startTime == null) {
            startTime = schedule.startTime.getCalculatedEventTime();
        }
        return startTime;
    }

    @Override
    public void setStartTime(Date time) {
        startTime = time;
    }

    @Override
    public boolean mayFireAgain() {
        return true;
    }

    @Override
    public void triggered(Calendar cal) {
        calendar = cal;
        ++count;
        setPreviousFireTime(getNextFireTime());
        setNextFireTime(getFireTimeAfter(getNextFireTime()));
    }

    @Override
    public void updateAfterMisfire(Calendar cal) {
        calendar = cal;
    }

    @Override
    public void updateWithNewCalendar(Calendar cal, long misfireThreshold) {
        calendar = cal;
        if (calendar != null && !calendar.isTimeIncluded(getNextFireTime().getTime())) {
            setNextFireTime(getFireTimeAfter(new Date(calendar.getNextIncludedTime(getNextFireTime().getTime()))));
        } else {
            setNextFireTime(getFireTimeAfter(new Date()));
        }
    }

    @Override
    protected boolean validateMisfireInstruction(int misfireInstruction) {
        return false;
    }

}
