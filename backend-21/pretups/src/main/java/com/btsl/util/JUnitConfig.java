package com.btsl.util;

import com.btsl.common.*;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.QueryConstants;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.SimProfileCache;
import com.btsl.ota.services.businesslogic.UserServicesCache;
import com.btsl.pretups.cardgroup.businesslogic.BonusBundleCache;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupCache;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileCache;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileMinCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductCache;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.user.businesslogic.wallet.UserProductWalletMappingCache;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionCache;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayForCategoryCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeProfileCache;
import com.btsl.pretups.iat.businesslogic.IATCountryMasterCache;
import com.btsl.pretups.iat.businesslogic.IATNWServiceCache;
import com.btsl.pretups.iccidkeymgmt.businesslogic.SimVenderCache;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.loyaltymgmt.businesslogic.LMSProfileCache;
import com.btsl.pretups.master.businesslogic.*;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.network.businesslogic.*;
import com.btsl.pretups.p2p.subscriber.businesslogic.RegistrationControlCache;
import com.btsl.pretups.payment.businesslogic.PaymentMethodCache;
import com.btsl.pretups.payment.businesslogic.ServicePaymentMappingCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.product.businesslogic.NetworkProductCache;
import com.btsl.pretups.product.businesslogic.NetworkProductServiceTypeCache;
import com.btsl.pretups.product.businesslogic.VomsProductsCache;
import com.btsl.pretups.routing.master.businesslogic.InterfaceRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.ServiceInterfaceRoutingCache;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceInstancePriorityCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.transfer.businesslogic.TransferRulesCache;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserDefaultCache;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.btsl.redis.pool.RedisConnectionPool;
import com.btsl.redis.util.HostPort;
import com.btsl.user.businesslogic.*;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.ULocale;
import com.monitorjbl.xlsx.StreamingReader;
import com.restapi.c2s.services.C2SBulkRcServiceImpl;

import com.restapi.channelAdmin.ChannelAdminTransferVO;
import com.restapi.channelenquiry.service.AlertCounterSummaryRequestVO;
import com.restapi.superadmin.requestVO.DeleteCategoryRequestVO;
import com.restapi.superadmin.requestVO.SaveCategoryRequestVO;
import jxl.Cell;
import jxl.Sheet;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Row;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class JUnitConfig {

    static Timestamp ts1 = Timestamp.from(Instant.now());

    public static String SOURCE = null;
    private static boolean INITIALIZED = false;

    private static JUnitConfig INSTANCE = new JUnitConfig();


    private static final Log _log = LogFactory.getLog(JUnitConfig.class);


    private static void initConfig() {


        {
            final String methodName = "init";
            String loggerConfigFile = null;
            String _instanceID = null;
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "ConfigServlet init() Entered ");

            }

            String constantspropsfile = null;
            String kafkapropsfile = null;
            String securityconstantspropsfile = "/WEB-INF/classes/configfiles/SecurityConstants.props";
            String restfulconstantspropsfile = "/WEB-INF/classes/configfiles/RestfulConstants.props";
            String clientblconstantspropsfile = "/WEB-INF/classes/configfiles/ClientBLConstants.props";
            String postgresqueryconstantspropsfile = "/WEB-INF/classes/configfiles/queryConstants/postgresqueryconstants.props";
            String oraclequeryconstantspropsfile = "/WEB-INF/classes/configfiles/queryConstants/oraclequeryconstants.props";
            String pretupsInputValidationpropsfile = "/WEB-INF/classes/configfiles/pretupsInputValidation.props";
            String serverHomePath = "/Users/com/data1/pretupsapp/tomcat_trunk_dev";
            try {
                constantspropsfile = serverHomePath + "/conf/pretups/Constants.props";
                kafkapropsfile = serverHomePath + "/conf/pretups/KafkaConstants.props";
                loggerConfigFile = serverHomePath + "/conf/pretups/LogConfig.props";
                securityconstantspropsfile = serverHomePath + "/conf/pretups/SecurityConstants.props";
                restfulconstantspropsfile = serverHomePath + "/conf/pretups/RestfulConstants.props";
                clientblconstantspropsfile = serverHomePath + "/conf/pretups/ClientBLConstants.props";
                pretupsInputValidationpropsfile = serverHomePath + "/conf/pretups/pretupsInputValidation.props";
                //postgresqueryconstantspropsfile = serverHomePath + "\\conf\\pretups\\postgresqueryconstants.props";
                //oraclequeryconstantspropsfile = serverHomePath + "\\conf\\pretups\\oraclequeryconstants.props";
            } catch (Exception e) {
                _log.info(methodName, "Config servlet 1 constantspropsfile=" + constantspropsfile + "  loggerConfigFile=" + loggerConfigFile);
                _log.errorTrace(methodName, e);
            }
            _log.info(methodName, "Config servlet constantspropsfile=" + constantspropsfile + "  loggerConfigFile=" + loggerConfigFile);
            try {
                try {
               //     org.apache.log4j.PropertyConfigurator.configure(loggerConfigFile);
                } catch (Exception e) {
                    _log.info(methodName, "Config servlet loggerConfigFile=" + loggerConfigFile);
                    _log.errorTrace(methodName, e);

                }
                _log.info(methodName, "ConfigServlet After LogConfig properties and Before Constants.props");
                try {
                    Constants.load(constantspropsfile);
                } catch (Exception e) {
                    _log.info(methodName, "Config servlet constantspropsfile=" + constantspropsfile);
                    _log.errorTrace(methodName, e);
                    throw new BTSLBaseException(INSTANCE, methodName, "Unable to load Constants.props from the given path " + constantspropsfile);
                }
                _log.info(methodName, "ConfigServlet After Constants properties And Before pretupsInputValidation.props file");


                try {
                    PretupsInputValidator.getInstance().load(pretupsInputValidationpropsfile);
                } catch (Exception e) {
                    _log.info(methodName, "Config servlet pretupsInputValidation.props=" + pretupsInputValidationpropsfile);
                    _log.errorTrace(methodName, e);
                    throw new BTSLBaseException(INSTANCE, methodName, "Unable to load pretupsInputValidation.props from the given path " + pretupsInputValidationpropsfile);
                }
                _log.info(methodName, "ConfigServlet After pretupsInputValidation  properties And Before KafkaConstants.props file");


                try {
                    Constants.loadKafkaConf(kafkapropsfile);
                } catch (Exception e) {
                    _log.info(methodName, "Config servlet kafkapropsfile=" + kafkapropsfile);
                    _log.errorTrace(methodName, e);
                    //throw new BTSLBaseException(this, methodName, "Unable to load Kafka.props from the given path " + kafkapropsfile);
                }
                _log.info(methodName, "ConfigServlet After KafkaConstants.props and before SecurityConstants.props");
                _instanceID = Constants.getProperty("INSTANCE_ID");
                _log.info(methodName, "ConfigServlet instanceID:" + _instanceID);
               /* try {
                    SecurityConstants.load(getServletContext().getRealPath(getInitParameter("securityconstantspropsfile")));
                } catch (Exception e) {
                    _log.info(methodName, "Config servlet securityconstantspropsfile=" + securityconstantspropsfile);
                    _log.errorTrace(methodName, e);
                    SecurityConstants.load(securityconstantspropsfile);
                    _log.info(methodName, "securityconstantspropsfile loaded from the location :" + securityconstantspropsfile);
                }*/
                _log.info(methodName, "ConfigServlet After SecurityConstants and Before RestfulConstants properties");
               /* try {
                    RestfulConstants.load(getServletContext().getRealPath(getInitParameter("restfulconstantspropsfile")));
                } catch (Exception e) {
                    _log.info(methodName, "Config servlet restfulconstantspropsfile=" + restfulconstantspropsfile);
                    _log.errorTrace(methodName, e);
                    RestfulConstants.load(restfulconstantspropsfile);
                    _log.info(methodName, "restfulconstantspropsfile loaded from the location :" + restfulconstantspropsfile);
                }*/
                _log.info(methodName, "ConfigServlet After RestfulConstants and Before ClientBLConstants properties");
                /*try {
                    ClientBLConstants.load(getServletContext().getRealPath(getInitParameter("clientblconstantspropsfile")));
                } catch (Exception e) {
                    _log.info(methodName, "Config servlet clientblconstantspropsfile=" + clientblconstantspropsfile);
                    _log.errorTrace(methodName, e);
                    ClientBLConstants.load(clientblconstantspropsfile);
                    _l*//*og.info(methodName, "clientblconstantspropsfile loaded from the location :" + clientblconstantspropsfile);
                }*/
                _log.info(methodName, "ConfigServlet After ClientBLConstants properties");
                _log.info(methodName, "ConfigServlet Before loading data in QueryConstants.props file");
                try {
                    String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
                    if (QueryConstants.DB_POSTGRESQL.equals(dbConnected))
                        try {
                            //QueryConstants.load(postgresqueryconstantspropsfile);
                            QueryConstants.load("D:/GitCode/pretups-backend/pretups/src/main/webapp/WEB-INF/classes/configfiles/queryConstants/postgresqueryconstants.props");
                        } catch (Exception e) {
                            _log.info(methodName, "Config servlet postgresqueryconstantspropsfile=" + postgresqueryconstantspropsfile);
                            _log.errorTrace(methodName, e);
                            QueryConstants.load(postgresqueryconstantspropsfile);
                        }
                    else if (QueryConstants.DB_ORACLE.equals(dbConnected))
                        try {
                            //QueryConstants.load(oraclequeryconstantspropsfile);
                            QueryConstants.load("/Users/com/data1/configfiles/queryConstants/oraclequeryconstants.props");
                            //_log.info(methodName, "Config servlet oraclequeryconstantspropsfile loaded from =" + getServletContext().getRealPath("/WEB-INF/classes/configfiles/queryConstants/oraclequeryconstants.props"));
                        } catch (Exception e) {
                            _log.info(methodName, "Config servlet oraclequeryconstantspropsfile=" + oraclequeryconstantspropsfile);
                            _log.errorTrace(methodName, e);
                            QueryConstants.load(oraclequeryconstantspropsfile);
                        }
                } catch (Exception e) {
                    _log.info(methodName, "Config servlet postgresqueryconstantspropsfile=" + postgresqueryconstantspropsfile + " oraclequeryconstantspropsfile=" + oraclequeryconstantspropsfile);
                    _log.errorTrace(methodName, e);
                    throw new BTSLBaseException(INSTANCE, methodName, "Unable to load database specific property file ");
                }
                Connection con = null;


                try {
                    if (PretupsI.REDIS_ENABLE.equals(Constants.getProperty("REDIS_ENABLE"))) {
                        HostPort.loadRedisServerDetailsAtStartup();
                        RedisConnectionPool.initializeSettings();
                    }
                } catch (Exception e) {
                    _log.info(methodName, "Config servlet redis Server Details");
                    _log.errorTrace(methodName, e);
                    throw new BTSLBaseException(INSTANCE, methodName, "Unable to initialize redis pool ");
                }
                //_alarmSender = new AlarmSender();
                //_alarmSender.start();
                _log.info(methodName, "ConfigServlet After staring AlarmSender");
                if ("AES".equals(Constants.getProperty("ENDECRYPTION_TYPE"))) {
                    final AESKeyStore aesKeyStore = new AESKeyStore();
                    boolean credentialsLoad = false;
                    String filePath = BTSLUtil.getFilePath(ConfigServlet.class);
                    filePath = filePath.substring(0, filePath.lastIndexOf(File.separatorChar));
                    credentialsLoad = aesKeyStore.LoadStoreKeyCredentials(filePath + File.separatorChar + "Credentials.txt");
                    if (credentialsLoad == false || AESKeyStore.getKey() == null) {
                        throw new BTSLBaseException(INSTANCE, methodName, "Unable to load Encryption keyLoad");
                    }
                }

                ExecutorService executor = Executors.newFixedThreadPool(30);

                _log.info(methodName, "ConfigServlet After configuring PropertyConfigurator and before loading Products ");
                VomsProductsCache.loadProductsAtStartup();
                _log.info(methodName, "ConfigServlet After loading products and before loading Networks ");

                _log.info(methodName, "ConfigServlet After loading products and before loading Networks ");
                NetworkCache.loadNetworkAtStartup();
                _log.info(methodName, "ConfigServlet After loading Networks and before loading Lookups ");
                executor.execute(new LookupsCache());    //LookupsCache.loadLookAtStartup();
                _log.info(methodName, "ConfigServlet After loading Lookups and before loading sublookups cache ");
                executor.execute(new SubLookupsCache());    //LookupsCache.loadLookAtStartup();
                _log.info(methodName, "ConfigServlet After loading subLookups and before loading prefrences cache ");
                PreferenceCache.loadPrefrencesOnStartUp();
                _log.info(methodName, "ConfigServlet After loading Preferences before loading Network prefixes ");
                executor.execute(new NetworkPrefixCache());    //NetworkPrefixCache.loadNetworkPrefixesAtStartup();
                _log.info(methodName, "ConfigServlet After loading Network prefixes before loading SystemPreferences");
                executor.execute(new SystemPreferences());    //SystemPreferences.load();
                _log.info(methodName, "ConfigServlet After loading SystemPreferences before loading KeywordCache");
                executor.execute(new ServiceKeywordCache());    //ServiceKeywordCache.loadServiceKeywordCacheOnStartUp();
                // ServiceInstancePriorityCache.loadServiceInstancePriorityCacheOnStartUp();
                //_log.info(methodName, "ConfigServlet After loading ServiceInstancePriorityCache before SimProfileCache ");
                executor.execute(new SimProfileCache());    //SimProfileCache.refreshSimProfileList();
                _log.info(methodName, "ConfigServlet After loading SimProfileCache before MSISDNPrefixInterfaceMappingCache ");
                executor.execute(new MSISDNPrefixInterfaceMappingCache());    //MSISDNPrefixInterfaceMappingCache.loadPrefixInterfaceMappingAtStartup();
                _log.info(methodName, "ConfigServlet After loading MSISDNPrefixInterfaceMappingCache before NetworkInterfaceModuleCache ");
                executor.execute(new NetworkInterfaceModuleCache());    //NetworkInterfaceModuleCache.loadNetworkInterfaceModuleAtStartup();
                _log.info(methodName, "ConfigServlet After loading NetworkInterfaceModuleCache before loading ServicePaymentMappingCache ");
                executor.execute(new ServicePaymentMappingCache());    //ServicePaymentMappingCache.loadServicePaymentMappingOnStartUp();
                _log.info(methodName, "ConfigServlet After loading ServicePaymentMappingCache  before loading TransferRulesCache  "); // table
                executor.execute(new TransferRulesCache());    //TransferRulesCache.loadTransferRulesAtStartup();
                _log.info(methodName, "ConfigServlet After loading TransferRulesCache before loading NetworkServicesCache ");
                executor.execute(new NetworkServicesCache());    //NetworkServicesCache.refreshNetworkServicesList();
                _log.info(methodName, "ConfigServlet After loading NetworkServicesCache  before loading MessageGatewayCache ");
                executor.execute(new MessageGatewayCache());    //MessageGatewayCache.loadMessageGatewayAtStartup();
                _log.info(methodName, "After loading MessageGatewayCache  before loading RequestInterfaceCache ");
                // RequestInterfaceCache.refreshRequestInterface();
                _log.info(methodName, "ConfigServlet After loading RequestInterfaceCache ");
                executor.execute(new FileCache());    //FileCache.loadAtStartUp();
                _log.info(methodName, "ConfigServlet After loading FileCache ");
                executor.execute(new NetworkProductServiceTypeCache());    //NetworkProductServiceTypeCache.refreshNetworkProductMapping();
                //executor.execute(new LookupsCache());	//NetworkProductServiceTypeCache.refreshProductServiceTypeMapping();
                _log.info(methodName, "ConfigServlet After loading Product service type cache ");
                final boolean fileRead = true;
                executor.execute(new LoadControllerCache(_instanceID, fileRead));    //LoadControllerCache.refreshInstanceLoad(_instanceID, fileRead);
                _log.info(methodName, "ConfigServlet After loading Instance Load ");
            /*LoadControllerCache.refreshNetworkLoad(fileRead);
            _log.info(methodName, "ConfigServlet After loading Network Load ");
            LoadControllerCache.refreshInterfaceLoad(fileRead);
            _log.info(methodName, "ConfigServlet After loading Interface Load ");
            LoadControllerCache.refreshTransactionLoad(fileRead);
            _log.info(methodName, "ConfigServlet After loading Transaction Load ");
            LoadControllerCache.refreshNetworkServiceCounters(_instanceID, fileRead);
            _log.info(methodName, "ConfigServlet After refreshNetworkServiceCounters ");
            LoadControllerCache.refreshNetworkServiceHourlyCounters(_instanceID);*/
                _log.info(methodName, "ConfigServlet After loading Transaction Load Before Loading Subscriber Routing Control Cache");
                executor.execute(new SubscriberRoutingControlCache());    //SubscriberRoutingControlCache.refreshSubscriberRoutingControl();
                _log.info(methodName, "ConfigServlet After Loading Subscriber Routing Control Cache Before Registeration Contol Cache ");
                executor.execute(new InterfaceRoutingControlCache());    //InterfaceRoutingControlCache.refreshInterfaceRoutingControl();
                _log.info(methodName, "ConfigServlet After Interface Routing Control Cache ");
                executor.execute(new LocaleMasterCache());    //LocaleMasterCache.refreshLocaleMasterCache();
                _log.info(methodName, "ConfigServlet After loading local master Cache ");
                executor.execute(new RegistrationControlCache());    //RegistrationControlCache.refreshRegisterationControl();
                _log.info(methodName, "ConfigServlet After Loading Registeration Contol Cache ");
                executor.execute(new ServiceInterfaceRoutingCache());    //ServiceInterfaceRoutingCache.refreshServiceInterfaceRouting();
                _log.info(methodName, "ConfigServlet After Loading Service Interface Routing Cache ");
                executor.execute(new GroupTypeProfileCache());    //GroupTypeProfileCache.loadGroupTypeProfilesAtStartup();
                _log.info(methodName, "ConfigServlet After Loading Group type profile Cache ");
                executor.execute(new SimVenderCache());    //SimVenderCache.loadMessageGatewayAtStartup();
                _log.info(methodName, "ConfigServlet After Loading Sim Vender cache Cache ");
                executor.execute(new PaymentMethodCache());    //PaymentMethodCache.loadPaymentMethodCacheOnStartUp();
                _log.info(methodName, "ConfigServlet After Loading Payment Method Cache ");
                executor.execute(new ServiceSelectorMappingCache());    //ServiceSelectorMappingCache.loadServiceSelectorMappingCacheOnStartUp();
                _log.info(methodName, "ConfigServlet After Loading Service selector mapping Cache ");
                executor.execute(new IATCountryMasterCache());    //IATCountryMasterCache.loadIATCountryMasterCache();
                _log.info(methodName, "ConfigServlet After Loading IAT Country Master Cache ");
                executor.execute(new IATNWServiceCache());    //IATNWServiceCache.loadIATNWServiceCache();
                _log.info(methodName, "ConfigServlet After Loading IAT Network Cache ");
                executor.execute(new BonusBundleCache());    //BonusBundleCache.loadBonusBundleCacheOnStartUp();
                _log.info(methodName, "ConfigServlet After Bonus Bundle mapping Cache ");
                //MessagesCaches.load(LocaleMasterCache.getLocaleList());
                Boolean isHttpEnable = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.HTTPS_ENABLE);
                if (isHttpEnable) {
                    _log.info(methodName, "Config Servlet before loading certificate ");
                    executor.execute(new CertificateLoader());    //CertificateLoader.loadCertificateOnStartUp();
                    _log.info(methodName, "Config Servlet after loading certificate ");
                }
                executor.execute(new NetworkProductCache());    //NetworkProductCache.loadNetworkProductMapAtStartup();
                _log.info(methodName, "ConfigServlet After Loading Network Product Cache ");
                executor.execute(new CardGroupCache());    //CardGroupCache.loadCardGroupMapAtStartup();
                _log.info(methodName, "ConfigServlet After Loading Card Group Cache ");
                executor.execute(new MessageGatewayForCategoryCache());    // MessageGatewayForCategoryCache.loadMeassageGatewayForCategoryMapAtStartup();
                _log.info(methodName, "ConfigServlet After Loading Message Gateway For Category Cache");
                executor.execute(new ServiceClassInfoByCodeCache());    //ServiceClassInfoByCodeCache.loadServiceClassByCodeMapAtStartup();
                _log.info(methodName, "ConfigServlet After Loading Service Class Info By Code Cache");
                executor.execute(new TransferProfileCache());    //TransferProfileCache.loadTransferProfileAtStartup();
                _log.info(methodName, "ConfigServlet After Loading Transfer Profile Cache ");
                executor.execute(new TransferProfileProductCache());    //TransferProfileProductCache.loadTransferProfileProductsAtStartup();
                _log.info(methodName, "ConfigServlet After Loading Transfer Profile Product Cache ");
                executor.execute(new UserDefaultCache());    //UserDefaultCache.loadUserDefaultConfigAtStartup();
                _log.info(methodName, "ConfigServlet After Loading User Default Cache ");
                executor.execute(new UserServicesCache());    //UserServicesCache.loadServicesAtStartup();
                _log.info(methodName, "ConfigServlet After Loading User Services Cache ");
                executor.execute(new ServiceSelectorInterfaceMappingCache());    //ServiceSelectorInterfaceMappingCache.loadServSelInterfMappingOnStartup();
                _log.info(methodName, "ConfigServlet After ServiceSelectorInterfaceMappingCache Cache ");
                executor.execute(new ServiceInstancePriorityCache());    //ServiceInstancePriorityCache.loadServiceInstancePriorityCacheOnStartUp();
                executor.execute(new CommissionProfileCache());    //CommissionProfileCache.loadCommissionProfilesAtStartup();
                executor.execute(new UserStatusCache());    //UserStatusCache.loadUserStatusDetailsAtStartUp();
                executor.execute(new LMSProfileCache());    //LMSProfileCache.loadLMSProfilesAtStartup();
                executor.execute(new CurrencyConversionCache());    //CurrencyConversionCache.loadCurrencyConversionDetailsAtStartUp();
                _log.info(methodName, "ConfigServlet After Currency Conversion Cache ");
                Boolean isUserProductMultipleWallet = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
                if (isUserProductMultipleWallet) {
                    executor.execute(new UserProductWalletMappingCache());    //UserProductWalletMappingCache.loadUserProductWalletMappingOnStartUp();
                }
                executor.execute(new CommissionProfileMinCache());    //CommissionProfileMinCache.loadMinCommissionDetailsAtStartUp();
                _log.info(methodName, "ConfigServlet After CommissionProfileMinCache ");
                /*if (PretupsI.YES.equalsIgnoreCase(Constants.getProperty("NEW_OAM"))) {
                    oamSim = new OAMSimulator(constantspropsfile);
                    oamSim.startCallback();
                }*/
                executor.execute(new CellIdCache());    //CellIdCache.loadCellIdAtStartUp();
                _log.info(methodName, "ConfigServlet After CellIdCache ");

                executor.shutdown();
                while (!executor.isTerminated()) {
                }
                MComConnectionI mcomCon = new MComConnection();
                con = mcomCon.getConnection();
                if (mcomCon != null) {
                    mcomCon.close("ConfigServlet#init");
                    mcomCon = null;
                }
                con = null;
                _log.info(methodName, "End ConfigServlet loading ................... ");


                //Additional handling in Caches - ex -
                LookupsCache.setDummayEntry();
               // loadLookupDropDown
                Map map = LookupsCache.get_lookupMap();




                //BTSLUtil
                mockStatic(BTSLDateUtil.class);
                //BTSLDateUtil.getGregorianDateInString
                Mockito.when(BTSLDateUtil.getGregorianDateInString(Mockito.anyString())).thenReturn("01/01/25");

                com.ibm.icu.util.Calendar cal;
                String calenderType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_TYPE);
                String calendarType = BTSLUtil.getTrimmedValue(calenderType);
                if(PretupsI.PERSIAN.equalsIgnoreCase(calendarType)){
                    ULocale locale = new ULocale(PretupsI.LOCALE_PERSIAN);
                    cal = com.ibm.icu.util.PersianCalendar.getInstance(locale);
                } else {
                    cal = GregorianCalendar.getInstance();
                }

                Mockito.when(BTSLDateUtil.getInstance()).thenReturn(cal);


