package com.oracle.deviceaccess;

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

import java.io.IOException;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class PeripheralManager {
    public static final int EXCLUSIVE = 1;
    public static final int SHARED = 2;
    public static final int UNSPECIFIED_ID = -1;

    private static Map<Class<?>, Collection<RegistrationListener>> listeners = new HashMap<>();
    private static Map<Class<? extends Peripheral>, PeripheralFactory<? extends Peripheral>> factories = new HashMap<>();
    private static Collection<PeripheralDescriptor<? extends Peripheral>> peripherals = new ArrayList<>();

    protected interface PeripheralFactory<P extends Peripheral<? super P>> {

        public <P extends Peripheral<? super P>> ReferencedPeripheralDescriptor<P, ? extends PeripheralConfig<? super P>> open(Integer mode, Integer id, java.lang.Class<P> intf, PeripheralConfig<? super P> config, java.lang.String name, java.lang.String... properties);

        static class ReferencedPeripheralDescriptor<P extends Peripheral<? super P>, Z extends PeripheralConfig<? super P>> implements PeripheralDescriptor<P> {

            private Z configuration;
            private Class<P> clazz;
            private int id;
            private String name;
            private String[] properties;
            private P peripheral;

            @Override
            public Z getConfiguration() {
                return configuration;
            }

            @Override
            public int getID() {
                return id;
            }

            @Override
            public Class<P> getInterface() {
                return clazz;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String[] getProperties() {
                return properties;
            }

            public P getPeripheral() {
                return peripheral;
            }
        }

    }

    PeripheralManager() {
    }

    public static Iterator<PeripheralDescriptor<? extends Peripheral>> list() {
        return peripherals.iterator();
    }

    public static <P extends Peripheral<? super P>> Iterator<PeripheralDescriptor<? extends Peripheral>> list(Class<P> paramClass) throws PeripheralTypeNotSupportedException {
        return peripherals.stream().filter(p -> paramClass.isAssignableFrom(p.getInterface())).collect(Collectors.toList()).iterator();
    }

    public static <P extends Peripheral<? super P>> P open(Class<P> paramClass, PeripheralConfig<? super P> paramPeripheralConfig) throws IOException, PeripheralConfigInvalidException, PeripheralTypeNotSupportedException, PeripheralNotFoundException, UnavailablePeripheralException {
        PeripheralFactory<? extends Peripheral> factory = factories.get(paramClass);
        if (factory != null) {
            PeripheralFactory.ReferencedPeripheralDescriptor<P, ? extends PeripheralConfig<? super P>> p = factory.open(EXCLUSIVE, UNSPECIFIED_ID, paramClass, paramPeripheralConfig, "");
            peripherals.add(p);
            return p.getPeripheral();
        }
        throw new PeripheralTypeNotSupportedException();
    }

    public static <P extends Peripheral<? super P>> P open(Class<P> paramClass, PeripheralConfig<? super P> paramPeripheralConfig, int paramInt) throws IOException, PeripheralConfigInvalidException, PeripheralTypeNotSupportedException, PeripheralNotFoundException, UnavailablePeripheralException, UnsupportedAccessModeException {
        PeripheralFactory<? extends Peripheral> factory = factories.get(paramClass);
        if (factory != null) {
            PeripheralFactory.ReferencedPeripheralDescriptor<P, ? extends PeripheralConfig<? super P>> p = factory.open(EXCLUSIVE, paramInt, paramClass, paramPeripheralConfig, "");
            peripherals.add(p);
            return p.getPeripheral();
        }
        throw new PeripheralTypeNotSupportedException();
    }

    public static <P extends Peripheral<? super P>> P open(int id) throws IOException, PeripheralNotFoundException, UnavailablePeripheralException {
        return open(id,EXCLUSIVE);
    }

    public static <P extends Peripheral<? super P>> P open(int paramInt, Class<P> paramClass) throws IOException, PeripheralTypeNotSupportedException, PeripheralNotFoundException, UnavailablePeripheralException {
        PeripheralFactory<? extends Peripheral> factory = factories.get(paramClass);
        if (factory != null) {
            PeripheralFactory.ReferencedPeripheralDescriptor<P, ? extends PeripheralConfig<? super P>> p = factory.open(EXCLUSIVE, paramInt, paramClass, null, "");
            peripherals.add(p);
            return p.getPeripheral();
        }
        throw new PeripheralTypeNotSupportedException();
    }

    public static <P extends Peripheral<? super P>> P open(int id, Class<P> paramClass, int mode) throws IOException, PeripheralTypeNotSupportedException, PeripheralNotFoundException, UnavailablePeripheralException, UnsupportedAccessModeException {
        PeripheralFactory<? extends Peripheral> factory = factories.get(paramClass);
        if (factory != null) {
            PeripheralFactory.ReferencedPeripheralDescriptor<P, ? extends PeripheralConfig<? super P>> p = factory.open(mode, id, paramClass, null, "");
            peripherals.add(p);
            return p.getPeripheral();
        }
        throw new PeripheralTypeNotSupportedException();
    }

    public static <P extends Peripheral<? super P>> P open(int id, int mode) throws IOException, PeripheralNotFoundException, UnavailablePeripheralException, UnsupportedAccessModeException {
        for (PeripheralDescriptor d : peripherals) {
            if (d.getID() == id) {
                PeripheralFactory<? extends Peripheral> factory = factories.get(d.getInterface());
                if (factory != null) {
                    PeripheralFactory.ReferencedPeripheralDescriptor<P, ? extends PeripheralConfig<? super P>> p = factory.open(mode, id, d.getInterface(), d.getConfiguration(), d.getName(), d.getProperties());
                    peripherals.add(p);
                    return p.getPeripheral();
                }
                throw new PeripheralTypeNotSupportedException();
            }
        }
        throw new PeripheralNotFoundException();
    }

    public static <P extends Peripheral<? super P>> P open(PeripheralConfig<? super P> config) throws IOException, PeripheralConfigInvalidException, PeripheralTypeNotSupportedException, PeripheralNotFoundException, UnavailablePeripheralException {
        PeripheralFactory<? extends Peripheral> factory = factories.get(config.getPeripheralClass());
        if (factory != null) {
            PeripheralFactory.ReferencedPeripheralDescriptor<P, ? extends PeripheralConfig<? super P>> p = factory.open(EXCLUSIVE, UNSPECIFIED_ID, null, config, "");
            peripherals.add(p);
            return p.getPeripheral();
        }
        throw new PeripheralTypeNotSupportedException();
    }

    public static <P extends Peripheral<? super P>> P open(PeripheralConfig<? super P> config, int mode) throws IOException, PeripheralConfigInvalidException, PeripheralTypeNotSupportedException, PeripheralNotFoundException, UnavailablePeripheralException, UnsupportedAccessModeException {
        PeripheralFactory<? extends Peripheral> factory = factories.get(config.getPeripheralClass());
        if (factory != null) {
            PeripheralFactory.ReferencedPeripheralDescriptor<P, ? extends PeripheralConfig<? super P>> p = factory.open(mode, UNSPECIFIED_ID, null, config, "");
            peripherals.add(p);
            return p.getPeripheral();
        }
        throw new PeripheralTypeNotSupportedException();
    }

    public static <P extends Peripheral<? super P>> P open(String name, Class<P> paramClass, int mode, String... properties) throws IOException, PeripheralTypeNotSupportedException, PeripheralNotFoundException, UnavailablePeripheralException, UnsupportedAccessModeException {
        PeripheralFactory<? extends Peripheral> factory = factories.get(paramClass);
        if (factory != null) {
            PeripheralFactory.ReferencedPeripheralDescriptor<P, ? extends PeripheralConfig<? super P>> p = factory.open(mode, UNSPECIFIED_ID, paramClass, null, name, properties);
            peripherals.add(p);
            return p.getPeripheral();
        }
        throw new PeripheralTypeNotSupportedException();
    }

    public static <P extends Peripheral<? super P>> P open(String name, Class<P> paramClass, String... properties) throws IOException, PeripheralTypeNotSupportedException, PeripheralNotFoundException, UnavailablePeripheralException {
        PeripheralFactory<? extends Peripheral> factory = factories.get(paramClass);
        if (factory != null) {
            PeripheralFactory.ReferencedPeripheralDescriptor<P, ? extends PeripheralConfig<? super P>> p = factory.open(EXCLUSIVE, UNSPECIFIED_ID, paramClass, null, name, properties);
            peripherals.add(p);
            return p.getPeripheral();
        }
        throw new PeripheralTypeNotSupportedException();
    }

    public static <P extends Peripheral<? super P>> int register(int id, Class<P> paramClass, PeripheralConfig<? super P> config, String paramString, String... properties) throws IOException, PeripheralTypeNotSupportedException, PeripheralConfigInvalidException, PeripheralNotFoundException, PeripheralExistsException {
        PeripheralFactory<? extends Peripheral> factory = factories.get(paramClass);
        if (factory != null) {
            PeripheralFactory.ReferencedPeripheralDescriptor<P, ? extends PeripheralConfig<? super P>> p = factory.open(EXCLUSIVE, id, paramClass, config, "", properties);
            peripherals.add(p);
            return p.getID();
        }
        throw new PeripheralTypeNotSupportedException();
    }

    public static void unregister(int paramInt) {
    }

    public static <P extends Peripheral<? super P>> void addRegistrationListener(RegistrationListener<P> paramRegistrationListener, Class<P> paramClass) {
        Collection<RegistrationListener> ll = listeners.get(paramClass);
        if (ll == null) {
            ll = new ArrayList<>();
            listeners.put(paramClass, ll);
        }
        ll.add(paramRegistrationListener);
    }

    public static <P extends Peripheral<? super P>> void removeRegistrationListener(RegistrationListener<P> paramRegistrationListener, Class<P> paramClass) {
        Collection<RegistrationListener> ll = listeners.get(paramClass);
        if (ll != null) {
            ll.remove(paramRegistrationListener);
        }
    }

    protected void registerFactory(Class<? extends Peripheral> clazz, PeripheralFactory factory) {
        factories.put(clazz, factory);
    }

}