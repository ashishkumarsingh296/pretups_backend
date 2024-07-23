package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AddCategoryResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link AddCategoryResponseVO}
     *   <li>{@link AddCategoryResponseVO#setAgentAllowed(String)}
     *   <li>{@link AddCategoryResponseVO#setCategoryCode(String)}
     *   <li>{@link AddCategoryResponseVO#setDomainCode(String)}
     *   <li>{@link AddCategoryResponseVO#getAgentAllowed()}
     *   <li>{@link AddCategoryResponseVO#getCategoryCode()}
     *   <li>{@link AddCategoryResponseVO#getDomainCode()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        AddCategoryResponseVO actualAddCategoryResponseVO = new AddCategoryResponseVO();
        actualAddCategoryResponseVO.setAgentAllowed("Agent Allowed");
        actualAddCategoryResponseVO.setCategoryCode("Category Code");
        actualAddCategoryResponseVO.setDomainCode("Domain Code");
        assertEquals("Agent Allowed", actualAddCategoryResponseVO.getAgentAllowed());
        assertEquals("Category Code", actualAddCategoryResponseVO.getCategoryCode());
        assertEquals("Domain Code", actualAddCategoryResponseVO.getDomainCode());
    }
}

