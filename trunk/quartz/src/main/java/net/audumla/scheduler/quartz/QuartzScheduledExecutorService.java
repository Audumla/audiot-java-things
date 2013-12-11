package net.audumla.scheduler.quartz;

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

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class QuartzScheduledExecutorService extends AbstractExecutorService implements ScheduledExecutorService {
    private static final Logger logger = LoggerFactory.getLogger(QuartzScheduledExecutorService.class);
    private Scheduler scheduler;
    private static long jobCount = 0;

    public QuartzScheduledExecutorService() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            logger.error("Instantiation error",e);
        }
    }


    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new QuartzRunnableFuture<T>(runnable,value,this.scheduler,generateName());
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new QuartzRunnableFuture<T>(callable,this.scheduler,generateName());
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        QuartzRunnableFuture<Object> rf = new QuartzRunnableFuture<Object>(command,null,this.scheduler,delay,unit,generateName());
        rf.run();
        return rf;
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        RunnableScheduledFuture<V> rf = new QuartzRunnableFuture<V>(callable,this.scheduler,delay,unit,generateName());
        rf.run();
        return rf;
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        RunnableScheduledFuture<Object> rf = new QuartzRunnableFuture<Object>(command,null,this.scheduler,initialDelay,unit,generateName());
        rf.run();
        return rf;
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        RunnableScheduledFuture<Object> rf = new QuartzRunnableFuture<Object>(command,null,this.scheduler,delay,unit,generateName());
        rf.run();
        return rf;
    }

    @Override
    public void shutdown() {
        try {
            scheduler.shutdown(true);
        } catch (SchedulerException e) {
            logger.error("Error shutting down",e);
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        try {
            scheduler.shutdown(false);
        } catch (SchedulerException e) {
            logger.error("Error shutting down",e);
        }
        return null;
    }

    @Override
    public boolean isShutdown() {
        try {
            return scheduler.isShutdown();
        } catch (SchedulerException e) {
            logger.error("Error determining shutting state", e);
        }
        return true;
    }

    @Override
    public boolean isTerminated() {
        return isShutdown();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void execute(Runnable command) {
        command.run();
    }

    protected String generateName() {
        return "job"+(jobCount++);

    }
}
