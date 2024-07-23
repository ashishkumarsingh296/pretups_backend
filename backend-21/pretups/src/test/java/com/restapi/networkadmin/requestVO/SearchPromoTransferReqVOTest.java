package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SearchPromoTransferReqVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SearchPromoTransferReqVO}
     *   <li>{@link SearchPromoTransferReqVO#setCategoryCode(String)}
     *   <li>{@link SearchPromoTransferReqVO#setCellGroupID(String)}
     *   <li>{@link SearchPromoTransferReqVO#setDomainCode(String)}
     *   <li>{@link SearchPromoTransferReqVO#setGeoGraphyDomainType(String)}
     *   <li>{@link SearchPromoTransferReqVO#setGeography(String)}
     *   <li>{@link SearchPromoTransferReqVO#setGrade(String)}
     *   <li>{@link SearchPromoTransferReqVO#setOptionTab(String)}
     *   <li>{@link SearchPromoTransferReqVO#setUserID(String)}
     *   <li>{@link SearchPromoTransferReqVO#getCategoryCode()}
     *   <li>{@link SearchPromoTransferReqVO#getCellGroupID()}
     *   <li>{@link SearchPromoTransferReqVO#getDomainCode()}
     *   <li>{@link SearchPromoTransferReqVO#getGeoGraphyDomainType()}
     *   <li>{@link SearchPromoTransferReqVO#getGeography()}
     *   <li>{@link SearchPromoTransferReqVO#getGrade()}
     *   <li>{@link SearchPromoTransferReqVO#getOptionTab()}
     *   <li>{@link SearchPromoTransferReqVO#getUserID()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SearchPromoTransferReqVO actualSearchPromoTransferReqVO = new SearchPromoTransferReqVO();
        actualSearchPromoTransferReqVO.setCategoryCode("Category Code");
        actualSearchPromoTransferReqVO.setCellGroupID("Cell Group ID");
        actualSearchPromoTransferReqVO.setDomainCode("Domain Code");
        actualSearchPromoTransferReqVO.setGeoGraphyDomainType("Geo Graphy Domain Type");
        actualSearchPromoTransferReqVO.setGeography("Geography");
        actualSearchPromoTransferReqVO.setGrade("Grade");
        actualSearchPromoTransferReqVO.setOptionTab("Option Tab");
        actualSearchPromoTransferReqVO.setUserID("User ID");
        assertEquals("Category Code", actualSearchPromoTransferReqVO.getCategoryCode());
        assertEquals("Cell Group ID", actualSearchPromoTransferReqVO.getCellGroupID());
        assertEquals("Domain Code", actualSearchPromoTransferReqVO.getDomainCode());
        assertEquals("Geo Graphy Domain Type", actualSearchPromoTransferReqVO.getGeoGraphyDomainType());
        assertEquals("Geography", actualSearchPromoTransferReqVO.getGeography());
        assertEquals("Grade", actualSearchPromoTransferReqVO.getGrade());
        assertEquals("Option Tab", actualSearchPromoTransferReqVO.getOptionTab());
        assertEquals("User ID", actualSearchPromoTransferReqVO.getUserID());
    }
}

