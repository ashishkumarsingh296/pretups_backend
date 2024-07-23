package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.transfer.web.ChannelTransferAckModel;

/**
 * @author pankaj.kumar
 *
 */
/**
 * @author pankaj.kumar
 *
 */
/**
 * @author pankaj.kumar
 *
 */
/**
 * @author pankaj.kumar
 *
 */
/**
 * @author pankaj.kumar
 *
 */
/**
 * @author pankaj.kumar
 *
 */
/**
 * @author pankaj.kumar
 *
 */
public class O2CTransferNumberAckDAO {

	private O2CTransfernumberAckRptQry o2CTransferAckRptQry;
	
	
	
	BTSLUtil btslUtil = new BTSLUtil();
	private static Log log = LogFactory.getLog(O2CTransferNumberAckDAO.class.getName());
	private static OperatorUtilI operatorUtilI = null;
	
	 static {
	        try {
	            operatorUtilI = (OperatorUtilI) Class.forName((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS)).newInstance();
	        } catch (Exception e) {

	        	log.errorTrace("static", e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BuddyMgtAction", "", "", "",
	                "Exception while loading the operator util class in class :" + ChannelTransferDAO.class.getName() + ":" + e.getMessage());
	        }
	    }

	 
	 
	
	
	/**
	 * O2CTransferNumberAckDAO 
	 */
	public O2CTransferNumberAckDAO(){
			o2CTransferAckRptQry = (O2CTransfernumberAckRptQry)ObjectProducer.getObject(QueryConstants.O2C_TRANSFER_NUMBER_ASK_QRY, QueryConstants.QUERY_PRODUCER);
		}
	
	
	
	
	
	
	/**
	 * @param con
	 * @param channelTransferAckModel
	 * @return
	 */

	public List loado2cTransferAskChannelUserReport(Connection con,ChannelTransferAckModel channelTransferAckModel) 
			             
	{
		
		
		if (log.isDebugEnabled()) {
            log.debug("loado2cTransferAskDAo","ENTERED");
        }
        final String method = "loado2cTransferAskChannelUserReport";
        ArrayList transfersList = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs= null;
        


       try {
		pstmt = o2CTransferAckRptQry.loado2cTransfeAskChannelUserReportQry(channelTransferAckModel, con);


    	  rs = pstmt.executeQuery();
    	  ChannelTransferVO transferVO=null;
          while(rs.next())
          {
        	 
         	transferVO = new ChannelTransferVO();
            transferVO.setTransferID(rs.getString("transfer_id"));
            transferVO.setGegoraphyDomainName(rs.getString("grph_domain_name"));
            transferVO.setDomainName(rs.getString("domain_name"));
            transferVO.setCategoryName(rs.getString("category_name"));
            transferVO.setTransferType(rs.getString("transfer_type"));
            transferVO.setToUserName(rs.getString("user_name"));
            transferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
            transferVO.setExternalTranDate(rs.getString("ext_txn_date"));
            transferVO.setCommProfileName(rs.getString("comm_profile_set_name"));
            transferVO.setProfileNames(rs.getString("profile_name"));
            transferVO.setReferenceNum(rs.getString("reference_no"));
            transferVO.setTransferCategory(rs.getString("transfer_category"));
            transferVO.setTransferDateAsString(rs.getString("transfer_date"));
 
            transferVO.setFirstApprovedBy(rs.getString("first_approved_by"));
            transferVO.setUserMsisdn(rs.getString("msisdn"));
            transferVO.setAddress1(rs.getString("address1"));
            transferVO.setAddress2(rs.getString("address2"));
            transferVO.setExternalCode(rs.getString("external_code"));
            transferVO.setProductName(rs.getString("product_name"));
            transferVO.setProductCode(rs.getString("product_code"));
            transferVO.setCommissionValue(rs.getString("commission_value"));
            transferVO.setCommissionRate(rs.getString("commission_rate")); 
            transferVO.setCommissionType(rs.getString("commission_type"));
            transferVO.setMrp(String.valueOf(rs.getLong("mrp")));
          
            transferVO.setNetPayableAmounts(rs.getLong("net_payable_amount"));
            transferVO.setPayableAmounts(rs.getLong("payable_amount"));
            transferVO.setUnitValue(rs.getLong("user_unit_price"));
            transferVO.setRequiredQuantity(rs.getLong("required_quantity"));
            transferVO.setTax1ValueAsString(rs.getString("tax1_value"));
            transferVO.setTax1Rate(rs.getString("tax1_rate"));
            transferVO.setTax1Type(rs.getString("tax1_type"));
            transferVO.setTax2ValueAsString(rs.getString("tax2_value"));
            transferVO.setTax2Rate(rs.getString("tax2_rate"));
            transferVO.setTax2Type(rs.getString("tax2_type"));
            transferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
            transferVO.setLevelOneApprovedQuantity(String.valueOf(rs.getLong("first_level_approved_quantity")));
            transferVO.setLevelTwoApprovedQuantity(String.valueOf(rs.getLong("second_level_approved_quantity")));
            transferVO.setLevelThreeApprovedQuantity(String.valueOf(rs.getLong("third_level_approved_quantity")));
            transferVO.setStatus(rs.getString("status"));
            transferVO.setApprovedQuantity(rs.getLong("approved_quantity"));
            transferVO.setChannelRemarks(rs.getString("channel_user_remarks"));
            transferVO.setOtfTypePctOrAMt(rs.getString("otf_type"));
            transferVO.setOtfRate(rs.getDouble("otf_rate"));
            transferVO.setOtfAmount(rs.getLong("otf_amount"));
            transferVO.setPayInstrumentNum(rs.getString("pmt_inst_no"));
            transferVO.setPayInstrumentType(rs.getString("pmt_inst_type"));
            transferVO.setPayInstrumentAmt(rs.getLong("pmt_inst_amount"));
            transferVO.setFirstApprovalRemark(rs.getString("first_approver_remarks"));
            transferVO.setSecondApprovalRemark(rs.getString("second_approver_remarks"));
            transferVO.setThirdApprovalRemark(rs.getString("third_approver_remarks"));
            transferVO.setReceiverCrQty((rs.getLong("receiver_credit_quantity")));
            transfersList.add(transferVO);
         	 
          }
          
		} 
       
       catch(ParseException | SQLException  e){
    	   log.error(method, "Exception:e=" + e);
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
       
     return transfersList;
      

		
	}
}
