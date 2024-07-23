package com.restapi.superadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class SMSCprofileResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SMSCprofileResponseVO}
     *   <li>{@link SMSCprofileResponseVO#setSmscProfileList(ArrayList)}
     *   <li>{@link SMSCprofileResponseVO#toString()}
     *   <li>{@link SMSCprofileResponseVO#getSmscProfileList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SMSCprofileResponseVO actualSmsCprofileResponseVO = new SMSCprofileResponseVO();
        ArrayList smscProfileList = new ArrayList();
        actualSmsCprofileResponseVO.setSmscProfileList(smscProfileList);
        String actualToStringResult = actualSmsCprofileResponseVO.toString();
        assertSame(smscProfileList, actualSmsCprofileResponseVO.getSmscProfileList());
        assertEquals("SMSCprofileResponseVO [smscProfileList=[]]", actualToStringResult);
    }
}

