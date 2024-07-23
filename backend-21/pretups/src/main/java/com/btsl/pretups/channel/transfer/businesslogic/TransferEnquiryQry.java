package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.transfer.businesslogic.TransferVO;

public interface TransferEnquiryQry {
	
	/**
	 * @param p_con
	 * @param recMsisdn
	 * @param userId
	 * @param noLastTxn
	 * @param noDays
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement loadLastXTransfersOldQryIfC2S(Connection p_con,
			String recMsisdn, String userId,int noLastTxn,int noDays, java.sql.Date txnDate) throws SQLException;
	/**
	 * @param con
	 * @param recMsisdn
	 * @param userId
	 * @param noLastTxn
	 * @param noDays
	 * @param service
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement loadLastXTransfersOldQryIfC2C(Connection con,
			String recMsisdn, String userId, int noLastTxn, int noDays, String service, java.sql.Date txnDate ) throws SQLException;
	/**
	 * @param con
	 * @param recMsisdn
	 * @param userId
	 * @param noLastTxn
	 * @param noDays
	 * @param service
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement loadLastXTransfersOldQryIfO2C(Connection con,
			String recMsisdn, String userId, int noLastTxn, int noDays, String service, java.sql.Date txnDate ) throws SQLException;
	
	
	/**
	 * @param p_con
	 * @param recMsisdn
	 * @param userId
	 * @param noLastTxn
	 * @param noDays
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement loadLastXTransfersNewQryIfC2S(Connection p_con,
			String recMsisdn, String userId,int noLastTxn,int noDays, java.sql.Date txnDate) throws SQLException;
	
	
	/**
	 * @param con
	 * @param recMsisdn
	 * @param userId
	 * @param noLastTxn
	 * @param noDays
	 * @param service
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement loadLastXTransfersNewQryIfC2C(Connection con,
			String recMsisdn, String userId, int noLastTxn, int noDays, String service, java.sql.Date txnDate ) throws SQLException;
	
	
	/**
	 * @param con
	 * @param recMsisdn
	 * @param userId
	 * @param noLastTxn
	 * @param noDays
	 * @param service
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement loadLastXTransfersNewQryIfO2C(Connection con,
			String recMsisdn, String userId, int noLastTxn, int noDays, String service, java.sql.Date txnDate ) throws SQLException;
	
	/**
	 * @throws BTSLBaseException
	 */
	 public PreparedStatement loadLastNDaysEVDTrfDetailsQry(Connection p_con,
			 int noDays, String serviceType, String senderMsisdn, String serialNumber,
			 String denomination, LocalDate transfer_date) throws BTSLBaseException, SQLException;

}
