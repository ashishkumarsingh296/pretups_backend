package com.txn.pretups.channel.transfer.businesslogic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.OfflineReportRunningThreadMap;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.AdditionalCommissionSummryC2SResp;
import com.btsl.pretups.channel.transfer.businesslogic.AddtnlCommSummaryRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.AddtnlCommSummryReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransactionDetails;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2StransferCommRespDTO;
import com.btsl.pretups.channel.transfer.businesslogic.C2StransferCommSummryData;
import com.btsl.pretups.channel.transfer.businesslogic.C2StransferCommisionRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.DownloadDataFomatReq;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersRespDTO;
import com.btsl.pretups.channel.transfer.requesthandler.PretupsUIReportsController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRptUIConsts;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.ibm.icu.util.Calendar;
import com.opencsv.CSVWriter;

/**
 * @author deepa.shyam
 *
 */
public class C2STransferTxnDAO {
	private static Log LOG = LogFactory.getLog(C2STransferTxnDAO.class
			.getName());
	private static C2STransferTxnQry c2STransferTxnQry;
	private static final String SQL_EXCEPTION = "SQL EXCEPTION: ";
	private static final String EXCEPTION = " Exception : ";

	public C2STransferTxnDAO() {
		super();
		c2STransferTxnQry = (C2STransferTxnQry) ObjectProducer.getObject(
				QueryConstants.C2S_TRANSFER_TXN_QRY,
				QueryConstants.QUERY_PRODUCER);
	}

