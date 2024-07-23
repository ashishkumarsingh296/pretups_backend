package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProductApprTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ProductAppr}
     *   <li>{@link ProductAppr#setProductcode(String)}
     *   <li>{@link ProductAppr#setQty(String)}
     *   <li>{@link ProductAppr#toString()}
     *   <li>{@link ProductAppr#getProductcode()}
     *   <li>{@link ProductAppr#getQty()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ProductAppr actualProductAppr = new ProductAppr();
        actualProductAppr.setProductcode("Productcode");
        actualProductAppr.setQty("Qty");
        String actualToStringResult = actualProductAppr.toString();
        assertEquals("Productcode", actualProductAppr.getProductcode());
        assertEquals("Qty", actualProductAppr.getQty());
        assertEquals("productcode = Productcodeqty = Qty", actualToStringResult);
    }
}

