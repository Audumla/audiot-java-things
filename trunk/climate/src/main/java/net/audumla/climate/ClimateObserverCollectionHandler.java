package net.audumla.climate;

import net.audumla.bean.BeanUtils;
import net.audumla.bean.BeanUtils.BeanProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class ClimateObserverCollectionHandler implements InvocationHandler, ClimateObserver, ClimateObserverCollection {
    private static final Logger logger = LogManager.getLogger(ClimateObserverCollectionHandler.class);
    private final ClimateDataSource source;
    private Deque<ClimateObserver> baseClimateObserverList = new ArrayDeque<ClimateObserver>();

    public ClimateObserverCollectionHandler(ClimateDataSource source) {
        this.source = source;
        this.source.setClimateObserver(this);
        this.source.setType(ClimateDataSource.ClimateDataSourceType.AGGREGATE);
    }

    @Override
    public void addClimateObserverTail(ClimateObserver station) {
        baseClimateObserverList.addLast(station);
    }

    @Override
    public void addClimateObserverTop(ClimateObserver station) {
        baseClimateObserverList.addFirst(station);
    }

    public ClimateObserver buildClimateObserver() {
        return (ClimateObserver) Proxy.newProxyInstance(ClimateObserver.class.getClassLoader(), new Class[]{ClimateObserver.class, ClimateObserverCollection.class}, this);
    }

    @Override
    public ClimateData getClimateData(Date date) {
        AggregateClimateDataHandler handler = new AggregateClimateDataHandler(date, baseClimateObserverList);
        ClimateData instance = (ClimateData) Proxy.newProxyInstance(ClimateObserver.class.getClassLoader(), new Class[]{ClimateData.class}, handler);
        /*
        for (ClimateObserver item : baseClimateObserverList) {
            if (item instanceof ClimateObserverClassDefinition) {
                ClimateObserverClassDefinition dws = (ClimateObserverClassDefinition) item;
                if (dws.supportsDate(date)) {
                    handler.addValidObserver(item);
                }
            } else {
                handler.addValidObserver(item);
            }
        }
        */
        return instance;
    }

    public ClimateDataSource getSource() {
        return source;
    }

    @Override
    public boolean supportsDate(Date date) {
        for (ClimateObserver observerSource : baseClimateObserverList) {
            if (observerSource.supportsDate(date)) {
                return true;
            }
        }
        return false;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            Method nMethod = this.getClass().getMethod(method.getName(), method.getParameterTypes());
            return nMethod.invoke(this, args);
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private static class AggregateClimateDataHandler implements InvocationHandler {
        private final Collection<ClimateObserver> validObservers;
        private final Date date;
        private final Collection<ClimateObserver> recursiveSources = new HashSet<ClimateObserver>();
        private ClimateDataSource source;

        public AggregateClimateDataHandler(Date date) {
            this.date = date;
            this.validObservers = new ArrayList<ClimateObserver>();
        }

        public AggregateClimateDataHandler(Date date, Collection<ClimateObserver> observers) {
            this.date = date;
            this.validObservers = observers;
        }

        public void addValidObserver(ClimateObserver source) {
            validObservers.add(source);
        }

        public Date getTime() {
            // override to ensure that underlying data objects are not loaded just to retrieve the time which we already have here.
            return date;
        }

        public ClimateDataSource getDataSource() {
            // override to ensure that underlying data objects are not loaded just to retrieve the data source that we can access via the outer observer instance.
            if (source != null) {
                return source;
            }
            for (ClimateObserver source : validObservers) {
                try {
                    return source.getSource();
                } catch (Exception ignored) {

                }
            }
            throw new UnsupportedOperationException();
        }

        synchronized public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            try {
                // if the invoker has a direct implementation of the method
                Method nMethod = this.getClass().getMethod(method.getName(), method.getParameterTypes());
                return nMethod.invoke(this, args);
            } catch (Exception ex) {
                UnsupportedOperationException defaultEx = null;
                for (ClimateObserver observerSource : validObservers) {
                    try {
                        // only call the source if it does not exist in the recursive list, otherwise we enter an endless loop
                        if (observerSource != null && !recursiveSources.contains(observerSource)) {
                            Object data = null;
                            if (observerSource instanceof ClimateDataFactory) {
                                Class<?> clazz = ((ClimateDataFactory) observerSource).getClimateDataClass();
                                if (BeanUtils.checkIsSuppored(clazz, method, false)) {
                                    if (observerSource.supportsDate(date)) {
                                        data = observerSource.getClimateData(date);
                                    }
                                }
                            } else {
                                data = observerSource.getClimateData(date);
                            }
                            if (data != null) {
                                try {
                                    // add the source that we are calling to the recursive list so that we can skip it if we end up in a recursive call
                                    recursiveSources.add(observerSource);
                                    if (data instanceof BeanProxy) {
                                        ((BeanProxy) data).setDelegator(proxy);
                                    }
                                    return method.invoke(data, args);
                                } finally {
                                    // always remove the recursive instance
                                    recursiveSources.remove(observerSource);
                                    source = observerSource.getSource();
                                }
                            }
                        }
                    } catch (UnsupportedOperationException e) {
                        defaultEx = e;

                    } catch (InvocationTargetException e) {
                        if (!(e.getCause() instanceof UnsupportedOperationException)) {
                             throw e.getCause();
                        }
                    }
                }
                if (defaultEx != null) {
                    throw defaultEx;
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        }

    }
}
