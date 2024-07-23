package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ReceiverSectionInputsTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ReceiverSectionInputs}
     *   <li>{@link ReceiverSectionInputs#setApplicableFrom(String)}
     *   <li>{@link ReceiverSectionInputs#setApplicableTo(String)}
     *   <li>{@link ReceiverSectionInputs#setCardGroupSet(String)}
     *   <li>{@link ReceiverSectionInputs#setRowIndex(String)}
     *   <li>{@link ReceiverSectionInputs#setServiceCardGroupID(String)}
     *   <li>{@link ReceiverSectionInputs#setServiceClassID(String)}
     *   <li>{@link ReceiverSectionInputs#setServiceType(String)}
     *   <li>{@link ReceiverSectionInputs#setStatus(String)}
     *   <li>{@link ReceiverSectionInputs#setSubscriberStatusValue(String)}
     *   <li>{@link ReceiverSectionInputs#setSubservice(String)}
     *   <li>{@link ReceiverSectionInputs#setTimeSlabs(String)}
     *   <li>{@link ReceiverSectionInputs#setType(String)}
     *   <li>{@link ReceiverSectionInputs#getApplicableFrom()}
     *   <li>{@link ReceiverSectionInputs#getApplicableTo()}
     *   <li>{@link ReceiverSectionInputs#getCardGroupSet()}
     *   <li>{@link ReceiverSectionInputs#getRowIndex()}
     *   <li>{@link ReceiverSectionInputs#getServiceCardGroupID()}
     *   <li>{@link ReceiverSectionInputs#getServiceClassID()}
     *   <li>{@link ReceiverSectionInputs#getServiceType()}
     *   <li>{@link ReceiverSectionInputs#getStatus()}
     *   <li>{@link ReceiverSectionInputs#getSubscriberStatusValue()}
     *   <li>{@link ReceiverSectionInputs#getSubservice()}
     *   <li>{@link ReceiverSectionInputs#getTimeSlabs()}
     *   <li>{@link ReceiverSectionInputs#getType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ReceiverSectionInputs actualReceiverSectionInputs = new ReceiverSectionInputs();
        actualReceiverSectionInputs.setApplicableFrom("jane.doe@example.org");
        actualReceiverSectionInputs.setApplicableTo("alice.liddell@example.org");
        actualReceiverSectionInputs.setCardGroupSet("Card Group Set");
        actualReceiverSectionInputs.setRowIndex("Row Index");
        actualReceiverSectionInputs.setServiceCardGroupID("Service Card Group ID");
        actualReceiverSectionInputs.setServiceClassID("Service Class ID");
        actualReceiverSectionInputs.setServiceType("Service Type");
        actualReceiverSectionInputs.setStatus("Status");
        actualReceiverSectionInputs.setSubscriberStatusValue("42");
        actualReceiverSectionInputs.setSubservice("Subservice");
        actualReceiverSectionInputs.setTimeSlabs("Time Slabs");
        actualReceiverSectionInputs.setType("Type");
        assertEquals("jane.doe@example.org", actualReceiverSectionInputs.getApplicableFrom());
        assertEquals("alice.liddell@example.org", actualReceiverSectionInputs.getApplicableTo());
        assertEquals("Card Group Set", actualReceiverSectionInputs.getCardGroupSet());
        assertEquals("Row Index", actualReceiverSectionInputs.getRowIndex());
        assertEquals("Service Card Group ID", actualReceiverSectionInputs.getServiceCardGroupID());
        assertEquals("Service Class ID", actualReceiverSectionInputs.getServiceClassID());
        assertEquals("Service Type", actualReceiverSectionInputs.getServiceType());
        assertEquals("Status", actualReceiverSectionInputs.getStatus());
        assertEquals("42", actualReceiverSectionInputs.getSubscriberStatusValue());
        assertEquals("Subservice", actualReceiverSectionInputs.getSubservice());
        assertEquals("Time Slabs", actualReceiverSectionInputs.getTimeSlabs());
        assertEquals("Type", actualReceiverSectionInputs.getType());
    }
}

