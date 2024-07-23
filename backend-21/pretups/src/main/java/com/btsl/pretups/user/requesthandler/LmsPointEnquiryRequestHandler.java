package com.btsl.pretups.user.requesthandler;

/**
 * @(#)LmsPointEnquiryRequestHandler.java
 *                                        Copyright(c) 2009, Bharti Telesoft
 *                                        Ltd.
 *                                        All Rights Reserved
 *                                        --------------------------------------
 *                                        --------------------------------------
 *                                        ---------------------
 *                                        Author Date History
 *                                        --------------------------------------
 *                                        --------------------------------------
 *                                        ---------------------
 *                                        Brajesh Prasad 08/01/14 Initial
 *                                        Creation
 *                                        --------------------------------------
 *                                        --------------------------------------
 *                                        ---------------------
 *                                        Controller for getting Loyalty Points
 *                                        of the Channel User, This will send
 *                                        the Accumulated Loyalty Points of the
 *                                        Channel User.
 */

import java.sql.Connection;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyPointsRedemptionDAO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyPointsRedemptionVO;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.ProductVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class LmsPointEnquiryRequestHandler implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(LmsPointEnquiryRequestHandler.class.getName());

    private Connection con = null;
    private MComConnectionI mcomCon = null;
    private String Msisdn = null;
    private String pin = null;
    private String CurrentLoyaltyPoints = null;
    private LoyaltyPointsRedemptionDAO lpRedempDAO = null;
    private LoyaltyPointsRedemptionVO lpRedemptionVO = null;
    private ChannelUserDAO channelUserDAO = null;
    private ChannelUserVO channelUserVO = null;
    private NetworkProductDAO networkProductDAO=null;
    private static String _notAllowedRecSendMessGatw=BTSLUtil.NullToString(Constants.getProperty("LMS_PTENQ_REC_MSG_NOT_REQD_GWCODE"));
	

    public void process(RequestVO p_requestVO) {
        final String obj = "LmsPointEnquiryRequestHandler";
        final String METHOD_NAME = "process";
        try {
            lpRedempDAO = new LoyaltyPointsRedemptionDAO();
            lpRedemptionVO = new LoyaltyPointsRedemptionVO();
            channelUserDAO = new ChannelUserDAO();
            final String[] p_requestArr = p_requestVO.getRequestMessageArray();
            Msisdn = p_requestVO.getRequestMSISDN();
            pin = null;
            final String requestGatewayType = p_requestVO.getRequestGatewayType();
            final int messageLen = p_requestArr.length;

            if (_log.isDebugEnabled()) {
                _log.debug(obj, "messageLen: " + messageLen+", requestGatewayType="+requestGatewayType);
            }
            pin = p_requestArr[1];
            lpRedemptionVO.setMsisdn(Msisdn);
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            // channelUserVO=channelUserDAO.loadChannelUserDetails(con, Msisdn);
            channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            /*if (BTSLUtil.isNullString(channelUserVO.getLmsProfile())) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.LMS_PROFLIE_NOT_ASSOCIATED);
                p_requestVO.setSuccessTxn(false);
                throw new Exception();
            }*/
            // start here
            final ListValueVO listValueVO = BTSLUtil.getOptionDesc(p_requestVO.getServiceType(), ((ChannelUserVO) p_requestVO.getSenderVO()).getAssociatedServiceTypeList());
            if (listValueVO == null || BTSLUtil.isNullString(listValueVO.getLabel())) {
                _log.error("validateServiceType", p_requestVO.getRequestIDStr(), " MSISDN=" + p_requestVO.getFilteredMSISDN() + " Service Type not found in allowed List");
                throw new BTSLBaseException("LmsPointEnquiryRequestHandler", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
            }
            // end here
            lpRedemptionVO.setUserID(channelUserVO.getUserID());
			lpRedemptionVO.setUserID(channelUserVO.getUserID());
			String productCode = null;
			if((requestGatewayType.equalsIgnoreCase(PretupsI.GATEWAY_TYPE_SMSC)|| requestGatewayType.equalsIgnoreCase(PretupsI.GATEWAY_TYPE_USSD)|| requestGatewayType.equalsIgnoreCase(PretupsI.GATEWAY_TYPE_EXTGW)) && messageLen>=3 && !BTSLUtil.isNullString(p_requestArr[2])) {
				p_requestVO.setProductCode(p_requestArr[2]);
				productCode = channelUserDAO.product(con,p_requestArr[2]);
			}else if(!requestGatewayType.equalsIgnoreCase(PretupsI.GATEWAY_TYPE_SMSC) && p_requestVO.getRequestMap()!=null && !BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("PRODUCTCODE"))){
				p_requestVO.setProductCode((String)p_requestVO.getRequestMap().get("PRODUCTCODE"));
				productCode = channelUserDAO.product(con,(String)p_requestVO.getRequestMap().get("PRODUCTCODE"));
			} else {
				networkProductDAO = new NetworkProductDAO();
				ArrayList  productList = networkProductDAO.loadProductList(con,channelUserVO.getNetworkID());
				productCode="";
				for(int index = 0;index < productList.size();index++){
					ProductVO profileProductVO = null;
					profileProductVO = (ProductVO)productList.get(index);
					productCode+= "'"+profileProductVO.getProductCode()+"'";
					if(index+1<productList.size()){
						productCode+=",";
					}
				}
				if(_log.isDebugEnabled()) {
					_log.debug(obj,"productCode  =  "+productCode);
				}
			}
			lpRedemptionVO.setProductCode(productCode);
			ArrayList<LoyaltyPointsRedemptionVO> arrayList  = lpRedempDAO.loaduserProfileBonusDetails(con, lpRedemptionVO);

            if ((p_requestVO.getPinValidationRequired(true))) {
                ChannelUserBL.validatePIN(con, channelUserVO, pin); // checking
                // if the
                // pin
                // entered
                // by the
                // channel
                // user is
                // valid or
                // not
            }
			if(arrayList == null || lpRedemptionVO.getProductCode() == null){
                p_requestVO.setMessageCode(PretupsErrorCodesI.NO_LOYALTY_POINTS_FOR_USER);
                p_requestVO.setSuccessTxn(false);
                // throw new
                // BTSLBaseException(this,"LmsPointsRedemptionRequestHandler","","");
                throw new BTSLBaseException("LmsPointEnquiryRequestHandler",METHOD_NAME,"No Loyality points for user");
            } else {
					int numberOfRecords = arrayList.size();
					CurrentLoyaltyPoints="";
					for(int index=0;index<numberOfRecords;index++){
						LoyaltyPointsRedemptionVO lpRedemptionVO = arrayList.get(index);
						if (_log.isDebugEnabled()) {
							_log.debug("LmsPointEnquiryRequestHandler", "Product:Bonus = " + lpRedemptionVO.getProductCode()+":"+lpRedemptionVO.getCurrentLoyaltyPoints());
						}
						
						CurrentLoyaltyPoints+=lpRedemptionVO.getProductCode()+":"+new Double(lpRedemptionVO.getCurrentLoyaltyPoints()).intValue(); //getting the current loyalty points of the user
						if(index+1<numberOfRecords){
							CurrentLoyaltyPoints+=",";
						}
						lpRedemptionVO=null;
					}
					
					if(numberOfRecords == 0){
						String[] productCodes = lpRedemptionVO.getProductCode().split(",");
						for (int i = 0; i < productCodes.length; i++) {
							CurrentLoyaltyPoints += productCodes[i] + ":0 ";
						}
					}
                p_requestVO.setCurrentLoyaltyPoints(CurrentLoyaltyPoints);
                p_requestVO.setSuccessTxn(true);
                p_requestVO.setMessageCode(PretupsErrorCodesI.TOTAL_LOYALTY_POINTS_FOR_USER);
                final String[] LoyaltyPoints = new String[] { CurrentLoyaltyPoints }; // setting
                // Loyalty
                // Points
                // into
                // the
                // arguments
                // for
                // response
                // message
                p_requestVO.setMessageArguments(LoyaltyPoints);
              //Added by gaurav for sending the message
				p_requestVO.setSenderMessageRequired(false);
				if(!_notAllowedRecSendMessGatw.contains((CharSequence)requestGatewayType)){		
					String senderMessage=BTSLUtil.getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments());
					PushMessage pushMessage=new PushMessage(p_requestVO.getRequestMSISDN(),senderMessage,p_requestVO.getRequestIDStr(),p_requestVO.getRequestGatewayCode(),p_requestVO.getLocale());
					pushMessage.push();
				}


            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            p_requestVO.setSuccessTxn(false);
            if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_MESSAGE_FORMAT))) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                            .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)) || (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_FORCE_CHANGE_RESETPIN)))) {
                p_requestVO.setMessageCode(be.getMessageKey()); // setting the
                // message code
                p_requestVO.setMessageArguments(be.getArgs());
            } else if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED)))) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
                p_requestVO.setSuccessTxn(false);
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
        } catch (Exception be) {
            p_requestVO.setSuccessTxn(false);
            _log.error("LmsPointEnquiryRequestHandler", "Exception:be=" + be);
            _log.errorTrace(METHOD_NAME, be);
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("LmsPointEnquiryRequestHandler#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("LmsPointEnquiryRequestHandler", "Exiting");
            }
        }
        return;
    }

}
