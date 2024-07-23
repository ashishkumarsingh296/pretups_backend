package com.web.pretups.channel.transfer.service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.MComReportDBConnection;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferWebDAO;
import com.web.pretups.channel.transfer.web.ChnlToChnlEnquiryModel;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;



/**
 * @author Himanshu.Kumar
 *
 */
@Service("chnlToChnlEnquiryService")
public class ChnlToChnlEnquiryServiceImpl implements ChnlToChnlEnquiryService {
	private static final String FAIL_KEY = "fail";	
	public static final Log _log = LogFactory
			.getLog(ChnlToChnlEnquiryServiceImpl.class.getName());
	
	
	 /**
     * This method is used to authorize the page and flush the form.
     * This method loads the information which has been displayed on the first
     * page.
     * 
     * @param chnlToChnlEnquiryModel
     * @param model
     * @param request
     * @param response
     * @return boolean
     * @throws IOException
     */
	@Override
	public boolean loadTransferTypeList(ChnlToChnlEnquiryModel chnlToChnlEnquiryModel,UserVO userVO, Model model,HttpServletRequest request) throws IOException {
		
		final String METHOD_NAME = "loadTransferTypeList";
		if (_log.isDebugEnabled()) {
            _log.debug("loadTransferTypeList", "Entered");
        }
		
		final ChnlToChnlEnquiryModel theForm = chnlToChnlEnquiryModel ;
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			
			// load the transfer type list
			 theForm.setTransferTypeList(LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE, true));
	        final ListValueVO listValueVO = new ListValueVO(PretupsI.ALL, PretupsI.ALL);
	        theForm.getTransferTypeList().add(0, listValueVO);
        
