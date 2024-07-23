package com.pretupsControllers.commissionprofile;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.classes.BaseTest;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.SystemPreferences;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.pretupsControllers.calculatorI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils._parser;

public class ChannelTransferBL extends BaseTest{
	
	public static long productCost = 0;
	public static long value = 0;
	static String POSITIVE_COMM_APPLY;
	

    /**
     * APPLICABLE FOR 6.8 AND BELOW
     * 
     * Method calculateMRPWithTaxAndDiscount()
     * To calculate the taxes on the selected product list
     * 
     * @param p_transferItemsList
     * @param p_txnType
     *            String
     * @throws Exception
     */
    public static HashMap<String, String> calculateMRPWithTaxAndDiscount(String ReceiverMSISDN, String p_txnType, String ProductCode, String requestedQuantity) throws Exception {
    	HashMap<String, String> returnMap = new HashMap<String, String>();
    	ChannelTransfersVO ChannelTransfersVO = new ChannelTransfersVO();
        ChannelTransfersVO.loadChannelTransfersDAO(ReceiverMSISDN, ProductCode, requestedQuantity);
    	long productCost = 0, value = 0;

            productCost = (long) (Double.parseDouble(ChannelTransfersVO.getRequestedQuantity()) * ChannelTransfersVO.getUnitValue());
            // In case of FOC Commission will not be calculated
            if (!p_txnType.equals(_masterVO.getProperty("FOCCode"))) {
                value = calculatorI.calculateCommission(ChannelTransfersVO.getCommType(), ChannelTransfersVO.getCommRate(), productCost);
            }
            ChannelTransfersVO.setCommValue(value);
            value = 0;
            value = calculatorI.calculateDiscount(ChannelTransfersVO.getDiscountType(), ChannelTransfersVO.getDiscountRate(), productCost);
            ChannelTransfersVO.setDiscountValue(value);
            value = 0;
            /*
             * To check whether tax is applicable for transaction or not.We have
             * the following three type of the
             * transaction and following corresponding check condition.
             * 1. In case of O2C transfer Tax calculation is mandatory.
             * 2. In case of C2C transfer Tax calculation is based on the
             * TAX_APPLICABLE_ON_C2C flag of the
             * commission profile.
             * 3. In case of FOC transfer Tax calculation is based on the
             * TAX_APPLICABLE_ON_FOC flag of the
             * commission profile.
             */
            if (p_txnType.equals(_masterVO.getProperty("O2CTransferCode")) || p_txnType.equals(_masterVO.getProperty("O2CWithdrawCode")) || 
            	p_txnType.equals(_masterVO.getProperty("O2CReturnCode")) || ((p_txnType.equals(_masterVO.getProperty("C2CTransferCode")) || 
            	p_txnType.equals(_masterVO.getProperty("C2CWithdrawCode")) || p_txnType.equals(_masterVO.getProperty("C2CReturnCode"))) && PretupsI.YES.equals(ChannelTransfersVO
                .getTaxOnChannelTransfer())) || (p_txnType.equals(_masterVO.getProperty("FOCCode")) && PretupsI.YES.equals(ChannelTransfersVO.getTaxOnFOCTransfer()))) {
                value = calculatorI.calculateTax1(ChannelTransfersVO.getTax1Type(), ChannelTransfersVO.getTax1Rate(), productCost);
                ChannelTransfersVO.setTax1Value(value);
                value = 0;
                value = calculatorI.calculateTax2(ChannelTransfersVO.getTax2Type(), ChannelTransfersVO.getTax2Rate(), ChannelTransfersVO.getTax1Value());
                ChannelTransfersVO.setTax2Value(value);
                value = 0;
                // set commision value as 0 , becoz commision not calculated on
                // the commision in case pf withdraw and return.
                POSITIVE_COMM_APPLY = DBHandler.AccessHandler.getSystemPreference("POSITIVE_COMM_APPLY");
                if (POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && (!_masterVO.getProperty("O2CTransferCode").equalsIgnoreCase(p_txnType) || !_masterVO.getProperty("C2CTransferCode").equalsIgnoreCase(p_txnType))/*!PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(p_channelTransferVO.getTransferSubType())*/) {
                	ChannelTransfersVO.setCommValue(0);
                }
                // In case of FOC Tax3 will not be calculated
                if (!p_txnType.equals(_masterVO.getProperty("FOCCode"))) {
                    value = calculatorI.calculateTax3(ChannelTransfersVO.getTax3Type(), ChannelTransfersVO.getTax3Rate(), ChannelTransfersVO.getCommValue());
                }
                ChannelTransfersVO.setTax3Value(value);
                value = 0;
            }
            value = calculatorI.calculateCommissionQuantity(ChannelTransfersVO.getCommValue(), ChannelTransfersVO.getUnitValue(), ChannelTransfersVO
                .getTax3Value());
            ChannelTransfersVO.setCommQuantity(value);
            long payableAmount = 0;
            long netPayableAmount = 0;
            // In case of FOC payableAmount will not be calculated
            /*
             * if(!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC))
             * {
             * payableAmount =
             * calculatorI.calculatePayableAmount(channelTransferItemsVO
             * .getUnitValue
             * (),Double.parseDouble(channelTransferItemsVO.getRequestedQuantity
             * ()),channelTransferItemsVO.getCommValue(),channelTransferItemsVO.
             * getDiscountValue());
             * netPayableAmount =
             * calculatorI.calculateNetPayableAmount(payableAmount
             * ,channelTransferItemsVO.getTax3Value());
             * }
             */
            if (!p_txnType.equals(_masterVO.getProperty("FOCCode")) && POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !_masterVO.getProperty("C2CTransferCode").equalsIgnoreCase(p_txnType) /*!PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER
                .equalsIgnoreCase(p_channelTransferVO.getTransferSubType())*/ ) {
            	Log.info("Entered First Segment");
            	returnMap.put("SENDER_DEBIT_QTY", ChannelTransfersVO.getRequestedQuantity());
            	ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
            	returnMap.put("RECEIVER_CREDIT_QTY", "" + ChannelTransfersVO.getRequiredQuantity());
            	ChannelTransfersVO.setReceiverCreditQty(ChannelTransfersVO.getRequiredQuantity());
            	returnMap.put("PRODUCT_TOTAL_MRP", "" + productCost);
            	ChannelTransfersVO.setProductTotalMRP(productCost);
            	returnMap.put("PAYABLE_AMOUNT", "" + productCost);
            	ChannelTransfersVO.setPayableAmount(productCost);
            	returnMap.put("NET_PAYABLE_AMOUNT", "" + productCost);
            	ChannelTransfersVO.setNetPayableAmount(productCost);
            }// this is executed in case of Transfer and POSITIVE_COMM_APPLY is
             // true
            else if (POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !p_txnType.equals(_masterVO.getProperty("FOCCode")) && _masterVO.getProperty("C2CTransferCode")
                .equalsIgnoreCase(p_txnType)) {
            	Log.info("Entered Second Segment");
            	value = 0;
                value = calculatorI.calculateReceiverCreditQuantity(ChannelTransfersVO.getRequestedQuantity(), ChannelTransfersVO.getUnitValue(),
                		ChannelTransfersVO.getCommQuantity());
                returnMap.put("SENDER_DEBIT_QTY", "" + _parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
                ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
                returnMap.put("RECEIVER_CREDIT_QTY", "" + value);
                ChannelTransfersVO.setReceiverCreditQty(value);
                returnMap.put("SET_PAYABLE_AMOUNT", "" + productCost);
                ChannelTransfersVO.setPayableAmount(productCost);
                returnMap.put("NET_PAYABLE_AMOUNT", "" + productCost);
                ChannelTransfersVO.setNetPayableAmount(productCost);
                returnMap.put("PRODUCT_TOTAL_MRP", "" + ChannelTransfersVO.getReceiverCreditQty() * ChannelTransfersVO.getUnitValue() / (SystemPreferences.MULTIPLICATIONFACTOR));
                ChannelTransfersVO
                    .setProductTotalMRP(ChannelTransfersVO.getReceiverCreditQty() * ChannelTransfersVO.getUnitValue() / (SystemPreferences.MULTIPLICATIONFACTOR));
            }// executed in case of FOC or in case of POSITIVE_COMM_APPLY value
             // is false
            else if (!POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !p_txnType.equals(_masterVO.getProperty("FOCCode"))) {
            	Log.info("Entered Third Segment");
            	payableAmount = calculatorI.calculatePayableAmount(ChannelTransfersVO.getUnitValue(), Double.parseDouble(ChannelTransfersVO.getRequestedQuantity()),
                		ChannelTransfersVO.getCommValue(), ChannelTransfersVO.getDiscountValue());
                netPayableAmount = calculatorI.calculateNetPayableAmount(payableAmount, ChannelTransfersVO.getTax3Value());
                returnMap.put("PAYABLE_AMOUNT", "" + payableAmount);
                ChannelTransfersVO.setPayableAmount(payableAmount);
                returnMap.put("NET_PAYABLE_AMOUNT", "" + netPayableAmount);
                ChannelTransfersVO.setNetPayableAmount(netPayableAmount);
                returnMap.put("SENDER_DEBIT_QTY", "" + _parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
                ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
                returnMap.put("RECEIVER_CREDIT_QTY", "" + ChannelTransfersVO.getRequiredQuantity());
                ChannelTransfersVO.setReceiverCreditQty(ChannelTransfersVO.getRequiredQuantity());
                returnMap.put("PRODUCT_TOTAL_MRP", "" + productCost);
                ChannelTransfersVO.setProductTotalMRP(productCost);
            } else {
            	Log.info("Entered Else Segment");
            	returnMap.put("SENDER_DEBIT_QTY", "" + _parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
            	ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
            	returnMap.put("RECEIVER_CREDIT_QTY", "" + ChannelTransfersVO.getRequiredQuantity());
            	ChannelTransfersVO.setReceiverCreditQty(ChannelTransfersVO.getRequiredQuantity());
            	returnMap.put("PRODUCT_TOTAL_MRP", "" + productCost);
            	ChannelTransfersVO.setProductTotalMRP(productCost);
            	returnMap.put("PAYABLE_AMOUNT", "" + payableAmount);
            	ChannelTransfersVO.setPayableAmount(payableAmount);
            	returnMap.put("NET_PAYABLE_AMOUNT", "" + netPayableAmount);
            	ChannelTransfersVO.setNetPayableAmount(netPayableAmount);
            }
            //ChannelTransfersVO.setApprovedQuantity(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
            return returnMap;
    }
    
    

    /**
     * Method calculateMRPWithTaxAndDiscount()
     * To calculate the taxes on the selected product list
     * 
     * @param p_transferItemsList
     * @param p_txnType
     *            String
     * @throws Exception
     */
    public static HashMap<String, String> calculateAmount(String ReceiverMSISDN, String p_txnType, String ProductCode, String requestedQuantity) throws Exception {
    	HashMap<String, String> calcMap = new HashMap<String, String>();
    	ChannelTransfersVO ChannelTransfersVO = new ChannelTransfersVO();
        ChannelTransfersVO.loadChannelTransfersDAO(ReceiverMSISDN, ProductCode, requestedQuantity);
    	long productCost = 0, value = 0;
    	
            productCost = (long) (Double.parseDouble(ChannelTransfersVO.getRequestedQuantity()) * ChannelTransfersVO.getUnitValue());
            // In case of FOC Commission will not be calculated
            if (!p_txnType.equals(_masterVO.getProperty("FOCCode"))) {
                value = calculatorI.calculateCommission(ChannelTransfersVO.getCommType(), ChannelTransfersVO.getCommRate(), productCost);
            }
            ChannelTransfersVO.setCommValue(value);
            value = 0;
            value = calculatorI.calculateDiscount(ChannelTransfersVO.getDiscountType(), ChannelTransfersVO.getDiscountRate(), productCost);
            ChannelTransfersVO.setDiscountValue(value);
            value = 0;
            /*
             * To check whether tax is applicable for transaction or not.We have
             * the following three type of the
             * transaction and following corresponding check condition.
             * 1. In case of O2C transfer Tax calculation is mandatory.
             * 2. In case of C2C transfer Tax calculation is based on the
             * TAX_APPLICABLE_ON_C2C flag of the
             * commission profile.
             * 3. In case of FOC transfer Tax calculation is based on the
             * TAX_APPLICABLE_ON_FOC flag of the
             * commission profile.
             */
            if (p_txnType.equals(_masterVO.getProperty("O2CTransferCode")) || p_txnType.equals(_masterVO.getProperty("O2CWithdrawCode")) || p_txnType.equals(_masterVO.getProperty("O2CReturnCode")) || ( (p_txnType.equals(_masterVO.getProperty("C2CTransferCode")) || p_txnType.equals(_masterVO.getProperty("C2CWithdrawCode")) || p_txnType.equals(_masterVO.getProperty("C2CReturnCode"))) && PretupsI.YES.equals(ChannelTransfersVO
                .getTaxOnChannelTransfer())) || (p_txnType.equals(_masterVO.getProperty("FOCCode")) && PretupsI.YES.equals(ChannelTransfersVO.getTaxOnFOCTransfer()))) {
                value = calculatorI.calculateTax1(ChannelTransfersVO.getTax1Type(), ChannelTransfersVO.getTax1Rate(), productCost);
                ChannelTransfersVO.setTax1Value(value);
                value = 0;
                value = calculatorI.calculateTax2(ChannelTransfersVO.getTax2Type(), ChannelTransfersVO.getTax2Rate(), ChannelTransfersVO.getTax1Value());
                ChannelTransfersVO.setTax2Value(value);
                value = 0;
                // set commision value as 0 , becoz commision not calculated on
                // the commision in case pf withdraw and return.
                POSITIVE_COMM_APPLY = DBHandler.AccessHandler.getSystemPreference("POSITIVE_COMM_APPLY");
                if (POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !(_masterVO.getProperty("O2CTransferCode").equalsIgnoreCase(p_txnType) || _masterVO.getProperty("C2CTransferCode").equalsIgnoreCase(p_txnType))) {
                	ChannelTransfersVO.setCommValue(0);
                }
                // In case of FOC Tax3 will not be calculated
                if (!p_txnType.equals(_masterVO.getProperty("FOCCode"))) {
                    value = calculatorI.calculateTax3(ChannelTransfersVO.getTax3Type(), ChannelTransfersVO.getTax3Rate(), ChannelTransfersVO.getCommValue());
                }
                ChannelTransfersVO.setTax3Value(value);
                value = 0;
            }
            value = calculatorI.calculateCommissionQuantity(ChannelTransfersVO.getCommValue(), ChannelTransfersVO.getUnitValue(), ChannelTransfersVO
                .getTax3Value());
            ChannelTransfersVO.setCommQuantity(value);
            long payableAmount = 0;
            long netPayableAmount = 0;
            // In case of FOC payableAmount will not be calculated
            /*
             * if(!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC))
             * {
             * payableAmount =
             * calculatorI.calculatePayableAmount(channelTransferItemsVO
             * .getUnitValue
             * (),Double.parseDouble(channelTransferItemsVO.getRequestedQuantity
             * ()),channelTransferItemsVO.getCommValue(),channelTransferItemsVO.
             * getDiscountValue());
             * netPayableAmount =
             * calculatorI.calculateNetPayableAmount(payableAmount
             * ,channelTransferItemsVO.getTax3Value());
             * }
             */
            if (!p_txnType.equals(_masterVO.getProperty("FOCCode")) && POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !(_masterVO.getProperty("O2CTransferCode")
                .equalsIgnoreCase(p_txnType) || _masterVO.getProperty("C2CTransferCode").equalsIgnoreCase(p_txnType))) {
            	System.out.println("Section 1 Entered");
            	ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
            	ChannelTransfersVO.setReceiverCreditQty(ChannelTransfersVO.getRequiredQuantity());
            	ChannelTransfersVO.setProductTotalMRP(productCost);
            	ChannelTransfersVO.setPayableAmount(productCost);
            	ChannelTransfersVO.setNetPayableAmount(productCost);
            }// this is executed in case of Transfer and POSITIVE_COMM_APPLY is
             // true
            else if (POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !p_txnType.equals(_masterVO.getProperty("FOCCode")) && (_masterVO.getProperty("O2CTransferCode")
                .equalsIgnoreCase(p_txnType) || _masterVO.getProperty("C2CTransferCode").equalsIgnoreCase(p_txnType))) {
            	System.out.println("Section 2 Entered");
            	value = 0;
                value = calculatorI.calculateReceiverCreditQuantity(ChannelTransfersVO.getRequestedQuantity(), ChannelTransfersVO.getUnitValue(),
                		ChannelTransfersVO.getCommQuantity());
                ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
                ChannelTransfersVO.setReceiverCreditQty(value);
                ChannelTransfersVO.setPayableAmount(productCost);
                ChannelTransfersVO.setNetPayableAmount(productCost);
                ChannelTransfersVO
                    .setProductTotalMRP(ChannelTransfersVO.getReceiverCreditQty() * ChannelTransfersVO.getUnitValue() / (SystemPreferences.MULTIPLICATIONFACTOR));
            }// executed in case of FOC or in case of POSITIVE_COMM_APPLY value
             // is false
            else if (!POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !p_txnType.equals(_masterVO.getProperty("FOCCode"))) {
            	System.out.println("Section 3 Entered");
            	payableAmount = calculatorI.calculatePayableAmount(ChannelTransfersVO.getUnitValue(), Double.parseDouble(ChannelTransfersVO.getRequestedQuantity()),
                		ChannelTransfersVO.getCommValue(), ChannelTransfersVO.getDiscountValue());
                netPayableAmount = calculatorI.calculateNetPayableAmount(payableAmount, ChannelTransfersVO.getTax3Value());
                ChannelTransfersVO.setPayableAmount(payableAmount);
                ChannelTransfersVO.setNetPayableAmount(netPayableAmount);
                ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
                ChannelTransfersVO.setReceiverCreditQty(ChannelTransfersVO.getRequiredQuantity());
                ChannelTransfersVO.setProductTotalMRP(productCost);
            } else {
            	System.out.println("Section Else Entered");
            	ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
            	ChannelTransfersVO.setReceiverCreditQty(ChannelTransfersVO.getRequiredQuantity());
            	ChannelTransfersVO.setProductTotalMRP(productCost);
            	ChannelTransfersVO.setPayableAmount(payableAmount);
            	ChannelTransfersVO.setNetPayableAmount(netPayableAmount);
            }
            
            calcMap.put("SENDER_DEBIT_QTY", ""+ChannelTransfersVO.getSenderDebitQty());
            calcMap.put("RECEIVER_CREDIT_QTY", ""+ChannelTransfersVO.getReceiverCreditQty());
            calcMap.put("PRODUCT_TOTAL_MRP", ""+ChannelTransfersVO.getProductTotalMRP());
            calcMap.put("PAYABLE_AMOUNT", ""+ChannelTransfersVO.getPayableAmount());
            calcMap.put("NET_PAYABLE_AMOUNT", ""+ChannelTransfersVO.getNetPayableAmount());
            return calcMap;
            //channelTransferItemsVO.setApprovedQuantity(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
    }
    
    /**
     * Method calculateMRPWithTaxAndDiscount()
     * To calculate the taxes on the selected product list
     * 
     * @param p_transferItemsList
     * @param p_txnType
     *            String
     * @throws SQLException 
     * @throws ParseException 
     * @throws Exception
     */
    public static HashMap<String, CommissionVO> calculateAmountOTF(String ReceiverMSISDN, String p_txnType, HashMap<String, String> requestedQuantities, String gatewayType) throws ParseException, SQLException {
    	final String methodname = "calculateAmountOTF";
    	Log.info("Entered " + methodname + "(" + ReceiverMSISDN + ", " + p_txnType + ", " + Arrays.asList(requestedQuantities) + ")");
    	HashMap<String, CommissionVO> calcMap = new HashMap<String, CommissionVO>();
    	String TARGET_BASED_BASE_COMMISSION = DBHandler.AccessHandler.getNetworkPreference(_masterVO.getMasterValue(MasterI.NETWORK_CODE), "TARGET_BASED_BASE_COMMISSION");
    	boolean addnl = false;
    	Boolean order= true;
    	ChannelTransfersVO ChannelTransfersVO = new ChannelTransfersVO();
    	UserOTFCountsVO UserOTFCountsVO = new UserOTFCountsVO();
    	CommissionProfileDetailsVO commissionProfileDetailsVO;
    	CommissionProfileDetailsVO otfDetailsVO = null;
    	List<CommissionProfileDetailsVO> otfSlabList;
    	long otherCommissionValue = 0l;
    	boolean otherCommProfileGateway = false, otherCommApplicable = false;
    	
		DateFormat df = new SimpleDateFormat("dd/MM/yy");
		Date dateobj = new Date();
		Date tempDate = df.parse(df.format(dateobj));
    
        boolean dualWalletAllowedGateway = false;	//if dual Wallet allowed gateway
		Log.info("Gateway Type: " + gatewayType);
		if(SystemPreferences.DW_ALLOWED_GATEWAYS.contains(gatewayType)) {
			dualWalletAllowedGateway = true;
		}

		for (Map.Entry<String, String> initiateList : requestedQuantities.entrySet()) {
		CommissionVO CommissionVO = new CommissionVO();
        ChannelTransfersVO.loadChannelTransfersDAO(ReceiverMSISDN, initiateList.getKey(), initiateList.getValue());
    	productCost = Long.parseLong(ChannelTransfersVO.getRequestedQuantity());
		if (!p_txnType.equals(_masterVO.getProperty("FOCCode")) /*&& ChannelTransfersVO.isOtfFlag()*/) { // Needs to be fixed
				if(TARGET_BASED_BASE_COMMISSION != null && TARGET_BASED_BASE_COMMISSION.equalsIgnoreCase("true"))
				{
					commissionProfileDetailsVO = DBHandler.AccessHandler.loadCommissionProfileDetailsForOTF(ChannelTransfersVO.getCommProfileDetailID());
					UserOTFCountsVO = DBHandler.AccessHandler.loadUserOTFCounts(ReceiverMSISDN, commissionProfileDetailsVO.getBaseCommProfileDetailID(), addnl);
					boolean timeFlag=true;

					if(!(commissionProfileDetailsVO.getOtfApplicableFrom()== null && commissionProfileDetailsVO.getOtfApplicableTo()==null))
					{
						if(!((commissionProfileDetailsVO.getOtfApplicableFrom().before(tempDate) 
								|| commissionProfileDetailsVO.getOtfApplicableFrom().equals(tempDate)) && (commissionProfileDetailsVO.getOtfApplicableTo().after(tempDate) 
										|| commissionProfileDetailsVO.getOtfApplicableTo().equals(tempDate))))
						{
							timeFlag = false;
						}
					}

					if(commissionProfileDetailsVO.getOtfTimeSlab()!=null)
					{
						if(!BTSLUtil.timeRangeValidation(commissionProfileDetailsVO.getOtfTimeSlab(), new Date()))
						{
							timeFlag=false;
						}
					}
					if(timeFlag)
					{
						otfSlabList= DBHandler.AccessHandler.getBaseCommOtfDetails(commissionProfileDetailsVO.getBaseCommProfileDetailID(), order);

						for (CommissionProfileDetailsVO commissionProfileOTFSlabVO : otfSlabList)
						{

							if(UserOTFCountsVO!=null)
							{
								if (UserOTFCountsVO.getOtfValue() >= commissionProfileOTFSlabVO.getOtfValue())
								{
									otfDetailsVO =  commissionProfileOTFSlabVO;
								}
							}
							else
							{
								if (productCost >= commissionProfileOTFSlabVO.getOtfValue())
								{
									otfDetailsVO = commissionProfileOTFSlabVO;
								}
							}
						}


						if(otfDetailsVO!=null)
						{

							commissionProfileDetailsVO.setBaseCommProfileOTFDetailID(otfDetailsVO.getBaseCommProfileOTFDetailID());
							commissionProfileDetailsVO.setBaseCommProfileDetailID(otfDetailsVO.getBaseCommProfileDetailID());
							commissionProfileDetailsVO.setOtfValue(otfDetailsVO.getOtfValue());
							commissionProfileDetailsVO.setOtfTypePctOrAMt(otfDetailsVO.getOtfTypePctOrAMt());
							commissionProfileDetailsVO.setOtfRate(otfDetailsVO.getOtfRate());
							calculateOTFforOPT(ChannelTransfersVO, commissionProfileDetailsVO);
							otfDetailsVO = null;
						}

						/*if(ChannelTransfersVO.getOtfAmount()!= 0)
						{
							channelUserVO.setCommissionProfileSetID(channelTransferVO.getCommProfileSetId());
							channelUserVO.setUserID(channelTransferVO.getToUserID());
							channelUserVO.setNetworkID(channelTransferVO.getNetworkCode());
							channelUserVO.setCommissionProfileSetVersion(channelTransferVO.getCommProfileVersion());
							ChannelTransferSVO.setCommProfileDetailID(commissionProfileDeatilsVO.getBaseCommProfileDetailID());
						}*/

					}
				}
			}
    	
            productCost = (long) (Double.parseDouble(ChannelTransfersVO.getRequestedQuantity()) * ChannelTransfersVO.getUnitValue());
        	
            otherCommApplicable = false;
            if(SystemPreferences.OTH_COM_ENABLED) {
        		//added for Other commission
        		value = 0;
        		//to check whether the other commission profile is gateway type
        		otherCommProfileGateway = PretupsI.OTHER_COMM_PROFILE_GATEWAY.equals(ChannelTransfersVO.get_otherCommissionProfileType()) ? true : false ;
        		
        		if(otherCommProfileGateway) {
        			if(dualWalletAllowedGateway) {
        				//check whether gateway is allowed and also matches the defined other commission profile type
        				otherCommApplicable = true;
        			}
        		}else {
        			otherCommApplicable = true;
        		}
        		
                if(otherCommApplicable) {
        			value = calculatorI.calculateOtherCommission(ChannelTransfersVO.getOtherCommisssionType(), 
        						ChannelTransfersVO.getOtherCommissionRate() , productCost);
        		}
                otherCommissionValue = value;
                ChannelTransfersVO.setOtherCommissionValue((double)value);
                
        	}
            value = 0;
         
            // In case of FOC Commission will not be calculated
            if (!p_txnType.equals(_masterVO.getProperty("FOCCode"))) {
                value = calculatorI.calculateCommission(ChannelTransfersVO.getCommType(), ChannelTransfersVO.getCommRate(), productCost);
            }        
            
            String OthBaseComm = DBHandler.AccessHandler.getSystemPreference(PretupsI.DW_COMMISSION_CAL);
            
            Log.info("Is other commission applicable :"  + otherCommApplicable + ", other commission value: " + Long.toString(otherCommissionValue));
            if(SystemPreferences.OTH_COM_ENABLED) {
                if(otherCommApplicable) {
                    //only calculate other commission value
                    if(PretupsI.DW_COMMISSION_CAL_OTH_COMMISSION.equals(OthBaseComm)) {
                        value = otherCommissionValue;      	
                    }//calculate base commission + other commission value
                    else if(PretupsI.DW_COMMISSION_CAL_BASE_OTH_COMMISSION.equals(OthBaseComm)){
                        value += otherCommissionValue;
                    }
                }
            }
            
            ChannelTransfersVO.setCommValue(value);
            value = 0;
			value = calculatorI.calculateDiscount(ChannelTransfersVO.getDiscountType(), ChannelTransfersVO.getDiscountRate(), productCost);
			ChannelTransfersVO.setDiscountValue(value);
			value = 0;
				
            /*
             * To check whether tax is applicable for transaction or not.We have
             * the following three type of the
             * transaction and following corresponding check condition.
             * 1. In case of O2C transfer Tax calculation is mandatory.
             * 2. In case of C2C transfer Tax calculation is based on the
             * TAX_APPLICABLE_ON_C2C flag of the
             * commission profile.
             * 3. In case of FOC transfer Tax calculation is based on the
             * TAX_APPLICABLE_ON_FOC flag of the
             * commission profile.
             */
	    	
            if (p_txnType.equals(_masterVO.getProperty("O2CTransferCode")) || p_txnType.equals(_masterVO.getProperty("O2CWithdrawCode")) || p_txnType.equals(_masterVO.getProperty("O2CReturnCode")) || ( (p_txnType.equals(_masterVO.getProperty("C2CTransferCode")) || p_txnType.equals(_masterVO.getProperty("C2CWithdrawCode")) || p_txnType.equals(_masterVO.getProperty("C2CReturnCode"))) && PretupsI.YES.equals(ChannelTransfersVO
                .getTaxOnChannelTransfer())) || (p_txnType.equals(_masterVO.getProperty("FOCCode")) && PretupsI.YES.equals(ChannelTransfersVO.getTaxOnFOCTransfer()))) {
                value = calculatorI.calculateTax1(ChannelTransfersVO.getTax1Type(), ChannelTransfersVO.getTax1Rate(), productCost);
                ChannelTransfersVO.setTax1Value(value);
                value = 0;
                value = calculatorI.calculateTax2(ChannelTransfersVO.getTax2Type(), ChannelTransfersVO.getTax2Rate(), ChannelTransfersVO.getTax1Value());
                ChannelTransfersVO.setTax2Value(value);
                value = 0;
                // set commision value as 0 , becoz commision not calculated on
                // the commision in case pf withdraw and return.

				if (_masterVO.getClientDetail("DUAL_COMMISSION_FieldType").equalsIgnoreCase("0")) {
					POSITIVE_COMM_APPLY = DBHandler.AccessHandler.getSystemPreference("POSITIVE_COMM_APPLY");
				} else {
					if (DBHandler.AccessHandler.getApplicableDualCommissioningType(ReceiverMSISDN).equalsIgnoreCase(PretupsI.Postive_Commission))
						POSITIVE_COMM_APPLY = "true";
					else
						POSITIVE_COMM_APPLY = "false";
				}

                if (POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !(_masterVO.getProperty("O2CTransferCode").equalsIgnoreCase(p_txnType) || _masterVO.getProperty("C2CTransferCode").equalsIgnoreCase(p_txnType))) {
                	ChannelTransfersVO.setCommValue(0);
                }
                // In case of FOC Tax3 will not be calculated
                if (!p_txnType.equals(_masterVO.getProperty("FOCCode"))) {
                    value = calculatorI.calculateTax3(ChannelTransfersVO.getTax3Type(), ChannelTransfersVO.getTax3Rate(), ChannelTransfersVO.getCommValue());
                }
                ChannelTransfersVO.setTax3Value(value);
                value = 0;
            }
            value = calculatorI.calculateCommissionQuantity(ChannelTransfersVO.getCommValue() /*+ otherCommissionValue*/, ChannelTransfersVO.getUnitValue(), ChannelTransfersVO
                .getTax3Value());
            
            if (!p_txnType.equals(_masterVO.getProperty("FOCCode")))
			{
				if(TARGET_BASED_BASE_COMMISSION != null && TARGET_BASED_BASE_COMMISSION.equalsIgnoreCase("true"))
				{ 
					value = value + ChannelTransfersVO.getOtfAmount();
				}
			}
            
            ChannelTransfersVO.setCommQuantity(value);
            long payableAmount = 0;
            long netPayableAmount = 0;
            // In case of FOC payableAmount will not be calculated
            /*
             * if(!p_txnType.equals(PretupsI.TRANSFER_TYPE_FOC))
             * {
             * payableAmount =
             * calculatorI.calculatePayableAmount(channelTransferItemsVO
             * .getUnitValue
             * (),Double.parseDouble(channelTransferItemsVO.getRequestedQuantity
             * ()),channelTransferItemsVO.getCommValue(),channelTransferItemsVO.
             * getDiscountValue());
             * netPayableAmount =
             * calculatorI.calculateNetPayableAmount(payableAmount
             * ,channelTransferItemsVO.getTax3Value());
             * }
             */
            if (!p_txnType.equals(_masterVO.getProperty("FOCCode")) && POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !(_masterVO.getProperty("O2CTransferCode")
                .equalsIgnoreCase(p_txnType) || _masterVO.getProperty("C2CTransferCode").equalsIgnoreCase(p_txnType))) {
            	ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
            	ChannelTransfersVO.setReceiverCreditQty(ChannelTransfersVO.getRequiredQuantity());
            	ChannelTransfersVO.setProductTotalMRP(productCost);
            	ChannelTransfersVO.setPayableAmount(productCost);
            	ChannelTransfersVO.setNetPayableAmount(productCost);
            }// this is executed in case of Transfer and POSITIVE_COMM_APPLY is
             // true
            else if (POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !p_txnType.equals(_masterVO.getProperty("FOCCode")) && (_masterVO.getProperty("O2CTransferCode")
                .equalsIgnoreCase(p_txnType) || _masterVO.getProperty("C2CTransferCode").equalsIgnoreCase(p_txnType))) {
            	value = 0;
                value = calculatorI.calculateReceiverCreditQuantity(ChannelTransfersVO.getRequestedQuantity(), ChannelTransfersVO.getUnitValue(),
                		ChannelTransfersVO.getCommQuantity());
                ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
                ChannelTransfersVO.setReceiverCreditQty(value);
                ChannelTransfersVO.setPayableAmount(productCost);
                ChannelTransfersVO.setNetPayableAmount(productCost);
                ChannelTransfersVO
                    .setProductTotalMRP(ChannelTransfersVO.getReceiverCreditQty() * ChannelTransfersVO.getUnitValue() / (SystemPreferences.MULTIPLICATIONFACTOR));
            }// executed in case of FOC or in case of POSITIVE_COMM_APPLY value
             // is false
            else if (!POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !p_txnType.equals(_masterVO.getProperty("FOCCode"))) {
            	
            	if(TARGET_BASED_BASE_COMMISSION != null && TARGET_BASED_BASE_COMMISSION.equalsIgnoreCase("true")) { 
					payableAmount = calculatorI.calculatePayableAmount(ChannelTransfersVO.getUnitValue(), Double.parseDouble(ChannelTransfersVO.getRequestedQuantity()),
							ChannelTransfersVO.getCommValue() + ChannelTransfersVO.getOtfAmount() /*+ otherCommissionValue*/, ChannelTransfersVO.getDiscountValue());
				} else {
					payableAmount = calculatorI.calculatePayableAmount(ChannelTransfersVO.getUnitValue(), Double.parseDouble(ChannelTransfersVO.getRequestedQuantity()),
							ChannelTransfersVO.getCommValue() /*+ otherCommissionValue*/, ChannelTransfersVO.getDiscountValue());
				}
            	
                netPayableAmount = calculatorI.calculateNetPayableAmount(payableAmount, ChannelTransfersVO.getTax3Value());
                ChannelTransfersVO.setPayableAmount(payableAmount);
                ChannelTransfersVO.setNetPayableAmount(netPayableAmount);
                ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
                ChannelTransfersVO.setReceiverCreditQty(ChannelTransfersVO.getRequiredQuantity());
                ChannelTransfersVO.setProductTotalMRP(productCost);
            } else {
            	ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
            	ChannelTransfersVO.setReceiverCreditQty(ChannelTransfersVO.getRequiredQuantity());
            	ChannelTransfersVO.setProductTotalMRP(productCost);
            	ChannelTransfersVO.setPayableAmount(payableAmount);
            	ChannelTransfersVO.setNetPayableAmount(netPayableAmount);
            }
            
            CommissionVO.setSenderDebitQty(ChannelTransfersVO.getSenderDebitQty());
            CommissionVO.setReceiverCreditQty(ChannelTransfersVO.getReceiverCreditQty());
            CommissionVO.setProductTotalMRP(ChannelTransfersVO.getProductTotalMRP());
            CommissionVO.setPayableAmount(ChannelTransfersVO.getPayableAmount());
            CommissionVO.setNetPayableAmount(ChannelTransfersVO.getNetPayableAmount());
            CommissionVO.setCommissionQty(ChannelTransfersVO.getCommQuantity());
            
            calcMap.put(initiateList.getKey(), CommissionVO);      
		}
           
            Log.info(methodname + " Returns : " + Arrays.asList(calcMap));
            Log.info("Exiting " + methodname + "()");
            return calcMap;
            //channelTransferItemsVO.setApprovedQuantity(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
    }    
	
	/**
	 * Method to calculate OTF for OPT and controls the flow of process
	 * 
	 * @param con
	 * @param channelTransferItemsVO
	 * @param additionalProfileDetailsVO
	 * @throws BTSLBaseException
	 */
	private static void calculateOTFforOPT(ChannelTransfersVO ChannelTransfersVO, CommissionProfileDetailsVO commissionProfileDetailsVO) {
		final String methodname = "calculateOTFforOPT";
		Log.debug("Entered " + methodname + "(" + ChannelTransfersVO + ", " + commissionProfileDetailsVO + ")");
		try {

			long otfAmount;

			otfAmount = calculatorI.calculateOTFComm(commissionProfileDetailsVO.getOtfTypePctOrAMt(), commissionProfileDetailsVO.getOtfRate(), ChannelTransfersVO.getRequiredQuantity());
			
			if (PretupsI.AMOUNT_TYPE_AMOUNT.equalsIgnoreCase(commissionProfileDetailsVO.getOtfTypePctOrAMt())) {
				ChannelTransfersVO.setOtfTypePctOrAMt(PretupsI.AMOUNT_TYPE_AMOUNT);
			}
			else if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(commissionProfileDetailsVO.getOtfTypePctOrAMt()))
			{
				ChannelTransfersVO.setOtfTypePctOrAMt(PretupsI.AMOUNT_TYPE_PERCENTAGE);
			}
			ChannelTransfersVO.setOtfAmount(otfAmount);
			ChannelTransfersVO.setOtfRate(commissionProfileDetailsVO.getOtfRate());
		}
		catch(Exception e)
		{
			Log.writeStackTrace(e);
		}
		Log.debug("Exited " + methodname + "()");
	}
	
}