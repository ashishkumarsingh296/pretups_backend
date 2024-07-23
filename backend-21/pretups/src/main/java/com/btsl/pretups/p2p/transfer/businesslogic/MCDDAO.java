package com.btsl.pretups.p2p.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;

public class MCDDAO {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    public boolean isListPresentInDatabase(Connection p_con, String p_listName, String p_userID) throws BTSLBaseException {

        final String methodName = "isListPresentInDatabase";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_listName ");
        	loggerValue.append(p_listName);
        	loggerValue.append("p_userID : ");
        	loggerValue.append(p_userID);
            _log.debug(methodName,loggerValue );
        }
        boolean isExist = false;

        PreparedStatement pstm = null;
        ResultSet rst = null;
        try {
            final String query = " SELECT 1 count FROM p2p_buddies WHERE  parent_id=?  and  List_name =?  ";

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query: " + query);
            }

            pstm = p_con.prepareStatement(query);
            pstm.setString(1, p_userID);
            pstm.setString(2, p_listName);
            rst = pstm.executeQuery();

            if (rst.next()) {
                isExist = true;
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MCDDAO[isListPresentInDatabase]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error("isListAlreadyRegistered", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MCDDAO[isListPresentInDatabase]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExist " + isExist);
            }
        }
        return isExist;

    }

    public ArrayList loadBuddyListDetails(Connection p_con, String p_userID, String p_listName, String p_selector) throws BTSLBaseException {
        final String methodName = "loadBuddyListDetails";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered   p_userID ");
        	loggerValue.append(p_userID);
        	loggerValue.append(" p_listName ");
        	loggerValue.append(p_listName);
        	loggerValue.append("p_selector");
        	loggerValue.append(p_selector);
            _log.debug(methodName,loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer("SELECT BUDDY_MSISDN,  SELECTOR_CODE,PREFERRED_AMOUNT ");
        strBuff.append("FROM P2P_BUDDIES WHERE PARENT_ID= ? AND STATUS =? AND SELECTOR_CODE =? AND PREFERRED_AMOUNT >0");
        if (!BTSLUtil.isNullString(p_listName)) {
            strBuff.append("  AND LIST_NAME =?");
        }
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList arrayList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_userID);
            pstmt.setString(2, PretupsI.YES);
            pstmt.setString(3, p_selector);
            if (!BTSLUtil.isNullString(p_listName)) {
                pstmt.setString(4, p_listName);
            }
            rs = pstmt.executeQuery();
            MCDListVO mcdListVO = null;
            while (rs.next()) {
                mcdListVO = new MCDListVO();
                mcdListVO.setMsisdn(rs.getString("BUDDY_MSISDN"));
                mcdListVO.setSelector1(rs.getString("SELECTOR_CODE"));
                mcdListVO.setAmount1(rs.getLong("PREFERRED_AMOUNT"));
                arrayList.add(mcdListVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MCDDAO[loadBuddyListDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MCDDAO[loadBuddyListDetails]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:  arrayList Size =" + arrayList.size());
            }
        }
        return arrayList;

    }

    public ArrayList countDays(Connection p_con, int p_days) throws BTSLBaseException {
        final String methodName = "countDays";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  ");
        	loggerValue.append(" p_days ");
        	loggerValue.append(p_days);
            _log.debug(methodName,loggerValue );
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final ArrayList list = new ArrayList();
        MCDQry mcdQry = (MCDQry)ObjectProducer.getObject(QueryConstants.MCD_QRY, QueryConstants.QUERY_PRODUCER);
        String queryBuff = mcdQry.countDaysQry();
        
        final String query = queryBuff;
        if (_log.isDebugEnabled()) {
            _log.debug("query", query);
        }
        try {
            MCDListVO mcdListVO = null;

            pstmt = p_con.prepareStatement(query);
            pstmt.setInt(1, p_days);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                mcdListVO = new MCDListVO();
                mcdListVO.setParentID(rs.getString("PARENT_ID"));
                mcdListVO.setListName(rs.getString("LIST_NAME"));
                mcdListVO.setLastTransfer(rs.getDate("LAST_TRANSFER"));
                mcdListVO.setDays(rs.getLong("DAYS"));
                list.add(mcdListVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MCDDAO[loadBuddyListDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MCDDAO[loadBuddyListDetails]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:  arrayList Size =" + list.size());
            }
        }
        return list;
    }

}
