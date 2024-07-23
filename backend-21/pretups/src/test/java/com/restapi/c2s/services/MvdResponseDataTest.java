package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MvdResponseDataTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link MvdResponseData}
     *   <li>{@link MvdResponseData#setCodeName(String)}
     *   <li>{@link MvdResponseData#setLabel(String)}
     *   <li>{@link MvdResponseData#setLabelWithValue(String)}
     *   <li>{@link MvdResponseData#setValue(String)}
     *   <li>{@link MvdResponseData#getCodeName()}
     *   <li>{@link MvdResponseData#getLabel()}
     *   <li>{@link MvdResponseData#getLabelWithValue()}
     *   <li>{@link MvdResponseData#getValue()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        MvdResponseData actualMvdResponseData = new MvdResponseData();
        actualMvdResponseData.setCodeName("Code Name");
        actualMvdResponseData.setLabel("Label");
        actualMvdResponseData.setLabelWithValue("42");
        actualMvdResponseData.setValue("42");
        assertEquals("Code Name", actualMvdResponseData.getCodeName());
        assertEquals("Label", actualMvdResponseData.getLabel());
        assertEquals("42", actualMvdResponseData.getLabelWithValue());
        assertEquals("42", actualMvdResponseData.getValue());
    }
}

