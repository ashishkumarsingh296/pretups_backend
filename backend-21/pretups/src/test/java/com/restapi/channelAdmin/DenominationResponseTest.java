package com.restapi.channelAdmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class DenominationResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DenominationResponse}
     *   <li>{@link DenominationResponse#setMrpList(ArrayList)}
     *   <li>{@link DenominationResponse#toString()}
     *   <li>{@link DenominationResponse#getMrpList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DenominationResponse actualDenominationResponse = new DenominationResponse();
        ArrayList<String> mrpList = new ArrayList<>();
        actualDenominationResponse.setMrpList(mrpList);
        String actualToStringResult = actualDenominationResponse.toString();
        assertSame(mrpList, actualDenominationResponse.getMrpList());
        assertEquals("DenominationResponse [mrpList=[]]", actualToStringResult);
    }
}

