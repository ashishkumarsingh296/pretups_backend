package com.btsl.db.query.postgres;

import java.util.ArrayList;

import com.btsl.pretups.cardgroup.businesslogic.BatchModifyCardGroupQry;

public class BatchModifyCardGroupPostgresQry implements BatchModifyCardGroupQry {
    @Override

    public String loadCardGroupDetailsListByDateQry(String p_serviceType, ArrayList cgSetselectedList) {
        final StringBuilder qry = new StringBuilder(" ");
        qry.append("SELECT DISTINCT cgs.card_group_set_id,cgs.card_group_set_name,cgs.network_code,cgs.service_type,cgd.card_group_code,cgd.version,cgs.last_version,cgd.start_range,");
        qry.append(" cgd.end_range, cgd.validity_period,cgd.grace_period,cgd.multiple_of, cgd.receiver_tax1_name,cgd.receiver_tax1_type,");
        qry.append(" cgd.receiver_tax1_rate,cgd.receiver_tax2_name,cgd.receiver_tax2_type, cgd.receiver_tax2_rate,  ");
        qry.append(" cgd.receiver_access_fee_type,cgd.receiver_access_fee_rate,cgd.min_receiver_access_fee,cgd.max_receiver_access_fee,");
        qry.append(" cgd.bonus_validity_value,cgd.validity_period_type,cgd.status,");
        qry.append(" cgs.sub_service,cgd.online_offline, cgd.both,cgd.card_group_id,");
        // for P2P
        qry.append(" cgd.sender_tax1_name, cgd.sender_tax1_type, cgd.sender_tax1_rate, cgd.sender_tax2_name, cgd.sender_tax2_type, cgd.sender_tax2_rate,");
        qry.append(" cgd.sender_access_fee_type, cgd.sender_access_fee_rate, cgd.min_sender_access_fee, cgd.max_sender_access_fee,");
        qry.append(" cgd.voucher_type,cgd.voucher_segment,cgd.voucher_product_id,");
        // Added for conversion
        qry.append(" cgd.sender_mult_factor, cgd.receiver_mult_factor,cgd.cos_required,coalesce(cgd.in_promo,'0')in_promo ");
        qry.append(" ,cgd.card_name, cgd.reversal_permitted ");
        qry.append(" FROM CARD_GROUP_DETAILS cgd,CARD_GROUP_SET cgs");
        qry.append(" WHERE cgd.card_group_set_id=cgs.card_group_set_id");
        qry.append(" AND cgd.VERSION in( SELECT DISTINCT VERSION");
        qry.append(" FROM CARD_GROUP_SET_VERSIONS csv1");
        qry.append(" WHERE csv1.card_group_set_id=cgd.card_group_set_id");
        qry.append(" AND csv1.applicable_from =(SELECT MAX(csv2.applicable_from)");
        qry.append(" FROM CARD_GROUP_SET_VERSIONS csv2");
        qry.append(" WHERE  csv2.applicable_from<=? AND csv2.card_group_set_id=cgd.card_group_set_id) )");
        qry.append(" AND cgs.status='Y' AND cgs.module_code=? AND cgd.status<>'N' AND cgs.network_code=?");
        if (!("ALL".equalsIgnoreCase(p_serviceType))) {
            qry.append(" AND cgs.service_type=?");
        }
//         if(cgSetselectedList!=null && cgSetselectedList.size()>0)
//         {
//        	 qry.append(" AND cgs.card_group_set_id in ('0' ");
//        	 for(int i = 0; i < cgSetselectedList.size(); i++)
//        	 {
//        		 qry.append(" ,'"+cgSetselectedList.get(i)+"'");
//        	 }
//        	 qry.append(")");
//         }
        qry.append(" ORDER BY cgs.card_group_set_name");
        return qry.toString();
    }

