package com.restapi.networkadmin.commissionprofile.responseVO;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class CommissionProfileGatewayListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CommissionProfileGatewayListResponseVO}
     *   <li>{@link CommissionProfileGatewayListResponseVO#setGatewayList(ArrayList)}
     *   <li>{@link CommissionProfileGatewayListResponseVO#getGatewayList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CommissionProfileGatewayListResponseVO actualCommissionProfileGatewayListResponseVO = new CommissionProfileGatewayListResponseVO();
        ArrayList gatewayList = new ArrayList();
        actualCommissionProfileGatewayListResponseVO.setGatewayList(gatewayList);
        assertSame(gatewayList, actualCommissionProfileGatewayListResponseVO.getGatewayList());
    }
}

