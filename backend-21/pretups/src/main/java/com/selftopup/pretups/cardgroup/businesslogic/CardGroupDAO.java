package com.selftopup.pretups.cardgroup.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
// commented for DB2 import oracle.jdbc.OraclePreparedStatement;
import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.ListValueVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.transfer.businesslogic.TransferVO;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

/*
 * CardGroupDAO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 28/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Card Group Data access object class for interaction with the database
 */

public class CardGroupDAO {

    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * This method loads the Card group details like the access fee, tax etc for
     * the slab in which the requested value lies
     * 
     * @param p_con
     * @param p_cardGroupSetAssocVO
     * @return CardGroupDetailsVO
     * @throws BTSLBaseException
     * @throws SQLException
     * @throws Exception
     */
    public CardGroupDetailsVO loadCardGroupDetails(Connection p_con, String p_cardGroupSetID, long p_requestAmount, java.util.Date p_applicableDate) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadCardGroupDetails", "Entered p_cardGroupSetID=" + p_cardGroupSetID + " p_requestAmount=" + p_requestAmount + " p_applicableDate=" + p_applicableDate);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        CardGroupDetailsVO cardGroupDetailsVO = null;
        String latestCardGroupVersion = null;
        BonusBundleDAO bonusBundleDAO = null;
        try {

            latestCardGroupVersion = loadCardGroupSetVersionLatestVersion(p_con, p_cardGroupSetID, p_applicableDate);
            if (!BTSLUtil.isNullString(latestCardGroupVersion)) {
                StringBuffer qry = new StringBuffer(" SELECT l.lookup_name set_name,cs.set_type,cgd.card_group_set_id, ");
                qry.append(" cgd.card_group_id,cgd.card_group_code,cgd.start_range,cgd.end_range,cgd.validity_period_type, ");
                qry.append(" cgd.validity_period,cgd.grace_period, cgd.sender_tax1_name, cgd.sender_tax1_type, ");
                qry.append(" cgd.sender_tax1_rate, cgd.sender_tax2_name, cgd.sender_tax2_type, cgd.sender_tax2_rate, ");
                qry.append(" cgd.receiver_tax1_name,cgd.receiver_tax1_type, cgd.receiver_tax1_rate,cgd.receiver_tax2_name, ");
                qry.append(" cgd.receiver_tax2_type, cgd.receiver_tax2_rate, ");
                qry.append(" cgd.sender_access_fee_type, cgd.sender_access_fee_rate, ");
                qry.append(" cgd.receiver_access_fee_type,cgd.receiver_access_fee_rate, cgd.min_sender_access_fee, ");
                qry.append(" cgd.max_sender_access_fee, cgd.min_receiver_access_fee,cgd.max_receiver_access_fee, cgd.multiple_of , ");
                qry.append(" cs.sub_service,cs.card_group_set_name, stsm.selector_name, cs.service_type, st.name service_name, cgd.status, cgd.bonus_validity_value,cgd.online_offline,cgd.both,cgd.both,");
                qry.append(" cgd.sender_mult_factor,cgd.receiver_mult_factor,cgd.cos_required ,cgd.in_promo ");
                qry.append(" FROM card_group_details cgd,lookups l, card_group_set cs, service_type st,service_type_selector_mapping stsm ");
                qry.append(" WHERE cgd.card_group_set_id=? AND cgd.version=? AND (cgd.start_range<=? ");
                qry.append(" AND cgd.end_range>=?) AND l.lookup_type=? AND cs.card_group_set_id=cgd.card_group_set_id ");
                qry.append(" AND cs.service_type=st.service_type AND stsm.service_type=st.service_type ");
                qry.append(" AND cs.sub_service=stsm.selector_code AND cs.service_type=stsm.service_type AND l.lookup_code=cs.set_type");
                String selectQuery = qry.toString();
                if (_log.isDebugEnabled())
                    _log.debug("loadCardGroupDetails", "select query:" + selectQuery);

                bonusBundleDAO = new BonusBundleDAO();

                pstmtSelect = p_con.prepareStatement(selectQuery);
                pstmtSelect.setString(1, p_cardGroupSetID);
                pstmtSelect.setString(2, latestCardGroupVersion);
                pstmtSelect.setLong(3, p_requestAmount);
                pstmtSelect.setLong(4, p_requestAmount);
                pstmtSelect.setString(5, PretupsI.CARD_GROUP_SET_TYPE);
                rs = pstmtSelect.executeQuery();
                if (rs.next()) {
                    cardGroupDetailsVO = new CardGroupDetailsVO();
                    cardGroupDetailsVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                    cardGroupDetailsVO.setCardGroupID(rs.getString("card_group_id"));
                    cardGroupDetailsVO.setCardGroupCode(rs.getString("card_group_code"));
                    cardGroupDetailsVO.setVersion(latestCardGroupVersion);
                    cardGroupDetailsVO.setStartRange(rs.getLong("start_range"));
                    cardGroupDetailsVO.setEndRange(rs.getLong("end_range"));
                    cardGroupDetailsVO.setValidityPeriodType(rs.getString("validity_period_type"));
                    cardGroupDetailsVO.setValidityPeriod(rs.getInt("validity_period"));
                    cardGroupDetailsVO.setGracePeriod(rs.getLong("grace_period"));
                    cardGroupDetailsVO.setSenderTax1Name(rs.getString("sender_tax1_name"));
                    cardGroupDetailsVO.setSenderTax1Type(rs.getString("sender_tax1_type"));
                    cardGroupDetailsVO.setSenderTax1Rate(rs.getDouble("sender_tax1_rate"));
                    cardGroupDetailsVO.setSenderTax2Name(rs.getString("sender_tax2_name"));
                    cardGroupDetailsVO.setSenderTax2Type(rs.getString("sender_tax2_type"));
                    cardGroupDetailsVO.setSenderTax2Rate(rs.getDouble("sender_tax2_rate"));
                    cardGroupDetailsVO.setReceiverTax1Name(rs.getString("receiver_tax1_name"));
                    cardGroupDetailsVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
                    cardGroupDetailsVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                    cardGroupDetailsVO.setReceiverTax2Name(rs.getString("receiver_tax2_name"));
                    cardGroupDetailsVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
                    cardGroupDetailsVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                    cardGroupDetailsVO.setSenderAccessFeeType(rs.getString("sender_access_fee_type"));
                    cardGroupDetailsVO.setSenderAccessFeeRate(rs.getDouble("sender_access_fee_rate"));
                    cardGroupDetailsVO.setReceiverAccessFeeType(rs.getString("receiver_access_fee_type"));
                    cardGroupDetailsVO.setReceiverAccessFeeRate(rs.getDouble("receiver_access_fee_rate"));
                    cardGroupDetailsVO.setMinSenderAccessFee(rs.getLong("min_sender_access_fee"));
                    cardGroupDetailsVO.setMaxSenderAccessFee(rs.getLong("max_sender_access_fee"));
                    cardGroupDetailsVO.setMinReceiverAccessFee(rs.getLong("min_receiver_access_fee"));
                    cardGroupDetailsVO.setMaxReceiverAccessFee(rs.getLong("max_receiver_access_fee"));
                    cardGroupDetailsVO.setMultipleOf(rs.getLong("multiple_of"));
                    cardGroupDetailsVO.setCardGroupSetName(rs.getString("card_group_set_name"));
                    cardGroupDetailsVO.setCardGroupSubServiceId(rs.getString("sub_service"));
                    cardGroupDetailsVO.setCardGroupSubServiceIdDesc(rs.getString("selector_name"));
                    cardGroupDetailsVO.setServiceTypeId(rs.getString("service_type"));
                    cardGroupDetailsVO.setServiceTypeDesc(rs.getString("service_name"));
                    cardGroupDetailsVO.setSetType(rs.getString("set_type"));
                    cardGroupDetailsVO.setSetTypeName(rs.getString("set_name"));
                    // added for suspend/resume card group slab
                    cardGroupDetailsVO.setStatus(rs.getString("status"));
                    // added by amit and reviewed by Vikask
                    cardGroupDetailsVO.setBonusValidityValue(rs.getLong("bonus_validity_value"));
                    cardGroupDetailsVO.setOnline(rs.getString("online_offline"));
                    cardGroupDetailsVO.setBoth(rs.getString("both"));
                    cardGroupDetailsVO.setSenderConvFactor(rs.getString("sender_mult_factor"));
                    cardGroupDetailsVO.setReceiverConvFactor(rs.getString("receiver_mult_factor"));

                    // added by gaurav for cos
                    if (SystemPreferences.COS_REQUIRED) {
                        cardGroupDetailsVO.setCosRequired(rs.getString("cos_required"));
                        if (BTSLUtil.isNullString(cardGroupDetailsVO.getCosRequired()) || cardGroupDetailsVO.getCosRequired().equalsIgnoreCase("N"))
                            cardGroupDetailsVO.setCosRequired("N");
                        else
                            cardGroupDetailsVO.setCosRequired("Y");
                    }
                    if (SystemPreferences.IN_PROMO_REQUIRED)
                        cardGroupDetailsVO.setInPromo(rs.getDouble("in_promo"));

                    // Set the Bonus Accounts associated with the card group.
                    cardGroupDetailsVO.setBonusAccList(bonusBundleDAO.loadBonusAccDetails(p_con, cardGroupDetailsVO));
                } else
                    throw new BTSLBaseException("CardGroupDAO", "loadCardGroupDetails", SelfTopUpErrorCodesI.CARD_GROUP_VALUE_NOT_IN_RANGE, 0, new String[] { PretupsBL.getDisplayAmount(p_requestAmount) }, null);
            } else {
                throw new BTSLBaseException("CardGroupDAO", "loadCardGroupDetails", SelfTopUpErrorCodesI.CARD_GROUP_SETVERNOT_ASSOCIATED);
            }
            return cardGroupDetailsVO;
        }// end of try
        catch (BTSLBaseException bex) {
            _log.error("loadCardGroupDetails", "BTSLBaseException " + bex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CardGroupDAO[loadCardGroupDetails]", "", "", "", "Base Exception:" + bex.getMessage());
            throw bex;
        } catch (SQLException sqle) {
            _log.error("loadCardGroupDetails", "SQLException " + sqle.getMessage());
            _log.errorTrace("loadCardGroupDetails: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadCardGroupDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadCardGroupDetails", "Exception " + e.getMessage());
            _log.errorTrace("loadCardGroupDetails: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadCardGroupDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadCardGroupDetails", "Exiting CardGroupDetailsVO:" + cardGroupDetailsVO);
        }// end of finally
    }

