/**
 * @(#)UpdateCacheServlet.java
 * Copyright(c) 2005, Bharti Telesoft Ltd.
 * All Rights Reserved
 *
 * use to update the cacahe on multiple instances of our system. it may be WEB or SMS
 * 
 * Instances are made in our database with there IP and PORT
 * we make the URL connection on the diffrent instances and call 
 * this servlet with the parameter selected by user.
 * IN case if user select the ALL option  in JSP to update cache. In that case we pass All with query String.
 * otherwise we pass selcted Cache option to all servlet on diffrent instances.
 * 
 * Only one servlet will load all instances from INSTANCE_LOAD table and only this servlet will make 
 * URL connection with other servlet with selected options. We diffrentiate this (to load instances or not) on 
 * the base of one request parameter i.e. "fromWeb" . 
 * 
 *    
 *-------------------------------------------------------------------------------------------------
 * Author                        Date            History
 *-------------------------------------------------------------------------------------------------
 * avinash.kamthan              Jul 27, 2005        Initital Creation
 * Ankit Zindal					20/11/2006		ChangeID=LOCALEMASTER	
 *-------------------------------------------------------------------------------------------------
 *
 */

package com.btsl.pretups.common.clientcommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.StringTokenizer;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayForCategoryCache;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeProfileCache;
import com.btsl.pretups.iat.businesslogic.IATCountryMasterCache;
import com.btsl.pretups.iat.businesslogic.IATNWServiceCache;
import com.btsl.pretups.inter.util.InterfaceCloserController;
import com.btsl.pretups.loyaltymgmt.businesslogic.LMSProfileCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.NetworkServicesCache;
import com.btsl.pretups.master.businesslogic.ServiceClassInfoByCodeCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
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
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.MessagesCaches;
/**
 * @author avinash.kamthan
 */
public class InstanceWiseUpdateCacheServlet extends HttpServlet
{

    /**
     * Field log.
     */
    private static final Log log = LogFactory
			.getLog(InstanceWiseUpdateCacheServlet.class.getName());
    /**
     * Field constantspropsfile.
     */
    private static String constantspropsfile = null;
    /**
     * Field loggerConfigFile.
     */
    private static String loggerConfigFile = null;
    /**
     * Field instanceID.
     */
    
	private static String ServerName=null;
    /**
     * Boolean decides whether to update NetworkPrefixCache individual, on selecting the NetworkCache and NetworkPrefixCache together to update 
     */

    /**
     * Constructor for UpdateCacheServlet.
     */
    public InstanceWiseUpdateCacheServlet()
    {
        super();
    }


    /**
     * Method init.
     * @param conf ServletConfig
     * @throws ServletException
     * @see jakarta.servlet.Servlet#init(ServletConfig)
     */
    @Override
    public void  init(ServletConfig conf) throws ServletException
    {
      
        super.init(conf);
		
		final String methodName = "init";
		final String jboseServerName="jboss.server.name";
		final String catalinabase="catalina.base";
		final String constantsFile="\\conf\\pretups\\Constants.props";
		final String logConfigFile="\\conf\\pretups\\LogConfig.props";
		String serverInfo = getServletContext().getServerInfo();
		try{
		if(System.getProperty("os.name").contains("Windows")){
			if(serverInfo.contains("Tomcat")){
				constantspropsfile=System.getProperty(catalinabase)+constantsFile;
				loggerConfigFile=System.getProperty(catalinabase)+logConfigFile;
			}else if(serverInfo.contains("Jboss")){
				constantspropsfile=System.getProperty(jboseServerName)+constantsFile;
				loggerConfigFile=System.getProperty(jboseServerName)+logConfigFile;
			}else if(serverInfo.contains("WebSphere")){
				constantspropsfile=System.getProperty("ServerName")+constantsFile;
				loggerConfigFile=System.getProperty("ServerName")+logConfigFile;
			}
		}else{
			if(serverInfo.contains("Tomcat")){
				constantspropsfile=System.getProperty(catalinabase)+"/conf/pretups/Constants.props";
				loggerConfigFile=System.getProperty(catalinabase)+"/conf/pretups/LogConfig.props";
			}else if(serverInfo.contains("Jboss")){
				constantspropsfile=System.getProperty(jboseServerName)+"/conf/pretups/Constants.props";
				loggerConfigFile=System.getProperty(jboseServerName)+"/conf/pretups/LogConfig.props";
			}else if(serverInfo.contains("WebSphere")){
				constantspropsfile=getInitParameter("constantspropsfile");
				loggerConfigFile=getInitParameter("loggerConfigFile");
			}
		}
		}catch(Exception e){
			log.errorTrace(methodName,e);
			log.info(methodName, "Config servlet 1 constantspropsfile="+constantspropsfile+"  loggerConfigFile="+loggerConfigFile);
		}
    }

