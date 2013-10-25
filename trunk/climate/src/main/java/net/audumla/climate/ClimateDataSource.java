package net.audumla.climate;

/*
 * *********************************************************************
 *  ORGANIZATION : audumla.net
 *  More information about this project can be found at the following locations:
 *  http://www.audumla.net/
 *  http://audumla.googlecode.com/
 * *********************************************************************
 *  Copyright (C) 2012 - 2013 Audumla.net
 *  Licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *  You may not use this file except in compliance with the License located at http://creativecommons.org/licenses/by-nc-nd/3.0/
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 *  "AS I BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

import net.audumla.astronomical.Geolocation;

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