package com.restapi.networkadmin.commissionprofile.requestVO;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class ChangeStatusForCommissionProfileRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ChangeStatusForCommissionProfileRequestVO}
     *   <li>{@link ChangeStatusForCommissionProfileRequestVO#setChangeStatusListForCommissionProfile(ArrayList)}
     *   <li>{@link ChangeStatusForCommissionProfileRequestVO#getChangeStatusListForCommissionProfile()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ChangeStatusForCommissionProfileRequestVO actualChangeStatusForCommissionProfileRequestVO = new ChangeStatusForCommissionProfileRequestVO();
        ArrayList<ChangeStatusForCommissionProfileVO> changeStatusListForCommissionProfile = new ArrayList<>();
        actualChangeStatusForCommissionProfileRequestVO
                .setChangeStatusListForCommissionProfile(changeStatusListForCommissionProfile);
        assertSame(changeStatusListForCommissionProfile,
                actualChangeStatusForCommissionProfileRequestVO.getChangeStatusListForCommissionProfile());
    }
}

