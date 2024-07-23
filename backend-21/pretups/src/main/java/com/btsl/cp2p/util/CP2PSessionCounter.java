package com.btsl.cp2p.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.cp2p.subscriber.businesslogic.CP2PSubscriberVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginLogger;
import com.btsl.login.LoginLoggerVO;
import com.btsl.user.businesslogic.RoleHitTimeVO;
import com.btsl.user.businesslogic.SessionInfoVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class CP2PSessionCounter implements HttpSessionListener {

	private static Log _log = LogFactory.getLog(CP2PSessionCounter.class.getName());
    private static int activeSessions = 0;
    private static Hashtable hashTable = new Hashtable();
    private static Hashtable subscriberCounters = new Hashtable();
    // private HttpSession session;
    private static Hashtable underProcessRoleHash = new Hashtable();
    private static long currentTotalUnderProcess = 0;            
    private static long maxTotalUnderProcess = 0;           

    /* Session Creation Event */
    public void sessionCreated(HttpSessionEvent se) {
        if (_log.isDebugEnabled())
            _log.debug("sessionCreated", "Entered se:" + se);
        HttpSession session = se.getSession();

        if (session != null) {
            if (_log.isDebugEnabled())
                _log.debug("sessionCreated", "Got ID as =" + session.getId());
            hashTable.put(session.getId(), session);
        }
        activeSessions++;
        if (_log.isDebugEnabled())
            _log.debug("sessionCreated", "Exiting se:" + se + " Htable size=" + hashTable.size());
    }

    /* Session Invalidation Event */
    public void sessionDestroyed(HttpSessionEvent se) {     
        if (_log.isDebugEnabled())
            _log.debug("sessionDestroyed", "Entered se:" + se);

        HttpSession session = se.getSession();

        if (session != null) {

            try {
                if (_log.isDebugEnabled())
                    _log.debug("sessionDestroyed", "Got ID as =" + session.getId());
                removeSubscriberFromCounters(session.getId());
                if (_log.isDebugEnabled())
                    _log.debug("sessionDestroyed", "After Removing Subscriber From Counters for " + session.getId() + " Size=" + hashTable.size());
                hashTable.remove(session.getId());
                if (_log.isDebugEnabled())
                    _log.debug("sessionDestroyed", "After Removing Subscriber From HashTable for " + session.getId() + " Size=" + hashTable.size());
            } catch (Exception e) {
                if (_log.isDebugEnabled())
                    _log.debug("sessionDestroyed", "Exception while destroying session e:" + e);
            }
        }
        if (activeSessions > 0)
            activeSessions--;
        if (_log.isDebugEnabled())
            _log.debug("sessionDestroyed", "Exiting se:" + se);
    }

    public static int getActiveSessions() {
        return activeSessions;
    }

    public static Hashtable getActiveSessionsHash() {
        return hashTable;
    }

    public static void checkMaxLocationTypeUsers(CP2PSubscriberVO cp2pSubscriberVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkMaxLocationTypeUsers", "Entered cp2pSubscriberVO:" + cp2pSubscriberVO);
        boolean checks = true;
        String disableLocationTypeCheck = BTSLUtil.NullToString(Constants.getProperty("DISABLE_MULTIPLE_LOGINS_CHECK"));
        if (_log.isDebugEnabled())
            _log.debug("checkMaxLocationTypeUsers", "disableLocationTypeCheck: " + disableLocationTypeCheck);
        if (BTSLUtil.isStringIn(cp2pSubscriberVO.getCategory(), disableLocationTypeCheck))
            checks = false;
        // checking maximum allowable users for particular location and type
        CP2PSessionVO locationSessionVO = null;
        CP2PSessionVO typeSessionVO = null;
        ArrayList userList = null;
        boolean isCheckRequired = false;
            /*
             * locationSessionVO=new SessionVO();
             * Hashtable useTypeHash=new Hashtable(15);
             * typeSessionVO=new SessionVO();
             * userList=new ArrayList();
             * userList.add(userVO);
             * typeSessionVO.incrementCounter();
             * typeSessionVO.setList(userList);
             * useTypeHash.put(userVO.getCategoryCode(),typeSessionVO);
             * locationSessionVO.incrementCounter();
             * locationSessionVO.setList(useTypeHash);
             * userCounters.put(userVO.getNetworkID(),locationSessionVO);
             */
            synchronized (CP2PSessionCounter.class) {
                locationSessionVO = (CP2PSessionVO) subscriberCounters.get(cp2pSubscriberVO.getNetworkId());
                if (_log.isDebugEnabled())
                    _log.debug("checkMaxLocationTypeUsers", "Attempt II Got for Session ID=" + cp2pSubscriberVO.getSessionInfoVO().getSessionID() + " locationSessionVO=" + locationSessionVO);

                if (locationSessionVO == null) {
                    locationSessionVO = new CP2PSessionVO();
                    Hashtable useTypeHash = new Hashtable(15);
                    typeSessionVO = new CP2PSessionVO();
                    userList = new ArrayList();
                    userList.add(cp2pSubscriberVO);
                    typeSessionVO.incrementCounter();
                    typeSessionVO.setList(userList);
                    useTypeHash.put(cp2pSubscriberVO.getCategory(), typeSessionVO);
                    locationSessionVO.incrementCounter();
                    locationSessionVO.setList(useTypeHash);
                    subscriberCounters.put(cp2pSubscriberVO.getNetworkId(), locationSessionVO);
                    if (_log.isDebugEnabled())
                    {
                    	StringBuffer msg=new StringBuffer("");
                    	msg.append("userVO ID=");
                    	msg.append(cp2pSubscriberVO.getUserID());
                    	msg.append(" Login ID =");
                    	msg.append(cp2pSubscriberVO.getLoginId());                    	
                    	msg.append(" userList size=");
                    	msg.append(userList.size());
                    	
                    	String message=msg.toString();
                        _log.debug("checkMaxLocationTypeUsers", message);
                    }
                    
                    } else {
                    	 if (_log.isDebugEnabled())
                    	 {
                    		StringBuffer msg=new StringBuffer("");
                         	msg.append("Attempt II Got for Session ID=");
                         	msg.append(cp2pSubscriberVO.getSessionInfoVO().getSessionID());
                         	msg.append(" locationSessionVO=");
                         	msg.append(locationSessionVO);                    	
                         	msg.append(" setting isCheckRequired=true");                         	
                         	
                         	String message=msg.toString();
                    		 _log.debug("checkMaxLocationTypeUsers", message);
                    	 }
                    	 
                    	 isCheckRequired = true;
                    }
            }
        if (isCheckRequired) {
            Hashtable userTypeHash = (Hashtable) locationSessionVO.getList();
            if (_log.isDebugEnabled())
            {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Got for Session ID=");
            	msg.append(cp2pSubscriberVO.getSessionInfoVO().getSessionID());
            	msg.append(" userTypeHash=");
            	msg.append(userTypeHash);
        
            	String message=msg.toString();
                _log.debug("checkMaxLocationTypeUsers", message);
            }
            
            int maxLocationCount = 2;
            try {
                maxLocationCount = Integer.parseInt(Constants.getProperty("MAX_LOGINS_LOCATION_" + cp2pSubscriberVO.getNetworkId()));
            } catch (Exception e) {
                _log.errorTrace("Exception in method checkMaxLocationTypeUsers() ", e);
                maxLocationCount = 60;
            }
            if (checks && (locationSessionVO.getCounter() >= maxLocationCount)) {
                throw new BTSLBaseException("SessionCounter", "checkMaxLocationTypeUsers", "user.sessioncounter.mesage.errormaxlocationcount", "cp2plogin");
            }
            if (userTypeHash != null) {
                typeSessionVO = (CP2PSessionVO) userTypeHash.get(cp2pSubscriberVO.getCategory());
                if (typeSessionVO == null) {
                    locationSessionVO.incrementCounter();
                    typeSessionVO = new CP2PSessionVO();
                    userList = new ArrayList();
                    userList.add(cp2pSubscriberVO);
                    typeSessionVO.incrementCounter();
                    typeSessionVO.setList(userList);
                    userTypeHash.put(cp2pSubscriberVO.getCategory(), typeSessionVO);

                } else {
                    userList = (ArrayList) typeSessionVO.getList();
                    // check for login i.e maximum number of users
                    long maxTypeCount = 2;
                    try {
                        maxTypeCount = Integer.parseInt(Constants.getProperty("MAX_LOGINS_TYPE_" + cp2pSubscriberVO.getNetworkId() + "_" + cp2pSubscriberVO.getCategory()));
                    } catch (Exception e) {
                        _log.errorTrace("Exception in method checkMaxLocationTypeUsers() ", e);
                        // try
                        // {
                        // maxTypeCount=cp2pSubscriberVO.getCategoryVO().getMaxLoginCount();
                        // }
                        // catch(Exception ex)
                        // {
                        // maxTypeCount=10;
                        // }
                    }
                    if (_log.isDebugEnabled())
                    {
                    	StringBuffer msg=new StringBuffer("");
                    	msg.append("Got for Session ID=");
                    	msg.append(cp2pSubscriberVO.getSessionInfoVO().getSessionID());
                    	msg.append("userVO ID=");
                    	msg.append(cp2pSubscriberVO.getUserID());
                    	msg.append(" Login ID =");
                    	msg.append(cp2pSubscriberVO.getLoginId());
                    	msg.append(" maxLocationCount=");
                    	msg.append(maxLocationCount);
                    	msg.append("     maxTypeCount=");
                    	msg.append(maxTypeCount);
                    	msg.append("locationSessionVO.getCounter()=");
                    	msg.append(locationSessionVO.getCounter());
                    	msg.append("     typeSessionVO.getCounter()=");
                    	msg.append(typeSessionVO.getCounter());
                    	
                    	String message=msg.toString();
                        _log.debug("checkMaxLocationTypeUsers", message);
                    }
                    
                    if (checks && (typeSessionVO.getCounter() >= maxTypeCount)) {
                        throw new BTSLBaseException("SessionCounter", "checkMaxLocationTypeUsers", "user.sessioncounter.mesage.errormaxtypecountreached", "cp2plogin");
                    }
                    locationSessionVO.incrementCounter();
                    typeSessionVO.incrementCounter();
                    userList.add(cp2pSubscriberVO);
                    if (_log.isDebugEnabled())
                    {
                    	StringBuffer msg=new StringBuffer("");
                    	msg.append("Got for Session ID=");
                    	msg.append(cp2pSubscriberVO.getSessionInfoVO().getSessionID());
                    	msg.append("userVO ID=");
                    	msg.append(cp2pSubscriberVO.getUserID());
                    	msg.append(" Login ID =");
                    	msg.append(cp2pSubscriberVO.getLoginId());
                    	msg.append(" userList size=");
                    	msg.append(userList.size());                    	
                    	
                    	String message=msg.toString();
                        _log.debug("checkMaxLocationTypeUsers", message);
                    }
                    
                    typeSessionVO.setList(userList);
                }
            }
        }
        if (_log.isDebugEnabled())
        {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Exiting for Got for Session ID=");
        	msg.append(cp2pSubscriberVO.getSessionInfoVO().getSessionID());
        	msg.append("userVO ID=");
        	msg.append(cp2pSubscriberVO.getUserID());
        	msg.append(" Login ID =");
        	msg.append(cp2pSubscriberVO.getLoginId());
        	msg.append(" locationSessionVO.getCounter()");
        	msg.append(locationSessionVO.getCounter());
        	msg.append("   typeSessionVO.getCounter()=");
        	msg.append(typeSessionVO.getCounter());
        	
        	String message=msg.toString();
            _log.debug("checkMaxLocationTypeUsers", message);    
        }
    }
    /**
     * removeUserFromCounters
     * 
     * @param sessionID
     */
    public void removeSubscriberFromCounters(String sessionID) {
        final String METHOD_NAME = "removeSubscriberFromCounters";
        try {
            if (_log.isDebugEnabled())
                _log.debug("removeUserFromCounters", "Entered ::" + sessionID);
            CP2PSessionVO locationSessionVO = null;
            CP2PSessionVO typeSessionVO = null;
            ArrayList userList = null;
            Enumeration enumUser = subscriberCounters.keys();
            String key = null;
            CP2PSubscriberVO cp2pSubscriberVO = null;
            LoginLoggerVO loginLoggerVO = null;
            Enumeration enumTypes = null;
            String keyType = null;
            Hashtable userTypeHash = null;
            while (enumUser.hasMoreElements()) {
                key = (String) enumUser.nextElement();
                locationSessionVO = (CP2PSessionVO) subscriberCounters.get(key);
                if (locationSessionVO != null) {
                    userTypeHash = (Hashtable) locationSessionVO.getList();
                    if (userTypeHash != null) {
                        enumTypes = userTypeHash.keys();
                        while (enumTypes.hasMoreElements()) {
                            keyType = (String) enumTypes.nextElement();
                            typeSessionVO = (CP2PSessionVO) userTypeHash.get(keyType);
                            userList = (ArrayList) typeSessionVO.getList();
                            for (int j = 0; j < userList.size(); j++) {
                                cp2pSubscriberVO = (CP2PSubscriberVO) userList.get(j);
                                if (cp2pSubscriberVO == null || cp2pSubscriberVO.getSessionInfoVO() == null) {
                                    if (_log.isDebugEnabled())
                                        _log.debug("removeUserFromCounters", "UserVO IS NULL*******");
                                    continue;
                                }
                                if (cp2pSubscriberVO.getSessionInfoVO().getSessionID().equals(sessionID)) {
                                    if (_log.isDebugEnabled())
                                        _log.debug("removeUserFromCounters", "removing userList.size()=" + userList.size() + "	Login ID=" + cp2pSubscriberVO.getLoginId() + " User ID =" + cp2pSubscriberVO.getUserID() + " user :Session ID=" + sessionID + " userVO.getLoggerMessage()=" + cp2pSubscriberVO.getLoggerMessage());
                                    locationSessionVO.decrementCounter();
                                    typeSessionVO.decrementCounter();
                                    userList.remove(cp2pSubscriberVO);

                                    loginLoggerVO = new LoginLoggerVO();
                                    // set the logout details for logging
                                    loginLoggerVO.setLoginID(cp2pSubscriberVO.getLoginId());
                                    loginLoggerVO.setUserID(cp2pSubscriberVO.getUserID());
                                    loginLoggerVO.setNetworkID(cp2pSubscriberVO.getNetworkId());
                                    loginLoggerVO.setNetworkName(cp2pSubscriberVO.getNetworkName());
                                    loginLoggerVO.setUserName(cp2pSubscriberVO.getUserName());
                                    loginLoggerVO.setUserType(cp2pSubscriberVO.getSubscriberType());
                                    loginLoggerVO.setDomainID(cp2pSubscriberVO.getDomainId());
                                    loginLoggerVO.setCategoryCode(cp2pSubscriberVO.getCategory());
                                    loginLoggerVO.setLoginTime(cp2pSubscriberVO.getLoginTime());
                                    loginLoggerVO.setLogoutTime(new Date());
                                    loginLoggerVO.setIpAddress(cp2pSubscriberVO.getRemoteAddress());
                                    loginLoggerVO.setBrowser(cp2pSubscriberVO.getBrowserType());
                                    if (BTSLUtil.isNullString(cp2pSubscriberVO.getLoggerMessage())) {
                                        loginLoggerVO.setLogType(TypesI.LOG_TYPE_EXPIRED);
                                        loginLoggerVO.setOtherInformation(Constants.getProperty("SESSION_EXPIRED") + " 	" + sessionID);
                                    } else {
                                        loginLoggerVO.setLogType(TypesI.LOG_TYPE_LOGOUT);
                                        loginLoggerVO.setOtherInformation(cp2pSubscriberVO.getLoggerMessage() + "		" + sessionID);
                                    }
                                    LoginLogger.log(loginLoggerVO);

                                    break;
                                }
                            }
                            /*
                             * Commented as we dont want the key to be removed
                             * from hashtable
                             * if(userList.isEmpty())
                             * userTypeHash.remove(keyType);
                             */
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (_log.isDebugEnabled())
                _log.debug("removeUserFromCounters", "Exception e:" + e);
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    /**
     * invalidateUserFromCounters
     * 
     * @param p_userVO
     */
    public static void invalidateUserFromCounters(CP2PSubscriberVO p_cp2pSubscriberVO) {
        final String METHOD_NAME = "invalidateUserFromCounters";
        try {
            if (_log.isDebugEnabled())
                _log.debug("invalidateUserFromCounters", "Entered ::" + p_cp2pSubscriberVO.getCreatedBy());
            CP2PSessionVO locationSessionVO = null;
            CP2PSessionVO typeSessionVO = null;
            ArrayList userList = null;
            Enumeration enumUser = subscriberCounters.keys();
            String key = null;
            CP2PSubscriberVO cp2pSubscriberVO = null;
            LoginLoggerVO loginLoggerVO = null;
            Enumeration enumTypes = null;
            String keyType = null;
            Hashtable userTypeHash = null;
            while (enumUser.hasMoreElements()) {
                key = (String) enumUser.nextElement();
                locationSessionVO = (CP2PSessionVO) subscriberCounters.get(key);
                if (locationSessionVO != null) {
                    userTypeHash = (Hashtable) locationSessionVO.getList();
                    if (userTypeHash != null) {
                        enumTypes = userTypeHash.keys();
                        while (enumTypes.hasMoreElements()) {
                            keyType = (String) enumTypes.nextElement();
                            typeSessionVO = (CP2PSessionVO) userTypeHash.get(keyType);
                            userList = (ArrayList) typeSessionVO.getList();
                            for (int j = 0; j < userList.size(); j++) {
                                cp2pSubscriberVO = (CP2PSubscriberVO) userList.get(j);
                                if (cp2pSubscriberVO == null)
                                    continue;
                                if (cp2pSubscriberVO.getCreatedBy().equals(p_cp2pSubscriberVO.getCreatedBy())) {
                                    if (_log.isDebugEnabled())
                                        _log.debug("invalidateUserFromCounters", "removing user ::" + cp2pSubscriberVO.getCreatedBy() + "     sessionID=" + cp2pSubscriberVO.getSessionInfoVO().getSessionID());
                                    locationSessionVO.decrementCounter();
                                    typeSessionVO.decrementCounter();
                                    userList.remove(cp2pSubscriberVO);
                                    // invalidating session
                                    try {
                                        HttpSession usersession = (HttpSession) hashTable.get(cp2pSubscriberVO.getSessionInfoVO().getSessionID());
                                        usersession.invalidate();
                                    } catch (Exception e) {
                                        _log.errorTrace(METHOD_NAME, e);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.error("invalidateUserFromCounters", "Exception e:" + e);
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    public static void incrementCounters(HttpServletRequest p_request) {
        final String METHOD_NAME = "incrementCounters";
        try {
            String sessionID = p_request.getSession().getId();
            if (_log.isDebugEnabled())
                _log.debug("incrementCounters", " Entered ::" + sessionID);
            CP2PSessionVO locationSessionVO = null;
            CP2PSessionVO typeSessionVO = null;
            ArrayList userList = null;
            Enumeration enumUser = subscriberCounters.keys();
            String key = null;
            SessionInfoVO sessionInfoVO = null;
            Hashtable userTypeHash = null;
            Enumeration enumTypes = null;
            String keyType = null;
            CP2PSubscriberVO cp2pSubscriberVO = null;
            String roleCode = null;
            RoleHitTimeVO uroleHitTimeVO = null;
            RoleHitTimeVO sroleHitTimeVO = null;
            while (enumUser.hasMoreElements()) {
                key = (String) enumUser.nextElement();
                locationSessionVO = (CP2PSessionVO) subscriberCounters.get(key);
                if (locationSessionVO != null) {
                    userTypeHash = (Hashtable) locationSessionVO.getList();
                    if (userTypeHash != null) {
                        enumTypes = userTypeHash.keys();
                        while (enumTypes.hasMoreElements()) {
                            keyType = (String) enumTypes.nextElement();
                            typeSessionVO = (CP2PSessionVO) userTypeHash.get(keyType);
                            userList = (ArrayList) typeSessionVO.getList();
                            for (int j = 0; j < userList.size(); j++) {
                                cp2pSubscriberVO = (CP2PSubscriberVO) userList.get(j);
                                if (cp2pSubscriberVO == null)
                                    continue;
                                sessionInfoVO = cp2pSubscriberVO.getSessionInfoVO();
                                if (sessionInfoVO != null && sessionInfoVO.getSessionID().equals(sessionID) && !sessionInfoVO.isUnderProcess()) {
                                    synchronized (CP2PSessionCounter.class) {
                                        roleCode = getRoleCode(p_request);
                                        sessionInfoVO.setCurrentRoleCode(roleCode);
                                        if (_log.isDebugEnabled())
                                            _log.debug("incrementCounters", "roleCode::" + roleCode + " sessionInfoVO: " + sessionInfoVO);
                                        sessionInfoVO.setUnderProcess(true);
                                      
                                        sessionInfoVO.setTotalHit(sessionInfoVO.getTotalHit() + 1);
                                        currentTotalUnderProcess++;
                                        if (maxTotalUnderProcess < currentTotalUnderProcess)
                                            maxTotalUnderProcess = currentTotalUnderProcess;
                                        uroleHitTimeVO = (RoleHitTimeVO) (sessionInfoVO.getRoleHitTimeMap().get(sessionInfoVO.getCurrentRoleCode()));
                                        if (uroleHitTimeVO != null) {
                                            uroleHitTimeVO.setTotalHits(uroleHitTimeVO.getTotalHits() + 1);
                                            uroleHitTimeVO.setTotalUnderProcess(uroleHitTimeVO.getTotalUnderProcess() + 1);
                                            uroleHitTimeVO.setLastAccessTime(System.currentTimeMillis());
                                        } else {
                                            uroleHitTimeVO = new RoleHitTimeVO(sessionInfoVO.getCurrentRoleCode(), 1, 0, System.currentTimeMillis());
                                            uroleHitTimeVO.setTotalUnderProcess(1);
                                            sessionInfoVO.getRoleHitTimeMap().put(sessionInfoVO.getCurrentRoleCode(), uroleHitTimeVO);
                                        }
                                        // increase overall under process hit---
                                        sroleHitTimeVO = (RoleHitTimeVO) underProcessRoleHash.get(sessionInfoVO.getCurrentRoleCode());
                                        if (sroleHitTimeVO != null) {
                                            sroleHitTimeVO.setTotalHits(sroleHitTimeVO.getTotalHits() + 1);
                                            sroleHitTimeVO.setTotalUnderProcess(sroleHitTimeVO.getTotalUnderProcess() + 1);
                                            sroleHitTimeVO.setLastAccessTime(System.currentTimeMillis());
                                        } else {
                                            sroleHitTimeVO = new RoleHitTimeVO(sessionInfoVO.getCurrentRoleCode(), 1, 1);
                                            sroleHitTimeVO.setTotalHits(1);
                                            sroleHitTimeVO.setTotalUnderProcess(1);
                                            sroleHitTimeVO.setLastAccessTime(System.currentTimeMillis());
                                            underProcessRoleHash.put(sessionInfoVO.getCurrentRoleCode(), sroleHitTimeVO);

                                        }
                                    }
                                    break;
                                }

                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.error("incrementCounters", " Exception e:" + e);
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    public static void decrementCounters(HttpServletRequest p_request) {
        final String METHOD_NAME = "decrementCounters";
        try {
            String sessionID = p_request.getSession().getId();

            if (_log.isDebugEnabled())
                _log.debug("decrementCounters", "Entered ::" + sessionID);
            CP2PSessionVO locationSessionVO = null;
            CP2PSessionVO typeSessionVO = null;
            ArrayList userList = null;
            Enumeration enumUser = subscriberCounters.keys();
            String key = null;
            SessionInfoVO sessionInfoVO = null;
            Hashtable userTypeHash = null;
            Enumeration enumTypes = null;
            String keyType = null;
            CP2PSubscriberVO cp2pSubscriberVO = null;
            String roleCode = null;
            RoleHitTimeVO uroleHitTimeVO = null;
            RoleHitTimeVO sroleHitTimeVO = null;
            while (enumUser.hasMoreElements()) {
                key = (String) enumUser.nextElement();
                locationSessionVO = (CP2PSessionVO) subscriberCounters.get(key);
                if (locationSessionVO != null) {
                    userTypeHash = (Hashtable) locationSessionVO.getList();
                    if (userTypeHash != null) {
                        enumTypes = userTypeHash.keys();
                        keyType = null;
                        while (enumTypes.hasMoreElements()) {
                            keyType = (String) enumTypes.nextElement();
                            typeSessionVO = (CP2PSessionVO) userTypeHash.get(keyType);
                            userList = (ArrayList) typeSessionVO.getList();
                            for (int j = 0; j < userList.size(); j++) {
                                cp2pSubscriberVO = (CP2PSubscriberVO) userList.get(j);
                                if (cp2pSubscriberVO == null)
                                    continue;
                                sessionInfoVO = cp2pSubscriberVO.getSessionInfoVO();
                                if (sessionInfoVO != null && sessionInfoVO.getSessionID().equals(sessionID) && sessionInfoVO.isUnderProcess()) {
                                    synchronized (CP2PSessionCounter.class) {
                                        roleCode = getRoleCode(p_request);
                                        sessionInfoVO.setCurrentRoleCode(roleCode);
                                        if (_log.isDebugEnabled())
                                            _log.debug("decrementCounters", "roleCode::" + roleCode + " sessionInfoVO: " + sessionInfoVO);
                                        sessionInfoVO.setUnderProcess(false);
                                        currentTotalUnderProcess--;
                                       
                                        uroleHitTimeVO = (RoleHitTimeVO) (sessionInfoVO.getRoleHitTimeMap().get(sessionInfoVO.getCurrentRoleCode()));
                                        if (uroleHitTimeVO != null) {
                                            uroleHitTimeVO.setTotalUnderProcess(uroleHitTimeVO.getTotalUnderProcess() - 1);
                                            uroleHitTimeVO.setTotalTime(uroleHitTimeVO.getTotalTime() + System.currentTimeMillis() - uroleHitTimeVO.getLastAccessTime());
                                        }
                                        // decrease overall under process hit---
                                        sroleHitTimeVO = (RoleHitTimeVO) underProcessRoleHash.get(sessionInfoVO.getCurrentRoleCode());
                                        if (sroleHitTimeVO != null) {
                                            sroleHitTimeVO.setTotalUnderProcess(sroleHitTimeVO.getTotalUnderProcess() - 1);
                                            sroleHitTimeVO.setTotalTime(sroleHitTimeVO.getTotalTime() + System.currentTimeMillis() - sroleHitTimeVO.getLastAccessTime());
                                        }
                                    }
                                    break;
                                }

                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.debug("decrementCounters", "Exception e:" + e);
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    private static String getRoleCode(HttpServletRequest p_request) {
        String roleCode = (String) p_request.getAttribute("roleCode");
        if (roleCode != null) {
            return roleCode;
        } else {
            String newUrlCode = p_request.getParameter("urlCode");
            String newModuleCode = p_request.getParameter("moduleCode");
            if (newUrlCode == null) {
                newUrlCode = (String) ((p_request.getSession()).getAttribute("urlCode"));

            }
            if (newModuleCode == null) {
                newModuleCode = (String) ((p_request.getSession()).getAttribute("moduleCode"));
            } else
                newUrlCode = "1";
            if (newUrlCode == null && newModuleCode != null)
                newUrlCode = "1";
            if (newUrlCode == null)
                newUrlCode = "ALL";
            if (newModuleCode == null)
                newModuleCode = "ALL";
            return (newModuleCode + newUrlCode);
        }
    }
}