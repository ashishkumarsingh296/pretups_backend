package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ErrorLogTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link ErrorLog#ErrorLog(String, String, String)}
     *   <li>{@link ErrorLog#setLabelName(String)}
     *   <li>{@link ErrorLog#setLineNo(String)}
     *   <li>{@link ErrorLog#setReason(String)}
     *   <li>{@link ErrorLog#toString()}
     *   <li>{@link ErrorLog#getLabelName()}
     *   <li>{@link ErrorLog#getLineNo()}
     *   <li>{@link ErrorLog#getReason()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ErrorLog actualErrorLog = new ErrorLog("Line No", "Label Name", "Just cause");
        actualErrorLog.setLabelName("Label Name");
        actualErrorLog.setLineNo("Line No");
        actualErrorLog.setReason("Just cause");
        String actualToStringResult = actualErrorLog.toString();
        assertEquals("Label Name", actualErrorLog.getLabelName());
        assertEquals("Line No", actualErrorLog.getLineNo());
        assertEquals("Just cause", actualErrorLog.getReason());
        assertEquals("ErrorLog [lineNo=Line No, labelName=Label Name, reason=Just cause]", actualToStringResult);
    }
}

