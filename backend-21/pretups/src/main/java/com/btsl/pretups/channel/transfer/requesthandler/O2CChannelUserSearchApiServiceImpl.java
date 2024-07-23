package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

@Service("O2CChannelUserSearchApiServiceI")
public class O2CChannelUserSearchApiServiceImpl implements O2CChannelUserSearchApiServiceI {
	private final Log log = LogFactory.getLog(this.getClass().getName());
	@Override
	public  SearchChannelUserVOResponseVO processRequest(String userName,String channelOwnerCategoryUserID,String geoDomainCode,String channelOwnerCategory,
			String channelDomainCode,String categoryCode,
			MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag) throws BTSLBaseException {
        final String METHOD_NAME = "processRequest";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
		MComConnectionI mcomCon = null;
		SearchChannelUserVOResponseVO response = null;
		 UserDAO userDao=null;
		 ArrayList<MasterErrorList> inputValidations=null;
		 Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
		try {
			//basic form validation at api level
        	inputValidations = new ArrayList<>();
			response = new SearchChannelUserVOResponseVO();
			/*
			 * Authentication
			 * @throws BTSLBaseException
			 */
			SearchUserRequestVO searchUserRequestVO=new SearchUserRequestVO();
			searchUserRequestVO.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(searchUserRequestVO,headers);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();	
			UserVO userVO=new UserVO();
			userDao = new UserDAO();
			userVO=userDao.loadUsersDetails(con, searchUserRequestVO.getData().getMsisdn());
			
			ArrayList domList = userVO.getDomainList();
			if(BTSLUtil.isNullString(userName))
			{
				MasterErrorList masterErrorList = new MasterErrorList();
				 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_REQ_USERNAME_BLANK_OR_LENGTH_INVALID,null);
				 masterErrorList.setErrorCode(PretupsErrorCodesI.EXTSYS_REQ_USERNAME_BLANK_OR_LENGTH_INVALID);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
				
			}
			if(BTSLUtil.isNullString(channelOwnerCategoryUserID))
			{
				MasterErrorList masterErrorList = new MasterErrorList();
				 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_REQ_USERNAME_BLANK_OR_LENGTH_INVALID,null);
				 masterErrorList.setErrorCode(PretupsErrorCodesI.EXTSYS_REQ_USERNAME_BLANK_OR_LENGTH_INVALID);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
				
			}
			if(BTSLUtil.isNullString(geoDomainCode))
			{
				MasterErrorList masterErrorList = new MasterErrorList();
				 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GEOGRAPHY_DOMAIN_CODE_DOES_NOT_EXIST,null);
				 masterErrorList.setErrorCode(PretupsErrorCodesI.GEOGRAPHY_DOMAIN_CODE_DOES_NOT_EXIST);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
			}
			if(BTSLUtil.isNullString(channelOwnerCategory))
			{
				MasterErrorList masterErrorList = new MasterErrorList();
				 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CHANNEL_OWNER_CATEGORY_NOT_EXIST,null);
				 masterErrorList.setErrorCode(PretupsErrorCodesI.CHANNEL_OWNER_CATEGORY_NOT_EXIST);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
			}
			if(BTSLUtil.isNullString(channelDomainCode))
			{
				MasterErrorList masterErrorList = new MasterErrorList();
				 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_INVALID,null);
				 masterErrorList.setErrorCode(PretupsErrorCodesI.DOMAIN_INVALID);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
			}
			if(!BTSLUtil.isNullOrEmptyList(inputValidations))
			{
				 response.setStatus("400");
				  response.setService("channelUserDetailsResp");
				  response.setErrorMap(new ErrorMap());
				  response.getErrorMap().setMasterErrorList(inputValidations);
				  return response;
			}
        if ((domList == null || domList.isEmpty()) && PretupsI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO
				.getCategoryVO().getFixedDomains())) {
			domList = new DomainDAO().loadCategoryDomainList(con);
		}
		domList = BTSLUtil.displayDomainList(domList);
		ArrayList domList1 = new ArrayList<>();
		String channelDomain = null;
		if (domList.size() == 1) {
			ListValueVO listValueVO = null;
			listValueVO = (ListValueVO) domList.get(0);
			userVO.setDomainID(listValueVO.getValue());
			channelDomain = listValueVO.getValue();
			channelDomainCode=channelDomain;
		} else {
			domList1=domList;
		}
		boolean ischannelDomainCode =false;
		for(int i=0;i<domList1.size();i++)
		{
			if(((ListValueVO)domList1.get(i)).getValue().equals(channelDomainCode))
			{
				ischannelDomainCode =true;
				break;
			}
		}
		if(!ischannelDomainCode)
		{
			//validation
			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DOMAIN_INVALID, 0, null,
					null);
		}
		final CategoryVO categoryVO = new CategoryWebDAO().loadOwnerCategory(con, channelDomainCode);
		
		if (categoryVO != null) {
			if(!categoryVO.getCategoryCode().equalsIgnoreCase(channelOwnerCategory))
			{
				//validation
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHANNEL_OWNER_CATEGORY_NOT_EXIST, 0, null,
						null);
			}
		}
		if(channelOwnerCategory.equalsIgnoreCase(categoryCode))
		{
			channelOwnerCategoryUserID="NA";
		}
		userName = "%" + userName + "%";
		userVO=userDao.loadUsersDetails(con, searchUserRequestVO.getData().getMsisdn());
		ArrayList list = new ChannelUserWebDAO().loadCategoryUsersWithinGeoDomainHirearchy(con, categoryCode, userVO.getNetworkID(), userName, channelOwnerCategoryUserID, geoDomainCode, userVO.getUserID());
		
		/*
		 * Setting response messages
		 */
		if(BTSLUtil.isNullOrEmptyList(list))
		{
			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHANNEL_USER_LIST_DOES_NOT_EXIST, 0, null,
					null);
		}
		response.setChannelUsersList(list);
		response.setService("channelUserDetailsResp");
		response.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
		response.setMessageCode(PretupsErrorCodesI.SUCCESS);
		String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
				PretupsErrorCodesI.SUCCESS, null);
		response.setMessage(resmsg);
        
	    } catch (BTSLBaseException be) {
	        log.error("processFile", "Exceptin:e=" + be);
	        log.errorTrace(METHOD_NAME, be);
       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
	        response.setMessageCode(be.getMessageKey());
	        response.setMessage(msg);
        	 if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
        		 //responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
    	         response.setStatus("401");
            }
           else{
        	    //responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
           		response.setStatus("400");
           }
        }catch (Exception e) {
            log.debug("processFile", e);
            response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            String resmsg = RestAPIStringParser.getMessage(
    				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
    				null);
            response.setMessage(resmsg);
            //responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
        	  response.setStatus("400");
        	
    	}finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("O2CChannelUserSearchApiController#" + "getSearchChannelUserDetails");
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace(METHOD_NAME, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(METHOD_NAME, e);
			}

			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, " Exited ");
			}
		}
		return response;
    
	}

	@Override
	public  GeoDomainCatResponse processRequestGeoDomainCat(
			MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag) throws BTSLBaseException {
        final String METHOD_NAME = "processRequestGeoDomainCat";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
		MComConnectionI mcomCon = null;
		GeoDomainCatResponse response = null;
		 UserDAO userDao=null;
		 Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
		try {
			response = new GeoDomainCatResponse();
			/*
			 * Authentication
			 * @throws BTSLBaseException
			 */
			SearchUserRequestVO searchUserRequestVO=new SearchUserRequestVO();
			searchUserRequestVO.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(searchUserRequestVO,headers);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();	
			UserVO userVO=new UserVO();
			userDao = new UserDAO();
			userVO=userDao.loadUsersDetails(con, searchUserRequestVO.getData().getMsisdn());
			GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
            // load the geographies info from the user_geographies
            ArrayList geographyList = _geographyDAO.loadUserGeographyList(con, userVO.getUserID(), userVO.getNetworkID());
            response.setGeoList(geographyList);
			ArrayList domList = userVO.getDomainList();
			if ((domList == null || domList.isEmpty()) && PretupsI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO
				.getCategoryVO().getFixedDomains())) {
			domList = new DomainDAO().loadCategoryDomainList(con);
		    }
		   response.setChannelDomainList(domList);
		   final ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
		   // load the category list we have to load all user category with in
		   // that network.
		   final ArrayList catgList = channelTransferRuleWebDAO.loadTransferRulesCategoryList(con, userVO.getNetworkID(), PretupsI.OPERATOR_TYPE_OPT);
		   final ArrayList catgeoryList = new ArrayList();
		   // if there is only one domain then all associated category will be
		   // populated earlier
		   // else they will reflect there when domain changes
		   ChannelTransferRuleVO rulesVO = null;
		   for (int i = 0, k = catgList.size(); i < k; i++) {
			rulesVO = (ChannelTransferRuleVO) catgList.get(i);
			// get only those categories having transfer allowed YES.
			if (PretupsI.YES.equals(rulesVO.getTransferAllowed())) {
				catgeoryList.add(new ListValueVO(rulesVO.getToCategoryDes(), rulesVO.getDomainCode() + ":" + rulesVO.getToCategory(),rulesVO.getToCategory(),null));
			}
		   }
		   response.setCategoryList(catgeoryList);
		   response.setService("geodomaincatResp");
		   response.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
		   response.setMessageCode(PretupsErrorCodesI.SUCCESS);
		   String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
				PretupsErrorCodesI.SUCCESS, null);
		   response.setMessage(resmsg);
        
	    } catch (BTSLBaseException be) {
	        log.error("processRequestGeoDomainCat", "Exceptin:e=" + be);
	        log.errorTrace(METHOD_NAME, be);
       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
	        response.setMessageCode(be.getMessageKey());
	        response.setMessage(msg);
        	 if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
        		 responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
    	         response.setStatus("401");
            }
           else{
        	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
           		response.setStatus("400");
           }
        }catch (Exception e) {
            log.debug("processRequestGeoDomainCat", e);
            response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            String resmsg = RestAPIStringParser.getMessage(
    				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
    				null);
            response.setMessage(resmsg);
            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
        	  response.setStatus("400");
        	
    	}finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("O2CChannelUserSearchApiServiceImpl#" + "processRequestGeoDomainCat");
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace(METHOD_NAME, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(METHOD_NAME, e);
			}

			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, " Exited ");
			}
		}
		return response;
    
	}
	
}
