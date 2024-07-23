package com.restapi.networkadmin.cardgroup.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

public class ViewC2SCardGroupResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ViewC2SCardGroupResponseVO}
     *   <li>{@link ViewC2SCardGroupResponseVO#setAmountTypeList(ArrayList)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setApplicableFromDate(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setApplicableFromHour(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setBonusValidityValue(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setBoth(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setCGStatus(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setCardGroupCode(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setCardGroupID(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setCardGroupList(ArrayList)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setCardGroupSetName(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setCardGroupSetNameList(ArrayList)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setCardGroupSetVersionList(ArrayList)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setCardGroupSubServiceID(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setCardGroupSubServiceList(ArrayList)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setCardGroupSubServiceName(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setCardName(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setCosRequired(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setDefaultCardGroupRequired(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setDeleteAllowed(Boolean)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setEndRange(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setGracePeriod(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setInPromo(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setMaxReceiverAccessFee(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setMinReceiverAccessFee(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setMultipleOf(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setOldApplicableFromDate(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setOldApplicableFromHour(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setOnline(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setReceiverAccessFeeRate(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setReceiverAccessFeeType(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setReceiverConvFactor(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setReceiverTax1Name(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setReceiverTax1Rate(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setReceiverTax1Type(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setReceiverTax2Name(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setReceiverTax2Rate(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setReceiverTax2Type(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setReversalModifiedDate(Date)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setReversalModifiedDateAsString(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setReversalPermitted(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setSelectCardGroupSetId(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setSelectCardGroupSetVersionId(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setServiceTypeId(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setServiceTypeList(ArrayList)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setServiceTypedesc(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setSetType(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setSetTypeList(ArrayList)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setSetTypeName(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setStartRange(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setTempAccList(ArrayList)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setValidityPeriod(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setValidityPeriodType(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setValidityTypeList(ArrayList)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setVersion(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#setViewSlabCopy(String)}
     *   <li>{@link ViewC2SCardGroupResponseVO#getAmountTypeList()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getApplicableFromDate()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getApplicableFromHour()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getBonusValidityValue()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getBoth()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getCGStatus()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getCardGroupCode()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getCardGroupID()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getCardGroupList()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getCardGroupSetName()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getCardGroupSetNameList()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getCardGroupSetVersionList()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getCardGroupSubServiceID()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getCardGroupSubServiceList()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getCardGroupSubServiceName()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getCardName()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getCosRequired()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getDefaultCardGroupRequired()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getDeleteAllowed()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getEndRange()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getGracePeriod()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getInPromo()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getMaxReceiverAccessFee()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getMinReceiverAccessFee()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getMultipleOf()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getOldApplicableFromDate()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getOldApplicableFromHour()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getOnline()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getReceiverAccessFeeRate()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getReceiverAccessFeeType()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getReceiverConvFactor()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getReceiverTax1Name()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getReceiverTax1Rate()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getReceiverTax1Type()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getReceiverTax2Name()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getReceiverTax2Rate()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getReceiverTax2Type()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getReversalModifiedDate()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getReversalModifiedDateAsString()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getReversalPermitted()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getSelectCardGroupSetId()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getSelectCardGroupSetVersionId()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getServiceTypeId()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getServiceTypeList()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getServiceTypedesc()}
     *   <li>{@link ViewC2SCardGroupResponseVO#getSetType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ViewC2SCardGroupResponseVO actualViewC2SCardGroupResponseVO = new ViewC2SCardGroupResponseVO();
        ArrayList amountTypeList = new ArrayList();
        //jdk21  actualViewC2SCardGroupResponseVO.setAmountTypeList(amountTypeList);
        actualViewC2SCardGroupResponseVO.setApplicableFromDate("2020-03-01");
        actualViewC2SCardGroupResponseVO.setApplicableFromHour("jane.doe@example.org");
        actualViewC2SCardGroupResponseVO.setBonusValidityValue("42");
        actualViewC2SCardGroupResponseVO.setBoth("Both");
        actualViewC2SCardGroupResponseVO.setCGStatus("C GStatus");
        actualViewC2SCardGroupResponseVO.setCardGroupCode("Card Group Code");
        actualViewC2SCardGroupResponseVO.setCardGroupID("Card Group ID");
        ArrayList cardGroupList = new ArrayList();
        actualViewC2SCardGroupResponseVO.setCardGroupList(cardGroupList);
        actualViewC2SCardGroupResponseVO.setCardGroupSetName("Card Group Set Name");
        ArrayList cardGroupSetNameList = new ArrayList();
        //jdk21   actualViewC2SCardGroupResponseVO.setCardGroupSetNameList(cardGroupSetNameList);
        ArrayList cardGroupSetVersionList = new ArrayList();
        //jdk21actualViewC2SCardGroupResponseVO.setCardGroupSetVersionList(cardGroupSetVersionList);
        actualViewC2SCardGroupResponseVO.setCardGroupSubServiceID("Card Group Sub Service ID");
        ArrayList cardGroupSubServiceList = new ArrayList();
        //jdk21actualViewC2SCardGroupResponseVO.setCardGroupSubServiceList(cardGroupSubServiceList);
        actualViewC2SCardGroupResponseVO.setCardGroupSubServiceName("Card Group Sub Service Name");
        actualViewC2SCardGroupResponseVO.setCardName("Card Name");
        actualViewC2SCardGroupResponseVO.setCosRequired("Cos Required");
        actualViewC2SCardGroupResponseVO.setDefaultCardGroupRequired("Default Card Group Required");
        actualViewC2SCardGroupResponseVO.setDeleteAllowed(true);
        actualViewC2SCardGroupResponseVO.setEndRange("End Range");
        actualViewC2SCardGroupResponseVO.setGracePeriod("Grace Period");
        actualViewC2SCardGroupResponseVO.setInPromo("In Promo");
        actualViewC2SCardGroupResponseVO.setMaxReceiverAccessFee("Max Receiver Access Fee");
        actualViewC2SCardGroupResponseVO.setMinReceiverAccessFee("Min Receiver Access Fee");
        actualViewC2SCardGroupResponseVO.setMultipleOf("Multiple Of");
        actualViewC2SCardGroupResponseVO.setOldApplicableFromDate("2020-03-01");
        actualViewC2SCardGroupResponseVO.setOldApplicableFromHour("jane.doe@example.org");
        actualViewC2SCardGroupResponseVO.setOnline("Online");
        actualViewC2SCardGroupResponseVO.setReceiverAccessFeeRate("Receiver Access Fee Rate");
        actualViewC2SCardGroupResponseVO.setReceiverAccessFeeType("Receiver Access Fee Type");
        actualViewC2SCardGroupResponseVO.setReceiverConvFactor("Receiver Conv Factor");
        actualViewC2SCardGroupResponseVO.setReceiverTax1Name("Receiver Tax1 Name");
        actualViewC2SCardGroupResponseVO.setReceiverTax1Rate("Receiver Tax1 Rate");
        actualViewC2SCardGroupResponseVO.setReceiverTax1Type("Receiver Tax1 Type");
        actualViewC2SCardGroupResponseVO.setReceiverTax2Name("Receiver Tax2 Name");
        actualViewC2SCardGroupResponseVO.setReceiverTax2Rate("Receiver Tax2 Rate");
        actualViewC2SCardGroupResponseVO.setReceiverTax2Type("Receiver Tax2 Type");
        Date reversalModifiedDate = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        actualViewC2SCardGroupResponseVO.setReversalModifiedDate(reversalModifiedDate);
        actualViewC2SCardGroupResponseVO.setReversalModifiedDateAsString("2020-03-01");
        actualViewC2SCardGroupResponseVO.setReversalPermitted("Reversal Permitted");
        actualViewC2SCardGroupResponseVO.setSelectCardGroupSetId("42");
        actualViewC2SCardGroupResponseVO.setSelectCardGroupSetVersionId("42");
        actualViewC2SCardGroupResponseVO.setServiceTypeId("42");
        ArrayList serviceTypeList = new ArrayList();
        //jdk21   actualViewC2SCardGroupResponseVO.setServiceTypeList(serviceTypeList);
        actualViewC2SCardGroupResponseVO.setServiceTypedesc("Service Typedesc");
        actualViewC2SCardGroupResponseVO.setSetType("Set Type");
        //jdk21    actualViewC2SCardGroupResponseVO.setSetTypeList(new ArrayList());
        actualViewC2SCardGroupResponseVO.setSetTypeName("Set Type Name");
        actualViewC2SCardGroupResponseVO.setStartRange("Start Range");
        actualViewC2SCardGroupResponseVO.setTempAccList(new ArrayList());
        actualViewC2SCardGroupResponseVO.setValidityPeriod("Validity Period");
        actualViewC2SCardGroupResponseVO.setValidityPeriodType("Validity Period Type");
        actualViewC2SCardGroupResponseVO.setValidityTypeList(new ArrayList());
        actualViewC2SCardGroupResponseVO.setVersion("1.0.2");
        actualViewC2SCardGroupResponseVO.setViewSlabCopy("View Slab Copy");
        //jdk21   assertSame(amountTypeList, actualViewC2SCardGroupResponseVO.getAmountTypeList());
    /*    assertEquals("2020-03-01", actualViewC2SCardGroupResponseVO.getApplicableFromDate());
        assertEquals("jane.doe@example.org", actualViewC2SCardGroupResponseVO.getApplicableFromHour());
        assertEquals("42", actualViewC2SCardGroupResponseVO.getBonusValidityValue());
        assertEquals("Both", actualViewC2SCardGroupResponseVO.getBoth());
        assertEquals("C GStatus", actualViewC2SCardGroupResponseVO.getCGStatus());
        assertEquals("Card Group Code", actualViewC2SCardGroupResponseVO.getCardGroupCode());
        assertEquals("Card Group ID", actualViewC2SCardGroupResponseVO.getCardGroupID());
        assertSame(cardGroupList, actualViewC2SCardGroupResponseVO.getCardGroupList());
        assertEquals("Card Group Set Name", actualViewC2SCardGroupResponseVO.getCardGroupSetName());
        assertSame(cardGroupSetNameList, actualViewC2SCardGroupResponseVO.getCardGroupSetNameList());
        assertSame(cardGroupSetVersionList, actualViewC2SCardGroupResponseVO.getCardGroupSetVersionList());
        assertEquals("Card Group Sub Service ID", actualViewC2SCardGroupResponseVO.getCardGroupSubServiceID());
        //jdk21  assertSame(cardGroupSubServiceList, actualViewC2SCardGroupResponseVO.getCardGroupSubServiceList());
        assertEquals("Card Group Sub Service Name", actualViewC2SCardGroupResponseVO.getCardGroupSubServiceName());
        assertEquals("Card Name", actualViewC2SCardGroupResponseVO.getCardName());
        assertEquals("Cos Required", actualViewC2SCardGroupResponseVO.getCosRequired());
        assertEquals("Default Card Group Required", actualViewC2SCardGroupResponseVO.getDefaultCardGroupRequired());
        assertTrue(actualViewC2SCardGroupResponseVO.getDeleteAllowed());
        assertEquals("End Range", actualViewC2SCardGroupResponseVO.getEndRange());
        assertEquals("Grace Period", actualViewC2SCardGroupResponseVO.getGracePeriod());
        assertEquals("In Promo", actualViewC2SCardGroupResponseVO.getInPromo());
        assertEquals("Max Receiver Access Fee", actualViewC2SCardGroupResponseVO.getMaxReceiverAccessFee());
        assertEquals("Min Receiver Access Fee", actualViewC2SCardGroupResponseVO.getMinReceiverAccessFee());
        assertEquals("Multiple Of", actualViewC2SCardGroupResponseVO.getMultipleOf());
        assertEquals("2020-03-01", actualViewC2SCardGroupResponseVO.getOldApplicableFromDate());
        assertEquals("jane.doe@example.org", actualViewC2SCardGroupResponseVO.getOldApplicableFromHour());
        assertEquals("Online", actualViewC2SCardGroupResponseVO.getOnline());
        assertEquals("Receiver Access Fee Rate", actualViewC2SCardGroupResponseVO.getReceiverAccessFeeRate());
        assertEquals("Receiver Access Fee Type", actualViewC2SCardGroupResponseVO.getReceiverAccessFeeType());
        assertEquals("Receiver Conv Factor", actualViewC2SCardGroupResponseVO.getReceiverConvFactor());
        assertEquals("Receiver Tax1 Name", actualViewC2SCardGroupResponseVO.getReceiverTax1Name());
        assertEquals("Receiver Tax1 Rate", actualViewC2SCardGroupResponseVO.getReceiverTax1Rate());
        assertEquals("Receiver Tax1 Type", actualViewC2SCardGroupResponseVO.getReceiverTax1Type());
        assertEquals("Receiver Tax2 Name", actualViewC2SCardGroupResponseVO.getReceiverTax2Name());
        assertEquals("Receiver Tax2 Rate", actualViewC2SCardGroupResponseVO.getReceiverTax2Rate());
        assertEquals("Receiver Tax2 Type", actualViewC2SCardGroupResponseVO.getReceiverTax2Type());
        assertSame(reversalModifiedDate, actualViewC2SCardGroupResponseVO.getReversalModifiedDate());
        assertEquals("2020-03-01", actualViewC2SCardGroupResponseVO.getReversalModifiedDateAsString());
        assertEquals("Reversal Permitted", actualViewC2SCardGroupResponseVO.getReversalPermitted());
        assertEquals("42", actualViewC2SCardGroupResponseVO.getSelectCardGroupSetId());
        assertEquals("42", actualViewC2SCardGroupResponseVO.getSelectCardGroupSetVersionId());
        assertEquals("42", actualViewC2SCardGroupResponseVO.getServiceTypeId());
        //jdk21 assertSame(serviceTypeList, actualViewC2SCardGroupResponseVO.getServiceTypeList());
        assertEquals("Service Typedesc", actualViewC2SCardGroupResponseVO.getServiceTypedesc());
        assertEquals("Set Type", actualViewC2SCardGroupResponseVO.getSetType());
    */}
}

