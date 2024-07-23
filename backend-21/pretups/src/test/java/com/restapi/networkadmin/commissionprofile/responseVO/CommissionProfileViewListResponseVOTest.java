package com.restapi.networkadmin.commissionprofile.responseVO;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class CommissionProfileViewListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CommissionProfileViewListResponseVO}
     *   <li>{@link CommissionProfileViewListResponseVO#setViewList(ArrayList)}
     *   <li>{@link CommissionProfileViewListResponseVO#getViewList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CommissionProfileViewListResponseVO actualCommissionProfileViewListResponseVO = new CommissionProfileViewListResponseVO();
        ArrayList viewList = new ArrayList();
        actualCommissionProfileViewListResponseVO.setViewList(viewList);
        assertSame(viewList, actualCommissionProfileViewListResponseVO.getViewList());
    }
}

