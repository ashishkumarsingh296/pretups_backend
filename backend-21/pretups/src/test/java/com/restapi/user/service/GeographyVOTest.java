package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GeographyVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GeographyVO}
     *   <li>{@link GeographyVO#setGraphDomainCode(String)}
     *   <li>{@link GeographyVO#setGraphDomainName(String)}
     *   <li>{@link GeographyVO#setGraphDomainTypeName(String)}
     *   <li>{@link GeographyVO#setParentGraphDomainCode(String)}
     *   <li>{@link GeographyVO#toString()}
     *   <li>{@link GeographyVO#getGraphDomainCode()}
     *   <li>{@link GeographyVO#getGraphDomainName()}
     *   <li>{@link GeographyVO#getGraphDomainTypeName()}
     *   <li>{@link GeographyVO#getParentGraphDomainCode()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GeographyVO actualGeographyVO = new GeographyVO();
        actualGeographyVO.setGraphDomainCode("Graph Domain Code");
        actualGeographyVO.setGraphDomainName("Graph Domain Name");
        actualGeographyVO.setGraphDomainTypeName("Graph Domain Type Name");
        actualGeographyVO.setParentGraphDomainCode("Parent Graph Domain Code");
        actualGeographyVO.toString();
        assertEquals("Graph Domain Code", actualGeographyVO.getGraphDomainCode());
        assertEquals("Graph Domain Name", actualGeographyVO.getGraphDomainName());
        assertEquals("Graph Domain Type Name", actualGeographyVO.getGraphDomainTypeName());
        assertEquals("Parent Graph Domain Code", actualGeographyVO.getParentGraphDomainCode());
    }
}

