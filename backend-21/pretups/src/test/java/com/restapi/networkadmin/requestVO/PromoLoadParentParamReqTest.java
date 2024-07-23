package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PromoLoadParentParamReqTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link PromoLoadParentParamReq}
     *   <li>{@link PromoLoadParentParamReq#setCategoryCode(String)}
     *   <li>{@link PromoLoadParentParamReq#setGeoDomainCode(String)}
     *   <li>{@link PromoLoadParentParamReq#setUserName(String)}
     *   <li>{@link PromoLoadParentParamReq#getCategoryCode()}
     *   <li>{@link PromoLoadParentParamReq#getGeoDomainCode()}
     *   <li>{@link PromoLoadParentParamReq#getUserName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        PromoLoadParentParamReq actualPromoLoadParentParamReq = new PromoLoadParentParamReq();
        actualPromoLoadParentParamReq.setCategoryCode("Category Code");
        actualPromoLoadParentParamReq.setGeoDomainCode("Geo Domain Code");
        actualPromoLoadParentParamReq.setUserName("janedoe");
        assertEquals("Category Code", actualPromoLoadParentParamReq.getCategoryCode());
        assertEquals("Geo Domain Code", actualPromoLoadParentParamReq.getGeoDomainCode());
        assertEquals("janedoe", actualPromoLoadParentParamReq.getUserName());
    }
}

