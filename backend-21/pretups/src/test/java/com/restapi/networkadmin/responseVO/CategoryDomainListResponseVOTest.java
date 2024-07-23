package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class CategoryDomainListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CategoryDomainListResponseVO}
     *   <li>{@link CategoryDomainListResponseVO#setCategoryDomainList(ArrayList)}
     *   <li>{@link CategoryDomainListResponseVO#setNetworkCode(String)}
     *   <li>{@link CategoryDomainListResponseVO#setNetworkDescription(String)}
     *   <li>{@link CategoryDomainListResponseVO#setType(String)}
     *   <li>{@link CategoryDomainListResponseVO#setUserCategory(String)}
     *   <li>{@link CategoryDomainListResponseVO#getCategoryDomainList()}
     *   <li>{@link CategoryDomainListResponseVO#getNetworkCode()}
     *   <li>{@link CategoryDomainListResponseVO#getNetworkDescription()}
     *   <li>{@link CategoryDomainListResponseVO#getType()}
     *   <li>{@link CategoryDomainListResponseVO#getUserCategory()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CategoryDomainListResponseVO actualCategoryDomainListResponseVO = new CategoryDomainListResponseVO();
        ArrayList categoryDomainList = new ArrayList();
        actualCategoryDomainListResponseVO.setCategoryDomainList(categoryDomainList);
        actualCategoryDomainListResponseVO.setNetworkCode("Network Code");
        actualCategoryDomainListResponseVO.setNetworkDescription("Network Description");
        actualCategoryDomainListResponseVO.setType("Type");
        actualCategoryDomainListResponseVO.setUserCategory("User Category");
        assertSame(categoryDomainList, actualCategoryDomainListResponseVO.getCategoryDomainList());
        assertEquals("Network Code", actualCategoryDomainListResponseVO.getNetworkCode());
        assertEquals("Network Description", actualCategoryDomainListResponseVO.getNetworkDescription());
        assertEquals("Type", actualCategoryDomainListResponseVO.getType());
        assertEquals("User Category", actualCategoryDomainListResponseVO.getUserCategory());
    }
}

