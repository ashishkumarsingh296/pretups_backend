package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GetAgentScreenDetailsReqTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link GetAgentScreenDetailsReq#GetAgentScreenDetailsReq()}
     *   <li>{@link GetAgentScreenDetailsReq#setCategoryGeoDomainType(String)}
     *   <li>{@link GetAgentScreenDetailsReq#setDomainCode(String)}
     *   <li>{@link GetAgentScreenDetailsReq#setParentCategoryCode(String)}
     *   <li>{@link GetAgentScreenDetailsReq#getCategoryGeoDomainType()}
     *   <li>{@link GetAgentScreenDetailsReq#getDomainCode()}
     *   <li>{@link GetAgentScreenDetailsReq#getParentCategoryCode()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GetAgentScreenDetailsReq actualGetAgentScreenDetailsReq = new GetAgentScreenDetailsReq();
        actualGetAgentScreenDetailsReq.setCategoryGeoDomainType("Category Geo Domain Type");
        actualGetAgentScreenDetailsReq.setDomainCode("Domain Code");
        actualGetAgentScreenDetailsReq.setParentCategoryCode("Parent Category Code");
        assertEquals("Category Geo Domain Type", actualGetAgentScreenDetailsReq.getCategoryGeoDomainType());
        assertEquals("Domain Code", actualGetAgentScreenDetailsReq.getDomainCode());
        assertEquals("Parent Category Code", actualGetAgentScreenDetailsReq.getParentCategoryCode());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link GetAgentScreenDetailsReq#GetAgentScreenDetailsReq(String, String, String)}
     *   <li>{@link GetAgentScreenDetailsReq#setCategoryGeoDomainType(String)}
     *   <li>{@link GetAgentScreenDetailsReq#setDomainCode(String)}
     *   <li>{@link GetAgentScreenDetailsReq#setParentCategoryCode(String)}
     *   <li>{@link GetAgentScreenDetailsReq#getCategoryGeoDomainType()}
     *   <li>{@link GetAgentScreenDetailsReq#getDomainCode()}
     *   <li>{@link GetAgentScreenDetailsReq#getParentCategoryCode()}
     * </ul>
     */
    @Test
    public void testConstructor2() {
        GetAgentScreenDetailsReq actualGetAgentScreenDetailsReq = new GetAgentScreenDetailsReq("Domain Code",
                "Parent Category Code", "Category Geo Domain Type");
        actualGetAgentScreenDetailsReq.setCategoryGeoDomainType("Category Geo Domain Type");
        actualGetAgentScreenDetailsReq.setDomainCode("Domain Code");
        actualGetAgentScreenDetailsReq.setParentCategoryCode("Parent Category Code");
        assertEquals("Category Geo Domain Type", actualGetAgentScreenDetailsReq.getCategoryGeoDomainType());
        assertEquals("Domain Code", actualGetAgentScreenDetailsReq.getDomainCode());
        assertEquals("Parent Category Code", actualGetAgentScreenDetailsReq.getParentCategoryCode());
    }

    /**
     * Method under test: {@link GetAgentScreenDetailsReq#clone()}
     */
    @Test
    public void testClone() {
        assertEquals("Category Geo Domain Type", ((GetAgentScreenDetailsReq) (new GetAgentScreenDetailsReq("Domain Code",
                "Parent Category Code", "Category Geo Domain Type")).clone()).getCategoryGeoDomainType());
        assertEquals("Parent Category Code", ((GetAgentScreenDetailsReq) (new GetAgentScreenDetailsReq("Domain Code",
                "Parent Category Code", "Category Geo Domain Type")).clone()).getParentCategoryCode());
        assertEquals("Domain Code", ((GetAgentScreenDetailsReq) (new GetAgentScreenDetailsReq("Domain Code",
                "Parent Category Code", "Category Geo Domain Type")).clone()).getDomainCode());
    }
}

