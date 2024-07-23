package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertNull;

import org.junit.Test;

public class DeletePromoTransferReqVOTest {
    /**
     * Method under test: default or parameterless constructor of {@link DeletePromoTransferReqVO}
     */
    @Test
    public void testConstructor() {
        DeletePromoTransferReqVO actualDeletePromoTransferReqVO = new DeletePromoTransferReqVO();
        assertNull(actualDeletePromoTransferReqVO.getCategoryCode());
        assertNull(actualDeletePromoTransferReqVO.getUserID());
        assertNull(actualDeletePromoTransferReqVO.getPromotionalLevel());
        assertNull(actualDeletePromoTransferReqVO.getOptionTab());
        assertNull(actualDeletePromoTransferReqVO.getList());
        assertNull(actualDeletePromoTransferReqVO.getGrade());
        assertNull(actualDeletePromoTransferReqVO.getGeography());
        assertNull(actualDeletePromoTransferReqVO.getGeoGraphyDomainType());
        assertNull(actualDeletePromoTransferReqVO.getDomainCode());
        assertNull(actualDeletePromoTransferReqVO.getCellGroupID());
    }
}

