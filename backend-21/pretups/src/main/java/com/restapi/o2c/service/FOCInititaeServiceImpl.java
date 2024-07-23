package com.restapi.o2c.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLUtil;
import com.fasterxml.jackson.databind.JsonNode;


/**
 * 
 * @author yogesh.dixit
 *
 */
@Service("FOCInititaeServiceI")
public class FOCInititaeServiceImpl implements FOCInititaeServiceI {
	public static final Log log = LogFactory.getLog(FOCInititaeServiceImpl.class.getName());
	@Override
	public BaseResponseMultiple<JsonNode> processFocInitiateRequest(FocInitiateRequestVO focInitiateRequestVO,
			HttpServletResponse response1) {
	   String methodName = "processFocInitiateRequest";
	   Connection con = null;MComConnectionI mcomCon = null;
	   BaseResponseMultiple<JsonNode> baseResponseMultiple =null;
	   final ArrayList<FocTransferInitaiateReqData> focInitiateRequestList = focInitiateRequestVO.getDatafoc();
	   ChannelUserVO userVO =  null;
	   ErrorMap errorMap = new ErrorMap();
	   String userpin;
        try {
              mcomCon = new MComConnection();con=mcomCon.getConnection();
              baseResponseMultiple=new BaseResponseMultiple<>();
      		  ArrayList<BaseResponse> baseResponseFinalSucess = new ArrayList<>();
              userVO = new ChannelUserVO();
              if(errorMap.getMasterErrorList() == null || errorMap.getMasterErrorList().size() == 0) {
            	  errorMap.setMasterErrorList(new ArrayList<MasterErrorList>());
              }
             if(errorMap.getRowErrorMsgLists() == null || errorMap.getRowErrorMsgLists().size() == 0) {
            	 errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
              }
              if(focInitiateRequestList.size() >0 ) {
            	 userpin =  focInitiateRequestList.get(0).getPin();
              }else {
            	  throw new BTSLBaseException("FOCInititaeServiceImpl", methodName,
							PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
              }
              userVO.setLoginID(focInitiateRequestVO.getData().getLoginid());
              userVO.setPassword(focInitiateRequestVO.getData().getPassword());
              //Loading Sender Details
              userVO = getOperatorDetails(con,userVO);
              try {
            	 if(PretupsI.YES.equals(userVO.getCategoryVO().getSmsInterfaceAllowed())) {
            		 ChannelUserBL.validatePIN(con, userVO, userpin);
            	 }
         	 }catch(BTSLBaseException e) {
         		 log.error(methodName, "BTSLBaseException " + e.getMessage());
      	         log.errorTrace(methodName, e);
      	         throw e;
         	 }
              int failcounter = 0;
              if(errorMap.getMasterErrorList() == null || errorMap.getMasterErrorList().size() == 0) {
            	  errorMap.setMasterErrorList(new ArrayList<MasterErrorList>());
              }
             if(errorMap.getRowErrorMsgLists() == null || errorMap.getRowErrorMsgLists().size() == 0) {
            	 errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
              }
              for(int i =0 ;i<focInitiateRequestList.size();i++) {
            	boolean processed =  processFoCInitiate(userVO, focInitiateRequestList.get(i), errorMap,baseResponseFinalSucess,i+1); 
            	if(!processed) {
            		failcounter++;
            	}
              }
            int successcount = focInitiateRequestList.size() - failcounter;
            if(successcount > 0) {
            	
            baseResponseMultiple.setSuccessList(baseResponseFinalSucess);
			baseResponseMultiple.setMessageCode("200");
			if(failcounter == 0) {
				baseResponseMultiple.setErrorMap(new ErrorMap());
			}else {
				baseResponseMultiple.setErrorMap(errorMap);
			}
			baseResponseMultiple.setMessage(successcount+" out of "+focInitiateRequestList.size()+ " record(s) processed successfully");
			baseResponseMultiple.setStatus("200");
			baseResponseMultiple.setService("FOC" + "RESP");
			return baseResponseMultiple;
			}
            else {
            	baseResponseMultiple.setSuccessList(baseResponseFinalSucess);
    			baseResponseMultiple.setMessageCode("400");
    			baseResponseMultiple.setErrorMap(errorMap);
    			baseResponseMultiple.setMessage("All record contains error");
    			baseResponseMultiple.setStatus("400");
    			baseResponseMultiple.setService("FOC" + "RESP");
    			return baseResponseMultiple;
            }
        }catch(BTSLBaseException e) {
			log.error(methodName, "BTSLBaseException " + e.getMessage());
	        log.errorTrace(methodName, e);
	        if(e.getMessageKey()==null){ 
	        	
	        	MasterErrorList err = new MasterErrorList();
	        	err.setErrorCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
	        	err.setErrorMsg(RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
						null));
	        	errorMap.getMasterErrorList().add(err); 	
	        	baseResponseMultiple.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
				baseResponseMultiple.setErrorMap(errorMap);
				baseResponseMultiple.setMessage(RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
						null));
				baseResponseMultiple.setStatus("400");
	        }
	        else{
	        	MasterErrorList err = new MasterErrorList();
	        	String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), e.getMessageKey(),
					null);
	        	err.setErrorCode(e.getMessageKey());
	        	err.setErrorMsg(resmsg);
	        	errorMap.getMasterErrorList().add(err);
	        	baseResponseMultiple.setMessageCode(e.getMessageKey());
				baseResponseMultiple.setErrorMap(errorMap);
				baseResponseMultiple.setMessage(resmsg);
				baseResponseMultiple.setStatus("400");
	        } 
        }
        catch(Exception e) {
			log.error(methodName, "Exception " + e.getMessage());
	        log.errorTrace(methodName, e);
	        MasterErrorList err = new MasterErrorList();
	        err.setErrorCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
	        String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
					null);
	        err.setErrorMsg(resmsg);
	        baseResponseMultiple.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			baseResponseMultiple.setErrorMap(errorMap);
			baseResponseMultiple.setMessage(resmsg);
			baseResponseMultiple.setStatus("400");
		}finally {
			if (mcomCon != null) {
				mcomCon.close("FOCInititaeServiceImpl#processFocInitiateRequest");
				mcomCon = null;
			 }
			 LogFactory.printLog(methodName, " Exited ", log);
			 baseResponseMultiple.setService("FOC" + "RESP");
        }
		return baseResponseMultiple;
		
	}
	
	
	public boolean processFoCInitiate(ChannelUserVO UserVO,FocTransferInitaiateReqData focInitiateRequestData,ErrorMap errorMap,ArrayList<BaseResponse> baseResponseFinalSucess,int rownum) {
		String methodName = "processFoCInitiate";
		if(errorMap.getRowErrorMsgLists().size() == 0) {
			errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
		} 
		
		RowErrorMsgLists rowRecord = new RowErrorMsgLists();
		rowRecord.setRowName("Record"+rownum+"");
		rowRecord.setRowValue(rownum+"");
		List<RowErrorMsgList> currentRowErrors  = new ArrayList<RowErrorMsgList>();
		RowErrorMsgList err = new RowErrorMsgList();
		List<RowErrorMsgLists> currentRowErrors2  = new ArrayList<RowErrorMsgLists>();
		err.setRowErrorMsgLists(currentRowErrors2);
		currentRowErrors.add(err);
		rowRecord.setRowErrorMsgList(currentRowErrors);
		ChannelUserVO channelUserVO = null;
		Connection con = null;MComConnectionI mcomCon = null;
		try {
			 mcomCon = new MComConnection();
			 con=mcomCon.getConnection();
			 channelUserVO = new ChannelUserVO();
			// validateRequestData(focInitiateRequestData,currentRowErrors);
             channelUserVO.setUserCode(focInitiateRequestData.getMsisdn2());
             channelUserVO.setMsisdn(focInitiateRequestData.getMsisdn2());
             //Loading Reciver Details
             channelUserVO =  getUserDetails(con, channelUserVO,UserVO);
             //Loading Products assigned to user
             loadUserProducts(con, UserVO,channelUserVO);
             String language1str = focInitiateRequestData.getLanguage1();
             String language2str = focInitiateRequestData.getLanguage2();
             if(language1str != null && language1str.length() > 29 ) {
            	 throw  new BTSLBaseException("FOCInititaeServiceImpl", methodName,
     					PretupsErrorCodesI.FOC_DEF_LEN_LANG1);
             }
             if(language2str != null && language2str.length() > 29 ) {
            	 throw  new BTSLBaseException("FOCInititaeServiceImpl", methodName,
     					PretupsErrorCodesI.FOC_DEF_LEN_LANG2);
             }
             
             ChannelTransferItemsVO channelTransferItemsVO = null;
             FOCProduct fOCProduct = null;
             final ArrayList fromArrayList = focInitiateRequestData.getFocProducts();
             if(fromArrayList == null || fromArrayList.size() == 0) {
            	 throw  new BTSLBaseException("FOCInititaeServiceImpl", methodName,
      					PretupsErrorCodesI.FOC_NULL_PRODUCT);
             }else {
            	boolean flag = false;
            	 for(int j =0 ;j<fromArrayList.size();j++) {
	           		  fOCProduct  = (FOCProduct) fromArrayList.get(j);
	           		  if(fOCProduct.getAppQuantity()!= null && !BTSLUtil.isNullString(fOCProduct.getAppQuantity())) {
	           			flag = true;
	           			break;
	           		  }
            	 }
            	 
            	 if(!flag) {
            		 throw  new BTSLBaseException("FOCInititaeServiceImpl", methodName,
           					PretupsErrorCodesI.FOC_BLANK_QTY);
            	 }
             }
             
             final ArrayList productList  = channelUserVO.getProductsList();
           // to make the diffrent refrence of current ArrayList
             final ArrayList itemsList = new ArrayList();
             for(int i =0 ;i<productList.size();i++) {
	           	channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(i);
	           	  for(int j =0 ;j<fromArrayList.size();j++) {
	           		  fOCProduct  = (FOCProduct) fromArrayList.get(j);
	           		  if(fOCProduct.getProductCode().equals(channelTransferItemsVO.getProductCode())) {
	           			  channelTransferItemsVO.setRequestedQuantity(fOCProduct.getAppQuantity());
	           			  itemsList.add(channelTransferItemsVO);
	           			  break;
	           		  }
           	    }
           	
           }
             
             if(itemsList == null || itemsList.size() ==0) {
            	 throw  new BTSLBaseException("FOCInititaeServiceImpl", methodName,
     					PretupsErrorCodesI.PRODUCTS_NOT_FOUND);
             }
             
             
             
           // load the tax and commission of the products frrom data bas
           // according to the user commission profile
           final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
           channelTransferVO.setChannelTransferitemsVOList(itemsList);
           channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
           channelTransferVO.setDualCommissionType(channelUserVO.getDualCommissionType());
           channelTransferVO.setOtfFlag(false);
           
           ChannelTransferBL.loadAndCalculateTaxOnProducts(con, channelUserVO.getCommissionProfileSetID(), channelUserVO.getCommissionProfileSetVersion(), channelTransferVO, false,
               "TransferDetails", PretupsI.TRANSFER_TYPE_FOC);
           // TransferDetails
           long totTax1 = 0, totTax2 = 0, totRequestedQty = 0, totTransferedAmt = 0, totalMRP = 0;

           ChannelTransferItemsVO transferItemsVO = null;
           for (int i = 0, k = itemsList.size(); i < k; i++) {
               transferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);

               totTax1 += transferItemsVO.getTax1Value();
               totTax2 += transferItemsVO.getTax2Value();
               if (transferItemsVO.getRequestedQuantity() != null && BTSLUtil.isDecimalValue(transferItemsVO.getRequestedQuantity())) {
                   totRequestedQty += PretupsBL.getSystemAmount(transferItemsVO.getRequestedQuantity());
                   totTransferedAmt += (Double.parseDouble(transferItemsVO.getRequestedQuantity()) * transferItemsVO.getUnitValue());
               }

               totalMRP += transferItemsVO.getProductTotalMRP();
           }
           channelTransferVO.setTotalTax1(PretupsBL.getSystemAmount(totTax1));
           channelTransferVO.setTotalTax2(PretupsBL.getSystemAmount(totTax2));
           channelTransferVO.setRequestedQuantity(PretupsBL.getSystemAmount(totRequestedQty));
           // in case of FOC Tax3, Payable Amount, Net Payable Amount, Pay Ins
           // Amount all be zero
           channelTransferVO.setTotalTax3(0);
           channelTransferVO.setPayableAmount(0);
           channelTransferVO.setNetPayableAmount(0);
           channelTransferVO.setPayInstrumentAmt(0);
           channelTransferVO.setTransferMRP(PretupsBL.getSystemAmount(totTransferedAmt));
           channelTransferVO.setNetworkCode(UserVO.getNetworkID());
           channelTransferVO.setCreatedBy(UserVO.getUserID());
           channelTransferVO.setModifiedBy(UserVO.getUserID());
           channelTransferVO.setTransferInitatedBy(UserVO.getUserID());
           if(!BTSLUtil.isNullString(UserVO.getActiveUserID()))
           channelTransferVO.setActiveUserId(UserVO.getActiveUserID());
           else
           channelTransferVO.setActiveUserId(UserVO.getUserID());
           channelTransferVO.setDefaultLang(focInitiateRequestData.getLanguage1());
           channelTransferVO.setSecondLang(focInitiateRequestData.getLanguage2());
           channelTransferVO.setReferenceNum(focInitiateRequestData.getRefnumber());
           channelTransferVO.setChannelRemarks(focInitiateRequestData.getRemarks());
           channelTransferVO.setChannelTransferitemsVOList(itemsList); 
           constructChannelTransferVO(channelTransferVO,channelUserVO,UserVO );
        
           final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
           if (SystemPreferences.MULTIPLE_WALLET_APPLY) {
               channelTransferVO.setWalletType(PretupsI.FOC_WALLET_TYPE);
           }else {
        	   channelTransferVO.setWalletType(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
           }
           ChannelTransferBL.genrateTransferID(channelTransferVO);
           final int count = channelTransferDAO.addChannelTransfer(con, channelTransferVO);
           if (count > 0) {
              mcomCon.finalCommit();
               final String args[] = { channelTransferVO.getTransferID() };
               BaseResponse baseResponse = new BaseResponse();
               baseResponse.setMessage(RestAPIStringParser.getMessage(
       				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.O2C_VOUCHER_TRF_SUCCESS,args));
       				baseResponse.setMessageCode(PretupsErrorCodesI.O2C_VOUCHER_TRF_SUCCESS);
       				baseResponse.setStatus(200);
       				baseResponse.setTransactionId(channelTransferVO.getTransferID());
       				baseResponseFinalSucess.add(baseResponse);
       				return true;
           } else {
              mcomCon.finalRollback();
               throw new BTSLBaseException(this, methodName, "channeltransfer.transferdetailsfocview.message.focunsuccess", "FOCSuccess");
           }
		}catch (BTSLBaseException be) {
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			} 
			catch (SQLException esql) {
				log.error(methodName,"SQLException : ", esql.getMessage());
			}
			log.error(methodName, "BTSLBaseException " + be.getMessage());
	        log.errorTrace(methodName, be);
	        if(be.getMessageKey()==null){ 
	        	RowErrorMsgLists err1 = new RowErrorMsgLists();
	        	err1.setRowName(PretupsErrorCodesI.REQ_NOT_PROCESS);
	        	err1.setRowValue(RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
						null));
	        	rowRecord.getRowErrorMsgList().get(0).getRowErrorMsgLists().add(err1);
	        }
	        else{
	        	RowErrorMsgLists err1 = new RowErrorMsgLists();
	        	String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
					null);
	        	err1.setRowName(be.getMessageKey());
	        	err1.setRowValue(resmsg);
	        	rowRecord.getRowErrorMsgList().get(0).getRowErrorMsgLists().add(err1);
	        } 
	        return false;
	    }
		
		catch(Exception e) {
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			} 
			catch (SQLException esql) {
				log.error(methodName,"SQLException : ", esql.getMessage());
			}
			log.error(methodName, "Exception " + e.getMessage());
	        log.errorTrace(methodName, e);
	        RowErrorMsgLists err1 = new RowErrorMsgLists();
	        err1.setRowName(PretupsErrorCodesI.REQ_NOT_PROCESS);
	        String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
					null);
	        err1.setRowValue(resmsg);
	        rowRecord.getRowErrorMsgList().get(0).getRowErrorMsgLists().add(err1);
	        return false;
		}finally {
			if (mcomCon != null) {
				mcomCon.close("FOCInititaeServiceImpl#processFocInitiateRequest");
				mcomCon = null;
			 }
			errorMap.getRowErrorMsgLists().add(rowRecord);
			 LogFactory.printLog(methodName, " Exited ", log);
		}
	}
	 
	
	


	private ChannelUserVO getUserDetails( Connection con,ChannelUserVO reciverVO, ChannelUserVO UserVO) throws BTSLBaseException {
   
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        final Date curDate = new Date();
        ChannelUserVO channelUserVO = null;
        UserPhoneVO phoneVO = null;
        final UserDAO userDAO = new UserDAO();
         String  methodName = "getUserDetails";
        try {
        if (SystemPreferences.SECONDARY_NUMBER_ALLOWED) {
            phoneVO = userDAO.loadUserAnyPhoneVO(con,reciverVO.getMsisdn());
            if (phoneVO == null) {
            	throw  new BTSLBaseException("FOCInititaeServiceImpl", methodName,
							PretupsErrorCodesI.CHANNEL_USR_NOT_FOUND);
            }
            channelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, phoneVO.getUserId(), false, curDate,false);
            if (channelUserVO == null) {
            	throw  new BTSLBaseException("FOCInititaeServiceImpl", methodName,
						PretupsErrorCodesI.CHANNEL_USR_NOT_FOUND);
            }
            if (!("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
                channelUserVO.setPrimaryMsisdn(channelUserVO.getMsisdn());
                channelUserVO.setMsisdn(phoneVO.getMsisdn());
            } else {
                channelUserVO.setPrimaryMsisdn(channelUserVO.getMsisdn());
            }
        } else {
            channelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, reciverVO.getMsisdn(), true, curDate,false);
        }

        boolean receiverStatusAllowed= false ;
		if (channelUserVO == null) {
			throw  new BTSLBaseException("FOCInititaeServiceImpl", methodName,
					PretupsErrorCodesI.CHANNEL_USR_NOT_FOUND);
        } else {
            final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(), channelUserVO
                .getUserType(), PretupsI.REQUEST_SOURCE_TYPE_REST);
            if (userStatusVO != null) {
                final String userStatusAllowed = userStatusVO.getUserReceiverAllowed();
                final String status[] = userStatusAllowed.split(",");
                for (int i = 0; i < status.length; i++) {
                    if (status[i].equals(channelUserVO.getStatus())) {
                        receiverStatusAllowed  = true;
                    }
                }
            } else {
            	throw new BTSLBaseException("FOCInititaeServiceImpl", methodName,
                 		PretupsErrorCodesI.FOC_USER_STATUS_NOT_CONGIGURED);
            }
        }
        if (!receiverStatusAllowed) {
        	throw new BTSLBaseException("FOCInititaeServiceImpl", methodName,
        			"channeltransfer.approval.msg.usersuspended");
        } else if (channelUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
            throw new BTSLBaseException("FOCInititaeServiceImpl", methodName,
        			"channeltransfer.approval.msg.nocommprofileapplicable");
        }

        if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
          //  final Locale locale = BTSLUtil.getBTSLLocale(request);
            String args[] = null;
            args = new String[] { reciverVO.getMsisdn(), channelUserVO.getCommissionProfileLang2Msg() };
           // final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
          /*  if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                args = new String[] {msisdn2, channelUserVO.getCommissionProfileLang1Msg() };
            }*/
            throw new BTSLBaseException("FOCInititaeServiceImpl", methodName,
        			"commissionprofile.notactive.msg");            
        } else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
            final String args[] = {reciverVO.getMsisdn()};
            throw new BTSLBaseException("FOCInititaeServiceImpl", methodName,
            		PretupsErrorCodesI.ERROR_TRANSFER_PROFILE_SUSPENDED, args);
        }

        // to check user status
        if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
        	throw new BTSLBaseException("FOCInititaeServiceImpl", methodName,
            		PretupsErrorCodesI.FOC_USER_IN_SUS); 
        }
        // to check the domain of the user with the domain of the logged
        // in user
        final ArrayList domainList = UserVO.getDomainList();
       if (domainList!= null && domainList.size()>0) {
            ListValueVO listValueVO = null;
            boolean domainfound = false;
           
            for (int i = 0, j = domainList.size(); i < j; i++) {
                listValueVO = (ListValueVO) domainList.get(i);
                if (channelUserVO.getDomainID().equals(listValueVO.getValue())) {
                    domainfound = true;
                    break;
                }
            }
            if (!domainfound) {
            	throw new BTSLBaseException("FOCInititaeServiceImpl", methodName,
                		PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_SEQUENCE_NOT_BELOW);
            }
        }
        // now check that is user down in the geographical domain of the
        // loggin user or not.
        final GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
        if (!geographicalDomainDAO.isGeoDomainExistInHierarchy(con, channelUserVO.getGeographicalCode(),UserVO.getUserID() )) {
        	throw  new BTSLBaseException("FOCInititaeServiceImpl", methodName,
					PretupsErrorCodesI.TO_USER_GEOGRAPHY_INVALID);
        	
           }
	     }
        catch (Exception e) {
            log.error(methodName, "Exception:e= " + e);
            log.errorTrace(methodName, e);
            throw new BTSLBaseException("FOCInititaeServiceImpl", methodName,
					e.getMessage());
        } finally {
            if (log.isDebugEnabled()) {
            }
        }
       return channelUserVO;
	}
	
	 /**
     * This method loads the associated products of the selected product type
     * 
     * 
     * @param p_con
     *            Connection
     * @param mapping
     * @param form
     * @param request
     * @return ActionForward
	 * @throws BTSLBaseException 
     */
    private void loadUserProducts(Connection p_con,ChannelUserVO UserVO,ChannelUserVO channelUserVO) throws BTSLBaseException {
        final String methodName = "loadUserProducts";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        try {
            final Date curDate = new Date();
            final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
           
            final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(p_con, channelUserVO.getNetworkID(), channelUserVO.getDomainID(),
                PretupsI.CATEGORY_TYPE_OPT, channelUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);

            if (channelTransferRuleVO == null) {	
            	throw  new BTSLBaseException("FOCInititaeServiceImpl", methodName,
    					"message.channeltransfer.transferrulenotexist");
            } else if (PretupsI.NO.equals(channelTransferRuleVO.getFocAllowed())) {
            	throw  new BTSLBaseException("FOCInititaeServiceImpl", methodName,
    					"message.channeltransfer.transferrulenotdefine");
            } else if (channelTransferRuleVO.getProductVOList() == null || channelTransferRuleVO.getProductVOList().isEmpty()) {
            	throw  new BTSLBaseException("FOCInititaeServiceImpl", methodName,
    					"message.transfer.noproductassigned.transferrule");
            }
            // load the product list according to the active network product
            // mapping and
            // according associated commission profile set ID.
            ArrayList list = ChannelTransferBL.loadO2CXfrProductList(p_con, UserVO.getProductCode(), channelUserVO.getNetworkID(), channelUserVO.getCommissionProfileSetID(),
                curDate, null);
            /**
             * User associated with commission profile.Commission profile
             * associated with products. Display only those products which have
             * commission profile same as user�s commission profile. If above
             * condition fail then display error message.
             */

            if (list.isEmpty()) {
            	throw  new BTSLBaseException("FOCInititaeServiceImpl", methodName,
    					"channeltransfer.transfer.errormsg.noproducttype");
            }

            // now further filter the list with the transfer rules list and the
            // above list of commission profile products.
            list = filterProductWithTransferRule(list, channelTransferRuleVO.getProductVOList());

            /**
             * This case arises
             * suppose in transfer rule products A and B are associated
             * In commission profile product C and D are associated.
             * We load product with intersection of transfer rule products and
             * commission profile products.
             * if no product found then display below message
             * 
             */
            if (list.isEmpty()) {
            	throw  new BTSLBaseException("FOCInititaeServiceImpl", methodName,
    					"message.transfer.noproductassigned.o2c.transferrule");
            }

            // set in the VO while initiating order in saveFOCOrder() mehtod
            channelUserVO.setTransferCategory(channelTransferRuleVO.getFocTransferType());
            channelUserVO.setProductsList(list);
            for (final Iterator<ChannelTransferItemsVO> iterator = list.iterator(); iterator.hasNext();) {
                final ChannelTransferItemsVO transferItemVO = iterator.next();
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                    transferItemVO.setUserWallet(null);
                }
            }
        } catch (Exception e) {
            log.error(methodName, "Exception:e= " + e);
            log.errorTrace(methodName, e);
            throw new BTSLBaseException("FOCInititaeServiceImpl", methodName,
					e.getMessage());
        } finally {
            if (log.isDebugEnabled()) {
            }
        }
    }

    /**
     * Filter the product on the bases of transfer rule
     * 
     * @param p_productList
     * @param p_productListWithXfrRule
     * @return ArrayList
     */
    /*
     * this method returns the list of products, which are comman in the both of
     * the arrayLists
     */
    private ArrayList filterProductWithTransferRule(ArrayList p_productList, ArrayList p_productListWithXfrRule) {
        if (log.isDebugEnabled()) {
            log.debug("filterProductWithTransferRule", "Entered p_productList: " + p_productList.size() + " p_productListWithXfrRule: " + p_productListWithXfrRule.size());
        }
        ChannelTransferItemsVO channelTransferItemsVO = null;
        ListValueVO listValueVO = null;
        final ArrayList tempList = new ArrayList();
        for (int m = 0, n = p_productList.size(); m < n; m++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) p_productList.get(m);
            for (int i = 0, k = p_productListWithXfrRule.size(); i < k; i++) {
                listValueVO = (ListValueVO) p_productListWithXfrRule.get(i);
                if (channelTransferItemsVO.getProductCode().equals(listValueVO.getValue())) {
                    tempList.add(channelTransferItemsVO);
                    break;
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("filterProductWithTransferRule", "Exiting tempList: " + tempList.size());
        }

        return tempList;
    }
    
    
    public ChannelUserVO  getOperatorDetails(Connection p_con,ChannelUserVO sender) throws BTSLBaseException {
    	 OperatorUtilI _operatorUtil = null;
    	
        LoginDAO _loginDAO = new LoginDAO();
      String   methodName ="getOperatorDetails";
        try {
			sender = _loginDAO.loadUserDetails(p_con, sender.getLoginID(), sender.getPassword(), new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            
			UserDAO userDao = new UserDAO();
            
            UserPhoneVO phoneVO = userDao.loadUserPhoneVO(p_con, sender.getUserID());
            if (phoneVO != null) {
            	sender.setActiveUserMsisdn(phoneVO.getMsisdn());
            	sender.setActiveUserPin(phoneVO.getSmsPin());
            }
            
            sender.setUserPhoneVO(phoneVO);
			
			if (PretupsI.STAFF_USER_TYPE.equals(sender.getUserType())) {
               // validateStaffLoginDetails(con, theForm, request, mapping, loginLoggerVO, channelUserVO);
                
                // Set Staff User Details
                ChannelUserVO staffUserVO = new ChannelUserVO();
                UserPhoneVO staffphoneVO = new UserPhoneVO();
                BeanUtils.copyProperties(staffUserVO, sender);
                if (phoneVO != null) {
                    BeanUtils.copyProperties(staffphoneVO, phoneVO);
                    staffUserVO.setUserPhoneVO(staffphoneVO);
                }
                staffUserVO.setPinReset(sender.getPinReset());
                sender.setStaffUserDetails(staffUserVO);
                ChannelUserVO parentChannelUserVO = new UserDAO().loadUserDetailsFormUserID(p_con, sender.getParentID());

                staffUserDetails(sender, parentChannelUserVO);

                sender.setPrefixId(parentChannelUserVO.getPrefixId());
            }
            GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
            // load the geographies info from the user_geographies
            ArrayList geographyList = _geographyDAO.loadUserGeographyList(p_con, sender.getUserID(), sender.getNetworkID());
            sender.setGeographicalAreaList(geographyList);

            // load the domain of the user that are associated with it
            DomainDAO domainDAO = new DomainDAO();
            sender.setDomainList(domainDAO.loadDomainListByUserId(p_con, sender.getUserID()));
            String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            try {
                _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (PretupsI.YES.equals(sender.getCategoryVO().getProductTypeAllowed())) {
            	sender.setAssociatedProductTypeList(new ProductTypeDAO().loadUserProductsListForLogin(p_con, sender.getUserID()));
            } else {
            	sender.setAssociatedProductTypeList(_operatorUtil.loadProductCodeList());
            }
            
            
         // load the Product Type list
            final ArrayList prodTypListtemp = new ArrayList(sender.getAssociatedProductTypeList());
            
           
            if (prodTypListtemp.isEmpty()) {
              /*  final BTSLMessages btslMessage = new BTSLMessages("channeltransfer.selectcategoryforfoctransfer.errormsg.noproducttype", jspForward);
                forward = super.handleMessage(btslMessage, request, mapping);*/
            }

            // if there is only one product associated with user then there
            // will be
            // no drop down will appear on the screen. just dispaly the
            // product type
            if (prodTypListtemp.size() >= 1) {
            	ListValueVO listValueVO = (ListValueVO) prodTypListtemp.get(0);
            	sender.setProductCode(listValueVO.getValue());
            } 
            	sender.setProductsList(prodTypListtemp);
            
		} catch (BTSLBaseException | IllegalAccessException | InvocationTargetException e) {
			log.error(methodName, "Exception:e= " + e);
            log.errorTrace(methodName, e);
            throw new BTSLBaseException(e);
		}catch (Exception e) {
            log.error(methodName, "Exception:e= " + e);
            log.errorTrace(methodName, e);
            throw new BTSLBaseException(e);
        } finally {
            if (log.isDebugEnabled()) {
            }
        }
    	return sender;
    	
    }
    
    
    
    /**
     * Common Method used for set the login details for StaffUser
     * 
     * @param channelUserVO
     * @param parentChannelUserVO
     */

    protected void staffUserDetails(ChannelUserVO channelUserVO, ChannelUserVO parentChannelUserVO) {
        channelUserVO.setUserID(channelUserVO.getParentID());
        channelUserVO.setParentID(parentChannelUserVO.getParentID());
        channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
        channelUserVO.setStatus(parentChannelUserVO.getStatus());
        channelUserVO.setUserType(parentChannelUserVO.getUserType());
        channelUserVO.setStaffUser(true);
        channelUserVO.setMsisdn(parentChannelUserVO.getMsisdn());
        channelUserVO.setPinRequired(parentChannelUserVO.getPinRequired());
        channelUserVO.setSmsPin(parentChannelUserVO.getSmsPin());
        channelUserVO.setParentLoginID(parentChannelUserVO.getLoginID());
    }
    
    private void constructChannelTransferVO(ChannelTransferVO p_channelTransferVO,ChannelUserVO channelUserVO,ChannelUserVO UserVO) throws Exception {
        final String methodName = "constructChannelTransferVO";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_channelTransferVO = " + p_channelTransferVO);
        }
        final Date currDate = new Date();
        p_channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setToUserID(channelUserVO.getUserID());
        p_channelTransferVO.setToUserName(channelUserVO.getUserName());
        // p_channelTransferVO.setGraphicalDomainCode(theForm.getGeographicalDomainCode());
        p_channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
        p_channelTransferVO.setDomainCode(channelUserVO.getDomainID());
        p_channelTransferVO.setNetworkCodeFor(UserVO.getNetworkID());
        p_channelTransferVO.setReceiverCategoryCode(channelUserVO.getCategoryCode());
        p_channelTransferVO.setReceiverGradeCode(channelUserVO.getUserGrade());
        p_channelTransferVO.setCommProfileSetId(channelUserVO.getCommissionProfileSetID());
        //p_channelTransferVO.setExternalTxnNum(channelUserVO);
        /*if (!BTSLUtil.isNullString(channelUserVO.getExternalTxnDate())) {
            p_channelTransferVO.setExternalTxnDate(BTSLUtil.getDateFromDateString(theForm.getExternalTxnDate()));
        }*/
        p_channelTransferVO.setCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
        p_channelTransferVO.setTransferDate(currDate);
        p_channelTransferVO.setCommProfileVersion(channelUserVO.getCommissionProfileSetVersion());
        p_channelTransferVO.setDualCommissionType(channelUserVO.getDualCommissionType());
        p_channelTransferVO.setCreatedOn(currDate);
        p_channelTransferVO.setModifiedOn(currDate);
        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
        p_channelTransferVO.setReceiverTxnProfile(channelUserVO.getTransferProfileID());
        p_channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
        p_channelTransferVO.setRequestGatewayCode(PretupsI.REQUEST_SOURCE_WEB);
        p_channelTransferVO.setRequestGatewayType(PretupsI.REQUEST_SOURCE_WEB);
        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
        p_channelTransferVO.setProductType(UserVO.getProductCode());
        p_channelTransferVO.setTransferCategory(channelUserVO.getTransferCategory());
        p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
        p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
        p_channelTransferVO.setControlTransfer(PretupsI.YES);
        p_channelTransferVO.setReceiverGgraphicalDomainCode(p_channelTransferVO.getGraphicalDomainCode());
        p_channelTransferVO.setReceiverDomainCode(channelUserVO.getDomainID());
        p_channelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(channelUserVO.getMsisdn()));
        p_channelTransferVO.setUserWalletCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting p_channelTransferVO= " + p_channelTransferVO);
        }
    }  
    
}






     