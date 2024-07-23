package com.inter.huaweicitycell;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class HuaweiProps {
    private static Logger _logger = Logger.getLogger(HuaweiProps.class.getName());
    public static Properties properties = new Properties();

    public static void load(String fileName) throws IOException {
        _logger.debug("Huaweiprops file load");
        File file = new File(fileName);
        properties.load(new FileInputStream(file));
        _logger.debug("Huaweiprops file load exiting");
    }

    public static String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    public static void logMessage(String p_message) {
        _logger.debug(p_message);
    }
}
