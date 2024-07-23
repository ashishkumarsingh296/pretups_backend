package com.restapi.superadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.common.ListValueVO;

import java.util.ArrayList;

import org.junit.Test;

public class DomainListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DomainListResponseVO}
     *   <li>{@link DomainListResponseVO#setDomainTypeList(ArrayList)}
     *   <li>{@link DomainListResponseVO#toString()}
     *   <li>{@link DomainListResponseVO#getDomainTypeList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DomainListResponseVO actualDomainListResponseVO = new DomainListResponseVO();
        ArrayList<ListValueVO> domainTypeList = new ArrayList<>();
        actualDomainListResponseVO.setDomainTypeList(domainTypeList);
        String actualToStringResult = actualDomainListResponseVO.toString();
        assertSame(domainTypeList, actualDomainListResponseVO.getDomainTypeList());
        assertEquals("DomainListResponseVO [domainTypeList=[]]", actualToStringResult);
    }
}

