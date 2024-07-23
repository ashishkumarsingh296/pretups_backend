package com.btsl.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginLogger;
import com.btsl.login.LoginLoggerVO;
import com.btsl.user.businesslogic.MonitorUserVO;
import com.btsl.user.businesslogic.RoleHitTimeVO;
import com.btsl.user.businesslogic.SessionInfoVO;
import com.btsl.user.businesslogic.UserVO;

public class SessionCounter implements HttpSessionListener {

    private static Log _log = LogFactory.getLog(SessionCounter.class.getName());
    private static int activeSessions = 0;
    private static Hashtable hashTable = new Hashtable();
    public static final Hashtable userCounters = new Hashtable();
    // private HttpSession session;
    private static Hashtable underProcessRoleHash = new Hashtable();
    private static long currentTotalUnderProcess = 0;
    private static long maxTotalUnderProcess = 0;
    public static final HashMap pushUrlMap = new HashMap();

    /* Session Creation Event */
    public void sessionCreated(HttpSessionEvent se) {
        if (_log.isDebugEnabled()) {
            _log.debug("sessionCreated", "Entered se:" + se);
        }
        HttpSession session = se.getSession();
        session = se.getSession();
        // System.out.println("session="+session);
        if (session != null) {
            if (_log.isDebugEnabled()) {
                _log.debug("sessionCreated", "Got ID as =" + session.getId());
            }
            hashTable.put(session.getId(), session);
        }
        activeSessions++;
        if (_log.isDebugEnabled()) {
            _log.debug("sessionCreated", "Exiting se:" + se + " Htable size=" + hashTable.size());
        }
    }

