package com.restapi.networkadmin.cardgroup.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CardGroupDetailsRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CardGroupDetailsRequestVO}
     *   <li>{@link CardGroupDetailsRequestVO#toString()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        assertEquals("CardGroupDetailsRequestVO [cardGroupSetID=null, version=null, cardGroupID=null, cardGroupCode=null,"
                + " startRange=0, endRange=0, validityPeriodType=null, validityPeriod=0, gracePeriod=0, receiverTax1Name=null,"
                + " receiverTax1Type=null, receiverTax1Rate=0.0, receiverTax2Name=null, receiverTax2Type=null,"
                + " receiverTax2Rate=0.0, receiverAccessFeeType=null, receiverAccessFeeRate=0.0, minReceiverAccessFee=0,"
                + " maxReceiverAccessFee=0, multipleOf=0, cardGroupSetName=null, cardGroupSubServiceIdDesc=null,"
                + " serviceTypeId=null, serviceTypeDesc=null, setType=null, setTypeName=null, status=null, rowIndex=0,"
                + " bonusValidityValue=0, online=null, both=null, networkCode=null, LastVersion=null, receiverConvFactor=null,"
                + " bonusAccList=null, tempAccList=null, locationIndex=0, bonusTalktimevalidity=0, cosRequired=null,"
                + " inPromo=0.0, reversalPermitted=null, cardName=null, reversalModifiedDate=null, reversalModifiedDateAsString"
                + "=null, maxReceiverAccessFeeAsString=null, startRangeAsString=null, validityPeriodAsString=null,"
                + " endRangeAsString=null, minReceiverAccessFeeAsString=null, multipleOfAsString=null, inPromoAsString=null,"
                + " receiverAccessFeeRateAsString=null, receiverTax1RateAsString=null, receiverTax2RateAsString=null,"
                + " validityPeriodTypeDesc=null, cardGroupSubServiceID=null, cardGroupList=null, editDetail=null,"
                + " cardGroupType=null]", (new CardGroupDetailsRequestVO()).toString());
    }
}

