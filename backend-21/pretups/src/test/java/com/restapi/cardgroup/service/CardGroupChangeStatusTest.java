package com.restapi.cardgroup.service;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO;
import com.btsl.pretups.channel.profile.businesslogic.CardGroupStatusVO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.validator.ValidatorException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

@ContextConfiguration(classes = {CardGroupChangeStatus.class, PretupsResponse.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class CardGroupChangeStatusTest {
    @Autowired
    private CardGroupChangeStatus cardGroupChangeStatus;

    @Autowired
    private PretupsResponse<?> pretupsResponse;

    /**
     * Method under test: {@link CardGroupChangeStatus#loadCardGroupSet(CardGroupStatusVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testLoadCardGroupSet() throws BTSLBaseException, IOException, SQLException {
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
        //       at com.restapi.cardgroup.service.CardGroupChangeStatus.loadCardGroupSet(CardGroupChangeStatus.java:84)

        // Arrange
        // TODO: Populate arranged inputs
        CardGroupChangeStatus cardGroupChangeStatus = null;
        CardGroupStatusVO defaultCardGroupVO = null;

        // Act
        PretupsResponse<List<CardGroupSetVO>> actualLoadCardGroupSetResult = cardGroupChangeStatus
                .loadCardGroupSet(defaultCardGroupVO);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CardGroupChangeStatus#updateCardGroupStatus(String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testUpdateCardGroupStatus() throws BTSLBaseException, IOException, SQLException {
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
        //       at com.restapi.cardgroup.service.CardGroupChangeStatus.updateCardGroupStatus(CardGroupChangeStatus.java:161)

        // Arrange
        // TODO: Populate arranged inputs
        CardGroupChangeStatus cardGroupChangeStatus = null;
        String requestData = "";

        // Act
        PretupsResponse<List<CardGroupSetVO>> actualUpdateCardGroupStatusResult = cardGroupChangeStatus
                .updateCardGroupStatus(requestData);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CardGroupChangeStatus#validateRequestData(String, PretupsResponse, CardGroupStatusVO, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateRequestData() throws IOException, ValidatorException, SAXException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.cardgroup.service.CardGroupChangeStatus.validateRequestData(CardGroupChangeStatus.java:302)
        //   See https://diff.blue/R013 to resolve this issue.

        CardGroupStatusVO cardGroupStatusVO = new CardGroupStatusVO();
        cardGroupStatusVO.setIdentifierType("Identifier Type");
        cardGroupStatusVO.setIdentifierValue("42");
        cardGroupStatusVO.setModuleCode("Module Code");
        cardGroupStatusVO.setNetworkCode("Network Code");
        cardGroupChangeStatus.validateRequestData("Type", pretupsResponse, cardGroupStatusVO, "Reference Name");
    }

    /**
     * Method under test: {@link CardGroupChangeStatus#validateRequestData(String, PretupsResponse, CardGroupStatusVO, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateRequestData2() throws IOException, ValidatorException, SAXException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.cardgroup.service.CardGroupChangeStatus.validateRequestData(CardGroupChangeStatus.java:302)
        //   See https://diff.blue/R013 to resolve this issue.

        CardGroupStatusVO cardGroupStatusVO = mock(CardGroupStatusVO.class);
        doNothing().when(cardGroupStatusVO).setIdentifierType(Mockito.<String>any());
        doNothing().when(cardGroupStatusVO).setIdentifierValue(Mockito.<String>any());
        doNothing().when(cardGroupStatusVO).setModuleCode(Mockito.<String>any());
        doNothing().when(cardGroupStatusVO).setNetworkCode(Mockito.<String>any());
        cardGroupStatusVO.setIdentifierType("Identifier Type");
        cardGroupStatusVO.setIdentifierValue("42");
        cardGroupStatusVO.setModuleCode("Module Code");
        cardGroupStatusVO.setNetworkCode("Network Code");
        cardGroupChangeStatus.validateRequestData("Type", pretupsResponse, cardGroupStatusVO, "Reference Name");
    }
}

