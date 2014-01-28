package net.audumla.devices.activator;

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

import org.apache.camel.CamelContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ActivatorTopicTest {
    private static final Logger logger = LoggerFactory.getLogger(ActivatorTopicTest.class);

    @Autowired
    protected CamelContext camelContext;

    @Before
    public void setUp() throws Exception {
//        camelContext.getTypeConverterRegistry().addTypeConverter(Callable.class, String.class, new StringToBeanConverter());
//        camelContext.getTypeConverterRegistry().addTypeConverter(String.class, Callable.class, new BeanToStringConverter());
    }

    @Test
    public void testActivator() throws Exception {

        synchronized (this) {
            this.wait(20000);
        }

    }
}
