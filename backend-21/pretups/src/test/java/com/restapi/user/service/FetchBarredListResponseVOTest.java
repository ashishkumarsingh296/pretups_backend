package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

public class FetchBarredListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link FetchBarredListResponseVO}
     *   <li>{@link FetchBarredListResponseVO#setBarredList(HashMap)}
     *   <li>{@link FetchBarredListResponseVO#toString()}
     *   <li>{@link FetchBarredListResponseVO#getBarredList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        FetchBarredListResponseVO actualFetchBarredListResponseVO = new FetchBarredListResponseVO();
        HashMap<String, ArrayList<BarredVo>> barredList = new HashMap<>();
        actualFetchBarredListResponseVO.setBarredList(barredList);
        String actualToStringResult = actualFetchBarredListResponseVO.toString();
        assertSame(barredList, actualFetchBarredListResponseVO.getBarredList());
        assertEquals("FetchBarredListResponseVO [barredList={}]", actualToStringResult);
    }
}

