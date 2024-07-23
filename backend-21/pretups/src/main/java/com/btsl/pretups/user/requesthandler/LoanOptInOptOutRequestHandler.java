package com.btsl.pretups.user.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.NetworkProductCache;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserLoanDAO;
import com.btsl.user.businesslogic.UserLoanVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class LoanOptInOptOutRequestHandler implements ServiceKeywordControllerI {
	   private Log log = LogFactory.getLog(LoanOptInOptOutRequestHandler.class.getName());
	    private boolean _isValidTimeForOptInOut = false;
	    private boolean _isValidLMSProfile = false;
	    private boolean _isAlreadyOptIn = false;
	    private UserLoanDAO userLoanDAO = new UserLoanDAO();

	
	
	public void process(RequestVO p_requestVO) {
		 final String METHOD_NAME = "process";
		
		if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, PretupsI.ENTERED+ p_requestVO+" messageLen=" + p_requestVO.getRequestMessageArray().length+"p_requestVO.getRequestMessageArray()="+p_requestVO.getRequestMessageArray());
        }
		Connection con = null;
        MComConnectionI mcomCon = null;
        final String serviceType = p_requestVO.getServiceType();
        
        try {
		final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
         UserPhoneVO userPhoneVO = null;
         if (!channelUserVO.isStaffUser()) {
             userPhoneVO = channelUserVO.getUserPhoneVO();
         } else {
             userPhoneVO = channelUserVO.getStaffUserDetails().getUserPhoneVO();
         }
         
         final String[]  messageArr = p_requestVO.getRequestMessageArray();
         mcomCon = new MComConnection();
         con=mcomCon.getConnection();
         
   
         if (userPhoneVO.getPinRequired().equals(PretupsI.YES) && p_requestVO.isPinValidationRequired()) {
             try {
                 ChannelUserBL.validatePIN(con, channelUserVO, p_requestVO.getPin());
             } catch (BTSLBaseException be) {
                 log.errorTrace(METHOD_NAME, be);
                 if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                     con.commit();
                 }
                 throw be;
             }
         }
         
         
     	
         ArrayList userLoanList=new ArrayList();
		String productCode =messageArr[2];
		
		
		if(NetworkProductCache.getObject(productCode)==null)
		{
			 throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.INVALID_PRODUCT_CODE);
		}
		UserLoanVO userLoanVO=new UserLoanVO();
		userLoanVO.setUser_id(channelUserVO.getUserID());
		userLoanVO.setProduct_code(productCode);
		
		if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, " serviceType=" +serviceType);
        }
		
		if(ParserUtility.SERVICE_LOAN_OPTIN_REQ.equals(serviceType)){
			final boolean isUserCategoryAllow = ((Boolean) PreferenceCache
					.getControlPreference(
							PreferenceI.CAT_USERWISE_LOAN_ENABLE,
							channelUserVO.getNetworkID(),
							channelUserVO.getCategoryCode())).booleanValue();
			
			   log.debug("process", "isUserCategoryAllow: " + isUserCategoryAllow);
               
			
			if(!isUserCategoryAllow){
				
				  throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.INVALID_CATEGORY_FOR_OPTIN);
			}
			
			userLoanVO.setOptinout_allowed(PretupsI.YES);
					
		}
		else if(ParserUtility.SERVICE_LOAN_OPTOUT_REQ.equals(serviceType)){
			userLoanVO.setOptinout_allowed(PretupsI.NO);
			
		}
		userLoanVO.setOptinout_by(PretupsI.SYSTEM);
		userLoanVO.setOptinout_on(new Date());
		
		userLoanList.add(userLoanVO); // adding the last entry				
		     
         int updateCount = userLoanDAO.insertUpdateUserLoanOptInOptOut(con, userLoanList);
         if (updateCount<=0) {
             throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.LOAN_ERROR_EXCEPTION);
         }

       
         
         if (updateCount>0)
         {
        	 mcomCon.finalCommit();
        		if(ParserUtility.SERVICE_LOAN_OPTIN_REQ.equals(serviceType))
        			p_requestVO.setMessageCode(PretupsErrorCodesI.LOAN_OPTIN_SUCCESS);
        		else if(ParserUtility.SERVICE_LOAN_OPTOUT_REQ.equals(serviceType))
        			p_requestVO.setMessageCode(PretupsErrorCodesI.LOAN_OPTOUT_SUCCESS);

         }
        }
        catch (BTSLBaseException e) {
          	try {
        		mcomCon.finalRollback();
        		p_requestVO.setMessageCode(e.getMessage());
        		p_requestVO.setMessageArguments(e.getArgs());
        	} catch (SQLException e1) {
        		log.trace(METHOD_NAME, e1.getMessage());
    			p_requestVO.setMessageCode(PretupsErrorCodesI.LOAN_ERROR_EXCEPTION);
        	}

        	log.errorTrace(METHOD_NAME, e);
        	log.error(METHOD_NAME, PretupsI.BTSLEXCEPTION + e);
        	  
          }
            catch(Exception e) {
            	try {
            		mcomCon.finalRollback();
            		p_requestVO.setMessageCode(PretupsErrorCodesI.LOAN_ERROR_EXCEPTION);
            		
            	} catch (SQLException e1) {
            		log.trace(METHOD_NAME, e1.getMessage());
            		p_requestVO.setMessageCode(PretupsErrorCodesI.LOAN_ERROR_EXCEPTION);
            	}

            	log.errorTrace(METHOD_NAME, e);
            	log.error(METHOD_NAME, PretupsI.EXCEPTION + e);

            }
           finally {
            	if(mcomCon != null)
            	{
            		mcomCon.close(METHOD_NAME);

            	}
            }
	}
       
}
