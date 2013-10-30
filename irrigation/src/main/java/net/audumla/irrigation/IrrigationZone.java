/*
 * Copyright (c) Audumla Technologies 2013.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

package net.audumla.irrigation;

import net.audumla.automate.Event;
import net.audumla.automate.EventHandler;
import net.audumla.bean.BeanUtils;
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
    private Double surfaceArea;
    private Double flowRate;
    private List<Event> irrigationEvents = new ArrayList<Event>();
    private ClimateObserver observer;
    private EventHandler eventHandler;
    private Double depthRate;
    private String name = BeanUtils.generateName(Zone.class);

    public IrrigationZone(ClimateObserver observer) {
        if (observer instanceof ClimateObserverCollection) {
            ClimateObserverCollection ao = (ClimateObserverCollection) observer;
            ao.addClimateObserverTop(new ZonedClimateObserver(observer.getSource(), this));
        } else {
            logger.error("Cannot zone observer");
        }
        this.observer = observer;
    }

    public IrrigationZone() {
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the irrigated depth in mm over a given duration
     */
    public static double calculateIrrigatedDepth(Zone zone, long seconds) {
        double litres = seconds * zone.getFlowRate();
        double volume = litres / 1000;
        double depth = (volume / zone.getSurfaceArea()) * 1000;
        return depth;
    }

    /**
     * @return the number of seconds required to irrigate to the given depth
     */
    public static long calculateIrrigationDuration(Zone zone, double depth) {
        double area = zone.getSurfaceArea(); // m2
        double volume = (depth / 1000) * area; //m3
        double litres = volume * 1000; // litres
        long duration = (long) (litres / zone.getFlowRate()); //seconds
        return duration;
    }

    public void setDepthRate(double depthRate) {
        this.depthRate = depthRate;
    }

    public void setObserver(ClimateObserver observer) {
        this.observer = observer;
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public ClimateObserver getClimateObserver() {
        return observer;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getShadeRating() {
        return shadeRating;
    }

    public void setShadeRating(double shadeRating) {
        this.shadeRating = shadeRating;
    }

    @Override
    public double getEnclosureRating() {
        return enclosureRating;
    }

    public void setEnclosureRating(double enclosureRating) {
        this.enclosureRating = enclosureRating;
    }

    @Override
    public List<Crop> getCrops() {
        return null;
    }

    @Override
    public double getCoverRating() {
        return coverRating;
    }

    public void setCoverRating(double coverRating) {
        this.coverRating = coverRating;
    }

    @Override
    public List<Event> getIrrigationEventsForDay(Date day) {
        return irrigationEvents.stream().filter(e -> DateUtils.isSameDay(e.getEventStartTime(), day)).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean handleEvent(Event event) {
        irrigationEvents.add(event);
        if (eventHandler != null) {
            return eventHandler.handleEvent(event);
        } else {
            logger.error("Event Handler not set for [" + getName() + "]");
            return false;
        }
    }

    /*
    @Override
    public void addIrrigationEvent(JulianDate when, int seconds) {
        AtomicSchedule timer = new AtomicSchedule(null);
        timer.setSeconds(seconds);
        timer.setStartTime(when);
        timer.setHandler(this);
        timer.setEnabled(true);
    }
    */

    @Override
    public double getSurfaceArea() {
        return surfaceArea;
    }

    public void setSurfaceArea(double surfaceArea) {
        this.surfaceArea = surfaceArea;
    }

    @Override
    public double getFlowRate() {
        return flowRate;
    }

    @Override
    public double getDepthRate() {
        return depthRate;
    }

    public void setFlowRate(double flowRate) {
        this.flowRate = flowRate;
    }


}
