package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.businesslogic.BatchO2CItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.BatchO2CMasterVO;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.junit.Test;

public class O2CBatchApprovalDetailsResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CBatchApprovalDetailsResponse}
     *   <li>{@link O2CBatchApprovalDetailsResponse#setApprovalDetails(BatchO2CMasterVO)}
     *   <li>{@link O2CBatchApprovalDetailsResponse#setFileAttachment(String)}
     *   <li>{@link O2CBatchApprovalDetailsResponse#setFileName(String)}
     *   <li>{@link O2CBatchApprovalDetailsResponse#setFileType(String)}
     *   <li>{@link O2CBatchApprovalDetailsResponse#getApprovalDetails()}
     *   <li>{@link O2CBatchApprovalDetailsResponse#getFileAttachment()}
     *   <li>{@link O2CBatchApprovalDetailsResponse#getFileName()}
     *   <li>{@link O2CBatchApprovalDetailsResponse#getFileType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CBatchApprovalDetailsResponse actualO2cBatchApprovalDetailsResponse = new O2CBatchApprovalDetailsResponse();
        BatchO2CItemsVO itemsVO = new BatchO2CItemsVO();
        itemsVO.setBatchDetailId("42");
        itemsVO.setBatchId("42");
        itemsVO.setBatchO2CItems(new HashMap<>());
        itemsVO.setBonusType("Bonus Type");
        itemsVO.setCancelledBy("Cancelled By");
        itemsVO.setCancelledOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        itemsVO.setCategoryCode("Category Code");
        itemsVO.setCategoryName("Category Name");
        itemsVO.setCommCalReqd(true);
        itemsVO.setCommissionProfileDetailId("42");
        itemsVO.setCommissionProfileSetId("42");
        itemsVO.setCommissionProfileVer("Commission Profile Ver");
        itemsVO.setCommissionRate(10.0d);
        itemsVO.setCommissionType("Commission Type");
        itemsVO.setCommissionValue(42L);
        itemsVO.setDualCommissionType("Dual Commission Type");
        itemsVO.setExtTxnDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        itemsVO.setExtTxnDateStr("2020-03-01");
        itemsVO.setExtTxnNo("Ext Txn No");
        itemsVO.setExternalCode("External Code");
        itemsVO.setFirstApprovedBy("First Approved By");
        itemsVO.setFirstApprovedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        itemsVO.setFirstApprovedQuantity(1L);
        itemsVO.setFirstApproverName("Jane");
        itemsVO.setFirstApproverRemarks("First Approver Remarks");
        itemsVO.setGradeCode("Grade Code");
        itemsVO.setGradeName("Grade Name");
        itemsVO.setInitiatedBy("Initiated By");
        itemsVO.setInitiatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        itemsVO.setInitiaterName("Initiater Name");
        itemsVO.setInitiatorRemarks("Initiator Remarks");
        itemsVO.setLoginID("Login ID");
        itemsVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        itemsVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        itemsVO.setMsisdn("Msisdn");
        itemsVO.setNetPayableAmount(1L);
        itemsVO.setPayableAmount(1L);
        itemsVO.setPaymentType("Payment Type");
        itemsVO.setRcrdStatus("Rcrd Status");
        itemsVO.setRecordNumber(10);
        itemsVO.setReferenceNo("Reference No");
        itemsVO.setRequestedQuantity(1L);
        itemsVO.setSecondApprQty(1L);
        itemsVO.setSecondApprovedBy("Second Approved By");
        itemsVO.setSecondApprovedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        itemsVO.setSecondApproverName("Second Approver Name");
        itemsVO.setSecondApproverRemarks("Second Approver Remarks");
        itemsVO.setSlabDefine(true);
        itemsVO.setStatus("Status");
        itemsVO.setTax1Rate(10.0d);
        itemsVO.setTax1Type("Tax1 Type");
        itemsVO.setTax1Value(42L);
        itemsVO.setTax2Rate(10.0d);
        itemsVO.setTax2Type("Tax2 Type");
        itemsVO.setTax2Value(42L);
        itemsVO.setTax3Rate(10.0d);
        itemsVO.setTax3Type("Tax3 Type");
        itemsVO.setTax3Value(42L);
        itemsVO.setThirdApprovedBy("Third Approved By");
        itemsVO.setThirdApprovedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        itemsVO.setThirdApproverRemarks("Third Approver Remarks");
        itemsVO.setTransferDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        itemsVO.setTransferDateStr("2020-03-01");
        itemsVO.setTransferMrp(1L);
        itemsVO.setTxnProfile("Txn Profile");
        itemsVO.setUserCategory("User Category");
        itemsVO.setUserGradeCode("User Grade Code");
        itemsVO.setUserId("42");
        itemsVO.setUserName("janedoe");
        itemsVO.setUserStatus("User Status");
        itemsVO.setWalletType("Wallet Type");
        BatchO2CMasterVO approvalDetails = new BatchO2CMasterVO();
        approvalDetails.setBatchDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        approvalDetails.setBatchDateStr("2020-03-01");
        approvalDetails.setBatchFileName("foo.txt");
        approvalDetails.setBatchId("42");
        approvalDetails.setBatchName("Batch Name");
        approvalDetails.setBatchO2CItemsVO(itemsVO);
        approvalDetails.setBatchTotalRecord(1);
        approvalDetails.setClosedRecords(1);
        approvalDetails.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        approvalDetails.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        approvalDetails.setDefaultLang("Default Lang");
        approvalDetails.setDomainCode("Domain Code");
        approvalDetails.setDomainCodeDesc("Domain Code Desc");
        approvalDetails.setDownLoadDataMap(new LinkedHashMap());
        approvalDetails.setGeographyList(new ArrayList());
        approvalDetails.setLevel1ApprovedRecords(1);
        approvalDetails.setLevel2ApprovedRecords(1);
        approvalDetails.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        approvalDetails
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        approvalDetails.setNetworkCode("Network Code");
        approvalDetails.setNetworkCodeFor("Network Code For");
        approvalDetails.setNewRecords(1);
        approvalDetails.setProductCode("Product Code");
        approvalDetails.setProductCodeDesc("Product Code Desc");
        approvalDetails.setProductMrp(1L);
        approvalDetails.setProductMrpStr("Product Mrp Str");
        approvalDetails.setProductName("Product Name");
        approvalDetails.setProductShortName("Product Short Name");
        approvalDetails.setProductType("Product Type");
        approvalDetails.setRejectedRecords(1);
        approvalDetails.setSecondLang("Second Lang");
        approvalDetails.setStatus("Status");
        approvalDetails.setStatusDesc("Status Desc");
        actualO2cBatchApprovalDetailsResponse.setApprovalDetails(approvalDetails);
        actualO2cBatchApprovalDetailsResponse.setFileAttachment("File Attachment");
        actualO2cBatchApprovalDetailsResponse.setFileName("foo.txt");
        actualO2cBatchApprovalDetailsResponse.setFileType("File Type");
        assertSame(approvalDetails, actualO2cBatchApprovalDetailsResponse.getApprovalDetails());
        assertEquals("File Attachment", actualO2cBatchApprovalDetailsResponse.getFileAttachment());
        assertEquals("foo.txt", actualO2cBatchApprovalDetailsResponse.getFileName());
        assertEquals("File Type", actualO2cBatchApprovalDetailsResponse.getFileType());
    }
}

