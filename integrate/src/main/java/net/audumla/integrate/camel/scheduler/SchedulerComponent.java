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
import org.apache.camel.component.quartz2.QuartzComponent;
import org.apache.camel.util.IntrospectionSupport;
import org.apache.camel.util.ObjectHelper;
import org.apache.commons.lang.reflect.ConstructorUtils;
import org.quartz.TriggerKey;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class SchedulerComponent extends QuartzComponent {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SchedulerComponent.class);
    private Map<Class<? extends DefaultSchedulerEndpoint>, Collection<String>> registeredSchedulers = new HashMap<Class<? extends DefaultSchedulerEndpoint>, Collection<String>>();
    private boolean prefixJobNameWithEndpointId;

    public SchedulerComponent() {
    }

    public SchedulerComponent(CamelContext camelContext) {
        super(camelContext);
    }

    public void registerScheduler(Class<? extends DefaultSchedulerEndpoint> clazz, String... params) {
        // if we have a new mapping that matches a previous entry, then remove the existing one to ensure that it is replaced by the new entry.
        List<String> lparams = asList(params);
        for (Map.Entry<Class<? extends DefaultSchedulerEndpoint>, Collection<String>> ps : registeredSchedulers.entrySet()) {
            if (ps.getValue().equals(lparams)) {
                registeredSchedulers.remove(ps.getKey());
            }
        }
        registeredSchedulers.put(clazz, lparams);
    }



     @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        // Get couple of scheduler settings
        Integer startDelayedSeconds = getAndRemoveParameter(parameters, "startDelayedSeconds", Integer.class);
        if (startDelayedSeconds != null) {
            if (getStartDelayedSeconds() != 0 && !(getStartDelayedSeconds() == startDelayedSeconds)) {
                logger.warn("A Quartz job is already configured with a different 'startDelayedSeconds' configuration! "
                        + "All Quartz jobs must share the same 'startDelayedSeconds' configuration! Cannot apply the 'startDelayedSeconds' configuration!");
            } else {
                setStartDelayedSeconds(startDelayedSeconds);
            }
        }

        Boolean autoStartScheduler = getAndRemoveParameter(parameters, "autoStartScheduler", Boolean.class);
        if (autoStartScheduler != null) {
            setAutoStartScheduler(autoStartScheduler);
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



}

