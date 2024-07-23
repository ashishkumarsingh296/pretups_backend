package com.btsl.pretups.cosmgmt.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListSorterUtil;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;

public class CosDAO {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    public ArrayList loadCOSMappingDetails(Connection p_con, String p_networkCode) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadCOSMappingDetails", "Entered");
        }
        final String METHOD_NAME = "loadCOSMappingDetails";
        ArrayList<CosVO> list = null;
        CosVO cosVO = null;
        
        StringBuffer selQueryBuff = new StringBuffer("SELECT cm.old_cos,cm.from_recharge,cm.to_recharge,");
        selQueryBuff.append("cm.new_cos,cm.status,cm.network_code,cm.created_on, cm.created_by, cm.modified_on,cm.modified_by FROM cos_masters cm ");
        selQueryBuff.append("WHERE cm.network_code=? AND cm.status <> 'N' ORDER BY cm.from_recharge");

        if (_log.isDebugEnabled()) {
            _log.debug("loadCOSMappingDetails", "SQL Queries : " + selQueryBuff.toString());
        }

        try (PreparedStatement pstmt = p_con.prepareStatement(selQueryBuff.toString());){
           
            pstmt.setString(1, p_networkCode);
           try(ResultSet rs = pstmt.executeQuery();)
           {
            list = new ArrayList<CosVO>();
            while (rs.next()) {
                cosVO = new CosVO();
                cosVO.setOldCosCode(SqlParameterEncoder.encodeParams(rs.getString("old_cos")));
                cosVO.setFromRecharge(SqlParameterEncoder.encodeParams(Long.toString(rs.getLong("from_recharge"))));
                cosVO.setToRecharge(SqlParameterEncoder.encodeParams(Long.toString(rs.getLong("to_recharge"))));
                cosVO.setNewCosCode(SqlParameterEncoder.encodeParams(rs.getString("new_cos")));
                cosVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("status")));
                cosVO.setCreatedOn(BTSLUtil.getDateStringFromDate(rs.getDate("created_on")));
                cosVO.setCreatedBy(SqlParameterEncoder.encodeParams(rs.getString("created_by")));
                if (rs.getDate("modified_on") != null) {
                    cosVO.setModifiedOn(BTSLUtil.getDateStringFromDate(rs.getDate("modified_on")));
                    cosVO.setModifiedBy(SqlParameterEncoder.encodeParams(rs.getString("modified_by")));
                }
                list.add(cosVO);
            }
        } 
        }catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("addCOSDefineData ", "Errors encountered  " + e.getMessage());
            throw new BTSLBaseException(this, "loadCOSMappingDetails", "error.general.sql.processing");
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadCOSMappingDetails", "Exiting list = " + list);
        }
        return list;
    }

    /**
     * This method insert COS details in DB by Batch
     * 
     * @author nand.sahu
     * @param p_con
     * @param p_cosVOList
     * @param p_messages
     * @param p_locale
     * @param userVO
     * @return errorList
     * @throws BTSLBaseException
     */
    public ArrayList addCOSDefineData(Connection p_con, ArrayList p_cosVOList, MessageResources p_messages, Locale p_locale, UserVO userVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("addCOSDefineData ", "Entered ");
        }
        final String METHOD_NAME = "addCOSDefineData";
        ArrayList<ListValueVO> errorList = null;
        ArrayList cosVODBList = null;
        ArrayList cosVODBPlusXLList = new ArrayList();
        
        int totalInsertCount = 0;
        ListSorterUtil sort = null;
        CosVO cosVO = null;
        CosVO updatedCosVODBPlusXL = null;
        ListValueVO errorVO = null;
        boolean isInvalidRange = false;
        StringBuffer insertCOSQuery = new StringBuffer("INSERT into COS_MASTERS(old_cos, from_recharge, to_recharge, new_cos,");
        insertCOSQuery.append(" status, network_code, created_on, created_by) VALUES(?,?,?,?,?,?,?,?)");

        try {
            errorList = new ArrayList<ListValueVO>();
            cosVODBList = this.loadCOSMappingDetails(p_con, userVO.getNetworkID());
            // Adding COSVO from Database and COSVO from xls file for Validation
            if (cosVODBList != null && !cosVODBList.isEmpty()) {
                cosVODBPlusXLList.addAll(cosVODBList);
            }
            sort = new ListSorterUtil();
            try( PreparedStatement pstmt = p_con.prepareStatement(insertCOSQuery.toString());)
            {
            // Loop for number of records in the xls file.
            int cosVOLists=p_cosVOList.size();
            for (int i = 0; i < cosVOLists; i++) {
                cosVODBPlusXLList = (ArrayList) sort.doSort("fromRecharge", null, cosVODBPlusXLList);
                cosVO = (CosVO) p_cosVOList.get(i);
                isInvalidRange = false;
                int cosVODBPlusLList=cosVODBPlusXLList.size();
                for (int j = 0; j <cosVODBPlusLList ; j++) {
                    updatedCosVODBPlusXL = (CosVO) cosVODBPlusXLList.get(j);
                    // From Recharge and To Recharge should not overlaps for
                    // same Old cos id.
                    if (updatedCosVODBPlusXL.getOldCosCode().equalsIgnoreCase(cosVO.getOldCosCode())) {
                        // If block check From Recharge validation and else
                        // block check to Recharge validation.
                        if (Long.parseLong(cosVO.getFromRecharge()) >= (Long.parseLong(updatedCosVODBPlusXL.getFromRecharge())) && Long.parseLong(cosVO.getFromRecharge()) <= (Long.parseLong(updatedCosVODBPlusXL.getToRecharge()))) {
                            errorVO = new ListValueVO("", (new Integer(cosVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale, "cosmanagement.define.error.fromrechargerange"));
                            errorList.add(errorVO);
                            isInvalidRange = true;
                        } else if (Long.parseLong(cosVO.getToRecharge()) >= (Long.parseLong(updatedCosVODBPlusXL.getFromRecharge())) && Long.parseLong(cosVO.getToRecharge()) <= (Long.parseLong(updatedCosVODBPlusXL.getToRecharge()))) {
                            errorVO = new ListValueVO("", (new Integer(cosVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale, "cosmanagement.define.error.torechargerange"));
                            errorList.add(errorVO);
                            isInvalidRange = true;
                        }
                    }
                }
                // Adding COS details in Database if From Recharge and To
                // Recharge range is valid
                if (!isInvalidRange) {
                    Date date = new Date();
                    pstmt.setString(1, cosVO.getOldCosCode());
                    pstmt.setLong(2, Long.parseLong(cosVO.getFromRecharge()));
                    pstmt.setLong(3, Long.parseLong(cosVO.getToRecharge()));
                    pstmt.setString(4, cosVO.getNewCosCode());
                    pstmt.setString(5, cosVO.getStatus());
                    pstmt.setString(6, cosVO.getNetworkCode());
                    pstmt.setDate(7, BTSLUtil.getSQLDateFromUtilDate(date));
                    pstmt.setString(8, cosVO.getCreatedBy());

                    int insertCount = pstmt.executeUpdate();
                    if (insertCount == 1) {
                        totalInsertCount++;
                        p_con.commit();
                        // Adding CosVO that successfully inserted into DB to
                        // validate next record in the file
                        cosVODBPlusXLList.add(cosVO);
                    } else {
                        errorVO = new ListValueVO("", (new Integer(cosVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale, "cosmanagement.define.xls.error.cosinsert"));
                        errorList.add(errorVO);
                        continue;
                    }
                }// end of isInvalidRange flag
            }
        }
        }catch (SQLException sqle) {
            _log.error("addCOSDefineData ", "Errors encountered  " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, "addCOSDefineData", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("addCOSDefineData ", "Errors encountered  " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "addCOSDefineData", "error.general.processing");
        }
        if (_log.isDebugEnabled()) {
            _log.debug("addCOSDefineData ", "Exiting totalInsertCount = " + totalInsertCount);
        }
        return errorList;
    }

    /**
     * Method generateUserId
     * Method to generate the userId while inserting new record
     * 
     * @param p_networkCode
     *            String
     * @param p_prefix
     *            String
     * @param p_counter
     *            Long
     * @param p_userPaddingLength
     *            TODO
     * @return String
     */
    /*
     * private String generateUserId(String p_prefix,String p_networkCode,long
     * p_counter, int p_userPaddingLength) throws Exception
     * {
     * if (_log.isDebugEnabled())
     * _log.debug("generateUserId" ,
     * "Entered p_networkCode="+p_networkCode+" p_prefix="
     * +p_prefix+" p_counter="+p_counter);
     * String id = BTSLUtil.padZeroesToLeft(p_counter+"",p_userPaddingLength);
     * id = p_prefix+p_networkCode+id;
     * if (_log.isDebugEnabled()) _log.debug("generateUserId" ,
     * "Exiting id="+id);
     * return id;
     * }
     */
    /**
     * modCOSDetails method delete all the COS details from Database and insert
     * again in DB as per given in file
     * 
     * @author nand.sahu
     * @param p_con
     * @param p_cosVOLstMap
     * @param p_messages
     * @param p_locale
     * @param userVO
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList modCOSDetails(Connection p_con, HashMap p_cosVOLstMap, MessageResources p_messages, Locale p_locale, UserVO userVO) throws BTSLBaseException {
        final String METHOD_NAME = "modCOSDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("modCOSDetails ", "Entered p_cosVOLstMap.size = " + p_cosVOLstMap.size());
        }
        ArrayList<ListValueVO> errorList = null;
        PreparedStatement insertpstmt = null;
        PreparedStatement deletepstmt = null;
        ArrayList cosVOAddedIntoDBList = new ArrayList();
        int insertedCount = 0;
        CosVO cosVO = null, cosVOAddedIntoDB = null;
        ListValueVO errorVO = null;
        boolean isInvalidRange = false;
        StringBuffer delQuery = new StringBuffer("DELETE FROM COS_MASTERS WHERE network_code=?");
        StringBuffer insertQuery = new StringBuffer("INSERT INTO cos_masters ( old_cos, from_recharge, to_recharge, new_cos, status,");
        insertQuery.append(" network_code, created_on, created_by, modified_on, modified_by ) VALUES (?,?,?,?,?,?,?,?,?,?)");
        if (_log.isDebugEnabled()) {
            _log.debug("modCOSDetails ", "DELETE Query : " + delQuery.toString());
            _log.debug("modCOSDetails ", "INSERT query : " + insertQuery.toString());
        }
        ArrayList p_cosVOList = (ArrayList) p_cosVOLstMap.get(PretupsI.CELL_ID_MAPPING_MODIFY);
        errorList = new ArrayList<ListValueVO>();
        try {

            deletepstmt = p_con.prepareStatement(delQuery.toString());
            insertpstmt = p_con.prepareStatement(insertQuery.toString());
            deletepstmt.setString(1, userVO.getNetworkID());
            deletepstmt.execute();
            Date date = new Date();
            ListSorterUtil sort = new ListSorterUtil();
            int cosVOLists=p_cosVOList.size();
            for (int i = 0; i < cosVOLists; i++) {
                isInvalidRange = false;
                cosVO = (CosVO) p_cosVOList.get(i);
                int cosVOAddedIntoDBLists=cosVOAddedIntoDBList.size();
                for (int j = 0; j <cosVOAddedIntoDBLists ; j++) {
                    cosVOAddedIntoDB = (CosVO) cosVOAddedIntoDBList.get(j);
                    // From Recharge and To Recharge should not overlaps for
                    // same Old cos id.
                    if (cosVOAddedIntoDB.getOldCosCode().equalsIgnoreCase(cosVO.getOldCosCode())) {
                        // If block check From Recharge validation and else
                        // block check to Recharge validation.
                        if (Long.parseLong(cosVO.getFromRecharge()) >= (Long.parseLong(cosVOAddedIntoDB.getFromRecharge())) && Long.parseLong(cosVO.getFromRecharge()) <= (Long.parseLong(cosVOAddedIntoDB.getToRecharge()))) {
                            errorVO = new ListValueVO("", (new Integer(cosVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale, "cosmanagement.define.error.fromrechargerange"));
                            errorList.add(errorVO);
                            isInvalidRange = true;
                        } else if (Long.parseLong(cosVO.getToRecharge()) >= (Long.parseLong(cosVOAddedIntoDB.getFromRecharge())) && Long.parseLong(cosVO.getToRecharge()) <= (Long.parseLong(cosVOAddedIntoDB.getToRecharge()))) {
                            errorVO = new ListValueVO("", (new Integer(cosVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale, "cosmanagement.define.error.torechargerange"));
                            errorList.add(errorVO);
                            isInvalidRange = true;
                        }
                    }
                }
                if (!isInvalidRange && errorList.isEmpty())// Insertion will
                                                           // happen if none
                                                           // errors available
                                                           // else reject whole
                                                           // file
                {
                    insertpstmt.setString(1, cosVO.getOldCosCode());
                    insertpstmt.setLong(2, Long.parseLong(cosVO.getFromRecharge()));
                    insertpstmt.setLong(3, Long.parseLong(cosVO.getToRecharge()));
                    insertpstmt.setString(4, cosVO.getNewCosCode());
                    insertpstmt.setString(5, cosVO.getStatus());
                    insertpstmt.setString(6, cosVO.getNetworkCode());
                    insertpstmt.setDate(7, BTSLUtil.getSQLDateFromUtilDate(date));// Need
                                                                                  // to
                                                                                  // Recheck
                    insertpstmt.setString(8, cosVO.getCreatedBy());
                    insertpstmt.setDate(9, BTSLUtil.getSQLDateFromUtilDate(date));
                    insertpstmt.setString(10, cosVO.getModifiedBy());
                    insertedCount = +insertpstmt.executeUpdate();
                    cosVOAddedIntoDBList.add(cosVO);
                    cosVOAddedIntoDBList = (ArrayList) sort.doSort("fromRecharge", null, cosVOAddedIntoDBList);
                }
            }

        } catch (SQLException sqle) {
            _log.error("modCOSDetails ", "Record could not been deleted " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, "modCOSDetails", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("modCOSDetails ", "Record could not been deleted " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "modCOSDetails", "error.general.processing");
        } finally {
            try {
                if (deletepstmt != null) {
                    deletepstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (insertpstmt != null) {
                    insertpstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadChannelUserDetails", "Exiting ");
            }
        }// end of finally
        if (_log.isDebugEnabled()) {
            _log.debug("modCOSDetails ", "Exiting insertedCount = " + insertedCount);
        }
        return errorList;
    }

    private ArrayList delCOSDetails(Connection p_con, ArrayList p_cosVOList, MessageResources p_messages, Locale p_locale, UserVO userVO, String fileName) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("delCOSDetails ", "Entered ");
        }
        final String METHOD_NAME = "delCOSDetails";
        ArrayList<ListValueVO> errorList = new ArrayList<ListValueVO>();
        
        CosVO cosVO = null;
        ListValueVO errorVO = null;
        int delCount = 0;
        StringBuffer updateQuery = new StringBuffer("UPDATE cos_masters cm SET cm.status='N', cm.modified_on=?,cm.modified_by=? WHERE cm.cos_id=?");
        try {
            if (p_cosVOList != null && !p_cosVOList.isEmpty()) {
            	int cosVOLists=p_cosVOList.size();
                for (int i = 0; i <cosVOLists ; i++) {
                    cosVO = (CosVO) p_cosVOList.get(i);
                   try(PreparedStatement pstmt = p_con.prepareStatement(updateQuery.toString());)
                   {
                    pstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(new Date()));
                    pstmt.setString(2, cosVO.getModifiedBy());
                    pstmt.setString(3, cosVO.getCosID());
                    int update = pstmt.executeUpdate();
                    if (update > 0) {
                        p_con.commit();
                        delCount++;
                    } else {
                        errorVO = new ListValueVO("", (new Integer(cosVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale, "cosmanagement.define.xls.database.error"));
                        errorList.add(errorVO);
                        continue;
                    }
                }
                }
            }
        } catch (SQLException sqe) {
            _log.error("delCOSDetails ", "Record could not been deleted " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, "addCOSManageData", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("delCOSDetails ", "Record could not been deleted " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "addCOSManageData", "error.general.processing");
        }
        if (_log.isDebugEnabled()) {
            _log.debug("delCOSDetails ", "Exiting " + delCount);
        }
        return errorList;
    }

    /**
     * Method to load the new COS from the COS_MASTERS table on the basis of
     * requested amount range
     * 
     * @author amit.singh
     * @param p_con
     *            Connection
     * @param p_requestedAmount
     *            String
     * @return String
     * @throws BTSLBaseException
     */
    public String loadNewCOSWithinRange(Connection p_con, String p_requestedAmount, String p_oldCos) throws BTSLBaseException {
        final String METHOD_NAME = "loadNewCOSWithinRange";
        String newCos = "";
        if (_log.isDebugEnabled()) {
            _log.debug("loadNewCOSWithinRange ", "Entered p_requestedAmount " + p_requestedAmount + " and p_oldCos=" + p_oldCos);
        }
        p_requestedAmount = PretupsBL.getDisplayAmount(Long.parseLong(p_requestedAmount));
        if (_log.isDebugEnabled()) {
            _log.debug("loadNewCOSWithinRange ", "After converting allowed amount for COS=" + p_requestedAmount);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer selectQuery = new StringBuffer("SELECT cm.NEW_COS FROM COS_MASTERS cm WHERE cm.old_cos=? AND cm.FROM_RECHARGE <=? ");
        selectQuery.append("AND cm.TO_RECHARGE>=? AND cm.STATUS=?");
        if (_log.isDebugEnabled()) {
            _log.debug("loadNewCOSWithinRange ", "selectQuery : " + selectQuery.toString());
        }
        try {
            pstmt = p_con.prepareStatement(selectQuery.toString());
            pstmt.setString(1, p_oldCos);
            pstmt.setLong(2, BTSLUtil.parseStringToLong(p_requestedAmount));
            pstmt.setLong(3, BTSLUtil.parseStringToLong(p_requestedAmount));
            pstmt.setString(4, PretupsI.COS_STATUS_ACTIVE);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                newCos = rs.getString("NEW_COS");
            }
        } catch (SQLException sqle) {
            _log.error("loadNewCOSWithinRange", "SQLException: " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CosDAO[loadNewCOSWithinRange]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadNewCOSWithinRange", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("loadNewCOSWithinRange", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CosDAO[loadNewCOSWithinRange]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadNewCOSWithinRange", "error.general.processing");
        } // end of catch
        finally {
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
                _log.debug("loadNewCOSWithinRange", "Exiting: newCos=" + newCos);
            }
        } // end of finally
        return newCos;
    }
}// End of class
