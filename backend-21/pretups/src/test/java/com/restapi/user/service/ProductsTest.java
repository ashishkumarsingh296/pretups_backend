package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProductsTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link Products}
     *   <li>{@link Products#setProductcode(String)}
     *   <li>{@link Products#setQty(String)}
     *   <li>{@link Products#toString()}
     *   <li>{@link Products#getProductcode()}
     *   <li>{@link Products#getQty()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        Products actualProducts = new Products();
        actualProducts.setProductcode("Productcode");
        actualProducts.setQty("Qty");
        String actualToStringResult = actualProducts.toString();
        assertEquals("Productcode", actualProducts.getProductcode());
        assertEquals("Qty", actualProducts.getQty());
        assertEquals("productcodeProductcodeqty = Qty", actualToStringResult);
    }
}

