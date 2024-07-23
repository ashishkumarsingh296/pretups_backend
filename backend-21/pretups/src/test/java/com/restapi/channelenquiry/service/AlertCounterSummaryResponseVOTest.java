package com.restapi.channelenquiry.service;

import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.businesslogic.UserZeroBalanceCounterSummaryVO;

import java.util.ArrayList;

import org.junit.Test;

public class AlertCounterSummaryResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link AlertCounterSummaryResponseVO}
     *   <li>{@link AlertCounterSummaryResponseVO#setAlertList(ArrayList)}
     *   <li>{@link AlertCounterSummaryResponseVO#getAlertList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        AlertCounterSummaryResponseVO actualAlertCounterSummaryResponseVO = new AlertCounterSummaryResponseVO();
        ArrayList<UserZeroBalanceCounterSummaryVO> alertList = new ArrayList<>();
        actualAlertCounterSummaryResponseVO.setAlertList(alertList);
        assertSame(alertList, actualAlertCounterSummaryResponseVO.getAlertList());
    }
}

