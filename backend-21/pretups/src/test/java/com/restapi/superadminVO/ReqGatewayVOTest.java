package com.restapi.superadminVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.Test;

public class ReqGatewayVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ReqGatewayVO}
     *   <li>{@link ReqGatewayVO#setAuthType(String)}
     *   <li>{@link ReqGatewayVO#setConfirmPassword(String)}
     *   <li>{@link ReqGatewayVO#setContentType(String)}
     *   <li>{@link ReqGatewayVO#setCreatedBy(String)}
     *   <li>{@link ReqGatewayVO#setCreatedOn(Date)}
     *   <li>{@link ReqGatewayVO#setDecryptedPassword(String)}
     *   <li>{@link ReqGatewayVO#setEncryptionKey(String)}
     *   <li>{@link ReqGatewayVO#setEncryptionLevel(String)}
     *   <li>{@link ReqGatewayVO#setGatewayCode(String)}
     *   <li>{@link ReqGatewayVO#setLastModifiedTime(long)}
     *   <li>{@link ReqGatewayVO#setLoginID(String)}
     *   <li>{@link ReqGatewayVO#setModifiedBy(String)}
     *   <li>{@link ReqGatewayVO#setModifiedOn(Date)}
     *   <li>{@link ReqGatewayVO#setOldPassword(String)}
     *   <li>{@link ReqGatewayVO#setPassword(String)}
     *   <li>{@link ReqGatewayVO#setStatus(String)}
     *   <li>{@link ReqGatewayVO#setUnderProcessCheckReqd(String)}
     *   <li>{@link ReqGatewayVO#setUpdatePassword(String)}
     *   <li>{@link ReqGatewayVO#getAuthType()}
     *   <li>{@link ReqGatewayVO#getConfirmPassword()}
     *   <li>{@link ReqGatewayVO#getContentType()}
     *   <li>{@link ReqGatewayVO#getCreatedBy()}
     *   <li>{@link ReqGatewayVO#getCreatedOn()}
     *   <li>{@link ReqGatewayVO#getDecryptedPassword()}
     *   <li>{@link ReqGatewayVO#getEncryptionKey()}
     *   <li>{@link ReqGatewayVO#getEncryptionLevel()}
     *   <li>{@link ReqGatewayVO#getGatewayCode()}
     *   <li>{@link ReqGatewayVO#getLastModifiedTime()}
     *   <li>{@link ReqGatewayVO#getLoginID()}
     *   <li>{@link ReqGatewayVO#getModifiedBy()}
     *   <li>{@link ReqGatewayVO#getModifiedOn()}
     *   <li>{@link ReqGatewayVO#getOldPassword()}
     *   <li>{@link ReqGatewayVO#getPassword()}
     *   <li>{@link ReqGatewayVO#getPort()}
     *   <li>{@link ReqGatewayVO#getServicePort()}
     *   <li>{@link ReqGatewayVO#getStatus()}
     *   <li>{@link ReqGatewayVO#getUnderProcessCheckReqd()}
     *   <li>{@link ReqGatewayVO#getUpdatePassword()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ReqGatewayVO actualReqGatewayVO = new ReqGatewayVO();
        actualReqGatewayVO.setAuthType("Auth Type");
        actualReqGatewayVO.setConfirmPassword("iloveyou");
        actualReqGatewayVO.setContentType("text/plain");
        actualReqGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        Date createdOn = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        actualReqGatewayVO.setCreatedOn(createdOn);
        actualReqGatewayVO.setDecryptedPassword("iloveyou");
        actualReqGatewayVO.setEncryptionKey("Encryption Key");
        actualReqGatewayVO.setEncryptionLevel("Encryption Level");
        actualReqGatewayVO.setGatewayCode("Gateway Code");
        actualReqGatewayVO.setLastModifiedTime(1L);
        actualReqGatewayVO.setLoginID("Login ID");
        actualReqGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        Date modifiedOn = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        actualReqGatewayVO.setModifiedOn(modifiedOn);
        actualReqGatewayVO.setOldPassword("iloveyou");
        actualReqGatewayVO.setPassword("iloveyou");
        actualReqGatewayVO.setStatus("Status");
        actualReqGatewayVO.setUnderProcessCheckReqd("Under Process Check Reqd");
        actualReqGatewayVO.setUpdatePassword("iloveyou");
        assertEquals("Auth Type", actualReqGatewayVO.getAuthType());
        assertEquals("iloveyou", actualReqGatewayVO.getConfirmPassword());
        assertEquals("text/plain", actualReqGatewayVO.getContentType());
        assertEquals("Jan 1, 2020 8:00am GMT+0100", actualReqGatewayVO.getCreatedBy());
        assertSame(createdOn, actualReqGatewayVO.getCreatedOn());
        assertEquals("iloveyou", actualReqGatewayVO.getDecryptedPassword());
        assertEquals("Encryption Key", actualReqGatewayVO.getEncryptionKey());
        assertEquals("Encryption Level", actualReqGatewayVO.getEncryptionLevel());
        assertEquals("Gateway Code", actualReqGatewayVO.getGatewayCode());
        assertEquals(1L, actualReqGatewayVO.getLastModifiedTime());
        assertEquals("Login ID", actualReqGatewayVO.getLoginID());
        assertEquals("Jan 1, 2020 9:00am GMT+0100", actualReqGatewayVO.getModifiedBy());
        assertSame(modifiedOn, actualReqGatewayVO.getModifiedOn());
        assertEquals("iloveyou", actualReqGatewayVO.getOldPassword());
        assertEquals("iloveyou", actualReqGatewayVO.getPassword());
        assertNull(actualReqGatewayVO.getPort());
        assertNull(actualReqGatewayVO.getServicePort());
        assertEquals("Status", actualReqGatewayVO.getStatus());
        assertEquals("Under Process Check Reqd", actualReqGatewayVO.getUnderProcessCheckReqd());
        assertEquals("iloveyou", actualReqGatewayVO.getUpdatePassword());
    }

    /**
     * Method under test: {@link ReqGatewayVO#setPort(String)}
     */
    @Test
    public void testSetPort() {
        ReqGatewayVO reqGatewayVO = new ReqGatewayVO();
        reqGatewayVO.setPort("Port");
        assertEquals("Port", reqGatewayVO.getPort());
    }

    /**
     * Method under test: {@link ReqGatewayVO#setPort(String)}
     */
    @Test
    public void testSetPort2() {
        ReqGatewayVO reqGatewayVO = new ReqGatewayVO();
        reqGatewayVO.setAuthType("Auth Type");
        reqGatewayVO.setConfirmPassword("iloveyou");
        reqGatewayVO.setContentType("text/plain");
        reqGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        Date createdOn = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        reqGatewayVO.setCreatedOn(createdOn);
        reqGatewayVO.setDecryptedPassword("iloveyou");
        reqGatewayVO.setEncryptionKey("Encryption Key");
        reqGatewayVO.setEncryptionLevel("Encryption Level");
        reqGatewayVO.setGatewayCode("Gateway Code");
        reqGatewayVO.setLastModifiedTime(1L);
        reqGatewayVO.setLoginID("Login ID");
        reqGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        Date modifiedOn = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        reqGatewayVO.setModifiedOn(modifiedOn);
        reqGatewayVO.setOldPassword("iloveyou");
        reqGatewayVO.setPassword("iloveyou");
        reqGatewayVO.setPort("Port");
        reqGatewayVO.setServicePort("Service Port");
        reqGatewayVO.setStatus("Status");
        reqGatewayVO.setUnderProcessCheckReqd("Under Process Check Reqd");
        reqGatewayVO.setUpdatePassword("iloveyou");
        reqGatewayVO.setPort(null);
        assertEquals("Auth Type", reqGatewayVO.getAuthType());
        assertEquals("iloveyou", reqGatewayVO.getUpdatePassword());
        assertEquals("Under Process Check Reqd", reqGatewayVO.getUnderProcessCheckReqd());
        assertEquals("Status", reqGatewayVO.getStatus());
        assertEquals("Service Port", reqGatewayVO.getServicePort());
        assertEquals("Port", reqGatewayVO.getPort());
        assertEquals("iloveyou", reqGatewayVO.getPassword());
        assertEquals("iloveyou", reqGatewayVO.getOldPassword());
        assertSame(modifiedOn, reqGatewayVO.getModifiedOn());
        assertEquals("Jan 1, 2020 9:00am GMT+0100", reqGatewayVO.getModifiedBy());
        assertEquals("Login ID", reqGatewayVO.getLoginID());
        assertEquals(1L, reqGatewayVO.getLastModifiedTime());
        assertEquals("Gateway Code", reqGatewayVO.getGatewayCode());
        assertEquals("Encryption Level", reqGatewayVO.getEncryptionLevel());
        assertEquals("Encryption Key", reqGatewayVO.getEncryptionKey());
        assertEquals("iloveyou", reqGatewayVO.getDecryptedPassword());
        assertSame(createdOn, reqGatewayVO.getCreatedOn());
        assertEquals("Jan 1, 2020 8:00am GMT+0100", reqGatewayVO.getCreatedBy());
        assertEquals("text/plain", reqGatewayVO.getContentType());
        assertEquals("iloveyou", reqGatewayVO.getConfirmPassword());
    }

    /**
     * Method under test: {@link ReqGatewayVO#setPort(String)}
     */
    @Test
    public void testSetPort3() {
        ReqGatewayVO reqGatewayVO = new ReqGatewayVO();
        reqGatewayVO.setAuthType("Auth Type");
        reqGatewayVO.setConfirmPassword("iloveyou");
        reqGatewayVO.setContentType("text/plain");
        reqGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        reqGatewayVO.setCreatedOn(mock(java.sql.Date.class));
        reqGatewayVO.setDecryptedPassword("iloveyou");
        reqGatewayVO.setEncryptionKey("Encryption Key");
        reqGatewayVO.setEncryptionLevel("Encryption Level");
        reqGatewayVO.setGatewayCode("Gateway Code");
        reqGatewayVO.setLastModifiedTime(1L);
        reqGatewayVO.setLoginID("Login ID");
        reqGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        java.util.Date modifiedOn = java.util.Date
                .from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        reqGatewayVO.setModifiedOn(modifiedOn);
        reqGatewayVO.setOldPassword("iloveyou");
        reqGatewayVO.setPassword("iloveyou");
        reqGatewayVO.setPort("Port");
        reqGatewayVO.setServicePort("Service Port");
        reqGatewayVO.setStatus("Status");
        reqGatewayVO.setUnderProcessCheckReqd("Under Process Check Reqd");
        reqGatewayVO.setUpdatePassword("iloveyou");
        reqGatewayVO.setPort(null);
        assertEquals("Auth Type", reqGatewayVO.getAuthType());
        assertEquals("iloveyou", reqGatewayVO.getUpdatePassword());
        assertEquals("Under Process Check Reqd", reqGatewayVO.getUnderProcessCheckReqd());
        assertEquals("Status", reqGatewayVO.getStatus());
        assertEquals("Service Port", reqGatewayVO.getServicePort());
        assertEquals("Port", reqGatewayVO.getPort());
        assertEquals("iloveyou", reqGatewayVO.getPassword());
        assertEquals("iloveyou", reqGatewayVO.getOldPassword());
        assertSame(modifiedOn, reqGatewayVO.getModifiedOn());
        assertEquals("Jan 1, 2020 9:00am GMT+0100", reqGatewayVO.getModifiedBy());
        assertEquals("Login ID", reqGatewayVO.getLoginID());
        assertEquals(1L, reqGatewayVO.getLastModifiedTime());
        assertEquals("Gateway Code", reqGatewayVO.getGatewayCode());
        assertEquals("Encryption Level", reqGatewayVO.getEncryptionLevel());
        assertEquals("Encryption Key", reqGatewayVO.getEncryptionKey());
        assertEquals("iloveyou", reqGatewayVO.getDecryptedPassword());
        assertEquals("Jan 1, 2020 8:00am GMT+0100", reqGatewayVO.getCreatedBy());
        assertEquals("text/plain", reqGatewayVO.getContentType());
        assertEquals("iloveyou", reqGatewayVO.getConfirmPassword());
    }

    /**
     * Method under test: {@link ReqGatewayVO#setServicePort(String)}
     */
    @Test
    public void testSetServicePort() {
        ReqGatewayVO reqGatewayVO = new ReqGatewayVO();
        reqGatewayVO.setServicePort("Service Port");
        assertEquals("Service Port", reqGatewayVO.getServicePort());
    }

    /**
     * Method under test: {@link ReqGatewayVO#setServicePort(String)}
     */
    @Test
    public void testSetServicePort2() {
        ReqGatewayVO reqGatewayVO = new ReqGatewayVO();
        reqGatewayVO.setAuthType("Auth Type");
        reqGatewayVO.setConfirmPassword("iloveyou");
        reqGatewayVO.setContentType("text/plain");
        reqGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        Date createdOn = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        reqGatewayVO.setCreatedOn(createdOn);
        reqGatewayVO.setDecryptedPassword("iloveyou");
        reqGatewayVO.setEncryptionKey("Encryption Key");
        reqGatewayVO.setEncryptionLevel("Encryption Level");
        reqGatewayVO.setGatewayCode("Gateway Code");
        reqGatewayVO.setLastModifiedTime(1L);
        reqGatewayVO.setLoginID("Login ID");
        reqGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        Date modifiedOn = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        reqGatewayVO.setModifiedOn(modifiedOn);
        reqGatewayVO.setOldPassword("iloveyou");
        reqGatewayVO.setPassword("iloveyou");
        reqGatewayVO.setPort("Port");
        reqGatewayVO.setServicePort("Service Port");
        reqGatewayVO.setStatus("Status");
        reqGatewayVO.setUnderProcessCheckReqd("Under Process Check Reqd");
        reqGatewayVO.setUpdatePassword("iloveyou");
        reqGatewayVO.setServicePort(null);
        assertEquals("Auth Type", reqGatewayVO.getAuthType());
        assertEquals("iloveyou", reqGatewayVO.getUpdatePassword());
        assertEquals("Under Process Check Reqd", reqGatewayVO.getUnderProcessCheckReqd());
        assertEquals("Status", reqGatewayVO.getStatus());
        assertEquals("Service Port", reqGatewayVO.getServicePort());
        assertEquals("Port", reqGatewayVO.getPort());
        assertEquals("iloveyou", reqGatewayVO.getPassword());
        assertEquals("iloveyou", reqGatewayVO.getOldPassword());
        assertSame(modifiedOn, reqGatewayVO.getModifiedOn());
        assertEquals("Jan 1, 2020 9:00am GMT+0100", reqGatewayVO.getModifiedBy());
        assertEquals("Login ID", reqGatewayVO.getLoginID());
        assertEquals(1L, reqGatewayVO.getLastModifiedTime());
        assertEquals("Gateway Code", reqGatewayVO.getGatewayCode());
        assertEquals("Encryption Level", reqGatewayVO.getEncryptionLevel());
        assertEquals("Encryption Key", reqGatewayVO.getEncryptionKey());
        assertEquals("iloveyou", reqGatewayVO.getDecryptedPassword());
        assertSame(createdOn, reqGatewayVO.getCreatedOn());
        assertEquals("Jan 1, 2020 8:00am GMT+0100", reqGatewayVO.getCreatedBy());
        assertEquals("text/plain", reqGatewayVO.getContentType());
        assertEquals("iloveyou", reqGatewayVO.getConfirmPassword());
    }

    /**
     * Method under test: {@link ReqGatewayVO#setServicePort(String)}
     */
    @Test
    public void testSetServicePort3() {
        ReqGatewayVO reqGatewayVO = new ReqGatewayVO();
        reqGatewayVO.setAuthType("Auth Type");
        reqGatewayVO.setConfirmPassword("iloveyou");
        reqGatewayVO.setContentType("text/plain");
        reqGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        reqGatewayVO.setCreatedOn(mock(java.sql.Date.class));
        reqGatewayVO.setDecryptedPassword("iloveyou");
        reqGatewayVO.setEncryptionKey("Encryption Key");
        reqGatewayVO.setEncryptionLevel("Encryption Level");
        reqGatewayVO.setGatewayCode("Gateway Code");
        reqGatewayVO.setLastModifiedTime(1L);
        reqGatewayVO.setLoginID("Login ID");
        reqGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        java.util.Date modifiedOn = java.util.Date
                .from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        reqGatewayVO.setModifiedOn(modifiedOn);
        reqGatewayVO.setOldPassword("iloveyou");
        reqGatewayVO.setPassword("iloveyou");
        reqGatewayVO.setPort("Port");
        reqGatewayVO.setServicePort("Service Port");
        reqGatewayVO.setStatus("Status");
        reqGatewayVO.setUnderProcessCheckReqd("Under Process Check Reqd");
        reqGatewayVO.setUpdatePassword("iloveyou");
        reqGatewayVO.setServicePort(null);
        assertEquals("Auth Type", reqGatewayVO.getAuthType());
        assertEquals("iloveyou", reqGatewayVO.getUpdatePassword());
        assertEquals("Under Process Check Reqd", reqGatewayVO.getUnderProcessCheckReqd());
        assertEquals("Status", reqGatewayVO.getStatus());
        assertEquals("Service Port", reqGatewayVO.getServicePort());
        assertEquals("Port", reqGatewayVO.getPort());
        assertEquals("iloveyou", reqGatewayVO.getPassword());
        assertEquals("iloveyou", reqGatewayVO.getOldPassword());
        assertSame(modifiedOn, reqGatewayVO.getModifiedOn());
        assertEquals("Jan 1, 2020 9:00am GMT+0100", reqGatewayVO.getModifiedBy());
        assertEquals("Login ID", reqGatewayVO.getLoginID());
        assertEquals(1L, reqGatewayVO.getLastModifiedTime());
        assertEquals("Gateway Code", reqGatewayVO.getGatewayCode());
        assertEquals("Encryption Level", reqGatewayVO.getEncryptionLevel());
        assertEquals("Encryption Key", reqGatewayVO.getEncryptionKey());
        assertEquals("iloveyou", reqGatewayVO.getDecryptedPassword());
        assertEquals("Jan 1, 2020 8:00am GMT+0100", reqGatewayVO.getCreatedBy());
        assertEquals("text/plain", reqGatewayVO.getContentType());
        assertEquals("iloveyou", reqGatewayVO.getConfirmPassword());
    }
}

