package com.selftopup.logging.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogConfigurationException;
import com.selftopup.logging.LogFactory;

public class LogFactoryImpl extends LogFactory {

    public LogFactoryImpl() {
        attributes = new Hashtable();
        instances = new Hashtable();
        logConstructor = null;
        logConstructorSignature = (new Class[] { java.lang.String.class });
        logMethod = null;
        logMethodSignature = (new Class[] { com.selftopup.logging.LogFactory.class });
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public String[] getAttributeNames() {
        Vector names = new Vector();
        for (Enumeration keys = attributes.keys(); keys.hasMoreElements(); names.addElement((String) keys.nextElement()))
            ;
        String results[] = new String[names.size()];
        for (int i = 0; i < results.length; i++)
            results[i] = (String) names.elementAt(i);

        return results;
    }

    public Log getInstance(Class clazz) throws LogConfigurationException {
        return getInstance(clazz.getName());
    }

    public Log getInstance(String name) throws LogConfigurationException {
        Log instance = (Log) instances.get(name);
        if (instance == null) {
            instance = newInstance(name);
            instances.put(name, instance);
        }
        return instance;
    }

    public void release() {
        instances.clear();
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public void setAttribute(String name, Object value) {
        if (value == null)
            attributes.remove(name);
        else
            attributes.put(name, value);
    }

    protected String getLogClassName() {
        if (logClassName != null)
            return logClassName;
        logClassName = (String) getAttribute("com.selftopup.logging.Log");
        if (logClassName == null)
            logClassName = (String) getAttribute("com.selftopup.logging.log");
        if (logClassName == null)
            try {
                logClassName = System.getProperty("com.selftopup.logging.Log");
            } catch (SecurityException e) {
            }
        if (logClassName == null)
            try {
                logClassName = System.getProperty("com.selftopup.logging.log");
            } catch (SecurityException e) {
            }
        if (logClassName == null && isLog4JAvailable())
            logClassName = "com.selftopup.logging.impl.Log4JLogger";
        if (logClassName == null && isJdk14Available())
            logClassName = "com.selftopup.logging.impl.Jdk14Logger";
        if (logClassName == null && isJdk13LumberjackAvailable())
            logClassName = "com.selftopup.logging.impl.Jdk13LumberjackLogger";
        if (logClassName == null)
            logClassName = "com.selftopup.logging.impl.SimpleLog";
        return logClassName;
    }

    protected Constructor getLogConstructor() throws LogConfigurationException {
        if (logConstructor != null)
            return logConstructor;
        String logClassName = getLogClassName();
        Class logClass = null;
        Class logInterface = null;
        try {
            logInterface = getClass().getClassLoader().loadClass("com.selftopup.logging.Log");
            logClass = loadClass(logClassName);
            if (logClass == null)
                throw new LogConfigurationException("No suitable Log implementation for " + logClassName);
            if (!logInterface.isAssignableFrom(logClass)) {
                Class interfaces[] = logClass.getInterfaces();
                for (int i = 0; i < interfaces.length; i++)
                    if ("com.selftopup.logging.Log".equals(interfaces[i].getName()))
                        throw new LogConfigurationException("Invalid class loader hierarchy.  You have more than one version of 'com.selftopup.logging.Log' visible, which is not allowed.");

                throw new LogConfigurationException("Class " + logClassName + " does not implement '" + "com.selftopup.logging.Log" + "'.");
            }
        } catch (Exception t) {
            throw new LogConfigurationException(t);
        }
        try {
            logMethod = logClass.getMethod("setLogFactory", logMethodSignature);
        } catch (Exception t) {
            logMethod = null;
        }
        try {
            logConstructor = logClass.getConstructor(logConstructorSignature);
            return logConstructor;
        } catch (Exception t) {
            throw new LogConfigurationException("No suitable Log constructor " + logConstructorSignature + " for " + logClassName, t);
        }
    }

    private static Class loadClass(final String name) throws ClassNotFoundException {
        Object result = AccessController.doPrivileged(new PrivilegedAction() {

            public Object run() {
                ClassLoader threadCL = LogFactory.getContextClassLoader();
                if (threadCL != null)
                    try {
                        return threadCL.loadClass(name);
                    } catch (ClassNotFoundException ex) {
                    }
                try {
                    return Class.forName(name);
                } catch (ClassNotFoundException e) {
                    return e;
                }
            }

        });
        if (result instanceof Class)
            return (Class) result;
        else
            throw (ClassNotFoundException) result;
    }

    protected boolean isJdk13LumberjackAvailable() {
        try {
            loadClass("java.util.logging.Logger");
            loadClass("com.selftopup.logging.impl.Jdk13LumberjackLogger");
            return true;
        } catch (Exception t) {
            return false;
        }
    }

    protected boolean isJdk14Available() {
        try {
            loadClass("java.util.logging.Logger");
            loadClass("com.selftopup.logging.impl.Jdk14Logger");
            Class throwable = loadClass("java.lang.Throwable");
            return throwable.getDeclaredMethod("getStackTrace", null) != null;
        } catch (Exception t) {
            return false;
        }
    }

    protected boolean isLog4JAvailable() {
        try {
            loadClass("org.apache.log4j.Logger");
            loadClass("com.selftopup.logging.impl.Log4JLogger");
            return true;
        } catch (Exception t) {
            return false;
        }
    }

    protected Log newInstance(String name) throws LogConfigurationException {
        Log instance = null;
        try {
            Object params[] = new Object[1];
            params[0] = name;
            instance = (Log) getLogConstructor().newInstance(params);
            if (logMethod != null) {
                params[0] = this;
                logMethod.invoke(instance, params);
            }
            return instance;
        } catch (InvocationTargetException e) {
            Throwable c = e.getTargetException();
            if (c != null)
                throw new LogConfigurationException(c);
            else
                throw new LogConfigurationException(e);
        } catch (Exception t) {
            throw new LogConfigurationException(t);
        }
    }

    public static final String LOG_PROPERTY = "com.selftopup.logging.Log";
    protected static final String LOG_PROPERTY_OLD = "com.selftopup.logging.log";
    protected Hashtable attributes;
    protected Hashtable instances;
    private String logClassName;
    protected Constructor logConstructor;
    protected Class logConstructorSignature[];
    protected Method logMethod;
    protected Class logMethodSignature[];

}