package com.btsl.logging;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;

public abstract class LogFactory {

    protected LogFactory() {
    }

    public abstract Object getAttribute(String s);

    public abstract String[] getAttributeNames();

    public abstract Log getInstance(Class class1) throws LogConfigurationException;

    public abstract Log getInstance(String s) throws LogConfigurationException;

    public abstract void release();

    public abstract void removeAttribute(String s);

    public abstract void setAttribute(String s, Object obj);
    

    public static LogFactory getFactory() throws LogConfigurationException {
        ClassLoader contextClassLoader = (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {

            public Object run() {
                return LogFactory.getContextClassLoader();
            }

        });
        LogFactory factory = getCachedFactory(contextClassLoader);
        if (factory != null)
            return factory;
        Properties props = null;
        try {
            InputStream stream = getResourceAsStream(contextClassLoader, "commons-logging.properties");
            if (stream != null) {
                props = new Properties();
                props.load(stream);
                stream.close();
            }
        } catch (IOException e) {// ignore
        } catch (SecurityException e) {// ignore
        }
        try {
            String factoryClass = System.getProperty("com.btsl.logging.LogFactory");
            if (factoryClass != null)
                factory = newFactory(factoryClass, contextClassLoader);
        } catch (SecurityException e) {// ignore
        }
        if (factory == null)
            try {
                InputStream is = getResourceAsStream(contextClassLoader, "META-INF/services/com.btsl.logging.LogFactory");
                if (is != null) {
                    BufferedReader rd;
                    try {
                        rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        rd = new BufferedReader(new InputStreamReader(is));
                    }
                    String factoryClassName = rd.readLine();
                    rd.close();
                    if (factoryClassName != null && !"".equals(factoryClassName))
                        factory = newFactory(factoryClassName, contextClassLoader);
                }
            } catch (Exception ex) {// ignore
            }
        if (factory == null && props != null) {
            String factoryClass = props.getProperty("com.btsl.logging.LogFactory");
            if (factoryClass != null)
                factory = newFactory(factoryClass, contextClassLoader);
        }
        if (factory == null)
            factory = newFactory("com.btsl.logging.impl.Log4jFactory", (com.btsl.logging.LogFactory.class).getClassLoader());
        if (factory != null) {
            cacheFactory(contextClassLoader, factory);
            if (props != null) {
                String name;
                String value;
                for (Enumeration names = props.propertyNames(); names.hasMoreElements(); factory.setAttribute(name, value)) {
                    name = (String) names.nextElement();
                    value = props.getProperty(name);
                }
            }
        }
        return factory;
    }

    public static Log getLog(Class clazz) throws LogConfigurationException {
        return getFactory().getInstance(clazz);
    }

    public static Log getLog(String name) throws LogConfigurationException {
        return getFactory().getInstance(name);
    }

    public static org.apache.logging.log4j.Logger getLogg(Class clazz) throws LogConfigurationException {
        return LogManager.getLogger(clazz.getName());
    }
    
    
    public static org.apache.logging.log4j.Logger getLogg(String name) throws LogConfigurationException {
        return LogManager.getLogger(name);
    }
    
    public static void release(ClassLoader classLoader) {
        synchronized (factories) {
            LogFactory factory = (LogFactory) factories.get(classLoader);
            if (factory != null) {
                factory.release();
                factories.remove(classLoader);
            }
        }
    }

    public static void releaseAll() {
        synchronized (factories) {
            LogFactory element;
            for (Enumeration elements = factories.elements(); elements.hasMoreElements(); element.release())
                element = (LogFactory) elements.nextElement();

            factories.clear();
        }
    }

    protected static ClassLoader getContextClassLoader() throws LogConfigurationException {
        ClassLoader classLoader = null;
        try {
            Method method = (java.lang.Thread.class).getMethod("getContextClassLoader", null);
            try {
                classLoader = (ClassLoader) method.invoke(Thread.currentThread(), null);
            } catch (IllegalAccessException e) {
                throw new LogConfigurationException("Unexpected IllegalAccessException", e);
            } catch (InvocationTargetException e) {
                if (!(e.getTargetException() instanceof SecurityException))
                    throw new LogConfigurationException("Unexpected InvocationTargetException", e.getTargetException());
            }
        } catch (NoSuchMethodException e) {
            classLoader = (com.btsl.logging.LogFactory.class).getClassLoader();
        }
        return classLoader;
    }

