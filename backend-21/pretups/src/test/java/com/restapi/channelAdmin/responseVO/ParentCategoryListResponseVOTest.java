package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.btsl.pretups.domain.businesslogic.CategoryVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ParentCategoryListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ParentCategoryListResponseVO}
     *   <li>{@link ParentCategoryListResponseVO#setNotApplicable(boolean)}
     *   <li>{@link ParentCategoryListResponseVO#setParentCategoryList(List)}
     *   <li>{@link ParentCategoryListResponseVO#toString()}
     *   <li>{@link ParentCategoryListResponseVO#getParentCategoryList()}
     *   <li>{@link ParentCategoryListResponseVO#isNotApplicable()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ParentCategoryListResponseVO actualParentCategoryListResponseVO = new ParentCategoryListResponseVO();
        actualParentCategoryListResponseVO.setNotApplicable(true);
        ArrayList<CategoryVO> parentCategoryList = new ArrayList<>();
        actualParentCategoryListResponseVO.setParentCategoryList(parentCategoryList);
        String actualToStringResult = actualParentCategoryListResponseVO.toString();
        assertSame(parentCategoryList, actualParentCategoryListResponseVO.getParentCategoryList());
        assertTrue(actualParentCategoryListResponseVO.isNotApplicable());
        assertEquals("GetParentCategoryListResponseVO [parentCategoryList=[], notApplicable=true]", actualToStringResult);
    }
}

