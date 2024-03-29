package net.audumla.camel.typeconverter;

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
import org.apache.camel.TypeConversionException;
import org.apache.camel.support.TypeConverterSupport;
import org.codehaus.jackson.map.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

public class BeanToCamelMessageConverter extends TypeConverterSupport {
    private static final Logger logger = LoggerFactory.getLogger(BeanToCamelMessageConverter.class);


    public <T> T convertTo(Class<T> tClass, Exchange exchange, Object o) throws TypeConversionException {
        try {
            BeanInfo info = Introspector.getBeanInfo(o.getClass());
            for (PropertyDescriptor desc : info.getPropertyDescriptors()) {
                exchange.getIn().setHeader("bean:"+desc.getName(),desc.getReadMethod().invoke(o).toString());
            }
            return (T) JSONBeanConverter.mapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new TypeConversionException(o,tClass,e);
        }
    }
}
