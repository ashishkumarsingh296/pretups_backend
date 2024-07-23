package com.restapi.channelAdmin;

import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CommissionProfileResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CommissionProfileResponseVO}
     *   <li>{@link CommissionProfileResponseVO#setCommissionProfileList(List)}
     *   <li>{@link CommissionProfileResponseVO#getCommissionProfileList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CommissionProfileResponseVO actualCommissionProfileResponseVO = new CommissionProfileResponseVO();
        ArrayList<CommissionProfileSetVO> commissionProfileList = new ArrayList<>();
        actualCommissionProfileResponseVO.setCommissionProfileList(commissionProfileList);
        assertSame(commissionProfileList, actualCommissionProfileResponseVO.getCommissionProfileList());
    }
}

