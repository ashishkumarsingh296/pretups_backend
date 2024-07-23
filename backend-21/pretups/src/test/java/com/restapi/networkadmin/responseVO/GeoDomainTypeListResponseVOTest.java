package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertSame;

import com.btsl.common.ListValueVO;

import java.util.ArrayList;

import org.junit.Test;

public class GeoDomainTypeListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GeoDomainTypeListResponseVO}
     *   <li>{@link GeoDomainTypeListResponseVO#setGeoDomTypeList(ArrayList)}
     *   <li>{@link GeoDomainTypeListResponseVO#getGeoDomTypeList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GeoDomainTypeListResponseVO actualGeoDomainTypeListResponseVO = new GeoDomainTypeListResponseVO();
        ArrayList<ListValueVO> geoDomTypeList = new ArrayList<>();
        actualGeoDomainTypeListResponseVO.setGeoDomTypeList(geoDomTypeList);
        assertSame(geoDomTypeList, actualGeoDomainTypeListResponseVO.getGeoDomTypeList());
    }
}

