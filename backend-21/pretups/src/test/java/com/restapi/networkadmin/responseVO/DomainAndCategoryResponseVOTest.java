package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class DomainAndCategoryResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DomainAndCategoryResponseVO}
     *   <li>{@link DomainAndCategoryResponseVO#setCategoryList(ArrayList)}
     *   <li>{@link DomainAndCategoryResponseVO#setCellGroupList(ArrayList)}
     *   <li>{@link DomainAndCategoryResponseVO#setDomainList(ArrayList)}
     *   <li>{@link DomainAndCategoryResponseVO#setGeoDomain(ArrayList)}
     *   <li>{@link DomainAndCategoryResponseVO#setGeoType(ArrayList)}
     *   <li>{@link DomainAndCategoryResponseVO#setGeoTypeCode(String)}
     *   <li>{@link DomainAndCategoryResponseVO#setGeoTypeDesc(String)}
     *   <li>{@link DomainAndCategoryResponseVO#setServiceGroupList(ArrayList)}
     *   <li>{@link DomainAndCategoryResponseVO#toString()}
     *   <li>{@link DomainAndCategoryResponseVO#getCategoryList()}
     *   <li>{@link DomainAndCategoryResponseVO#getCellGroupList()}
     *   <li>{@link DomainAndCategoryResponseVO#getDomainList()}
     *   <li>{@link DomainAndCategoryResponseVO#getGeoDomain()}
     *   <li>{@link DomainAndCategoryResponseVO#getGeoType()}
     *   <li>{@link DomainAndCategoryResponseVO#getGeoTypeCode()}
     *   <li>{@link DomainAndCategoryResponseVO#getGeoTypeDesc()}
     *   <li>{@link DomainAndCategoryResponseVO#getServiceGroupList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DomainAndCategoryResponseVO actualDomainAndCategoryResponseVO = new DomainAndCategoryResponseVO();
        ArrayList categoryList = new ArrayList();
        actualDomainAndCategoryResponseVO.setCategoryList(categoryList);
        ArrayList cellGroupList = new ArrayList();
        actualDomainAndCategoryResponseVO.setCellGroupList(cellGroupList);
        ArrayList domainList = new ArrayList();
        actualDomainAndCategoryResponseVO.setDomainList(domainList);
        ArrayList geoDomain = new ArrayList();
        actualDomainAndCategoryResponseVO.setGeoDomain(geoDomain);
        ArrayList geoType = new ArrayList();
        actualDomainAndCategoryResponseVO.setGeoType(geoType);
        actualDomainAndCategoryResponseVO.setGeoTypeCode("Geo Type Code");
        actualDomainAndCategoryResponseVO.setGeoTypeDesc("Geo Type Desc");
        ArrayList serviceGroupList = new ArrayList();
        actualDomainAndCategoryResponseVO.setServiceGroupList(serviceGroupList);
        String actualToStringResult = actualDomainAndCategoryResponseVO.toString();
        assertSame(categoryList, actualDomainAndCategoryResponseVO.getCategoryList());
        assertSame(cellGroupList, actualDomainAndCategoryResponseVO.getCellGroupList());
        assertSame(domainList, actualDomainAndCategoryResponseVO.getDomainList());
        assertSame(geoDomain, actualDomainAndCategoryResponseVO.getGeoDomain());
        assertSame(geoType, actualDomainAndCategoryResponseVO.getGeoType());
        assertEquals("Geo Type Code", actualDomainAndCategoryResponseVO.getGeoTypeCode());
        assertEquals("Geo Type Desc", actualDomainAndCategoryResponseVO.getGeoTypeDesc());
        assertSame(serviceGroupList, actualDomainAndCategoryResponseVO.getServiceGroupList());
        assertEquals(
                "DomainAndCategoryResponseVO [domainList=[], categoryList=[], geoType=[], geoDomain=[], geoTypeCode=Geo"
                        + " Type Code, geoTypeDesc=Geo Type Desc, cellGroupList=[], serviceGroupList=[]]",
                actualToStringResult);
    }
}

