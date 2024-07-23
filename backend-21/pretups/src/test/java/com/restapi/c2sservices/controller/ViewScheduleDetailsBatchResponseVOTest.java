package com.restapi.c2sservices.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;

import java.util.ArrayList;

import org.junit.Test;

public class ViewScheduleDetailsBatchResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ViewScheduleDetailsBatchResponseVO}
     *   <li>{@link ViewScheduleDetailsBatchResponseVO#setScheduleDetailList(ArrayList)}
     *   <li>{@link ViewScheduleDetailsBatchResponseVO#toString()}
     *   <li>{@link ViewScheduleDetailsBatchResponseVO#getScheduleDetailList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ViewScheduleDetailsBatchResponseVO actualViewScheduleDetailsBatchResponseVO = new ViewScheduleDetailsBatchResponseVO();
        ArrayList<ScheduleBatchMasterVO> scheduleDetailsList = new ArrayList<>();
        actualViewScheduleDetailsBatchResponseVO.setScheduleDetailList(scheduleDetailsList);
        String actualToStringResult = actualViewScheduleDetailsBatchResponseVO.toString();
        assertSame(scheduleDetailsList, actualViewScheduleDetailsBatchResponseVO.getScheduleDetailList());
        assertEquals("ViewScheduleDetailsListResponseVO [scheduleDetailList=[]]", actualToStringResult);
    }
}

