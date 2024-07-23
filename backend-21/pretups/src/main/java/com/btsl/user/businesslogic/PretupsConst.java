/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package com.btsl.user.businesslogic;

import lombok.Getter;

/**
 * This Enum constants used for System.
 * 
 * @author SubeshKCV
 */

/**
 * Gets the int value.
 *
 * @return the int value
 */
@Getter
public enum PretupsConst {
    
    LOCALE_PERSIAN("fa_IR@calendar=persian") ,
    CALENDAR_TYPE("CALENDAR_TYPE"),
    PERSIAN("PERSIAN"),
    VOMS_BATCHES_DOC_TYPE("VMBTCHUD"),
    BATCH_INTIATED("IN"),
    BATCH_ACCEPTED("AC"),
     BATCH_PROCESS_INITIATE("INITIATE"),
    VOUCHER_NEW("GE"),
    SYSTEM("SYSTEM"),
    EXECUTED("EX"),
    BATCH_GENERATED("GE"),
    LOCALE_LANGAUGE_EN("en"),
	 BATCH_APP1("A1"),
    BATCH_APP2("A2"),
    VOUCHER_TYPE_TEST_DIGITAL("DT"),
    VOUCHER_TYPE_TEST_PHYSICAL("PT"),
    VOUCHER_TYPE_TEST_ELECTRONIC("ET"),
    BATCH_REJECTED("RE"),
    BATCH_APP1_ALREARDYAPPROVED("A1ALREADYDONE"),
    BATCH_APP2_ALREARDYAPPROVED("A2ALREADYDONE"),
    INVALID_APPROVAL_LEVEL("InvalidApprvLevel"),
    BATCH_EXECUTED("EX"),
    BATCH_ALREARDYEXECUTED("BATCHALRDYEX"),
    BATCH_ALREARDYREJECTED("BATCHALRDYRE"),
    BATCHSIZE_EXCEED("BATCHSIZEEXCEED"),
    
    DOT("."),
    FIELDNAME("fieldName"),
    //Module Generate voucher
    GENVOUCHER("GENVOUCHER"),
    GENVOUCHER_ROWNO("rowNo"),  // field attribute
    GENVOUCHER_SEGMENT("segment"),
    GENVOUCHER_DENOMINATIONID("denominationID"),
    GENVOUCHER_PROFILEID("profileID"),
    GENVOUCHER_QUANTITY("quantity"),
    GENVOUCHER_REMARK("remark"),
    GENVOUCHER_NETWORKCODE("networkCode"),
    
    //validation rules for voucher Appproval service   
    VOUCHERAPPRV("VOUCHERAPPROVE"),
    VOUCHERAPPROVE_BATCHNO("batchNo"),  // field attribute
    VOUCHERAPPROVE_QUANTITY("quantity"),
    VOUCHERAPPROVE_REMARKS("remarks"),
    VOUCHERAPPROVE_NETWORKCODE("networkCode"),
    VOUCHERAPPROVE_APPRLEVEL("approveLevel"),
   
  //validation rules for voucher Enquiry service   
    VOUCHERENQUIRY("VOUCHERENQUIRY"),
    VOUCHERENQUIRY_SERIALNO("serialNo"),  // field attribute
    VOUCHERENQUIRY_VOUCHERTYPE("voucherType"),
    VOUCHERENQUIRY_NETWORKCODE("networkCode"),
    	