    @Override
    public String loadCardGroupDetailsListByDateQryFile(String p_serviceType, ArrayList cgSetselectedList) {
        final StringBuilder qry = new StringBuilder(" ");
        qry.append("SELECT DISTINCT cgs.card_group_set_id,cgs.card_group_set_name,cgs.network_code,cgs.service_type,cgd.card_group_code,cgd.version,cgs.last_version,cgd.start_range,");
        qry.append(" cgd.end_range, cgd.validity_period,cgd.grace_period,cgd.multiple_of, cgd.receiver_tax1_name,cgd.receiver_tax1_type,");
        qry.append(" cgd.receiver_tax1_rate,cgd.receiver_tax2_name,cgd.receiver_tax2_type, cgd.receiver_tax2_rate,  ");
        qry.append(" cgd.receiver_access_fee_type,cgd.receiver_access_fee_rate,cgd.min_receiver_access_fee,cgd.max_receiver_access_fee,");
        qry.append(" cgd.bonus_validity_value,cgd.validity_period_type,cgd.status,");
        qry.append(" cgs.sub_service,cgd.online_offline, cgd.both,cgd.card_group_id,");
        // for P2P
        qry.append(" cgd.sender_tax1_name, cgd.sender_tax1_type, cgd.sender_tax1_rate, cgd.sender_tax2_name, cgd.sender_tax2_type, cgd.sender_tax2_rate,");
        qry.append(" cgd.sender_access_fee_type, cgd.sender_access_fee_rate, cgd.min_sender_access_fee, cgd.max_sender_access_fee,");
        qry.append(" cgd.voucher_type,cgd.voucher_segment,cgd.voucher_product_id,");
        // Added for conversion
        qry.append(" cgd.sender_mult_factor, cgd.receiver_mult_factor,cgd.cos_required,coalesce(cgd.in_promo,'0')in_promo ");
        qry.append(" ,cgd.card_name, cgd.reversal_permitted ");
        qry.append(" FROM CARD_GROUP_DETAILS cgd,CARD_GROUP_SET cgs");
        qry.append(" WHERE cgd.card_group_set_id=cgs.card_group_set_id");
        qry.append(" AND cgd.VERSION in( SELECT DISTINCT VERSION");
        qry.append(" FROM CARD_GROUP_SET_VERSIONS csv1");
        qry.append(" WHERE csv1.card_group_set_id=cgd.card_group_set_id");
        qry.append(" AND csv1.applicable_from =(SELECT MAX(csv2.applicable_from)");
        qry.append(" FROM CARD_GROUP_SET_VERSIONS csv2");
        qry.append(" WHERE  csv2.applicable_from<=? AND csv2.card_group_set_id=cgd.card_group_set_id) )");
        qry.append(" AND cgs.status='Y' AND cgs.module_code=? AND cgd.status<>'N' AND cgs.network_code=?");
        if (!("ALL".equalsIgnoreCase(p_serviceType))) {
            qry.append(" AND cgs.service_type=?");
        }
        if (cgSetselectedList != null && cgSetselectedList.size() > 0) {
            qry.append(" AND cgs.card_group_set_id in ('0' ");
            for (int i = 0; i < cgSetselectedList.size(); i++) {
                qry.append(" ,'" + cgSetselectedList.get(i) + "'");
            }
            qry.append(")");
        }
        qry.append(" ORDER BY cgs.card_group_set_name");
        return qry.toString();
    }

//    /**
//     * @see 'New' query to fetch all version before given date
//     * @param p_serviceType
//     * @param cgSetselectedList
//     * @return
//     */
//    @Override
//    public String loadCardGroupDetailsListByDateQryFile(String p_serviceType, ArrayList cgSetselectedList) {
//        final StringBuilder qry = new StringBuilder(" ");
//        qry.append("SELECT DISTINCT cgs.card_group_set_id, cgs.card_group_set_name, cgs.network_code, cgs.service_type, cgd.card_group_code, cgd.version, cgs.last_version, cgd.start_range,");
//        qry.append(" cgd.end_range, cgd.validity_period, cgd.grace_period, cgd.multiple_of, cgd.receiver_tax1_name, cgd.receiver_tax1_type, cgd.receiver_tax1_rate, cgd.receiver_tax2_name, cgd.receiver_tax2_type, cgd.receiver_tax2_rate, cgd.receiver_access_fee_type, cgd.receiver_access_fee_rate, cgd.min_receiver_access_fee, cgd.max_receiver_access_fee, cgd.bonus_validity_value, cgd.validity_period_type, cgd.status, cgs.sub_service, cgd.online_offline,");
//        qry.append("cgd.both, cgd.card_group_id, cgd.sender_tax1_name, cgd.sender_tax1_type, cgd.sender_tax1_rate, cgd.sender_tax2_name, cgd.sender_tax2_type, cgd.sender_tax2_rate, cgd.sender_access_fee_type, cgd.sender_access_fee_rate, cgd.min_sender_access_fee, cgd.max_sender_access_fee, cgd.voucher_type, cgd.voucher_segment, cgd.voucher_product_id, cgd.sender_mult_factor, cgd.receiver_mult_factor, cgd.cos_required,");
//        qry.append("COALESCE(cgd.in_promo, '0') AS in_promo,cgd.card_name, cgd.reversal_permitted ");
//        qry.append("FROM CARD_GROUP_DETAILS cgd JOIN CARD_GROUP_SET cgs ON cgd.card_group_set_id = cgs.card_group_set_id ");
//        qry.append("WHERE");
//        if (cgSetselectedList != null && cgSetselectedList.size() > 0) {
//            qry.append(" cgs.card_group_set_id in ('0' ");
//            for (int i = 0; i < cgSetselectedList.size(); i++) {
//                qry.append(" ,'" + cgSetselectedList.get(i) + "'");
//            }
//            qry.append(") AND");
//        }
//        qry.append(" cgs.status = 'Y'");
//        qry.append("AND cgs.module_code = ? AND cgs.network_code = ? AND cgs.service_type = ? AND cgd.status <> 'N'");
//        qry.append(" AND cgd.version IN (");
//        qry.append("SELECT DISTINCT version FROM CARD_GROUP_SET_VERSIONS");
//        qry.append(" WHERE applicable_from <= ? AND card_group_set_id = cgd.card_group_set_id )");
//        qry.append("ORDER BY cgs.card_group_set_name");
//        return qry.toString();
//    }
}
