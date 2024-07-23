/**
 * @(#)LoyaltyBL.java
 *                    Copyright(c) 2005, Bharti Telesoft Ltd.
 *                    All Rights Reserved
 * 
 *                    <description>
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    rakesh.sinha Dec,2013 Initital Creation
 *                    Vibhu Trehan Jan,2014 Modification & customization
 * 
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 */

package com.btsl.pretups.loyaltymgmt.businesslogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.LoyaltyPointsLog;
import com.btsl.pretups.loyaltymgmt.transfer.requesthandler.LoyaltyController;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class LoyaltyBL {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private LoyaltyController _loyaltyController = new LoyaltyController();
    private Connection con = null;
    private MComConnectionI mcomCon = null;

    public void distributeLoyaltyPoints(String p_module, String p_transactionid, LoyaltyVO p_loyaltyVO) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("distributeLoyaltyPoints Method", "Entered For TransactionID: " + p_transactionid);
        }
        final String METHOD_NAME = "distributeLoyaltyPoints";
        PromotionDetailsVO promotionDetailsVO = null;
        PromotionDetailsVO promotionDetailsVO2 = null;
        PromotionDetailsVO promotionDetailsVO3 = null;
        p_loyaltyVO.setTxnStatus(PretupsI.SUCCESS);
        try {
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
            // generating a transfer ID
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_STOCK_REQUIRED))).booleanValue()) {
                PretupsBL.generateLMSTransferID(p_loyaltyVO);
            }
            final String setId = p_loyaltyVO.getSetId();
            final String serviceType = p_loyaltyVO.getServiceType();
            final String receiverSetId = p_loyaltyVO.getToSetId();
            final long txnAmount = p_loyaltyVO.getTransferamt();
            promotionDetailsVO = _loyaltyController.loadProfile(con, setId);
            // check whether the promotion belongs to promotion_type=LYTPT. If
            // not, then exit.
            if (promotionDetailsVO != null) {
                p_loyaltyVO.setNetworkCode(promotionDetailsVO.getNetworkCode());
                if (promotionDetailsVO.getPromotionType().equalsIgnoreCase(PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT)) {

                    // For LMS Profile Details are loaded here
                    promotionDetailsVO = _loyaltyController.loadLMSProfileAndVersion(con, serviceType, setId, txnAmount);

				if(promotionDetailsVO==null || !p_loyaltyVO.getProductCode().equalsIgnoreCase(promotionDetailsVO.getProductCode())){

                        p_loyaltyVO.setTxnStatus(PretupsErrorCodesI.FAILED);
                        p_loyaltyVO.setErrorode(PretupsErrorCodesI.LOYALTY_PROMONOT_DEFINED);
                        throw new  BTSLBaseException(this,"distributeLoyaltyPoints",PretupsErrorCodesI.LOYALTY_PROMONOT_DEFINED);
                    } else {
                        promotionDetailsVO.setPromotionType(PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT);
                        p_loyaltyVO.setVersion(promotionDetailsVO.getVersion());
                    }
                    // brajesh
                    // for the toSetid-(For receiver)
                    if (!BTSLUtil.isNullString(p_loyaltyVO.getToSetId())) {
                        promotionDetailsVO3 = _loyaltyController.loadProfile(con, receiverSetId);
                        if (promotionDetailsVO3 != null) {
                            if (promotionDetailsVO3.getPromotionType().equalsIgnoreCase(PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT)) {
                                // For LMS Profile Details are loaded here
                                promotionDetailsVO2 = _loyaltyController.loadLMSProfileAndVersion(con, serviceType, receiverSetId, txnAmount);
								if(promotionDetailsVO2==null  || !p_loyaltyVO.getProductCode().equalsIgnoreCase(promotionDetailsVO2.getProductCode())){

                                    p_loyaltyVO.setTxnStatus(PretupsErrorCodesI.FAILED);
                                    p_loyaltyVO.setErrorode(PretupsErrorCodesI.LOYALTY_PROMONOT_DEFINED);
                                    throw new  BTSLBaseException(this,"distributeLoyaltyPoints",PretupsErrorCodesI.LOYALTY_PROMONOT_DEFINED);
                                } else {
                                    promotionDetailsVO2.setPromotionType(promotionDetailsVO3.getPromotionType());
                                    promotionDetailsVO2.setSetName(promotionDetailsVO3.getSetName());
                                    promotionDetailsVO2.setNetworkCode(promotionDetailsVO3.getNetworkCode());

                                }
                            }
                        }

                    }

                    if (p_module.equalsIgnoreCase(PretupsI.C2S_MODULE) && promotionDetailsVO != null) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("distributeLoyaltyPoints Method", "Entered for TransactionID : " + p_transactionid + " ServiceType : " + p_loyaltyVO.getServiceType());
                        }
                        _loyaltyController.DistributeLoyaltyPointsForC2SService(con, p_loyaltyVO, promotionDetailsVO);
                        if (_log.isDebugEnabled()) {
                            _log.debug("distributeLoyaltyPoints Method", "Exit...for Transaction ID : " + p_transactionid + " Service Type : " + p_loyaltyVO.getServiceType());
                        }
                        LoyaltyPointsLog.log("TXN", p_loyaltyVO.getUserid(), p_loyaltyVO.getSetId(), new Date(), PretupsI.CREDIT, p_transactionid, PretupsI.C2S_MODULE,
                                        p_loyaltyVO.getServiceType(), p_loyaltyVO.getTotalCrLoyaltyPoint(), 0L, 0L, "200", "");
                    } else if (p_module.equalsIgnoreCase(PretupsI.CHANNEL_TYPE_C2C)) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("distributeLoyaltyPoints Method", "Entered for TransactionID : " + p_transactionid + " ServiceType : " + p_loyaltyVO.getServiceType());
                        }
                        _loyaltyController.DistributeLoyaltyPointsForC2CService(con, p_loyaltyVO, promotionDetailsVO, promotionDetailsVO2);
                        if (_log.isDebugEnabled()) {
                            _log.debug("distributeLoyaltyPoints Method", "Exitfor Transaction ID : " + p_transactionid + " ServiceType : " + p_loyaltyVO.getServiceType());
                        }
                        LoyaltyPointsLog.log("TXN", p_loyaltyVO.getFromuserId(), p_loyaltyVO.getSetId(), new Date(), PretupsI.CREDIT, p_transactionid, PretupsI.C2C_MODULE,
                                        p_loyaltyVO.getServiceType(), p_loyaltyVO.getTotalCrLoyaltyPoint(), 0L, 0L, "200", "");
                        LoyaltyPointsLog.log("TXN", p_loyaltyVO.getTouserId(), p_loyaltyVO.getToSetId(), new Date(), PretupsI.CREDIT, p_transactionid, PretupsI.C2C_MODULE,
                                        p_loyaltyVO.getServiceType(), p_loyaltyVO.getTotalCrLoyaltyPoint(), 0L, 0L, "200", "");
                    } else if (p_module.equalsIgnoreCase(PretupsI.CHANNEL_TYPE_O2C) && promotionDetailsVO != null) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("distributeLoyaltyPoints Method", "Entered for TransactionID : " + p_transactionid + " ServiceType : " + p_loyaltyVO.getServiceType());
                        }
                        _loyaltyController.DistributeLoyaltyPointsForO2CService(con, p_loyaltyVO, promotionDetailsVO);
                        if (_log.isDebugEnabled()) {
                            _log.debug("distributeLoyaltyPoints Method", "Exit for Transaction ID : " + p_transactionid + " ServiceType : " + p_loyaltyVO.getServiceType());
                        }
                        LoyaltyPointsLog.log("TXN", p_loyaltyVO.getUserid(), p_loyaltyVO.getSetId(), new Date(), PretupsI.CREDIT, p_transactionid, PretupsI.O2C_MODULE,
                                        p_loyaltyVO.getServiceType(), p_loyaltyVO.getTotalCrLoyaltyPoint(), 0L, 0L, "200", "");
                    }
                    try {
                        con.commit();
                    } catch (Exception bex) {
                        _log.errorTrace(METHOD_NAME, bex);
                        p_loyaltyVO.setErrorode(PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
                        // Logger-start
                        if (p_module.equalsIgnoreCase(PretupsI.C2S_MODULE)) {
                            LoyaltyPointsLog.log("TXN", p_loyaltyVO.getUserid(), p_loyaltyVO.getSetId(), new Date(), PretupsI.CREDIT, p_transactionid, PretupsI.C2S_MODULE,
                                            p_loyaltyVO.getServiceType(), p_loyaltyVO.getTotalCrLoyaltyPoint(), 0L, 0L, "206", p_loyaltyVO.getErrorode());
                        }
                        if (p_module.equalsIgnoreCase(PretupsI.O2C_MODULE)) {
                            LoyaltyPointsLog.log("TXN", p_loyaltyVO.getUserid(), p_loyaltyVO.getSetId(), new Date(), PretupsI.CREDIT, p_transactionid, PretupsI.O2C_MODULE,
                                            p_loyaltyVO.getServiceType(), p_loyaltyVO.getTotalCrLoyaltyPoint(), 0L, 0L, "206", p_loyaltyVO.getErrorode());
                        }
                        if (p_module.equalsIgnoreCase(PretupsI.C2C_MODULE)) {
                            LoyaltyPointsLog.log("TXN", p_loyaltyVO.getFromuserId(), p_loyaltyVO.getSetId(), new Date(), PretupsI.CREDIT, p_transactionid,
                                            PretupsI.C2C_MODULE, p_loyaltyVO.getServiceType(), p_loyaltyVO.getTotalCrLoyaltyPoint(), 0L, 0L, "206", p_loyaltyVO.getErrorode());
                            LoyaltyPointsLog.log("TXN", p_loyaltyVO.getTouserId(), p_loyaltyVO.getToSetId(), new Date(), PretupsI.CREDIT, p_transactionid,
                                            PretupsI.C2C_MODULE, p_loyaltyVO.getServiceType(), p_loyaltyVO.getTotalCrLoyaltyPoint(), 0L, 0L, "206", p_loyaltyVO.getErrorode());
                        }
                        // Logger-end
                        throw new BTSLBaseException("distributeLoyaltyPoints", "process", PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
                    }
                    try {
                        if (con != null) {
                            con.rollback();
                        }
                    } catch (SQLException e) {
                        _log.errorTrace(METHOD_NAME, e);
                    }
                }
                // else condition is only valid for c2c. Where Receiver's
                // Promotion is selected as LoyaltyPoints.
                else {
                    if (!BTSLUtil.isNullString(receiverSetId))

                    {
                        promotionDetailsVO2 = _loyaltyController.loadProfile(con, receiverSetId);
                        // check whether the promotion belongs to
                        // promotion_type=LYTPT. If not, then exit.
                        if (promotionDetailsVO2 != null) {
                            if (promotionDetailsVO2.getPromotionType().equalsIgnoreCase(PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT)) {

                                // For LMS Profile Details are loaded here
                                promotionDetailsVO2 = _loyaltyController.loadLMSProfileAndVersion(con, serviceType, receiverSetId, txnAmount);

								if(promotionDetailsVO2==null   || !p_loyaltyVO.getProductCode().equalsIgnoreCase(promotionDetailsVO2.getProductCode())){

                                    p_loyaltyVO.setTxnStatus(PretupsErrorCodesI.FAILED);
                                    p_loyaltyVO.setErrorode(PretupsErrorCodesI.LOYALTY_PROMONOT_DEFINED);
                                    throw new BTSLBaseException(this, "distributeLoyaltyPoints", PretupsErrorCodesI.LOYALTY_PROMONOT_DEFINED);
                                }
                                p_loyaltyVO.setUserid(p_loyaltyVO.getTouserId());
                                String senderSetId = p_loyaltyVO.getSetId();
                                p_loyaltyVO.setSetId(p_loyaltyVO.getToSetId());
                                String version =  p_loyaltyVO.getVersion();
                                p_loyaltyVO.setVersion(promotionDetailsVO2.getVersion());
                                if (_log.isDebugEnabled()) {
                                    _log.debug("distributeLoyaltyPoints Method", "Entered for TransactionID : " + p_transactionid + " ServiceType : " + p_loyaltyVO
                                                    .getServiceType());
                                }
                                _loyaltyController.DistributeLoyaltyPointsForO2CService(con, p_loyaltyVO, promotionDetailsVO2);// For
                                // C2C
                                p_loyaltyVO.setVersion(version);
                                p_loyaltyVO.setSetId(senderSetId);
                                if (_log.isDebugEnabled()) {
                                    _log.debug("distributeLoyaltyPoints Method", "Exit for Transaction ID : " + p_transactionid + " ServiceType : " + p_loyaltyVO
                                                    .getServiceType());
                                }
                                LoyaltyPointsLog.log("TXN", p_loyaltyVO.getFromuserId(), p_loyaltyVO.getSetId(), new Date(), PretupsI.CREDIT, p_transactionid,
                                                PretupsI.C2C_MODULE, p_loyaltyVO.getServiceType(), p_loyaltyVO.getTotalCrLoyaltyPoint(), 0L, 0L, "200", "");
                                LoyaltyPointsLog.log("TXN", p_loyaltyVO.getTouserId(), p_loyaltyVO.getToSetId(), new Date(), PretupsI.CREDIT, p_transactionid,
                                                PretupsI.C2C_MODULE, p_loyaltyVO.getServiceType(), p_loyaltyVO.getTotalCrLoyaltyPoint(), 0L, 0L, "200", "");
                                try {
                                    con.commit();
                                } catch (Exception bex) {
                                    _log.errorTrace(METHOD_NAME, bex);
                                    p_loyaltyVO.setErrorode(PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
                                    LoyaltyPointsLog.log("TXN", p_loyaltyVO.getFromuserId(), p_loyaltyVO.getSetId(), new Date(), PretupsI.CREDIT, p_transactionid,
                                                    PretupsI.C2C_MODULE, p_loyaltyVO.getServiceType(), p_loyaltyVO.getTotalCrLoyaltyPoint(), 0L, 0L, "206", p_loyaltyVO
                                                                    .getErrorode());
                                    LoyaltyPointsLog.log("TXN", p_loyaltyVO.getTouserId(), p_loyaltyVO.getToSetId(), new Date(), PretupsI.CREDIT, p_transactionid,
                                                    PretupsI.C2C_MODULE, p_loyaltyVO.getServiceType(), p_loyaltyVO.getTotalCrLoyaltyPoint(), 0L, 0L, "206", p_loyaltyVO
                                                                    .getErrorode());
                                    throw new BTSLBaseException("distributeLoyaltyPoints", "process", PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
                                }
                                try {
                                    if (con != null) {
                                        con.rollback();
                                    }
                                } catch (SQLException e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                }
                            }
                        }
                    }
                }
            }

            else {
                if (!BTSLUtil.isNullString(receiverSetId))

                {
                    promotionDetailsVO2 = _loyaltyController.loadProfile(con, receiverSetId);
                    // check whether the promotion belongs to
                    // promotion_type=LYTPT. If not, then exit.
                    if (promotionDetailsVO2 != null) {
                        if (promotionDetailsVO2.getPromotionType().equalsIgnoreCase(PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT)) {

                            // For LMS Profile Details are loaded here
                            promotionDetailsVO2 = _loyaltyController.loadLMSProfileAndVersion(con, serviceType, receiverSetId, txnAmount);

							if(promotionDetailsVO2==null   || !p_loyaltyVO.getProductCode().equalsIgnoreCase(promotionDetailsVO2.getProductCode())){

                                p_loyaltyVO.setTxnStatus(PretupsErrorCodesI.FAILED);
                                p_loyaltyVO.setErrorode(PretupsErrorCodesI.LOYALTY_PROMONOT_DEFINED);
                                throw new BTSLBaseException(this, "distributeLoyaltyPoints", PretupsErrorCodesI.LOYALTY_PROMONOT_DEFINED);
                            }
                            p_loyaltyVO.setUserid(p_loyaltyVO.getTouserId());
                            if (_log.isDebugEnabled()) {
                                _log.debug("distributeLoyaltyPoints Method", "Entered for TransactionID : " + p_transactionid + " ServiceType : " + p_loyaltyVO
                                                .getServiceType());
                            }
                            _loyaltyController.DistributeLoyaltyPointsForC2SService(con, p_loyaltyVO, promotionDetailsVO2); // FOR
                            // C2C
                            if (_log.isDebugEnabled()) {
                                _log.debug("distributeLoyaltyPoints Method", "Exit for Transaction ID : " + p_transactionid + " ServiceType : " + p_loyaltyVO.getServiceType());
                            }
                            LoyaltyPointsLog.log("TXN", p_loyaltyVO.getFromuserId(), p_loyaltyVO.getSetId(), new Date(), PretupsI.CREDIT, p_transactionid,
                                            PretupsI.C2C_MODULE, p_loyaltyVO.getServiceType(), p_loyaltyVO.getTotalCrLoyaltyPoint(), 0L, 0L, "200", "");
                            LoyaltyPointsLog.log("TXN", p_loyaltyVO.getTouserId(), p_loyaltyVO.getToSetId(), new Date(), PretupsI.CREDIT, p_transactionid,
                                            PretupsI.C2C_MODULE, p_loyaltyVO.getServiceType(), p_loyaltyVO.getTotalCrLoyaltyPoint(), 0L, 0L, "200", "");
                            try {
                                con.commit();
                            } catch (Exception bex) {
                                _log.errorTrace(METHOD_NAME, bex);
                                p_loyaltyVO.setErrorode(PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
                                LoyaltyPointsLog.log("TXN", p_loyaltyVO.getFromuserId(), p_loyaltyVO.getSetId(), new Date(), PretupsI.CREDIT, p_transactionid,
                                                PretupsI.C2C_MODULE, p_loyaltyVO.getServiceType(), p_loyaltyVO.getTotalCrLoyaltyPoint(), 0L, 0L, "206", p_loyaltyVO
                                                                .getErrorode());
                                LoyaltyPointsLog.log("TXN", p_loyaltyVO.getTouserId(), p_loyaltyVO.getToSetId(), new Date(), PretupsI.CREDIT, p_transactionid,
                                                PretupsI.C2C_MODULE, p_loyaltyVO.getServiceType(), p_loyaltyVO.getTotalCrLoyaltyPoint(), 0L, 0L, "206", p_loyaltyVO
                                                                .getErrorode());
                                throw new BTSLBaseException("distributeLoyaltyPoints", "process", PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
                            }
                            try {
                                if (con != null) {
                                    con.rollback();
                                }
                            } catch (SQLException e) {
                                _log.errorTrace(METHOD_NAME, e);
                            }
                        }
                    }
                }
            }

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("distributeLoyaltyPoints Method[BTSLBaseException]",
                                "Error During LoyaltyPointDistribution...for Transaction ID : " + p_transactionid + " Service Type : " + p_loyaltyVO.getServiceType() + " Error Code " + p_loyaltyVO
                                                .getErrorode());
            }
        } catch (Exception ex) {
            p_loyaltyVO.setTxnStatus(PretupsI.TXN_STATUS_FAIL);
            try {
                _log.errorTrace(METHOD_NAME, ex);
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("distributeLoyaltyPoints Method[Exception]",
                                "Error During LoyaltiPointDistribution...for Transaction ID : " + p_transactionid + " Service Type : " + p_loyaltyVO.getServiceType() + " Error Code " + p_loyaltyVO
                                                .getErrorode());
            }
        } finally {
			if (mcomCon != null) {
				mcomCon.close("LoyaltyBL#distributeLoyaltyPoints");
				mcomCon = null;
			}
            if (p_loyaltyVO!=null && !p_loyaltyVO.getTxnStatus().equalsIgnoreCase(PretupsI.TXN_STATUS_SUCCESS) && promotionDetailsVO!=null && !promotionDetailsVO.getPromotionType().equalsIgnoreCase(
                            PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT) && !p_loyaltyVO.getTxnStatus().equalsIgnoreCase(PretupsI.SUCCESS)) {
                if (!BTSLUtil.isNullString(p_loyaltyVO.getErrorode())) {
                    p_loyaltyVO.setTxnStatus(PretupsI.TXN_STATUS_FAIL);

                    if (p_loyaltyVO.getModuleType().equalsIgnoreCase(PretupsI.C2C_MODULE)) {
                        p_loyaltyVO.setUserid(p_loyaltyVO.getFromuserId());
                        p_loyaltyVO.setUserid(p_loyaltyVO.getTouserId());
                    } 
                    try {
                        if (con != null) {
                            con.rollback();
                        }
                    } catch (SQLException e) {
                        _log.errorTrace(METHOD_NAME, e);
                    }
                }
            }

            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
                con = null;
            }
        }

    }

}
