package com.inter.ferma6;

/**
 * @(#)FermaConnectionServlet.java
 *                                 Copyright(c) 2005, Bharti Telesoft Int.
 *                                 Public Ltd.
 *                                 All Rights Reserved
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Abhijit Chauhan June 22,2005 Initial Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 ------
 */
import java.io.IOException;
import java.util.HashMap;

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
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.util.Constants;

public class Ferma6ConnectionServlet extends HttpServlet {
    private static Log _log = LogFactory.getLog(Ferma6ConnectionServlet.class.getName());
    public static HashMap _schedulerMap = null;
    public static HashMap _managerMap = null;

    public void init(ServletConfig arg0) throws ServletException {
        // TODO Auto-generated method stub
        super.init(arg0);
        if (_log.isDebugEnabled())
            _log.debug("init", "Entered");
        initialize();
        if (_log.isDebugEnabled())
            _log.debug("init", "Exiting");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (_log.isDebugEnabled())
            _log.debug("doGet", "Entered action: " + action);
        if (action == null)
            return;
        if (action.equals("RELOAD")) {
            initialize();
        }
        if (action.equals("REFRESH")) {
            refresh();
        }
        if (_log.isDebugEnabled())
            _log.debug("doGet", "Exiting action: " + action);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (_log.isDebugEnabled())
            _log.debug("doPost", "Entered");
        doGet(request, response);
        if (_log.isDebugEnabled())
            _log.debug("doPost", "Exiting");
    }

    /**
     * Initialize Ferma Scheduler and Ferma Connection manager(relogin)
     * 
     */
    public static void initialize() {
        String ids = Constants.getProperty("FERMA6_CONN_POOL_IDS");
        if (_log.isDebugEnabled())
            _log.debug("initialize", "Entered FERMA6_CONN_POOL_IDS ids : " + ids);
        _schedulerMap = new HashMap();
        _managerMap = new HashMap();
        String[] _ids = ids.split(",");
        String id;
        String urlStr = null;
        long minHoldDuration = 0;
        long maxHoldDuration = 0;
        for (int i = 0; i < _ids.length; i++) {
            id = _ids[i];
            try {
                if (_log.isDebugEnabled())
                    _log.debug("initialize", "Entered id : " + id);
                Ferma6INScheduler fermaINScheduler = new Ferma6INScheduler();
                minHoldDuration = Long.parseLong(FileCache.getValue(id, "MIN_HOLD_DURATION"));
                maxHoldDuration = Long.parseLong(FileCache.getValue(id, "MAX_HOLD_DURATION"));
                urlStr = FileCache.getValue(id, "URL1");
                fermaINScheduler.setInterfaceID(id.trim());
                fermaINScheduler.setUrlStr(urlStr);
                fermaINScheduler.setUrlID(1);
                fermaINScheduler.setResetTime(System.currentTimeMillis());
                fermaINScheduler.setMinHoldDuration(minHoldDuration);
                fermaINScheduler.setMaxHoldDuration(maxHoldDuration);
                _schedulerMap.put(id, fermaINScheduler);

                Ferma6ConnectionManager fermaConnectionManager = new Ferma6ConnectionManager(id);
                fermaConnectionManager.login(urlStr);
                _managerMap.put(id, fermaConnectionManager);

            } catch (Exception e) {
                _log.error("initialize", "Exception e:" + e.getMessage() + " id=" + id);
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, id, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Ferma6ConnectionServlet[initialize]", "", "", "", "Exception while initialize FERMA Scheduler Parameters Exception:" + e.getMessage());
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("initialize", "Exiting");
    }

    /**
     * Referesh/Update Ferma Scheduler Paramaters
     * 
     */
    public static void refresh() {
        String ids = Constants.getProperty("FERMA6_CONN_POOL_IDS");
        if (_log.isDebugEnabled())
            _log.debug("refresh", "Entered FERMA6_CONN_POOL_IDS : " + ids);
        String[] _ids = ids.split(",");
        String id;
        String urlStr = null;
        long minHoldDuration = 0;
        long maxHoldDuration = 0;
        for (int i = 0; i < _ids.length; i++) {
            id = _ids[i];
            try {
                Ferma6INScheduler fermaINScheduler = (Ferma6INScheduler) _schedulerMap.get(id);
                minHoldDuration = Long.parseLong(FileCache.getValue(id, "MIN_HOLD_DURATION"));
                maxHoldDuration = Long.parseLong(FileCache.getValue(id, "MAX_HOLD_DURATION"));
                if (_log.isDebugEnabled())
                    _log.debug("refresh", "minHoldDuration : " + minHoldDuration + " maxHoldDuration : " + maxHoldDuration);
                fermaINScheduler.setMinHoldDuration(minHoldDuration);
                fermaINScheduler.setMaxHoldDuration(maxHoldDuration);
            } catch (Exception e) {
                _log.error("refresh", "Exception e:" + e.getMessage() + " id=" + id);
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, id, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "Ferma6ConnectionServlet[refresh]", "", "", "", "Exception while referesh FERMA Scheduler Parameters Exception:" + e.getMessage());
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("refresh", "Exiting");
    }

    public void destroy() {
        String ids = Constants.getProperty("FERMA6_CONN_POOL_IDS");
        if (_log.isDebugEnabled())
            _log.debug("destroy", "Entered FERMA6_CONN_POOL_IDS : " + ids);
        String[] _ids = ids.split(",");
        String id;
        String urlStr = null;
        for (int i = 0; i < _ids.length; i++) {
            id = _ids[i];
            try {
                Ferma6INScheduler fermaINScheduler = (Ferma6INScheduler) _schedulerMap.get(id);
                urlStr = fermaINScheduler.getUrlStr();
                Ferma6ConnectionManager fermaConnectionManager = (Ferma6ConnectionManager) _managerMap.get(id);
                fermaConnectionManager.logout(urlStr);
            } catch (Exception e) {
                _log.error("destroy", "Exception e:" + e.getMessage() + " id=" + id);
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_INFO, id, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "Ferma6ConnectionServlet[destroy]", "", "", "", "Exception while initialize FERMA Scheduler Parameters Exception:" + e.getMessage());
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("destroy", "Exiting");
    }
}
