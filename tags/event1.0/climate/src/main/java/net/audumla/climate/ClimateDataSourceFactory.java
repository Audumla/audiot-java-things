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
import net.audumla.astronomy.Geolocation;
import net.audumla.bean.BeanUtils;
import net.audumla.bean.SafeParse;
import net.audumla.climate.bom.BOMDataLoader;

import org.apache.commons.io.IOUtils;

import org.json.JSONArray;
import org.json.JSONObject;


import java.io.Reader;

/**
 * Class description
 *
 * @author         Marius Gleeson    
 */
public class ClimateDataSourceFactory {

    /**
     * Method description
     *
     *
     * @return
     */
    static public ClimateDataSourceFactory getInstance() {
        return new ClimateDataSourceFactory();
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public ClimateDataSource newInstance() {
        ClimateDataSource source = BeanUtils.buildBean(ClimateDataSource.class);

        return BeanUtils.buildBeanDecorator(new ClimateDataSourceDecorator(source), source);
    }

    /**
     * Method description
     *
     *
     * @param clazz
     * @param <T>
     *
     * @return
     */
    public static <T extends ClimateDataSource> T newInstance(Class<T> clazz) {
        T source = BeanUtils.buildBean(clazz);

        return BeanUtils.buildBeanDecorator(new ClimateDataSourceDecorator(source), source);
    }

    /**
     * Method description
     *
     *
     * @param source
     *
     * @return
     */
    public static ClimateDataSource decorateInstance(ClimateDataSource source) {
        return BeanUtils.buildBeanDelegator(source);
    }

    /**
     * Method description
     *
     *
     * @param source
     * @param wrapper
     * @param <T>
     *
     * @return
     */
    public static <T extends ClimateDataSource> T decorateInstance(ClimateDataSource source, T wrapper) {
        return BeanUtils.buildBeanDelegator(source, wrapper);
    }

    /**
     * Method description
     *
     *
     * @param source
     * @param clazz
     * @param <T>
     *
     * @return
     */
    public static <T extends ClimateDataSource> T decorateInstance(ClimateDataSource source, Class<T> clazz) {
        T wrapper = BeanUtils.buildBean(clazz);

        return (T) BeanUtils.buildBeanDelegator(source, wrapper);
    }

    /**
     * Class description
     *
     * @author         Marius Gleeson
     */
    static public class ClimateDataSourceDecorator extends Geolocation.Location implements Comparable<ClimateDataSource> {
        protected ClimateDataSource source;

        protected ClimateDataSourceDecorator(ClimateDataSource source) {
            this.source = source;
        }

        /**
         * Method description
         *
         *
         * @param o
         *
         * @return
         */
        public int compareTo(ClimateDataSource o) {
            try {
                if ((o.getLatitude() == source.getLatitude()) && (o.getLongitude() == source.getLongitude()) && (source.getLatitude() != 0)
                    && (source.getLongitude() != 0)) {
                    return 0;
                }
            } catch (UnsupportedOperationException ignored) {}

            if ((o.getId().length() > 0) && (source.getId().length() > 0)) {
                return source.getId().compareTo(o.getId());
            }

            return -1;
        }

        /**
         * Method description
         *
         *
         * @param o
         *
         * @return
         */
        public boolean equals(Object o) {
            assert o instanceof ClimateDataSource;

            return compareTo((ClimateDataSource) o) == 0;
        }

        /**
         * Method description
         *
         *
         * @return
         */
        @Override
        public String toString() {
            return source.toString();
        }

        /**
         * Method description
         *
         *
         * @return
         */
        public Double getElevation() {

            // need to move this out to a seperate observer
            try {

                Double e = super.getElevation();
                if (e == null) {
                    throw new UnsupportedOperationException();
                }
                return e;
            } catch (Throwable ex) {
                try {
                    double elevation = Double.MAX_VALUE;
                    String url = "/maps/api/elevation/json?locations=" + super.getLatitude() + "," + super.getLongitude()
                                 + "&sensor=false";
                    Reader reader = BOMDataLoader.instance().getData(BOMDataLoader.HTTP, "maps.googleapis.com", url);
                    JSONObject json = new JSONObject(IOUtils.toString(reader));
                    JSONArray results = json.getJSONArray("results");

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject data = results.getJSONObject(i);

                        elevation = SafeParse.parseDouble(data.get("elevation"));
                    }

                    if (elevation != Double.MAX_VALUE) {
                        source.setElevation(elevation);
                    }

                    return elevation;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            throw new UnsupportedOperationException();
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
