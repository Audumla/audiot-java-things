/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.irrigation;
/**
 * User: audumla
 * Date: 23/07/13
 * Time: 10:20 AM
 */

import org.apache.log4j.Logger;

import java.util.Date;

public class IrrigationEventImpl implements IrrigationEvent {
    private static final Logger logger = Logger.getLogger(IrrigationEventImpl.class);

    private Date time;
    private long duration;
    private EventStatus status = EventStatus.PENDING;
    private Date endTime;
    private String failureMessage;
    private Exception failureException;

    public IrrigationEventImpl(Date time, long duration, double depth) {
        this.time = time;
        this.duration = duration;
    }

    @Override
    public Date getEventStartTime() {
        return time;
    }

    @Override
    public Date getEventEndTime() {
        return endTime;
    }

    @Override
    public long getEventDuration() {
        return duration;
    }

    @Override
    public EventStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(EventStatus status) {
        this.status = status;
    }

    @Override
    public void setFailed(Exception ex, String message) {
        setStatus(EventStatus.FAILED);
        failureException = ex;
        failureMessage = message;
    }

    @Override
    public String getFailureMessage() {
        return failureMessage;
    }

    @Override
    public Exception getFailureException() {
        return failureException;
    }

}
