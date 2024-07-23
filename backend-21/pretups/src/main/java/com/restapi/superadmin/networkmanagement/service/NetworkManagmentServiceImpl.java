package com.restapi.superadmin.networkmanagement.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


import com.restapi.superadmin.networkmanagement.requestVO.ModifyNetworkRequestVO;
import com.restapi.superadmin.networkmanagement.responseVO.NetworkListResponseVO;
import com.restapi.superadmin.networkmanagement.responseVO.ServiceIDListResponseVO;
import com.restapi.superadmin.networkmanagement.serviceI.NetworkManagmentServiceI;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;
import com.web.pretups.network.businesslogic.NetworkWebDAO;
import com.btsl.pretups.network.businesslogic.NetworkVO;

@Service("NetworkManagmentServiceI")
public class NetworkManagmentServiceImpl implements NetworkManagmentServiceI{

	public static final Log LOG = LogFactory.getLog(NetworkManagmentServiceImpl.class.getName());
	public static final String classname = "NetworkManagmentServiceImpl";

	
	@Override
	public NetworkListResponseVO viewNetworkList(Connection con, String loginId, HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException {
		final String methodName = "viewNetworkList";
		if(LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered:=" + methodName);
		}
		
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		NetworkListResponseVO response = new NetworkListResponseVO();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		NetworkVO networkVO = new NetworkVO();
		ArrayList<NetworkVO> networkList = new ArrayList<>();
		ArrayList<NetworkVO> availableNetworkList = new ArrayList<>();
		
		try {
			
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
		    networkList = NetworkManagmentServiceImpl.getAllNetwoekList(con,userVO);
		    //change error code
		    if (networkList.isEmpty()) {
				throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.DIV_FAIL, 0, null);
			} else {
				for (int i = 0; i < networkList.size(); i++) {
					NetworkVO network = networkList.get(i);
					if(network.getStatus().equalsIgnoreCase(PretupsI.YES)) {
						network.setStatusDesc(PretupsI.ACTIVE);
					}else if(network.getStatus().equalsIgnoreCase(PretupsI.SUSPEND)) {
						network.setStatusDesc(PretupsI.SUSPENDED);
					}
					
					availableNetworkList.add(network);
				}

				response.setNetworkList(availableNetworkList);
				response.setStatus((HttpStatus.SC_OK));
				//change status code
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DIV_SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.DIV_SUCCESS);

			}
			
		}catch(BTSLBaseException baseException) {
			LOG.error(methodName, "Exception:e=" + baseException);
			LOG.errorTrace(methodName, baseException);
			if (!BTSLUtil.isNullString(baseException.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, baseException.getMessage(), null);
				response.setMessageCode(baseException.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}catch(Exception exception) {
			LOG.error(methodName, "Exception:e=" + exception);
			LOG.errorTrace(methodName, exception);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.DIV_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DIV_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		if(LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exited := Network Avaulable " + availableNetworkList);
		}
		
		return response;
	}

	
	static ArrayList<NetworkVO> getAllNetwoekList(Connection con,UserVO userVO) throws BTSLBaseException{
		final String methodName = "getNetwoekList()";
		if(LOG.isDebugEnabled()) {
			LOG.debug(methodName, "UserVO : + "+userVO);
		}
		
		String categoryCode = userVO.getCategoryCode();
		String userId = userVO.getUserID();
		
		
		ArrayList<NetworkVO> networkList = new ArrayList<>();
		if (TypesI.SUPER_ADMIN.equals(categoryCode)|| TypesI.SUPER_NETWORK_ADMIN.equals(categoryCode)|| TypesI.SUPER_CHANNEL_ADMIN.equals(categoryCode) || TypesI.SUPER_CUSTOMER_CARE.equals(categoryCode) || TypesI.NETWORK_ADMIN.equals(categoryCode)) {
	            NetworkDAO _networkDAO = new NetworkDAO();
	            String status = "'" + PretupsI.STATUS_DELETE + "'";
	            if (TypesI.NO.equals(userVO.getCategoryVO().getViewOnNetworkBlock())) {
	                status = "'" + PretupsI.STATUS_DELETE + "','" + PretupsI.STATUS_SUSPEND + "'";
	            }
	            if(TypesI.SUPER_NETWORK_ADMIN.equals(categoryCode) || TypesI.SUPER_CUSTOMER_CARE.equals(categoryCode))
	            {
	            	networkList = _networkDAO.loadNetworkListForSuperOperatorUsers(con, status, userId);
	            }
	            else if(TypesI.SUPER_CHANNEL_ADMIN.equals(categoryCode))
	            {
	            	networkList = _networkDAO.loadNetworkListForSuperChannelAdm(con, status, userId);
	            }
	            else
	            {
	            	networkList = _networkDAO.loadNetworkList(con, status);
	            }
	            if (networkList == null || networkList.isEmpty()) {
	            	userVO.setNetworkName(TypesI.NETWORK_NAME_DEFAULT);
	            	userVO.setNetworkID("");
	            }// end of if
	            else if (networkList.size() == 1) {

	                NetworkVO networkVO = (NetworkVO) networkList.get(0);
	                // change the network related information into the channelUserVO
	                userVO.setNetworkID(networkVO.getNetworkCode());
	                userVO.setNetworkName(networkVO.getNetworkName());
	                userVO.setReportHeaderName(networkVO.getReportHeaderName());
	                userVO.setNetworkStatus(networkVO.getStatus());
	                // ChangeID=LOCALEMASTER
	                // Check which language message to be sent from the locale
	                // master table for the perticuler locale.
	                String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	                String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	                LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(new Locale(defaultLanguage, defaultCountry));
	                if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
	                    userVO.setMessage(networkVO.getLanguage1Message());
	                } else {
	                    userVO.setMessage(networkVO.getLanguage2Message());
	                }

	                /*
	                 * while change the network location also change the user
	                 * geographical list becs while adding user we check the domain
	                 * type of the loginUser and AddedUser if domain type is same of
	                 * both the user then the geographical list of the added user is
	                 * same as the geographical list of the login user
	                 */
	                UserGeographiesVO geographyVO = null;
	                ArrayList<UserGeographiesVO> geographyList = new ArrayList();
	                geographyVO = new UserGeographiesVO();
	                geographyVO.setGraphDomainCode(networkVO.getNetworkCode());
	                geographyVO.setGraphDomainName(networkVO.getNetworkName());
	                geographyVO.setGraphDomainTypeName(userVO.getCategoryVO().getGrphDomainTypeName());
	                geographyList.add(geographyVO);
	                userVO.setGeographicalAreaList(geographyList);
	                if(TypesI.SUPER_CHANNEL_ADMIN.equals(categoryCode))  //Changing for superchannel admin
	                		{	
	                
	                ArrayList	userGeoList = new GeographicalDomainDAO().loadUserGeographyList(con, userId, userVO.getNetworkID());
	                userVO.setGeographicalAreaList(userGeoList);
	                		}
	            } else {
	            	userVO.setNetworkName(TypesI.NETWORK_NAME_DEFAULT);
	            	userVO.setNetworkID("");
	                /*
	                 * String path = mapping.findForward("changeNetwork").getPath();
	                 * path = path+"&page=0"; forward = new ActionForward();
	                 * forward.setPath(path);
	                 */

	                /*
	                 * To handle the problem while doing change network in frames.
	                 */

	            }
	           
	        }
		
		return networkList;
	}
	
	
	@Override
	public NetworkListResponseVO viewNetworkStatusList(Connection con, String loginId, HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException {
		final String methodName = "viewNetworkList";
		if(LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered:=" + methodName);
		}
		
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		NetworkListResponseVO response = new NetworkListResponseVO();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		NetworkVO networkVO = new NetworkVO();
		ArrayList<NetworkVO> networkList = new ArrayList<>();
		ArrayList<NetworkVO> availableNetworkList = new ArrayList<>();
		
		try {
			NetworkWebDAO networkWebDAO = new NetworkWebDAO();
		    networkList = networkWebDAO.loadNetworkStatusList(con);
		    //change error code
		    if (networkList.isEmpty()) {
				throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.DIV_FAIL, 0, null);
			} else {
				for (int i = 0; i < networkList.size(); i++) {
					availableNetworkList.add(networkList.get(i));
				}

				response.setNetworkList(availableNetworkList);
				response.setStatus((HttpStatus.SC_OK));
				//change status code
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DIV_SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.DIV_SUCCESS);

			}
			
		}catch(BTSLBaseException baseException) {
			LOG.error(methodName, "Exception:e=" + baseException);
			LOG.errorTrace(methodName, baseException);
			if (!BTSLUtil.isNullString(baseException.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, baseException.getMessage(), null);
				response.setMessageCode(baseException.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}catch(Exception exception) {
			LOG.error(methodName, "Exception:e=" + exception);
			LOG.errorTrace(methodName, exception);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.DIV_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DIV_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		if(LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exited := Network Avaulable " + availableNetworkList);
		}
		
		return response;
	}

	@Override
	public BaseResponse modifyNetworkDetails(Connection con, String loginId, ModifyNetworkRequestVO requestVO,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException {
		final String METHOD_NAME = "ModifyNetworkDetails";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		BaseResponse response = new BaseResponse();
		NetworkWebDAO networkWebDAO = new NetworkWebDAO();
		try {
			List blankLanguageMessage = validateNetworkDetails(requestVO.getNetworkList());
			if(blankLanguageMessage.size() > 0) {
				response.setStatus(HttpStatus.SC_PARTIAL_CONTENT);
				response.setMessageCode(String.valueOf(HttpStatus.SC_PARTIAL_CONTENT));
				String messageArr = String.join(",", blankLanguageMessage);
				//response.setErrorMap(blankLanguageMessage);
				response.setMessage(messageArr);
				return response;
			}
			int update = -1;
			update = networkWebDAO.updateNetworkStatus(con,requestVO.getNetworkList()); 
			if (update > 0) {
				con.commit();
				response.setStatus((HttpStatus.SC_OK));
				String[] messageArr = {"values are updated."};
		    	response.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
		    	response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),messageArr));
			}
			if(update < 0) {
				throw new Exception("Unable to Update");
			}else {
				response.setStatus(HttpStatus.SC_OK);
				response.setMessage(update + " values are updated.");
			}
		}catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.DIV_MODIFY_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DIV_MODIFY_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		return response;
	}
	
	
	


	@Override
	public BaseResponse loadServiceSetList(Connection con, HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException {
		final String methodName = "loadServiceListRestAPI";
		if(LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered:=" + methodName);
		}
		
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		ServiceIDListResponseVO response = new ServiceIDListResponseVO();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		NetworkVO networkVO = new NetworkVO();
		ArrayList serviceSetID = new ArrayList<>();
		try {
			
			NetworkWebDAO networkWebDAO = new NetworkWebDAO();
			serviceSetID = networkWebDAO.loadServiceSetList(con,TypesI.YES);
		    if (serviceSetID.isEmpty()) {
				throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.DIV_FAIL, 0, null);
			} else {

				response.setServiceSetID(serviceSetID);
				response.setStatus((HttpStatus.SC_OK));
				//change status code
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DIV_SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.DIV_SUCCESS);

			}
			
		}catch(BTSLBaseException baseException) {
			LOG.error(methodName, "Exception:e=" + baseException);
			LOG.errorTrace(methodName, baseException);
			if (!BTSLUtil.isNullString(baseException.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, baseException.getMessage(), null);
				response.setMessageCode(baseException.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}catch(Exception exception) {
			LOG.error(methodName, "Exception:e=" + exception);
			LOG.errorTrace(methodName, exception);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.DIV_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DIV_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		if(LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exited := ServiceSetId Avaulable " + serviceSetID);
		}
		
		return response;
		
	}


	@Override
	public BaseResponse modifyNetworkDetail(Connection con, String loginId, NetworkVO requestVO,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException {
		
		final String METHOD_NAME = "ModifyNetworkDetails";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		BaseResponse response = new BaseResponse();
		NetworkWebDAO networkWebDAO = new NetworkWebDAO();
		try {
			
			int update = -1; 
			update = this.updateNetworkDetail(con,loginId,requestVO,responseSwag); 
			if (update > 0) {
				con.commit();
				response.setStatus((HttpStatus.SC_OK));
				String[] messageArr = {"values are updated."};
		    	response.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
		    	response.setMessage(RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),response.getMessageCode(),messageArr));
			}
			if(update < 0) {
				throw new Exception("Unable to Update");
			}else {
				response.setStatus(HttpStatus.SC_OK);
				response.setMessage(update + " values are updated.");
			}
		}catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.DIV_MODIFY_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DIV_MODIFY_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		return response;
	}


	@Override
	public int updateNetworkDetail(Connection con, String loginId,NetworkVO requestVO, HttpServletResponse responseSwag) throws BTSLBaseException, SQLException {
		// TODO Auto-generated method stub
		
		 final String methodName = "updateNetworkDetail";
			MComConnectionI mcomCon = null;
	        int updateCount = 0;
	        try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
	            NetworkWebDAO networkwebDAO = new NetworkWebDAO();
	            HashMap map = new HashMap();

	            /*
	             * check whether the Network Name is already exist in the DB or not
	             * if exist throw an exception
	             */
	            if (networkwebDAO.isNetworkNameExist(con, requestVO.getNetworkCode(), requestVO.getNetworkName())) {

	                map.put("network.error.networknameexist", null);
	            }

	            // check for netwrok short name
	            if (networkwebDAO.isNetworkShortNameExist(con, requestVO.getNetworkCode(), requestVO.getNetworkShortName())) {

	                map.put("network.error.networkshortnameexist", null);
	            }

	            // check for network ERP code
	            if (networkwebDAO.isNetworkERPCodeExist(con, requestVO.getNetworkCode(), requestVO.getErpNetworkCode())) {
	    
	                map.put("network.error.erpnetworkcodeexist", null);
	            }

	            if (map.size() > 0) {
	                throw new BTSLBaseException(this, "updateNetwork", map, "DetailView");
	            }

	            NetworkVO networkVO = requestVO;

	            // populate the networkVO from the form
	           

	            // set the default values
	            //UserVO userVO =  getUserFormSession(request);
	            //networkVO.setModifiedBy(userVO.getUserID());
	            Date currentDate = new Date();
	            networkVO.setModifiedOn(currentDate);
	            networkVO.setNetworkType(PretupsI.NETWORK_TYPE_DEFAULT);

	            updateCount = networkwebDAO.updateNetwork(con, networkVO);

	            int geographyCount = 0;

	            if (updateCount > 0) {
	                // while add new network also insert that network code entry in
	                // geographical_domains

	                GeographicalDomainVO geographicalDoaminVO = new GeographicalDomainVO();
	                geographicalDoaminVO.setGrphDomainCode(networkVO.getNetworkCode());
	                geographicalDoaminVO.setNetworkCode(networkVO.getNetworkCode());
	                geographicalDoaminVO.setGrphDomainName(networkVO.getNetworkName());
	                geographicalDoaminVO.setParentDomainCode(TypesI.GRPH_DOMAIN_CODE);
	                geographicalDoaminVO.setGrphDomainShortName(networkVO.getNetworkCode());
	                geographicalDoaminVO.setDescription(networkVO.getNetworkName());
	                geographicalDoaminVO.setStatus(networkVO.getStatus());
	                geographicalDoaminVO.setGrphDomainType(TypesI.GRPH_DOMAIN_TYPE_NETWORK);
	                //geographicalDoaminVO.setCreatedBy(userVO.getUserID());
	                geographicalDoaminVO.setCreatedOn(currentDate);
	                //geographicalDoaminVO.setModifiedBy(userVO.getUserID());
	                geographicalDoaminVO.setModifiedOn(currentDate);

	                GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO();
	                geographyCount = geographicalDomainWebDAO.updateGeographicalDomain(con, geographicalDoaminVO);
	            } else {
	            	mcomCon.finalRollback();
	                LOG.error("updateNetwork", "Error: while updating Network");
	                throw new BTSLBaseException(this, "updateNetwork", "error.general.processing");
	            }

	            // Commit or Rollback the Transaction
	            if (geographyCount > 0) {
	            	mcomCon.finalCommit();

//	                // log the data in adminOperationLog.log
//	                AdminOperationVO adminOperationVO = new AdminOperationVO();
//	                adminOperationVO.setSource(TypesI.LOGGER_NETWORK_SOURCE);
//	                adminOperationVO.setDate(currentDate);
//	                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
//	                adminOperationVO.setInfo("Network " + networkVO.getNetworkName() + " has successfully modified");
//	                adminOperationVO.setLoginID(userVO.getLoginID());
//	                adminOperationVO.setUserID(userVO.getUserID());
//	                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
//	                adminOperationVO.setNetworkCode(networkVO.getNetworkCode());
//	                adminOperationVO.setMsisdn(userVO.getMsisdn());
//	                AdminOperationLog.log(adminOperationVO);


	                BTSLMessages btslMessage = new BTSLMessages("network.networkdetail.successeditmessage", "list");
	                //forward = this.handleMessage(btslMessage, request, mapping);
	            } else {
	            	mcomCon.finalRollback();
	                LOG.error("updateNetwork", "Error: while updating geographical_domains table");
	                throw new BTSLBaseException(this, "updateNetwork", "error.general.processing");
	            }

	        } catch (Exception e) {
	            LOG.error("updateNetwork", "Exceptin:e=" + e);
	            LOG.errorTrace(methodName, e);
	            // pass con Object for rollback the Transaction
	            //return super.handleError(this, "updateNetwork", e, request, mapping, con);
	        } finally {
				if (mcomCon != null) {
					mcomCon.close("NetworkAction#updateNetwork");
					mcomCon = null;
				}

	            if (LOG.isDebugEnabled()) {
	                LOG.debug("updateNetwork", "Exiting");
	            }
	        }

	        //return forward;
		return updateCount;
	}
	
	
	public List<?> validateNetworkDetails(List p_voList) {
		int listSize = 0;
		List<String> message = new ArrayList<String>();
		if (p_voList != null) {
            listSize = p_voList.size();
        }

        for (int i = 0; i < listSize; i++) {
            final NetworkVO networkVO = (NetworkVO) p_voList.get(i);
            if(networkVO.getStatus().equals("S") && (BTSLUtil.isNullString(networkVO.getLanguage1Message()) || BTSLUtil.isNullString(networkVO.getLanguage2Message()))) {
//            	MasterErrorList masterErrorList = new MasterErrorList();
//            	masterErrorList.setErrorCode(String.valueOf(HttpStatus.SC_PARTIAL_CONTENT));
//            	masterErrorList.setErrorMsg(networkVO.getNetworkCode() + " Language messages are mandatory for suspended network.");
            	message.add(networkVO.getNetworkCode());
            	LOG.debug(networkVO.getNetworkCode(), message);
            }
        }
       
        return message;
	}

}
