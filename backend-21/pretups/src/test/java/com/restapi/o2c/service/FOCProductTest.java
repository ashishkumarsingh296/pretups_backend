package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FOCProductTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link FOCProduct}
     *   <li>{@link FOCProduct#setAppQuantity(String)}
     *   <li>{@link FOCProduct#setProductCode(String)}
     *   <li>{@link FOCProduct#toString()}
     *   <li>{@link FOCProduct#getAppQuantity()}
     *   <li>{@link FOCProduct#getProductCode()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        FOCProduct actualFocProduct = new FOCProduct();
        actualFocProduct.setAppQuantity("App Quantity");
        actualFocProduct.setProductCode("Product Code");
        String actualToStringResult = actualFocProduct.toString();
        assertEquals("App Quantity", actualFocProduct.getAppQuantity());
        assertEquals("Product Code", actualFocProduct.getProductCode());
        assertEquals("FOCProduct [productCode=Product Code, appQuantity=App Quantity]", actualToStringResult);
    }
}

