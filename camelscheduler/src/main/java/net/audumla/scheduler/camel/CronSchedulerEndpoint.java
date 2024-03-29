package net.audumla.scheduler.camel;

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

import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.quartz.CronScheduleBuilder.cronSchedule;

public class CronSchedulerEndpoint extends DefaultSchedulerEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(CronSchedulerEndpoint.class);
    private String cron;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public CronSchedulerEndpoint(String uri, SchedulerComponent component) {
        super(uri, component);
    }

    @Override
    protected Trigger createTrigger(Date startTime) {
        String cronexpr = (String) getTriggerParameters().get("expression");
        if (cronexpr == null) {
            cronexpr = cron;
        }
        logger.debug("Creating CronTrigger: {}", cron);
        return TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey())
                .startAt(startTime)
                .withSchedule(cronSchedule(cronexpr).withMisfireHandlingInstructionFireAndProceed())
                .build();
    }

    @Override
    public String getParameterPrefix() {
        return "trigger";
    }

    public static Map<String,String> getParameters() {
        Map<String,String> params = new HashMap<String,String>();
        // add the parameters that will identify that a cron scheduler is to be used. Null values imply that the value is a wildcard for identification purposes.
        params.put("cron",null);
        params.put("trigger.cron",null);
        return params;
    }

}
