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

public class PeripheralManager {
    public static final int EXCLUSIVE = 1;
    public static final int SHARED = 2;
    public static final int UNSPECIFIED_ID = -1;

    private static Map<Class<?>, Collection<RegistrationListener>> listeners = new HashMap<>();
    private static Collection<PeripheralDescriptor<? extends Peripheral>> peripherals = new ArrayList<>();
    private static Collection<PeripheralProvider> providers = new ArrayList<>();
    private static int idCount = 0;

    static class ReferencedPeripheralDescriptor<P extends Peripheral<? super P>, C extends PeripheralConfig<? super P>> implements PeripheralDescriptor<P> {

        private C configuration;
        private int id;
        private String name;
        private String[] properties;

        ReferencedPeripheralDescriptor(C configuration, int id, String name, String[] properties) {
            this.configuration = configuration;
            this.id = id;
            this.name = name;
            this.properties = properties;
        }

        @Override
        public C getConfiguration() {
            return configuration;
        }

        @Override
        public int getID() {
            return id;
        }

        @Override
        public Class<P> getInterface() {
            return null;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String[] getProperties() {
            return properties;
        }
    }

    PeripheralManager() {
    }

    public static Iterator<PeripheralDescriptor<? extends Peripheral>> list() {
        return peripherals.iterator();
    }

    public static <P extends Peripheral<? super P>> Iterator<PeripheralDescriptor<? extends Peripheral>> list(Class<P> paramClass) throws PeripheralTypeNotSupportedException {
        return peripherals.stream().filter(p -> paramClass.isAssignableFrom(p.getInterface())).iterator();
    }

    public static <P extends Peripheral<? super P>> P open(Class<P> peripheralClass, PeripheralConfig<? super P> config) throws IOException, PeripheralConfigInvalidException, PeripheralTypeNotSupportedException, PeripheralNotFoundException, UnavailablePeripheralException {
        return open(peripheralClass, config,EXCLUSIVE);
    }

    public static <P extends Peripheral<? super P>> P open(Class<P> peripheralClass, PeripheralConfig<? super P> config, int mode) throws IOException, PeripheralConfigInvalidException, PeripheralTypeNotSupportedException, PeripheralNotFoundException, UnavailablePeripheralException, UnsupportedAccessModeException {
        PeripheralProvider<P> p = getProvider(peripheralClass, config.getClass(), null);
        return openPeripheral(p, config, null, mode, UNSPECIFIED_ID, null);
    }

    public static <P extends Peripheral<? super P>> P open(int id) throws IOException, PeripheralNotFoundException, UnavailablePeripheralException {
        return open(id, null, EXCLUSIVE);
    }

    public static <P extends Peripheral<? super P>> P open(int id, Class<P> peripheralClass) throws IOException, PeripheralTypeNotSupportedException, PeripheralNotFoundException, UnavailablePeripheralException {
        return open(id,peripheralClass,EXCLUSIVE);
    }

    public static <P extends Peripheral<? super P>> P open(int id, Class<P> peripheralClass, int mode) throws IOException, PeripheralTypeNotSupportedException, PeripheralNotFoundException, UnavailablePeripheralException, UnsupportedAccessModeException {
        PeripheralProvider<P> p = getProvider(peripheralClass, null, null);
        return openPeripheral(p, null, null, mode, id, null);
    }

    public static <P extends Peripheral<? super P>> P open(int id, int mode) throws IOException, PeripheralNotFoundException, UnavailablePeripheralException, UnsupportedAccessModeException {
        return open(id,null,mode);
    }

    public static <P extends Peripheral<? super P>> P open(PeripheralConfig<? super P> config) throws IOException, PeripheralConfigInvalidException, PeripheralTypeNotSupportedException, PeripheralNotFoundException, UnavailablePeripheralException {
        return open(config,EXCLUSIVE);
    }

    public static <P extends Peripheral<? super P>> P open(PeripheralConfig<? super P> config, int mode) throws IOException, PeripheralConfigInvalidException, PeripheralTypeNotSupportedException, PeripheralNotFoundException, UnavailablePeripheralException, UnsupportedAccessModeException {
        PeripheralProvider<P> p = getProvider(null, config.getClass(), null);
        return openPeripheral(p, config, null, mode, UNSPECIFIED_ID, null);
    }

