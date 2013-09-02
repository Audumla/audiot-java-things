package net.audumla.climate;

/**
 * User: audumla
 * Date: 22/07/13
 * Time: 7:16 PM
 */
public interface ClimateObserverFactory {

    ClimateObserver getClimateObserver(ClimateDataSource source);

}
