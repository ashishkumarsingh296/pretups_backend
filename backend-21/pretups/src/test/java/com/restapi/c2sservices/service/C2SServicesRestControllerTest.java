package com.restapi.c2sservices.service;

import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.junit.Test;

public class C2SServicesRestControllerTest {
    /**
     * Method under test: {@link C2SServicesRestController#processChannelUserRequest(JsonNode)}
     */
    @Test
    public void testProcessChannelUserRequest() {
        C2SServicesRestController c2sServicesRestController = new C2SServicesRestController();
        assertNull(c2sServicesRestController.processChannelUserRequest(MissingNode.getInstance()));
    }

    /**
     * Method under test: {@link C2SServicesRestController#processChannelUserRequest(JsonNode)}
     */
    @Test
    public void testProcessChannelUserRequest2() {
        C2SServicesRestController c2sServicesRestController = new C2SServicesRestController();
        assertNull(c2sServicesRestController
                .processChannelUserRequest(new ArrayNode(JsonNodeFactory.withExactBigDecimals(true))));
    }

    /**
     * Method under test: {@link C2SServicesRestController#processChannelUserRequestApproval(JsonNode)}
     */
    @Test
    public void testProcessChannelUserRequestApproval() {
        C2SServicesRestController c2sServicesRestController = new C2SServicesRestController();
        assertNull(c2sServicesRestController.processChannelUserRequestApproval(MissingNode.getInstance()));
    }
}

