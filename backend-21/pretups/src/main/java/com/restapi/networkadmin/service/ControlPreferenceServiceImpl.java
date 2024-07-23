package com.restapi.networkadmin.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.ControlPreferenceDAO;
import com.btsl.pretups.preference.businesslogic.ControlPreferenceVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCacheVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI;
import com.restapi.networkadmin.requestVO.UpdateControlPreferenceVO;
import com.restapi.networkadmin.responseVO.ControlPreferenceListsResponseVO;
import com.restapi.networkadmin.serviceI.ControlPreferenceService;
import com.web.pretups.preference.businesslogic.PreferenceWebDAO;

@Service
public class ControlPreferenceServiceImpl implements ControlPreferenceService{
	protected static final Log LOG = LogFactory.getLog(CAC2STransferReversalServiceI.class.getName());

	@Override
	public ControlPreferenceListsResponseVO fetchCtrlPreferenceLists(Locale locale, String moduleCodeString,
			String controlCodeString, String preferenceCodeString, String loginId,HttpServletResponse responseSwag) throws SQLException {
		final String methodName = "fetchCtrlPreferenceLists";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = new UserVO();
        ControlPreferenceListsResponseVO response = new ControlPreferenceListsResponseVO();
        try {
        	mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            if(BTSLUtil.isNullString(controlCodeString) || BTSLUtil.isNullString(moduleCodeString)) {
            	response.setModuleList(LookupsCache.loadLookupDropDown(PretupsI.MODULE_TYPE, true));
            	response.setControlList(LookupsCache.loadLookupDropDown(PretupsI.LOOKUP_TYPE_CONTROL, true));
            	
            }
            else if(BTSLUtil.isNullString(preferenceCodeString)){
            	response.setPreferenceTypeList(new PreferenceWebDAO().loadSystemPreferenceData(con,controlCodeString, moduleCodeString));
            }
            else {
                userVO = new UserDAO().loadAllUserDetailsByLoginID(con, loginId);

            	response.setPreferenceList(new ControlPreferenceDAO().loadControlPreferenceData(con, controlCodeString, moduleCodeString, preferenceCodeString, userVO.getNetworkID()));
            }
            response.setStatus(PretupsI.RESPONSE_SUCCESS);
            response.setMessageCode(PretupsErrorCodesI.SUCCESS);
   		 	String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
   		 	response.setMessage(resmsg);
   		 	responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
            
        }catch (BTSLBaseException be) {
     	   if (be.getMessage().equalsIgnoreCase("1080001") || be.getMessage().equalsIgnoreCase("1080002")
 					|| be.getMessage().equalsIgnoreCase("1080003") || be.getMessage().equalsIgnoreCase("241023")
 					|| be.getMessage().equalsIgnoreCase("241018")) {
 				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
 				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
 			} else {
 				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
 				response.setStatus(HttpStatus.SC_BAD_REQUEST);
 			}
 			String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
 			response.setMessageCode(be.getMessage());
 			response.setMessage(resmsg);
        }catch (Exception ex) {
 			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
 			response.setStatus(PretupsI.RESPONSE_FAIL);
 			LOG.errorTrace(methodName, ex);
 			LOG.error(methodName, "Exception = " + ex.getMessage());
 		}finally {
 			if (mcomCon != null) {
 				mcomCon.close("");
 				mcomCon = null;
 			}
 			if(con != null)
 				con.close();
 		}
        
 		return response;
		
	}
	@Override
	public BaseResponse updateControlPreference(Locale locale, HttpServletResponse responseSwag,
			UpdateControlPreferenceVO updateControlPreferenceVO,String msisdn) throws SQLException {
		final String methodName = "updateControlPreference";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        BaseResponse response = new BaseResponse();
        ControlPreferenceVO controlPreferenceVO =null;
        int updateCount = 0;
        ArrayList<ControlPreferenceVO> preferenceList = updateControlPreferenceVO.getCtrlPreferenceList();
        try {
        	mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            UserVO userVO = new UserDAO().loadUsersDetails(con, msisdn);
            updateCount = new ControlPreferenceDAO().updateControlPreference(con, preferenceList, new Date(), userVO.getUserID(), userVO.getNetworkID());
            if (con != null) {
                if (updateCount == preferenceList.size()) {
                	mcomCon.finalCommit();
                    // log the data in adminOperationLog.log
                    AdminOperationVO adminOperationVO = new AdminOperationVO();
                    adminOperationVO.setSource(TypesI.LOGGER_PREFERENCE_CONTROLUNIT_SOURCE);
                    adminOperationVO.setDate(new Date());
                    adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                    adminOperationVO.setLoginID(userVO.getLoginID());
                    adminOperationVO.setUserID(userVO.getUserID());
                    adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                    adminOperationVO.setNetworkCode(userVO.getNetworkID());
                    adminOperationVO.setMsisdn(userVO.getMsisdn());
                    for (int i = 0, j = preferenceList.size(); i < j; i++) {
                        controlPreferenceVO = (ControlPreferenceVO) preferenceList.get(i);
                        adminOperationVO.setInfo("Control (" + controlPreferenceVO.getNetworkCode() + ") preference (" + controlPreferenceVO.getPreferenceCode() + ") has modified successfully");
                        AdminOperationLog.log(adminOperationVO);
                    }
                    response.setStatus(PretupsI.RESPONSE_SUCCESS);
					response.setMessageCode(PretupsErrorCodesI.PREFERENCE_UPDATE_SUCCESS);
					String resmsg = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.PREFERENCE_UPDATE_SUCCESS, null);
					response.setMessage(resmsg);
                } else {
                	mcomCon.finalRollback();
					throw new BTSLBaseException(CAC2STransferReversalServiceI.class.getName(), methodName, PretupsErrorCodesI.CTRL_PREFERENCE_UPDATE_FAIL, 0, null);
				
                }
            }
        }catch (BTSLBaseException be) {
      	   if (be.getMessage().equalsIgnoreCase("1080001") || be.getMessage().equalsIgnoreCase("1080002")
  					|| be.getMessage().equalsIgnoreCase("1080003") || be.getMessage().equalsIgnoreCase("241023")
  					|| be.getMessage().equalsIgnoreCase("241018")) {
  				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
  				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
  			} else {
  				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
  				response.setStatus(HttpStatus.SC_BAD_REQUEST);
  			}
  			String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
  			response.setMessageCode(be.getMessage());
  			response.setMessage(resmsg);
         }catch (Exception ex) {
  			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
  			response.setStatus(PretupsI.RESPONSE_FAIL);
  			LOG.errorTrace(methodName, ex);
  			LOG.error(methodName, "Exception = " + ex.getMessage());
  		}finally {
  			if (mcomCon != null) {
  				mcomCon.close("");
  				mcomCon = null;
  			}
  			if(con != null)
  				con.close();
  		}
         
  		return response;
 	
	}
}
