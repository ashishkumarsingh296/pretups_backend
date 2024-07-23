package com.restapi.superadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class CategoryListRespVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CategoryListRespVO}
     *   <li>{@link CategoryListRespVO#setCategoryList(ArrayList)}
     *   <li>{@link CategoryListRespVO#toString()}
     *   <li>{@link CategoryListRespVO#getCategoryList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CategoryListRespVO actualCategoryListRespVO = new CategoryListRespVO();
        ArrayList categoryList = new ArrayList();
        actualCategoryListRespVO.setCategoryList(categoryList);
        String actualToStringResult = actualCategoryListRespVO.toString();
        assertSame(categoryList, actualCategoryListRespVO.getCategoryList());
        assertEquals("CategoryListRespVO [categoryList=[]]", actualToStringResult);
    }
}

