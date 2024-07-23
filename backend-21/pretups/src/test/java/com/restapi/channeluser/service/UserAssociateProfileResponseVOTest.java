package com.restapi.channeluser.service;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class UserAssociateProfileResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserAssociateProfileResponseVO}
     *   <li>{@link UserAssociateProfileResponseVO#setGradeList(List)}
     *   <li>{@link UserAssociateProfileResponseVO#setLmsList(List)}
     *   <li>{@link UserAssociateProfileResponseVO#setTransferProfileList(List)}
     *   <li>{@link UserAssociateProfileResponseVO#setTransferRuleTypeList(List)}
     *   <li>{@link UserAssociateProfileResponseVO#getGradeList()}
     *   <li>{@link UserAssociateProfileResponseVO#getLmsList()}
     *   <li>{@link UserAssociateProfileResponseVO#getTransferProfileList()}
     *   <li>{@link UserAssociateProfileResponseVO#getTransferRuleTypeList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserAssociateProfileResponseVO actualUserAssociateProfileResponseVO = new UserAssociateProfileResponseVO();
        ArrayList<GradeList> gradeList = new ArrayList<>();
        actualUserAssociateProfileResponseVO.setGradeList(gradeList);
        ArrayList<LMSList> lmsList = new ArrayList<>();
        actualUserAssociateProfileResponseVO.setLmsList(lmsList);
        ArrayList<TransferProfileList> transferProfileList = new ArrayList<>();
        actualUserAssociateProfileResponseVO.setTransferProfileList(transferProfileList);
        ArrayList<TransferRuleTypeList> transferRuleTypeList = new ArrayList<>();
        actualUserAssociateProfileResponseVO.setTransferRuleTypeList(transferRuleTypeList);
        assertSame(gradeList, actualUserAssociateProfileResponseVO.getGradeList());
        assertSame(lmsList, actualUserAssociateProfileResponseVO.getLmsList());
        assertSame(transferProfileList, actualUserAssociateProfileResponseVO.getTransferProfileList());
        assertSame(transferRuleTypeList, actualUserAssociateProfileResponseVO.getTransferRuleTypeList());
    }
}

