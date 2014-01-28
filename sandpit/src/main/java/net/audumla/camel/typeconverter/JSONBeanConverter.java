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

import net.audumla.automate.event.activator.ActivatorCommand;
import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Callable;

@Converter
public class JSONBeanConverter {
    private static final Logger logger = LoggerFactory.getLogger(JSONBeanConverter.class);
    static protected ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
        mapper.disable(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES);
        mapper.enableDefaultTyping(); // defaults for defaults (see below); include as wrapper-array, non-concrete types
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT); // all non-final types
    }

    @Converter
    public static String convertCallableToString(Callable o) throws IOException {
        return JSONBeanConverter.mapper.writeValueAsString(o);
    }

    @Converter
    public static Callable convertStringToObject(String string, Exchange exchange) throws IOException {
        Callable callable = JSONBeanConverter.mapper.readValue(string, new TypeReference<Callable>() {
        });
        //exchange.getFromEndpoint().getEndpointUri()


        return callable;
    }

    @Converter
    public static ActivatorCommand convertStringToActivatorCommand(String string, Exchange exchange) throws IOException {
        ActivatorCommand callable = JSONBeanConverter.mapper.readValue(string, new TypeReference<ActivatorCommand>() {
        });
        //exchange.getFromEndpoint().getEndpointUri()


        return callable;
    }
}
