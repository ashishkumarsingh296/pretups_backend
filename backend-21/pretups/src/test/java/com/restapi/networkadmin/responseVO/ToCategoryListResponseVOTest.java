package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class ToCategoryListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ToCategoryListResponseVO}
     *   <li>{@link ToCategoryListResponseVO#setCategoryList(ArrayList)}
     *   <li>{@link ToCategoryListResponseVO#setProductList(ArrayList)}
     *   <li>{@link ToCategoryListResponseVO#getCategoryList()}
     *   <li>{@link ToCategoryListResponseVO#getProductList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ToCategoryListResponseVO actualToCategoryListResponseVO = new ToCategoryListResponseVO();
        ArrayList categoryList = new ArrayList();
        actualToCategoryListResponseVO.setCategoryList(categoryList);
        ArrayList productList = new ArrayList();
        actualToCategoryListResponseVO.setProductList(productList);
        assertSame(categoryList, actualToCategoryListResponseVO.getCategoryList());
        assertSame(productList, actualToCategoryListResponseVO.getProductList());
    }
}

