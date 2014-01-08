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

import net.audumla.bean.BeanUtils;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

public class CamelScheduledExecutorService extends AbstractExecutorService implements ScheduledExecutorService {
    private static final Logger logger = LoggerFactory.getLogger(CamelScheduledExecutorService.class);
    private CamelContext context;
    private String to;
    private SchedulerURIGenerator schedulerURIGenerator = new QuartzSchedulerURIGenerator();

    public CamelScheduledExecutorService(CamelContext context) {
        this.context = context;
    }

    public CamelScheduledExecutorService() {
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
//        return createRoute(Executors.callable(command),unit.toMillis(delay));
        return null;
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
//        return createRoute(callable,unit.toMillis(delay));
        return null;
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
//        return createRoute(Executors.callable(command),unit.toMillis(delay));
        return null;
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
//        return createRoute(Executors.callable(command),unit.toMillis(delay),initialDelay);
        return null;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void execute(Runnable command) {
        createRoute(command,0);
    }

    public CamelContext getContext() {
        return context;
    }

    public void setContext(CamelContext context) {
        this.context = context;
    }


    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }


    public CamelScheduledExecutorService.SchedulerURIGenerator getSchedulerURIGenerator() {
        return schedulerURIGenerator;
    }

    public void setSchedulerURIGenerator(CamelScheduledExecutorService.SchedulerURIGenerator schedulerURIGenerator) {
        this.schedulerURIGenerator = schedulerURIGenerator;
    }

    protected void createRoute(Object message,long milliseconds){
        RouteBuilder builder = new RouteBuilder() {
            public void configure() {
                from(schedulerURIGenerator.generateURI(milliseconds)).
                        bean(message).
                        to(to);
            }
        };
        try {
            getContext().addRoutes(builder);
        } catch (Exception e) {
            logger.error("Unable to create route",e);
        }
    }

    public interface SchedulerURIGenerator {
        String generateURI(long delayInMilliSeconds);
    }

    public static class QuartzSchedulerURIGenerator implements SchedulerURIGenerator {
        private String componentName = "audumlaScheduler";

        @Override
        public String generateURI(long delayInMilliSeconds) {
            return componentName+="://+"+BeanUtils.generateName(getClass())+"?trigger.repeatInterval="+delayInMilliSeconds;

        }
    }
}
