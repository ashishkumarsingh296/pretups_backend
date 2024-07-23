package com.restapi.networkadmin;

import static org.junit.Assert.assertSame;

import com.btsl.common.ListValueVO;

import java.util.ArrayList;

import org.junit.Test;

public class C2STransferRuleResponseVO1Test {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2STransferRuleResponseVO1}
     *   <li>{@link C2STransferRuleResponseVO1#setCardGroupList(ArrayList)}
     *   <li>{@link C2STransferRuleResponseVO1#setGatewayList(ArrayList)}
     *   <li>{@link C2STransferRuleResponseVO1#setServiceClassList(ArrayList)}
     *   <li>{@link C2STransferRuleResponseVO1#setServiceTypeList(ArrayList)}
     *   <li>{@link C2STransferRuleResponseVO1#setSubServiceTypeList(ArrayList)}
     *   <li>{@link C2STransferRuleResponseVO1#getCardGroupList()}
     *   <li>{@link C2STransferRuleResponseVO1#getGatewayList()}
     *   <li>{@link C2STransferRuleResponseVO1#getServiceClassList()}
     *   <li>{@link C2STransferRuleResponseVO1#getServiceTypeList()}
     *   <li>{@link C2STransferRuleResponseVO1#getSubServiceTypeList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2STransferRuleResponseVO1 actualC2sTransferRuleResponseVO1 = new C2STransferRuleResponseVO1();
        ArrayList<ListValueVO> cardGroupList = new ArrayList<>();
        actualC2sTransferRuleResponseVO1.setCardGroupList(cardGroupList);
        ArrayList<ListValueVO> gatewayList = new ArrayList<>();
        actualC2sTransferRuleResponseVO1.setGatewayList(gatewayList);
        ArrayList serviceClassList = new ArrayList();
        actualC2sTransferRuleResponseVO1.setServiceClassList(serviceClassList);
        ArrayList serviceTypeList = new ArrayList();
        actualC2sTransferRuleResponseVO1.setServiceTypeList(serviceTypeList);
        ArrayList subServiceTypeList = new ArrayList();
        actualC2sTransferRuleResponseVO1.setSubServiceTypeList(subServiceTypeList);
        assertSame(cardGroupList, actualC2sTransferRuleResponseVO1.getCardGroupList());
        assertSame(gatewayList, actualC2sTransferRuleResponseVO1.getGatewayList());
        assertSame(serviceClassList, actualC2sTransferRuleResponseVO1.getServiceClassList());
        assertSame(serviceTypeList, actualC2sTransferRuleResponseVO1.getServiceTypeList());
        assertSame(subServiceTypeList, actualC2sTransferRuleResponseVO1.getSubServiceTypeList());
    }
}

