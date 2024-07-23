/**
 * @(#)CustomerRechargeFileCreation.java
 *                                       Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                       All Rights Reserved
 *                                       This class is used for finding the
 *                                       records for generating the Customer
 *                                       Recharge File.
 *                                       <description>
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       -------------------
 *                                       Author Date History
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       -------------------
 *                                       Pushkar Sharma Jan 12, 2015 Initital
 *                                       Creation
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       ------------------
 * 
 */

package com.btsl.pretups.processes.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.util.BTSLUtil;

public class CustomerRechargeFileCreationDAO {
    private static final Log LOG = LogFactory.getLog(CustomerRechargeFileCreationDAO.class.getName());

    /**
     * getCustomerRechargeDetail()
     * 
     * @param conn
     * @param executedUpto
     * @param nextExecutionTime
     * @return detailList
     * @throws BTSLBaseException
     */
    public List<C2STransferVO> getCustomerRechargeDetail(Connection conn, Date executedUpto, Date nextExecutionTime) throws BTSLBaseException {
    	//local_index_missing
        final String methodName = "getCustomerRechargeDetail()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entering :: executedUpto ::" + executedUpto + ":: nextExecutionTime ::" + nextExecutionTime);
        }
        List<C2STransferVO> detailList = null;
        C2STransferVO c2sTransferVO = null;
        ResultSet rset = null;
        StringBuilder strBuilder = null;
        PreparedStatement pstmt = null;
        String dateFormate = "yyyy-MM-dd HH:mm:ss";

        try {
            Timestamp executedTimeStamp = BTSLUtil.getTimestampFromUtilDate(executedUpto);
            Timestamp nextExecutionTimeStamp = BTSLUtil.getTimestampFromUtilDate(nextExecutionTime);

            strBuilder = new StringBuilder("SELECT CT.TRANSFER_DATE_TIME, CT.SENDER_MSISDN, U.EXTERNAL_CODE, U.USER_NAME, CT.RECEIVER_MSISDN, ");
            strBuilder.append(" CT.BONUS_DETAILS, CT.TRANSFER_VALUE, CT.SENDER_CATEGORY, CT.SERVICE_TYPE, CT.CELL_ID, ADJ.TRANSFER_VALUE as ADJ_TRANSFER_VALUE");
            strBuilder.append(" FROM USERS U, C2S_TRANSFERS CT LEFT JOIN ADJUSTMENTS ADJ ON CT.TRANSFER_ID = ADJ.REFERENCE_ID  ");
            strBuilder.append(" WHERE CT.TRANSFER_DATE >= ? AND CT.TRANSFER_DATE <= ? AND CT.SENDER_ID = U.USER_ID AND CT.TRANSFER_DATE_TIME BETWEEN ? AND ?");

            pstmt = conn.prepareStatement(strBuilder.toString());
            pstmt.clearParameters();
            pstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(new Date(executedTimeStamp.getTime())));
            pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(new Date(nextExecutionTimeStamp.getTime())));
            pstmt.setTimestamp(3, executedTimeStamp);
            pstmt.setTimestamp(4, nextExecutionTimeStamp);

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Executed Query  in DAO  ::" + strBuilder.toString());
                LOG.debug(methodName, "executedUpto in DAO  ::" + executedTimeStamp);
                LOG.debug(methodName, "nextExecutionTime  in DAO  ::" + nextExecutionTimeStamp);
            }

            rset = pstmt.executeQuery();
            detailList = new ArrayList<C2STransferVO>();
            int count = 0;
            while (rset.next()) {
                count++;
                String bonusIds = "";
                String bonusType = "";
                String bonusValue = "";
                c2sTransferVO = new C2STransferVO();

                c2sTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(rset.getTimestamp("TRANSFER_DATE_TIME"), dateFormate));

                if (rset.getString("SENDER_MSISDN") != null) {
                    c2sTransferVO.setSenderMsisdn(rset.getString("SENDER_MSISDN"));
                } else {
                    c2sTransferVO.setSenderMsisdn("");
                }
                if (rset.getString("EXTERNAL_CODE") != null) {
                    c2sTransferVO.setReferenceID(rset.getString("EXTERNAL_CODE"));
                } else {
                    c2sTransferVO.setReferenceID("");
                }
                if (rset.getString("USER_NAME") != null) {
                    c2sTransferVO.setSenderName(rset.getString("USER_NAME"));
                } else {
                    c2sTransferVO.setSenderName("");
                }
                if (rset.getString("RECEIVER_MSISDN") != null) {
                    c2sTransferVO.setReceiverMsisdn(rset.getString("RECEIVER_MSISDN"));
                } else {
                    c2sTransferVO.setReceiverMsisdn("");
                }
                if (rset.getString("SENDER_CATEGORY") != null) {
                    c2sTransferVO.setSenderCategoryCode(rset.getString("SENDER_CATEGORY"));
                } else {
                    c2sTransferVO.setSenderCategoryCode("");
                }
                if (rset.getString("SERVICE_TYPE") != null) {
                    c2sTransferVO.setServiceType(rset.getString("SERVICE_TYPE"));
                } else {
                    c2sTransferVO.setServiceType("");
                }
                if (rset.getString("CELL_ID") != null) {
                    c2sTransferVO.setCellId(rset.getString("CELL_ID"));
                } else {
                    c2sTransferVO.setCellId("");
                }
                if (rset.getString("ADJ_TRANSFER_VALUE") != null) {
                    c2sTransferVO.setTransferValueStr(rset.getString("ADJ_TRANSFER_VALUE"));
                } else {
                    c2sTransferVO.setTransferValueStr("");
                }
                c2sTransferVO.setTransferValue(rset.getLong("TRANSFER_VALUE"));
                c2sTransferVO.setBonusSummarySting(rset.getString("BONUS_DETAILS"));

                if (rset.getString("BONUS_DETAILS") != null) {
                    String bonusDetail = rset.getString("BONUS_DETAILS");
                    if (bonusDetail != null && bonusDetail.length() > 0) {
                        String[] singleBonus = bonusDetail.split("\\|");
                        for (int i = 0; i < singleBonus.length; i++) {
                            String[] bonus = singleBonus[i].split(":");
                            bonusIds = bonus[0] + "#";
                            bonusType = bonus[1] + "#";
                            bonusValue = bonus[2] + "#";
                        }
                    }
                }

                c2sTransferVO.setBonusBundleIdS(this.removeLastHash(bonusIds));
                c2sTransferVO.setBonusBundleTypes(this.removeLastHash(bonusType));
                c2sTransferVO.setBonusBundleValues(this.removeLastHash(bonusValue));

                detailList.add(c2sTransferVO);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Count for this query  ::" + count);
            }
        } catch (SQLException sqle) {
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CustomerRechargeFileCreationDAO[" + methodName + "]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CustomerRechargeFileCreationDAO[" + methodName + "]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try {
                if (rset != null) {
                	rset.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName + " error in statement ", e);
            }
        	try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName + " error in statement ", e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting :: List Size == " + detailList.size());
            }
        }

        return detailList;
    }

    private String removeLastHash(String str) {
        String strReturned = "";
        if (str != null && str.length() > 0) {
            strReturned = str.substring(0, str.length() - 1);
        }
        return strReturned;
    }

    /**
     * updateProcessStatus()
     * 
     * @param conn
     * @param startedDate
     * @param executedUpto
     * @param processId
     * @return updateCount
     * @throws BTSLBaseException
     */
    public int updateProcessStatus(Connection conn, Date startedDate, Date executedUpto, String processId) throws BTSLBaseException {
        final String methodName = "updateProcessStatus";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entering :: processId ::" + processId + " ::startedDate ::" + startedDate + " :: executedUpto ::" + executedUpto);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            String sqlUpdate = " UPDATE process_status SET start_date=?,executed_upto=?,executed_on=? WHERE process_id=? ";
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Update qrySelect:" + sqlUpdate);
            }
            pstmtUpdate = conn.prepareStatement(sqlUpdate);
            pstmtUpdate.clearParameters();

            pstmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(startedDate));
            pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(executedUpto));
            pstmtUpdate.setDate(3, BTSLUtil.getSQLDateFromUtilDate(executedUpto));
            pstmtUpdate.setString(4, processId);

            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqe) {
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CustomerRechargeFileCreationDAO[" + methodName + "]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("CustomerRechargeFileCreationDAO", methodName, "");
        } catch (Exception ex) {
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CustomerRechargeFileCreationDAO[" + methodName + "]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("CustomerRechargeFileCreationDAO", methodName, "");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace("CustomerRechargeFileCreationDAO[" + methodName + "]", e);
            }
            if (LOG.isDebugEnabled())
                LOG.debug("CustomerRechargeFileCreationDAO", "Exiting " + methodName + " updateCount :: " + updateCount);
        }
        return updateCount;
    }

    /**
     * markProcessStatusAsComplete()
     * 
     * @param conn
     * @param processId
     * @param processStatusVO
     * @return updateCount
     * @throws BTSLBaseException
     *             It changes the status of the process to C [Complete]
     */
    public int markProcessStatusAsComplete(Connection conn, String processId, ProcessStatusVO processStatusVO) throws BTSLBaseException {
        final String methodName = "markProcessStatusAsComplete";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Entered ::  processId ::" + processId + " :: processStatusVO ::" + processStatusVO);
        }
        int updateCount = 0;
        try {
            ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
            processStatusVO.setProcessID(processId);
            processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
            updateCount = processStatusDAO.updateProcessDetail(conn, processStatusVO);
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CustomerRechargeFileCreationDAO[" + methodName + "]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CustomerRechargeFileCreationDAO", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (LOG.isDebugEnabled())
                LOG.debug(methodName, "Exiting: updateCount=" + updateCount);
        }
        return updateCount;

    }
}
