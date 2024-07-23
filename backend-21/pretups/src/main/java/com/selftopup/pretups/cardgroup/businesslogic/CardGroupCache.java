package com.selftopup.pretups.cardgroup.businesslogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.util.PretupsBL;

public class CardGroupCache {
    private static Log _log = LogFactory.getLog(CardGroupCache.class.getName());
    private static HashMap<String, ArrayList<CardGroupDetailsVO>> _cardGroupMap = new HashMap<String, ArrayList<CardGroupDetailsVO>>();
    private static HashMap<String, ArrayList<CardGroupSetVersionVO>> _cardGroupVerMap = new HashMap<String, ArrayList<CardGroupSetVersionVO>>();

    /**
     * @author ankur.dhawan
     *         Description : This method loads the card group cache at startup
     *         Method : loadCardGroupMapAtStartup
     * @return
     */
    public static void loadCardGroupMapAtStartup() {
        if (_log.isDebugEnabled())
            _log.debug("loadCardGroupMapAtStartup", "Entered");
        _cardGroupVerMap = loadVersionMapping();
        _cardGroupMap = loadMapping();
        if (_log.isDebugEnabled())
            _log.debug("loadCardGroupMapAtStartup()", "Exited");
    }

    /**
     * @author ankur.dhawan
     *         Description : This method loads the card group mapping
     *         Method : loadMapping
     * @return HashMap
     */
    private static HashMap<String, ArrayList<CardGroupDetailsVO>> loadMapping() {
        if (_log.isDebugEnabled())
            _log.debug("loadMapping", "Entered");

        HashMap<String, ArrayList<CardGroupDetailsVO>> cardGroupMap = null;
        CardGroupDAO cardGroupDAO = null;
        try {
            cardGroupDAO = new CardGroupDAO();
            cardGroupMap = cardGroupDAO.loadCardGroupCache();
        } catch (Exception e) {
            _log.error("loadMapping", "Exception e:" + e.getMessage());
            _log.errorTrace("loadMapping: Exception print stack trace:", e);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("loadMapping", "Exiting. cardGroupMap.size()=" + _cardGroupMap.size());
        }
        return cardGroupMap;
    }

    /**
     * @author ankur.dhawan
     *         Description : This method returns the details of card group based
     *         on cardGroupSetID,requestAmount,
     *         applicableDate from the cardGroupMap
     *         Method : getCardGroupDetails
     * @return CardGroupDetailsVO
     */
    public static CardGroupDetailsVO getCardGroupDetails(String p_cardGroupSetID, long p_requestAmount, java.util.Date p_applicableDate) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getCardGroupDetails", "Entered p_cardGroupSetID=" + p_cardGroupSetID + " p_requestAmount=" + p_requestAmount + " p_applicableDate=" + p_applicableDate);

        CardGroupSetVersionVO cardGroupSetVersionVO = null;
        CardGroupSetVersionVO prevCardGroupSetVersionVO = null;
        ArrayList<CardGroupSetVersionVO> cardGroupVersionList = null;
        CardGroupDetailsVO newCardGroupDetailsVO = null;
        ;
        String latestVersion = null;
        Iterator iter = null;
        ArrayList<CardGroupDetailsVO> cardGroupList = null;
        CardGroupDetailsVO cardGroupDetailsVO = null;
        // CardGroupDetailsVO prevCardGroupDetailsVO=null;
        boolean cardGroupFound = false;

