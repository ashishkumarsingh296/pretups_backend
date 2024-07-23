package com.restapi.superadminVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.Test;

public class AddMessGatewayVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link AddMessGatewayVO}
     *   <li>{@link AddMessGatewayVO#setGatewayCode(String)}
     *   <li>{@link AddMessGatewayVO#setHandlerClassDescription(String)}
     *   <li>{@link AddMessGatewayVO#setMessGatewayVO(MessGatewayVO)}
     *   <li>{@link AddMessGatewayVO#setPushDetailCheckbox(String)}
     *   <li>{@link AddMessGatewayVO#setPushDetailDisable(boolean)}
     *   <li>{@link AddMessGatewayVO#setReqDetailCheckbox(String)}
     *   <li>{@link AddMessGatewayVO#setReqDetailDisable(boolean)}
     *   <li>{@link AddMessGatewayVO#setTimeOut(String)}
     *   <li>{@link AddMessGatewayVO#setUpdatePassword(String)}
     *   <li>{@link AddMessGatewayVO#getGatewayCode()}
     *   <li>{@link AddMessGatewayVO#getHandlerClassDescription()}
     *   <li>{@link AddMessGatewayVO#getMessGatewayVO()}
     *   <li>{@link AddMessGatewayVO#getPushDetailCheckbox()}
     *   <li>{@link AddMessGatewayVO#getPushDetailDisable()}
     *   <li>{@link AddMessGatewayVO#getReqDetailCheckbox()}
     *   <li>{@link AddMessGatewayVO#getReqDetailDisable()}
     *   <li>{@link AddMessGatewayVO#getTimeOut()}
     *   <li>{@link AddMessGatewayVO#getUpdatePassword()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        AddMessGatewayVO actualAddMessGatewayVO = new AddMessGatewayVO();
        actualAddMessGatewayVO.setGatewayCode("Gateway Code");
        actualAddMessGatewayVO.setHandlerClassDescription("Handler Class Description");
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
        MessGatewayVO messGatewayVO = new MessGatewayVO();
        messGatewayVO.setAccessFrom("jane.doe@example.org");
        messGatewayVO.setAltGatewayVO(alternateGatewayVO);
        messGatewayVO.setBinaryMsgAllowed("Binary Msg Allowed");
        messGatewayVO.setCategoryCode("Category Code");
        messGatewayVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        messGatewayVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        messGatewayVO.setFlowType("Flow Type");
        messGatewayVO.setGatewayCode("Gateway Code");
        messGatewayVO.setGatewayName("Gateway Name");
        messGatewayVO.setGatewaySubType("Gateway Sub Type");
        messGatewayVO.setGatewaySubTypeDes("Gateway Sub Type Des");
        messGatewayVO.setGatewaySubTypeName("Gateway Sub Type Name");
        messGatewayVO.setGatewayType("Gateway Type");
        messGatewayVO.setGatewayTypeDes("Gateway Type Des");
        messGatewayVO.setHandlerClass("Handler Class");
        messGatewayVO.setHost("localhost");
        messGatewayVO.setLastModifiedTime(1L);
        messGatewayVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        messGatewayVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        messGatewayVO.setNetworkCode("Network Code");
        messGatewayVO.setPlainMsgAllowed("Plain Msg Allowed");
        messGatewayVO.setProtocol("Protocol");
        messGatewayVO.setReqGatewayVO(reqGatewayVO);
        messGatewayVO.setReqpasswordtype(" reqpasswordtype");
        messGatewayVO.setResGatewayVO(resGatewayVO);
        messGatewayVO.setResponseType("Response Type");
        messGatewayVO.setStatus("Status");
        messGatewayVO.setTimeoutValue(10L);
        messGatewayVO.setUserAuthorizationReqd(true);
        actualAddMessGatewayVO.setMessGatewayVO(messGatewayVO);
        actualAddMessGatewayVO.setPushDetailCheckbox("Push Detail Checkbox");
        actualAddMessGatewayVO.setPushDetailDisable(true);
        actualAddMessGatewayVO.setReqDetailCheckbox("Req Detail Checkbox");
        actualAddMessGatewayVO.setReqDetailDisable(true);
        actualAddMessGatewayVO.setTimeOut("Time Out");
        actualAddMessGatewayVO.setUpdatePassword("iloveyou");
        assertEquals("Gateway Code", actualAddMessGatewayVO.getGatewayCode());
        assertEquals("Handler Class Description", actualAddMessGatewayVO.getHandlerClassDescription());
        assertSame(messGatewayVO, actualAddMessGatewayVO.getMessGatewayVO());
        assertEquals("Push Detail Checkbox", actualAddMessGatewayVO.getPushDetailCheckbox());
        assertTrue(actualAddMessGatewayVO.getPushDetailDisable());
        assertEquals("Req Detail Checkbox", actualAddMessGatewayVO.getReqDetailCheckbox());
        assertTrue(actualAddMessGatewayVO.getReqDetailDisable());
        assertEquals("Time Out", actualAddMessGatewayVO.getTimeOut());
        assertEquals("iloveyou", actualAddMessGatewayVO.getUpdatePassword());
    }
}

