package net.audumla.camel.scheduler;

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

import net.audumla.bean.SafeParse;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

public class SimpleScheduleEndpoint extends DefaultSchedulerEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(SimpleScheduleEndpoint.class);
    private boolean fireNow;

    public SimpleScheduleEndpoint(String uri, SchedulerComponent component) {
        super(uri, component);
    }

    public boolean isFireNow() {
        return fireNow;
    }

    public void setFireNow(boolean fireNow) {
        this.fireNow = fireNow;
    }

    @Override
    protected Trigger createTrigger(Date startTime) {
        logger.debug("Creating SimpleTrigger.");
        long interval = SafeParse.parseLong((String) getTriggerParameters().get("repeatInterval"));
        int count = SafeParse.parseInteger((String) getTriggerParameters().get("repeatCount"));
        if (interval == 0) {
            count = 0;
        }

        TriggerBuilder<SimpleTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey())
                .startAt(startTime)
                .withSchedule(simpleSchedule().withRepeatCount(count).withIntervalInMilliseconds(interval).withMisfireHandlingInstructionFireNow());

        // Enable trigger to fire now by setting startTime in the past.
        if (fireNow) {
            triggerBuilder.startAt(new Date(System.currentTimeMillis() - interval));
        }


        return triggerBuilder.build();
    }

    @Override
    public String getParameterPrefix() {
        return "trigger";
    }


}
