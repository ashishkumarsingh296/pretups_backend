/*
 * #SimBL.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Aug 18, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.ota.services.businesslogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.ibm.icu.util.Calendar;

public class SimBL {
    private static Log _log = LogFactory.getLog(SimBL.class.getName());
    public static final SimDAO simDAO = new SimDAO();

    /**
	 * ensures no instantiation
	 */
    private SimBL(){
    	
    }
    /**
     * Method ischeckLockOperation
     * 
     * @param con
     * @param msisdn
     *            String
     * @return String
     * @throws SQLException
     * @throws Exception
     */

    public static boolean ischeckLockOperation(Connection con, String msisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("ischeckLockOperation ", "Entering MobileNO = " + msisdn);
        }
        final String METHOD_NAME = "ischeckLockOperation";
        boolean isLock = true;// locked

        try {
            ArrayList lockar = simDAO.checkLockOperation(con, msisdn);
            if (lockar.isEmpty() || lockar.isEmpty()) {
                isLock = false;
            } else {
                Date lockDate = BTSLUtil.getUtilDateFromTimestamp((Timestamp) lockar.get(0));
                int lockTime = Integer.parseInt((String) lockar.get(1));
                Calendar createdDate = BTSLDateUtil.getInstance();
                createdDate.setTime(lockDate);
                createdDate.set(Calendar.MINUTE, createdDate.get(Calendar.MINUTE) + lockTime);
                Calendar presentDate = BTSLDateUtil.getInstance();
                if (createdDate.after(presentDate)) {
                    isLock = true;// because created Time + lock > present date
                                  // means lock is acquired
                } else {
                    isLock = false; // because created Time + lock < present
                                    // date means lock is Released
                }
            }
            return isLock;
        }

        catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        }

        catch (SQLException se) {
            _log.errorTrace(METHOD_NAME, se);
            _log.error("ischeckLockOperation", " SQLException::" + se.getMessage());
            isLock = true;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("ischeckLockOperation ", "Exception::" + e.getMessage());
            isLock = true;
        }
        return isLock;
    }// end of ischeckLockOperation
}
