package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ParentGeoDomainRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ParentGeoDomainRequestVO}
     *   <li>{@link ParentGeoDomainRequestVO#setIndex(String)}
     *   <li>{@link ParentGeoDomainRequestVO#setValue(String)}
     *   <li>{@link ParentGeoDomainRequestVO#getIndex()}
     *   <li>{@link ParentGeoDomainRequestVO#getValue()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ParentGeoDomainRequestVO actualParentGeoDomainRequestVO = new ParentGeoDomainRequestVO();
        actualParentGeoDomainRequestVO.setIndex("Index");
        actualParentGeoDomainRequestVO.setValue("42");
        assertEquals("Index", actualParentGeoDomainRequestVO.getIndex());
        assertEquals("42", actualParentGeoDomainRequestVO.getValue());
    }
}

