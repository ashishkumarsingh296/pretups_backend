package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class AddPromoTransferReqVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link AddPromoTransferReqVO}
     *   <li>{@link AddPromoTransferReqVO#setCategoryCode(String)}
     *   <li>{@link AddPromoTransferReqVO#setCellGroupID(String)}
     *   <li>{@link AddPromoTransferReqVO#setDomainCode(String)}
     *   <li>{@link AddPromoTransferReqVO#setGeoGraphyDomainType(String)}
     *   <li>{@link AddPromoTransferReqVO#setGeography(String)}
     *   <li>{@link AddPromoTransferReqVO#setGrade(String)}
     *   <li>{@link AddPromoTransferReqVO#setList(ArrayList)}
     *   <li>{@link AddPromoTransferReqVO#setOptionTab(String)}
     *   <li>{@link AddPromoTransferReqVO#setPromotionalLevel(String)}
     *   <li>{@link AddPromoTransferReqVO#setUserID(String)}
     *   <li>{@link AddPromoTransferReqVO#getCategoryCode()}
     *   <li>{@link AddPromoTransferReqVO#getCellGroupID()}
     *   <li>{@link AddPromoTransferReqVO#getDomainCode()}
     *   <li>{@link AddPromoTransferReqVO#getGeoGraphyDomainType()}
     *   <li>{@link AddPromoTransferReqVO#getGeography()}
     *   <li>{@link AddPromoTransferReqVO#getGrade()}
     *   <li>{@link AddPromoTransferReqVO#getList()}
     *   <li>{@link AddPromoTransferReqVO#getOptionTab()}
     *   <li>{@link AddPromoTransferReqVO#getPromotionalLevel()}
     *   <li>{@link AddPromoTransferReqVO#getUserID()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        AddPromoTransferReqVO actualAddPromoTransferReqVO = new AddPromoTransferReqVO();
        actualAddPromoTransferReqVO.setCategoryCode("Category Code");
        actualAddPromoTransferReqVO.setCellGroupID("Cell Group ID");
        actualAddPromoTransferReqVO.setDomainCode("Domain Code");
        actualAddPromoTransferReqVO.setGeoGraphyDomainType("Geo Graphy Domain Type");
        actualAddPromoTransferReqVO.setGeography("Geography");
        actualAddPromoTransferReqVO.setGrade("Grade");
        ArrayList<ReceiverSectionInputs> list = new ArrayList<>();
        actualAddPromoTransferReqVO.setList(list);
        actualAddPromoTransferReqVO.setOptionTab("Option Tab");
        actualAddPromoTransferReqVO.setPromotionalLevel("Promotional Level");
        actualAddPromoTransferReqVO.setUserID("User ID");
        assertEquals("Category Code", actualAddPromoTransferReqVO.getCategoryCode());
        assertEquals("Cell Group ID", actualAddPromoTransferReqVO.getCellGroupID());
        assertEquals("Domain Code", actualAddPromoTransferReqVO.getDomainCode());
        assertEquals("Geo Graphy Domain Type", actualAddPromoTransferReqVO.getGeoGraphyDomainType());
        assertEquals("Geography", actualAddPromoTransferReqVO.getGeography());
        assertEquals("Grade", actualAddPromoTransferReqVO.getGrade());
        assertSame(list, actualAddPromoTransferReqVO.getList());
        assertEquals("Option Tab", actualAddPromoTransferReqVO.getOptionTab());
        assertEquals("Promotional Level", actualAddPromoTransferReqVO.getPromotionalLevel());
        assertEquals("User ID", actualAddPromoTransferReqVO.getUserID());
    }
}

