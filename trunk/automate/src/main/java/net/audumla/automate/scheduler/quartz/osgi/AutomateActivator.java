package net.audumla.automate.scheduler.quartz.osgi;
/**
 * User: audumla
 * Date: 8/09/13
 * Time: 6:29 PM
 */

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

public class AutomateActivator implements BundleActivator {
    private static final Logger logger = Logger.getLogger(AutomateActivator.class);
    private Scheduler scheduler;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            logger.info("Audumla Irrigation - started");

        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        try {
            if (scheduler != null) {
                scheduler.shutdown();
                logger.info("Audumla Irrigation - stopped");
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
    }
}
