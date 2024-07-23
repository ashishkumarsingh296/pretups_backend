package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProductsC2CTest {
    /**
     * Method under test: {@link ProductsC2C#setAdditionalProperty(String, Object)}
     */
    @Test
    public void testSetAdditionalProperty() {
        ProductsC2C productsC2C = new ProductsC2C();
        productsC2C.setAdditionalProperty("Name", "Value");
        assertEquals(1, productsC2C.getAdditionalProperties().size());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ProductsC2C}
     *   <li>{@link ProductsC2C#setProductcode(String)}
     *   <li>{@link ProductsC2C#setQty(String)}
     *   <li>{@link ProductsC2C#getProductcode()}
     *   <li>{@link ProductsC2C#getQty()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ProductsC2C actualProductsC2C = new ProductsC2C();
        actualProductsC2C.setProductcode("Productcode");
        actualProductsC2C.setQty("Qty");
        assertEquals("Productcode", actualProductsC2C.getProductcode());
        assertEquals("Qty", actualProductsC2C.getQty());
    }
}

