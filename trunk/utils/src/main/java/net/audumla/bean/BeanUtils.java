package net.audumla.bean;

import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * The Class BeanUtils.
 */
public class BeanUtils {

    public static long count;

    /**
     * Builds the bean delegator.
     *
     * @param <Bean> the generic type
     * @param bean   the bean
     * @return the bean
     */
    @SuppressWarnings("unchecked")
    public static <Bean, Wrapper extends Bean> Wrapper buildBeanDelegator(Bean bean, Wrapper wrapper) {
        InvocationHandler handler = new BeanDelegatorHandler<Bean>(bean, wrapper);
        Set<Class<?>> interfaces = new LinkedHashSet<Class<?>>();
        interfaces.addAll(ClassUtils.getAllInterfaces(bean.getClass()));
        interfaces.addAll(ClassUtils.getAllInterfaces(wrapper.getClass()));
        return (Wrapper) Proxy.newProxyInstance(bean.getClass().getClassLoader(), interfaces.toArray(new Class<?>[0]), handler);
    }

    /**
     * Builds the bean delegator.
     *
     * @param <Bean> the generic type
     * @param bean   the bean
     * @return the bean
     */
    @SuppressWarnings("unchecked")
    public static <Bean> Bean buildBeanDelegator(Bean bean) {
        InvocationHandler handler = new BeanDelegatorHandler<Bean>(bean);
        return (Bean) Proxy.newProxyInstance(bean.getClass().getClassLoader(), ClassUtils.getAllInterfaces(bean.getClass()).toArray(new Class<?>[0]), handler);
    }

    /**
     * Builds the bean.
     *
     * @param <Bean>        the generic type
     * @param beanInterface the bean interface
     * @param intf          the intf
     * @return the bean
     */
    @SuppressWarnings("unchecked")
    public static <Bean> Bean buildBean(Class<Bean> beanInterface, Class<?>... intf) {
        InvocationHandler handler = new BeanHandler();
        Set<Class<?>> list = new LinkedHashSet<Class<?>>(Arrays.asList(intf));
        list.add(beanInterface);
        list.remove(null);
        return (Bean) Proxy.newProxyInstance(beanInterface.getClassLoader(), list.toArray(new Class<?>[0]), handler);
    }

    /**
     * Uses the contents of one bean to populate the contents of another. The two beans are joined after this invocation
     *
     * @param <Bean> the generic type
     * @param intf   the intf
     * @return the bean
     */
    @SuppressWarnings("unchecked")
    public static <Bean> Bean convertBean(Bean bean, Class<?>... intf) {
        InvocationHandler handler;
        if (Proxy.isProxyClass(bean.getClass())) {
            handler = Proxy.getInvocationHandler(bean);
        } else {
            handler = new BeanHandler();
        }
        List<Class<?>> list = new ArrayList<Class<?>>(Arrays.asList(intf));
        return (Bean) Proxy.newProxyInstance(intf[0].getClassLoader(), intf, handler);
    }

    /**
     * Uses the contents of one bean to populate the contents of another. The two beans are joined after this invocation
     *
     * @param <Bean> the generic type
     * @param intf   the intf
     * @return the bean
     */
    @SuppressWarnings("unchecked")
    public static <BeanBase, Bean extends BeanBase> Bean convertBean(BeanBase bean, Class<Bean> beanInterface, Class<?>... intf) {
        InvocationHandler handler;
        if (Proxy.isProxyClass(bean.getClass())) {
            handler = Proxy.getInvocationHandler(bean);
        } else {
            handler = new BeanHandler();
        }
        Set<Class<?>> list = new LinkedHashSet<Class<?>>(Arrays.asList(intf));
        list.add(beanInterface);
        return (Bean) Proxy.newProxyInstance(beanInterface.getClassLoader(), list.toArray(new Class<?>[list.size()]), handler);
    }

    /**
     * Builds the bean decorator.
     *
     * @param <Decorator> the generic type
     * @param <Bean>      the generic type
     * @param decorator   the decorator
     * @param bean        the bean
     * @return the bean
     */
    @SuppressWarnings("unchecked")
    public static <Decorator, Bean> Bean buildBeanDecorator(Decorator decorator, Bean bean) {
        BeanDecoratorHandler<Decorator, Bean> handler = new BeanDecoratorHandler<Decorator, Bean>();
        handler.bean = bean;
        handler.decorator = decorator;
        return (Bean) Proxy.newProxyInstance(bean.getClass().getClassLoader(), ClassUtils.getAllInterfaces(bean.getClass()).toArray(new Class<?>[0]), handler);
    }

    /**
     * Check is suppored.
     *
     * @param clazz        the clazz
     * @param method       the method
     * @param defaultState
     * @return true, if successful
     */
    public static boolean checkIsSuppored(Class<?> clazz, Method method, boolean defaultState) {
        try {
            Method m = clazz.getMethod(method.getName(), method.getParameterTypes());
            SupportedFunction supported = m.getAnnotation(SupportedFunction.class);
            return supported == null ? defaultState : supported.supported();
        } catch (Exception ex) {
            return false;
        }
    }

    public static String generateName(Class<?> clazz) {
        if (count > (Long.MAX_VALUE - 1)) {
            count = 0;
        }
        return clazz.getSimpleName() + (++count);
    }

    public static interface BeanProxy {
        void setDelegator(Object proxy);
    }

    /**
     * Marker Class to retrieve the underlying variables of a bean proxy.
     */
    public interface BeanVarHandler<Bean> {
        default Map<String, Object> getVars() {
            if (Proxy.isProxyClass(getBean().getClass())) {
                InvocationHandler existingHandler = Proxy.getInvocationHandler(getBean());
                if (existingHandler instanceof BeanVarHandler) {
                    return ((BeanVarHandler) existingHandler).getVars();
                }
            }
            throw new UnsupportedOperationException("Cannot locate variables for an object that is not a Bean Proxy");
        }

