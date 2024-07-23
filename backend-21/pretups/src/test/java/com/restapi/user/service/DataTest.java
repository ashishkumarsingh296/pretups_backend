package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DataTest {
    /**
     * Method under test: {@link Data#setAdditionalProperty(String, Object)}
     */
    @Test
    public void testSetAdditionalProperty() {
        Data data = new Data();
        data.setAdditionalProperty("Name", "Value");
        assertEquals(1, data.getAdditionalProperties().size());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link Data}
     *   <li>{@link Data#setDate(String)}
     *   <li>{@link Data#setExtcode2(String)}
     *   <li>{@link Data#setExtcode(String)}
     *   <li>{@link Data#setExtnwcode(String)}
     *   <li>{@link Data#setExtrefnum(String)}
     *   <li>{@link Data#setLanguage1(String)}
     *   <li>{@link Data#setLoginid2(String)}
     *   <li>{@link Data#setLoginid(String)}
     *   <li>{@link Data#setMsisdn2(String)}
     *   <li>{@link Data#setMsisdn(String)}
     *   <li>{@link Data#setPassword(String)}
     *   <li>{@link Data#setPin(String)}
     *   <li>{@link Data#setProducts(List)}
     *   <li>{@link Data#getDate()}
     *   <li>{@link Data#getExtcode2()}
     *   <li>{@link Data#getExtcode()}
     *   <li>{@link Data#getExtnwcode()}
     *   <li>{@link Data#getExtrefnum()}
     *   <li>{@link Data#getLanguage1()}
     *   <li>{@link Data#getLoginid2()}
     *   <li>{@link Data#getLoginid()}
     *   <li>{@link Data#getMsisdn2()}
     *   <li>{@link Data#getMsisdn()}
     *   <li>{@link Data#getPassword()}
     *   <li>{@link Data#getPin()}
     *   <li>{@link Data#getProducts()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        Data actualData = new Data();
        actualData.setDate("2020-03-01");
        actualData.setExtcode2("Extcode2");
        actualData.setExtcode("Extcode");
        actualData.setExtnwcode("Extnwcode");
        actualData.setExtrefnum("Extrefnum");
        actualData.setLanguage1("en");
        actualData.setLoginid2("Loginid2");
        actualData.setLoginid("Loginid");
        actualData.setMsisdn2("Msisdn2");
        actualData.setMsisdn("Msisdn");
        actualData.setPassword("iloveyou");
        actualData.setPin("Pin");
        ArrayList<ProductsC2C> products = new ArrayList<>();
        actualData.setProducts(products);
        assertEquals("2020-03-01", actualData.getDate());
        assertEquals("Extcode2", actualData.getExtcode2());
        assertEquals("Extcode", actualData.getExtcode());
        assertEquals("Extnwcode", actualData.getExtnwcode());
        assertEquals("Extrefnum", actualData.getExtrefnum());
        assertEquals("en", actualData.getLanguage1());
        assertEquals("Loginid2", actualData.getLoginid2());
        assertEquals("Loginid", actualData.getLoginid());
        assertEquals("Msisdn2", actualData.getMsisdn2());
        assertEquals("Msisdn", actualData.getMsisdn());
        assertEquals("iloveyou", actualData.getPassword());
        assertEquals("Pin", actualData.getPin());
        assertSame(products, actualData.getProducts());
    }
}

