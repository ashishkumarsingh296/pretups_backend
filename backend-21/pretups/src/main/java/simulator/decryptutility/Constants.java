package simulator.decryptutility;

/*
 * Constants.java
 * 
 * 
 * 
 * Name Date History
 * ------------------------------------------------------------------------
 * Sanjay 07/07/2003 Initial Creation
 * 
 * ------------------------------------------------------------------------
 * Copyright (c) 2003 Bharti Telesoft Ltd.
 */

import java.io.*;
import java.util.Properties;

public class Constants implements java.io.Serializable {
    public static String KEY = "981AFA8CDEB2A0F7E0A011B557BB08CF";
    public static Properties properties = new Properties(); // to keep the value

    // of propertie

    public static void load(String fileName) throws IOException {
        final File file = new File(fileName);
        final FileInputStream fileInputStream = new FileInputStream(file);
        properties.load(fileInputStream);
        fileInputStream.close();
    }// end of load

    public static String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }// end of getProperty
}// end of Constants

