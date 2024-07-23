package com.btsl.pretups.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.SimProfileCache;
import com.btsl.ota.services.businesslogic.UserServicesCache;
import com.btsl.pretups.cardgroup.businesslogic.BonusBundleCache;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupCache;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductCache;
import com.btsl.pretups.channel.user.businesslogic.wallet.UserProductWalletMappingCache;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayForCategoryCache;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeProfileCache;
import com.btsl.pretups.iat.businesslogic.IATCountryMasterCache;
import com.btsl.pretups.iat.businesslogic.IATNWServiceCache;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.util.InterfaceCloserController;
import com.btsl.pretups.loyaltymgmt.businesslogic.LMSProfileCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.NetworkServicesCache;
import com.btsl.pretups.master.businesslogic.ServiceClassInfoByCodeCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.SubLookupsCache;
import com.btsl.pretups.messages.businesslogic.MessagesManagementCache;
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
import com.btsl.pretups.routing.master.businesslogic.InterfaceRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.ServiceInterfaceRoutingCache;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.transfer.businesslogic.TransferRulesCache;
import com.btsl.pretups.user.businesslogic.UserDefaultCache;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.btsl.user.businesslogic.CellIdCache;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.MessagesCaches;

public class UpdateRedisCacheServlet  extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Field _log.
     */
    private static Log _log = LogFactory.getLog(UpdateRedisCacheServlet.class.getName());
    
    /**
     * Field loggerConfigFile.
     */
    private static String loggerConfigFile = null;
    
    private boolean isNPCacheUpdated = false;
    
    public UpdateRedisCacheServlet() {
        super();
    }
    
    /**
     * Method init.
     * 
     * @param conf
     *            ServletConfig
     * @throws ServletException
     * @see jakarta.servlet.Servlet#init(ServletConfig)
     */
    public void init(ServletConfig conf) throws ServletException {

        super.init(conf);

        final String methodName = "init";
        String serverInfo = getServletContext().getServerInfo();
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                if (serverInfo.contains("Tomcat")) {
                    loggerConfigFile = System.getProperty("catalina.base") + "\\conf\\pretups\\LogConfig.props";
                } else if (serverInfo.contains("Jboss")) {
                    loggerConfigFile = System.getProperty("jboss.server.name") + "\\conf\\pretups\\LogConfig.props";
                } else if (serverInfo.contains("WebSphere")) {
                    loggerConfigFile = System.getProperty("ServerName") + "\\conf\\pretups\\LogConfig.props";
                }
            } else {
                if (serverInfo.contains("Tomcat")) {
                    loggerConfigFile = System.getProperty("catalina.base") + "/conf/pretups/LogConfig.props";
                } else if (serverInfo.contains("Jboss")) {   
                    loggerConfigFile = System.getProperty("jboss.server.name") + "/conf/pretups/LogConfig.props";
                } else if (serverInfo.contains("WebSphere")) {
                    loggerConfigFile = System.getProperty("ServerName") + "/conf/pretups/LogConfig.props";
                }
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.info(methodName, "Config servlet 1   loggerConfigFile=" + loggerConfigFile);
        }
    }
    
    
    /**
     * Method destroy.
     * 
     * @see jakarta.servlet.Servlet#destroy()
     */
    public void destroy() {
        super.destroy();
    }
    
    public void updateFilePath(String serverInfo) {
        final String methodName = "updateFilePath";
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                if (serverInfo.contains("Tomcat")) {
                    loggerConfigFile = System.getProperty("catalina.base") + "\\conf\\pretups\\LogConfig.props";
                } else if (serverInfo.contains("Jboss")) {
                    loggerConfigFile = System.getProperty("jboss.server.name") + "\\conf\\pretups\\LogConfig.props";
                } else if (serverInfo.contains("WebSphere")) {
                    loggerConfigFile = System.getProperty("ServerName") + "\\conf\\pretups\\LogConfig.props";
                }
            } else {
                if (serverInfo.contains("Tomcat")) {
                    loggerConfigFile = System.getProperty("catalina.base") + "/conf/pretups/LogConfig.props";
                } else if (serverInfo.contains("Jboss")) {   
                    loggerConfigFile = System.getProperty("jboss.server.name") + "/conf/pretups/LogConfig.props";
                } else if (serverInfo.contains("WebSphere")) {
                    loggerConfigFile = System.getProperty("ServerName") + "/conf/pretups/LogConfig.props";
                }
            }
            _log.info(methodName, "Config servlet 1 loggerConfigFile=" + loggerConfigFile);
            
        }
            catch (Exception e) {
                _log.errorTrace(methodName, e);
                _log.info(methodName, "Config servlet 1 loggerConfigFile=" + loggerConfigFile);
            }
    	
    }

    /**
     * The doPost method of the servlet. <br>
     * This method is called when a form has its tag value method equals to get.
     * 
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String methodName = "doPost";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        final String cacheParam = "cacheParam";
        final String cacheAllParam = "cacheAll";
        BufferedReader br = null;
        ArrayList list = null;
        String cacheAll = null;
        String[] cacheValues = null;
        String msg[] = null;
        String msgF[] = null;
        int index = 0;
        int indexF = 0;
        ExecutorService executor = null;
        try{
            List<String> arrList = Collections.synchronizedList(new ArrayList<String>());
            List<String> arrListF = Collections.synchronizedList(new ArrayList<String>());
            Locale locale = BTSLUtil.getBTSLLocale(request);
        	   if ("WEB".equalsIgnoreCase(request.getParameter("fromWeb"))) {
        		   cacheAll = request.getParameter(cacheAllParam);
                   cacheValues = request.getParameterValues(cacheParam);
                   if (BTSLUtil.isNullString(cacheAll) && cacheValues == null ) {
                       request.setAttribute("messageStr", PretupsI.NODATA_ENTERED);
                       RequestDispatcher requestDispatcher = request.getRequestDispatcher("/jsp/master/updateCache.jsp");
                       requestDispatcher.forward(request, response);
                       return;
                   }
                   
                   msg = new String[cacheValues.length];
   				int poolSize = Integer.parseInt(Constants.getProperty("UPDATE_CACHE_THREAD_POOL_SIZE"));
   				executor = Executors.newFixedThreadPool(poolSize);
   				if (cacheValues != null) {
					for (int i = 0, k = cacheValues.length; i < k; i++) {
						if (cacheValues[i] != null) {
							executor.execute(new UpdateRedisCache(arrList, arrListF, cacheValues[i], locale));
						}
					}
   				}
   				
				executor.shutdown();
   				while (!executor.isTerminated()) {
				}
        	   }else{
               	String[] cacheValue = request.getParameterValues(cacheParam);
               	String[] localeValue = request.getParameterValues("locale");
               	String updateCacheErrMsg = updateCache(cacheValue[0], localeValue[0].toString());
                   return;
               }
        	   
        	   Connection con = null;
   	        MComConnectionI mcomCon = null;
   	        HashMap mp1 = new HashMap();
   			try{
   				mcomCon = new MComConnection();
   	        	con=mcomCon.getConnection();
   	        	CacheVO vo = null;
   	        	ArrayList cacheList = (new CacheDAO()).loadCacheList(con);
   	        	for (int a = 0; a < cacheList.size(); a++) {
   					vo = (CacheVO) cacheList.get(a);
   					mp1.put(vo.getCacheCode(), vo.getCacheName());
   				}
   			}catch(SQLException e){
   				 _log.error("updateCache", "Exceptin:e=" + e);
   		         _log.errorTrace(methodName, e);
   			}
   			finally{
   				if (mcomCon != null) {
   					mcomCon.close("UpdateCacheServlet#doPost");
   					mcomCon = null;
   				}
   			}
   			_log.debug(methodName, "CacheCode:CacheName" + mp1);
        	Collections.sort(arrList);
   			synchronized(arrList) {
   				msg = new String[arrList.size()];
   	            for(int i=0;i<arrList.size();i++){
   	            	String s1 = ((String)arrList.get(i));
   	            	StringBuffer s3=new StringBuffer();;
   	            	s3.append(mp1.get(s1));
           			msg[index++]=BTSLUtil.getMessage(locale, "error.updateserverCache.successmsg", new String[]{s3.toString()});
           			_log.debug(methodName, "CacheName:" + mp1.get(s1));
   	            	
   	            }
   			}
   			
   			Collections.sort(arrListF);
   			synchronized(arrListF) {
   				msgF = new String[arrListF.size()];
   	            for(int i=0;i<arrListF.size();i++){
   	            	String s1 = ((String)arrListF.get(i));
   	            	StringBuilder s3= new StringBuilder();
   	            	s3.append(mp1.get(s1));
           			msg[index++]=BTSLUtil.getMessage(locale, "error.updateserverCache.failmsg", new String[]{s3.toString()});
           			_log.debug(methodName, "CacheName:" + mp1.get(s1));
   	            	
   	            }
   			}
           	_log.info(methodName, "End UpdateCacheServlet  ................... ");
           	
           	if ("WEB".equalsIgnoreCase(request.getParameter("fromWeb")) && (msg != null || msgF != null)) {
                request.setAttribute("messageArr", msg);
                request.setAttribute("messageArrF", msgF);
                request.setAttribute("labelArr",new String[]{BTSLUtil.getMessage(locale, "updateserverCache.faillabel", null),BTSLUtil.getMessage(locale, "updateserverCache.successlabel", null)});
                RequestDispatcher requestDispatcher = request.getRequestDispatcher("/jsp/master/updateRedisCacheSuccess.jsp");
                requestDispatcher.forward(request, response);
            }
        	
        } catch (Exception e) {
            _log.error(methodName, "BTSLBaseException " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UpdateCacheServlet[doPost]", "", "", "", "Exception:" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exited ");
        }
    
    }
    
    
    
    
    /**
     * Method updateCache.
     * 
     * @param p_cacheId
     *            int
     * @param IP
     *            String
     * @param port
     *            String
     * @param p_locale
     *            String
     * @return String
     */
    public String updateCache(String p_cacheId, String p_locale) {
        final String methodName = "updateCache";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_cacheId=" + p_cacheId + " p_locale=" + p_locale);
        }

        InputStreamReader inputStreamReader = null;
        BufferedReader br = null;
        boolean flag = true;
        if (p_cacheId.equals(PretupsI.CACHE_ALL)) {
            p_cacheId = PretupsI.CACHE_NETWORK;
            flag = false;
        }

        StringBuffer cacheNotUpdated = new StringBuffer();

        switch (p_cacheId) {
        case PretupsI.CACHE_NETWORK:
            // Loading the network cache
            try {
                NetworkCache.updateNetwork();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Newtork Cache,");
                _log.error(methodName, "Exception Network cache " + e.getMessage());
            }
            if (flag) {
                // Loading the NetworkPrefixCache.
                try {
                    NetworkPrefixCache.updateNetworkPrefixes();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    cacheNotUpdated.append("Network Prefixes Cache,");
                    _log.error(methodName, "Exception NetworkPrefixCache cache " + e.getMessage());
                }
                break;
            }
        case PretupsI.CACHE_LOOKUPS:
            try {
                LookupsCache.updateData();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("LooKUps Cache,");
                _log.error(methodName, "Exception Look cache " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_PreferenceCache:
            try {
                PreferenceCache.updatePrefrences();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Preferences Cache,");
                _log.error(methodName, "Exception PreferenceCache cache " + e.getMessage());
            }
            try {
                SystemPreferences.reload();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("System Preferences Cache,");
                _log.error(methodName, "Exception SystemPreferences cache " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_NetworkPrefixCache:
            try {
                if (!isNPCacheUpdated) {
                    NetworkPrefixCache.updateNetworkPrefixes();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Network Prefixes Cache,");
                _log.error(methodName, "Exception NetworkPrefixCache cache " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_USER_WALLET_MAPPING:
            try {
                UserProductWalletMappingCache.loadUserProductWalletMappingOnStartUp();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("user Wallet  Cache,");
                _log.error(methodName, "Exception User Wallet cache " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_ServiceKeywordCache:
            try {
                ServiceKeywordCache.updateServiceKeywords();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Service Keyword Cache,");
                _log.error(methodName, "Exception ServiceKeywordCache cache " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_MSISDNPrefixInterfaceMappingCache:
            try {
                MSISDNPrefixInterfaceMappingCache.updatePrefixInterfaceMapping();
                if (!BTSLUtil.isNullString(Constants.getProperty("INTERFACE_CLOSER_IN_IDS"))) {
                    InterfaceCloserController.update("ALL");
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("MSISDN Prefixes Interface Mapping Cache,");
                _log.error(methodName, "Exception MSISDNPrefixInterfaceMappingCache cache " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_NetworkInterfaceModuleCache:
            try {
                NetworkInterfaceModuleCache.updateNetworkInterfaceModule();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Network Interface Module Cache,");
                _log.error(methodName, "Exception NetworkInterfaceModuleCache cache " + e.getMessage());
            }
            if (flag) {
                break;
            }

        case PretupsI.CACHE_ServicePaymentMappingCache:
            try {
                // ServicePaymentMappingCache.loadServicePaymentMappingOnStartUp();
                ServicePaymentMappingCache.updateServicePaymentMapping();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Service Payment Mapping Cache,");
                _log.error(methodName, "Exception ServicePaymentMappingCache cache " + e.getMessage());
            }
            if (flag) {
                break;
            }

        case PretupsI.CACHE_TransferRulesCache:
            try {
                TransferRulesCache.updateTransferRulesMapping();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Transfer Rules Cache,");
                _log.error(methodName, "Exception TransferRulesCache cache " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_MessageGatewayCache:
            try {
                MessageGatewayCache.updateMessageGateway();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Message Gateway Cache,");
                _log.error(methodName, "Exception MessageGatewayCache cache " + e.getMessage());
            }
            try {
                MessageGatewayCache.updateMessageGatewayMapping();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Message Gateway Mapping Cache,");
                _log.error(methodName, "Exception MessageGatewayCache cache " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_FileCache:
            try {
                FileCache.updateData();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("File Cache,");
                _log.error(methodName, "Exception FileCache " + e.getMessage());
            }
            if (flag) {
                break;
            }

        case PretupsI.CACHE_SIM_PROFILE:
            try {
                SimProfileCache.refreshSimProfileList();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Sim Profile Cache,");
                _log.error(methodName, "Exception SimProfileCache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_NETWORK_SERVICE_CACHE:
            try {
                NetworkServicesCache.refreshNetworkServicesList();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Network Services Cache,");
                _log.error(methodName, "Exception NetworkServicesCache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_NETWORK_PRODUCT_SERVICE_TYPE:
            try {
                NetworkProductServiceTypeCache.refreshNetworkProductMapping();
                NetworkProductServiceTypeCache.refreshProductServiceTypeMapping();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Network Product Mapping and Product Service Type Mapping Cache,");
                _log.error(methodName, "Exception NetworkProductServiceTypeCache  " + e.getMessage());
            }
            if (flag) {
                break;
            }

        case PretupsI.CACHE_ROUTING_CONTROL:
            try {
                SubscriberRoutingControlCache.refreshSubscriberRoutingControl();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Subscriber Routing Control Cache,");
                _log.error(methodName, "Exception SubscriberRoutingControlCache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_SERVICE_INTERFACE_MAPPING:
            try {
            	ServiceSelectorInterfaceMappingCache.updateServSelInterfMapping();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Service Selector Interface Mapping Cache,");
                _log.error(methodName, "Exception ServiceSelectorInterfaceMappingCache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_SERVICE_ROUTING:
            try {
                ServiceInterfaceRoutingCache.refreshServiceInterfaceRouting();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Service Interface Routing Cache,");
                _log.error(methodName, "Exception ServiceInterfaceRoutingCache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_REGISTRATION_CONTROL:
            try {
                RegistrationControlCache.refreshRegisterationControl();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Registeration Control Cache,");
                _log.error(methodName, "Exception RegistrationControlCache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_CONSTANT_PROPS:
            try {
            //    Constants.load(constantspropsfile);
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Constant Props,");
                _log.error(methodName, "Exception Constant Props  " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_LOGGER_CONFIG:
            try {
         //       org.apache.log4j.PropertyConfigurator.configure(loggerConfigFile);
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Logger Config ,");
                _log.error(methodName, "Exception LOGGER CONFIG  " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_MESSAGE:
            try {
                // ChangeID=LOCALEMASTER
                // Languagelist to load messages cache will now be loaded from
                // the locale master cache.
                MessagesCaches.reload(LocaleMasterCache.getLocaleList());
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Message Cache ,");
                _log.error(methodName, "Exception Message Cache " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_MESSAGE_RESOURCES:

        	boolean f = false;
        	InstanceLoadVO vo = null;
        	/*ArrayList instanceList = loadInstances();
        	for (int a = 0; a < instanceList.size(); a++) {
				vo = (InstanceLoadVO) instanceList.get(a);
				if(instanceID.equals(vo.getInstanceID()) && "WEB".equals(vo.getInstanceType())){
					f = true;
					break;
				}
					
			}*/
        	
            if (f) {
                StringBuffer buffer = new StringBuffer("http://");
                buffer.append(vo.getHostAddress());
                buffer.append(":");
                buffer.append(vo.getHostPort());
                buffer.append("/pretups/reloadMessageResource.do");
                if (!"en_US".equals(p_locale)) {
                    buffer.append("?locale=" + p_locale);
                }

                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Message Resource Cache Update URL=" + buffer.toString());
                }

                HttpURLConnection con = null;
                try {
                    URL url = new URL(buffer.toString());
                    URLConnection uc = url.openConnection();
                    con = (HttpURLConnection) uc;
                    con.setUseCaches(false);
                    con.setDoInput(true);
                    con.setDoOutput(true);

                    inputStreamReader = new InputStreamReader(uc.getInputStream());
                    br = new BufferedReader(inputStreamReader);
                    String str = null;
                    StringBuffer message = new StringBuffer();
                    while ((str = br.readLine()) != null) {
                        message.append(str);
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Message Resource Cache Update Status =" + message.toString());
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    cacheNotUpdated.append("Message Resource Cache ,");
                    _log.error(methodName, "Exception Message Cache " + e.getMessage());
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    if (inputStreamReader != null) {
                        try {
                            inputStreamReader.close();
                        } catch (IOException e) {
                            _log.errorTrace(methodName, e);
                        }
                    }
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            _log.errorTrace(methodName, e);
                        }
                    }
                }
            }
            if (flag) {
                break;
            }

        case PretupsI.CACHE_GROUP_TYPE_PROFILE:
            try {
                GroupTypeProfileCache.updateGroupTypeProfilesCache();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Group type profile cache,");
                _log.error(methodName, "Exception Group type profile cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
            /*
             * The new cache are added by ankit Z on date 3/8/6
             * First is for network interface module cache
             * Second is for interface routing control cache. This is used for
             * alternate routing
             */
        case PretupsI.CACHE_NETWORK_INTERFACE_MODULE:
            try {
                NetworkInterfaceModuleCache.updateNetworkInterfaceModule();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Network interface module cache,");
                _log.error(methodName, "Exception Network interface module cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }

        case PretupsI.CACHE_INTERFACE_ROUTING_CONTROL:
            try {
                InterfaceRoutingControlCache.refreshInterfaceRoutingControl();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Interface routing control cache,");
                _log.error(methodName, "Exception Interface routing control cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_PAYMENT_METHOD:
            try {
                PaymentMethodCache.updatePaymentMethod();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Payment method cache,");
                _log.error(methodName, "Exception Payment method cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_SERVICE_SELECTOR_MAPPING:
            try {
                ServiceSelectorMappingCache.loadServiceSelectorMappingCacheOnStartUp();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Service selector mapping cache,");
                _log.error(methodName, "Exception Service selector mapping cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }

        case PretupsI.CACHE_BONUS_BUNDLES:
            try {
                BonusBundleCache.loadBonusBundleCacheOnStartUp();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Service Bonus Bundle Cache,");
                _log.error(methodName, "Exception Service Bonus Bundle Cache:" + e.getMessage());
            }
            if (flag) {
                break;
            }

        case PretupsI.IAT_COUNTRY_MASTER_CACHE:
            try {
                IATCountryMasterCache.loadIATCountryMasterCache();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("IAT Country master cache,");
                _log.error(methodName, "Exception Service selector mapping cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.IAT_NETWORK_CACHE:
            try {
                IATNWServiceCache.loadIATNWServiceCache();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("IAT network service mapping cache,");
                _log.error(methodName, "Exception Service selector mapping cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }

        case PretupsI.CACHE_MESSAGE_MANAGEMENT:
            try {
                MessagesManagementCache.loadMessagManagementCache();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("IAT Country master cache,");
                _log.error(methodName, "Exception Service selector mapping cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_NETWORK_PRODUCT:
            try {
                NetworkProductCache.updateNetworkProductMap();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Network product cache,");
                _log.error(methodName, "Exception network product cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_CARD_GROUP:
            try {
                CardGroupCache.updateCardGroupMap();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Card Group cache,");
                _log.error(methodName, "Exception Card Group cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_MESSAGE_GATEWAY_CATEGORY:
            try {
                MessageGatewayForCategoryCache.updateMeassageGatewayForCategoryMap();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Message gateway for Category cache,");
                _log.error(methodName, "Exception messageGatewayForCategory cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }

        case PretupsI.CACHE_SERVICE_CLASS_CODE:
            try {
                ServiceClassInfoByCodeCache.updateServiceClassByCodeMap();;
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Service Class Info By Code Cache,");
                _log.error(methodName, "Exception ServiceClassInfoByCode cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.TRANSFER_PROFILE:
            try {
                TransferProfileCache.loadTransferProfileAtStartup();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Transfer Profile cache,");
                _log.error(methodName, "Exception Transfer Profile cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }

        case PretupsI.TRANSFER_PROFILE_PRODUCT:
            try {
                TransferProfileProductCache.updateTransferProfileProducts();;
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Transfer Profile Product cache,");
                _log.error(methodName, "Exception Transfer Profile Product cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }

        case PretupsI.CACHE_USER_DEFAULT:
            try {
                UserDefaultCache.updateUserDefaultConfig();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("User Default Config Mapping cache,");
                _log.error(methodName, "Exception User Default Config cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }

        case PretupsI.CACHE_USER_SERVICES:
            try {
                UserServicesCache.updateServicesMap();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("User Service  mapping cache,");
                _log.error(methodName, "Exception User Service mapping cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_COMMISSION_PROFILE:
            try {
                CommissionProfileCache.updateCommissionProfileMapping();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Commission Profile mapping cache,");
                _log.error(methodName, "Exception Commission Profile mapping cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_USER_ALLOWED_STATUS:
            try {
                UserStatusCache.updateUserStatusMapping();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Commission Profile mapping cache,");
                _log.error(methodName, "Exception Commission Profile mapping cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
        case PretupsI.CACHE_LMS_PROFILE:
            try {
                LMSProfileCache.updateLmsProfileMapping();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("LMS Profile cache,");
                _log.error(methodName, "LMS Profile cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
	 case PretupsI.CACHE_CURRENCY:
            try {
                CurrencyConversionCache.updateCurrencyConversionMapping();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                cacheNotUpdated.append("Currency Conversion cache,");
                _log.error(methodName, "Currency Conversion cache  " + e.getMessage());
            }
            if (flag) {
                break;
            }
	 case PretupsI.CACHE_CELL_ID:
         try {
             CellIdCache.loadCellIdAtStartUp();
         } catch (Exception e) {
             _log.errorTrace(methodName, e);
             cacheNotUpdated.append("Cell ID cache ,");
             _log.error(methodName, "Cell ID cache   " + e.getMessage());
         }
         if (flag) {
             break;
         }
	 case PretupsI.CACHE_SUB_LOOKUPS:
         try {
             SubLookupsCache.updateData();
         } catch (Exception e) {
             _log.errorTrace(methodName, e);
             cacheNotUpdated.append("SubLooKUps Cache,");
             _log.error(methodName, "Exception SubLook cache " + e.getMessage());
         }
         if (flag) {
             break;
         }
	 default:
     	 if(_log.isDebugEnabled()){
     		_log.debug("Default Value " ,p_cacheId);
     	 }
        }

        String str = null;
        if (cacheNotUpdated.indexOf(",") != -1) {
            str = cacheNotUpdated.substring(0, cacheNotUpdated.lastIndexOf(","));
        }

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting " + cacheNotUpdated.toString());
        }
        return str;
    
    }
}