        Bean getBean();

    }

    /**
     * The Class BeanDecoratorHandler.
     *
     * @param <Decorator> the generic type
     * @param <Bean>      the generic type
     */
    private static class BeanDecoratorHandler<Decorator, Bean> implements BeanVarHandler<Bean>, InvocationHandler {


        private Decorator decorator;
        private Bean bean;

        public Bean getBean() {
            return bean;
        }

        public Decorator getDecorator() {
            return decorator;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                // search for method in decorator and call if found. Allows
                // overrides to be implemented without breaking base behaviour
                Method nMethod = getDecorator().getClass().getMethod(method.getName(), method.getParameterTypes());
                return nMethod.invoke(getDecorator(), args);
            } catch (NoSuchMethodException ex) {
                for (Class in : bean.getClass().getInterfaces()) {
                    try {
                        method = in.getMethod(method.getName(), method.getParameterTypes());
                        return method.invoke(bean, args);
                    } catch (IllegalAccessException | NoSuchMethodException ignored) {
                    } catch (UnsupportedOperationException uo) {
                        throw uo;
                    } catch (InvocationTargetException it) {
                        throw it.getTargetException();
                    }
                }
                throw new UnsupportedOperationException("No availble method " + method.getName());
            }
        }
    }

    /**
     * The Class BeanHandler.
     * <p/>
     * Creates a generic handler to manage get/set methods for all fields from a given set of instances.
     */
    private static class BeanHandler implements InvocationHandler, BeanVarHandler<Object> {

        protected final Map<String, Object> vars;

        private BeanHandler(Map<String, Object> vars) {
            this.vars = vars;
        }

        private BeanHandler() {
            this.vars = new HashMap<String, Object>();
        }

        @Override
        public Map<String, Object> getVars() {
            return vars;
        }

        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            for (String key : vars.keySet()) {
                if (buffer.length() > 0) {
                    buffer.append(" , ");
                }
                String value = vars.get(key).toString();
                buffer.append(key).append(" -> ").append(value);
            }
            return buffer.toString();
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("toString")) {
                return toString();
            }
            String name = method.getName();
            if (BeanUtils.checkIsSuppored(method.getDeclaringClass(), method, true)) {
                if (method.getName().startsWith("set")) {
                    name = method.getName().replaceFirst("set", "");
                    if (args[0] != null) {
                        vars.put(name, args[0]);
                    } else {
                        vars.remove(name);
                    }
                    return null;
                } else {
                    if (method.getName().startsWith("get")) {
                        name = method.getName().replaceFirst("get", "");
                        Object result = vars.get(name);
                        if (!method.getReturnType().isPrimitive() || result != null) {
                            return result;
                        }
                    }
                }
            }
            throw new UnsupportedOperationException("Bean does not support property " + name.replaceFirst("(set)|(get)", ""));
        }

    }

    /**
     * The Class BeanDelegatorHandler.
     * <p/>
     * This handler wraps an existing bean with a wrapper instance of the same bean type allowing modification to the wrapper while still being able to retrieve
     * original values if the wrapper fields have not been set. If the original bean contains a value for a field then calls to set that field will result in
     * the wrapper being set and the original bean being left unchanged. If the original bean has no value for a field then a call to set that field will result
     * in the original beans field being set On calls to get a value, if the wrapper h  as been given a value then that is returned, otherwise the value in the
     * original bean is returned
     * <p/>
     * This allows an instance of a bean to be shared across multiple objects, and allows objects to modify field values within their context without affecting
     * the original bean values that may still be accessed within other contexts
     *
     * @param <Bean> the generic type
     */
    private static class BeanDelegatorHandler<Bean> implements InvocationHandler, BeanVarHandler<Bean> {


        private Bean delegated;
        private Object wrapper;

        /**
         * The Constructor.
         *
         * @param delegated the delegated
         */
        protected BeanDelegatorHandler(Bean delegated) {
            this.delegated = delegated;
            List<Class<?>> allInterfaces = ClassUtils.getAllInterfaces(delegated.getClass());
            this.wrapper = Proxy.newProxyInstance(delegated.getClass().getClassLoader(),
                    allInterfaces.toArray(new Class<?>[allInterfaces.size()]), new BeanHandler());
        }

        protected BeanDelegatorHandler(Bean delegated, Bean wrapper) {
            this.delegated = delegated;
            this.wrapper = wrapper;
        }

        @Override
        public Bean getBean() {
            return delegated;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().startsWith("get")) {
                Object value = null;
                try {
                    value = method.invoke(wrapper, args);
                } catch (Exception ignored) {
                }
                if (value == null && method.getDeclaringClass().isAssignableFrom(delegated.getClass())) {
                    value = method.invoke(delegated, args);
                }
                return value;
            } else {
                if (method.getName().startsWith("set")) {
                    String name = method.getName().replaceFirst("set", "get");
                    Object value = null;
                    try {
                        Method gMethod = delegated.getClass().getMethod(name);
                        value = gMethod.invoke(delegated);
                    } catch (Exception ignored) {
                    }
                    if (value == null && method.getDeclaringClass().isAssignableFrom(delegated.getClass())) {
                        value = method.invoke(delegated, args);
                    } else {
                        if (method.getDeclaringClass().isAssignableFrom(wrapper.getClass())) {
                            value = method.invoke(wrapper, args);
                        }
                    }
                    return value;
                }
            }
            try {
                return method.invoke(wrapper, args);
            } catch (Exception ex) {
                try {
                    return method.invoke(delegated, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            throw new UnsupportedOperationException(method.getName());
        }
    }

}
