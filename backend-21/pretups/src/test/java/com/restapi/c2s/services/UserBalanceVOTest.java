package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserBalanceVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserBalanceVO}
     *   <li>{@link UserBalanceVO#setBalance(String)}
     *   <li>{@link UserBalanceVO#setProductCode(String)}
     *   <li>{@link UserBalanceVO#setProductName(String)}
     *   <li>{@link UserBalanceVO#toString()}
     *   <li>{@link UserBalanceVO#getBalance()}
     *   <li>{@link UserBalanceVO#getProductCode()}
     *   <li>{@link UserBalanceVO#getProductName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserBalanceVO actualUserBalanceVO = new UserBalanceVO();
        actualUserBalanceVO.setBalance("Balance");
        actualUserBalanceVO.setProductCode("Product Code");
        actualUserBalanceVO.setProductName("Product Name");
        String actualToStringResult = actualUserBalanceVO.toString();
        assertEquals("Balance", actualUserBalanceVO.getBalance());
        assertEquals("Product Code", actualUserBalanceVO.getProductCode());
        assertEquals("Product Name", actualUserBalanceVO.getProductName());
        assertEquals("UserBalanceVO [balance=Balance, productCode=Product Code, productName=Product Name]",
                actualToStringResult);
    }
}

