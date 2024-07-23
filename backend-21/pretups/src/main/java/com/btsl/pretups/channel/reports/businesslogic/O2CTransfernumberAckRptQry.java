package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.web.pretups.channel.transfer.web.ChannelTransferAckModel;



/**
 * @author pankaj.kumar
 *
 */
public interface O2CTransfernumberAckRptQry {

	  /**
	 * @param channelTransferAckModel
	 * @param con
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public PreparedStatement   loado2cTransferAskDetailsReportQry(ChannelTransferAckModel channelTransferAckModel,Connection con)throws SQLException, ParseException;
		
	  /**
	 * @param channelTransferAckModel
	 * @param con
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public PreparedStatement loado2cTransfeAskChannelUserReportQry(ChannelTransferAckModel channelTransferAckModel,Connection con)throws SQLException, ParseException;
		
	 /**
	 * @param channelTransferAckModel
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement loado2cTransferAskDailyReportQry(ChannelTransferAckModel channelTransferAckModel,Connection con)throws SQLException;

}
