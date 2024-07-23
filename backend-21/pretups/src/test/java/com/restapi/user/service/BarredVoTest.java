package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BarredVoTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BarredVo}
     *   <li>{@link BarredVo#setBarredAs(String)}
     *   <li>{@link BarredVo#setCategoryCode(String)}
     *   <li>{@link BarredVo#setCategoryName(String)}
     *   <li>{@link BarredVo#setDomainCode(String)}
     *   <li>{@link BarredVo#setDomainName(String)}
     *   <li>{@link BarredVo#setLoginId(String)}
     *   <li>{@link BarredVo#getBarredAs()}
     *   <li>{@link BarredVo#getCategoryCode()}
     *   <li>{@link BarredVo#getCategoryName()}
     *   <li>{@link BarredVo#getDomainCode()}
     *   <li>{@link BarredVo#getDomainName()}
     *   <li>{@link BarredVo#getLoginId()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BarredVo actualBarredVo = new BarredVo();
        actualBarredVo.setBarredAs("Barred As");
        actualBarredVo.setCategoryCode("Category Code");
        actualBarredVo.setCategoryName("Category Name");
        actualBarredVo.setDomainCode("Domain Code");
        actualBarredVo.setDomainName("Domain Name");
        actualBarredVo.setLoginId("42");
        assertEquals("Barred As", actualBarredVo.getBarredAs());
        assertEquals("Category Code", actualBarredVo.getCategoryCode());
        assertEquals("Category Name", actualBarredVo.getCategoryName());
        assertEquals("Domain Code", actualBarredVo.getDomainCode());
        assertEquals("Domain Name", actualBarredVo.getDomainName());
        assertEquals("42", actualBarredVo.getLoginId());
    }
}

