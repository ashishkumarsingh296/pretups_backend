package com.btsl.common;

/**
 * IDGenerator.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 02/03/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
/**
 * 
 * @author abhijit.chauhan
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class IDGenerator {
    private static Log log = LogFactory.getFactory().getInstance(IDGenerator.class.getName());
    private static IDGeneratorDAO idGeneratorDAO = new IDGeneratorDAO();

    /**
	 * ensures no instantiation
	 */
    private IDGenerator() {
        
    }
    
    /**
     * 
     * @param p_idType
     * @param p_year
     * @return
     * @throws BTSLBaseException
     */
    public static long getNextID(String p_idType, String p_year) throws BTSLBaseException {
        return getNextID(p_idType, p_year, TypesI.ALL, null);
    }

    public static long getNextID(String p_idType, String p_year, Date p_currentDate) throws BTSLBaseException {
        return getNextID(p_idType, p_year, TypesI.ALL, p_currentDate);
    }

    public static long getNextID(String p_idType, String p_year, String p_networkID) throws BTSLBaseException {
        return getNextID(p_idType, p_year, p_networkID, null);
    }

    public static long getNextID(Connection p_con, String p_idType, String p_year) throws BTSLBaseException {
        return getNextID(p_con, p_idType, p_year, TypesI.ALL, null);
    }

    public static long getNextID(String p_idType, String p_year, String p_networkID, Date p_currentDate) throws BTSLBaseException {
        Connection con = null;MComConnectionI mcomCon = null;
        final String METHOD_NAME = "getNextID";
        try {
			mcomCon = new MComConnection();
			try {
				con = mcomCon.getConnection();
			} catch (SQLException sqle) {
				log.errorTrace(METHOD_NAME, sqle);
			}
            long id = idGeneratorDAO.getNextID(con, p_idType, p_year, p_networkID, p_currentDate);
            return id;
        } finally {
            if (con != null) {
                try {
                	mcomCon.finalCommit();
                } catch (Exception e) {
                    log.errorTrace(METHOD_NAME, e);
                }
				if (mcomCon != null) {
					mcomCon.close("IDGenerator#getNextID");
					mcomCon = null;
				}
            }
        }
    }

    public static long getNextID(String p_idType, String p_year, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {
        Connection con = null;MComConnectionI mcomCon = null;
        final String METHOD_NAME = "getNextID";
        try {
        	mcomCon = new MComConnection();try{con=mcomCon.getConnection();}catch (SQLException sqle) { log.errorTrace(METHOD_NAME, sqle);}
            long id = idGeneratorDAO.getNextID(con, p_idType, p_year, p_channelTransferVO);
            return id;
        } finally {
            if (con != null) {
                try {
                	mcomCon.finalCommit();
                } catch (Exception e) {
                	 log.error(METHOD_NAME, "Exception:e=" + e);
        			 log.errorTrace(METHOD_NAME, e);
                
                }
				if (mcomCon != null) {
					mcomCon.close("IDGenerator#getNextID");
					mcomCon = null;
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
        log.debug("getNextID", " Entered p_idType:" + p_idType + " p_year:" + p_year + " p_networkID:" + p_networkID + " p_currentDate:" + p_currentDate + " p_noOfIds:" + p_noOfIds);
        final String METHOD_NAME = "getNextID";
        Connection con = null;MComConnectionI mcomCon = null;
        long id = 0;
        try {
        	mcomCon = new MComConnection();try{con=mcomCon.getConnection();} catch (SQLException sqle) { log.errorTrace(METHOD_NAME, sqle);}
            id = idGeneratorDAO.getNextID(con, p_idType, p_year, p_networkID, p_currentDate, p_noOfIds);
            return id;
        } catch (BTSLBaseException be) {
            log.error("getNextID", "BTSLBaseException:" + be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGenerator[getNextID]", "", "", "", "BTSL BaseException:" + be.getMessage());
            throw be;
        } finally {
            if (con != null) {
                try {
                	mcomCon.finalCommit();
                } catch (Exception e) {
                    log.errorTrace(METHOD_NAME, e);
                }
                if(mcomCon != null){mcomCon.close("IDGenerator#getNextID");mcomCon=null;}
            }
            log.debug("getNextID", " Exiting with id:" + id);
        }
    }

    public static long getNextID(Connection p_con, String p_idType, String p_year, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {
        final String methodName="getNextID";
    	try { 
            long id = getNextID(p_idType, p_year, p_channelTransferVO);
            return id;
        } finally { 
        	 if (log.isDebugEnabled())
 				log.debug(methodName , "Exiting");
  
        }
    }

    public static long getNextID(Connection p_con, String p_idType, String p_year, String p_networkID, Date p_currentDate) throws BTSLBaseException {
    	 final String methodName="getNextID";
    	try { 
            long id = getNextID(p_idType, p_year, p_networkID, p_currentDate);
            return id;
        } finally { 
        	if (log.isDebugEnabled())
 				log.debug(methodName , "Exiting");
        }
    }

    public static long getNextID(String p_idType, String p_year, NetworkStockTxnVO p_networkStockTxnVO) throws BTSLBaseException {
        Connection con = null;MComConnectionI mcomCon = null;
        final String METHOD_NAME = "getNextID";
        try {
        	mcomCon = new MComConnection();try{con=mcomCon.getConnection();} catch (SQLException sqle) { log.errorTrace(METHOD_NAME, sqle);}
            long id = idGeneratorDAO.getNextID(con, p_idType, p_year, p_networkStockTxnVO);
            return id;
        } finally {
            if (con != null) {
                try {
                	mcomCon.finalCommit();
                } catch (Exception e) {
                    log.errorTrace(METHOD_NAME, e);
                }
                if(mcomCon != null){mcomCon.close("IDGenerator#getNextID");mcomCon=null;}
            }
        }
    }
    
    
    
    
    public static long getNextIDByConnection(Connection p_con, String p_idType, String p_year) throws BTSLBaseException {
        return getNextIDByConn(p_con, p_idType, p_year, TypesI.ALL, null);
    }
    
    public static long getNextIDByConn(Connection con,String p_idType, String p_year, String p_networkID, Date p_currentDate) throws BTSLBaseException {
        
        final String METHOD_NAME = "getNextIDByConnection";
        log.info(METHOD_NAME, "getNextIDByConnection getting ID");
        try {
	        long id = idGeneratorDAO.getNextID(con, p_idType, p_year, p_networkID, p_currentDate);
            return id;
        } finally {
        	log.info(METHOD_NAME, "getNextIDByConnection exiting");
        }
    }

    
}// end of the class IDGenerator