    private static LogFactory getCachedFactory(ClassLoader contextClassLoader) {
        LogFactory factory = null;
        if (contextClassLoader != null)
            factory = (LogFactory) factories.get(contextClassLoader);
        return factory;
    }

    private static void cacheFactory(ClassLoader classLoader, LogFactory factory) {
        if (classLoader != null && factory != null)
            factories.put(classLoader, factory);
    }

    protected static LogFactory newFactory(final String factoryClass, final ClassLoader classLoader) throws LogConfigurationException {
        Object result = AccessController.doPrivileged(new PrivilegedAction() {

            public Object run() {
                Class logFactoryClass = null;
                try {
                    if (classLoader != null)
                        try {
                            logFactoryClass = classLoader.loadClass(factoryClass);
                            return (LogFactory) logFactoryClass.newInstance();
                        } catch (ClassNotFoundException ex) {
                            if (classLoader == (com.btsl.logging.LogFactory.class).getClassLoader())
                                throw ex;
                        } catch (NoClassDefFoundError e) {
                            if (classLoader == (com.btsl.logging.LogFactory.class).getClassLoader())
                                throw e;
                        } catch (ClassCastException e) {
                            if (classLoader == (com.btsl.logging.LogFactory.class).getClassLoader())
                                throw e;
                        }
                    logFactoryClass = Class.forName(factoryClass);
                    return (LogFactory) logFactoryClass.newInstance();
                } catch (Exception e) {
                    if (logFactoryClass != null && !(com.btsl.logging.LogFactory.class).isAssignableFrom(logFactoryClass))
                        return new LogConfigurationException("The chosen LogFactory implementation does not extend LogFactory. Please check your configuration.", e);
                    else
                        return new LogConfigurationException(e);
                }
            }

        });
        if (result instanceof LogConfigurationException)
            throw (LogConfigurationException) result;
        else
            return (LogFactory) result;
    }

    private static InputStream getResourceAsStream(final ClassLoader loader, final String name) {
        return (InputStream) AccessController.doPrivileged(new PrivilegedAction() {

            public Object run() {
                if (loader != null)
                    return loader.getResourceAsStream(name);
                else
                    return ClassLoader.getSystemResourceAsStream(name);
            }

        });
    }

    /**
     * To write log in debug mode
     * @param methodName Name of Method
     * @param info Information which is required for logging
     * @param log Log object of Logger
     */
    public static void printLog(String methodName, String info, Log log){ 
        if(log.isDebugEnabled()){ 
                log.debug(methodName, info); 
        } 
    }
    /**
     * To write log in error mode
     * @param methodName Name of Method
     * @param info Information which is required for logging
     * @param log Log object of Logger
     */
    public static void printError(String methodName, String info, Log log){
    	if (log.isErrorEnabled()) {
            log.error(methodName, info);
        }
    }

    public static final String FACTORY_PROPERTY = "com.btsl.logging.LogFactory";
    public static final String FACTORY_DEFAULT = "com.btsl.logging.impl.Log4jFactory";
    public static final String FACTORY_PROPERTIES = "commons-logging.properties";
    protected static final String SERVICE_ID = "META-INF/services/com.btsl.logging.LogFactory";
    protected static Hashtable factories = new Hashtable();
    
    /**
     * To write log in debug mode
     * @param methodName Name of Method
     * @param info Information which is required for logging
     * @param log Log object of Logger
     */
    public static void printLog(String methodName, StringBuilder info, Log log){ 
        if(log.isDebugEnabled()){ 
        	log.debug(methodName, info); 
        } 
    }
    /**
     * To write log in debug mode
     * @param methodName Name of Method
     * @param info Information which is required for logging
     * @param log Log object of Logger
     */
    public static void printError(String methodName, StringBuilder info, Log log){
    	if (log.isErrorEnabled()) {
            log.error(methodName, info);
        }
    }
}
