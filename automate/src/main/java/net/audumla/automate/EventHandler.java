package net.audumla.automate;

import net.audumla.bean.BeanUtils;

/**
 * User: mgleeson
 * Date: 10/09/13
 * Time: 2:48 PM
 */
public interface EventHandler {

    default String getName() {
        return BeanUtils.generateName(EventHandler.class);
    }

    void handleEvent(Event event);

}
