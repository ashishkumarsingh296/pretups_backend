package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserOTFCountsVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class O2CApprovalTxnDetailsResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CApprovalTxnDetailsResponseVO}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setAddress(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setAllOrder(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setAllUser(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setApprovalDone(boolean)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setApprovalLevel(int)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setApprove1Remark(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setApprove2Remark(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setApprove3Remark(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setCategoryCode(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setCategoryCodeForUserCode(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setCategoryList(ArrayList)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setCategoryName(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setChannelOwnerCategory(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setChannelOwnerCategoryDesc(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setChannelOwnerCategoryUserID(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setChannelOwnerCategoryUserName(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setChannelTransferList(ArrayList)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setChannelTransferVO(ChannelTransferVO)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setChannelUserStatus(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setCloseTransaction(boolean)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setCommissionProfileID(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setCommissionProfileName(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setCommissionProfileVersion(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setCommissionQuantity(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setCurrentApprovalLevel(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setDistributorMode(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setDistributorModeDesc(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setDistributorModeList(List)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setDistributorModeValue(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setDistributorName(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setDomainCode(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setDomainList(ArrayList)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setDomainName(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setDomainNameForUserCode(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setDomainTypeCode(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setDualCommissionType(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setErpCode(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setErrorList(ArrayList)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setExternalTxnDate(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setExternalTxnExist(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setExternalTxnMandatory(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setExternalTxnNum(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setFirstApprovalLimit(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setFirstLevelApprovedQuantity(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setFromUserCodeFlag(boolean)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setGardeDesc(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setGeoDomainCodeForUser(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setGeoDomainNameForUser(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setGeographicDomainCode(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setGeographicDomainList(ArrayList)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setGeographicDomainName(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setListSize(int)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setMrpList(ArrayList)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setNetPayableAmount(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setNetPayableAmountApproval(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setNetworkCode(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setNetworkName(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setOtfCountsUpdated(boolean)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setOtfRate(Double)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setOtfType(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setOtfValue(Long)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setOwnerSame(boolean)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setPackageDetails(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setPackageDetailsDesc(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setPackageDetailsList(List)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setPackageTotal(double)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setPayableAmount(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setPayableAmountApproval(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setPaymentInstDesc(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setPaymentInstNum(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setPaymentInstrumentAmt(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setPaymentInstrumentCode(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setPaymentInstrumentDate(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setPaymentInstrumentList(ArrayList)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setPaymentInstrumentName(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setPopUpUserID(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setPrimaryNumber(boolean)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setPrimaryTxnNum(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setProductType(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setQuantity(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setReceiverCreditQuantity(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setReconcilationFlag(boolean)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setRefrenceNum(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setRejectOrder(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setRemarks(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setReportHeaderName(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setRetPrice(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setSecondApprovalLimit(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setSecondLevelApprovedQuantity(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setSegment(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setSegmentDesc(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setSelectedTransfer(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setSelectedUserId(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setSenderDebitQuantity(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setSessionDomainCode(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setShowPaymentDetails(boolean)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setShowPaymentInstrumentType(boolean)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setSlabsList(ArrayList)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setSlabsListSize(int)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setThirdLevelApprovedQuantity(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTime(long)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setToPrimaryMSISDN(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTotalComm(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTotalCommValue(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTotalInitialRequestedQuantity(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTotalMRP(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTotalNetPayableAmount(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTotalOtfValue(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTotalOthComm(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTotalPayableAmount(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTotalReqQty(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTotalStock(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTotalTax1(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTotalTax2(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTotalTax3(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTotalTransferedAmount(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTransferDate(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTransferInitatorLoginID(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTransferItemList(ArrayList)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTransferMultipleOff(long)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTransferNum(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setTransferProfileName(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setUserCode(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setUserID(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setUserList(ArrayList)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setUserName(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setUserNameTmp(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setUserOTFCountsVO(UserOTFCountsVO)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setValidatePaymentInstruments(boolean)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setVomsActiveMrp(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setVomsCategoryList(ArrayList)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setVomsProductList(ArrayList)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setVoucherType(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setVoucherTypeDesc(String)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#setVoucherTypeList(ArrayList)}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#toString()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getAddress()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getAllOrder()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getAllUser()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getApprovalLevel()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getApprove1Remark()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getApprove2Remark()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getApprove3Remark()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getCategoryCode()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getCategoryCodeForUserCode()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getCategoryList()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getCategoryName()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getChannelOwnerCategory()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getChannelOwnerCategoryDesc()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getChannelOwnerCategoryUserID()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getChannelOwnerCategoryUserName()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getChannelTransferList()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getChannelTransferVO()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getChannelUserStatus()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getCommissionProfileID()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getCommissionProfileName()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getCommissionProfileVersion()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getCommissionQuantity()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getCurrentApprovalLevel()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getDistributorMode()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getDistributorModeDesc()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getDistributorModeList()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getDistributorModeValue()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getDistributorName()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getDomainCode()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getDomainList()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getDomainName()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getDomainNameForUserCode()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getDomainTypeCode()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getDualCommissionType()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getErpCode()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getErrorList()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getExternalTxnDate()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getExternalTxnExist()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getExternalTxnMandatory()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getExternalTxnNum()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getFirstApprovalLimit()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getFirstLevelApprovedQuantity()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getGardeDesc()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getGeoDomainCodeForUser()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getGeoDomainNameForUser()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getGeographicDomainCode()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getGeographicDomainList()}
     *   <li>{@link O2CApprovalTxnDetailsResponseVO#getGeographicDomainName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CApprovalTxnDetailsResponseVO actualO2cApprovalTxnDetailsResponseVO = new O2CApprovalTxnDetailsResponseVO();
        actualO2cApprovalTxnDetailsResponseVO.setAddress("42 Main St");
        actualO2cApprovalTxnDetailsResponseVO.setAllOrder("All Order");
        actualO2cApprovalTxnDetailsResponseVO.setAllUser("All User");
        actualO2cApprovalTxnDetailsResponseVO.setApprovalDone(true);
        actualO2cApprovalTxnDetailsResponseVO.setApprovalLevel(42);
        actualO2cApprovalTxnDetailsResponseVO.setApprove1Remark("Approve1 Remark");
        actualO2cApprovalTxnDetailsResponseVO.setApprove2Remark("Approve2 Remark");
        actualO2cApprovalTxnDetailsResponseVO.setApprove3Remark("Approve3 Remark");
        actualO2cApprovalTxnDetailsResponseVO.setCategoryCode("Category Code");
        actualO2cApprovalTxnDetailsResponseVO.setCategoryCodeForUserCode("Category Code For User Code");
        ArrayList categoryList = new ArrayList();
        actualO2cApprovalTxnDetailsResponseVO.setCategoryList(categoryList);
        actualO2cApprovalTxnDetailsResponseVO.setCategoryName("Category Name");
        actualO2cApprovalTxnDetailsResponseVO.setChannelOwnerCategory("Channel Owner Category");
        actualO2cApprovalTxnDetailsResponseVO.setChannelOwnerCategoryDesc("Channel Owner Category Desc");
        actualO2cApprovalTxnDetailsResponseVO.setChannelOwnerCategoryUserID("Channel Owner Category User ID");
        actualO2cApprovalTxnDetailsResponseVO.setChannelOwnerCategoryUserName("janedoe");
        ArrayList channelTransferList = new ArrayList();
        actualO2cApprovalTxnDetailsResponseVO.setChannelTransferList(channelTransferList);
        ChannelTransferVO channelTransferVO = ChannelTransferVO.getInstance();
        actualO2cApprovalTxnDetailsResponseVO.setChannelTransferVO(channelTransferVO);
        actualO2cApprovalTxnDetailsResponseVO.setChannelUserStatus("Channel User Status");
        actualO2cApprovalTxnDetailsResponseVO.setCloseTransaction(true);
        actualO2cApprovalTxnDetailsResponseVO.setCommissionProfileID("Commission Profile ID");
        actualO2cApprovalTxnDetailsResponseVO.setCommissionProfileName("foo.txt");
        actualO2cApprovalTxnDetailsResponseVO.setCommissionProfileVersion("1.0.2");
        actualO2cApprovalTxnDetailsResponseVO.setCommissionQuantity("Commission Quantity");
        actualO2cApprovalTxnDetailsResponseVO.setCurrentApprovalLevel("Current Approval Level");
        actualO2cApprovalTxnDetailsResponseVO.setDistributorMode("Distributor Mode");
        actualO2cApprovalTxnDetailsResponseVO.setDistributorModeDesc("Distributor Mode Desc");
        ArrayList<Object> distributorModeList = new ArrayList<>();
        actualO2cApprovalTxnDetailsResponseVO.setDistributorModeList(distributorModeList);
        actualO2cApprovalTxnDetailsResponseVO.setDistributorModeValue("42");
        actualO2cApprovalTxnDetailsResponseVO.setDistributorName("Distributor Name");
        actualO2cApprovalTxnDetailsResponseVO.setDomainCode("Domain Code");
        ArrayList domainList = new ArrayList();
        actualO2cApprovalTxnDetailsResponseVO.setDomainList(domainList);
        actualO2cApprovalTxnDetailsResponseVO.setDomainName("Domain Name");
        actualO2cApprovalTxnDetailsResponseVO.setDomainNameForUserCode("Domain Name For User Code");
        actualO2cApprovalTxnDetailsResponseVO.setDomainTypeCode("Domain Type Code");
        actualO2cApprovalTxnDetailsResponseVO.setDualCommissionType("Dual Commission Type");
        actualO2cApprovalTxnDetailsResponseVO.setErpCode("Erp Code");
        ArrayList<ListValueVO> errorList = new ArrayList<>();
        actualO2cApprovalTxnDetailsResponseVO.setErrorList(errorList);
        actualO2cApprovalTxnDetailsResponseVO.setExternalTxnDate("2020-03-01");
        actualO2cApprovalTxnDetailsResponseVO.setExternalTxnExist("External Txn Exist");
        actualO2cApprovalTxnDetailsResponseVO.setExternalTxnMandatory("External Txn Mandatory");
        actualO2cApprovalTxnDetailsResponseVO.setExternalTxnNum("External Txn Num");
        actualO2cApprovalTxnDetailsResponseVO.setFirstApprovalLimit("First Approval Limit");
        actualO2cApprovalTxnDetailsResponseVO.setFirstLevelApprovedQuantity("First Level Approved Quantity");
        actualO2cApprovalTxnDetailsResponseVO.setFromUserCodeFlag(true);
        actualO2cApprovalTxnDetailsResponseVO.setGardeDesc("Garde Desc");
        actualO2cApprovalTxnDetailsResponseVO.setGeoDomainCodeForUser("Geo Domain Code For User");
        actualO2cApprovalTxnDetailsResponseVO.setGeoDomainNameForUser("Geo Domain Name For User");
        actualO2cApprovalTxnDetailsResponseVO.setGeographicDomainCode("Geographic Domain Code");
        ArrayList geographicDomainList = new ArrayList();
        actualO2cApprovalTxnDetailsResponseVO.setGeographicDomainList(geographicDomainList);
        actualO2cApprovalTxnDetailsResponseVO.setGeographicDomainName("Geographic Domain Name");
        actualO2cApprovalTxnDetailsResponseVO.setListSize(3);
        actualO2cApprovalTxnDetailsResponseVO.setMrpList(new ArrayList<>());
        actualO2cApprovalTxnDetailsResponseVO.setNetPayableAmount("10");
        actualO2cApprovalTxnDetailsResponseVO.setNetPayableAmountApproval("10");
        actualO2cApprovalTxnDetailsResponseVO.setNetworkCode("Network Code");
        actualO2cApprovalTxnDetailsResponseVO.setNetworkName("Network Name");
        actualO2cApprovalTxnDetailsResponseVO.setOtfCountsUpdated(true);
        actualO2cApprovalTxnDetailsResponseVO.setOtfRate(10.0d);
        actualO2cApprovalTxnDetailsResponseVO.setOtfType("Otf Type");
        actualO2cApprovalTxnDetailsResponseVO.setOtfValue(42L);
        actualO2cApprovalTxnDetailsResponseVO.setOwnerSame(true);
        actualO2cApprovalTxnDetailsResponseVO.setPackageDetails("java.text");
        actualO2cApprovalTxnDetailsResponseVO.setPackageDetailsDesc("java.text");
        actualO2cApprovalTxnDetailsResponseVO.setPackageDetailsList(new ArrayList<>());
        actualO2cApprovalTxnDetailsResponseVO.setPackageTotal(10.0d);
        actualO2cApprovalTxnDetailsResponseVO.setPayableAmount("10");
        actualO2cApprovalTxnDetailsResponseVO.setPayableAmountApproval("10");
        actualO2cApprovalTxnDetailsResponseVO.setPaymentInstDesc("Payment Inst Desc");
        actualO2cApprovalTxnDetailsResponseVO.setPaymentInstNum("Payment Inst Num");
        actualO2cApprovalTxnDetailsResponseVO.setPaymentInstrumentAmt("Payment Instrument Amt");
        actualO2cApprovalTxnDetailsResponseVO.setPaymentInstrumentCode("Payment Instrument Code");
        actualO2cApprovalTxnDetailsResponseVO.setPaymentInstrumentDate("2020-03-01");
        actualO2cApprovalTxnDetailsResponseVO.setPaymentInstrumentList(new ArrayList());
        actualO2cApprovalTxnDetailsResponseVO.setPaymentInstrumentName("Payment Instrument Name");
        actualO2cApprovalTxnDetailsResponseVO.setPopUpUserID("Pop Up User ID");
        actualO2cApprovalTxnDetailsResponseVO.setPrimaryNumber(true);
        actualO2cApprovalTxnDetailsResponseVO.setPrimaryTxnNum("Primary Txn Num");
        actualO2cApprovalTxnDetailsResponseVO.setProductType("Product Type");
        actualO2cApprovalTxnDetailsResponseVO.setQuantity("Quantity");
        actualO2cApprovalTxnDetailsResponseVO.setReceiverCreditQuantity("Receiver Credit Quantity");
        actualO2cApprovalTxnDetailsResponseVO.setReconcilationFlag(true);
        actualO2cApprovalTxnDetailsResponseVO.setRefrenceNum("Refrence Num");
        actualO2cApprovalTxnDetailsResponseVO.setRejectOrder("Reject Order");
        actualO2cApprovalTxnDetailsResponseVO.setRemarks("Remarks");
        actualO2cApprovalTxnDetailsResponseVO.setReportHeaderName("Report Header Name");
        actualO2cApprovalTxnDetailsResponseVO.setRetPrice("Ret Price");
        actualO2cApprovalTxnDetailsResponseVO.setSecondApprovalLimit("Second Approval Limit");
        actualO2cApprovalTxnDetailsResponseVO.setSecondLevelApprovedQuantity("Second Level Approved Quantity");
        actualO2cApprovalTxnDetailsResponseVO.setSegment("Segment");
        actualO2cApprovalTxnDetailsResponseVO.setSegmentDesc("Segment Desc");
        actualO2cApprovalTxnDetailsResponseVO.setSelectedTransfer("Selected Transfer");
        actualO2cApprovalTxnDetailsResponseVO.setSelectedUserId("42");
        actualO2cApprovalTxnDetailsResponseVO.setSenderDebitQuantity("Sender Debit Quantity");
        actualO2cApprovalTxnDetailsResponseVO.setSessionDomainCode("Session Domain Code");
        actualO2cApprovalTxnDetailsResponseVO.setShowPaymentDetails(true);
        actualO2cApprovalTxnDetailsResponseVO.setShowPaymentInstrumentType(true);
        actualO2cApprovalTxnDetailsResponseVO.setSlabsList(new ArrayList());
        actualO2cApprovalTxnDetailsResponseVO.setSlabsListSize(3);
        actualO2cApprovalTxnDetailsResponseVO.setThirdLevelApprovedQuantity("Third Level Approved Quantity");
        actualO2cApprovalTxnDetailsResponseVO.setTime(10L);
        actualO2cApprovalTxnDetailsResponseVO.setToPrimaryMSISDN("To Primary MSISDN");
        actualO2cApprovalTxnDetailsResponseVO.setTotalComm("Total Comm");
        actualO2cApprovalTxnDetailsResponseVO.setTotalCommValue("42");
        actualO2cApprovalTxnDetailsResponseVO.setTotalInitialRequestedQuantity("Total Initial Requested Quantity");
        actualO2cApprovalTxnDetailsResponseVO.setTotalMRP("Total MRP");
        actualO2cApprovalTxnDetailsResponseVO.setTotalNetPayableAmount("10");
        actualO2cApprovalTxnDetailsResponseVO.setTotalOtfValue("42");
        actualO2cApprovalTxnDetailsResponseVO.setTotalOthComm("Total Oth Comm");
        actualO2cApprovalTxnDetailsResponseVO.setTotalPayableAmount("10");
        actualO2cApprovalTxnDetailsResponseVO.setTotalReqQty("Total Req Qty");
        actualO2cApprovalTxnDetailsResponseVO.setTotalStock("Total Stock");
        actualO2cApprovalTxnDetailsResponseVO.setTotalTax1("Total Tax1");
        actualO2cApprovalTxnDetailsResponseVO.setTotalTax2("Total Tax2");
        actualO2cApprovalTxnDetailsResponseVO.setTotalTax3("Total Tax3");
        actualO2cApprovalTxnDetailsResponseVO.setTotalTransferedAmount("10");
        actualO2cApprovalTxnDetailsResponseVO.setTransferDate("2020-03-01");
        actualO2cApprovalTxnDetailsResponseVO.setTransferInitatorLoginID("Transfer Initator Login ID");
        actualO2cApprovalTxnDetailsResponseVO.setTransferItemList(new ArrayList());
        actualO2cApprovalTxnDetailsResponseVO.setTransferMultipleOff(1L);
        actualO2cApprovalTxnDetailsResponseVO.setTransferNum("Transfer Num");
        actualO2cApprovalTxnDetailsResponseVO.setTransferProfileName("foo.txt");
        actualO2cApprovalTxnDetailsResponseVO.setUserCode("User Code");
        actualO2cApprovalTxnDetailsResponseVO.setUserID("User ID");
        actualO2cApprovalTxnDetailsResponseVO.setUserList(new ArrayList());
        actualO2cApprovalTxnDetailsResponseVO.setUserName("janedoe");
        actualO2cApprovalTxnDetailsResponseVO.setUserNameTmp("janedoe");
        UserOTFCountsVO userOTFCountsVO = new UserOTFCountsVO();
        userOTFCountsVO.setAddnl(true);
        userOTFCountsVO.setAdnlComOTFDetailId("42");
        userOTFCountsVO.setBaseComOTFDetailId("42");
        userOTFCountsVO.setCommType("Comm Type");
        userOTFCountsVO.setOtfCount(3);
        userOTFCountsVO.setOtfValue(42L);
        userOTFCountsVO.setUpdateRecord(true);
        userOTFCountsVO.setUserID("User ID");
        actualO2cApprovalTxnDetailsResponseVO.setUserOTFCountsVO(userOTFCountsVO);
        actualO2cApprovalTxnDetailsResponseVO.setValidatePaymentInstruments(true);
        actualO2cApprovalTxnDetailsResponseVO.setVomsActiveMrp("Voms Active Mrp");
        actualO2cApprovalTxnDetailsResponseVO.setVomsCategoryList(new ArrayList());
        actualO2cApprovalTxnDetailsResponseVO.setVomsProductList(new ArrayList());
        actualO2cApprovalTxnDetailsResponseVO.setVoucherType("Voucher Type");
        actualO2cApprovalTxnDetailsResponseVO.setVoucherTypeDesc("Voucher Type Desc");
        actualO2cApprovalTxnDetailsResponseVO.setVoucherTypeList(new ArrayList<>());
        actualO2cApprovalTxnDetailsResponseVO.toString();
        assertEquals("42 Main St", actualO2cApprovalTxnDetailsResponseVO.getAddress());
        assertEquals("All Order", actualO2cApprovalTxnDetailsResponseVO.getAllOrder());
        assertEquals("All User", actualO2cApprovalTxnDetailsResponseVO.getAllUser());
        assertEquals(42, actualO2cApprovalTxnDetailsResponseVO.getApprovalLevel());
        assertEquals("Approve1 Remark", actualO2cApprovalTxnDetailsResponseVO.getApprove1Remark());
        assertEquals("Approve2 Remark", actualO2cApprovalTxnDetailsResponseVO.getApprove2Remark());
        assertEquals("Approve3 Remark", actualO2cApprovalTxnDetailsResponseVO.getApprove3Remark());
        assertEquals("Category Code", actualO2cApprovalTxnDetailsResponseVO.getCategoryCode());
        assertEquals("Category Code For User Code", actualO2cApprovalTxnDetailsResponseVO.getCategoryCodeForUserCode());
        assertSame(categoryList, actualO2cApprovalTxnDetailsResponseVO.getCategoryList());
        assertEquals("Category Name", actualO2cApprovalTxnDetailsResponseVO.getCategoryName());
        assertEquals("Channel Owner Category", actualO2cApprovalTxnDetailsResponseVO.getChannelOwnerCategory());
        assertEquals("Channel Owner Category Desc", actualO2cApprovalTxnDetailsResponseVO.getChannelOwnerCategoryDesc());
        assertEquals("Channel Owner Category User ID",
                actualO2cApprovalTxnDetailsResponseVO.getChannelOwnerCategoryUserID());
        assertEquals("janedoe", actualO2cApprovalTxnDetailsResponseVO.getChannelOwnerCategoryUserName());
        assertSame(channelTransferList, actualO2cApprovalTxnDetailsResponseVO.getChannelTransferList());
        assertSame(channelTransferVO, actualO2cApprovalTxnDetailsResponseVO.getChannelTransferVO());
        assertEquals("Channel User Status", actualO2cApprovalTxnDetailsResponseVO.getChannelUserStatus());
        assertEquals("Commission Profile ID", actualO2cApprovalTxnDetailsResponseVO.getCommissionProfileID());
        assertEquals("foo.txt", actualO2cApprovalTxnDetailsResponseVO.getCommissionProfileName());
        assertEquals("1.0.2", actualO2cApprovalTxnDetailsResponseVO.getCommissionProfileVersion());
        assertEquals("Commission Quantity", actualO2cApprovalTxnDetailsResponseVO.getCommissionQuantity());
        assertEquals("Current Approval Level", actualO2cApprovalTxnDetailsResponseVO.getCurrentApprovalLevel());
        assertEquals("Distributor Mode", actualO2cApprovalTxnDetailsResponseVO.getDistributorMode());
        assertEquals("Distributor Mode Desc", actualO2cApprovalTxnDetailsResponseVO.getDistributorModeDesc());
        assertSame(distributorModeList, actualO2cApprovalTxnDetailsResponseVO.getDistributorModeList());
        assertEquals("42", actualO2cApprovalTxnDetailsResponseVO.getDistributorModeValue());
        assertEquals("Distributor Name", actualO2cApprovalTxnDetailsResponseVO.getDistributorName());
        assertEquals("Domain Code", actualO2cApprovalTxnDetailsResponseVO.getDomainCode());
        assertSame(domainList, actualO2cApprovalTxnDetailsResponseVO.getDomainList());
        assertEquals("Domain Name", actualO2cApprovalTxnDetailsResponseVO.getDomainName());
        assertEquals("Domain Name For User Code", actualO2cApprovalTxnDetailsResponseVO.getDomainNameForUserCode());
        assertEquals("Domain Type Code", actualO2cApprovalTxnDetailsResponseVO.getDomainTypeCode());
        assertEquals("Dual Commission Type", actualO2cApprovalTxnDetailsResponseVO.getDualCommissionType());
        assertEquals("Erp Code", actualO2cApprovalTxnDetailsResponseVO.getErpCode());
        assertSame(errorList, actualO2cApprovalTxnDetailsResponseVO.getErrorList());
        assertEquals("2020-03-01", actualO2cApprovalTxnDetailsResponseVO.getExternalTxnDate());
        assertEquals("External Txn Exist", actualO2cApprovalTxnDetailsResponseVO.getExternalTxnExist());
        assertEquals("External Txn Mandatory", actualO2cApprovalTxnDetailsResponseVO.getExternalTxnMandatory());
        assertEquals("External Txn Num", actualO2cApprovalTxnDetailsResponseVO.getExternalTxnNum());
        assertEquals("First Approval Limit", actualO2cApprovalTxnDetailsResponseVO.getFirstApprovalLimit());
        assertEquals("First Level Approved Quantity",
                actualO2cApprovalTxnDetailsResponseVO.getFirstLevelApprovedQuantity());
        assertEquals("Garde Desc", actualO2cApprovalTxnDetailsResponseVO.getGardeDesc());
        assertEquals("Geo Domain Code For User", actualO2cApprovalTxnDetailsResponseVO.getGeoDomainCodeForUser());
        assertEquals("Geo Domain Name For User", actualO2cApprovalTxnDetailsResponseVO.getGeoDomainNameForUser());
        assertEquals("Geographic Domain Code", actualO2cApprovalTxnDetailsResponseVO.getGeographicDomainCode());
        assertSame(geographicDomainList, actualO2cApprovalTxnDetailsResponseVO.getGeographicDomainList());
        assertEquals("Geographic Domain Name", actualO2cApprovalTxnDetailsResponseVO.getGeographicDomainName());
    }
}