    /**
     * Method to get the applicable card group set based on the network and
     * applicable dates
     * 
     * @param p_con
     * @param p_cardGroupSetID
     *            String
     * @param p_applicableDate
     *            Date
     * 
     * @return Card group set ID String
     * @throws BTSLBaseException
     */
    public String loadCardGroupSetVersionLatestVersion(Connection p_con, String p_cardGroupSetID, Date p_applicableDate) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadCardGroupSetVersionLatestVersion", "Entered p_cardGroupSetID=" + p_cardGroupSetID + " p_applicableDate=" + p_applicableDate);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        String latestCardGroupVersion = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT version ");
            selectQueryBuff.append(" FROM card_group_set_versions cgd ");
            selectQueryBuff.append(" WHERE cgd.card_group_set_id=? ");
            selectQueryBuff.append(" AND applicable_from =(SELECT MAX(applicable_from) ");
            selectQueryBuff.append(" FROM card_group_set_versions ");
            selectQueryBuff.append(" WHERE  applicable_from<=? AND card_group_set_id=cgd.card_group_set_id) ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadCardGroupSetVersionLatestVersion", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_cardGroupSetID);
            pstmtSelect.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_applicableDate));
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                latestCardGroupVersion = rs.getString("version");
            } else
                throw new BTSLBaseException("CardGroupDAO", "loadCardGroupSetVersionLatestVersion", SelfTopUpErrorCodesI.CARD_GROUP_SETVERNOT_ASSOCIATED);
            return latestCardGroupVersion;
        }// end of try
        catch (BTSLBaseException bex) {
            _log.error("loadCardGroupSetVersionLatestVersion", "BTSLBaseException " + bex.getMessage());
            throw bex;
        } catch (SQLException sqle) {
            _log.error("loadCardGroupSetVersionLatestVersion", "SQLException " + sqle.getMessage());
            _log.errorTrace("loadCardGroupSetVersionLatestVersion: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetVersionLatestVersion]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadCardGroupSetVersionLatestVersion", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadCardGroupSetVersionLatestVersion", "Exception " + e.getMessage());
            _log.errorTrace("loadCardGroupSetVersionLatestVersion: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetVersionLatestVersion]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadCardGroupSetVersionLatestVersion", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadCardGroupSetVersionLatestVersion", "Exiting latestCardGroupVersion:" + latestCardGroupVersion);
        }// end of finally
    }

    /**
     * Method for inserting Card Group Set.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_groupSetVO
     *            CardGroupSetVO
     * @return updateCount int
     * @exception BTSLBaseException
     */
    public int addCardGroupSet(Connection p_con, CardGroupSetVO p_groupSetVO) throws BTSLBaseException {
        // commented for DB2 OraclePreparedStatement psmtInsert = null;
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        if (_log.isDebugEnabled()) {
            _log.debug("addCardGroupSet", "Entered: p_groupSetVO= " + p_groupSetVO);
        }
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("INSERT INTO card_group_set (card_group_set_id,");
            strBuff.append("card_group_set_name,network_code,created_on,created_by,");
            strBuff.append("modified_on,modified_by,last_version,module_code,status,sub_service,service_type,set_type,is_default) values ");
            strBuff.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("addCardGroupSet", "Query sqlInsert:" + insertQuery);
            }

            // commented for DB2 psmtInsert =
            // (OraclePreparedStatement)p_con.prepareStatement(insertQuery);
            psmtInsert = (PreparedStatement) p_con.prepareStatement(insertQuery);
            psmtInsert.setString(1, p_groupSetVO.getCardGroupSetID());
            // commented for DB2 psmtInsert.setFormOfUse(2,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtInsert.setString(2, p_groupSetVO.getCardGroupSetName());
            psmtInsert.setString(3, p_groupSetVO.getNetworkCode());
            psmtInsert.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_groupSetVO.getCreatedOn()));
            psmtInsert.setString(5, p_groupSetVO.getCreatedBy());
            psmtInsert.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(p_groupSetVO.getModifiedOn()));
            psmtInsert.setString(7, p_groupSetVO.getModifiedBy());
            psmtInsert.setString(8, p_groupSetVO.getLastVersion());
            psmtInsert.setString(9, p_groupSetVO.getModuleCode());
            psmtInsert.setString(10, p_groupSetVO.getStatus());
            psmtInsert.setString(11, p_groupSetVO.getSubServiceType());
            psmtInsert.setString(12, p_groupSetVO.getServiceType());
            psmtInsert.setString(13, p_groupSetVO.getSetType());
            psmtInsert.setString(14, p_groupSetVO.getDefaultCardGroup());
            insertCount = psmtInsert.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            _log.error("addCardGroupSet", "SQLException: " + sqle.getMessage());
            _log.errorTrace("addCardGroupSet: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[addCardGroupSet]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addCardGroupSet", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("addCardGroupSet", "Exception: " + e.getMessage());
            _log.errorTrace("addCardGroupSet: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[addCardGroupSet]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addCardGroupSet", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("addCardGroupSet", "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method for inserting Card Group Set Version.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_cardGroupSetVO
     *            CardGroupSetVO
     * @return updateCount int
     * @exception BTSLBaseException
     */
    public int addCardGroupSetVersion(Connection p_con, CardGroupSetVersionVO p_cardGroupSetVO) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        if (_log.isDebugEnabled()) {
            _log.debug("addCardGroupSetVersion", "Entered: p_cardGroupSetVO= " + p_cardGroupSetVO);
        }

        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("INSERT INTO card_group_set_versions (card_group_set_id,");
            strBuff.append("version,applicable_from,created_by,created_on,modified_by,modified_on) ");
            strBuff.append(" values (?,?,?,?,?,?,?)");

            String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("addCardGroupSetVersion", "Query sqlInsert:" + insertQuery);
            }

            psmtInsert = p_con.prepareStatement(insertQuery);
            psmtInsert.setString(1, p_cardGroupSetVO.getCardGroupSetID());
            psmtInsert.setString(2, p_cardGroupSetVO.getVersion());
            psmtInsert.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_cardGroupSetVO.getApplicableFrom()));
            psmtInsert.setString(4, p_cardGroupSetVO.getCreatedBy());
            psmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(p_cardGroupSetVO.getCreadtedOn()));
            psmtInsert.setString(6, p_cardGroupSetVO.getModifiedBy());
            psmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(p_cardGroupSetVO.getModifiedOn()));

            insertCount = psmtInsert.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            _log.error("addCardGroupSetVersion", "SQLException: " + sqle.getMessage());
            _log.errorTrace("addCardGroupSetVersion: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[addCardGroupSetVersion]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addCardGroupSetVersion", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("addCardGroupSetVersion", "Exception: " + e.getMessage());
            _log.errorTrace("addCardGroupSetVersion: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[addCardGroupSetVersion]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addCardGroupSetVersion", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("addCardGroupSetVersion", "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method for inserting Card Group Details.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_detailVOList
     *            ArrayList
     * @return updateCount int
     * @exception BTSLBaseException
     */
    public int addCardGroupDetails(Connection p_con, ArrayList p_detailVOList) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        BonusBundleDAO bonusBundleDAO = null;

        if (_log.isDebugEnabled()) {
            _log.debug("addCardGroupDetails", "Entered: Inserted p_detailVOList Size= " + p_detailVOList.size());
        }

        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("INSERT INTO card_group_details (card_group_set_id,");
            strBuff.append("version,card_group_id,start_range,end_range,validity_period_type,");
            strBuff.append("validity_period,grace_period,sender_tax1_name,sender_tax1_type, ");
            strBuff.append("sender_tax1_rate,sender_tax2_name,sender_tax2_type,sender_tax2_rate,");
            strBuff.append("receiver_tax1_name,receiver_tax1_type,receiver_tax1_rate,");
            strBuff.append("receiver_tax2_name,receiver_tax2_type,receiver_tax2_rate,");
            strBuff.append("sender_access_fee_type,sender_access_fee_rate,min_sender_access_fee,");
            strBuff.append("max_sender_access_fee,receiver_access_fee_type,receiver_access_fee_rate,");
            strBuff.append("min_receiver_access_fee,max_receiver_access_fee,card_group_code,multiple_of,bonus_validity_value, ");
            strBuff.append("online_offline,both,sender_mult_factor,receiver_mult_factor,status,cos_required,in_promo )");
            strBuff.append(" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            String insertQuery = strBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("addCardGroupDetails", "Query sqlInsert:" + insertQuery);
            }

            psmtInsert = p_con.prepareStatement(insertQuery);
            CardGroupDetailsVO detailVO = null;
            bonusBundleDAO = new BonusBundleDAO();

            for (int i = 0, j = p_detailVOList.size(); i < j; i++) {
                detailVO = (CardGroupDetailsVO) p_detailVOList.get(i);

                psmtInsert.setString(1, detailVO.getCardGroupSetID());
                psmtInsert.setString(2, detailVO.getVersion());
                psmtInsert.setString(3, detailVO.getCardGroupID());
                psmtInsert.setLong(4, detailVO.getStartRange());
                psmtInsert.setLong(5, detailVO.getEndRange());
                psmtInsert.setString(6, detailVO.getValidityPeriodType());
                psmtInsert.setInt(7, detailVO.getValidityPeriod());
                psmtInsert.setLong(8, detailVO.getGracePeriod());
                psmtInsert.setString(9, detailVO.getSenderTax1Name());
                psmtInsert.setString(10, detailVO.getSenderTax1Type());
                psmtInsert.setDouble(11, detailVO.getSenderTax1Rate());
                psmtInsert.setString(12, detailVO.getSenderTax2Name());
                psmtInsert.setString(13, detailVO.getSenderTax2Type());
                psmtInsert.setDouble(14, detailVO.getSenderTax2Rate());
                psmtInsert.setString(15, detailVO.getReceiverTax1Name());
                psmtInsert.setString(16, detailVO.getReceiverTax1Type());
                psmtInsert.setDouble(17, detailVO.getReceiverTax1Rate());
                psmtInsert.setString(18, detailVO.getReceiverTax2Name());
                psmtInsert.setString(19, detailVO.getReceiverTax2Type());
                psmtInsert.setDouble(20, detailVO.getReceiverTax2Rate());
                psmtInsert.setString(21, detailVO.getSenderAccessFeeType());
                psmtInsert.setDouble(22, detailVO.getSenderAccessFeeRate());
                psmtInsert.setLong(23, detailVO.getMinSenderAccessFee());
                psmtInsert.setLong(24, detailVO.getMaxSenderAccessFee());
                psmtInsert.setString(25, detailVO.getReceiverAccessFeeType());
                psmtInsert.setDouble(26, detailVO.getReceiverAccessFeeRate());
                psmtInsert.setLong(27, detailVO.getMinReceiverAccessFee());
                psmtInsert.setLong(28, detailVO.getMaxReceiverAccessFee());
                psmtInsert.setString(29, detailVO.getCardGroupCode());
                psmtInsert.setLong(30, detailVO.getMultipleOf());
                psmtInsert.setLong(31, detailVO.getBonusValidityValue());
                psmtInsert.setString(32, detailVO.getOnline());
                psmtInsert.setString(33, detailVO.getBoth());
                if (detailVO.getSenderConvFactor() == null)
                    detailVO.setSenderConvFactor("1");
                psmtInsert.setDouble(34, Double.parseDouble(detailVO.getSenderConvFactor()));
                psmtInsert.setDouble(35, Double.parseDouble(detailVO.getReceiverConvFactor()));
                psmtInsert.setString(36, detailVO.getStatus());

                psmtInsert.setString(37, detailVO.getCosRequired());
                // added for in promo
                psmtInsert.setDouble(38, detailVO.getInPromo());

                insertCount = psmtInsert.executeUpdate();

                psmtInsert.clearParameters();
                // check the status of the insert
                if (insertCount <= 0)
                    throw new BTSLBaseException(this, "addCardGroupDetails", "error.general.sql.processing");

                bonusBundleDAO = new BonusBundleDAO();
                bonusBundleDAO.addBonusBundleDetails(p_con, detailVO);
            }

        } // end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error("addCardGroupDetails", "SQLException: " + sqle.getMessage());
            _log.errorTrace("addCardGroupDetails: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[addCardGroupDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addCardGroupDetails", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("addCardGroupDetails", "Exception: " + e.getMessage());
            _log.errorTrace("addCardGroupDetails: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[addCardGroupDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addCardGroupDetails", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("addCardGroupDetails", "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method for loading Card Group Sets.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_moduleCode
     *            String
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadCardGroupSet(Connection p_con, String p_networkCode, String p_moduleCode) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadCardGroupSet()", "Entered p_networkCode=" + p_networkCode + " p_moduleCode=" + p_moduleCode);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;

        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT l.lookup_name set_name,cs.set_type,cs.card_group_set_id,cs.card_group_set_name, ");
        strBuff.append(" cs.network_code, cs.created_on,cs.created_by, cs.modified_on,cs.modified_by, ");
        strBuff.append(" cs.last_version, cs.module_code, cs.status, cs.language_1_message, cs.language_2_message, ");
        strBuff.append(" cs.sub_service, stsm.selector_name, cs.service_type,cs.is_default, st.name service_name ");
        strBuff.append(" FROM card_group_set cs, service_type st,service_type_selector_mapping stsm, lookups l ");
        strBuff.append(" WHERE cs.network_code = ? AND cs.module_code = ? ANd cs.status <> 'N' ");
        strBuff.append(" AND cs.sub_service=stsm.selector_code AND cs.service_type=st.service_type ");
        strBuff.append(" AND stsm.service_type=cs.service_type AND l.lookup_type=? ");
        strBuff.append(" AND l.lookup_code=cs.set_type ORDER BY cs.card_group_set_name");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadCardGroupSet()", "QUERY sqlSelect=" + sqlSelect);
        }

        ArrayList list = new ArrayList();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, p_moduleCode);
            pstmtSelect.setString(3, PretupsI.CARD_GROUP_SET_TYPE);
            rs = pstmtSelect.executeQuery();
            CardGroupSetVO cardGroupSetVO = null;
            while (rs.next()) {
                cardGroupSetVO = new CardGroupSetVO();
                cardGroupSetVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                cardGroupSetVO.setCardGroupSetName(rs.getString("card_group_set_name"));
                cardGroupSetVO.setNetworkCode(rs.getString("network_code"));
                cardGroupSetVO.setCreatedBy(rs.getString("created_by"));
                cardGroupSetVO.setModifiedBy(rs.getString("modified_by"));
                cardGroupSetVO.setCreatedOn(rs.getDate("created_on"));
                cardGroupSetVO.setModifiedOn(rs.getDate("modified_on"));
                cardGroupSetVO.setLastModifiedOn((rs.getTimestamp("modified_on").getTime()));
                cardGroupSetVO.setLastVersion(rs.getString("last_version"));
                cardGroupSetVO.setModuleCode(rs.getString("module_code"));
                cardGroupSetVO.setStatus(rs.getString("status"));
                cardGroupSetVO.setLanguage1Message(rs.getString("language_1_message"));
                cardGroupSetVO.setLanguage2Message(rs.getString("language_2_message"));
                cardGroupSetVO.setSubServiceType(rs.getString("sub_service"));
                cardGroupSetVO.setSubServiceTypeDescription(rs.getString("selector_name"));
                cardGroupSetVO.setServiceType(rs.getString("service_type"));
                cardGroupSetVO.setServiceTypeDesc(rs.getString("service_name"));
                cardGroupSetVO.setSetType(rs.getString("set_type"));
                cardGroupSetVO.setSetTypeName(rs.getString("set_name"));
                cardGroupSetVO.setDefaultCardGroup(rs.getString("is_default"));
                list.add(cardGroupSetVO);
            }

        } catch (SQLException sqe) {
            _log.error("loadCardGroupSet()", "SQLException : " + sqe);
            _log.errorTrace("loadCardGroupSet: Exception print stack trace:", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSet]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCardGroupSet", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadCardGroupSet()", "Exception : " + ex);
            _log.errorTrace("loadCardGroupSet: Exception print stack trace:", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSet]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCardGroupSet", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadCardGroupSet", "Exiting: cardGroupSet size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading Card Group Set Versions.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_currentDate
     *            java.util.Date
     * @param p_moduleCode
     *            String
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadCardGroupSetVersion(Connection p_con, String p_networkCode, Date p_currentDate, String p_moduleCode) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadCardGroupSetVersion()", "Entered p_networkCode=" + p_networkCode + " currentDate:" + p_currentDate + " p_moduleCode=" + p_moduleCode);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;

        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT cv.card_group_set_id,cv.version,applicable_from");
        strBuff.append(" FROM card_group_set cg,card_group_set_versions cv WHERE cg.network_code = ? ");
        strBuff.append(" AND cg.card_group_set_id = cv.card_group_set_id AND cg.module_code = ? AND ");
        strBuff.append(" (cv.applicable_from >= ? OR cv.applicable_from =(SELECT MAX(cv2.applicable_from) ");
        strBuff.append(" from card_group_set_versions cv2 WHERE cg.card_group_set_id = cv2.card_group_set_id ))");
        strBuff.append(" AND cg.status <> 'N' ORDER BY version");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadCardGroupSetVersion()", "QUERY sqlSelect=" + sqlSelect);
        }

        ArrayList list = new ArrayList();

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, p_moduleCode);
            pstmtSelect.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_currentDate));

            rs = pstmtSelect.executeQuery();

            CardGroupSetVersionVO cardGroupSetVersionVO = null;

            while (rs.next()) {

                cardGroupSetVersionVO = new CardGroupSetVersionVO();
                cardGroupSetVersionVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                cardGroupSetVersionVO.setVersion(rs.getString("version"));
                cardGroupSetVersionVO.setApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                cardGroupSetVersionVO.setOldApplicableFrom(rs.getTimestamp("applicable_from").getTime());

                list.add(cardGroupSetVersionVO);
            }

        } catch (SQLException sqe) {
            _log.error("loadCardGroupSetVersion()", "SQLException : " + sqe);
            _log.errorTrace("loadCardGroupSetVersion: Exception print stack trace:", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetVersion]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCardGroupSetVersion()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadCardGroupSetVersion()", "Exception : " + ex);
            _log.errorTrace("loadCardGroupSetVersion: Exception print stack trace:", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetVersion]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCardGroupSetVersion()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled()) {
                _log.debug("loadCardGroupSetVersion()", "Exiting: cardGroupSetVersion size=" + list.size());
            }
        }
        return list;
    }

    /**
     * This method loads the Card group details on the basis of
     * Card_Group_Set_Id
     * 
     * @param p_con
     * @param p_cardGroupSetId
     *            String
     * @param p_version
     *            String
     * @return cardGroupDetailList ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCardGroupDetailsListByID(Connection p_con, String p_cardGroupSetID, String p_version) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadCardGroupDetailsListByID", "Entered p_cardGroupSetID=" + p_cardGroupSetID + " p_version=" + p_version);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList cardGroupDetailList = new ArrayList();
        CardGroupDetailsVO cardGroupDetailsVO = null;
        // Added to load the bonus accounts
        BonusBundleDAO bonusBundleDAO = null;
        try {

            StringBuffer qry = new StringBuffer(" SELECT l2.lookup_name set_name, cs.set_type, cd.card_group_set_id, ");
            qry.append(" cd.card_group_id, cd.card_group_code, cd.start_range,cd.end_range,cd.validity_period_type, ");
            qry.append(" cd.validity_period,cd.grace_period, cd.sender_tax1_name, cd.sender_tax1_type, ");
            qry.append(" cd.sender_tax1_rate, cd.sender_tax2_name, cd.sender_tax2_type, cd.sender_tax2_rate, ");
            qry.append(" cd.receiver_tax1_name, cd.receiver_tax1_type, cd.receiver_tax1_rate, cd.receiver_tax2_name, ");
            qry.append(" cd.receiver_tax2_type, cd.receiver_tax2_rate,");
            qry.append("  cd.sender_access_fee_type, cd.sender_access_fee_rate, ");
            qry.append(" cd.min_sender_access_fee, cd.max_sender_access_fee,  cd.receiver_access_fee_type, ");
            qry.append(" cd.receiver_access_fee_rate, cd.min_receiver_access_fee, cd.max_receiver_access_fee, ");
            qry.append(" cd.multiple_of, cs.service_type, st.name service_name, cs.card_group_set_name, cs.sub_service, ");
            qry.append(" stsm.selector_name, cd.status, cd.bonus_validity_value, cd.online_offline,cd.both, ");
            qry.append(" cd.sender_mult_factor, cd.receiver_mult_factor, cd.cos_required , cd.in_promo ");
            qry.append(" FROM card_group_details cd,card_group_set cs,service_type st, ");
            qry.append(" lookups l2, service_type_selector_mapping stsm ");
            qry.append(" WHERE cs.status<>'N' AND st.status<>'N' AND stsm.status<>'N' AND cd.card_group_set_id=cs.card_group_set_id ");
            qry.append(" AND cd.card_group_set_id=? AND cd.version = ? AND cs.service_type=st.service_type ");
            qry.append(" AND l2.lookup_type=? AND l2.lookup_code=cs.set_type AND stsm.service_type=cs.service_type ");
            qry.append(" AND stsm.selector_code=cs.sub_service");
            String selectQuery = qry.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadCardGroupDetailsListByID", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_cardGroupSetID);
            pstmtSelect.setString(2, p_version);
            pstmtSelect.setString(3, PretupsI.CARD_GROUP_SET_TYPE);
            rs = pstmtSelect.executeQuery();

            bonusBundleDAO = new BonusBundleDAO();
            while (rs.next()) {
                cardGroupDetailsVO = new CardGroupDetailsVO();
                cardGroupDetailsVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                cardGroupDetailsVO.setCardGroupID(rs.getString("card_group_id"));
                cardGroupDetailsVO.setCardGroupCode(rs.getString("card_group_code"));
                cardGroupDetailsVO.setVersion("version");
                cardGroupDetailsVO.setStartRange(rs.getLong("start_range"));
                cardGroupDetailsVO.setEndRange(rs.getLong("end_range"));
                cardGroupDetailsVO.setValidityPeriodType(rs.getString("validity_period_type"));
                cardGroupDetailsVO.setValidityPeriod(rs.getInt("validity_period"));
                cardGroupDetailsVO.setGracePeriod(rs.getLong("grace_period"));
                cardGroupDetailsVO.setSenderTax1Name(rs.getString("sender_tax1_name"));
                cardGroupDetailsVO.setSenderTax1Type(rs.getString("sender_tax1_type"));
                cardGroupDetailsVO.setSenderTax1Rate(rs.getDouble("sender_tax1_rate"));
                cardGroupDetailsVO.setSenderTax2Name(rs.getString("sender_tax2_name"));
                cardGroupDetailsVO.setSenderTax2Type(rs.getString("sender_tax2_type"));
                cardGroupDetailsVO.setSenderTax2Rate(rs.getDouble("sender_tax2_rate"));
                cardGroupDetailsVO.setReceiverTax1Name(rs.getString("receiver_tax1_name"));
                cardGroupDetailsVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
                cardGroupDetailsVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                cardGroupDetailsVO.setReceiverTax2Name(rs.getString("receiver_tax2_name"));
                cardGroupDetailsVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
                cardGroupDetailsVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                cardGroupDetailsVO.setSenderAccessFeeType(rs.getString("sender_access_fee_type"));
                cardGroupDetailsVO.setSenderAccessFeeRate(rs.getDouble("sender_access_fee_rate"));
                cardGroupDetailsVO.setMinSenderAccessFee(rs.getLong("min_sender_access_fee"));
                cardGroupDetailsVO.setMaxSenderAccessFee(rs.getLong("max_sender_access_fee"));
                cardGroupDetailsVO.setReceiverAccessFeeType(rs.getString("receiver_access_fee_type"));
                cardGroupDetailsVO.setReceiverAccessFeeRate(rs.getDouble("receiver_access_fee_rate"));
                cardGroupDetailsVO.setMinReceiverAccessFee(rs.getLong("min_receiver_access_fee"));
                cardGroupDetailsVO.setMaxReceiverAccessFee(rs.getLong("max_receiver_access_fee"));
                cardGroupDetailsVO.setMultipleOf(rs.getLong("multiple_of"));
                cardGroupDetailsVO.setCardGroupSetName(rs.getString("card_group_set_name"));
                cardGroupDetailsVO.setCardGroupSubServiceId(rs.getString("sub_service"));
                cardGroupDetailsVO.setServiceTypeId(rs.getString("service_type"));
                cardGroupDetailsVO.setServiceTypeDesc(rs.getString("service_name"));
                cardGroupDetailsVO.setSetType(rs.getString("set_type"));
                cardGroupDetailsVO.setSetTypeName(rs.getString("set_name"));
                cardGroupDetailsVO.setCardGroupSubServiceIdDesc("selector_name");
                cardGroupDetailsVO.setStatus(rs.getString("status"));// added
                                                                     // for slab
                                                                     // suspend/resume
                // added by vikas kumar for card group updation
                cardGroupDetailsVO.setBonusValidityValue(rs.getLong("bonus_validity_value"));
                cardGroupDetailsVO.setOnline(rs.getString("online_offline"));
                cardGroupDetailsVO.setBoth(rs.getString("both"));
                cardGroupDetailsVO.setSenderConvFactor(rs.getString("sender_mult_factor"));
                cardGroupDetailsVO.setReceiverConvFactor(rs.getString("receiver_mult_factor"));
                // added by gaurav for cos required
                if (SystemPreferences.COS_REQUIRED)
                    cardGroupDetailsVO.setCosRequired(rs.getString("cos_required"));

                if (SystemPreferences.IN_PROMO_REQUIRED)
                    cardGroupDetailsVO.setInPromo(rs.getDouble("in_promo"));
                // Now load the bonuses defined for the card group.
                cardGroupDetailsVO.setBonusAccList(bonusBundleDAO.loadBonusAccDetailsListByID(p_con, p_cardGroupSetID, p_version, cardGroupDetailsVO.getCardGroupID()));

                cardGroupDetailList.add(cardGroupDetailsVO);
            }
            return cardGroupDetailList;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadCardGroupDetailsListByID", "SQLException " + sqle.getMessage());
            _log.errorTrace("loadCardGroupDetailsListByID: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupDetailsListByID]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadCardGroupDetailsListByID", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadCardGroupDetailsListByID", "Exception " + e.getMessage());
            _log.errorTrace("loadCardGroupDetailsListByID: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupDetailsListByID]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadCardGroupDetailsListByID", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadCardGroupDetailsListByID", "Exiting CardGroupDetailList Size:" + cardGroupDetailList.size());
        }// end of finally
    }

    /**
     * Method for checking Card Group Set Name is already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_cardGroupSetName
     *            String
     * @param p_cardGroupSetId
     *            String
     * 
     * @return flag boolean
     * @exception BTSLBaseException
     */
    public boolean isCardGroupSetNameExist(Connection p_con, String p_networkCode, String p_cardGroupSetName, String p_cardGroupSetId) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("isCardGroupSetNameExist", "Entered: p_networkCode=" + p_networkCode + " cardGroupSetName=" + p_cardGroupSetName + " p_cardGroupSetId=" + p_cardGroupSetId);
        }

        // commented for DB2 OraclePreparedStatement pstmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        StringBuffer strBuff = new StringBuffer();

        if (BTSLUtil.isNullString(p_cardGroupSetId)) {
            strBuff.append("SELECT card_group_set_name FROM card_group_set WHERE network_code = ?");
            strBuff.append(" AND upper(card_group_set_name) = upper(?)");
        } else {
            strBuff.append("SELECT card_group_set_name FROM card_group_set WHERE network_code = ?");
            strBuff.append(" AND card_group_set_id != ? AND upper(card_group_set_name) = upper(?)");
        }
        String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("isCardGroupSetNameExist", "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            // commented for DB2 pstmt =
            // (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
            pstmt = (PreparedStatement) p_con.prepareStatement(sqlSelect);
            if (BTSLUtil.isNullString(p_cardGroupSetId)) {
                pstmt.setString(1, p_networkCode);
                // commented for DB2 pstmt.setFormOfUse(2,
                // OraclePreparedStatement.FORM_NCHAR);
                pstmt.setString(2, p_cardGroupSetName);
            } else {
                pstmt.setString(1, p_networkCode);
                pstmt.setString(2, p_cardGroupSetId);
                // commented for DB2pstmt.setFormOfUse(3,
                // OraclePreparedStatement.FORM_NCHAR);
                pstmt.setString(3, p_cardGroupSetName);
            }
            rs = pstmt.executeQuery();

            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } catch (SQLException sqe) {
            _log.error("isCardGroupSetNameExist", "SQLException : " + sqe);
            _log.errorTrace("isCardGroupSetNameExist: Exception print stack trace:", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[isCardGroupSetNameExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isCardGroupSetNameExist", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("isCardGroupSetNameExist", "Exception : " + ex);
            _log.errorTrace("isCardGroupSetNameExist: Exception print stack trace:", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[isCardGroupSetNameExist]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "isCardGroupSetNameExist", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled()) {
                _log.debug("isCardGroupSetNameExist", "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Method for checking CardGroup Set is already exist with the same
     * applicable date of the same set id.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_applicableDate
     *            Date
     * @param p_setId
     *            String
     * @param p_version
     *            String
     * 
     * @return flag boolean
     * @exception BTSLBaseException
     */
    public boolean isCardGroupAlreadyExist(Connection p_con, Date p_applicableDate, String p_setId, String p_version) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("isCardGroupAlreadyExist", "Entered: p_applicableDate=" + p_applicableDate + " p_setId=" + p_setId);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append("SELECT card_group_set_id FROM card_group_set_versions ");
        strBuff.append("WHERE applicable_from = ? AND card_group_set_id = ? AND version != ?");

        String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("isCardGroupAlreadyExist", "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(p_applicableDate));
            pstmt.setString(2, p_setId);
            pstmt.setString(3, p_version);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } catch (SQLException sqe) {
            _log.error("isCardGroupAlreadyExist", "SQLException : " + sqe);
            _log.errorTrace("isCardGroupAlreadyExist: Exception print stack trace:", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[isCardGroupAlreadyExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isCardGroupAlreadyExist", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("isCardGroupAlreadyExist", "Exception : " + ex);
            _log.errorTrace("isCardGroupAlreadyExist: Exception print stack trace:", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[isCardGroupAlreadyExist]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "isCardGroupAlreadyExist", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("isCardGroupAlreadyExist", "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Method for Updating Card Group Set(only update the version).
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_cardGroupSetVO
     *            CardGroupSetVO
     * 
     * @return updateCount int
     * @exception BTSLBaseException
     */
    public int updateCardGroupSet(Connection p_con, CardGroupSetVO p_cardGroupSetVO) throws BTSLBaseException {

        // commented for DB2 OraclePreparedStatement psmtUpdate = null;
        PreparedStatement psmtUpdate = null;
        int updateCount = 0;

        if (_log.isDebugEnabled()) {
            _log.debug("updateCardGroupSet", "Entered: p_cardGroupSetVO=" + p_cardGroupSetVO);
        }

        try {
            StringBuffer strBuff = new StringBuffer();

            strBuff.append("UPDATE card_group_set SET card_group_set_name = ? ,last_version = ?, ");
            strBuff.append(" modified_on = ?, modified_by = ?,set_type=? WHERE card_group_set_id = ?");

            String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("updateCardGroupSet", "Query sqlInsert:" + insertQuery);

            // commented for DB2psmtUpdate =
            // (OraclePreparedStatement)p_con.prepareStatement(insertQuery);
            psmtUpdate = (PreparedStatement) p_con.prepareStatement(insertQuery);
            // commented for DB2 psmtUpdate.setFormOfUse(1,
            // OraclePreparedStatement.FORM_NCHAR);
            psmtUpdate.setString(1, p_cardGroupSetVO.getCardGroupSetName());

            psmtUpdate.setString(2, p_cardGroupSetVO.getLastVersion());
            psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_cardGroupSetVO.getModifiedOn()));
            psmtUpdate.setString(4, p_cardGroupSetVO.getModifiedBy());
            // psmtUpdate.setString(5,p_cardGroupSetVO .getSubServiceType());
            psmtUpdate.setString(5, p_cardGroupSetVO.getSetType());
            psmtUpdate.setString(6, p_cardGroupSetVO.getCardGroupSetID());

            boolean modified = this.recordModified(p_con, p_cardGroupSetVO.getCardGroupSetID(), p_cardGroupSetVO.getLastModifiedOn());

            // if modified = true mens record modified by another user
            if (modified)
                throw new BTSLBaseException("error.modified");

            updateCount = psmtUpdate.executeUpdate();

        } // end of try
        catch (BTSLBaseException be) {
            _log.error("updateCardGroupSet", "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error("updateCardGroupSet", "SQLException: " + sqle.getMessage());
            _log.errorTrace("updateCardGroupSet: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[updateCardGroupSet]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateCardGroupSet", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("updateCardGroupSet", "Exception: " + e.getMessage());
            _log.errorTrace("updateCardGroupSet: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[updateCardGroupSet]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateCardGroupSet", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled())
                _log.debug("updateCardGroupSet", "Exiting: updateCount=" + updateCount);
        } // end of finally

        return updateCount;
    }

    /**
     * Method for Updating Card Group Set Version.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_cardGroupSetVersionVO
     *            CardGroupSetVersionVO
     * 
     * @return updateCount int
     * @exception BTSLBaseException
     */
    public int updateCardGroupSetVersion(Connection p_con, CardGroupSetVersionVO p_cardGroupSetVersionVO) throws BTSLBaseException {

        PreparedStatement psmtUpdate = null;
        int updateCount = 0;

        if (_log.isDebugEnabled())
            _log.debug("updateCardGroupSetVersion", "Entered: p_cardGroupSetVersionVO=" + p_cardGroupSetVersionVO);

        try {
            StringBuffer strBuff = new StringBuffer();

            strBuff.append("UPDATE card_group_set_versions SET modified_on = ?, modified_by = ? ");
            strBuff.append("WHERE card_group_set_id = ? and version = ?");

            String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("updateCardGroupSetVersion", "Query sqlInsert:" + insertQuery);

            psmtUpdate = p_con.prepareStatement(insertQuery);

            psmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(p_cardGroupSetVersionVO.getModifiedOn()));
            psmtUpdate.setString(2, p_cardGroupSetVersionVO.getModifiedBy());
            psmtUpdate.setString(3, p_cardGroupSetVersionVO.getCardGroupSetID());
            psmtUpdate.setString(4, p_cardGroupSetVersionVO.getVersion());

            updateCount = psmtUpdate.executeUpdate();

        } // end of try
        catch (SQLException sqle) {
            _log.error("updateCardGroupSetVersion", "SQLException: " + sqle.getMessage());
            _log.errorTrace("updateCardGroupSetVersion: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[updateCardGroupSetVersion]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateCardGroupSetVersion", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("updateCardGroupSetVersion", "Exception: " + e.getMessage());
            _log.errorTrace("updateCardGroupSetVersion: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[updateCardGroupSetVersion]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateCardGroupSetVersion", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled())
                _log.debug("updateCardGroupSetVersion", "Exiting: updateCount=" + updateCount);
        } // end of finally

        return updateCount;
    }

    /**
     * This method is used to check whether the record in the database is
     * modified or not If there is any error then throws the SQLException
     * 
     * @param con
     *            Connection
     * @param p_cardGroupSetID
     *            String
     * @param oldLastModified
     *            long
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean recordModified(Connection con, String p_cardGroupSetID, long oldLastModified) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("recordModified", "Entered: p_cardGroupSetID= " + p_cardGroupSetID + "oldLastModified= " + oldLastModified);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        String sqlRecordModified = "SELECT modified_on FROM card_group_set WHERE card_group_set_id = ?";
        Timestamp newLastModified = null;
        if ((oldLastModified) == 0) {
            return false;
        }
        try {
            if (_log.isDebugEnabled())
                _log.debug("recordModified", "QUERY: sqlselect= " + sqlRecordModified);
            // create a prepared statement and execute it
            pstmt = con.prepareStatement(sqlRecordModified);
            pstmt.setString(1, p_cardGroupSetID);
            rs = pstmt.executeQuery();
            if (rs.next())
                newLastModified = rs.getTimestamp("modified_on");
            if (_log.isDebugEnabled()) {
                _log.debug("recordModified", " old=" + oldLastModified);
                _log.debug("recordModified", " new=" + newLastModified.getTime());
            }
            if (newLastModified.getTime() != oldLastModified)
                modified = true;

            return modified;
        } // end of try
        catch (SQLException sqle) {
            _log.error("recordModified", "SQLException: " + sqle.getMessage());
            _log.errorTrace("updateCardGroupSetVersion: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[recordModified]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "recordModified", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("recordModified", "Exception: " + e.getMessage());
            _log.errorTrace("recordModified: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[recordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "recordModified", "error.general.processing");
        } // end of catch

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled())
                _log.debug("recordModified", "Exititng: modified=" + modified);
        } // end of finally
    }

    /**
     * Method for Deleting Card Group Details
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_cardGroupSetId
     *            String
     * @param p_version
     *            string
     * 
     * @return deleteCount int
     * @exception BTSLBaseException
     */
    public int deleteCardGroupDetails(Connection p_con, String p_cardGroupSetId, String p_version) throws BTSLBaseException {
        PreparedStatement psmtDelete = null;
        int deleteCount = 0;
        if (_log.isDebugEnabled())
            _log.debug("deleteCardGroupDetails", "Entered: cardGroupSetId=" + p_cardGroupSetId + " p_version= " + p_version);
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("DELETE FROM card_group_details ");
            strBuff.append("WHERE card_group_set_id = ? and version = ?");
            String deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("deleteCardGroupDetails", "Query sqlDelete:" + deleteQuery);
            }
            psmtDelete = p_con.prepareStatement(deleteQuery);
            psmtDelete.setString(1, p_cardGroupSetId);
            psmtDelete.setString(2, p_version);

            deleteCount = psmtDelete.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            _log.error("deleteCardGroupDetails", "SQLException: " + sqle.getMessage());
            _log.errorTrace("deleteCardGroupDetails: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[deleteCardGroupDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteCardGroupDetails", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("deleteCardGroupDetails", "Exception: " + e.getMessage());
            _log.errorTrace("deleteCardGroupDetails: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[deleteCardGroupDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteCardGroupDetails", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtDelete != null) {
                    psmtDelete.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("deleteCardGroupDetails", "Exiting: deleteCount=" + deleteCount);
            }
        } // end of finally

        return deleteCount;
    }

    /**
     * Method for Deleting Card Group Set(only update the status set status=N).
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_cardGroupSetVO
     *            CardGroupSetVO
     * 
     * @return updateCount int
     * @exception BTSLBaseException
     */
    public int deleteCardGroupSet(Connection p_con, CardGroupSetVO p_cardGroupSetVO) throws BTSLBaseException {
        PreparedStatement psmtDelete = null;
        int deleteCount = 0;
        if (_log.isDebugEnabled())
            _log.debug("deleteCardGroupSet", "Entered: p_cardGroupSetVO=" + p_cardGroupSetVO);
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("UPDATE card_group_set SET status = ? , modified_on = ?, modified_by = ? ");
            strBuff.append("WHERE card_group_set_id = ?");
            String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("deleteCardGroupSet", "Query sqlInsert:" + insertQuery);
            psmtDelete = p_con.prepareStatement(insertQuery);
            psmtDelete.setString(1, p_cardGroupSetVO.getStatus());
            psmtDelete.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_cardGroupSetVO.getModifiedOn()));
            psmtDelete.setString(3, p_cardGroupSetVO.getModifiedBy());
            psmtDelete.setString(4, p_cardGroupSetVO.getCardGroupSetID());
            deleteCount = psmtDelete.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            _log.error("deleteCardGroupSet", "SQLException: " + sqle.getMessage());
            _log.errorTrace("deleteCardGroupSet: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[deleteCardGroupSet]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteCardGroupSet", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("deleteCardGroupSet", "Exception: " + e.getMessage());
            _log.errorTrace("deleteCardGroupSet: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[deleteCardGroupSet]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteCardGroupSet", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtDelete != null) {
                    psmtDelete.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteCardGroupSet", "Exiting: deleteCount=" + deleteCount);
        } // end of finally
        return deleteCount;
    }

    /**
     * Method for update Card Group Set Table.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_voList
     *            java.util.ArrayList
     * 
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int suspendCardGroupSetList(Connection p_con, ArrayList p_voList) throws BTSLBaseException {
        // commented for DB2 OraclePreparedStatement psmtUpdate = null;
        PreparedStatement psmtUpdate = null;
        int updateCount = 0;

        if (_log.isDebugEnabled())
            _log.debug("suspendCardGroupSetList", "Entered: p_voList Size= " + p_voList.size());

        try {
            int listSize = 0;
            boolean modified = false;
            if (p_voList != null)
                listSize = p_voList.size();

            StringBuffer strBuff = new StringBuffer();

            strBuff.append("Update card_group_set SET status = ?, modified_by = ?, modified_on = ?,");
            strBuff.append("language_1_message = ?, language_2_message = ?");
            strBuff.append(" WHERE card_group_set_id = ?");

            String updateQuery = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("suspendCardGroupSetList", "Query sqlUpdate:" + updateQuery);

            // commented for DB2 psmtUpdate = (OraclePreparedStatement)
            // p_con.prepareStatement(updateQuery);
            psmtUpdate = (PreparedStatement) p_con.prepareStatement(updateQuery);
            CardGroupSetVO cardGroupSetVO = null;
            for (int i = 0; i < listSize; i++) {
                cardGroupSetVO = (CardGroupSetVO) p_voList.get(i);

                psmtUpdate.setString(1, cardGroupSetVO.getStatus());
                psmtUpdate.setString(2, cardGroupSetVO.getModifiedBy());
                psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(cardGroupSetVO.getModifiedOn()));
                psmtUpdate.setString(4, cardGroupSetVO.getLanguage1Message());

                // commented for DB2 psmtUpdate.setFormOfUse(5,
                // OraclePreparedStatement.FORM_NCHAR);
                psmtUpdate.setString(5, cardGroupSetVO.getLanguage2Message());

                psmtUpdate.setString(6, cardGroupSetVO.getCardGroupSetID());

                modified = this.recordModified(p_con, cardGroupSetVO.getCardGroupSetID(), cardGroupSetVO.getLastModifiedOn());

                // if modified = true mens record modified by another user
                if (modified)
                    throw new BTSLBaseException("error.modified");

                updateCount = psmtUpdate.executeUpdate();

                psmtUpdate.clearParameters();

                // check the status of the update
                if (updateCount <= 0)
                    throw new BTSLBaseException(this, "suspendCardGroupSetList", "error.general.sql.processing");
            }

        } // end of try
        catch (BTSLBaseException be) {
            _log.error("suspendCardGroupSetList", "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error("suspendCardGroupSetList", "SQLException: " + sqle.getMessage());
            _log.errorTrace("suspendCardGroupSetList: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[suspendCardGroupSetList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "suspendCardGroupSetList", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("suspendCardGroupSetList", "Exception: " + e.getMessage());
            _log.errorTrace("suspendCardGroupSetList: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[suspendCardGroupSetList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "suspendCardGroupSetList", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("suspendCardGroupSetList", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally

        return updateCount;
    }

    /**
     * Method load Card Group Set FOr TransferRule
     * 
     * @author vikas.yadav
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_moduleCode
     *            String
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadCardGroupSetForTransferRule(Connection p_con, String p_networkCode, String p_moduleCode, String p_set_type) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadCardGroupSetFOrTransferRule()", "Entered p_networkCode=" + p_networkCode + " p_moduleCode=" + p_moduleCode + "p_set_type=" + p_set_type);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;

        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT card_group_set_id,card_group_set_name,sub_service, service_type, set_type ");
        strBuff.append("FROM card_group_set ");
        strBuff.append("WHERE network_code =? AND module_code = ? AND status <> ? AND set_type = ? ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadCardGroupSetFOrTransferRule()", "QUERY sqlSelect=" + sqlSelect);
        ArrayList list = new ArrayList();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);

            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, p_moduleCode);
            pstmtSelect.setString(3, PretupsI.STATUS_DELETE);
            pstmtSelect.setString(4, p_set_type);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                list.add(new ListValueVO(rs.getString("card_group_set_name"), rs.getString("sub_service") + ":" + rs.getString("card_group_set_id") + ":" + rs.getString("service_type")));
            }

        } catch (SQLException sqe) {
            _log.error("loadCardGroupSetForTransferRule()", "SQLException : " + sqe);
            _log.errorTrace("loadCardGroupSetForTransferRule: Exception print stack trace:", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetForTransferRule]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCardGroupSetFOrTransferRule", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadCardGroupSetForTransferRule()", "Exception : " + ex);
            _log.errorTrace("loadCardGroupSetForTransferRule: Exception print stack trace:", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetForTransferRule]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCardGroupSetFOrTransferRule", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadCardGroupSetFOrTransferRule", "Exiting: cardGroupSet size=" + list.size());
        }
        return list;
    }

    /**
     * Method for loading Card Group Set Versions List.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_cardGroupSetId
     *            String
     * @param p_moduleCode
     *            String
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadCardGroupSetVersionList(Connection p_con, String p_cardGroupSetId, String p_moduleCode) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadCardGroupSetVersionList()", "Entered p_cardGroupSetId=" + p_cardGroupSetId + "p_moduleCode=" + p_moduleCode);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT cv.card_group_set_id,cv.version,applicable_from");
        strBuff.append(" FROM card_group_set cg,card_group_set_versions cv WHERE cg.card_group_set_id=? ");
        strBuff.append(" AND cg.card_group_set_id = cv.card_group_set_id AND cg.module_code = ? ");
        strBuff.append(" AND cg.status <> 'N' ORDER BY version");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadCardGroupSetVersionList()", "QUERY sqlSelect=" + sqlSelect);

        ArrayList list = new ArrayList();

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_cardGroupSetId);
            pstmtSelect.setString(2, p_moduleCode);
            rs = pstmtSelect.executeQuery();
            CardGroupSetVersionVO cardGroupSetVersionVO = null;
            while (rs.next()) {

                cardGroupSetVersionVO = new CardGroupSetVersionVO();
                cardGroupSetVersionVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                cardGroupSetVersionVO.setVersion(rs.getString("version"));
                cardGroupSetVersionVO.setApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                cardGroupSetVersionVO.setOldApplicableFrom(rs.getTimestamp("applicable_from").getTime());
                list.add(cardGroupSetVersionVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadCardGroupSetVersionList()", "SQLException : " + sqe);
            _log.errorTrace("loadCardGroupSetVersionList: Exception print stack trace:", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetVersionList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCardGroupSetVersionList()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadCardGroupSetVersionList()", "Exception : " + ex);
            _log.errorTrace("loadCardGroupSetVersionList: Exception print stack trace:", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetVersionList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCardGroupSetVersionList()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled())
                _log.debug("loadCardGroupSetVersionList()", "Exiting: loadCardGroupSetVersionList size=" + list.size());
        }
        return list;
    }

    /**
     * Method for Deleting Card Group version
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_cardGroupSetId
     *            String
     * @param p_version
     *            string
     * 
     * @return deleteCount int
     * @exception BTSLBaseException
     */
    public int deleteVersion(Connection p_con, String p_cardGroupSetId, String p_version) throws BTSLBaseException {
        PreparedStatement psmtDelete = null;
        int deleteCount = 0;
        if (_log.isDebugEnabled())
            _log.debug("deleteVersion", "Entered: cardGroupSetId=" + p_cardGroupSetId + " p_version= " + p_version);
        try {
            int deleteDetails = deleteCardGroupDetails(p_con, p_cardGroupSetId, p_version);

            BonusBundleDAO bonusBundleDAO = new BonusBundleDAO();
            int deleteBonusCount = bonusBundleDAO.deletePreviousBonus(p_con, p_cardGroupSetId, p_version);

            if (deleteDetails > 0 && deleteBonusCount > 0) {
                StringBuffer strBuff = new StringBuffer();
                strBuff.append("DELETE FROM card_group_set_versions ");
                strBuff.append("WHERE card_group_set_id = ? and version = ? ");
                String deleteQuery = strBuff.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug("deleteVersion", "Query sqlDelete:" + deleteQuery);
                }
                psmtDelete = p_con.prepareStatement(deleteQuery);
                psmtDelete.setString(1, p_cardGroupSetId);
                psmtDelete.setString(2, p_version);
                deleteCount = psmtDelete.executeUpdate();
            }
        } // end of try
        catch (SQLException sqle) {
            _log.error("deleteVersion", "SQLException: " + sqle.getMessage());
            _log.errorTrace("deleteVersion: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[deleteVersion]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteVersion", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("deleteCardGroupDetails", "Exception: " + e.getMessage());
            _log.errorTrace("deleteCardGroupDetails: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[deleteVersion]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteVersion", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtDelete != null) {
                    psmtDelete.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("deleteVersion", "Exiting: deleteCount=" + deleteCount);
            }
        } // end of finally
        return deleteCount;
    }

    /**
     * Method: loadServiceTypeList
     * This method is used to load the service type list for the network.
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_module
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadServiceTypeList(Connection p_con, String p_networkCode, String p_module) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadServiceTypeList", "Entered p_networkCode=" + p_networkCode);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList serviceTypeList = new ArrayList();
        try {
            StringBuffer selectQueryBuff = new StringBuffer("SELECT DISTINCT ST.service_type,ST.name FROM service_type ST,network_services NS");
            selectQueryBuff.append(" WHERE ST.service_type=NS.service_type AND NS.status<>'N' AND ST.status<>'N' AND NS.sender_network=? AND ST.module=? ORDER BY ST.name");
            if (_log.isDebugEnabled()) {
                _log.debug("loadServiceTypeList", "Query selectQueryBuff:" + selectQueryBuff.toString());
            }
            pstmtSelect = p_con.prepareStatement(selectQueryBuff.toString());
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, p_module);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                serviceTypeList.add(new ListValueVO(rs.getString("name"), rs.getString("service_type")));
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadServiceTypeList", "SQLException " + sqle.getMessage());
            _log.errorTrace("loadServiceTypeList: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadServiceTypeList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeList", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadServiceTypeList", "Exception " + e.getMessage());
            _log.errorTrace("loadServiceTypeList: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadServiceTypeList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeList", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadServiceTypeList", "Exiting serviceTypeList.size:" + serviceTypeList.size());
        }// end of final
        return serviceTypeList;
    }

    /**
     * Method for loading Card Group slabs.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_cardGroupSetID
     *            String
     *            return cardGroupDetailsVO
     * @exception BTSLBaseException
     */
    public ArrayList loadCardGroupSlab(Connection p_con, String p_cardGroupSetID, java.util.Date p_applicableDate) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadCardGroupSlab", "Entered p_cardGroupSetID=" + p_cardGroupSetID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList cardGroupDetailsVOList = null;
        CardGroupDetailsVO cardGroupDetailsVO = null;
        String latestCardGroupVersion = null;
        try {
            latestCardGroupVersion = loadCardGroupSetVersionLatestVersion(p_con, p_cardGroupSetID, p_applicableDate);
            if (!BTSLUtil.isNullString(latestCardGroupVersion)) {
                StringBuffer qry = new StringBuffer(" SELECT card_group_set_id,version,card_group_id,start_range, ");
                qry.append(" end_range,card_group_code FROM  card_group_details");
                qry.append(" WHERE card_group_set_id=? AND  version=? ");
                String selectQuery = qry.toString();
                if (_log.isDebugEnabled())
                    _log.debug("loadCardGroupSlab", "select query:" + selectQuery);
                pstmtSelect = p_con.prepareStatement(selectQuery);
                pstmtSelect.setString(1, p_cardGroupSetID);
                pstmtSelect.setString(2, latestCardGroupVersion);
                rs = pstmtSelect.executeQuery();
                cardGroupDetailsVOList = new ArrayList();
                while (rs.next()) {
                    cardGroupDetailsVO = new CardGroupDetailsVO();
                    cardGroupDetailsVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                    cardGroupDetailsVO.setCardGroupID(rs.getString("card_group_id"));
                    cardGroupDetailsVO.setCardGroupCode(rs.getString("card_group_code"));
                    cardGroupDetailsVO.setVersion(latestCardGroupVersion);
                    cardGroupDetailsVO.setStartRange(rs.getLong("start_range"));
                    cardGroupDetailsVO.setEndRange(rs.getLong("end_range"));
                    cardGroupDetailsVOList.add(cardGroupDetailsVO);
                }
            } else
                throw new BTSLBaseException("CardGroupDAO", "loadCardGroupDetails", SelfTopUpErrorCodesI.CARD_GROUP_SLAB_NOT_FOUND);

        }// end of try
        catch (BTSLBaseException bex) {
            _log.error("loadCardGroupSlab", "BTSLBaseException " + bex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CardGroupDAO[loadCardGroupSlab]", "", "", "", "Base Exception:" + bex.getMessage());
            throw bex;
        } catch (SQLException sqle) {
            _log.error("loadCardGroupSlab", "SQLException " + sqle.getMessage());
            _log.errorTrace("loadCardGroupSlab: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSlab]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadCardGroupSlab", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadCardGroupSlab", "Exception " + e.getMessage());
            _log.errorTrace("loadCardGroupSlab: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSlab]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadCardGroupSlab", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadCardGroupSlab", "Exiting cardGroupDetailsVOList size:" + cardGroupDetailsVOList.size());
        }// end of finally
        return cardGroupDetailsVOList;
    }

    public int suspendResumeCardGroupDetail(Connection p_con, CardGroupDetailsVO p_cardGroupDetailsVO) throws BTSLBaseException {
        PreparedStatement psmtUpdate = null;
        int updateCount = 0;

        if (_log.isDebugEnabled())
            _log.debug("suspendResumeCardGroupDetail", "Entered: p_cardGroupDetailsVO=" + p_cardGroupDetailsVO);

        try {
            StringBuffer strBuff = new StringBuffer();

            strBuff.append("UPDATE card_group_details SET status = ? ");
            strBuff.append("WHERE card_group_set_id = ? and card_group_id = ? and version = ?");

            String updateQuery = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("suspendResumeCardGroupDetail", "Query sqlInsert:" + updateQuery);

            psmtUpdate = p_con.prepareStatement(updateQuery);

            psmtUpdate.setString(1, p_cardGroupDetailsVO.getStatus());
            psmtUpdate.setString(2, p_cardGroupDetailsVO.getCardGroupSetID());
            psmtUpdate.setString(3, p_cardGroupDetailsVO.getCardGroupID());
            psmtUpdate.setString(4, p_cardGroupDetailsVO.getVersion());

            updateCount = psmtUpdate.executeUpdate();

        } // end of try
        catch (SQLException sqle) {
            _log.error("suspendResumeCardGroupDetail", "SQLException: " + sqle.getMessage());
            _log.errorTrace("suspendResumeCardGroupDetail: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[updateCardGroupSetVersion]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateCardGroupSetVersion", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("suspendResumeCardGroupDetail", "Exception: " + e.getMessage());
            _log.errorTrace("suspendResumeCardGroupDetail: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[suspendResumeCardGroupDetail]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "suspendResumeCardGroupDetail", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled())
                _log.debug("suspendResumeCardGroupDetail", "Exiting: updateCount=" + updateCount);
        } // end of finally

        return updateCount;
    }

    /**
     * Method to get the applicable card group set based on the network and
     * applicable dates
     * 
     * @param p_con
     * @param p_cardGroupSetID
     *            String
     * @param p_applicableDate
     *            Date
     * 
     * @return Card group set ID String
     * @throws BTSLBaseException
     */
    public String loadDefaultCardGroup(Connection p_con, String p_serviceTypeID, String p_cardGroupSubServiceID, String p_defaultCardGroupRequired) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadDefaultCardGroup", "Entered p_serviceTypeID=" + p_serviceTypeID + " p_cardGroupSubServiceID=" + p_cardGroupSubServiceID + " p_defaultCardGroupRequired=" + p_defaultCardGroupRequired);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        String defaultCardGroup = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT card_group_set_name, card_group_set_id   ");
            selectQueryBuff.append(" FROM card_group_set ");
            selectQueryBuff.append(" WHERE SERVICE_TYPE=? AND sub_service=?");
            selectQueryBuff.append(" AND is_default=? AND status <>'N'");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadDefaultCardGroup", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_serviceTypeID);
            pstmtSelect.setString(2, p_cardGroupSubServiceID);
            pstmtSelect.setString(3, p_defaultCardGroupRequired);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                defaultCardGroup = rs.getString("card_group_set_id");
            }
            return defaultCardGroup;
        }// end of try

        catch (SQLException sqle) {
            _log.error("loadDefaultCardGroup", "SQLException " + sqle.getMessage());
            _log.errorTrace("loadDefaultCardGroup: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadDefaultCardGroup]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadDefaultCardGroup", SelfTopUpErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadDefaultCardGroup", "Exception " + e.getMessage());
            _log.errorTrace("loadDefaultCardGroup: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadDefaultCardGroup]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadDefaultCardGroup", SelfTopUpErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            _log.debug("loadDefaultCardGroup", "Exiting: defaultCardGroup=" + defaultCardGroup);
        }// end of finally
    }

    /**
     * Method to get the currently applicable card group set based on the
     * network and current dates
     * 
     * @param p_con
     * @param p_currentDate
     *            Date
     * @param p_card_grp_setID
     *            String
     * 
     * @return isApplicable boolean;
     * @throws BTSLBaseException
     */
    public boolean isApplicableNow(Connection p_con, Date p_currentDate, String p_card_grp_setID) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("isApplicableNow()", "Entered currentDate:" + p_currentDate + " p_card_grp_setID=" + p_card_grp_setID);

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isApplicable = false;

        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT card_group_set_id,version,applicable_from");
        strBuff.append(" FROM card_group_set_versions WHERE  ");
        strBuff.append(" card_group_set_id = ? AND applicable_from <= ?");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("isApplicableNow()", "QUERY sqlSelect=" + sqlSelect);

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_card_grp_setID);
            pstmtSelect.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_currentDate));

            rs = pstmtSelect.executeQuery();
            if (rs.next())
                isApplicable = true;

        } catch (SQLException sqe) {
            _log.error("isApplicableNow()", "SQLException : " + sqe);
            _log.errorTrace("loadDefaultCardGroup: Exception print stack trace:", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[isApplicableNow]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isApplicableNow()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("isApplicableNow()", "Exception : " + ex);
            _log.errorTrace("isApplicableNow: Exception print stack trace:", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[isApplicableNow]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "isApplicableNow()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled())
                _log.debug("isApplicableNow()", "Exiting: ");
        }
        return isApplicable;
    }

    /**
     * Method to update new cardgroup as default and previous as normal
     * 
     * @param p_con
     * @param p_prv_cardGroupSetID
     *            String
     * @param p_curent_cardGroupSetID
     *            String
     * 
     * @return isApplicable boolean;
     * @throws BTSLBaseException
     */
    public boolean updateAsDefault(Connection p_con, String p_prv_cardGroupSetID, String p_curent_cardGroupSetID, String p_userID, Date p_current) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("updateAsDefault()", "Entered p_prv_cardGroupSetID:" + p_prv_cardGroupSetID + " p_curent_cardGroupSetID=" + p_curent_cardGroupSetID + ", p_userID:" + p_userID + ", p_current:" + p_current);

        PreparedStatement psmtUpdate = null;
        int updateCount = 0;
        boolean isApplicable = false;

        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" UPDATE card_group_set");
        strBuff.append(" SET is_default=?, modified_by=?, modified_on=?");
        strBuff.append(" WHERE card_group_set_id = ?");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("updateAsDefault()", "QUERY sqlSelect=" + sqlSelect);

        try {
            psmtUpdate = p_con.prepareStatement(sqlSelect);
            psmtUpdate.clearParameters();
            psmtUpdate.setString(1, PretupsI.YES);
            psmtUpdate.setString(2, p_userID);
            psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_current));
            psmtUpdate.setString(4, p_curent_cardGroupSetID);

            updateCount = psmtUpdate.executeUpdate();
            if (updateCount > 0) {
                if (!BTSLUtil.isNullString(p_prv_cardGroupSetID)) {
                    updateCount = 0;
                    psmtUpdate.clearParameters();
                    psmtUpdate.setString(1, PretupsI.NO);
                    psmtUpdate.setString(2, p_userID);
                    psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_current));
                    psmtUpdate.setString(4, p_prv_cardGroupSetID);

                    updateCount = psmtUpdate.executeUpdate();
                    if (updateCount > 0)
                        isApplicable = true;
                } else
                    isApplicable = true;

            }
        } catch (SQLException sqe) {
            _log.error("updateAsDefault()", "SQLException : " + sqe);
            _log.errorTrace("updateAsDefault: Exception print stack trace:", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[updateAsDefault]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isApplicableNow()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("updateAsDefault()", "Exception : " + ex);
            _log.errorTrace("updateAsDefault: Exception print stack trace:", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[updateAsDefault]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "updateAsDefault()", "error.general.processing");
        } finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled())
                _log.debug("updateAsDefault()", "Exiting: ");
        }
        return isApplicable;
    }

    /**
     * Method for isDefaultCardGroupExist.
     * 
     * @param p_con
     *            java.sql.Connection
     *            return boolean
     * @exception BTSLBaseException
     */
    public boolean isDefaultCardGroupExist(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isDefaultCardGroupExist", "Entered: p_serviceType=" + p_transferVO.getServiceType() + ", p_subService=" + p_transferVO.getSubService() + ",module_code=" + p_transferVO.getModule());

        boolean isDefaultExist = false;
        StringBuffer queryBuff = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            queryBuff = new StringBuffer();
            queryBuff.append(" select CARD_GROUP_SET_ID,STATUS,LANGUAGE_1_MESSAGE,IS_DEFAULT ");
            queryBuff.append(" from CARD_GROUP_SET where service_type=? and SUB_SERVICE=? and is_default='Y' and module_code=?");
            String selectQuery = queryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("isDefaultCardGroupExist", "selectQuery =" + selectQuery);

            psmt = p_con.prepareStatement(selectQuery);
            psmt.setString(1, p_transferVO.getServiceType());
            psmt.setString(2, p_transferVO.getSubService());
            psmt.setString(3, p_transferVO.getModule());
            rs = psmt.executeQuery();

            while (rs.next()) {
                p_transferVO.setCardGroupSetID(rs.getString("CARD_GROUP_SET_ID"));
                p_transferVO.setStatus(rs.getString("status"));
                p_transferVO.setSenderReturnMessage(rs.getString("LANGUAGE_1_MESSAGE"));
                isDefaultExist = true;
            }
        } // end of try
        catch (SQLException sqle) {
            _log.error("isDefaultCardGroupExist", "SQLException: " + sqle.getMessage());
            _log.errorTrace("isDefaultCardGroupExist: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[isDefaultCardGroupExist]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isDefaultCardGroupExist", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("isDefaultCardGroupExist", "Exception: " + e.getMessage());
            _log.errorTrace("isDefaultCardGroupExist: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[isDefaultCardGroupExist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isDefaultCardGroupExist", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isDefaultCardGroupExist", "Exiting isDefaultExist " + isDefaultExist);
        } // end of finally
        return isDefaultExist;
    }

    /**
     * This method loads the Card group details like the access fee, tax etc for
     * the slab in which the requested value lies
     * 
     * @param p_con
     * @param p_cardGroupSetAssocVO
     * @return CardGroupDetailsVO
     * @throws BTSLBaseException
     * @throws SQLException
     * @throws Exception
     */
    public HashMap<String, ArrayList<CardGroupDetailsVO>> loadCardGroupCache() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadCardGroupCache", "Entered ");
        PreparedStatement pstmtSelect = null;
        Connection con = null;
        HashMap<String, ArrayList<CardGroupDetailsVO>> cardGroupMap = new HashMap<String, ArrayList<CardGroupDetailsVO>>();
        ResultSet rs = null;
        CardGroupDetailsVO cardGroupDetailsVO = null;
        String key = null;
        String oldKey = null;
        ArrayList<CardGroupDetailsVO> cardGroupList = null;
        try {
            StringBuffer qry = new StringBuffer(" SELECT l.lookup_name set_name,cs.set_type,cgd.card_group_set_id, ");
            qry.append(" cgd.card_group_id,cgd.card_group_code,cgd.start_range,cgd.end_range,cgd.validity_period_type, ");
            qry.append(" cgd.validity_period,cgd.grace_period, cgd.sender_tax1_name, cgd.sender_tax1_type, ");
            qry.append(" cgd.sender_tax1_rate, cgd.sender_tax2_name, cgd.sender_tax2_type, cgd.sender_tax2_rate, ");
            qry.append(" cgd.receiver_tax1_name,cgd.receiver_tax1_type, cgd.receiver_tax1_rate,cgd.receiver_tax2_name, ");
            qry.append(" cgd.receiver_tax2_type, cgd.receiver_tax2_rate,  ");
            qry.append(" cgd.bonus_validity_value, cgd.sender_access_fee_type, cgd.sender_access_fee_rate, ");
            qry.append(" cgd.receiver_access_fee_type,cgd.receiver_access_fee_rate, cgd.min_sender_access_fee, ");
            qry.append(" cgd.max_sender_access_fee, cgd.min_receiver_access_fee,cgd.max_receiver_access_fee, cgd.multiple_of, ");
            qry.append(" cs.sub_service,cs.card_group_set_name, stsm.selector_name, cs.service_type, st.name service_name,  cgd.status, ");
            qry.append(" cgd.online_offline,cgd.both,cgd.sender_mult_factor,cgd.receiver_mult_factor, ");
            qry.append(" cgs.version, cgs.applicable_from, cgd.cos_required , cgd.in_promo ");
            qry.append(" FROM card_group_details cgd,lookups l, card_group_set cs, service_type st,service_type_selector_mapping stsm, card_group_set_versions cgs ");
            qry.append(" WHERE cgs.card_group_set_id=cgd.card_group_set_id AND cgs.version=cgd.version ");
            qry.append(" AND l.lookup_type=? AND cs.card_group_set_id=cgd.card_group_set_id ");
            // Commented below lines By Diwakar for OCM
            // qry.append(" AND cgs.applicable_from >=(SELECT MAX(cdme.applicable_from) FROM CARD_GROUP_SET_VERSIONS cdme ");
            // qry.append(" WHERE  (cdme.applicable_from<=SYSDATE AND cdme.card_group_set_id=cgd.card_group_set_id) OR cgs.applicable_from >=sysdate) ");
            qry.append(" AND (cgs.applicable_from >=(SELECT MAX(cdme.applicable_from) FROM CARD_GROUP_SET_VERSIONS cdme WHERE  (cdme.applicable_from<=SYSDATE AND cdme.card_group_set_id=cgd.card_group_set_id)) ");
            qry.append(" OR cgs.applicable_from >=sysdate) ");
            // Ended Here
            qry.append(" AND cs.service_type=st.service_type AND stsm.service_type=st.service_type ");
            qry.append(" AND cs.sub_service=stsm.selector_code AND cs.service_type=stsm.service_type AND l.lookup_code=cs.set_type ");
            qry.append(" ORDER BY card_group_set_id,cgd.version ");
            String selectQuery = qry.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadCardGroupCache", "select query:" + selectQuery);
            con = OracleUtil.getSingleConnection();
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, PretupsI.CARD_GROUP_SET_TYPE);
            rs = pstmtSelect.executeQuery();
            BonusBundleDAO bonusBundleDAO = new BonusBundleDAO();
            while (rs.next()) {
                cardGroupDetailsVO = new CardGroupDetailsVO();
                cardGroupDetailsVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                cardGroupDetailsVO.setCardGroupID(rs.getString("card_group_id"));
                cardGroupDetailsVO.setCardGroupCode(rs.getString("card_group_code"));
                cardGroupDetailsVO.setStartRange(rs.getLong("start_range"));
                cardGroupDetailsVO.setEndRange(rs.getLong("end_range"));
                cardGroupDetailsVO.setValidityPeriodType(rs.getString("validity_period_type"));
                cardGroupDetailsVO.setValidityPeriod(rs.getInt("validity_period"));
                cardGroupDetailsVO.setGracePeriod(rs.getLong("grace_period"));
                cardGroupDetailsVO.setSenderTax1Name(rs.getString("sender_tax1_name"));
                cardGroupDetailsVO.setSenderTax1Type(rs.getString("sender_tax1_type"));
                cardGroupDetailsVO.setSenderTax1Rate(rs.getDouble("sender_tax1_rate"));
                cardGroupDetailsVO.setSenderTax2Name(rs.getString("sender_tax2_name"));
                cardGroupDetailsVO.setSenderTax2Type(rs.getString("sender_tax2_type"));
                cardGroupDetailsVO.setSenderTax2Rate(rs.getDouble("sender_tax2_rate"));
                cardGroupDetailsVO.setReceiverTax1Name(rs.getString("receiver_tax1_name"));
                cardGroupDetailsVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
                cardGroupDetailsVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                cardGroupDetailsVO.setReceiverTax2Name(rs.getString("receiver_tax2_name"));
                cardGroupDetailsVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
                cardGroupDetailsVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                cardGroupDetailsVO.setBonusValidityValue(rs.getInt("bonus_validity_value"));
                cardGroupDetailsVO.setSenderAccessFeeType(rs.getString("sender_access_fee_type"));
                cardGroupDetailsVO.setSenderAccessFeeRate(rs.getDouble("sender_access_fee_rate"));
                cardGroupDetailsVO.setReceiverAccessFeeType(rs.getString("receiver_access_fee_type"));
                cardGroupDetailsVO.setReceiverAccessFeeRate(rs.getDouble("receiver_access_fee_rate"));
                cardGroupDetailsVO.setMinSenderAccessFee(rs.getLong("min_sender_access_fee"));
                cardGroupDetailsVO.setMaxSenderAccessFee(rs.getLong("max_sender_access_fee"));
                cardGroupDetailsVO.setMinReceiverAccessFee(rs.getLong("min_receiver_access_fee"));
                cardGroupDetailsVO.setMaxReceiverAccessFee(rs.getLong("max_receiver_access_fee"));
                cardGroupDetailsVO.setMultipleOf(rs.getLong("multiple_of"));
                cardGroupDetailsVO.setCardGroupSetName(rs.getString("card_group_set_name"));
                cardGroupDetailsVO.setCardGroupSubServiceId(rs.getString("sub_service"));
                cardGroupDetailsVO.setCardGroupSubServiceIdDesc(rs.getString("selector_name"));
                cardGroupDetailsVO.setServiceTypeId(rs.getString("service_type"));
                cardGroupDetailsVO.setServiceTypeDesc(rs.getString("service_name"));
                cardGroupDetailsVO.setSetType(rs.getString("set_type"));
                cardGroupDetailsVO.setSetTypeName(rs.getString("set_name"));
                cardGroupDetailsVO.setStatus(rs.getString("status"));

                cardGroupDetailsVO.setOnline(rs.getString("online_offline"));
                cardGroupDetailsVO.setBoth(rs.getString("both"));
                cardGroupDetailsVO.setSenderConvFactor(rs.getString("sender_mult_factor"));
                cardGroupDetailsVO.setReceiverConvFactor(rs.getString("receiver_mult_factor"));

                cardGroupDetailsVO.setVersion(rs.getString("version"));
                cardGroupDetailsVO.setApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));

                // added for cos
                if (SystemPreferences.COS_REQUIRED) {
                    cardGroupDetailsVO.setCosRequired(rs.getString("cos_required"));
                    if (BTSLUtil.isNullString(cardGroupDetailsVO.getCosRequired()) || cardGroupDetailsVO.getCosRequired().equalsIgnoreCase("N"))
                        cardGroupDetailsVO.setCosRequired(PretupsI.NO);
                    else
                        cardGroupDetailsVO.setCosRequired(PretupsI.YES);
                }
                if (SystemPreferences.IN_PROMO_REQUIRED)
                    cardGroupDetailsVO.setInPromo(rs.getDouble("in_promo"));
                // cardGroupDetailsVO.setApplicableFrom(rs.getDate("applicable_from"));
                cardGroupDetailsVO.setBonusAccList(bonusBundleDAO.loadBonusAccDetails(con, cardGroupDetailsVO));
                // key=cardGroupDetailsVO.getCardGroupSetID();
                key = cardGroupDetailsVO.getCardGroupSetID() + "_" + cardGroupDetailsVO.getVersion();
                if (oldKey == null)
                    cardGroupList = new ArrayList<CardGroupDetailsVO>();
                else if (!oldKey.equals(key)) {
                    if (oldKey != null) {
                        cardGroupMap.put(oldKey, cardGroupList);
                    }
                    cardGroupList = new ArrayList<CardGroupDetailsVO>();
                }
                cardGroupList.add(cardGroupDetailsVO);
                oldKey = key;
            }
            // Added for bug removal incase of single card group
            if (oldKey.equals(key)) {
                if (oldKey != null) {
                    cardGroupMap.put(oldKey, cardGroupList);
                }
            }
            return cardGroupMap;
        }// end of try
        catch (BTSLBaseException bex) {
            _log.error("loadCardGroupCache", "BTSLBaseException " + bex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CardGroupDAO[loadCardGroupCache]", "", "", "", "Base Exception:" + bex.getMessage());
            throw bex;
        } catch (SQLException sqle) {
            _log.error("loadCardGroupCache", "SQLException " + sqle.getMessage());
            _log.errorTrace("loadCardGroupCache: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupCache]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadCardGroupCache", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadCardGroupCache", "Exception " + e.getMessage());
            _log.errorTrace("loadCardGroupCache: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupCache]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadCardGroupCache", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadCardGroupCache", "Exiting cardGroupMap.size()=:" + cardGroupMap.size());
        }// end of finally
    }

    /**
     * This method loads the Card group version details
     * 
     * @param p_con
     * @param p_cardGroupSetAssocVO
     * @return CardGroupDetailsVO
     * @throws BTSLBaseException
     * @throws SQLException
     * @throws Exception
     */
    public HashMap<String, ArrayList<CardGroupSetVersionVO>> loadCardGroupVersionCache() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadCardGroupVersionCache", "Entered ");
        PreparedStatement pstmtSelect = null;
        Connection con = null;
        HashMap<String, ArrayList<CardGroupSetVersionVO>> cardGroupVersionMap = new HashMap<String, ArrayList<CardGroupSetVersionVO>>();
        ArrayList<CardGroupSetVersionVO> cardGroupVersionList = null;
        ResultSet rs = null;
        CardGroupSetVersionVO cardGroupSetVersionVO = null;
        String key = null;
        String oldKey = null;
        try {
            StringBuffer qry = new StringBuffer("SELECT cgsv.card_group_set_id, cgsv.version, cgsv.applicable_from ");
            qry.append("FROM card_group_set_versions cgsv ,card_group_set cgs ");
            qry.append("WHERE cgs.card_group_set_id=cgsv.card_group_set_id ");
            qry.append("AND (cgsv.applicable_from >=(SELECT MAX(cdme.applicable_from) FROM CARD_GROUP_SET_VERSIONS cdme ");
            qry.append("WHERE  cdme.applicable_from<=SYSDATE AND cdme.card_group_set_id=cgs.card_group_set_id) OR cgsv.applicable_from >=sysdate) ");
            qry.append("ORDER BY card_group_set_id,version ");

            String selectQuery = qry.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadCardGroupVersionCache", "select query:" + selectQuery);
            con = OracleUtil.getSingleConnection();
            pstmtSelect = con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                cardGroupSetVersionVO = new CardGroupSetVersionVO();
                cardGroupSetVersionVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                cardGroupSetVersionVO.setVersion(rs.getString("version"));
                cardGroupSetVersionVO.setApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_From")));
                key = cardGroupSetVersionVO.getCardGroupSetID();
                if (oldKey == null)
                    cardGroupVersionList = new ArrayList<CardGroupSetVersionVO>();
                else if (!oldKey.equals(key)) {
                    if (oldKey != null)
                        cardGroupVersionMap.put(oldKey, cardGroupVersionList);
                    cardGroupVersionList = new ArrayList<CardGroupSetVersionVO>();
                }
                cardGroupVersionList.add(cardGroupSetVersionVO);
                oldKey = key;
            }
            cardGroupVersionMap.put(oldKey, cardGroupVersionList);
            return cardGroupVersionMap;
        } catch (BTSLBaseException bex) {
            _log.error("loadCardGroupVersionCache", "BTSLBaseException " + bex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CardGroupDAO[loadCardGroupVersionCache]", "", "", "", "Base Exception:" + bex.getMessage());
            throw bex;
        } catch (SQLException sqle) {
            _log.error("loadCardGroupVersionCache", "SQLException " + sqle.getMessage());
            _log.errorTrace("loadCardGroupVersionCache: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupVersionCache]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadCardGroupVersionCache", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadCardGroupVersionCache", "Exception " + e.getMessage());
            _log.errorTrace("loadCardGroupVersionCache: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupVersionCache]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadCardGroupVersionCache", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadCardGroupCache", "Exiting cardGroupVersionMap.size()=:" + cardGroupVersionMap.size());
        }// end of finally
    }

    /**
     * @param p_con
     * @param p_cardGroupSetID
     * @param p_applicableDate
     * @return
     * @throws BTSLBaseException
     * @author sonali.garg
     */
    public CardGroupDetailsVO loadCardGroupMinMax(Connection p_con, String p_cardGroupSetID, java.util.Date p_applicableDate) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadCardGroupMinMax", "Entered p_cardGroupSetID=" + p_cardGroupSetID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        CardGroupDetailsVO cardGroupDetailsVO = null;
        String latestCardGroupVersion = null;
        try {
            latestCardGroupVersion = loadCardGroupSetVersionLatestVersion(p_con, p_cardGroupSetID, p_applicableDate);
            if (!BTSLUtil.isNullString(latestCardGroupVersion)) {
                StringBuffer qry = new StringBuffer(" SELECT min(start_range) min,max(end_range) max");
                // qry.append("card_group_set_id,version,card_group_id,card_group_code");
                qry.append(" FROM card_group_details ");
                qry.append(" WHERE card_group_set_id=? AND  version=? ");
                String selectQuery = qry.toString();
                if (_log.isDebugEnabled())
                    _log.debug("loadCardGroupMinMax", "select query:" + selectQuery);
                pstmtSelect = p_con.prepareStatement(selectQuery);
                pstmtSelect.setString(1, p_cardGroupSetID);
                pstmtSelect.setString(2, latestCardGroupVersion);
                rs = pstmtSelect.executeQuery();

                while (rs.next()) {
                    cardGroupDetailsVO = new CardGroupDetailsVO();
                    /*
                     * cardGroupDetailsVO.setCardGroupSetID(rs.getString(
                     * "card_group_set_id"));
                     * cardGroupDetailsVO.setCardGroupID(rs.getString(
                     * "card_group_id"));
                     * cardGroupDetailsVO.setCardGroupCode(rs.getString(
                     * "card_group_code"));
                     */
                    cardGroupDetailsVO.setVersion(latestCardGroupVersion);
                    cardGroupDetailsVO.setStartRange(rs.getLong("min"));
                    cardGroupDetailsVO.setEndRange(rs.getLong("max"));
                }
            } else
                throw new BTSLBaseException("CardGroupDAO", "loadCardGroupMinMax", SelfTopUpErrorCodesI.CARD_GROUP_SLAB_NOT_FOUND);

        }// end of try
        catch (BTSLBaseException bex) {
            _log.error("loadCardGroupMinMax", "BTSLBaseException " + bex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CardGroupDAO[loadCardGroupSlab]", "", "", "", "Base Exception:" + bex.getMessage());
            throw bex;
        } catch (SQLException sqle) {
            _log.error("loadCardGroupMinMax", "SQLException " + sqle.getMessage());
            _log.errorTrace("loadCardGroupMinMax: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSlab]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadCardGroupMinMax", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadCardGroupMinMax", "Exception " + e.getMessage());
            _log.errorTrace("loadCardGroupMinMax: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSlab]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CardGroupDAO", "loadCardGroupMinMax", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadCardGroupMinMax", "Exiting CardGroupDetailsVO:" + cardGroupDetailsVO);
        }// end of finally
        return cardGroupDetailsVO;
    }
}
