package net.audumla.devices.activator;

/**
 * Created with IntelliJ IDEA.
 * User: mgleeson
 * Date: 10/09/13
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Activator {
    void activate();
    void deactivate();
    void addListener(ActivatorListener listener);
    void removeListener(ActivatorListener listener);
    void activate(int seconds,boolean block);
    String getName();

}
