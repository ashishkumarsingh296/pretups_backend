package com.btsl.pretups.cardgroup.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListSorterUtil;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.SqlParameterEncoder;
import com.btsl.voms.vomscommon.VOMSI;

/**
 * @(#)BatchModifyCardGroupDAO.java
 *                                  Copyright(c) 2009, Bharti Telesoft Ltd.
 *                                  All Rights Reserved
 * 
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Author Date History
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Sanjeew 10/02/09 Initial Creation
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 * 
 *                                  This class is used for load/Modify card
 *                                  group details in Batch
 * 
 */

public class BatchModifyCardGroupDAO {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private  BatchModifyCardGroupQry batchModifyCardGroupQry = (BatchModifyCardGroupQry)ObjectProducer.getObject(QueryConstants.BATCH_MODIFY_CARDGROUP_QRY, QueryConstants.QUERY_PRODUCER);
    /**
     * This method loads the all the active Card group details on requested date
     * on the basis of Service type,
     * Network code and date
     * 
     * @param p_con
     * @param p_date
     *            String
     * @param p_networkCode
     *            String
     * @param p_serviceType
     *            String
     * @return cardGroupDetailList ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCardGroupDetailsListByDate(Connection p_con, String p_date, String p_networkCode, String p_serviceType, String p_module,ArrayList p_cgSetselectedList) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadCardGroupDetailsListByDate",
                "Entered p_date=" + p_date + ", p_networkCode:" + p_networkCode + ", p_serviceType:" + p_serviceType + ", p_module:" + p_module+ ", p_cgSetselectedList:" + p_cgSetselectedList);
        }
        final String METHOD_NAME = "loadCardGroupDetailsListByDate";
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtAppSelect = null;
        ResultSet rs = null;
        ResultSet appRs = null;
        PreparedStatement pstmtSelect1 = null;
        final ArrayList cardGroupDetailList = new ArrayList();

        CardGroupDetailsVO cardGroupDetailsVO = null;
        BonusBundleDAO bonusBundleDAO = null;
        try {
            bonusBundleDAO = new BonusBundleDAO();
            
            final String selectQuery = batchModifyCardGroupQry.loadCardGroupDetailsListByDateQry(p_serviceType,p_cgSetselectedList);
            final StringBuilder appqry = new StringBuilder(" ");
            appqry.append("SELECT card_group_set_id,version,applicable_from FROM card_group_set_versions");
            appqry.append(" WHERE card_group_set_id = ? AND version = ?");
            final String selectAppQuery = appqry.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("loadCardGroupDetailsListByDate", "select selectQuery:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            if (_log.isDebugEnabled()) {
                _log.debug("loadCardGroupDetailsListByDate", "select selectAppQuery:" + selectAppQuery);
            }
            
            StringBuilder qry1 = new StringBuilder();
            qry1.append(" select cad.card_group_set_id,cad.version,cad.card_group_id,cad.bundle_id,cad.type,cad.validity,");
            qry1.append(" cad.value,cad.mult_factor,bbm.bundle_name,bbm.bundle_type,bbm.res_in_status,bbm.bundle_code");
            qry1.append(" from card_group_sub_bon_acc_details cad, bonus_bundle_master bbm");
            qry1.append(" where cad.bundle_id=bbm.bundle_id");
            qry1.append(" and card_group_set_id=? and version=? and card_group_id=? order by bbm.bundle_id ");
            final String selectQuery1 = qry1.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("loadCardGroupDetailsListByDate", "select selectAppQuery:" + selectQuery1);
            }
            pstmtSelect1 = p_con.prepareStatement(selectQuery1);
            
            pstmtAppSelect = p_con.prepareStatement(selectAppQuery);

            int i = 0;
            pstmtSelect.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(BTSLUtil.getDateFromDateString(p_date, PretupsI.TIMESTAMP_DATESPACEHHMM)));
            pstmtSelect.setString(++i, p_module);
            pstmtSelect.setString(++i, p_networkCode);
            if (!p_serviceType.equalsIgnoreCase(PretupsI.ALL)) {
                pstmtSelect.setString(++i, p_serviceType);
            }
            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                cardGroupDetailsVO = new CardGroupDetailsVO();
                cardGroupDetailsVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                cardGroupDetailsVO.setCardGroupID(rs.getString("card_group_id"));
                cardGroupDetailsVO.setCardGroupSetName(rs.getString("card_group_set_name"));
                cardGroupDetailsVO.setNetworkCode(rs.getString("network_code"));
                cardGroupDetailsVO.setCardGroupCode(rs.getString("card_group_code"));
                cardGroupDetailsVO.setStartRange(rs.getLong("start_range"));
                cardGroupDetailsVO.setEndRange(rs.getLong("end_range"));
                cardGroupDetailsVO.setValidityPeriod(rs.getInt("validity_period"));
                cardGroupDetailsVO.setGracePeriod(rs.getLong("grace_period"));
                cardGroupDetailsVO.setMultipleOf(rs.getLong("multiple_of"));
                cardGroupDetailsVO.setReceiverTax1Name(rs.getString("receiver_tax1_name"));
                cardGroupDetailsVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
                cardGroupDetailsVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                cardGroupDetailsVO.setReceiverTax2Name(rs.getString("receiver_tax2_name"));
                cardGroupDetailsVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
                cardGroupDetailsVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                cardGroupDetailsVO.setReceiverAccessFeeType(rs.getString("receiver_access_fee_type"));
                cardGroupDetailsVO.setReceiverAccessFeeRate(rs.getDouble("receiver_access_fee_rate"));
                cardGroupDetailsVO.setMinReceiverAccessFee(rs.getLong("min_receiver_access_fee"));
                cardGroupDetailsVO.setMaxReceiverAccessFee(rs.getLong("max_receiver_access_fee"));

                // for P2P
                cardGroupDetailsVO.setSenderTax1Name(rs.getString("sender_tax1_name"));
                cardGroupDetailsVO.setSenderTax1Type(rs.getString("sender_tax1_type"));
                cardGroupDetailsVO.setSenderTax1Rate(rs.getDouble("sender_tax1_rate"));
                cardGroupDetailsVO.setSenderTax2Name(rs.getString("sender_tax2_name"));
                cardGroupDetailsVO.setSenderTax2Type(rs.getString("sender_tax2_type"));
                cardGroupDetailsVO.setSenderTax2Rate(rs.getDouble("sender_tax2_rate"));
                cardGroupDetailsVO.setSenderAccessFeeType(rs.getString("sender_access_fee_type"));
                cardGroupDetailsVO.setSenderAccessFeeRate(rs.getDouble("sender_access_fee_rate"));
                cardGroupDetailsVO.setMinSenderAccessFee(rs.getLong("min_sender_access_fee"));
                cardGroupDetailsVO.setMaxSenderAccessFee(rs.getLong("max_sender_access_fee"));
                cardGroupDetailsVO.setCardGroupSubServiceId(rs.getString("sub_service"));
                cardGroupDetailsVO.setServiceTypeId(rs.getString("service_type"));
                cardGroupDetailsVO.setBonusValidityValue(rs.getInt("bonus_validity_value"));
                cardGroupDetailsVO.setVersion(rs.getString("version"));
                cardGroupDetailsVO.setLastVersion(rs.getString("last_version"));
                cardGroupDetailsVO.setValidityPeriodType(rs.getString("validity_period_type"));
                cardGroupDetailsVO.setOnline(rs.getString("online_offline"));
                cardGroupDetailsVO.setBoth(rs.getString("both"));
                cardGroupDetailsVO.setStatus(rs.getString("status"));
                cardGroupDetailsVO.setVoucherType(rs.getString("voucher_type"));
                cardGroupDetailsVO.setVoucherSegment(rs.getString("voucher_segment"));
                cardGroupDetailsVO.setVoucherProductId(rs.getString("voucher_product_id"));
                if(!BTSLUtil.isNullString(cardGroupDetailsVO.getVoucherType()))
        	    	cardGroupDetailsVO.setVoucherDenomination(cardGroupDetailsVO.getStartRangeAsString());
                // Added for conversion from one account to another.
                if(rs.getString("sender_mult_factor").indexOf('.')>-1){
                    cardGroupDetailsVO.setSenderConvFactor(rs.getString("sender_mult_factor").substring(0,rs.getString("sender_mult_factor").indexOf('.')));                    
                }else{
                    cardGroupDetailsVO.setSenderConvFactor(rs.getString("sender_mult_factor"));
                }
                if(rs.getString("receiver_mult_factor").indexOf('.')>-1){
                    cardGroupDetailsVO.setReceiverConvFactor(rs.getString("receiver_mult_factor").substring(0,rs.getString("receiver_mult_factor").indexOf('.')));                    
                }else{
                    cardGroupDetailsVO.setReceiverConvFactor(rs.getString("receiver_mult_factor"));
                }
               
                // added for cos
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
                    cardGroupDetailsVO.setCosRequired(rs.getString("cos_required"));
                }
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED)).booleanValue()) {
                    cardGroupDetailsVO.setInPromo(rs.getDouble("in_promo"));
                }
                pstmtAppSelect.setString(1, rs.getString("card_group_set_id"));
                pstmtAppSelect.setString(2, rs.getString("version"));
                appRs = pstmtAppSelect.executeQuery();
                pstmtAppSelect.clearParameters();
                if (appRs.next()) {
                    cardGroupDetailsVO.setApplicableFrom(appRs.getTimestamp("applicable_from"));
                    cardGroupDetailsVO.setOldApplicableFrom(appRs.getTimestamp("applicable_from").getTime());
                }
                // Added for dynamic bonus bundle feature.
                // Set the Bonus Accounts associated with the card group.
                cardGroupDetailsVO.setBonusAccList(bonusBundleDAO.loadBonusAccDetails(p_con, pstmtSelect1, cardGroupDetailsVO));

                cardGroupDetailsVO.setCardName(rs.getString("card_name"));
                cardGroupDetailsVO.setReversalPermitted(rs.getString("reversal_permitted"));
                cardGroupDetailList.add(cardGroupDetailsVO);
            }
            return cardGroupDetailList;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadCardGroupDetailsListByDate", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BatchModifyCardGroupDAO[loadCardGroupDetailsListByDate]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("BatchModifyCardGroupDAO", "loadCardGroupDetailsListByDate", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadCardGroupDetailsListByDate", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BatchModifyCardGroupDAO[loadCardGroupDetailsListByDate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("BatchModifyCardGroupDAO", "loadCardGroupDetailsListByDate", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	try {
                if (pstmtSelect1 != null) {
                    pstmtSelect1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
        	try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (appRs != null) {
                    appRs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtAppSelect != null) {
                    pstmtAppSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadCardGroupDetailsListByDate", "Exiting CardGroupDetailList Size:" + cardGroupDetailList.size());
            }
        }// end of finally
    }

    /**
     * This method used to modify cardgroup details in batch
     * 
     * @param p_con
     * @param p_cardModifyDetailList
     *            ArrayList
     * @param p_messages
     *            MessageResources
     * @param p_serviceType
     *            String
     * @param p_locale
     *            Locale
     *            param p_currentDate Date
     * @param p_user
     *            String
     * @return errorList ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList modifyCardGroupinBatch(Connection p_con, ArrayList p_cardModifyDetailList, String p_serviceType, MessageResources p_messages, Locale p_locale, Date p_currentDate, String p_user) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug(
                "modifyCardGroupinBatch",
                "Entered: p_cardDetailList.size()=" + p_cardModifyDetailList.size() + " p_serviceType=" + p_serviceType + " p_messages=" + p_messages + " p_locale=" + p_locale + ", p_currentDate=" + p_currentDate + ", p_user=" + p_user);
        }
        final String METHOD_NAME = "modifyCardGroupinBatch";
        final ArrayList errorList = new ArrayList();
        ListValueVO errorVO = null;
        int index = 0;
        PreparedStatement cardApplicableSelect = null;
        final PreparedStatement slabRangeSelect = null;
        PreparedStatement selectormappingSelect = null;
        PreparedStatement valPeriTypeSelect = null;
        PreparedStatement cardGrpDetailsUpdate = null;
        PreparedStatement cardGrSetUpdate = null;
        PreparedStatement cardGrSetVerUpdate = null;
        PreparedStatement cardGrSetVer1Update = null;
        PreparedStatement cardgrDetailsInsert = null;
        PreparedStatement lastVersionSelect = null;
        PreparedStatement cardGrSetVerInsert = null;
        PreparedStatement cardDetDelete = null;
        BonusBundleDAO bonusBundleDAO = null;

        final StringBuilder selectCardApplicable = new StringBuilder("SELECT version from card_group_set_versions where card_group_set_id=? AND applicable_from=?");
        final String selectCardApplicableQuery = selectCardApplicable.toString();
        ResultSet rsCardApplicable = null;
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query selectCardApplicableQuery:" + selectCardApplicableQuery);
        }

        final StringBuilder selectSelectorMapping = new StringBuilder(" SELECT selector_code, selector_name FROM service_type_selector_mapping WHERE service_type=?");
        final String selectSelectorMappingQuery = selectSelectorMapping.toString();
        final ResultSet rsSelectormapping = null;
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query selectSelectormappingQuery:" + selectSelectorMappingQuery);
        }

        final StringBuilder selectValPeriType = new StringBuilder(" SELECT DISTINCT validity_period_type FROM card_group_details WHERE card_group_set_id=?");
        final String selectValPeriTypeQuery = selectValPeriType.toString();
        final ResultSet rsValPeriType = null;
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query selectValPeriTypeQuery:" + selectValPeriTypeQuery);
        }

        final StringBuilder selectLastVersion = new StringBuilder(" SELECT DISTINCT last_version FROM CARD_GROUP_SET WHERE card_group_set_id=? AND status=? AND SERVICE_TYPE=?");
        final String selectLastVersionQuery = selectLastVersion.toString();
        ResultSet rsLastVersion = null;
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query selectLastVersionQuery:" + selectLastVersionQuery);
        }

        final StringBuilder updateCardGrpDetails = new StringBuilder(
            "UPDATE card_group_details SET start_range=?, end_range=?, validity_period=?, grace_period=?, receiver_tax1_type=?,");
        if((PretupsI.VOUCHER_CONS_SERVICE).equals(p_serviceType)){
            updateCardGrpDetails.append(" voucher_type=?, voucher_segment=?, voucher_product_id=?");
        }
        updateCardGrpDetails.append(" receiver_tax1_type=?,receiver_tax1_rate=?, receiver_tax2_type=?, receiver_tax2_rate=?, bonus_talk_value_type=?, bonus_talk_value_rate=?,");
        updateCardGrpDetails.append(" bonus_validity_value=?, receiver_access_fee_type=?, receiver_access_fee_rate=?, min_receiver_access_fee=?,");
        updateCardGrpDetails.append(" max_receiver_access_fee=?, multiple_of=?, bonus1=?, bonus2=?, status=?, bonus_talk_time_validity=?, bonus_sms_validity=?,");
        updateCardGrpDetails
            .append(" bonus_mms_validity=?, validity_period_type=?, online_offline=?, both=?,cos_required=?,in_promo=?  WHERE card_group_set_id=? AND version=? AND card_group_code=?");
       
        final String updateCardgrDetailsQuery = updateCardGrpDetails.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query updateCardgrDetailsQuery:" + updateCardgrDetailsQuery);
        }

        final StringBuilder updateCardGrSet = new StringBuilder("UPDATE card_group_set SET last_version = ?, ");
        updateCardGrSet.append(" modified_on = ?, modified_by = ? WHERE card_group_set_id = ?");
        final String updateCardGrSetQuery = updateCardGrSet.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query updateCardGrSetQuery:" + updateCardGrSetQuery);
        }

        final StringBuilder updateCardGrSetVer = new StringBuilder(" UPDATE card_group_set_versions SET modified_on = ?, modified_by = ? ");
        updateCardGrSetVer.append(" WHERE card_group_set_id = ? and version = ?");
        final String updateCardGrSetVerQuery = updateCardGrSetVer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query updateCardGrSetVerQuery:" + updateCardGrSetVerQuery);
        }

        final StringBuilder updateCardGrSetVer1 = new StringBuilder("UPDATE card_group_set_versions SET applicable_from=?,modified_on = ?, modified_by = ? ");
        updateCardGrSetVer1.append(" WHERE card_group_set_id = ? and version = ?");
        final String updateCardGrSetVer1Query = updateCardGrSetVer1.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query updateCardGrSetVer1Query:" + updateCardGrSetVer1Query);
        }

        final StringBuilder deleteCardDet = new StringBuilder("DELETE FROM card_group_details ");
        deleteCardDet.append("WHERE card_group_set_id = ? and version = ?");
        final String deleteCardDetQuery = deleteCardDet.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query deleteCardDetQuery:" + deleteCardDetQuery);
        }

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("INSERT INTO card_group_details (card_group_set_id,");
        strBuff.append("version,card_group_id,start_range,end_range,validity_period_type,");
        strBuff.append("validity_period,grace_period,sender_tax1_name,sender_tax1_type, ");
        strBuff.append("sender_tax1_rate,sender_tax2_name,sender_tax2_type,sender_tax2_rate,");
        strBuff.append("receiver_tax1_name,receiver_tax1_type,receiver_tax1_rate,");
        strBuff.append("receiver_tax2_name,receiver_tax2_type,receiver_tax2_rate,bonus_validity_value,");
        strBuff.append("sender_access_fee_type,sender_access_fee_rate,min_sender_access_fee,");
        strBuff.append("max_sender_access_fee,receiver_access_fee_type,receiver_access_fee_rate,");
        strBuff.append("min_receiver_access_fee,max_receiver_access_fee,card_group_code,multiple_of,status,");
        strBuff.append("voucher_type,voucher_segment,voucher_product_id,");
        if(QueryConstants.DB_POSTGRESQL.equals(Constants.getProperty(QueryConstants.PRETUPS_DB))){
            strBuff.append("online_offline,\"both\",sender_mult_factor,receiver_mult_factor,cos_required,in_promo,card_name,reversal_permitted,reversal_modified_date )");     
        }else{
            strBuff.append("online_offline,both,sender_mult_factor,receiver_mult_factor,cos_required,in_promo,card_name,reversal_permitted,reversal_modified_date )");
        }
       
        strBuff.append(" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        final String insertCardgrDetailsQuery = strBuff.toString();
        // String insertCardgrDetailsQuery = insertCardgrDetails.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query insertCardgrDetailsQuery:" + insertCardgrDetailsQuery);
        }

        final StringBuilder insertCardGrSetVer = new StringBuilder("INSERT INTO card_group_set_versions (card_group_set_id, version,");
        insertCardGrSetVer.append(" applicable_from, created_by, created_on, modified_by, modified_on) VALUES (?,?,?,?,?,?,?)");
        final String insertCardGrSetVerQuery = insertCardGrSetVer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query insertCardGrSetVerQuery:" + insertCardGrSetVerQuery);
        }

        try {
            cardApplicableSelect = p_con.prepareStatement(selectCardApplicableQuery);
            valPeriTypeSelect = p_con.prepareStatement(selectValPeriTypeQuery);
            selectormappingSelect = p_con.prepareStatement(selectSelectorMappingQuery);
            lastVersionSelect = p_con.prepareStatement(selectLastVersionQuery);
            cardGrpDetailsUpdate = p_con.prepareStatement(updateCardgrDetailsQuery);
            cardGrSetUpdate = p_con.prepareStatement(updateCardGrSetQuery);
            cardGrSetVerUpdate = p_con.prepareStatement(updateCardGrSetVerQuery);
            cardGrSetVer1Update = p_con.prepareStatement(updateCardGrSetVer1Query);
            cardgrDetailsInsert = p_con.prepareStatement(insertCardgrDetailsQuery);
            cardGrSetVerInsert = p_con.prepareStatement(insertCardGrSetVerQuery);
            cardDetDelete = p_con.prepareStatement(deleteCardDetQuery);

            String version = null;
            String newVersion = null;
            int updateCount = 0;
            int deleteCount = 0;
            int insertCount = 0;
            boolean startEndRange = true;
            boolean modifyFlag = false;
            CardGroupDetailsVO cardGroupDetailsVO = null;
            CardGroupDetailsVO tmpCardGroupDetailsVO = null;
            // Collections.sort(p_cardModifyDetailList);
            ArrayList list = null;
            ListSorterUtil sort = null;
            /*Modify by Lalit
             * Selector variable was initialized as null variable and 
             * No value is assigned in next process. I just initialized it as blank so it does not 
             * print null in error message.             * 
             * */
           // final String selector = null;
            final String selector = "";
            for (int i = 0, length = p_cardModifyDetailList.size(); i < length; i++) {
                version = null;
                list = null;
                list = new ArrayList();

                list = (ArrayList) p_cardModifyDetailList.get(i);
                if ((list != null && list.isEmpty()) || list == null) {
                    p_cardModifyDetailList.remove(i);
                    continue;
                }
                cardGroupDetailsVO = (CardGroupDetailsVO) list.get(0);
                rsLastVersion = null;
                lastVersionSelect.clearParameters();
                index = 0;
                lastVersionSelect.setString(++index, cardGroupDetailsVO.getCardGroupSetID());
                lastVersionSelect.setString(++index, PretupsI.STATUS_ACTIVE);
                lastVersionSelect.setString(++index, cardGroupDetailsVO.getServiceTypeId());
                rsLastVersion = lastVersionSelect.executeQuery();
                if (rsLastVersion.next()) {
                    version = rsLastVersion.getString("last_version");
                    if (version != null) {
                        newVersion = new Integer(((new Integer(version).intValue()) + 1)).toString();
                    }
                }
                int errorRowNum=0;
                if ("Y".equalsIgnoreCase((cardGroupDetailsVO.getEditDetail()))) {
                    sort = null;
                    sort = new ListSorterUtil();
                    list = (ArrayList) sort.doSort("startRange", null, list);
                    startEndRange = true;
                    if (list != null && !list.isEmpty()) {
                        CardGroupDetailsVO preCardVO = (CardGroupDetailsVO) list.get(0);
                        CardGroupDetailsVO nextCardVO = null;
                        for (int k = 1, j = list.size(); k < j; k++) {
                            nextCardVO = (CardGroupDetailsVO) list.get(k);
                            if (nextCardVO.getStartRange() <= preCardVO.getEndRange()) {
                                startEndRange = false;
		                        errorRowNum=k;
                                break;
                            }
                            preCardVO = nextCardVO;
                        }
                    }
                }
                if (!startEndRange) {
		            errorVO=new ListValueVO("",(new Integer(cardGroupDetailsVO.getRowIndex()+errorRowNum)).toString(),p_messages.getMessage(p_locale,"cardgroup.cardgroupc2sdetails.modify.error.startendrange"));
                    errorList.add(errorVO);
                    continue;
                }
                rsCardApplicable = null;

                // If you want to modify future card group that have same
                // applicable date
                if ("Y".equalsIgnoreCase(cardGroupDetailsVO.getEditDetail())) {
                    modifyFlag = false;
                    cardApplicableSelect.clearParameters();
                    index = 0;
                    cardApplicableSelect.setString(++index, cardGroupDetailsVO.getCardGroupSetID());
                    cardApplicableSelect.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(cardGroupDetailsVO.getApplicableFrom()));
                    rsCardApplicable = cardApplicableSelect.executeQuery();
                    if (rsCardApplicable.next()) {
                        modifyFlag = true;
                        version = rsCardApplicable.getString("version");
                    }
                }

