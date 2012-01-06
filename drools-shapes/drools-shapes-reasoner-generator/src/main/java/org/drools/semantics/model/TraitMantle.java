/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.semantics.model;


import org.drools.factmodel.traits.Trait;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class TraitMantle extends HashMap<String,Object> implements InvocationHandler  {

    private Set<Class<?>> types = new LinkedHashSet<Class<?>>();

    private Object proxy;

    private Object core;

    private static HashMap<Class, List<Object>> delegateCache = new HashMap<Class,List<Object>>();;

    private LinkedList<Object> delegates;







    public static <K> IThing wrap(K obj) {
        return wrap(obj, new HashMap<String,Object>(), IThing.class);
    }

    public static <K> IThing wrap(K obj, Map<String,Object> map) {
        return wrap(obj, map, IThing.class);
    }


    public static <T> T wrap(Object obj, Map<String,Object> map, Class<T> trait) {

        TraitMantle mantle = new TraitMantle(map, obj);

        Class[] interfaces = null;
        if (obj.getClass().getInterfaces().length > 0) {
            interfaces = obj.getClass().getInterfaces();
            interfaces = Arrays.copyOf(interfaces,interfaces.length + 1);
            interfaces[interfaces.length-1] = trait;

            for (Class itf : obj.getClass().getInterfaces()) {
                mantle.types.add(itf);
            }
        } else {
            interfaces = new Class[] {trait};
        }




        T proxy = (T) Proxy.newProxyInstance(TraitMantle.class.getClassLoader(),
                interfaces,
                mantle );

        mantle.types.add(IThing.class);

        if ( trait != IThing.class ) {
            bindImpl( mantle, trait );
        }

        mantle.proxy = proxy;
        return proxy;
    }


    private static <T> T don(IThing thing, Class<T> intface) {
        TraitMantle mantle = (TraitMantle) thing.getMantle();

        if (mantle.types.contains(intface)) {
            rebindImpl(mantle, intface);
            return (T) mantle.proxy;
        }


        Class[] interfaces = (Class[]) mantle.types.toArray(new Class[mantle.types.size()]);
        interfaces = Arrays.copyOf(interfaces,interfaces.length + 1);
        interfaces[interfaces.length-1] = intface;

        T proxy = (T) Proxy.newProxyInstance(TraitMantle.class.getClassLoader(),
                interfaces,
                mantle );

        mantle.types.add(intface);
        bindImpl( mantle, intface );

        mantle.proxy = proxy;
        return proxy;
    }




    private static <T> T cast(IThing thing, Class<T> intface) {
        TraitMantle mantle = (TraitMantle) thing.getMantle();

        if (! mantle.types.contains(intface)) {
            return null;
        }

        Class[] interfaces = new Class[] {intface};

        T proxy = (T) Proxy.newProxyInstance(TraitMantle.class.getClassLoader(),
                interfaces,
                mantle );

        return proxy;
    }


    private static <T> IThing shed(IThing thing, Class<T> intface) {
        if (IThing.class.equals(intface)) {
            return thing;
        }

        TraitMantle mantle = (TraitMantle) thing.getMantle();
        if (! mantle.types.contains(intface)) {
            return thing;
        }

        mantle.types.remove(intface);

        unbindImpl( mantle, intface );

        Class[] interfaces = (Class[]) mantle.types.toArray(new Class[mantle.types.size()]);

        Object proxy = Proxy.newProxyInstance(TraitMantle.class.getClassLoader(),
                interfaces,
                mantle );

        mantle.proxy = proxy;
        return (IThing) proxy;
    }





    private static <T> void unbindImpl(TraitMantle mantle, Class<T> intface) {
        System.out.println("Removing " + intface);
        List<Object> implementors = delegateCache.get(intface);
        System.err.println("Removing " + implementors + " frm 0" + delegateCache);


        mantle.delegates.removeAll(implementors);
    }


    private static void bindImpl( TraitMantle mantle, Class<?> intface ) {
        List<Object> implementors;
        if (delegateCache.containsKey(intface)) {
            implementors = delegateCache.get(intface);
        } else {
            implementors = findAllImplementors(intface);
            delegateCache.put(intface,implementors);
        }

        System.err.println("Bound " + intface + " in " + delegateCache);

        mantle.delegates.removeAll(implementors);
        mantle.delegates.addAll(implementors);
    }

    private static <T> void rebindImpl(TraitMantle mantle, Class<T> intface) {
        List<Object> implementors = delegateCache.get(intface);
        mantle.delegates.removeAll(implementors);
        mantle.delegates.addAll(implementors);
        System.err.println("reBound " + intface + " in " + delegateCache);
    }




    private static List<Object> findAllImplementors(Class<?> intface) {
        List<Object> list = new ArrayList<Object>(5);
        recur(list, intface);
        System.out.println("Found all implementors for " + intface + " > > " + list);
        return list;
    }

    private static void recur(List<Object> list, Class<?> intface) {
        for (Class intf : intface.getInterfaces()) {
            recur(list,intf);
        }

        Trait aTrait = intface.getAnnotation(Trait.class);
        if (aTrait != null) {
            try {
                Class impl = aTrait.impl();
                if (impl != null) {
                    Method factory = impl.getMethod("newInstance");
                    Object del = factory.invoke(null);
                    list.add(del);
                }
            } catch (Exception ignored) {

            }
        }

    }











    protected TraitMantle(Map<String, Object> properties, Object obj) {
        this.delegates = new LinkedList<Object>();
        this.types = new HashSet<Class<?>>();
        this.putAll(properties);
        this.core = obj;
    }




    public Object invoke(Object proxy, Method method, Object[] args) throws NoSuchMethodException {
        String name = method.getName();

        if (args == null) {
            if ("getTypes".equals(name)) {
                return types;
            } else if ("getCore".equals(name)) {
                return core;
            } else if ("getMantle".equals(name)) {
                return this;
            } else if (name.startsWith("get")) {
                String prop = name.substring(3,4).toLowerCase() + name.substring(4);
                if (containsKey(prop)) {
                    return get(prop);
                } else {
                    try {
                        return checkCoreThenDelegate(name);
                    } catch ( NoSuchMethodException nsme ) {
                        if ( method.getReturnType().isPrimitive() ) {
                            if (int.class.isAssignableFrom(method.getReturnType())) {
                                return 0;
                            } else if (double.class.isAssignableFrom(method.getReturnType())) {
                                return 0.0;
                            } else {
                                throw new RuntimeException("Missing prim. type " + method.getReturnType());
                            }
                        } else {
                            return null;
                        }
                    }
                }
            } else if ("hashCode".equals(name)) {
                return core.hashCode();
            } else if ("size".equals(name)) {
                return size();
            } else if ("entrySet".equals(name)) {
                return entrySet();
            } else {
                return checkCoreThenDelegate(name);
            }
        }




        int len = args.length;
        if (len == 1) {

            Object arg = args[0];

            if ("get".equals(name)) {

                if (containsKey(arg)) {
                    return get(arg);
                } else {
                    if (arg instanceof String) {
                        try {
                            String propName = (String) arg;
                            return checkCoreThenDelegate("get"+ propName.substring(0,1).toUpperCase() + propName.substring(1));
                        } catch (NoSuchMethodException nsme) {
                            return null;         // undefined
                        }
                    } else {
                        return checkCoreThenDelegate(name);
                    }
                }
            } else if (name.startsWith("set")) {
                String propName = name.substring(3).toLowerCase();
                if (containsKey(propName)) {
                    return put(propName,arg);
                } else {
                    try {
                        System.out.println("x" + name);
                        return checkCoreThenDelegate(name, method.getParameterTypes(), args);
                    } catch (NoSuchMethodException nsme) {
                        System.out.println("y" + propName);
                        put(propName,arg);
                    }
                }
            } else if ("is".equals(name)) {
                if (containsKey(arg) && get(arg) instanceof Boolean ) {
                    return get(arg);
                } else {
                    if (arg instanceof String) {
                        String propName = (String) arg;
                        return checkCoreThenDelegate("is"+ propName.substring(0,1).toUpperCase() + propName.substring(1));
                    } else {
                        return checkDelegateThenCore(name, method.getParameterTypes(), args);
                    }
                }
            } else if ("equals".equals(name)) {
                return core.equals(arg);
            } else if ("hasType".equals(name)) {
                return types.contains(arg);
            } else if ("cast".equals(name)) {
                return TraitMantle.cast((IThing) proxy, (Class<?>) arg);
            } else if ("don".equals(name)) {
                return TraitMantle.don((IThing) proxy, (Class<?>) arg);
            } else if ("shed".equals(name)) {
                return shed((IThing) proxy, (Class<?>) arg);
            } else if ("remove".equals(name)) {
                return remove(arg);
            } else if ("containsKey".equals(name)) {
                return containsKey(arg);
            } else {
                return checkDelegateThenCore(name, method.getParameterTypes(), args);
            }
        } else if (len == 2) {

            if ("set".equals(name)) {
                if (args[0] instanceof String) {
                    String prop = (String) args[0];
                    if (containsKey(prop)) {
                        return put(prop,args[1]);
                    } else {
                        try {
                            prop = "set" + prop.substring(0,1).toUpperCase() + prop.substring(1);
                            return checkCoreThenDelegate(prop, new Class[] {args[1].getClass()}, new Object[] {args[1]});
                        } catch ( NoSuchMethodException nsme ) {
                            return checkDelegateThenCore(name, method.getParameterTypes(), args);
                        }
                    }
                } else {
                    return checkDelegateThenCore(name, method.getParameterTypes(), args);
                }
            } else if ("put".equals(name)) {
                if (args[0] instanceof String) {
                    return put((String) args[0],args[1]);
                } else {
                    return checkDelegateThenCore(name, method.getParameterTypes(), args);
                }
            } else {
                return checkDelegateThenCore(name, method.getParameterTypes(), args);
            }
        } else {
            return checkDelegateThenCore(name, method.getParameterTypes(), args);

        }

        return null;
    }





    private Object checkCoreThenDelegate(String name) throws NoSuchMethodException {
        try {
            Method method = core.getClass().getMethod(name);
            return method.invoke(core);
        } catch (Exception e) {
            Iterator<Object> iter = delegates.descendingIterator();

            while (iter.hasNext()) {
                Object delegate = iter.next();
                try {
                    Method method = delegate.getClass().getMethod(name);
                    return method.invoke(delegate);
                } catch (Exception e1) {
                    System.err.println("method " + name + "() not found in " + delegate.getClass() +" , moving to next one " );
                }
            }
            throw new NoSuchMethodException(name+"()");

        }
    }


    private Object checkCoreThenDelegate(String name, Class[] argClasses, Object[] args) throws NoSuchMethodException {
        try {
            Method method = core.getClass().getMethod(name,argClasses);
            return method.invoke(core,args);
        } catch (Exception e) {
            System.out.println("Method not dounf in" + core.getClass().getName() + " : " + name + " << " + format(argClasses));

            Iterator<Object> iter = delegates.descendingIterator();
            while (iter.hasNext()) {
                Object delegate = iter.next();
                try {
                    Method method = delegate.getClass().getMethod(name, argClasses);
                    return method.invoke(delegate, args);
                } catch (Exception e1) {
                    System.err.println("method " + name + "(" + format(argClasses) +") not found in " + delegate.getClass() +" , moving to next one " );
//                    throw new NoSuchMethodException(name+"()");
                }
            }
            throw new NoSuchMethodException(name+ "(" + format(argClasses) +")");
        }
    }


    private Object checkDelegateThenCore(String name, Class[] argClasses, Object[] args) throws NoSuchMethodException {
        Iterator<Object> iter = delegates.descendingIterator();
        while (iter.hasNext()) {
            Object delegate = iter.next();

            try {
                Method method = delegate.getClass().getMethod(name, argClasses);
                return method.invoke(delegate, args);
            } catch (Exception e1) {
                System.err.println("method " + name + "(" + format(argClasses) +") not found in " + delegate.getClass() +" , moving to next one " );
//                    throw new NoSuchMethodException(name+"()");
            }
        }

        try {
            Method method = core.getClass().getMethod(name,argClasses);
            return method.invoke(core,args);
        } catch (Exception e3) {
            throw new NoSuchMethodException(name + "(" + format(argClasses) +")");
        }

    }





    private static String format(Class[] argz) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int j = 0; j < argz.length -1; j++) {
            sb.append(argz[j].getName()).append(",");
        }
        if (argz.length > 0) {
            sb.append(argz[argz.length-1]).append("]");
        } else {
            sb.append("]");
        }
        return sb.toString();
    }




}
