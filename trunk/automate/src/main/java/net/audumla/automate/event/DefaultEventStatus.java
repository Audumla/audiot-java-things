package net.audumla.automate.event;

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

import java.time.Instant;

public class DefaultEventStatus implements EventStatus {

    private EventState state = EventState.PENDING;
    private Throwable failureThrowable;
    private String failureMessage;
    private Instant executedTime;
    private Instant completedTime;

    @Override
    public void setFailed(Throwable ex, String message) {
        failureMessage = message;
        failureThrowable = ex;
        state = EventState.FAILED;
    }

    @Override
    public String getFailureMessage() {
        return failureMessage;
    }

    @Override
    public Throwable getFailureException() {
        return failureThrowable;
    }

    @Override
    public EventState getState() {
        return state;
    }

    @Override
    public void setState(EventState status) {
        this.state = status;
    }

    @Override
    public Instant getExecutedTime() {
        return executedTime;
    }

    @Override
    public Instant getCompletedTime() {
        return completedTime;
    }

    @Override
    public void setExecutedTime(Instant executedTime) {
        this.executedTime = executedTime;

    }

    @Override
    public void setCompletedTime(Instant completedTime) {
        this.completedTime = completedTime;
    }
}
