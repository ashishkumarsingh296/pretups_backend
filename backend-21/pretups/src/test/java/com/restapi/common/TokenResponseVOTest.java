package com.restapi.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TokenResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link TokenResponseVO}
     *   <li>{@link TokenResponseVO#setMessage(String)}
     *   <li>{@link TokenResponseVO#setMessageCode(String)}
     *   <li>{@link TokenResponseVO#setRefreshToken(String)}
     *   <li>{@link TokenResponseVO#setStatus(String)}
     *   <li>{@link TokenResponseVO#setToken(String)}
     *   <li>{@link TokenResponseVO#toString()}
     *   <li>{@link TokenResponseVO#getMessage()}
     *   <li>{@link TokenResponseVO#getMessageCode()}
     *   <li>{@link TokenResponseVO#getRefreshToken()}
     *   <li>{@link TokenResponseVO#getStatus()}
     *   <li>{@link TokenResponseVO#getToken()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        TokenResponseVO actualTokenResponseVO = new TokenResponseVO();
        actualTokenResponseVO.setMessage("Not all who wander are lost");
        actualTokenResponseVO.setMessageCode("Message Code");
        actualTokenResponseVO.setRefreshToken("ABC123");
        actualTokenResponseVO.setStatus("Status");
        actualTokenResponseVO.setToken("ABC123");
        String actualToStringResult = actualTokenResponseVO.toString();
        assertEquals("Not all who wander are lost", actualTokenResponseVO.getMessage());
        assertEquals("Message Code", actualTokenResponseVO.getMessageCode());
        assertEquals("ABC123", actualTokenResponseVO.getRefreshToken());
        assertEquals("Status", actualTokenResponseVO.getStatus());
        assertEquals("ABC123", actualTokenResponseVO.getToken());
        assertEquals(
                "TokenResponseVO [token=ABC123, refreshToken=ABC123, status=Status, messageCode=Message Code, message=Not"
                        + " all who wander are lost]",
                actualToStringResult);
    }
}

