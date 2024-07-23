package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GeoDomainAddRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GeoDomainAddRequestVO}
     *   <li>{@link GeoDomainAddRequestVO#setDescription(String)}
     *   <li>{@link GeoDomainAddRequestVO#setGrphDomainCode(String)}
     *   <li>{@link GeoDomainAddRequestVO#setGrphDomainName(String)}
     *   <li>{@link GeoDomainAddRequestVO#setGrphDomainShortName(String)}
     *   <li>{@link GeoDomainAddRequestVO#setGrphDomainType(String)}
     *   <li>{@link GeoDomainAddRequestVO#setIsDefault(String)}
     *   <li>{@link GeoDomainAddRequestVO#setParentDomainCode(String)}
     *   <li>{@link GeoDomainAddRequestVO#setStatus(String)}
     *   <li>{@link GeoDomainAddRequestVO#getDescription()}
     *   <li>{@link GeoDomainAddRequestVO#getGrphDomainCode()}
     *   <li>{@link GeoDomainAddRequestVO#getGrphDomainName()}
     *   <li>{@link GeoDomainAddRequestVO#getGrphDomainShortName()}
     *   <li>{@link GeoDomainAddRequestVO#getGrphDomainType()}
     *   <li>{@link GeoDomainAddRequestVO#getIsDefault()}
     *   <li>{@link GeoDomainAddRequestVO#getParentDomainCode()}
     *   <li>{@link GeoDomainAddRequestVO#getStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GeoDomainAddRequestVO actualGeoDomainAddRequestVO = new GeoDomainAddRequestVO();
        actualGeoDomainAddRequestVO.setDescription("The characteristics of someone or something");
        actualGeoDomainAddRequestVO.setGrphDomainCode("Grph Domain Code");
        actualGeoDomainAddRequestVO.setGrphDomainName("Grph Domain Name");
        actualGeoDomainAddRequestVO.setGrphDomainShortName("Grph Domain Short Name");
        actualGeoDomainAddRequestVO.setGrphDomainType("Grph Domain Type");
        actualGeoDomainAddRequestVO.setIsDefault("Is Default");
        actualGeoDomainAddRequestVO.setParentDomainCode("Parent Domain Code");
        actualGeoDomainAddRequestVO.setStatus("Status");
        assertEquals("The characteristics of someone or something", actualGeoDomainAddRequestVO.getDescription());
        assertEquals("Grph Domain Code", actualGeoDomainAddRequestVO.getGrphDomainCode());
        assertEquals("Grph Domain Name", actualGeoDomainAddRequestVO.getGrphDomainName());
        assertEquals("Grph Domain Short Name", actualGeoDomainAddRequestVO.getGrphDomainShortName());
        assertEquals("Grph Domain Type", actualGeoDomainAddRequestVO.getGrphDomainType());
        assertEquals("Is Default", actualGeoDomainAddRequestVO.getIsDefault());
        assertEquals("Parent Domain Code", actualGeoDomainAddRequestVO.getParentDomainCode());
        assertEquals("Status", actualGeoDomainAddRequestVO.getStatus());
    }
}

