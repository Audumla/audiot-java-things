package net.audumla.climate;

/**
 * User: audumla
 * Date: 24/07/13
 * Time: 5:06 PM
 */
public interface ClimateObserverCollection {
    void addClimateObserverTail(ClimateObserver station);

    void addClimateObserverTop(ClimateObserver station);

    public ClimateObserver buildClimateObserver();
}
