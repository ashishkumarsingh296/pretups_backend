package com.restapi.o2c.service;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.XssWrapper;
import com.btsl.pretups.channel.transfer.businesslogic.BatchO2CItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.BatchO2CMasterVO;
import com.btsl.pretups.filters.OwnHttpRequestWrapper;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.util.JUnitConfig;
import com.restapi.o2c.service.bulko2capprovalrequestvo.BulkO2CApprovalRequestVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {O2CBatchApprovalServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class O2CBatchApprovalServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private O2CBatchApprovalServiceImpl o2CBatchApprovalServiceImpl;

    /**
     * Method under test: {@link O2CBatchApprovalServiceImpl#getBulkO2CApprovalList(BulkO2CApprovalRequestVO, String, Locale, O2CApprovalListVO, HttpServletResponse, Connection)}
     */
    @Test
    public void testGetBulkO2CApprovalList() throws BTSLBaseException, SQLException {
        BulkO2CApprovalRequestVO bulkO2CApprovalRequestVO = new BulkO2CApprovalRequestVO();
        bulkO2CApprovalRequestVO.setApprovalLevel("Approval Level");
        bulkO2CApprovalRequestVO.setApprovalType("Approval Type");
        bulkO2CApprovalRequestVO.setCategory("Category");
        bulkO2CApprovalRequestVO.setDomain("Domain");
        bulkO2CApprovalRequestVO.setGeographicalDomain("Geographical Domain");
        Locale locale = Locale.getDefault();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        O2CApprovalListVO response = new O2CApprovalListVO();
        response.setBulkApprovalList(new HashMap<>());
        response.setErrorMap(errorMap);
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setO2cApprovalList(new ArrayList<>());
        response.setStatus(1);
        response.setTransactionId("42");
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
       // doNothing().when(con).close();
        thrown.expect(BTSLBaseException.class);
        o2CBatchApprovalServiceImpl.getBulkO2CApprovalList(bulkO2CApprovalRequestVO, "Msisdn", locale, response,
                responseSwag, JUnitConfig.getConnection());
       // verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
       // verify(con).close();
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet).getInt(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link O2CBatchApprovalServiceImpl#getBulkO2CApprovalList(BulkO2CApprovalRequestVO, String, Locale, O2CApprovalListVO, HttpServletResponse, Connection)}
     */
    @Test
    public void testGetBulkO2CApprovalList2() throws BTSLBaseException, SQLException {
        BulkO2CApprovalRequestVO bulkO2CApprovalRequestVO = new BulkO2CApprovalRequestVO();
        bulkO2CApprovalRequestVO.setApprovalLevel("1");
        bulkO2CApprovalRequestVO.setApprovalType("Approval Type");
        bulkO2CApprovalRequestVO.setCategory("Category");
        bulkO2CApprovalRequestVO.setDomain("Domain");
        bulkO2CApprovalRequestVO.setGeographicalDomain("Geographical Domain");
        Locale locale = Locale.getDefault();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        O2CApprovalListVO response = new O2CApprovalListVO();
        response.setBulkApprovalList(new HashMap<>());
        response.setErrorMap(errorMap);
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setO2cApprovalList(new ArrayList<>());
        response.setStatus(1);
        response.setTransactionId("42");
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        //doNothing().when(con).close();
        thrown.expect(BTSLBaseException.class);
        o2CBatchApprovalServiceImpl.getBulkO2CApprovalList(bulkO2CApprovalRequestVO, "Msisdn", locale, response,
                responseSwag, JUnitConfig.getConnection());
        //verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        //verify(con).close();
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet).getInt(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link O2CBatchApprovalServiceImpl#getBulkO2CApprovalList(BulkO2CApprovalRequestVO, String, Locale, O2CApprovalListVO, HttpServletResponse, Connection)}
     */
    @Test
    public void testGetBulkO2CApprovalList3() throws BTSLBaseException, SQLException {
        BulkO2CApprovalRequestVO bulkO2CApprovalRequestVO = new BulkO2CApprovalRequestVO();
        bulkO2CApprovalRequestVO.setApprovalLevel("2");
        bulkO2CApprovalRequestVO.setApprovalType("Approval Type");
        bulkO2CApprovalRequestVO.setCategory("Category");
        bulkO2CApprovalRequestVO.setDomain("Domain");
        bulkO2CApprovalRequestVO.setGeographicalDomain("Geographical Domain");
        Locale locale = Locale.getDefault();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        O2CApprovalListVO response = new O2CApprovalListVO();
        response.setBulkApprovalList(new HashMap<>());
        response.setErrorMap(errorMap);
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setO2cApprovalList(new ArrayList<>());
        response.setStatus(1);
        response.setTransactionId("42");
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
     //   doNothing().when(con).close();
        thrown.expect(BTSLBaseException.class);
        o2CBatchApprovalServiceImpl.getBulkO2CApprovalList(bulkO2CApprovalRequestVO, "Msisdn", locale, response,
                responseSwag, JUnitConfig.getConnection());
      //  verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
      //  verify(con).close();
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet).getInt(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link O2CBatchApprovalServiceImpl#getBulkO2CApprovalList(BulkO2CApprovalRequestVO, String, Locale, O2CApprovalListVO, HttpServletResponse, Connection)}
     */
    @Test
    public void testGetBulkO2CApprovalList4() throws BTSLBaseException, SQLException {
        BulkO2CApprovalRequestVO bulkO2CApprovalRequestVO = new BulkO2CApprovalRequestVO();
        bulkO2CApprovalRequestVO.setApprovalLevel("3");
        bulkO2CApprovalRequestVO.setApprovalType("Approval Type");
        bulkO2CApprovalRequestVO.setCategory("Category");
        bulkO2CApprovalRequestVO.setDomain("Domain");
        bulkO2CApprovalRequestVO.setGeographicalDomain("Geographical Domain");
        Locale locale = Locale.getDefault();

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        O2CApprovalListVO response = new O2CApprovalListVO();
        response.setBulkApprovalList(new HashMap<>());
        response.setErrorMap(errorMap);
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setO2cApprovalList(new ArrayList<>());
        response.setStatus(1);
        response.setTransactionId("42");
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new Response());
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
      //  doNothing().when(con).close();
        thrown.expect(BTSLBaseException.class);
        o2CBatchApprovalServiceImpl.getBulkO2CApprovalList(bulkO2CApprovalRequestVO, "Msisdn", locale, response,
                responseSwag, JUnitConfig.getConnection());
      //  verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
       // verify(con).close();
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet).getInt(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link O2CBatchApprovalServiceImpl#processO2CBatchApprovalDetails(Connection, O2CBatchApprovalDetailsRequestVO, String, Locale, O2CBatchApprovalDetailsResponse, HttpServletResponse, HttpServletRequest)}
     */
    @Test
    public void testProcessO2CBatchApprovalDetails() throws BTSLBaseException, SQLException {
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
    //    doNothing().when(con).close();

        O2CBatchApprovalDetailsRequestVO batchapprovalDetailsRequest = new O2CBatchApprovalDetailsRequestVO();
        batchapprovalDetailsRequest.setData(new O2CBatchApprovalDetails());
        Locale locale = Locale.getDefault();

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
        itemsVO
                .setSecondApprovedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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
        approvalDetails
                .setBatchDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        approvalDetails.setBatchDateStr("2020-03-01");
        approvalDetails.setBatchFileName("foo.txt");
        approvalDetails.setBatchId("42");
        approvalDetails.setBatchName("Batch Name");
        approvalDetails.setBatchO2CItemsVO(itemsVO);
        approvalDetails.setBatchTotalRecord(1);
        approvalDetails.setClosedRecords(1);
        approvalDetails.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        approvalDetails
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        O2CBatchApprovalDetailsResponse response = new O2CBatchApprovalDetailsResponse();
        response.setApprovalDetails(approvalDetails);
        response.setErrorMap(errorMap);
        response.setFileAttachment("File Attachment");
        response.setFileName("foo.txt");
        response.setFileType("File Type");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        CustomResponseWrapper response1 = new CustomResponseWrapper(new Response());
        thrown.expect(BTSLBaseException.class);
        o2CBatchApprovalServiceImpl.processO2CBatchApprovalDetails(JUnitConfig.getConnection(), batchapprovalDetailsRequest, "Msisdn", locale,
                response, response1, new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest())));
     //   verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
      //  verify(con).close();
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet).getInt(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getTimestamp(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(timestamp).getTime();
    }

    /**
     * Method under test: {@link O2CBatchApprovalServiceImpl#processO2CBatchApprovalDetails(Connection, O2CBatchApprovalDetailsRequestVO, String, Locale, O2CBatchApprovalDetailsResponse, HttpServletResponse, HttpServletRequest)}
     */
    @Test
    public void testProcessO2CBatchApprovalDetails2() throws BTSLBaseException, SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        //when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
   //     doNothing().when(con).close();

        O2CBatchApprovalDetailsRequestVO batchapprovalDetailsRequest = new O2CBatchApprovalDetailsRequestVO();
        batchapprovalDetailsRequest.setData(new O2CBatchApprovalDetails());
        Locale locale = Locale.getDefault();

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
        itemsVO
                .setSecondApprovedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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
        approvalDetails
                .setBatchDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        approvalDetails.setBatchDateStr("2020-03-01");
        approvalDetails.setBatchFileName("foo.txt");
        approvalDetails.setBatchId("42");
        approvalDetails.setBatchName("Batch Name");
        approvalDetails.setBatchO2CItemsVO(itemsVO);
        approvalDetails.setBatchTotalRecord(1);
        approvalDetails.setClosedRecords(1);
        approvalDetails.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        approvalDetails
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        O2CBatchApprovalDetailsResponse response = new O2CBatchApprovalDetailsResponse();
        response.setApprovalDetails(approvalDetails);
        response.setErrorMap(errorMap);
        response.setFileAttachment("File Attachment");
        response.setFileName("foo.txt");
        response.setFileType("File Type");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        CustomResponseWrapper response1 = new CustomResponseWrapper(new Response());
        thrown.expect(BTSLBaseException.class);
        o2CBatchApprovalServiceImpl.processO2CBatchApprovalDetails(JUnitConfig.getConnection(), batchapprovalDetailsRequest, "Msisdn", locale,
                response, response1, new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest())));
    //    verify(con).prepareStatement(Mockito.<String>any());
     //   verify(con).close();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }
}

