package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class O2CProductApprTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CProductAppr}
     *   <li>{@link O2CProductAppr#setAppQuantity(String)}
     *   <li>{@link O2CProductAppr#setProductCode(String)}
     *   <li>{@link O2CProductAppr#toString()}
     *   <li>{@link O2CProductAppr#getAppQuantity()}
     *   <li>{@link O2CProductAppr#getProductCode()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CProductAppr actualO2cProductAppr = new O2CProductAppr();
        actualO2cProductAppr.setAppQuantity("App Quantity");
        actualO2cProductAppr.setProductCode("Product Code");
        String actualToStringResult = actualO2cProductAppr.toString();
        assertEquals("App Quantity", actualO2cProductAppr.getAppQuantity());
        assertEquals("Product Code", actualO2cProductAppr.getProductCode());
        assertEquals("O2CProductAppr [productCode=Product Code, appQuantity=App Quantity]", actualToStringResult);
    }
}

