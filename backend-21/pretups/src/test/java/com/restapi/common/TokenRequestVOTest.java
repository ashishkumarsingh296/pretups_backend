package com.restapi.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TokenRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link TokenRequestVO}
     *   <li>{@link TokenRequestVO#setIdentifierType(String)}
     *   <li>{@link TokenRequestVO#setIdentifierValue(String)}
     *   <li>{@link TokenRequestVO#setPasswordOrSmspin(String)}
     *   <li>{@link TokenRequestVO#toString()}
     *   <li>{@link TokenRequestVO#getIdentifierType()}
     *   <li>{@link TokenRequestVO#getIdentifierValue()}
     *   <li>{@link TokenRequestVO#getPasswordOrSmspin()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        TokenRequestVO actualTokenRequestVO = new TokenRequestVO();
        actualTokenRequestVO.setIdentifierType("Identifier Type");
        actualTokenRequestVO.setIdentifierValue("42");
        actualTokenRequestVO.setPasswordOrSmspin("Password Or Smspin");
        String actualToStringResult = actualTokenRequestVO.toString();
        assertEquals("Identifier Type", actualTokenRequestVO.getIdentifierType());
        assertEquals("42", actualTokenRequestVO.getIdentifierValue());
        assertEquals("Password Or Smspin", actualTokenRequestVO.getPasswordOrSmspin());
        assertEquals(
                "TokenRequestVO [identifierType=Identifier Type, identifierValue=42, passwordOrSmspin=Password" + " Or Smspin]",
                actualToStringResult);
    }
}

