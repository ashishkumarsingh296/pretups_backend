package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PromoTransferDropdownListReqTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link PromoTransferDropdownListReq}
     *   <li>{@link PromoTransferDropdownListReq#setCategoryCode(String)}
     *   <li>{@link PromoTransferDropdownListReq#setDomainCode(String)}
     *   <li>{@link PromoTransferDropdownListReq#getCategoryCode()}
     *   <li>{@link PromoTransferDropdownListReq#getDomainCode()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        PromoTransferDropdownListReq actualPromoTransferDropdownListReq = new PromoTransferDropdownListReq();
        actualPromoTransferDropdownListReq.setCategoryCode("Category Code");
        actualPromoTransferDropdownListReq.setDomainCode("Domain Code");
        assertEquals("Category Code", actualPromoTransferDropdownListReq.getCategoryCode());
        assertEquals("Domain Code", actualPromoTransferDropdownListReq.getDomainCode());
    }
}

