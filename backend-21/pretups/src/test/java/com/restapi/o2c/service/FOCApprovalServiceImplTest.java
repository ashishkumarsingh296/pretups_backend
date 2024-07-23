package com.restapi.o2c.service;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.security.CustomResponseWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;

@ContextConfiguration(classes = {FOCApprovalServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class FOCApprovalServiceImplTest {
    @Autowired
    private FOCApprovalServiceImpl fOCApprovalServiceImpl;

    /**
     * Method under test: {@link FOCApprovalServiceImpl#detailViewFOCApproval(ApprovalVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testDetailViewFOCApproval() throws Exception {
        com.btsl.util.JUnitConfig.init();

        ApprovalVO theForm = new ApprovalVO();
        theForm.setAddress("42 Main St");
        theForm.setApprove1Remark("Approve1 Remark");
        theForm.setApprove2Remark("Approve2 Remark");
        theForm.setApprove3Remark("Approve3 Remark");
        theForm.setCategoryCode("Category Code");
        theForm.setCategoryCodeDesc("Category Code Desc");
        theForm.setCategoryList(new ArrayList());
        theForm.setChannelTransferVO(ChannelTransferVO.getInstance());
        theForm.setChannelUserID("Channel User ID");
        theForm.setChannelUserName("janedoe");
        theForm.setChannelUserStatus("Channel User Status");
        theForm.setCurrentDate("2020-03-01");
        theForm.setDefaultLang("Default Lang");
        theForm.setDomainCode("Domain Code");
        theForm.setDomainCodeDesc("Domain Code Desc");
        theForm.setDomainList(new ArrayList());
        theForm.setDomainTypeCode("Domain Type Code");
        theForm.setErpCode("Erp Code");
        theForm.setExternalTxnDate("2020-03-01");
        theForm.setExternalTxnExist("External Txn Exist");
        theForm.setExternalTxnMandatory("External Txn Mandatory");
        theForm.setExternalTxnNum("External Txn Num");
        theForm.setFocOrderApprovalLevel(42);
        theForm.setGeoDomainCodeDescForUser("Geo Domain Code Desc For User");
        theForm.setGeographicalDomainCode("Geographical Domain Code");
        theForm.setGeographicalDomainCodeDesc("Geographical Domain Code Desc");
        theForm.setGeographicalDomainList(new ArrayList());
        theForm.setMultiWallet(true);
        theForm.setNetCommissionQuantity("Net Commission Quantity");
        theForm.setNetworkCode("Network Code");
        theForm.setNetworkCodeDesc("Network Code Desc");
        theForm.setProductListWithTaxes(new ArrayList());
        theForm.setProductQuantity("Product Quantity");
        theForm.setProductTypeCode("Product Type Code");
        theForm.setProductTypeCodeDesc("Product Type Code Desc");
        theForm.setProductTypeList(new ArrayList());
        theForm.setReceiverCrQuantity("Receiver Cr Quantity");
        theForm.setReference("Reference");
        theForm.setRefrenceNum("Refrence Num");
        theForm.setRemarks("Remarks");
        theForm.setRequestType("Request Type");
        theForm.setSearchCriteria("Search Criteria");
        theForm.setSearchUserID("Search User ID");
        theForm.setSearchUserName("janedoe");
        theForm.setSecondLang("Second Lang");
        theForm.setSenderDrQuantity("Sender Dr Quantity");
        theForm.setSessionDomainCode("Session Domain Code");
        theForm.setStatus("Status");
        theForm.setToPrimaryMSISDN("To Primary MSISDN");
        theForm.setTotalMRP("Total MRP");
        theForm.setTotalReqQty("Total Req Qty");
        theForm.setTotalStock("Total Stock");
        theForm.setTotalTax1("Total Tax1");
        theForm.setTotalTax2("Total Tax2");
        theForm.setTotalTransferedAmount("10");
        theForm.setTransferCategory("Transfer Category");
        theForm.setTransferDateAsString("2020-03-01");
        theForm.setTransferItemList(new ArrayList());
        theForm.setTransferNumber("42");
        theForm.setUserCode("User Code");
        theForm.setUserCommProfileName("foo.txt");
        theForm.setUserCommProfileSetID("User Comm Profile Set ID");
        theForm.setUserCommProfileSetVersion("1.0.2");
        theForm.setUserGradeName("User Grade Name");
        theForm.setUserID("User ID");
        theForm.setUserList(new ArrayList());
        theForm.setUserMsisdn("User Msisdn");
        theForm.setUserName("janedoe");
        theForm.setUserTransferProfileCode("User Transfer Profile Code");
        theForm.setUserTransferProfileName("foo.txt");
        theForm.setWalletCode("Wallet Code");
        theForm.setWalletTypeList(new ArrayList<>());
        theForm.setWalletTypeWithWalletCode("Wallet Type With Wallet Code");
        fOCApprovalServiceImpl.detailViewFOCApproval(theForm);
    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#detailViewFOCApproval(ApprovalVO)}
     */
    @Test
    public void testDetailViewFOCApproval2() throws Exception {
        ApprovalVO theForm = mock(ApprovalVO.class);
        when(theForm.getExternalTxnExist()).thenReturn("External Txn Exist");
        when(theForm.getExternalTxnNum()).thenReturn("External Txn Num");
        when(theForm.getStatus()).thenReturn("Status");
        doNothing().when(theForm).setAddress(Mockito.<String>any());
        doNothing().when(theForm).setApprove1Remark(Mockito.<String>any());
        doNothing().when(theForm).setApprove2Remark(Mockito.<String>any());
        doNothing().when(theForm).setApprove3Remark(Mockito.<String>any());
        doNothing().when(theForm).setCategoryCode(Mockito.<String>any());
        doNothing().when(theForm).setCategoryCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setCategoryList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setChannelTransferVO(Mockito.<ChannelTransferVO>any());
        doNothing().when(theForm).setChannelUserID(Mockito.<String>any());
        doNothing().when(theForm).setChannelUserName(Mockito.<String>any());
        doNothing().when(theForm).setChannelUserStatus(Mockito.<String>any());
        doNothing().when(theForm).setCurrentDate(Mockito.<String>any());
        doNothing().when(theForm).setDefaultLang(Mockito.<String>any());
        doNothing().when(theForm).setDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setDomainCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setDomainList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setDomainTypeCode(Mockito.<String>any());
        doNothing().when(theForm).setErpCode(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnDate(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnExist(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnMandatory(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnNum(Mockito.<String>any());
        doNothing().when(theForm).setFocOrderApprovalLevel(anyInt());
        doNothing().when(theForm).setGeoDomainCodeDescForUser(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setMultiWallet(anyBoolean());
        doNothing().when(theForm).setNetCommissionQuantity(Mockito.<String>any());
        doNothing().when(theForm).setNetworkCode(Mockito.<String>any());
        doNothing().when(theForm).setNetworkCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setProductListWithTaxes(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setProductQuantity(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeCode(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setReceiverCrQuantity(Mockito.<String>any());
        doNothing().when(theForm).setReference(Mockito.<String>any());
        doNothing().when(theForm).setRefrenceNum(Mockito.<String>any());
        doNothing().when(theForm).setRemarks(Mockito.<String>any());
        doNothing().when(theForm).setRequestType(Mockito.<String>any());
        doNothing().when(theForm).setSearchCriteria(Mockito.<String>any());
        doNothing().when(theForm).setSearchUserID(Mockito.<String>any());
        doNothing().when(theForm).setSearchUserName(Mockito.<String>any());
        doNothing().when(theForm).setSecondLang(Mockito.<String>any());
        doNothing().when(theForm).setSenderDrQuantity(Mockito.<String>any());
        doNothing().when(theForm).setSessionDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setStatus(Mockito.<String>any());
        doNothing().when(theForm).setToPrimaryMSISDN(Mockito.<String>any());
        doNothing().when(theForm).setTotalMRP(Mockito.<String>any());
        doNothing().when(theForm).setTotalReqQty(Mockito.<String>any());
        doNothing().when(theForm).setTotalStock(Mockito.<String>any());
        doNothing().when(theForm).setTotalTax1(Mockito.<String>any());
        doNothing().when(theForm).setTotalTax2(Mockito.<String>any());
        doNothing().when(theForm).setTotalTransferedAmount(Mockito.<String>any());
        doNothing().when(theForm).setTransferCategory(Mockito.<String>any());
        doNothing().when(theForm).setTransferDateAsString(Mockito.<String>any());
        doNothing().when(theForm).setTransferItemList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setTransferNumber(Mockito.<String>any());
        doNothing().when(theForm).setUserCode(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileName(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileSetID(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileSetVersion(Mockito.<String>any());
        doNothing().when(theForm).setUserGradeName(Mockito.<String>any());
        doNothing().when(theForm).setUserID(Mockito.<String>any());
        doNothing().when(theForm).setUserList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setUserMsisdn(Mockito.<String>any());
        doNothing().when(theForm).setUserName(Mockito.<String>any());
        doNothing().when(theForm).setUserTransferProfileCode(Mockito.<String>any());
        doNothing().when(theForm).setUserTransferProfileName(Mockito.<String>any());
        doNothing().when(theForm).setWalletCode(Mockito.<String>any());
        doNothing().when(theForm).setWalletTypeList(Mockito.<List<ListValueVO>>any());
        doNothing().when(theForm).setWalletTypeWithWalletCode(Mockito.<String>any());
        theForm.setAddress("42 Main St");
        theForm.setApprove1Remark("Approve1 Remark");
        theForm.setApprove2Remark("Approve2 Remark");
        theForm.setApprove3Remark("Approve3 Remark");
        theForm.setCategoryCode("Category Code");
        theForm.setCategoryCodeDesc("Category Code Desc");
        theForm.setCategoryList(new ArrayList());
        theForm.setChannelTransferVO(ChannelTransferVO.getInstance());
        theForm.setChannelUserID("Channel User ID");
        theForm.setChannelUserName("janedoe");
        theForm.setChannelUserStatus("Channel User Status");
        theForm.setCurrentDate("2020-03-01");
        theForm.setDefaultLang("Default Lang");
        theForm.setDomainCode("Domain Code");
        theForm.setDomainCodeDesc("Domain Code Desc");
        theForm.setDomainList(new ArrayList());
        theForm.setDomainTypeCode("Domain Type Code");
        theForm.setErpCode("Erp Code");
        theForm.setExternalTxnDate("2020-03-01");
        theForm.setExternalTxnExist("External Txn Exist");
        theForm.setExternalTxnMandatory("External Txn Mandatory");
        theForm.setExternalTxnNum("External Txn Num");
        theForm.setFocOrderApprovalLevel(42);
        theForm.setGeoDomainCodeDescForUser("Geo Domain Code Desc For User");
        theForm.setGeographicalDomainCode("Geographical Domain Code");
        theForm.setGeographicalDomainCodeDesc("Geographical Domain Code Desc");
        theForm.setGeographicalDomainList(new ArrayList());
        theForm.setMultiWallet(true);
        theForm.setNetCommissionQuantity("Net Commission Quantity");
        theForm.setNetworkCode("Network Code");
        theForm.setNetworkCodeDesc("Network Code Desc");
        theForm.setProductListWithTaxes(new ArrayList());
        theForm.setProductQuantity("Product Quantity");
        theForm.setProductTypeCode("Product Type Code");
        theForm.setProductTypeCodeDesc("Product Type Code Desc");
        theForm.setProductTypeList(new ArrayList());
        theForm.setReceiverCrQuantity("Receiver Cr Quantity");
        theForm.setReference("Reference");
        theForm.setRefrenceNum("Refrence Num");
        theForm.setRemarks("Remarks");
        theForm.setRequestType("Request Type");
        theForm.setSearchCriteria("Search Criteria");
        theForm.setSearchUserID("Search User ID");
        theForm.setSearchUserName("janedoe");
        theForm.setSecondLang("Second Lang");
        theForm.setSenderDrQuantity("Sender Dr Quantity");
        theForm.setSessionDomainCode("Session Domain Code");
        theForm.setStatus("Status");
        theForm.setToPrimaryMSISDN("To Primary MSISDN");
        theForm.setTotalMRP("Total MRP");
        theForm.setTotalReqQty("Total Req Qty");
        theForm.setTotalStock("Total Stock");
        theForm.setTotalTax1("Total Tax1");
        theForm.setTotalTax2("Total Tax2");
        theForm.setTotalTransferedAmount("10");
        theForm.setTransferCategory("Transfer Category");
        theForm.setTransferDateAsString("2020-03-01");
        theForm.setTransferItemList(new ArrayList());
        theForm.setTransferNumber("42");
        theForm.setUserCode("User Code");
        theForm.setUserCommProfileName("foo.txt");
        theForm.setUserCommProfileSetID("User Comm Profile Set ID");
        theForm.setUserCommProfileSetVersion("1.0.2");
        theForm.setUserGradeName("User Grade Name");
        theForm.setUserID("User ID");
        theForm.setUserList(new ArrayList());
        theForm.setUserMsisdn("User Msisdn");
        theForm.setUserName("janedoe");
        theForm.setUserTransferProfileCode("User Transfer Profile Code");
        theForm.setUserTransferProfileName("foo.txt");
        theForm.setWalletCode("Wallet Code");
        theForm.setWalletTypeList(new ArrayList<>());
        theForm.setWalletTypeWithWalletCode("Wallet Type With Wallet Code");
        fOCApprovalServiceImpl.detailViewFOCApproval(theForm);
        verify(theForm).getExternalTxnExist();
        verify(theForm).getExternalTxnNum();
        verify(theForm).getStatus();
        verify(theForm).setAddress(Mockito.<String>any());
        verify(theForm).setApprove1Remark(Mockito.<String>any());
        verify(theForm).setApprove2Remark(Mockito.<String>any());
        verify(theForm).setApprove3Remark(Mockito.<String>any());
        verify(theForm).setCategoryCode(Mockito.<String>any());
        verify(theForm).setCategoryCodeDesc(Mockito.<String>any());
        verify(theForm).setCategoryList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setChannelTransferVO(Mockito.<ChannelTransferVO>any());
        verify(theForm).setChannelUserID(Mockito.<String>any());
        verify(theForm).setChannelUserName(Mockito.<String>any());
        verify(theForm).setChannelUserStatus(Mockito.<String>any());
        verify(theForm).setCurrentDate(Mockito.<String>any());
        verify(theForm).setDefaultLang(Mockito.<String>any());
        verify(theForm).setDomainCode(Mockito.<String>any());
        verify(theForm).setDomainCodeDesc(Mockito.<String>any());
        verify(theForm).setDomainList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setDomainTypeCode(Mockito.<String>any());
        verify(theForm).setErpCode(Mockito.<String>any());
        verify(theForm).setExternalTxnDate(Mockito.<String>any());
        verify(theForm).setExternalTxnExist(Mockito.<String>any());
        verify(theForm).setExternalTxnMandatory(Mockito.<String>any());
        verify(theForm).setExternalTxnNum(Mockito.<String>any());
        verify(theForm).setFocOrderApprovalLevel(anyInt());
        verify(theForm).setGeoDomainCodeDescForUser(Mockito.<String>any());
        verify(theForm).setGeographicalDomainCode(Mockito.<String>any());
        verify(theForm).setGeographicalDomainCodeDesc(Mockito.<String>any());
        verify(theForm).setGeographicalDomainList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setMultiWallet(anyBoolean());
        verify(theForm).setNetCommissionQuantity(Mockito.<String>any());
        verify(theForm).setNetworkCode(Mockito.<String>any());
        verify(theForm).setNetworkCodeDesc(Mockito.<String>any());
        verify(theForm).setProductListWithTaxes(Mockito.<ArrayList<Object>>any());
        verify(theForm).setProductQuantity(Mockito.<String>any());
        verify(theForm).setProductTypeCode(Mockito.<String>any());
        verify(theForm).setProductTypeCodeDesc(Mockito.<String>any());
        verify(theForm).setProductTypeList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setReceiverCrQuantity(Mockito.<String>any());
        verify(theForm).setReference(Mockito.<String>any());
        verify(theForm).setRefrenceNum(Mockito.<String>any());
        verify(theForm).setRemarks(Mockito.<String>any());
        verify(theForm).setRequestType(Mockito.<String>any());
        verify(theForm).setSearchCriteria(Mockito.<String>any());
        verify(theForm).setSearchUserID(Mockito.<String>any());
        verify(theForm).setSearchUserName(Mockito.<String>any());
        verify(theForm).setSecondLang(Mockito.<String>any());
        verify(theForm).setSenderDrQuantity(Mockito.<String>any());
        verify(theForm).setSessionDomainCode(Mockito.<String>any());
        verify(theForm).setStatus(Mockito.<String>any());
        verify(theForm).setToPrimaryMSISDN(Mockito.<String>any());
        verify(theForm).setTotalMRP(Mockito.<String>any());
        verify(theForm).setTotalReqQty(Mockito.<String>any());
        verify(theForm).setTotalStock(Mockito.<String>any());
        verify(theForm).setTotalTax1(Mockito.<String>any());
        verify(theForm).setTotalTax2(Mockito.<String>any());
        verify(theForm).setTotalTransferedAmount(Mockito.<String>any());
        verify(theForm).setTransferCategory(Mockito.<String>any());
        verify(theForm).setTransferDateAsString(Mockito.<String>any());
        verify(theForm).setTransferItemList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setTransferNumber(Mockito.<String>any());
        verify(theForm).setUserCode(Mockito.<String>any());
        verify(theForm).setUserCommProfileName(Mockito.<String>any());
        verify(theForm).setUserCommProfileSetID(Mockito.<String>any());
        verify(theForm).setUserCommProfileSetVersion(Mockito.<String>any());
        verify(theForm).setUserGradeName(Mockito.<String>any());
        verify(theForm).setUserID(Mockito.<String>any());
        verify(theForm).setUserList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setUserMsisdn(Mockito.<String>any());
        verify(theForm).setUserName(Mockito.<String>any());
        verify(theForm).setUserTransferProfileCode(Mockito.<String>any());
        verify(theForm).setUserTransferProfileName(Mockito.<String>any());
        verify(theForm).setWalletCode(Mockito.<String>any());
        verify(theForm).setWalletTypeList(Mockito.<List<ListValueVO>>any());
        verify(theForm).setWalletTypeWithWalletCode(Mockito.<String>any());
    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#detailViewFOCApproval(ApprovalVO)}
     */
    @Test
    public void testDetailViewFOCApproval3() throws Exception {
        ApprovalVO theForm = mock(ApprovalVO.class);
        when(theForm.getExternalTxnExist()).thenReturn("Y");
        when(theForm.getExternalTxnNum()).thenReturn("External Txn Num");
        when(theForm.getStatus()).thenReturn("Status");
        doNothing().when(theForm).setAddress(Mockito.<String>any());
        doNothing().when(theForm).setApprove1Remark(Mockito.<String>any());
        doNothing().when(theForm).setApprove2Remark(Mockito.<String>any());
        doNothing().when(theForm).setApprove3Remark(Mockito.<String>any());
        doNothing().when(theForm).setCategoryCode(Mockito.<String>any());
        doNothing().when(theForm).setCategoryCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setCategoryList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setChannelTransferVO(Mockito.<ChannelTransferVO>any());
        doNothing().when(theForm).setChannelUserID(Mockito.<String>any());
        doNothing().when(theForm).setChannelUserName(Mockito.<String>any());
        doNothing().when(theForm).setChannelUserStatus(Mockito.<String>any());
        doNothing().when(theForm).setCurrentDate(Mockito.<String>any());
        doNothing().when(theForm).setDefaultLang(Mockito.<String>any());
        doNothing().when(theForm).setDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setDomainCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setDomainList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setDomainTypeCode(Mockito.<String>any());
        doNothing().when(theForm).setErpCode(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnDate(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnExist(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnMandatory(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnNum(Mockito.<String>any());
        doNothing().when(theForm).setFocOrderApprovalLevel(anyInt());
        doNothing().when(theForm).setGeoDomainCodeDescForUser(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setMultiWallet(anyBoolean());
        doNothing().when(theForm).setNetCommissionQuantity(Mockito.<String>any());
        doNothing().when(theForm).setNetworkCode(Mockito.<String>any());
        doNothing().when(theForm).setNetworkCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setProductListWithTaxes(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setProductQuantity(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeCode(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setReceiverCrQuantity(Mockito.<String>any());
        doNothing().when(theForm).setReference(Mockito.<String>any());
        doNothing().when(theForm).setRefrenceNum(Mockito.<String>any());
        doNothing().when(theForm).setRemarks(Mockito.<String>any());
        doNothing().when(theForm).setRequestType(Mockito.<String>any());
        doNothing().when(theForm).setSearchCriteria(Mockito.<String>any());
        doNothing().when(theForm).setSearchUserID(Mockito.<String>any());
        doNothing().when(theForm).setSearchUserName(Mockito.<String>any());
        doNothing().when(theForm).setSecondLang(Mockito.<String>any());
        doNothing().when(theForm).setSenderDrQuantity(Mockito.<String>any());
        doNothing().when(theForm).setSessionDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setStatus(Mockito.<String>any());
        doNothing().when(theForm).setToPrimaryMSISDN(Mockito.<String>any());
        doNothing().when(theForm).setTotalMRP(Mockito.<String>any());
        doNothing().when(theForm).setTotalReqQty(Mockito.<String>any());
        doNothing().when(theForm).setTotalStock(Mockito.<String>any());
        doNothing().when(theForm).setTotalTax1(Mockito.<String>any());
        doNothing().when(theForm).setTotalTax2(Mockito.<String>any());
        doNothing().when(theForm).setTotalTransferedAmount(Mockito.<String>any());
        doNothing().when(theForm).setTransferCategory(Mockito.<String>any());
        doNothing().when(theForm).setTransferDateAsString(Mockito.<String>any());
        doNothing().when(theForm).setTransferItemList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setTransferNumber(Mockito.<String>any());
        doNothing().when(theForm).setUserCode(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileName(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileSetID(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileSetVersion(Mockito.<String>any());
        doNothing().when(theForm).setUserGradeName(Mockito.<String>any());
        doNothing().when(theForm).setUserID(Mockito.<String>any());
        doNothing().when(theForm).setUserList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setUserMsisdn(Mockito.<String>any());
        doNothing().when(theForm).setUserName(Mockito.<String>any());
        doNothing().when(theForm).setUserTransferProfileCode(Mockito.<String>any());
        doNothing().when(theForm).setUserTransferProfileName(Mockito.<String>any());
        doNothing().when(theForm).setWalletCode(Mockito.<String>any());
        doNothing().when(theForm).setWalletTypeList(Mockito.<List<ListValueVO>>any());
        doNothing().when(theForm).setWalletTypeWithWalletCode(Mockito.<String>any());
        theForm.setAddress("42 Main St");
        theForm.setApprove1Remark("Approve1 Remark");
        theForm.setApprove2Remark("Approve2 Remark");
        theForm.setApprove3Remark("Approve3 Remark");
        theForm.setCategoryCode("Category Code");
        theForm.setCategoryCodeDesc("Category Code Desc");
        theForm.setCategoryList(new ArrayList());
        theForm.setChannelTransferVO(ChannelTransferVO.getInstance());
        theForm.setChannelUserID("Channel User ID");
        theForm.setChannelUserName("janedoe");
        theForm.setChannelUserStatus("Channel User Status");
        theForm.setCurrentDate("2020-03-01");
        theForm.setDefaultLang("Default Lang");
        theForm.setDomainCode("Domain Code");
        theForm.setDomainCodeDesc("Domain Code Desc");
        theForm.setDomainList(new ArrayList());
        theForm.setDomainTypeCode("Domain Type Code");
        theForm.setErpCode("Erp Code");
        theForm.setExternalTxnDate("2020-03-01");
        theForm.setExternalTxnExist("External Txn Exist");
        theForm.setExternalTxnMandatory("External Txn Mandatory");
        theForm.setExternalTxnNum("External Txn Num");
        theForm.setFocOrderApprovalLevel(42);
        theForm.setGeoDomainCodeDescForUser("Geo Domain Code Desc For User");
        theForm.setGeographicalDomainCode("Geographical Domain Code");
        theForm.setGeographicalDomainCodeDesc("Geographical Domain Code Desc");
        theForm.setGeographicalDomainList(new ArrayList());
        theForm.setMultiWallet(true);
        theForm.setNetCommissionQuantity("Net Commission Quantity");
        theForm.setNetworkCode("Network Code");
        theForm.setNetworkCodeDesc("Network Code Desc");
        theForm.setProductListWithTaxes(new ArrayList());
        theForm.setProductQuantity("Product Quantity");
        theForm.setProductTypeCode("Product Type Code");
        theForm.setProductTypeCodeDesc("Product Type Code Desc");
        theForm.setProductTypeList(new ArrayList());
        theForm.setReceiverCrQuantity("Receiver Cr Quantity");
        theForm.setReference("Reference");
        theForm.setRefrenceNum("Refrence Num");
        theForm.setRemarks("Remarks");
        theForm.setRequestType("Request Type");
        theForm.setSearchCriteria("Search Criteria");
        theForm.setSearchUserID("Search User ID");
        theForm.setSearchUserName("janedoe");
        theForm.setSecondLang("Second Lang");
        theForm.setSenderDrQuantity("Sender Dr Quantity");
        theForm.setSessionDomainCode("Session Domain Code");
        theForm.setStatus("Status");
        theForm.setToPrimaryMSISDN("To Primary MSISDN");
        theForm.setTotalMRP("Total MRP");
        theForm.setTotalReqQty("Total Req Qty");
        theForm.setTotalStock("Total Stock");
        theForm.setTotalTax1("Total Tax1");
        theForm.setTotalTax2("Total Tax2");
        theForm.setTotalTransferedAmount("10");
        theForm.setTransferCategory("Transfer Category");
        theForm.setTransferDateAsString("2020-03-01");
        theForm.setTransferItemList(new ArrayList());
        theForm.setTransferNumber("42");
        theForm.setUserCode("User Code");
        theForm.setUserCommProfileName("foo.txt");
        theForm.setUserCommProfileSetID("User Comm Profile Set ID");
        theForm.setUserCommProfileSetVersion("1.0.2");
        theForm.setUserGradeName("User Grade Name");
        theForm.setUserID("User ID");
        theForm.setUserList(new ArrayList());
        theForm.setUserMsisdn("User Msisdn");
        theForm.setUserName("janedoe");
        theForm.setUserTransferProfileCode("User Transfer Profile Code");
        theForm.setUserTransferProfileName("foo.txt");
        theForm.setWalletCode("Wallet Code");
        theForm.setWalletTypeList(new ArrayList<>());
        theForm.setWalletTypeWithWalletCode("Wallet Type With Wallet Code");
        fOCApprovalServiceImpl.detailViewFOCApproval(theForm);
        verify(theForm).getExternalTxnExist();
        verify(theForm).getExternalTxnNum();
        verify(theForm).getStatus();
        verify(theForm).setAddress(Mockito.<String>any());
        verify(theForm).setApprove1Remark(Mockito.<String>any());
        verify(theForm).setApprove2Remark(Mockito.<String>any());
        verify(theForm).setApprove3Remark(Mockito.<String>any());
        verify(theForm).setCategoryCode(Mockito.<String>any());
        verify(theForm).setCategoryCodeDesc(Mockito.<String>any());
        verify(theForm).setCategoryList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setChannelTransferVO(Mockito.<ChannelTransferVO>any());
        verify(theForm).setChannelUserID(Mockito.<String>any());
        verify(theForm).setChannelUserName(Mockito.<String>any());
        verify(theForm).setChannelUserStatus(Mockito.<String>any());
        verify(theForm).setCurrentDate(Mockito.<String>any());
        verify(theForm).setDefaultLang(Mockito.<String>any());
        verify(theForm).setDomainCode(Mockito.<String>any());
        verify(theForm).setDomainCodeDesc(Mockito.<String>any());
        verify(theForm).setDomainList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setDomainTypeCode(Mockito.<String>any());
        verify(theForm).setErpCode(Mockito.<String>any());
        verify(theForm).setExternalTxnDate(Mockito.<String>any());
        verify(theForm).setExternalTxnExist(Mockito.<String>any());
        verify(theForm).setExternalTxnMandatory(Mockito.<String>any());
        verify(theForm).setExternalTxnNum(Mockito.<String>any());
        verify(theForm).setFocOrderApprovalLevel(anyInt());
        verify(theForm).setGeoDomainCodeDescForUser(Mockito.<String>any());
        verify(theForm).setGeographicalDomainCode(Mockito.<String>any());
        verify(theForm).setGeographicalDomainCodeDesc(Mockito.<String>any());
        verify(theForm).setGeographicalDomainList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setMultiWallet(anyBoolean());
        verify(theForm).setNetCommissionQuantity(Mockito.<String>any());
        verify(theForm).setNetworkCode(Mockito.<String>any());
        verify(theForm).setNetworkCodeDesc(Mockito.<String>any());
        verify(theForm).setProductListWithTaxes(Mockito.<ArrayList<Object>>any());
        verify(theForm).setProductQuantity(Mockito.<String>any());
        verify(theForm).setProductTypeCode(Mockito.<String>any());
        verify(theForm).setProductTypeCodeDesc(Mockito.<String>any());
        verify(theForm).setProductTypeList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setReceiverCrQuantity(Mockito.<String>any());
        verify(theForm).setReference(Mockito.<String>any());
        verify(theForm).setRefrenceNum(Mockito.<String>any());
        verify(theForm).setRemarks(Mockito.<String>any());
        verify(theForm).setRequestType(Mockito.<String>any());
        verify(theForm).setSearchCriteria(Mockito.<String>any());
        verify(theForm).setSearchUserID(Mockito.<String>any());
        verify(theForm).setSearchUserName(Mockito.<String>any());
        verify(theForm).setSecondLang(Mockito.<String>any());
        verify(theForm).setSenderDrQuantity(Mockito.<String>any());
        verify(theForm).setSessionDomainCode(Mockito.<String>any());
        verify(theForm).setStatus(Mockito.<String>any());
        verify(theForm).setToPrimaryMSISDN(Mockito.<String>any());
        verify(theForm).setTotalMRP(Mockito.<String>any());
        verify(theForm).setTotalReqQty(Mockito.<String>any());
        verify(theForm).setTotalStock(Mockito.<String>any());
        verify(theForm).setTotalTax1(Mockito.<String>any());
        verify(theForm).setTotalTax2(Mockito.<String>any());
        verify(theForm).setTotalTransferedAmount(Mockito.<String>any());
        verify(theForm).setTransferCategory(Mockito.<String>any());
        verify(theForm).setTransferDateAsString(Mockito.<String>any());
        verify(theForm).setTransferItemList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setTransferNumber(Mockito.<String>any());
        verify(theForm).setUserCode(Mockito.<String>any());
        verify(theForm).setUserCommProfileName(Mockito.<String>any());
        verify(theForm).setUserCommProfileSetID(Mockito.<String>any());
        verify(theForm).setUserCommProfileSetVersion(Mockito.<String>any());
        verify(theForm).setUserGradeName(Mockito.<String>any());
        verify(theForm).setUserID(Mockito.<String>any());
        verify(theForm).setUserList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setUserMsisdn(Mockito.<String>any());
        verify(theForm).setUserName(Mockito.<String>any());
        verify(theForm).setUserTransferProfileCode(Mockito.<String>any());
        verify(theForm).setUserTransferProfileName(Mockito.<String>any());
        verify(theForm).setWalletCode(Mockito.<String>any());
        verify(theForm).setWalletTypeList(Mockito.<List<ListValueVO>>any());
        verify(theForm).setWalletTypeWithWalletCode(Mockito.<String>any());
    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#detailViewFOCApproval(ApprovalVO)}
     */
    @Test
    public void testDetailViewFOCApproval4() throws Exception {
        ApprovalVO theForm = mock(ApprovalVO.class);
        when(theForm.getExternalTxnNum()).thenReturn(null);
        when(theForm.getStatus()).thenReturn("Status");
        doNothing().when(theForm).setAddress(Mockito.<String>any());
        doNothing().when(theForm).setApprove1Remark(Mockito.<String>any());
        doNothing().when(theForm).setApprove2Remark(Mockito.<String>any());
        doNothing().when(theForm).setApprove3Remark(Mockito.<String>any());
        doNothing().when(theForm).setCategoryCode(Mockito.<String>any());
        doNothing().when(theForm).setCategoryCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setCategoryList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setChannelTransferVO(Mockito.<ChannelTransferVO>any());
        doNothing().when(theForm).setChannelUserID(Mockito.<String>any());
        doNothing().when(theForm).setChannelUserName(Mockito.<String>any());
        doNothing().when(theForm).setChannelUserStatus(Mockito.<String>any());
        doNothing().when(theForm).setCurrentDate(Mockito.<String>any());
        doNothing().when(theForm).setDefaultLang(Mockito.<String>any());
        doNothing().when(theForm).setDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setDomainCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setDomainList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setDomainTypeCode(Mockito.<String>any());
        doNothing().when(theForm).setErpCode(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnDate(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnExist(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnMandatory(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnNum(Mockito.<String>any());
        doNothing().when(theForm).setFocOrderApprovalLevel(anyInt());
        doNothing().when(theForm).setGeoDomainCodeDescForUser(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setMultiWallet(anyBoolean());
        doNothing().when(theForm).setNetCommissionQuantity(Mockito.<String>any());
        doNothing().when(theForm).setNetworkCode(Mockito.<String>any());
        doNothing().when(theForm).setNetworkCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setProductListWithTaxes(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setProductQuantity(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeCode(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setReceiverCrQuantity(Mockito.<String>any());
        doNothing().when(theForm).setReference(Mockito.<String>any());
        doNothing().when(theForm).setRefrenceNum(Mockito.<String>any());
        doNothing().when(theForm).setRemarks(Mockito.<String>any());
        doNothing().when(theForm).setRequestType(Mockito.<String>any());
        doNothing().when(theForm).setSearchCriteria(Mockito.<String>any());
        doNothing().when(theForm).setSearchUserID(Mockito.<String>any());
        doNothing().when(theForm).setSearchUserName(Mockito.<String>any());
        doNothing().when(theForm).setSecondLang(Mockito.<String>any());
        doNothing().when(theForm).setSenderDrQuantity(Mockito.<String>any());
        doNothing().when(theForm).setSessionDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setStatus(Mockito.<String>any());
        doNothing().when(theForm).setToPrimaryMSISDN(Mockito.<String>any());
        doNothing().when(theForm).setTotalMRP(Mockito.<String>any());
        doNothing().when(theForm).setTotalReqQty(Mockito.<String>any());
        doNothing().when(theForm).setTotalStock(Mockito.<String>any());
        doNothing().when(theForm).setTotalTax1(Mockito.<String>any());
        doNothing().when(theForm).setTotalTax2(Mockito.<String>any());
        doNothing().when(theForm).setTotalTransferedAmount(Mockito.<String>any());
        doNothing().when(theForm).setTransferCategory(Mockito.<String>any());
        doNothing().when(theForm).setTransferDateAsString(Mockito.<String>any());
        doNothing().when(theForm).setTransferItemList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setTransferNumber(Mockito.<String>any());
        doNothing().when(theForm).setUserCode(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileName(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileSetID(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileSetVersion(Mockito.<String>any());
        doNothing().when(theForm).setUserGradeName(Mockito.<String>any());
        doNothing().when(theForm).setUserID(Mockito.<String>any());
        doNothing().when(theForm).setUserList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setUserMsisdn(Mockito.<String>any());
        doNothing().when(theForm).setUserName(Mockito.<String>any());
        doNothing().when(theForm).setUserTransferProfileCode(Mockito.<String>any());
        doNothing().when(theForm).setUserTransferProfileName(Mockito.<String>any());
        doNothing().when(theForm).setWalletCode(Mockito.<String>any());
        doNothing().when(theForm).setWalletTypeList(Mockito.<List<ListValueVO>>any());
        doNothing().when(theForm).setWalletTypeWithWalletCode(Mockito.<String>any());
        theForm.setAddress("42 Main St");
        theForm.setApprove1Remark("Approve1 Remark");
        theForm.setApprove2Remark("Approve2 Remark");
        theForm.setApprove3Remark("Approve3 Remark");
        theForm.setCategoryCode("Category Code");
        theForm.setCategoryCodeDesc("Category Code Desc");
        theForm.setCategoryList(new ArrayList());
        theForm.setChannelTransferVO(ChannelTransferVO.getInstance());
        theForm.setChannelUserID("Channel User ID");
        theForm.setChannelUserName("janedoe");
        theForm.setChannelUserStatus("Channel User Status");
        theForm.setCurrentDate("2020-03-01");
        theForm.setDefaultLang("Default Lang");
        theForm.setDomainCode("Domain Code");
        theForm.setDomainCodeDesc("Domain Code Desc");
        theForm.setDomainList(new ArrayList());
        theForm.setDomainTypeCode("Domain Type Code");
        theForm.setErpCode("Erp Code");
        theForm.setExternalTxnDate("2020-03-01");
        theForm.setExternalTxnExist("External Txn Exist");
        theForm.setExternalTxnMandatory("External Txn Mandatory");
        theForm.setExternalTxnNum("External Txn Num");
        theForm.setFocOrderApprovalLevel(42);
        theForm.setGeoDomainCodeDescForUser("Geo Domain Code Desc For User");
        theForm.setGeographicalDomainCode("Geographical Domain Code");
        theForm.setGeographicalDomainCodeDesc("Geographical Domain Code Desc");
        theForm.setGeographicalDomainList(new ArrayList());
        theForm.setMultiWallet(true);
        theForm.setNetCommissionQuantity("Net Commission Quantity");
        theForm.setNetworkCode("Network Code");
        theForm.setNetworkCodeDesc("Network Code Desc");
        theForm.setProductListWithTaxes(new ArrayList());
        theForm.setProductQuantity("Product Quantity");
        theForm.setProductTypeCode("Product Type Code");
        theForm.setProductTypeCodeDesc("Product Type Code Desc");
        theForm.setProductTypeList(new ArrayList());
        theForm.setReceiverCrQuantity("Receiver Cr Quantity");
        theForm.setReference("Reference");
        theForm.setRefrenceNum("Refrence Num");
        theForm.setRemarks("Remarks");
        theForm.setRequestType("Request Type");
        theForm.setSearchCriteria("Search Criteria");
        theForm.setSearchUserID("Search User ID");
        theForm.setSearchUserName("janedoe");
        theForm.setSecondLang("Second Lang");
        theForm.setSenderDrQuantity("Sender Dr Quantity");
        theForm.setSessionDomainCode("Session Domain Code");
        theForm.setStatus("Status");
        theForm.setToPrimaryMSISDN("To Primary MSISDN");
        theForm.setTotalMRP("Total MRP");
        theForm.setTotalReqQty("Total Req Qty");
        theForm.setTotalStock("Total Stock");
        theForm.setTotalTax1("Total Tax1");
        theForm.setTotalTax2("Total Tax2");
        theForm.setTotalTransferedAmount("10");
        theForm.setTransferCategory("Transfer Category");
        theForm.setTransferDateAsString("2020-03-01");
        theForm.setTransferItemList(new ArrayList());
        theForm.setTransferNumber("42");
        theForm.setUserCode("User Code");
        theForm.setUserCommProfileName("foo.txt");
        theForm.setUserCommProfileSetID("User Comm Profile Set ID");
        theForm.setUserCommProfileSetVersion("1.0.2");
        theForm.setUserGradeName("User Grade Name");
        theForm.setUserID("User ID");
        theForm.setUserList(new ArrayList());
        theForm.setUserMsisdn("User Msisdn");
        theForm.setUserName("janedoe");
        theForm.setUserTransferProfileCode("User Transfer Profile Code");
        theForm.setUserTransferProfileName("foo.txt");
        theForm.setWalletCode("Wallet Code");
        theForm.setWalletTypeList(new ArrayList<>());
        theForm.setWalletTypeWithWalletCode("Wallet Type With Wallet Code");
        fOCApprovalServiceImpl.detailViewFOCApproval(theForm);
        verify(theForm).getExternalTxnNum();
        verify(theForm).getStatus();
        verify(theForm).setAddress(Mockito.<String>any());
        verify(theForm).setApprove1Remark(Mockito.<String>any());
        verify(theForm).setApprove2Remark(Mockito.<String>any());
        verify(theForm).setApprove3Remark(Mockito.<String>any());
        verify(theForm).setCategoryCode(Mockito.<String>any());
        verify(theForm).setCategoryCodeDesc(Mockito.<String>any());
        verify(theForm).setCategoryList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setChannelTransferVO(Mockito.<ChannelTransferVO>any());
        verify(theForm).setChannelUserID(Mockito.<String>any());
        verify(theForm).setChannelUserName(Mockito.<String>any());
        verify(theForm).setChannelUserStatus(Mockito.<String>any());
        verify(theForm).setCurrentDate(Mockito.<String>any());
        verify(theForm).setDefaultLang(Mockito.<String>any());
        verify(theForm).setDomainCode(Mockito.<String>any());
        verify(theForm).setDomainCodeDesc(Mockito.<String>any());
        verify(theForm).setDomainList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setDomainTypeCode(Mockito.<String>any());
        verify(theForm).setErpCode(Mockito.<String>any());
        verify(theForm).setExternalTxnDate(Mockito.<String>any());
        verify(theForm).setExternalTxnExist(Mockito.<String>any());
        verify(theForm).setExternalTxnMandatory(Mockito.<String>any());
        verify(theForm).setExternalTxnNum(Mockito.<String>any());
        verify(theForm).setFocOrderApprovalLevel(anyInt());
        verify(theForm).setGeoDomainCodeDescForUser(Mockito.<String>any());
        verify(theForm).setGeographicalDomainCode(Mockito.<String>any());
        verify(theForm).setGeographicalDomainCodeDesc(Mockito.<String>any());
        verify(theForm).setGeographicalDomainList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setMultiWallet(anyBoolean());
        verify(theForm).setNetCommissionQuantity(Mockito.<String>any());
        verify(theForm).setNetworkCode(Mockito.<String>any());
        verify(theForm).setNetworkCodeDesc(Mockito.<String>any());
        verify(theForm).setProductListWithTaxes(Mockito.<ArrayList<Object>>any());
        verify(theForm).setProductQuantity(Mockito.<String>any());
        verify(theForm).setProductTypeCode(Mockito.<String>any());
        verify(theForm).setProductTypeCodeDesc(Mockito.<String>any());
        verify(theForm).setProductTypeList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setReceiverCrQuantity(Mockito.<String>any());
        verify(theForm).setReference(Mockito.<String>any());
        verify(theForm).setRefrenceNum(Mockito.<String>any());
        verify(theForm).setRemarks(Mockito.<String>any());
        verify(theForm).setRequestType(Mockito.<String>any());
        verify(theForm).setSearchCriteria(Mockito.<String>any());
        verify(theForm).setSearchUserID(Mockito.<String>any());
        verify(theForm).setSearchUserName(Mockito.<String>any());
        verify(theForm).setSecondLang(Mockito.<String>any());
        verify(theForm).setSenderDrQuantity(Mockito.<String>any());
        verify(theForm).setSessionDomainCode(Mockito.<String>any());
        verify(theForm).setStatus(Mockito.<String>any());
        verify(theForm).setToPrimaryMSISDN(Mockito.<String>any());
        verify(theForm).setTotalMRP(Mockito.<String>any());
        verify(theForm).setTotalReqQty(Mockito.<String>any());
        verify(theForm).setTotalStock(Mockito.<String>any());
        verify(theForm).setTotalTax1(Mockito.<String>any());
        verify(theForm).setTotalTax2(Mockito.<String>any());
        verify(theForm).setTotalTransferedAmount(Mockito.<String>any());
        verify(theForm).setTransferCategory(Mockito.<String>any());
        verify(theForm).setTransferDateAsString(Mockito.<String>any());
        verify(theForm).setTransferItemList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setTransferNumber(Mockito.<String>any());
        verify(theForm).setUserCode(Mockito.<String>any());
        verify(theForm).setUserCommProfileName(Mockito.<String>any());
        verify(theForm).setUserCommProfileSetID(Mockito.<String>any());
        verify(theForm).setUserCommProfileSetVersion(Mockito.<String>any());
        verify(theForm).setUserGradeName(Mockito.<String>any());
        verify(theForm).setUserID(Mockito.<String>any());
        verify(theForm).setUserList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setUserMsisdn(Mockito.<String>any());
        verify(theForm).setUserName(Mockito.<String>any());
        verify(theForm).setUserTransferProfileCode(Mockito.<String>any());
        verify(theForm).setUserTransferProfileName(Mockito.<String>any());
        verify(theForm).setWalletCode(Mockito.<String>any());
        verify(theForm).setWalletTypeList(Mockito.<List<ListValueVO>>any());
        verify(theForm).setWalletTypeWithWalletCode(Mockito.<String>any());
    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#detailViewFOCApproval(ApprovalVO)}
     */
    @Test
    public void testDetailViewFOCApproval5() throws Exception {
        ApprovalVO theForm = mock(ApprovalVO.class);
        when(theForm.getExternalTxnNum()).thenReturn("");
        when(theForm.getStatus()).thenReturn("Status");
        doNothing().when(theForm).setAddress(Mockito.<String>any());
        doNothing().when(theForm).setApprove1Remark(Mockito.<String>any());
        doNothing().when(theForm).setApprove2Remark(Mockito.<String>any());
        doNothing().when(theForm).setApprove3Remark(Mockito.<String>any());
        doNothing().when(theForm).setCategoryCode(Mockito.<String>any());
        doNothing().when(theForm).setCategoryCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setCategoryList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setChannelTransferVO(Mockito.<ChannelTransferVO>any());
        doNothing().when(theForm).setChannelUserID(Mockito.<String>any());
        doNothing().when(theForm).setChannelUserName(Mockito.<String>any());
        doNothing().when(theForm).setChannelUserStatus(Mockito.<String>any());
        doNothing().when(theForm).setCurrentDate(Mockito.<String>any());
        doNothing().when(theForm).setDefaultLang(Mockito.<String>any());
        doNothing().when(theForm).setDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setDomainCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setDomainList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setDomainTypeCode(Mockito.<String>any());
        doNothing().when(theForm).setErpCode(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnDate(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnExist(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnMandatory(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnNum(Mockito.<String>any());
        doNothing().when(theForm).setFocOrderApprovalLevel(anyInt());
        doNothing().when(theForm).setGeoDomainCodeDescForUser(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setMultiWallet(anyBoolean());
        doNothing().when(theForm).setNetCommissionQuantity(Mockito.<String>any());
        doNothing().when(theForm).setNetworkCode(Mockito.<String>any());
        doNothing().when(theForm).setNetworkCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setProductListWithTaxes(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setProductQuantity(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeCode(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setReceiverCrQuantity(Mockito.<String>any());
        doNothing().when(theForm).setReference(Mockito.<String>any());
        doNothing().when(theForm).setRefrenceNum(Mockito.<String>any());
        doNothing().when(theForm).setRemarks(Mockito.<String>any());
        doNothing().when(theForm).setRequestType(Mockito.<String>any());
        doNothing().when(theForm).setSearchCriteria(Mockito.<String>any());
        doNothing().when(theForm).setSearchUserID(Mockito.<String>any());
        doNothing().when(theForm).setSearchUserName(Mockito.<String>any());
        doNothing().when(theForm).setSecondLang(Mockito.<String>any());
        doNothing().when(theForm).setSenderDrQuantity(Mockito.<String>any());
        doNothing().when(theForm).setSessionDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setStatus(Mockito.<String>any());
        doNothing().when(theForm).setToPrimaryMSISDN(Mockito.<String>any());
        doNothing().when(theForm).setTotalMRP(Mockito.<String>any());
        doNothing().when(theForm).setTotalReqQty(Mockito.<String>any());
        doNothing().when(theForm).setTotalStock(Mockito.<String>any());
        doNothing().when(theForm).setTotalTax1(Mockito.<String>any());
        doNothing().when(theForm).setTotalTax2(Mockito.<String>any());
        doNothing().when(theForm).setTotalTransferedAmount(Mockito.<String>any());
        doNothing().when(theForm).setTransferCategory(Mockito.<String>any());
        doNothing().when(theForm).setTransferDateAsString(Mockito.<String>any());
        doNothing().when(theForm).setTransferItemList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setTransferNumber(Mockito.<String>any());
        doNothing().when(theForm).setUserCode(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileName(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileSetID(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileSetVersion(Mockito.<String>any());
        doNothing().when(theForm).setUserGradeName(Mockito.<String>any());
        doNothing().when(theForm).setUserID(Mockito.<String>any());
        doNothing().when(theForm).setUserList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setUserMsisdn(Mockito.<String>any());
        doNothing().when(theForm).setUserName(Mockito.<String>any());
        doNothing().when(theForm).setUserTransferProfileCode(Mockito.<String>any());
        doNothing().when(theForm).setUserTransferProfileName(Mockito.<String>any());
        doNothing().when(theForm).setWalletCode(Mockito.<String>any());
        doNothing().when(theForm).setWalletTypeList(Mockito.<List<ListValueVO>>any());
        doNothing().when(theForm).setWalletTypeWithWalletCode(Mockito.<String>any());
        theForm.setAddress("42 Main St");
        theForm.setApprove1Remark("Approve1 Remark");
        theForm.setApprove2Remark("Approve2 Remark");
        theForm.setApprove3Remark("Approve3 Remark");
        theForm.setCategoryCode("Category Code");
        theForm.setCategoryCodeDesc("Category Code Desc");
        theForm.setCategoryList(new ArrayList());
        theForm.setChannelTransferVO(ChannelTransferVO.getInstance());
        theForm.setChannelUserID("Channel User ID");
        theForm.setChannelUserName("janedoe");
        theForm.setChannelUserStatus("Channel User Status");
        theForm.setCurrentDate("2020-03-01");
        theForm.setDefaultLang("Default Lang");
        theForm.setDomainCode("Domain Code");
        theForm.setDomainCodeDesc("Domain Code Desc");
        theForm.setDomainList(new ArrayList());
        theForm.setDomainTypeCode("Domain Type Code");
        theForm.setErpCode("Erp Code");
        theForm.setExternalTxnDate("2020-03-01");
        theForm.setExternalTxnExist("External Txn Exist");
        theForm.setExternalTxnMandatory("External Txn Mandatory");
        theForm.setExternalTxnNum("External Txn Num");
        theForm.setFocOrderApprovalLevel(42);
        theForm.setGeoDomainCodeDescForUser("Geo Domain Code Desc For User");
        theForm.setGeographicalDomainCode("Geographical Domain Code");
        theForm.setGeographicalDomainCodeDesc("Geographical Domain Code Desc");
        theForm.setGeographicalDomainList(new ArrayList());
        theForm.setMultiWallet(true);
        theForm.setNetCommissionQuantity("Net Commission Quantity");
        theForm.setNetworkCode("Network Code");
        theForm.setNetworkCodeDesc("Network Code Desc");
        theForm.setProductListWithTaxes(new ArrayList());
        theForm.setProductQuantity("Product Quantity");
        theForm.setProductTypeCode("Product Type Code");
        theForm.setProductTypeCodeDesc("Product Type Code Desc");
        theForm.setProductTypeList(new ArrayList());
        theForm.setReceiverCrQuantity("Receiver Cr Quantity");
        theForm.setReference("Reference");
        theForm.setRefrenceNum("Refrence Num");
        theForm.setRemarks("Remarks");
        theForm.setRequestType("Request Type");
        theForm.setSearchCriteria("Search Criteria");
        theForm.setSearchUserID("Search User ID");
        theForm.setSearchUserName("janedoe");
        theForm.setSecondLang("Second Lang");
        theForm.setSenderDrQuantity("Sender Dr Quantity");
        theForm.setSessionDomainCode("Session Domain Code");
        theForm.setStatus("Status");
        theForm.setToPrimaryMSISDN("To Primary MSISDN");
        theForm.setTotalMRP("Total MRP");
        theForm.setTotalReqQty("Total Req Qty");
        theForm.setTotalStock("Total Stock");
        theForm.setTotalTax1("Total Tax1");
        theForm.setTotalTax2("Total Tax2");
        theForm.setTotalTransferedAmount("10");
        theForm.setTransferCategory("Transfer Category");
        theForm.setTransferDateAsString("2020-03-01");
        theForm.setTransferItemList(new ArrayList());
        theForm.setTransferNumber("42");
        theForm.setUserCode("User Code");
        theForm.setUserCommProfileName("foo.txt");
        theForm.setUserCommProfileSetID("User Comm Profile Set ID");
        theForm.setUserCommProfileSetVersion("1.0.2");
        theForm.setUserGradeName("User Grade Name");
        theForm.setUserID("User ID");
        theForm.setUserList(new ArrayList());
        theForm.setUserMsisdn("User Msisdn");
        theForm.setUserName("janedoe");
        theForm.setUserTransferProfileCode("User Transfer Profile Code");
        theForm.setUserTransferProfileName("foo.txt");
        theForm.setWalletCode("Wallet Code");
        theForm.setWalletTypeList(new ArrayList<>());
        theForm.setWalletTypeWithWalletCode("Wallet Type With Wallet Code");
        fOCApprovalServiceImpl.detailViewFOCApproval(theForm);
        verify(theForm).getExternalTxnNum();
        verify(theForm).getStatus();
        verify(theForm).setAddress(Mockito.<String>any());
        verify(theForm).setApprove1Remark(Mockito.<String>any());
        verify(theForm).setApprove2Remark(Mockito.<String>any());
        verify(theForm).setApprove3Remark(Mockito.<String>any());
        verify(theForm).setCategoryCode(Mockito.<String>any());
        verify(theForm).setCategoryCodeDesc(Mockito.<String>any());
        verify(theForm).setCategoryList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setChannelTransferVO(Mockito.<ChannelTransferVO>any());
        verify(theForm).setChannelUserID(Mockito.<String>any());
        verify(theForm).setChannelUserName(Mockito.<String>any());
        verify(theForm).setChannelUserStatus(Mockito.<String>any());
        verify(theForm).setCurrentDate(Mockito.<String>any());
        verify(theForm).setDefaultLang(Mockito.<String>any());
        verify(theForm).setDomainCode(Mockito.<String>any());
        verify(theForm).setDomainCodeDesc(Mockito.<String>any());
        verify(theForm).setDomainList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setDomainTypeCode(Mockito.<String>any());
        verify(theForm).setErpCode(Mockito.<String>any());
        verify(theForm).setExternalTxnDate(Mockito.<String>any());
        verify(theForm).setExternalTxnExist(Mockito.<String>any());
        verify(theForm).setExternalTxnMandatory(Mockito.<String>any());
        verify(theForm).setExternalTxnNum(Mockito.<String>any());
        verify(theForm).setFocOrderApprovalLevel(anyInt());
        verify(theForm).setGeoDomainCodeDescForUser(Mockito.<String>any());
        verify(theForm).setGeographicalDomainCode(Mockito.<String>any());
        verify(theForm).setGeographicalDomainCodeDesc(Mockito.<String>any());
        verify(theForm).setGeographicalDomainList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setMultiWallet(anyBoolean());
        verify(theForm).setNetCommissionQuantity(Mockito.<String>any());
        verify(theForm).setNetworkCode(Mockito.<String>any());
        verify(theForm).setNetworkCodeDesc(Mockito.<String>any());
        verify(theForm).setProductListWithTaxes(Mockito.<ArrayList<Object>>any());
        verify(theForm).setProductQuantity(Mockito.<String>any());
        verify(theForm).setProductTypeCode(Mockito.<String>any());
        verify(theForm).setProductTypeCodeDesc(Mockito.<String>any());
        verify(theForm).setProductTypeList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setReceiverCrQuantity(Mockito.<String>any());
        verify(theForm).setReference(Mockito.<String>any());
        verify(theForm).setRefrenceNum(Mockito.<String>any());
        verify(theForm).setRemarks(Mockito.<String>any());
        verify(theForm).setRequestType(Mockito.<String>any());
        verify(theForm).setSearchCriteria(Mockito.<String>any());
        verify(theForm).setSearchUserID(Mockito.<String>any());
        verify(theForm).setSearchUserName(Mockito.<String>any());
        verify(theForm).setSecondLang(Mockito.<String>any());
        verify(theForm).setSenderDrQuantity(Mockito.<String>any());
        verify(theForm).setSessionDomainCode(Mockito.<String>any());
        verify(theForm).setStatus(Mockito.<String>any());
        verify(theForm).setToPrimaryMSISDN(Mockito.<String>any());
        verify(theForm).setTotalMRP(Mockito.<String>any());
        verify(theForm).setTotalReqQty(Mockito.<String>any());
        verify(theForm).setTotalStock(Mockito.<String>any());
        verify(theForm).setTotalTax1(Mockito.<String>any());
        verify(theForm).setTotalTax2(Mockito.<String>any());
        verify(theForm).setTotalTransferedAmount(Mockito.<String>any());
        verify(theForm).setTransferCategory(Mockito.<String>any());
        verify(theForm).setTransferDateAsString(Mockito.<String>any());
        verify(theForm).setTransferItemList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setTransferNumber(Mockito.<String>any());
        verify(theForm).setUserCode(Mockito.<String>any());
        verify(theForm).setUserCommProfileName(Mockito.<String>any());
        verify(theForm).setUserCommProfileSetID(Mockito.<String>any());
        verify(theForm).setUserCommProfileSetVersion(Mockito.<String>any());
        verify(theForm).setUserGradeName(Mockito.<String>any());
        verify(theForm).setUserID(Mockito.<String>any());
        verify(theForm).setUserList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setUserMsisdn(Mockito.<String>any());
        verify(theForm).setUserName(Mockito.<String>any());
        verify(theForm).setUserTransferProfileCode(Mockito.<String>any());
        verify(theForm).setUserTransferProfileName(Mockito.<String>any());
        verify(theForm).setWalletCode(Mockito.<String>any());
        verify(theForm).setWalletTypeList(Mockito.<List<ListValueVO>>any());
        verify(theForm).setWalletTypeWithWalletCode(Mockito.<String>any());
    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#detailViewFOCApproval(ApprovalVO)}
     */
    @Test
    public void testDetailViewFOCApproval6() throws Exception {
        ApprovalVO theForm = mock(ApprovalVO.class);
        when(theForm.getExternalTxnMandatory()).thenReturn("External Txn Mandatory");
        when(theForm.getExternalTxnExist()).thenReturn("External Txn Exist");
        when(theForm.getExternalTxnNum()).thenReturn("External Txn Num");
        when(theForm.getStatus()).thenReturn("approve");
        doNothing().when(theForm).setAddress(Mockito.<String>any());
        doNothing().when(theForm).setApprove1Remark(Mockito.<String>any());
        doNothing().when(theForm).setApprove2Remark(Mockito.<String>any());
        doNothing().when(theForm).setApprove3Remark(Mockito.<String>any());
        doNothing().when(theForm).setCategoryCode(Mockito.<String>any());
        doNothing().when(theForm).setCategoryCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setCategoryList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setChannelTransferVO(Mockito.<ChannelTransferVO>any());
        doNothing().when(theForm).setChannelUserID(Mockito.<String>any());
        doNothing().when(theForm).setChannelUserName(Mockito.<String>any());
        doNothing().when(theForm).setChannelUserStatus(Mockito.<String>any());
        doNothing().when(theForm).setCurrentDate(Mockito.<String>any());
        doNothing().when(theForm).setDefaultLang(Mockito.<String>any());
        doNothing().when(theForm).setDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setDomainCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setDomainList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setDomainTypeCode(Mockito.<String>any());
        doNothing().when(theForm).setErpCode(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnDate(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnExist(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnMandatory(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnNum(Mockito.<String>any());
        doNothing().when(theForm).setFocOrderApprovalLevel(anyInt());
        doNothing().when(theForm).setGeoDomainCodeDescForUser(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setMultiWallet(anyBoolean());
        doNothing().when(theForm).setNetCommissionQuantity(Mockito.<String>any());
        doNothing().when(theForm).setNetworkCode(Mockito.<String>any());
        doNothing().when(theForm).setNetworkCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setProductListWithTaxes(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setProductQuantity(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeCode(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setReceiverCrQuantity(Mockito.<String>any());
        doNothing().when(theForm).setReference(Mockito.<String>any());
        doNothing().when(theForm).setRefrenceNum(Mockito.<String>any());
        doNothing().when(theForm).setRemarks(Mockito.<String>any());
        doNothing().when(theForm).setRequestType(Mockito.<String>any());
        doNothing().when(theForm).setSearchCriteria(Mockito.<String>any());
        doNothing().when(theForm).setSearchUserID(Mockito.<String>any());
        doNothing().when(theForm).setSearchUserName(Mockito.<String>any());
        doNothing().when(theForm).setSecondLang(Mockito.<String>any());
        doNothing().when(theForm).setSenderDrQuantity(Mockito.<String>any());
        doNothing().when(theForm).setSessionDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setStatus(Mockito.<String>any());
        doNothing().when(theForm).setToPrimaryMSISDN(Mockito.<String>any());
        doNothing().when(theForm).setTotalMRP(Mockito.<String>any());
        doNothing().when(theForm).setTotalReqQty(Mockito.<String>any());
        doNothing().when(theForm).setTotalStock(Mockito.<String>any());
        doNothing().when(theForm).setTotalTax1(Mockito.<String>any());
        doNothing().when(theForm).setTotalTax2(Mockito.<String>any());
        doNothing().when(theForm).setTotalTransferedAmount(Mockito.<String>any());
        doNothing().when(theForm).setTransferCategory(Mockito.<String>any());
        doNothing().when(theForm).setTransferDateAsString(Mockito.<String>any());
        doNothing().when(theForm).setTransferItemList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setTransferNumber(Mockito.<String>any());
        doNothing().when(theForm).setUserCode(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileName(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileSetID(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileSetVersion(Mockito.<String>any());
        doNothing().when(theForm).setUserGradeName(Mockito.<String>any());
        doNothing().when(theForm).setUserID(Mockito.<String>any());
        doNothing().when(theForm).setUserList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setUserMsisdn(Mockito.<String>any());
        doNothing().when(theForm).setUserName(Mockito.<String>any());
        doNothing().when(theForm).setUserTransferProfileCode(Mockito.<String>any());
        doNothing().when(theForm).setUserTransferProfileName(Mockito.<String>any());
        doNothing().when(theForm).setWalletCode(Mockito.<String>any());
        doNothing().when(theForm).setWalletTypeList(Mockito.<List<ListValueVO>>any());
        doNothing().when(theForm).setWalletTypeWithWalletCode(Mockito.<String>any());
        theForm.setAddress("42 Main St");
        theForm.setApprove1Remark("Approve1 Remark");
        theForm.setApprove2Remark("Approve2 Remark");
        theForm.setApprove3Remark("Approve3 Remark");
        theForm.setCategoryCode("Category Code");
        theForm.setCategoryCodeDesc("Category Code Desc");
        theForm.setCategoryList(new ArrayList());
        theForm.setChannelTransferVO(ChannelTransferVO.getInstance());
        theForm.setChannelUserID("Channel User ID");
        theForm.setChannelUserName("janedoe");
        theForm.setChannelUserStatus("Channel User Status");
        theForm.setCurrentDate("2020-03-01");
        theForm.setDefaultLang("Default Lang");
        theForm.setDomainCode("Domain Code");
        theForm.setDomainCodeDesc("Domain Code Desc");
        theForm.setDomainList(new ArrayList());
        theForm.setDomainTypeCode("Domain Type Code");
        theForm.setErpCode("Erp Code");
        theForm.setExternalTxnDate("2020-03-01");
        theForm.setExternalTxnExist("External Txn Exist");
        theForm.setExternalTxnMandatory("External Txn Mandatory");
        theForm.setExternalTxnNum("External Txn Num");
        theForm.setFocOrderApprovalLevel(42);
        theForm.setGeoDomainCodeDescForUser("Geo Domain Code Desc For User");
        theForm.setGeographicalDomainCode("Geographical Domain Code");
        theForm.setGeographicalDomainCodeDesc("Geographical Domain Code Desc");
        theForm.setGeographicalDomainList(new ArrayList());
        theForm.setMultiWallet(true);
        theForm.setNetCommissionQuantity("Net Commission Quantity");
        theForm.setNetworkCode("Network Code");
        theForm.setNetworkCodeDesc("Network Code Desc");
        theForm.setProductListWithTaxes(new ArrayList());
        theForm.setProductQuantity("Product Quantity");
        theForm.setProductTypeCode("Product Type Code");
        theForm.setProductTypeCodeDesc("Product Type Code Desc");
        theForm.setProductTypeList(new ArrayList());
        theForm.setReceiverCrQuantity("Receiver Cr Quantity");
        theForm.setReference("Reference");
        theForm.setRefrenceNum("Refrence Num");
        theForm.setRemarks("Remarks");
        theForm.setRequestType("Request Type");
        theForm.setSearchCriteria("Search Criteria");
        theForm.setSearchUserID("Search User ID");
        theForm.setSearchUserName("janedoe");
        theForm.setSecondLang("Second Lang");
        theForm.setSenderDrQuantity("Sender Dr Quantity");
        theForm.setSessionDomainCode("Session Domain Code");
        theForm.setStatus("Status");
        theForm.setToPrimaryMSISDN("To Primary MSISDN");
        theForm.setTotalMRP("Total MRP");
        theForm.setTotalReqQty("Total Req Qty");
        theForm.setTotalStock("Total Stock");
        theForm.setTotalTax1("Total Tax1");
        theForm.setTotalTax2("Total Tax2");
        theForm.setTotalTransferedAmount("10");
        theForm.setTransferCategory("Transfer Category");
        theForm.setTransferDateAsString("2020-03-01");
        theForm.setTransferItemList(new ArrayList());
        theForm.setTransferNumber("42");
        theForm.setUserCode("User Code");
        theForm.setUserCommProfileName("foo.txt");
        theForm.setUserCommProfileSetID("User Comm Profile Set ID");
        theForm.setUserCommProfileSetVersion("1.0.2");
        theForm.setUserGradeName("User Grade Name");
        theForm.setUserID("User ID");
        theForm.setUserList(new ArrayList());
        theForm.setUserMsisdn("User Msisdn");
        theForm.setUserName("janedoe");
        theForm.setUserTransferProfileCode("User Transfer Profile Code");
        theForm.setUserTransferProfileName("foo.txt");
        theForm.setWalletCode("Wallet Code");
        theForm.setWalletTypeList(new ArrayList<>());
        theForm.setWalletTypeWithWalletCode("Wallet Type With Wallet Code");
        fOCApprovalServiceImpl.detailViewFOCApproval(theForm);
        verify(theForm).getExternalTxnExist();
        verify(theForm).getExternalTxnMandatory();
        verify(theForm, atLeast(1)).getExternalTxnNum();
        verify(theForm).getStatus();
        verify(theForm).setAddress(Mockito.<String>any());
        verify(theForm).setApprove1Remark(Mockito.<String>any());
        verify(theForm).setApprove2Remark(Mockito.<String>any());
        verify(theForm).setApprove3Remark(Mockito.<String>any());
        verify(theForm).setCategoryCode(Mockito.<String>any());
        verify(theForm).setCategoryCodeDesc(Mockito.<String>any());
        verify(theForm).setCategoryList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setChannelTransferVO(Mockito.<ChannelTransferVO>any());
        verify(theForm).setChannelUserID(Mockito.<String>any());
        verify(theForm).setChannelUserName(Mockito.<String>any());
        verify(theForm).setChannelUserStatus(Mockito.<String>any());
        verify(theForm).setCurrentDate(Mockito.<String>any());
        verify(theForm).setDefaultLang(Mockito.<String>any());
        verify(theForm).setDomainCode(Mockito.<String>any());
        verify(theForm).setDomainCodeDesc(Mockito.<String>any());
        verify(theForm).setDomainList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setDomainTypeCode(Mockito.<String>any());
        verify(theForm).setErpCode(Mockito.<String>any());
        verify(theForm).setExternalTxnDate(Mockito.<String>any());
        verify(theForm).setExternalTxnExist(Mockito.<String>any());
        verify(theForm).setExternalTxnMandatory(Mockito.<String>any());
        verify(theForm).setExternalTxnNum(Mockito.<String>any());
        verify(theForm).setFocOrderApprovalLevel(anyInt());
        verify(theForm).setGeoDomainCodeDescForUser(Mockito.<String>any());
        verify(theForm).setGeographicalDomainCode(Mockito.<String>any());
        verify(theForm).setGeographicalDomainCodeDesc(Mockito.<String>any());
        verify(theForm).setGeographicalDomainList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setMultiWallet(anyBoolean());
        verify(theForm).setNetCommissionQuantity(Mockito.<String>any());
        verify(theForm).setNetworkCode(Mockito.<String>any());
        verify(theForm).setNetworkCodeDesc(Mockito.<String>any());
        verify(theForm).setProductListWithTaxes(Mockito.<ArrayList<Object>>any());
        verify(theForm).setProductQuantity(Mockito.<String>any());
        verify(theForm).setProductTypeCode(Mockito.<String>any());
        verify(theForm).setProductTypeCodeDesc(Mockito.<String>any());
        verify(theForm).setProductTypeList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setReceiverCrQuantity(Mockito.<String>any());
        verify(theForm).setReference(Mockito.<String>any());
        verify(theForm).setRefrenceNum(Mockito.<String>any());
        verify(theForm).setRemarks(Mockito.<String>any());
        verify(theForm).setRequestType(Mockito.<String>any());
        verify(theForm).setSearchCriteria(Mockito.<String>any());
        verify(theForm).setSearchUserID(Mockito.<String>any());
        verify(theForm).setSearchUserName(Mockito.<String>any());
        verify(theForm).setSecondLang(Mockito.<String>any());
        verify(theForm).setSenderDrQuantity(Mockito.<String>any());
        verify(theForm).setSessionDomainCode(Mockito.<String>any());
        verify(theForm).setStatus(Mockito.<String>any());
        verify(theForm).setToPrimaryMSISDN(Mockito.<String>any());
        verify(theForm).setTotalMRP(Mockito.<String>any());
        verify(theForm).setTotalReqQty(Mockito.<String>any());
        verify(theForm).setTotalStock(Mockito.<String>any());
        verify(theForm).setTotalTax1(Mockito.<String>any());
        verify(theForm).setTotalTax2(Mockito.<String>any());
        verify(theForm).setTotalTransferedAmount(Mockito.<String>any());
        verify(theForm).setTransferCategory(Mockito.<String>any());
        verify(theForm).setTransferDateAsString(Mockito.<String>any());
        verify(theForm).setTransferItemList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setTransferNumber(Mockito.<String>any());
        verify(theForm).setUserCode(Mockito.<String>any());
        verify(theForm).setUserCommProfileName(Mockito.<String>any());
        verify(theForm).setUserCommProfileSetID(Mockito.<String>any());
        verify(theForm).setUserCommProfileSetVersion(Mockito.<String>any());
        verify(theForm).setUserGradeName(Mockito.<String>any());
        verify(theForm).setUserID(Mockito.<String>any());
        verify(theForm).setUserList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setUserMsisdn(Mockito.<String>any());
        verify(theForm).setUserName(Mockito.<String>any());
        verify(theForm).setUserTransferProfileCode(Mockito.<String>any());
        verify(theForm).setUserTransferProfileName(Mockito.<String>any());
        verify(theForm).setWalletCode(Mockito.<String>any());
        verify(theForm).setWalletTypeList(Mockito.<List<ListValueVO>>any());
        verify(theForm).setWalletTypeWithWalletCode(Mockito.<String>any());
    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#detailViewFOCApproval(ApprovalVO)}
     */
    @Test
    public void testDetailViewFOCApproval7() throws Exception {
        ApprovalVO theForm = mock(ApprovalVO.class);
        when(theForm.getExternalTxnMandatory()).thenReturn("Y");
        when(theForm.getExternalTxnNum()).thenReturn("External Txn Num");
        when(theForm.getStatus()).thenReturn("approve");
        doNothing().when(theForm).setAddress(Mockito.<String>any());
        doNothing().when(theForm).setApprove1Remark(Mockito.<String>any());
        doNothing().when(theForm).setApprove2Remark(Mockito.<String>any());
        doNothing().when(theForm).setApprove3Remark(Mockito.<String>any());
        doNothing().when(theForm).setCategoryCode(Mockito.<String>any());
        doNothing().when(theForm).setCategoryCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setCategoryList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setChannelTransferVO(Mockito.<ChannelTransferVO>any());
        doNothing().when(theForm).setChannelUserID(Mockito.<String>any());
        doNothing().when(theForm).setChannelUserName(Mockito.<String>any());
        doNothing().when(theForm).setChannelUserStatus(Mockito.<String>any());
        doNothing().when(theForm).setCurrentDate(Mockito.<String>any());
        doNothing().when(theForm).setDefaultLang(Mockito.<String>any());
        doNothing().when(theForm).setDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setDomainCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setDomainList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setDomainTypeCode(Mockito.<String>any());
        doNothing().when(theForm).setErpCode(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnDate(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnExist(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnMandatory(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnNum(Mockito.<String>any());
        doNothing().when(theForm).setFocOrderApprovalLevel(anyInt());
        doNothing().when(theForm).setGeoDomainCodeDescForUser(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setMultiWallet(anyBoolean());
        doNothing().when(theForm).setNetCommissionQuantity(Mockito.<String>any());
        doNothing().when(theForm).setNetworkCode(Mockito.<String>any());
        doNothing().when(theForm).setNetworkCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setProductListWithTaxes(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setProductQuantity(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeCode(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setReceiverCrQuantity(Mockito.<String>any());
        doNothing().when(theForm).setReference(Mockito.<String>any());
        doNothing().when(theForm).setRefrenceNum(Mockito.<String>any());
        doNothing().when(theForm).setRemarks(Mockito.<String>any());
        doNothing().when(theForm).setRequestType(Mockito.<String>any());
        doNothing().when(theForm).setSearchCriteria(Mockito.<String>any());
        doNothing().when(theForm).setSearchUserID(Mockito.<String>any());
        doNothing().when(theForm).setSearchUserName(Mockito.<String>any());
        doNothing().when(theForm).setSecondLang(Mockito.<String>any());
        doNothing().when(theForm).setSenderDrQuantity(Mockito.<String>any());
        doNothing().when(theForm).setSessionDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setStatus(Mockito.<String>any());
        doNothing().when(theForm).setToPrimaryMSISDN(Mockito.<String>any());
        doNothing().when(theForm).setTotalMRP(Mockito.<String>any());
        doNothing().when(theForm).setTotalReqQty(Mockito.<String>any());
        doNothing().when(theForm).setTotalStock(Mockito.<String>any());
        doNothing().when(theForm).setTotalTax1(Mockito.<String>any());
        doNothing().when(theForm).setTotalTax2(Mockito.<String>any());
        doNothing().when(theForm).setTotalTransferedAmount(Mockito.<String>any());
        doNothing().when(theForm).setTransferCategory(Mockito.<String>any());
        doNothing().when(theForm).setTransferDateAsString(Mockito.<String>any());
        doNothing().when(theForm).setTransferItemList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setTransferNumber(Mockito.<String>any());
        doNothing().when(theForm).setUserCode(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileName(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileSetID(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileSetVersion(Mockito.<String>any());
        doNothing().when(theForm).setUserGradeName(Mockito.<String>any());
        doNothing().when(theForm).setUserID(Mockito.<String>any());
        doNothing().when(theForm).setUserList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setUserMsisdn(Mockito.<String>any());
        doNothing().when(theForm).setUserName(Mockito.<String>any());
        doNothing().when(theForm).setUserTransferProfileCode(Mockito.<String>any());
        doNothing().when(theForm).setUserTransferProfileName(Mockito.<String>any());
        doNothing().when(theForm).setWalletCode(Mockito.<String>any());
        doNothing().when(theForm).setWalletTypeList(Mockito.<List<ListValueVO>>any());
        doNothing().when(theForm).setWalletTypeWithWalletCode(Mockito.<String>any());
        theForm.setAddress("42 Main St");
        theForm.setApprove1Remark("Approve1 Remark");
        theForm.setApprove2Remark("Approve2 Remark");
        theForm.setApprove3Remark("Approve3 Remark");
        theForm.setCategoryCode("Category Code");
        theForm.setCategoryCodeDesc("Category Code Desc");
        theForm.setCategoryList(new ArrayList());
        theForm.setChannelTransferVO(ChannelTransferVO.getInstance());
        theForm.setChannelUserID("Channel User ID");
        theForm.setChannelUserName("janedoe");
        theForm.setChannelUserStatus("Channel User Status");
        theForm.setCurrentDate("2020-03-01");
        theForm.setDefaultLang("Default Lang");
        theForm.setDomainCode("Domain Code");
        theForm.setDomainCodeDesc("Domain Code Desc");
        theForm.setDomainList(new ArrayList());
        theForm.setDomainTypeCode("Domain Type Code");
        theForm.setErpCode("Erp Code");
        theForm.setExternalTxnDate("2020-03-01");
        theForm.setExternalTxnExist("External Txn Exist");
        theForm.setExternalTxnMandatory("External Txn Mandatory");
        theForm.setExternalTxnNum("External Txn Num");
        theForm.setFocOrderApprovalLevel(42);
        theForm.setGeoDomainCodeDescForUser("Geo Domain Code Desc For User");
        theForm.setGeographicalDomainCode("Geographical Domain Code");
        theForm.setGeographicalDomainCodeDesc("Geographical Domain Code Desc");
        theForm.setGeographicalDomainList(new ArrayList());
        theForm.setMultiWallet(true);
        theForm.setNetCommissionQuantity("Net Commission Quantity");
        theForm.setNetworkCode("Network Code");
        theForm.setNetworkCodeDesc("Network Code Desc");
        theForm.setProductListWithTaxes(new ArrayList());
        theForm.setProductQuantity("Product Quantity");
        theForm.setProductTypeCode("Product Type Code");
        theForm.setProductTypeCodeDesc("Product Type Code Desc");
        theForm.setProductTypeList(new ArrayList());
        theForm.setReceiverCrQuantity("Receiver Cr Quantity");
        theForm.setReference("Reference");
        theForm.setRefrenceNum("Refrence Num");
        theForm.setRemarks("Remarks");
        theForm.setRequestType("Request Type");
        theForm.setSearchCriteria("Search Criteria");
        theForm.setSearchUserID("Search User ID");
        theForm.setSearchUserName("janedoe");
        theForm.setSecondLang("Second Lang");
        theForm.setSenderDrQuantity("Sender Dr Quantity");
        theForm.setSessionDomainCode("Session Domain Code");
        theForm.setStatus("Status");
        theForm.setToPrimaryMSISDN("To Primary MSISDN");
        theForm.setTotalMRP("Total MRP");
        theForm.setTotalReqQty("Total Req Qty");
        theForm.setTotalStock("Total Stock");
        theForm.setTotalTax1("Total Tax1");
        theForm.setTotalTax2("Total Tax2");
        theForm.setTotalTransferedAmount("10");
        theForm.setTransferCategory("Transfer Category");
        theForm.setTransferDateAsString("2020-03-01");
        theForm.setTransferItemList(new ArrayList());
        theForm.setTransferNumber("42");
        theForm.setUserCode("User Code");
        theForm.setUserCommProfileName("foo.txt");
        theForm.setUserCommProfileSetID("User Comm Profile Set ID");
        theForm.setUserCommProfileSetVersion("1.0.2");
        theForm.setUserGradeName("User Grade Name");
        theForm.setUserID("User ID");
        theForm.setUserList(new ArrayList());
        theForm.setUserMsisdn("User Msisdn");
        theForm.setUserName("janedoe");
        theForm.setUserTransferProfileCode("User Transfer Profile Code");
        theForm.setUserTransferProfileName("foo.txt");
        theForm.setWalletCode("Wallet Code");
        theForm.setWalletTypeList(new ArrayList<>());
        theForm.setWalletTypeWithWalletCode("Wallet Type With Wallet Code");
        fOCApprovalServiceImpl.detailViewFOCApproval(theForm);
        verify(theForm).getExternalTxnMandatory();
        verify(theForm).getExternalTxnNum();
        verify(theForm).getStatus();
        verify(theForm).setAddress(Mockito.<String>any());
        verify(theForm).setApprove1Remark(Mockito.<String>any());
        verify(theForm).setApprove2Remark(Mockito.<String>any());
        verify(theForm).setApprove3Remark(Mockito.<String>any());
        verify(theForm).setCategoryCode(Mockito.<String>any());
        verify(theForm).setCategoryCodeDesc(Mockito.<String>any());
        verify(theForm).setCategoryList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setChannelTransferVO(Mockito.<ChannelTransferVO>any());
        verify(theForm).setChannelUserID(Mockito.<String>any());
        verify(theForm).setChannelUserName(Mockito.<String>any());
        verify(theForm).setChannelUserStatus(Mockito.<String>any());
        verify(theForm).setCurrentDate(Mockito.<String>any());
        verify(theForm).setDefaultLang(Mockito.<String>any());
        verify(theForm).setDomainCode(Mockito.<String>any());
        verify(theForm).setDomainCodeDesc(Mockito.<String>any());
        verify(theForm).setDomainList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setDomainTypeCode(Mockito.<String>any());
        verify(theForm).setErpCode(Mockito.<String>any());
        verify(theForm).setExternalTxnDate(Mockito.<String>any());
        verify(theForm).setExternalTxnExist(Mockito.<String>any());
        verify(theForm).setExternalTxnMandatory(Mockito.<String>any());
        verify(theForm).setExternalTxnNum(Mockito.<String>any());
        verify(theForm).setFocOrderApprovalLevel(anyInt());
        verify(theForm).setGeoDomainCodeDescForUser(Mockito.<String>any());
        verify(theForm).setGeographicalDomainCode(Mockito.<String>any());
        verify(theForm).setGeographicalDomainCodeDesc(Mockito.<String>any());
        verify(theForm).setGeographicalDomainList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setMultiWallet(anyBoolean());
        verify(theForm).setNetCommissionQuantity(Mockito.<String>any());
        verify(theForm).setNetworkCode(Mockito.<String>any());
        verify(theForm).setNetworkCodeDesc(Mockito.<String>any());
        verify(theForm).setProductListWithTaxes(Mockito.<ArrayList<Object>>any());
        verify(theForm).setProductQuantity(Mockito.<String>any());
        verify(theForm).setProductTypeCode(Mockito.<String>any());
        verify(theForm).setProductTypeCodeDesc(Mockito.<String>any());
        verify(theForm).setProductTypeList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setReceiverCrQuantity(Mockito.<String>any());
        verify(theForm).setReference(Mockito.<String>any());
        verify(theForm).setRefrenceNum(Mockito.<String>any());
        verify(theForm).setRemarks(Mockito.<String>any());
        verify(theForm).setRequestType(Mockito.<String>any());
        verify(theForm).setSearchCriteria(Mockito.<String>any());
        verify(theForm).setSearchUserID(Mockito.<String>any());
        verify(theForm).setSearchUserName(Mockito.<String>any());
        verify(theForm).setSecondLang(Mockito.<String>any());
        verify(theForm).setSenderDrQuantity(Mockito.<String>any());
        verify(theForm).setSessionDomainCode(Mockito.<String>any());
        verify(theForm).setStatus(Mockito.<String>any());
        verify(theForm).setToPrimaryMSISDN(Mockito.<String>any());
        verify(theForm).setTotalMRP(Mockito.<String>any());
        verify(theForm).setTotalReqQty(Mockito.<String>any());
        verify(theForm).setTotalStock(Mockito.<String>any());
        verify(theForm).setTotalTax1(Mockito.<String>any());
        verify(theForm).setTotalTax2(Mockito.<String>any());
        verify(theForm).setTotalTransferedAmount(Mockito.<String>any());
        verify(theForm).setTransferCategory(Mockito.<String>any());
        verify(theForm).setTransferDateAsString(Mockito.<String>any());
        verify(theForm).setTransferItemList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setTransferNumber(Mockito.<String>any());
        verify(theForm).setUserCode(Mockito.<String>any());
        verify(theForm).setUserCommProfileName(Mockito.<String>any());
        verify(theForm).setUserCommProfileSetID(Mockito.<String>any());
        verify(theForm).setUserCommProfileSetVersion(Mockito.<String>any());
        verify(theForm).setUserGradeName(Mockito.<String>any());
        verify(theForm).setUserID(Mockito.<String>any());
        verify(theForm).setUserList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setUserMsisdn(Mockito.<String>any());
        verify(theForm).setUserName(Mockito.<String>any());
        verify(theForm).setUserTransferProfileCode(Mockito.<String>any());
        verify(theForm).setUserTransferProfileName(Mockito.<String>any());
        verify(theForm).setWalletCode(Mockito.<String>any());
        verify(theForm).setWalletTypeList(Mockito.<List<ListValueVO>>any());
        verify(theForm).setWalletTypeWithWalletCode(Mockito.<String>any());
    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#detailViewFOCApproval(ApprovalVO)}
     */
    @Test
    public void testDetailViewFOCApproval8() throws Exception {
        ApprovalVO theForm = mock(ApprovalVO.class);
        when(theForm.getExternalTxnNum()).thenReturn(null);
        when(theForm.getStatus()).thenReturn("approve");
        doNothing().when(theForm).setAddress(Mockito.<String>any());
        doNothing().when(theForm).setApprove1Remark(Mockito.<String>any());
        doNothing().when(theForm).setApprove2Remark(Mockito.<String>any());
        doNothing().when(theForm).setApprove3Remark(Mockito.<String>any());
        doNothing().when(theForm).setCategoryCode(Mockito.<String>any());
        doNothing().when(theForm).setCategoryCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setCategoryList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setChannelTransferVO(Mockito.<ChannelTransferVO>any());
        doNothing().when(theForm).setChannelUserID(Mockito.<String>any());
        doNothing().when(theForm).setChannelUserName(Mockito.<String>any());
        doNothing().when(theForm).setChannelUserStatus(Mockito.<String>any());
        doNothing().when(theForm).setCurrentDate(Mockito.<String>any());
        doNothing().when(theForm).setDefaultLang(Mockito.<String>any());
        doNothing().when(theForm).setDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setDomainCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setDomainList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setDomainTypeCode(Mockito.<String>any());
        doNothing().when(theForm).setErpCode(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnDate(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnExist(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnMandatory(Mockito.<String>any());
        doNothing().when(theForm).setExternalTxnNum(Mockito.<String>any());
        doNothing().when(theForm).setFocOrderApprovalLevel(anyInt());
        doNothing().when(theForm).setGeoDomainCodeDescForUser(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setGeographicalDomainList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setMultiWallet(anyBoolean());
        doNothing().when(theForm).setNetCommissionQuantity(Mockito.<String>any());
        doNothing().when(theForm).setNetworkCode(Mockito.<String>any());
        doNothing().when(theForm).setNetworkCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setProductListWithTaxes(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setProductQuantity(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeCode(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeCodeDesc(Mockito.<String>any());
        doNothing().when(theForm).setProductTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setReceiverCrQuantity(Mockito.<String>any());
        doNothing().when(theForm).setReference(Mockito.<String>any());
        doNothing().when(theForm).setRefrenceNum(Mockito.<String>any());
        doNothing().when(theForm).setRemarks(Mockito.<String>any());
        doNothing().when(theForm).setRequestType(Mockito.<String>any());
        doNothing().when(theForm).setSearchCriteria(Mockito.<String>any());
        doNothing().when(theForm).setSearchUserID(Mockito.<String>any());
        doNothing().when(theForm).setSearchUserName(Mockito.<String>any());
        doNothing().when(theForm).setSecondLang(Mockito.<String>any());
        doNothing().when(theForm).setSenderDrQuantity(Mockito.<String>any());
        doNothing().when(theForm).setSessionDomainCode(Mockito.<String>any());
        doNothing().when(theForm).setStatus(Mockito.<String>any());
        doNothing().when(theForm).setToPrimaryMSISDN(Mockito.<String>any());
        doNothing().when(theForm).setTotalMRP(Mockito.<String>any());
        doNothing().when(theForm).setTotalReqQty(Mockito.<String>any());
        doNothing().when(theForm).setTotalStock(Mockito.<String>any());
        doNothing().when(theForm).setTotalTax1(Mockito.<String>any());
        doNothing().when(theForm).setTotalTax2(Mockito.<String>any());
        doNothing().when(theForm).setTotalTransferedAmount(Mockito.<String>any());
        doNothing().when(theForm).setTransferCategory(Mockito.<String>any());
        doNothing().when(theForm).setTransferDateAsString(Mockito.<String>any());
        doNothing().when(theForm).setTransferItemList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setTransferNumber(Mockito.<String>any());
        doNothing().when(theForm).setUserCode(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileName(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileSetID(Mockito.<String>any());
        doNothing().when(theForm).setUserCommProfileSetVersion(Mockito.<String>any());
        doNothing().when(theForm).setUserGradeName(Mockito.<String>any());
        doNothing().when(theForm).setUserID(Mockito.<String>any());
        doNothing().when(theForm).setUserList(Mockito.<ArrayList<Object>>any());
        doNothing().when(theForm).setUserMsisdn(Mockito.<String>any());
        doNothing().when(theForm).setUserName(Mockito.<String>any());
        doNothing().when(theForm).setUserTransferProfileCode(Mockito.<String>any());
        doNothing().when(theForm).setUserTransferProfileName(Mockito.<String>any());
        doNothing().when(theForm).setWalletCode(Mockito.<String>any());
        doNothing().when(theForm).setWalletTypeList(Mockito.<List<ListValueVO>>any());
        doNothing().when(theForm).setWalletTypeWithWalletCode(Mockito.<String>any());
        theForm.setAddress("42 Main St");
        theForm.setApprove1Remark("Approve1 Remark");
        theForm.setApprove2Remark("Approve2 Remark");
        theForm.setApprove3Remark("Approve3 Remark");
        theForm.setCategoryCode("Category Code");
        theForm.setCategoryCodeDesc("Category Code Desc");
        theForm.setCategoryList(new ArrayList());
        theForm.setChannelTransferVO(ChannelTransferVO.getInstance());
        theForm.setChannelUserID("Channel User ID");
        theForm.setChannelUserName("janedoe");
        theForm.setChannelUserStatus("Channel User Status");
        theForm.setCurrentDate("2020-03-01");
        theForm.setDefaultLang("Default Lang");
        theForm.setDomainCode("Domain Code");
        theForm.setDomainCodeDesc("Domain Code Desc");
        theForm.setDomainList(new ArrayList());
        theForm.setDomainTypeCode("Domain Type Code");
        theForm.setErpCode("Erp Code");
        theForm.setExternalTxnDate("2020-03-01");
        theForm.setExternalTxnExist("External Txn Exist");
        theForm.setExternalTxnMandatory("External Txn Mandatory");
        theForm.setExternalTxnNum("External Txn Num");
        theForm.setFocOrderApprovalLevel(42);
        theForm.setGeoDomainCodeDescForUser("Geo Domain Code Desc For User");
        theForm.setGeographicalDomainCode("Geographical Domain Code");
        theForm.setGeographicalDomainCodeDesc("Geographical Domain Code Desc");
        theForm.setGeographicalDomainList(new ArrayList());
        theForm.setMultiWallet(true);
        theForm.setNetCommissionQuantity("Net Commission Quantity");
        theForm.setNetworkCode("Network Code");
        theForm.setNetworkCodeDesc("Network Code Desc");
        theForm.setProductListWithTaxes(new ArrayList());
        theForm.setProductQuantity("Product Quantity");
        theForm.setProductTypeCode("Product Type Code");
        theForm.setProductTypeCodeDesc("Product Type Code Desc");
        theForm.setProductTypeList(new ArrayList());
        theForm.setReceiverCrQuantity("Receiver Cr Quantity");
        theForm.setReference("Reference");
        theForm.setRefrenceNum("Refrence Num");
        theForm.setRemarks("Remarks");
        theForm.setRequestType("Request Type");
        theForm.setSearchCriteria("Search Criteria");
        theForm.setSearchUserID("Search User ID");
        theForm.setSearchUserName("janedoe");
        theForm.setSecondLang("Second Lang");
        theForm.setSenderDrQuantity("Sender Dr Quantity");
        theForm.setSessionDomainCode("Session Domain Code");
        theForm.setStatus("Status");
        theForm.setToPrimaryMSISDN("To Primary MSISDN");
        theForm.setTotalMRP("Total MRP");
        theForm.setTotalReqQty("Total Req Qty");
        theForm.setTotalStock("Total Stock");
        theForm.setTotalTax1("Total Tax1");
        theForm.setTotalTax2("Total Tax2");
        theForm.setTotalTransferedAmount("10");
        theForm.setTransferCategory("Transfer Category");
        theForm.setTransferDateAsString("2020-03-01");
        theForm.setTransferItemList(new ArrayList());
        theForm.setTransferNumber("42");
        theForm.setUserCode("User Code");
        theForm.setUserCommProfileName("foo.txt");
        theForm.setUserCommProfileSetID("User Comm Profile Set ID");
        theForm.setUserCommProfileSetVersion("1.0.2");
        theForm.setUserGradeName("User Grade Name");
        theForm.setUserID("User ID");
        theForm.setUserList(new ArrayList());
        theForm.setUserMsisdn("User Msisdn");
        theForm.setUserName("janedoe");
        theForm.setUserTransferProfileCode("User Transfer Profile Code");
        theForm.setUserTransferProfileName("foo.txt");
        theForm.setWalletCode("Wallet Code");
        theForm.setWalletTypeList(new ArrayList<>());
        theForm.setWalletTypeWithWalletCode("Wallet Type With Wallet Code");
        fOCApprovalServiceImpl.detailViewFOCApproval(theForm);
        verify(theForm, atLeast(1)).getExternalTxnNum();
        verify(theForm).getStatus();
        verify(theForm).setAddress(Mockito.<String>any());
        verify(theForm).setApprove1Remark(Mockito.<String>any());
        verify(theForm).setApprove2Remark(Mockito.<String>any());
        verify(theForm).setApprove3Remark(Mockito.<String>any());
        verify(theForm).setCategoryCode(Mockito.<String>any());
        verify(theForm).setCategoryCodeDesc(Mockito.<String>any());
        verify(theForm).setCategoryList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setChannelTransferVO(Mockito.<ChannelTransferVO>any());
        verify(theForm).setChannelUserID(Mockito.<String>any());
        verify(theForm).setChannelUserName(Mockito.<String>any());
        verify(theForm).setChannelUserStatus(Mockito.<String>any());
        verify(theForm).setCurrentDate(Mockito.<String>any());
        verify(theForm).setDefaultLang(Mockito.<String>any());
        verify(theForm).setDomainCode(Mockito.<String>any());
        verify(theForm).setDomainCodeDesc(Mockito.<String>any());
        verify(theForm).setDomainList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setDomainTypeCode(Mockito.<String>any());
        verify(theForm).setErpCode(Mockito.<String>any());
        verify(theForm).setExternalTxnDate(Mockito.<String>any());
        verify(theForm).setExternalTxnExist(Mockito.<String>any());
        verify(theForm).setExternalTxnMandatory(Mockito.<String>any());
        verify(theForm).setExternalTxnNum(Mockito.<String>any());
        verify(theForm).setFocOrderApprovalLevel(anyInt());
        verify(theForm).setGeoDomainCodeDescForUser(Mockito.<String>any());
        verify(theForm).setGeographicalDomainCode(Mockito.<String>any());
        verify(theForm).setGeographicalDomainCodeDesc(Mockito.<String>any());
        verify(theForm).setGeographicalDomainList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setMultiWallet(anyBoolean());
        verify(theForm).setNetCommissionQuantity(Mockito.<String>any());
        verify(theForm).setNetworkCode(Mockito.<String>any());
        verify(theForm).setNetworkCodeDesc(Mockito.<String>any());
        verify(theForm).setProductListWithTaxes(Mockito.<ArrayList<Object>>any());
        verify(theForm).setProductQuantity(Mockito.<String>any());
        verify(theForm).setProductTypeCode(Mockito.<String>any());
        verify(theForm).setProductTypeCodeDesc(Mockito.<String>any());
        verify(theForm).setProductTypeList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setReceiverCrQuantity(Mockito.<String>any());
        verify(theForm).setReference(Mockito.<String>any());
        verify(theForm).setRefrenceNum(Mockito.<String>any());
        verify(theForm).setRemarks(Mockito.<String>any());
        verify(theForm).setRequestType(Mockito.<String>any());
        verify(theForm).setSearchCriteria(Mockito.<String>any());
        verify(theForm).setSearchUserID(Mockito.<String>any());
        verify(theForm).setSearchUserName(Mockito.<String>any());
        verify(theForm).setSecondLang(Mockito.<String>any());
        verify(theForm).setSenderDrQuantity(Mockito.<String>any());
        verify(theForm).setSessionDomainCode(Mockito.<String>any());
        verify(theForm).setStatus(Mockito.<String>any());
        verify(theForm).setToPrimaryMSISDN(Mockito.<String>any());
        verify(theForm).setTotalMRP(Mockito.<String>any());
        verify(theForm).setTotalReqQty(Mockito.<String>any());
        verify(theForm).setTotalStock(Mockito.<String>any());
        verify(theForm).setTotalTax1(Mockito.<String>any());
        verify(theForm).setTotalTax2(Mockito.<String>any());
        verify(theForm).setTotalTransferedAmount(Mockito.<String>any());
        verify(theForm).setTransferCategory(Mockito.<String>any());
        verify(theForm).setTransferDateAsString(Mockito.<String>any());
        verify(theForm).setTransferItemList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setTransferNumber(Mockito.<String>any());
        verify(theForm).setUserCode(Mockito.<String>any());
        verify(theForm).setUserCommProfileName(Mockito.<String>any());
        verify(theForm).setUserCommProfileSetID(Mockito.<String>any());
        verify(theForm).setUserCommProfileSetVersion(Mockito.<String>any());
        verify(theForm).setUserGradeName(Mockito.<String>any());
        verify(theForm).setUserID(Mockito.<String>any());
        verify(theForm).setUserList(Mockito.<ArrayList<Object>>any());
        verify(theForm).setUserMsisdn(Mockito.<String>any());
        verify(theForm).setUserName(Mockito.<String>any());
        verify(theForm).setUserTransferProfileCode(Mockito.<String>any());
        verify(theForm).setUserTransferProfileName(Mockito.<String>any());
        verify(theForm).setWalletCode(Mockito.<String>any());
        verify(theForm).setWalletTypeList(Mockito.<List<ListValueVO>>any());
        verify(theForm).setWalletTypeWithWalletCode(Mockito.<String>any());
    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#getMessage(Locale, String)}
     */
    @Test
    public void testGetMessage() {
        assertNull(fOCApprovalServiceImpl.getMessage(Locale.getDefault(), "An error occurred"));
        assertNull(fOCApprovalServiceImpl.getMessage(Locale.getDefault(Locale.Category.DISPLAY), "An error occurred"));
        assertNull(fOCApprovalServiceImpl.getMessage(null, "An error occurred"));
        assertNull(fOCApprovalServiceImpl.getMessage(Locale.getDefault(), "An error occurred", new String[]{"Args"}));
        assertNull(fOCApprovalServiceImpl.getMessage(Locale.getDefault(Locale.Category.DISPLAY), "An error occurred",
                new String[]{"Args"}));
        assertNull(fOCApprovalServiceImpl.getMessage(null, "An error occurred", new String[]{"Args"}));
    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#processFOCApproval(FOCApprovalRequestVO, MultiValueMap, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessFOCApproval() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();

        FOCApprovalRequestVO focApprovalRequest = new FOCApprovalRequestVO();
        List<FOCApprovalData> focApprovalRequests = new ArrayList<>();

        FOCApprovalData fOCApprovalData = new FOCApprovalData();

        fOCApprovalData.setExtNwCode("String");
        fOCApprovalData.setCurrentStatus("String");
        fOCApprovalData.setStatus("Y");
        fOCApprovalData.setToMsisdn("9999999999");
        fOCApprovalData.setRemarks("String");
        fOCApprovalData.setExtTxnNumber("123");
        fOCApprovalData.setRefNumber("String");
        fOCApprovalData.setTxnId("String");
        fOCApprovalData.setLanguage1("String");
        fOCApprovalData.setLanguage2("String");


        focApprovalRequests.add(fOCApprovalData);

        focApprovalRequest.setFocApprovalRequests(focApprovalRequests);
        HttpHeaders headers = new HttpHeaders();
        CustomResponseWrapper response = mock(CustomResponseWrapper.class);
        doNothing().when(response).setStatus(Mockito.anyInt());


        //          System.out.println("data " + data.getLoginid());
        fOCApprovalServiceImpl.processFOCApproval(focApprovalRequest, headers, response);


    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#processFOCApproval(FOCApprovalRequestVO, MultiValueMap, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessFOCApproval2() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();

        FOCApprovalRequestVO focApprovalRequest = mock(FOCApprovalRequestVO.class);
        doNothing().when(focApprovalRequest).setFocApprovalRequests(Mockito.<List<FOCApprovalData>>any());
        focApprovalRequest.setFocApprovalRequests(new ArrayList<>());
        HttpHeaders headers = new HttpHeaders();
        fOCApprovalServiceImpl.processFOCApproval(focApprovalRequest, headers, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#processFOCApproval(FOCApprovalRequestVO, MultiValueMap, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessFOCApproval3() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();

        FOCApprovalRequestVO focApprovalRequest = mock(FOCApprovalRequestVO.class);
        doNothing().when(focApprovalRequest).setFocApprovalRequests(Mockito.<List<FOCApprovalData>>any());
        focApprovalRequest.setFocApprovalRequests(new ArrayList<>());
        HttpHeaders headers = new HttpHeaders();
        fOCApprovalServiceImpl.processFOCApproval(focApprovalRequest, headers,
                new CustomResponseWrapper(new MockHttpServletResponse()));
    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#processFOCApproval(FOCApprovalRequestVO, MultiValueMap, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessFOCApproval4() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();

        FOCApprovalRequestVO focApprovalRequest = mock(FOCApprovalRequestVO.class);
        doNothing().when(focApprovalRequest).setFocApprovalRequests(Mockito.<List<FOCApprovalData>>any());
        focApprovalRequest.setFocApprovalRequests(new ArrayList<>());
        HttpHeaders headers = new HttpHeaders();
        CustomResponseWrapper response = mock(CustomResponseWrapper.class);
        doNothing().when(response).setStatus(anyInt());
        fOCApprovalServiceImpl.processFOCApproval(focApprovalRequest, headers, new CustomResponseWrapper(response));
    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#processFOCApproval(FOCApprovalRequestVO, MultiValueMap, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessFOCApproval5() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        FOCApprovalRequestVO focApprovalRequest = mock(FOCApprovalRequestVO.class);
        doNothing().when(focApprovalRequest).setFocApprovalRequests(Mockito.<List<FOCApprovalData>>any());
        focApprovalRequest.setFocApprovalRequests(new ArrayList<>());
        fOCApprovalServiceImpl.processFOCApproval(focApprovalRequest, new HttpHeaders(), null);
    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#processFOCApproval(FOCApprovalRequestVO, MultiValueMap, HttpServletResponse)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testProcessFOCApproval6() throws BTSLBaseException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.o2c.service.FOCApprovalServiceImpl.processFOCApproval(FOCApprovalServiceImpl.java:222)
        //   See https://diff.blue/R013 to resolve this issue.

        FOCApprovalRequestVO focApprovalRequest = new FOCApprovalRequestVO();
        focApprovalRequest.setFocApprovalRequests(new ArrayList<>());
        HttpHeaders headers = new HttpHeaders();
        fOCApprovalServiceImpl.processFOCApproval(focApprovalRequest, headers, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#processFOCApproval(FOCApprovalRequestVO, MultiValueMap, HttpServletResponse)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testProcessFOCApproval7() throws BTSLBaseException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.o2c.service.FOCApprovalServiceImpl.processFOCApproval(FOCApprovalServiceImpl.java:222)
        //   See https://diff.blue/R013 to resolve this issue.

        FOCApprovalRequestVO focApprovalRequest = mock(FOCApprovalRequestVO.class);
        doNothing().when(focApprovalRequest).setFocApprovalRequests(Mockito.<List<FOCApprovalData>>any());
        focApprovalRequest.setFocApprovalRequests(new ArrayList<>());
        HttpHeaders headers = new HttpHeaders();
        fOCApprovalServiceImpl.processFOCApproval(focApprovalRequest, headers, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#processFOCApproval(FOCApprovalRequestVO, MultiValueMap, HttpServletResponse)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testProcessFOCApproval8() throws BTSLBaseException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.FOCApprovalServiceImpl.processFOCApproval(FOCApprovalServiceImpl.java:214)
        //   See https://diff.blue/R013 to resolve this issue.

        FOCApprovalRequestVO focApprovalRequest = mock(FOCApprovalRequestVO.class);
        doNothing().when(focApprovalRequest).setFocApprovalRequests(Mockito.<List<FOCApprovalData>>any());
        focApprovalRequest.setFocApprovalRequests(new ArrayList<>());
        HttpHeaders headers = new HttpHeaders();
        fOCApprovalServiceImpl.processFOCApproval(focApprovalRequest, headers,
                new CustomResponseWrapper(new MockHttpServletResponse()));
    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#processFOCApproval(FOCApprovalRequestVO, MultiValueMap, HttpServletResponse)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testProcessFOCApproval9() throws BTSLBaseException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.FOCApprovalServiceImpl.processFOCApproval(FOCApprovalServiceImpl.java:214)
        //   See https://diff.blue/R013 to resolve this issue.

        FOCApprovalRequestVO focApprovalRequest = mock(FOCApprovalRequestVO.class);
        doNothing().when(focApprovalRequest).setFocApprovalRequests(Mockito.<List<FOCApprovalData>>any());
        focApprovalRequest.setFocApprovalRequests(new ArrayList<>());
        HttpHeaders headers = new HttpHeaders();
        CustomResponseWrapper response = mock(CustomResponseWrapper.class);
        doNothing().when(response).setStatus(anyInt());
        fOCApprovalServiceImpl.processFOCApproval(focApprovalRequest, headers, new CustomResponseWrapper(response));
    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#processFOCApproval(FOCApprovalRequestVO, MultiValueMap, HttpServletResponse)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testProcessFOCApproval10() throws BTSLBaseException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.FOCApprovalServiceImpl.processFOCApproval(FOCApprovalServiceImpl.java:222)
        //   See https://diff.blue/R013 to resolve this issue.

        FOCApprovalRequestVO focApprovalRequest = mock(FOCApprovalRequestVO.class);
        doNothing().when(focApprovalRequest).setFocApprovalRequests(Mockito.<List<FOCApprovalData>>any());
        focApprovalRequest.setFocApprovalRequests(new ArrayList<>());
        fOCApprovalServiceImpl.processFOCApproval(focApprovalRequest, new HttpHeaders(), null);
    }

    /**
     * Method under test: {@link FOCApprovalServiceImpl#viewFOCTransferDetails(ApprovalVO, Locale)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testViewFOCTransferDetails() throws Exception {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.ApprovalVO.<init>(ApprovalVO.java:94)
        //   See https://diff.blue/R013 to resolve this issue.

        ApprovalVO theForm = new ApprovalVO();
        theForm.setAddress("42 Main St");
        theForm.setApprove1Remark("Approve1 Remark");
        theForm.setApprove2Remark("Approve2 Remark");
        theForm.setApprove3Remark("Approve3 Remark");
        theForm.setCategoryCode("Category Code");
        theForm.setCategoryCodeDesc("Category Code Desc");
        theForm.setCategoryList(new ArrayList());
        theForm.setChannelTransferVO(ChannelTransferVO.getInstance());
        theForm.setChannelUserID("Channel User ID");
        theForm.setChannelUserName("janedoe");
        theForm.setChannelUserStatus("Channel User Status");
        theForm.setCurrentDate("2020-03-01");
        theForm.setDefaultLang("Default Lang");
        theForm.setDomainCode("Domain Code");
        theForm.setDomainCodeDesc("Domain Code Desc");
        theForm.setDomainList(new ArrayList());
        theForm.setDomainTypeCode("Domain Type Code");
        theForm.setErpCode("Erp Code");
        theForm.setExternalTxnDate("2020-03-01");
        theForm.setExternalTxnExist("External Txn Exist");
        theForm.setExternalTxnMandatory("External Txn Mandatory");
        theForm.setExternalTxnNum("External Txn Num");
        theForm.setFocOrderApprovalLevel(42);
        theForm.setGeoDomainCodeDescForUser("Geo Domain Code Desc For User");
        theForm.setGeographicalDomainCode("Geographical Domain Code");
        theForm.setGeographicalDomainCodeDesc("Geographical Domain Code Desc");
        theForm.setGeographicalDomainList(new ArrayList());
        theForm.setMultiWallet(true);
        theForm.setNetCommissionQuantity("Net Commission Quantity");
        theForm.setNetworkCode("Network Code");
        theForm.setNetworkCodeDesc("Network Code Desc");
        theForm.setProductListWithTaxes(new ArrayList());
        theForm.setProductQuantity("Product Quantity");
        theForm.setProductTypeCode("Product Type Code");
        theForm.setProductTypeCodeDesc("Product Type Code Desc");
        theForm.setProductTypeList(new ArrayList());
        theForm.setReceiverCrQuantity("Receiver Cr Quantity");
        theForm.setReference("Reference");
        theForm.setRefrenceNum("Refrence Num");
        theForm.setRemarks("Remarks");
        theForm.setRequestType("Request Type");
        theForm.setSearchCriteria("Search Criteria");
        theForm.setSearchUserID("Search User ID");
        theForm.setSearchUserName("janedoe");
        theForm.setSecondLang("Second Lang");
        theForm.setSenderDrQuantity("Sender Dr Quantity");
        theForm.setSessionDomainCode("Session Domain Code");
        theForm.setStatus("Status");
        theForm.setToPrimaryMSISDN("To Primary MSISDN");
        theForm.setTotalMRP("Total MRP");
        theForm.setTotalReqQty("Total Req Qty");
        theForm.setTotalStock("Total Stock");
        theForm.setTotalTax1("Total Tax1");
        theForm.setTotalTax2("Total Tax2");
        theForm.setTotalTransferedAmount("10");
        theForm.setTransferCategory("Transfer Category");
        theForm.setTransferDateAsString("2020-03-01");
        theForm.setTransferItemList(new ArrayList());
        theForm.setTransferNumber("42");
        theForm.setUserCode("User Code");
        theForm.setUserCommProfileName("foo.txt");
        theForm.setUserCommProfileSetID("User Comm Profile Set ID");
        theForm.setUserCommProfileSetVersion("1.0.2");
        theForm.setUserGradeName("User Grade Name");
        theForm.setUserID("User ID");
        theForm.setUserList(new ArrayList());
        theForm.setUserMsisdn("User Msisdn");
        theForm.setUserName("janedoe");
        theForm.setUserTransferProfileCode("User Transfer Profile Code");
        theForm.setUserTransferProfileName("foo.txt");
        theForm.setWalletCode("Wallet Code");
        theForm.setWalletTypeList(new ArrayList<>());
        theForm.setWalletTypeWithWalletCode("Wallet Type With Wallet Code");
        fOCApprovalServiceImpl.viewFOCTransferDetails(theForm, Locale.getDefault());
    }
}

