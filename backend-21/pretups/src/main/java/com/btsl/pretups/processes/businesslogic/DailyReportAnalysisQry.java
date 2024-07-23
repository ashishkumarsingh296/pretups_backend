package com.btsl.pretups.processes.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookSearchInputVO;

/**
 * @author satakshi.gaur
 *
 */
public interface DailyReportAnalysisQry {

	/**
	 * @return
	 */
	public StringBuilder loadC2SFailRechargeQry();
	
	/**
	 * @param con
	 * @param fromDatePassed
	 * @param toDatePassed
	 * @param networkCode
	 * @param service
	 * @return
	 */
	public PreparedStatement loadC2SRecevierRequestQry(Connection con, Date fromDatePassed, Date toDatePassed, String networkCode, String service);
	/**
	 * @return
	 */
	public StringBuilder loadTotalC2SRecevierRequestQry();
	/**
	 * @return
	 */
	public StringBuilder loadP2PFailRechargeQry();
	
	/**
	 * @param con
	 * @param fromDatePassed
	 * @param toDatePassed
	 * @param networkCode
	 * @param service
	 * @return
	 */
	public PreparedStatement loadP2PRecevierRequestQry(Connection con,
			Date fromDatePassed, Date toDatePassed, String networkCode,
			String service);
	/**
	 * @return
	 */
	public StringBuilder loadTotalP2PRecevierRequestQry();
	/**
	 * @param date
	 * @return
	 * @throws BTSLBaseException
	 */
	public StringBuilder loadC2SReceiverRequestHourlyQry(Date date) throws BTSLBaseException;
	/**
	 * @param date
	 * @return
	 * @throws BTSLBaseException
	 */
	public StringBuilder loadP2PReceiverRequestHourlyQry(Date date) throws BTSLBaseException;
	/**
	 * @return
	 */
	public StringBuilder loadCountsForNtwrkTransferQry();
	/**
	 * @return
	 */
	public StringBuilder loadChannelServiceCountsQry();
	/**
	 * @return
	 */
	public StringBuilder loadC2SServiceCountsQry();
	/**
	 * @return
	 */
	public StringBuilder loadCountsForP2PServicesQry();
	/**
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public StringBuilder loadChannelActivUserCountsQry(Date fromDate,
			Date toDate);
	
	
	public StringBuilder loadC2SRechargeQry();
	
	public StringBuilder loadTotalC2SRechargeQry();
	
	public StringBuilder loadC2STransferSummaryProductQry();
	
	public StringBuilder loadTotalP2PRechargeQry();
	
	public StringBuilder loadP2PTransferSummaryProductQry();
	
	public StringBuilder loadP2PRechargeQry();
	
	public StringBuilder loadInterfaceWiseC2SRechargeQry();
	
	public StringBuilder loadInterfaceWiseP2PRechargeQry();
	
	public StringBuilder loadC2SServiceInterfaceRechargeQry();
	
	public StringBuilder loadP2PServiceInterfaceRechargeQry();
	
	public StringBuilder searchPassBookDetailsQry(PassbookSearchInputVO passbookSearchInputVO); 
	
	public StringBuilder getDataFromReportMasterByID(String reportID);
	
	public String  checkDB();
	
}


