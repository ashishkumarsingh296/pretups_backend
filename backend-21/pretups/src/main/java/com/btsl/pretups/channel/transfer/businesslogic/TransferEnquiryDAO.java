package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class TransferEnquiryDAO {

    private static Log LOG = LogFactory.getLog(TransferEnquiryDAO.class.getName());
    public static OperatorUtilI _operatorUtilI = null;
    private TransferEnquiryQry transferEnquiryQry = null;
    static {
        try {
            _operatorUtilI = (OperatorUtilI) Class.forName((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS)).newInstance();
        } catch (Exception e) {

            LOG.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BuddyMgtAction", "", "", "",
                "Exception while loading the operator util class in class :" + ChannelTransferDAO.class.getName() + ":" + e.getMessage());
        }
    }
    
    public TransferEnquiryDAO() {
    	   transferEnquiryQry = (TransferEnquiryQry)ObjectProducer.getObject(QueryConstants.TRANSFER_ENQUIRY_QRY, QueryConstants.QUERY_PRODUCER);
	}

    /**
     * This method Load the last N number of c2c, c2s and o2c transaction
     * details.
     * 
     * @author vikram.kumar
     * @param p_con
     *            Connection
     * @param p_user_id
     *            String
     * @param p_noLastTxn
     *            int //no of last transactions to be fetched.
     * @param serviceType
     *            // C2C ,O2C & C2S transaction details.
     * @param noDays
     *            //fetch only data for this period if null/0 then no check on
     *            the date.
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public List<C2STransferVO> loadLastXTransfers(Connection p_con, String p_user_id, int p_noLastTxn, String serviceType, 
    		int noDays, String recMsisdn, java.sql.Date txnDate) throws BTSLBaseException {
        final String methodName = "loadLastXTransfers";
        try {
            if (_operatorUtilI.getNewDataAftrTbleMerging(BTSLUtil.addDaysInUtilDate(new Date(), -noDays), new Date())) {
            	return loadLastXTransfers_new(p_con, p_user_id, p_noLastTxn, serviceType, noDays, recMsisdn, txnDate);
            } else {
                return loadLastXTransfers_old(p_con, p_user_id, p_noLastTxn, serviceType, noDays, recMsisdn, txnDate);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
                LOG.errorTrace(methodName, e);
        }
        ;

        return loadLastXTransfers_new(p_con, p_user_id, p_noLastTxn, serviceType, noDays,recMsisdn,txnDate);
    }

    private List<C2STransferVO> loadLastXTransfers_old(Connection p_con, String p_user_id, int p_noLastTxn, String serviceType, int noDays, String recMsisdn, java.sql.Date txnDate) throws BTSLBaseException {
        final String methodName = "loadLastXTransfers_old";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  p_user_id: ");
        	loggerValue.append(p_user_id);
        	loggerValue.append(", p_noLastTxn: ");
        	loggerValue.append(p_noLastTxn);
        	loggerValue.append(", serviceType: " );
        	loggerValue.append(serviceType);
        	loggerValue.append("noDays: " );
        	loggerValue.append(noDays);
        	loggerValue.append(" receiver msisdn: ");
        	loggerValue.append(recMsisdn);
            LOG.debug(methodName,  loggerValue);
        }
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        C2STransferVO transferVO = null;
        List<C2STransferVO> transfersList = null;
        StringBuffer strBuff = null;
        try {
            transfersList = new ArrayList<C2STransferVO>();
            final String[] services = serviceType.split(",");
           
            for (int j = 0; j < services.length; j++) {
                if (PretupsI.SERVICE_TYPE_C2S_LAST_X_TRANSFER.equals(services[j])) {
                	pstmt = transferEnquiryQry.loadLastXTransfersOldQryIfC2S(p_con, recMsisdn, p_user_id, p_noLastTxn, noDays, txnDate);
                    rs = pstmt.executeQuery();
                    while (rs.next()) {
                        transferVO = new C2STransferVO();
                        transferVO.setTransferID(rs.getString("transfer_id"));
                        transferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                        transferVO.setTransferDateTimeAsString(BTSLDateUtil.getLocaleDateTimeFromDate(rs.getTimestamp("transfer_date_time")));
                        transferVO.setTransferStatus(rs.getString("transfer_status"));
                        transferVO.setType(PretupsI.C2S_MODULE);
                        transferVO.setReceiverMsisdn(rs.getString("receiver_Msisdn"));
                        transferVO.setTransferValue(rs.getLong("net_payable_amount"));
                        transferVO.setCreatedOn(rs.getTimestamp("created_on"));
                        transferVO.setServiceType(rs.getString("service"));
                        transferVO.setServiceName(rs.getString("name"));
                        transferVO.setStatus(rs.getString("statusname"));
                        transferVO.setSenderPostBalance(rs.getLong("post_balance"));
                        transferVO.setProductName(rs.getString("SHORT_NAME"));
                        transferVO.setSID(rs.getString("SUBS_SID"));
                        transfersList.add(transferVO);
                    }
                }
                // modified bt rahuld for korek
                else if (services[j].contains(PretupsI.SERVICE_TYPE_C2C_LAST_X_TRANSFER)) {
                	pstmt1 =  transferEnquiryQry.loadLastXTransfersOldQryIfC2C(p_con, recMsisdn, p_user_id, p_noLastTxn, noDays, services[j], txnDate);
                    rs1 = pstmt1.executeQuery();
                    while (rs1.next()) {
                        transferVO = new C2STransferVO();
                        transferVO.setTransferID(rs1.getString("transfer_id"));
                        transferVO.setTransferDateTime(rs1.getTimestamp("CLOSE_DATE"));
                        transferVO.setTransferDateTimeAsString(BTSLDateUtil.getLocaleDateTimeFromDate(rs1.getTimestamp("CLOSE_DATE")));
                        transferVO.setTransferStatus(rs1.getString("status"));
                        transferVO.setType(PretupsI.C2C_MODULE);
                        transferVO.setReceiverMsisdn(rs1.getString("to_msisdn"));
                        transferVO.setSenderMsisdn(rs1.getString("msisdn"));
                        transferVO.setTransferValue(rs1.getLong("net_payable_amount"));
                        transferVO.setCreatedOn(rs1.getTimestamp("created_on"));
                      
                        transferVO.setServiceType(rs1.getString("service")); // set
                        // service
                        // type
                        // here.
                        transferVO.setServiceName(rs1.getString("name")); // from
                        // look-up
                        // set
                        // service
                        // name
                        transferVO.setStatus(rs1.getString("statusname"));
                        final long approvedQuantity = rs1.getLong("approved_quantity");
                        if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(rs1.getString("transfer_sub_type")) || PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(rs
                            .getString("transfer_sub_type"))) {
                            transferVO.setSenderPostBalance(rs1.getLong("sender_previous_stock") - approvedQuantity);
                        } else {
                            transferVO.setSenderPostBalance(rs1.getLong("receiver_previous_stock") + approvedQuantity);
                        }
                        transfersList.add(transferVO);
                    }
                }
                // else
                else if (services[j].contains(PretupsI.SERVICE_TYPE_O2C_LAST_X_TRANSFER))
                // for o2c
                {
                	pstmt2 = transferEnquiryQry.loadLastXTransfersOldQryIfO2C(p_con, recMsisdn, p_user_id, p_noLastTxn, noDays, services[j], txnDate);
                    rs2 = pstmt2.executeQuery();
                    while (rs2.next()) {
                        transferVO = new C2STransferVO();
                        transferVO.setTransferID(rs2.getString("transfer_id"));
                        transferVO.setTransferDateTime(rs2.getTimestamp("CLOSE_DATE"));
                        transferVO.setTransferDateTimeAsString(BTSLDateUtil.getLocaleDateTimeFromDate(rs2.getTimestamp("CLOSE_DATE")));
                        transferVO.setTransferStatus(rs2.getString("status"));
                        transferVO.setType(PretupsI.TRANSFER_TYPE_O2C);
                        transferVO.setSenderMsisdn(rs2.getString("msisdn"));
                        transferVO.setReceiverMsisdn(rs2.getString("to_msisdn"));
                        transferVO.setTransferValue(rs2.getLong("approved_quantity"));
                        transferVO.setCreatedOn(rs2.getTimestamp("created_on"));
                        
                        transferVO.setServiceType(rs2.getString("service")); // from
                        // look-up
                        // set
                        // service
                        // name
                        transferVO.setServiceName(rs2.getString("name")); // set
                        // sub
                        // service
                        // type
                        // here.
                        transferVO.setStatus(rs2.getString("statusname"));
                        // for user post balance.
                        final long approvedQuantity = rs2.getLong("approved_quantity");
                        if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(rs2.getString("transfer_sub_type")) || PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(rs2
                            .getString("transfer_sub_type"))) {
                            transferVO.setSenderPostBalance(rs2.getLong("sender_previous_stock") - approvedQuantity);
                        } else {
                            transferVO.setSenderPostBalance(rs2.getLong("receiver_previous_stock") + approvedQuantity);
                        }
                        transfersList.add(transferVO);
                    }
                }

            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            LOG.error(methodName, loggerValue );
            LOG.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadLastXTransfers]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            LOG.error(methodName, loggerValue );
            LOG.errorTrace(methodName, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadLastXTransfers]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rs2 != null) {
                    rs2.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt1 != null) {
                    pstmt1.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt2 != null) {
                    pstmt2.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  transfersList =");
            	loggerValue.append(transfersList.size());
                LOG.debug(methodName, loggerValue);
            }
        }
        return transfersList;
    }

    /**
     * This method Load the last N number of c2c, c2s and o2c transaction
     * details.
     * 
     * @author vikram.kumar
     * @param p_con
     *            Connection
     * @param p_user_id
     *            String
     * @param p_noLastTxn
     *            int //no of last transactions to be fetched.
     * @param serviceType
     *            // C2C ,O2C & C2S transaction details.
     * @param noDays
     *            //fetch only data for this period if null/0 then no check on
     *            the date.
     * @return ArrayList
     * @throws BTSLBaseException
     */
    private List<C2STransferVO> loadLastXTransfers_new(Connection p_con, String p_user_id, int p_noLastTxn, String serviceType, 
    		int noDays, String recMsisdn, java.sql.Date txnDate) throws BTSLBaseException {
        final String methodName = "loadLastXTransfers_new";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  p_user_id: ");
        	loggerValue.append(p_user_id);
        	loggerValue.append(", p_noLastTxn: " );
        	loggerValue.append(p_noLastTxn);
        	loggerValue.append(", serviceType: ");
        	loggerValue.append(serviceType);
        	loggerValue.append("noDays: ");
        	loggerValue.append(noDays);
        	loggerValue.append(" receiver msisdn: ");
        	loggerValue.append(recMsisdn);
            LOG.debug(methodName,  loggerValue);
        }
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        C2STransferVO transferVO = null;
        List<C2STransferVO> transfersList = null;
        StringBuffer strBuff = null;
        int i;
        try {
            transfersList = new ArrayList<C2STransferVO>();
            final String[] services = serviceType.split(",");
            final Date differenceDate = BTSLUtil.getDifferenceDate(new Date(), -noDays); // for
            // getting
            // diffence
            // date.
            for (int j = 0; j < services.length; j++) {
                if (PretupsI.SERVICE_TYPE_C2S_LAST_X_TRANSFER.equals(services[j])) {
                	LOG.info(methodName,PretupsI.SERVICE_TYPE_C2S_LAST_X_TRANSFER );
                	pstmt = transferEnquiryQry.loadLastXTransfersNewQryIfC2S(p_con, recMsisdn, p_user_id, p_noLastTxn, noDays, txnDate);
                    rs = pstmt.executeQuery();
                    while (rs.next()) {
                        transferVO = new C2STransferVO();
                        transferVO.setTransferID(rs.getString("transfer_id"));
                        transferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                        transferVO.setTransferDateTimeAsString(BTSLDateUtil.getLocaleDateTimeFromDate(rs.getTimestamp("transfer_date_time")));
                        transferVO.setTransferStatus(rs.getString("transfer_status"));
                        transferVO.setType(PretupsI.C2S_MODULE);
                        transferVO.setReceiverMsisdn(rs.getString("receiver_Msisdn"));
                        transferVO.setTransferValue(rs.getLong("net_payable_amount"));
                        transferVO.setCreatedOn(rs.getTimestamp("created_on"));
                        transferVO.setServiceType(rs.getString("service"));
                        transferVO.setServiceName(rs.getString("name"));
                        transferVO.setStatus(rs.getString("statusname"));
                        transferVO.setSenderPostBalance(rs.getLong("sender_post_balance"));
                        transferVO.setProductName(rs.getString("SHORT_NAME"));
                        transferVO.setSID(rs.getString("SUBS_SID"));
                        transfersList.add(transferVO);
                    }
                }
                // else
                // modified bt rahuld for korek
                else if (services[j].contains(PretupsI.SERVICE_TYPE_C2C_LAST_X_TRANSFER)) {
                    final String aa[] = services[j].split(":");
                    pstmt1 = transferEnquiryQry.loadLastXTransfersNewQryIfC2C(p_con, recMsisdn, p_user_id, p_noLastTxn, noDays, services[j], txnDate);
                    rs = pstmt1.executeQuery();
                    while (rs.next()) {
                        transferVO = new C2STransferVO();
                        transferVO.setTransferID(rs1.getString("transfer_id"));
                        transferVO.setTransferDateTime(rs1.getTimestamp("CLOSE_DATE"));
                        transferVO.setTransferDateTimeAsString(BTSLDateUtil.getLocaleDateTimeFromDate(rs1.getTimestamp("CLOSE_DATE")));
                        transferVO.setTransferStatus(rs1.getString("status"));
                        transferVO.setType(PretupsI.C2C_MODULE);
                        transferVO.setReceiverMsisdn(rs1.getString("to_msisdn"));
                        transferVO.setSenderMsisdn(rs1.getString("msisdn"));
                        transferVO.setTransferValue(rs1.getLong("net_payable_amount"));
                        transferVO.setCreatedOn(rs1.getTimestamp("created_on"));
                        transferVO.setServiceType(rs1.getString("service")); // set
                        // service
                        // type
                        // here.
                        transferVO.setServiceName(rs1.getString("name")); // from
                        // look-up
                        // set
                        // service
                        // name
                     
                        transferVO.setStatus(rs1.getString("statusname"));
                        final long approvedQuantity = rs1.getLong("approved_quantity");
                        if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(rs1.getString("transfer_sub_type")) || PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(rs1
                            .getString("transfer_sub_type"))) {
                            transferVO.setSenderPostBalance(rs1.getLong("sender_previous_stock") - approvedQuantity);
                        } else {
                            transferVO.setSenderPostBalance(rs1.getLong("receiver_previous_stock") + approvedQuantity);
                        }
                        transfersList.add(transferVO);
                    }
                }
                // else
                else if (services[j].contains(PretupsI.SERVICE_TYPE_O2C_LAST_X_TRANSFER))
                // for o2c
                {
                	pstmt2 = transferEnquiryQry.loadLastXTransfersNewQryIfO2C(p_con, recMsisdn, p_user_id, p_noLastTxn, noDays, services[j], txnDate);
                    rs2 = pstmt2.executeQuery();
                    while (rs2.next()) {
                        transferVO = new C2STransferVO();
                        transferVO.setTransferID(rs2.getString("transfer_id"));
                        transferVO.setTransferDateTime(rs2.getTimestamp("CLOSE_DATE"));
                        transferVO.setTransferDateTimeAsString(BTSLDateUtil.getLocaleDateTimeFromDate(rs2.getTimestamp("CLOSE_DATE")));
                        transferVO.setTransferStatus(rs2.getString("status"));
                        transferVO.setType(PretupsI.TRANSFER_TYPE_O2C);
                        transferVO.setSenderMsisdn(rs2.getString("msisdn"));
                        transferVO.setReceiverMsisdn(rs2.getString("to_msisdn"));
                        transferVO.setTransferValue(rs2.getLong("approved_quantity"));
                        transferVO.setCreatedOn(rs2.getTimestamp("created_on"));
                        transferVO.setServiceType(rs2.getString("service")); 
                     
                        // from
                        // look-up
                        // set
                        // service
                        // name
                        transferVO.setServiceName(rs2.getString("name")); // set
                        // sub
                        // service
                        // type
                        // here.
                        transferVO.setStatus(rs2.getString("statusname"));
                        // for user post balance.
                        final long approvedQuantity = rs2.getLong("approved_quantity");
                        if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(rs2.getString("transfer_sub_type")) || PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(rs2
                            .getString("transfer_sub_type"))) {
                            transferVO.setSenderPostBalance(rs2.getLong("sender_previous_stock") - approvedQuantity);
                        } else {
                            transferVO.setSenderPostBalance(rs2.getLong("receiver_previous_stock") + approvedQuantity);
                        }
                        transfersList.add(transferVO);
                    }
                }

            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            LOG.error(methodName,  loggerValue );
            LOG.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadLastXTransfers]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append( ex);
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append( ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadLastXTransfers]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rs2 != null) {
                    rs2.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt1 != null) {
                    pstmt1.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt2 != null) {
                    pstmt2.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
             	loggerValue.append("Exiting:  transfersList =");
             	loggerValue.append( transfersList.size());
                LOG.debug(methodName,  loggerValue);
            }
        }
        return transfersList;
    }
	public List<TransferVO> loadLastNDaysEVDTrfDetails(Connection p_con, int noDays, String serviceType, String senderMsisdn, String serialNumber, String denomination) throws BTSLBaseException{
    	final String methodName = "loadLastNDaysEVDTrfDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered: noDays: " + noDays + " serviceType: " + serviceType + " senderMsisdn: " + senderMsisdn + " serailNumber: " + serialNumber + " denomination: " + denomination);
        }
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        TransferVO transferVO = null;
        List<TransferVO> transferVOList = null;
        
        // date till transactions are needed 
        LocalDate transfer_date = LocalDate.now().minusDays(noDays);
        
        System.out.println("From transfer date: " + transfer_date);
        
       /* final StringBuffer sbr = new StringBuffer();
        sbr.append("SELECT TO_CHAR(TRANSFER_DATE, ?) TRANSFER_DATE, SERIAL_NUMBER, (TRANSFER_VALUE/").append(SystemPreferences.AMOUNT_MULT_FACTOR).append(") TRANSFER_VALUE ");
        sbr.append("FROM C2S_TRANSFERS WHERE SERVICE_TYPE = ? AND SENDER_MSISDN = ? ");
        sbr.append("AND TRANSFER_DATE >= ? ");
        if(!BTSLUtil.isNullString(serialNumber)) {
        	sbr.append("AND SERIAL_NUMBER = ? ");
        }
        if(!BTSLUtil.isNullString(denomination)) {
        	sbr.append("AND TRANSFER_VALUE = ? ");
        }
        sbr.append("ORDER BY TRANSFER_DATE DESC");
        String sqlSelect = sbr.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "sql query = " + sqlSelect);
        }
        
        try {
        	int i = 1;
        	 pstmt = p_con.prepareStatement(sqlSelect);
        	 pstmt.setString(i++, Constants.getProperty("report.onlydateformat"));
        	 pstmt.setString(i++, serviceType);
        	 pstmt.setString(i++, senderMsisdn);
        	 pstmt.setDate(i++,java.sql.Date.valueOf(transfer_date.toString()));
        	 if(!BTSLUtil.isNullString(serialNumber)) {
        		 pstmt.setString(i++, serialNumber);
        	 }
        	 if(!BTSLUtil.isNullString(denomination)) {
        		 pstmt.setLong(i++, (Long.parseLong(denomination)*SystemPreferences.AMOUNT_MULT_FACTOR));
        	 }*/
        try {
        	pstmt = transferEnquiryQry.loadLastNDaysEVDTrfDetailsQry(p_con, noDays, serviceType, senderMsisdn, serialNumber, denomination, transfer_date);
        	 rs = pstmt.executeQuery();
        	 transferVOList = new ArrayList<TransferVO>();
        	 
        	 while(rs.next()) {
        		 transferVO = new TransferVO();
        		 transferVO.setTransferDateStr(rs.getString("TRANSFER_DATE")); 
        		 transferVO.setSerialNumber(rs.getString("SERIAL_NUMBER"));
        		 transferVO.setTransferValueStr(Double.toString(Double.parseDouble(rs.getString("TRANSFER_VALUE"))/SystemPreferences.AMOUNT_MULT_FACTOR));
        		 transferVOList.add(transferVO);
        	 }
        	 rs.close();
        }catch(SQLException sqe) {
        	LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadEVDEnqDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        }catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadEVDEnqDetails]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }finally {
        	try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: ");
            }
            
        }
        return transferVOList;
    }
    
    
}
