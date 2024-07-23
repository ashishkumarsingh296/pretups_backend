package com.selftopup.util;

import java.io.File;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;

import com.selftopup.pretups.gateway.businesslogic.PushMessage;
import com.selftopup.alarm.AlarmSender;
import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.CertificateLoader;
import com.selftopup.loadcontroller.LoadControllerCache;
import com.selftopup.pretups.cardgroup.businesslogic.BonusBundleCache;
import com.selftopup.pretups.cardgroup.businesslogic.CardGroupCache;
import com.selftopup.pretups.gateway.businesslogic.MessageGatewayCache;
import com.selftopup.pretups.inter.cache.FileCache;
import com.selftopup.pretups.master.businesslogic.LocaleMasterCache;
import com.selftopup.pretups.master.businesslogic.LookupsCache;
import com.selftopup.pretups.master.businesslogic.NetworkServicesCache;
import com.selftopup.pretups.master.businesslogic.ServiceClassInfoByCodeCache;
import com.selftopup.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.selftopup.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingCache;
import com.selftopup.pretups.network.businesslogic.NetworkCache;
import com.selftopup.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixCache;
import com.selftopup.pretups.p2p.subscriber.businesslogic.RegistrationControlCache;
import com.selftopup.pretups.payment.businesslogic.PaymentMethodCache;
import com.selftopup.pretups.payment.businesslogic.ServicePaymentMappingCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.product.businesslogic.NetworkProductCache;
import com.selftopup.pretups.product.businesslogic.NetworkProductServiceTypeCache;
import com.selftopup.pretups.routing.master.businesslogic.InterfaceRoutingControlCache;
import com.selftopup.pretups.routing.master.businesslogic.ServiceInterfaceRoutingCache;
import com.selftopup.pretups.routing.master.businesslogic.SubscriberRoutingControlCache;
import com.selftopup.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.selftopup.pretups.transfer.businesslogic.TransferRulesCache;

public class ConfigServlet extends HttpServlet {
    private String loggerConfigFile;
    private String constantspropsfile;
    private String _instanceID;
    private static AlarmSender _alarmSender = null;

