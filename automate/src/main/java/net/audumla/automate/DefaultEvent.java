/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.automate;
/**
 * User: audumla
 * JulianDate: 23/07/13
 * Time: 10:20 AM
 */

import net.audumla.bean.BeanUtils;
import org.apache.log4j.Logger;

import java.util.Date;

public class DefaultEvent implements Event {
    private static final Logger logger = Logger.getLogger(DefaultEvent.class);

    private Date time;
    private long duration;
    private EventStatus status = EventStatus.PENDING;
    private Date endTime;
    private String failureMessage;
    private Throwable failureException;
    private String name = BeanUtils.generateName(this);

    public DefaultEvent(Date time, long duration) {
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
    public void setFailed(Throwable ex, String message) {
        setStatus(EventStatus.FAILED);
        failureException = ex;
        failureMessage = message;
    }

    @Override
    public String getFailureMessage() {
        return failureMessage;
    }

    @Override
    public Throwable getFailureException() {
        return failureException;
    }

    @Override
    public String getName() {
        return name;
    }

}
