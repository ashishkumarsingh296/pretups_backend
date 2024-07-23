package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class ParentGeoDomainResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ParentGeoDomainResponseVO}
     *   <li>{@link ParentGeoDomainResponseVO#setParentDomainList(ArrayList)}
     *   <li>{@link ParentGeoDomainResponseVO#setParentDomainType(String)}
     *   <li>{@link ParentGeoDomainResponseVO#getParentDomainList()}
     *   <li>{@link ParentGeoDomainResponseVO#getParentDomainType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ParentGeoDomainResponseVO actualParentGeoDomainResponseVO = new ParentGeoDomainResponseVO();
        ArrayList parentDomainList = new ArrayList();
        actualParentGeoDomainResponseVO.setParentDomainList(parentDomainList);
        actualParentGeoDomainResponseVO.setParentDomainType("Parent Domain Type");
        assertSame(parentDomainList, actualParentGeoDomainResponseVO.getParentDomainList());
        assertEquals("Parent Domain Type", actualParentGeoDomainResponseVO.getParentDomainType());
    }
}

