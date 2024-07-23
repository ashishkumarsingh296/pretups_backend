/**
 * @(#)AutoO2CDAO.java
 * 
 * 
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Hitesh Ghanghas 10/04/2013 initial creation
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 */
package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;

public class AutoO2CDAO {
    private Log _log = LogFactory.getLog(AutoO2CDAO.class.getName());

    /*
     * 
     */
    public int initiateAutoO2C(Connection p_con, String User_id) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered User_id:");
        	loggerValue.append(User_id);
            _log.debug("initiateAutoO2C",  loggerValue);
        }
        final String METHOD_NAME = "initiateAutoO2C";
        PreparedStatement pstmtUpdate = null;
        final ResultSet rs = null;
        int update_count = 0;
        try {
            final StringBuffer updateQueryBuff = new StringBuffer("update channel_users set AUTO_O2C_ALLOW=? where user_id=?");
            final String updateQuery = updateQueryBuff.toString();
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("update query:");
            	loggerValue.append(updateQuery);
                _log.debug("initiateAutoO2C",  loggerValue );
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(1, PretupsI.NEW);
            pstmtUpdate.setString(2, User_id);
            update_count = pstmtUpdate.executeUpdate();

        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException " );
        	loggerValue.append(sqle.getMessage());
            _log.error("initiateAutoO2C", loggerValue );
            _log.errorTrace(METHOD_NAME, sqle);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:" );
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoO2CDAO[initiateAutoO2C]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, "initiateAutoO2C", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException " );
        	loggerValue.append(e.getMessage());
            _log.error("initiateAutoO2C",  loggerValue );
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:"  );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoO2CDAO[initiateAutoO2C]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "initiateAutoO2C", "error.general.sql.processing");

        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append( " updateCount:" );
            	loggerValue.append(update_count);
                _log.debug("initiateAutoO2C", loggerValue );
            }
        }// end of finally

        return update_count;
    }

    /**
     * @param p_con
     * @param p_searchParam
     *            it can be userID
     * @param p_approvalLevel
     *            it can be different approval level.On the base of approval
     *            level diffrent approval list will load
     * @param p_networkCode
     * @param p_networkCodeFor
     * @param p_domainCode
     *            Domain code
     * @param p_geoCode
     *            Geo Domain Code (ALL in case of Approve 2 and 3)
     * @param p_loginUserID
     *            User ID of the person who has logged in
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList autoO2CList(Connection p_con, String p_approvalLevel, String p_networkCode, String Category_code) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
    		loggerValue.append("Entered ");
    		loggerValue.append(" p_approvalLevel: ");
    		loggerValue.append(p_approvalLevel);
    		loggerValue.append(" p_networkCode ");
    		loggerValue.append(p_networkCode);
    		loggerValue.append(" p_roamNetworkCode ");
            _log.debug("autoO2CList",  loggerValue);
        }
        final String METHOD_NAME = "autoO2CList";
        // UserVO userVO=new UserVO();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer(" select CU.USER_ID,CU.AUTO_O2C_ALLOW, U.USER_NAME from  USERS U,CHANNEL_USERS CU    ");
        strBuff.append(" where CU.AUTO_O2C_ALLOW=? and U.NETWORK_CODE=? and U.STATUS<> 'N' and U.USER_ID=CU.USER_ID and U.category_code =? ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            _log.debug("autoO2CList", loggerValue );
        }
        final ArrayList arrayList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int m = 0;

            if (PretupsI.AUTO_O2C_ORDER_NEW.equals(p_approvalLevel)) {
                pstmt.setString(++m, PretupsI.AUTO_O2C_ORDER_NEW);

            } else if (PretupsI.AUTO_O2C_ORDER_APPROVE1.equals(p_approvalLevel)) {
                pstmt.setString(++m, PretupsI.AUTO_O2C_ORDER_APPROVE1);

            } else if (PretupsI.AUTO_O2C_ORDER_APPROVE2.equals(p_approvalLevel)) {
                pstmt.setString(++m, PretupsI.AUTO_O2C_ORDER_APPROVE2);
            }
            pstmt.setString(++m, p_networkCode);
            pstmt.setString(++m, Category_code);

            rs = pstmt.executeQuery();

            ChannelTransferVO transferVO = null;
            int i = 0;
            while (rs.next()) {
                transferVO = new ChannelTransferVO();
                transferVO.setActiveUserName(rs.getString("USER_NAME"));
                // transferVO.setTransferInitatedByName();
                transferVO.setToUserID(rs.getString("USER_ID"));
                transferVO.setNetworkCode(p_networkCode);
                transferVO.setStatus(rs.getString("AUTO_O2C_ALLOW"));
                transferVO.setIndex(i);
                ++i;
                arrayList.add(transferVO);
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
        	
            _log.error("autoO2CList",  loggerValue);
            _log.errorTrace(METHOD_NAME, sqe);
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoO2CDAO[AutoO2CList]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            _log.error("autoO2CList",  loggerValue );
            _log.errorTrace(METHOD_NAME, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoO2CDAO[AutoO2CList]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, "loadChannelTransfersList", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  arrayList Size =");
            	loggerValue.append(arrayList.size());
                _log.debug("autoO2CList", loggerValue);
            }
        }
        return arrayList;
    }

    /**
     * @param p_con
     * @param p_searchParam
     *            it can be userID
     * @param p_approvalLevel
     *            it can be different approval level.On the base of approval
     *            level diffrent approval list will load
     * @param p_networkCode
     * @param p_networkCodeFor
     * @param p_domainCode
     *            Domain code
     * @param p_geoCode
     *            Geo Domain Code (ALL in case of Approve 2 and 3)
     * @param p_loginUserID
     *            User ID of the person who has logged in
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList autoO2CListForSingleUser(Connection p_con, String p_approvalLevel, String p_networkCode, String user_id) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
    		loggerValue.append("Entered ");
    		loggerValue.append(" p_approvalLevel: ");
    		loggerValue.append(p_approvalLevel);
    		loggerValue.append(" p_networkCode ");
    		loggerValue.append(p_networkCode);
    		loggerValue.append(" p_roamNetworkCode ");
            _log.debug("autoO2CListForSingleUser", loggerValue);
        }
        final String METHOD_NAME = "autoO2CListForSingleUser";
        // UserVO userVO=new UserVO();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer(" select CU.USER_ID,CU.AUTO_O2C_ALLOW, U.USER_NAME from  USERS U,CHANNEL_USERS CU    ");
        strBuff.append(" where CU.AUTO_O2C_ALLOW=? and U.NETWORK_CODE=? and U.STATUS<> 'N' and CU.USER_ID=? and U.USER_ID=? ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("autoO2CList", "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList arrayList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int m = 0;

            if (PretupsI.AUTO_O2C_ORDER_NEW.equals(p_approvalLevel)) {
                pstmt.setString(++m, PretupsI.AUTO_O2C_ORDER_NEW);

            } else if (PretupsI.AUTO_O2C_ORDER_APPROVE1.equals(p_approvalLevel)) {
                pstmt.setString(++m, PretupsI.AUTO_O2C_ORDER_APPROVE1);

            } else if (PretupsI.AUTO_O2C_ORDER_APPROVE2.equals(p_approvalLevel)) {
                pstmt.setString(++m, PretupsI.AUTO_O2C_ORDER_APPROVE2);
            }
            pstmt.setString(++m, p_networkCode);
            pstmt.setString(++m, user_id);
            pstmt.setString(++m, user_id);
            rs = pstmt.executeQuery();

            ChannelTransferVO transferVO = null;
            int i = 0;
            while (rs.next()) {
                transferVO = new ChannelTransferVO();
                transferVO.setActiveUserName(rs.getString("USER_NAME"));
                // transferVO.setTransferInitatedByName();
                transferVO.setToUserID(rs.getString("USER_ID"));
                transferVO.setNetworkCode(p_networkCode);
                transferVO.setStatus(rs.getString("AUTO_O2C_ALLOW"));
                ++i;
                arrayList.add(transferVO);
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
    		loggerValue.append("SQLException : ");
    		loggerValue.append(sqe);
            _log.error("autoO2CList", loggerValue);
            _log.errorTrace(METHOD_NAME, sqe);
            loggerValue.setLength(0);
    		loggerValue.append("SQL Exception:");
    		loggerValue.append( sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoO2CDAO[AutoO2CList]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
    		loggerValue.append("Exception : ");
    		loggerValue.append(ex);
            _log.error("autoO2CList", loggerValue);
            _log.errorTrace(METHOD_NAME, ex);
            loggerValue.setLength(0);
    		loggerValue.append("Exception:" );
    		loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoO2CDAO[AutoO2CList]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "loadChannelTransfersList", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  arrayList Size =");
            	loggerValue.append(arrayList.size());
                _log.debug("autoO2CList",  loggerValue );
            }
        }
        return arrayList;
    }

    public int approveAutoO2C(Connection p_con, String User_id, String approval_level) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered User_id:");
        	loggerValue.append(User_id);
            _log.debug("approveAutoO2C",  loggerValue );
        }
        final String METHOD_NAME = "approveAutoO2C";
        PreparedStatement pstmtUpdate = null;
        final ResultSet rs = null;
        int update_count = 0;
        try {
            final StringBuffer updateQueryBuff = new StringBuffer("update channel_users set AUTO_O2C_ALLOW=? where user_id=?");
            final String updateQuery = updateQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("initiateAutoO2C", "update query:" + updateQuery);
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            if (approval_level.equals(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1)) {
                if (("1".equals(Integer.toString(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_O2C_MAX_APPROVAL_LEVEL))).intValue())))) {
                    pstmtUpdate.setString(1, PretupsI.YES);
                } else {
                    pstmtUpdate.setString(1, PretupsI.AUTO_O2C_ORDER_APPROVE1);
                }
            } else if (approval_level.equals(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2)) {
                if (("2".equals(Integer.toString(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_O2C_MAX_APPROVAL_LEVEL))).intValue())))) {
                    pstmtUpdate.setString(1, PretupsI.YES);
                } else {
                    pstmtUpdate.setString(1, PretupsI.AUTO_O2C_ORDER_APPROVE2);
                }

            } else {
                if (("3".equals(Integer.toString(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_O2C_MAX_APPROVAL_LEVEL))).intValue())))) {
                    pstmtUpdate.setString(1, PretupsI.YES);
                } else {
                    pstmtUpdate.setString(1, PretupsI.AUTO_O2C_ORDER_APPROVE3);
                }

            }
            pstmtUpdate.setString(2, User_id);
            update_count = pstmtUpdate.executeUpdate();

        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            _log.error("approveAutoO2C",loggerValue );
            _log.errorTrace(METHOD_NAME, sqle);
        	loggerValue.setLength(0);
        	loggerValue.append( "SQL Exception:" );
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoO2CDAO[initiateAutoO2C]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, "approveAutoO2C", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append( "SQLException " );
        	loggerValue.append(e.getMessage());
            _log.error("approveAutoO2C", loggerValue);
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
        	loggerValue.append( "SQL Exception:" );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoO2CDAO[initiateAutoO2C]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, "approveAutoO2C", "error.general.sql.processing");

        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append(" updateCount:");
            	loggerValue.append(update_count);
                _log.debug("approveAutoO2C", loggerValue );
            }
        }// end of finally

        return update_count;
    }

    public int rejectAutoO2C(Connection p_con, String User_id, String approval_level) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered User_id:");
        	loggerValue.append(User_id);
            _log.debug("rejectAutoO2C",  loggerValue );
        }
        final String METHOD_NAME = "rejectAutoO2C";
        PreparedStatement pstmtUpdate = null;
        final ResultSet rs = null;
        int update_count = 0;
        try {
            final StringBuffer updateQueryBuff = new StringBuffer("update channel_users set AUTO_O2C_ALLOW=? where user_id=?");
            final String updateQuery = updateQueryBuff.toString();
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("update query:");
            	loggerValue.append(updateQuery);
                _log.debug("initiateAutoO2C",  loggerValue);
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(1, PretupsI.AUTO_O2C_ORDER_REJECTED);
            pstmtUpdate.setString(2, User_id);
            update_count = pstmtUpdate.executeUpdate();

        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            _log.error("rejectAutoO2C",  loggerValue );
            _log.errorTrace(METHOD_NAME, sqle);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoO2CDAO[initiateAutoO2C]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "rejectAutoO2C", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(e.getMessage());
            _log.error("rejectAutoO2C", loggerValue );
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoO2CDAO[initiateAutoO2C]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, "rejectAutoO2C", "error.general.sql.processing");

        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append(" updateCount:");
            	loggerValue.append(update_count);
                _log.debug("approveAutoO2C", loggerValue );
            }
        }// end of finally

        return update_count;

    }

    /**
     * Method loadCategoryUsersWithinGeoDomainHirearchy.
     * This method the loads the user list with userID and UserName, for the
     * search screen .
     * 
     * @param p_con
     *            Connection
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_userName
     *            String
     * @param p_ownerUserID
     *            String
     * @param p_geographicalDomainCode
     *            String
     * @param p_loginUserID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCategoryUsersWithinGeoDomainHirearchy(Connection p_con, String p_categoryCode, String p_networkCode, String p_userName, String p_ownerUserID, String p_geographicalDomainCode, String p_loginUserID, String user_type) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  Category Code ");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(" Network Code ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" User Name ");
        	loggerValue.append(p_userName);
        	loggerValue.append("  ownerUserID ");
        	loggerValue.append(p_ownerUserID);
        	loggerValue.append(" p_geographicalDomainCode: ");
        	loggerValue.append(p_geographicalDomainCode);
        	loggerValue.append(" loginUserID: ");
        	loggerValue.append(p_loginUserID);
            _log.debug(
                "loadCategoryUsersWithinGeoDomainHirearchy",loggerValue );
        }
        final String METHOD_NAME = "loadCategoryUsersWithinGeoDomainHirearchy";
        // commented for DB2 OraclePreparedStatement pstmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;


        final ArrayList arrayList = new ArrayList();
        try {
            // commented for DB2
            // pstmt = (OraclePreparedStatement)
            // p_con.prepareStatement(sqlSelect);
        	
        	AutoO2CQry autoO2CQry=(AutoO2CQry)ObjectProducer.getObject(QueryConstants.AUTO_O2C_QRY, QueryConstants.QUERY_PRODUCER);
            
        	pstmt = autoO2CQry.loadCategoryUsersWithinGeoDomainHirearchy(p_con, p_categoryCode, p_networkCode, p_userName, p_ownerUserID, p_geographicalDomainCode, p_loginUserID, user_type);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                arrayList.add(new ListValueVO(rs.getString("user_name"), rs.getString("user_id")));
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error("loadCategoryUsersWithinGeoDomainHirearchy",  loggerValue);
            _log.errorTrace(METHOD_NAME, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[loadCategoryUsersWithinGeoDomainHirearchy]", "", "", "", loggerValue.toString() );
            throw new BTSLBaseException(this, "loadCategoryUsersWithinGeoDomainHirearchy", "error.general.sql.processing");
        } catch (Exception ex) {
        	 loggerValue.setLength(0);
         	loggerValue.append("Exception:");
         	loggerValue.append(ex.getMessage());
            _log.error("", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[loadCategoryUsersWithinGeoDomainHirearchy]", "", "", "", loggerValue.toString() );
            throw new BTSLBaseException(this, "loadCategoryUsersWithinGeoDomainHirearchy", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }

            if (_log.isDebugEnabled()) {
                 loggerValue.setLength(0);
                 loggerValue.append("Exiting:  arrayList Size =");
                 loggerValue.append(arrayList.size());
                _log.debug("loadCategoryUsersWithinGeoDomainHirearchy", loggerValue );
            }
        }
        return arrayList;
    }

    /**
     * Method loadCategoryUsersWithinGeoDomainHirearchy.
     * This method the loads the user list with userID and UserName, for the
     * search screen .
     * 
     * @param p_con
     *            Connection
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_userName
     *            String
     * @param p_ownerUserID
     *            String
     * @param p_geographicalDomainCode
     *            String
     * @param p_loginUserID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadChannelUsersWithinGeoDomainHirearchy(Connection p_con, String p_categoryCode, String p_networkCode, String p_userName, String p_ownerUserID, String p_geographicalDomainCode, String p_loginUserID, String user_type, int approval_level) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  Category Code ");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(" Network Code ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" User Name ");
        	loggerValue.append(p_userName);
        	loggerValue.append("  ownerUserID ");
        	loggerValue.append(p_ownerUserID);
        	loggerValue.append(" p_geographicalDomainCode: ");
        	loggerValue.append(p_geographicalDomainCode);
        	loggerValue.append(" loginUserID: " );
        	loggerValue.append(p_loginUserID);
            _log.debug( "loadChannelUsersWithinGeoDomainHirearchy",loggerValue );
        }
        final String METHOD_NAME = "loadChannelUsersWithinGeoDomainHirearchy";
        // commented for DB2 OraclePreparedStatement pstmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        

        final ArrayList arrayList = new ArrayList();
        try {
        	
        	AutoO2CQry autoO2CQry=(AutoO2CQry)ObjectProducer.getObject(QueryConstants.AUTO_O2C_QRY, QueryConstants.QUERY_PRODUCER);
            
        	pstmt = autoO2CQry.loadChannelUsersWithinGeoDomainHirearchy(p_con, p_categoryCode, p_networkCode, p_userName, p_ownerUserID, p_geographicalDomainCode, p_loginUserID, user_type, approval_level);
           
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                arrayList.add(new ListValueVO(rs.getString("user_name"), rs.getString("user_id")));
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error("loadChannelUsersWithinGeoDomainHirearchy",  loggerValue);
            _log.errorTrace(METHOD_NAME, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[loadCategoryUsersWithinGeoDomainHirearchy]", "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, "loadChannelUsersWithinGeoDomainHirearchy", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
            loggerValue.append("Exception:");
        	loggerValue.append(ex.getMessage());
            _log.error("", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[loadCategoryUsersWithinGeoDomainHirearchy]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadChannelUsersWithinGeoDomainHirearchy", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }

            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  arrayList Size =" );
            	loggerValue.append(arrayList.size());
                _log.debug("loadChannelUsersWithinGeoDomainHirearchy", loggerValue );
            }
        }
        return arrayList;
    }

}