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

import org.apache.camel.Route;
import org.apache.camel.component.quartz2.*;
import org.apache.camel.util.EndpointHelper;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class DefaultSchedulerEndpoint extends QuartzEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(DefaultSchedulerEndpoint.class);
    private Map<String, Object> triggerParameters;
    private Map<String, Object> jobParameters;
    // An internal variables to track whether a job has been in scheduler or not, and has it paused or not.
    private AtomicBoolean jobAdded = new AtomicBoolean(false);

    public DefaultSchedulerEndpoint(String uri, SchedulerComponent component) {
        super(uri, component);
    }

    public void setTriggerParameters(Map<String, Object> triggerParameters) {
        this.triggerParameters = triggerParameters;
    }

    public Map<String, Object> getTriggerParameters() {
        return triggerParameters;
    }

    public Map<String, Object> getJobParameters() {
        return jobParameters;
    }

    public void setJobParameters(Map<String, Object> jobParameters) {
        this.jobParameters = jobParameters;
    }

    private void addJobInScheduler() throws Exception {
        // Add or use existing trigger to/from scheduler
        Scheduler scheduler = getComponent().getScheduler();
        JobDetail jobDetail;
        Trigger trigger = scheduler.getTrigger(getTriggerKey());
        if (trigger == null) {
            Date startTime = new Date();
            if (getComponent().getScheduler().isStarted()) {
                startTime = new Date(System.currentTimeMillis() + getTriggerStartDelay());
            }
            jobDetail = createJobDetail();
            trigger = createTrigger(startTime);

            if (getTriggerParameters() != null && getTriggerParameters().size() > 0) {
                logger.debug("Setting user extra triggerParameters {}", getTriggerParameters());
                setProperties(trigger, getTriggerParameters());
            }

            logger.debug("Created trigger={}", trigger);

            updateJobDataMap(jobDetail);

            // Schedule it now. Remember that scheduler might not be started it, but we can schedule now.
            Date nextFireDate = scheduler.scheduleJob(jobDetail, trigger);
            if (logger.isInfoEnabled()) {
                logger.info("Job {} (triggerType={}, jobClass={}) is scheduled. Next fire date is {}",
                        new Object[]{trigger.getKey(), trigger.getClass().getSimpleName(),
                                jobDetail.getJobClass().getSimpleName(), nextFireDate});
            }
        } else {
            ensureNoDupTriggerKey();
        }

        // Increase camel job count for this endpoint
        AtomicInteger number = (AtomicInteger) scheduler.getContext().get(QuartzConstants.QUARTZ_CAMEL_JOBS_COUNT);
        if (number != null) {
            number.incrementAndGet();
        }

        jobAdded.set(true);
    }

    protected abstract Trigger createTrigger(Date startTime);

    protected void ensureNoDupTriggerKey() {
        for (Route route : getCamelContext().getRoutes()) {
            if (route.getEndpoint() instanceof DefaultSchedulerEndpoint) {
                DefaultSchedulerEndpoint quartzEndpoint = (DefaultSchedulerEndpoint) route.getEndpoint();
                TriggerKey checkTriggerKey = quartzEndpoint.getTriggerKey();
                if (getTriggerKey().equals(checkTriggerKey)) {
                    throw new IllegalArgumentException("Trigger key " + getTriggerKey() + " is already in used by " + quartzEndpoint);
                }
            }
        }
    }

    protected void updateJobDataMap(JobDetail jobDetail) {
        // Store this camelContext name into the job data
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        String camelContextName = getCamelContext().getManagementName();
        String endpointUri = getEndpointUri();
        logger.debug("Adding camelContextName={}, endpintUri={} into job data map.", camelContextName, endpointUri);
        jobDataMap.put(QuartzConstants.QUARTZ_CAMEL_CONTEXT_NAME, camelContextName);
        jobDataMap.put(QuartzConstants.QUARTZ_ENDPOINT_URI, endpointUri);
    }

    protected void setProperties(Object bean, Map<String, Object> parameters) throws Exception {
        EndpointHelper.setReferenceProperties(getCamelContext(), bean, parameters);
        EndpointHelper.setProperties(getCamelContext(), bean, parameters);
    }

    protected JobDetail createJobDetail() throws Exception {
        // Camel endpoint timer will assume one to one for JobDetail and Trigger, so let's use same name as trigger
        String name = getTriggerKey().getName();
        String group = getTriggerKey().getGroup();
        Class<? extends Job> jobClass = isStateful() ? StatefulCamelJob.class : CamelJob.class;
        logger.debug("Creating new {}.", jobClass.getSimpleName());

        JobDetail result = JobBuilder.newJob(jobClass)
                .withIdentity(name, group)
                .build();

        // Let user parameters to further set JobDetail properties.
        if (getJobParameters() != null && getJobParameters().size() > 0) {
            logger.debug("Setting user extra jobParameters {}", getJobParameters());
            setProperties(result, getJobParameters());
        }

        logger.debug("Created jobDetail={}", result);
        return result;
    }

    @Override
    protected void doStart() throws Exception {
        if (isDeleteJob() && isPauseJob()) {
            throw new IllegalArgumentException("Cannot have both options deleteJob and pauseJob enabled");
        }

        addJobInScheduler();
    }

    @Override
    protected void doStop() throws Exception {
        removeJobInScheduler();
    }

    public void onConsumerStart(QuartzConsumer quartzConsumer) throws Exception {
        getConsumerLoadBalancer().addProcessor(quartzConsumer.getProcessor());
        if (!jobAdded.get()) {
            addJobInScheduler();
        } else {
            resumeTrigger();
        }
    }

    public void onConsumerStop(QuartzConsumer quartzConsumer) throws Exception {
        getConsumerLoadBalancer().removeProcessor(quartzConsumer.getProcessor());
        if (jobAdded.get()) {
            pauseTrigger();
        }
    }


    private void removeJobInScheduler() throws Exception {
        Scheduler scheduler = getComponent().getScheduler();
        if (scheduler == null) {
            return;
        }

        if (isDeleteJob()) {
            boolean isClustered = scheduler.getMetaData().isJobStoreClustered();
            if (!scheduler.isShutdown() && !isClustered) {
                logger.info("Deleting job {}", getTriggerKey());
                scheduler.unscheduleJob(getTriggerKey());

                jobAdded.set(false);
            }
        } else if (isPauseJob()) {
            boolean isClustered = scheduler.getMetaData().isJobStoreClustered();
            if (!scheduler.isShutdown() && !isClustered) {
                logger.info("Pausing job {}", getTriggerKey());
                scheduler.pauseTrigger(getTriggerKey());

                jobAdded.set(false);
            }
        }

        // Decrement camel job count for this endpoint
        AtomicInteger number = (AtomicInteger) scheduler.getContext().get(QuartzConstants.QUARTZ_CAMEL_JOBS_COUNT);
        if (number != null) {
            number.decrementAndGet();
        }
    }
}