    /* Session Invalidation Event */
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        if (_log.isDebugEnabled()) {
            _log.debug("sessionDestroyed", "Entered se:" + se);
        }
        // System.out.println("Session destroyed se="+se);
        session = se.getSession();
        // System.out.println("session...="+session);
        if (session != null) {
            // System.out.println("session not null ");
            try {
                if (_log.isDebugEnabled()) {
                    _log.debug("sessionDestroyed", "Got ID as =" + session.getId());
                }
                removeUserFromCounters(session.getId());
                if (_log.isDebugEnabled()) {
                    _log.debug("sessionDestroyed", "Got ID as =" + session.getId());
                }
                removeMonitorUserFromCounters(session.getId());
                if (_log.isDebugEnabled()) {
                    _log.debug("sessionDestroyed", "After Removing User From Counters for " + session.getId() + " Size=" + hashTable.size());
                }
                hashTable.remove(session.getId());
                if (_log.isDebugEnabled()) {
                    _log.debug("sessionDestroyed", "After Removing User From HashTable for " + session.getId() + " Size=" + hashTable.size());
                }
            } catch (Exception e) {
                if (_log.isDebugEnabled()) {
                    _log.debug("sessionDestroyed", "Exception while destroying session e:" + e);
                }
            }
        }
        if (activeSessions > 0) {
            activeSessions--;
        }
        if (_log.isDebugEnabled()) {
            _log.debug("sessionDestroyed", "Exiting se:" + se);
        }
    }

    public static int getActiveSessions() {
        return activeSessions;
    }

    public static Hashtable getActiveSessionsHash() {
        return hashTable;
    }

    public static void checkMaxLocationTypeUsers(UserVO userVO) throws BTSLBaseException {
        final String METHOD_NAME = "checkMaxLocationTypeUsers";
        if (_log.isDebugEnabled()) {
            _log.debug("checkMaxLocationTypeUsers", "Entered userVO:" + userVO);
        }
        boolean checks = true;
        final String disableLocationTypeCheck = BTSLUtil.NullToString(Constants.getProperty("DISABLE_MULTIPLE_LOGINS_CHECK"));
        if (_log.isDebugEnabled()) {
            _log.debug("checkMaxLocationTypeUsers", "disableLocationTypeCheck: " + disableLocationTypeCheck);
        }
        if (BTSLUtil.isStringIn(userVO.getCategoryCode(), disableLocationTypeCheck)) {
            checks = false;
        }
        // checking maximum allowable users for particular location and type
        SessionVO locationSessionVO = null;
        SessionVO typeSessionVO = null;
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
            synchronized (SessionCounter.class) {
                locationSessionVO = (SessionVO) userCounters.get(userVO.getNetworkID());
                if (_log.isDebugEnabled()) {
                    _log.debug("checkMaxLocationTypeUsers",
                        "Attempt II Got for Session ID=" + userVO.getSessionInfoVO().getSessionID() + " locationSessionVO=" + locationSessionVO);
                }
  
                if (locationSessionVO == null) {
                    locationSessionVO = new SessionVO();
                    final Hashtable useTypeHash = new Hashtable(15);
                    typeSessionVO = new SessionVO();
                    userList = new ArrayList();
                    userList.add(userVO);
                    typeSessionVO.incrementCounter();
                    typeSessionVO.setList(userList);
                    useTypeHash.put(userVO.getCategoryCode(), typeSessionVO);
                    locationSessionVO.incrementCounter();
                    locationSessionVO.setList(useTypeHash);
                    userCounters.put(userVO.getNetworkID(), locationSessionVO);
                    if (_log.isDebugEnabled()) {
                        _log.debug("checkMaxLocationTypeUsers", "userVO ID=" + userVO.getUserID() + " Login ID =" + userVO.getLoginID() + " userList size=" + userList.size());
                    }
                } else {
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                            "checkMaxLocationTypeUsers",
                            "Attempt II Got for Session ID=" + userVO.getSessionInfoVO().getSessionID() + " locationSessionVO=" + locationSessionVO + " setting isCheckRequired=true");
                    }
                    isCheckRequired = true;
                }
            }
            
        if (isCheckRequired) {
            final Hashtable userTypeHash = (Hashtable) locationSessionVO.getList();
            if (_log.isDebugEnabled()) {
                _log.debug("checkMaxLocationTypeUsers", "Got for Session ID=" + userVO.getSessionInfoVO().getSessionID() + " userTypeHash=" + userTypeHash);
            }

            int maxLocationCount = 2;
            try {
                maxLocationCount = Integer.parseInt(Constants.getProperty("MAX_LOGINS_LOCATION_" + userVO.getNetworkID()));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                maxLocationCount = 60;
            }
            if (checks && (locationSessionVO.getCounter() >= maxLocationCount)) {
                throw new BTSLBaseException("SessionCounter", "checkMaxLocationTypeUsers", "user.sessioncounter.mesage.errormaxlocationcount", "index");
            }
            if (userTypeHash != null) {
                typeSessionVO = (SessionVO) userTypeHash.get(userVO.getCategoryCode());
                if (typeSessionVO == null) {
                    locationSessionVO.incrementCounter();
                    typeSessionVO = new SessionVO();
                    userList = new ArrayList();
                    userList.add(userVO);
                    typeSessionVO.incrementCounter();
                    typeSessionVO.setList(userList);
                    userTypeHash.put(userVO.getCategoryCode(), typeSessionVO);

                } else {
                    userList = (ArrayList) typeSessionVO.getList();
                    // check for login i.e maximum number of users
                    long maxTypeCount = 2;
                    try {
                        maxTypeCount = Integer.parseInt(Constants.getProperty("MAX_LOGINS_TYPE_" + userVO.getNetworkID() + "_" + userVO.getCategoryCode()));
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        try {
                            maxTypeCount = userVO.getCategoryVO().getMaxLoginCount();
                        } catch (Exception ex) {
                            _log.errorTrace(METHOD_NAME, ex);
                            maxTypeCount = 10;
                        }
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                            "checkMaxLocationTypeUsers",
                            "Got for Session ID=" + userVO.getSessionInfoVO().getSessionID() + "userVO ID=" + userVO.getUserID() + " Login ID =" + userVO.getLoginID() + " maxLocationCount=" + maxLocationCount + "     maxTypeCount=" + maxTypeCount + "locationSessionVO.getCounter()=" + locationSessionVO
                                .getCounter() + "     typeSessionVO.getCounter()=" + typeSessionVO.getCounter());
                    }
                    if (checks && (typeSessionVO.getCounter() >= maxTypeCount)) {
                        throw new BTSLBaseException("SessionCounter", "checkMaxLocationTypeUsers", "user.sessioncounter.mesage.errormaxtypecountreached", "index");
                    }
                    locationSessionVO.incrementCounter();
                    typeSessionVO.incrementCounter();
                    userList.add(userVO);
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                            "checkMaxLocationTypeUsers",
                            "Got for Session ID=" + userVO.getSessionInfoVO().getSessionID() + "userVO ID=" + userVO.getUserID() + " Login ID =" + userVO.getLoginID() + " userList size=" + userList
                                .size());
                    }
                    typeSessionVO.setList(userList);
                }
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug(
                "checkMaxLocationTypeUsers",
                "Exiting for Got for Session ID=" + userVO.getSessionInfoVO().getSessionID() + "userVO ID=" + userVO.getUserID() + " Login ID =" + userVO.getLoginID() + " locationSessionVO.getCounter()" + locationSessionVO
                    .getCounter() + "   typeSessionVO.getCounter()=" + typeSessionVO.getCounter());
        }
    }

    /**
     * removeUserFromCounters
     * 
     * @param sessionID
     */
    public void removeUserFromCounters(String sessionID) {
        final String METHOD_NAME = "removeUserFromCounters";
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("removeUserFromCounters", "Entered ::" + sessionID);
            }
            SessionVO locationSessionVO = null;
            SessionVO typeSessionVO = null;
            ArrayList userList = null;
            final Enumeration enumUser = userCounters.keys();
            String key = null;
            UserVO userVO = null;
            LoginLoggerVO loginLoggerVO = null;
            Enumeration enumTypes = null;
            String keyType = null;
            Hashtable userTypeHash = null;
            while (enumUser.hasMoreElements()) {
                key = (String) enumUser.nextElement();
                // locationSessionVO=(SessionVO)userCounters.get(key);
                final Object objectType = (Object) userCounters.get(key);
                if (objectType instanceof SessionVO) {
                    locationSessionVO = (SessionVO) objectType;
                    if (locationSessionVO != null) {
                        userTypeHash = (Hashtable) locationSessionVO.getList();
                        if (userTypeHash != null) {
                            enumTypes = userTypeHash.keys();
                            keyType = null;
                            while (enumTypes.hasMoreElements()) {
                                keyType = (String) enumTypes.nextElement();
                                typeSessionVO = (SessionVO) userTypeHash.get(keyType);
                                userList = (ArrayList) typeSessionVO.getList();
                                for (int j = 0; j < userList.size(); j++) {
                                    userVO = (UserVO) userList.get(j);
                                    if (userVO == null || userVO.getSessionInfoVO() == null) {
                                        if (_log.isDebugEnabled()) {
                                            _log.debug("removeUserFromCounters", "UserVO IS NULL*******");
                                        }
                                        continue;
                                    }
                                    if (userVO.getSessionInfoVO().getSessionID().equals(sessionID)) {
                                        if (_log.isDebugEnabled()) {
                                            _log.debug(
                                                "removeUserFromCounters",
                                                "removing userList.size()=" + userList.size() + "	Login ID=" + userVO.getLoginID() + " User ID =" + userVO.getUserID() + " user :Session ID=" + sessionID + " userVO.getLoggerMessage()=" + userVO
                                                    .getLoggerMessage());
                                        }
                                        locationSessionVO.decrementCounter();
                                        typeSessionVO.decrementCounter();
                                        userList.remove(userVO);

                                        loginLoggerVO = new LoginLoggerVO();
                                        // set the logout details for logging
                                        loginLoggerVO.setLoginID(userVO.getLoginID());
                                        loginLoggerVO.setUserID(userVO.getUserID());
                                        loginLoggerVO.setNetworkID(userVO.getNetworkID());
                                        loginLoggerVO.setNetworkName(userVO.getNetworkName());
                                        loginLoggerVO.setUserName(userVO.getUserName());
                                        loginLoggerVO.setUserType(userVO.getUserType());
                                        loginLoggerVO.setDomainID(userVO.getDomainID());
                                        loginLoggerVO.setCategoryCode(userVO.getCategoryCode());
                                        loginLoggerVO.setLoginTime(userVO.getLoginTime());
                                        loginLoggerVO.setLogoutTime(new Date());
                                        loginLoggerVO.setIpAddress(userVO.getRemoteAddress());
                                        loginLoggerVO.setBrowser(userVO.getBrowserType());
                                        if (BTSLUtil.isNullString(userVO.getLoggerMessage())) {
                                            loginLoggerVO.setLogType(TypesI.LOG_TYPE_EXPIRED);
                                            loginLoggerVO.setOtherInformation(Constants.getProperty("SESSION_EXPIRED") + " 	" + sessionID);
                                        } else {
                                            loginLoggerVO.setLogType(TypesI.LOG_TYPE_LOGOUT);
                                            loginLoggerVO.setOtherInformation(userVO.getLoggerMessage() + "		" + sessionID);
                                        }
                                        LoginLogger.log(loginLoggerVO);

                                        break;
                                    }
                                }
                                /*
                                 * Commented as we dont want the key to be
                                 * removed from hashtable
                                 * if(userList.isEmpty())
                                 * userTypeHash.remove(keyType);
                                 */
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (_log.isDebugEnabled()) {
                _log.debug("removeUserFromCounters", "Exception e:" + e);
            }
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    /**
     * invalidateUserFromCounters
     * 
     * @param p_userVO
     */
    public static void invalidateUserFromCounters(UserVO p_userVO) {
        final String METHOD_NAME = "invalidateUserFromCounters";
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("invalidateUserFromCounters", "Entered ::" + p_userVO.getCreatedBy());
            }
            SessionVO locationSessionVO = null;
            SessionVO typeSessionVO = null;
            ArrayList userList = null;
            final Enumeration enumUser = userCounters.keys();
            String key = null;
            UserVO userVO = null;
            final LoginLoggerVO loginLoggerVO = null;
            Enumeration enumTypes = null;
            String keyType = null;
            Hashtable userTypeHash = null;
            while (enumUser.hasMoreElements()) {
                key = (String) enumUser.nextElement();
                // locationSessionVO=(SessionVO)userCounters.get(key);
                final Object objectType = (Object) userCounters.get(key);
                if (objectType instanceof SessionVO) {
                    locationSessionVO = (SessionVO) objectType;
                    if (locationSessionVO != null) {
                        userTypeHash = (Hashtable) locationSessionVO.getList();
                        if (userTypeHash != null) {
                            enumTypes = userTypeHash.keys();
                            keyType = null;
                            while (enumTypes.hasMoreElements()) {
                                keyType = (String) enumTypes.nextElement();
                                typeSessionVO = (SessionVO) userTypeHash.get(keyType);
                                userList = (ArrayList) typeSessionVO.getList();
                                // if(_log.isDebugEnabled())_log.debug("invalidateUserFromCounters","userList size="+userList.size());
                                for (int j = 0; j < userList.size(); j++) {
                                    userVO = (UserVO) userList.get(j);
                                    if (userVO == null) {
                                        continue;
                                    }
                                    // if(_log.isDebugEnabled())_log.debug("invalidateUserFromCounters","comparing "+userVO.getCreatedBy()+"   "+p_userVO.getCreatedBy());
                                    if (userVO.getCreatedBy().equals(p_userVO.getCreatedBy())) {
                                        if (_log.isDebugEnabled()) {
                                            _log.debug("invalidateUserFromCounters", "removing user ::" + userVO.getCreatedBy() + "     sessionID=" + userVO
                                                .getSessionInfoVO().getSessionID());
                                        }
                                        locationSessionVO.decrementCounter();
                                        typeSessionVO.decrementCounter();
                                        userList.remove(userVO);
                                        // invalidating session
                                        try {
                                            final HttpSession usersession = (HttpSession) hashTable.get(userVO.getSessionInfoVO().getSessionID());
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
            }
        } catch (Exception e) {
            _log.error("invalidateUserFromCounters", "Exception e:" + e);
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    public static void incrementCounters(HttpServletRequest p_request) {
        final String METHOD_NAME = "incrementCounters";
        try {
            final String sessionID = p_request.getSession().getId();
            if (_log.isDebugEnabled()) {
                _log.debug("incrementCounters", " Entered ::" + sessionID);
            }
            SessionVO locationSessionVO = null;
            SessionVO typeSessionVO = null;
            ArrayList userList = null;
            final Enumeration enumUser = userCounters.keys();
            String key = null;
            SessionInfoVO sessionInfoVO = null;
            Hashtable userTypeHash = null;
            Enumeration enumTypes = null;
            String keyType = null;
            UserVO userVO = null;
            String roleCode = null;
            RoleHitTimeVO uroleHitTimeVO = null;
            RoleHitTimeVO sroleHitTimeVO = null;
            while (enumUser.hasMoreElements()) {
                key = (String) enumUser.nextElement();
                // locationSessionVO=(SessionVO)userCounters.get(key);
                final Object objectType = (Object) userCounters.get(key);
                if (objectType instanceof SessionVO) {
                    locationSessionVO = (SessionVO) objectType;
                    sessionInfoVO = null;
                    if (locationSessionVO != null) {
                        userTypeHash = (Hashtable) locationSessionVO.getList();
                        if (userTypeHash != null) {
                            enumTypes = userTypeHash.keys();
                            keyType = null;
                            while (enumTypes.hasMoreElements()) {
                                keyType = (String) enumTypes.nextElement();
                                typeSessionVO = (SessionVO) userTypeHash.get(keyType);
                                userList = (ArrayList) typeSessionVO.getList();
                                for (int j = 0; j < userList.size(); j++) {
                                    userVO = (UserVO) userList.get(j);
                                    if (userVO == null) {
                                        continue;
                                    }
                                    sessionInfoVO = userVO.getSessionInfoVO();
                                    if (sessionInfoVO != null && sessionInfoVO.getSessionID().equals(sessionID) && !sessionInfoVO.isUnderProcess()) {
                                        synchronized (SessionCounter.class) {
                                            roleCode = getRoleCode(p_request);
                                            sessionInfoVO.setCurrentRoleCode(roleCode);
                                            if (_log.isDebugEnabled()) {
                                                _log.debug("incrementCounters", "roleCode::" + roleCode + " sessionInfoVO: " + sessionInfoVO);
                                            }
                                            sessionInfoVO.setUnderProcess(true);
                                            // locationSessionVO.decrementCounter();
                                            // typeSessionVO.decrementCounter();
                                            sessionInfoVO.setTotalHit(sessionInfoVO.getTotalHit() + 1);
                                            currentTotalUnderProcess++;
                                            if (maxTotalUnderProcess < currentTotalUnderProcess) {
                                                maxTotalUnderProcess = currentTotalUnderProcess;
                                            }
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
                                            // increase overall under process
                                            // hit---
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
            }
        } catch (Exception e) {
            _log.error("incrementCounters", " Exception e:" + e);
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    public static void decrementCounters(HttpServletRequest p_request) {
        final String METHOD_NAME = "decrementCounters";
        try {
            final String sessionID = p_request.getSession().getId();

            if (_log.isDebugEnabled()) {
                _log.debug("decrementCounters", "Entered ::" + sessionID);
            }
            SessionVO locationSessionVO = null;
            SessionVO typeSessionVO = null;
            ArrayList userList = null;
            final Enumeration enumUser = userCounters.keys();
            String key = null;
            SessionInfoVO sessionInfoVO = null;
            Hashtable userTypeHash = null;
            Enumeration enumTypes = null;
            String keyType = null;
            UserVO userVO = null;
            String roleCode = null;
            RoleHitTimeVO uroleHitTimeVO = null;
            RoleHitTimeVO sroleHitTimeVO = null;
            while (enumUser.hasMoreElements()) {
                key = (String) enumUser.nextElement();
                // locationSessionVO=(SessionVO)userCounters.get(key);
                final Object objectType = (Object) userCounters.get(key);
                if (objectType instanceof SessionVO) {
                    locationSessionVO = (SessionVO) objectType;
                    sessionInfoVO = null;
                    if (locationSessionVO != null) {
                        userTypeHash = (Hashtable) locationSessionVO.getList();
                        if (userTypeHash != null) {
                            enumTypes = userTypeHash.keys();
                            keyType = null;
                            while (enumTypes.hasMoreElements()) {
                                keyType = (String) enumTypes.nextElement();
                                typeSessionVO = (SessionVO) userTypeHash.get(keyType);
                                userList = (ArrayList) typeSessionVO.getList();
                                for (int j = 0; j < userList.size(); j++) {
                                    userVO = (UserVO) userList.get(j);
                                    if (userVO == null) {
                                        continue;
                                    }
                                    sessionInfoVO = userVO.getSessionInfoVO();
                                    if (sessionInfoVO != null && sessionInfoVO.getSessionID().equals(sessionID) && sessionInfoVO.isUnderProcess()) {
                                        synchronized (SessionCounter.class) {
                                            roleCode = getRoleCode(p_request);
                                            sessionInfoVO.setCurrentRoleCode(roleCode);
                                            if (_log.isDebugEnabled()) {
                                                _log.debug("decrementCounters", "roleCode::" + roleCode + " sessionInfoVO: " + sessionInfoVO);
                                            }
                                            sessionInfoVO.setUnderProcess(false);
                                            currentTotalUnderProcess--;
                                            // locationSessionVO.decrementCounter();
                                            // typeSessionVO.decrementCounter();
                                            uroleHitTimeVO = (RoleHitTimeVO) (sessionInfoVO.getRoleHitTimeMap().get(sessionInfoVO.getCurrentRoleCode()));
                                            if (uroleHitTimeVO != null) {
                                                uroleHitTimeVO.setTotalUnderProcess(uroleHitTimeVO.getTotalUnderProcess() - 1);
                                                uroleHitTimeVO.setTotalTime(uroleHitTimeVO.getTotalTime() + System.currentTimeMillis() - uroleHitTimeVO.getLastAccessTime());
                                            }
                                            // decrease overall under process
                                            // hit---
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
            }
        } catch (Exception e) {
            _log.debug("decrementCounters", "Exception e:" + e);
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    private static String getRoleCode(HttpServletRequest p_request) {
        final String roleCode = (String) p_request.getAttribute("roleCode");
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
            } else {
                newUrlCode = "1";
            }
            if (newUrlCode == null && newModuleCode != null) {
                newUrlCode = "1";
            }
            if (newUrlCode == null) {
                newUrlCode = "ALL";
            }
            if (newModuleCode == null) {
                newModuleCode = "ALL";
            }
            return (newModuleCode + newUrlCode);
        }
    }

    public static void checkMaxLocationTypeMonitorUsers(MonitorUserVO p_monitoruservo) throws BTSLBaseException {
        final String METHOD_NAME = "checkMaxLocationTypeMonitorUsers";
        if (_log.isDebugEnabled()) {
            _log.debug("checkMaxLocationTypeMonitorUsers", "Entered userVO:" + p_monitoruservo);
        }
        MonitorUserVO typeMonitorUserVO = null;
        ArrayList userList = null;
        boolean isCheckRequired = false;

           	synchronized (SessionCounter.class) {
                typeMonitorUserVO = (MonitorUserVO) userCounters.get(p_monitoruservo.getCategoryCode());
                if (_log.isDebugEnabled()) {
                    _log.debug("checkMaxLocationTypeMonitorUsers",
                        "Attempt II Got for Session ID=" + p_monitoruservo.getSessionID() + " typeMonitorUserVO=" + typeMonitorUserVO);
                }

                if (typeMonitorUserVO == null) {
                    typeMonitorUserVO = new MonitorUserVO();
                    userList = new ArrayList();
                    userList.add(p_monitoruservo);
                    typeMonitorUserVO.incrementCounter();
                    typeMonitorUserVO.setList(userList);
                    userCounters.put(p_monitoruservo.getCategoryCode(), typeMonitorUserVO);
                    if (_log.isDebugEnabled()) {
                        _log.debug("checkMaxLocationTypeMonitorUsers",
                            "monitoruserVO ID=" + p_monitoruservo.getUserID() + " Login ID =" + p_monitoruservo.getLoginID() + " userList size=" + userList.size());
                    }
                } else {
                    if (_log.isDebugEnabled()) {
                        _log.debug("checkMaxLocationTypeMonitorUsers",
                            "Attempt II Got for Session ID=" + p_monitoruservo.getSessionID() + " typeMonitorUserVO=" + typeMonitorUserVO);
                    }
                    isCheckRequired = true;
                }
            }
        
        if (isCheckRequired) {
            userList = (ArrayList) typeMonitorUserVO.getList();
            long maxTypeCount = 0;
            try {
                maxTypeCount = p_monitoruservo.getMaxLoginCount();
                if (maxTypeCount == 0) {
                    throw new BTSLBaseException("SessionCounter", "checkMaxLocationTypeMonitorUsers");
                }
            } catch (BTSLBaseException btex) {
                _log.errorTrace(METHOD_NAME, btex);
                maxTypeCount = 10;
            }

            if (_log.isDebugEnabled()) {
                _log.debug(
                    "checkMaxLocationTypeMonitorUsers",
                    "Got for Session ID=" + p_monitoruservo.getSessionID() + "monitoruserVO ID=" + p_monitoruservo.getUserID() + " Login ID =" + p_monitoruservo.getLoginID() + "     maxTypeCount=" + maxTypeCount + "     typeSessionVO.getCounter()=" + typeMonitorUserVO
                        .getCounter());
            }
            if (typeMonitorUserVO.getCounter() >= maxTypeCount) {
                throw new BTSLBaseException("SessionCounter", "checkMaxLocationTypeMonitorUsers", "user.sessioncounter.mesage.errormaxtypecountreached", "welcomeHome");
            }
            typeMonitorUserVO.incrementCounter();
            userList.add(p_monitoruservo);
            if (_log.isDebugEnabled()) {
                _log.debug(
                    "checkMaxLocationTypeMonitorUsers",
                    "Got for Session ID=" + p_monitoruservo.getSessionID() + "userVO ID=" + p_monitoruservo.getUserID() + " Login ID =" + p_monitoruservo.getLoginID() + " userList size=" + userList
                        .size());
            }
            typeMonitorUserVO.setList(userList);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("checkMaxLocationTypeMonitorUsers", "Exiting for Got for Session ID=" + p_monitoruservo.getSessionID() + "monitoruserVO ID=" + p_monitoruservo
                .getUserID() + " Login ID =" + p_monitoruservo.getLoginID() + " typeMonitorUserVO.getCounter()" + typeMonitorUserVO.getCounter());
        }
    }

    public void removeMonitorUserFromCounters(String sessionID) {
        final String METHOD_NAME = "removeMonitorUserFromCounters";
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("removeMonitorUserFromCounters", "Entered ::" + sessionID);
            }
            MonitorUserVO typeMonitorUserVO = null;
            ArrayList userList = null;
            final Enumeration enumUser = userCounters.keys();
            String key = null;
            MonitorUserVO monitoruservo = null;
            LoginLoggerVO loginLoggerVO = null;
            while (enumUser.hasMoreElements()) {
                key = (String) enumUser.nextElement();
                // typeMonitorUserVO = (MonitorUserVO)userCounters.get(key);
                final Object objectType = (Object) userCounters.get(key);
                if (objectType instanceof MonitorUserVO) {
                    typeMonitorUserVO = (MonitorUserVO) objectType;
                    if (typeMonitorUserVO != null) {
                        userList = (ArrayList) typeMonitorUserVO.getList();
                        for (int j = 0; j < userList.size(); j++) {
                            monitoruservo = (MonitorUserVO) userList.get(j);
                            if (monitoruservo == null) {
                                if (_log.isDebugEnabled()) {
                                    _log.debug("removeMonitorUserFromCounters", "MonitorUserVO IS NULL*******");
                                }
                                continue;
                            }
                            if (monitoruservo.getSessionID().equals(sessionID)) {
                                if (_log.isDebugEnabled()) {
                                    _log.debug("removeMonitorUserFromCounters", "Session ID as argument =" + sessionID + " Session ID of monitorUserVO :: " + monitoruservo
                                        .getSessionID());
                                }
                                typeMonitorUserVO.decrementCounter();
                                userList.remove(monitoruservo);
                                loginLoggerVO = new LoginLoggerVO();
                                // set the logout details for logging
                                loginLoggerVO.setLoginID(monitoruservo.getLoginID());
                                loginLoggerVO.setUserID(monitoruservo.getUserID());
                                loginLoggerVO.setNetworkID(monitoruservo.getNetworkID());
                                loginLoggerVO.setUserName(monitoruservo.getUserName());
                                loginLoggerVO.setUserType(monitoruservo.getUserType());
                                loginLoggerVO.setCategoryCode(monitoruservo.getCategoryCode());
                                loginLoggerVO.setLogoutTime(new Date());
                                loginLoggerVO.setIpAddress(monitoruservo.getRemoteAddr());
                                loginLoggerVO.setLogType(TypesI.LOG_TYPE_EXPIRED);
                                // loginLoggerVO.setOtherInformation(Constants.getProperty("SESSION_EXPIRED")+" 	"+sessionID);
                                LoginLogger.log(loginLoggerVO);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            if (_log.isDebugEnabled()) {
                _log.debug("removeMonitorUserFromCounters", "Exception e:" + e);
            }
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("removeMonitorUserFromCounters", " Exiting ");
            }
        }
    }
}