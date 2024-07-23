package com.restapi.networkadmin.o2creconciliation.service;

import com.btsl.common.*;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.transfer.businesslogic.*;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyBL;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyDAO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.PromotionDetailsVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.TargetBasedCommissionMessages;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.*;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.client.pretups.gateway.businesslogic.USSDPushMessage;
import com.restapi.networkadmin.o2creconciliation.requestVO.O2CReconciliationFailRequestVO;
import com.restapi.networkadmin.o2creconciliation.requestVO.O2CReconciliationOrderApproveRequestVO;
import com.restapi.networkadmin.o2creconciliation.responseVO.*;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.voms.vomscategory.businesslogic.VomsCategoryWebDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service("O2CReconciliationService")
public class O2CReconciliationServiceImpl implements O2CReconciliationService {

    public static final Log LOG = LogFactory.getLog(O2CReconciliationServiceImpl.class.getName());
    public static final String CLASS_NAME = "O2CReconciliationServiceImpl";

    @Override
    public O2CReconciliationListResponseVO o2cReconciliationList(Connection con, UserVO userVO, String fromDate, String toDate) throws BTSLBaseException {

        final String METHOD_NAME = "o2cReconciliationList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        try {
            Objects.requireNonNull(fromDate);
            Objects.requireNonNull(toDate);
            if (fromDate.isEmpty() || toDate.isEmpty())
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_RECON_DATE_INVALID);
        } catch (NullPointerException e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_RECON_DATE_INVALID);
        }

        String dateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);//Constants.getProperty(PretupsI.VOUCHER_EXPIRY_DATE_FORMAT);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

        try {
            LocalDate parsedFromDate = LocalDate.parse(fromDate, formatter);
            LocalDate parsedToDate = LocalDate.parse(toDate, formatter);
            long daysBetween = ChronoUnit.DAYS.between(parsedFromDate, parsedToDate);

            if (daysBetween > 30) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_RECON_DATE_INVALID_30_DAYS);
            }

            if (parsedToDate.isAfter(LocalDate.now())) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_RECON_DATE_INVALID_FUTURE);
            }

            if (parsedFromDate.isAfter(parsedToDate)) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_RECON_DATE_INVALID_RANGE);
            }

        } catch (DateTimeParseException e) {
            final String args[] = {dateFormat};
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EXT_TRF_RULE_TYPE_INVALID_DATE, args);
        }

        O2CReconciliationListResponseVO response = new O2CReconciliationListResponseVO();
        final ChannelTransferDAO transferDAO = new ChannelTransferDAO();

        final ArrayList reconciliationList = transferDAO.loadChannelTransfersList(con, userVO.getNetworkID(), userVO.getNetworkID(), PretupsI.TRANSFER_CATEGORY_SALE, fromDate, toDate);
        if (reconciliationList == null || reconciliationList.isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_RECON_LIST_NOT_FOUND);
        }

        generateO2CReconciliationListResponse(response, reconciliationList);
        return response;
    }

    @Override
    public O2CReconciliationTxnDetailVO o2cReconciliationTransactionDetail(Connection con, UserVO userVO, String transferId) throws Exception {

        final String METHOD_NAME = "o2cReconciliationTransactionDetail";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        try {
            Objects.requireNonNull(transferId);
            if (transferId.isEmpty())
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_RECON_TRANSFER_ID_INVALID);
        } catch (NullPointerException e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_RECON_TRANSFER_ID_INVALID);
        }

        final ChannelTransferDAO transferDAO = new ChannelTransferDAO();
        O2CReconciliationTxnDetailVO response = new O2CReconciliationTxnDetailVO();

        boolean secondaryNumberAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED);
        String externalTxnMandatoryDomainType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_MANDATORY_DOMAINTYPE);
        final String externalTxnMandatory = (String) PreferenceCache.getSystemPreferenceValue(PretupsI.TRANSFER_EXTERNAL_TXN_MANDATORY);

        final ChannelTransferVO txnDetail = transferDAO.loadChannelTransfersDetail(con, userVO.getNetworkID(), userVO.getNetworkID(), PretupsI.TRANSFER_CATEGORY_SALE, transferId);

        if (txnDetail.getTransferID() == null)
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_RECON_TRANSFER_ID_INVALID);

        try {
            String domainTypeCode = null;
            validateUserInformation(con, domainTypeCode, txnDetail.getToUserID(), response);
            final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
            channelTransferDAO.loadChannelTransfersVO(con, txnDetail);

            if (secondaryNumberAllow) {
                final UserDAO userDAO = new UserDAO();
                UserPhoneVO phoneVO = userDAO.loadUserAnyPhoneVO(con, txnDetail.getToMSISDN());
                try {
                    Objects.requireNonNull(phoneVO);
                } catch (NullPointerException e) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_PHONE_INFO_FOUND);
                }
                if (PretupsI.YES.equalsIgnoreCase(phoneVO.getPrimaryNumber())) {
                    response.setMobileNumber(phoneVO.getMsisdn());
                } else {
                    phoneVO = userDAO.loadUserPhoneVO(con, phoneVO.getUserId());
                    response.setMobileNumber(phoneVO.getMsisdn());
                }
            }

            if (PretupsI.TRANSFER_SUB_TYPE_VOUCHER.equals(txnDetail.getTransferSubType())) {
                ArrayList slabslist = new ArrayList();
                ArrayList vomsProductlist = null;
                ArrayList vomsCategoryList = null;
                VomsCategoryWebDAO vomsCategorywebDAO = null;
                final VomsProductDAO vomsProductDAO = new VomsProductDAO();
                ArrayList voucherTypeList = new ArrayList<VomsCategoryVO>();
                vomsCategorywebDAO = new VomsCategoryWebDAO();

                ArrayList channleVoucherItemList = new ChannelTransferDAO().loadChannelVoucherItemsList(con, txnDetail.getTransferID(), txnDetail.getTransferDate());
                int length = channleVoucherItemList.size();
                txnDetail.setChannelVoucherItemsVoList(channleVoucherItemList);
                txnDetail.setChannelTransferitemsVOList(new ChannelTransferDAO().loadChannelTransferItems(con, txnDetail.getTransferID()));
                if (length > 0) {
                    var vomsCategoryVO = new VomsCategoryVO();
                    vomsCategoryVO.setVoucherType(((ChannelVoucherItemsVO) channleVoucherItemList.get(0)).getVoucherType());
                    vomsCategoryVO.setName(((ChannelVoucherItemsVO) channleVoucherItemList.get(0)).getVoucherType());
                    vomsCategoryVO.setStatus(PretupsI.YES);
                    voucherTypeList.add(vomsCategoryVO);
                }

                ArrayList mrplist = new ArrayList();
                if (voucherTypeList.isEmpty()) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_PARENT_DENOMINATION_EXIST);
                }

                if (voucherTypeList.size() == 1) {
                    String activemrpstr = PretupsI.EMPTY;
                    VomsCategoryVO voucherCategory = (VomsCategoryVO) voucherTypeList.get(0);
                    vomsProductlist = vomsProductDAO.loadProductDetailsList(con, voucherCategory.getVoucherType(), PretupsI.SINGLE_QUOTES + VOMSI.VOMS_STATUS_ACTIVE + PretupsI.SINGLE_QUOTES, false, PretupsI.EMPTY, ((ChannelVoucherItemsVO) channleVoucherItemList.get(0)).getNetworkCode(), ((ChannelVoucherItemsVO) channleVoucherItemList.get(0)).getSegment());
                    vomsCategoryList = vomsCategorywebDAO.loadCategoryList(con, voucherCategory.getVoucherType(), VOMSI.VOMS_STATUS_ACTIVE, VOMSI.EVD_CATEGORY_TYPE_FIXED, true, txnDetail.getNetworkCode(), null);

                    if (vomsCategoryList.isEmpty()) {
                        throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_ACTIVE_PROFILE_EXISTS);
                    }

                    for (Object vomsCategoryVO : vomsCategoryList) {
                        String mrp = Double.toString(((VomsCategoryVO) vomsCategoryVO).getMrp());
                        ListValueVO lv = new ListValueVO(mrp, mrp);
                        mrplist.add(lv);
                        activemrpstr += (activemrpstr.isEmpty() ? PretupsI.EMPTY : PretupsI.COMMA) + mrp;
                    }
                }

                for (Object voucherItemVO : channleVoucherItemList) {
                    VomsBatchVO vomsOrderVO = new VomsBatchVO();
                    ChannelVoucherItemsVO voucherItem = (ChannelVoucherItemsVO) voucherItemVO;
                    vomsOrderVO.setSeq_id(Long.valueOf(voucherItem.getSNo()).intValue());
                    vomsOrderVO.setDenomination(voucherItem.getTransferMrp() + ".0");
                    vomsOrderVO.setQuantity(String.valueOf(voucherItem.getRequiredQuantity()));
                    vomsOrderVO.setFromSerialNo(voucherItem.getFromSerialNum());
                    vomsOrderVO.setToSerialNo(voucherItem.getToSerialNum());

                    ArrayList arList = new ArrayList();
                    for (Object vo : vomsProductlist) {
                        if (BTSLUtil.floatEqualityCheck((double) voucherItem.getTransferMrp(), (double) ((VomsProductVO) vo).getMrp(), "==")) {
                            arList.add(vo);
                            vomsOrderVO.setProductName(((VomsProductVO) vo).getProductName());
                        }
                    }

                    vomsOrderVO.setPreQuantity(String.valueOf(voucherItem.getRequiredQuantity()));
                    vomsOrderVO.setPreFromSerialNo(voucherItem.getFromSerialNum());
                    vomsOrderVO.setPreToSerialNo(voucherItem.getToSerialNum());
                    vomsOrderVO.setPreProductId(voucherItem.getProductId());
                    vomsOrderVO.setProductlist(arList);
                    slabslist.add(vomsOrderVO);
                }

                List<ProductDetailVO> productList = new ArrayList<>();
                for (Object transferItemsVO : txnDetail.getChannelTransferitemsVOList()) {
                    ProductDetailVO productDetailVO = populateProductDetail((ChannelTransferItemsVO) transferItemsVO);
                    productList.add(productDetailVO);
                }
                List<VoucherDetailVO> voucherList = new ArrayList<>();
                for (Object voucher : slabslist) {
                    VoucherDetailVO voucherDetailVO = populateVoucherDetail((VomsBatchVO) voucher);
                    voucherList.add(voucherDetailVO);
                }
                response.setVoucherDetails(voucherList);
                generateResponseTransactionDetail(response, productList, txnDetail);
            } else {
                List<ProductDetailVO> productList = new ArrayList<>();
                final ArrayList itemsList = ChannelTransferBL.loadChannelTransferItemsWithBalances(con, transferId, userVO.getNetworkID(), userVO.getNetworkID(), txnDetail.getToUserID());
                if (itemsList != null && !itemsList.isEmpty()) {
                    for (Object product : itemsList) {
                        ProductDetailVO productDetailVO = populateProductDetail((ChannelTransferItemsVO) product);
                        productList.add(productDetailVO);
                    }
                }
                generateResponseTransactionDetail(response, productList, txnDetail);
            }

        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }

        return response;
    }

    @Override
    public BaseResponse o2cReconciliationFail(MComConnectionI mcomCon, Connection con, UserVO userVO, O2CReconciliationFailRequestVO request) throws Exception {

        final String METHOD_NAME = "o2cReconciliationFail";
        if (LOG.isDebugEnabled()) {
            LOG.debug("o2cReconciliationFail", "Entered");
        }

        BaseResponse response = new BaseResponse();
        String transferId = request.getTransferID();
        boolean o2cEmailNotification = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.O2C_EMAIL_NOTIFICATION);
        boolean externalTxnUnique = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);

        try {

            final ChannelTransferDAO transferDAO = new ChannelTransferDAO();
            final ChannelTransferVO txnDetail = transferDAO.loadChannelTransfersDetail(con, userVO.getNetworkID(), userVO.getNetworkID(), PretupsI.TRANSFER_CATEGORY_SALE, transferId);

            o2cReconFailValidations(request);

            if (txnDetail.getTransferID() == null)
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_RECON_TRANSFER_ID_INVALID);

            if (txnDetail.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL) || txnDetail.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
                final String[] args = {transferId};
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_RECON_ALREADY_PROCESSED, args);
            }

            final Date date = new Date();
            txnDetail.setFirstApprovedBy(userVO.getUserID());
            txnDetail.setFirstApprovedOn(date);
            txnDetail.setCanceledBy(userVO.getUserID());
            txnDetail.setCanceledOn(date);
            txnDetail.setModifiedBy(userVO.getUserID());
            txnDetail.setModifiedOn(date);
            txnDetail.setPreviousStatus(PretupsI.CHANNEL_TRANSFER_ORDER_PENDING);
            txnDetail.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
            txnDetail.setReconciliationFlag(true);
            txnDetail.setExternalTxnNum(request.getExternalTxnNum());
            txnDetail.setExternalTxnDate(BTSLUtil.getDateFromDateString(request.getExternalTxnDate()));
            txnDetail.setReferenceNum(request.getReferenceNum());
            txnDetail.setPayInstrumentType(request.getPaymentInstrumentCode());
            txnDetail.setPayInstrumentNum(request.getPaymentInstrumentNumber());
            txnDetail.setPayInstrumentDate(BTSLUtil.getDateFromDateString(request.getPaymentInstrumentDate()));
            txnDetail.setPayInstrumentStatus(PretupsI.REJECT_IAT_LIST);
            txnDetail.setFirstApprovalRemark(request.getReconciliationRemarks());

            final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();

            if (externalTxnUnique) {
                final boolean isExternalTxnNotUnique = channelTransferDAO.isExtTxnExists(con, request.getExternalTxnNum(), request.getTransferID());
                if (isExternalTxnNotUnique) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MESSAGE_CHANNELTRANSFER_EXTERNALTXNNUMBERNOTUNIQUE);
                }
            }

            final int updateCount = channelTransferDAO.cancelTransferOrder(con, txnDetail, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);

            if (updateCount > 0) {
                mcomCon.finalCommit();
                final UserDAO userDAO = new UserDAO();
                final UserPhoneVO phoneVO = userDAO.loadUserPhoneVO(con, txnDetail.getToUserID());
                String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
                String language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
                final Locale locale = new Locale(language, country);
                if (phoneVO != null) {
                    txnDetail.setChannelTransferitemsVOList(channelTransferDAO.loadChannelTransferItems(con, txnDetail.getTransferID()));
                    country = phoneVO.getCountry();
                    language = phoneVO.getPhoneLanguage();
                } else {
                    final String args[] = {transferId, txnDetail.getToUserName()};
                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_PHONE_INFO_EXISTS, args);
                    response.setMessage(resmsg);
                    response.setMessageCode(PretupsErrorCodesI.NO_PHONE_INFO_EXISTS);
                    return response;
                }

                BTSLMessages messages = null;
                if (PretupsI.TRANSFER_SUB_TYPE_VOUCHER.equals(txnDetail.getTransferSubType())) {
                    txnDetail.setChannelVoucherItemsVoList(channelTransferDAO.loadChannelVoucherItemsList(con, txnDetail.getTransferID(), txnDetail.getTransferDate()));
                    final Object[] smsListArr = prepareSMSMessageListForVoucher(con, txnDetail);
                    final String[] array = {txnDetail.getTransferID(), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0])};
                    messages = new BTSLMessages(PretupsErrorCodesI.O2C_TRANSFER_VOUCHER_CANCELLED, array);
                } else {
                    final Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiver(con, txnDetail, PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_CANCEL_TXNSUBKEY, PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_CANCEL_BALSUBKEY);
                    final String[] array = {txnDetail.getTransferID(), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1])};
                    messages = new BTSLMessages(PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_CANCEL, array);
                }
                final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, txnDetail.getTransferID(), null, locale, txnDetail.getNetworkCode());
                pushMessage.push();

                ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
                if (o2cEmailNotification) {
                    final String email = channelUserWebDAO.loadUserEmail(con, txnDetail.getToUserID());
                    txnDetail.setEmail(email);
                    sendEmailNotification(con, txnDetail, channelTransferDAO, "", "One", PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_SUBJECT_FAILED, userVO);
                }
                final String args[] = {request.getTransferID()};
                String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_RECON_CANCELLATION_SUCCESS, args);
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.O2C_RECON_CANCELLATION_SUCCESS);
            } else {
                mcomCon.finalRollback();
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_RECON_CANCELLATION_FAILED);
            }

        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }

    private void generateO2CReconciliationListResponse(O2CReconciliationListResponseVO response, ArrayList reconciliationList) {

        final ArrayList<O2CReconciliationListVO> o2CReconciliationVOS = new ArrayList<>();
        for (Object o : reconciliationList) {
            O2CReconciliationListVO o2CReconciliationListVO = new O2CReconciliationListVO();
            ChannelTransferVO transferVO = (ChannelTransferVO) o;

            o2CReconciliationListVO.setTransferID(transferVO.getTransferID());
            o2CReconciliationListVO.setReferenceNumber(transferVO.getReferenceNum());
            o2CReconciliationListVO.setTransactionNumber(transferVO.getExternalTxnNum());
            o2CReconciliationListVO.setTransactionDate(transferVO.getExternalTxnDateAsString());
            o2CReconciliationListVO.setTransferDate(transferVO.getTransferDateAsString());
            o2CReconciliationListVO.setInitiatedBy(transferVO.getTransferInitatedByName());
            o2CReconciliationListVO.setTransferValue(transferVO.getTransferMRPAsString());
            o2CReconciliationListVO.setAmount(transferVO.getPayableAmountAsString());
            o2CReconciliationListVO.setTransactionStatus(transferVO.getStatus());
            o2CReconciliationListVO.setDistributionType(transferVO.getTransferSubTypeAsString());
            o2CReconciliationListVO.setMobileNumber(transferVO.getToMSISDN());
            o2CReconciliationVOS.add(o2CReconciliationListVO);
        }

        response.setO2cReconciliationListVO(o2CReconciliationVOS);
    }

    @Override
    public BaseResponse o2cOrderApproval(Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, O2CReconciliationOrderApproveRequestVO request, HttpServletRequest requestVO, HttpServletResponse response1) throws BTSLBaseException, ParseException, SQLException {
        final String METHOD_NAME = "o2cOrderApproval";
        if (LOG.isDebugEnabled()) {
            LOG.debug("o2cOrderApproval", "Entered");
        }
        BaseResponse response = new BaseResponse();
        final ChannelTransferDAO transferDAO = new ChannelTransferDAO();
        String domainCode = null;
        boolean receiverMessageSendReq = false;
        boolean ussdReceiverMessageSendReq = false;
        boolean externalTxnMandatory = false;
        String externalTxnExist = PretupsI.YES;
        String currentApprovalLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;
        boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
        boolean secondaryNumberAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED);
        boolean messageToPrimaryRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED);
        boolean o2cEmailNotification = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.O2C_EMAIL_NOTIFICATION);
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        String txnReceiverUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG));
        boolean transactionTypeAlwd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
        boolean paymentModeAlwd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD);
        boolean externalTxnNumeric = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_NUMERIC);
        boolean externalTxnUnique = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);
        final String externalTxnMandatorypref = (String) PreferenceCache.getSystemPreferenceValue(PretupsI.TRANSFER_EXTERNAL_TXN_MANDATORY);
        if (!BTSLUtil.isNullString(externalTxnMandatorypref)) {
            externalTxnMandatory = Boolean.parseBoolean(PretupsI.YES);
        }
        String Status = null;
        com.btsl.pretups.util.OperatorUtilI operatorUtili = null;
        ChannelUserWebDAO channelUserWebDAO = null;
        String transferId = request.getTransferID();

        try {
            Objects.requireNonNull(transferId);
            if (transferId.isEmpty())
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_RECON_TRANSFER_ID_INVALID);
        } catch (NullPointerException e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_RECON_TRANSFER_ID_INVALID);
        }
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EXCEPTION_WHILE_LOADING_THE_CLASS, 0, null);
        }
        final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
        final UserDAO userDAO = new UserDAO();
        CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
        Boolean isPrimaryNumber = false;
        Boolean closeTransaction = false;
        try {
            channelUserWebDAO = new ChannelUserWebDAO();
            final Date date = new Date();
            final ChannelTransferVO channelTransferVO = transferDAO.loadChannelTransfersDetail(con, userVO.getNetworkID(), userVO.getNetworkID(), PretupsI.TRANSFER_CATEGORY_SALE, request.getTransferID());
            if(request.getTransferID().isEmpty() || request.getTransferID() == null || !request.getTransferID().equals(channelTransferVO.getTransferID())){
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_RECON_TRANSFER_ID_INVALID);
            }
            final ArrayList itemsList = ChannelTransferBL.loadChannelTransferItemsWithBalances(con, request.getTransferID(), userVO.getNetworkID(), userVO.getNetworkID(), channelTransferVO.getToUserID());
            channelTransferDAO.loadChannelTransfersVO(con, channelTransferVO);
            double totTax1 = 0, totTax2 = 0, totTax3 = 0, totComm = 0, totMRP = 0, firAppQty = 0, secAppQty = 0, thrAppQty = 0, totOthComm = 0;
            long totStock = 0, totReqQty = 0, totalInitialReqQty = 0, totalOTFVal = 0;
            double mrpAmt = 0, commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
            if (itemsList != null && !itemsList.isEmpty()) {
                ChannelTransferItemsVO channelTransferItemsVO = null;
                for (int i = 0, j = itemsList.size(); i < j; i++) {
                    channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
                    if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
                        mrpAmt = channelTransferItemsVO.getReceiverCreditQty() * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
                    }
                    channelTransferItemsVO.setProductMrpStr(channelTransferVO.getTransferMRPAsString());
                    totTax1 += channelTransferItemsVO.getTax1Value();
                    totTax2 += channelTransferItemsVO.getTax2Value();
                    totTax3 += channelTransferItemsVO.getTax3Value();
                    totComm += channelTransferItemsVO.getCommValue();
                    totOthComm += channelTransferItemsVO.getOthCommValue();
                    totMRP += mrpAmt;

                    totReqQty += channelTransferItemsVO.getRequiredQuantity();
                    totalInitialReqQty += channelTransferItemsVO.getInitialRequestedQuantity();
                    totStock += channelTransferItemsVO.getAfterTransSenderPreviousStock();
                    totalOTFVal += channelTransferItemsVO.getOtfAmount();
                    commissionQty += channelTransferItemsVO.getCommQuantity();
                    senderDebitQty += channelTransferItemsVO.getSenderDebitQty();
                    receiverCreditQty += channelTransferItemsVO.getReceiverCreditQty();
                    if (!BTSLUtil.isNullString(channelTransferItemsVO.getFirstApprovedQuantity())) {
                        firAppQty += Double.parseDouble(channelTransferItemsVO.getFirstApprovedQuantity());
                    }
                    if (!BTSLUtil.isNullString(channelTransferItemsVO.getSecondApprovedQuantity())) {
                        secAppQty += Double.parseDouble(channelTransferItemsVO.getSecondApprovedQuantity());
                    }
                    if (!BTSLUtil.isNullString(channelTransferItemsVO.getThirdApprovedQuantity())) {
                        thrAppQty += Double.parseDouble(channelTransferItemsVO.getThirdApprovedQuantity());
                    }
                }
            }
            String toPrimaryMSISDN = null;
            Boolean isprimaryNumber = false;
            if (secondaryNumberAllow) {
                UserPhoneVO phoneVO = userDAO.loadUserAnyPhoneVO(con, channelTransferVO.getToMSISDN());
                if (PretupsI.YES.equalsIgnoreCase(phoneVO.getPrimaryNumber())) {
                    toPrimaryMSISDN = phoneVO.getMsisdn();
                    isprimaryNumber = true;
                } else {
                    phoneVO = userDAO.loadUserPhoneVO(con, phoneVO.getUserId());
                    toPrimaryMSISDN = phoneVO.getMsisdn();
                    isprimaryNumber = false;
                }
            }
            channelTransferVO.setChannelTransferitemsVOList(itemsList);
            String type = (transactionTypeAlwd) ? PretupsI.TRANSFER_TYPE_O2C : PretupsI.ALL;
            String paymentMode = (transactionTypeAlwd && paymentModeAlwd) ? channelTransferVO.getPayInstrumentType() : PretupsI.ALL;
            ArrayList list = commissionProfileDAO.loadProductListWithTaxes(con, channelTransferVO.getCommProfileSetId(), channelTransferVO.getCommProfileVersion(), channelTransferVO.getChannelTransferitemsVOList(), type, paymentMode);
            if (!((ChannelTransferItemsVO) list.get(0)).isSlabDefine()) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MESSAGE_CHANNELTRANSFER_APPROVEDQUANTITY_NOTINSLAB);
            }
            if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentApprovalLevel)) {
                if (channelTransferVO.getRequestedQuantity() <= channelTransferVO.getFirstApproverLimit()) {
                    closeTransaction = true;
                }
            }
            if (!channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_PENDING)) {
                final String args[] = {transferId};
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_RECON_ALREADY_PROCESSED, args);
            }
            if(request.getReferenceNum().length() > 20){
                String arr[] = {"Reference number", "20"};
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EXTERNAL_TRANSACTION_LENGTH_EXCEED, arr);
            }
            if(request.getPaymentInstrumentCode() == null || request.getPaymentInstrumentCode().isEmpty()){
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PAYMENT_INSTRUMENT_TYPE_BLANK);
            }
            if(!request.getPaymentInstrumentCode().equals(channelTransferVO.getPayInstrumentType())){
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE);
            }
            if(request.getPaymentInstrumentNumber().length() >15){
                String arr[] = {"Payment Instrument number", "15"};
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EXTERNAL_TRANSACTION_LENGTH_EXCEED, arr);
            }
            if(request.getPaymentInstrumentDate().isEmpty() || request.getPaymentInstrumentDate() == null){
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ERROR_PAYMENT_INSTRUMENT_DATE_BLANK);
            }
            final String dateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
            if (dateFormat.length() != request.getPaymentInstrumentDate().length()) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ERROR_PAYMENT_INSTRUMENT_DATE_NOT_PROPER);
            }
            if(dateFormat.length() != request.getExternalTxnDate().length()){
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ERROR_EXT_DATE_NOT_PROPER);
            }
            if(request.getExternalTxnNum().length() > 10){
                String arr[] = {"Transaction number", "10"};
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EXTERNAL_TRANSACTION_LENGTH_EXCEED, arr);
            }
            if ((!BTSLUtil.isNullString(request.getExternalTxnNum()) && PretupsI.YES.equals(externalTxnMandatory)) || (!BTSLUtil.isNullString(request.getExternalTxnNum()) && PretupsI.YES.equals(externalTxnExist))) {
                if (externalTxnNumeric) {
                    try {
                        final long externalTxnIDLong = Long.parseLong(request.getExternalTxnNum());
                        if (externalTxnIDLong < 0) {
                            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MESSAGE_CHANNELTRANSFER_EXTERNALTXNNUMBERNOTNUMERIC);
                        }
                    } catch (Exception e) {
                        LOG.errorTrace(METHOD_NAME, e);
                        throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MESSAGE_CHANNELTRANSFER_EXTERNALTXNNUMBERNOTNUMERIC);
                    }
                }
                if (externalTxnUnique) {
                    final boolean isExternalTxnNotUnique = channelTransferDAO.isExtTxnExists(con, request.getExternalTxnNum(), request.getTransferID());
                    if (isExternalTxnNotUnique) {
                        throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MESSAGE_CHANNELTRANSFER_EXTERNALTXNNUMBERNOTUNIQUE);
                    }
                }
            }
            if (!BTSLUtil.isNullString(channelTransferVO.getFirstApprovalRemark()) && channelTransferVO.getFirstApprovalRemark().length() > 100) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CHANNELTRANSFER_TRANSFERDETAILAPPROVALLEVELONE_LABEL_APPROVERREMARK);
            }
            boolean sendOrderToApproval = false;
            String _serviceType = PretupsI.SERVICE_TYPE_CHNL_O2C_INTR;
            receiverMessageSendReq = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW, userVO.getNetworkID(), _serviceType)).booleanValue();
            ussdReceiverMessageSendReq = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.USSD_REC_MSG_SEND_ALLOW, userVO.getNetworkID(), _serviceType)).booleanValue();
            domainCode = channelTransferVO.getDomainCode();
            Status = channelTransferDAO.getStatusOfDomain(con, domainCode);
            if (PretupsI.NO.equals(Status)) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_APPROVAL_ERROR_INVALIDDOMAIN);
            }
            String failLevel = "One";
            String message = null;
            channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
            if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentApprovalLevel)) {
                channelTransferVO.setFirstApprovalRemark(request.getReconciliationRemarks());
                channelTransferVO.setFirstApprovedBy(userVO.getUserID());
                channelTransferVO.setFirstApprovedOn(date);
                if (channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE) || channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_NEW)) {
                    channelTransferVO.setPayInstrumentStatus(PretupsI.PAID);
                    message = PretupsErrorCodesI.O2C_RECONCILIATION_MSG_SUCCESS;
                } else {
                    channelTransferVO.setPayInstrumentStatus(PretupsI.REJECT_IAT_LIST);
                    message = PretupsErrorCodesI.CHANNELTRANSFER_FAIL_RECONCILIATION_MSG_SUCCESS;
                }
                // for o2c transfer quantity change
                channelTransferVO.setLevelOneApprovedQuantity(channelTransferVO.getLevelOneApprovedQuantity());
                channelTransferVO.setPayableAmount(PretupsBL.getSystemAmount(channelTransferVO.getPayableAmount()));
                channelTransferVO.setNetPayableAmount(PretupsBL.getSystemAmount(channelTransferVO.getNetPayableAmount()));
                channelTransferVO.setPayInstrumentAmt(PretupsBL.getSystemAmount(channelTransferVO.getPayInstrumentAmt()));
                channelTransferVO.setTransferMRP(PretupsBL.getSystemAmount(totMRP));
                channelTransferVO.setTotalTax1(PretupsBL.getSystemAmount(totTax1));
                channelTransferVO.setTotalTax2(PretupsBL.getSystemAmount(totTax2));
                channelTransferVO.setTotalTax3(PretupsBL.getSystemAmount(totTax3));
                if (PretupsI.YES.equals(externalTxnMandatory)) {
                    channelTransferVO.setExternalTxnDate(BTSLUtil.getDateFromDateString(request.getExternalTxnDate()));
                    channelTransferVO.setExternalTxnNum(request.getExternalTxnNum());
                }
                channelTransferVO.setPayInstrumentNum(request.getPaymentInstrumentNumber());
                if (channelTransferVO.getTransferMRP() <= channelTransferVO.getFirstApproverLimit()) {
                    sendOrderToApproval = true;
                }
            }
            if (PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equals(request.getPaymentInstrumentCode())) {
                sendOrderToApproval = true;
            }
            channelTransferVO.setReconciliationFlag(Boolean.parseBoolean(PretupsI.TRUE));
            if (PretupsI.TRANSFER_SUB_TYPE_VOUCHER.equals(channelTransferVO.getTransferSubType())) {
                ArrayList channleVoucherItemList = new ChannelTransferDAO().loadChannelVoucherItemsList(con, channelTransferVO.getTransferID(), channelTransferVO.getTransferDate());
                channelTransferVO.setChannelVoucherItemsVoList(channleVoucherItemList);
                if ((channelTransferDAO.updateChannelTransferApproval(con, channelTransferVO, sendOrderToApproval, true)) > 0) {
                    mcomCon.finalCommit();
                    final UserPhoneVO phoneVO = userDAO.loadUserAnyPhoneVO(con, channelTransferVO.getToUserCode());
                    String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
                    String language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(channelTransferVO.getStatus())) {
                        if (o2cEmailNotification) {
                            final String email = channelUserWebDAO.loadUserEmail(con, channelTransferVO.getToUserID());
                            channelTransferVO.setEmail(email);
                            sendEmailNotification(con, channelTransferVO, channelTransferDAO, "", "", PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_SUBJECT_INITIATE, userVO);
                            sendEmailNotification(con, channelTransferVO, channelTransferDAO, "APV1O2CTRF", "", PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_SUBJECT_APPROVER, userVO);
                        }
                        receiverMessageSendReq = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW, userVO.getNetworkID(), _serviceType)).booleanValue();
                        UserPhoneVO primaryPhoneVO = null;
                        if (secondaryNumberAllow && (messageToPrimaryRequired && !isprimaryNumber)) {
                            primaryPhoneVO = userDAO.loadUserAnyPhoneVO(con, channelTransferVO.getToPrimaryMSISDN());
                        }
                        if (receiverMessageSendReq) {
                            String messageKey = PretupsErrorCodesI.O2C_TRANSFER_VOUCHER_INITIATE;
                            if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
                                messageKey = PretupsErrorCodesI.O2C_TRANSFER_VOUCHER_OUTSIDE_SETTLEMENT;
                            }
                            if (primaryPhoneVO != null) {
                                final Object[] smsListArr = prepareSMSMessageListForVoucher(con, channelTransferVO);
                                final String[] array = {channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0])};
                                final BTSLMessages messages = new BTSLMessages(messageKey, array);
                                final PushMessage pushMessage = new PushMessage(primaryPhoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
                                pushMessage.push();
                            }
                            if (phoneVO != null) {
                                final Object[] smsListArr = prepareSMSMessageListForVoucher(con, channelTransferVO);
                                final String[] array = {channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0])};
                                final BTSLMessages messages = new BTSLMessages(messageKey, array);
                                final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
                                pushMessage.push();
                            }
                        }
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equals(channelTransferVO.getStatus())) {
                        final Object[] smsListArr = prepareSMSMessageListForVoucher(con, channelTransferVO);
                        final String[] array = {channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0])};
                        final BTSLMessages btslmessages = new BTSLMessages(PretupsErrorCodesI.O2C_TRANSFER_VOUCHER_CANCELLED, array);
                        final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), btslmessages, channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
                        pushMessage.push();
                        if (o2cEmailNotification) {
                            final String email = channelUserWebDAO.loadUserEmail(con, channelTransferVO.getToUserID());
                            channelTransferVO.setEmail(email);
                            sendEmailNotification(con, channelTransferVO, channelTransferDAO, "", failLevel, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_SUBJECT_FAILED, userVO);
                        }
                    }
                } else {
                    mcomCon.finalRollback();
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CHANNELTRANSFER_APPROVAL_MSG_UNSUCCESS);
                }

            }
            channelTransferVO.setModifiedBy(userVO.getUserID());
            channelTransferVO.setModifiedOn(date);
            // set payment instrument no and date (it may be changed at the time
            // of approval)

            channelTransferVO.setPayInstrumentType(request.getPaymentInstrumentCode());
            channelTransferVO.setPayInstrumentNum(request.getPaymentInstrumentNumber());
            channelTransferVO.setPayInstrumentDate(BTSLUtil.getDateFromDateString(request.getPaymentInstrumentDate()));

            // set the items list from form to VO
            channelTransferVO.setChannelTransferitemsVOList(itemsList);
            if (sendOrderToApproval) {
                if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, channelTransferVO.getNetworkCode()) && (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(channelTransferVO.getTransferSubType()) || PretupsI.TRANSFER_SUB_TYPE_VOUCHER.equals(channelTransferVO.getTransferSubType()))) {
                    ChannelTransferBL.increaseOptOTFCounts(con, channelTransferVO);
                }
            }
            ChannelTransferItemsVO channelTransferItemVO = (ChannelTransferItemsVO) channelTransferVO.getChannelTransferitemsVOList().get(0);
            if (sendOrderToApproval) {
                message = PretupsErrorCodesI.O2C_RECONCILIATION_MSG_SUCCESS;
                channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                channelTransferVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
                // Validate MRP && Successive Block for channel transaction
                long successiveReqBlockTime4ChnlTxn = ((Long) PreferenceCache.getSystemPreferenceValue(PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE_O2C)).longValue();
                try {
                    ChannelTransferBL.validateChannelLastTransferMrpSuccessiveBlockTimeout(con, channelTransferVO, date, successiveReqBlockTime4ChnlTxn);
                } catch (Exception e) {
                    String args[] = {channelTransferVO.getUserMsisdn(), PretupsBL.getDisplayAmount(channelTransferVO.getTransferMRP()), String.valueOf(successiveReqBlockTime4ChnlTxn / 60)};
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_APPROVAL_ERROR_MRPBLOCKTIMEOUT, args);
                }
                approveOrder(con, channelTransferVO, userVO.getUserID(), date);
            }
            if (PretupsI.TRANSFER_SUB_TYPE_VOUCHER.equals(channelTransferVO.getTransferSubType())) {
                mcomCon.partialCommit();
                final String args[] = {channelTransferVO.getTransferID()};
                response.setStatus(PretupsI.RESPONSE_SUCCESS);
                response.setMessageCode(PretupsErrorCodesI.O2C_RECONCILIATION_MSG_SUCCESS);
                String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_RECONCILIATION_MSG_SUCCESS, args);
                response.setMessage(resmsg);
                response1.setStatus(PretupsI.RESPONSE_SUCCESS);
                return response;
            }
            final String email = channelUserWebDAO.loadUserEmail(con, channelTransferVO.getToUserID());
            channelTransferVO.setEmail(email);

            int updateCount = 0;
            channelTransferVO.setExternalTxnNum(request.getExternalTxnNum());
            if (!BTSLUtil.isNullString(request.getExternalTxnDate())) {
                channelTransferVO.setExternalTxnDate(BTSLUtil.getDateFromDateString(request.getExternalTxnDate()));
            }
            // added for editable reference number
            channelTransferVO.setReferenceNum(BTSLUtil.NullToString(request.getReferenceNum()));
            // added for logger
            if (channelTransferVO.getTransferMRP() <= channelTransferVO.getFirstApproverLimit() && PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentApprovalLevel)) {
                OneLineTXNLog.log(channelTransferVO, null);
            }
            // end
            if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentApprovalLevel)) {
                channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                if (PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equals(request.getPaymentInstrumentCode())) {
                    updateCount = channelTransferDAO.updateO2CChannelTransferApproval(con, channelTransferVO, sendOrderToApproval, true ? true : false);
                } else {
                    updateCount = channelTransferDAO.updateChannelTransferApprovalLevelOne(con, channelTransferVO, sendOrderToApproval);
                }
            }
            if (updateCount > 0) {
                mcomCon.finalCommit();
                if (lmsAppl) {
                    try {
                        if (channelTransferVO.getStatus().equalsIgnoreCase(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
                            final Date currentdate = new Date();

                            final LoyaltyBL _loyaltyBL = new LoyaltyBL();
                            final LoyaltyVO loyaltyVO = new LoyaltyVO();
                            PromotionDetailsVO promotionDetailsVO = new PromotionDetailsVO();
                            final ArrayList arr = new ArrayList();
                            final LoyaltyDAO _loyaltyDAO = new LoyaltyDAO();
                            loyaltyVO.setServiceType(PretupsI.O2C_MODULE);
                            loyaltyVO.setModuleType(PretupsI.O2C_MODULE);
                            if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
                                loyaltyVO.setTransferamt(channelTransferVO.getSenderDrQty());
                            } else {
                                loyaltyVO.setTransferamt(channelTransferVO.getTransferMRP());
                            }
                            loyaltyVO.setCategory(channelTransferVO.getReceiverCategoryCode());
                            loyaltyVO.setUserid(channelTransferVO.getToUserID());
                            loyaltyVO.setNetworkCode(channelTransferVO.getNetworkCode());
                            loyaltyVO.setSenderMsisdn(channelTransferVO.getUserMsisdn());
                            loyaltyVO.setTxnId(channelTransferVO.getTransferID());
                            loyaltyVO.setCreatedOn(currentdate);
                            loyaltyVO.setProductCode(channelTransferItemVO.getProductCode());
                            arr.add(loyaltyVO.getUserid());
                            promotionDetailsVO = _loyaltyDAO.loadSetIdByUserId(con, arr);
                            loyaltyVO.setSetId(promotionDetailsVO.get_setId());
                            if (loyaltyVO.getSetId() == null) {
                                LOG.error(CLASS_NAME, "Exception durign LMS Module Profile Details are not found");
                            } else {
                                _loyaltyBL.distributeLoyaltyPoints(PretupsI.O2C_MODULE, channelTransferVO.getTransferID(), loyaltyVO);
                            }
                        }
                    } catch (Exception ex) {
                        LOG.error(CLASS_NAME, "Exception durign LMS Module " + ex.getMessage());
                        LOG.errorTrace(METHOD_NAME, ex);
                    }
                }
                if (channelTransferVO.getStatus().equalsIgnoreCase(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
                    if (!PretupsI.USER_STATUS_ACTIVE.equals(channelTransferVO.getChannelUserStatus())) {
                        int updatecount = 0;
                        final String str[] = txnReceiverUserStatusChang.split(PretupsI.COMMA); // "CH:Y,EX:Y".split(",");
                        String newStatus[] = null;
                        boolean changeStatusRequired = false;
                        int strLength = str.length;
                        for (int i = 0; i < strLength; i++) {
                            newStatus = str[i].split(PretupsI.COLON);
                            if (newStatus[0].equals(channelTransferVO.getChannelUserStatus()) && operatorUtili != null) {
                                changeStatusRequired = true;
                                updatecount = operatorUtili.changeUserStatusToActive(con, channelTransferVO.getToUserID(), channelTransferVO.getChannelUserStatus(), newStatus[1]);
                                break;
                            }
                        }
                        if (changeStatusRequired) {
                            if (updatecount > 0) {
                                mcomCon.finalCommit();
                            } else {
                                mcomCon.finalRollback();
                                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CHANNELTRANSFER_APPROVAL_MSG_UNSUCCESS);
                            }
                        }
                    }
                }
                if (o2cEmailNotification) {
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentApprovalLevel)) {
                        final String firstApprvLimit = PretupsBL.getDisplayAmount(channelTransferVO.getFirstApproverLimit());
                        if (channelTransferVO.isReconciliationFlag() && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(channelTransferVO.getTransferSubType()))
                            sendEmailNotification(con, channelTransferVO, channelTransferDAO, "", PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_CONTENT_TRANSFER_COMPLETED, userVO);
                        else {
                            sendEmailNotification(con, channelTransferVO, channelTransferDAO, "", PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_CONTENT_TRANSFER_COMPLETED, userVO);
                        }
                    }
                }

                if (sendOrderToApproval) {
                    if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, channelTransferVO.getNetworkCode())) {
                        if (channelTransferVO.isTargetAchieved() && PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(channelTransferVO.getStatus())) {
                            // Message handling for OTF
                            TargetBasedCommissionMessages tbcm = new TargetBasedCommissionMessages();
                            tbcm.loadBaseCommissionProfileDetailsForTargetMessages(con, channelTransferVO.getToUserID(), channelTransferVO.getMessageArgumentList());
                        }
                    }
                    ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
                    UserPhoneVO primaryPhoneVO = null;
                    if (secondaryNumberAllow && (messageToPrimaryRequired && !isprimaryNumber)) {
                        primaryPhoneVO = userDAO.loadUserAnyPhoneVO(con, channelTransferVO.getToPrimaryMSISDN());
                    }
                    final UserPhoneVO phoneVO = userDAO.loadUserAnyPhoneVO(con, channelTransferVO.getUserMsisdn());
                    String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
                    String language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
                    if (receiverMessageSendReq) {
                        if (primaryPhoneVO != null) {
                            country = primaryPhoneVO.getCountry();
                            language = primaryPhoneVO.getPhoneLanguage();
                            final Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiver(con, channelTransferVO, PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS2, PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS_BALSUBKEY);
                            final String[] array = {channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1])};
                            final BTSLMessages messages = new BTSLMessages(PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS1, array);
                            final PushMessage pushMessage = new PushMessage(primaryPhoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
                            pushMessage.push();
                        }
                        if (phoneVO != null) {
                            country = phoneVO.getCountry();
                            language = phoneVO.getPhoneLanguage();
                            final Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiver(con, channelTransferVO, PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS2, PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS_BALSUBKEY);
                            final String[] array = {channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1])};
                            final BTSLMessages messages = new BTSLMessages(PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS1, array);
                            final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
                            pushMessage.push();
                        } else {
                            final String arr[] = {channelTransferVO.getTransferID(), channelTransferVO.getToUserName()};
                            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CHANNELTRANSFER_PHONEINFO_NOTEXIST_MSG, arr);
                        }
                    }
                    if (ussdReceiverMessageSendReq) {
                        if (primaryPhoneVO != null) {
                            country = primaryPhoneVO.getCountry();
                            language = primaryPhoneVO.getPhoneLanguage();
                            Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiver(con, channelTransferVO, PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS2, PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS_BALSUBKEY);
                            String[] array = {channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1])};
                            BTSLMessages messages = new BTSLMessages(PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS1, array);
                            USSDPushMessage pushMessage = new USSDPushMessage(primaryPhoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
                            pushMessage.push();
                        }
                        if (phoneVO != null) {
                            country = phoneVO.getCountry();
                            language = phoneVO.getPhoneLanguage();
                            Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiver(con, channelTransferVO, PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS2, PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS_BALSUBKEY);
                            String[] array = {channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1])};
                            BTSLMessages messages = new BTSLMessages(PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS1, array);
                            USSDPushMessage pushMessage = new USSDPushMessage(phoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
                            pushMessage.push();
                        } else {
                            String arr[] = {channelTransferVO.getTransferID(), channelTransferVO.getToUserName()};
                            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CHANNELTRANSFER_PHONEINFO_NOTEXIST_MSG, arr);
                        }
                    }
                }
                // prepare the message
                final String args[] = {channelTransferVO.getTransferID()};
                response.setStatus(PretupsI.RESPONSE_SUCCESS);
                response.setMessageCode(PretupsErrorCodesI.O2C_RECONCILIATION_MSG_SUCCESS);
                String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_RECONCILIATION_MSG_SUCCESS, args);
                response.setMessage(resmsg);
                response1.setStatus(PretupsI.RESPONSE_SUCCESS);
            } else {
                // con.rollback();
                mcomCon.finalRollback();
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CHANNELTRANSFER_APPROVAL_MSG_UNSUCCESS);
            }
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("orderApproval", "Exiting:=");
            }
        }
        return response;
    }

    private void approveOrder(Connection con, ChannelTransferVO channelTransferVO, String userId, Date date) throws BTSLBaseException {
        final String METHOD_NAME = "approveOrder";
        final boolean debit = true;
        String Status;
        String domainCode;
        final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered channelTransferVO  " + channelTransferVO + " userId " + userId + " date " + date);
        }
        boolean userProductMultipleWallet = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
        // prepare networkStockList and debit the network stock
        domainCode = channelTransferVO.getDomainCode();
        Status = channelTransferDAO.getStatusOfDomain(con, domainCode);
        if (PretupsI.NO.equals(Status)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_APPROVAL_ERROR_INVALIDDOMAIN);
        }
        channelTransferVO.setTransferDate((Date) date.clone());
        if (!PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(channelTransferVO.getTransferSubType())) {
            ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(con, channelTransferVO, userId, (Date) date.clone(), debit);
            ChannelTransferBL.updateNetworkStockTransactionDetails(con, channelTransferVO, userId, (Date) date.clone());
            if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
                ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(con, channelTransferVO, userId, (Date) date.clone(), debit);
                ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(con, channelTransferVO, userId, (Date) date.clone());
            }
            final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
            userBalancesDAO.updateUserDailyBalances(con, (Date) date.clone(), constructBalanceVOFromTxnVO(channelTransferVO));
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            if (userProductMultipleWallet) {
                channelUserDAO.creditUserBalancesForMultipleWallet(con, channelTransferVO, true, null);
            } else {
                channelUserDAO.creditUserBalances(con, channelTransferVO, true, null);
            }
        }
        channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_O2C);
        ChannelTransferBL.updateOptToChannelUserInCounts(con, channelTransferVO, "", date);

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Exiting ");
        }
    }

    private UserBalancesVO constructBalanceVOFromTxnVO(ChannelTransferVO p_channelTransferVO) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("constructBalanceVOFromTxnVO", "Entered:NetworkStockTxnVO=>" + p_channelTransferVO);
        }
        final String METHOD_NAME = "constructBalanceVOFromTxnVO";
        final UserBalancesVO userBalancesVO = UserBalancesVO.getInstance();
        userBalancesVO.setUserID(p_channelTransferVO.getToUserID());
        userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
        userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
        userBalancesVO.setLastTransferOn(p_channelTransferVO.getModifiedOn());
        // Added to log user MSISDN on 13/02/2008
        userBalancesVO.setUserMSISDN(p_channelTransferVO.getUserMsisdn());
        if (LOG.isDebugEnabled()) {
            LOG.debug(CLASS_NAME, METHOD_NAME + userBalancesVO);
        }
        return userBalancesVO;
    }

    private Object[] prepareSMSMessageListForVoucher(Connection con, ChannelTransferVO channelTransferVO) {
        final String METHOD_NAME = "prepareSMSMessageListForVoucher";
        StringBuilder loggerValue = new StringBuilder();
        if (LOG.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered channelTransferVO =  : ");
            loggerValue.append(channelTransferVO);
            LOG.debug(METHOD_NAME, loggerValue);
        }
        final ArrayList txnSmsMessageList = new ArrayList();
        KeyArgumentVO keyArgumentVO = null;
        String argsArr[] = null;
        final ArrayList channelVoucherItemsVOList = channelTransferVO.getChannelVoucherItemsVoList();
        ChannelVoucherItemsVO channelVoucherItemsVO = null;
        for (int i = 0, k = channelVoucherItemsVOList.size(); i < k; i++) {
            channelVoucherItemsVO = (ChannelVoucherItemsVO) channelVoucherItemsVOList.get(i);
            keyArgumentVO = new KeyArgumentVO();
            argsArr = new String[2];
            argsArr[0] = String.valueOf(channelVoucherItemsVO.getTransferMrp());
            argsArr[1] = String.valueOf(channelVoucherItemsVO.getRequiredQuantity());
            keyArgumentVO.setKey(PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS2);
            keyArgumentVO.setArguments(argsArr);
            txnSmsMessageList.add(keyArgumentVO);
        }
        if (LOG.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Exited txnSmsMessageList.size() = ");
            loggerValue.append(txnSmsMessageList.size());
            LOG.debug(METHOD_NAME, loggerValue);
        }
        return (new Object[]{txnSmsMessageList});
    }

    private void sendEmailNotification(Connection con, ChannelTransferVO channelTransferVO, ChannelTransferDAO channelTransferDAO, String roleCode, String approvalLevel, String subject, UserVO userVO) {
        final String METHOD_NAME = "sendEmailNotification";
        final Locale locale = BTSLUtil.getSystemLocaleForEmail();

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered ");
        }

        try {
            final String from = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_FROM, null);
            String cc = PretupsI.EMPTY;
            String message1 = null;
            final String bcc = "";
            String emailSubject = "";
            boolean isHeaderAdded = false;
            channelTransferVO.setToUserMsisdn(channelTransferVO.getUserMsisdn());
            String notifyContent = "";
            if (!BTSLUtil.isNullString(roleCode) && "APV1O2CTRF".equals(roleCode)) {
                notifyContent = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_CONTENT, null);
            } else if (subject.equalsIgnoreCase(PretupsI.TRANSFER_INITIATE))
                notifyContent = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_SUBJECT_INITIATE, null);
            else if (subject.equalsIgnoreCase(PretupsI.TRANSFER_FAILED))
                notifyContent = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_SUBJECT_INITIATE, null);
            else {
                notifyContent = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_SUBJECT_INITIATE, null);
            }
            String appr1Quan = null;
            if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(channelTransferVO.getTransferSubType())) {
                if (channelTransferVO.getLevelOneApprovedQuantity() != null)
                    appr1Quan = PretupsBL.getDisplayAmount(Double.parseDouble(channelTransferVO.getLevelOneApprovedQuantity()));
            } else {
                appr1Quan = channelTransferVO.getLevelOneApprovedQuantity();
            }

            // For getting name and msisdn of initiator
            ArrayList arrayList = new ArrayList();
            ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
            arrayList = channelUserWebDAO.loadUserNameAndEmail(con, channelTransferVO.getCreatedBy());

            String message = null;

            message = notifyContent + "<br>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_CHANNELUSER_DETAILS, null) + "<br>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_TRANSFERID, null) + PretupsI.SPACE + channelTransferVO.getTransferID() + "<br>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_CHANNELUSER_NAME, null) + PretupsI.SPACE + channelTransferVO.getToUserName() + "<br>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_CHANNELUSER_MSISDN, null) + PretupsI.SPACE + channelTransferVO.getUserMsisdn() + "<br>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_TRANSFER_MRP, null) + PretupsI.SPACE + channelTransferVO.getTransferMRPAsString() + "<br>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_CONTENT_REQ_AMOUNT, null) + PretupsI.SPACE + PretupsBL.getDisplayAmount(channelTransferVO.getRequestedQuantity());

            /*
             * Message Content exclusively for transfer rejects
             * Showing only Fail Subject, TranferID, Reject User, Fail Remarks
             */

            if (subject.equalsIgnoreCase(PretupsI.TRANSFER_FAILED)) {
                message = message + "<br>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_TRANSFER_TYPE, null) + PretupsI.SPACE + channelTransferVO.getType() + "<br>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_INITIATOR_NAME, null) + PretupsI.SPACE + arrayList.get(0) + "<br>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_INITIATOR_MSISDN, null) + PretupsI.SPACE + arrayList.get(1) + "<br>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_CONTENT_REJECTED_BY, null) + userVO.getUserName() + "<br>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_CONTENT_REJECTION_REMARKS, null);
                if ("One".equals(approvalLevel)) message += channelTransferVO.getFirstApprovalRemark();
            } else {
                message = message + "<br>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_CONTENT_NET_PAYABLE_AMOUNT, null) + PretupsI.SPACE + PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount());
                if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
                    message = message + "<br>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_CONTENT_APPR_QUANTITY, null) + PretupsI.SPACE + appr1Quan;
                }
                message = message + "<br>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_TRANSFER_TYPE, null) + PretupsI.SPACE + channelTransferVO.getType() + "<br>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_INITIATOR_NAME, null) + PretupsI.SPACE + arrayList.get(0) + "<br>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_INITIATOR_MSISDN, null) + PretupsI.SPACE + arrayList.get(1);
            }

            if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(channelTransferVO.getTransferSubType())) {
                if (!(subject.equalsIgnoreCase(PretupsI.TRANSFER_FAILED))) {
                    String totalCommission = PretupsBL.getDisplayAmount(((ChannelTransferItemsVO) channelTransferVO.getChannelTransferitemsVOList().get(0)).getCommQuantity());
                    message = message + "<br>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_TOTAL_COMMISSION, null) + PretupsI.SPACE + totalCommission;
                    if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
                        message = message + "<br>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_OFFLINE_SETTLEMENT, null);
                    }
                }

                if (channelTransferVO.getChannelVoucherItemsVoList() != null) {
                    for (int i = 0; i < channelTransferVO.getChannelVoucherItemsVoList().size(); i++) {
                        if (((ChannelVoucherItemsVO) channelTransferVO.getChannelVoucherItemsVoList().get(i)).getToSerialNum() != null && ((ChannelVoucherItemsVO) channelTransferVO.getChannelVoucherItemsVoList().get(i)).getFromSerialNum() != null) {
                            if (!isHeaderAdded) {
                                isHeaderAdded = true;
                                message1 = "<br>" + "<table><tr>" + " <td style='width: 5%;'>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_SERIALNUMBER, null) + "</td>" + " <td style='width: 10%;'>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_DENOMINATION, null) + "</td>" + " <td style='width: 10%;'>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_QUANTITY, null) + "</td>" + " <td style='width: 25%;'>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_FROMSERIALNO, null) + "</td>" + " <td style='width: 25%;'>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_TOSERIALNO, null) + "</td>" + " <td style='width: 12%;'>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_VOUCHERTYPE, null) + "</td>" + "</tr>";
                            }
                            message1 = message1 + "<tr><td style='width: 5%;'>" + (i + 1) + "</td>" + "<td style='width: 10%;'>" + ((ChannelVoucherItemsVO) channelTransferVO.getChannelVoucherItemsVoList().get(i)).getTransferMrp() + "</td>" + "<td style='width: 10%;'>" + ((ChannelVoucherItemsVO) channelTransferVO.getChannelVoucherItemsVoList().get(i)).getRequiredQuantity() + "</td>" + "<td style='width: 25%;'>" + ((ChannelVoucherItemsVO) channelTransferVO.getChannelVoucherItemsVoList().get(i)).getFromSerialNum() + "</td>" + "<td style='width: 25%;'>" + ((ChannelVoucherItemsVO) channelTransferVO.getChannelVoucherItemsVoList().get(i)).getToSerialNum() + "</td>" + "<td style='width: 12%;'>" + new VomsProductDAO().getNameFromVoucherType(con, ((ChannelVoucherItemsVO) channelTransferVO.getChannelVoucherItemsVoList().get(i)).getVoucherType()) + "</td>" + "</tr>";
                        } else {
                            if (!isHeaderAdded) {
                                isHeaderAdded = true;
                                message1 = "<br>" + "<table><tr>" + "   <td> S.No. </td>" + "   <td>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_DENOMINATION, null) + "</td>" + " <td>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_QUANTITY, null) + "</td>" + "<td>" + RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_EMAIL_NOTIFICATION_VOUCHERTYPE, null) + "</td>" + "</tr>";
                            }
                            message1 = message1 + "<tr><td>" + (i + 1) + "</td>" + "<td>" + ((ChannelVoucherItemsVO) channelTransferVO.getChannelVoucherItemsVoList().get(i)).getTransferMrp() + "</td>" + "<td>" + ((ChannelVoucherItemsVO) channelTransferVO.getChannelVoucherItemsVoList().get(i)).getRequiredQuantity() + "</td>" + "<td>" + new VomsProductDAO().getNameFromVoucherType(con, ((ChannelVoucherItemsVO) channelTransferVO.getChannelVoucherItemsVoList().get(i)).getVoucherType()) + "</td>" + "</tr>";
                        }
                    }
                    message = message + message1 + "</table>";
                }
            }

            final boolean isAttachment = false;
            final String pathofFile = "";
            final String fileNameTobeDisplayed = "";
            String to = "";
            if (!BTSLUtil.isNullString(roleCode)) {
                emailSubject = RestAPIStringParser.getMessage(locale, subject, null);
                to = channelTransferDAO.getEmailIdOfApprover(con, roleCode, channelTransferVO.getToUserID());
            } else {
                emailSubject = RestAPIStringParser.getMessage(locale, subject, null);
                to = channelTransferVO.getEmail();
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, message);
            }
            // Send email
            EMailSender.sendMail(to, from, bcc, cc, emailSubject, message, isAttachment, pathofFile, fileNameTobeDisplayed);
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.error(METHOD_NAME, " Email sending failed" + e.getMessage());
            }
            LOG.errorTrace(METHOD_NAME, e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Exiting ....");
        }
    }

    private void validateUserInformation(Connection p_con, String domainTypeCode, String userID, O2CReconciliationTxnDetailVO o2CReconciliationTxnDetailVO) throws Exception {

        final String METHOD_NAME = "validateUserInformation";

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered userID = " + userID);
        }

        int receiverStatusAllowed = 0;

        try {

            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();

            final Date curDate = new Date();
            final ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(p_con, userID, false, curDate, false);
            if (channelUserVO == null) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USER_DETAILS_NOT_FOUND);
            } else {
                final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(), channelUserVO.getUserType(), PretupsI.REQUEST_SOURCE_TYPE_WEB);
                if (userStatusVO != null) {
                    final String userStatusAllowed = userStatusVO.getUserReceiverAllowed();
                    final String status[] = userStatusAllowed.split(",");
                    for (int i = 0; i < status.length; i++) {
                        if (status[i].equals(channelUserVO.getStatus())) {
                            receiverStatusAllowed = 1;
                        }
                    }
                } else {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USER_NOT_ACTIVE);
                }
            }

            if (receiverStatusAllowed == 0) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USER_NOT_ACTIVE);

            } else if (channelUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_USER_COMM_PROFILE);

            } else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                final Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
                String args[] = null;
                args = new String[]{channelUserVO.getCommissionProfileLang2Msg()};
                final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                    args = new String[]{channelUserVO.getCommissionProfileLang1Msg()};
                }
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.COMM_PROFILE_NOT_ACTIVE, args);
            } else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.TRF_PROFILE_NOT_ACTIVE);

            }

            if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USER_SUSPENDED);

            }
            domainTypeCode = channelUserVO.getDomainTypeCode();
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting" + METHOD_NAME);
            }
        }
    }

    private ProductDetailVO populateProductDetail(ChannelTransferItemsVO channelTransferItemsVO) {
        ProductDetailVO productDetailVO = new ProductDetailVO();
        productDetailVO.setProductShortCode(channelTransferItemsVO.getProductShortCode());
        productDetailVO.setProductName(channelTransferItemsVO.getProductName());
        productDetailVO.setDenomination(channelTransferItemsVO.getTransferMultipleOf() / (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR));
        productDetailVO.setNetworkStock(channelTransferItemsVO.getNetworkStockAsString());
        productDetailVO.setRequestedQuantity(channelTransferItemsVO.getInitialRequestedQuantityStr());
        productDetailVO.setTax1(channelTransferItemsVO.getTax1ValueAsString());
        productDetailVO.setTax1Type(channelTransferItemsVO.getTax1Type());
        productDetailVO.setTax1Rate(channelTransferItemsVO.getTax1RateAsString());
        productDetailVO.setTax2(channelTransferItemsVO.getTax2ValueAsString());
        productDetailVO.setTax2Type(channelTransferItemsVO.getTax2Type());
        productDetailVO.setTax2Rate(channelTransferItemsVO.getTax2RateAsString());
        productDetailVO.setCommission(channelTransferItemsVO.getCommAsString());
        productDetailVO.setCommissionType(channelTransferItemsVO.getCommType());
        productDetailVO.setCommissionRate(channelTransferItemsVO.getCommRateAsString());
        productDetailVO.setCbc(channelTransferItemsVO.getOthCommAsString());
        productDetailVO.setCbcType(channelTransferItemsVO.getOthCommType());
        productDetailVO.setCbcRate(channelTransferItemsVO.getOthCommRateAsString());
        productDetailVO.setTds(channelTransferItemsVO.getTax3ValueAsString());
        productDetailVO.setTdsType(channelTransferItemsVO.getTax3Type());
        productDetailVO.setTdsRate(channelTransferItemsVO.getTax3RateAsString());
        productDetailVO.setPayableAmount(channelTransferItemsVO.getPayableAmountAsString());
        productDetailVO.setNetPayableAmount(channelTransferItemsVO.getNetPayableAmountAsString());
        productDetailVO.setNetCommissionQuantity(channelTransferItemsVO.getCommQuantityAsString());
        productDetailVO.setReceiverCreditQuantity(channelTransferItemsVO.getReceiverCreditQtyAsString());
        return productDetailVO;
    }

    private VoucherDetailVO populateVoucherDetail(VomsBatchVO voucher) {
        VoucherDetailVO voucherDetailVO = new VoucherDetailVO();
        voucherDetailVO.setSNo(String.valueOf(voucher.getSeq_id()));
        voucherDetailVO.setDenomination(voucher.getDenomination());
        voucherDetailVO.setQuantity(voucher.getQuantity());
        return voucherDetailVO;
    }

    private void generateResponseTransactionDetail(O2CReconciliationTxnDetailVO response, List<ProductDetailVO> productList, ChannelTransferVO txnDetail) {
        response.setTransferId(txnDetail.getTransferID());
        response.setNetworkName(txnDetail.getNetworkName());
        response.setDomain(txnDetail.getDomainCodeDesc());
        response.setCategory(txnDetail.getReceiverCategoryDesc());
        response.setName(txnDetail.getToUserName());
        response.setErpCode(txnDetail.getErpNum());
        response.setGrade(txnDetail.getReceiverGradeCodeDesc());
        response.setProductType(txnDetail.getProductType());
        response.setTransferDate(txnDetail.getTransferDateAsString());
        response.setTransactionNumber(txnDetail.getExternalTxnNum());
        response.setTransactionDate(txnDetail.getExternalTxnDateAsString());
        response.setCommissionProfile(txnDetail.getCommProfileName());
        response.setTransferProfile(txnDetail.getReceiverTxnProfileName());
        response.setReferenceNumber(txnDetail.getReferenceNum());
        response.setProductDetails(productList);
        response.setInitiatorRemarks(txnDetail.getChannelRemarks());
        response.setPaymentMode(txnDetail.getPayInstrumentType());
        response.setInstrumentNumber(txnDetail.getPayInstrumentNum());
        response.setPaymentDate(txnDetail.getPayInstrumentDateAsString());
        response.setReconciliationRemark(txnDetail.getFirstApprovalRemark());
    }

    private void o2cReconFailValidations(O2CReconciliationFailRequestVO request) throws BTSLBaseException {
        final String METHOD_NAME = "o2cReconFailValidations";

        try {
            Objects.requireNonNull(request.getTransferID());
            if (request.getTransferID().isEmpty())
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_RECON_TRANSFER_ID_INVALID);

        } catch (NullPointerException e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.O2C_RECON_TRANSFER_ID_INVALID);
        }

        String dateFormat = Constants.getProperty(PretupsI.VOUCHER_EXPIRY_DATE_FORMAT);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        try {
                LocalDate.parse(request.getExternalTxnDate(), formatter);
                LocalDate.parse(request.getPaymentInstrumentDate(), formatter);
        } catch (DateTimeParseException dte) {
            final String args[] = {dateFormat};
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EXT_TRF_RULE_TYPE_INVALID_DATE, args);
        }
        if(request.getPaymentInstrumentDate() == null || request.getPaymentInstrumentDate().isEmpty()){
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ERROR_PAYMENT_INSTRUMENT_DATE_BLANK);
        }
        if(request.getReferenceNum().length() > 20){
            String arr[] = {"Reference number", "20"};
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EXTERNAL_TRANSACTION_LENGTH_EXCEED, arr);
        }
        if(request.getPaymentInstrumentCode() == null || request.getPaymentInstrumentCode().isEmpty()){
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PAYMENT_INSTRUMENT_TYPE_BLANK);
        }
        if(!request.getPaymentInstrumentCode().equals(PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE)){
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE);
        }
        if(request.getPaymentInstrumentNumber().length() >15){
            String arr[] = {"Payment Instrument number", "15"};
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EXTERNAL_TRANSACTION_LENGTH_EXCEED, arr);
        }
        if(request.getExternalTxnNum().length() > 10){
            String arr[] = {"Transaction number", "10"};
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EXTERNAL_TRANSACTION_LENGTH_EXCEED, arr);
        }
    }
}
