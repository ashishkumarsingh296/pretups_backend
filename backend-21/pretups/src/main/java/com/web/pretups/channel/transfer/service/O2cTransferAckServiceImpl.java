package com.web.pretups.channel.transfer.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.validator.ValidatorException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.O2CTransferNumberAckDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.DownloadCSVReports;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.transfer.web.ChannelTransferAckModel;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

/**
 * @author pankaj.kumar
 *
 */

 
@Service("o2cTransferAckService")
public class O2cTransferAckServiceImpl implements O2cTransferAckService {

	private static final Log  _log = LogFactory
			.getLog(O2cTransferAckServiceImpl.class.getName());
	private static final String FAIL_KEY = "fail";
	private static final String MODEL_KEY = "channelTransferAckModel";
	

	@Override
	public boolean loadTransferAckList(HttpServletRequest request,HttpServletResponse response,ChannelTransferAckModel channelTransferAckModel, UserVO userVO,
			          Model model, BindingResult bindingResult)throws ValidatorException, IOException, SAXException
	{

		final String method = "loadTransferAckLists";
		if (_log.isDebugEnabled()) {
			_log.debug(method,"Exiting");
		}
		Connection con;
		MComConnectionI mcomCon;
		ChannelUserWebDAO channelUserWebDAO ;

		
		try {
			// ====== Throw Exception if You have performed an invalid
			// operation=================
			
			CommonValidator commonValidator = new CommonValidator(
					"configfiles/transfer/validator-O2CTransfersAsk.xml",
					channelTransferAckModel, "O2CTrfAck");
			Map<String, String> errorMessages = commonValidator.validateModel();
			PretupsRestUtil pru = new PretupsRestUtil();
			pru.processFieldError(errorMessages, bindingResult);

			if (bindingResult.hasFieldErrors()) {
				request.getSession().setAttribute(MODEL_KEY,channelTransferAckModel);
				return false;
			}
			
			channelUserWebDAO = new ChannelUserWebDAO();

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
			 final String transferID = channelTransferAckModel.getTransferNum();
			channelTransferAckModel.setNetworkCode(userVO.getNetworkID());
			channelTransferAckModel.setNetworkName(userVO.getNetworkName());
			channelTransferAckModel.setReportHeaderName(userVO
					.getReportHeaderName());
			
			

			 final ArrayList transferList = channelTransferDAO.loadEnquiryChannelTransfersList(con, transferID, channelTransferAckModel.getChannelCategoryUserID(), null, null, channelTransferAckModel
		                .getStatusCode(), PretupsI.ALL, channelTransferAckModel.getProductType(), channelTransferAckModel.getTransferCategoryCode(), null);
			
			if (transferList == null || transferList.isEmpty()) {
				model.addAttribute(
						FAIL_KEY,
						PretupsRestUtil.getMessageString("o2ctransfer.acknowledgementdetail.msg.notransfer"));
				    return false;

			}

			/*
			 * check that the transaction must be closed
			 */

			ChannelTransferVO transferVO ;
			transferVO =  (ChannelTransferVO)transferList.get(0);
			
			
			if(!PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(transferVO.getStatus())) 
			{
				model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("o2ctransfer.acknowledgementdetail.msg.notransfer"));
			    return false;
			}
			// load the user hierarchy to validate the sender msisdn and with in
			// the login user hierarchy.
			ArrayList hierarchyList = null;
			channelTransferAckModel.setChannelUserLoginedFlag(false);
			if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
				channelTransferAckModel.setChannelUserLoginedFlag(true);
				String userID ;
				if (PretupsI.CATEGORY_TYPE_AGENT.equals(userVO.getCategoryVO()
						.getCategoryType())
						&& PretupsI.NO.equals(userVO.getCategoryVO()
								.getHierarchyAllowed())) {
					userID = userVO.getParentID();
				} else {
					userID = userVO.getUserID();
				}

				// load whole hierarchy of the form user and check to user under
				// the hierarchy.
				hierarchyList = channelUserWebDAO.loadChannelUserHierarchy(con,
						userID, false);
				if (hierarchyList == null || hierarchyList.isEmpty()) {
				
					
					model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("o2ctransfer.acknowledgementdetail.msg.nohierarchy"));
					return false;
				}
			}
			// now if user entered the txnID direct and try to view the detail
			// of the txn then check the channel domain
			// and the geographical domain of the txn by the loggedIN user
			else if (userVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)
					&& transferList.size() == 1) {
				transferVO =  (ChannelTransferVO)transferList.get(0);
				// if there is only one domains associated with user then there
				// will be
				// no drop down will appear on the screen. just dispaly the
				// domian
				// load the user domain list
				ArrayList domainList = userVO.getDomainList();
				if ((domainList == null || domainList.isEmpty())
						&& PretupsI.YES.equals(userVO.getCategoryVO()
								.getDomainAllowed())
						&& PretupsI.DOMAINS_FIXED.equals(userVO.getCategoryVO()
								.getFixedDomains())) {
					domainList = new DomainDAO().loadCategoryDomainList(con);
				}
				domainList = BTSLUtil.displayDomainList(domainList);
				
				boolean domainfound = false;
				domainfound = checkDomainListSize(transferVO, domainList,
						domainfound);
				if (!domainfound) {
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("o2cenquiry.viewo2ctransfers.msg.usernotindomain",new String[] { channelTransferAckModel.getTransferNum() }));
					return false;
				}
				// now check that is user down in the geographical domain of the
				// loggin user or not.

				final GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
				if (!geographicalDomainDAO
						.isGeoDomainExistInHierarchy(con,
								transferVO.getGraphicalDomainCode(),
								userVO.getUserID())) {
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("o2cenquiry.viewo2ctransfers.msg.usernotdowngeogrphy",new String[] { channelTransferAckModel.getTransferNum() }));
					return false;
				}

			}
			
			if (!BTSLUtil
					.isNullString(channelTransferAckModel.getTransferNum())
					&& hierarchyList != null && !hierarchyList.isEmpty()) {
				boolean isMatched  ;
				for (int m = 0, n = transferList.size(); m < n; m++) {
					transferVO =  (ChannelTransferVO)transferList.get(m);
					isMatched = false;
					isMatched = getHierarchyList(transferVO, hierarchyList,
							isMatched);
					if (!isMatched) {
						model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("o2ctransfer.acknowledgementdetail.msg.notauthorize"));
						return false;
					}
				}
			}

			channelTransferAckModel.setTransferList(transferList);
			if (!transferList.isEmpty()) {
				channelTransferAckModel.setSelectedIndex("0");
			}
			channelTransferAckModel
					.setSearchListSize(transferList.size());
			final int index = Integer.parseInt(channelTransferAckModel
					.getSelectedIndex());
			final ChannelTransferVO channelTransferVO = (ChannelTransferVO) channelTransferAckModel
					.getTransferList().get(index);
			channelTransferDAO.loadChannelTransfersVO(con, channelTransferVO);
			final ArrayList itemsList = ChannelTransferBL
					.loadChannelTransferItemsWithBalances(con,
							channelTransferVO.getTransferID(),
							channelTransferVO.getNetworkCode(),
							channelTransferVO.getNetworkCodeFor(),
							channelTransferVO.getToUserID());
			long totTax1 = 0L;
			long totTax2 = 0L; 
			long totTax3 = 0L;
			long totReqQty = 0L;
			long totStock = 0L;
			long totComm = 0L;
			long totMRP = 0L;
			long mrpAmt ;
			if (itemsList != null && !itemsList.isEmpty()) {
				ChannelTransferItemsVO channelTransferItemsVO ;
				for (int i = 0, j = itemsList.size(); i < j; i++) {
					channelTransferItemsVO = (ChannelTransferItemsVO) itemsList
							.get(i);
					mrpAmt = channelTransferItemsVO.getRequiredQuantity()
							* Long.parseLong(PretupsBL
									.getDisplayAmount(channelTransferItemsVO
											.getUnitValue()));
					channelTransferItemsVO.setProductMrpStr(PretupsBL
							.getDisplayAmount(mrpAmt));
					totTax1 += channelTransferItemsVO.getTax1Value();
					totTax2 += channelTransferItemsVO.getTax2Value();
					totTax3 += channelTransferItemsVO.getTax3Value();
					totComm += channelTransferItemsVO.getCommValue();
					totMRP += mrpAmt;
					totReqQty += channelTransferItemsVO.getRequiredQuantity();
					totStock += channelTransferItemsVO.getNetworkStock();
				}

			}
			channelTransferAckModel.setTotalComm(PretupsBL
					.getDisplayAmount(totComm));
			channelTransferAckModel.setTotalTax1(String.valueOf(totTax1));
			channelTransferAckModel.setTotalTax2(String.valueOf(totTax2));
			channelTransferAckModel.setTotalTax3(String.valueOf(totTax3));
			channelTransferAckModel.setTotalStock(PretupsBL
					.getDisplayAmount(totStock));
			channelTransferAckModel.setTotalReqQty(PretupsBL
					.getDisplayAmount(totReqQty));
			channelTransferAckModel.setTotalMRP(PretupsBL
					.getDisplayAmount(totMRP));
			channelTransferAckModel.setTransferItemsList(itemsList);
			O2CTransferNumberAckDAO o2CTransferAskRptDAO = new O2CTransferNumberAckDAO();
				
					final List<ChannelTransferVO> o2cTransferListAsk = o2CTransferAskRptDAO.loado2cTransferAskChannelUserReport(con,channelTransferAckModel);
					if (transferList == null || transferList.isEmpty()) {
						model.addAttribute(
								FAIL_KEY,
								PretupsRestUtil.getMessageString("o2ctransfer.acknowledgementdetail.msg.notransfer"));
						    return false;

					}
					channelTransferAckModel.setTransferList(o2cTransferListAsk);
					if (!o2cTransferListAsk.isEmpty()) {
						channelTransferAckModel.setSelectedIndex("0");
					}
					channelTransferAckModel.setSearchListSize(transferList.size());
					final int indexs = Integer.parseInt(channelTransferAckModel.getSelectedIndex());
					final ChannelTransferVO channelTransferVOO= (ChannelTransferVO) channelTransferAckModel
							.getTransferList().get(indexs);
				
					this.constructFormFromVOR(channelTransferAckModel, channelTransferVOO);
		}
          
		catch (Exception ex) {
			_log.error(method, "Exception : " + ex.getMessage());
            _log.errorTrace(method, ex);
		}

		return true;

	}



	private void constructFormFromVOR(
			ChannelTransferAckModel channelTransferAckModel,
			ChannelTransferVO channelTransferVO) throws ParseException, BTSLBaseException {
	    	channelTransferAckModel.setTransferNumberDispaly(channelTransferVO.getTransferID());
	    	channelTransferAckModel.setUserName(channelTransferVO.getToUserName());
	    	channelTransferAckModel.setStatus(channelTransferVO.getStatus());
	    	channelTransferAckModel.setGegoraphyDomainName(channelTransferVO.getGegoraphyDomainName());
	    	channelTransferAckModel.setDomainName(channelTransferVO.getDomainName());
	    	channelTransferAckModel.setMsisdn(channelTransferVO.getUserMsisdn());
	 	    channelTransferAckModel.setCategoryName(channelTransferVO.getCategoryName());
	 	    channelTransferAckModel.setCommissionProfileName(channelTransferVO.getCommProfileName());
	 	    channelTransferAckModel.setProfileNames(channelTransferVO.getProfileNames());
	 	   
	 	    channelTransferAckModel.setTransferType(channelTransferVO.getTransferType());
	 	    channelTransferAckModel.setExternalCode(channelTransferVO.getExternalCode());
	 	    channelTransferAckModel.setExternalTxnNum(channelTransferVO.getExternalTxnNum());
	 	    channelTransferAckModel.setExternalTranDate(channelTransferVO.getExternalTranDate());
	 	    channelTransferAckModel.setTransferCategory(channelTransferVO.getTransferCategory());
	 	   channelTransferAckModel.setTransferDateAsString(channelTransferVO.getTransferDateAsString());
	 	
	 	 channelTransferAckModel.setExternalTxnDates(channelTransferVO.getExternalTxnDate());
	 	 if (channelTransferAckModel.getExternalTxnDates() != null) {
	 		channelTransferAckModel.setExternalTranDate(BTSLUtil.getDateStringFromDate(channelTransferAckModel.getExternalTxnDates()));
         }
	 	
	 	    channelTransferAckModel.setRefrenceNum(channelTransferVO.getReferenceNum());
	 	    channelTransferAckModel.setAddress(channelTransferVO.getFullAddress());
	 	    channelTransferAckModel.setProductCode(channelTransferVO.getProductCode());
	 	    channelTransferAckModel.setProductName(channelTransferVO.getProductName());
	 	    channelTransferAckModel.setUnitValue(channelTransferVO.getUnitValue());
	 	    channelTransferAckModel.setRequiredQuantity(channelTransferVO.getRequiredQuantity());
	        channelTransferAckModel.setApprovedQuantity(String.valueOf(channelTransferVO.getApprovedQuantity()));
	    	channelTransferAckModel.setFirstLevelApprovedQuantity(channelTransferVO.getLevelOneApprovedQuantity());
			channelTransferAckModel.setSecondLevelApprovedQuantity(channelTransferVO.getLevelTwoApprovedQuantity());
			channelTransferAckModel.setThirdLevelApprovedQuantity(channelTransferVO.getLevelThreeApprovedQuantity());
	        channelTransferAckModel.setTax1Rate(channelTransferVO.getTax1Rate());
	        channelTransferAckModel.setTax1Type(channelTransferVO.getTax1Type());
	        channelTransferAckModel.setTax2Value(channelTransferVO.getTax2Value());
	        channelTransferAckModel.setTax1Value(channelTransferVO.getTax1Value());
	        channelTransferAckModel.setTax2Rate(channelTransferVO.getTax2Rate());
	        channelTransferAckModel.setTax2Type(channelTransferVO.getTax2Type());
            channelTransferAckModel.setCommissionType(channelTransferVO.getCommissionType());
            channelTransferAckModel.setCommissionRate(channelTransferVO.getCommissionRate());
            channelTransferAckModel.setCommissionValue(channelTransferVO.getCommissionValue());
            channelTransferAckModel.setOtfTypePctOrAMt(channelTransferVO.getOtfTypePctOrAMt());
            channelTransferAckModel.setOtfRate(channelTransferVO.getOtfRate());
            channelTransferAckModel.setOtfAmount(channelTransferVO.getOtfAmount());
            channelTransferAckModel.setMrp(channelTransferVO.getMrp());
            channelTransferAckModel.setPayableAmounts(channelTransferVO.getPayableAmounts());
   	    	channelTransferAckModel.setNetPayableAmounts(channelTransferVO.getNetPayableAmounts());
   	    	//channelTransferAckModel.setReceiverCrQtyAsString(channelTransferVO.getReceiverCrQtyAsString()); 
   	    	channelTransferAckModel.setReceiverCrQty(channelTransferVO.getReceiverCrQty());
   	    	channelTransferAckModel.setPayInstrumentType(channelTransferVO.getPayInstrumentType());
   	        channelTransferAckModel.setPayInstrumentNum(channelTransferVO.getPayInstrumentNum());

   	        channelTransferAckModel.setPayInstrumentAmt(String.valueOf(channelTransferVO.getPayInstrumentAmt()));

	        channelTransferAckModel.setRemarks(channelTransferVO.getChannelRemarks());
	        channelTransferAckModel.setApprove1Remark(channelTransferVO.getFirstApprovalRemark());
	        channelTransferAckModel.setApprove2Remark(channelTransferVO.getSecondApprovalRemark());
	        channelTransferAckModel.setApprove3Remark(channelTransferVO.getThirdApprovalRemark());
         
	        channelTransferAckModel.setErpCode(channelTransferVO.getErpNum());
	        channelTransferAckModel.setProductType(channelTransferVO.getProductType());
	    	
            channelTransferAckModel.setProductTypeDesc(BTSLUtil.getOptionDesc(channelTransferAckModel.getProductType(), channelTransferAckModel.getProductsTypeList()).getLabel());
            
	        if (channelTransferVO.getExternalTxnDate() != null) {
	            channelTransferAckModel.setExternalTxnDate(BTSLUtil.getDateStringFromDate(channelTransferVO.getExternalTxnDate()));
	        } else {
	            channelTransferAckModel.setExternalTxnDate(null);
	        }
          if (channelTransferVO.getPayInstrumentDate() != null) {
            channelTransferAckModel.setPaymentInstrumentDate(BTSLUtil.getDateStringFromDate(channelTransferVO.getPayInstrumentDate()));
          }
    	
         if (channelTransferVO.getTransferDate() != null) {
            channelTransferAckModel.setTransferDate(BTSLUtil.getDateStringFromDate(channelTransferVO.getTransferDate()));
         }
       
    
        channelTransferAckModel.setTransferProfileName(channelTransferVO.getReceiverTxnProfileName());
    
		

       
        channelTransferAckModel.setChannelDomainDesc(channelTransferVO.getDomainCodeDesc());
      
        channelTransferAckModel.setTrfTypeDetail(channelTransferVO.getTransferSubTypeValue());
		channelTransferAckModel.setCommissionQuantity( PretupsBL.getDisplayAmount(channelTransferVO.getCommQty()) );
		channelTransferAckModel.setSenderDebitQuantity(PretupsBL.getDisplayAmount(channelTransferVO.getSenderDrQty()) );
			
        if (_log.isDebugEnabled()) {
            _log.debug("constructFromFromVOO", "Exiting");
        }

	}
	private boolean checkDomainListSize(ChannelTransferVO transferVO,
			ArrayList domainList, boolean domainfound) {
		ListValueVO listValueVO;
		boolean gotDomain=domainfound;
		for (int i = 0, j = domainList.size(); i < j; i++) {
			listValueVO = (ListValueVO) domainList.get(i);
			if (transferVO.getDomainCode().equals(
					listValueVO.getValue())) {
				gotDomain = true;
				break;
			}
		}
		return gotDomain;
	}
	private boolean getHierarchyList(ChannelTransferVO transferVO,
			ArrayList hierarchyList, boolean isMatched) {
		ChannelUserVO channelUserVO;
		boolean hasMatched=isMatched;
		for (int i = 0, j = hierarchyList.size(); i < j; i++) {
			channelUserVO = (ChannelUserVO) hierarchyList.get(i);
			if (channelUserVO.getUserID().equals(
					transferVO.getToUserID())
					|| channelUserVO.getUserID().equals(
							transferVO.getFromUserID())) {
				hasMatched = true;
				break;
			}
		}
		return hasMatched;
	}
	@Override
	public String downloadFileforAck(
			ChannelTransferAckModel channelTransferAckModel)
			throws BTSLBaseException, SQLException, InterruptedException {
		DownloadCSVReports downloadCSVReports = new  DownloadCSVReports();
		 Connection con ;
	        MComConnectionI mcomCon;
	    mcomCon = new MComConnection();
	            con=mcomCon.getConnection();
	            String rptCode="O2CTRFACK01";
   
	            return downloadCSVReports.prepareDataAck(channelTransferAckModel,rptCode, con);
	}

	
	
	
}
