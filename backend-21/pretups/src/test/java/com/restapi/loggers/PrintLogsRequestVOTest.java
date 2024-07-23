package com.restapi.loggers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class PrintLogsRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link PrintLogsRequestVO}
     *   <li>{@link PrintLogsRequestVO#setLogData(ArrayList)}
     *   <li>{@link PrintLogsRequestVO#toString()}
     *   <li>{@link PrintLogsRequestVO#getLogData()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        PrintLogsRequestVO actualPrintLogsRequestVO = new PrintLogsRequestVO();
        ArrayList<LogVO> logData = new ArrayList<>();
        actualPrintLogsRequestVO.setLogData(logData);
        String actualToStringResult = actualPrintLogsRequestVO.toString();
        assertSame(logData, actualPrintLogsRequestVO.getLogData());
        assertEquals("PrintLogsRequestVO [logData=[]]", actualToStringResult);
    }
}