        try {
            cardGroupVersionList = _cardGroupVerMap.get(p_cardGroupSetID);

            if (!cardGroupVersionList.isEmpty()) {
                iter = cardGroupVersionList.iterator();
                while (iter.hasNext()) {
                    cardGroupSetVersionVO = (CardGroupSetVersionVO) iter.next();
                    if (cardGroupSetVersionVO.getApplicableFrom().compareTo(p_applicableDate) <= 0) {
                        if (prevCardGroupSetVersionVO != null && cardGroupSetVersionVO.getApplicableFrom().compareTo(prevCardGroupSetVersionVO.getApplicableFrom()) < 0) {
                            latestVersion = prevCardGroupSetVersionVO.getVersion();
                        } else {
                            latestVersion = cardGroupSetVersionVO.getVersion();
                            prevCardGroupSetVersionVO = cardGroupSetVersionVO;
                        }
                    }
                }
            } else if (cardGroupVersionList.isEmpty() || prevCardGroupSetVersionVO == null) {
                throw new BTSLBaseException("CardGroupCache", "getCardGroupDetails", SelfTopUpErrorCodesI.CARD_GROUP_SETVERNOT_ASSOCIATED, 0, new String[] { PretupsBL.getDisplayAmount(p_requestAmount) }, null);
            }

            iter = null;

            if (latestVersion != null) {
                cardGroupList = _cardGroupMap.get(p_cardGroupSetID + "_" + latestVersion);

                iter = cardGroupList.iterator();
                while (iter.hasNext()) {
                    cardGroupDetailsVO = (CardGroupDetailsVO) iter.next();
                    if (cardGroupDetailsVO.getStartRange() <= p_requestAmount && cardGroupDetailsVO.getEndRange() >= p_requestAmount) {
                        cardGroupFound = true;
                        break;
                    }
                }
            }

            if (cardGroupDetailsVO == null || !cardGroupFound) {
                throw new BTSLBaseException("CardGroupCache", "getCardGroupDetails", SelfTopUpErrorCodesI.CARD_GROUP_VALUE_NOT_IN_RANGE, 0, new String[] { PretupsBL.getDisplayAmount(p_requestAmount) }, null);
            }
            if (cardGroupDetailsVO != null)
                newCardGroupDetailsVO = copyCardGroupDetailsVO(cardGroupDetailsVO, latestVersion);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("getCardGroupDetails", "SQLException " + e.getMessage());
            _log.errorTrace("getCardGroupDetails: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupCache[getCardGroupDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CardGroupCache", "getCardGroupDetails", "error.general.processing");
        }
        if (_log.isDebugEnabled())
            _log.debug("getCardGroupDetails", "Exiting. cardGroupDetailsVO=" + newCardGroupDetailsVO.toString());
        return newCardGroupDetailsVO;
    }

    private static CardGroupDetailsVO copyCardGroupDetailsVO(CardGroupDetailsVO p_cardGroupDetailsVO, String p_latestVersion) {

        CardGroupDetailsVO temp = new CardGroupDetailsVO();
        temp.setCardGroupSetID(p_cardGroupDetailsVO.getCardGroupSetID());
        temp.setCardGroupID(p_cardGroupDetailsVO.getCardGroupID());
        temp.setCardGroupCode(p_cardGroupDetailsVO.getCardGroupCode());
        temp.setVersion(p_latestVersion);
        temp.setStartRange(p_cardGroupDetailsVO.getStartRange());
        temp.setEndRange(p_cardGroupDetailsVO.getEndRange());
        temp.setValidityPeriodType(p_cardGroupDetailsVO.getValidityPeriodType());
        temp.setValidityPeriod(p_cardGroupDetailsVO.getValidityPeriod());
        temp.setGracePeriod(p_cardGroupDetailsVO.getGracePeriod());
        temp.setSenderTax1Name(p_cardGroupDetailsVO.getSenderTax1Name());
        temp.setSenderTax1Type(p_cardGroupDetailsVO.getSenderTax1Type());
        temp.setSenderTax1Rate(p_cardGroupDetailsVO.getSenderTax1Rate());
        temp.setSenderTax2Name(p_cardGroupDetailsVO.getSenderTax2Name());
        temp.setSenderTax2Type(p_cardGroupDetailsVO.getSenderTax2Type());
        temp.setSenderTax2Rate(p_cardGroupDetailsVO.getSenderTax2Rate());
        temp.setReceiverTax1Name(p_cardGroupDetailsVO.getReceiverTax1Name());
        temp.setReceiverTax1Type(p_cardGroupDetailsVO.getReceiverTax1Type());
        temp.setReceiverTax1Rate(p_cardGroupDetailsVO.getReceiverTax1Rate());
        temp.setReceiverTax2Name(p_cardGroupDetailsVO.getReceiverTax2Name());
        temp.setReceiverTax2Type(p_cardGroupDetailsVO.getReceiverTax2Type());
        temp.setReceiverTax2Rate(p_cardGroupDetailsVO.getReceiverTax2Rate());
        temp.setBonusTalkTimeType(p_cardGroupDetailsVO.getBonusTalkTimeType());
        temp.setBonusTalkTimeRate(p_cardGroupDetailsVO.getBonusTalkTimeRate());
        temp.setBonusValidityValue(p_cardGroupDetailsVO.getBonusValidityValue());
        temp.setSenderAccessFeeType(p_cardGroupDetailsVO.getSenderAccessFeeType());
        temp.setSenderAccessFeeRate(p_cardGroupDetailsVO.getSenderAccessFeeRate());
        temp.setReceiverAccessFeeType(p_cardGroupDetailsVO.getReceiverAccessFeeType());
        temp.setReceiverAccessFeeRate(p_cardGroupDetailsVO.getReceiverAccessFeeRate());
        temp.setMinSenderAccessFee(p_cardGroupDetailsVO.getMinSenderAccessFee());
        temp.setMaxSenderAccessFee(p_cardGroupDetailsVO.getMaxSenderAccessFee());
        temp.setMinReceiverAccessFee(p_cardGroupDetailsVO.getMinReceiverAccessFee());
        temp.setMaxReceiverAccessFee(p_cardGroupDetailsVO.getMaxReceiverAccessFee());
        temp.setMultipleOf(p_cardGroupDetailsVO.getMultipleOf());
        temp.setCardGroupSetName(p_cardGroupDetailsVO.getCardGroupSetName());
        temp.setCardGroupSubServiceId(p_cardGroupDetailsVO.getCardGroupSubServiceId());
        temp.setCardGroupSubServiceIdDesc(p_cardGroupDetailsVO.getCardGroupSubServiceIdDesc());
        temp.setServiceTypeId(p_cardGroupDetailsVO.getServiceTypeId());
        temp.setServiceTypeDesc(p_cardGroupDetailsVO.getServiceTypeDesc());
        temp.setSetType(p_cardGroupDetailsVO.getSetType());
        temp.setSetTypeName(p_cardGroupDetailsVO.getSetTypeName());
        temp.setBonus1(p_cardGroupDetailsVO.getBonus1());
        temp.setBonus2(p_cardGroupDetailsVO.getBonus2());
        temp.setStatus(p_cardGroupDetailsVO.getStatus());
        temp.setBonusTalktimevalidity(p_cardGroupDetailsVO.getBonusTalktimevalidity());
        temp.setBonus1validity(p_cardGroupDetailsVO.getBonus1validity());
        temp.setBonus2validity(p_cardGroupDetailsVO.getBonus2validity());
        temp.setOnline(p_cardGroupDetailsVO.getOnline());
        temp.setBoth(p_cardGroupDetailsVO.getBoth());
        temp.setSenderConvFactor(p_cardGroupDetailsVO.getSenderConvFactor());
        temp.setReceiverConvFactor(p_cardGroupDetailsVO.getReceiverConvFactor());
        temp.setApplicableFrom(p_cardGroupDetailsVO.getApplicableFrom());
        temp.setBonusAccList(p_cardGroupDetailsVO.getBonusAccList());
        // added for cos
        if (SystemPreferences.COS_REQUIRED)
            temp.setCosRequired(p_cardGroupDetailsVO.getCosRequired());
        // added for IN promo
        if (SystemPreferences.IN_PROMO_REQUIRED) {
            temp.setInPromo(p_cardGroupDetailsVO.getInPromo());
        }
        return temp;
    }

    /**
     * @author ankur.dhawan
     *         Description : This method loads the card group mapping
     *         Method : loadMapping
     * @return HashMap
     */
    private static HashMap<String, ArrayList<CardGroupSetVersionVO>> loadVersionMapping() {
        if (_log.isDebugEnabled())
            _log.debug("loadversionMapping", "Entered");

        HashMap<String, ArrayList<CardGroupSetVersionVO>> cardGroupVersionMap = null;
        CardGroupDAO cardGroupDAO = null;
        try {
            cardGroupDAO = new CardGroupDAO();
            cardGroupVersionMap = cardGroupDAO.loadCardGroupVersionCache();
        } catch (Exception e) {
            _log.error("loadMapping", "Exception e:" + e.getMessage());
            _log.errorTrace("loadMapping: Exception print stack trace:", e);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("loadMapping", "Exiting. cardGroupVersionMap.size()=" + cardGroupVersionMap.size());
        }
        return cardGroupVersionMap;

    }
}