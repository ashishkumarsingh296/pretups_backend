package com.restapi.networkadmin.redemption;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.*;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.*;
import com.btsl.util.BTSLUtil;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

@Service("RedemptionServiceI")
public class RedemptionServiceImpl implements RedemptionServiceI {
    public static final Log log = LogFactory.getLog(RedemptionServiceImpl.class.getName());
    public static final String classname = "RedemptionServiceImpl";

    @Override
    public RedemptionResponseVO initateRedemption(MultiValueMap<String, String> headers, Connection con, MComConnectionI mcomCon, Locale locale, RedemptionRequestVO redemptionRequestVO, ChannelUserVO userVO,String gateway) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "initateRedemption";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        RedemptionResponseVO response = new RedemptionResponseVO();
        boolean processed = false;

        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            processed = processFoCInitiate(redemptionRequestVO, con, mcomCon, response, userVO,gateway);
            if (processed) {
                response.setStatus((HttpStatus.SC_OK));
                String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TXN_SUCCES, null);
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.TXN_SUCCES);
            }

        } finally {
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }

    public boolean processFoCInitiate(RedemptionRequestVO redemptionRequestVO, Connection con, MComConnectionI mcomCon, RedemptionResponseVO redemptionResponseVO, ChannelUserVO UserVO,String gateway) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "processFoCInitiate";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        ChannelUserVO channelUserVO = null;
        try {
            Date date = new Date();
            channelUserVO = new ChannelUserVO();
            channelUserVO.setUserCode(redemptionRequestVO.getMsisdn2());
            channelUserVO.setMsisdn(redemptionRequestVO.getMsisdn2());
            channelUserVO = getUserDetails(con, channelUserVO, UserVO);
            loadUserProducts(con, UserVO, channelUserVO);
            ChannelTransferItemsVO channelTransferItemsVO = null;
            final ArrayList productList = channelUserVO.getProductsList();
            final ArrayList itemsList = new ArrayList();
            final ArrayList fromArrayList = redemptionRequestVO.getFocProducts();
            RedemptionProductVO productVO = null;
            for (int i = 0; i < productList.size(); i++) {
                channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(i);
                for (int j = 0; j < fromArrayList.size(); j++) {
                    productVO = (RedemptionProductVO) fromArrayList.get(j);
                    if (productVO.getProductCode().equals(channelTransferItemsVO.getProductCode())) {
                        channelTransferItemsVO.setRequestedQuantity(productVO.getAppQuantity());
                        itemsList.add(channelTransferItemsVO);
                        break;
                    }
                }
            }
            if (itemsList == null || itemsList.size() == 0) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.PRODUCTS_NOT_FOUND, 0, null);
            }
            final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
            channelTransferVO.setChannelTransferitemsVOList(itemsList);
            channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
            channelTransferVO.setDualCommissionType(channelUserVO.getDualCommissionType());
            channelTransferVO.setOtfFlag(false);

            ChannelTransferBL.loadAndCalculateTaxOnProducts(con, channelUserVO.getCommissionProfileSetID(), channelUserVO.getCommissionProfileSetVersion(), channelTransferVO, false,
                    "TransferDetails", PretupsI.TRANSFER_TYPE_FOC);
            long totTax1 = 0, totTax2 = 0, totRequestedQty = 0, totTransferedAmt = 0, totalMRP = 0;

            ChannelTransferItemsVO transferItemsVO = null;
            for (int i = 0, k = itemsList.size(); i < k; i++) {
                transferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
                channelTransferVO.setProductType(((ChannelTransferItemsVO) itemsList.get(i)).getProductType());
                totTax1 += transferItemsVO.getTax1Value();
                totTax2 += transferItemsVO.getTax2Value();
                if (transferItemsVO.getRequestedQuantity() != null && BTSLUtil.isDecimalValue(transferItemsVO.getRequestedQuantity())) {
                    totRequestedQty += PretupsBL.getSystemAmount(transferItemsVO.getRequestedQuantity());
                    totTransferedAmt += (Double.parseDouble(transferItemsVO.getRequestedQuantity()) * transferItemsVO.getUnitValue());
                }

                totalMRP += transferItemsVO.getProductTotalMRP();
            }
            channelTransferVO.setTotalTax1(PretupsBL.getSystemAmount(totTax1));
            channelTransferVO.setTotalTax2(PretupsBL.getSystemAmount(totTax2));
            channelTransferVO.setRequestedQuantity(totRequestedQty);
            channelTransferVO.setTotalTax3(0);
            channelTransferVO.setPayableAmount(0);
            channelTransferVO.setNetPayableAmount(0);
            channelTransferVO.setPayInstrumentAmt(0);
            channelTransferVO.setTransferMRP(totTransferedAmt);
            channelTransferVO.setNetworkCode(UserVO.getNetworkID());
            channelTransferVO.setCreatedBy(UserVO.getUserID());
            channelTransferVO.setModifiedBy(UserVO.getUserID());
            channelTransferVO.setTransferInitatedBy(UserVO.getUserID());
            if (!BTSLUtil.isNullString(UserVO.getActiveUserID()))
                channelTransferVO.setActiveUserId(UserVO.getActiveUserID());
            else
                channelTransferVO.setActiveUserId(UserVO.getUserID());
            channelTransferVO.setDefaultLang("");
            channelTransferVO.setSecondLang("");
            channelTransferVO.setReferenceNum(redemptionRequestVO.getRefnumber());
            channelTransferVO.setChannelRemarks(redemptionRequestVO.getRemarks());
            channelTransferVO.setChannelTransferitemsVOList(itemsList);
            constructChannelTransferVO(channelTransferVO, channelUserVO, UserVO ,gateway);

            final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
            if (SystemPreferences.MULTIPLE_WALLET_APPLY) {
                channelTransferVO.setWalletType(PretupsI.FOC_WALLET_TYPE);
            } else {
                channelTransferVO.setWalletType(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
            }
            ChannelTransferBL.genrateTransferID(channelTransferVO);
            final int count = channelTransferDAO.addChannelTransfer(con, channelTransferVO);
            if (count > 0) {
                mcomCon.finalCommit();
                redemptionResponseVO.setTransactionId(channelTransferVO.getTransferID());
                channelTransferVO.setFirstApprovalRemark(redemptionRequestVO.getRemarks());
                channelTransferVO.setFirstApprovedBy(channelUserVO.getUserID());
                channelTransferVO.setFirstApprovedOn(date);
                channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                if (SystemPreferences.MULTIPLE_WALLET_APPLY) {
                    channelTransferVO.setWalletType(PretupsI.FOC_WALLET_TYPE);
                } else {
                    channelTransferVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
                }
                channelTransferVO.setLevelOneApprovedQuantity(channelTransferVO.getRequestedQuantityAsString());
                channelTransferVO.setModifiedBy(channelUserVO.getUserID());
                channelTransferVO.setModifiedOn(date);
                approveOrder(con, channelTransferVO, channelUserVO.getUserID(), date);
                int updateCount1 = 0;
                updateCount1 = channelTransferDAO.updateChannelTransferApprovalLevelOne(con, channelTransferVO,
                        true);
                if (updateCount1 > 0) {
                    mcomCon.finalCommit();
                }
                return true;
            } else {
                mcomCon.finalRollback();
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.TRANSACTION_FAIL, 0, null);

            }


        } finally {
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
    }

    private ChannelUserVO getUserDetails(Connection con, ChannelUserVO reciverVO, ChannelUserVO UserVO) throws BTSLBaseException, Exception {

        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        final Date curDate = new Date();
        ChannelUserVO channelUserVO = null;
        UserPhoneVO phoneVO = null;
        final UserDAO userDAO = new UserDAO();
        String methodName = "getUserDetails";
        try {
            GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
            ArrayList geographyList = _geographyDAO.loadUserGeographyList(con, UserVO.getUserID(), UserVO.getNetworkID());
            UserVO.setGeographicalAreaList(geographyList);
            DomainDAO domainDAO = new DomainDAO();
            UserVO.setDomainList(domainDAO.loadDomainListByUserId(con, UserVO.getUserID()));
            phoneVO = new UserPhoneVO();
            if (SystemPreferences.SECONDARY_NUMBER_ALLOWED) {
                phoneVO = userDAO.loadUserAnyPhoneVO(con, reciverVO.getMsisdn());
                if (phoneVO == null) {
                    throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.CHANNEL_USR_NOT_FOUND, 0, null);
                }
                channelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, phoneVO.getUserId(), false, curDate, false);
                if (channelUserVO == null) {
                    throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.CHANNEL_USR_NOT_FOUND, 0, null);
                }
                if (!("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
                    channelUserVO.setPrimaryMsisdn(channelUserVO.getMsisdn());
                    channelUserVO.setMsisdn(phoneVO.getMsisdn());
                } else {
                    channelUserVO.setPrimaryMsisdn(channelUserVO.getMsisdn());
                }
            } else {
                channelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, reciverVO.getMsisdn(), true, curDate, false);
            }

            boolean receiverStatusAllowed = false;
            if (channelUserVO == null) {
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.CHANNEL_USR_NOT_FOUND, 0, null);
            } else {
                final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(), channelUserVO
                        .getUserType(), PretupsI.REQUEST_SOURCE_TYPE_REST);
                if (userStatusVO != null) {
                    final String userStatusAllowed = userStatusVO.getUserReceiverAllowed();
                    final String status[] = userStatusAllowed.split(",");
                    for (int i = 0; i < status.length; i++) {
                        if (status[i].equals(channelUserVO.getStatus())) {
                            receiverStatusAllowed = true;
                        }
                    }
                } else {
                    throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.FOC_USER_STATUS_NOT_CONGIGURED, 0, null);
                }
            }
            if (!receiverStatusAllowed) {
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_ALREADY_SUSPENDED, 0, null);
            } else if (channelUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.COMM1, 0, null);
            }

            if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.COMM2, 0, null);
            } else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.ERROR_TRANSFER_PROFILE_SUSPENDED, 0, null);

            }
            if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.FOC_USER_IN_SUS, 0, null);
            }
            final ArrayList domainList = UserVO.getDomainList();
            if (domainList != null && domainList.size() > 0) {
                ListValueVO listValueVO = null;
                boolean domainfound = false;

                for (int i = 0, j = domainList.size(); i < j; i++) {
                    listValueVO = (ListValueVO) domainList.get(i);
                    if (channelUserVO.getDomainID().equals(listValueVO.getValue())) {
                        domainfound = true;
                        break;
                    }
                }
                if (!domainfound) {
                    throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_SEQUENCE_NOT_BELOW, 0, null);
                }
            }

            final GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
            if (!geographicalDomainDAO.isGeoDomainExistInHierarchy(con, channelUserVO.getGeographicalCode(), UserVO.getUserID())) {
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.TO_USER_GEOGRAPHY_INVALID, 0, null);

            }
        } catch (Exception e) {
            log.error(methodName, "Exception:e= " + e);
            log.errorTrace(methodName, e);
            throw new BTSLBaseException(classname, methodName,
                    e.getMessage());
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:=" + methodName);
            }
        }
        return channelUserVO;
    }

    private void loadUserProducts(Connection p_con, ChannelUserVO UserVO, ChannelUserVO channelUserVO) throws BTSLBaseException {
        final String methodName = "loadUserProducts";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        try {
            final Date curDate = new Date();
            final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
            final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(p_con, channelUserVO.getNetworkID(), channelUserVO.getDomainID(),
                    PretupsI.CATEGORY_TYPE_OPT, channelUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);

            if (channelTransferRuleVO == null) {
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.USER_TRANSFER_RULE_NOT_EXIST, 0, null);
            } else if (PretupsI.NO.equals(channelTransferRuleVO.getFocAllowed())) {
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.POD_1, 0, null);
            } else if (channelTransferRuleVO.getProductVOList() == null || channelTransferRuleVO.getProductVOList().isEmpty()) {
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.POD_2, 0, null);
            }
            ArrayList list = ChannelTransferBL.loadO2CXfrProductList(p_con, UserVO.getProductCode(), channelUserVO.getNetworkID(), channelUserVO.getCommissionProfileSetID(),
                    curDate, null);

            if (list.isEmpty()) {
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.POD_3, 0, null);
            }
            list = filterProductWithTransferRule(list, channelTransferRuleVO.getProductVOList());
            if (list.isEmpty()) {
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.POD_4, 0, null);
            }
            channelUserVO.setTransferCategory(PretupsI.MOBILE_CATEGORY);
            channelUserVO.setProductsList(list);
            for (final Iterator<ChannelTransferItemsVO> iterator = list.iterator(); iterator.hasNext(); ) {
                final ChannelTransferItemsVO transferItemVO = iterator.next();
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                    transferItemVO.setUserWallet(null);
                }
            }
        } catch (Exception e) {
            log.error(methodName, "Exception:e= " + e);
            log.errorTrace(methodName, e);
            throw new BTSLBaseException(classname, methodName,
                    e.getMessage());
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:=" + methodName);
            }
        }
    }

    private ArrayList filterProductWithTransferRule(ArrayList p_productList, ArrayList p_productListWithXfrRule) {
        final String METHOD_NAME = "filterProductWithTransferRule";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered p_productList: " + p_productList.size() + " p_productListWithXfrRule: " + p_productListWithXfrRule.size());
        }
        ChannelTransferItemsVO channelTransferItemsVO = null;
        ListValueVO listValueVO = null;
        final ArrayList tempList = new ArrayList();
        for (int m = 0, n = p_productList.size(); m < n; m++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) p_productList.get(m);
            for (int i = 0, k = p_productListWithXfrRule.size(); i < k; i++) {
                listValueVO = (ListValueVO) p_productListWithXfrRule.get(i);
                if (channelTransferItemsVO.getProductCode().equals(listValueVO.getValue())) {
                    tempList.add(channelTransferItemsVO);
                    break;
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Exiting tempList: " + tempList.size());
        }

        return tempList;
    }

    private void constructChannelTransferVO(ChannelTransferVO p_channelTransferVO, ChannelUserVO channelUserVO, ChannelUserVO UserVO,String gateway) throws Exception {
        final String methodName = "constructChannelTransferVO";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_channelTransferVO = " + p_channelTransferVO);
        }
        final Date currDate = new Date();
        p_channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setToUserID(channelUserVO.getUserID());
        p_channelTransferVO.setToUserName(channelUserVO.getUserName());
        p_channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
        p_channelTransferVO.setDomainCode(channelUserVO.getDomainID());
        p_channelTransferVO.setNetworkCodeFor(UserVO.getNetworkID());
        p_channelTransferVO.setReceiverCategoryCode(channelUserVO.getCategoryCode());
        p_channelTransferVO.setReceiverGradeCode(channelUserVO.getUserGrade());
        p_channelTransferVO.setCommProfileSetId(channelUserVO.getCommissionProfileSetID());
        p_channelTransferVO.setCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
        p_channelTransferVO.setTransferDate(currDate);
        p_channelTransferVO.setCommProfileVersion(channelUserVO.getCommissionProfileSetVersion());
        p_channelTransferVO.setDualCommissionType(channelUserVO.getDualCommissionType());
        p_channelTransferVO.setCreatedOn(currDate);
        p_channelTransferVO.setModifiedOn(currDate);
        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
        p_channelTransferVO.setReceiverTxnProfile(channelUserVO.getTransferProfileID());
        p_channelTransferVO.setSource(gateway);
        p_channelTransferVO.setRequestGatewayCode(gateway);
        p_channelTransferVO.setRequestGatewayType(gateway);
        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
        p_channelTransferVO.setProductType(p_channelTransferVO.getProductType());
        p_channelTransferVO.setTransferCategory(channelUserVO.getTransferCategory());
        p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
        p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
        p_channelTransferVO.setControlTransfer(PretupsI.YES);
        p_channelTransferVO.setReceiverGgraphicalDomainCode(p_channelTransferVO.getGraphicalDomainCode());
        p_channelTransferVO.setReceiverDomainCode(channelUserVO.getDomainID());
        p_channelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(channelUserVO.getMsisdn()));
        p_channelTransferVO.setUserWalletCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting p_channelTransferVO= " + p_channelTransferVO);
        }
    }

    private void approveOrder(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_userId, Date p_date)
            throws BTSLBaseException {
        final boolean debit = true;
        ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, p_channelTransferVO, p_userId, p_date,
                debit);
        ChannelTransferBL.updateNetworkStockTransactionDetails(p_con, p_channelTransferVO, p_userId, p_date);
        final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
        userBalancesDAO.updateUserDailyBalances(p_con, p_date, constructBalanceVOFromTxnVO(p_channelTransferVO));
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        p_channelTransferVO.setTransferDate(p_date);
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
            channelUserDAO.creditUserBalancesForMultipleWallet(p_con, p_channelTransferVO, true, null);
        } else {
            channelUserDAO.creditUserBalances(p_con, p_channelTransferVO, true, null);
        }
        p_channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_FOC);
        ChannelTransferBL.updateOptToChannelUserInCounts(p_con, p_channelTransferVO, null, p_date);
    }

    private UserBalancesVO constructBalanceVOFromTxnVO(ChannelTransferVO p_channelTransferVO) {
        final String methodName = "constructBalanceVOFromTxnVO";
        final UserBalancesVO userBalancesVO = new UserBalancesVO();
        userBalancesVO.setUserID(p_channelTransferVO.getToUserID());
        userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
        userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
        userBalancesVO.setLastTransferOn(p_channelTransferVO.getModifiedOn());
        userBalancesVO.setProductType(p_channelTransferVO.getProductType());
        userBalancesVO.setNetworkFor(p_channelTransferVO.getNetworkCodeFor());
        userBalancesVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
            userBalancesVO.setWalletCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN)));
            p_channelTransferVO.setUserWalletCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN)));
        } else {
            userBalancesVO.setWalletCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
            p_channelTransferVO.setUserWalletCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
        }
        return userBalancesVO;
    }

}