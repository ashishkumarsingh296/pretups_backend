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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author rahul.arya
 *
 */
public class O2CTransferDetailsRptDAO  {
     
	private static Log log = LogFactory.getLog(ChannelTransferDAO.class.getName());
	O2CTransferDetailsRptQry o2cTransferDetailsRptQry;
	/**
	 * Constructor
	 */
	public O2CTransferDetailsRptDAO(){
		o2cTransferDetailsRptQry = (O2CTransferDetailsRptQry)ObjectProducer.getObject(QueryConstants.O2C_TRANSFER_DETAILS_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
    }
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList loado2cTransferDetailsChannelUserReport(Connection con,UsersReportModel usersReportModel) throws SQLException, ParseException, BTSLBaseException
	{

		if (log.isDebugEnabled()) {
            log.debug("loado2cTransferDetailsChannelUserReport",PretupsI.ENTERED);
        }
        final String METHOD_NAME = "loado2cTransferDetailsChannelUserReport";
        ArrayList transfersList = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ChannelTransferVO channelTransferVO = null; 
        
      
        
      
        try {
        	 pstmt = o2cTransferDetailsRptQry.loado2cTransferDetailsChannelUserReportQry(usersReportModel, con);
             rs = pstmt.executeQuery();	
             while(rs.next())
             {

            	 channelTransferVO=new ChannelTransferVO();
            	 channelTransferVO.setFromUserID(rs.getString("from_user"));
            	 channelTransferVO.setToUserID(rs.getString("to_user"));
            	 channelTransferVO.setTransferID(rs.getString("transfer_id"));
            	 channelTransferVO.setTransferCategory(rs.getString("transfer_category"));
            	 channelTransferVO.setTransferSubType(rs.getString("transfer_sub_type"));
            	 channelTransferVO.setTransferDateAsString(rs.getString("transfer_date"));
            	 channelTransferVO.setModifiedOnAsString(rs.getString("modified_on"));
            	 channelTransferVO.setProductCode(rs.getString("product_name"));
            	 channelTransferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
            	 channelTransferVO.setExternalTranDate(rs.getString("ext_txn_date"));
            	 channelTransferVO.setTransactionMode(rs.getString("transaction_mode"));
            	 channelTransferVO.setReqQuantity(rs.getString("required_quantity").trim());
            	 channelTransferVO.setApprovedAmountAsString(rs.getString("approved_quantity").trim());
            	 channelTransferVO.setTax1ValueAsString(rs.getString("tax1_value").trim());
            	 channelTransferVO.setTax2ValueAsString(rs.getString("tax2_value").trim());
            	 channelTransferVO.setCommisionTxnId(rs.getString("commission_value").trim());
            	 // channelTransferVO.setOtfAmount((long)(rs.getFloat("otf_amount")));
            	 channelTransferVO.setOtfAmount(BTSLUtil.parseFloatToLong(rs.getFloat("otf_amount")));
            	 channelTransferVO.setReceiverCrQtyAsString(rs.getString("receiver_credit_quantity").trim());
            	 channelTransferVO.setPayableAmountAsStr(rs.getString("payable_amount").trim());
            	 channelTransferVO.setNetPayableAmountAsStr(rs.getString("net_payable_amount").trim());
            	 transfersList.add(channelTransferVO);
             }
		} catch (SQLException sqe) {
			log.error(METHOD_NAME, " SQLException : " + sqe);
			if (log.isDebugEnabled())
				log.debug(METHOD_NAME, "  SQL Exception:" + sqe);
			throw new BTSLBaseException(this, "", " error.general.sql.processing");
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList loado2cTransferDetailsReport(Connection con,UsersReportModel usersReportModel ) throws BTSLBaseException 
	{
		if (log.isDebugEnabled()) {
            log.debug("loado2cTransferDetailsReport",PretupsI.ENTERED);
        }
        final String Method_Name = "loado2cTransferDetailsReport";
        ArrayList transfersList = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ChannelTransferVO channelTransferVO = null;
        
       
        try {
        	pstmt = o2cTransferDetailsRptQry.loado2cTransferDetailsReportQry(usersReportModel,con);
			
             rs = pstmt.executeQuery();
             
             while(rs.next())
             {
            	 channelTransferVO=new ChannelTransferVO();
            	 channelTransferVO.setFromUserID(rs.getString("from_user"));
            	 channelTransferVO.setToUserID(rs.getString("to_user"));
            	 channelTransferVO.setToMSISDN(rs.getString("to_msisdn"));
            	 channelTransferVO.setTransferID(rs.getString("transfer_id"));
            	 channelTransferVO.setTransferCategory(rs.getString("transfer_category"));
            	 channelTransferVO.setTransferSubType(rs.getString("transfer_sub_type"));
            	 channelTransferVO.setTransferDateAsString(rs.getString("transfer_date"));
            	 channelTransferVO.setModifiedOnAsString(rs.getString("modified_on"));
            	 channelTransferVO.setRequestGatewayType(rs.getString("REQUEST_GATEWAY_TYPE"));
            	 channelTransferVO.setPayInstrumentType(rs.getString("pmt_inst_type"));
            	 channelTransferVO.setPayInstrumentDateAsString(rs.getString("pmt_inst_date"));
            	 channelTransferVO.setPayInstrumentNum(rs.getString("pmt_inst_no"));
            	 channelTransferVO.setProductCode(rs.getString("product_name"));
            	 channelTransferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
            	 channelTransferVO.setExternalTranDate(rs.getString("ext_txn_date"));
            	 channelTransferVO.setTransactionMode(rs.getString("transaction_mode"));
                 channelTransferVO.setReqQuantity(rs.getString("required_quantity").trim());
            	 channelTransferVO.setApprovedAmountAsString(rs.getString("approved_quantity").trim());
            	 channelTransferVO.setCommisionTxnId(rs.getString("commission_value").trim());
            	 channelTransferVO.setOtfAmount(BTSLUtil.parseFloatToLong(rs.getFloat("otf_amount")));
            	 channelTransferVO.setReceiverCrQtyAsString(rs.getString("receiver_credit_quantity").trim());
            	 channelTransferVO.setPayableAmountAsStr(rs.getString("payable_amount").trim());
            	 channelTransferVO.setNetPayableAmountAsStr(rs.getString("net_payable_amount").trim());
            	 transfersList.add(channelTransferVO);
             }
             
		} catch (SQLException | ParseException  sqe) {
			log.error(Method_Name, "SQLException : " + sqe);
			if (log.isDebugEnabled())
				log.debug(Method_Name, "SQL Exception:" + sqe);
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
				log.debug(Method_Name, PretupsI.EXITED);
			}
		}
		return transfersList;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList loado2cTransferDailyDetailsReport(Connection con,UsersReportModel usersReportModel) throws ParseException, SQLException, BTSLBaseException
	{
            
		if (log.isDebugEnabled()) {
            log.debug("loado2cTransferDailyDetailsReport",PretupsI.ENTERED);
        }
        final String METHOD_NAME = "loado2cTransferDailyDetailsReport";
        ArrayList transfersList = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ChannelTransferVO channelTransferVO = null;
       
        
         
        try {
        	 pstmt = o2cTransferDetailsRptQry.loado2cTransferDailyReportQry(usersReportModel,con);
             rs = pstmt.executeQuery();
             while(rs.next())
             {
            	 channelTransferVO=new ChannelTransferVO();
            	
            	 channelTransferVO.setUserName(rs.getString("user_name"));
            	 channelTransferVO.setMsisdn(rs.getString("msisdn"));
            	 channelTransferVO.setCategoryName(rs.getString("category_name"));
            	 channelTransferVO.setDomainName(rs.getString("domain_name"));
            	 channelTransferVO.setParentName(rs.getString("parent_name"));
            	 channelTransferVO.setParentMsisdn(rs.getString("parent_msisdn"));
            	 channelTransferVO.setOwnerName(rs.getString("owner_name"));
            	 channelTransferVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
            	 channelTransferVO.setGraphicalDomainCode(rs.getString("grph_domain_code"));
            	 channelTransferVO.setProductCode(rs.getString("product_name"));
            	 channelTransferVO.setTransferSubType(rs.getString("transfer_sub_type"));
            	 channelTransferVO.setTransferCategory(rs.getString("transfer_category"));
                 channelTransferVO.setTransInCount(rs.getString("trans_in_count"));
                 channelTransferVO.setTransInAmount(rs.getString("trans_in_amount"));
                 channelTransferVO.setTransOutCount(rs.getString("trans_out_count"));
                 channelTransferVO.setTransOutAmount(rs.getString("trans_out_amount"));
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
