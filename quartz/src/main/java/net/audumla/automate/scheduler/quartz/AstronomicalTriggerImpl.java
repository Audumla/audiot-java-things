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

import net.audumla.Time;
import org.apache.commons.lang.time.DateUtils;
import org.quartz.Calendar;
import org.quartz.ScheduleBuilder;
import org.quartz.impl.triggers.AbstractTrigger;

import java.util.Date;

public class AstronomicalTriggerImpl extends AbstractTrigger<AstronomicalTrigger> implements AstronomicalTrigger {

    private static final long serialVersionUID = 1L;
    protected AstronomicalScheduleBuilder.AstronomicalSchedule schedule;
    protected Date startTime;
    protected Date endTime;
    protected Date nextFireTime;
    protected Date previousFireTime;
    protected int count = 0;

    public AstronomicalTriggerImpl(AstronomicalScheduleBuilder.AstronomicalSchedule schedule) {
        this.schedule = schedule;
    }

    public void setNextFireTime(Date time) {
        nextFireTime = time;
    }

    public void setPreviousFireTime(Date time) {
        previousFireTime = time;
    }

    @Override
    public Date computeFirstFireTime(Calendar now) {
        setNextFireTime(getFireTimeAfter(getStartTime()));
        return getNextFireTime();
    }

    @Override
    public Date getEndTime() {
        return endTime;
    }

    @Override
    public Date getFinalFireTime() {
        return null;
    }

    @Override
    public Date getFireTimeAfter(Date now) {
        if (schedule.repeat == Integer.MIN_VALUE || count < schedule.repeat) {
            Date nextFire = Time.offset(now, 0, 0, schedule.interval);
            Date end = schedule.endTime.getOffsetTime(now);
            if (end.after(nextFire)) {
                Date start = schedule.startTime.getOffsetTime(now);
                if (start.after(nextFire)) {
                    //return the start getEventStartTime if the start getEventStartTime is after the calculated interval getEventStartTime
                    return start;
                } else {
                    // return the calculated interval if it lies between the start and end times
                    return nextFire;
                }
            } else {
                // return tomorrows start getEventStartTime if the interval getEventStartTime is after the end getEventStartTime.
                return schedule.startTime.getOffsetTime(DateUtils.addDays(now, 1));
            }
        } else {
            return null;
        }

    }

    @Override
    public Date getNextFireTime() {
        return nextFireTime;
    }

    @Override
    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    @Override
    public ScheduleBuilder<AstronomicalTrigger> getScheduleBuilder() {
        return new AstronomicalScheduleBuilder();
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }

    @Override
    public boolean mayFireAgain() {
        return true;
    }

    @Override
    public void setEndTime(Date time) {
        endTime = time;
    }

    @Override
    public void setStartTime(Date time) {
        startTime = time;
    }

    @Override
    public void triggered(Calendar arg0) {
        ++count;
        setPreviousFireTime(getNextFireTime());
        setNextFireTime(getFireTimeAfter(getNextFireTime()));
    }

    @Override
    public void updateAfterMisfire(Calendar arg0) {
    }

    @Override
    public void updateWithNewCalendar(Calendar arg0, long arg1) {
    }

    @Override
    protected boolean validateMisfireInstruction(int arg0) {
        return false;
    }

}
