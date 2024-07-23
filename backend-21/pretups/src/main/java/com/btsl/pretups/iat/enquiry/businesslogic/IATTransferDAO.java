package com.btsl.pretups.iat.enquiry.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.iat.transfer.businesslogic.IATTransferItemVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;

public class IATTransferDAO {
    private static Log _log = LogFactory.getFactory().getInstance(IATTransferDAO.class.getName());
    private static OperatorUtilI _operatorUtilI = null;
    static {
        try {
            _operatorUtilI = (OperatorUtilI) Class.forName((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS)).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "", "", "", "", "Exception while loading the operator util class in class :" + IATTransferDAO.class.getName() + ":" + e.getMessage());
        }
    }

    /**
     * Method for loading Services List for IAT Enquiry.
     * 
     * 
     * @author Gopal
     * 
     * @param pCon
     *            java.sql.Connection
     * @param p_moduleCode
     *            String
     * @param p_allServices
     *            boolean
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadServicesListForIATEnquiry(Connection pCon, String p_moduleCode) throws BTSLBaseException {
        
    	final String methodName = "loadServicesListForIATEnquiry";
    	if (_log.isDebugEnabled()) {
            _log.debug("loadServicesListForIATEnquiry", "Entered p_moduleCode=" + p_moduleCode);
        }

        
        
        StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT distinct st.service_type,st.name,st.type ");
        strBuff.append("FROM service_type st,IAT_NW_SERVICE_MAPPING insm");
        strBuff.append(" WHERE st.external_interface = 'Y' AND status = 'Y' AND st.module= ? ");
        strBuff.append(" AND st.SERVICE_TYPE=insm.SERVICE_TYPE ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try (PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);){
           
            pstmt.setString(1, p_moduleCode);
           try(ResultSet rs = pstmt.executeQuery();)
           {
            while (rs.next()) {
                ListValueVO vo = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                vo.setOtherInfo(rs.getString("type"));
                list.add(vo);
            }
        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATTransferDAO[loadServicesListForIATEnquiry]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicesList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATTransferDAO[loadServicesListForIATEnquiry]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
          
           
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: serviceList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method loadIATTransferVOList.
     * This method load the list of the transfers for the IAT Type.
     * 
     * @param pCon
     *            Connection
     * @param pNetworkCode
     *            String
     * @param pFromdate
     *            Date
     * @param p_toDate
     *            Date
     * @param pSenderMsisdn
     *            String
     * @param pTransferID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadIATTransferVOList(Connection pCon, String pNetworkCode, Date pFromdate, Date p_todate, String pSenderMsisdn, String pTransferID, String pServiceType) throws BTSLBaseException {
        if (p_todate == null) {
            p_todate = new Date();
        }
        final String methodName = "loadIATTransferVOList";
        try {
            if (_operatorUtilI.getNewDataAftrTbleMerging(pFromdate, p_todate)) {
                return loadIATTransferVOList_new(pCon, pNetworkCode, pFromdate, p_todate, pSenderMsisdn, pTransferID, pServiceType);
            } else {
                return loadIATTransferVOList_old(pCon, pNetworkCode, pFromdate, p_todate, pSenderMsisdn, pTransferID, pServiceType);
            }
        } catch (Exception e) {
            _log.error("getTransactionDetails", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
        }
        

        return loadIATTransferVOList_new(pCon, pNetworkCode, pFromdate, p_todate, pSenderMsisdn, pTransferID, pServiceType);

    }

    private ArrayList loadIATTransferVOList_old(Connection pCon, String pNetworkCode, Date pFromdate, Date p_toDate, String pSenderMsisdn, String pTransferID, String pServiceType) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
        	StringBuilder sb = new StringBuilder("");
        	sb.append("Entered pNetworkCode=");
        	sb.append(pNetworkCode);
        	sb.append(", pSenderMsisdn:");
        	sb.append(pSenderMsisdn);
        	sb.append(" pFromdate:");
        	sb.append(pFromdate);
        	sb.append(" p_toDate: ");
        	sb.append(p_toDate);
        	sb.append(",pTransferID=");
        	sb.append(pTransferID);
        	sb.append(",pServiceType=");
        	sb.append(pServiceType);
        	
            _log.debug("loadIATTransferVOList_old", sb.toString());
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferVO c2sTransferVO = null;
        IATTransferItemVO iatTransferItemVO = null;
        ArrayList c2sTransferVOList = new ArrayList();
        UserPhoneVO phoneVo = null;
        String isSenderPrimary = null;
        final String methodName = "loadIATTransferVOList_old";
        try {
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
                UserDAO userDAO = new UserDAO();
                if (!BTSLUtil.isNullString(pSenderMsisdn)) {
                    phoneVo = userDAO.loadUserAnyPhoneVO(pCon, pSenderMsisdn);
                    if (phoneVo != null) {
                        isSenderPrimary = phoneVo.getPrimaryNumber();
                    }
                }
            }

            IATTransferQry iatTransferQry = (IATTransferQry) ObjectProducer.getObject(QueryConstants.IAT_TRANSFER_QRY, QueryConstants.QUERY_PRODUCER);
            pstmtSelect = iatTransferQry.loadIATTransferVOListOldQry(pCon, pNetworkCode, pFromdate, p_toDate, pSenderMsisdn, pTransferID, pServiceType, phoneVo);
            rs = pstmtSelect.executeQuery();
            ArrayList sourceTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSACTION_SOURCE_TYPE, true);

            while (rs.next()) {
                c2sTransferVO = new C2STransferVO();

                c2sTransferVO.setProductName(rs.getString("short_name"));
                c2sTransferVO.setServiceName(rs.getString("name"));
                c2sTransferVO.setSenderName(rs.getString("user_name"));

                c2sTransferVO.setErrorMessage(rs.getString("errcode"));
                c2sTransferVO.setTransferID(rs.getString("transfer_id"));
                c2sTransferVO.setTransferDate(rs.getDate("transfer_date"));
                c2sTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                c2sTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time")));
                c2sTransferVO.setNetworkCode(rs.getString("network_code"));
                c2sTransferVO.setSenderID(rs.getString("sender_id"));
               
                c2sTransferVO.setProductCode(rs.getString("product_code"));
                c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                c2sTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                c2sTransferVO.setReceiverNetworkCode(rs.getString("receiver_network_code"));
                c2sTransferVO.setTransferValue(rs.getLong("transfer_value"));
                c2sTransferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                c2sTransferVO.setErrorCode(rs.getString("error_code"));
                c2sTransferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
                c2sTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                c2sTransferVO.setReferenceID(rs.getString("reference_id"));
                c2sTransferVO.setServiceType(rs.getString("service_type"));
                c2sTransferVO.setDifferentialApplicable(rs.getString("differential_applicable"));
                c2sTransferVO.setPinSentToMsisdn(rs.getString("pin_sent_to_msisdn"));
                c2sTransferVO.setLanguage(rs.getString("language"));
                c2sTransferVO.setCountry(rs.getString("country"));
                c2sTransferVO.setSkey(rs.getLong("skey"));
                c2sTransferVO.setSkeyGenerationTime(rs.getDate("skey_generation_time"));
                c2sTransferVO.setSkeySentToMsisdn(rs.getString("skey_sent_to_msisdn"));
                c2sTransferVO.setRequestThroughQueue(rs.getString("request_through_queue"));
                c2sTransferVO.setCreditBackStatus(rs.getString("credit_back_status"));
                c2sTransferVO.setQuantity(rs.getLong("quantity"));
                c2sTransferVO.setReconciliationFlag(rs.getString("reconciliation_flag"));
                c2sTransferVO.setReconciliationDate(rs.getDate("reconciliation_date"));
                c2sTransferVO.setReconciliationBy(rs.getString("reconciliation_by"));
                c2sTransferVO.setCreatedOn(rs.getDate("created_on"));
                c2sTransferVO.setCreatedBy(rs.getString("created_by"));
                c2sTransferVO.setModifiedOn(rs.getDate("modified_on"));
                c2sTransferVO.setModifiedBy(rs.getString("modified_by"));
                c2sTransferVO.setTransferStatus(rs.getString("txnstatus"));
                c2sTransferVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                c2sTransferVO.setVersion(rs.getString("version"));
                c2sTransferVO.setCardGroupID(rs.getString("card_group_id"));
                c2sTransferVO.setSenderTransferValue(rs.getLong("sender_transfer_value"));
                c2sTransferVO.setReceiverAccessFee(rs.getLong("receiver_access_fee"));
                c2sTransferVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
                c2sTransferVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                c2sTransferVO.setReceiverTax1Value(rs.getLong("receiver_tax1_value"));
                c2sTransferVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
                c2sTransferVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                c2sTransferVO.setReceiverTax2Value(rs.getLong("receiver_tax2_value"));
                c2sTransferVO.setReceiverValidity(rs.getInt("receiver_validity"));
                c2sTransferVO.setReceiverTransferValue(rs.getLong("receiver_transfer_value"));
                c2sTransferVO.setReceiverBonusValue(rs.getLong("receiver_bonus_value"));
                c2sTransferVO.setReceiverGracePeriod(rs.getInt("receiver_grace_period"));
                c2sTransferVO.setReceiverBonusValidity(rs.getInt("receiver_bonus_validity"));
                c2sTransferVO.setCardGroupCode(rs.getString("card_group_code"));
                c2sTransferVO.setReceiverValPeriodType(rs.getString("receiver_valperiod_type"));
                c2sTransferVO.setDifferentialGiven(rs.getString("differential_given"));
                c2sTransferVO.setGrphDomainCode(rs.getString("grph_domain_code"));
                c2sTransferVO.setSourceType(BTSLUtil.getOptionDesc(rs.getString("source_type"), sourceTypeList).getLabel());

                // Changed on 27/05/07 For service type selector Mapping
                c2sTransferVO.setSubService(PretupsBL.getSelectorDescriptionFromCode(c2sTransferVO.getServiceType() + "_" + rs.getString("sub_service")));
                c2sTransferVO.setSerialNumber(rs.getString("serial_number"));
                c2sTransferVO.setExtCreditIntfceType(rs.getString("ext_credit_intfce_type"));
                // IAT transaction Info
                iatTransferItemVO = new IATTransferItemVO();
                iatTransferItemVO.setIatTxnId(rs.getString("iat_txn_id"));

                if (rs.getDate("iat_timestamp") != null) {
                    iatTransferItemVO.setIatTimestampString(BTSLUtil.getDateStringFromDate(rs.getDate("iat_timestamp")));
                }

                iatTransferItemVO.setIatRcvrCountryName(rs.getString("rec_country_name"));
                iatTransferItemVO.setIatRcvrCountryCode(rs.getInt("rec_country_code"));
                iatTransferItemVO.setIatRecNWCode(rs.getString("rec_nw_code"));
                iatTransferItemVO.setIatErrorCode(rs.getString("iat_error_code"));
                iatTransferItemVO.setIatErrorMessage(rs.getString("iat_message"));
                iatTransferItemVO.setIatRecCountryShortName(rs.getString("rec_country_short_name"));
                iatTransferItemVO.setIatRcvrCurrency(rs.getString("currency"));
                iatTransferItemVO.setIatCreditRespCode(rs.getString("credit_resp_code"));
                iatTransferItemVO.setIatCreditMessage(rs.getString("credit_msg"));
                iatTransferItemVO.setIatRcvrNWErrorCode(rs.getString("rec_nw_error_code"));
                iatTransferItemVO.setIatRcvrNWErrorMessage(rs.getString("rec_nw_message"));
                iatTransferItemVO.setIatCheckStatusRespCode(rs.getString("chk_status_resp_code"));

                iatTransferItemVO.setIatProvRatio(rs.getDouble("prov_ratio"));
                iatTransferItemVO.setIatExchangeRate(rs.getDouble("exchange_rate"));

                iatTransferItemVO.setIatFailedAt(rs.getString("failed_at"));
                iatTransferItemVO.setIatInterfaceID(rs.getString("interface_id"));
                iatTransferItemVO.setIatTxnStatus(rs.getString("iat_txn_status"));

                iatTransferItemVO.setIatReceivedAmount(Double.valueOf(PretupsBL.getDisplayAmount(rs.getLong("sent_amt"))));
                iatTransferItemVO.setIatFees(Double.valueOf(PretupsBL.getDisplayAmount(rs.getLong("fees"))));
                iatTransferItemVO.setIatReceiverSystemBonus(Double.valueOf(PretupsBL.getDisplayAmount(rs.getLong("rec_bonus"))));
                iatTransferItemVO.setIatRcvrRcvdAmt(Double.valueOf(PretupsBL.getDisplayAmount(rs.getLong("rcvd_amt"))));
                iatTransferItemVO.setIatSentAmtByIAT(Double.valueOf(PretupsBL.getDisplayAmount(rs.getLong("sent_amt_iattorec"))));

                c2sTransferVO.setIatTransferItemVO(iatTransferItemVO);
                c2sTransferVOList.add(c2sTransferVO);
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error("loadIATTransferVOList_old", "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATTransferDAO[loadIATTransferVOList_old]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadIATTransferVOList_old", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadIATTransferVOList_old", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATTransferDAO[loadIATTransferVOList_old]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadIATTransferVOList_old", "error.general.processing");
        }// end of catch
        finally {
            try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing result set.", e);
              }
            try{
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing result set.", e);
              }
            if (_log.isDebugEnabled()) {
                _log.debug("loadIATTransferVOList_old", "Exiting c2sTransferVOList.size()=" + c2sTransferVOList.size());
            }
        }// end of finally

        return c2sTransferVOList;
    }

    /**
     * Method loadIATTransferVOList.
     * This method load the list of the transfers for the IAT Type.
     * 
     * @param pCon
     *            Connection
     * @param pNetworkCode
     *            String
     * @param pFromdate
     *            Date
     * @param p_toDate
     *            Date
     * @param pSenderMsisdn
     *            String
     * @param pTransferID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    private ArrayList loadIATTransferVOList_new(Connection pCon, String pNetworkCode, Date pFromdate, Date p_toDate, String pSenderMsisdn, String pTransferID, String pServiceType) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
        	StringBuilder sb = new StringBuilder("");
        	sb.append("Entered pNetworkCode=");
        	sb.append(pNetworkCode);
        	sb.append(", pSenderMsisdn:");
        	sb.append(pSenderMsisdn);
        	sb.append(" pFromdate:");
        	sb.append(pFromdate);
        	sb.append(" p_toDate: ");
        	sb.append(p_toDate);
        	sb.append(",pTransferID=");
        	sb.append(pTransferID);
        	sb.append(",pServiceType=");
        	sb.append(pServiceType);
        	
            _log.debug("loadIATTransferVOList_new", sb.toString());
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferVO c2sTransferVO = null;
        IATTransferItemVO iatTransferItemVO = null;
        ArrayList c2sTransferVOList = new ArrayList();
        UserPhoneVO phoneVo = null;
        String isSenderPrimary = null;
        final String methodName = "loadIATTransferVOList_new";
        try {
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
                UserDAO userDAO = new UserDAO();
                if (!BTSLUtil.isNullString(pSenderMsisdn)) {
                    phoneVo = userDAO.loadUserAnyPhoneVO(pCon, pSenderMsisdn);
                    if (phoneVo != null) {
                        isSenderPrimary = phoneVo.getPrimaryNumber();
                    }
                }
            }
            IATTransferQry iatTransferQry = (IATTransferQry)ObjectProducer.getObject(QueryConstants.IAT_TRANSFER_QRY, QueryConstants.QUERY_PRODUCER);
            pstmtSelect = iatTransferQry.loadIATTransferVOListNewQry(pCon, pNetworkCode, pFromdate, p_toDate, pSenderMsisdn, pTransferID, pServiceType, phoneVo);
            rs = pstmtSelect.executeQuery();
            ArrayList sourceTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSACTION_SOURCE_TYPE, true);

            while (rs.next()) {
                c2sTransferVO = new C2STransferVO();

                c2sTransferVO.setProductName(rs.getString("short_name"));
                c2sTransferVO.setServiceName(rs.getString("name"));
                c2sTransferVO.setSenderName(rs.getString("user_name"));

                c2sTransferVO.setErrorMessage(rs.getString("errcode"));
                c2sTransferVO.setTransferID(rs.getString("transfer_id"));
                c2sTransferVO.setTransferDate(rs.getDate("transfer_date"));
                c2sTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                c2sTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time")));
                c2sTransferVO.setNetworkCode(rs.getString("network_code"));
                c2sTransferVO.setSenderID(rs.getString("sender_id"));

                c2sTransferVO.setProductCode(rs.getString("product_code"));
                c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                c2sTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                c2sTransferVO.setReceiverNetworkCode(rs.getString("receiver_network_code"));
                c2sTransferVO.setTransferValue(rs.getLong("transfer_value"));
                c2sTransferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                c2sTransferVO.setErrorCode(rs.getString("error_code"));
                c2sTransferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
                c2sTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                c2sTransferVO.setReferenceID(rs.getString("reference_id"));
                c2sTransferVO.setServiceType(rs.getString("service_type"));
                c2sTransferVO.setDifferentialApplicable(rs.getString("differential_applicable"));
                c2sTransferVO.setPinSentToMsisdn(rs.getString("pin_sent_to_msisdn"));
                c2sTransferVO.setLanguage(rs.getString("language"));
                c2sTransferVO.setCountry(rs.getString("country"));
                c2sTransferVO.setSkey(rs.getLong("skey"));
                c2sTransferVO.setSkeyGenerationTime(rs.getDate("skey_generation_time"));
                c2sTransferVO.setSkeySentToMsisdn(rs.getString("skey_sent_to_msisdn"));
                c2sTransferVO.setRequestThroughQueue(rs.getString("request_through_queue"));
                c2sTransferVO.setCreditBackStatus(rs.getString("credit_back_status"));
                c2sTransferVO.setQuantity(rs.getLong("quantity"));
                c2sTransferVO.setReconciliationFlag(rs.getString("reconciliation_flag"));
                c2sTransferVO.setReconciliationDate(rs.getDate("reconciliation_date"));
                c2sTransferVO.setReconciliationBy(rs.getString("reconciliation_by"));
                c2sTransferVO.setCreatedOn(rs.getDate("created_on"));
                c2sTransferVO.setCreatedBy(rs.getString("created_by"));
                c2sTransferVO.setModifiedOn(rs.getDate("modified_on"));
                c2sTransferVO.setModifiedBy(rs.getString("modified_by"));
                c2sTransferVO.setTransferStatus(rs.getString("txnstatus"));
                c2sTransferVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                c2sTransferVO.setVersion(rs.getString("version"));
                c2sTransferVO.setCardGroupID(rs.getString("card_group_id"));
                c2sTransferVO.setSenderTransferValue(rs.getLong("sender_transfer_value"));
                c2sTransferVO.setReceiverAccessFee(rs.getLong("receiver_access_fee"));
                c2sTransferVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
                c2sTransferVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                c2sTransferVO.setReceiverTax1Value(rs.getLong("receiver_tax1_value"));
                c2sTransferVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
                c2sTransferVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                c2sTransferVO.setReceiverTax2Value(rs.getLong("receiver_tax2_value"));
                c2sTransferVO.setReceiverValidity(rs.getInt("receiver_validity"));
                c2sTransferVO.setReceiverTransferValue(rs.getLong("receiver_transfer_value"));
                c2sTransferVO.setReceiverBonusValue(rs.getLong("receiver_bonus_value"));
                c2sTransferVO.setReceiverGracePeriod(rs.getInt("receiver_grace_period"));
                c2sTransferVO.setReceiverBonusValidity(rs.getInt("receiver_bonus_validity"));
                c2sTransferVO.setCardGroupCode(rs.getString("card_group_code"));
                c2sTransferVO.setReceiverValPeriodType(rs.getString("receiver_valperiod_type"));
                c2sTransferVO.setDifferentialGiven(rs.getString("differential_given"));
                c2sTransferVO.setGrphDomainCode(rs.getString("grph_domain_code"));
                c2sTransferVO.setSourceType(BTSLUtil.getOptionDesc(rs.getString("source_type"), sourceTypeList).getLabel());

                // Changed on 27/05/07 For service type selector Mapping
                c2sTransferVO.setSubService(PretupsBL.getSelectorDescriptionFromCode(c2sTransferVO.getServiceType() + "_" + rs.getString("sub_service")));
                c2sTransferVO.setSerialNumber(rs.getString("serial_number"));
                c2sTransferVO.setExtCreditIntfceType(rs.getString("ext_credit_intfce_type"));
                // IAT transaction Info
                iatTransferItemVO = new IATTransferItemVO();
                iatTransferItemVO.setIatTxnId(rs.getString("iat_txn_id"));

                if (rs.getDate("iat_timestamp") != null) {
                    iatTransferItemVO.setIatTimestampString(BTSLUtil.getDateStringFromDate(rs.getDate("iat_timestamp")));
                }

                iatTransferItemVO.setIatRcvrCountryName(rs.getString("rec_country_name"));
                iatTransferItemVO.setIatRcvrCountryCode(rs.getInt("rec_country_code"));
                iatTransferItemVO.setIatRecNWCode(rs.getString("rec_nw_code"));
                iatTransferItemVO.setIatErrorCode(rs.getString("iat_error_code"));
                iatTransferItemVO.setIatErrorMessage(rs.getString("iat_message"));
                iatTransferItemVO.setIatRecCountryShortName(rs.getString("rec_country_short_name"));
                iatTransferItemVO.setIatRcvrCurrency(rs.getString("currency"));
                iatTransferItemVO.setIatCreditRespCode(rs.getString("credit_resp_code"));
                iatTransferItemVO.setIatCreditMessage(rs.getString("credit_msg"));
                iatTransferItemVO.setIatRcvrNWErrorCode(rs.getString("rec_nw_error_code"));
                iatTransferItemVO.setIatRcvrNWErrorMessage(rs.getString("rec_nw_message"));
                iatTransferItemVO.setIatCheckStatusRespCode(rs.getString("chk_status_resp_code"));

                iatTransferItemVO.setIatProvRatio(rs.getDouble("prov_ratio"));
                iatTransferItemVO.setIatExchangeRate(rs.getDouble("exchange_rate"));

                iatTransferItemVO.setIatFailedAt(rs.getString("failed_at"));
                iatTransferItemVO.setIatInterfaceID(rs.getString("interface_id"));
                iatTransferItemVO.setIatTxnStatus(rs.getString("iat_txn_status"));

                iatTransferItemVO.setIatReceivedAmount(Double.valueOf(PretupsBL.getDisplayAmount(rs.getLong("sent_amt"))));
                iatTransferItemVO.setIatFees(Double.valueOf(PretupsBL.getDisplayAmount(rs.getLong("fees"))));
                iatTransferItemVO.setIatReceiverSystemBonus(Double.valueOf(PretupsBL.getDisplayAmount(rs.getLong("rec_bonus"))));
                iatTransferItemVO.setIatRcvrRcvdAmt(Double.valueOf(PretupsBL.getDisplayAmount(rs.getLong("rcvd_amt"))));
                iatTransferItemVO.setIatSentAmtByIAT(Double.valueOf(PretupsBL.getDisplayAmount(rs.getLong("sent_amt_iattorec"))));

                c2sTransferVO.setIatTransferItemVO(iatTransferItemVO);
                c2sTransferVOList.add(c2sTransferVO);
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error("loadIATTransferVOList_new", "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATTransferDAO[loadIATTransferVOList_new]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadIATTransferVOList_new", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadIATTransferVOList_new", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATTransferDAO[loadIATTransferVOList_new]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadIATTransferVOList_new", "error.general.processing");
        }// end of catch
        finally {
            try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing result set.", e);
              }
            try{
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing result set.", e);
              }
            if (_log.isDebugEnabled()) {
                _log.debug("loadIATTransferVOList_new", "Exiting c2sTransferVOList.size()=" + c2sTransferVOList.size());
            }
        }// end of finally

        return c2sTransferVOList;
    }

    /**
     * Method loadIATTransferItemsVOList.
     * This method is to load the items list according to the transfer ID.
     * 
     * @param pCon
     *            Connection
     * @param pTransferID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadIATTransferItemsVOList(Connection pCon, String pTransferID, Date pFromdate, Date p_todate) throws BTSLBaseException {
        if (p_todate == null) {
            p_todate = new Date();
        }
        final String methodName = "loadIATTransferItemsVOList";
        try {
            if (_operatorUtilI.getNewDataAftrTbleMerging(pFromdate, p_todate)) {
                return loadIATTransferItemsVOList(pCon, pTransferID);
            } else {
                return loadIATTransferItemsVOList_old(pCon, pTransferID);
            }
        } catch (Exception e) {
            _log.error("getTransactionDetails", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
        }
        

        return loadIATTransferItemsVOList(pCon, pTransferID);

    }

    private ArrayList loadIATTransferItemsVOList_old(Connection pCon, String pTransferID) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadIATTransferItemsVOList_old", "Entered pTransferID=" + pTransferID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferItemVO c2sTransferItemVO = null;
        ArrayList c2sTransferItemsVOList = new ArrayList();
        final String methodName = "loadIATTransferItemsVOList_old";
        try {
            IATTransferQry iatTransferQry = (IATTransferQry) ObjectProducer.getObject(QueryConstants.IAT_TRANSFER_QRY, QueryConstants.QUERY_PRODUCER);
            pstmtSelect = iatTransferQry.loadIATTransferItemsVOListOldQry(pCon, pTransferID);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                c2sTransferItemVO = new C2STransferItemVO();

                c2sTransferItemVO.setTransferID(rs.getString("transfer_id"));
                c2sTransferItemVO.setMsisdn(rs.getString("msisdn"));
                c2sTransferItemVO.setEntryDate(rs.getDate("entry_date"));
                c2sTransferItemVO.setRequestValue(rs.getLong("request_value"));
                c2sTransferItemVO.setPreviousBalance(rs.getLong("previous_balance"));

                c2sTransferItemVO.setPostBalance(rs.getLong("post_balance"));
                c2sTransferItemVO.setUserType(rs.getString("user_type"));
                c2sTransferItemVO.setTransferType(rs.getString("transfer_type_value"));
                c2sTransferItemVO.setEntryType(rs.getString("entry_type"));
                c2sTransferItemVO.setValidationStatus(rs.getString("validation_status"));
                c2sTransferItemVO.setUpdateStatus(rs.getString("update_status"));
                c2sTransferItemVO.setTransferValue(rs.getLong("transfer_value"));
                c2sTransferItemVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                c2sTransferItemVO.setInterfaceType(rs.getString("interface_type"));
                c2sTransferItemVO.setInterfaceID(rs.getString("interface_id"));

                c2sTransferItemVO.setInterfaceResponseCode(rs.getString("interface_response_code"));
                c2sTransferItemVO.setInResponseCodeDesc(rs.getString("in_response_code_desc"));
                c2sTransferItemVO.setInterfaceReferenceID(rs.getString("interface_reference_id"));
                c2sTransferItemVO.setSubscriberType(rs.getString("subscriber_type"));
                c2sTransferItemVO.setServiceClassCode(rs.getString("service_class_code"));
                c2sTransferItemVO.setPreviousExpiry(rs.getDate("msisdn_previous_expiry"));
                c2sTransferItemVO.setNewExpiry(rs.getDate("msisdn_new_expiry"));
                c2sTransferItemVO.setTransferStatus(rs.getString("transfer_status"));

                c2sTransferItemVO.setTransferDate(rs.getDate("transfer_date"));
                c2sTransferItemVO.setTransferDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time")));
                c2sTransferItemVO.setEntryDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("entry_date_time")));
                c2sTransferItemVO.setFirstCall(rs.getString("first_call"));
                c2sTransferItemVO.setSNo(rs.getInt("sno"));
                c2sTransferItemVO.setPrefixID(rs.getLong("prefix_id"));
                c2sTransferItemVO.setServiceClass(rs.getString("service_class_id"));
                c2sTransferItemVO.setProtocolStatus(rs.getString("protocol_status"));
                c2sTransferItemVO.setAccountStatus(rs.getString("account_status"));
                c2sTransferItemVO.setReferenceID(rs.getString("reference_id"));
                c2sTransferItemVO.setLanguage(rs.getString("language"));
                c2sTransferItemVO.setCountry(rs.getString("country"));
                c2sTransferItemVO.setTransferStatusMessage(rs.getString("value"));

               
                c2sTransferItemsVOList.add(c2sTransferItemVO);
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error("loadIATTransferItemsVOList_old", "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATTransferDAO[loadIATTransferItemsVOList_old]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadIATTransferItemsVOList_old", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadC2STransferItemsVOList", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATTransferDAO[loadIATTransferItemsVOList_old]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadIATTransferItemsVOList_old", "error.general.processing");
        }// end of catch
        finally {
            try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing result set.", e);
              }
            try{
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing result set.", e);
              }
            if (_log.isDebugEnabled()) {
                _log.debug("loadIATTransferItemsVOList_old", "Exiting c2sTransferItemsVOList.size()=" + c2sTransferItemsVOList.size());
            }
        }// end of finally

        return c2sTransferItemsVOList;
    }

    /**
     * Method loadIATTransferItemsVOList.
     * This method is to load the items list according to the transfer ID.
     * 
     * @param pCon
     *            Connection
     * @param pTransferID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    private ArrayList loadIATTransferItemsVOList(Connection pCon, String pTransferID) throws BTSLBaseException {

    	   final String methodName = "loadIATTransferItemsVOList";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered pTransferID=" + pTransferID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
       
        ArrayList c2sTransferItemsVOList = new ArrayList();
      
        try {
            
        	IATTransferQry iatTransferQry = (IATTransferQry) ObjectProducer.getObject(QueryConstants.IAT_TRANSFER_QRY, QueryConstants.QUERY_PRODUCER);
        	pstmtSelect = iatTransferQry.loadIATTransferItemsVOListQry(pCon, pTransferID);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                C2STransferItemVO senderItemVO = new C2STransferItemVO();

                senderItemVO.setTransferID(rs.getString("transfer_id"));
                senderItemVO.setMsisdn(rs.getString("sender_msisdn"));
                senderItemVO.setEntryDate(rs.getDate("created_on"));
                senderItemVO.setRequestValue(rs.getLong("quantity"));
                senderItemVO.setPreviousBalance(rs.getLong("sender_previous_balance"));

                senderItemVO.setPostBalance(rs.getLong("sender_post_balance"));
                senderItemVO.setUserType(PretupsI.USER_TYPE_SENDER);
                senderItemVO.setTransferType(rs.getString("transfer_type_value"));
                senderItemVO.setEntryType(PretupsI.DEBIT);
              
                senderItemVO.setUpdateStatus(rs.getString("debit_status"));
                senderItemVO.setTransferValue(rs.getLong("transfer_value"));
                senderItemVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
              
                senderItemVO.setTransferStatus(rs.getString("transfer_status"));

                senderItemVO.setTransferDate(rs.getDate("transfer_date"));
                senderItemVO.setTransferDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time")));
 
                senderItemVO.setFirstCall(rs.getString("first_call"));
                senderItemVO.setSNo(1);
               
                senderItemVO.setLanguage(rs.getString("language"));
                senderItemVO.setCountry(rs.getString("country"));
                senderItemVO.setTransferStatusMessage(rs.getString("value"));


                c2sTransferItemsVOList.add(senderItemVO);

                C2STransferItemVO receiverItemVO = new C2STransferItemVO();

                receiverItemVO.setTransferID(rs.getString("transfer_id"));
                receiverItemVO.setMsisdn(rs.getString("receiver_msisdn"));
                receiverItemVO.setEntryDate(rs.getDate("created_on"));
                receiverItemVO.setRequestValue(rs.getLong("quantity"));
                receiverItemVO.setPreviousBalance(rs.getLong("receiver_previous_balance"));

                receiverItemVO.setPostBalance(rs.getLong("receiver_post_balance"));
                receiverItemVO.setUserType(PretupsI.USER_TYPE_RECEIVER);
                receiverItemVO.setTransferType(rs.getString("transfer_type_value"));
                receiverItemVO.setEntryType(PretupsI.CREDIT);
                receiverItemVO.setValidationStatus(rs.getString("validation_status"));
                receiverItemVO.setUpdateStatus(rs.getString("credit_status"));
                receiverItemVO.setTransferValue(rs.getLong("transfer_value"));
                receiverItemVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                receiverItemVO.setInterfaceType(rs.getString("interface_type"));
                receiverItemVO.setInterfaceID(rs.getString("interface_id"));

                receiverItemVO.setInterfaceResponseCode(rs.getString("interface_response_code"));
                receiverItemVO.setInResponseCodeDesc(rs.getString("in_response_code_desc"));
                receiverItemVO.setInterfaceReferenceID(rs.getString("interface_reference_id"));
                receiverItemVO.setSubscriberType(rs.getString("subscriber_type"));
                receiverItemVO.setServiceClassCode(rs.getString("service_class_code"));
                receiverItemVO.setPreviousExpiry(rs.getDate("msisdn_previous_expiry"));
                receiverItemVO.setNewExpiry(rs.getDate("msisdn_new_expiry"));
                receiverItemVO.setTransferStatus(rs.getString("transfer_status"));

                receiverItemVO.setTransferDate(rs.getDate("transfer_date"));
                receiverItemVO.setTransferDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time")));

                receiverItemVO.setFirstCall(rs.getString("first_call"));
                receiverItemVO.setSNo(2);
                receiverItemVO.setPrefixID(rs.getLong("prefix_id"));
                receiverItemVO.setServiceClass(rs.getString("service_class_id"));
                receiverItemVO.setProtocolStatus(rs.getString("protocol_status"));
                receiverItemVO.setAccountStatus(rs.getString("account_status"));
                receiverItemVO.setReferenceID(rs.getString("reference_id"));
                receiverItemVO.setLanguage(rs.getString("language"));
                receiverItemVO.setCountry(rs.getString("country"));
                receiverItemVO.setTransferStatusMessage(rs.getString("value"));

                c2sTransferItemsVOList.add(receiverItemVO);

                String cr_bk_status = rs.getString("credit_back_status");
                String reconciliation_flag = rs.getString("reconciliation_flag");

                int itemNo = 2;

                if (!BTSLUtil.isNullString(cr_bk_status)) {

                    C2STransferItemVO creditBackVO = new C2STransferItemVO();

                    creditBackVO.setMsisdn(senderItemVO.getMsisdn());
                    creditBackVO.setRequestValue(senderItemVO.getRequestValue());
                    creditBackVO.setSubscriberType(senderItemVO.getSubscriberType());
                    creditBackVO.setTransferDate(senderItemVO.getTransferDate());
                    creditBackVO.setTransferDateTime(senderItemVO.getTransferDateTime());
                    creditBackVO.setTransferID(senderItemVO.getTransferID());
                    creditBackVO.setUserType(senderItemVO.getUserType());
                    creditBackVO.setEntryDate(senderItemVO.getEntryDate());
                    creditBackVO.setEntryDateTime(senderItemVO.getEntryDateTime());
                    creditBackVO.setPrefixID(senderItemVO.getPrefixID());
                    creditBackVO.setTransferValue(senderItemVO.getTransferValue());
                    creditBackVO.setInterfaceID(senderItemVO.getInterfaceID());
                    creditBackVO.setInterfaceType(senderItemVO.getInterfaceType());
                    creditBackVO.setServiceClass(senderItemVO.getServiceClass());
                    creditBackVO.setServiceClassCode(senderItemVO.getServiceClassCode());
                    creditBackVO.setInterfaceHandlerClass(senderItemVO.getInterfaceHandlerClass());
                    creditBackVO.setLanguage(senderItemVO.getLanguage());
                    creditBackVO.setCountry(senderItemVO.getCountry());

                    creditBackVO.setSNo(++itemNo);
                    creditBackVO.setEntryType(PretupsI.CREDIT);
                    creditBackVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
                    creditBackVO.setValidationStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                    creditBackVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                    creditBackVO.setUpdateStatus(cr_bk_status);
                    creditBackVO.setPreviousBalance(rs.getLong("SENDER_CR_BK_PREV_BAL"));
                    creditBackVO.setPostBalance(rs.getLong("SENDER_CR_BK_POST_BAL"));

                    c2sTransferItemsVOList.add(creditBackVO);

                }
                if (!BTSLUtil.isNullString(reconciliation_flag)) {

                    C2STransferItemVO reconcileVO = new C2STransferItemVO();
                    reconcileVO.setMsisdn(senderItemVO.getMsisdn());
                    reconcileVO.setSNo(++itemNo);
                    reconcileVO.setEntryType(rs.getString("reconcile_entry_type"));
                    reconcileVO.setEntryDate(rs.getDate("reconciliation_date"));
                    reconcileVO.setEntryDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("reconciliation_date")));
                    reconcileVO.setTransferDate(rs.getDate("reconciliation_date"));
                    reconcileVO.setTransferDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("reconciliation_date")));
                    reconcileVO.setTransferID(senderItemVO.getTransferID());
                    reconcileVO.setUserType(senderItemVO.getUserType());
                    reconcileVO.setTransferType(PretupsI.TRANSFER_TYPE_RECON);
                    reconcileVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                    reconcileVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                    reconcileVO.setPreviousBalance(rs.getLong("SENDER_CR_SETL_PREV_BAL"));
                    reconcileVO.setPostBalance(rs.getLong("SENDER_CR_SETL_POST_BAL"));

                    c2sTransferItemsVOList.add(reconcileVO);
                }

            }

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATTransferDAO[loadIATTransferItemsVOList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATTransferDAO[loadIATTransferItemsVOList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
           try{
               if (rs!= null){
            	   rs.close();
               }
             }
             catch (SQLException e){
           	  _log.error("An error occurred closing result set.", e);
             }
           try{
               if (pstmtSelect!= null){
            	   pstmtSelect.close();
               }
             }
             catch (SQLException e){
           	  _log.error("An error occurred closing result set.", e);
             }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting c2sTransferItemsVOList.size()=" + c2sTransferItemsVOList.size());
            }
        }// end of finally

        return c2sTransferItemsVOList;
    }

    // Load key values for IAT Transfer Status
    public ArrayList loadKeyValuesList(Connection pCon, boolean p_isAllKey, String pType, String p_inKeys) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
        	StringBuilder sb = new StringBuilder("");
        	sb.append("Entered: p_isAllKey=");
        	sb.append(p_isAllKey);
        	sb.append(", pType=");
        	sb.append( pType );
        	sb.append(", p_inKeys=");
        	sb.append(p_inKeys);
            _log.debug("loadKeyValuesList", sb.toString());
        }
        final String methodName = "loadKeyValuesList";
        ArrayList list = new ArrayList();
       
         
        

        StringBuilder strBuff = new StringBuilder("SELECT key, value, type, text1 FROM key_values WHERE type=? ");
        if (!p_isAllKey) {
            strBuff.append("AND key IN (" + p_inKeys + ") ");
        }
        strBuff.append("ORDER BY key");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadKeyValuesList", "QUERY sqlSelect=" + sqlSelect);
        }
        try( PreparedStatement pstmtSelect =  pCon.prepareStatement(sqlSelect);) {
           
           
            pstmtSelect.setString(1, pType);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                list.add(new ListValueVO(rs.getString("value"), rs.getString("key")));
            }
        } 
        }catch (SQLException sqe) {
            _log.error("loadKeyValuesList", "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserReportDAO[loadKeyValuesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadKeyValuesList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadKeyValuesList", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserReportDAO[loadKeyValuesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadKeyValuesList", "error.general.processing");
        } finally {
        
           
            if (_log.isDebugEnabled()) {
                _log.debug("loadKeyValuesList", "Exiting: userList size =" + list.size());
            }
        }
        return list;
    }
}
