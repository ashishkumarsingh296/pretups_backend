package com.restapi.networkadmin.commissionprofile.responseVO;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class CommissionProfileSubServiceListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CommissionProfileSubServiceListResponseVO}
     *   <li>{@link CommissionProfileSubServiceListResponseVO#setSubServiceList(ArrayList)}
     *   <li>{@link CommissionProfileSubServiceListResponseVO#getSubServiceList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CommissionProfileSubServiceListResponseVO actualCommissionProfileSubServiceListResponseVO = new CommissionProfileSubServiceListResponseVO();
        ArrayList subServiceList = new ArrayList();
        actualCommissionProfileSubServiceListResponseVO.setSubServiceList(subServiceList);
        assertSame(subServiceList, actualCommissionProfileSubServiceListResponseVO.getSubServiceList());
    }
}

