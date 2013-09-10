/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.irrigation;

import net.audumla.climate.ClimateObserver;
import net.audumla.climate.ClimateObserverCollection;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class IrrigationZone implements Zone {
    private Logger logger = Logger.getLogger(IrrigationZone.class);
    private double shadeRating;
    private double enclosureRating;
    private double coverRating;
    private double surfaceArea;
    private double flowRate;
    private List<IrrigationEvent> irrigationEvents = new ArrayList<IrrigationEvent>();
    private ClimateObserver observer;
    private IrrigationEventHandler eventHandler;


    public IrrigationZone(ClimateObserver observer) {
        if (observer instanceof ClimateObserverCollection) {
            ClimateObserverCollection ao = (ClimateObserverCollection) observer;
            ao.addClimateObserverTop(new ZonedClimateObserver(observer.getSource(), this));
        } else {
            logger.error("Cannot zone observer");
        }
        this.observer = observer;
    }

    public void setEventHandler(IrrigationEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public ClimateObserver getClimateObserver() {
        return observer;
    }


    public double getShadeRating() {
        return shadeRating;
    }

    public void setShadeRating(double shadeRating) {
        this.shadeRating = shadeRating;
    }

    public double getEnclosureRating() {
        return enclosureRating;
    }

    public void setEnclosureRating(double enclosureRating) {
        this.enclosureRating = enclosureRating;
    }

    public List<Crop> getCrops() {
        return null;
    }

    public double getCoverRating() {
        return coverRating;
    }

    public void setCoverRating(double coverRating) {
        this.coverRating = coverRating;
    }

    @Override
    public List<IrrigationEvent> getIrrigationEventsForDay(Date day) {
        return irrigationEvents.stream().filter(e -> DateUtils.isSameDay(e.getEventTime(), day)).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void addIrrigationEvent(IrrigationEvent event) {
        irrigationEvents.add(event);
        eventHandler.handleEvent( event);
    }

    public double getSurfaceArea() {
        return surfaceArea;
    }

    public void setSurfaceArea(double surfaceArea) {
        this.surfaceArea = surfaceArea;
    }

    public double getFlowRate() {
        return flowRate;
    }

    public void setFlowRate(double flowRate) {
        this.flowRate = flowRate;
    }
}
