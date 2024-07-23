package com.restapi.staffuser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class FetchStaffUserResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link FetchStaffUserResponse}
     *   <li>{@link FetchStaffUserResponse#setRolesList(Map)}
     *   <li>{@link FetchStaffUserResponse#toString()}
     *   <li>{@link FetchStaffUserResponse#getRolesList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        FetchStaffUserResponse actualFetchStaffUserResponse = new FetchStaffUserResponse();
        HashMap<Object, Object> rolesList = new HashMap<>();
        actualFetchStaffUserResponse.setRolesList(rolesList);
        String actualToStringResult = actualFetchStaffUserResponse.toString();
        assertSame(rolesList, actualFetchStaffUserResponse.getRolesList());
        assertEquals("FetchStaffUserResponse [rolesList={}]", actualToStringResult);
    }
}

