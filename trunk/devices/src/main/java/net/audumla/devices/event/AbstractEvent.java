package net.audumla.devices.event;
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

/**
 * User: audumla
 * JulianDate: 23/07/13
 * Time: 10:20 AM
 */

import net.audumla.bean.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;

public class AbstractEvent implements Event {
    private static final Logger logger = LoggerFactory.getLogger(AbstractEvent.class);

    private EventStatus status = EventStatus.PENDING;
    private String failureMessage;
    private Throwable failureException;
    private String name = BeanUtils.generateName(Event.class);
    private Instant executedTime;
    private Instant completedTime;
    private EventScheduler scheduler;

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

    @Override
    public Instant getExecutedTime() {
        return executedTime;
    }

    @Override
    public Instant getCompletedTime() {
        return completedTime;
    }

    public void setExecutedTime(Instant executedTime) {
        this.executedTime = executedTime;
    }

    public void setCompletedTime(Instant completedTime) {
        this.completedTime = completedTime;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            return org.apache.commons.beanutils.BeanUtils.cloneBean(this);
        } catch (Exception e) {
            logger.error("Unable to clone Event ",this.getClass().getName(),e);
            throw new CloneNotSupportedException("Failed to clone Activator Command");
        }
    }

    public EventScheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(EventScheduler scheduler) {
        this.scheduler = scheduler;
    }
}
