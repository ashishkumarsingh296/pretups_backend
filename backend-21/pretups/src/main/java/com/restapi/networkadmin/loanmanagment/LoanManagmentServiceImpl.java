package com.restapi.networkadmin.loanmanagment;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpStatus;
import org.json.HTTP;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.IDGenerator;
import com.btsl.common.IDGeneratorDAO;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileCombinedVO;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileDetailsVO;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileOracleQry;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.user.businesslogic.LookupCache;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
//import com.inet.annotations.PublicApi;
import com.restapi.networkadmin.loanmanagment.LoanListResponseVO;
import com.restapi.networkadmin.loanmanagment.LoanProductListResponseVO;
import com.restapi.networkadmin.loanmanagment.ModifyLoanProfileRequestVO;
import com.restapi.networkadmin.loanmanagment.AddLoanProfileRequestVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service("LoanManagmentServiceImpl")
public class LoanManagmentServiceImpl implements LoanManagmentServiceI{

        public static final Log LOG = LogFactory.getLog(LoanManagmentServiceImpl.class.getName());
        public static final String classname = "LoanManagmentServiceImpl";
        public static ArrayList<LoanProfileCombinedVO> loanProfileList=null;
       

        @Override
        public LoanListResponseVO loadLoanProfileList(Connection con,String networkName,String categoryCode,String domainCode, HttpServletResponse responseSwag)
                        throws BTSLBaseException, SQLException {
                final String methodName = "loadLoanProfileList";
                if(LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "Entered:=" + methodName);
                }
                LoanListResponseVO loanListResponseVO = new LoanListResponseVO();
                LoanProfileDAO profileList = new LoanProfileDAO();
                Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
                try {
                	     final boolean isUserCategoryAllow = ((Boolean) PreferenceCache
        					.getControlPreference(PreferenceI.CAT_USERWISE_LOAN_ENABLE,networkName,domainCode)).booleanValue();
                      			
		                    if(!isUserCategoryAllow) {
		                    	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LOAN_NOT_ALLOWED);
		                    }              	
                         loanProfileList =  profileList.loadLoanProfiles(con, categoryCode, networkName);
                        if(loanProfileList.isEmpty()) {
                        	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LOAN_FAIL);
                        }else {
                                loanListResponseVO.setloanProfileList(loanProfileList);
                                loanListResponseVO.setStatus((HttpStatus.SC_OK));
                                //change status code
                                String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LOAN_SUCCESS, null);
                                loanListResponseVO.setMessage(resmsg);
                                loanListResponseVO.setMessageCode(PretupsErrorCodesI.LOAN_SUCCESS);
                        }
                }catch(BTSLBaseException baseException) {
                        LOG.error(methodName, "Exception:e=" + baseException);
                        LOG.errorTrace(methodName, baseException);
                        if (!BTSLUtil.isNullString(baseException.getMessage())) {
                                String msg = RestAPIStringParser.getMessage(locale, baseException.getMessage(), null);
                                loanListResponseVO.setMessageCode(baseException.getMessage());
                                loanListResponseVO.setMessage(msg);
                                loanListResponseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
                                responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
                        }
                }catch(Exception exception) {
                        LOG.error(methodName, "Exception:e=" + exception);
                        LOG.errorTrace(methodName, exception);
                        loanListResponseVO.setStatus((HttpStatus.SC_BAD_REQUEST));
                        String resmsg = RestAPIStringParser.getMessage(
                                        new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                                        PretupsErrorCodesI.LOAN_FAIL, null);
                        loanListResponseVO.setMessage(resmsg);
                        loanListResponseVO.setMessageCode(PretupsErrorCodesI.LOAN_FAIL);
                        responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
                }
                return loanListResponseVO;
        }

        @Override
        public LoanListResponseVO viewLoanProfileByID(Connection con, String profileID, HttpServletResponse responseSwag)
                        throws BTSLBaseException, SQLException {
                final String methodName = "viewLoanProfileByID";
                if(LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "Entered:=" + methodName);
                }
                LoanListResponseVO loanListResponseVO = new LoanListResponseVO();
                LoanProfileDAO loadProfileDao = new LoanProfileDAO();
                Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
                try {
                        LoanProfileCombinedVO loanProfile = loadProfileDao.loadLoanProfileById(con, profileID);
                        
                        ArrayList<LoanProfileDetailsVO> loanProfileSlabList =  loadProfileDao.loadLoanProfileSlabs(con, profileID);
                        if(loanProfileSlabList.isEmpty() || BTSLUtil.isNullObject(loanProfile)) {

                        }else {
                                loanListResponseVO.setLoanProfileSlabList(loanProfileSlabList);
                                loanListResponseVO.setombinedVO(loanProfile);
                                loanListResponseVO.setStatus((HttpStatus.SC_OK));
                                //change status code
                                String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LOAN_SUCCESS, null);
                                loanListResponseVO.setMessage(resmsg);
                                loanListResponseVO.setMessageCode(PretupsErrorCodesI.LOAN_SUCCESS);
                        }
                }catch(BTSLBaseException baseException) {
                        LOG.error(methodName, "Exception:e=" + baseException);
                        LOG.errorTrace(methodName, baseException);
                        if (!BTSLUtil.isNullString(baseException.getMessage())) {
                                String msg = RestAPIStringParser.getMessage(locale, baseException.getMessage(), null);
                                loanListResponseVO.setMessageCode(baseException.getMessage());
                                loanListResponseVO.setMessage(msg);
                                loanListResponseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
                                responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
                        }
                }catch(Exception exception) {
                        LOG.error(methodName, "Exception:e=" + exception);
                        LOG.errorTrace(methodName, exception);
                        loanListResponseVO.setStatus((HttpStatus.SC_BAD_REQUEST));
                        String resmsg = RestAPIStringParser.getMessage(
                                        new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                                        PretupsErrorCodesI.LOAN_FAIL, null);
                        loanListResponseVO.setMessage(resmsg);
                        loanListResponseVO.setMessageCode(PretupsErrorCodesI.LOAN_FAIL);
                        responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
                }
                return loanListResponseVO;
        }
        
	@Override
        public LoanListResponseVO deleteLoanProfileByID(Connection con, String profileID, HttpServletResponse responseSwag)
                        throws BTSLBaseException, SQLException {
                final String methodName = "deleteLoanProfileByID";
                if(LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "Entered:=" + methodName);
                }
                LoanListResponseVO loanListResponseVO = new LoanListResponseVO();
                LoanProfileDAO loadProfileDao = new LoanProfileDAO();
                Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
                try {
                        int deleteProfile=loadProfileDao.deleteLoanProfile(con, profileID);
                        if(deleteProfile<=0) {
                        	loanListResponseVO.setMessage(deleteProfile + " Unable to delete profile");
                        }else {
                                loanListResponseVO.setDeleteProfile(deleteProfile);
                                loanListResponseVO.setStatus((HttpStatus.SC_OK));
                                //change status code
                                String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LOAN_DELETE_SUCCESS, null);
                                loanListResponseVO.setMessage(resmsg);
                                loanListResponseVO.setMessageCode(PretupsErrorCodesI.LOAN_DELETE_SUCCESS);
                        }
                }catch(BTSLBaseException baseException) {
                        LOG.error(methodName, "Exception:e=" + baseException);
                        LOG.errorTrace(methodName, baseException);
                        if (!BTSLUtil.isNullString(baseException.getMessage())) {
                                String msg = RestAPIStringParser.getMessage(locale, baseException.getMessage(), null);
                                loanListResponseVO.setMessageCode(baseException.getMessage());
                                loanListResponseVO.setMessage(msg);
                                loanListResponseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
                                responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
                        }
                }catch(Exception exception) {
                        LOG.error(methodName, "Exception:e=" + exception);
                        LOG.errorTrace(methodName, exception);
                        loanListResponseVO.setStatus((HttpStatus.SC_BAD_REQUEST));
                        String resmsg = RestAPIStringParser.getMessage(
                                        new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                                        PretupsErrorCodesI.LOAN_DELETE_FAIL, null);
                        loanListResponseVO.setMessage(resmsg);
                        loanListResponseVO.setMessageCode(PretupsErrorCodesI.LOAN_DELETE_FAIL);
                        responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
                }
                return loanListResponseVO;
                	
	}
	@Override
	public LoanProductListResponseVO viewProductList(Connection con, String loginID, HttpServletResponse response1) throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "viewProductList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		LoanProductListResponseVO response = new LoanProductListResponseVO();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		ArrayList viewList;
		NetworkProductDAO networkProductDAO = new NetworkProductDAO();
		try {

			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			viewList = networkProductDAO.loadProductList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE);
			if (viewList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
			}
			if ((!BTSLUtil.isNullOrEmptyList(viewList))) {
				response.setProductList(viewList);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
			}
		} catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}
	
	@Override
	public BaseResponse modifyLoanProfileDetail(MultiValueMap<String, String> headers,HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response,ModifyLoanProfileRequestVO requestVO) throws Exception {	
		
		
		final String METHOD_NAME = "modifyLoanProfileDetail";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		LoanProfileDAO loanDAO = new LoanProfileDAO();
		Date currentDate = new Date();
		try {
			LoanProfileCombinedVO loanProfileVO= new LoanProfileCombinedVO();
			loanProfileVO.setProfileName(requestVO.getProfileName());
			loanProfileVO.setProfileType(requestVO.getProfileType());
			loanProfileVO.setProfileID(requestVO.getProfileID());
			loanProfileVO.setModifiedOn(currentDate);
			loanProfileVO.setModifiedBy(userVO.getUserID());
			loanProfileVO.setStatus(PretupsI.STATUS);
			loanProfileVO.setLoanProfileDetailsList(requestVO.getLoanProfileDetailsList().getSlabList());
			int update = loanDAO.updateLoanProfiles(con, loanProfileVO); 
            if (update <= 0) {
                try {
                    mcomCon.finalRollback();
                } catch (Exception e) {
                    LOG.errorTrace(METHOD_NAME, e);
                }
                LOG.error(METHOD_NAME, "Error: while updating loan Profile Set");
                throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
            }
            mcomCon.finalCommit();
        	//adding logs starts
            final AdminOperationVO adminOperationVO = new AdminOperationVO();
            adminOperationVO.setSource(PretupsI.BATCH_LOAN_PROFILE_LIST);
            adminOperationVO.setDate(currentDate);
            adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
            adminOperationVO
                .setInfo("Loan Profile for Category(" + loanProfileVO.getCategoryCode() + "), Name(" + loanProfileVO.getProfileName() + "), ID(" + loanProfileVO
                        .getProfileID() + ") has been updated successfully ");
            adminOperationVO.setLoginID(userVO.getLoginID());
            adminOperationVO.setUserID(userVO.getUserID());
            adminOperationVO.setCategoryCode(userVO.getCategoryCode());
            adminOperationVO.setNetworkCode(userVO.getNetworkID());
            adminOperationVO.setMsisdn(userVO.getMsisdn());
            AdminOperationLog.log(adminOperationVO);
            //adding logs end
	        response1.setStatus(HttpStatus.SC_OK);
			response.setStatus((HttpStatus.SC_OK));
			
			final String[] arr = { requestVO.getProfileName() };
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LOAN_MODIFY_SUCCESS, arr);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LOAN_MODIFY_SUCCESS);

            response.setTransactionId(loanProfileVO.getProfileID());
        
        }
        finally {
        	if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
        }
		return response;
		
	}
	
	
	@Override
	public BaseResponse addLoanProfile(MultiValueMap<String, String> headers,HttpServletRequest httpServletRequest, HttpServletResponse response1,
			Connection con,MComConnectionI mcomCon ,Locale locale, UserVO userVO, BaseResponse response,
			AddLoanProfileRequestVO addLoanProfileRequestVO)  throws Exception {
				
		final String METHOD_NAME = "addLoanProfile";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
        
		
        try {
        	final Date currentDate = new Date();
            final LoanProfileDAO loanDAO = new LoanProfileDAO();
            final LoanProfileCombinedVO loanProfileVO = new LoanProfileCombinedVO();
            final IDGeneratorDAO idGeneratorDAO = new IDGeneratorDAO();
			Long profileID = idGeneratorDAO.getNextID(con, "LOAN", TypesI.ALL, "AK",null);
			loanProfileVO.setProfileID(profileID.toString());
			System.out.println(loanProfileVO.getProfileID()+ "arpita details of profile ID  ");
            if(!BTSLUtil.isNullString(loanDAO.isProfileNameExists(con, addLoanProfileRequestVO.getProfileName()))) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LOAN_PROFILE_NAME_EXISTS, 0, null);
			}
            loanProfileVO.setProfileName(addLoanProfileRequestVO.getProfileName());
            loanProfileVO.setCategoryCode(addLoanProfileRequestVO.getCategoryCode());
            loanProfileVO.setProfileType(addLoanProfileRequestVO.getProfileType());
            loanProfileVO.setNetworkCode(addLoanProfileRequestVO.getNetworkCode());
            loanProfileVO.setCreatedOn(currentDate);
            loanProfileVO.setCreatedBy(userVO.getUserID());
            loanProfileVO.setModifiedOn(currentDate);
            loanProfileVO.setModifiedBy(userVO.getUserID());
                       
            // insert LOAN_PROFILES
            loanProfileVO.setLoanProfileDetailsList(addLoanProfileRequestVO.getLoanProfileDetailsList().getSlabList());                      
            final int insertSetCount = loanDAO.addLoanProfile(con, loanProfileVO);
            if (insertSetCount <= 0) {
                try {
                    mcomCon.finalRollback();
                } catch (Exception e) {
                    LOG.errorTrace(METHOD_NAME, e);
                }
                LOG.error(METHOD_NAME, "Error: while Inserting loan Profile Set");
                throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
            }         
            mcomCon.finalCommit();
        	
        	//adding logs starts
            final AdminOperationVO adminOperationVO = new AdminOperationVO();
            adminOperationVO.setSource(PretupsI.BATCH_LOAN_PROFILE_LIST);
            adminOperationVO.setDate(currentDate);
            adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
            adminOperationVO
                .setInfo("Loan Profile for Category(" + loanProfileVO.getCategoryCode() + "), Name(" + loanProfileVO.getProfileName() + "), ID(" + loanProfileVO
                        .getProfileID() + ") has been successfully Added");
            adminOperationVO.setLoginID(userVO.getLoginID());
            adminOperationVO.setUserID(userVO.getUserID());
            adminOperationVO.setCategoryCode(userVO.getCategoryCode());
            adminOperationVO.setNetworkCode(userVO.getNetworkID());
            adminOperationVO.setMsisdn(userVO.getMsisdn());
            AdminOperationLog.log(adminOperationVO);
            //adding logs end
			response1.setStatus(HttpStatus.SC_OK);
			response.setStatus((HttpStatus.SC_OK));
			
			final String[] arr = { addLoanProfileRequestVO.getProfileName() };
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LOAN_ADD_SUCCESS, arr);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LOAN_ADD_SUCCESS);

            response.setTransactionId(loanProfileVO.getProfileID());
        
        }
        finally {
        	if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
        }
		return response;	
	}
}