//                mockStatic(BTSLUtil.class);
                //BTSLDateUtil.getGregorianDateInString
 //               Mockito.when(BTSLUtil.isValideFileName(Mockito.any())).thenReturn(true);


                mockStatic(PretupsRestUtil.class);
                Mockito.when(PretupsRestUtil.getMessageString(Mockito.anyString())).thenReturn("String");


            } catch (BTSLBaseException exception) {
                System.err.println("ConfigServlet init() Exception " + exception);
                _log.errorTrace(methodName, exception);
                _log.errorTrace(methodName, exception);
            } catch (Exception exception) {
                System.err.println("ConfigServlet init() Exception " + exception);
                _log.errorTrace(methodName, exception);
                _log.errorTrace(methodName, exception);
            }// end of catch

            try {
                _log.info("methodName", "starting AppDBResourceAnalyzer.startUtilizationThread");
           //     AppDBResourceAnalyzer.startUtilizationThread();
            } catch (Exception e) {
                _log.info(methodName, "Exception while invoking AppDBResourceAnalyzer.startUtilizationThread");
            }


            _log.info(methodName, "ConfigServlet init() Exiting");
        }

    }

    private static void initialize() {
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_DEFAULT_SMSPIN")).thenReturn("0000");
        when(PreferenceCache.getSystemPreferenceValueAsString("CREPT_MAX_DATEDIFF")).thenReturn("60");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_DATEDIFF")).thenReturn("30");
        when(PreferenceCache.getSystemPreferenceValueAsString("REPORT_MAX_DATEDIFF")).thenReturn("20");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_DEFAULT_PASSWORD")).thenReturn("0000");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_DEFAULT_SMSPIN")).thenReturn("0000");
        when(PreferenceCache.getSystemPreferenceValueAsString("SYSTEM_DATE_FORMAT")).thenReturn("dd/MM/yy");
        when(PreferenceCache.getSystemPreferenceValueAsString("PIN_PWD_ALERT_DYS")).thenReturn("365");
        when(PreferenceCache.getSystemPreferenceValueAsString("PWD_CHANGE_NOT_REQ")).thenReturn("CCE");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXTERNAL_TXN_UNIQUE")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("RC_NBK_AL_DAYS_DIF")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("STAFF_USER_COUNT")).thenReturn("25");
        when(PreferenceCache.getSystemPreferenceValueAsString("DCT_VOUCHER_EN")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("PRO_TRF_ST_LVL_CODE")).thenReturn("1");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_PROMO_TRF_APP")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("MNP_ALLOWED")).thenReturn("True");
        when(PreferenceCache.getSystemPreferenceValueAsString("PORT_USR_SUSPEND_REQ")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_SNO_MIN_LENGTH")).thenReturn("9");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_SNO_MAX_LENGTH")).thenReturn("16");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_PIN_MIN_LENGTH")).thenReturn("9");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_PIN_MAX_LENGTH")).thenReturn("14");
        when(PreferenceCache.getSystemPreferenceValueAsString("LOW_BAL_MSGGATEWAY")).thenReturn("LOWBALGW");
        when(PreferenceCache.getSystemPreferenceValueAsString("RC_NBK_DIF_RQ_TO_IN")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("RC_AL_AC_STATUS_NBK")).thenReturn("DEACT,SUS");
        when(PreferenceCache.getSystemPreferenceValueAsString("RC_NBK_AMT_DEDCTED")).thenReturn("2000");
        when(PreferenceCache.getSystemPreferenceValueAsString("PRC_AL_AC_STATUS_NBK")).thenReturn("Active");
        when(PreferenceCache.getSystemPreferenceValueAsString("PRC_NBK_DIF_RQ_TO_IN")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("PRC_NBK_AL_DAYS_DIF")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("PRC_NBK_AMT_DEDCTED")).thenReturn("2000");
        when(PreferenceCache.getSystemPreferenceValueAsString("AUTO_PAYMENT_METHOD")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("PTUPS_MOBQUTY_MERGD")).thenReturn("TRUE");
        when(PreferenceCache.getSystemPreferenceValueAsString("ICCID_CHECKSTRING")).thenReturn("9862");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_ALLOW_SELF_TOPUP")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("PIN_SEND_TO")).thenReturn("C");
        when(PreferenceCache.getSystemPreferenceValueAsString("GRPT_CHRG_ALLOWED")).thenReturn("SMSC,USSD");
        when(PreferenceCache.getSystemPreferenceValueAsString("GRPT_CTRL_ALLOWED")).thenReturn("SMSC,USSD");
        when(PreferenceCache.getSystemPreferenceValueAsString("GRPT_CONTROL_LEVEL")).thenReturn("M");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_DEF_SEL_BILLPAY")).thenReturn("C");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXT_CODE_MAND_FOC")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("SECOND_LANG_CHARSET")).thenReturn("ISO-8859-15");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2C_RET_PARENT_ONLY")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_BUDDIES_ALLOWED")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("DECIML_ALOW_SERVICES")).thenReturn("RC,GRC,PPB,EVD,INTRRC,PSTNRC");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXTERNAL_DATE_FORMAT")).thenReturn("dd/MM/yy");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_ALLOW_SELF_UTILI")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_ALOW_SLF_UTLTBIL")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_S_CRBKAM_UTLTBIL")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("NOTIFY_SRVCCLS_REC")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("MIN_ID_NUM_LNTH")).thenReturn("8");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_ID_NUM_LNTH")).thenReturn("12");
        when(PreferenceCache.getSystemPreferenceValueAsString("XML_MAX_RCD_SUM_RESP")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("XML_DATE_RANGE")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("XML_DFT_DATE_RANGE")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("USE_HOME_STOCK")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("OTH_ID_PREFIX_LIST")).thenReturn("");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALPHANUM_ID_NUM_ALWD")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("ID_NUM_VAL_TYPE")).thenReturn("M");
        when(PreferenceCache.getSystemPreferenceValueAsString("USE_DISPLAY_AMT")).thenReturn("TRUE");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_DENOMINATION_VAL")).thenReturn("100000");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_MAXERRORCOUNTEN")).thenReturn("1000000");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_MAXERRORCOUNTOT")).thenReturn("1000000");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_SERIALNO_LENGTH")).thenReturn("16");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_UPEXPHOURS")).thenReturn("4");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_OFFPEAKHRS")).thenReturn("0-24");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_MAX_VOUCHER_EN")).thenReturn("1000000");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_MAX_VOUCHER_OT")).thenReturn("1000000");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_MAXBATCHDY")).thenReturn("3");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_DATE_FORMAT")).thenReturn("dd/MM/yy");
        when(PreferenceCache.getSystemPreferenceValueAsString("PEERTRFMINLMT")).thenReturn("500");
        when(PreferenceCache.getSystemPreferenceValueAsString("PEERTRFMAXLMT")).thenReturn("1500000000");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_PER_TRANSFER")).thenReturn("90");
        when(PreferenceCache.getSystemPreferenceValueAsString("DAY_SDR_MX_TRANS_AMT")).thenReturn("100000");
        when(PreferenceCache.getSystemPreferenceValueAsString("DAY_SDR_MX_TRANS_NUM")).thenReturn("3");
        when(PreferenceCache.getSystemPreferenceValueAsString("MIN_RES_BALTYPE")).thenReturn("AMT");
        when(PreferenceCache.getSystemPreferenceValueAsString("MIN_RESIDUAL_BAL")).thenReturn("10000");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_MAX_PIN_BLK_CONT")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("MIN_VALIDITY_DAYS")).thenReturn("400");
        when(PreferenceCache.getSystemPreferenceValueAsString("SUCC_BLOCK_TIME")).thenReturn("0");
        when(PreferenceCache.getSystemPreferenceValueAsString("DA_CONFAIL_COUNT")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("DA_SUCTRAN_ALLWDCOUN")).thenReturn("300");
        when(PreferenceCache.getSystemPreferenceValueAsString("DA_FAIL_TXN_ALLWDCOU")).thenReturn("40");
        when(PreferenceCache.getSystemPreferenceValueAsString("DA_TOTXN_AMT_ALLWDCO")).thenReturn("300000000");
        when(PreferenceCache.getSystemPreferenceValueAsString("TAX2_ON_TAX1")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("MIN_MSISDN_LENGTH")).thenReturn("6");
        when(PreferenceCache.getSystemPreferenceValueAsString("SCC_BLCK_TIME_P2P")).thenReturn("60");
        when(PreferenceCache.getSystemPreferenceValueAsString("DA_REC_AMT_ALLWD_P2P")).thenReturn("1500000");
        when(PreferenceCache.getSystemPreferenceValueAsString("DA_SUCTRAN_ALLWD_P2P")).thenReturn("545454");
        when(PreferenceCache.getSystemPreferenceValueAsString("DEF_FRCXML_SEL_P2P")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("DEF_FRCXML_SEL_C2S")).thenReturn("");
        when(PreferenceCache.getSystemPreferenceValueAsString("GROUP_ROLE_ALLOWED")).thenReturn("Y");
        when(PreferenceCache.getSystemPreferenceValueAsString("CHNL_PLAIN_SMS_SEPT")).thenReturn(" ");
        when(PreferenceCache.getSystemPreferenceValueAsString("FOC_ODR_APPROVAL_LVL")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_ENQ_BAL_HIDE")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("SMS_TO_LOGIN_USER")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("DEFAULT_MESSGATEWAY")).thenReturn("SMSC");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_MIN_PIN_LENGTH")).thenReturn("4");
        when(PreferenceCache.getSystemPreferenceValueAsString("SEP_TRF_CTRL")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_SEP_TRFR_COUNT")).thenReturn("FALSE");
        when(PreferenceCache.getSystemPreferenceValueAsString("USER_CODE_REQUIRED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_PIN_BLK_RST_DRTN")).thenReturn("1440");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_DYS_ATR_CNGE_PIN")).thenReturn("365");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_DYS_ATR_CNGE_PIN")).thenReturn("365");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_MSISDN_TEXTBOX")).thenReturn("15");
        when(PreferenceCache.getSystemPreferenceValueAsString("MSISDN_PREFIX_LENGTH")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("AMOUNT_MULT_FACTOR")).thenReturn("100");
        when(PreferenceCache.getSystemPreferenceValueAsString("PIN_REQUIRED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("PIN_LENGTH")).thenReturn("4");
        when(PreferenceCache.getSystemPreferenceValueAsString("DEFAULT_LANGUAGE")).thenReturn("en");
        when(PreferenceCache.getSystemPreferenceValueAsString("SKEY_REQUIRED")).thenReturn("N");
        when(PreferenceCache.getSystemPreferenceValueAsString("MON_SDR_MX_TRANS_NUM")).thenReturn("999999999");
        when(PreferenceCache.getSystemPreferenceValueAsString("MON_SDR_MX_TRANS_AMT")).thenReturn("999999999");
        when(PreferenceCache.getSystemPreferenceValueAsString("WK_SDR_MX_TRANS_AMT")).thenReturn("99999999");
        when(PreferenceCache.getSystemPreferenceValueAsString("WK_SDR_MX_TRANS_NUM")).thenReturn("5000000");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_PIN_BLK_RST_DRTN")).thenReturn("1440");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_MAX_PIN_BLK_CONT")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("MVD_MAX_VOUCHER")).thenReturn("10000");
        when(PreferenceCache.getSystemPreferenceValueAsString("SYSTEM_DTTIME_FORMAT")).thenReturn("dd/MM/yy HH:mm:ss");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_AMB_CR_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("FINANCIAL_YEAR_START")).thenReturn("0");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXTERNAL_TXN_NUMERIC")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("CHK_BLK_LST_STAT")).thenReturn("FALSE");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXTTXNMANDT_DOMAINTP")).thenReturn("DISTB_CHAN,CORPORATE");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXTTXNMANDT_FOC")).thenReturn("4");
        when(PreferenceCache.getSystemPreferenceValueAsString("NOTIFI_SRVCCLS_REC")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("NOTIF_SRVCCLS_RECC2S")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("NOTIFI_SRVCCLS_SEN")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("NOTIFSRVCLS_REC_BLPY")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("SECOND_LANG_ENCODING")).thenReturn("ISO-8859-15");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALLOW_SELF_BILLPAY")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_AMB_CR_ALOW_PPBP")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("SEP_OUTSIDE_TXN_CTRL")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("DEF_FRCXML_SEL_BLPY")).thenReturn("");
        when(PreferenceCache.getSystemPreferenceValueAsString("PP_DEF_STATUS_ACT")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("SPACE_ALLOW_IN_LOGIN")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("DEF_WEB_GW_CODE")).thenReturn("WEB");
        when(PreferenceCache.getSystemPreferenceValueAsString("MSISDN_PREFIX_LIST")).thenReturn("236,+236,0236");
        when(PreferenceCache.getSystemPreferenceValueAsString("DYS_AFTER_CHANGE_PWD")).thenReturn("365");
        when(PreferenceCache.getSystemPreferenceValueAsString("REQ_CUSER_DLT_APP")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALWD_SERVICES_NUMBCK")).thenReturn("RC,PRC");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXTTXNLEVEL")).thenReturn("1,2");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXTTXNMANDT")).thenReturn("1,2");
        when(PreferenceCache.getSystemPreferenceValueAsString("USE_PPAID_CONTROLS")).thenReturn("FALSE");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_PLAIN_SMS_ALLWD")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("LANGAUGES_SUPPORTED")).thenReturn("0:en#US,1:fr#NG");
        when(PreferenceCache.getSystemPreferenceValueAsString("DEFAULT_COUNTRY")).thenReturn("US");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_USER_REGTN_REQ")).thenReturn("Y");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALLOW_SELF_TOPUP")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("OPERATOR_UTIL_C")).thenReturn("com.btsl.pretups.util.OperatorUtil");
        when(PreferenceCache.getSystemPreferenceValueAsString("USRLEVELAPPROVAL")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("PROD_REQ")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("TR_ID_REQ")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("LG_MN_REQ")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("DEF_PROD")).thenReturn("ETOPUP");
        when(PreferenceCache.getSystemPreferenceValueAsString("SMS_P_INDX")).thenReturn("03");
        when(PreferenceCache.getSystemPreferenceValueAsString("ERROR_FOR_FAIL_CT")).thenReturn("76711");
        when(PreferenceCache.getSystemPreferenceValueAsString("FRSTAPPLM")).thenReturn("10000");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAXTRNSFR")).thenReturn("5000000");
        when(PreferenceCache.getSystemPreferenceValueAsString("MINTRNSFR")).thenReturn("1000");
        when(PreferenceCache.getSystemPreferenceValueAsString("PEERTRNSFR")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("SKEYEXPIRYSEC")).thenReturn("300");
        when(PreferenceCache.getSystemPreferenceValueAsString("CIRCLEMAXLMT")).thenReturn("5000000000");
        when(PreferenceCache.getSystemPreferenceValueAsString("SKEYLENGTH")).thenReturn("4");
        when(PreferenceCache.getSystemPreferenceValueAsString("CIRCLEMINLMT")).thenReturn("100");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_AMB_CR_ALLOWED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_CR_BACK_ALLOWED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_PWD_BLOCK_COUNT")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("PWD_BLK_RST_DURATION")).thenReturn("24");
        when(PreferenceCache.getSystemPreferenceValueAsString("TRSFR_DEF_SRVCTYPE")).thenReturn("PRE");
        when(PreferenceCache.getSystemPreferenceValueAsString("USER_HIERARCHY_SIZE")).thenReturn("50");
        when(PreferenceCache.getSystemPreferenceValueAsString("DEFAULT_PRODUCT")).thenReturn("101");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_MAX_PIN_LENGTH")).thenReturn("4");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_LOGIN_PWD_LENGTH")).thenReturn("8");
        // when(PreferenceCache.getSystemPreferenceValueAsString("MIN_LOGIN_PWD_LENGTH")).thenReturn(6);
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_SMS_PIN_LENGTH")).thenReturn("4");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_MSISDN_LENGTH")).thenReturn("15");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_PLAIN_SMS_SEPT")).thenReturn("");
        when(PreferenceCache.getSystemPreferenceValueAsString("MIN_SMS_PIN_LENGTH")).thenReturn("4");
        when(PreferenceCache.getSystemPreferenceValueAsString("USER_EVENT_REMARKS")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("MULT_CRE_TRA_DED_ACC_SEP")).thenReturn(",");
        when(PreferenceCache.getSystemPreferenceValueAsString("APPROVER_CAN_EDIT")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("VAL_DAYS_TO_CHK_VLUP")).thenReturn("31");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2C_SMS_NOTIFY")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("NEG_ADD_COMM_APPLY")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("DISSABLE_BUTTON_LIST")).thenReturn("NO_BUTTON");
        when(PreferenceCache.getSystemPreferenceValueAsString("SMS_MMS_ALLOWED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALLOW_SELF_TOPUP_FRC")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("STK_REG_ICCID")).thenReturn("FALSE");
        when(PreferenceCache.getSystemPreferenceValueAsString("ACT_BONUS_REDEM_DUR")).thenReturn("15");
        when(PreferenceCache.getSystemPreferenceValueAsString("ACT_BONUS_MIN_AMOUNT")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOLUME_CALC_ALLOWED")).thenReturn("N");
        when(PreferenceCache.getSystemPreferenceValueAsString("POSITIVE_COMM_APPLY")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("CHECK_REC_TXN_AT_IAT")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_IAT_RUNNING")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXTTXNMANDT_DP")).thenReturn("4");
        when(PreferenceCache.getSystemPreferenceValueAsString("DP_ODR_APPROVAL_LVL")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXT_CODE_MAND_DP")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("DP_SMS_NOTIFY")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("DP_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("LAST_X_TRF_DAYS_NO")).thenReturn("1");
        when(PreferenceCache.getSystemPreferenceValueAsString("SHA2_FAMILY_TYPE")).thenReturn("SHA-256");
        when(PreferenceCache.getSystemPreferenceValueAsString("HTTPS_ENABLE")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("NWADM_CROSS_ALLOW")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("STAFF_AS_USER")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("RVERSE_TRN_EXPIRY")).thenReturn("7");
        when(PreferenceCache.getSystemPreferenceValueAsString("RVE_C2S_TRN_EXPIRY")).thenReturn("999999");
        when(PreferenceCache.getSystemPreferenceValueAsString("PRVT_RCHRG_ALLOW")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("MVD_MIN_VOUCHER")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("NAMEEMBOSS_SEPT")).thenReturn("_");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_MIN_EXPIRY_DAYS")).thenReturn("1");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALLOW_SELF_TOPUP_BRC")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("CARD_GROUP_BONUS_RANGE")).thenReturn("500");
        when(PreferenceCache.getSystemPreferenceValueAsString("LOGIN_SPCL_CHAR_ALLOW")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_PIN_ENCRIPT_ALLOWED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("FEE_VALDAYS_TO_EXT")).thenReturn("800,30");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_CT_WITH_VAL_UPDN")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALLOW_SELF_EVR")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_PWD_BLK_EXP_DRN")).thenReturn("999999999999");
        when(PreferenceCache.getSystemPreferenceValueAsString("CP2P_PIN_BLK_EXP_DRN")).thenReturn("3");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_PIN_BLK_EXP_DRN")).thenReturn("999999999999");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_SEPARATE_RPT_DB")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("DISABLE_SEND_PIN_BTN")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("ZERO_BAL_THRESHOLD_VALUE")).thenReturn("0");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_SEP_BONUS_REQUIRED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("DISABLE_UNBLOCK_PASWD_BTN")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("MESSAGE_TO_PRIMARY_REQUIRED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("SECONDARY_NUMBER_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_REG_EXPIRY_PERIOD")).thenReturn("999999999");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_RANDOM_PIN_GENERATE")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("WEB_RANDOM_PWD_GENERATE")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("BATCH_USER_PASSWD_MODIFY_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2C_BATCH_APPROVAL_LVL")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("RESET_PWD_EXP_TIME_IN_HOURS")).thenReturn("999999999");
        when(PreferenceCache.getSystemPreferenceValueAsString("RESET_PIN_EXP_TIME_IN_HOURS")).thenReturn("999999999");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_CARD_GROUP_SLAB_COPY")).thenReturn("N");
        when(PreferenceCache.getSystemPreferenceValueAsString("AUTO_PIN_GENERATE_ALLOW")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("AUTO_PWD_GENERATE_ALLOW")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("PSWD_EXP_TIME_IN_HOUR_ON_CREATION")).thenReturn("999999999");
        when(PreferenceCache.getSystemPreferenceValueAsString("POINT_CONVERSION_FACTOR")).thenReturn("1");
        when(PreferenceCache.getSystemPreferenceValueAsString("MULTIPLE_WALLET_APPLY")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXTTXNMANDT_DOMAINTP_DP")).thenReturn("DISTB_CHAN,CORPORATE,DEALER,COMP_SHOP");
        when(PreferenceCache.getSystemPreferenceValueAsString("CHNL_PLAIN_SMS_SEPT_LOGINID")).thenReturn("#");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_DEFAULT_PROFILE")).thenReturn("TRUE");
        when(PreferenceCache.getSystemPreferenceValueAsString("O2C_EMAIL_NOTIFICATION")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("SID_ISNUMERIC")).thenReturn("True");
        when(PreferenceCache.getSystemPreferenceValueAsString("SMS_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("AUTO_C2C_TRANSFER_AMT")).thenReturn("10000");
        when(PreferenceCache.getSystemPreferenceValueAsString("MIN_SID_LENGTH")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_SID_LENGTH")).thenReturn("15");
        when(PreferenceCache.getSystemPreferenceValueAsString("MVD_MAX_VOUCHER_EXTGW")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("RPTSUMM_MAX_DATEDIFF")).thenReturn("15");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_RECHARGE_MULTIPLE_ENTRY")).thenReturn("S");
        when(PreferenceCache.getSystemPreferenceValueAsString("LAST_X_TRANSFER_STATUS")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("PINPAS_EN_DE_CRYPTION_TYPE")).thenReturn("DES");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_CARD_GROUP_SLAB_COPY")).thenReturn("N");
        when(PreferenceCache.getSystemPreferenceValueAsString("SERVICE_FOR_LAST_X_TRANSFER")).thenReturn("C2S,O2C,C2C");
        when(PreferenceCache.getSystemPreferenceValueAsString("LAST_X_CUSTENQ_STATUS")).thenReturn("1");
        when(PreferenceCache.getSystemPreferenceValueAsString("LAST_TRF_MULTIPLE_SMS")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("DLRY_RCPT_TRK")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("CR_BK_ALW_EVD_AMB")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("SLF_EVD_ALWD")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("SR_WISE_MSG_EVD")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("BUDDY_PIN_REQ")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("FOC_SMS_NOTIFY")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("PRF_ASSOCIATE_AGENT")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_ENQ_BAL_HIDE")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_ALLD_BALANCE_C2S")).thenReturn("1000000000");
        when(PreferenceCache.getSystemPreferenceValueAsString("MO_TOTXN_AMT_ALLWDCO")).thenReturn("3000000000");
        when(PreferenceCache.getSystemPreferenceValueAsString("MO_SUCTRAN_ALLWDCOUN")).thenReturn("5000");
        when(PreferenceCache.getSystemPreferenceValueAsString("WE_TOTXN_AMT_ALLWDCO")).thenReturn("1000000000");
        when(PreferenceCache.getSystemPreferenceValueAsString("WE_SUCTRAN_ALLWDCOUN")).thenReturn("30000");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_ALLD_BALANCE_P2P")).thenReturn("50000000");
        when(PreferenceCache.getSystemPreferenceValueAsString("MO_SUCTRAN_ALLWD_P2P")).thenReturn("1000");
        when(PreferenceCache.getSystemPreferenceValueAsString("MO_REC_AMT_ALLWD_P2P")).thenReturn("5000000");
        when(PreferenceCache.getSystemPreferenceValueAsString("WE_SUCTRAN_ALLWD_P2P")).thenReturn("700");
        when(PreferenceCache.getSystemPreferenceValueAsString("WE_REC_AMT_ALLWD_P2P")).thenReturn("1250000");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_REF_NUM_REQ")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_REF_NUM_UNIQUE")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("PAYAMT_MRP_SAME")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("PRV_PASS_NOT_ALLOW")).thenReturn("3");
        when(PreferenceCache.getSystemPreferenceValueAsString("PRV_PIN_NOT_ALLOW")).thenReturn("3");
        when(PreferenceCache.getSystemPreferenceValueAsString("CP2P_PIN_VALIDAT_REQ")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("REC_MSG_SEND_ALLOW")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("OPT_USR_APRL_LEVEL")).thenReturn("1");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_FEE_APPL_VAL_EXT")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("PVT_SID_SERVICE_ALLOW")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("RP2PDWH_OPT_SPECIFIC_PROC_NAME")).thenReturn("RP2PDWHTEMPPRC");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2PDWH_OPT_SPECIFIC_PROC_NAME")).thenReturn("P2pdwhtempprc");
        when(PreferenceCache.getSystemPreferenceValueAsString("IATDWH_OPT_SPECIFIC_PROC_NAME")).thenReturn("Iatdwhtempprc");
        when(PreferenceCache.getSystemPreferenceValueAsString("PVT_RECH_MESSGATEWAY")).thenReturn("PVTRECH");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXTTXNMANDT_O2C")).thenReturn("4");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXT_CODE_MAND_O2C")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("MSISDN_USAGE_SUMM_FLAG")).thenReturn("TRUE");
        when(PreferenceCache.getSystemPreferenceValueAsString("O2C_ODR_APPROVAL_LVL")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("O2C_SMS_NOTIFY")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("AUTO_FOC_TRANSFER_AMT")).thenReturn("500");
        when(PreferenceCache.getSystemPreferenceValueAsString("AUTO_FOC_TRANSFER_AMOUNT")).thenReturn("500");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALLOW_ROAM_RECHARGE")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("SOS_ALLOWED_MAX_BALANCE")).thenReturn("500");
        when(PreferenceCache.getSystemPreferenceValueAsString("SOS_RECHARGE_AMOUNT")).thenReturn("500");
        when(PreferenceCache.getSystemPreferenceValueAsString("SOS_ST_DEDUCT_UPFRONT")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("SOS_DAYS_GAP_BTWN_TWO_TRAN")).thenReturn("0");
        when(PreferenceCache.getSystemPreferenceValueAsString("SOS_SETTLE_DAYS")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("GMB_INTERACTIVE_OPTION_ALLOWED")).thenReturn("Y");
        when(PreferenceCache.getSystemPreferenceValueAsString("USSD_MENU_CODE")).thenReturn("100");
        when(PreferenceCache.getSystemPreferenceValueAsString("USSD_TAGS_CELLID_SWITCHID_MANDATORY")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("SOS_ELIBILITY_ACC")).thenReturn("Core,LMB_ALLOWED");
        when(PreferenceCache.getSystemPreferenceValueAsString("SOS_MINIMUM_AON")).thenReturn("90");
        when(PreferenceCache.getSystemPreferenceValueAsString("SOS_MIN_VALIDITY_DAYS")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("SOS_ONLINE_ALLOW")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("ENQ_POSTBAL_ALLOW")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("ENQ_POSTBAL_IN")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("LMB_VALIDITY_DAYS_FORCESETTLE")).thenReturn("120");
        when(PreferenceCache.getSystemPreferenceValueAsString("LAST_SERVICE_TYPE_CHECK")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("MRP_BLOCK_TIME_ALLOWED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("LMB_BLK_UPL")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("LMB_FORCE_SETL_STAT_ALLOW")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("PLAIN_RES_PARSE_REQUIRED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("COUNTRY_CODE")).thenReturn("91");
        when(PreferenceCache.getSystemPreferenceValueAsString("REQ_CUSER_SUS_APP")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("RVERSE_TXN_APPRV_LVL")).thenReturn("0");
        when(PreferenceCache.getSystemPreferenceValueAsString("DB_ENTRY_NOT_ALLOWED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("ACT_FRST_RCH_APP")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_REC_GEN_FAIL_MSG_REQD_V_FOR_GIFTER")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_REC_GEN_FAIL_MSG_REQD_T_FOR_GIFTER")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_MAX_APPROVAL_LEVEL")).thenReturn("3");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_USER_KEY_REQD")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCHER_TRACKING_ALLOWED")).thenReturn("True");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCHER_EN_ON_TRACKING")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_AUTO_FOC_ALLOW_LIMIT")).thenReturn("20");
        when(PreferenceCache.getSystemPreferenceValueAsString("PROCESS_FEE_REV_ALLOWED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("LMB_DEBIT_REQ")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("USR_DEF_CONFIG_UPDATE_REQ")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("SELECTOR_INTERFACE_MAPPING")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("CP_SUSPENSION_DAYS_LIMIT")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("CAT_ALLOW_CREATION")).thenReturn("DIST,BCU,RET,AG");
        when(PreferenceCache.getSystemPreferenceValueAsString("MSISDN_MIGRATION_LIST")).thenReturn("012:0122");
        when(PreferenceCache.getSystemPreferenceValueAsString("PRVT_RC_MSISDN_PREFIX_LIST")).thenReturn("1111,2222");
        when(PreferenceCache.getSystemPreferenceValueAsString("LAST_X_RECHARGE_STATUS")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("SERVICE_FOR_LAST_X_RECHARGE")).thenReturn("C2S");
        when(PreferenceCache.getSystemPreferenceValueAsString("SMS_PIN_BYPASS_GATEWAY")).thenReturn("PLAIN");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_FNAME_LNAME_ALLOWED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXT_CODE_MAND_USER")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXT_VOMS_MSG_ENDEC_KEY")).thenReturn("ZO4UGUGCAFTLK9MOZO4UGUGCAFTLK9MO");
        when(PreferenceCache.getSystemPreferenceValueAsString("MCDL_MAX_LIST_COUNT")).thenReturn("60");
        when(PreferenceCache.getSystemPreferenceValueAsString("MCDL_DIFF_REQST_SEP")).thenReturn(",");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_MCDL_DEFAULT_AMOUNT")).thenReturn("0");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_MCDL_MAXADD_AMOUNT")).thenReturn("50000000");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_MCDL_AUTO_DELETION_DAYS")).thenReturn("90");
        when(PreferenceCache.getSystemPreferenceValueAsString("COS_REQUIRED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("TRF_RULE_USER_LEVEL_ALLOW")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("RSA_AUTHENTICATION_REQUIRED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("COUNT_TO_ASK_RSA_CODE")).thenReturn("3");
        when(PreferenceCache.getSystemPreferenceValueAsString("LOGIN_ID_CHECK_ALLOWED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("STAFF_USER_APRL_LEVEL")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("BATCH_USER_PROFILE_ASSIGN")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("BATCH_INTIATE_NOTIF_TYPE")).thenReturn("BOTH");
        when(PreferenceCache.getSystemPreferenceValueAsString("EMAIL_SERVICE_ALLOW")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("SRVC_PROD_MAPPING_ALLOWED")).thenReturn("RC,GRC,INTRRC,RCREV,PSTNRC,PPB");
        when(PreferenceCache.getSystemPreferenceValueAsString("SERVICE_PROVIDER_PROMO_ALLOW")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("CELL_GROUP_REQUIRED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("SRVC_PROD_INTFC_MAPPING_ALLOWED")).thenReturn("PVAS,VAS,RC,CDATA,DTH");
        when(PreferenceCache.getSystemPreferenceValueAsString("CELL_ID_SWITCH_ID_REQUIRED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("DEBIT_SENDER_SIMACT")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("SIMACT_DEFAULT_SELECTOR")).thenReturn("1");
        when(PreferenceCache.getSystemPreferenceValueAsString("IN_PROMO_REQUIRED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("LOGIN_PASSWORD_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("MULTI_AMOUNT_ENABLED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("SHOW_CAPTCHA")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("EMAIL_AUTH_REQ")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("PER_DAY_BAR_FOR_DEL_LIMIT")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("REQ_CUSER_BAR_APP")).thenReturn("1");
        when(PreferenceCache.getSystemPreferenceValueAsString("AUTO_O2C_MAX_APPROVAL_LEVEL")).thenReturn("3");
        when(PreferenceCache.getSystemPreferenceValueAsString("AUTO_O2C_AMOUNT")).thenReturn("1000");
        when(PreferenceCache.getSystemPreferenceValueAsString("SRVCS_FOR_PROD_MAPPING")).thenReturn("VAS,PVAS,RCREV");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_REQ_MSISDN_FOR_STAFF")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("MIN_HRDIF_CR_ST_LMS")).thenReturn("8");
        when(PreferenceCache.getSystemPreferenceValueAsString("MIN_HRDIF_ST_ED_LMS")).thenReturn("120");
        when(PreferenceCache.getSystemPreferenceValueAsString("LMS_APPL")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_SEPARATE_EXT_DB")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("LMS_VOL_COUNT_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("LMS_PROF_APR_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("LMS_MULT_FACTOR")).thenReturn("1.99");
        when(PreferenceCache.getSystemPreferenceValueAsString("LMS_VOL_CREDIT_LOYAL_PTS")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_LAST_TRANSFERS_DAYS")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("THLD_PRTP_PRCSS_TIME")).thenReturn("400");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXTSYS_USR_APRL_LEVEL_REQUIRED")).thenReturn("0");
        when(PreferenceCache.getSystemPreferenceValueAsString("LMS_STOCK_REQUIRED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("INVALID_PWD_COUNT_FOR_CAPTCHA")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("PAYMENTDETAILSMANDATE_O2C")).thenReturn("0");
        when(PreferenceCache.getSystemPreferenceValueAsString("USER_CREATION_MANDATORY_FIELDS")).thenReturn("email,externalCode");
        when(PreferenceCache.getSystemPreferenceValueAsString("USER_APPROVAL_LEVEL")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("O2C_APPRV_QTY_LEVEL")).thenReturn("1,2");
        when(PreferenceCache.getSystemPreferenceValueAsString("STAFF_USER_AUTH_TYPE")).thenReturn("NA");
        when(PreferenceCache.getSystemPreferenceValueAsString("CHANNEL_USER_ROLE_TYPE_DISPLAY")).thenReturn("ALL");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_PARTIAL_BATCH_ALLOWED")).thenReturn("TRUE");
        when(PreferenceCache.getSystemPreferenceValueAsString("MULTIPLE_VOUCHER_TABLE")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_INTERFACE_FLAG")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("USER_PRODUCT_MULTIPLE_WALLET")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("DEFAULT_WALLET")).thenReturn("MAIN");
        when(PreferenceCache.getSystemPreferenceValueAsString("MIN_VOUCHER_CODE_LENGTH")).thenReturn("6");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_MSISDN_ASSO_REQ")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("ADMINISTRBLY_USER_STATUS_CHANG")).thenReturn("DE:Y,DE:CH,DE:EX,DE:N,EX:Y,EX:CH,CH:Y,Y:EX,PA:DE");
        when(PreferenceCache.getSystemPreferenceValueAsString("REALTIME_AUTO_C2C_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_TRNSFR_INVNO_SRVCTYP")).thenReturn("CPB");
        when(PreferenceCache.getSystemPreferenceValueAsString("O2C_PNDNG_FOR_APPR_SEND_MAIL_DAYS_CNT")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("OTP_TIMEOUT_INSEC")).thenReturn("121");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_TRNSFR_AMTBLCK_SRVCTYP")).thenReturn("PIN,CE,PMD,RPB,CCN");
        when(PreferenceCache.getSystemPreferenceValueAsString("AUTO_O2C_APPROVAL_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("MIN_ACCOUNT_ID_LENGTH")).thenReturn("8");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_ACCOUNT_ID_LENGTH")).thenReturn("8");
        when(PreferenceCache.getSystemPreferenceValueAsString("SAP_INTEGARATION_FOR_USRINFO")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("AUTH_TYPE_REQ")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("COUNT_TO_ASK_OTP_CODE")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_REVERSAL_TXNID_SRVCTYP")).thenReturn("CCN");
        when(PreferenceCache.getSystemPreferenceValueAsString("CAPTCHA_LENGTH")).thenReturn("6");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_AUTOTOPUP_AMT")).thenReturn("10000");
        when(PreferenceCache.getSystemPreferenceValueAsString("PERCENTAGE_OF_PRE_REVERSAL")).thenReturn("70");
        when(PreferenceCache.getSystemPreferenceValueAsString("WALLET_FOR_ADNL_CMSN")).thenReturn("BONUS");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALLOW_ROAM_ADDCOMM")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALLOW_CCARD_ROAM_RECHARGE")).thenReturn("TRUE");
        when(PreferenceCache.getSystemPreferenceValueAsString("ROAM_INTERFACE_ID")).thenReturn("INTID00032");
        when(PreferenceCache.getSystemPreferenceValueAsString("LMS_PCT_POINTS_CALCULATION")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("SEC_QUES_COUNT")).thenReturn("3");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCHER_SERVICES_LIST")).thenReturn("RC,PRC,EVD,PPB");
        when(PreferenceCache.getSystemPreferenceValueAsString("TXN_RECEIVER_USER_STATUS_CHANG")).thenReturn("W:CH,EX:Y,CH:Y,PA:Y");
        when(PreferenceCache.getSystemPreferenceValueAsString("TXN_SENDER_USER_STATUS_CHANG")).thenReturn("EX:Y,CH:Y,PA:Y");
        when(PreferenceCache.getSystemPreferenceValueAsString("LIFECYCLE_STATUS_DAYS_LIST")).thenReturn("PA:Y:CH:EX:DE:N,1:1:1:1:1");
        when(PreferenceCache.getSystemPreferenceValueAsString("BLOCKING_ALLOWED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALERT_ALLOWED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("CARD_GROUP_ENQRES")).thenReturn("NAME:RANGE:REV_PR:DATE");
        when(PreferenceCache.getSystemPreferenceValueAsString("SEND_SMS_TO_PARENT")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("DP_NETWORKLEVEL_DAILYLIMIT")).thenReturn("100000");
        when(PreferenceCache.getSystemPreferenceValueAsString("REALTIME_AUTOALERT_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("SEND_SMS2PARENT_SERVICES_LIST")).thenReturn("RC,PRC,EVD,PPB");
        when(PreferenceCache.getSystemPreferenceValueAsString("DP_SYSTEMLEVEL_LIMIT")).thenReturn("30000");
        when(PreferenceCache.getSystemPreferenceValueAsString("RET_OPRTR_STOCK")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("ROAM_PENALTY_OWNER_PERCENTAGE")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("ROAM_RECHARGE_DAILY_THRESHOLD")).thenReturn("100");
        when(PreferenceCache.getSystemPreferenceValueAsString("ROAM_RECHARGE_PENALTY_PERCENTAGE")).thenReturn("40");
        when(PreferenceCache.getSystemPreferenceValueAsString("CENTRALIZED_USER_MANAGEMENT")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("INTF_NODE_VALIDATION")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("OPT_IN_OUT_ALLOW")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXTTXNMANDT_LPT")).thenReturn("4");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXT_CODE_MAND_LPT")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("LPT_ODR_APPROVAL_LVL")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("M_PRE_PERCENTAGE")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("M_SLAVE_PERCENTAGE")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("DECENTER_ROAM_LOCATION")).thenReturn("MH");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_ALLOWED_SCHTYPE")).thenReturn("WK,MO");
        when(PreferenceCache.getSystemPreferenceValueAsString("SRVC_ALLOW_ROAM_PENALTY")).thenReturn("RC,GRC");
        when(PreferenceCache.getSystemPreferenceValueAsString("CHOICE_RECHARGE_APPLICABLE")).thenReturn("False");
        when(PreferenceCache.getSystemPreferenceValueAsString("TWO_FA_REQ")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("TIME_FOR_REVERSAL")).thenReturn("48000");
        when(PreferenceCache.getSystemPreferenceValueAsString("TIME_FOR_REVERSAL_CCE")).thenReturn("48");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALLOWED_DAYS_FOR_REVERSAL")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALLOWED_SERVICES_FOR_REVERSAL")).thenReturn("RC,GRC");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALLOW_BULK_C2S_REVERSAL_MESSAGE")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALLOWED_GATEWAY_FOR_BULK_REVERSAL")).thenReturn("WEB");
        when(PreferenceCache.getSystemPreferenceValueAsString("DP_ONLINE_LIMIT")).thenReturn("3000");
        when(PreferenceCache.getSystemPreferenceValueAsString("DECIMAL_ALLOWED_IN_SERVICES")).thenReturn("RC,GRC,PPB,PRC,INTRRC,PSTNRC");
        when(PreferenceCache.getSystemPreferenceValueAsString("ADMIN_MESSAGE_REQD")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("O2C_BATCH_WITHDRAW_MESSAGE_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("OTP_ALLOWED_LENGTH")).thenReturn("8");
        when(PreferenceCache.getSystemPreferenceValueAsString("DEFAULT_CURRENCY")).thenReturn("COP");
        when(PreferenceCache.getSystemPreferenceValueAsString("FNF_ZB_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("LOW_BASED_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_SERVICES_TYPE_SERVICECLASS")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("MSISDN_LENGTH")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("LB_SYSTEMLEVEL_LIMIT")).thenReturn("100000");
        when(PreferenceCache.getSystemPreferenceValueAsString("SID_ENCRYPTION_ALLOWED")).thenReturn("TRUE");
        when(PreferenceCache.getSystemPreferenceValueAsString("DUPLICATE_CARDGROUP_CODE_ALLOW")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("OWNER_COMMISION_ALLOWED")).thenReturn("True");
        when(PreferenceCache.getSystemPreferenceValueAsString("AUTO_NWSTK_CRTN_ALWD")).thenReturn("True");
        when(PreferenceCache.getSystemPreferenceValueAsString("AUTO_NWSTK_CRTN_THRESHOLD")).thenReturn("SAL:ETOPUP:10000:500000,INC:ETOPUP:200000:5000000,FOC:ETOPUP:60000:700000,SAL:POSTETOPUP:200:6000000,INC:POSTETOPUP:2000:5000000,FOC:POSTETOPUP:60000:700000,SAL:VOUCHTRACK:200:6000000,INC:VOUCHTRACK:2000:5000000,FOC:VOUCHTRACK:60000:700000");
        when(PreferenceCache.getSystemPreferenceValueAsString("TOKEN_EXPIRY_IN_MINTS")).thenReturn("60");
        when(PreferenceCache.getSystemPreferenceValueAsString("O2C_DIRECT_TRANSFER")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("SEQUENCE_ID_RANGE")).thenReturn("12");
        when(PreferenceCache.getSystemPreferenceValueAsString("SEQUENCE_ID_ENABLE")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("HASHING_ID_RANGE")).thenReturn("12");
        when(PreferenceCache.getSystemPreferenceValueAsString("HASHING_ENABLE")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("CHANNEL_SOS_ENABLE")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("AUTO_C2C_SOS_CAT_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("CHANNEL_AUTOC2C_ENABLE")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("SOS_SETTLEMENT_TYPE")).thenReturn("MANUAL");
        when(PreferenceCache.getSystemPreferenceValueAsString("CHANNEL_SOS_ALLOWED_WALLET")).thenReturn("PARENT");
        when(PreferenceCache.getSystemPreferenceValueAsString("DOWNLOAD_CSV_REPORT_REQUIRED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALLOW_TRANSACTION_IF_SOS_SETTLEMENT_FAIL")).thenReturn("FOC, DP");
        when(PreferenceCache.getSystemPreferenceValueAsString("LR_ENABLED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("TARGET_BASED_ADDNL_COMMISSION")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("OFFLINE_SETTLE_EXTUSR")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("TARGET_BASED_ADDNL_COMMISSION_SLABS")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("TARGET_BASED_BASE_COMMISSION_SLABS")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("TARGET_BASED_BASE_COMMISSION")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALLOWD_USR_TYP_CREATION")).thenReturn("N");
        when(PreferenceCache.getSystemPreferenceValueAsString("SYSTEM_ROLE_ALLOWED")).thenReturn("N");
        when(PreferenceCache.getSystemPreferenceValueAsString("CHNL_USR_LAST_ACTIVE_TXN")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("ADD_COMM_SEPARATE_MSG")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("CHANNEL_TRANSFERS_INFO_REQUIRED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("REALTIME_OTF_MESSAGES")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("POST_SERVICE_CLASS")).thenReturn("1000");
        when(PreferenceCache.getSystemPreferenceValueAsString("SUBS_BLK_AFT_X_CONS_FAIL")).thenReturn("4");
        when(PreferenceCache.getSystemPreferenceValueAsString("SUBS_UNBLK_AFT_X_TIME")).thenReturn("90");
        when(PreferenceCache.getSystemPreferenceValueAsString("DECRYPT_KEY_VISIBLE")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("THIRD_PARTY_VISIBLE")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_PROFILE_ACTIVATION_REQ")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_AUTO_VOUCHER_CRTN_ALWD")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("STK_MASTER_KEY")).thenReturn("6B4FC9246FB075B619626600EAA870F9");
        when(PreferenceCache.getSystemPreferenceValueAsString("BURN_RATE_THRESHOLD_PCT")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCHER_BURN_RATE_SMS_ALERT")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCHER_BURN_RATE_EMAIL_ALERT")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("PIN_VALIDATATION_IN_USSD")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("INET_REPORT_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_VALIDATION_BY_IN")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_DAMG_PIN_LNTH_ALLOW")).thenReturn("3");
        when(PreferenceCache.getSystemPreferenceValueAsString("VPIN_INVALID_COUNT")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_PROF_TALKTIME_MANDATORY")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_PROF_VALIDITY_MANDATORY")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_PROFILE_DEF_MINMAXQTY")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_PROFILE_MIN_REORDERQTY")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_PROFILE_MAX_REORDERQTY")).thenReturn("10000");
        when(PreferenceCache.getSystemPreferenceValueAsString("NW_NATIONAL_PREFIX")).thenReturn("99");
        when(PreferenceCache.getSystemPreferenceValueAsString("NW_CODE_NW_PREFIX_MAPPING")).thenReturn("NG=11,PB=12");
        when(PreferenceCache.getSystemPreferenceValueAsString("OTH_COM_CHNL")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("SERVICES_ALLOWED_SHOW_CARDGROUPLIST")).thenReturn("ABC");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_REQ_VOUCHER_QTY")).thenReturn("5000");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_PROMO_TRF_APP")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_PRO_TRF_ST_LVL_CODE")).thenReturn("1");
        when(PreferenceCache.getSystemPreferenceValueAsString("P2P_PRE_SERVCLASS_AS_POST")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("LAST_C2C_ENQ_MSG_REQ")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("USSD_REC_MSG_SEND_ALLOW")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_SEQ_ALWD")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_SEQID_FOR_GWC")).thenReturn("");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2S_SEQID_APPL_SER")).thenReturn("");
        when(PreferenceCache.getSystemPreferenceValueAsString("ADDITIONAL_IN_FIELDS_ALLOWED")).thenReturn("");
        when(PreferenceCache.getSystemPreferenceValueAsString("PAYMENT_MODE_ALWD")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("EMAIL_DEFAULT_LOCALE")).thenReturn("en_US");
        when(PreferenceCache.getSystemPreferenceValueAsString("PG_INTEFRATION_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("USER_VOUCHERTYPE_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("PIN_REQUIRED_P2P")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAPP_SESSION_EXPIRY_SEC")).thenReturn("300");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAPP_PRODUCT_GROUPING_REQ_SRV")).thenReturn("VAS,PVAS");
        when(PreferenceCache.getSystemPreferenceValueAsString("O2CAMB_MINUTES_DELAY")).thenReturn("-5");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_ALLOW_CONTENT_TYPE")).thenReturn("pdf, jpg, jpeg, png");
        when(PreferenceCache.getSystemPreferenceValueAsString("CHNLUSR_VOUCHER_CATGRY_ALLWD")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("TRANSACTION_TYPE")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCHER_THIRDPARTY_STATUS")).thenReturn("WH");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMSPIN_EN_DE_CRYPTION_TYPE")).thenReturn("DES");
        when(PreferenceCache.getSystemPreferenceValueAsString("IPV6_ENABLED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("NATIONAL_VOUCHER_ENABLE")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("NATIONAL_VOUCHER_NETWORK_CODE")).thenReturn("NG");
        when(PreferenceCache.getSystemPreferenceValueAsString("DOWNLD_BATCH_BY_BATCHID")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("PAYMENT_VERIFICATION_ALLOWED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN")).thenReturn("0");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN")).thenReturn("3");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN")).thenReturn("3");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_VOUCHER_EXPIRY_EXTN_LIMIT")).thenReturn("20");
        when(PreferenceCache.getSystemPreferenceValueAsString("ONLINE_BATCH_EXP_DATE_LIMIT")).thenReturn("15");
        when(PreferenceCache.getSystemPreferenceValueAsString("USER_VOUCHERSEGMENT_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("DATE_FORMAT_CAL_JAVA")).thenReturn("dd/MM/yy");
        when(PreferenceCache.getSystemPreferenceValueAsString("DATE_TIME_FORMAT")).thenReturn("dd/MM/yyyy HH24:mi:ss");
        when(PreferenceCache.getSystemPreferenceValueAsString("LOCALE_CALENDAR")).thenReturn("en-US");
        when(PreferenceCache.getSystemPreferenceValueAsString("LOCALE_ENGLISH")).thenReturn("@calendar=persian");
        when(PreferenceCache.getSystemPreferenceValueAsString("TIMEZONE_ID")).thenReturn("Asia/Kolkata");
        when(PreferenceCache.getSystemPreferenceValueAsString("CALENDAR_TYPE")).thenReturn("gregorian");
        when(PreferenceCache.getSystemPreferenceValueAsString("CALENDER_DATE_FORMAT")).thenReturn("dd/mm/yy");
        when(PreferenceCache.getSystemPreferenceValueAsString("CALENDAR_SYSTEM")).thenReturn("gregorian");
        when(PreferenceCache.getSystemPreferenceValueAsString("FORMAT_MONTH_YEAR")).thenReturn("yyyy/mm");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXTERNAL_CALENDAR_TYPE")).thenReturn("persian");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_CAL_ICON_VISIBLE")).thenReturn("Y");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_MON_DATE_ON_UI")).thenReturn("N");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_PIN_BLK_EXP_DRN")).thenReturn("100000");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_NATIONAL_LOCAL_PREFIX_ENABLE")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("LDAP_UTIL_C")).thenReturn("com.btsl.pretups.util.LDAPUtil");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_SERVICES")).thenReturn("VCN");
        when(PreferenceCache.getSystemPreferenceValueAsString("ONLINE_VOUCHER_GEN_LIMIT_SYSTEM")).thenReturn("100000");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_P_STATUS_CHANGE")).thenReturn("EN,DA,ST,OH,WH,S");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCHER_PROFLE_IS_OPTIONAL")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_E_STATUS_CHANGE_MAP")).thenReturn("OH:EN,OH:ST,OH:DA,OH:S,PA:EN,PA:S,S:EN,S:ST,S:OH,EN:OH,EN:ST,EN:DA,EN:S,GE:DA,GE:EN,GE:ST");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_D_STATUS_CHANGE_MAP")).thenReturn("OH:EN,OH:ST,OH:DA,OH:S,PA:EN,PA:S,S:EN,S:ST,S:OH,EN:OH,EN:ST,EN:DA,EN:S,GE:DA,GE:ST");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_D_CHANGE_STATUS")).thenReturn("EN,DA,ST,OH,S");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_E_CHANGE_STATUS")).thenReturn("EN,DA,ST,OH,S");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_P_CHANGE_STATUS")).thenReturn("EN,DA,ST,OH,WH,S");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_D_STATUS_CHANGE")).thenReturn("EN,DA,ST,OH,S");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_E_STATUS_CHANGE")).thenReturn("EN,DA,ST,OH,S");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_P_STATUS_CHANGE_MAP")).thenReturn("OH:EN,OH:ST,OH:DA,OH:S,PA:EN,PA:S,S:EN,S:ST,S:OH,EN:OH,EN:ST,EN:DA,EN:S,PE:WH,PE:ST,WH:S,GE:DA,GE:EN,GE:ST");
        when(PreferenceCache.getSystemPreferenceValueAsString("ONLINE_CHANGE_STATUS_SYSTEM_LMT")).thenReturn("100000");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2C_APPRV_QTY_LEVEL")).thenReturn("1");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_APPROVAL_LEVEL_C2C_INITIATE")).thenReturn("1");
        when(PreferenceCache.getSystemPreferenceValueAsString("DVD_ORDER_BY_PARAMETERS")).thenReturn("EXPIRY_DATE ,CREATED_ON, SERIAL_NO");
        when(PreferenceCache.getSystemPreferenceValueAsString("REC_MSG_SEND_ALLOW_C2C")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2C_ALLOWED_VOUCHER_LIST")).thenReturn("EN,OH,PA,ST,GE");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER")).thenReturn("");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2C_ALLOW_CONTENT_TYPE")).thenReturn("pdf,png,jpg");
        when(PreferenceCache.getSystemPreferenceValueAsString("ONLINE_VOUCHER_GEN_LIMIT")).thenReturn("100000");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCHER_GEN_EMAIL_NOTIFICATION")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCHER_GEN_SMS_NOTIFICATION")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("SUBSCRIBER_VOUCHER_PIN_REQUIRED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("ONLINE_DVD_LIMIT")).thenReturn("100000");
        when(PreferenceCache.getSystemPreferenceValueAsString("ONLINE_VOUCHER_GEN_LIMIT_NW")).thenReturn("15000");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_D_LIFECYCLE")).thenReturn("GE:EN:CU");
        when(PreferenceCache.getSystemPreferenceValueAsString("SCREEN_WISE_ALLOWED_VOUCHER_TYPE")).thenReturn("ACTIVE_PROF:E,ET;VOUC_DOWN:P,PT;O2C:D,DT,P,PT");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_P_LIFECYCLE")).thenReturn("GE:PE:WH:EN:CU");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_E_LIFECYCLE")).thenReturn("GE:EN:CU");
        when(PreferenceCache.getSystemPreferenceValueAsString("DVD_BATCH_FILEEXT")).thenReturn("csv");
        when(PreferenceCache.getSystemPreferenceValueAsString("ONLINE_CHANGE_STATUS_NETWORK_LMT")).thenReturn("10000");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_APPROVAL_LEVEL_C2C_TRANSFER")).thenReturn("0");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2C_EMAIL_NOTIFICATION")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCHER_PROFILE_OTHER_INFO")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2C_SMS_NOTIFICATION")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE")).thenReturn("");
        when(PreferenceCache.getSystemPreferenceValueAsString("PAYMENTDETAILSMANDATE_C2C")).thenReturn("3");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_BUN_PRE_ID_NULL_ALLOW")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_VOU_BUN_NAME_LEN_ZERO_ALLOW")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("RECENT_C2C_TXN")).thenReturn("4");
        when(PreferenceCache.getSystemPreferenceValueAsString("TWO_FA_REQ_FOR_PIN")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("OTP_RESEND_TIMES")).thenReturn("3");
        when(PreferenceCache.getSystemPreferenceValueAsString("OTP_RESEND_DURATION")).thenReturn("180");
        when(PreferenceCache.getSystemPreferenceValueAsString("OTP_VALIDITY_PERIOD")).thenReturn("5000");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2CVCRPT_DATEDIFF")).thenReturn("40");
        when(PreferenceCache.getSystemPreferenceValueAsString("O2CVCRPT_DATEDIFF")).thenReturn("15");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_BLANK_VOUCHER_REQ")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("CARD_GROUP_ALLOWED_CATEGORIES")).thenReturn("NWADM,SUNADM");
        when(PreferenceCache.getSystemPreferenceValueAsString("TRANSFER_RULE_ALLOWED_CATEGORIES")).thenReturn("NWADM,SUNADM");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCH_GEN_BATCH_SIZE")).thenReturn("200");
        when(PreferenceCache.getSystemPreferenceValueAsString("AUTOCOMPLETE_USER_DETAILS_COUNT")).thenReturn("50");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCH_GEN_RETRY_COUNT")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_SECURITY_COMMON_VALIDATION_REQUIRED")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("RESOURCE_UTIL_ONLINE_VOMS_GEN_THRESHOLD_LIMIT")).thenReturn("100");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALPHANUM_SPCL_REGEX")).thenReturn("[a-zA-Z\\d]+?");
        when(PreferenceCache.getSystemPreferenceValueAsString("AVAILABLE_SOURCE_TYPE")).thenReturn("XML,PLAIN,JSON,WEB");
        when(PreferenceCache.getSystemPreferenceValueAsString("SUCC_BLOCK_TIME_O2C")).thenReturn("0");
        when(PreferenceCache.getSystemPreferenceValueAsString("SUCC_BLOCK_TIME_C2C")).thenReturn("0");
        when(PreferenceCache.getSystemPreferenceValueAsString("MRP_BLOCK_TIME_ALLOWED_CHNL")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("MRP_BLOCK_TIMEOUT_SERVICES_GATEWAY_CHNL")).thenReturn("O2C,C2C");
        when(PreferenceCache.getSystemPreferenceValueAsString("OTP_ON_SMS")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_INVALID_OTP")).thenReturn("3");
        when(PreferenceCache.getSystemPreferenceValueAsString("BLOCK_TIME_INVALID_OTP")).thenReturn("60");
        when(PreferenceCache.getSystemPreferenceValueAsString("MIN_LENGTH_TO_AUTOCOMPLETE")).thenReturn("3");
        when(PreferenceCache.getSystemPreferenceValueAsString("OAUTH_TOKEN_TIME_TO_LIVE")).thenReturn("60");
        when(PreferenceCache.getSystemPreferenceValueAsString("PAYMENTDETAILSMANDATEVOUCHER_C2C")).thenReturn("0");
        when(PreferenceCache.getSystemPreferenceValueAsString("REPORT_MAX_DATEDIFF_ADMIN_CONS")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("REPORT_MAX_DATEDIFF_USER_CONS")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("REPORT_MAX_DATEDIFF_USER_AVAIL")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("REPORT_MAX_DATEDIFF_ADMIN_AVAIL")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("REPORT_MAX_DATEDIFF_ADMIN_NLEVEL")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("USER_ALLOWED_VINFO")).thenReturn("SUBCU,BCU,DIST,SE,SUADM,SUNADM,NWADM,SSADM,AG,RET");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_LOGINS_LOCATION")).thenReturn("10000");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_LOGINS_TYPE")).thenReturn("5000");
        when(PreferenceCache.getSystemPreferenceValueAsString("MIN_LAST_DAYS_CG")).thenReturn("1");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_LAST_DAYS_CG")).thenReturn("365");
        when(PreferenceCache.getSystemPreferenceValueAsString("BYPASS_EVD_KANEL_MES_STAT")).thenReturn("FALSE");
        when(PreferenceCache.getSystemPreferenceValueAsString("LANGS_SUPT_ENCODING")).thenReturn("ar,ku,ku1,ru,fa");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_ONE_TIME_SID")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("TOKEN_EXPIRE_TIME")).thenReturn("6000");
        when(PreferenceCache.getSystemPreferenceValueAsString("REFRESH_TOKEN_EXPIRE_TIME")).thenReturn("6000");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_UI_DISPLAY_DATE_TIME")).thenReturn("dd/MM/yy hh:mm:ss aa");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCHER_UPLOAD_FILE_FORMATS")).thenReturn("xls,xlsx,csv");
        when(PreferenceCache.getSystemPreferenceValueAsString("TOP_N_PRODUCT_VALUE")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("REPORT_DATE_DURATION")).thenReturn("30");
        when(PreferenceCache.getSystemPreferenceValueAsString("AvgSaleNoOfDays")).thenReturn("15");
        when(PreferenceCache.getSystemPreferenceValueAsString("ElectronicStockDaysCalculatedOn")).thenReturn("EN");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCHER_DTL_LOW_BALANCE")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("DIGITAL_RECHARGE_VOUCHER_TYPE")).thenReturn("digital,digital1,test_digit");
        when(PreferenceCache.getSystemPreferenceValueAsString("FORMAT_DATE_MONTH")).thenReturn("dd/MM");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_CELLID_REQUIRED_FROM_IN")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_POSITIVE_COMM_DEBIT_FROM_SENDER_REQ")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_RECORDS_SIZE_VAL")).thenReturn("999999999999");
        when(PreferenceCache.getSystemPreferenceValueAsString("LOG_OUT_TIME")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("FORCE_LOGOUT")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("FILE_IS_NOT_EXIST")).thenReturn("{0} does not exist.");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_VOUCHER_TEMPLATE_TYPE")).thenReturn("XLSX");
        when(PreferenceCache.getSystemPreferenceValueAsString("BURN_RATE_VOUCHER_TYPES")).thenReturn("P,E");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_LOGINS_TYPE_NG_DIST")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_LOGINS_TYPE_NG_SSADM")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_SERIAL_NO_MAX_LENGTH")).thenReturn("16");
        when(PreferenceCache.getSystemPreferenceValueAsString("RIGHT_CLICK_ENABLE")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("REPORT_ONLINE_OR_OFFLINE")).thenReturn("OFFLINE");
        when(PreferenceCache.getSystemPreferenceValueAsString("UI_COPY_CONTENT_ENABLE")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALLOW_SAME_REPORT_EXEC")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("TOT_RPT_EXEC_PERUSER")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("CGTAX34APP")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("REPORT_OFFLINE")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("SUBCRBR_PRFX_ROUTNG_ALWD")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("CHARSET_ENCODING")).thenReturn("UTF-16");
        when(PreferenceCache.getSystemPreferenceValueAsString("DEF_CHNL_TRANSFER_ALLOWED")).thenReturn("C2CVOMSTRFINI,TRFINI");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALIAS_TO_BE_ENCRYPTED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("LOAD_BAL_IP_ALLOWED")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("FILE_UPLOAD_MAX_SIZE")).thenReturn("9999999");
        when(PreferenceCache.getSystemPreferenceValueAsString("SYSTEM_IDLE_TIME")).thenReturn("900");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_FILE_SIZE_FOR_VMSSIGNED_DOC")).thenReturn("102400000");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_MAX_BATCHES_ALLOWED")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("UPLOAD_FILE_APPRV_LEVEL")).thenReturn("3");
        when(PreferenceCache.getSystemPreferenceValueAsString("OPERATOR_UTIL_VMS")).thenReturn("OperatorUtil");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOU_UPLOAD_ERROR_DISPLAY")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_CHECK_SUM_ENABLE")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_GEN_BATCH_COUNT")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("VMS_APPROVAL_REQ")).thenReturn("N");
        when(PreferenceCache.getSystemPreferenceValueAsString("DEFAULT_PRODUCT_CODE")).thenReturn("ETOPUP");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_BULK_FILE_SIZE_BYTES")).thenReturn("10200000");
        when(PreferenceCache.getSystemPreferenceValueAsString("DW_ALLOWED_GATEWAYS")).thenReturn("EXTGW,DWEXTGW");
        when(PreferenceCache.getSystemPreferenceValueAsString("SAP_ALLOWED")).thenReturn("False");
        when(PreferenceCache.getSystemPreferenceValueAsString("AUTO_SEARCH")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("C2C_BATCH_FILEEXT")).thenReturn("xls");
        when(PreferenceCache.getSystemPreferenceValueAsString("ERROR_FILE_C2C")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_ORDER_SLAB_LENGTH")).thenReturn("4");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOMS_MIN_ALT_VALUE")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("COM_PAY_OUT")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("USER_ALLOW_CONTENT_TYPE")).thenReturn("xlsx");
        when(PreferenceCache.getSystemPreferenceValueAsString("OFFLINERPT_DOWNLD_PATH")).thenReturn("/data1/pretupsapp/offlineDownloadFile/test2downloads/");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_VOU_DEN_PROFILE_ZERO_ALLOW")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("NET_PREFIX_TO_VALIDATED_FOR_BAR_UNBAR")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("CHADM_CROSS_ALLOW")).thenReturn("FALSE");
        when(PreferenceCache.getSystemPreferenceValueAsString("USR_BTCH_SUS_DEL_APRVL")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("ADMINISTRBLY_USER_STATUS_CHANG_NEW")).thenReturn("DE:Y,DE:CH,DE:EX,DE:N,EX:Y,EX:CH,CH:Y,Y:EX,PA:DE,Y:S,S:RE,Y:BR");
        when(PreferenceCache.getSystemPreferenceValueAsString("ADD_INFO_REQUIRED_FOR_VOUCHER")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("IS_TRF_RULE_USER_LEVEL_ALLOW")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("USERWISE_LOAN_ENABLE")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("EXTREFNUM_MANDATORY_GATEWAYS")).thenReturn("ext");
        when(PreferenceCache.getSystemPreferenceValueAsString("TEMP_PIN_EXPIRY_DURATION")).thenReturn("24");
        when(PreferenceCache.getSystemPreferenceValueAsString("IMEI_OPTIONAL")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("CSRF_ENABLE")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("DIRECT_VOUCHER_ENABLE")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("OTP_VALIDITY_TIME")).thenReturn("500");
        when(PreferenceCache.getSystemPreferenceValueAsString("USER_EXTERNAL_CODE_DOMAINWISE")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("TARGET_COMMISSION_SLABS")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValueAsString("COMMISSION_SLABS")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("COMMISSION_BASE_SLAB")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("COMMISSION_ADD_SLAB")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("CHANNEL_USER_STATUS_CHANGE_BATCH_FILEEXT")).thenReturn("xls");
        when(PreferenceCache.getSystemPreferenceValueAsString("OTP_REQUIRED")).thenReturn("ONETIME");
        when(PreferenceCache.getSystemPreferenceValueAsString("CHANGE_NETWORK")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("SNDR_NETWORK_IDENTIFY_ON_IMSI_BASIS")).thenReturn("FALSE");
        when(PreferenceCache.getSystemPreferenceValueAsString("MAX_CHARS_FOR_SEARCH")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALLOW_TRANSACTION_IF_LOAN_SETTLEMENT_FAIL")).thenReturn("RETURN, RCREV");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCHER_SHOW")).thenReturn("FALSE");
        when(PreferenceCache.getSystemPreferenceValueAsString("ALLOW_GATEWAYCODE_FOR_LOAN_SETTLEMENT")).thenReturn("USSD,SMSC");
        when(PreferenceCache.getSystemPreferenceValueAsString("RESTRICT_TRANSACTION_FOR_LOAN_SETTLEMENT")).thenReturn("FOC");
        when(PreferenceCache.getSystemPreferenceValueAsString("LOAN_PROFILE_SLAB_LENGTH")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("BLOCK_GATEWAYCODE_FOR_LOAN_SETTLEMENT")).thenReturn("WEB,GSTREXTGW,EXTGW");
        when(PreferenceCache.getSystemPreferenceValueAsString("CAT_USERWISE_LOAN_ENABLE")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("CATEGORIES_LIFECYCLECHANGE")).thenReturn("RETA,SA");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCHER")).thenReturn("false");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCHER_EXP_EMAIL_NOTIFICATION")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCHREXPIRYUP_ONLINECOUNT")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("VOUCHER_EXP_SMS_NOTIFICATION")).thenReturn("true");
        when(PreferenceCache.getSystemPreferenceValueAsString("ADD_C2S_MAX")).thenReturn("10");
        when(PreferenceCache.getSystemPreferenceValueAsString("RANGE_BASED_ADDNL_COMMISSION_SLABS")).thenReturn("2");
        when(PreferenceCache.getSystemPreferenceValueAsString("RANGE_BASED_BASE_COMMISSION_SLABS")).thenReturn("2");
    }


    public static void initialize2()
    {
        when(PreferenceCache.getSystemPreferenceValue("C2S_DEFAULT_SMSPIN")).thenReturn("0000");
        when(PreferenceCache.getSystemPreferenceValue("CREPT_MAX_DATEDIFF")).thenReturn(60);
        when(PreferenceCache.getSystemPreferenceValue("MAX_DATEDIFF")).thenReturn(30);
        when(PreferenceCache.getSystemPreferenceValue("REPORT_MAX_DATEDIFF")).thenReturn(20);
        when(PreferenceCache.getSystemPreferenceValue("C2S_DEFAULT_PASSWORD")).thenReturn("0000");
        when(PreferenceCache.getSystemPreferenceValue("P2P_DEFAULT_SMSPIN")).thenReturn("0000");
        when(PreferenceCache.getSystemPreferenceValue("SYSTEM_DATE_FORMAT")).thenReturn("dd/MM/yy");
        when(PreferenceCache.getSystemPreferenceValue("PIN_PWD_ALERT_DYS")).thenReturn(365);
        when(PreferenceCache.getSystemPreferenceValue("PWD_CHANGE_NOT_REQ")).thenReturn("CCE");
        when(PreferenceCache.getSystemPreferenceValue("EXTERNAL_TXN_UNIQUE")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("RC_NBK_AL_DAYS_DIF")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("STAFF_USER_COUNT")).thenReturn(25);
        when(PreferenceCache.getSystemPreferenceValue("DCT_VOUCHER_EN")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("PRO_TRF_ST_LVL_CODE")).thenReturn(1);
        when(PreferenceCache.getSystemPreferenceValue("C2S_PROMO_TRF_APP")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("MNP_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("PORT_USR_SUSPEND_REQ")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_SNO_MIN_LENGTH")).thenReturn(9);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_SNO_MAX_LENGTH")).thenReturn(16);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_PIN_MIN_LENGTH")).thenReturn(9);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_PIN_MAX_LENGTH")).thenReturn(14);
        when(PreferenceCache.getSystemPreferenceValue("LOW_BAL_MSGGATEWAY")).thenReturn("LOWBALGW");
        when(PreferenceCache.getSystemPreferenceValue("RC_NBK_DIF_RQ_TO_IN")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("RC_AL_AC_STATUS_NBK")).thenReturn("DEACT,SUS");
        when(PreferenceCache.getSystemPreferenceValue("RC_NBK_AMT_DEDCTED")).thenReturn(2000);
        when(PreferenceCache.getSystemPreferenceValue("PRC_AL_AC_STATUS_NBK")).thenReturn("Active");
        when(PreferenceCache.getSystemPreferenceValue("PRC_NBK_DIF_RQ_TO_IN")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("PRC_NBK_AL_DAYS_DIF")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("PRC_NBK_AMT_DEDCTED")).thenReturn(2000);
        when(PreferenceCache.getSystemPreferenceValue("AUTO_PAYMENT_METHOD")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("PTUPS_MOBQUTY_MERGD")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("ICCID_CHECKSTRING")).thenReturn("9862");
        when(PreferenceCache.getSystemPreferenceValue("P2P_ALLOW_SELF_TOPUP")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("PIN_SEND_TO")).thenReturn("C");
        when(PreferenceCache.getSystemPreferenceValue("GRPT_CHRG_ALLOWED")).thenReturn("SMSC,USSD");
        when(PreferenceCache.getSystemPreferenceValue("GRPT_CTRL_ALLOWED")).thenReturn("SMSC,USSD");
        when(PreferenceCache.getSystemPreferenceValue("GRPT_CONTROL_LEVEL")).thenReturn("M");
        when(PreferenceCache.getSystemPreferenceValue("C2S_DEF_SEL_BILLPAY")).thenReturn("C");
        when(PreferenceCache.getSystemPreferenceValue("EXT_CODE_MAND_FOC")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("SECOND_LANG_CHARSET")).thenReturn("ISO-8859-15");
        when(PreferenceCache.getSystemPreferenceValue("C2C_RET_PARENT_ONLY")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("MAX_BUDDIES_ALLOWED")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("DECIML_ALOW_SERVICES")).thenReturn("RC,GRC,PPB,EVD,INTRRC,PSTNRC");
        when(PreferenceCache.getSystemPreferenceValue("EXTERNAL_DATE_FORMAT")).thenReturn("dd/MM/yy");
        when(PreferenceCache.getSystemPreferenceValue("C2S_ALLOW_SELF_UTILI")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("C2S_ALOW_SLF_UTLTBIL")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("C2S_S_CRBKAM_UTLTBIL")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("NOTIFY_SRVCCLS_REC")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("MIN_ID_NUM_LNTH")).thenReturn(8);
        when(PreferenceCache.getSystemPreferenceValue("MAX_ID_NUM_LNTH")).thenReturn(12);
        when(PreferenceCache.getSystemPreferenceValue("XML_MAX_RCD_SUM_RESP")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("XML_DATE_RANGE")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("XML_DFT_DATE_RANGE")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("USE_HOME_STOCK")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("OTH_ID_PREFIX_LIST")).thenReturn("");
        when(PreferenceCache.getSystemPreferenceValue("ALPHANUM_ID_NUM_ALWD")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("ID_NUM_VAL_TYPE")).thenReturn("M");
        when(PreferenceCache.getSystemPreferenceValue("USE_DISPLAY_AMT")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("MAX_DENOMINATION_VAL")).thenReturn(100000);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_MAXERRORCOUNTEN")).thenReturn(1000000);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_MAXERRORCOUNTOT")).thenReturn(1000000);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_SERIALNO_LENGTH")).thenReturn(16);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_UPEXPHOURS")).thenReturn(4);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_OFFPEAKHRS")).thenReturn("0-24");
        when(PreferenceCache.getSystemPreferenceValue("VOMS_MAX_VOUCHER_EN")).thenReturn(1000000);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_MAX_VOUCHER_OT")).thenReturn(1000000);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_MAXBATCHDY")).thenReturn(3);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_DATE_FORMAT")).thenReturn("dd/MM/yy");
        when(PreferenceCache.getSystemPreferenceValue("PEERTRFMINLMT")).thenReturn(500);
        when(PreferenceCache.getSystemPreferenceValue("PEERTRFMAXLMT")).thenReturn(1500000000);
        when(PreferenceCache.getSystemPreferenceValue("MAX_PER_TRANSFER")).thenReturn(90);
        when(PreferenceCache.getSystemPreferenceValue("DAY_SDR_MX_TRANS_AMT")).thenReturn(100000);
        when(PreferenceCache.getSystemPreferenceValue("DAY_SDR_MX_TRANS_NUM")).thenReturn(3);
        when(PreferenceCache.getSystemPreferenceValue("MIN_RES_BALTYPE")).thenReturn("AMT");
        when(PreferenceCache.getSystemPreferenceValue("MIN_RESIDUAL_BAL")).thenReturn(10000);
        when(PreferenceCache.getSystemPreferenceValue("C2S_MAX_PIN_BLK_CONT")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("MIN_VALIDITY_DAYS")).thenReturn(400);
        when(PreferenceCache.getSystemPreferenceValue("SUCC_BLOCK_TIME")).thenReturn("0");
        when(PreferenceCache.getSystemPreferenceValue("DA_CONFAIL_COUNT")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("DA_SUCTRAN_ALLWDCOUN")).thenReturn(300);
        when(PreferenceCache.getSystemPreferenceValue("DA_FAIL_TXN_ALLWDCOU")).thenReturn(40);
        when(PreferenceCache.getSystemPreferenceValue("DA_TOTXN_AMT_ALLWDCO")).thenReturn(300000000);
        when(PreferenceCache.getSystemPreferenceValue("TAX2_ON_TAX1")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("MIN_MSISDN_LENGTH")).thenReturn(6);
        when(PreferenceCache.getSystemPreferenceValue("SCC_BLCK_TIME_P2P")).thenReturn(60);
        when(PreferenceCache.getSystemPreferenceValue("DA_REC_AMT_ALLWD_P2P")).thenReturn(1500000);
        when(PreferenceCache.getSystemPreferenceValue("DA_SUCTRAN_ALLWD_P2P")).thenReturn(545454);
        when(PreferenceCache.getSystemPreferenceValue("DEF_FRCXML_SEL_P2P")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("DEF_FRCXML_SEL_C2S")).thenReturn("");
        when(PreferenceCache.getSystemPreferenceValue("GROUP_ROLE_ALLOWED")).thenReturn("Y");
        when(PreferenceCache.getSystemPreferenceValue("CHNL_PLAIN_SMS_SEPT")).thenReturn(" ");
        when(PreferenceCache.getSystemPreferenceValue("FOC_ODR_APPROVAL_LVL")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("P2P_ENQ_BAL_HIDE")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("SMS_TO_LOGIN_USER")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("DEFAULT_MESSGATEWAY")).thenReturn("SMSC");
        when(PreferenceCache.getSystemPreferenceValue("C2S_MIN_PIN_LENGTH")).thenReturn(4);
        when(PreferenceCache.getSystemPreferenceValue("SEP_TRF_CTRL")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("C2S_SEP_TRFR_COUNT")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("USER_CODE_REQUIRED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("C2S_PIN_BLK_RST_DRTN")).thenReturn(1440);
        when(PreferenceCache.getSystemPreferenceValue("C2S_DYS_ATR_CNGE_PIN")).thenReturn(365);
        when(PreferenceCache.getSystemPreferenceValue("P2P_DYS_ATR_CNGE_PIN")).thenReturn(365);
        when(PreferenceCache.getSystemPreferenceValue("MAX_MSISDN_TEXTBOX")).thenReturn(15);
        when(PreferenceCache.getSystemPreferenceValue("MSISDN_PREFIX_LENGTH")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("AMOUNT_MULT_FACTOR")).thenReturn(100);
        when(PreferenceCache.getSystemPreferenceValue("PIN_REQUIRED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("PIN_LENGTH")).thenReturn(4);
        when(PreferenceCache.getSystemPreferenceValue("DEFAULT_LANGUAGE")).thenReturn("en");
        when(PreferenceCache.getSystemPreferenceValue("SKEY_REQUIRED")).thenReturn("N");
        when(PreferenceCache.getSystemPreferenceValue("MON_SDR_MX_TRANS_NUM")).thenReturn(999999999);
        when(PreferenceCache.getSystemPreferenceValue("MON_SDR_MX_TRANS_AMT")).thenReturn(999999999);
        when(PreferenceCache.getSystemPreferenceValue("WK_SDR_MX_TRANS_AMT")).thenReturn(99999999);
        when(PreferenceCache.getSystemPreferenceValue("WK_SDR_MX_TRANS_NUM")).thenReturn(5000000);
        when(PreferenceCache.getSystemPreferenceValue("P2P_PIN_BLK_RST_DRTN")).thenReturn(1440);
        when(PreferenceCache.getSystemPreferenceValue("P2P_MAX_PIN_BLK_CONT")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("MVD_MAX_VOUCHER")).thenReturn(10000);
        when(PreferenceCache.getSystemPreferenceValue("SYSTEM_DTTIME_FORMAT")).thenReturn("dd/MM/yy HH:mm:ss");
        when(PreferenceCache.getSystemPreferenceValue("C2S_AMB_CR_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("FINANCIAL_YEAR_START")).thenReturn(0);
        when(PreferenceCache.getSystemPreferenceValue("EXTERNAL_TXN_NUMERIC")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("CHK_BLK_LST_STAT")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("EXTTXNMANDT_DOMAINTP")).thenReturn("DISTB_CHAN,CORPORATE");
        when(PreferenceCache.getSystemPreferenceValue("EXTTXNMANDT_FOC")).thenReturn(4);
        when(PreferenceCache.getSystemPreferenceValue("NOTIFI_SRVCCLS_REC")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("NOTIF_SRVCCLS_RECC2S")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("NOTIFI_SRVCCLS_SEN")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("NOTIFSRVCLS_REC_BLPY")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("SECOND_LANG_ENCODING")).thenReturn("ISO-8859-15");
        when(PreferenceCache.getSystemPreferenceValue("ALLOW_SELF_BILLPAY")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("C2S_AMB_CR_ALOW_PPBP")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("SEP_OUTSIDE_TXN_CTRL")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("DEF_FRCXML_SEL_BLPY")).thenReturn("");
        when(PreferenceCache.getSystemPreferenceValue("PP_DEF_STATUS_ACT")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("SPACE_ALLOW_IN_LOGIN")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("DEF_WEB_GW_CODE")).thenReturn("WEB");
        when(PreferenceCache.getSystemPreferenceValue("MSISDN_PREFIX_LIST")).thenReturn("236,+236,0236");
        when(PreferenceCache.getSystemPreferenceValue("DYS_AFTER_CHANGE_PWD")).thenReturn("365");
        when(PreferenceCache.getSystemPreferenceValue("REQ_CUSER_DLT_APP")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("ALWD_SERVICES_NUMBCK")).thenReturn("RC,PRC");
        when(PreferenceCache.getSystemPreferenceValue("EXTTXNLEVEL")).thenReturn("1,2");
        when(PreferenceCache.getSystemPreferenceValue("EXTTXNMANDT")).thenReturn("1,2");
        when(PreferenceCache.getSystemPreferenceValue("USE_PPAID_CONTROLS")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("C2S_PLAIN_SMS_ALLWD")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("LANGAUGES_SUPPORTED")).thenReturn("0:en#US,1:fr#NG");
        when(PreferenceCache.getSystemPreferenceValue("DEFAULT_COUNTRY")).thenReturn("US");
        when(PreferenceCache.getSystemPreferenceValue("C2S_USER_REGTN_REQ")).thenReturn("Y");
        when(PreferenceCache.getSystemPreferenceValue("ALLOW_SELF_TOPUP")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("OPERATOR_UTIL_C")).thenReturn("com.btsl.pretups.util.OperatorUtil");
        when(PreferenceCache.getSystemPreferenceValue("USRLEVELAPPROVAL")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("PROD_REQ")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("TR_ID_REQ")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("LG_MN_REQ")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("DEF_PROD")).thenReturn("ETOPUP");
        when(PreferenceCache.getSystemPreferenceValue("SMS_P_INDX")).thenReturn("03");
        when(PreferenceCache.getSystemPreferenceValue("ERROR_FOR_FAIL_CT")).thenReturn("76711");
        when(PreferenceCache.getSystemPreferenceValue("FRSTAPPLM")).thenReturn(10000);
        when(PreferenceCache.getSystemPreferenceValue("MAXTRNSFR")).thenReturn(5000000);
        when(PreferenceCache.getSystemPreferenceValue("MINTRNSFR")).thenReturn(1000);
        when(PreferenceCache.getSystemPreferenceValue("PEERTRNSFR")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("SKEYEXPIRYSEC")).thenReturn(300);
        when(PreferenceCache.getSystemPreferenceValue("CIRCLEMAXLMT")).thenReturn("5000000000");
        when(PreferenceCache.getSystemPreferenceValue("SKEYLENGTH")).thenReturn(4);
        when(PreferenceCache.getSystemPreferenceValue("CIRCLEMINLMT")).thenReturn(100);
        when(PreferenceCache.getSystemPreferenceValue("P2P_AMB_CR_ALLOWED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("P2P_CR_BACK_ALLOWED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("MAX_PWD_BLOCK_COUNT")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("PWD_BLK_RST_DURATION")).thenReturn(24);
        when(PreferenceCache.getSystemPreferenceValue("TRSFR_DEF_SRVCTYPE")).thenReturn("PRE");
        when(PreferenceCache.getSystemPreferenceValue("USER_HIERARCHY_SIZE")).thenReturn(50);
        when(PreferenceCache.getSystemPreferenceValue("DEFAULT_PRODUCT")).thenReturn("101");
        when(PreferenceCache.getSystemPreferenceValue("C2S_MAX_PIN_LENGTH")).thenReturn(4);
        when(PreferenceCache.getSystemPreferenceValue("MAX_LOGIN_PWD_LENGTH")).thenReturn(8);
        when(PreferenceCache.getSystemPreferenceValue("MIN_LOGIN_PWD_LENGTH")).thenReturn(6);
        when(PreferenceCache.getSystemPreferenceValue("MAX_SMS_PIN_LENGTH")).thenReturn(4);
        when(PreferenceCache.getSystemPreferenceValue("MAX_MSISDN_LENGTH")).thenReturn(15);
        when(PreferenceCache.getSystemPreferenceValue("P2P_PLAIN_SMS_SEPT")).thenReturn("");
        when(PreferenceCache.getSystemPreferenceValue("MIN_SMS_PIN_LENGTH")).thenReturn(4);
        when(PreferenceCache.getSystemPreferenceValue("USER_EVENT_REMARKS")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("MULT_CRE_TRA_DED_ACC_SEP")).thenReturn(",");
        when(PreferenceCache.getSystemPreferenceValue("APPROVER_CAN_EDIT")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("VAL_DAYS_TO_CHK_VLUP")).thenReturn(31);
        when(PreferenceCache.getSystemPreferenceValue("C2C_SMS_NOTIFY")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("NEG_ADD_COMM_APPLY")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("DISSABLE_BUTTON_LIST")).thenReturn("NO_BUTTON");
        when(PreferenceCache.getSystemPreferenceValue("SMS_MMS_ALLOWED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("ALLOW_SELF_TOPUP_FRC")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("STK_REG_ICCID")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("ACT_BONUS_REDEM_DUR")).thenReturn(15);
        when(PreferenceCache.getSystemPreferenceValue("ACT_BONUS_MIN_AMOUNT")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("VOLUME_CALC_ALLOWED")).thenReturn("N");
        when(PreferenceCache.getSystemPreferenceValue("POSITIVE_COMM_APPLY")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("CHECK_REC_TXN_AT_IAT")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("IS_IAT_RUNNING")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("EXTTXNMANDT_DP")).thenReturn(4);
        when(PreferenceCache.getSystemPreferenceValue("DP_ODR_APPROVAL_LVL")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("EXT_CODE_MAND_DP")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("DP_SMS_NOTIFY")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("DP_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("LAST_X_TRF_DAYS_NO")).thenReturn(1);
        when(PreferenceCache.getSystemPreferenceValue("SHA2_FAMILY_TYPE")).thenReturn("SHA-256");
        when(PreferenceCache.getSystemPreferenceValue("HTTPS_ENABLE")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("NWADM_CROSS_ALLOW")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("STAFF_AS_USER")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("RVERSE_TRN_EXPIRY")).thenReturn(7);
        when(PreferenceCache.getSystemPreferenceValue("RVE_C2S_TRN_EXPIRY")).thenReturn(999999);
        when(PreferenceCache.getSystemPreferenceValue("PRVT_RCHRG_ALLOW")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("MVD_MIN_VOUCHER")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("NAMEEMBOSS_SEPT")).thenReturn("_");
        when(PreferenceCache.getSystemPreferenceValue("VOMS_MIN_EXPIRY_DAYS")).thenReturn(1);
        when(PreferenceCache.getSystemPreferenceValue("ALLOW_SELF_TOPUP_BRC")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("CARD_GROUP_BONUS_RANGE")).thenReturn(500);
        when(PreferenceCache.getSystemPreferenceValue("LOGIN_SPCL_CHAR_ALLOW")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_PIN_ENCRIPT_ALLOWED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("FEE_VALDAYS_TO_EXT")).thenReturn("800,30");
        when(PreferenceCache.getSystemPreferenceValue("IS_CT_WITH_VAL_UPDN")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("ALLOW_SELF_EVR")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("C2S_PWD_BLK_EXP_DRN")).thenReturn("999999999999");
        when(PreferenceCache.getSystemPreferenceValue("CP2P_PIN_BLK_EXP_DRN")).thenReturn(3);
        when(PreferenceCache.getSystemPreferenceValue("C2S_PIN_BLK_EXP_DRN")).thenReturn("999999999999");
        when(PreferenceCache.getSystemPreferenceValue("IS_SEPARATE_RPT_DB")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("DISABLE_SEND_PIN_BTN")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("ZERO_BAL_THRESHOLD_VALUE")).thenReturn(0);
        when(PreferenceCache.getSystemPreferenceValue("IS_SEP_BONUS_REQUIRED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("DISABLE_UNBLOCK_PASWD_BTN")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("MESSAGE_TO_PRIMARY_REQUIRED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("SECONDARY_NUMBER_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("P2P_REG_EXPIRY_PERIOD")).thenReturn(999999999);
        when(PreferenceCache.getSystemPreferenceValue("C2S_RANDOM_PIN_GENERATE")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("WEB_RANDOM_PWD_GENERATE")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("BATCH_USER_PASSWD_MODIFY_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("C2C_BATCH_APPROVAL_LVL")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("RESET_PWD_EXP_TIME_IN_HOURS")).thenReturn("999999999");
        when(PreferenceCache.getSystemPreferenceValue("RESET_PIN_EXP_TIME_IN_HOURS")).thenReturn("999999999");
        when(PreferenceCache.getSystemPreferenceValue("C2S_CARD_GROUP_SLAB_COPY")).thenReturn("N");
        when(PreferenceCache.getSystemPreferenceValue("AUTO_PIN_GENERATE_ALLOW")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("AUTO_PWD_GENERATE_ALLOW")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("PSWD_EXP_TIME_IN_HOUR_ON_CREATION")).thenReturn("999999999");
        when(PreferenceCache.getSystemPreferenceValue("POINT_CONVERSION_FACTOR")).thenReturn("1");
        when(PreferenceCache.getSystemPreferenceValue("MULTIPLE_WALLET_APPLY")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("EXTTXNMANDT_DOMAINTP_DP")).thenReturn("DISTB_CHAN,CORPORATE,DEALER,COMP_SHOP");
        when(PreferenceCache.getSystemPreferenceValue("CHNL_PLAIN_SMS_SEPT_LOGINID")).thenReturn("#");
        when(PreferenceCache.getSystemPreferenceValue("IS_DEFAULT_PROFILE")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("O2C_EMAIL_NOTIFICATION")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("SID_ISNUMERIC")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("SMS_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("AUTO_C2C_TRANSFER_AMT")).thenReturn(10000);
        when(PreferenceCache.getSystemPreferenceValue("MIN_SID_LENGTH")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("MAX_SID_LENGTH")).thenReturn(15);
        when(PreferenceCache.getSystemPreferenceValue("MVD_MAX_VOUCHER_EXTGW")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("RPTSUMM_MAX_DATEDIFF")).thenReturn(15);
        when(PreferenceCache.getSystemPreferenceValue("C2S_RECHARGE_MULTIPLE_ENTRY")).thenReturn("S");
        when(PreferenceCache.getSystemPreferenceValue("LAST_X_TRANSFER_STATUS")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("PINPAS_EN_DE_CRYPTION_TYPE")).thenReturn("DES");
        when(PreferenceCache.getSystemPreferenceValue("P2P_CARD_GROUP_SLAB_COPY")).thenReturn("N");
        when(PreferenceCache.getSystemPreferenceValue("SERVICE_FOR_LAST_X_TRANSFER")).thenReturn("C2S,O2C,C2C");
        when(PreferenceCache.getSystemPreferenceValue("LAST_X_CUSTENQ_STATUS")).thenReturn("1");
        when(PreferenceCache.getSystemPreferenceValue("LAST_TRF_MULTIPLE_SMS")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("DLRY_RCPT_TRK")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("CR_BK_ALW_EVD_AMB")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("SLF_EVD_ALWD")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("SR_WISE_MSG_EVD")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("BUDDY_PIN_REQ")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("FOC_SMS_NOTIFY")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("PRF_ASSOCIATE_AGENT")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("C2S_ENQ_BAL_HIDE")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("MAX_ALLD_BALANCE_C2S")).thenReturn(1000000000);
        when(PreferenceCache.getSystemPreferenceValue("MO_TOTXN_AMT_ALLWDCO")).thenReturn("3000000000");
        when(PreferenceCache.getSystemPreferenceValue("MO_SUCTRAN_ALLWDCOUN")).thenReturn(5000);
        when(PreferenceCache.getSystemPreferenceValue("WE_TOTXN_AMT_ALLWDCO")).thenReturn("1000000000");
        when(PreferenceCache.getSystemPreferenceValue("WE_SUCTRAN_ALLWDCOUN")).thenReturn(30000);
        when(PreferenceCache.getSystemPreferenceValue("MAX_ALLD_BALANCE_P2P")).thenReturn(50000000);
        when(PreferenceCache.getSystemPreferenceValue("MO_SUCTRAN_ALLWD_P2P")).thenReturn(1000);
        when(PreferenceCache.getSystemPreferenceValue("MO_REC_AMT_ALLWD_P2P")).thenReturn(5000000);
        when(PreferenceCache.getSystemPreferenceValue("WE_SUCTRAN_ALLWD_P2P")).thenReturn(700);
        when(PreferenceCache.getSystemPreferenceValue("WE_REC_AMT_ALLWD_P2P")).thenReturn(1250000);
        when(PreferenceCache.getSystemPreferenceValue("C2S_REF_NUM_REQ")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("C2S_REF_NUM_UNIQUE")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("PAYAMT_MRP_SAME")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("PRV_PASS_NOT_ALLOW")).thenReturn(3);
        when(PreferenceCache.getSystemPreferenceValue("PRV_PIN_NOT_ALLOW")).thenReturn(3);
        when(PreferenceCache.getSystemPreferenceValue("CP2P_PIN_VALIDAT_REQ")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("REC_MSG_SEND_ALLOW")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("OPT_USR_APRL_LEVEL")).thenReturn(1);
        when(PreferenceCache.getSystemPreferenceValue("IS_FEE_APPL_VAL_EXT")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("PVT_SID_SERVICE_ALLOW")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("RP2PDWH_OPT_SPECIFIC_PROC_NAME")).thenReturn("RP2PDWHTEMPPRC");
        when(PreferenceCache.getSystemPreferenceValue("P2PDWH_OPT_SPECIFIC_PROC_NAME")).thenReturn("P2pdwhtempprc");
        when(PreferenceCache.getSystemPreferenceValue("IATDWH_OPT_SPECIFIC_PROC_NAME")).thenReturn("Iatdwhtempprc");
        when(PreferenceCache.getSystemPreferenceValue("PVT_RECH_MESSGATEWAY")).thenReturn("PVTRECH");
        when(PreferenceCache.getSystemPreferenceValue("EXTTXNMANDT_O2C")).thenReturn(4);
        when(PreferenceCache.getSystemPreferenceValue("EXT_CODE_MAND_O2C")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("MSISDN_USAGE_SUMM_FLAG")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("O2C_ODR_APPROVAL_LVL")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("O2C_SMS_NOTIFY")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("AUTO_FOC_TRANSFER_AMT")).thenReturn(500);
        when(PreferenceCache.getSystemPreferenceValue("AUTO_FOC_TRANSFER_AMOUNT")).thenReturn(500);
        when(PreferenceCache.getSystemPreferenceValue("ALLOW_ROAM_RECHARGE")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("SOS_ALLOWED_MAX_BALANCE")).thenReturn(500);
        when(PreferenceCache.getSystemPreferenceValue("SOS_RECHARGE_AMOUNT")).thenReturn(500);
        when(PreferenceCache.getSystemPreferenceValue("SOS_ST_DEDUCT_UPFRONT")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("SOS_DAYS_GAP_BTWN_TWO_TRAN")).thenReturn("0");
        when(PreferenceCache.getSystemPreferenceValue("SOS_SETTLE_DAYS")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("GMB_INTERACTIVE_OPTION_ALLOWED")).thenReturn("Y");
        when(PreferenceCache.getSystemPreferenceValue("USSD_MENU_CODE")).thenReturn("100");
        when(PreferenceCache.getSystemPreferenceValue("USSD_TAGS_CELLID_SWITCHID_MANDATORY")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("SOS_ELIBILITY_ACC")).thenReturn("Core,LMB_ALLOWED");
        when(PreferenceCache.getSystemPreferenceValue("SOS_MINIMUM_AON")).thenReturn(90);
        when(PreferenceCache.getSystemPreferenceValue("SOS_MIN_VALIDITY_DAYS")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("SOS_ONLINE_ALLOW")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("ENQ_POSTBAL_ALLOW")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("ENQ_POSTBAL_IN")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("LMB_VALIDITY_DAYS_FORCESETTLE")).thenReturn(120);
        when(PreferenceCache.getSystemPreferenceValue("LAST_SERVICE_TYPE_CHECK")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("MRP_BLOCK_TIME_ALLOWED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("LMB_BLK_UPL")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("LMB_FORCE_SETL_STAT_ALLOW")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("PLAIN_RES_PARSE_REQUIRED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("COUNTRY_CODE")).thenReturn(91);
        when(PreferenceCache.getSystemPreferenceValue("REQ_CUSER_SUS_APP")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("RVERSE_TXN_APPRV_LVL")).thenReturn(0);
        when(PreferenceCache.getSystemPreferenceValue("DB_ENTRY_NOT_ALLOWED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("ACT_FRST_RCH_APP")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("C2S_REC_GEN_FAIL_MSG_REQD_V_FOR_GIFTER")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("C2S_REC_GEN_FAIL_MSG_REQD_T_FOR_GIFTER")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_MAX_APPROVAL_LEVEL")).thenReturn(3);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_USER_KEY_REQD")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("VOUCHER_TRACKING_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("VOUCHER_EN_ON_TRACKING")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("MAX_AUTO_FOC_ALLOW_LIMIT")).thenReturn(20);
        when(PreferenceCache.getSystemPreferenceValue("PROCESS_FEE_REV_ALLOWED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("LMB_DEBIT_REQ")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("USR_DEF_CONFIG_UPDATE_REQ")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("SELECTOR_INTERFACE_MAPPING")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("CP_SUSPENSION_DAYS_LIMIT")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("CAT_ALLOW_CREATION")).thenReturn("DIST,BCU,RET,AG");
        when(PreferenceCache.getSystemPreferenceValue("MSISDN_MIGRATION_LIST")).thenReturn("012:0122");
        when(PreferenceCache.getSystemPreferenceValue("PRVT_RC_MSISDN_PREFIX_LIST")).thenReturn("1111,2222");
        when(PreferenceCache.getSystemPreferenceValue("LAST_X_RECHARGE_STATUS")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValue("SERVICE_FOR_LAST_X_RECHARGE")).thenReturn("C2S");
        when(PreferenceCache.getSystemPreferenceValue("SMS_PIN_BYPASS_GATEWAY")).thenReturn("PLAIN");
        when(PreferenceCache.getSystemPreferenceValue("IS_FNAME_LNAME_ALLOWED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("EXT_CODE_MAND_USER")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("EXT_VOMS_MSG_ENDEC_KEY")).thenReturn("ZO4UGUGCAFTLK9MOZO4UGUGCAFTLK9MO");
        when(PreferenceCache.getSystemPreferenceValue("MCDL_MAX_LIST_COUNT")).thenReturn(60);
        when(PreferenceCache.getSystemPreferenceValue("MCDL_DIFF_REQST_SEP")).thenReturn(",");
        when(PreferenceCache.getSystemPreferenceValue("P2P_MCDL_DEFAULT_AMOUNT")).thenReturn(0);
        when(PreferenceCache.getSystemPreferenceValue("P2P_MCDL_MAXADD_AMOUNT")).thenReturn(50000000);
        when(PreferenceCache.getSystemPreferenceValue("P2P_MCDL_AUTO_DELETION_DAYS")).thenReturn(90);
        when(PreferenceCache.getSystemPreferenceValue("COS_REQUIRED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("TRF_RULE_USER_LEVEL_ALLOW")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("RSA_AUTHENTICATION_REQUIRED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("COUNT_TO_ASK_RSA_CODE")).thenReturn(3);
        when(PreferenceCache.getSystemPreferenceValue("LOGIN_ID_CHECK_ALLOWED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("STAFF_USER_APRL_LEVEL")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("BATCH_USER_PROFILE_ASSIGN")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("BATCH_INTIATE_NOTIF_TYPE")).thenReturn("BOTH");
        when(PreferenceCache.getSystemPreferenceValue("EMAIL_SERVICE_ALLOW")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("SRVC_PROD_MAPPING_ALLOWED")).thenReturn("RC,GRC,INTRRC,RCREV,PSTNRC,PPB");
        when(PreferenceCache.getSystemPreferenceValue("SERVICE_PROVIDER_PROMO_ALLOW")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("CELL_GROUP_REQUIRED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("SRVC_PROD_INTFC_MAPPING_ALLOWED")).thenReturn("PVAS,VAS,RC,CDATA,DTH");
        when(PreferenceCache.getSystemPreferenceValue("CELL_ID_SWITCH_ID_REQUIRED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("DEBIT_SENDER_SIMACT")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("SIMACT_DEFAULT_SELECTOR")).thenReturn("1");
        when(PreferenceCache.getSystemPreferenceValue("IN_PROMO_REQUIRED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("LOGIN_PASSWORD_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("MULTI_AMOUNT_ENABLED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("SHOW_CAPTCHA")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("EMAIL_AUTH_REQ")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("PER_DAY_BAR_FOR_DEL_LIMIT")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("REQ_CUSER_BAR_APP")).thenReturn("1");
        when(PreferenceCache.getSystemPreferenceValue("AUTO_O2C_MAX_APPROVAL_LEVEL")).thenReturn(3);
        when(PreferenceCache.getSystemPreferenceValue("AUTO_O2C_AMOUNT")).thenReturn(1000);
        when(PreferenceCache.getSystemPreferenceValue("SRVCS_FOR_PROD_MAPPING")).thenReturn("VAS,PVAS,RCREV");
        when(PreferenceCache.getSystemPreferenceValue("IS_REQ_MSISDN_FOR_STAFF")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("MIN_HRDIF_CR_ST_LMS")).thenReturn(8);
        when(PreferenceCache.getSystemPreferenceValue("MIN_HRDIF_ST_ED_LMS")).thenReturn(120);
        when(PreferenceCache.getSystemPreferenceValue("LMS_APPL")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("IS_SEPARATE_EXT_DB")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("LMS_VOL_COUNT_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("LMS_PROF_APR_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("LMS_MULT_FACTOR")).thenReturn("1.99");
        when(PreferenceCache.getSystemPreferenceValue("LMS_VOL_CREDIT_LOYAL_PTS")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("MAX_LAST_TRANSFERS_DAYS")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("THLD_PRTP_PRCSS_TIME")).thenReturn(400);
        when(PreferenceCache.getSystemPreferenceValue("EXTSYS_USR_APRL_LEVEL_REQUIRED")).thenReturn(0);
        when(PreferenceCache.getSystemPreferenceValue("LMS_STOCK_REQUIRED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("INVALID_PWD_COUNT_FOR_CAPTCHA")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("PAYMENTDETAILSMANDATE_O2C")).thenReturn("0");
        when(PreferenceCache.getSystemPreferenceValue("USER_CREATION_MANDATORY_FIELDS")).thenReturn("email,externalCode");
        when(PreferenceCache.getSystemPreferenceValue("USER_APPROVAL_LEVEL")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("O2C_APPRV_QTY_LEVEL")).thenReturn("1,2");
        when(PreferenceCache.getSystemPreferenceValue("STAFF_USER_AUTH_TYPE")).thenReturn("NA");
        when(PreferenceCache.getSystemPreferenceValue("CHANNEL_USER_ROLE_TYPE_DISPLAY")).thenReturn("ALL");
        when(PreferenceCache.getSystemPreferenceValue("IS_PARTIAL_BATCH_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("MULTIPLE_VOUCHER_TABLE")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_INTERFACE_FLAG")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("USER_PRODUCT_MULTIPLE_WALLET")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("DEFAULT_WALLET")).thenReturn("MAIN");
        when(PreferenceCache.getSystemPreferenceValue("MIN_VOUCHER_CODE_LENGTH")).thenReturn(6);
        when(PreferenceCache.getSystemPreferenceValue("IS_MSISDN_ASSO_REQ")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("ADMINISTRBLY_USER_STATUS_CHANG")).thenReturn("DE:Y,DE:CH,DE:EX,DE:N,EX:Y,EX:CH,CH:Y,Y:EX,PA:DE");
        when(PreferenceCache.getSystemPreferenceValue("REALTIME_AUTO_C2C_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("C2S_TRNSFR_INVNO_SRVCTYP")).thenReturn("CPB");
        when(PreferenceCache.getSystemPreferenceValue("O2C_PNDNG_FOR_APPR_SEND_MAIL_DAYS_CNT")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("OTP_TIMEOUT_INSEC")).thenReturn(121);
        when(PreferenceCache.getSystemPreferenceValue("C2S_TRNSFR_AMTBLCK_SRVCTYP")).thenReturn("PIN,CE,PMD,RPB,CCN");
        when(PreferenceCache.getSystemPreferenceValue("AUTO_O2C_APPROVAL_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("MIN_ACCOUNT_ID_LENGTH")).thenReturn(8);
        when(PreferenceCache.getSystemPreferenceValue("MAX_ACCOUNT_ID_LENGTH")).thenReturn(8);
        when(PreferenceCache.getSystemPreferenceValue("SAP_INTEGARATION_FOR_USRINFO")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("AUTH_TYPE_REQ")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("COUNT_TO_ASK_OTP_CODE")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("C2S_REVERSAL_TXNID_SRVCTYP")).thenReturn("CCN");
        when(PreferenceCache.getSystemPreferenceValue("CAPTCHA_LENGTH")).thenReturn(6);
        when(PreferenceCache.getSystemPreferenceValue("MAX_AUTOTOPUP_AMT")).thenReturn(10000);
        when(PreferenceCache.getSystemPreferenceValue("PERCENTAGE_OF_PRE_REVERSAL")).thenReturn(70);
        when(PreferenceCache.getSystemPreferenceValue("WALLET_FOR_ADNL_CMSN")).thenReturn("BONUS");
        when(PreferenceCache.getSystemPreferenceValue("ALLOW_ROAM_ADDCOMM")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("ALLOW_CCARD_ROAM_RECHARGE")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("ROAM_INTERFACE_ID")).thenReturn("INTID00032");
        when(PreferenceCache.getSystemPreferenceValue("LMS_PCT_POINTS_CALCULATION")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("SEC_QUES_COUNT")).thenReturn(3);
        when(PreferenceCache.getSystemPreferenceValue("VOUCHER_SERVICES_LIST")).thenReturn("RC,PRC,EVD,PPB");
        when(PreferenceCache.getSystemPreferenceValue("TXN_RECEIVER_USER_STATUS_CHANG")).thenReturn("W:CH,EX:Y,CH:Y,PA:Y");
        when(PreferenceCache.getSystemPreferenceValue("TXN_SENDER_USER_STATUS_CHANG")).thenReturn("EX:Y,CH:Y,PA:Y");
        when(PreferenceCache.getSystemPreferenceValue("LIFECYCLE_STATUS_DAYS_LIST")).thenReturn("PA:Y:CH:EX:DE:N,1:1:1:1:1");
        when(PreferenceCache.getSystemPreferenceValue("BLOCKING_ALLOWED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("ALERT_ALLOWED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("CARD_GROUP_ENQRES")).thenReturn("NAME:RANGE:REV_PR:DATE");
        when(PreferenceCache.getSystemPreferenceValue("SEND_SMS_TO_PARENT")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("DP_NETWORKLEVEL_DAILYLIMIT")).thenReturn(100000);
        when(PreferenceCache.getSystemPreferenceValue("REALTIME_AUTOALERT_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("SEND_SMS2PARENT_SERVICES_LIST")).thenReturn("RC,PRC,EVD,PPB");
        when(PreferenceCache.getSystemPreferenceValue("DP_SYSTEMLEVEL_LIMIT")).thenReturn(30000);
        when(PreferenceCache.getSystemPreferenceValue("RET_OPRTR_STOCK")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("ROAM_PENALTY_OWNER_PERCENTAGE")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("ROAM_RECHARGE_DAILY_THRESHOLD")).thenReturn(100);
        when(PreferenceCache.getSystemPreferenceValue("ROAM_RECHARGE_PENALTY_PERCENTAGE")).thenReturn(40);
        when(PreferenceCache.getSystemPreferenceValue("CENTRALIZED_USER_MANAGEMENT")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("INTF_NODE_VALIDATION")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("OPT_IN_OUT_ALLOW")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("EXTTXNMANDT_LPT")).thenReturn(4);
        when(PreferenceCache.getSystemPreferenceValue("EXT_CODE_MAND_LPT")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("LPT_ODR_APPROVAL_LVL")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("M_PRE_PERCENTAGE")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("M_SLAVE_PERCENTAGE")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("DECENTER_ROAM_LOCATION")).thenReturn("MH");
        when(PreferenceCache.getSystemPreferenceValue("P2P_ALLOWED_SCHTYPE")).thenReturn("WK,MO");
        when(PreferenceCache.getSystemPreferenceValue("SRVC_ALLOW_ROAM_PENALTY")).thenReturn("RC,GRC");
        when(PreferenceCache.getSystemPreferenceValue("CHOICE_RECHARGE_APPLICABLE")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("TWO_FA_REQ")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("TIME_FOR_REVERSAL")).thenReturn(48000);
        when(PreferenceCache.getSystemPreferenceValue("TIME_FOR_REVERSAL_CCE")).thenReturn(48);
        when(PreferenceCache.getSystemPreferenceValue("ALLOWED_DAYS_FOR_REVERSAL")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("ALLOWED_SERVICES_FOR_REVERSAL")).thenReturn("RC,GRC");
        when(PreferenceCache.getSystemPreferenceValue("ALLOW_BULK_C2S_REVERSAL_MESSAGE")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("ALLOWED_GATEWAY_FOR_BULK_REVERSAL")).thenReturn("WEB");
        when(PreferenceCache.getSystemPreferenceValue("DP_ONLINE_LIMIT")).thenReturn(3000);
        when(PreferenceCache.getSystemPreferenceValue("DECIMAL_ALLOWED_IN_SERVICES")).thenReturn("RC,GRC,PPB,PRC,INTRRC,PSTNRC");
        when(PreferenceCache.getSystemPreferenceValue("ADMIN_MESSAGE_REQD")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("O2C_BATCH_WITHDRAW_MESSAGE_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("OTP_ALLOWED_LENGTH")).thenReturn(8);
        when(PreferenceCache.getSystemPreferenceValue("DEFAULT_CURRENCY")).thenReturn("COP");
        when(PreferenceCache.getSystemPreferenceValue("FNF_ZB_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("LOW_BASED_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("P2P_SERVICES_TYPE_SERVICECLASS")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("MSISDN_LENGTH")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("LB_SYSTEMLEVEL_LIMIT")).thenReturn(100000);
        when(PreferenceCache.getSystemPreferenceValue("SID_ENCRYPTION_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("DUPLICATE_CARDGROUP_CODE_ALLOW")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("OWNER_COMMISION_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("AUTO_NWSTK_CRTN_ALWD")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("AUTO_NWSTK_CRTN_THRESHOLD")).thenReturn("SAL:ETOPUP:10000:500000,INC:ETOPUP:200000:5000000,FOC:ETOPUP:60000:700000,SAL:POSTETOPUP:200:6000000,INC:POSTETOPUP:2000:5000000,FOC:POSTETOPUP:60000:700000,SAL:VOUCHTRACK:200:6000000,INC:VOUCHTRACK:2000:5000000,FOC:VOUCHTRACK:60000:700000");
        when(PreferenceCache.getSystemPreferenceValue("TOKEN_EXPIRY_IN_MINTS")).thenReturn(60);
        when(PreferenceCache.getSystemPreferenceValue("O2C_DIRECT_TRANSFER")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("SEQUENCE_ID_RANGE")).thenReturn(12);
        when(PreferenceCache.getSystemPreferenceValue("SEQUENCE_ID_ENABLE")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("HASHING_ID_RANGE")).thenReturn(12);
        when(PreferenceCache.getSystemPreferenceValue("HASHING_ENABLE")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("CHANNEL_SOS_ENABLE")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("AUTO_C2C_SOS_CAT_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("CHANNEL_AUTOC2C_ENABLE")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("SOS_SETTLEMENT_TYPE")).thenReturn("MANUAL");
        when(PreferenceCache.getSystemPreferenceValue("CHANNEL_SOS_ALLOWED_WALLET")).thenReturn("PARENT");
        when(PreferenceCache.getSystemPreferenceValue("DOWNLOAD_CSV_REPORT_REQUIRED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("ALLOW_TRANSACTION_IF_SOS_SETTLEMENT_FAIL")).thenReturn("FOC, DP");
        when(PreferenceCache.getSystemPreferenceValue("LR_ENABLED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("TARGET_BASED_ADDNL_COMMISSION")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("OFFLINE_SETTLE_EXTUSR")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("TARGET_BASED_ADDNL_COMMISSION_SLABS")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("TARGET_BASED_BASE_COMMISSION_SLABS")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("TARGET_BASED_BASE_COMMISSION")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("ALLOWD_USR_TYP_CREATION")).thenReturn("N");
        when(PreferenceCache.getSystemPreferenceValue("SYSTEM_ROLE_ALLOWED")).thenReturn("N");
        when(PreferenceCache.getSystemPreferenceValue("CHNL_USR_LAST_ACTIVE_TXN")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("ADD_COMM_SEPARATE_MSG")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("CHANNEL_TRANSFERS_INFO_REQUIRED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("REALTIME_OTF_MESSAGES")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("POST_SERVICE_CLASS")).thenReturn("1000");
        when(PreferenceCache.getSystemPreferenceValue("SUBS_BLK_AFT_X_CONS_FAIL")).thenReturn(4);
        when(PreferenceCache.getSystemPreferenceValue("SUBS_UNBLK_AFT_X_TIME")).thenReturn(90);
        when(PreferenceCache.getSystemPreferenceValue("DECRYPT_KEY_VISIBLE")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("THIRD_PARTY_VISIBLE")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_PROFILE_ACTIVATION_REQ")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("VMS_AUTO_VOUCHER_CRTN_ALWD")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("STK_MASTER_KEY")).thenReturn("6B4FC9246FB075B619626600EAA870F9");
        when(PreferenceCache.getSystemPreferenceValue("BURN_RATE_THRESHOLD_PCT")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("VOUCHER_BURN_RATE_SMS_ALERT")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("VOUCHER_BURN_RATE_EMAIL_ALERT")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("PIN_VALIDATATION_IN_USSD")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("INET_REPORT_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_VALIDATION_BY_IN")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_DAMG_PIN_LNTH_ALLOW")).thenReturn(3);
        when(PreferenceCache.getSystemPreferenceValue("VPIN_INVALID_COUNT")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_PROF_TALKTIME_MANDATORY")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_PROF_VALIDITY_MANDATORY")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_PROFILE_DEF_MINMAXQTY")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_PROFILE_MIN_REORDERQTY")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_PROFILE_MAX_REORDERQTY")).thenReturn(10000);
        when(PreferenceCache.getSystemPreferenceValue("NW_NATIONAL_PREFIX")).thenReturn(99);
        when(PreferenceCache.getSystemPreferenceValue("NW_CODE_NW_PREFIX_MAPPING")).thenReturn("NG=11,PB=12");
        when(PreferenceCache.getSystemPreferenceValue("OTH_COM_CHNL")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("SERVICES_ALLOWED_SHOW_CARDGROUPLIST")).thenReturn("ABC");
        when(PreferenceCache.getSystemPreferenceValue("MAX_REQ_VOUCHER_QTY")).thenReturn(5000);
        when(PreferenceCache.getSystemPreferenceValue("P2P_PROMO_TRF_APP")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("P2P_PRO_TRF_ST_LVL_CODE")).thenReturn(1);
        when(PreferenceCache.getSystemPreferenceValue("P2P_PRE_SERVCLASS_AS_POST")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("LAST_C2C_ENQ_MSG_REQ")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("USSD_REC_MSG_SEND_ALLOW")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("C2S_SEQ_ALWD")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("C2S_SEQID_FOR_GWC")).thenReturn("");
        when(PreferenceCache.getSystemPreferenceValue("C2S_SEQID_APPL_SER")).thenReturn("");
        when(PreferenceCache.getSystemPreferenceValue("ADDITIONAL_IN_FIELDS_ALLOWED")).thenReturn("");
        when(PreferenceCache.getSystemPreferenceValue("PAYMENT_MODE_ALWD")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("EMAIL_DEFAULT_LOCALE")).thenReturn("en_US");
        when(PreferenceCache.getSystemPreferenceValue("PG_INTEFRATION_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("USER_VOUCHERTYPE_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("PIN_REQUIRED_P2P")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("MAPP_SESSION_EXPIRY_SEC")).thenReturn(300);
        when(PreferenceCache.getSystemPreferenceValue("MAPP_PRODUCT_GROUPING_REQ_SRV")).thenReturn("VAS,PVAS");
        when(PreferenceCache.getSystemPreferenceValue("O2CAMB_MINUTES_DELAY")).thenReturn("-5");
        when(PreferenceCache.getSystemPreferenceValue("VMS_ALLOW_CONTENT_TYPE")).thenReturn("pdf, jpg, jpeg, png");
        when(PreferenceCache.getSystemPreferenceValue("CHNLUSR_VOUCHER_CATGRY_ALLWD")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("TRANSACTION_TYPE")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("VOUCHER_THIRDPARTY_STATUS")).thenReturn("WH");
        when(PreferenceCache.getSystemPreferenceValue("VMSPIN_EN_DE_CRYPTION_TYPE")).thenReturn("DES");
        when(PreferenceCache.getSystemPreferenceValue("IPV6_ENABLED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("NATIONAL_VOUCHER_ENABLE")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("NATIONAL_VOUCHER_NETWORK_CODE")).thenReturn("NG");
        when(PreferenceCache.getSystemPreferenceValue("DOWNLD_BATCH_BY_BATCHID")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("PAYMENT_VERIFICATION_ALLOWED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN")).thenReturn(0);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN")).thenReturn(3);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN")).thenReturn(3);
        when(PreferenceCache.getSystemPreferenceValue("MAX_VOUCHER_EXPIRY_EXTN_LIMIT")).thenReturn(20);
        when(PreferenceCache.getSystemPreferenceValue("ONLINE_BATCH_EXP_DATE_LIMIT")).thenReturn(15);
        when(PreferenceCache.getSystemPreferenceValue("USER_VOUCHERSEGMENT_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("DATE_FORMAT_CAL_JAVA")).thenReturn("dd/MM/yy");
        when(PreferenceCache.getSystemPreferenceValue("DATE_TIME_FORMAT")).thenReturn("dd/MM/yyyy HH24:mi:ss");
        when(PreferenceCache.getSystemPreferenceValue("LOCALE_CALENDAR")).thenReturn("en-US");
        when(PreferenceCache.getSystemPreferenceValue("LOCALE_ENGLISH")).thenReturn("@calendar=persian");
        when(PreferenceCache.getSystemPreferenceValue("TIMEZONE_ID")).thenReturn("Asia/Kolkata");
        when(PreferenceCache.getSystemPreferenceValue("CALENDAR_TYPE")).thenReturn("gregorian");
        when(PreferenceCache.getSystemPreferenceValue("CALENDER_DATE_FORMAT")).thenReturn("dd/mm/yy");
        when(PreferenceCache.getSystemPreferenceValue("CALENDAR_SYSTEM")).thenReturn("gregorian");
        when(PreferenceCache.getSystemPreferenceValue("FORMAT_MONTH_YEAR")).thenReturn("yyyy/mm");
        when(PreferenceCache.getSystemPreferenceValue("EXTERNAL_CALENDAR_TYPE")).thenReturn("persian");
        when(PreferenceCache.getSystemPreferenceValue("IS_CAL_ICON_VISIBLE")).thenReturn("Y");
        when(PreferenceCache.getSystemPreferenceValue("IS_MON_DATE_ON_UI")).thenReturn("N");
        when(PreferenceCache.getSystemPreferenceValue("VOMS_PIN_BLK_EXP_DRN")).thenReturn(100000);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_NATIONAL_LOCAL_PREFIX_ENABLE")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("LDAP_UTIL_C")).thenReturn("com.btsl.pretups.util.LDAPUtil");
        when(PreferenceCache.getSystemPreferenceValue("VMS_SERVICES")).thenReturn("VCN");
        when(PreferenceCache.getSystemPreferenceValue("ONLINE_VOUCHER_GEN_LIMIT_SYSTEM")).thenReturn(100000);
        when(PreferenceCache.getSystemPreferenceValue("VMS_P_STATUS_CHANGE")).thenReturn("EN,DA,ST,OH,WH,S");
        when(PreferenceCache.getSystemPreferenceValue("VOUCHER_PROFLE_IS_OPTIONAL")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("VMS_E_STATUS_CHANGE_MAP")).thenReturn("OH:EN,OH:ST,OH:DA,OH:S,PA:EN,PA:S,S:EN,S:ST,S:OH,EN:OH,EN:ST,EN:DA,EN:S,GE:DA,GE:EN,GE:ST");
        when(PreferenceCache.getSystemPreferenceValue("VMS_D_STATUS_CHANGE_MAP")).thenReturn("OH:EN,OH:ST,OH:DA,OH:S,PA:EN,PA:S,S:EN,S:ST,S:OH,EN:OH,EN:ST,EN:DA,EN:S,GE:DA,GE:ST");
        when(PreferenceCache.getSystemPreferenceValue("VMS_D_CHANGE_STATUS")).thenReturn("EN,DA,ST,OH,S");
        when(PreferenceCache.getSystemPreferenceValue("VMS_E_CHANGE_STATUS")).thenReturn("EN,DA,ST,OH,S");
        when(PreferenceCache.getSystemPreferenceValue("VMS_P_CHANGE_STATUS")).thenReturn("EN,DA,ST,OH,WH,S");
        when(PreferenceCache.getSystemPreferenceValue("VMS_D_STATUS_CHANGE")).thenReturn("EN,DA,ST,OH,S");
        when(PreferenceCache.getSystemPreferenceValue("VMS_E_STATUS_CHANGE")).thenReturn("EN,DA,ST,OH,S");
        when(PreferenceCache.getSystemPreferenceValue("VMS_P_STATUS_CHANGE_MAP")).thenReturn("OH:EN,OH:ST,OH:DA,OH:S,PA:EN,PA:S,S:EN,S:ST,S:OH,EN:OH,EN:ST,EN:DA,EN:S,PE:WH,PE:ST,WH:S,GE:DA,GE:EN,GE:ST");
        when(PreferenceCache.getSystemPreferenceValue("ONLINE_CHANGE_STATUS_SYSTEM_LMT")).thenReturn(100000);
        when(PreferenceCache.getSystemPreferenceValue("C2C_APPRV_QTY_LEVEL")).thenReturn(1);
        when(PreferenceCache.getSystemPreferenceValue("MAX_APPROVAL_LEVEL_C2C_INITIATE")).thenReturn(1);
        when(PreferenceCache.getSystemPreferenceValue("DVD_ORDER_BY_PARAMETERS")).thenReturn("EXPIRY_DATE ,CREATED_ON, SERIAL_NO");
        when(PreferenceCache.getSystemPreferenceValue("REC_MSG_SEND_ALLOW_C2C")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("C2C_ALLOWED_VOUCHER_LIST")).thenReturn("EN,OH,PA,ST,GE");
        when(PreferenceCache.getSystemPreferenceValue("MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER")).thenReturn("");
        when(PreferenceCache.getSystemPreferenceValue("C2C_ALLOW_CONTENT_TYPE")).thenReturn("pdf,png,jpg");
        when(PreferenceCache.getSystemPreferenceValue("ONLINE_VOUCHER_GEN_LIMIT")).thenReturn(100000);
        when(PreferenceCache.getSystemPreferenceValue("VOUCHER_GEN_EMAIL_NOTIFICATION")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("VOUCHER_GEN_SMS_NOTIFICATION")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("SUBSCRIBER_VOUCHER_PIN_REQUIRED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("ONLINE_DVD_LIMIT")).thenReturn(100000);
        when(PreferenceCache.getSystemPreferenceValue("ONLINE_VOUCHER_GEN_LIMIT_NW")).thenReturn(15000);
        when(PreferenceCache.getSystemPreferenceValue("VMS_D_LIFECYCLE")).thenReturn("GE:EN:CU");
        when(PreferenceCache.getSystemPreferenceValue("SCREEN_WISE_ALLOWED_VOUCHER_TYPE")).thenReturn("ACTIVE_PROF:E,ET;VOUC_DOWN:P,PT;O2C:D,DT,P,PT");
        when(PreferenceCache.getSystemPreferenceValue("VMS_P_LIFECYCLE")).thenReturn("GE:PE:WH:EN:CU");
        when(PreferenceCache.getSystemPreferenceValue("VMS_E_LIFECYCLE")).thenReturn("GE:EN:CU");
        when(PreferenceCache.getSystemPreferenceValue("DVD_BATCH_FILEEXT")).thenReturn("csv");
        when(PreferenceCache.getSystemPreferenceValue("ONLINE_CHANGE_STATUS_NETWORK_LMT")).thenReturn(10000);
        when(PreferenceCache.getSystemPreferenceValue("MAX_APPROVAL_LEVEL_C2C_TRANSFER")).thenReturn(0);
        when(PreferenceCache.getSystemPreferenceValue("C2C_EMAIL_NOTIFICATION")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("VOUCHER_PROFILE_OTHER_INFO")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("C2C_SMS_NOTIFICATION")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE")).thenReturn("");
        when(PreferenceCache.getSystemPreferenceValue("PAYMENTDETAILSMANDATE_C2C")).thenReturn(3);
        when(PreferenceCache.getSystemPreferenceValue("IS_BUN_PRE_ID_NULL_ALLOW")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("IS_VOU_BUN_NAME_LEN_ZERO_ALLOW")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("RECENT_C2C_TXN")).thenReturn(4);
        when(PreferenceCache.getSystemPreferenceValue("TWO_FA_REQ_FOR_PIN")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("OTP_RESEND_TIMES")).thenReturn(3);
        when(PreferenceCache.getSystemPreferenceValue("OTP_RESEND_DURATION")).thenReturn(180);
        when(PreferenceCache.getSystemPreferenceValue("OTP_VALIDITY_PERIOD")).thenReturn(5000);
        when(PreferenceCache.getSystemPreferenceValue("C2CVCRPT_DATEDIFF")).thenReturn(40);
        when(PreferenceCache.getSystemPreferenceValue("O2CVCRPT_DATEDIFF")).thenReturn(15);
        when(PreferenceCache.getSystemPreferenceValue("IS_BLANK_VOUCHER_REQ")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("CARD_GROUP_ALLOWED_CATEGORIES")).thenReturn("NWADM,SUNADM");
        when(PreferenceCache.getSystemPreferenceValue("TRANSFER_RULE_ALLOWED_CATEGORIES")).thenReturn("NWADM,SUNADM");
        when(PreferenceCache.getSystemPreferenceValue("VOUCH_GEN_BATCH_SIZE")).thenReturn(200);
        when(PreferenceCache.getSystemPreferenceValue("AUTOCOMPLETE_USER_DETAILS_COUNT")).thenReturn(50);
        when(PreferenceCache.getSystemPreferenceValue("VOUCH_GEN_RETRY_COUNT")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("IS_SECURITY_COMMON_VALIDATION_REQUIRED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("RESOURCE_UTIL_ONLINE_VOMS_GEN_THRESHOLD_LIMIT")).thenReturn(100);
        when(PreferenceCache.getSystemPreferenceValue("ALPHANUM_SPCL_REGEX")).thenReturn("[a-zA-Z\\d]+?");
        when(PreferenceCache.getSystemPreferenceValue("AVAILABLE_SOURCE_TYPE")).thenReturn("XML,PLAIN,JSON,WEB");
        when(PreferenceCache.getSystemPreferenceValue("SUCC_BLOCK_TIME_O2C")).thenReturn(0);
        when(PreferenceCache.getSystemPreferenceValue("SUCC_BLOCK_TIME_C2C")).thenReturn(0);
        when(PreferenceCache.getSystemPreferenceValue("MRP_BLOCK_TIME_ALLOWED_CHNL")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("MRP_BLOCK_TIMEOUT_SERVICES_GATEWAY_CHNL")).thenReturn("O2C,C2C");
        when(PreferenceCache.getSystemPreferenceValue("OTP_ON_SMS")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("MAX_INVALID_OTP")).thenReturn(3);
        when(PreferenceCache.getSystemPreferenceValue("BLOCK_TIME_INVALID_OTP")).thenReturn(60);
        when(PreferenceCache.getSystemPreferenceValue("MIN_LENGTH_TO_AUTOCOMPLETE")).thenReturn(3);
        when(PreferenceCache.getSystemPreferenceValue("OAUTH_TOKEN_TIME_TO_LIVE")).thenReturn(60);
        when(PreferenceCache.getSystemPreferenceValue("PAYMENTDETAILSMANDATEVOUCHER_C2C")).thenReturn(0);
        when(PreferenceCache.getSystemPreferenceValue("REPORT_MAX_DATEDIFF_ADMIN_CONS")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("REPORT_MAX_DATEDIFF_USER_CONS")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("REPORT_MAX_DATEDIFF_USER_AVAIL")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("REPORT_MAX_DATEDIFF_ADMIN_AVAIL")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("REPORT_MAX_DATEDIFF_ADMIN_NLEVEL")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("USER_ALLOWED_VINFO")).thenReturn("SUBCU,BCU,DIST,SE,SUADM,SUNADM,NWADM,SSADM,AG,RET");
        when(PreferenceCache.getSystemPreferenceValue("MAX_LOGINS_LOCATION")).thenReturn(10000);
        when(PreferenceCache.getSystemPreferenceValue("MAX_LOGINS_TYPE")).thenReturn(5000);
        when(PreferenceCache.getSystemPreferenceValue("MIN_LAST_DAYS_CG")).thenReturn(1);
        when(PreferenceCache.getSystemPreferenceValue("MAX_LAST_DAYS_CG")).thenReturn(365);
        when(PreferenceCache.getSystemPreferenceValue("BYPASS_EVD_KANEL_MES_STAT")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("LANGS_SUPT_ENCODING")).thenReturn("ar,ku,ku1,ru,fa");
        when(PreferenceCache.getSystemPreferenceValue("IS_ONE_TIME_SID")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("TOKEN_EXPIRE_TIME")).thenReturn(6000);
        when(PreferenceCache.getSystemPreferenceValue("REFRESH_TOKEN_EXPIRE_TIME")).thenReturn(6000);
        when(PreferenceCache.getSystemPreferenceValue("VMS_UI_DISPLAY_DATE_TIME")).thenReturn("dd/MM/yy hh:mm:ss aa");
        when(PreferenceCache.getSystemPreferenceValue("VOUCHER_UPLOAD_FILE_FORMATS")).thenReturn("xls,xlsx,csv");
        when(PreferenceCache.getSystemPreferenceValue("TOP_N_PRODUCT_VALUE")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("REPORT_DATE_DURATION")).thenReturn(30);
        when(PreferenceCache.getSystemPreferenceValue("AvgSaleNoOfDays")).thenReturn(15);
        when(PreferenceCache.getSystemPreferenceValue("ElectronicStockDaysCalculatedOn")).thenReturn("EN");
        when(PreferenceCache.getSystemPreferenceValue("VOUCHER_DTL_LOW_BALANCE")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("DIGITAL_RECHARGE_VOUCHER_TYPE")).thenReturn("digital,digital1,test_digit");
        when(PreferenceCache.getSystemPreferenceValue("FORMAT_DATE_MONTH")).thenReturn("dd/MM");
        when(PreferenceCache.getSystemPreferenceValue("IS_CELLID_REQUIRED_FROM_IN")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("IS_POSITIVE_COMM_DEBIT_FROM_SENDER_REQ")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("MAX_RECORDS_SIZE_VAL")).thenReturn("999999999999");
        when(PreferenceCache.getSystemPreferenceValue("LOG_OUT_TIME")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("FORCE_LOGOUT")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("FILE_IS_NOT_EXIST")).thenReturn("{0} does not exist.");
        when(PreferenceCache.getSystemPreferenceValue("VMS_VOUCHER_TEMPLATE_TYPE")).thenReturn("XLSX");
        when(PreferenceCache.getSystemPreferenceValue("BURN_RATE_VOUCHER_TYPES")).thenReturn("P,E");
        when(PreferenceCache.getSystemPreferenceValue("MAX_LOGINS_TYPE_NG_DIST")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("MAX_LOGINS_TYPE_NG_SSADM")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_SERIAL_NO_MAX_LENGTH")).thenReturn(16);
        when(PreferenceCache.getSystemPreferenceValue("RIGHT_CLICK_ENABLE")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("REPORT_ONLINE_OR_OFFLINE")).thenReturn("OFFLINE");
        when(PreferenceCache.getSystemPreferenceValue("UI_COPY_CONTENT_ENABLE")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("ALLOW_SAME_REPORT_EXEC")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("TOT_RPT_EXEC_PERUSER")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("CGTAX34APP")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("REPORT_OFFLINE")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("SUBCRBR_PRFX_ROUTNG_ALWD")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("CHARSET_ENCODING")).thenReturn("UTF-16");
        when(PreferenceCache.getSystemPreferenceValue("DEF_CHNL_TRANSFER_ALLOWED")).thenReturn("C2CVOMSTRFINI,TRFINI");
        when(PreferenceCache.getSystemPreferenceValue("ALIAS_TO_BE_ENCRYPTED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("LOAD_BAL_IP_ALLOWED")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("FILE_UPLOAD_MAX_SIZE")).thenReturn(9999999);
        when(PreferenceCache.getSystemPreferenceValue("SYSTEM_IDLE_TIME")).thenReturn(900);
        when(PreferenceCache.getSystemPreferenceValue("MAX_FILE_SIZE_FOR_VMSSIGNED_DOC")).thenReturn("102400000");
        when(PreferenceCache.getSystemPreferenceValue("VMS_MAX_BATCHES_ALLOWED")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("UPLOAD_FILE_APPRV_LEVEL")).thenReturn(3);
        when(PreferenceCache.getSystemPreferenceValue("OPERATOR_UTIL_VMS")).thenReturn("OperatorUtil");
        when(PreferenceCache.getSystemPreferenceValue("VOU_UPLOAD_ERROR_DISPLAY")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("VMS_CHECK_SUM_ENABLE")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_GEN_BATCH_COUNT")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("VMS_APPROVAL_REQ")).thenReturn("N");
        when(PreferenceCache.getSystemPreferenceValue("DEFAULT_PRODUCT_CODE")).thenReturn("ETOPUP");
        when(PreferenceCache.getSystemPreferenceValue("MAX_BULK_FILE_SIZE_BYTES")).thenReturn("10200000");
        when(PreferenceCache.getSystemPreferenceValue("DW_ALLOWED_GATEWAYS")).thenReturn("EXTGW,DWEXTGW");
        when(PreferenceCache.getSystemPreferenceValue("SAP_ALLOWED")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("AUTO_SEARCH")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("C2C_BATCH_FILEEXT")).thenReturn("xls");
        when(PreferenceCache.getSystemPreferenceValue("ERROR_FILE_C2C")).thenReturn("5");
        when(PreferenceCache.getSystemPreferenceValue("VOMS_ORDER_SLAB_LENGTH")).thenReturn(4);
        when(PreferenceCache.getSystemPreferenceValue("VOMS_MIN_ALT_VALUE")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("COM_PAY_OUT")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("USER_ALLOW_CONTENT_TYPE")).thenReturn("xlsx");
        when(PreferenceCache.getSystemPreferenceValue("OFFLINERPT_DOWNLD_PATH")).thenReturn("/data1/pretupsapp/offlineDownloadFile/test2downloads/");
        when(PreferenceCache.getSystemPreferenceValue("IS_VOU_DEN_PROFILE_ZERO_ALLOW")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("NET_PREFIX_TO_VALIDATED_FOR_BAR_UNBAR")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("CHADM_CROSS_ALLOW")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("USR_BTCH_SUS_DEL_APRVL")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("ADMINISTRBLY_USER_STATUS_CHANG_NEW")).thenReturn("DE:Y,DE:CH,DE:EX,DE:N,EX:Y,EX:CH,CH:Y,Y:EX,PA:DE,Y:S,S:RE,Y:BR");
        when(PreferenceCache.getSystemPreferenceValue("ADD_INFO_REQUIRED_FOR_VOUCHER")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("IS_TRF_RULE_USER_LEVEL_ALLOW")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("USERWISE_LOAN_ENABLE")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("EXTREFNUM_MANDATORY_GATEWAYS")).thenReturn("ext");
        when(PreferenceCache.getSystemPreferenceValue("TEMP_PIN_EXPIRY_DURATION")).thenReturn(24);
        when(PreferenceCache.getSystemPreferenceValue("IMEI_OPTIONAL")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("CSRF_ENABLE")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("DIRECT_VOUCHER_ENABLE")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("OTP_VALIDITY_TIME")).thenReturn(500);
        when(PreferenceCache.getSystemPreferenceValue("USER_EXTERNAL_CODE_DOMAINWISE")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("TARGET_COMMISSION_SLABS")).thenReturn(5);
        when(PreferenceCache.getSystemPreferenceValue("COMMISSION_SLABS")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("COMMISSION_BASE_SLAB")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("COMMISSION_ADD_SLAB")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("CHANNEL_USER_STATUS_CHANGE_BATCH_FILEEXT")).thenReturn("xls");
        when(PreferenceCache.getSystemPreferenceValue("OTP_REQUIRED")).thenReturn("ONETIME");
        when(PreferenceCache.getSystemPreferenceValue("CHANGE_NETWORK")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("SNDR_NETWORK_IDENTIFY_ON_IMSI_BASIS")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("MAX_CHARS_FOR_SEARCH")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("ALLOW_TRANSACTION_IF_LOAN_SETTLEMENT_FAIL")).thenReturn("RETURN, RCREV");
        when(PreferenceCache.getSystemPreferenceValue("VOUCHER_SHOW")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("ALLOW_GATEWAYCODE_FOR_LOAN_SETTLEMENT")).thenReturn("USSD,SMSC");
        when(PreferenceCache.getSystemPreferenceValue("RESTRICT_TRANSACTION_FOR_LOAN_SETTLEMENT")).thenReturn("FOC");
        when(PreferenceCache.getSystemPreferenceValue("LOAN_PROFILE_SLAB_LENGTH")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("BLOCK_GATEWAYCODE_FOR_LOAN_SETTLEMENT")).thenReturn("WEB,GSTREXTGW,EXTGW");
        when(PreferenceCache.getSystemPreferenceValue("CAT_USERWISE_LOAN_ENABLE")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("CATEGORIES_LIFECYCLECHANGE")).thenReturn("RETA,SA");
        when(PreferenceCache.getSystemPreferenceValue("VOUCHER")).thenReturn(false);
        when(PreferenceCache.getSystemPreferenceValue("VOUCHER_EXP_EMAIL_NOTIFICATION")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("VOUCHREXPIRYUP_ONLINECOUNT")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("VOUCHER_EXP_SMS_NOTIFICATION")).thenReturn(true);
        when(PreferenceCache.getSystemPreferenceValue("ADD_C2S_MAX")).thenReturn(10);
        when(PreferenceCache.getSystemPreferenceValue("RANGE_BASED_ADDNL_COMMISSION_SLABS")).thenReturn(2);
        when(PreferenceCache.getSystemPreferenceValue("RANGE_BASED_BASE_COMMISSION_SLABS")).thenReturn(2);

    }

    public static void initialize4() {


        when(PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,"String")).thenReturn(true);
        when(PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,"String")).thenReturn(true);
        when(PreferenceCache.getNetworkPrefrencesValue(PreferenceI.RVERSE_TRN_EXPIRY,"String")).thenReturn(7);


        when(PreferenceCache.getNetworkPrefrencesValue(PreferenceI.REALTIME_OTF_MSGS,"String")).thenReturn(false);
        when(PreferenceCache.getNetworkPrefrencesValue(PreferenceI.NETWORK_STOCK_CIRCLE_MAXLIMIT,"String")).thenReturn(100);
        when(PreferenceCache.getNetworkPrefrencesValue(PreferenceI.NETWORK_STOCK_FIRSTAPPLIMIT,"String")).thenReturn(50);




        when(PreferenceCache.getControlPreference(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(125);


        when(PreferenceCache.getControlPreference("C2S_DEFAULT_SMSPIN", "String", "String")).thenReturn("0000");
        when(PreferenceCache.getControlPreference("CREPT_MAX_DATEDIFF", "String", "String")).thenReturn("60");
        when(PreferenceCache.getControlPreference("MAX_DATEDIFF", "String", "String")).thenReturn("30");
        when(PreferenceCache.getControlPreference("REPORT_MAX_DATEDIFF", "String", "String")).thenReturn("20");
        when(PreferenceCache.getControlPreference("C2S_DEFAULT_PASSWORD", "String", "String")).thenReturn("0000");
        when(PreferenceCache.getControlPreference("P2P_DEFAULT_SMSPIN", "String", "String")).thenReturn("0000");
        when(PreferenceCache.getControlPreference("SYSTEM_DATE_FORMAT", "String", "String")).thenReturn("dd/MM/yy");
        when(PreferenceCache.getControlPreference("PIN_PWD_ALERT_DYS", "String", "String")).thenReturn("365");
        when(PreferenceCache.getControlPreference("PWD_CHANGE_NOT_REQ", "String", "String")).thenReturn("CCE");
        when(PreferenceCache.getControlPreference("EXTERNAL_TXN_UNIQUE", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("RC_NBK_AL_DAYS_DIF", "String", "String")).thenReturn("10");

        when(PreferenceCache.getControlPreference("STAFF_USER_COUNT", "String", "String")).thenReturn(25);



        when(PreferenceCache.getControlPreference("DCT_VOUCHER_EN", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("PRO_TRF_ST_LVL_CODE", "String", "String")).thenReturn("1");
        when(PreferenceCache.getControlPreference("C2S_PROMO_TRF_APP", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("MNP_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("PORT_USR_SUSPEND_REQ", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("VOMS_SNO_MIN_LENGTH", "String", "String")).thenReturn("9");
        when(PreferenceCache.getControlPreference("VOMS_SNO_MAX_LENGTH", "String", "String")).thenReturn("16");
        when(PreferenceCache.getControlPreference("VOMS_PIN_MIN_LENGTH", "String", "String")).thenReturn("9");
        when(PreferenceCache.getControlPreference("VOMS_PIN_MAX_LENGTH", "String", "String")).thenReturn("14");
        when(PreferenceCache.getControlPreference("LOW_BAL_MSGGATEWAY", "String", "String")).thenReturn("LOWBALGW");
        when(PreferenceCache.getControlPreference("RC_NBK_DIF_RQ_TO_IN", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("RC_AL_AC_STATUS_NBK", "String", "String")).thenReturn("DEACT,SUS");
        when(PreferenceCache.getControlPreference("RC_NBK_AMT_DEDCTED", "String", "String")).thenReturn("2000");
        when(PreferenceCache.getControlPreference("PRC_AL_AC_STATUS_NBK", "String", "String")).thenReturn("Active");
        when(PreferenceCache.getControlPreference("PRC_NBK_DIF_RQ_TO_IN", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("PRC_NBK_AL_DAYS_DIF", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("PRC_NBK_AMT_DEDCTED", "String", "String")).thenReturn("2000");
        when(PreferenceCache.getControlPreference("AUTO_PAYMENT_METHOD", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("PTUPS_MOBQUTY_MERGD", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("ICCID_CHECKSTRING", "String", "String")).thenReturn("9862");
        when(PreferenceCache.getControlPreference("P2P_ALLOW_SELF_TOPUP", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("PIN_SEND_TO", "String", "String")).thenReturn("C");
        when(PreferenceCache.getControlPreference("GRPT_CHRG_ALLOWED", "String", "String")).thenReturn("SMSC,USSD");
        when(PreferenceCache.getControlPreference("GRPT_CTRL_ALLOWED", "String", "String")).thenReturn("SMSC,USSD");
        when(PreferenceCache.getControlPreference("GRPT_CONTROL_LEVEL", "String", "String")).thenReturn("M");
        when(PreferenceCache.getControlPreference("C2S_DEF_SEL_BILLPAY", "String", "String")).thenReturn("C");
        when(PreferenceCache.getControlPreference("EXT_CODE_MAND_FOC", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("SECOND_LANG_CHARSET", "String", "String")).thenReturn("ISO-8859-15");
        when(PreferenceCache.getControlPreference("C2C_RET_PARENT_ONLY", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("MAX_BUDDIES_ALLOWED", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("DECIML_ALOW_SERVICES", "String", "String")).thenReturn("RC,GRC,PPB,EVD,INTRRC,PSTNRC");
        when(PreferenceCache.getControlPreference("EXTERNAL_DATE_FORMAT", "String", "String")).thenReturn("dd/MM/yy");
        when(PreferenceCache.getControlPreference("C2S_ALLOW_SELF_UTILI", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("C2S_ALOW_SLF_UTLTBIL", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("C2S_S_CRBKAM_UTLTBIL", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("NOTIFY_SRVCCLS_REC", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("MIN_ID_NUM_LNTH", "String", "String")).thenReturn("8");
        when(PreferenceCache.getControlPreference("MAX_ID_NUM_LNTH", "String", "String")).thenReturn("12");
        when(PreferenceCache.getControlPreference("XML_MAX_RCD_SUM_RESP", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("XML_DATE_RANGE", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("XML_DFT_DATE_RANGE", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("USE_HOME_STOCK", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("OTH_ID_PREFIX_LIST", "String", "String")).thenReturn("");
        when(PreferenceCache.getControlPreference("ALPHANUM_ID_NUM_ALWD", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("ID_NUM_VAL_TYPE", "String", "String")).thenReturn("M");
        when(PreferenceCache.getControlPreference("USE_DISPLAY_AMT", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("MAX_DENOMINATION_VAL", "String", "String")).thenReturn("100000");
        when(PreferenceCache.getControlPreference("VOMS_MAXERRORCOUNTEN", "String", "String")).thenReturn("1000000");
        when(PreferenceCache.getControlPreference("VOMS_MAXERRORCOUNTOT", "String", "String")).thenReturn("1000000");
        when(PreferenceCache.getControlPreference("VOMS_SERIALNO_LENGTH", "String", "String")).thenReturn("16");
        when(PreferenceCache.getControlPreference("VOMS_UPEXPHOURS", "String", "String")).thenReturn("4");
        when(PreferenceCache.getControlPreference("VOMS_OFFPEAKHRS", "String", "String")).thenReturn("0-24");
        when(PreferenceCache.getControlPreference("VOMS_MAX_VOUCHER_EN", "String", "String")).thenReturn("1000000");
        when(PreferenceCache.getControlPreference("VOMS_MAX_VOUCHER_OT", "String", "String")).thenReturn("1000000");
        when(PreferenceCache.getControlPreference("VOMS_MAXBATCHDY", "String", "String")).thenReturn("3");
        when(PreferenceCache.getControlPreference("VOMS_DATE_FORMAT", "String", "String")).thenReturn("dd/MM/yy");
        when(PreferenceCache.getControlPreference("PEERTRFMINLMT", "String", "String")).thenReturn("500");
        when(PreferenceCache.getControlPreference("PEERTRFMAXLMT", "String", "String")).thenReturn("1500000000");
        when(PreferenceCache.getControlPreference("MAX_PER_TRANSFER", "String", "String")).thenReturn("90");
        when(PreferenceCache.getControlPreference("DAY_SDR_MX_TRANS_AMT", "String", "String")).thenReturn("100000");
        when(PreferenceCache.getControlPreference("DAY_SDR_MX_TRANS_NUM", "String", "String")).thenReturn("3");
        when(PreferenceCache.getControlPreference("MIN_RES_BALTYPE", "String", "String")).thenReturn("AMT");
        when(PreferenceCache.getControlPreference("MIN_RESIDUAL_BAL", "String", "String")).thenReturn("10000");
        when(PreferenceCache.getControlPreference("C2S_MAX_PIN_BLK_CONT", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("MIN_VALIDITY_DAYS", "String", "String")).thenReturn("400");
        when(PreferenceCache.getControlPreference("SUCC_BLOCK_TIME", "String", "String")).thenReturn("0");
        when(PreferenceCache.getControlPreference("DA_CONFAIL_COUNT", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("DA_SUCTRAN_ALLWDCOUN", "String", "String")).thenReturn("300");
        when(PreferenceCache.getControlPreference("DA_FAIL_TXN_ALLWDCOU", "String", "String")).thenReturn("40");
        when(PreferenceCache.getControlPreference("DA_TOTXN_AMT_ALLWDCO", "String", "String")).thenReturn("300000000");
        when(PreferenceCache.getControlPreference("TAX2_ON_TAX1", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("MIN_MSISDN_LENGTH", "String", "String")).thenReturn("6");
        when(PreferenceCache.getControlPreference("SCC_BLCK_TIME_P2P", "String", "String")).thenReturn("60");
        when(PreferenceCache.getControlPreference("DA_REC_AMT_ALLWD_P2P", "String", "String")).thenReturn("1500000");
        when(PreferenceCache.getControlPreference("DA_SUCTRAN_ALLWD_P2P", "String", "String")).thenReturn("545454");
        when(PreferenceCache.getControlPreference("DEF_FRCXML_SEL_P2P", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("DEF_FRCXML_SEL_C2S", "String", "String")).thenReturn("");
        when(PreferenceCache.getControlPreference("GROUP_ROLE_ALLOWED", "String", "String")).thenReturn("Y");
        when(PreferenceCache.getControlPreference("CHNL_PLAIN_SMS_SEPT", "String", "String")).thenReturn(" ");
        when(PreferenceCache.getControlPreference("FOC_ODR_APPROVAL_LVL", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("P2P_ENQ_BAL_HIDE", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("SMS_TO_LOGIN_USER", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("DEFAULT_MESSGATEWAY", "String", "String")).thenReturn("SMSC");
        when(PreferenceCache.getControlPreference("C2S_MIN_PIN_LENGTH", "String", "String")).thenReturn("4");
        when(PreferenceCache.getControlPreference("SEP_TRF_CTRL", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("C2S_SEP_TRFR_COUNT", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("USER_CODE_REQUIRED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("C2S_PIN_BLK_RST_DRTN", "String", "String")).thenReturn("1440");
        when(PreferenceCache.getControlPreference("C2S_DYS_ATR_CNGE_PIN", "String", "String")).thenReturn("365");
        when(PreferenceCache.getControlPreference("P2P_DYS_ATR_CNGE_PIN", "String", "String")).thenReturn("365");
        when(PreferenceCache.getControlPreference("MAX_MSISDN_TEXTBOX", "String", "String")).thenReturn("15");
        when(PreferenceCache.getControlPreference("MSISDN_PREFIX_LENGTH", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("AMOUNT_MULT_FACTOR", "String", "String")).thenReturn("100");
        when(PreferenceCache.getControlPreference("PIN_REQUIRED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("PIN_LENGTH", "String", "String")).thenReturn("4");
        when(PreferenceCache.getControlPreference("DEFAULT_LANGUAGE", "String", "String")).thenReturn("en");
        when(PreferenceCache.getControlPreference("SKEY_REQUIRED", "String", "String")).thenReturn("N");
        when(PreferenceCache.getControlPreference("MON_SDR_MX_TRANS_NUM", "String", "String")).thenReturn("999999999");
        when(PreferenceCache.getControlPreference("MON_SDR_MX_TRANS_AMT", "String", "String")).thenReturn("999999999");
        when(PreferenceCache.getControlPreference("WK_SDR_MX_TRANS_AMT", "String", "String")).thenReturn("99999999");
        when(PreferenceCache.getControlPreference("WK_SDR_MX_TRANS_NUM", "String", "String")).thenReturn("5000000");
        when(PreferenceCache.getControlPreference("P2P_PIN_BLK_RST_DRTN", "String", "String")).thenReturn("1440");
        when(PreferenceCache.getControlPreference("P2P_MAX_PIN_BLK_CONT", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("MVD_MAX_VOUCHER", "String", "String")).thenReturn("10000");
        when(PreferenceCache.getControlPreference("SYSTEM_DTTIME_FORMAT", "String", "String")).thenReturn("dd/MM/yy HH:mm:ss");
        when(PreferenceCache.getControlPreference("C2S_AMB_CR_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("FINANCIAL_YEAR_START", "String", "String")).thenReturn("0");
        when(PreferenceCache.getControlPreference("EXTERNAL_TXN_NUMERIC", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("CHK_BLK_LST_STAT", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("EXTTXNMANDT_DOMAINTP", "String", "String")).thenReturn("DISTB_CHAN,CORPORATE");
        when(PreferenceCache.getControlPreference("EXTTXNMANDT_FOC", "String", "String")).thenReturn("4");
        when(PreferenceCache.getControlPreference("NOTIFI_SRVCCLS_REC", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("NOTIF_SRVCCLS_RECC2S", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("NOTIFI_SRVCCLS_SEN", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("NOTIFSRVCLS_REC_BLPY", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("SECOND_LANG_ENCODING", "String", "String")).thenReturn("ISO-8859-15");
        when(PreferenceCache.getControlPreference("ALLOW_SELF_BILLPAY", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("C2S_AMB_CR_ALOW_PPBP", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("SEP_OUTSIDE_TXN_CTRL", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("DEF_FRCXML_SEL_BLPY", "String", "String")).thenReturn("");
        when(PreferenceCache.getControlPreference("PP_DEF_STATUS_ACT", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("SPACE_ALLOW_IN_LOGIN", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("DEF_WEB_GW_CODE", "String", "String")).thenReturn("WEB");
        when(PreferenceCache.getControlPreference("MSISDN_PREFIX_LIST", "String", "String")).thenReturn("236,+236,0236");
        when(PreferenceCache.getControlPreference("DYS_AFTER_CHANGE_PWD", "String", "String")).thenReturn("365");
        when(PreferenceCache.getControlPreference("REQ_CUSER_DLT_APP", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("ALWD_SERVICES_NUMBCK", "String", "String")).thenReturn("RC,PRC");
        when(PreferenceCache.getControlPreference("EXTTXNLEVEL", "String", "String")).thenReturn("1,2");
        when(PreferenceCache.getControlPreference("EXTTXNMANDT", "String", "String")).thenReturn("1,2");
        when(PreferenceCache.getControlPreference("USE_PPAID_CONTROLS", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("C2S_PLAIN_SMS_ALLWD", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("LANGAUGES_SUPPORTED", "String", "String")).thenReturn("0:en#US,1:fr#NG");
        when(PreferenceCache.getControlPreference("DEFAULT_COUNTRY", "String", "String")).thenReturn("US");
        when(PreferenceCache.getControlPreference("C2S_USER_REGTN_REQ", "String", "String")).thenReturn("Y");
        when(PreferenceCache.getControlPreference("ALLOW_SELF_TOPUP", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("OPERATOR_UTIL_C", "String", "String")).thenReturn("com.btsl.pretups.util.OperatorUtil");
        when(PreferenceCache.getControlPreference("USRLEVELAPPROVAL", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("PROD_REQ", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("TR_ID_REQ", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("LG_MN_REQ", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("DEF_PROD", "String", "String")).thenReturn("ETOPUP");
        when(PreferenceCache.getControlPreference("SMS_P_INDX", "String", "String")).thenReturn("03");
        when(PreferenceCache.getControlPreference("ERROR_FOR_FAIL_CT", "String", "String")).thenReturn("76711");
        when(PreferenceCache.getControlPreference("FRSTAPPLM", "String", "String")).thenReturn("10000");
        when(PreferenceCache.getControlPreference("MAXTRNSFR", "String", "String")).thenReturn("5000000");
        when(PreferenceCache.getControlPreference("MINTRNSFR", "String", "String")).thenReturn("1000");
        when(PreferenceCache.getControlPreference("PEERTRNSFR", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("SKEYEXPIRYSEC", "String", "String")).thenReturn("300");
        when(PreferenceCache.getControlPreference("CIRCLEMAXLMT", "String", "String")).thenReturn("5000000000");
        when(PreferenceCache.getControlPreference("SKEYLENGTH", "String", "String")).thenReturn("4");
        when(PreferenceCache.getControlPreference("CIRCLEMINLMT", "String", "String")).thenReturn("100");
        when(PreferenceCache.getControlPreference("P2P_AMB_CR_ALLOWED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("P2P_CR_BACK_ALLOWED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("MAX_PWD_BLOCK_COUNT", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("PWD_BLK_RST_DURATION", "String", "String")).thenReturn("24");
        when(PreferenceCache.getControlPreference("TRSFR_DEF_SRVCTYPE", "String", "String")).thenReturn("PRE");
        when(PreferenceCache.getControlPreference("USER_HIERARCHY_SIZE", "String", "String")).thenReturn("50");
        when(PreferenceCache.getControlPreference("DEFAULT_PRODUCT", "String", "String")).thenReturn("101");
        when(PreferenceCache.getControlPreference("C2S_MAX_PIN_LENGTH", "String", "String")).thenReturn("4");
        when(PreferenceCache.getControlPreference("MAX_LOGIN_PWD_LENGTH", "String", "String")).thenReturn("8");
        when(PreferenceCache.getControlPreference("MIN_LOGIN_PWD_LENGTH", "String", "String")).thenReturn(6);
        when(PreferenceCache.getControlPreference("MAX_SMS_PIN_LENGTH", "String", "String")).thenReturn("4");
        when(PreferenceCache.getControlPreference("MAX_MSISDN_LENGTH", "String", "String")).thenReturn("15");
        when(PreferenceCache.getControlPreference("P2P_PLAIN_SMS_SEPT", "String", "String")).thenReturn("");
        when(PreferenceCache.getControlPreference("MIN_SMS_PIN_LENGTH", "String", "String")).thenReturn("4");
        when(PreferenceCache.getControlPreference("USER_EVENT_REMARKS", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("MULT_CRE_TRA_DED_ACC_SEP", "String", "String")).thenReturn(",");
        when(PreferenceCache.getControlPreference("APPROVER_CAN_EDIT", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("VAL_DAYS_TO_CHK_VLUP", "String", "String")).thenReturn("31");
        when(PreferenceCache.getControlPreference("C2C_SMS_NOTIFY", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("NEG_ADD_COMM_APPLY", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("DISSABLE_BUTTON_LIST", "String", "String")).thenReturn("NO_BUTTON");
        when(PreferenceCache.getControlPreference("SMS_MMS_ALLOWED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("ALLOW_SELF_TOPUP_FRC", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("STK_REG_ICCID", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("ACT_BONUS_REDEM_DUR", "String", "String")).thenReturn("15");
        when(PreferenceCache.getControlPreference("ACT_BONUS_MIN_AMOUNT", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("VOLUME_CALC_ALLOWED", "String", "String")).thenReturn("N");
        when(PreferenceCache.getControlPreference("POSITIVE_COMM_APPLY", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("CHECK_REC_TXN_AT_IAT", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("IS_IAT_RUNNING", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("EXTTXNMANDT_DP", "String", "String")).thenReturn("4");
        when(PreferenceCache.getControlPreference("DP_ODR_APPROVAL_LVL", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("EXT_CODE_MAND_DP", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("DP_SMS_NOTIFY", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("DP_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("LAST_X_TRF_DAYS_NO", "String", "String")).thenReturn("1");
        when(PreferenceCache.getControlPreference("SHA2_FAMILY_TYPE", "String", "String")).thenReturn("SHA-256");
        when(PreferenceCache.getControlPreference("HTTPS_ENABLE", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("NWADM_CROSS_ALLOW", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("STAFF_AS_USER", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("RVERSE_TRN_EXPIRY", "String", "String")).thenReturn("7");
        when(PreferenceCache.getControlPreference("RVE_C2S_TRN_EXPIRY", "String", "String")).thenReturn("999999");
        when(PreferenceCache.getControlPreference("PRVT_RCHRG_ALLOW", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("MVD_MIN_VOUCHER", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("NAMEEMBOSS_SEPT", "String", "String")).thenReturn("_");
        when(PreferenceCache.getControlPreference("VOMS_MIN_EXPIRY_DAYS", "String", "String")).thenReturn("1");
        when(PreferenceCache.getControlPreference("ALLOW_SELF_TOPUP_BRC", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("CARD_GROUP_BONUS_RANGE", "String", "String")).thenReturn("500");
        when(PreferenceCache.getControlPreference("LOGIN_SPCL_CHAR_ALLOW", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("VOMS_PIN_ENCRIPT_ALLOWED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("FEE_VALDAYS_TO_EXT", "String", "String")).thenReturn("800,30");
        when(PreferenceCache.getControlPreference("IS_CT_WITH_VAL_UPDN", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("ALLOW_SELF_EVR", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("C2S_PWD_BLK_EXP_DRN", "String", "String")).thenReturn("999999999999");
        when(PreferenceCache.getControlPreference("CP2P_PIN_BLK_EXP_DRN", "String", "String")).thenReturn("3");
        when(PreferenceCache.getControlPreference("C2S_PIN_BLK_EXP_DRN", "String", "String")).thenReturn("999999999999");
        when(PreferenceCache.getControlPreference("IS_SEPARATE_RPT_DB", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("DISABLE_SEND_PIN_BTN", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("ZERO_BAL_THRESHOLD_VALUE", "String", "String")).thenReturn("0");
        when(PreferenceCache.getControlPreference("IS_SEP_BONUS_REQUIRED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("DISABLE_UNBLOCK_PASWD_BTN", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("MESSAGE_TO_PRIMARY_REQUIRED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("SECONDARY_NUMBER_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("P2P_REG_EXPIRY_PERIOD", "String", "String")).thenReturn("999999999");
        when(PreferenceCache.getControlPreference("C2S_RANDOM_PIN_GENERATE", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("WEB_RANDOM_PWD_GENERATE", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("BATCH_USER_PASSWD_MODIFY_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("C2C_BATCH_APPROVAL_LVL", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("RESET_PWD_EXP_TIME_IN_HOURS", "String", "String")).thenReturn("999999999");
        when(PreferenceCache.getControlPreference("RESET_PIN_EXP_TIME_IN_HOURS", "String", "String")).thenReturn("999999999");
        when(PreferenceCache.getControlPreference("C2S_CARD_GROUP_SLAB_COPY", "String", "String")).thenReturn("N");
        when(PreferenceCache.getControlPreference("AUTO_PIN_GENERATE_ALLOW", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("AUTO_PWD_GENERATE_ALLOW", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("PSWD_EXP_TIME_IN_HOUR_ON_CREATION", "String", "String")).thenReturn("999999999");
        when(PreferenceCache.getControlPreference("POINT_CONVERSION_FACTOR", "String", "String")).thenReturn("1");
        when(PreferenceCache.getControlPreference("MULTIPLE_WALLET_APPLY", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("EXTTXNMANDT_DOMAINTP_DP", "String", "String")).thenReturn("DISTB_CHAN,CORPORATE,DEALER,COMP_SHOP");
        when(PreferenceCache.getControlPreference("CHNL_PLAIN_SMS_SEPT_LOGINID", "String", "String")).thenReturn("#");
        when(PreferenceCache.getControlPreference("IS_DEFAULT_PROFILE", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("O2C_EMAIL_NOTIFICATION", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("SID_ISNUMERIC", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("SMS_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("AUTO_C2C_TRANSFER_AMT", "String", "String")).thenReturn("10000");
        when(PreferenceCache.getControlPreference("MIN_SID_LENGTH", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("MAX_SID_LENGTH", "String", "String")).thenReturn("15");
        when(PreferenceCache.getControlPreference("MVD_MAX_VOUCHER_EXTGW", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("RPTSUMM_MAX_DATEDIFF", "String", "String")).thenReturn("15");
        when(PreferenceCache.getControlPreference("C2S_RECHARGE_MULTIPLE_ENTRY", "String", "String")).thenReturn("S");
        when(PreferenceCache.getControlPreference("LAST_X_TRANSFER_STATUS", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("PINPAS_EN_DE_CRYPTION_TYPE", "String", "String")).thenReturn("DES");
        when(PreferenceCache.getControlPreference("P2P_CARD_GROUP_SLAB_COPY", "String", "String")).thenReturn("N");
        when(PreferenceCache.getControlPreference("SERVICE_FOR_LAST_X_TRANSFER", "String", "String")).thenReturn("C2S,O2C,C2C");
        when(PreferenceCache.getControlPreference("LAST_X_CUSTENQ_STATUS", "String", "String")).thenReturn("1");
        when(PreferenceCache.getControlPreference("LAST_TRF_MULTIPLE_SMS", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("DLRY_RCPT_TRK", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("CR_BK_ALW_EVD_AMB", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("SLF_EVD_ALWD", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("SR_WISE_MSG_EVD", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("BUDDY_PIN_REQ", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("FOC_SMS_NOTIFY", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("PRF_ASSOCIATE_AGENT", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("C2S_ENQ_BAL_HIDE", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("MAX_ALLD_BALANCE_C2S", "String", "String")).thenReturn("1000000000");
        when(PreferenceCache.getControlPreference("MO_TOTXN_AMT_ALLWDCO", "String", "String")).thenReturn("3000000000");
        when(PreferenceCache.getControlPreference("MO_SUCTRAN_ALLWDCOUN", "String", "String")).thenReturn("5000");
        when(PreferenceCache.getControlPreference("WE_TOTXN_AMT_ALLWDCO", "String", "String")).thenReturn("1000000000");
        when(PreferenceCache.getControlPreference("WE_SUCTRAN_ALLWDCOUN", "String", "String")).thenReturn("30000");
        when(PreferenceCache.getControlPreference("MAX_ALLD_BALANCE_P2P", "String", "String")).thenReturn("50000000");
        when(PreferenceCache.getControlPreference("MO_SUCTRAN_ALLWD_P2P", "String", "String")).thenReturn("1000");
        when(PreferenceCache.getControlPreference("MO_REC_AMT_ALLWD_P2P", "String", "String")).thenReturn("5000000");
        when(PreferenceCache.getControlPreference("WE_SUCTRAN_ALLWD_P2P", "String", "String")).thenReturn("700");
        when(PreferenceCache.getControlPreference("WE_REC_AMT_ALLWD_P2P", "String", "String")).thenReturn("1250000");
        when(PreferenceCache.getControlPreference("C2S_REF_NUM_REQ", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("C2S_REF_NUM_UNIQUE", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("PAYAMT_MRP_SAME", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("PRV_PASS_NOT_ALLOW", "String", "String")).thenReturn("3");
        when(PreferenceCache.getControlPreference("PRV_PIN_NOT_ALLOW", "String", "String")).thenReturn("3");
        when(PreferenceCache.getControlPreference("CP2P_PIN_VALIDAT_REQ", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("REC_MSG_SEND_ALLOW", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("OPT_USR_APRL_LEVEL", "String", "String")).thenReturn("1");
        when(PreferenceCache.getControlPreference("IS_FEE_APPL_VAL_EXT", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("PVT_SID_SERVICE_ALLOW", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("RP2PDWH_OPT_SPECIFIC_PROC_NAME", "String", "String")).thenReturn("RP2PDWHTEMPPRC");
        when(PreferenceCache.getControlPreference("P2PDWH_OPT_SPECIFIC_PROC_NAME", "String", "String")).thenReturn("P2pdwhtempprc");
        when(PreferenceCache.getControlPreference("IATDWH_OPT_SPECIFIC_PROC_NAME", "String", "String")).thenReturn("Iatdwhtempprc");
        when(PreferenceCache.getControlPreference("PVT_RECH_MESSGATEWAY", "String", "String")).thenReturn("PVTRECH");
        when(PreferenceCache.getControlPreference("EXTTXNMANDT_O2C", "String", "String")).thenReturn("4");
        when(PreferenceCache.getControlPreference("EXT_CODE_MAND_O2C", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("MSISDN_USAGE_SUMM_FLAG", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("O2C_ODR_APPROVAL_LVL", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("O2C_SMS_NOTIFY", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("AUTO_FOC_TRANSFER_AMT", "String", "String")).thenReturn("500");
        when(PreferenceCache.getControlPreference("AUTO_FOC_TRANSFER_AMOUNT", "String", "String")).thenReturn("500");
        when(PreferenceCache.getControlPreference("ALLOW_ROAM_RECHARGE", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("SOS_ALLOWED_MAX_BALANCE", "String", "String")).thenReturn("500");
        when(PreferenceCache.getControlPreference("SOS_RECHARGE_AMOUNT", "String", "String")).thenReturn("500");
        when(PreferenceCache.getControlPreference("SOS_ST_DEDUCT_UPFRONT", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("SOS_DAYS_GAP_BTWN_TWO_TRAN", "String", "String")).thenReturn("0");
        when(PreferenceCache.getControlPreference("SOS_SETTLE_DAYS", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("GMB_INTERACTIVE_OPTION_ALLOWED", "String", "String")).thenReturn("Y");
        when(PreferenceCache.getControlPreference("USSD_MENU_CODE", "String", "String")).thenReturn("100");
        when(PreferenceCache.getControlPreference("USSD_TAGS_CELLID_SWITCHID_MANDATORY", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("SOS_ELIBILITY_ACC", "String", "String")).thenReturn("Core,LMB_ALLOWED");
        when(PreferenceCache.getControlPreference("SOS_MINIMUM_AON", "String", "String")).thenReturn("90");
        when(PreferenceCache.getControlPreference("SOS_MIN_VALIDITY_DAYS", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("SOS_ONLINE_ALLOW", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("ENQ_POSTBAL_ALLOW", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("ENQ_POSTBAL_IN", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("LMB_VALIDITY_DAYS_FORCESETTLE", "String", "String")).thenReturn("120");
        when(PreferenceCache.getControlPreference("LAST_SERVICE_TYPE_CHECK", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("MRP_BLOCK_TIME_ALLOWED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("LMB_BLK_UPL", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("LMB_FORCE_SETL_STAT_ALLOW", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("PLAIN_RES_PARSE_REQUIRED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("COUNTRY_CODE", "String", "String")).thenReturn("91");
        when(PreferenceCache.getControlPreference("REQ_CUSER_SUS_APP", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("RVERSE_TXN_APPRV_LVL", "String", "String")).thenReturn("0");
        when(PreferenceCache.getControlPreference("DB_ENTRY_NOT_ALLOWED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("ACT_FRST_RCH_APP", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("C2S_REC_GEN_FAIL_MSG_REQD_V_FOR_GIFTER", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("C2S_REC_GEN_FAIL_MSG_REQD_T_FOR_GIFTER", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("VOMS_MAX_APPROVAL_LEVEL", "String", "String")).thenReturn("3");
        when(PreferenceCache.getControlPreference("VOMS_USER_KEY_REQD", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("VOUCHER_TRACKING_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("VOUCHER_EN_ON_TRACKING", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("MAX_AUTO_FOC_ALLOW_LIMIT", "String", "String")).thenReturn("20");
        when(PreferenceCache.getControlPreference("PROCESS_FEE_REV_ALLOWED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("LMB_DEBIT_REQ", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("USR_DEF_CONFIG_UPDATE_REQ", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("SELECTOR_INTERFACE_MAPPING", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("CP_SUSPENSION_DAYS_LIMIT", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("CAT_ALLOW_CREATION", "String", "String")).thenReturn("DIST,BCU,RET,AG");
        when(PreferenceCache.getControlPreference("MSISDN_MIGRATION_LIST", "String", "String")).thenReturn("012:0122");
        when(PreferenceCache.getControlPreference("PRVT_RC_MSISDN_PREFIX_LIST", "String", "String")).thenReturn("1111,2222");
        when(PreferenceCache.getControlPreference("LAST_X_RECHARGE_STATUS", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("SERVICE_FOR_LAST_X_RECHARGE", "String", "String")).thenReturn("C2S");
        when(PreferenceCache.getControlPreference("SMS_PIN_BYPASS_GATEWAY", "String", "String")).thenReturn("PLAIN");
        when(PreferenceCache.getControlPreference("IS_FNAME_LNAME_ALLOWED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("EXT_CODE_MAND_USER", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("EXT_VOMS_MSG_ENDEC_KEY", "String", "String")).thenReturn("ZO4UGUGCAFTLK9MOZO4UGUGCAFTLK9MO");
        when(PreferenceCache.getControlPreference("MCDL_MAX_LIST_COUNT", "String", "String")).thenReturn("60");
        when(PreferenceCache.getControlPreference("MCDL_DIFF_REQST_SEP", "String", "String")).thenReturn(",");
        when(PreferenceCache.getControlPreference("P2P_MCDL_DEFAULT_AMOUNT", "String", "String")).thenReturn("0");
        when(PreferenceCache.getControlPreference("P2P_MCDL_MAXADD_AMOUNT", "String", "String")).thenReturn("50000000");
        when(PreferenceCache.getControlPreference("P2P_MCDL_AUTO_DELETION_DAYS", "String", "String")).thenReturn("90");
        when(PreferenceCache.getControlPreference("COS_REQUIRED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("TRF_RULE_USER_LEVEL_ALLOW", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("RSA_AUTHENTICATION_REQUIRED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("COUNT_TO_ASK_RSA_CODE", "String", "String")).thenReturn("3");
        when(PreferenceCache.getControlPreference("LOGIN_ID_CHECK_ALLOWED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("STAFF_USER_APRL_LEVEL", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("BATCH_USER_PROFILE_ASSIGN", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("BATCH_INTIATE_NOTIF_TYPE", "String", "String")).thenReturn("BOTH");
        when(PreferenceCache.getControlPreference("EMAIL_SERVICE_ALLOW", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("SRVC_PROD_MAPPING_ALLOWED", "String", "String")).thenReturn("RC,GRC,INTRRC,RCREV,PSTNRC,PPB");
        when(PreferenceCache.getControlPreference("SERVICE_PROVIDER_PROMO_ALLOW", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("CELL_GROUP_REQUIRED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("SRVC_PROD_INTFC_MAPPING_ALLOWED", "String", "String")).thenReturn("PVAS,VAS,RC,CDATA,DTH");
        when(PreferenceCache.getControlPreference("CELL_ID_SWITCH_ID_REQUIRED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("DEBIT_SENDER_SIMACT", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("SIMACT_DEFAULT_SELECTOR", "String", "String")).thenReturn("1");
        when(PreferenceCache.getControlPreference("IN_PROMO_REQUIRED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("LOGIN_PASSWORD_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("MULTI_AMOUNT_ENABLED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("SHOW_CAPTCHA", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("EMAIL_AUTH_REQ", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("PER_DAY_BAR_FOR_DEL_LIMIT", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("REQ_CUSER_BAR_APP", "String", "String")).thenReturn("1");
        when(PreferenceCache.getControlPreference("AUTO_O2C_MAX_APPROVAL_LEVEL", "String", "String")).thenReturn("3");
        when(PreferenceCache.getControlPreference("AUTO_O2C_AMOUNT", "String", "String")).thenReturn("1000");
        when(PreferenceCache.getControlPreference("SRVCS_FOR_PROD_MAPPING", "String", "String")).thenReturn("VAS,PVAS,RCREV");
        when(PreferenceCache.getControlPreference("IS_REQ_MSISDN_FOR_STAFF", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("MIN_HRDIF_CR_ST_LMS", "String", "String")).thenReturn("8");
        when(PreferenceCache.getControlPreference("MIN_HRDIF_ST_ED_LMS", "String", "String")).thenReturn("120");
        when(PreferenceCache.getControlPreference("LMS_APPL", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("IS_SEPARATE_EXT_DB", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("LMS_VOL_COUNT_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("LMS_PROF_APR_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("LMS_MULT_FACTOR", "String", "String")).thenReturn("1.99");
        when(PreferenceCache.getControlPreference("LMS_VOL_CREDIT_LOYAL_PTS", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("MAX_LAST_TRANSFERS_DAYS", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("THLD_PRTP_PRCSS_TIME", "String", "String")).thenReturn("400");
        when(PreferenceCache.getControlPreference("EXTSYS_USR_APRL_LEVEL_REQUIRED", "String", "String")).thenReturn("0");
        when(PreferenceCache.getControlPreference("LMS_STOCK_REQUIRED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("INVALID_PWD_COUNT_FOR_CAPTCHA", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("PAYMENTDETAILSMANDATE_O2C", "String", "String")).thenReturn("0");
        when(PreferenceCache.getControlPreference("USER_CREATION_MANDATORY_FIELDS", "String", "String")).thenReturn("email,externalCode");
        when(PreferenceCache.getControlPreference("USER_APPROVAL_LEVEL", "String", "String")).thenReturn(1);
        when(PreferenceCache.getControlPreference("O2C_APPRV_QTY_LEVEL", "String", "String")).thenReturn("1,2");
        when(PreferenceCache.getControlPreference("STAFF_USER_AUTH_TYPE", "String", "String")).thenReturn("NA");
        when(PreferenceCache.getControlPreference("CHANNEL_USER_ROLE_TYPE_DISPLAY", "String", "String")).thenReturn("ALL");
        when(PreferenceCache.getControlPreference("IS_PARTIAL_BATCH_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("MULTIPLE_VOUCHER_TABLE", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("VOMS_INTERFACE_FLAG", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("USER_PRODUCT_MULTIPLE_WALLET", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("DEFAULT_WALLET", "String", "String")).thenReturn("MAIN");
        when(PreferenceCache.getControlPreference("MIN_VOUCHER_CODE_LENGTH", "String", "String")).thenReturn("6");
        when(PreferenceCache.getControlPreference("IS_MSISDN_ASSO_REQ", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("ADMINISTRBLY_USER_STATUS_CHANG", "String", "String")).thenReturn("DE:Y,DE:CH,DE:EX,DE:N,EX:Y,EX:CH,CH:Y,Y:EX,PA:DE");
        when(PreferenceCache.getControlPreference("REALTIME_AUTO_C2C_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("C2S_TRNSFR_INVNO_SRVCTYP", "String", "String")).thenReturn("CPB");
        when(PreferenceCache.getControlPreference("O2C_PNDNG_FOR_APPR_SEND_MAIL_DAYS_CNT", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("OTP_TIMEOUT_INSEC", "String", "String")).thenReturn("121");
        when(PreferenceCache.getControlPreference("C2S_TRNSFR_AMTBLCK_SRVCTYP", "String", "String")).thenReturn("PIN,CE,PMD,RPB,CCN");
        when(PreferenceCache.getControlPreference("AUTO_O2C_APPROVAL_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("MIN_ACCOUNT_ID_LENGTH", "String", "String")).thenReturn("8");
        when(PreferenceCache.getControlPreference("MAX_ACCOUNT_ID_LENGTH", "String", "String")).thenReturn("8");
        when(PreferenceCache.getControlPreference("SAP_INTEGARATION_FOR_USRINFO", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("AUTH_TYPE_REQ", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("COUNT_TO_ASK_OTP_CODE", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("C2S_REVERSAL_TXNID_SRVCTYP", "String", "String")).thenReturn("CCN");
        when(PreferenceCache.getControlPreference("CAPTCHA_LENGTH", "String", "String")).thenReturn("6");
        when(PreferenceCache.getControlPreference("MAX_AUTOTOPUP_AMT", "String", "String")).thenReturn("10000");
        when(PreferenceCache.getControlPreference("PERCENTAGE_OF_PRE_REVERSAL", "String", "String")).thenReturn("70");
        when(PreferenceCache.getControlPreference("WALLET_FOR_ADNL_CMSN", "String", "String")).thenReturn("BONUS");
        when(PreferenceCache.getControlPreference("ALLOW_ROAM_ADDCOMM", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("ALLOW_CCARD_ROAM_RECHARGE", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("ROAM_INTERFACE_ID", "String", "String")).thenReturn("INTID00032");
        when(PreferenceCache.getControlPreference("LMS_PCT_POINTS_CALCULATION", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("SEC_QUES_COUNT", "String", "String")).thenReturn("3");
        when(PreferenceCache.getControlPreference("VOUCHER_SERVICES_LIST", "String", "String")).thenReturn("RC,PRC,EVD,PPB");
        when(PreferenceCache.getControlPreference("TXN_RECEIVER_USER_STATUS_CHANG", "String", "String")).thenReturn("W:CH,EX:Y,CH:Y,PA:Y");
        when(PreferenceCache.getControlPreference("TXN_SENDER_USER_STATUS_CHANG", "String", "String")).thenReturn("EX:Y,CH:Y,PA:Y");
        when(PreferenceCache.getControlPreference("LIFECYCLE_STATUS_DAYS_LIST", "String", "String")).thenReturn("PA:Y:CH:EX:DE:N,1:1:1:1:1");
        when(PreferenceCache.getControlPreference("BLOCKING_ALLOWED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("ALERT_ALLOWED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("CARD_GROUP_ENQRES", "String", "String")).thenReturn("NAME:RANGE:REV_PR:DATE");
        when(PreferenceCache.getControlPreference("SEND_SMS_TO_PARENT", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("DP_NETWORKLEVEL_DAILYLIMIT", "String", "String")).thenReturn("100000");
        when(PreferenceCache.getControlPreference("REALTIME_AUTOALERT_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("SEND_SMS2PARENT_SERVICES_LIST", "String", "String")).thenReturn("RC,PRC,EVD,PPB");
        when(PreferenceCache.getControlPreference("DP_SYSTEMLEVEL_LIMIT", "String", "String")).thenReturn("30000");
        when(PreferenceCache.getControlPreference("RET_OPRTR_STOCK", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("ROAM_PENALTY_OWNER_PERCENTAGE", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("ROAM_RECHARGE_DAILY_THRESHOLD", "String", "String")).thenReturn("100");
        when(PreferenceCache.getControlPreference("ROAM_RECHARGE_PENALTY_PERCENTAGE", "String", "String")).thenReturn("40");
        when(PreferenceCache.getControlPreference("CENTRALIZED_USER_MANAGEMENT", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("INTF_NODE_VALIDATION", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("OPT_IN_OUT_ALLOW", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("EXTTXNMANDT_LPT", "String", "String")).thenReturn("4");
        when(PreferenceCache.getControlPreference("EXT_CODE_MAND_LPT", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("LPT_ODR_APPROVAL_LVL", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("M_PRE_PERCENTAGE", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("M_SLAVE_PERCENTAGE", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("DECENTER_ROAM_LOCATION", "String", "String")).thenReturn("MH");
        when(PreferenceCache.getControlPreference("P2P_ALLOWED_SCHTYPE", "String", "String")).thenReturn("WK,MO");
        when(PreferenceCache.getControlPreference("SRVC_ALLOW_ROAM_PENALTY", "String", "String")).thenReturn("RC,GRC");
        when(PreferenceCache.getControlPreference("CHOICE_RECHARGE_APPLICABLE", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("TWO_FA_REQ", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("TIME_FOR_REVERSAL", "String", "String")).thenReturn("48000");
        when(PreferenceCache.getControlPreference("TIME_FOR_REVERSAL_CCE", "String", "String")).thenReturn("48");
        when(PreferenceCache.getControlPreference("ALLOWED_DAYS_FOR_REVERSAL", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("ALLOWED_SERVICES_FOR_REVERSAL", "String", "String")).thenReturn("RC,GRC");
        when(PreferenceCache.getControlPreference("ALLOW_BULK_C2S_REVERSAL_MESSAGE", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("ALLOWED_GATEWAY_FOR_BULK_REVERSAL", "String", "String")).thenReturn("WEB");
        when(PreferenceCache.getControlPreference("DP_ONLINE_LIMIT", "String", "String")).thenReturn("3000");
        when(PreferenceCache.getControlPreference("DECIMAL_ALLOWED_IN_SERVICES", "String", "String")).thenReturn("RC,GRC,PPB,PRC,INTRRC,PSTNRC");
        when(PreferenceCache.getControlPreference("ADMIN_MESSAGE_REQD", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("O2C_BATCH_WITHDRAW_MESSAGE_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("OTP_ALLOWED_LENGTH", "String", "String")).thenReturn("8");
        when(PreferenceCache.getControlPreference("DEFAULT_CURRENCY", "String", "String")).thenReturn("COP");
        when(PreferenceCache.getControlPreference("FNF_ZB_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("LOW_BASED_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("P2P_SERVICES_TYPE_SERVICECLASS", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("MSISDN_LENGTH", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("LB_SYSTEMLEVEL_LIMIT", "String", "String")).thenReturn("100000");
        when(PreferenceCache.getControlPreference("SID_ENCRYPTION_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("DUPLICATE_CARDGROUP_CODE_ALLOW", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("OWNER_COMMISION_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("AUTO_NWSTK_CRTN_ALWD", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("AUTO_NWSTK_CRTN_THRESHOLD", "String", "String")).thenReturn("SAL:ETOPUP:10000:500000,INC:ETOPUP:200000:5000000,FOC:ETOPUP:60000:700000,SAL:POSTETOPUP:200:6000000,INC:POSTETOPUP:2000:5000000,FOC:POSTETOPUP:60000:700000,SAL:VOUCHTRACK:200:6000000,INC:VOUCHTRACK:2000:5000000,FOC:VOUCHTRACK:60000:700000");
        when(PreferenceCache.getControlPreference("TOKEN_EXPIRY_IN_MINTS", "String", "String")).thenReturn("60");
        when(PreferenceCache.getControlPreference("O2C_DIRECT_TRANSFER", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("SEQUENCE_ID_RANGE", "String", "String")).thenReturn("12");
        when(PreferenceCache.getControlPreference("SEQUENCE_ID_ENABLE", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("HASHING_ID_RANGE", "String", "String")).thenReturn("12");
        when(PreferenceCache.getControlPreference("HASHING_ENABLE", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("CHANNEL_SOS_ENABLE", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("AUTO_C2C_SOS_CAT_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("CHANNEL_AUTOC2C_ENABLE", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("SOS_SETTLEMENT_TYPE", "String", "String")).thenReturn("MANUAL");
        when(PreferenceCache.getControlPreference("CHANNEL_SOS_ALLOWED_WALLET", "String", "String")).thenReturn("PARENT");
        when(PreferenceCache.getControlPreference("DOWNLOAD_CSV_REPORT_REQUIRED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("ALLOW_TRANSACTION_IF_SOS_SETTLEMENT_FAIL", "String", "String")).thenReturn("FOC, DP");
        when(PreferenceCache.getControlPreference("LR_ENABLED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("TARGET_BASED_ADDNL_COMMISSION", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("OFFLINE_SETTLE_EXTUSR", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("TARGET_BASED_ADDNL_COMMISSION_SLABS", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("TARGET_BASED_BASE_COMMISSION_SLABS", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("TARGET_BASED_BASE_COMMISSION", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("ALLOWD_USR_TYP_CREATION", "String", "String")).thenReturn("N");
        when(PreferenceCache.getControlPreference("SYSTEM_ROLE_ALLOWED", "String", "String")).thenReturn("N");
        when(PreferenceCache.getControlPreference("CHNL_USR_LAST_ACTIVE_TXN", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("ADD_COMM_SEPARATE_MSG", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("CHANNEL_TRANSFERS_INFO_REQUIRED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("REALTIME_OTF_MESSAGES", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("POST_SERVICE_CLASS", "String", "String")).thenReturn("1000");
        when(PreferenceCache.getControlPreference("SUBS_BLK_AFT_X_CONS_FAIL", "String", "String")).thenReturn("4");
        when(PreferenceCache.getControlPreference("SUBS_UNBLK_AFT_X_TIME", "String", "String")).thenReturn("90");
        when(PreferenceCache.getControlPreference("DECRYPT_KEY_VISIBLE", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("THIRD_PARTY_VISIBLE", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("VOMS_PROFILE_ACTIVATION_REQ", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("VMS_AUTO_VOUCHER_CRTN_ALWD", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("STK_MASTER_KEY", "String", "String")).thenReturn("6B4FC9246FB075B619626600EAA870F9");
        when(PreferenceCache.getControlPreference("BURN_RATE_THRESHOLD_PCT", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("VOUCHER_BURN_RATE_SMS_ALERT", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("VOUCHER_BURN_RATE_EMAIL_ALERT", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("PIN_VALIDATATION_IN_USSD", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("INET_REPORT_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("VOMS_VALIDATION_BY_IN", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("VOMS_DAMG_PIN_LNTH_ALLOW", "String", "String")).thenReturn("3");
        when(PreferenceCache.getControlPreference("VPIN_INVALID_COUNT", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("VOMS_PROF_TALKTIME_MANDATORY", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("VOMS_PROF_VALIDITY_MANDATORY", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("VOMS_PROFILE_DEF_MINMAXQTY", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("VOMS_PROFILE_MIN_REORDERQTY", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("VOMS_PROFILE_MAX_REORDERQTY", "String", "String")).thenReturn("10000");
        when(PreferenceCache.getControlPreference("NW_NATIONAL_PREFIX", "String", "String")).thenReturn("99");
        when(PreferenceCache.getControlPreference("NW_CODE_NW_PREFIX_MAPPING", "String", "String")).thenReturn("NG=11,PB=12");
        when(PreferenceCache.getControlPreference("OTH_COM_CHNL", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("SERVICES_ALLOWED_SHOW_CARDGROUPLIST", "String", "String")).thenReturn("ABC");
        when(PreferenceCache.getControlPreference("MAX_REQ_VOUCHER_QTY", "String", "String")).thenReturn("5000");
        when(PreferenceCache.getControlPreference("P2P_PROMO_TRF_APP", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("P2P_PRO_TRF_ST_LVL_CODE", "String", "String")).thenReturn("1");
        when(PreferenceCache.getControlPreference("P2P_PRE_SERVCLASS_AS_POST", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("LAST_C2C_ENQ_MSG_REQ", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("USSD_REC_MSG_SEND_ALLOW", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("C2S_SEQ_ALWD", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("C2S_SEQID_FOR_GWC", "String", "String")).thenReturn("");
        when(PreferenceCache.getControlPreference("C2S_SEQID_APPL_SER", "String", "String")).thenReturn("");
        when(PreferenceCache.getControlPreference("ADDITIONAL_IN_FIELDS_ALLOWED", "String", "String")).thenReturn("");
        when(PreferenceCache.getControlPreference("PAYMENT_MODE_ALWD", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("EMAIL_DEFAULT_LOCALE", "String", "String")).thenReturn("en_US");
        when(PreferenceCache.getControlPreference("PG_INTEFRATION_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("USER_VOUCHERTYPE_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("PIN_REQUIRED_P2P", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("MAPP_SESSION_EXPIRY_SEC", "String", "String")).thenReturn("300");
        when(PreferenceCache.getControlPreference("MAPP_PRODUCT_GROUPING_REQ_SRV", "String", "String")).thenReturn("VAS,PVAS");
        when(PreferenceCache.getControlPreference("O2CAMB_MINUTES_DELAY", "String", "String")).thenReturn("-5");
        when(PreferenceCache.getControlPreference("VMS_ALLOW_CONTENT_TYPE", "String", "String")).thenReturn("pdf, jpg, jpeg, png");

        when(PreferenceCache.getControlPreference("CHNLUSR_VOUCHER_CATGRY_ALLWD", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("CHNLUSR_VOUCHER_CATGRY_ALLWD", "String", "RET")).thenReturn(true);
        when(PreferenceCache.getControlPreference("CHNLUSR_VOUCHER_CATGRY_ALLWD", "String", "DIST")).thenReturn(true);

        when(PreferenceCache.getControlPreference("TRANSACTION_TYPE", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("VOUCHER_THIRDPARTY_STATUS", "String", "String")).thenReturn("WH");
        when(PreferenceCache.getControlPreference("VMSPIN_EN_DE_CRYPTION_TYPE", "String", "String")).thenReturn("DES");
        when(PreferenceCache.getControlPreference("IPV6_ENABLED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("NATIONAL_VOUCHER_ENABLE", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("NATIONAL_VOUCHER_NETWORK_CODE", "String", "String")).thenReturn("NG");
        when(PreferenceCache.getControlPreference("DOWNLD_BATCH_BY_BATCHID", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("PAYMENT_VERIFICATION_ALLOWED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN", "String", "String")).thenReturn("0");
        when(PreferenceCache.getControlPreference("VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN", "String", "String")).thenReturn("3");
        when(PreferenceCache.getControlPreference("VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN", "String", "String")).thenReturn("3");
        when(PreferenceCache.getControlPreference("MAX_VOUCHER_EXPIRY_EXTN_LIMIT", "String", "String")).thenReturn("20");
        when(PreferenceCache.getControlPreference("ONLINE_BATCH_EXP_DATE_LIMIT", "String", "String")).thenReturn("15");
        when(PreferenceCache.getControlPreference("USER_VOUCHERSEGMENT_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("DATE_FORMAT_CAL_JAVA", "String", "String")).thenReturn("dd/MM/yy");
        when(PreferenceCache.getControlPreference("DATE_TIME_FORMAT", "String", "String")).thenReturn("dd/MM/yyyy HH24:mi:ss");
        when(PreferenceCache.getControlPreference("LOCALE_CALENDAR", "String", "String")).thenReturn("en-US");
        when(PreferenceCache.getControlPreference("LOCALE_ENGLISH", "String", "String")).thenReturn("@calendar=persian");
        when(PreferenceCache.getControlPreference("TIMEZONE_ID", "String", "String")).thenReturn("Asia/Kolkata");
        when(PreferenceCache.getControlPreference("CALENDAR_TYPE", "String", "String")).thenReturn("gregorian");
        when(PreferenceCache.getControlPreference("CALENDER_DATE_FORMAT", "String", "String")).thenReturn("dd/mm/yy");
        when(PreferenceCache.getControlPreference("CALENDAR_SYSTEM", "String", "String")).thenReturn("gregorian");
        when(PreferenceCache.getControlPreference("FORMAT_MONTH_YEAR", "String", "String")).thenReturn("yyyy/mm");
        when(PreferenceCache.getControlPreference("EXTERNAL_CALENDAR_TYPE", "String", "String")).thenReturn("persian");
        when(PreferenceCache.getControlPreference("IS_CAL_ICON_VISIBLE", "String", "String")).thenReturn("Y");
        when(PreferenceCache.getControlPreference("IS_MON_DATE_ON_UI", "String", "String")).thenReturn("N");
        when(PreferenceCache.getControlPreference("VOMS_PIN_BLK_EXP_DRN", "String", "String")).thenReturn("100000");
        when(PreferenceCache.getControlPreference("VOMS_NATIONAL_LOCAL_PREFIX_ENABLE", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("LDAP_UTIL_C", "String", "String")).thenReturn("com.btsl.pretups.util.LDAPUtil");
        when(PreferenceCache.getControlPreference("VMS_SERVICES", "String", "String")).thenReturn("VCN");
        when(PreferenceCache.getControlPreference("ONLINE_VOUCHER_GEN_LIMIT_SYSTEM", "String", "String")).thenReturn("100000");
        when(PreferenceCache.getControlPreference("VMS_P_STATUS_CHANGE", "String", "String")).thenReturn("EN,DA,ST,OH,WH,S");
        when(PreferenceCache.getControlPreference("VOUCHER_PROFLE_IS_OPTIONAL", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("VMS_E_STATUS_CHANGE_MAP", "String", "String")).thenReturn("OH:EN,OH:ST,OH:DA,OH:S,PA:EN,PA:S,S:EN,S:ST,S:OH,EN:OH,EN:ST,EN:DA,EN:S,GE:DA,GE:EN,GE:ST");
        when(PreferenceCache.getControlPreference("VMS_D_STATUS_CHANGE_MAP", "String", "String")).thenReturn("OH:EN,OH:ST,OH:DA,OH:S,PA:EN,PA:S,S:EN,S:ST,S:OH,EN:OH,EN:ST,EN:DA,EN:S,GE:DA,GE:ST");
        when(PreferenceCache.getControlPreference("VMS_D_CHANGE_STATUS", "String", "String")).thenReturn("EN,DA,ST,OH,S");
        when(PreferenceCache.getControlPreference("VMS_E_CHANGE_STATUS", "String", "String")).thenReturn("EN,DA,ST,OH,S");
        when(PreferenceCache.getControlPreference("VMS_P_CHANGE_STATUS", "String", "String")).thenReturn("EN,DA,ST,OH,WH,S");
        when(PreferenceCache.getControlPreference("VMS_D_STATUS_CHANGE", "String", "String")).thenReturn("EN,DA,ST,OH,S");
        when(PreferenceCache.getControlPreference("VMS_E_STATUS_CHANGE", "String", "String")).thenReturn("EN,DA,ST,OH,S");
        when(PreferenceCache.getControlPreference("VMS_P_STATUS_CHANGE_MAP", "String", "String")).thenReturn("OH:EN,OH:ST,OH:DA,OH:S,PA:EN,PA:S,S:EN,S:ST,S:OH,EN:OH,EN:ST,EN:DA,EN:S,PE:WH,PE:ST,WH:S,GE:DA,GE:EN,GE:ST");
        when(PreferenceCache.getControlPreference("ONLINE_CHANGE_STATUS_SYSTEM_LMT", "String", "String")).thenReturn("100000");
        when(PreferenceCache.getControlPreference("C2C_APPRV_QTY_LEVEL", "String", "String")).thenReturn("1");
        when(PreferenceCache.getControlPreference("MAX_APPROVAL_LEVEL_C2C_INITIATE", "String", "String")).thenReturn("1");
        when(PreferenceCache.getControlPreference("DVD_ORDER_BY_PARAMETERS", "String", "String")).thenReturn("EXPIRY_DATE ,CREATED_ON, SERIAL_NO");
        when(PreferenceCache.getControlPreference("REC_MSG_SEND_ALLOW_C2C", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("C2C_ALLOWED_VOUCHER_LIST", "String", "String")).thenReturn("EN,OH,PA,ST,GE");
        when(PreferenceCache.getControlPreference("MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER", "String", "String")).thenReturn("");
        when(PreferenceCache.getControlPreference("C2C_ALLOW_CONTENT_TYPE", "String", "String")).thenReturn("pdf,png,jpg");
        when(PreferenceCache.getControlPreference("ONLINE_VOUCHER_GEN_LIMIT", "String", "String")).thenReturn("100000");
        when(PreferenceCache.getControlPreference("VOUCHER_GEN_EMAIL_NOTIFICATION", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("VOUCHER_GEN_SMS_NOTIFICATION", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("SUBSCRIBER_VOUCHER_PIN_REQUIRED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("ONLINE_DVD_LIMIT", "String", "String")).thenReturn("100000");
        when(PreferenceCache.getControlPreference("ONLINE_VOUCHER_GEN_LIMIT_NW", "String", "String")).thenReturn("15000");
        when(PreferenceCache.getControlPreference("VMS_D_LIFECYCLE", "String", "String")).thenReturn("GE:EN:CU");
        when(PreferenceCache.getControlPreference("SCREEN_WISE_ALLOWED_VOUCHER_TYPE", "String", "String")).thenReturn("ACTIVE_PROF:E,ET;VOUC_DOWN:P,PT;O2C:D,DT,P,PT");
        when(PreferenceCache.getControlPreference("VMS_P_LIFECYCLE", "String", "String")).thenReturn("GE:PE:WH:EN:CU");
        when(PreferenceCache.getControlPreference("VMS_E_LIFECYCLE", "String", "String")).thenReturn("GE:EN:CU");
        when(PreferenceCache.getControlPreference("DVD_BATCH_FILEEXT", "String", "String")).thenReturn("csv");
        when(PreferenceCache.getControlPreference("ONLINE_CHANGE_STATUS_NETWORK_LMT", "String", "String")).thenReturn("10000");
        when(PreferenceCache.getControlPreference("MAX_APPROVAL_LEVEL_C2C_TRANSFER", "String", "String")).thenReturn("0");
        when(PreferenceCache.getControlPreference("C2C_EMAIL_NOTIFICATION", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("VOUCHER_PROFILE_OTHER_INFO", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("C2C_SMS_NOTIFICATION", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE", "String", "String")).thenReturn("");
        when(PreferenceCache.getControlPreference("PAYMENTDETAILSMANDATE_C2C", "String", "String")).thenReturn("3");
        when(PreferenceCache.getControlPreference("IS_BUN_PRE_ID_NULL_ALLOW", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("IS_VOU_BUN_NAME_LEN_ZERO_ALLOW", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("RECENT_C2C_TXN", "String", "String")).thenReturn("4");
        when(PreferenceCache.getControlPreference("TWO_FA_REQ_FOR_PIN", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("OTP_RESEND_TIMES", "String", "String")).thenReturn("3");
        when(PreferenceCache.getControlPreference("OTP_RESEND_DURATION", "String", "String")).thenReturn("180");
        when(PreferenceCache.getControlPreference("OTP_VALIDITY_PERIOD", "String", "String")).thenReturn("5000");
        when(PreferenceCache.getControlPreference("C2CVCRPT_DATEDIFF", "String", "String")).thenReturn("40");
        when(PreferenceCache.getControlPreference("O2CVCRPT_DATEDIFF", "String", "String")).thenReturn("15");
        when(PreferenceCache.getControlPreference("IS_BLANK_VOUCHER_REQ", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("CARD_GROUP_ALLOWED_CATEGORIES", "String", "String")).thenReturn("NWADM,SUNADM");
        when(PreferenceCache.getControlPreference("TRANSFER_RULE_ALLOWED_CATEGORIES", "String", "String")).thenReturn("NWADM,SUNADM");
        when(PreferenceCache.getControlPreference("VOUCH_GEN_BATCH_SIZE", "String", "String")).thenReturn("200");
        when(PreferenceCache.getControlPreference("AUTOCOMPLETE_USER_DETAILS_COUNT", "String", "String")).thenReturn("50");
        when(PreferenceCache.getControlPreference("VOUCH_GEN_RETRY_COUNT", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("IS_SECURITY_COMMON_VALIDATION_REQUIRED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("RESOURCE_UTIL_ONLINE_VOMS_GEN_THRESHOLD_LIMIT", "String", "String")).thenReturn("100");
        when(PreferenceCache.getControlPreference("ALPHANUM_SPCL_REGEX", "String", "String")).thenReturn("[a-zA-Z\\d]+?");
        when(PreferenceCache.getControlPreference("AVAILABLE_SOURCE_TYPE", "String", "String")).thenReturn("XML,PLAIN,JSON,WEB");
        when(PreferenceCache.getControlPreference("SUCC_BLOCK_TIME_O2C", "String", "String")).thenReturn("0");
        when(PreferenceCache.getControlPreference("SUCC_BLOCK_TIME_C2C", "String", "String")).thenReturn("0");
        when(PreferenceCache.getControlPreference("MRP_BLOCK_TIME_ALLOWED_CHNL", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("MRP_BLOCK_TIMEOUT_SERVICES_GATEWAY_CHNL", "String", "String")).thenReturn("O2C,C2C");
        when(PreferenceCache.getControlPreference("OTP_ON_SMS", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("MAX_INVALID_OTP", "String", "String")).thenReturn("3");
        when(PreferenceCache.getControlPreference("BLOCK_TIME_INVALID_OTP", "String", "String")).thenReturn("60");
        when(PreferenceCache.getControlPreference("MIN_LENGTH_TO_AUTOCOMPLETE", "String", "String")).thenReturn("3");
        when(PreferenceCache.getControlPreference("OAUTH_TOKEN_TIME_TO_LIVE", "String", "String")).thenReturn("60");
        when(PreferenceCache.getControlPreference("PAYMENTDETAILSMANDATEVOUCHER_C2C", "String", "String")).thenReturn("0");
        when(PreferenceCache.getControlPreference("REPORT_MAX_DATEDIFF_ADMIN_CONS", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("REPORT_MAX_DATEDIFF_USER_CONS", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("REPORT_MAX_DATEDIFF_USER_AVAIL", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("REPORT_MAX_DATEDIFF_ADMIN_AVAIL", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("REPORT_MAX_DATEDIFF_ADMIN_NLEVEL", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("USER_ALLOWED_VINFO", "String", "String")).thenReturn("SUBCU,BCU,DIST,SE,SUADM,SUNADM,NWADM,SSADM,AG,RET");
        when(PreferenceCache.getControlPreference("MAX_LOGINS_LOCATION", "String", "String")).thenReturn("10000");
        when(PreferenceCache.getControlPreference("MAX_LOGINS_TYPE", "String", "String")).thenReturn("5000");
        when(PreferenceCache.getControlPreference("MIN_LAST_DAYS_CG", "String", "String")).thenReturn("1");
        when(PreferenceCache.getControlPreference("MAX_LAST_DAYS_CG", "String", "String")).thenReturn("365");
        when(PreferenceCache.getControlPreference("BYPASS_EVD_KANEL_MES_STAT", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("LANGS_SUPT_ENCODING", "String", "String")).thenReturn("ar,ku,ku1,ru,fa");
        when(PreferenceCache.getControlPreference("IS_ONE_TIME_SID", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("TOKEN_EXPIRE_TIME", "String", "String")).thenReturn("6000");
        when(PreferenceCache.getControlPreference("REFRESH_TOKEN_EXPIRE_TIME", "String", "String")).thenReturn("6000");
        when(PreferenceCache.getControlPreference("VMS_UI_DISPLAY_DATE_TIME", "String", "String")).thenReturn("dd/MM/yy hh:mm:ss aa");
        when(PreferenceCache.getControlPreference("VOUCHER_UPLOAD_FILE_FORMATS", "String", "String")).thenReturn("xls,xlsx,csv");
        when(PreferenceCache.getControlPreference("TOP_N_PRODUCT_VALUE", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("REPORT_DATE_DURATION", "String", "String")).thenReturn("30");
        when(PreferenceCache.getControlPreference("AvgSaleNoOfDays", "String", "String")).thenReturn("15");
        when(PreferenceCache.getControlPreference("ElectronicStockDaysCalculatedOn", "String", "String")).thenReturn("EN");
        when(PreferenceCache.getControlPreference("VOUCHER_DTL_LOW_BALANCE", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("DIGITAL_RECHARGE_VOUCHER_TYPE", "String", "String")).thenReturn("digital,digital1,test_digit");
        when(PreferenceCache.getControlPreference("FORMAT_DATE_MONTH", "String", "String")).thenReturn("dd/MM");
        when(PreferenceCache.getControlPreference("IS_CELLID_REQUIRED_FROM_IN", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("IS_POSITIVE_COMM_DEBIT_FROM_SENDER_REQ", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("MAX_RECORDS_SIZE_VAL", "String", "String")).thenReturn("999999999999");
        when(PreferenceCache.getControlPreference("LOG_OUT_TIME", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("FORCE_LOGOUT", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("FILE_IS_NOT_EXIST", "String", "String")).thenReturn("{0} does not exist.");
        when(PreferenceCache.getControlPreference("VMS_VOUCHER_TEMPLATE_TYPE", "String", "String")).thenReturn("XLSX");
        when(PreferenceCache.getControlPreference("BURN_RATE_VOUCHER_TYPES", "String", "String")).thenReturn("P,E");
        when(PreferenceCache.getControlPreference("MAX_LOGINS_TYPE_NG_DIST", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("MAX_LOGINS_TYPE_NG_SSADM", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("VOMS_SERIAL_NO_MAX_LENGTH", "String", "String")).thenReturn("16");
        when(PreferenceCache.getControlPreference("RIGHT_CLICK_ENABLE", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("REPORT_ONLINE_OR_OFFLINE", "String", "String")).thenReturn("OFFLINE");
        when(PreferenceCache.getControlPreference("UI_COPY_CONTENT_ENABLE", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("ALLOW_SAME_REPORT_EXEC", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("TOT_RPT_EXEC_PERUSER", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("CGTAX34APP", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("REPORT_OFFLINE", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("SUBCRBR_PRFX_ROUTNG_ALWD", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("CHARSET_ENCODING", "String", "String")).thenReturn("UTF-16");
        when(PreferenceCache.getControlPreference("DEF_CHNL_TRANSFER_ALLOWED", "String", "String")).thenReturn("C2CVOMSTRFINI,TRFINI");
        when(PreferenceCache.getControlPreference("ALIAS_TO_BE_ENCRYPTED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("LOAD_BAL_IP_ALLOWED", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("FILE_UPLOAD_MAX_SIZE", "String", "String")).thenReturn("9999999");
        when(PreferenceCache.getControlPreference("SYSTEM_IDLE_TIME", "String", "String")).thenReturn("900");
        when(PreferenceCache.getControlPreference("MAX_FILE_SIZE_FOR_VMSSIGNED_DOC", "String", "String")).thenReturn("102400000");
        when(PreferenceCache.getControlPreference("VMS_MAX_BATCHES_ALLOWED", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("UPLOAD_FILE_APPRV_LEVEL", "String", "String")).thenReturn("3");
        when(PreferenceCache.getControlPreference("OPERATOR_UTIL_VMS", "String", "String")).thenReturn("OperatorUtil");
        when(PreferenceCache.getControlPreference("VOU_UPLOAD_ERROR_DISPLAY", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("VMS_CHECK_SUM_ENABLE", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("VOMS_GEN_BATCH_COUNT", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("VMS_APPROVAL_REQ", "String", "String")).thenReturn("N");
        when(PreferenceCache.getControlPreference("DEFAULT_PRODUCT_CODE", "String", "String")).thenReturn("ETOPUP");
        when(PreferenceCache.getControlPreference("MAX_BULK_FILE_SIZE_BYTES", "String", "String")).thenReturn("10200000");
        when(PreferenceCache.getControlPreference("DW_ALLOWED_GATEWAYS", "String", "String")).thenReturn("EXTGW,DWEXTGW");
        when(PreferenceCache.getControlPreference("SAP_ALLOWED", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("AUTO_SEARCH", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("C2C_BATCH_FILEEXT", "String", "String")).thenReturn("xls");
        when(PreferenceCache.getControlPreference("ERROR_FILE_C2C", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("VOMS_ORDER_SLAB_LENGTH", "String", "String")).thenReturn("4");
        when(PreferenceCache.getControlPreference("VOMS_MIN_ALT_VALUE", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("COM_PAY_OUT", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("USER_ALLOW_CONTENT_TYPE", "String", "String")).thenReturn("xlsx");
        when(PreferenceCache.getControlPreference("OFFLINERPT_DOWNLD_PATH", "String", "String")).thenReturn("/data1/pretupsapp/offlineDownloadFile/test2downloads/");
        when(PreferenceCache.getControlPreference("IS_VOU_DEN_PROFILE_ZERO_ALLOW", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("NET_PREFIX_TO_VALIDATED_FOR_BAR_UNBAR", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("CHADM_CROSS_ALLOW", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("USR_BTCH_SUS_DEL_APRVL", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("ADMINISTRBLY_USER_STATUS_CHANG_NEW", "String", "String")).thenReturn("DE:Y,DE:CH,DE:EX,DE:N,EX:Y,EX:CH,CH:Y,Y:EX,PA:DE,Y:S,S:RE,Y:BR");
        when(PreferenceCache.getControlPreference("ADD_INFO_REQUIRED_FOR_VOUCHER", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("IS_TRF_RULE_USER_LEVEL_ALLOW", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("USERWISE_LOAN_ENABLE", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("EXTREFNUM_MANDATORY_GATEWAYS", "String", "String")).thenReturn("ext");
        when(PreferenceCache.getControlPreference("TEMP_PIN_EXPIRY_DURATION", "String", "String")).thenReturn("24");
        when(PreferenceCache.getControlPreference("IMEI_OPTIONAL", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("CSRF_ENABLE", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("DIRECT_VOUCHER_ENABLE", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("OTP_VALIDITY_TIME", "String", "String")).thenReturn("500");
        when(PreferenceCache.getControlPreference("USER_EXTERNAL_CODE_DOMAINWISE", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("TARGET_COMMISSION_SLABS", "String", "String")).thenReturn("5");
        when(PreferenceCache.getControlPreference("COMMISSION_SLABS", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("COMMISSION_BASE_SLAB", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("COMMISSION_ADD_SLAB", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("CHANNEL_USER_STATUS_CHANGE_BATCH_FILEEXT", "String", "String")).thenReturn("xls");
        when(PreferenceCache.getControlPreference("OTP_REQUIRED", "String", "String")).thenReturn("ONETIME");
        when(PreferenceCache.getControlPreference("CHANGE_NETWORK", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("SNDR_NETWORK_IDENTIFY_ON_IMSI_BASIS", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("MAX_CHARS_FOR_SEARCH", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("ALLOW_TRANSACTION_IF_LOAN_SETTLEMENT_FAIL", "String", "String")).thenReturn("RETURN, RCREV");
        when(PreferenceCache.getControlPreference("VOUCHER_SHOW", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("ALLOW_GATEWAYCODE_FOR_LOAN_SETTLEMENT", "String", "String")).thenReturn("USSD,SMSC");
        when(PreferenceCache.getControlPreference("RESTRICT_TRANSACTION_FOR_LOAN_SETTLEMENT", "String", "String")).thenReturn("FOC");
        when(PreferenceCache.getControlPreference("LOAN_PROFILE_SLAB_LENGTH", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("BLOCK_GATEWAYCODE_FOR_LOAN_SETTLEMENT", "String", "String")).thenReturn("WEB,GSTREXTGW,EXTGW");
        when(PreferenceCache.getControlPreference("CAT_USERWISE_LOAN_ENABLE", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("CATEGORIES_LIFECYCLECHANGE", "String", "String")).thenReturn("RETA,SA");
        when(PreferenceCache.getControlPreference("VOUCHER", "String", "String")).thenReturn(false);
        when(PreferenceCache.getControlPreference("VOUCHER_EXP_EMAIL_NOTIFICATION", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("VOUCHREXPIRYUP_ONLINECOUNT", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("VOUCHER_EXP_SMS_NOTIFICATION", "String", "String")).thenReturn(true);
        when(PreferenceCache.getControlPreference("ADD_C2S_MAX", "String", "String")).thenReturn("10");
        when(PreferenceCache.getControlPreference("RANGE_BASED_ADDNL_COMMISSION_SLABS", "String", "String")).thenReturn("2");
        when(PreferenceCache.getControlPreference("RANGE_BASED_BASE_COMMISSION_SLABS", "String", "String")).thenReturn("2");
    }


    private static void initProps() {
        String constantspropsfile = null;
        constantspropsfile = "C:\\temp_tomcat\\conf\\pretups\\Constants.props";
        try {
            Constants.load(constantspropsfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Connection con = null;


    private static MComConnection mcomCon = null;


    public static Connection getConnection(){
        if(con == null){
            JUnitConfig.init();
        }
        //initConnections();
        return con;
    }

    public static MComConnection getMComConnection(){
        if(mcomCon == null){
            JUnitConfig.init();
        }
        return mcomCon;
    }


    public static void initConnections4() {
        try {
            ResultSet resultSet = mock(ResultSet.class);
            //PreparedStatement psmtInsert = mock(PreparedStatement.class);
            //when(psmtInsert.executeUpdate()).thenReturn(1);


            when(resultSet.getInt(Mockito.<String>any())).thenReturn(2).thenReturn(1).thenReturn(2).thenReturn(1);
            Timestamp ts = Timestamp.from(Instant.now());

            when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(ts);

            //rs.getTimestamp("modified_on").getTime()
            java.util.Date date = new java.util.Date();
            Date sqlDate = new Date(date.getTime());

            when(resultSet.getDate(Mockito.<String>any())).thenReturn(sqlDate);


            when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
            when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
            when(resultSet.getString("category_code")).thenReturn("DIST");
            when(resultSet.getString("scheduler_status")).thenReturn("C");
            when(resultSet.getString("record_count")).thenReturn("1");



            //when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);

            when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);



            doNothing().when(resultSet).close();
            PreparedStatement preparedStatement = mock(PreparedStatement.class);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(preparedStatement.executeUpdate()).thenReturn(1);//TODO: Aded
            doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
            doNothing().when(preparedStatement).close();
            //Connection con = mock(Connection.class);
            con = mock(Connection.class);
            when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

             mcomCon = mock(MComConnection.class);

            when(mcomCon.getConnection()).thenReturn(con);

        } catch (Exception e) {
        }
    }





    public static void constructorInit(){

        java.util.Date dummyDate = new java.util.Date();

        try (MockedConstruction<SimpleDateFormat> mComm = Mockito.mockConstruction(SimpleDateFormat.class,
                (mock, context) -> {
                    // further stubbings ...
                    when(mock.parse(Mockito.anyString())).thenReturn(dummyDate);
                })) {
           // SimpleDateFormat MComm = new SimpleDateFormat();
            //SimpleDateFormat MComm = mock(SimpleDateFormat.class);
            SimpleDateFormat MComm = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);

            try {
                when(MComm.parse(Mockito.anyString())).thenReturn(dummyDate);
            }catch(Exception e){
                e.printStackTrace();
            }


        }catch(Exception e){
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);


        //java.sql.Date frDate = new java.sql.Date();

        try {
            java.util.Date frDate = sdf.parse( "22/01/23 00:00:00");

			} catch (Exception e) {
            e.printStackTrace();
        }


        try (MockedConstruction<MComConnection> mComm = Mockito.mockConstruction(MComConnection.class,
                    (mock, context) -> {
                        // further stubbings ...
                        when(mock.getConnection()).thenReturn(JUnitConfig.getConnection());
                    })) {
                MComConnection MComm = new MComConnection();
                try {
                    //try{mockStatic(OracleUtil.class).close();}catch(Exception e){}
                 try{   mockStatic(OracleUtil.class); }catch(Exception e){}
                    //JUnitConfig.initConnections();//Already done
                    when(OracleUtil.getConnection()).thenReturn(JUnitConfig.getConnection());
                }catch(Exception e){
                    e.printStackTrace();
                }


            }catch(Exception e){

                try{
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);


                    BufferedWriter bw = new BufferedWriter(new FileWriter("D:/logs5.txt", true));

                    bw.write("Error -  "+sw.toString());
                    bw.close();
                }catch(Exception ee){}
            }
    }

    public static void initConnections() {
        try {


            try {
                mockStatic(OAuthenticationUtil.class);


            }catch(Exception e){}

            /* 20082023
            ResultSet resultSet = mock(ResultSet.class);
            ResultSet resultSet2 = mock(ResultSet.class);

            when(resultSet.getInt(Mockito.<String>any())).thenReturn(2).thenReturn(1).thenReturn(2).thenReturn(1);
            when(resultSet2.getInt(Mockito.<String>any())).thenReturn(2).thenReturn(1).thenReturn(2).thenReturn(1);
*/
            Timestamp ts = Timestamp.from(Instant.now());
   //20082023         when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(ts);
    //20082023        when(resultSet2.getTimestamp(Mockito.<String>any())).thenReturn(ts);

            java.util.Date date = new java.util.Date();
            Date sqlDate = new Date(date.getTime());
/* 20082023
            when(resultSet.getDate(Mockito.<String>any())).thenReturn(sqlDate);
            when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
            when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
            when(resultSet.getString("category_code")).thenReturn("DIST");
            when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    ;
            when(resultSet2.getDate(Mockito.<String>any())).thenReturn(sqlDate);
            when(resultSet2.getLong(Mockito.<String>any())).thenReturn(1L);
            when(resultSet2.getString(Mockito.<String>any())).thenReturn("String");
            when(resultSet2.getString("category_code")).thenReturn("DIST");
            when(resultSet2.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);




            doNothing().when(resultSet).close();
            doNothing().when(resultSet2).close();

*/
            PreparedStatement preparedStatement = mock(PreparedStatement.class);

            ResultSet[] rs = new ResultSet[50];

            for(int i =0 ; i<50 ; i++){
                 rs[i] = mock(ResultSet.class);


                 if(i % 2 == 0 ) {
                     when(rs[i].getInt(Mockito.<String>any())).thenReturn(2).thenReturn(1).thenReturn(2).thenReturn(1);
                 }else{
                     when(rs[i].getInt(Mockito.<String>any())).thenReturn(1).thenReturn(2).thenReturn(1).thenReturn(2);
                 }

                 //rs.getTimestamp("modified_on").getTime()
                when(rs[i].getTimestamp(Mockito.<String>any())).thenReturn(ts1);

              //  Timestamp ts2 = Timestamp.from(Instant.now());
               // long ll = (Instant.now()).getLong() ;
                Time ts4 = Time.valueOf("00:00:00") ;



                when(rs[i].getTime(Mockito.<String>any())).thenReturn(ts4);

                java.util.Date date1 = new java.util.Date();
                Date sqlDate1 = new Date(date1.getTime());

                when(rs[i].getDate(Mockito.<String>any())).thenReturn(sqlDate1);
                when(rs[i].getLong(Mockito.<String>any())).thenReturn(1L);
                when(rs[i].getString(Mockito.<String>any())).thenReturn("String");

                if(i % 2==0) {
                    when(rs[i].getString("category_code")).thenReturn("DIST").thenReturn("RET").thenReturn("DIST").thenReturn("RET");
                }else{
                    when(rs[i].getString("category_code")).thenReturn("RET").thenReturn("DIST").thenReturn("RET").thenReturn("DIST");
                }

                when(rs[i].getString("scheduler_status")).thenReturn("C");
                when(rs[i].getString("record_count")).thenReturn("1");


                if(SOURCE != null && SOURCE.contains("BatchCommissionProfileServiceImplTest")){
                    when(rs[i].getString("domain_code")).thenReturn("DIST");
                    when(rs[i].getString("PRODUCT_NAME")).thenReturn("ETOPUP");
                    when(rs[i].getString("PRODUCT_CODE")).thenReturn("ETOPUP");

                }

                if(SOURCE != null ){
                if(SOURCE.contains("ChannelAdmin") || SOURCE.contains("O2cTxnReversalService") ){

                    if(i % 2 == 0 ){
                        when(rs[i].getString("user_type")).thenReturn(PretupsI.OPERATOR_USER_TYPE);
                    }else{
                        when(rs[i].getString("user_type")).thenReturn(PretupsI.USER_TYPE_CHANNEL);
                    }



                }
                }else {
                    when(rs[i].getString("user_type")).thenReturn(PretupsI.OPERATOR_USER_TYPE);
                }

                when(rs[i].getString("COMM_PROFILE_SET_NAME")).thenReturn("COMM_PROFILE_SET_NAME");
                when(rs[i].getString("SHORT_CODE")).thenReturn("SHORT_CODE");
                when(rs[i].getString("BALANCE")).thenReturn("10");
                when(rs[i].getString("BALANCE")).thenReturn("10");
                when(rs[i].getString("BALANCE")).thenReturn("10");

                when(rs[i].getString("BALANCE")).thenReturn("10");
                when(rs[i].getString("first_level_approved_quantity")).thenReturn("100");
                when(rs[i].getString("second_level_approved_quantity")).thenReturn("100");
                when(rs[i].getString("third_level_approved_quantity")).thenReturn("100");
                when(rs[i].getString("SENDER_PREVIOUS_STOCK")).thenReturn("10000");
                when(rs[i].getString("SENDER_POST_STOCK")).thenReturn("10000");
                when(rs[i].getString("RECEIVER_PREVIOUS_STOCK")).thenReturn("10000");
                when(rs[i].getString("RECEIVER_POST_STOCK")).thenReturn("10000");




                when(rs[i].getString("sms_interface_allowed")).thenReturn("Y");

                when(rs[i].getString("mapstatus")).thenReturn("Y");

                //PretupsI.USER_STATUS_SUSPEND

                if(SOURCE != null && SOURCE.contains("ChannelAdmin")){
                    when(rs[i].getString("status")).thenReturn(PretupsI.USER_STATUS_SUSPEND);
                }else {
                    when(rs[i].getString("status")).thenReturn("Y");
                }

                when(rs[i].getString("userstatus")).thenReturn("Y");
                when(rs[i].getString("l")).thenReturn("2");



                when(rs[i].getString("from_category")).thenReturn("DIST").thenReturn("RET").thenReturn("DIST").thenReturn("RET");
                when(rs[i].getString("to_category")).thenReturn("RET").thenReturn("DIST").thenReturn("DIST").thenReturn("RET");




                when(rs[i].next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);


                doNothing().when(rs[i]).close();
                doNothing().when(rs[i]).close();
              //  PreparedStatement pstmt = mock(PreparedStatement.class);



            }
            when(preparedStatement.executeQuery()).thenReturn(rs[0])
                    .thenReturn(rs[1])
                    .thenReturn(rs[2])
                    .thenReturn(rs[3])
                    .thenReturn(rs[4])
                    .thenReturn(rs[5])
                    .thenReturn(rs[6])
                    .thenReturn(rs[7])
                    .thenReturn(rs[8])
                    .thenReturn(rs[9])
                    .thenReturn(rs[10])
                    .thenReturn(rs[11])
                    .thenReturn(rs[12])
                    .thenReturn(rs[13])
                    .thenReturn(rs[14])
                    .thenReturn(rs[15])
                    .thenReturn(rs[16])
                    .thenReturn(rs[17])
                    .thenReturn(rs[18])
                    .thenReturn(rs[19])

                    .thenReturn(rs[20])
                    .thenReturn(rs[21])
                    .thenReturn(rs[22])
                    .thenReturn(rs[23])
                    .thenReturn(rs[24])
                    .thenReturn(rs[25])
                    .thenReturn(rs[26])
                    .thenReturn(rs[27])
                    .thenReturn(rs[28])
                    .thenReturn(rs[29])
                    .thenReturn(rs[30])
                    .thenReturn(rs[32])
                    .thenReturn(rs[33])
                    .thenReturn(rs[34])
                    .thenReturn(rs[35])
                    .thenReturn(rs[36])
                    .thenReturn(rs[37])
                    .thenReturn(rs[38])
                    .thenReturn(rs[39])
                    .thenReturn(rs[40])
                    .thenReturn(rs[41])
                    .thenReturn(rs[42])
                    .thenReturn(rs[43])
                    .thenReturn(rs[44])
                    .thenReturn(rs[45])
                    .thenReturn(rs[46])
                    .thenReturn(rs[47])
                    .thenReturn(rs[48])
                    .thenReturn(rs[49]);


           /* when(preparedStatement.executeQuery()).thenReturn(resultSet)
                    .thenReturn(resultSet2)
                    .thenReturn(resultSet)
                    .thenReturn(resultSet)
                    .thenReturn(resultSet)
                    .thenReturn(resultSet)
                    .thenReturn(resultSet)
                    .thenReturn(resultSet)
                    .thenReturn(resultSet)
                    .thenReturn(resultSet)
                    .thenReturn(resultSet)
                    .thenReturn(resultSet)
                    .thenReturn(resultSet)
                    .thenReturn(resultSet)
                    .thenReturn(resultSet)
                    .thenReturn(resultSet)
                    .thenReturn(resultSet)
                    .thenReturn(resultSet)
                    .thenReturn(resultSet)
                    .thenReturn(resultSet);*/
            when(preparedStatement.executeUpdate()).thenReturn(1);//TODO: Aded
            doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
            doNothing().when(preparedStatement).close();
            //Connection con = mock(Connection.class);
            con = mock(Connection.class);
            when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

            mcomCon = mock(MComConnection.class);

            when(mcomCon.getConnection()).thenReturn(con);



/* 20082023
            try (MockedConstruction<MComConnection> mComm = Mockito.mockConstruction(MComConnection.class,
                    (mock, context) -> {
                        // further stubbings ...
                        when(mock.getConnection()).thenReturn(JUnitConfig.getConnection());
                    })) {
                MComConnection MComm = new MComConnection();
                try {
                    mockStatic(OracleUtil.class);
                    //JUnitConfig.initConnections();//Already done
                    when(OracleUtil.getConnection()).thenReturn(JUnitConfig.getConnection());
                }catch(Exception e){
                    e.printStackTrace();
                }


            }*/





            try (MockedConstruction<OAuthUserData> mock = Mockito.mockConstruction(OAuthUserData.class
                    /*,
                    (mock, context) -> {
//                        mock.setLoginid("testuser");
                        when(mock.getLoginid()).thenReturn("testuser");


                    })*/)) {
                OAuthUserData oAuthUserData = new OAuthUserData();
                try {
                   // oAuthUserData2.setLoginid("testuser");
                    when(oAuthUserData.getLoginid()).thenReturn("testuser");

                }catch(Exception e){
                    e.printStackTrace();
                }
            }


/*

            try (MockedConstruction<OAuthUser> oAuthUser = Mockito.mockConstruction(OAuthUser.class,
                    (mock, context) -> {
                        // further stubbings ...
                        when(mock.getData().getLoginid()).thenReturn("testuser");
                    })) {
                OAuthUser oAuthUser2 = mock(OAuthUser.class) ;
                try {

                    when(oAuthUser2.getData().getLoginid()).thenReturn("testuser");

                }catch(Exception e){
                    e.printStackTrace();
                }
            }

*/

        } catch (Exception e) {
            e.printStackTrace();

            try{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);


                BufferedWriter bw = new BufferedWriter(new FileWriter("D:/logs4.txt", true));

                bw.write("Error -  "+sw.toString());
                bw.close();
            }catch(Exception ee){}

        }


        try{
            Mockito.mockStatic(FileUtils.class);

            byte[] bytes = new byte[10];

            bytes[0] =1;
            bytes[1] =1;
            bytes[2] =1;
            bytes[3] =1;
            bytes[4] =1;
            bytes[5] =1;
            bytes[6] =1;
            bytes[7] =1;
            bytes[8] =1;
            bytes[9] =1;

            when(FileUtils.readFileToByteArray(Mockito.any())).thenReturn(bytes) ;

        }catch(Exception e){}

        try{

            if(SOURCE != null && SOURCE.contains("BatchCommissionProfileServiceImplTest")){

            }else {

                try {

                    Mockito.mockStatic(Workbook.class);
                } catch (Exception e) {
                }
                //  Workbook.createWorkbook(new File(fileName));
                Workbook workBook = mock(Workbook.class);
                WritableWorkbook writableWorkbook = mock(WritableWorkbook.class);

                Sheet excelsheet = mock(Sheet.class);
                SheetSettings sheetSettings = mock(SheetSettings.class);

                doNothing().when(sheetSettings).setVerticalFreeze(Mockito.anyInt());

                WritableSheet writabelExcelsheet = mock(WritableSheet.class);
                //worksheet.getSettings()
                Cell mockCell1 = mock(Cell.class);
                Cell mockCell2 = mock(Cell.class);
                Cell mockCell3 = mock(Cell.class);
                Cell mockCell4 = mock(Cell.class);
                Cell mockCell5 = mock(Cell.class);
                Cell mockCell6 = mock(Cell.class);
                Cell mockCell7 = mock(Cell.class);
                Cell mockCell8 = mock(Cell.class);
                Cell mockCell9 = mock(Cell.class);
                Cell mockCell10 = mock(Cell.class);
                Cell mockCell11 = mock(Cell.class);
                Cell mockCell12 = mock(Cell.class);
                Cell mockCell13 = mock(Cell.class);
                Cell mockCell14 = mock(Cell.class);

                Cell mockCell15 = mock(Cell.class);
                Cell mockCell16 = mock(Cell.class);
                Cell mockCell17 = mock(Cell.class);
                Cell mockCell18 = mock(Cell.class);
                Cell mockCell19 = mock(Cell.class);
                Cell mockCell20 = mock(Cell.class);
                Cell mockCell21 = mock(Cell.class);
                Cell mockCell22 = mock(Cell.class);
                Cell mockCell23 = mock(Cell.class);
                Cell mockCell24 = mock(Cell.class);
                Cell mockCell25 = mock(Cell.class);
                Cell mockCell26 = mock(Cell.class);
                Cell mockCell27 = mock(Cell.class);
                Cell mockCell28 = mock(Cell.class);
                Cell mockCell29 = mock(Cell.class);


                when(Workbook.createWorkbook(Mockito.<File>any())).thenReturn(writableWorkbook);

                when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
                when(workBook.getNumberOfSheets()).thenReturn(2);
                when(workBook.getSheet(Mockito.anyInt())).thenReturn(excelsheet);


                when(writableWorkbook.getNumberOfSheets()).thenReturn(2);
                when(writableWorkbook.createSheet(Mockito.anyString(), Mockito.anyInt())).thenReturn(writabelExcelsheet);


                when(excelsheet.getRows()).thenReturn(25);
                when(excelsheet.getColumns()).thenReturn(25);

                when(writabelExcelsheet.getRows()).thenReturn(10);
                when(writabelExcelsheet.getColumns()).thenReturn(25);

                when(writabelExcelsheet.getSettings()).thenReturn(sheetSettings);
/*
            if(SOURCE != null && SOURCE.contains("BatchCommissionProfileServiceImplTest")){
                when(mockCell1.getContents()).thenReturn("ytdshj1");
                when(mockCell2.getContents()).thenReturn("dfjks");
                when(mockCell3.getContents()).thenReturn("ETOPUP");
                when(mockCell4.getContents()).thenReturn("O2C");//Domain-DIST
                when(mockCell5.getContents()).thenReturn("DD");
                when(mockCell6.getContents()).thenReturn("ALL");
                when(mockCell7.getContents()).thenReturn("ALL");
                when(mockCell8.getContents()).thenReturn("4111");
                when(mockCell9.getContents()).thenReturn("NC");
                when(mockCell10.getContents()).thenReturn("2");
                when(mockCell11.getContents()).thenReturn("14302");
                when(mockCell12.getContents()).thenReturn("56951");
                when(mockCell13.getContents()).thenReturn("17/05/2024");
                when(mockCell14.getContents()).thenReturn("13:32");

                when(mockCell15.getContents()).thenReturn("N");
                when(mockCell16.getContents()).thenReturn("N");
                when(mockCell17.getContents()).thenReturn("1.0");
                when(mockCell18.getContents()).thenReturn("1.0");
                when(mockCell19.getContents()).thenReturn("100.0");
                when(mockCell20.getContents()).thenReturn("1.0");
                when(mockCell21.getContents()).thenReturn("10.0");
                when(mockCell22.getContents()).thenReturn("PCT");
                when(mockCell23.getContents()).thenReturn("0.0");
                when(mockCell24.getContents()).thenReturn("PCT");
                when(mockCell25.getContents()).thenReturn("0.0");
                when(mockCell26.getContents()).thenReturn("PCT");
                when(mockCell27.getContents()).thenReturn("0.0");
                when(mockCell28.getContents()).thenReturn("PCT");
                when(mockCell29.getContents()).thenReturn("0.0");
            }else {
            */
                when(mockCell1.getContents()).thenReturn("String1");
                when(mockCell2.getContents()).thenReturn("String2");
                when(mockCell3.getContents()).thenReturn("String3");
                when(mockCell4.getContents()).thenReturn("String");//Domain-DIST
                when(mockCell5.getContents()).thenReturn("String5");
                when(mockCell6.getContents()).thenReturn("String6");
                when(mockCell7.getContents()).thenReturn("String7");
                when(mockCell8.getContents()).thenReturn("String8");
                when(mockCell9.getContents()).thenReturn("String9");
                when(mockCell10.getContents()).thenReturn("String11");
                when(mockCell11.getContents()).thenReturn("String11");
                when(mockCell12.getContents()).thenReturn("String12");
                when(mockCell13.getContents()).thenReturn("String13");
                when(mockCell14.getContents()).thenReturn("String14");
                //}

                for (int row = 0; row < 25; row++) {
                    for (int col = 0; col < 25; col++) {

                    }
                }

                when(excelsheet.getCell(Mockito.anyInt(), Mockito.anyInt()))
                        .thenReturn(mockCell1)
                        .thenReturn(mockCell2)
                        .thenReturn(mockCell3)
                        .thenReturn(mockCell4)
                        .thenReturn(mockCell5)
                        .thenReturn(mockCell6)
                        .thenReturn(mockCell7)
                        .thenReturn(mockCell8)
                        .thenReturn(mockCell9)
                        .thenReturn(mockCell10)
                        .thenReturn(mockCell11)
                        .thenReturn(mockCell12)
                        .thenReturn(mockCell13)
                        .thenReturn(mockCell14)
                        .thenReturn(mockCell15)
                        .thenReturn(mockCell16)
                        .thenReturn(mockCell17)
                        .thenReturn(mockCell18)
                        .thenReturn(mockCell19)
                        .thenReturn(mockCell20)
                        .thenReturn(mockCell21)
                        .thenReturn(mockCell22)
                        .thenReturn(mockCell23)
                        .thenReturn(mockCell24)
                        .thenReturn(mockCell25)
                        .thenReturn(mockCell26)
                        .thenReturn(mockCell27)
                        .thenReturn(mockCell28)
                        .thenReturn(mockCell29);

                when(writabelExcelsheet.getCell(Mockito.anyInt(), Mockito.anyInt())).thenReturn(mockCell1);
            }
        }catch(Exception e){
            e.printStackTrace();
        }







        try{

try{            Mockito.mockStatic(org.apache.poi.ss.usermodel.Workbook.class); }catch(Exception e){}
            org.apache.poi.ss.usermodel.Workbook workBook = mock(org.apache.poi.ss.usermodel.Workbook.class);

            org.apache.poi.ss.usermodel.Sheet excelsheet = mock(org.apache.poi.ss.usermodel.Sheet.class);
            org.apache.poi.ss.usermodel.Row excelRow = mock(org.apache.poi.ss.usermodel.Row.class);

            org.apache.poi.ss.usermodel.Cell mockCell = mock(org.apache.poi.ss.usermodel.Cell.class);

        //    when(org.apache.poi.ss.usermodel.Workbook.createWorkbook(Mockito.<File>any())).thenReturn(writableWorkbook);

          //  when(Workbook.getWorkbook(Mockito.<File>any())).thenReturn(workBook);
            when(workBook.getNumberOfSheets()).thenReturn(2);
            when(workBook.getSheet(Mockito.any())).thenReturn(excelsheet);
            when(excelsheet.getRow(Mockito.anyInt())).thenReturn(excelRow) ;


            ArrayList<org.apache.poi.ss.usermodel.Row> listt = new ArrayList() ;
            listt.add(excelRow) ;

            final Iterator<Row> iter = listt.iterator() ;


            when(excelsheet.rowIterator()).thenReturn(iter) ;
            when(excelsheet.iterator()).thenReturn(iter) ;

/*

            doNothing().when(StreamingReader.builder()
                    .rowCacheSize(anyInt()));

            doNothing().when(StreamingReader.builder()
                    .bufferSize(anyInt()));
*/


           /* when(StreamingReader.builder()
                    .rowCacheSize(anyInt())    // number of rows to keep in memory (defaults to 10)
                    .bufferSize(anyInt())     // buffer size to use when reading InputStream to file (defaults to 1024)
                   .open(Mockito.any(InputStream.class))).thenReturn(workBook);
*/

try{            mockStatic(StreamingReader.class) ; }catch(Exception e){}
            StreamingReader.Builder builder = mock(StreamingReader.Builder.class);

            when(StreamingReader.builder()
                    ).thenReturn(builder);

            when(builder.bufferSize(anyInt())).thenReturn(builder);
            when(builder.rowCacheSize(anyInt())).thenReturn(builder);

           /* when(builder
                    .open(Mockito.any(InputStream.class))).thenReturn(workBook);
*/
            doReturn(workBook).when(builder).open(Mockito.any(InputStream.class)) ;





            when(workBook.getNumberOfSheets()).thenReturn(4);

           when(workBook.getSheetAt(Mockito.anyInt())).thenReturn(excelsheet) ;


            final String dir = "/Users/com/data1/";//Constants.getProperty("UploadBatchUserFilePath"); // Upload
            InputStream isDummy = null;
            isDummy = new FileInputStream(new File(dir+"abcd.xls"));

           org.apache.poi.ss.usermodel.Workbook workbookDummy = StreamingReader.builder()
                   .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                   .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                   .open(isDummy);

          /* org.apache.poi.ss.usermodel.Sheet excelsheet2 = workbookDummy.getSheetAt(0);
            for(Row r : excelsheet2) {

                System.out.println("row "+r);
            }*/

            //when(excelsheet.getRows()).thenReturn(10);
//            when(excelsheet.getColumns()).thenReturn(25);

            //when(mockCell.getContents()).thenReturn("String");
           // when(excelsheet.getCell(Mockito.anyInt(), Mockito.anyInt())).thenReturn(mockCell);
           // when(writabelExcelsheet.getCell(Mockito.anyInt(), Mockito.anyInt())).thenReturn(mockCell);

        }catch(Exception e){
            e.printStackTrace();
        }




    }

    public static void init(String className){

        //closeStatic();
        SOURCE = className ;

        if(INITIALIZED == false) {

            INITIALIZED = true;
            initConfig();

            Mockito.mockStatic(PreferenceCache.class);
            Mockito.mockStatic(IDGenerator.class);
            try {
                when(IDGenerator.getNextID(TypesI.USERID, TypesI.ALL, "String")).thenReturn(1L);
            } catch (Exception e) {
            }

            Mockito.mockStatic(NetworkPrefixCache.class);
            NetworkPrefixVO prefixVO = new NetworkPrefixVO();
            prefixVO.setNetworkCode("String");
            prefixVO.setOperator("String");

            when(NetworkPrefixCache.getObject(Mockito.anyString())).thenReturn(prefixVO);

            //getOperator

            initProps();
            initialize();
            initialize2();
            initialize4();


            //   initConnections();

        }else{

            try{
                Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
            }catch(Exception e){

                try{
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);


                    BufferedWriter bw = new BufferedWriter(new FileWriter("D:/logs2.txt", true));
                    bw.write(e+""+e.getMessage());
                    bw.newLine();
                    bw.write("StackTrace: "+sw.toString());
                    bw.close();
                }catch(Exception ee){}

            }


        }
        //added on 10082023
        /*initProps();
        initialize();
        initialize2();
        initialize4();*/
        //added on 10082023


        initConnections();
        constructorInit();
    }

    public static ChannelAdminTransferVO getChannelAdminTransferVO (){
        ChannelAdminTransferVO actualChannelAdminTransferVO = new ChannelAdminTransferVO();
        actualChannelAdminTransferVO.setToParentUser("To Parent User");
        actualChannelAdminTransferVO.setUserId("42");

        return actualChannelAdminTransferVO ;
    }

    public static AlertCounterSummaryRequestVO getAlertCounterSummaryRequestVO(){
        AlertCounterSummaryRequestVO alertCounterSummaryRequestVO = new AlertCounterSummaryRequestVO();
        alertCounterSummaryRequestVO.setGeoCode("String") ;
        alertCounterSummaryRequestVO.setCatCode("String");
        alertCounterSummaryRequestVO.setDomainCode("String");

        return alertCounterSummaryRequestVO ;

    }

    public static DeleteCategoryRequestVO getDeleteCategoryRequestVO(){
        DeleteCategoryRequestVO obj =  new DeleteCategoryRequestVO();
        obj.setAgentAllowed("Y");
        obj.setCategoryCode("String");
        obj.setCategoryName("String");
        obj.setDomainCode("String");
        return obj;
    }

    public static UserVO getUserVO(){


        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        categoryVO.setAgentAllowed("Agent Allowed");
        categoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        categoryVO.setAgentCategoryCode("Agent Category Code");
        categoryVO.setAgentCategoryName("Agent Category Name");
        categoryVO.setAgentCategoryStatus("Agent Category Status");
        categoryVO.setAgentCategoryStatusList(new ArrayList());
        categoryVO.setAgentCategoryType("Agent Category Type");
        categoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        categoryVO.setAgentCp2pPayee("Cp2p Payee");
        categoryVO.setAgentCp2pPayer("Cp2p Payer");
        categoryVO.setAgentCp2pWithinList("Cp2p Within List");
        categoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        categoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        categoryVO.setAgentDomainName("Agent Domain Name");
        categoryVO.setAgentFixedRoles("Agent Fixed Roles");
        categoryVO.setAgentGatewayName("Agent Gateway Name");
        categoryVO.setAgentGatewayType("Agent Gateway Type");
        categoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        categoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        categoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        categoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        categoryVO.setAgentMaxLoginCount(3L);
        categoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        categoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        categoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        categoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        categoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        categoryVO.setAgentParentOrOwnerRadioValue("42");
        categoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        categoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        categoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        categoryVO.setAgentRoleName("Agent Role Name");
        categoryVO.setAgentRoleTypeList(new ArrayList());
        categoryVO.setAgentRolesMapSelected(new HashMap());
        categoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        categoryVO.setAgentServiceAllowed("Agent Service Allowed");
        categoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        categoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        categoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        categoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        categoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        categoryVO.setAllowedGatewayTypes(new ArrayList());
        categoryVO.setAuthenticationType("Type");
        categoryVO.setCategoryCode("Category Code");
        categoryVO.setCategoryName("Category Name");
        categoryVO.setCategorySequenceNumber(10);
        categoryVO.setCategoryStatus("Category Status");
        categoryVO.setCategoryType("Category Type");
        categoryVO.setCategoryTypeCode("Category Type Code");
        categoryVO.setCp2pPayee("Payee");
        categoryVO.setCp2pPayer("Payer");
        categoryVO.setCp2pWithinList("Within List");
        categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        categoryVO.setCreatedOn(java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        categoryVO.setDisplayAllowed("Display Allowed");
        categoryVO.setDomainAllowed("Domain Allowed");
        categoryVO.setDomainCodeforCategory("Domain Codefor Category");
        categoryVO.setDomainName("Domain Name");
        categoryVO.setDomainTypeCode("Domain Type Code");
        categoryVO.setFixedDomains("Fixed Domains");
        categoryVO.setFixedRoles("Fixed Roles");
        categoryVO.setGeographicalDomainSeqNo(1);
        categoryVO.setGrphDomainSequenceNo(1);
        categoryVO.setGrphDomainType("Grph Domain Type");
        categoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        categoryVO.setHierarchyAllowed("Hierarchy Allowed");
        categoryVO.setLastModifiedTime(1L);
        categoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        categoryVO.setMaxLoginCount(3L);
        categoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        categoryVO.setMaxTxnMsisdnInt(3);
        categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        categoryVO.setModifiedOn(java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        categoryVO.setModifyAllowed("Modify Allowed");
        categoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        categoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        categoryVO.setNumberOfCategoryForDomain(10);
        categoryVO.setOutletsAllowed("Outlets Allowed");
        categoryVO.setParentCategoryCode("Parent Category Code");
        categoryVO.setParentOrOwnerRadioValue("42");
        categoryVO.setProductTypeAllowed("Product Type Allowed");
        categoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        categoryVO.setRadioIndex(1);
        categoryVO.setRechargeByParentOnly("By Parent Only");
        categoryVO.setRecordCount(3);
        categoryVO.setRestrictedMsisdns("Restricted Msisdns");
        categoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        categoryVO.setSequenceNumber(10);
        categoryVO.setServiceAllowed("Service Allowed");
        categoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        categoryVO.setTransferToListOnly("Transfer To List Only");
        categoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        categoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        categoryVO.setUserIdPrefix("User Id Prefix");
        categoryVO.setViewOnNetworkBlock("View On Network Block");
        categoryVO.setWebInterfaceAllowed("Web Interface Allowed");

        SessionInfoVO sessionInfoVO = new SessionInfoVO();
        sessionInfoVO.setCookieID("Cookie ID");
        sessionInfoVO.setCurrentModuleCode("Current Module Code");
        sessionInfoVO.setCurrentPageCode("Current Page Code");
        sessionInfoVO.setCurrentPageName("Current Page Name");
        sessionInfoVO.setCurrentRoleCode("Current Role Code");
        sessionInfoVO.setMessageGatewayVO(new MessageGatewayVO());
        sessionInfoVO.setRemoteAddr("42 Main St");
        sessionInfoVO.setRemoteHost("localhost");
        sessionInfoVO.setRoleHitTimeMap(new HashMap());
        sessionInfoVO.setSessionID("Session ID");
        sessionInfoVO.setTotalHit(1L);
        sessionInfoVO.setUnderProcess(true);
        sessionInfoVO.setUnderProcessHit(1L);


        UserVO userVO = mock(UserVO.class);
        userVO.setActiveUserID("Active User ID");
        userVO.setActiveUserLoginId("42");
        userVO.setActiveUserMsisdn("Active User Msisdn");
        userVO.setActiveUserPin("Active User Pin");
        userVO.setAddCommProfOTFDetailId("42");
        userVO.setAddress1("42 Main St");
        userVO.setAddress2("42 Main St");

        List<UserBalancesVO> balanceList =  new ArrayList<>() ;

        UserBalancesVO balance =  new UserBalancesVO();
        balance.setUserID("String");
        balance.setBalance(10L);
        balance.setPreviousBalance(10L);
        balance.setProductCode("String");
        balance.setNetworkCode("String");
        balance.setLastTransferID("String");
        balance.setNetworkFor("String");

        balanceList.add(balance);

        userVO.setAgentBalanceList(balanceList);

        userVO.setAllowedDay(new String[]{"Allowed Days"});
        userVO.setAllowedDays("Allowed Days");
        userVO.setAllowedIps("Allowed Ips");
        userVO.setAllowedUserTypeCreation("Allowed User Type Creations");
        userVO.setAppintmentDate("2020-03-01");
        userVO.setAppointmentDate(java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setAssType("Ass Type");
        userVO.setAssoMsisdn("Asso Msisdn");


        userVO.setAssociatedGeographicalList(new ArrayList());

        ArrayList associateProdList = new ArrayList<>();
        ListValueVO prod = new ListValueVO("String","String");
        associateProdList.add(prod);
        userVO.setAssociatedProductTypeList(associateProdList);


        ArrayList assocServList = new ArrayList<>();


        ListValueVO service = new ListValueVO("String","String");
        assocServList.add(prod);
        userVO.setAssociatedServiceTypeList(assocServList);

        userVO.setAssociationCreatedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setAssociationModifiedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setAuthType("Type");
        userVO.setAuthTypeAllowed("Type Allowed");
        userVO.setBatchID("Batch ID");
        userVO.setBatchName("Batch Name");
        userVO.setBrowserType("Browser Type");
        userVO.setC2sMisFromDate(java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setC2sMisToDate(java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setCategoryCode("Category Code");
        userVO.setCategoryCodeDesc("Category Code Desc");
        userVO.setCategoryVO(categoryVO);
        userVO.setCity("Oxford");
        userVO.setCompany("Company");
        userVO.setConfirmPassword("iloveyou");
        userVO.setContactNo("Contact N0");
        userVO.setContactPerson("Contact Person");
        userVO.setCountry("GB");
        userVO.setCountryCode("GB");
        userVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        userVO.setCreatedByUserName("janedoe");
        userVO.setCreatedOn(java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setCreatedOnAsString("Jan 1, 2020 8:00am GMT+0100");
        userVO.setCreated_On("Jan 1, 2020 8:00am GMT+0100");
        userVO.setCreationType("Creation Type");
        userVO.setCurrentModule("Current Module");
        userVO.setCurrentRoleCode("Current Role Code");
        userVO.setDepartmentCode("Department Code");
        userVO.setDepartmentDesc("Department Desc");


        ArrayList deptList = new ArrayList<>();
        ListValueVO dept = new ListValueVO("String","String");
        deptList.add(dept);
        userVO.setDepartmentList(deptList);


        userVO.setDesignation("Designation");
        userVO.setDivisionCode("Division Code");
        userVO.setDivisionDesc("Division Desc");



        ArrayList devList = new ArrayList<>();
        ListValueVO dev = new ListValueVO("String","String");
        devList.add(dev);
        userVO.setDivisionList(devList);


        userVO.setDocumentNo("Document No");
        userVO.setDocumentType("Document Type");
        userVO.setDomainCodes(new String[]{"Domain Codes"});
        userVO.setDomainID("Domain ID");

        ArrayList daminList = new ArrayList<>();
        ListValueVO domain = new ListValueVO("String","String");
        daminList.add(domain);
        userVO.setDomainList(daminList);


        userVO.setDomainName("Domain Name");
        userVO.setDomainStatus("Domain Status");
        userVO.setDomainTypeCode("Domain Type Code");
        userVO.setEmail("jane.doe@example.org");
        userVO.setEmpCode("Emp Code");
        userVO.setExternalCode("External Code");
        userVO.setFax("Fax");
        userVO.setFirstName("Name");
        userVO.setFromTime("jane.doe@example.org");
        userVO.setFxedInfoStr("Fxed Info Str");
        userVO.setGeographicalAreaList(new ArrayList<>());
        userVO.setGeographicalCode("Geographical Codes");
        userVO.setGeographicalCodeArray(new String[]{"Geographical Code Arrays"});
        userVO.setGeographicalCodeStatus("Geographical Code Status");
        userVO.setGeographicalList(new ArrayList());
        userVO.setGrphDomainTypeName("Grph Domain Type Names");
        userVO.setInfo1("Info1");
        userVO.setInfo10("Info10");
        userVO.setInfo11("Info11");
        userVO.setInfo12("Info12");
        userVO.setInfo13("Info13");
        userVO.setInfo14("Info14");
        userVO.setInfo15("Info15");
        userVO.setInfo2("Info2");
        userVO.setInfo3("Info3");
        userVO.setInfo4("Info4");
        userVO.setInfo5("Info5");
        userVO.setInfo6("Info6");
        userVO.setInfo7("Info7");
        userVO.setInfo8("Info8");
        userVO.setInfo9("Info9");
        userVO.setInvalidPasswordCount(3);
        userVO.setIsSerAssignChnlAdm(true);
        userVO.setLanguage("en");
        userVO.setLastLoginOn(java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setLastModified(1L);
        userVO.setLastName("Name");
        userVO.setLatitude("Latitude");
        userVO.setLevel1ApprovedBy("Level1 Approved By");
        userVO.setLevel1ApprovedOn(java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setLevel2ApprovedBy("Level2 Approved By");
        userVO.setLevel2ApprovedOn(java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setLoggerMessage("Logger Message");
        userVO.setLoginID("Login ID");
        userVO.setLoginTime(java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setLongitude("Longitude");
        userVO.setMenuItemList(new ArrayList());
        userVO.setMessage("Not all who wander are lost");
        userVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        userVO.setModifiedOn(java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setModuleCodeString("Code String");
        userVO.setMsisdn("Msisdn");

        ArrayList<UserPhoneVO> UserPhoneVOList =  new ArrayList<>();

        UserPhoneVO phoneVO =  new UserPhoneVO();
        phoneVO.setPhoneLanguage("String");
        phoneVO.setCountry("String");
        phoneVO.setMsisdn("9999999999");
        phoneVO.setSmsPin("1357");

        UserPhoneVOList.add(phoneVO);

        userVO.setMsisdnList(UserPhoneVOList);



        userVO.setNetworkID("Network ID");
        userVO.setNetworkList(new ArrayList());
        userVO.setNetworkName("Network Name");
        userVO.setNetworkStatus("Network Status");
        userVO.setOTPValidated(true);
        userVO.setOldLastLoginOn(java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setOtfCount(3);
        userVO.setOtfValue(42L);
        userVO.setOwnerCategoryName("Owner Category Name");
        userVO.setOwnerCompany("Company");
        userVO.setOwnerID("Owner ID");
        userVO.setOwnerMsisdn("Owner Msisdn");
        userVO.setOwnerName("Owner Name");
        userVO.setP2pMisFromDate(java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setP2pMisToDate(java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setPageCodeString("Code String");
        userVO.setParentCategoryName("Parent Category Name");
        userVO.setParentID("Parent ID");
        userVO.setParentMsisdn("Parent Msisdn");
        userVO.setParentName("Parent Name");
        userVO.setPassword("iloveyou");
        userVO.setPasswordCountUpdatedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO
                .setPasswordModifiedOn(java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setPasswordModifyFlag(true);
        userVO.setPasswordReset("Password Reset");
        userVO.setPaymentType("Payment Type");
        userVO.setPaymentTypes("Payment Types");
        userVO.setPaymentTypes(new String[]{"Payment Types"});

        ArrayList<String> paymentTypesList = new ArrayList<>();
        paymentTypesList.add("String");
        userVO.setPaymentTypesList(paymentTypesList);


        userVO.setPinReset("Pin Reset");
        userVO.setPreviousStatus("Previous Status");
        userVO.setProductCodes(new String[]{"Product Codess"});

        ArrayList<ChannelTransferItemsVO>  productsList =  new ArrayList<>();

        ChannelTransferItemsVO channelTransferItemsVO = new ChannelTransferItemsVO();

        channelTransferItemsVO.setTransferID("String");
        channelTransferItemsVO.setPayableAmount(100L);

        productsList.add(channelTransferItemsVO);

        userVO.setProductsList(productsList);


        userVO.setReferenceID("Reference ID");
        userVO.setRemarks("Remarks");
        userVO.setRemoteAddress("42 Main St");
        userVO.setReportHeaderName("Report Header Name");
        userVO.setRequestType("Request Type");
        userVO.setRequetedByUserName("janedoe");
        userVO.setRestrictedMsisdnAllow("Restricted Msisdn Allow");
        userVO.setRoleFlag(new String[]{"Role Flags"});
        userVO.setRoleType("Role Types");
        userVO.setRolesMap(new HashMap());
        userVO.setRolesMapSelected(new HashMap());
        userVO.setRsaAllowed(true);
        userVO.setRsaFlag("Rsa Flag");
        userVO.setRsaRequired(true);
        userVO.setRsavalidated(true);
        userVO.setSegmentList(new ArrayList());
        userVO.setServiceList(new ArrayList());
        userVO.setServicesList(new ArrayList());
        userVO.setServicesTypes(new String[]{"Services Typess"});
        userVO.setSessionInfoVO(sessionInfoVO);
        userVO.setShortName("Short Name");
        userVO.setShowPassword("iloveyou");
        userVO.setSsn("123-45-678");
        userVO.setStaffUser(true);
        userVO.setStaffUserDetails(ChannelUserVO.getInstance());
        userVO.setState("MD");
        userVO.setStatus("Status");
        userVO.setStatusDesc("Status Desc");

        userVO.setStatusList(new ArrayList());

        userVO.setSuspendedByUserName("janedoe");
        userVO.setSuspendedOn(java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        userVO.setToTime("To Time");
        userVO.setUpdateSimRequired(true);
        userVO.setUserBalanceList(new ArrayList<>());
        userVO.setUserCode("User Code");
        userVO.setUserID("User ID");
        userVO.setUserLanguage("en");
        userVO.setUserLanguageDesc("en");
        userVO.setUserLanguageList(new ArrayList());
        userVO.setUserLoanVOList(new ArrayList<>());
        userVO.setUserName("janedoe");
        userVO.setUserNamePrefix("janedoe");
        userVO.setUserNamePrefixList(new ArrayList());
        userVO.setUserPhoneVO(UserPhoneVO.getInstance());
        userVO.setUserType("User Type");
        userVO.setUsingNewSTK(true);
        userVO.setValidRequestURLs("https://example.org/example");
        userVO.setValidStatus(1);
        userVO.setVoucherList(new ArrayList());
        userVO.setWebLoginID(" web Login ID");

        return userVO ;
    }
    public static UserVO getUserVO2(){
        UserVO userVO = mock(UserVO.class);
        when(userVO.getCategoryCode()).thenReturn("Category Code");
        when(userVO.getLoginID()).thenReturn("Login ID");
        when(userVO.getMsisdn()).thenReturn("Msisdn");
        when(userVO.getNetworkID()).thenReturn("Network ID");
        when(userVO.getUserID()).thenReturn("User ID");

        return userVO ;
    }

    public static SaveCategoryRequestVO getSaveCategoryRequestVO(){
        SaveCategoryRequestVO obj = new SaveCategoryRequestVO();
        obj.setAgentAllowed("Y");
        obj.setCategoryCode("String");
        obj.setCategoryName("String");
        obj.setCategoryStatus("Y");
        return obj;
    }
    public static ChannelUserVO getChannelUserVO(){
        ChannelUserVO channelUserVO = mock(ChannelUserVO.class);//ChannelUserVO.getInstance();

        ArrayList domainList =  new ArrayList();
        ListValueVO domainVO =  new ListValueVO();
        domainVO.setTypeName("Test Domain");
        domainVO.setCodeName("Test Domain Code");
        domainVO.setStatus("Y");
        domainVO.setType("Test");
        domainList.add(domainVO) ;
        when(channelUserVO.getDomainList()).thenReturn(domainList) ;

        CategoryVO catVO = new CategoryVO() ;
        catVO.setMultipleGrphDomains(PretupsI.YES);
        when(channelUserVO.getCategoryVO()).thenReturn(catVO) ;

        when(channelUserVO.getUserType()).thenReturn(PretupsI.USER_TYPE_CHANNEL);
        when(channelUserVO.getCategoryCode()).thenReturn(PretupsI.CATEGORY_CODE_RETAILER);
        when(channelUserVO.getNetworkID()).thenReturn("String");
        when(channelUserVO.getDomainID()).thenReturn("DISTB_CHAN");


    return channelUserVO ;

    }

    public static void closeStatic(){
       try{ mockStatic(OracleUtil.class).close(); }catch(Exception e){}
        try{ mockStatic(BTSLDateUtil.class).close(); }catch(Exception e){}
        try{ mockStatic(PretupsRestUtil.class).close(); }catch(Exception e){}
        try{ mockStatic(OAuthenticationUtil.class).close(); }catch(Exception e){}
        try{ Mockito.mockStatic(FileUtils.class).close(); }catch(Exception e){}
        try{ Mockito.mockStatic(Workbook.class).close(); }catch(Exception e){}
        try{ Mockito.mockStatic(org.apache.poi.ss.usermodel.Workbook.class).close(); }catch(Exception e){}
        try{ mockStatic(StreamingReader.class) .close(); }catch(Exception e){}
        try{ Mockito.mockStatic(PreferenceCache.class).close(); }catch(Exception e){}
        try{ Mockito.mockStatic(IDGenerator.class).close(); }catch(Exception e){}
        try{ Mockito.mockStatic(NetworkPrefixCache.class).close(); }catch(Exception e){}
    }
    public static void init(){
        //closeStatic();

        SOURCE = null;
        if(INITIALIZED == false) {

            INITIALIZED = true;
            initConfig();

            Mockito.mockStatic(PreferenceCache.class);
            Mockito.mockStatic(IDGenerator.class);
            try {
                when(IDGenerator.getNextID(TypesI.USERID, TypesI.ALL, "String")).thenReturn(1L);
            } catch (Exception e) {
            }

            Mockito.mockStatic(NetworkPrefixCache.class);
            NetworkPrefixVO prefixVO = new NetworkPrefixVO();
            prefixVO.setNetworkCode("String");
            prefixVO.setOperator("String");

            when(NetworkPrefixCache.getObject(Mockito.anyString())).thenReturn(prefixVO);

            //getOperator

            initProps();
            initialize();
            initialize2();
            initialize4();


         //   initConnections();

        }else{

            try{
                Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
            }catch(Exception e){

                try{
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);


                    BufferedWriter bw = new BufferedWriter(new FileWriter("D:/logs2.txt", true));
                    bw.write(e+""+e.getMessage());
                    bw.newLine();
                    bw.write("StackTrace: "+sw.toString());
                    bw.close();
                }catch(Exception ee){}

            }


        }
        //added on 10082023
        /*initProps();
        initialize();
        initialize2();
        initialize4();*/
        //added on 10082023


        initConnections();
        constructorInit();
    }
}
