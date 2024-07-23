package com.restapi.superadmin.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
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
import com.btsl.pretups.common.CacheDAO;
import com.btsl.pretups.common.CacheVO;
import com.btsl.pretups.common.LocalUpdateCacheBL;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.UpdateCacheBL;
import com.btsl.pretups.common.UpdateCacheServlet;
import com.btsl.pretups.common.UpdateRedisCache;
import com.btsl.pretups.common.UpdateRedisCacheServlet;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.superadmin.requestVO.UpdateCacheRequestVO;
import com.restapi.superadmin.responseVO.UpdateCacheResponseVO;
import com.restapi.superadmin.serviceI.UpdateCacheServiceI;

@Service("UpdateCacheServiceI")
public class UpdateCacheServiceImpl extends HttpServlet implements UpdateCacheServiceI {
	
	public static final Log log = LogFactory.getLog(UpdateCacheServiceImpl.class.getName());
	public static final String classname = "UpdateCacheServiceImpl";
	
    private static String constantspropsfile = null;
    private static String loggerConfigFile = null;
    private static String instanceID = null;
    String ServerName = null;
    private boolean isNPCacheUpdated = false;

	public UpdateCacheResponseVO updateCacheList(Connection con, MComConnectionI mcomCon, Locale locale,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException {
		
		final String methodName =  "updateCacheList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		UpdateCacheResponseVO response = null;
		response = new UpdateCacheResponseVO();
		
		ArrayList<InstanceLoadVO> instanceList = new ArrayList<InstanceLoadVO>();
		ArrayList<CacheVO> cacheList = new ArrayList<CacheVO>();
		
		try {
			
        	instanceList = (new LoadControllerDAO()).loadInstanceLoadDetails(con);
			if (instanceList.isEmpty()) {
				throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.UPDATE_INSTANCE_LIST_NOT_FOUND, 0, null);
			} else {
				response.setInstanceList(instanceList);
			}
        	
            cacheList = (new CacheDAO()).loadCacheList(con);
			if (cacheList.isEmpty()) {
				throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.UPDATE_CACHE_LIST_NOT_FOUND, 0, null);
			} else {
				response.setCacheList(cacheList);
			}
			
        	response.setRedis(Constants.getProperty("REDIS_ENABLE"));
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPDATE_INSTANCE_CACHE_LIST_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.UPDATE_INSTANCE_CACHE_LIST_FOUND);
			
		} catch (BTSLBaseException be) {
			
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			
		} catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.UPDATE_INSTANCE_CACHE_LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.UPDATE_INSTANCE_CACHE_LIST_NOT_FOUND);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		return response;
	}

	@Override
	public UpdateCacheResponseVO updateCache(Connection con, UserVO userVO, MComConnectionI mcomCon, Locale locale, UpdateCacheRequestVO request) {
		
		final String methodName =  "updateCache";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		UpdateCacheResponseVO response = null;
		response = new UpdateCacheResponseVO();
		
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
//        String updateID_req = "15";
        
        String serverInfo = Constants.getProperty("SERVER_INFO");
	    (new UpdateCacheServlet()).updateFilePath(serverInfo);
        
        instanceID = Constants.getProperty("INSTANCE_ID");
        
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
           
            cacheValues = request.getCacheList();
            instanceValues = request.getInstanceList();
            
            if (cacheValues == null ) {
            	response.setMessage(RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.EMPTY_INSTANCE_CACHE_LIST, null));
            	response.setMessageCode(PretupsErrorCodesI.EMPTY_INSTANCE_CACHE_LIST);
                return response;
            }
            
			msg = new String[cacheValues.length * instanceValues.length];
			int poolSize = Integer.parseInt(Constants.getProperty("UPDATE_CACHE_THREAD_POOL_SIZE"));
			executor = Executors.newFixedThreadPool(poolSize);
			
//            if (updateID_req != null && updateID_req.equalsIgnoreCase(instanceID)) {
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
						sbf.append("&locale");
						sbf.append("=");
						sbf.append(locale.toString());
						
						if (log.isDebugEnabled()) {
							log.debug(methodName," instanceID=" + instanceID + "  instanceLoadVO.getInstanceID()=" + instanceLoadVO.getInstanceID() + "  cacheValues[j]=" + cacheValues[j]);
						}
						if (instanceID.equals(instanceLoadVO.getInstanceID())) {
							webInstance = true;
							IP = instanceLoadVO.getHostAddress();
							ServerName = "[" + instanceLoadVO.getInstanceID() + "] " + instanceLoadVO.getInstanceName() ;
							port = instanceLoadVO.getHostPort();
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
						 // log the data in adminOperationLog.log
		                
					}
				}