    /**
     * Method destroy.
     * @see jakarta.servlet.Servlet#destroy()
     */
    @Override
    public void destroy()
    {
        super.destroy();
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
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	final String methodName = "doGet";
    	final String fromWeb="fromWeb";
    	boolean isNPCacheUpdated=false;
    	LogFactory.printLog(methodName, "Entered", log);
        
        String localString = null;
      
        try
        {
        	 String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
             String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
            String []msg =  null;
            String msgSms =  "";
			int index = -1;
            String ip = null;
            String port = null;

            	boolean isWeb = false;
                ip=request.getParameter("SERVER_IP");
           
                if(request.getParameter("SERVER_PORT")!=null)
                    port=request.getParameter("SERVER_PORT");
                ServerName=ip+":"+port;
                if("WEB".equalsIgnoreCase(request.getParameter(fromWeb)))
                {
                	ip = "127.0"+".0.1";
                    isWeb = true; 
                }    
                Locale locale=null;
             
                if (!BTSLUtil.isNullString(request.getParameter("locale")))
                {
                	String[] localeArr=request.getParameter("locale").split("_");
                	if(localeArr.length!=2)
                	{
                		locale=new Locale(lang,country);
                	}else{
                		locale=new Locale(localeArr[0],localeArr[1]);	
                	}
                	
                } else if(!BTSLUtil.isNullString(localString)){
                	String[] localeArr=localString.split("_");
                	if(localeArr.length!=2)
                	{
                		locale=new Locale(lang,country);
                	}else{
                		locale=new Locale(localeArr[0],localeArr[1]);	
                	}
                	
                }else{
                	locale=new Locale(lang,country);
                }
               
                StringBuilder errorInUpdateCache = new StringBuilder();
               
                	try{
                	if(!BTSLUtil.isNullString(Constants.getProperty("UPDATE_CACHE_ALL_PARAMETER")))
            		{
                		StringTokenizer st = new StringTokenizer(Constants.getProperty("UPDATE_CACHE_ALL_PARAMETER"),",");  
                		 while (st.hasMoreTokens()) {  
                	        String updateCacheErrMsg = updateCache(st.nextToken(), ip, port, locale.toString(),isWeb,isNPCacheUpdated);
                             if(updateCacheErrMsg != null)
                                 errorInUpdateCache.append(updateCacheErrMsg);
                          } 
            		}
                	else
                	{
                		String updateCacheErrMsg = "No Cache id Value found in UPDATE_CACHE_ALL_PARAMETER or UPDATE_CACHE_ALL_PARAMETER is not defined constant.props";
                        if(updateCacheErrMsg != null)
                            errorInUpdateCache.append(updateCacheErrMsg);
                		
                	}
                	}catch(Exception e)
                	{
                		  log.error(methodName, "BTSLBaseException " + e.getMessage());
                          log.errorTrace(methodName,e);
                		
                		String updateCacheErrMsg = "No Cache id Value found in UPDATE_CACHE_ALL_PARAMETER or UPDATE_CACHE_ALL_PARAMETER is not defined constant.props";
                        if(updateCacheErrMsg != null)
                            errorInUpdateCache.append(updateCacheErrMsg);
                		
                	}
                if (!"WEB".equalsIgnoreCase(request.getParameter(fromWeb)))
                {
                    PrintWriter out = response.getWriter();
                    if(errorInUpdateCache != null && errorInUpdateCache.length() > 0  )
                    {
                    	String[] messageArgArray={ServerName,errorInUpdateCache.toString()};
                    	
                    	msgSms=BTSLUtil.getMessage(locale,"error.updateserverCache.failmsg",messageArgArray);
                        out.print(msgSms);

                    }
                    else
                    {
                    	String[] messageArgArray={ServerName};
                    	msgSms=BTSLUtil.getMessage(locale,"error.updateserverCache.successmsg",messageArgArray);
                        out.print(msgSms);
                    }
                }
                else
                {
                	// self cache message on web  
                	msg = new String[1];
                	index = 0;
                	if(errorInUpdateCache != null && errorInUpdateCache.length() > 0  )
                	{
                		String[] messageArgArray={ServerName,errorInUpdateCache.toString()};
                		msg[index]=BTSLUtil.getMessage(locale,"error.updateserverCache.failmsg",messageArgArray);
                	}
                	else
                	{
                		String[] messageArgArray={ServerName};
                		msg[index] = BTSLUtil.getMessage(locale,"error.updateserverCache.successmsg",messageArgArray);
                	}
                }
                

                /**
                 * Forward the request to web
                 */
                /*if ("WEB".equalsIgnoreCase(request.getParameter(fromWeb)) && msg != null)
                {
                    // forward the request to particular jsp
                    request.setAttribute("messageArr",msg);
                    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/jsp/master/updateCacheSuccess.jsp");
                    requestDispatcher.forward(request,response);
                }  */ 
            
        } catch (Exception e)
        {
            log.error(methodName, "BTSLBaseException " + e.getMessage());
            log.errorTrace(methodName,e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UpdateCacheServlet[doPost]", "", "", "", "Exception:" + e.getMessage());
        }
    	LogFactory.printLog(methodName, "Exited", log);
        
    }

    

    /**
     * Method updateCache.
     * @param p_cacheId int
     * @param IP String
     * @param port String
     * @param p_locale String
     * @return String
     */
    private String updateCache(String cacheId, String ip, String port, String locale,boolean isWeb,boolean isNPCacheUpdatedTemp)
    {

    	final String methodName = "updateCache";

    	LogFactory.printLog(methodName, "Entered p_cacheId=" + cacheId+" IP="+ip+" port="+port+" p_locale="+locale,log);

    	InputStreamReader inputStreamReader = null;
    	BufferedReader br = null;
    	boolean isNPCacheUpdated=isNPCacheUpdatedTemp;
    	StringBuilder cacheNotUpdated = new StringBuilder();

    	switch (cacheId)
    	{

    	case PretupsI.CACHE_ALL:
    		//all cache update method
    		updateAllCache(ip,port,locale,isWeb,isNPCacheUpdated);
    		break;
    	case PretupsI.CACHE_NETWORK:
    		//Loading the network cache
    		try
    		{
    			NetworkCache.updateNetwork();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Newtork Cache,");  log.error(methodName, "Exception Network cache " + e.getMessage()); }
    		//Loading the NetworkPrefixCache.
    		try
    		{
    			NetworkPrefixCache.updateNetworkPrefixes();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Network Prefixes Cache:,");log.error(methodName, "Exception:: NetworkPrefixCache cache " + e.getMessage()); }
    		break;
    	case PretupsI.CACHE_LOOKUPS:
    		try
    		{
    			LookupsCache.updateData();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("LooKUps Cache,");log.error(methodName, "Exception Look cache " + e.getMessage()); }
    		break;
    	case PretupsI.CACHE_PreferenceCache:
    		try
    		{
    			PreferenceCache.updatePrefrences();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Preferences Cache,");log.error(methodName, "Exception PreferenceCache cache " + e.getMessage()); }
    		try
    		{
    			SystemPreferences.reload();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("System Preferences Cache,");log.error(methodName, "Exception SystemPreferences cache " + e.getMessage()); }
    		break;
    	case PretupsI.CACHE_NetworkPrefixCache:
    		try
    		{
    			if(!isNPCacheUpdated)
    				NetworkPrefixCache.updateNetworkPrefixes();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Network Prefixes Cache ,");log.error(methodName, "Exception :NetworkPrefixCache cache " + e.getMessage()); }
    		break;
    	case PretupsI.CACHE_USER_WALLET_MAPPING:
    		try {
    			UserProductWalletMappingCache.loadUserProductWalletMappingOnStartUp();
    		} catch (Exception e) {
    			log.errorTrace(methodName, e);
    			cacheNotUpdated.append("user Wallet  Cache,");
    			log.error(methodName, "Exception User Wallet cache " + e.getMessage());
    		}
    		break;
    	case PretupsI.CACHE_ServiceKeywordCache:
    		try
    		{
    			ServiceKeywordCache.updateServiceKeywords();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Service Keyword Cache,");log.error(methodName, "Exception ServiceKeywordCache cache " + e.getMessage()); }
    		break;
    	case PretupsI.CACHE_MSISDNPrefixInterfaceMappingCache:
    		try
    		{
    			MSISDNPrefixInterfaceMappingCache.updatePrefixInterfaceMapping();
    			if(!BTSLUtil.isNullString(Constants.getProperty("INTERFACE_CLOSER_IN_IDS")))
    				InterfaceCloserController.update("ALL");
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("MSISDN Prefixes Interface Mapping Cache,");log.error(methodName, "Exception MSISDNPrefixInterfaceMappingCache cache " + e.getMessage()); }
    		break;
    	case PretupsI.CACHE_NetworkInterfaceModuleCache:
    		try
    		{
    			NetworkInterfaceModuleCache.updateNetworkInterfaceModule();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Network Interface Module Cache,");log.error(methodName, "Exception NetworkInterfaceModuleCache cache " + e.getMessage()); }
    		break;

    	case PretupsI.CACHE_ServicePaymentMappingCache:
    		try
    		{
    			ServicePaymentMappingCache.updateServicePaymentMapping();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Service Payment Mapping Cache,");log.error(methodName, "Exception ServicePaymentMappingCache cache " + e.getMessage()); }
    		break;

    	case PretupsI.CACHE_TransferRulesCache:
    		try
    		{
    			TransferRulesCache.updateTransferRulesMapping();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Transfer Rules Cache,");log.error(methodName, "Exception TransferRulesCache cache " + e.getMessage()); }
    		break;
    	case PretupsI.CACHE_MessageGatewayCache:
    		try
    		{
    			MessageGatewayCache.updateMessageGateway();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Message Gateway Cache,");log.error(methodName, "Exception: MessageGatewayCache cache " + e.getMessage()); }
    		try
    		{
    			MessageGatewayCache.updateMessageGatewayMapping();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Message Gateway Mapping Cache,");log.error(methodName, "Exception:: MessageGatewayCache cache " + e.getMessage()); }
    		break;

    		/**
    		 * code is commented on the jsp also since we donot want to initialize counters in the case of cache
    		 * updation.
    		 */

    	case PretupsI.CACHE_SIM_PROFILE:
    		try
    		{
    			SimProfileCache.refreshSimProfileList();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Sim Profile Cache,");log.error(methodName, "Exception SimProfileCache  " + e.getMessage()); }
    		break;
    	case PretupsI.CACHE_NETWORK_SERVICE_CACHE:
    		try
    		{
    			NetworkServicesCache.refreshNetworkServicesList();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Network Services Cache,");log.error(methodName, "Exception NetworkServicesCache  " + e.getMessage()); }
    		break;
    	case PretupsI.CACHE_NETWORK_PRODUCT_SERVICE_TYPE:
    		try
    		{
    			NetworkProductServiceTypeCache.refreshNetworkProductMapping();
    			NetworkProductServiceTypeCache.refreshProductServiceTypeMapping();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Network Product Mapping and Product Service Type Mapping Cache,");log.error(methodName, "Exception NetworkProductServiceTypeCache  " + e.getMessage()); }
    		break;

    	case PretupsI.CACHE_ROUTING_CONTROL:
    		try
    		{
    			SubscriberRoutingControlCache.refreshSubscriberRoutingControl();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Subscriber Routing Control Cache,");log.error(methodName, "Exception SubscriberRoutingControlCache  " + e.getMessage()); }
    		break;
    	case PretupsI.CACHE_SERVICE_ROUTING:
    		try
    		{
    			ServiceInterfaceRoutingCache.refreshServiceInterfaceRouting();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Service Interface Routing Cache,");log.error(methodName, "Exception ServiceInterfaceRoutingCache  " + e.getMessage()); }
    		break;
    	case PretupsI.CACHE_REGISTRATION_CONTROL:
    		try
    		{
    			RegistrationControlCache.refreshRegisterationControl();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Registeration Control Cache,");log.error(methodName, "Exception RegistrationControlCache  " + e.getMessage()); }
    		break;
    	case PretupsI.CACHE_CONSTANT_PROPS:
    		try
    		{
    			Constants.load(constantspropsfile);
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Constant Props,");log.error(methodName, "Exception Constant Props  " + e.getMessage()); }
    		break;
    	case PretupsI.CACHE_LOGGER_CONFIG:
    		try
    		{
    		//	org.apache.log4j.PropertyConfigurator.configure(loggerConfigFile);
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Logger Config ,");log.error(methodName, "Exception LOGGER CONFIG  " + e.getMessage()); }
    		break;
    	case PretupsI.CACHE_MESSAGE:
    		try
    		{
    			//ChangeID=LOCALEMASTER
    			//Languagelist to load messages cache will now be loaded from the locale master cache.
    			MessagesCaches.reload(LocaleMasterCache.getLocaleList());
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Message Cache ,");log.error(methodName, "Exception Message Cache" + e.getMessage()); }
    		break;
    	case PretupsI.CACHE_MESSAGE_RESOURCES:

    		if(isWeb)
    		{
    			StringBuilder buffer = new StringBuilder("http://");
    			buffer.append(ip);
    			buffer.append(":");
    			buffer.append(port);
    			buffer.append("/pretups/reloadMessageResource.do");
    			if(!"en_US".equals(locale))
    			{
    				buffer.append("?locale="+locale);
    			}
    			LogFactory.printLog(methodName, "Message Resource Cache Update URL="+buffer.toString(),log);

    			HttpURLConnection con = null;
    			try
    			{
    				URL url = new URL(buffer.toString());
    				URLConnection uc = url.openConnection();
    				con = (HttpURLConnection) uc;                        
    				con.setUseCaches(false);
    				con.setDoInput(true);
    				con.setDoOutput(true);


    				inputStreamReader = new InputStreamReader( uc.getInputStream() );
    				br = new BufferedReader( inputStreamReader);
    				String str = null;
    				StringBuilder message = new StringBuilder();
    				while((str = br.readLine()) != null){
    					message.append(str);
    				}
    				LogFactory.printLog(methodName, "Message Resource Cache Update Status ="+message.toString(),log);
    			}catch (Exception e) {
    				log.errorTrace(methodName,e);cacheNotUpdated.append("Message Resource Cache ,");log.error(methodName, "Exception Message Cache:: " + e.getMessage()); 
    			}finally{
    				if(con != null) con.disconnect();
    				if (inputStreamReader != null) {
    					try {
    						inputStreamReader.close();
    					} catch (IOException e) {
    						log.errorTrace(methodName,e);
    					}
    				}
    				if (br != null) {
    					try {
    						br.close();
    					} catch (IOException e) {
    						log.errorTrace(methodName,e);
    					}
    				}
    			}
    		}
    		break;

    	case PretupsI.CACHE_GROUP_TYPE_PROFILE:
    		try
    		{
    			GroupTypeProfileCache.updateGroupTypeProfilesCache();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Group type profile cache,");log.error(methodName, "Exception Group type profile cache  " + e.getMessage()); }
    		break; 
    		/*The new cache are added by ankit Z on date 3/8/6
    		 * First is for network interface module cache
    		 * Second is for interface routing control cache. This is used for alternate routing
    		 */
    	case PretupsI.CACHE_NETWORK_INTERFACE_MODULE:
    		try
    		{
    			NetworkInterfaceModuleCache.updateNetworkInterfaceModule();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Network interface module cache,");log.error(methodName, "Exception Network interface module cache  " + e.getMessage()); }
    		break; 

    	case PretupsI.CACHE_INTERFACE_ROUTING_CONTROL:
    		try
    		{
    			InterfaceRoutingControlCache.refreshInterfaceRoutingControl();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Interface routing control cache,");log.error(methodName, "Exception Interface routing control cache  " + e.getMessage()); }
    		break; 
    	case PretupsI.CACHE_PAYMENT_METHOD:
    		try
    		{
    			PaymentMethodCache.updatePaymentMethod() ;
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Payment method cache,");log.error(methodName, "Exception Payment method cache  " + e.getMessage()); }
    		break; 
    	case PretupsI.CACHE_SERVICE_SELECTOR_MAPPING:
    		try
    		{
    			ServiceSelectorMappingCache.loadServiceSelectorMappingCacheOnStartUp();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Service selector mapping cache,");log.error(methodName, "Exception Service selector mapping cache:  " + e.getMessage()); }
    		break; 

    	case PretupsI.CACHE_BONUS_BUNDLES:
    		try
    		{
    			BonusBundleCache.loadBonusBundleCacheOnStartUp();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Service Bonus Bundle Cache,");log.error(methodName, "Exception Service Bonus Bundle Cache:" + e.getMessage()); }
    		break; 

    	case PretupsI.IAT_COUNTRY_MASTER_CACHE:
    		try
    		{
    			IATCountryMasterCache.loadIATCountryMasterCache();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("IAT Country master cache ,");log.error(methodName, "Exception Service selector mapping cache::  " + e.getMessage()); }
    		break; 
    	case PretupsI.IAT_NETWORK_CACHE:
    		try
    		{
    			IATNWServiceCache.loadIATNWServiceCache();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("IAT network service mapping cache,");log.error(methodName, "Exception Service selector mapping cache:::  " + e.getMessage()); }
    		break; 


    	case PretupsI.CACHE_MESSAGE_MANAGEMENT:
    		try
    		{
    			MessagesManagementCache.loadMessagManagementCache();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("IAT Country master cache:,");log.error(methodName, "Exception Service selector mapping cache::::  " + e.getMessage()); }
    		break;
    	case PretupsI.CACHE_NETWORK_PRODUCT:
    		try
    		{
    			NetworkProductCache.loadNetworkProductMapAtStartup();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Network product cache,");log.error(methodName, "Exception network product cache  " + e.getMessage()); }
    		break; 
    	case PretupsI.CACHE_CARD_GROUP:
    		try
    		{
    			CardGroupCache.loadCardGroupMapAtStartup();				
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Card Group cache,");log.error(methodName, "Exception Card Group cache  " + e.getMessage()); }
    		break;
    	case PretupsI.CACHE_MESSAGE_GATEWAY_CATEGORY:
    		try
    		{
    			MessageGatewayForCategoryCache.loadMeassageGatewayForCategoryMapAtStartup();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Message gateway for Category cache,");log.error(methodName, "Exception messageGatewayForCategory cache  " + e.getMessage()); }
    		break;

    	case PretupsI.CACHE_SERVICE_CLASS_CODE:
    		try
    		{
    			ServiceClassInfoByCodeCache.loadServiceClassByCodeMapAtStartup();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Service Class Info By Code Cache,");log.error(methodName, "Exception ServiceClassInfoByCode cache  " + e.getMessage()); }
    		break;
    	case PretupsI.TRANSFER_PROFILE:
    		try
    		{
    			TransferProfileCache.loadTransferProfileAtStartup();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Transfer Profile cache,");log.error(methodName, "Exception Transfer Profile cache  " + e.getMessage()); }
    		break; 

    	case PretupsI.TRANSFER_PROFILE_PRODUCT:
    		try
    		{
    			TransferProfileProductCache.loadTransferProfileProductsAtStartup();
    		}catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("Transfer Profile Product cache,");log.error(methodName, "Exception Transfer Profile Product cache  " + e.getMessage()); }
    		break;
    	case PretupsI.CACHE_USER_DEFAULT:
    		try
    		{
    			UserDefaultCache.updateUserDefaultConfig();
    		}
    		catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("User Default Config Mapping cache,");log.error(methodName, "Exception User Default Config cache  " + e.getMessage()); }
    		break; 

    	case PretupsI.CACHE_USER_SERVICES:
    		try
    		{
    			UserServicesCache.updateServicesMap();
    		}
    		catch (Exception e) 
    		{log.errorTrace(methodName,e);cacheNotUpdated.append("User Service  mapping cache,");log.error(methodName, "Exception User Service mapping cache  " + e.getMessage()); }
    		break; 
    	case PretupsI.CACHE_COMMISSION_PROFILE:
    		try{
    			CommissionProfileCache.updateCommissionProfileMapping();
    		}catch(Exception e){
    			log.errorTrace(methodName,e);cacheNotUpdated.append("Commission Profile mapping  cache,");log.error(methodName, "Exception Commission Profile mapping cache " + e.getMessage()); 	}
    		break;
    	case PretupsI.CACHE_USER_ALLOWED_STATUS:
    		try{
    			UserStatusCache.updateUserStatusMapping();
    		}catch(Exception e){
    			log.errorTrace(methodName,e);cacheNotUpdated.append("Commission  Profile mapping cache,");log.error(methodName, "Exception Commission Profile  mapping cache  " + e.getMessage()); 	}
    		break;
    	case PretupsI.CACHE_LMS_PROFILE:
    		try {
    			LMSProfileCache.updateLmsProfileMapping();
    		}
    		catch (Exception e) {
    			log.errorTrace(methodName, e);
    			cacheNotUpdated.append("Lms Profile mapping cache,");
    			log.error(methodName, "Exception Lms Profile mapping cache  " + e.getMessage());
    		}
    		break;

    	default:
    		break;

    	}





    	String str = null;
    	if(cacheNotUpdated.indexOf(",") != -1)
    		str = cacheNotUpdated.substring(0,cacheNotUpdated.lastIndexOf(","));

    	LogFactory.printLog(methodName, "Exiting "+cacheNotUpdated.toString(), log);
    	return str;
    }
    
    




    public void updateAllCache( String ip, String port, String locale,boolean isWeb,boolean isNPCacheUpdatedTemp){

    	String methodName="updateAllCache";
    	LogFactory.printLog(methodName, "Entered p_cacheId=" +" IP="+ip+" port="+port+" p_locale="+locale,log);
    	StringBuilder cacheNotUpdated=new StringBuilder();
    	InputStreamReader inputStreamReader = null;
    	BufferedReader br = null;
    	boolean isNPCacheUpdated=isNPCacheUpdatedTemp;
    	try
    	{
    		NetworkCache.updateNetwork();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Newtork Cache,");  log.error(methodName, "Exception Network cache " + e.getMessage()); }
    	try
    	{
    		NetworkPrefixCache.updateNetworkPrefixes();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Network Prefixes Cache,");log.error(methodName, "Exception NetworkPrefixCache cache " + e.getMessage()); }
    	try
    	{
    		LookupsCache.updateData();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("LooKUps Cache,");log.error(methodName, "Exception Look cache " + e.getMessage()); }
    	try
    	{
    		PreferenceCache.updatePrefrences();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Preferences Cache,");log.error(methodName, "Exception PreferenceCache cache " + e.getMessage()); }
    	try
    	{
    		SystemPreferences.reload();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("System Preferences Cache,");log.error(methodName, "Exception SystemPreferences cache " + e.getMessage()); }

    	try
    	{
    		if(!isNPCacheUpdated)
    			NetworkPrefixCache.updateNetworkPrefixes();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Network Prefixes Cache,");log.error(methodName, "Exception NetworkPrefixCache cache " + e.getMessage()); }

    	try {
    		UserProductWalletMappingCache.loadUserProductWalletMappingOnStartUp();
    	} catch (Exception e) {
    		log.errorTrace(methodName, e);
    		cacheNotUpdated.append("user Wallet  Cache,");
    		log.error(methodName, "Exception User Wallet cache " + e.getMessage());
    	}

    	try
    	{
    		ServiceKeywordCache.updateServiceKeywords();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Service Keyword Cache,");log.error(methodName, "Exception ServiceKeywordCache cache " + e.getMessage()); }

    	try
    	{
    		MSISDNPrefixInterfaceMappingCache.updatePrefixInterfaceMapping();
    		if(!BTSLUtil.isNullString(Constants.getProperty("INTERFACE_CLOSER_IN_IDS")))
    			InterfaceCloserController.update("ALL");
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("MSISDN Prefixes Interface Mapping Cache,");log.error(methodName, "Exception MSISDNPrefixInterfaceMappingCache cache " + e.getMessage()); }
    	try
    	{
    		NetworkInterfaceModuleCache.updateNetworkInterfaceModule();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Network Interface Module Cache,");log.error(methodName, "Exception NetworkInterfaceModuleCache cache " + e.getMessage()); }
    	try
    	{
    		ServicePaymentMappingCache.updateServicePaymentMapping();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Service Payment Mapping Cache,");log.error(methodName, "Exception ServicePaymentMappingCache cache " + e.getMessage()); }
    	try
    	{
    		TransferRulesCache.updateTransferRulesMapping();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Transfer Rules Cache,");log.error(methodName, "Exception TransferRulesCache cache " + e.getMessage()); }
    	try
    	{
    		MessageGatewayCache.updateMessageGateway();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Message Gateway Cache,");log.error(methodName, "Exception MessageGatewayCache cache " + e.getMessage()); }
    	try
    	{
    		MessageGatewayCache.updateMessageGatewayMapping();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Message Gateway Mapping Cache,");log.error(methodName, "Exception MessageGatewayCache cache " + e.getMessage()); }

    	/**
    	 * code is commented on the jsp also since we donot want to initialize counters in the case of cache
    	 * updation.
    	 */
    	try
    	{
    		SimProfileCache.refreshSimProfileList();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Sim Profile Cache,");log.error(methodName, "Exception SimProfileCache  " + e.getMessage()); }
    	try
    	{
    		NetworkServicesCache.refreshNetworkServicesList();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Network Services Cache,");log.error(methodName, "Exception NetworkServicesCache  " + e.getMessage()); }
    	try
    	{
    		NetworkProductServiceTypeCache.refreshNetworkProductMapping();
    		NetworkProductServiceTypeCache.refreshProductServiceTypeMapping();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Network Product Mapping and Product Service Type Mapping Cache,");log.error(methodName, "Exception NetworkProductServiceTypeCache  " + e.getMessage()); }
    	try
    	{
    		SubscriberRoutingControlCache.refreshSubscriberRoutingControl();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Subscriber Routing Control Cache,");log.error(methodName, "Exception SubscriberRoutingControlCache  " + e.getMessage()); }
    	try
    	{
    		ServiceInterfaceRoutingCache.refreshServiceInterfaceRouting();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Service Interface Routing Cache,");log.error(methodName, "Exception ServiceInterfaceRoutingCache  " + e.getMessage()); }
    	try
    	{
    		RegistrationControlCache.refreshRegisterationControl();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Registeration Control Cache,");log.error(methodName, "Exception RegistrationControlCache  " + e.getMessage()); }
    	try
    	{
    		Constants.load(constantspropsfile);
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Constant Props,");log.error(methodName, "Exception Constant Props  " + e.getMessage()); }
    	try
    	{
    	//	org.apache.log4j.PropertyConfigurator.configure(loggerConfigFile);
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Logger Config ,");log.error(methodName, "Exception LOGGER CONFIG  " + e.getMessage()); }
    	try
    	{
    		//ChangeID=LOCALEMASTER
    		//Languagelist to load messages cache will now be loaded from the locale master cache.
    		MessagesCaches.reload(LocaleMasterCache.getLocaleList());
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Message Cache ,");log.error(methodName, "Exception Message Cache " + e.getMessage()); }

    	if(isWeb)
    	{
    		StringBuilder buffer = new StringBuilder("http://");
    		buffer.append(ip);
    		buffer.append(":");
    		buffer.append(port);
    		buffer.append("/pretups/reloadMessageResource.do");
    		if(!"en_US".equals(locale))
    		{
    			buffer.append("?locale="+locale);
    		}
    		LogFactory.printLog(methodName, "Message Resource Cache Update URL="+buffer.toString(),log);

    		HttpURLConnection con = null;
    		try
    		{
    			URL url = new URL(buffer.toString());
    			URLConnection uc = url.openConnection();
    			con = (HttpURLConnection) uc;                        
    			con.setUseCaches(false);
    			con.setDoInput(true);
    			con.setDoOutput(true);


    			inputStreamReader = new InputStreamReader( uc.getInputStream() );
    			br = new BufferedReader( inputStreamReader);
    			String str = null;
    			StringBuilder message = new StringBuilder();
    			while((str = br.readLine()) != null){
    				message.append(str);
    			}
    			LogFactory.printLog(methodName, "Message Resource Cache Update Status ="+message.toString(),log);
    		}catch (Exception e) {
    			log.errorTrace(methodName,e);cacheNotUpdated.append("Message Resource Cache ,");log.error(methodName, "Exception Message Cache " + e.getMessage()); 
    		}finally{
    			if(con != null) con.disconnect();
    			if (inputStreamReader != null) {
    				try {
    					inputStreamReader.close();
    				} catch (IOException e) {
    					log.errorTrace(methodName,e);
    				}
    			}
    			if (br != null) {
    				try {
    					br.close();
    				} catch (IOException e) {
    					log.errorTrace(methodName,e);
    				}
    			}
    		}
    	}
    	try
    	{
    		GroupTypeProfileCache.updateGroupTypeProfilesCache();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Group type profile cache,");log.error(methodName, "Exception Group type profile cache  " + e.getMessage()); }
    	/*The new cache are added by ankit Z on date 3/8/6
    	 * First is for network interface module cache
    	 * Second is for interface routing control cache. This is used for alternate routing
    	 */
    	try
    	{
    		NetworkInterfaceModuleCache.updateNetworkInterfaceModule();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Network interface module cache,");log.error(methodName, "Exception Network interface module cache  " + e.getMessage()); }
    	try
    	{
    		InterfaceRoutingControlCache.refreshInterfaceRoutingControl();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Interface routing control cache,");log.error(methodName, "Exception Interface routing control cache  " + e.getMessage()); }
    	try
    	{
    		PaymentMethodCache.updatePaymentMethod() ;
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Payment method cache,");log.error(methodName, "Exception Payment method cache  " + e.getMessage()); }
    	try
    	{
    		ServiceSelectorMappingCache.loadServiceSelectorMappingCacheOnStartUp();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Service selector mapping cache,");log.error(methodName, "Exception Service selector mapping cache " + e.getMessage()); }
    	try
    	{
    		BonusBundleCache.loadBonusBundleCacheOnStartUp();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Service Bonus Bundle Cache,");log.error(methodName, "Exception Service Bonus Bundle Cache:" + e.getMessage()); }
    	try
    	{
    		IATCountryMasterCache.loadIATCountryMasterCache();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("IAT Country master cache,");log.error(methodName, "Exception Service selector mapping cache:  " + e.getMessage()); }
    	try
    	{
    		IATNWServiceCache.loadIATNWServiceCache();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("IAT network service mapping cache,");log.error(methodName, "Exception Service selector mapping cache  " + e.getMessage()); }
    	try
    	{
    		MessagesManagementCache.loadMessagManagementCache();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("IAT Country master cache,");log.error(methodName, "Exception Service selector mapping cache  " + e.getMessage()); }
    	try
    	{
    		NetworkProductCache.loadNetworkProductMapAtStartup();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Network product cache,");log.error(methodName, "Exception network product cache  " + e.getMessage()); }
    	try
    	{
    		CardGroupCache.loadCardGroupMapAtStartup();				
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Card Group cache,");log.error(methodName, "Exception Card Group cache  " + e.getMessage()); }
    	try
    	{
    		MessageGatewayForCategoryCache.loadMeassageGatewayForCategoryMapAtStartup();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Message gateway for Category cache,");log.error(methodName, "Exception messageGatewayForCategory cache  " + e.getMessage()); }
    	try
    	{
    		ServiceClassInfoByCodeCache.loadServiceClassByCodeMapAtStartup();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Service Class Info By Code Cache,");log.error(methodName, "Exception ServiceClassInfoByCode cache  " + e.getMessage()); }
    	try
    	{
    		TransferProfileCache.loadTransferProfileAtStartup();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Transfer Profile cache,");log.error(methodName, "Exception Transfer Profile cache  " + e.getMessage()); }
    	try
    	{
    		TransferProfileProductCache.loadTransferProfileProductsAtStartup();
    	}catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("Transfer Profile Product cache,");log.error(methodName, "Exception Transfer Profile Product cache  " + e.getMessage()); }
    	try
    	{
    		UserDefaultCache.updateUserDefaultConfig();
    	}
    	catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("User Default Config Mapping cache,");log.error(methodName, "Exception User Default Config cache  " + e.getMessage()); }
    	try
    	{
    		UserServicesCache.updateServicesMap();
    	}
    	catch (Exception e) 
    	{log.errorTrace(methodName,e);cacheNotUpdated.append("User Service  mapping cache,");log.error(methodName, "Exception User Service mapping cache  " + e.getMessage()); }
    	try{
    		CommissionProfileCache.updateCommissionProfileMapping();
    	}catch(Exception e){
    		log.errorTrace(methodName,e);cacheNotUpdated.append("Commission Profile mapping cache,");log.error(methodName, "Exception Commission Profile mapping cache  " + e.getMessage()); 	}
    	try{
    		UserStatusCache.updateUserStatusMapping();
    	}catch(Exception e){
    		log.errorTrace(methodName,e);cacheNotUpdated.append("Commission Profile mapping cache,");log.error(methodName, "Exception Commission Profile mapping cache  " + e.getMessage()); 	}
    	try {
    		LMSProfileCache.updateLmsProfileMapping();
    	}
    	catch (Exception e) {
    		log.errorTrace(methodName, e);
    		cacheNotUpdated.append("Lms Profile mapping cache,");
    		log.error(methodName, "Exception Lms Profile mapping cache  " + e.getMessage());
    	}


}
}
