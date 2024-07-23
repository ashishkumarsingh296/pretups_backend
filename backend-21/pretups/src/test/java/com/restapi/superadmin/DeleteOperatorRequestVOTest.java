package com.restapi.superadmin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DeleteOperatorRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DeleteOperatorRequestVO}
     *   <li>{@link DeleteOperatorRequestVO#setId(String)}
     *   <li>{@link DeleteOperatorRequestVO#setLastModified(String)}
     *   <li>{@link DeleteOperatorRequestVO#setStatus(String)}
     *   <li>{@link DeleteOperatorRequestVO#setUserId(String)}
     *   <li>{@link DeleteOperatorRequestVO#setUserName(String)}
     *   <li>{@link DeleteOperatorRequestVO#toString()}
     *   <li>{@link DeleteOperatorRequestVO#getId()}
     *   <li>{@link DeleteOperatorRequestVO#getLastModified()}
     *   <li>{@link DeleteOperatorRequestVO#getStatus()}
     *   <li>{@link DeleteOperatorRequestVO#getUserId()}
     *   <li>{@link DeleteOperatorRequestVO#getUserName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DeleteOperatorRequestVO actualDeleteOperatorRequestVO = new DeleteOperatorRequestVO();
        actualDeleteOperatorRequestVO.setId("42");
        actualDeleteOperatorRequestVO.setLastModified("Jan 1, 2020 9:00am GMT+0100");
        actualDeleteOperatorRequestVO.setStatus("Status");
        actualDeleteOperatorRequestVO.setUserId("42");
        actualDeleteOperatorRequestVO.setUserName("janedoe");
        String actualToStringResult = actualDeleteOperatorRequestVO.toString();
        assertEquals("42", actualDeleteOperatorRequestVO.getId());
        assertEquals("Jan 1, 2020 9:00am GMT+0100", actualDeleteOperatorRequestVO.getLastModified());
        assertEquals("Status", actualDeleteOperatorRequestVO.getStatus());
        assertEquals("42", actualDeleteOperatorRequestVO.getUserId());
        assertEquals("janedoe", actualDeleteOperatorRequestVO.getUserName());
        assertEquals("DeleteOperatorRequestVO [userId=42, lastModified=Jan 1, 2020 9:00am GMT+0100, status=Status, id=42,"
                + " userName=janedoe]", actualToStringResult);
    }
}

