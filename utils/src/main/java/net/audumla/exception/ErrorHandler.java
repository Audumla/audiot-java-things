package net.audumla.exception;

/**
 * User: audumla
 * Date: 10/09/13
 * Time: 9:49 PM
 */

@FunctionalInterface
public interface ErrorHandler {
    void handleError(String message, Exception ex);

}
