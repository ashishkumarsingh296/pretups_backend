package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.btsl.common.ErrorMap;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {C2STotalTransactionCountResponse.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class C2STotalTransactionCountResponseTest {
    @Autowired
    private C2STotalTransactionCountResponse c2STotalTransactionCountResponse;

    /**
     * Method under test: {@link C2STotalTransactionCountResponse#setTotlaTrnxCount(long)}
     */
    @Test
    public void testSetTotlaTrnxCount() {
        C2STotalTransactionCountResponse c2sTotalTransactionCountResponse = new C2STotalTransactionCountResponse();
        c2sTotalTransactionCountResponse.setTotlaTrnxCount(3L);
        assertEquals(3L, c2sTotalTransactionCountResponse.getTotlaTrnxCount().longValue());
    }

    /**
     * Method under test: {@link C2STotalTransactionCountResponse#setTotlaTrnxCount(long)}
     */
    @Test
    public void testSetTotlaTrnxCount2() {
        ErrorMap errorMap = mock(ErrorMap.class);
        doNothing().when(errorMap).setMasterErrorList(Mockito.<List<MasterErrorList>>any());
        doNothing().when(errorMap).setRowErrorMsgLists(Mockito.<List<RowErrorMsgLists>>any());
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        C2STotalTransactionCountResponse c2sTotalTransactionCountResponse = new C2STotalTransactionCountResponse();
        c2sTotalTransactionCountResponse.setErrorMap(errorMap);
        c2sTotalTransactionCountResponse.setTotlaTrnxCount(3L);
        verify(errorMap).setMasterErrorList(Mockito.<List<MasterErrorList>>any());
        verify(errorMap).setRowErrorMsgLists(Mockito.<List<RowErrorMsgLists>>any());
        assertEquals(3L, c2sTotalTransactionCountResponse.getTotlaTrnxCount().longValue());
    }

    /**
     * Method under test: {@link C2STotalTransactionCountResponse#setAdditionalProperty(String, Object)}
     */
    @Test
    public void testSetAdditionalProperty() {
        C2STotalTransactionCountResponse c2sTotalTransactionCountResponse = new C2STotalTransactionCountResponse();
        c2sTotalTransactionCountResponse.setAdditionalProperty("Name", "Value");
        assertEquals(1, c2sTotalTransactionCountResponse.getAdditionalProperties().size());
    }

    /**
     * Method under test: {@link C2STotalTransactionCountResponse#setAdditionalProperty(String, Object)}
     */
    @Test
    public void testSetAdditionalProperty2() {
        ErrorMap errorMap = mock(ErrorMap.class);
        doNothing().when(errorMap).setMasterErrorList(Mockito.<List<MasterErrorList>>any());
        doNothing().when(errorMap).setRowErrorMsgLists(Mockito.<List<RowErrorMsgLists>>any());
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        C2STotalTransactionCountResponse c2sTotalTransactionCountResponse = new C2STotalTransactionCountResponse();
        c2sTotalTransactionCountResponse.setErrorMap(errorMap);
        c2sTotalTransactionCountResponse.setAdditionalProperty("Name", "Value");
        verify(errorMap).setMasterErrorList(Mockito.<List<MasterErrorList>>any());
        verify(errorMap).setRowErrorMsgLists(Mockito.<List<RowErrorMsgLists>>any());
        assertEquals(1, c2sTotalTransactionCountResponse.getAdditionalProperties().size());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2STotalTransactionCountResponse}
     *   <li>{@link C2STotalTransactionCountResponse#setDate(String)}
     *   <li>{@link C2STotalTransactionCountResponse#setErrorcode(String)}
     *   <li>{@link C2STotalTransactionCountResponse#setMessage(String)}
     *   <li>{@link C2STotalTransactionCountResponse#toString()}
     *   <li>{@link C2STotalTransactionCountResponse#getDate()}
     *   <li>{@link C2STotalTransactionCountResponse#getErrorcode()}
     *   <li>{@link C2STotalTransactionCountResponse#getMessage()}
     *   <li>{@link C2STotalTransactionCountResponse#getTotlaTrnxCount()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2STotalTransactionCountResponse actualC2sTotalTransactionCountResponse = new C2STotalTransactionCountResponse();
        actualC2sTotalTransactionCountResponse.setDate("2020-03-01");
        actualC2sTotalTransactionCountResponse.setErrorcode("An error occurred");
        actualC2sTotalTransactionCountResponse.setMessage("Not all who wander are lost");
        String actualToStringResult = actualC2sTotalTransactionCountResponse.toString();
        assertEquals("2020-03-01", actualC2sTotalTransactionCountResponse.getDate());
        assertEquals("An error occurred", actualC2sTotalTransactionCountResponse.getErrorcode());
        assertEquals("Not all who wander are lost", actualC2sTotalTransactionCountResponse.getMessage());
        assertNull(actualC2sTotalTransactionCountResponse.getTotlaTrnxCount());
        assertEquals(
                "C2STotalTransactionCountResponse [date=2020-03-01, message=Not all who wander are lost, totlaTrnxCount=null,"
                        + " errorcode=An error occurred, additionalProperties={}]",
                actualToStringResult);
    }
}

