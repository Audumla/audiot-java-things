package net.audumla.climate.bom;

import net.audumla.climate.ClimateDataSource;

/**
 * User: audumla
 * Date: 23/07/13
 * Time: 5:38 PM
 */
public interface BOMClimateDataSource extends ClimateDataSource {
    String getBOMSampleID();
    void setBOMSampleID(String id);
}
