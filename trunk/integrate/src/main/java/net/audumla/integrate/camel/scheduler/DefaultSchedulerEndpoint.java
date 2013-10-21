package net.audumla.integrate.camel.scheduler;

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

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.Route;
import org.apache.camel.component.quartz2.*;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.processor.loadbalancer.LoadBalancer;
import org.apache.camel.processor.loadbalancer.RoundRobinLoadBalancer;
import org.apache.camel.util.EndpointHelper;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class DefaultSchedulerEndpoint extends DefaultEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(DefaultSchedulerEndpoint.class);
    private TriggerKey triggerKey;
    private LoadBalancer consumerLoadBalancer;
    private Map<String, Object> jobParameters;
    private boolean stateful = false;
    private Map<String, Object> triggerParameters;
    private boolean deleteJob = true;
    private boolean pauseJob;
    /**
     * In case of scheduler has already started, we want the trigger start slightly after current time to
     * ensure endpoint is fully started before the job kicks in.
     */
    private long triggerStartDelay = 500; // in millis second
    // An internal variables to track whether a job has been in scheduler or not, and has it paused or not.
    private AtomicBoolean jobAdded = new AtomicBoolean(false);
    private AtomicBoolean jobPaused = new AtomicBoolean(false);

    public DefaultSchedulerEndpoint(String uri, SchedulerComponent component) {
        super(uri, component);
    }

    public boolean isStateful() {
        return stateful;
    }

    public void setStateful(boolean stateful) {
        this.stateful = stateful;
    }

    public Map<String, Object> getJobParameters() {
        return jobParameters;
    }

    public void setJobParameters(Map<String, Object> jobParameters) {
        this.jobParameters = jobParameters;
    }

    public Map<String, Object> getTriggerParameters() {
        return triggerParameters;
    }

    public void setTriggerParameters(Map<String, Object> triggerParameters) {
        this.triggerParameters = triggerParameters;
    }

    public long getTriggerStartDelay() {
        return triggerStartDelay;
    }

    public void setTriggerStartDelay(long triggerStartDelay) {
        this.triggerStartDelay = triggerStartDelay;
    }

    public boolean isDeleteJob() {
        return deleteJob;
    }

    public void setDeleteJob(boolean deleteJob) {
        this.deleteJob = deleteJob;
    }

    public boolean isPauseJob() {
        return pauseJob;
    }

    public void setPauseJob(boolean pauseJob) {
        this.pauseJob = pauseJob;
    }

    public LoadBalancer getConsumerLoadBalancer() {
        if (consumerLoadBalancer == null) {
            consumerLoadBalancer = new RoundRobinLoadBalancer();
        }
        return consumerLoadBalancer;
    }

    public void setConsumerLoadBalancer(LoadBalancer consumerLoadBalancer) {
        this.consumerLoadBalancer = consumerLoadBalancer;
    }

    public TriggerKey getTriggerKey() {
        return triggerKey;
    }

    public void setTriggerKey(TriggerKey triggerKey) {
        this.triggerKey = triggerKey;
    }

    @Override
    public Producer createProducer() throws Exception {
        throw new UnsupportedOperationException("Quartz producer is not supported.");
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        QuartzConsumer result = new QuartzConsumer(this, processor);
        configureConsumer(result);
        return result;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    protected void doStop() throws Exception {
        removeJobInScheduler();
    }

    private void removeJobInScheduler() throws Exception {
        Scheduler scheduler = getComponent().getScheduler();
        if (scheduler == null) {
            return;
        }

        if (deleteJob) {
            boolean isClustered = scheduler.getMetaData().isJobStoreClustered();
            if (!scheduler.isShutdown() && !isClustered) {
                logger.info("Deleting job {}", triggerKey);
                scheduler.unscheduleJob(triggerKey);

                jobAdded.set(false);
            }
        } else if (pauseJob) {
            boolean isClustered = scheduler.getMetaData().isJobStoreClustered();
            if (!scheduler.isShutdown() && !isClustered) {
                logger.info("Pausing job {}", triggerKey);
                scheduler.pauseTrigger(triggerKey);

                jobAdded.set(false);
            }
        }

        // Decrement camel job count for this endpoint
        AtomicInteger number = (AtomicInteger) scheduler.getContext().get(QuartzConstants.QUARTZ_CAMEL_JOBS_COUNT);
        if (number != null) {
            number.decrementAndGet();
        }
    }

    private void addJobInScheduler() throws Exception {
        // Add or use existing trigger to/from scheduler
        Scheduler scheduler = getComponent().getScheduler();
        JobDetail jobDetail;
        Trigger trigger = scheduler.getTrigger(triggerKey);
        if (trigger == null) {
            Date startTime = new Date();
            if (getComponent().getScheduler().isStarted()) {
                startTime = new Date(System.currentTimeMillis() + triggerStartDelay);
            }
            jobDetail = createJobDetail();
            trigger = createTrigger(startTime);

            if (triggerParameters != null && triggerParameters.size() > 0) {
                logger.debug("Setting user extra triggerParameters {}", triggerParameters);
                setProperties(trigger, triggerParameters);
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

    private void ensureNoDupTriggerKey() {
        for (Route route : getCamelContext().getRoutes()) {
            if (route.getEndpoint() instanceof QuartzEndpoint) {
                QuartzEndpoint quartzEndpoint = (QuartzEndpoint) route.getEndpoint();
                TriggerKey checkTriggerKey = quartzEndpoint.getTriggerKey();
                if (triggerKey.equals(checkTriggerKey)) {
                    throw new IllegalArgumentException("Trigger key " + triggerKey + " is already in used by " + quartzEndpoint);
                }
            }
        }
    }

    private void updateJobDataMap(JobDetail jobDetail) {
        // Store this camelContext name into the job data
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        String camelContextName = getCamelContext().getManagementName();
        String endpointUri = getEndpointUri();
        logger.debug("Adding camelContextName={}, endpintUri={} into job data map.", camelContextName, endpointUri);
        jobDataMap.put(QuartzConstants.QUARTZ_CAMEL_CONTEXT_NAME, camelContextName);
        jobDataMap.put(QuartzConstants.QUARTZ_ENDPOINT_URI, endpointUri);
    }

    @Override
    public SchedulerComponent getComponent() {
        return (SchedulerComponent) super.getComponent();
    }

    public void pauseTrigger() throws Exception {
        if (jobPaused.get()) {
            return;
        }
        jobPaused.set(true);

        Scheduler scheduler = getComponent().getScheduler();
        if (scheduler != null && !scheduler.isShutdown()) {
            logger.info("Pausing trigger {}", triggerKey);
            scheduler.pauseTrigger(triggerKey);
        }
    }

    public void resumeTrigger() throws Exception {
        if (!jobPaused.get()) {
            return;
        }
        jobPaused.set(false);

        Scheduler scheduler = getComponent().getScheduler();
        if (scheduler != null) {
            logger.info("Resuming trigger {}", triggerKey);
            scheduler.resumeTrigger(triggerKey);
        }
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

    private void setProperties(Object bean, Map<String, Object> parameters) throws Exception {
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
        if (jobParameters != null && jobParameters.size() > 0) {
            logger.debug("Setting user extra jobParameters {}", jobParameters);
            setProperties(result, jobParameters);
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
}