    public static <P extends Peripheral<? super P>> P open(String name, Class<P> peripheralClass, int mode, String... properties) throws IOException, PeripheralTypeNotSupportedException, PeripheralNotFoundException, UnavailablePeripheralException, UnsupportedAccessModeException {
        PeripheralProvider<P> p = getProvider(peripheralClass, null, properties);
        return openPeripheral(p, null, properties, mode, UNSPECIFIED_ID, name);
    }

    public static <P extends Peripheral<? super P>> P open(String name, Class<P> peripheralClass, String... properties) throws IOException, PeripheralTypeNotSupportedException, PeripheralNotFoundException, UnavailablePeripheralException {
        return open(name,peripheralClass,EXCLUSIVE,properties);
    }

    private static <P extends Peripheral<? super P>> int register(int id, Class<? extends Peripheral> peripheralClass, PeripheralConfig<? super P> config, String name, String[] properties) throws IOException, PeripheralTypeNotSupportedException, PeripheralConfigInvalidException, PeripheralNotFoundException, PeripheralExistsException {
        if (id == UNSPECIFIED_ID) {
            while (peripherals.stream().anyMatch( p -> p.getID() == idCount)) {
                ++idCount;
            }
            id = idCount++;
        }
        else {
            final int i = id;
            if (peripherals.stream().anyMatch( p -> p.getID() == i)) {
                throw new PeripheralExistsException();
            }
        }
        if (config == null ) {
            throw new java.lang.NullPointerException();
        }
        if (id < 0) {
            throw new java.lang.IllegalArgumentException();
        }
        PeripheralProvider<P> provider = getProvider(peripheralClass, config.getClass(), properties);
        openPeripheral(provider,config,properties,EXCLUSIVE,id,name);
        final int i = id;
        peripherals.stream().filter(p -> p.getID() == i).forEach(p -> listeners.entrySet().stream().filter(l -> l.getKey().isAssignableFrom(p.getInterface())).forEach(l -> l.getValue().forEach(n -> n.peripheralRegistered(new RegistrationEvent(p)))));
        return id;
    }

    public static void unregister(int id) {
        peripherals.stream().filter(p -> p.getID() == id).forEach(p -> listeners.entrySet().stream().filter(l -> l.getKey().isAssignableFrom(p.getInterface())).forEach(l -> l.getValue().forEach(n -> n.peripheralUnregistered(new RegistrationEvent(p)))));
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

    protected void registerprovider(Class<? extends Peripheral> clazz, PeripheralProvider provider) {
        providers.add(provider);
    }

    protected static <P extends Peripheral<? super P>, C> PeripheralProvider<P> getProvider(Class<?> peripheralClass, Class<?> configClass, String[] properties) throws PeripheralTypeNotSupportedException {
        PeripheralProvider<P> provider = null;
        if (peripheralClass != null) {
            Optional<PeripheralProvider> found = providers.stream().filter(p -> p.getType().equals(peripheralClass)).findFirst();
            if (found.isPresent()) {
                return found.get();
            }
        }

        if (configClass != null) {
            Optional<PeripheralProvider> found = providers.stream().filter(p -> p.getConfigType().equals(configClass)).findFirst();
            if (found.isPresent()) {
                return found.get();
            }
        }

        if (properties != null) {
            Optional<PeripheralProvider> found = providers.stream().filter(p -> p.matches(properties)).findFirst();
            if (found.isPresent()) {
                return found.get();
            }
        }

        return provider;
    }

    protected static <P extends Peripheral<? super P>> P openPeripheral(PeripheralProvider<P> provider, PeripheralConfig<? super P> config, java.lang.String[] properties, int mode, int id, String name) throws IOException {
        if (provider != null) {
            P p = provider.open(config, properties, mode);
            if (p != null) {
                ReferencedPeripheralDescriptor<P, PeripheralConfig<? super P>> desc = new ReferencedPeripheralDescriptor<P, PeripheralConfig<? super P>>(config,id,name,properties);
                peripherals.add(desc);
                return p;
            }
            throw new PeripheralNotFoundException();
        }
        throw new PeripheralTypeNotSupportedException();
    }

}