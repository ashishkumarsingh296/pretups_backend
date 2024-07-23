package com.restapi.superadmin.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
////import org.apache.struts.action.ActionForward;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.RequestGatewayVO;
import com.btsl.pretups.gateway.businesslogic.ResponseGatewayVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.channeluser.service.ReprintVoucherController;
import com.restapi.superadmin.AddMessageGatewayController;
import com.restapi.superadmin.responseVO.MessageGatewayDetailResponseVO;
import com.restapi.superadmin.serviceI.AddMessageGatewayServiceI;
import com.restapi.superadminVO.AddMessGatewayVO;
import com.restapi.superadminVO.GatewayListResponseVO;
import com.restapi.superadminVO.MessGatewayVO;
import com.restapi.superadminVO.ReqGatewayVO;
import com.restapi.superadminVO.ResGatewayVO;
import com.web.pretups.gateway.businesslogic.MessageGatewayWebDAO;


@Service("AddMessageGatewayServiceI")
public class AddMessageGatewayServiceImpl implements AddMessageGatewayServiceI{
	
	public static final Log log = LogFactory.getLog(AddMessageGatewayController.class.getName());
	public static final String classname = "AddMessageGatewayServiceImpl";

	@Override
	public GatewayListResponseVO loadGatewaysList(Connection con, Locale locale, HttpServletResponse response1) {
		// TODO Auto-generated method stub
		final String METHOD_NAME = "loadGatewaysList";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		MessageGatewayWebDAO messageGatewaywebDAO = null;
		
		GatewayListResponseVO response = new GatewayListResponseVO();
		ArrayList gatewayTypeList = new ArrayList();
		ArrayList gatewaySubTypeList = new ArrayList();
		try {
			messageGatewaywebDAO = new MessageGatewayWebDAO();
			
//			response.setGatewayTypeList(messageGatewaywebDAO.loadGatewayTypeList(con, PretupsI.GATEWAY_DISPLAY_ALLOW_YES, PretupsI.GATEWAY_MODIFIED_ALLOW_YES));
//            response.setGatewaySubTypeList(messageGatewaywebDAO.loadGatewaySubTypeList(con));
			
			gatewayTypeList = messageGatewaywebDAO.loadGatewayTypeList(con, PretupsI.GATEWAY_DISPLAY_ALLOW_YES, PretupsI.GATEWAY_MODIFIED_ALLOW_YES);
			if (gatewayTypeList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.MESSAGE_GATEWAY_LIST_NOT_FOUND, 0, null);
			} else {
				response.setGatewayTypeList(gatewayTypeList);
			}
			
			gatewaySubTypeList = messageGatewaywebDAO.loadGatewaySubTypeList(con);
			if (gatewaySubTypeList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.MESSAGE_GATEWAY_SUB_TYPE_LIST_NOT_FOUND, 0, null);
			} else {
				response.setGatewaySubTypeList(gatewaySubTypeList);
			}
			
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MESSAGE_GATEWAY_LIST_AND_SUB_TYPE_LIST_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.MESSAGE_GATEWAY_LIST_AND_SUB_TYPE_LIST_FOUND);
	        
		}
		catch(BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}
		catch(Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.MESSAGE_GATEWAY_LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.MESSAGE_GATEWAY_LIST_NOT_FOUND);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		
		
		return response;
	}

	
	
	
	
	
	@Override
	public MessageGatewayDetailResponseVO displayMessageGatewayDetail(Connection con, MComConnectionI mcomCon, Locale locale,
			HttpServletRequest request, HttpServletResponse responseSwag, AddMessGatewayVO addMessGatewayVO,
			MessageGatewayDetailResponseVO response, ChannelUserVO userVO) {
		
		final String METHOD_NAME = "displayMessageGatewayDetail";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		MessGatewayVO messGatewayVO = addMessGatewayVO.getMessGatewayVO();
        ResGatewayVO resGatewayVO = messGatewayVO.getResGatewayVO();
        ReqGatewayVO reqGatewayVO = messGatewayVO.getReqGatewayVO();
		MessageGatewayWebDAO messageGatewaywebDAO = null;
		
		try {
			
			messageGatewaywebDAO = new MessageGatewayWebDAO();
			
			if (messageGatewaywebDAO.isMessageGatewayExist(con, messGatewayVO.getGatewayCode())) {
                if (log.isDebugEnabled()) {
                    log.debug("displayMessageGatewayDetail", "isMessageGatewayExist=true");
                }
                //throw new BTSLBaseException(this, "displayDetail", "gateway.operation.msg.alreadyexist", "selectmessagegateway");
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.MESSAGE_GATEWAY_ALREADY_EXIST_CODE, 0, null);
            } else if (messageGatewaywebDAO.isMessageGatewayNameExist(con, messGatewayVO.getGatewayCode(), messGatewayVO.getGatewayName(), false)) {
                if (log.isDebugEnabled()) {
                    log.debug("displayMessageGatewayDetail", "isMessageGatewayNameExist=true");
                }
                //throw new BTSLBaseException(this, "displayDetail", "gateway.operation.msg.namealreadyexist", "selectmessagegateway");
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.MESSAGE_GATEWAY_ALREADY_EXIST_NAME, 0, null);
            }
			
			
			ListValueVO listValueVO = null;
			//adding code for gateway handler starts ************
			 listValueVO = messageGatewaywebDAO.loadClassHandlerList(con, PretupsI.GATEWAY_HANDLER_CLASS, messGatewayVO.getGatewayType());
			 response.setHandlerClassDescription(listValueVO.getLabel());
             
			//adding code for gateway handler ends ************
			 
			 if (addMessGatewayVO.getReqDetailCheckbox() != null && addMessGatewayVO.getReqDetailCheckbox().equals(PretupsI.SELECT_CHECKBOX)) { 
			 }
			 if (addMessGatewayVO.getPushDetailCheckbox() != null && addMessGatewayVO.getPushDetailCheckbox().equals(PretupsI.SELECT_CHECKBOX)) { 
			 }
			 
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DISPLAY_MESSAGE_GATEWAY_DETAIL_SUCCESS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DISPLAY_MESSAGE_GATEWAY_DETAIL_SUCCESS);
			
		}
		 catch (BTSLBaseException be) {
				log.error(METHOD_NAME, "Exception:e=" + be);
				log.errorTrace(METHOD_NAME, be);
				if (!BTSLUtil.isNullString(be.getMessage())) {
					String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
					response.setMessageCode(be.getMessage());
					response.setMessage(msg);
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
					responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				}

			}
		catch(Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.DISPLAY_MESSAGE_GATEWAY_DETAIL_FAILURE, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DISPLAY_MESSAGE_GATEWAY_DETAIL_FAILURE);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		return response;
		
	}
	
	
	
	
	
	@Override
	public BaseResponse addMessGateway(Connection con, MComConnectionI mcomCon, Locale locale,HttpServletRequest request, HttpServletResponse responseSwag, AddMessGatewayVO addMessGatewayVO, BaseResponse response, ChannelUserVO userVO) {
		
		final String METHOD_NAME = "addMessGateway";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		int addCount = 0;
        // this variable is used to check the actual value of the addCount since
        // it may be
        // different for different requests as add only messageGateway or add
        // both messageGateway
        // and requestGateway or add all the three gateway details.
        int requiredAddCount = 1;
        Date currentDate = null;
		
        MessGatewayVO messGatewayVO = addMessGatewayVO.getMessGatewayVO();
        ResGatewayVO resGatewayVO = messGatewayVO.getResGatewayVO();
        ReqGatewayVO reqGatewayVO = messGatewayVO.getReqGatewayVO();
        ResGatewayVO altGatewayVO = messGatewayVO.getAltGatewayVO();
		
        MessageGatewayWebDAO messageGatewaywebDAO = null;
        
        final String arr[] = new String[1];
        try {
        	
        	//
        	messageGatewaywebDAO = new MessageGatewayWebDAO();
        	
        	
        	//checking unique gateway code ->starts **********
        	if (messageGatewaywebDAO.isMessageGatewayExist(con, messGatewayVO.getGatewayCode())) {
                if (log.isDebugEnabled()) {
                    log.debug("displayDetail", "isMessageGatewayExist=true");
                }
                //throw new BTSLBaseException(this, "displayDetail", "gateway.operation.msg.alreadyexist", "selectmessagegateway");
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.MESSAGE_GATEWAY_ALREADY_EXIST_CODE, 0, null);
            } else if (messageGatewaywebDAO.isMessageGatewayNameExist(con, messGatewayVO.getGatewayCode(), messGatewayVO.getGatewayName(), false)) {
                if (log.isDebugEnabled()) {
                    log.debug("displayDetail", "isMessageGatewayNameExist=true");
                }
                //throw new BTSLBaseException(this, "displayDetail", "gateway.operation.msg.namealreadyexist", "selectmessagegateway");
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.MESSAGE_GATEWAY_ALREADY_EXIST_NAME, 0, null);
            }
        	//checking unique gateway code -> ends *******
        	
        	//setting handler starts*****************
        	ListValueVO listValueVO = null;
        	listValueVO = messageGatewaywebDAO.loadClassHandlerList(con, PretupsI.GATEWAY_HANDLER_CLASS, addMessGatewayVO.getMessGatewayVO().getGatewayType());
        	addMessGatewayVO.setHandlerClassDescription(listValueVO.getLabel());
        	addMessGatewayVO.getMessGatewayVO().setHandlerClass(listValueVO.getValue());
        	//setting handler ends ****************
        	
        	// setting gateway subtype starts **********
        	
        	//listValueVO = BTSLUtil.getOptionDesc(messageGatewayForm.getMessageGatewayVO().getGatewayType(), messageGatewayForm.getGatewayTypeList());
            //messageGatewayForm.setGatewayTypeDescription(listValueVO.getLabel());

            //listValueVO = BTSLUtil.getOptionDesc(messageGatewayForm.getMessageGatewayVO().getGatewaySubType(), messageGatewayForm.getGatewaySubTypeList());
            //messageGatewayForm.setGatewaySubTypeDescription(listValueVO.getLabel());

            // extracting the gateway sub type code form the combitation
            // of two values seperated by :
            // this is constructed in DAO at the time of gateway sub
            // type loading
            String[] subTypeDescriptionArr = new String[2];
            //subTypeDescription = listValueVO.getValue();
            //subTypeDescriptionArr = subTypeDescription.split(":");
            subTypeDescriptionArr = messGatewayVO.getGatewaySubType().split(":");
            messGatewayVO.setGatewaySubType(subTypeDescriptionArr[1]);
        	
            //setting gateway subtype ends*************
        	
        	//
        	
        	currentDate = new Date();
            
        	 if (messGatewayVO.getGatewayCode() != null) {
                 messGatewayVO.setCreatedOn(currentDate);
                 messGatewayVO.setModifiedOn(currentDate);
                 messGatewayVO.setCreatedBy(userVO.getUserID());
                 messGatewayVO.setModifiedBy(userVO.getUserID());
                 messGatewayVO.setNetworkCode(userVO.getNetworkID());
                 messGatewayVO.setStatus(PretupsI.STATUS_ACTIVE);
             }
             if (reqGatewayVO.getServicePort() != null) {
                 reqGatewayVO.setGatewayCode(messGatewayVO.getGatewayCode());
                 reqGatewayVO.setCreatedOn(currentDate);
                 reqGatewayVO.setModifiedOn(currentDate);
                 reqGatewayVO.setCreatedBy(userVO.getUserID());
                 reqGatewayVO.setModifiedBy(userVO.getUserID());
             }
             if (resGatewayVO.getPort() != null) {
                 resGatewayVO.setGatewayCode(messGatewayVO.getGatewayCode());
                 resGatewayVO.setCreatedOn(currentDate);
                 resGatewayVO.setModifiedOn(currentDate);
                 resGatewayVO.setCreatedBy(userVO.getUserID());
                 resGatewayVO.setModifiedBy(userVO.getUserID());

                 // number format exception will not be thrown since it is
                 // validated by the validation rule 'integer'
                 if (!BTSLUtil.isNullString(addMessGatewayVO.getTimeOut())) {
                     resGatewayVO.setTimeOut(Integer.parseInt(addMessGatewayVO.getTimeOut()));
                 } else {
                     resGatewayVO.setTimeOut(0);
                 }
             }
             
             //MessageGatewayWebDAO messageGatewaywebDAO = new MessageGatewayWebDAO();
             
             addCount = messageGatewaywebDAO.addMessGateway(con, messGatewayVO);
             if (addMessGatewayVO.getReqDetailCheckbox() != null && addMessGatewayVO.getReqDetailCheckbox().equals(PretupsI.SELECT_CHECKBOX)) {
                 requiredAddCount++;
                 addCount += messageGatewaywebDAO.addReqMessageGateway(con, reqGatewayVO);
             }
             if (addMessGatewayVO.getPushDetailCheckbox() != null && addMessGatewayVO.getPushDetailCheckbox().equals(PretupsI.SELECT_CHECKBOX)) {
                 requiredAddCount++;
                 addCount += messageGatewaywebDAO.addResMessageGateway(con, resGatewayVO);
             }
             if (addMessGatewayVO.getPushDetailCheckbox() != null && addMessGatewayVO.getPushDetailCheckbox().equals(PretupsI.SELECT_CHECKBOX) && addMessGatewayVO.getReqDetailCheckbox() != null && addMessGatewayVO.getReqDetailCheckbox().equals(PretupsI.SELECT_CHECKBOX)) {
                 requiredAddCount++;
                 addCount += messageGatewaywebDAO.addMessGatewayMapping(con, messGatewayVO.getGatewayCode(), messGatewayVO.getGatewayCode(), null, currentDate);
             }
             if (con != null) {
                 if (addCount == requiredAddCount) {
                 	mcomCon.finalCommit();
                     // log the data in adminOperationLog.log
                     AdminOperationVO adminOperationVO = new AdminOperationVO();
                     adminOperationVO.setSource(TypesI.LOGGER_MESSAGE_GATEWAY_SOURCE);
                     adminOperationVO.setDate(currentDate);
                     adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
                     adminOperationVO.setInfo("Message Gateway (" + messGatewayVO.getGatewayCode() + ") has added successfully");
                     adminOperationVO.setLoginID(userVO.getLoginID());
                     adminOperationVO.setUserID(userVO.getUserID());
                     adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                     adminOperationVO.setNetworkCode(userVO.getNetworkID());
                     adminOperationVO.setMsisdn(userVO.getMsisdn());
                     AdminOperationLog.log(adminOperationVO);

                     //BTSLMessages btslMessage = new BTSLMessages("gateway.operation.msg.addsuccess", "selectmessagegatewaypage");
                     //forward = super.handleMessage(btslMessage, request, mapping);
                     arr[0] = String.valueOf(messGatewayVO.getGatewayName());
                     
                     response.setStatus((HttpStatus.SC_OK));
     				 String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MESSAGE_GATEWAY_ADD_SUCCESS, arr);
     				 response.setMessage(resmsg);
     				 response.setMessageCode(PretupsErrorCodesI.MESSAGE_GATEWAY_ADD_SUCCESS);
                 } else {
                 	mcomCon.finalRollback();
                     //throw new BTSLBaseException(this, "addMessageGateway", "gateway.operation.msg.addunsuccess", "selectmessagegateway");
                 	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.MESSAGE_GATEWAY_ADD_FAILURE, 0, null);
                 }
             }
        	
        }
        catch (BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}
        catch (Exception e) {
        	log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.MESSAGE_GATEWAY_ADD_FAILURE, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.MESSAGE_GATEWAY_ADD_FAILURE);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
        }
      
		
		return response;
		
	}
	
	
	
	
	
	
	
	@Override
	public BaseResponse updateMessGateway(Connection con, MComConnectionI mcomCon, Locale locale,HttpServletRequest request, HttpServletResponse responseSwag, AddMessGatewayVO addMessGatewayVO, BaseResponse response, ChannelUserVO userVO) {
		final String METHOD_NAME = "updateMessGateway";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		MessGatewayVO messGatewayVO = addMessGatewayVO.getMessGatewayVO();
        ResGatewayVO resGatewayVO = messGatewayVO.getResGatewayVO();
        ReqGatewayVO reqGatewayVO = messGatewayVO.getReqGatewayVO();
        ResGatewayVO altGatewayVO = messGatewayVO.getAltGatewayVO();
		
        MessageGatewayWebDAO messageGatewaywebDAO = null;
        
        
        int updateCount = 0;
        // this variable is used to check the actual value of the updateCount
        // since it may be
        // different for different requests as update only messageGateway or
        // update both messageGateway
        // and requestGateway or update all the three gateway details.
        int requiredUpdateCount = 1;
        Date currentDate = null;
        
        final String arr[] = new String[1];
        try {
        	messageGatewaywebDAO = new MessageGatewayWebDAO();
        	
        	currentDate = new Date();
        	//setting handler starts*****************
        	ListValueVO listValueVO = null;
        	listValueVO = messageGatewaywebDAO.loadClassHandlerList(con, PretupsI.GATEWAY_HANDLER_CLASS, addMessGatewayVO.getMessGatewayVO().getGatewayType());
        	addMessGatewayVO.setHandlerClassDescription(listValueVO.getLabel());
        	addMessGatewayVO.getMessGatewayVO().setHandlerClass(listValueVO.getValue());
        	//setting handler ends ****************
        	
        	//
        	
	        	if (messageGatewaywebDAO.isMessageGatewayNameExist(con, messGatewayVO.getGatewayCode(), messGatewayVO.getGatewayName(), true)) {
	        		//throw new BTSLBaseException(this, "displayDetail", "gateway.operation.msg.namealreadyexist", "selectmessagegateway");
	        		throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.MESSAGE_GATEWAY_ALREADY_EXIST_NAME, 0, null);
	        	}
        	
        	//
        	
        	
        	
        	// if user want to delete(soft delete) any of the request or
            // response message gateway it should
            // verify that there is no mapping exist in the
            // message_req_resp_mapping table. if an entry exist
            // then display the message
            // "before deleteing the gateway, first delete the mapping"
            if ((reqGatewayVO.getStatus() != null && reqGatewayVO.getStatus().equals(PretupsI.GATEWAY_STATUS_DELETE)) || (resGatewayVO.getStatus() != null && resGatewayVO.getStatus().equals(PretupsI.GATEWAY_STATUS_DELETE))) {
                if (messageGatewaywebDAO.isMessageGatewayMappingExist(con, addMessGatewayVO.getGatewayCode(), addMessGatewayVO.getGatewayCode())) {
                    if (log.isDebugEnabled()) {
                        log.debug("updateMessGateway", "Error: GatewayMapping Exist=true");
                    }
                    //throw new BTSLBaseException(this, "updateMessageGateway", "gateway.operation.msg.mappingexist", "modifygatewaypage");
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.MESSAGE_GATEWAY_MAPPING_EXIST, 0, null);
                }
            }
            
            
            messGatewayVO.setModifiedOn(currentDate);
            messGatewayVO.setModifiedBy(userVO.getUserID());
            //
            //messGatewayVO.setLastModifiedTime(messGatewayVO.getModifiedOn().getTimestamp("modified_on").getTime());
            //
            
            if (reqGatewayVO.getServicePort() != null) {
            	
            	if (!BTSLUtil.isNullString(reqGatewayVO.getPassword()) && (reqGatewayVO.getPassword().equals(BTSLUtil.getDefaultPasswordText(reqGatewayVO.getPassword())))) {
                    reqGatewayVO.setPassword(reqGatewayVO.getOldPassword());
                }

                reqGatewayVO.setGatewayCode(messGatewayVO.getGatewayCode());
                reqGatewayVO.setModifiedOn(currentDate);
                reqGatewayVO.setModifiedBy(userVO.getUserID());
                // if record is to be inserted
                reqGatewayVO.setCreatedOn(currentDate);
                reqGatewayVO.setCreatedBy(userVO.getUserID());

                /*
                 * done by ashishT
                 * change done during implementation of hashing.
                 * setting the status of updatepassword to requestGatewayVO from
                 * MessageGatewayForm
                 */
                if (PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(addMessGatewayVO.getUpdatePassword())) {
                	reqGatewayVO.setUpdatePassword(addMessGatewayVO.getUpdatePassword());
                }
            }
            
            if (resGatewayVO.getPort() != null) {
            	
            	// check whether the user has changed the password or not
                if (!BTSLUtil.isNullString(resGatewayVO.getPassword()) && (resGatewayVO.getPassword().equals(BTSLUtil.getDefaultPasswordText(resGatewayVO.getPassword())))) {
                    resGatewayVO.setPassword(resGatewayVO.getOldPassword());
                }
            	
                resGatewayVO.setGatewayCode(messGatewayVO.getGatewayCode());
                resGatewayVO.setModifiedOn(currentDate);
                resGatewayVO.setModifiedBy(userVO.getUserID());

                // number format exception will not be thrown since it is
                // validated in the jsp by the validation rule 'integer'
                if (!BTSLUtil.isNullString(addMessGatewayVO.getTimeOut())) {
                    resGatewayVO.setTimeOut(Integer.parseInt(addMessGatewayVO.getTimeOut()));
                } else {
                    resGatewayVO.setTimeOut(0);
                }
                // if record is to be inserted
                resGatewayVO.setCreatedOn(currentDate);
                resGatewayVO.setCreatedBy(userVO.getUserID());
                
                /*
                 * done by ashishT
                 * change done during implementation of hashing.
                 * setting the status of updatepassword to responseGatewayVO
                 * from MessageGatewayForm
                 */
                if (PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(addMessGatewayVO.getUpdatePassword())) {
                    resGatewayVO.setUpdatePassword(addMessGatewayVO.getUpdatePassword());
                }
            }
            // update message gateway record
            updateCount = messageGatewaywebDAO.updateMessGateway(con, messGatewayVO);
            // if request message gateway is selected then update its
            // information.
            if (addMessGatewayVO.getReqDetailCheckbox() != null && addMessGatewayVO.getReqDetailCheckbox().equals(PretupsI.SELECT_CHECKBOX)) {
                requiredUpdateCount++;
                if (messageGatewaywebDAO.isRequestMessageGatewayExist(con, addMessGatewayVO.getGatewayCode(), null)) {
                    updateCount += messageGatewaywebDAO.updateReqMessageGateway(con, reqGatewayVO);
                } else {
                    updateCount += messageGatewaywebDAO.addReqMessageGateway(con, reqGatewayVO);
                }
            }
         // if response message gateway is selected then update its
            // information.
            if (addMessGatewayVO.getPushDetailCheckbox() != null && addMessGatewayVO.getPushDetailCheckbox().equals(PretupsI.SELECT_CHECKBOX)) {
                requiredUpdateCount++;
                if (messageGatewaywebDAO.isResponseMessageGatewayExist(con, addMessGatewayVO.getGatewayCode(), null)) {
                    updateCount += messageGatewaywebDAO.updateResMessageGateway(con, resGatewayVO);
                } else {
                    updateCount += messageGatewaywebDAO.addResMessageGateway(con, resGatewayVO);
                }
            }
            // if both response and request message gateway is selected the make
            // an entry in the gateway mapping table.
            if (addMessGatewayVO.getPushDetailCheckbox() != null && addMessGatewayVO.getPushDetailCheckbox().equals(PretupsI.SELECT_CHECKBOX) && addMessGatewayVO.getReqDetailCheckbox() != null && addMessGatewayVO.getReqDetailCheckbox().equals(PretupsI.SELECT_CHECKBOX)) {
                // if mapping does not exist then add a new entry
                if (!messageGatewaywebDAO.isMessageGatewayMappingExist(con, addMessGatewayVO.getGatewayCode())) {
                    requiredUpdateCount++;
                    updateCount += messageGatewaywebDAO.addMessGatewayMapping(con, messGatewayVO.getGatewayCode(), messGatewayVO.getGatewayCode(), null, currentDate);
                }
            }
                
            
            if (con != null) {
                if (updateCount == requiredUpdateCount) {
                	mcomCon.finalCommit();

                    // log the data in adminOperationLog.log
                    AdminOperationVO adminOperationVO = new AdminOperationVO();
                    adminOperationVO.setSource(TypesI.LOGGER_MESSAGE_GATEWAY_SOURCE);
                    adminOperationVO.setDate(currentDate);
                    adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                    adminOperationVO.setInfo("Message Gateway (" + messGatewayVO.getGatewayCode() + ") has modified successfully");
                    adminOperationVO.setLoginID(userVO.getLoginID());
                    adminOperationVO.setUserID(userVO.getUserID());
                    adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                    adminOperationVO.setNetworkCode(userVO.getNetworkID());
                    adminOperationVO.setMsisdn(userVO.getMsisdn());
                    AdminOperationLog.log(adminOperationVO);

                    
                    arr[0] = String.valueOf(messGatewayVO.getGatewayName());
                    response.setStatus((HttpStatus.SC_OK));
    				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MESSAGE_GATEWAY_UPDATE_SUCCESS, arr);
    				response.setMessage(resmsg);
    				response.setMessageCode(PretupsErrorCodesI.MESSAGE_GATEWAY_UPDATE_SUCCESS);
                    //BTSLMessages btslMessage = new BTSLMessages("gateway.operation.msg.updatesuccess", "selectmessagegatewaypage");
                    //forward = super.handleMessage(btslMessage, request, mapping);
                } else {
                	mcomCon.finalRollback();
                    //throw new BTSLBaseException(this, "updateMessageGateway", "gateway.operation.msg.updateunsuccess", "selectmessagegateway");
                	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.MESSAGE_GATEWAY_UPDATE_FAILURE, 0, null);
                }
            }
        	
        }
        catch(BTSLBaseException be) {
        	log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
        }
        catch (Exception e) {
        	log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.MESSAGE_GATEWAY_UPDATE_FAILURE, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.MESSAGE_GATEWAY_UPDATE_FAILURE);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
        }
        
		return response;
		
	}
	
	public GatewayListResponseVO loadClassHandlerList(Connection con, Locale locale, HttpServletResponse response1,
			String gatewayCode) throws BTSLBaseException {
		final String METHOD_NAME = "loadClassHandlerList";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		GatewayListResponseVO response = new GatewayListResponseVO();
		MessageGatewayWebDAO messageGatewayWebDAO = new MessageGatewayWebDAO();
		ListValueVO classHandlerList = new ListValueVO();

		try {
			classHandlerList = messageGatewayWebDAO.loadClassHandlerList(con, PretupsI.GATEWAY_HANDLER_CLASS,
					gatewayCode);
			if (!BTSLUtil.isNullObject(classHandlerList)) {
				response.setClassHandler(classHandlerList.getLabel());
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			} else {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_RECORDS_FOUND, 0, null);
			}

		} finally {
			log.debug(METHOD_NAME, "Exit:=" + METHOD_NAME);
		}

		return response;

	}
	

}
