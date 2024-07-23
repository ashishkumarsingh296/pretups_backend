package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class O2CProductResponseDataTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CProductResponseData}
     *   <li>{@link O2CProductResponseData#setCode(String)}
     *   <li>{@link O2CProductResponseData#setName(String)}
     *   <li>{@link O2CProductResponseData#setShortCode(Long)}
     *   <li>{@link O2CProductResponseData#toString()}
     *   <li>{@link O2CProductResponseData#getCode()}
     *   <li>{@link O2CProductResponseData#getName()}
     *   <li>{@link O2CProductResponseData#getShortCode()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CProductResponseData actualO2cProductResponseData = new O2CProductResponseData();
        actualO2cProductResponseData.setCode("Code");
        actualO2cProductResponseData.setName("Name");
        actualO2cProductResponseData.setShortCode(1L);
        String actualToStringResult = actualO2cProductResponseData.toString();
        assertEquals("Code", actualO2cProductResponseData.getCode());
        assertEquals("Name", actualO2cProductResponseData.getName());
        assertEquals(1L, actualO2cProductResponseData.getShortCode().longValue());
        assertEquals("O2CProductResponseData [code=Code, shortCode=1, name=Name]", actualToStringResult);
    }
}

