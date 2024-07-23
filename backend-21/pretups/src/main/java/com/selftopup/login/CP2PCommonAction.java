package com.selftopup.login;

/*
 * CommonAction.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Shashi Ranjan 26/05/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

import com.selftopup.common.BTSLActionSupport;
import jakarta.servlet.http.HttpSession;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;

public class CP2PCommonAction extends BTSLActionSupport {

    private static final long serialVersionUID = 1L;

    public Log _log = LogFactory.getLog(this.getClass().getName());

    public String crystalReport() {
        if (_log.isDebugEnabled())
            _log.debug("CrystalReport", "Entered");
        String forward = null;
        try {
            HttpSession session = _request.getSession(false);
            String report = (String) session.getAttribute("urlStr");

            session.setAttribute("report", report);
            session.setAttribute("mySession", session.getId());

            if (_log.isDebugEnabled())
                _log.debug("crystalReport", "report ::" + report);
            forward = "crystalReport";

        } catch (Exception e) {
            _log.error("crystalReport", "Exceptin:e=" + e);
            e.printStackTrace();
            return ERROR;
        }
        if (_log.isDebugEnabled())
            _log.debug("crystalReport", "Exiting with forward Path=" + forward);
        return forward;
    }

    public String crystalRpt() {
        if (_log.isDebugEnabled())
            _log.debug("crystalRpt", "Entered");
        try {
            String forward = null;
            HttpSession session = _request.getSession(false);
            String report = (String) session.getAttribute("urlStr");

            session.setAttribute("report", report);
            session.setAttribute("requestSession", session.getId());

            if (_log.isDebugEnabled())
                _log.debug("crystalRpt", "\n requestSession ::" + session.getId());
            if (_log.isDebugEnabled())
                _log.debug("crystalRpt", "\n report ::" + report);
            forward = "crystalRpt";

            if (_log.isDebugEnabled())
                _log.debug("crystalRpt", "Exiting with forward Path=" + forward);
            return forward;
        } catch (Exception e) {
            _log.error("crystalRpt", "Exceptin:e=" + e);
            e.printStackTrace();
            return ERROR;
        }
    }
}