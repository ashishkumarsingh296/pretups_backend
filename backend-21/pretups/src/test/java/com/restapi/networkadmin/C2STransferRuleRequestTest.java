package com.restapi.networkadmin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class C2STransferRuleRequestTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2STransferRuleRequest}
     *   <li>{@link C2STransferRuleRequest#setCardGroupSet(String)}
     *   <li>{@link C2STransferRuleRequest#setCategoryCode(String)}
     *   <li>{@link C2STransferRuleRequest#setGatewayCode(String)}
     *   <li>{@link C2STransferRuleRequest#setGradeCode(String)}
     *   <li>{@link C2STransferRuleRequest#setRecieverServiceClassId(String)}
     *   <li>{@link C2STransferRuleRequest#setRecieverSubscriberType(String)}
     *   <li>{@link C2STransferRuleRequest#setSenderSubscriberType(String)}
     *   <li>{@link C2STransferRuleRequest#setServiceType(String)}
     *   <li>{@link C2STransferRuleRequest#setSubServiceId(String)}
     *   <li>{@link C2STransferRuleRequest#getCardGroupSet()}
     *   <li>{@link C2STransferRuleRequest#getCategoryCode()}
     *   <li>{@link C2STransferRuleRequest#getGatewayCode()}
     *   <li>{@link C2STransferRuleRequest#getGradeCode()}
     *   <li>{@link C2STransferRuleRequest#getRecieverServiceClassId()}
     *   <li>{@link C2STransferRuleRequest#getRecieverSubscriberType()}
     *   <li>{@link C2STransferRuleRequest#getSenderSubscriberType()}
     *   <li>{@link C2STransferRuleRequest#getServiceType()}
     *   <li>{@link C2STransferRuleRequest#getSubServiceId()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2STransferRuleRequest actualC2sTransferRuleRequest = new C2STransferRuleRequest();
        actualC2sTransferRuleRequest.setCardGroupSet("Card Group Set");
        actualC2sTransferRuleRequest.setCategoryCode("Category Code");
        actualC2sTransferRuleRequest.setGatewayCode("Gateway Code");
        actualC2sTransferRuleRequest.setGradeCode("Grade Code");
        actualC2sTransferRuleRequest.setRecieverServiceClassId("42");
        actualC2sTransferRuleRequest.setRecieverSubscriberType("Reciever Subscriber Type");
        actualC2sTransferRuleRequest.setSenderSubscriberType("Sender Subscriber Type");
        actualC2sTransferRuleRequest.setServiceType("Service Type");
        actualC2sTransferRuleRequest.setSubServiceId("42");
        assertEquals("Card Group Set", actualC2sTransferRuleRequest.getCardGroupSet());
        assertEquals("Category Code", actualC2sTransferRuleRequest.getCategoryCode());
        assertEquals("Gateway Code", actualC2sTransferRuleRequest.getGatewayCode());
        assertEquals("Grade Code", actualC2sTransferRuleRequest.getGradeCode());
        assertEquals("42", actualC2sTransferRuleRequest.getRecieverServiceClassId());
        assertEquals("Reciever Subscriber Type", actualC2sTransferRuleRequest.getRecieverSubscriberType());
        assertEquals("Sender Subscriber Type", actualC2sTransferRuleRequest.getSenderSubscriberType());
        assertEquals("Service Type", actualC2sTransferRuleRequest.getServiceType());
        assertEquals("42", actualC2sTransferRuleRequest.getSubServiceId());
    }
}

