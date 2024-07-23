package com.restapi.networkadmin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class C2STransferRuleRequest1Test {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2STransferRuleRequest1}
     *   <li>{@link C2STransferRuleRequest1#setCardGroupSet(String)}
     *   <li>{@link C2STransferRuleRequest1#setCategoryCode(String)}
     *   <li>{@link C2STransferRuleRequest1#setGatewayCode(String)}
     *   <li>{@link C2STransferRuleRequest1#setGradeCode(String)}
     *   <li>{@link C2STransferRuleRequest1#setRecieverServiceClassId(String)}
     *   <li>{@link C2STransferRuleRequest1#setRecieverSubscriberType(String)}
     *   <li>{@link C2STransferRuleRequest1#setSenderSubscriberType(String)}
     *   <li>{@link C2STransferRuleRequest1#setServiceType(String)}
     *   <li>{@link C2STransferRuleRequest1#setStatus(String)}
     *   <li>{@link C2STransferRuleRequest1#setSubServiceId(String)}
     *   <li>{@link C2STransferRuleRequest1#getCardGroupSet()}
     *   <li>{@link C2STransferRuleRequest1#getCategoryCode()}
     *   <li>{@link C2STransferRuleRequest1#getGatewayCode()}
     *   <li>{@link C2STransferRuleRequest1#getGradeCode()}
     *   <li>{@link C2STransferRuleRequest1#getRecieverServiceClassId()}
     *   <li>{@link C2STransferRuleRequest1#getRecieverSubscriberType()}
     *   <li>{@link C2STransferRuleRequest1#getSenderSubscriberType()}
     *   <li>{@link C2STransferRuleRequest1#getServiceType()}
     *   <li>{@link C2STransferRuleRequest1#getStatus()}
     *   <li>{@link C2STransferRuleRequest1#getSubServiceId()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2STransferRuleRequest1 actualC2sTransferRuleRequest1 = new C2STransferRuleRequest1();
        actualC2sTransferRuleRequest1.setCardGroupSet("Card Group Set");
        actualC2sTransferRuleRequest1.setCategoryCode("Category Code");
        actualC2sTransferRuleRequest1.setGatewayCode("Gateway Code");
        actualC2sTransferRuleRequest1.setGradeCode("Grade Code");
        actualC2sTransferRuleRequest1.setRecieverServiceClassId("42");
        actualC2sTransferRuleRequest1.setRecieverSubscriberType("Reciever Subscriber Type");
        actualC2sTransferRuleRequest1.setSenderSubscriberType("Sender Subscriber Type");
        actualC2sTransferRuleRequest1.setServiceType("Service Type");
        actualC2sTransferRuleRequest1.setStatus("Status");
        actualC2sTransferRuleRequest1.setSubServiceId("42");
        assertEquals("Card Group Set", actualC2sTransferRuleRequest1.getCardGroupSet());
        assertEquals("Category Code", actualC2sTransferRuleRequest1.getCategoryCode());
        assertEquals("Gateway Code", actualC2sTransferRuleRequest1.getGatewayCode());
        assertEquals("Grade Code", actualC2sTransferRuleRequest1.getGradeCode());
        assertEquals("42", actualC2sTransferRuleRequest1.getRecieverServiceClassId());
        assertEquals("Reciever Subscriber Type", actualC2sTransferRuleRequest1.getRecieverSubscriberType());
        assertEquals("Sender Subscriber Type", actualC2sTransferRuleRequest1.getSenderSubscriberType());
        assertEquals("Service Type", actualC2sTransferRuleRequest1.getServiceType());
        assertEquals("Status", actualC2sTransferRuleRequest1.getStatus());
        assertEquals("42", actualC2sTransferRuleRequest1.getSubServiceId());
    }
}

