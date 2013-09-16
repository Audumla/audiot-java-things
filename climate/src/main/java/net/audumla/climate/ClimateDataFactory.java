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

import net.audumla.bean.BeanUtils;
import net.audumla.spacetime.Time;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.ClassUtils;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * A factory for creating ClimateData objects.
 */
public interface ClimateDataFactory {


    Class<? extends ClimateData> getClimateDataClass();

    Class<? extends ClimateObservation> getClimateObservationClass();

    /**
     * Gets the climate data.
     *
     * @param dws the dws
     * @return the climate data
     */
    public static WritableClimateData newWritableClimateData(ClimateDataFactory dws, ClimateDataSource source) {
        WritableClimateData bean = BeanUtils.buildBean(WritableClimateData.class, dws.getClimateDataClass());
        WritableClimateData data = BeanUtils.buildBeanDecorator(new WritableClimateDataDecorator(bean), bean);
        data.setDataSource(source);
        return data;
    }

    /**
     * Replaces the climate data with a readonly version.
     *
     * @param cd the existing climate data bean
     * @return the climate data
     */
    public static ClimateData convertToReadOnlyClimateData(ClimateData cd) {
        if (cd == null) {
            return null;
        }
        Set<Class<?>> interfaces = new LinkedHashSet<Class<?>>();
        interfaces.addAll(ClassUtils.getAllInterfaces(cd.getClass()));
        interfaces.remove(WritableClimateData.class);
        return BeanUtils.convertBean(cd, interfaces.toArray(new Class<?>[interfaces.size()]));
    }

    /**
     * Replaces the climate observation with a readonly version.
     *
     * @param cd the existing climate data bean
     * @return the climate data
     */
    public static WritableClimateObservation convertToWritableClimateObservation(ClimateObservation cd) {
        if (cd == null) {
            return null;
        }
        Set<Class<?>> interfaces = new LinkedHashSet<Class<?>>();
        interfaces.addAll(ClassUtils.getAllInterfaces(cd.getClass()));
        return BeanUtils.convertBean(cd, WritableClimateObservation.class, interfaces.toArray(new Class<?>[interfaces.size()]));
    }

    /**
     * Replaces the climate data with a writable version.
     *
     * @param cd the existing climate data bean
     * @return the climate data
     */
    public static WritableClimateData convertToWritableClimateData(ClimateData cd) {
        if (cd == null) {
            return null;
        }
        Set<Class<?>> interfaces = new LinkedHashSet<Class<?>>();
        interfaces.addAll(ClassUtils.getAllInterfaces(cd.getClass()));
        return BeanUtils.convertBean(cd, WritableClimateData.class, interfaces.toArray(new Class<?>[interfaces.size()]));
    }

    /**
     * Replaces the climate observation with a readonly version.
     *
     * @param cd the existing climate data bean
     * @return the climate data
     */
    public static ClimateObservation convertToReadOnlyClimateObservation(ClimateObservation cd) {
        if (cd == null) {
            return null;
        }
        Set<Class<?>> interfaces = new LinkedHashSet<Class<?>>();
        interfaces.addAll(ClassUtils.getAllInterfaces(cd.getClass()));
        interfaces.remove(WritableClimateObservation.class);
        return BeanUtils.convertBean(cd, interfaces.toArray(new Class<?>[interfaces.size()]));
    }

    /**
     * Gets the climate observation.
     *
     * @param dws the dws
     * @return the climate observation
     */
    public static WritableClimateObservation newWritableClimateObservation(ClimateDataFactory dws, ClimateDataSource source) {
        WritableClimateObservation bean = BeanUtils.buildBean(WritableClimateObservation.class, dws.getClimateObservationClass());
        WritableClimateObservation obs = BeanUtils.buildBeanDecorator(new ClimateObservationDecorator(bean), bean);
        obs.setDataSource(source);
        return obs;
    }

    /**
     * The Class ClimateObservationDecorator.
     */
    public static class ClimateObservationDecorator {

        private ClimateObservation bean;

        protected ClimateObservationDecorator(ClimateObservation bean) {
            this.bean = bean;
        }

        /**
         * Gets the previous observation.
         *
         * @return the previous observation
         */
        public ClimateObservation getPreviousObservation() {
            return bean.getObservationSet().lower(bean);
        }

        /**
         * Gets the next observation.
         *
         * @return the next observation
         */
        public ClimateObservation getNextObservation() {
            ClimateObservation obs = bean.getObservationSet().higher(bean);

            if (obs == null) {
                // we have hit the end of the list for this ClimateData instance.
                // we now need to access the original observer and request an observation that is later than where we are now
                ClimateObserver observer = bean.getDataSource().getClimateObserver();
                Date nextTime = DateUtils.addDays(bean.getTime(), 1);
                nextTime = DateUtils.setHours(nextTime, 0);
                nextTime = DateUtils.setMinutes(nextTime, 0);
                ClimateData cd = observer.getClimateData(nextTime);
                obs = cd.getObservation(nextTime, ClimateData.ObservationMatch.SUBSEQUENT);
            }

            return obs;
        }

        public double getRainfallSince(ClimateObservation previousObservation) {
            return 0.0;
        }


    }

    /**
     * The Class ClimateDataDecorator.
     */
    public static class WritableClimateDataDecorator extends ClimateDataDecorator<WritableClimateData> {

        protected WritableClimateDataDecorator(WritableClimateData bean) {
            super(bean);
        }

        /**
         * Adds the observation.
         *
         * @param obs the obs
         */
        public void addObservation(WritableClimateObservation obs) {
            NavigableSet<ClimateObservation> observations = bean.getObservations();
            if (observations == null) {
                observations = new ConcurrentSkipListSet<ClimateObservation>((o1, o2) -> o1.getTime().compareTo(o2.getTime()));
                bean.setObservations(observations);
            }
            obs.setObservationSet(observations);
            observations.add(ClimateDataFactory.convertToReadOnlyClimateObservation(obs));
        }

        @Override
        public String toString() {
            return super.bean.toString();
        }
    }

    public static class ClimateDataDecorator<Data extends ClimateData> {
        protected Data bean;

        protected ClimateDataDecorator(Data bean) {
            this.bean = bean;
        }

        /**
         * Gets the observation.
         *
         * @param time the time
         * @return the observation that is closest to the given time. Only observerations that are later or equal to the time will be returned
         */
        public ClimateObservation getObservation(Date time, ClimateData.ObservationMatch match) {

            long diff = Long.MAX_VALUE;
            ClimateObservation closest = null;
            if (bean.getObservations() != null) {
                for (ClimateObservation obs : bean.getObservations()) {
                    if (!Time.hasYear(obs.getTime())) {
                        time = Time.setNullYear(time);
                    }
                    if (!Time.hasDay(obs.getTime())) {
                        time = Time.setNullDay(time);
                    }

                    long obsOffset = obs.getTime().getTime();
                    long timeOffset = time.getTime();

                    if ((match.equals(ClimateData.ObservationMatch.SUBSEQUENT) && obsOffset >= timeOffset) ||
                            (match.equals(ClimateData.ObservationMatch.PREVIOUS) && obsOffset <= timeOffset) ||
                            (match.equals(ClimateData.ObservationMatch.CLOSEST))) {
                        long newDiff = Math.abs(obsOffset - timeOffset);
                        if (newDiff < diff) {
                            diff = newDiff;
                            closest = obs;
                        }
                    }
                }
            }
            if (closest != null) {
                return closest;
            } else {
                return null;
                //throw new UnsupportedOperationException();
            }
        }

    }

}
