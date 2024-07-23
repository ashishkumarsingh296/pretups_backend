package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.web.pretups.channel.transfer.web.ChannelTransferEnquiryModel;
import org.junit.Test;

public class O2CTransferDetailsResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CTransferDetailsResponseVO}
     *   <li>{@link O2CTransferDetailsResponseVO#setChannelTransferVO(ChannelTransferVO)}
     *   <li>{@link O2CTransferDetailsResponseVO#setTransferDetails(ChannelTransferEnquiryModel)}
     *   <li>{@link O2CTransferDetailsResponseVO#toString()}
     *   <li>{@link O2CTransferDetailsResponseVO#getChannelTransferVO()}
     *   <li>{@link O2CTransferDetailsResponseVO#getTransferDetails()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CTransferDetailsResponseVO actualO2cTransferDetailsResponseVO = new O2CTransferDetailsResponseVO();
        ChannelTransferVO channelTransferVO = ChannelTransferVO.getInstance();
        actualO2cTransferDetailsResponseVO.setChannelTransferVO(channelTransferVO);
        ChannelTransferEnquiryModel transferDetails = new ChannelTransferEnquiryModel();
        actualO2cTransferDetailsResponseVO.setTransferDetails(transferDetails);
        actualO2cTransferDetailsResponseVO.toString();
        assertSame(channelTransferVO, actualO2cTransferDetailsResponseVO.getChannelTransferVO());
        assertSame(transferDetails, actualO2cTransferDetailsResponseVO.getTransferDetails());
    }
}

