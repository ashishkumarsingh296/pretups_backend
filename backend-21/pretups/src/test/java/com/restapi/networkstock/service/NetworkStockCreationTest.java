package com.restapi.networkstock.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import com.btsl.common.PretupsResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

public class NetworkStockCreationTest {
    /**
     * Method under test: {@link NetworkStockCreation#addNetworkStock(String)}
     */
    @Test
    public void testAddNetworkStock() {
        PretupsResponse<JsonNode> actualAddNetworkStockResult = (new NetworkStockCreation())
                .addNetworkStock("Request Data");
        assertEquals("networkstock.createstock.msg.unsuccess", actualAddNetworkStockResult.getSuccessMsg());
        assertEquals(400, actualAddNetworkStockResult.getStatusCode().intValue());
        assertFalse(actualAddNetworkStockResult.getStatus());
        assertEquals("networkstock.createstock.msg.unsuccess", actualAddNetworkStockResult.getMessageKey());
        assertNull(actualAddNetworkStockResult.getFieldError());
    }

    /**
     * Method under test: {@link NetworkStockCreation#addNetworkStock(String)}
     */
    @Test
    public void testAddNetworkStock2() {
        NetworkStockCreation networkStockCreation = new NetworkStockCreation();
        networkStockCreation.addNetworkStock("addNetworkStock");
        PretupsResponse<JsonNode> actualAddNetworkStockResult = networkStockCreation.addNetworkStock("Request Data");
        assertEquals("networkstock.createstock.msg.unsuccess", actualAddNetworkStockResult.getSuccessMsg());
        assertEquals(400, actualAddNetworkStockResult.getStatusCode().intValue());
        assertFalse(actualAddNetworkStockResult.getStatus());
        assertEquals("networkstock.createstock.msg.unsuccess", actualAddNetworkStockResult.getMessageKey());
        assertNull(actualAddNetworkStockResult.getFieldError());
    }

    /**
     * Method under test: {@link NetworkStockCreation#addNetworkStock(String)}
     */
    @Test
    public void testAddNetworkStock3() {
        PretupsResponse<JsonNode> actualAddNetworkStockResult = (new NetworkStockCreation()).addNetworkStock(null);
        assertEquals("networkstock.createstock.msg.unsuccess", actualAddNetworkStockResult.getSuccessMsg());
        assertEquals(400, actualAddNetworkStockResult.getStatusCode().intValue());
        assertFalse(actualAddNetworkStockResult.getStatus());
        assertEquals("networkstock.createstock.msg.unsuccess", actualAddNetworkStockResult.getMessageKey());
        assertNull(actualAddNetworkStockResult.getFieldError());
    }

    /**
     * Method under test: {@link NetworkStockCreation#addNetworkStock(String)}
     */
    @Test
    public void testAddNetworkStock4() {
        PretupsResponse<JsonNode> actualAddNetworkStockResult = (new NetworkStockCreation()).addNetworkStock("");
        assertEquals("networkstock.createstock.msg.unsuccess", actualAddNetworkStockResult.getSuccessMsg());
        assertEquals(400, actualAddNetworkStockResult.getStatusCode().intValue());
        assertFalse(actualAddNetworkStockResult.getStatus());
        assertEquals("networkstock.createstock.msg.unsuccess", actualAddNetworkStockResult.getMessageKey());
        assertNull(actualAddNetworkStockResult.getFieldError());
    }

    /**
     * Method under test: {@link NetworkStockCreation#addNetworkStock(String)}
     */
    @Test
    public void testAddNetworkStock5() {
        PretupsResponse<JsonNode> actualAddNetworkStockResult = (new NetworkStockCreation()).addNetworkStock("42");
        assertEquals("networkstock.createstock.msg.unsuccess", actualAddNetworkStockResult.getSuccessMsg());
        assertEquals(400, actualAddNetworkStockResult.getStatusCode().intValue());
        assertFalse(actualAddNetworkStockResult.getStatus());
        assertEquals("networkstock.createstock.msg.unsuccess", actualAddNetworkStockResult.getMessageKey());
        assertNull(actualAddNetworkStockResult.getFieldError());
    }
}

