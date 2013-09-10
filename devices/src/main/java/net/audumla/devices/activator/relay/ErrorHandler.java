package net.audumla.devices.activator.relay;

/**
 * User: audumla
 * Date: 10/09/13
 * Time: 9:49 PM
 */
public interface ErrorHandler {
    void handleError(String message, Exception ex);

}
