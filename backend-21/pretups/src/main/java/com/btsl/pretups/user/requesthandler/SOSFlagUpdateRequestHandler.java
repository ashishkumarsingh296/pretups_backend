package com.btsl.pretups.user.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

public class SOSFlagUpdateRequestHandler implements ServiceKeywordControllerI {
    private Log log = LogFactory.getLog(SOSFlagUpdateRequestHandler.class.getName());
     static final String METHOD_NAME = "process";
    
    public void process(RequestVO requestVO)
    {
    	StringBuilder loggerValue= new StringBuilder();
    	if (log.isDebugEnabled()) {
            loggerValue.setLength(0);
        	loggerValue.append(" Entered " );
        	loggerValue.append(requestVO);
        	loggerValue.append(" messageLen=" );
        	loggerValue.append(requestVO.getRequestMessageArray().length);
        	log.debug(METHOD_NAME, loggerValue);
        }
    	 	
    	Connection con = null;
        MComConnectionI mcomCon = null;
        String externalRefNum = null;
        String sosTxnID = null;
        String sosAllowed =null;
        long sosAllowedAmount =0;
        long sosThresholdAmount =0;
        ChannelUserDAO channelUserDAO = null;
        ChannelUserWebDAO channelUserWebDAO =null;
        String channelUserMsisdn = null;
        ChannelTransferDAO channelTransferDAO = null;
		ChannelTransferVO channelTransferVO=  null;
		UserTransferCountsDAO userTransferCountsDAO = null;
		int updateCountUTC =0;
		int updateCountChnlTrans =0;
		
        
        try {
            final ChannelUserVO channelUserVO = (ChannelUserVO) requestVO.getSenderVO();
            UserPhoneVO userPhoneVO = null;
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }
            
            final String[]  messageArr = requestVO.getRequestMessageArray();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            
      
            if (userPhoneVO.getPinRequired().equals(PretupsI.YES) && requestVO.isPinValidationRequired()) {
                try {
                    ChannelUserBL.validatePIN(con, channelUserVO, messageArr[messageArr.length-1]);
                } catch (BTSLBaseException be) {
                    log.errorTrace(METHOD_NAME, be);
                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                        con.commit();
                    }
                    throw be;
                }
            }
            
            channelUserDAO = new ChannelUserDAO();
            channelUserWebDAO= new ChannelUserWebDAO();
            channelUserMsisdn = messageArr[1];
            sosAllowed =messageArr[2];
            sosAllowedAmount =PretupsBL.getSystemAmount(Double.parseDouble(messageArr[3]));
            sosThresholdAmount =PretupsBL.getSystemAmount(Double.parseDouble(messageArr[4]));
            sosTxnID = messageArr[5];
            externalRefNum = requestVO.getExternalReferenceNum();
            
            if (log.isDebugEnabled()) {
                loggerValue.setLength(0);
            	loggerValue.append(" sosTxnID=" );
            	loggerValue.append(sosTxnID);
            	loggerValue.append(" externalRefNum=");
            	loggerValue.append(externalRefNum);
            	log.debug(METHOD_NAME, loggerValue);
            }
            
            ChannelUserVO userVO= channelUserDAO.loadChannelUserDetails(con, channelUserMsisdn);
            if(userVO != null)
            {
            	userVO.setSosAllowed(sosAllowed);
            	userVO.setSosAllowedAmount(sosAllowedAmount);
            	userVO.setSosThresholdLimit(sosThresholdAmount);
            	userVO.setLastSosTransactionId(sosTxnID);
            	userVO.setChannelUserID(userVO.getUserID());
            	int updateCount = channelUserWebDAO.sosUpdate(con, userVO);
            	if(PretupsI.NO.equals(userVO.getSosAllowed()) && updateCount>0) {
            		  mcomCon.partialCommit();
            	   	  requestVO.setMessageCode(PretupsErrorCodesI.SOS_DISABLE_SUCCESS);
                }
            	else if(PretupsI.YES.equals(userVO.getSosAllowed()) && updateCount>0) {
            		channelTransferDAO = new ChannelTransferDAO();
            		channelTransferVO=  new ChannelTransferVO();
            		userTransferCountsDAO = new UserTransferCountsDAO();

            		channelTransferVO.setTransferID(userVO.getLastSosTransactionId());
            		channelTransferVO.setNetworkCode(requestVO.getExternalNetworkCode());
            		channelTransferVO.setNetworkCodeFor(requestVO.getExternalNetworkCode());

            		channelTransferDAO.loadChannelTransfersVO(con, channelTransferVO);

            		channelTransferVO.setSosProductCode(channelTransferVO.getProductCode());
            		channelTransferVO.setTransactionMode(PretupsI.SOS_TRANSACTION_MODE);
            		channelTransferVO.setSosStatus(PretupsI.SOS_PENDING_STATUS);
            		channelTransferVO.setSosFlag(true);

            		updateCountUTC = userTransferCountsDAO.updateUserTransferCountsforSOS(con,channelTransferVO,channelTransferVO.getToUserID());

            		if (updateCountUTC>0) {

            			updateCountChnlTrans = new ChannelTransferDAO().sosUpdateChannelTransfer(con, userVO.getLastSosTransactionId(), requestVO.getActiverUserId(), channelTransferVO.getToUserID(),null,requestVO.getNetworkCode());
            		}
            	}
            
            	else if(updateCount<0)
            	  {
            		  mcomCon.finalRollback();
            		  requestVO.setMessageCode(PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
            	  }
            	if (updateCount>0 || updateCountUTC>0 || updateCountChnlTrans > 0 ) {

            		if(mcomCon != null)
            		{
            			mcomCon.finalCommit();

            		}
            		requestVO.setSuccessTxn(true);
            		if(PretupsI.YES.equals(sosAllowed)) {

            			requestVO.setMessageCode(PretupsErrorCodesI.SOS_ENABLE_SUCCESS);
            			requestVO.setMessageArguments(new String[]{PretupsBL.getDisplayAmount(sosThresholdAmount),PretupsBL.getDisplayAmount(sosAllowedAmount)});
            		}
            		else if (PretupsI.NO.equals(sosAllowed))
            			requestVO.setMessageCode(PretupsErrorCodesI.SOS_DISABLE_SUCCESS);
            	}
            	 else {
            		 mcomCon.finalRollback();
            		 requestVO.setMessageCode(PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
            	 }
            	
            }
            else
            	throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.SOS_MSISDN_DETAILS_NOT_FOUND,0,new String[]{channelUserMsisdn},null);
            
            
        }catch(Exception e) {
   		 try {
			mcomCon.finalRollback();
			requestVO.setMessageCode(PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
		} catch (SQLException e1) {
			log.trace(METHOD_NAME, e1.getMessage());
		}
   		 
   	    log.errorTrace(METHOD_NAME, e);
        log.error(METHOD_NAME, "Exception e: " + e);
        
            loggerValue.setLength(0);
        	loggerValue.append("Exception e: ");
        	loggerValue.append(e);
        	log.error(METHOD_NAME, loggerValue);
        
        requestVO.setMessageCode(e.getMessage());

        }
       finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("SOSFlagUpdateRequestHandler#process");

        	}
        }
      
    }
    

}
