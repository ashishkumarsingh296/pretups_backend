package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class PromoDepDropdownlistRespVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link PromoDepDropdownlistRespVO}
     *   <li>{@link PromoDepDropdownlistRespVO#setCardGroupList(ArrayList)}
     *   <li>{@link PromoDepDropdownlistRespVO#setCellGroupList(ArrayList)}
     *   <li>{@link PromoDepDropdownlistRespVO#setGeographicalDomainType(ArrayList)}
     *   <li>{@link PromoDepDropdownlistRespVO#setGeographyListAll(ArrayList)}
     *   <li>{@link PromoDepDropdownlistRespVO#setGradeListByCategory(ArrayList)}
     *   <li>{@link PromoDepDropdownlistRespVO#setNoOfRows(int)}
     *   <li>{@link PromoDepDropdownlistRespVO#setServiceClassList(ArrayList)}
     *   <li>{@link PromoDepDropdownlistRespVO#setServiceProviderGroupList(ArrayList)}
     *   <li>{@link PromoDepDropdownlistRespVO#setSubServiceList(ArrayList)}
     *   <li>{@link PromoDepDropdownlistRespVO#setSubscriberStatusValueList(ArrayList)}
     *   <li>{@link PromoDepDropdownlistRespVO#getCardGroupList()}
     *   <li>{@link PromoDepDropdownlistRespVO#getCellGroupList()}
     *   <li>{@link PromoDepDropdownlistRespVO#getGeographicalDomainType()}
     *   <li>{@link PromoDepDropdownlistRespVO#getGeographyListAll()}
     *   <li>{@link PromoDepDropdownlistRespVO#getGradeListByCategory()}
     *   <li>{@link PromoDepDropdownlistRespVO#getNoOfRows()}
     *   <li>{@link PromoDepDropdownlistRespVO#getServiceClassList()}
     *   <li>{@link PromoDepDropdownlistRespVO#getServiceProviderGroupList()}
     *   <li>{@link PromoDepDropdownlistRespVO#getSubServiceList()}
     *   <li>{@link PromoDepDropdownlistRespVO#getSubscriberStatusValueList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        PromoDepDropdownlistRespVO actualPromoDepDropdownlistRespVO = new PromoDepDropdownlistRespVO();
        ArrayList cardGroupList = new ArrayList();
        actualPromoDepDropdownlistRespVO.setCardGroupList(cardGroupList);
        ArrayList cellGroupList = new ArrayList();
        actualPromoDepDropdownlistRespVO.setCellGroupList(cellGroupList);
        ArrayList geographicalDomainType = new ArrayList();
        actualPromoDepDropdownlistRespVO.setGeographicalDomainType(geographicalDomainType);
        ArrayList geographyListAll = new ArrayList();
        actualPromoDepDropdownlistRespVO.setGeographyListAll(geographyListAll);
        ArrayList gradeListByCategory = new ArrayList();
        actualPromoDepDropdownlistRespVO.setGradeListByCategory(gradeListByCategory);
        actualPromoDepDropdownlistRespVO.setNoOfRows(1);
        ArrayList serviceClassList = new ArrayList();
        actualPromoDepDropdownlistRespVO.setServiceClassList(serviceClassList);
        ArrayList serviceProviderGroupList = new ArrayList();
        actualPromoDepDropdownlistRespVO.setServiceProviderGroupList(serviceProviderGroupList);
        ArrayList subServiceList = new ArrayList();
        actualPromoDepDropdownlistRespVO.setSubServiceList(subServiceList);
        ArrayList subscriberStatusValueList = new ArrayList();
        actualPromoDepDropdownlistRespVO.setSubscriberStatusValueList(subscriberStatusValueList);
        assertSame(cardGroupList, actualPromoDepDropdownlistRespVO.getCardGroupList());
        assertSame(cellGroupList, actualPromoDepDropdownlistRespVO.getCellGroupList());
        assertSame(geographicalDomainType, actualPromoDepDropdownlistRespVO.getGeographicalDomainType());
        assertSame(geographyListAll, actualPromoDepDropdownlistRespVO.getGeographyListAll());
        assertSame(gradeListByCategory, actualPromoDepDropdownlistRespVO.getGradeListByCategory());
        assertEquals(1, actualPromoDepDropdownlistRespVO.getNoOfRows());
        assertSame(serviceClassList, actualPromoDepDropdownlistRespVO.getServiceClassList());
        assertSame(serviceProviderGroupList, actualPromoDepDropdownlistRespVO.getServiceProviderGroupList());
        assertSame(subServiceList, actualPromoDepDropdownlistRespVO.getSubServiceList());
        assertSame(subscriberStatusValueList, actualPromoDepDropdownlistRespVO.getSubscriberStatusValueList());
    }
}

