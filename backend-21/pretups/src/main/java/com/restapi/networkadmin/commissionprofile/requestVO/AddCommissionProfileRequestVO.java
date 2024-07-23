package com.restapi.networkadmin.commissionprofile.requestVO;


import java.util.ArrayList;
import java.util.Date;

import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileCombinedVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileProductsVO;
import com.btsl.pretups.channel.profile.businesslogic.OtfProfileCombinedVO;
import com.restapi.networkadmin.commissionprofile.requestVO.AdditionalProfileCombinedVONew;
import com.restapi.networkadmin.commissionprofile.requestVO.CommissionProfileCombinedVONew;
import com.restapi.networkadmin.commissionprofile.requestVO.OtfProfileCombinedVONew;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AddCommissionProfileRequestVO {

//for commissionProfileSetVO -> general details for add commission profile
	// private String commProfileSetId;   ->randomly generated
	private String profileName;
	private String categoryCode;
	private String networkCode;
	private String version = "1";
	
	private String shortCode;
	//  private String status;
	private String grphDomainCode;
	private String gradeCode;
	private String dualCommType;  //NC for normal commissioning and PC for positive commissioning
	
	
	
//commissionProfileSetVersionVO -> details for setting version
	// private String commProfileSetId;
	//private String commProfileSetVersion="1";
	private String applicableFromHour = null; //setApplicableFrom will require this
	private String applicableFromDate = null; //setApplicableFrom will require this
	
	//private String dualCommissionType;  //NC for normal commissioning and PC for positive commissioning, already coming from above division
	private String otherCommissionProfile;
	
	
	
//details for assigning commission slabs
	private ArrayList<CommissionProfileCombinedVONew> commissionProfileList;
	//private String commProfileProductID;       //randomly generated
	// private String commProfileSetId;   ->randomly generated  //already generated and saved
	//private String version = "1";   alraedy used
	//private long transferMultipleOff;  //get from commissionProfileProductsVO
	//for slabs
	//private String commProfileProductID;       //randomly generated -> already generated
	//private String commProfileDetailID;      //randomly generated
	
	
	
//details for assigning CBC slabs
	private ArrayList<OtfProfileCombinedVONew> otfProfileList;
	
	
//details for assigning additional commission slabs
	private ArrayList<AdditionalProfileCombinedVONew> additionalProfileList;
	
}