    public void init(ServletConfig conf) throws ServletException {
        System.out.println("ConfigServlet init() Entered ");
        super.init(conf);
        constantspropsfile = getServletContext().getRealPath(getInitParameter("constantspropsfile"));
        loggerConfigFile = getServletContext().getRealPath(getInitParameter("loggerConfigFile"));
        _instanceID = getInitParameter("instanceCode");
        System.out.println("ConfigServlet constantspropsfile:" + constantspropsfile);
        System.out.println("ConfigServlet loggerConfigFile:" + loggerConfigFile);
        System.out.println("ConfigServlet instanceID:" + _instanceID);
        System.out.println("Config servlet constantspropsfile=" + constantspropsfile + "  loggerConfigFile=" + loggerConfigFile + "_instanceID=" + _instanceID);
        try {
            System.out.println("ConfigServlet Before loading data in Constants.props file");
            Constants.load(constantspropsfile);
            System.out.println("ConfigServlet After Constants properties and before configuring PropertyConfigurator");
            org.apache.log4j.PropertyConfigurator.configure(loggerConfigFile);

            _alarmSender = new AlarmSender();
            _alarmSender.start();
            System.out.println("ConfigServlet After staring AlarmSender");

            // Generate Key object on system start-up if ENDECRYPTION_TYPE is
            // AES
            if ("AES".equals(Constants.getProperty("ENDECRYPTION_TYPE"))) {
                AESKeyStore aesKeyStore = new AESKeyStore();
                boolean credentialsLoad = false;
                String filePath = BTSLUtil.getFilePath(ConfigServlet.class);
                filePath = filePath.substring(0, filePath.lastIndexOf(File.separatorChar));
                credentialsLoad = aesKeyStore.LoadStoreKeyCredentials(filePath + File.separatorChar + "Credentials.txt");
                if (credentialsLoad == false || AESKeyStore.getKey() == null)
                    throw new BTSLBaseException(this, "init", "Unable to load Encryption keyLoad");
            }

            System.out.println("ConfigServlet After configuring PropertyConfigurator and before loading Networks ");
            NetworkCache.loadNetworkAtStartup();
            System.out.println("ConfigServlet After loading Networks and before loading Lookups ");
            LookupsCache.loadLookAtStartup();
            System.out.println("ConfigServlet After loading Lookups and before loading prefrences cache ");
            PreferenceCache.loadPrefrencesOnStartUp();
            System.out.println("ConfigServlet After loading Preferences before loading Network prefixes ");
            NetworkPrefixCache.loadNetworkPrefixesAtStartup();
            System.out.println("ConfigServlet After loading Network prefixes before loading SystemPreferences");
            SystemPreferences.load();
            System.out.println("ConfigServlet After loading SystemPreferences before loading KeywordCache");
            ServiceKeywordCache.loadServiceKeywordCacheOnStartUp();
            System.out.println("ConfigServlet After loading SimProfileCache before MSISDNPrefixInterfaceMappingCache ");
            MSISDNPrefixInterfaceMappingCache.loadPrefixInterfaceMappingAtStartup();
            System.out.println("ConfigServlet After loading MSISDNPrefixInterfaceMappingCache before NetworkInterfaceModuleCache ");
            NetworkInterfaceModuleCache.loadNetworkInterfaceModuleAtStartup();
            System.out.println("ConfigServlet After loading NetworkInterfaceModuleCache before loading ServicePaymentMappingCache ");
            ServicePaymentMappingCache.loadServicePaymentMappingOnStartUp();
            System.out.println("ConfigServlet After loading ServicePaymentMappingCache  before loading TransferRulesCache  "); // table
                                                                                                                               // service_payment_mapping
                                                                                                                               // doesnot
                                                                                                                               // exist
            TransferRulesCache.loadTransferRulesAtStartup();
            System.out.println("ConfigServlet After loading TransferRulesCache before loading NetworkServicesCache ");
            NetworkServicesCache.refreshNetworkServicesList();
            System.out.println("ConfigServlet After loading NetworkServicesCache  before loading MessageGatewayCache ");
            MessageGatewayCache.loadMessageGatewayAtStartup();
            System.out.println("After loading MessageGatewayCache  before loading RequestInterfaceCache ");
            // RequestInterfaceCache.refreshRequestInterface();
            System.out.println("ConfigServlet After loading RequestInterfaceCache ");
            FileCache.loadAtStartUp();
            System.out.println("ConfigServlet After loading FileCache ");
            NetworkProductServiceTypeCache.refreshNetworkProductMapping();
            NetworkProductServiceTypeCache.refreshProductServiceTypeMapping();
            System.out.println("ConfigServlet After loading Product service type cache ");

            // Load Controller Cache
            boolean fileRead = true;
            LoadControllerCache.refreshInstanceLoad(_instanceID, fileRead);
            System.out.println("ConfigServlet After loading Instance Load ");
            LoadControllerCache.refreshNetworkLoad(fileRead);
            System.out.println("ConfigServlet After loading Network Load ");
            LoadControllerCache.refreshInterfaceLoad(fileRead);
            System.out.println("ConfigServlet After loading Interface Load ");
            LoadControllerCache.refreshTransactionLoad(fileRead);
            System.out.println("ConfigServlet After loading Transaction Load ");
            LoadControllerCache.refreshNetworkServiceCounters(_instanceID, fileRead);

            System.out.println("ConfigServlet After refreshNetworkServiceCounters ");
            LoadControllerCache.refreshNetworkServiceHourlyCounters(_instanceID);

            System.out.println("ConfigServlet After loading Transaction Load Before Loading Subscriber Routing Control Cache");
            SubscriberRoutingControlCache.refreshSubscriberRoutingControl();
            System.out.println("ConfigServlet After Loading Subscriber Routing Control Cache Before Registeration Contol Cache ");
            InterfaceRoutingControlCache.refreshInterfaceRoutingControl();
            System.out.println("ConfigServlet After Interface Routing Control Cache ");
            LocaleMasterCache.refreshLocaleMasterCache();
            System.out.println("ConfigServlet After loading local master Cache ");
            RegistrationControlCache.refreshRegisterationControl();
            System.out.println("ConfigServlet After Loading Registeration Contol Cache ");
            ServiceInterfaceRoutingCache.refreshServiceInterfaceRouting();
            System.out.println("ConfigServlet After Loading Service Interface Routing Cache ");
            PaymentMethodCache.loadPaymentMethodCacheOnStartUp();
            System.out.println("ConfigServlet After Loading Payment Method Cache ");
            ServiceSelectorMappingCache.loadServiceSelectorMappingCacheOnStartUp();
            System.out.println("ConfigServlet After Loading Service selector mapping Cache ");
            BonusBundleCache.loadBonusBundleCacheOnStartUp();
            System.out.println("ConfigServlet After Bonus Bundle mapping Cache ");
            MessagesCaches.load(LocaleMasterCache.getLocaleList());
            if (SystemPreferences.HTTPS_ENABLE) {
                System.out.println("Config Servlet before loading certificate ");
                CertificateLoader.loadCertificateOnStartUp();
                System.out.println("Config Servlet after loading certificate ");
            }
            // Added for NetworkProductCache
            NetworkProductCache.loadNetworkProductMapAtStartup();
            System.out.println("ConfigServlet After Loading Network Product Cache ");
            // Added for CardGroupCache
            CardGroupCache.loadCardGroupMapAtStartup();
            System.out.println("ConfigServlet After Loading Card Group Cache ");
            // Added for messageGatewayForCategoryCache
            // MessageGatewayForCategoryCache.loadMeassageGatewayForCategoryMapAtStartup();
            System.out.println("ConfigServlet After Loading Message Gateway For Category Cache");
            // added for serviceClassInfoByCodeCache
            ServiceClassInfoByCodeCache.loadServiceClassByCodeMapAtStartup();
            System.out.println("ConfigServlet After Loading Service Class Info By Code Cache");
        }// end of try
        catch (Exception exception) {
            System.err.println("ConfigServlet init() Exception " + exception);
            exception.printStackTrace();
        }// end of catch
        System.out.println("ConfigServlet init() Exiting");
    }// end of init

