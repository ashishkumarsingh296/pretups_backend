package com.restapi.channelAdmin.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.JUnitConfig;
import com.restapi.channelAdmin.requestVO.BulkUserUploadRequestVO;
import com.restapi.channelAdmin.responseVO.BulkUserUploadResponseVO;

import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Ignore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.configuration.IMockitoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mockStatic;

@ContextConfiguration(classes = {ChannelAdminService.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChannelAdminServiceTest {
    @Autowired
    private ChannelAdminService channelAdminService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Method under test: {@link ChannelAdminService#initiateBulkUsersUpload(Connection, BulkUserUploadRequestVO, UserVO, BulkUserUploadResponseVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testInitiateBulkUsersUpload() throws BTSLBaseException, IOException, SQLException {
        //Connection con = null;
        BulkUserUploadRequestVO requestVO = new BulkUserUploadRequestVO();

        requestVO.setBatchName("1234");
        requestVO.setDomainCode("String");
        Date date = new Date() ;
        SimpleDateFormat sdf=new SimpleDateFormat("ddMMYYYYhhmmss");
        String dateString=sdf.format(date);


        requestVO.setFile(dateString);
        requestVO.setBatchName("String");
        requestVO.setFileType("xls");
        requestVO.setGeographyCode("String");
        requestVO.setFileName(dateString+".xls");

        UserVO sessionUserVO = new UserVO();
        ArrayList domainVOList = new ArrayList<>() ;

        ListValueVO lv = new ListValueVO();
        lv.setType("String");
        lv.setCodeName("String");
        domainVOList.add(lv) ;

        sessionUserVO.setDomainList(domainVOList);

//if (userType.equals(PretupsI.OPERATOR_USER_TYPE) || (userType.equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {

        sessionUserVO.setUserType(PretupsI.OPERATOR_USER_TYPE);
        sessionUserVO.setActiveUserID("Active User ID");
        sessionUserVO.setActiveUserLoginId("42");
        sessionUserVO.setActiveUserMsisdn("Active User Msisdn");
        sessionUserVO.setActiveUserPin("Active User Pin");
        sessionUserVO.setAddCommProfOTFDetailId("42");
        sessionUserVO.setAddress1("42 Main St");
        sessionUserVO.setAddress2("42 Main St");
        sessionUserVO.setAgentBalanceList(new ArrayList<>());
        sessionUserVO.setAllowedDay(new String[]{"Allowed Days"});
        sessionUserVO.setAllowedDays("Allowed Days");
        sessionUserVO.setAllowedIps("Allowed Ips");
        sessionUserVO.setAllowedUserTypeCreation("Allowed User Type Creations");
        sessionUserVO.setAppintmentDate("2020-03-01");
        sessionUserVO
                .setAppointmentDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setAssType("Ass Type");
        sessionUserVO.setAssoMsisdn("Asso Msisdn");
        sessionUserVO.setAssociatedGeographicalList(new ArrayList());
        sessionUserVO.setAssociatedProductTypeList(new ArrayList());
        sessionUserVO.setAssociatedServiceTypeList(new ArrayList());
        sessionUserVO
                .setAssociationCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setAssociationModifiedOn(
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setAuthType("Type");
        sessionUserVO.setAuthTypeAllowed("Type Allowed");
        sessionUserVO.setBatchID("Batch ID");
        sessionUserVO.setBatchName("Batch Name");
        sessionUserVO.setBrowserType("Browser Type");
        sessionUserVO
                .setC2sMisFromDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO
                .setC2sMisToDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        sessionUserVO.setCategoryCode("Category Code");
        sessionUserVO.setCategoryCodeDesc("Category Code Desc");




        BulkUserUploadResponseVO actualBulkUserUploadResponseVO = new BulkUserUploadResponseVO();
        actualBulkUserUploadResponseVO.setErrorFlag("An error occurred");
        ArrayList errorList = new ArrayList();
        actualBulkUserUploadResponseVO.setErrorList(errorList);
        actualBulkUserUploadResponseVO.setFileName("foo.txt");
        actualBulkUserUploadResponseVO.setFileType("File Type");
        actualBulkUserUploadResponseVO.setFileattachment("Fileattachment");
        actualBulkUserUploadResponseVO.setNoOfRecords("No Of Records");
        actualBulkUserUploadResponseVO.setTotalRecords(1);

        HttpServletResponse responseSwag = Mockito.mock(HttpServletResponse.class);

        Mockito.doNothing().when(responseSwag).setStatus(Mockito.anyInt());


        JUnitConfig.init();
//        mockStatic(BTSLUtil.class);
        //BTSLDateUtil.getGregorianDateInString
 //       Mockito.when(BTSLUtil.isValideFileName(Mockito.anyString())).thenReturn(true);


        // Act
        this.channelAdminService.initiateBulkUsersUpload(JUnitConfig.getConnection(), requestVO, sessionUserVO, actualBulkUserUploadResponseVO, responseSwag);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ChannelAdminService#uploadFileToServerWithHashMap(HashMap, String, String, byte[], String)}
     */
    @Test
    public void testUploadFileToServerWithHashMap() throws BTSLBaseException, UnsupportedEncodingException {
        HashMap<String, String> p_formFile = new HashMap<>();
        thrown.expect(BTSLBaseException.class);
        ChannelAdminService.uploadFileToServerWithHashMap(p_formFile, "P dir Path", "text/plain",
                "AXAXAXAX".getBytes("UTF-8"), "File Type");
    }

    /**
     * Method under test: {@link ChannelAdminService#uploadFileToServerWithHashMap(HashMap, String, String, byte[], String)}
     */
    @Test
    public void testUploadFileToServerWithHashMap2() throws BTSLBaseException, UnsupportedEncodingException {
        HashMap<String, String> p_formFile = new HashMap<>();
        p_formFile.put("foo", "foo");
        thrown.expect(BTSLBaseException.class);
        ChannelAdminService.uploadFileToServerWithHashMap(p_formFile, "P dir Path", "text/plain",
                "AXAXAXAX".getBytes("UTF-8"), "File Type");
    }
}

