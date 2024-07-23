package com.restapi.o2c.service;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class ApprovalVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link ApprovalVO#setAddress(String)}
     *   <li>{@link ApprovalVO#setApprove1Remark(String)}
     *   <li>{@link ApprovalVO#setApprove2Remark(String)}
     *   <li>{@link ApprovalVO#setApprove3Remark(String)}
     *   <li>{@link ApprovalVO#setCategoryCode(String)}
     *   <li>{@link ApprovalVO#setCategoryCodeDesc(String)}
     *   <li>{@link ApprovalVO#setCategoryList(ArrayList)}
     *   <li>{@link ApprovalVO#setChannelTransferVO(ChannelTransferVO)}
     *   <li>{@link ApprovalVO#setChannelUserID(String)}
     *   <li>{@link ApprovalVO#setChannelUserName(String)}
     *   <li>{@link ApprovalVO#setChannelUserStatus(String)}
     *   <li>{@link ApprovalVO#setCurrentDate(String)}
     *   <li>{@link ApprovalVO#setDefaultLang(String)}
     *   <li>{@link ApprovalVO#setDomainCode(String)}
     *   <li>{@link ApprovalVO#setDomainCodeDesc(String)}
     *   <li>{@link ApprovalVO#setDomainList(ArrayList)}
     *   <li>{@link ApprovalVO#setDomainTypeCode(String)}
     *   <li>{@link ApprovalVO#setErpCode(String)}
     *   <li>{@link ApprovalVO#setExternalTxnDate(String)}
     *   <li>{@link ApprovalVO#setExternalTxnExist(String)}
     *   <li>{@link ApprovalVO#setExternalTxnMandatory(String)}
     *   <li>{@link ApprovalVO#setExternalTxnNum(String)}
     *   <li>{@link ApprovalVO#setFocOrderApprovalLevel(int)}
     *   <li>{@link ApprovalVO#setGeoDomainCodeDescForUser(String)}
     *   <li>{@link ApprovalVO#setGeographicalDomainCode(String)}
     *   <li>{@link ApprovalVO#setGeographicalDomainCodeDesc(String)}
     *   <li>{@link ApprovalVO#setGeographicalDomainList(ArrayList)}
     *   <li>{@link ApprovalVO#setMultiWallet(boolean)}
     *   <li>{@link ApprovalVO#setNetCommissionQuantity(String)}
     *   <li>{@link ApprovalVO#setNetworkCode(String)}
     *   <li>{@link ApprovalVO#setNetworkCodeDesc(String)}
     *   <li>{@link ApprovalVO#setProductListWithTaxes(ArrayList)}
     *   <li>{@link ApprovalVO#setProductQuantity(String)}
     *   <li>{@link ApprovalVO#setProductTypeCode(String)}
     *   <li>{@link ApprovalVO#setProductTypeCodeDesc(String)}
     *   <li>{@link ApprovalVO#setProductTypeList(ArrayList)}
     *   <li>{@link ApprovalVO#setReceiverCrQuantity(String)}
     *   <li>{@link ApprovalVO#setReference(String)}
     *   <li>{@link ApprovalVO#setRefrenceNum(String)}
     *   <li>{@link ApprovalVO#setRemarks(String)}
     *   <li>{@link ApprovalVO#setRequestType(String)}
     *   <li>{@link ApprovalVO#setSearchCriteria(String)}
     *   <li>{@link ApprovalVO#setSearchUserID(String)}
     *   <li>{@link ApprovalVO#setSearchUserName(String)}
     *   <li>{@link ApprovalVO#setSecondLang(String)}
     *   <li>{@link ApprovalVO#setSenderDrQuantity(String)}
     *   <li>{@link ApprovalVO#setSessionDomainCode(String)}
     *   <li>{@link ApprovalVO#setStatus(String)}
     *   <li>{@link ApprovalVO#setToPrimaryMSISDN(String)}
     *   <li>{@link ApprovalVO#setTotalMRP(String)}
     *   <li>{@link ApprovalVO#setTotalReqQty(String)}
     *   <li>{@link ApprovalVO#setTotalStock(String)}
     *   <li>{@link ApprovalVO#setTotalTax1(String)}
     *   <li>{@link ApprovalVO#setTotalTax2(String)}
     *   <li>{@link ApprovalVO#setTotalTransferedAmount(String)}
     *   <li>{@link ApprovalVO#setTransferCategory(String)}
     *   <li>{@link ApprovalVO#setTransferDateAsString(String)}
     *   <li>{@link ApprovalVO#setTransferItemList(ArrayList)}
     *   <li>{@link ApprovalVO#setTransferNumber(String)}
     *   <li>{@link ApprovalVO#setUserCode(String)}
     *   <li>{@link ApprovalVO#setUserCommProfileName(String)}
     *   <li>{@link ApprovalVO#setUserCommProfileSetID(String)}
     *   <li>{@link ApprovalVO#setUserCommProfileSetVersion(String)}
     *   <li>{@link ApprovalVO#setUserGradeName(String)}
     *   <li>{@link ApprovalVO#setUserID(String)}
     *   <li>{@link ApprovalVO#setUserList(ArrayList)}
     *   <li>{@link ApprovalVO#setUserMsisdn(String)}
     *   <li>{@link ApprovalVO#setUserName(String)}
     *   <li>{@link ApprovalVO#setUserTransferProfileCode(String)}
     *   <li>{@link ApprovalVO#setUserTransferProfileName(String)}
     *   <li>{@link ApprovalVO#setWalletCode(String)}
     *   <li>{@link ApprovalVO#setWalletTypeList(List)}
     *   <li>{@link ApprovalVO#setWalletTypeWithWalletCode(String)}
     *   <li>{@link ApprovalVO#getAddress()}
     *   <li>{@link ApprovalVO#getApprove1Remark()}
     *   <li>{@link ApprovalVO#getApprove2Remark()}
     *   <li>{@link ApprovalVO#getApprove3Remark()}
     *   <li>{@link ApprovalVO#getCategoryCode()}
     *   <li>{@link ApprovalVO#getCategoryCodeDesc()}
     *   <li>{@link ApprovalVO#getCategoryList()}
     *   <li>{@link ApprovalVO#getChannelTransferVO()}
     *   <li>{@link ApprovalVO#getChannelUserID()}
     *   <li>{@link ApprovalVO#getChannelUserName()}
     *   <li>{@link ApprovalVO#getChannelUserStatus()}
     *   <li>{@link ApprovalVO#getCurrentDate()}
     *   <li>{@link ApprovalVO#getDefaultLang()}
     *   <li>{@link ApprovalVO#getDomainCode()}
     *   <li>{@link ApprovalVO#getDomainCodeDesc()}
     *   <li>{@link ApprovalVO#getDomainList()}
     *   <li>{@link ApprovalVO#getDomainTypeCode()}
     *   <li>{@link ApprovalVO#getErpCode()}
     *   <li>{@link ApprovalVO#getExternalTxnDate()}
     *   <li>{@link ApprovalVO#getExternalTxnExist()}
     *   <li>{@link ApprovalVO#getExternalTxnMandatory()}
     *   <li>{@link ApprovalVO#getExternalTxnNum()}
     *   <li>{@link ApprovalVO#getFocOrderApprovalLevel()}
     *   <li>{@link ApprovalVO#getGeoDomainCodeDescForUser()}
     *   <li>{@link ApprovalVO#getGeographicalDomainCode()}
     *   <li>{@link ApprovalVO#getGeographicalDomainCodeDesc()}
     *   <li>{@link ApprovalVO#getGeographicalDomainList()}
     *   <li>{@link ApprovalVO#getNetCommissionQuantity()}
     *   <li>{@link ApprovalVO#getNetworkCode()}
     *   <li>{@link ApprovalVO#getNetworkCodeDesc()}
     *   <li>{@link ApprovalVO#getProductListWithTaxes()}
     *   <li>{@link ApprovalVO#getProductQuantity()}
     *   <li>{@link ApprovalVO#getProductTypeCode()}
     *   <li>{@link ApprovalVO#getProductTypeCodeDesc()}
     *   <li>{@link ApprovalVO#getProductTypeList()}
     *   <li>{@link ApprovalVO#getReceiverCrQuantity()}
     *   <li>{@link ApprovalVO#getReference()}
     *   <li>{@link ApprovalVO#getRefrenceNum()}
     *   <li>{@link ApprovalVO#getRemarks()}
     *   <li>{@link ApprovalVO#getRequestType()}
     *   <li>{@link ApprovalVO#getSearchCriteria()}
     *   <li>{@link ApprovalVO#getSearchUserID()}
     *   <li>{@link ApprovalVO#getSearchUserName()}
     *   <li>{@link ApprovalVO#getSecondLang()}
     *   <li>{@link ApprovalVO#getSenderDrQuantity()}
     *   <li>{@link ApprovalVO#getSessionDomainCode()}
     *   <li>{@link ApprovalVO#getStatus()}
     *   <li>{@link ApprovalVO#getToPrimaryMSISDN()}
     *   <li>{@link ApprovalVO#getTotalMRP()}
     *   <li>{@link ApprovalVO#getTotalReqQty()}
     *   <li>{@link ApprovalVO#getTotalStock()}
     *   <li>{@link ApprovalVO#getTotalTax1()}
     *   <li>{@link ApprovalVO#getTotalTax2()}
     *   <li>{@link ApprovalVO#getTotalTransferedAmount()}
     *   <li>{@link ApprovalVO#getTransferCategory()}
     *   <li>{@link ApprovalVO#getTransferDateAsString()}
     *   <li>{@link ApprovalVO#getTransferItemList()}
     *   <li>{@link ApprovalVO#getTransferNumber()}
     *   <li>{@link ApprovalVO#getUserCode()}
     *   <li>{@link ApprovalVO#getUserCommProfileName()}
     *   <li>{@link ApprovalVO#getUserCommProfileSetID()}
     *   <li>{@link ApprovalVO#getUserCommProfileSetVersion()}
     *   <li>{@link ApprovalVO#getUserGradeName()}
     *   <li>{@link ApprovalVO#getUserID()}
     *   <li>{@link ApprovalVO#getUserList()}
     *   <li>{@link ApprovalVO#getUserMsisdn()}
     *   <li>{@link ApprovalVO#getUserName()}
     *   <li>{@link ApprovalVO#getUserTransferProfileCode()}
     *   <li>{@link ApprovalVO#getUserTransferProfileName()}
     *   <li>{@link ApprovalVO#getWalletCode()}
     *   <li>{@link ApprovalVO#getWalletTypeList()}
     *   <li>{@link ApprovalVO#getWalletTypeWithWalletCode()}
     *   <li>{@link ApprovalVO#isMultiWallet()}
     * </ul>
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testSetAddress() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R081 Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.restapi.o2c.service.ApprovalVO.<init>().
        //   The arrange section threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.ApprovalVO.<init>(ApprovalVO.java:94)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ApprovalVO approvalVO = null;
        String address = "";

        // Act
        approvalVO.setAddress(address);
        String approve1Remark = "";
        approvalVO.setApprove1Remark(approve1Remark);
        String approve2Remark = "";
        approvalVO.setApprove2Remark(approve2Remark);
        String approve3Remark = "";
        approvalVO.setApprove3Remark(approve3Remark);
        String categoryCode = "";
        approvalVO.setCategoryCode(categoryCode);
        String categoryCodeDesc = "";
        approvalVO.setCategoryCodeDesc(categoryCodeDesc);
        ArrayList categoryList = null;
        approvalVO.setCategoryList(categoryList);
        ChannelTransferVO channelTransferVO = null;
        approvalVO.setChannelTransferVO(channelTransferVO);
        String channelUserID = "";
        approvalVO.setChannelUserID(channelUserID);
        String channelUserName = "";
        approvalVO.setChannelUserName(channelUserName);
        String channelUserStatus = "";
        approvalVO.setChannelUserStatus(channelUserStatus);
        String currentDate = "";
        approvalVO.setCurrentDate(currentDate);
        String defaultLang = "";
        approvalVO.setDefaultLang(defaultLang);
        String domainCode = "";
        approvalVO.setDomainCode(domainCode);
        String domainCodeDesc = "";
        approvalVO.setDomainCodeDesc(domainCodeDesc);
        ArrayList domainList = null;
        approvalVO.setDomainList(domainList);
        String domainTypeCode = "";
        approvalVO.setDomainTypeCode(domainTypeCode);
        String erpCode = "";
        approvalVO.setErpCode(erpCode);
        String externalTxnDate = "";
        approvalVO.setExternalTxnDate(externalTxnDate);
        String externalTxnExist = "";
        approvalVO.setExternalTxnExist(externalTxnExist);
        String externalTxnMandatory = "";
        approvalVO.setExternalTxnMandatory(externalTxnMandatory);
        String externalTxnNum = "";
        approvalVO.setExternalTxnNum(externalTxnNum);
        int focOrderApprovalLevel = 0;
        approvalVO.setFocOrderApprovalLevel(focOrderApprovalLevel);
        String geoDomainCodeDescForUser = "";
        approvalVO.setGeoDomainCodeDescForUser(geoDomainCodeDescForUser);
        String geographicalDomainCode = "";
        approvalVO.setGeographicalDomainCode(geographicalDomainCode);
        String geographicalDomainCodeDesc = "";
        approvalVO.setGeographicalDomainCodeDesc(geographicalDomainCodeDesc);
        ArrayList geographicalDomainList = null;
        approvalVO.setGeographicalDomainList(geographicalDomainList);
        boolean multiWallet = false;
        approvalVO.setMultiWallet(multiWallet);
        String netCommissionQuantity = "";
        approvalVO.setNetCommissionQuantity(netCommissionQuantity);
        String networkCode = "";
        approvalVO.setNetworkCode(networkCode);
        String networkCodeDesc = "";
        approvalVO.setNetworkCodeDesc(networkCodeDesc);
        ArrayList productListWithTaxes = null;
        approvalVO.setProductListWithTaxes(productListWithTaxes);
        String productQuantity = "";
        approvalVO.setProductQuantity(productQuantity);
        String productTypeCode = "";
        approvalVO.setProductTypeCode(productTypeCode);
        String productTypeCodeDesc = "";
        approvalVO.setProductTypeCodeDesc(productTypeCodeDesc);
        ArrayList productTypeList = null;
        approvalVO.setProductTypeList(productTypeList);
        String receiverCrQuantity = "";
        approvalVO.setReceiverCrQuantity(receiverCrQuantity);
        String reference = "";
        approvalVO.setReference(reference);
        String refrenceNum = "";
        approvalVO.setRefrenceNum(refrenceNum);
        String remarks = "";
        approvalVO.setRemarks(remarks);
        String requestType = "";
        approvalVO.setRequestType(requestType);
        String searchCriteria = "";
        approvalVO.setSearchCriteria(searchCriteria);
        String searchUserID = "";
        approvalVO.setSearchUserID(searchUserID);
        String searchUserName = "";
        approvalVO.setSearchUserName(searchUserName);
        String secondLang = "";
        approvalVO.setSecondLang(secondLang);
        String senderDrQuantity = "";
        approvalVO.setSenderDrQuantity(senderDrQuantity);
        String sessionDomainCode = "";
        approvalVO.setSessionDomainCode(sessionDomainCode);
        String status = "";
        approvalVO.setStatus(status);
        String toPrimaryMSISDN = "";
        approvalVO.setToPrimaryMSISDN(toPrimaryMSISDN);
        String totalMRP = "";
        approvalVO.setTotalMRP(totalMRP);
        String totalReqQty = "";
        approvalVO.setTotalReqQty(totalReqQty);
        String totalStock = "";
        approvalVO.setTotalStock(totalStock);
        String totalTax1 = "";
        approvalVO.setTotalTax1(totalTax1);
        String totalTax2 = "";
        approvalVO.setTotalTax2(totalTax2);
        String totalTransferedAmount = "";
        approvalVO.setTotalTransferedAmount(totalTransferedAmount);
        String transferCategory = "";
        approvalVO.setTransferCategory(transferCategory);
        String transferDateAsString = "";
        approvalVO.setTransferDateAsString(transferDateAsString);
        ArrayList transferItemList = null;
        approvalVO.setTransferItemList(transferItemList);
        String transferNumber = "";
        approvalVO.setTransferNumber(transferNumber);
        String userCode = "";
        approvalVO.setUserCode(userCode);
        String userCommProfileName = "";
        approvalVO.setUserCommProfileName(userCommProfileName);
        String userCommProfileSetID = "";
        approvalVO.setUserCommProfileSetID(userCommProfileSetID);
        String userCommProfileSetVersion = "";
        approvalVO.setUserCommProfileSetVersion(userCommProfileSetVersion);
        String userGradeName = "";
        approvalVO.setUserGradeName(userGradeName);
        String userID = "";
        approvalVO.setUserID(userID);
        ArrayList userList = null;
        approvalVO.setUserList(userList);
        String userMsisdn = "";
        approvalVO.setUserMsisdn(userMsisdn);
        String userName = "";
        approvalVO.setUserName(userName);
        String userTransferProfileCode = "";
        approvalVO.setUserTransferProfileCode(userTransferProfileCode);
        String userTransferProfileName = "";
        approvalVO.setUserTransferProfileName(userTransferProfileName);
        String walletCode = "";
        approvalVO.setWalletCode(walletCode);
        List<ListValueVO> walletTypeList = null;
        approvalVO.setWalletTypeList(walletTypeList);
        String walletTypeWithWalletCode = "";
        approvalVO.setWalletTypeWithWalletCode(walletTypeWithWalletCode);
        String actualAddress = approvalVO.getAddress();
        String actualApprove1Remark = approvalVO.getApprove1Remark();
        String actualApprove2Remark = approvalVO.getApprove2Remark();
        String actualApprove3Remark = approvalVO.getApprove3Remark();
        String actualCategoryCode = approvalVO.getCategoryCode();
        String actualCategoryCodeDesc = approvalVO.getCategoryCodeDesc();
        ArrayList actualCategoryList = approvalVO.getCategoryList();
        ChannelTransferVO actualChannelTransferVO = approvalVO.getChannelTransferVO();
        String actualChannelUserID = approvalVO.getChannelUserID();
        String actualChannelUserName = approvalVO.getChannelUserName();
        String actualChannelUserStatus = approvalVO.getChannelUserStatus();
        String actualCurrentDate = approvalVO.getCurrentDate();
        String actualDefaultLang = approvalVO.getDefaultLang();
        String actualDomainCode = approvalVO.getDomainCode();
        String actualDomainCodeDesc = approvalVO.getDomainCodeDesc();
        ArrayList actualDomainList = approvalVO.getDomainList();
        String actualDomainTypeCode = approvalVO.getDomainTypeCode();
        String actualErpCode = approvalVO.getErpCode();
        String actualExternalTxnDate = approvalVO.getExternalTxnDate();
        String actualExternalTxnExist = approvalVO.getExternalTxnExist();
        String actualExternalTxnMandatory = approvalVO.getExternalTxnMandatory();
        String actualExternalTxnNum = approvalVO.getExternalTxnNum();
        int actualFocOrderApprovalLevel = approvalVO.getFocOrderApprovalLevel();
        String actualGeoDomainCodeDescForUser = approvalVO.getGeoDomainCodeDescForUser();
        String actualGeographicalDomainCode = approvalVO.getGeographicalDomainCode();
        String actualGeographicalDomainCodeDesc = approvalVO.getGeographicalDomainCodeDesc();
        ArrayList actualGeographicalDomainList = approvalVO.getGeographicalDomainList();
        String actualNetCommissionQuantity = approvalVO.getNetCommissionQuantity();
        String actualNetworkCode = approvalVO.getNetworkCode();
        String actualNetworkCodeDesc = approvalVO.getNetworkCodeDesc();
        ArrayList actualProductListWithTaxes = approvalVO.getProductListWithTaxes();
        String actualProductQuantity = approvalVO.getProductQuantity();
        String actualProductTypeCode = approvalVO.getProductTypeCode();
        String actualProductTypeCodeDesc = approvalVO.getProductTypeCodeDesc();
        ArrayList actualProductTypeList = approvalVO.getProductTypeList();
        String actualReceiverCrQuantity = approvalVO.getReceiverCrQuantity();
        String actualReference = approvalVO.getReference();
        String actualRefrenceNum = approvalVO.getRefrenceNum();
        String actualRemarks = approvalVO.getRemarks();
        String actualRequestType = approvalVO.getRequestType();
        String actualSearchCriteria = approvalVO.getSearchCriteria();
        String actualSearchUserID = approvalVO.getSearchUserID();
        String actualSearchUserName = approvalVO.getSearchUserName();
        String actualSecondLang = approvalVO.getSecondLang();
        String actualSenderDrQuantity = approvalVO.getSenderDrQuantity();
        String actualSessionDomainCode = approvalVO.getSessionDomainCode();
        String actualStatus = approvalVO.getStatus();
        String actualToPrimaryMSISDN = approvalVO.getToPrimaryMSISDN();
        String actualTotalMRP = approvalVO.getTotalMRP();
        String actualTotalReqQty = approvalVO.getTotalReqQty();
        String actualTotalStock = approvalVO.getTotalStock();
        String actualTotalTax1 = approvalVO.getTotalTax1();
        String actualTotalTax2 = approvalVO.getTotalTax2();
        String actualTotalTransferedAmount = approvalVO.getTotalTransferedAmount();
        String actualTransferCategory = approvalVO.getTransferCategory();
        String actualTransferDateAsString = approvalVO.getTransferDateAsString();
        ArrayList actualTransferItemList = approvalVO.getTransferItemList();
        String actualTransferNumber = approvalVO.getTransferNumber();
        String actualUserCode = approvalVO.getUserCode();
        String actualUserCommProfileName = approvalVO.getUserCommProfileName();
        String actualUserCommProfileSetID = approvalVO.getUserCommProfileSetID();
        String actualUserCommProfileSetVersion = approvalVO.getUserCommProfileSetVersion();
        String actualUserGradeName = approvalVO.getUserGradeName();
        String actualUserID = approvalVO.getUserID();
        ArrayList actualUserList = approvalVO.getUserList();
        String actualUserMsisdn = approvalVO.getUserMsisdn();
        String actualUserName = approvalVO.getUserName();
        String actualUserTransferProfileCode = approvalVO.getUserTransferProfileCode();
        String actualUserTransferProfileName = approvalVO.getUserTransferProfileName();
        String actualWalletCode = approvalVO.getWalletCode();
        List<ListValueVO> actualWalletTypeList = approvalVO.getWalletTypeList();
        String actualWalletTypeWithWalletCode = approvalVO.getWalletTypeWithWalletCode();
        boolean actualIsMultiWalletResult = approvalVO.isMultiWallet();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link ApprovalVO#setAddress(String)}
     *   <li>{@link ApprovalVO#setApprove1Remark(String)}
     *   <li>{@link ApprovalVO#setApprove2Remark(String)}
     *   <li>{@link ApprovalVO#setApprove3Remark(String)}
     *   <li>{@link ApprovalVO#setCategoryCode(String)}
     *   <li>{@link ApprovalVO#setCategoryCodeDesc(String)}
     *   <li>{@link ApprovalVO#setCategoryList(ArrayList)}
     *   <li>{@link ApprovalVO#setChannelTransferVO(ChannelTransferVO)}
     *   <li>{@link ApprovalVO#setChannelUserID(String)}
     *   <li>{@link ApprovalVO#setChannelUserName(String)}
     *   <li>{@link ApprovalVO#setChannelUserStatus(String)}
     *   <li>{@link ApprovalVO#setCurrentDate(String)}
     *   <li>{@link ApprovalVO#setDefaultLang(String)}
     *   <li>{@link ApprovalVO#setDomainCode(String)}
     *   <li>{@link ApprovalVO#setDomainCodeDesc(String)}
     *   <li>{@link ApprovalVO#setDomainList(ArrayList)}
     *   <li>{@link ApprovalVO#setDomainTypeCode(String)}
     *   <li>{@link ApprovalVO#setErpCode(String)}
     *   <li>{@link ApprovalVO#setExternalTxnDate(String)}
     *   <li>{@link ApprovalVO#setExternalTxnExist(String)}
     *   <li>{@link ApprovalVO#setExternalTxnMandatory(String)}
     *   <li>{@link ApprovalVO#setExternalTxnNum(String)}
     *   <li>{@link ApprovalVO#setFocOrderApprovalLevel(int)}
     *   <li>{@link ApprovalVO#setGeoDomainCodeDescForUser(String)}
     *   <li>{@link ApprovalVO#setGeographicalDomainCode(String)}
     *   <li>{@link ApprovalVO#setGeographicalDomainCodeDesc(String)}
     *   <li>{@link ApprovalVO#setGeographicalDomainList(ArrayList)}
     *   <li>{@link ApprovalVO#setMultiWallet(boolean)}
     *   <li>{@link ApprovalVO#setNetCommissionQuantity(String)}
     *   <li>{@link ApprovalVO#setNetworkCode(String)}
     *   <li>{@link ApprovalVO#setNetworkCodeDesc(String)}
     *   <li>{@link ApprovalVO#setProductListWithTaxes(ArrayList)}
     *   <li>{@link ApprovalVO#setProductQuantity(String)}
     *   <li>{@link ApprovalVO#setProductTypeCode(String)}
     *   <li>{@link ApprovalVO#setProductTypeCodeDesc(String)}
     *   <li>{@link ApprovalVO#setProductTypeList(ArrayList)}
     *   <li>{@link ApprovalVO#setReceiverCrQuantity(String)}
     *   <li>{@link ApprovalVO#setReference(String)}
     *   <li>{@link ApprovalVO#setRefrenceNum(String)}
     *   <li>{@link ApprovalVO#setRemarks(String)}
     *   <li>{@link ApprovalVO#setRequestType(String)}
     *   <li>{@link ApprovalVO#setSearchCriteria(String)}
     *   <li>{@link ApprovalVO#setSearchUserID(String)}
     *   <li>{@link ApprovalVO#setSearchUserName(String)}
     *   <li>{@link ApprovalVO#setSecondLang(String)}
     *   <li>{@link ApprovalVO#setSenderDrQuantity(String)}
     *   <li>{@link ApprovalVO#setSessionDomainCode(String)}
     *   <li>{@link ApprovalVO#setStatus(String)}
     *   <li>{@link ApprovalVO#setToPrimaryMSISDN(String)}
     *   <li>{@link ApprovalVO#setTotalMRP(String)}
     *   <li>{@link ApprovalVO#setTotalReqQty(String)}
     *   <li>{@link ApprovalVO#setTotalStock(String)}
     *   <li>{@link ApprovalVO#setTotalTax1(String)}
     *   <li>{@link ApprovalVO#setTotalTax2(String)}
     *   <li>{@link ApprovalVO#setTotalTransferedAmount(String)}
     *   <li>{@link ApprovalVO#setTransferCategory(String)}
     *   <li>{@link ApprovalVO#setTransferDateAsString(String)}
     *   <li>{@link ApprovalVO#setTransferItemList(ArrayList)}
     *   <li>{@link ApprovalVO#setTransferNumber(String)}
     *   <li>{@link ApprovalVO#setUserCode(String)}
     *   <li>{@link ApprovalVO#setUserCommProfileName(String)}
     *   <li>{@link ApprovalVO#setUserCommProfileSetID(String)}
     *   <li>{@link ApprovalVO#setUserCommProfileSetVersion(String)}
     *   <li>{@link ApprovalVO#setUserGradeName(String)}
     *   <li>{@link ApprovalVO#setUserID(String)}
     *   <li>{@link ApprovalVO#setUserList(ArrayList)}
     *   <li>{@link ApprovalVO#setUserMsisdn(String)}
     *   <li>{@link ApprovalVO#setUserName(String)}
     *   <li>{@link ApprovalVO#setUserTransferProfileCode(String)}
     *   <li>{@link ApprovalVO#setUserTransferProfileName(String)}
     *   <li>{@link ApprovalVO#setWalletCode(String)}
     *   <li>{@link ApprovalVO#setWalletTypeList(List)}
     *   <li>{@link ApprovalVO#setWalletTypeWithWalletCode(String)}
     *   <li>{@link ApprovalVO#getAddress()}
     *   <li>{@link ApprovalVO#getApprove1Remark()}
     *   <li>{@link ApprovalVO#getApprove2Remark()}
     *   <li>{@link ApprovalVO#getApprove3Remark()}
     *   <li>{@link ApprovalVO#getCategoryCode()}
     *   <li>{@link ApprovalVO#getCategoryCodeDesc()}
     *   <li>{@link ApprovalVO#getCategoryList()}
     *   <li>{@link ApprovalVO#getChannelTransferVO()}
     *   <li>{@link ApprovalVO#getChannelUserID()}
     *   <li>{@link ApprovalVO#getChannelUserName()}
     *   <li>{@link ApprovalVO#getChannelUserStatus()}
     *   <li>{@link ApprovalVO#getCurrentDate()}
     *   <li>{@link ApprovalVO#getDefaultLang()}
     *   <li>{@link ApprovalVO#getDomainCode()}
     *   <li>{@link ApprovalVO#getDomainCodeDesc()}
     *   <li>{@link ApprovalVO#getDomainList()}
     *   <li>{@link ApprovalVO#getDomainTypeCode()}
     *   <li>{@link ApprovalVO#getErpCode()}
     *   <li>{@link ApprovalVO#getExternalTxnDate()}
     *   <li>{@link ApprovalVO#getExternalTxnExist()}
     *   <li>{@link ApprovalVO#getExternalTxnMandatory()}
     *   <li>{@link ApprovalVO#getExternalTxnNum()}
     *   <li>{@link ApprovalVO#getFocOrderApprovalLevel()}
     *   <li>{@link ApprovalVO#getGeoDomainCodeDescForUser()}
     *   <li>{@link ApprovalVO#getGeographicalDomainCode()}
     *   <li>{@link ApprovalVO#getGeographicalDomainCodeDesc()}
     *   <li>{@link ApprovalVO#getGeographicalDomainList()}
     *   <li>{@link ApprovalVO#getNetCommissionQuantity()}
     *   <li>{@link ApprovalVO#getNetworkCode()}
     *   <li>{@link ApprovalVO#getNetworkCodeDesc()}
     *   <li>{@link ApprovalVO#getProductListWithTaxes()}
     *   <li>{@link ApprovalVO#getProductQuantity()}
     *   <li>{@link ApprovalVO#getProductTypeCode()}
     *   <li>{@link ApprovalVO#getProductTypeCodeDesc()}
     *   <li>{@link ApprovalVO#getProductTypeList()}
     *   <li>{@link ApprovalVO#getReceiverCrQuantity()}
     *   <li>{@link ApprovalVO#getReference()}
     *   <li>{@link ApprovalVO#getRefrenceNum()}
     *   <li>{@link ApprovalVO#getRemarks()}
     *   <li>{@link ApprovalVO#getRequestType()}
     *   <li>{@link ApprovalVO#getSearchCriteria()}
     *   <li>{@link ApprovalVO#getSearchUserID()}
     *   <li>{@link ApprovalVO#getSearchUserName()}
     *   <li>{@link ApprovalVO#getSecondLang()}
     *   <li>{@link ApprovalVO#getSenderDrQuantity()}
     *   <li>{@link ApprovalVO#getSessionDomainCode()}
     *   <li>{@link ApprovalVO#getStatus()}
     *   <li>{@link ApprovalVO#getToPrimaryMSISDN()}
     *   <li>{@link ApprovalVO#getTotalMRP()}
     *   <li>{@link ApprovalVO#getTotalReqQty()}
     *   <li>{@link ApprovalVO#getTotalStock()}
     *   <li>{@link ApprovalVO#getTotalTax1()}
     *   <li>{@link ApprovalVO#getTotalTax2()}
     *   <li>{@link ApprovalVO#getTotalTransferedAmount()}
     *   <li>{@link ApprovalVO#getTransferCategory()}
     *   <li>{@link ApprovalVO#getTransferDateAsString()}
     *   <li>{@link ApprovalVO#getTransferItemList()}
     *   <li>{@link ApprovalVO#getTransferNumber()}
     *   <li>{@link ApprovalVO#getUserCode()}
     *   <li>{@link ApprovalVO#getUserCommProfileName()}
     *   <li>{@link ApprovalVO#getUserCommProfileSetID()}
     *   <li>{@link ApprovalVO#getUserCommProfileSetVersion()}
     *   <li>{@link ApprovalVO#getUserGradeName()}
     *   <li>{@link ApprovalVO#getUserID()}
     *   <li>{@link ApprovalVO#getUserList()}
     *   <li>{@link ApprovalVO#getUserMsisdn()}
     *   <li>{@link ApprovalVO#getUserName()}
     *   <li>{@link ApprovalVO#getUserTransferProfileCode()}
     *   <li>{@link ApprovalVO#getUserTransferProfileName()}
     *   <li>{@link ApprovalVO#getWalletCode()}
     *   <li>{@link ApprovalVO#getWalletTypeList()}
     *   <li>{@link ApprovalVO#getWalletTypeWithWalletCode()}
     *   <li>{@link ApprovalVO#isMultiWallet()}
     * </ul>
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSetAddress2() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R081 Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.restapi.o2c.service.ApprovalVO.<init>().
        //   The arrange section threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.ApprovalVO.<init>(ApprovalVO.java:94)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ApprovalVO approvalVO = null;
        String address = "";

        // Act
        approvalVO.setAddress(address);
        String approve1Remark = "";
        approvalVO.setApprove1Remark(approve1Remark);
        String approve2Remark = "";
        approvalVO.setApprove2Remark(approve2Remark);
        String approve3Remark = "";
        approvalVO.setApprove3Remark(approve3Remark);
        String categoryCode = "";
        approvalVO.setCategoryCode(categoryCode);
        String categoryCodeDesc = "";
        approvalVO.setCategoryCodeDesc(categoryCodeDesc);
        ArrayList categoryList = null;
        approvalVO.setCategoryList(categoryList);
        ChannelTransferVO channelTransferVO = null;
        approvalVO.setChannelTransferVO(channelTransferVO);
        String channelUserID = "";
        approvalVO.setChannelUserID(channelUserID);
        String channelUserName = "";
        approvalVO.setChannelUserName(channelUserName);
        String channelUserStatus = "";
        approvalVO.setChannelUserStatus(channelUserStatus);
        String currentDate = "";
        approvalVO.setCurrentDate(currentDate);
        String defaultLang = "";
        approvalVO.setDefaultLang(defaultLang);
        String domainCode = "";
        approvalVO.setDomainCode(domainCode);
        String domainCodeDesc = "";
        approvalVO.setDomainCodeDesc(domainCodeDesc);
        ArrayList domainList = null;
        approvalVO.setDomainList(domainList);
        String domainTypeCode = "";
        approvalVO.setDomainTypeCode(domainTypeCode);
        String erpCode = "";
        approvalVO.setErpCode(erpCode);
        String externalTxnDate = "";
        approvalVO.setExternalTxnDate(externalTxnDate);
        String externalTxnExist = "";
        approvalVO.setExternalTxnExist(externalTxnExist);
        String externalTxnMandatory = "";
        approvalVO.setExternalTxnMandatory(externalTxnMandatory);
        String externalTxnNum = "";
        approvalVO.setExternalTxnNum(externalTxnNum);
        int focOrderApprovalLevel = 0;
        approvalVO.setFocOrderApprovalLevel(focOrderApprovalLevel);
        String geoDomainCodeDescForUser = "";
        approvalVO.setGeoDomainCodeDescForUser(geoDomainCodeDescForUser);
        String geographicalDomainCode = "";
        approvalVO.setGeographicalDomainCode(geographicalDomainCode);
        String geographicalDomainCodeDesc = "";
        approvalVO.setGeographicalDomainCodeDesc(geographicalDomainCodeDesc);
        ArrayList geographicalDomainList = null;
        approvalVO.setGeographicalDomainList(geographicalDomainList);
        boolean multiWallet = false;
        approvalVO.setMultiWallet(multiWallet);
        String netCommissionQuantity = "";
        approvalVO.setNetCommissionQuantity(netCommissionQuantity);
        String networkCode = "";
        approvalVO.setNetworkCode(networkCode);
        String networkCodeDesc = "";
        approvalVO.setNetworkCodeDesc(networkCodeDesc);
        ArrayList productListWithTaxes = null;
        approvalVO.setProductListWithTaxes(productListWithTaxes);
        String productQuantity = "";
        approvalVO.setProductQuantity(productQuantity);
        String productTypeCode = "";
        approvalVO.setProductTypeCode(productTypeCode);
        String productTypeCodeDesc = "";
        approvalVO.setProductTypeCodeDesc(productTypeCodeDesc);
        ArrayList productTypeList = null;
        approvalVO.setProductTypeList(productTypeList);
        String receiverCrQuantity = "";
        approvalVO.setReceiverCrQuantity(receiverCrQuantity);
        String reference = "";
        approvalVO.setReference(reference);
        String refrenceNum = "";
        approvalVO.setRefrenceNum(refrenceNum);
        String remarks = "";
        approvalVO.setRemarks(remarks);
        String requestType = "";
        approvalVO.setRequestType(requestType);
        String searchCriteria = "";
        approvalVO.setSearchCriteria(searchCriteria);
        String searchUserID = "";
        approvalVO.setSearchUserID(searchUserID);
        String searchUserName = "";
        approvalVO.setSearchUserName(searchUserName);
        String secondLang = "";
        approvalVO.setSecondLang(secondLang);
        String senderDrQuantity = "";
        approvalVO.setSenderDrQuantity(senderDrQuantity);
        String sessionDomainCode = "";
        approvalVO.setSessionDomainCode(sessionDomainCode);
        String status = "";
        approvalVO.setStatus(status);
        String toPrimaryMSISDN = "";
        approvalVO.setToPrimaryMSISDN(toPrimaryMSISDN);
        String totalMRP = "";
        approvalVO.setTotalMRP(totalMRP);
        String totalReqQty = "";
        approvalVO.setTotalReqQty(totalReqQty);
        String totalStock = "";
        approvalVO.setTotalStock(totalStock);
        String totalTax1 = "";
        approvalVO.setTotalTax1(totalTax1);
        String totalTax2 = "";
        approvalVO.setTotalTax2(totalTax2);
        String totalTransferedAmount = "";
        approvalVO.setTotalTransferedAmount(totalTransferedAmount);
        String transferCategory = "";
        approvalVO.setTransferCategory(transferCategory);
        String transferDateAsString = "";
        approvalVO.setTransferDateAsString(transferDateAsString);
        ArrayList transferItemList = null;
        approvalVO.setTransferItemList(transferItemList);
        String transferNumber = "";
        approvalVO.setTransferNumber(transferNumber);
        String userCode = "";
        approvalVO.setUserCode(userCode);
        String userCommProfileName = "";
        approvalVO.setUserCommProfileName(userCommProfileName);
        String userCommProfileSetID = "";
        approvalVO.setUserCommProfileSetID(userCommProfileSetID);
        String userCommProfileSetVersion = "";
        approvalVO.setUserCommProfileSetVersion(userCommProfileSetVersion);
        String userGradeName = "";
        approvalVO.setUserGradeName(userGradeName);
        String userID = "";
        approvalVO.setUserID(userID);
        ArrayList userList = null;
        approvalVO.setUserList(userList);
        String userMsisdn = "";
        approvalVO.setUserMsisdn(userMsisdn);
        String userName = "";
        approvalVO.setUserName(userName);
        String userTransferProfileCode = "";
        approvalVO.setUserTransferProfileCode(userTransferProfileCode);
        String userTransferProfileName = "";
        approvalVO.setUserTransferProfileName(userTransferProfileName);
        String walletCode = "";
        approvalVO.setWalletCode(walletCode);
        List<ListValueVO> walletTypeList = null;
        approvalVO.setWalletTypeList(walletTypeList);
        String walletTypeWithWalletCode = "";
        approvalVO.setWalletTypeWithWalletCode(walletTypeWithWalletCode);
        String actualAddress = approvalVO.getAddress();
        String actualApprove1Remark = approvalVO.getApprove1Remark();
        String actualApprove2Remark = approvalVO.getApprove2Remark();
        String actualApprove3Remark = approvalVO.getApprove3Remark();
        String actualCategoryCode = approvalVO.getCategoryCode();
        String actualCategoryCodeDesc = approvalVO.getCategoryCodeDesc();
        ArrayList actualCategoryList = approvalVO.getCategoryList();
        ChannelTransferVO actualChannelTransferVO = approvalVO.getChannelTransferVO();
        String actualChannelUserID = approvalVO.getChannelUserID();
        String actualChannelUserName = approvalVO.getChannelUserName();
        String actualChannelUserStatus = approvalVO.getChannelUserStatus();
        String actualCurrentDate = approvalVO.getCurrentDate();
        String actualDefaultLang = approvalVO.getDefaultLang();
        String actualDomainCode = approvalVO.getDomainCode();
        String actualDomainCodeDesc = approvalVO.getDomainCodeDesc();
        ArrayList actualDomainList = approvalVO.getDomainList();
        String actualDomainTypeCode = approvalVO.getDomainTypeCode();
        String actualErpCode = approvalVO.getErpCode();
        String actualExternalTxnDate = approvalVO.getExternalTxnDate();
        String actualExternalTxnExist = approvalVO.getExternalTxnExist();
        String actualExternalTxnMandatory = approvalVO.getExternalTxnMandatory();
        String actualExternalTxnNum = approvalVO.getExternalTxnNum();
        int actualFocOrderApprovalLevel = approvalVO.getFocOrderApprovalLevel();
        String actualGeoDomainCodeDescForUser = approvalVO.getGeoDomainCodeDescForUser();
        String actualGeographicalDomainCode = approvalVO.getGeographicalDomainCode();
        String actualGeographicalDomainCodeDesc = approvalVO.getGeographicalDomainCodeDesc();
        ArrayList actualGeographicalDomainList = approvalVO.getGeographicalDomainList();
        String actualNetCommissionQuantity = approvalVO.getNetCommissionQuantity();
        String actualNetworkCode = approvalVO.getNetworkCode();
        String actualNetworkCodeDesc = approvalVO.getNetworkCodeDesc();
        ArrayList actualProductListWithTaxes = approvalVO.getProductListWithTaxes();
        String actualProductQuantity = approvalVO.getProductQuantity();
        String actualProductTypeCode = approvalVO.getProductTypeCode();
        String actualProductTypeCodeDesc = approvalVO.getProductTypeCodeDesc();
        ArrayList actualProductTypeList = approvalVO.getProductTypeList();
        String actualReceiverCrQuantity = approvalVO.getReceiverCrQuantity();
        String actualReference = approvalVO.getReference();
        String actualRefrenceNum = approvalVO.getRefrenceNum();
        String actualRemarks = approvalVO.getRemarks();
        String actualRequestType = approvalVO.getRequestType();
        String actualSearchCriteria = approvalVO.getSearchCriteria();
        String actualSearchUserID = approvalVO.getSearchUserID();
        String actualSearchUserName = approvalVO.getSearchUserName();
        String actualSecondLang = approvalVO.getSecondLang();
        String actualSenderDrQuantity = approvalVO.getSenderDrQuantity();
        String actualSessionDomainCode = approvalVO.getSessionDomainCode();
        String actualStatus = approvalVO.getStatus();
        String actualToPrimaryMSISDN = approvalVO.getToPrimaryMSISDN();
        String actualTotalMRP = approvalVO.getTotalMRP();
        String actualTotalReqQty = approvalVO.getTotalReqQty();
        String actualTotalStock = approvalVO.getTotalStock();
        String actualTotalTax1 = approvalVO.getTotalTax1();
        String actualTotalTax2 = approvalVO.getTotalTax2();
        String actualTotalTransferedAmount = approvalVO.getTotalTransferedAmount();
        String actualTransferCategory = approvalVO.getTransferCategory();
        String actualTransferDateAsString = approvalVO.getTransferDateAsString();
        ArrayList actualTransferItemList = approvalVO.getTransferItemList();
        String actualTransferNumber = approvalVO.getTransferNumber();
        String actualUserCode = approvalVO.getUserCode();
        String actualUserCommProfileName = approvalVO.getUserCommProfileName();
        String actualUserCommProfileSetID = approvalVO.getUserCommProfileSetID();
        String actualUserCommProfileSetVersion = approvalVO.getUserCommProfileSetVersion();
        String actualUserGradeName = approvalVO.getUserGradeName();
        String actualUserID = approvalVO.getUserID();
        ArrayList actualUserList = approvalVO.getUserList();
        String actualUserMsisdn = approvalVO.getUserMsisdn();
        String actualUserName = approvalVO.getUserName();
        String actualUserTransferProfileCode = approvalVO.getUserTransferProfileCode();
        String actualUserTransferProfileName = approvalVO.getUserTransferProfileName();
        String actualWalletCode = approvalVO.getWalletCode();
        List<ListValueVO> actualWalletTypeList = approvalVO.getWalletTypeList();
        String actualWalletTypeWithWalletCode = approvalVO.getWalletTypeWithWalletCode();
        boolean actualIsMultiWalletResult = approvalVO.isMultiWallet();

        // Assert
        // TODO: Add assertions on result
    }
}

