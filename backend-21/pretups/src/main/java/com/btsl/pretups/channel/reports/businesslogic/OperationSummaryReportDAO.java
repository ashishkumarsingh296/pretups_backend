package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsI;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author anubhav.pandey1
 *
 */
public class OperationSummaryReportDAO {
	
	


	private static Log log = LogFactory.getLog(OperationSummaryReportDAO.class.getName());
	OperationSummaryRptQry operationSummaryRptQry;
	/**
	 * Constructor
	 */
	public OperationSummaryReportDAO() {
		
		operationSummaryRptQry = (OperationSummaryRptQry)ObjectProducer.getObject(QueryConstants.OPERATION_SUMMARY_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
		   
	}

	/**
	 * @param con
	 * @param thisForm
	 * @return
	 */
	public ArrayList<ChannelTransferVO> loadOperationSummaryChannelUserMainReport(Connection con, UsersReportModel thisForm) {
		
		if (log.isDebugEnabled()) {
            log.debug("loadOperationSummaryChannelUserMainReport",PretupsI.ENTERED);
        }
        final String methodName = "loadOperationSummaryChannelUserMainReport";
        ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ChannelTransferVO channelTransferVO = new ChannelTransferVO(); 
        
      
        
      
        try {
        	 pstmt = operationSummaryRptQry.loadOperationSummaryChannelUserMainReport(thisForm, con);
             rs = pstmt.executeQuery();	
             while(rs.next())
             {		

            	channelTransferList(transfersList, rs, channelTransferVO);
             }
		} catch (SQLException sqe) {
			log.errorTrace(methodName, sqe);
						
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
	 * @param transfersList
	 * @param rs
	 * @param channelTransferVO
	 * @throws SQLException
	 */
	private void channelTransferList(ArrayList<ChannelTransferVO> transfersList, ResultSet rs,ChannelTransferVO channelTransferVO) throws SQLException {
		channelTransferVO.setTransferDateAsString(rs.getString("trans_date"));
		channelTransferVO.setUserName(rs.getString("user_name"));
		channelTransferVO.setUserMsisdn(rs.getString("msisdn"));
		channelTransferVO.setOpeningBalance(rs.getString("opening_balance"));
		channelTransferVO.setO2cTransferInCount(rs.getString("o2c_transfer_in_count"));
		channelTransferVO.setO2cTransferInAmount(rs.getString("o2c_transfer_in_amount"));
		channelTransferVO.setC2cTransferInCount(rs.getString("c2c_transfer_in_count"));
		channelTransferVO.setC2cTransferInAmount(rs.getString("c2c_transfer_in_amount"));
		channelTransferVO.setC2cReturnPlusWithCount(rs.getString("c2c_return_plus_with_in_count"));
		channelTransferVO.setC2cReturnPlusWithINAmount(rs.getString("c2c_return_plus_with_in_amount"));
		 channelTransferVO.setO2cReturnPlusWithoutCount(rs.getString("o2c_return_plus_with_out_count"));
		channelTransferVO.setO2cReturnPlusWithoutAmount(rs.getString("o2c_return_plus_with_out_amount"));
		channelTransferVO.setC2cTransferOutCount(rs.getString("c2c_transfer_out_count"));
		channelTransferVO.setC2cTransferOutAmount(rs.getString("c2c_transfer_out_amount"));
		channelTransferVO.setC2cReturnWithOutCount(rs.getString("c2c_return_plus_with_out_count"));
		channelTransferVO.setC2cReturnWithOutAmount(rs.getString("c2c_return_plus_with_out_amount"));
		channelTransferVO.setC2sTransferOutCount(rs.getString("c2s_transfer_out_count"));
		channelTransferVO.setC2sTransferAmount(rs.getString("c2s_transfer_out_amount"));
		channelTransferVO.setClosingBalance(rs.getString("closing_balance"));
		
		transfersList.add(channelTransferVO);
	}

	/**
	 * @param con
	 * @param thisForm
	 * @return
	 */
	public ArrayList<ChannelTransferVO> loadOperationSummaryChannelUserTotalReport(	Connection con, UsersReportModel thisForm) {
		
		if (log.isDebugEnabled()) {
            log.debug("loadOperationSummaryChannelUserTotalReport",PretupsI.ENTERED);
        }
        final String methodName = "loadOperationSummaryChannelUserTotalReport";
        ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
       
        ChannelTransferVO channelTransferVO = new ChannelTransferVO(); 
        
      
        
      
        try {
        	 pstmt = operationSummaryRptQry.loadOperationSummaryChannelUserTotalReport(thisForm, con);
             rs = pstmt.executeQuery();	
             while(rs.next())
             {		

            	channelTransferList(transfersList, rs, channelTransferVO);
             }
		} catch (SQLException sqe) {
			log.errorTrace(methodName, sqe);
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
	 * @param con
	 * @param thisForm
	 * @return
	 */
	public ArrayList<ChannelTransferVO> loadOperationSummaryOperatorMainReport(	Connection con, UsersReportModel thisForm) {

		if (log.isDebugEnabled()) {
            log.debug("loadOperationSummaryOperatorMainReport",PretupsI.ENTERED);
        }
        final String methodName = "loadOperationSummaryOperatorMainReport";
        ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ChannelTransferVO channelTransferVO = new ChannelTransferVO(); 
        
      
        
      
        try {
        	 pstmt = operationSummaryRptQry.loadOperationSummaryOperatorMainReport(thisForm, con);
             rs = pstmt.executeQuery();	
             while(rs.next())
             {		

            	channelTransferList(transfersList, rs, channelTransferVO);
             }
		} catch (SQLException sqe) {
			log.errorTrace(methodName, sqe);
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
	 * @param con
	 * @param thisForm
	 * @return
	 */
	public ArrayList<ChannelTransferVO> loadOperationSummaryOperatorTotalReport(Connection con, UsersReportModel thisForm) {


		if (log.isDebugEnabled()) {
            log.debug("loadOperationSummaryOperatorTotalReport",PretupsI.ENTERED);
        }
        final String methodName = "loadOperationSummaryOperatorTotalReport";
        ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ChannelTransferVO channelTransferVO = new ChannelTransferVO(); 
        
      
        
      
        try {
        	 pstmt = operationSummaryRptQry.loadOperationSummaryOperatorTotalReport(thisForm, con);
             rs = pstmt.executeQuery();	
             while(rs.next())
             {		

            	channelTransferList(transfersList, rs, channelTransferVO);
             }
		} catch (SQLException sqe) {
			log.errorTrace(methodName, sqe);
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
