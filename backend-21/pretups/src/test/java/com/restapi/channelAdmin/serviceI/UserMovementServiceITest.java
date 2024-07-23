package com.restapi.channelAdmin.serviceI;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.user.businesslogic.UserMigrationVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.util.JUnitConfig;
import com.restapi.channelAdmin.requestVO.UserMigrationRequestVO;
import com.restapi.channelAdmin.responseVO.UserMigrationResponseVO;
import com.restapi.user.service.FileDownloadResponse;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {UserMovementServiceI.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class UserMovementServiceITest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private UserMovementServiceI userMovementServiceI;

    /**
     * Method under test: {@link UserMovementServiceI#getUserMovementTemplate(OAuthUser, HttpServletResponse, Locale, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetUserMovementTemplate() throws SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.channelAdmin.serviceI.UserMovementServiceI.getUserMovementTemplate(UserMovementServiceI.java:94)

        // Arrange
        // TODO: Populate arranged inputs
        OAuthUser oAuthUser = null;
        HttpServletResponse responseSwag = null;
        Locale locale = null;
        String domainCode = "";

        // Act
        FileDownloadResponse actualUserMovementTemplate = this.userMovementServiceI.getUserMovementTemplate(oAuthUser,
                responseSwag, locale, domainCode);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link UserMovementServiceI#confirmUserMigration(Locale, UserMigrationRequestVO, HttpServletResponse, OAuthUser, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testConfirmUserMigration() throws SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.channelAdmin.serviceI.UserMovementServiceI.confirmUserMigration(UserMovementServiceI.java:215)

        // Arrange
        // TODO: Populate arranged inputs
        Locale locale = null;
        UserMigrationRequestVO requestVO = null;
        HttpServletResponse responseSwag = null;
        OAuthUser oAuthUser = null;
        String domainCode = "";

        // Act
        UserMigrationResponseVO actualConfirmUserMigrationResult = this.userMovementServiceI.confirmUserMigration(locale,
                requestVO, responseSwag, oAuthUser, domainCode);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link UserMovementServiceI#validateFileDetailsMap(HashMap)}
     */
    @Test
    public void testValidateFileDetailsMap() throws BTSLBaseException {
        thrown.expect(BTSLBaseException.class);
        userMovementServiceI.validateFileDetailsMap(new HashMap<>());
    }

    /**
     * Method under test: {@link UserMovementServiceI#validateFileDetailsMap(HashMap)}
     */
    @Test
    public void testValidateFileDetailsMap2() throws BTSLBaseException {
        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILENAME", "FILENAME");
        thrown.expect(BTSLBaseException.class);
        userMovementServiceI.validateFileDetailsMap(fileDetailsMap);
    }

    /**
     * Method under test: {@link UserMovementServiceI#validateFileDetailsMap(HashMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFileDetailsMap3() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.restapi.channelAdmin.serviceI.UserMovementServiceI.validateFileName(UserMovementServiceI.java:523)
        //       at com.restapi.channelAdmin.serviceI.UserMovementServiceI.validateFileDetailsMap(UserMovementServiceI.java:512)
        //   See https://diff.blue/R013 to resolve this issue.

        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILEATTACHMENT", "FILEATTACHMENT");
        fileDetailsMap.put("FILENAME", "FILENAME");
        userMovementServiceI.validateFileDetailsMap(fileDetailsMap);
    }

    /**
     * Method under test: {@link UserMovementServiceI#validateFileDetailsMap(HashMap)}
     */
    @Test
    public void testValidateFileDetailsMap4() throws BTSLBaseException {
        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("FILENAME", "");
        thrown.expect(BTSLBaseException.class);
        userMovementServiceI.validateFileDetailsMap(fileDetailsMap);
    }

    /**
     * Method under test: {@link UserMovementServiceI#validateFileName(String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFileName() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.restapi.channelAdmin.serviceI.UserMovementServiceI.validateFileName(UserMovementServiceI.java:523)
        //   See https://diff.blue/R013 to resolve this issue.

        userMovementServiceI.validateFileName("foo.txt");
    }

    /**
     * Method under test: {@link UserMovementServiceI#migrateUsers(Connection, List)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testMigrateUsers() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.user.businesslogic.UserMigrationDAO.userMigrationProcess(UserMigrationDAO.java:2238)
        //       at com.restapi.channelAdmin.serviceI.UserMovementServiceI.migrateUsers(UserMovementServiceI.java:542)
        //   See https://diff.blue/R013 to resolve this issue.

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).close();
        Connection pCon = mock(Connection.class);
        when(pCon.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        userMovementServiceI.migrateUsers(JUnitConfig.getConnection(), new ArrayList<>());
    }

    /**
     * Method under test: {@link UserMovementServiceI#migrateUsers(Connection, List)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testMigrateUsers2() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.user.businesslogic.UserMigrationDAO.userMigrationProcess(UserMigrationDAO.java:2238)
        //       at com.restapi.channelAdmin.serviceI.UserMovementServiceI.migrateUsers(UserMovementServiceI.java:542)
        //   See https://diff.blue/R013 to resolve this issue.

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doThrow(new SQLException()).when(preparedStatement).close();
        Connection pCon = mock(Connection.class);
        when(pCon.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        userMovementServiceI.migrateUsers(JUnitConfig.getConnection(), new ArrayList<>());
    }

    /**
     * Method under test: {@link UserMovementServiceI#migrateUsers(Connection, List)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testMigrateUsers3() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.user.businesslogic.UserMigrationDAO.userMigrationProcess(UserMigrationDAO.java:2238)
        //       at com.restapi.channelAdmin.serviceI.UserMovementServiceI.migrateUsers(UserMovementServiceI.java:542)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).clearParameters();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection pCon = mock(Connection.class);
        when(pCon.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        UserMigrationVO userMigrationVO = new UserMigrationVO();
        userMigrationVO.setCountry("GB");
        userMigrationVO.setDomainID("migrateUsers");
        userMigrationVO.setFromParentCatCode("jane.doe@example.org");
        userMigrationVO.setFromParentGeoCode("jane.doe@example.org");
        userMigrationVO.setFromParentMsisdn("jane.doe@example.org");
        userMigrationVO.setFromUserCatCode("jane.doe@example.org");
        userMigrationVO.setFromUserGeoCode("jane.doe@example.org");
        userMigrationVO.setFromUserID("jane.doe@example.org");
        userMigrationVO.setFromUserLoginID("jane.doe@example.org");
        userMigrationVO.setFromUserMsisdn("jane.doe@example.org");
        userMigrationVO.setFromUserName("janedoe");
        userMigrationVO.setFromUserParentName("jane.doe@example.org");
        userMigrationVO.setFromUserReferenceID("jane.doe@example.org");
        userMigrationVO.setFromUserStatus("jane.doe@example.org");
        userMigrationVO.setLineNumber(2);
        userMigrationVO.setMessage("Not all who wander are lost");
        userMigrationVO.setMsisdn("migrateUsers");
        userMigrationVO.setNetworkCode("migrateUsers");
        userMigrationVO.setNetworkID("migrateUsers");
        userMigrationVO.setParentExist(true);
        userMigrationVO.setPhoneLang("6625550144");
        userMigrationVO.setRecordNumber("42");
        userMigrationVO.setToGeoDomainType("migrateUsers");
        userMigrationVO.setToOwnerID("migrateUsers");
        userMigrationVO.setToParentCatCode("migrateUsers");
        userMigrationVO.setToParentGeoCode("migrateUsers");
        userMigrationVO.setToParentID("migrateUsers");
        userMigrationVO.setToParentMsisdn("migrateUsers");
        userMigrationVO.setToUserCatCode("migrateUsers");
        userMigrationVO.setToUserCatCodeSeqNo("migrateUsers");
        userMigrationVO.setToUserGeoCode("migrateUsers");
        userMigrationVO.setToUserMsisdn("migrateUsers");
        userMigrationVO.setToUserParentName("migrateUsers");
        userMigrationVO.setUserID("migrateUsers");

        ArrayList<UserMigrationVO> pUserMigrationList = new ArrayList<>();
        pUserMigrationList.add(userMigrationVO);
        userMovementServiceI.migrateUsers(JUnitConfig.getConnection(), pUserMigrationList);
    }

    /**
     * Method under test: {@link UserMovementServiceI#migrateUsers(Connection, List)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testMigrateUsers4() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.user.businesslogic.UserMigrationDAO.userMigrationProcess(UserMigrationDAO.java:2238)
        //       at com.restapi.channelAdmin.serviceI.UserMovementServiceI.migrateUsers(UserMovementServiceI.java:542)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).clearParameters();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection pCon = mock(Connection.class);
        doNothing().when(pCon).rollback();
        when(pCon.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        UserMigrationVO userMigrationVO = new UserMigrationVO();
        userMigrationVO.setCountry("GB");
        userMigrationVO.setDomainID("migrateUsers");
        userMigrationVO.setFromParentCatCode("jane.doe@example.org");
        userMigrationVO.setFromParentGeoCode("jane.doe@example.org");
        userMigrationVO.setFromParentMsisdn("jane.doe@example.org");
        userMigrationVO.setFromUserCatCode("jane.doe@example.org");
        userMigrationVO.setFromUserGeoCode("jane.doe@example.org");
        userMigrationVO.setFromUserID("jane.doe@example.org");
        userMigrationVO.setFromUserLoginID("jane.doe@example.org");
        userMigrationVO.setFromUserMsisdn("jane.doe@example.org");
        userMigrationVO.setFromUserName("janedoe");
        userMigrationVO.setFromUserParentName("jane.doe@example.org");
        userMigrationVO.setFromUserReferenceID("jane.doe@example.org");
        userMigrationVO.setFromUserStatus("jane.doe@example.org");
        userMigrationVO.setLineNumber(2);
        userMigrationVO.setMessage("Not all who wander are lost");
        userMigrationVO.setMsisdn("migrateUsers");
        userMigrationVO.setNetworkCode("migrateUsers");
        userMigrationVO.setNetworkID("migrateUsers");
        userMigrationVO.setParentExist(true);
        userMigrationVO.setPhoneLang("6625550144");
        userMigrationVO.setRecordNumber("42");
        userMigrationVO.setToGeoDomainType("migrateUsers");
        userMigrationVO.setToOwnerID("migrateUsers");
        userMigrationVO.setToParentCatCode("migrateUsers");
        userMigrationVO.setToParentGeoCode("migrateUsers");
        userMigrationVO.setToParentID("migrateUsers");
        userMigrationVO.setToParentMsisdn("migrateUsers");
        userMigrationVO.setToUserCatCode("migrateUsers");
        userMigrationVO.setToUserCatCodeSeqNo("migrateUsers");
        userMigrationVO.setToUserGeoCode("migrateUsers");
        userMigrationVO.setToUserMsisdn("migrateUsers");
        userMigrationVO.setToUserParentName("migrateUsers");
        userMigrationVO.setUserID("migrateUsers");

        ArrayList<UserMigrationVO> pUserMigrationList = new ArrayList<>();
        pUserMigrationList.add(userMigrationVO);
        userMovementServiceI.migrateUsers(JUnitConfig.getConnection(), pUserMigrationList);
    }

    /**
     * Method under test: {@link UserMovementServiceI#migrateUsers(Connection, List)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testMigrateUsers5() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.user.businesslogic.UserMigrationDAO.userMigrationProcess(UserMigrationDAO.java:2238)
        //       at com.restapi.channelAdmin.serviceI.UserMovementServiceI.migrateUsers(UserMovementServiceI.java:542)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).clearParameters();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection pCon = mock(Connection.class);
        doThrow(new SQLException()).when(pCon).rollback();
        when(pCon.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        UserMigrationVO userMigrationVO = new UserMigrationVO();
        userMigrationVO.setCountry("GB");
        userMigrationVO.setDomainID("migrateUsers");
        userMigrationVO.setFromParentCatCode("jane.doe@example.org");
        userMigrationVO.setFromParentGeoCode("jane.doe@example.org");
        userMigrationVO.setFromParentMsisdn("jane.doe@example.org");
        userMigrationVO.setFromUserCatCode("jane.doe@example.org");
        userMigrationVO.setFromUserGeoCode("jane.doe@example.org");
        userMigrationVO.setFromUserID("jane.doe@example.org");
        userMigrationVO.setFromUserLoginID("jane.doe@example.org");
        userMigrationVO.setFromUserMsisdn("jane.doe@example.org");
        userMigrationVO.setFromUserName("janedoe");
        userMigrationVO.setFromUserParentName("jane.doe@example.org");
        userMigrationVO.setFromUserReferenceID("jane.doe@example.org");
        userMigrationVO.setFromUserStatus("jane.doe@example.org");
        userMigrationVO.setLineNumber(2);
        userMigrationVO.setMessage("Not all who wander are lost");
        userMigrationVO.setMsisdn("migrateUsers");
        userMigrationVO.setNetworkCode("migrateUsers");
        userMigrationVO.setNetworkID("migrateUsers");
        userMigrationVO.setParentExist(true);
        userMigrationVO.setPhoneLang("6625550144");
        userMigrationVO.setRecordNumber("42");
        userMigrationVO.setToGeoDomainType("migrateUsers");
        userMigrationVO.setToOwnerID("migrateUsers");
        userMigrationVO.setToParentCatCode("migrateUsers");
        userMigrationVO.setToParentGeoCode("migrateUsers");
        userMigrationVO.setToParentID("migrateUsers");
        userMigrationVO.setToParentMsisdn("migrateUsers");
        userMigrationVO.setToUserCatCode("migrateUsers");
        userMigrationVO.setToUserCatCodeSeqNo("migrateUsers");
        userMigrationVO.setToUserGeoCode("migrateUsers");
        userMigrationVO.setToUserMsisdn("migrateUsers");
        userMigrationVO.setToUserParentName("migrateUsers");
        userMigrationVO.setUserID("migrateUsers");

        ArrayList<UserMigrationVO> pUserMigrationList = new ArrayList<>();
        pUserMigrationList.add(userMigrationVO);
        userMovementServiceI.migrateUsers(JUnitConfig.getConnection(), pUserMigrationList);
    }

    /**
     * Method under test: {@link UserMovementServiceI#migrateUsers(Connection, List)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testMigrateUsers6() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.user.businesslogic.UserMigrationDAO.userMigrationProcess(UserMigrationDAO.java:2238)
        //       at com.restapi.channelAdmin.serviceI.UserMovementServiceI.migrateUsers(UserMovementServiceI.java:542)
        //   See https://diff.blue/R013 to resolve this issue.

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doNothing().when(preparedStatement).clearParameters();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection pCon = mock(Connection.class);
        doNothing().when(pCon).rollback();
        when(pCon.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        UserMigrationVO userMigrationVO = new UserMigrationVO();
        userMigrationVO.setCountry("GB");
        userMigrationVO.setDomainID("migrateUsers");
        userMigrationVO.setFromParentCatCode("jane.doe@example.org");
        userMigrationVO.setFromParentGeoCode("jane.doe@example.org");
        userMigrationVO.setFromParentMsisdn("jane.doe@example.org");
        userMigrationVO.setFromUserCatCode("jane.doe@example.org");
        userMigrationVO.setFromUserGeoCode("jane.doe@example.org");
        userMigrationVO.setFromUserID("jane.doe@example.org");
        userMigrationVO.setFromUserLoginID("jane.doe@example.org");
        userMigrationVO.setFromUserMsisdn("jane.doe@example.org");
        userMigrationVO.setFromUserName("janedoe");
        userMigrationVO.setFromUserParentName("jane.doe@example.org");
        userMigrationVO.setFromUserReferenceID("jane.doe@example.org");
        userMigrationVO.setFromUserStatus("jane.doe@example.org");
        userMigrationVO.setLineNumber(2);
        userMigrationVO.setMessage("Not all who wander are lost");
        userMigrationVO.setMsisdn("migrateUsers");
        userMigrationVO.setNetworkCode("migrateUsers");
        userMigrationVO.setNetworkID("migrateUsers");
        userMigrationVO.setParentExist(true);
        userMigrationVO.setPhoneLang("6625550144");
        userMigrationVO.setRecordNumber("42");
        userMigrationVO.setToGeoDomainType("migrateUsers");
        userMigrationVO.setToOwnerID("migrateUsers");
        userMigrationVO.setToParentCatCode("migrateUsers");
        userMigrationVO.setToParentGeoCode("migrateUsers");
        userMigrationVO.setToParentID("migrateUsers");
        userMigrationVO.setToParentMsisdn("migrateUsers");
        userMigrationVO.setToUserCatCode("migrateUsers");
        userMigrationVO.setToUserCatCodeSeqNo("migrateUsers");
        userMigrationVO.setToUserGeoCode("migrateUsers");
        userMigrationVO.setToUserMsisdn("migrateUsers");
        userMigrationVO.setToUserParentName("migrateUsers");
        userMigrationVO.setUserID("migrateUsers");

        UserMigrationVO userMigrationVO2 = new UserMigrationVO();
        userMigrationVO2.setCountry("GBR");
        userMigrationVO2.setDomainID("Entered with User List Size=");
        userMigrationVO2.setFromParentCatCode("migrateUsers");
        userMigrationVO2.setFromParentGeoCode("migrateUsers");
        userMigrationVO2.setFromParentMsisdn("migrateUsers");
        userMigrationVO2.setFromUserCatCode("migrateUsers");
        userMigrationVO2.setFromUserGeoCode("migrateUsers");
        userMigrationVO2.setFromUserID("migrateUsers");
        userMigrationVO2.setFromUserLoginID("migrateUsers");
        userMigrationVO2.setFromUserMsisdn("migrateUsers");
        userMigrationVO2.setFromUserName("jane.doe@example.org");
        userMigrationVO2.setFromUserParentName("migrateUsers");
        userMigrationVO2.setFromUserReferenceID("migrateUsers");
        userMigrationVO2.setFromUserStatus("migrateUsers");
        userMigrationVO2.setLineNumber(10);
        userMigrationVO2.setMessage("migrateUsers");
        userMigrationVO2.setMsisdn("Entered with User List Size=");
        userMigrationVO2.setNetworkCode("Entered with User List Size=");
        userMigrationVO2.setNetworkID("Entered with User List Size=");
        userMigrationVO2.setParentExist(false);
        userMigrationVO2.setPhoneLang("8605550118");
        userMigrationVO2.setRecordNumber("migrateUsers");
        userMigrationVO2.setToGeoDomainType("Entered with User List Size=");
        userMigrationVO2.setToOwnerID("Entered with User List Size=");
        userMigrationVO2.setToParentCatCode("Entered with User List Size=");
        userMigrationVO2.setToParentGeoCode("Entered with User List Size=");
        userMigrationVO2.setToParentID("Entered with User List Size=");
        userMigrationVO2.setToParentMsisdn("Entered with User List Size=");
        userMigrationVO2.setToUserCatCode("Entered with User List Size=");
        userMigrationVO2.setToUserCatCodeSeqNo("Entered with User List Size=");
        userMigrationVO2.setToUserGeoCode("Entered with User List Size=");
        userMigrationVO2.setToUserMsisdn("Entered with User List Size=");
        userMigrationVO2.setToUserParentName("Entered with User List Size=");
        userMigrationVO2.setUserID("Entered with User List Size=");

        UserMigrationVO userMigrationVO3 = new UserMigrationVO();
        userMigrationVO3.setCountry("GB");
        userMigrationVO3.setDomainID(" domainid");
        userMigrationVO3.setFromParentCatCode("jane.doe@example.org");
        userMigrationVO3.setFromParentGeoCode("jane.doe@example.org");
        userMigrationVO3.setFromParentMsisdn("jane.doe@example.org");
        userMigrationVO3.setFromUserCatCode("jane.doe@example.org");
        userMigrationVO3.setFromUserGeoCode("jane.doe@example.org");
        userMigrationVO3.setFromUserID("jane.doe@example.org");
        userMigrationVO3.setFromUserLoginID("jane.doe@example.org");
        userMigrationVO3.setFromUserMsisdn("jane.doe@example.org");
        userMigrationVO3.setFromUserName("janedoe");
        userMigrationVO3.setFromUserParentName("jane.doe@example.org");
        userMigrationVO3.setFromUserReferenceID("jane.doe@example.org");
        userMigrationVO3.setFromUserStatus("jane.doe@example.org");
        userMigrationVO3.setLineNumber(2);
        userMigrationVO3.setMessage("Not all who wander are lost");
        userMigrationVO3.setMsisdn(" msisdn");
        userMigrationVO3.setNetworkCode("Network Code");
        userMigrationVO3.setNetworkID(" networkid");
        userMigrationVO3.setParentExist(false);
        userMigrationVO3.setPhoneLang("6625550144");
        userMigrationVO3.setRecordNumber("42");
        userMigrationVO3.setToGeoDomainType("To Geo Domain Type");
        userMigrationVO3.setToOwnerID("To Owner ID");
        userMigrationVO3.setToParentCatCode("To Parent Cat Code");
        userMigrationVO3.setToParentGeoCode("To Parent Geo Code");
        userMigrationVO3.setToParentID("To Parent ID");
        userMigrationVO3.setToParentMsisdn("To Parent Msisdn");
        userMigrationVO3.setToUserCatCode("To User Cat Code");
        userMigrationVO3.setToUserCatCodeSeqNo("To User Cat Code Seq No");
        userMigrationVO3.setToUserGeoCode("To User Geo Code");
        userMigrationVO3.setToUserMsisdn("To User Msisdn");
        userMigrationVO3.setToUserParentName("To User Parent Name");
        userMigrationVO3.setUserID(" userid");

        ArrayList<UserMigrationVO> pUserMigrationList = new ArrayList<>();
        pUserMigrationList.add(userMigrationVO3);
        pUserMigrationList.add(userMigrationVO2);
        pUserMigrationList.add(userMigrationVO);
        userMovementServiceI.migrateUsers(JUnitConfig.getConnection(), pUserMigrationList);
    }

    /**
     * Method under test: {@link UserMovementServiceI#writeFileCSV(List, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testWriteFileCSV() throws IOException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R011 Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to access files (file '\directory\foo.txt', permission 'write').
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        userMovementServiceI.writeFileCSV(new ArrayList<>(), "/directory/foo.txt");
    }

    /**
     * Method under test: {@link UserMovementServiceI#getNpUserList(OAuthUser, HttpServletResponse, Locale, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetNpUserList() throws SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.channelAdmin.serviceI.UserMovementServiceI.getNpUserList(UserMovementServiceI.java:636)

        // Arrange
        // TODO: Populate arranged inputs
        OAuthUser oAuthUser = null;
        HttpServletResponse responseSwag = null;
        Locale locale = null;
        String domain = "";

        // Act
        FileDownloadResponse actualNpUserList = this.userMovementServiceI.getNpUserList(oAuthUser, responseSwag, locale,
                domain);

        // Assert
        // TODO: Add assertions on result
    }
}

