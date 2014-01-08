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

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.Callable;

public abstract class AbstractActivatorCommand implements ActivatorCommand {
    private static final Logger logger = LoggerFactory.getLogger(AbstractActivatorCommand.class);
    protected Activator activator;
    protected Set<ActivatorListener> alisteners = new HashSet<ActivatorListener>();

    public AbstractActivatorCommand() {
    }

    public AbstractActivatorCommand(Activator activator, ActivatorListener... listeners) {
        this.activator = activator;
        this.alisteners.addAll(Arrays.asList(listeners));
    }

    @Override
    public void setActivator(Activator activator) {
        this.activator = activator;
    }

    @Override
    public Activator getActivator() {
        return activator;
    }

    @Override
    public ActivatorListener[] getListeners() {
        return alisteners.toArray(new ActivatorListener[alisteners.size()]);
    }

    @Override
    public void setListeners(ActivatorListener[] listeners) {
        alisteners.addAll(Arrays.asList(listeners));
    }

    @Override
    public void addListener(ActivatorListener listener) {
        alisteners.add(listener);
    }

    @Override
    public void removeListener(ActivatorListener listener) {
        alisteners.remove(listener);

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            return BeanUtils.cloneBean(this);
        } catch (Exception e) {
            logger.error("Unable to clone Activator Command {}",this.getClass().getName(),e);
            throw new CloneNotSupportedException("Failed to clone Activator Command");
        }
    }
}
