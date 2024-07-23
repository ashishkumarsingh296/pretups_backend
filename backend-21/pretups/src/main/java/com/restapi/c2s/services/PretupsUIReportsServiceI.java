package com.restapi.c2s.services;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.channel.transfer.businesslogic.AdditionalCommissionSummryC2SResp;
import com.btsl.pretups.channel.transfer.businesslogic.AddtlnCommSummryDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.AddtnlCommSummryReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.BulkUserAddRptReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.BulkUserAddStatusRptResp;
import com.btsl.pretups.channel.transfer.businesslogic.BulkuserAddStsDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTransferCommDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTransferCommReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CtransferCommisionResp;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.C2StransferCommisionResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetCommissionSlabReqVO;
import com.btsl.pretups.channel.transfer.businesslogic.GetCommissionSlabResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetO2CTransferAckDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetO2CTransferAcknowledgeResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetParentOwnerProfileReq;
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransfAckDownloadReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransferDetailDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransferDetailsReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CtransferDetSearchResp;
import com.btsl.pretups.channel.transfer.businesslogic.OfflineReportActionReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.OfflineReportActionResp;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookSearchInfoRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookSearchInfoResponse;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistSearchRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistoryReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistorySearchResp;
import com.btsl.pretups.channel.transfer.businesslogic.UserNameAutoSearchReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.UserStaffDetailsReqDTO;
import com.btsl.pretups.channel.transfer.requesthandler.FetchStaffDetailsRespVO;
import com.btsl.pretups.channel.transfer.requesthandler.FetchUserNameAutoSearchRespVO;
import com.btsl.pretups.channel.transfer.requesthandler.GetParentOwnerProfileRespVO;
import com.btsl.pretups.channel.transfer.requesthandler.ViewAllOfflineRptStatusRespVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

@Service
public interface PretupsUIReportsServiceI {
		
		/**
		 * 
		 * @param msisdn
		 * @param passbookSearchInfoRequestVO
		 * @param locale
		 * @param response
		 * @throws BTSLBaseException
		 */
		public void getPassBookSearchInfo(String msisdn, PassbookSearchInfoRequestVO passbookSearchInfoRequestVO, Locale locale,PassbookSearchInfoResponse response) throws BTSLBaseException;
		
		/**
		 * 
		 * 
		 * @param pinPassHistSearchReqVO
		 * @param response
		 * @throws BTSLBaseException
		 * return List<PinPassHistSearchRecordVO>
		 */
		public List<PinPassHistSearchRecordVO>  getPinPasshistSearchInfo(PinPassHistoryReqDTO pinPassHistoryReqDTO, PinPassHistorySearchResp response) throws BTSLBaseException ;
		
	
		/**
		 * 
		 * 
		 * @param pinPassHistSearchReqVO
		 * @param response
		 * @throws BTSLBaseException
		 * void
		 */
		public void  downloadPinPassHistData(PinPassHistoryReqDTO pinPassHistoryReqDTO, PinPassHistDownloadResp response) throws BTSLBaseException;
		
		
		
		/**
		 * 
		 * 
		 * @param C2STransferCommReqDTO
		 * @param response
		 * @throws BTSLBaseException
		 * 
		 */
		public void  getC2StransferCommissionInfo(C2STransferCommReqDTO c2STransferCommReqDTO, C2StransferCommisionResp response) throws BTSLBaseException ;
		
		
		/**
		 * 
		 * 
		 * @param C2STransferCommReqDTO
		 * @param response
		 * @throws BTSLBaseException
		 * 
		 */		
		public Map<String,String> downloadC2StransferCommData(C2STransferCommReqDTO c2STransferCommReqDTO,C2STransferCommDownloadResp response) throws BTSLBaseException;
		
		
		/**
		 * 
		 * 
		 * @param C2CransferCommReqDTO
		 * @param response
		 * @throws BTSLBaseException
		 * 
		 */
		public void  getC2CtransferCommissionInfo(C2CTransferCommReqDTO c2CTransferCommReqDTO, C2CtransferCommisionResp response) throws BTSLBaseException ;
	
	
		/**
		 * 
		 * 
		 * @param 
		 * @param response
		 * @throws BTSLBaseException
		 * 
		 */
		public void  getUserNameAutoSearchData(UserNameAutoSearchReqDTO userNameAutoSearchReqDTO, FetchUserNameAutoSearchRespVO response) throws BTSLBaseException ;
		
		
		
		/**
		 * 
		 * 
		 * @param C2cTransferCommReqDTO
		 * @param response
		 * @throws BTSLBaseException
		 * @return map
		 * 
		 */		
		public Map<String,String> downloadC2CtransferCommData(C2CTransferCommReqDTO c2cTransferCommReqDTO,
				C2CTransferCommDownloadResp response) throws BTSLBaseException;
	
	
		/**
		 * 
		 * 
		 * @param GetParentOwnerProfileReq
		 * @param response
		 * @throws BTSLBaseException
		 * 
		 */		
		public void getParentOwnerProfileInfo(GetParentOwnerProfileReq getParentOwnerProfileReq,GetParentOwnerProfileRespVO response) throws BTSLBaseException;
		
		
		
