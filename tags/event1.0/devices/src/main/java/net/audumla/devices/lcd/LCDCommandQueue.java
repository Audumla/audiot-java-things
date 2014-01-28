package net.audumla.devices.lcd;

public class LCDCommandQueue {
//
//    private static final Logger logger = Logger.getLogger(RPII2CLCD.class);
//    private static final int DEFAULT_QUEUE_SIZE = 128;
//    private static LCDCommandQueue instance;
//
//    private BlockingQueue<LCDCommand> queue;
//    private AsyncThread thread;
//
//    private LCDCommandQueue() {
//        this.queue = new ArrayBlockingQueue<LCDCommand>(DEFAULT_QUEUE_SIZE);
//    }
//
//    public static LCDCommandQueue instance(LCD lcd) {
//        if (instance == null) {
//            instance = new LCDCommandQueue();
//            instance.start(lcd);
//        }
//        return instance;
//    }
//
//    protected void start(LCD lcd) {
//        thread = new AsyncThread(lcd, queue);
//        append(new LCDInitializeCommand());
//        thread.start();
//    }
//
//    public void stop() {
//        append(new LCDShutdownCommand());
//        try {
//            thread.join();
//        } catch (final InterruptedException ex) {
//            logger.warn("Interrupted while stopping LCD");
//        }
//    }
//
//    public void append(LCDCommand... commands) {
//        for (LCDCommand command : commands) {
//            append(command);
//        }
//        append(new LCDPauseCommand());
//
//    }
//
//    public void append(final LCDCommand command) {
//        if (queue.remainingCapacity() > 0) {
//            try {
//                queue.add(command);
//            } catch (final IllegalStateException ex) {
//                logger.error("LCD queue is full");
//            }
//        } else {
//            logger.error("LCD queue is full");
//        }
//    }
//
//    private class AsyncThread extends Thread {
//
//        private volatile boolean shutdown = false;
//        private final BlockingQueue<LCDCommand> queue;
//        private LCD lcd;
//
//        public AsyncThread(LCD lcd, final BlockingQueue<LCDCommand> queue) {
//            this.queue = queue;
//            this.lcd = lcd;
//        }
//
//        public void run() {
//            while (!shutdown) {
//                try {
//                    do {
//                        LCDCommand s = queue.take();
//                        if (LCDShutdownCommand.class.equals(s.getClass())) {
//                            shutdown();
//                        }
//                        s.execute(lcd);
//                        logger.debug("LCD Command - " + s.getClass().getName());
//                    } while (!shutdown && !queue.isEmpty());
//                } catch (final Exception ex) {
//                    logger.error(ex);
//                }
//            }
//        }
//
//        public void shutdown() {
//            shutdown = true;
//        }
//    }
}