/**
 * @(#)UpdateCacheServlet.java
 *                             Copyright(c) 2005, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 * 
 *                             use to update the cacahe on multiple instances of
 *                             our system. it may be WEB or SMS
 * 
 *                             Instances are made in our database with there IP
 *                             and PORT
 *                             we make the URL connection on the diffrent
 *                             instances and call
 *                             this servlet with the parameter selected by user.
 *                             IN case if user select the ALL option in JSP to
 *                             update cache. In that case we pass All with query
 *                             String.
 *                             otherwise we pass selcted Cache option to all
 *                             servlet on diffrent instances.
 * 
 *                             Only one servlet will load all instances from
 *                             INSTANCE_LOAD table and only this servlet will
 *                             make
 *                             URL connection with other servlet with selected
 *                             options. We diffrentiate this (to load instances
 *                             or not) on
 *                             the base of one request parameter i.e. "fromWeb"
 *                             .
 * 
 * 
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             avinash.kamthan Jul 27, 2005 Initital Creation
 *                             Ankit Zindal 20/11/2006 ChangeID=LOCALEMASTER
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 * 
 */

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
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.loadcontroller.LoadControllerDAO;
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


/**
 * @author avinash.kamthan
 */
public class UpdateCacheServlet extends HttpServlet {

    /**
     * Field _log.
     */
    private static Log _log = LogFactory.getLog(UpdateCacheServlet.class.getName());
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
    private static String instanceID = null;

    String ServerName = null;
    /**
     * Boolean decides whether to update NetworkPrefixCache individual, on
     * selecting the NetworkCache and NetworkPrefixCache together to update
     */
    private boolean isNPCacheUpdated = false;

