package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SearchPromoTransferRespVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SearchPromoTransferRespVO}
     *   <li>{@link SearchPromoTransferRespVO#setListSearchPromoData(List)}
     *   <li>{@link SearchPromoTransferRespVO#getListSearchPromoData()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SearchPromoTransferRespVO actualSearchPromoTransferRespVO = new SearchPromoTransferRespVO();
        ArrayList<PromoTransferSearchVO> listSearchPromoData = new ArrayList<>();
        actualSearchPromoTransferRespVO.setListSearchPromoData(listSearchPromoData);
        assertSame(listSearchPromoData, actualSearchPromoTransferRespVO.getListSearchPromoData());
    }
}

