package com.restapi.superadmin.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.IDGenerator;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.superadmin.requestVO.CatTrfProfileRequestVO;
import com.restapi.superadmin.responseVO.CatTrfProfileListResponseVO;
import com.restapi.superadmin.responseVO.DomainManagmentResponseVO;
import com.restapi.superadmin.serviceI.CategoryTransferProfileService;
import com.web.pretups.channel.profile.businesslogic.TransferProfileWebDAO;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.domain.businesslogic.DomainWebDAO;
import com.web.pretups.gateway.businesslogic.MessageGatewayWebDAO;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;



@Service
public class CategoryTransferProfileServiceImpl implements CategoryTransferProfileService {
	protected static final Log LOG = LogFactory.getLog(CategoryTransferProfileServiceImpl.class.getName());


	private boolean subscriberOutCountFlag = false;
	private boolean unctrlTransferFlag = false;
	private ArrayList transferProfileList = new ArrayList<>();
	@Override
	public CatTrfProfileListResponseVO getCatTrfProfileList(OAuthUser oAuthUser, HttpServletResponse responseSwag,
			Locale locale, String domainCode, String categoryCode,String networkCode) throws SQLException{
		final String methodName = "getCatTrfProfileList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        CatTrfProfileListResponseVO response = new CatTrfProfileListResponseVO();
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = new UserVO();
        TransferProfileWebDAO transferProfileWebDAO = new TransferProfileWebDAO();
        UserDAO userDAO = new UserDAO();
        CategoryWebDAO categoryWebDAO = null;
        NetworkProductDAO networkProductDAO = new NetworkProductDAO();
        TransferProfileVO profileVO  = new TransferProfileVO();
        TransferProfileDAO profileDAO = new TransferProfileDAO();
        
        try {
        	mcomCon = new MComConnection();
        	con = mcomCon.getConnection();
        	categoryWebDAO = new CategoryWebDAO();
        	userVO = userDAO.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
        	transferProfileList = transferProfileWebDAO.loadTransferProfileDetailList(con, categoryCode, PretupsI.PARENT_PROFILE_ID_CATEGORY, userVO.getNetworkID());
        	subscriberOutCountFlag = ((Boolean) PreferenceCache.getSystemPreferenceValue(PretupsI.SUBSCRIBER_TRANSFER_OUTCOUNT)).booleanValue();
        	 boolean flag = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEP_OUTSIDE_TXN_CTRL))).booleanValue();
             if (flag) {
                 flag = categoryWebDAO.isUncontrolTransferAllowed(con, domainCode, categoryCode);
             }
             // ends here
             unctrlTransferFlag = flag;
            if(transferProfileList.size()!=0) {
            TransferProfileVO profileIdVO = (TransferProfileVO) transferProfileList.get(0);
            String profileId = profileIdVO.getProfileId();
            
            profileVO = profileDAO.loadTransferProfileThroughProfileID(con, profileId, networkCode, categoryCode, true);
            transferProfileList.set(0, profileVO);
        	response.setCatProfileTrfList(transferProfileList);
        	response.setStatus(PretupsI.RESPONSE_SUCCESS);
        	response.setMessageCode(PretupsErrorCodesI.SUCCESS);
        	String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
        	response.setMessage(resmsg);
   		 	}
             else
            {
            profileVO = new TransferProfileVO();
            profileVO.setProfileProductList(networkProductDAO.loadNetworkProductList(con, networkCode));     
            transferProfileList.add(0, profileVO);
            response.setCatProfileTrfList(transferProfileList);
            response.setStatus(PretupsI.RESPONSE_FAIL);
            }
            
        	responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
        }
        catch (Exception ex) {
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
	public BaseResponse addCatTrfProfile(Locale locale, CatTrfProfileRequestVO requestVO,
			HttpServletResponse responseSwag, OAuthUser oAuthUser)throws SQLException {
		final String methodName = "addCatTrfProfile";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        BaseResponse response = new BaseResponse();
        TransferProfileVO transferProfileVO = new TransferProfileVO();
        int addCount = 0;
        ArrayList productBalanceList = null;
        TransferProfileWebDAO transferProfileWebDAO = null;
        try {
        	mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            final Date currentDate = new Date();
            transferProfileWebDAO = new TransferProfileWebDAO();
            final UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
            final long profile_id = IDGenerator.getNextID(PretupsI.PROFILE_ID, PretupsI.ALL);
            productBalanceList = requestVO.getProductBalanceList();//networkProductDAO.loadNetworkProductList(con, requestVO.getNetworkCode());
            
            transferProfileVO.setProfileId(String.valueOf(profile_id));
            transferProfileVO.setProfileName(requestVO.getProfileName());
            transferProfileVO.setShortName(requestVO.getShortName());
            transferProfileVO.setDescription(requestVO.getDescription());
            transferProfileVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
            transferProfileVO.setDailyInCount(Long.parseLong(requestVO.getDailyInCount()));
            transferProfileVO.setDailyInValue(PretupsBL.getSystemAmount(requestVO.getDailyInValue()));
            transferProfileVO.setWeeklyInCount(Long.parseLong(requestVO.getWeeklyInCount()));
            transferProfileVO.setWeeklyInValue(PretupsBL.getSystemAmount(requestVO.getWeeklyInValue()));
            transferProfileVO.setMonthlyInCount(Long.parseLong(requestVO.getMonthlyInCount()));
            transferProfileVO.setMonthlyInValue(PretupsBL.getSystemAmount(requestVO.getMonthlyInValue()));
            transferProfileVO.setDailyOutCount(Long.parseLong(requestVO.getDailyOutCount()));
            transferProfileVO.setDailyOutValue(PretupsBL.getSystemAmount(requestVO.getDailyOutValue()));
            transferProfileVO.setWeeklyOutCount(Long.parseLong(requestVO.getWeeklyOutCount()));
            transferProfileVO.setWeeklyOutValue(PretupsBL.getSystemAmount(requestVO.getWeeklyOutValue()));
            transferProfileVO.setMonthlyOutCount(Long.parseLong(requestVO.getMonthlyOutCount()));
            transferProfileVO.setMonthlyOutValue(PretupsBL.getSystemAmount(requestVO.getMonthlyOutValue()));
            
            transferProfileVO.setDailyInAltCount(Long.parseLong(requestVO.getDailyInAltCount()));
            transferProfileVO.setDailyInAltValue(PretupsBL.getSystemAmount(requestVO.getDailyInAltValue()));
            transferProfileVO.setWeeklyInAltCount(Long.parseLong(requestVO.getWeeklyInAltCount()));
            transferProfileVO.setWeeklyInAltValue(PretupsBL.getSystemAmount(requestVO.getWeeklyInAltValue()));
            transferProfileVO.setMonthlyInAltCount(Long.parseLong(requestVO.getMonthlyInAltCount()));
            transferProfileVO.setMonthlyInAltValue(PretupsBL.getSystemAmount(requestVO.getMonthlyInAltValue()));
            transferProfileVO.setDailyOutAltCount(Long.parseLong(requestVO.getDailyOutAltCount()));
            transferProfileVO.setDailyOutAltValue(PretupsBL.getSystemAmount(requestVO.getDailyOutAltValue()));
            transferProfileVO.setWeeklyOutAltCount(Long.parseLong(requestVO.getWeeklyOutAltCount()));
            transferProfileVO.setWeeklyOutAltValue(PretupsBL.getSystemAmount(requestVO.getWeeklyOutAltValue()));
            transferProfileVO.setMonthlyOutAltCount(Long.parseLong(requestVO.getMonthlyOutAltCount()));
            transferProfileVO.setMonthlyOutAltValue(PretupsBL.getSystemAmount(requestVO.getMonthlyOutAltValue()));
            if(subscriberOutCountFlag) {
            	transferProfileVO.setDailySubscriberOutCount(Long.parseLong(requestVO.getDailySubscriberOutCount()));
                transferProfileVO.setDailySubscriberOutValue(PretupsBL.getSystemAmount(requestVO.getDailySubscriberOutValue()));
                transferProfileVO.setWeeklySubscriberOutCount(Long.parseLong(requestVO.getWeeklySubscriberOutCount()));
                transferProfileVO.setWeeklySubscriberOutValue(PretupsBL.getSystemAmount(requestVO.getWeeklySubscriberOutValue()));
                transferProfileVO.setMonthlySubscriberOutCount(Long.parseLong(requestVO.getMonthlySubscriberOutCount()));
                transferProfileVO.setMonthlySubscriberOutValue(PretupsBL.getSystemAmount(requestVO.getMonthlySubscriberOutValue()));
                
            	transferProfileVO.setDailySubscriberOutAltCount(Long.parseLong(requestVO.getDailySubscriberOutAltCount()));
                transferProfileVO.setDailySubscriberOutAltValue(PretupsBL.getSystemAmount(requestVO.getDailySubscriberOutAltValue()));
                transferProfileVO.setWeeklySubscriberOutAltCount(Long.parseLong(requestVO.getWeeklySubscriberOutAltCount()));
                transferProfileVO.setWeeklySubscriberOutAltValue(PretupsBL.getSystemAmount(requestVO.getWeeklySubscriberOutAltValue()));
                transferProfileVO.setMonthlySubscriberOutAltCount(Long.parseLong(requestVO.getMonthlySubscriberOutAltCount()));
                transferProfileVO.setMonthlySubscriberOutAltValue(PretupsBL.getSystemAmount(requestVO.getMonthlySubscriberOutAltValue()));
                
            }
            transferProfileVO.setDailySubscriberInCount(Long.parseLong(requestVO.getDailySubscriberInCount()));
            transferProfileVO.setDailySubscriberInValue(PretupsBL.getSystemAmount(requestVO.getDailySubscriberInValue()));
            transferProfileVO.setWeeklySubscriberInCount(Long.parseLong(requestVO.getWeeklySubscriberInCount()));
            transferProfileVO.setWeeklySubscriberInValue(PretupsBL.getSystemAmount(requestVO.getWeeklySubscriberInValue()));
            transferProfileVO.setMonthlySubscriberInCount(Long.parseLong(requestVO.getMonthlySubscriberInCount()));
            transferProfileVO.setMonthlySubscriberInValue(PretupsBL.getSystemAmount(requestVO.getMonthlySubscriberInValue()));
            
        	transferProfileVO.setDailySubscriberInAltCount(Long.parseLong(requestVO.getDailySubscriberInAltCount()));
            transferProfileVO.setDailySubscriberInAltValue(PretupsBL.getSystemAmount(requestVO.getDailySubscriberInAltValue()));
            transferProfileVO.setWeeklySubscriberInAltCount(Long.parseLong(requestVO.getWeeklySubscriberInAltCount()));
            transferProfileVO.setWeeklySubscriberInAltValue(PretupsBL.getSystemAmount(requestVO.getWeeklySubscriberInAltValue()));
            transferProfileVO.setMonthlySubscriberInAltCount(Long.parseLong(requestVO.getMonthlySubscriberInAltCount()));
            transferProfileVO.setMonthlySubscriberInAltValue(PretupsBL.getSystemAmount(requestVO.getMonthlySubscriberInAltValue()));
            
            if(unctrlTransferFlag) {
            	transferProfileVO.setUnctrlDailyInCount(Long.parseLong(requestVO.getUnctrlDailyInCount()));
                transferProfileVO.setUnctrlDailyInValue(PretupsBL.getSystemAmount(requestVO.getUnctrlDailyInValue()));
                transferProfileVO.setUnctrlWeeklyInCount(Long.parseLong(requestVO.getUnctrlWeeklyInCount()));
                transferProfileVO.setUnctrlWeeklyInValue(PretupsBL.getSystemAmount(requestVO.getUnctrlWeeklyInValue()));
                transferProfileVO.setUnctrlMonthlyInCount(Long.parseLong(requestVO.getUnctrlMonthlyInCount()));
                transferProfileVO.setUnctrlMonthlyInValue(PretupsBL.getSystemAmount(requestVO.getUnctrlMonthlyInValue()));
                transferProfileVO.setUnctrlDailyOutCount(Long.parseLong(requestVO.getUnctrlDailyOutCount()));
                transferProfileVO.setUnctrlDailyOutValue(PretupsBL.getSystemAmount(requestVO.getUnctrlDailyOutValue()));
                transferProfileVO.setUnctrlWeeklyOutCount(Long.parseLong(requestVO.getUnctrlWeeklyOutCount()));
                transferProfileVO.setUnctrlWeeklyOutValue(PretupsBL.getSystemAmount(requestVO.getUnctrlWeeklyOutValue()));
                transferProfileVO.setUnctrlMonthlyOutCount(Long.parseLong(requestVO.getUnctrlMonthlyOutCount()));
                transferProfileVO.setUnctrlMonthlyOutValue(PretupsBL.getSystemAmount(requestVO.getUnctrlMonthlyOutValue()));
                
                transferProfileVO.setUnctrlDailyInAltCount(Long.parseLong(requestVO.getUnctrlDailyInAltCount()));
                transferProfileVO.setUnctrlDailyInAltValue(PretupsBL.getSystemAmount(requestVO.getUnctrlDailyInAltValue()));
                transferProfileVO.setUnctrlWeeklyInAltCount(Long.parseLong(requestVO.getUnctrlWeeklyInAltCount()));
                transferProfileVO.setUnctrlWeeklyInAltValue(PretupsBL.getSystemAmount(requestVO.getUnctrlWeeklyInAltValue()));
                transferProfileVO.setUnctrlMonthlyInAltCount(Long.parseLong(requestVO.getUnctrlMonthlyInAltCount()));
                transferProfileVO.setUnctrlMonthlyInAltValue(PretupsBL.getSystemAmount(requestVO.getUnctrlMonthlyInAltValue()));
                transferProfileVO.setUnctrlDailyOutAltCount(Long.parseLong(requestVO.getUnctrlDailyOutAltCount()));
                transferProfileVO.setUnctrlDailyOutAltValue(PretupsBL.getSystemAmount(requestVO.getUnctrlDailyOutAltValue()));
                transferProfileVO.setUnctrlWeeklyOutAltCount(Long.parseLong(requestVO.getUnctrlWeeklyOutAltCount()));
                transferProfileVO.setUnctrlWeeklyOutAltValue(PretupsBL.getSystemAmount(requestVO.getUnctrlWeeklyOutAltValue()));
                transferProfileVO.setUnctrlMonthlyOutAltCount(Long.parseLong(requestVO.getUnctrlMonthlyOutAltCount()));
                transferProfileVO.setUnctrlMonthlyOutAltValue(PretupsBL.getSystemAmount(requestVO.getUnctrlMonthlyOutAltValue()));
                
            }
            transferProfileVO.setNetworkCode(requestVO.getNetworkCode());
            transferProfileVO.setCategory(requestVO.getCategory());
            transferProfileVO.setLastModifiedTime(requestVO.getLastModifiedTime());
            transferProfileVO.setParentProfileID(PretupsI.PARENT_PROFILE_ID_CATEGORY);
            if(BTSLUtil.isNullString(requestVO.getIsDefault())){
            	transferProfileVO.setIsDefault(PretupsI.NO);
            } else {
            	transferProfileVO.setIsDefault(requestVO.getIsDefault());
            }
            transferProfileVO.setCreatedBy(userVO.getUserID());
            transferProfileVO.setModifiedBy(userVO.getUserID());
            transferProfileVO.setCreatedOn(currentDate);
            transferProfileVO.setModifiedOn(currentDate);
            
            
            if (transferProfileWebDAO.isTransferProfileShortNameExist(con, transferProfileVO.getShortName())) {
                throw new BTSLBaseException(this, methodName, "profile.error.shortnameexist", "");
            }
            if (transferProfileWebDAO.isTransferProfileNameExist(con, transferProfileVO.getProfileName())) {
                throw new BTSLBaseException(this, methodName, "profile.error.profilenameexist", "");
            }
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue() && PretupsI.PARENT_PROFILE_ID_USER.equals(transferProfileVO.getParentProfileID()) && PretupsI.YES
                    .equals(transferProfileVO.getIsDefault())) {
                    transferProfileWebDAO.updateDefaultProfile(con, transferProfileVO.getCategory(), transferProfileVO.getNetworkCode());
                }
            
            addCount = transferProfileWebDAO.addTransferControlProfile(con, transferProfileVO, profile_id);
            if(addCount>0) {
            	final int count = transferProfileWebDAO.addTransferControlProfileProduct(con, productBalanceList, profile_id);
            	if(count>0) {
            		mcomCon.finalCommit();
            		response.setStatus(PretupsI.RESPONSE_SUCCESS);
            		response.setMessageCode(PretupsErrorCodesI.SUCCESS);
                    response.setTransactionId(String.valueOf(profile_id));
            		String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
           		 	response.setMessage(resmsg);
           		 	responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
            	}
            	else {
            		mcomCon.finalRollback();
            		throw new BTSLBaseException(this.getClass().getName(),methodName,PretupsErrorCodesI.FAILED,0,null );	
            	}
            }else {
            	mcomCon.finalRollback();
        		throw new BTSLBaseException(this.getClass().getName(),methodName,PretupsErrorCodesI.FAILED,0,null );	
        	
            }
        } catch (BTSLBaseException e) {
			LOG.error(methodName, "Exception:e=" + e);
			LOG.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(locale, e.getMessage(), e.getArgs());

			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.FAILED);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
        finally {
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
	public BaseResponse modifyCatTrfProfile(Locale locale, CatTrfProfileRequestVO requestVO,
			HttpServletResponse responseSwag, OAuthUser oAuthUser) throws SQLException {
		final String methodName = "modifyCatTrfProfile";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        
        Connection con = null;
        MComConnectionI mcomCon = null;
        BaseResponse response = new BaseResponse();
        TransferProfileVO transferProfileVO = new TransferProfileVO();
        int updateCount = 0;
        ArrayList productBalanceList = null;
        TransferProfileWebDAO transferProfileWebDAO = null;
        String categoryDomainCode = null;
        NetworkProductDAO networkProductDAO = null;
        try {
        	mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            final Date currentDate = new Date();
            transferProfileWebDAO = new TransferProfileWebDAO();
            networkProductDAO = new NetworkProductDAO();
            final UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
            String profile_id =requestVO.getProfileId(); //IDGenerator.getNextID(PretupsI.PROFILE_ID, PretupsI.ALL);
            productBalanceList = requestVO.getProductBalanceList();//networkProductDAO.loadNetworkProductList(con, requestVO.getNetworkCode());
            
            transferProfileVO.setProfileId(profile_id);
            transferProfileVO.setProfileName(requestVO.getProfileName());
            transferProfileVO.setShortName(requestVO.getShortName());
            transferProfileVO.setDescription(requestVO.getDescription());
            transferProfileVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
            transferProfileVO.setDailyInCount(Long.parseLong(requestVO.getDailyInCount()));
            transferProfileVO.setDailyInValue(PretupsBL.getSystemAmount(requestVO.getDailyInValue()));
            transferProfileVO.setWeeklyInCount(Long.parseLong(requestVO.getWeeklyInCount()));
            transferProfileVO.setWeeklyInValue(PretupsBL.getSystemAmount(requestVO.getWeeklyInValue()));
            transferProfileVO.setMonthlyInCount(Long.parseLong(requestVO.getMonthlyInCount()));
            transferProfileVO.setMonthlyInValue(PretupsBL.getSystemAmount(requestVO.getMonthlyInValue()));
            transferProfileVO.setDailyOutCount(Long.parseLong(requestVO.getDailyOutCount()));
            transferProfileVO.setDailyOutValue(PretupsBL.getSystemAmount(requestVO.getDailyOutValue()));
            transferProfileVO.setWeeklyOutCount(Long.parseLong(requestVO.getWeeklyOutCount()));
            transferProfileVO.setWeeklyOutValue(PretupsBL.getSystemAmount(requestVO.getWeeklyOutValue()));
            transferProfileVO.setMonthlyOutCount(Long.parseLong(requestVO.getMonthlyOutCount()));
            transferProfileVO.setMonthlyOutValue(PretupsBL.getSystemAmount(requestVO.getMonthlyOutValue()));
            
            transferProfileVO.setDailyInAltCount(Long.parseLong(requestVO.getDailyInAltCount()));
            transferProfileVO.setDailyInAltValue(PretupsBL.getSystemAmount(requestVO.getDailyInAltValue()));
            transferProfileVO.setWeeklyInAltCount(Long.parseLong(requestVO.getWeeklyInAltCount()));
            transferProfileVO.setWeeklyInAltValue(PretupsBL.getSystemAmount(requestVO.getWeeklyInAltValue()));
            transferProfileVO.setMonthlyInAltCount(Long.parseLong(requestVO.getMonthlyInAltCount()));
            transferProfileVO.setMonthlyInAltValue(PretupsBL.getSystemAmount(requestVO.getMonthlyInAltValue()));
            transferProfileVO.setDailyOutAltCount(Long.parseLong(requestVO.getDailyOutAltCount()));
            transferProfileVO.setDailyOutAltValue(PretupsBL.getSystemAmount(requestVO.getDailyOutAltValue()));
            transferProfileVO.setWeeklyOutAltCount(Long.parseLong(requestVO.getWeeklyOutAltCount()));
            transferProfileVO.setWeeklyOutAltValue(PretupsBL.getSystemAmount(requestVO.getWeeklyOutAltValue()));
            transferProfileVO.setMonthlyOutAltCount(Long.parseLong(requestVO.getMonthlyOutAltCount()));
            transferProfileVO.setMonthlyOutAltValue(PretupsBL.getSystemAmount(requestVO.getMonthlyOutAltValue()));
            if(subscriberOutCountFlag) {
            	transferProfileVO.setDailySubscriberOutCount(Long.parseLong(requestVO.getDailySubscriberOutCount()));
                transferProfileVO.setDailySubscriberOutValue(PretupsBL.getSystemAmount(requestVO.getDailySubscriberOutValue()));
                transferProfileVO.setWeeklySubscriberOutCount(Long.parseLong(requestVO.getWeeklySubscriberOutCount()));
                transferProfileVO.setWeeklySubscriberOutValue(PretupsBL.getSystemAmount(requestVO.getWeeklySubscriberOutValue()));
                transferProfileVO.setMonthlySubscriberOutCount(Long.parseLong(requestVO.getMonthlySubscriberOutCount()));
                transferProfileVO.setMonthlySubscriberOutValue(PretupsBL.getSystemAmount(requestVO.getMonthlySubscriberOutValue()));
                
            	transferProfileVO.setDailySubscriberOutAltCount(Long.parseLong(requestVO.getDailySubscriberOutAltCount()));
                transferProfileVO.setDailySubscriberOutAltValue(PretupsBL.getSystemAmount(requestVO.getDailySubscriberOutAltValue()));
                transferProfileVO.setWeeklySubscriberOutAltCount(Long.parseLong(requestVO.getWeeklySubscriberOutAltCount()));
                transferProfileVO.setWeeklySubscriberOutAltValue(PretupsBL.getSystemAmount(requestVO.getWeeklySubscriberOutAltValue()));
                transferProfileVO.setMonthlySubscriberOutAltCount(Long.parseLong(requestVO.getMonthlySubscriberOutAltCount()));
                transferProfileVO.setMonthlySubscriberOutAltValue(PretupsBL.getSystemAmount(requestVO.getMonthlySubscriberOutAltValue()));
                
            }
            transferProfileVO.setDailySubscriberInCount(Long.parseLong(requestVO.getDailySubscriberInCount()));
            transferProfileVO.setDailySubscriberInValue(PretupsBL.getSystemAmount(requestVO.getDailySubscriberInValue()));
            transferProfileVO.setWeeklySubscriberInCount(Long.parseLong(requestVO.getWeeklySubscriberInCount()));
            transferProfileVO.setWeeklySubscriberInValue(PretupsBL.getSystemAmount(requestVO.getWeeklySubscriberInValue()));
            transferProfileVO.setMonthlySubscriberInCount(Long.parseLong(requestVO.getMonthlySubscriberInCount()));
            transferProfileVO.setMonthlySubscriberInValue(PretupsBL.getSystemAmount(requestVO.getMonthlySubscriberInValue()));
            
        	transferProfileVO.setDailySubscriberInAltCount(Long.parseLong(requestVO.getDailySubscriberInAltCount()));
            transferProfileVO.setDailySubscriberInAltValue(PretupsBL.getSystemAmount(requestVO.getDailySubscriberInAltValue()));
            transferProfileVO.setWeeklySubscriberInAltCount(Long.parseLong(requestVO.getWeeklySubscriberInAltCount()));
            transferProfileVO.setWeeklySubscriberInAltValue(PretupsBL.getSystemAmount(requestVO.getWeeklySubscriberInAltValue()));
            transferProfileVO.setMonthlySubscriberInAltCount(Long.parseLong(requestVO.getMonthlySubscriberInAltCount()));
            transferProfileVO.setMonthlySubscriberInAltValue(PretupsBL.getSystemAmount(requestVO.getMonthlySubscriberInAltValue()));
            
            if(unctrlTransferFlag) {
            	transferProfileVO.setUnctrlDailyInCount(Long.parseLong(requestVO.getUnctrlDailyInCount()));
                transferProfileVO.setUnctrlDailyInValue(PretupsBL.getSystemAmount(requestVO.getUnctrlDailyInValue()));
                transferProfileVO.setUnctrlWeeklyInCount(Long.parseLong(requestVO.getUnctrlWeeklyInCount()));
                transferProfileVO.setUnctrlWeeklyInValue(PretupsBL.getSystemAmount(requestVO.getUnctrlWeeklyInValue()));
                transferProfileVO.setUnctrlMonthlyInCount(Long.parseLong(requestVO.getUnctrlMonthlyInCount()));
                transferProfileVO.setUnctrlMonthlyInValue(PretupsBL.getSystemAmount(requestVO.getUnctrlMonthlyInValue()));
                transferProfileVO.setUnctrlDailyOutCount(Long.parseLong(requestVO.getUnctrlDailyOutCount()));
                transferProfileVO.setUnctrlDailyOutValue(PretupsBL.getSystemAmount(requestVO.getUnctrlDailyOutValue()));
                transferProfileVO.setUnctrlWeeklyOutCount(Long.parseLong(requestVO.getUnctrlWeeklyOutCount()));
                transferProfileVO.setUnctrlWeeklyOutValue(PretupsBL.getSystemAmount(requestVO.getUnctrlWeeklyOutValue()));
                transferProfileVO.setUnctrlMonthlyOutCount(Long.parseLong(requestVO.getUnctrlMonthlyOutCount()));
                transferProfileVO.setUnctrlMonthlyOutValue(PretupsBL.getSystemAmount(requestVO.getUnctrlMonthlyOutValue()));
                
                transferProfileVO.setUnctrlDailyInAltCount(Long.parseLong(requestVO.getUnctrlDailyInAltCount()));
                transferProfileVO.setUnctrlDailyInAltValue(PretupsBL.getSystemAmount(requestVO.getUnctrlDailyInAltValue()));
                transferProfileVO.setUnctrlWeeklyInAltCount(Long.parseLong(requestVO.getUnctrlWeeklyInAltCount()));
                transferProfileVO.setUnctrlWeeklyInAltValue(PretupsBL.getSystemAmount(requestVO.getUnctrlWeeklyInAltValue()));
                transferProfileVO.setUnctrlMonthlyInAltCount(Long.parseLong(requestVO.getUnctrlMonthlyInAltCount()));
                transferProfileVO.setUnctrlMonthlyInAltValue(PretupsBL.getSystemAmount(requestVO.getUnctrlMonthlyInAltValue()));
                transferProfileVO.setUnctrlDailyOutAltCount(Long.parseLong(requestVO.getUnctrlDailyOutAltCount()));
                transferProfileVO.setUnctrlDailyOutAltValue(PretupsBL.getSystemAmount(requestVO.getUnctrlDailyOutAltValue()));
                transferProfileVO.setUnctrlWeeklyOutAltCount(Long.parseLong(requestVO.getUnctrlWeeklyOutAltCount()));
                transferProfileVO.setUnctrlWeeklyOutAltValue(PretupsBL.getSystemAmount(requestVO.getUnctrlWeeklyOutAltValue()));
                transferProfileVO.setUnctrlMonthlyOutAltCount(Long.parseLong(requestVO.getUnctrlMonthlyOutAltCount()));
                transferProfileVO.setUnctrlMonthlyOutAltValue(PretupsBL.getSystemAmount(requestVO.getUnctrlMonthlyOutAltValue()));
                
            }
            transferProfileVO.setNetworkCode(requestVO.getNetworkCode());
            transferProfileVO.setCategory(requestVO.getCategory());
            transferProfileVO.setLastModifiedTime(requestVO.getLastModifiedTime());
            transferProfileVO.setParentProfileID(PretupsI.PARENT_PROFILE_ID_CATEGORY);

            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue()) {
            	transferProfileVO.setIsDefault(PretupsI.YES);
            }
            transferProfileVO.setCreatedBy(userVO.getUserID());
            transferProfileVO.setModifiedBy(userVO.getUserID());
            transferProfileVO.setCreatedOn(currentDate);
            transferProfileVO.setModifiedOn(currentDate);
            final String shortName = requestVO.getShortName();
            final String profileName = requestVO.getProfileName();
            
            
            if (transferProfileWebDAO.isTransferProfileNameExistForModify(con, profileName, profile_id)) {
                throw new BTSLBaseException(this, methodName, "profile.error.profilenameexist", methodName);
            }
            if (transferProfileWebDAO.isTransferProfileShortNameExistForModify(con, shortName, profile_id)) {
                throw new BTSLBaseException(this, methodName, "profile.error.shortnameexist", methodName);
            }
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue() && PretupsI.PARENT_PROFILE_ID_USER.equals(transferProfileVO.getParentProfileID()) && !PretupsI.YES
                .equals("N") && PretupsI.YES.equals(transferProfileVO.getIsDefault())) {
                transferProfileWebDAO.updateDefaultProfile(con, transferProfileVO.getCategory(), transferProfileVO.getNetworkCode());
            }
            
            updateCount = transferProfileWebDAO.modifyTransferControlProfile(con, transferProfileVO);
            if(updateCount>0) {
            	updateCount = transferProfileWebDAO.modifyTransferControlProfileProduct(con, productBalanceList, profile_id);
                if(updateCount>0) {
            		mcomCon.finalCommit();
            		response.setStatus(PretupsI.RESPONSE_SUCCESS);
            		response.setMessageCode(PretupsErrorCodesI.SUCCESS);
            		String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
           		 	response.setMessage(resmsg);
           		 	responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
            	}
            	else {
            		mcomCon.finalRollback();
            		throw new BTSLBaseException(this.getClass().getName(),methodName,PretupsErrorCodesI.FAILED,0,null );	
            	}
            }else {
            	mcomCon.finalRollback();
        		throw new BTSLBaseException(this.getClass().getName(),methodName,PretupsErrorCodesI.FAILED,0,null );	
        	
            }
        } catch (Exception e) {
			LOG.error(methodName, "Exception:e=" + e);
			LOG.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					e.getMessage(), null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.FAILED);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
        finally {
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
	public BaseResponse deleteCatTrfProfile(Locale locale, HttpServletResponse responseSwag, OAuthUser oAuthUser,
			String profileId) throws SQLException {
		final String methodName = "deleteCatTrfProfile";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        BaseResponse response = new BaseResponse();
        TransferProfileWebDAO transferProfileWebDAO = null;
        TransferProfileVO profileVO = null;
        ChannelUserWebDAO channelUserWebDAO = null;
        int count = 0;

        final TransferProfileVO lastModifiedProfileVO = new TransferProfileVO();

        try {
        	
        	mcomCon = new MComConnection();
        	con = mcomCon.getConnection();
        	profileVO = new TransferProfileVO();
        	transferProfileWebDAO = new TransferProfileWebDAO();

        	if (transferProfileList != null) {
                for (int i = 0; i < transferProfileList.size(); i++) {
                    profileVO = (TransferProfileVO) transferProfileList.get(i);
                    if (profileVO.getProfileId().equalsIgnoreCase(profileId)) {
                        lastModifiedProfileVO.setLastModifiedTime(profileVO.getLastModifiedTime());
                        break;
                    }
                }
            }
        	channelUserWebDAO = new ChannelUserWebDAO();
            count = channelUserWebDAO.userExistForTransferProfile(con, profileId);
            if (count > 0) {
            	String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_ASSOCIATED,null);
    			response.setMessageCode(PretupsErrorCodesI.USER_ASSOCIATED);
    			response.setMessage(resmsg);
                throw new BTSLBaseException(this,methodName, PretupsErrorCodesI.USER_ASSOCIATED);
            }
        
                if (transferProfileWebDAO.isTransferProfileExistForCategoryCode(con, profileVO.getCategory(), profileVO.getNetworkCode(), PretupsI.PARENT_PROFILE_ID_USER)) {
                	String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MULTI_USER_ASSOCIATED,null);
        			response.setMessageCode(PretupsErrorCodesI.MULTI_USER_ASSOCIATED);
        			response.setMessage(resmsg);
                	
                	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MULTI_USER_ASSOCIATED);
                }
        	
        	
        	Date currDate = new Date();
        	lastModifiedProfileVO.setModifiedOn(currDate);
        	lastModifiedProfileVO.setModifiedBy(oAuthUser.getData().getUserid());
        	int countD = transferProfileWebDAO.deleteTransferControlProfile(con, profileId, lastModifiedProfileVO);
        	if(countD>0) {
        		mcomCon.finalCommit();
        		response.setStatus(PretupsI.RESPONSE_SUCCESS);
    			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
       		 	response.setMessage(resmsg);
       		 	response.setMessageCode(PretupsErrorCodesI.SUCCESS);
    			responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
    			
        	}
        	else {
        		mcomCon.finalRollback();
        		throw new Exception("profile.transferprofileaction.msg.deleteunsuccess");				
        	}
        }catch (Exception e) {
			LOG.error(methodName, "Exception:e=" + e);
			LOG.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));			
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					e.getMessage(), null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.FAILED);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
        finally {
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
	public DomainManagmentResponseVO getdomainManagmentList(Locale locale, HttpServletResponse responseSwag,
			OAuthUser oAuthUser, String domainType) throws SQLException{
		final String methodName = "getdomainManagmentList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        DomainManagmentResponseVO response = new DomainManagmentResponseVO();
        DomainWebDAO domainWebDAO = new DomainWebDAO();
        ArrayList arrDomainTypes = null;
        ArrayList messageGatewayTypeList = null;
        ArrayList arrGeographicalDomain = null; 
        HashMap roleList = null;
        HashMap groupList = null;
        MessageGatewayWebDAO messageGatewaywebDAO = new MessageGatewayWebDAO();
        GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO();
        
        try {
        	mcomCon = new MComConnection();
        	con = mcomCon.getConnection();
        	if(BTSLUtil.isNullString(domainType))
        		arrDomainTypes = domainWebDAO.loadDomainTypeList(con);
        	else {
        		messageGatewayTypeList = messageGatewaywebDAO.loadMessageGatewayTypeList(con, PretupsI.GATEWAY_DISPLAY_ALLOW_YES);
        		arrGeographicalDomain = geographicalDomainWebDAO.loadGeographicalDomainTypeList(con);
        		roleList = domainWebDAO.loadRolesListNew(con, domainType, PretupsI.SYSTEM_ROLE);
        	}
            response.setRoleList(roleList);
            response.setDomainList(arrDomainTypes);
            response.setGeoList(arrGeographicalDomain);
            response.setAllowdSource(messageGatewayTypeList);
            response.setStatus(HttpStatus.SC_OK);
            
        }catch (Exception e) {
			LOG.error(methodName, "Exception:e=" + e);
			LOG.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					e.getMessage(), null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.FAILED);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
        finally {
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