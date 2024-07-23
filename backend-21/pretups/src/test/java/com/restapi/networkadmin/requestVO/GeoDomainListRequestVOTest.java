package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertSame;

import org.junit.Test;

public class GeoDomainListRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GeoDomainListRequestVO}
     *   <li>{@link GeoDomainListRequestVO#setParentTypeList(String[])}
     *   <li>{@link GeoDomainListRequestVO#getParentTypeList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GeoDomainListRequestVO actualGeoDomainListRequestVO = new GeoDomainListRequestVO();
        String[] parentTypeList = new String[]{"Parent Type List"};
        actualGeoDomainListRequestVO.setParentTypeList(parentTypeList);
        assertSame(parentTypeList, actualGeoDomainListRequestVO.getParentTypeList());
    }
}

