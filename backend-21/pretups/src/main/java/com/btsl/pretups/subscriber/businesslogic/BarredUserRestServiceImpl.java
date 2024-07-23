package com.btsl.pretups.subscriber.businesslogic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.master.businesslogic.SubLookUpVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.subscriber.web.BarredUserValidator;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;

/**
 * This class implements BarredUserRestService and provides methods for
 * processing Bar User request 
 */
public class BarredUserRestServiceImpl implements BarredUserRestService {

	private static final Log _log = LogFactory.getLog(BarredUserRestServiceImpl.class.getName());
	

	/**
	 * Process Bar User Request
	 *
	 * @param requestData
	 *            The request data in the form of JSON String
	 * @return response The PretupsResponse object having status of request and
	 *         different types of messages
	 * @throws BTSLBaseException,
	 *             IOException, Exception
	 * @throws SQLException
	 * @throws SAXException
	 * @throws ValidatorException
	 */
	@Override
	public PretupsResponse<JsonNode> addBarredUser(String requestData)
			throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException {
		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserRestServiceImpl#addBarredUser", PretupsI.ENTERED);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<JsonNode> response = new PretupsResponse<>();
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,
					new TypeReference<JsonNode>() {
					});

			if (_log.isDebugEnabled()) {
				_log.debug(PretupsI.DATA_OBJECT, dataObject);
			}
			BarredUserVO barredUserVO = (BarredUserVO) PretupsRestUtil
					.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<BarredUserVO>() {
					});

			BarredUserValidator barredUserValidator = new BarredUserValidator();
			barredUserValidator.validateRequestData(dataObject.get("type").textValue(), response, barredUserVO,
					"BarredUserVO");

			if (response.hasFieldError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}

			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(dataObject, con);

			barredUserValidator.validateBarredUser(barredUserVO, userVO, con, response, PretupsI.BARUSER);

			if (response.hasFormError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}

			Date currentDate = new Date(System.currentTimeMillis());
			barredUserVO.setCreatedOn(currentDate);
			barredUserVO.setModifiedOn(currentDate);
			barredUserVO.setCreatedBy(userVO.getUserID());
			barredUserVO.setModifiedBy(userVO.getUserID());

			Integer addCount = new BarredUserDAO().addBarredUser(con, barredUserVO);

			if (addCount > 0) {
				this.saveInUserEventRemark(barredUserVO, con, PretupsI.BARRING_USER_REMARKS);
				
				mcomCon.finalCommit();
				this.sendPushMessage(barredUserVO, PretupsErrorCodesI.CHANNEL_USER_BARRED);
				
			} else {
				mcomCon.finalRollback();
			}

			response.setParameters(new String[] { barredUserVO.getMsisdn() });
			response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "subscriber.barreduser.add.mobile.success");
			response.setMessageCode(PretupsErrorCodesI.MOB_NO_BARRED_SUCCESSFULLY);
		} finally {
			if(mcomCon != null)
			{
				mcomCon.close("BarredUserRestServiceImpl#addBarredUser");
				mcomCon=null;
				}
		}

		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserRestServiceImpl#addBarredUser", PretupsI.EXITED);
		}
		return response;
	}

	private void sendPushMessage(BarredUserVO barredUserVO, String message) {
		if ((PretupsI.USER_TYPE_SENDER.equals(barredUserVO.getUserType())
				&& PretupsI.C2S_MODULE.equals(barredUserVO.getModule()))
				|| PretupsI.MSISDN_VALIDATION.equals(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.IDENTIFICATION_NUMBER_VAL_TYPE)))) {
			Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
			PushMessage pushMessage = new PushMessage(barredUserVO.getMsisdn(),
					new BTSLMessages(message), null, null, locale,
					barredUserVO.getNetworkCode());
			pushMessage.push();
		}
		
	}

	/**
	 * Insert data in USER_EVENTS_REMARK table on the basis of certain criteria
	 *
	 * @param channelUserVO
	 *            The ChannelUserVO object
	 * @param barredUserVO
	 *            The BarredUserVO object
	 * @param connection
	 *            Connection object
	 * @return
	 * @throws BTSLBaseException,
	 *             SQLException
	 */
	private void saveInUserEventRemark(BarredUserVO barredUserVO, Connection connection, String type)
			throws BTSLBaseException, SQLException {
		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserRestServiceImpl#saveInUserEventRemark", PretupsI.ENTERED);
		}
		
		if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS))
				.booleanValue()) {
			if("C2S".equalsIgnoreCase(barredUserVO.getModule())
					&& ("SENDER".equalsIgnoreCase(barredUserVO.getUserType())
							|| "RECEIVER".equalsIgnoreCase(barredUserVO.getUserType()))) {

				ChannelUserDAO channelUserDAO = new ChannelUserDAO();
				ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(connection,
						barredUserVO.getMsisdn());
				
				if (channelUserVO != null) {
					List<UserEventRemarksVO> barUnbarRemarks = new ArrayList<>();

					UserEventRemarksVO userRemarskVO = new UserEventRemarksVO();

					userRemarskVO.setCreatedBy(barredUserVO.getCreatedBy());
					userRemarskVO.setCreatedOn(new Date());
					userRemarskVO.setEventType(type);
					userRemarskVO.setRemarks(barredUserVO.getBarredReason());
					userRemarskVO.setMsisdn(barredUserVO.getMsisdn());
					userRemarskVO.setUserID(channelUserVO.getUserID());
					userRemarskVO.setUserType(barredUserVO.getUserType());
					userRemarskVO.setModule(PretupsI.C2S_MODULE);
					barUnbarRemarks.add(userRemarskVO);
					UserWebDAO userwebDAO = new UserWebDAO();
					Integer insertCount = userwebDAO.insertEventRemark(connection, barUnbarRemarks);
					if(insertCount <= 0) {
						connection.rollback();
					}

				}
			}
		}
		
		
		
		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserRestServiceImpl#saveInUserEventRemark", PretupsI.EXITED);
		}
	}

	/**
	 * Process View Bar User Request
	 *
	 * @param requestData
	 *            The request data in the form of JSON String
	 * @return response The PretupsResponse<List<BarredUserVO>> object having
	 *         list of users
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @throws SQLException
	 * @throws SAXException
	 * @throws ValidatorException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<BarredUserVO>> fetchBarredUserList(String requestData)
			throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException {

		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserRestServiceImpl#fetchBarredUserList", PretupsI.ENTERED);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<BarredUserVO>> response = new PretupsResponse<>();
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,
					new TypeReference<JsonNode>() {
					});
			if (_log.isDebugEnabled()) {
				_log.debug(PretupsI.DATA_OBJECT, dataObject);
			}
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(dataObject, con);

			BarredUserVO barredUserVO = (BarredUserVO) PretupsRestUtil
					.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<BarredUserVO>() {
					});
			barredUserVO.setNetworkCode(userVO.getNetworkID());
			BarredUserValidator barredUserValidator = new BarredUserValidator();
			if ((BTSLUtil.isNullString(barredUserVO.getMsisdn()) || "".equalsIgnoreCase(barredUserVO.getMsisdn()))
					&& (BTSLUtil.isNullString(barredUserVO.getModule())
							|| "".equalsIgnoreCase(barredUserVO.getModule()))) {
				barredUserValidator.validateRequestData(dataObject.get("type").textValue(), response, barredUserVO,
						"ViewBarredUserList");
			} else if (!BTSLUtil.isNullString(barredUserVO.getMsisdn())
					|| !"".equalsIgnoreCase(barredUserVO.getMsisdn())) {
				barredUserValidator.validateRequestData(dataObject.get("type").textValue(), response, barredUserVO,
						"ViewBarredUserListByMSISDN");
			} else {
				barredUserValidator.validateRequestData(dataObject.get("type").textValue(), response, barredUserVO,
						"ViewBarredUserListByType");
			}

			if (response.hasFieldError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			 List<BarredUserVO> barredUserList = new BarredUserDAO().loadBarredUserList(con, barredUserVO);
			 List<BarredUserVO> finalBarredList =  new ArrayList<BarredUserVO>(); //added to make final barred list according to hierachy and child of user

			if (barredUserList.isEmpty()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setFormError("subscriber.viewbaruser.notexists");
				response.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_VIEW_BAR_USER_NOT_EXIST);
				return response;
			}
			String userID = null;
            if (PretupsI.CATEGORY_TYPE_AGENT.equals(userVO.getCategoryVO().getCategoryType()) && PretupsI.NO.equals(userVO.getCategoryVO().getHierarchyAllowed())) {
                userID = userVO.getParentID();
            } else {
                userID = userVO.getUserID();
            }
            List<ListValueVO> childUserList = null;
            UserWebDAO userwebDao = new UserWebDAO();
			ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
            childUserList = userwebDao.loadUserListByLogin(con, userVO.getUserID(), PretupsI.STAFF_USER_TYPE, "%"); // for all staff user list                                                                                                                // getting
            List<ChannelUserVO> hierarchyList = channelUserWebDAO.loadChannelUserHierarchy(con, userID, false); //for hierarchy of user
            int barredUserListSize= barredUserList.size();
            for (int i = 0; i < barredUserListSize; i++) {
                barredUserVO = new BarredUserVO();
                barredUserVO = barredUserList.get(i);
                // added for the viewing details for channel user
                if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE) && !(userVO.getMsisdn().equals(barredUserVO.getMsisdn()))) {
                    boolean isExist = false;
                    String filteredMsisdn = null;
                    // checking that current user is authorized to see the list
                    if (!BTSLUtil.isNullString(barredUserVO.getMsisdn())) {
                        
                    	ChannelUserVO channelUserVO = null;
                        if (!BTSLUtil.isNullString(barredUserVO.getMsisdn())) {
                            filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(barredUserVO.getMsisdn());
                        
                        if (!hierarchyList.isEmpty()) {
                            // check in the child user list
                        	int hierarchyListSize= hierarchyList.size();
                            for (int j = 0; j < hierarchyListSize; j++) {
                                channelUserVO =  hierarchyList.get(j);
                                if (channelUserVO.getMsisdn().equals(filteredMsisdn)) {
                                	finalBarredList.add(barredUserVO);
                                	isExist = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!isExist) {
    					//check staff list
                    	int childUserListSize= childUserList.size();
                    	for (int j = 0; j < childUserListSize; j++) {
    						ListValueVO childUser = childUserList.get(j);
    						if (barredUserVO.getMsisdn().equals(childUser.getOtherInfo2())) {
    							finalBarredList.add(barredUserVO);
    							isExist = true;
    							break;
    						}
    					}
    				}
                }

                }
            			 
            }
            if(userVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE))
            {
            	finalBarredList = barredUserList ;
            }
            if (finalBarredList.isEmpty()) {
				response.setFormError("subscriber.barreduser.msg.mobileno.notauthorise.to.viewbar",
						new String[] { barredUserVO.getMsisdn() });
				response.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_BARRED_USER_MOBILE_NOT_AUTHORISE);
				return response;
			}
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, finalBarredList);
		} finally {
			if(mcomCon != null)
			{
				mcomCon.close("BarredUserRestServiceImpl#fetchBarredUserList");
				mcomCon=null;
				}
		}
		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserRestServiceImpl#fetchBarredUserList", PretupsI.EXITED);
		}
		return response;
	}

	/**
	 * Process unBar User Request
	 *
	 * @param requestData
	 *            The request data in the form of JSON String
	 * @return response The PretupsResponse<List<BarredUserVO>> object having
	 *         list of users
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @throws SQLException
	 * @throws SAXException
	 * @throws ValidatorException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<BarredUserVO>> fetchBarredUserToUnbarredList(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException {

		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserRestServiceImpl#fetchUnBarredUserList", PretupsI.ENTERED);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<BarredUserVO>> response = new PretupsResponse<>();
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,
					new TypeReference<JsonNode>() {
					});
			if (_log.isDebugEnabled()) {
				_log.debug(PretupsI.DATA_OBJECT, dataObject);
			}
			

			BarredUserVO barredUserVO = (BarredUserVO) PretupsRestUtil
					.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<BarredUserVO>() {
					});

			BarredUserValidator barredUserValidator = new BarredUserValidator();

			barredUserValidator.validateRequestData(dataObject.get("type").textValue(), response, barredUserVO, "CONUNBARUSER");
			
			if (response.hasFieldError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(dataObject, con);
			
			barredUserValidator.validateBarredUser(barredUserVO, userVO, con, response, PretupsI.UNBARUSER);

			if (response.hasFormError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}

			BarredUserDAO barredUserDAO = new BarredUserDAO();
			List<BarredUserVO> barredUserList = barredUserDAO.loadInfoOfBarredUser(con, barredUserVO);
			if (barredUserList.isEmpty()) {
				response.setResponse("subscriber.unbaruser.notexists", null);
				response.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_UNBARRED_USER_NOT_EXIST);
				return response;
			}
			
			if(!this.processBarredTypeList(response, userVO, barredUserList)){
				return response;
			}
			
			
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, barredUserList);

		} finally {
			if(mcomCon != null)
			{
				mcomCon.close("BarredUserRestServiceImpl#fetchUnBarredUserList");
				mcomCon=null;
				}
		}
		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserRestServiceImpl#fetchUnBarredUserList", PretupsI.EXITED);
		}
		return response;
	}

	/**
	 * Process Barred Type List
	 * @param userVO Object of UserVO
	 * @param barredUserList Barred type list
	 * @return response The PretupsResponse<List<BarredUserVO>> object having
	 *         list of users
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	private Boolean processBarredTypeList(PretupsResponse<List<BarredUserVO>> response, UserVO userVO, List<BarredUserVO> barredUserList) throws SQLException, BTSLBaseException {
		List<SubLookUpVO> barredTypeList = new SubLookUpDAO().loadSublookupVOList(PretupsI.BARRING_TYPE);
        if (barredTypeList == null || barredTypeList.isEmpty()) {
        	response.setResponse("subscriber.barreduser.nobartype", null);
        	response.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_BARRED_NO_BAR_TYPE);
        	return false;
        }
        for (int i = 0, j = barredTypeList.size(); i < j; i++) {
        	SubLookUpVO subLookUpVO = barredTypeList.get(i);
            if (subLookUpVO.getLookupCode().equals(PretupsI.P2P_BARTYPE_LOOKUP_CODE)) {
                subLookUpVO.setSubLookupCode(PretupsI.P2P_MODULE + ":" + subLookUpVO.getSubLookupCode());
            } else {
                subLookUpVO.setSubLookupCode(PretupsI.C2S_MODULE + ":" + subLookUpVO.getSubLookupCode());
            }
        }
        
        for (int i = 0, j = barredTypeList.size(); i < j; i++) {
        	SubLookUpVO subLookUpVO =  barredTypeList.get(i);
            if (!subLookUpVO.getLookupCode().equals(PretupsI.CHANNLE_USER_BARTYPE_LOOKUP_CODE)) {
                barredTypeList.remove(i);
                i--;
                j--;
            }
        }
        if (barredTypeList.isEmpty()) {
        	response.setResponse("subscriber.barreduser.nobartype", null);
        	response.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_BARRED_NO_BAR_TYPE);
        	return false;
        }
        
        if (!userVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)) {
        	int barredUserListSize= barredUserList.size();
            for (int i = 0; i < barredUserListSize; i++) {
            	BarredUserVO barredUserVOObject =  barredUserList.get(i);
                for (int j = 0; j < barredTypeList.size(); j++) {
                	SubLookUpVO subLookUpVO = barredTypeList.get(j);
                    String[] barType = subLookUpVO.getSubLookupCode().split(":");
                    if (barType[1].equals(barredUserVOObject.getBarredType())) {
                        break;
                    } else if (barredTypeList.size() == j + 1) {
                        barredUserList.remove(i);
                        i--;
                    }
                }
            }
        }
        if (barredUserList.isEmpty()) {
        	response.setResponse("subscriber.unbaruser.notauthorized", null);
        	response.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_UNBARRED_USER_UNAUTHORIZED);
        	return false;
        }
		return true;
	}
	
	
	/**
	 * Process BarredUser List to Unbar User
	 *
	 * @param requestData
	 *            The request data in the form of JSON String
	 * @return response The PretupsResponse<List<BarredUserVO>> object having
	 *         list of users
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @throws SQLException
	 * @throws SAXException
	 * @throws ValidatorException
	 */
	@Override
	public PretupsResponse<JsonNode> processSelectedBarredUserToUnbar(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException {

		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserRestServiceImpl#processSelectedBarredUserToUnbar", PretupsI.ENTERED);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<JsonNode> response = new PretupsResponse<>();
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
			if (_log.isDebugEnabled()) {
				_log.debug(PretupsI.DATA_OBJECT, dataObject);
			}
			
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(dataObject, con);

			BarredUserVO barredUserVO = (BarredUserVO) PretupsRestUtil
					.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<BarredUserVO>() {
					});
			
			BarredUserValidator barredUserValidator = new BarredUserValidator();
			barredUserValidator.validateRequestData(dataObject.get("type").textValue(), response, barredUserVO, "CONUNBARUSER");
			
			if (response.hasFieldError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			
			String[] barredUserList = barredUserVO.getBarredTypeList();
			
			if(barredUserList == null || barredUserList.length == 0){
				response.setFormError("no.data.has.been.processed.as.some.user.information.is.incorrect");
				response.setMessageCode(PretupsErrorCodesI.UNBARRING_FAILED_INCORRECT_AS_USER_INFO);
            	return response;
			}
			
			List<BarredUserVO> barredUserVOList = new ArrayList<>();
			
			for (int i = 0; i < barredUserList.length; i++) {
				BarredUserVO barredUser = new BarredUserVO();
				barredUser.setModule(barredUserVO.getModule());
				barredUser.setUserType(barredUserVO.getUserType());
				barredUser.setMsisdn(barredUserVO.getMsisdn());
				barredUser.setBarredReason(barredUserVO.getBarredReason());
				barredUser.setBarredType(barredUserList[i]);
				barredUser.setNetworkCode(userVO.getNetworkID());
				barredUserVOList.add(barredUser);
			}
			
			BarredUserDAO barredUserDAO = new BarredUserDAO();
            Integer deleteCount = barredUserDAO.deleteFromBarredMsisdnTable(con, barredUserVOList);
            
            if(deleteCount != barredUserVOList.size()){
            	mcomCon.finalRollback();
            	response.setFormError("no.data.has.been.processed.as.some.user.information.is.incorrect");
            	response.setMessageCode(PretupsErrorCodesI.UNBARRING_FAILED_INCORRECT_AS_USER_INFO);
            	return response;
            }else if(deleteCount > 0){
            	
            	Date currentDate = new Date(System.currentTimeMillis());
    			barredUserVO.setCreatedOn(currentDate);
    			barredUserVO.setModifiedOn(currentDate);
    			barredUserVO.setCreatedBy(userVO.getUserID());
    			barredUserVO.setModifiedBy(userVO.getUserID());
            	this.saveInUserEventRemark(barredUserVO, con, PretupsI.UNBARRING_USER_REMARKS);
            }
            mcomCon.finalCommit();
            this.sendPushMessage(barredUserVO, PretupsErrorCodesI.CHANNEL_USER_UNBARRED);
            response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "user.has.been.unbarred.successfully");
            response.setMessageCode(PretupsErrorCodesI.USER_UNBARRED_SUCCESSFULLY);
		} finally {
			if(mcomCon != null)
			{
				mcomCon.close("BarredUserRestServiceImpl#processSelectedBarredUserToUnbar");
				mcomCon=null;
				}
		}
		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserRestServiceImpl#processSelectedBarredUserToUnbar", PretupsI.EXITED);
		}
		return response;
	}

}
