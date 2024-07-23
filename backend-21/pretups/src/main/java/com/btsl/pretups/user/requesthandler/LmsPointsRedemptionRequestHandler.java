package com.btsl.pretups.user.requesthandler;

/**
 * @(#)LmsPointsRedemptionRequestHandler.java
 *                                            Copyright(c) 2009, Bharti Telesoft
 *                                            Ltd.
 *                                            All Rights Reserved
 *                                            ----------------------------------
 *                                            ----------------------------------
 *                                            -----------------------------
 *                                            Author Date History
 *                                            ----------------------------------
 *                                            ----------------------------------
 *                                            -----------------------------
 *                                            Brajesh Prasad 08/01/14 Initial
 *                                            Creation
 *                                            ----------------------------------
 *                                            ----------------------------------
 *                                            -----------------------------
 *                                            Controller for redeeming the
 *                                            Loyalty Points of the Channel
 *                                            User, This will get the amount of
 *                                            to be redeemed by the user and
 *                                            return will send
 *                                            the credited amount and requested
 *                                            redeemed points of the Channel
 *                                            User.
 */

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGeneratorDAO;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.LoyaltyPointsLog;
import com.btsl.pretups.loyalitystock.businesslogic.LoyalityStockTxnVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyPointsRedemptionDAO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyPointsRedemptionVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockDAO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
public class LmsPointsRedemptionRequestHandler implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(LmsPointsRedemptionRequestHandler.class.getName());
    
    private static OperatorUtilI calculatorI = null;
    private double lmsMultFactor = 0;
    private Connection con = null;
    private MComConnectionI mcomCon = null;
    private static String _notAllowedRecSendMessGatw=BTSLUtil.NullToString(Constants.getProperty("LMS_PTRED_REC_MSG_NOT_REQD_GWCODE"));
	
    private static final float EPSILON=0.0000001f;
    public void process(RequestVO p_requestVO) {
        final String obj = "LmsPointsRedemptionRequestHandler";
        final String METHOD_NAME = "process";
        try {
            String Msisdn = null;
            String pin = null;
            String CurrentLoyaltyPoints = null;
            String PreviousLoyaltyPoints = null;
            LoyaltyPointsRedemptionDAO lpRedempDAO = null;
            LoyaltyPointsRedemptionVO lpRedemptionVO = null;
            ChannelUserDAO channelUserDAO = null;
            ChannelUserVO channelUserVO = null;
            lpRedemptionVO = new LoyaltyPointsRedemptionVO();
            String requestedPoints = null;
            lpRedempDAO = new LoyaltyPointsRedemptionDAO();
            channelUserDAO = new ChannelUserDAO();
            final String[] p_requestArr = p_requestVO.getRequestMessageArray();
            Msisdn = p_requestVO.getRequestMSISDN();
            pin = null;
            final String requestGatewayType = p_requestVO.getRequestGatewayType();
            final int messageLen = p_requestArr.length;
            if (_log.isDebugEnabled()) {
                _log.debug(obj, "messageLen: " + messageLen);
            }
            lpRedemptionVO.setRedempType("STOCK");

            requestedPoints = p_requestArr[1];
            pin = p_requestArr[2];
            lpRedemptionVO.setRedempLoyaltyPointString((requestedPoints));
            lpRedemptionVO.setMsisdn(Msisdn);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			String productCode = PretupsI.PRODUCT_ETOPUP;
			if(requestGatewayType.equalsIgnoreCase("SMSC")) {
				if(messageLen>=4 && !BTSLUtil.isNullString(p_requestArr[3])){
					p_requestVO.setProductCode(p_requestArr[3]);
					productCode = channelUserDAO.product(con,p_requestArr[3]);
				} else {
					p_requestVO.setProductCode(productCode);
				}
			}else {
				if(p_requestVO.getRequestMap()!=null && !BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("PRODUCTCODE"))){
					p_requestVO.setProductCode((String)p_requestVO.getRequestMap().get("PRODUCTCODE"));
					productCode = channelUserDAO.product(con,(String)p_requestVO.getRequestMap().get("PRODUCTCODE"));
				} else {
					p_requestVO.setProductCode(productCode);
				}
			}
			
			lpRedemptionVO.setProductCode(productCode);			
			channelUserVO=channelUserDAO.loadChannelUserDetails(con, Msisdn);
            final ListValueVO listValueVO = BTSLUtil.getOptionDesc(p_requestVO.getServiceType(), ((ChannelUserVO) p_requestVO.getSenderVO()).getAssociatedServiceTypeList());
            if (listValueVO == null || BTSLUtil.isNullString(listValueVO.getLabel())) {
                _log.error("validateServiceType", p_requestVO.getRequestIDStr(), " MSISDN=" + p_requestVO.getFilteredMSISDN() + " Service Type not found in allowed List");
                throw new BTSLBaseException("LmsPointEnquiryRequestHandler", METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
            }
            channelUserVO = channelUserDAO.loadChannelUserDetails(con, Msisdn);
            
            lpRedemptionVO.setUserID(channelUserVO.getUserID());
            lpRedemptionVO.setNetworkID(channelUserVO.getNetworkID());
            final String setID = channelUserVO.getLmsProfile();
            lpRedemptionVO.setParentID(channelUserVO.getParentID());
            lpRedemptionVO = lpRedempDAO.loaduserProfileRelatedDetails(con, lpRedemptionVO);
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
			int numberOfRecords = arrayList.size();
            if (arrayList == null || numberOfRecords==0 ) {// checking
                // if
                // there
                // is
                // entry
                // for
                // the
                // user
                // in
                // the
                // bonus
                // table
                // or
                // not
                p_requestVO.setMessageCode(PretupsErrorCodesI.NO_LOYALTY_POINTS_FOR_USER);
                p_requestVO.setSuccessTxn(false);
                // throw new
                throw new BTSLBaseException(this,"LmsPointsRedemptionRequestHandler",p_requestVO.getMessageCode(),"");
            } else {
				LoyaltyPointsRedemptionVO lpRedemptionVO2 =null;
				for(int index=0;index<numberOfRecords;index++){
					lpRedemptionVO2 = arrayList.get(index);	
					if (_log.isDebugEnabled()) {
						_log.debug("LmsPointsRedemptionRequestHandler", "Product:Bonus = " + lpRedemptionVO2.getProductCode()+":"+lpRedemptionVO2.getCurrentLoyaltyPoints());
					}
					
					lpRedemptionVO.setCurrentLoyaltyPoints(lpRedemptionVO2.getCurrentLoyaltyPoints());
				}
                CurrentLoyaltyPoints = lpRedemptionVO.getCurrentLoyaltyPoints();
                if (CurrentLoyaltyPoints.equals("0")) { // checking if there are
                    // no loyalty points for
                    // the user to be
                    // redeemed
                    p_requestVO.setSuccessTxn(false);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.ZERO_LOYALTY_POINTS_FOR_USER);
                } else {
                    confirmDetails(con, lpRedemptionVO, p_requestVO);
                    if (!p_requestVO.isSuccessTxn()) {
                    	throw new BTSLBaseException(this, METHOD_NAME, p_requestVO.getMessageCode()); 
                    }
                    PreviousLoyaltyPoints = lpRedemptionVO.getCurrentLoyaltyPoints();
                    saveRedempDetails(con, lpRedemptionVO, p_requestVO);
                    if (!p_requestVO.isSuccessTxn()) {
                    	throw new BTSLBaseException(this, METHOD_NAME, p_requestVO.getMessageCode()); 
                    }
                    CurrentLoyaltyPoints = lpRedemptionVO.getCurrentLoyaltyPoints(); // getting
                    // the
                    // current
                    // loyalty
                    // points
                    // of
                    // the
                    // user
                    p_requestVO.setCurrentLoyaltyPoints(CurrentLoyaltyPoints);
                    p_requestVO.setCreditedAmount(lpRedemptionVO.getRedempLoyaltyAmount());
                    p_requestVO.setRedemptionId(lpRedemptionVO.getLmsTxnId());
                    p_requestVO.setSuccessTxn(true);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.TOTAL_LOYALTY_REDEMPTION_AND_AMOUNT);
					String [] RedemptionDetails=new String[]{CurrentLoyaltyPoints,p_requestVO.getCreditedAmount(),p_requestVO.getRedemptionId(),BTSLUtil.getMessage(p_requestVO.getLocale(),p_requestVO.getProductCode(),null)}; //setting Loyalty Points into the arguments for response message
                    // Loyalty
                    // Points
                    // into
                    // the
                    // arguments
                    // for
                    // response
                    // message
                    p_requestVO.setMessageArguments(RedemptionDetails);
                  //Added by Diwakar for sending the message
					p_requestVO.setSenderMessageRequired(false);
					if(!_notAllowedRecSendMessGatw.contains((CharSequence)requestGatewayType)){		
						String senderMessage=BTSLUtil.getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments());
						PushMessage pushMessage=new PushMessage(p_requestVO.getRequestMSISDN(),senderMessage,p_requestVO.getRequestIDStr(),p_requestVO.getRequestGatewayCode(),p_requestVO.getLocale());
						pushMessage.push();
					}
                
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            if (_log.isDebugEnabled()) {
                _log.debug("LmsPointsRedemptionRequestHandler", "be.isKey()" + be.isKey() + " be.getMessageKey()" + be.getMessageKey());
            }
            p_requestVO.setSuccessTxn(false);
            if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_MESSAGE_FORMAT))) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                            .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                p_requestVO.setMessageCode(be.getMessageKey()); // setting the
                // message code
                p_requestVO.setMessageArguments(be.getArgs());
            } else if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED)))) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
                p_requestVO.setSuccessTxn(false);
            } else if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.INVALID_REDEMP_LOYALTY_POINTS))) {
            	p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_REDEMP_LOYALTY_POINTS);
            } else if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.NOT_ENOUGH_LOYALTY_POINTS))) {
            	p_requestVO.setMessageCode(PretupsErrorCodesI.NOT_ENOUGH_LOYALTY_POINTS);
            } else if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.INVALID_AMOUNT))) {
            	p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_AMOUNT);
            } else if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.AMOUNT_ZERO))) {
            	p_requestVO.setMessageCode(PretupsErrorCodesI.AMOUNT_ZERO);
            } else if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.NO_LOYALTY_POINTS_FOR_USER))) {
            	p_requestVO.setMessageCode(PretupsErrorCodesI.NO_LOYALTY_POINTS_FOR_USER);
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
        } catch (Exception be) {
            p_requestVO.setSuccessTxn(false);
            _log.error("LmsPointsRedemptionRequestHandler", "Exception:be=" + be);
            _log.errorTrace(METHOD_NAME, be);
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("LmsPointsRedemptionRequestHandler#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("LmsPointsRedemptionRequestHandler", "Exiting");
            }
        }
    }

    private void confirmDetails(Connection p_con, LoyaltyPointsRedemptionVO lpRedemptionVO, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "confirmDetails";
        try {
            double amount;
            if (_log.isDebugEnabled()) {
                _log.debug("LmsPointsRedemptionRequestHandler", "confirmDetails" + "p_requestVO.toString()" + p_requestVO.toString() + " Message: " + p_requestVO.getMessageCode());
            }
            if (BTSLUtil.isNullString(lpRedemptionVO.getRedempLoyaltyPointString())) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REDEMP_POINTS_NULL);
                p_requestVO.setSuccessTxn(false);
                throw new BTSLBaseException(this,"confirmDetails","Null redemption Point");
            } else {
                if (!BTSLUtil.isNumeric(lpRedemptionVO.getRedempLoyaltyPointString())) {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_REDEMP_LOYALTY_POINTS);
                    p_requestVO.setSuccessTxn(false);
                    throw new BTSLBaseException(this,"confirmDetails","Invalid redemption loyality points");
                }
                if (!BTSLUtil.isDecimalValue(lpRedemptionVO.getRedempLoyaltyPointString())) {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_REDEMP_LOYALTY_POINTS);
                    p_requestVO.setSuccessTxn(false);
                    throw new BTSLBaseException(this,"confirmDetails","Invalid redemption loyality points");
                }
                if (new Double(lpRedemptionVO.getCurrentLoyaltyPoints()).intValue() < new Double(lpRedemptionVO.getRedempLoyaltyPointString()).intValue()) {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.NOT_ENOUGH_LOYALTY_POINTS);
                    p_requestVO.setSuccessTxn(false);
                    throw new BTSLBaseException(this,"confirmDetails","Not enough loyality points");
                } else if (new Double(lpRedemptionVO.getRedempLoyaltyPointString()).intValue() <= 0) {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_REDEMP_LOYALTY_POINTS);
                    p_requestVO.setSuccessTxn(false);
                    throw new BTSLBaseException(this,"confirmDetails","Invalid redemption loyality points");
                }
                lmsMultFactor = Double.parseDouble(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_MULT_FACTOR)));
                amount = (double) (lmsMultFactor * Double.parseDouble(lpRedemptionVO.getRedempLoyaltyPointString()));
                lpRedemptionVO.setRedempLoyaltyAmount(String.valueOf(amount));

                if (!BTSLUtil.isValidAmount(lpRedemptionVO.getRedempLoyaltyAmount())) {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_AMOUNT);
                    p_requestVO.setSuccessTxn(false);
                    throw new BTSLBaseException(this,"confirmDetails","Invalid amount");
                }
                if (Math.abs(amount-0)<EPSILON) {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.AMOUNT_ZERO);
                    p_requestVO.setSuccessTxn(false);
                    throw new BTSLBaseException(this,"confirmDetails","Zero Amount");

                }
            }
        } catch (Exception e) {
            _log.error("confirmDetails", "Exception:e=" + e);
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("confirmDetails", "Exiting");
            }
        }
    }

    private void saveRedempDetails(Connection p_con, LoyaltyPointsRedemptionVO lpRedemptionVO, RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("saveRedempDetails", "Entered");
        }
        final String METHOD_NAME = "saveRedempDetails";
        String redepType = null;
        final int requestedLoyaltyPoints = 0;
        Long amount = 0L;
        long updatedLoyaltyStock = 0;
        Connection con = null;
        MComConnectionI mcomCon = null;
        LoyaltyPointsRedemptionVO redempVO = null;
        LoyalityStockTxnVO loyalityStockTxnVO = null;
        LoyaltyPointsRedemptionDAO lpRedempDAO = null;
        int userLoyaltyPointUpdateCount = 0;
        final Date currentDate = new Date();
        LoyaltyVO loyaltyVO = null;

        final UserBalancesVO userVO = new UserBalancesVO();
        try {
            loyaltyVO = new LoyaltyVO();
            ChannelUserVO parentUserVO = null;
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            redepType = lpRedemptionVO.getRedempType();
            lpRedempDAO = new LoyaltyPointsRedemptionDAO();
            loyalityStockTxnVO = lpRedempDAO.checkLoyaltyStockDetails(lpRedemptionVO.getNetworkID());
            loyaltyVO.setCreatedOn(currentDate);
            loyaltyVO.setNetworkCode(lpRedemptionVO.getNetworkID());
            PretupsBL.generateLMSTransferID(loyaltyVO);
            final String redmptionTxnId = loyaltyVO.getLmstxnid();
            lpRedemptionVO.setRedemptionID(redmptionTxnId);
            if (_log.isDebugEnabled()) {
                _log.debug("saveRedempDetails", "redmptionTxnId= " + redmptionTxnId);
            }

            final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            try {
                calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[initialize]", "", "", "",
                                "Exception while loading the class at the call:" + e.getMessage());
            }
            redempVO = new LoyaltyPointsRedemptionVO();
            amount = PretupsBL.getSystemAmount(lpRedemptionVO.getRedempLoyaltyAmount());
            final int updatedUserPoints = (new Double(lpRedemptionVO.getCurrentLoyaltyPoints()).intValue()) - new Double(lpRedemptionVO.getRedempLoyaltyPointString()).intValue();
            redempVO.setRedempLoyaltyAmount(String.valueOf(amount));
            redempVO.setSetId(String.valueOf(lpRedemptionVO.getSetId()));
            redempVO.setVersion(lpRedemptionVO.getVersion());
            redempVO.setRedempLoyaltyPoint(Integer.parseInt(lpRedemptionVO.getRedempLoyaltyPointString()));
            redempVO.setPreviousLoyaltyPointsBuffer(lpRedemptionVO.getPreviousLoyaltyPoints());
            redempVO.setPreviousLoyaltyPoints(lpRedemptionVO.getCurrentLoyaltyPoints());
            redempVO.setCurrentLoyaltyPoints(String.valueOf(updatedUserPoints));
            redempVO.setRedempType(lpRedemptionVO.getRedempType());
            redempVO.setModifiedOn(currentDate);
            redempVO.setMultFactor(String.valueOf(lmsMultFactor));
            redempVO.setUserID(lpRedemptionVO.getUserID());
            redempVO.setRedemptionID(redmptionTxnId);
            redempVO.setNetworkID(lpRedemptionVO.getNetworkID());
            redempVO.setMsisdn(lpRedemptionVO.getMsisdn());
            redempVO.setProductCode(lpRedemptionVO.getProductCode());
            redempVO.setProductShortCode(lpRedemptionVO.getProductShortCode());
            redempVO.setCreatedOn(loyaltyVO.getCreatedOn());
            redempVO.setRedemptionDate(loyaltyVO.getCreatedOn());
            redempVO.setParentContribution(lpRedemptionVO.getParentContribution());
            redempVO.setParentID(lpRedemptionVO.getParentID());
            redempVO.setOperatorContribution(lpRedemptionVO.getOperatorContribution());
            lpRedemptionVO.setCurrentLoyaltyPoints(String.valueOf(updatedUserPoints));
            lpRedemptionVO.setLmsTxnId(loyaltyVO.getLmstxnid());

            if (BTSLUtil.isNullString(redempVO.getSetId()) && BTSLUtil.isNullString(redempVO.getVersion())) {
            	LoyaltyPointsLog.log("LMS", redempVO.getUserID(), redempVO.getSetId(), currentDate, "DR", redempVO.getRedemptionID(), "REDEM", "0", 0l, 0l, 0l, "206",
                                PretupsErrorCodesI.ASSOCIATED_PROFILE_NOT_ACTIVE);

                p_requestVO.setMessageCode(PretupsErrorCodesI.LMS_PROFILE_NOT_ACTIVE);
                throw new BTSLBaseException(this,METHOD_NAME,"LMS profile not active");
            }
            if (!lpRedempDAO.isUserActive(con, redempVO)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.USER_STATUS_NOTACTIVE);
                throw new BTSLBaseException(this,METHOD_NAME,"User status not active");
            }
            if (lpRedempDAO.isUserInSuspended(con, redempVO)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_SUSPENDED);
                throw new BTSLBaseException(this,METHOD_NAME,"User Suspended");
            }

            updatedLoyaltyStock = loyalityStockTxnVO.getPostStock() + requestedLoyaltyPoints;

            loyalityStockTxnVO.setModifiedBy(userVO.getUserID());
            loyalityStockTxnVO.setModifiedOn(currentDate);
            loyalityStockTxnVO.setPreviousStock(loyalityStockTxnVO.getPostStock());
            loyalityStockTxnVO.setPostStock(updatedLoyaltyStock);
            loyalityStockTxnVO.setNetworkCode(lpRedemptionVO.getNetworkID());
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
            C2sBalanceQueryVO parentBalancesVO = null;
            final NetworkDAO networkDAO = new NetworkDAO();
            NetworkVO networkVO = null;
            final NetworkStockDAO networkStockDAO = new NetworkStockDAO();
            NetworkStockVO stockVO = null;
            final ChannelTransferVO channelTransferVo = new ChannelTransferVO();
            if (redempVO.getParentContribution() != 0 && !"ROOT".equals(redempVO.getParentID())) {
                lpRedempDAO.loadLMSParentUserDetails(con, redempVO);
                final long parentCont = (Long.valueOf(redempVO.getRedempLoyaltyAmount()) * redempVO.getParentContribution()) / 100;
                final long optcont = (Long.valueOf(redempVO.getRedempLoyaltyAmount()) * redempVO.getOperatorContribution()) / 100;
                redempVO.setO2cContribution(optcont);
                redempVO.setC2cContribution(parentCont);
                final String parentMSISDN = redempVO.getParentMsisdn();
                parentUserVO = (ChannelUserVO) channelUserDAO.loadChannelUserDetails(con, parentMSISDN);
                if (parentUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND) || parentUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_DELETED) || parentUserVO
                                .getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_CANCELED)) {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_REQ_USR_PARENT_NOT_ACTIVE);
                    throw new BTSLBaseException(this,"saveRedempDetails","User parent not active");
                }
                if (parentUserVO.getOutSuspened().equalsIgnoreCase(PretupsI.YES)) {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.PARENT_SUSPENDED);
                    throw new BTSLBaseException(this,"saveRedempDetails","Parent suspended");
                }
                final ArrayList balanceList = userBalancesDAO.loadUserBalances(con, parentUserVO.getUserID());
                List<TransferProfileProductVO> profileList = new TransferProfileDAO().loadTransferProfileProductsList(p_con, parentUserVO.getTransferProfileID());
               final String product = redempVO.getProductCode();
                if ((balanceList != null) && (balanceList.size() > 0)) {
                	int balanceListSize = balanceList.size();
                    for (int k = 0, l = balanceListSize; k < l; k++) {
                        parentBalancesVO = (C2sBalanceQueryVO) balanceList.get(k);
                        if (parentBalancesVO.getProductCode().equalsIgnoreCase(product)) {
                            parentBalancesVO = null;
                            parentBalancesVO = (C2sBalanceQueryVO) balanceList.get(k);
                            int profileListSize = profileList.size();
                            for (int i = 0; i < profileListSize; i++) {
                            	TransferProfileProductVO transferProfileProdductVO = profileList.get(i);
                            	if(transferProfileProdductVO.getProductCode().equalsIgnoreCase(product) && ((parentBalancesVO.getBalance() - parentCont)<= transferProfileProdductVO.getMinResidualBalanceAsLong())){
                            		p_requestVO.setMessageCode(PretupsErrorCodesI.PARENT_NOT_ENOUGH_BALANCE);
                            		p_requestVO.setSuccessTxn(false);
                            		throw new BTSLBaseException(this,METHOD_NAME,"parent doesn't have enough balance");
								}
							}
                        }
                    }
                    
                    
                    
                    if (parentBalancesVO.getBalance() < parentCont) {
                        LoyaltyPointsLog.log("LMS", redempVO.getUserID(), redempVO.getSetId(), currentDate, "DR", redempVO.getRedemptionID(), "REDM", "0", 0l, 0l, 0l,
                                        PretupsErrorCodesI.PARENT_USER_IS_NOT_ACTIVE, "");
                        return;
                    }

                } else {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.PARENT_NOT_ENOUGH_BALANCE);
                    throw new BTSLBaseException(this,METHOD_NAME,"parent doesn't have enough balance");
                }
                // / network check
                ArrayList stocklist = new ArrayList();
                networkVO = (NetworkVO) networkDAO.loadNetwork(con, redempVO.getNetworkID());
                stocklist = networkStockDAO.loadCurrentStockList(con, networkVO.getNetworkCode(), networkVO.getNetworkCode(), networkVO.getNetworkType());
                int stocklistSize = stocklist.size();
                for (int i = 0; i < stocklistSize; i++) {
                    stockVO = (NetworkStockVO) stocklist.get(i);
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
                        if (PretupsI.FOC_WALLET_TYPE.equals(stockVO.getWalletType())) {
                            break;
                        }
                    } else {
                        if (PretupsI.SALE_WALLET_TYPE.equals(stockVO.getWalletType())) {
                            break;
                        }
                    }
                }
                if (stockVO.getWalletbalance() <= optcont) {
                    LoyaltyPointsLog.log("LMS", redempVO.getUserID(), redempVO.getSetId(), currentDate, "DR", redempVO.getRedemptionID(), "REDEM", "0", 0l, 0l, 0l,
                                    PretupsErrorCodesI.ERROR_NW_STOCK_LESS, "");
                    return;

                }
                performC2C(con, parentUserVO, redempVO, parentBalancesVO, p_requestVO);
                final String c2cTxnId = redempVO.getReferenceNo();
                if (!BTSLUtil.isNullString(c2cTxnId)) {
                    perfomFOC(con, parentUserVO, redempVO, stockVO, p_requestVO);
                }

                final String focTxnId = redempVO.getReferenceNo();
                if (!BTSLUtil.isNullString(c2cTxnId) && !BTSLUtil.isNullString(focTxnId)) {
                    if (redempVO.getOperatorContribution() == 0) {
                        redempVO.setReferenceNo(c2cTxnId);
                    }
                    long sumAmount = 0L;
                    redempVO.setTxnStatus("200");
                    sumAmount = parentCont + optcont;
                    redempVO.setSumAmount(sumAmount);
                } else {
                    p_requestVO.setSuccessTxn(false);
                }
                if ("200".equals(redempVO.getTxnStatus())) {
                    userLoyaltyPointUpdateCount = lpRedempDAO.updateUserLoyaltyPointsDetail(redempVO, redepType);
                    if (userLoyaltyPointUpdateCount > 0) {
                        redempVO.setCreatedBy(redempVO.getUserID());

                        userLoyaltyPointUpdateCount = lpRedempDAO.insertRedemtionDetails(con, redempVO);
                    }

                    if (userLoyaltyPointUpdateCount > 0) {
                      mcomCon.finalCommit();
                        LoyaltyPointsLog.log("LMS", redempVO.getUserID(), redempVO.getSetId(), currentDate, "DR", redempVO.getRedemptionID(), "REDEM", "0", (long) redempVO
                                        .getRedempLoyaltyPoint(), 0l, redempVO.getO2cContribution(), PretupsErrorCodesI.TXN_STATUS_SUCCESS, "");
                        

                    } else {
                       mcomCon.finalRollback();
                    }
                }

            } else {
                redempVO.setO2cContribution(Long.valueOf(redempVO.getRedempLoyaltyAmount()));
                ArrayList stocklist = new ArrayList();
                networkVO = (NetworkVO) networkDAO.loadNetwork(con, redempVO.getNetworkID());
                stocklist = networkStockDAO.loadCurrentStockList(con, networkVO.getNetworkCode(), networkVO.getNetworkCode(), networkVO.getNetworkType());
                stockVO = (NetworkStockVO) stocklist.get(0);
                if (stockVO.getWalletbalance() <= redempVO.getO2cContribution()) {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.INSUFFIECIENT_NETWORK_STOCK);
                    throw new BTSLBaseException(this,METHOD_NAME,"Insufficient Network Stock");

                }

                perfomFOC(con, parentUserVO, redempVO, stockVO, p_requestVO);

                lpRedemptionVO.setTransactionId(redempVO.getTransactionId());
                final String focTxnId = redempVO.getReferenceNo();
                if (!BTSLUtil.isNullString(focTxnId)) {
                    if ("200".equals(redempVO.getTxnStatus())) {
                        redempVO.setSumAmount(Long.valueOf(redempVO.getRedempLoyaltyAmount()));
                        userLoyaltyPointUpdateCount = lpRedempDAO.updateUserLoyaltyPointsDetail(redempVO, redepType);
                        if (userLoyaltyPointUpdateCount > 0) {
                            redempVO.setCreatedBy(redempVO.getUserID());
                            userLoyaltyPointUpdateCount = lpRedempDAO.insertRedemtionDetails(con, redempVO);
                        }
                        if (userLoyaltyPointUpdateCount > 0) {
                            mcomCon.finalCommit();
                            // //push
                            LoyaltyPointsLog.log("LMS", redempVO.getUserID(), redempVO.getSetId(), currentDate, "DR", redempVO.getRedemptionID(), "REDEM", "0",
                                            (long) redempVO.getRedempLoyaltyPoint(), 0l, redempVO.getO2cContribution(), PretupsErrorCodesI.TXN_STATUS_SUCCESS, "");
                            //Commented for duplicate redemption message 
                            
                        } else {
                            mcomCon.finalRollback();
                        }
                    }
                } else {
                    p_requestVO.setSuccessTxn(false);
                }
            }

            if (userLoyaltyPointUpdateCount > 0 && "200".equals(redempVO.getTxnStatus())) {
                redempVO.setRedempStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                redempVO.setErrorCode(null);
                lpRedemptionVO.setRedempStatus(PretupsI.SUCCESS);

            } else {
                redempVO.setRedempStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                p_requestVO.setSuccessTxn(false);
                p_requestVO.setMessageCode(PretupsErrorCodesI.REDEMPTION_STATUS_FAIL);
            }
           

        } catch (Exception e) {

            _log.error("saveRedempDetails", "Exception:e=" + e);
            _log.errorTrace(METHOD_NAME, e);
            return;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("LmsPointsRedemptionRequestHandler#saveRedempDetails");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("saveRedempDetails", "Exiting");
            }
        }

    }

    public void performC2C(Connection con, ChannelUserVO parentUserVO, LoyaltyPointsRedemptionVO redempVO, C2sBalanceQueryVO parentBalancesVO, RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("performC2C", "Entered");
        }
        final String METHOD_NAME = "performC2C";
        try {
            final ChannelTransferVO channelTransferVo = new ChannelTransferVO();
            final Date currentDate = new Date();
            final long requestedvalue = redempVO.getC2cContribution();
            ChannelTransferItemsVO channelTransferItemsVO = null;
            ChannelTransferItemsVO transferItemsVO = null;
            ChannelUserVO channelUserVO = new ChannelUserVO();
            ArrayList channelTransferItemVOList = new ArrayList();
            final ChannelUserTransferDAO channelUserTransferDAO = new ChannelUserTransferDAO();
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
            final String product =redempVO.getProductCode() ;
            channelTransferVo.setTransactionMode(PretupsI.AUTO_C2C_TXN_MODE);
            channelTransferVo.setNetworkCode(redempVO.getNetworkID());
            channelTransferVo.setNetworkCodeFor(redempVO.getNetworkID());
            channelTransferVo.setCreatedOn(currentDate);
            genrateChnnlToChnnlTrfID(con, channelTransferVo);
            channelTransferVo.getTransferID();
            channelTransferVo.setActiveUserId(parentUserVO.getUserID());
            channelTransferVo.setProductCode(parentBalancesVO.getProductCode());
            channelTransferVo.setFromUserID(parentUserVO.getUserID());
            channelTransferVo.setToUserID(redempVO.getUserID());
            channelTransferVo.setSenderTxnProfile(parentUserVO.getTransferProfileID());
            channelTransferVo.setFromUserCode(parentUserVO.getUserCode());
            channelUserVO = (ChannelUserVO) channelUserDAO.loadChannelUserDetails(con, redempVO.getMsisdn());
            channelTransferItemVOList = channelUserTransferDAO.parentBalanceUpdateValue(con, channelTransferVo.getFromUserID(), requestedvalue);
            if ((channelTransferItemVOList != null) && (channelTransferItemVOList.size() > 0)) {
                for (int m = 0, n = channelTransferItemVOList.size(); m < n; m++) {
                    transferItemsVO = (ChannelTransferItemsVO) channelTransferItemVOList.get(m);
                    if (transferItemsVO.getProductCode().equalsIgnoreCase(product)) {
                        transferItemsVO = null;
                        transferItemsVO = (ChannelTransferItemsVO) channelTransferItemVOList.get(m);
                    }
                }
            }
            channelTransferVo.setChannelTransferitemsVOList(channelTransferItemVOList);
            channelTransferVo.setTransferMRP(requestedvalue);
            channelTransferVo.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
            channelTransferVo.setCategoryCode(parentUserVO.getCategoryCode());
            channelTransferVo.setReceiverCategoryCode(channelUserVO.getCategoryCode());
            if (transferItemsVO != null) {
                channelTransferVo.setRequestedQuantity(Long.valueOf(transferItemsVO.getRequestedQuantity()));
            }
            int creditCount = 0;
            int debitCount = 0;
            int updateCount = 0;
            int upcount = 0;
            final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
            final UserBalancesVO userBalancesVO = constructBalanceVOFromTxnVO(channelTransferVo);
            userBalancesVO.setUserID(channelTransferVo.getFromUserID());
            upcount = userBalancesDAO.updateUserDailyBalances(con, currentDate, userBalancesVO);
            userBalancesVO.setUserID(channelTransferVo.getToUserID());
            upcount = userBalancesDAO.updateUserDailyBalances(con, currentDate, userBalancesVO);

            channelTransferVo.setReceiverTxnProfile(channelUserVO.getTransferProfileID());
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                debitCount = channelUserDAO.debitUserBalancesForMultipleWallet(con, channelTransferVo, false, null);
                channelTransferVo.setUserWalletCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN)));
                creditCount = channelUserDAO.creditUserBalancesForMultipleWallet(con, channelTransferVo, false, null);
            } else {
                debitCount = channelUserDAO.debitUserBalances(con, channelTransferVo, false, null);
                creditCount = channelUserDAO.creditUserBalances(con, channelTransferVo, false, null);
            }

            channelTransferVo.setGraphicalDomainCode(parentUserVO.getGeographicalCode());
            channelTransferVo.setDomainCode(parentUserVO.getDomainID());
            channelTransferVo.setSenderGradeCode(parentUserVO.getUserGrade());
            channelTransferVo.setReceiverGradeCode(channelUserVO.getUserGrade());
            channelTransferVo.setReferenceNum(parentUserVO.getReferenceID());
            channelTransferVo.setCommProfileSetId(redempVO.getSetId());
            channelTransferVo.setCommProfileVersion(redempVO.getVersion());
            channelTransferVo.setDualCommissionType(channelUserVO.getDualCommissionType());
            channelTransferVo.setCreatedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
            channelTransferVo.setModifiedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
            channelTransferVo.setTransferInitatedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
            channelTransferVo.setTransferDate(currentDate);
            channelTransferVo.setCreatedOn(currentDate);
            channelTransferVo.setModifiedOn(currentDate);
            channelTransferVo.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            channelTransferVo.setType(PretupsI.CHANNEL_TYPE_C2C);
            channelTransferVo.setSource(PretupsI.REQUEST_SOURCE_SYSTEM); // as
            // discussed
            // with
            // Ved
            // Sir,
            channelTransferVo.setReceiverCategoryCode(channelUserVO.getCategoryCode());
            channelTransferVo.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);

            channelTransferVo.setTransferType(PretupsI.CHANNEL_TYPE_C2C);

            channelTransferVo.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_LMS);
            channelTransferVo.setControlTransfer(PretupsI.YES);
            channelTransferVo.setToUserCode(redempVO.getMsisdn());
            channelTransferVo.setReceiverDomainCode(channelUserVO.getDomainID());
            channelTransferVo.setReceiverGgraphicalDomainCode(channelUserVO.getGeographicalCode());
            channelTransferVo.setReceiverTxnProfile(channelUserVO.getTransferProfileID());
            channelTransferVo.setTransferMRP(requestedvalue);
            channelTransferVo.setRequestedQuantity(requestedvalue);

            // for enquiry
            channelTransferItemsVO = (ChannelTransferItemsVO) channelTransferVo.getChannelTransferitemsVOList().get(0); // 23
            // dec
            if (_log.isDebugEnabled()) {
                _log.info("process", "Payable amount=" + channelTransferItemsVO.getPayableAmount());
                _log.info("process", "Net Payable amount=" + channelTransferItemsVO.getPayableAmount());
            }
            channelTransferVo.setPayableAmount(channelTransferItemsVO.getPayableAmount());
            channelTransferVo.setNetPayableAmount(channelTransferItemsVO.getNetPayableAmount());
            try {
                updateCount = channelTransferDAO.addChannelTransfer(con, channelTransferVo);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                con.rollback();

            }
            if (creditCount > 0 && debitCount > 0 && updateCount > 0 && upcount > 0) {
                con.commit();
                redempVO.setTxnStatus("200");
                redempVO.setReferenceNo(channelTransferVo.getTransferID());

            } else {
                redempVO.setTxnStatus("206");
                p_requestVO.setSuccessTxn(false);

            }

        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback();
                }

                _log.error("performC2C", "Exception " + e.getMessage());
                _log.errorTrace(METHOD_NAME, e);
            } catch (Exception e1) {
                _log.errorTrace(METHOD_NAME, e1);
            }
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("performC2C", "Exited  ID =" + redempVO.getReferenceNo());
            }

        }

    }

    public void genrateChnnlToChnnlTrfID(Connection p_con, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("genrateChnnlToChnnlTrfID", "Entered ChannelTransferVO =" + p_channelTransferVO);
        }
        final String METHOD_NAME = "genrateChnnlToChnnlTrfID";
        try {
            final long tmpId = getNextID(p_con, PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID, BTSLUtil.getFinancialYear(), p_channelTransferVO);
            p_channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(p_channelTransferVO, PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID, tmpId));

        } catch (Exception e) {
            _log.error("genrateChnnlToChnnlTrfID", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ChannelTransferBL", "genrateChnnlToChnnlTrfID", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("genrateChnnlToChnnlTrfID", "Exited  ID =" + p_channelTransferVO.getTransferID());
            }
        }

    }

    public long getNextID(Connection p_con, String p_idType, String p_year, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {
        final String METHOD_NAME = "getNextID";
        try {
            final IDGeneratorDAO _idGeneratorDAO = new IDGeneratorDAO();
            final long id = _idGeneratorDAO.getNextID(p_con, p_idType, p_year, p_channelTransferVO);
            return id;
        } finally {
            if (p_con != null) {
                try {
                    p_con.commit();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
            }
        }
    }

    public void perfomFOC(Connection con, ChannelUserVO parentUserVO, LoyaltyPointsRedemptionVO redempVO, NetworkStockVO stockVO, RequestVO p_requestVO) {
        final String METHOD_NAME = "perfomFOC";
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("perfomFOC", "Entered ChannelTransferVO ");
            }
            int creditCount = 0;

            int updateCount = 0;
            int upCount = 0;
            final String product = redempVO.getProductCode();

            final long transferAmount = redempVO.getO2cContribution();
          
            final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
            final Date currentDate = new Date();
            ChannelTransferItemsVO transferItemsVO = null;
            ChannelUserVO channelUserVO = new ChannelUserVO();
            ArrayList channelTransferItemVOList = new ArrayList();
            final ChannelUserTransferDAO channelUserTransferDAO = new ChannelUserTransferDAO();
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
            channelUserVO = (ChannelUserVO) channelUserDAO.loadChannelUserDetails(con, redempVO.getMsisdn());
            channelTransferVO.setTransactionMode(PretupsI.AUTO_FOC_TXN_MODE);
            channelTransferVO.setNetworkCode(channelUserVO.getNetworkID());
            channelTransferVO.setNetworkCodeFor(channelUserVO.getNetworkID());
            channelTransferVO.setCreatedOn(currentDate);
            genrateOprtToChnnlTrfID(con, channelTransferVO);
            channelTransferVO.getTransferID();
            channelTransferVO.setTransferDate(currentDate);
            channelTransferVO.getTransferID();
            channelTransferVO.setActiveUserId(channelUserVO.getUserID());
            channelTransferVO.setToUserID(redempVO.getUserID());
            ArrayList<ChannelTransferItemsVO> channelTransferItemVOListNew=new ArrayList();
				channelTransferItemVOList=channelUserTransferDAO.getTransferlistForAutoFOC(con, redempVO.getUserID(),transferAmount,redempVO.getProductCode());
            if ((channelTransferItemVOList != null) && (channelTransferItemVOList.size() > 0)) {
                for (int m = 0, n = channelTransferItemVOList.size(); m < n; m++) {
                    transferItemsVO = (ChannelTransferItemsVO) channelTransferItemVOList.get(m);
                    if (transferItemsVO.getProductCode().equalsIgnoreCase(product)) {
                        transferItemsVO = null;
                        transferItemsVO = (ChannelTransferItemsVO) channelTransferItemVOList.get(m);
                        channelTransferItemVOListNew.add(transferItemsVO);
                       
                    }
                }
            }
            channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOListNew);
            channelTransferVO.setTransferMRP(transferAmount);
            channelTransferVO.setReceiverCategoryCode(channelUserVO.getCategoryCode());
            channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            channelTransferVO.setType(PretupsI.TRANSFER_TYPE_FOC);
            channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
            channelTransferVO.setModifiedOn(currentDate);
            channelTransferVO.setRequestedQuantity(transferAmount);

            channelTransferVO.setWalletType(PretupsI.TRANSFER_TYPE_FOC);
            final boolean debit = true;
            int updateCount1 = -1;
            updateCount = ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(con, channelTransferVO, redempVO.getUserID(), currentDate, debit);
            if (updateCount < 1) {
                throw new BTSLBaseException("O2CDirectTransferController", "transactionApproval", PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }

            updateCount1 = ChannelTransferBL.updateNetworkStockTransactionDetails(con, channelTransferVO, redempVO.getUserID(), currentDate);
            if (updateCount1 < 1) {
                throw new BTSLBaseException("O2CDirectTransferController", "transactionApproval", PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }
            channelTransferVO.setToUserID(redempVO.getUserID());
            channelTransferVO.setReceiverTxnProfile(channelUserVO.getTransferProfileID());
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                channelTransferVO.setUserWalletCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN)));
                creditCount = channelUserDAO.creditUserBalancesForMultipleWallet(con, channelTransferVO, false, null);
            } else {
                creditCount = channelUserDAO.creditUserBalances(con, channelTransferVO, false, null);
            }

            channelTransferVO.setReceiverTxnProfile(channelUserVO.getTransferProfileID());
            channelTransferVO.setReceiverTxnProfileName(channelUserVO.getTransferProfileName());
            channelTransferVO.setTotalTax1(0);
            channelTransferVO.setTotalTax2(0);

            channelTransferVO.setTotalTax3(0);
            channelTransferVO.setRequestedQuantity(transferAmount);
            channelTransferVO.setPayableAmount(0);
            channelTransferVO.setNetPayableAmount(0);
            channelTransferVO.setPayInstrumentAmt(transferAmount);
            channelTransferVO.setTransferMRP(transferAmount);
            channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);

            channelTransferVO.setToUserName(channelUserVO.getUserName());

            channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
            channelTransferVO.setDomainCode(channelUserVO.getDomainID());
            channelTransferVO.setReceiverCategoryCode(channelUserVO.getCategoryCode());
            channelTransferVO.setCommProfileSetId(redempVO.getSetId());
            channelTransferVO.setNetworkCodeFor(channelUserVO.getNetworkID());
            channelTransferVO.setCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
            channelTransferVO.setTransferDate(currentDate);
            channelTransferVO.setCommProfileVersion(redempVO.getVersion());
            channelTransferVO.setDualCommissionType(channelUserVO.getDualCommissionType());
            channelTransferVO.setCreatedOn(currentDate);
            channelTransferVO.setModifiedOn(currentDate);
            channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
            channelTransferVO.setSource(p_requestVO.getRequestGatewayCode());

            channelTransferVO.setProductCode(redempVO.getProductShortCode());
            channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
            channelTransferVO.setType(PretupsI.TRANSFER_TYPE_O2C);
            channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_LMS);
            channelTransferVO.setControlTransfer(PretupsI.YES);
            channelTransferVO.setCommQty(0);
            channelTransferVO.setSenderDrQty(0);
            channelTransferVO.setReceiverCrQty(0);
            channelTransferVO.setWalletType(PretupsI.FOC_WALLET_TYPE);;
            channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            channelTransferVO.setCreatedBy(PretupsI.SYSTEM);
            channelTransferVO.setModifiedBy(PretupsI.SYSTEM);
            channelTransferVO.setCreatedOn(currentDate);
            channelTransferVO.setModifiedOn(currentDate);
            channelTransferVO.setToUserCode(redempVO.getMsisdn());
            final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();

            final UserBalancesVO userBalancesVO = constructBalanceVOFromTxnVO(channelTransferVO);
            redempVO.setTransactionId(channelTransferVO.getTransferID());
            userBalancesVO.setUserID(channelTransferVO.getToUserID());
            upCount = userBalancesDAO.updateUserDailyBalances(con, currentDate, userBalancesVO);
            final int count = channelTransferDAO.addChannelTransfer(con, channelTransferVO);
            if (upCount > 0 && count > 0 && updateCount1 > 0 && creditCount > 0) {
                con.commit();
                redempVO.setReferenceNo(channelTransferVO.getTransferID());
                redempVO.setTxnStatus("200");
            } else {
                con.rollback();
                redempVO.setTxnStatus("206");
                p_requestVO.setSuccessTxn(false);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            try {
                con.rollback();
            } catch (Exception e1) {
                _log.errorTrace(METHOD_NAME, e1);
            }
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("performFOC", "Exited  ID =" + redempVO.getReferenceNo());
            }

        }
    }

    public UserBalancesVO constructBalanceVOFromTxnVO(ChannelTransferVO p_channelTransferVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("constructBalanceVOFromTxnVO", "Entered:NetworkStockTxnVO=>" + p_channelTransferVO);
        }
        final UserBalancesVO userBalancesVO = new UserBalancesVO();
        userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
        userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
        userBalancesVO.setLastTransferOn(p_channelTransferVO.getTransferDate());
        if (_log.isDebugEnabled()) {
            _log.debug("constructBalanceVOFromTxnVO", "Exiting userBalancesVO=" + userBalancesVO);
        }
        return userBalancesVO;
    }

    private void genrateOprtToChnnlTrfID(Connection p_con, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("genrateOprtToChnnlTrfID", "Entered ChannelTransferVO =" + p_channelTransferVO);
        }
        final String METHOD_NAME = "genrateOprtToChnnlTrfID";
        try {

            final long tmpId = getNextID(p_con, PretupsI.CHANNEL_TRANSFER_O2C_ID, BTSLUtil.getFinancialYear(), p_channelTransferVO);
            p_channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(p_channelTransferVO, PretupsI.CHANNEL_TRANSFER_O2C_ID, tmpId));

        } catch (Exception e) {
            _log.error("genrateOprtToChnnlTrfID", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);

            throw new BTSLBaseException("ChannelTransferBL", "genrateOprtToChnnlTrfID", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("genrateOprtToChnnlTrfID", "Exited  ID =" + p_channelTransferVO.getTransferID());
            }
        }

    }
}
