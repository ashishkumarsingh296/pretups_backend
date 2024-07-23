package com.btsl.pretups.cardgroup.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
import com.btsl.util.SqlParameterEncoder;


public class CardGroupDAO {

    private final Log _log = LogFactory.getLog(this.getClass().getName());
    private CardGroupQry cardGroupQry= (CardGroupQry)ObjectProducer.getObject(QueryConstants.CARD_GROUP_DAO_QRY, QueryConstants.QUERY_PRODUCER);
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
        final String methodName = "loadCardGroupDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_cardGroupSetID=");
        	loggerValue.append(p_cardGroupSetID);
			loggerValue.append(" p_requestAmount=");
        	loggerValue.append(p_requestAmount);
        	loggerValue.append(" p_applicableDate=");
        	loggerValue.append(p_applicableDate);
            _log.debug(methodName,loggerValue);
        }
        
        CardGroupDetailsVO cardGroupDetailsVO = null;
        String latestCardGroupVersion = null;
        BonusBundleDAO bonusBundleDAO = null;
        try {

            latestCardGroupVersion = loadCardGroupSetVersionLatestVersion(p_con, p_cardGroupSetID, p_applicableDate);
            if (!BTSLUtil.isNullString(latestCardGroupVersion)) {
                final StringBuilder qry = new StringBuilder(" SELECT l.lookup_name set_name,cs.set_type,cgd.card_group_set_id, ");
                qry.append(" cgd.card_group_id,cgd.card_group_code,cgd.start_range,cgd.end_range,cgd.validity_period_type, ");
                qry.append(" cgd.validity_period,cgd.grace_period, cgd.sender_tax1_name, cgd.sender_tax1_type, ");
                qry.append(" cgd.sender_tax1_rate, cgd.sender_tax2_name, cgd.sender_tax2_type, cgd.sender_tax2_rate, ");
                qry.append(" cgd.receiver_tax1_name,cgd.receiver_tax1_type, cgd.receiver_tax1_rate,cgd.receiver_tax2_name, ");
                qry.append(" cgd.receiver_tax2_type, cgd.receiver_tax2_rate, ");
                qry.append(" cgd.sender_access_fee_type, cgd.sender_access_fee_rate, ");
                qry.append(" cgd.receiver_access_fee_type,cgd.receiver_access_fee_rate, cgd.min_sender_access_fee, ");
                qry.append(" cgd.max_sender_access_fee, cgd.min_receiver_access_fee,cgd.max_receiver_access_fee, cgd.multiple_of , ");
                qry.append(" cs.sub_service,cs.card_group_set_name, stsm.selector_name, cs.service_type, st.name service_name, cgd.status, cgd.bonus_validity_value,cgd.online_offline,cgd.both,cgd.both,");
                qry.append(" cgd.sender_mult_factor,cgd.receiver_mult_factor,cgd.cos_required ,cgd.in_promo, cgd.card_name, cgd.reversal_permitted, reversal_modified_date ,cgd.VOUCHER_TYPE,cgd.VOUCHER_SEGMENT,cgd.VOUCHER_PRODUCT_ID");
                if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CGTAX34APP)).booleanValue()){
					qry.append(" ,cgd.receiver_tax3_name,cgd.receiver_tax3_type,cgd.receiver_tax3_rate, ");
					qry.append(" cgd.receiver_tax4_name,cgd.receiver_tax4_type,cgd.receiver_tax4_rate ");
				 }
                qry.append(" FROM card_group_details cgd,lookups l, card_group_set cs, service_type st,service_type_selector_mapping stsm ");
                qry.append(" WHERE cgd.card_group_set_id=? AND cgd.version=? AND (cgd.start_range<=? ");
                qry.append(" AND cgd.end_range>=?) AND l.lookup_type=? AND cs.card_group_set_id=cgd.card_group_set_id ");
                qry.append(" AND cs.service_type=st.service_type AND stsm.service_type=st.service_type ");
                qry.append(" AND cs.sub_service=stsm.selector_code AND cs.service_type=stsm.service_type AND l.lookup_code=cs.set_type");
                final String selectQuery = qry.toString();
                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("select query:");
                	loggerValue.append(selectQuery);
                    _log.debug(methodName,loggerValue);
                }
                
                StringBuilder qry1 = new StringBuilder();
                qry1.append(" select cad.card_group_set_id,cad.version,cad.card_group_id,cad.bundle_id,cad.type,cad.validity,");
                qry1.append(" cad.value,cad.mult_factor,bbm.bundle_name,bbm.bundle_type,bbm.res_in_status,bbm.bundle_code");
                qry1.append(" from card_group_sub_bon_acc_details cad, bonus_bundle_master bbm");
                qry1.append(" where cad.bundle_id=bbm.bundle_id");
                qry1.append(" and card_group_set_id=? and version=? and card_group_id=? order by bbm.bundle_id ");
                final String selectQuery1 = qry1.toString();
                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("select selectAppQuery:");
                	loggerValue.append(selectQuery1);
                    _log.debug("loadCardGroupDetailsListByDate",loggerValue);
                }
                try(PreparedStatement pstmtSelect1 = p_con.prepareStatement(selectQuery1);)
                {
                bonusBundleDAO = new BonusBundleDAO();

                try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
                {
                pstmtSelect.setString(1, p_cardGroupSetID);
                pstmtSelect.setString(2, latestCardGroupVersion);
                pstmtSelect.setLong(3, p_requestAmount);
                pstmtSelect.setLong(4, p_requestAmount);
                pstmtSelect.setString(5, PretupsI.CARD_GROUP_SET_TYPE);
                try(ResultSet rs = pstmtSelect.executeQuery();)
                {
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
                    cardGroupDetailsVO.setVoucherSegment(rs.getString("VOUCHER_SEGMENT"));
                    cardGroupDetailsVO.setVoucherType(rs.getString("VOUCHER_TYPE"));
                    cardGroupDetailsVO.setVoucherProductId(rs.getString("VOUCHER_PRODUCT_ID"));
                    // added by gaurav for cos
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
                        cardGroupDetailsVO.setCosRequired(rs.getString("cos_required"));
                        if (BTSLUtil.isNullString(cardGroupDetailsVO.getCosRequired()) || "N".equalsIgnoreCase(cardGroupDetailsVO.getCosRequired())) {
                            cardGroupDetailsVO.setCosRequired("N");
                        } else {
                            cardGroupDetailsVO.setCosRequired("Y");
                        }
                    }
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED)).booleanValue()) {
                        cardGroupDetailsVO.setInPromo(rs.getDouble("in_promo"));
                    }
                    cardGroupDetailsVO.setCardName(rs.getString("card_name"));
                    cardGroupDetailsVO.setReversalPermitted(rs.getString("reversal_permitted"));
                    cardGroupDetailsVO.setReversalModifiedDate(rs.getDate("reversal_modified_date"));
                    if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CGTAX34APP)).booleanValue())
					{
						if(!BTSLUtil.isNullString(rs.getString("receiver_tax3_name")))
						cardGroupDetailsVO.setReceiverTax3Name(rs.getString("receiver_tax3_name"));
							else
								cardGroupDetailsVO.setReceiverTax3Name(BTSLUtil.NullToString(rs.getString("receiver_tax3_name")));
							if(!BTSLUtil.isNullString(rs.getString("receiver_tax3_type")))
								cardGroupDetailsVO.setReceiverTax3Type(rs.getString("receiver_tax3_type"));
							else
								cardGroupDetailsVO.setReceiverTax3Type(BTSLUtil.NullToString(rs.getString("receiver_tax3_type")));
							if(!BTSLUtil.isNullString(rs.getString("receiver_tax4_name")))
								cardGroupDetailsVO.setReceiverTax4Name(rs.getString("receiver_tax4_name"));
							else
								cardGroupDetailsVO.setReceiverTax4Name(BTSLUtil.NullToString(rs.getString("receiver_tax4_name")));
							if(!BTSLUtil.isNullString(rs.getString("receiver_tax4_type")))
								cardGroupDetailsVO.setReceiverTax4Type(rs.getString("receiver_tax4_type"));
							else
								cardGroupDetailsVO.setReceiverTax4Type(BTSLUtil.NullToString(rs.getString("receiver_tax4_type")));
							cardGroupDetailsVO.setReceiverTax3Rate(rs.getDouble("receiver_tax3_rate"));					
							cardGroupDetailsVO.setReceiverTax4Rate(rs.getDouble("receiver_tax4_rate"));
	                }

                    // Set the Bonus Accounts associated with the card group.
                    cardGroupDetailsVO.setBonusAccList(bonusBundleDAO.loadBonusAccDetails(p_con, pstmtSelect1, cardGroupDetailsVO));
                } else {
                    throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.CARD_GROUP_VALUE_NOT_IN_RANGE, 0, new String[] { PretupsBL
                        .getDisplayAmount(p_requestAmount) }, null);
                }
            } 
            }
            }
            }else {
                throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.CARD_GROUP_SETVERNOT_ASSOCIATED);
            }
            return cardGroupDetailsVO;
        }// end of try
        catch (BTSLBaseException bex) {
        	loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException ");
        	loggerValue.append(bex.getMessage());
            _log.error(methodName,loggerValue);
			String logVal1=loggerValue.toString();
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CardGroupDAO[loadCardGroupDetails]", "", "", "",
            		logVal1);
            throw bex;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, sqle);
			String logVal1=loggerValue.toString();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupDetails]", "", "", "",
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupDetails]", "", "", "",
            		logVal1);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting CardGroupDetailsVO:" + cardGroupDetailsVO);
            }
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
        final String methodName = "loadCardGroupSetVersionLatestVersion";
        StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered p_cardGroupSetID=");
    	loggerValue.append(p_cardGroupSetID);
		loggerValue.append(" p_applicableDate=");
    	loggerValue.append(p_applicableDate);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,loggerValue);
        }
        
         
        String latestCardGroupVersion = null;
        try {
            final StringBuilder selectQueryBuff = new StringBuilder(" SELECT version ");
            selectQueryBuff.append(" FROM card_group_set_versions cgd ");
            selectQueryBuff.append(" WHERE cgd.card_group_set_id=? ");
            selectQueryBuff.append(" AND applicable_from =(SELECT MAX(applicable_from) ");
            selectQueryBuff.append(" FROM card_group_set_versions ");
            selectQueryBuff.append(" WHERE  applicable_from<=? AND card_group_set_id=cgd.card_group_set_id) ");
            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("select query:");
            	loggerValue.append(selectQuery);
                _log.debug(methodName,loggerValue);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_cardGroupSetID);
            pstmtSelect.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_applicableDate));
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                latestCardGroupVersion = rs.getString("version");
            } else {
                throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.CARD_GROUP_SETVERNOT_ASSOCIATED);
            }
            return latestCardGroupVersion;
        }
            }
        }// end of try
        catch (BTSLBaseException bex) {
        	loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException ");
        	loggerValue.append(bex.getMessage());
            _log.error(methodName,loggerValue);
            throw bex;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetVersionLatestVersion]",
                "", "", "", logVal1);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSetVersionLatestVersion]",
                "", "", "", logVal1);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting latestCardGroupVersion:");
            	loggerValue.append(latestCardGroupVersion);
                _log.debug(methodName,loggerValue);
            }
        }// end of finally
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
    public int addCardGroupDetails(Connection p_con, List p_detailVOList) throws BTSLBaseException {
        
        int insertCount = 0;
        BonusBundleDAO bonusBundleDAO = null;
        StringBuilder loggerValue= new StringBuilder();
        final String methodName = "addCardGroupDetails";
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: Inserted p_detailVOList Size= ");
        	loggerValue.append(p_detailVOList.size());
            _log.debug(methodName,loggerValue);
        }

        try {
            final String insertQuery = cardGroupQry.addCardGroupDetailsQry();

            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlInsert:");
            	loggerValue.append(insertQuery);
                _log.debug(methodName,loggerValue);
            }

           try(PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);)
           {
            CardGroupDetailsVO detailVO = null;
            bonusBundleDAO = new BonusBundleDAO();
            
            for (int i = 0, j = p_detailVOList.size(); i < j; i++) {
                detailVO = (CardGroupDetailsVO) p_detailVOList.get(i);
                int k=1;
                psmtInsert.setString(k++, detailVO.getCardGroupSetID());
                psmtInsert.setString(k++, detailVO.getVersion());
                psmtInsert.setString(k++, detailVO.getCardGroupID());
                psmtInsert.setLong(k++, detailVO.getStartRange());
                psmtInsert.setLong(k++, detailVO.getEndRange());
                psmtInsert.setString(k++, detailVO.getValidityPeriodType());
                psmtInsert.setInt(k++, detailVO.getValidityPeriod());
                psmtInsert.setLong(k++, detailVO.getGracePeriod());
                psmtInsert.setString(k++, detailVO.getSenderTax1Name());
                psmtInsert.setString(k++, detailVO.getSenderTax1Type());
                psmtInsert.setDouble(k++, detailVO.getSenderTax1Rate());
                psmtInsert.setString(k++, detailVO.getSenderTax2Name());
                psmtInsert.setString(k++, detailVO.getSenderTax2Type());
                psmtInsert.setDouble(k++, detailVO.getSenderTax2Rate());
                psmtInsert.setString(k++, detailVO.getReceiverTax1Name());
                psmtInsert.setString(k++, detailVO.getReceiverTax1Type());
                psmtInsert.setDouble(k++, detailVO.getReceiverTax1Rate());
                psmtInsert.setString(k++, detailVO.getReceiverTax2Name());
                psmtInsert.setString(k++, detailVO.getReceiverTax2Type());
                psmtInsert.setDouble(k++, detailVO.getReceiverTax2Rate());
                psmtInsert.setString(k++, detailVO.getSenderAccessFeeType());
                psmtInsert.setDouble(k++, detailVO.getSenderAccessFeeRate());
                psmtInsert.setLong(k++, detailVO.getMinSenderAccessFee());
                psmtInsert.setLong(k++, detailVO.getMaxSenderAccessFee());
                psmtInsert.setString(k++, detailVO.getReceiverAccessFeeType());
                psmtInsert.setDouble(k++, detailVO.getReceiverAccessFeeRate());
                psmtInsert.setLong(k++, detailVO.getMinReceiverAccessFee());
                psmtInsert.setLong(k++, detailVO.getMaxReceiverAccessFee());
                psmtInsert.setString(k++, detailVO.getCardGroupCode());
                psmtInsert.setLong(k++, detailVO.getMultipleOf());
                psmtInsert.setLong(k++, detailVO.getBonusValidityValue());
                psmtInsert.setString(k++, detailVO.getOnline());
                psmtInsert.setString(k++, detailVO.getBoth());
                if (BTSLUtil.isNullString(detailVO.getSenderConvFactor())) {
                    detailVO.setSenderConvFactor("1");
                }
                psmtInsert.setDouble(k++, Double.parseDouble(detailVO.getSenderConvFactor()));
                psmtInsert.setDouble(k++, Double.parseDouble(detailVO.getReceiverConvFactor()));
                psmtInsert.setString(k++, detailVO.getStatus());

                psmtInsert.setString(k++, detailVO.getCosRequired());
                // added for in promo
                psmtInsert.setDouble(k++, detailVO.getInPromo());

                psmtInsert.setString(k++, detailVO.getCardName());
                psmtInsert.setString(k++, detailVO.getReversalPermitted());
                psmtInsert.setTimestamp(k++, BTSLUtil.getSQLDateTimeFromUtilDate(detailVO.getReversalModifiedDate()));
                if(PretupsI.CARD_GROUP_VMS.equals(detailVO.getCardGroupType())) {
                	psmtInsert.setString(k++, detailVO.getVoucherType());
                	psmtInsert.setString(k++, detailVO.getVoucherSegment());
                	psmtInsert.setString(k++, detailVO.getVoucherProductId());
                }
                else{
                	psmtInsert.setString(k++, "NA");
                	psmtInsert.setString(k++, "NA");
                	psmtInsert.setString(k++, "NA");
                }
                if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CGTAX34APP)).booleanValue()){
					psmtInsert.setString (k++,detailVO.getReceiverTax3Name());
					psmtInsert.setString (k++,detailVO.getReceiverTax3Type());
					psmtInsert.setDouble (k++,detailVO.getReceiverTax3Rate());
					psmtInsert.setString (k++,detailVO.getReceiverTax4Name());
					psmtInsert.setString (k++,detailVO.getReceiverTax4Type());
					psmtInsert.setDouble (k++,detailVO.getReceiverTax4Rate());
				}
                insertCount = psmtInsert.executeUpdate();

                psmtInsert.clearParameters();
                // check the status of the insert
                if (insertCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }

                bonusBundleDAO = new BonusBundleDAO();
                bonusBundleDAO.addBonusBundleDetails(p_con, detailVO);
            }

        } 
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, sqle);
            String logVal=loggerValue.toString();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[addCardGroupDetails]", "", "", "",
            		logVal);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append( "Exception: ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            String logVal=loggerValue.toString();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[addCardGroupDetails]", "", "", "",
            		logVal);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount=");
            	loggerValue.append(insertCount);
                _log.debug(methodName,loggerValue);
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
    
    public ArrayList<CardGroupSetVO> loadCardGroupSet(Connection p_con, String p_networkCode, String p_moduleCode) throws BTSLBaseException {
        final String methodName = "loadCardGroupSet";
        StringBuilder loggerValue= new StringBuilder(); 
		
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_networkCode=");
        	loggerValue.append(p_networkCode);
    		loggerValue.append(" p_moduleCode=");
        	loggerValue.append(p_moduleCode);
            _log.debug(methodName,loggerValue);
        }
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT l.lookup_name set_name,cs.set_type,cs.card_group_set_id,cs.card_group_set_name, ");
        strBuff.append(" cs.network_code, cs.created_on,cs.created_by, cs.modified_on,cs.modified_by, ");
        strBuff.append(" cs.last_version, cs.module_code, cs.status, cs.language_1_message, cs.language_2_message, ");
        strBuff.append(" cs.sub_service, stsm.selector_name, cs.service_type,cs.is_default, st.name service_name ");
        strBuff.append(" FROM card_group_set cs, service_type st,service_type_selector_mapping stsm, lookups l ");
        strBuff.append(" WHERE cs.network_code = ? AND cs.module_code = ? ANd cs.status <> 'N' ");
        strBuff.append(" AND cs.sub_service=stsm.selector_code AND cs.service_type=st.service_type ");
        strBuff.append(" AND stsm.service_type=cs.service_type AND l.lookup_type=? ");
        strBuff.append(" AND l.lookup_code=cs.set_type ORDER BY cs.card_group_set_name");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            _log.debug(methodName,loggerValue);
        }

        final ArrayList<CardGroupSetVO> list = new ArrayList<CardGroupSetVO>();
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, p_moduleCode);
            pstmtSelect.setString(3, PretupsI.CARD_GROUP_SET_TYPE);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            CardGroupSetVO cardGroupSetVO = null;
            while (rs.next()) {
                cardGroupSetVO = new CardGroupSetVO();
                cardGroupSetVO.setCardGroupSetID(SqlParameterEncoder.encodeParams(rs.getString("card_group_set_id")));
                cardGroupSetVO.setCardGroupSetName(SqlParameterEncoder.encodeParams(rs.getString("card_group_set_name")));
                cardGroupSetVO.setNetworkCode(SqlParameterEncoder.encodeParams(rs.getString("network_code")));
                cardGroupSetVO.setCreatedBy(SqlParameterEncoder.encodeParams(rs.getString("created_by")));
                cardGroupSetVO.setModifiedBy(SqlParameterEncoder.encodeParams(rs.getString("modified_by")));
                cardGroupSetVO.setCreatedOn(rs.getDate("created_on"));
                cardGroupSetVO.setModifiedOn(rs.getDate("modified_on"));
                cardGroupSetVO.setLastModifiedOn((rs.getTimestamp("modified_on").getTime()));
                cardGroupSetVO.setLastVersion(SqlParameterEncoder.encodeParams(rs.getString("last_version")));
                cardGroupSetVO.setModuleCode(SqlParameterEncoder.encodeParams(rs.getString("module_code")));
                cardGroupSetVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("status")));
                cardGroupSetVO.setLanguage1Message(SqlParameterEncoder.encodeParams(rs.getString("language_1_message")));
                cardGroupSetVO.setLanguage2Message(SqlParameterEncoder.encodeParams(rs.getString("language_2_message")));
                cardGroupSetVO.setSubServiceType(SqlParameterEncoder.encodeParams(rs.getString("sub_service")));
                cardGroupSetVO.setSubServiceTypeDescription(SqlParameterEncoder.encodeParams(rs.getString("selector_name")));
                cardGroupSetVO.setServiceType(SqlParameterEncoder.encodeParams(rs.getString("service_type")));
                cardGroupSetVO.setServiceTypeDesc(SqlParameterEncoder.encodeParams(rs.getString("service_name")));
                cardGroupSetVO.setSetType(SqlParameterEncoder.encodeParams(rs.getString("set_type")));
                cardGroupSetVO.setSetTypeName(SqlParameterEncoder.encodeParams(rs.getString("set_name")));
                cardGroupSetVO.setDefaultCardGroup(SqlParameterEncoder.encodeParams(rs.getString("is_default")));
                list.add(cardGroupSetVO);
            }

        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
    		String logVal1=loggerValue.toString();
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSet]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, "loadCardGroupSet", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
    		String logVal1=loggerValue.toString();
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSet]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, "loadCardGroupSet", "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: cardGroupSet size=");
            	loggerValue.append(list.size());
                _log.debug("loadCardGroupSet",loggerValue);
            }
        }
        return list;
    }
    
    /**
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_moduleCode
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList<CardGroupSetVO> loadCardGroupSetWithDate(Connection p_con, String p_networkCode, String p_moduleCode) throws BTSLBaseException {
        final String methodName = "loadCardGroupSetWithDate";
        StringBuilder loggerValue= new StringBuilder(); 
		
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_networkCode=");
        	loggerValue.append(p_networkCode);
    		loggerValue.append(" p_moduleCode=");
        	loggerValue.append(p_moduleCode);
            _log.debug(methodName,loggerValue);
        }
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT l.lookup_name set_name,cs.set_type,cs.card_group_set_id,cs.card_group_set_name, ");
        strBuff.append(" cs.network_code, cs.created_on,cs.created_by, cs.modified_on,cs.modified_by, ");
        strBuff.append(" cs.last_version, cs.module_code, cs.status, cs.language_1_message, cs.language_2_message, ");
        strBuff.append(" cs.sub_service, stsm.selector_name, cs.service_type,cs.is_default, st.name service_name ");
        strBuff.append(" FROM card_group_set cs, service_type st,service_type_selector_mapping stsm, lookups l ");
        strBuff.append(" WHERE cs.network_code = ? AND cs.module_code = ? ANd cs.status <> 'N' ");
        strBuff.append(" AND cs.sub_service=stsm.selector_code AND cs.service_type=st.service_type ");
        strBuff.append(" AND stsm.service_type=cs.service_type AND l.lookup_type=? ");
        strBuff.append(" AND l.lookup_code=cs.set_type ORDER BY cs.card_group_set_name");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            _log.debug(methodName,loggerValue);
        }

        final ArrayList<CardGroupSetVO> list = new ArrayList<CardGroupSetVO>();
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, p_moduleCode);
            pstmtSelect.setString(3, PretupsI.CARD_GROUP_SET_TYPE);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            CardGroupSetVO cardGroupSetVO = null;
            while (rs.next()) {
                cardGroupSetVO = new CardGroupSetVO();
                cardGroupSetVO.setCardGroupSetID(SqlParameterEncoder.encodeParams(rs.getString("card_group_set_id")));
                cardGroupSetVO.setCardGroupSetName(SqlParameterEncoder.encodeParams(rs.getString("card_group_set_name")));
                cardGroupSetVO.setNetworkCode(SqlParameterEncoder.encodeParams(rs.getString("network_code")));
                cardGroupSetVO.setCreatedBy(SqlParameterEncoder.encodeParams(rs.getString("created_by")));
                cardGroupSetVO.setModifiedBy(SqlParameterEncoder.encodeParams(rs.getString("modified_by")));
                
                cardGroupSetVO.setCreatedOn(rs.getDate("created_on"));
                cardGroupSetVO.setModifiedOn(rs.getDate("modified_on"));
                cardGroupSetVO.setCreatedOnStr(rs.getDate("created_on").toString());
                cardGroupSetVO.setModifiedOnStr(rs.getDate("modified_on").toString());
                
                cardGroupSetVO.setLastModifiedOn((rs.getTimestamp("modified_on").getTime()));
                cardGroupSetVO.setLastVersion(SqlParameterEncoder.encodeParams(rs.getString("last_version")));
                cardGroupSetVO.setModuleCode(SqlParameterEncoder.encodeParams(rs.getString("module_code")));
                cardGroupSetVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("status")));
                cardGroupSetVO.setLanguage1Message(SqlParameterEncoder.encodeParams(rs.getString("language_1_message")));
                cardGroupSetVO.setLanguage2Message(SqlParameterEncoder.encodeParams(rs.getString("language_2_message")));
                cardGroupSetVO.setSubServiceType(SqlParameterEncoder.encodeParams(rs.getString("sub_service")));
                cardGroupSetVO.setSubServiceTypeDescription(SqlParameterEncoder.encodeParams(rs.getString("selector_name")));
                cardGroupSetVO.setServiceType(SqlParameterEncoder.encodeParams(rs.getString("service_type")));
                cardGroupSetVO.setServiceTypeDesc(SqlParameterEncoder.encodeParams(rs.getString("service_name")));
                cardGroupSetVO.setSetType(SqlParameterEncoder.encodeParams(rs.getString("set_type")));
                cardGroupSetVO.setSetTypeName(SqlParameterEncoder.encodeParams(rs.getString("set_name")));
                cardGroupSetVO.setDefaultCardGroup(SqlParameterEncoder.encodeParams(rs.getString("is_default")));
                list.add(cardGroupSetVO);
            }

        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
    		String logVal1=loggerValue.toString();
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSet]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
    		String logVal1=loggerValue.toString();
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSet]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: cardGroupSet size=");
            	loggerValue.append(list.size());
                _log.debug(methodName,loggerValue);
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
    public ArrayList<CardGroupDetailsVO> loadCardGroupDetailsListByID(Connection p_con, String p_cardGroupSetID, String p_version) throws BTSLBaseException {
        final String methodName = "loadCardGroupDetailsListByID";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_cardGroupSetID=");
        	loggerValue.append(p_cardGroupSetID);
			loggerValue.append(" p_version=");
        	loggerValue.append(p_version);
            _log.debug(methodName, loggerValue);
        }
         
        final ArrayList<CardGroupDetailsVO> cardGroupDetailList = new ArrayList<CardGroupDetailsVO>();
        CardGroupDetailsVO cardGroupDetailsVO = null;
        // Added to load the bonus accounts
        BonusBundleDAO bonusBundleDAO = null;
        try {

            final StringBuilder qry = new StringBuilder(" SELECT l2.lookup_name set_name, cs.set_type, cd.card_group_set_id, ");
            qry.append(" cd.card_group_id, cd.card_group_code, cd.start_range,cd.end_range,cd.validity_period_type, ");
            qry.append(" cd.validity_period,cd.grace_period, cd.sender_tax1_name, cd.sender_tax1_type, ");
            qry.append(" cd.sender_tax1_rate, cd.sender_tax2_name, cd.sender_tax2_type, cd.sender_tax2_rate, ");
            qry.append(" cd.receiver_tax1_name, cd.receiver_tax1_type, cd.receiver_tax1_rate, cd.receiver_tax2_name, ");
            qry.append(" cd.receiver_tax2_type, cd.receiver_tax2_rate,cd.version,");
            qry.append(" cd.sender_access_fee_type, cd.sender_access_fee_rate, ");
            qry.append(" cd.min_sender_access_fee, cd.max_sender_access_fee,  cd.receiver_access_fee_type, ");
            qry.append(" cd.receiver_access_fee_rate, cd.min_receiver_access_fee, cd.max_receiver_access_fee, ");
            qry.append(" cd.multiple_of, cs.service_type, st.name service_name, cs.card_group_set_name, cs.sub_service, ");
            qry.append(" stsm.selector_name, cd.status, cd.bonus_validity_value, cd.online_offline,cd.both, ");
            qry.append(" cd.sender_mult_factor, cd.receiver_mult_factor, cd.cos_required , cd.in_promo, cd.card_name, ");
            qry.append(" cd.reversal_permitted, cd.reversal_modified_date,cd.voucher_type,cd.voucher_segment,cd.voucher_product_id ");
            if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CGTAX34APP)).booleanValue()){
				 qry.append(" , cd.receiver_tax3_name, cd.receiver_tax3_type, cd.receiver_tax3_rate,cd.receiver_tax4_name, cd.receiver_tax4_type, cd.receiver_tax4_rate ");
			}
            qry.append(" FROM card_group_details cd,card_group_set cs,service_type st, ");
            qry.append(" lookups l2, service_type_selector_mapping stsm ");
            qry.append(" WHERE cs.status<>'N' AND st.status<>'N' AND stsm.status<>'N' AND cd.card_group_set_id=cs.card_group_set_id ");
            qry.append(" AND cd.card_group_set_id=? AND cd.version = ? AND cs.service_type=st.service_type ");
            qry.append(" AND l2.lookup_type=? AND l2.lookup_code=cs.set_type AND stsm.service_type=cs.service_type ");
            qry.append(" AND stsm.selector_code=cs.sub_service");
            final String selectQuery = qry.toString();
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("select query:");
            	loggerValue.append(selectQuery);
                _log.debug(methodName,loggerValue);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_cardGroupSetID);
            pstmtSelect.setString(2, p_version);
            pstmtSelect.setString(3, PretupsI.CARD_GROUP_SET_TYPE);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {

            bonusBundleDAO = new BonusBundleDAO();
            while (rs.next()) {
                cardGroupDetailsVO = new CardGroupDetailsVO();
                cardGroupDetailsVO.setCardGroupSetID(SqlParameterEncoder.encodeParams(rs.getString("card_group_set_id")));
                cardGroupDetailsVO.setCardGroupID(SqlParameterEncoder.encodeParams(rs.getString("card_group_id")));
                cardGroupDetailsVO.setCardGroupCode(SqlParameterEncoder.encodeParams(rs.getString("card_group_code")));
                cardGroupDetailsVO.setVersion(SqlParameterEncoder.encodeParams(rs.getString("version")));
                cardGroupDetailsVO.setStartRange(rs.getLong("start_range"));
                cardGroupDetailsVO.setEndRange(rs.getLong("end_range"));
                cardGroupDetailsVO.setValidityPeriodType(SqlParameterEncoder.encodeParams(rs.getString("validity_period_type")));
                cardGroupDetailsVO.setValidityPeriod(rs.getInt("validity_period"));
                cardGroupDetailsVO.setGracePeriod(rs.getLong("grace_period"));
                cardGroupDetailsVO.setSenderTax1Name(SqlParameterEncoder.encodeParams(rs.getString("sender_tax1_name")));
                cardGroupDetailsVO.setSenderTax1Type(SqlParameterEncoder.encodeParams(rs.getString("sender_tax1_type")));
                cardGroupDetailsVO.setSenderTax1Rate(rs.getDouble("sender_tax1_rate"));
                cardGroupDetailsVO.setSenderTax2Name(SqlParameterEncoder.encodeParams(rs.getString("sender_tax2_name")));
                cardGroupDetailsVO.setSenderTax2Type(SqlParameterEncoder.encodeParams(rs.getString("sender_tax2_type")));
                cardGroupDetailsVO.setSenderTax2Rate(rs.getDouble("sender_tax2_rate"));
                cardGroupDetailsVO.setReceiverTax1Name(SqlParameterEncoder.encodeParams(rs.getString("receiver_tax1_name")));
                cardGroupDetailsVO.setReceiverTax1Type(SqlParameterEncoder.encodeParams(rs.getString("receiver_tax1_type")));
                cardGroupDetailsVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                cardGroupDetailsVO.setReceiverTax2Name(SqlParameterEncoder.encodeParams(rs.getString("receiver_tax2_name")));
                cardGroupDetailsVO.setReceiverTax2Type(SqlParameterEncoder.encodeParams(rs.getString("receiver_tax2_type")));
                cardGroupDetailsVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                cardGroupDetailsVO.setSenderAccessFeeType(SqlParameterEncoder.encodeParams(rs.getString("sender_access_fee_type")));
                cardGroupDetailsVO.setSenderAccessFeeRate(rs.getDouble("sender_access_fee_rate"));
                cardGroupDetailsVO.setMinSenderAccessFee(rs.getLong("min_sender_access_fee"));
                cardGroupDetailsVO.setMaxSenderAccessFee(rs.getLong("max_sender_access_fee"));
                cardGroupDetailsVO.setReceiverAccessFeeType(SqlParameterEncoder.encodeParams(rs.getString("receiver_access_fee_type")));
                cardGroupDetailsVO.setReceiverAccessFeeRate(rs.getDouble("receiver_access_fee_rate"));
                cardGroupDetailsVO.setMinReceiverAccessFee(rs.getLong("min_receiver_access_fee"));
                cardGroupDetailsVO.setMaxReceiverAccessFee(rs.getLong("max_receiver_access_fee"));
                cardGroupDetailsVO.setMultipleOf(rs.getLong("multiple_of"));
                cardGroupDetailsVO.setCardGroupSetName(SqlParameterEncoder.encodeParams(rs.getString("card_group_set_name")));
                cardGroupDetailsVO.setCardGroupSubServiceId(SqlParameterEncoder.encodeParams(rs.getString("sub_service")));
                cardGroupDetailsVO.setServiceTypeId(SqlParameterEncoder.encodeParams(rs.getString("service_type")));
                cardGroupDetailsVO.setServiceTypeDesc(SqlParameterEncoder.encodeParams(rs.getString("service_name")));
                cardGroupDetailsVO.setSetType(SqlParameterEncoder.encodeParams(rs.getString("set_type")));
                cardGroupDetailsVO.setSetTypeName(SqlParameterEncoder.encodeParams(rs.getString("set_name")));
                cardGroupDetailsVO.setCardGroupSubServiceIdDesc(rs.getString("selector_name"));
                cardGroupDetailsVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("status")));// added
                // for slab
                // suspend/resume
                // added by vikas kumar for card group updation
                cardGroupDetailsVO.setBonusValidityValue(rs.getLong("bonus_validity_value"));
                cardGroupDetailsVO.setOnline(SqlParameterEncoder.encodeParams(rs.getString("online_offline")));
                cardGroupDetailsVO.setBoth(SqlParameterEncoder.encodeParams(rs.getString("both")));
                cardGroupDetailsVO.setSenderConvFactor(SqlParameterEncoder.encodeParams(rs.getString("sender_mult_factor")));
                cardGroupDetailsVO.setReceiverConvFactor(SqlParameterEncoder.encodeParams(rs.getString("receiver_mult_factor")));
                // added by gaurav for cos required
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
                    cardGroupDetailsVO.setCosRequired(SqlParameterEncoder.encodeParams(rs.getString("cos_required")));
                }

                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED)).booleanValue()) {
                    cardGroupDetailsVO.setInPromo(rs.getDouble("in_promo"));
                }
                // Now load the bonuses defined for the card group.
                cardGroupDetailsVO.setBonusAccList(bonusBundleDAO.loadBonusAccDetailsListByID(p_con, p_cardGroupSetID, p_version, cardGroupDetailsVO.getCardGroupID()));

                cardGroupDetailsVO.setCardName(SqlParameterEncoder.encodeParams(rs.getString("card_name")));
                cardGroupDetailsVO.setReversalPermitted(SqlParameterEncoder.encodeParams(rs.getString("reversal_permitted")));
                cardGroupDetailsVO.setReversalModifiedDate(rs.getTimestamp("reversal_modified_date"));
                cardGroupDetailsVO.setVoucherType(SqlParameterEncoder.encodeParams(rs.getString("voucher_type")));
                cardGroupDetailsVO.setVoucherSegment(SqlParameterEncoder.encodeParams(rs.getString("voucher_segment")));
                cardGroupDetailsVO.setVoucherProductId(SqlParameterEncoder.encodeParams(rs.getString("voucher_product_id")));
                if(!(cardGroupDetailsVO.getReversalModifiedDate()==null)){
                final DateFormat dateFormat = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
                cardGroupDetailsVO.setReversalModifiedDateAsString(SqlParameterEncoder.encodeParams(BTSLDateUtil.getLocaleTimeStamp(dateFormat.format(rs.getTimestamp("reversal_modified_date")))));
                if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CGTAX34APP)).booleanValue()){
					if(!BTSLUtil.isNullString(SqlParameterEncoder.encodeParams(rs.getString("receiver_tax3_name"))))
				cardGroupDetailsVO.setReceiverTax3Name(SqlParameterEncoder.encodeParams(rs.getString("receiver_tax3_name")));
					else
						cardGroupDetailsVO.setReceiverTax3Name(BTSLUtil.NullToString(SqlParameterEncoder.encodeParams(rs.getString("receiver_tax3_name"))));
					if(!BTSLUtil.isNullString(SqlParameterEncoder.encodeParams(rs.getString("receiver_tax3_type"))))
				cardGroupDetailsVO.setReceiverTax3Type(SqlParameterEncoder.encodeParams(rs.getString("receiver_tax3_type")));
					else
						cardGroupDetailsVO.setReceiverTax3Type(BTSLUtil.NullToString(SqlParameterEncoder.encodeParams(rs.getString("receiver_tax3_type"))));
					if(!BTSLUtil.isNullString(SqlParameterEncoder.encodeParams(rs.getString("receiver_tax4_name"))))
				cardGroupDetailsVO.setReceiverTax4Name(SqlParameterEncoder.encodeParams(rs.getString("receiver_tax4_name")));
					else
						cardGroupDetailsVO.setReceiverTax4Name(BTSLUtil.NullToString(SqlParameterEncoder.encodeParams(rs.getString("receiver_tax4_name"))));
					if(!BTSLUtil.isNullString(SqlParameterEncoder.encodeParams(rs.getString("receiver_tax4_type"))))
				cardGroupDetailsVO.setReceiverTax4Type(SqlParameterEncoder.encodeParams(rs.getString("receiver_tax4_type")));
					else
						cardGroupDetailsVO.setReceiverTax4Type(BTSLUtil.NullToString(SqlParameterEncoder.encodeParams(rs.getString("receiver_tax4_type"))));
					cardGroupDetailsVO.setReceiverTax3Rate(rs.getDouble("receiver_tax3_rate"));					
					cardGroupDetailsVO.setReceiverTax4Rate(rs.getDouble("receiver_tax4_rate"));
				}
                }
                cardGroupDetailList.add(cardGroupDetailsVO);
            }
            return cardGroupDetailList;
        }
            }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, sqle);
            String logVal1=loggerValue.toString();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupDetailsListByID]", "", "",
                "", logVal1);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            String logVal1=loggerValue.toString();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupDetailsListByID]", "", "",
                "", logVal1);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting CardGroupDetailList Size:");
            	loggerValue.append( cardGroupDetailList.size());
                _log.debug(methodName,loggerValue);
            }
        }// end of finally
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
        final String methodName = "loadServiceTypeList";
        StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered p_networkCode=");
    	loggerValue.append(p_networkCode);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,loggerValue);
        }
         
        final ArrayList serviceTypeList = new ArrayList();
        try {
            final StringBuilder selectQueryBuff = new StringBuilder("SELECT DISTINCT ST.service_type,ST.name FROM service_type ST,network_services NS");
            selectQueryBuff.append(" WHERE ST.service_type=NS.service_type AND NS.status<>'N' AND ST.status<>'N' AND NS.sender_network=? AND ST.module=? AND ST.external_interface='Y' ORDER BY ST.name");
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query selectQueryBuff:");
            	loggerValue.append(selectQueryBuff.toString());
                _log.debug(methodName,loggerValue);
            }
           try( PreparedStatement pstmtSelect = p_con.prepareStatement(selectQueryBuff.toString());)
           {
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, p_module);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                serviceTypeList.add(new ListValueVO(SqlParameterEncoder.encodeParams(rs.getString("name")),
                		SqlParameterEncoder.encodeParams(rs.getString("service_type"))));
            }
        }
           }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
    		String logVal1=loggerValue.toString();
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadServiceTypeList]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
    		String logVal1=loggerValue.toString();
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadServiceTypeList]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting serviceTypeList.size:");
            	loggerValue.append(serviceTypeList.size());
                _log.debug(methodName,loggerValue);
            }
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
        final String methodName = "loadCardGroupSlab";
        StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered p_cardGroupSetID=");
    	loggerValue.append(p_cardGroupSetID);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,loggerValue);
        }
       
        ArrayList cardGroupDetailsVOList = null;
        CardGroupDetailsVO cardGroupDetailsVO = null;
        String latestCardGroupVersion = null;
        try {
            latestCardGroupVersion = loadCardGroupSetVersionLatestVersion(p_con, p_cardGroupSetID, p_applicableDate);
            if (!BTSLUtil.isNullString(latestCardGroupVersion)) {
                final StringBuilder qry = new StringBuilder(" SELECT card_group_set_id,version,card_group_id,start_range, ");
				qry.append(" end_range,card_group_code, card_name,reversal_permitted, reversal_modified_date FROM  card_group_details");
                qry.append(" WHERE card_group_set_id=? AND  version=? ");
                final String selectQuery = qry.toString();
                if (_log.isDebugEnabled()) {
            		loggerValue.setLength(0);
                	loggerValue.append("select query:");
                	loggerValue.append(selectQuery);
                    _log.debug(methodName,loggerValue);
                }
               try(PreparedStatement  pstmtSelect = p_con.prepareStatement(selectQuery);)
               {
                pstmtSelect.setString(1, p_cardGroupSetID);
                pstmtSelect.setString(2, latestCardGroupVersion);
                try(ResultSet rs = pstmtSelect.executeQuery();)
                {
                cardGroupDetailsVOList = new ArrayList();
                while (rs.next()) {
                    cardGroupDetailsVO = new CardGroupDetailsVO();
                    cardGroupDetailsVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                    cardGroupDetailsVO.setCardGroupID(rs.getString("card_group_id"));
                    cardGroupDetailsVO.setCardGroupCode(rs.getString("card_group_code"));
                    cardGroupDetailsVO.setVersion(latestCardGroupVersion);
                    cardGroupDetailsVO.setStartRange(rs.getLong("start_range"));
                    cardGroupDetailsVO.setEndRange(rs.getLong("end_range"));
    				cardGroupDetailsVO.setCardName(rs.getString("card_name"));
					cardGroupDetailsVO.setReversalPermitted(rs.getString("reversal_permitted"));
					cardGroupDetailsVO.setReversalModifiedDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("reversal_modified_date")));
                    cardGroupDetailsVOList.add(cardGroupDetailsVO);
                }
            } 
            }
            }else {
                throw new BTSLBaseException("CardGroupDAO", "loadCardGroupDetails", PretupsErrorCodesI.CARD_GROUP_SLAB_NOT_FOUND);
            }

        }// end of try
        catch (BTSLBaseException bex) {
    		loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException ");
        	loggerValue.append(bex.getMessage());
            _log.error(methodName,loggerValue);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CardGroupDAO[loadCardGroupSlab]", "", "", "",
                "Base Exception:" + bex.getMessage());
            throw bex;
        } catch (SQLException sqle) {
    		loggerValue.setLength(0);
        	loggerValue.append("SQL Exception ");
        	loggerValue.append(sqle.getMessage());
    		String logVal1=loggerValue.toString();
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSlab]", "", "", "",
            		logVal1);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
    		String logVal1=loggerValue.toString();
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSlab]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Exiting cardGroupDetailsVOList size:");
            	loggerValue.append(cardGroupDetailsVOList.size());
                _log.debug(methodName,loggerValue);
            }
        }// end of finally
        return cardGroupDetailsVOList;
    }

    public int suspendResumeCardGroupDetail(Connection p_con, CardGroupDetailsVO p_cardGroupDetailsVO) throws BTSLBaseException {
        
        int updateCount = 0;
        StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered: p_cardGroupDetailsVO=");
    	loggerValue.append(p_cardGroupDetailsVO);
        final String methodName = "suspendResumeCardGroupDetail";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,loggerValue);
        }

        try {
            final StringBuilder strBuff = new StringBuilder();

            strBuff.append("UPDATE card_group_details SET status = ? ");
            strBuff.append("WHERE card_group_set_id = ? and card_group_id = ? and version = ?");

            final String updateQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlInsert:");
            	loggerValue.append(updateQuery);
                _log.debug(methodName,loggerValue);
            }

            try(PreparedStatement psmtUpdate = p_con.prepareStatement(updateQuery);)
            {

            psmtUpdate.setString(1, p_cardGroupDetailsVO.getStatus());
            psmtUpdate.setString(2, p_cardGroupDetailsVO.getCardGroupSetID());
            psmtUpdate.setString(3, p_cardGroupDetailsVO.getCardGroupID());
            psmtUpdate.setString(4, p_cardGroupDetailsVO.getVersion());

            updateCount = psmtUpdate.executeUpdate();

        }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException: ");
        	loggerValue.append(sqle.getMessage());
    		String logVal1=loggerValue.toString();
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[updateCardGroupSetVersion]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateCardGroupSetVersion", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
    		String logVal1=loggerValue.toString();
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[suspendResumeCardGroupDetail]", "", "",
                "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount=");
            	loggerValue.append(updateCount);
                _log.debug(methodName,loggerValue);
            }
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
    public ArrayList<String> loadDefaultCardGroup(Connection p_con, String p_serviceTypeID, String p_cardGroupSubServiceID, String p_defaultCardGroupRequired,String ntwrkcode) throws BTSLBaseException {
        final String methodName = "loadDefaultCardGroup";
        StringBuilder loggerValue= new StringBuilder(); 
		loggerValue.setLength(0);
    	loggerValue.append("Entered p_serviceTypeID=");
    	loggerValue.append(p_serviceTypeID);
		loggerValue.append(" p_cardGroupSubServiceID=");
    	loggerValue.append(p_cardGroupSubServiceID);
    	loggerValue.append(" p_defaultCardGroupRequired=");
    	loggerValue.append(p_defaultCardGroupRequired);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,loggerValue);
        }
        
        ArrayList<String> defaultCardGroup = new ArrayList<>();
        try {
            final StringBuilder selectQueryBuff = new StringBuilder(" SELECT card_group_set_name, card_group_set_id   ");
            selectQueryBuff.append(" FROM card_group_set ");
            selectQueryBuff.append(" WHERE SERVICE_TYPE=? AND sub_service=? and network_code=?");
            selectQueryBuff.append(" AND is_default=? AND status <>'N'");
            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("select query:");
            	loggerValue.append(selectQuery);
                _log.debug(methodName, loggerValue);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_serviceTypeID);
            pstmtSelect.setString(2, p_cardGroupSubServiceID);
            pstmtSelect.setString(3, ntwrkcode);
            pstmtSelect.setString(4, p_defaultCardGroupRequired);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                defaultCardGroup.add(rs.getString("card_group_set_id"));
                defaultCardGroup.add(rs.getString("card_group_set_name"));
            }
            return defaultCardGroup;
        }
            }
        }// end of try

        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception ");
        	loggerValue.append(sqle.getMessage());
    		String logVal1=loggerValue.toString();
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadDefaultCardGroup]", "", "", "",
            		logVal1);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
    		String logVal1=loggerValue.toString();
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadDefaultCardGroup]", "", "", "",
            		logVal1);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting: defaultCardGroup=");
        	loggerValue.append(defaultCardGroup);
           _log.debug(methodName,loggerValue);
        }// end of finally
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
    public ConcurrentMap<String, ArrayList<CardGroupDetailsVO>> loadCardGroupCache() throws BTSLBaseException {
        final String methodName = "loadCardGroupCache";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        StringBuilder loggerValue= new StringBuilder(); 
       
        Connection con = null;
        final ConcurrentMap<String, ArrayList<CardGroupDetailsVO>> cardGroupMap = new ConcurrentHashMap<String, ArrayList<CardGroupDetailsVO>>();
         
        
        CardGroupDetailsVO cardGroupDetailsVO = null;
        String key = null;
        String oldKey = null;
        ArrayList<CardGroupDetailsVO> cardGroupList = null;
        try {
        	StringBuilder qry = cardGroupQry.loadCardGroupCacheQry();
            final String selectQuery = qry.toString();
            if (_log.isDebugEnabled()) {
            	 loggerValue.setLength(0);
             	loggerValue.append("select query:");
             	loggerValue.append(selectQuery);
                _log.debug(methodName,loggerValue);
            }

            con = OracleUtil.getSingleConnection();

            StringBuilder qry1 = new StringBuilder();
            qry1.append(" select cad.card_group_set_id,cad.version,cad.card_group_id,cad.bundle_id,cad.type,cad.validity,");
            qry1.append(" cad.value,cad.mult_factor,bbm.bundle_name,bbm.bundle_type,bbm.res_in_status,bbm.bundle_code");
            qry1.append(" from card_group_sub_bon_acc_details cad, bonus_bundle_master bbm");
            qry1.append(" where cad.bundle_id=bbm.bundle_id");
            qry1.append(" and card_group_set_id=? and version=? and card_group_id=? order by bbm.bundle_id ");
            final String selectQuery1 = qry1.toString();
            if (_log.isDebugEnabled()) {
            	 loggerValue.setLength(0);
             	loggerValue.append("select selectQuery:");
             	loggerValue.append(selectQuery1);
                _log.debug(methodName,loggerValue);
            }
            try(PreparedStatement pstmtSelect1 = con.prepareStatement(selectQuery1);PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);)
            {
           
            pstmtSelect.setString(1, PretupsI.CARD_GROUP_SET_TYPE);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            final BonusBundleDAO bonusBundleDAO = new BonusBundleDAO();
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
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
                    cardGroupDetailsVO.setCosRequired(rs.getString("cos_required"));
                    if (BTSLUtil.isNullString(cardGroupDetailsVO.getCosRequired()) || "N".equalsIgnoreCase(cardGroupDetailsVO.getCosRequired())) {
                        cardGroupDetailsVO.setCosRequired(PretupsI.NO);
                    } else {
                        cardGroupDetailsVO.setCosRequired(PretupsI.YES);
                    }
                }
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED)).booleanValue()) {
                    cardGroupDetailsVO.setInPromo(rs.getDouble("in_promo"));
                }
                cardGroupDetailsVO.setCardName(rs.getString("card_name"));
                cardGroupDetailsVO.setReversalPermitted(rs.getString("reversal_permitted"));
                cardGroupDetailsVO.setReversalModifiedDate(rs.getDate("reversal_modified_date"));
                if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CGTAX34APP)).booleanValue()){
                	if(!BTSLUtil.isNullString(rs.getString("receiver_tax3_name")))
						cardGroupDetailsVO.setReceiverTax3Name(rs.getString("receiver_tax3_name"));
							else
								cardGroupDetailsVO.setReceiverTax3Name(BTSLUtil.NullToString(rs.getString("receiver_tax3_name")));
							if(!BTSLUtil.isNullString(rs.getString("receiver_tax3_type")))
								cardGroupDetailsVO.setReceiverTax3Type(rs.getString("receiver_tax3_type"));
							else
								cardGroupDetailsVO.setReceiverTax3Type(BTSLUtil.NullToString(rs.getString("receiver_tax3_type")));
							if(!BTSLUtil.isNullString(rs.getString("receiver_tax4_name")))
								cardGroupDetailsVO.setReceiverTax4Name(rs.getString("receiver_tax4_name"));
							else
								cardGroupDetailsVO.setReceiverTax4Name(BTSLUtil.NullToString(rs.getString("receiver_tax4_name")));
							if(!BTSLUtil.isNullString(rs.getString("receiver_tax4_type")))
								cardGroupDetailsVO.setReceiverTax4Type(rs.getString("receiver_tax4_type"));
							else
								cardGroupDetailsVO.setReceiverTax4Type(BTSLUtil.NullToString(rs.getString("receiver_tax4_type")));
							cardGroupDetailsVO.setReceiverTax3Rate(rs.getDouble("receiver_tax3_rate"));					
							cardGroupDetailsVO.setReceiverTax4Rate(rs.getDouble("receiver_tax4_rate"));
					}
                cardGroupDetailsVO.setVoucherType(rs.getString("voucher_type"));
                cardGroupDetailsVO.setVoucherSegment(rs.getString("voucher_segment"));
                cardGroupDetailsVO.setVoucherProductId(rs.getString("voucher_product_id"));
                cardGroupDetailsVO.setBonusAccList(bonusBundleDAO.loadBonusAccDetails(con, pstmtSelect1, cardGroupDetailsVO));
                key = cardGroupDetailsVO.getCardGroupSetID() + "_" + cardGroupDetailsVO.getVersion();
                if (oldKey == null) {
                    cardGroupList = new ArrayList<>();
                } else if (!oldKey.equals(key)) {
                    cardGroupMap.put(oldKey, cardGroupList);
                    cardGroupList = new ArrayList<>();
                }
                cardGroupList.add(cardGroupDetailsVO);
                oldKey = key;
            }
            // Added for bug removal incase of single card group
            if (oldKey != null && oldKey.equals(key)) {
                cardGroupMap.put(oldKey, cardGroupList);
            }
            
            if(cardGroupMap!=null ) {
        		_log.info(methodName, "With out completing this loadCardGroupCache  process, we should not execute Bulk EVD/DVD process");
        		_log.info(methodName, "Exiting , to check loadCardGroupCache process is completed" + cardGroupMap.size());
        	}
            return cardGroupMap;
        }
        }
        }// end of try
        catch (BTSLBaseException bex) {
        	 loggerValue.setLength(0);
         	loggerValue.append("BTSLBaseException ");
         	loggerValue.append(bex.getMessage());
            _log.error(methodName,loggerValue);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CardGroupDAO[loadCardGroupCache]", "", "", "",
                "Base Exception:" + bex.getMessage());
            throw bex;
        } catch (SQLException sqle) {
        	 loggerValue.setLength(0);
          	loggerValue.append("SQL Exception ");
          	loggerValue.append(sqle.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, sqle);
            String logVal=loggerValue.toString();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupCache]", "", "", "",
            		logVal);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
        	 loggerValue.setLength(0);
           	loggerValue.append("Exception ");
           	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            String logVal=loggerValue.toString();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupCache]", "", "", "",
            		logVal);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	
        	OracleUtil.closeQuietly(con);
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
               	loggerValue.append("Exiting cardGroupMap.size()=:");
               	loggerValue.append(cardGroupMap.size());
                _log.debug(methodName,loggerValue);
            }
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
        final String methodName = "loadCardGroupMinMax";
        StringBuilder loggerValue= new StringBuilder(); 
        loggerValue.setLength(0);
    	loggerValue.append("Entered p_cardGroupSetID=");
    	loggerValue.append(p_cardGroupSetID);
		String logVal1=loggerValue.toString();
        LogFactory.printLog(methodName,logVal1, _log);
        
        CardGroupDetailsVO cardGroupDetailsVO = null;
        String latestCardGroupVersion = null;
        try {
            latestCardGroupVersion = loadCardGroupSetVersionLatestVersion(p_con, p_cardGroupSetID, p_applicableDate);
            if (!BTSLUtil.isNullString(latestCardGroupVersion)) {
                final StringBuilder qry = new StringBuilder(" SELECT min(start_range) min,max(end_range) max");
                qry.append(" FROM card_group_details ");
                qry.append(" WHERE card_group_set_id=? AND  version=? ");
                final String selectQuery = qry.toString();
                loggerValue.setLength(0);
            	loggerValue.append("select query:");
            	loggerValue.append(selectQuery);
        		String logVal=loggerValue.toString();
                LogFactory.printLog(methodName,logVal, _log);
                try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
                {
                pstmtSelect.setString(1, p_cardGroupSetID);
                pstmtSelect.setString(2, latestCardGroupVersion);
                try(ResultSet rs = pstmtSelect.executeQuery();)
                {

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
            } 
                }
            }else {
                throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.CARD_GROUP_SLAB_NOT_FOUND);
            }

        }// end of try
        catch (BTSLBaseException bex) {
        	loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException ");
        	loggerValue.append(bex.getMessage());
            _log.error(methodName,loggerValue);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CardGroupDAO[loadCardGroupSlab]", "", "", "",
                "Base Exception:" + bex.getMessage());
            throw bex;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception ");
        	loggerValue.append(sqle.getMessage());
    		String logVal2=loggerValue.toString();
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSlab]", "", "", "",
            		logVal2);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
    		String logVal3=loggerValue.toString();
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardGroupSlab]", "", "", "",
            		logVal3);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting CardGroupDetailsVO:");
        	loggerValue.append(cardGroupDetailsVO);
    		String logVal4=loggerValue.toString();
            LogFactory.printLog(methodName, logVal4 , _log);
        }// end of finally
        return cardGroupDetailsVO;
    }

    /**
     * @param p_con
     * @param p_cardGroupSetID
     * @param p_cardGroupId
     * @param p_reversalPermitted
     * @return
     * @throws BTSLBaseException
     */
    public boolean checkForReversalPermittedChange(Connection p_con, String p_cardGroupSetID, String p_cardGroupId, String p_reversalPermitted) throws BTSLBaseException {
        final String methodName = "checkForReversalPermittedChange";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered p_cardGroupSetID=");
        	loggerValue.append(p_cardGroupSetID);
    		loggerValue.append("p_cardGroupId=");
        	loggerValue.append(p_cardGroupId);
            _log.debug(methodName,loggerValue);
        }
         
        boolean result = true;
        String prevReversalPermitted = null;
        try {
            final StringBuilder qry = new StringBuilder(" SELECT cd.reversal_permitted");
            qry.append(" FROM card_group_details cd ");
            qry.append(" WHERE cd.card_group_set_id=? AND  cd.card_group_id=?");
            final String selectQuery = qry.toString();
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("select query:");
            	loggerValue.append(selectQuery);
                _log.debug(methodName,loggerValue);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_cardGroupSetID);
            pstmtSelect.setString(2, p_cardGroupId);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                prevReversalPermitted = rs.getString("reversal_permitted");
            }
            if (!p_reversalPermitted.equals(prevReversalPermitted)) {
                result = false;
            }
        }
            }
        }// end of try
        catch (SQLException sqle) {
    		loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName,loggerValue);
            String logVal1=loggerValue.toString();
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[checkForReversalPermittedChange]", "",
                "", "", logVal1);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
    		loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue);
            String logVal=loggerValue.toString();
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[checkForReversalPermittedChange]", "",
                "", "", logVal);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Exiting result:");
            	loggerValue.append(result);
                _log.debug(methodName,loggerValue);
            }
        }// end of finally
        return result;
    }

    /**
     * @return
     * @throws BTSLBaseException
     */
    public ConcurrentMap<String, ArrayList<CardGroupDetailsVO>> loadCrdGrpRevPrmtdCache() throws BTSLBaseException {
        final String methodName = "loadCardGroupCache";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        
        Connection con = null;
        final ConcurrentMap<String, ArrayList<CardGroupDetailsVO>> cardGroupMap = new ConcurrentHashMap<String, ArrayList<CardGroupDetailsVO>>();
       
        CardGroupDetailsVO cardGroupDetailsVO = null;
        String key = null;
        StringBuilder keyBuilder=new StringBuilder();
        String oldKey = null;
        ArrayList<CardGroupDetailsVO> cardGroupList = null;
        try {
			StringBuilder qry =new StringBuilder(" SELECT reversal_permitted,card_group_set_id,card_group_id,REVERSAL_MODIFIED_DATE ");
            qry.append(" FROM card_group_details ");
            final String selectQuery = qry.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            con = OracleUtil.getSingleConnection();
            try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                cardGroupDetailsVO = new CardGroupDetailsVO();
                cardGroupDetailsVO.setCardGroupID(rs.getString("card_group_id"));
                cardGroupDetailsVO.setReversalPermitted(rs.getString("reversal_permitted"));
                cardGroupDetailsVO.setCardGroupSetID(rs.getString("card_group_set_id"));
				cardGroupDetailsVO.setReversalModifiedDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("reversal_modified_date")));
				keyBuilder.setLength(0);
				keyBuilder.append(cardGroupDetailsVO.getCardGroupSetID());
				keyBuilder.append("_");
				keyBuilder.append(cardGroupDetailsVO.getCardGroupID());
                key = keyBuilder.toString();
                if (oldKey == null) {
                    cardGroupList = new ArrayList<>();
                } else if (!oldKey.equals(key)) {
                    cardGroupMap.put(oldKey, cardGroupList);
                    cardGroupList = new ArrayList<>();
                }
                cardGroupList.add(cardGroupDetailsVO);
                oldKey = key;
            }
            // Added for bug removal incase of single card group
            if (oldKey != null && oldKey.equals(key)) {
                cardGroupMap.put(oldKey, cardGroupList);
            }
            return cardGroupMap;
        }
        }// end of try
        catch (BTSLBaseException bex) {
        	loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException ");
        	loggerValue.append(bex.getMessage());
            _log.error(methodName,loggerValue);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "CardGroupDAO[loadCrdGrpRevPrmtdCache]", "", "", "",
                "Base Exception:" + bex.getMessage());
            throw bex;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception ");
        	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCrdGrpRevPrmtdCache]", "", "", "",
            		logVal1);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
            _log.error(methodName,loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCrdGrpRevPrmtdCache]", "", "", "",
            		logVal1);
            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	OracleUtil.closeQuietly(con);
        	
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting cardGroupMap.size()=:");
            	loggerValue.append(cardGroupMap.size());
				String logVal1=loggerValue.toString();
                _log.debug(methodName,loggerValue);
            }
        }// end of finally
    }
    

	/**
	 * @param p_con
	 * @param p_transferVO
	 * @param p_requestVO
	 * @return
	 * @throws BTSLBaseException
	 */
	public ArrayList<CardGroupDetailsVO> loadCardGroupSlab(Connection p_con,TransferVO p_transferVO,RequestVO p_requestVO) throws BTSLBaseException
	{
		StringBuilder loggerValue= new StringBuilder(); 
		String methodName = "loadCardGroupSlab";
		if (_log.isDebugEnabled()){
			loggerValue.setLength(0);
	    	loggerValue.append("Entered p_cardGroupSetID=");
	    	loggerValue.append(p_transferVO.getCardGroupSetID());
			loggerValue.append(" applicable date time=");
	    	loggerValue.append(p_transferVO.getTransferDateTime());
	    	loggerValue.append(" sub service=");
	    	loggerValue.append(p_requestVO.getEnquirySubService());
	    	loggerValue.append(" requested amount=");
	    	loggerValue.append(p_requestVO.getEnquiryAmount());
			_log.debug(methodName,loggerValue);
			}
		
		ArrayList<CardGroupDetailsVO> cardGroupDetailsVOList = null;
		CardGroupDetailsVO cardGroupDetailsVO=null;
		String latestCardGroupVersion=null;
		try
		{
			latestCardGroupVersion=loadCardGroupSetVersionLatestVersion(p_con,p_transferVO.getCardGroupSetID(),p_transferVO.getTransferDateTime());
			if(!BTSLUtil.isNullString(latestCardGroupVersion))
			{
				StringBuilder qry =new StringBuilder(" SELECT cgd.card_group_set_id,cgd.version,cgd.card_group_id,cgd.start_range, ");
				qry.append(" cgd.end_range,cgd.card_group_code, cgd.card_name,cgd.reversal_permitted, cgd.reversal_modified_date,stsm.selector_name,stsm.SELECTOR_CODE ");
				qry.append(" FROM  card_group_details cgd, card_group_set cgs, service_type_selector_mapping stsm ");
				qry.append(" WHERE cgd.card_group_set_id=cgs.card_group_set_id AND cgd.version=cgs.last_version  " );
				qry.append(" AND cgd.card_group_set_id=? AND  cgd.version=?  ");
				qry.append(" AND cgd.start_range<=? AND  cgd.end_range>=? ");
				qry.append(" AND cgs.service_type=stsm.service_type AND cgs.sub_service=stsm.selector_code AND cgs.service_type=? ");
				if(!BTSLUtil.isNullString(p_requestVO.getEnquirySubService()))
					qry.append(" AND cgs.sub_service=?  ");
				String selectQuery=qry.toString();
				if(_log.isDebugEnabled()){
					loggerValue.setLength(0);
			    	loggerValue.append("select query:");
			    	loggerValue.append(selectQuery);
					_log.debug(methodName,loggerValue);
				}
				try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
				{
				pstmtSelect.setString(1,p_transferVO.getCardGroupSetID());
				pstmtSelect.setString(2,latestCardGroupVersion);
				pstmtSelect.setLong(3,PretupsBL.getSystemAmount(p_requestVO.getEnquiryAmount()));
				pstmtSelect.setLong(4,PretupsBL.getSystemAmount(p_requestVO.getEnquiryAmount()));
				pstmtSelect.setString(5,p_requestVO.getEnquiryServiceType());
				if(!BTSLUtil.isNullString(p_requestVO.getEnquirySubService()))
					pstmtSelect.setString(6,p_requestVO.getEnquirySubService());
				try(ResultSet rs = pstmtSelect.executeQuery();)
				{
				cardGroupDetailsVOList = new ArrayList<>();
				while(rs.next())
				{
					cardGroupDetailsVO = new CardGroupDetailsVO();
					cardGroupDetailsVO.setCardGroupSetID(rs.getString("card_group_set_id"));
					cardGroupDetailsVO.setCardGroupID(rs.getString("card_group_id"));
					cardGroupDetailsVO.setCardGroupCode(rs.getString("card_group_code"));
					cardGroupDetailsVO.setVersion(latestCardGroupVersion);
					cardGroupDetailsVO.setStartRange(Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("start_range"))));
					cardGroupDetailsVO.setEndRange(Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("end_range"))));
					cardGroupDetailsVO.setCardName(rs.getString("card_name"));
					cardGroupDetailsVO.setReversalPermitted(rs.getString("reversal_permitted"));
					cardGroupDetailsVO.setReversalModifiedDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("reversal_modified_date")));
					cardGroupDetailsVO.setServiceTypeSelector(rs.getString("selector_name"));
					cardGroupDetailsVO.setCardGroupSubServiceId(rs.getString("SELECTOR_CODE"));
					cardGroupDetailsVOList.add(cardGroupDetailsVO);
				}
			}
				}
			}
			else
				throw new BTSLBaseException("CardGroupDAO","loadCardGroupDetails",PretupsErrorCodesI.CARD_GROUP_SLAB_NOT_FOUND);
			
		}//end of try
		catch(BTSLBaseException bex)
		{
			loggerValue.setLength(0);
	    	loggerValue.append("BTSLBaseException ");
	    	loggerValue.append(bex.getMessage());
			_log.error(methodName,loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"CardGroupDAO[loadCardGroupSlab]","","","","Base Exception:"+bex.getMessage());
			throw bex;			
		}
		catch (SQLException sqle)
		{
			loggerValue.setLength(0);
	    	loggerValue.append("SQL Exception ");
	    	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
			_log.error(methodName,loggerValue);
			_log.errorTrace(methodName,sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CardGroupDAO[loadCardGroupSlab]","","","",logVal1);
			throw new BTSLBaseException("CardGroupDAO",methodName,PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}//end of catch
		catch (Exception e)
		{
			loggerValue.setLength(0);
	    	loggerValue.append("Exception ");
	    	loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
			_log.error(methodName,loggerValue);
			_log.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CardGroupDAO[loadCardGroupSlab]","","","",logVal1);
			throw new BTSLBaseException("CardGroupDAO",methodName,PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}//end of catch
		finally
		{
			
			if(_log.isDebugEnabled()){
				loggerValue.setLength(0);
		    	loggerValue.append("Exiting cardGroupDetailsVOList size:");
		    	loggerValue.append(cardGroupDetailsVOList.size());
				_log.debug(methodName,loggerValue);
			}
		 }//end of finally
		return cardGroupDetailsVOList;		
	}
	
	/**
	 * Method for validating card group details in the case of choice recharge
	 * @param p_startRange
	 * @param p_endRange
	 * @param p_subService
	 * @throws Exception
	 */

	
	public List<CardGroupDetailsVO> validateCardGroupDetailsForChoiceRecharge(String p_startRange,String p_endRange,String p_subService,String p_networkCode)  throws Exception
	{

		final String methodName="validateCardGroupDetailsForChoiceRecharge";
		StringBuilder loggerValue= new StringBuilder(); 
		if(_log.isDebugEnabled())
		{
			loggerValue.setLength(0);
	    	loggerValue.append("Entered p_startRange=");
	    	loggerValue.append(p_startRange);
			loggerValue.append("p_endRange=");
	    	loggerValue.append(p_endRange);
	    	loggerValue.append("p_subService=");
	    	loggerValue.append(p_subService);
			_log.debug(methodName,loggerValue);
		}
		
		Connection con =null;MComConnectionI mcomCon = null;
		StringBuilder strbuff1 =new StringBuilder();
		StringBuilder strbuff2 =new StringBuilder ();
		ArrayList<CardGroupDetailsVO> list =new ArrayList<>();
        CardGroupDetailsVO cardGroupDetailsVO=null;
        Date currentDate= new Date();
		try
		{
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			if(PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equals( p_subService.split(":")[0]))
			{
				if("1".equals(p_subService.split(":")[1]))
				{
				//for sub service 1
					
					strbuff1.append("SELECT cgsv.card_group_set_id, cgsv.version, cgsv.applicable_from ,cgd.start_range, cgd.end_range, cgs.CARD_GROUP_SET_NAME,cgd.card_name ,stsm.selector_name,stsm.SELECTOR_CODE ");
					strbuff1.append(" FROM card_group_set_versions cgsv , card_group_set cgs , card_group_details cgd , service_type_selector_mapping stsm ");
					strbuff1.append(" WHERE cgs.card_group_set_id=cgsv.card_group_set_id and cgd.card_group_set_id=cgs.card_group_set_id and");
					strbuff1.append(" cgd.card_group_set_id=cgsv.CARD_GROUP_SET_ID and cgd.VERSION=cgsv.VERSION and cgs.SERVICE_TYPE=stsm.service_type and cgs.SUB_SERVICE=stsm.selector_code ");
					strbuff1.append(" and (cgsv.applicable_from >=( SELECT MAX(cdme.applicable_from) FROM CARD_GROUP_SET_VERSIONS cdme WHERE  cdme.applicable_from<=?  AND cdme.card_group_set_id=cgs.card_group_set_id) ");
					strbuff1.append(" OR cgsv.applicable_from >=?) ");
					strbuff1.append(" and  cgd.card_group_set_id IN (SELECT card_group_set_id FROM card_group_set where service_type=? and network_code=? and sub_service<>'1') and cgs.status <>'N' ");
					strbuff1.append(" ORDER BY card_group_set_id,version ");
					
					if (_log.isDebugEnabled()) {
						loggerValue.setLength(0);
				    	loggerValue.append("Select Query= ");
				    	loggerValue.append(strbuff1.toString());
			            	_log.debug(methodName,loggerValue);
						}
					
					 try(PreparedStatement pstmt1= con.prepareStatement(strbuff1.toString());)
					 {
					 pstmt1.setTimestamp(1,BTSLUtil.getTimestampFromUtilDate(currentDate));
					 pstmt1.setTimestamp(2,BTSLUtil.getTimestampFromUtilDate(currentDate));
			         pstmt1.setString(3, p_subService.split(":")[0]);
			         pstmt1.setString(4, p_networkCode);
			        try(ResultSet rs= pstmt1.executeQuery();)
			        {
			         
				}
				}
				}
				else
				{
				//for sub service 2,3,4,5
					
					strbuff2.append("SELECT cgsv.card_group_set_id, cgsv.version, cgsv.applicable_from  ,cgd.start_range, cgd.end_range ,cgs.CARD_GROUP_SET_NAME,cgd.card_name,stsm.selector_name,stsm.SELECTOR_CODE ");
					strbuff2.append(" FROM card_group_set_versions cgsv , card_group_set cgs , card_group_details cgd, service_type_selector_mapping stsm  WHERE ");
					strbuff2.append(" cgs.card_group_set_id=cgsv.card_group_set_id and cgd.card_group_set_id=cgs.card_group_set_id and ");
					strbuff2.append(" cgd.card_group_set_id=cgsv.CARD_GROUP_SET_ID and cgd.VERSION=cgsv.VERSION and cgs.SERVICE_TYPE=stsm.service_type and cgs.SUB_SERVICE=stsm.selector_code ");
					strbuff2.append(" and (cgsv.applicable_from >=( SELECT MAX(cdme.applicable_from) FROM CARD_GROUP_SET_VERSIONS cdme WHERE  cdme.applicable_from<=?  AND cdme.card_group_set_id=cgs.card_group_set_id) ");
					strbuff2.append(" OR cgsv.applicable_from >=?) ");
					strbuff2.append(" and  cgd.card_group_set_id IN (SELECT card_group_set_id FROM card_group_set where service_type=? and network_code=? and sub_service='1') and cgs.status <>'N' ");
					strbuff2.append(" ORDER BY card_group_set_id,version");
					
					 if (_log.isDebugEnabled()) {
						 loggerValue.setLength(0);
					    	loggerValue.append("Select Query= ");
					    	loggerValue.append(strbuff2.toString());
			            	_log.debug(methodName,loggerValue);
						}
					 
					try(PreparedStatement pstmt2= con.prepareStatement(strbuff2.toString());)
					{
					pstmt2.setTimestamp(1,BTSLUtil.getTimestampFromUtilDate(currentDate));
					pstmt2.setTimestamp(2,BTSLUtil.getTimestampFromUtilDate(currentDate));
		            pstmt2.setString(3, p_subService.split(":")[0]);
		            pstmt2.setString(4, p_networkCode);
				
		            try(ResultSet rs= pstmt2.executeQuery();)
		            {
		            
		            
					
				
				
		         while(rs.next())
		         {
		           	cardGroupDetailsVO = new CardGroupDetailsVO();
		           	cardGroupDetailsVO.setApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
		           	cardGroupDetailsVO.setCardGroupSetID(rs.getString("card_group_set_id"));
		            cardGroupDetailsVO.setStartRange(rs.getLong("start_range"));
		            cardGroupDetailsVO.setEndRange(rs.getLong("end_range"));
		            cardGroupDetailsVO.setVersion(rs.getString("version"));
		            cardGroupDetailsVO.setCardGroupSetName(rs.getString("card_group_set_name"));
		            cardGroupDetailsVO.setCardName(rs.getString("card_name"));
		            cardGroupDetailsVO.setCardGroupSubServiceIdDesc(rs.getString("selector_name"));
		            cardGroupDetailsVO.setCardGroupSubServiceId(rs.getString("selector_code"));
		            list.add(cardGroupDetailsVO);
		            	
				} 
			}
	            
		}
		}
		}
		}
	      catch (SQLException sqe)
			{
	        	_log.errorTrace(methodName, sqe);
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQL Exception:");
	        	loggerValue.append(sqe.getMessage());
	    		String logVal1=loggerValue.toString();
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorUtil[validateCardGroupDetailsForChoiceRecharge]","","","",logVal1);
				throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
			}
	        catch (Exception ex)
			{
	        	_log.errorTrace(methodName, ex);
	        	loggerValue.setLength(0);
	        	loggerValue.append("Exception:");
	        	loggerValue.append(ex.getMessage());
	    		String logVal1=loggerValue.toString();
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorUtil[validateCardGroupDetailsForChoiceRecharge]","","","",logVal1);
				throw new BTSLBaseException(this, methodName, "error.general.processing");
			} 
	        finally
			{
	        	if(mcomCon != null){mcomCon.close("CardGroupDAO#validateCardGroupDetailsForChoiceRecharge");mcomCon=null;}
	        	
				if (_log.isDebugEnabled()) {
					loggerValue.setLength(0);
			    	loggerValue.append("Exiting: list=");
			    	loggerValue.append(list);
					_log.debug(methodName,loggerValue);
				}
			}//end of finally.
			return list;
	}
	

	
	/**
	 * @param p_con
	 * @param p_transferVO
	 * @param p_requestVO
	 * @return
	 * @throws BTSLBaseException
	 */
	public ArrayList<CardGroupDetailsVO> loadALLCardGroupSlab(Connection p_con,TransferVO p_transferVO,RequestVO p_requestVO) throws BTSLBaseException
	{
		StringBuilder loggerValue= new StringBuilder(); 
		String methodName = "loadCardGroupSlab";
		if (_log.isDebugEnabled()){
			loggerValue.setLength(0);
	    	loggerValue.append("Entered p_cardGroupSetID=");
	    	loggerValue.append(p_transferVO.getCardGroupSetID());
			loggerValue.append(" applicable date time=");
	    	loggerValue.append(p_transferVO.getTransferDateTime());
	    	loggerValue.append(" sub service=");
	    	loggerValue.append(p_requestVO.getEnquirySubService());
	    	
			_log.debug(methodName,loggerValue);
			}
		
		ArrayList<CardGroupDetailsVO> cardGroupDetailsVOList = null;
		CardGroupDetailsVO cardGroupDetailsVO=null;
		String latestCardGroupVersion=null;
		try
		{
			latestCardGroupVersion=loadCardGroupSetVersionLatestVersion(p_con,p_transferVO.getCardGroupSetID(),p_transferVO.getTransferDateTime());
			if(!BTSLUtil.isNullString(latestCardGroupVersion))
			{
				StringBuilder qry =new StringBuilder(" SELECT cgd.card_group_set_id,cgd.version,cgd.card_group_id,cgd.start_range, ");
				qry.append(" cgd.end_range,cgd.card_group_code, cgd.card_name,cgd.reversal_permitted, cgd.reversal_modified_date,stsm.selector_name,stsm.SELECTOR_CODE ,cgd.VALIDITY_PERIOD");
				qry.append(" FROM  card_group_details cgd, card_group_set cgs, service_type_selector_mapping stsm ");
				qry.append(" WHERE cgd.card_group_set_id=cgs.card_group_set_id AND cgd.version=cgs.last_version  " );
				qry.append(" AND cgd.card_group_set_id=? AND  cgd.version=?  ");
				//qry.append(" AND cgd.start_range<=? AND  cgd.end_range>=? ");
				qry.append(" AND cgs.service_type=stsm.service_type AND cgs.sub_service=stsm.selector_code AND cgs.service_type=? ");
				if(!BTSLUtil.isNullString(p_requestVO.getEnquirySubService()))
					qry.append(" AND cgs.sub_service=?  ");
				String selectQuery=qry.toString();
				if(_log.isDebugEnabled()){
					loggerValue.setLength(0);
			    	loggerValue.append("select query:");
			    	loggerValue.append(selectQuery);
					_log.debug(methodName,loggerValue);
				}
				try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
				{
				pstmtSelect.setString(1,p_transferVO.getCardGroupSetID());
				pstmtSelect.setString(2,latestCardGroupVersion);
				pstmtSelect.setString(3,p_requestVO.getEnquiryServiceType());
				if(!BTSLUtil.isNullString(p_requestVO.getEnquirySubService()))
					pstmtSelect.setString(4,p_requestVO.getEnquirySubService());
				try(ResultSet rs = pstmtSelect.executeQuery();)
				{
				cardGroupDetailsVOList = new ArrayList<>();
				while(rs.next())
				{
					cardGroupDetailsVO = new CardGroupDetailsVO();
					cardGroupDetailsVO.setCardGroupSetID(rs.getString("card_group_set_id"));
					cardGroupDetailsVO.setCardGroupID(rs.getString("card_group_id"));
					cardGroupDetailsVO.setCardGroupCode(rs.getString("card_group_code"));
					cardGroupDetailsVO.setVersion(latestCardGroupVersion);
					cardGroupDetailsVO.setStartRange(Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("start_range"))));
					cardGroupDetailsVO.setEndRange(Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("end_range"))));
					cardGroupDetailsVO.setCardName(rs.getString("card_name"));
					if(BTSLUtil.isNullString(rs.getString("reversal_permitted"))){
						cardGroupDetailsVO.setReversalPermitted("Y");
					}else{
						cardGroupDetailsVO.setReversalPermitted(rs.getString("reversal_permitted"));
					}
					
					cardGroupDetailsVO.setReversalModifiedDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("reversal_modified_date")));
					cardGroupDetailsVO.setServiceTypeSelector(rs.getString("selector_name"));
					cardGroupDetailsVO.setCardGroupSubServiceId(rs.getString("SELECTOR_CODE"));
					cardGroupDetailsVO.setValidityPeriod(rs.getString("VALIDITY_PERIOD"));
					cardGroupDetailsVOList.add(cardGroupDetailsVO);
				}
			}
				}
			}
			else
				throw new BTSLBaseException("CardGroupDAO","loadCardGroupDetails",PretupsErrorCodesI.CARD_GROUP_SLAB_NOT_FOUND);
			
		}//end of try
		catch(BTSLBaseException bex)
		{
			loggerValue.setLength(0);
	    	loggerValue.append("BTSLBaseException ");
	    	loggerValue.append(bex.getMessage());
			_log.error(methodName,loggerValue);
			EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"CardGroupDAO[loadCardGroupSlab]","","","","Base Exception:"+bex.getMessage());
			throw bex;			
		}
		catch (SQLException sqle)
		{
			loggerValue.setLength(0);
	    	loggerValue.append("SQL Exception ");
	    	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
			_log.error(methodName,loggerValue);
			_log.errorTrace(methodName,sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CardGroupDAO[loadCardGroupSlab]","","","",logVal1);
			throw new BTSLBaseException("CardGroupDAO",methodName,PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}//end of catch
		catch (Exception e)
		{
			loggerValue.setLength(0);
	    	loggerValue.append("Exception ");
	    	loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
			_log.error(methodName,loggerValue);
			_log.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CardGroupDAO[loadCardGroupSlab]","","","",logVal1);
			throw new BTSLBaseException("CardGroupDAO",methodName,PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}//end of catch
		finally
		{
			
			if(_log.isDebugEnabled()){
				loggerValue.setLength(0);
		    	loggerValue.append("Exiting cardGroupDetailsVOList size:");
		    	loggerValue.append(cardGroupDetailsVOList.size());
				_log.debug(methodName,loggerValue);
			}
		 }//end of finally
		return cardGroupDetailsVOList;		
	}

	
	
	   public String loadServiceType(Connection p_con, String p_networkCode, String p_module,String serviceTypeDesc) throws BTSLBaseException {
	        final String methodName = "loadServiceType";
	        StringBuilder loggerValue= new StringBuilder(); 
			loggerValue.setLength(0);
	    	loggerValue.append("Entered p_networkCode=");
	    	loggerValue.append(p_networkCode);
	    	loggerValue.append("Entered p_module=");
	    	loggerValue.append(p_module);
	    	loggerValue.append("Entered serviceTypeDesc=");
	    	loggerValue.append(serviceTypeDesc);
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName,loggerValue);
	        }
	         
	         String serviceType = null;
	        try {
	            final StringBuilder selectQueryBuff = new StringBuilder("SELECT DISTINCT ST.service_type FROM service_type ST,network_services NS");
	            selectQueryBuff.append(" WHERE ST.service_type=NS.service_type AND NS.status<>'N' AND ST.status<>'N' AND NS.sender_network=? AND ST.module=? AND ST.external_interface='Y' AND ST.name=? ");
	            if (_log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Query selectQueryBuff:");
	            	loggerValue.append(selectQueryBuff.toString());
	                _log.debug(methodName,loggerValue);
	            }
	           try( PreparedStatement pstmtSelect = p_con.prepareStatement(selectQueryBuff.toString());)
	           {
	            pstmtSelect.setString(1, p_networkCode);
	            pstmtSelect.setString(2, p_module);
	            pstmtSelect.setString(3, serviceTypeDesc);
	            try(ResultSet rs = pstmtSelect.executeQuery();)
	            {
	            while (rs.next()) {
	                serviceType = rs.getString("service_type");
	            }
	        }
	           }
	        }// end of try
	        catch (SQLException sqle) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQLException ");
	        	loggerValue.append(sqle.getMessage());
	    		String logVal1=loggerValue.toString();
	            _log.error(methodName,loggerValue);
	            _log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadServiceTypeList]", "", "", "",
	                "SQL Exception:" + sqle.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        }// end of catch
	        catch (Exception e) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Exception ");
	        	loggerValue.append(e.getMessage());
	    		String logVal1=loggerValue.toString();
	            _log.error(methodName,loggerValue);
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadServiceTypeList]", "", "", "",
	            		logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        }// end of catch
	        finally {
	        	
	            if (_log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting serviceType:");
	            	loggerValue.append(serviceType);
	                _log.debug(methodName,loggerValue);
	            }
	        }// end of final
	        return serviceType;
	    }
	   
	   public boolean isCardGroupSetExist(Connection con, String serviceType, String subServiceTypeCode, String ntwrkcode, String cardGroupSetName) throws BTSLBaseException 
		{
			 final String methodName = "isCardGroupSetExist";
		        StringBuilder loggerValue= new StringBuilder(); 
				loggerValue.setLength(0);
		    	loggerValue.append("Entered CardGroupSetName=");
		    	loggerValue.append(cardGroupSetName);
		        if (_log.isDebugEnabled()) {
		            _log.debug(methodName,loggerValue);
		        }
		        
		        boolean isExist = false;
		        try {
		            final StringBuilder selectQueryBuff = new StringBuilder(" SELECT card_group_set_name, card_group_set_id");
		            selectQueryBuff.append(" FROM card_group_set ");
		            selectQueryBuff.append(" WHERE SERVICE_TYPE=? AND sub_service=? and network_code=? and card_group_set_name = ?");
		            selectQueryBuff.append(" AND status <>'N'");
		            final String selectQuery = selectQueryBuff.toString();
		            if (_log.isDebugEnabled()) {
		            	loggerValue.setLength(0);
		            	loggerValue.append("select query:");
		            	loggerValue.append(selectQuery);
		                _log.debug(methodName, loggerValue);
		            }
		            try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);)
		            {
		            pstmtSelect.setString(1, serviceType);
		            pstmtSelect.setString(2, subServiceTypeCode);
		            pstmtSelect.setString(3, ntwrkcode);
		            pstmtSelect.setString(4, cardGroupSetName);
		            try(ResultSet rs = pstmtSelect.executeQuery();)
		            {
		            	if (rs.next()) {
		            		isExist = true;
		            	}
		            }
		            }
		        }// end of try
		        catch (SQLException sqle) {
		        	loggerValue.setLength(0);
		        	loggerValue.append("SQL Exception ");
		        	loggerValue.append(sqle.getMessage());
		    		String logVal1=loggerValue.toString();
		            _log.error(methodName,loggerValue);
		            _log.errorTrace(methodName, sqle);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[isCardGroupSetExist]", "", "", "",
		            		logVal1);
		            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
		        }// end of catch
		        catch (Exception e) {
		        	loggerValue.setLength(0);
		        	loggerValue.append("Exception ");
		        	loggerValue.append(e.getMessage());
		    		String logVal1=loggerValue.toString();
		            _log.error(methodName,loggerValue);
		            _log.errorTrace(methodName, e);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[isCardGroupSetExist]", "", "", "",
		            		logVal1);
		            throw new BTSLBaseException("CardGroupDAO", methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
		        }// end of catch
		        finally {
		        	loggerValue.setLength(0);
		           _log.debug(methodName,loggerValue);
		        }// end of finally
		        return isExist;      
		}
	   
	   public String loadCardgroupSetID(Connection p_con, String p_cardGroupID, String version) throws BTSLBaseException
	{
		   String setId = ""; 
		   final String methodName = "loadCardgroupSetID";
	        StringBuilder loggerValue= new StringBuilder(); 
			loggerValue.setLength(0);
	    	loggerValue.append("Entered CardGroupId=");
	    	loggerValue.append(p_cardGroupID);
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName,loggerValue);
	        }
	        
	        try {
	            final StringBuilder selectQueryBuff = new StringBuilder("SELECT CARD_GROUP_SET_ID");
	            selectQueryBuff.append(" FROM PRETUPS_TRUNK_DEV.CARD_GROUP_DETAILS WHERE CARD_GROUP_ID = ? AND VERSION = ? ");
	            if (_log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Query selectQueryBuff:");
	            	loggerValue.append(selectQueryBuff.toString());
	                _log.debug(methodName,loggerValue);
	            }
	           try( PreparedStatement pstmtSelect = p_con.prepareStatement(selectQueryBuff.toString());)
	           {
	            pstmtSelect.setString(1, p_cardGroupID);
	            pstmtSelect.setString(2, version);
	            try(ResultSet rs = pstmtSelect.executeQuery();)
	            {
	            while (rs.next()) {
	            	setId = rs.getString("CARD_GROUP_SET_ID");
	            	}
	            }
	           }
	         }// end of try
	         catch (SQLException sqle) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQLException ");
	        	loggerValue.append(sqle.getMessage());
	    		String logVal1=loggerValue.toString();
	            _log.error(methodName,loggerValue);
	            _log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardgroupSetID]", "", "", "",
	                "SQL Exception:" + sqle.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        }// end of catch
	        catch (Exception e) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Exception ");
	        	loggerValue.append(e.getMessage());
	    		String logVal1=loggerValue.toString();
	            _log.error(methodName,loggerValue);
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadCardgroupSetID]", "", "", "",
	            		logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        }// end of catch
	        finally {
	        	
	            if (_log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting cardGroupSetId:");
	            	loggerValue.append(setId);
	                _log.debug(methodName,loggerValue);
	            }
	        }// end of final
	        
	        return setId;
	   
}
	   
	   
	   
	   
	   
	   
	   public ArrayList ValidateServiceTypeList(Connection p_con, String p_networkCode, String p_module,String serviceType) throws BTSLBaseException {
	        final String methodName = "loadServiceTypeList";
	        StringBuilder loggerValue= new StringBuilder(); 
			loggerValue.setLength(0);
	    	loggerValue.append("Entered p_networkCode=");
	    	loggerValue.append(p_networkCode);
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName,loggerValue);
	        }
	         
	        final ArrayList serviceTypeList = new ArrayList();
	        try {
	            final StringBuilder selectQueryBuff = new StringBuilder("SELECT DISTINCT ST.service_type,ST.name FROM service_type ST,network_services NS");
	            selectQueryBuff.append(" WHERE ST.service_type=NS.service_type AND NS.status<>'N' AND ST.status<>'N' AND NS.sender_network=? AND ST.module=? AND ST.external_interface='Y'  and ST.service_type= ? ORDER BY ST.name");
	            if (_log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Query selectQueryBuff:");
	            	loggerValue.append(selectQueryBuff.toString());
	                _log.debug(methodName,loggerValue);
	            }
	           try( PreparedStatement pstmtSelect = p_con.prepareStatement(selectQueryBuff.toString());)
	           {
	            pstmtSelect.setString(1, p_networkCode);
	            pstmtSelect.setString(2, p_module);
	            pstmtSelect.setString(3, serviceType);
	            
	            try(ResultSet rs = pstmtSelect.executeQuery();)
	            {
	            while (rs.next()) {
	                serviceTypeList.add(new ListValueVO(SqlParameterEncoder.encodeParams(rs.getString("name")),
	                		SqlParameterEncoder.encodeParams(rs.getString("service_type"))));
	            }
	        }
	           }
	        }// end of try
	        catch (SQLException sqle) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQLException ");
	        	loggerValue.append(sqle.getMessage());
	    		String logVal1=loggerValue.toString();
	            _log.error(methodName,loggerValue);
	            _log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadServiceTypeList]", "", "", "",
	                "SQL Exception:" + sqle.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        }// end of catch
	        catch (Exception e) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Exception ");
	        	loggerValue.append(e.getMessage());
	    		String logVal1=loggerValue.toString();
	            _log.error(methodName,loggerValue);
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[loadServiceTypeList]", "", "", "",
	            		logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        }// end of catch
	        finally {
	        	
	            if (_log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting serviceTypeList.size:");
	            	loggerValue.append(serviceTypeList.size());
	                _log.debug(methodName,loggerValue);
	            }
	        }// end of final
	        return serviceTypeList;
	    }
	   
	   
	   
	   
	   public ArrayList validateServiceTypeList(Connection p_con, String p_networkCode, String p_module,String serviceType) throws BTSLBaseException {
	        final String methodName = "loadServiceTypeList";
	        StringBuilder loggerValue= new StringBuilder(); 
			loggerValue.setLength(0);
	    	loggerValue.append("Entered p_networkCode=");
	    	loggerValue.append(p_networkCode);
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName,loggerValue);
	        }
	         
	        final ArrayList serviceTypeList = new ArrayList();
	        try {
	            final StringBuilder selectQueryBuff = new StringBuilder("SELECT DISTINCT ST.service_type,ST.name FROM service_type ST,network_services NS");
	            selectQueryBuff.append(" WHERE ST.service_type=NS.service_type AND NS.status<>'N' AND ST.status<>'N' AND NS.sender_network=? AND ST.module=? AND ST.external_interface='Y' and ST.service_type = ? ORDER BY ST.name");
	            if (_log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Query selectQueryBuff:");
	            	loggerValue.append(selectQueryBuff.toString());
	                _log.debug(methodName,loggerValue);
	            }
	           try( PreparedStatement pstmtSelect = p_con.prepareStatement(selectQueryBuff.toString());)
	           {
	            pstmtSelect.setString(1, p_networkCode);
	            pstmtSelect.setString(2, p_module);
	            pstmtSelect.setString(3, serviceType);
	            
	            try(ResultSet rs = pstmtSelect.executeQuery();)
	            {
	            while (rs.next()) {
	                serviceTypeList.add(new ListValueVO(SqlParameterEncoder.encodeParams(rs.getString("name")),
	                		SqlParameterEncoder.encodeParams(rs.getString("service_type"))));
	            }
	        }
	           }
	        }// end of try
	        catch (SQLException sqle) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQLException ");
	        	loggerValue.append(sqle.getMessage());
	    		String logVal1=loggerValue.toString();
	            _log.error(methodName,loggerValue);
	            _log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[validateServiceTypeList]", "", "", "",
	                "SQL Exception:" + sqle.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        }// end of catch
	        catch (Exception e) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Exception ");
	        	loggerValue.append(e.getMessage());
	    		String logVal1=loggerValue.toString();
	            _log.error(methodName,loggerValue);
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupDAO[validateServiceTypeList]", "", "", "",
	            		logVal1);
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        }// end of catch
	        finally {
	        	
	            if (_log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting serviceTypeList.size:");
	            	loggerValue.append(serviceTypeList.size());
	                _log.debug(methodName,loggerValue);
	            }
	        }// end of final
	        return serviceTypeList;
	    }



	   
}


