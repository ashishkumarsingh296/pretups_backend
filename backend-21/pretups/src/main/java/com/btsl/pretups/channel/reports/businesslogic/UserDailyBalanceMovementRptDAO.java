package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsI;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * 
 * @author rahul.arya
 *
 */
public class UserDailyBalanceMovementRptDAO {

	private UserDailyBalanceMovementRptQuery userDailyBalanceMovementRptQuery;
	
	private Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * 
	 * @author rahul.arya
	 *
	 */
	public UserDailyBalanceMovementRptDAO() {
		userDailyBalanceMovementRptQuery = (UserDailyBalanceMovementRptQuery)ObjectProducer.getObject(QueryConstants.USER_BAL_MOVEMENT_REPORT_QRY,QueryConstants.QUERY_PRODUCER);
	}

	
	
	/**
	 * 
	 * @param con
	 * @param usersReportModel
	 * @return
	 * @throws BTSLBaseException
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList dailyBalanceMovementChnlUserRpt(Connection con,
			UsersReportModel usersReportModel)  throws BTSLBaseException, ParseException {
		String dailyBalanceMovementChnlUserRpt = "dailyBalanceMovementChnlUserRpt";
		if (log.isDebugEnabled()) {
			log.debug(dailyBalanceMovementChnlUserRpt, "ENTERED");
		}
		 ChannelTransferVO channelTransferVO;
		final String methodName = dailyBalanceMovementChnlUserRpt;
		ArrayList<Object> transfersList = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = userDailyBalanceMovementRptQuery
					.dailyBalanceMovementChnlUserRpt(usersReportModel,con);
            rs = pstmt.executeQuery();
            while(rs.next())
            {
            	channelTransferVO = new ChannelTransferVO();
            	channelTransferVO.setTransferDateAsString(rs.getString("transfer_date"));
            	channelTransferVO.setUserName(rs.getString("user_name"));
            	channelTransferVO.setMsisdn(rs.getString("msisdn"));
            	channelTransferVO.setExternalCode(rs.getString("external_code"));
            	channelTransferVO.setGegoraphyDomainName(rs.getString("grph_domain_name"));
            	channelTransferVO.setParentName(rs.getString("parent_name"));
            	channelTransferVO.setParentMsisdn(rs.getString("parent_msisdn"));
            	channelTransferVO.setGrandName(rs.getString("grand_name"));
            	channelTransferVO.setGrandMsisdn(rs.getString("grand_msisdn"));
            	channelTransferVO.setGrandGeo(rs.getString("grand_geo"));
                channelTransferVO.setOwnerGeo(rs.getString("owner_geo"));
            	channelTransferVO.setProductName(rs.getString("product_name"));
            	channelTransferVO.setOpeningBalance(rs.getString("opening_balance"));
            	channelTransferVO.setStockBought(rs.getString("o2c_transfer_in_amount"));
            	channelTransferVO.setStockReturn(rs.getString("stock_return"));
            	channelTransferVO.setChannelTransfers(rs.getString("channel_transfer"));
            	channelTransferVO.setChannelReturn(rs.getString("channel_return"));
            	channelTransferVO.setC2sTransfers(rs.getString("c2s_transfer_out_amount"));
            	channelTransferVO.setClosingBalance(rs.getString("closing_balance"));
            	channelTransferVO.setReconStatus(rs.getString("recon_status"));
            	transfersList.add(channelTransferVO);
            }
		} catch (SQLException sqe) {
			log.error(methodName, "SQLException : " + sqe);
			if (log.isDebugEnabled())
				log.debug(methodName, "SQL Exception:" + sqe);
			throw new BTSLBaseException(this, "", "error.general.sql.processing",sqe);
		} finally {
			try{
		        if (rs!= null){
		        	rs.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
		return transfersList;
	}
	/**
	 * 
	 * @param con
	 * @param usersReportModel
	 * @return
	 * @throws BTSLBaseException
	 * @throws ParseException 
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList dailyBalanceMovementOptRpt(Connection con,
			UsersReportModel usersReportModel) throws BTSLBaseException, ParseException {
		String dailyBalanceMovementOptRpt="dailyBalanceMovementOptRpt";
		if (log.isDebugEnabled()) {
			log.debug(dailyBalanceMovementOptRpt, "ENTERED");
		}
		final String methodName = dailyBalanceMovementOptRpt;
		ArrayList transfersList = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		 ChannelTransferVO channelTransferVO;
		
		try {
			pstmt = userDailyBalanceMovementRptQuery.dailyBalanceMovementOptRpt(usersReportModel,con);
            rs = pstmt.executeQuery();
            while(rs.next())
            {
            	channelTransferVO=new ChannelTransferVO();
            	channelTransferVO.setTransferDateAsString(rs.getString("transfer_date"));
            	channelTransferVO.setUserName(rs.getString("user_name"));
            	channelTransferVO.setMsisdn(rs.getString("msisdn"));
            	channelTransferVO.setExternalCode(rs.getString("external_code"));
            	channelTransferVO.setGegoraphyDomainName(rs.getString("grph_domain_name"));
            	channelTransferVO.setParentName(rs.getString("p_name"));
            	channelTransferVO.setParentMsisdn(rs.getString("p_msisdn"));
            	channelTransferVO.setOwnerName(rs.getString("owner_name"));
            	channelTransferVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
            	channelTransferVO.setProductName(rs.getString("product_name"));
            	channelTransferVO.setOpeningBalance(rs.getString("opening_balance").trim());
            	channelTransferVO.setStockBought(rs.getString("stock_bought").trim());
            	channelTransferVO.setStockReturn(rs.getString("stock_return").trim());
            	channelTransferVO.setChannelTransfers(rs.getString("channel_transfer").trim());
            	channelTransferVO.setChannelReturn(rs.getString("channel_return").trim());
            	channelTransferVO.setNetBalance(rs.getString("net_balance").trim());
            	channelTransferVO.setNetLifting(rs.getString("net_lifting").trim());
            	channelTransferVO.setC2sTransfers(rs.getString("c2s_transfer").trim());
            	channelTransferVO.setReconStatus(rs.getString("recon_value").trim());
            	channelTransferVO.setClosingBalance(rs.getString("closing_balance").trim());
            	transfersList.add(channelTransferVO);
            }
		} catch (SQLException sqe) {
			log.error(methodName, "SQLException : " + sqe);
			if (log.isDebugEnabled())
				log.debug(methodName, "SQL Exception:" + sqe);
			throw new BTSLBaseException(this, "", "error.general.sql.processing",sqe);
		} finally {
			try{
		        if (rs!= null){
		        	rs.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
		return transfersList;
	}
}
