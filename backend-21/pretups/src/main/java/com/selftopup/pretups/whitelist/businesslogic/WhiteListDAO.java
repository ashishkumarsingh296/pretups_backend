/**
 * @# WhiteListDAO
 *    This class is the controller class of the Channel user Module.
 * 
 *    Created on Created by History
 *    --------------------------------------------------------------------------
 *    ------
 *    March 28, 2006 Ankit Zindal Initial creation
 *    May 16,2006 Ashish Kumar Modification
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.selftopup.pretups.whitelist.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.ListValueVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;

public class WhiteListDAO {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method loadInterfaceDetails.
     * This method is to get the interface id from white list on the basis of
     * the subscriber msisdn.
     * 
     * @param p_con
     *            Connection
     * @param p_Msisdn
     *            String
     * 
     * @return WhiteListVO
     * @throws BTSLBaseException
     */
    public WhiteListVO loadInterfaceDetails(Connection p_con, String p_Msisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadInterfaceDetails", "Entered p_Msisdn:" + p_Msisdn);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        WhiteListVO whiteListVO = null;
        ListValueVO listValueVO = null;
        StringBuffer strBuff = new StringBuffer("SELECT WL.interface_id ,I.external_id, I.status,I.message_language1, I.message_language2, IT.handler_class,IT.underprocess_msg_reqd,SC.service_class_id,I.status_type statustype, ");
        strBuff.append(" WL.account_id,WL.account_status,WL.service_class,WL.credit_limit,WL.network_code,WL.msisdn,WL.entry_date,WL.external_interface_code,WL.created_on,WL.created_by,WL.status,WL.imsi,WL.language,WL.country,I.single_state_transaction ");
        strBuff.append(" FROM white_list WL,interfaces I,interface_types IT ,service_classes SC ");
        strBuff.append(" WHERE WL.msisdn = ? AND  WL.status='Y' AND I.status<>'N' ");
        strBuff.append(" AND I.interface_id=WL.interface_id AND I.interface_type_id=IT.interface_type_id ");
        strBuff.append(" AND I.interface_id=SC.interface_id(+) AND SC.service_class_code(+)=? AND SC.STATUS(+)<>'N' ");
        try {
            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceDetails", "Select Query= " + strBuff.toString());
            pstmt = p_con.prepareStatement(strBuff.toString());
            pstmt.setString(1, p_Msisdn);
            pstmt.setString(2, PretupsI.ALL);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("handler_class"), rs.getString("interface_id"));
                listValueVO.setType(rs.getString("underprocess_msg_reqd"));
                listValueVO.setTypeName(rs.getString("service_class_id"));
                listValueVO.setIDValue(rs.getString("external_id"));
                listValueVO.setStatus(rs.getString("status"));
                listValueVO.setStatusType(rs.getString("statustype"));
                listValueVO.setOtherInfo(rs.getString("message_language1"));
                listValueVO.setOtherInfo2(rs.getString("message_language2"));
                listValueVO.setSingleStep(rs.getString("single_state_transaction"));
                whiteListVO = new WhiteListVO();
                whiteListVO.setAccountID(rs.getString("account_id"));
                whiteListVO.setAccountStatus(rs.getString("account_status"));
                whiteListVO.setServiceClassCode(rs.getString("service_class"));
                whiteListVO.setCreditLimit(rs.getLong("credit_limit"));
                whiteListVO.setNetworkCode(rs.getString("network_code"));
                whiteListVO.setMsisdn(rs.getString("msisdn"));
                whiteListVO.setEntryDate(rs.getDate("entry_date"));
                whiteListVO.setExternalInterfaceCode(rs.getString("external_interface_code"));
                whiteListVO.setCreatedBy(rs.getString("created_by"));
                whiteListVO.setCreatedOn(rs.getDate("created_on"));
                whiteListVO.setStatus(rs.getString("status"));
                whiteListVO.setImsi(rs.getString("imsi"));
                whiteListVO.setLanguage(rs.getString("language"));
                whiteListVO.setCountry(rs.getString("country"));
                whiteListVO.setListValueVO(listValueVO);
            }

        }// end of try
        catch (SQLException sqe) {
            _log.error("loadInterfaceDetails", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[loadInterfaceDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceDetails", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadInterfaceDetails", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[loadInterfaceDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceDetails", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceDetails", "Exiting: whiteListVO=" + whiteListVO);
        }// end of finally.
        return whiteListVO;
    }

    /**
     * Method loadWhiteListSubsDetails
     * Method for Extracting Postpaid Subscriber Details.
     * 
     * @author Amit Ruwali
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @return whiteListVO WhiteListVO
     * @throws BTSLBaseException
     */

    public WhiteListVO loadWhiteListSubsDetails(Connection p_con, String p_msisdn) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("loadWhiteListSubsDetails", "Entered p_msisdn=" + p_msisdn);

        WhiteListVO whiteListVO = null;
        PreparedStatement pstmtSubList = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer("SELECT W.network_code,W.msisdn,W.account_id,W.entry_date,");
        strBuff.append("W.account_status,W.service_class,W.movement_code,W.imsi,LM.name AS LANG,");
        strBuff.append("SC.service_class_name,W.credit_limit,W.interface_id,IT.interface_name,W.external_interface_code,L1.lookup_name AS WLSTATUS,nvl(U.user_name,W.activated_by) AS UN,");
        strBuff.append("W.activated_on,W.activated_by,W.created_on,W.created_by,W.status,W.language,W.country,L.lookup_name,SC.service_class_name");
        strBuff.append(" FROM white_list W,interfaces I,interface_types IT,lookups L,lookups L1,service_classes SC,locale_master LM,USERS U ");
        strBuff.append(" WHERE L.lookup_type(+)=? AND L1.lookup_type(+)=? AND  SC.service_class_code(+)=W.service_class AND");
        strBuff.append(" SC.interface_id(+)=W.interface_id AND I.interface_id(+)=W.interface_id ");
        strBuff.append(" AND I.interface_type_id=IT.interface_type_id(+) AND W.msisdn =? AND U.user_id(+)=W.activated_by ");
        strBuff.append(" AND W.movement_code = L.lookup_code(+) AND W.status = L1.lookup_code(+)");
        strBuff.append(" AND LM.language(+)=W.language ORDER BY W.account_id");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadWhiteListSubsDetails", "QUERY sqlSelect=" + sqlSelect);
        try {
            pstmtSubList = p_con.prepareStatement(sqlSelect);
            pstmtSubList.setString(1, PretupsI.WHITE_LIST_MOVEMENT_CODE);
            pstmtSubList.setString(2, PretupsI.WHITE_LIST_STATUS);
            pstmtSubList.setString(3, p_msisdn);
            rs = pstmtSubList.executeQuery();
            if (rs.next()) {
                whiteListVO = new WhiteListVO();
                whiteListVO.setMsisdn(rs.getString("msisdn"));
                whiteListVO.setAccountID(rs.getString("account_id"));
                whiteListVO.setInterfaceID(rs.getString("interface_id"));
                whiteListVO.setInterfaceName(rs.getString("interface_name"));
                whiteListVO.setEntryDate(rs.getDate("entry_date"));
                whiteListVO.setEntryDateStr(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("entry_date"))));
                whiteListVO.setAccountStatus(rs.getString("account_status"));
                whiteListVO.setServiceClassCode(rs.getString("service_class"));
                whiteListVO.setServiceClassName(rs.getString("service_class_name"));
                whiteListVO.setCreditLimit(rs.getLong("credit_limit"));
                whiteListVO.setCreditLimitStr(String.valueOf(PretupsBL.getDisplayAmount((rs.getLong("credit_limit")))));
                whiteListVO.setStatus(rs.getString("status"));
                whiteListVO.setStatusStr(rs.getString("WLSTATUS"));
                whiteListVO.setLanguage(rs.getString("LANG"));
                whiteListVO.setCountry(rs.getString("country"));
                whiteListVO.setMovementCode(rs.getString("movement_code"));
                whiteListVO.setMovementName(rs.getString("lookup_name"));
                whiteListVO.setActivatedOn(rs.getDate("activated_on"));
                whiteListVO.setActivatedOnStr(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("activated_on")));
                // whiteListVO.setActivatedBy(rs.getString("activated_by"));
                whiteListVO.setActivatedBy(rs.getString("UN"));
                whiteListVO.setImsi(rs.getString("imsi"));
                whiteListVO.setNetworkCode(rs.getString("network_code"));
            }
        } catch (SQLException sqe) {
            _log.error("loadWhiteListSubsDetails", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[loadWhiteListSubsDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadWhiteListSubsDetails", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadWhiteListSubsDetails", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[loadWhiteListSubsDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadWhiteListSubsDetails", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmtSubList != null) {
                    pstmtSubList.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadWhiteListSubsDetails", "Exiting whiteListVO=" + whiteListVO);
        }
        return whiteListVO;
    }

    /**
     * This method is used to insert white list subscriber detail in batch.
     * 
     * @param Connection
     *            p_con
     * @param ArrayList
     *            p_insertList
     * @return int
     * @throws BTSLBaseException
     */
    public int batchInsertWhiteListSubsDetail(Connection p_con, ArrayList p_insertList) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("batchInsertWhiteListSubsDetail", "Entered p_insertList.size()=" + p_insertList.size());
        int insertCount = 0;
        WhiteListVO whiteListVO = null;
        PreparedStatement pstmtSubList = null;
        int[] batchInCt;
        int k = 0;
        StringBuffer strBuff = new StringBuffer(" INSERT INTO white_list VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        if (_log.isDebugEnabled())
            _log.debug("batchInsertWhiteListSubsDetail", "SQL INSERT QUERY strBuff.toString()=" + strBuff.toString());
        try {
            pstmtSubList = p_con.prepareStatement(strBuff.toString());
            for (int i = 0, size = p_insertList.size(); i < size; i++) {
                k = 0;
                whiteListVO = (WhiteListVO) p_insertList.get(i);
                pstmtSubList.setString(++k, whiteListVO.getNetworkCode());
                pstmtSubList.setString(++k, whiteListVO.getMsisdn());
                pstmtSubList.setString(++k, whiteListVO.getAccountID());
                pstmtSubList.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(whiteListVO.getEntryDate()));
                pstmtSubList.setString(++k, whiteListVO.getAccountStatus());
                pstmtSubList.setString(++k, whiteListVO.getServiceClassCode());
                pstmtSubList.setString(++k, whiteListVO.getCreditLimitStr());
                pstmtSubList.setString(++k, whiteListVO.getInterfaceID());
                pstmtSubList.setString(++k, whiteListVO.getExternalInterfaceCode());
                pstmtSubList.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(whiteListVO.getCreatedOn()));
                pstmtSubList.setString(++k, whiteListVO.getCreatedBy());
                pstmtSubList.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(whiteListVO.getModifiedOn()));
                pstmtSubList.setString(++k, whiteListVO.getModifiedBy());
                pstmtSubList.setString(++k, whiteListVO.getStatus());
                pstmtSubList.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(whiteListVO.getActivatedOn()));
                pstmtSubList.setString(++k, whiteListVO.getActivatedBy());
                pstmtSubList.setString(++k, whiteListVO.getMovementCode());
                pstmtSubList.setString(++k, whiteListVO.getLanguage());
                pstmtSubList.setString(++k, whiteListVO.getCountry());
                pstmtSubList.setString(++k, whiteListVO.getImsi());
                pstmtSubList.addBatch();
                whiteListVO = null;// Setting whiteListVO is null to suggest JVM
                                   // for GC.
            }
            batchInCt = pstmtSubList.executeBatch();
            pstmtSubList.clearBatch();
            insertCount = batchInCt.length;
        } catch (SQLException sqe) {
            _log.error("batchInsertWhiteListSubsDetail", "SQLException : " + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[batchInsertWhiteListSubsDetail]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "batchInsertWhiteListSubsDetail", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("batchInsertWhiteListSubsDetail", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[batchInsertWhiteListSubsDetail]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "batchInsertWhiteListSubsDetail", "error.general.processing");
        } finally {
            try {
                if (pstmtSubList != null) {
                    pstmtSubList.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("batchInsertWhiteListSubsDetail", "Exiting insertCount=" + insertCount);
        }
        return insertCount;
    }

    /**
     * This method is used to delete all records in the white list in batch.
     * 
     * @param Connection
     *            p_con
     * @param String
     *            p_netWorkCode
     * @param String
     *            p_interfaceID
     * @return int
     * @throws BTSLBaseException
     */
    public int batchDeleteWhiteListSubsDetail(Connection p_con, String p_netWorkCode, String p_interfaceID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("batchDeleteWhiteListSubsDetail", "Entered p_netWorkCode = " + p_netWorkCode + " p_interfaceID = " + p_interfaceID);
        int deleteCount = 0;
        PreparedStatement pstmtSubList = null;
        String deleteQuery = new String("DELETE FROM white_list WHERE NETWORK_CODE=? AND INTERFACE_ID=? ");
        if (_log.isDebugEnabled())
            _log.debug("batchDeleteWhiteListSubsDetail", "deleteQuery = " + deleteQuery);
        try {
            pstmtSubList = p_con.prepareStatement(deleteQuery);
            pstmtSubList.setString(1, p_netWorkCode);
            pstmtSubList.setString(2, p_interfaceID);
            deleteCount = pstmtSubList.executeUpdate();
            if (deleteCount == 0)
                if (_log.isDebugEnabled())
                    _log.debug("batchDeleteWhiteListSubsDetail", "No records are found for delete");
        } catch (SQLException sqe) {
            _log.error("batchDeleteWhiteListSubsDetail", "SQLException : " + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[batchDeleteWhiteListSubsDetail]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "batchDeleteWhiteListSubsDetail", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("batchDeleteWhiteListSubsDetail", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[batchDeleteWhiteListSubsDetail]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "batchDeleteWhiteListSubsDetail", "error.general.processing");
        } finally {
            try {
                if (pstmtSubList != null) {
                    pstmtSubList.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("batchDeleteWhiteListSubsDetail", "Exiting deleteCount=" + deleteCount);
        }
        return deleteCount;
    }

    /**
     * This method is used to delete all records in the white list in batch.
     * 
     * @param Connection
     *            p_con
     * @param ArrayList
     *            p_deleteList
     * @return int
     * @throws BTSLBaseException
     */
    public int deleteWhiteListSubsDetail(Connection p_con, ArrayList p_deleteList) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("deleteWhiteListSubsDetail", "Entered p_deleteList.size() = " + p_deleteList.size());
        int deleteCount = 0;
        int tempDelCount = 0;
        WhiteListVO whiteListVO = null;
        PreparedStatement pstmtSubList = null;
        String deleteQuery = new String("DELETE FROM white_list WHERE network_code=? AND interface_id=? AND msisdn=?");
        if (_log.isDebugEnabled())
            _log.debug("deleteWhiteListSubsDetail", "deleteQuery = " + deleteQuery);
        try {
            pstmtSubList = p_con.prepareStatement(deleteQuery);
            for (int i = 0, size = p_deleteList.size(); i < size; i++) {
                whiteListVO = (WhiteListVO) p_deleteList.get(i);
                pstmtSubList.setString(1, whiteListVO.getNetworkCode());
                pstmtSubList.setString(2, whiteListVO.getInterfaceID());
                pstmtSubList.setString(3, whiteListVO.getMsisdn());
                tempDelCount = pstmtSubList.executeUpdate();

                // Incase when number is not found for deletion,information will
                // be logged in error mode- discussed with Sanjay Sir
                if (tempDelCount == 0)
                    _log.error("batchDeleteWhiteListSubsDetail", "Records with MSISDN = " + whiteListVO.getMsisdn() + "is not found for delete");
                pstmtSubList.clearParameters();
                deleteCount += tempDelCount;
                whiteListVO = null;// Setting whiteListVO is null to suggest JVM
                                   // for GC.
            }
        } catch (SQLException sqe) {
            _log.error("deleteWhiteListSubsDetail", "SQLException : " + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[deleteWhiteListSubsDetail]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "deleteWhiteListSubsDetail", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("deleteWhiteListSubsDetail", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[deleteWhiteListSubsDetail]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "deleteWhiteListSubsDetail", "error.general.processing");
        } finally {
            try {
                if (pstmtSubList != null) {
                    pstmtSubList.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteWhiteListSubsDetail", "Exiting deleteCount=" + deleteCount);
        }
        return deleteCount;
    }

    /**
     * This method is used to update the whiteList information.While updating
     * this method checks following
     * 1.If the record that is to be updated not found then Insert that record.
     * 
     * @param Connection
     *            p_con
     * @param ArrayList
     *            p_updateList
     * @throws BTSLBaseException
     */
    public int updateWhiteListSubsDetail(Connection p_con, ArrayList p_updateList) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateWhiteListSubsDetail", "Entered p_updateList.size()= " + p_updateList.size());
        int updateCount = 0;
        int insertCt = 0;
        int tempUpdateCount = 0;
        int k = 0;
        ArrayList tempInsertList = new ArrayList();
        WhiteListVO whiteListVO = null;
        PreparedStatement pstmtUpdate = null;
        StringBuffer strBuff = new StringBuffer(" UPDATE white_list SET network_code=?, interface_id=?, account_status=?,credit_limit=?,imsi=?,account_id=?, ");
        strBuff.append("service_class=?,movement_code=?,modified_on=?,modified_by=?,activated_on=?, ");
        strBuff.append("activated_by=?,language=?,country=?  WHERE msisdn=? ");
        if (_log.isDebugEnabled())
            _log.debug("updateWhiteListSubsDetail", "UpdateQuery strBuff.toString() = " + strBuff.toString());
        try {
            pstmtUpdate = p_con.prepareStatement(strBuff.toString());
            for (int i = 0, size = p_updateList.size(); i < size; i++) {
                k = 0;
                tempUpdateCount = 0;
                whiteListVO = (WhiteListVO) p_updateList.get(i);
                pstmtUpdate.setString(++k, whiteListVO.getNetworkCode());
                pstmtUpdate.setString(++k, whiteListVO.getInterfaceID());
                pstmtUpdate.setString(++k, whiteListVO.getAccountStatus());
                pstmtUpdate.setString(++k, whiteListVO.getCreditLimitStr());
                pstmtUpdate.setString(++k, whiteListVO.getImsi());
                pstmtUpdate.setString(++k, whiteListVO.getAccountID());
                pstmtUpdate.setString(++k, whiteListVO.getServiceClassCode());
                pstmtUpdate.setString(++k, whiteListVO.getMovementCode());
                pstmtUpdate.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(whiteListVO.getModifiedOn()));
                pstmtUpdate.setString(++k, whiteListVO.getModifiedBy());
                pstmtUpdate.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(whiteListVO.getActivatedOn()));
                pstmtUpdate.setString(++k, whiteListVO.getActivatedBy());
                pstmtUpdate.setString(++k, whiteListVO.getLanguage());
                pstmtUpdate.setString(++k, whiteListVO.getCountry());
                pstmtUpdate.setString(++k, whiteListVO.getMsisdn());
                tempUpdateCount = pstmtUpdate.executeUpdate();
                pstmtUpdate.clearParameters();
                if (tempUpdateCount == 0) {
                    // Incase when number is not found for update, record will
                    // be inserted and information will be logged in error mode-
                    // discussed with Sanjay Sir
                    _log.error("updateWhiteListSubsDetail", "RECORD WITH MSISDN = " + whiteListVO.getMsisdn() + " NOT FOUND FOR UPDATE");
                    tempInsertList.add(whiteListVO);
                } else
                    whiteListVO = null;
                updateCount += tempUpdateCount;
            }
            try {
                if (tempInsertList.size() > 0)
                    insertCt = insertIndWhiteListDetail(p_con, tempInsertList);
            } catch (BTSLBaseException be) {
                _log.error("updateWhiteListSubsDetail", "BTSLBaseException", be.getMessage());
                throw be;
            }
        } catch (SQLException sqe) {
            _log.error("updateWhiteListSubsDetail", "SQLException : " + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[updateWhiteListSubsDetail]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "updateWhiteListSubsDetail", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("updateWhiteListSubsDetail", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[updateWhiteListSubsDetail]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "updateWhiteListSubsDetail", "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateWhiteListSubsDetail", "Exiting updateCount =" + updateCount + " insertCt = " + insertCt);
        }
        return updateCount + insertCt;
    }

    /**
     * This method is used to insert the white list information. While inserting
     * it cheks the following
     * 1.If the record that is to be inserted,already present, update that
     * record.
     * 
     * @param Connection
     *            p_con
     * @param ArrayList
     *            p_insertList
     * @return int
     * @throws BTSLBaseException
     */
    public int insertIndWhiteListDetail(Connection p_con, ArrayList p_insertList) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("insertIndWhiteListDetail", "Entered p_insertList.size()=" + p_insertList.size());
        int insertCount = 0;
        int updateCt = 0;
        int tempInsertCount = 0;
        int k = 0;
        WhiteListVO whiteListVO = null;
        PreparedStatement pstmtSubList = null;
        ArrayList tempUpdateList = new ArrayList();
        StringBuffer strBuff = new StringBuffer(" INSERT INTO white_list ( network_code, msisdn, account_id, entry_date, ");
        strBuff.append("account_status, service_class, credit_limit, interface_id, external_interface_code, created_on, created_by, ");
        strBuff.append("modified_on, modified_by, status, activated_on, activated_by, movement_code, language, country, imsi ) ");
        strBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        if (_log.isDebugEnabled())
            _log.debug("insertIndWhiteListDetail", "SQL INSERT QUERY strBuff.toString()=" + strBuff.toString());
        try {
            pstmtSubList = p_con.prepareStatement(strBuff.toString());
            for (int i = 0, size = p_insertList.size(); i < size; i++) {
                k = 0;
                tempInsertCount = 0;
                try {
                    whiteListVO = (WhiteListVO) p_insertList.get(i);
                    pstmtSubList.setString(++k, whiteListVO.getNetworkCode());
                    pstmtSubList.setString(++k, whiteListVO.getMsisdn());
                    pstmtSubList.setString(++k, whiteListVO.getAccountID());
                    pstmtSubList.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(whiteListVO.getEntryDate()));
                    pstmtSubList.setString(++k, whiteListVO.getAccountStatus());
                    pstmtSubList.setString(++k, whiteListVO.getServiceClassCode());
                    pstmtSubList.setString(++k, whiteListVO.getCreditLimitStr());
                    pstmtSubList.setString(++k, whiteListVO.getInterfaceID());
                    pstmtSubList.setString(++k, whiteListVO.getExternalInterfaceCode());
                    pstmtSubList.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(whiteListVO.getCreatedOn()));
                    pstmtSubList.setString(++k, whiteListVO.getCreatedBy());
                    pstmtSubList.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(whiteListVO.getModifiedOn()));
                    pstmtSubList.setString(++k, whiteListVO.getModifiedBy());
                    pstmtSubList.setString(++k, whiteListVO.getStatus());
                    pstmtSubList.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(whiteListVO.getActivatedOn()));
                    pstmtSubList.setString(++k, whiteListVO.getActivatedBy());
                    pstmtSubList.setString(++k, whiteListVO.getMovementCode());
                    pstmtSubList.setString(++k, whiteListVO.getLanguage());
                    pstmtSubList.setString(++k, whiteListVO.getCountry());
                    pstmtSubList.setString(++k, whiteListVO.getImsi());
                    tempInsertCount = pstmtSubList.executeUpdate();
                    insertCount += tempInsertCount;
                    pstmtSubList.clearParameters();
                    whiteListVO = null;
                } catch (SQLException sqe) {
                    // Checking the cause of SQLException, if it occurs because
                    // of the record exists already,update that record.
                    if (sqe.getErrorCode() == 00001 && sqe.getMessage().indexOf("PK_WHITE_LIST") > 0) {
                        // Incase when number is already exist and insert will
                        // cause to update that records,information will be
                        // logged in error mode- discussed with Sanjay Sir
                        _log.error("insertIndWhiteListDetail", "record already Exist = " + whiteListVO.getMsisdn());
                        pstmtSubList.clearParameters();// Confirm?
                        tempUpdateList.add(whiteListVO);
                    } else {
                        _log.error("insertIndWhiteListDetail", "SQLException : " + sqe.getMessage());
                        sqe.printStackTrace();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[insertIndWhiteListDetail]", "", "", "", "SQL Exception:" + sqe.getMessage());
                        throw new BTSLBaseException(this, "insertIndWhiteListDetail", "error.general.sql.processing");
                    }

                }// end of catch-SQLException
            }// end of for loop
            if (tempUpdateList.size() > 0) {
                try {
                    updateCt = updateWhiteListSubsDetail(p_con, tempUpdateList);
                } catch (BTSLBaseException be) {
                    _log.error("insertIndWhiteListDetail", "BTSLBaseException be=" + be.getMessage());
                    throw be;
                }// end of catch-Exception
            }// end of if
        } catch (SQLException sqe) {
            _log.error("insertIndWhiteListDetail", "SQLException : " + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[insertIndWhiteListDetail]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "insertIndWhiteListDetail", "error.general.sql.processing");
        }// end of catch-SQLException
        catch (Exception ex) {
            _log.error("insertIndWhiteListDetail", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "WhiteListDAO[insertIndWhiteListDetail]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "insertIndWhiteListDetail", "error.general.processing");
        }// end of catch-Exception
        finally {
            try {
                if (pstmtSubList != null) {
                    pstmtSubList.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("insertIndWhiteListDetail", "Exiting insertCount=" + insertCount + " updateCt=" + updateCt);
        }// end of finally
        return insertCount + updateCt;
    }
}
