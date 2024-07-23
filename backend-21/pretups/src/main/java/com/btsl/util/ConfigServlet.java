package com.btsl.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;

import com.btsl.user.businesslogic.ThreadCacheManagerCleanup;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;

import com.btsl.alarm.AlarmSender;
import com.btsl.alarm.OAMSimulator;
import com.btsl.common.BTSLBaseException;
import com.btsl.common.CertificateLoader;
import com.btsl.common.PretupsInputValidator;
import com.btsl.container.businesslogic.ContainerStartUPBL;
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
import com.btsl.pretups.channel.user.businesslogic.wallet.UserProductWalletMappingCache;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayForCategoryCache;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeProfileCache;
import com.btsl.pretups.iat.businesslogic.IATCountryMasterCache;
import com.btsl.pretups.iat.businesslogic.IATNWServiceCache;
import com.btsl.pretups.iccidkeymgmt.businesslogic.SimVenderCache;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.loyaltymgmt.businesslogic.LMSProfileCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.NetworkServicesCache;
import com.btsl.pretups.master.businesslogic.ServiceClassInfoByCodeCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.SubLookupsCache;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingCache;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
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
import com.btsl.pretups.user.businesslogic.UserDefaultCache;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.btsl.redis.pool.RedisConnectionPool;
import com.btsl.redis.util.HostPort;
import com.btsl.user.businesslogic.CellIdCache;
import com.btsl.user.businesslogic.UserStatusCache;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ConfigServlet extends HttpServlet {
    private static final Log _log = LogFactory.getLog(ConfigServlet.class);
    //  private String loggerConfigFile;
    /*
    private static String constantspropsfile;
    private static String kafkapropsfile;
    private static final String securityconstantspropsfile = "/WEB-INF/classes/configfiles/SecurityConstants.props";
	private static final String restfulconstantspropsfile = "/WEB-INF/classes/configfiles/RestfulConstants.props";
	private static final String clientblconstantspropsfile = "/WEB-INF/classes/configfiles/ClientBLConstants.props";
	private static final String postgresqueryconstantspropsfile ="/WEB-INF/classes/configfiles/queryConstants/postgresqueryconstants.props";
	private static final String oraclequeryconstantspropsfile ="/WEB-INF/classes/configfiles/queryConstants/oraclequeryconstants.props";
	*/
    //private String _instanceID;
    private static AlarmSender _alarmSender = null;
    private static OAMSimulator oamSim = null;

    @Override
    public void init(ServletConfig conf) throws ServletException {
        final String methodName = "init";
        String _instanceID=null;
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "ConfigServlet init() Entered ");

        }
        _log.info(methodName, "ConfigServlet init() Entered ");
        super.init(conf);
        final String serverInfo = getServletContext().getServerInfo();

        String constantspropsfile = null;
        String kafkapropsfile = null;
        String loggerConfigFile = null;
        String securityconstantspropsfile = "/WEB-INF/classes/configfiles/SecurityConstants.props";
        String restfulconstantspropsfile = "/WEB-INF/classes/configfiles/RestfulConstants.props";
        String clientblconstantspropsfile = "/WEB-INF/classes/configfiles/ClientBLConstants.props";
        String postgresqueryconstantspropsfile ="/WEB-INF/classes/configfiles/queryConstants/postgresqueryconstants.props";
        String oraclequeryconstantspropsfile ="/WEB-INF/classes/configfiles/queryConstants/oraclequeryconstants.props";
        String pretupsInputValidationpropsfile ="/WEB-INF/classes/configfiles/pretupsInputValidation.props";

        try {
            System.out.println(serverInfo);
            String serverHomePath = null;
            if (serverInfo.contains("Tomcat")) {
                serverHomePath = System.getProperty("catalina.base");
            } else if (serverInfo.contains("Jboss")) {
                serverHomePath = System.getProperty("jboss.server.name");
            } else if (serverInfo.contains("WebSphere")) {
                serverHomePath = System.getProperty("ServerName");
            }else{
                InitialContext ctx = new InitialContext();
                MBeanServer server = (MBeanServer)ctx.lookup("java:comp/env/jmx/runtime");
                ObjectName service = new ObjectName("com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean");
                ObjectName domain = (ObjectName)server.getAttribute(service, "DomainConfiguration");
                serverHomePath = server.getAttribute(domain, "RootDirectory") + "";
            }
            if (System.getProperty("os.name").contains("Windows")) {
                constantspropsfile = serverHomePath + "\\conf\\pretups\\Constants.props";
                kafkapropsfile = serverHomePath + "\\conf\\pretups\\KafkaConstants.props";
                loggerConfigFile = serverHomePath + "\\conf\\pretups\\LogConfig.props";
                securityconstantspropsfile = serverHomePath + "\\conf\\pretups\\SecurityConstants.props";
                restfulconstantspropsfile = serverHomePath + "\\conf\\pretups\\RestfulConstants.props";
                clientblconstantspropsfile = serverHomePath + "\\conf\\pretups\\ClientBLConstants.props";
                pretupsInputValidationpropsfile=serverHomePath + "\\conf\\pretups\\pretupsInputValidation.props";
                //postgresqueryconstantspropsfile = serverHomePath + "\\conf\\pretups\\postgresqueryconstants.props";
                //oraclequeryconstantspropsfile = serverHomePath + "\\conf\\pretups\\oraclequeryconstants.props";
            } else {
                constantspropsfile = serverHomePath + "/conf/pretups/Constants.props";
                kafkapropsfile = serverHomePath + "/conf/pretups/KafkaConstants.props";
                loggerConfigFile = serverHomePath + "/conf/pretups/LogConfig.props";
                securityconstantspropsfile = serverHomePath + "/conf/pretups/SecurityConstants.props";
                restfulconstantspropsfile = serverHomePath + "/conf/pretups/RestfulConstants.props";
                clientblconstantspropsfile = serverHomePath + "/conf/pretups/ClientBLConstants.props";
                pretupsInputValidationpropsfile=serverHomePath + "/conf/pretups/pretupsInputValidation.props";
                //postgresqueryconstantspropsfile = serverHomePath + "/conf/pretups/postgresqueryconstants.props";
                //oraclequeryconstantspropsfile = serverHomePath + "/conf/pretups/oraclequeryconstants.props";
            }
        } catch (Exception e) {
            _log.info(methodName, "Config servlet 1 constantspropsfile=" + constantspropsfile + "  loggerConfigFile=" + loggerConfigFile);
            _log.errorTrace(methodName, e);
        }
        _log.info(methodName, "Config servlet constantspropsfile=" + constantspropsfile + "  loggerConfigFile=" + loggerConfigFile);
        try {
            /** it's handled by logging.config props in application.properties in spring boot app
            try {
                org.apache.log4j.PropertyConfigurator.configure(loggerConfigFile);
            } catch (Exception e) {
                _log.info(methodName, "Config servlet loggerConfigFile=" + loggerConfigFile);
                _log.errorTrace(methodName, e);
                 throw new BTSLBaseException(this, methodName, "Unable to load LogConfig.props from the given path " + loggerConfigFile);
            }
            _log.info(methodName, "ConfigServlet After LogConfig properties and Before Constants.props");
             **/
            try {
                Constants.load(constantspropsfile);
            } catch (Exception e) {
                _log.info(methodName, "Config servlet constantspropsfile=" + constantspropsfile);
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, "Unable to load Constants.props from the given path " + constantspropsfile);
            }
            _log.info(methodName, "ConfigServlet After Constants properties And Before pretupsInputValidation.props file");

            try {
                PretupsInputValidator.getInstance().load(pretupsInputValidationpropsfile);
            } catch (Exception e) {
                _log.info(methodName, "Config servlet pretupsInputValidation.props=" + pretupsInputValidationpropsfile);
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, "Unable to load pretupsInputValidation.props from the given path " + pretupsInputValidationpropsfile);
            }
            _log.info(methodName, "ConfigServlet After pretupsInputValidation  properties And Before KafkaConstants.props file");


            try {
                Constants.loadKafkaConf(kafkapropsfile);
            } catch (Exception e) {
                _log.info(methodName, "Config servlet kafkapropsfile=" + kafkapropsfile);
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, "Unable to load Kafka.props from the given path " + kafkapropsfile);
            }
            _log.info(methodName, "ConfigServlet After KafkaConstants.props and before SecurityConstants.props");
            _instanceID = Constants.getProperty("INSTANCE_ID");
            _log.info(methodName, "ConfigServlet instanceID:" + _instanceID);
            try {
                InputStream in = new ClassPathResource(getInitParameter("securityconstantspropsfile")).getInputStream();
                SecurityConstants.load(in);

            } catch (Exception e) {
                _log.info(methodName, "Config servlet securityconstantspropsfile=" + getInitParameter("securityconstantspropsfile"));
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, "Unable to load securityconstants.props from the  classpath" + getInitParameter("securityconstantspropsfile"));
            }
            _log.info(methodName, "ConfigServlet After SecurityConstants and Before RestfulConstants properties");
            try {
                InputStream in = new ClassPathResource(getInitParameter("restfulconstantspropsfile")).getInputStream();
                RestfulConstants.load(in);
            } catch (Exception e) {
                _log.info(methodName, "Config servlet restfulconstantspropsfile=" + getInitParameter("restfulconstantspropsfile"));
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, "Unable to load restfulconstants.props from the  classpath" + getInitParameter("restfulconstantspropsfile"));
            }
            _log.info(methodName, "ConfigServlet After RestfulConstants and Before ClientBLConstants properties");
            try {
                InputStream in = new ClassPathResource(getInitParameter("clientblconstantspropsfile")).getInputStream();
                ClientBLConstants.load(in);
            } catch (Exception e) {
                _log.info(methodName, "Config servlet clientblconstantspropsfile=" + getInitParameter("clientblconstantspropsfile"));
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, "Unable to load clientblconstants.props from the  classpath" + getInitParameter("clientblconstantspropsfile"));
            }
            _log.info(methodName, "ConfigServlet After ClientBLConstants properties");
            _log.info(methodName, "ConfigServlet Before loading data in QueryConstants.props file");
            try {
                String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
                if (QueryConstants.DB_POSTGRESQL.equals(dbConnected) && !serverInfo.contains("WebLogic"))
                    try{
                        InputStream in = new ClassPathResource(getInitParameter("postgresqueryconstantspropsfile")).getInputStream();
                        QueryConstants.load(in);
                        _log.info(methodName, "Config servlet postgresqueryconstantspropsfile loaded from classpath" + getInitParameter("postgresqueryconstantspropsfile"));
                    } catch (Exception e) {
                        _log.info(methodName, "Config servlet postgresqueryconstantspropsfile=" + getInitParameter("postgresqueryconstantspropsfile"));
                        _log.errorTrace(methodName, e);
                        throw new BTSLBaseException(this, methodName, "Unable to load postgresqueryconstants.props from the  classpath" + getInitParameter("postgresqueryconstantspropsfile"));
                    }
                else if(QueryConstants.DB_ORACLE.equals(dbConnected) && !serverInfo.contains("WebLogic"))
                    try{
                        InputStream in = new ClassPathResource(getInitParameter("oraclequeryconstantspropsfile")).getInputStream();
                        QueryConstants.load(in);
                        _log.info(methodName, "Config servlet oraclequeryconstantspropsfile loaded from classpath/" + getInitParameter("oraclequeryconstantspropsfile"));
                    } catch (Exception e) {
                        _log.info(methodName, "Config servlet oraclequeryconstantspropsfile=" + getInitParameter("oraclequeryconstantspropsfile"));
                        _log.errorTrace(methodName, e);
                        throw new BTSLBaseException(this, methodName, "Unable to load oraclequeryconstants.props from the  classpath/" + oraclequeryconstantspropsfile);
                    }
                else if(QueryConstants.DB_POSTGRESQL.equals(dbConnected) && serverInfo.contains("WebLogic"))
                    QueryConstants.load(postgresqueryconstantspropsfile);
                else if(QueryConstants.DB_ORACLE.equals(dbConnected) && serverInfo.contains("WebLogic"))
                    QueryConstants.load(oraclequeryconstantspropsfile);
                else
                    QueryConstants.loadDefault(oraclequeryconstantspropsfile);
            } catch (Exception e) {
                _log.info(methodName, "Config servlet postgresqueryconstantspropsfile=" + postgresqueryconstantspropsfile + " oraclequeryconstantspropsfile="+oraclequeryconstantspropsfile);
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, "Unable to load database specific property file ");
            }
            Connection con = null;
            try{
                if(PretupsI.TRUE.equals(getInitParameter("pretups.auto.instance.creation"))) {
                    con = OracleUtil.getSingleConnection();
                    String instanceId = ContainerStartUPBL.initializeContainer(con, System.getProperty("pretups.startup.port"),
                            System.getProperty("pretups.instance.type"), System.getProperty("pretups.instance.module"));
                    con.commit();
                    if(!BTSLUtil.isNullString(instanceId)){
                        StringBuffer text = new StringBuffer("\nRST_INSTANCE_ID=").append(instanceId);
                        text.append("\nDEF_INSTANCE_ID=").append(instanceId);
                        text.append("\nINSTANCE_ID=").append(instanceId).append("\n");
                        Files.write(Paths.get(constantspropsfile), text.toString().getBytes(), StandardOpenOption.APPEND);
                    }else
                        throw new BTSLBaseException(this, methodName, "Unable to find available instance_id into the instance_load pool ");
                    Constants.load(constantspropsfile); //reload Constants.props
                    _instanceID = Constants.getProperty("INSTANCE_ID");
                    _log.info(methodName, "ConfigServlet instanceID after reload of Constants.props :" + _instanceID);
                }else if(System.getProperty("pretups.instance.id") != null){
                    con = OracleUtil.getSingleConnection();
                    String instanceId = System.getProperty("pretups.instance.id").trim();
                    int result = 0;
                    if(!BTSLUtil.isNullString(instanceId)){
                        result = ContainerStartUPBL.updateContainer(con, System.getProperty("pretups.startup.port"),
                                System.getProperty("pretups.instance.type"), System.getProperty("pretups.instance.module"), instanceId); //System.getenv().get("pretups.http.port")
                        con.commit();
                    }
                    if(!BTSLUtil.isNullString(instanceId) && result > 0){
                        StringBuffer text = new StringBuffer("\nRST_INSTANCE_ID=").append(instanceId);
                        text.append("\nDEF_INSTANCE_ID=").append(instanceId);
                        text.append("\nINSTANCE_ID=").append(instanceId).append("\n");
                        Files.write(Paths.get(constantspropsfile), text.toString().getBytes(), StandardOpenOption.APPEND);
                    }else
                        throw new BTSLBaseException(this, methodName, "Unable to find available instance_id into the instance_load pool ");
                    Constants.load(constantspropsfile); //reload Constants.props
                    _instanceID = Constants.getProperty("INSTANCE_ID");
                    _log.info(methodName, "ConfigServlet instanceID after reload of Constants.props :" + _instanceID);
                }
            }catch (IOException e) {
                if(con != null)
                {
                    con.rollback();
                }
                _log.info(methodName, "Config servlet - Constants.props updatation failed for instance_id");
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, "Unable to update instance_id into Constants.props ");
            }catch(Exception e){
                if(con != null)
                {
                    con.rollback();
                }
                _log.info(methodName, "Config servlet - Constants.props updatation failed for instance_id");
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, "Unable to update instance_id into Constants.props ");
            }finally{
                if(con != null)
                {
                    con.close();
                }
                con = null;
            }
            _log.info(methodName, "ConfigServlet After instance_id update into Constants.props and Before Redis pool creation");
            try{
                if(PretupsI.REDIS_ENABLE.equals(Constants.getProperty("REDIS_ENABLE"))) {
                    HostPort.loadRedisServerDetailsAtStartup();
                    RedisConnectionPool.initializeSettings();
                }
            }catch(Exception e){
                _log.info(methodName, "Config servlet redis Server Details");
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, "Unable to initialize redis pool ");
            }
            PreferenceCache.loadPrefrencesOnStartUp();
            _alarmSender = new AlarmSender();
            _alarmSender.start();
            _log.info(methodName, "ConfigServlet After staring AlarmSender");
            if ("AES".equals(Constants.getProperty("ENDECRYPTION_TYPE"))) {
                final AESKeyStore aesKeyStore = new AESKeyStore();
                boolean credentialsLoad = false;
                String filePath = BTSLUtil.getFilePath(ConfigServlet.class);
                filePath = filePath.substring(0, filePath.lastIndexOf(File.separatorChar));
                credentialsLoad = aesKeyStore.LoadStoreKeyCredentials(filePath + File.separatorChar + "Credentials.txt");
                if (credentialsLoad == false || AESKeyStore.getKey() == null) {
                    throw new BTSLBaseException(this, methodName, "Unable to load Encryption keyLoad");
                }
            }

            ExecutorService executor = Executors.newFixedThreadPool(30);

            _log.info(methodName, "ConfigServlet After configuring PropertyConfigurator and before loading Products ");
            VomsProductsCache.loadProductsAtStartup();
            _log.info(methodName, "ConfigServlet After loading products and before loading Networks ");

            _log.info(methodName, "ConfigServlet After loading products and before loading Networks ");
            NetworkCache.loadNetworkAtStartup();
            _log.info(methodName, "ConfigServlet After loading Networks and before loading Lookups ");
            executor.execute(new LookupsCache());	//LookupsCache.loadLookAtStartup();
            _log.info(methodName, "ConfigServlet After loading Lookups and before loading sublookups cache ");
            executor.execute(new SubLookupsCache());	//LookupsCache.loadLookAtStartup();
            _log.info(methodName, "ConfigServlet After loading subLookups and before loading prefrences cache ");
            //PreferenceCache.loadPrefrencesOnStartUp();
            _log.info(methodName, "ConfigServlet After loading Preferences before loading Network prefixes ");
            executor.execute(new NetworkPrefixCache());	//NetworkPrefixCache.loadNetworkPrefixesAtStartup();
            _log.info(methodName, "ConfigServlet After loading Network prefixes before loading SystemPreferences");
            executor.execute(new SystemPreferences());	//SystemPreferences.load();
            _log.info(methodName, "ConfigServlet After loading SystemPreferences before loading KeywordCache");
            executor.execute(new ServiceKeywordCache());	//ServiceKeywordCache.loadServiceKeywordCacheOnStartUp();
            // ServiceInstancePriorityCache.loadServiceInstancePriorityCacheOnStartUp();
            //_log.info(methodName, "ConfigServlet After loading ServiceInstancePriorityCache before SimProfileCache ");
            executor.execute(new SimProfileCache());	//SimProfileCache.refreshSimProfileList();
            _log.info(methodName, "ConfigServlet After loading SimProfileCache before MSISDNPrefixInterfaceMappingCache ");
            executor.execute(new MSISDNPrefixInterfaceMappingCache());	//MSISDNPrefixInterfaceMappingCache.loadPrefixInterfaceMappingAtStartup();
            _log.info(methodName, "ConfigServlet After loading MSISDNPrefixInterfaceMappingCache before NetworkInterfaceModuleCache ");
            executor.execute(new NetworkInterfaceModuleCache());	//NetworkInterfaceModuleCache.loadNetworkInterfaceModuleAtStartup();
            _log.info(methodName, "ConfigServlet After loading NetworkInterfaceModuleCache before loading ServicePaymentMappingCache ");
            executor.execute(new ServicePaymentMappingCache());	//ServicePaymentMappingCache.loadServicePaymentMappingOnStartUp();
            _log.info(methodName, "ConfigServlet After loading ServicePaymentMappingCache  before loading TransferRulesCache  "); // table
            executor.execute(new TransferRulesCache());	//TransferRulesCache.loadTransferRulesAtStartup();
            _log.info(methodName, "ConfigServlet After loading TransferRulesCache before loading NetworkServicesCache ");
            executor.execute(new NetworkServicesCache());	//NetworkServicesCache.refreshNetworkServicesList();
            _log.info(methodName, "ConfigServlet After loading NetworkServicesCache  before loading MessageGatewayCache ");
            executor.execute(new MessageGatewayCache());	//MessageGatewayCache.loadMessageGatewayAtStartup();
            _log.info(methodName, "After loading MessageGatewayCache  before loading RequestInterfaceCache ");
            // RequestInterfaceCache.refreshRequestInterface();
            _log.info(methodName, "ConfigServlet After loading RequestInterfaceCache ");
            executor.execute(new FileCache());	//FileCache.loadAtStartUp();
            _log.info(methodName, "ConfigServlet After loading FileCache ");
            executor.execute(new NetworkProductServiceTypeCache());	//NetworkProductServiceTypeCache.refreshNetworkProductMapping();
            //executor.execute(new LookupsCache());	//NetworkProductServiceTypeCache.refreshProductServiceTypeMapping();
            _log.info(methodName, "ConfigServlet After loading Product service type cache ");
            final boolean fileRead = true;
            executor.execute(new LoadControllerCache(_instanceID, fileRead));	//LoadControllerCache.refreshInstanceLoad(_instanceID, fileRead);
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
            executor.execute(new SubscriberRoutingControlCache());	//SubscriberRoutingControlCache.refreshSubscriberRoutingControl();
            _log.info(methodName, "ConfigServlet After Loading Subscriber Routing Control Cache Before Registeration Contol Cache ");
            executor.execute(new InterfaceRoutingControlCache());	//InterfaceRoutingControlCache.refreshInterfaceRoutingControl();
            _log.info(methodName, "ConfigServlet After Interface Routing Control Cache ");
            executor.execute(new LocaleMasterCache());	//LocaleMasterCache.refreshLocaleMasterCache();
            _log.info(methodName, "ConfigServlet After loading local master Cache ");
            executor.execute(new RegistrationControlCache());	//RegistrationControlCache.refreshRegisterationControl();
            _log.info(methodName, "ConfigServlet After Loading Registeration Contol Cache ");
            executor.execute(new ServiceInterfaceRoutingCache());	//ServiceInterfaceRoutingCache.refreshServiceInterfaceRouting();
            _log.info(methodName, "ConfigServlet After Loading Service Interface Routing Cache ");
            executor.execute(new GroupTypeProfileCache());	//GroupTypeProfileCache.loadGroupTypeProfilesAtStartup();
            _log.info(methodName, "ConfigServlet After Loading Group type profile Cache ");
            executor.execute(new SimVenderCache());	//SimVenderCache.loadMessageGatewayAtStartup();
            _log.info(methodName, "ConfigServlet After Loading Sim Vender cache Cache ");
            executor.execute(new PaymentMethodCache());	//PaymentMethodCache.loadPaymentMethodCacheOnStartUp();
            _log.info(methodName, "ConfigServlet After Loading Payment Method Cache ");
            executor.execute(new ServiceSelectorMappingCache());	//ServiceSelectorMappingCache.loadServiceSelectorMappingCacheOnStartUp();
            _log.info(methodName, "ConfigServlet After Loading Service selector mapping Cache ");
            executor.execute(new IATCountryMasterCache());	//IATCountryMasterCache.loadIATCountryMasterCache();
            _log.info(methodName, "ConfigServlet After Loading IAT Country Master Cache ");
            executor.execute(new IATNWServiceCache());	//IATNWServiceCache.loadIATNWServiceCache();
            _log.info(methodName, "ConfigServlet After Loading IAT Network Cache ");
            executor.execute(new BonusBundleCache());	//BonusBundleCache.loadBonusBundleCacheOnStartUp();
            _log.info(methodName, "ConfigServlet After Bonus Bundle mapping Cache ");
            //MessagesCaches.load(LocaleMasterCache.getLocaleList());
            Boolean isHttpEnable = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.HTTPS_ENABLE);
            if (isHttpEnable) {
                _log.info(methodName, "Config Servlet before loading certificate ");
                executor.execute(new CertificateLoader());	//CertificateLoader.loadCertificateOnStartUp();
                _log.info(methodName, "Config Servlet after loading certificate ");
            }
            executor.execute(new NetworkProductCache());	//NetworkProductCache.loadNetworkProductMapAtStartup();
            _log.info(methodName, "ConfigServlet After Loading Network Product Cache ");
            executor.execute(new CardGroupCache());	//CardGroupCache.loadCardGroupMapAtStartup();
            _log.info(methodName, "ConfigServlet After Loading Card Group Cache ");
            executor.execute(new MessageGatewayForCategoryCache());	// MessageGatewayForCategoryCache.loadMeassageGatewayForCategoryMapAtStartup();
            _log.info(methodName, "ConfigServlet After Loading Message Gateway For Category Cache");
            executor.execute(new ServiceClassInfoByCodeCache());	//ServiceClassInfoByCodeCache.loadServiceClassByCodeMapAtStartup();
            _log.info(methodName, "ConfigServlet After Loading Service Class Info By Code Cache");
            executor.execute(new TransferProfileCache());	//TransferProfileCache.loadTransferProfileAtStartup();
            _log.info(methodName, "ConfigServlet After Loading Transfer Profile Cache ");
            executor.execute(new TransferProfileProductCache());	//TransferProfileProductCache.loadTransferProfileProductsAtStartup();
            _log.info(methodName, "ConfigServlet After Loading Transfer Profile Product Cache ");
            executor.execute(new UserDefaultCache());	//UserDefaultCache.loadUserDefaultConfigAtStartup();
            _log.info(methodName, "ConfigServlet After Loading User Default Cache ");
            executor.execute(new UserServicesCache());	//UserServicesCache.loadServicesAtStartup();
            _log.info(methodName, "ConfigServlet After Loading User Services Cache ");
            executor.execute(new ServiceSelectorInterfaceMappingCache());	//ServiceSelectorInterfaceMappingCache.loadServSelInterfMappingOnStartup();
            _log.info(methodName, "ConfigServlet After ServiceSelectorInterfaceMappingCache Cache ");
            executor.execute(new ServiceInstancePriorityCache());	//ServiceInstancePriorityCache.loadServiceInstancePriorityCacheOnStartUp();
            executor.execute(new CommissionProfileCache());	//CommissionProfileCache.loadCommissionProfilesAtStartup();
            executor.execute(new UserStatusCache());	//UserStatusCache.loadUserStatusDetailsAtStartUp();
            executor.execute(new LMSProfileCache());	//LMSProfileCache.loadLMSProfilesAtStartup();
            executor.execute(new CurrencyConversionCache());	//CurrencyConversionCache.loadCurrencyConversionDetailsAtStartUp();
            _log.info(methodName, "ConfigServlet After Currency Conversion Cache ");
            Boolean isUserProductMultipleWallet = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
            if (isUserProductMultipleWallet) {
                executor.execute(new UserProductWalletMappingCache());	//UserProductWalletMappingCache.loadUserProductWalletMappingOnStartUp();
            }
            executor.execute(new CommissionProfileMinCache());	//CommissionProfileMinCache.loadMinCommissionDetailsAtStartUp();
            _log.info(methodName, "ConfigServlet After CommissionProfileMinCache ");
            if (PretupsI.YES.equalsIgnoreCase(Constants.getProperty("NEW_OAM"))) {
                oamSim = new OAMSimulator(constantspropsfile);
                oamSim.startCallback();
            }
            executor.execute(new CellIdCache());	//CellIdCache.loadCellIdAtStartUp();
            _log.info(methodName, "ConfigServlet After CellIdCache ");

            executor.shutdown();
            while (!executor.isTerminated()) {
            }
            MComConnectionI mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            if(mcomCon != null)
            {
                mcomCon.close("ConfigServlet#init");
                mcomCon=null;
            }
            con = null;
            _log.info(methodName, "End ConfigServlet loading ................... ");
        }
        catch (BTSLBaseException exception) {
            System.err.println("ConfigServlet init() Exception " + exception);
            _log.errorTrace(methodName, exception);
            _log.errorTrace(methodName, exception);
        }  catch (Exception exception) {
            System.err.println("ConfigServlet init() Exception " + exception);
            _log.errorTrace(methodName, exception);
            _log.errorTrace(methodName, exception);
        }// end of catch

        try {
            _log.info("methodName", "starting AppDBResourceAnalyzer.startUtilizationThread");
            //AppDBResourceAnalyzer.startUtilizationThread();
        } catch (Exception e) {
            _log.info(methodName, "Exception while invoking AppDBResourceAnalyzer.startUtilizationThread");
        }


        _log.info(methodName, "ConfigServlet init() Exiting");
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
        final String methodName = "loadProcessCache";
        _log.debug(methodName, "ConfigServlet ::loadProcessCache(): Entered constantspropsfile=" + p_configFilePathName + "  loggerConfigFile=" + p_logConfileFilePathName);

        try {
            _log.info(methodName, "ConfigServlet ::loadProcessCache(): Before loading data in Constants.props file");
            Constants.load(p_configFilePathName);
            //postgres migration
            _log.info(methodName, "ConfigServlet Before loading data in QueryConstants.props file");
            try {
                String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
                if (QueryConstants.DB_POSTGRESQL.equals(dbConnected))
                    QueryConstants.load(Constants.getProperty("PostgresQuerypath"));
                else if(QueryConstants.DB_ORACLE.equals(dbConnected))
                    QueryConstants.load(Constants.getProperty("OracleQuerypath"));

                QueryConstants.loadDefault(Constants.getProperty("OracleQuerypath"));

            } catch (Exception e) {
                _log.info(methodName, "Config servlet postgresqueryconstantspropsfile=" + Constants.getProperty("PostgresQuerypath") + " oraclequeryconstantspropsfile="+Constants.getProperty("OracleQuerypath"));
                _log.errorTrace(methodName, e);

            }
            //postgres
            _log.info(methodName, "ConfigServlet ::loadProcessCache(): After Constants properties and before configuring PropertyConfigurator");
           // org.apache.log4j.PropertyConfigurator.configure(p_logConfileFilePathName);

            _alarmSender = new AlarmSender();
            _alarmSender.start();
            _log.info(methodName, "ConfigServlet After staring AlarmSender");
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
            _log.info(methodName, "ConfigServlet ::loadProcessCache(): After configuring PropertyConfigurator and before loading Networks ");

            // Generate Key object on system start-up if ENDECRYPTION_TYPE is
            // AES
            if ("AES".equals(Constants.getProperty("ENDECRYPTION_TYPE"))) {
                final AESKeyStore aesKeyStore = new AESKeyStore();
                boolean credentialsLoad = false;
                String filePath = BTSLUtil.getFilePath(ConfigServlet.class);
                filePath = filePath.substring(0, filePath.lastIndexOf(File.separatorChar));
                credentialsLoad = aesKeyStore.LoadStoreKeyCredentials(filePath + File.separatorChar + "Credentials.txt");
                if (credentialsLoad == false || AESKeyStore.getKey() == null) {
                    throw new BTSLBaseException("loadProcessCache", "Unable to load Encryption keyLoad");
                }
            }

            NetworkCache.loadNetworkAtStartup();
            _log.info(methodName, "ConfigServlet ::loadProcessCache(): After loading Networks and before loading prefrences cache");
            PreferenceCache.loadPrefrencesOnStartUp();
            _log.info(methodName, "ConfigServlet After loading prefrences cache and before loading SystemPreferences ");
            SystemPreferences.load();
            _log.info(methodName, "ConfigServlet ::loadProcessCache(): After loading SystemPreferences before loading MessageGatewayCache");
            MessageGatewayCache.loadMessageGatewayAtStartup();
            _log.info(methodName, "ConfigServlet ::loadProcessCache(): After loading MessageGatewayCache  before loading LacaleMasterCache ");
            LocaleMasterCache.refreshLocaleMasterCache();
            _log.info(methodName, "ConfigServlet ::loadProcessCache(): After loading local master Cache ");
            // ChangeID=LOCALEMASTER
            // Load messages cache
            MessagesCaches.load(LocaleMasterCache.getLocaleList());
            // Added for messageGatewayForCategoryCache
            MessageGatewayForCategoryCache.loadMeassageGatewayForCategoryMapAtStartup();
            _log.info(methodName, "ConfigServlet After Loading Message Gateway For Category Cache");

            TransferProfileCache.loadTransferProfileAtStartup();
            _log.info(methodName, "ConfigServlet After Loading Transfer Profile Cache ");

            TransferProfileProductCache.loadTransferProfileProductsAtStartup();
            _log.info(methodName, "ConfigServlet After Loading Transfer Profile Product Cache ");

            UserStatusCache.loadUserStatusDetailsAtStartUp();
            _log.info(methodName, "ConfigServlet After loading User Status Cache ");

            CommissionProfileCache.loadCommissionProfilesAtStartup();
            _log.info(methodName, "ConfigServlet After loading commission profile Cache ");

            CommissionProfileMinCache.loadMinCommissionDetailsAtStartUp();
            _log.info(methodName, "ConfigServlet After CommissionProfileMinCache ");

            // Added for NetworkProductCache
            NetworkProductCache.loadNetworkProductMapAtStartup();
            _log.info(methodName, "ConfigServlet After Loading Network Product Cache ");

            LookupsCache.loadLookAtStartup();
            _log.info(methodName, "ConfigServlet After Loading LookupsCache ");

            SubLookupsCache.loadSubLookAtStartup();;
            _log.info(methodName, "ConfigServlet After Loading SubLookupsCache ");

            final boolean fileRead = true;
            final String smsInstanceID = Constants.getProperty("INSTANCE_ID");
            if (!BTSLUtil.isNullString(smsInstanceID)) {
                LoadControllerCache.refreshInstanceLoad(smsInstanceID, fileRead);
            }

        }// end of try
        catch (BTSLBaseException exception) {
            _log.errorTrace(methodName, exception);
        } catch (Exception exception) {
            _log.errorTrace(methodName, exception);
        }// end of catch
        _log.info(methodName, "ConfigServlet ::loadProcessCache(): Exiting");
    }// end of loadProcessCache

    @Override
    public void destroy() {
        // this code use for send the logout request to IN and close all
        // connection from pool & storing
        // counter object's current state into file.
        _log.info("destroy", "ConfigServlet::inside the destroy method");
        LoadControllerCache.writeToFile();
        _alarmSender.SetRunningStatus(false);
        if(PretupsI.REDIS_ENABLE.equals(Constants.getProperty("REDIS_ENABLE"))) {
            if(RedisConnectionPool.getPoolInstance() != null)
                RedisConnectionPool.getPoolInstance().destroy();
        }
        // Stop AppDBResourceAnalyzer
        try {
            _log.info("destroy", "stopping AppDBResourceAnalyzer.stopUtilizationThread");
           // AppDBResourceAnalyzer.stopUtilizationThread();
        } catch (Exception e) {
            _log.info("destroy", "Exception while stopping AppDBResourceAnalyzer.stopUtilizationThread");
        }


    }

    /**
     * Method called from various process to destroy the depencies thread
     * created by load method
     *
     */
    public static void destroyProcessCache() {
        // Flag Set to stop the Alarm Sender Thread spawned in Load Process
        // cache method
        _alarmSender.SetRunningStatus(false);
        PushMessage.executor.shutdown();
    }

    public static AlarmSender getAlarmSender() {
        return _alarmSender;
    }

    public static void setAlarmSender(AlarmSender alarmSender) {
        _alarmSender = alarmSender;
    }
}// end of class