    /**
     * Method loadProcessCache
     * This method will load the process cache.
     * 
     * @param p_configFilePathName
     *            String
     * @param p_logConfileFilePathName
     *            String
     * @throws Exception
     */

    public static void loadProcessCache(String p_configFilePathName, String p_logConfileFilePathName) throws Exception {
        System.out.println("ConfigServlet ::loadProcessCache(): Entered constantspropsfile=" + p_configFilePathName + "  loggerConfigFile=" + p_logConfileFilePathName);

        try {
            System.out.println("ConfigServlet ::loadProcessCache(): Before loading data in Constants.props file");
            Constants.load(p_configFilePathName);
            System.out.println("ConfigServlet ::loadProcessCache(): After Constants properties and before configuring PropertyConfigurator");
            org.apache.log4j.PropertyConfigurator.configure(p_logConfileFilePathName);

            _alarmSender = new AlarmSender();
            _alarmSender.start();
            System.out.println("ConfigServlet After staring AlarmSender");
            // This is commeneted for changeID=LOCALEMASTER because message
            // cache will be loaded after local master cache.

            // load Mesages for SMS
            /*
             * ArrayList list=new ArrayList();
             * Locale localeEn=new Locale("en","US");
             * list.add(localeEn);
             * if(Constants.getProperty("SECOND_LOCALE") != null)
             * {
             * String arr[] = Constants.getProperty("SECOND_LOCALE").split("_");
             * if(arr != null && arr.length == 2)
             * {
             * Locale localeOth=new Locale(arr[0],arr[1]);
             * list.add(localeOth);
             * }
             * }//end if
             * MessagesCaches.load(list);
             */
            System.out.println("ConfigServlet ::loadProcessCache(): After configuring PropertyConfigurator and before loading Networks ");

            // Generate Key object on system start-up if ENDECRYPTION_TYPE is
            // AES
            if ("AES".equals(Constants.getProperty("ENDECRYPTION_TYPE"))) {
                AESKeyStore aesKeyStore = new AESKeyStore();
                boolean credentialsLoad = false;
                String filePath = BTSLUtil.getFilePath(ConfigServlet.class);
                filePath = filePath.substring(0, filePath.lastIndexOf(File.separatorChar));
                credentialsLoad = aesKeyStore.LoadStoreKeyCredentials(filePath + File.separatorChar + "Credentials.txt");
                if (credentialsLoad == false || AESKeyStore.getKey() == null)
                    throw new BTSLBaseException("loadProcessCache", "Unable to load Encryption keyLoad");
            }

            NetworkCache.loadNetworkAtStartup();
            System.out.println("ConfigServlet ::loadProcessCache(): After loading Networks and before loading prefrences cache");
            PreferenceCache.loadPrefrencesOnStartUp();
            System.out.println("ConfigServlet After loading prefrences cache and before loading SystemPreferences ");
            SystemPreferences.load();
            System.out.println("ConfigServlet ::loadProcessCache(): After loading SystemPreferences before loading MessageGatewayCache");
            MessageGatewayCache.loadMessageGatewayAtStartup();
            System.out.println("ConfigServlet ::loadProcessCache(): After loading MessageGatewayCache  before loading LacaleMasterCache ");
            LocaleMasterCache.refreshLocaleMasterCache();
            System.out.println("ConfigServlet ::loadProcessCache(): After loading local master Cache ");
            // ChangeID=LOCALEMASTER
            // Load messages cache
            MessagesCaches.load(LocaleMasterCache.getLocaleList());
            // Added for messageGatewayForCategoryCache
            // MessageGatewayForCategoryCache.loadMeassageGatewayForCategoryMapAtStartup();
            System.out.println("ConfigServlet After Loading Message Gateway For Category Cache");

            // TransferProfileCache.loadTransferProfileAtStartup();
            System.out.println("ConfigServlet After Loading Transfer Profile Cache ");

            // TransferProfileProductCache.loadTransferProfileProductsAtStartup();
            System.out.println("ConfigServlet After Loading Transfer Profile Product Cache ");

            boolean fileRead = true;
            String smsInstanceID = Constants.getProperty("INSTANCE_ID");
            if (!BTSLUtil.isNullString(smsInstanceID))

                LoadControllerCache.refreshInstanceLoad(smsInstanceID, fileRead);
            LoadControllerCache.refreshNetworkLoad();

        }// end of try
        catch (Exception exception) {
            System.err.println("ConfigServlet ::loadProcessCache():Exception " + exception);
            exception.printStackTrace();
        }// end of catch
        System.out.println("ConfigServlet ::loadProcessCache(): Exiting");
    }// end of loadProcessCache

    public void destroy() {
        // this code use for send the logout request to IN and close all
        // connection from pool & storing
        // counter object's current state into file.
        System.out.println("ConfigServlet::inside the destroy method");
        LoadControllerCache.writeToFile();
        _alarmSender._running = false;
    }

    /**
     * Method called from various process to destroy the depencies thread
     * created by load method
     * 
     */
    public static void destroyProcessCache() {
        // Flag Set to stop the Alarm Sender Thread spawned in Load Process
        // cache method
        _alarmSender._running = false;
        PushMessage.executor.shutdown();
    }

    public static AlarmSender getAlarmSender() {
        return _alarmSender;
    }

    public static void setAlarmSender(AlarmSender alarmSender) {
        _alarmSender = alarmSender;
    }
}// end of class

