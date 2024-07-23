package com.restapi.superadmin.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.JUnitConfig;
import com.restapi.superadmin.responseVO.GradeTypeListResponseVO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {GradeManagementServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class GradeManagementServiceImplTest {
    @Autowired
    private GradeManagementServiceImpl gradeManagementServiceImpl;

    /**
     * Method under test: {@link GradeManagementServiceImpl#getGradeTypeList(Connection, MComConnectionI, Locale, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetGradeTypeList() throws BTSLBaseException, SQLException {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = null;
        HttpServletResponse responseSwag = null;

        // Act
        GradeTypeListResponseVO actualGradeTypeList = this.gradeManagementServiceImpl.getGradeTypeList (com.btsl.util.JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale,
                responseSwag);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link GradeManagementServiceImpl#viewGradeList(Connection, MComConnectionI, Locale, String, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testViewGradeList() {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = null;
        String domainCode = "";
        String categoryCode = "";
        HttpServletResponse responseSwag = null;

        // Act
        GradeTypeListResponseVO actualViewGradeListResult = this.gradeManagementServiceImpl.viewGradeList (com.btsl.util.JUnitConfig.getConnection(), JUnitConfig.getMComConnection(),
                locale, domainCode, categoryCode, responseSwag);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link GradeManagementServiceImpl#addGrade(Connection, MComConnectionI, Locale, ChannelUserVO, String, String, String, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testAddGrade() {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = null;
        ChannelUserVO userVO = null;
        String categoryCode = "";
        String gradeCode = "";
        String gradeName = "";
        String defaultGrade = "";
        HttpServletResponse responseSwag = null;

        // Act
        GradeTypeListResponseVO actualAddGradeResult = this.gradeManagementServiceImpl.addGrade (com.btsl.util.JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale,
                userVO, categoryCode, gradeCode, gradeName, defaultGrade, responseSwag);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link GradeManagementServiceImpl#modifyGrade(Connection, MComConnectionI, Locale, ChannelUserVO, String, String, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testModifyGrade() {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = null;
        ChannelUserVO userVO = null;
        String gradeCode = "";
        String gradeName = "";
        String defaultGrade = "";
        HttpServletResponse responseSwag = null;

        // Act
        GradeTypeListResponseVO actualModifyGradeResult = this.gradeManagementServiceImpl.modifyGrade (com.btsl.util.JUnitConfig.getConnection(), JUnitConfig.getMComConnection(),
                locale, userVO, gradeCode, gradeName, defaultGrade, responseSwag);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link GradeManagementServiceImpl#deleteGrade(Connection, MComConnectionI, Locale, ChannelUserVO, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDeleteGrade() {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = null;
        ChannelUserVO userVO = null;
        String gradeCode = "";
        HttpServletResponse responseSwag = null;

        // Act
        GradeTypeListResponseVO actualDeleteGradeResult = this.gradeManagementServiceImpl.deleteGrade (com.btsl.util.JUnitConfig.getConnection(), JUnitConfig.getMComConnection(),
                locale, userVO, gradeCode, responseSwag);

        // Assert
        // TODO: Add assertions on result
    }
}

