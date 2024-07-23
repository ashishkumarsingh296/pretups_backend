package com.restapi.superadmin;

import static org.mockito.Mockito.mock;

import com.btsl.common.BTSLBaseException;
import com.btsl.security.CustomResponseWrapper;

import java.sql.Connection;
import java.sql.SQLException;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {DeleteOperatorUserImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class DeleteOperatorUserImplTest {
    @Autowired
    private DeleteOperatorUserImpl deleteOperatorUserImpl;

    /**
     * Method under test: {@link DeleteOperatorUserImpl#deleteOperatorUser(Connection, String, DeleteOperatorRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDeleteOperatorUser() throws BTSLBaseException, SQLException {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.superadmin.DeleteOperatorUserImpl.deleteOperatorUser(DeleteOperatorUserImpl.java:52)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);

        DeleteOperatorRequestVO requestVO = new DeleteOperatorRequestVO();
        requestVO.setId("42");
        requestVO.setLastModified("Jan 1, 2020 9:00am GMT+0100");
        requestVO.setStatus("Status");
        requestVO.setUserId("42");
        requestVO.setUserName("janedoe");
        deleteOperatorUserImpl.deleteOperatorUser (com.btsl.util.JUnitConfig.getConnection(), "42", requestVO, new CustomResponseWrapper(new Response()));
    }
}