	public static OperatorUtilI _operatorUtilI = null;
	static {
		try {
			_operatorUtilI = (OperatorUtilI) Class
					.forName(
							(String) PreferenceCache
									.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS))
					.newInstance();
		} catch (Exception e) {

			LOG.errorTrace("static", e);
			EventHandler.handle(
					EventIDI.SYSTEM_ERROR,
					EventComponentI.SYSTEM,
					EventStatusI.RAISED,
					EventLevelI.FATAL,
					"BuddyMgtAction",
					"",
					"",
					"",
					"Exception while loading the operator util class in class :"
							+ C2STransferDAO.class.getName() + ":"
							+ e.getMessage());
		}
	}

	/**
	 * This method load channelTransferVO of last transfer ID F
	 * 
	 * @author manoj
	 * @param p_con
	 * @param p_lastTransferID
	 *            java.lang.String
	 * @return ChannelTransferItmsVO
	 * @throws BTSLBaseException
	 */
	public C2STransferVO loadLastTransfersStatusVOForC2S(Connection p_con,
			String p_lastTransferID) throws BTSLBaseException {
		//local_index_implemented
		final String methodName = "loadLastTransfersStatusVOForC2S";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered   p_lastTransferID "
					+ p_lastTransferID);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		C2STransferVO transferVO = null;
		//local_index_implemented.
		StringBuffer strBuff = new StringBuffer(
				" SELECT C2S.transfer_id,C2S.sender_id, C2S.service_type,C2S.sender_msisdn,C2S.receiver_msisdn,C2S.transfer_value,C2S.transfer_status,C2S.receiver_transfer_value,C2S.receiver_access_fee,C2S.sender_post_balance, ");
		strBuff.append(" KV.value,C2S.transfer_date_time,C2S.SUBS_SID,P.short_name,P.product_short_code,ST.name ");
		strBuff.append(" FROM c2s_transfers C2S,products P, key_values KV,service_type ST ");
		strBuff.append(" WHERE transfer_date =? AND transfer_id =? AND C2S.product_code=P.product_code AND C2S.transfer_status=KV.key AND KV.type=?  ");
		strBuff.append(" AND C2S.service_type=ST.service_type AND ST.module=? ");
		String sqlSelect = strBuff.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		try {
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_lastTransferID)));
			pstmt.setString(2, p_lastTransferID);
			pstmt.setString(3, PretupsI.KEY_VALUE_C2C_STATUS);
			pstmt.setString(4, PretupsI.C2S_MODULE);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				transferVO = new C2STransferVO();
				transferVO.setTransferID(rs.getString("transfer_id"));
				transferVO.setSenderID(rs.getString("sender_id"));
				transferVO.setServiceType(rs.getString("name"));
				transferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
				transferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
				transferVO.setTransferValue(rs.getLong("transfer_value"));
				transferVO.setTransferStatus(rs.getString("transfer_status"));
				transferVO.setTransferDateTime(rs
						.getTimestamp("transfer_date_time"));
				transferVO.setTransferDateTimeAsString(BTSLDateUtil.getLocaleDateTimeFromDate(rs
				.getTimestamp("transfer_date_time")));
				transferVO.setProductName(rs.getString("short_name"));
				transferVO.setProductShortCode(rs
						.getString("product_short_code"));
				transferVO.setReceiverTransferValue(rs
						.getLong("receiver_transfer_value"));
				transferVO.setValue(rs.getString("value"));
				transferVO.setSID(rs.getString("SUBS_SID"));
				   transferVO.setReceiverAccessFee(rs.getLong("receiver_access_fee"));
	                transferVO.setPostBalance(rs.getLong("sender_post_balance"));
	           
			}
		} catch (SQLException sqe) {
			LOG.error(methodName, "SQLException : " + sqe);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2STransferDAO[loadLastTransfersStatusVOForC2S]", "", "",
					"", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "",
					"error.general.sql.processing");
		} catch (Exception ex) {
			LOG.error(methodName, "Exception : " + ex);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2STransferDAO[loadLastTransfersStatusVOForC2S]", "", "",
					"", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		} finally {
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
				LOG.debug(methodName, "Exiting:  transferVO =" + transferVO);
			}
		}
		return transferVO;
	}

	/**
	 * This method load transaction details using external reference num 
	 * 
	 * @author vipul
	 * @param p_con
	 * @param p_referenceID
	 *            java.lang.String
	 * @return ChannelTransferItmsVO
	 * @throws BTSLBaseException
	 */
	public C2STransferVO loadLastTransfersStatusVOForC2SWithExtRefNum(
			Connection p_con, String p_referenceID, RequestVO p_requestVO)
			throws BTSLBaseException {
		final String methodName = "loadLastTransfersStatusVOForC2SWithExtRefNum";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered   p_referenceID=" + p_referenceID
					+ " p_requestVO=" + p_requestVO);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		C2STransferVO transferVO = null;

		final String sqlSelect = c2STransferTxnQry
				.loadLastTransfersStatusVOForC2SWithExtRefNumQry();

		java.sql.Date createdOnDate = BTSLUtil
				.getSQLDateFromUtilDate(p_requestVO.getCreatedOn());
		java.sql.Date previousdate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil
				.addDaysInUtilDate(createdOnDate, -3));

		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY createdOnDate=" + createdOnDate
					+ " previousdate=" + previousdate);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		try {
			int i = 1;
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setDate(i++, previousdate);
			pstmt.setDate(i++, createdOnDate);
			pstmt.setString(i++, p_referenceID);
			pstmt.setString(i++, PretupsI.KEY_VALUE_C2C_STATUS);
			pstmt.setString(i++, PretupsI.C2S_MODULE);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				transferVO = new C2STransferVO();
				transferVO.setTransferID(rs.getString("transfer_id"));
				transferVO.setSenderID(rs.getString("sender_id"));
				transferVO.setServiceType(rs.getString("name"));
				transferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
				transferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
				transferVO.setTransferValue(rs.getLong("transfer_value"));
				transferVO.setTransferStatus(rs.getString("transfer_status"));
				transferVO.setTransferDateTime(rs
						.getTimestamp("transfer_date_time"));
				transferVO.setTransferDateTimeAsString(BTSLDateUtil.getLocaleDateTimeFromDate(rs
						.getTimestamp("transfer_date_time")));		
				transferVO.setProductName(rs.getString("short_name"));
				transferVO.setProductShortCode(rs
						.getString("product_short_code"));
				transferVO.setReceiverTransferValue(rs
						.getLong("receiver_transfer_value"));
				transferVO.setValue(rs.getString("value"));
			    transferVO.setReceiverAccessFee(rs.getLong("receiver_access_fee"));
	            transferVO.setPostBalance(rs.getLong("sender_post_balance"));
	           
			}
		} catch (SQLException sqe) {
			LOG.error(methodName, "SQLException : " + sqe);
			LOG.errorTrace(methodName, sqe);
			EventHandler
					.handle(EventIDI.SYSTEM_ERROR,
							EventComponentI.SYSTEM,
							EventStatusI.RAISED,
							EventLevelI.FATAL,
							"C2STransferDAO[loadLastTransfersStatusVOForC2SWithExtRefNum]",
							"", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "",
					"error.general.sql.processing");
		} catch (Exception ex) {
			LOG.error(methodName, "Exception : " + ex);
			LOG.errorTrace(methodName, ex);
			EventHandler
					.handle(EventIDI.SYSTEM_ERROR,
							EventComponentI.SYSTEM,
							EventStatusI.RAISED,
							EventLevelI.FATAL,
							"C2STransferDAO[loadLastTransfersStatusVOForC2SWithExtRefNum]",
							"", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		} finally {
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
				LOG.debug(methodName, "Exiting:  transferVO =" + transferVO);
			}
		}
		return transferVO;
	}

	/**
	 * This method Load the last N number of C2S transactions done for a
	 * subsriber by a user. author Vikram kumar
	 * 
	 * @param p_con
	 *            Connection
	 * @param p_user_id
	 *            String
	 * @param p_noLastTxn
	 *            int
	 * @param serviceType
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList loadLastXCustTransfers(Connection p_con, String p_user_id,
			int p_noLastTxn, String receiverMsisdn) throws BTSLBaseException {
		final String methodName = "loadLastXCustTransfers";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered   p_user_id " + p_user_id
					+ " p_noLastTxn: " + p_noLastTxn + "receiverMsisdn "
					+ receiverMsisdn);
		}
		Integer maxLastTransferDays = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LAST_TRANSFERS_DAYS);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		C2STransferVO transferVO = null;
		ArrayList transfersList = null;
		final Calendar cal = BTSLDateUtil.getInstance();
		java.util.Date dt = cal.getTime(); // Current Date
		try {
			dt = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(BTSLUtil.addDaysInUtilDate(dt, - (int)maxLastTransferDays), PretupsI.DATE_FORMAT));
			transfersList = new ArrayList();
			pstmt = c2STransferTxnQry.loadLastXCustTransfersQry(p_con, p_user_id, p_noLastTxn, receiverMsisdn, dt);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				transferVO = new C2STransferVO();
				transferVO.setTransferID(rs.getString("transfer_id"));
				transferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
				transferVO.setTransferValue(rs.getLong("net_payable_amount"));
				transferVO.setServiceType(rs.getString("service"));
				transferVO.setTransferStatus(rs.getString("transfer_status"));
				transferVO.setStatus(rs.getString("statusname"));
				transfersList.add(transferVO);
			}
		} catch (SQLException sqe) {
			LOG.error(methodName, "SQLException : " + sqe);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2STransferDAO[loadLastXCustTransfers]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "",
					"error.general.sql.processing");
		} catch (Exception ex) {
			LOG.error(methodName, "Exception : " + ex);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2STransferDAO[loadLastXCustTransfers]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "loadLastXTransfers",
					"error.general.processing");
		} finally {
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
				LOG.debug(methodName, "Exiting:  transfersList ="
						+ transfersList.size());
			}
		}
		return transfersList;
	}

	/**
	 * @param p_con
	 * @param p_roleCode
	 * @param p_channelUserID
	 * @return
	 * @throws BTSLBaseException
	 * @author rahul.dutt this method is used to fetch channel transaction one
	 *         by a retailer to a particular subcriber on a particular date
	 */
	public ArrayList getChanneltransAmtDatewise(Connection p_con,
			String p_networkCode, Date p_fromDate, Date p_toDate,
			String p_senderMsisdn, String p_receiverMsisdn, String p_amount)
			throws BTSLBaseException {

		final String methodName = "getChanneltransAmtDatewise";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered p_networkCode=" + p_networkCode
					+ " p_senderMsisdn:" + p_senderMsisdn + " p_fromDate:"
					+ p_fromDate + " p_toDate:" + p_toDate
					+ " p_receiverMsisdn=" + p_receiverMsisdn + " p_amount:"
					+ p_amount);
		}
		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		C2STransferVO c2sTransferVO = null;
		ArrayList c2sTransferVOList = new ArrayList();
		try {
			pstmtSelect = c2STransferTxnQry.getChanneltransAmtDatewiseQry(
					p_con, p_networkCode, p_fromDate, p_toDate, p_senderMsisdn,
					p_receiverMsisdn, p_amount);
			rs = pstmtSelect.executeQuery();
			while (rs.next()) {
				c2sTransferVO = new C2STransferVO();
				c2sTransferVO.setTransferID(rs.getString("transfer_id"));
				c2sTransferVO.setTransferDate(rs.getDate("transfer_date"));
				c2sTransferVO.setNetworkCode(rs.getString("network_code"));
				c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
				c2sTransferVO
						.setReceiverMsisdn(rs.getString("receiver_msisdn"));
				c2sTransferVO.setTransferStatus(rs.getString("transtatus"));
				c2sTransferVO.setServiceType(rs.getString("service_type"));
				c2sTransferVO.setQuantity(rs.getLong("quantity"));
				c2sTransferVO.setTransferValue(rs.getLong("transfer_value"));
				c2sTransferVO.setServiceName(rs.getString("servicename"));
				c2sTransferVO.setTransferDateTime(rs
						.getTimestamp("transfer_date_time"));
				c2sTransferVO.setTransferDateStr(BTSLUtil
						.getDateTimeStringFromDate(rs
								.getTimestamp("transfer_date_time")));
				c2sTransferVO.setErrorCode(rs.getString("error_code"));
				c2sTransferVOList.add(c2sTransferVO);
			}
		}// end of try
		catch (SQLException sqle) {
			LOG.error(methodName, "SQLException " + sqle.getMessage());
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2STransferDAO[getChanneltransAmtDatewise]", "", "", "",
					"SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		}// end of catch

		catch (Exception e) {
			LOG.error(methodName, "Exception " + e.getMessage());
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2STransferDAO[getChanneltransAmtDatewise]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		}// end of catch
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelect != null) {
					pstmtSelect.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting c2sTransferVOList.size()="
						+ c2sTransferVOList.size());
			}
		}// end of finally

		return c2sTransferVOList;
	}
	
	/**
	 * This method Load the last N number of C2S transactions done for a
	 * subsriber by a user. 
	 * 
	 * @param con
	 *            Connection
	 * @param senderMsisdn
	 *            String
	 * @param receiverMsisdn
	 *            String
	 * @return C2STransferVO
	 * @throws BTSLBaseException
	 */
	public C2STransferVO loadLastC2STransfersBySubscriberMSISDN(Connection con, String senderMsisdn, String receiverMsisdn, Date fromDate) throws BTSLBaseException {
        final String methodName = "loadLastC2STransfersBySubscriberMSISDN";
        if(LOG.isDebugEnabled()){
            LOG.debug(methodName, "Entered   senderMsisdn " + senderMsisdn + " receiverMsisdn "+ receiverMsisdn);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        C2STransferVO transferVO = null;

        try {
            pstmt = c2STransferTxnQry.loadLastC2STransfersBySubscriberMSISDNQry(con,senderMsisdn, receiverMsisdn, fromDate);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                transferVO = new C2STransferVO();
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setSenderID(rs.getString("sender_id"));
                transferVO.setServiceType(rs.getString("name"));
                transferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                transferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                transferVO.setTransferValue(rs.getLong("transfer_value"));
                transferVO.setTransferStatus(rs.getString("transfer_status"));
                transferVO.setTransferDateTime(rs.getTimestamp("created_on"));
                transferVO.setProductName(rs.getString("short_name"));
                transferVO.setProductShortCode(rs.getString("product_short_code"));
                transferVO.setReceiverTransferValue(rs.getLong("receiver_transfer_value"));
                transferVO.setValue(rs.getString("value"));
			}
		} catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,EventStatusI.RAISED, EventLevelI.FATAL,
					"C2STransferDAO[loadLastC2STransfersBySubscriberMSISDN]", "", "", "","SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "","error.general.sql.processing");
		} catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,EventStatusI.RAISED, EventLevelI.FATAL,
					"C2STransferDAO[loadLastC2STransfersBySubscriberMSISDN]", "", "", "","Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName,"error.general.processing");
		} finally {
            try {
            	try{
            		if (rs!= null){
            			rs.close();
            		}
            	}
            	catch (SQLException e){
            		LOG.error("An error occurred closing result set.", e);
            	}
			} catch (Exception e) {
                LOG.errorTrace(methodName, e);
			}
            try {
            	try{
                	if (pstmt!= null){
                		pstmt.close();
                	}
                }
                catch (SQLException e){
                	LOG.error("An error occurred closing statement.", e);
                }
			} catch (Exception e) {
                LOG.errorTrace(methodName, e);
			}

            if(LOG.isDebugEnabled()){
                LOG.debug(methodName, "Exiting: ");
            }
		}
        return transferVO;
	}
	
	/**
	 * This method load total sales done by the user on current day 
	 * 
	 * @author 
	 * @param con
	 * @param userID java.lang.String
	 * @param startDate
	 * @param serviceType
	 * @param subService
	 * @return totalSales
	 * @throws BTSLBaseException
	 */
	public double loadTotalSalesToday(Connection con, String userID, Date startDate, String serviceType, String subService) throws BTSLBaseException {
		//local_index_implemented
        final String methodName = "loadTotalSalesToday";
        if(LOG.isDebugEnabled()){
            LOG.debug(methodName, "Entered   userID = "+userID+" startDate = "+startDate+" serviceType = "+serviceType+" subService = "+subService);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double totalSales = 0;
        StringBuilder strBuff = new StringBuilder(" SELECT sum(transfer_value) as totalsales from c2s_transfers where TRANSFER_DATE = ? AND transfer_status = ? ");
        strBuff.append(" AND SENDER_ID = ? AND SERVICE_TYPE = ? ");
        if(subService==null){
            strBuff.append("AND SUB_SERVICE = ? ");
		}
        String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(startDate));
            pstmt.setString(2, PretupsI.TXN_STATUS_SUCCESS);
            pstmt.setString(3, userID);
            pstmt.setString(4, serviceType);
            if(subService==null){
                pstmt.setString(5, subService);
			}
            rs = pstmt.executeQuery();
            if (rs.next()) {
                totalSales=rs.getDouble("totalsales");
			}
		} catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,EventStatusI.RAISED, EventLevelI.FATAL, 
            		"C2STransferDAO[loadTotalSalesToday]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
		} catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, 
            		"C2STransferDAO[loadTotalSalesToday]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
			try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
            if(LOG.isDebugEnabled()){
                LOG.debug(methodName, "Exiting:  totalSales = "+totalSales);
            }
		}
        return totalSales;
	}

	
	/**
	 * This method load total sales done by the user on current day 
	 * 
	 * @author 
	 * @param con
	 * @param userID java.lang.String
	 * @param fromDate
	 * @param toDate
	 * @param serviceType
	 * @param subService
	 * @return totalSales
	 * @throws BTSLBaseException
	 */
	public double loadTotalSalesFromMIS(Connection con, String userID, Date fromDate, Date toDate, String serviceType, String subService) throws BTSLBaseException {
        final String methodName = "loadTotalSalesFromMIS";
        if(LOG.isDebugEnabled()){
            LOG.debug(methodName, "Entered   userID = "+userID+" fromDate = "+fromDate+" toDate = "+toDate + "serviceType = "+serviceType+" subService = "+subService);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double totalSales = 0;
        StringBuilder strBuff = new StringBuilder(" SELECT sum(TRANSACTION_AMOUNT) as totalsales from DAILY_C2S_TRANS_DETAILS where TRANS_DATE >= ? ");
        strBuff.append("AND TRANS_DATE <= ? AND USER_ID = ? AND SERVICE_TYPE = ? ");
        if(subService==null){
            strBuff.append("AND SUB_SERVICE = ? ");
		}
        String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(fromDate));
            pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(toDate));
            pstmt.setString(3, userID);
            pstmt.setString(4, serviceType);
            if(subService==null){
                pstmt.setString(5, subService);
			}
            rs = pstmt.executeQuery();
            if (rs.next()) {
                totalSales=rs.getDouble("totalsales");
			}
		} catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,EventStatusI.RAISED, EventLevelI.FATAL, 
            		"C2STransferDAO[loadTotalSalesFromMIS]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
		} catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, 
            		"C2STransferDAO[loadTotalSalesFromMIS]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
			try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
            if(LOG.isDebugEnabled()){
                LOG.debug(methodName, "Exiting:  totalSales = "+totalSales);
            }
		}
        return totalSales;
	}


	
	/**
	 * @param con
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param ServiceType
	 * @param Value
	 * @return
	 * @throws BTSLBaseException
	 */
	public HashMap<String, Object> getC2SnProdTxnDetails(Connection con,String userId,Date fromDate,Date toDate,String ServiceType,String value) throws BTSLBaseException{
    	final String methodName="getC2SnProdTxnDetails";
    	StringBuffer p_data= new StringBuffer();
		 if (LOG.isDebugEnabled()){
			 p_data.append("Entered  with userId:").append(userId);
			 p_data.append(" fromDate: ").append(fromDate);
			 p_data.append(" toDate: ").append(toDate);
			 p_data.append(" ServiceType: ").append(ServiceType);
			 p_data.append(" value: ").append(value);
	         LOG.debug(methodName, "Entered  with p_data" + p_data.toString());
		 }
    	 PreparedStatement pstmt = null;
         ResultSet rs = null;
         C2STransactionDetails c2sTransactionDetails ;
         ArrayList<C2STransactionDetails> transactionList=new ArrayList<C2STransactionDetails>();
         LinkedHashMap<String, Object> resultMap = new LinkedHashMap<String,Object>();
          long totalCount =0;
          long totalValue =0 ;
         try
         {
        	 pstmt = c2STransferTxnQry.getC2SnProdTxnDetails(con, userId, fromDate, toDate, ServiceType, value);
             rs = pstmt.executeQuery();
             while (rs.next()) {
            	 c2sTransactionDetails= new C2STransactionDetails();
            	 c2sTransactionDetails.setTransferCount(rs.getString("transferCount"));
            	 c2sTransactionDetails.setTransferValue(PretupsBL.getDisplayAmount(rs.getLong("transferValue")));
            	 c2sTransactionDetails.setAmount(PretupsBL.getDisplayAmount(rs.getLong("amount")));
            	 transactionList.add(c2sTransactionDetails);	   
            	 totalCount += Long.parseLong(c2sTransactionDetails.getTransferCount());
            	 totalValue += Long.parseLong(c2sTransactionDetails.getTransferValue());
            	 }
             resultMap.put("fromDate", fromDate);
             resultMap.put("toDate", toDate);
             resultMap.put("totalCount", totalCount);
             resultMap.put("totalValue", totalValue);
             resultMap.put("data", transactionList);
            
         }
         catch (SQLException sqle)
 		 {
 			LOG.error(methodName,"SQLException "+sqle.getMessage());
 			LOG.errorTrace(methodName, sqle);
 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2STransferDAO["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
 			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
 		}//end of catch
 		catch (Exception e)
 		{
 			LOG.error(methodName,"Exception "+e.getMessage());
 			LOG.errorTrace(methodName, e);
 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2STransferDAO["+methodName+"]","","","","Exception:"+e.getMessage());
 			throw new BTSLBaseException(this, methodName, "error.general.processing");
 		}//end of catch
 		finally
 		{
 			try{if(rs!=null) rs.close();}catch(Exception e){
 				LOG.error(methodName, "Exception:e=" + e);
 	            LOG.errorTrace(methodName, e);	
 			}
 			try{if(pstmt!=null) pstmt.close();}
 			catch(Exception e){
 				LOG.error(methodName, "Exception:e=" + e);
 	            LOG.errorTrace(methodName, e);	
 			}
 			if(LOG.isDebugEnabled())LOG.debug(methodName,"Exiting resultMap ="+resultMap);
 		 }//end of finally
 	    return resultMap;	 	    
 	
 }
	
	/**
	 * @param con
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param ServiceType
	 * @param amount
	 * @return
	 * @throws BTSLBaseException
	 */
	public ArrayList<C2STransactionDetails> getC2STxnDetailsByAmt(Connection con,String userId,Date fromDate,Date toDate,String ServiceType,String amount) throws BTSLBaseException{


    	final String methodName="getC2STxnDetailsByAmt";
		 if (LOG.isDebugEnabled())
             LOG.debug(methodName, "Entered  with userId" + userId);
    	 PreparedStatement pstmt = null;
         ResultSet rs = null;
         C2STransactionDetails c2sTransactionDetails ;
         ArrayList<C2STransactionDetails> transactionList=new ArrayList<C2STransactionDetails>();
         String[] values= amount.split(",");
         StringBuilder strBuff = new StringBuilder("SELECT CT.TRANSFER_DATE,COUNT( CT.TRANSFER_VALUE ) AS totalCount,SUM( ct.TRANSFER_VALUE ) AS totalValue");
         strBuff.append(" FROM C2S_TRANSFERS CT,SERVICE_TYPE ST,users u WHERE ");
         strBuff.append(" u.USER_ID = CT.SENDER_ID AND u.USER_ID =? AND ct.TRANSFER_DATE BETWEEN ? AND ? ");
         strBuff.append(" AND ST.SERVICE_TYPE = ? AND CT.SERVICE_TYPE = ST.SERVICE_TYPE  AND CT.TRANSFER_STATUS = '200' ");
         strBuff.append(" AND TRANSFER_VALUE in ( ");
         for(int i =0;i<values.length;i++)
         {
        	 if(i==values.length-1){
        		 strBuff.append(" ? ");
        	 }else{
        		 strBuff.append(" ? ,");
        	 }
        	 
         }
         strBuff.append(" ) GROUP BY TRANSFER_DATE ORDER BY TRANSFER_DATE ");
         if (LOG.isDebugEnabled())
             LOG.debug(methodName, "select Query=" +strBuff);
         try
         {
        	 pstmt = con.prepareStatement(strBuff.toString());	            
        	 int i = 1;
        	 pstmt.setString(i++, userId);
        	 pstmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(fromDate));
        	 pstmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(toDate));
        	 pstmt.setString(i++, ServiceType);
        	 for(int j =0;j<values.length;j++)
             {
        		 pstmt.setInt(i++, Integer.parseInt(values[j]));
             }
        	 
             rs = pstmt.executeQuery();
             while (rs.next()) {
            	 c2sTransactionDetails= new C2STransactionDetails();
            	 c2sTransactionDetails.setTransferCount(rs.getString("totalCount"));
            	 c2sTransactionDetails.setTransferValue(PretupsBL.getDisplayAmount(rs.getLong("totalValue")));
            	 c2sTransactionDetails.setTransferdate(rs.getDate("TRANSFER_DATE"));
            	 transactionList.add(c2sTransactionDetails);	   
            	 }
         }
         catch (SQLException sqle)
 		 {
 			LOG.error(methodName,"SQLException "+sqle.getMessage());
 			LOG.errorTrace(methodName, sqle);
 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2STransferDAO["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
 			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
 		}//end of catch
 		catch (Exception e)
 		{
 			LOG.error(methodName,"Exception "+e.getMessage());
 			LOG.errorTrace(methodName, e);
 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2STransferDAO["+methodName+"]","","","","Exception:"+e.getMessage());
 			throw new BTSLBaseException(this, methodName, "error.general.processing");
 		}//end of catch
 		finally
 		{
 			try{if(rs!=null) rs.close();}catch(Exception e){
 				LOG.error(methodName, "Exception:e=" + e);
 	            LOG.errorTrace(methodName, e);	
 			}
 			try{if(pstmt!=null) pstmt.close();}
 			catch(Exception e){
 				LOG.error(methodName, "Exception:e=" + e);
 	            LOG.errorTrace(methodName, e);	
 			}
 			if(LOG.isDebugEnabled())LOG.debug(methodName,"Exiting transactionList.size() ="+transactionList.size());
 		 }//end of finally
 	    return transactionList;	 	    
 	
 }
	
	/**
	 * @param con
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param ServiceType
	 * @param value
	 * @return
	 * @throws BTSLBaseException
	 */
	public HashMap<String, Object> getC2SnProdTxnDetailsCount(Connection con,String userId,Date fromDate,Date toDate,String ServiceType,String value) throws BTSLBaseException{
    	final String methodName="getC2SnProdTxnDetailsCount";
		 if (LOG.isDebugEnabled())
             LOG.debug(methodName, "Entered  with userId" + userId);
    	 PreparedStatement pstmt = null;
         ResultSet rs = null;
         LinkedHashMap<String, Object> resultMap = new LinkedHashMap<String,Object>();
         StringBuilder strBuff = new StringBuilder("SELECT COUNT( CT.TRANSFER_VALUE ) AS totalCount,SUM( ct.TRANSFER_VALUE ) AS totalValue");
         strBuff.append(" FROM C2S_TRANSFERS CT,SERVICE_TYPE ST,users u WHERE ");
         strBuff.append(" u.USER_ID = CT.SENDER_ID AND u.USER_ID =? AND ct.TRANSFER_DATE BETWEEN ? AND ? ");
         strBuff.append(" AND ST.SERVICE_TYPE = ? AND CT.SERVICE_TYPE = ST.SERVICE_TYPE  AND CT.TRANSFER_STATUS = '200' ");
         strBuff.append(" AND TRANSFER_VALUE = ? ");
         
         if (LOG.isDebugEnabled())
             LOG.debug(methodName, "select Query=" +strBuff);
         try
         {
        	 pstmt = con.prepareStatement(strBuff.toString());	            
        	 int i = 1;
        	 long finalSuccessCount=0;
        	 long finalSuccessValue=0;
        	 pstmt.setString(i++, userId);
        	 pstmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(fromDate));
        	 pstmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(toDate));
        	 pstmt.setString(i++, ServiceType);
        	 pstmt.setInt(i++, Integer.parseInt(value));
             rs = pstmt.executeQuery();
             if (rs.next()) {
            	 finalSuccessCount = rs.getLong("totalCount");
            	 finalSuccessValue = Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("totalValue")));
             }
             resultMap.put("fromDate", fromDate);
             resultMap.put("toDate", toDate);
             resultMap.put("totalCount", finalSuccessCount);
             resultMap.put("totalValue", finalSuccessValue);
         }
         catch (SQLException sqle)
 		 {
 			LOG.error(methodName,"SQLException "+sqle.getMessage());
 			LOG.errorTrace(methodName, sqle);
 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2STransferDAO["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
 			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
 		}//end of catch
 		catch (Exception e)
 		{
 			LOG.error(methodName,"Exception "+e.getMessage());
 			LOG.errorTrace(methodName, e);
 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2STransferDAO["+methodName+"]","","","","Exception:"+e.getMessage());
 			throw new BTSLBaseException(this, methodName, "error.general.processing");
 		}//end of catch
 		finally
 		{
 			try{if(rs!=null) rs.close();}catch(Exception e){
 				LOG.error(methodName, "Exception:e=" + e);
 	            LOG.errorTrace(methodName, e);	
 			}
 			try{if(pstmt!=null) pstmt.close();}
 			catch(Exception e){
 				LOG.error(methodName, "Exception:e=" + e);
 	            LOG.errorTrace(methodName, e);	
 			}
 			if(LOG.isDebugEnabled())LOG.debug(methodName,"Exiting resultMap ="+resultMap);
 		 }//end of finally
 	    return 	resultMap;	    
 	
 }
	
	
	
	
	
	/**
	 * This method get data for Channel to subscriber commission info data. 
	 * 
	 * @author Subesh KCV
	 * @param p_con
	 * @param c2STransferCommReqDTO
	 *         
	 * @return List<C2StransferCommisionRecordVO>
	 * @throws BTSLBaseException
	 */
	public C2StransferCommRespDTO searchC2STransferCommissionData(Connection p_con,C2STransferCommReqDTO  c2STransferCommReqDTO,DownloadDataFomatReq downloadDataFomatReq,Workbook workbook, Sheet sheet,CSVWriter csvWriter,ByteArrayOutputStream byteArrayOutputStream)
			throws BTSLBaseException {
		final String methodName = "searchC2STransferCommissionData";
		
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, c2STransferCommReqDTO.toString());
		}
		PreparedStatement pstmt = null;
		
		C2STransferVO transferVO = null;
		

		 Long totalRequestedAmount=0l;
		 Long totalCreditedAmount=0l;
		Long totalbonusAmount=0l;
		Long totalTransferAmount=0l;
		Long totaltransferCount=0l;

		final String sqlSelect = c2STransferTxnQry
				.getC2STransferCommissiondetails(c2STransferCommReqDTO);
	    
	    
	    if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, sqlSelect);
		}
	    
	    StringBuilder msg = new StringBuilder();
	    int continueLastRow=0;// For xlsx writing...
	    Long totalRecordsCount=0l;
        List<C2StransferCommisionRecordVO> listC2STransferCommissionRecordVO = new ArrayList<>();
		C2StransferCommRespDTO c2StransferCommRespDTO = new C2StransferCommRespDTO();
		continueLastRow=downloadDataFomatReq.getContinueLastRow().intValue();// For csv writing....
        try {
        	
        	pstmt = p_con.prepareStatement(sqlSelect.toString());
        	//pstmt.setFetchSize(100);
        	int i = 0;
        	UserDAO userDAO = new UserDAO();
        	String activeUserID =null;
        	if(!BTSLUtil.isNullString(c2STransferCommReqDTO.getUserType())  && c2STransferCommReqDTO.getUserType().trim().equals(PretupsI.STAFF_USER_TYPE) ) {
        		if(c2STransferCommReqDTO.getUserType()!=null && c2STransferCommReqDTO.getUserType().equals(PretupsI.STAFF_USER_TYPE) ) {
        		   	 if(c2STransferCommReqDTO.getOptionStaff_LoginIDOrMsisdn()!=null && c2STransferCommReqDTO.getOptionStaff_LoginIDOrMsisdn().equals(PretupsI.OPTION_LOGIN_ID) ) {
        		   		  if(BTSLUtil.isNullObject( c2STransferCommReqDTO.getLoginIDOrMsisdn())){
        		   			  throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
        									PretupsErrorCodesI.STAFF_LOGIN_ID_MANDATORY); 
        		   		  }
        		   		  ChannelUserVO channUserVO =userDAO.loadUserDetailsByLoginId(p_con, c2STransferCommReqDTO.getLoginIDOrMsisdn());
        		   		  if(BTSLUtil.isNullObject(channUserVO)){
        		   			  throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
        									PretupsErrorCodesI.INVALID_STAFF_LOGIN_ID); 
        		   		  }
        		   		activeUserID=channUserVO.getUserID();
        		   	 }else {
        		   	  if(BTSLUtil.isNullObject(c2STransferCommReqDTO.getLoginIDOrMsisdn())){
        					  throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
        								PretupsErrorCodesI.STAFF_MOBILE_NUM_MANDATORY); 
        				  }
        		 		  ChannelUserVO channUserVO =userDAO.loadUserDetailsByMsisdn(p_con, c2STransferCommReqDTO.getLoginIDOrMsisdn());
        				  if(BTSLUtil.isNullObject(channUserVO)){
        					  throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
        								PretupsErrorCodesI.INVALID_STAFF_MSISDN); 
        				  }
        				  activeUserID=channUserVO.getUserID();
        				 }
        			}
        		
        		++i;
            	pstmt.setString(i,activeUserID);
            	
            	
          	 }else  if(!BTSLUtil.isNullString(c2STransferCommReqDTO.getUserType())  && c2STransferCommReqDTO.getUserType().trim().equals(PretupsI.CHANNEL_USER_TYPE) ) {
          		 if(c2STransferCommReqDTO.getReqTab().equals(PretupsI.C2C_ADVANCED_TAB_REQ)) {
          		    if (c2STransferCommReqDTO.getChannelUserID()!= null && !c2STransferCommReqDTO.getChannelUserID().equals(PretupsI.ALL) ) {
	          			 ChannelUserVO channUserVO =userDAO.loadUserDetailsByLoginId(p_con, c2STransferCommReqDTO.getChannelUserID());
	          			activeUserID = channUserVO.getUserID();
	          			++i;
	                	pstmt.setString(i,activeUserID ); 	 
          		    }
          		 }else {
          			ChannelUserVO channUserVO =userDAO.loadUserDetailsByMsisdn(p_con, c2STransferCommReqDTO.getMobileNumber());
          			activeUserID = channUserVO.getUserID();
          			++i;
                	pstmt.setString(i,activeUserID ); 	 
          		 }
          		 
          		
          	 } else {
          		 if (LOG.isDebugEnabled()) {
         			LOG.debug(methodName, "User type selected : ALL" );
         		}
          	 }
        	
        	++i;
        	pstmt.setString(i,c2STransferCommReqDTO.getTransStatus() );
        	
        	++i;
        	pstmt.setString(i,c2STransferCommReqDTO.getTransStatus() );
        	
        	++i;
        	pstmt.setString(i,c2STransferCommReqDTO.getService() );

        	++i;
        	pstmt.setString(i,c2STransferCommReqDTO.getService() );
        	++i;
        	pstmt.setString(i, c2STransferCommReqDTO.getNetworkCode());
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(BTSLDateUtil.getGregorianDate(c2STransferCommReqDTO.getReportDate() +  PretupsRptUIConsts.REPORT_FROM_TIME.getReportValues())));
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(BTSLDateUtil.getGregorianDate(c2STransferCommReqDTO.getReportDate() +  PretupsRptUIConsts.REPORT_TO_TIME.getReportValues())));
        	++i;
         	pstmt.setString(i, c2STransferCommReqDTO.getCategoryCode());
         	++i;
         	pstmt.setString(i, c2STransferCommReqDTO.getCategoryCode());
         	++i;
         	pstmt.setString(i, c2STransferCommReqDTO.getDomain());
         	++i;
         	pstmt.setString(i, c2STransferCommReqDTO.getGeography());
         	++i;
         	pstmt.setString(i, c2STransferCommReqDTO.getGeography());
            ++i;
           	pstmt.setString(i, c2STransferCommReqDTO.getUserId());
           	if(downloadDataFomatReq.isCheckDataExistforFilterRequest()) {
           		pstmt.setFetchSize(10);	
           	}else {
           		pstmt.setFetchSize(1000);  // Normal Data populating.	
           	}
           	
           	/*
            if(!BTSLUtil.isEmpty(c2STransferCommReqDTO.getSenderMsisdn())  &&  !c2STransferCommReqDTO.getSenderMsisdn().trim().toUpperCase().equals(PretupsI.ALL) ) {
            	++i;
               	pstmt.setString(i, c2STransferCommReqDTO.getSenderMsisdn());
         	    }
             
             if(!BTSLUtil.isEmpty(c2STransferCommReqDTO.getSenderUserID())  &&  !c2STransferCommReqDTO.getSenderUserID().trim().toUpperCase().equals(PretupsI.ALL) ) {
            	 ++i;
                	pstmt.setString(i, c2STransferCommReqDTO.getSenderUserID());
          	    } */
           	C2STransfCommReportWriter  	c2STransfCommReportWriter = new C2STransfCommReportWriter();
               	
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            	
            		while (rs.next()) {
            			totalRecordsCount=totalRecordsCount+1;
            			if(downloadDataFomatReq.isCheckDataExistforFilterRequest()) { // if false , don't check this condition.
            				// For large Report execution info.. , based on record fetchsize 10.
            				break;
            			}
            			
            			C2StransferCommisionRecordVO c2StransferCommisionRecordVO = new C2StransferCommisionRecordVO();
            			
            			c2StransferCommisionRecordVO.setTransdateTime(String.valueOf(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time"), SystemPreferences.SYSTEM_DATETIME_FORMAT)));
            			c2StransferCommisionRecordVO.setTransactionID(rs.getString("TRANSACTION_ID"));
            			//c2StransferCommisionRecordVO.setServices(rs.getString("SERVICES"));
            			c2StransferCommisionRecordVO.setSubService(rs.getString("subservice_name"));
            			c2StransferCommisionRecordVO.setSenderMobileNumber(rs.getString("SENDER_MSISDN"));
            			c2StransferCommisionRecordVO.setSenderName(rs.getString("USER_NAME"));
            			c2StransferCommisionRecordVO.setSenderGeography(rs.getString("SENDER_GEOGRAPHY"));
            			c2StransferCommisionRecordVO.setSenderCategory(rs.getString("SENDER_CATEGORY_NAME"));
            			c2StransferCommisionRecordVO.setReceiverMobileNumber(rs.getString("RECEIVER_MOBILENUM"));
            			c2StransferCommisionRecordVO.setService(rs.getString("SERVICE_NAME"));
            			c2StransferCommisionRecordVO.setRequestedAmount(rs.getString("REQUESTED_AMOUNT"));
            			c2StransferCommisionRecordVO.setStatus(rs.getString("status"));
//            			c2StransferCommisionRecordVO.setSenderNetworkCode(rs.getString("SENDER_NETWORK_NAME"));
            			c2StransferCommisionRecordVO.setParentMobileNumber(rs.getString("parent_msisdn"));
            			c2StransferCommisionRecordVO.setParentName(rs.getString("parent_name"));
            			c2StransferCommisionRecordVO.setParentCategory(rs.getString("Parent_category_name"));
            			c2StransferCommisionRecordVO.setParentGeography(rs.getString("PARENT_GEOGRAPHY"));
            			c2StransferCommisionRecordVO.setOwnerMobileNumber(rs.getString("owner_msisdn")); 
            			c2StransferCommisionRecordVO.setOwnerName(rs.getString("owner_name")); 
            			c2StransferCommisionRecordVO.setOwnerCategory(rs.getString("Owner_Category")); 
            			c2StransferCommisionRecordVO.setOwnerGeography(rs.getString("owner_geography")); 
            			
            			c2StransferCommisionRecordVO.setReceiverMobileNumber(rs.getString("RECEIVER_MOBILENUM"));
            			c2StransferCommisionRecordVO.setReceiverServiceClass(rs.getString("RECEIVER_SERVICECLASS"));
            			c2StransferCommisionRecordVO.setCurrencyDetail(rs.getString("multicurrency_detail"));
            			
            			
            			c2StransferCommisionRecordVO.setRequestSource(rs.getString("REQUEST_SOURCE"));
            			c2StransferCommisionRecordVO.setAdjustmentTransID(rs.getString("adjustment_id"));
            			c2StransferCommisionRecordVO.setCommissionType(rs.getString("commission_Type"));
            			c2StransferCommisionRecordVO.setAdditionalCommission(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("differentialCommission"))));   
            			
            			c2StransferCommisionRecordVO.setCacRate(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("cac_rate"))));
            			
            			c2StransferCommisionRecordVO.setCacType(rs.getString("cac_type"));
            			
            			c2StransferCommisionRecordVO.setCacAmount(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("cac_rate"))));
            			//c2StransferCommisionRecordVO.setRate(rs.getString("MARGIN_RATE"));

            			totalRequestedAmount=totalRequestedAmount+rs.getLong("REQUESTED_AMOUNT");
            			totalCreditedAmount=totalCreditedAmount +rs.getLong("credited_Amount");
            			totalbonusAmount =totalbonusAmount +  rs.getLong("bonus_amount");
            		     totalTransferAmount= totalTransferAmount + rs.getLong("TRANSFER_AMOUNT");
            			//totaltransferCount = totaltransferCount +rs.getLong("TRANSFER_AMOUNT");
            			
            			c2StransferCommisionRecordVO.setRequestedAmount(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("REQUESTED_AMOUNT"))));
            			
            			c2StransferCommisionRecordVO.setCreditedAmount(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("credited_Amount"))));
            			c2StransferCommisionRecordVO.setVoucherserialNo(rs.getString("SERIAL_NUMBER"));
            			c2StransferCommisionRecordVO.setBonus(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("bonus_amount"))));
            			c2StransferCommisionRecordVO.setPinSentTo(rs.getString("pinsentTOmsisdn"));
            			c2StransferCommisionRecordVO.setTransferAmount(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("TRANSFER_AMOUNT"))));
            			
            			
            			c2StransferCommisionRecordVO.setRoamPenalty(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("penalty"))));
            			
            			c2StransferCommisionRecordVO.setProcessingFee(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("receiver_access_fee"))));
            			
            			c2StransferCommisionRecordVO.setExternalCode(rs.getString("external_code"));
