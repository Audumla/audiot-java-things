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

import net.audumla.bean.BeanUtils;
import org.quartz.*;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.listeners.TriggerListenerSupport;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SequentialTriggerListener extends TriggerListenerSupport {

    private JobKey activeJob;
    private Scheduler activeScheduler;
    private Queue<JobDetail> queuedJobs = new ConcurrentLinkedQueue<JobDetail>();
    private String name = BeanUtils.generateName(TriggerListener.class);

    public SequentialTriggerListener() {
    }

    public SequentialTriggerListener(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    synchronized public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        if (activeJob != null) {
            getLog().debug("Queueing Sequential Job - " + context.getJobDetail().getKey().getName());
            JobDetail jd = context.getJobDetail();
            activeScheduler = context.getScheduler();
            jd = JobBuilder.newJob().usingJobData(jd.getJobDataMap()).withIdentity(getName() + ":" + jd.getKey().getName(), jd.getKey().getGroup())
                    .ofType(jd.getJobClass()).build();
            queuedJobs.add(jd);
            return true;
        } else {
            activeJob = trigger.getJobKey();
            getLog().debug("Executing Job - " + activeJob.getName());
            return false;
        }
    }

    public void triggerMisfired(Trigger trigger) {
        triggerFinalized(trigger);
    }

    public void triggerComplete(Trigger trigger, JobExecutionContext context, CompletedExecutionInstruction triggerInstructionCode) {
        triggerFinalized(trigger);
    }

    synchronized protected void triggerFinalized(Trigger trigger) {
        try {
            if (trigger.getJobKey().equals(activeJob)) {
                getLog().debug("Finalized Sequential Job - " + activeJob.getName());
                activeJob = null;
                JobDetail jd = queuedJobs.poll();
                if (jd != null) {
                    getLog().debug("Triggering Sequential Job - " + jd.getKey().getName());
                    activeScheduler.scheduleJob(jd, TriggerBuilder.newTrigger().forJob(jd).withIdentity("trigger:" + jd.getKey().getName(), jd.getKey().getGroup())
                            .startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0).withIntervalInMilliseconds(1)).build());
                }
            } else {
                // this should not occur as the trigger finalizing should be the one we are tracking.
                getLog().warn("Sequential Trigger Listener execution order failure");
            }
        } catch (SchedulerException ex) {
            getLog().warn("Sequential Trigger Listener failure", ex);
        }
    }

}
