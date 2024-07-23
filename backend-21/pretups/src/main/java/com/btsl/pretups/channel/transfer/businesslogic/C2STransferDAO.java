package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.iat.transfer.businesslogic.IATTransferItemVO;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.CryptoUtil;
import com.btsl.util.XmlTagValueConstant;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.sshtools.j2ssh.util.Hash;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.string;

public class C2STransferDAO {

    private static Log LOG = LogFactory.getLog(C2STransferDAO.class.getName());
    private static C2STransferQry c2STransferQry;
    public static OperatorUtilI _operatorUtilI = null;
    
    
    static {
        try {
            _operatorUtilI = (OperatorUtilI) Class.forName((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS)).newInstance();
            c2STransferQry = (C2STransferQry) ObjectProducer.getObject(QueryConstants.C2S_TRANSFER_QRY,QueryConstants.QUERY_PRODUCER);
        } catch (Exception e) {

            LOG.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BuddyMgtAction", "", "", "",
                "Exception while loading the operator util class in class :" + C2STransferDAO.class.getName() + ":" + e.getMessage());
        }
    }

    /**
     * Method to add the C2S Transfers related information in the database
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_voucher_list
     * @return addCount
     * @throws BTSLBaseExceptionao
     */
    public int addC2STransferDetails(Connection p_con, C2STransferVO p_c2sTransferVO, List p_voucherList) throws BTSLBaseException {
        final String methodName = "addC2STransferDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_c2sTransferVO:");
        	loggerValue.append(p_c2sTransferVO.toString() );
        	loggerValue.append(" Voucher List size:" );
        	loggerValue.append(p_voucherList.size());
            LOG.debug(methodName,  loggerValue);
        }
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        ChannelUserVO channelUserVO = null;
        VomsVoucherVO voucherVO = null;
        ArrayList transferItemList = null;
        ArrayList newTransferItemList = null;
        C2STransferItemVO itemVO = null;
        C2STransferItemVO newItemVO = null;
        int listSize = 0;
        try {
            channelUserVO = (ChannelUserVO) p_c2sTransferVO.getSenderVO();
            Boolean c2sSeqIDAlwd = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SEQID_ALWD);
            String c2sSeqIDForGWC = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SEQID_FOR_GWC);
            String c2sSeqIDApplSer = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SEQID_APPL_SER);
            int i = 1;
            String tableName = BTSLUtil.getTableName("c2s_transfers");
            final StringBuffer insertQueryBuff = new StringBuffer(" INSERT INTO ");
            insertQueryBuff.append(tableName).append(" (transfer_id,transfer_date,transfer_date_time,network_code,sender_id,sender_category,product_code,sender_msisdn, ");
            insertQueryBuff.append(" receiver_msisdn,receiver_network_code,transfer_value,error_code,request_gateway_type,request_gateway_code, ");
            insertQueryBuff.append(" grph_domain_code,reference_id,service_type,pin_sent_to_msisdn,language,country,skey,skey_generation_time, ");
            insertQueryBuff.append(" skey_sent_to_msisdn,request_through_queue,quantity,created_by,created_on,modified_by,modified_on,transfer_status, ");
            insertQueryBuff.append(" card_group_set_id,version,card_group_id,sender_transfer_value,receiver_access_fee,receiver_tax1_type, ");
            insertQueryBuff.append(" receiver_tax1_rate,receiver_tax1_value,receiver_tax2_type,receiver_tax2_rate,receiver_tax2_value, ");
            insertQueryBuff.append(" receiver_validity,receiver_transfer_value,receiver_bonus_value,receiver_grace_period,receiver_bonus_validity, ");
            insertQueryBuff
                .append(" card_group_code,receiver_valperiod_type,temp_transfer_id,transfer_profile_id,commission_profile_id,source_type,sub_service,start_time,end_time,serial_number ");
            
            insertQueryBuff.append(" ,ext_credit_intfce_type,ACTIVE_USER_ID,subs_sid ,cell_id,switch_id,txn_type "); //6
            insertQueryBuff.append(",reversal_id,info1,info2,info3,info4,info5,info6,info7,info8,info9,info10,multicurrency_detail,bonus_amount ");//12
            if(p_c2sTransferVO.isRoam()&&p_c2sTransferVO.isStopAddnCommission()){
            	insertQueryBuff.append(",penalty, owner_penalty, penalty_details ");	//3
            }
            insertQueryBuff.append(" ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"
            		+ ",?,?,?,?,?,?" + ",?,?,?,?,?,?,?,?,?,?,?,?,?"); //18 total extra
            if(p_c2sTransferVO.isRoam() && p_c2sTransferVO.isStopAddnCommission()){
            	insertQueryBuff.append(",?,?,?");	//3
            }
            insertQueryBuff.append(")");
            //MVD---ext_credit_intfce_type,ACTIVE_USER_ID ");
            //insertQueryBuff.append(" ,subs_sid ");// for priveta recharge by
            //insertQueryBuff.append(",cell_id,switch_id ,txn_type")
            final String insertQuery = insertQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Insert query:" );
            	loggerValue.append(insertQuery);
                LOG.debug("addTransferDetails", loggerValue );
            }
            pstmtInsert = p_con.prepareStatement(insertQuery);

            transferItemList = p_c2sTransferVO.getTransferItemList();
            listSize = transferItemList.size();
            newTransferItemList = new ArrayList();
            CryptoUtil cryptoutil = CryptoUtil.getInstance();
            boolean aliasToBeEncrypted = false; 
            aliasToBeEncrypted = ((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.ALIAS_TO_BE_ENCRYPTED))).booleanValue();
            for (int j = 0, size = p_voucherList.size(); j < size; j++) {
                i = 1;
                voucherVO = (VomsVoucherVO) p_voucherList.get(j);
                pstmtInsert.setString(i, voucherVO.getTransactionID());
                i++;
                pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_c2sTransferVO.getTransferDate()));
                i++;
                pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getTransferDateTime()));
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getNetworkCode());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getSenderID());
                i++;
                pstmtInsert.setString(i, channelUserVO.getCategoryCode());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getProductCode());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getSenderMsisdn());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getReceiverMsisdn());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getReceiverNetworkCode());//10
                i++;
                pstmtInsert.setLong(i, p_c2sTransferVO.getRequestedAmount());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getErrorCode());
                i++;
                
                pstmtInsert.setString(i, p_c2sTransferVO.getRequestGatewayType());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getRequestGatewayCode());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getGrphDomainCode());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getReferenceID());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getServiceType());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getPinSentToMsisdn());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getLanguage());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getCountry());//20
                i++;
                pstmtInsert.setLong(i, p_c2sTransferVO.getSkey());
                i++;
                if (p_c2sTransferVO.getSkeyGenerationTime() == null) {
                    pstmtInsert.setNull(i, Types.TIMESTAMP);
                    i++;
                } else {
                    pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getSkeyGenerationTime()));
                    i++;
                }
                pstmtInsert.setString(i, p_c2sTransferVO.getSkeySentToMsisdn());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getRequestThroughQueue());
                i++;
                pstmtInsert.setLong(i, p_c2sTransferVO.getQuantity());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getCreatedBy());
                i++;
                pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getCreatedOn()));
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getModifiedBy());
                i++;
                pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getModifiedOn()));
                i++;
                
                pstmtInsert.setString(i, p_c2sTransferVO.getTransferStatus());//30
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getCardGroupSetID());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getVersion());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getCardGroupID());
                i++;
                pstmtInsert.setLong(i, p_c2sTransferVO.getSenderTransferValue());
                i++;
                pstmtInsert.setLong(i, p_c2sTransferVO.getReceiverAccessFee());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getReceiverTax1Type());
                i++;
                pstmtInsert.setDouble(i, p_c2sTransferVO.getReceiverTax1Rate());
                i++;
                pstmtInsert.setLong(i, p_c2sTransferVO.getReceiverTax1Value());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getReceiverTax2Type());
                i++;
                pstmtInsert.setDouble(i, p_c2sTransferVO.getReceiverTax2Rate());//40
                i++;
                pstmtInsert.setLong(i, p_c2sTransferVO.getReceiverTax2Value());
                i++;
                pstmtInsert.setInt(i, p_c2sTransferVO.getReceiverValidity());
                i++;
                pstmtInsert.setLong(i, p_c2sTransferVO.getReceiverTransferValue());
                i++;
                pstmtInsert.setLong(i, p_c2sTransferVO.getReceiverBonusValue());
                i++;
                pstmtInsert.setLong(i, p_c2sTransferVO.getReceiverGracePeriod());
                i++;
                pstmtInsert.setInt(i, p_c2sTransferVO.getReceiverBonusValidity());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getCardGroupCode());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getReceiverValPeriodType());
                i++;
                if(c2sSeqIDAlwd && BTSLUtil.isStringIn(channelUserVO.getServiceTypes(), c2sSeqIDApplSer) && BTSLUtil.isStringIn(((UserPhoneVO) channelUserVO.getUserPhoneVO()).getRequestGatewayCode(), c2sSeqIDForGWC)){
                	pstmtInsert.setString(i, ((UserPhoneVO) channelUserVO.getUserPhoneVO()).getOwnerTempTransferId());
                }else{
                	pstmtInsert.setString(i, ((UserPhoneVO) channelUserVO.getUserPhoneVO()).getTempTransferID());
                }
                
                i++;
                pstmtInsert.setString(i, channelUserVO.getTransferProfileID());//50
                i++;
                pstmtInsert.setString(i, channelUserVO.getCommissionProfileSetID());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getSourceType());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getSubService());
                i++;
                pstmtInsert.setLong(i, p_c2sTransferVO.getRequestStartTime());
                i++;
                pstmtInsert.setLong(i, System.currentTimeMillis());
                i++;
                pstmtInsert.setString(i, voucherVO.getSerialNo());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getExtCreditIntfceType());
                i++;
                
                pstmtInsert.setString(i, p_c2sTransferVO.getActiveUserId());
                i++;
                if(aliasToBeEncrypted){
              	  if(!BTSLUtil.isNullString(p_c2sTransferVO.getSubscriberSID()))
              		  pstmtInsert.setString(i, cryptoutil.encrypt(p_c2sTransferVO.getSubscriberSID(),Constants.KEY));
              	  else
              		  pstmtInsert.setString(i, p_c2sTransferVO.getSubscriberSID());
                }
                else
              	  pstmtInsert.setString(i, p_c2sTransferVO.getSubscriberSID());
                
               
                i++;
                // added for ussd
                pstmtInsert.setString(i, p_c2sTransferVO.getCellId());//60
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getSwitchId());
                i++;
                if (BTSLUtil.isNullString(p_c2sTransferVO.getTxnType())) {
                    pstmtInsert.setString(i, PretupsI.TXNTYPE_T);
                    i++;
                } else {
                    pstmtInsert.setString(i, p_c2sTransferVO.getTxnType());
                    i++;
                }
                // VFE 6 CR
                pstmtInsert.setString(i, p_c2sTransferVO.getReverseTransferID());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getInfo1());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getInfo2());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getInfo3());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getInfo4());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getInfo5());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getInfo6());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getInfo7());//70
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getInfo8());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getInfo9());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getInfo10());
                i++;
                pstmtInsert.setString(i, p_c2sTransferVO.getMultiCurrencyDetailVO());//74
                i++;
				pstmtInsert.setLong(i, p_c2sTransferVO.getPromoBonus());//75
                i++;
                if (p_c2sTransferVO.isRoam()&&p_c2sTransferVO.isStopAddnCommission()) {
                    pstmtInsert.setLong(i, p_c2sTransferVO.getRoamPenalty());
                    i++;
                    pstmtInsert.setLong(i, p_c2sTransferVO.getRoamPenaltyOwner());
                	if(p_c2sTransferVO.getOwnerCommProfile()==null)
                		p_c2sTransferVO.setOwnerCommProfile("0");
                	if(BTSLUtil.isNullString(p_c2sTransferVO.getRoamDiffAmount()))
                		p_c2sTransferVO.setRoamDiffAmount("0");
                	else if(Long.parseLong(p_c2sTransferVO.getRoamDiffAmount())<0)
                		p_c2sTransferVO.setRoamDiffAmount("0");
                	 i++;
                	 pstmtInsert.setString(i, p_c2sTransferVO.getRoamDiffAmount()+":"+p_c2sTransferVO.getRoamPenaltyPercentage()+":"+p_c2sTransferVO.getRoamPenaltyPercentageOwner()+":"+p_c2sTransferVO.getOwnerCommProfile());
                }
                addCount = pstmtInsert.executeUpdate();
                addCount = BTSLUtil.getInsertCount(addCount);// added to make code compatible with insertion in partitioned table in postgres DB
               
                pstmtInsert.clearParameters();

                if (addCount > 0) {
                    if (listSize > 0) {
                        for (int k = 0; k < listSize; k++) {
                            itemVO = (C2STransferItemVO) transferItemList.get(k);
                            
                            newItemVO = C2STransferItemVO.getInstance();

                            newItemVO.setTransferID(voucherVO.getTransactionID());
                            newItemVO.setSNo(itemVO.getSNo());
                            newItemVO.setMsisdn(itemVO.getMsisdn());
                            newItemVO.setEntryDate(BTSLUtil.getSQLDateFromUtilDate(itemVO.getEntryDate()));
                            newItemVO.setRequestValue(itemVO.getRequestValue());
                            // this is done as previous and post balance of
                            // sender and receiver will be different for each
                            // request
                            if (PretupsI.USER_TYPE_SENDER.equalsIgnoreCase(itemVO.getUserType())) {
                                // previous balance=previous
                                // blance-((previous-post)/quantity*(j))
                                // post balance=previous
                                // blance-((previous-post)/quantity*(j+1))
                                newItemVO.setPreviousBalance(itemVO.getPreviousBalance() - ((itemVO.getPreviousBalance() - itemVO.getPostBalance()) / size * j));
                                newItemVO.setPostBalance(itemVO.getPreviousBalance() - ((itemVO.getPreviousBalance() - itemVO.getPostBalance()) / size * (j + 1)));
                            } else {
                                newItemVO.setPreviousBalance(itemVO.getPreviousBalance());
                                newItemVO.setPostBalance(itemVO.getPostBalance());
                            }
                            newItemVO.setUserType(itemVO.getUserType());
                            newItemVO.setTransferType(itemVO.getTransferType());
                            newItemVO.setEntryType(itemVO.getEntryType());
                            newItemVO.setValidationStatus(itemVO.getValidationStatus());
                            newItemVO.setUpdateStatus(itemVO.getUpdateStatus());
                            newItemVO.setServiceClass(itemVO.getServiceClass());
                            newItemVO.setProtocolStatus(itemVO.getProtocolStatus());
                            newItemVO.setAccountStatus(itemVO.getAccountStatus());
                            newItemVO.setTransferValue(itemVO.getTransferValue());
                            newItemVO.setInterfaceType(itemVO.getInterfaceType());
                            newItemVO.setInterfaceID(itemVO.getInterfaceID());
                            newItemVO.setInterfaceResponseCode(itemVO.getInterfaceResponseCode());
                            newItemVO.setInterfaceReferenceID(itemVO.getInterfaceReferenceID());
                            newItemVO.setSubscriberType(itemVO.getSubscriberType());
                            newItemVO.setServiceClassCode(itemVO.getServiceClassCode());
                            newItemVO.setPreviousExpiry(BTSLUtil.getSQLDateFromUtilDate(itemVO.getPreviousExpiry()));
                            newItemVO.setNewExpiry(BTSLUtil.getSQLDateFromUtilDate(itemVO.getNewExpiry()));
                            newItemVO.setTransferStatus(itemVO.getTransferStatus());
                            newItemVO.setTransferDate(BTSLUtil.getSQLDateFromUtilDate(itemVO.getTransferDate()));
                            newItemVO.setTransferDateTime(BTSLUtil.getTimestampFromUtilDate(itemVO.getTransferDateTime()));
                            newItemVO.setFirstCall(itemVO.getFirstCall());
                            newItemVO.setEntryDateTime(BTSLUtil.getTimestampFromUtilDate(itemVO.getEntryDateTime()));
                            newItemVO.setPrefixID(itemVO.getPrefixID());
                            newItemVO.setReferenceID(itemVO.getReferenceID());
                            newItemVO.setLanguage(itemVO.getLanguage());
                            newItemVO.setCountry(itemVO.getCountry());
                            newTransferItemList.add(newItemVO);
                        }
                    }
                }
            addCount = 0;
            addCount = addC2STransferItemDetails(p_con, newTransferItemList, p_c2sTransferVO.getTransferID());
            }
            return addCount;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException " );
        	loggerValue.append(sqle.getMessage());
            LOG.error("addTransferDetails", loggerValue );
            addCount = 0;
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[addC2STransferDetails]",
                p_c2sTransferVO.getTransferID(), p_c2sTransferVO.getSenderMsisdn(), p_c2sTransferVO.getSenderNetworkCode(),  loggerValue.toString());
            throw new BTSLBaseException(this, "addTransferDetails", "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            LOG.error("addTransferDetails", "Exception " + e.getMessage());
            addCount = 0;
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[addC2STransferDetails]",
                p_c2sTransferVO.getTransferID(), p_c2sTransferVO.getSenderMsisdn(), p_c2sTransferVO.getSenderNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addTransferDetails", "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting addCount=");
            	loggerValue.append(addCount);
                LOG.debug("addTransferDetails", loggerValue );
            }
        }// end of finally
    }

    /**
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_boolean
     * @return
     * @throws BTSLBaseException
     */
    public int addC2STransferDetails(Connection p_con, C2STransferVO p_c2sTransferVO, boolean p_boolean) throws BTSLBaseException {
        final String methodName = "addC2STransferDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_c2sTransferVO:");
        	loggerValue.append(p_c2sTransferVO.toString());
        	loggerValue.append("p_boolean:" );
        	loggerValue.append(p_boolean);
            LOG.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {

            C2STransferItemVO senderVO = null;
            C2STransferItemVO receiverVO = null;
            C2STransferItemVO creditBackVO = null;
            C2STransferItemVO reconcileVO = null;
           boolean aliasToBeEncrypted = false; 
           aliasToBeEncrypted = ((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.ALIAS_TO_BE_ENCRYPTED))).booleanValue();
            // int item_size=p_c2sTransferVO.getTransferItemList().size();
            final NetworkPrefixVO networkPrefixVO1 = PretupsBL.getNetworkDetails(p_c2sTransferVO.getReceiverMsisdn(), PretupsI.USER_TYPE_RECEIVER);

            int item_size = 0;
            final ArrayList transferItemList = p_c2sTransferVO.getTransferItemList();
            if (transferItemList != null) {
                item_size = p_c2sTransferVO.getTransferItemList().size();
            } else {
                item_size = 0;
            }

            boolean insertIAT = false;
            if (item_size == 0) {
                p_boolean = false;
            } else {
                for (int l = 0, m = p_c2sTransferVO.getTransferItemList().size(); l < m; l++) {
                    final Object obj = p_c2sTransferVO.getTransferItemList().get(l);
                    if (!(obj instanceof C2STransferItemVO)) {
                        if (obj instanceof IATTransferItemVO) {
                            insertIAT = true;
                        }
                        continue;
                    }
                    final C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) obj;
                    if (c2STransferItemVO.getSNo() == 1) {
                        senderVO = c2STransferItemVO;
                    } else if (c2STransferItemVO.getSNo() == 2) {
                        receiverVO = c2STransferItemVO;
                    } else if (c2STransferItemVO.getSNo() > 3 || (c2STransferItemVO.getSNo() == 3 && c2STransferItemVO.getTransferType().equals(PretupsI.TRANSFER_TYPE_RECON))) {
                        reconcileVO = c2STransferItemVO;
                    } else if (c2STransferItemVO.getSNo() == 3) {
                        creditBackVO = c2STransferItemVO;
                    }

                }

            }

            int i = 1;
            String tableName = BTSLUtil.getTableName("c2s_transfers");
            final StringBuffer insertQueryBuff = new StringBuffer(" INSERT INTO ");
            insertQueryBuff.append(tableName).append(" (transfer_id,transfer_date,transfer_date_time,network_code,sender_id,sender_category,product_code,sender_msisdn, ");
            insertQueryBuff.append(" receiver_msisdn,receiver_network_code,transfer_value,error_code,request_gateway_type,request_gateway_code, ");
            insertQueryBuff.append(" grph_domain_code,reference_id,service_type,pin_sent_to_msisdn,language,country,skey,skey_generation_time, ");
            insertQueryBuff.append(" skey_sent_to_msisdn,request_through_queue,quantity,created_by,created_on,modified_by,modified_on,transfer_status, ");
            insertQueryBuff.append(" card_group_set_id,version,card_group_id,sender_transfer_value,receiver_access_fee,receiver_tax1_type, ");
            insertQueryBuff.append(" receiver_tax1_rate,receiver_tax1_value,receiver_tax2_type,receiver_tax2_rate,receiver_tax2_value, ");
            insertQueryBuff.append(" receiver_validity,receiver_transfer_value,receiver_bonus_value,receiver_grace_period,receiver_bonus_validity, ");
            insertQueryBuff.append(" card_group_code,receiver_valperiod_type,temp_transfer_id,transfer_profile_id,commission_profile_id,source_type,sub_service,start_time,end_time,serial_number,ext_credit_intfce_type,ACTIVE_USER_ID ");
            insertQueryBuff.append(" ,subs_sid,cell_id,switch_id ,txn_type, otf_applicable");
            if (networkPrefixVO1 != null && receiverVO == null) {
                insertQueryBuff.append(" ,prefix_id ");
            }

            if (p_boolean) {
                boolean comma_req = false;
                if (senderVO != null) {
                    insertQueryBuff.append(",sender_previous_balance, sender_post_balance,debit_status,service_provider_name,SENDER_PREFIX_ID ");
                    comma_req = true;
                }
                if (receiverVO != null) {
                    if (comma_req) {
                        insertQueryBuff.append(", ");
                    }
                    insertQueryBuff.append(" RECEIVER_PREVIOUS_BALANCE,RECEIVER_POST_BALANCE,transfer_type,validation_status,   ");
                    insertQueryBuff.append(" credit_status,service_class_id,protocol_status,account_status,interface_type,interface_id,  ");
                    insertQueryBuff.append(" interface_response_code,interface_reference_id,subscriber_type,service_class_code, ");
                    insertQueryBuff.append(" msisdn_previous_expiry,msisdn_new_expiry, first_call,prefix_id ,RCVR_INTRFC_REFERENCE_ID");
                    comma_req = true;
                }
                if (creditBackVO != null) {
                    if (comma_req) {
                        insertQueryBuff.append(", ");
                    }
                    insertQueryBuff.append(" SENDER_CR_BK_PREV_BAL,  ");
                    insertQueryBuff.append(" SENDER_CR_BK_POST_BAL,  ");
                    insertQueryBuff.append(" credit_back_status ");
                    comma_req = true;
                }
                if (reconcileVO != null) {
                    if (comma_req) {
                        insertQueryBuff.append(", ");
                    }
                    insertQueryBuff.append(" SENDER_CR_SETL_PREV_BAL,SENDER_CR_SETL_POST_BAL,reconcile_status  ");
                }

            }
            insertQueryBuff.append(",reversal_id,info1,info2,info3,info4,info5,info6,info7,info8,info9,info10,multicurrency_detail ,bonus_amount "); 
            if(p_c2sTransferVO.isRoam()&&p_c2sTransferVO.isStopAddnCommission()){
            	insertQueryBuff.append(",penalty, owner_penalty, penalty_details ");	
            }
            if (networkPrefixVO1 != null && receiverVO == null) {
                insertQueryBuff.append(") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ,?,?");
            } else {
                insertQueryBuff.append(") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ,?,?");
            }

            insertQueryBuff.append(",?,?");

            if (p_boolean) {
                boolean comma_req = false;
                if (senderVO != null) {
                    insertQueryBuff.append(",?,?,?,?,?  ");
                    comma_req = true;
                }
                if (receiverVO != null) {
                    if (comma_req) {
                        insertQueryBuff.append(",");
                    }
                    insertQueryBuff.append("?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ");

                    comma_req = true;
                }
                if (creditBackVO != null) {
                    if (comma_req) {
                        insertQueryBuff.append(",");
                    }
                    insertQueryBuff.append("?, ?, ?");
                    comma_req = true;
                }
                if (reconcileVO != null) {
                    if (comma_req) {
                        insertQueryBuff.append(",");
                    }
                    insertQueryBuff.append("?,?,?,?");
                }

            }
            insertQueryBuff.append(",?,?,?,?,?,?,?,?,?,?,?,?,?");

            if(p_c2sTransferVO.isRoam()&&p_c2sTransferVO.isStopAddnCommission()){
            	insertQueryBuff.append(",?,?, ? ");	
            }
            insertQueryBuff.append(" ) ");

            final String insertQuery = insertQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Insert query:");
            	loggerValue.append(insertQuery);
                LOG.debug(methodName,  loggerValue );
            }

            pstmtInsert = p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(i, p_c2sTransferVO.getTransferID());
            i++;
            pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_c2sTransferVO.getTransferDate()));
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getSQLDateTimeFromUtilDate(new java.sql.Date(System.currentTimeMillis())));
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getNetworkCode());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getSenderID());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getSenderCategoryCode());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getProductCode());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getSenderMsisdn());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getReceiverMsisdn());
            i++;
            if (PretupsI.IAT_TRANSACTION_TYPE.equals(p_c2sTransferVO.getExtCreditIntfceType())) {
                pstmtInsert.setString(i, p_c2sTransferVO.getIatTransferItemVO().getIatRecNWCode());
                i++;
            } else {
                pstmtInsert.setString(i, p_c2sTransferVO.getReceiverNetworkCode());
                i++;
            }
            pstmtInsert.setLong(i, p_c2sTransferVO.getRequestedAmount());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getErrorCode());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getRequestGatewayType());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getRequestGatewayCode());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getGrphDomainCode());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getReferenceID());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getServiceType());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getPinSentToMsisdn());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getLanguage());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getCountry());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getSkey());
            i++;
            if (p_c2sTransferVO.getSkeyGenerationTime() == null) {
                pstmtInsert.setNull(i, Types.TIMESTAMP);
                i++;
            } else {
                pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getSkeyGenerationTime()));
                i++;
            }
            pstmtInsert.setString(i, p_c2sTransferVO.getSkeySentToMsisdn());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getRequestThroughQueue());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getQuantity());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getCreatedBy());
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getCreatedOn()));
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getModifiedBy());
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getModifiedOn()));
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getTransferStatus());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getCardGroupSetID());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getVersion());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getCardGroupID());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getSenderTransferValue());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getReceiverAccessFee());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getReceiverTax1Type());
            i++;
            pstmtInsert.setDouble(i, p_c2sTransferVO.getReceiverTax1Rate());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getReceiverTax1Value());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getReceiverTax2Type());
            i++;
            pstmtInsert.setDouble(i, p_c2sTransferVO.getReceiverTax2Rate());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getReceiverTax2Value());
            i++;
            pstmtInsert.setInt(i, p_c2sTransferVO.getReceiverValidity());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getReceiverTransferValue());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getReceiverBonusValue());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getReceiverGracePeriod());
            i++;
            if (PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL.equals(p_c2sTransferVO.getServiceType())) {
                pstmtInsert.setInt(i, 0);
                i++;
            } else {
                pstmtInsert.setInt(i, p_c2sTransferVO.getReceiverBonusValidity());
                i++;
            }
            pstmtInsert.setString(i, p_c2sTransferVO.getCardGroupCode());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getReceiverValPeriodType());
            i++;
        	pstmtInsert.setString(i, p_c2sTransferVO.getTempId());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getTransferProfileID());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getCommissionProfileSetID());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getSourceType());
            i++;

                pstmtInsert.setString(i, p_c2sTransferVO.getSubService());
                i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getRequestStartTime());
            i++;
            pstmtInsert.setLong(i, System.currentTimeMillis());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getSerialNumber());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getExtCreditIntfceType());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getActiveUserId());
            i++;
            
            if(aliasToBeEncrypted){
          	  if(!BTSLUtil.isNullString(p_c2sTransferVO.getSubscriberSID()))
          		  pstmtInsert.setString(i, new CryptoUtil().encrypt(p_c2sTransferVO.getSubscriberSID(),Constants.KEY));
          	  else
          		  pstmtInsert.setString(i, p_c2sTransferVO.getSubscriberSID());
            }
            else
          	  pstmtInsert.setString(i, p_c2sTransferVO.getSubscriberSID());
            
           
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getCellId());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getSwitchId());
            i++;
            if (BTSLUtil.isNullString(p_c2sTransferVO.getTxnType())) {
                pstmtInsert.setString(i, PretupsI.TXNTYPE_T);
                i++;
            } else {
                pstmtInsert.setString(i, p_c2sTransferVO.getTxnType());
                i++;
            }
            pstmtInsert.setString(i, p_c2sTransferVO.isOtfApplicable());
            i++;
            if (networkPrefixVO1 != null && receiverVO == null) {
                pstmtInsert.setLong(i, networkPrefixVO1.getPrefixID());
                i++;
            }
            if (p_boolean) {
                if (senderVO != null) {
                    pstmtInsert.setLong(i, senderVO.getPreviousBalance());
                    i++;
                    pstmtInsert.setLong(i, senderVO.getPostBalance());
                    i++;
                    pstmtInsert.setString(i, senderVO.getTransferStatus());
                    i++;
                    pstmtInsert.setString(i, senderVO.getServiceProviderName());
                    i++;
                    pstmtInsert.setLong(i, senderVO.getPrefixID());
                    i++;
                }
                if (receiverVO != null) {
                    pstmtInsert.setLong(i, receiverVO.getPreviousBalance());
                    i++;
                    pstmtInsert.setLong(i, receiverVO.getPostBalance());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getTransferType());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getValidationStatus());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getTransferStatus());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getServiceClass());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getProtocolStatus());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getAccountStatus());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getInterfaceType());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getInterfaceID());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getInterfaceResponseCode());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getInterfaceReferenceID());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getSubscriberType());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getServiceClassCode());
                    i++;
                    pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(receiverVO.getPreviousExpiry()));
                    i++;
                    pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(receiverVO.getNewExpiry()));
                    i++;
                    pstmtInsert.setString(i, receiverVO.getFirstCall());
                    i++;
                    pstmtInsert.setLong(i, receiverVO.getPrefixID());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getReferenceID());
                    i++;
                }
                if (creditBackVO != null) {
                    pstmtInsert.setLong(i, creditBackVO.getPreviousBalance());
                    i++;
                    pstmtInsert.setLong(i, creditBackVO.getPostBalance());
                    i++;
                    pstmtInsert.setString(i, creditBackVO.getTransferStatus());
                    i++;
                }
                if (reconcileVO != null) {
                    pstmtInsert.setLong(i, reconcileVO.getPreviousBalance());
                    i++;
                    pstmtInsert.setLong(i, reconcileVO.getPostBalance());
                    i++;
                    pstmtInsert.setString(i, reconcileVO.getTransferStatus());
                    i++;
                }
            }
            pstmtInsert.setString(i, p_c2sTransferVO.getReverseTransferID());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo1());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo2());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo3());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo4());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo5());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo6());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo7());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo8());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo9());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo10());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getMultiCurrencyDetailVO());
            i++;
			  pstmtInsert.setLong(i, p_c2sTransferVO.getPromoBonus());//75
                i++;
            if (p_c2sTransferVO.isRoam()&&p_c2sTransferVO.isStopAddnCommission()) {
                pstmtInsert.setLong(i, p_c2sTransferVO.getRoamPenalty());
                i++;
                pstmtInsert.setLong(i, p_c2sTransferVO.getRoamPenaltyOwner());
            	if(p_c2sTransferVO.getOwnerCommProfile()==null)
            		p_c2sTransferVO.setOwnerCommProfile("0");
            	if(BTSLUtil.isNullString(p_c2sTransferVO.getRoamDiffAmount()))
            		p_c2sTransferVO.setRoamDiffAmount("0");
            	else if(Long.parseLong(p_c2sTransferVO.getRoamDiffAmount())<0)
            		p_c2sTransferVO.setRoamDiffAmount("0");
            	 i++;
            	 pstmtInsert.setString(i, p_c2sTransferVO.getRoamDiffAmount()+":"+p_c2sTransferVO.getRoamPenaltyPercentage()+":"+p_c2sTransferVO.getRoamPenaltyPercentageOwner()+":"+p_c2sTransferVO.getOwnerCommProfile());
            }
            addCount = pstmtInsert.executeUpdate();
            addCount = BTSLUtil.getInsertCount(addCount);// added to make code compatible with insertion in partitioned table in postgres DB


            if (addCount > 0) {
                if (p_c2sTransferVO.getTransferItemList() != null && !p_c2sTransferVO.getTransferItemList().isEmpty() && insertIAT) {
                    addCount = 0;
                    addCount = addC2STransferItemDetails(p_con, p_c2sTransferVO.getTransferItemList(), p_c2sTransferVO.getTransferID(), true);
                }
            }
            return addCount;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName,  loggerValue);
            addCount = 0;
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqle.getMessage());
            
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[addC2STransferDetails]",
                p_c2sTransferVO.getTransferID(), p_c2sTransferVO.getSenderMsisdn(), p_c2sTransferVO.getSenderNetworkCode(),  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	  loggerValue.setLength(0);
              loggerValue.append("Exception:");
              loggerValue.append(e.getMessage());
            LOG.error(methodName, loggerValue );
            addCount = 0;
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[addC2STransferDetails]",
                p_c2sTransferVO.getTransferID(), p_c2sTransferVO.getSenderMsisdn(), p_c2sTransferVO.getSenderNetworkCode(), loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting addCount=");
            	loggerValue.append(addCount);
                LOG.debug(methodName,  loggerValue );
            }
        }// end of finally
    }

    /**
     * Method to add the transfer items of C2S in the database
     * 
     * @param p_con
     * @param transferItemsList
     * @param p_transferID
     * @return int
     * @throws BTSLBaseException
     */
    public int addC2STransferItemDetails(Connection p_con, ArrayList p_transferItemsList, String p_transferID) throws BTSLBaseException {
        final String methodName = "addC2STransferItemDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_transferID:");
        	loggerValue.append(p_transferID);
        	loggerValue.append(" transferItemsList Size=");
        	loggerValue.append(p_transferItemsList.size());
            LOG.debug(methodName,  loggerValue);
        }
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtInsertIat = null;
        int addCount = 0;
        C2STransferItemVO senderVO = null;
        C2STransferItemVO receiverVO = null;
        C2STransferItemVO creditBackVO = null;
        C2STransferItemVO reconcileVO = null;
        final int item_size = p_transferItemsList.size();
        if (item_size == 0) {
            return 0;
        } else {
            for (int l = 0, m = p_transferItemsList.size(); l < m; l++) {
                final Object obj = p_transferItemsList.get(l);
                if (!(obj instanceof C2STransferItemVO)) {
                    continue;
                }
                final C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) obj;
                if (c2STransferItemVO.getSNo() == 1) {
                    senderVO = c2STransferItemVO;
                } else if (c2STransferItemVO.getSNo() == 2) {
                    receiverVO = c2STransferItemVO;
                } else if (c2STransferItemVO.getSNo() > 3 || (c2STransferItemVO.getSNo() == 3 && c2STransferItemVO.getTransferType().equals(PretupsI.TRANSFER_TYPE_RECON))) {
                    reconcileVO = c2STransferItemVO;
                } else if (c2STransferItemVO.getSNo() == 3) {
                    creditBackVO = c2STransferItemVO;
                }

            }

        }
        try {
            // C2STransferItemVO cs2TransferItemVO = null;
            IATTransferItemVO iatTransferItemVO = null;
            int i = 1;
            int itemCount = 1;
            final StringBuffer insertQueryBuff = new StringBuffer();
            // " INSERT INTO c2s_transfer_items (transfer_id, sno,msisdn, entry_date, request_value, previous_balance, post_balance, user_type, transfer_type, entry_type, ");
            // insertQueryBuff.append(" validation_status, update_status, service_class_id,protocol_status,account_status, ");
            // insertQueryBuff.append(" transfer_value, interface_type, interface_id, interface_response_code, ");
            // insertQueryBuff.append(" interface_reference_id, subscriber_type, service_class_code, msisdn_previous_expiry, ");
            // insertQueryBuff.append(" msisdn_new_expiry, transfer_status, transfer_date, transfer_date_time,first_call,entry_date_time,prefix_id,reference_id,language,country) ");
            // insertQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
;
            boolean comma_req = false;
            //local index implemented
            insertQueryBuff.append("update c2s_transfers  set  ");
            if (senderVO != null) {
                insertQueryBuff.append(" sender_msisdn=?, ");
                insertQueryBuff.append(" sender_previous_balance=?,  ");
                insertQueryBuff.append(" sender_post_balance=?,  ");
                insertQueryBuff.append(" debit_status=?,service_provider_name=?,SENDER_PREFIX_ID=? ");//service_provider_name,SENDER_PREFIX_ID
                comma_req = true;
            }
            if (receiverVO != null) {
                if (comma_req) {
                    insertQueryBuff.append(", ");
                }
                insertQueryBuff.append(" receiver_msisdn=?,  ");//validation_status,RCVR_INTRFC_REFERENCE_ID
                insertQueryBuff.append(" RECEIVER_PREVIOUS_BALANCE=?,  ");
                insertQueryBuff.append(" RECEIVER_POST_BALANCE=?,  ");
                insertQueryBuff.append(" transfer_type=?,  ");
                insertQueryBuff.append(" validation_status=?,  ");
                insertQueryBuff.append(" credit_status=?, ");
                insertQueryBuff.append(" service_class_id=?, ");
                insertQueryBuff.append(" protocol_status=?, ");
                insertQueryBuff.append(" account_status=?, ");
                insertQueryBuff.append(" interface_type=?,  ");
                insertQueryBuff.append(" interface_id=?,  ");
                insertQueryBuff.append(" interface_response_code=?, ");
                insertQueryBuff.append(" interface_reference_id=?,  ");
                insertQueryBuff.append(" subscriber_type=?,  ");
                insertQueryBuff.append(" service_class_code=?,  ");
                insertQueryBuff.append(" msisdn_previous_expiry=?, ");
                insertQueryBuff.append(" msisdn_new_expiry=?,  ");
                insertQueryBuff.append(" first_call=?, ");
                insertQueryBuff.append(" prefix_id=?, ");
                insertQueryBuff.append(" reference_id=?,  RCVR_INTRFC_REFERENCE_ID=? ");
                comma_req = true;
            }
            if (creditBackVO != null) {
                if (comma_req) {
                    insertQueryBuff.append(", ");
                }
                insertQueryBuff.append(" SENDER_CR_BK_PREV_BAL=?,  ");
                insertQueryBuff.append(" SENDER_CR_BK_POST_BAL=?,  ");
                insertQueryBuff.append(" credit_back_status=? ");
                comma_req = true;
            }
            if (reconcileVO != null) {
                if (comma_req) {
                    insertQueryBuff.append(", ");
                }
                insertQueryBuff.append(" SENDER_CR_SETL_PREV_BAL=?,  ");
                insertQueryBuff.append(" SENDER_CR_SETL_POST_BAL=?, ");
                insertQueryBuff.append(" reconcile_status=? ");

            }
            insertQueryBuff.append(" where transfer_id=? and TRANSFER_DATE =?");

            String insertQuery = insertQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Update query:");
            	loggerValue.append(insertQuery);
                LOG.debug(methodName,  loggerValue );
            }

            final StringBuffer insertIATQueryBuff = new StringBuffer(" INSERT INTO c2s_iat_transfer_items (transfer_id, rec_country_code, rec_nw_code,");
            insertIATQueryBuff.append("rec_msisdn, notify_msisdn, failed_at, exchange_rate, prov_ratio,");
            insertIATQueryBuff.append("rec_bonus, iat_timestamp, credit_resp_code, credit_msg,");
            insertIATQueryBuff.append("chk_status_resp_code, iat_error_code, iat_message, rec_nw_error_code, rec_nw_message,");
            insertIATQueryBuff
                .append("fees, rcvd_amt, iat_txn_id, sent_amt, sender_id, service_type, transfer_status,transfer_date,transfer_value,quantity,sent_amt_iattorec) ");
            insertIATQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            final String insertIATQuery = insertIATQueryBuff.toString();

            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Insert IAT query:");
            	loggerValue.append(insertIATQuery);
                LOG.debug(methodName, loggerValue );
            }
            
            boolean c2s_transfers_updated = false;
            if (p_transferItemsList != null && !p_transferItemsList.isEmpty()) {
            	pstmtInsert = p_con.prepareStatement(insertQuery);
            	pstmtInsertIat = p_con.prepareStatement(insertIATQuery);
                for (int j = 0, k = p_transferItemsList.size(); j < k; j++) {
                    addCount = 0;
                    i = 1;
                    if (pstmtInsert != null) {
                        pstmtInsert.clearParameters();
                    }
                    if (p_transferItemsList.get(j) instanceof IATTransferItemVO) {
                        iatTransferItemVO = (IATTransferItemVO) p_transferItemsList.get(j);
                        pstmtInsert = pstmtInsertIat;
                        pstmtInsert.clearParameters();
                        pstmtInsert.setString(i, iatTransferItemVO.getIatSenderTxnId());
                        i++;
                        pstmtInsert.setInt(i, iatTransferItemVO.getIatRcvrCountryCode());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatRecNWCode());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatRecMsisdn());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatNotifyMsisdn());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatFailedAt());
                        i++;
                        pstmtInsert.setDouble(i, iatTransferItemVO.getIatExchangeRate());
                        i++;
                        pstmtInsert.setDouble(i, iatTransferItemVO.getIatProvRatio());
                        i++;
                        pstmtInsert.setDouble(i, iatTransferItemVO.getIatReceiverSystemBonus());
                        i++;
                        pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(iatTransferItemVO.getIatTimestamp()));
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatCreditMessage());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatCreditRespCode());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatErrorCode());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatErrorMessage());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatCheckStatusRespCode());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatRcvrNWErrorCode());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatRcvrNWErrorMessage());
                        i++;
                        pstmtInsert.setDouble(i, iatTransferItemVO.getIatFees());
                        i++;
                        pstmtInsert.setDouble(i, iatTransferItemVO.getIatRcvrRcvdAmt());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatTxnId());
                        i++;
                        pstmtInsert.setDouble(i, iatTransferItemVO.getIatReceivedAmount());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getSenderId());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getServiceType());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getTransferStatus());
                        i++;
                        pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(iatTransferItemVO.getSendingNWTimestamp()));
                        i++;
                        pstmtInsert.setLong(i, iatTransferItemVO.getTransferValue());
                        i++;
                        pstmtInsert.setLong(i, iatTransferItemVO.getQuantity());
                        i++;
                        pstmtInsert.setDouble(i, iatTransferItemVO.getIatSentAmtByIAT());
                        i++;
                    } else if (!c2s_transfers_updated) {
                        pstmtInsert = pstmtInsert;
                        pstmtInsert.clearParameters();
                        if (senderVO != null) {
                            pstmtInsert.setString(i, senderVO.getMsisdn());
                            i++;
                            pstmtInsert.setLong(i, senderVO.getPreviousBalance());
                            i++;
                            pstmtInsert.setLong(i, senderVO.getPostBalance());
                            i++;
                            pstmtInsert.setString(i, senderVO.getTransferStatus());
                            i++;
                            pstmtInsert.setString(i, senderVO.getServiceProviderName());
                            i++;
                            pstmtInsert.setLong(i, senderVO.getPrefixID());
                            i++;
                        }
                        if (receiverVO != null) {
                            pstmtInsert.setString(i, receiverVO.getMsisdn());
                            i++;
                            pstmtInsert.setLong(i, receiverVO.getPreviousBalance());
                            i++;
                            pstmtInsert.setLong(i, receiverVO.getPostBalance());
                            i++;
                            pstmtInsert.setString(i, receiverVO.getTransferType());
                            i++;
                            pstmtInsert.setString(i, receiverVO.getValidationStatus());
                            i++;
                            pstmtInsert.setString(i, receiverVO.getTransferStatus());
                            i++;
                            pstmtInsert.setString(i, receiverVO.getServiceClass());
                            i++;
                            pstmtInsert.setString(i, receiverVO.getProtocolStatus());
                            i++;
                            pstmtInsert.setString(i, receiverVO.getAccountStatus());
                            i++;
                            pstmtInsert.setString(i, receiverVO.getInterfaceType());
                            i++;
                            pstmtInsert.setString(i, receiverVO.getInterfaceID());
                            i++;
                            pstmtInsert.setString(i, receiverVO.getInterfaceResponseCode());
                            i++;
                            pstmtInsert.setString(i, receiverVO.getInterfaceReferenceID());
                            i++;
                            pstmtInsert.setString(i, receiverVO.getSubscriberType());
                            i++;
                            pstmtInsert.setString(i, receiverVO.getServiceClassCode());
                            i++;
                            pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(receiverVO.getPreviousExpiry()));
                            i++;
                            pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(receiverVO.getNewExpiry()));
                            i++;
                            pstmtInsert.setString(i, receiverVO.getFirstCall());
                            i++;
                            pstmtInsert.setLong(i, receiverVO.getPrefixID());
                            i++;
                            pstmtInsert.setString(i, receiverVO.getReferenceID());
                            i++;
                            pstmtInsert.setString(i, receiverVO.getReferenceID());
                            i++;
                        }
                        if (creditBackVO != null) {
                            pstmtInsert.setLong(i, creditBackVO.getPreviousBalance());
                            i++;
                            pstmtInsert.setLong(i, creditBackVO.getPostBalance());
                            i++;
                            pstmtInsert.setString(i, creditBackVO.getTransferStatus());
                            i++;
                        }
                        if (reconcileVO != null) {
                            pstmtInsert.setLong(i, reconcileVO.getPreviousBalance());
                            i++;
                            pstmtInsert.setLong(i, reconcileVO.getPostBalance());
                            i++;
                            pstmtInsert.setString(i, reconcileVO.getTransferStatus());
                            i++;
                        }
                        if( receiverVO != null)
                        	pstmtInsert.setString(i, receiverVO.getTransferID());
                        else
                        pstmtInsert.setString(i, p_transferID);
                        i++;
                        pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_transferID)));
                        c2s_transfers_updated = true;
                    } else {
                        addCount = 1;
                        continue;
                    }
                    addCount = pstmtInsert.executeUpdate();
                    pstmtInsert.clearParameters();
                    if (addCount < 0) {
                        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                    }
                    itemCount = itemCount + 1;
                }
            }
            return addCount;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName,  loggerValue);
            addCount = 0;
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[addC2STransferItemDetails]",
                p_transferID, "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	 loggerValue.setLength(0);
         	loggerValue.append("Exception ");
         	loggerValue.append(e.getMessage());
            LOG.error(methodName,  loggerValue);
            addCount = 0;
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:" );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[addC2STransferItemDetails]",
                p_transferID, "", "", loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertIat != null) {
                	pstmtInsertIat.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting addCount=");
            	loggerValue.append(addCount);
                LOG.debug(methodName, loggerValue );
            }
        }// end of finally
    }

    /**
     * Method to add the transfer items of C2S in the database
     * 
     * @param p_con
     * @param transferItemsList
     * @param p_transferID
     * @return int
     * @throws BTSLBaseException
     */
    public int addC2STransferItemDetails(Connection p_con, ArrayList p_transferItemsList, String p_transferID, boolean p_boolean) throws BTSLBaseException {
        final String methodName = "addC2STransferItemDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_transferID:");
        	loggerValue.append(p_transferID);
        	loggerValue.append( " transferItemsList Size=");
        	loggerValue.append(p_transferItemsList.size());
        	loggerValue.append(" p_boolean=");
        	loggerValue.append(p_boolean);
            LOG.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmtInsert = null;
        int addCount = 0;

        try {
            // C2STransferItemVO cs2TransferItemVO = null;
            IATTransferItemVO iatTransferItemVO = null;
            int i = 1;

            // " INSERT INTO c2s_transfer_items (transfer_id, sno,msisdn, entry_date, request_value, previous_balance, post_balance, user_type, transfer_type, entry_type, ");
            // insertQueryBuff.append(" validation_status, update_status, service_class_id,protocol_status,account_status, ");
            // insertQueryBuff.append(" transfer_value, interface_type, interface_id, interface_response_code, ");
            // insertQueryBuff.append(" interface_reference_id, subscriber_type, service_class_code, msisdn_previous_expiry, ");
            // insertQueryBuff.append(" msisdn_new_expiry, transfer_status, transfer_date, transfer_date_time,first_call,entry_date_time,prefix_id,reference_id,language,country) ");
            // insertQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

            final StringBuffer insertIATQueryBuff = new StringBuffer(" INSERT INTO c2s_iat_transfer_items (transfer_id, rec_country_code, rec_nw_code,");
            insertIATQueryBuff.append("rec_msisdn, notify_msisdn, failed_at, exchange_rate, prov_ratio,");
            insertIATQueryBuff.append("rec_bonus, iat_timestamp, credit_resp_code, credit_msg,");
            insertIATQueryBuff.append("chk_status_resp_code, iat_error_code, iat_message, rec_nw_error_code, rec_nw_message,");
            insertIATQueryBuff
                .append("fees, rcvd_amt, iat_txn_id, sent_amt, sender_id, service_type, transfer_status,transfer_date,transfer_value,quantity,sent_amt_iattorec) ");
            insertIATQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            final String insertIATQuery = insertIATQueryBuff.toString();

            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Insert IAT query:");
            	loggerValue.append(insertIATQuery);
                LOG.debug(methodName, loggerValue);
            }

            String insertQuery = insertIATQuery;
            pstmtInsert = p_con.prepareStatement(insertQuery);
            if (p_transferItemsList != null && !p_transferItemsList.isEmpty()) {
                for (int j = 0, k = p_transferItemsList.size(); j < k; j++) {
                    addCount = 0;
                    i = 1;
                    if (pstmtInsert != null) {
                        pstmtInsert.clearParameters();
                    }
                    if (p_transferItemsList.get(j) instanceof IATTransferItemVO) {
                        iatTransferItemVO = (IATTransferItemVO) p_transferItemsList.get(j);
                        pstmtInsert.clearParameters();
                        pstmtInsert.setString(i, iatTransferItemVO.getIatSenderTxnId());
                        i++;
                        pstmtInsert.setInt(i, iatTransferItemVO.getIatRcvrCountryCode());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatRecNWCode());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatRecMsisdn());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatNotifyMsisdn());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatFailedAt());
                        i++;
                        pstmtInsert.setDouble(i, iatTransferItemVO.getIatExchangeRate());
                        i++;
                        pstmtInsert.setDouble(i, iatTransferItemVO.getIatProvRatio());
                        i++;
                        pstmtInsert.setDouble(i, iatTransferItemVO.getIatReceiverSystemBonus());
                        i++;
                        pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(iatTransferItemVO.getIatTimestamp()));
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatCreditMessage());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatCreditRespCode());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatErrorCode());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatErrorMessage());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatCheckStatusRespCode());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatRcvrNWErrorCode());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatRcvrNWErrorMessage());
                        i++;
                        pstmtInsert.setDouble(i, iatTransferItemVO.getIatFees());
                        i++;
                        pstmtInsert.setDouble(i, iatTransferItemVO.getIatRcvrRcvdAmt());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getIatTxnId());
                        i++;
                        pstmtInsert.setDouble(i, iatTransferItemVO.getIatReceivedAmount());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getSenderId());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getServiceType());
                        i++;
                        pstmtInsert.setString(i, iatTransferItemVO.getTransferStatus());
                        i++;
                        pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(iatTransferItemVO.getSendingNWTimestamp()));
                        i++;
                        pstmtInsert.setLong(i, iatTransferItemVO.getTransferValue());
                        i++;
                        pstmtInsert.setLong(i, iatTransferItemVO.getQuantity());
                        i++;
                        pstmtInsert.setDouble(i, iatTransferItemVO.getIatSentAmtByIAT());
                        i++;
                    } else {

                        addCount = 1;
                        continue;
                    }
                    addCount = pstmtInsert.executeUpdate();
                    pstmtInsert.clearParameters();
                    if (addCount < 0) {
                        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                    }
                }
            }
            return addCount;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName, loggerValue );
            addCount = 0;
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[addC2STransferItemDetails]",
                p_transferID, "", "", loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            LOG.error(methodName,  loggerValue);
            addCount = 0;
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:" );
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[addC2STransferItemDetails]",
                p_transferID, "", "", loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting addCount=");
            	loggerValue.append(addCount);
                LOG.debug(methodName,  loggerValue );
            }
        }// end of finally
    }

    /**
     * Method to update the transfer details of C2S in the database
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @return
     * @throws BTSLBaseException
     */
    public int updateC2STransferDetails(Connection p_con, C2STransferVO p_c2sTransferVO) throws BTSLBaseException {
        final String methodName = "updateC2STransferDetails"; 
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_c2sTransferVO:");
        	loggerValue.append(p_c2sTransferVO.toString());
            LOG.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            int i = 1;
            String tableName = BTSLUtil.getTableName("c2s_transfers");
            //local index implemented
            final StringBuffer updateQueryBuff = new StringBuffer(" UPDATE ");
            updateQueryBuff.append(tableName).append(" SET ");
            updateQueryBuff
                .append(" reference_id=?, error_code=?,differential_applicable=?,differential_given=?, credit_back_status=?,modified_on=?,modified_by=?,transfer_status=? ,end_time=? ,pin_sent_to_msisdn=?, bonus_details=? , ");
            updateQueryBuff.append(" RECEIVER_PREVIOUS_BALANCE=?, RECEIVER_POST_BALANCE=?, ");
            updateQueryBuff.append(" validation_status=?, credit_status=?, account_status=?, interface_response_code=? , msisdn_previous_expiry=?, ");
            updateQueryBuff.append(" msisdn_new_expiry=?, first_call=?, interface_reference_id=?,protocol_status=?, ");
            updateQueryBuff.append(" adjust_dr_txn_type=?, adjust_dr_txn_id=?, adjust_dr_update_status=?, ");
            updateQueryBuff.append(" adjust_cr_txn_type=?, adjust_cr_txn_id=?, adjust_cr_update_status=? , adjust_value=?, ");
            updateQueryBuff.append(" promo_previous_balance=?, promo_post_balance=?, promo_previous_expiry=?, promo_new_expiry=? ");
            updateQueryBuff.append(",promo_status=?, interface_promo_response_code=?,cos_status=?, interface_cos_response_code=?, new_service_class_code=?");
            if (!BTSLUtil.isNullString(p_c2sTransferVO.getExtCreditIntfceType())) {
                updateQueryBuff.append(" ,ext_credit_intfce_type=? ");
            }
            updateQueryBuff.append(" ,lms_profile=?, lms_version=? ");
            if (!BTSLUtil.isNullString(p_c2sTransferVO.isOtfApplicable())) {
                updateQueryBuff.append(" ,otf_applicable=? ");
            }
            updateQueryBuff.append(" WHERE transfer_id=? and transfer_date=? ");
            final String updateQuery = updateQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Insert query:" + updateQuery + " " + p_c2sTransferVO.getReferenceID() + " " + p_c2sTransferVO.getErrorCode() + " " + p_c2sTransferVO
                    .getDifferentialApplicable() + " " + p_c2sTransferVO.getDifferentialGiven() + " " + p_c2sTransferVO.getCreditBackStatus() + " " + p_c2sTransferVO
                    .getModifiedBy()+ " " + p_c2sTransferVO.getTransferID() + " " + p_c2sTransferVO.getTransferDateTime()+ " " + p_c2sTransferVO.getTransferDate()+ " " + p_c2sTransferVO.getNetworkCode());
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(i, p_c2sTransferVO.getReferenceID());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getErrorCode());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getDifferentialApplicable());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getDifferentialGiven());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getCreditBackStatus());
            i++;
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getModifiedOn()));
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getModifiedBy());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getTransferStatus());
            i++;
            pstmtUpdate.setLong(i, System.currentTimeMillis());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getPinSentToMsisdn());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getBonusSummarySting());
            i++;
            //final C2STransferItemVO receiverItemVO = (C2STransferItemVO) p_c2sTransferVO.getTransferItemList().get(1);
            pstmtUpdate.setLong(i, p_c2sTransferVO.getPreviousBalance());
            i++;
            pstmtUpdate.setLong(i, p_c2sTransferVO.getPostBalance());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getValidationStatus());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getUpdateStatus());
            i++;
            pstmtUpdate.setString(i, BTSLUtil.NullToString(p_c2sTransferVO.getAccountStatus()));
            i++;
            pstmtUpdate.setString(i, BTSLUtil.NullToString(p_c2sTransferVO.getInterfaceResponseCode()));
            i++;
            if (p_c2sTransferVO.getPreviousExpiry() != null) {
                pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_c2sTransferVO.getPreviousExpiry()));
                i++;
            } else {
                pstmtUpdate.setNull(i, Types.DATE);
                i++;
            }
            if (p_c2sTransferVO.getNewExpiry() != null) {
                pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_c2sTransferVO.getNewExpiry()));
                i++;
            } else {
                pstmtUpdate.setNull(i, Types.DATE);
                i++;
            }
            pstmtUpdate.setString(i, p_c2sTransferVO.getFirstCall());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getInterfaceReferenceId());
            i++;
            pstmtUpdate.setString(i, BTSLUtil.NullToString(p_c2sTransferVO.getProtocolStatus()));
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getTransferType2());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getInterfaceReferenceID2());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getUpdateStatus2());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getTransferType1());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getInterfaceReferenceID1());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getUpdateStatus1());
            i++;
            pstmtUpdate.setLong(i, p_c2sTransferVO.getAdjustValue());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getPreviousPromoBalance());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getNewPromoBalance());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getPreviousPromoExpiry());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getNewPromoExpiry());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getPromoStatus());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getInterfacePromoStatus());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getCosStatus());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getInterfacePromoStatus());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getNewServiceClssCode());
            i++;
                if (!BTSLUtil.isNullString(p_c2sTransferVO.getExtCreditIntfceType())) {
                pstmtUpdate.setString(i, p_c2sTransferVO.getExtCreditIntfceType());
                i++;
            }
            pstmtUpdate.setString(i, p_c2sTransferVO.getLmsProfile());
            i++;
            if (p_c2sTransferVO.getLmsVersion() != null) {
                pstmtUpdate.setString(i, p_c2sTransferVO.getLmsVersion());
                i++;
            } else {
                pstmtUpdate.setString(i, "");
                i++;
            } if (!BTSLUtil.isNullString(p_c2sTransferVO.isOtfApplicable())) {
            	pstmtUpdate.setString(i, p_c2sTransferVO.isOtfApplicable());
            	i++;
            }
            pstmtUpdate.setString(i, p_c2sTransferVO.getTransferID());
            i++;
            pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_c2sTransferVO.getTransferID())));
            updateCount = pstmtUpdate.executeUpdate();
            updateCount=BTSLUtil.getInsertCount(updateCount);
            if (updateCount > 0) {
                if ("IAT".equalsIgnoreCase(p_c2sTransferVO.getExtCreditIntfceType())) {
                    updateCount = 0;
                    updateCount = updateC2SIATTransferItemDetails(p_con, (IATTransferItemVO) p_c2sTransferVO.getTransferItemList().get(2), p_c2sTransferVO.getTransferID());
                }
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName,  loggerValue );
            updateCount = 0;
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[updateC2STransferDetails]",
                p_c2sTransferVO.getTransferID(), p_c2sTransferVO.getSenderMsisdn(), p_c2sTransferVO.getNetworkCode(),  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            LOG.error(methodName, loggerValue );
            updateCount = 0;
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[updateC2STransferDetails]",
                p_c2sTransferVO.getTransferID(), p_c2sTransferVO.getSenderMsisdn(), p_c2sTransferVO.getNetworkCode(), loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting updateCount=");
            	loggerValue.append(updateCount);
                LOG.debug(methodName,  loggerValue );
            }
        }// end of finally
    }

    /**
     * Method to update the transfer details of C2S in the database
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_transferIdList
     * @return updateCount
     * @author vipul
     * @throws BTSLBaseException
     */

    public int updateC2STransferDetails(Connection p_con, C2STransferVO p_c2sTransferVO, String p_transferId) throws BTSLBaseException {

        final String methodName = "updateC2STransferDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_c2sTransferVO:");
        	loggerValue.append(p_c2sTransferVO.toString());
        	loggerValue.append("p_transferId=");
        	loggerValue.append(p_transferId); 
        	LOG.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        final ChannelUserVO senderVO = (ChannelUserVO) p_c2sTransferVO.getSenderVO();
        try {
            int i = 1;
            String tableName = BTSLUtil.getTableName("c2s_transfers");
            //local index implemented
            final StringBuffer updateQueryBuff = new StringBuffer(" UPDATE ");
            updateQueryBuff.append(tableName).append(" SET ");
            updateQueryBuff
                .append(" reference_id=?, error_code=?,differential_applicable=?,differential_given=?, credit_back_status=?,modified_on=?,modified_by=?,transfer_status=? ,end_time=? ,pin_sent_to_msisdn=?, bonus_details=? , ");
            updateQueryBuff.append(" RECEIVER_PREVIOUS_BALANCE=?, RECEIVER_POST_BALANCE=?, ");
            updateQueryBuff.append(" validation_status=?, credit_status=?, account_status=?, interface_response_code=? , msisdn_previous_expiry=?, ");
            updateQueryBuff.append(" msisdn_new_expiry=?, first_call=?, interface_reference_id=?,protocol_status=?, ");
            updateQueryBuff.append(" adjust_dr_txn_type=?, adjust_dr_txn_id=?, adjust_dr_update_status=?, ");
            updateQueryBuff.append(" adjust_cr_txn_type=?, adjust_cr_txn_id=?, adjust_cr_update_status=? , adjust_value=?, ");
            updateQueryBuff.append(" promo_previous_balance=?, promo_post_balance=?, promo_previous_expiry=?, promo_new_expiry=? ");
            updateQueryBuff.append(",promo_status=?, interface_promo_response_code=?,cos_status=?, interface_cos_response_code=?, new_service_class_code=?");
            if (!BTSLUtil.isNullString(p_c2sTransferVO.getExtCreditIntfceType())) {
                updateQueryBuff.append(" ,ext_credit_intfce_type=? ");
            }
            updateQueryBuff.append(" ,lms_profile=?, lms_version=? ");
            updateQueryBuff.append(" ,subscriber_type=? ");
            if (!BTSLUtil.isNullString(p_c2sTransferVO.isOtfApplicable())) {
                updateQueryBuff.append(" ,otf_applicable=? ");
            }
            updateQueryBuff.append(" WHERE transfer_id=? and transfer_date=? ");
            final String updateQuery = updateQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Insert query:");
            	loggerValue.append(updateQuery);
            	loggerValue.append(" ");
            	loggerValue.append(p_c2sTransferVO.getReferenceID());
            	loggerValue.append(" ");
            	loggerValue.append(p_c2sTransferVO.getErrorCode());
            	loggerValue.append(" " );
            	loggerValue.append(p_c2sTransferVO.getDifferentialApplicable());
            	loggerValue.append(" ");
            	loggerValue.append(p_c2sTransferVO.getDifferentialGiven());
            	loggerValue.append(" ");
            	loggerValue.append(p_c2sTransferVO.getCreditBackStatus());
            	loggerValue.append(" " );
            	loggerValue.append(p_c2sTransferVO.getModifiedBy());
            	loggerValue.append( " ");
            	loggerValue.append(p_c2sTransferVO.getTransferID());
            	loggerValue.append(" ");
            	loggerValue.append(p_c2sTransferVO.getTransferDateTime());
            	loggerValue.append(" ");
            	loggerValue.append(p_c2sTransferVO.getTransferDate());
            	loggerValue.append(" ");
            	loggerValue.append(p_c2sTransferVO.getNetworkCode());
                LOG.debug(methodName,  loggerValue );
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(i, p_c2sTransferVO.getReferenceID());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getErrorCode());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getDifferentialApplicable());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getDifferentialGiven());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getCreditBackStatus());
            i++;
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getModifiedOn()));
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getModifiedBy());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getTransferStatus());
            i++;
            pstmtUpdate.setLong(i, System.currentTimeMillis());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getPinSentToMsisdn());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getBonusSummarySting());
            i++;
            final C2STransferItemVO receiverItemVO = (C2STransferItemVO) p_c2sTransferVO.getTransferItemList().get(1);
            pstmtUpdate.setLong(i, receiverItemVO.getPreviousBalance());
            i++;
            pstmtUpdate.setLong(i, receiverItemVO.getPostBalance());
            i++;
            pstmtUpdate.setString(i, receiverItemVO.getValidationStatus());
            i++;
            pstmtUpdate.setString(i, receiverItemVO.getUpdateStatus());
            i++;
            pstmtUpdate.setString(i, BTSLUtil.NullToString(receiverItemVO.getAccountStatus()));
            i++;
            pstmtUpdate.setString(i, BTSLUtil.NullToString(receiverItemVO.getInterfaceResponseCode()));
            i++;
            if (receiverItemVO.getPreviousExpiry() != null) {
                pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(receiverItemVO.getPreviousExpiry()));
                i++;
            } else {
                pstmtUpdate.setNull(i, Types.DATE);
                i++;
            }
            if (receiverItemVO.getNewExpiry() != null) {
                pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(receiverItemVO.getNewExpiry()));
                i++;
            } else {
                pstmtUpdate.setNull(i, Types.DATE);
                i++;
            }
            pstmtUpdate.setString(i, receiverItemVO.getFirstCall());
            i++;
            pstmtUpdate.setString(i, receiverItemVO.getInterfaceReferenceID());
            i++;
            pstmtUpdate.setString(i, BTSLUtil.NullToString(receiverItemVO.getProtocolStatus()));
            i++;
            pstmtUpdate.setString(i, receiverItemVO.getTransferType2());
            i++;
            pstmtUpdate.setString(i, receiverItemVO.getInterfaceReferenceID2());
            i++;
            pstmtUpdate.setString(i, receiverItemVO.getUpdateStatus2());
            i++;
            pstmtUpdate.setString(i, receiverItemVO.getTransferType1());
            i++;
            pstmtUpdate.setString(i, receiverItemVO.getInterfaceReferenceID1());
            i++;
            pstmtUpdate.setString(i, receiverItemVO.getUpdateStatus1());
            i++;
            pstmtUpdate.setLong(i, receiverItemVO.getAdjustValue());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getPreviousPromoBalance());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getNewPromoBalance());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getPreviousPromoExpiry());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getNewPromoExpiry());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getPromoStatus());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getInterfacePromoStatus());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getCosStatus());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getInterfacePromoStatus());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getNewServiceClssCode());
            i++;
                if (!BTSLUtil.isNullString(p_c2sTransferVO.getExtCreditIntfceType())) {
                pstmtUpdate.setString(i, p_c2sTransferVO.getExtCreditIntfceType());
                i++;
            }
            if (senderVO.getLmsProfile() != null) {
                pstmtUpdate.setString(i, senderVO.getLmsProfile());
                i++;
            } else {
                pstmtUpdate.setString(i, "");
                i++;
            }
            if (p_c2sTransferVO.getLmsVersion() != null) {
                pstmtUpdate.setString(i, p_c2sTransferVO.getLmsVersion());
                i++;
            } else {
                pstmtUpdate.setString(i, "");
                i++;
            }// END
            if (p_c2sTransferVO.getReceiverSubscriberType() != null) {
                pstmtUpdate.setString(i, p_c2sTransferVO.getReceiverSubscriberType());
                i++;
            } else {
                pstmtUpdate.setString(i, "");
                i++;
            }
            if (!BTSLUtil.isNullString(p_c2sTransferVO.isOtfApplicable())) {
            	  pstmtUpdate.setString(i, p_c2sTransferVO.isOtfApplicable());
            	  i++;
            }
            pstmtUpdate.setString(i, p_transferId);
            i++;
            pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_transferId)));
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0) {
                if ("IAT".equalsIgnoreCase(p_c2sTransferVO.getExtCreditIntfceType())) {
                    updateCount = 0;
                    updateCount = updateC2SIATTransferItemDetails(p_con, (IATTransferItemVO) p_c2sTransferVO.getTransferItemList().get(2), p_c2sTransferVO.getTransferID());
                }
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append( "SQLException ");
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName,loggerValue);
            updateCount = 0;
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
        	loggerValue.append( "SQL Exception:" );
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[updateC2STransferDetails]",
                p_c2sTransferVO.getTransferID(), p_c2sTransferVO.getSenderMsisdn(), p_c2sTransferVO.getNetworkCode(), loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            LOG.error(methodName,  loggerValue );
            updateCount = 0;
            LOG.errorTrace(methodName, e);
        	loggerValue.setLength(0);
        	loggerValue.append("Exception :");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[updateC2STransferDetails]",
                p_c2sTransferVO.getTransferID(), p_c2sTransferVO.getSenderMsisdn(), p_c2sTransferVO.getNetworkCode(), loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting updateCount=");
            	loggerValue.append(updateCount);
                LOG.debug(methodName,  loggerValue );
            }
        }// end of finally
    
    	
    	
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
     * @param p_networkCode
     *            String
     * @param p_fromDate
     *            Date
     * @param p_toDate
     *            Date
     * @param p_senderMsisdn
     *            String
     * @param p_receiverMsisdn
     *            String
     * @param p_transferID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadC2STransferVOList(Connection p_con, String p_networkCode, Date p_fromDate, Date p_toDate, String p_senderMsisdn, String p_receiverMsisdn, String p_transferID, String p_serviceType) throws BTSLBaseException {

        final String methodName = "loadC2STransferVOList";
        
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append( "Entered p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(", p_senderMsisdn:" );
        	loggerValue.append(p_senderMsisdn);
        	loggerValue.append(" p_fromDate:");
        	loggerValue.append(p_fromDate);
        	loggerValue.append(" p_toDate: ");
        	loggerValue.append(p_toDate);
        	loggerValue.append("'p_receiverMsisdn=");
        	loggerValue.append(p_receiverMsisdn);
        	loggerValue.append(",p_transferID=");
        	loggerValue.append(p_transferID);
        	loggerValue.append(",p_serviceType=");
        	loggerValue.append(p_serviceType);
            LOG.debug( methodName,loggerValue );
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferVO c2sTransferVO = null;
        final ArrayList c2sTransferVOList = new ArrayList();
        UserPhoneVO phoneVo = null;
        String isSenderPrimary = null;
        try {
        	boolean SECONDARY_NUMBER_ALLOWED = false;
        	SECONDARY_NUMBER_ALLOWED = ((Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)))
					.booleanValue();
            if (SECONDARY_NUMBER_ALLOWED) {
                final UserDAO userDAO = new UserDAO();
                if (!BTSLUtil.isNullString(p_senderMsisdn)) {
                    phoneVo = userDAO.loadUserAnyPhoneVO(p_con, p_senderMsisdn);
                    if (phoneVo != null) {
                        isSenderPrimary = phoneVo.getPrimaryNumber();
                    }
                }
            }
            

            pstmtSelect = c2STransferQry.loadC2STransferVOListQry(p_con,p_fromDate,p_toDate,p_senderMsisdn,isSenderPrimary,p_receiverMsisdn,p_transferID, p_networkCode,p_serviceType,phoneVo );
            rs = pstmtSelect.executeQuery();
            CryptoUtil cryptoutil = CryptoUtil.getInstance();
            final ArrayList sourceTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSACTION_SOURCE_TYPE, true);
           boolean aliasToBeEncrypted = false; 
           aliasToBeEncrypted = ((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.ALIAS_TO_BE_ENCRYPTED))).booleanValue();
            while (rs.next()) {
                c2sTransferVO = new C2STransferVO();

                c2sTransferVO.setProductName(rs.getString("short_name"));
                c2sTransferVO.setServiceName(rs.getString("name"));
                c2sTransferVO.setSenderName(rs.getString("user_name"));
                c2sTransferVO.setPromoBonus(rs.getLong("bonus_amount"));

                c2sTransferVO.setErrorMessage(rs.getString("errcode"));
                c2sTransferVO.setTransferID(rs.getString("transfer_id"));
                c2sTransferVO.setTransferDate(rs.getDate("transfer_date"));
                c2sTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                c2sTransferVO.setTransferDateStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time"))));                       
                c2sTransferVO.setNetworkCode(rs.getString("network_code"));
                c2sTransferVO.setNetworkName(rs.getString("network_name"));
                c2sTransferVO.setSenderID(rs.getString("sender_id"));
                // c2sTransferVO.setSenderCategory(rs.getString("sender_category"));
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
                // c2sTransferVO.setSubService(BTSLUtil.getOptionDesc(rs.getString("sub_service"),subServiceTypeList).getLabel());
                // Changed on 27/05/07 For service type selector Mapping
                c2sTransferVO.setSubService(PretupsBL.getSelectorDescriptionFromCode(c2sTransferVO.getServiceType() + "_" + rs.getString("sub_service")));
                c2sTransferVO.setSerialNumber(rs.getString("serial_number"));
                c2sTransferVO.setSubscriberSID(rs.getString("subs_sid"));
               
                
                if (!BTSLUtil.isNullString(rs.getString("subs_sid"))) {
                    c2sTransferVO.setReceiverMsisdn(rs.getString("subs_sid"));
                    // added for cell id and switch id
                    // if(SystemPreferences.CELL_ID_SWITCH_ID_REQUIRED)
                }
                try{
                    if(aliasToBeEncrypted){
                          if(!BTSLUtil.isNullString(c2sTransferVO.getSubscriberSID()) && !c2sTransferVO.getSubscriberSID().matches("[0-9]+")){
                              c2sTransferVO.setSubscriberSID(cryptoutil.decrypt(c2sTransferVO.getSubscriberSID(),Constants.KEY));
                          c2sTransferVO.setReceiverMsisdn(c2sTransferVO.getSubscriberSID());
                          }
                    }
                }catch(Exception e){
                	loggerValue.setLength(0);
                	loggerValue.append("SQLException " );
                	loggerValue.append(e.getMessage());
                     LOG.error(methodName, loggerValue );
                     LOG.errorTrace(methodName, e);
                }

                // added for C2S_Bonuses removal
                if (_operatorUtilI.getNewDataAftrTbleMerging(p_fromDate, p_toDate))

                {
                    c2sTransferVO.setCellId(rs.getString("cell_id"));
                    c2sTransferVO.setSwitchId(rs.getString("switch_id"));
                    c2sTransferVO.setReverseTransferID(rs.getString("reversal_id"));
                    c2sTransferVO.setInfo1(rs.getString("info1"));
                    c2sTransferVO.setInfo2(rs.getString("info2"));
                    c2sTransferVO.setInfo3(rs.getString("info3"));
                    c2sTransferVO.setInfo4(rs.getString("info4"));
                    c2sTransferVO.setInfo5(rs.getString("info5"));
                    c2sTransferVO.setInfo6(rs.getString("info6"));
                    c2sTransferVO.setInfo7(rs.getString("info7"));
                    c2sTransferVO.setInfo8(rs.getString("info8"));
                    c2sTransferVO.setInfo9(rs.getString("info9"));
                    c2sTransferVO.setInfo10(rs.getString("info10"));
                    c2sTransferVO.setExtCreditIntfceType(rs.getString("ext_credit_intfce_type"));
                    c2sTransferVO.setMultiCurrencyDetailVO(rs.getString("multicurrency_detail"));

                	c2sTransferVO.setBonusSummarySting(rs.getString("bonus_details"));
                    c2sTransferVO.setPreviousPromoBalance(rs.getString("promo_previous_balance"));
                    c2sTransferVO.setNewPromoBalance(rs.getString("promo_post_balance"));
                    c2sTransferVO.setPreviousPromoExpiry(rs.getString("promo_previous_expiry"));
                    c2sTransferVO.setNewPromoExpiry(rs.getString("promo_new_expiry"));
                }

                c2sTransferVOList.add(c2sTransferVO);
            }

        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException " );
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2STransferVOList]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (BTSLBaseException be) {
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"TransferDAO[loadC2STransferVOList]","","","","BTSL BaseException:"+be.getMessage());
            throw be;
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append( "Exception ");
        	loggerValue.append(e.getMessage());
            LOG.error(methodName, loggerValue );
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append( "Exception ");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2STransferVOList]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting c2sTransferVOList.size()=");
            	loggerValue.append(c2sTransferVOList.size());
                LOG.debug(methodName,  loggerValue);
            }
        }// end of finally

        return c2sTransferVOList;
    }

    /**
     * Method loadC2SRulesListForChannelUserAssociation.
     * 
     * @param p_con
     *            Connection
     * 
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadC2SRulesListForChannelUserAssociation(Connection p_con, String p_networkCode) throws BTSLBaseException {

        final String methodName = "loadC2SRulesListForChannelUserAssociation";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("p_networkCode : ");
        	loggerValue.append(p_networkCode);
            LOG.debug(methodName, loggerValue );
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelTransferRuleVO channelTransferRuleVO = null;
        final ArrayList list = new ArrayList();
        try {
            final StringBuffer selectQueryBuff = new StringBuffer();

            selectQueryBuff.append("SELECT DISTINCT from_category,to_category ");
            selectQueryBuff.append("FROM chnl_transfer_rules  ");
            selectQueryBuff.append("WHERE type = ? AND parent_association_allowed = ? AND network_code = ? AND status !='N'");
            final String selectQuery = selectQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("select query:" );
            	loggerValue.append(selectQuery);
                LOG.debug(methodName, loggerValue );
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);

            pstmtSelect.setString(1, PretupsI.TRANSFER_RULE_TYPE_CHANNEL);
            pstmtSelect.setString(2, TypesI.YES);
            pstmtSelect.setString(3, p_networkCode);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                channelTransferRuleVO = new ChannelTransferRuleVO();

                channelTransferRuleVO.setFromCategory(rs.getString("from_category"));
                channelTransferRuleVO.setToCategory(rs.getString("to_category"));

                list.add(channelTransferRuleVO);
            }

        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName,  loggerValue );
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "C2STransferDAO[loadC2SRulesListForChannelUserAssociation]", "", "", "",  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "C2STransferDAO[loadC2SRulesListForChannelUserAssociation]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting List size=" );
            	loggerValue.append(list.size());
                LOG.debug(methodName,loggerValue );
            }
        }// end of finally

        return list;
    }

    /**
     * Method updateReconcilationStatus.
     * This method update the C2S reconciliation parameters.
     * 
     * @param p_con
     *            Connection
     * @param p_c2sTransferVO
     *            C2SReconciliationVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateReconcilationStatus(Connection p_con, C2STransferVO p_c2sTransferVO) throws BTSLBaseException {
        final String methodName = "updateReconcilationStatus";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:p_c2sTransferVO=");
        	loggerValue.append(p_c2sTransferVO);
            LOG.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate2 = null;
        int updateCount = 0;
        try {
            
            String query = c2STransferQry.updateReconcilationStatusQry(p_c2sTransferVO);
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query=");
            	loggerValue.append(query);
                LOG.debug(methodName,  loggerValue );
            }

            pstmtUpdate = p_con.prepareStatement(query);
            int i = 1;
            pstmtUpdate.setString(i, p_c2sTransferVO.getTransferStatus());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getModifiedBy());
            i++;
            pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_c2sTransferVO.getModifiedOn()));
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getModifiedBy());
            i++;
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getModifiedOn()));
            i++;
            // By sandeep goel ID RECON001
            // to update the credit back status
            pstmtUpdate.setString(i, p_c2sTransferVO.getCreditBackStatus());
            i++;
            // ends here
			
			if(!(BTSLUtil.isNullString(p_c2sTransferVO.getDifferentialApplicable()))){
            	 pstmtUpdate.setString(i, p_c2sTransferVO.getDifferentialApplicable());
            	 i++;
            	 pstmtUpdate.setString(i, p_c2sTransferVO.getDifferentialGiven());
            	 i++;
            }
			pstmtUpdate.setString(i, p_c2sTransferVO.isOtfApplicable());
			i++;
            pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_c2sTransferVO.getTransferID())));
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getTransferID());
            i++;
            // By sandeep ID REC001
            // to perform the check "is Already modify"
            pstmtUpdate.setString(i, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            i++;
            pstmtUpdate.setString(i, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            i++;
            updateCount = pstmtUpdate.executeUpdate();

            if (updateCount <= 0) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2STransferDAO[updateReconcilationStatus]", "", "",
                    "", "Record is already modified Txn ID=" + p_c2sTransferVO.getTransferID());
            } else // if(updateCount>=1)
            {
                if (!p_c2sTransferVO.getTransferItemList().equals(null) && !p_c2sTransferVO.getTransferItemList().isEmpty()) {
                    updateCount = 0;
                    updateCount = addC2STransferItemDetails(p_con, p_c2sTransferVO.getTransferItemList(), p_c2sTransferVO.getTransferID());
                }
            }
            // added by satakshi to remove reversal id from original transaction
            // record if ambiguous reversal is settled by marking fail
            if (p_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL) && p_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL) && updateCount > 0) {
                updateCount = 0;
                final StringBuffer updateQuery2 = new StringBuffer();
                //local index implemented
                updateQuery2.append("UPDATE c2s_transfers SET modified_on=?,modified_by=?,reversal_id = null ");
                updateQuery2.append("WHERE transfer_id=? and transfer_date=? ");
                final String query2 = updateQuery2.toString();
                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("Query=");
                	loggerValue.append(query2);
                    LOG.debug(methodName,loggerValue);
                }
                pstmtUpdate2 = p_con.prepareStatement(query2);
                int j = 1;
                pstmtUpdate2.setTimestamp(j++, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getModifiedOn()));
                pstmtUpdate2.setString(j++, p_c2sTransferVO.getModifiedBy());
                pstmtUpdate2.setString(j++, p_c2sTransferVO.getReverseTransferID());
                pstmtUpdate2.setDate(j++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_c2sTransferVO.getReverseTransferID())));
                updateCount = pstmtUpdate2.executeUpdate();

            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException:");
        	loggerValue.append(sqe.getMessage());
            LOG.error(methodName,  loggerValue);
            LOG.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[updateReconcilationStatus]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage());
            LOG.error(methodName, loggerValue );
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[updateReconcilationStatus]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            try {
                if (pstmtUpdate2 != null) {
                    pstmtUpdate2.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            if (LOG.isDebugEnabled()) {
            	  loggerValue.setLength(0);
              	loggerValue.append("Exiting:return=");
              	loggerValue.append(updateCount);
                LOG.debug(methodName,  loggerValue );
            }
        }
        return updateCount;
    }

    /**
     * Method loadC2STransferDetails.
     * This method is to load the details of transfer using the transfer ID.
     * 
     * @param p_con
     *            Connection
     * @param p_transferID
     *            String
     * @return C2STransferVO
     * @throws BTSLBaseException
     */
    public C2STransferVO loadC2STransferDetails(Connection p_con, String p_transferID) throws BTSLBaseException {

        final String methodName = "loadC2STransferDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_transferID=");
        	loggerValue.append(p_transferID);
            LOG.debug(methodName, loggerValue );
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferVO transferVO = null;
        C2STransferItemVO c2sSenderTransferItemVO = null;
        try {
            final StringBuffer selectQueryBuff = new StringBuffer();
            //local index implemented
            selectQueryBuff.append("SELECT ct.transfer_id, ct.sender_msisdn, ct.receiver_msisdn, ct.transfer_value, ct.error_code, ct.request_gateway_code,ct.PENALTY, ct.OWNER_PENALTY, ");
            selectQueryBuff.append("ct.transfer_status, ct.sender_transfer_value,ct.interface_id,ct.interface_type,ct.product_code,ct.request_gateway_type,ct.service_type, ");
            selectQueryBuff.append("ct.receiver_access_fee, ct.receiver_validity, ct.receiver_transfer_value, ct.receiver_grace_period, ");
            selectQueryBuff
                .append("ct.receiver_network_code,ct.transfer_date,ct.transfer_date_time ,ct.network_code,ct.created_on,ct.serial_number,ct.pin_sent_to_msisdn,ct.sender_id, ");
            selectQueryBuff
                .append("ct.sender_post_balance ,ct.receiver_post_balance ,KV.value,ct.debit_status supdate,ct.credit_status rupdate,ct.card_group_set_id,ct.reversal_id,ct.card_group_id,ct.interface_reference_id,ct.quantity ");
            selectQueryBuff.append(",ct.receiver_access_fee,ct.receiver_tax1_value,ct.receiver_tax2_value, ct.penalty_details ");
            selectQueryBuff.append(",ct.service_class_id,ct.service_class_code, ct.sub_service FROM c2s_transfers ct,key_values KV ");
            selectQueryBuff.append("WHERE ct.transfer_id=? and ct.transfer_date=? ");
            selectQueryBuff.append("AND ct.transfer_status=KV.key AND KV.type=? ");

            final String selectQuery = selectQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("select query:");
            	loggerValue.append(selectQuery);
                LOG.debug(methodName,  loggerValue);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setString(i++, p_transferID);
            try
            {
            pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_transferID)));
            } catch(Exception e)
            {
            	throw new BTSLBaseException("XMLTagValueValidation", "methodName", PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, new String[]{XmlTagValueConstant.TAG_TXNID});
            }
            pstmtSelect.setString(i++, PretupsI.KEY_VALUE_C2C_STATUS);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                transferVO = new C2STransferVO();
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                transferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                transferVO.setTransferValue(rs.getLong("transfer_value"));
                transferVO.setRequestedAmount(rs.getLong("transfer_value"));
                transferVO.setErrorCode(rs.getString("error_code"));
                transferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                transferVO.setTransferStatus(rs.getString("transfer_status"));
                transferVO.setSenderTransferValue(rs.getLong("sender_transfer_value"));
                transferVO.setReceiverAccessFee(rs.getLong("receiver_access_fee"));
                transferVO.setReceiverValidity(rs.getInt("receiver_validity"));
                transferVO.setReceiverTransferValue(rs.getLong("receiver_transfer_value"));
                transferVO.setReceiverGracePeriod(rs.getLong("receiver_grace_period"));
                transferVO.setSenderPostBalance(rs.getLong("sender_post_balance"));
                transferVO.setReceiverPostBalance(rs.getLong("receiver_post_balance"));
                transferVO.setValue(rs.getString("value"));
                transferVO.setReceiverNetworkCode(rs.getString("receiver_network_code"));
                transferVO.setTransferDate(rs.getDate("transfer_date"));
                transferVO.setNetworkCode(rs.getString("network_code"));
                transferVO.setCreatedOn(rs.getDate("created_on"));
                transferVO.setSerialNumber(rs.getString("serial_number"));
                transferVO.setPinSentToMsisdn(rs.getString("pin_sent_to_msisdn"));
                transferVO.setSenderID(rs.getString("sender_id"));
                transferVO.setProductCode(rs.getString("product_code"));
                transferVO.setServiceType(rs.getString("service_type"));
                transferVO.setReceiverAccessFee(rs.getLong("receiver_access_fee"));
                transferVO.setReceiverTax1Value(rs.getLong("receiver_tax1_value"));
                transferVO.setReceiverTax2Value(rs.getLong("receiver_tax2_value"));
                c2sSenderTransferItemVO = new C2STransferItemVO();
                c2sSenderTransferItemVO.setInterfaceID(rs.getString("interface_id"));
                transferVO.setSenderTransferItemVO(c2sSenderTransferItemVO);
                transferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
                transferVO.setTransferDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time")));
                transferVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                transferVO.setReverseTransferID(rs.getString("reversal_id"));
                transferVO.setCardGroupID(rs.getString("card_group_id"));
                transferVO.setQuantity(rs.getLong("quantity"));
                transferVO.setPenalty(rs.getLong("PENALTY"));
                transferVO.setOwnerPenalty(rs.getLong("OWNER_PENALTY"));
                // C2STransferItemVO c2sSenderTransferItemVO=new
                // C2STransferItemVO();
                // c2sSenderTransferItemVO.setUpdateStatus(rs.getString("supdate"));
                // transferVO.setSenderTransferItemVO(c2sSenderTransferItemVO);
                // C2STransferItemVO c2sReceiverTransferItemVO=new
                // C2STransferItemVO();
                // c2sReceiverTransferItemVO.setUpdateStatus(rs.getString("rupdate"));
                // transferVO.setReceiverTransferItemVO(c2sReceiverTransferItemVO);
                transferVO.setServiceClass(rs.getString("service_class_id"));
                transferVO.setServiceClassCode(rs.getString("service_class_code"));
                transferVO.setInterfaceReferenceId(rs.getString("interface_reference_id"));
		        transferVO.setSubService(rs.getString("sub_service"));
		        transferVO.setPenaltyDetails(rs.getString("penalty_details"));

            }

        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName,  loggerValue );
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2STransferDetails]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }
        catch (BTSLBaseException be) {
        	loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException ");
        	loggerValue.append(be.getMessage());
            LOG.error(methodName,  loggerValue );
            LOG.errorTrace(methodName, be);
            loggerValue.setLength(0);
        	loggerValue.append("BTSL Exception:");
        	loggerValue.append(be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2STransferDetails]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, be.getMessageKey(),be.getArgs());
        }
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            LOG.error("loadC2STransferItemsVOList",  loggerValue );
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2STransferDetails]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting ");
            }
        }// end of finally
        return transferVO;
    }

    /**
     * This method is used to load transfer details from c2s tranfer to get the
     * status of transaction in ScheduledTopUP Process.
     * 
     * @param Connection
     *            p_con
     * @param p_transferID
     * @return C2STransferVO
     * @throws BTSLBaseException
     */
    public C2STransferVO loadC2STransferDetailsForSchProcess(Connection p_con, String p_transferID) throws BTSLBaseException {

        final String methodName = "loadC2STransferDetailsForSchProcess";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_transferID=");
        	loggerValue.append(p_transferID);
            LOG.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferVO transferVO = null;
        try {
            final StringBuffer selectQueryBuff = new StringBuffer();
            //local index implemented
            selectQueryBuff.append("SELECT transfer_id, transfer_date, transfer_date_time,network_code,sender_id,sender_msisdn, ");
            selectQueryBuff.append(" receiver_msisdn, transfer_value, error_code, request_gateway_code,transfer_status, sender_transfer_value ");
            selectQueryBuff.append(" FROM c2s_transfers ");
            selectQueryBuff.append(" WHERE transfer_id=? and transfer_date=? ");
            final String selectQuery = selectQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("select query:");
            	loggerValue.append(selectQuery);
                LOG.debug("loadC2STransferDetails",  loggerValue);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setString(i, p_transferID);
            i++;
            pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_transferID)));
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                transferVO = new C2STransferVO();
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                transferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                transferVO.setTransferValue(rs.getLong("transfer_value"));
                transferVO.setErrorCode(rs.getString("error_code"));
                transferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                transferVO.setTransferStatus(rs.getString("transfer_status"));
                transferVO.setSenderTransferValue(rs.getLong("sender_transfer_value"));
            }

        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException " );
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName, loggerValue );
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:" );
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2STransferDetails]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            LOG.error(methodName,  loggerValue);
            LOG.errorTrace(methodName, e);
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
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting ");
            }
        }// end of finally
        return transferVO;
    }

    /**
     * 
     * method markC2SReceiverAmbiguous
     * This method is used in the C2S Reconciliation module, by this method
     * receiver's transfer status is updated
     * as ambigous and previous transfer status is assigned to the update
     * status.
     * 
     * @param p_con
     * @param p_transferID
     * @return
     * @throws BTSLBaseException
     *             int
     * @author sandeep.goel ID REC001
     */
    public int markC2SReceiverAmbiguous(Connection p_con, String p_transferID) throws BTSLBaseException {
        final String methodName = "markC2SReceiverAmbiguous";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_transferID:");
        	loggerValue.append(p_transferID);
            LOG.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            int i = 1;
            //local index implemented
            final StringBuffer updateQueryBuff = new StringBuffer(" UPDATE c2s_transfers ");
            updateQueryBuff.append("SET credit_status=? WHERE  transfer_id=? and transfer_date=? ");
            final String updateQuery = updateQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Update query:");
            	loggerValue.append(updateQuery);
                LOG.debug("updateTransferItemDetails", loggerValue );
            }

            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(i, InterfaceErrorCodesI.AMBIGOUS);
            i++;
            pstmtUpdate.setString(i, p_transferID);
            i++;
            pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_transferID)));
            // pstmtUpdate.setString(i++, PretupsI.USER_TYPE_RECEIVER);
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName, loggerValue );
            updateCount = 0;
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[markC2SReceiverAmbiguous]",
                p_transferID, "", "",  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
            loggerValue.append("Exception " );
            loggerValue.append(e.getMessage());
            LOG.error(methodName, loggerValue);
            updateCount = 0;
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception " );
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[markC2SReceiverAmbiguous]",
                p_transferID, "", "",  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting updateCount=" + updateCount);
            }
        }// end of finally
    }

    /**
     * Method to update the transfer items of C2S in the database
     * 
     * @param p_con
     * @param p_c2sTransferItemVO
     * @param p_transferID
     * @return
     * @throws BTSLBaseException
     */
    public int updateC2SIATTransferItemDetails(Connection p_con, IATTransferItemVO p_iatTransferItemVO, String p_transferID) throws BTSLBaseException {
        final String methodName = "updateC2SIATTransferItemDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_transferID:");
        	loggerValue.append(p_transferID);
        	loggerValue.append(", p_iatTransferItemVO : ");
        	loggerValue.append(p_iatTransferItemVO.toString());
            LOG.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            int i = 1;
            final StringBuffer updateQueryBuff = new StringBuffer("UPDATE c2s_iat_transfer_items SET failed_at=?, exchange_rate=?, prov_ratio=?,");
            updateQueryBuff.append(" rec_bonus=?, iat_timestamp=?, credit_msg=?, credit_resp_code=?,");
            updateQueryBuff.append(" iat_error_code=?, iat_message=?,chk_status_resp_code=?, ");
            updateQueryBuff.append("rec_nw_error_code=?, rec_nw_message=?, fees=?, rcvd_amt=?,");
            updateQueryBuff.append(" iat_txn_id=?, sent_amt=?, transfer_status=?,transfer_value=?,quantity=?,sent_amt_iattorec=? ");
            updateQueryBuff.append("where transfer_id=? and rec_msisdn=? ");
            final String updateQuery = updateQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Insert query:");
            	loggerValue.append(updateQuery);
                LOG.debug(methodName,  loggerValue );
            }

            pstmtUpdate = p_con.prepareStatement(updateQuery);

            pstmtUpdate.setString(i, p_iatTransferItemVO.getIatFailedAt());
            i++;
            pstmtUpdate.setDouble(i, p_iatTransferItemVO.getIatExchangeRate());
            i++;
            pstmtUpdate.setDouble(i, p_iatTransferItemVO.getIatProvRatio());
            i++;
            pstmtUpdate.setDouble(i, p_iatTransferItemVO.getIatReceiverSystemBonus());
            i++;
            pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_iatTransferItemVO.getIatTimestamp()));
            i++;
            pstmtUpdate.setString(i, p_iatTransferItemVO.getIatCreditMessage());
            i++;
            pstmtUpdate.setString(i, p_iatTransferItemVO.getIatCreditRespCode());
            i++;
            pstmtUpdate.setString(i, p_iatTransferItemVO.getIatErrorCode());
            i++;
            pstmtUpdate.setString(i, p_iatTransferItemVO.getIatErrorMessage());
            i++;
            pstmtUpdate.setString(i, p_iatTransferItemVO.getIatCheckStatusRespCode());
            i++;
            pstmtUpdate.setString(i, p_iatTransferItemVO.getIatRcvrNWErrorCode());
            i++;
            pstmtUpdate.setString(i, p_iatTransferItemVO.getIatRcvrNWErrorMessage());
            i++;
            pstmtUpdate.setDouble(i, p_iatTransferItemVO.getIatFees());
            i++;
            pstmtUpdate.setDouble(i, p_iatTransferItemVO.getIatRcvrRcvdAmt());
            i++;
            pstmtUpdate.setString(i, p_iatTransferItemVO.getIatTxnId());
            i++;
            pstmtUpdate.setDouble(i, p_iatTransferItemVO.getIatReceivedAmount());
            i++;
            pstmtUpdate.setString(i, p_iatTransferItemVO.getTransferStatus());
            i++;
            pstmtUpdate.setLong(i, p_iatTransferItemVO.getTransferValue());
            i++;
            pstmtUpdate.setLong(i, p_iatTransferItemVO.getQuantity());
            i++;
            pstmtUpdate.setDouble(i, p_iatTransferItemVO.getIatSentAmtByIAT());
            i++;
            pstmtUpdate.setString(i, p_iatTransferItemVO.getIatSenderTxnId());
            i++;
            pstmtUpdate.setString(i, p_iatTransferItemVO.getIatRecMsisdn());
            i++;
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            LOG.error("updateC2STransferItemDetails",  loggerValue );
            updateCount = 0;
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[updateC2STransferItemDetails]",
                p_transferID, p_iatTransferItemVO.getIatRecMsisdn(), "", loggerValue.toString());
            throw new BTSLBaseException(this, "updateC2STransferItemDetails", "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	  loggerValue.setLength(0);
          	loggerValue.append("Exception:");
          	loggerValue.append(e.getMessage());
            LOG.error("updateC2STransferItemDetails", loggerValue);
            updateCount = 0;
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[updateC2STransferItemDetails]",
                p_transferID, p_iatTransferItemVO.getIatRecMsisdn(), "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateC2STransferItemDetails", "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	 loggerValue.setLength(0);
               	loggerValue.append("Exiting updateCount=");
               	loggerValue.append(updateCount);
                LOG.debug("updateC2STransferItemDetails",  loggerValue );
            }
        }// end of finally
    }

    /**
     * Method loadC2STransferItemsVOList.
     * This method is to load the items list according to the transfer ID.
     * 
     * @param p_con
     *            Connection
     * @param p_transferID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadC2STransferItemsVOList(Connection p_con, String p_transferID, Date p_fromdate, Date p_todate) throws BTSLBaseException {
        final String methodName = "loadC2STransferItemsVOList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (p_todate == null) {
            p_todate = new Date();
        }
        try {
            if (_operatorUtilI.getNewDataAftrTbleMerging(p_fromdate, p_todate)) {
                return loadC2STransferItemsVOList(p_con, p_transferID);
            } else {
                return loadC2STransferItemsVOList_old(p_con, p_transferID);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception " );
        	loggerValue.append(e.getMessage());
            LOG.error("getTransactionDetails", loggerValue );
            LOG.errorTrace(methodName, e);
        }
        ;

        return loadC2STransferItemsVOList(p_con, p_transferID);

    }

    private ArrayList loadC2STransferItemsVOList_old(Connection p_con, String p_transferID) throws BTSLBaseException {

        final String methodName = "loadC2STransferItemsVOList_old";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_transferID=");
        	loggerValue.append(p_transferID);
            LOG.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferItemVO c2sTransferItemVO = null;
        final ArrayList c2sTransferItemsVOList = new ArrayList();
        try {
        	pstmtSelect = c2STransferQry.loadC2STransferItemsVOList_oldQry(p_con,p_transferID);
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
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName,  loggerValue );
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2STransferItemsVOList]", "", "",
                "",  loggerValue.toString());
            throw new BTSLBaseException(this, "loadC2STransferItemsVOList", "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            LOG.error(methodName,loggerValue );
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2STransferItemsVOList]", "", "",
                "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting c2sTransferItemsVOList.size()=");
            	loggerValue.append(c2sTransferItemsVOList.size());
                LOG.debug(methodName,  loggerValue );
            }
        }// end of finally

        return c2sTransferItemsVOList;
    }

    /**
     * Method loadC2STransferItemsVOList.
     * This method is to load the items list according to the transfer ID.
     * 
     * @param p_con
     *            Connection
     * @param p_transferID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadC2STransferItemsVOList(Connection p_con, String p_transferID) throws BTSLBaseException {

        final String methodName = "loadC2STransferItemsVOList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_transferID=");
        	loggerValue.append(p_transferID);
            LOG.debug(methodName, loggerValue );
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        // C2STransferItemVO c2sTransferItemVO=null;
        final ArrayList c2sTransferItemsVOList = new ArrayList();
        try {
        	pstmtSelect = c2STransferQry.loadC2STransferItemsVOListQry(p_con,p_transferID);
            rs = pstmtSelect.executeQuery();
            boolean aliasToBeEncrypted = false; 
            aliasToBeEncrypted = ((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.ALIAS_TO_BE_ENCRYPTED))).booleanValue();
            if (rs.next()) {
                final C2STransferItemVO senderVO = new C2STransferItemVO();

                senderVO.setTransferID(rs.getString("transfer_id"));
                senderVO.setMsisdn(rs.getString("sender_msisdn"));

                senderVO.setEntryDate(rs.getDate("created_on"));
                senderVO.setRequestValue(rs.getLong("transfer_value"));

                senderVO.setPreviousBalance(rs.getLong("sender_previous_balance"));

                senderVO.setPostBalance(rs.getLong("sender_post_balance"));

                senderVO.setUserType(PretupsI.USER_TYPE_SENDER);
                senderVO.setTransferType(rs.getString("transfer_type_value"));
                senderVO.setEntryType(PretupsI.DEBIT);
                // c2sTransferItemVO.setValidationStatus(rs.getString("validation_status"));
                senderVO.setUpdateStatus(rs.getString("debit_status"));
                senderVO.setTransferValue(rs.getLong("sender_transfer_value"));
                senderVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("sender_transfer_value")));
                // c2sTransferItemVO.setInterfaceType(rs.getString("interface_type"));
                // c2sTransferItemVO.setInterfaceID(rs.getString("interface_id"));
                // c2sTransferItemVO.setInterfaceResponseCode(rs.getString("interface_response_code"));
                // c2sTransferItemVO.setInResponseCodeDesc(rs.getString("in_response_code_desc"));
                // c2sTransferItemVO.setInterfaceReferenceID(rs.getString("interface_reference_id"));
                senderVO.setSubscriberType(rs.getString("subscriber_type"));
                // c2sTransferItemVO.setServiceClassCode(rs.getString("service_class_code"));
                // c2sTransferItemVO.setPreviousExpiry(rs.getDate("msisdn_previous_expiry"));
                // c2sTransferItemVO.setNewExpiry(rs.getDate("msisdn_new_expiry"));
                senderVO.setTransferStatus(rs.getString("transfer_status"));

                senderVO.setTransferDate(rs.getDate("transfer_date"));
                senderVO.setTransferDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time")));
                
                //defect c2s transfer enquiry spring
                if((rs.getTimestamp("transfer_date_time"))!=null)
                	senderVO.setTransferDatetimeStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time"))));
                if((rs.getTimestamp("created_on"))!=null)
                	senderVO.setEntryDateTimeStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on"))));

                senderVO.setEntryDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                
                // c2sTransferItemVO.setFirstCall(rs.getString("first_call"));
                senderVO.setSNo(1);
                // c2sTransferItemVO.setPrefixID(rs.getLong("prefix_id"));
                // c2sTransferItemVO.setServiceClass(rs.getString("service_class_id"));
                // c2sTransferItemVO.setProtocolStatus(rs.getString("protocol_status"));
                // c2sTransferItemVO.setAccountStatus(rs.getString("account_status"));
                // c2sTransferItemVO.setReferenceID(rs.getString("reference_id"));
                senderVO.setLanguage(rs.getString("language"));
                senderVO.setCountry(rs.getString("country"));
                senderVO.setTransferStatusMessage(rs.getString("value"));
                // added for service provider name
                senderVO.setServiceProviderName(rs.getString("SERVICE_PROVIDER_NAME"));
                // Added By Brajesh For LMS ambiguous case handling
                senderVO.setLmsProfile(rs.getString("lms_profile"));
                senderVO.setLmsVersion(rs.getString("lms_version"));
            	senderVO.setCommission(PretupsBL.getDisplayAmount(rs.getLong("bonus_amount"))); //trasha
				senderVO.setDifferential(PretupsBL.getDisplayAmount(rs.getLong("differntial")));//trasha
				
                c2sTransferItemsVOList.add(senderVO);

                final C2STransferItemVO receiverVO = new C2STransferItemVO();

                receiverVO.setTransferID(rs.getString("transfer_id"));
                receiverVO.setMsisdn(rs.getString("receiver_msisdn"));
                receiverVO.setEntryDate(rs.getDate("created_on"));
                receiverVO.setRequestValue(rs.getLong("transfer_value"));
                receiverVO.setPreviousBalance(rs.getLong("receiver_previous_balance"));
                receiverVO.setPostBalance(rs.getLong("receiver_post_balance"));
                receiverVO.setUserType(PretupsI.USER_TYPE_RECEIVER);
                receiverVO.setTransferType(rs.getString("transfer_type_value"));
                receiverVO.setEntryType(PretupsI.CREDIT);
                receiverVO.setValidationStatus(rs.getString("validation_status"));
                receiverVO.setUpdateStatus(rs.getString("credit_status"));
                receiverVO.setTransferValue(rs.getLong("receiver_transfer_value"));
                receiverVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("receiver_transfer_value")));
                receiverVO.setInterfaceType(rs.getString("interface_type"));
                receiverVO.setInterfaceID(rs.getString("interface_id"));
                receiverVO.setInterfaceResponseCode(rs.getString("interface_response_code"));
                receiverVO.setInResponseCodeDesc(rs.getString("in_response_code_desc"));
                receiverVO.setInterfaceReferenceID(rs.getString("interface_reference_id"));
                receiverVO.setSubscriberType(rs.getString("subscriber_type"));
                receiverVO.setServiceClassCode(rs.getString("service_class_code"));
                receiverVO.setPreviousExpiry(rs.getDate("msisdn_previous_expiry"));
                receiverVO.setNewExpiry(rs.getDate("msisdn_new_expiry"));
                receiverVO.setTransferStatus(rs.getString("transfer_status"));
                receiverVO.setTransferDate(rs.getDate("transfer_date"));
                receiverVO.setTransferDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time")));
                receiverVO.setEntryDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                receiverVO.setFirstCall(rs.getString("first_call"));
                receiverVO.setSNo(2);
                receiverVO.setPrefixID(rs.getLong("prefix_id"));
                receiverVO.setServiceClass(rs.getString("service_class_id"));
                receiverVO.setProtocolStatus(rs.getString("protocol_status"));
                receiverVO.setAccountStatus(rs.getString("account_status"));
                receiverVO.setReferenceID(rs.getString("reference_id"));
                receiverVO.setCurrencyDetail(rs.getString("multicurrency_detail"));
                receiverVO.setLanguage(rs.getString("language"));
                receiverVO.setCountry(rs.getString("country"));
                receiverVO.setTransferStatusMessage(rs.getString("value"));
                
                //defect c2s transfer enquiry spring
                if((rs.getTimestamp("transfer_date_time"))!=null)
                	receiverVO.setTransferDatetimeStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time"))));
                if((rs.getTimestamp("created_on"))!=null)
                	receiverVO.setEntryDateTimeStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on"))));
                if((rs.getDate("msisdn_previous_expiry"))!=null)
                	receiverVO.setPreviousExpiryStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("msisdn_previous_expiry"))));
                if((rs.getDate("msisdn_new_expiry"))!=null)
                	receiverVO.setNewExpiryStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("msisdn_new_expiry"))));
                
                //added for defect 897
                if(!BTSLUtil.isNullString(rs.getString("subs_sid")))
                receiverVO.setMsisdn(rs.getString("subs_sid"));

                
                    if(aliasToBeEncrypted&&!BTSLUtil.isNullString(rs.getString("subs_sid")) && !rs.getString("subs_sid").matches("[0-9]+")){
                              receiverVO.setMsisdn(new CryptoUtil().decrypt(rs.getString("subs_sid"),Constants.KEY));
                    }
               
                //added for defect 897
                
                // Added By Brajesh For LMS ambiguous case handling
                senderVO.setLmsProfile(rs.getString("lms_profile"));
                senderVO.setLmsVersion(rs.getString("lms_version"));

                /*
                 * receiverVO.setPromoStatus(rs.getString("promo_status"));
                 * receiverVO.setInterfacePromoStatus(rs.getString(
                 * "interface_promo_response_code"));
                 * receiverVO.setPreviousPromoExpiry(rs.getDate(
                 * "promo_previous_expiry"));
                 * receiverVO.setNewPromoExpiry(rs.getDate("promo_new_expiry"));
                 * //receiverVO.setPromoStatusMessage(rs.getString(
                 * "promo_status_value"));
                 * //receiverVO.setInterfacePromoDesc(rs.getString(
                 * "in_promo_response_code_desc"));
                 * receiverVO.setCosStatus(rs.getString("cos_status"));
                 * receiverVO.setInterfaceCosStatus(rs.getString(
                 * "interface_cos_response_code"));
                 * receiverVO.setNewServiceClssCode(rs.getString(
                 * "new_service_class_code"));
                 * //receiverVO.setCosStatusMessage(rs.getString("cos_status_value"
                 * ));
                 * // receiverVO.setInterfaceCosDesc(rs.getString(
                 * "in_cos_response_code_desc"));
                 * receiverVO.setPreviousPromoBalance(rs.getLong(
                 * "promo_previous_balance"));
                 * receiverVO.setNewPromoBalance(rs.getLong("promo_post_balance")
                 * );
                 */
                c2sTransferItemsVOList.add(receiverVO);

                final String cr_bk_status = rs.getString("credit_back_status");
                final String reconciliation_flag = rs.getString("reconciliation_flag");

                int itemNo = 2;
                if (!BTSLUtil.isNullString(cr_bk_status)) {

                    final C2STransferItemVO creditBackVO = new C2STransferItemVO();

                    creditBackVO.setMsisdn(senderVO.getMsisdn());
                    creditBackVO.setRequestValue(senderVO.getRequestValue());
                    creditBackVO.setSubscriberType(senderVO.getSubscriberType());
                    creditBackVO.setTransferDate(senderVO.getTransferDate());
                    creditBackVO.setTransferDateTime(senderVO.getTransferDateTime());
                    creditBackVO.setTransferID(senderVO.getTransferID());
                    creditBackVO.setUserType(senderVO.getUserType());
                    creditBackVO.setEntryDate(senderVO.getEntryDate());
                    creditBackVO.setEntryDateTime(senderVO.getEntryDateTime());
                    creditBackVO.setPrefixID(senderVO.getPrefixID());
                    creditBackVO.setTransferValue(senderVO.getTransferValue());
                    creditBackVO.setInterfaceID(senderVO.getInterfaceID());
                    creditBackVO.setInterfaceType(senderVO.getInterfaceType());
                    creditBackVO.setServiceClass(senderVO.getServiceClass());
                    creditBackVO.setServiceClassCode(senderVO.getServiceClassCode());
                    creditBackVO.setInterfaceHandlerClass(senderVO.getInterfaceHandlerClass());
                    creditBackVO.setLanguage(senderVO.getLanguage());
                    creditBackVO.setCountry(senderVO.getCountry());
                    ++itemNo;
                    creditBackVO.setSNo(itemNo);
                    creditBackVO.setEntryType(PretupsI.CREDIT);
                    creditBackVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
                    creditBackVO.setValidationStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                    creditBackVO.setTransferStatus(cr_bk_status);
                    creditBackVO.setUpdateStatus(cr_bk_status);
                    creditBackVO.setPreviousBalance(rs.getLong("SENDER_CR_BK_PREV_BAL"));
                    creditBackVO.setPostBalance(rs.getLong("SENDER_CR_BK_POST_BAL"));

                    c2sTransferItemsVOList.add(creditBackVO);

                }
                if ((!BTSLUtil.isNullString(reconciliation_flag)) && PretupsI.YES.equals(reconciliation_flag)) {

                    final C2STransferItemVO reconcileVO = new C2STransferItemVO();
                    reconcileVO.setMsisdn(senderVO.getMsisdn());
                    ++itemNo;
                    reconcileVO.setSNo(itemNo);
                    reconcileVO.setEntryType(rs.getString("reconcile_entry_type"));
                    reconcileVO.setEntryDate(rs.getDate("reconciliation_date"));
                    reconcileVO.setEntryDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("reconciliation_date")));
                    reconcileVO.setTransferDate(rs.getDate("reconciliation_date"));
                    reconcileVO.setTransferDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("reconciliation_date")));
                    reconcileVO.setTransferID(senderVO.getTransferID());
                    reconcileVO.setUserType(senderVO.getUserType());
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
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName,  loggerValue );
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:" );
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2STransferItemsVOList]", "", "",
                "", loggerValue.toString() );
            throw new BTSLBaseException(this, "loadC2STransferItemsVOList_new", "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            LOG.error(methodName,  loggerValue );
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2STransferItemsVOList]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting c2sTransferItemsVOList.size()=");
            	loggerValue.append(c2sTransferItemsVOList.size());
                LOG.debug(methodName,  loggerValue);
            }
        }// end of finally

        return c2sTransferItemsVOList;
    }

    /**
     * Method validateCollelctionCancellationDetails.
     * This method is to load the details of transfer using the transfer ID.
     * 
     * @param p_con
     *            Connection
     * @param p_transferID
     *            String
     * @param p_channelUserVO
     *            ChannelUserVO
     * @param p_receiverMsisdn
     *            String
     * @param p_c2sTransferVO
     *            C2STransferVO
     * @return Boolean
     * @throws BTSLBaseException
     */
    public C2STransferVO loadOldTxnIDForReversal(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO, String p_serviceType) throws BTSLBaseException {
        final String methodName = "loadOldTxnIDForReversal";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_transferID=");
        	loggerValue.append(p_c2sTransferVO.getTransferID());
        	loggerValue.append(" ,Old Txn Id=" );
        	loggerValue.append(p_c2sTransferVO.getOldTxnId());
        	loggerValue.append(" , Service Type=");
        	loggerValue.append( p_requestVO.getServiceType());
            LOG.debug(methodName,loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferVO transferVO = null;
        C2STransferItemVO c2sSenderTransferItemVO = null;
        String time = null;
        if (p_requestVO.getRequestorCategoryCode() != null && (PretupsI.BCU_USER.equalsIgnoreCase(p_requestVO.getRequestorCategoryCode()) || PretupsI.CUSTOMER_CARE.equalsIgnoreCase(p_requestVO.getRequestorCategoryCode())|| TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(p_requestVO.getRequestorCategoryCode())|| TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(p_requestVO.getRequestorCategoryCode()))) {
        	 time = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TIME_FOR_REVERSAL_CCE));
        } else {
            time = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TIME_FOR_REVERSAL));
        }

        final Date date = new Date();
        //final Date date1 = BTSLUtil.getSQLDateFromUtilDate(date);
        try {
        	if(p_c2sTransferVO.getOldTxnId().equals("0"))
        	{
        		throw  new BTSLBaseException(PretupsErrorCodesI.C2S_INVALID_TXN_ID_REVERSAL);
        	}
        	pstmtSelect=c2STransferQry.loadOldTxnIDForReversalQry(p_con, p_c2sTransferVO, p_requestVO, p_serviceType, date, time);
            rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                transferVO = new C2STransferVO();
            	transferVO.setServiceType(rs.getString("service_type"));
                transferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                transferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                transferVO.setQuantity(rs.getLong("quantity"));
                transferVO.setRequestedAmount(rs.getLong("quantity"));
                transferVO.setTransferValue(rs.getLong("transfer_value"));
                transferVO.setErrorCode(rs.getString("error_code"));
                transferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                transferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
                transferVO.setTransferStatus(rs.getString("transfer_status"));
                transferVO.setReceiverValidity(rs.getInt("receiver_validity"));
                transferVO.setReceiverTransferValue(rs.getLong("receiver_transfer_value"));
                transferVO.setReceiverNetworkCode(rs.getString("receiver_network_code"));
                transferVO.setTransferDate(rs.getDate("transfer_date"));
                transferVO.setNetworkCode(rs.getString("network_code"));
                transferVO.setCreatedOn(rs.getDate("created_on"));
                transferVO.setSenderID(rs.getString("sender_id"));
                transferVO.setProductCode(rs.getString("product_code"));
                c2sSenderTransferItemVO = new C2STransferItemVO();
                c2sSenderTransferItemVO.setInterfaceID(rs.getString("interface_id"));
                transferVO.setSenderTransferItemVO(c2sSenderTransferItemVO);
                transferVO.setTransferDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time")));
                transferVO.setCardGroupCode(rs.getString("card_group_code"));
                transferVO.setCardGroupID(rs.getString("card_group_id"));
                transferVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                transferVO.setVersion(rs.getString("version"));
                transferVO.setDifferentialApplicable(rs.getString("differential_applicable"));
                transferVO.setDifferentialGiven(rs.getString("differential_given"));
                transferVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
                transferVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                transferVO.setReceiverTax1Value(rs.getLong("receiver_tax1_value"));
                transferVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
                transferVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                transferVO.setReceiverTax2Value(rs.getLong("receiver_tax2_value"));
                transferVO.setReceiverBonusValue(rs.getLong("receiver_bonus_value"));
                transferVO.setReceiverGracePeriod(rs.getLong("receiver_grace_period"));
                transferVO.setReceiverBonusValidity(rs.getInt("receiver_bonus_validity"));
                transferVO.setReceiverValPeriodType(rs.getString("receiver_valperiod_type"));
                transferVO.setReverseTransferID(rs.getString("REVERSAL_ID"));
                transferVO.setSelectorCode(rs.getString("SUB_SERVICE"));
                transferVO.setSubService(rs.getString("SUB_SERVICE"));
                transferVO.setCellId(rs.getString("cell_id"));
                transferVO.setSwitchId(rs.getString("switch_id"));
                transferVO.setReceiverAccessFee(rs.getLong("RECEIVER_ACCESS_FEE"));
                transferVO.setInfo1(rs.getString("INFO1"));
                transferVO.setInfo2(rs.getString("INFO2"));
                transferVO.setInfo3(rs.getString("INFO3"));
                transferVO.setOtfApplicable(rs.getString("otf_applicable"));
                transferVO.setSubscriberSID(rs.getString("subs_sid"));

                final C2STransferItemVO receiverVo = new C2STransferItemVO();
                final C2STransferItemVO senderVO = new C2STransferItemVO();
                receiverVo.setSNo(2);
                receiverVo.setServiceClass(rs.getString("SERVICE_CLASS_ID"));
                receiverVo.setServiceClassCode(rs.getString("SERVICE_CLASS_CODE"));
                final ArrayList receiverVoList = new ArrayList();
                final ArrayList senderVoList = new ArrayList();
                senderVO.setSNo(1);
                // senderVO.setPrefixID(rs.getLong("SENDER_PREFIX_ID"));
                senderVoList.add(senderVO);
                receiverVoList.add(receiverVo);
                transferVO.setTransferItemList(senderVoList);
                transferVO.setTransferItemList(receiverVoList);
            }

        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName,  loggerValue);
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception :");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadOldTxnIDForReversal]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch(ParseException be) {
        	loggerValue.setLength(0);
        	loggerValue.append("ParseException " );
        	loggerValue.append( be.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, be);
            loggerValue.setLength(0);
        	loggerValue.append( "ParseException:" );
        	loggerValue.append( be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadOldTxnIDForReversal]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_INVALID_TXN_ID_REVERSAL, be);
        } catch (BTSLBaseException be) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException " );
        	loggerValue.append( be.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, be);
            loggerValue.setLength(0);
        	loggerValue.append( "BTSLBase Exception:" );
        	loggerValue.append( be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadOldTxnIDForReversal]", "", "", "",
            		loggerValue.toString());
            if (PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL.equals(p_serviceType)) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_INVALID_PRE_TXN_ID_REVERSAL,be);
            } else {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_INVALID_TXN_ID_REVERSAL,be);
            }
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            LOG.error(methodName,  loggerValue);
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadOldTxnIDForReversal]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, e.getMessage(),e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting transferVO=");
            	loggerValue.append(transferVO);
                LOG.debug(methodName, loggerValue);
            }
        }
        return transferVO;
    }

    /**
     * Method to update the transfer details of C2S in the database
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @return
     * @throws BTSLBaseException
     */
    public int updateOldC2STransferDetailsReversal(Connection p_con, C2STransferVO p_c2sTransferVO) throws BTSLBaseException {
        final String methodName = "updateOldC2STransferDetailsReversal";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_c2sTransferVO:");
        	loggerValue.append(p_c2sTransferVO.toString());
            LOG.debug(methodName,  loggerValue);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            int i = 1;
            //local index implemented
            final StringBuffer updateQueryBuff = new StringBuffer(" UPDATE c2s_transfers SET   ");
            updateQueryBuff.append(" REVERSAL_ID=?, credit_back_status=?,modified_on=?,modified_by=? ");
            updateQueryBuff.append(" WHERE transfer_id=? and transfer_date=? and transfer_status= ? ");
            final String updateQuery = updateQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Insert query:" + updateQuery + " " + p_c2sTransferVO.getReferenceID() + " " + p_c2sTransferVO.getErrorCode() + " " + p_c2sTransferVO
                    .getDifferentialApplicable() + " " + p_c2sTransferVO.getDifferentialGiven() + " " + p_c2sTransferVO.getCreditBackStatus() + " " + p_c2sTransferVO
                    .getModifiedBy());
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(i, p_c2sTransferVO.getTransferID());
            i++;
            pstmtUpdate.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getModifiedOn()));
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getModifiedBy());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getOldTxnId());
            i++;
            pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_c2sTransferVO.getOldTxnId())));
            i++;
            pstmtUpdate.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            updateCount = pstmtUpdate.executeUpdate();
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName,  loggerValue );
            updateCount = 0;
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[updateOldC2STransferDetailsReversal]",
                p_c2sTransferVO.getTransferID(), p_c2sTransferVO.getSenderMsisdn(), p_c2sTransferVO.getNetworkCode(), loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            LOG.error(methodName,  loggerValue );
            updateCount = 0;
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[updateOldC2STransferDetailsReversal]",
                p_c2sTransferVO.getTransferID(), p_c2sTransferVO.getSenderMsisdn(), p_c2sTransferVO.getNetworkCode(),  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting updateCount=" );
            	loggerValue.append(updateCount);
                LOG.debug(methodName, loggerValue);
            }
        }// end of finally
        return updateCount;
    }

    /**
     * Method to update the transfer details of C2S in the database
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @return
     * @throws BTSLBaseException
     */
    public int updateC2STransferDetailsReversal(Connection p_con, C2STransferVO p_c2sTransferVO) throws BTSLBaseException {
        final String methodName = "updateC2STransferDetailsReversal";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_c2sTransferVO:");
        	loggerValue.append(p_c2sTransferVO.toString());
            LOG.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            int i = 1;
            //local index implemented
            final StringBuffer updateQueryBuff = new StringBuffer(" UPDATE c2s_transfers SET   ");
            updateQueryBuff
                .append(" reference_id=?, error_code=?,differential_applicable=?,differential_given=?, credit_back_status=?,modified_on=?,modified_by=?,transfer_status=? ,end_time=? ,pin_sent_to_msisdn=?, bonus_details=? , ");
            updateQueryBuff.append(" RECEIVER_PREVIOUS_BALANCE=?, RECEIVER_POST_BALANCE=?,SENDER_PREVIOUS_BALANCE=?,SENDER_POST_BALANCE=?, ");
            updateQueryBuff.append(" validation_status=?, credit_status=?, account_status=?, interface_response_code=? , msisdn_previous_expiry=?, ");
            updateQueryBuff.append(" msisdn_new_expiry=?, first_call=?, interface_reference_id=?,protocol_status=?, ");
            updateQueryBuff.append(" adjust_dr_txn_type=?, adjust_dr_txn_id=?, adjust_dr_update_status=?, ");
            updateQueryBuff.append(" adjust_cr_txn_type=?, adjust_cr_txn_id=?, adjust_cr_update_status=? , adjust_value=?, ");
            updateQueryBuff.append(" promo_previous_balance=?, promo_post_balance=?, promo_previous_expiry=?, promo_new_expiry=? ");
            updateQueryBuff.append(",promo_status=?, interface_promo_response_code=?,cos_status=?, interface_cos_response_code=?, new_service_class_code=?,SENDER_TRANSFER_VALUE=?,TXN_TYPE=?,sub_service=?,penalty_Details=?,penalty=?,owner_penalty=?");
            updateQueryBuff.append(" WHERE transfer_id=? and transfer_date=? ");
            final String updateQuery = updateQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Insert query:");
            	loggerValue.append(updateQuery);
            	loggerValue.append(" ");
            	loggerValue.append(p_c2sTransferVO.getReferenceID());
            	loggerValue.append(" " );
            	loggerValue.append(p_c2sTransferVO.getErrorCode());
            	
            	loggerValue.append(" " );
            	loggerValue.append(p_c2sTransferVO.getDifferentialApplicable());
            	loggerValue.append(" ");
            	loggerValue.append(p_c2sTransferVO.getDifferentialGiven());
            	loggerValue.append(" ");
            	loggerValue.append(p_c2sTransferVO.getCreditBackStatus());
            	loggerValue.append(" ");
            	loggerValue.append(p_c2sTransferVO.getModifiedBy());
                LOG.debug(methodName,  loggerValue );
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(i, p_c2sTransferVO.getReferenceID());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getErrorCode());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getDifferentialApplicable());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getDifferentialGiven());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getCreditBackStatus());
            i++;
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getModifiedOn()));
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getModifiedBy());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getTransferStatus());
            i++;
            pstmtUpdate.setLong(i, System.currentTimeMillis());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getPinSentToMsisdn());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getBonusSummarySting());
            i++;
            final C2STransferItemVO receiverItemVO = (C2STransferItemVO) p_c2sTransferVO.getTransferItemList().get(1);
            pstmtUpdate.setLong(i, receiverItemVO.getPreviousBalance());
            i++;
            pstmtUpdate.setLong(i, receiverItemVO.getPostBalance());
            i++;
            pstmtUpdate.setLong(i, p_c2sTransferVO.getPreviousBalance());
            i++;
            pstmtUpdate.setLong(i, p_c2sTransferVO.getPostBalance());
            i++;
            pstmtUpdate.setString(i, receiverItemVO.getValidationStatus());
            i++;
            pstmtUpdate.setString(i, receiverItemVO.getUpdateStatus());
            i++;
            pstmtUpdate.setString(i, BTSLUtil.NullToString(receiverItemVO.getAccountStatus()));
            i++;
            pstmtUpdate.setString(i, BTSLUtil.NullToString(receiverItemVO.getInterfaceResponseCode()));
            i++;
            if (receiverItemVO.getPreviousExpiry() != null) {
                pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(receiverItemVO.getPreviousExpiry()));
                i++;
            } else {
                pstmtUpdate.setNull(i, Types.DATE);
                i++;
            }
            if (receiverItemVO.getNewExpiry() != null) {
                pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(receiverItemVO.getNewExpiry()));
                i++;
            } else {
                pstmtUpdate.setNull(i, Types.DATE);
                i++;
            }
            pstmtUpdate.setString(i, receiverItemVO.getFirstCall());
            i++;
            pstmtUpdate.setString(i, receiverItemVO.getInterfaceReferenceID());
            i++;
            pstmtUpdate.setString(i, BTSLUtil.NullToString(receiverItemVO.getProtocolStatus()));
            i++;
            pstmtUpdate.setString(i, receiverItemVO.getTransferType2());
            i++;
            pstmtUpdate.setString(i, receiverItemVO.getInterfaceReferenceID2());
            i++;
            pstmtUpdate.setString(i, receiverItemVO.getUpdateStatus2());
            i++;
            pstmtUpdate.setString(i, receiverItemVO.getTransferType1());
            i++;
            pstmtUpdate.setString(i, receiverItemVO.getInterfaceReferenceID1());
            i++;
            pstmtUpdate.setString(i, receiverItemVO.getUpdateStatus1());
            i++;
            pstmtUpdate.setLong(i, receiverItemVO.getAdjustValue());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getPreviousPromoBalance());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getNewPromoBalance());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getPreviousPromoExpiry());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getNewPromoExpiry());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getPromoStatus());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getInterfacePromoStatus());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getCosStatus());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getInterfacePromoStatus());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getNewServiceClssCode());
            i++;
            pstmtUpdate.setLong(i, p_c2sTransferVO.getSenderTransferValue());
            i++;
            if (BTSLUtil.isNullString(p_c2sTransferVO.getTxnType())) {
                pstmtUpdate.setString(i, PretupsI.TXNTYPE_X);
                i++;
            } else {
                pstmtUpdate.setString(i, p_c2sTransferVO.getTxnType());
                i++;
            }
            pstmtUpdate.setString(i, p_c2sTransferVO.getSubService());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getPenaltyDetails());
            i++;
            pstmtUpdate.setLong(i, p_c2sTransferVO.getRoamPenalty());
            i++;
            pstmtUpdate.setLong(i, p_c2sTransferVO.getRoamPenaltyOwner());
            i++;
            pstmtUpdate.setString(i, p_c2sTransferVO.getTransferID());
            i++;
            pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_c2sTransferVO.getTransferID())));
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0) {
                if ("IAT".equalsIgnoreCase(p_c2sTransferVO.getExtCreditIntfceType())) {
                    updateCount = 0;
                    updateCount = updateC2SIATTransferItemDetails(p_con, (IATTransferItemVO) p_c2sTransferVO.getTransferItemList().get(2), p_c2sTransferVO.getTransferID());
                }
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException " );
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName, loggerValue);
            updateCount = 0;
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqle.getMessage());
            
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[v]", p_c2sTransferVO.getTransferID(),
                p_c2sTransferVO.getSenderMsisdn(), p_c2sTransferVO.getNetworkCode(),  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            LOG.error(methodName,  loggerValue );
            updateCount = 0;
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[updateC2STransferDetailsReversal]",
                p_c2sTransferVO.getTransferID(), p_c2sTransferVO.getSenderMsisdn(), p_c2sTransferVO.getNetworkCode(),  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting updateCount=");
            	loggerValue.append(updateCount);
                LOG.debug(methodName,  loggerValue);
            }
        }// end of finally
    }

    // for c2s reverse transactions by Akanksha
    /*public ArrayList<ChannelTransferVO> getReversalTransactions(Connection p_con, ChannelTransferVO channeltransferVO, String senderMsisdn) throws BTSLBaseException {

        final String methodName = "getReversalTransactions";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered channeltransferVO:");
        	loggerValue.append(channeltransferVO.toString());
        	
            LOG.debug(methodName,  loggerValue );
        }
        final ArrayList<ChannelTransferVO> al = new ArrayList<ChannelTransferVO>();

        final String txID = channeltransferVO.getTransferID();
        final String msisdn = channeltransferVO.getToUserMsisdn();
        final String  time = SystemPreferences.TIME_FOR_REVERSAL;
        final Date date = new Date();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
        	pstmtSelect=c2STransferQry.getReversalTransactionsQry(msisdn, p_con, senderMsisdn, txID, date, time);
            rs = pstmtSelect.executeQuery();
            int j = 0;
            while (rs.next()) {
                channeltransferVO = new ChannelTransferVO();
                channeltransferVO.setUserMsisdn(rs.getString("sender_msisdn"));
                channeltransferVO.setTransferID(rs.getString("transfer_id"));
                channeltransferVO.setSenderCategory(rs.getString("sender_category"));
                channeltransferVO.setToUserMsisdn(rs.getString("receiver_msisdn"));
                channeltransferVO.setTransferType(rs.getString("subscriber_type"));
                channeltransferVO.setServiceClass(rs.getString("service_class_code"));
                channeltransferVO.setTransferMRP(rs.getLong("transfer_value"));
                channeltransferVO.setDisplayTransferMRP(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                channeltransferVO.setIndex(j);
                al.add(channeltransferVO);
                j++;
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            LOG.error(methodName,  loggerValue );
            LOG.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[getReversalTransactions]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	 loggerValue.setLength(0);
         	loggerValue.append("Exception ");
         	loggerValue.append( e.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
         	loggerValue.append("Exception:" );
         	loggerValue.append(e.getMessage());
            LOG.error(methodName, loggerValue);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[getReversalTransactions]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  al =");
            	loggerValue.append(al);
                LOG.debug(methodName,  loggerValue);
            }
        }
        return al;
    }*/

    /**
     * This method load channelTransferVO of last transfer ID 
     * 
     * @author manoj
     * @param p_con
     * @param p_lastTransferID
     *            java.lang.String
     * @return ChannelTransferItmsVO
     * @throws BTSLBaseException
     */
    public C2STransferVO loadLastTransfersStatusVOForC2S(Connection p_con, String p_lastTransferID) throws BTSLBaseException {
        final String methodName = "loadLastTransfersStatusVOForC2S";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered   p_lastTransferID " + p_lastTransferID);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        C2STransferVO transferVO = null;
        //local index implemented
        final StringBuffer strBuff = new StringBuffer(
            " SELECT C2S.transfer_id,C2S.sender_id, C2S.service_type,C2S.sender_msisdn,C2S.receiver_msisdn,C2S.transfer_value,C2S.transfer_status,C2S.receiver_transfer_value, ");
        strBuff.append(" KV.value,C2S.transfer_date_time,P.short_name,P.product_short_code,ST.name,C2S.error_code ");
        strBuff.append(" FROM c2s_transfers C2S,products P, key_values KV,service_type ST ");
        strBuff.append(" WHERE transfer_id =? AND C2S.transfer_date=? AND C2S.product_code=P.product_code AND C2S.transfer_status=KV.key AND KV.type=?  ");
        strBuff.append(" AND C2S.service_type=ST.service_type AND ST.module=? ");
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_lastTransferID);
            pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_lastTransferID)));
            pstmt.setString(3, PretupsI.KEY_VALUE_C2C_STATUS);
            pstmt.setString(4, PretupsI.C2S_MODULE);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                transferVO = new C2STransferVO();
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setSenderID(rs.getString("sender_id"));
                transferVO.setServiceType(rs.getString("name"));
                transferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                transferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                transferVO.setTransferValue(rs.getLong("transfer_value"));
                transferVO.setTransferStatus(rs.getString("transfer_status"));
                transferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                transferVO.setProductName(rs.getString("short_name"));
                transferVO.setProductShortCode(rs.getString("product_short_code"));
                transferVO.setReceiverTransferValue(rs.getLong("receiver_transfer_value"));
                transferVO.setValue(rs.getString("value"));
                if(null!=rs.getString("error_code"))
                    transferVO.setErrorCode (rs.getString("error_code"));
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadLastTransfersStatusVOForC2S]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadLastTransfersStatusVOForC2S]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting:  transferVO =" + transferVO);
            }
        }
        return transferVO;
    }

    /**
     * This method load transaction details using external reference num 
     * 
     * @author vipul
     * @param p_con
     * @param p_referenceID
     *            java.lang.String
     * @return ChannelTransferItmsVO
     * @throws BTSLBaseException
     */
    public C2STransferVO loadLastTransfersStatusVOForC2SWithExtRefNum(Connection p_con, String p_referenceID, RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "loadLastTransfersStatusVOForC2SWithExtRefNum";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered   p_referenceID=");
        	loggerValue.append(p_referenceID);
        	loggerValue.append(" p_requestVO=");
        	loggerValue.append(p_requestVO);
            LOG.debug(methodName,  loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        C2STransferVO transferVO = null;

        String sqlSelect = c2STransferQry.loadLastTransfersStatusVOForC2SWithExtRefNumQry();

        final java.sql.Date createdOnDate = BTSLUtil.getSQLDateFromUtilDate(p_requestVO.getCreatedOn());
        final java.sql.Date previousdate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(createdOnDate, -3));

        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY createdOnDate=");
        	loggerValue.append(createdOnDate);
        	loggerValue.append(" previousdate=");
        	loggerValue.append(previousdate);
            LOG.debug(methodName,  loggerValue );
        }

        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            LOG.debug(methodName,  loggerValue);
        }
        try {
            int i = 1;
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setDate(i, previousdate);
            i++;
            pstmt.setDate(i, createdOnDate);
            i++;
            pstmt.setString(i, p_referenceID);
            i++;
            pstmt.setString(i, PretupsI.KEY_VALUE_C2C_STATUS);
            i++;
            pstmt.setString(i, PretupsI.C2S_MODULE);
            i++;
            rs = pstmt.executeQuery();
            if (rs.next()) {
                transferVO = new C2STransferVO();
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setSenderID(rs.getString("sender_id"));
                transferVO.setServiceType(rs.getString("name"));
                transferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                transferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                transferVO.setTransferValue(rs.getLong("transfer_value"));
                transferVO.setTransferStatus(rs.getString("transfer_status"));
                transferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                transferVO.setProductName(rs.getString("short_name"));
                transferVO.setProductShortCode(rs.getString("product_short_code"));
                transferVO.setReceiverTransferValue(rs.getLong("receiver_transfer_value"));
                transferVO.setValue(rs.getString("value"));
                if(null!=rs.getString("error_code"))
                    transferVO.setErrorCode (rs.getString("error_code"));
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : " );
        	loggerValue.append(sqe);
            LOG.error(methodName, loggerValue );
            LOG.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:" );
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "C2STransferDAO[loadLastTransfersStatusVOForC2SWithExtRefNum]", "", "", "", loggerValue.toString() );
            throw new BTSLBaseException(this, "", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : " );
        	loggerValue.append(ex);
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "C2STransferDAO[loadLastTransfersStatusVOForC2SWithExtRefNum]", "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  transferVO =");
            	loggerValue.append(transferVO);
                LOG.debug(methodName,  loggerValue );
            }
        }
        return transferVO;
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
     * @param p_networkCode
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
    public ArrayList loadC2STransferVOList(Connection p_con, String p_networkCode, Date p_fromDate, Date p_toDate, ArrayList userList, String p_receiverMsisdn, String p_transferID, String p_serviceType, String senderCat) throws BTSLBaseException {

        final String methodName = "loadC2STransferVOList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(", p_senderMsisdn:" );
        	loggerValue.append(userList);
        	loggerValue.append(" p_fromDate:" );
        	loggerValue.append(p_fromDate);
        	loggerValue.append(" p_toDate: ");
        	loggerValue.append(p_toDate);
        	loggerValue.append("'p_receiverMsisdn=");
        	loggerValue.append(p_receiverMsisdn);
        	loggerValue.append(",p_transferID=");
        	loggerValue.append(p_transferID);
        	loggerValue.append(",p_serviceType=");
        	loggerValue.append(p_serviceType);
        	loggerValue.append( "senderCat ");
        	loggerValue.append(senderCat);
            LOG.debug(methodName,loggerValue );
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferVO c2sTransferVO = null;
        final ArrayList c2sTransferVOList = new ArrayList();
        ListValueVO user = new ListValueVO();
        try {
          
        	pstmtSelect = c2STransferQry.loadC2STransferVOListQry(p_con,p_networkCode,p_fromDate,p_toDate,userList,p_receiverMsisdn,p_transferID,p_serviceType,senderCat,user ); 
            rs = pstmtSelect.executeQuery();
            final ArrayList sourceTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSACTION_SOURCE_TYPE, true);
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
                c2sTransferVO.setActiveUserId(rs.getString("active_user_id"));

                c2sTransferVO.setCellId(rs.getString("cell_id"));
                c2sTransferVO.setSwitchId(rs.getString("switch_id"));

                if (_operatorUtilI.getNewDataAftrTbleMerging(p_fromDate, p_toDate))

                {
                    c2sTransferVO.setBonusSummarySting(rs.getString("bonus_details"));
                    c2sTransferVO.setPreviousPromoBalance(rs.getString("promo_previous_balance"));
                    c2sTransferVO.setNewPromoBalance(rs.getString("promo_post_balance"));
                    c2sTransferVO.setPreviousPromoExpiry(rs.getString("promo_previous_expiry"));
                    c2sTransferVO.setNewPromoExpiry(rs.getString("promo_new_expiry"));
                }

                c2sTransferVOList.add(c2sTransferVO);

            }

        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2STransferVOList]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2STransferVOList]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting c2sTransferVOList.size()=" + c2sTransferVOList.size());
            }
        }// end of finally

        return c2sTransferVOList;
    }

    /**
     * This method load the list of the records having AMBIGUOUS/UNDERPROCESS
     * status.
     * 
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @param p_networkCode
     * @param p_serviceType
     * @return
     * @throws BTSLBaseException
     *             ArrayList
     */
    public ArrayList loadC2SReconciliationList(Connection p_con, Date p_fromDate, Date p_toDate, String p_networkCode, String p_serviceType) throws BTSLBaseException {

        final String methodName = "loadC2SReconciliationList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  p_fromDate:");
        	loggerValue.append(p_fromDate);
        	loggerValue.append(" p_toDate: ");
        	loggerValue.append(p_toDate);
        	loggerValue.append(",p_serviceType=");
        	loggerValue.append(p_serviceType);
            LOG.debug(methodName, loggerValue );
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferVO c2sTransferVO = null;
        ChannelUserVO channelUserVO = null;
        UserPhoneVO userPhoneVO = null;
        final ArrayList c2sTransferVOList = new ArrayList();
        try {
        	pstmtSelect = c2STransferQry.loadC2SReconciliationListQry( p_con, p_networkCode, p_fromDate, p_toDate,  p_serviceType);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                c2sTransferVO = new C2STransferVO();

                c2sTransferVO.setProductName(rs.getString("short_name"));
                c2sTransferVO.setServiceName(rs.getString("name"));
                c2sTransferVO.setSenderName(rs.getString("user_name"));
                c2sTransferVO.setOwnerUserID(rs.getString("owner_id"));
                c2sTransferVO.setErrorMessage(rs.getString("value"));
                c2sTransferVO.setTransferID(rs.getString("transfer_id"));
                c2sTransferVO.setTransferDate(rs.getDate("transfer_date"));
                c2sTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                c2sTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time")));
                c2sTransferVO.setNetworkCode(rs.getString("network_code"));
                c2sTransferVO.setSenderNetworkCode(c2sTransferVO.getNetworkCode());
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
                c2sTransferVO.setTransferStatus(rs.getString("txn_status"));
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
                c2sTransferVO.setTxnStatus(rs.getString("transfer_status"));
                c2sTransferVO.setSourceType(rs.getString("source_type"));
                c2sTransferVO.setSerialNumber(rs.getString("serial_number"));
                c2sTransferVO.setExtCreditIntfceType(rs.getString("ext_credit_intfce_type"));
                c2sTransferVO.setSubService(rs.getString("SUB_SERVICE"));
                channelUserVO = new ChannelUserVO();
                channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelUserVO.setCommissionProfileSetID(rs.getString("commission_profile_id"));
                channelUserVO.setCategoryCode(rs.getString("sender_category"));
                userPhoneVO = new UserPhoneVO();
                userPhoneVO.setCountry(rs.getString("phcountry"));
                userPhoneVO.setPhoneLanguage(rs.getString("phone_language"));
                userPhoneVO.setMsisdn(rs.getString("msisdn"));
                userPhoneVO.setLocale(new Locale(userPhoneVO.getPhoneLanguage(), userPhoneVO.getCountry()));
                channelUserVO.setUserPhoneVO(userPhoneVO);
                c2sTransferVO.setSenderVO(channelUserVO);
                c2sTransferVOList.add(c2sTransferVO);
            }

        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName,  loggerValue);
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2SReconciliationList]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            LOG.error(methodName,  loggerValue );
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2SReconciliationList]", "", "",
                "", loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting c2sTransferVOList.size()=");
            	loggerValue.append(c2sTransferVOList.size());
                LOG.debug(methodName,  loggerValue);
            }
        }// end of finally

        return c2sTransferVOList;
    }

    /**
     * This method Load the last N number of C2S transactions done for a
     * subsriber by a user.
     * author Vikram kumar
     * 
     * @param p_con
     *            Connection
     * @param p_user_id
     *            String
     * @param p_noLastTxn
     *            int
     * @param serviceType
     * @return ArrayList
     * @throws BTSLBaseException
     */
    /*public ArrayList loadLastXCustTransfers(Connection p_con, String p_user_id, int p_noLastTxn, String receiverMsisdn) throws BTSLBaseException {
        final String methodName = "loadLastXCustTransfers";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered   p_user_id ");
        	loggerValue.append(p_user_id);
        	loggerValue.append( " p_noLastTxn: ");
        	loggerValue.append(p_noLastTxn);
        	loggerValue.append("receiverMsisdn ");
        	loggerValue.append(receiverMsisdn);
            LOG.debug(methodName,  loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        C2STransferVO transferVO = null;
        ArrayList transfersList = null;

        try {
        	pstmt = c2STransferQry.loadLastXCustTransfersQry(p_con,receiverMsisdn,p_user_id,p_noLastTxn,transfersList);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                transferVO = new C2STransferVO();
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                transferVO.setTransferValue(rs.getLong("net_payable_amount"));
                transferVO.setServiceType(rs.getString("service"));
                transferVO.setTransferStatus(rs.getString("transfer_status"));
                transferVO.setStatus(rs.getString("statusname"));
                transfersList.add(transferVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            LOG.error(methodName,  loggerValue);
            LOG.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadLastXCustTransfers]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:" );
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadLastXCustTransfers]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "loadLastXTransfers", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  transfersList =");
            	loggerValue.append( transfersList.size());
                LOG.debug(methodName,  loggerValue);
            }
        }
        return transfersList;
    }*/

    /**
     * Load bonus items entries
     * 
     * @param p_con
     * @param p_transferID
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadC2SBonusVOList(Connection p_con, String p_transferID) throws BTSLBaseException {

        final String methodName = "loadC2SBonusVOList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_transferID=");
        	loggerValue.append(p_transferID);
            LOG.debug(methodName,  loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList bonusTransferVOList = new ArrayList();
        BonusTransferVO bonusTransferVO = null;
        try {
            final StringBuffer selectQueryBuff = new StringBuffer();
            selectQueryBuff.append("SELECT account_id, account_code, account_name, account_type, account_rate,");
            selectQueryBuff.append("previous_balance, previous_validity, previous_grace, balance, ");
            selectQueryBuff.append("validity, grace, post_balance, post_validity, post_grace, created_on ");
            selectQueryBuff.append("from c2s_bonuses where transfer_id=? ");
            final String selectQuery = selectQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("select query:");
            	loggerValue.append(selectQuery);
                LOG.debug(methodName,  loggerValue );
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            final int i = 1;
            pstmtSelect.setString(i, p_transferID);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                bonusTransferVO = new BonusTransferVO();

                bonusTransferVO.setAccountCode(rs.getString("account_code"));
                bonusTransferVO.setAccountId(rs.getString("account_id"));
                bonusTransferVO.setAccountName(rs.getString("account_name"));
                bonusTransferVO.setAccountRate(rs.getLong("account_rate"));
                bonusTransferVO.setAccountType(rs.getString("account_type"));
                bonusTransferVO.setBalance(Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("balance"))));
                bonusTransferVO.setCreatedOn(rs.getDate("created_on"));
                bonusTransferVO.setGrace(rs.getLong("grace"));
                bonusTransferVO.setPostBalance(Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("post_balance"))));
                bonusTransferVO.setPostGrace(rs.getDate("post_grace"));
                bonusTransferVO.setPostValidity(rs.getDate("post_validity"));
                bonusTransferVO.setPreviousBalance(Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("balance"))));
                bonusTransferVO.setPreviousGrace((rs.getDate("previous_grace")));
                bonusTransferVO.setPreviousValidity(rs.getDate("previous_validity"));
                bonusTransferVO.setValidity(rs.getLong("validity"));
                bonusTransferVO.setTransferId(p_transferID);

                bonusTransferVOList.add(bonusTransferVO);
            }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            LOG.error("loadC2SBonusVO",  loggerValue );
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2SBonusVO]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            LOG.error("loadC2STransferItemsVOList",  loggerValue );
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2SBonusVO]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting bonusTransferVOList.size()=");
            	loggerValue.append(bonusTransferVOList.size());
                LOG.debug(methodName,  loggerValue );
            }
        }// end of finally
        return bonusTransferVOList;
    }

    /**
     * @param p_con
     * @param p_roleCode
     * @param p_channelUserID
     * @return
     * @throws BTSLBaseException
     * @author rahul.dutt
     *         this method is used to fetch channel transaction one by a
     *         retailer to a particular subcriber on a particular date
     */
    /*public ArrayList getChanneltransAmtDatewise(Connection p_con, String p_networkCode, Date p_fromDate, Date p_toDate, String p_senderMsisdn, String p_receiverMsisdn, String p_amount) throws BTSLBaseException {

        final String methodName = "getChanneltransAmtDatewise";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append( "Entered p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_senderMsisdn:");
        	loggerValue.append(p_senderMsisdn);
        	loggerValue.append(" p_fromDate:");
        	loggerValue.append(p_fromDate);
        	loggerValue.append(" p_toDate:");
        	loggerValue.append(p_toDate);
        	loggerValue.append(" p_receiverMsisdn=");
        	loggerValue.append(" p_amount:");
        	loggerValue.append(p_amount);
            LOG.debug( methodName,loggerValue );
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferVO c2sTransferVO = null;
        final ArrayList c2sTransferVOList = new ArrayList();
        try {
            String selectQuery = c2STransferQry.getChanneltransAmtDatewiseQry(p_fromDate,p_toDate,p_amount);
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("select query:");
            	loggerValue.append(selectQuery);
                LOG.debug(methodName,  loggerValue );
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            i++;
            pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            i++;
            pstmtSelect.setString(i, p_networkCode);
            i++;
            pstmtSelect.setString(i, p_senderMsisdn);
            i++;
            pstmtSelect.setString(i, p_receiverMsisdn);
            i++;
            if (!PretupsI.ALL.equals(p_amount)) {
                pstmtSelect.setString(i, p_amount);
                i++;
            }
            pstmtSelect.setString(i, PretupsI.C2S_ERRCODE_VALUS);
            i++;
            // ArrayList subServiceTypeList =
            // LookupsCache.loadLookupDropDown(PretupsI.SUB_SERVICES,true);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                c2sTransferVO = new C2STransferVO();
                c2sTransferVO.setTransferID(rs.getString("transfer_id"));
                c2sTransferVO.setTransferDate(rs.getDate("transfer_date"));
                c2sTransferVO.setNetworkCode(rs.getString("network_code"));
                c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                c2sTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                c2sTransferVO.setTransferStatus(rs.getString("transtatus"));
                c2sTransferVO.setServiceType(rs.getString("service_type"));
                c2sTransferVO.setQuantity(rs.getLong("quantity"));
                c2sTransferVO.setTransferValue(rs.getLong("transfer_value"));
                c2sTransferVO.setServiceName(rs.getString("servicename"));
                c2sTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                c2sTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time")));
                c2sTransferVO.setErrorCode(rs.getString("error_code"));
                c2sTransferVOList.add(c2sTransferVO);
            }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName,  loggerValue);
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[getChanneltransAmtDatewise]", "", "",
                "",  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch

        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            LOG.error(methodName,  loggerValue );
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[getChanneltransAmtDatewise]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting c2sTransferVOList.size()=");
            	loggerValue.append(c2sTransferVOList.size());
                LOG.debug(methodName, loggerValue );
            }
        }// end of finally

        return c2sTransferVOList;
    }*/

    // added by satakshi to insert reversal id for original transaction id if
    // recharge reversal is ambiguous
    public int updateC2STransferForAmbigousReversal(Connection p_con, C2STransferVO p_c2sTransferVO) throws BTSLBaseException {
        final String methodName = "updateC2STransferForAmbigousReversal";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_c2sTransferVO:");
        	loggerValue.append(p_c2sTransferVO.toString());
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            int i = 1;
            //local index implemented
            final StringBuffer updateQueryBuff = new StringBuffer(" UPDATE c2s_transfers SET   ");
            updateQueryBuff.append("modified_on=?,modified_by=?, reversal_id=? WHERE transfer_id=? and transfer_date=? ");

            final String updateQuery = updateQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Update query:" + updateQuery + " " + p_c2sTransferVO.getReferenceID());
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery);

            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_c2sTransferVO.getModifiedBy());
            pstmtUpdate.setString(i++, p_c2sTransferVO.getTransferID());
            pstmtUpdate.setString(i++, p_c2sTransferVO.getOldTxnId());
            pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_c2sTransferVO.getOldTxnId())));
            updateCount = pstmtUpdate.executeUpdate();

            return updateCount;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName,  loggerValue );
            updateCount = 0;
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
        	loggerValue.append( "SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[v]", p_c2sTransferVO.getTransferID(),
                p_c2sTransferVO.getSenderMsisdn(), p_c2sTransferVO.getNetworkCode(), loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception " );
        	loggerValue.append(e.getMessage());
            LOG.error(methodName, loggerValue);
            updateCount = 0;
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[updateC2STransferForAmbigousReversal]",
                p_c2sTransferVO.getTransferID(), p_c2sTransferVO.getSenderMsisdn(), p_c2sTransferVO.getNetworkCode(),  loggerValue.toString() );
            
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting updateCount=");
            	loggerValue.append(updateCount);
                LOG.debug(methodName, loggerValue);
            }
        }// end of finally

    }

	/**
     * @param p_con
     * @param pTpsMap
     * @param p_requestVO
     * @return
     * @throws BTSLBaseException
     */
    public int insertTPSDetails(Connection p_con,Map<Date,Integer> pTpsMap, RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "insertTPSDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "entered p_requestVO=" + p_requestVO.toString());
        }
        PreparedStatement pstmtInsert = null;
		String insertQuery = null;
        int addCount = -1;
        int i = 0;
        try {
          
			insertQuery =  c2STransferQry.insertTPSDetailsQry(pTpsMap);
            pstmtInsert = p_con.prepareStatement(insertQuery);
            
			for(Map.Entry<Date,Integer> tpsPair : pTpsMap.entrySet())
			{
				 pstmtInsert.setTimestamp(++i,BTSLUtil.getSQLDateTimeFromUtilDate(tpsPair.getKey()));
				 pstmtInsert.setString(++i, p_requestVO.getInstanceID());
				 pstmtInsert.setInt(++i, tpsPair.getValue());
				 
				  pstmtInsert.setDate(++i,BTSLUtil.getSQLDateFromUtilDate(tpsPair.getKey()));
				
			}
           
            addCount = pstmtInsert.executeUpdate();
			
        } catch (SQLException sqe) {
            if (LOG.isDebugEnabled()) {
                LOG.error(methodName, " SQL Exception::" + sqe.getMessage());
            }
            LOG.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, "error.general.processing",sqe);
        }

        catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.error(methodName, " Exception " + e.getMessage());
            }
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Finally Exiting"+addCount);
            }
        }
        return addCount;
    }
    
	/**
     * @param p_con
     * @param pDate
     * @param pHour
     * @return
     * @throws BTSLBaseException
     */
    public Map<String,String> fetchTPSDetails(Connection p_con,String pDate,String pHour) throws BTSLBaseException {
        final String methodName = "fetchTPSDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "entered ");
        }
		
		Map<String,String> tpsMap = new HashMap<>();
		
		 PreparedStatement pstmtInsert = null;
        
        int i = 0;
		ResultSet rs = null;
        try {
            final StringBuilder strBuff = new StringBuilder("SELECT COUNT (1) count, NVL(MAX (tps),0) max_tps, NVL(SUM (tps),0) ");
			strBuff.append("total_tps  FROM tps_details WHERE tps_date = ? and TO_CHAR(tps_date_time,'HH24')=?");
		
			final String fetchQuery = strBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "TPS FETCH QUERY= " + fetchQuery);
            }
            pstmtInsert = p_con.prepareStatement(fetchQuery);
            
			pstmtInsert.setDate(++i,BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(pDate)));
			pstmtInsert.setString(++i,pHour);
			
            rs  = pstmtInsert.executeQuery();
			
			if(rs.next())
			{
				tpsMap.put("COUNT",Long.toString(rs.getLong("count")));
				tpsMap.put("MAX_TPS",Long.toString(rs.getLong("max_tps")));
				tpsMap.put("TOTAL_TPS",Long.toString(rs.getLong("total_tps")));
				
			}
			
			
        } catch (SQLException sqe) {
            if (LOG.isDebugEnabled()) {
                LOG.error(methodName, " SQL Exception::" + sqe.getMessage());
            }
            LOG.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, "error.general.processing",sqe);
        }

        catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.error(methodName, " Exception " + e.getMessage());
            }
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
        	try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Finally Exiting");
            }
        }
        return tpsMap;
    }
    
    /**
	 * Method getIDList()
	 * This method is used to get Transaction id using Reference id 
	 * @param p_con Connection
	 * @param _IDlist ArrayList
	 * @author rajvi.desai
	 */ 

	 public ArrayList<String> getIDList(Connection p_con,ArrayList<String> p_IDlist) throws BTSLBaseException
    {
    	final String methodName="getIDList";
		 if (LOG.isDebugEnabled())
             LOG.debug(methodName, "Entered  with list size" + p_IDlist.size());
    	 PreparedStatement pstmt = null;
         ResultSet rs = null;
         ArrayList<String> finalSuccessList=new ArrayList<String>();
         String sqlSelect = "select ct.transfer_id from c2s_transfers ct where ct.interface_reference_id=?";
         if (LOG.isDebugEnabled())
             LOG.debug(methodName, "select Query=" +sqlSelect);
         try
         {
        	 pstmt = p_con.prepareStatement(sqlSelect);		            
	         for(int i=0;i<p_IDlist.size();i++)
	         {
	            pstmt.setString(1,p_IDlist.get(i));
	            rs = pstmt.executeQuery();
            	if(rs.next()){
            		finalSuccessList.add(rs.getString("transfer_id"));
            	}
            	pstmt.clearParameters();		            
            }
            
         }
         catch (SQLException sqle)
 		 {
 			LOG.error(methodName,"SQLException "+sqle.getMessage());
 			LOG.errorTrace(methodName, sqle);
 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2STransferDAO["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
 			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
 		}//end of catch
 		catch (Exception e)
 		{
 			LOG.error(methodName,"Exception "+e.getMessage());
 			LOG.errorTrace(methodName, e);
 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2STransferDAO["+methodName+"]","","","","Exception:"+e.getMessage());
 			throw new BTSLBaseException(this, methodName, "error.general.processing");
 		}//end of catch
 		finally
 		{
 			try{if(rs!=null) rs.close();}catch(Exception e){
 				LOG.error(methodName, "Exception:e=" + e);
 	            LOG.errorTrace(methodName, e);	
 			}
 			try{if(pstmt!=null) pstmt.close();}
 			catch(Exception e){
 				LOG.error(methodName, "Exception:e=" + e);
 	            LOG.errorTrace(methodName, e);	
 			}
 			if(LOG.isDebugEnabled())LOG.debug(methodName,"Exiting finalSuccessList size="+finalSuccessList.size());
 		 }//end of finally
 	    return finalSuccessList;	 	    
 	}
	 
	 /**
	 * @param con
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param ServiceType
	 * @return
	 * @throws BTSLBaseException
	 */
	public ArrayList<C2STransactionDetails> getC2STxnDetailsAll(Connection con,String userId,Date fromDate,Date toDate,String ServiceType) throws BTSLBaseException{


	    	final String methodName="getC2STxnDetailsAll";
			 if (LOG.isDebugEnabled())
	             LOG.debug(methodName, "Entered  with userId" + userId);
	    	 PreparedStatement pstmt = null;
	         ResultSet rs = null;
	         C2STransactionDetails c2sTransactionDetails ;
	         ArrayList<C2STransactionDetails> transactionList=new ArrayList<C2STransactionDetails>();
	         StringBuilder strBuff = new StringBuilder("SELECT * FROM( SELECT DC.TRANS_DATE,SUM(DC.TRANSACTION_COUNT) AS totalCount,SUM(DC.transaction_amount ) AS totalValue");
	         strBuff.append(" FROM DAILY_C2S_TRANS_DETAILS DC,SERVICE_TYPE ST,users u WHERE ");
	         strBuff.append(" u.USER_ID = DC.USER_ID AND u.USER_ID =? AND DC.TRANS_DATE BETWEEN ? AND ? ");
	         strBuff.append(" AND ST.SERVICE_TYPE = ? AND DC.SERVICE_TYPE = ST.SERVICE_TYPE  AND DC.TRANSACTION_COUNT <> '0' GROUP BY DC.TRANS_DATE) rs ORDER BY TRANS_DATE");
	         if (LOG.isDebugEnabled())
	             LOG.debug(methodName, "select Query=" +strBuff);
	         try
	         {
	        	 SimpleDateFormat rdf = new SimpleDateFormat(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
	        	 pstmt = con.prepareStatement(strBuff.toString());	            
	        	 int i = 1;
	        	 pstmt.setString(i++, userId);
	        	 pstmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(fromDate));
	        	 pstmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(toDate));
	        	 pstmt.setString(i++, ServiceType);
	             rs = pstmt.executeQuery();
	             while (rs.next()) {
	            	 c2sTransactionDetails= new C2STransactionDetails();
	            	 c2sTransactionDetails.setTransferCount(rs.getString("totalCount"));
	            	 c2sTransactionDetails.setTransferValue(PretupsBL.getDisplayAmount(rs.getLong("totalValue")));
	            	 c2sTransactionDetails.setTransferdate(rs.getDate("TRANS_DATE"));
	            	 c2sTransactionDetails.setTransferDateString(rdf.format(rs.getDate("TRANS_DATE")));
	            	 transactionList.add(c2sTransactionDetails);	   
	            	 }
	         }
	         catch (SQLException sqle)
	 		 {
	 			LOG.error(methodName,"SQLException "+sqle.getMessage());
	 			LOG.errorTrace(methodName, sqle);
	 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2STransferDAO["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
	 			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	 		}//end of catch
	 		catch (Exception e)
	 		{
	 			LOG.error(methodName,"Exception "+e.getMessage());
	 			LOG.errorTrace(methodName, e);
	 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2STransferDAO["+methodName+"]","","","","Exception:"+e.getMessage());
	 			throw new BTSLBaseException(this, methodName, "error.general.processing");
	 		}//end of catch
	 		finally
	 		{
	 			try{if(rs!=null) rs.close();}catch(Exception e){
	 				LOG.error(methodName, "Exception:e=" + e);
	 	            LOG.errorTrace(methodName, e);	
	 			}
	 			try{if(pstmt!=null) pstmt.close();}
	 			catch(Exception e){
	 				LOG.error(methodName, "Exception:e=" + e);
	 	            LOG.errorTrace(methodName, e);	
	 			}
	 			if(LOG.isDebugEnabled())LOG.debug(methodName,"Exiting transactionList.size() ="+transactionList.size());
	 		 }//end of finally
	 	    return transactionList;	 	    
	 	
	 }
	 
	 
	 /**
	 * @param con
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param ServiceType
	 * @return
	 * @throws BTSLBaseException
	 */
	public HashMap<String, Object> getC2STxnDetailsAllCount(Connection con,String userId,Date fromDate,Date toDate,String ServiceType) throws BTSLBaseException{


	    	final String methodName="getC2STxnDetailsAll";
			 if (LOG.isDebugEnabled())
	             LOG.debug(methodName, "Entered  with userId" + userId);
	    	 PreparedStatement pstmt = null;
	         ResultSet rs = null;
	         LinkedHashMap<String, Object> resultMap = new LinkedHashMap<String,Object>();
	         StringBuilder strBuff = new StringBuilder("SELECT SUM(DC.transaction_amount ) AS totalValue ,SUM(DC.TRANSACTION_COUNT) AS totalCount");
	         strBuff.append(" FROM DAILY_C2S_TRANS_DETAILS DC,SERVICE_TYPE ST,users u WHERE ");
	         strBuff.append(" u.USER_ID = DC.USER_ID AND u.USER_ID =? AND DC.TRANS_DATE BETWEEN ? AND ? ");
	         strBuff.append(" AND ST.SERVICE_TYPE = ? AND DC.SERVICE_TYPE = ST.SERVICE_TYPE ");
	         if (LOG.isDebugEnabled())
	             LOG.debug(methodName, "select Query=" +strBuff);
	         try
	         {
	        	 pstmt = con.prepareStatement(strBuff.toString());	            
	        	 int i = 1;
	        	 String finalSuccessCount=null;
		         String finalSuccessValue=null;
	        	 pstmt.setString(i++, userId);
	        	 pstmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(fromDate));
	        	 pstmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(toDate));
	        	 pstmt.setString(i++, ServiceType);
	             rs = pstmt.executeQuery();
	             if (rs.next()) {
	            	 finalSuccessCount = rs.getString("totalCount");
	            	 finalSuccessValue= PretupsBL.getDisplayAmount(rs.getLong("totalValue"));
	             }
	             resultMap.put("fromDate", fromDate);
	             resultMap.put("toDate", toDate);
	             resultMap.put("totalCount", finalSuccessCount);
	             resultMap.put("totalValue", finalSuccessValue);
	         }
	         catch (SQLException sqle)
	 		 {
	 			LOG.error(methodName,"SQLException "+sqle.getMessage());
	 			LOG.errorTrace(methodName, sqle);
	 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2STransferDAO["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
	 			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	 		}//end of catch
	 		catch (Exception e)
	 		{
	 			LOG.error(methodName,"Exception "+e.getMessage());
	 			LOG.errorTrace(methodName, e);
	 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2STransferDAO["+methodName+"]","","","","Exception:"+e.getMessage());
	 			throw new BTSLBaseException(this, methodName, "error.general.processing");
	 		}//end of catch
	 		finally
	 		{
	 			try{if(rs!=null) rs.close();}catch(Exception e){
	 				LOG.error(methodName, "Exception:e=" + e);
	 	            LOG.errorTrace(methodName, e);	
	 			}
	 			try{if(pstmt!=null) pstmt.close();}
	 			catch(Exception e){
	 				LOG.error(methodName, "Exception:e=" + e);
	 	            LOG.errorTrace(methodName, e);	
	 			}
	 			if(LOG.isDebugEnabled())LOG.debug(methodName,"Exiting resultMap ="+resultMap);
	 		 }//end of finally
	 	    return 	resultMap;	    
	 	
	 }
	 /**
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_boolean
     * @return
     * @throws BTSLBaseException
     */
    public int addC2STransferDetailsfromRedis(Connection p_con, C2STransferVO p_c2sTransferVO, boolean p_boolean) throws BTSLBaseException {
        final String methodName = "addC2STransferDetailsfromRedis";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_c2sTransferVO:");
        	loggerValue.append(p_c2sTransferVO.toString());
        	loggerValue.append("p_boolean:" );
        	loggerValue.append(p_boolean);
            LOG.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {

            C2STransferItemVO senderVO = null;
            C2STransferItemVO receiverVO = null;
            C2STransferItemVO creditBackVO = null;
            C2STransferItemVO reconcileVO = null;
            final NetworkPrefixVO networkPrefixVO1 = PretupsBL.getNetworkDetails(p_c2sTransferVO.getReceiverMsisdn(), PretupsI.USER_TYPE_RECEIVER);

            int item_size = 0;
            final ArrayList transferItemList = p_c2sTransferVO.getTransferItemList();
            if (transferItemList != null) {
                item_size = p_c2sTransferVO.getTransferItemList().size();
            } else {
                item_size = 0;
            }

            boolean insertIAT = false;
            if (item_size == 0) {
                p_boolean = false;
            } else {
                for (int l = 0, m = p_c2sTransferVO.getTransferItemList().size(); l < m; l++) {
                    final Object obj = p_c2sTransferVO.getTransferItemList().get(l);
                    if (!(obj instanceof C2STransferItemVO)) {
                        if (obj instanceof IATTransferItemVO) {
                            insertIAT = true;
                        }
                        continue;
                    }
                    final C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) obj;
                    if (c2STransferItemVO.getSNo() == 1) {
                        senderVO = c2STransferItemVO;
                    } else if (c2STransferItemVO.getSNo() == 2) {
                        receiverVO = c2STransferItemVO;
                    } else if (c2STransferItemVO.getSNo() > 3 || (c2STransferItemVO.getSNo() == 3 && c2STransferItemVO.getTransferType().equals(PretupsI.TRANSFER_TYPE_RECON))) {
                        reconcileVO = c2STransferItemVO;
                    } else if (c2STransferItemVO.getSNo() == 3) {
                        creditBackVO = c2STransferItemVO;
                    }

                }

            }

            int i = 1;
            String tableName = BTSLUtil.getTableName("c2s_transfers");
            final StringBuffer insertQueryBuff = new StringBuffer(" INSERT INTO ");
            insertQueryBuff.append(tableName).append(" (transfer_id,transfer_date,transfer_date_time,network_code,sender_id,sender_category,product_code,sender_msisdn, ");
            insertQueryBuff.append(" receiver_msisdn,receiver_network_code,transfer_value,error_code,request_gateway_type,request_gateway_code, ");
            insertQueryBuff.append(" grph_domain_code,reference_id,service_type,pin_sent_to_msisdn,language,country,skey,skey_generation_time, ");
            insertQueryBuff.append(" skey_sent_to_msisdn,request_through_queue,quantity,created_by,created_on,modified_by,modified_on,transfer_status, ");
            insertQueryBuff.append(" card_group_set_id,version,card_group_id,sender_transfer_value,receiver_access_fee,receiver_tax1_type, ");
            insertQueryBuff.append(" receiver_tax1_rate,receiver_tax1_value,receiver_tax2_type,receiver_tax2_rate,receiver_tax2_value, ");
            insertQueryBuff.append(" receiver_validity,receiver_transfer_value,receiver_bonus_value,receiver_grace_period,receiver_bonus_validity, ");
            insertQueryBuff.append(" card_group_code,receiver_valperiod_type,temp_transfer_id,transfer_profile_id,commission_profile_id,source_type,sub_service,start_time,end_time,serial_number,ext_credit_intfce_type,ACTIVE_USER_ID ");
            insertQueryBuff.append(" ,subs_sid,cell_id,switch_id ,txn_type, otf_applicable");
            
            if (networkPrefixVO1 != null && receiverVO == null) {
                insertQueryBuff.append(" ,prefix_id ");
            }

            if (p_boolean) {
                boolean comma_req = false;
                if (senderVO != null) {
                    insertQueryBuff.append(",sender_previous_balance, sender_post_balance,debit_status,service_provider_name,SENDER_PREFIX_ID ");
                    comma_req = true;
                }
                if (receiverVO != null) {
                    if (comma_req) {
                        insertQueryBuff.append(", ");
                    }
                    insertQueryBuff.append(" RECEIVER_PREVIOUS_BALANCE,RECEIVER_POST_BALANCE,transfer_type,validation_status,   ");
                    insertQueryBuff.append(" credit_status,service_class_id,protocol_status,account_status,interface_type,interface_id,  ");
                    insertQueryBuff.append(" interface_response_code,interface_reference_id,subscriber_type,service_class_code, ");
                    insertQueryBuff.append(" msisdn_previous_expiry,msisdn_new_expiry, first_call,prefix_id ,RCVR_INTRFC_REFERENCE_ID");
                    comma_req = true;
                }
                if (creditBackVO != null) {
                    if (comma_req) {
                        insertQueryBuff.append(", ");
                    }
                    insertQueryBuff.append(" SENDER_CR_BK_PREV_BAL,  ");
                    insertQueryBuff.append(" SENDER_CR_BK_POST_BAL,  ");
                    insertQueryBuff.append(" credit_back_status ");
                    comma_req = true;
                }
                if (reconcileVO != null) {
                    if (comma_req) {
                        insertQueryBuff.append(", ");
                    }
                    insertQueryBuff.append(" SENDER_CR_SETL_PREV_BAL,SENDER_CR_SETL_POST_BAL,reconcile_status  ");
                }

            }
            insertQueryBuff.append(",reversal_id,info1,info2,info3,info4,info5,info6,info7,info8,info9,info10,multicurrency_detail ,bonus_amount "); 
            if(p_c2sTransferVO.isRoam()&&p_c2sTransferVO.isStopAddnCommission()){
            	insertQueryBuff.append(",penalty, owner_penalty, penalty_details ");	
            }
            insertQueryBuff.append(" ,differential_applicable,differential_given , bonus_details ");
            insertQueryBuff.append(" ,adjust_dr_txn_type, adjust_dr_txn_id, adjust_dr_update_status, ");
            insertQueryBuff.append(" adjust_cr_txn_type, adjust_cr_txn_id, adjust_cr_update_status , adjust_value, ");
            insertQueryBuff.append(" promo_previous_balance, promo_post_balance, promo_previous_expiry, promo_new_expiry ");
            insertQueryBuff.append(",promo_status, interface_promo_response_code,cos_status, interface_cos_response_code, new_service_class_code");
            
            if (!BTSLUtil.isNullString(p_c2sTransferVO.getExtCreditIntfceType())) {
            	insertQueryBuff.append(" ,ext_credit_intfce_type");
            }
            insertQueryBuff.append(" ,lms_profile, lms_version ");
            if (networkPrefixVO1 != null && receiverVO == null) {
                insertQueryBuff.append(") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ,?,?");
            } else {
                insertQueryBuff.append(") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ,?,?");
            }

            insertQueryBuff.append(",?,?");

            if (p_boolean) {
                boolean comma_req = false;
                if (senderVO != null) {
                    insertQueryBuff.append(",?,?,?,?,?  ");
                    comma_req = true;
                }
                if (receiverVO != null) {
                    if (comma_req) {
                        insertQueryBuff.append(",");
                    }
                    insertQueryBuff.append("?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ");

                    comma_req = true;
                }
                if (creditBackVO != null) {
                    if (comma_req) {
                        insertQueryBuff.append(",");
                    }
                    insertQueryBuff.append("?, ?, ?");
                    comma_req = true;
                }
                if (reconcileVO != null) {
                    if (comma_req) {
                        insertQueryBuff.append(",");
                    }
                    insertQueryBuff.append("?,?,?,?");
                }

            }
            insertQueryBuff.append(",?,?,?,?,?,?,?,?,?,?,?,?,?");

            if(p_c2sTransferVO.isRoam()&&p_c2sTransferVO.isStopAddnCommission()){
            	insertQueryBuff.append(",?,?, ? ");	
            }
     
            insertQueryBuff.append(",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ");	
            if (!BTSLUtil.isNullString(p_c2sTransferVO.getExtCreditIntfceType())) {
            	insertQueryBuff.append(" ,? ");
            }
            insertQueryBuff.append(" ,?,? ");
            
            insertQueryBuff.append(" ) ");

            final String insertQuery = insertQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Insert query:");
            	loggerValue.append(insertQuery);
                LOG.debug(methodName,  loggerValue );
            }

            pstmtInsert = p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(i, p_c2sTransferVO.getTransferID());
            i++;
            pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_c2sTransferVO.getTransferDate()));
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getTransferDateTime()));
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getNetworkCode());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getSenderID());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getSenderCategoryCode());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getProductCode());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getSenderMsisdn());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getReceiverMsisdn());
            i++;
            if (PretupsI.IAT_TRANSACTION_TYPE.equals(p_c2sTransferVO.getExtCreditIntfceType())) {
                pstmtInsert.setString(i, p_c2sTransferVO.getIatTransferItemVO().getIatRecNWCode());
                i++;
            } else {
                pstmtInsert.setString(i, p_c2sTransferVO.getReceiverNetworkCode());
                i++;
            }
            pstmtInsert.setLong(i, p_c2sTransferVO.getRequestedAmount());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getErrorCode());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getRequestGatewayType());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getRequestGatewayCode());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getGrphDomainCode());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getReferenceID());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getServiceType());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getPinSentToMsisdn());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getLanguage());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getCountry());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getSkey());
            i++;
            if (p_c2sTransferVO.getSkeyGenerationTime() == null) {
                pstmtInsert.setNull(i, Types.TIMESTAMP);
                i++;
            } else {
                pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getSkeyGenerationTime()));
                i++;
            }
            pstmtInsert.setString(i, p_c2sTransferVO.getSkeySentToMsisdn());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getRequestThroughQueue());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getQuantity());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getCreatedBy());
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getCreatedOn()));
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getModifiedBy());
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getModifiedOn()));
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getTransferStatus());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getCardGroupSetID());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getVersion());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getCardGroupID());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getSenderTransferValue());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getReceiverAccessFee());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getReceiverTax1Type());
            i++;
            pstmtInsert.setDouble(i, p_c2sTransferVO.getReceiverTax1Rate());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getReceiverTax1Value());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getReceiverTax2Type());
            i++;
            pstmtInsert.setDouble(i, p_c2sTransferVO.getReceiverTax2Rate());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getReceiverTax2Value());
            i++;
            pstmtInsert.setInt(i, p_c2sTransferVO.getReceiverValidity());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getReceiverTransferValue());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getReceiverBonusValue());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getReceiverGracePeriod());
            i++;
            if (PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL.equals(p_c2sTransferVO.getServiceType())) {
                pstmtInsert.setInt(i, 0);
                i++;
            } else {
                pstmtInsert.setInt(i, p_c2sTransferVO.getReceiverBonusValidity());
                i++;
            }
            pstmtInsert.setString(i, p_c2sTransferVO.getCardGroupCode());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getReceiverValPeriodType());
            i++;
        	pstmtInsert.setString(i, p_c2sTransferVO.getTempId());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getTransferProfileID());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getCommissionProfileSetID());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getSourceType());
            i++;

                pstmtInsert.setString(i, p_c2sTransferVO.getSubService());
                i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getRequestStartTime());
            i++;
            pstmtInsert.setLong(i, System.currentTimeMillis());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getSerialNumber());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getExtCreditIntfceType());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getActiveUserId());
            i++;
            
            if(((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.ALIAS_TO_BE_ENCRYPTED))).booleanValue()){
          	  if(!BTSLUtil.isNullString(p_c2sTransferVO.getSubscriberSID()))
          		  pstmtInsert.setString(i, new CryptoUtil().encrypt(p_c2sTransferVO.getSubscriberSID(),Constants.KEY));
          	  else
          		  pstmtInsert.setString(i, p_c2sTransferVO.getSubscriberSID());
            }
            else
          	  pstmtInsert.setString(i, p_c2sTransferVO.getSubscriberSID());
            
           
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getCellId());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getSwitchId());
            i++;
            if (BTSLUtil.isNullString(p_c2sTransferVO.getTxnType())) {
                pstmtInsert.setString(i, PretupsI.TXNTYPE_T);
                i++;
            } else {
                pstmtInsert.setString(i, p_c2sTransferVO.getTxnType());
                i++;
            }
            pstmtInsert.setString(i, p_c2sTransferVO.isOtfApplicable());
            i++;
            if (networkPrefixVO1 != null && receiverVO == null) {
                pstmtInsert.setLong(i, networkPrefixVO1.getPrefixID());
                i++;
            }
            if (p_boolean) {
                if (senderVO != null) {
                    pstmtInsert.setLong(i, senderVO.getPreviousBalance());
                    i++;
                    pstmtInsert.setLong(i, senderVO.getPostBalance());
                    i++;
                    pstmtInsert.setString(i, senderVO.getTransferStatus());
                    i++;
                    pstmtInsert.setString(i, senderVO.getServiceProviderName());
                    i++;
                    pstmtInsert.setLong(i, senderVO.getPrefixID());
                    i++;
                }
                if (receiverVO != null) {
                    pstmtInsert.setLong(i, receiverVO.getPreviousBalance());
                    i++;
                    pstmtInsert.setLong(i, receiverVO.getPostBalance());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getTransferType());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getValidationStatus());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getTransferStatus());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getServiceClass());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getProtocolStatus());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getAccountStatus());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getInterfaceType());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getInterfaceID());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getInterfaceResponseCode());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getInterfaceReferenceID());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getSubscriberType());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getServiceClassCode());
                    i++;
                    pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(receiverVO.getPreviousExpiry()));
                    i++;
                    pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(receiverVO.getNewExpiry()));
                    i++;
                    pstmtInsert.setString(i, receiverVO.getFirstCall());
                    i++;
                    pstmtInsert.setLong(i, receiverVO.getPrefixID());
                    i++;
                    pstmtInsert.setString(i, receiverVO.getReferenceID());
                    i++;
                }
                if (creditBackVO != null) {
                    pstmtInsert.setLong(i, creditBackVO.getPreviousBalance());
                    i++;
                    pstmtInsert.setLong(i, creditBackVO.getPostBalance());
                    i++;
                    pstmtInsert.setString(i, creditBackVO.getTransferStatus());
                    i++;
                }
                if (reconcileVO != null) {
                    pstmtInsert.setLong(i, reconcileVO.getPreviousBalance());
                    i++;
                    pstmtInsert.setLong(i, reconcileVO.getPostBalance());
                    i++;
                    pstmtInsert.setString(i, reconcileVO.getTransferStatus());
                    i++;
                }
            }
            pstmtInsert.setString(i, p_c2sTransferVO.getReverseTransferID());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo1());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo2());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo3());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo4());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo5());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo6());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo7());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo8());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo9());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInfo10());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getMultiCurrencyDetailVO());
            i++;
			pstmtInsert.setLong(i, p_c2sTransferVO.getPromoBonus());//75
            i++;
            
            if (p_c2sTransferVO.isRoam()&&p_c2sTransferVO.isStopAddnCommission()) {
                pstmtInsert.setLong(i, p_c2sTransferVO.getRoamPenalty());
                i++;
                pstmtInsert.setLong(i, p_c2sTransferVO.getRoamPenaltyOwner());
            	if(p_c2sTransferVO.getOwnerCommProfile()==null)
            		p_c2sTransferVO.setOwnerCommProfile("0");
            	if(BTSLUtil.isNullString(p_c2sTransferVO.getRoamDiffAmount()))
            		p_c2sTransferVO.setRoamDiffAmount("0");
            	else if(Long.parseLong(p_c2sTransferVO.getRoamDiffAmount())<0)
            		p_c2sTransferVO.setRoamDiffAmount("0");
            	 i++;
            	 pstmtInsert.setString(i, p_c2sTransferVO.getRoamDiffAmount()+":"+p_c2sTransferVO.getRoamPenaltyPercentage()+":"+p_c2sTransferVO.getRoamPenaltyPercentageOwner()+":"+p_c2sTransferVO.getOwnerCommProfile());
            	 i++;
            }
           
            pstmtInsert.setString(i, p_c2sTransferVO.getDifferentialApplicable());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getDifferentialGiven());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getBonusSummarySting());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getTransferType2());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInterfaceReferenceID2());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getUpdateStatus2());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getTransferType1());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInterfaceReferenceID1());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getUpdateStatus1());
            i++;
            pstmtInsert.setLong(i, p_c2sTransferVO.getAdjustValue());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getPreviousPromoBalance());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getNewPromoBalance());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getPreviousPromoExpiry());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getNewPromoExpiry());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getPromoStatus());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInterfacePromoStatus());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getCosStatus());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getInterfacePromoStatus());
            i++;
            pstmtInsert.setString(i, p_c2sTransferVO.getNewServiceClssCode());
            i++;
            if (!BTSLUtil.isNullString(p_c2sTransferVO.getExtCreditIntfceType())) {
            	pstmtInsert.setString(i, p_c2sTransferVO.getExtCreditIntfceType());
                i++;
            }
            pstmtInsert.setString(i, p_c2sTransferVO.getLmsProfile());
            i++;
            if (p_c2sTransferVO.getLmsVersion() != null) {
        	   pstmtInsert.setString(i, p_c2sTransferVO.getLmsVersion());
            } else {
        	   pstmtInsert.setString(i, "");
            }
            
            addCount = pstmtInsert.executeUpdate();
            addCount = BTSLUtil.getInsertCount(addCount);// added to make code compatible with insertion in partitioned table in postgres DB


            if (addCount > 0) {
                if (p_c2sTransferVO.getTransferItemList() != null && !p_c2sTransferVO.getTransferItemList().isEmpty() && insertIAT) {
                    addCount = 0;
                    addCount = addC2STransferItemDetails(p_con, p_c2sTransferVO.getTransferItemList(), p_c2sTransferVO.getTransferID(), true);
                }
            }
            return addCount;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            LOG.error(methodName,  loggerValue);
            addCount = 0;
            LOG.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqle.getMessage());
            
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[addC2STransferDetails]",
                p_c2sTransferVO.getTransferID(), p_c2sTransferVO.getSenderMsisdn(), p_c2sTransferVO.getSenderNetworkCode(),  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	  loggerValue.setLength(0);
              loggerValue.append("Exception:");
              loggerValue.append(e.getMessage());
            LOG.error(methodName, loggerValue );
            addCount = 0;
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[addC2STransferDetails]",
                p_c2sTransferVO.getTransferID(), p_c2sTransferVO.getSenderMsisdn(), p_c2sTransferVO.getSenderNetworkCode(), loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting addCount=");
            	loggerValue.append(addCount);
                LOG.debug(methodName,  loggerValue );
            }
        }// end of finally
    }

}
