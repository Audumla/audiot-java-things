package net.audumla.climate;

import net.audumla.util.Geolocation;

import java.util.Date;

public interface ClimateDataSource extends Geolocation, Comparable<ClimateDataSource> {

    public enum ClimateDataSourceType {DAILY_STATISTICAL, MONTHLY_STATISTICAL, DAILY_OBSERVATION, PERIODIC_OBSERVATION, DAILY_FORECAST, AGGREGATE, DERIVED}

    String getName();

    void setName(String name);

    String getId();

    void setId(String id);

    Date getFirstRecord();

    void setFirstRecord(Date firstRecord);

    Date getLastRecord();

    void setLastRecord(Date lastRecord);

    Date getState();

    void setState(String state);

    ClimateDataSourceType getType();

    void setType(ClimateDataSourceType type);

    ClimateObserver getClimateObserver();

    void setClimateObserver(ClimateObserver observer);
}