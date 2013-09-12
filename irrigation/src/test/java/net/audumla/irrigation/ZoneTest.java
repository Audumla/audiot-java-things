/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.irrigation;
/**
 * User: audumla
 * Date: 24/07/13
 * Time: 11:20 PM
 */


import net.audumla.automate.ActivatorEventHandler;
import net.audumla.climate.*;
import net.audumla.devices.activator.ActivatorMock;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class ZoneTest {
    private static final Logger logger = Logger.getLogger(ZoneTest.class);

    @Test
    public void testSuccessfulFixedEvent() throws Exception {
        ActivatorEventHandler handler = new ActivatorEventHandler();
        handler.setActivator(new ActivatorMock(true, true));

        IrrigationZone zone = new IrrigationZone();
        zone.setEventHandler(handler);
        zone.addIrrigationEvent(new Date(), 2);
        synchronized (this) {
            this.wait(2100);
        }
    }

    @Test
    public void testZoneCalculations() {
        ClimateDataSource source = new ClimateDataSourceFactory().newInstance();
        source.setLatitude(-37.84);
        source.setLongitude(144.98);
        ClimateObserver observer = ClimateObserverCatalogue.getInstance().getClimateObserver(source);

        IrrigationZone zone = new IrrigationZone(observer);
        zone.setFlowRate(0.1);
        zone.setSurfaceArea(4.0);
        zone.setCoverRating(1);
        zone.setEnclosureRating(1);
        zone.setShadeRating(1);

        ClimateData data = zone.getClimateObserver().getClimateData(new Date());
        Assert.assertEquals(data.getRainfall(), 0, 0);
        Assert.assertEquals(data.getSolarRadiation(), 0, 0);
        Assert.assertEquals(data.getAverageWindSpeed(), 0, 0);
        Assert.assertEquals(data.getRainfall(), 0, 0);


    }

}
