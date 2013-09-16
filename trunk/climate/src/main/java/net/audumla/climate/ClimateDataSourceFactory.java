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
import net.audumla.climate.bom.BOMDataLoader;
import net.audumla.bean.SafeParse;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Reader;

/**
 * The Class ClimateDataSourceDecorator.
 */
public class ClimateDataSourceFactory {

    static public ClimateDataSourceFactory getInstance() {
        return new ClimateDataSourceFactory();
    }

    public ClimateDataSource newInstance() {
        ClimateDataSource source = BeanUtils.buildBean(ClimateDataSource.class);
        return BeanUtils.buildBeanDecorator(new ClimateDataSourceDecorator(source), source);
    }

    public static <T extends ClimateDataSource> T newInstance(Class<T> clazz) {
        T source = BeanUtils.buildBean(clazz);
        return BeanUtils.buildBeanDecorator(new ClimateDataSourceDecorator(source), source);
    }

    public static ClimateDataSource decorateInstance(ClimateDataSource source) {
        return BeanUtils.buildBeanDelegator(source);
    }

    public static <T extends ClimateDataSource> T decorateInstance(ClimateDataSource source, T wrapper) {
        return BeanUtils.buildBeanDelegator(source, wrapper);
    }

    public static <T extends ClimateDataSource> T decorateInstance(ClimateDataSource source, Class<T> clazz) {
        T wrapper = BeanUtils.buildBean(clazz);
        return (T) BeanUtils.buildBeanDelegator(source, wrapper);
    }

    static public class ClimateDataSourceDecorator implements Comparable<ClimateDataSource> {
        protected ClimateDataSource source;

        protected ClimateDataSourceDecorator(ClimateDataSource source) {
            this.source = source;
        }

        public int compareTo(ClimateDataSource o) {
            try {
                if (o.getLatitude() == source.getLatitude() && o.getLongitude() == source.getLongitude() && source.getLatitude() != 0
                        && source.getLongitude() != 0) {
                    return 0;
                }
            } catch (UnsupportedOperationException ignored) {
            }
            if (o.getId().length() > 0 && source.getId().length() > 0) {
                return source.getId().compareTo(o.getId());
            }
            return -1;
        }

        public boolean equals(Object o) {
            return compareTo((ClimateDataSource) o) == 0;
        }

        @Override
        public String toString() {
            return source.toString();
        }

        public double getElevation() {
            // need to move this out to a seperate observer
            try {
                return source.getElevation();
            } catch (Exception ex) {
                try {
                    double elevation = Double.MAX_VALUE;
                    String url = "/maps/api/elevation/json?locations=" + source.getLatitude() + "," + source.getLongitude() + "&sensor=false";
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
