/*
 * Created on June 26, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.zteethopia;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author Vipan Kumar
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class ZTEINHBLogger {

    private static Logger _logger = Logger.getLogger(ZTEINHBLogger.class.getName());
    public static Properties properties = new Properties();

    public static void load(String fileName) throws IOException {
        _logger.debug("ZTEEthioProps file load");
        File file = new File(fileName);
        properties.load(new FileInputStream(file));
        _logger.debug("ZTEEthioProps file load exiting");
    }

    public static String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    public static void logMessage(String p_message) {
        _logger.debug(p_message);
    }

}
