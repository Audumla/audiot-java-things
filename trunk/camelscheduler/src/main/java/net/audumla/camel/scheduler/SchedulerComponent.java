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

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.quartz2.QuartzComponent;
import org.apache.camel.util.IntrospectionSupport;
import org.apache.camel.util.ObjectHelper;
import org.apache.commons.lang.reflect.ConstructorUtils;
import org.quartz.TriggerKey;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class SchedulerComponent extends QuartzComponent {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SchedulerComponent.class);
    private Map<Class<? extends DefaultSchedulerEndpoint>, Map<String, String>> registeredSchedulers = new HashMap<Class<? extends DefaultSchedulerEndpoint>, Map<String, String>>();
    private boolean prefixJobNameWithEndpointId;

    public SchedulerComponent() {
    }

    public SchedulerComponent(CamelContext camelContext) {
        super(camelContext);
    }

    public void registerScheduler(Class<? extends DefaultSchedulerEndpoint> clazz) {
        try {
            Method m = clazz.getDeclaredMethod("getParameters");
            registeredSchedulers.put(clazz, (Map<String, String>) m.invoke(null));
        } catch (Exception ex) {
            logger.error("Cannot register scheduler '{}'", clazz, ex);
        }
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


        DefaultSchedulerEndpoint result = null;
        // Create quartz endpoint
        for (Class<? extends DefaultSchedulerEndpoint> clazz : registeredSchedulers.keySet()) {
            Map<String, String> params = registeredSchedulers.get(clazz);
            Set<String> subset = new TreeSet<String>(parameters.keySet());
            // see if any of the parameters match with a scheduler.
            subset.retainAll(params.keySet());
            if (subset.size() > 0) {
                boolean found = true;
                for (String key : subset) {
                    String value = params.get(key);
                    if (value != null) {
                        Pattern pattern = Pattern.compile(value);
                        if (!pattern.matcher(parameters.get(key).toString()).find()) {
                            found = false;
                            break;
                        }
                    }

                }
                if (found) {
                    try {
                        result = (DefaultSchedulerEndpoint) ConstructorUtils.invokeConstructor(clazz, new Object[]{uri, this});
                    } catch (Exception ex) {
                        logger.error("Failed to create Scheduler - {}", uri, ex);
                        return null;
                    }
                }
            }
        }
        if (result == null) {
            result = new SimpleScheduleEndpoint(uri, this);
        }
        // Extract [trigger].XXX and job.XXX properties to be set on endpoint below
        Map<String, Object> triggerParameters = IntrospectionSupport.extractProperties(parameters, result.getParameterPrefix() + ".");
        Map<String, Object> jobParameters = IntrospectionSupport.extractProperties(parameters, "job.");

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

