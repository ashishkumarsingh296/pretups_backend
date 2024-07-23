package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GeoDomainDeleteRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GeoDomainDeleteRequestVO}
     *   <li>{@link GeoDomainDeleteRequestVO#setGrphDomainCode(String)}
     *   <li>{@link GeoDomainDeleteRequestVO#setIsDefault(String)}
     *   <li>{@link GeoDomainDeleteRequestVO#getGrphDomainCode()}
     *   <li>{@link GeoDomainDeleteRequestVO#getIsDefault()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GeoDomainDeleteRequestVO actualGeoDomainDeleteRequestVO = new GeoDomainDeleteRequestVO();
        actualGeoDomainDeleteRequestVO.setGrphDomainCode("Grph Domain Code");
        actualGeoDomainDeleteRequestVO.setIsDefault("Is Default");
        assertEquals("Grph Domain Code", actualGeoDomainDeleteRequestVO.getGrphDomainCode());
        assertEquals("Is Default", actualGeoDomainDeleteRequestVO.getIsDefault());
    }
}

