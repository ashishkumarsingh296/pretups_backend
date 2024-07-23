package com.restapi.superadminVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.Test;

public class MessGatewayVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link MessGatewayVO}
     *   <li>{@link MessGatewayVO#setAccessFrom(String)}
     *   <li>{@link MessGatewayVO#setAltGatewayVO(ResGatewayVO)}
     *   <li>{@link MessGatewayVO#setBinaryMsgAllowed(String)}
     *   <li>{@link MessGatewayVO#setCategoryCode(String)}
     *   <li>{@link MessGatewayVO#setCreatedBy(String)}
     *   <li>{@link MessGatewayVO#setCreatedOn(Date)}
     *   <li>{@link MessGatewayVO#setFlowType(String)}
     *   <li>{@link MessGatewayVO#setGatewayCode(String)}
     *   <li>{@link MessGatewayVO#setGatewayName(String)}
     *   <li>{@link MessGatewayVO#setGatewaySubType(String)}
     *   <li>{@link MessGatewayVO#setGatewaySubTypeDes(String)}
     *   <li>{@link MessGatewayVO#setGatewaySubTypeName(String)}
     *   <li>{@link MessGatewayVO#setGatewayType(String)}
     *   <li>{@link MessGatewayVO#setGatewayTypeDes(String)}
     *   <li>{@link MessGatewayVO#setHandlerClass(String)}
     *   <li>{@link MessGatewayVO#setHost(String)}
     *   <li>{@link MessGatewayVO#setLastModifiedTime(long)}
     *   <li>{@link MessGatewayVO#setModifiedBy(String)}
     *   <li>{@link MessGatewayVO#setModifiedOn(Date)}
     *   <li>{@link MessGatewayVO#setNetworkCode(String)}
     *   <li>{@link MessGatewayVO#setPlainMsgAllowed(String)}
     *   <li>{@link MessGatewayVO#setProtocol(String)}
     *   <li>{@link MessGatewayVO#setReqGatewayVO(ReqGatewayVO)}
     *   <li>{@link MessGatewayVO#setReqpasswordtype(String)}
     *   <li>{@link MessGatewayVO#setResGatewayVO(ResGatewayVO)}
     *   <li>{@link MessGatewayVO#setResponseType(String)}
     *   <li>{@link MessGatewayVO#setStatus(String)}
     *   <li>{@link MessGatewayVO#setTimeoutValue(long)}
     *   <li>{@link MessGatewayVO#setUserAuthorizationReqd(boolean)}
     *   <li>{@link MessGatewayVO#getAccessFrom()}
     *   <li>{@link MessGatewayVO#getAltGatewayVO()}
     *   <li>{@link MessGatewayVO#getBinaryMsgAllowed()}
     *   <li>{@link MessGatewayVO#getCategoryCode()}
     *   <li>{@link MessGatewayVO#getCreatedBy()}
     *   <li>{@link MessGatewayVO#getCreatedOn()}
     *   <li>{@link MessGatewayVO#getFlowType()}
     *   <li>{@link MessGatewayVO#getGatewayCode()}
     *   <li>{@link MessGatewayVO#getGatewayName()}
     *   <li>{@link MessGatewayVO#getGatewaySubType()}
     *   <li>{@link MessGatewayVO#getGatewaySubTypeDes()}
     *   <li>{@link MessGatewayVO#getGatewaySubTypeName()}
     *   <li>{@link MessGatewayVO#getGatewayType()}
     *   <li>{@link MessGatewayVO#getGatewayTypeDes()}
     *   <li>{@link MessGatewayVO#getHandlerClass()}
     *   <li>{@link MessGatewayVO#getHost()}
     *   <li>{@link MessGatewayVO#getLastModifiedTime()}
     *   <li>{@link MessGatewayVO#getModifiedBy()}
     *   <li>{@link MessGatewayVO#getModifiedOn()}
     *   <li>{@link MessGatewayVO#getNetworkCode()}
     *   <li>{@link MessGatewayVO#getPlainMsgAllowed()}
     *   <li>{@link MessGatewayVO#getProtocol()}
     *   <li>{@link MessGatewayVO#getReqGatewayVO()}
     *   <li>{@link MessGatewayVO#getReqpasswordtype()}
     *   <li>{@link MessGatewayVO#getResGatewayVO()}
     *   <li>{@link MessGatewayVO#getResponseType()}
     *   <li>{@link MessGatewayVO#getStatus()}
     *   <li>{@link MessGatewayVO#getTimeoutValue()}
     *   <li>{@link MessGatewayVO#isUserAuthorizationReqd()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        MessGatewayVO actualMessGatewayVO = new MessGatewayVO();
        actualMessGatewayVO.setAccessFrom("jane.doe@example.org");
        ResGatewayVO alternateGatewayVO = new ResGatewayVO();
        alternateGatewayVO.setConfirmPassword("iloveyou");
        alternateGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        alternateGatewayVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        alternateGatewayVO.setDecryptedPassword("iloveyou");
        alternateGatewayVO.setDestNo("Dest No");
        alternateGatewayVO.setGatewayCode("Gateway Code");
        alternateGatewayVO.setLastModifiedTime(1L);
        alternateGatewayVO.setLoginID("Login ID");
        alternateGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        alternateGatewayVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        alternateGatewayVO.setOldPassword("iloveyou");
        alternateGatewayVO.setPassword("iloveyou");
        alternateGatewayVO.setPath("Path");
        alternateGatewayVO.setPort("Port");
        alternateGatewayVO.setServicePort("Service Port");
        alternateGatewayVO.setStatus("Status");
        alternateGatewayVO.setTimeOut(1);
        alternateGatewayVO.setUpdatePassword("iloveyou");
        actualMessGatewayVO.setAltGatewayVO(alternateGatewayVO);
        actualMessGatewayVO.setBinaryMsgAllowed("Binary Msg Allowed");
        actualMessGatewayVO.setCategoryCode("Category Code");
        actualMessGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        Date createdOn = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        actualMessGatewayVO.setCreatedOn(createdOn);
        actualMessGatewayVO.setFlowType("Flow Type");
        actualMessGatewayVO.setGatewayCode("Gateway Code");
        actualMessGatewayVO.setGatewayName("Gateway Name");
        actualMessGatewayVO.setGatewaySubType("Gateway Sub Type");
        actualMessGatewayVO.setGatewaySubTypeDes("Gateway Sub Type Des");
        actualMessGatewayVO.setGatewaySubTypeName("Gateway Sub Type Name");
        actualMessGatewayVO.setGatewayType("Gateway Type");
        actualMessGatewayVO.setGatewayTypeDes("Gateway Type Des");
        actualMessGatewayVO.setHandlerClass("Handler Class");
        actualMessGatewayVO.setHost("localhost");
        actualMessGatewayVO.setLastModifiedTime(1L);
        actualMessGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        Date modifiedOn = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        actualMessGatewayVO.setModifiedOn(modifiedOn);
        actualMessGatewayVO.setNetworkCode("Network Code");
        actualMessGatewayVO.setPlainMsgAllowed("Plain Msg Allowed");
        actualMessGatewayVO.setProtocol("Protocol");
        ReqGatewayVO reqGatewayVO = new ReqGatewayVO();
        reqGatewayVO.setAuthType("Auth Type");
        reqGatewayVO.setConfirmPassword("iloveyou");
        reqGatewayVO.setContentType("text/plain");
        reqGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        reqGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reqGatewayVO.setDecryptedPassword("iloveyou");
        reqGatewayVO.setEncryptionKey("Encryption Key");
        reqGatewayVO.setEncryptionLevel("Encryption Level");
        reqGatewayVO.setGatewayCode("Gateway Code");
        reqGatewayVO.setLastModifiedTime(1L);
        reqGatewayVO.setLoginID("Login ID");
        reqGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        reqGatewayVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        reqGatewayVO.setOldPassword("iloveyou");
        reqGatewayVO.setPassword("iloveyou");
        reqGatewayVO.setPort("Port");
        reqGatewayVO.setServicePort("Service Port");
        reqGatewayVO.setStatus("Status");
        reqGatewayVO.setUnderProcessCheckReqd("Under Process Check Reqd");
        reqGatewayVO.setUpdatePassword("iloveyou");
        actualMessGatewayVO.setReqGatewayVO(reqGatewayVO);
        actualMessGatewayVO.setReqpasswordtype(" reqpasswordtype");
        ResGatewayVO resGatewayVO = new ResGatewayVO();
        resGatewayVO.setConfirmPassword("iloveyou");
        resGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        resGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        resGatewayVO.setDecryptedPassword("iloveyou");
        resGatewayVO.setDestNo("Dest No");
        resGatewayVO.setGatewayCode("Gateway Code");
        resGatewayVO.setLastModifiedTime(1L);
        resGatewayVO.setLoginID("Login ID");
        resGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        resGatewayVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        resGatewayVO.setOldPassword("iloveyou");
        resGatewayVO.setPassword("iloveyou");
        resGatewayVO.setPath("Path");
        resGatewayVO.setPort("Port");
        resGatewayVO.setServicePort("Service Port");
        resGatewayVO.setStatus("Status");
        resGatewayVO.setTimeOut(1);
        resGatewayVO.setUpdatePassword("iloveyou");
        actualMessGatewayVO.setResGatewayVO(resGatewayVO);
        actualMessGatewayVO.setResponseType("Response Type");
        actualMessGatewayVO.setStatus("Status");
        actualMessGatewayVO.setTimeoutValue(10L);
        actualMessGatewayVO.setUserAuthorizationReqd(true);
        assertEquals("jane.doe@example.org", actualMessGatewayVO.getAccessFrom());
        assertSame(alternateGatewayVO, actualMessGatewayVO.getAltGatewayVO());
        assertEquals("Binary Msg Allowed", actualMessGatewayVO.getBinaryMsgAllowed());
        assertEquals("Category Code", actualMessGatewayVO.getCategoryCode());
        assertEquals("Jan 1, 2020 8:00am GMT+0100", actualMessGatewayVO.getCreatedBy());
        assertSame(createdOn, actualMessGatewayVO.getCreatedOn());
        assertEquals("Flow Type", actualMessGatewayVO.getFlowType());
        assertEquals("Gateway Code", actualMessGatewayVO.getGatewayCode());
        assertEquals("Gateway Name", actualMessGatewayVO.getGatewayName());
        assertEquals("Gateway Sub Type", actualMessGatewayVO.getGatewaySubType());
        assertEquals("Gateway Sub Type Des", actualMessGatewayVO.getGatewaySubTypeDes());
        assertEquals("Gateway Sub Type Name", actualMessGatewayVO.getGatewaySubTypeName());
        assertEquals("Gateway Type", actualMessGatewayVO.getGatewayType());
        assertEquals("Gateway Type Des", actualMessGatewayVO.getGatewayTypeDes());
        assertEquals("Handler Class", actualMessGatewayVO.getHandlerClass());
        assertEquals("localhost", actualMessGatewayVO.getHost());
        assertEquals(1L, actualMessGatewayVO.getLastModifiedTime());
        assertEquals("Jan 1, 2020 9:00am GMT+0100", actualMessGatewayVO.getModifiedBy());
        assertSame(modifiedOn, actualMessGatewayVO.getModifiedOn());
        assertEquals("Network Code", actualMessGatewayVO.getNetworkCode());
        assertEquals("Plain Msg Allowed", actualMessGatewayVO.getPlainMsgAllowed());
        assertEquals("Protocol", actualMessGatewayVO.getProtocol());
        assertSame(reqGatewayVO, actualMessGatewayVO.getReqGatewayVO());
        assertEquals(" reqpasswordtype", actualMessGatewayVO.getReqpasswordtype());
        assertSame(resGatewayVO, actualMessGatewayVO.getResGatewayVO());
        assertEquals("Response Type", actualMessGatewayVO.getResponseType());
        assertEquals("Status", actualMessGatewayVO.getStatus());
        assertEquals(10L, actualMessGatewayVO.getTimeoutValue());
        assertTrue(actualMessGatewayVO.isUserAuthorizationReqd());
    }

    /**
     * Method under test: default or parameterless constructor of {@link MessGatewayVO}
     */
    @Test
    public void testConstructor2() {
        MessGatewayVO actualMessGatewayVO = new MessGatewayVO();
        assertTrue(actualMessGatewayVO.isUserAuthorizationReqd());
        assertEquals("Y", actualMessGatewayVO.getReqpasswordtype());
        assertNull(actualMessGatewayVO.getGatewayCode());
        assertEquals(0L, actualMessGatewayVO.getLastModifiedTime());
        ResGatewayVO resGatewayVO = actualMessGatewayVO.getResGatewayVO();
        assertNull(resGatewayVO.getPort());
        assertNull(resGatewayVO.getServicePort());
        assertEquals(0, resGatewayVO.getTimeOut());
        assertEquals(0L, resGatewayVO.getLastModifiedTime());
    }
}

