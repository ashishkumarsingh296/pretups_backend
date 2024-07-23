package com.restapi.user.service;

import static org.junit.Assert.assertSame;

import java.util.HashMap;

import org.junit.Test;

public class RegularExpressionResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link RegularExpressionResponseVO}
     *   <li>{@link RegularExpressionResponseVO#setRegMap(HashMap)}
     *   <li>{@link RegularExpressionResponseVO#getRegMap()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        RegularExpressionResponseVO actualRegularExpressionResponseVO = new RegularExpressionResponseVO();
        HashMap<String, Object> regMap = new HashMap<>();
        actualRegularExpressionResponseVO.setRegMap(regMap);
        assertSame(regMap, actualRegularExpressionResponseVO.getRegMap());
    }
}

