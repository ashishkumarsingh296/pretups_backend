package com.web.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class ChannelTransferWebDAO {

    /**
     * Commons Logging instance.
     */
    private static Log _log = LogFactory.getLog(ChannelTransferWebDAO.class.getName());
    private static OperatorUtilI _operatorUtilI = null;
    static {
        try {
            _operatorUtilI = (OperatorUtilI) Class.forName((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS)).newInstance();
        } catch (Exception e) {

            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BuddyMgtAction", "", "", "",
                "Exception while loading the operator util class in class :" + ChannelTransferWebDAO.class.getName() + ":" + e.getMessage());
        }
    }

    /**
     * Method loadC2STransferVOList.
     * This method load the list of the transfers for the C2S Type.
     * This method is modified by sandeep goel as netwok code is passed as
     * argument to load only login user's
     * network transacitons.
     * 
     * @param p_con
     *            Connection
     * @param p_activeUserID
     *            String
     * @param p_fromDate
     *            Date
     * @param p_toDate
     *            Date
     * @param Users
     *            ArrayList
     * @param p_receiverMsisdn
     *            String
     * @param p_transferID
     *            String
     * @param String
     *            Sender Category
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadStaffChnlEnquiryTransfersList(Connection p_con, String p_activeUserID, ArrayList userList, Date p_fromDate, Date p_toDate, String p_transferNum, String p_type, String p_transferTypeCode, String senderCat) throws BTSLBaseException {

        final String methodName = "loadStaffChnlEnquiryTransfersList";
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                "Entered  p_activeUserID: " + p_activeUserID + " FromDate:" + p_fromDate + " ToDate:" + p_toDate + " transferNum:" + p_transferNum + " TYPE: " + p_type + "Transfer type: " + p_transferTypeCode);
        }
        ListValueVO user = new ListValueVO();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer("SELECT ct.transfer_id,ct.network_code,ct.network_code_for, ");
        strBuff.append("ct.grph_domain_code, ct.domain_code,ct.sender_category_code,ct.sender_grade_code, ct.dual_comm_type, ");
        strBuff.append("ct.receiver_grade_code,ct.from_user_id,ct.to_user_id,ct.transfer_date,ct.transfer_MRP, ");
        strBuff.append("ct.reference_no,ct.requested_quantity,ct.channel_user_remarks,ct.type,ct.payable_amount, ");
        strBuff.append("ct.net_payable_amount,ct.pmt_inst_type,ct.pmt_inst_no,ct.pmt_inst_date,ct.pmt_inst_amount, ");
        strBuff.append("ct.total_tax1,ct.total_tax2,ct.total_tax3,ct.product_type,ct.transfer_sub_type, ct.transfer_type,");
        strBuff.append("ct.transfer_category,ct.source,ct.control_transfer,ct.msisdn from_msisdn, ct.to_msisdn to_msisdn, ct.active_user_id, ");
        strBuff.append("u1.user_name frmuser,u2.user_name touser,u1.user_code frmcode,u2.user_code tocode, ");
        strBuff.append("l.lookup_name,ug2.grph_domain_code r_geo_code,u2.category_code r_cat_code, ");
        strBuff
            .append("cat2.category_name r_cat_name,cat2.domain_code r_dom_code,cat1.category_name s_cat_name,cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity ");
        strBuff.append("FROM channel_transfers ct, users u1, users u2 , lookups l,user_geographies ug2,categories cat1 ,categories cat2,channel_transfers_items cti ");
        strBuff.append("WHERE ");
        strBuff.append(" ct.transfer_date >= ? AND ct.transfer_date < ? ");
        if (!BTSLUtil.isNullString(p_transferNum)) {
            strBuff.append(" AND ct.transfer_id=? ");
        }
        if (!BTSLUtil.isNullString(p_activeUserID)) {
            strBuff.append(" AND ct.active_user_id= ? ");
        }

        if (!PretupsI.ALL.equals(p_transferTypeCode)) {
            strBuff.append("AND ct.transfer_sub_type=? ");
        }
        final boolean isstaffList = true;
        if (BTSLUtil.isNullString(p_transferNum) && BTSLUtil.isNullString(p_activeUserID)) {
            if (userList != null && userList.size() == 1) {
                user = (ListValueVO) userList.get(0);
                /*
                 * if(user.getType().equals(PretupsI.CHANNEL_USER_TYPE))
                 * {
                 * strBuff.append(" AND ct.active_user_id= ? ");
                 * }
                 * else
                 */
                {
                    strBuff.append(" AND ct.active_user_id= ? ");
                }
            }
            if (userList != null && userList.size() > 1) {
                final StringBuffer str = new StringBuffer();
                for (int k = 0; k < userList.size(); k++) {
                    user = (ListValueVO) userList.get(k);
                    /*
                     * if (user.getType().equals(PretupsI.CHANNEL_USER_TYPE))
                     * {
                     * isstaffList=false;
                     * break;
                     * }
                     * else
                     * {
                     */
                    if (!BTSLUtil.isNullString(user.getValue())) {
                        str.append("'");
                        str.append(user.getValue());
                        str.append("',");
                    }

                    // }
                }
                final String userID = str.substring(0, str.length() - 1);
                if (isstaffList && !BTSLUtil.isNullString(userID)) {
                    strBuff.append("AND ct.active_user_id in (" + userID + ") ");
                } else {
                    strBuff.append("AND (ct.from_user_id = ? OR ct.to_user_id =?) ");
                }
            }
        }
        strBuff.append(" AND u1.status <> 'N' AND u1.status <> 'C' AND u2.status <> 'N' AND u2.status <> 'C' ");
        strBuff.append(" AND u2.user_id=ug2.user_id AND u2.category_code=cat2.category_code AND u1.category_code=cat1.category_code AND ");
        strBuff
            .append("ct.type=? AND ct.from_user_id=u1.user_id AND ct.to_user_id=u2.user_id AND l.lookup_code=ct.transfer_sub_type AND l.lookup_type=? AND ct.transfer_id=cti.transfer_id ");
        strBuff.append("ORDER BY ct.created_on DESC ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList enquiryItemsList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int m = 0;
            ++m;
            pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            ++m;
            pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(p_toDate, 1)));
            if (!BTSLUtil.isNullString(p_transferNum)) {
                ++m;
                pstmt.setString(m, p_transferNum);
            }
            if (!BTSLUtil.isNullString(p_activeUserID)) {
                ++m;
                pstmt.setString(m, p_activeUserID);
            }
            if (!PretupsI.ALL.equals(p_transferTypeCode)) {
                ++m;
                pstmt.setString(m, p_transferTypeCode);
            }
            if (BTSLUtil.isNullString(p_transferNum) && BTSLUtil.isNullString(p_activeUserID)) {
                if (userList != null && userList.size() == 1) {
                    if (user.getType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                        ++m;
                        pstmt.setString(m, user.getValue());
                    } else {
                        ++m;
                        pstmt.setString(m, user.getValue());
                    }
                } else if (userList != null && userList.size() > 1) {
                    if (!isstaffList) {
                        ++m;
                        pstmt.setString(m, user.getValue());
                        ++m;
                        pstmt.setString(m, user.getValue());
                    }

                }
            }
            ++m;
            pstmt.setString(m, p_type);
            ++m;
            pstmt.setString(m, PretupsI.TRANSFER_TYPE);
            rs = pstmt.executeQuery();

            ChannelTransferVO transferVO = null;
            final ArrayList sourceTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSACTION_SOURCE_TYPE, true);
            while (rs.next()) {
                transferVO = new ChannelTransferVO();
                transferVO.setTransferType(rs.getString("transfer_type"));
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setNetworkCode(rs.getString("network_code"));
                transferVO.setNetworkCodeFor(rs.getString("network_code_for"));
                transferVO.setGraphicalDomainCode(rs.getString("grph_domain_code"));
                transferVO.setDomainCode(rs.getString("domain_code"));
                transferVO.setSenderGradeCode(rs.getString("sender_grade_code"));
                transferVO.setReceiverGradeCode(rs.getString("receiver_grade_code"));
                transferVO.setFromUserID(rs.getString("from_user_id"));
                transferVO.setToUserID(rs.getString("to_user_id"));
                transferVO.setTransferDate(rs.getDate("transfer_date"));
                transferVO.setTransferDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("transfer_date"))));
                transferVO.setReferenceNum(rs.getString("reference_no"));
                transferVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                transferVO.setChannelRemarks(rs.getString("channel_user_remarks"));
                transferVO.setType(rs.getString("type"));
                transferVO.setPayableAmount(rs.getLong("payable_amount"));
                transferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
                transferVO.setPayInstrumentType(rs.getString("pmt_inst_type"));
                transferVO.setPayInstrumentNum(rs.getString("pmt_inst_no"));
                transferVO.setPayInstrumentDate(rs.getDate("pmt_inst_date"));
                transferVO.setPayInstrumentAmt(rs.getLong("pmt_inst_amount"));
                transferVO.setTotalTax1(rs.getLong("total_tax1"));
                transferVO.setTotalTax2(rs.getLong("total_tax2"));
                transferVO.setTotalTax3(rs.getLong("total_tax3"));
                transferVO.setProductType(rs.getString("product_type"));
                transferVO.setFromUserCode(rs.getString("from_msisdn"));
                transferVO.setToUserCode(rs.getString("to_msisdn"));
                transferVO.setTransferMRP(rs.getLong("transfer_mrp"));
                transferVO.setFromUserName(rs.getString("frmuser"));
                transferVO.setToUserName(rs.getString("touser"));
                transferVO.setTransferSubType(rs.getString("lookup_name"));
                transferVO.setTransferCategoryCode(rs.getString("transfer_category"));
                transferVO.setSource(BTSLUtil.getOptionDesc(rs.getString("source"), sourceTypeList).getLabel());
                transferVO.setControlTransfer(rs.getString("control_transfer"));
                transferVO.setReceiverCategoryCode(rs.getString("r_cat_code"));
                transferVO.setReceiverCategoryDesc(rs.getString("r_cat_name"));
                transferVO.setReceiverGgraphicalDomainCode(rs.getString("r_geo_code"));
                transferVO.setReceiverDomainCode(rs.getString("r_dom_code"));
                transferVO.setSenderCatName(rs.getString("s_cat_name"));
                transferVO.setCommQty(rs.getLong("commision_quantity"));
                transferVO.setSenderDrQty(rs.getLong("sender_debit_quantity"));
                transferVO.setReceiverCrQty(rs.getLong("receiver_credit_quantity"));
                transferVO.setActiveUserId(rs.getString("active_user_id"));
                transferVO.setDualCommissionType(rs.getString("dual_comm_type"));
                enquiryItemsList.add(transferVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferWebDAO[loadStaffChnlEnquiryTransfersList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception ex) {
            _log.error("", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferWebDAO[loadStaffChnlEnquiryTransfersList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:  arrayList Size =" + enquiryItemsList.size());
            }
        }
        return enquiryItemsList;

    }

    // ------------------------ Added by Amit Raheja
    // -------------------------------

    /**
     * This function loads the details of channel to channel thansfer
     * 
     * @param p_con
     * @param p_fromUserCode
     * @param p_toUserCode
     * @param p_fromDate
     * @param p_toDate
     * @param p_transferNum
     * @param p_type
     * @param p_transferTypeCode
     * @return
     * @throws BTSLBaseException
     * */
    public ArrayList loadChnlToChnlEnquiryTransfersListC2C(Connection p_con, String p_fromUserCode, String p_toUserCode, Date p_fromDate, Date p_toDate, String p_transferNum, String p_type, String p_transferTypeCode , String p_networkCode) throws BTSLBaseException {

        final String methodName = "loadChnlToChnlEnquiryTransfersListC2C";
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                "Entered  fromUserCode: " + p_fromUserCode + " toUserCode: " + p_toUserCode + " FromDate:" + p_fromDate + " ToDate:" + p_toDate + " transferNum:" + p_transferNum + " TYPE: " + p_type + "Transfer type: " + p_transferTypeCode + "Network Code:" +p_networkCode);
        }
        Boolean isChannelSOSEnable = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
        Boolean isSecondaryNumberAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED);
        String isFromUserPrimary = null;
        String isToUserPrimary = null;
        if (isSecondaryNumberAllowed) {
            if (BTSLUtil.isNullString(p_transferNum)) {
                final UserDAO userDAO = new UserDAO();
                UserPhoneVO userPhoneVO = null;
                if (!BTSLUtil.isNullString(p_fromUserCode)) {
                    userPhoneVO = userDAO.loadUserAnyPhoneVO(p_con, p_fromUserCode);
                    if (userPhoneVO != null && ("N".equalsIgnoreCase(userPhoneVO.getPrimaryNumber()))) {
                        isFromUserPrimary = userPhoneVO.getPrimaryNumber();
                    }
                }
                if (!BTSLUtil.isNullString(p_toUserCode)) {
                    userPhoneVO = userDAO.loadUserAnyPhoneVO(p_con, p_toUserCode);
                    if (userPhoneVO != null && ("N".equalsIgnoreCase(userPhoneVO.getPrimaryNumber()))) {
                        isToUserPrimary = userPhoneVO.getPrimaryNumber();
                    }
                }
            }
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer("SELECT ct.transfer_id,ct.network_code,ct.network_code_for, ");
        strBuff.append("ct.grph_domain_code, ct.domain_code,ct.sender_category_code,ct.sender_grade_code, ct.dual_comm_type, ");
        strBuff.append("ct.receiver_grade_code,ct.from_user_id,ct.to_user_id,ct.transfer_date,ct.transfer_MRP, ");
        strBuff.append("ct.reference_no,ct.requested_quantity,ct.channel_user_remarks,ct.type,cti.payable_amount, ");
        strBuff.append("cti.net_payable_amount,ct.pmt_inst_type,ct.pmt_inst_no,ct.pmt_inst_date,ct.pmt_inst_amount, ");
        strBuff.append("ct.total_tax1,ct.total_tax2,ct.total_tax3,ct.product_type,ct.transfer_sub_type, ct.transfer_type,");
        strBuff.append("ct.transfer_category,ct.source,ct.control_transfer,ct.msisdn from_msisdn, ct.to_msisdn to_msisdn, ");
        if (isChannelSOSEnable)  {
        strBuff.append("ct.SOS_STATUS, ct.SOS_SETTLEMENT_DATE, ");
        }
        strBuff.append("u1.user_name frmuser,u2.user_name touser,u1.user_code frmcode,u2.user_code tocode, ");
        strBuff.append("l.lookup_name,ug2.grph_domain_code r_geo_code,u2.category_code r_cat_code, ");
        strBuff
            .append("cat2.category_name r_cat_name,cat2.domain_code r_dom_code,cat1.category_name s_cat_name,cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity, ");
        strBuff.append(" cti.SENDER_PREVIOUS_STOCK, cti.RECEIVER_PREVIOUS_STOCK, cti.SENDER_POST_STOCK, cti.RECEIVER_POST_STOCK ");
        // added for ussd
        strBuff.append(",ct.cell_id,ct.switch_id, ct.TRANSFER_CATEGORY ");
        strBuff.append("FROM channel_transfers ct, users u1, users u2 , lookups l,user_geographies ug2,categories cat1 ,categories cat2,channel_transfers_items cti  ");
        if ((!BTSLUtil.isNullString(isFromUserPrimary)) || (!BTSLUtil.isNullString(isFromUserPrimary))) {
            strBuff.append(", user_phones up1, user_phones up2 ");
        }
        strBuff.append("WHERE ");
        if (!BTSLUtil.isNullString(p_transferNum)) {
            strBuff.append("ct.transfer_id=? AND ");
        } else {
            if (!BTSLUtil.isNullString(p_fromUserCode)) {
                if (BTSLUtil.isNullString(isFromUserPrimary)) {
                    strBuff.append("(u1.user_code=? OR u2.user_code=? ) AND ");
                } else {
                    strBuff.append("(up1.msisdn=? OR up2.msisdn=? ) AND ");
                    strBuff.append("(u1.user_id=up1.user_id OR u2.user_id=up2.user_id ) AND ");
                }
            }
            if (!BTSLUtil.isNullString(p_toUserCode)) {
                if (BTSLUtil.isNullString(isFromUserPrimary)) {
                    strBuff.append(" (u2.user_code=? OR u1.user_code=? ) AND ");
                } else {
                    strBuff.append("(up2.msisdn=? OR up1.msisdn=? ) AND ");
                    strBuff.append("(u2.user_id=up2.user_id OR u1.user_id=up1.user_id ) AND ");
                }
            }
        }
            strBuff.append(" ct.transfer_date >= ? AND ct.transfer_date < ? AND ");

            if (!PretupsI.ALL.equals(p_transferTypeCode)) {
                strBuff.append("ct.transfer_sub_type=? AND ");
            }
        
        // strBuff.append(" u1.status <> 'N' AND u1.status <> 'C' AND u2.status <> 'N' AND u2.status <> 'C' ");
        strBuff.append(" u2.user_id=ug2.user_id AND u2.category_code=cat2.category_code AND u1.category_code=cat1.category_code AND ");
        strBuff
            .append("ct.type=? AND ct.from_user_id=u1.user_id AND ct.to_user_id=u2.user_id AND l.lookup_code=ct.transfer_sub_type AND l.lookup_type=? AND ct.transfer_id=cti.transfer_id ");
        if (!BTSLUtil.isNullString(isFromUserPrimary) || (!BTSLUtil.isNullString(isFromUserPrimary))) {
            strBuff.append(" AND ct.msisdn=up1.msisdn AND ct.to_msisdn=up2.msisdn ");
        }
        strBuff.append("AND ct.network_code=?");
        strBuff.append("ORDER BY ct.created_on ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList enquiryItemsList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int m = 0;
            if (!BTSLUtil.isNullString(p_transferNum)) {
                ++m;
                pstmt.setString(m, p_transferNum);
            } else {
                if (!BTSLUtil.isNullString(p_fromUserCode)) {
                    ++m;
                    pstmt.setString(m, p_fromUserCode);
                    ++m;
                    pstmt.setString(m, "");
                }
                if (!BTSLUtil.isNullString(p_toUserCode)) {
                    ++m;
                    pstmt.setString(m, p_toUserCode);
                    ++m;
                    pstmt.setString(m, "");
                }
            }
                ++m;
                pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                ++m;
                pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(p_toDate, 1)));
                if (!PretupsI.ALL.equals(p_transferTypeCode)) {
                    ++m;
                    pstmt.setString(m, p_transferTypeCode);
                }
            
            ++m;
            pstmt.setString(m, p_type);
            ++m;
            pstmt.setString(m, PretupsI.TRANSFER_TYPE);
            ++m;
            pstmt.setString(m, p_networkCode);
            rs = pstmt.executeQuery();

            ChannelTransferVO transferVO = null;
            final ArrayList sourceTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSACTION_SOURCE_TYPE, true);
			HashMap<String, String> validateDuplicateTransferID= new HashMap<String,String>();
            while (rs.next()) {
			
                if(!validateDuplicateTransferID.containsKey(rs.getString("transfer_id"))){
                	validateDuplicateTransferID.put(rs.getString("transfer_id"), rs.getString("transfer_id"));
					transferVO = new ChannelTransferVO();
					transferVO.setTransferCategory(rs.getString("TRANSFER_CATEGORY"));
					transferVO.setTransferType(rs.getString("transfer_type"));
					transferVO.setTransferID(rs.getString("transfer_id"));
					transferVO.setNetworkCode(rs.getString("network_code"));
					transferVO.setNetworkCodeFor(rs.getString("network_code_for"));
					transferVO.setGraphicalDomainCode(rs.getString("grph_domain_code"));
					transferVO.setDomainCode(rs.getString("domain_code"));
					transferVO.setTransferDate(rs.getDate("transfer_date"));
					transferVO.setTransferDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("transfer_date"))));
					transferVO.setReferenceNum(rs.getString("reference_no"));
					transferVO.setRequestedQuantity(rs.getLong("requested_quantity"));
					transferVO.setChannelRemarks(rs.getString("channel_user_remarks"));
					transferVO.setType(rs.getString("type"));
					transferVO.setPayableAmount(rs.getLong("payable_amount"));
					
					transferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
					transferVO.setPayInstrumentType(rs.getString("pmt_inst_type"));
					transferVO.setPayInstrumentNum(rs.getString("pmt_inst_no"));
					transferVO.setPayInstrumentDate(rs.getDate("pmt_inst_date"));
					transferVO.setPayInstrumentAmt(rs.getLong("pmt_inst_amount"));
					transferVO.setTotalTax1(rs.getLong("total_tax1"));
					transferVO.setTotalTax2(rs.getLong("total_tax2"));
					transferVO.setTotalTax3(rs.getLong("total_tax3"));
					transferVO.setProductType(rs.getString("product_type"));
					transferVO.setFromUserCode(rs.getString("from_msisdn"));
					transferVO.setToUserCode(rs.getString("to_msisdn"));
			
					if (isChannelSOSEnable)  {
				        transferVO.setSosStatus(rs.getString("SOS_STATUS"));
				        transferVO.setSosSettlementDate(rs.getDate("SOS_SETTLEMENT_DATE"));
				        }
					transferVO.setTransferMRP(rs.getLong("transfer_mrp"));
					
					transferVO.setTransferSubType(rs.getString("lookup_name"));
					
					if(transferVO.getTransferSubType().equals(PretupsI.C2C_REVERSE_SUBTYPE))
					{
						transferVO.setToUserName(rs.getString("frmuser"));
						transferVO.setFromUserName(rs.getString("touser"));
						transferVO.setSenderCatName(rs.getString("r_cat_name"));
						transferVO.setReceiverCategoryDesc(rs.getString("s_cat_name"));
						transferVO.setReceiverGradeCode(rs.getString("sender_grade_code"));
						transferVO.setSenderGradeCode(rs.getString("receiver_grade_code"));
						transferVO.setToUserID(rs.getString("from_user_id"));
						transferVO.setFromUserID(rs.getString("to_user_id"));
					}
					else
					{
						transferVO.setFromUserName(rs.getString("frmuser"));
						transferVO.setToUserName(rs.getString("touser"));
						transferVO.setReceiverCategoryCode(rs.getString("r_cat_code"));
						transferVO.setReceiverCategoryDesc(rs.getString("r_cat_name"));
						transferVO.setSenderCatName(rs.getString("s_cat_name"));
						transferVO.setSenderGradeCode(rs.getString("sender_grade_code"));
						transferVO.setReceiverGradeCode(rs.getString("receiver_grade_code"));
						transferVO.setFromUserID(rs.getString("from_user_id"));
						transferVO.setToUserID(rs.getString("to_user_id"));
					}
					transferVO.setTransferCategoryCode(rs.getString("transfer_category"));
					transferVO.setSource(BTSLUtil.getOptionDesc(rs.getString("source"), sourceTypeList).getLabel());
					transferVO.setControlTransfer(rs.getString("control_transfer"));

					transferVO.setReceiverGgraphicalDomainCode(rs.getString("r_geo_code"));
					transferVO.setReceiverDomainCode(rs.getString("r_dom_code"));

					transferVO.setCommQty(rs.getLong("commision_quantity"));
					transferVO.setSenderDrQty(rs.getLong("sender_debit_quantity"));
					transferVO.setReceiverCrQty(rs.getLong("receiver_credit_quantity"));
					transferVO.setReceiverPostStock(rs.getString("RECEIVER_POST_STOCK"));
					transferVO.setReceiverPreviousStock(rs.getLong("RECEIVER_PREVIOUS_STOCK"));
					transferVO.setSenderPostStock(rs.getString("SENDER_POST_STOCK"));
					transferVO.setSenderPreviousStock(rs.getLong("SENDER_PREVIOUS_STOCK"));
					// added for ussd
					transferVO.setCellId(rs.getString("cell_id"));
					transferVO.setSwitchId(rs.getString("switch_id"));
					transferVO.setDualCommissionType(rs.getString("dual_comm_type"));
					enquiryItemsList.add(transferVO);
				}
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferWebDAO[loadChnlToChnlEnquiryTransfersListC2C]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception ex) {
            _log.error("", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferWebDAO[loadChnlToChnlEnquiryTransfersListC2C]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:  arrayList Size =" + enquiryItemsList.size());
            }
        }
        return enquiryItemsList;
    }

    // ----------------------Addition ends
    // -----------------------------------------

    /*
     * Added by Amit Raheja for reverse transactions
     */
    /**
     * This function loads the details of channel to channel thansfer
     * 
     * @param p_con
     * @param p_fromUserID
     * @param p_toUserID
     * @param p_fromDate
     * @param p_toDate
     * @param p_transferNum
     * @param p_type
     * @param p_transferTypeCode
     * @param p_isMoreMsisdn
     * @return
     * @throws BTSLBaseException
     * */
    public ArrayList loadReversalChnlToChnlTransfersList(Connection p_con, String p_fromUserID, String p_toUserID, String p_senOrRecMobileNo, Date p_fromDate, Date p_toDate, String p_transferNum, String p_type) throws BTSLBaseException {

        final String methodName = "loadReversalChnlToChnlTransfersList";
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                "Entered  fromUserCode: " + p_fromUserID + " toUserCode: " + p_toUserID + " FromDate:" + p_fromDate + " ToDate:" + p_toDate + " transferNum:" + p_transferNum + " p_senOrRecMobileNo:" + p_senOrRecMobileNo + " TYPE: " + p_type);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer("SELECT ct.ref_transfer_id,ct.transfer_id,ct.network_code,ct.network_code_for, ct.transaction_mode, ");
        strBuff.append("ct.grph_domain_code, ct.domain_code,ct.sender_category_code,ct.sender_grade_code, ");
        strBuff.append("ct.receiver_grade_code,ct.from_user_id,ct.to_user_id,ct.transfer_date,ct.transfer_MRP, ");
        strBuff.append("ct.reference_no,ct.ext_txn_no,ct.ext_txn_date,ct.requested_quantity,ct.channel_user_remarks,ct.created_on,ct.created_by,ct.type,ct.payable_amount, ");
        strBuff.append("ct.net_payable_amount,ct.pmt_inst_type,ct.pmt_inst_no,ct.pmt_inst_date,ct.pmt_inst_amount, ");
        strBuff.append("ct.status,ct.transfer_initiated_by,ct.total_tax1,ct.total_tax2,ct.total_tax3,ct.product_type,ct.transfer_sub_type, ct.transfer_type,");
        strBuff
            .append("ct.receiver_txn_profile,ct.sender_txn_profile,ct.transfer_category,ct.source,ct.control_transfer,ct.msisdn from_msisdn, ct.to_msisdn to_msisdn,ct.commission_profile_ver,ct.commission_profile_set_id, ");
        strBuff.append("ct.transfer_category, ct.request_gateway_code,ct.request_gateway_type,ct.pmt_inst_source, ");
        strBuff.append("ct.first_approved_by, ct.second_approved_by,ct.third_approved_by,ct.sms_default_lang, ");
        strBuff.append("ct.sms_second_lang, ct.first_approved_on,ct.second_approved_on,ct.third_approved_on, ");
        strBuff.append("ct.first_approver_limit, ct.second_approver_limit,ct.close_date,ct.modified_on, ");
        strBuff.append("u1.user_name frmuser,u2.user_name touser,u1.user_code frmcode,u2.user_code tocode, ");
        strBuff.append("u1.login_id from_login_id,u2.login_id to_login_id,ct.to_grph_domain_code r_geo_code,u2.category_code r_cat_code, ");
        strBuff.append("cat2.category_name r_cat_name,cat2.domain_code r_dom_code,cat1.category_name s_cat_name ");
        strBuff.append(",ct.cell_id,ct.switch_id,ct.dual_comm_type ");
        strBuff.append("FROM channel_transfers ct, users u1, users u2,categories cat1 ,categories cat2 ");
        if (BTSLUtil.isNullString(p_transferNum)) {
            strBuff.append(",user_phones up ");
        }
        strBuff.append("WHERE ct.TYPE=? AND ct.transfer_sub_type='T' AND ");
        if (!BTSLUtil.isNullString(p_senOrRecMobileNo) && PretupsI.CHANNEL_TYPE_C2C.equals(p_type)) {
            strBuff.append(" ct.to_msisdn=? AND ");
        }
        if (!BTSLUtil.isNullString(p_transferNum)) {
            strBuff.append("ct.transfer_id=? AND ");
        } else if (!BTSLUtil.isNullString(p_fromUserID)) {
            strBuff.append(" ct.from_user_id=? AND ct.msisdn=up.msisdn AND ct.from_user_id=up.user_id AND ");
        } else if (!BTSLUtil.isNullString(p_toUserID)) {
            strBuff.append(" ct.to_user_id=? AND ct.to_msisdn=up.msisdn AND ct.to_user_id=up.user_id AND ");
        }
        if (p_fromDate != null) {
            strBuff.append(" ct.created_on >=  ? AND ");
        }
        if (p_toUserID != null) {
            strBuff.append(" ct.created_on <= ?  AND ");
        }
        strBuff.append(" ct.from_user_id=u1.user_id AND ct.to_user_id=u2.user_id AND ");
        strBuff.append(" u2.category_code=cat2.category_code  AND u1.category_code=cat1.category_code ");
        strBuff.append(" ORDER BY ct.created_on DESC ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList reverseTrnItemsList = new ArrayList();
        final ArrayList alreadyReverseTrnItemsList = new ArrayList();
        final ArrayList returnList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int m = 0;
            ++m;
            pstmt.setString(m, p_type);
            if (!BTSLUtil.isNullString(p_senOrRecMobileNo) && PretupsI.CHANNEL_TYPE_C2C.equals(p_type)) {
                ++m;
                pstmt.setString(m, p_senOrRecMobileNo);
            }
            if (!BTSLUtil.isNullString(p_transferNum)) {
                ++m;
                pstmt.setString(m, p_transferNum);
            } else if (!BTSLUtil.isNullString(p_fromUserID)) {
                ++m;
                pstmt.setString(m, p_fromUserID);
            } else if (!BTSLUtil.isNullString(p_toUserID)) {
                ++m;
                pstmt.setString(m, p_toUserID);
            }

            if (p_fromDate != null) {
                ++m;
                pstmt.setTimestamp(m, BTSLUtil.getSQLDateTimeFromUtilDate(p_fromDate));
            }
            if (p_toUserID != null) {
                ++m;
                pstmt.setTimestamp(m, BTSLUtil.getSQLDateTimeFromUtilDate(p_toDate));
            }
            rs = pstmt.executeQuery();

            ChannelTransferVO transferVO = null;
            final ArrayList sourceTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSACTION_SOURCE_TYPE, true);
            final ArrayList list = LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE, true);
            while (rs.next()) {
                transferVO = new ChannelTransferVO();
                transferVO.setTransferType(rs.getString("transfer_type"));
                transferVO.setRefTransferID(rs.getString("ref_transfer_id"));
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setNetworkCode(rs.getString("network_code"));
                transferVO.setNetworkCodeFor(rs.getString("network_code_for"));
                transferVO.setGraphicalDomainCode(rs.getString("grph_domain_code"));
                transferVO.setDomainCode(rs.getString("domain_code"));
                transferVO.setCategoryCode(rs.getString("sender_category_code"));
                transferVO.setSenderGradeCode(rs.getString("sender_grade_code"));
                transferVO.setReceiverGradeCode(rs.getString("receiver_grade_code"));
                transferVO.setFromUserID(rs.getString("from_user_id"));
                transferVO.setToUserID(rs.getString("to_user_id"));
                transferVO.setTransferDate(rs.getDate("transfer_date"));
                transferVO.setTransferDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("transfer_date"))));
                transferVO.setReferenceNum(rs.getString("reference_no"));
                transferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
                transferVO.setExternalTxnDate(rs.getDate("ext_txn_date"));
                transferVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                // transferVO.setChannelRemarks( rs.getString(
                // "channel_user_remarks"));
                transferVO.setCreatedBy(rs.getString("created_by"));
                transferVO.setCreatedOn(rs.getTimestamp("created_on"));
                transferVO.setCloseDate(rs.getTimestamp("close_date"));
                transferVO.setStatus(rs.getString("status"));
                transferVO.setTransferInitatedBy(rs.getString("transfer_initiated_by"));
                transferVO.setSenderTxnProfile(rs.getString("sender_txn_profile"));
                transferVO.setReceiverTxnProfile(rs.getString("receiver_txn_profile"));
                transferVO.setType(rs.getString("type"));
                transferVO.setPayableAmount(rs.getLong("payable_amount"));
                transferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
                transferVO.setPayInstrumentType(rs.getString("pmt_inst_type"));
                transferVO.setPayInstrumentNum(rs.getString("pmt_inst_no"));
                transferVO.setPayInstrumentDate(rs.getDate("pmt_inst_date"));
                transferVO.setPayInstrumentAmt(rs.getLong("pmt_inst_amount"));
                transferVO.setTotalTax1(rs.getLong("total_tax1"));
                transferVO.setTotalTax2(rs.getLong("total_tax2"));
                transferVO.setTotalTax3(rs.getLong("total_tax3"));
                transferVO.setProductType(rs.getString("product_type"));
                transferVO.setFromUserCode(rs.getString("from_msisdn"));
                transferVO.setToUserCode(rs.getString("to_msisdn"));
                transferVO.setTransferMRP(rs.getLong("transfer_mrp"));
                transferVO.setFromUserName(rs.getString("frmuser"));
                transferVO.setToUserName(rs.getString("touser"));
                transferVO.setTransferCategoryCode(rs.getString("transfer_category"));
                transferVO.setSource(BTSLUtil.getOptionDesc(rs.getString("source"), sourceTypeList).getLabel());
                transferVO.setTransferSubTypeValue(rs.getString("transfer_sub_type"));
                transferVO.setTransferSubType(BTSLUtil.getOptionDesc(rs.getString("transfer_sub_type"), list).getLabel());
                transferVO.setControlTransfer(rs.getString("control_transfer"));
                transferVO.setReceiverCategoryCode(rs.getString("r_cat_code"));
                transferVO.setReceiverCategoryDesc(rs.getString("r_cat_name"));
                transferVO.setReceiverGgraphicalDomainCode(rs.getString("r_geo_code"));
                transferVO.setReceiverDomainCode(rs.getString("r_dom_code"));
                transferVO.setSenderCatName(rs.getString("s_cat_name"));
                transferVO.setSenderLoginID(rs.getString("from_login_id"));
                transferVO.setReceiverLoginID(rs.getString("to_login_id"));
                transferVO.setCommProfileSetId(rs.getString("commission_profile_set_id"));
                transferVO.setCommProfileVersion(rs.getString("commission_profile_ver"));
                transferVO.setTransferCategory(rs.getString("transfer_category"));
                transferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                transferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
                transferVO.setPaymentInstSource(rs.getString("pmt_inst_source"));
                // transferVO.setFirstApprovedBy(rs.getString("first_approved_by"));
                // transferVO.setSecondApprovedBy(rs.getString("second_approved_by"));
                // transferVO.setThirdApprovedBy(rs.getString("third_approved_by"));
                transferVO.setDefaultLang(rs.getString("sms_default_lang"));
                transferVO.setSecondLang(rs.getString("sms_second_lang"));
                // transferVO.setFirstApprovedOn(rs.getDate("first_approved_on"));
                // transferVO.setSecondApprovedOn(rs.getDate("second_approved_on"));
                // transferVO.setThirdApprovedOn(rs.getDate("third_approved_on"));
                // transferVO.setFirstApproverLimit(rs.getLong("first_approver_limit"));
                // transferVO.setSecondApprovalLimit(rs.getLong("second_approver_limit"));
                transferVO.setDualCommissionType(rs.getString("dual_comm_type"));
                // added for ussd
                transferVO.setCellId(rs.getString("cell_id"));
                transferVO.setSwitchId(rs.getString("switch_id"));
                transferVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                transferVO.setTransactionMode(rs.getString("transaction_mode"));
                if (!BTSLUtil.isNullString(transferVO.getRefTransferID())) {
                    alreadyReverseTrnItemsList.add(transferVO);
                } else {
                    reverseTrnItemsList.add(transferVO);
                }
            }
            returnList.add(alreadyReverseTrnItemsList);
            returnList.add(reverseTrnItemsList);

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferWebDAO[loadReversalChnlToChnlTransfersList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception ex) {
            _log.error("", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferWebDAO[loadReversalChnlToChnlTransfersList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:  arrayList Size =" + reverseTrnItemsList.size());
            }
        }
        return returnList;
    }

    /**
     * updatChannelTransferAfterReverseTrx :method used for enter new Rev trn id
     * for old trn id
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @return
     * @throws BTSLBaseException
     */

    public int updatChannelTransferAfterReverseTrx(Connection p_con, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {
        final String methodName = "updatChannelTransferAfterReverseTrx";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ChannelTransferVO : " + p_channelTransferVO);
        }
        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer(" update  channel_transfers set  modified_by=?, modified_on=?, ref_transfer_id= ? ");
            strBuff.append(" where  transfer_id=? ");
            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("updatChannelTransfer", "update query:" + query);
            }

            psmt = p_con.prepareStatement(query);
            int i = 0;
            ++i;
            psmt.setString(i, p_channelTransferVO.getModifiedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getRefTransferID());
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferID());
            // boolean modifiedFlag =
            // this.isRecordModified(p_con,p_channelTransferVO.getLastModifiedTime(),p_channelTransferVO.getTransferID());
            // if (modifiedFlag)
            // throw new BTSLBaseException(this,
            // "updatC2STransferAfterReverseTrx", "error.modify.true");
            updateCount = psmt.executeUpdate();
            if (updateCount <= 0) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                    "ChannelTransferWebDAO[updatChannelTransferAfterReverseTrx]", "", "", "", "BTSLBaseException: update count <=0");
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            psmt.clearParameters();
            i = 0;
            ++i;
            psmt.setString(i, p_channelTransferVO.getModifiedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferID());
            ++i;
            psmt.setString(i, p_channelTransferVO.getRefTransferID());
            updateCount = psmt.executeUpdate();
            if (updateCount <= 0) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                    "ChannelTransferWebDAO[updatChannelTransferAfterReverseTrx]", "", "", "", "BTSLBaseException: update count <=0");
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferWebDAO[updatChannelTransferAfterReverseTrx]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferWebDAO[updatChannelTransferAfterReverseTrx]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting Success :" + updateCount);
            }
        }// end of finally

        return updateCount;
    }

    /**
     * This function loads the details of channel to channel thansfer
     * 
     * @param p_con
     * @param p_fromUserID
     * @param p_toUserID
     * @param p_fromDate
     * @param p_toDate
     * @param p_transferNum
     * @param p_type
     * @param p_transferTypeCode
     * @param p_isMoreMsisdn
     * @return
     * @throws BTSLBaseException
     * */
    public ArrayList loadReversalChnlToChnlTransfersApprovalList(Connection p_con, String p_toUserID, String p_fromUserID, String p_senOrRecMobileNo, String p_origTransferNum, String p_revTransferNum, String p_type) throws BTSLBaseException {

        final String methodName = "loadReversalChnlToChnlTransfersApprovalList";
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                "Entered  toUserCode: " + p_toUserID + " fromUserCode: " + p_fromUserID + " transferNum:" + p_origTransferNum + " revTransferNum:" + p_revTransferNum + " p_senOrRecMobileNo:" + p_senOrRecMobileNo + " TYPE: " + p_type);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer("SELECT ct.ref_transfer_id,ct.transfer_id,ct.network_code,ct.network_code_for, ");
        strBuff.append("ct.grph_domain_code, ct.domain_code,ct.sender_category_code,ct.sender_grade_code, ");
        strBuff.append("ct.receiver_grade_code,ct.from_user_id,ct.to_user_id,ct.transfer_date,ct.transfer_MRP, ");
        strBuff.append("ct.reference_no,ct.ext_txn_no,ct.ext_txn_date,ct.requested_quantity,ct.channel_user_remarks,ct.created_on,ct.created_by,ct.type,ct.payable_amount, ");
        strBuff.append("ct.net_payable_amount,ct.pmt_inst_type,ct.pmt_inst_no,ct.pmt_inst_date,ct.pmt_inst_amount, ");
        strBuff.append("ct.status,ct.transfer_initiated_by,ct.total_tax1,ct.total_tax2,ct.total_tax3,ct.product_type,ct.transfer_sub_type, ct.transfer_type,");
        strBuff
            .append("ct.receiver_txn_profile,ct.sender_txn_profile,ct.transfer_category,ct.source,ct.control_transfer,ct.msisdn from_msisdn, ct.to_msisdn to_msisdn,ct.commission_profile_ver,ct.commission_profile_set_id, ");
        strBuff.append("ct.transfer_category, ct.request_gateway_code,ct.request_gateway_type,ct.pmt_inst_source, ");
        strBuff.append("ct.first_approved_by, ct.second_approved_by,ct.third_approved_by,ct.sms_default_lang, ");
        strBuff.append("ct.sms_second_lang, ct.first_approved_on,ct.second_approved_on,ct.third_approved_on, ");
        strBuff.append("ct.first_approver_limit, ct.second_approver_limit,ct.close_date,ct.modified_on, ");
        strBuff.append("u1.user_name frmuser,u2.user_name touser,u1.user_code frmcode,u2.user_code tocode, ");
        strBuff.append("u1.login_id from_login_id,u2.login_id to_login_id,ct.to_grph_domain_code r_geo_code,u2.category_code r_cat_code, ");
        strBuff.append("cat2.category_name r_cat_name,cat2.domain_code r_dom_code,cat1.category_name s_cat_name ");
        strBuff.append(",ct.cell_id,ct.switch_id ");

        strBuff.append("FROM channel_transfers ct, users u1, users u2,categories cat1 ,categories cat2 ");
        if (BTSLUtil.isNullString(p_revTransferNum) && BTSLUtil.isNullString(p_origTransferNum)) {
            strBuff.append(",user_phones up ");
        }
        strBuff.append("WHERE ct.TYPE=? AND ct.transfer_sub_type='X' AND ");
        if (!BTSLUtil.isNullString(p_senOrRecMobileNo) && PretupsI.CHANNEL_TYPE_C2C.equals(p_type)) {
            strBuff.append(" ct.to_msisdn=? AND ");
        }
        if (!BTSLUtil.isNullString(p_revTransferNum)) {
            strBuff.append("ct.transfer_id=? AND ");
        } else if (!BTSLUtil.isNullString(p_origTransferNum)) {
            strBuff.append("ct.ref_transfer_id=? AND ");
        } else if (!BTSLUtil.isNullString(p_fromUserID)) {
            strBuff.append(" ct.from_user_id=? AND ct.msisdn=up.msisdn AND ct.from_user_id=up.user_id AND ");
        } else if (!BTSLUtil.isNullString(p_toUserID)) {
            strBuff.append(" ct.to_user_id=? AND ct.to_msisdn=up.msisdn AND ct.to_user_id=up.user_id AND ");
        }
        // strBuff.append(
        // " ct.created_on >=  ?  AND ct.created_on <= ?  AND ");
        strBuff.append(" ct.status='" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "' AND ");
        strBuff.append(" ct.from_user_id=u1.user_id AND ct.to_user_id=u2.user_id AND ");
        strBuff.append(" u2.category_code=cat2.category_code  AND u1.category_code=cat1.category_code ");
        strBuff.append(" ORDER BY ct.created_on DESC ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        // ArrayList reverseTrnItemsList = new ArrayList();
        // ArrayList alreadyReverseTrnItemsList = new ArrayList();
        final ArrayList returnList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int m = 0;
            ++m;
            pstmt.setString(m, p_type);
            if (!BTSLUtil.isNullString(p_senOrRecMobileNo) && PretupsI.CHANNEL_TYPE_C2C.equals(p_type)) {
                ++m;
                pstmt.setString(m, p_senOrRecMobileNo);
            }
            if (!BTSLUtil.isNullString(p_revTransferNum)) {
                ++m;
                pstmt.setString(m, p_revTransferNum);
            } else if (!BTSLUtil.isNullString(p_origTransferNum)) {
                ++m;
                pstmt.setString(m, p_origTransferNum);
            } else if (!BTSLUtil.isNullString(p_fromUserID)) {
                ++m;
                pstmt.setString(m, p_fromUserID);
            } else if (!BTSLUtil.isNullString(p_toUserID)) {
                ++m;
                pstmt.setString(m, p_toUserID);
            }

            // pstmt.setTimestamp(++m,
            // BTSLUtil.getSQLDateTimeFromUtilDate(p_fromDate));
            // pstmt.setTimestamp(++m,
            // BTSLUtil.getSQLDateTimeFromUtilDate(p_toDate));

            rs = pstmt.executeQuery();

            ChannelTransferVO transferVO = null;
            final ArrayList sourceTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSACTION_SOURCE_TYPE, true);
            final ArrayList list = LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE, true);
            while (rs.next()) {
                transferVO = new ChannelTransferVO();
                transferVO.setTransferType(rs.getString("transfer_type"));
                transferVO.setRefTransferID(rs.getString("ref_transfer_id"));
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setNetworkCode(rs.getString("network_code"));
                transferVO.setNetworkCodeFor(rs.getString("network_code_for"));
                transferVO.setGraphicalDomainCode(rs.getString("grph_domain_code"));
                transferVO.setDomainCode(rs.getString("domain_code"));
                transferVO.setCategoryCode(rs.getString("sender_category_code"));
                transferVO.setSenderGradeCode(rs.getString("sender_grade_code"));
                transferVO.setReceiverGradeCode(rs.getString("receiver_grade_code"));
                transferVO.setFromUserID(rs.getString("from_user_id"));
                transferVO.setToUserID(rs.getString("to_user_id"));
                transferVO.setTransferDate(rs.getDate("transfer_date"));
                transferVO.setTransferDateAsString(BTSLUtil.getDateStringFromDate(rs.getDate("transfer_date")));
                transferVO.setReferenceNum(rs.getString("reference_no"));
                transferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
                transferVO.setExternalTxnDate(rs.getDate("ext_txn_date"));
                transferVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                // transferVO.setChannelRemarks( rs.getString(
                // "channel_user_remarks"));
                transferVO.setCreatedBy(rs.getString("created_by"));
                transferVO.setCreatedOn(rs.getTimestamp("created_on"));
                transferVO.setCloseDate(rs.getTimestamp("close_date"));
                transferVO.setStatus(rs.getString("status"));
                transferVO.setTransferInitatedBy(rs.getString("transfer_initiated_by"));
                transferVO.setSenderTxnProfile(rs.getString("sender_txn_profile"));
                transferVO.setReceiverTxnProfile(rs.getString("receiver_txn_profile"));
                transferVO.setType(rs.getString("type"));
                transferVO.setPayableAmount(rs.getLong("payable_amount"));
                transferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
                transferVO.setPayInstrumentType(rs.getString("pmt_inst_type"));
                transferVO.setPayInstrumentNum(rs.getString("pmt_inst_no"));
                transferVO.setPayInstrumentDate(rs.getDate("pmt_inst_date"));
                transferVO.setPayInstrumentAmt(rs.getLong("pmt_inst_amount"));
                transferVO.setTotalTax1(rs.getLong("total_tax1"));
                transferVO.setTotalTax2(rs.getLong("total_tax2"));
                transferVO.setTotalTax3(rs.getLong("total_tax3"));
                transferVO.setProductType(rs.getString("product_type"));
                transferVO.setFromUserCode(rs.getString("from_msisdn"));
                transferVO.setToUserCode(rs.getString("to_msisdn"));
                transferVO.setTransferMRP(rs.getLong("transfer_mrp"));
                transferVO.setFromUserName(rs.getString("frmuser"));
                transferVO.setToUserName(rs.getString("touser"));
                transferVO.setTransferCategoryCode(rs.getString("transfer_category"));
                transferVO.setSource(BTSLUtil.getOptionDesc(rs.getString("source"), sourceTypeList).getLabel());
                transferVO.setTransferSubTypeValue(rs.getString("transfer_sub_type"));
                transferVO.setTransferSubType(BTSLUtil.getOptionDesc(rs.getString("transfer_sub_type"), list).getLabel());
                transferVO.setControlTransfer(rs.getString("control_transfer"));
                transferVO.setReceiverCategoryCode(rs.getString("r_cat_code"));
                transferVO.setReceiverCategoryDesc(rs.getString("r_cat_name"));
                transferVO.setReceiverGgraphicalDomainCode(rs.getString("r_geo_code"));
                transferVO.setReceiverDomainCode(rs.getString("r_dom_code"));
                transferVO.setSenderCatName(rs.getString("s_cat_name"));
                transferVO.setSenderLoginID(rs.getString("from_login_id"));
                transferVO.setReceiverLoginID(rs.getString("to_login_id"));
                transferVO.setCommProfileSetId(rs.getString("commission_profile_set_id"));
                transferVO.setCommProfileVersion(rs.getString("commission_profile_ver"));
                transferVO.setTransferCategory(rs.getString("transfer_category"));
                transferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                transferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
                transferVO.setPaymentInstSource(rs.getString("pmt_inst_source"));
                // transferVO.setFirstApprovedBy(rs.getString("first_approved_by"));
                // transferVO.setSecondApprovedBy(rs.getString("second_approved_by"));
                // transferVO.setThirdApprovedBy(rs.getString("third_approved_by"));
                transferVO.setDefaultLang(rs.getString("sms_default_lang"));
                transferVO.setSecondLang(rs.getString("sms_second_lang"));
                // transferVO.setFirstApprovedOn(rs.getDate("first_approved_on"));
                // transferVO.setSecondApprovedOn(rs.getDate("second_approved_on"));
                // transferVO.setThirdApprovedOn(rs.getDate("third_approved_on"));
                // transferVO.setFirstApproverLimit(rs.getLong("first_approver_limit"));
                // transferVO.setSecondApprovalLimit(rs.getLong("second_approver_limit"));
                transferVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                /*
                 * if(!BTSLUtil.isNullString(transferVO.getRefTransferID()))
                 * alreadyReverseTrnItemsList.add(transferVO);
                 * else
                 * reverseTrnItemsList.add(transferVO);
                 */

                // added for cell ID and Switch ID
                transferVO.setCellId(rs.getString("cell_id"));
                transferVO.setSwitchId(rs.getString("switch_id"));
                returnList.add(transferVO);
            }
            // returnList.add(alreadyReverseTrnItemsList);
            // returnList.add(reverseTrnItemsList);

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferWebDAO[loadReversalChnlToChnlTransfersApprovalList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadReversalChnlToChnlTransfersList", "error.general.sql.processing");
        }

        catch (Exception ex) {
            _log.error("", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferWebDAO[loadReversalChnlToChnlTransfersApprovalList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:  arrayList Size =" + returnList.size());
            }
        }
        return returnList;
    }

    /**
     * Load the enquiry Channel Transfer List
     * 
     * @param p_con
     * @param p_transferID
     * @param p_userID
     * @param p_fromDate
     * @param p_toDate
     * @param p_status
     * @param p_userCode
     *            TODO
     * @param p_type
     *            TODO
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadO2CChannelTransfersList(Connection p_con, String p_transferID, String p_userID, Date p_fromDate, Date p_toDate, String p_status, String p_transferTypeCode, String p_productType, String p_transferCategory, String p_userCode) throws BTSLBaseException {

        final String methodName = "loadO2CChannelTransfersList";
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                "Entered  TransferNumber: " + p_transferID + " UserID: " + p_userID + " FromDate:" + p_fromDate + " ToDate:" + p_toDate + " Status:" + p_status + ",p_transferTypeCode=" + p_transferTypeCode + ", Product Type:" + p_productType + ",p_transferCategory=" + p_transferCategory + ", p_userCode=" + p_userCode);
        }
        Boolean isSecondaryNumberAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String isPrimary = null;
        if (isSecondaryNumberAllowed) {
            if (BTSLUtil.isNullString(p_transferID) && (!BTSLUtil.isNullString(p_userCode))) {
                final UserDAO userDAO = new UserDAO();
                UserPhoneVO userPhoneVO = null;
                userPhoneVO = userDAO.loadUserAnyPhoneVO(p_con, p_userCode);
                if (userPhoneVO != null) {
                    isPrimary = userPhoneVO.getPrimaryNumber();
                }
            }
        }

        ChannelTransferWebQry channelTransferWebQry = (ChannelTransferWebQry) ObjectProducer.getObject(QueryConstants.CHN_TRNSFR_QRY, QueryConstants.QUERY_PRODUCER);
       String strBuff = channelTransferWebQry.loadO2CChannelTransfersListQry(isPrimary, p_transferID, p_userCode, p_transferTypeCode, p_transferCategory);
        final ArrayList enquiryItemsList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(strBuff.toString());
            int m = 0;
            if (!BTSLUtil.isNullString(p_transferID)) {
                ++m;
                pstmt.setString(m, p_transferID);
                ++m;
                pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                ++m;
                pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                ++m;
                pstmt.setString(m, p_transferTypeCode);
            } else if (!BTSLUtil.isNullString(p_userCode)) {
                ++m;
                pstmt.setString(m, p_userCode);
                if (p_fromDate != null) {
                    ++m;
                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                } else {
                    ++m;
                    pstmt.setDate(m, null);
                }
                if (p_toDate != null) {
                    ++m;
                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                } else {
                    ++m;
                    pstmt.setDate(m, null);
                }
                ++m;
                pstmt.setString(m, p_transferCategory);
                ++m;
                pstmt.setString(m, p_transferTypeCode);
            } else {
                if (p_fromDate != null) {
                    ++m;
                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                } else {
                    ++m;
                    pstmt.setDate(m, null);
                }
                if (p_toDate != null) {
                    ++m;
                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                } else {
                    ++m;
                    pstmt.setDate(m, null);
                }
                ++m;
                pstmt.setString(m, p_productType);
                ++m;
                pstmt.setString(m, p_transferCategory);
                if (!PretupsI.ALL.equals(p_transferTypeCode) && PretupsI.TRANSFER_CATEGORY_SALE.equals(p_transferCategory)) {
                    ++m;
                    pstmt.setString(m, p_transferTypeCode);
                }
                ++m;
                pstmt.setString(m, p_userID);

                if (PretupsI.ALL.equals(p_transferTypeCode)) {
                    ++m;
                    pstmt.setString(m, p_userID);
                }
            }
            ++m;
            pstmt.setString(m, p_status);
            ++m;
            pstmt.setString(m, PretupsI.TRANSFER_TYPE);
            rs = pstmt.executeQuery();

            ChannelTransferVO transferVO = null;
            while (rs.next()) {
                transferVO = new ChannelTransferVO();
                transferVO.setTransferSubTypeValue(rs.getString("lookup_name"));
                transferVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setTransferType(rs.getString("transfer_type"));
                transferVO.setTransferSubType(rs.getString("transfer_sub_type"));
                transferVO.setNetworkCode(rs.getString("network_code"));
                transferVO.setNetworkCodeFor(rs.getString("network_code_for"));
                transferVO.setToUserName(rs.getString("user_name"));
                transferVO.setTransferDate(rs.getDate("transfer_date"));
                if (transferVO.getTransferDate() != null) {
                    transferVO.setTransferDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getTransferDate())));
                }
                transferVO.setFirstApprovedBy(rs.getString("first_approved_by"));
                transferVO.setFirstApprovedOn(rs.getDate("first_approved_on"));
                transferVO.setSecondApprovedBy(rs.getString("second_approved_by"));
                transferVO.setSecondApprovedOn(rs.getDate("second_approved_on"));
                transferVO.setThirdApprovedBy(rs.getString("third_approved_by"));
                transferVO.setThirdApprovedOn(rs.getDate("third_approved_on"));
                transferVO.setCanceledBy(rs.getString("cancelled_by"));
                transferVO.setCanceledOn(rs.getDate("cancelled_on"));

                if (!BTSLUtil.isNullString(rs.getString("from_msisdn"))) {
                    transferVO.setUserMsisdn(rs.getString("from_msisdn"));
                } else if (!BTSLUtil.isNullString(rs.getString("to_msisdn"))) {
                    transferVO.setUserMsisdn(rs.getString("to_msisdn"));
                } else {
                    transferVO.setUserMsisdn(rs.getString("msisdn"));
                }
                transferVO.setTransferCategory(rs.getString("transfer_category"));
                transferVO.setPayableAmount(rs.getLong("payable_amount"));
                transferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
                transferVO.setStatus(rs.getString("status"));
                transferVO.setFirstApprovedByName(rs.getString("firstapprovedby"));
                transferVO.setSecondApprovedByName(rs.getString("secondapprovedby"));
                transferVO.setThirdApprovedByName(rs.getString("thirdapprovedby"));
                transferVO.setCanceledByApprovedName(rs.getString("cancelledby"));
                transferVO.setGraphicalDomainCode(rs.getString("grph_domain_code"));
                transferVO.setGrphDomainCodeDesc(rs.getString("grph_domain_name"));
                transferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
                transferVO.setExternalTxnDate(rs.getDate("ext_txn_date"));
                transferVO.setFromUserID(rs.getString("from_user_id"));
                transferVO.setToUserID(rs.getString("to_user_id"));
                transferVO.setDomainCode(rs.getString("domain_code"));
                // added by amit for o2c transfer quantity change
                transferVO.setLevelOneApprovedQuantity(rs.getString("first_level_approved_quantity"));
                transferVO.setLevelTwoApprovedQuantity(rs.getString("second_level_approved_quantity"));
                transferVO.setLevelThreeApprovedQuantity(rs.getString("third_level_approved_quantity"));
                transferVO.setStatusDesc(((LookupsVO) LookupsCache.getObject(PretupsI.CHANNEL_TRANSFER_ORDER_STATUS, transferVO.getStatus())).getLookupName());
                // Added By Babu Kunwar For displaying Balnce in O2C Enquiry
                transferVO.setSenderPostStock(rs.getString("SENDER_POST_STOCK"));
                transferVO.setSenderPreviousStock(rs.getLong("SENDER_PREVIOUS_STOCK"));
                transferVO.setReceiverPostStock(rs.getString("RECEIVER_POST_STOCK"));
                transferVO.setReceiverPreviousStock(rs.getLong("RECEIVER_PREVIOUS_STOCK"));
                transferVO.setTransactionMode(rs.getString("transaction_mode"));
                if (transferVO.getThirdApprovedBy() != null) {
                    transferVO.setFinalApprovedBy(transferVO.getThirdApprovedByName());
                    if (transferVO.getThirdApprovedOn() != null) {
                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getThirdApprovedOn())));
                    }
                } else if (transferVO.getSecondApprovedBy() != null) {
                    transferVO.setFinalApprovedBy(transferVO.getSecondApprovedByName());
                    if (transferVO.getSecondApprovedOn() != null) {
                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getSecondApprovedOn())));
                    }
                } else if (transferVO.getFirstApprovedBy() != null) {
                    transferVO.setFinalApprovedBy(transferVO.getFirstApprovedByName());
                    if (transferVO.getFirstApprovedOn() != null) {
                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getFirstApprovedOn())));
                    }
                }

                if (transferVO.getCanceledBy() != null) {
                    transferVO.setFinalApprovedBy(transferVO.getCanceledByApprovedName());
                    if (transferVO.getCanceledOn() != null) {
                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getCanceledOn())));
                    }
                }
                enquiryItemsList.add(transferVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[loadO2CChannelTransfersList]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[loadO2CChannelTransfersList]",
                "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:  arrayList Size =" + enquiryItemsList.size());
            }
        }
        return enquiryItemsList;
    }

    /**
     * Method: loadlistOfUnusedBatches
     * This method loads detail of unused batches
     * 
     * @author gaurav.pandey
     * @param p_con
     *            java.sql.Connection
     * @param p_productId
     *            String
     * @return totalQuantity int
     * @throws BTSLBaseException
     */
    public ArrayList loadlistOfUnusedBatches(Connection p_con, String p_productId, String Serial_no, String finalSerialNO) throws BTSLBaseException {
        final String methodName = "loadlistOfUnusedBatches";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_productId=" + p_productId);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // VomsBatchVO vomsBatchVO= new VomsBatchVO();
        final VomsVoucherVO vomsVoucherVO = new VomsVoucherVO();
        final ArrayList list = new ArrayList();
        final StringBuffer strBuff = new StringBuffer("SELECT PRODUCT_ID , TOTAL_NO_OF_VOUCHERS , BATCH_NO, FROM_SERIAL_NO, TO_SERIAL_NO FROM VOMS_BATCHES ");

        strBuff.append("WHERE PRODUCT_ID=? AND BATCH_TYPE NOT IN ('EN','GE','PA','WH')AND STATUS =? AND FROM_SERIAL_NO BETWEEN ? AND ? ");

        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("loadSumForProductId", "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(1, p_productId);
            pstmt.setString(2, VOMSI.EXECUTED);
            pstmt.setString(3, Serial_no);
            pstmt.setString(4, finalSerialNO);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                vomsVoucherVO.setProductID("PRODUCT_ID");
                vomsVoucherVO.setBatchNo(rs.getString("BATCH_NO"));
                vomsVoucherVO.set_totalVouchers(rs.getLong("TOTAL_NO_OF_VOUCHERS"));
                vomsVoucherVO.setToSerialNo(rs.getString("TO_SERIAL_NO"));
                vomsVoucherVO.set_fromSerialNo(rs.getString("FROM_SERIAL_NO"));
                list.add(vomsVoucherVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[loadlistOfUnusedBatches]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[loadlistOfUnusedBatches]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
                _log.error(methodName, " Exception : in closing Rssultset" + ex);
            }
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (Exception ex) {
                _log.error(methodName, " Exception : in closing preparedstatement for Update" + ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: list size is  =" + list.size());
            }
        }
        return list;

    }

    /**
     * Method: insertVomsBatches
     * This method loads detail of unused batches
     * 
     * @author gaurav.pandey
     * @param p_con
     *            java.sql.Connection
     * @param p_productId
     *            String
     * @return totalQuantity int
     * @throws BTSLBaseException
     */
    public int insertVomsBatches(Connection p_con, VomsBatchVO vomsBatchVO) throws BTSLBaseException {
        final String methodName = "insertVomsBatches";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }

        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        int insert_count = 0;
        ResultSet rs = null;
        String Refrence_NO = null;
        String Refrence_type = null;
        int seq_id=0;

        // VomsBatchVO vomsBatchVO= new VomsBatchVO();
        Boolean isSequenceIDEnable = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE);
        final StringBuilder str = new StringBuilder( "SELECT BATCH_NO,BATCH_TYPE "); // gaurav
        if(isSequenceIDEnable){
        str.append(" , Sequence_id ");
        }
        str.append(" FROM VOMS_BATCHES WHERE PRODUCT_ID=? AND BATCH_TYPE='WH' AND FROM_SERIAL_NO<= ? AND TO_SERIAL_NO >=?");
        final StringBuffer strBuff = new StringBuffer(" INSERT INTO  VOMS_BATCHES (BATCH_NO,PRODUCT_ID,BATCH_TYPE,REFERENCE_NO,REFERENCE_TYPE,");
        strBuff.append("TOTAL_NO_OF_VOUCHERS,FROM_SERIAL_NO,");
        strBuff.append("TO_SERIAL_NO,TOTAL_NO_OF_FAILURE,");
        strBuff.append("TOTAL_NO_OF_SUCCESS,NETWORK_CODE,CREATED_DATE,CREATED_BY,");
        strBuff.append("MODIFIED_DATE,MODIFIED_BY,STATUS,CREATED_ON,MODIFIED_ON,PROCESS,");
        strBuff.append("MESSAGE ,user_id,EXT_TXN_NO,VOUCHER_SEGMENT ");
        if(isSequenceIDEnable){
            strBuff.append(" , Sequence_id ");
            }
        strBuff.append(" )");
        strBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ");
        if(isSequenceIDEnable){
        	strBuff.append(" , ?");
        }
        strBuff.append(" )");
 // gaurav

        final String sqlSelect = strBuff.toString();
        int i =1;
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {

            pstmt1 = p_con.prepareStatement(str.toString());
            pstmt1.setString(1, vomsBatchVO.getProductID());
            pstmt1.setString(2, vomsBatchVO.getFromSerialNo());
            pstmt1.setString(3, vomsBatchVO.getToSerialNo());
            rs = pstmt1.executeQuery();
            if (rs.next()) {
                Refrence_NO = rs.getString("BATCH_NO");
                Refrence_type = rs.getString("BATCH_TYPE");
                if(isSequenceIDEnable){
                seq_id = rs.getInt("sequence_id");
                }
            }
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(i++, vomsBatchVO.getBatchNo());
            pstmt.setString(i++, vomsBatchVO.getProductID());
            pstmt.setString(i++, vomsBatchVO.getBatchType());
            pstmt.setString(i++, Refrence_NO);
            pstmt.setString(i++, Refrence_type);

            pstmt.setLong(i++, Long.parseLong(vomsBatchVO.getQuantity()));
            pstmt.setString(i++, vomsBatchVO.getFromSerialNo());
            pstmt.setString(i++, vomsBatchVO.getToSerialNo());
            pstmt.setInt(i++, vomsBatchVO.getFailureCount());
            pstmt.setLong(i++, vomsBatchVO.getSuccessCount());
            pstmt.setString(i++, vomsBatchVO.get_NetworkCode());
            pstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(vomsBatchVO.getCreatedDate()));
            pstmt.setString(i++, vomsBatchVO.getCreatedBy());
            pstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(vomsBatchVO.getModifiedOn()));
            pstmt.setString(i++, vomsBatchVO.getModifiedBy());
            pstmt.setString(i++, VOMSI.UNDERPROCESS);
            pstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(vomsBatchVO.getCreatedDate()));
            pstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(vomsBatchVO.getModifiedOn()));
            pstmt.setString(i++, VOMSI.BATCH_PROCESS_ENABLE);
            pstmt.setString(i++, "Enabled successfuly");
            pstmt.setString(i++, vomsBatchVO.getToUserID());
            pstmt.setString(i++, vomsBatchVO.getExtTxnNo());
            pstmt.setString(i++, vomsBatchVO.getSegment());
            if(isSequenceIDEnable){
            	 pstmt.setInt(i++, seq_id);
            }

            insert_count = pstmt.executeUpdate();

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[insertVomsBatches]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadlistOfUnusedBatches", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[insertVomsBatches]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadlistOfUnusedBatches", "error.general.processing");
        } finally {
        	try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try{
                if (pstmt!= null){
               	 pstmt.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
            try{
                if (pstmt1!= null){
               	 pstmt1.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: insert count  =" + insert_count);
            }
        }
        return insert_count;

    }

    /**
     * Method loadUserInterfaceDetails() is used to load interface details
     * for XML Authentication
     * 
     * @param p_con
     * @param p_type
     * @throws BTSLBaseException
     * @author akanksha.gupta
     */
    public TransferItemVO loadUserInterfaceDetails(Connection p_con, String p_type) throws BTSLBaseException {
        final String methodName = "loadUserInterfaceDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "p_type ::" + p_type);
        }
        TransferItemVO transferItemVO = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT i.external_id, i.status, i.message_language1, i.message_language2,");
        strBuff.append(" i.status_type statustype, i.single_state_transaction, ");
        strBuff.append(" im.handler_class, im.underprocess_msg_reqd ,");
        strBuff.append(" im.interface_type_id,i.INTERFACE_ID ");
        strBuff.append(" FROM interfaces i, interface_types im ");
        strBuff.append(" WHERE i.interface_type_id = ? and i.interface_type_id = im.interface_type_id AND i.status <> 'N'");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            if (!BTSLUtil.isNullString(p_type)) {
                pstmt.setString(1, p_type);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                transferItemVO = TransferItemVO.getInstance();
                transferItemVO.setInterfaceID(rs.getString("INTERFACE_ID"));
                transferItemVO.setInterfaceHandlerClass(rs.getString("handler_class"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[loadUserInterfaceDetails]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[loadUserInterfaceDetails]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: ************* ");
            }
        }
        return transferItemVO;
    }

    /**
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @param isOrderApproved
     * @return
     * @throws BTSLBaseException
     * @author akanksha.gupta
     */
    public int addChannelTransferAutoApproved(Connection p_con, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {
        final String methodName = "addChannelTransferAutoApproved";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered subscriberVO : " + p_channelTransferVO);
        }
        Boolean isMultipleWalletApply = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY);
        // commented for DB2 OraclePreparedStatement psmt = null;
        PreparedStatement psmt = null;
        int updateCount = 0;
        try {

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Entered ChannelTransferVO : " + p_channelTransferVO);
            }
            // commented for DB2 OraclePreparedStatement psmt = null;
            final StringBuffer strBuff = new StringBuffer(" INSERT INTO  channel_transfers ( transfer_id, network_code, network_code_for, grph_domain_code, ");
            strBuff.append(" domain_code, sender_category_code, sender_grade_code, receiver_grade_code, from_user_id, ");
            strBuff.append(" to_user_id, transfer_date, reference_no, ext_txn_no, ext_txn_date, commission_profile_set_id, ");
            strBuff.append(" commission_profile_ver, requested_quantity, channel_user_remarks,  ");
            strBuff.append(" created_on, created_by, modified_by, modified_on, status, transfer_type, transfer_initiated_by, transfer_mrp, ");
            strBuff.append(" payable_amount, net_payable_amount, pmt_inst_type, pmt_inst_no, pmt_inst_date, ");
            strBuff.append(" pmt_inst_amount, sender_txn_profile, receiver_txn_profile, total_tax1, total_tax2, ");
            strBuff.append(" total_tax3, source, receiver_category_code , product_type , transfer_category ,");
            strBuff.append(" first_approver_limit, second_approver_limit,pmt_inst_source,  ");
            strBuff.append(" type,transfer_sub_type,close_date,control_transfer,request_gateway_code, request_gateway_type, ");
            strBuff.append(" msisdn,to_msisdn,to_grph_domain_code,to_domain_code,  ");
            strBuff
                .append(" first_approved_by, first_approved_on, second_approved_by, second_approved_on, third_approved_by, third_approved_on,sms_default_lang,sms_second_lang,active_user_id,TRANSACTION_MODE,first_approver_remarks ");
            strBuff.append(",cell_id,switch_id,first_level_approved_quantity");
            if (isMultipleWalletApply) {
                strBuff.append(",TXN_WALLET");
                strBuff.append(") VALUES ");
                strBuff
                    .append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            } else {
                strBuff.append(") VALUES ");
                strBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            }

            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query:" + query);
            }

            // commented for DB2 psmt = (OraclePreparedStatement)
            // p_con.prepareStatement(query);
            psmt = p_con.prepareStatement(query);
            int i = 0;
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferID());
            ++i;
            psmt.setString(i, p_channelTransferVO.getNetworkCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getNetworkCodeFor());
            ++i;
            psmt.setString(i, p_channelTransferVO.getGraphicalDomainCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getDomainCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getCategoryCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getSenderGradeCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getReceiverGradeCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getFromUserID());
            ++i;
            psmt.setString(i, p_channelTransferVO.getToUserID());
            ++i;
            psmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_channelTransferVO.getTransferDate()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getReferenceNum());
            ++i;
            psmt.setString(i, p_channelTransferVO.getExternalTxnNum());
            ++i;
            psmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_channelTransferVO.getExternalTxnDate()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getCommProfileSetId());
            ++i;
            psmt.setString(i, p_channelTransferVO.getCommProfileVersion());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getRequestedQuantity());
            // for multilanguage support
            // commented for DB2psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getChannelRemarks());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getCreatedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getCreatedBy());
            ++i;
            psmt.setString(i, p_channelTransferVO.getModifiedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getStatus());
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferInitatedBy());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTransferMRP());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getNetPayableAmount());
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getPayInstrumentDate()));
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayInstrumentAmt());
            ++i;
            psmt.setString(i, p_channelTransferVO.getSenderTxnProfile());
            ++i;
            psmt.setString(i, p_channelTransferVO.getReceiverTxnProfile());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTotalTax1());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTotalTax2());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTotalTax3());
            ++i;
            psmt.setString(i, p_channelTransferVO.getSource());
            ++i;
            psmt.setString(i, p_channelTransferVO.getReceiverCategoryCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getProductType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferCategory());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getFirstApproverLimit());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getSecondApprovalLimit());
            // for multilanguage support
            // commented for DB2psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getPaymentInstSource());
            ++i;
            psmt.setString(i, p_channelTransferVO.getType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferSubType());
            if (PretupsI.CHANNEL_TYPE_O2C.equals(p_channelTransferVO.getType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER
                .equals(p_channelTransferVO.getTransferSubType()) && PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(p_channelTransferVO.getStatus())) {
                ++i;
                psmt.setTimestamp(i, null);
            } else {
                ++i;
                psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getCreatedOn()));
            }
            ++i;
            psmt.setString(i, p_channelTransferVO.getControlTransfer());
            ++i;
            psmt.setString(i, p_channelTransferVO.getRequestGatewayCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getRequestGatewayType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getFromUserCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getToUserCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getReceiverGgraphicalDomainCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getReceiverDomainCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getFirstApprovedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getFirstApprovedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getSecondApprovedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getSecondApprovedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getThirdApprovedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getThirdApprovedOn()));

            // commented for DB2psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getDefaultLang());
            // commented for DB2psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getSecondLang());
            ++i;
            psmt.setString(i, p_channelTransferVO.getActiveUserId());
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransactionMode());
            // added for cell id and switch id.
            ++i;
            psmt.setString(i, PretupsI.O2C_APPROVED_BY);
            ++i;
            psmt.setString(i, p_channelTransferVO.getCellId());
            ++i;
            psmt.setString(i, p_channelTransferVO.getSwitchId());
            ++i;
            psmt.setString(i, p_channelTransferVO.getLevelOneApprovedQuantity());

            if (isMultipleWalletApply) {
                ++i;
                psmt.setString(i, p_channelTransferVO.getWalletType());
            }

            updateCount = psmt.executeUpdate();
            updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
            if (updateCount <= 0) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                    "ChannelTransferWebDAO[addChannelTransferAutoApproved]", "", "", "", "BTSLBaseException: update count <=0");
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            // add the items in item list
            addTransferItemsWithApproval(p_con, p_channelTransferVO.getChannelTransferitemsVOList(), p_channelTransferVO.getTransferID(), p_channelTransferVO.getCreatedOn(),
                p_channelTransferVO.getTransferType(), p_channelTransferVO.getType());

        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferWebDAO[addChannelTransferAutoApproved]", "", "", "", "SQL Exception:" + sqle.getMessage());
            if (sqle.getErrorCode() == 1) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.O2C_EXTGW_DUPLICATE_TRANSCATION);
            } else {
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
        }// end of catch
        catch (BTSLBaseException bbe) {
            _log.error(methodName, "Exception " + bbe.getMessage());
            _log.errorTrace(methodName, bbe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferWebDAO[addChannelTransferAutoApproved]", "", "", "", "BTSL Exception :" + bbe.getMessage());

            throw new BTSLBaseException(this, methodName, bbe.getMessage());
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferWebDAO[addChannelTransferAutoApproved]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, e.getMessage());
        }// end of catch
        finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting Success :" + updateCount);
            }
        }// end of finally

        return updateCount;

    }

    /**
     * Add the transfer items
     * 
     * @param p_con
     * @param p_transferItemList
     * @param p_transferId
     * @param p_transferDate
     * @param p_transferType
     * @return int
     * @throws BTSLBaseException
     * @author akanksha.gupta
     */
    private int addTransferItemsWithApproval(Connection p_con, ArrayList p_transferItemList, String p_transferId, Date p_transferDate, String p_transferType, String p_type) throws BTSLBaseException {

        final String methodName = "addTransferItemsWithApproval";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered TransferItemList Size: " + p_transferItemList.size() + " TransferId : " + p_transferId + "?p_transferDate=" + p_transferDate);
        }

        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer(" INSERT INTO channel_transfers_items ( ");
            strBuff.append(" s_no,transfer_id,product_code,required_quantity,approved_quantity,user_unit_price, ");
            strBuff.append(" commission_profile_detail_id,commission_type, commission_rate, commission_value, ");
            strBuff.append(" tax1_type, tax1_rate, tax1_value, tax2_type,tax2_rate, tax2_value , tax3_type, ");
            strBuff.append(" tax3_rate, tax3_value, payable_amount, net_payable_amount,mrp,");
            strBuff
                .append(" sender_previous_stock, receiver_previous_stock,transfer_date,sender_post_stock, receiver_post_stock, sender_debit_quantity,receiver_credit_quantity,commision_quantity ) ");
            strBuff.append(" VALUES  ");
            strBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            final String query = strBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query:" + query);
            }

            psmt = p_con.prepareStatement(query);
            ChannelTransferItemsVO transferItemsVO = null;
            for (int i = 0, k = p_transferItemList.size(); i < k; i++) {
                transferItemsVO = (ChannelTransferItemsVO) p_transferItemList.get(i);

                psmt.clearParameters();
                int m = 0;
                ++m;
                psmt.setInt(m, (i + 1));
                ++m;
                psmt.setString(m, p_transferId);
                ++m;
                psmt.setString(m, transferItemsVO.getProductCode());
                ++m;
                psmt.setLong(m, transferItemsVO.getRequiredQuantity());
                ++m;
                psmt.setLong(m, transferItemsVO.getApprovedQuantity());
                ++m;
                psmt.setLong(m, transferItemsVO.getUnitValue());
                ++m;
                psmt.setString(m, transferItemsVO.getCommProfileDetailID());
                ++m;
                psmt.setString(m, transferItemsVO.getCommType());
                ++m;
                psmt.setDouble(m, transferItemsVO.getCommRate());
                ++m;
                psmt.setLong(m, transferItemsVO.getCommValue());
                ++m;
                psmt.setString(m, transferItemsVO.getTax1Type());
                ++m;
                psmt.setDouble(m, transferItemsVO.getTax1Rate());
                ++m;
                psmt.setLong(m, transferItemsVO.getTax1Value());
                ++m;
                psmt.setString(m, transferItemsVO.getTax2Type());
                ++m;
                psmt.setDouble(m, transferItemsVO.getTax2Rate());
                ++m;
                psmt.setLong(m, transferItemsVO.getTax2Value());
                ++m;
                psmt.setString(m, transferItemsVO.getTax3Type());
                ++m;
                psmt.setDouble(m, transferItemsVO.getTax3Rate());
                ++m;
                psmt.setLong(m, transferItemsVO.getTax3Value());
                ++m;
                psmt.setLong(m, transferItemsVO.getPayableAmount());
                ++m;
                psmt.setLong(m, transferItemsVO.getNetPayableAmount());
                ++m;
                psmt.setLong(m, transferItemsVO.getProductTotalMRP());
                ++m;
                psmt.setLong(m, transferItemsVO.getAfterTransSenderPreviousStock());
                ++m;
                psmt.setLong(m, transferItemsVO.getAfterTransReceiverPreviousStock());
                ++m;
                psmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(p_transferDate));
                ++m;
                psmt.setLong(m, transferItemsVO.getAfterTransSenderPreviousStock() - transferItemsVO.getSenderDebitQty());
                ++m;
                psmt.setLong(m, transferItemsVO.getAfterTransReceiverPreviousStock() + transferItemsVO.getReceiverCreditQty());
                ++m;
                psmt.setLong(m, transferItemsVO.getSenderDebitQty());
                ++m;
                psmt.setLong(m, transferItemsVO.getReceiverCreditQty());
                ++m;
                psmt.setLong(m, transferItemsVO.getCommQuantity());
                updateCount = psmt.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres

                if (updateCount <= 0) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                        "ChannelTransferWebDAO[addTransferItemsWithApproval]", "", "", "", "BTSLBaseException: update count <=0");
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[addTransferItemsWithApproval]",
                "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (BTSLBaseException bbe) {
            _log.error(methodName, "BTSLBaseException " + bbe.getMessage());
            _log.errorTrace(methodName, bbe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[addTransferItemsWithApproval]",
                "", "", "", "BTSLBaseException :" + bbe.getMessage());

            throw new BTSLBaseException(this, methodName, bbe.getMessage());
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[addTransferItemsWithApproval]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, e.getMessage());
        }// end of catch
        finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting Success :" + updateCount);
            }
        }// end of finally

        return updateCount;
    }

    public boolean validateSerialNo(Connection p_con, String Serial_no, String productId,String vomsType) throws BTSLBaseException {
        final String methodName = "validateSerialNo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_productId=" + productId);
        }

        PreparedStatement pstmt = null;

        ResultSet rs = null;

        PreparedStatement pstmt1 = null;

        ResultSet rs1 = null;
        boolean isvalid = false;

        final StringBuilder sqlSelect = new StringBuilder("select batch_no from voms_batches where product_id=? and from_serial_no<=? and to_serial_no>=? ");
        if(VOMSI.VOUCHER_TYPE_DIGITAL.equals(vomsType) || VOMSI.VOUCHER_TYPE_TEST_DIGITAL.equals(vomsType) || VOMSI.VOUCHER_TYPE_PHYSICAL.equals(vomsType))
        	sqlSelect.append(" and batch_type='GE'");
        else
        	sqlSelect.append(" and batch_type='WH'");
        final String sqlSelect1 = "select batch_no from voms_batches where product_id=? and from_serial_no<=? and to_serial_no>=? and batch_type in ('EN','PA','UP') and status <> ?";

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect.toString());
            pstmt.setString(1, productId);
            pstmt.setString(2, Serial_no);
            pstmt.setString(3, Serial_no);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                isvalid = true;
            }
            if (isvalid) {
                pstmt1 = p_con.prepareStatement(sqlSelect1);
                pstmt1.setString(1, productId);

                // pstmt.setString(2,VOMSI.BATCH_O2C_TRANSFER);

                pstmt1.setString(2, Serial_no);
                pstmt1.setString(3, Serial_no);
                pstmt1.setString(4, VOMSI.BATCHFAILEDSTATUS);
                rs1 = pstmt1.executeQuery();

                if (rs1.next()) {
                    isvalid = false;
                } else {
                    isvalid = true;
                }

            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[validateSerialNo]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[validateSerialNo]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt1 != null) {
                    pstmt1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: is valid =" + isvalid);
            }
        }
        return isvalid;

    }
 
    public boolean validateVoucherChangeStatus(Connection p_con, String serial_no) throws BTSLBaseException {
        final String methodName = "validateVoucherChangeStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: serial_no=" + serial_no );
        }

        PreparedStatement pstmt = null;

        ResultSet rs = null;


        boolean isUsed = false;

        final String sqlSelect = " select ct.transfer_id from channel_transfers ct, channel_voucher_items cvi " +
                                 " where cvi.from_serial_no <=? and cvi.to_serial_no >=? "+ 
                                 " and ct.transfer_id= cvi.transfer_id and ct.status NOT IN ('CLOSE','CNCL')";
 

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(1, serial_no);
            pstmt.setString(2, serial_no);
            rs = pstmt.executeQuery();

            while (rs.next()) {
            	isUsed = true;
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[validateVoucherChangeStatus]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[validateVoucherChangeStatus]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: isUsed =" + isUsed);
            }
        }
        return isUsed;

    }    

    public boolean validateVoucherSerialNo(Connection p_con, String serial_no, String transfer_id, String type) throws BTSLBaseException {
        final String methodName = "validateVoucherSerialNo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: serial_no=" + serial_no + "Entered: transfer_id=" +  transfer_id );
        }

        PreparedStatement pstmt = null;

        ResultSet rs = null;

        PreparedStatement pstmt1 = null;

        ResultSet rs1 = null;
        boolean isUsed = false;

        final StringBuilder sqlSelect = new StringBuilder("select cvi.transfer_id from CHANNEL_VOUCHER_ITEMS cvi,CHANNEL_TRANSFERS ct where ct.transfer_sub_type='V' ");
        sqlSelect.append("and ct.status!='CNCL' and (cvi.from_serial_no <= ? and cvi.to_serial_no >= ?) ");
        sqlSelect.append("and ct.TRANSFER_ID = cvi.TRANSFER_ID and cvi.TRANSFER_ID!= ? and cvi.type = ?");  //or (cvi.from_serial_no <= ts and cvi.to_serial_no >= ts)

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect.toString());
            int i = 1;
            pstmt.setString(i++, serial_no);
            pstmt.setString(i++, serial_no);
            pstmt.setString(i++, transfer_id);
            pstmt.setString(i++, type);
            rs = pstmt.executeQuery();

            while (rs.next()) {
            	transfer_id = rs.getString("transfer_id");
            	isUsed = true;
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[validateSerialNo]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[validateSerialNo]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt1 != null) {
                    pstmt1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: isUsed =" + isUsed);
            }
        }
        return isUsed;

    }
    // Akanksha
    public ArrayList<VomsBatchVO> validateBatch(Connection p_con, VomsBatchVO p_vomsBatchVO) throws BTSLBaseException {
        final String methodName = "validateBatch";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final ArrayList<VomsBatchVO> usedBatches = new ArrayList<VomsBatchVO>();

        final String sqlSelect = "select from_serial_no , to_serial_no, batch_no from voms_batches where from_serial_no > ? and from_serial_no < ? and batch_type IN ('EN','PA')";

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(1, p_vomsBatchVO.getFromSerialNo());
            pstmt.setString(2, p_vomsBatchVO.getToSerialNo());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                p_vomsBatchVO =  VomsBatchVO.getInstance();
                p_vomsBatchVO.setUsedFromSerialNo(rs.getString("from_serial_no"));
                p_vomsBatchVO.setUsedToSerialNo(rs.getString("to_serial_no"));
                usedBatches.add(p_vomsBatchVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[validateSerialNo]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[validateSerialNo]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: ArrayList size =" + usedBatches.size());
            }
        }
        return usedBatches;

    }
    
    /***
     *updatChannelTransferItemsAfterReverseTrx
     * @param pcon
     * @param pchannelTransferVO
     * @return
     * @throws BTSLBaseException
     */
    
    public int updatChannelTransferItemsAfterReverseTrx(Connection pcon, ChannelTransferVO pchannelTransferVO) throws BTSLBaseException {
      
    	final String methodName = "updatChannelTransferItemsAfterReverseTrx";
        writeLogger(methodName,"Entered ChannelTransferVO : " + pchannelTransferVO);
         final String eventDisplay=  "ChannelTransferWebDAO[updatChannelTransferItemsAfterReverseTrx]";
        PreparedStatement psmt1 = null;
        int updateCount = 0;
        try {
             final StringBuilder strBuff1 = new StringBuilder(" update  channel_transfers_items set  REQUIRED_QUANTITY=? where transfer_id= ? ");
            final String queryItems = strBuff1.toString();
            writeLogger(methodName,"update query:" + queryItems);
            psmt1=pcon.prepareStatement(queryItems);
           int  i = 0;
            ++i;
            psmt1.setLong(i, pchannelTransferVO.getRequestedQuantity());
            ++i;
            psmt1.setString(i, pchannelTransferVO.getTransferID());
            updateCount = psmt1.executeUpdate();
           
            if (updateCount <= 0) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                		eventDisplay, "", "", "", "BTSLBaseException: update count <=0");
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            
        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
            		eventDisplay, "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
            		eventDisplay, "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            
            try{
                if (psmt1!= null){
                	psmt1.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
            writeLogger(methodName,"Exiting Success :" + updateCount);
               
            
        }// end of finally

        return updateCount;
    }
    
    private void writeLogger(String methodName, String content)
    {
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName, content);
        }
    }
    
    public int updatChannelTransferForStockUpdate(Connection p_con, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {
        final String methodName = "updatChannelTransferForStockUpdate";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ChannelTransferVO : " + p_channelTransferVO);
        }
        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer(" update  channel_transfers set  modified_by=?, modified_on=?, stock_updated= ?");
            strBuff.append(" where  transfer_id=? ");
            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("updatChannelTransfer", "update query:" + query);
            }

            psmt = p_con.prepareStatement(query);
            int i = 0;
            ++i;
            psmt.setString(i, p_channelTransferVO.getModifiedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));
            ++i;
            psmt.setString(i, TypesI.YES);
            ++i;
            psmt.setString(i, p_channelTransferVO.getRefTransferID());
            
            updateCount = psmt.executeUpdate();
            if (updateCount <= 0) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                    "ChannelTransferWebDAO[updatChannelTransferAfterReverseTrx]", "", "", "", "BTSLBaseException: update count <=0");
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
           
        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferWebDAO[updatChannelTransferAfterReverseTrx]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferWebDAO[updatChannelTransferAfterReverseTrx]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting Success :" + updateCount);
            }
        }// end of finally

        return updateCount;
    }
    
    
    /**
     * Method: insertVomsBatchesDownoad
     * This method loads detail of unused batches
     * 
     * @author gaurav.pandey
     * @param p_con
     *            java.sql.Connection
     * @param p_productId
     *            String
     * @return totalQuantity int
     * @throws BTSLBaseException
     */
    public int insertVomsBatchesDownoad(Connection p_con, VomsBatchVO vomsBatchVO) throws BTSLBaseException {
        final String methodName = "insertVomsBatchesDownoad";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }

        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        int insert_count = 0;
        ResultSet rs = null;
        String Refrence_NO = null;
        String Refrence_type = null;
        int seq_id=0;
        int i =1;
        // VomsBatchVO vomsBatchVO= new VomsBatchVO();
        Boolean isSequenceIDEnable = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE);
        final StringBuilder str = new StringBuilder( "SELECT BATCH_NO,BATCH_TYPE "); // gaurav
        if(isSequenceIDEnable){
        str.append(" , Sequence_id ");
        }
        str.append(" FROM VOMS_BATCHES WHERE PRODUCT_ID=? AND BATCH_TYPE='GE' AND FROM_SERIAL_NO<= ? AND TO_SERIAL_NO >=?");
        final StringBuffer strBuff = new StringBuffer(" INSERT INTO  VOMS_BATCHES (BATCH_NO,PRODUCT_ID,BATCH_TYPE,REFERENCE_NO,REFERENCE_TYPE,");
        strBuff.append("TOTAL_NO_OF_VOUCHERS,FROM_SERIAL_NO,");
        strBuff.append("TO_SERIAL_NO,TOTAL_NO_OF_FAILURE,");
        strBuff.append("TOTAL_NO_OF_SUCCESS,NETWORK_CODE,CREATED_DATE,CREATED_BY,");
        strBuff.append("MODIFIED_DATE,MODIFIED_BY,STATUS,CREATED_ON,MODIFIED_ON,PROCESS,");
        strBuff.append("MESSAGE,VOUCHER_SEGMENT  ");
        if(isSequenceIDEnable){
            strBuff.append(" , Sequence_id ");
            }
        strBuff.append(" )");
        strBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
        if(isSequenceIDEnable){
        	strBuff.append(" , ?");
        }
        strBuff.append(" )");
 // gaurav

        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {

            pstmt1 = p_con.prepareStatement(str.toString());
            pstmt1.setString(1, vomsBatchVO.getProductid());
            pstmt1.setString(2, vomsBatchVO.getFromSerialNo());
            pstmt1.setString(3, vomsBatchVO.getToSerialNo());
            rs = pstmt1.executeQuery();
            if (rs.next()) {
                Refrence_NO = rs.getString("BATCH_NO");
                Refrence_type = rs.getString("BATCH_TYPE");
                if(isSequenceIDEnable){
                seq_id = rs.getInt("sequence_id");
                }
            }
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(i++, vomsBatchVO.getBatchNo());
            pstmt.setString(i++, vomsBatchVO.getProductid());
            pstmt.setString(i++, vomsBatchVO.getBatchType());
            pstmt.setString(i++, vomsBatchVO.getReferenceNo());
            pstmt.setString(i++, vomsBatchVO.getReferenceType());

            pstmt.setLong(i++, Long.parseLong(vomsBatchVO.getQuantity()));
            pstmt.setString(i++, vomsBatchVO.getFromSerialNo());
            pstmt.setString(i++, vomsBatchVO.getToSerialNo());
            pstmt.setInt(i++, vomsBatchVO.getFailureCount());
            pstmt.setLong(i++, vomsBatchVO.getSuccessCount());
            pstmt.setString(i++, vomsBatchVO.get_NetworkCode());
            pstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(vomsBatchVO.getCreatedDate()));
            pstmt.setString(i++, vomsBatchVO.getCreatedBy());
            pstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(vomsBatchVO.getModifiedOn()));
            pstmt.setString(i++, vomsBatchVO.getModifiedBy());
            pstmt.setString(i++, VOMSI.EXECUTED);
            pstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(vomsBatchVO.getCreatedDate()));
            pstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(vomsBatchVO.getModifiedOn()));
            pstmt.setString(i++, VOMSI.BATCH_PROCESS_CHANGE);
            pstmt.setString(i++, "Batch SuccessFully Executed ...........");
            pstmt.setString(i++, vomsBatchVO.getSegment());
            if(isSequenceIDEnable){
            	 pstmt.setInt(i++, seq_id);
            }

            insert_count = pstmt.executeUpdate();

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[insertVomsBatchesDownoad]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadlistOfUnusedBatches", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[insertVomsBatchesDownoad]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadlistOfUnusedBatches", "error.general.processing");
        } finally {
        	 try {
                 if (rs != null)
                     rs.close();
             } catch (Exception e) {
                 _log.errorTrace(methodName, e);
             }
             try{
                 if (pstmt!= null){
                	 pstmt.close();
                 }
               }
               catch (SQLException e){
             	  _log.error("An error occurred closing statement.", e);
               }
             try{
                 if (pstmt1!= null){
                	 pstmt1.close();
                 }
               }
               catch (SQLException e){
             	  _log.error("An error occurred closing statement.", e);
               }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: insert count  =" + insert_count);
            }
        }
        return insert_count;

    }
    
    public ChannelTransferVO loadChannelTransferDetails(Connection p_con, String p_transferID) throws BTSLBaseException {

        final String methodName = "loadChannelTransferDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_transferID=");
        	loggerValue.append(p_transferID);
            _log.debug(methodName, loggerValue );
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelTransferVO transferVO = null;
        try {
            final StringBuffer selectQueryBuff = new StringBuffer();
            selectQueryBuff.append("SELECT status from channel_transfers ct ");
            selectQueryBuff.append("WHERE ct.transfer_id=? and ct.transfer_date=? ");

            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("select query:");
            	loggerValue.append(selectQuery);
                _log.debug(methodName,  loggerValue);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setString(i++, p_transferID);
            pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_transferID)));
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                transferVO = new ChannelTransferVO();
                transferVO.setStatus(rs.getString("status"));
            }

        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2STransferDetails]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error("loadC2STransferItemsVOList",  loggerValue );
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2STransferDetails]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting ");
            }
        }// end of finally
        return transferVO;
    }
    

    public boolean doesRangeContainMultipleProfiles(Connection p_con, String fromSerialNo, String toSerialNo, long quantity, String userId,String voucherType , String voucherSegment,String denomination,String networkCode) throws BTSLBaseException {
        final String methodName = "doesRangeContainMultipleProfiles";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            loggerValue.append("Entered: ");
            loggerValue.append("fromSerialNo=" + fromSerialNo);
            loggerValue.append("toSerialNo=" + toSerialNo);
            loggerValue.append("quantity=" + quantity);
            _log.debug(methodName, loggerValue.toString());
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;
        boolean doesRangeContainMultipleProfiles = false;
        String c2cAllowedVoucherList = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_ALLOWED_VOUCHER_LIST);
        String[] allowedVoucherStatus =null;
        allowedVoucherStatus = c2cAllowedVoucherList.split(",");  
        StringBuilder sqlLogger = new StringBuilder("SELECT product_id, count(1) count FROM VOMS_VOUCHERS WHERE VOUCHER_TYPE = ? AND VOUCHER_SEGMENT = ? ");
        sqlLogger.append(" AND SERIAL_NO>=? AND SERIAL_NO <=? AND USER_ID = ? AND USER_NETWORK_CODE = ? AND MRP = ?");
        if(allowedVoucherStatus != null && allowedVoucherStatus.length > 0) {
        	 sqlLogger.append(" AND STATUS IN ");
        	 sqlLogger.append(" (");
             for(int j = 0;j<allowedVoucherStatus.length-1;j++)
             {
            	 sqlLogger.append("?,");
             } 
        }
        sqlLogger.append("?) group by product_id ");
        	
        final String sqlSelect = sqlLogger.toString(); 

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
        	long count = 0L;
        	int i = 1;
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(i++, voucherType);
            pstmt.setString(i++, voucherSegment);
            pstmt.setString(i++, fromSerialNo);
            pstmt.setString(i++, toSerialNo);
            pstmt.setString(i++, userId);
            pstmt.setString(i++, networkCode);
            pstmt.setLong(i++, PretupsBL.getSystemAmount(denomination));

            if(allowedVoucherStatus != null && allowedVoucherStatus.length > 0) {
                for(int j = 0;j<allowedVoucherStatus.length;j++)
                {
                	 pstmt.setString(i++, allowedVoucherStatus[j]);
                }
           }
            else{
            	 pstmt.setString(i++, VOMSI.VOMS_ENABLE_STATUS);   
           }
            rs = pstmt.executeQuery();

            while (rs.next()) {
            	count ++;
            }
            if(count > 1) {
            	doesRangeContainMultipleProfiles = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[doesRangeContainsMultipleProfiles]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[doesRangeContainsMultipleProfiles]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt1 != null) {
                    pstmt1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: doesRangeContainMultipleProfiles =" + doesRangeContainMultipleProfiles);
            }
        }
        return doesRangeContainMultipleProfiles;

    }

    public boolean areAllVouchersAssociated(Connection p_con, String fromSerialNo, String toSerialNo, long quantity, String userId,String voucherType , String voucherSegment,String denomination,String networkCode) throws BTSLBaseException {
        final String methodName = "areAllVouchersAssociated";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            loggerValue.append("Entered: ");
            loggerValue.append("fromSerialNo=" + fromSerialNo);
            loggerValue.append("toSerialNo=" + toSerialNo);
            loggerValue.append("quantity=" + quantity);
            _log.debug(methodName, loggerValue.toString());
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;
        boolean areAllVouchersAssociated = false;
        String[] allowedVoucherStatus =null;
        String c2cAllowedVoucherList = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_ALLOWED_VOUCHER_LIST);
        allowedVoucherStatus = c2cAllowedVoucherList.split(",");  
        StringBuilder sqlLogger = new StringBuilder("SELECT count(1) count FROM VOMS_VOUCHERS WHERE VOUCHER_TYPE = ? AND VOUCHER_SEGMENT = ? ");
        sqlLogger.append(" AND SERIAL_NO>=? AND SERIAL_NO <=? AND USER_ID = ? AND USER_NETWORK_CODE = ? AND MRP = ?");
        if(allowedVoucherStatus != null && allowedVoucherStatus.length > 0) {
        	 sqlLogger.append(" AND STATUS IN ");
        	 sqlLogger.append(" (");
             for(int j = 0;j<allowedVoucherStatus.length-1;j++)
             {
            	 sqlLogger.append("?,");
             } 
        }
        sqlLogger.append("?) ");
        
        //to check if C2S recharge is already performed for voucher
        sqlLogger.append(" AND SOLD_STATUS NOT IN ('Y') ");
        final String sqlSelect = sqlLogger.toString(); 

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
        	int count = 0;
        	int i = 1;
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(i++, voucherType);
            pstmt.setString(i++, voucherSegment);
            pstmt.setString(i++, fromSerialNo);
            pstmt.setString(i++, toSerialNo);
            pstmt.setString(i++, userId);
            pstmt.setString(i++, networkCode);
            pstmt.setLong(i++, PretupsBL.getSystemAmount(denomination));

            if(allowedVoucherStatus != null && allowedVoucherStatus.length > 0) {
                for(int j = 0;j<allowedVoucherStatus.length;j++)
                {
                	 pstmt.setString(i++, allowedVoucherStatus[j]);
                }
           }
            else{
            	 pstmt.setString(i++, VOMSI.VOMS_ENABLE_STATUS);   
           }
            rs = pstmt.executeQuery();

            while (rs.next()) {
            	count = rs.getInt("count");
            }
            if(count > 0 && quantity == count) {
            	areAllVouchersAssociated = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[validateSerialNo]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[validateSerialNo]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt1 != null) {
                    pstmt1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: areAllVouchersAssociated =" + areAllVouchersAssociated);
            }
        }
        return areAllVouchersAssociated;

    }

    
    public boolean validateVoucherSerialNoC2C(Connection p_con, String serialNo, String transferID, String type) throws BTSLBaseException {
        final String methodName = "validateVoucherSerialNoC2C";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: serial_no=" + serialNo + "Entered: transfer_id=" +  transferID  + "Entered: type= " +type);
        }

        PreparedStatement pstmt = null;

        ResultSet rs = null;

        PreparedStatement pstmt1 = null;

        ResultSet rs1 = null;
        boolean isUsed = false;

        final StringBuilder sqlSelect = new StringBuilder("select cvi.transfer_id from CHANNEL_VOUCHER_ITEMS cvi,CHANNEL_TRANSFERS ct where ct.transfer_sub_type='V' ");
        sqlSelect.append("and ct.status not in ('CNCL','CLOSE') and (cvi.from_serial_no <= ? and cvi.to_serial_no >= ?) ");
        sqlSelect.append("and ct.TRANSFER_ID = cvi.TRANSFER_ID and cvi.TRANSFER_ID!= ? and cvi.type = ?");  //or (cvi.from_serial_no <= ts and cvi.to_serial_no >= ts)

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect.toString());
            int i = 1;
            pstmt.setString(i++, serialNo);
            pstmt.setString(i++, serialNo);
            pstmt.setString(i++, transferID);
            pstmt.setString(i++, type);
            rs = pstmt.executeQuery();

            while (rs.next()) {
            	transferID = rs.getString("transfer_id");
            	isUsed = true;
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[validateSerialNo]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[validateSerialNo]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt1 != null) {
                    pstmt1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: isUsed =" + isUsed);
            }
        }
        return isUsed;

    }
    
    
    public boolean validateVoucherSerialNoC2S(Connection p_con, String serialNo) throws BTSLBaseException {
        final String methodName = "validateVoucherSerialNoC2S";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: serial_no=" + serialNo);
        }

        PreparedStatement pstmt = null;

        ResultSet rs = null;

        PreparedStatement pstmt1 = null;

        ResultSet rs1 = null;
        boolean isUsed = false;

        final StringBuilder sqlSelect = new StringBuilder("select vv.sold_status from VOMS_VOUCHERS vv where vv.serial_no = ? ");
      
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect.toString());
            int i = 1;
            pstmt.setString(i++, serialNo);
            rs = pstmt.executeQuery();

            while (rs.next()) {
            	String soldStatus = rs.getString("sold_status");
            	if("Y".equalsIgnoreCase(soldStatus)) {
            		isUsed = true;
            	}
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[validateSerialNo]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[validateSerialNo]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt1 != null) {
                    pstmt1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: isUsed =" + isUsed);
            }
        }
        return isUsed;

    }
    
    
    public boolean doesRangeContainMultipleProfilesforO2c(Connection p_con, String fromSerialNo, String toSerialNo, long quantity,String voucherType , String voucherSegment,String denomination,String networkCode) throws BTSLBaseException {
        final String methodName = "doesRangeContainMultipleProfiles";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            loggerValue.append("Entered: ");
            loggerValue.append("fromSerialNo=" + fromSerialNo);
            loggerValue.append("toSerialNo=" + toSerialNo);
            loggerValue.append("quantity=" + quantity);
            _log.debug(methodName, loggerValue.toString());
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;
        boolean doesRangeContainMultipleProfiles = false;
        String c2cAllowedVoucherList = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_ALLOWED_VOUCHER_LIST);
        String[] allowedVoucherStatus =null;
        allowedVoucherStatus = c2cAllowedVoucherList.split(",");  
        StringBuilder sqlLogger = new StringBuilder("SELECT product_id, count(1) count FROM VOMS_VOUCHERS WHERE VOUCHER_TYPE = ? AND VOUCHER_SEGMENT = ? ");
        sqlLogger.append(" AND SERIAL_NO>=? AND SERIAL_NO <=? AND USER_NETWORK_CODE = ? AND MRP = ?");
        if(allowedVoucherStatus != null && allowedVoucherStatus.length > 0) {
        	 sqlLogger.append(" AND STATUS IN ");
        	 sqlLogger.append(" (");
             for(int j = 0;j<allowedVoucherStatus.length-1;j++)
             {
            	 sqlLogger.append("?,");
             } 
        }
        sqlLogger.append("?) group by product_id ");
        	
        final String sqlSelect = sqlLogger.toString(); 

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
        	long count = 0L;
        	int i = 1;
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(i++, voucherType);
            pstmt.setString(i++, voucherSegment);
            pstmt.setString(i++, fromSerialNo);
            pstmt.setString(i++, toSerialNo);
            pstmt.setString(i++, networkCode);
            pstmt.setLong(i++, PretupsBL.getSystemAmount(denomination));

            if(allowedVoucherStatus != null && allowedVoucherStatus.length > 0) {
                for(int j = 0;j<allowedVoucherStatus.length;j++)
                {
                	 pstmt.setString(i++, allowedVoucherStatus[j]);
                }
           }
            else{
            	 pstmt.setString(i++, VOMSI.VOMS_ENABLE_STATUS);   
           }
            rs = pstmt.executeQuery();

            while (rs.next()) {
            	count ++;
            }
            if(count > 1) {
            	doesRangeContainMultipleProfiles = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[doesRangeContainsMultipleProfiles]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[doesRangeContainsMultipleProfiles]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt1 != null) {
                    pstmt1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: doesRangeContainMultipleProfiles =" + doesRangeContainMultipleProfiles);
            }
        }
        return doesRangeContainMultipleProfiles;

    }

}
