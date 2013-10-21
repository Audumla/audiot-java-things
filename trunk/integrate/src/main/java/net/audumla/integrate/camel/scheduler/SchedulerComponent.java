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

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.StartupListener;
import org.apache.camel.component.quartz2.QuartzConstants;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.util.IntrospectionSupport;
import org.apache.camel.util.ObjectHelper;
import org.apache.commons.lang.reflect.ConstructorUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.*;

public class SchedulerComponent extends DefaultComponent implements StartupListener {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SchedulerComponent.class);
    private static Map<Class<? extends DefaultSchedulerEndpoint>, Collection<String>> registeredSchedulers;
    private SchedulerFactory schedulerFactory;
    private Scheduler scheduler;
    private Properties properties;
    private String propertiesFile;
    private int startDelayedSeconds;
    private boolean autoStartScheduler = true;
    private boolean prefixJobNameWithEndpointId;

    public SchedulerComponent() {
    }

    public SchedulerComponent(CamelContext camelContext) {
        super(camelContext);
    }

    static public void registerScheduler(Class<? extends DefaultSchedulerEndpoint> clazz, String... params) {
        // if we have a new mapping that matches a previous entry, then remove the existing one to ensure that it is replaced by the new entry.
        List<String> lparams = asList(params);
        for (Map.Entry<Class<? extends DefaultSchedulerEndpoint>, Collection<String>> ps : registeredSchedulers.entrySet()) {
            if (ps.getValue().equals(lparams)) {
                registeredSchedulers.remove(ps.getKey());
            }
        }
        registeredSchedulers.put(clazz, lparams);
    }

    public int getStartDelayedSeconds() {
        return startDelayedSeconds;
    }

    public void setStartDelayedSeconds(int startDelayedSeconds) {
        this.startDelayedSeconds = startDelayedSeconds;
    }

    public boolean isAutoStartScheduler() {
        return autoStartScheduler;
    }

    public void setAutoStartScheduler(boolean autoStartScheduler) {
        this.autoStartScheduler = autoStartScheduler;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getPropertiesFile() {
        return propertiesFile;
    }

    public void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public SchedulerFactory getSchedulerFactory() throws SchedulerException {
        if (schedulerFactory == null) {
            schedulerFactory = createSchedulerFactory();
        }
        return schedulerFactory;
    }

    public void setSchedulerFactory(SchedulerFactory schedulerFactory) {
        this.schedulerFactory = schedulerFactory;
    }

    private SchedulerFactory createSchedulerFactory() throws SchedulerException {
        SchedulerFactory answer;

        Properties prop = loadProperties();
        if (prop != null) {

            // force disabling update checker (will do online check over the internet)
            prop.put("org.quartz.scheduler.skipUpdateCheck", "true");

            answer = new StdSchedulerFactory(prop);
        } else {
            // read default props to be able to use a single scheduler per camel context
            // if we need more than one scheduler per context use setScheduler(Scheduler)
            // or setFactory(SchedulerFactory) methods

            // must use classloader from StdSchedulerFactory to work even in OSGi
            InputStream is = StdSchedulerFactory.class.getClassLoader().getResourceAsStream("org/quartz/quartz.properties");
            if (is == null) {
                throw new SchedulerException("Quartz properties file not found in classpath: org/quartz/quartz.properties");
            }
            prop = new Properties();
            try {
                prop.load(is);
            } catch (IOException e) {
                throw new SchedulerException("Error loading Quartz properties file from classpath: org/quartz/quartz.properties", e);
            }

            // camel context name will be a suffix to use one scheduler per context
            String identity = getCamelContext().getManagementName();

            String instName = prop.getProperty(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME);
            if (instName == null) {
                instName = "scheduler-" + identity;
            } else {
                instName = instName + "-" + identity;
            }
            prop.setProperty(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, instName);

            // force disabling update checker (will do online check over the internet)
            prop.put("org.quartz.scheduler.skipUpdateCheck", "true");

            answer = new StdSchedulerFactory(prop);
        }

        if (logger.isDebugEnabled()) {
            String name = prop.getProperty(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME);
            logger.debug("Creating SchedulerFactory: {} with properties: {}", name, prop);
        }
        return answer;
    }

    /**
     * Is the quartz scheduler clustered?
     */
    public boolean isClustered() throws SchedulerException {
        return getScheduler().getMetaData().isJobStoreClustered();
    }

    private Properties loadProperties() throws SchedulerException {
        Properties answer = getProperties();
        if (answer == null && getPropertiesFile() != null) {
            logger.info("Loading Quartz properties file from classpath: {}", getPropertiesFile());
            InputStream is = getCamelContext().getClassResolver().loadResourceAsStream(getPropertiesFile());
            if (is == null) {
                throw new SchedulerException("Quartz properties file not found in classpath: " + getPropertiesFile());
            }
            answer = new Properties();
            try {
                answer.load(is);
            } catch (IOException e) {
                throw new SchedulerException("Error loading Quartz properties file from classpath: " + getPropertiesFile(), e);
            }
        }
        return answer;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        // Get couple of scheduler settings
        Integer startDelayedSeconds = getAndRemoveParameter(parameters, "startDelayedSeconds", Integer.class);
        if (startDelayedSeconds != null) {
            if (this.startDelayedSeconds != 0 && !(this.startDelayedSeconds == startDelayedSeconds)) {
                logger.warn("A Quartz job is already configured with a different 'startDelayedSeconds' configuration! "
                        + "All Quartz jobs must share the same 'startDelayedSeconds' configuration! Cannot apply the 'startDelayedSeconds' configuration!");
            } else {
                this.startDelayedSeconds = startDelayedSeconds;
            }
        }

        Boolean autoStartScheduler = getAndRemoveParameter(parameters, "autoStartScheduler", Boolean.class);
        if (autoStartScheduler != null) {
            this.autoStartScheduler = autoStartScheduler;
        }

        Boolean prefixJobNameWithEndpointId = getAndRemoveParameter(parameters, "prefixJobNameWithEndpointId", Boolean.class);
        if (prefixJobNameWithEndpointId != null) {
            this.prefixJobNameWithEndpointId = prefixJobNameWithEndpointId;
        }

        // Extract trigger.XXX and job.XXX properties to be set on endpoint below
        Map<String, Object> triggerParameters = IntrospectionSupport.extractProperties(parameters, "trigger.");
        Map<String, Object> jobParameters = IntrospectionSupport.extractProperties(parameters, "job.");

        DefaultSchedulerEndpoint result = null;
        // Create quartz endpoint
        for (Class<? extends DefaultSchedulerEndpoint> clazz : registeredSchedulers.keySet()) {
            Collection<String> params = registeredSchedulers.get(clazz);
            if (parameters.keySet().containsAll(params)) {
                try {
                    result = (DefaultSchedulerEndpoint) ConstructorUtils.invokeConstructor(clazz, new Object[]{uri, this});
                } catch (Exception ex) {
                    logger.error("Failed to create Scheduler", ex);
                }
            }
        }
        if (result == null) {
            result = new SimpleScheduleEndpoint(uri,this);
        }
        TriggerKey triggerKey = createTriggerKey(uri, remaining, result);
        result.setTriggerKey(triggerKey);
        result.setTriggerParameters(triggerParameters);
        result.setJobParameters(jobParameters);
        return result;
    }

    private TriggerKey createTriggerKey(String uri, String remaining, DefaultSchedulerEndpoint endpoint) throws Exception {
        // Parse uri for trigger name and group
        URI u = new URI(uri);
        String path = ObjectHelper.after(u.getPath(), "/");
        String host = u.getHost();

        // host can be null if the uri did contain invalid host characters such as an underscore
        if (host == null) {
            host = ObjectHelper.before(remaining, "/");
            if (host == null) {
                host = remaining;
            }
        }

        // Trigger group can be optional, if so set it to this context's unique name
        String name;
        String group;
        if (ObjectHelper.isNotEmpty(path) && ObjectHelper.isNotEmpty(host)) {
            group = host;
            name = path;
        } else {
            String camelContextName = getCamelContext().getManagementName();
            group = camelContextName == null ? "Camel" : "Camel_" + camelContextName;
            name = host;
        }


        if (prefixJobNameWithEndpointId) {
            name = endpoint.getId() + "_" + name;
        }

        return new TriggerKey(name, group);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        if (scheduler == null) {
            createAndInitScheduler();
        }
    }

    private void createAndInitScheduler() throws SchedulerException {
        logger.info("Create and initializing scheduler.");
        scheduler = createScheduler();

        // Store CamelContext into QuartzContext space
        SchedulerContext quartzContext = scheduler.getContext();
        String camelContextName = getCamelContext().getManagementName();
        logger.debug("Storing camelContextName={} into Quartz Context space.", camelContextName);
        quartzContext.put(QuartzConstants.QUARTZ_CAMEL_CONTEXT + "-" + camelContextName, getCamelContext());

        // Set camel job counts to zero. We needed this to prevent shutdown in case there are multiple Camel contexts
        // that has not completed yet, and the last one with job counts to zero will eventually shutdown.
        AtomicInteger number = (AtomicInteger) quartzContext.get(QuartzConstants.QUARTZ_CAMEL_JOBS_COUNT);
        if (number == null) {
            number = new AtomicInteger(0);
            quartzContext.put(QuartzConstants.QUARTZ_CAMEL_JOBS_COUNT, number);
        }
    }

    private Scheduler createScheduler() throws SchedulerException {
        return getSchedulerFactory().getScheduler();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();

        if (scheduler != null) {
            AtomicInteger number = (AtomicInteger) scheduler.getContext().get(QuartzConstants.QUARTZ_CAMEL_JOBS_COUNT);
            if (number != null && number.get() > 0) {
                logger.info("Cannot shutdown scheduler: " + scheduler.getSchedulerName() + " as there are still " + number.get() + " jobs registered.");
            } else {
                logger.info("Shutting down scheduler. (will wait for all jobs to complete first.)");
                scheduler.shutdown(true);
                scheduler = null;
            }
        }
    }

    @Override
    public void onCamelContextStarted(CamelContext context, boolean alreadyStarted) throws Exception {
        // If Camel has already started and then user add a route dynamically, we need to ensure
        // to create and init the scheduler first.
        if (scheduler == null) {
            createAndInitScheduler();
        }

        // Now scheduler is ready, let see how we should start it.
        if (!autoStartScheduler) {
            logger.info("Not starting scheduler because autoStartScheduler is set to false.");
        } else {
            if (startDelayedSeconds > 0) {
                if (scheduler.isStarted()) {
                    logger.warn("The scheduler has already started. Cannot apply the 'startDelayedSeconds' configuration!");
                } else {
                    logger.info("Starting scheduler with startDelayedSeconds={}", startDelayedSeconds);
                    scheduler.startDelayed(startDelayedSeconds);
                }
            } else {
                if (scheduler.isStarted()) {
                    logger.info("The scheduler has already been started.");
                } else {
                    logger.info("Starting scheduler.");
                    scheduler.start();
                }
            }
        }
    }

}

