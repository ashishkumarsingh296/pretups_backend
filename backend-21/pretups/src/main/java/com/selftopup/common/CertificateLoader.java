package com.selftopup.common;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

public class CertificateLoader {

    private static Log _log = LogFactory.getLog(CertificateLoader.class.getName());
    private static KeyStore ts;
    private static javax.net.ssl.SSLSocketFactory sslSocketFactory;
    private static boolean tomcatServer = true;

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

        try {
            ts = KeyStore.getInstance("JKS");
            if (_log.isDebugEnabled())
                _log.debug("loadCertificateOnStartUp", "Entered  ");
            String trustStore = null;
            String trustStorePassword = null;

            trustStore = Constants.getProperty("TRUST_STORE");
            if (BTSLUtil.isNullString(trustStore)) {
                _log.error("loadCertificateOnStartUp", "trustStore:" + trustStore);
                throw new BTSLBaseException(SelfTopUpErrorCodesI.HTTPS_PARAMETER_ERROR);
            }

            trustStorePassword = Constants.getProperty("TRUST_STORE_PASSWORD");
            if (BTSLUtil.isNullString(trustStorePassword)) {
                _log.error("loadCertificateOnStartUp", "trustStorePassword:" + trustStorePassword);
                throw new BTSLBaseException(SelfTopUpErrorCodesI.HTTPS_PARAMETER_ERROR);
            }
            ts.load(new FileInputStream(trustStore), trustStorePassword.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ts);
            javax.net.ssl.TrustManager tm[] = tmf.getTrustManagers();
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, tm, null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            setTomcatServer(false);
        } finally {
            if (isTomcatServer()) {
                if (_log.isDebugEnabled())
                    _log.debug("loadCertificateOnStartUp", "Exit:  Server is Tomcat");
            } else {
                if (_log.isDebugEnabled())
                    _log.debug("loadCertificateOnStartUp", "Exit:  Server is WebSphere");
            }

        }
    }
}
