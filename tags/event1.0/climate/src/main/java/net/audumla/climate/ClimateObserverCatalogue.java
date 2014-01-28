package net.audumla.climate;

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
 *  "AS I BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

import net.audumla.climate.bom.BOMClimateObserverCatalogue;

import java.util.*;

public class ClimateObserverCatalogue implements ClimateObserverFactory {
    protected static ClimateObserverCatalogue instance = new ClimateObserverCatalogue();
    protected Map<String, ClimateObserver> stationMap = new HashMap<String, ClimateObserver>();
    protected List<ClimateObserverFactoryListener> listeners = new ArrayList<ClimateObserverFactoryListener>();
    protected List<ClimateObserverFactory> factoryList = new ArrayList<ClimateObserverFactory>();

    protected ClimateObserverCatalogue() {
        registerClimateObserverFactory(new BOMClimateObserverCatalogue());
        addClimateObserverFactoryListener(new ClimateObserverFactoryListener() {
            @Override
            public void climateObserverCreated(ClimateObserver observer) {
                if (observer instanceof ClimateObserverCollection) {
                    ClimateObserverCollection ao = (ClimateObserverCollection) observer;
                    ao.addClimateObserverTail(new DerivedClimateObserver(ClimateDataSourceFactory.decorateInstance(observer.getSource())));
                }
            }
        });
    }

    static public ClimateObserverCatalogue getInstance() {
        return instance;
    }

    public ClimateObserver getRegisteredClimateObserver(ClimateDataSource source) {
        return stationMap.get(source.getId());
    }

    synchronized public ClimateObserver getClimateObserver(ClimateDataSource source) {
        ClimateObserver observer = getRegisteredClimateObserver(source);
        if (observer == null) {
            Iterator<ClimateObserverFactory> it = factoryList.iterator();

            while ((observer == null) && (it.hasNext())) {
                ClimateObserverFactory factory = it.next();
                observer = factory.getClimateObserver(source);
            }
            if (observer != null) {
                final ClimateObserver o = observer;
                listeners.stream().forEach(l -> l.climateObserverCreated(o));
            }
        }
        return observer;
    }

    public ClimateObserverFactoryListener addClimateObserverFactoryListener(ClimateObserverFactoryListener listener) {
        listeners.add(listener);
        return listener;
    }

    public ClimateObserverFactoryListener removeClimateObserverFactoryListener(ClimateObserverFactoryListener listener) {
        listeners.remove(listener);
        return listener;
    }

    public ClimateObserver registerClimateObserver(ClimateDataSource source, ClimateObserver station) {
        try {
            if (source.getId().length() > 0) {
                return stationMap.put(source.getId(), station);
            }
        } catch (Exception ignored) {

        }
        return null;
    }

    public void registerClimateObserverFactory(ClimateObserverFactory factory) {
        factoryList.add(factory);
    }
}
