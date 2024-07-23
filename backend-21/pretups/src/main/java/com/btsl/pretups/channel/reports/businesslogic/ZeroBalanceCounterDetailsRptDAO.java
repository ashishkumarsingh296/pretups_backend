package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author tarun.kumar
 *
 */
public class ZeroBalanceCounterDetailsRptDAO {

	private Log log = LogFactory.getLog(this.getClass().getName());
	private ZeroBalanceCounterDetailsRptQry zeroBalanceCounterDetailsRptQry;
	private ChannelReportsUserVO channelReportsUserVO;	
	/**
	 * Default Constructor
	 */
	public ZeroBalanceCounterDetailsRptDAO(){
		zeroBalanceCounterDetailsRptQry = (ZeroBalanceCounterDetailsRptQry)ObjectProducer.getObject(QueryConstants.ZERO_BALANCE_COUNTER_DETAILS_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
    }		
	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 */
	public List<ChannelReportsUserVO> loadZeroBalanceCounterDetailsReport(Connection con,UsersReportModel usersReportModel ) {						
		
		if (log.isDebugEnabled()) {
            log.debug("loadZeroBalanceCounterDetailsReport","ENTERED");
        }
        final String methodName = "loadZeroBalanceCounterDetailsReport";
        ArrayList<ChannelReportsUserVO> reportList = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;      
        try {       	 
			 pstmt = zeroBalanceCounterDetailsRptQry.loadoZeroBalCounterDetailsReportQry(con,usersReportModel);			       	
             rs = pstmt.executeQuery();
             while(rs.next())
             {           	    
            	 channelReportsUserVO= new ChannelReportsUserVO();
            	 channelReportsUserVO.setUserName(rs.getString("user_name"));
            	 channelReportsUserVO.setMsisdn(rs.getString("msisdn"));
            	 channelReportsUserVO.setUserStatus(rs.getString("user_status")); 
            	 channelReportsUserVO.setEntryDateTime(rs.getString("entry_date_time"));
            	 channelReportsUserVO.setTransferId(rs.getString("transfer_id"));
            	 channelReportsUserVO.setTransactionType(rs.getString("transaction_type"));
            	 channelReportsUserVO.setCategoryName(rs.getString("category_name"));
            	 channelReportsUserVO.setProductName(rs.getString("product_name"));
            	 channelReportsUserVO.setRecordType(rs.getString("record_type"));
            	 channelReportsUserVO.setPreviousBalance(String.valueOf(rs.getString("previous_balance")));//numeric
            	 //channelReportsUserVO.setPostBalance(String.valueOf(rs.getString("post_balance")));//numeric
            	 channelReportsUserVO.setThresholdValue(String.valueOf(rs.getString("threshold_value")));//numeric 
            	 channelReportsUserVO.setParentName(rs.getString("parent_name"));
            	 channelReportsUserVO.setParentMsisdn(rs.getString("parent_msisdn"));
            	 channelReportsUserVO.setOwnerName(rs.getString("owner_name"));
            	 channelReportsUserVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
            	 
            	 
            	 reportList.add(channelReportsUserVO);
             }
             if (log.isDebugEnabled()) {
                 log.debug("loadZeroBalanceCounterDetailsReportList",reportList);
             }   
		} catch (SQLException e) {			
			 log.errorTrace(methodName, e);
		}finally{
			try{
	               if (rs!= null){
	               	rs.close();
	               }
	             }
	             catch (SQLException e){
	            	 log.error("An error occurred closing statement.", e);
	             }
			try{
	               if (pstmt!= null){
	            	   pstmt.close();
	               }
	             }
	             catch (SQLException e){
	            	 log.error("An error occurred closing statement.", e);
	             }
		}
        return reportList;
	}
	
	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 */
	public List<ChannelReportsUserVO> loadZeroBalanceCounterChnlUserDetailsReport(	Connection con, UsersReportModel usersReportModel)  {		
		
		final String methodName="loadZeroBalanceCounterChnlUserDetailsReport";
		
		if (log.isDebugEnabled()) {
            log.debug("loadZeroBalanceCounterChnlUserDetailsReport","ENTERED");
        }       
       
		ArrayList<ChannelReportsUserVO> reportList = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;                            
        try {       	 
			 pstmt = zeroBalanceCounterDetailsRptQry.loadoZeroBalCounterChnlUserDetailsReportQry(con,usersReportModel);			        	
             rs = pstmt.executeQuery();
             while(rs.next())
             {           	    
            	 channelReportsUserVO= new ChannelReportsUserVO();
            	 channelReportsUserVO.setUserName(rs.getString("user_name"));
            	 channelReportsUserVO.setMsisdn(rs.getString("msisdn"));
            	 channelReportsUserVO.setUserStatus(rs.getString("user_status")); 
            	 channelReportsUserVO.setEntryDateTime(rs.getString("entry_date_time"));
            	 channelReportsUserVO.setTransferId(rs.getString("transfer_id"));
            	 channelReportsUserVO.setTransactionType(rs.getString("transaction_type"));
            	 channelReportsUserVO.setCategoryName(rs.getString("category_name"));
            	 channelReportsUserVO.setProductName(rs.getString("product_name"));
            	 channelReportsUserVO.setRecordType(rs.getString("record_type"));
            	 channelReportsUserVO.setPreviousBalance(String.valueOf(rs.getString("previous_balance")));//numeric
            	// channelReportsUserVO.setPostBalance(String.valueOf(rs.getString("post_balance")));//numeric
            	 channelReportsUserVO.setThresholdValue(String.valueOf(rs.getString("threshold_value")));//numeric            	                	 
            	 reportList.add(channelReportsUserVO);
             }
             rs.close();
             if (log.isDebugEnabled()) {
                 log.debug("loadZeroBalanceCounterChnlUserDetailsReportList",reportList);
             }   
		} catch (SQLException  sqe) {			
			log.error(methodName, "SQLException : " + sqe);
			if (log.isDebugEnabled())
			log.debug(methodName, "SQL Exception:" + sqe);           
		}finally{
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
        }
        return reportList;				
	}
}