    /**
     * Constructor for UpdateCacheServlet.
     */
    public UpdateCacheServlet() {
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
                    constantspropsfile = System.getProperty("catalina.base") + "\\conf\\pretups\\Constants.props";
                    loggerConfigFile = System.getProperty("catalina.base") + "\\conf\\pretups\\LogConfig.props";
                } else if (serverInfo.contains("Jboss")) {
                    constantspropsfile = System.getProperty("jboss.server.name") + "\\conf\\pretups\\Constants.props";
                    loggerConfigFile = System.getProperty("jboss.server.name") + "\\conf\\pretups\\LogConfig.props";
                } else if (serverInfo.contains("WebSphere")) {
                    constantspropsfile = System.getProperty("ServerName") + "\\conf\\pretups\\Constants.props";
                    loggerConfigFile = System.getProperty("ServerName") + "\\conf\\pretups\\LogConfig.props";
                }
            } else {
                if (serverInfo.contains("Tomcat")) {
                    constantspropsfile = System.getProperty("catalina.base") + "/conf/pretups/Constants.props";
                    loggerConfigFile = System.getProperty("catalina.base") + "/conf/pretups/LogConfig.props";
                } else if (serverInfo.contains("Jboss")) {   
                    constantspropsfile = System.getProperty("jboss.server.name") + "/conf/pretups/Constants.props";
                    loggerConfigFile = System.getProperty("jboss.server.name") + "/conf/pretups/LogConfig.props";
                } else if (serverInfo.contains("WebSphere")) {
                    constantspropsfile = System.getProperty("ServerName") + "/conf/pretups/Constants.props";
                    loggerConfigFile = System.getProperty("ServerName") + "/conf/pretups/LogConfig.props";
                }
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.info(methodName, "Config servlet 1 constantspropsfile=" + constantspropsfile + "  loggerConfigFile=" + loggerConfigFile);
        }
        instanceID = Constants.getProperty("INSTANCE_ID");
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
                    constantspropsfile = System.getProperty("catalina.base") + "\\conf\\pretups\\Constants.props";
                    loggerConfigFile = System.getProperty("catalina.base") + "\\conf\\pretups\\LogConfig.props";
                } else if (serverInfo.contains("Jboss")) {
                    constantspropsfile = System.getProperty("jboss.server.name") + "\\conf\\pretups\\Constants.props";
                    loggerConfigFile = System.getProperty("jboss.server.name") + "\\conf\\pretups\\LogConfig.props";
                } else if (serverInfo.contains("WebSphere")) {
                    constantspropsfile = System.getProperty("ServerName") + "\\conf\\pretups\\Constants.props";
                    loggerConfigFile = System.getProperty("ServerName") + "\\conf\\pretups\\LogConfig.props";
                }
            } else {
                if (serverInfo.contains("Tomcat")) {
                    constantspropsfile = System.getProperty("catalina.base") + "/conf/pretups/Constants.props";
                    loggerConfigFile = System.getProperty("catalina.base") + "/conf/pretups/LogConfig.props";
                } else if (serverInfo.contains("Jboss")) {   
                    constantspropsfile = System.getProperty("jboss.server.name") + "/conf/pretups/Constants.props";
                    loggerConfigFile = System.getProperty("jboss.server.name") + "/conf/pretups/LogConfig.props";
                } else if (serverInfo.contains("WebSphere")) {
                    constantspropsfile = System.getProperty("ServerName") + "/conf/pretups/Constants.props";
                    loggerConfigFile = System.getProperty("ServerName") + "/conf/pretups/LogConfig.props";
                }
            }
            instanceID = Constants.getProperty("INSTANCE_ID");
            _log.info(methodName, "Config servlet 1 constantspropsfile=" + constantspropsfile + "  loggerConfigFile=" + loggerConfigFile);
            
        }
            catch (Exception e) {
                _log.errorTrace(methodName, e);
                _log.info(methodName, "Config servlet 1 constantspropsfile=" + constantspropsfile + "  loggerConfigFile=" + loggerConfigFile);
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
        final String instanceParam = "instanceParam";
        final String cacheAllParam = "cacheAll";
        BufferedReader br = null;
        ArrayList list = null;
        String cacheAll = null;
        String localString = null;
        String[] cacheValues = null;
        String[] instanceValues = null;
        StringBuffer sbf = null;
        InstanceLoadVO instLoadVO = null;
        Map mp = null;
        String updateID_req = request.getParameter("updateid");
        try {
            String msg[] = null;
            String msgF[] = null;
            int index = -1;
            int indexF = -1;
            String IP = null;
            String port = null;
            List<String> arrList = Collections.synchronizedList(new ArrayList<String>());
            List<String> arrListF = Collections.synchronizedList(new ArrayList<String>());
            ExecutorService executor = null;
            boolean webInstance = false;
            if ("WEB".equalsIgnoreCase(request.getParameter("fromWeb"))) {
                cacheAll = request.getParameter(cacheAllParam);
                cacheValues = request.getParameterValues(cacheParam);
                instanceValues = request.getParameterValues(instanceParam);
                if (BTSLUtil.isNullString(cacheAll) && cacheValues == null ) {
                    request.setAttribute("messageStr", PretupsI.NODATA_ENTERED);
                    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/jsp/master/updateCache.jsp");
                    requestDispatcher.forward(request, response);
                    return;
                }
				msg = new String[cacheValues.length * instanceValues.length];
				int poolSize = Integer.parseInt(Constants.getProperty("UPDATE_CACHE_THREAD_POOL_SIZE"));
				executor = Executors.newFixedThreadPool(poolSize);
                if (updateID_req != null && updateID_req.equalsIgnoreCase(instanceID)) 
				{
					mp = new HashMap();
					list = loadInstances();
					for (int a = 0; a < list.size(); a++) {
						instLoadVO = (InstanceLoadVO) list.get(a);
						mp.put(instLoadVO.getInstanceID(), instLoadVO);
					}
					StringBuffer urlString = null;
					InstanceLoadVO instanceLoadVO = null;
					index = 0;
					indexF = 0;
					for (int i = 0, k = instanceValues.length; i < k; i++) {
						instanceLoadVO = (InstanceLoadVO) mp.get(instanceValues[i]);
						for (int j = 0, m = cacheValues.length; j < m; j++) {
							sbf = new StringBuffer();
							sbf.append(cacheParam);
							sbf.append("=");
							sbf.append(cacheValues[j]);
							Locale locale = BTSLUtil.getBTSLLocale(request);
							sbf.append("&locale");
							sbf.append("=");
							sbf.append(locale.toString());

							if (_log.isDebugEnabled()) {
								_log.debug(methodName,"  FROMWEB=" + request.getParameter("fromWeb") + " instanceID=" + instanceID + "  instanceLoadVO.getInstanceID()=" + instanceLoadVO.getInstanceID() + "  cacheValues[j]=" + cacheValues[j]);
							}
							if ("WEB".equalsIgnoreCase(request.getParameter("fromWeb")) && instanceID.equals(instanceLoadVO.getInstanceID())) {
								webInstance = true;
								IP = instanceLoadVO.getHostAddress();
								ServerName = "[" + instanceLoadVO.getInstanceID() + "] " + instanceLoadVO.getInstanceName() ;
								port = instanceLoadVO.getHostPort();
								HttpSession session = request.getSession(false);
								if (session.getAttribute("Authorization") != null && session.getId().equalsIgnoreCase(session.getAttribute("Authorization").toString())) {
									if (_log.isDebugEnabled()) {
										_log.debug(methodName, "::" + session.getId() + "::" + session.getAttribute("Authorization").toString());
									}
								}
								localString = locale.toString();
								continue;
							}
							urlString = new StringBuffer();
							if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.HTTPS_ENABLE)).booleanValue()) {
								urlString.append("https");
							} else {
								urlString.append("http");
							}
							urlString.append("://");
							urlString.append(instanceLoadVO.getHostAddress());
							urlString.append(":");
							urlString.append(instanceLoadVO.getHostPort());
							urlString.append("/");
							urlString.append(instanceLoadVO.getContext());
							urlString.append("/");
							urlString.append("UpdateCacheServlet?");
							urlString.append(sbf);
							executor.execute(new UpdateCacheBL(arrList, arrListF, urlString.toString(), instanceLoadVO, cacheValues[j]));
						}
					}
				}
                else {
                    msg = new String[1];
                    msg[0] = "Mentioned Cache have not been updated succssfully  ";
                    request.setAttribute("messageArr", msg);
                    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/jsp/master/updateCacheSuccess.jsp");
                    requestDispatcher.forward(request, response);
                }
            }else{
            	String[] cacheValue = request.getParameterValues(cacheParam);
            	String[] locale = request.getParameterValues("locale");
            	String updateCacheErrMsg = updateCache(cacheValue[0], locale[0].toString());
                return;
            }
            cacheAll = request.getParameter("cacheAll");
            cacheValues = request.getParameterValues("cacheParam");
            boolean isWeb = false;
            if ("WEB".equalsIgnoreCase(request.getParameter("fromWeb"))) {
                isWeb = true;
            }
            Locale locale = null;
            if (!BTSLUtil.isNullString(request.getParameter("locale"))) {
                String[] localeArr = request.getParameter("locale").split("_");
                if (localeArr.length != 2) {
                    locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                } else {
                    locale = new Locale(localeArr[0], localeArr[1]);
                }
            } else if (!BTSLUtil.isNullString(localString)) {
                String[] localeArr = localString.split("_");
                if (localeArr.length != 2) {
                    locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                } else {
                    locale = new Locale(localeArr[0], localeArr[1]);
                }
            } else {
                locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            }
			if (executor != null) {
				if (webInstance && mp.containsKey(instanceID)) {
					if (cacheValues != null) {
						for (int i = 0, k = cacheValues.length; i < k; i++) {
							if (cacheValues[i] != null) {
								executor.execute(new LocalUpdateCacheBL(arrList, arrListF, cacheValues[i], locale, ServerName,instanceID));
							}
						}
						isNPCacheUpdated = false;
					}
				}
				executor.shutdown();
				while (!executor.isTerminated()) {
				}
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
				String s2="";
				String s3="";
	            for(int i=0;i<arrList.size();i++){
	            	String[] s1 = ((String)arrList.get(i)).split(":");
	            	if(i==0)
	            		s2=s1[0];
	            	if(s1[0].equals(s2)){
	            		if(i == (arrList.size()-1)){
	            			s3=s3+mp1.get(s1[1]);
	            			msg[index++]=s2+":"+BTSLUtil.getMessage(locale, "error.updateserverCache.successmsg", new String[]{s3});
	            		}else{
	            			_log.debug(methodName, "CacheName:" + mp1.get(s1[1]));
	            			s3=s3+mp1.get(s1[1])+", ";
	            		}
	            	}else{
	            		s3=s3.substring(0,s3.length()-2);
	            		msg[index++]=s2+":"+BTSLUtil.getMessage(locale, "error.updateserverCache.successmsg", new String[]{s3});
	            		s2=s1[0];
	            		if(i == (arrList.size()-1))
	            			msg[index++]=s2+":"+BTSLUtil.getMessage(locale, "error.updateserverCache.successmsg", new String[]{s3});
	            		s3=mp1.get(s1[1])+", ";
	            	}
	            }
			}
			Collections.sort(arrListF);
			synchronized(arrListF) {
				msgF = new String[arrListF.size()];
				String s2="";
				String s3="";
	            for(int i=0;i<arrListF.size();i++){
	            	String[] s1 = ((String)arrListF.get(i)).split(":");
	            	if(i==0)
	            		s2=s1[0];
	            	if(s1[0].equals(s2)){
	            		if(i == (arrListF.size()-1)){
	            			s3=s3+mp1.get(s1[1]);
		            		msgF[indexF++]=s2+":"+BTSLUtil.getMessage(locale, "error.updateserverCache.failmsg", new String[]{s3});
	            		}else{
	            			s3=s3+mp1.get(s1[1])+", ";
	            		}
	            	}else{
	            		s3=s3.substring(0,s3.length()-2);
	            		msgF[indexF++]=s2+":"+BTSLUtil.getMessage(locale, "error.updateserverCache.failmsg", new String[]{s3});
	            		s2=s1[0];
	            		if(i == (arrListF.size()-1))
	            			msgF[indexF++]=s2+":"+BTSLUtil.getMessage(locale, "error.updateserverCache.failmsg", new String[]{s3});
	            		s3=mp1.get(s1[1])+", ";
	            	}
	            }
			}
        	_log.info(methodName, "End UpdateCacheServlet  ................... ");
            if ("WEB".equalsIgnoreCase(request.getParameter("fromWeb")) && (msg != null || msgF != null)) {
                request.setAttribute("messageArr", msg);
                request.setAttribute("messageArrF", msgF);
                request.setAttribute("labelArr",new String[]{BTSLUtil.getMessage(locale, "updateserverCache.faillabel", null),BTSLUtil.getMessage(locale, "updateserverCache.successlabel", null)});
                RequestDispatcher requestDispatcher = request.getRequestDispatcher("/jsp/master/updateCacheSuccess.jsp");
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
     * Method loadInstances.
     * 
     * @return ArrayList
     */
    private ArrayList loadInstances() {
        Connection con = null;
        MComConnectionI mcomCon = null;
        ArrayList list = null;
        final String methodName = "loadInstances";
        try {
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
            LoadControllerDAO controllerDAO = new LoadControllerDAO();
            list = controllerDAO.loadInstanceLoadDetails(con);
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UpdateCacheServlet[loadInstances]", "", "", "", "Exception while loading instances  Getting =" + e.getMessage());
        }// end of catch
        finally {
        	if(mcomCon != null){
        		mcomCon.close("UpdateCacheServlet#loadInstances");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting list.size " + list.size());
            }
        }// end of finally

        return list;
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
                Constants.load(constantspropsfile);
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
         ///       org.apache.log4j.PropertyConfigurator.configure(loggerConfigFile);
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
        	ArrayList instanceList = loadInstances();
        	for (int a = 0; a < instanceList.size(); a++) {
				vo = (InstanceLoadVO) instanceList.get(a);
				if(instanceID.equals(vo.getInstanceID()) && "WEB".equals(vo.getInstanceType())){
					f = true;
					break;
				}
					
			}
        	
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
                NetworkProductCache.loadNetworkProductMapAtStartup();
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
                CardGroupCache.loadCardGroupMapAtStartup();
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
                MessageGatewayForCategoryCache.loadMeassageGatewayForCategoryMapAtStartup();
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
                ServiceClassInfoByCodeCache.loadServiceClassByCodeMapAtStartup();
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
                TransferProfileProductCache.loadTransferProfileProductsAtStartup();
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

    // This method checks the user information sent in the Authorization
    // header against the database of users maintained in the users Hashtable.
    private boolean allowUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String METHOD_NAME = "allowUser";
        String userID = null;
        String password = null;
        boolean valid = false;
        String AuthPass = null;

        LoadControllerDAO controllerDAO = null;

        _log.debug("allowUser", "Enter");

        try {

            String authHeader = request.getHeader("Authorization");
            _log.debug("allowUser", "authHeader ::" + authHeader);

            if (authHeader != null) {

                controllerDAO = new LoadControllerDAO();
                Hashtable tempHash = controllerDAO.loadInstanceLoadDetails(instanceID);
                tempHash.get("p_instanceID");

                InstanceLoadVO instanceLoadVO = (InstanceLoadVO) tempHash.get(instanceID);

                if (instanceLoadVO != null) {
                    AuthPass = instanceLoadVO.getAuthPass();
                    ServerName = "[" + instanceLoadVO.getInstanceID() + "] " + instanceLoadVO.getInstanceName() + " ";
                }

                StringTokenizer st = new StringTokenizer(authHeader);
                if (st.hasMoreTokens()) {
                    String basic = st.nextToken();

                    // We only handle HTTP Basic authentication

                    if (basic.equalsIgnoreCase("Basic")) {
                        String credentials = st.nextToken();

                        // This example uses sun.misc.* classes. You will need
                        // to provide your own
                        // if you are not comfortable with that.

                        String userPass = new String(BTSLUtil.decodeBuffer(credentials));

                        // The decoded string is in the form "userID:password".

                        int p = userPass.indexOf(":");
                        if (p != -1) {
                            userID = userPass.substring(0, p);
                            password = userPass.substring(p + 1);

                            // Validate user ID and password and set valid true
                            // true if valid.
                            // In this example, we simply check that neither
                            // field is blank

                            if ((userID.trim().equals(instanceID)) && (password.trim().equals(AuthPass))) {
                                valid = true;
                            }
                        }
                    }
                }
            } else {
                valid = false;
            }

            return valid;
        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            return valid = false;
        }

        finally {
            _log.debug("allowUser", "Exiting with valid ::" + valid);
        }
    }

}
