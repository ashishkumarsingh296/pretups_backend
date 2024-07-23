package com.selftopup.common;

/**
 * IDGenerator.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 02/03/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

import java.sql.Connection;
import java.util.Date;

import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.OracleUtil;

/**
 * 
 * @author abhijit.chauhan
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class IDGenerator {
    private static Log _log = LogFactory.getFactory().getInstance(IDGenerator.class.getName());
    private static IDGeneratorDAO _idGeneratorDAO = new IDGeneratorDAO();

    public IDGenerator() {
        super();
    }

    public static long getNextID(String p_idType, String p_year) throws BTSLBaseException {
        return getNextID(p_idType, p_year, TypesI.ALL, null);
    }

    public static long getNextID(String p_idType, String p_year, String p_networkID) throws BTSLBaseException {
        return getNextID(p_idType, p_year, p_networkID, null);
    }

    public static long getNextID(Connection p_con, String p_idType, String p_year) throws BTSLBaseException {
        return getNextID(p_con, p_idType, p_year, TypesI.ALL, null);
    }

    public static long getNextID(String p_idType, String p_year, String p_networkID, Date p_currentDate) throws BTSLBaseException {
        Connection con = null;
        try {
            con = OracleUtil.getConnection();
            long id = _idGeneratorDAO.getNextID(con, p_idType, p_year, p_networkID, p_currentDate);
            return id;
        } finally {
            if (con != null) {
                try {
                    con.commit();
                } catch (Exception e) {
                }
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Populates the specified no of ids from the database
     * 
     * @param p_idType
     * @param p_year
     * @param p_networkID
     * @param p_currentDate
     * @param p_noOfIds
     * @throws BTSLBaseException
     */
    public static long getNextID(String p_idType, String p_year, String p_networkID, Date p_currentDate, int p_noOfIds) throws BTSLBaseException {
        _log.debug("getNextID", " Entered p_idType:" + p_idType + " p_year:" + p_year + " p_networkID:" + p_networkID + " p_currentDate:" + p_currentDate + " p_noOfIds:" + p_noOfIds);
        Connection con = null;
        long id = 0;
        try {
            con = OracleUtil.getConnection();
            id = _idGeneratorDAO.getNextID(con, p_idType, p_year, p_networkID, p_currentDate, p_noOfIds);
            return id;
        } catch (BTSLBaseException be) {
            _log.error("getNextID", "BTSLBaseException:" + be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGenerator[getNextID]", "", "", "", "BTSL BaseException:" + be.getMessage());
            throw be;
        } finally {
            if (con != null) {
                try {
                    con.commit();
                } catch (Exception e) {
                }
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
            _log.debug("getNextID", " Exiting with id:" + id);
        }
    }

    public static long getNextID(Connection p_con, String p_idType, String p_year, String p_networkID, Date p_currentDate) throws BTSLBaseException {
        try { // To handle commit problem in between transaction.
              // long
              // id=_idGeneratorDAO.getNextID(p_con,p_idType,p_year,p_networkID,p_currentDate);
            long id = getNextID(p_idType, p_year, p_networkID, p_currentDate);
            return id;
        } finally { // To handle commit problem in between transaction.
            /*
             * if(p_con!=null)
             * {
             * try{p_con.commit();}catch(Exception e){}
             * }
             */
        }
    }

}// end of the class IDGenerator