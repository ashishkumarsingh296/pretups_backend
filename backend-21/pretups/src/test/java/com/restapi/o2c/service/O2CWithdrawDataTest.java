package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.businesslogic.Products;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class O2CWithdrawDataTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CWithdrawData}
     *   <li>{@link O2CWithdrawData#setFromUserId(String)}
     *   <li>{@link O2CWithdrawData#setLanguage(String)}
     *   <li>{@link O2CWithdrawData#setPin(String)}
     *   <li>{@link O2CWithdrawData#setProducts(List)}
     *   <li>{@link O2CWithdrawData#setRemarks(String)}
     *   <li>{@link O2CWithdrawData#setWalletType(String)}
     *   <li>{@link O2CWithdrawData#getFromUserId()}
     *   <li>{@link O2CWithdrawData#getLanguage()}
     *   <li>{@link O2CWithdrawData#getPin()}
     *   <li>{@link O2CWithdrawData#getProducts()}
     *   <li>{@link O2CWithdrawData#getRemarks()}
     *   <li>{@link O2CWithdrawData#getWalletType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CWithdrawData actualO2cWithdrawData = new O2CWithdrawData();
        actualO2cWithdrawData.setFromUserId("42");
        actualO2cWithdrawData.setLanguage("en");
        actualO2cWithdrawData.setPin("Pin");
        ArrayList<Products> products = new ArrayList<>();
        actualO2cWithdrawData.setProducts(products);
        actualO2cWithdrawData.setRemarks("Remarks");
        actualO2cWithdrawData.setWalletType("Wallet Type");
        assertEquals("42", actualO2cWithdrawData.getFromUserId());
        assertEquals("en", actualO2cWithdrawData.getLanguage());
        assertEquals("Pin", actualO2cWithdrawData.getPin());
        assertSame(products, actualO2cWithdrawData.getProducts());
        assertEquals("Remarks", actualO2cWithdrawData.getRemarks());
        assertEquals("Wallet Type", actualO2cWithdrawData.getWalletType());
    }
}

