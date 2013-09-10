package net.audumla.irrigation.controller;
/**
 * User: audumla
 * Date: 23/08/13
 * Time: 9:30 AM
 */

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Controller implements Runnable {
    private static final Logger logger = Logger.getLogger(Controller.class);
    private boolean continueExecution = true;
    private ApplicationContext context;

    public static void main(String args[]) {
        Controller controller = new Controller();
        controller.initialize();
        controller.run();
    }

    public void initialize() {
        context = new ClassPathXmlApplicationContext("irrigation.xml");
    }

    public void stop() {
        continueExecution = false;
    }

    @Override
    public void run() {
        Scheduler scheduler = null;
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            while (continueExecution) {
                synchronized (this) {
                    this.wait(5000);
                }
            }

        } catch (Exception e) {
            logger.error(e);
        } finally {
            try {
                if (scheduler != null) {
                    scheduler.shutdown();
                }
            } catch (Exception ex) {
                logger.error(ex);
            }
        }
    }
}
