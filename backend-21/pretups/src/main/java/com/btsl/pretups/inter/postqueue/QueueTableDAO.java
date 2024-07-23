/**
 * @# QueueTableDAO
 *    This class is used to make database interaction with
 *    postpaid_cust_pay_master table.
 * 
 *    Created on Created by History
 *    --------------------------------------------------------------------------
 *    ------
 *    March 28, 2006 Ankit Zindal Initial creation
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2006 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.inter.postqueue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;

public class QueueTableDAO {

    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method for insert Data In Postpaid_cust_pay_master Table.
     * 
     * @param p_con
     *            Connection
     * @param p_queueTableVO
     *            QueueTableVO
     * @return int
     * @exception BTSLBaseException
     */
    public int insertDataInQueueTable(Connection p_con, QueueTableVO p_queueTableVO) throws BTSLBaseException {
    	
            final  String methodName="insertDataInQueueTable";
    	if (_log.isDebugEnabled())
            _log.debug("insertDataInQueueTable()", "Entered p_queueTableVO=" + p_queueTableVO.toString());
        int addCount = -1;
        PreparedStatement pstmt = null;
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("INSERT INTO postpaid_cust_pay_master(queue_id,network_code,msisdn,account_id,amount,transfer_id,status, ");
        strBuff.append("entry_date,description,service_type,entry_type,module_code,sender_id, ");
        strBuff.append("created_on,source_type,interface_id,external_id,service_class,product_code,tax_amount,access_fee_amount,entry_for,bonus_amount,sender_msisdn,gateway_code,interface_amount,imsi,receiver_msisdn,type ) ");
        strBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        String sqlInsert = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("insertDataInQueueTable()", "QUERY sqlInsert=" + sqlInsert);
        try {
            pstmt = p_con.prepareStatement(sqlInsert);
            int i = 1;
            pstmt.setString(i++, p_queueTableVO.getQueueID());
            pstmt.setString(i++, p_queueTableVO.getNetworkID());
            pstmt.setString(i++, p_queueTableVO.getMsisdn());
            pstmt.setString(i++, p_queueTableVO.getAccountID());
            pstmt.setLong(i++, p_queueTableVO.getAmount());
            pstmt.setString(i++, p_queueTableVO.getTransferID());
            pstmt.setInt(i++, Integer.parseInt(p_queueTableVO.getStatus()));
            pstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_queueTableVO.getEntryOn()));
            pstmt.setString(i++, p_queueTableVO.getDescription());
            pstmt.setString(i++, p_queueTableVO.getServiceType());
            pstmt.setString(i++, p_queueTableVO.getEntryType());
            pstmt.setString(i++, p_queueTableVO.getModule());
            pstmt.setString(i++, p_queueTableVO.getSenderID());
            pstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_queueTableVO.getCreatedOn()));
            pstmt.setString(i++, p_queueTableVO.getSourceType());
            pstmt.setString(i++, p_queueTableVO.getInterfaceID());
            pstmt.setString(i++, p_queueTableVO.getExternalInterfaceID());
            pstmt.setString(i++, p_queueTableVO.getServiceClass());
            pstmt.setString(i++, p_queueTableVO.getProductCode());
            pstmt.setLong(i++, p_queueTableVO.getTaxAmount());
            pstmt.setLong(i++, p_queueTableVO.getAccessFee());
            pstmt.setString(i++, p_queueTableVO.getEntryFor());
            pstmt.setLong(i++, p_queueTableVO.getBonusAmount());
            pstmt.setString(i++, p_queueTableVO.getSenderMsisdn());
            pstmt.setString(i++, p_queueTableVO.getGatewayCode());
            pstmt.setDouble(i++, p_queueTableVO.getInterfaceAmount());
            pstmt.setString(i++, InterfaceUtil.NullToString(p_queueTableVO.getImsi()));
            pstmt.setString(i++, p_queueTableVO.getReceiverMsisdn());
            pstmt.setString(i++, p_queueTableVO.getType());
            addCount = pstmt.executeUpdate();
        } catch (SQLException sqe) {
            _log.error("insertDataInQueueTable()", "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableDAO[insertDataInQueueTable]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "insertDataInQueueTable()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("insertDataInQueueTable()", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableDAO[insertDataInQueueTable]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "insertDataInQueueTable()", "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            	 _log.error(methodName,e);
            }
            if (_log.isDebugEnabled())
                _log.debug("insertDataInQueueTable()", "Exiting addCount=" + addCount);
        }
        return addCount;
    }

    /**
     * Method for update Data In Table.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_queueTableVO
     *            QueueTableVO
     * 
     * @return int
     * @exception BTSLBaseException
     */
    public int updateDataInQueueTable(Connection p_con, QueueTableVO p_queueTableVO) throws BTSLBaseException {
    	 final String methodName="updateDataInQueueTable";
        if (_log.isDebugEnabled())
            _log.debug("updateDataInQueueTable()", "Entered p_queueTableVO=" + p_queueTableVO.toString());
        int updateCount = -1;
        PreparedStatement pstmtUpdate = null;
        StringBuilder strBuffUpdate = new StringBuilder("UPDATE postpaid_cust_pay_master SET status=? where transfer_id=?  AND  msisdn=? AND status=? ");
        String sqlUpdate = strBuffUpdate.toString();
        if (_log.isDebugEnabled())
            _log.debug("updateDataInQueueTable()", "QUERY sqlUpdate=" + sqlUpdate);
        try {
            int i = 1;
            pstmtUpdate = p_con.prepareStatement(sqlUpdate);
            i = 1;
            pstmtUpdate.setString(i++, PretupsI.STATUS_QUEUE_FAIL);
            pstmtUpdate.setString(i++, p_queueTableVO.getTransferID());
            pstmtUpdate.setString(i++, p_queueTableVO.getMsisdn());
            pstmtUpdate.setString(i++, PretupsI.STATUS_QUEUE_AVAILABLE);
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount <= 0)
                updateCount = insertDataInQueueTable(p_con, p_queueTableVO);
        } catch (SQLException sqe) {
            _log.error("updateDataInQueueTable()", "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableDAO[updateDataInQueueTable]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "updateDataInQueueTable()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("updateDataInQueueTable()", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableDAO[updateDataInQueueTable]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "updateDataInQueueTable()", "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
            	_log.error(methodName,e);
            }
            if (_log.isDebugEnabled())
                _log.debug("updateDataInQueueTable()", "Exiting updateCount=" + updateCount);
        }
        return updateCount;
    }

    /***
     * Method to calculate the size of queue table
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_serviceType
     *            String
     * @param p_interfaceID
     *            String
     * 
     * @return int
     * @exception BTSLBaseException
     */

    public int calculateQueueTableSize(Connection p_con, String p_serviceType, String p_interfaceID) throws BTSLBaseException {
    	final String methodName = "calculateQueueTableSize";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_serviceType=" + p_serviceType + " p_interfaceID=" + p_interfaceID);
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        int counts = 0;
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT COUNT(Msisdn) FROM postpaid_cust_pay_master WHERE status=? AND interface_id=?");
        if (!PretupsI.ALL.equals(p_serviceType)) {
            if (p_serviceType.indexOf(",") != -1)
                strBuff.append(" AND service_type IN(?)");
            else
                strBuff.append(" AND service_type =?");
        }
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.STATUS_QUEUE_AVAILABLE);
            pstmt.setString(2, p_interfaceID);
            if (!PretupsI.ALL.equals(p_serviceType)) {
            	pstmt.setString(3, p_serviceType);
            }
            rs = pstmt.executeQuery();
            if (rs.next())
                counts = rs.getInt(1);
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableDAO[calculateQueueTableSize]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableDAO[calculateQueueTableSize]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            	_log.error(methodName, "Exception : " + e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            	_log.error(methodName, "Exception : " + e);

            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting queue table size =counts=" + counts);
        }
        return counts;
    }

    /***
     * Method to get the queue id from sequence.
     * 
     * @param p_con
     *            java.sql.Connection
     * @return String
     * @exception BTSLBaseException
     */

    public String getQueueID(Connection p_con) throws BTSLBaseException {
    	final String methodName="getQueueID";
        if (_log.isDebugEnabled())
            _log.debug("getQueueID()", "Entered");
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String id = null;
        final QueueTableQry queueTableQry = (QueueTableQry)ObjectProducer.getObject(QueryConstants.QUEUE_TABLE_ORY, QueryConstants.QUERY_PRODUCER);
        String sqlSelect = queueTableQry.getQueueIDQry();
        if (_log.isDebugEnabled())
            _log.debug("getQueueID()", "QUERY sqlSelect=" + sqlSelect);
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            if (rs.next())
                id = rs.getString(1);
        } catch (SQLException sqe) {
            _log.error("getQueueID()", "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableDAO[getQueueID]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "getQueueID()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("getQueueID()", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableDAO[getQueueID]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "getQueueID()", "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            	_log.error(methodName,e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            	_log.error(methodName,e);
            }
            if (_log.isDebugEnabled())
                _log.debug("getQueueID()", "Exiting id=" + id);
        }
        return id;
    }

    /**
     * Method getQueueDataForCDRGenerationProcess
     * Method to laod the data for CDR from postpaid_cust_pay_master table
     * 
     * @author Amit Ruwali
     * @param p_con
     *            Connection
     * @param p_serviceType
     *            String
     * @param p_interfaceID
     *            String
     * @param p_startTime
     *            TimeStamp
     * @param p_endTime
     *            TimeStamp
     * @return queueList ArrayList
     * @exception BTSLBaseException
     */

    public ArrayList getQueueDataForCDRGenerationProcess(Connection p_con, String p_serviceType, String p_interfaceID, Timestamp p_startTime, Timestamp p_endTime) throws BTSLBaseException {
           final String methodName="getQueueDataForCDRGenerationProcess";
    	if (_log.isDebugEnabled())
            _log.debug("getQueueDataForCDRGenerationProcess", "Entered p_serviceType=" + p_serviceType + "p_interfaceID=" + p_interfaceID + "p_startTime=" + p_startTime + "p_endTime=" + p_endTime);

        String serType = p_serviceType.replaceAll(",", "','");
        p_serviceType = "'" + serType + "'";

        if (_log.isDebugEnabled())
            _log.debug("getQueueDataForCDRGenerationProcess", "p_serviceType=" + p_serviceType);

        ArrayList queueList = new ArrayList();
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        QueueTableVO queueTableVO = null;
        StringBuilder strBuff = new StringBuilder("SELECT ppb.queue_id,ppb.network_code,ppb.msisdn,ppb.account_id,ppb.amount,ppb.transfer_id");
        strBuff.append(",ppb.status,ppb.entry_date,ppb.description,ppb.process_id,ppb.process_date,ppb.other_info,ppb.service_type,ppb.entry_type");
        strBuff.append(",ppb.process_status,ppb.module_code,ppb.sender_id,ppb.created_on,ppb.source_type,ppb.interface_id,ppb.external_id,");
        strBuff.append("ppb.service_class,ppb.product_code,ppb.tax_amount,ppb.access_fee_amount,ppb.entry_for,ppb.bonus_amount,");
        strBuff.append("ppb.sender_msisdn,ppb.cdr_file_name,ppb.gateway_code,ppb.interface_amount,ppb.imsi,ppb.receiver_msisdn,ppb.type ");
        //strBuff.append(",pu.user_name ");// vfe
        //strBuff.append("FROM postpaid_cust_pay_master ppb,users u,users pu WHERE ppb.sender_id=u.user_id and u.owner_id=pu.user_id AND ppb.created_on>=? AND ppb.created_on<? ");
        strBuff.append(" FROM postpaid_cust_pay_master ppb WHERE ppb.created_on>=? AND ppb.created_on<? ");
        strBuff.append("AND ppb.service_type IN(" + p_serviceType +")");
        //String[] args = p_serviceType.split(",");
        //BTSLUtil.pstmtForInQuery(args, strBuff);
        strBuff.append(" AND ppb.interface_id =? AND ppb.status=0 ORDER BY ppb.created_on");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("getQueueDataForCDRGenerationProcess", "QUERY sqlSelect=" + sqlSelect);
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmt.setTimestamp(i++, p_startTime);
            pstmt.setTimestamp(i++, p_endTime);
            /*for(int j=0;j<args.length;j++)
        	{
        		 pstmt.setString(i++, args[j]);
        	}*/
            pstmt.setString(i++, p_interfaceID);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                queueTableVO = new QueueTableVO();
                queueTableVO.setQueueID(rs.getString("queue_id"));
                queueTableVO.setNetworkID(rs.getString("network_code"));
                queueTableVO.setMsisdn(rs.getString("msisdn"));
                queueTableVO.setAccountID(rs.getString("account_id"));
                queueTableVO.setAmount(rs.getLong("amount"));
                queueTableVO.setTransferID(rs.getString("transfer_id"));
                queueTableVO.setStatus(rs.getString("status"));
                queueTableVO.setEntryOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("entry_date")));
                // queueTableVO.setEntryOn(rs.getDate("entry_date"));Changed by
                // Dhiraj on 25/04/07
                queueTableVO.setDescription(rs.getString("description"));
                queueTableVO.setProcessID(rs.getString("process_id"));
                queueTableVO.setProcessDate(rs.getDate("process_date"));
                queueTableVO.setOtherInfo(rs.getString("other_info"));
                queueTableVO.setServiceType(rs.getString("service_type"));
                queueTableVO.setEntryType(rs.getString("entry_type"));
                queueTableVO.setProcessStatus(rs.getString("process_status"));
                queueTableVO.setModule(rs.getString("module_code"));
                queueTableVO.setSenderID(rs.getString("sender_id"));
                // queueTableVO.setCreatedOn(rs.getDate("created_on"));Changed
                // by Dhiraj on 25/04/07
                queueTableVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                queueTableVO.setSourceType(rs.getString("source_type"));
                queueTableVO.setInterfaceID(rs.getString("interface_id"));
                queueTableVO.setExternalInterfaceID(rs.getString("external_id"));
                queueTableVO.setServiceClass(rs.getString("service_class"));
                queueTableVO.setProductCode(rs.getString("product_code"));
                queueTableVO.setTaxAmount(rs.getLong("tax_amount"));
                queueTableVO.setAccessFee(rs.getLong("access_fee_amount"));
                queueTableVO.setEntryFor(rs.getString("entry_for"));
                queueTableVO.setBonusAmount(rs.getLong("bonus_amount"));
                queueTableVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                queueTableVO.setCdrFileName(rs.getString("cdr_file_name"));
                queueTableVO.setGatewayCode(rs.getString("gateway_code"));
                queueTableVO.setInterfaceAmount(rs.getDouble("interface_amount"));
                queueTableVO.setImsi(rs.getString("imsi"));
                queueTableVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                queueTableVO.setType(rs.getString("type"));
                //queueTableVO.setOwnerID(rs.getString("user_name"));
                queueList.add(queueTableVO);
            }
        } catch (Exception ex) {
            _log.error("getQueueDataForCDRGenerationProcess", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableDAO[getQueueDataForCDRGenerationProcess]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "getQueueDataForCDRGenerationProcess", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            	 _log.error(methodName,e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            	 _log.error(methodName,e);
            }
            if (_log.isDebugEnabled())
                _log.debug("getQueueDataForCDRGenerationProcess", "Exiting queueList.size=" + queueList.size());
        }
        return queueList;
    }

    /**
     * Method updateQueueDataForCDR
     * Method used to Update CDR data in postpaid_cust_pay_master table
     * 
     * @author Amit Ruwali
     * @param p_con
     *            Connection
     * @param p_cdrVOList
     *            ArrayList
     * @return updateCount int
     * @exception BTSLBaseException
     */

    public int updateQueueDataForCDR(Connection p_con, ArrayList p_cdrVOList) throws BTSLBaseException {
    	final String methodName="updateQueueDataForCDR";
        if (_log.isDebugEnabled())
            _log.debug("updateQueueDataForCDR", "Entered p_cdrVOList.size()=" + p_cdrVOList.size());
        PreparedStatement pstmt = null;
        QueueTableVO queueTableVO = null;
        int updateCount = -1;
        int updatedRecs = 0;
        int size = 0;
        StringBuilder strBuff = new StringBuilder("UPDATE postpaid_cust_pay_master SET status=?,process_date=?,");
        strBuff.append("process_status=?,process_id=?,cdr_file_name=? WHERE queue_id=?");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("updateQueueDataForCDR", "QUERY sqlSelect=" + sqlSelect);
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            if (p_cdrVOList != null) {
                size = p_cdrVOList.size();
                for (int i = 0; i < size; i++) {
                    queueTableVO = (QueueTableVO) p_cdrVOList.get(i);
                    pstmt.setString(1, queueTableVO.getStatus());
                    pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(queueTableVO.getProcessDate()));
                    pstmt.setString(3, queueTableVO.getProcessStatus());
                    pstmt.setString(4, queueTableVO.getProcessID());
                    pstmt.setString(5, queueTableVO.getCdrFileName());
                    pstmt.setString(6, queueTableVO.getQueueID());
                    updateCount = pstmt.executeUpdate();
                    pstmt.clearParameters();
                    if (updateCount > 0)
                        updatedRecs++;
                }
            }
            if (updatedRecs == size)
                updateCount = size;
            else
                updateCount = -1;
        } catch (Exception ex) {
            _log.error("updateQueueDataForCDR", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableDAO[updateQueueDataForCDR]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "updateQueueDataForCDR", "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            	_log.error(methodName,e);
            }
            if (_log.isDebugEnabled())
                _log.debug("updateQueueDataForCDR", "Exiting updateCount=" + updateCount);
        }
        return updateCount;
    }

    public int getAmountforPPBEnqCalc(Connection p_con, String p_recvrMsisdn) throws BTSLBaseException {
    	 final String methodName="getAmountforPPBEnqCalc";
        if (_log.isDebugEnabled())
            _log.debug("getAmountforPPBEnqCalc()", "Entered p_serviceType=" + p_recvrMsisdn);
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        int counts = 0;
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT SUM(Amount) tot FROM postpaid_cust_pay_master WHERE msisdn=? and status=? ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("getAmountforPPBEnqCalc()", "QUERY sqlSelect=" + sqlSelect);
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_recvrMsisdn);
            pstmt.setInt(2, Integer.parseInt(PretupsI.STATUS_QUEUE_AVAILABLE));
            rs = pstmt.executeQuery();
            if (rs.next())
                counts = rs.getInt("tot");
        } catch (SQLException sqe) {
            _log.error("getAmountforPPBEnqCalc()", "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableDAO[getAmountforPPBEnqCalc]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "getAmountforPPBEnqCalc()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("getAmountforPPBEnqCalc()", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableDAO[getAmountforPPBEnqCalc]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "getAmountforPPBEnqCalc()", "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            	_log.error(methodName,e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            	_log.error(methodName,e);
            }
            if (_log.isDebugEnabled())
                _log.debug("getAmountforPPBEnqCalc()", "Exiting queue table size =counts=" + counts);
        }
        return counts;
    }
}