   //Addprofile 
    ADDPROFILE("ADDPROFILE"),
    ADDPROFILE_VOUCHERTYPE("voucherType"),  // field attribute
    ADDPROFILE_TALKTIME("talkTime"),
    ADDPROFILE_CATEGORYID("categoryId"),
    ADDPROFILE_AUTOGENERATE("autoGenerate"),
    ADDPROFILE_VOUCHERSEGMENT("voucherSegment"),
    ADDPROFILE_TYPE("type"),
    ADDPROFILE_SERVICEID("serviceId"),
    ADDPROFILE_MRP("mrp"),
    ADDPROFILE_PROFILENAME("profileName"),
    ADDPROFILE_PROFILESHORTNAME("profileShortName"),
    ADDPROFILE_MINREORDERQNTY("minReOrderQty"),
    ADDPROFILE_VOUCHERGENQNTY("voucherGenerateQuantity"),
    ADDPROFILE_MAXREORDERQNTY("maxReOrderQty"),
    ADDPROFILE_EXPIREDPERISTRFLG("expiryPeriodStrFlag"),
    ADDPROFILE_VALIDITIY("validity"),
    ADDPROFILE_EXPDATESTRNGFLAG("expiryDateStringFlag"),
    ADDPROFILE_VOUCHERTHRESHOLD("voucherThreshold"),
    ADDPROFILE_DESCRIPTION("description"),
    ADDPROFILE_EXPIRYPERIODSTR("expiryPeriodStr"),
    ADDPROFILE_SECONDRYPREFIXCOD("secondaryPrefixCode"),
    ADDPROFILE_EXPIRYDATESTRING("expiryDateString"),
    ADDPROFILE_ITEMCODE("itemCode"),
    ADDPROFILE_NETWORKCODE("networkCode"),
    
    //Modify profile
    MODIFYPROFILE("MODIFYPROFILE"),
    MODIFYPROFILE_PROFILEID("profileId"),
    MODIFYPROFILE_VOUCHERTYPE("voucherType"),
    MODIFYPROFILE_TALKTIME("talkTime"),
    MODIFYPROFILE_VALIDITIY("validity"),
    MODIFYPROFILE_AUTOGENERATE("autoGenerate"),
    MODIFYPROFILE_VOUCHERTHRESHOLD("voucherThreshold"),
    MODIFYPROFILE_VOUCHERGENQNTY("voucherGenerateQuantity"),
    MODIFYPROFILE_EXPIREDPERISTRFLG("expiryPeriodStrFlag"),
    MODIFYPROFILE_EXPIRYPERIODSTR("expiryPeriodStr"),
    MODIFYPROFILE_EXPDATESTRNGFLAG("expiryDateStringFlag"),
    MODIFYPROFILE_EXPIRYDATESTRING("expiryDateString"),
    MODIFYPROFILE_DESCRIPTION("description"),
    MODIFYPROFILE_ITEMCODE("itemCode"),
    MODIFYPROFILE_SECONDRYPREFIXCOD("secondaryPrefixCode"),
    MODIFYPROFILE_MINREORDERQNTY("minReOrderQty"),
    MODIFYPROFILE_MAXREORDERQNTY("maxReOrderQty"),    
    MODIFYPROFILE_STATUS("status"),
    
    
    
    
    
    ADDDENOMINATION("ADDDENOMINATION"),    
    ADDDENOMINATION_NAME("denominationName"),    
    ADDDENOMINATION_SHORTNAME("shortName"),    
    ADDDENOMINATION_PAYABLEAMOUNT("payableAmount"),    
    ADDDENOMINATION_DENOMINATION("denomination"),    
    ADDDENOMINATION_DESCRIPTION("description"),    
    ADDDENOMINATION_VOUCHERTYPE("voucherType"),    
    ADDDENOMINATION_VOUCHERSEGMENT("voucherSegment"),   
    ADDDENOMINATION_TYPE("type"),   
    ADDDENOMINATION_SERVICEID("serviceId"),
    ADDDENOMINATION_NETWORKCODE("networkCode"),
    
    MODIFYDENOMINATION("MODIFYDENOMINATION"),  
    MODIFYDENOMINATION_ID("denominationID"),
    MODIFYDENOMINATION_NAME("denominationName"), 
    MODIFYDENOMINATION_SHORTNAME("shortName"),    
    MODIFYDENOMINATION_DESCRIPTION("description"),
    MODIFYDENOMINATION_DENOMINATION("denomination"), 
    MODIFYDENOMINATION_PAYABLEAMOUNT("payableAmount"), 
    
    
    BL_EVD_FILE_ENCRY_KEY("BL_EVD_FILE_ENCRY_KEY"),
    
    
    

    ;

    /** The str value. */
    private String strValue;

    /** The int value. */
    private int intValue;

    /**
     * Construct String Constant.
     *
     * @param strValue
     *            - strValue
     */
    PretupsConst(String strValue) {
        this.strValue = strValue;
    }

	public String getStrValue() {
		return strValue;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
    
   /* PretupsConst(int intValue) {
        this.intValue = intValue;
    }*/
    
    
    
    
}
