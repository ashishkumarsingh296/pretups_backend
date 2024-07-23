package com.restapi.user.service;

import com.btsl.common.BTSLBaseException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {TransactionPinManagementServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class TransactionPinManagementServiceImplTest {
    @Autowired
    private TransactionPinManagementServiceImpl transactionPinManagementServiceImpl;

    /**
     * Method under test: {@link TransactionPinManagementServiceImpl#validatePinModifyRequired(String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidatePinModifyRequired() throws BTSLBaseException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.user.service.TransactionPinManagementServiceImpl.validatePinModifyRequired(TransactionPinManagementServiceImpl.java:34)
        //   See https://diff.blue/R013 to resolve this issue.

        transactionPinManagementServiceImpl.validatePinModifyRequired("Msisdn");
    }

    /**
     * Method under test: {@link TransactionPinManagementServiceImpl#validatePinModifyRequired(String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidatePinModifyRequired2() throws BTSLBaseException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.user.service.TransactionPinManagementServiceImpl.validatePinModifyRequired(TransactionPinManagementServiceImpl.java:34)
        //   See https://diff.blue/R013 to resolve this issue.

        transactionPinManagementServiceImpl.validatePinModifyRequired("validatePinModifyRequired");
    }
}