//            			c2StransferCommisionRecordVO.setLoginID(rs.getString("LOGIN_ID"));
//            			c2StransferCommisionRecordVO.setDifferentialApplicable(rs.getString("differential_applicable"));
//            			c2StransferCommisionRecordVO.setDifferentialGiven(rs.getString("differential_given"));
//            			c2StransferCommisionRecordVO.setDifferentialCommission(String.valueOf(PretupsBL
//        						.getDisplayAmount(rs.getLong("differentialCommission"))));
           
            			//listC2STransferCommissionRecordVO.add(c2StransferCommisionRecordVO); // To avoid writing lakhs of records into list, can cause memory issue, iteration performance issue.
            			
            			if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(downloadDataFomatReq.getFileType().toUpperCase())
                				|| PretupsI.FILE_CONTENT_TYPE_XLSX.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
            				if(workbook!=null && sheet!=null) {
            					c2STransfCommReportWriter.writeXLSXRow(workbook,sheet,downloadDataFomatReq,continueLastRow,c2StransferCommisionRecordVO);
            					continueLastRow=continueLastRow+1;
            					if(totalRecordsCount%1000==0) {
            						byteArrayOutputStream.flush();            						
            					}
            				}
            				
            			} else if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
            				if(csvWriter!=null) {
            					if(totalRecordsCount%1000==0) {
            						csvWriter.flush();
            					}
                        	c2STransfCommReportWriter.writeCSVRow(csvWriter,downloadDataFomatReq,c2StransferCommisionRecordVO);
            				}
                        	
                        }
            			
            		}
            		
            		if(!downloadDataFomatReq.isCheckDataExistforFilterRequest()) { // if false , don't check this condition.
            		C2StransferCommSummryData c2StransferCommSummryData = new C2StransferCommSummryData();
            		c2StransferCommSummryData.setTotalbonusAmount(String.valueOf(PretupsBL
        						.getDisplayAmount(totalbonusAmount)));
            		c2StransferCommSummryData.setTotalCreditedAmount(String.valueOf(PretupsBL
    						.getDisplayAmount(totalCreditedAmount)));
            		c2StransferCommSummryData.setTotalRequestedAmount(String.valueOf(PretupsBL
    						.getDisplayAmount(totalRequestedAmount)));
            		c2StransferCommSummryData.setTotalTransferAmount(String.valueOf(PretupsBL
    						.getDisplayAmount(totalTransferAmount
    								)));
            		c2StransferCommSummryData.setTotaltransferCount("0");
            		
            		
            
            		c2StransferCommRespDTO.setC2StransferCommSummryData(c2StransferCommSummryData);
            		//c2StransferCommRespDTO.setListC2sTransferCommRecordVO(listC2STransferCommissionRecordVO);
            		c2StransferCommRespDTO.setTotalDownloadedRecords(totalRecordsCount+"");
            		
            		
            		//C2StransferCommSummryData c2StransferCommSummryData = new C2StransferCommSummryData();
            		c2StransferCommSummryData.setTotalbonusAmount(String.valueOf(PretupsBL
        						.getDisplayAmount(totalbonusAmount)));
            		c2StransferCommSummryData.setTotalCreditedAmount(String.valueOf(PretupsBL
    						.getDisplayAmount(totalCreditedAmount)));
            		c2StransferCommSummryData.setTotalRequestedAmount(String.valueOf(PretupsBL
    						.getDisplayAmount(totalRequestedAmount)));
            		c2StransferCommSummryData.setTotalTransferAmount(String.valueOf(PretupsBL
    						.getDisplayAmount(totalTransferAmount
    								)));
            		c2StransferCommSummryData.setTotaltransferCount("0");
            		HashMap<String,String> totSummaryColValue = new HashMap<String,String>();
            		totSummaryColValue.put(C2STransferCommDownloadColumns.BONUS.getColumnName(), c2StransferCommSummryData.getTotalbonusAmount());
    				totSummaryColValue.put(C2STransferCommDownloadColumns.REQUESTED_AMOUNT.getColumnName(), c2StransferCommSummryData.getTotalRequestedAmount());
    				totSummaryColValue.put(C2STransferCommDownloadColumns.CREDITED_AMOUNT.getColumnName(), c2StransferCommSummryData.getTotalCreditedAmount());
    				totSummaryColValue.put(C2STransferCommDownloadColumns.TRANSFER_AMOUNT.getColumnName(), c2StransferCommSummryData.getTotalTransferAmount());
    				
    			   	if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(downloadDataFomatReq.getFileType().toUpperCase())
            				|| PretupsI.FILE_CONTENT_TYPE_XLSX.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
    			 //  	c2STransfCommReportWriter.writeXLSXTotalSummaryColumns(workbook, sheet, downloadDataFomatReq,continueLastRow,totSummaryColValue,totalSummaryCaptureCols);
	    				//workbook.write(outExcel);
    			   	}else {
    			   	c2STransfCommReportWriter.writeCSVTotalSummaryColumns(csvWriter,downloadDataFomatReq,totSummaryColValue);	
    			   	}
       		   		c2StransferCommRespDTO.setC2StransferCommSummryData(c2StransferCommSummryData);
            		}
            		c2StransferCommRespDTO.setTotalDownloadedRecords(String.valueOf(totalRecordsCount));
            		c2StransferCommRespDTO.setLastRecordNo(Long.valueOf(continueLastRow));
            		
            		 
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
        	LOG.error(methodName, msg);
        	LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferTxnDAO[searchC2STransferCommissionData]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
        	LOG.error(methodName, msg);
        	LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferTxnDAO[searchC2STransferCommissionData]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	}
            if (LOG.isDebugEnabled()) {
            	LOG.debug(methodName, "Exiting userName:" + c2StransferCommRespDTO);
            }
            
            return c2StransferCommRespDTO;
    }
	
	
	
	
	
	
	/**
	 * This method get data for Channel to subscriber commission info data. 
	 * 
	 * @author Subesh KCV
	 * @param p_con
	 * @param c2STransferCommReqDTO
	 *         
	 * @return List<C2StransferCommisionRecordVO>
	 * @throws BTSLBaseException
	 */
	public C2StransferCommRespDTO downloadC2STransferCommissionDataOffline(Connection p_con,C2STransferCommReqDTO  c2STransferCommReqDTO,DownloadDataFomatReq downloadDataFomatReq)
			throws BTSLBaseException {
		final String methodName = "downloadC2STransferCommissionDataOffline";
		
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, c2STransferCommReqDTO.toString());
		}
		PreparedStatement pstmt = null;
		
		C2STransferVO transferVO = null;
		

		 Long totalRequestedAmount=0l;
		 Long totalCreditedAmount=0l;
		Long totalbonusAmount=0l;
		Long totalTransferAmount=0l;
		Long totaltransferCount=0l;
		Double totalTax1=0.0;
		Double totalTax2=0.0;

		final String sqlSelect = c2STransferTxnQry
				.getC2STransferCommissiondetails(c2STransferCommReqDTO);
	    
	    
	    if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, sqlSelect);
		}
	    
	    StringBuilder msg = new StringBuilder();
       
        
		C2StransferCommRespDTO c2StransferCommRespDTO = new C2StransferCommRespDTO();
		String offlineDownloadLocation = SystemPreferences.OFFLINERPT_DOWNLD_PATH;
		java.io.FileWriter outputWriter = null;
		FileOutputStream outExcel = null;
		File file = null;
		HashSet<String> transactionIdset = new HashSet<>();
		CSVWriter csvWriter=null;
		Workbook workbook =null;
		Sheet sheet =null;
		int continueLastRow=0;// For xlsx writing...
		int lastRow = 0;
		String filePath=null;
        try {
        	
        	pstmt = p_con.prepareStatement(sqlSelect.toString());
        	pstmt.setFetchSize(1000);
        	int i = 0;
        	UserDAO userDAO = new UserDAO();
        	String activeUserID =PretupsI.ALL;
        	
        	++i;
        	pstmt.setString(i,c2STransferCommReqDTO.getUserId());
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(BTSLDateUtil.getGregorianDate(c2STransferCommReqDTO.getReportDate() +  PretupsRptUIConsts.REPORT_FROM_TIME.getReportValues())));
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(BTSLDateUtil.getGregorianDate(c2STransferCommReqDTO.getReportDate() +  PretupsRptUIConsts.REPORT_TO_TIME.getReportValues())));
        	++i;
        	pstmt.setString(i, c2STransferCommReqDTO.getNetworkCode());
        	if(!BTSLUtil.isNullString(c2STransferCommReqDTO.getUserType())  && c2STransferCommReqDTO.getUserType().trim().equals(PretupsI.STAFF_USER_TYPE) ) {
        		if(c2STransferCommReqDTO.getUserType()!=null && c2STransferCommReqDTO.getUserType().equals(PretupsI.STAFF_USER_TYPE) ) {
        		   	 if(c2STransferCommReqDTO.getOptionStaff_LoginIDOrMsisdn()!=null && c2STransferCommReqDTO.getOptionStaff_LoginIDOrMsisdn().equals(PretupsI.OPTION_LOGIN_ID) ) {
        		   		  if(BTSLUtil.isNullObject( c2STransferCommReqDTO.getLoginIDOrMsisdn())){
        		   			  throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
        									PretupsErrorCodesI.STAFF_LOGIN_ID_MANDATORY); 
        		   		  }
        		   		if(!c2STransferCommReqDTO.getLoginIDOrMsisdn().equals(PretupsI.ALL)) {
        		   		  ChannelUserVO channUserVO =userDAO.loadUserDetailsByLoginId(p_con, c2STransferCommReqDTO.getLoginIDOrMsisdn());
        		   		  if(BTSLUtil.isNullObject(channUserVO)){
        		   			  throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
        									PretupsErrorCodesI.INVALID_STAFF_LOGIN_ID); 
        		   		  }
        		   		activeUserID=channUserVO.getUserID();
        		   		}
        		   	 }else {
        		   	  if(BTSLUtil.isNullObject(c2STransferCommReqDTO.getLoginIDOrMsisdn())){
        					  throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
        								PretupsErrorCodesI.STAFF_MOBILE_NUM_MANDATORY); 
        				  }
        		   	if(!c2STransferCommReqDTO.getLoginIDOrMsisdn().equals(PretupsI.ALL)) {
        		 		  ChannelUserVO channUserVO =userDAO.loadUserDetailsByMsisdn(p_con, c2STransferCommReqDTO.getLoginIDOrMsisdn());
        				  if(BTSLUtil.isNullObject(channUserVO)){
        					  throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
        								PretupsErrorCodesI.INVALID_STAFF_MSISDN); 
        				  }
        				  activeUserID=channUserVO.getUserID();
        				 }
        		   	  }
        			}
        		
        		++i;
            	pstmt.setString(i,activeUserID);
            	++i;
            	pstmt.setString(i,activeUserID);
            	
          	 }else  if(!BTSLUtil.isNullString(c2STransferCommReqDTO.getUserType())  && c2STransferCommReqDTO.getUserType().trim().equals(PretupsI.CHANNEL_USER_TYPE) ) {
          		 if(c2STransferCommReqDTO.getReqTab().equals(PretupsI.C2C_ADVANCED_TAB_REQ)) {
          			if(!c2STransferCommReqDTO.getChannelUserID().equals(PretupsI.ALL)){
          			 ChannelUserVO channUserVO =userDAO.loadUserDetailsByLoginId(p_con, c2STransferCommReqDTO.getChannelUserID());
          			activeUserID = channUserVO.getUserID();
          			}
          		 }else {
          			if(!c2STransferCommReqDTO.getMobileNumber().equals(PretupsI.ALL)){
          			ChannelUserVO channUserVO =userDAO.loadUserDetailsByMsisdn(p_con, c2STransferCommReqDTO.getMobileNumber());
          			activeUserID = channUserVO.getUserID();
          			}
          		 }
          		 
          		++i;
            	pstmt.setString(i,activeUserID ); 	 
            	++i;
            	pstmt.setString(i,activeUserID );
          	 } else {
          		 if (LOG.isDebugEnabled()) {
         			LOG.debug(methodName, "User type selected : ALL" );
         		}
          	 }
        	
        	++i;
        	pstmt.setString(i,c2STransferCommReqDTO.getTransStatus() );
        	
        	++i;
        	pstmt.setString(i,c2STransferCommReqDTO.getTransStatus() );
        	
        	++i;
        	pstmt.setString(i,c2STransferCommReqDTO.getService() );

        	++i;
        	pstmt.setString(i,c2STransferCommReqDTO.getService() );
        	
        	++i;
         	pstmt.setString(i, c2STransferCommReqDTO.getCategoryCode());
         	++i;
         	pstmt.setString(i, c2STransferCommReqDTO.getCategoryCode());
         	++i;
         	pstmt.setString(i, c2STransferCommReqDTO.getDomain());
         	++i;
         	pstmt.setString(i, c2STransferCommReqDTO.getGeography());
         	++i;
         	pstmt.setString(i, c2STransferCommReqDTO.getGeography());
            ++i;
           	pstmt.setString(i, c2STransferCommReqDTO.getUserId());
          
           	if(!BTSLUtil.isEmpty(c2STransferCommReqDTO.getMobileNumber())  &&  !c2STransferCommReqDTO.getMobileNumber().trim().toUpperCase().equals(PretupsI.ALL) ) {
           		    ++i;
           			pstmt.setString(i, c2STransferCommReqDTO.getMobileNumber());
         	 }
             
           	/*
             if(!BTSLUtil.isEmpty(c2STransferCommReqDTO.getSenderUserID())  &&  !c2STransferCommReqDTO.getSenderUserID().trim().toUpperCase().equals(PretupsI.ALL) ) {
            	 ++i;
                	pstmt.setString(i, c2STransferCommReqDTO.getSenderUserID());
          	    } */ 
         	C2STransfCommReportWriter c2STransfCommReportWriter = new C2STransfCommReportWriter(); 
         	//offlineDownloadLocation="D://downloadedReports//";
           if(c2STransferCommReqDTO.isOffline()) {
           	      //filePath="D://downloadedReports//"+c2STransferCommReqDTO.getFileName();  // for dev Testing...
           		filePath=offlineDownloadLocation+c2STransferCommReqDTO.getFileName();
           	}else {
           		//filePath="D://downloadedReports//"+downloadDataFomatReq.getFileName()+"."+downloadDataFomatReq.getFileType();  // for dev Testing...
           		filePath=offlineDownloadLocation+downloadDataFomatReq.getFileName()+"."+downloadDataFomatReq.getFileType();
           	}
           
           
          
         	
			
			//FileWriter already use  com.btsl.pretups.channel.transfer.util.clientutils.FileWriter,
			//So using below package java.io.FileWriter
             HashMap<String,String>	totalSummaryCaptureCols=null;
             
            try(ResultSet rs = pstmt.executeQuery();)
            	{
   			 file = new File(filePath);
               	if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(downloadDataFomatReq.getFileType().toUpperCase())
        				|| PretupsI.FILE_CONTENT_TYPE_XLSX.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
            		outExcel =new FileOutputStream(file);
        		    workbook = new XSSFWorkbook();
        			sheet = workbook.createSheet(downloadDataFomatReq.getFileName());
        			try {
        				sheet.autoSizeColumn(PretupsRptUIConsts.ZERO.getNumValue());
            			sheet.autoSizeColumn(PretupsRptUIConsts.ONE.getNumValue());
            			sheet.autoSizeColumn(PretupsRptUIConsts.TWO.getNumValue());
        			}catch (Exception e) {
        				LOG.error("", "Error occurred while autosizing columns");
        				e.printStackTrace();
        			}
        			
        			Font headerFont = workbook.createFont();
        			headerFont.setBold(true);
        			// headerFont.setFontHeightInPoints( (Short) 14);
        			CellStyle headerCellStyle = workbook.createCellStyle();
        			headerCellStyle.setFont(headerFont);
        	 totalSummaryCaptureCols = c2STransfCommReportWriter.constructXLSX(workbook,sheet, downloadDataFomatReq, c2STransferCommReqDTO,lastRow,headerCellStyle);
        	 continueLastRow=Integer.parseInt(totalSummaryCaptureCols.get(PretupsI.XLSX_LAST_ROW));
        		} else if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
        			//FileWriter already use  com.btsl.pretups.channel.transfer.util.clientutils.FileWriter,
           			//So using below package java.io.FileWriter
           			 outputWriter = new java.io.FileWriter(file);
        	    	 csvWriter = new CSVWriter(outputWriter, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
    						CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
                	c2STransfCommReportWriter.constructCSV(csvWriter, downloadDataFomatReq, c2STransferCommReqDTO);
                }   
            	  Long totalNumberOfRecords =0l;
            	  c2StransferCommRespDTO.setNoDataFound(true);
            		while (rs.next()) {
            			if(transactionIdset.contains(rs.getString("TRANSACTION_ID"))) {
                   		 continue;
                   	 }
                   	 	else 
                   		 transactionIdset.add(rs.getString("TRANSACTION_ID"));
                   	 
            			
            			
            			if(c2STransferCommReqDTO.isOffline() && OfflineReportRunningThreadMap.checkTaskCancellationRequest(c2STransferCommReqDTO.getOfflineReportTaskID())){
            			  throw new BTSLBaseException(C2STransferTxnDAO.class.getName(), methodName,
  								PretupsErrorCodesI.OFFLINE_REPORT_CANCELLED); 
            			}
            			
            			
            			c2StransferCommRespDTO.setNoDataFound(false);
            			totalNumberOfRecords=totalNumberOfRecords+1;
            			C2StransferCommisionRecordVO c2StransferCommisionRecordVO = new C2StransferCommisionRecordVO();
            			
            			c2StransferCommisionRecordVO.setTransdateTime(String.valueOf(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time"), SystemPreferences.SYSTEM_DATETIME_FORMAT)));
            			c2StransferCommisionRecordVO.setTransactionID(rs.getString("TRANSACTION_ID"));
            			//c2StransferCommisionRecordVO.setServices(rs.getString("SERVICES"));
            			c2StransferCommisionRecordVO.setSubService(rs.getString("subservice_name"));
            			c2StransferCommisionRecordVO.setSenderMobileNumber(rs.getString("SENDER_MSISDN"));
            			c2StransferCommisionRecordVO.setSenderName(rs.getString("USER_NAME"));
            			c2StransferCommisionRecordVO.setSenderGeography(rs.getString("SENDER_GEOGRAPHY"));
            			c2StransferCommisionRecordVO.setSenderCategory(rs.getString("SENDER_CATEGORY_NAME"));
            			c2StransferCommisionRecordVO.setSenderMobileType(rs.getString("MOBILE_TYPE"));
            			c2StransferCommisionRecordVO.setReceiverMobileNumber(rs.getString("RECEIVER_MOBILENUM"));
            			c2StransferCommisionRecordVO.setService(rs.getString("SERVICE_NAME"));
            			c2StransferCommisionRecordVO.setRequestedAmount(rs.getString("REQUESTED_AMOUNT"));
            			c2StransferCommisionRecordVO.setStatus(rs.getString("status"));
//            			c2StransferCommisionRecordVO.setSenderNetworkCode(rs.getString("SENDER_NETWORK_NAME"));
            			c2StransferCommisionRecordVO.setParentMobileNumber(rs.getString("parent_msisdn"));
            			c2StransferCommisionRecordVO.setParentName(rs.getString("parent_name"));
            			c2StransferCommisionRecordVO.setParentCategory(rs.getString("Parent_category_name"));
            			c2StransferCommisionRecordVO.setParentGeography(rs.getString("PARENT_GEOGRAPHY"));
            			c2StransferCommisionRecordVO.setOwnerMobileNumber(rs.getString("owner_msisdn")); 
            			c2StransferCommisionRecordVO.setOwnerName(rs.getString("owner_name")); 
            			c2StransferCommisionRecordVO.setOwnerCategory(rs.getString("Owner_Category")); 
            			c2StransferCommisionRecordVO.setOwnerGeography(rs.getString("owner_geography")); 
            			
            			c2StransferCommisionRecordVO.setReceiverMobileNumber(rs.getString("RECEIVER_MOBILENUM"));
            			c2StransferCommisionRecordVO.setReceiverServiceClass(rs.getString("RECEIVER_SERVICECLASS"));
            			
            			
            			c2StransferCommisionRecordVO.setRequestSource(rs.getString("REQUEST_SOURCE"));
            			c2StransferCommisionRecordVO.setAdjustmentTransID(rs.getString("adjustment_id"));
            			c2StransferCommisionRecordVO.setCommissionType(rs.getString("MARGIN_TYPE_DESC"));
            			c2StransferCommisionRecordVO.setAdditionalCommission(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("differentialCommission"))));   
            			
            			c2StransferCommisionRecordVO.setCacRate(String.valueOf(rs.getLong("cac_rate")));
            			
            			c2StransferCommisionRecordVO.setCacType(rs.getString("cac_type"));
            			
            			c2StransferCommisionRecordVO.setCacAmount(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("cac_amount"))));
            			
            			
            			c2StransferCommisionRecordVO.setMarginRate(rs.getString("MARGIN_RATE"));
            			c2StransferCommisionRecordVO.setMarginAmount(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("differentialCommission"))));
            			c2StransferCommisionRecordVO.setMarginType(rs.getString("MARGIN_TYPE_DESC"));
            			c2StransferCommisionRecordVO.setCurrencyDetail(rs.getString("multicurrency_detail"));
            			c2StransferCommisionRecordVO.setBonusType(rs.getString("BONUS_TYPE"));
            			
            			totalTax1 = totalTax1 + rs.getDouble("tax1_value");
            			totalTax2 = totalTax2 + rs.getDouble("tax2_value");
            			
            			totalRequestedAmount=totalRequestedAmount+rs.getLong("REQUESTED_AMOUNT");
            			totalCreditedAmount=totalCreditedAmount +rs.getLong("credited_Amount");
            			totalbonusAmount =totalbonusAmount +  rs.getLong("bonus_amount");
            		     totalTransferAmount= totalTransferAmount + rs.getLong("TRANSFER_AMOUNT");
            			//totaltransferCount = totaltransferCount +rs.getLong("TRANSFER_AMOUNT");
            			
            			c2StransferCommisionRecordVO.setRequestedAmount(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("REQUESTED_AMOUNT"))));
            			
            			c2StransferCommisionRecordVO.setCreditedAmount(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("credited_Amount"))));
            			c2StransferCommisionRecordVO.setVoucherserialNo(rs.getString("SERIAL_NUMBER"));
            			c2StransferCommisionRecordVO.setTax1(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getDouble("tax1_value"))));
            			c2StransferCommisionRecordVO.setTax2(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getDouble("tax2_value"))));
            			
            			if(rs.getString("BONUS_COMMISSION_TYPE")!=null &&  PretupsI.PROMO_BONUS_CODE.equals(rs.getString("BONUS_COMMISSION_TYPE")) ) {
            				c2StransferCommisionRecordVO.setBonus(String.valueOf(PretupsBL
            						.getDisplayAmount(rs.getLong("bonus_amount"))));
                		}else {
                			c2StransferCommisionRecordVO.setBonus("-");
                		}
            			
            			c2StransferCommisionRecordVO.setPinSentTo(rs.getString("pinsentTOmsisdn"));
            			c2StransferCommisionRecordVO.setTransferAmount(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("TRANSFER_AMOUNT"))));
            			c2StransferCommisionRecordVO.setExternalReferenceID(rs.getString("interface_reference_id"));
            			c2StransferCommisionRecordVO.setRoamPenalty(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("penalty"))));
            			c2StransferCommisionRecordVO.setProcessingFee(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("receiver_access_fee"))));
            			
            			c2StransferCommisionRecordVO.setExternalCode(rs.getString("external_code"));
            			c2StransferCommisionRecordVO.setRequestGateway(rs.getString("request_Gateway_Desc"));
            			c2StransferCommisionRecordVO.setPreviousBalance(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("SENDER_PREVIOUS_BALANCE"))));
            			c2StransferCommisionRecordVO.setPostBalance(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("SENDER_POST_BALANCE"))));
            			
            			c2StransferCommisionRecordVO.setReceiverBonusValue(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("receiver_bonus_value"))));
            			
