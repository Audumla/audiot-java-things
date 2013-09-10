/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.scheduler.quartz;

import net.audumla.util.Time;
import org.apache.commons.lang.time.DateUtils;
import org.quartz.Calendar;
import org.quartz.ScheduleBuilder;
import org.quartz.impl.triggers.AbstractTrigger;

import java.util.Date;

public class AstrologicalTriggerImpl extends AbstractTrigger<AstrologicalTrigger> implements AstrologicalTrigger {

    private static final long serialVersionUID = 1L;
    protected AstrologicalScheduleBuilder.AstrologicalSchedule schedule;
    protected Date startTime;
    protected Date endTime;
    protected Date nextFireTime;
    protected Date previousFireTime;
    protected int count = 0;

    public AstrologicalTriggerImpl(AstrologicalScheduleBuilder.AstrologicalSchedule schedule) {
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
            if (DateUtils.toCalendar(end).after(DateUtils.toCalendar(nextFire))) {
                Date start = schedule.startTime.getOffsetTime(now);
                if (DateUtils.toCalendar(start).after(DateUtils.toCalendar(nextFire))) {
                    //return the start getEventTime if the start getEventTime is after the calculated interval getEventTime
                    return start;
                } else {
                    // return the calculated interval if it lies between the start and end times
                    return nextFire;
                }
            } else {
                // return tomorrows start getEventTime if the interval getEventTime is after the end getEventTime.
                return schedule.startTime.getOffsetTime(DateUtils.addDays(now, 1));
            }
        }
        else {
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
    public ScheduleBuilder<AstrologicalTrigger> getScheduleBuilder() {
        return new AstrologicalScheduleBuilder();
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
        // TODO Auto-generated method stub

    }

    @Override
    public void updateWithNewCalendar(Calendar arg0, long arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean validateMisfireInstruction(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }

}
