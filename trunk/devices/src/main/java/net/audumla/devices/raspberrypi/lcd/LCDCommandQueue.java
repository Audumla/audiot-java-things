package net.audumla.devices.raspberrypi.lcd;

import org.apache.log4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class LCDCommandQueue {

    private static final Logger logger = Logger.getLogger(LCD.class);
    private static final int DEFAULT_QUEUE_SIZE = 128;
    private static LCDCommandQueue instance;

    private BlockingQueue<LCDCommand> queue;
    private AsynchThread thread;

    private LCDCommandQueue() {
        this.queue = new ArrayBlockingQueue<LCDCommand>(DEFAULT_QUEUE_SIZE);
    }

    public static LCDCommandQueue instance() {
        if (instance == null) {
            instance = new LCDCommandQueue();
            instance.start();
        }
        return instance;
    }

    protected void start() {
        thread = new AsynchThread(queue);
        append(new LCDInititializeCommand());
        thread.start();
    }

    protected void stop() {
        append(new LCDShutdownCommand());
        try {
            thread.join();
        } catch (final InterruptedException ex) {
            logger.warn("Interrupted while stopping LCD");
        }
    }

    public void append(LCDCommand... commands) {
        for (LCDCommand command : commands) {
            append(command);
        }
        append(new LCDPauseCommand());

    }

    public void append(final LCDCommand command) {
        if (queue.remainingCapacity() > 0) {
            try {
                queue.add(command);
            } catch (final IllegalStateException ex) {
                logger.error("LCD queue is full");
            }
        } else {
            logger.error("LCD queue is full");
        }
    }

    private class AsynchThread extends Thread {

        private volatile boolean shutdown = false;
        private final BlockingQueue<LCDCommand> queue;

        public AsynchThread(final BlockingQueue<LCDCommand> queue) {
            this.queue = queue;
        }

        public void run() {
            while (!shutdown) {
                try {
                    do {
                        LCDCommand s = queue.take();
                        if (LCDShutdownCommand.class.equals(s.getClass())) {
                            shutdown();
                        }
                        s.execute(LCD.instance());
                        logger.debug("LCD Command - " + s.getClass().getName());
                    } while (!shutdown && !queue.isEmpty());
                } catch (final Exception ex) {
                    logger.error(ex);
                }
            }
        }

        public void shutdown() {
            shutdown = true;
        }
    }
}