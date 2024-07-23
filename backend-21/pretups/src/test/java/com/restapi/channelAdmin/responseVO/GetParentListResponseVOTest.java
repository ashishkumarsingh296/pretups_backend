package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.common.ListValueVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class GetParentListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GetParentListResponseVO}
     *   <li>{@link GetParentListResponseVO#setParentList(List)}
     *   <li>{@link GetParentListResponseVO#toString()}
     *   <li>{@link GetParentListResponseVO#getParentList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GetParentListResponseVO actualGetParentListResponseVO = new GetParentListResponseVO();
        ArrayList<ListValueVO> parentList = new ArrayList<>();
        actualGetParentListResponseVO.setParentList(parentList);
        String actualToStringResult = actualGetParentListResponseVO.toString();
        assertSame(parentList, actualGetParentListResponseVO.getParentList());
        assertEquals("getParentListResponseVO [parentList=[]]", actualToStringResult);
    }
}

