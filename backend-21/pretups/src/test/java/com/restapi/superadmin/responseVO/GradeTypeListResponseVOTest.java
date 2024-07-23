package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;

import java.util.ArrayList;

import org.junit.Test;

public class GradeTypeListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GradeTypeListResponseVO}
     *   <li>{@link GradeTypeListResponseVO#setCategoryList(ArrayList)}
     *   <li>{@link GradeTypeListResponseVO#setDomainList(ArrayList)}
     *   <li>{@link GradeTypeListResponseVO#setGradeList(ArrayList)}
     *   <li>{@link GradeTypeListResponseVO#toString()}
     *   <li>{@link GradeTypeListResponseVO#getCategoryList()}
     *   <li>{@link GradeTypeListResponseVO#getDomainList()}
     *   <li>{@link GradeTypeListResponseVO#getGradeList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GradeTypeListResponseVO actualGradeTypeListResponseVO = new GradeTypeListResponseVO();
        ArrayList<ListValueVO> categoryList = new ArrayList<>();
        actualGradeTypeListResponseVO.setCategoryList(categoryList);
        ArrayList<DomainVO> domainList = new ArrayList<>();
        actualGradeTypeListResponseVO.setDomainList(domainList);
        ArrayList<GradeVO> gradeList = new ArrayList<>();
        actualGradeTypeListResponseVO.setGradeList(gradeList);
        String actualToStringResult = actualGradeTypeListResponseVO.toString();
        assertSame(categoryList, actualGradeTypeListResponseVO.getCategoryList());
        assertSame(domainList, actualGradeTypeListResponseVO.getDomainList());
        assertSame(gradeList, actualGradeTypeListResponseVO.getGradeList());
        assertEquals("GradeTypeListResponseVO [domainList=[]]GradeTypeListResponseVO [categoryList=[]]",
                actualToStringResult);
    }
}

