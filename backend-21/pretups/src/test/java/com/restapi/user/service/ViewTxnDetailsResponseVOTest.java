package com.restapi.user.service;

import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import org.junit.Test;

public class ViewTxnDetailsResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ViewTxnDetailsResponseVO}
     *   <li>{@link ViewTxnDetailsResponseVO#setDataObj(ChannelTransferVO)}
     *   <li>{@link ViewTxnDetailsResponseVO#getDataObj()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ViewTxnDetailsResponseVO actualViewTxnDetailsResponseVO = new ViewTxnDetailsResponseVO();
        ChannelTransferVO dataObj = ChannelTransferVO.getInstance();
        actualViewTxnDetailsResponseVO.setDataObj(dataObj);
        assertSame(dataObj, actualViewTxnDetailsResponseVO.getDataObj());
    }
}

