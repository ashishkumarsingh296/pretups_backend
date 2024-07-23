/**
 * @(#)LoyaltyController.java
 *                            Copyright(c) 2005, Bharti Telesoft Ltd.
 *                            All Rights Reserved
 * 
 *                            <description>
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            rakesh.sinha Dec,2013 Initital Creation
 *                            Vibhu Trehan Jan,2014 Modification & customization
 * 
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            This is the controller class for the Loyalty
 *                            Management.
 */

package com.btsl.pretups.loyaltymgmt.transfer.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.PromotionDetailsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.txn.pretups.loyaltymgmt.businesslogic.LoyaltyTxnDAO;

public class LoyaltyController {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private LoyaltyTxnDAO loyaltytxnDAO = new LoyaltyTxnDAO();

    public PromotionDetailsVO loadProfile(Connection con, String p_setId) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadProfile Method", "Entered...for Set ID : " + p_setId);
        }
        final String METHOD_NAME = "loadProfile";
        PromotionDetailsVO promotionDetailsVO = null;
        try {
            promotionDetailsVO = loyaltytxnDAO.loadProfile(con, p_setId);
        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, "LoyaltyController", "loadProfile", PretupsErrorCodesI.LOYALTY_PROMONOT_DEFINED_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("loadProfile Method", "Exit...for Set ID : " + p_setId);
            }
        }

        return promotionDetailsVO;
    }

    public PromotionDetailsVO loadLMSProfileAndVersion(Connection con, String p_serviceType, String p_setId, long txnAmount) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("Method-loadLMSProfileAndVersion ", "Entered...for Set ID : " + p_setId + ",p_serviceType:" + p_serviceType);
        }
        final String METHOD_NAME = "loadLMSProfileAndVersion";
        PromotionDetailsVO promotionDetailsVO = null;
        try {
            promotionDetailsVO = loyaltytxnDAO.loadLMSProfileAndVersion(con, p_serviceType, p_setId, txnAmount);
        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, "LoyaltyController", "loadLMSProfileAndVersion", PretupsErrorCodesI.LOYALTY_PROMONOT_DEFINED_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("Method-loadLMSProfileAndVersion", "Exit ");
            }
        }

        return promotionDetailsVO;
    }

    public void DistributeLoyaltyPointsForC2CService(Connection con, LoyaltyVO p_loyaltyvo, PromotionDetailsVO promotionDetailsVO, PromotionDetailsVO promotionDetailsVO2) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("DistributeLoyaltyPointsForC2CService Method", "Entered............With Sender Userid :" + p_loyaltyvo.getUserid() + " reciver Userid :" + p_loyaltyvo
                            .getReciverid());
        }
        final String METHOD_NAME = "DistributeLoyaltyPointsForC2CService";
        try {
            if (!(promotionDetailsVO == null)) {
                if (promotionDetailsVO.getPromotionType().equalsIgnoreCase(PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT)) {
                    p_loyaltyvo.setUserid(p_loyaltyvo.getFromuserId());
                    DistributeLoyaltyPointsForC2SService(con, p_loyaltyvo, promotionDetailsVO);
                    // insertLoyaltyTxnData(con,p_loyaltyvo);

                }
            }
            if (!(promotionDetailsVO2 == null)) {
                if (promotionDetailsVO2.getPromotionType().equalsIgnoreCase(PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT)) {
                    p_loyaltyvo.setUserid(p_loyaltyvo.getTouserId());
                    String senderSetId = p_loyaltyvo.getSetId();
                    p_loyaltyvo.setSetId(p_loyaltyvo.getToSetId());
                    String version =  p_loyaltyvo.getVersion();
                    p_loyaltyvo.setVersion(promotionDetailsVO2.getVersion());
                    DistributeLoyaltyPointsForO2CService(con, p_loyaltyvo, promotionDetailsVO2);
                    p_loyaltyvo.setSetId(senderSetId);
                    p_loyaltyvo.setVersion(version);
                    
                    // insertLoyaltyTxnData(con,p_loyaltyvo);
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
                _log.debug("DistributeLoyaltyPointsForC2CService Method[BTSLBaseException]", "Error During LoyaltiPointDistribution...for Transaction ID : " + p_loyaltyvo
                                .getTxnId() + " Service Type : " + p_loyaltyvo.getServiceType() + " Error Code " + p_loyaltyvo.getErrorode());
            }
        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, "LoyaltyController", "DistributeLoyaltyPointsForC2CService", PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
        }
    }

    public void DistributeLoyaltyPointsForO2CService(Connection con, LoyaltyVO p_loyaltyvo, PromotionDetailsVO promotionDetailsVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("DistributeLoyaltyPointsForO2CService Method", "Entered............");
        }
        final String METHOD_NAME = "DistributeLoyaltyPointsForO2CService";
        try {
            long totalCreditPoints = 0;
            int count = 0;
            int count2 = 0;

            long points = 0L;

            points = promotionDetailsVO.getPoints();
            if (promotionDetailsVO.getPointsType().equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
                final double calPoints = Double.parseDouble(PretupsBL.getDisplayAmount(p_loyaltyvo.getTransferamt() * promotionDetailsVO.getPoints())) / 100;
                points = BTSLUtil.parseDoubleToLong(PretupsBL.Round(calPoints, 0));
            }
            p_loyaltyvo.setTotalCrLoyaltyPoint(points);

            count = creditLoyaltyPointToPayeerO2C(con, p_loyaltyvo);
            if (count > 0) {
                totalCreditPoints = totalCreditPoints + p_loyaltyvo.getTotalCrLoyaltyPoint();
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_STOCK_REQUIRED))).booleanValue()) {
                    count2 = loyaltytxnDAO.debitNetworkLoyaltyStock(con, p_loyaltyvo, totalCreditPoints);
                } else {
                    count2 = 1;
                }

                if (count2 == 0) {
                    throw new BTSLBaseException(this, "DistributeLoyaltyPointsForC2CService", PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
                }
                p_loyaltyvo.setTotalCrLoyaltyPoint(totalCreditPoints);
            } else {
                throw new BTSLBaseException(this, "LoyaltyController", "DistributeLoyaltyPointsForO2CService", PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
            }
            p_loyaltyvo.setTotalCrLoyaltyPoint(totalCreditPoints);
        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, "LoyaltyController", "DistributeLoyaltyPointsForO2CService", PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
        }
    }

    public void DistributeLoyaltyPointsForC2SService(Connection con, LoyaltyVO p_loyaltyvo, PromotionDetailsVO promotionDetailsVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("DistributeLoyaltyPointsForC2SService Method", "Entered with userId: " + p_loyaltyvo.getUserid());
        }
        final String METHOD_NAME = "DistributeLoyaltyPointsForC2SService";
        try {

            long totalCreditPoints = 0l;
            int count = 0;
            int count2 = 0;
            long points = 0L;
            points = promotionDetailsVO.getPoints();
            if (promotionDetailsVO.getPointsType().equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
                final double calPoints = Double.parseDouble(PretupsBL.getDisplayAmount(p_loyaltyvo.getTransferamt() * promotionDetailsVO.getPoints())) / 100;
                points = BTSLUtil.parseDoubleToLong(PretupsBL.Round(calPoints, 0));
            }
            p_loyaltyvo.setTotalCrLoyaltyPoint(points);
            count = creditLoyaltyPointOnlyPayeeC2S(con, p_loyaltyvo);
            if (count > 0) {
                totalCreditPoints = totalCreditPoints + p_loyaltyvo.getTotalCrLoyaltyPoint();
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_STOCK_REQUIRED))).booleanValue()) {
                    count2 = loyaltytxnDAO.debitNetworkLoyaltyStock(con, p_loyaltyvo, totalCreditPoints);
                } else {
                    count2 = 1;
                }
                if (count2 == 0) {
                    throw new BTSLBaseException(this, "DistributeLoyaltyPointsForC2CService", PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
                }
                p_loyaltyvo.setTotalCrLoyaltyPoint(totalCreditPoints);
            } else {
                throw new BTSLBaseException(this, "LoyaltyController", "DistributeLoyaltyPointsForC2CService", PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
            }

        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, "LoyaltyController", "DistributeLoyaltyPointsForC2SService", PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
        }

    }

    private int creditLoyaltyPointToPayeerO2C(Connection con, LoyaltyVO p_LoyaltyVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("creditLoyaltyPointToPayeeO2C Method", "Entered...with receiver ID" + p_LoyaltyVO.getReciverid());
        }
        final String METHOD_NAME = "creditLoyaltyPointToPayeerO2C";
        int count = 0;
        try {
            count = loyaltytxnDAO.creditLoyaltyPointToPayeerO2C(con, p_LoyaltyVO);
        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
        }
        return count;
    }

    private int creditLoyaltyPointOnlyPayeeC2S(Connection con, LoyaltyVO p_LoyaltyVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("creditLoyaltyPointToPayee Method", "Entered with ID" + p_LoyaltyVO.getUserid());
        }
        final String METHOD_NAME = "creditLoyaltyPointOnlyPayeeC2S";
        int count = 0;
        try {
            count = loyaltytxnDAO.creditLoyaltyPointToPayeeC2S(con, p_LoyaltyVO);
        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
        }
        return count;
    }

    /*
     * public void insertLoyaltyTxnData(Connection con,LoyaltyVO p_LoyaltyVO)
     * throws BTSLBaseException {
     * if(_log.isDebugEnabled())_log.debug("insertLoyaltyTxnData Method",
     * "Entered...");
     * int count=0;
     * try
     * {
     * count=_loyaltyDAO.insertLoyaltyTxnData(con,p_LoyaltyVO);
     * if(count>0)
     * con.commit();
     * }
     * catch(Exception ex){ex.printStackTrace();}
     * 
     * }
     */
}
