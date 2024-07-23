package com.restapi.networkadmin.commissionprofile.responseVO;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class CommissionProfileProductListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CommissionProfileProductListResponseVO}
     *   <li>{@link CommissionProfileProductListResponseVO#setProductList(ArrayList)}
     *   <li>{@link CommissionProfileProductListResponseVO#getProductList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CommissionProfileProductListResponseVO actualCommissionProfileProductListResponseVO = new CommissionProfileProductListResponseVO();
        ArrayList productList = new ArrayList();
        actualCommissionProfileProductListResponseVO.setProductList(productList);
        assertSame(productList, actualCommissionProfileProductListResponseVO.getProductList());
    }
}

