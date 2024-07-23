package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TransactionPinManagementResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link TransactionPinManagementResponseVO}
     *   <li>{@link TransactionPinManagementResponseVO#setPinChangeRequired(Boolean)}
     *   <li>{@link TransactionPinManagementResponseVO#toString()}
     *   <li>{@link TransactionPinManagementResponseVO#getPinChangeRequired()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        TransactionPinManagementResponseVO actualTransactionPinManagementResponseVO = new TransactionPinManagementResponseVO();
        actualTransactionPinManagementResponseVO.setPinChangeRequired(true);
        String actualToStringResult = actualTransactionPinManagementResponseVO.toString();
        assertTrue(actualTransactionPinManagementResponseVO.getPinChangeRequired());
        assertEquals("TransactionPinManagementResponseVO [pinChangeRequired=true]", actualToStringResult);
    }
}

