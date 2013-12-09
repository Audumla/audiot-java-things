package net.audumla.camel;

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
 *  "AS IS BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

import org.apache.camel.Exchange;
import org.apache.camel.support.TypeConverterSupport;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class StringToMapTypeConverter extends TypeConverterSupport {
    private static final Logger logger = Logger.getLogger(StringToMapTypeConverter.class);

    @SuppressWarnings("unchecked")
    public <T> T convertTo(Class<T> type, Exchange exchange, Object value) {
        Map<Object,Object> map = new HashMap<Object,Object>();
        map.put("Message",value.toString());
        return (T) map;
    }
}
