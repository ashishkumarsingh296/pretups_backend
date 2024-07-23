package com.restapi.cardgroup.service;

import com.btsl.common.PretupsResponse;
import com.btsl.pretups.channel.profile.businesslogic.DefaultCardGroupVO;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.validator.ValidatorException;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

public class DefaultCardGroupTest {
    /**
     * Method under test: {@link DefaultCardGroup#defaultCardGroup(DefaultCardGroupVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testDefaultCardGroup() throws ValidatorException, SAXException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.cardgroup.service.DefaultCardGroup.defaultCardGroup(DefaultCardGroup.java:73)

        // Arrange
        // TODO: Populate arranged inputs
        DefaultCardGroup defaultCardGroup = null;
        DefaultCardGroupVO requestVO = null;

        // Act
        PretupsResponse<JsonNode> actualDefaultCardGroupResult = defaultCardGroup.defaultCardGroup(requestVO);

        // Assert
        // TODO: Add assertions on result
    }
}