//            } else {
//                msg = new String[1];
//                msg[0] = "Mentioned Cache have not been updated successfully  ";
//            }
			
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
				 log.error(methodName, "Exception:e=" + e);
		         log.errorTrace(methodName, e);
			}
			finally{
				if (mcomCon != null) {
					mcomCon.close("UpdateCacheServiceImpl#updateCache");
					mcomCon = null;
				}
			}
			log.debug(methodName, "CacheCode:CacheName" + mp1);
			
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
	            			msg[index++]=s2+":"+RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SINGLE_CACHE_SUCCESS, new String[]{s3});
	            		}else{
	            			log.debug(methodName, "CacheName:" + mp1.get(s1[1]));
	            			s3=s3+mp1.get(s1[1])+", ";
	            		}
	            	}else{
	            		s3=s3.substring(0,s3.length()-2);
	            		msg[index++]=s2+":"+RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SINGLE_CACHE_SUCCESS, new String[]{s3});
	            		s2=s1[0];
	            		if(i == (arrList.size()-1))
	            			msg[index++]=s2+":"+RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SINGLE_CACHE_SUCCESS, new String[]{s3});
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
		            		msgF[indexF++]=s2+":"+RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SINGLE_CACHE_FAIL, new String[]{s3});
	            		}else{
	            			s3=s3+mp1.get(s1[1])+", ";
	            		}
	            	}else{
	            		s3=s3.substring(0,s3.length()-2);
	            		msgF[indexF++]=s2+":"+RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SINGLE_CACHE_FAIL, new String[]{s3});
	            		s2=s1[0];
	            		if(i == (arrListF.size()-1))
	            			msgF[indexF++]=s2+":"+RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SINGLE_CACHE_FAIL, new String[]{s3});
	            		s3=mp1.get(s1[1])+", ";
	            	}
	            }
			}
			
            int countS = 0;
            int countF = 0;
            		
            for(int i=0 ; i<msg.length; i++) {
                if(msg[i] != null) { 
                	countS += 1;
                	AdminOperationVO adminOperationVO = new AdminOperationVO();
	                adminOperationVO.setSource(TypesI.NETWORK_ADMIN);
	                adminOperationVO.setDate(new Date());
	                adminOperationVO.setOperation("UPDATE_CACHE");
	                adminOperationVO.setInfo( msg[i] );
	                
	                adminOperationVO.setLoginID(userVO.getLoginID());
	                adminOperationVO.setUserID(userVO.getUserID());
	                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
	                adminOperationVO.setNetworkCode(userVO.getNetworkID());
	                adminOperationVO.setMsisdn(userVO.getMsisdn());
	                AdminOperationLog.log(adminOperationVO);
                }
             }
            
            for(int i=0 ; i<msgF.length; i++) {
                if(msgF[i] != null) { 
                	countF += 1;
                }
             }
			
        	log.info(methodName, "End UpdateCacheServiceImpl  ................... ");
        	
           	downloadLogFile(msg, msgF, response);
           	
        	response.setMsg(msg);
        	response.setMsgF(msgF);
        	response.setCountS(countS);
        	response.setCountF(countF);
        	
           	if (countS == instanceValues.length) {
    			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPDATE_CACHE_SUCCESS, null);
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.UPDATE_CACHE_SUCCESS);
    			response.setStatus((HttpStatus.SC_OK));
           	}
           	else if (countS > 0 && countF >0) {
    			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPDATE_CACHE_PARTIAL, null);
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.UPDATE_CACHE_PARTIAL);
    			response.setStatus((HttpStatus.SC_BAD_REQUEST));
           	}
           	else {
           		String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPDATE_CACHE_FAIL, null);
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.UPDATE_CACHE_FAIL);
    			response.setStatus((HttpStatus.SC_BAD_REQUEST));
           	}
			
        } catch (Exception e) {
            log.error(methodName, "BTSLBaseException " + e.getMessage());
            log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.UPDATE_CACHE_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.UPDATE_CACHE_FAIL);
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited ");
        }
		
		return response;
	}
	
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
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UpdateCacheServiceImpl[loadInstances]", "", "", "", "Exception while loading instances  Getting =" + e.getMessage());
        }// end of catch
        finally {
        	if(mcomCon != null){
        		mcomCon.close("UpdateCacheServiceImpl#loadInstances");
        		mcomCon=null;
        		}
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting list.size " + list.size());
            }
        }// end of finally

        return list;
    }

	@Override
	public UpdateCacheResponseVO updateRedisCache(Connection con,UserVO userVO, MComConnectionI mcomCon, Locale locale,
			UpdateCacheRequestVO request) {
		
		final String methodName =  "updateRedisCache";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		UpdateCacheResponseVO response = null;
		response = new UpdateCacheResponseVO();
		
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
		
        String serverInfo = Constants.getProperty("SERVER_INFO_REDIS");
	    (new UpdateRedisCacheServlet()).updateFilePath(serverInfo);
        
        try {
        	
            List<String> arrList = Collections.synchronizedList(new ArrayList<String>());
            List<String> arrListF = Collections.synchronizedList(new ArrayList<String>());
            cacheValues = request.getCacheList();
            
            if (cacheValues == null ) {
            	response.setMessage(RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.EMPTY_CACHE_LIST, null));
            	response.setMessageCode(PretupsErrorCodesI.EMPTY_CACHE_LIST);
                return response;
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
   				 log.error("updateCache", "Exceptin:e=" + e);
   		         log.errorTrace(methodName, e);
   			}
   			finally{
   				if (mcomCon != null) {
   					mcomCon.close("UpdateCacheServiceImpl#updateRedisCache");
   					mcomCon = null;
   				}
   			}
   			log.debug(methodName, "CacheCode:CacheName" + mp1);

   			Collections.sort(arrList);
   			synchronized(arrList) {
   				msg = new String[arrList.size()];
   	            for(int i=0;i<arrList.size();i++){
   	            	String s1 = ((String)arrList.get(i));
   	            	StringBuffer s3=new StringBuffer();;
   	            	s3.append(mp1.get(s1));
           			msg[index++]=BTSLUtil.getMessage(locale, PretupsErrorCodesI.SINGLE_CACHE_SUCCESS, new String[]{s3.toString()});
           			log.debug(methodName, "CacheName:" + mp1.get(s1));
   	            	
   	            }
   			}
   			index = 0;
   			Collections.sort(arrListF);
   			synchronized(arrListF) {
   				msgF = new String[arrListF.size()];
   	            for(int i=0;i<arrListF.size();i++){
   	            	String s1 = ((String)arrListF.get(i));
   	            	StringBuilder s3 = new StringBuilder();
   	            	s3.append(mp1.get(s1));
           			msgF[index++]=BTSLUtil.getMessage(locale, PretupsErrorCodesI.SINGLE_CACHE_FAIL, new String[]{s3.toString()});
           			log.debug(methodName, "CacheName:" + mp1.get(s1));
   	            	
   	            }
   			}
   			
            int countS = 0;
            int countF = 0;
            		
            for(int i=0 ; i<msg.length; i++) {
                if(msg[i] != null) { 
                	 AdminOperationVO adminOperationVO = new AdminOperationVO();
		                adminOperationVO.setSource(TypesI.NETWORK_ADMIN);
		                adminOperationVO.setDate(new Date());
		                adminOperationVO.setOperation("UPDATE_CACHE");
		                adminOperationVO.setInfo( msg[i] );
		                
		                adminOperationVO.setLoginID(userVO.getLoginID());
		                adminOperationVO.setUserID(userVO.getUserID());
		                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
		                adminOperationVO.setNetworkCode(userVO.getNetworkID());
		                adminOperationVO.setMsisdn(userVO.getMsisdn());
		                AdminOperationLog.log(adminOperationVO);
                	countS += 1;
                }
             }
            
            for(int i=0 ; i<msgF.length; i++) {
                if(msgF[i] != null) { 
                	countF += 1;
                }
             }
   			
           	log.info(methodName, "End UpdateCacheServiceImpl  ................... ");
           	
           	downloadLogFile(msg, msgF, response);
           	
        	response.setCountS(countS);
        	response.setCountF(countF);
        	response.setMsg(msg);
        	response.setMsgF(msgF);	
        	
           	if (countS == cacheValues.length) {
    			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPDATE_CACHE_SUCCESS, null);
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.UPDATE_CACHE_SUCCESS);
    			response.setStatus((HttpStatus.SC_OK));
           	}
           	else if (countS > 0 && countF >0) {
    			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPDATE_CACHE_PARTIAL, null);
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.UPDATE_CACHE_PARTIAL);
    			response.setStatus((HttpStatus.SC_BAD_REQUEST));
           	}
           	else {
           		String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPDATE_CACHE_FAIL, null);
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.UPDATE_CACHE_FAIL);
    			response.setStatus((HttpStatus.SC_BAD_REQUEST));
           	}
        	
        } catch (Exception e) {
            log.error(methodName, "BTSLBaseException " + e.getMessage());
            log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					 PretupsErrorCodesI.UPDATE_CACHE_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.UPDATE_CACHE_FAIL);
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited ");
        }
        
		return response;
	}
	
	 public void downloadLogFile(String [] msg, String[] msgF, UpdateCacheResponseVO response) 
	 {	
		 final String methodName = "downloadLogFile";
	    Writer out =null;
	    File newFile = null;
	    File newFile1 = null;
	    Date date= new Date();
		if (log.isDebugEnabled())
			log.debug(methodName, "Entered");
		
		try {
			String filePath = Constants.getProperty("DownloadErLogFilePath");
			try
			{
				File fileDir = new File(filePath);
				if(!fileDir.isDirectory())
					fileDir.mkdirs();
			}
			catch(Exception e)
			{			
				log.errorTrace(methodName,e);
				log.error(methodName,"Exception" + e.getMessage());
				throw new BTSLBaseException(this,methodName,"bulkuser.processuploadedfile.downloadfile.error.dirnotcreated");
			}
			
			String _fileName = "UpdateCacheLog"+BTSLUtil.getFileNameStringFromDate(new Date())+".txt";
		    newFile1=new File(filePath);
            if(! newFile1.isDirectory())
	         	 newFile1.mkdirs();
            String absolutefileName=filePath+_fileName;
			
            newFile = new File(absolutefileName);
            out = new OutputStreamWriter(new FileOutputStream(newFile));
			String failMsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.UPDATE_CACHE_FAIL, null);
            out.write(failMsg +"\n");
            
			if(!Constants.getProperty("REDIS_ENABLE").equals(PretupsI.REDIS_ENABLE)) {
				if (msgF.length > 0) {
		            for (int i=0; i < msgF.length; i++) {
		            	if (msgF[i] != null) {		            		
			            	String fail = msgF[i];
			            	String[] parts = fail.split(":");
			            	String instID = parts[0];
			            	String message = parts[1];
			            	out.write(instID + ",");
			            	out.write(message + ",");
				        	out.write(",");
				        	out.write("\n");
		            	}
		            }
				}
				else {
		        	out.write("No data found" + ",");
		        	out.write("\n");
				}
	            out.write("\n");
	            
				String successMsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						PretupsErrorCodesI.UPDATE_CACHE_SUCCESS, null);
	            out.write(successMsg +"\n");
	            
	            if (msg.length > 0) {
		            for (int i=0; i < msg.length; i++) {
		            	if (msg[i] != null) {		            		
			            	String success = msg[i];
			            	String[] parts = success.split(":");
			            	String instID = parts[0];
			            	String message = parts[1];
			            	out.write(instID + ",");
			            	out.write(message + ",");
				        	out.write(",");
				        	out.write("\n");
		            	}
		            }
	            }
				else {
		        	out.write("No data found" + ",");
		        	out.write("\n");
				}
			}
			else {
				if (msgF.length > 0) {
		            for (int i=0; i < msgF.length; i++) {
		            	if (msgF[i] != null) {		            		
			            	String fail = msgF[i];
			            	out.write(fail + ",");
				        	out.write(",");
				        	out.write("\n");
		            	}
		            }
				}
				else {
		        	out.write("No data found" + ",");
		        	out.write("\n");
				}
	            out.write("\n");
	            
				String successMsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						PretupsErrorCodesI.UPDATE_CACHE_SUCCESS, null);
	            out.write(successMsg +"\n");
	            
	            if (msg.length > 0) {
		            for (int i=0; i < msg.length; i++) {
		            	if (msg[i] != null) {		            		
			            	String success = msg[i];
			            	out.write(success + ",");
				        	out.write(",");
				        	out.write("\n");
		            	}
		            }
	            }
				else {
		        	out.write("No data found" + ",");
		        	out.write("\n");
				}
			}
            
        	out.write("\n");
 			out.close();
 			File error =new File(absolutefileName);
			byte[] fileContent = FileUtils.readFileToByteArray(error);
	   		String encodedString = Base64.getEncoder().encodeToString(fileContent);	   		
	   		response.setFileAttachment(encodedString);
	   		response.setFileName(_fileName);
		}
		catch (Exception e)
		{
			log.error(methodName,"Exception:e="+e);
			log.errorTrace(methodName,e);
		}
		finally
		{
	      	if (log.isDebugEnabled()){
	      		log.debug(methodName,"Exiting... ");
	      	}
	        if (out!=null)
	        try{
	          	out.close();
	          	}
	         catch(Exception e){
	         	log.errorTrace(methodName, e);
	        }
		}  
	 }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
