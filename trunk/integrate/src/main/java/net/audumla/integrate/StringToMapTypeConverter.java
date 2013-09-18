package net.audumla.integrate;

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
