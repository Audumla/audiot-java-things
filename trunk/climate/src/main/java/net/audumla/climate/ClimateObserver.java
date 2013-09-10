package net.audumla.climate;

import java.util.Date;


public interface ClimateObserver {
    ClimateData getClimateData(Date date);
    ClimateDataSource getSource();
    boolean supportsDate(Date date);

}
