package com.restapi.networkadmin.service;

import static org.mockito.Mockito.mock;

import com.btsl.common.BTSLBaseException;

import java.sql.Connection;
import java.sql.SQLException;

import com.btsl.util.JUnitConfig;
import oracle.jdbc.proxy.oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {ModifyBatchC2SCardGroupServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ModifyBatchC2SCardGroupServiceImplTest {
    @Autowired
    private ModifyBatchC2SCardGroupServiceImpl modifyBatchC2SCardGroupServiceImpl;

    /**
     * Method under test: {@link ModifyBatchC2SCardGroupServiceImpl#getServiceTypeList(Connection, String, String)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testGetServiceTypeList() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.user.businesslogic.UserDAO.loadAllUserDetailsByLoginID(UserDAO.java:4514)
        //       at com.restapi.networkadmin.service.ModifyBatchC2SCardGroupServiceImpl.getServiceTypeList(ModifyBatchC2SCardGroupServiceImpl.java:41)
        //   See https://diff.blue/R013 to resolve this issue.

        JUnitConfig.init();
        modifyBatchC2SCardGroupServiceImpl.getServiceTypeList(JUnitConfig.getConnection(), "Login User ID", "Type");
    }

    /**
     * Method under test: {@link ModifyBatchC2SCardGroupServiceImpl#getServiceTypeList(Connection, String, String)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testGetServiceTypeList2() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.user.businesslogic.UserDAO.loadAllUserDetailsByLoginID(UserDAO.java:4514)
        //       at com.restapi.networkadmin.service.ModifyBatchC2SCardGroupServiceImpl.getServiceTypeList(ModifyBatchC2SCardGroupServiceImpl.java:41)
        //   See https://diff.blue/R013 to resolve this issue.

        JUnitConfig.init();
        modifyBatchC2SCardGroupServiceImpl.getServiceTypeList(
                JUnitConfig.getConnection(),
                "Login User ID", "Type");
    }

    /**
     * Method under test: {@link ModifyBatchC2SCardGroupServiceImpl#getCardGroupNameList(Connection, String, String)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testGetCardGroupNameList() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.user.businesslogic.UserDAO.loadAllUserDetailsByLoginID(UserDAO.java:4514)
        //       at com.restapi.networkadmin.service.ModifyBatchC2SCardGroupServiceImpl.getCardGroupNameList(ModifyBatchC2SCardGroupServiceImpl.java:59)
        //   See https://diff.blue/R013 to resolve this issue.

        JUnitConfig.init();
        modifyBatchC2SCardGroupServiceImpl.getCardGroupNameList(JUnitConfig.getConnection(), "Login User ID", "Type");
    }

    /**
     * Method under test: {@link ModifyBatchC2SCardGroupServiceImpl#getCardGroupNameList(Connection, String, String)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testGetCardGroupNameList2() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.user.businesslogic.UserDAO.loadAllUserDetailsByLoginID(UserDAO.java:4514)
        //       at com.restapi.networkadmin.service.ModifyBatchC2SCardGroupServiceImpl.getCardGroupNameList(ModifyBatchC2SCardGroupServiceImpl.java:59)
        //   See https://diff.blue/R013 to resolve this issue.

        JUnitConfig.init();
        modifyBatchC2SCardGroupServiceImpl.getCardGroupNameList(
                JUnitConfig.getConnection(),
                "Login User ID", "Type");
    }
}

