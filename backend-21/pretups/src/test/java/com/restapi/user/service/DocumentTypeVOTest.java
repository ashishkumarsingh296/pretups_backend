package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DocumentTypeVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DocumentTypeVO}
     *   <li>{@link DocumentTypeVO#setDocumentCode(String)}
     *   <li>{@link DocumentTypeVO#setDocumentName(String)}
     *   <li>{@link DocumentTypeVO#toString()}
     *   <li>{@link DocumentTypeVO#getDocumentCode()}
     *   <li>{@link DocumentTypeVO#getDocumentName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DocumentTypeVO actualDocumentTypeVO = new DocumentTypeVO();
        actualDocumentTypeVO.setDocumentCode("Document Code");
        actualDocumentTypeVO.setDocumentName("Document Name");
        actualDocumentTypeVO.toString();
        assertEquals("Document Code", actualDocumentTypeVO.getDocumentCode());
        assertEquals("Document Name", actualDocumentTypeVO.getDocumentName());
    }
}