	        if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE) && !theForm.isStaffEnquiry()) {
	            theForm.setFromUserCode(userVO.getUserCode());
	        }
	        
	        // Changes for Operator users
            theForm.setUserType(userVO.getUserType());
            if (userVO.getCategoryCode().equalsIgnoreCase(PretupsI.CUSTOMER_CARE)) {
                theForm.setUserType(PretupsI.CUSTOMER_CARE);
            }
            final ArrayList loggedInUserDomainList = new ArrayList();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            if (!theForm.getUserType().equals(PretupsI.CUSTOMER_CARE)) {
                // CategoryDAO categoryDAO = new CategoryDAO();
                final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
                theForm.setZoneList(userVO.getGeographicalAreaList());
                theForm.setDomainList(BTSLUtil.displayDomainList(userVO.getDomainList()));
                final int loginSeqNo = userVO.getCategoryVO().getSequenceNumber();
                theForm.setCategorySeqNo(loginSeqNo + "");
                if (userVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)) {
                    theForm.setParentCategoryList(categoryWebDAO.loadCategoryReportList(con));
                } else {
                    theForm.setParentCategoryList(categoryWebDAO.loadCategoryReporSeqtList(con, loginSeqNo));
                }

                theForm.setLoginUserID(userVO.getUserID());
                theForm.setLoggedInUserCategoryCode(userVO.getCategoryVO().getCategoryCode());
                if (userVO.isStaffUser()) {
                    theForm.setLoggedInUserName(userVO.getParentName());
                } else {
                    theForm.setLoggedInUserName(userVO.getUserName());
                }
                if (theForm.getDomainListSize() == 0) {
                    loggedInUserDomainList.add(new ListValueVO(userVO.getDomainName(), userVO.getDomainID()));
                    theForm.setDomainList(loggedInUserDomainList);
                    theForm.setDomainCode(userVO.getDomainID());
                    theForm.setDomainName(userVO.getDomainName());
                } else if (theForm.getDomainListSize() == 1) {
                    final ListValueVO listvo = (ListValueVO) theForm.getDomainList().get(0);
                    theForm.setDomainCode(listvo.getValue());
                    theForm.setDomainName(listvo.getLabel());
                }

                final ArrayList zoneList = theForm.getZoneList();
                UserGeographiesVO geographyVO = null;
                ListValueVO listValueVOZone = null;
                final ArrayList geoList = new ArrayList();

                for (int i = 0, k = zoneList.size(); i < k; i++) {
                    geographyVO = (UserGeographiesVO) zoneList.get(i);
                    geoList.add(new ListValueVO(geographyVO.getGraphDomainName(), geographyVO.getGraphDomainCode()));
                }
                if (geoList.size() == 1) {
                    listValueVOZone = (ListValueVO) geoList.get(0);
                    theForm.setZoneCode(listValueVOZone.getValue());
                    theForm.setZoneName(listValueVOZone.getLabel());
                    theForm.setZoneList(geoList);
                } else {
                    theForm.setZoneList(geoList);
                }
            }

	      
		}
		
            catch (Exception e) {
            	_log.error(METHOD_NAME, "Exception:e=" + e);
    			_log.errorTrace(METHOD_NAME, e);
             
            }finally{
			if (mcomCon != null) {
				mcomCon.close("ChnlToChnlEnquiryServiceImpl#loadTransferTypeList");
				mcomCon = null;
			}
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadTransferTypeList", "Exiting forward:=" + chnlToChnlEnquiryModel.getJspPath());
            }	
		
		  model.addAttribute("ChnlToChnlEnquiryModel",theForm);
			return true;
	} 
	
	/**
     * This method loads the transfer enquiry list
     * 
     * @param userVO
     * @param chnlToChnlEnquiryModel
     * @param model
     * @param bindingResult
     * @param request
     * @param response
     * @return boolean
     */
	@Override
	public boolean showEnquiryDetails(UserVO userVO,ChnlToChnlEnquiryModel chnlToChnlEnquiryModel, Model model,BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws IOException {
		final String METHOD_NAME = "showEnquiryDetails";
		if (_log.isDebugEnabled()) {
            _log.debug("showEnquiryDetails", "Entered");
        }
		
		Connection con = null;
		MComConnectionI mcomCon = null;
        Date fromDate = null;
        Date toDate = null;
        ChannelTransferWebDAO channelTransferWebDAO = null;
        ChannelUserWebDAO channelUserWebDAO = null;
        final ChnlToChnlEnquiryModel theForm = chnlToChnlEnquiryModel ;
        theForm.setTransferTypeList(LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE, true));
        final ListValueVO listValueVO = new ListValueVO(PretupsI.ALL, PretupsI.ALL);
        theForm.getTransferTypeList().add(0, listValueVO);
        try {
        	
            if(request.getParameter("submitTransferNum")!=null){
   			 CommonValidator commonValidator=new CommonValidator("configfiles/transfer/validator-c2cEnquiry.xml", chnlToChnlEnquiryModel, "C2CModuleTransferNum");
            	Map<String, String> errorMessages = commonValidator.validateModel();
            	PretupsRestUtil pru=new PretupsRestUtil();
            	pru.processFieldError(errorMessages, bindingResult);
            	model.addAttribute("formNumber", "Panel-One");
            	request.getSession().setAttribute("formNumber", "Panel-One");
   		 }
   		 if(request.getParameter("submitFromUserCode")!=null){
   			 CommonValidator commonValidator=new CommonValidator("configfiles/transfer/validator-c2cEnquiry.xml", chnlToChnlEnquiryModel, "C2CModuleFromUserCode");
            	Map<String, String> errorMessages = commonValidator.validateModel();
            	PretupsRestUtil pru=new PretupsRestUtil();
            	pru.processFieldError(errorMessages, bindingResult); 
            	model.addAttribute("formNumber", "Panel-Two");
              	request.getSession().setAttribute("formNumber", "Panel-Two");
   		 }
   		 if(request.getParameter("submitToUserCode")!=null){
   			 CommonValidator commonValidator=new CommonValidator("configfiles/transfer/validator-c2cEnquiry.xml", chnlToChnlEnquiryModel, "C2CModuleToUserCode");
            	Map<String, String> errorMessages = commonValidator.validateModel();
            	PretupsRestUtil pru=new PretupsRestUtil();
            	pru.processFieldError(errorMessages, bindingResult); 
            	model.addAttribute("formNumber", "Panel-Three");
              	request.getSession().setAttribute("formNumber", "Panel-Three");
   		 }
   		 if(bindingResult.hasFieldErrors()){
			    
      		return false;
      	 }	 
        	
        	channelUserWebDAO = new ChannelUserWebDAO();
			 theForm.setSosSettlementDate("");
			 
            if (!BTSLUtil.isNullString(theForm.getFromUserCode())) {
                theForm.setFromUserCode(PretupsBL.getFilteredMSISDN(theForm.getFromUserCode()));
            }
            if (!BTSLUtil.isNullString(theForm.getToUserCode())) {
                theForm.setToUserCode(PretupsBL.getFilteredMSISDN(theForm.getToUserCode()));
            }
            if (BTSLUtil.isNullString(theForm.getTransferNum())) {
                if (!BTSLUtil.isNullString(theForm.getFromUserCode()) && theForm.getFromUserCode().equals(theForm.getToUserCode())) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("showEnquiryDetails", "From user MSISDN and To user MSISDN are same");
                    }
                    //throw new BTSLBaseException(this, "showEnquiryDetails", "transferenquiry.enquirysearchattribute.msg.fromtosame", "searchattribute");
                    model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.transferenquiry.enquirysearchattribute.msg.fromtosame"));
                    return false;
                }
            }
           
            // load the user hierarchy to validate the input values.
            ArrayList hierarchyList = null;

            if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_RPT_DB)).booleanValue() && PretupsI.RESET_CHECKBOX.equals(theForm.getCurrentDateFlag()))) {
            	mcomCon = new MComReportDBConnection();
            	con=mcomCon.getConnection();
            } else {
            	mcomCon = new MComConnection();
            	con=mcomCon.getConnection();
            }
            ChannelUserVO channelUserVO = null;
            if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                String userID = null;
                if (PretupsI.CATEGORY_TYPE_AGENT.equals(userVO.getCategoryVO().getCategoryType()) && PretupsI.NO.equals(userVO.getCategoryVO().getHierarchyAllowed())) {
                    userID = userVO.getParentID();
                } else {
                    userID = userVO.getUserID();
                }

                // load whole hierarchy of the form user and check to user under
                // the hierarchy.
                hierarchyList = channelUserWebDAO.loadChannelUserHierarchy(con, userID, false);
                if (hierarchyList.isEmpty()) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("showEnquiryDetails", "Logged in user has no child user so there would be no transactions");
                    }
                   // throw new BTSLBaseException(this, "showEnquiryDetails", "transferenquiry.enquirysearchattribute.msg.nohierarchy", "searchattribute");
                    model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.transferenquiry.enquirysearchattribute.msg.nohierarchy"));
                    return false;
                }
                // validate the from user code down to the user hierarchy of the
                // login user.
                final String formUserCode = theForm.getFromUserCode();
                if (!BTSLUtil.isNullString(formUserCode) && !((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
                    if (BTSLUtil.isNullString(theForm.getTransferNum())) {
                        boolean isMatched = false;
                        if (hierarchyList.size() > 0) {
                            isMatched = false;
                            for (int i = 0, j = hierarchyList.size(); i < j; i++) {
                                channelUserVO = (ChannelUserVO) hierarchyList.get(i);
                                if (channelUserVO.getMsisdn().equals(formUserCode)) {
                                    isMatched = true;
                                    break;
                                }
                            }
                            if (!isMatched) {
                                /*throw new BTSLBaseException(this, "showEnquiryDetails", "transferenquiry.enquirysearchattribute.msg.notauthorise", 0,
                                    new String[] { formUserCode }, "searchattribute");*/
                            	String[] arr={formUserCode};
                                model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.transferenquiry.enquirysearchattribute.msg.notauthorise",arr));
                                return false;    
                            }
                        }
                    }
                } else if (!BTSLUtil.isNullString(formUserCode)) {
                    UserPhoneVO userPhoneVO = null;
                    final UserDAO userDAO = new UserDAO();
                    userPhoneVO = userDAO.loadUserAnyPhoneVO(con, formUserCode);
                    if (BTSLUtil.isNullString(theForm.getTransferNum())) {
                        boolean isMatched = false;
                        if (userPhoneVO != null && hierarchyList.size() > 0) {
                            isMatched = false;
                            for (int i = 0, j = hierarchyList.size(); i < j; i++) {
                                channelUserVO = (ChannelUserVO) hierarchyList.get(i);
                                if (channelUserVO.getUserID().equals(userPhoneVO.getUserId())) {
                                    isMatched = true;
                                    break;
                                }
                            }
                            if (!isMatched) {
                               /* throw new BTSLBaseException(this, "showEnquiryDetails", "transferenquiry.enquirysearchattribute.msg.notauthorise", 0,
                                    new String[] { formUserCode }, "searchattribute");*/
                            	String[] arr={formUserCode};
                            	 model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.transferenquiry.enquirysearchattribute.msg.notauthorise",arr));
                                 return false;
                            }
                        }
                    }
                }
            }

            // get the transfer id if entered
            String transferID = null;
            if (!BTSLUtil.isNullString(theForm.getTransferNum())) {
                transferID = theForm.getTransferNum().trim();
            }
            if (!BTSLUtil.isNullString(theForm.getFromDate())) {
                fromDate = BTSLUtil.getDateFromDateString(theForm.getFromDate());
            }
            if (!BTSLUtil.isNullString(theForm.getToDate())) {
                toDate = BTSLUtil.getDateFromDateString(theForm.getToDate());
            }
            

       	 // check date differnce in fromDate and toDate
           int diff = BTSLUtil.getDifferenceInUtilDates(fromDate, toDate);
           if(diff > 20){
           	model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.transferenquiry.enquirysearchattribute.msg.datediffernce", new String[]{Constants.getProperty("DATE_DIFFERENCE")}));
               return false;
           }
       	
            theForm.setTransferTypeValue(BTSLUtil.getOptionDesc(theForm.getTransferTypeCode(), theForm.getTransferTypeList()).getLabel());
            channelTransferWebDAO = new ChannelTransferWebDAO();
            
           
            String toUserCode = theForm.getToUserCode();
            if (!BTSLUtil.isNullString(theForm.getToUserCode())) {
                toUserCode = theForm.getToUserCode().trim();
            }
            // load the transaction list on the basis of the input values.
            final ArrayList tmptransferList = channelTransferWebDAO.loadChnlToChnlEnquiryTransfersListC2C(con, theForm.getFromUserCode(), toUserCode, fromDate, toDate,
                transferID, PretupsI.CHANNEL_TYPE_C2C, theForm.getTransferTypeCode(), userVO.getNetworkID());
            // now validate the input values if transfer id was input
            ChannelTransferVO transferVO = null;
            ArrayList transferList = new ArrayList();
            if (hierarchyList != null && hierarchyList.size() > 0) {
                for (int m = 0, n = tmptransferList.size(); m < n; m++) {
                    transferVO = (ChannelTransferVO) tmptransferList.get(m);
                    for (int i = 0, j = hierarchyList.size(); i < j; i++) {
                        channelUserVO = (ChannelUserVO) hierarchyList.get(i);
                        if (channelUserVO.getUserID().equals(transferVO.getFromUserID())) {
                        	
                        	String a=PretupsBL.getDisplayAmount(transferVO.getRequestedQuantity());
                        	transferVO.setReqQuantity(a);
                            transferList.add(transferVO);
                            break;
                        }
                    }
                }
            } else {
                transferList = tmptransferList;
            }
            final ArrayList transferCategoryList = LookupsCache.loadLookupDropDown(PretupsI.C2C_TRANSFER_TYPE, true);
            
            theForm.setTransferList(transferList); 
            theForm.setJspPath("channeltransfer/c2cEnquiryTransferListView");

            if (transferList.size() != 0) {
                theForm.setSelectedIndex("0");
                int index;
                final int transferListSize = transferList.size();
                for (index = 0; index < transferListSize; index++) {
                    transferVO = (ChannelTransferVO) transferList.get(index);
                    transferVO.setTransferCategoryCodeDesc(BTSLUtil.getOptionDesc(transferVO.getTransferCategoryCode(), transferCategoryList).getLabel());
                }
                if (transferList.size() == 1) {
        //            theForm.setTransferTypeValue(BTSLUtil.getOptionDesc(PretupsI.ALL, theForm.getTransferTypeList()).getLabel());
                     enquiryDetail(theForm, request);
                }
            }
           if(transferList.size() == 0){
        	   model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.channeltransfer.c2cenquirysearchattribute.notransferdetail.msg"));
               return false;
           }
        }
        catch (Exception e) {
            _log.error("showEnquiryDetails", "Exception:e=" + e);
            _log.errorTrace(METHOD_NAME, e);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("ChnlToChnlEnquiryServiceImpl#showEnquiryDetails");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("showEnquiryDetails", "Exiting forward:=" + theForm.getJspPath());
            }
        }
        request.getSession().setAttribute("c2cTransferList", theForm);
        model.addAttribute("c2cTransferList",theForm);
        return true;
        
	
   }  // showEnquiryDetails end 
	
	
	
	  /**
     * This method is used to calculate total of all the fields.
     * 
     * @param theForm
     * @param request
     * @return boolean
     */
	@Override
	public boolean enquiryDetail(ChnlToChnlEnquiryModel theForm, HttpServletRequest request) {
        final String METHOD_NAME = "enquiryDetail";
        if (_log.isDebugEnabled()) {
            _log.debug("enquiryDetail", "Entered");
        }
        
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            if (request.getParameter("backbutton") != null) {
                if (!BTSLUtil.isNullString(theForm.getTransferNum())) {
                    // theForm.setToUserCode(null);
                    theForm.setTransferTypeCode(PretupsI.ALL);
                } else {
                    theForm.setTransferNum(null);
                }
                theForm.setUserId(null);
                theForm.setJspPath("channeltransfer/c2cEnquirySearchAttributeView");
                return true;
            }
            final int index = Integer.parseInt(theForm.getSelectedIndex());
            final ChannelTransferVO channelTransferVO = (ChannelTransferVO) theForm.getTransferList().get(index);
            this.constructFormFromVO(theForm, channelTransferVO);
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            final ArrayList itemsList = ChannelTransferBL.loadChannelTransferItemsWithBalances(con, channelTransferVO.getTransferID(), channelTransferVO.getNetworkCode(),
                channelTransferVO.getNetworkCodeFor(), channelTransferVO.getToUserID());
            theForm.setNetworkCodeCheck((boolean) PreferenceCache.getNetworkPrefrencesValue(
					PreferenceI.TARGET_BASED_BASE_COMMISSION,
					theForm.getNetworkCodeFor()));
            long totTax1 = 0L, totTax2 = 0L, totTax3 = 0L, totReqQty = 0L, totStock = 0L, totComm = 0L, totMRP = 0L;
            long totOtf = 0L;
            long mrpAmt = 0L;
            long senderPreStock = 0L;// BUG FIX by AshishT for Mobinil5.7
            long senderPostStock = 0L;// BUG FIX by AshishT for Mobinil5.7
            long receiverPreStock = 0L;// BUG FIX by AshishT for Mobinil5.7
            long receiverPostStock = 0L;// BUG FIX by AshishT for Mobinil5.7
            if (itemsList != null && itemsList.size() > 0) {
                ChannelTransferItemsVO channelTransferItemsVO = null;
                for (int i = 0, j = itemsList.size(); i < j; i++) {
                    channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
                    mrpAmt = channelTransferItemsVO.getRequiredQuantity() * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
                    channelTransferItemsVO.setProductMrpStr(PretupsBL.getDisplayAmount(mrpAmt));
                    totTax1 += channelTransferItemsVO.getTax1Value();
                    totTax2 += channelTransferItemsVO.getTax2Value();
                    totTax3 += channelTransferItemsVO.getTax3Value();
                    totComm += channelTransferItemsVO.getCommValue();
                    totOtf += channelTransferItemsVO.getOtfAmount();
                    
                    totMRP += mrpAmt;
                    totReqQty += channelTransferItemsVO.getRequiredQuantity();
                    totStock += channelTransferItemsVO.getNetworkStock();
                    senderPreStock = channelTransferItemsVO.getSenderPreviousStock();// BUG FIX  by AshishT for Mobinil5.7
                    senderPostStock = channelTransferItemsVO.getSenderPostStock(); //  BUG FIX  by AshishT for Mobinil5.7
                    receiverPreStock = channelTransferItemsVO.getReceiverPreviousStock();//  BUG FIX  by AshishT for Mobinil5.7
                    receiverPostStock = channelTransferItemsVO.getReceiverPostStock();//  BUG FIX  by AshishT for Mobinil5.7

                    
                }
            }
            theForm.setTotalComm(PretupsBL.getDisplayAmount(totComm));
            theForm.setTotalTax1(PretupsBL.getDisplayAmount(totTax1));
            theForm.setTotalTax2(PretupsBL.getDisplayAmount(totTax2));
            theForm.setTotalTax3(PretupsBL.getDisplayAmount(totTax3));
            theForm.setTotalStock(PretupsBL.getDisplayAmount(totStock));
            theForm.setTotalReqQty(PretupsBL.getDisplayAmount(totReqQty));
            theForm.setTotalMRP(PretupsBL.getDisplayAmount(totMRP));
            theForm.setTotOtf(PretupsBL.getDisplayAmount(totOtf));
           // theForm.setSenderPreviousStock(PretupsBL.getDisplayAmount(senderPreStock)); //  BUG FIX  by AshishT for Mobinil5.7
           // theForm.setSenderPostStock(PretupsBL.getDisplayAmount(senderPostStock));//  BUG FIX  by AshishT for Mobinil5.7
           // theForm.setReceiverPreviousStock(PretupsBL.getDisplayAmount(receiverPreStock));//  BUG FIX  by AshishT for Mobinil5.7
            //theForm.setReceiverPostStock(PretupsBL.getDisplayAmount(receiverPostStock));//  BUG FIX  by AshishT for Mobinil5.7

            theForm.setTransferItemsList(itemsList);
            theForm.setJspPath("/channeltransfer/c2cEnquiryViewDetailsView");
        } catch (Exception e) {
            _log.error("enquiryDetail", "Exception:e=" + e);
            _log.errorTrace(METHOD_NAME, e);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("ChnlToChnlEnquiryServiceImpl#enquiryDetail");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("enquiryDetail", "Exiting forward:=" + theForm.getJspPath());
            }
        }
        return true;
    }
	
	
 /**
  * 
 * @param theForm
 * @param p_channelTransferVO
 * @throws ParseException
 */
