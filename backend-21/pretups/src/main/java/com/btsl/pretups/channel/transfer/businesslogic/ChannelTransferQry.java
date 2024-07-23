package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public interface ChannelTransferQry {

	public String loadChannelTransfersListQry(String reveiverCategoryCode, String geoCode, String domainCode, String searchParam, String approvalLevel);
	
	public String loadChannelToChannelStockTransfersListQry(String reveiverCategoryCode, String geoCode, String domainCode, String searchParam, String approvalLevel);
	
	public String loadChannelToChannelStockTransfersListTransferIdQry(String reveiverCategoryCode, String geoCode, String domainCode, String searchParam, String approvalLevel);
	
	public String loadChannelToChannelStockTransfersListQryWildCard(String reveiverCategoryCode, String geoCode, String domainCode, String searchParam, String approvalLevel,String userNameSearch);

	public String loadChannelToChannelVoucherTransfersListQryWildCard(String reveiverCategoryCode, String geoCode, String domainCode, String searchParam, String approvalLevel,String userNameSearch);

	
	
	public String loadChannelToChannelStockTransfersListQryPagination(String reveiverCategoryCode, String geoCode, String domainCode, String searchParam, String approvalLevel,String PageNumber,String EntriesPerPage,String userNameSearch);

	public String loadChannelTransfersVOQry(ChannelTransferVO channelTransferVO);
	
	public String loadChannelTransfersVOTcpQry(ChannelTransferVO channelTransferVO);
	
	public String loadChannelTransferItemsQry();

	public StringBuilder loadEnquiryChannelTransfersListQry(String isPrimary,String transferID,String userCode,String status,String transferCategory,String transferTypeCode);
	
	public StringBuilder getEmailIdOfApproverQry();
	
	public StringBuilder getEmailIdOfApproversQry(String parentUserId);
	
	public StringBuilder getEmailIdOfRoleApproversQry();
	
	public PreparedStatement loadLastXC2STransfersServiceWiseQry(Connection con,
			String serviceType,int noDays, StringBuilder services,Date diffdate,String userId,int noLastTxn) throws SQLException;
	
	public PreparedStatement loadLastXC2CTransfersServiceWiseQry(Connection con,
			String serviceType,int noDays, StringBuilder services,Date diffdate,String userId,int noLastTxn,String aa[],String c2cInOut) throws SQLException;
	
	public PreparedStatement loadLastXO2CTransfersServiceWiseQry(Connection con,
			String serviceType,int noDays, StringBuilder services,Date diffdate,String userId,int noLastTxn,String aa[]) throws SQLException;
	
	public PreparedStatement loadLastXO2CTransferDetailsQry(Connection con,String userId,int lastNoOfTxn,Date differenceDate,String txnType,String txnSubType)throws SQLException;
	public PreparedStatement loadLastXC2CTransferDetailsQry(Connection con,String userId,int lastNoOfTxn,Date differenceDate,String txnType,String txnSubType,String c2cInOut)throws SQLException;
	public PreparedStatement loadLastXC2STransferDetailsQry(Connection con,String userId,int lastNoOfTxn,Date differenceDate,String txnType,String txnSubType)throws SQLException;
	public StringBuilder loadChannelTxnDetailsQry(Connection con,String pType,String userType) throws SQLException;
	public String loadChannelTransferDetailQry();

	public String loadChannelTransferDetailTcpQry();
	
	public StringBuilder loadVoucherDetailsForTransactionIdQry();
	public String loadChannelC2CVoucherTransfersListQry(String reveiverCategoryCode, String geoCode, String domainCode, String searchParam, String approvalLevel);
	public String loadChannelC2CVoucherTransfersListTransactionIdQry(String reveiverCategoryCode, String geoCode, String domainCode, String searchParam, String approvalLevel);
	
	
	
	public String loadChannelC2CVoucherTransfersListQryPagination(String reveiverCategoryCode, String geoCode, String domainCode, String searchParam, String approvalLevel,String pageNumber,String entriesPerPage,String userNameSearch);

	StringBuilder loadVoucherDetailsForTransactionIdChannelQry();
	public String loadChannelTransfersVOC2CQry(ChannelTransferVO channelTransferVO);
	
	public String loadChannelTransfersVOC2CTcpQry(ChannelTransferVO channelTransferVO);
	
	StringBuilder loadBundleIDForTransactionIdQry();
	
	StringBuilder loadPackageVoucherDetailsForTransactionIdQry();
	//Added for MRP && Successive Block for channel transaction
	public String loadChannelTransferDetailsQry(boolean p_chnlTxnMrpBlockTimeoutAllowed,boolean p_requestGatewayCodeCheckRequired, ChannelTransferVO p_channelTransferVO);
	
	public String loadChannelTransferDetailsTcpQry(boolean p_chnlTxnMrpBlockTimeoutAllowed,boolean p_requestGatewayCodeCheckRequired, ChannelTransferVO p_channelTransferVO);
	
	public String getC2CTransferCommissiondetails(C2CTransferCommReqDTO c2CTransferCommReqDTO);
	
	public String getO2CTransferAcknowldgementDetails(O2CTransfAckDownloadReqDTO getO2CTransfAcknReqVO);
	
	
	public String searchO2CTransferDetails(O2CTransferDetailsReqDTO getO2CTransfAcknReqVO);
	
	public StringBuilder loadEnquiryO2cListQry(String isPrimary, String searchBy, String p_transferID,
    		String p_userID, Date p_fromDate, Date p_toDate, String p_status, String[] p_transferSubTypeCodeArr, String p_userCode,  String p_transferCategory,String userType);
	
	public StringBuilder loadEnquiryC2cListQry( String isFromUserPrimary, String isToUserPrimary, String searchBy, String p_transferID,
    		String p_userID, Date p_fromDate, Date p_toDate, String p_status, String[] p_transferSubTypeCodeArr, String p_fromUserCode,
    		String p_toUserCode, String p_transferCategory, String p_staffUserID,  String p_userType,String sessionUserDomain);
	
	public String viewTransactionIDAllowCheck();
	
	public String viewTransactionIDAllowCheckNew();
	
	public String loadO2CChannelTransfersListQry(String isPrimary,String p_transferID,String p_userCode, String p_transferTypeCode, String p_transferCategory );

	
}
