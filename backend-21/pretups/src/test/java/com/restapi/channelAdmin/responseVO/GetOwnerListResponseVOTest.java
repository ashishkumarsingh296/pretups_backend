package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.user.businesslogic.UserVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class GetOwnerListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GetOwnerListResponseVO}
     *   <li>{@link GetOwnerListResponseVO#setOwnerList(List)}
     *   <li>{@link GetOwnerListResponseVO#toString()}
     *   <li>{@link GetOwnerListResponseVO#getOwnerList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GetOwnerListResponseVO actualGetOwnerListResponseVO = new GetOwnerListResponseVO();
        ArrayList<UserVO> ownerList = new ArrayList<>();
        actualGetOwnerListResponseVO.setOwnerList(ownerList);
        String actualToStringResult = actualGetOwnerListResponseVO.toString();
        assertSame(ownerList, actualGetOwnerListResponseVO.getOwnerList());
        assertEquals("getOwnerListResponseVO [ownerList=[]]", actualToStringResult);
    }
}

