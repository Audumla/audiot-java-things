package net.audumla.automate;

import net.audumla.bean.BeanUtils;

/**
 * User: mgleeson
 * JulianDate: 10/09/13
 * Time: 2:48 PM
 */
public interface EventHandler {

    default String getName() {
        return BeanUtils.generateName(EventHandler.class);
    }

    boolean handleEvent(Event event);

}
