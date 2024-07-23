package com.restapi.c2sservices.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;

import java.util.ArrayList;

import org.junit.Test;

public class ViewScheduleDetailsListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ViewScheduleDetailsListResponseVO}
     *   <li>{@link ViewScheduleDetailsListResponseVO#setScheduleDetailList(ArrayList)}
     *   <li>{@link ViewScheduleDetailsListResponseVO#toString()}
     *   <li>{@link ViewScheduleDetailsListResponseVO#getScheduleDetailList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ViewScheduleDetailsListResponseVO actualViewScheduleDetailsListResponseVO = new ViewScheduleDetailsListResponseVO();
        ArrayList<ScheduleBatchDetailVO> scheduleDetailsList = new ArrayList<>();
        actualViewScheduleDetailsListResponseVO.setScheduleDetailList(scheduleDetailsList);
        String actualToStringResult = actualViewScheduleDetailsListResponseVO.toString();
        assertSame(scheduleDetailsList, actualViewScheduleDetailsListResponseVO.getScheduleDetailList());
        assertEquals("ViewScheduleDetailsListResponseVO [scheduleDetailList=[]]", actualToStringResult);
    }
}

