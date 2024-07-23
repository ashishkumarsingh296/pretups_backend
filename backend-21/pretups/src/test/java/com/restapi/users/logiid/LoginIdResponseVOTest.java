package com.restapi.users.logiid;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LoginIdResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link LoginIdResponseVO}
     *   <li>{@link LoginIdResponseVO#setListLoginIdNew(List)}
     *   <li>{@link LoginIdResponseVO#setLoginIdExist(boolean)}
     *   <li>{@link LoginIdResponseVO#getListLoginIdNew()}
     *   <li>{@link LoginIdResponseVO#getLoginIdExist()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        LoginIdResponseVO actualLoginIdResponseVO = new LoginIdResponseVO();
        ArrayList<String> listLoginIdNew = new ArrayList<>();
        actualLoginIdResponseVO.setListLoginIdNew(listLoginIdNew);
        actualLoginIdResponseVO.setLoginIdExist(true);
        assertSame(listLoginIdNew, actualLoginIdResponseVO.getListLoginIdNew());
        assertTrue(actualLoginIdResponseVO.getLoginIdExist());
    }
}

