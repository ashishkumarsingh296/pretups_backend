/**
 * @(#)CommCalculatorController.java
 * 
 *                                   Controller for retailer mobile app
 *                                   commission calculator(base and additional
 *                                   commission)
 */

package com.btsl.pretups.channel.transfer.requesthandler;

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
import com.btsl.pretups.adjustments.businesslogic.AdjustmentsVO;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.NetworkProductVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.txn.pretups.channel.profile.businesslogic.CommissionProfileTxnDAO;

public class CommCalculatorController implements ServiceKeywordControllerI {
    private final static Log _log = LogFactory.getLog(CommCalculatorController.class.getName());
    private AdjustmentsVO _adjustmentVODebit = new AdjustmentsVO();
    private static final float EPSILON=0.0000001f;
    private static OperatorUtilI calculatorI = null;
    static {
        final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("staticblock", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommCalculatorController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
     * 
     * @param p_requestVO
     *            RequestVO
     * @see com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI#process(RequestVO)
     */

    @Override
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        final String[] _requestMessageArray = p_requestVO.getRequestMessageArray();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered for RequestID:" + p_requestVO.getRequestID());
        }
        Connection con = null;MComConnectionI mcomCon = null;
        try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            final String _msisdn = p_requestVO.getRequestMSISDN();
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            ChannelUserVO channelUserVO = new ChannelUserVO();

            channelUserVO = channelUserDAO.loadChannelUserDetails(con, _msisdn);

            String commProfileSetId = null;
            commProfileSetId = channelUserVO.getCommissionProfileSetID();
            final Date p_currentDate = new Date();
            final CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();
            final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
            CommissionProfileSetVO commissionProfileSetVO = commissionProfileTxnDAO.loadCommProfileSetDetails(con, commProfileSetId, p_currentDate);
            final String latestCommProfileVersion = commissionProfileSetVO.getCommProfileVersion();

            if (_requestMessageArray[2].equalsIgnoreCase(PretupsI.TRANSFER_TYPE_O2C) || _requestMessageArray[2].equalsIgnoreCase(PretupsI.TRANSFER_TYPE_C2C)) {
                ArrayList<ChannelTransferItemsVO> itemsList = new ArrayList<ChannelTransferItemsVO>();
                ChannelTransferItemsVO channelTransferItemsVO = new ChannelTransferItemsVO();

                final NetworkProductDAO networkProductDAO = new NetworkProductDAO();
                final ArrayList<NetworkProductVO> prodList = networkProductDAO.loadProductListForXfr(con, null, p_requestVO.getRequestNetworkCode());
                int prodListsizes=prodList.size();
                for (int i = 0; i <prodListsizes ; i++) {
                    final NetworkProductVO networkProductVO = prodList.get(i);
                    if (networkProductVO.getStatus().equals(PretupsI.STATUS_ACTIVE)) {
                        channelTransferItemsVO = new ChannelTransferItemsVO();
                        channelTransferItemsVO.setProductType(networkProductVO.getProductType());
                        channelTransferItemsVO.setProductCode(networkProductVO.getProductCode());
                        channelTransferItemsVO.setProductName(networkProductVO.getProductName());
                        channelTransferItemsVO.setShortName(networkProductVO.getShortName());
                        channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
                        channelTransferItemsVO.setProductCategory(networkProductVO.getProductCategory());
                        channelTransferItemsVO.setErpProductCode(networkProductVO.getErpProductCode());
                        channelTransferItemsVO.setStatus(networkProductVO.getStatus());
                        channelTransferItemsVO.setUnitValue(networkProductVO.getUnitValue());
                        channelTransferItemsVO.setModuleCode(networkProductVO.getModuleCode());
                        channelTransferItemsVO.setProductUsage(networkProductVO.getProductUsage());
                        channelTransferItemsVO.setRequestedQuantity(_requestMessageArray[3]);
                        channelTransferItemsVO.setProductCode(_requestMessageArray[5]);
                        itemsList.add(channelTransferItemsVO);
                    }
                }
              
                String type = (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue())?_requestMessageArray[2]:PretupsI.ALL;
        		String paymentMode = PretupsI.ALL;
                itemsList = commissionProfileDAO.loadProductListWithTaxes(con, commProfileSetId, latestCommProfileVersion, itemsList, type, paymentMode);

                if (_requestMessageArray[2].equalsIgnoreCase(PretupsI.TRANSFER_TYPE_O2C)) {
                    try {
                        ChannelTransferBL.calculateMRPWithTaxAndDiscount(itemsList, PretupsI.TRANSFER_TYPE_O2C);
                        final String commRate = Double.toString(channelTransferItemsVO.getCommRate());
                        final String commValue = PretupsBL.getDisplayAmount(channelTransferItemsVO.getCommValue());
                        final String tax3Rate = Double.toString(channelTransferItemsVO.getTax3Rate());
                        final String tax3Value = PretupsBL.getDisplayAmount(channelTransferItemsVO.getTax3Value());
                        final long netcomm = channelTransferItemsVO.getCommValue() - channelTransferItemsVO.getTax3Value();
                        final String netComm = PretupsBL.getDisplayAmount(netcomm);

                        final String msgarg[] = { commRate.trim(), commValue.trim(), tax3Rate.trim(), tax3Value.trim(), netComm.trim() };
                        p_requestVO.setMessageArguments(msgarg);
                        p_requestVO.setMessageCode(PretupsErrorCodesI.MAPP_BASE_COMMISSION);
                    } catch (BTSLBaseException be) {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.MAPP_BASE_COMMISSION_OUT_OF_RANGE);
                        _log.errorTrace(METHOD_NAME, be);
                    }
                } else {
                    try {
                        ChannelTransferBL.calculateMRPWithTaxAndDiscount(itemsList, PretupsI.TRANSFER_TYPE_C2C);
                        final String commRate = Double.toString(channelTransferItemsVO.getCommRate());
                        final String commValue = PretupsBL.getDisplayAmount(channelTransferItemsVO.getCommValue());
                        final String tax3Rate = Double.toString(channelTransferItemsVO.getTax3Rate());
                        final String tax3Value = PretupsBL.getDisplayAmount(channelTransferItemsVO.getTax3Value());
                        final long netcomm = channelTransferItemsVO.getCommValue() - channelTransferItemsVO.getTax3Value();
                        final String netComm = PretupsBL.getDisplayAmount(netcomm);

                        final String msgarg[] = { commRate.trim(), commValue.trim(), tax3Rate.trim(), tax3Value.trim(), netComm.trim() };
                        p_requestVO.setMessageArguments(msgarg);
                        p_requestVO.setMessageCode(PretupsErrorCodesI.MAPP_BASE_COMMISSION);
                    } catch (BTSLBaseException be) {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.MAPP_BASE_COMMISSION_OUT_OF_RANGE);
                        _log.errorTrace(METHOD_NAME, be);
                    }
                }
              
            }

            if (_requestMessageArray[2].equalsIgnoreCase(PretupsI.TRANSFER_TYPE_C2S)) {
                final AdditionalProfileDeatilsVO additionalProfileDetailsVO = commissionProfileTxnDAO.loadAdditionCommissionDetailsForCalculator(con, null, commProfileSetId,
                    Long.parseLong(_requestMessageArray[3]), p_currentDate, _requestMessageArray[6], null, _requestMessageArray[7]);
                if (additionalProfileDetailsVO != null) {
                    calculateAdditionalCommission(additionalProfileDetailsVO, Long.parseLong(_requestMessageArray[3]));
                    final String addCommRate = Double.toString(additionalProfileDetailsVO.getAddCommRate());
                    final String addCommValue = Double.toString(_adjustmentVODebit.getMarginAmount());
                    final String tax2Rate = Double.toString(additionalProfileDetailsVO.getTax2Rate());
                    final long nettax = _adjustmentVODebit.getTax2Value() + _adjustmentVODebit.getTax1Value();
                    final String netTax = Long.toString(nettax);
                    final long netcomm = _adjustmentVODebit.getTransferValue();

                    final String msgarg[] = { addCommRate.trim(), addCommValue.trim(), tax2Rate.trim(), netTax.trim(), Long.toString(netcomm).trim() };
                    p_requestVO.setMessageArguments(msgarg);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.MAPP_ADDITIONAL_COMMISSION);
                } else {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.MAPP_ADDITIONAL_COMMISSION_FAILED);
                }
            }
        }

        catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);

        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, "Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommCalculatorController[process]", "", "", "",
                "Exception:" + e.getMessage());
        } finally {
			if (mcomCon != null) {
				mcomCon.close("CommCalculatorController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exited");
            }
        }

    }

    /**
     * Added for calculating additional Commission in retailer app
     * Method calculateAdditionalCommission
     * Calculates the value to be transferred to the user after taxes etc
     * 
     * @param p_additionalProfileDetailsVO
     * @param p_requestAmount
     * 
     * */

    private void calculateAdditionalCommission(AdditionalProfileDeatilsVO p_additionalProfileDetailsVO, long p_requestAmount) throws BTSLBaseException {
        final String METHOD_NAME = "calculateAdditionalCommission";
        StringBuilder loggerValue= new StringBuilder(); 

        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered with p_requestAmount :");
        	loggerValue.append(p_requestAmount);
            _log.debug(METHOD_NAME,  loggerValue);
        }
        try {
            long amountTemp = 0;
            long calculatedTax1Value = 0;
            long calculatedTax2Value = 0;
            long transferValue = 0;
            amountTemp = calculatorI.calculateDifferentialComm(p_additionalProfileDetailsVO.getAddCommType(), p_additionalProfileDetailsVO.getAddCommRate(), p_requestAmount);
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append(" margin amt :");
            	loggerValue.append(amountTemp);
                _log.debug(METHOD_NAME,  loggerValue);
            }

            if (Math.abs(p_additionalProfileDetailsVO.getDiffrentialFactor()-0)<EPSILON) {
            	loggerValue.setLength(0);
            	loggerValue.append(" Differential factor: multipleFactor:");
            	loggerValue.append(p_additionalProfileDetailsVO.getDiffrentialFactor());
                _log.error("calculateAdditionalCommission:  ", loggerValue );
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DiffCalBL[calculateAdditionalCommission]", "", "",
                    "", "Differential factor: multipleFactor cannot be zero");
                throw new BTSLBaseException(this, "calculateAdditionalCommission", PretupsErrorCodesI.ERR_DIFF_FACTOR_CANNOT_BE_ZERO);
            }
            final long afterMultipleFact = BTSLUtil.parseDoubleToLong(amountTemp * p_additionalProfileDetailsVO.getDiffrentialFactor());

            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("afterMultipleFact :" );
            	loggerValue.append(afterMultipleFact);
                _log.debug(METHOD_NAME, loggerValue );
            }

           
            _adjustmentVODebit.setMarginAmount(afterMultipleFact);

            calculatedTax1Value = calculatorI.calculateDifferentialTax1(p_additionalProfileDetailsVO.getTax1Type(), p_additionalProfileDetailsVO.getTax1Rate(),
                afterMultipleFact, p_requestAmount);

            _adjustmentVODebit.setTax1Value(calculatedTax1Value);
          

            if (p_additionalProfileDetailsVO.getTax2Type().equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT)) {
                calculatedTax2Value = BTSLUtil.parseDoubleToLong(p_additionalProfileDetailsVO.getTax2Rate());
            } else // If percentage
            {
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TAX2_ON_TAX1_CODE))).booleanValue()) {
                    calculatedTax2Value = calculatorI.calculateDifferentialTax2(p_additionalProfileDetailsVO.getTax2Type(), p_additionalProfileDetailsVO.getTax2Rate(),
                        calculatedTax1Value);
                } else {
                    calculatedTax2Value = calculatorI.calculateDifferentialTax2(p_additionalProfileDetailsVO.getTax2Type(), p_additionalProfileDetailsVO.getTax2Rate(),
                        afterMultipleFact);
                }
            }
            _adjustmentVODebit.setTax2Value(calculatedTax2Value);
           

            transferValue = calculatorI.calculateDifferentialTransferValue(p_requestAmount, afterMultipleFact, calculatedTax1Value, calculatedTax2Value);
        
            _adjustmentVODebit.setTransferValue(transferValue);

        }

        catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);

        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception :" );
        	loggerValue.append(e.getMessage());
            _log.error(METHOD_NAME,loggerValue );
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append( e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "CommCalculatorController[calculateAdditionalCommission]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "calculateAdditionalCommission", PretupsErrorCodesI.ERROR_EXCEPTION);
        }

    }

}