                if ("Y".equalsIgnoreCase(cardGroupDetailsVO.getEditDetail()) && modifyFlag) {
                    index = 0;
                    cardGrSetUpdate.setString(++index, version);
                    cardGrSetUpdate.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
                    cardGrSetUpdate.setString(++index, p_user);
                    cardGrSetUpdate.setString(++index, cardGroupDetailsVO.getCardGroupSetID());
                    index = 0;
                    cardDetDelete.clearParameters();
                    cardDetDelete.setString(++index, cardGroupDetailsVO.getCardGroupSetID());
                    cardDetDelete.setString(++index, version);
                    deleteCount = 0;
                    try {
                        deleteCount = cardDetDelete.executeUpdate();
                    } catch (SQLException sqe) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex())).toString(), p_messages.getMessage(p_locale,
                            "cardgroup.cardgroupc2sdetails.modify.error.exception.delete.cardgroupsetversion") + selector);
                        errorList.add(errorVO);
                        _log.errorTrace(METHOD_NAME, sqe);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                    if (deleteCount <= 0) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex())).toString(), p_messages.getMessage(p_locale,
                            "cardgroup.cardgroupc2sdetails.modify.error.exception.delete.cardgroupsetversion") + selector);
                        errorList.add(errorVO);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }

                    try {
                        deleteCount = 0;
                        bonusBundleDAO = new BonusBundleDAO();
                        deleteCount = bonusBundleDAO.deletePreviousBonus(p_con, cardGroupDetailsVO.getCardGroupSetID(), version);

                    } catch (Exception sqe) {
                        // Create New message and paste here
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex())).toString(), p_messages.getMessage(p_locale,
                            "cardgroup.cardgroupc2sdetails.modify.error.exception.delete.cardgroupsetBonusversion") + selector);
                        errorList.add(errorVO);
                        _log.errorTrace(METHOD_NAME, sqe);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                    if (deleteCount <= 0) {
                        // Create New message and paste here
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex())).toString(), p_messages.getMessage(p_locale,
                            "cardgroup.cardgroupc2sdetails.modify.error.exception.delete.cardgroupsetversion") + selector);
                        errorList.add(errorVO);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                    for (int t = 0, s = list.size(); t < s; t++) {
                        tmpCardGroupDetailsVO = (CardGroupDetailsVO) list.get(t);

                        final long id = IDGenerator.getNextID(PretupsI.CARD_GROUP_ID, TypesI.ALL);
                        tmpCardGroupDetailsVO.setVersion(version);
                        tmpCardGroupDetailsVO.setCardGroupID(String.valueOf(id));
                        index = 0;
                        int k =0;
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getCardGroupSetID());
                        cardgrDetailsInsert.setString(++k, version);// detailVO.getVersion());
                        cardgrDetailsInsert.setString(++k, String.valueOf(id));
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getStartRange());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getEndRange());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getValidityPeriodType());
                        cardgrDetailsInsert.setInt(++k, tmpCardGroupDetailsVO.getValidityPeriod());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getGracePeriod());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderTax1Name());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderTax1Type());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getSenderTax1Rate());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderTax2Name());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderTax2Type());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getSenderTax2Rate());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverTax1Name());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverTax1Type());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getReceiverTax1Rate());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverTax2Name());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverTax2Type());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getReceiverTax2Rate());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getBonusValidityValue());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderAccessFeeType());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getSenderAccessFeeRate());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMinSenderAccessFee());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMaxSenderAccessFee());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverAccessFeeType());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getReceiverAccessFeeRate());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMinReceiverAccessFee());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMaxReceiverAccessFee());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getCardGroupCode());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMultipleOf());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getStatus());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getVoucherType());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getVoucherSegment());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getVoucherProductId());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getOnline());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getBoth());
                        // Added for converson factor of sender & receiver.
                        if (tmpCardGroupDetailsVO.getSenderConvFactor() == null) {
                            tmpCardGroupDetailsVO.setSenderConvFactor("1");
                        }
                        if (tmpCardGroupDetailsVO.getReceiverConvFactor() == null) {
                            tmpCardGroupDetailsVO.setReceiverConvFactor("0");
                        }
                        cardgrDetailsInsert.setInt(++k, Integer.parseInt(tmpCardGroupDetailsVO.getSenderConvFactor()));
                        cardgrDetailsInsert.setInt(++k,  Integer.parseInt(tmpCardGroupDetailsVO.getReceiverConvFactor()));
                        // added for cos

                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getCosRequired());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getInPromo());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getCardName());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReversalPermitted());
                        cardgrDetailsInsert.setTimestamp(++k, BTSLUtil.getSQLDateTimeFromUtilDate(tmpCardGroupDetailsVO.getReversalModifiedDate()));
                        insertCount = 0;
                        try {
                            insertCount = cardgrDetailsInsert.executeUpdate();
                        } catch (SQLException sqe) {
                            errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex())).toString(), p_messages.getMessage(p_locale,
                                "cardgroup.cardgroupc2sdetails.modify.error.exception.insert.cardgroupdetails") + selector);
                            errorList.add(errorVO);
                            insertCount = 0;
                            _log.errorTrace(METHOD_NAME, sqe);
                            try {
                                if (p_con != null) {
                                    p_con.rollback();
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                            }
                            break;
                        }
                        cardgrDetailsInsert.clearParameters();
                        if (insertCount <= 0) {
                            errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex())).toString(), p_messages.getMessage(p_locale,
                                "cardgroup.cardgroupc2sdetails.modify.error.exception.insert.cardgroupdetails") + selector);
                            errorList.add(errorVO);
                            try {
                                if (p_con != null) {
                                    p_con.rollback();
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                            }
                            break;
                        }
                        bonusBundleDAO = new BonusBundleDAO();
                        bonusBundleDAO.addBonusBundleDetails(p_con, tmpCardGroupDetailsVO);
                    }
                    if (insertCount <= 0) {
                        continue;
                    }
                    index = 0;
                    cardGrSetVerUpdate.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
                    cardGrSetVerUpdate.setString(++index, p_user);
                    cardGrSetVerUpdate.setString(++index, cardGroupDetailsVO.getCardGroupSetID());
                    cardGrSetVerUpdate.setString(++index, version);
                    updateCount = 0;
                    try {
                        updateCount = cardGrSetVerUpdate.executeUpdate();
                    } catch (SQLException sqe) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex())).toString(), p_messages.getMessage(p_locale,
                            "cardgroup.cardgroupc2sdetails.modify.error.exception.update.cardgroupsetversion") + selector);
                        errorList.add(errorVO);
                        _log.errorTrace(METHOD_NAME, sqe);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                    cardGrSetVerUpdate.clearParameters();
                    if (updateCount <= 0) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex())).toString(), p_messages.getMessage(p_locale,
                            "cardgroup.cardgroupc2sdetails.modify.error.exception.update.cardgroupsetversion") + selector);
                        errorList.add(errorVO);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                    updateCount = 0;
                    try {
                        updateCount = cardGrSetUpdate.executeUpdate();
                    } catch (SQLException sqe) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex())).toString(), p_messages.getMessage(p_locale,
                            "cardgroup.cardgroupc2sdetails.modify.error.exception.update.cardgroupset") + selector);
                        errorList.add(errorVO);
                        _log.errorTrace(METHOD_NAME, sqe);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                    cardGrSetUpdate.clearParameters();
                    if (updateCount <= 0) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex())).toString(), p_messages.getMessage(p_locale,
                            "cardgroup.cardgroupc2sdetails.modify.error.exception.update.cardgroupset") + selector);
                        errorList.add(errorVO);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                } else {
                    index = 0;
                    cardGrSetUpdate.setString(++index, newVersion);
                    cardGrSetUpdate.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
                    cardGrSetUpdate.setString(++index, p_user);
                    cardGrSetUpdate.setString(++index, cardGroupDetailsVO.getCardGroupSetID());

                    index = 0;
                    cardGrSetVerInsert.setString(++index, cardGroupDetailsVO.getCardGroupSetID());
                    cardGrSetVerInsert.setString(++index, newVersion);
                    cardGrSetVerInsert.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(cardGroupDetailsVO.getApplicableFrom()));
                    cardGrSetVerInsert.setString(++index, p_user);
                    cardGrSetVerInsert.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
                    cardGrSetVerInsert.setString(++index, p_user);
                    cardGrSetVerInsert.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
                    insertCount = 0;
                    try {
                        insertCount = cardGrSetVerInsert.executeUpdate();
                    } catch (SQLException sqe) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex())).toString(), p_messages.getMessage(p_locale,
                            "cardgroup.cardgroupc2sdetails.modify.error.exception.insert.cardgroupsetver") + selector);
                        errorList.add(errorVO);
                        _log.errorTrace(METHOD_NAME, sqe);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                    cardGrSetVerInsert.clearParameters();
                    if (insertCount <= 0) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex())).toString(), p_messages.getMessage(p_locale,
                            "cardgroup.cardgroupc2sdetails.modify.error.exception.insert.cardgroupsetver") + selector);
                        errorList.add(errorVO);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                    updateCount = 0;
                    try {
                        updateCount = cardGrSetUpdate.executeUpdate();
                    } catch (SQLException sqe) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex())).toString(), p_messages.getMessage(p_locale,
                            "cardgroup.cardgroupc2sdetails.modify.error.exception.update.cardgroupset") + selector);
                        errorList.add(errorVO);
                        _log.errorTrace(METHOD_NAME, sqe);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                    cardGrSetUpdate.clearParameters();
                    if (updateCount <= 0) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex())).toString(), p_messages.getMessage(p_locale,
                            "cardgroup.cardgroupc2sdetails.modify.error.exception.update.cardgroupset") + selector);
                        errorList.add(errorVO);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }

                    for (int t = 0, s = list.size(); t < s; t++) {
                        tmpCardGroupDetailsVO = (CardGroupDetailsVO) list.get(t);
                        index = 0;
                        int k=0;
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getCardGroupSetID());
                        cardgrDetailsInsert.setString(++k, newVersion);// detailVO.getVersion());
                        tmpCardGroupDetailsVO.setVersion(newVersion);
                        tmpCardGroupDetailsVO.setCardGroupID(String.valueOf(IDGenerator.getNextID(PretupsI.CARD_GROUP_ID, TypesI.ALL)));
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getCardGroupID());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getStartRange());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getEndRange());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getValidityPeriodType());
                        cardgrDetailsInsert.setInt(++k, tmpCardGroupDetailsVO.getValidityPeriod());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getGracePeriod());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderTax1Name());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderTax1Type());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getSenderTax1Rate());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderTax2Name());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderTax2Type());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getSenderTax2Rate());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverTax1Name());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverTax1Type());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getReceiverTax1Rate());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverTax2Name());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverTax2Type());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getReceiverTax2Rate());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getBonusValidityValue());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderAccessFeeType());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getSenderAccessFeeRate());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMinSenderAccessFee());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMaxSenderAccessFee());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverAccessFeeType());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getReceiverAccessFeeRate());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMinReceiverAccessFee());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMaxReceiverAccessFee());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getCardGroupCode());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMultipleOf());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getStatus());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getVoucherType());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getVoucherSegment());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getVoucherProductId());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getOnline());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getBoth());
                        // Added for converson factor of sender & receiver.
                        if (tmpCardGroupDetailsVO.getSenderConvFactor() == null) {
                            tmpCardGroupDetailsVO.setSenderConvFactor("1");
                        }
                        if (tmpCardGroupDetailsVO.getReceiverConvFactor() == null) {
                            tmpCardGroupDetailsVO.setReceiverConvFactor("0");
                        }
                        cardgrDetailsInsert.setInt(++k, Integer.parseInt(tmpCardGroupDetailsVO.getSenderConvFactor()));
                        cardgrDetailsInsert.setInt(++k,  Integer.parseInt(tmpCardGroupDetailsVO.getReceiverConvFactor()));
                        // added for cos
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getCosRequired());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getInPromo());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getCardName());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReversalPermitted());
                        cardgrDetailsInsert.setTimestamp(++k, BTSLUtil.getSQLDateTimeFromUtilDate(tmpCardGroupDetailsVO.getReversalModifiedDate()));
                        insertCount = 0;
                        try {
                            insertCount = cardgrDetailsInsert.executeUpdate();
                        } catch (SQLException sqe) {
                            errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex())).toString(), p_messages.getMessage(p_locale,
                                "cardgroup.cardgroupc2sdetails.modify.error.exception.insert.cardgroupdetails") + selector);
                            errorList.add(errorVO);
                            _log.errorTrace(METHOD_NAME, sqe);
                            try {
                                if (p_con != null) {
                                    p_con.rollback();
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                            }
                            break;
                        }
                        cardgrDetailsInsert.clearParameters();
                        if (insertCount <= 0) {
                            errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex())).toString(), p_messages.getMessage(p_locale,
                                "cardgroup.cardgroupc2sdetails.modify.error.exception.insert.cardgroupdetails") + selector);
                            errorList.add(errorVO);
                            try {
                                if (p_con != null) {
                                    p_con.rollback();
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                            }
                            break;
                        }
                        bonusBundleDAO = new BonusBundleDAO();
                        bonusBundleDAO.addBonusBundleDetails(p_con, tmpCardGroupDetailsVO);
                    }
                }
                try {
                    if (p_con != null) {
                        p_con.commit();
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
            }

        } catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error("modifyCardGroupinBatch1", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchModifyCardGroupDAO[modifyCardGroupinBatch1]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "modifyCardGroupinBatch1", "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error("modifyCardGroupinBatch1", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchModifyCardGroupDAO[modifyCardGroupinBatch1]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "modifyCardGroupinBatch1", "error.general.processing");
        } finally {
        	try {
                if (rsLastVersion != null) {
                	rsLastVersion.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
        	try {
                if (cardApplicableSelect != null) {
                    cardApplicableSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (slabRangeSelect != null) {
                    slabRangeSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (selectormappingSelect != null) {
                    selectormappingSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (valPeriTypeSelect != null) {
                    valPeriTypeSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (cardGrpDetailsUpdate != null) {
                    cardGrpDetailsUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (cardGrSetUpdate != null) {
                    cardGrSetUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (cardGrSetVerUpdate != null) {
                    cardGrSetVerUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (cardGrSetVer1Update != null) {
                    cardGrSetVer1Update.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (cardgrDetailsInsert != null) {
                    cardgrDetailsInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (cardGrSetVerInsert != null) {
                    cardGrSetVerInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (cardDetDelete != null) {
                    cardDetDelete.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsCardApplicable != null) {
                    rsCardApplicable.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsSelectormapping != null) {
                    rsSelectormapping.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsValPeriType != null) {
                    rsValPeriType.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (lastVersionSelect != null) {
                	lastVersionSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            
            if (_log.isDebugEnabled()) {
                _log.debug("modifyCardGroupinBatch1", "Exiting: errorList size =" + errorList.size());
            }
        }
        return errorList;
    }
    
    public ArrayList<CardGroupDetailsVO> loadCardGroupProfileDetails(Connection p_con,String pnetworkCode) throws BTSLBaseException {
        final String methodName = "loadCardGroupProfileDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
            _log.debug(methodName,loggerValue);
        }

        ArrayList<CardGroupDetailsVO> cardList = new ArrayList<>();
        try {
                final StringBuilder qry = new StringBuilder(" SELECT VC.VOUCHER_TYPE,VT.NAME, VC.VOUCHER_SEGMENT,LK.lookup_name,");
                qry.append(" VP.mrp,VP.product_id,VP.product_name");
                qry.append(" FROM voms_products VP JOIN voms_categories VC ON VP.status = ? AND VC.NETWORK_CODE = ? AND VC.category_id = VP.category_id");
                qry.append(" JOIN lookups LK ON LK.lookup_code = VP.VOUCHER_SEGMENT AND LK.LOOKUP_TYPE = ?");
                qry.append(" JOIN VOMS_TYPES VT ON  VT.VOUCHER_TYPE = VC.VOUCHER_TYPE ORDER BY VP.product_id");
                final String selectQuery = qry.toString();
                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("select query:");
                	loggerValue.append(selectQuery);
                    _log.debug(methodName,loggerValue);
                }
                
                
                try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
                {
                	pstmtSelect.setString(1, PretupsI.STATUS_ACTIVE);
                	pstmtSelect.setString(2, pnetworkCode);
                	pstmtSelect.setString(3, VOMSI.VOUCHER_SEGMENT);
                try(ResultSet rs = pstmtSelect.executeQuery();)
                {
                while (rs.next()) {
                	CardGroupDetailsVO cardGroupDetailsVO = new CardGroupDetailsVO();
                    cardGroupDetailsVO.setVoucherType(SqlParameterEncoder.encodeParams(rs.getString("VOUCHER_TYPE")));
                    cardGroupDetailsVO.setVoucherTypeDesc(SqlParameterEncoder.encodeParams(rs.getString("NAME")));
                    cardGroupDetailsVO.setVoucherSegment(SqlParameterEncoder.encodeParams(rs.getString("VOUCHER_SEGMENT")));
                    cardGroupDetailsVO.setVoucherSegmentDesc(SqlParameterEncoder.encodeParams(rs.getString("lookup_name")));
                    cardGroupDetailsVO.setStartRange(rs.getLong("mrp"));
                    cardGroupDetailsVO.setEndRange(rs.getLong("mrp"));
                    cardGroupDetailsVO.setVoucherDenomination(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
                    cardGroupDetailsVO.setVoucherProductId(SqlParameterEncoder.encodeParams(rs.getString("product_id")));
                    cardGroupDetailsVO.setProductName(SqlParameterEncoder.encodeParams(rs.getString("product_name")));
                    cardList.add(cardGroupDetailsVO);
                }
            } 
                return cardList;
            }
            
        }// end of try
         catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, sqle);
			String logVal1=loggerValue.toString();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupProfileDetails]", "", "", "",
            		logVal1);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
			String logVal1=loggerValue.toString();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupProfileDetails]", "", "", "",
            		logVal1);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting cardList:" + cardList);
            }
        }// end of finally
    }
    
    /**
     * @author sarthak.saini
     * @param p_con
     * @param p_cardModifyDetailList
     * @param p_serviceType
     * @param p_locale
     * @param p_currentDate
     * @param p_user
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList modifyCardGroupinBatchAngular(Connection p_con, ArrayList p_cardModifyDetailList, String p_serviceType,  Locale p_locale, Date p_currentDate, String p_user) throws BTSLBaseException {
        
        final String METHOD_NAME = "modifyCardGroupinBatchAngular";
        if (_log.isDebugEnabled()) {
            _log.debug(
                METHOD_NAME,
                "Entered: p_cardDetailList.size()=" + p_cardModifyDetailList.size() + " p_serviceType=" + p_serviceType  + " p_locale=" + p_locale + ", p_currentDate=" + p_currentDate + ", p_user=" + p_user);
        }
        final ArrayList errorList = new ArrayList();
        ListValueVO errorVO = null;
        int index = 0;
        PreparedStatement cardApplicableSelect = null;
        final PreparedStatement slabRangeSelect = null;
        PreparedStatement selectormappingSelect = null;
        PreparedStatement valPeriTypeSelect = null;
        PreparedStatement cardGrpDetailsUpdate = null;
        PreparedStatement cardGrSetUpdate = null;
        PreparedStatement cardGrSetVerUpdate = null;
        PreparedStatement cardGrSetVer1Update = null;
        PreparedStatement cardgrDetailsInsert = null;
        PreparedStatement lastVersionSelect = null;
        PreparedStatement cardGrSetVerInsert = null;
        PreparedStatement cardDetDelete = null;
        BonusBundleDAO bonusBundleDAO = null;

        final StringBuilder selectCardApplicable = new StringBuilder("SELECT version from card_group_set_versions where card_group_set_id=? AND applicable_from=?");
        final String selectCardApplicableQuery = selectCardApplicable.toString();
        ResultSet rsCardApplicable = null;
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query selectCardApplicableQuery:" + selectCardApplicableQuery);
        }

        final StringBuilder selectSelectorMapping = new StringBuilder(" SELECT selector_code, selector_name FROM service_type_selector_mapping WHERE service_type=?");
        final String selectSelectorMappingQuery = selectSelectorMapping.toString();
        final ResultSet rsSelectormapping = null;
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query selectSelectormappingQuery:" + selectSelectorMappingQuery);
        }

        final StringBuilder selectValPeriType = new StringBuilder(" SELECT DISTINCT validity_period_type FROM card_group_details WHERE card_group_set_id=?");
        final String selectValPeriTypeQuery = selectValPeriType.toString();
        final ResultSet rsValPeriType = null;
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query selectValPeriTypeQuery:" + selectValPeriTypeQuery);
        }

        final StringBuilder selectLastVersion = new StringBuilder(" SELECT DISTINCT last_version FROM CARD_GROUP_SET WHERE card_group_set_id=? AND status=? AND SERVICE_TYPE=?");
        final String selectLastVersionQuery = selectLastVersion.toString();
        ResultSet rsLastVersion = null;
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query selectLastVersionQuery:" + selectLastVersionQuery);
        }

        final StringBuilder updateCardGrpDetails = new StringBuilder(
            "UPDATE card_group_details SET start_range=?, end_range=?, validity_period=?, grace_period=?, receiver_tax1_type=?,");
        if((PretupsI.VOUCHER_CONS_SERVICE).equals(p_serviceType)){
            updateCardGrpDetails.append(" voucher_type=?, voucher_segment=?, voucher_product_id=?");
        }
        updateCardGrpDetails.append(" receiver_tax1_type=?,receiver_tax1_rate=?, receiver_tax2_type=?, receiver_tax2_rate=?, bonus_talk_value_type=?, bonus_talk_value_rate=?,");
        updateCardGrpDetails.append(" bonus_validity_value=?, receiver_access_fee_type=?, receiver_access_fee_rate=?, min_receiver_access_fee=?,");
        updateCardGrpDetails.append(" max_receiver_access_fee=?, multiple_of=?, bonus1=?, bonus2=?, status=?, bonus_talk_time_validity=?, bonus_sms_validity=?,");
        updateCardGrpDetails
            .append(" bonus_mms_validity=?, validity_period_type=?, online_offline=?, both=?,cos_required=?,in_promo=?  WHERE card_group_set_id=? AND version=? AND card_group_code=?");
       
        final String updateCardgrDetailsQuery = updateCardGrpDetails.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query updateCardgrDetailsQuery:" + updateCardgrDetailsQuery);
        }

        final StringBuilder updateCardGrSet = new StringBuilder("UPDATE card_group_set SET last_version = ?, ");
        updateCardGrSet.append(" modified_on = ?, modified_by = ? WHERE card_group_set_id = ?");
        final String updateCardGrSetQuery = updateCardGrSet.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query updateCardGrSetQuery:" + updateCardGrSetQuery);
        }

        final StringBuilder updateCardGrSetVer = new StringBuilder(" UPDATE card_group_set_versions SET modified_on = ?, modified_by = ? ");
        updateCardGrSetVer.append(" WHERE card_group_set_id = ? and version = ?");
        final String updateCardGrSetVerQuery = updateCardGrSetVer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query updateCardGrSetVerQuery:" + updateCardGrSetVerQuery);
        }

        final StringBuilder updateCardGrSetVer1 = new StringBuilder("UPDATE card_group_set_versions SET applicable_from=?,modified_on = ?, modified_by = ? ");
        updateCardGrSetVer1.append(" WHERE card_group_set_id = ? and version = ?");
        final String updateCardGrSetVer1Query = updateCardGrSetVer1.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query updateCardGrSetVer1Query:" + updateCardGrSetVer1Query);
        }

        final StringBuilder deleteCardDet = new StringBuilder("DELETE FROM card_group_details ");
        deleteCardDet.append("WHERE card_group_set_id = ? and version = ?");
        final String deleteCardDetQuery = deleteCardDet.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query deleteCardDetQuery:" + deleteCardDetQuery);
        }

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("INSERT INTO card_group_details (card_group_set_id,");
        strBuff.append("version,card_group_id,start_range,end_range,validity_period_type,");
        strBuff.append("validity_period,grace_period,sender_tax1_name,sender_tax1_type, ");
        strBuff.append("sender_tax1_rate,sender_tax2_name,sender_tax2_type,sender_tax2_rate,");
        strBuff.append("receiver_tax1_name,receiver_tax1_type,receiver_tax1_rate,");
        strBuff.append("receiver_tax2_name,receiver_tax2_type,receiver_tax2_rate,bonus_validity_value,");
        strBuff.append("sender_access_fee_type,sender_access_fee_rate,min_sender_access_fee,");
        strBuff.append("max_sender_access_fee,receiver_access_fee_type,receiver_access_fee_rate,");
        strBuff.append("min_receiver_access_fee,max_receiver_access_fee,card_group_code,multiple_of,status,");
        strBuff.append("voucher_type,voucher_segment,voucher_product_id,");
        if(QueryConstants.DB_POSTGRESQL.equals(Constants.getProperty(QueryConstants.PRETUPS_DB))){
            strBuff.append("online_offline,\"both\",sender_mult_factor,receiver_mult_factor,cos_required,in_promo,card_name,reversal_permitted,reversal_modified_date )");     
        }else{
            strBuff.append("online_offline,both,sender_mult_factor,receiver_mult_factor,cos_required,in_promo,card_name,reversal_permitted,reversal_modified_date )");
        }
       
        strBuff.append(" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        final String insertCardgrDetailsQuery = strBuff.toString();
        // String insertCardgrDetailsQuery = insertCardgrDetails.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query insertCardgrDetailsQuery:" + insertCardgrDetailsQuery);
        }

        final StringBuilder insertCardGrSetVer = new StringBuilder("INSERT INTO card_group_set_versions (card_group_set_id, version,");
        insertCardGrSetVer.append(" applicable_from, created_by, created_on, modified_by, modified_on) VALUES (?,?,?,?,?,?,?)");
        final String insertCardGrSetVerQuery = insertCardGrSetVer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("modifyCardGroupinBatch1", "Query insertCardGrSetVerQuery:" + insertCardGrSetVerQuery);
        }

        try {
            cardApplicableSelect = p_con.prepareStatement(selectCardApplicableQuery);
            valPeriTypeSelect = p_con.prepareStatement(selectValPeriTypeQuery);
            selectormappingSelect = p_con.prepareStatement(selectSelectorMappingQuery);
            lastVersionSelect = p_con.prepareStatement(selectLastVersionQuery);
            cardGrpDetailsUpdate = p_con.prepareStatement(updateCardgrDetailsQuery);
            cardGrSetUpdate = p_con.prepareStatement(updateCardGrSetQuery);
            cardGrSetVerUpdate = p_con.prepareStatement(updateCardGrSetVerQuery);
            cardGrSetVer1Update = p_con.prepareStatement(updateCardGrSetVer1Query);
            cardgrDetailsInsert = p_con.prepareStatement(insertCardgrDetailsQuery);
            cardGrSetVerInsert = p_con.prepareStatement(insertCardGrSetVerQuery);
            cardDetDelete = p_con.prepareStatement(deleteCardDetQuery);

            String version = null;
            String newVersion = null;
            int updateCount = 0;
            int deleteCount = 0;
            int insertCount = 0;
            boolean startEndRange = true;
            boolean modifyFlag = false;
            CardGroupDetailsVO cardGroupDetailsVO = null;
            CardGroupDetailsVO tmpCardGroupDetailsVO = null;
            // Collections.sort(p_cardModifyDetailList);
            ArrayList list = null;
            ListSorterUtil sort = null;
            /*Modify by Lalit
             * Selector variable was initialized as null variable and 
             * No value is assigned in next process. I just initialized it as blank so it does not 
             * print null in error message.             * 
             * */
           // final String selector = null;
            String selector = "";
            for (int i = 0, length = p_cardModifyDetailList.size(); i < length; i++) {
                version = null;
                list = null;
                list = new ArrayList();

                list = (ArrayList) p_cardModifyDetailList.get(i);
                if ((list != null && list.isEmpty()) || list == null) {
                    p_cardModifyDetailList.remove(i);
                    continue;
                }
                cardGroupDetailsVO = (CardGroupDetailsVO) list.get(0);
                selector = cardGroupDetailsVO.getCardGroupSetID();
                rsLastVersion = null;
                lastVersionSelect.clearParameters();
                index = 0;
                lastVersionSelect.setString(++index, cardGroupDetailsVO.getCardGroupSetID());
                lastVersionSelect.setString(++index, PretupsI.STATUS_ACTIVE);
                lastVersionSelect.setString(++index, cardGroupDetailsVO.getServiceTypeId());
                rsLastVersion = lastVersionSelect.executeQuery();
                if (rsLastVersion.next()) {
                    version = rsLastVersion.getString("last_version");
                    if (version != null) {
                        newVersion = new Integer(((new Integer(version).intValue()) + 1)).toString();
                    }
                }
                int errorRowNum=0;
                if ("Y".equalsIgnoreCase((cardGroupDetailsVO.getEditDetail()))) {
                    sort = null;
                    sort = new ListSorterUtil();
                    list = (ArrayList) sort.doSort("startRange", null, list);
                    startEndRange = true;
                    if (list != null && !list.isEmpty()) {
                        CardGroupDetailsVO preCardVO = (CardGroupDetailsVO) list.get(0);
                        CardGroupDetailsVO nextCardVO = null;
                        for (int k = 1, j = list.size(); k < j; k++) {
                            nextCardVO = (CardGroupDetailsVO) list.get(k);
                            if (nextCardVO.getStartRange() <= preCardVO.getEndRange()) {
                                startEndRange = false;
		                        errorRowNum=k;
                                break;
                            }
                            preCardVO = nextCardVO;
                        }
                    }
                }
                if (!startEndRange) {
		            errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex()+errorRowNum)).toString(), RestAPIStringParser
							.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.error.startendrange", null));
		            errorList.add(errorVO);
                    continue;
                }
                rsCardApplicable = null;

                // If you want to modify future card group that have same
                // applicable date
                if ("Y".equalsIgnoreCase(cardGroupDetailsVO.getEditDetail())) {
                    modifyFlag = false;
                    cardApplicableSelect.clearParameters();
                    index = 0;
                    cardApplicableSelect.setString(++index, cardGroupDetailsVO.getCardGroupSetID());
                    cardApplicableSelect.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(cardGroupDetailsVO.getApplicableFrom()));
                    rsCardApplicable = cardApplicableSelect.executeQuery();
                    if (rsCardApplicable.next()) {
                        modifyFlag = true;
                        version = rsCardApplicable.getString("version");
                    }
                }

                if ("Y".equalsIgnoreCase(cardGroupDetailsVO.getEditDetail()) && modifyFlag) {
                    index = 0;
                    cardGrSetUpdate.setString(++index, version);
                    cardGrSetUpdate.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
                    cardGrSetUpdate.setString(++index, p_user);
                    cardGrSetUpdate.setString(++index, cardGroupDetailsVO.getCardGroupSetID());
                    index = 0;
                    cardDetDelete.clearParameters();
                    cardDetDelete.setString(++index, cardGroupDetailsVO.getCardGroupSetID());
                    cardDetDelete.setString(++index, version);
                    deleteCount = 0;
                    try {
                        deleteCount = cardDetDelete.executeUpdate();
                    } catch (SQLException sqe) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex()+errorRowNum)).toString(), RestAPIStringParser
    							.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.error.exception.delete.cardgroupsetversion", null));
    		            errorList.add(errorVO);
                        _log.errorTrace(METHOD_NAME, sqe);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                    selector =  cardGroupDetailsVO.getCardGroupSetID();
                    if (deleteCount <= 0) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex()+errorRowNum)).toString(), RestAPIStringParser
    							.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.error.exception.delete.cardgroupsetversion", null)+selector);
    		            errorList.add(errorVO);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }

                    try {
                        deleteCount = 0;
                        bonusBundleDAO = new BonusBundleDAO();
                        deleteCount = bonusBundleDAO.deletePreviousBonus(p_con, cardGroupDetailsVO.getCardGroupSetID(), version);

                    } catch (Exception sqe) {
                        // Create New message and paste here
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex()+errorRowNum)).toString(), RestAPIStringParser
    							.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.error.exception.delete.cardgroupsetBonusversion", null)+selector);
    		            errorList.add(errorVO);
                        _log.errorTrace(METHOD_NAME, sqe);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                    if (deleteCount <= 0) {
                        // Create New message and paste here
                       errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex()+errorRowNum)).toString(), RestAPIStringParser
    							.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.error.exception.delete.cardgroupsetversion", null)+selector);
    		            errorList.add(errorVO);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                    for (int t = 0, s = list.size(); t < s; t++) {
                        tmpCardGroupDetailsVO = (CardGroupDetailsVO) list.get(t);

                        final long id = IDGenerator.getNextID(PretupsI.CARD_GROUP_ID, TypesI.ALL);
                        tmpCardGroupDetailsVO.setVersion(version);
                        tmpCardGroupDetailsVO.setCardGroupID(String.valueOf(id));
                        index = 0;
                        int k =0;
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getCardGroupSetID());
                        cardgrDetailsInsert.setString(++k, version);// detailVO.getVersion());
                        cardgrDetailsInsert.setString(++k, String.valueOf(id));
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getStartRange());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getEndRange());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getValidityPeriodType());
                        cardgrDetailsInsert.setInt(++k, tmpCardGroupDetailsVO.getValidityPeriod());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getGracePeriod());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderTax1Name());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderTax1Type());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getSenderTax1Rate());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderTax2Name());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderTax2Type());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getSenderTax2Rate());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverTax1Name());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverTax1Type());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getReceiverTax1Rate());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverTax2Name());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverTax2Type());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getReceiverTax2Rate());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getBonusValidityValue());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderAccessFeeType());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getSenderAccessFeeRate());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMinSenderAccessFee());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMaxSenderAccessFee());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverAccessFeeType());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getReceiverAccessFeeRate());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMinReceiverAccessFee());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMaxReceiverAccessFee());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getCardGroupCode());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMultipleOf());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getStatus());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getVoucherType()==null?"NA":tmpCardGroupDetailsVO.getVoucherType());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getVoucherSegment()==null?"NA":tmpCardGroupDetailsVO.getVoucherSegment());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getVoucherProductId()==null?"NA":tmpCardGroupDetailsVO.getVoucherProductId());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getOnline());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getBoth());
                        // Added for converson factor of sender & receiver.
                        if (tmpCardGroupDetailsVO.getSenderConvFactor() == null) {
                            tmpCardGroupDetailsVO.setSenderConvFactor("1");
                        }
                        if (tmpCardGroupDetailsVO.getReceiverConvFactor() == null) {
                            tmpCardGroupDetailsVO.setReceiverConvFactor("0");
                        }
                        cardgrDetailsInsert.setInt(++k, Integer.parseInt(tmpCardGroupDetailsVO.getSenderConvFactor()));
                        cardgrDetailsInsert.setInt(++k,  Integer.parseInt(tmpCardGroupDetailsVO.getReceiverConvFactor()));
                        // added for cos

                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getCosRequired());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getInPromo());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getCardName());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReversalPermitted());
                        cardgrDetailsInsert.setTimestamp(++k, BTSLUtil.getSQLDateTimeFromUtilDate(tmpCardGroupDetailsVO.getReversalModifiedDate()));
                        insertCount = 0;
                        try {
                            insertCount = cardgrDetailsInsert.executeUpdate();
                        } catch (SQLException sqe) {
                            errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex()+errorRowNum)).toString(), RestAPIStringParser
        							.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.error.exception.insert.cardgroupdetails", null)+selector);
        		            errorVO.setIDValue(cardGroupDetailsVO.getCardGroupSetID());
                            errorList.add(errorVO);

                            insertCount = 0;
                            _log.errorTrace(METHOD_NAME, sqe);
                            try {
                                if (p_con != null) {
                                    p_con.rollback();
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                            }
                            break;
                        }
                        cardgrDetailsInsert.clearParameters();
                        if (insertCount <= 0) {
                            errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex()+errorRowNum)).toString(), RestAPIStringParser
        							.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.error.exception.insert.cardgroupdetails", null)+selector);
                            errorVO.setIDValue(cardGroupDetailsVO.getCardGroupSetID());
                            errorList.add(errorVO);
                            try {
                                if (p_con != null) {
                                    p_con.rollback();
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                            }
                            break;
                        }
                        bonusBundleDAO = new BonusBundleDAO();
                        bonusBundleDAO.addBonusBundleDetails(p_con, tmpCardGroupDetailsVO);
                    }
                    if (insertCount <= 0) {
                        continue;
                    }
                    index = 0;
                    cardGrSetVerUpdate.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
                    cardGrSetVerUpdate.setString(++index, p_user);
                    cardGrSetVerUpdate.setString(++index, cardGroupDetailsVO.getCardGroupSetID());
                    cardGrSetVerUpdate.setString(++index, version);
                    updateCount = 0;
                    try {
                        updateCount = cardGrSetVerUpdate.executeUpdate();
                    } catch (SQLException sqe) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex()+errorRowNum)).toString(), RestAPIStringParser
    							.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.error.exception.update.cardgroupsetversion", null)+selector);
                        errorVO.setIDValue(cardGroupDetailsVO.getCardGroupSetID());
                        errorList.add(errorVO);
                        _log.errorTrace(METHOD_NAME, sqe);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                    cardGrSetVerUpdate.clearParameters();
                    if (updateCount <= 0) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex()+errorRowNum)).toString(), RestAPIStringParser
    							.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.error.exception.update.cardgroupsetversion", null)+selector);
                        errorVO.setIDValue(cardGroupDetailsVO.getCardGroupSetID());
                        errorList.add(errorVO);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                    updateCount = 0;
                    try {
                        updateCount = cardGrSetUpdate.executeUpdate();
                    } catch (SQLException sqe) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex()+errorRowNum)).toString(), RestAPIStringParser
    							.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.error.exception.update.cardgroupset", null)+selector);
                        errorVO.setIDValue(cardGroupDetailsVO.getCardGroupSetID());
                        errorList.add(errorVO);
                        _log.errorTrace(METHOD_NAME, sqe);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                    cardGrSetUpdate.clearParameters();
                    if (updateCount <= 0) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex()+errorRowNum)).toString(), RestAPIStringParser
    							.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.error.exception.update.cardgroupset", null)+selector);
                        errorVO.setIDValue(cardGroupDetailsVO.getCardGroupSetID());
                        errorList.add(errorVO);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                } else {
                    index = 0;
                    cardGrSetUpdate.setString(++index, newVersion);
                    cardGrSetUpdate.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
                    cardGrSetUpdate.setString(++index, p_user);
                    cardGrSetUpdate.setString(++index, cardGroupDetailsVO.getCardGroupSetID());

                    index = 0;
                    cardGrSetVerInsert.setString(++index, cardGroupDetailsVO.getCardGroupSetID());
                    cardGrSetVerInsert.setString(++index, newVersion);
                    cardGrSetVerInsert.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(cardGroupDetailsVO.getApplicableFrom()));
                    cardGrSetVerInsert.setString(++index, p_user);
                    cardGrSetVerInsert.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
                    cardGrSetVerInsert.setString(++index, p_user);
                    cardGrSetVerInsert.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
                    insertCount = 0;
                    try {
                        insertCount = cardGrSetVerInsert.executeUpdate();
                    } catch (SQLException sqe) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex()+errorRowNum)).toString(), RestAPIStringParser
    							.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.error.exception.insert.cardgroupsetver", null)+selector);
                        errorVO.setIDValue(cardGroupDetailsVO.getCardGroupSetID());
                        errorList.add(errorVO);
                        _log.errorTrace(METHOD_NAME, sqe);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                    cardGrSetVerInsert.clearParameters();
                    if (insertCount <= 0) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex()+errorRowNum)).toString(), RestAPIStringParser
    							.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.error.exception.insert.cardgroupsetver", null)+selector);
                        errorVO.setIDValue(cardGroupDetailsVO.getCardGroupSetID());
                        errorList.add(errorVO);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                    updateCount = 0;
                    try {
                        updateCount = cardGrSetUpdate.executeUpdate();
                    } catch (SQLException sqe) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex()+errorRowNum)).toString(), RestAPIStringParser
    							.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.error.exception.update.cardgroupset", null)+selector);
                        errorVO.setIDValue(cardGroupDetailsVO.getCardGroupSetID());
                        errorList.add(errorVO);
                        _log.errorTrace(METHOD_NAME, sqe);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }
                    cardGrSetUpdate.clearParameters();
                    if (updateCount <= 0) {
                        errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex()+errorRowNum)).toString(), RestAPIStringParser
    							.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.error.exception.update.cardgroupset", null)+selector);
                        errorVO.setIDValue(cardGroupDetailsVO.getCardGroupSetID());
                        errorList.add(errorVO);
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                        continue;
                    }

                    for (int t = 0, s = list.size(); t < s; t++) {
                        tmpCardGroupDetailsVO = (CardGroupDetailsVO) list.get(t);
                        index = 0;
                        int k=0;
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getCardGroupSetID());
                        cardgrDetailsInsert.setString(++k, newVersion);// detailVO.getVersion());
                        tmpCardGroupDetailsVO.setVersion(newVersion);
                        tmpCardGroupDetailsVO.setCardGroupID(String.valueOf(IDGenerator.getNextID(PretupsI.CARD_GROUP_ID, TypesI.ALL)));
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getCardGroupID());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getStartRange());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getEndRange());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getValidityPeriodType());
                        cardgrDetailsInsert.setInt(++k, tmpCardGroupDetailsVO.getValidityPeriod());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getGracePeriod());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderTax1Name());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderTax1Type());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getSenderTax1Rate());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderTax2Name());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderTax2Type());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getSenderTax2Rate());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverTax1Name());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverTax1Type());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getReceiverTax1Rate());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverTax2Name());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverTax2Type());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getReceiverTax2Rate());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getBonusValidityValue());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getSenderAccessFeeType());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getSenderAccessFeeRate());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMinSenderAccessFee());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMaxSenderAccessFee());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReceiverAccessFeeType());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getReceiverAccessFeeRate());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMinReceiverAccessFee());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMaxReceiverAccessFee());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getCardGroupCode());
                        cardgrDetailsInsert.setLong(++k, tmpCardGroupDetailsVO.getMultipleOf());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getStatus());
                        
                        
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getVoucherType()==null?"NA":tmpCardGroupDetailsVO.getVoucherType());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getVoucherSegment()==null?"NA":tmpCardGroupDetailsVO.getVoucherSegment());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getVoucherProductId()==null?"NA":tmpCardGroupDetailsVO.getVoucherProductId());
                        
                        
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getOnline());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getBoth());
                        // Added for converson factor of sender & receiver.
                        if (tmpCardGroupDetailsVO.getSenderConvFactor() == null) {
                            tmpCardGroupDetailsVO.setSenderConvFactor("1");
                        }
                        if (tmpCardGroupDetailsVO.getReceiverConvFactor() == null) {
                            tmpCardGroupDetailsVO.setReceiverConvFactor("0");
                        }
                        cardgrDetailsInsert.setInt(++k, Integer.parseInt(tmpCardGroupDetailsVO.getSenderConvFactor()));
                        cardgrDetailsInsert.setInt(++k,  Integer.parseInt(tmpCardGroupDetailsVO.getReceiverConvFactor()));
                        // added for cos
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getCosRequired());
                        cardgrDetailsInsert.setDouble(++k, tmpCardGroupDetailsVO.getInPromo());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getCardName());
                        cardgrDetailsInsert.setString(++k, tmpCardGroupDetailsVO.getReversalPermitted());
                        cardgrDetailsInsert.setTimestamp(++k, BTSLUtil.getSQLDateTimeFromUtilDate(tmpCardGroupDetailsVO.getReversalModifiedDate()));
                        insertCount = 0;
                        try {
                            insertCount = cardgrDetailsInsert.executeUpdate();
                        } catch (SQLException sqe) {
                            errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex()+errorRowNum)).toString(), RestAPIStringParser
        							.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.error.exception.insert.cardgroupdetails", null)+selector);
                            errorVO.setIDValue(cardGroupDetailsVO.getCardGroupSetID());
                            errorList.add(errorVO);
                            _log.errorTrace(METHOD_NAME, sqe);
                            try {
                                if (p_con != null) {
                                    p_con.rollback();
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                            }
                            break;
                        }
                        cardgrDetailsInsert.clearParameters();
                        if (insertCount <= 0) {
                            errorVO = new ListValueVO("", (new Integer(cardGroupDetailsVO.getRowIndex()+errorRowNum)).toString(), RestAPIStringParser
        							.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.error.exception.insert.cardgroupdetails", null)+selector);
                            errorVO.setIDValue(cardGroupDetailsVO.getCardGroupSetID());
                            errorList.add(errorVO);
                            try {
                                if (p_con != null) {
                                    p_con.rollback();
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                            }
                            break;
                        }
                        bonusBundleDAO = new BonusBundleDAO();
                        bonusBundleDAO.addBonusBundleDetails(p_con, tmpCardGroupDetailsVO);
                    }
                }
                try {
                    if (p_con != null) {
                        p_con.commit();
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
            }

        } catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error("modifyCardGroupinBatch1", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchModifyCardGroupDAO[modifyCardGroupinBatch1]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "modifyCardGroupinBatch1", "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error("modifyCardGroupinBatch1", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchModifyCardGroupDAO[modifyCardGroupinBatch1]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "modifyCardGroupinBatch1", "error.general.processing");
        } finally {
        	try {
                if (rsLastVersion != null) {
                	rsLastVersion.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
        	try {
                if (cardApplicableSelect != null) {
                    cardApplicableSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (slabRangeSelect != null) {
                    slabRangeSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (selectormappingSelect != null) {
                    selectormappingSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (valPeriTypeSelect != null) {
                    valPeriTypeSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (cardGrpDetailsUpdate != null) {
                    cardGrpDetailsUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (cardGrSetUpdate != null) {
                    cardGrSetUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (cardGrSetVerUpdate != null) {
                    cardGrSetVerUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (cardGrSetVer1Update != null) {
                    cardGrSetVer1Update.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (cardgrDetailsInsert != null) {
                    cardgrDetailsInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (cardGrSetVerInsert != null) {
                    cardGrSetVerInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (cardDetDelete != null) {
                    cardDetDelete.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsCardApplicable != null) {
                    rsCardApplicable.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsSelectormapping != null) {
                    rsSelectormapping.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsValPeriType != null) {
                    rsValPeriType.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (lastVersionSelect != null) {
                	lastVersionSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            
            if (_log.isDebugEnabled()) {
                _log.debug("modifyCardGroupinBatch1", "Exiting: errorList size =" + errorList.size());
            }
        }
        return errorList;
    }
    public ArrayList loadCardGroupDetailsListByDateAngular(Connection p_con, String p_date, String p_networkCode, String p_serviceType, String p_module,ArrayList p_cgSetselectedList,boolean downlaodFile) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadCardGroupDetailsListByDate",
                    "Entered p_date=" + p_date + ", p_networkCode:" + p_networkCode + ", p_serviceType:" + p_serviceType + ", p_module:" + p_module+ ", p_cgSetselectedList:" + p_cgSetselectedList);
        }
        final String METHOD_NAME = "loadCardGroupDetailsListByDate";
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtAppSelect = null;
        ResultSet rs = null;
        ResultSet appRs = null;
        PreparedStatement pstmtSelect1 = null;
        final ArrayList cardGroupDetailList = new ArrayList();

        CardGroupDetailsVO cardGroupDetailsVO = null;
        BonusBundleDAO bonusBundleDAO = null;
        try {
            bonusBundleDAO = new BonusBundleDAO();
            String selectQuery;
            if (downlaodFile)
                selectQuery = batchModifyCardGroupQry.loadCardGroupDetailsListByDateQryFile(p_serviceType, p_cgSetselectedList);
            else
                selectQuery = batchModifyCardGroupQry.loadCardGroupDetailsListByDateQry(p_serviceType, p_cgSetselectedList);
            final StringBuilder appqry = new StringBuilder(" ");
            appqry.append("SELECT card_group_set_id,version,applicable_from FROM card_group_set_versions");
            appqry.append(" WHERE card_group_set_id = ? AND version = ?");
            final String selectAppQuery = appqry.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("loadCardGroupDetailsListByDate", "select selectQuery:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            if (_log.isDebugEnabled()) {
                _log.debug("loadCardGroupDetailsListByDate", "select selectAppQuery:" + selectAppQuery);
            }

            StringBuilder qry1 = new StringBuilder();
            qry1.append(" select cad.card_group_set_id,cad.version,cad.card_group_id,cad.bundle_id,cad.type,cad.validity,");
            qry1.append(" cad.value,cad.mult_factor,bbm.bundle_name,bbm.bundle_type,bbm.res_in_status,bbm.bundle_code");
            qry1.append(" from card_group_sub_bon_acc_details cad, bonus_bundle_master bbm");
            qry1.append(" where cad.bundle_id=bbm.bundle_id");
            qry1.append(" and card_group_set_id=? and version=? and card_group_id=? order by bbm.bundle_id ");
            final String selectQuery1 = qry1.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("loadCardGroupDetailsListByDate", "select selectAppQuery:" + selectQuery1);
            }
            pstmtSelect1 = p_con.prepareStatement(selectQuery1);

            pstmtAppSelect = p_con.prepareStatement(selectAppQuery);

            int i = 0;
//            if (downlaodFile) {
//                pstmtSelect.setString(++i, p_module);
//                pstmtSelect.setString(++i, p_networkCode);
//                if (!p_serviceType.equalsIgnoreCase(PretupsI.ALL)) {
//                    pstmtSelect.setString(++i, p_serviceType);
//                }
//                pstmtSelect.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(BTSLUtil.getDateFromDateString(p_date, PretupsI.TIMESTAMP_DATESPACEHHMM)));
//            } else {
                pstmtSelect.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(BTSLUtil.getDateFromDateString(p_date, PretupsI.TIMESTAMP_DATESPACEHHMM)));
                pstmtSelect.setString(++i, p_module);
                pstmtSelect.setString(++i, p_networkCode);
                if (!p_serviceType.equalsIgnoreCase(PretupsI.ALL)) {
                    pstmtSelect.setString(++i, p_serviceType);
                }
//            }
            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                cardGroupDetailsVO = new CardGroupDetailsVO();
                cardGroupDetailsVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                cardGroupDetailsVO.setCardGroupID(rs.getString("card_group_id"));
                cardGroupDetailsVO.setCardGroupSetName(rs.getString("card_group_set_name"));
                cardGroupDetailsVO.setNetworkCode(rs.getString("network_code"));
                cardGroupDetailsVO.setCardGroupCode(rs.getString("card_group_code"));
                cardGroupDetailsVO.setStartRange(rs.getLong("start_range"));
                cardGroupDetailsVO.setEndRange(rs.getLong("end_range"));
                cardGroupDetailsVO.setValidityPeriod(rs.getInt("validity_period"));
                cardGroupDetailsVO.setGracePeriod(rs.getLong("grace_period"));
                cardGroupDetailsVO.setMultipleOf(rs.getLong("multiple_of"));
                cardGroupDetailsVO.setReceiverTax1Name(rs.getString("receiver_tax1_name"));
                cardGroupDetailsVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
                cardGroupDetailsVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                cardGroupDetailsVO.setReceiverTax2Name(rs.getString("receiver_tax2_name"));
                cardGroupDetailsVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
                cardGroupDetailsVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                cardGroupDetailsVO.setReceiverAccessFeeType(rs.getString("receiver_access_fee_type"));
                cardGroupDetailsVO.setReceiverAccessFeeRate(rs.getDouble("receiver_access_fee_rate"));
                cardGroupDetailsVO.setMinReceiverAccessFee(rs.getLong("min_receiver_access_fee"));
                cardGroupDetailsVO.setMaxReceiverAccessFee(rs.getLong("max_receiver_access_fee"));

                // for P2P
                cardGroupDetailsVO.setSenderTax1Name(rs.getString("sender_tax1_name"));
                cardGroupDetailsVO.setSenderTax1Type(rs.getString("sender_tax1_type"));
                cardGroupDetailsVO.setSenderTax1Rate(rs.getDouble("sender_tax1_rate"));
                cardGroupDetailsVO.setSenderTax2Name(rs.getString("sender_tax2_name"));
                cardGroupDetailsVO.setSenderTax2Type(rs.getString("sender_tax2_type"));
                cardGroupDetailsVO.setSenderTax2Rate(rs.getDouble("sender_tax2_rate"));
                cardGroupDetailsVO.setSenderAccessFeeType(rs.getString("sender_access_fee_type"));
                cardGroupDetailsVO.setSenderAccessFeeRate(rs.getDouble("sender_access_fee_rate"));
                cardGroupDetailsVO.setMinSenderAccessFee(rs.getLong("min_sender_access_fee"));
                cardGroupDetailsVO.setMaxSenderAccessFee(rs.getLong("max_sender_access_fee"));
                cardGroupDetailsVO.setCardGroupSubServiceId(rs.getString("sub_service"));
                cardGroupDetailsVO.setServiceTypeId(rs.getString("service_type"));
                cardGroupDetailsVO.setBonusValidityValue(rs.getInt("bonus_validity_value"));
                cardGroupDetailsVO.setVersion(rs.getString("version"));
                cardGroupDetailsVO.setLastVersion(rs.getString("last_version"));
                cardGroupDetailsVO.setValidityPeriodType(rs.getString("validity_period_type"));
                cardGroupDetailsVO.setOnline(rs.getString("online_offline"));
                cardGroupDetailsVO.setBoth(rs.getString("both"));
                cardGroupDetailsVO.setStatus(rs.getString("status"));
                cardGroupDetailsVO.setVoucherType(rs.getString("voucher_type"));
                cardGroupDetailsVO.setVoucherSegment(rs.getString("voucher_segment"));
                cardGroupDetailsVO.setVoucherProductId(rs.getString("voucher_product_id"));
                if(!BTSLUtil.isNullString(cardGroupDetailsVO.getVoucherType()))
                    cardGroupDetailsVO.setVoucherDenomination(cardGroupDetailsVO.getStartRangeAsString());
                // Added for conversion from one account to another.
                if(rs.getString("sender_mult_factor").indexOf('.')>-1){
                    cardGroupDetailsVO.setSenderConvFactor(rs.getString("sender_mult_factor").substring(0,rs.getString("sender_mult_factor").indexOf('.')));
                }else{
                    cardGroupDetailsVO.setSenderConvFactor(rs.getString("sender_mult_factor"));
                }
                if(rs.getString("receiver_mult_factor").indexOf('.')>-1){
                    cardGroupDetailsVO.setReceiverConvFactor(rs.getString("receiver_mult_factor").substring(0,rs.getString("receiver_mult_factor").indexOf('.')));
                }else{
                    cardGroupDetailsVO.setReceiverConvFactor(rs.getString("receiver_mult_factor"));
                }

                // added for cos
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
                    cardGroupDetailsVO.setCosRequired(rs.getString("cos_required"));
                }
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED)).booleanValue()) {
                    cardGroupDetailsVO.setInPromo(rs.getDouble("in_promo"));
                }
                pstmtAppSelect.setString(1, rs.getString("card_group_set_id"));
                pstmtAppSelect.setString(2, rs.getString("version"));
                appRs = pstmtAppSelect.executeQuery();
                pstmtAppSelect.clearParameters();
                if (appRs.next()) {
                    cardGroupDetailsVO.setApplicableFrom(appRs.getTimestamp("applicable_from"));
                    cardGroupDetailsVO.setOldApplicableFrom(appRs.getTimestamp("applicable_from").getTime());
                }
                // Added for dynamic bonus bundle feature.
                // Set the Bonus Accounts associated with the card group.
                cardGroupDetailsVO.setBonusAccList(bonusBundleDAO.loadBonusAccDetails(p_con, pstmtSelect1, cardGroupDetailsVO));

                cardGroupDetailsVO.setCardName(rs.getString("card_name"));
                cardGroupDetailsVO.setReversalPermitted(rs.getString("reversal_permitted"));
                cardGroupDetailList.add(cardGroupDetailsVO);
            }
            return cardGroupDetailList;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadCardGroupDetailsListByDate", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "BatchModifyCardGroupDAO[loadCardGroupDetailsListByDate]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("BatchModifyCardGroupDAO", "loadCardGroupDetailsListByDate", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadCardGroupDetailsListByDate", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "BatchModifyCardGroupDAO[loadCardGroupDetailsListByDate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("BatchModifyCardGroupDAO", "loadCardGroupDetailsListByDate", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (pstmtSelect1 != null) {
                    pstmtSelect1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (appRs != null) {
                    appRs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtAppSelect != null) {
                    pstmtAppSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadCardGroupDetailsListByDate", "Exiting CardGroupDetailList Size:" + cardGroupDetailList.size());
            }
        }// end of finally
    }

}
