package com.btsl.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
/**
 * 
 * @author ayush.abhijeet
 *
 */
public class CertificateLoader implements Runnable {

    private static Log log = LogFactory.getLog(CertificateLoader.class.getName());
    private static KeyStore ts;
    private static javax.net.ssl.SSLSocketFactory sslSocketFactory;
    private static boolean tomcatServer = true;
    
    public void run() {
        try {
            Thread.sleep(50);
            loadCertificateOnStartUp();
        } catch (Exception e) {
        	 log.error("CertificateLoader init() Exception ", e);
        }
    }


    /**
     * @return the tomcatServer
     */
    public static boolean isTomcatServer() {
        return tomcatServer;
    }

    /**
     * @param tomcatServer
     *            the tomcatServer to set
     */
    public static void setTomcatServer(boolean tomcatServer) {
        CertificateLoader.tomcatServer = tomcatServer;
    }

    /**
     * @return the sslSocketFactory
     */
    public static javax.net.ssl.SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    /**
     * @return the ts
     */
    public static KeyStore getTs() {
        return ts;
    }

    /**
     * @param ts
     *            the ts to set
     */

    public static void loadCertificateOnStartUp() {
        FileInputStream fileInputStream = null;
        final String methodName = "loadCertificateOnStartUp";
        try {
            ts = KeyStore.getInstance("JKS");
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Entered  ");
            }
            String trustStore = null;
            String trustStorePassword = null;

            trustStore = Constants.getProperty("TRUST_STORE");
            if (BTSLUtil.isNullString(trustStore)) {
                log.error(methodName, "trustStore:" + trustStore);
                throw new BTSLBaseException(PretupsErrorCodesI.HTTPS_PARAMETER_ERROR);
            }

            trustStorePassword = Constants.getProperty("TRUST_STORE_PASSWORD");
            if (BTSLUtil.isNullString(trustStorePassword)) {
                log.error(methodName, "trustStorePassword:" + trustStorePassword);
                throw new BTSLBaseException(PretupsErrorCodesI.HTTPS_PARAMETER_ERROR);
            }
            fileInputStream = new FileInputStream(trustStore);
            ts.load(fileInputStream, trustStorePassword.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ts);
            javax.net.ssl.TrustManager tm[] = tmf.getTrustManagers();
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, tm, null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            setTomcatServer(false);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    log.errorTrace(methodName, e);
                }
            }
            if (isTomcatServer()) {
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Exit:  Server is Tomcat");
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Exit:  Server is WebSphere");
                }
            }

        }
    }
}
