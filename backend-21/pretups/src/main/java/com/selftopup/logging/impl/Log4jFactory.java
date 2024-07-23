package com.selftopup.logging.impl;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.selftopup.logging.*;
import org.apache.log4j.Logger;

// Referenced classes of package com.btsl.logging.impl:
// Log4JLogger

/**
 * @deprecated Class Log4jFactory is deprecated
 */

public final class Log4jFactory extends LogFactory {

    public Log4jFactory() {
        attributes = new Hashtable();
        instances = new Hashtable();
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
        Log instance = (Log) instances.get(clazz);
        if (instance != null) {
            return instance;
        } else {
            instance = new Log4JLogger(Logger.getLogger(clazz));
            instances.put(clazz, instance);
            return instance;
        }
    }

    public Log getInstance(String name) throws LogConfigurationException {
        Log instance = (Log) instances.get(name);
        if (instance != null) {
            return instance;
        } else {
            instance = new Log4JLogger(Logger.getLogger(name));
            instances.put(name, instance);
            return instance;
        }
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

    private Hashtable attributes;
    private Hashtable instances;
}