private void constructFormFromVO(ChnlToChnlEnquiryModel theForm, ChannelTransferVO p_channelTransferVO) throws ParseException {
	        if (_log.isDebugEnabled()) {
	            _log.debug("constructFormFromVO", "Entered theForm  " + theForm + "  p_channelTransferVO  " + p_channelTransferVO);
	        }
	        theForm.setTransferNumber(p_channelTransferVO.getTransferID());
	        theForm.setNetworkCode(p_channelTransferVO.getNetworkCode());
	        theForm.setNetworkCodeFor(p_channelTransferVO.getNetworkCodeFor());
	        theForm.setFromUserName(p_channelTransferVO.getFromUserName());
	        theForm.setToUserName(p_channelTransferVO.getToUserName());
	        theForm.setDomainCode(p_channelTransferVO.getDomainCode());
	        theForm.setGrphDomainCode(p_channelTransferVO.getGraphicalDomainCode());
	        theForm.setSenderGradeCode(p_channelTransferVO.getSenderGradeCode());
	        theForm.setReceiverGradeCode(p_channelTransferVO.getReceiverGradeCode());
	        theForm.setFromUserID(p_channelTransferVO.getFromUserID());
	        theForm.setToUserID(p_channelTransferVO.getToUserID());
	        theForm.setTransferDate(BTSLUtil.getDateStringFromDate(p_channelTransferVO.getTransferDate()));
	        theForm.setReferenceNo(p_channelTransferVO.getReferenceNum());
	        theForm.setRequestedQty(PretupsBL.getDisplayAmount(p_channelTransferVO.getRequestedQuantity()));
	        theForm.setChannelUserRamarks(p_channelTransferVO.getChannelRemarks());
	        theForm.setProductType(p_channelTransferVO.getProductType());
	        theForm.setType(p_channelTransferVO.getType());
	        theForm.setPayableAmt(PretupsBL.getDisplayAmount(p_channelTransferVO.getPayableAmount()));
	        theForm.setNetPayableAmt(PretupsBL.getDisplayAmount(p_channelTransferVO.getNetPayableAmount()));
	        theForm.setTax1(PretupsBL.getDisplayAmount(p_channelTransferVO.getTotalTax1()));
	        theForm.setTax2(PretupsBL.getDisplayAmount(p_channelTransferVO.getTotalTax2()));
	        theForm.setTax3(PretupsBL.getDisplayAmount(p_channelTransferVO.getTotalTax3()));
	        theForm.setTransferSubType(p_channelTransferVO.getTransferSubType());
	        theForm.setTmpFromUserCode(p_channelTransferVO.getFromUserCode());
	        theForm.setTmpToUserCode(p_channelTransferVO.getToUserCode());
	        theForm.setTransferCategoryCodeDesc(p_channelTransferVO.getTransferCategoryCodeDesc());
	        theForm.setSourceTypeDesc(p_channelTransferVO.getSource());
	        theForm.setControlledTxn(p_channelTransferVO.getControlTransfer());
	        theForm.setReceiverCategoryDesc(p_channelTransferVO.getReceiverCategoryDesc());
	        theForm.setReceiverDomainCode(p_channelTransferVO.getReceiverDomainCode());
	        theForm.setReceiverGgraphicalDomainCode(p_channelTransferVO.getReceiverGgraphicalDomainCode());
	        theForm.setSenderCatName(p_channelTransferVO.getSenderCatName());
	        // by gaurav for display cell_id
	        theForm.setCellId(p_channelTransferVO.getCellId());
	        theForm.setSwitchId(p_channelTransferVO.getSwitchId());
	        theForm.setSosStatus(p_channelTransferVO.getSosStatus());
	        if(p_channelTransferVO.getSosSettlementDate()!=null){
	        theForm.setSosSettlementDate(BTSLUtil.getDateStringFromDate(p_channelTransferVO.getSosSettlementDate()));
	        }
	        if (theForm.isStaffEnquiry()) {
	            theForm.setActiveUserName(p_channelTransferVO.getActiveUserName());
	        }
	        if (_log.isDebugEnabled()) {
	            _log.debug("constructFromFromVO", "Exiting");
	        }
	    }
 
 
 /**
	 * Method downloadFileForEnq
	 * This method use for C2C transfer enquiry. 
	 * This method write in xls file and download the xls file.
	 * @param chnlToChnlEnquiryModel
	 * @param model
	 * @param request
	 * @param response
	 */
	public String downloadFileForEnq(ChnlToChnlEnquiryModel chnlToChnlEnquiryModel, Model model,HttpServletRequest request, HttpServletResponse response)throws IOException
	{
		String methodName= "downloadFileForEnq";
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Entered");
		String filePath=null;
		String fileName=null;
		String filelocation =null;
		String fileArr[][]=null;
		String headingArr[][]=null;
		final ChnlToChnlEnquiryModel theForm = chnlToChnlEnquiryModel ;
		try
		{
	        
			ArrayList transferDetalsList = theForm.getTransferList();
			
			filePath = Constants.getProperty("DownloadC2CTransferEnqPath");
			try
			{
				File fileDir = new File(filePath);
				if(!fileDir.isDirectory())
					fileDir.mkdirs();
			}
			catch(SecurityException e)
			{			
				 _log.debug(methodName, "Exception"+e.getMessage());
				 _log.errorTrace(methodName, e);
				throw new BTSLBaseException(this,"downloadFileForEnq","downloadfile.error.dirnotcreated","error");
				 /*model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("downloadfile.error.dirnotcreated"));
                 return false;*/

			}
			fileName = Constants.getProperty("DownloadC2CTransferEnqtFileName")+BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()+".xls";
			//ExcelRW excelRW=new ExcelRW();
			int cols=11;
			
			int rows=transferDetalsList.size()+1;
	        fileArr=new String[rows][cols];
	        int i=0,j=0;
	        String heading="channel.transfer.c2c.xls.enq.fileheading";
	       	headingArr=new String[2][4];
	 	    headingArr[0][j]="channel.transfer.c2c.xls.enq.msisdn";
	 	    j=j+1;
	 	    headingArr[0][j]="channel.transfer.c2c.xls.enq.trftype";
	 	    j=j+1;
	 	    headingArr[0][j]="channel.transfer.c2c.xls.enq.fromdate";
	 	   j=j+1;
	 	    headingArr[0][j]="channel.transfer.c2c.xls.enq.todate";
	 	   j=j+1;
	        
	        fileArr[0][i]="channel.transfer.c2c.xls.enq.transferid";
	        i=i+1;
	        fileArr[0][i]="channel.transfer.c2c.xls.enq.trfsubtype";
	        i=i+1;
	        fileArr[0][i]="channel.transfer.c2c.xls.enq.trfdate";
	        i=i+1;
	        fileArr[0][i]="channel.transfer.c2c.xls.enq.qty";
	        i=i+1;
	        fileArr[0][i]="channel.transfer.c2c.xls.enq.amt";
	        i=i+1;
	        fileArr[0][i]="channel.transfer.c2c.xls.enq.refno";
	        i=i+1;
	        fileArr[0][i]="channel.transfer.c2c.xls.enq.frommsisdn";
	        i=i+1;
	        fileArr[0][i]="channel.transfer.c2c.xls.enq.tomsisdn";
	        i=i+1;
	        fileArr[0][i]="channel.transfer.c2c.xls.enq.trfcat";
	        i=i+1;
	        fileArr[0][i]="channel.transfer.c2c.xls.enq.reqsrc";
	        i=i+1;
	        fileArr[0][i]="channel.transfer.c2c.xls.enq.txnctrltype";
	        i=i+1;
	        
	        fileArr=this.convertTo2dArray(fileArr,transferDetalsList,rows);
	        headingArr=this.convertTo2dArrayHeader(headingArr,theForm);
	        PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
	        pretupsRestUtil.writeExcel(ExcelFileIDI.USER_BAL_ENQ,fileArr, headingArr, heading, 2, BTSLUtil.getBTSLLocale(request),filePath+""+fileName);
	        filelocation = filePath + fileName;	       
	      
	      /*  theForm.setJspPath("/DownloadTemplateUtil");
			String path = theForm.getJspPath();
         path = path+"?fileName="+fileName;
			path = path+"&filePath="+filePath;
			
			  theForm.setJspPath(path);*/
		}
		catch(Exception e)
		{
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
		    
		   //return false;
		}
		finally
		{
		    if (_log.isDebugEnabled())
				_log.debug(methodName, "Exit forward : "+ theForm.getJspPath());
		}
		return filelocation;
	}
	
	/**
	 * method convertTo2dArrayHeader
	 * This method is used to convert ArrayList to 2D String array for header information
	 * @param p_fileArr
	 * @param p_form
	 * @return String[][]
	 */
	private String[][] convertTo2dArrayHeader(String [][]p_fileArr,ChnlToChnlEnquiryModel p_form)
	{
		String methodName= "convertTo2dArrayHeader";
	    if (_log.isDebugEnabled())
			_log.debug(methodName, "Entered p_fileArr="+p_fileArr.length+" p_form="+p_form);
	    try
	    {
		    int rows=1;
		    int cols=0;
			p_fileArr[rows][cols]=p_form.getFromUserCode();
			cols=cols+1;
			p_fileArr[rows][cols]=p_form.getTransferTypeValue();
			cols=cols+1;
			p_fileArr[rows][cols]=p_form.getFromDate();
			cols=cols+1;
			p_fileArr[rows][cols]=p_form.getToDate();
			cols=cols+1;
		    
	    }
	    catch(Exception e)
	    {
	    	 _log.debug(methodName, "Exception"+e.getMessage());
		     _log.errorTrace(methodName, e);
			 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, 
			    		"ChnlToChnEnquiryAction[convertTo2dArrayHeader]", "", "", "", "Exception:" + e.getMessage());
	    }
	    if (_log.isDebugEnabled())
			_log.debug(methodName, "Exit p_fileArr="+p_fileArr);
		return p_fileArr;
	}
	/**
	 * Method convertTo2dArray.
	 * This method is used to convert ArrayList to 2D String array
	 * @param p_fileArr String[][]
	 * @param ArrayList p_batchDetalsList
	 * @param int p_rows
	 * @return p_fileArr String[][]
	 */
	private String[][] convertTo2dArray(String [][]p_fileArr,ArrayList p_transferDetalsList,int p_rows)
	{
		String methodName= "convertTo2dArray";
	    if (_log.isDebugEnabled())
			_log.debug(methodName, "Entered p_fileArr="+p_fileArr.length+" p_transferDetalsList.size()="+p_transferDetalsList.size()+" p_rows"+p_rows);
	    try
	    {
		    Iterator iterator=p_transferDetalsList.iterator();
		    int rows=0;
		    int cols;
		    ChannelTransferVO transferVO=null;
			while (iterator.hasNext())
			{
				transferVO = (ChannelTransferVO)iterator.next();
			    rows++;
			    cols=0;
			    p_fileArr[rows][cols]=transferVO.getTransferID();
			    cols=cols+1;
			    p_fileArr[rows][cols]=transferVO.getTransferSubType();
			    cols=cols+1;
			    p_fileArr[rows][cols]=transferVO.getTransferDateAsString();
			    cols=cols+1;
			    p_fileArr[rows][cols]=transferVO.getRequestedQuantityAsString();
			    cols=cols+1;
			    p_fileArr[rows][cols]=transferVO.getPayableAmountAsString();
			    cols=cols+1;
			    if(BTSLUtil.isNullString(transferVO.getReferenceNum()))
			    	{p_fileArr[rows][cols]=null;
			    cols=cols+1;
			    	}
			    else
			    {
			    	p_fileArr[rows][cols]=transferVO.getReferenceNum();
			    cols=cols+1; 
			    }
			    p_fileArr[rows][cols]=transferVO.getFromUserCode();
			    cols=cols+1;
			    p_fileArr[rows][cols]=transferVO.getToUserCode();
			    cols=cols+1;
			    p_fileArr[rows][cols]=transferVO.getTransferCategoryCodeDesc();
			    cols=cols+1;
			    p_fileArr[rows][cols]=transferVO.getSource();
			    cols=cols+1;
			    if(PretupsI.YES.equalsIgnoreCase(transferVO.getControlTransfer()))
			    	{p_fileArr[rows][cols]="Controlled";
			    	cols=cols+1;
			    	}
			    else if(PretupsI.NO.equalsIgnoreCase(transferVO.getControlTransfer()))
			    	{p_fileArr[rows][cols]="Uncontrolled";
			    cols=cols+1;
			    }
			    else if(PretupsI.CONTROL_LEVEL_ADJ.equalsIgnoreCase(transferVO.getControlTransfer()))
			    	{p_fileArr[rows][cols]="Adjustment";
			    cols=cols+1;
			    }
			    
			}
	    }
	    catch(Exception e)
	    {
	        _log.debug(methodName, "Exception"+e.getMessage());
	        _log.errorTrace(methodName, e);
		    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, 
		    		"ChnlToChnEnquiryAction[convertTo2dArray]", "", "", "", "Exception:" + e.getMessage());
		    
	    }
	    if (_log.isDebugEnabled())
			_log.debug(methodName, "Exit p_fileArr="+p_fileArr);
		
		return p_fileArr;
	}
	
}
