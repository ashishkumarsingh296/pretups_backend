package com.restapi.networkadmin.cardgroup.requestVO;

import com.btsl.pretups.cardgroup.businesslogic.BonusAccountDetailsVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class CalculateTransferValueRequestVO {

	private long requestedAmount;
	//*100
	private double multipleOf;
	private double receiverAccessFeeRate;
	private double receiverTax2Rate;
	private double receiverTax1Rate;
    private String receiverAccessFeeType;
    private long minReceiverAccessFee;
    private long maxReceiverAccessFee;
    private String receiverTax1Type;
	private String receiverTax2Type;
	private String receiverConvFactor;
	//GRC:1
	private String cardGroupSubServiceID;


	private ArrayList<BonusAccountDetailsVO> bonusAccList = null;
	private int validityPeriod;
	private long bonusPeriod;
	private long gracePeriod;

	private String validityPeriodType;
}
