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
 * @author pankaj.kumar
 *
 */
public class StaffC2CTransferdetailsDAO {

	private static Log log=LogFactory.getLog(StaffC2CTransferdetailsDAO.class.getName());
	StaffC2CTransferdetailsRptQry staffC2CTransferdetailsRptQry;
	
	/**
	 *  method StaffC2CTransferdetailsDAO
	 */
	public	StaffC2CTransferdetailsDAO(){
		staffC2CTransferdetailsRptQry = (StaffC2CTransferdetailsRptQry)ObjectProducer.getObject(QueryConstants.STAFFC2C_TRANSFER_DETAILS_REPORT_QRY, QueryConstants.QUERY_PRODUCER);

	}
	
	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 * @throws BTSLBaseException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList loadStaffc2cTransferDetailsChannelUserReport(Connection con,UsersReportModel usersReportModel) throws SQLException, ParseException, BTSLBaseException
	{

		if (log.isDebugEnabled()) {
            log.debug("loadStaffc2cTransferDetailsChannelUserReport",PretupsI.ENTERED);
        }
        final String METHOD_NAME = "loadStaffc2cTransferDetailsChannelUserReport";
        ArrayList transfersList = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ChannelTransferVO channelTransferVO = null; 
        
      
        
      
        try {
        	 pstmt = staffC2CTransferdetailsRptQry.loadstaffc2cTransferDetailsChannelUserReportQry(usersReportModel, con);
             rs = pstmt.executeQuery();	
             while(rs.next())
             {

            	 channelTransferVO=new ChannelTransferVO();
            	 channelTransferVO.setActiveUserId(rs.getString("active_user_id"));
            	 channelTransferVO.setToUserID(rs.getString("to_user_id"));
            	 channelTransferVO.setFromUserID(rs.getString("from_user"));
            	 channelTransferVO.setToUserName(rs.getString("to_user"));
            	 channelTransferVO.setTransferID(rs.getString("transfer_id"));
            	 channelTransferVO.setTransferSubType(rs.getString("transfer_sub_type"));
            	 channelTransferVO.setType(rs.getString("type"));
            
            
            	 channelTransferVO.setProductName(rs.getString("product_name"));
            	
            	
            	 channelTransferVO.setPayableAmountAsStr(rs.getString("payable_amount"));
        
            	 channelTransferVO.setNetPayableAmountAsStr(rs.getString("net_payable_amount"));
            	 
            	 channelTransferVO.setStatus(rs.getString("status"));
            	 channelTransferVO.setMrp(rs.getString("mrp"));
            	 channelTransferVO.setCommissionType(rs.getString("commision")); 
            
            	 channelTransferVO.setCommQtyAsString(rs.getString("commision_quantity"));
            	
            	 channelTransferVO.setReceiverCrQtyAsString(rs.getString("receiver_credit_quantity"));
            	 
            	
             	 channelTransferVO.setSenderDrQtyAsString(rs.getString("sender_debit_quantity"));
             	 
            
            	 channelTransferVO.setTax2ValueAsString(rs.getString("tax2_value"));
            	 channelTransferVO.setTax1ValueAsString(rs.getString("tax1_value"));
            	 channelTransferVO.setSenderCategory(rs.getString("sender_category_code"));
            	 channelTransferVO.setReceiverCategoryCode(rs.getString("receiver_category_code"));
            	 channelTransferVO.setSenderCatName(rs.getString("sender_category_name"));
            	 channelTransferVO.setReceiverCategoryDesc(rs.getString("receiver_category_name"));
            	 channelTransferVO.setSource(rs.getString("SOURCE"));
            
            	
            	 transfersList.add(channelTransferVO);
             }
		} catch (SQLException sqe) {
			log.error(METHOD_NAME, "SQLException : " + sqe);
			if (log.isDebugEnabled())
				log.debug(METHOD_NAME, "SQL Exception:" + sqe);
			throw new BTSLBaseException(this, "", "error.general.sql.processing");
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
				log.debug(METHOD_NAME, PretupsI.EXITED);
			}
		}
		return transfersList;
     
	}
	
}
