package net.audumla.concurrent;

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

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.concurrent.Callable;

public class TemplatedExecuter<T> extends InLineExecuter {
    private static final Logger logger = LoggerFactory.getLogger(TemplatedExecuter.class);
    private T protoType;

    public TemplatedExecuter() {
    }

    public TemplatedExecuter(T protoType) {
        this.protoType = protoType;
    }

    public T getTemplate() {
        return protoType;
    }

    public void setTemplate(T protoType) {
        this.protoType = protoType;
    }

    protected void mergeProperties(Object source, Object destination) throws IntrospectionException {
        BeanInfo info = Introspector.getBeanInfo(source.getClass());
        for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
            try {
                // get the property value from the destination object.
                Object destvalue = BeanUtils.getProperty(destination, descriptor.getName());
                // only copy the value if the destination has not been set. This will therefore only work on non primitive types
                if (destvalue == null) {
                    Object sourceValue = descriptor.getReadMethod().invoke(source);
                    // Only copy values values where the destination values is null
                    if (sourceValue != null) {
                        BeanUtils.copyProperty(destination, descriptor.getName(), sourceValue);
                    }
                }


            } catch (Exception ignored) {

            }
        }
    }

    public Object execute(Callable command) throws Exception {
        mergeProperties(getTemplate(), command);
        return super.execute((Callable<?>) command);
    }
}
