package com.btsl.db.query.postgres;

import com.btsl.pretups.cardgroup.businesslogic.CardGroupQry;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;

/**
 * @author satakshi.gaur
 *
 */
public class CardGroupPostgresQry implements CardGroupQry {

	@Override
	public StringBuilder loadCardGroupCacheQry() {
		final StringBuilder qry = new StringBuilder(" SELECT l.lookup_name set_name,cs.set_type,cgd.card_group_set_id, ");
        qry.append(" cgd.card_group_id,cgd.card_group_code,cgd.start_range,cgd.end_range,cgd.validity_period_type, ");
        qry.append(" cgd.validity_period,cgd.grace_period, cgd.sender_tax1_name, cgd.sender_tax1_type, ");
        qry.append(" cgd.sender_tax1_rate, cgd.sender_tax2_name, cgd.sender_tax2_type, cgd.sender_tax2_rate, ");
        qry.append(" cgd.receiver_tax1_name,cgd.receiver_tax1_type, cgd.receiver_tax1_rate,cgd.receiver_tax2_name, ");
        qry.append(" cgd.receiver_tax2_type, cgd.receiver_tax2_rate,  ");
        qry.append(" cgd.bonus_validity_value, cgd.sender_access_fee_type, cgd.sender_access_fee_rate, ");
        qry.append(" cgd.receiver_access_fee_type,cgd.receiver_access_fee_rate, cgd.min_sender_access_fee, ");
        qry.append(" cgd.max_sender_access_fee, cgd.min_receiver_access_fee,cgd.max_receiver_access_fee, cgd.multiple_of, ");
        qry.append(" cgd.voucher_type, cgd.voucher_segment, cgd.voucher_product_id, ");
        qry.append(" cs.sub_service,cs.card_group_set_name, stsm.selector_name, cs.service_type, st.name service_name,  cgd.status, ");
        qry.append(" cgd.online_offline,cgd.both,cgd.sender_mult_factor,cgd.receiver_mult_factor, ");
        qry.append(" cgs.version, cgs.applicable_from, cgd.cos_required , cgd.in_promo, cgd.card_name, cgd.reversal_permitted, cgd.reversal_modified_date ");
        if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CGTAX34APP)).booleanValue())
            qry.append(" , cgd.receiver_tax3_name, cgd.receiver_tax3_type, cgd.receiver_tax3_rate,cgd.receiver_tax4_name, cgd.receiver_tax4_type, cgd.receiver_tax4_rate ");
        qry.append(" FROM card_group_details cgd,lookups l, card_group_set cs, service_type st,service_type_selector_mapping stsm, card_group_set_versions cgs ");
        qry.append(" WHERE cgs.card_group_set_id=cgd.card_group_set_id AND cgs.version=cgd.version ");
        qry.append(" AND l.lookup_type=? AND cs.card_group_set_id=cgd.card_group_set_id ");
        qry.append(" AND (cgs.applicable_from >=(SELECT MAX(cdme.applicable_from) FROM CARD_GROUP_SET_VERSIONS cdme WHERE  (cdme.applicable_from<=CURRENT_TIMESTAMP AND cdme.card_group_set_id=cgd.card_group_set_id)) ");
        qry.append(" OR cgs.applicable_from >=CURRENT_TIMESTAMP) ");
        // Ended Here
        qry.append(" AND cs.service_type=st.service_type AND stsm.service_type=st.service_type ");
        qry.append(" AND cs.sub_service=stsm.selector_code AND cs.service_type=stsm.service_type AND l.lookup_code=cs.set_type ");
        qry.append(" ORDER BY card_group_set_id,cgd.version ");
		return qry;
	}
	@Override
	public String addCardGroupDetailsQry() {
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append("INSERT INTO card_group_details (card_group_set_id,");
        strBuff.append("version,card_group_id,start_range,end_range,validity_period_type,");
        strBuff.append("validity_period,grace_period,sender_tax1_name,sender_tax1_type, ");
        strBuff.append("sender_tax1_rate,sender_tax2_name,sender_tax2_type,sender_tax2_rate,");
        strBuff.append("receiver_tax1_name,receiver_tax1_type,receiver_tax1_rate,");
        strBuff.append("receiver_tax2_name,receiver_tax2_type,receiver_tax2_rate,");
        strBuff.append("sender_access_fee_type,sender_access_fee_rate,min_sender_access_fee,");
        strBuff.append("max_sender_access_fee,receiver_access_fee_type,receiver_access_fee_rate,");
        strBuff.append("min_receiver_access_fee,max_receiver_access_fee,card_group_code,multiple_of,bonus_validity_value, ");
        strBuff.append("online_offline,\"both\",sender_mult_factor,receiver_mult_factor,status,cos_required,in_promo,card_name, ");
        strBuff.append("reversal_permitted,reversal_modified_date,voucher_type,voucher_segment,voucher_product_id");
        if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CGTAX34APP)).booleanValue()){
			strBuff.append(",receiver_tax3_name,receiver_tax3_type,receiver_tax3_rate,");
			strBuff.append("receiver_tax4_name,receiver_tax4_type,receiver_tax4_rate )");
		}
		else
		strBuff.append(" )");
		if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CGTAX34APP)).booleanValue()){
		strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		}
		else
        strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		return strBuff.toString();
	}

}