//            			c2StransferCommisionRecordVO.setLoginID(rs.getString("LOGIN_ID"));
//            			c2StransferCommisionRecordVO.setDifferentialApplicable(rs.getString("differential_applicable"));
//            			c2StransferCommisionRecordVO.setDifferentialGiven(rs.getString("differential_given"));
//            			c2StransferCommisionRecordVO.setDifferentialCommission(String.valueOf(PretupsBL
//        						.getDisplayAmount(rs.getLong("differentialCommission"))));

            			
           //listC2STransferCommissionRecordVO.add(c2StransferCommisionRecordVO);  Removed , as putting millions of records in list will have a memory issue.
            			
            			
            			
            			if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(downloadDataFomatReq.getFileType().toUpperCase())
                				|| PretupsI.FILE_CONTENT_TYPE_XLSX.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
            				c2STransfCommReportWriter.writeXLSXRow(workbook,sheet,downloadDataFomatReq,continueLastRow,c2StransferCommisionRecordVO);
            				continueLastRow=continueLastRow+1;
            				if(totalNumberOfRecords%5000==0) {
            					outExcel.flush();
            					workbook.write(outExcel);
        					}
            			} else if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
                        	c2STransfCommReportWriter.writeCSVRow(csvWriter,downloadDataFomatReq,c2StransferCommisionRecordVO);
                        	if(totalNumberOfRecords%5000==0) {
        						csvWriter.flush();
        						outputWriter.flush();
        					}
                        	
                        }
            		}
            		C2StransferCommSummryData c2StransferCommSummryData = new C2StransferCommSummryData();
            		if(totalNumberOfRecords>0) {
	            		c2StransferCommSummryData.setTotalbonusAmount(String.valueOf(PretupsBL
	        						.getDisplayAmount(totalbonusAmount)));
	            		c2StransferCommSummryData.setTotalCreditedAmount(String.valueOf(PretupsBL
	    						.getDisplayAmount(totalCreditedAmount)));
	            		c2StransferCommSummryData.setTotalRequestedAmount(String.valueOf(PretupsBL
	    						.getDisplayAmount(totalRequestedAmount)));
	            		c2StransferCommSummryData.setTotalTransferAmount(String.valueOf(PretupsBL
	    						.getDisplayAmount(totalTransferAmount
	    								)));
	            		c2StransferCommSummryData.setTotaltransferCount("0");
	            		
	            		c2StransferCommSummryData.setTotaltax1(String.valueOf(PretupsBL
	    						.getDisplayAmount(totalTax1
	    								)));
	            		c2StransferCommSummryData.setTotaltax2(String.valueOf(PretupsBL
	    						.getDisplayAmount(totalTax2
	    								)));
	            		
	            		HashMap<String,String> totSummaryColValue = new HashMap<String,String>();
	            		totSummaryColValue.put(C2STransferCommDownloadColumns.BONUS.getColumnName(), c2StransferCommSummryData.getTotalbonusAmount());
	    				totSummaryColValue.put(C2STransferCommDownloadColumns.REQUESTED_AMOUNT.getColumnName(), c2StransferCommSummryData.getTotalRequestedAmount());
	    				totSummaryColValue.put(C2STransferCommDownloadColumns.CREDITED_AMOUNT.getColumnName(), c2StransferCommSummryData.getTotalCreditedAmount());
	    				totSummaryColValue.put(C2STransferCommDownloadColumns.TRANSFER_AMOUNT.getColumnName(), c2StransferCommSummryData.getTotalTransferAmount());
	    				totSummaryColValue.put(C2STransferCommDownloadColumns.TAX1.getColumnName(), c2StransferCommSummryData.getTotaltax1());
	    				totSummaryColValue.put(C2STransferCommDownloadColumns.TAX2.getColumnName(), c2StransferCommSummryData.getTotaltax2());
	    				
	    			   	if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(downloadDataFomatReq.getFileType().toUpperCase())
	            				|| PretupsI.FILE_CONTENT_TYPE_XLSX.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
	    			   	c2STransfCommReportWriter.writeXLSXTotalSummaryColumns(workbook, sheet, downloadDataFomatReq,continueLastRow,totSummaryColValue,totalSummaryCaptureCols);
		    				workbook.write(outExcel);
	    			   	}else {
	    			   	c2STransfCommReportWriter.writeCSVTotalSummaryColumns(csvWriter,downloadDataFomatReq,totSummaryColValue);	
	    			   	}
	       		   		c2StransferCommRespDTO.setC2StransferCommSummryData(c2StransferCommSummryData);
	            		c2StransferCommRespDTO.setTotalDownloadedRecords(String.valueOf(totalNumberOfRecords));
	            	//	c2StransferCommRespDTO.setListC2sTransferCommRecordVO(listC2STransferCommissionRecordVO);
            		}
            		
            		if(outExcel!=null && workbook!=null) {
	            		outExcel.flush();
						//workbook.write(outExcel);
            		}
            		if(csvWriter!=null && outputWriter!=null) {
						csvWriter.flush();
						outputWriter.flush();
            		}
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
        	LOG.error(methodName, msg);
        	LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferTxnDAO[searchC2STransferCommissionData]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
        	LOG.error(methodName, msg);
        	LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferTxnDAO[searchC2STransferCommissionData]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	 
        	  if(csvWriter!=null) {
        		  try {
					csvWriter.close();
				} catch (IOException e) {
					LOG.error("An error occurred closing csvwriter.", e);
				}
        	  }
        	  
        	  if(outputWriter!=null) {
        		  try {
        			  outputWriter.close();
				} catch (IOException e) {
					LOG.error("An error occurred closing csvwriter.", e);
				}
        	  }
        	  
        	   if(outExcel!=null) {
        		   try {
					outExcel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LOG.error("An error occurred closing XSLX Writer.", e);
				}
        	   }
        	   
        	   
        	   if(workbook!=null) {
        		   try {
        			   workbook.close();
        			   
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LOG.error("An error occurred closing XSLX Writer.", e);
				}
        	   }
        	   
        	   if(!c2STransferCommReqDTO.isOffline()) {
        		   c2StransferCommRespDTO.setOnlineFilePath(filePath);
    	        
                      }
        	
        	}
            if (LOG.isDebugEnabled()) {
            	LOG.debug(methodName, "Exiting userName:" + c2StransferCommRespDTO);
            }
            
            return c2StransferCommRespDTO;
    }


	/**
	 * This method get data for additional commission summary
	 * 
	 * @author Subesh KCV
	 * @param p_con
	 * @param AdditionalCommissionSummryC2SResp
	 *         
	 * @return AdditionalCommissionSummryC2SResp
	 * @throws BTSLBaseException
	 */
	public AdditionalCommissionSummryC2SResp searchAddtnlCommSummryData(Connection p_con,AddtnlCommSummryReqDTO  addtnlCommSummryReqDTO )
			throws BTSLBaseException {
		final String methodName = "searchAddtnlCommSummryData";
		
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, addtnlCommSummryReqDTO.toString());
		}
		PreparedStatement pstmt = null;
		AdditionalCommissionSummryC2SResp additionalCommissionSummryC2SResp = new AdditionalCommissionSummryC2SResp();
				

		 Long totalTransCount=0l;
		 Long totalDiffComm=0l;
		

		final String sqlSelect = c2STransferTxnQry
				.getAddtnlCommSummaryDets(addtnlCommSummryReqDTO);
	    
	    
	    if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, sqlSelect);
		}
	    
	    StringBuilder msg = new StringBuilder();
       
        List<AddtnlCommSummaryRecordVO> listAddtnlCommSummry = new ArrayList<>();
		//
        try {
        	
        	pstmt = p_con.prepareStatement(sqlSelect.toString());
        	pstmt.setFetchSize(100);
        	int i = 0;
        	++i;
        	pstmt.setString(i,addtnlCommSummryReqDTO.getDomain());
        	
        	++i;
        	pstmt.setString(i,addtnlCommSummryReqDTO.getExtnwcode() );
        	
        	++i;
        	pstmt.setString(i,addtnlCommSummryReqDTO.getCategoryCode() );

        	++i;
        	pstmt.setString(i,addtnlCommSummryReqDTO.getCategoryCode() );
        	++i;
        	pstmt.setString(i, addtnlCommSummryReqDTO.getService());
        	++i;
        	pstmt.setString(i, addtnlCommSummryReqDTO.getService());
        	
        String fromDate=null;
        String toDate=null;
        SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
		sdf.setLenient(false);

        if(!BTSLUtil.isEmpty(addtnlCommSummryReqDTO.getDailyOrmonthlyOption()) && addtnlCommSummryReqDTO.getDailyOrmonthlyOption().trim().toUpperCase().equals(PretupsI.PERIOD_MONTHLY)  ) {
        	fromDate="01/"+addtnlCommSummryReqDTO.getFromMonthYear();
			 toDate="01/"+addtnlCommSummryReqDTO.getToMonthYear(); // Just to check max of the month.
			 Date tmpToDate= new Date();
			 try {
				 tmpToDate = sdf.parse(toDate + PretupsRptUIConsts.REPORT_TO_TIME.getReportValues());
				} catch (ParseException e) {
					throw new BTSLBaseException("PretupsUIReportsController", "lowthresholdsearch",
							PretupsErrorCodesI.CCE_XML_ERROR_FROM_DATE_REQUIRED, 0, null);
				}
			 
			 Calendar cal = Calendar.getInstance();
			    cal.setTime(tmpToDate);
			     int maxdays =cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			 
			     toDate=maxdays+"/"+addtnlCommSummryReqDTO.getToMonthYear(); // Just to check max of the month.
        } else {
        	 fromDate=addtnlCommSummryReqDTO.getFromDate();
            toDate=addtnlCommSummryReqDTO.getToDate();
        }
        	
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(BTSLDateUtil.getGregorianDate(fromDate+PretupsRptUIConsts.REPORT_FROM_TIME.getReportValues())));
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(BTSLDateUtil.getGregorianDate(toDate +PretupsRptUIConsts.REPORT_TO_TIME.getReportValues())));
            ++i;
         	pstmt.setString(i, addtnlCommSummryReqDTO.getGeography());
         	++i;
         	pstmt.setString(i, addtnlCommSummryReqDTO.getGeography());
            ++i;
           	pstmt.setString(i, addtnlCommSummryReqDTO.getUserId());
          
               	
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            	
            		while (rs.next()) {
            			
            			AddtnlCommSummaryRecordVO addtnlcommSummryRecordVO = new AddtnlCommSummaryRecordVO();
            			
            			addtnlcommSummryRecordVO.setTransferDateOrMonth(String.valueOf(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("trans_date"), SystemPreferences.SYSTEM_DATETIME_FORMAT)));
            			addtnlcommSummryRecordVO.setLoginID(rs.getString("login_id"));
            			addtnlcommSummryRecordVO.setUserName(rs.getString("user_name"));
            			addtnlcommSummryRecordVO.setUserMobileNumber(rs.getString("msisdn"));
            			addtnlcommSummryRecordVO.setUserCategory(rs.getString("category_name"));
            			addtnlcommSummryRecordVO.setUserGeography(rs.getString("grph_domain_name"));
            			addtnlcommSummryRecordVO.setParentName(rs.getString("parent_msisdn"));
            			addtnlcommSummryRecordVO.setParentCategory(rs.getString("parent_cat"));
            			addtnlcommSummryRecordVO.setParentGeography(rs.getString("parent_geo"));
            			addtnlcommSummryRecordVO.setOwnerName(rs.getString("owner_name"));
            			addtnlcommSummryRecordVO.setOwnerMobileNumber(rs.getString("owner_msisdn"));
            			addtnlcommSummryRecordVO.setOwnerCategory(rs.getString("owner_category"));
            			addtnlcommSummryRecordVO.setOwnerGeography(rs.getString("owner_geo"));
            			addtnlcommSummryRecordVO.setService(rs.getString("service_type_name"));
            			addtnlcommSummryRecordVO.setSubService(rs.getString("selector_name"));
            			addtnlcommSummryRecordVO.setTransactionCount(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("transaction_count"))));
            			addtnlcommSummryRecordVO.setDifferentialCommission(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("differential_amount"))));
            			
            			
            			
            			totalTransCount=totalTransCount+rs.getLong("transaction_count");
            			totalDiffComm=totalDiffComm +rs.getLong("differential_amount");
            				
            			listAddtnlCommSummry.add(addtnlcommSummryRecordVO);
            		}
            		
            		additionalCommissionSummryC2SResp.setAddtnlcommissionSummaryList(listAddtnlCommSummry);
            		additionalCommissionSummryC2SResp.setTotalDiffAmount(String.valueOf(PretupsBL
        						.getDisplayAmount(totalDiffComm)));
            		additionalCommissionSummryC2SResp.setTotalTransactionCount(String.valueOf(PretupsBL
        						.getDisplayAmount(totalTransCount)));
            		
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
        	LOG.error(methodName, msg);
        	LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferTxnDAO[searchAddtnlCommSummryData]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
        	LOG.error(methodName, msg);
        	LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferTxnDAO[searchAddtnlCommSummryData]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	}
            if (LOG.isDebugEnabled()) {
            	LOG.debug(methodName, "Exiting userName:" + additionalCommissionSummryC2SResp);
            }
            
            return additionalCommissionSummryC2SResp;
    }


	
	
	
	/**
	 * This method get data for Channel to subscriber commission info data. 
	 * 
	 * @author Subesh KCV
	 * @param p_con
	 * @param c2STransferCommReqDTO
	 * @return List<C2StransferCommisionRecordVO>
	 * @throws BTSLBaseException
	 */
	public PassbookOthersRespDTO downloadPassbookOthersOffline(Connection p_con,PassbookOthersReqDTO  passbookOthersReqDTO,DownloadDataFomatReq downloadDataFomatReq)
			throws BTSLBaseException {
		final String methodName = "downloadPassbookOthersOffline";
		File onlineFile = null;
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, passbookOthersReqDTO.toString());
		}
		PreparedStatement pstmt = null;
		
		InputStream is=null;

		final String sqlSelect = c2STransferTxnQry
				.getPassbookOthersQuery(passbookOthersReqDTO);
	    
	    
	    if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, sqlSelect);
		}
	    
	    StringBuilder msg = new StringBuilder();
       
        
	    PassbookOthersRespDTO passbookOthersRespDTO = new PassbookOthersRespDTO();
		String offlineDownloadLocation = SystemPreferences.OFFLINERPT_DOWNLD_PATH;
		java.io.FileWriter outputWriter = null;
		FileOutputStream outExcel = null;
		File file = null;
		CSVWriter csvWriter=null;
		Workbook workbook =null;
		Sheet sheet =null;
		int continueLastRow=0;// For xlsx writing...
		int lastRow = 0;
		String filePath=null;
        try {
        	
        	pstmt = p_con.prepareStatement(sqlSelect.toString());
        	pstmt.setFetchSize(1000);
        	int i = 0;
        	UserDAO userDAO = new UserDAO();
        	String activeUserID =null;
        	
    		++i;
        	pstmt.setString(i,passbookOthersReqDTO.getUserId()); // Logged in userID
            ++i;
        	pstmt.setString(i, passbookOthersReqDTO.getNetworkCode());
        	++i;
         	pstmt.setString(i, passbookOthersReqDTO.getDomain());
        	++i;
         	pstmt.setString(i, passbookOthersReqDTO.getCategoryCode());
         	++i;
         	pstmt.setString(i, passbookOthersReqDTO.getCategoryCode());
         	
         	++i;
           	pstmt.setString(i, passbookOthersReqDTO.getUser());  // Input userid
           	++i;
           	pstmt.setString(i, passbookOthersReqDTO.getUser()); // Input userid
           	++i;
           	pstmt.setString(i, passbookOthersReqDTO.getUserId()); // Logged in user id
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(BTSLDateUtil.getGregorianDate(passbookOthersReqDTO.getFromDate() +  PretupsRptUIConsts.REPORT_FROM_TIME.getReportValues())));
        	++i;
        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(BTSLDateUtil.getGregorianDate(passbookOthersReqDTO.getToDate() +  PretupsRptUIConsts.REPORT_TO_TIME.getReportValues())));
        	++i;
         	pstmt.setString(i, passbookOthersReqDTO.getProduct());
        	++i;
         	pstmt.setString(i, passbookOthersReqDTO.getProduct());
           
         	++i;
         	pstmt.setString(i, passbookOthersReqDTO.getGeography());
        	++i;
         	pstmt.setString(i, passbookOthersReqDTO.getGeography());
            ++i;
           	pstmt.setString(i, passbookOthersReqDTO.getUserId());
          
         	PassbookOthersReportWriter passbookOthersWriter = new PassbookOthersReportWriter(); 
            // filePath=offlineDownloadLocation+passbookOthersReqDTO.getFileName();  
         	if(passbookOthersReqDTO.isOffline()) {
         	 //filePath="D://downloadedReports//"+passbookOthersReqDTO.getFileName();  // for dev Testing...
         		filePath=offlineDownloadLocation+passbookOthersReqDTO.getFileName();
         	}else {
         	//	filePath="D://downloadedReports//"+downloadDataFomatReq.getFileName()+"."+downloadDataFomatReq.getFileType();  // for dev Testing...
         		filePath=offlineDownloadLocation+downloadDataFomatReq.getFileName()+"."+downloadDataFomatReq.getFileType();
         	}
         	
			
			//FileWriter already use  com.btsl.pretups.channel.transfer.util.clientutils.FileWriter,
			//So using below package java.io.FileWriter
             HashMap<String,String>	totalSummaryCaptureCols=null;
             
            try(ResultSet rs = pstmt.executeQuery();)
            	{
   			 file = new File(filePath);
               	if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(downloadDataFomatReq.getFileType().toUpperCase())
        				|| PretupsI.FILE_CONTENT_TYPE_XLSX.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
            		outExcel =new FileOutputStream(file);
        		    workbook = new XSSFWorkbook();
        			sheet = workbook.createSheet(downloadDataFomatReq.getFileName());
        			try {
        				sheet.autoSizeColumn(PretupsRptUIConsts.ZERO.getNumValue());
            			sheet.autoSizeColumn(PretupsRptUIConsts.ONE.getNumValue());
            			sheet.autoSizeColumn(PretupsRptUIConsts.TWO.getNumValue());
        			}catch (Exception e) {
        				LOG.error("", "Error occurred while autosizing columns");
        				e.printStackTrace();
        			}
        			
        			Font headerFont = workbook.createFont();
        			headerFont.setBold(true);
        			// headerFont.setFontHeightInPoints( (Short) 14);
        			CellStyle headerCellStyle = workbook.createCellStyle();
        			headerCellStyle.setFont(headerFont);
        	 totalSummaryCaptureCols = passbookOthersWriter.constructXLSX(workbook,sheet, downloadDataFomatReq, passbookOthersReqDTO,lastRow,headerCellStyle);
        	 continueLastRow=Integer.parseInt(totalSummaryCaptureCols.get(PretupsI.XLSX_LAST_ROW));
        		} else if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
        			//FileWriter already use  com.btsl.pretups.channel.transfer.util.clientutils.FileWriter,
           			//So using below package java.io.FileWriter
           			 outputWriter = new java.io.FileWriter(file);
        	    	 csvWriter = new CSVWriter(outputWriter, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
    						CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
        	    	 passbookOthersWriter.constructCSV(csvWriter, downloadDataFomatReq, passbookOthersReqDTO);
                }   
            	  Long totalNumberOfRecords =0l;
            	  passbookOthersRespDTO.setNoDataFound(true);
            		while (rs.next()) {
            			
            			if(passbookOthersReqDTO.isOffline() && OfflineReportRunningThreadMap.checkTaskCancellationRequest(passbookOthersReqDTO.getOfflineReportTaskID())){
            			  throw new BTSLBaseException(C2STransferTxnDAO.class.getName(), methodName,
  								PretupsErrorCodesI.OFFLINE_REPORT_CANCELLED); 
            			}
            			
            			
            			passbookOthersRespDTO.setNoDataFound(false);
            			totalNumberOfRecords=totalNumberOfRecords+1;
            			PassbookOthersRecordVO passbookOthersRecordVO = new PassbookOthersRecordVO();
            			
            			passbookOthersRecordVO.setTransDate(String.valueOf(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date"), SystemPreferences.SYSTEM_DATE_FORMAT)));
            			passbookOthersRecordVO.setProductName(rs.getString("product_name")); 
            			passbookOthersRecordVO.setUserName(rs.getString("user_name"));
            			passbookOthersRecordVO.setUserMobilenumber(rs.getString("msisdn"));
            			passbookOthersRecordVO.setUserCategory(rs.getString("USERCATEGORY"));
            			passbookOthersRecordVO.setUserGeography(rs.getString("USEGEOGRPHY"));
            			//passbookOthersRecordVO.setExternalCode(rs.getString("externalcode"));
            			passbookOthersRecordVO.setParentName(rs.getString("parent_name"));
            			passbookOthersRecordVO.setParentMobilenumber(rs.getString("parent_msisdn"));
            			passbookOthersRecordVO.setParentCategory(rs.getString("parentcategoryName"));
            			passbookOthersRecordVO.setParentGeography(rs.getString("ParentGeography"));
            			passbookOthersRecordVO.setOwnerName(rs.getString("ownerName"));
            			passbookOthersRecordVO.setOwnerGeography(rs.getString("owner_geo"));
            			passbookOthersRecordVO.setOwnerCategory(rs.getString("ownercategoryName"));
            			passbookOthersRecordVO.setOwnerMobileNumber(rs.getString("ownermsisdn"));
            			passbookOthersRecordVO.setO2cTransferCount(String.valueOf(PretupsBL
             						.getDisplayAmount(rs.getLong("o2cTransferCount"))));
            			passbookOthersRecordVO.setO2cTransferAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("o2cTransferAmount"))));
            			passbookOthersRecordVO.setO2cReturnCount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("o2cReturnCount"))));
            			passbookOthersRecordVO.setO2cReturnAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("o2cReturnAmount"))));
            			passbookOthersRecordVO.setO2cWithdrawCount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("o2cWithdrawCount"))));
            			passbookOthersRecordVO.setO2cWithdrawAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("o2cWithdrawAmount"))));	
            			passbookOthersRecordVO.setO2cWithdrawAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("o2cWithdrawAmount"))));	
            			passbookOthersRecordVO.setC2cTransfer_InCount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransfer_InCount"))));
            			passbookOthersRecordVO.setC2cTransfer_InAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransfer_InAmount"))));
            			passbookOthersRecordVO.setC2cTransfer_OutCount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransfer_OutCount"))));
            			passbookOthersRecordVO.setC2cTransfer_OutAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransfer_OutAmount"))));
            			passbookOthersRecordVO.setC2cTransfer_OutAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransfer_OutAmount"))));
            			passbookOthersRecordVO.setC2cTransferRet_InCount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransferRet_InCount"))));
            			passbookOthersRecordVO.setC2cTransferRet_InAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransferRet_InAmount"))));
            			passbookOthersRecordVO.setC2cTransferRet_OutCount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransferRet_OutCount"))));
            			passbookOthersRecordVO.setC2cTransferRet_OutAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransferRet_OutAmount"))));
            			passbookOthersRecordVO.setC2cTransferWithdraw_InCount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransferWithdraw_InCount"))));
            			passbookOthersRecordVO.setC2cTransferWithdraw_InAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2cTransferWithdraw_InAmount"))));
            			passbookOthersRecordVO.setC2cTransferWithdraw_OutAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2c_withdraw_out_amount"))));
            			passbookOthersRecordVO.setC2sTransfer_amount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2s_transfer_out_amount"))));
            			passbookOthersRecordVO.setC2sTransfer_count(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("c2s_transfer_out_COUNT"))));
            			passbookOthersRecordVO.setOpeningBalance(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("opening_balance"))));
            			passbookOthersRecordVO.setClosingBalance(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("closing_balance"))));
            			passbookOthersRecordVO.setAdditionalcommissionAmount(String.valueOf(PretupsBL
         						.getDisplayAmount(rs.getLong("COMMISSION"))));
            			//(opening balance + stock bought - stock return- channel transfers + channel return - C2S transfer)		
            		Long calculateReconStatus = rs.getLong("opening_balance") + 
            				rs.getLong("o2cTransferAmount") - 
            				(rs.getLong("o2cReturnAmount") + rs.getLong("o2cWithdrawAmount")) - 
            				(rs.getLong("c2cTransfer_OutAmount") + rs.getLong("c2cTransferWithdraw_OutCount") + rs.getLong("c2cTransferRet_OutAmount"))+
            				(rs.getLong("c2cTransferWithdraw_InAmount")  + rs.getLong("c2cTransferRet_InAmount") + rs.getLong("c2cTransfer_InAmount") )
            				-rs.getLong("c2s_transfer_out_amount"); 
            		
            		 if(rs.getLong("closing_balance")==calculateReconStatus) {
            			 passbookOthersRecordVO.setReconStatus("N"); 
            		 }else {
            			 passbookOthersRecordVO.setReconStatus("Y");
            		 }
            			 
            			if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(downloadDataFomatReq.getFileType().toUpperCase())
                				|| PretupsI.FILE_CONTENT_TYPE_XLSX.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
            				passbookOthersWriter.writeXLSXRow(workbook,sheet,downloadDataFomatReq,continueLastRow,passbookOthersRecordVO);
            				continueLastRow=continueLastRow+1;
            				if(totalNumberOfRecords%5000==0) {
            					outExcel.flush();
            					workbook.write(outExcel);
        					}
            			} else if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
            				passbookOthersWriter.writeCSVRow(csvWriter,downloadDataFomatReq,passbookOthersRecordVO);
                        	if(totalNumberOfRecords%5000==0) {
        						csvWriter.flush();
        						outputWriter.flush();
        					}
                        	
                        }
            		}
            		if(outExcel!=null && workbook!=null) {
	            		outExcel.flush();
						workbook.write(outExcel);
            		}
            		if(csvWriter!=null && outputWriter!=null) {
						csvWriter.flush();
						outputWriter.flush();
            		}
					
            			       		   	    passbookOthersRespDTO.setTotalDownloadedRecords(String.valueOf(totalNumberOfRecords));
            			       		   	    
            			       		   	    
            			       		   	
            			    		
            		
            		
            		
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
        	LOG.error(methodName, msg);
        	LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferTxnDAO[searchC2STransferCommissionData]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
        	LOG.error(methodName, msg);
        	LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferTxnDAO[searchC2STransferCommissionData]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	 
        	  if(csvWriter!=null) {
        		  try {
					csvWriter.close();
				} catch (IOException e) {
					LOG.error("An error occurred closing csvwriter.", e);
				}
        	  }
        	  
        	  if(outputWriter!=null) {
        		  try {
        			  outputWriter.close();
				} catch (IOException e) {
					LOG.error("An error occurred closing csvwriter.", e);
				}
        	  }
        	  
        	   if(outExcel!=null) {
        		   try {
					outExcel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LOG.error("An error occurred closing XSLX Writer.", e);
				}
        	   }
        	   
        	   
        	   if(workbook!=null) {
        		   try {
        			   workbook.close();
        			   
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LOG.error("An error occurred closing XSLX Writer.", e);
				}
        	   }
        	   
        	   if(!passbookOthersReqDTO.isOffline()) {
        		   passbookOthersRespDTO.setOnlineFilePath(filePath);
    	        
                      }
        	
        }
            if (LOG.isDebugEnabled()) {
            	LOG.debug(methodName, "Exiting userName:" + passbookOthersReqDTO);
            }
            
            return passbookOthersRespDTO;
    }
	
	
}