		/**
		 * 
		 * 
		 * @param GetCommissionSlabReqVO
		 * @param response
		 * @throws BTSLBaseException
		 * 
		 */		
		public void getCommissionSlabDetails(GetCommissionSlabReqVO getCommissionSlabReqVO,GetCommissionSlabResp response) throws BTSLBaseException;
		
		
		
		/**
		 * 
		 * 
		 * @param O2CTransfAckDownloadReqDTO
		 * @param response
		 * @throws BTSLBaseException
		 * 
		 */		
		public void getO2cTransferAcknowledgement(O2CTransfAckDownloadReqDTO getO2CTransfAcknReqVO,GetO2CTransferAcknowledgeResp response) throws BTSLBaseException;
		
		
		
		/**
		 * 
		 * 
		 * @param O2CTransfAckDownloadReqDTO
		 * @param response 
		 * @throws BTSLBaseException
		 * 
		 */		
		public void downloadO2CTransferAcknowlege(O2CTransfAckDownloadReqDTO o2CTransfAckDownloadReqDTO,GetO2CTransferAckDownloadResp response) throws BTSLBaseException;


		
		
		
		
		/**
		 * 
		 * 
		 * @param O2CTransfAckDownloadReqDTO
		 * @param response
		 * @throws BTSLBaseException
		 * 
		 */		
		public void getO2cTransferDetails(O2CTransferDetailsReqDTO o2CTransferDetailsReqDTO,O2CtransferDetSearchResp response) throws BTSLBaseException;
		
		
		
		/**
		 * 
		 * 
		 * @param O2CTransferDetailsReqDTO
		 * @param response 
		 * @throws BTSLBaseException
		 * **/
				
		public void downloadO2CTransferDetails(O2CTransferDetailsReqDTO o2CTransferDetailsReqDTO,O2CTransferDetailDownloadResp response) throws BTSLBaseException;
		
		
		
		/**
		 * 
		 * 
		 * @param AddtnlCommSummryReqDTO
		 * @param response
		 * @throws BTSLBaseException
		 * 
		 */		
		public void getAdditionCommSummryDetails(AddtnlCommSummryReqDTO addtnlCommSummryReqDTO,AdditionalCommissionSummryC2SResp response) throws BTSLBaseException;
		
		
		
		/**
		 * 
		 * 
		 * @param addtnlCommSummryReqDTO
		 * @param response 
		 * @throws BTSLBaseException
		 * **/
				
		public void downloadAddntlCommSummry(AddtnlCommSummryReqDTO addtnlCommSummryReqDTO,AddtlnCommSummryDownloadResp response) throws BTSLBaseException;
		
		
		
		
		/**
		 * 
		 * 
		 * @param userStaffDetailsReqDTO
		 * @param response 
		 * @throws BTSLBaseException
		 * **/
		public void getStaffUserDetailsData(UserStaffDetailsReqDTO userStaffDetailsReqDTO,FetchStaffDetailsRespVO response) throws BTSLBaseException;	
		
		
		/**
		 * 
		 * 
		 * @param initiatedUserID
		 * @param response 
		 * @throws BTSLBaseException
		 * **/
		public void getAllOfflineReportProcessStatus(String initiatedUserID,ViewAllOfflineRptStatusRespVO response,Locale locale) throws BTSLBaseException;
		
		/**
		 * 
		 * 
		 * @param offlineReportActionReqDTO
		 * @param response 
		 * @throws BTSLBaseException
		 * **/		
		public void delegateOfflineAction(OfflineReportActionReqDTO offlineReportActionReqDTO ,OfflineReportActionResp response) throws BTSLBaseException;
		
		/**
		 * 
		 * 
		 * @param PassbookOthersReqDTO
		 * @param passbookOthersDownloadResp 
		 * @throws BTSLBaseException
		 * **/		
		public void downloadPassbookOthersData(PassbookOthersReqDTO passbookOthersReqDTO,PassbookOthersDownloadResp passbookOthersDownloadResp) throws BTSLBaseException;
		
		
		/**
		 * @param passbookOthersReqDTO
		 * @param channelUserVO
		 * @param bulkUserAddStatusRptResp 
		 * @throws BTSLBaseException
		 * **/		
		public void  searchBulkUserAddStatus(BulkUserAddRptReqDTO passbookOthersReqDTO,ChannelUserVO channelUserVO,BulkUserAddStatusRptResp bulkUserAddStatusRptResp) throws BTSLBaseException;
		
		
		
		/**
		 * 
		 * 
		 * @param BulkUserAddRptReqDTO
		 * @param response 
		 * @throws BTSLBaseException
		 * **/
				
		public void downloadBulkUserAddStsDetails(BulkUserAddRptReqDTO bulkUserAddRptReqDTO,ChannelUserVO channelUserVO,BulkuserAddStsDownloadResp response) throws BTSLBaseException;

}

