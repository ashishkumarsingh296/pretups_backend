package com.btsl.pretups.user.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;

public class SelfChannelUserBarHandler implements ServiceKeywordControllerI {

    private static Log _log = LogFactory.getLog(SelfChannelUserBarHandler.class.getName());
    
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered Request ID" + p_requestVO.getRequestID() + " Msisdn=" + p_requestVO.getFilteredMSISDN());
        }

        Connection con = null;MComConnectionI mcomCon = null;
        
        String serviceType = p_requestVO.getServiceType();
        try {

            final String messageArr[] = p_requestVO.getRequestMessageArray();
            long userBalance = 0;
            String productCode= null;
            String parentMsisdn = (String)p_requestVO.getRequestMap().get("parentMsisdn");
            
            if(!BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("userBalance"))) {
            	userBalance = (long)Double.parseDouble((String)p_requestVO.getRequestMap().get("userBalance"));
            }
            else {
            	 throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT);
            }
            
            if(!BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("productCode"))) {
            	productCode = (String)p_requestVO.getRequestMap().get("productCode");
            }
            else {
            	 throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT);
            }
            
            
            
            if(_log.isDebugEnabled()) {
            	_log.debug(METHOD_NAME, "Parent msisdn= " + parentMsisdn + "User Balance= " + userBalance + "Product Code= " + productCode);
            }
            
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            BarredUserVO barredUserVO = new BarredUserVO();
            
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            

            if (ParserUtility.SERVICE_SELF_CUBAR.equals(serviceType) && messageArr.length != 2) {
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT);
            }

            final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            
            UserPhoneVO userPhoneVO = null;
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }
            
            if (userPhoneVO.getPinRequired().equals(PretupsI.YES) && p_requestVO.isPinValidationRequired()) {
                try {
                    ChannelUserBL.validatePIN(con, channelUserVO, messageArr[1]);
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                        con.commit();
                    }
                    throw be;
                }
            }
            
            
            

            channelUserVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(channelUserVO.getMsisdn()));
            channelUserVO.setNetworkCode(((NetworkPrefixVO) NetworkPrefixCache.getObject(channelUserVO.getMsisdnPrefix())).getNetworkCode());
            
            
            //validation on pin if barring
            if (ParserUtility.SERVICE_SELF_CUBAR.equals(serviceType) && (channelUserVO.getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)) {
                try {
                    ChannelUserBL.validatePIN(con, channelUserVO, messageArr[1]);
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                        throw be;
                    }
                }
            }
            
            
            if(_log.isDebugEnabled()) {
            	_log.debug(METHOD_NAME, "Parent id:" + channelUserVO.getParentID());
            }
            // validate parent msisdn
            if(BTSLUtil.isNullString(parentMsisdn)) {
            	if(!channelUserVO.getParentID().equals(PretupsI.ROOT_PARENT_ID)){
            		 throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT);
            	}
            }
            else {
            	String parentMsisdnDB = channelUserDAO.loadParentMsisdn(con, channelUserVO.getParentID());
            	if(!BTSLUtil.isNullString(parentMsisdnDB) && !parentMsisdnDB.equals(parentMsisdn)) {
            		throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.PARENT_MSISDN_WRONG);
            	}
            }
            
            // validate balance based on product
            if(_log.isDebugEnabled()) {
            	_log.debug(METHOD_NAME, "network code= " + channelUserVO.getNetworkCode() + " userID= " + channelUserVO.getActiveUserID());
            }

            ArrayList<UserBalancesVO> userBalances = channelUserDAO.loadUserBalances(con,channelUserVO.getNetworkID(), channelUserVO.getNetworkID(), channelUserVO.getActiveUserID());
            
            if(userBalances.size() == 0) {
            	
            	throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_MISSING_SENDER_IDENTIFICATION );
            }
            else {
            	boolean flag = false;
            	for(UserBalancesVO vo: userBalances) {
            		if(vo.getProductCode().equals(productCode)) {
            			flag = true;
            			if((long)Double.parseDouble(vo.getBalanceStr()) != userBalance){
            				
            				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.INCORRECT_USER_BALANCE );
            			}
            		}
            		
            	}
            	if(!flag) {
            		
            		throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.SOS_INVALID_PRODUCT_CODE );
            	}
            }
            

            

            

            final BarredUserDAO barredUserDAO = new BarredUserDAO();
            ArrayList<BarredUserVO> barredUserList = new ArrayList();
            int status = 0;
            if(ParserUtility.SERVICE_SELF_CUBAR.equals(serviceType)) {
            	
            	barredUserList = barredUserDAO.loadSingleBarredMsisdnDetails(con, PretupsI.C2S_MODULE, channelUserVO.getNetworkID(), p_requestVO.getFilteredMSISDN(), PretupsI.USER_TYPE_SENDER, null);
            	
            	if(barredUserList !=null && barredUserList.size() != 0) {
            		throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHANEL_USER_ALREADY_BARRED);
            	}
            	
            	
            	barredUserVO = channelprepareBarredUserVO(channelUserVO);
            	status = barredUserDAO.addBarredUser(con, barredUserVO);
                if (status > 0) {
                    con.commit();

                    p_requestVO.setMessageCode(PretupsErrorCodesI.BARRED_CHANEL_SUCCESS);
                } else {
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.BARRED_CHANEL_FAILED);
                }
            }
            else if(ParserUtility.SERVICE_SELF_CU_UNBAR.equals(serviceType)) {
            	// delete barred user
            	//BarredUserVO tempVO = channelprepareBarredUserVO(channelUserVO);
            	barredUserList = barredUserDAO.loadSingleBarredMsisdnDetails(con, PretupsI.C2S_MODULE, channelUserVO.getNetworkID(), p_requestVO.getFilteredMSISDN(), PretupsI.USER_TYPE_SENDER, null);
            	
            	if(barredUserList !=null && barredUserList.size() == 0) {
            		throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.UNBAR_FAIL);
            	}
            	barredUserVO = barredUserList.get(0);
            	
            	if(barredUserVO.getBarredType().equals(PretupsI.BARRED_TYPE_SELF) || barredUserVO.getBarredType().equals(PretupsI.BARRED_TYPE_PIN_INVALID)) {
            		
            		
            		status = barredUserDAO.deleteSingleBarredMsisdn(con, PretupsI.C2S_MODULE, channelUserVO.getNetworkID(), p_requestVO.getFilteredMSISDN(), PretupsI.USER_TYPE_SENDER, barredUserVO.getBarredType());
                    if (status > 0) {
                        con.commit();
                        p_requestVO.setMessageCode(PretupsErrorCodesI.UNBAR_SUCCESS);
                    } else {
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.UNBAR_FAIL);
                    }
            	}
            	else {
            		throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.UNBAR_FAIL );
            	}
            	

            }
            



        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            p_requestVO.setSuccessTxn(false);
            _log.error(METHOD_NAME, "BTSLBaseException while self barring of=" + p_requestVO.getFilteredMSISDN() + " getting Exception =" + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.BARRED_CHANEL_FAILED);
            }

        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            p_requestVO.setSuccessTxn(false);
            _log.error(METHOD_NAME, "Exception while self barring of=" + p_requestVO.getFilteredMSISDN() + " getting Exception =" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfChannelUserBarHandler[process]", p_requestVO
                            .getFilteredMSISDN(), "", "", "Exception while self barring:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("SelfChannelUserBarHandler#process");
        		mcomCon=null;
        		}
        	
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Exited ");
            }
        }
    }

    /**
     * Method to prepare the VO for barring the user
     * 
     * @param p_channelUserVO
     * @param p_barredType
     * @param p_userType
     * @param p_reason
     * @param p_createdBy
     * @param p_module
     * @return BarredUserVO
     */
    private static BarredUserVO channelprepareBarredUserVO(ChannelUserVO p_channelUserVO) {

        if (_log.isDebugEnabled()) {
            _log.debug("channelprepareBarredUserVO", " Entered MSISDN=" + p_channelUserVO.getMsisdn());
        }

        final Date curDate = new Date();
        final BarredUserVO barredUserVO = new BarredUserVO();
        barredUserVO.setModule(PretupsI.C2S_MODULE);
        barredUserVO.setMsisdn(p_channelUserVO.getMsisdn());
        barredUserVO.setBarredType(PretupsI.BARRED_TYPE_SELF);
        barredUserVO.setCreatedBy(PretupsI.SYSTEM_USER);
        barredUserVO.setCreatedOn(curDate);
        barredUserVO.setNetworkCode(p_channelUserVO.getNetworkCode());
        barredUserVO.setModifiedBy(PretupsI.SYSTEM_USER);
        barredUserVO.setModifiedOn(curDate);
        barredUserVO.setUserType(PretupsI.BARRED_USER_TYPE_SENDER);
        barredUserVO.setBarredReason(PretupsI.BARRED_SUBSCRIBER_SELF_RSN);

        if (_log.isDebugEnabled()) {
            _log.debug("channelprepareBarredUserVO", "Exited ");
        }

        return barredUserVO;
    }
}
