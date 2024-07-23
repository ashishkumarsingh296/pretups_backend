package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VoucherConResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link VoucherConResponseVO#VoucherConResponseVO(String, int, String, String, String)}
     *   <li>{@link VoucherConResponseVO#setExternalRefId(String)}
     *   <li>{@link VoucherConResponseVO#setMessage(String)}
     *   <li>{@link VoucherConResponseVO#setMessageCode(String)}
     *   <li>{@link VoucherConResponseVO#setStatus(int)}
     *   <li>{@link VoucherConResponseVO#setTxnid(String)}
     *   <li>{@link VoucherConResponseVO#toString()}
     *   <li>{@link VoucherConResponseVO#getExternalRefId()}
     *   <li>{@link VoucherConResponseVO#getMessage()}
     *   <li>{@link VoucherConResponseVO#getMessageCode()}
     *   <li>{@link VoucherConResponseVO#getStatus()}
     *   <li>{@link VoucherConResponseVO#getTxnid()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherConResponseVO actualVoucherConResponseVO = new VoucherConResponseVO("42", 1, "Message Code",
                "Not all who wander are lost", "Txnid");
        actualVoucherConResponseVO.setExternalRefId("42");
        actualVoucherConResponseVO.setMessage("Not all who wander are lost");
        actualVoucherConResponseVO.setMessageCode("Message Code");
        actualVoucherConResponseVO.setStatus(1);
        actualVoucherConResponseVO.setTxnid("Txnid");
        String actualToStringResult = actualVoucherConResponseVO.toString();
        assertEquals("42", actualVoucherConResponseVO.getExternalRefId());
        assertEquals("Not all who wander are lost", actualVoucherConResponseVO.getMessage());
        assertEquals("Message Code", actualVoucherConResponseVO.getMessageCode());
        assertEquals(1, actualVoucherConResponseVO.getStatus());
        assertEquals("Txnid", actualVoucherConResponseVO.getTxnid());
        assertEquals(
                "VoucherConResponseVO [externalRefId=42, status=1, messageCode=Message Code, message=Not all who wander"
                        + " are lost, txnid=Txnid]",
                actualToStringResult);
    }
}

