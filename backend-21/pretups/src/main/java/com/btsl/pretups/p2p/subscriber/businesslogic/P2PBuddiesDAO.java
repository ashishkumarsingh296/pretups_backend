package com.btsl.pretups.p2p.subscriber.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.util.BTSLUtil;

public class P2PBuddiesDAO {

    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Returns an object of BuddVO that will contain Buddy information if the
     * passed buddy belong to the parent
     * 
     * @param p_con
     * @param p_parentID
     * @param p_buddy
     * @return BuddyVO
     * @throws BTSLBaseException
     */
    public BuddyVO loadBuddyDetails(Connection p_con, String p_parentID, String p_buddy) throws BTSLBaseException {
        final String methodName = "loadBuddyDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_parentID:" + p_parentID + " p_buddy:" + p_buddy);
        }

        PreparedStatement pstmtSelect = null;
        BuddyVO buddyVO = null;
        ResultSet rs = null;
        try {
            final StringBuffer selectQueryBuff = new StringBuffer();
            selectQueryBuff.append("SELECT buddy_msisdn, parent_id, buddy_seq_num,buddy_name,status, ");
            selectQueryBuff.append("buddy_last_transfer_id, buddy_last_transfer_on,buddy_last_transfer_type, ");
            selectQueryBuff.append("buddy_total_transfer, buddy_total_transfer_amt, created_on, created_by,modified_on, ");
            selectQueryBuff.append("modified_by,last_transfer_amount,prefix_id,preferred_amount ");
            selectQueryBuff.append("FROM p2p_buddies ");
            selectQueryBuff.append("WHERE parent_id=? AND (upper(buddy_name)=?  OR buddy_msisdn=?) ");
            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_parentID);
            pstmtSelect.setString(2, p_buddy.toUpperCase());
            pstmtSelect.setString(3, p_buddy);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                buddyVO = new BuddyVO();
                buddyVO.setMsisdn(rs.getString("buddy_msisdn"));
                buddyVO.setOwnerUser(rs.getString("parent_id"));
                buddyVO.setSeqNumber(rs.getInt("buddy_seq_num"));
                buddyVO.setName(rs.getString("buddy_name"));
                buddyVO.setPrefixID(rs.getLong("prefix_id"));
                buddyVO.setStatus(rs.getString("status"));
                buddyVO.setLastTransferID(rs.getString("buddy_last_transfer_id"));
                if (rs.getTimestamp("buddy_last_transfer_on") != null) {
                    buddyVO.setLastTransferOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("buddy_last_transfer_on")));
                }
                buddyVO.setLastTransferType(rs.getString("buddy_last_transfer_type"));
                buddyVO.setBuddyTotalTransfers(rs.getLong("buddy_total_transfer"));
                buddyVO.setBuddyTotalTransferAmount(rs.getLong("buddy_total_transfer_amt"));
                if (rs.getTimestamp("created_on") != null) {
                    buddyVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                }
                buddyVO.setCreatedBy(rs.getString("created_by"));
                if (rs.getTimestamp("modified_on") != null) {
                    buddyVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                }
                buddyVO.setModifiedBy(rs.getString("modified_by"));
                buddyVO.setLastTransferAmount(rs.getLong("last_transfer_amount"));
                buddyVO.setPrefixID(rs.getLong("prefix_id"));
                buddyVO.setPreferredAmount(rs.getLong("preferred_amount"));
            }
            return buddyVO;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[loadBuddyDetails]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[loadBuddyDetails]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting buddyVO:" + buddyVO);
            }
        }// end of finally
    }

    /**
     * To add buddy in the subscriber List
     * 
     * @param p_con
     * @param p_buddyVO
     * @return int
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public int addBuddy(Connection p_con, BuddyVO p_buddyVO) throws BTSLBaseException {
        final String methodName = "addBuddy";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered buddy VO : " + p_buddyVO);
        }
        PreparedStatement psmt = null;
        int insertCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer();
            // insert query
            strBuff.append(" INSERT INTO p2p_buddies  ");
            strBuff.append(" (buddy_msisdn, parent_id, buddy_seq_num, buddy_name,prefix_id, status, ");
            strBuff.append(" preferred_amount, created_on, created_by, modified_on, modified_by) ");
            strBuff.append(" VALUES ");
            strBuff.append(" (?,?,?,?,?,?,?,?,?,?,?) ");
            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query:" + query);
            }

            psmt = p_con.prepareStatement(query);

            psmt.setString(1, p_buddyVO.getMsisdn());
            psmt.setString(2, p_buddyVO.getOwnerUser());
            psmt.setInt(3, p_buddyVO.getSeqNumber());
            psmt.setString(4, p_buddyVO.getName());
            psmt.setLong(5, p_buddyVO.getPrefixID());
            psmt.setString(6, p_buddyVO.getStatus());
            psmt.setLong(7, p_buddyVO.getPreferredAmount());
            psmt.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(p_buddyVO.getCreatedOn()));
            psmt.setString(9, p_buddyVO.getCreatedBy());
            psmt.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(p_buddyVO.getModifiedOn()));
            psmt.setString(11, p_buddyVO.getModifiedBy());
            insertCount = psmt.executeUpdate();

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[addBuddy]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[addBuddy]", "", "", "", "Exception:" + e
                .getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting insertCount  :" + insertCount);
            }
        }// end of finally

        return insertCount;

    }

    /**
     * Delete the buddy from user buddy list
     * 
     * @param p_con
     * @param p_buddyVO
     * @return update Count
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public int deleteBuddy(Connection p_con, BuddyVO p_buddyVO) throws BTSLBaseException {
        final String methodName = "deleteBuddy";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered buddy VO : " + p_buddyVO);
        }

        PreparedStatement psmt = null;
        int deleteCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer();
            // insert query
            strBuff.append(" DELETE FROM p2p_buddies  ");
            strBuff.append(" WHERE parent_id=? AND  buddy_msisdn=? ");

            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query:" + query);
            }

            psmt = p_con.prepareStatement(query);

            psmt.setString(1, p_buddyVO.getOwnerUser());
            psmt.setString(2, p_buddyVO.getMsisdn());
            deleteCount = psmt.executeUpdate();

            // entry in the history table
            if (deleteCount > 0) {
                final ArrayList arrayList = new ArrayList();
                arrayList.add(p_buddyVO);
                // deleteCount = this.addBuddyHistory(p_con, arrayList);
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[deleteBuddy]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[deleteBuddy]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting update Count :" + deleteCount);
            }
        }// end of finally
        return deleteCount;
    }

    /**
     * To delete all the buddies of the user and move them into history table
     * 
     * @param p_con
     * @param p_senderVO
     *            SenderVO
     * @return int status
     * @throws BTSLBaseException
     */
    public int deleteBuddiesList(Connection p_con, SenderVO p_senderVO) throws BTSLBaseException {
        final String methodName = "deleteBuddiesList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered Sender VO  : " + p_senderVO);
        }
        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer(100);
            // insert query
            strBuff.append(" DELETE FROM p2p_buddies  ");
            strBuff.append(" WHERE parent_id=? ");

            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "delete query:" + query);
            }

            psmt = p_con.prepareStatement(query);
            psmt.setString(1, p_senderVO.getUserID());
            updateCount = psmt.executeUpdate();
            /*
             * if (updateCount > 0)
             * {
             * updateCount = this.addBuddyHistory(p_con,
             * p_senderVO.getVoList());
             * }
             */
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[deleteBuddiesList]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[deleteBuddiesList]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting update Count :" + updateCount);
            }
        }// end of finally
        return updateCount;
    }

    /**
     * Returns Buddy List information if the passed buddyList belong to the
     * parent
     * 
     * @param p_con
     * @param p_listname
     * @param p_sendermsisdn
     * @return list
     * @throws BTSLBaseException
     * @author harsh dixit
     * @date 10 Aug 12
     */
    public ArrayList loadBuddyListDetails(Connection p_con, String p_listname, String p_sendermsisdn) throws BTSLBaseException {
        final String methodName = "loadBuddyListDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered senderMsisdn:" + p_sendermsisdn + " p_buddyListName:" + p_listname);
        }

        PreparedStatement pstmtSelect = null;
        BuddyVO buddyVO = null;
        ResultSet rs = null;
        final ArrayList list = new ArrayList();
        try {
            final StringBuffer selectQueryBuff = new StringBuffer();
            selectQueryBuff.append("SELECT buddy_msisdn, parent_id, buddy_seq_num,buddy_name,status, ");
            selectQueryBuff.append("buddy_last_transfer_id, buddy_last_transfer_on,buddy_last_transfer_type, ");
            selectQueryBuff.append("buddy_total_transfer, buddy_total_transfer_amt, created_on, created_by,modified_on, ");
            selectQueryBuff.append("modified_by,last_transfer_amount,prefix_id,preferred_amount,list_name,selector_code ");
            selectQueryBuff.append("FROM p2p_buddies WHERE parent_id IN (Select user_id from P2P_Subscribers where msisdn=?) ");
            if (!BTSLUtil.isNullString(p_listname)) {
                selectQueryBuff.append(" AND list_name=?");
            }
            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_sendermsisdn);
            if (!BTSLUtil.isNullString(p_listname)) {
                pstmtSelect.setString(2, p_listname);
            }

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                buddyVO = new BuddyVO();
                buddyVO.setMsisdn(rs.getString("buddy_msisdn"));
                buddyVO.setOwnerUser(rs.getString("parent_id"));
                buddyVO.setSeqNumber(rs.getInt("buddy_seq_num"));
                buddyVO.setName(rs.getString("buddy_name"));
                buddyVO.setStatus(rs.getString("status"));
                buddyVO.setLastTransferID(rs.getString("buddy_last_transfer_id"));
                if (rs.getTimestamp("buddy_last_transfer_on") != null) {
                    buddyVO.setLastTransferOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("buddy_last_transfer_on")));
                }
                buddyVO.setLastTransferType(rs.getString("buddy_last_transfer_type"));
                buddyVO.setBuddyTotalTransfers(rs.getLong("buddy_total_transfer"));
                buddyVO.setBuddyTotalTransferAmount(rs.getLong("buddy_total_transfer_amt"));
                if (rs.getTimestamp("created_on") != null) {
                    buddyVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                }
                buddyVO.setCreatedBy(rs.getString("created_by"));
                if (rs.getTimestamp("modified_on") != null) {
                    buddyVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                }
                buddyVO.setModifiedBy(rs.getString("modified_by"));
                buddyVO.setLastTransferAmount(rs.getLong("last_transfer_amount"));
                buddyVO.setPrefixID(rs.getLong("prefix_id"));
                buddyVO.setPreferredAmount(rs.getLong("preferred_amount"));
                buddyVO.setListName(rs.getString("list_name"));
                buddyVO.setSelectorCode(rs.getString("selector_code"));
                list.add(buddyVO);
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[loadBuddyListDetails]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[loadBuddyListDetails]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting list size:" + list.size());
            }
        }// end of finally
        return list;
    }

    // end of loadBuddyListDetails by harsh

    /**
     * To delete buddyList of the user and move them into history table
     * 
     * @param p_con
     * @param String
     *            p_listname
     * @param String
     *            p_sendermsisdn
     * @return int updateCount
     * @throws BTSLBaseException
     * @author Harsh Dixit
     * @date 10 Aug 12
     */
    public int delMultCreditTrfList(Connection p_con, String p_listname, String p_sendermsisdn) throws BTSLBaseException {
        final String methodName = "delMultCreditTrfList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered Sender Msisdn  : " + p_sendermsisdn);
        }
        PreparedStatement psmt = null;
        int updateCount = 0;
        ArrayList list = new ArrayList();
        list = this.loadBuddyListDetails(p_con, p_listname, p_sendermsisdn);
        try {

            final StringBuffer strBuff = new StringBuffer(100);
            // insert query
            strBuff.append("DELETE FROM p2p_buddies  ");
            strBuff.append("WHERE parent_id IN (SELECT user_id FROM P2P_Subscribers WHERE msisdn=?)");
            if (!BTSLUtil.isNullString(p_listname)) {
                strBuff.append(" AND list_name=? ");
            }

            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "delete query:" + query);
            }

            psmt = p_con.prepareStatement(query);
            psmt.setString(1, p_sendermsisdn);
            if (!BTSLUtil.isNullString(p_listname)) {
                psmt.setString(2, p_listname);
            }
            updateCount = psmt.executeUpdate();
            if (updateCount > 0) {
                // updateCount = this.addBuddyHistory(p_con, list);
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[delMultCreditTrfList]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[delMultCreditTrfList]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting update Count :" + updateCount);
            }
        }// end of finally
        return updateCount;
    }

    /**
     * Load the buddy list of the P2P Subscriber
     * 
     * @param p_con
     * @param p_parentID
     * @return ArrayList which have the object of BuddyVO
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public ArrayList loadBuddyList(Connection p_con, String p_parentID) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("getBuddyList", "Entered p_parentID:" + p_parentID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList list = new ArrayList();
        BuddyVO buddyVO = null;
        final String methodName = "loadBuddyList";
        try {

            final StringBuffer selectQueryBuff = new StringBuffer();
            selectQueryBuff.append("SELECT buddy_msisdn,parent_id, buddy_seq_num,buddy_name, prefix_id,status,  ");
            selectQueryBuff.append("buddy_last_transfer_id, buddy_last_transfer_on,buddy_last_transfer_type,");
            selectQueryBuff.append("buddy_total_transfer,buddy_total_transfer_amt,created_on, created_by,modified_on, ");
            selectQueryBuff.append("modified_by,last_transfer_amount,preferred_amount ");
            selectQueryBuff.append("FROM p2p_buddies ");
            selectQueryBuff.append("WHERE parent_id=? ");
            selectQueryBuff.append("ORDER BY buddy_seq_num ");
            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("loadBuddyDetails", "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_parentID);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                buddyVO = new BuddyVO();
                buddyVO.setMsisdn(rs.getString("buddy_msisdn"));
                buddyVO.setOwnerUser(rs.getString("parent_id"));
                buddyVO.setSeqNumber(rs.getInt("buddy_seq_num"));
                buddyVO.setName(rs.getString("buddy_name"));
                buddyVO.setPrefixID(rs.getLong("prefix_id"));
                buddyVO.setStatus(rs.getString("status"));
                buddyVO.setLastTransferID(rs.getString("buddy_last_transfer_id"));
                if (rs.getTimestamp("buddy_last_transfer_on") != null) {
                    buddyVO.setLastTransferOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("buddy_last_transfer_on")));
                }
                if (rs.getTimestamp("buddy_last_transfer_on") != null) {
                    buddyVO.setLastTxnOnAsString(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("buddy_last_transfer_on")));
                }
                buddyVO.setLastTransferType(rs.getString("buddy_last_transfer_type"));
                buddyVO.setBuddyTotalTransfers(rs.getLong("buddy_total_transfer"));
                buddyVO.setBuddyTotalTransferAmount(rs.getLong("buddy_total_transfer_amt"));
                if (rs.getTimestamp("created_on") != null) {
                    buddyVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                }
                if (rs.getTimestamp("created_on") != null) {
                    buddyVO.setCreatedOnAsString(BTSLUtil.getDateStringFromDate(rs.getTimestamp("created_on")));
                }
                buddyVO.setCreatedBy(rs.getString("created_by"));
                if (rs.getTimestamp("modified_on") != null) {
                    buddyVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                }
                buddyVO.setModifiedBy(rs.getString("modified_by"));
                buddyVO.setLastTransferAmount(rs.getLong("last_transfer_amount"));
                buddyVO.setPreferredAmount(rs.getLong("preferred_amount"));
                list.add(buddyVO);
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[loadBuddyList]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[loadBuddyList]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("getBuddyList", "Exiting buddyVO:" + list.size());
            }
        }// end of finally
        return list;
    }

    /**
     * Method to update the buddy details
     * 
     * @param p_con
     * @param p_buddyVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateBuddyDetails(Connection p_con, BuddyVO p_buddyVO) throws BTSLBaseException {
        final String methodName = "updateBuddyDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_buddyVO :" + p_buddyVO);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            final StringBuffer sbf = new StringBuffer(" UPDATE p2p_buddies SET buddy_last_transfer_id=?,buddy_last_transfer_on=?,buddy_last_transfer_type=?, ");
            sbf.append(" last_transfer_amount=?,buddy_total_transfer=?,buddy_total_transfer_amt=?,modified_on=?, modified_by=? ");
            sbf.append(" WHERE buddy_msisdn=? AND  parent_id=? ");
            final String updateQuery = sbf.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + updateQuery);
            }

            int i = 1;

            pstmtUpdate = p_con.prepareStatement(updateQuery);

            pstmtUpdate.setString(i++, p_buddyVO.getLastTransferID());
            if (p_buddyVO.getLastTransferOn() != null) {
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_buddyVO.getLastTransferOn()));
            } else {
                pstmtUpdate.setNull(i++, Types.DATE);
            }
            pstmtUpdate.setString(i++, p_buddyVO.getLastTransferType());
            pstmtUpdate.setLong(i++, p_buddyVO.getLastTransferAmount());
            pstmtUpdate.setLong(i++, p_buddyVO.getBuddyTotalTransfers());
            pstmtUpdate.setLong(i++, p_buddyVO.getBuddyTotalTransferAmount());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_buddyVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_buddyVO.getModifiedBy());
            pstmtUpdate.setString(i++, p_buddyVO.getMsisdn());
            pstmtUpdate.setString(i++, p_buddyVO.getOwnerUser());
            updateCount = pstmtUpdate.executeUpdate();
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[updateBuddyDetails]", "", p_buddyVO
                .getMsisdn(), "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[updateBuddyDetails]", "", p_buddyVO
                .getMsisdn(), "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting updateCount:" + updateCount);
            }
        }// end of finally
        return updateCount;
    }

    /**
     * Returns an object of BuddVO that will contain Buddy information if the
     * buddy is exist for BuddynName and Mobli
     * for the parent
     * 
     * @param p_con
     * @param p_parentID
     * @param p_buddyName
     * @param p_buddyMobileNo
     * @return BuddyVO
     * @throws BTSLBaseException
     */
    public BuddyVO subscriberBuddyExist(Connection p_con, String p_parentID, String p_buddyName, String p_buddyMobileNo) throws BTSLBaseException {
        final String methodName = "subscriberBuddyExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_parentID:" + p_parentID + " p_buddyName:" + p_buddyName + "p_buddyMobileNo:" + p_buddyMobileNo);
        }

        PreparedStatement pstmtSelect = null;
        BuddyVO buddyVO = null;
        ResultSet rs = null;
        try {
            final StringBuffer selectQueryBuff = new StringBuffer();
            selectQueryBuff.append(" SELECT buddy_msisdn, parent_id, buddy_seq_num,buddy_name,status ");
            selectQueryBuff.append(" FROM p2p_buddies ");
            selectQueryBuff.append(" WHERE parent_id = ? AND ( upper(buddy_name) = upper(?)  OR buddy_msisdn = ? ) ");
            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }

            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_parentID);
            pstmtSelect.setString(2, p_buddyName);
            pstmtSelect.setString(3, p_buddyMobileNo);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                buddyVO = new BuddyVO();
                buddyVO.setMsisdn(rs.getString("buddy_msisdn"));
                buddyVO.setOwnerUser(rs.getString("parent_id"));
                buddyVO.setSeqNumber(rs.getInt("buddy_seq_num"));
                buddyVO.setName(rs.getString("buddy_name"));
                buddyVO.setStatus(rs.getString("status"));
            }
            return buddyVO;

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[loadBuddyDetails]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[loadBuddyDetails]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting buddyVO:" + buddyVO);
            }
        }// end of finally
    }// end subscriberBuddyExist

    public BuddyVO loadReciverMSISDN(Connection p_con, String Parent_id, String MSISDN_Name) throws BTSLBaseException {
        final String methodName = "loadReciverMSISDN";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered MSISDN_Name:" + MSISDN_Name + "Parent_id:" + Parent_id);
        }

        PreparedStatement pstmtSelect = null;
        BuddyVO buddyVO = null;
        ResultSet rs = null;
        try {
            final StringBuffer selectQueryBuff = new StringBuffer();
            selectQueryBuff.append("SELECT BUDDY_MSISDN FROM P2P_BUDDIES ");
            selectQueryBuff.append("  WHERE status= ? AND PARENT_ID= ? AND (buddy_name=?  OR  buddy_msisdn=? )");
            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(2, Parent_id);
            pstmtSelect.setString(3, MSISDN_Name);
            pstmtSelect.setString(4, MSISDN_Name);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                buddyVO = new BuddyVO();
                buddyVO.setMsisdn(rs.getString("BUDDY_MSISDN"));
            }
            return buddyVO;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[loadReciverMSISDN]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PBuddiesDAO[loadReciverMSISDN]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting RECIEVER MSISDN:" + buddyVO);
            }
        }// end of finally
    }

}
