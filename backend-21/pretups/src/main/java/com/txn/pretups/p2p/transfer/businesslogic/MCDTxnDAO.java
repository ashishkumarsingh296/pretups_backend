package com.txn.pretups.p2p.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.p2p.transfer.businesslogic.MCDListVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.ibm.icu.util.Calendar;

public class MCDTxnDAO {

    private Log _log = LogFactory.getLog(this.getClass().getName());

    public int isListAlreadyRegistered(Connection p_con, String p_listName, String p_userID) throws BTSLBaseException {

        final String methodName = "isListAlreadyRegistered";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_listName " + p_listName, "p_userID : " + p_userID);
        }

        int listCount = 0;
        String listNameDB = null;
        PreparedStatement pstm = null;
        PreparedStatement pstm1 = null;
        ResultSet rst = null;
        ResultSet rst1 = null;
        try {
            String query = " SELECT list_name FROM p2p_buddies WHERE  parent_id=?  and  status = ?  ";

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query: " + query);
            }

            String selectCount = "select  count (  buddy_msisdn) count from p2p_buddies WHERE  parent_id=? and status = ? and list_name= ? ";
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "selectCount: " + selectCount);
            }

            pstm = p_con.prepareStatement(query);
            pstm.setString(1, p_userID);
            pstm.setString(2, PretupsI.YES);
            rst = pstm.executeQuery();

            if (rst.next()) {
                listNameDB = rst.getString("list_name");

                if (p_listName.equals(listNameDB)) {
                    pstm1 = p_con.prepareStatement(selectCount);
                    pstm1.setString(1, p_userID);
                    pstm1.setString(2, PretupsI.YES);
                    pstm1.setString(3, p_listName);
                    rst1 = pstm1.executeQuery();
                    if (rst1.next()) {
                        listCount = rst1.getInt("count");
                    }
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_MULT_CDT_LIST_AMD_ALREADYREGD);
                }
            }

        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException : " + be);
            _log.errorTrace(methodName, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[isListAlreadyRegistered]", "", "", "", "BTSLBaseException:" + be.getMessage());
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[isListAlreadyRegistered]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isCardNumberAlreadyRegistered", PretupsErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[isListAlreadyRegistered]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rst1 != null) {
                    rst1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstm1!= null) {
                    pstm1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting listCount " + listCount);
            }
        }
        return listCount;

    }

    public boolean addMCDListAmountDetailsForSelector(Connection p_con, String p_selector, long p_amount,
            MCDListVO mcdListVO) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        PreparedStatement pstmtSelectMsisdn = null;
        int insertCount = 0;
        boolean detailsadded = false;
        StringBuffer strBuff = null;
        ResultSet rst = null;
        final String methodName = "addMCDListAmountDetailsForSelector";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: mcdListVO= " + mcdListVO + "p_selector" + p_selector + " p_amount"
                    + p_amount);
        }
        try {
            strBuff = new StringBuffer(
                    "SELECT 1 FROM p2p_buddies WHERE BUDDY_MSISDN=? and selector_code=? and parent_id =? and status =? ");
            String selectMsisdnQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query selectMsisdnQuery:" + selectMsisdnQuery);
            }
            pstmtSelectMsisdn = (PreparedStatement) p_con.prepareStatement(selectMsisdnQuery);

            strBuff = new StringBuffer();
            strBuff.append("INSERT INTO p2p_buddies (BUDDY_MSISDN, PARENT_ID, STATUS,");
            strBuff.append("CREATED_ON, CREATED_BY, PREFERRED_AMOUNT, PREFIX_ID,");
            strBuff.append(" LIST_NAME, SELECTOR_CODE,MODIFIED_ON, MODIFIED_BY)");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }
            psmtInsert = (PreparedStatement) p_con.prepareStatement(insertQuery);

            pstmtSelectMsisdn.setString(1, mcdListVO.getMsisdn());
            pstmtSelectMsisdn.setString(2, p_selector);
            pstmtSelectMsisdn.setString(3, mcdListVO.getParentID());
            pstmtSelectMsisdn.setString(4, PretupsI.YES);
            rst = pstmtSelectMsisdn.executeQuery();
            if (rst.next()) {
                mcdListVO.setReason1("Msisdn Already Present");
                mcdListVO.setReason2("Msisdn Already Present");
            } else {
                psmtInsert.setString(1, mcdListVO.getMsisdn());
                psmtInsert.setString(2, mcdListVO.getParentID());
                psmtInsert.setString(3, PretupsI.YES);
                psmtInsert.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(mcdListVO.getCreatedOn()));
                psmtInsert.setString(5, mcdListVO.getCreatedBy());
                psmtInsert.setDouble(6, p_amount);
                psmtInsert.setLong(7, mcdListVO.getPrefixID());
                psmtInsert.setString(8, mcdListVO.getListName());
                psmtInsert.setString(9, p_selector);
                psmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(mcdListVO.getModifiedOn()));
                psmtInsert.setString(11, mcdListVO.getModifiedBy());
                insertCount = psmtInsert.executeUpdate();
                if (insertCount > 0) {
                    detailsadded = true;
                }
            }

        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[addMCDListAmountDetailsForSelector]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[addMCDListAmountDetailsForSelector]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rst != null) {
                	rst.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectMsisdn != null) {
                	pstmtSelectMsisdn.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return detailsadded;

    }

    public boolean updateMCDListAmountDetailsForSelector(Connection p_con, String p_selector, long p_amount,
            MCDListVO mcdListVO) throws BTSLBaseException {
        PreparedStatement psmtUpdate = null;
        PreparedStatement pstmtSelectMsisdn = null;
        StringBuffer strBuff = null;
        int updatedCount = 0;
        boolean detailsupdated = false;
        ResultSet rst = null;
        final String methodName = "updateMCDListAmountDetailsForSelector";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: mcdListVO= " + mcdListVO + " p_selector " + p_selector + " p_amount "
                    + p_amount);
        }
        try {
            strBuff = new StringBuffer(
                    "SELECT 1 FROM p2p_buddies WHERE BUDDY_MSISDN=? and selector_code=? and status=? ");
            String selectMsisdnQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query selectMsisdnQuery:" + selectMsisdnQuery);
            }
            pstmtSelectMsisdn = (PreparedStatement) p_con.prepareStatement(selectMsisdnQuery);

            strBuff = new StringBuffer();
            strBuff.append(" update p2p_buddies set PREFERRED_AMOUNT=? , MODIFIED_ON =? , MODIFIED_BY =? ");
            strBuff.append(" where BUDDY_MSISDN= ? and parent_id = ? and selector_code=? and status =? ");
            String updateQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query updateInsert:" + updateQuery);
            }
            psmtUpdate = (PreparedStatement) p_con.prepareStatement(updateQuery);

            pstmtSelectMsisdn.setString(1, mcdListVO.getMsisdn());
            pstmtSelectMsisdn.setString(2, p_selector);
            pstmtSelectMsisdn.setString(3, PretupsI.YES);
            rst = pstmtSelectMsisdn.executeQuery();
            if (rst.next()) {
                psmtUpdate.setDouble(1, p_amount);
                psmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(mcdListVO.getModifiedOn()));
                psmtUpdate.setString(3, mcdListVO.getModifiedBy());
                psmtUpdate.setString(4, mcdListVO.getMsisdn());
                psmtUpdate.setString(5, mcdListVO.getParentID());
                psmtUpdate.setString(6, p_selector);
                psmtUpdate.setString(7, PretupsI.YES);

                updatedCount = psmtUpdate.executeUpdate();
                if (updatedCount > 0) {
                    detailsupdated = true;
                }
            } else {
                mcdListVO.setReason1(" Msisdn Not Found ");

            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[updateMCDListAmountDetailsForSelector]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[updateMCDListAmountDetailsForSelector]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	 try {
                 if (rst != null) {
                	 rst.close();
                 }
             } catch (Exception e) {
                 _log.errorTrace(methodName, e);
             }
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectMsisdn != null) {
                	pstmtSelectMsisdn.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: insertCount=" + updatedCount);
            }
        } // end of finally

        return detailsupdated;

    }

    public boolean deleteMCDListAmountDetailsForSelector(Connection p_con, String p_selector, MCDListVO mcdListVO)
            throws BTSLBaseException {
        PreparedStatement psmtUpdate = null;
        int updatedCount = 0;
        boolean detailsupdated = false;
        final String methodName = "deleteMCDListAmountDetailsForSelector";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: mcdListVO= " + mcdListVO + " p_selector" + p_selector);
        }
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("delete from  p2p_buddies ");
            strBuff.append("where buddy_msisdn= ? and selector_code=? and parent_id= ? and status =? ");
            String updateQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query updateInsert:" + updateQuery);
            }
            psmtUpdate = (PreparedStatement) p_con.prepareStatement(updateQuery);

            psmtUpdate.setString(1, mcdListVO.getMsisdn());
            psmtUpdate.setString(2, p_selector);
            psmtUpdate.setString(3, mcdListVO.getParentID());
            psmtUpdate.setString(4, PretupsI.YES);

            updatedCount = psmtUpdate.executeUpdate();
            if (updatedCount > 0) {
                detailsupdated = true;
            }

        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[deleteMCDListAmountDetailsForSelector]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[deleteMCDListAmountDetailsForSelector]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: insertCount=" + updatedCount);
            }
        } // end of finally

        return detailsupdated;

    }

    public boolean deleteMCDList(Connection p_con, String p_list, String p_userID) throws BTSLBaseException {
        PreparedStatement psmtUpdate = null;
        int deleteCount = 0;
        boolean listdeleted = false;
        final String methodName = "deleteMCDList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_list" + p_list);
        }
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("delete from  p2p_buddies ");
            strBuff.append("where parent_id=? and list_name=? ");
            String deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query updateInsert:" + deleteQuery);
            }
            psmtUpdate = (PreparedStatement) p_con.prepareStatement(deleteQuery);

            psmtUpdate.setString(1, p_userID);
            psmtUpdate.setString(2, p_list);

            deleteCount = psmtUpdate.executeUpdate();
            if (deleteCount > 0) {
                listdeleted = true;
            }

        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[deleteMCDList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[deleteMCDList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: deleteCount=" + deleteCount);
            }
        } // end of finally

        return listdeleted;

    }

    public ArrayList loadBuddySelectorAmountList(Connection p_con, String p_userID, String p_listName)
            throws BTSLBaseException {
        final String methodName = "loadBuddySelectorAmountList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered   p_userID " + p_userID + " p_listName " + p_listName);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList arrayList = null;
        MCDListVO mcdListVO = null;
        StringBuffer strBuff = new StringBuffer("SELECT BUDDY_MSISDN,  SELECTOR_CODE,PREFERRED_AMOUNT,LIST_NAME  ");
        strBuff.append("FROM P2P_BUDDIES WHERE PARENT_ID= ? AND STATUS =? ");
        if (!BTSLUtil.isNullString(p_listName)) {
            strBuff.append("  AND LIST_NAME =?");
        }
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            arrayList = new ArrayList();
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_userID);
            pstmt.setString(2, PretupsI.YES);

            if (!BTSLUtil.isNullString(p_listName)) {
                pstmt.setString(3, p_listName);
            }
            rs = pstmt.executeQuery();

            while (rs.next()) {
                mcdListVO = new MCDListVO();
                mcdListVO.setMsisdn(rs.getString("BUDDY_MSISDN"));
                mcdListVO.setSelector1(rs.getString("SELECTOR_CODE"));
                mcdListVO.setAmount1(rs.getLong("PREFERRED_AMOUNT"));
                mcdListVO.setListName(rs.getString("LIST_NAME"));
                p_listName = mcdListVO.getListName();
                arrayList.add(mcdListVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[loadBuddySelectorAmountList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[loadBuddySelectorAmountList]", "", "", "", "Exception:" + ex.getMessage());
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

    /**
     * This method is used to check uniqueness of p2p_batch in Database for
     * Schedulder Credit Transfer .
     * 
     * @param p_con
     * @param p_requestVO
     *            RequestVO
     * @return batchExists boolean
     * @throws BTSLBaseException
     * @author Harsh.Dixit
     * @date 06-05-2013
     */

    public boolean isBatchAlreadyExist(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {

        final String methodName = "isBatchAlreadyExist";
        if (_log.isDebugEnabled()) {
            _log.debug(
                    methodName,
                    "Entered: p_listName " + p_requestVO.getMcdListName() + " p_userID "
                            + p_requestVO.getActiverUserId() + " p_schType " + p_requestVO.getMcdScheduleType());
        }
        int listCount = 0;
        String listNameDB = null;
        boolean batchExists = false;
        PreparedStatement pstm = null;
        PreparedStatement pstm1 = null;
        ResultSet rst = null;
        ResultSet rst1 = null;
        try {
            String query = " SELECT list_name FROM p2p_batches WHERE  parent_id=?  and  status = ? and schedule_type = ? ";

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query: " + query);
            }

            String selectCount = "select  count (  batch_id) count from p2p_batches WHERE  parent_id=? and status = ? and list_name= ? and schedule_type = ? ";
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "selectCount: " + selectCount);
            }

            pstm = p_con.prepareStatement(query);
            pstm.setString(1, p_requestVO.getActiverUserId());
            pstm.setString(2, PretupsI.YES);
            pstm.setString(3, p_requestVO.getMcdScheduleType());
            rst = pstm.executeQuery();

            if (rst.next()) {
                listNameDB = rst.getString("list_name");
                if (p_requestVO.getMcdListName().equals(listNameDB)) {
                    pstm1 = p_con.prepareStatement(selectCount);
                    pstm1.setString(1, p_requestVO.getActiverUserId());
                    pstm1.setString(2, PretupsI.YES);
                    pstm1.setString(3, p_requestVO.getMcdListName());
                    pstm1.setString(4, p_requestVO.getMcdScheduleType());
                    rst1 = pstm1.executeQuery();
                    if (rst1.next()) {
                        listCount = rst1.getInt("count");
                    }
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_MULT_CDT_BATCH_AMD_ALREADYREGD);
                }
            }
            if (listCount == 1) {
                batchExists = true;
            }
        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException : " + be);
            _log.errorTrace(methodName, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[isBatchAlreadyExist]", "", "", "", "BTSLBaseException:" + be.getMessage());
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[isBatchAlreadyExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isCardNumberAlreadyRegistered", PretupsErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[isBatchAlreadyExist]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "isListAlreadyRegistered", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rst1 != null) {
                    rst1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstm1 != null) {
                    pstm1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting listCount " + listCount);
            }
        }
        return batchExists;

    }

    /**
     * This method is used to insert p2p_batch entry in Database for Schedulder
     * Credit Transfer .
     * 
     * @param p_con
     * @param p_requestVO
     *            RequestVO
     * @return batchAdded
     * @throws BTSLBaseException
     * @author Harsh.Dixit
     * @date 06-05-2013
     */
    public boolean addP2PBatchDetails(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        OperatorUtilI operatorUtil = null;
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        boolean batchAdded = false;
        // int noOfSchedule=Integer.parseInt(p_requestVO.getMcdNoOfSchedules());
        Long noOfSchedule = Long.parseLong(p_requestVO.getMcdNoOfSchedules());
        StringBuffer strBuff = null;
        String batchID = null;
        final String methodName = "addP2PBatchDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Adding Scheduled Batches Details:" + p_requestVO.getExternalNetworkCode());
        }
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[addP2PBatchDetails]", "", "", "",
                    "Exception while loading the class at the call:" + e.getMessage());
        }
        batchID = operatorUtil.formatBatchesID(
                p_requestVO.getExternalNetworkCode(),
                PretupsI.P2P_BUDDYLIST_BATCH_ID,
                new Date(),
                IDGenerator.getNextID(PretupsI.P2P_BUDDYLIST_BATCH_ID, BTSLUtil.getFinancialYear(),
                        p_requestVO.getExternalNetworkCode()));
        try {

            strBuff = new StringBuffer();

            strBuff.append("INSERT INTO p2p_batches (BATCH_ID, PARENT_ID,LIST_NAME, STATUS,");
            strBuff.append("SCHEDULE_TYPE,NO_OF_SCHEDULE,SENDER_SERVICE_CLASS,");
            strBuff.append("CREATED_BY,CREATED_ON,MODIFIED_BY,MODIFIED_ON,BATCH_TOTAL_RECORD, ");
            // added by harsh for saving schedule_date in DB
            strBuff.append(" SCHEDULE_DATE)");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
            String insertQuery = strBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }

            psmtInsert = (PreparedStatement) p_con.prepareStatement(insertQuery);
            psmtInsert.setString(1, batchID);
            psmtInsert.setString(2, p_requestVO.getActiverUserId());
            psmtInsert.setString(3, p_requestVO.getMcdListName());
            psmtInsert.setString(4, PretupsI.YES);
            psmtInsert.setString(5, p_requestVO.getMcdScheduleType());
            psmtInsert.setLong(6, noOfSchedule);
            psmtInsert.setString(7, p_requestVO.getMcdSenderProfile());
            psmtInsert.setString(8, p_requestVO.getActiverUserId());
            psmtInsert.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(p_requestVO.getCreatedOn()));
            psmtInsert.setString(10, p_requestVO.getActiverUserId());
            psmtInsert.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_requestVO.getCreatedOn()));
            psmtInsert.setLong(12, p_requestVO.getMcdListAddCount());
            // added by harsh for saving schedule_date in DB
            psmtInsert.setDate(13,
                    BTSLUtil.getSQLDateFromUtilDate(getNextScheduleDate(p_requestVO.getMcdScheduleType())));
            insertCount = psmtInsert.executeUpdate();

            if (insertCount > 0) {
                batchAdded = true;
            }

        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[addMCDListAmountDetailsForSelector]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addSMCDListAmountDetails", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[addMCDListAmountDetailsForSelector]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return batchAdded;

    }

    /**
     * This method is used to insert buddy list in Database for Scheduled Credit
     * Transfer .
     * 
     * @param p_con
     * @param p_selector
     *            String
     * @param mcdListVO
     *            MCDListVO
     * @return ChannelUserVO
     * @throws BTSLBaseException
     * @author Harsh.Dixit
     */

    public boolean addSMCDListAmountDetails(Connection p_con, RequestVO p_requestVO, long p_amount, MCDListVO mcdListVO)
            throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        PreparedStatement pstmtSelectMsisdn = null;
        int insertCount = 0;
        boolean detailsadded = false;
        StringBuffer strBuff = null;
        ResultSet rst = null;
        ArrayList selectorList = new ArrayList();
        ServiceSelectorMappingVO serviceSelectorMappingVO = null;
        selectorList = ServiceSelectorMappingCache
                .getSelectorListForServiceType(PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER);
        int selectorListSize = selectorList.size();
        for (int i = 0; i < selectorListSize; i++) {
            serviceSelectorMappingVO = (ServiceSelectorMappingVO) selectorList.get(i);
            if (serviceSelectorMappingVO.isDefaultCode()) {
                break;
            }
        }
        final String methodName = "addSMCDListAmountDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: buddyMsisdn= " + mcdListVO.getMsisdn() + "selector_code="
                    + serviceSelectorMappingVO.getSelectorCode() + "parent_id=" + mcdListVO.getParentID() + "sch_type="
                    + p_requestVO.getMcdScheduleType() + " p_amount" + p_amount);
        }
        try {
            strBuff = new StringBuffer(
                    "SELECT 1 FROM p2p_buddies pbi ,P2P_BATCHES pbt WHERE pbi.BUDDY_MSISDN=? and pbi.selector_code=? and pbi.parent_id =? and pbi.status =? and pbi.list_name=pbt.LIST_NAME  and pbt.SCHEDULE_TYPE=? ");
            String selectMsisdnQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query selectMsisdnQuery:" + selectMsisdnQuery);
            }
            pstmtSelectMsisdn = (PreparedStatement) p_con.prepareStatement(selectMsisdnQuery);

            strBuff = new StringBuffer();

            strBuff.append("INSERT INTO p2p_buddies (BUDDY_MSISDN, PARENT_ID, STATUS, ");
            strBuff.append("CREATED_ON, CREATED_BY, PREFERRED_AMOUNT, PREFIX_ID, ");
            strBuff.append(" LIST_NAME, SELECTOR_CODE,MODIFIED_ON, MODIFIED_BY,RECEIVER_SERVICE_CLASS ) ");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
            String insertQuery = strBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }

            psmtInsert = (PreparedStatement) p_con.prepareStatement(insertQuery);

            pstmtSelectMsisdn.setString(1, mcdListVO.getMsisdn());
            pstmtSelectMsisdn.setString(2, serviceSelectorMappingVO.getSelectorCode());
            pstmtSelectMsisdn.setString(3, mcdListVO.getParentID());
            pstmtSelectMsisdn.setString(4, PretupsI.YES);
            pstmtSelectMsisdn.setString(5, p_requestVO.getMcdScheduleType());

            rst = pstmtSelectMsisdn.executeQuery();
            if (rst.next()) {
                mcdListVO.setReason("Msisdn Already Present");
            } else {

                psmtInsert.setString(1, mcdListVO.getMsisdn());
                psmtInsert.setString(2, mcdListVO.getParentID());
                psmtInsert.setString(3, PretupsI.YES);
                psmtInsert.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(mcdListVO.getCreatedOn()));
                psmtInsert.setString(5, mcdListVO.getCreatedBy());
                psmtInsert.setLong(6, p_amount);
                psmtInsert.setLong(7, mcdListVO.getPrefixID());
                psmtInsert.setString(8, mcdListVO.getListName());
                psmtInsert.setString(9, serviceSelectorMappingVO.getSelectorCode());
                psmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(mcdListVO.getModifiedOn()));
                psmtInsert.setString(11, mcdListVO.getModifiedBy());
                psmtInsert.setString(12, p_requestVO.getReceiverServiceClassId());
                insertCount = psmtInsert.executeUpdate();
                if (insertCount > 0) {
                    detailsadded = true;
                }
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[addMCDListAmountDetailsForSelector]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[addMCDListAmountDetailsForSelector]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rst != null) {
                	rst.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectMsisdn != null) {
                	pstmtSelectMsisdn.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return detailsadded;

    }

    /**
     * This method is used to update buddy list in Database for Scheduled Credit
     * Transfer .
     * 
     * @param p_con
     * @param p_requestVO
     *            RequestVO
     * @param mcdListVO
     *            MCDListVO
     * @return ChannelUserVO
     * @throws BTSLBaseException
     * @author Harsh.Dixit
     */
    public boolean updateSMCDListAmountDetails(Connection p_con, RequestVO p_requestVO, long p_amount,
            MCDListVO mcdListVO) throws BTSLBaseException {
        PreparedStatement psmtUpdate = null;
        PreparedStatement psmtUpdate1 = null;
        PreparedStatement pstmtSelectMsisdn = null;
        StringBuffer strBuff = null;
        int updatedCount = 0;
        // int noOfScheduleInDB=0;
        // int noOfScheduleInReq=0;
        Long noOfScheduleInDB = 0L;
        Long noOfScheduleInReq = 0L;
        if (!BTSLUtil.isNullString(p_requestVO.getMcdNoOfSchedules())) {
            // noOfScheduleInReq=Integer.parseInt(p_requestVO.getMcdNoOfSchedules());
            noOfScheduleInReq = Long.parseLong(p_requestVO.getMcdNoOfSchedules());
        }
        String key = null;
        boolean detailsUpdated = false;
        ResultSet rst = null;
        ArrayList selectorList = new ArrayList();
        ServiceSelectorMappingVO serviceSelectorMappingVO = null;
        selectorList = ServiceSelectorMappingCache
                .getSelectorListForServiceType(PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER);
        int selectorListSize = selectorList.size();
        for (int i = 0; i < selectorListSize; i++) {
            serviceSelectorMappingVO = (ServiceSelectorMappingVO) selectorList.get(i);
            if (serviceSelectorMappingVO.isDefaultCode()) {
                break;
            }
        }
        final String methodName = "updateSMCDListAmountDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: buddyMsisdn= " + mcdListVO.getMsisdn() + " selector_code "
                    + serviceSelectorMappingVO.getSelectorCode() + "parent_id" + mcdListVO.getParentID()
                    + "scheduleType" + p_requestVO.getMcdScheduleType() + " status " + PretupsI.YES);
        }
        try {
            strBuff = new StringBuffer();
            strBuff.append("SELECT NO_OF_SCHEDULE FROM P2P_BATCHES pbt,p2p_buddies pbi  WHERE pbi.BUDDY_MSISDN=?");
            strBuff.append("and pbt.parent_id= pbi.parent_id and pbi.parent_id =? and pbi.status =?");
            strBuff.append("  and pbi.list_name=pbt.LIST_NAME  and pbt.SCHEDULE_TYPE=? ");
            String selectMsisdnQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query selectMsisdnQuery:" + selectMsisdnQuery);
            }
            pstmtSelectMsisdn = (PreparedStatement) p_con.prepareStatement(selectMsisdnQuery);
            pstmtSelectMsisdn.setString(1, mcdListVO.getMsisdn());
            pstmtSelectMsisdn.setString(2, mcdListVO.getParentID());
            pstmtSelectMsisdn.setString(3, PretupsI.YES);
            pstmtSelectMsisdn.setString(4, p_requestVO.getMcdScheduleType());
            rst = pstmtSelectMsisdn.executeQuery();

            if (rst.next()) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Entered: mcdListVO.getMsisdn() " + mcdListVO.getMsisdn() + " p_requestVO "
                            + p_requestVO + " p_amount " + p_amount + "list_name" + p_requestVO.getMcdListName());
                }
                // noOfScheduleInDB=Integer.parseInt(rst.getString("NO_OF_SCHEDULE"));
                noOfScheduleInDB = Long.parseLong(rst.getString("NO_OF_SCHEDULE"));
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "noOfSchInDB=" + noOfScheduleInDB);
                }
                if (BTSLUtil.isNullString(p_requestVO.getMcdNoOfSchedules())) {
                    noOfScheduleInReq = noOfScheduleInDB;
                }
                if (noOfScheduleInReq < noOfScheduleInDB) {
                    String args[] = { String.valueOf(noOfScheduleInDB) };
                    key = PretupsErrorCodesI.P2P_MCDL_ALLOWED_FREQUENCY;
                    p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_MCDL_ALLOWED_FREQUENCY);
                    // throw new
                    // BTSLBaseException("MCDDAO","updateSMCDListAmountDetails",PretupsErrorCodesI.P2P_MCDL_ALLOWED_FREQUENCY,args);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_MCDL_ALLOWED_FREQUENCY, 0,
                            args, null);
                }
                strBuff = new StringBuffer();
                strBuff.append(" update p2p_buddies set PREFERRED_AMOUNT=? , MODIFIED_ON =? , MODIFIED_BY =? ");
                strBuff.append(" where BUDDY_MSISDN= ? and parent_id = ? and list_name = ? and selector_code=? and status =? ");
                String updateQuery = strBuff.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Query updateInsert:" + updateQuery);
                }
                psmtUpdate = (PreparedStatement) p_con.prepareStatement(updateQuery);

                psmtUpdate.setDouble(1, p_amount);
                psmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(mcdListVO.getModifiedOn()));
                psmtUpdate.setString(3, mcdListVO.getModifiedBy());
                psmtUpdate.setString(4, mcdListVO.getMsisdn());
                psmtUpdate.setString(5, mcdListVO.getParentID());
                psmtUpdate.setString(6, p_requestVO.getMcdListName());
                psmtUpdate.setString(7, serviceSelectorMappingVO.getSelectorCode());
                psmtUpdate.setString(8, PretupsI.YES);
                updatedCount = psmtUpdate.executeUpdate();
                if (updatedCount > 0) {
                    detailsUpdated = true;
                    strBuff = new StringBuffer();
                    strBuff.append(" update p2p_batches set NO_OF_SCHEDULE=?,MODIFIED_ON=?,MODIFIED_BY=? ");
                    strBuff.append(" where parent_id = ? and list_name = ? and status =? and schedule_type = ?");
                    String updateBatches = strBuff.toString();
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Query updateBatches:" + updateBatches);
                    }
                    psmtUpdate1 = (PreparedStatement) p_con.prepareStatement(updateBatches);
                    psmtUpdate1.setLong(1, noOfScheduleInReq);
                    psmtUpdate1.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(mcdListVO.getModifiedOn()));
                    psmtUpdate1.setString(3, mcdListVO.getModifiedBy());
                    psmtUpdate1.setString(4, mcdListVO.getParentID());
                    psmtUpdate1.setString(5, p_requestVO.getMcdListName());
                    psmtUpdate1.setString(6, PretupsI.YES);
                    psmtUpdate1.setString(7, p_requestVO.getMcdScheduleType());
                    updatedCount = psmtUpdate1.executeUpdate();
                }
            } else {
                mcdListVO.setReason(" Msisdn Not Found ");

            }

        } catch (BTSLBaseException be) {
            _log.error(methodName, " BTSL Exception while update p2p_batches table:" + be.getMessage());
            throw be;
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[updateMCDListAmountDetailsForSelector]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[updateMCDListAmountDetailsForSelector]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	try {
                if (rst != null) {
                	rst.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        	try {
                if (pstmtSelectMsisdn != null) {
                	pstmtSelectMsisdn.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtUpdate1 != null) {
                    psmtUpdate1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: insertCount=" + updatedCount);
            }
        } // end of finally

        return detailsUpdated;

    }

    /**
     * This method is used to delete individual buddies already present in saved
     * buddylist in Database for Scheduled Credit Transfer .
     * 
     * @param p_con
     * @param p_requestVO
     *            RequestVO
     * @param mcdListVO
     *            MCDListVO
     * @return ChannelUserVO
     * @throws BTSLBaseException
     * @author Harsh.Dixit
     * @date 06-05-2013
     */
    public boolean deleteSMCDListAmountDetails(Connection p_con, RequestVO p_requestVO, MCDListVO mcdListVO)
            throws BTSLBaseException {

        PreparedStatement psmtUpdate = null;
        PreparedStatement pstmtSelectMsisdn = null;
        StringBuffer strBuff = null;
        int deleteCount = 0;

        long listCount = 0;
        boolean deleteBatch = false;
        boolean detailsDelete = false;
        ResultSet rst = null;
        ArrayList selectorList = new ArrayList();
        ServiceSelectorMappingVO serviceSelectorMappingVO = null;
        selectorList = ServiceSelectorMappingCache
                .getSelectorListForServiceType(PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER);
        int selectorListSize = selectorList.size();
        for (int i = 0; i < selectorListSize; i++) {
            serviceSelectorMappingVO = (ServiceSelectorMappingVO) selectorList.get(i);
            if (serviceSelectorMappingVO.isDefaultCode()) {
                break;
            }
        }
        final String methodName = "deleteSMCDListAmountDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: buddyMsisdn= " + mcdListVO.getMsisdn() + " selector_code "
                    + serviceSelectorMappingVO.getSelectorCode() + "parent_id" + mcdListVO.getParentID()
                    + "scheduleType" + p_requestVO.getMcdScheduleType() + " status " + PretupsI.YES);
        }
        try {
            strBuff = new StringBuffer();
            strBuff.append("SELECT count(*) FROM p2p_buddies WHERE BUDDY_MSISDN=?");
            strBuff.append("and parent_id =? and list_name=? and status =?");

            String selectMsisdnQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query selectMsisdnQuery:" + selectMsisdnQuery);
            }
            pstmtSelectMsisdn = (PreparedStatement) p_con.prepareStatement(selectMsisdnQuery);
            pstmtSelectMsisdn.setString(1, mcdListVO.getMsisdn());
            pstmtSelectMsisdn.setString(2, mcdListVO.getParentID());
            pstmtSelectMsisdn.setString(3, mcdListVO.getListName());
            pstmtSelectMsisdn.setString(4, PretupsI.YES);

            rst = pstmtSelectMsisdn.executeQuery();

            if (rst.next()) {

                if (_log.isDebugEnabled()) {
                    _log.debug(methodName,
                            "Entered: buddyMsidn= " + mcdListVO.getMsisdn() + " parent_id" + mcdListVO.getParentID()
                                    + "scheduleType" + p_requestVO.getMcdScheduleType());
                }
                strBuff = new StringBuffer();
                strBuff.append(" delete from  p2p_buddies ");
                strBuff.append(" where buddy_msisdn= ? and parent_id= ? and selector_code= ? and status =?  ");
                strBuff.append(" and list_name in  ");
                strBuff.append("(select list_name from p2p_batches where schedule_type=? and list_name=? )");
                String deleteQuery = strBuff.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Query deleteQuery:" + deleteQuery);
                }

                psmtUpdate = (PreparedStatement) p_con.prepareStatement(deleteQuery);

                psmtUpdate.setString(1, mcdListVO.getMsisdn());
                psmtUpdate.setString(2, mcdListVO.getParentID());
                psmtUpdate.setString(3, serviceSelectorMappingVO.getSelectorCode());
                psmtUpdate.setString(4, PretupsI.YES);
                psmtUpdate.setString(5, p_requestVO.getMcdScheduleType());
                psmtUpdate.setString(6, mcdListVO.getListName());

                deleteCount = psmtUpdate.executeUpdate();
                if (deleteCount > 0) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "deleteCount=" + deleteCount);
                    }
                    detailsDelete = true;
                    listCount = this.getTotalBuddyInList(p_con, mcdListVO.getListName(), mcdListVO.getParentID(),
                            p_requestVO.getMcdScheduleType());
                    if (listCount == 0) {
                        deleteBatch = this.deleteSMCDList(p_con, mcdListVO.getListName(), mcdListVO.getParentID());
                    }
                    if (deleteBatch) {
                        _log.debug("Blank List Removed ::", " parent_id" + mcdListVO.getParentID() + "listName"
                                + mcdListVO.getListName());
                    }
                }

            } else {
                mcdListVO.setReason(" Msisdn Not Found ");

            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());

            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[updateMCDListAmountDetailsForSelector]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[updateMCDListAmountDetailsForSelector]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {

            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectMsisdn != null) {
                	pstmtSelectMsisdn.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: deleteCount=" + deleteCount);
            }
        } // end of finally

        return detailsDelete;

    }

    /**
     * This method is used to check uniqueness of buddy list in Database for
     * Schedulder Credit Transfer .
     * 
     * @param p_con
     * @param p_listName
     *            String
     * @param p_userID
     *            String
     * @parm schType String
     * @return listCount int
     * @throws BTSLBaseException
     * @author Harsh.Dixit
     * @date 06-05-2013
     */
    public long isListAlreadyRegistered(Connection p_con, String p_listName, String p_userID, String schType)
            throws BTSLBaseException {

        final String methodName = "isListAlreadyRegistered";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_listName " + p_listName, "p_userID : " + p_userID + "p_scheduleType :"
                    + schType);
        }

        int listCount = 0;
        String listNameDB = null;
        PreparedStatement pstm = null;
        PreparedStatement pstm1 = null;
        ResultSet rst = null;
        ResultSet rst1 = null;
        try {
            String query = " select pba.list_name from p2p_batches pba where pba.parent_id=? and pba.status=? and pba.schedule_type=? and  pba.parent_id in (select pbu.parent_id from p2p_buddies pbu where pbu.status = ? ) ";
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query: " + query);
            }
            // String selectCount
            // ="select  count(pbu.buddy_msisdn) count from p2p_buddies pbu WHERE pbu.parent_id=? and pbu.list_name = ? and pbu.parent_id in ( select pba.parent_id from p2p_batches pba where pba.status = ? and pba.SCHEDULE_TYPE = ?) ";
            String selectCount = "select  count(pbu.buddy_msisdn) count from p2p_buddies pbu WHERE pbu.parent_id=? and pbu.status = ?";
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "selectCount: " + selectCount);
            }
            pstm = p_con.prepareStatement(query);
            pstm.setString(1, p_userID);
            pstm.setString(2, PretupsI.YES);
            pstm.setString(3, schType);
            pstm.setString(4, PretupsI.YES);

            rst = pstm.executeQuery();

            if (rst.next()) {
                listNameDB = rst.getString("list_name");
                System.out.println("listNameDB ::" + listNameDB + " p_listName ::" + p_listName);
                if (p_listName.equals(listNameDB)) {
                    pstm1 = p_con.prepareStatement(selectCount);
                    pstm1.setString(1, p_userID);
                    pstm1.setString(2, PretupsI.YES);

                    rst1 = pstm1.executeQuery();
                    if (rst1.next()) {
                        listCount = rst1.getInt("count");
                    }
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_MULT_CDT_LIST_AMD_ALREADYREGD);
                }
            }
        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException : " + be);
            _log.errorTrace(methodName, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[isListAlreadyRegistered]", "", "", "", "BTSLBaseException:" + be.getMessage());
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[isListAlreadyRegistered]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isCardNumberAlreadyRegistered", PretupsErrorCodesI.ERROR_EXCEPTION);
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[isListAlreadyRegistered]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
        	 try {
                 if (rst != null) {
                     rst.close();
                 }
             } catch (Exception e) {
                 _log.errorTrace(methodName, e);
             }
        	 try {
                 if (rst1 != null) {
                     rst1.close();
                 }
             } catch (Exception e) {
                 _log.errorTrace(methodName, e);
             }
            try {
                if (pstm != null) {
                    pstm.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstm1 != null) {
                    pstm1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting listCount " + listCount);
            }
        }
        return listCount;

    }

    /**
     * This method is used to calculate cumulative amount stored in Database
     * against a particular buddy list for Schedulder Credit Transfer .
     * 
     * @param p_con
     *            Connection
     * @param p_listName
     *            String
     * @param p_userID
     *            String
     * @return totalAmount long
     * @throws BTSLBaseException
     * @author Harsh.Dixit
     * @date 06-05-2013
     */
    public long getTotalAmountInBuddyList(Connection p_con, String p_userID, String p_listName, String schType) {
        final String methodName = "getTotalAmountInBuddyList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_listName " + p_listName, "parent_id : " + p_userID + " scheduleType :: "
                    + schType);
        }

        long totalAmount = 0;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        try {
            String query = " SELECT sum(pbu.PREFERRED_AMOUNT) Total_Amount FROM p2p_buddies pbu WHERE pbu.parent_id= ? and  pbu.status= ?   ";
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query: " + query);
            }
            pstm = p_con.prepareStatement(query);
            pstm.setString(1, p_userID);
            pstm.setString(2, PretupsI.YES);

            rst = pstm.executeQuery();
            if (rst.next()) {
                totalAmount = rst.getLong("Total_Amount");
            }
        }

        catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[getTotalAmountInBuddyList]", "", "", "", "SQL Exception:" + sqe.getMessage());
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[getTotalAmountInBuddyList]", "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting total Amount " + totalAmount);
            }
        }
        return totalAmount;
    }

    // ///////////////////////////added by pradyumn
    // ///////////////////////for list view
    public ArrayList loadScheduleBuddyList(Connection p_con, String p_userID, String p_listName, String p_sctype)
            throws BTSLBaseException {
        final String methodName = "loadScheduleBuddyList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered   p_userID " + p_userID + " p_listName " + p_listName + "p_sctype"
                    + p_sctype);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList arrayList = null;
        MCDListVO mcdListVO = null;
        StringBuffer strBuff = new StringBuffer(
                "SELECT BUDDY_MSISDN,PREFERRED_AMOUNT from p2p_buddies p , p2p_batches m ");
        strBuff.append(" where p.parent_id=m.parent_id  and m.PARENT_ID=?  and m.STATUS=? ");
        if (!BTSLUtil.isNullString(p_listName)) {
            strBuff.append(" and m.list_name=? and  p.list_name=m.list_name ");
        }
        if (!BTSLUtil.isNullString(p_sctype)) {
            strBuff.append(" and m.SCHEDULE_TYPE= ? ");
        }

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            arrayList = new ArrayList();
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_userID);
            pstmt.setString(2, PretupsI.YES);
            if (!BTSLUtil.isNullString(p_listName) && !BTSLUtil.isNullString(p_sctype)) {
                pstmt.setString(3, p_listName);
                pstmt.setString(4, p_sctype);
            } else if (!BTSLUtil.isNullString(p_listName)) {
                pstmt.setString(3, p_listName);
            } else if (!BTSLUtil.isNullString(p_sctype)) {
                pstmt.setString(3, p_sctype);
            }
            rs = pstmt.executeQuery();

            while (rs.next()) {
                mcdListVO = new MCDListVO();
                mcdListVO.setMsisdn(rs.getString("BUDDY_MSISDN"));
                mcdListVO.setAmount1(rs.getLong("PREFERRED_AMOUNT"));
                arrayList.add(mcdListVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[loadBuddySelectorAmountList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[loadBuddySelectorAmountList]", "", "", "", "Exception:" + ex.getMessage());
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

    public void checkScheduleListStatus(Connection p_con, RequestVO p_requestVO, String p_userID, String p_listName,
            String p_sctype) throws BTSLBaseException {

        final String methodName = "checkScheduleListStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        PreparedStatement pstmtSelect = null;
        Date nextScheduleDate = null;
        DateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
        ResultSet rs = null;
        // int noOfScheduleInReq=0;
        long noOfScheduleInReq = 0L;
        try {
            StringBuffer selectQueryBuff = new StringBuffer("SELECT list_name,schedule_type,no_of_schedule");

            selectQueryBuff.append(" ,execution_upto, schedule_date ");

            selectQueryBuff.append(" FROM P2P_BATCHES WHERE parent_id=? AND status=?");
            if (!BTSLUtil.isNullString(p_listName)) {
                selectQueryBuff.append(" AND list_name=? ");
            }
            if (!BTSLUtil.isNullString(p_sctype)) {
                selectQueryBuff.append(" AND schedule_type=? ");
            }
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query SQLSelect=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_userID);
            pstmtSelect.setString(2, PretupsI.YES);
            if (!BTSLUtil.isNullString(p_listName) && !BTSLUtil.isNullString(p_sctype)) {
                pstmtSelect.setString(3, p_listName);
                pstmtSelect.setString(4, p_sctype);
            } else if (!BTSLUtil.isNullString(p_listName)) {
                pstmtSelect.setString(3, p_listName);
            } else if (!BTSLUtil.isNullString(p_sctype)) {
                pstmtSelect.setString(3, p_sctype);
            }
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {

                if (!(BTSLUtil.isNullString(p_listName) && BTSLUtil.isNullString(p_sctype))) {
                    p_requestVO.setMcdListName(rs.getString("list_name"));
                    // noOfScheduleInReq=rs.getInt("no_of_schedule");
                    noOfScheduleInReq = rs.getLong("no_of_schedule");
                    p_requestVO.setMcdNoOfSchedules(String.valueOf(noOfScheduleInReq));
                    p_requestVO.setMcdScheduleType(rs.getString("schedule_type"));
                }
                if (!(rs.getDate("schedule_date") == null)) {
                    nextScheduleDate = rs.getDate("schedule_date");
                }
                String nxSchdate = sdf.format(nextScheduleDate).toString();
                p_requestVO.setExecutedUpto(sdf.parse(nxSchdate));
            }
            //
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[checkScheduleListStatus]", "", p_userID, "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[checkScheduleListStatus]", "", p_userID, "", "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting ");
            }
        }// end of finally
    }

    // ///////////////////////////////////////////////////////////////////////////////load
    // scheduled list details
    // ///delete list from batches

    public boolean deleteSMCDList(Connection p_con, String p_list, String p_userID) throws BTSLBaseException {
        PreparedStatement psmtUpdate = null;
        int deleteCount = 0;
        boolean listdeleted = false;
        final String methodName = "deleteSMCDList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_list" + p_list);
        }
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("delete from p2p_batches ");
            strBuff.append("where parent_id=? and list_name=? ");
            String deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query updateInsert:" + deleteQuery);
            }
            psmtUpdate = (PreparedStatement) p_con.prepareStatement(deleteQuery);
            psmtUpdate.setString(1, p_userID);
            psmtUpdate.setString(2, p_list);

            deleteCount = psmtUpdate.executeUpdate();
            if (deleteCount > 0) {
                listdeleted = true;
            }

        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[deleteSMCDList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("deleteMCDList", "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[deleteSMCDList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: deleteCount=" + deleteCount);
            }
        } // end of finally

        return listdeleted;
    }

    /**
     * This method is used to get Next Schedule Date for credit transfer after
     * saving buddy list
     * 
     * @param p_userId
     *            String
     * @param p_listName
     *            String
     * @param schType
     *            String
     * @return nextScheduleDate String
     * @author Harsh.Dixit
     * @date 06-08-2013
     */
    public String getNextScheduleDateAsString(String p_userId, String p_listName, String schType) {
        final String methodName = "getNextScheduleDate";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered   p_userID " + p_userId + " p_listName " + p_listName + " scheduleType "
                    + schType);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        PreparedStatement pstmtSelect = null;
        SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
        Calendar calendar = BTSLDateUtil.getInstance();
        ResultSet rs = null;
        Date nextScheduleDate = null;
        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            StringBuffer selectQueryBuff = new StringBuffer("SELECT SCHEDULE_DATE FROM p2p_batches  ");
            selectQueryBuff.append(" WHERE  parent_id=? and list_name=? and status = ? and SCHEDULE_TYPE = ? ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect=" + selectQuery);
            }
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_userId);
            pstmtSelect.setString(2, p_listName);
            pstmtSelect.setString(3, PretupsI.YES);
            pstmtSelect.setString(4, schType);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                if (!(rs.getDate("SCHEDULE_DATE") == null)) {
                    nextScheduleDate = rs.getDate("SCHEDULE_DATE");
                    calendar.setTime(nextScheduleDate);
                }
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
        }// end of catch
        catch (Exception e) {
            _log.error("process", "Exception e:" + e.getMessage());
            _log.errorTrace(methodName, e);
        } finally {
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
            if (mcomCon != null) {
                mcomCon.close("MCDTxnDAO#getNextScheduleDateAsString");
                mcomCon = null;
            }
        }
        return sdf.format(nextScheduleDate).toString();
    }

    /**
     * This method is used to get Next Schedule Date for credit transfer after
     * saving buddy list
     * 
     * @param schType
     *            String
     * @return nextScheduleDate Date
     * @author Harsh.Dixit
     * @date 06-08-2013
     */
    public Date getNextScheduleDate(String schType) {
        Calendar calendar = BTSLDateUtil.getInstance();
        Date nextScheduleDate = null;
        System.out.println("current date & time " + calendar.getTime());
        if (schType.equals(PretupsI.SCHEDULE_TYPE_DAILY_FILTER)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            nextScheduleDate = calendar.getTime();
        }
        if (schType.equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER)) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            nextScheduleDate = calendar.getTime();
        }
        if (schType.equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER)) {
            calendar.add(Calendar.MONTH, 1);
            nextScheduleDate = calendar.getTime();
        }
        return nextScheduleDate;
    }

    public int getTotalBuddyInList(Connection p_con, String p_listName, String p_userID, String schType) {
        final String methodName = "getTotalBuddyInList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_listName " + p_listName, "p_userID : " + p_userID + "p_scheduleType :"
                    + schType);
        }

        int listCount = 0;
        PreparedStatement pstm = null;
        ResultSet rst = null;

        try {
            StringBuffer selectCount = new StringBuffer();
            selectCount
                    .append("select  count(pbu.buddy_msisdn) count from p2p_buddies pbu WHERE pbu.parent_id=? and pbu.list_name=? ");
            selectCount
                    .append("and pbu.parent_id in (select pba.parent_id from p2p_batches pba where pba.schedule_type=? and pba.status=?  )");
            String selectCountQuery = selectCount.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "selectCount: " + selectCountQuery);
            }
            pstm = (PreparedStatement) p_con.prepareStatement(selectCountQuery);
            pstm.setString(1, p_userID);
            pstm.setString(2, p_listName);
            pstm.setString(3, schType);
            pstm.setString(4, PretupsI.YES);

            rst = pstm.executeQuery();

            if (rst.next()) {
                listCount = rst.getInt("count");
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[getTotalBuddyInList]", "", "", "", "SQL Exception:" + sqe.getMessage());
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "MCDDAO[getTotalBuddyInList]", "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting listCount " + listCount);
            }
        }
        return listCount;
    }

    /**
     * This method is used to get Next Schedule Date for credit transfer after
     * saving buddy list
     * 
     * @param schType
     *            String
     * @return nextScheduleDate Date
     * @author Harsh.Dixit
     * @date 06-08-2013
     */
    public String getNextScheduleDate(Date schDate, String schType) {
        Calendar calendar = BTSLDateUtil.getInstance();
        calendar.setTime(schDate);
        Date nextScheduleDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
        if (schType.equals(PretupsI.SCHEDULE_TYPE_DAILY_FILTER)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            nextScheduleDate = calendar.getTime();
        }
        if (schType.equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER)) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            nextScheduleDate = calendar.getTime();
        }
        if (schType.equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER)) {
            calendar.add(Calendar.MONTH, 1);
            nextScheduleDate = calendar.getTime();
        }
        return sdf.format(nextScheduleDate).toString();
    }
}
