package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserBalanceResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserBalanceResponseVO}
     *   <li>{@link UserBalanceResponseVO#setClosingBalance(String)}
     *   <li>{@link UserBalanceResponseVO#setOpeningBalance(String)}
     *   <li>{@link UserBalanceResponseVO#toString()}
     *   <li>{@link UserBalanceResponseVO#getClosingBalance()}
     *   <li>{@link UserBalanceResponseVO#getOpeningBalance()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserBalanceResponseVO actualUserBalanceResponseVO = new UserBalanceResponseVO();
        actualUserBalanceResponseVO.setClosingBalance("Closing Balance");
        actualUserBalanceResponseVO.setOpeningBalance("Opening Balance");
        String actualToStringResult = actualUserBalanceResponseVO.toString();
        assertEquals("Closing Balance", actualUserBalanceResponseVO.getClosingBalance());
        assertEquals("Opening Balance", actualUserBalanceResponseVO.getOpeningBalance());
        assertEquals("UserBalanceResponseVO [openingBalance=Opening Balance, closingBalance=Closing Balance]",
                actualToStringResult);
    }
}

