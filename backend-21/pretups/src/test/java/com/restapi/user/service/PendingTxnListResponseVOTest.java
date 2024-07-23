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

@ContextConfiguration(classes = {PendingTxnListResponseVO.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PendingTxnListResponseVOTest {
    @Autowired
    private PendingTxnListResponseVO pendingTxnListResponseVO;

    /**
     * Method under test: {@link PendingTxnListResponseVO#setTotlaTrnxCount(long)}
     */
    @Test
    public void testSetTotlaTrnxCount() {
        PendingTxnListResponseVO pendingTxnListResponseVO2 = new PendingTxnListResponseVO();
        pendingTxnListResponseVO2.setTotlaTrnxCount(3L);
        assertEquals(3L, pendingTxnListResponseVO2.getTotlaTrnxCount().longValue());
    }

    /**
     * Method under test: {@link PendingTxnListResponseVO#setTotlaTrnxCount(long)}
     */
    @Test
    public void testSetTotlaTrnxCount2() {
        ErrorMap errorMap = mock(ErrorMap.class);
        doNothing().when(errorMap).setMasterErrorList(Mockito.<List<MasterErrorList>>any());
        doNothing().when(errorMap).setRowErrorMsgLists(Mockito.<List<RowErrorMsgLists>>any());
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        PendingTxnListResponseVO pendingTxnListResponseVO2 = new PendingTxnListResponseVO();
        pendingTxnListResponseVO2.setErrorMap(errorMap);
        pendingTxnListResponseVO2.setTotlaTrnxCount(3L);
        verify(errorMap).setMasterErrorList(Mockito.<List<MasterErrorList>>any());
        verify(errorMap).setRowErrorMsgLists(Mockito.<List<RowErrorMsgLists>>any());
        assertEquals(3L, pendingTxnListResponseVO2.getTotlaTrnxCount().longValue());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link PendingTxnListResponseVO}
     *   <li>{@link PendingTxnListResponseVO#setErrorcode(String)}
     *   <li>{@link PendingTxnListResponseVO#setMessage(String)}
     *   <li>{@link PendingTxnListResponseVO#toString()}
     *   <li>{@link PendingTxnListResponseVO#getErrorcode()}
     *   <li>{@link PendingTxnListResponseVO#getMessage()}
     *   <li>{@link PendingTxnListResponseVO#getTotlaTrnxCount()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        PendingTxnListResponseVO actualPendingTxnListResponseVO = new PendingTxnListResponseVO();
        actualPendingTxnListResponseVO.setErrorcode("An error occurred");
        actualPendingTxnListResponseVO.setMessage("Not all who wander are lost");
        String actualToStringResult = actualPendingTxnListResponseVO.toString();
        assertEquals("An error occurred", actualPendingTxnListResponseVO.getErrorcode());
        assertEquals("Not all who wander are lost", actualPendingTxnListResponseVO.getMessage());
        assertNull(actualPendingTxnListResponseVO.getTotlaTrnxCount());
        assertEquals(
                "PendingTxnListResponseVO [message=Not all who wander are lost, totlaTrnxCount=null, errorcode=An error"
                        + " occurred]",
                actualToStringResult);
    }
}

