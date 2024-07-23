package com.btsl.pretups.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

//import org.apache.struts.util.MessageResourcesFactory;
//import org.apache.struts.util.PropertyMessageResources;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class BTSLMessageResources /*extends PropertyMessageResources */{

   
	private static final long serialVersionUID = 1L;
	public static final Log logger = LogFactory.getLog(BTSLMessageResources.class.getName());

/*    public BTSLMessageResources(MessageResourcesFactory factory, String config) {
        super(factory, config);
        if (logger.isDebugEnabled()) {
            logger.debug("BTSLMessageResources()", "Initializing, config='" + config + "'");
        }
    }*/

   /* public synchronized void loadLocales(String localeKey, boolean isReload, String contextPath) {
    	final String methodName = "loadLocales";
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, "Entered localeKey=" + BTSLUtil.logForgingReqParam(localeKey) + ", isReload=" + isReload + ", contextPath=" + contextPath);
        }
        // Have we already attempted to load messages for this locale?
        if (locales.get(localeKey) != null && !isReload) {
            return;
        }

        locales.put(localeKey, localeKey);
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " locales=" + locales);
        }
        // Set up to load the property resource for this locale key, if we can
        String name = config.replace('.', '/');
        if (localeKey.length() > 0) {
            name += "_" + localeKey;
        }

        name += ".properties";
        InputStream is = null;
        Properties props = new Properties();

        // Load the specified property resource
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " Loading resource '" + name + "'");
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }

        String tomcatPath = contextPath + System.getProperty("file.separator") + name;

        try {
            is = new FileInputStream(new File(tomcatPath));
            // classLoader.getResourceAsStream(name);
            if (is != null) {
                try {
                    props.load(is);

                } catch (IOException e) {
                    log.error("loadLocale()", e);
                }
            }
        } catch (FileNotFoundException e1) {
            logger.errorTrace(methodName, e1);
            tomcatPath = tomcatPath.substring(0, (tomcatPath.indexOf(localeKey) - 1)) + ".properties";

            try {
                is = new FileInputStream(new File(tomcatPath));
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
        } finally {
                try {
                    if(is!=null){
                    	is.close();
                    }
                  } catch (IOException e) {
                    log.error("loadLocale()", e);
                }
            }
        
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " Loading resource completed props.size()=" + props.size());
        }
        // Copy the corresponding values into our cache
        if (props.size() < 1) {
            return;
        }

        //synchronized (messages) {
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Befor Updation messages.size()=" + messages.size() + ", Messagers values=" + messages);
            }
            Iterator names = props.keySet().iterator();
            while (names.hasNext()) {
                String key = (String) names.next();
                if (logger.isDebugEnabled()) {
                    logger.debug(methodName, " Saving message key '" + messageKey(localeKey, key));
                }
                messages.put(messageKey(localeKey, key), props.getProperty(key));
            }
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "After Updation messages.size()=" + messages.size() + ", Messagers values=" + messages);
            }
        //}

        if (logger.isDebugEnabled()) {
            logger.debug(methodName, "Exited ");
        }
    }*/

}
