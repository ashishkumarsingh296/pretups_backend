package com.btsl.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BaseExceptionTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link BaseException#BaseException(String)}
     *   <li>{@link BaseException#setErrorCode(String)}
     *   <li>{@link BaseException#getErrorCode()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BaseException actualBaseException = new BaseException("An error occurred");
        actualBaseException.setErrorCode("Code");
        assertEquals("Code", actualBaseException.getErrorCode());
    }
}

