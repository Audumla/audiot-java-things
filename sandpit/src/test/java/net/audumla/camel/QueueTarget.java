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

import org.apache.camel.Body;
import org.apache.camel.Consume;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueTarget {
    private static final Logger logger = LoggerFactory.getLogger(QueueTarget.class);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name = "Test";

    @Consume(uri="activemq:queue:test")
    public void getMessageDoSomething(@Header("JMSCorrelationID") String correlationID, @Body Object body) {
        logger.debug("Output Queue Message - {}",body);
    }

    public Object enrich(Object body) {
        logger.debug("Enrich Queue Message - {}",body);
//        return "Duration 5";
        return "Enriched - "+body;
    }

}
